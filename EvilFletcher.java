import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Map;
import java.util.Random;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.rsbot.bot.Bot;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSObject;

@ScriptManifest(authors = { "Evil Knievel" }, category = "Fletching", name = "Evil All-in-one Fletcher", version = 1.04, description = "<html><head></head><body><b><center>When you click Start a GUI will pop up</center></b></body></html>\n")
public class EvilFletcher extends Script implements PaintListener {

	// GUI Inputs:
	public static boolean guidone = false;
	public static boolean antiban;
	public static boolean autoChopBox;
	public static String whatToFletch = "";
	public static String doWhat = "";

	@SuppressWarnings("serial")
	class GUI extends JFrame implements ActionListener {

		private final JLabel descLabel = new JLabel("<html><center><b>"
				+ properties.name() + " v" + properties.version()
				+ "</b></center></html>");
		private final JLabel descLabel1 = new JLabel(
				"This can do everything Fletching Related.");
		private final JLabel descLabel2 = new JLabel(
				"Start at any bank with appropriate items needed.");
		private final JButton startScript = new JButton("Start script");
		private final JTabbedPane tabs = new JTabbedPane();
		private final JPanel startPanel = new JPanel();
		private final JPanel settingsPanel = new JPanel();
		private final JPanel othersPanel = new JPanel();
		private final JComboBox whatToFletch = new JComboBox(new String[] {
				"Bows - Cut", "Bows - String", "Bolts" });
		private final DefaultComboBoxModel model = new DefaultComboBoxModel();
		private final JComboBox whatToDo = new JComboBox(model);
		private final JCheckBox antibanBox = new JCheckBox("Use antiban?");
		// private JCheckBox autoChopBox = new
		// JCheckBox("Auto chop and fletch?");
		private String[] doOptions = new String[] { "Normal - Shortbow",
				"Normal - Longbow", "Oak - Shortbow", "Oak - Longbow",
				"Willow - Shortbow", "Willow - Longbow", "Maple - Shortbow",
				"Maple - Longbow", "Yew - Shortbow", "Yew - Longbow",
				"Magic - Shortbow", "Magic - Longbow" };

		public GUI() {
			super(properties.name() + " v" + properties.version()
					+ " - By Evil Knievel");
			startPanel.setLayout(new FlowLayout());
			settingsPanel.setLayout(new FlowLayout());
			othersPanel.setLayout(new FlowLayout());

			startScript.setActionCommand("start");
			startScript.addActionListener(this);

			startPanel.add(descLabel);
			startPanel.add(descLabel1);
			startPanel.add(descLabel2);
			startPanel.add(startScript);

			whatToFletch.addActionListener(this);
			settingsPanel.add(whatToFletch);

			model.removeAllElements();
			for (final String s : doOptions) {
				model.addElement(s);
			}
			settingsPanel.add(whatToDo);

			othersPanel.add(antibanBox);

			tabs.addTab("Start", startPanel);
			tabs.addTab("Fletching Options", settingsPanel);
			tabs.addTab("Other Options", othersPanel);

			add(tabs);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setPreferredSize(new Dimension(260, 165));
			pack();
			setVisible(true);
		}

