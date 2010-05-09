import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.rsbot.accessors.Node;
import org.rsbot.accessors.RSNPCNode;
import org.rsbot.bot.Bot;
import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSItemTile;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = { "ByteCode" }, category = "Combat", name = "AlkharidFighter", version = 7.5,
		description = "<html>" + "<body>" + "Click OK to start the GUI." + "</body>" + "</html>")
@SuppressWarnings( { "unused", "unchecked", "serial" })
public class AlkharidFighter extends Script implements PaintListener,
		ServerMessageListener {


	private final ScriptManifest props = getClass().getAnnotation(
			ScriptManifest.class);
	private int kills = 0, foodID, foodCount, ArrowID, ArrowCount, mouseSpeed,
			Obj1 = 35534;
	public int lootArray[];
	private int[] mainArea = { 3280, 3305, 3156, 3183 };
	private int[] bankArea = { 3269, 3269, 3166, 3175 };
	public URLConnection url = null;
	public BufferedReader in = null;
	public BufferedWriter out = null;
	private String status = "Starting up.";
	public String ArrowName;
	public boolean guiWait = true, useFood, pickArrows, buryBones, lootItems,
			changeSpeed, antiBan;
	final NumberFormat nf = NumberFormat.getInstance();
	private long startTime, runTime;
	private int startXP[] = new int[8], currXP[] = new int[8],
			xpGain[] = new int[8];
	private String statNames[] = { "Ranged", "Attack", "Strength", "Defense",
			"Magic", "Hitpoints", "Prayer" };
	private String itemNames[] = { "Bones", "Bronze arrow", "Bronze bolts",
			"Bronze med helm", "Cabbage", "Chaos rune", "Coins", "Copper ore",
			"Earth rune", "Earth talisman", "Fire rune", "Fishing bait",
			"Grimy avantoe", "Grimy dwarf weed", "Grimy guam",
			"Grimy harralander", "Grimy irit", "Grimy kwuarm",
			"Grimy lantadyme", "Grimy marrentill", "Grimy ranarr",
			"Grimy tarromin", "Iron dagger", "Iron med helm", "Mind rune" };

	private RSTile[] pathToBank = { new RSTile(3293, 3181),
			new RSTile(3282, 3182), new RSTile(3275, 3174),
			new RSTile(3275, 3168), new RSTile(3270, 3167) };
	private RSTile mTile = new RSTile(3292, 3175);

	XGui gui = new XGui();

	XAntiBan antiban;
	Thread t;

	public int loop() {
		if (changeSpeed) {
			getMouseSpeed();
		}
		failSafe();
		setRun();
		bank();
		if (getMyPlayer().isMoving()) {
			return random(50, 120);
		}
		if (useFood) {
			useFood();
		}
		RSNPC Target = getNearestNPCByName("Al-Kharid warrior");
		if (!getMyPlayer().isInCombat()
				&& getMyPlayer().getInteracting() == null) {
			lootItems();
			equipArrows();
			if (buryBones) {
				BuryBones();
			}
			if (Target != null) {
				if (!Target.isOnScreen()) {
					walkTo(Target.getLocation());
					status = "Walking";
					return random(50, 120);
				}
				clickNPC(Target, "Attack");
				status = "Attacking";
				return random(1000, 2000);
			}
		}

		return random(50, 120);
	}

	public boolean atWarriors() {
		return playerIsInArea(mainArea);
	}

	private void equipArrows() {
		if (pickArrows) {
			if (getInventoryCount(ArrowID) >= ArrowCount) {
				status = "Equipping arrows";
				atInventoryItem(ArrowID, "Wield");
				wait(random(1000, 1500));
			}
		}
	}

	public boolean atBank() {
		return playerIsInArea(bankArea);
	}

	public boolean menuContains(String item) {
		try {
			for (String s : getMenuItems()) {
				if (s.toLowerCase().contains(item.toLowerCase()))
					return true;
			}
		} catch (Exception e) {
			return menuContains(item);
		}
		return false;
	}

	private void bank() {
		if (useFood) {
			if (!inventoryContains(foodID)) {
				log("We are out of food, banking.");
				status = "Banking.";
				while (!getFood()) {
					wait(random(50, 120));
				}
			} else {
				if (!atWarriors()) {
					log("Walking back to alkharid warriors.");
					status = "Walking Back";
					walkToTile(mTile);
				}
			}
		} else if (!buryBones && isInventoryFull()) {
			log("Banking loot.");
			status = "Banking.";
			while (!bankLoot()) {
				wait(random(50, 120));
			}
		} else if (!buryBones && !isInventoryFull()) {
			if (!atWarriors()) {
				log("Walking back to alkharid warriors.");
				status = "Walking Back";
				walkToTile(mTile);
			}
		}
	}

	private void walkToTile(final RSTile tile) {
		walkTo(randomizeTile(tile, 0, 0));
	}

	private void walkPathToBank(RSTile[] path) {
		walkPathMM(randomizePath(path, 1, 1));
	}

	private boolean getFood() {
		if (!atBank()) {
			walkToBank();
		}
		if (bankIsOpen()) {
			if (getInventoryCount() >= 1) {
				bank.depositAll();
			}
			wait(random(1000, 1700));
			log("Withdrawing food ( x" + foodCount + ")");
			if (bank.getCount(foodID) >= foodCount) {
				if (bank.atItem(foodID, "Withdraw-X")) {
					wait(random(1200, 1700));
					Bot.getInputManager().sendKeys(Integer.toString(foodCount),
							true);
					wait(random(1200, 1700));
					bank.close();
					return true;
				}
			} else {
				log("Out of food in bank.");
				stopScript(true);
			}
		}
		return false;
	}

	private boolean bankLoot() {
		if (!atBank()) {
			walkToBank();
		}
		if (bankIsOpen()) {
			bank.depositAll();
			wait(random(1200, 1700));
			bank.close();
			return true;
		}
		return false;
	}

	private boolean bankIsOpen() {
		return bank.isOpen() || bank.open();
	}

	private void walkToBank() {
		if (!atBank()) {
			walkPathToBank(pathToBank);
			wait(random(50, 120));
		}
	}

	boolean clickNPC(final RSNPC n, final String action) {
		try {
			Point p;
			while ((p = n.getScreenLocation()) != null && p.x != -1
					&& getMouseLocation().distance(n.getScreenLocation()) > 8) {
				moveMouse(p, 5, 5);
			}
			if (!pointOnScreen(getMouseLocation())) {
				return false;
			}
			if (menuContains("Attack")) {
				clickMouse(true);
				return true;
			}
		} catch (final Exception ignored) {
		}
		return true;
	}

	public boolean onStart(Map<String, String> args) {
		DGui dgui = new DGui();
		while (guiWait) {
			wait(random(50, 120));
		}
		startTime = System.currentTimeMillis();
		if (antiBan) {
			antiban = new XAntiBan();
			t = new Thread(antiban);
			if (!t.isAlive()) {
				log("Starting AntiBan Thread.");
				t.start();
			}
		}
		startXP[0] = skills.getCurrentSkillExp(Constants.STAT_RANGE); // ranged
		startXP[1] = skills.getCurrentSkillExp(Constants.STAT_ATTACK); // attack
		startXP[2] = skills.getCurrentSkillExp(Constants.STAT_STRENGTH); // strength
		startXP[3] = skills.getCurrentSkillExp(Constants.STAT_DEFENSE); // defense
		startXP[4] = skills.getCurrentSkillExp(Constants.STAT_MAGIC); // magic
		startXP[5] = skills.getCurrentSkillExp(Constants.STAT_HITPOINTS); // hp
		startXP[6] = skills.getCurrentSkillExp(Constants.STAT_PRAYER); // prayer

		log("Thanks for using AlkharidKiller.");
		return !guiWait;
	}

	public void serverMessageReceived(String s) {

	}

	public void onFinish() {
		Bot.getEventManager().removeListener(PaintListener.class, this);
		if (antiBan) {
			if (t.isAlive()) {
				antiban.stopThread = true;
			}
		}
		log("Thanks for using AlkharidFighter.");
	}

	public void paintWrapper(final Graphics g) {

		g.setColor(new Color(51, 0, 51, 255));
		g.drawRect(13, 350, 461, 103);

		g.setColor(new Color(0, 204, 255, 255));
		g.fillRect(14, 352, 225, 99);
		g.fillRect(232, 389, 0, 0);

		g.fillRect(240, 352, -222, 99);

		g.fillRect(246, 352, 225, 99);

		g.setColor(new Color(0, 51, 51, 255));
		g.fillRect(238, 352, 9, 99);

		g.fillRect(90, 300, 310, 35);
		g.fillRect(173, 138, 0, 0);
	}

	public void paintBody(final Graphics g) {
		g.setColor(Color.RED);
		g.setFont(new Font("Impact", Font.BOLD, 25));
		g.drawString("Al-Kharid Fighter " + props.version(), 132, 326);

		int x = 17;
		int y = 350;

		g.setColor(Color.BLACK);
		g.setFont(new Font("Impact", Font.BOLD, 15));
		g.drawString("Status: " + status, x, (y = y + 15));
		g.setFont(new Font("Calibri", Font.PLAIN, 12));
		g.drawString("Runtime: " + formatTime((int) runTime), x,
				(y = y + 15));
		if (xpGain[5] >= 25) {
			kills = xpGain[5] / 25;
		}
		g.drawString("Total kills: " + kills, x, (y = y + 15));
		if (changeSpeed) {
			g.drawString("Mouse speed: " + mouseSpeed, x, (y = y + 15));
		}
		if (buryBones) {
			g.drawString("Bury bones: " + buryBones, x, (y = y + 15));
		}
		if (pickArrows) {
			g.drawString("Pickup arrows and equip: " + pickArrows, x,
					(y = y + 15));
		}
		if (antiBan) {
			g.drawString("Antiban Enabled: " + antiBan, x, y);
		}
	}

	public void paintXP(final Graphics g) {
		g.setColor(Color.BLACK);
		g.setFont(new Font("Calibri", Font.PLAIN, 12));
		int x = 250;
		int y = 347;
		for (int x1 = 0; x1 < xpGain.length; x1++) {
			if (xpGain[x1] > 0) {
				g.drawString(statNames[x1] + " XP: " + xpGain[x1], x,
						(y = y + 15));
			}
		}
	}

	private void drawMouse(final Graphics g) {
		final Point loc = getMouseLocation();
		if (System.currentTimeMillis()
				- Bot.getClient().getMouse().getMousePressTime() < 500) {
			g.setColor(new Color(0, 0, 0, 50));
			g.fillOval(loc.x - 5, loc.y - 5, 10, 10);
		} else {
			g.setColor(Color.BLACK);
		}
		g.drawLine(0, loc.y, 766, loc.y);
		g.drawLine(loc.x, 0, loc.x, 505);
	}

	public void serverMessageRecieved(ServerMessageEvent e) {
		String serverString = e.getMessage();
	}

	public void onRepaint(Graphics g) {
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
				RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		runTime = System.currentTimeMillis() - startTime;
		if (isLoggedIn() && !guiWait) {
			currXP[0] = skills.getCurrentSkillExp(Constants.STAT_RANGE); // ranged
			currXP[1] = skills.getCurrentSkillExp(Constants.STAT_ATTACK); // attack
			currXP[2] = skills.getCurrentSkillExp(Constants.STAT_STRENGTH); // strength
			currXP[3] = skills.getCurrentSkillExp(Constants.STAT_DEFENSE); // defense
			currXP[4] = skills.getCurrentSkillExp(Constants.STAT_MAGIC); // magic
			currXP[5] = skills.getCurrentSkillExp(Constants.STAT_HITPOINTS); // hp
			currXP[6] = skills.getCurrentSkillExp(Constants.STAT_PRAYER); // prayer

			xpGain[0] = currXP[0] - startXP[0]; // ranged
			xpGain[1] = currXP[1] - startXP[1]; // attack
			xpGain[2] = currXP[2] - startXP[2]; // strength
			xpGain[3] = currXP[3] - startXP[3]; // defense
			xpGain[4] = currXP[4] - startXP[4]; // magic
			xpGain[5] = currXP[5] - startXP[5]; // hp
			xpGain[6] = currXP[6] - startXP[6]; // prayer

			drawMouse(g);
			paintWrapper(g);
			paintBody(g);
			paintXP(g);
		}
	}

	private boolean listContainsString(List<String> list, String string) {
		try {
			int a;
			for (a = list.size() - 1; a-- >= 0;) {
				if (list.get(a).contains(string))
					return true;
			}
		} catch (Exception ignored) {
		}
		return false;
	}

	private RSNPC getInteractingNPC() {
		final int[] validNPCs = Bot.getClient().getRSNPCIndexArray();

		for (final int element : validNPCs) {
			Node localNode = Calculations.findNodeByID(Bot.getClient()
					.getRSNPCNC(), element);
			if (localNode == null)
				continue;
			if (!(localNode instanceof RSNPCNode)) {
				continue;
			}
			final RSNPC Monster = new RSNPC(((RSNPCNode) localNode).getRSNPC());
			if (Monster.getInteracting() != null) {
				if (Monster.getInteracting().equals(getMyPlayer())) {
					return Monster;
				}
			}
		}
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void failSafe() {
		RSObject sObj = getNearestObjectByID(Obj1);
		while (sObj != null && Bot.getClient().getPlane() > 0) {
			status = "Failsafe active.";
			log("Failsafe activated.");
			walkTileOnScreen(sObj.getLocation());
			wait(1000);
			atObject(sObj, "Climb-down");
			wait(1000);
		}
	}

	private int lootItems() {
		if (!isInventoryFull() && lootArray != null) {
			for (int i = 0; i < lootArray.length; i++) {
				if (lootArray[i] != 0) {
					RSItemTile item;
					while ((item = getNearestGroundItemByID(lootArray[i])) != null
							&& !isInventoryFull()) {
						status = "Looting";
						if (tileOnScreen(item)) {
							Point daP = Calculations.tileToScreen(new RSTile(
									item.getX(), item.getY()));
							int theX = daP.x;
							int theY = daP.y;
							moveMouse(theX, theY);
							wait(random(50, 100));
							if (getMenuItems().size() > 0) {
								List<String> actions = getMenuItems();
								if (pickArrows) {
									if (lootArray[i] == ArrowID) {
										if (listContainsString(actions, "Take "
												+ ArrowName)) {
											if (actions.get(0).contains(
													"Take " + ArrowName)) {
												clickMouse(true);
												wait(random(750, 1000));
												break;
											} else {
												clickMouse(false);
												wait(random(250, 500));
												atMenu("Take " + ArrowName);
												wait(random(1000, 1300));
												break;
											}
										} else {
											continue;
										}
									}
								}
								if (listContainsString(actions, "Take "
										+ itemNames[i])) {
									if (actions.get(0).contains(
											"Take " + itemNames[i])) {
										clickMouse(true);
										wait(random(750, 1000));
									} else {
										clickMouse(false);
										wait(random(250, 500));
										atMenu("Take " + itemNames[i]);
										wait(random(1000, 1300));
									}

								}
								log("Take " + itemNames[i]);
								while (getMyPlayer().isMoving()) {
									wait(random(50, 120));
								}

							}
						} else {
							walkTo(new RSTile(item.getX(), item.getY()));
						}
					}
				}
			}
		}
		return 50;
	}

	public int getMouseSpeed() {
		return mouseSpeed;
	}

	private void BuryBones() {
		while (getInventoryCount(526) > 0) {
			status = "Burying bones";
			log("Burying bones...");
			atInventoryItem(526, "Bury");
			wait(random(350, 750));
			if (getInventoryCount(526) < 1) {
				log("Bury bones has finished.");
				break;
			}
		}
	}

	private void useFood() {
		if (getInventoryCount(foodID) != 0 && hasLowHP()) {
			status = "Eating food";
			atInventoryItem(foodID, "Eat");
		}
	}

	private void setRun() {
		if (getMyPlayer().isMoving()) {
			if (!isRunning() && getEnergy() > 50) {
				setRun(true);
				wait(random(1000, 1500));
			}
		}
	}

	private boolean hasLowHP() {
		return getMyPlayer().getHPPercent() < random(50 - 20, 60 + 10);
	}

	public boolean playerIsInArea(int[] paramArrayOfInt) {
		/*
		 * 0 = bottom tile 1 = top tile 2 = left tile 3 = right tile
		 */
		RSTile localRSTile = getMyPlayer().getLocation();
		return ((localRSTile.getX() >= paramArrayOfInt[0])
				&& (localRSTile.getX() <= paramArrayOfInt[1])
				&& (localRSTile.getY() >= paramArrayOfInt[2]) && (localRSTile
				.getY() <= paramArrayOfInt[3]));
	}

	private String formatTime(final int milliseconds) {
		final long t_seconds = milliseconds / 1000;
		final long t_minutes = t_seconds / 60;
		final long t_hours = t_minutes / 60;
		final int seconds = (int) (t_seconds % 60);
		final int minutes = (int) (t_minutes % 60);
		final int hours = (int) (t_hours % 60);
		return nf.format(hours) + ":" + nf.format(minutes) + ":"
				+ nf.format(seconds);
	}

	private class XAntiBan implements Runnable {
		public boolean stopThread;

		public void run() {
			while (!stopThread) {
				try {
					if (random(0, 15) == 0) {
						final char[] LR = new char[] { KeyEvent.VK_LEFT,
								KeyEvent.VK_RIGHT };
						final char[] UD = new char[] { KeyEvent.VK_DOWN,
								KeyEvent.VK_UP };
						final char[] LRUD = new char[] { KeyEvent.VK_LEFT,
								KeyEvent.VK_RIGHT, KeyEvent.VK_UP,
								KeyEvent.VK_UP };
						final int random2 = random(0, 2);
						final int random1 = random(0, 2);
						final int random4 = random(0, 4);

						if (random(0, 3) == 0) {
							Bot.getInputManager().pressKey(LR[random1]);
							Thread.sleep(random(100, 400));
							Bot.getInputManager().pressKey(UD[random2]);
							Thread.sleep(random(300, 600));
							Bot.getInputManager().releaseKey(UD[random2]);
							Thread.sleep(random(100, 400));
							Bot.getInputManager().releaseKey(LR[random1]);
						} else {
							Bot.getInputManager().pressKey(LRUD[random4]);
							if (random4 > 1) {
								Thread.sleep(random(300, 600));
							} else {
								Thread.sleep(random(500, 900));
							}
							Bot.getInputManager().releaseKey(LRUD[random4]);
						}
					} else {
						Thread.sleep(random(200, 2000));
					}
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public class DGui extends JDialog implements ActionListener {

		public DGui() {
			showDialog();
		}

		JButton btnOK;
		JLabel label9;

		public void showDialog() {

			// ---- btnOK ----
			btnOK = new JButton();
			btnOK.addActionListener(this);
			btnOK.setText("OK");
			add(btnOK);
			btnOK.setBounds(160, 145, 190, btnOK.getPreferredSize().height);

			// ---- label9 ----
			label9 = new JLabel();

			label9
					.setText("<html><img src='http://binaryx.nl/bytecode/images/brought.png'></html>");
			add(label9);
			label9.setBounds(5, 5, 535, 135);

			setResizable(false);
			setTitle("ByteCode Scripting");
			setAlwaysOnTop(true);
			setLayout(null);

			setPreferredSize(new Dimension(550, 200));
			pack();

			setVisible(true);

		}

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnOK) {
				setVisible(false);
				gui.setVisible(true);
			}
		}
	}

	public class XGui extends JFrame implements ListSelectionListener,
			ActionListener {

		public String getNews() throws IOException {
			try {
				String line;
				String Text = "";

				url = new URL(
						"http://binaryx.nl/BinaryX/Development/AlkharidFighter/news.txt")
						.openConnection();

				in = new BufferedReader(new InputStreamReader(url
						.getInputStream()));
				while ((line = in.readLine()) != null) {
					Text += line + "\n";

				}
				return Text;
			} catch (MalformedURLException e) {

				e.printStackTrace();
			} catch (UnknownHostException e) {
				e.printStackTrace();

			} catch (IOException e) {
				e.printStackTrace();

			} finally {
				in.close();
			}
			return null;
		}

		public XGui() {
			initComponents();
		}

		public void valueChanged(final ListSelectionEvent arg0) {

		}

		public void actionPerformed(final ActionEvent e) {
			if (e.getSource() == btnStart) {
				if (txtfid.getText().equals("0") || txtfid.getText().equals("")
						|| Integer.parseInt(txtfid.getText()) == 0) {
					log("Eating: Disabled.");
					useFood = false;
				} else {
					useFood = true;
					log("Eating Enabled.");
					foodID = Integer.parseInt(txtfid.getText());
					foodCount = Integer.parseInt((String) cbfcount
							.getSelectedItem());
					log("Food ID: " + foodID);
					log("Food Count: " + foodCount);
				}
				pickArrows = checkBox1.isSelected();
				log("Pickup and equip arrows: " + pickArrows);
				lootArray = new int[listLoot.getModel().getSize()];

				for (int i = 0; i < listLoot.getModel().getSize(); i++) {
					if (listLoot.isSelectedIndex(i)) {

						String selected = (String) listLoot.getModel()
								.getElementAt(i);
						String itemID = selected.substring(selected
								.indexOf(","));
						String replaced = itemID.replaceFirst(",", "");
						lootArray[i] = Integer.parseInt(replaced);

					}
				}

				if (pickArrows) {
					ArrowID = Integer.parseInt(txtArrowID.getText());
					ArrowName = txtArrowName.getText();
					ArrowCount = Integer.parseInt(txtArrowCount.getText());
					for (int x = 0; x < lootArray.length; x++) {
						if (lootArray[x] == 0) { // found an unused spot in the
							// loot list.
							lootArray[x] = ArrowID;
							break;
						}
					}
				}
				buryBones = chkBury.isSelected();
				log("Bury bones: " + buryBones);
				mouseSpeed = sliderMS.getValue();
				changeSpeed = sliderMS.getValue() > 1;
				log("Change mouse speed: " + changeSpeed + " (" + mouseSpeed
						+ ").");
				antiBan = chkAntiBan.isSelected();
				log("Antiban enabled: " + antiBan);
				guiWait = false;
				setVisible(false);
			}

		}

		private void initComponents() {
			// JFormDesigner - Component initialization - DO NOT MODIFY
			// //GEN-BEGIN:initComponents
			tabbedPane1 = new JTabbedPane();
			panel2 = new JPanel();
			label1 = new JLabel();
			txtfid = new JTextField();
			cbfcount = new JComboBox();
			chkAntiBan = new JCheckBox();
			sliderMS = new JSlider();
			label5 = new JLabel();
			chkBury = new JCheckBox();
			checkBox1 = new JCheckBox();
			scrollPane3 = new JScrollPane();
			listLoot = new JList();
			label4 = new JLabel();
			separator1 = new JSeparator();
			separator2 = new JSeparator();
			separator3 = new JSeparator();
			separator4 = new JSeparator();
			scrollPane1 = new JScrollPane();
			txtNews = new JTextArea();
			separator5 = new JSeparator();
			separator6 = new JSeparator();
			label7 = new JLabel();
			txtArrowCount = new JTextField();
			label8 = new JLabel();
			label6 = new JLabel();
			txtArrowID = new JTextField();
			label12 = new JLabel();
			txtArrowName = new JTextField();
			btnStart = new JButton();
			btnLink = new JButton();
			label3 = new JLabel();

			//======== this ========
			setResizable(false);
			setTitle("AlkharidFighter");
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			setAlwaysOnTop(true);
			Container contentPane = getContentPane();
			contentPane.setLayout(null);
			btnStart.addActionListener(this);
			cbfcount.addActionListener(this);
			txtfid.addActionListener(this);
			chkAntiBan.addActionListener(this);
			listLoot.addListSelectionListener(this);
			chkBury.addActionListener(this);
			JLabel label2;
			label2 = new JLabel();

						// ---- label2 ----
						label2.setText("<html><img src='http://binaryx.nl/BinaryX/Development/AlkharidFighter/GUILogo.png'></html>");
						contentPane.add(label2);
						label2.setBounds(85, 5, 435, 45);

			//======== tabbedPane1 ========
			{

				//======== panel2 ========
				{
					panel2.setLayout(null);

					//---- label1 ----
					label1.setText("Food ID:");
					panel2.add(label1);
					label1.setBounds(60, 45, 55, 15);

					//---- txtfid ----
					txtfid.setText("0");
					txtfid.setToolTipText("Leave this field 0 to disable eating.");
					panel2.add(txtfid);
					txtfid.setBounds(110, 35, 65, 15);

					//---- cbfcount ----
					cbfcount.setModel(new DefaultComboBoxModel(new String[] {
						"28",
						"27",
						"26",
						"25",
						"24",
						"23",
						"22",
						"21",
						"20",
						"19",
						"18",
						"17",
						"16",
						"15",
						"14",
						"13",
						"12",
						"11",
						"10",
						"9",
						"8",
						"7",
						"6",
						"5",
						"4",
						"3",
						"2",
						"1"
					}));
					cbfcount.setToolTipText("The total amount of food to be withdrawn when banking.");
					panel2.add(cbfcount);
					cbfcount.setBounds(110, 55, 65, cbfcount.getPreferredSize().height);

					//---- chkAntiBan ----
					chkAntiBan.setText("Enable Antiban");
					panel2.add(chkAntiBan);
					chkAntiBan.setBounds(new Rectangle(new Point(105, 100), chkAntiBan.getPreferredSize()));

					//---- sliderMS ----
					sliderMS.setPaintTicks(true);
					sliderMS.setMinimum(1);
					sliderMS.setMaximum(10);
					sliderMS.setValue(1);
					sliderMS.setPaintLabels(true);
					sliderMS.setSnapToTicks(true);
					sliderMS.setMajorTickSpacing(10);
					sliderMS.setMinorTickSpacing(1);
					sliderMS.setPaintTicks(true);
					sliderMS.setPaintLabels(true);
					panel2.add(sliderMS);
					sliderMS.setBounds(5, 145, 300, 15);

					//---- label5 ----
					label5.setText("Mouse Speed - Leave it 1 for default speed.");
					panel2.add(label5);
					label5.setBounds(45, 125, label5.getPreferredSize().width, 14);

					//---- chkBury ----
					chkBury.setText("Bury Bones");
					panel2.add(chkBury);
					chkBury.setBounds(new Rectangle(new Point(105, 165), chkBury.getPreferredSize()));

					//---- checkBox1 ----
					checkBox1.setText("Pickup and Equip arrows");
					panel2.add(checkBox1);
					checkBox1.setBounds(new Rectangle(new Point(0, 200), checkBox1.getPreferredSize()));

					//======== scrollPane3 ========
					{

						//---- listLoot ----
						listLoot.setModel(new AbstractListModel() {
							String[] values = {
								"Bones,526",
								"Bronze Arrows,882",
								"Bronze Bolts,877",
								"Bronze med helm,1139",
								"Cabbage,1965",
								"Chaos rune,562",
								"Coins,995",
								"Copper ore,436",
								"Earth rune,557",
								"Earth Talisman,1440",
								"Fire rune,554",
								"Fishing bait,313",
								"Grimy avantoe,211",
								"Grimy dwarf weed,217",
								"Grimy guam,199",
								"Grimy harralander,205",
								"Grimy irit,209",
								"Grimy kwuarm,219",
								"Grimy lantadyme,2485",
								"Grimy marrentill,201",
								"Grimy ranarr,207",
								"Grimy tarromin,203",
								"Iron dagger,1203",
								"Iron med helm,1137",
								"Mind rune,558"
							};
							public int getSize() { return values.length; }
							public Object getElementAt(int i) { return values[i]; }
						});
						listLoot.setToolTipText("Select the item(s) you wish to loot.");
						listLoot.setBorder(UIManager.getBorder("ToolTip.border"));
						scrollPane3.setViewportView(listLoot);
					}
					panel2.add(scrollPane3);
					scrollPane3.setBounds(305, 30, 265, 200);

					//---- label4 ----
					label4.setText("Use CTRL to select multiple items.");
					panel2.add(label4);
					label4.setBounds(new Rectangle(new Point(305, 230), label4.getPreferredSize()));
					panel2.add(separator1);
					separator1.setBounds(0, 20, 580, 15);
					panel2.add(separator2);
					separator2.setBounds(0, 90, 295, 5);
					panel2.add(separator3);
					separator3.setBounds(5, 290, 575, 5);

					//---- separator4 ----
					separator4.setOrientation(SwingConstants.VERTICAL);
					panel2.add(separator4);
					separator4.setBounds(295, 20, 5, 270);

					//======== scrollPane1 ========
					{

						//---- txtNews ----
						txtNews.setText("Getting latest news from binaryx.nl ..");
						txtNews.setLineWrap(true);
						txtNews.setEditable(false);
						try {
						txtNews.setText(getNews());
						} catch(IOException ignored) { }
						scrollPane1.setViewportView(txtNews);
					}
					panel2.add(scrollPane1);
					scrollPane1.setBounds(0, 300, 575, 110);
					panel2.add(separator5);
					separator5.setBounds(0, 160, 295, 5);
					panel2.add(separator6);
					separator6.setBounds(0, 195, 295, 5);

					//---- label7 ----
					label7.setText("when there are");
					panel2.add(label7);
					label7.setBounds(new Rectangle(new Point(140, 205), label7.getPreferredSize()));

					//---- txtArrowCount ----
					txtArrowCount.setText("1000");
					panel2.add(txtArrowCount);
					txtArrowCount.setBounds(5, 230, 45, 15);

					//---- label8 ----
					label8.setText("in the inventory.");
					panel2.add(label8);
					label8.setBounds(new Rectangle(new Point(55, 230), label8.getPreferredSize()));

					//---- label6 ----
					label6.setText("Arrow ID:");
					panel2.add(label6);
					label6.setBounds(new Rectangle(new Point(5, 250), label6.getPreferredSize()));

					//---- txtArrowID ----
					txtArrowID.setText("0");
					panel2.add(txtArrowID);
					txtArrowID.setBounds(75, 250, 50, 15);

					//---- label12 ----
					label12.setText("Arrow Name:");
					panel2.add(label12);
					label12.setBounds(new Rectangle(new Point(5, 270), label12.getPreferredSize()));

					//---- txtArrowName ----
					txtArrowName.setText("Bronze arrow");
					panel2.add(txtArrowName);
					txtArrowName.setBounds(new Rectangle(new Point(75, 265), txtArrowName.getPreferredSize()));

					{ // compute preferred size
						Dimension preferredSize = new Dimension();
						for(int i = 0; i < panel2.getComponentCount(); i++) {
							Rectangle bounds = panel2.getComponent(i).getBounds();
							preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
							preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
						}
						Insets insets = panel2.getInsets();
						preferredSize.width += insets.right;
						preferredSize.height += insets.bottom;
						panel2.setMinimumSize(preferredSize);
						panel2.setPreferredSize(preferredSize);
					}
				}
				tabbedPane1.addTab("General Settings", panel2);

			}
			contentPane.add(tabbedPane1);
			tabbedPane1.setBounds(5, 60, 580, 435);

			//---- btnStart ----
			btnStart.setText("Start Script");
			btnStart.setToolTipText("Start AlkharidFighter.");
			contentPane.add(btnStart);
			btnStart.setBounds(215, 535, 160, 20);

			//---- btnLink ----
			btnLink.setText("Visit The Underdome");
			btnLink.setToolTipText("Go to the Underdome.");
			contentPane.add(btnLink);
			btnLink.setBounds(215, 515, 160, 20);

			//---- label3 ----
			label3.setText("Hover the mouse over a component for more information.");
			contentPane.add(label3);
			label3.setBounds(new Rectangle(new Point(165, 495), label3.getPreferredSize()));

			{ // compute preferred size
				Dimension preferredSize = new Dimension();
				for(int i = 0; i < contentPane.getComponentCount(); i++) {
					Rectangle bounds = contentPane.getComponent(i).getBounds();
					preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
					preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
				}
				Insets insets = contentPane.getInsets();
				preferredSize.width += insets.right;
				preferredSize.height += insets.bottom;
				contentPane.setMinimumSize(preferredSize);
				contentPane.setPreferredSize(preferredSize);
			}
			pack();
			setLocationRelativeTo(getOwner());
			// //GEN-END:initComponents
		}

		// JFormDesigner - Variables declaration - DO NOT MODIFY
		// //GEN-BEGIN:variables
		private JTabbedPane tabbedPane1;
		private JPanel panel2;
		private JLabel label1;
		private JTextField txtfid;
		private JComboBox cbfcount;
		private JCheckBox chkAntiBan;
		private JSlider sliderMS;
		private JLabel label5;
		private JCheckBox chkBury;
		private JCheckBox checkBox1;
		private JScrollPane scrollPane3;
		private JList listLoot;
		private JLabel label4;
		private JSeparator separator1;
		private JSeparator separator2;
		private JSeparator separator3;
		private JSeparator separator4;
		private JScrollPane scrollPane1;
		private JTextArea txtNews;
		private JSeparator separator5;
		private JSeparator separator6;
		private JLabel label7;
		private JTextField txtArrowCount;
		private JLabel label8;
		private JLabel label6;
		private JTextField txtArrowID;
		private JLabel label12;
		private JTextField txtArrowName;
		private JButton btnStart;
		private JButton btnLink;
		private JLabel label3;
		// JFormDesigner - End of variables declaration //GEN-END:variables
	}

}