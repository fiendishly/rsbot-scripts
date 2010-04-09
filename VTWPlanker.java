import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Random;
import javax.swing.*;

import org.rsbot.bot.Bot;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.GEItemInfo;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = {"Versus The World"}, category = "Money", name = "VTW Planker", version = 1.6, description = "<html><body style='font-family: Arial; padding: 0px; background-color: #FFFFFF;'><center><b>Setup in GUI</b><br /><br />Start the script inside Varrock East bank with the amount of coins you wish to use in ur inventory. The script will run until you run out of either coins in inventory or logs in the bank. <br /><br />This is an AIO Planker. It offers every type of plank, and if you choose, will rest at the musician when needed.</center></body></html>")
public class VTWPlanker extends Script implements PaintListener {

	private final ScriptManifest properties = getClass().getAnnotation(
			ScriptManifest.class);

	// SETUP
	Thread t;
	GUI vtwBasicGUI;
	VTWPlankerAntiBan antiban;
	public int failCount = 0;
	public int selection = 0;
	public int plankCount = 0;
	public int runEnergy = random(50, 80);
	public long startTime = 0;
	public boolean setup = false;
	public boolean run = false;
	public boolean startup = false;
	public boolean useMusician = false;
	public boolean countCheck = true;
	public String status = "Start-up";

	// ITEM ID'S
	final static int LOGID[] = {1511, 1521, 6333, 6332};
	final static int PLANKID[] = {960, 8778, 8780, 8782};
	final static int COINS = 995;

	// NPC'S
	final static int MILLERID = 4250;
	final static int MUSICIANID = 8700;
	final static int BANKERID = 5912;

	// OBJECT ID
	final static int BANKID = 11402;

	// PATH
	final RSTile bankToMillerPath[] = {new RSTile(3254, 3422),
			new RSTile(3263, 3427), new RSTile(3275, 3428),
			new RSTile(3284, 3431), new RSTile(3286, 3441),
			new RSTile(3289, 3453), new RSTile(3294, 3462),
			new RSTile(3297, 3472), new RSTile(3299, 3481),
			new RSTile(3302, 3489)};

	// PRICES
	public int logPrice = 0;
	public int plankPrice = 0;
	public int charge = 0;
	public int millPrice[] = {100, 250, 500, 1500};

	// GAME STATES
	private enum State {
		bank, walk, plank
	}

	// *******************************************************//
	// PAINT SCREEN
	// *******************************************************//
	public void onRepaint(final Graphics g) {
		if (getCurrentTab() != TAB_INVENTORY) {
			return;
		}

		final int profit = plankCount * plankPrice - plankCount
				* (logPrice + millPrice[selection]);
		final int perHourProfit = (int) (profit * 3600000D / (System
				.currentTimeMillis() - startTime));
		final int perHourPlanks = (int) (plankCount * 3600000D / (System
				.currentTimeMillis() - startTime));

		final long runTime = System.currentTimeMillis() - startTime;
		final int seconds = (int) (runTime / 1000 % 60);
		final int minutes = (int) (runTime / 1000 / 60) % 60;
		final int hours = (int) (runTime / 1000 / 60 / 60) % 60;

		final StringBuilder botTime = new StringBuilder();
		if (hours < 10) {
			botTime.append('0');
		}
		botTime.append(hours);
		botTime.append(':');
		if (minutes < 10) {
			botTime.append('0');
		}
		botTime.append(minutes);
		botTime.append(':');
		if (seconds < 10) {
			botTime.append('0');
		}
		botTime.append(seconds);

		try {
			g.setColor(new Color(72, 61, 139, 200));
			g.fillRoundRect(555, 210, 175, 250, 10, 10);
			g.setColor(Color.white);
			final int[] coords = new int[]{225, 235, 255, 270, 285, 300, 315,
					330, 345, 360, 375, 390, 405, 420, 440};
			g.setFont(new Font("Calibri", Font.PLAIN, 14));
			g.drawString("Versus The World", 570, coords[1]);
			g.setFont(new Font("Calibri", Font.BOLD, 23));
			g.drawString("Planker", 605, coords[2]);
			g.setFont(new Font("Calibri", Font.PLAIN, 10));
			g.drawString("Version: " + properties.version(), 618, coords[3]);
			g.setFont(new Font("Calibri", Font.PLAIN, 12));
			g.drawString("___________________________", 561, 280);
			g.drawString("Time running: " + botTime, 580, coords[5]);

			g.setFont(new Font("Calibri", Font.PLAIN, 12));
			g.drawString("Status: " + status, 570, coords[7]);
			g.drawString("Profit: " + profit + "", 570, coords[9]);
			g.drawString(
					"Profit/Hour: " + Integer.toString(perHourProfit) + "",
					570, coords[10]);
			g.drawString("Planks made: " + Integer.toString(plankCount) + "",
					570, coords[12]);
			g.drawString(
					"Planks/Hour: " + Integer.toString(perHourPlanks) + "",
					570, coords[13]);

		} catch (final NullPointerException e) {
			e.printStackTrace();
		}
	}