		public void actionPerformed(final ActionEvent e) {
			if (e.getActionCommand().equals("start")) {
				EvilFletcher.antiban = antibanBox.isSelected();
				EvilFletcher.guidone = true;
				EvilFletcher.whatToFletch = whatToFletch.getSelectedItem()
						.toString();
				EvilFletcher.doWhat = whatToDo.getSelectedItem().toString();
				dispose();
				return;
			}

			if (whatToFletch.getSelectedItem().equals("Bows - Cut")) {
				doOptions = new String[] { "Normal - Shortbow",
						"Normal - Longbow", "Oak - Shortbow", "Oak - Longbow",
						"Willow - Shortbow", "Willow - Longbow",
						"Maple - Shortbow", "Maple - Longbow",
						"Yew - Shortbow", "Yew - Longbow", "Magic - Shortbow",
						"Magic - Longbow" };
			}

			if (whatToFletch.getSelectedItem().equals("Bows - String")) {
				doOptions = new String[] { "Normal - Shortbow",
						"Normal - Longbow", "Oak - Shortbow", "Oak - Longbow",
						"Willow - Shortbow", "Willow - Longbow",
						"Maple - Shortbow", "Maple - Longbow",
						"Yew - Shortbow", "Yew - Longbow", "Magic - Shortbow",
						"Magic - Longbow" };
			}

			if (whatToFletch.getSelectedItem().equals("Bolts")) {
				doOptions = new String[] { "Bronze", "Iron", "Steel",
						"Mithril", "Adamantite", "Runite" };
			}
			model.removeAllElements();
			for (final String s : doOptions) {
				model.addElement(s);
			}
		}

	}

	// Script Manifest:
	private final ScriptManifest properties = getClass().getAnnotation(
			ScriptManifest.class);

	// Interfaces/Objects/NPCs:
	final RSInterface INTERFACE_FLETCH = RSInterface.getInterface(513);
	final RSInterfaceChild FLETCH_AREA = RSInterface.getChildInterface(513, 3);

	public int[] BankChests = { 27663, 4483, 12308, 21301, 42192 };
	public RSObject Chest = getNearestObjectByID(BankChests);

	// AntiBan:
	EvilAntiBan antib;
	Thread t;

	// Paint:
	private long startTime = -1;
	private long startingxp = -1;
	private final Color BG = new Color(123, 123, 123, 100);
	private final Color TEXT = new Color(220, 220, 0, 255);
	private final Color RED = new Color(255, 0, 0, 150);

	// Bow Cutting:
	public int Knife = 946;
	public int Log;

	// Bow Stringing:
	public int Bow_String = 1777;
	public int Bow;

	// Bolt Making:
	public int Bolt;
	public int Feathers = 314;

	// Tree Chopping -> Fletching:
	public boolean AxeCheck = false;
	public int Tree;

	public int[] Axes = { 1349, 1351, 1353, 1355, 1357, 1359, 1361, 6739 };

	public int CurrentLevel = skills.getRealSkillLevel(STAT_WOODCUTTING);

	public int[] NORMALTREES = { 5004, 5005, 5045, 3879, 3881, 3882, 3883,
			3885, 3886, 3887, 3888, 3889, 3890, 3891, 3892, 3893, 3928, 3967,
			3968, 4048, 4049, 4050, 4051, 4052, 4053, 4054, 3033, 3034, 3035,
			3036, 2409, 2447, 2448, 1330, 1331, 1332, 1310, 1305, 1304, 1303,
			1301, 1276, 1277, 1278, 1279, 1280, 8742, 8743, 8973, 8974, 1315,
			1316 };

	public int[] OAKTREES = { 1281, 3037, 8462, 8463, 8464, 8465, 8466, 8467 };

	public int[] WILLOWTREES = { 1308, 5551, 5552, 5553, 8481, 8482, 8483,
			8484, 8485, 8486, 8487, 8488 };

	public int[] MAPLETREES = { 1307 };

	public int[] YEWTREES = { 1309, 8503, 8504, 8505, 8506, 8507, 8508, 8509,
			8510, 8511, 8512, 8513 };

	public int[] MAGICTREES = { 1306 };

	protected int getMouseSpeed() {
		return random(6, 9);
	}

