import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import org.rsbot.bot.Bot;
import org.rsbot.bot.input.Mouse;
import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.script.Constants;
import org.rsbot.script.GEItemInfo;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;
import org.rsbot.util.GlobalConfiguration;
import org.rsbot.util.ScreenshotUtil;

@ScriptManifest(authors = { "Taha", "Jacmob", "SpeedWing" }, category = "Smithing", name = "Smart Smelter", version = 2.3, description = "<html><body style='font-family: Arial; margin: 10px;'><span style='color: #00AA00; font-weight: bold;'>Smart Smelter</span>&nbsp;<strong>Version:&nbsp;2.3</strong><br />Select your account and press OK to configure the script settings.</body></html>")
public class SmartSmelter extends Script implements PaintListener,
		ServerMessageListener {
	private enum State {
		BANK, SMELT, WALK_TO_BANK, WALK_TO_FURNACE, TOGGLE_RUN, WAIT, STOP, OPEN_DOOR
	}

	SmartSmelterGUI gui;
	SmartSmelterAntiBan antiban;
	Thread t;

	public boolean start, runScript, shutDown;
	public String bar;
	public RSTile[] toBank, toFurnace;
	public int primaryOreID, secondaryOreID,
			numberOfPrimaryOresNeededForSmelting,
			numberOfSecondaryOresNeededForSmelting,
			numberOfPrimaryOresNeededForWithdrawal, numberToSmelt,
			furnaceObjectID, primaryChild, copperOreID = 436, tinOreID = 438,
			ironOreID = 440, silverOreID = 442, coalOreID = 453,
			goldOreID = 444, mithrilOreID = 447, adamantOreID = 449,
			runeOreID = 451;
	public double expPerBar;
	public GEItemInfo profitPerBar;

	private long startTime;
	private final int oreSelectionInterface = 311;
	private int startLvl, startExp;
	private String state = "Loading";
	private int smelted;

	protected int getMouseSpeed() {
		return random(6, 11);
	}

	private State getState() {
		if (smelted >= numberToSmelt) {
			return State.STOP;
		} else if ((getEnergy() > 40 && random(0, 5) == 0 || getEnergy() > 80)
				&& !bank.isOpen() && !isRunning()
				&& !getInterface(oreSelectionInterface).isValid()) {
			return State.TOGGLE_RUN;
		} else if (getNearestObjectByID(5244) != null
				&& tileOnScreen(getNearestObjectByID(5244).getLocation())) {
			return State.OPEN_DOOR;
		} else if (getInventoryCount(primaryOreID) >= numberOfPrimaryOresNeededForSmelting
				&& getInventoryCount(secondaryOreID) >= numberOfSecondaryOresNeededForSmelting
				&& distanceTo(toBank[0]) > 3) {
			return State.WALK_TO_FURNACE;
		} else if (distanceTo(toFurnace[0]) > 5
				&& (getInventoryCount(primaryOreID) < numberOfPrimaryOresNeededForSmelting || getInventoryCount(secondaryOreID) < numberOfSecondaryOresNeededForSmelting)) {
			return State.WALK_TO_BANK;
		} else if (getInventoryCount(primaryOreID) < numberOfPrimaryOresNeededForSmelting
				|| getInventoryCount(secondaryOreID) < numberOfSecondaryOresNeededForSmelting) {
			return State.BANK;
		} else if (!isSmelting()) {
			return State.SMELT;
		} else {
			return State.WAIT;
		}
	}

	public boolean onStart(final Map<String, String> args) {
		gui = new SmartSmelterGUI(this);
		gui.setVisible(true);
		try {
			new URL("http://www.ipcounter.de/count_js.php?u=63163145")
					.openStream();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		while (!start) {
			wait(100);
		}
		if (runScript) {
			log("Smart Smelter initialized!");
			antiban = new SmartSmelterAntiBan();
			t = new Thread(antiban);
			startTime = System.currentTimeMillis();
			return true;
		} else {
			log("Smart Smelter exited!");
			return false;
		}
	}

	public int loop() {
		try {
			getMouseSpeed();
			setCameraAltitude(true);
			if (!t.isAlive()) {
				t.start();
				log("AntiBan initialized!");
			}
			switch (getState()) {
			case TOGGLE_RUN:
				state = "Enabling Run Mode";
				setRun(true);
				break;

			case BANK:
				state = "Banking";
				if (!bank.isOpen()) {
					bank.open();
					wait(random(500, 1000));
				} else {
					if (getInventoryCount(primaryOreID) > numberOfPrimaryOresNeededForWithdrawal
							|| getInventoryCount(secondaryOreID) > 28 - numberOfPrimaryOresNeededForWithdrawal) {
						if (getInventoryCount(primaryOreID)
								- numberOfPrimaryOresNeededForWithdrawal > 0) {
							bank
									.deposit(
											primaryOreID,
											getInventoryCount(primaryOreID)
													- numberOfPrimaryOresNeededForWithdrawal);
						}
						if (getInventoryCount(secondaryOreID)
								- (28 - numberOfPrimaryOresNeededForWithdrawal) > 0) {
							bank
									.deposit(
											secondaryOreID,
											getInventoryCount(secondaryOreID)
													- (28 - numberOfPrimaryOresNeededForWithdrawal));
						}
					}
					if (getInventoryCountExcept(primaryOreID, secondaryOreID) > 0) {
						bank.depositAll();
						wait(random(200, 400));
					}
					if (!bankContainsEnoughOres()) {
						log("Not enough ores!");
						bank.close();
						stopScript();
					}
					if (getInventoryCount(primaryOreID) < oresToWithdraw()
							&& bankContainsEnoughOres()) {
						bank.withdraw(primaryOreID, oresToWithdraw()
								- getInventoryCount(primaryOreID));
					}
					if (!bar.equals("Iron") && !bar.equals("Silver")
							&& !bar.equals("Gold")
							&& getInventoryCount(primaryOreID) > 0) {
						bank.withdraw(secondaryOreID, 0);
					}
					wait(random(200, 400));
				}
				break;

			case SMELT:
				state = "Smelting";
				final RSObject furnace = getNearestObjectByID(furnaceObjectID);
				if (furnace != null) {
					if (!tileOnScreen(furnace.getLocation())) {
						int t = 0;
						while (t < 6) {
							turnToTile(furnace.getLocation());
							if (tileOnScreen(furnace.getLocation())) {
								break;
							} else {
								t++;
							}
						}
						if (t > 5) {
							if (!walkPathMM(toFurnace)) {
								walkToClosestTile(toFurnace);
							}
						}
					}
					if (!getInterface(oreSelectionInterface).isValid()) {
						atObject(furnace, "Smelt");
					}
					if (getInterface(oreSelectionInterface).isValid()) {
						if (getInventoryCount(primaryOreID) <= 10
								&& getInventoryCount(primaryOreID) > 5) {
							atInterface(oreSelectionInterface, primaryChild,
									"Smelt 10 " + bar);
						} else if (getInventoryCount(primaryOreID) <= 5
								&& getInventoryCount(primaryOreID) > 1) {
							atInterface(oreSelectionInterface, primaryChild,
									"Smelt 5 " + bar);
						} else if (getInventoryCount(primaryOreID) == 1) {
							atInterface(oreSelectionInterface, primaryChild,
									"Smelt 1 " + bar);
						} else {
							atInterface(oreSelectionInterface, primaryChild,
									"Smelt X " + bar);
							wait(random(600, 800));
							String randomText = Integer
									.toString(random(28, 100));
							if (random(0, 15) == 0) {
								randomText = randomText + "k";
							}
							if (random(0, 15) == 0 && !randomText.contains("k")) {
								randomText = randomText + "m";
							}
							sendText(randomText, true);
						}
					}
				}
				break;

			case WALK_TO_BANK:
				state = "Walking to Bank";
				if (distanceTo(getDestination()) < random(5, 12)) {
					if (!walkPathMM(toBank)) {
						walkToClosestTile(toBank);
					}
				}

				break;

			case WALK_TO_FURNACE:
				state = "Walking to Furnace";
				if (distanceTo(getDestination()) < random(5, 12)) {
					if (!walkPathMM(toFurnace)) {
						walkToClosestTile(toFurnace);
					}
				}
				break;

			case OPEN_DOOR:
				state = "Opening Door";
				atTile(getNearestObjectByID(5244).getLocation(), "Open");
				return random(1200, 1400);

			case WAIT:
				state = "Waiting";
				break;

			case STOP:
				stopScript();
				break;
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return random(300, 400);
	}

	public void onFinish() {
		ScreenshotUtil.takeScreenshot(isLoggedIn());
		log("Gained: "
				+ (skills.getCurrentSkillLevel(Constants.STAT_SMITHING) - startLvl)
				+ " Smithing Levels");
		log.info("Profit: " + smelted * profitPerBar.getMarketPrice() + " GP");
		antiban.stopThread = true;
		shutDown();
	}

	private int oresToWithdraw() {
		return numberToSmelt - smelted > numberOfPrimaryOresNeededForWithdrawal ? numberOfPrimaryOresNeededForWithdrawal
				: numberToSmelt - smelted;
	}

	private boolean bankContainsEnoughOres() {
		if (bank.isOpen()) {
			if (bank.getCount(primaryOreID) + getInventoryCount(primaryOreID) >= numberOfPrimaryOresNeededForSmelting
					&& bank.getCount(secondaryOreID)
							+ getInventoryCount(secondaryOreID) >= numberOfSecondaryOresNeededForSmelting) {
				return true;
			} else {
				if (bank.searchItem("ore")) {
					if (bank.getCount(primaryOreID)
							+ getInventoryCount(primaryOreID) >= numberOfSecondaryOresNeededForSmelting
							&& bank.getCount(secondaryOreID)
									+ getInventoryCount(secondaryOreID) >= numberOfSecondaryOresNeededForSmelting) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean isSmelting() {
		if (getInventoryCount(primaryOreID) < numberOfPrimaryOresNeededForSmelting
				|| getInventoryCount(secondaryOreID) < numberOfSecondaryOresNeededForSmelting) {
			return false;
		}
		for (int i = 0; i < 5; i++) {
			if (getMyPlayer().getAnimation() == 3243) {
				return true;
			} else {
				wait(random(400, 500));
			}
		}
		return false;
	}

	public void shutDown() {
		if (shutDown) {
			try {
				Runtime
						.getRuntime()
						.exec(
								"shutdown -s -t 120 -c \"Smart Smelter automatic shutdown has initiliazed. To stop the shutdown, hold start and press R, then type in 'shutdown -a' and press OK.\"");
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void serverMessageRecieved(final ServerMessageEvent e) {
		if (e.getMessage().contains("You've just advanced")) {
			ScreenshotUtil.takeScreenshot(isLoggedIn());
			clickContinue();
		}
		if (e.getMessage().contains("You retrieve a bar of")) {
			smelted++;
		}
		if (e.getMessage().contains("magic of the Varrock armour")) {
			smelted++;
		}
		if (e.getMessage().contains("members' server to use this furnace")) {
			log("Use a F2P furnace!");
			stopScript();
		}
	}

	public void onRepaint(final Graphics g) {
		if (gui != null && !gui.isVisible()) {
			final Mouse m = Bot.getClient().getMouse();
			if (m.x >= 11 && m.x < 11 + 65 && m.y >= 310 && m.y < 310 + 20) {
				gui.setVisible(true);
			}
			g.setColor(Color.GREEN);
			g.fillRect(11, 310, 65, 20);
			g.setColor(Color.BLACK);
			g.drawString("Show GUI", 15, 310 + 15);
		}
		if (isLoggedIn()) {
			final long runTime = System.currentTimeMillis() - startTime;
			final int seconds = (int) (runTime / 1000 % 60);
			final int minutes = (int) (runTime / 1000 / 60) % 60;
			final int hours = (int) (runTime / 1000 / 60 / 60) % 60;

			final StringBuilder b = new StringBuilder();
			if (hours < 10) {
				b.append('0');
			}
			b.append(hours);
			b.append(':');
			if (minutes < 10) {
				b.append('0');
			}
			b.append(minutes);
			b.append(':');
			if (seconds < 10) {
				b.append('0');
			}
			b.append(seconds);

			if (startLvl <= 0 || startExp <= 0) {
				startLvl = skills.getCurrentSkillLevel(Constants.STAT_SMITHING);
				startExp = skills.getCurrentSkillExp(Constants.STAT_SMITHING);
			}

			final int x = 294;
			int y = 4;
			final int xl = 222;
			final int yl = 85;

			g.setColor(new Color(0, 0, 0, 120));
			g.fillRect(x, y, xl, yl);
			g.setColor(new Color(248, 237, 22));
			g.drawRect(x, y, xl, yl);

			g.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
			g.drawString(getClass().getAnnotation(ScriptManifest.class).name()
					+ " v"
					+ getClass().getAnnotation(ScriptManifest.class).version(),
					x + 10, y += 15);
			g
					.drawString(
							"Gained: "
									+ (skills.getCurrentSkillExp(STAT_SMITHING)
											- startExp < 1000 ? skills
											.getCurrentSkillExp(STAT_SMITHING)
											- startExp
											: Math
													.round((skills
															.getCurrentSkillExp(STAT_SMITHING) - startExp) * 10) / 10)
									+ " Exp"
									+ " || Exp/Hour: "
									+ (int) ((skills
											.getCurrentSkillExp(Constants.STAT_SMITHING) - startExp) * 3600000D / ((double) System
											.currentTimeMillis() - (double) startTime)),
							x + 10, y += 15);
			g.drawString("Smelted: " + smelted + " " + bar + " Bars", x + 10,
					y += 15);
			g.drawString("Time Running: " + b, x + 10, y += 15);
			g.drawString("Current State: ", x + 10, y += 15);
			g.setColor(Color.RED);
			g.drawString(state, x + 95, y);
		}
	}

	private class SmartSmelterGUI extends JFrame {
		private final File changeLogFile = new File(new File(
				GlobalConfiguration.Paths.getSettingsDirectory()),
				"SmartSmelterChangeLog.txt");
		private final File settingsFile = new File(new File(
				GlobalConfiguration.Paths.getSettingsDirectory()),
				"SmartSmelterSettings.txt");
		SmartSmelter script;
		private static final long serialVersionUID = 1L;

		// GEN-BEGIN:variables
		private JLabel label1;
		private JPanel buttonPanel;
		private JButton startButton;
		private JButton exitButton;
		private JTabbedPane mainTabbedPane;
		private JPanel panel1;
		private JLabel label14;
		private JComboBox locComboBox;
		private JLabel label3;
		private JComboBox barComboBox;
		private JLabel label4;
		private JTextField numberTextField;
		private JCheckBox shutDownCheckBox;
		private JEditorPane description;
		private JPanel panel2;
		private JLabel label6;
		private JLabel label2;
		private JLabel label7;
		private JLabel label5;
		private JScrollPane scrollPane1;
		private JEditorPane changeLogEditorPane;
		private JPanel panel3;
		private JLabel label8;
		private JLabel label9;
		private JLabel label11;
		private JLabel label10;
		private JLabel label12;
		private JButton threadButton;

		// GEN-END:variables
		private SmartSmelterGUI(final SmartSmelter scr) {
			script = scr;
			initComponents();
			description.setEditable(false);
			changeLogEditorPane.setEditable(false);
		}

		private void barComboBoxActionPerformed(final ActionEvent e) {
			updateDescription();
		}

		private void exitButtonActionPerformed(final ActionEvent e) {
			if (exitButton.getText().equals("Exit!")) {
				dispose();
				script.runScript = false;
				script.start = true;
			} else {
				script.stopScript(false);
				dispose();
			}
		}

		private void initComponents() {
			// GEN-BEGIN:initComponents
			label1 = new JLabel();
			buttonPanel = new JPanel();
			startButton = new JButton();
			exitButton = new JButton();
			mainTabbedPane = new JTabbedPane();
			panel1 = new JPanel();
			label14 = new JLabel();
			locComboBox = new JComboBox();
			label3 = new JLabel();
			barComboBox = new JComboBox();
			label4 = new JLabel();
			numberTextField = new JTextField();
			shutDownCheckBox = new JCheckBox();
			description = new JEditorPane();
			panel2 = new JPanel();
			label6 = new JLabel();
			label2 = new JLabel();
			label7 = new JLabel();
			label5 = new JLabel();
			scrollPane1 = new JScrollPane();
			changeLogEditorPane = new JEditorPane();
			panel3 = new JPanel();
			label8 = new JLabel();
			label9 = new JLabel();
			label11 = new JLabel();
			label10 = new JLabel();
			label12 = new JLabel();
			threadButton = new JButton();

			// ======== this ========
			setTitle("Smart Smelter Script Options");
			setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			final Container contentPane = getContentPane();
			contentPane.setLayout(new BorderLayout(0, 5));
			addWindowListener(new WindowAdapter() {
				public void windowClosing(final WindowEvent ev) {
					if (exitButton.getText().equals("Exit!")) {
						dispose();
						script.start = true;
					} else {
						setVisible(false);
					}
				}
			});

			// ---- label1 ----
			label1.setText("Smart Smelter Script Options");
			label1.setFont(new Font("Century Gothic", Font.PLAIN, 22));
			label1.setHorizontalAlignment(SwingConstants.CENTER);
			contentPane.add(label1, BorderLayout.NORTH);

			// ======== buttonPanel ========
			{
				buttonPanel.setLayout(new GridBagLayout());
				((GridBagLayout) buttonPanel.getLayout()).columnWidths = new int[] {
						90, 85 };
				((GridBagLayout) buttonPanel.getLayout()).rowHeights = new int[] {
						3, 0 };
				((GridBagLayout) buttonPanel.getLayout()).rowWeights = new double[] {
						1.0, 1.0E-4 };

				// ---- startButton ----
				startButton.setText("Start!");
				startButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
				startButton.setFocusable(false);
				startButton.addActionListener(new ActionListener() {
					public void actionPerformed(final ActionEvent e) {
						startButtonActionPerformed(e);
					}
				});
				buttonPanel.add(startButton, new GridBagConstraints(0, 0, 1, 1,
						0.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

				// ---- exitButton ----
				exitButton.setText("Exit!");
				exitButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
				exitButton.setFocusable(false);
				exitButton.addActionListener(new ActionListener() {
					public void actionPerformed(final ActionEvent e) {
						exitButtonActionPerformed(e);
					}
				});
				buttonPanel.add(exitButton, new GridBagConstraints(1, 0, 1, 1,
						0.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			}
			contentPane.add(buttonPanel, BorderLayout.SOUTH);

			// ======== mainTabbedPane ========
			{
				mainTabbedPane.setFocusable(false);

				// ======== panel1 ========
				{
					panel1.setFocusable(false);
					panel1.setLayout(new GridBagLayout());
					((GridBagLayout) panel1.getLayout()).columnWidths = new int[] {
							47, 139, 35, 33, 0 };
					((GridBagLayout) panel1.getLayout()).rowHeights = new int[] {
							18, 10, 18, 16, 34, 0, 0 };
					((GridBagLayout) panel1.getLayout()).columnWeights = new double[] {
							0.0, 0.0, 0.0, 1.0, 1.0E-4 };
					((GridBagLayout) panel1.getLayout()).rowWeights = new double[] {
							0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0E-4 };

					// ---- label14 ----
					label14.setText("Location:");
					label14.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
					label14.setHorizontalAlignment(SwingConstants.RIGHT);
					panel1.add(label14, new GridBagConstraints(0, 0, 2, 1, 0.0,
							0.0, GridBagConstraints.CENTER,
							GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0,
							0));

					// ---- locComboBox ----
					locComboBox.setFont(new Font("Comic Sans MS", Font.PLAIN,
							12));
					locComboBox.setFocusable(false);
					locComboBox.setModel(new DefaultComboBoxModel(new String[] {
							"Falador", "Al Kharid", "Edgeville",
							"Port Phasmatys" }));
					panel1.add(locComboBox, new GridBagConstraints(3, 0, 1, 1,
							0.0, 0.0, GridBagConstraints.CENTER,
							GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0,
							0));

					// ---- label3 ----
					label3.setHorizontalAlignment(SwingConstants.RIGHT);
					label3.setText("Bar:");
					label3.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
					panel1.add(label3, new GridBagConstraints(0, 1, 2, 1, 0.0,
							0.0, GridBagConstraints.CENTER,
							GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0,
							0));

					// ---- barComboBox ----
					barComboBox.setFocusable(false);
					barComboBox.setModel(new DefaultComboBoxModel(new String[] {
							"Bronze", "Iron", "Silver", "Steel", "Gold",
							"Mithril", "Adamant", "Rune" }));
					barComboBox.setFont(new Font("Comic Sans MS", Font.PLAIN,
							12));
					barComboBox.addActionListener(new ActionListener() {
						public void actionPerformed(final ActionEvent e) {
							barComboBoxActionPerformed(e);
						}
					});
					panel1.add(barComboBox, new GridBagConstraints(3, 1, 1, 1,
							0.0, 0.0, GridBagConstraints.CENTER,
							GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0,
							0));

					// ---- label4 ----
					label4.setText("Numbers of bars to smelt:");
					label4.setHorizontalAlignment(SwingConstants.RIGHT);
					label4.setFont(new Font("Comic Sans MS", Font.PLAIN, 14));
					panel1.add(label4, new GridBagConstraints(0, 2, 2, 1, 0.0,
							0.0, GridBagConstraints.CENTER,
							GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0,
							0));

					// ---- numberTextField ----
					numberTextField.setFont(new Font("Comic Sans MS",
							Font.PLAIN, 12));
					numberTextField.setText("100");
					numberTextField.addKeyListener(new KeyAdapter() {
						public void keyTyped(final KeyEvent e) {
							numberTextFieldKeyTyped(e);
						}

						public void keyReleased(final KeyEvent e) {
							numberTextFieldKeyReleased(e);
						}
					});
					panel1.add(numberTextField, new GridBagConstraints(3, 2, 1,
							1, 0.0, 0.0, GridBagConstraints.CENTER,
							GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0,
							0));

					// ---- shutDownCheckBox ----
					shutDownCheckBox
							.setText("Turn off the computer when finished");
					shutDownCheckBox.setFont(new Font("Comic Sans MS",
							Font.PLAIN, 12));
					shutDownCheckBox.setFocusable(false);
					shutDownCheckBox.addActionListener(new ActionListener() {
						public void actionPerformed(final ActionEvent e) {
							shutDownCheckBoxActionPerformed(e);
						}
					});
					panel1.add(shutDownCheckBox, new GridBagConstraints(1, 4,
							3, 1, 0.0, 0.0, GridBagConstraints.CENTER,
							GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0,
							0));

					// ---- description ----
					description.setBorder(null);
					description.setBackground(new Color(212, 208, 200));
					description.setFont(new Font("Comic Sans MS", Font.PLAIN,
							12));
					panel1.add(description, new GridBagConstraints(0, 5, 4, 1,
							0.0, 0.0, GridBagConstraints.CENTER,
							GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0,
							0));
				}
				mainTabbedPane.addTab("Script Settings", panel1);

				// ======== panel2 ========
				{
					panel2.setFocusable(false);
					panel2.setLayout(new GridBagLayout());
					((GridBagLayout) panel2.getLayout()).columnWidths = new int[] {
							152, 0, 0 };
					((GridBagLayout) panel2.getLayout()).rowHeights = new int[] {
							25, 25, 0, 0 };
					((GridBagLayout) panel2.getLayout()).columnWeights = new double[] {
							0.0, 1.0, 1.0E-4 };
					((GridBagLayout) panel2.getLayout()).rowWeights = new double[] {
							0.0, 0.0, 1.0, 1.0E-4 };

					// ---- label6 ----
					label6.setText("Your script version:");
					label6.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
					panel2.add(label6, new GridBagConstraints(0, 0, 1, 1, 0.0,
							0.0, GridBagConstraints.CENTER,
							GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0,
							0));

					// ---- label2 ----
					label2.setText("Please wait...");
					label2.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
					panel2.add(label2, new GridBagConstraints(1, 0, 1, 1, 0.0,
							0.0, GridBagConstraints.CENTER,
							GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0,
							0));

					// ---- label7 ----
					label7.setText("Latest script version:");
					label7.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
					panel2.add(label7, new GridBagConstraints(0, 1, 1, 1, 0.0,
							0.0, GridBagConstraints.CENTER,
							GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0,
							0));

					// ---- label5 ----
					label5.setText("Please wait...");
					label5.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
					panel2.add(label5, new GridBagConstraints(1, 1, 1, 1, 0.0,
							0.0, GridBagConstraints.CENTER,
							GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0,
							0));

					// ======== scrollPane1 ========
					{

						// ---- changeLogEditorPane ----
						changeLogEditorPane
								.setText("Please wait as the changelog is loading...");
						changeLogEditorPane.setFont(new Font("Comic Sans MS",
								Font.PLAIN, 12));
						scrollPane1.setViewportView(changeLogEditorPane);
					}
					panel2.add(scrollPane1, new GridBagConstraints(0, 2, 2, 1,
							0.0, 0.0, GridBagConstraints.CENTER,
							GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0,
							0));
				}
				mainTabbedPane.addTab("Version Info", panel2);

				// ======== panel3 ========
				{
					panel3.setFocusable(false);
					panel3.setLayout(new GridBagLayout());
					((GridBagLayout) panel3.getLayout()).columnWidths = new int[] {
							123, 42, 69, 19, 0 };
					((GridBagLayout) panel3.getLayout()).rowHeights = new int[] {
							33, 25, 25, 36, 0, 0, 0 };
					((GridBagLayout) panel3.getLayout()).columnWeights = new double[] {
							0.0, 0.0, 0.0, 1.0, 1.0E-4 };
					((GridBagLayout) panel3.getLayout()).rowWeights = new double[] {
							0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0E-4 };

					// ---- label8 ----
					label8.setText("Credits go to...");
					label8
							.setFont(new Font("Times New Roman", Font.ITALIC,
									28));
					label8.setHorizontalAlignment(SwingConstants.CENTER);
					panel3.add(label8, new GridBagConstraints(0, 0, 3, 1, 0.0,
							0.0, GridBagConstraints.CENTER,
							GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0,
							0));

					// ---- label9 ----
					label9.setText("Taha");
					label9.setHorizontalAlignment(SwingConstants.RIGHT);
					label9.setFont(new Font("Comic Sans MS", Font.BOLD, 12));
					panel3.add(label9, new GridBagConstraints(0, 1, 1, 1, 0.0,
							0.0, GridBagConstraints.CENTER,
							GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0,
							0));

					// ---- label11 ----
					label11.setText("Programming the script");
					label11.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
					panel3.add(label11, new GridBagConstraints(2, 1, 2, 1, 0.0,
							0.0, GridBagConstraints.CENTER,
							GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0,
							0));

					// ---- label10 ----
					label10.setText("Jacmob");
					label10.setHorizontalAlignment(SwingConstants.RIGHT);
					label10.setFont(new Font("Comic Sans MS", Font.BOLD, 12));
					panel3.add(label10, new GridBagConstraints(0, 2, 1, 1, 0.0,
							0.0, GridBagConstraints.CENTER,
							GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0,
							0));

					// ---- label12 ----
					label12.setText("Help with the GUI");
					label12.setFont(new Font("Comic Sans MS", Font.PLAIN, 12));
					panel3.add(label12, new GridBagConstraints(2, 2, 2, 1, 0.0,
							0.0, GridBagConstraints.CENTER,
							GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0,
							0));

					// ---- threadButton ----
					threadButton.setText("RSBot Script Thread");
					threadButton.setFocusable(false);
					threadButton.addActionListener(new ActionListener() {
						public void actionPerformed(final ActionEvent e) {
							threadButtonActionPerformed(e);
						}
					});
					panel3.add(threadButton, new GridBagConstraints(3, 4, 1, 1,
							0.0, 0.0, GridBagConstraints.CENTER,
							GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0,
							0));
				}
				mainTabbedPane.addTab("Credits", panel3);

			}
			contentPane.add(mainTabbedPane, BorderLayout.CENTER);
			setSize(400, 300);
			setLocationRelativeTo(getOwner());
			// FINAL TOUCHES
			try {
				settingsFile.createNewFile();
				BufferedReader in;
				in = new BufferedReader(new FileReader(settingsFile));
				String line;
				String[] opts = {};
				while ((line = in.readLine()) != null) {
					if (line.contains(":")) {
						opts = line.split(":");
					}
				}
				in.close();
				if (opts.length == 4) {
					locComboBox.setSelectedItem(opts[0]);
					barComboBox.setSelectedItem(opts[1]);
					numberTextField.setText(opts[2]);
					if (opts[3].equals("true")) {
						shutDownCheckBox.setSelected(true);
					}
				}
			} catch (final IOException ignored) {
			}
			updateDescription();
			label2.setText(Double.toString(script.getClass().getAnnotation(
					ScriptManifest.class).version()));
			try {
				final URL url = new URL(
						"http://www.itaha.com/rsbot/SmartSmelterVersion.txt");
				final BufferedReader br = new BufferedReader(
						new InputStreamReader(new BufferedInputStream(url
								.openConnection().getInputStream())));
				final double ver = Double.parseDouble(br.readLine().trim());
				br.close();
				label5.setText(Double.toString(ver));
			} catch (final Exception e) {
				label5.setText("0.0");
			}
			if (Double.parseDouble(label5.getText()) > Double
					.parseDouble(label2.getText())) {
				label2.setForeground(Color.red);
				label5.setForeground(Color.red);
				System.out.println("There is a new update available!");
			} else {
				label2.setForeground(Color.blue);
				label5.setForeground(Color.blue);
			}
			final Thread change = new Thread(new Runnable() {
				public void run() {
					try {
						changeLogFile.createNewFile();
						final BufferedReader in = new BufferedReader(
								new InputStreamReader(
										new URL(
												"http://www.itaha.com/rsbot/SmartSmelterChangeLog.txt")
												.openStream()));
						final BufferedWriter out = new BufferedWriter(
								new FileWriter(changeLogFile));
						String temp;
						while ((temp = in.readLine()) != null) {
							out.append(temp);
							out.newLine();
						}
						in.close();
						out.close();
					} catch (final Exception e) {
						System.out
								.print("Unable to retrieve latest changelog.");
					}
					try {
						changeLogEditorPane.read(new BufferedReader(
								new FileReader(changeLogFile)), changeLogFile);
					} catch (final FileNotFoundException e) {
						System.out.println("Changelog file not found.");
					} catch (final IOException e) {
						System.out.println("Unable to open changelog.");
					}
				}
			});
			change.start();
			// GEN-END:initComponents
		}

		private void numberTextFieldKeyTyped(final KeyEvent e) {
			if (e.getKeyChar() != '0' && e.getKeyChar() != '1'
					&& e.getKeyChar() != '2' && e.getKeyChar() != '3'
					&& e.getKeyChar() != '4' && e.getKeyChar() != '5'
					&& e.getKeyChar() != '6' && e.getKeyChar() != '7'
					&& e.getKeyChar() != '8' && e.getKeyChar() != '9'
					|| numberTextField.getText().length() >= 6) {
				e.consume();
			}
		}

		private void numberTextFieldKeyReleased(final KeyEvent e) {
			updateDescription();
		}

		private void shutDownCheckBoxActionPerformed(final ActionEvent e) {
			updateDescription();
		}

		private void startButtonActionPerformed(final ActionEvent e) {
			setVisible(false);
			script.bar = barComboBox.getSelectedItem().toString();
			script.numberToSmelt = Integer.parseInt(numberTextField.getText());
			script.shutDown = shutDownCheckBox.isSelected();
			if (locComboBox.getSelectedItem().toString().equals("Falador")) {
				script.toFurnace = new RSTile[] { new RSTile(2946, 3369),
						new RSTile(2951, 3378), new RSTile(2960, 3380),
						new RSTile(2970, 3379), new RSTile(2974, 3369) };
				script.toBank = script.reversePath(script.toFurnace);
				script.furnaceObjectID = 11666;
			}
			if (locComboBox.getSelectedItem().toString().equals("Al Kharid")) {
				script.toFurnace = new RSTile[] { new RSTile(3271, 3167),
						new RSTile(3278, 3175), new RSTile(3277, 3185) };
				script.toBank = script.reversePath(script.toFurnace);
				script.furnaceObjectID = 11666;
			}
			if (locComboBox.getSelectedItem().toString().equals("Edgeville")) {
				script.toFurnace = new RSTile[] { new RSTile(3097, 3496),
						new RSTile(3108, 3500) };
				script.toBank = script.reversePath(script.toFurnace);
				script.furnaceObjectID = 26814;
			}
			if (locComboBox.getSelectedItem().toString().equals(
					"Port Phasmatys")) {
				script.toFurnace = new RSTile[] { new RSTile(3689, 3473),
						new RSTile(3684, 3476), new RSTile(3686, 3479) };
				script.toBank = script.reversePath(script.toFurnace);
				script.furnaceObjectID = 11666;
			}
			if (script.bar.equals("Bronze")) {
				script.primaryOreID = script.copperOreID;
				script.secondaryOreID = script.tinOreID;
				script.numberOfPrimaryOresNeededForSmelting = 1;
				script.numberOfSecondaryOresNeededForSmelting = 1;
				script.numberOfPrimaryOresNeededForWithdrawal = 14;
				script.primaryChild = 3;
				script.expPerBar = 6.2;
				script.profitPerBar = script.grandExchange.loadItemInfo(2349);
			}
			if (script.bar.equals("Iron")) {
				script.primaryOreID = script.ironOreID;
				script.numberOfPrimaryOresNeededForSmelting = 1;
				script.numberOfPrimaryOresNeededForWithdrawal = 28;
				script.primaryChild = 5;
				script.expPerBar = 12.5;
				script.profitPerBar = script.grandExchange.loadItemInfo(2351);
			}
			if (script.bar.equals("Silver")) {
				script.primaryOreID = script.silverOreID;
				script.numberOfPrimaryOresNeededForSmelting = 1;
				script.numberOfPrimaryOresNeededForWithdrawal = 28;
				script.primaryChild = 6;
				script.expPerBar = 13.7;
				script.profitPerBar = script.grandExchange.loadItemInfo(2355);
			}
			if (script.bar.equals("Steel")) {
				script.primaryOreID = script.ironOreID;
				script.secondaryOreID = script.coalOreID;
				script.numberOfPrimaryOresNeededForSmelting = 1;
				script.numberOfSecondaryOresNeededForSmelting = 2;
				script.numberOfPrimaryOresNeededForWithdrawal = 10;
				script.primaryChild = 7;
				script.expPerBar = 17.5;
				script.profitPerBar = script.grandExchange.loadItemInfo(2353);
			}
			if (script.bar.equals("Gold")) {
				script.primaryOreID = script.goldOreID;
				script.numberOfPrimaryOresNeededForSmelting = 1;
				script.numberOfPrimaryOresNeededForWithdrawal = 28;
				script.primaryChild = 8;
				script.expPerBar = 22.5;
				script.profitPerBar = script.grandExchange.loadItemInfo(2357);
			}
			if (script.bar.equals("Mithril")) {
				script.primaryOreID = script.mithrilOreID;
				script.secondaryOreID = script.coalOreID;
				script.numberOfPrimaryOresNeededForSmelting = 1;
				script.numberOfSecondaryOresNeededForSmelting = 4;
				script.numberOfPrimaryOresNeededForWithdrawal = 5;
				script.primaryChild = 9;
				script.expPerBar = 30;
				script.profitPerBar = script.grandExchange.loadItemInfo(2359);
			}
			if (script.bar.equals("Adamant")) {
				script.primaryOreID = script.adamantOreID;
				script.secondaryOreID = script.coalOreID;
				script.numberOfPrimaryOresNeededForSmelting = 1;
				script.numberOfSecondaryOresNeededForSmelting = 6;
				script.numberOfPrimaryOresNeededForWithdrawal = 4;
				script.primaryChild = 10;
				script.expPerBar = 37.5;
				script.profitPerBar = script.grandExchange.loadItemInfo(2361);
			}
			if (script.bar.equals("Rune")) {
				script.primaryOreID = script.runeOreID;
				script.secondaryOreID = script.coalOreID;
				script.numberOfPrimaryOresNeededForSmelting = 1;
				script.numberOfSecondaryOresNeededForSmelting = 8;
				script.numberOfPrimaryOresNeededForWithdrawal = 3;
				script.primaryChild = 11;
				script.expPerBar = 50;
				script.profitPerBar = script.grandExchange.loadItemInfo(2363);
			}
			try {
				final BufferedWriter out = new BufferedWriter(new FileWriter(
						settingsFile));
				out.write(locComboBox.getSelectedItem().toString() + ":"
						+ barComboBox.getSelectedItem().toString() + ":"
						+ Integer.parseInt(numberTextField.getText()) + ":"
						+ (shutDownCheckBox.isSelected() ? "true" : "false"));
				out.close();
			} catch (final Exception z) {
				z.printStackTrace();
			}

			if (startButton.getText().equals("Apply!")) {
				return;
			}

			startButton.setText("Apply!");
			exitButton.setText("Stop Script!");
			buttonPanel.revalidate();
			script.start = true;
			script.runScript = true;
		}

		private void threadButtonActionPerformed(final ActionEvent e) {
			final String URL = "http://www.rsbot.org/vb/showthread.php?p=1754115";
			final java.awt.Desktop browser = java.awt.Desktop.getDesktop();
			java.net.URI location = null;
			try {
				location = new java.net.URI(URL);
			} catch (final URISyntaxException a) {
				a.printStackTrace();
			}
			try {
				browser.browse(location);
			} catch (final IOException c) {
				c.printStackTrace();
			}
		}

		private void updateDescription() {
			final String loc = locComboBox.getSelectedItem().toString();
			if (shutDownCheckBox.isSelected()) {
				description
						.setText("You will be smelting "
								+ numberTextField.getText()
								+ " "
								+ barComboBox.getSelectedItem().toString()
										.toLowerCase()
								+ " bars at "
								+ loc
								+ ". Your computer will shutdown itself when the task is finished.");
			} else {
				description.setText("You will be smelting "
						+ numberTextField.getText()
						+ " "
						+ barComboBox.getSelectedItem().toString()
								.toLowerCase() + " bars at " + loc + ".");
			}
		}
	}

	private class SmartSmelterAntiBan implements Runnable {
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

}