	// *******************************************************//
	// ON START
	// *******************************************************//
	@Override
	public boolean onStart(final Map<String, String> args) {
		vtwBasicGUI = new GUI(this);
		vtwBasicGUI.setLocationRelativeTo(null);
		vtwBasicGUI.setVisible(true);
		try {
			new URL("http://www.scriptic.net/forum/index.php").openStream();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		while (!setup) {
			wait(800);
		}
		if (run) {
			antiban = new VTWPlankerAntiBan();
			t = new Thread(antiban);
			return true;
		} else {
			return false;
		}
	}

	// *******************************************************//
	// MAIN LOOP
	// *******************************************************//
	public int loop() {
		try { // SETUP
			if (isLoggedIn() && !startup) {
				log("Loading Grand Exchange Prices ...");
				logPrice = getMarketPriceByID(LOGID[selection]);
				log("Log Price: " + logPrice + "gp.");
				plankPrice = getMarketPriceByID(PLANKID[selection]);
				log("Plank Price: " + plankPrice + "gp.");
				startTime = System.currentTimeMillis();
				charge = 27 * millPrice[selection];
				startup = true;
				return random(50, 150);
			}
		} catch (final Exception ignored) {
		}

		try { // ANTI BAN
			if (!t.isAlive()) {
				t.start();
				log("AntiBan initialized!");
				return random(50, 150);
			}
		} catch (final Exception ignored) {
		}

		try { // ENERGY CHECK
			if (energyCheck()) {
				setRun(true);
				wait(random(1000, 1300));
				return random(50, 150);
			}
		} catch (final Exception ignored) {
		}

		try { // MUSICIAN CHECK
			if (restCheck() && atMusician() && useMusician) {
				final RSNPC musician = getNearestNPCByID(MUSICIANID);
				atNPC(musician, "Listen-to");
				wait(random(1000, 1300));
				while (getMyPlayer().getAnimation() != -1) {
					status = "Resting";
					wait(500);
					if (onEnergyCheck() > 94) {
						break;
					}
				}
				return random(50, 150);
			}
		} catch (final Exception ignored) {
		}

		try { // PLANK COUNT CHECK
			if (countCheck) {
				if (inventoryContains(PLANKID[selection])) {
					plankCount += getInventoryCount(PLANKID[selection]);
					countCheck = false;
				}
			}
		} catch (final Exception ignored) {
		}

		try { // MAIN SCRIPT
			runScript();
		} catch (final Exception ignored) {
		}

		return random(50, 150);
	}

	// *******************************************************//
	// MAIN SCRIPT
	// *******************************************************//
	private void runScript() {
		switch (getState()) {
			case bank:
				if (bank.getInterface().isValid()) {
					if (getInventoryCount() != 0) {
						bank.depositAllExcept(COINS);
						wait(random(300, 500));
						countCheck = true;
					}
					if (getInventoryCount(COINS) < charge
							|| bank.getCount(LOGID[selection]) < 27) {
						log("Insufficient resources, terminating script.");
						wait(random(500, 750));
						bank.close();
						stopScript();
					}
					if (bank.atItem(LOGID[selection], "Withdraw-All")) {
						wait(random(500, 750));
						failCount = 0;
						return;
					} else {
						failCount++;
						if (failCount >= 5) {
							stopScript();
						} else {
							return;
						}
					}
				} else {
					if (!bank.isOpen()) {
						final RSObject booth = getNearestObjectByID(BANKID);
						final RSTile stuck = new RSTile(3250, 3419);
						if (tileOnScreen(booth.getLocation())) {
							if (distanceTo(stuck) < 3) {
								atNPC(getNearestNPCByID(BANKERID), "Bank Banker");
								waitForIface(bank.getInterface(), 3000);
							} else {
								atObject(getNearestObjectByID(BANKID), "uickly");
								waitForIface(bank.getInterface(), 3000);
							}
						} else {
							turnToTile(booth.getLocation());
						}
					}
				}
				return;
			case walk:
				failCount = 0;
				if (needsMiller()) {
					if (distanceTo(getDestination()) < random(5, 7)) {
						walkVTWPath(randomizePath(bankToMillerPath, 2, 2), true);
					}
				}

				if (needsBank()) {
					if (distanceTo(getDestination()) < random(5, 7)) {
						walkVTWPath(randomizePath(bankToMillerPath, 2, 2), false);
					}
				}
				return;
			case plank:
				final int X[] = {random(145, 220), random(295, 380),
						random(145, 220), random(295, 380)};
				final int Y[] = {random(80, 140), random(80, 140),
						random(210, 270), random(210, 270)};
				final RSNPC miller = getNearestNPCByID(MILLERID);
				final RSInterface select = RSInterface.getInterface(403);
				if (select.isValid()) {
					if (failCount >= 3) {
						atNPC(miller, "Buy-plank");
						waitForIface(select, 3000);
						failCount = 0;
					}
					clickMouse(X[selection], Y[selection], false);
					wait(random(100, 350));
					atMenu("All");
					wait(random(800, 1000));
					failCount++;
				} else {
					if (tileOnScreen(miller.getLocation())) {
						atNPC(miller, "Buy-plank");
						waitForIface(select, 3000);
					} else {
						turnToTile(miller.getLocation());
					}
				}
		}
	}

	// *******************************************************//
	// OTHER METHODS
	// *******************************************************//
	private boolean walkVTWPath(final RSTile[] tilearray, final boolean forward) {
		int i;
		final int arrl = tilearray.length - 1;
		if (forward) {
			for (i = 0; i <= arrl; i++) {
				if (distanceTo(tilearray[i]) < random(6, 8)) {
					i++;
					break;
				}
			}
			if (i > arrl) {
				i = arrl;
			}
			if (distanceTo(tilearray[arrl]) < random(8, 10)) {
				walkTileMM(tilearray[arrl]);
				return true;
			}
			if (distanceTo(getDestination()) < random(6, 8)) {
				walkTileMM(tilearray[i]);
			}
		} else {
			for (i = arrl; i >= 0; i--) {
				if (distanceTo(tilearray[i]) < random(6, 8)) {
					i--;
					break;
				}
			}
			if (i < 0) {
				i = 0;
			}
			if (distanceTo(tilearray[0]) < random(8, 10)) {
				walkTileMM(tilearray[0]);
				return true;
			}
			if (distanceTo(getDestination()) < random(6, 8)) {
				walkTileMM(tilearray[i]);
			}
		}
		return false;
	}

	private int getMarketPriceByID(final int ID) {
		GEItemInfo i;
		int marketprice;

		i = grandExchange.loadItemInfo(ID);
		marketprice = i.getMarketPrice();

		return marketprice;
	}

	private int onEnergyCheck() {
		return Integer
				.parseInt(RSInterface.getChildInterface(750, 5).getText());
	}

	private boolean energyCheck() {
		try {
			if (onEnergyCheck() >= runEnergy && !isRunning()) {
				runEnergy = random(40, 65);
				return true;
			} else {
				return false;
			}
		} catch (final Exception e) {
			return false;
		}
	}

	private boolean restCheck() {
		return onEnergyCheck() < 20;
	}

	public boolean atBank() {
		final RSObject booth = getNearestObjectByID(BANKID);
		return booth != null && distanceTo(booth) <= 7;
	}

	public boolean atMill() {
		final RSNPC miller = getNearestNPCByID(MILLERID);
		return miller != null && distanceTo(miller) <= 7;
	}

	public boolean atMusician() {
		final RSNPC musician = getNearestNPCByID(MUSICIANID);
		return musician != null && distanceTo(musician) <= 7;
	}

	public boolean needsBank() {
		return getInventoryCount(LOGID[selection]) <= 0;
	}

	public boolean needsMiller() {
		return getInventoryCount(PLANKID[selection]) <= 0;
	}

	public State getState() {
		if (atBank() && needsBank()) {
			status = "Banking";
			antiban.stopThread = true;
			return State.bank;
		}
		if (atMill() && needsMiller()) {
			status = "Planking";
			antiban.stopThread = true;
			return State.plank;
		}
		status = "Walking";
		antiban.stopThread = false;
		return State.walk;
	}

	// *******************************************************//
	// ON FINISH
	// *******************************************************//
	@Override
	public void onFinish() {
		antiban.stopThread = true;
	}

	// *******************************************************//
	// ANTI BAN
	// *******************************************************//
	private class VTWPlankerAntiBan implements Runnable {
		private boolean stopThread;

		public void run() {
			final Random random = new Random();
			while (!stopThread) {
				try {
					if (random.nextInt(Math.abs(15)) == 0) {
						final char[] LR = new char[]{KeyEvent.VK_LEFT,
								KeyEvent.VK_RIGHT};
						final char[] UD = new char[]{KeyEvent.VK_DOWN,
								KeyEvent.VK_UP};
						final char[] LRUD = new char[]{KeyEvent.VK_LEFT,
								KeyEvent.VK_RIGHT, KeyEvent.VK_UP,
								KeyEvent.VK_UP};
						final int random2 = random.nextInt(Math.abs(2));
						final int random1 = random.nextInt(Math.abs(2));
						final int random4 = random.nextInt(Math.abs(4));

						if (random.nextInt(Math.abs(3)) == 0) {
							Bot.getInputManager().pressKey(LR[random1]);
							Thread.sleep(random.nextInt(Math.abs(400 - 100)));
							Bot.getInputManager().pressKey(UD[random2]);
							Thread.sleep(random.nextInt(Math.abs(600 - 300)));
							Bot.getInputManager().releaseKey(UD[random2]);
							Thread.sleep(random.nextInt(Math.abs(400 - 100)));
							Bot.getInputManager().releaseKey(LR[random1]);
						} else {
							Bot.getInputManager().pressKey(LRUD[random4]);
							if (random4 > 1) {
								Thread.sleep(random
										.nextInt(Math.abs(600 - 300)));
							} else {
								Thread.sleep(random
										.nextInt(Math.abs(900 - 500)));
							}
							Bot.getInputManager().releaseKey(LRUD[random4]);
						}
					} else {
						Thread.sleep(random.nextInt(Math.abs(2000 - 200)));
					}
				} catch (final Exception e) {
					System.out.println("AntiBan error detected!");
				}
			}
		}
	}

	// *******************************************************//
	// GUI
	// *******************************************************//
	public class GUI extends JFrame implements WindowListener {

		private static final long serialVersionUID = -5781125843266714028L;
		private JTabbedPane panelGUI;
		private JPanel settingsPANEL;
		private JLabel titleLabel;
		private JLabel nameLabel;
		private JSeparator separator1;
		private JSeparator separator2;
		private JSeparator separator3;
		private JButton startButton;
		private JPanel contactPANEL;
		private JLabel devLabel;
		private JLabel websiteLabel;
		private JButton checkVersionButton;
		private JLabel versionLabel;
		private JCheckBox restChecked;
		private JComboBox plankSelection;
		private JLabel versionCheckResult;
		VTWPlanker script;

		public GUI(final VTWPlanker scr) {
			script = scr;
			initComponents();
		}

		private void initComponents() {

			panelGUI = new JTabbedPane();
			settingsPANEL = new JPanel();
			contactPANEL = new JPanel();
			setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			addWindowListener(this);
			titleLabel = new JLabel();
			titleLabel.setFont(new Font("Calibri", 0, 11));
			titleLabel.setText("Versus The World");
			nameLabel = new JLabel();
			nameLabel.setFont(new Font("Calibri", 0, 36));
			nameLabel.setText("Planker");
			separator1 = new JSeparator();
			restChecked = new JCheckBox();
			restChecked.setText("Musician Rest");
			separator2 = new JSeparator();
			separator3 = new JSeparator();
			startButton = new JButton();
			startButton.setText("Start");
			startButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(final java.awt.event.ActionEvent evt) {
					startButtonActionPerformed();
				}
			});
			plankSelection = new JComboBox();
			plankSelection.setModel(new DefaultComboBoxModel(
					new String[]{"Normal Plank", "Oak Plank", "Teak Plank",
							"Mahogany Plank"}));
			final GroupLayout settingsPANELLayout = new GroupLayout(
					settingsPANEL);
			settingsPANEL.setLayout(settingsPANELLayout);
			settingsPANELLayout
					.setHorizontalGroup(settingsPANELLayout
							.createParallelGroup(
									javax.swing.GroupLayout.Alignment.LEADING)
							.addGroup(
							settingsPANELLayout
									.createSequentialGroup()
									.addGroup(
											settingsPANELLayout
													.createParallelGroup(
															javax.swing.GroupLayout.Alignment.LEADING)
													.addGroup(
															settingsPANELLayout
																	.createSequentialGroup()
																	.addContainerGap()
																	.addComponent(
																	separator1,
																	javax.swing.GroupLayout.DEFAULT_SIZE,
																	191,
																	Short.MAX_VALUE))
													.addGroup(
															settingsPANELLayout
																	.createSequentialGroup()
																	.addGap(
																			36,
																			36,
																			36)
																	.addComponent(
																	startButton,
																	javax.swing.GroupLayout.PREFERRED_SIZE,
																	137,
																	javax.swing.GroupLayout.PREFERRED_SIZE))
													.addGroup(
															settingsPANELLayout
																	.createSequentialGroup()
																	.addContainerGap()
																	.addComponent(
																	separator2,
																	javax.swing.GroupLayout.DEFAULT_SIZE,
																	191,
																	Short.MAX_VALUE))
													.addGroup(
															settingsPANELLayout
																	.createSequentialGroup()
																	.addGap(
																			39,
																			39,
																			39)
																	.addComponent(
																	plankSelection,
																	javax.swing.GroupLayout.PREFERRED_SIZE,
																	124,
																	javax.swing.GroupLayout.PREFERRED_SIZE))
													.addGroup(
															settingsPANELLayout
																	.createSequentialGroup()
																	.addGap(
																			60,
																			60,
																			60)
																	.addComponent(
																	restChecked))
													.addGroup(
															settingsPANELLayout
																	.createSequentialGroup()
																	.addGap(
																			33,
																			33,
																			33)
																	.addGroup(
																	settingsPANELLayout
																			.createParallelGroup(
																					javax.swing.GroupLayout.Alignment.LEADING)
																			.addGroup(
																					settingsPANELLayout
																							.createSequentialGroup()
																							.addGap(
																									2,
																									2,
																									2)
																							.addComponent(
																							nameLabel))
																			.addComponent(
																			titleLabel)))
													.addGroup(
													settingsPANELLayout
															.createSequentialGroup()
															.addContainerGap()
															.addComponent(
															separator3,
															javax.swing.GroupLayout.DEFAULT_SIZE,
															191,
															Short.MAX_VALUE)))
									.addContainerGap()));
			settingsPANELLayout
					.setVerticalGroup(settingsPANELLayout
							.createParallelGroup(
									javax.swing.GroupLayout.Alignment.LEADING)
							.addGroup(
							settingsPANELLayout
									.createSequentialGroup()
									.addContainerGap()
									.addComponent(titleLabel)
									.addGap(1, 1, 1)
									.addComponent(nameLabel)
									.addPreferredGap(
											javax.swing.LayoutStyle.ComponentPlacement.RELATED)
									.addComponent(
											separator1,
											javax.swing.GroupLayout.PREFERRED_SIZE,
											10,
											javax.swing.GroupLayout.PREFERRED_SIZE)
									.addGap(15, 15, 15)
									.addComponent(
											plankSelection,
											javax.swing.GroupLayout.PREFERRED_SIZE,
											javax.swing.GroupLayout.DEFAULT_SIZE,
											javax.swing.GroupLayout.PREFERRED_SIZE)
									.addGap(18, 18, 18)
									.addComponent(
											separator2,
											javax.swing.GroupLayout.PREFERRED_SIZE,
											10,
											javax.swing.GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(
											javax.swing.LayoutStyle.ComponentPlacement.RELATED)
									.addComponent(restChecked)
									.addGap(18, 18, 18)
									.addComponent(
											separator3,
											javax.swing.GroupLayout.PREFERRED_SIZE,
											10,
											javax.swing.GroupLayout.PREFERRED_SIZE)
									.addGap(18, 18, 18).addComponent(
									startButton)
									.addContainerGap(19,
									Short.MAX_VALUE)));
			panelGUI.addTab("Settings", settingsPANEL);
			devLabel = new JLabel();
			devLabel.setText("Developed by: Versus The World");
			websiteLabel = new JLabel();
			websiteLabel.setText("http://www.scriptic.net");
			versionLabel = new JLabel();
			versionLabel.setText("Version: " + script.properties.version());
			checkVersionButton = new JButton();
			checkVersionButton.setText("Check Version");
			checkVersionButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					checkVersionActionPerformed(evt);
				}
			});
			versionCheckResult = new JLabel();
			versionCheckResult.setText("   ");

			final GroupLayout contactPANELLayout = new GroupLayout(contactPANEL);
			contactPANEL.setLayout(contactPANELLayout);
			contactPANELLayout
					.setHorizontalGroup(contactPANELLayout
							.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addGroup(
							contactPANELLayout
									.createSequentialGroup()
									.addGroup(
											contactPANELLayout
													.createParallelGroup(
															GroupLayout.Alignment.LEADING)
													.addGroup(
															contactPANELLayout
																	.createSequentialGroup()
																	.addGap(
																			27,
																			27,
																			27)
																	.addComponent(
																	devLabel))
													.addGroup(
															contactPANELLayout
																	.createSequentialGroup()
																	.addGap(
																			43,
																			43,
																			43)
																	.addGroup(
																	contactPANELLayout
																			.createParallelGroup(
																					GroupLayout.Alignment.LEADING)
																			.addComponent(
																					websiteLabel)
																			.addComponent(
																			checkVersionButton,
																			GroupLayout.PREFERRED_SIZE,
																			119,
																			GroupLayout.PREFERRED_SIZE)))
													.addGroup(
													contactPANELLayout
															.createSequentialGroup()
															.addGap(
																	72,
																	72,
																	72)
															.addGroup(
															contactPANELLayout
																	.createParallelGroup(
																			GroupLayout.Alignment.LEADING)
																	.addComponent(
																			versionCheckResult)
																	.addComponent(
																	versionLabel))))
									.addContainerGap(27,
									Short.MAX_VALUE)));
			contactPANELLayout
					.setVerticalGroup(contactPANELLayout
							.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addGroup(
							contactPANELLayout
									.createSequentialGroup()
									.addGap(20, 20, 20)
									.addComponent(devLabel)
									.addPreferredGap(
											LayoutStyle.ComponentPlacement.UNRELATED)
									.addComponent(websiteLabel)
									.addPreferredGap(
											LayoutStyle.ComponentPlacement.RELATED,
											33, Short.MAX_VALUE)
									.addComponent(checkVersionButton)
									.addGap(18, 18, 18).addComponent(
									versionCheckResult).addGap(
									26, 26, 26).addComponent(
									versionLabel).addGap(23,
									23, 23)));
			panelGUI.addTab("Contact", contactPANEL);
			final GroupLayout layout = new GroupLayout(getContentPane());
			getContentPane().setLayout(layout);
			layout.setHorizontalGroup(layout.createParallelGroup(
					GroupLayout.Alignment.LEADING)
					.addComponent(panelGUI, GroupLayout.PREFERRED_SIZE, 216,
					GroupLayout.PREFERRED_SIZE));
			layout.setVerticalGroup(layout.createParallelGroup(
					GroupLayout.Alignment.LEADING)
					.addComponent(panelGUI, GroupLayout.PREFERRED_SIZE, 290,
					GroupLayout.PREFERRED_SIZE));
			pack();
		}

		private void checkVersionActionPerformed(java.awt.event.ActionEvent evt) {
			double ver;
			URLConnection url;
			BufferedReader in;
			try {
				url = new URL("http://www.scriptic.net/scripts/VTWPlankerVersion.txt").openConnection();
				in = new BufferedReader(new InputStreamReader(url.getInputStream()));
				ver = Double.parseDouble(in.readLine());
				if (ver > script.properties.version()) {
					versionCheckResult.setText("Outdated!");
					checkVersionButton.setEnabled(false);
					versionCheckResult.setEnabled(false);
				} else {
					versionCheckResult.setText("Up To Date");
					checkVersionButton.setEnabled(false);
					versionCheckResult.setEnabled(false);
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void startButtonActionPerformed() {
			script.selection = plankSelection.getSelectedIndex();
			if (restChecked.isSelected()) {
				script.useMusician = true;
			}
			script.setup = true;
			script.run = true;
			dispose();
		}

		public void windowClosing(final WindowEvent arg0) {
			script.setup = true;
			script.run = false;
			dispose();
		}

		public void windowActivated(final WindowEvent e) {
			toFront();
		}

		public void windowClosed(final WindowEvent e) {

		}

		public void windowDeactivated(final WindowEvent e) {

		}

		public void windowDeiconified(final WindowEvent e) {

		}

		public void windowIconified(final WindowEvent e) {

		}

		public void windowOpened(final WindowEvent e) {

		}
	}
}