	public boolean onStart(final Map<String, String> args) {
		log("Opening GUI.");
		new GUI();
		while (!guidone) {
			wait(1000);
		}

		antib = new EvilAntiBan();
		t = new Thread(antib);

		if (whatToFletch.equals("Bows - Cut")) {
			if (doWhat.equals("Normal - Shortbow")
					|| doWhat.equals("Normal - Longbow")) {
				Log = 1511;
			}

			if (doWhat.equals("Oak - Shortbow")
					|| doWhat.equals("Oak - Longbow")) {
				Log = 1521;
			}

			if (doWhat.equals("Willow - Shortbow")
					|| doWhat.equals("Willow - Longbow")) {
				Log = 1519;
			}

			if (doWhat.equals("Maple - Shortbow")
					|| doWhat.equals("Maple - Longbow")) {
				Log = 1517;
			}

			if (doWhat.equals("Yew - Shortbow")
					|| doWhat.equals("Yew - Longbow")) {
				Log = 1515;
			}

			if (doWhat.equals("Magic - Shortbow")
					|| doWhat.equals("Magic - Longbow")) {
				Log = 1513;
			}
		}

		if (whatToFletch.equals("Bows - String")) {
			if (doWhat.equals("Normal - Shortbow")) {
				Bow = 50;
			}

			if (doWhat.equals("Normal - Longbow")) {
				Bow = 48;
			}

			if (doWhat.equals("Oak - Shortbow")) {
				Bow = 54;
			}

			if (doWhat.equals("Oak - Longbow")) {
				Bow = 56;
			}

			if (doWhat.equals("Willow - Shortbow")) {
				Bow = 60;
			}

			if (doWhat.equals("Willow - Longbow")) {
				Bow = 58;
			}

			if (doWhat.equals("Maple - Shortbow")) {
				Bow = 64;
			}

			if (doWhat.equals("Maple - Longbow")) {
				Bow = 62;
			}

			if (doWhat.equals("Yew - Shortbow")) {
				Bow = 68;
			}

			if (doWhat.equals("Yew - Longbow")) {
				Bow = 66;
			}

			if (doWhat.equals("Magic - Shortbow")) {
				Bow = 72;
			}

			if (doWhat.equals("Magic - Longbow")) {
				Bow = 70;
			}
		}

		if (whatToFletch.equals("Bolts")) {
			if (doWhat.equals("Bronze")) {
				Bolt = 9375;
			}

			if (doWhat.equals("Iron")) {
				Bolt = 9377;
			}

			if (doWhat.equals("Steel")) {
				Bolt = 9378;
			}

			if (doWhat.equals("Mithril")) {
				Bolt = 9379;
			}

			if (doWhat.equals("Adamantite")) {
				Bolt = 9380;
			}

			if (doWhat.equals("Runite")) {
				Bolt = 9381;
			}
		}

		return true;
	}

	public void FletchBow() {
		if (INTERFACE_FLETCH.isValid()) {
			if (!doWhat.contains("Magic") && !doWhat.contains("Normal")) {
				if (doWhat.contains("Short")) {
					moveMouse(random(52, 147), random(390, 450));
					wait(random(400, 800));
					atMenu("Make X");
					wait(random(800, 1100));
					sendText("27", true);
					wait(2000);
				} else {
					moveMouse(random(215, 309), random(390, 450));
					wait(random(400, 800));
					atMenu("Make X");
					wait(random(800, 1100));
					sendText("27", true);
					wait(2000);
				}
			} else if (doWhat.contains("Magic")) {
				if (doWhat.contains("Short")) {
					moveMouse(random(95, 172), random(376, 462));
					wait(random(400, 800));
					atMenu("Make X");
					wait(random(800, 1100));
					sendText("27", true);
					wait(2000);
				} else {
					moveMouse(random(341, 432), random(391, 464));
					wait(random(400, 800));
					atMenu("Make X");
					wait(random(800, 1100));
					sendText("27", true);
					wait(2000);
				}
			} else if (doWhat.contains("Normal")) {
				if (doWhat.contains("Short")) {
					moveMouse(random(170, 225), random(391, 454));
					wait(random(400, 800));
					atMenu("Make X");
					wait(random(800, 1100));
					sendText("27", true);
					wait(2000);
				} else {
					moveMouse(random(287, 364), random(391, 454));
					wait(random(400, 800));
					atMenu("Make X");
					wait(random(800, 1100));
					sendText("27", true);
					wait(2000);
				}
			}
		} else {
			atInventoryItem(Knife, "Use");
			wait(random(400, 600));
			atInventoryItem(Log, "Use");
			wait(random(400, 600));
		}
	}

	public void FletchString() {
		if (!FLETCH_AREA.isValid()) {
			wait(random(300, 900));
			atInventoryItem(Bow, "Use");
			wait(random(300, 900));
			atInventoryItem(Bow_String, "Use");
			wait(random(300, 900));
		}
		if (FLETCH_AREA.isValid()) {
			if (!atInterface(FLETCH_AREA, "Make All")) {
				atInventoryItem(Bow_String, "Use");
			} else {
				wait(2000);
			}
		}
	}

	@Override
	public int loop() {
		if (antiban) {
			getMouseSpeed();
			setCameraAltitude(true);
			if (!t.isAlive()) {
				t.start();
				log("Evil-AntiBan initialized!");
				// Credits to Taha for AntiBan. Thanks taha :)
			}
		}

		while (getMyPlayer().getAnimation() == 1248 && !canContinue()) {
			waitForAnim(-1);
		}

		while (getMyPlayer().getAnimation() == 6688
				|| getInventoryCount(Bow) <= 13 && getInventoryCount(Bow) >= 1
				&& !canContinue()) {
			waitForAnim(-1);
		}

		if (Bot.getClient().isItemSelected() == Bow_String
				&& FLETCH_AREA.isValid()
				|| Bot.getClient().isItemSelected() == Bow
				&& FLETCH_AREA.isValid()) {
			atInventoryItem(Bot.getClient().isItemSelected(), "Use");
			wait(random(300, 900));
		}

		if (canContinue()) {
			if (whatToFletch.equals("Bows - Cut")) {
				atInventoryItem(Log, "Use");
				wait(random(400, 800));
				atInventoryItem(Knife, "Use");
				wait(1500);
				if (INTERFACE_FLETCH.isValid() && FLETCH_AREA.isValid()
						&& Bot.getClient().isItemSelected() != Knife
						&& Bot.getClient().isItemSelected() != Log) {
					if (doWhat.contains("Short")) {
						moveMouse(random(52, 147), random(390, 450));
						wait(random(400, 800));
						atMenu("Make X");
						wait(random(800, 1100));
						sendText("27", true);
						wait(2000);
					} else {
						moveMouse(random(215, 309), random(390, 450));
						wait(random(400, 800));
						atMenu("Make X");
						wait(random(800, 1100));
						sendText("27", true);
						wait(2000);
					}
				}
			} else if (whatToFletch.equals("Bow - String")) {
				atInventoryItem(Bow, "Use");
				wait(random(400, 800));
				atInventoryItem(Bow_String, "Use");
				wait(1500);
				if (RSInterface.getInterface(513).isValid()
						&& Bot.getClient().isItemSelected() != Bow_String
						&& Bot.getClient().isItemSelected() != Bow) {
					if (doWhat.contains("Short")) {
						moveMouse(random(52, 147), random(390, 450));
						wait(random(400, 800));
						atMenu("Make All");
						wait(6000);
					} else {
						moveMouse(random(215, 309), random(390, 450));
						wait(random(400, 800));
						atMenu("Make All");
						wait(100);
					}
				}
			}
		}

		if (whatToFletch.equals("Bows - Cut") && !autoChopBox) {
			if (!inventoryContains(Knife)) {
				log("You have no knife for cutting logs into bows(u).");
				stopScript();
			} else if (getInventoryCount(Log) >= 27 && !bank.isOpen()) {
				if (Bot.getClient().isItemSelected() != Log
						&& Bot.getClient().isItemSelected() != Knife) {
					FletchBow();
				}
			} else if (getInventoryCount(Log) <= 0 && !bank.isOpen()
					&& Chest == null) {
				bank.open();
			} else if (getInventoryCount(Log) <= 0 && !bank.isOpen()
					&& Chest != null) {
				atObject(Chest, "Use");
			} else if (bank.isOpen() && !(getInventoryCount(Log) >= 27)) {
				bank.depositAllExcept(Knife);
				wait(random(600, 1100));
				bank.withdraw(Log, 100);
			} else if (bank.isOpen() && getInventoryCount(Log) >= 27) {
				bank.close();
			}
			return random(50, 250);
		}

		if (whatToFletch.equals("Bows - String") && !autoChopBox) {
			if (!inventoryContains(Bow) || !inventoryContains(Bow_String)
					&& !bank.isOpen() && Chest == null) {
				bank.open();
				wait(random(500, 1000));
				bank.depositAll();
				wait(random(500, 1000));
				bank.withdraw(Bow, 14);
				wait(random(800, 1200));
				bank.withdraw(Bow_String, 14);
			} else if (!inventoryContains(Bow)
					|| !inventoryContains(Bow_String) && !bank.isOpen()
					&& Chest != null) {
				atObject(Chest, "Use");
				wait(random(500, 1000));
				bank.depositAll();
				wait(random(500, 1000));
				bank.withdraw(Bow, 14);
				wait(random(800, 1200));
				bank.withdraw(Bow_String, 14);
			} else if (bank.isOpen() && getInventoryCount(Bow) >= 14) {
				bank.close();
			} else if (Bot.getClient().isItemSelected() != Bow_String
					&& Bot.getClient().isItemSelected() != Bow) {
				FletchString();
			}
			return random(50, 250);
		}

		if (whatToFletch.equals("Bolts") && !autoChopBox) {
			if (!inventoryContains(Bolt) || !inventoryContains(Feathers)) {
				log("You have no items for making bolts.");
				stopScript();
			} else {
				atInventoryItem(Bolt, "Use");
				wait(random(400, 800));
				atInventoryItem(Feathers, "Use");
				return random(100, 500);
			}
			return random(50, 250);
		}

		if (autoChopBox) {
		}
		return random(200, 350);
	}

	// Me starts becoming red again!! DO NOT USE @Override on IMPLEMENTATION
	// methods.
	// TBH DO NOT USE IT AT ALL, UNLESS YOU KNOW WHAT IT DOES
	public void onRepaint(final Graphics g) {
		if (isLoggedIn()) {
			final int x = 312;
			int y = 4;

			if (startingxp == -1) {
				startingxp = (long) skills.getCurrentSkillExp(STAT_FLETCHING);
				startTime = System.currentTimeMillis();
			}

			final int levelsGained = skills.getRealSkillLevel(STAT_FLETCHING)
					- skills.getLvlByExp((int) startingxp);
			final long runSeconds = (System.currentTimeMillis() - startTime) / 1000;

			g.setColor(BG);
			if (runSeconds != 0) {
				g.fill3DRect(x - 6, y, 211, 146, true);
			} else {
				g.fill3DRect(x - 6, y, 211, 105, true);
			}

			g.setColor(TEXT);
			g.drawString(properties.name() + " v" + properties.version(), x,
					y += 17);
			g.drawString(properties.name() + " v" + properties.version(), x, y);
			g.drawString("Running for "
					+ getFormattedTime(System.currentTimeMillis() - startTime)
					+ ".", x, y += 20);

			if (levelsGained < 0) {
				startingxp = skills.getCurrentSkillExp(STAT_FLETCHING);
			} else if (levelsGained == 1) {
				g
						.drawString(
								"Gained: "
										+ (skills
												.getCurrentSkillExp(STAT_FLETCHING) - startingxp)
										+ " XP (" + levelsGained + " lvl)", x,
								y += 20);
			} else {
				g
						.drawString(
								"Gained: "
										+ (skills
												.getCurrentSkillExp(STAT_FLETCHING) - startingxp)
										+ " XP (" + levelsGained + " lvls)", x,
								y += 20);
			}

			if (runSeconds > 0) {
				g
						.drawString(
								"Averaging: "
										+ (skills
												.getCurrentSkillExp(STAT_FLETCHING) - startingxp)
										* 3600 / runSeconds + " XP/hr", x,
								y += 20);
			}

			g.drawString("Current level: "
					+ skills.getRealSkillLevel(STAT_FLETCHING), x, y += 20);
			g.drawString("Next level: "
					+ skills.getXPToNextLevel(STAT_FLETCHING) + " XP", x,
					y += 20);
			if (runSeconds != 0) {
				g.setColor(RED);
				g.fill3DRect(x, y += 9, 200, 13, true);
				g.setColor(new Color((int) (255 - Math
						.floor(2.55 * (double) skills
								.getPercentToNextLevel(STAT_FLETCHING))), 255,
						0, 150));
				g.fill3DRect(x, y,
						skills.getPercentToNextLevel(STAT_FLETCHING) * 2, 13,
						true);
			}
		}
	}

	private String getFormattedTime(final long timeMillis) {
		long millis = timeMillis;
		final long seconds2 = millis / 1000;
		final long hours = millis / (1000 * 60 * 60);
		millis -= hours * 1000 * 60 * 60;
		final long minutes = millis / (1000 * 60);
		millis -= minutes * 1000 * 60;
		final long seconds = millis / 1000;
		String hoursString = "";
		String minutesString = "";
		String secondsString = seconds + "";
		String type = "seconds";

		if (minutes > 0) {
			minutesString = minutes + ":";
			type = "minutes";
		} else if (hours > 0 && seconds2 > 0) {
			minutesString = "0:";
		}
		if (hours > 0) {
			hoursString = hours + ":";
			type = "hours";
		}
		if (minutes < 10 && !type.equals("seconds")) {
			minutesString = "0" + minutesString;
		}
		if (hours < 10 && type.equals("hours")) {
			hoursString = "0" + hoursString;
		}
		if (seconds < 10 && !type.equals("seconds")) {
			secondsString = "0" + secondsString;
		}

		return hoursString + minutesString + secondsString + " " + type;
	}

	static class EvilAntiBan implements Runnable {
		// Thanks Taha - You're awesome :D
		public static boolean stopThread;

		public void run() {
			final Random random = new Random();
			while (!stopThread) {
				try {
					if (random.nextInt(15) == 0) {
						final char[] LR = new char[] { KeyEvent.VK_LEFT,
								KeyEvent.VK_RIGHT };
						final char[] UD = new char[] { KeyEvent.VK_DOWN,
								KeyEvent.VK_UP };
						final char[] LRUD = new char[] { KeyEvent.VK_LEFT,
								KeyEvent.VK_RIGHT, KeyEvent.VK_UP,
								KeyEvent.VK_UP };
						final int random2 = random.nextInt(2);
						final int random1 = random.nextInt(2);
						final int random4 = random.nextInt(4);

						if (random.nextInt(Math.abs(3)) == 0) {
							Bot.getInputManager().pressKey(LR[random1]);
							Thread.sleep(random.nextInt(300));
							Bot.getInputManager().pressKey(UD[random2]);
							Thread.sleep(random.nextInt(300));
							Bot.getInputManager().releaseKey(UD[random2]);
							Thread.sleep(random.nextInt(300));
							Bot.getInputManager().releaseKey(LR[random1]);
						} else {
							Bot.getInputManager().pressKey(LRUD[random4]);
							if (random4 > 1) {
								Thread.sleep(random.nextInt(300));
							} else {
								Thread.sleep(random.nextInt(400));
							}
							Bot.getInputManager().releaseKey(LRUD[random4]);
						}
					} else {
						Thread.sleep(random.nextInt(1800));
					}
				} catch (final Exception e) {
					System.out.println("Evil-AntiBan error detected!");
				}
			}
		}
	}

}