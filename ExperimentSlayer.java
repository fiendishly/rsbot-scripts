import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.rsbot.bot.Bot;
import org.rsbot.bot.input.Mouse;
import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Constants;
import org.rsbot.script.Methods;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.Skills;
import org.rsbot.script.wrappers.RSCharacter;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSItemTile;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSPlayer;
import org.rsbot.script.wrappers.RSTile;
import org.rsbot.util.GlobalConfiguration;
import org.rsbot.util.ScreenshotUtil;

@ScriptManifest(authors = {"Pervy Shuya"}, category = "Combat", name = "ExperimentSlayer Pro", version = 5.4, description = "<style type='text/css'>body {background:url('http://img339.imageshack.us/img339/6561/11tf6.gif') no-repeat}</style><html><head><center><head></td><td><center>"
		+ "<html><head>"
		+ "</head><body>"
		+ "<center>"
		+ "<b><font size=\"6\" color=\"red\">"
		+ "Experiment Slayer Pro"
		+ "</font></b><br>"
		+ "<font size=\"3\" color=\"white\">Please start at Experiments, with armour.<br></font>"
		+ "<body style='font-family: Calibri; color:white; padding: 0px; text-align: center; background-color: black;'>"
		+ "</center>"
		+ "<center><h2>All settings can be set on the GUI, select your character and Start!</h2></center></td></tr><tr><td colspan=\"4\"><hr></td></tr>"
		+ "<b>Features:</b><br>"
		+ "<tr><td> -level to logout support"
		+ "<tr><td><b>All New Banking + walking</b>"
		+ "<tr><td> Saves screenshot upon ending script"
		+ "<tr><td> Added garrent new awesome paint along with pray support"
		+ "<tr><td>New emergency teleport if Hp drops to 5 or below!"
		+ "<tr><td><b>System Update Detection!</b>"
		+ "<tr><td><b>New built-in AntiBan woot woot!</b>"
		+ "<tr><td><b> New intergrated Quick chat responder =D</b>"
		+ "<tr><td> -Script more flawless :D"
		+ "<tr><td> -Eats and logsout if no food in bag!"
		+ "<tr><td> -Added run control and the special attack! woop woop =)"
		+ "<tr><td> -Super smooth Npc attacking method"
		+ "<tr><td> -100% supports range!"
		+ "<tr><td> -Supports B2p now :D"
		+ "<tr><td> -Brand new updater! autoupdates itself asking for your permission</td></tr>"
		+ "<br>"
		+ "</table>"
		+ "<tr><td colspan=2><b><font size=\"3\" color=\"FF6600\">&nbsp&nbsp Thanks From Garrent for his new awesome paint design <3<3 and Vavenger1989 for the pickup ranging method, Credits to Taha for the awesome script update with the new gui and cleaner script code, thanks for Austin  for helping me with the bug of bot standing around not searching the npc's. Many Thanks to Myr for the speed and flawless changes in the attacking and searching of npc, (99% flawless) and iscream for the QC methods cheers buddies ;)</font></b></td></tr></table>"
		+ "</center>" + "</body></html>")
public class ExperimentSlayer extends Script implements PaintListener,
		ServerMessageListener {
	final ScriptManifest properties = getClass().getAnnotation(
			ScriptManifest.class);
	final GarrettsPaint thePainter = new GarrettsPaint();
	ExperimentSlayerGUI gui;
	ExperimentSlayerChatResponder experimentThread;
	Thread t;

	String LOC, LOG, lastMessage = null;
	String[] skillz = {"Attack", "attack", "ATT", "Att", "att", "Defence",
			"defence", "Defense", "defense", "DEF", "Def", "def", "Strength",
			"strength", "STR", "Str", "str", "Hitpoints", "hitpoints", "HP",
			"Hp", "hp", "Hits", "hits", "Range", "range", "Ranged", "ranged",
			"Prayer", "prayer", "Pray", "pray", "prey", "Magic", "magic",
			"Mage", "mage", "Cooking", "cooking", "Cook", "cook",
			"Woodcutting", "woodcutting", "WC", "Wc", "wc", "Fletching",
			"fletching", "Fletch", "fletch", "Fishing", "fishing", "Fish",
			"fish", "Firemaking", "firemaking", "Firemake", "firemake", "FM",
			"Fm", "fm", "Crafting", "crafting", "Craft", "craft", "Smithing",
			"smithing", "Smith", "smith", "Mining", "mining", "Mine", "mine",
			"Herblore", "herblore", "Herb", "herb", "Agility", "agility",
			"Agil", "agil", "Thieving", "thieving", "Theif", "thief", "Slayer",
			"slayer", "Slay", "slay", "Runecrafting", "runecrafting",
			"Runecraft", "runecraft", "RC", "Rc", "rc", "Farming", "farming",
			"Farm", "farm", "Hunting", "hunting", "Hunter", "hunter",
			"Construction", "construction", "constructing", "constructing",
			"Summoning", "summoning", "Summon", "summon"};

	final int[] Potions = {2440, 157, 159, 161, 2442, 163, 165, 167, 2436,
			145, 147, 149, 113, 115, 117, 119, 2432, 133, 135, 137, 2428, 121,
			123, 125, 2444, 169, 171, 173};
	int[] superStrength = {2440, 157, 159, 161};
	int[] superDefense = {2442, 163, 165, 167};
	int[] superAttack = {2436, 145, 147, 149};
	int[] normalStrength = {113, 115, 117, 119};
	int[] normalDefense = {2432, 133, 135, 137};
	int[] normalAttack = {2428, 121, 123, 125};
	int[] normalRange = {2444, 169, 171, 173};
	String[] potionsToDrink = {"Super Attack", "Super Strength",
			"Super Defense", "Normal Range", "Normal Attack",
			"Normal Strength", "Normal Defense"};

	int[] tabs = {TAB_ATTACK, TAB_CLAN, TAB_IGNORE, TAB_FRIENDS, TAB_OPTIONS,
			TAB_QUESTS, TAB_MAGIC, TAB_MUSIC, TAB_PRAYER, TAB_EQUIPMENT,
			INTERFACE_TAB_EMOTES};
	int maxYTab = 52, maxXTab = 64, levelToGo = 0,
			skillToCheck = Constants.STAT_HITPOINTS;
	int[] stats = {STAT_ATTACK, STAT_DEFENSE, STAT_STRENGTH, STAT_HITPOINTS,
			STAT_RANGE, STAT_PRAYER, STAT_MAGIC, STAT_COOKING,
			STAT_WOODCUTTING, STAT_FLETCHING, STAT_FISHING, STAT_FIREMAKING,
			STAT_CRAFTING, STAT_SMITHING, STAT_MINING, STAT_HERBLORE,
			STAT_AGILITY, STAT_THIEVING, STAT_SLAYER, STAT_FARMING,
			STAT_RUNECRAFTING, STAT_HUNTER, STAT_CONSTRUCTION, STAT_SUMMONING};

	private int randomTime = random(210000, 720000);
	private long lastCheck = System.currentTimeMillis();

	int autoRandom(int min, int max) {
		return min + (int) (java.lang.Math.random() * (max - min));
	}

	private final int[] experimentID = {1678, 1677};
	private int[] foodID = {1895, 1893, 1891, 4293, 2142, 291, 2140, 3228,
			9980, 7223, 6297, 6293, 6295, 6299, 7521, 9988, 7228, 2878, 7568,
			2343, 1861, 13433, 315, 325, 319, 3144, 347, 355, 333, 339, 351,
			329, 3381, 361, 10136, 5003, 379, 365, 373, 7946, 385, 397, 391,
			3369, 3371, 3373, 2309, 2325, 2333, 2327, 2331, 2323, 2335, 7178,
			7180, 7188, 7190, 7198, 7200, 7208, 7210, 7218, 7220, 2003, 2011,
			2289, 2291, 2293, 2295, 2297, 2299, 2301, 2303, 1891, 1893, 1895,
			1897, 1899, 1901, 7072, 7062, 7078, 7064, 7084, 7082, 7066, 7068,
			1942, 6701, 6703, 7054, 6705, 7056, 7060, 2130, 1985, 1993, 1989,
			1978, 5763, 5765, 1913, 5747, 1905, 5739, 1909, 5743, 1907, 1911,
			5745, 2955, 5749, 5751, 5753, 5755, 5757, 5759, 5761, 2084, 2034,
			2048, 2036, 2217, 2213, 2205, 2209, 2054, 2040, 2080, 2277, 2225,
			2255, 2221, 2253, 2219, 2281, 2227, 2223, 2191, 2233, 2092, 2032,
			2074, 2030, 2281, 2235, 2064, 2028, 2187, 2185, 2229, 6883, 1971,
			4608, 1883, 1885}, teleTabIDs = {8007, 8008, 8009, 8010, 8011}, /*
			 * Tele
			 * tabs
			 */
			peach = {6883, 1971, 1969}, /* Peach */
			tabID = {8015, 8015}, /* B2P tabs */
			bankBooths = {24914}, Worldz = new int[]{12, 15, 18, 22, 23, 24, 27,
			28, 31, 36, 39, 42, 44, 45, 46, 48, 51, 52, 53, 54, 56, 58, 59, 60,
			64, 66, 67, 68, 69, 70, 71, 76, 77, 78, 79, 82, 83, 84, 88, 89, 91,
			92, 97, 98, 99, 100, 103, 104, 110, 111, 112, 114, 115, 116, 117,
			121, 124, 129, 130, 131, 132, 137, 138, 142, 143, 144, 145, 148,
			151, 156, 157, 158, 159, 160, 164, 166, 170}, avaBag = {10499};

	RSNPC npcMonster = null;
	BufferedImage normal = null, clicked = null;

	int randomBooth = bankBooths[random(0, bankBooths.length)];
	RSObject bankBooth = getNearestObjectByID(randomBooth);
	RSObject ladder = getNearestObjectByID(1757);

	private RSTile centerOfPath = new RSTile(0, 0);
	private RSTile bankToStatue = new RSTile(3578, 3526);
	private RSTile goingToBank = bankBooth.getLocation();
	RSTile undergroundToStairs = new RSTile(3576, 9927);
	RSTile stairsToUnderground = new RSTile(3486, 9938);
	RSTile[] Spot1 = {new RSTile(3571, 9932), new RSTile(3566, 9936),
			new RSTile(3564, 9940), new RSTile(3558, 9945)};
	RSTile[] Spot2 = {new RSTile(3571, 9932), new RSTile(3566, 9936),
			new RSTile(3564, 9940), new RSTile(3558, 9945),
			new RSTile(3552, 9939), new RSTile(3547, 9932),
			new RSTile(3538, 9930), new RSTile(3532, 9928),
			new RSTile(3529, 9932), new RSTile(3522, 9932),
			new RSTile(3514, 9933), new RSTile(3507, 9933),
			new RSTile(3502, 9935), new RSTile(3496, 9937),
			new RSTile(3490, 9938), new RSTile(3484, 9939)};
	RSTile statueTile = new RSTile(3578, 3527);
	RSTile ladderTile = new RSTile(3577, 9927);
	RSTile bankTile = new RSTile(3510, 3480);
	RSTile WalkTile = new RSTile(3678, 3526);

	private long startTime = System.currentTimeMillis(), nextSpecialTime;

	private String Status = "Starting", Location = "Getting Location",
			foodName, arrowName;

	private final int KILLNPCS = 0, BANKING = 1, KILLSCRIPT = 2;

	private int Action = 0, arrowID = -1, bronzeArrow = 882, ironArrow = 884,
			steelArrow = 886, mithrilArrow = 888, adamantArrow = 890,
			runeArrow = 892, bronzeBolt = 877, boneBolt = 8882,
			blueriteBolt = 9139, ironBolt = 9140, steelBolt = 9141,
			blackBolt = 13083, mithrilBolt = 9142, adamantBolt = 9143,
			runeBolt = 9144, broadBolt = 13280, bronzeKnife = 864,
			ironKnife = 863, steelKnife = 865, blackKnife = 869,
			mithrilKnife = 866, adamantKnife = 867, runeKnife = 868,
			Bones = 526, /* Bones */
			itpIDs = 526, count = 0, experimentKilled,
			experimentPerHour, lifePoints, hpLvl, atkExp, atkLvl, defExp,
			defLvl, startAtkExp, startDefExp, startStrExp, startRangedExp,
			startHpExp, strExp, rangedExp, strLvl, rangedLvl, paintStyle,
			attackStyle = -1, strGained, atkGained, rgeGained, defGained,
			hpGained, hpExp, speed, specialCost = 0, lastSpecialValue = 0,
			Food, bronzeDart = 806, ironDart = 807, steelDart = 808,
			blackDart = 3093, mithrilDart = 809, adamantDart = 810,
			runeDart = 811;

	int chanceRandom(int min, int max) {
		return min + (int) (java.lang.Math.random() * (max - min));
	}

	private boolean Banking, chatResponder, doSpec, getBones, guiWait = true,
			guiExit;

	private final Rectangle ATTACK = new Rectangle(600, 273, 5, 5),
			STRENGTH = new Rectangle(685, 275, 5, 5), DEFENCE = new Rectangle(
			685, 326, 5, 5), RANGE = new Rectangle(685, 275, 5, 5);

	private double latestVersion;

	RSTile[] randompath;

	private double getVersion() {
		return getClass().getAnnotation(ScriptManifest.class).version();
	}

	/**
	 * *********************************************************
	 * ON START
	 * **********************************************************
	 */
	@Override
	public boolean onStart(final Map<String, String> args) {
		if (!checkVersion())
			return false;
		gui = new ExperimentSlayerGUI();
		gui.setVisible(true);
		while (guiWait) {
			wait(100);
		}
		gui.setVisible(false);
		final int ads = JOptionPane.showConfirmDialog(null,
				"Would you like to support me by donating?", "Starting Up",
				JOptionPane.YES_NO_OPTION);
		if (ads == 0) {
			final String message = "<html><b>Thank you for your support!</b><br/>"
					+ "<p>This will give me encouragement to write more scripts.<br/>"
					+ "This is a BIG help.  Thank you.<br/>"
					+ "(You will be redirected to the donate page)<br/>"
					+ "</html>";
			JOptionPane.showMessageDialog(null, message);
			openURL("https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=XPQ2H6AUTAKXU&lc=GB&item_name=ExperimentSlayez%20Script%20Service"
					+ "&currency_code=USD&bn=PP%2dDonationsBF%3abtn_donateCC_LG%2egif%3aNonHostedGuest");
		}
		try {
			final URL cursorURL = new URL(
					"http://dl.dropbox.com/u/3900566/Mouse.png");
			final URL cursor80URL = new URL(
					"http://dl.dropbox.com/u/3900566/click.png");
			normal = ImageIO.read(cursorURL);
			clicked = ImageIO.read(cursor80URL);
		} catch (MalformedURLException e) {
			log("Unable to buffer cursor.");
		} catch (IOException e) {
			log("Unable to open cursor image.");
		}
		thePainter.scriptRunning = true;
		experimentThread = new ExperimentSlayerChatResponder();
		if (chatResponder) {
			experimentThread.start();
			log("Chat Responder Enabled");
		} else {
			log("Chat Responder Thread Disabled");
		}
		if (isLoggedIn()) {
			setAttackStyles();
			setCameraAltitude(true);
			setCompass('N');
			return true && !guiExit;
		} else {
			log("You must be logged in to START this script.");
			return false;
		}
	}

	private void setAttackStyles() {
		Status = "Prefrences";
		switch (attackStyle) {
			case 0: // Attack
				openTab(Constants.TAB_ATTACK);
				wait(random(400, 600));
				clickMouse(ATTACK.x, ATTACK.y, ATTACK.width, ATTACK.height, true);
				wait(random(400, 600));
				break;
			case 1: // Strength
				openTab(Constants.TAB_ATTACK);
				wait(random(400, 600));
				clickMouse(STRENGTH.x, STRENGTH.y, STRENGTH.width, STRENGTH.height,
						true);
				wait(random(400, 600));
				break;
			case 2: // Defence
				openTab(Constants.TAB_ATTACK);
				wait(random(400, 600));
				clickMouse(DEFENCE.x, DEFENCE.y, DEFENCE.width, DEFENCE.height,
						true);
				wait(random(400, 600));
				break;
			case 3: // Range
				openTab(Constants.TAB_ATTACK);
				wait(random(400, 600));
				clickMouse(RANGE.x, RANGE.y, RANGE.width, RANGE.height, true);
				wait(random(400, 600));
				break;
		}
		openTab(Constants.TAB_INVENTORY);
		wait(random(600, 700));
	}

	private boolean checkVersion() {
		try {
			URLConnection localURLConnection = new URL(
					"http://www.rs2sell.co.cc/scripts/ExperimentSlayerVERSION.txt")
					.openConnection();
			BufferedReader localBufferedReader = new BufferedReader(
					new InputStreamReader(localURLConnection.getInputStream()));
			latestVersion = Double.parseDouble(localBufferedReader.readLine());
		} catch (Exception localException1) {
			log("Error loading version data.");
		}
		if (latestVersion > getVersion()
				&& (JOptionPane
				.showConfirmDialog(
						null,
						"A new version of ExperimentSlayer Pro is available, do you want to update ExperimentSlayer Pro?") == 0)) {
			try {
				String str = GlobalConfiguration.Paths
						.getScriptsPrecompiledDirectory()
						+ File.separator + "ExperimentSlayer.jar";
				URL localURL = new URL(
						"http://www.rs2sell.co.cc/scripts/ExperimentSlayer.jar");
				try {
					downloadJar(localURL, str);
				} catch (Exception localException2) {
				}
			} catch (MalformedURLException localMalformedURLException) {
				localMalformedURLException.printStackTrace();
			}
			JOptionPane
					.showMessageDialog(null,
							"Newest version downloaded, Restart ExperimentSlayer Pro Please!!");
			return false;
		}
		return true;
	}

	private void downloadJar(URL paramURL, String paramString) throws Exception {
		URLConnection localURLConnection = paramURL.openConnection();
		localURLConnection.setRequestProperty("Accept-Encoding", "zip, jar");
		localURLConnection.connect();
		BufferedInputStream localBufferedInputStream = new BufferedInputStream(
				localURLConnection.getInputStream());
		BufferedOutputStream localBufferedOutputStream = new BufferedOutputStream(
				new FileOutputStream(paramString));
		byte[] arrayOfByte = new byte[1024];
		while (true) {
			int i1 = localBufferedInputStream.read(arrayOfByte);
			if (i1 == -1) {
				break;
			}
			localBufferedOutputStream.write(arrayOfByte, 0, i1);
		}
		localBufferedOutputStream.flush();
		localBufferedOutputStream.close();
		localBufferedInputStream.close();
	}

	@SuppressWarnings("unused")
	private boolean oldUpdater() {
		URLConnection url = null;
		BufferedReader in = null;
		BufferedWriter out = null;
		if (getVersion() >= latestVersion) {
			JOptionPane.showMessageDialog(null,
					"You have the latest version of ExperimentSlayer!");
			final int ads = JOptionPane.showConfirmDialog(null,
					"Would you like to support me by donating?", "Starting Up",
					JOptionPane.YES_NO_OPTION);
			if (getVersion() < latestVersion) {
				JOptionPane
						.showMessageDialog(null,
								"You do not have the latest version of ExperimentSlayer!");
				try {
					getLatestVersion();
					if (getVersion() != latestVersion) {
						if (JOptionPane.showConfirmDialog(null,
								"Update found. Do you want to update?") == 0) {
							JOptionPane
									.showMessageDialog(null,
											"Please choose 'ExperimentSlayer.java' in your scripts folder and hit 'Open'");
							JFileChooser fc = new JFileChooser();
							if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
								url = new URL(
										"http://www.rs2sell.co.cc/scripts/ExperimentSlayer.java")
										.openConnection();
								in = new BufferedReader(new InputStreamReader(
										url.getInputStream()));
								out = new BufferedWriter(new FileWriter(fc
										.getSelectedFile().getPath()));
								String inp;
								while ((inp = in.readLine()) != null) {
									out.write(inp);
									out.newLine();
									out.flush();
								}
								log("Script successfully downloaded. Please recompile and reload your scripts!");
								return false;
							} else {
								log("Update canceled");
							}
						} else {
							log("Update canceled");
						}
					} else {
						JOptionPane.showMessageDialog(null,
								"You have the latest version. :)");
					}
					if (in != null) {
						in.close();
					}
					if (out != null) {
						out.close();
					}
				} catch (IOException e) {
					log("Problem getting version :/");
					return false;
				}
			}
		}
		return false;
	}

	private void openURL(final String url) {
		final String osName = System.getProperty("os.name");
		try {
			if (osName.startsWith("Mac OS")) {
				final Class<?> fileMgr = Class
						.forName("com.apple.eio.FileManager");
				final Method openURL = fileMgr.getDeclaredMethod("openURL",
						new Class[]{String.class});
				openURL.invoke(null, new Object[]{url});
			} else if (osName.startsWith("Windows")) {
				Runtime.getRuntime().exec(
						"rundll32 url.dll,FileProtocolHandler " + url);
			} else {
				final String[] browsers = {"firefox", "opera", "konqueror",
						"epiphany", "mozilla", "google chrome", "netscape"};
				String browser = null;
				for (int count = 0; count < browsers.length && browser == null; count++) {
					if (Runtime.getRuntime().exec(
							new String[]{"which", browsers[count]})
							.waitFor() == 0) {
						browser = browsers[count];
					}
				}
				if (browser == null)
					throw new Exception("Could not find web browser");
				else {
					Runtime.getRuntime().exec(new String[]{browser, url});
				}
			}
		} catch (final Exception e) {
		}
	}

	public boolean isFighting() {
		return getMyPlayer().getInteracting() != null
				&& getMyPlayer().isInCombat();
	}

	private boolean clickNPC(final RSNPC npc, final String action) {
		if (npc == null)
			return false;
		final RSTile tile = npc.getLocation();
		if (!tile.isValid())
			return false;

		try {
			Point screenLoc = npc.getScreenLocation();
			if (distanceTo(tile) > 6 || !pointOnScreen(screenLoc)) {
				turnToTile(tile);
			}
			if (distanceTo(tile) > 20) {
				walkTileMM(tile);
				return false;
			}
			for (int i = 0; i < 12; i++) {
				screenLoc = npc.getScreenLocation();
				if (!npc.isValid() || !pointOnScreen(screenLoc))
					return false;
				moveMouse(screenLoc, 5, 5);
				if (getMenuItems().get(0).toLowerCase().contains(
						npc.getName().toLowerCase())) {
					break;
				}
				if (getMouseLocation().equals(screenLoc)) {
					break;
				}
			}
			final List<String> menuItems = getMenuItems();
			if (menuItems.isEmpty())
				return false;
			for (String menuItem : menuItems) {
				if (menuItem.toLowerCase()
						.contains(npc.getName().toLowerCase())) {
					if (menuItems.get(0).toLowerCase().contains(
							action.toLowerCase())) {
						clickMouse(true);
						return true;
					} else {
						clickMouse(false);
						return atMenu(action);
					}
				}
			}
		} catch (final Exception e) {
			log.log(Level.SEVERE, "clickNPC(RSNPC, String) error: ", e);
			return false;
		}
		return false;
	}

	private double getLatestVersion() {
		URLConnection url;
		BufferedReader in;
		try {
			url = new URL(
					"http://www.rs2sell.co.cc/scripts/ExperimentSlayerVERSION.txt")
					.openConnection();
			in = new BufferedReader(new InputStreamReader(url.getInputStream()));
			latestVersion = Double.parseDouble(in.readLine());
		} catch (final Exception e) {
			log("Error loading version data.");
		}
		return latestVersion;
	}

	private int playerCount() {
		int lvl = 3;
		int numbPlayers = 0;
		while (lvl <= 138) {
			RSCharacter player = getNearestPlayerByLevel(lvl);
			if (player != null && distanceTo(player) < 40) {
				numbPlayers++;
			}
			lvl++;
		}
		return numbPlayers;
	}

	@SuppressWarnings("unused")
	private int SwitchWorldz() {
		if (playerCount() > 3 && getNearestNPCByID(experimentID) != null) {
			if (!getMyPlayer().isInCombat()) {
				Status = "Switching worlds";
				speed = 10;
				log("There are more than 3 players here. Switching worlds.");
				switchWorld(Worldz[random(0, Worldz.length - 1)]);
				speed = 5;
			}
		}
		return random(2000, 3500);
	}

	private int checkExperimentKC() {
		if (experimentKilled == 1) {
			log("First Blood! One down many to fuck XD");
			return 1000000;
		} else if (experimentKilled == 5) {
			log("Our Trainging begins :3");
			return 1000000;
		} else if (experimentKilled == 10) {
			log("10 dead... how many to to rape?");
			return 1000000;
		} else if (experimentKilled == 50) {
			log("50 slayed woot! experiment rage ^^");
			return 1000000;
		} else if (experimentKilled == 100) {
			log("Monster kill!! 100 Experiments dead, Nice one!");
			return 1000000;
		} else if (experimentKilled == 250) {
			log("Killing spree! 250 Experiments slaughter, Alright owning!");
			return 1000000;
		} else if (experimentKilled == 500) {
			log("Boom rapeshot!!, you already killed 500 Experiments!");
			return 1000000;
		} else if (experimentKilled == 999) {
			log("Holy shit! you just raped your 999th Experiment!");
			return 1000000;
		} else if (experimentKilled == 1500) {
			log("Godlike! 1500 Experiment raped and bang up!");
			return 1000000;
		} else if (experimentKilled == 2000) {
			log("Dominating! 2000 Experiment pwned and owned!");
			return 1000000;
		} else if (experimentKilled == 5000) {
			log("Ludichris Kill! 5000 Experiment wasted!");
			return 1000000;
		}
		return random(200, 600);
	}

	public int getSkillLevel() {
		return skills.getRealSkillLevel(skillToCheck);
	}

	public int getSkill(String s) {
		if (s.equalsIgnoreCase("Attack"))
			return Constants.STAT_ATTACK;
		if (s.equalsIgnoreCase("Strength"))
			return Constants.STAT_STRENGTH;
		if (s.equalsIgnoreCase("Defense"))
			return Constants.STAT_DEFENSE;
		if (s.equalsIgnoreCase("Range"))
			return Constants.STAT_RANGE;
		return 0;
	}

	private void drinkPot(String type) {
		if (type.equals("Super Attack")) {
			for (final int id : superAttack) {
				if (getInventoryCount(id) > 0) {
					clickInventoryItem(superAttack, "Drink");
				}
			}
		} else if (type.equals("Super Strength")) {
			for (final int id : superStrength) {
				if (getInventoryCount(id) > 0) {
					clickInventoryItem(superStrength, "Drink");
				}
			}
		} else if (type.equals("Super Defense")) {
			for (final int id : superDefense) {
				if (getInventoryCount(id) > 0) {
					clickInventoryItem(superDefense, "Drink");
				}
			}
		} else if (type.equals("Normal Range")) {
			for (final int id : normalRange) {
				if (getInventoryCount(id) > 0) {
					clickInventoryItem(normalRange, "Drink");
				}
			}
		} else if (type.equals("Normal Attack")) {
			for (final int id : normalAttack) {
				if (getInventoryCount(id) > 0) {
					clickInventoryItem(normalAttack, "Drink");
				}
			}
		} else if (type.equals("Normal Strength")) {
			for (final int id : normalStrength) {
				if (getInventoryCount(id) > 0) {
					clickInventoryItem(normalStrength, "Drink");
				}
			}
		} else if (type.equals("Normal Defense")) {
			for (final int id : normalDefense) {
				if (getInventoryCount(id) > 0) {
					clickInventoryItem(normalDefense, "Drink");
				}
			}
		}
	}

	private boolean shouldDrinkPot() {
		return (skills.getCurrentSkillLevel(Constants.STAT_ATTACK) <= skills
				.getRealSkillLevel(Constants.STAT_ATTACK))
				|| (skills.getCurrentSkillLevel(Constants.STAT_STRENGTH) <= skills
				.getRealSkillLevel(Constants.STAT_STRENGTH))
				|| (skills.getCurrentSkillLevel(Constants.STAT_DEFENSE) <= skills
				.getRealSkillLevel(Constants.STAT_DEFENSE))
				|| (skills.getCurrentSkillLevel(Constants.STAT_RANGE) <= skills
				.getRealSkillLevel(Constants.STAT_RANGE));
	}

	private void pickOI() {
		getInventoryItemByID(itpIDs);
		int OldOitemCount = getInventoryCount(itpIDs);
		RSItemTile TItem = getGroundItemByID(4, itpIDs);
		if (TItem != null && getInventoryCount() < 26
				&& canPickUpItem(TItem.getItem().getID())) {
			atTile(TItem, "Take");
			while (getMyPlayer().isMoving()) {
				wait(15);
			}
			if (getInventoryCount(itpIDs) > OldOitemCount) {
				count = count + 1;
				log("Picked up item.");
				return;
			} else {
				log("No item found.");
				return;
			}
		}
		return;
	}

	private void checkForFood() {
		int RealHP = skills.getRealSkillLevel(STAT_HITPOINTS) * 10;
		if (getCurrentLifepoint() <= random(RealHP / 2.5, RealHP / 2)) {
			log("Food time");
			if (getInventoryCount(peach) >= 1) {
				clickInventoryItem(peach, "Eat");
				wait(random(1000, 2000));
			} else {
				log("Emergency tab time");
				clickInventoryItem(tabID, "Break");
				wait(random(2000, 3000));
			}
			if (getInventoryCount(Bones) >= 20) {
				if (getInventoryCount(tabID) >= 1) {
					clickInventoryItem(tabID, "Break");
					log("Made more peaches");
					wait(random(2000, 3000));
				} else {
					log("We are out of tablets!");
					wait(5000);
					logout();
					stopScript();
				}
			}
		}
	}

	private void clickMouseSlightly() {
		Point p = new Point(
				(int) (getMouseLocation().getX() + (Math.random() * 50 > 25 ? 1
						: -1)
						* (30 + Math.random() * 90)), (int) (getMouseLocation()
						.getY() + (Math.random() * 50 > 25 ? 1 : -1)
						* (30 + Math.random() * 90)));
		if (p.getX() < 1 || p.getY() < 1 || p.getX() > 761 || p.getY() > 499) {
			clickMouseSlightly();
			return;
		}
		clickMouse(p, true);
	}

	private int clickSpec() {
		openTab(Constants.TAB_ATTACK);
		wait(random(200, 500));
		clickMouse(645 + random(0, 4), 425 + random(0, 4), true);
		openTab(Constants.TAB_INVENTORY);
		return random(200, 300);
	}

	private int getAngleToCoord(RSTile loc) {
		int x1 = getMyPlayer().getLocation().getX();
		int y1 = getMyPlayer().getLocation().getY();
		int x = x1 - loc.getX();
		int y = y1 - loc.getY();
		double angle = Math.toDegrees(Math.atan2(y, x));
		log("Angle: " + (int) angle);
		return (int) angle;
	}

	private boolean containsSkill(String text) {

		for (int i = 0; i < skillz.length; i++) {
			if (text.toLowerCase().contains(skillz[i]))
				return true;
		}

		return false;
	}

	public String getChildText(int iface, int child) {
		return RSInterface.getInterface(iface).getChild(child).getText();
	}

	public String getResponse(String question) throws InterruptedException {
		try {
			// Setting up the URL
			URL url = enter(question);

			// Buffer Reading
			BufferedReader in = new BufferedReader(new InputStreamReader(url
					.openStream()));
			String str = null;
			for (int i = 0; i < 90; i++) {
				String r = in.readLine();
				if (r.contains("<B>splotchy ==>")) {
					String modify = r.split("==> ")[1];
					return modify.split("<")[0];
				}
			}
			in.close();
			return str;
		} catch (Exception e) {
			return null;
		}
	}

	String space = "+";

	public String modify(String b) throws InterruptedException {
		return b.replace(" ", space);
	}

	public URL enter(String input) throws InterruptedException {
		input = modify(input);
		try {
			return new URL("http://algebra.com/cgi-bin/chat.mpl?input=" + input);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String getChatMessage() {
		String text = null;

		for (int i = 58; i < 157; i++) {
			if (getChildText(137, i) != null) {
				if (getChildText(137, i).contains("<col=0000ff>")) {
					text = getChildText(137, i).substring(
							getChildText(137, i).indexOf("<col=0000ff>") + 12);
				}
			}
		}
		return text;
	}

	public boolean sendMessage(String question) {
		if (getChatMessage().toLowerCase() != null) {
			sendText(getChatMessage().toLowerCase(), true);
			return true;
		}
		return false;
	}

	private boolean canUseSpecial() {
		final int SPECIAL_BAR_VAL = getSetting(300);
		final int SPECIAL_IS_ON = getSetting(301);
		switch (SPECIAL_IS_ON) {
			case 0:
				if (specialCost == 0) {
					if (SPECIAL_BAR_VAL < lastSpecialValue) {
						specialCost = lastSpecialValue - SPECIAL_BAR_VAL;
						return false;
					}
				}
				if (SPECIAL_BAR_VAL >= specialCost
						&& System.currentTimeMillis() >= nextSpecialTime) {
					nextSpecialTime = System.currentTimeMillis()
							+ random(5000, 45000);
					return true;
				}
			case 1:
				lastSpecialValue = SPECIAL_BAR_VAL;
				return false;
		}
		return false;
	}

	@Override
	protected int getMouseSpeed() {
		return random(speed - 1, speed + 1);
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

	private void overlayTile(final Graphics g, final RSTile t, final Color c) {
		final Point p = Calculations.tileToScreen(t);
		final Point pn = Calculations.tileToScreen(t.getX(), t.getY(), 0, 0, 0);
		final Point px = Calculations.tileToScreen(t.getX() + 1, t.getY(), 0,
				0, 0);
		final Point py = Calculations.tileToScreen(t.getX(), t.getY() + 1, 0,
				0, 0);
		final Point pxy = Calculations.tileToScreen(t.getX() + 1, t.getY() + 1,
				0, 0, 0);
		final Point[] points = {p, pn, px, py, pxy};
		for (final Point point : points) {
			if (!pointOnScreen(point))
				return;
		}
		g.setColor(c);
		g.fillPolygon(new int[]{py.x, pxy.x, px.x, pn.x}, new int[]{py.y,
				pxy.y, px.y, pn.y}, 4);
		g.drawPolygon(new int[]{py.x, pxy.x, px.x, pn.x}, new int[]{py.y,
				pxy.y, px.y, pn.y}, 4);
	}

	private boolean clickInventoryItem(final int[] ids, final String command) {
		try {
			if (getCurrentTab() != Constants.TAB_INVENTORY
					&& !RSInterface.getInterface(Constants.INTERFACE_BANK)
					.isValid()
					&& !RSInterface.getInterface(Constants.INTERFACE_STORE)
					.isValid()) {
				openTab(Constants.TAB_INVENTORY);
			}
			final int[] items = getInventoryArray();
			final java.util.List<Integer> possible = new ArrayList<Integer>();
			for (int i = 0; i < items.length; i++) {
				for (final int item : ids) {
					if (items[i] == item) {
						possible.add(i);
					}
				}
			}
			if (possible.size() == 0)
				return false;
			final int idx = possible.get(random(0, possible.size()));
			final Point t = getInventoryItemPoint(idx);
			moveMouse(t, 5, 5);
			wait(random(100, 290));
			if (getMenuActions().get(0).equals(command)) {
				clickMouse(true);
				return true;
			} else
				// clickMouse(false);
				return atMenu(command);
		} catch (final Exception e) {
			log.log(Level.SEVERE, "clickInventoryFood(int...) error: ", e);
			return false;
		}
	}

	private boolean canPickUpItem(int itemID) {
		if (getInventoryCount() < 28)
			return true;
		else {
			if (getInventoryCount(itemID) > 0
					&& getInventoryItemByID(itemID).getStackSize() > 1)
				return true;
		}
		return false;
	}

	private int getCurrentLifepoint() {
		if (RSInterface.getInterface(748).getChild(8).getText() != null) {
			lifePoints = Integer.parseInt(RSInterface.getInterface(748)
					.getChild(8).getText());
		} else {
			log("Hp interface == null");
		}
		return lifePoints;
	}

	private int getAction() {
		if (getNearestFreeNPCByID(experimentID) != null && !isFighting())
			return KILLNPCS;
		else if (Banking && getInventoryCount(foodID) <= 1)
			return BANKING;
		else if (!Banking && distanceTo(centerOfPath) > 20) {
			if (random(1, 2) == 1) {
				randompath = randomizePath(Spot1, 2, 2);
				centerOfPath = new RSTile(3557, 9949);
			} else {
				randompath = randomizePath(Spot2, 2, 2);
				centerOfPath = new RSTile(3484, 9939);
			}
			if (distanceTo(centerOfPath) > 20) {
				if (distanceTo(statueTile) < distanceTo(ladderTile)) {
					if (!tileOnScreen(statueTile) && distanceTo(statueTile) > 4) {
						walkTo(randomizeTile(bankToStatue, 2, 2));
						Location = "Retrieving location";
						Status = "Walking to Memorial";
						atTile(WalkTile, "Walk");
						wait(random(1700, 2100));
					}
					if (tileOnScreen(statueTile)) {
						Location = "At Cave's Entrance";
						Status = "Pushing Memorial";
						atTile(statueTile, "Push");
						wait(random(3300, 3500));
					}
				}

				if (tileOnScreen(ladderTile)) {
					wait(random(50, 100));
					walkPathMM(randomizePath(randompath, 2, 2), 13 + random(2,
							4));
					Status = "Walking back to Npc";
					Location = "Experiment Cave";
					wait(random(1700, 2100));
				}
			}
			return random(500, 600);
		} else if (levelToGo <= getSkillLevel())
			return KILLSCRIPT;
		return random(20, 30);
	}

	int getStatX(int id) {
		switch (id) {
			case STAT_ATTACK:
				return 552;
			case STAT_STRENGTH:
				return 552;
			case STAT_DEFENSE:
				return 552;
			case STAT_RANGE:
				return 552;
			case STAT_PRAYER:
				return 552;
			case STAT_MAGIC:
				return 552;
			case STAT_RUNECRAFTING:
				return 552;
			case STAT_HITPOINTS:
				return 606;
			case STAT_AGILITY:
				return 606;
			case STAT_HERBLORE:
				return 606;
			case STAT_THIEVING:
				return 606;
			case STAT_CRAFTING:
				return 606;
			case STAT_FLETCHING:
				return 606;
			case STAT_SLAYER:
				return 606;
			case STAT_MINING:
				return 660;
			case STAT_SMITHING:
				return 660;
			case STAT_FISHING:
				return 660;
			case STAT_COOKING:
				return 660;
			case STAT_FIREMAKING:
				return 660;
			case STAT_WOODCUTTING:
				return 660;
			case STAT_FARMING:
				return 660;
		}
		log("Stats Tab - Error getting stats X-coordinates - Random move on screen");
		return autoRandom(1, 760);
	}

	int getStatY(int id) {
		switch (id) {
			case STAT_ATTACK:
				return 229;
			case STAT_STRENGTH:
				return 262;
			case STAT_DEFENSE:
				return 294;
			case STAT_RANGE:
				return 326;
			case STAT_PRAYER:
				return 358;
			case STAT_MAGIC:
				return 390;
			case STAT_RUNECRAFTING:
				return 422;
			case STAT_HITPOINTS:
				return 229;
			case STAT_AGILITY:
				return 262;
			case STAT_HERBLORE:
				return 294;
			case STAT_THIEVING:
				return 326;
			case STAT_CRAFTING:
				return 358;
			case STAT_FLETCHING:
				return 390;
			case STAT_SLAYER:
				return 422;
			case STAT_MINING:
				return 229;
			case STAT_SMITHING:
				return 262;
			case STAT_FISHING:
				return 294;
			case STAT_COOKING:
				return 326;
			case STAT_FIREMAKING:
				return 358;
			case STAT_WOODCUTTING:
				return 390;
			case STAT_FARMING:
				return 422;
		}
		log("Stats Tab - Error getting stats Y-coordinates - Random move on screen");
		return autoRandom(1, 500);
	}

	private boolean hoverPlayer() {
		RSPlayer player = null;
		int[] validPlayers = Bot.getClient().getRSPlayerIndexArray();
		org.rsbot.accessors.RSPlayer[] players = Bot.getClient()
				.getRSPlayerArray();

		for (int element : validPlayers) {
			if (players[element] == null) {
				continue;
			}

			player = new RSPlayer(players[element]);
			String playerName = player.getName();
			String myPlayerName = getMyPlayer().getName();
			if (playerName.equals(myPlayerName)) {
				continue;
			}
			try {
				RSTile targetLoc = player.getLocation();
				String name = player.getName();
				Point checkPlayer = Calculations.tileToScreen(targetLoc);
				if (pointOnScreen(checkPlayer) && checkPlayer != null) {
					clickMouse(checkPlayer, 5, 5, false);
					log("Hover Player - Right click on " + name);
				} else {
					continue;
				}
				return true;
			} catch (Exception ignored) {
			}
		}
		return player != null;
	}

	private RSTile examineRandomObject(int scans) {
		RSTile start = getMyPlayer().getLocation();
		ArrayList<RSTile> possibleTiles = new ArrayList<RSTile>();
		for (int h = 1; h < scans * scans; h += 2) {
			for (int i = 0; i < h; i++) {
				for (int j = 0; j < h; j++) {
					int offset = (h + 1) / 2 - 1;
					if (i > 0 && i < h - 1) {
						j = h - 1;
					}
					RSTile tile = new RSTile(start.getX() - offset + i, start
							.getY()
							- offset + j);
					RSObject objectToList = getObjectAt(tile);

					if (objectToList != null && objectToList.getType() == 0
							&& tileOnScreen(objectToList.getLocation())
							&& objectToList.getLocation().isValid()) {
						possibleTiles.add(objectToList.getLocation());
					}
				}
			}
		}
		if (possibleTiles.size() == 0) {
			log("Examine Object - Found no object");
			return null;
		}
		if (possibleTiles.size() > 0 && possibleTiles != null) {
			final RSTile objectLoc = possibleTiles.get(autoRandom(0,
					possibleTiles.size()));
			Point objectPoint = objectLoc.getScreenLocation();
			if (objectPoint != null) {
				log("Examine Object - Found object at: RSTile "
						+ objectLoc.getX() + ", " + objectLoc.getY());
				try {
					moveMouse(objectPoint);
					if (atMenu("xamine")) {
						log("Examine Object - Examined object");
					} else {
						log("Examine Object - Error examine");
					}
					wait(random(500, 1000));
				} catch (NullPointerException ignored) {
				}
			}
		}
		return null;
	}

	/**
	 * *********************************************************
	 * MAIN LOOP
	 * **********************************************************
	 */
	@Override
	public int loop() {
		if (isLoggedIn()
				&& (System.currentTimeMillis() - lastCheck) >= randomTime) {
			lastCheck = System.currentTimeMillis();
			randomTime = random(210000, 720000);

			int antiBan = autoRandom(0, 11);
			switch (antiBan) {
				case 1:
					speed = (autoRandom(6, 10));
					getMouseSpeed();
					log("Mouse Speed - Changed mouse speed to: " + speed);
					break;

				case 2:
					int movedMouse = 0;
					for (int i = 1; i < 11; i++) {
						int randomMouse = autoRandom(0, 3);
						if (randomMouse == 1) {
							movedMouse++;
							moveMouse(1, 1, 760, 500);
							wait(random(500, 1000));
							if (movedMouse <= 1) {
								log("Moved Mouse - " + movedMouse + " Time");
							} else {
								log("Moved Mouse - " + movedMouse + " Times");
							}
						}
					}
					break;

				case 3:
					int randomShit = autoRandom(0, 4);
					if (randomShit == 1) {
						int randTab = tabs[autoRandom(0, tabs.length)];
						if (getCurrentTab() != randTab) {
							openTab(randTab);
							log("Random Tab - Opened random tab");
						}
						int moveMouseOrNot = autoRandom(0, 4);
						if (moveMouseOrNot == 1 || moveMouseOrNot == 2) {
							moveMouse(550, 209, 730, 465);
							log("Random Tab - Moved mouse in tab");
						}
						int backToInvent = autoRandom(0, 4);
						if (backToInvent == 1 || backToInvent == 2) {
							openTab(Constants.TAB_INVENTORY);
							log("Random Tab - Switched back to invent");
						}
					}
					break;

				case 4:
					if (!getMyPlayer().isInCombat()
							|| getMyPlayer().getInteracting() != null) {
						int randomTime = autoRandom(500, 15000);
						wait(randomTime);
						log("AFK - " + randomTime + " Ms");
					} else {
						log("AFK - We're under attack");
					}
					break;

				case 5:
					hoverPlayer();
					wait(autoRandom(750, 3000));
					while (isMenuOpen()) {
						moveMouseRandomly(750);
						wait(random(100, 500));
					}
					break;

				case 6:
					int randomTurn = autoRandom(0, 5);
					switch (randomTurn) {
						case 1:
						case 2:
							log("Camera - Turned camera");
							new CameraRotateThread().start();
							break;

						case 3:
							log("Camera - Changed camera height");
							new CameraHeightThread().start();
							break;
						case 4:
							int randomFormation = autoRandom(0, 3);
							if (randomFormation == 1) {
								log("Camera - Turned camera and changed height");
								new CameraRotateThread().start();
								new CameraHeightThread().start();
							} else {
								log("Camera - Changed height and turned camera");
								new CameraHeightThread().start();
								new CameraRotateThread().start();
							}
							break;
					}
					break;

				case 7:
					examineRandomObject(5);
					wait(autoRandom(750, 3000));
					int moveMouseAfter2 = autoRandom(0, 4);
					wait(autoRandom(200, 3000));
					if (moveMouseAfter2 == 1 && moveMouseAfter2 == 2) {
						moveMouse(1, 1, 760, 500);
						log("Examine Object - Moved mouse after");
					}
					break;

				case 8:
					if (getCurrentTab() != Constants.TAB_INVENTORY
							&& !RSInterface.getInterface(Constants.INTERFACE_BANK)
							.isValid()
							&& !RSInterface.getInterface(Constants.INTERFACE_STORE)
							.isValid()) {
						openTab(Constants.TAB_INVENTORY);
						log("Hover item - Opened Inventory");
					}

					if (getCurrentTab() != Constants.TAB_INVENTORY
							&& !RSInterface.getInterface(Constants.INTERFACE_BANK)
							.isValid()
							&& !RSInterface.getInterface(Constants.INTERFACE_STORE)
							.isValid()) {
						openTab(Constants.TAB_INVENTORY);
						log("Hover item - Opened Inventory");
					}

					int[] items = getInventoryArray();
					java.util.List<Integer> possible = new ArrayList<Integer>();
					for (int i = 0; i < items.length; i++) {
						if (items[i] > 1) {
							possible.add(i);
						}
					}
					if (possible.size() == 0) {
						log("Hover Item - No items in inventory");
					}
					if (possible != null && possible.size() >= 1) {
						int idx = possible.get(random(0, possible.size()));
						Point t = getInventoryItemPoint(idx);
						try {
							if (idx != -1) {
								moveMouse(t, 5, 5);
								int rightClickOrNot = autoRandom(0, 3);
								if (rightClickOrNot == 1 || rightClickOrNot == 2) {
									clickMouse(false);
									log("Hover item - Right clicked item");
								} else {
									log("Hover item - Hovered item");
								}
								int moveAfter = autoRandom(0, 3);
								if (moveAfter == 1 || moveAfter == 2) {
									moveMouse(1, 1, 760, 500);
									log("Hover item - Moved mouse after");
								}
							} else {
								log("Hover item - No items in inventory");
							}
						} catch (final Exception e) {
							log("Hover item - Error hovering item");
						}
					}
					break;

				case 9:
					if (getCurrentTab() != TAB_STATS) {
						openTab(TAB_STATS);
						log("Stats Tab - Opened stats tab");
						int hoveredSkill = 0;
						int shouldHover = autoRandom(0, 4);
						for (int i = 1; i < 5; i++) {
							if (shouldHover == 1 || shouldHover == 2
									|| shouldHover == 3) {
								int randomStat = stats[autoRandom(0, stats.length)];
								hoveredSkill++;
								moveMouse(getStatX(randomStat),
										getStatY(randomStat), maxXTab, maxYTab);
								if (hoveredSkill <= 1) {
									log("Stats Tab - Hovered " + hoveredSkill
											+ " skill");
								} else {
									log("Stats Tab - Hovered " + hoveredSkill
											+ " skills");
								}
								wait(autoRandom(500, 7000));
							}
						}
					}
					int backToInvent = autoRandom(0, 3);
					if (backToInvent == 1) {
						openTab(Constants.TAB_INVENTORY);
						log("Stats Tab - Switched back to inventory");
					}
					break;

				default: // Default, skipping
					log("Skipped AntiBan");
					break;
			}
			return randomTime;
		}
		getMouseSpeed();
		if (Location == null || Status == null) {
			Status = "Updating";
			Location = "Updating";
		}
		if (getBones) {
			checkForFood();
			pickOI();
		}
		if (!isRunning() && getEnergy() > random(20, 30)) {
			setRun(true);
		}
		if (!isRetaliateEnabled()) {
			if (getCurrentTab() != Constants.TAB_ATTACK) {
				openTab(TAB_ATTACK);
			}
			clickMouse(random(579, 706), random(363, 395), true);
			wait(random(600, 800));
		}
		if (!thePainter.savedStats) {
			thePainter.saveStats();
		}

		int RealHP = skills.getRealSkillLevel(STAT_HITPOINTS) * 10;
		if (getCurrentLifepoint() <= random(RealHP / 2.5, RealHP / 2)) {
			if (getInventoryCount(foodID) >= 1) {
				Status = "Eating food";
				clickInventoryItem(foodID, "Eat");
				return (random(450, 650));
			} else if (getCurrentLifepoint() <= random(RealHP / 2.5, RealHP / 2)
					&& getBones) {
				if (getInventoryCount(foodID) >= 1) {
					Status = "Eating food";
					clickInventoryItem(foodID, "Eat");
					return (random(450, 650));
				} else if (getInventoryCount(foodID) == 0) {
					Status = "Out of food! shutting down";
					log("We are out of food! logging out");
					wait(8000);
					logout();
					stopScript();
				}
			}
		}

		if (getCurrentLifepoint() <= RealHP / 10) {
			if (getInventoryCount(teleTabIDs) >= 1) {
				Status = "Teleporting";
				clickInventoryItem(teleTabIDs, "Break");
				log("Emergency teleporting.....Now logging out");
				wait(5000);
				logout();
				stopScript();
			}
		}

		Action = getAction();
		switch (Action) {
			case KILLNPCS:
				Location = "Experiment Cave";
				RSItemTile ARROW_TILE = getGroundItemByID(arrowID);

				if (arrowID != -1 && !getMyPlayer().isInCombat()) {
					Status = "Picking up Arrows";
					Location = "At Arrow Tile";
					atTile(ARROW_TILE, "Take " + arrowName);
					wait(random(400, 600));
				}
				if (ARROW_TILE != null)
					return 100;
				if (inventoryContains(arrowID)
						&& getInventoryCount(arrowID) == random(50, 100)) {
					if (getCurrentTab() != TAB_INVENTORY) {
						openTab(TAB_INVENTORY);
					}
					Status = "Equiping Arrows";
					atInventoryItem(arrowID, "Wield");
					return random(15000, 30000);
				}

				if (getInventoryCount(Potions) > 0 && shouldDrinkPot()) {
					for (String aPotion : potionsToDrink) {
						drinkPot(aPotion);
					}
				}

				if (doSpec) {
					if (canUseSpecial()) {
						clickSpec();
					}
				}

				RSNPC npcMonster = getNearestFreeNPCByID(experimentID);
				if (npcMonster != null) {
					if (npcMonster.getInteracting() != null
							&& getMyPlayer().getInteracting() == null)
						return random(100, 200);

					RSNPC Npc = getNearestNPCByID(experimentID);
					if (Npc.getHPPercent() < 1) {
						checkExperimentKC();
						return random(50, 120);

					}
					if (pointOnScreen(npcMonster.getScreenLocation())
							&& getMyPlayer().getInteracting() == null) {
						Status = "Attacking Experiments";
						clickNPC(npcMonster, "attack");
						moveMouse(-1, 1);
						return random(1500, 1800);
					} else if (!pointOnScreen(npcMonster.getScreenLocation())
							&& getMyPlayer().getInteracting() == null
							&& !getMyPlayer().isMoving()) {
						Status = "Walking 2 Experiments";
						walkTo(npcMonster.getLocation());
						moveMouseSlightly();
						return random(1800, 2500);
					}
					Point nextNPC = npcMonster.getScreenLocation();
					if (getMyPlayer().getInteracting() != null
							&& nextNPC != null
							&& pointOnScreen(nextNPC)
							&& getCurrentLifepoint() > random(RealHP / 2.5,
							RealHP / 2)) {
						int npcAngle = getAngleToCoord(npcMonster.getLocation());
						Status = "Setting camara angle";
						setCameraRotation(npcAngle);
					}
					return random(200, 400);
				}
				return random(500, 1000);

			case KILLSCRIPT:
				log("Reached Goal Skill Level");
				stopScript();
				return random(100, 200);

			case BANKING:
				if (distanceTo(ladderTile) < distanceTo(bankTile)) {
					log("walking to ladder");
					if (!tileOnScreen(ladderTile)) {
						walkTo(randomizeTile(undergroundToStairs, 2, 2));
						Status = "Walking to Ladders";
						Location = "Experiment Cave";
						wait(random(1700, 2200));
					}
					if (tileOnScreen(ladderTile)) {
						Location = "At Ladders";
						Status = "Climbing up Ladders";
						atObject(ladder, "Climb-up");
						turnToObject(ladder);
						wait(random(1700, 2100));
					}
				}
				if (distanceTo(bankTile) < distanceTo(ladderTile)) {
					if (!tileOnScreen(bankTile) || bankBooth != null
							&& !tileOnScreen(bankBooth.getLocation())) {
						walkTo(randomizeTile(goingToBank, 2, 2));
						Location = "Retrieving location";
						Status = "Going to Bank";
						wait(random(1700, 2100));
					}
					if (bankBooth != null && tileOnScreen(bankBooth.getLocation())) {
						Location = "Canfis Bank";
						if (!bank.open()) {
							bank
									.atBankBooth(bankBooth.getLocation(),
											"Use-quickly");
							Status = "Banking";
							wait(random(1000, 1200));
						}
					}
					if (bank.isOpen()) {
						int Amount = 28 - getInventoryCount() - 4;
						bank.depositAllExcept(foodID);
						Status = "Banking";
						bank.searchItem(foodName);
						wait(random(1200, 1300));
						if (bank.getCount(foodID) < Amount) {
							Amount = bank.getCount(foodID);
						}
						bank.withdraw(Food, Amount);
						wait(random(1200, 1300));
						bank.close();
						wait(random(1000, 1200));
					}
					if (bank.isOpen() && bank.getCount(foodID) == 0
							&& getInventoryCount(foodID) <= 2) {
						Status = "Out of food! shutting down";
						log("We are out of food! logging out");
						wait(8000);
						logout();
						stopScript();
					}
				}
				return random(500, 600);
		}

		if (getInventoryCount(Food) >= 2 && distanceTo(centerOfPath) > 20) {
			/*
			 * This randomizes the path we use to walk back
			 */
			if (random(1, 2) == 1) {
				randompath = randomizePath(Spot1, 2, 2);
				centerOfPath = new RSTile(3557, 9949);
			} else {
				randompath = randomizePath(Spot2, 2, 2);
				centerOfPath = new RSTile(3484, 9939);
			}
			// centerofpath is the center of the attacking area
			if (getInventoryCount(Food) >= 2 && distanceTo(centerOfPath) > 20) {
				if (distanceTo(statueTile) < distanceTo(ladderTile)) {
					if (!tileOnScreen(statueTile) && distanceTo(statueTile) > 4) {
						walkTo(randomizeTile(bankToStatue, 2, 2));
						Location = "Retrieving location";
						Status = "Walking to Memorial";
						atTile(WalkTile, "Walk");
						wait(random(1700, 2100));
					}
					if (tileOnScreen(statueTile)) {
						Location = "At Cave's Entrance";
						Status = "Pushing Memorial";
						atTile(statueTile, "Push");
						wait(random(3300, 3500));
					}
				}

				if (tileOnScreen(ladderTile)) {
					wait(random(50, 100));
					walkPathMM(randomizePath(randompath, 2, 2), 13 + random(2,
							4));
					Status = "Walking back to Npc";
					Location = "Experiment Cave";
					wait(random(1700, 2100));
				}
				return random(500, 600);
			}
		}

		return random(400, 1200);
	}

	public void serverMessageRecieved(ServerMessageEvent arg0) {
		String serverString = arg0.getMessage();

		if (serverString.contains("<col=ffff00>System update in")) {
			log("There will be a system update soon, so we logged out");
			logout();
			stopScript();
		}
		if (serverString.contains("Oh dear, you are dead!")) {
			Status = "Dead";
			Location = "Lost :(";
			log("We somehow died :S, shutting down");
			logout();
			stopScript();
		}
		if (serverString.contains("Someone else is fighting that")) {
			Status = "Random Clicking";
			log("We click on someone experiment, randomly clicking");
			log("so we don't look like we botting :)");
			clickMouseSlightly();
		}
		if (serverString.contains("already under attack")) {
			wait(random(2000, 3000));
			npcMonster = null;
		}
		if (serverString.contains("There is no ammo left in your quiver.")) {
			log("We have no arrows left, shutting down!");
			logout();
			stopScript();
		}
		if (serverString.contains("You've just advanced")) {
			log("Woo a level! hurray 4 pwning experiments");
			atkLvl++;
			defLvl++;
			strLvl++;
			rangedLvl++;
			wait(random(1500, 2500));
			if (canContinue()) {
				clickContinue();
			} else if (serverString
					.contentEquals("Congratulations! You've just advanced a Hitpoints level!")) {
				log("Woo a level! hurray 4 pwning experiments");
				hpLvl++;
				wait(random(1500, 2500));
				if (canContinue()) {
					clickContinue();
				}
			}
		}
	}

	@Override
	public void onFinish() {
		long millis = System.currentTimeMillis() - startTime;
		long hours = millis / (1000 * 60 * 60);
		millis -= hours * (1000 * 60 * 60);
		long minutes = millis / (1000 * 60);
		millis -= minutes * (1000 * 60);
		long seconds = millis / 1000;
		ScreenshotUtil.takeScreenshot(true);
		log.info("Ran for " + hours + ":" + minutes + ":" + seconds);
		log.info(": You have gained " + hpLvl + " hp levels + " + hpGained
				+ " hp experience.");
		if (attackStyle == 0) {
			log.info(": You have gained " + atkLvl + " attack levels + "
					+ atkGained + " attack experience.");
		}
		if (paintStyle == 2) {
			log.info(": You have gained " + defLvl + " defense levels + "
					+ defGained + " defense experience.");
		}
		if (attackStyle == 1) {
			log.info(": You have gained " + strLvl + " strength levels + "
					+ strGained + " strength experience.");
		}
		if (attackStyle == 3) {
			log.info(": You have gained " + rangedLvl + " range levels + "
					+ rgeGained + " range experience.");
		}
		log("We Gained a total of "
				+ (atkGained + defGained + strGained + rgeGained + hpGained)
				+ " Experience");
		log("You Raped " + experimentKilled + " Experiments");
		log("\u2020\u2020\u2020\u2020\u2020\u2020Stopping Experiment Slayer\u2020\u2020\u2020\u2020\u2020\u2020");
		log("<3<3\u2020Bye Bye Mr Experiment Rapist\u2020<3<3");
		logout();
	}

	private RSTile getClosestTileInRegion(final RSTile tile) {
		if (tileInRegion(tile))
			return tile;
		final RSTile loc = getMyPlayer().getLocation();
		final RSTile walk = new RSTile((loc.getX() + tile.getX()) / 2, (loc
				.getY() + tile.getY()) / 2);
		return tileInRegion(walk) ? walk : getClosestTileInRegion(walk);
	}

	private boolean tileInRegion(final RSTile tile) {
		final int tileX = tile.getX() - Bot.getClient().getBaseX(), tileY = tile
				.getY()
				- Bot.getClient().getBaseY();
		return !(tileX < 0 || tileY < 0 || tileX > 103 || tileY > 103);
	}

	@Override
	public boolean walkTo(final RSTile tile) {
		RSTile dest = getMyPlayer().getLocation();
		RSTile[] path = null;
		if (Methods.distanceBetween(tile, dest) > 1) {
			dest = getClosestTileInRegion(tile);
			path = pathFinder.findPath(getMyPlayer().getLocation(), dest);
		}
		if (path == null)
			return false;
		for (int i = path.length - 1; i >= 0; i--) {
			if (distanceTo(path[i]) < 17
					&& getRealDistanceTo(path[i], false) < 60) {
				final RSTile currDest = getDestination();
				if (currDest != null) {
					if (Methods.distanceBetween(currDest, path[i]) <= 3) {
						break;
					}
				}
				walkTo(checkTile(path[i]), 1, 1);
				wait(random(200, 400));
				final RSTile cdest = getDestination();
				if (cdest != null && distanceTo(cdest) > 6) {
					wait(random(300, 500));
				}
				break;
			}
		}
		return true;
	}

	private final AStar pathFinder = new AStar();

	private RSTile checkTile(final RSTile tile) {
		if (distanceTo(tile) < 17)
			return tile;
		final RSTile loc = getMyPlayer().getLocation();
		final RSTile walk = new RSTile((loc.getX() + tile.getX()) / 2, (loc
				.getY() + tile.getY()) / 2);
		return distanceTo(walk) < 17 ? walk : checkTile(walk);
	}

	/**
	 * *********************************************************
	 * PAINT
	 * **********************************************************
	 */
	public void onRepaint(Graphics g) {
		thePainter.paint(g);
		drawMouse(g);

		if (normal != null) {
			final Mouse mouse = Bot.getClient().getMouse();
			final int mouse_x = mouse.getMouseX();
			final int mouse_y = mouse.getMouseY();
			final int mouse_x2 = mouse.getMousePressX();
			final int mouse_y2 = mouse.getMousePressY();
			final long mpt = System.currentTimeMillis()
					- mouse.getMousePressTime();
			if (mouse.getMousePressTime() == -1 || mpt >= 1000) {
				g.drawImage(normal, mouse_x - 8, mouse_y - 8, null);
			}
			if (mpt < 1000) {
				g.drawImage(clicked, mouse_x2 - 8, mouse_y2 - 8, null);
				g.drawImage(normal, mouse_x - 8, mouse_y - 8, null);
			}
		}

		if (getNearestFreeNPCByID(experimentID) != null) {
			if (tileOnScreen(getNearestFreeNPCByID(experimentID).getLocation())) {
				overlayTile(g, getNearestFreeNPCByID(experimentID)
						.getLocation(), new Color(200, 0, 0, 75));
			}
			if (tileOnMap(getNearestFreeNPCByID(experimentID).getLocation())) {
				g.setColor(new Color(0, 255, 0, 255));
				g.fillOval(tileToMinimap(getNearestFreeNPCByID(experimentID)
						.getLocation()).x - 3,
						tileToMinimap(getNearestFreeNPCByID(experimentID)
								.getLocation()).y - 3, 6, 6);
			}
			if (getNearestObjectByID(randomBooth) != null) {
				if (tileOnScreen(getNearestObjectByID(randomBooth)
						.getLocation())) {
					overlayTile(g, getNearestObjectByID(randomBooth)
							.getLocation(), new Color(255, 255, 0, 75));
				}
				if (tileOnMap(getNearestObjectByID(randomBooth).getLocation())) {
					g.setColor(new Color(255, 255, 0, 75));
					g.fillOval(tileToMinimap(getNearestObjectByID(randomBooth)
							.getLocation()).x - 3,
							tileToMinimap(getNearestObjectByID(randomBooth)
									.getLocation()).y - 1, 2, 2);
				}
			}
		}
	}

	// Credits to Garrent for his paint

	class GarrettsPaint {

		final Rectangle r = new Rectangle(7, 345, 408, 114);
		final Rectangle r1 = new Rectangle(420, 345, 77, 25);
		final Rectangle r2 = new Rectangle(420, 374, 77, 26);
		final Rectangle r3 = new Rectangle(420, 404, 77, 26);
		final Rectangle r4 = new Rectangle(420, 434, 77, 25);
		final Rectangle r2c = new Rectangle(415, 374, 5, 26);
		final Rectangle r3c = new Rectangle(415, 404, 5, 26);
		final Rectangle r4c = new Rectangle(415, 434, 5, 25);
		final Rectangle sb1 = new Rectangle(12, 350, 398, 12);
		final Rectangle sb2 = new Rectangle(12, 363, 398, 12);
		final Rectangle sb3 = new Rectangle(12, 376, 398, 12);
		final Rectangle sb4 = new Rectangle(12, 389, 398, 12);
		final Rectangle sb5 = new Rectangle(12, 402, 398, 12);
		final Rectangle sb6 = new Rectangle(12, 415, 398, 12);
		final Rectangle sb7 = new Rectangle(12, 428, 398, 12);
		final Rectangle sb8 = new Rectangle(12, 441, 398, 12);
		final Rectangle sb1s = new Rectangle(12, 350, 196, 12);
		final Rectangle sb2s = new Rectangle(12, 363, 196, 12);
		final Rectangle sb3s = new Rectangle(12, 376, 196, 12);
		final Rectangle sb4s = new Rectangle(12, 389, 196, 12);
		final Rectangle sb5s = new Rectangle(12, 402, 196, 12);
		final Rectangle sb6s = new Rectangle(12, 415, 196, 12);
		final Rectangle sb7s = new Rectangle(12, 428, 196, 12);
		final Rectangle sb8s = new Rectangle(12, 441, 196, 12);
		final Rectangle sb9s = new Rectangle(213, 350, 196, 12);
		final Rectangle sb10s = new Rectangle(213, 363, 196, 12);
		final Rectangle sb11s = new Rectangle(213, 376, 196, 12);
		final Rectangle sb12s = new Rectangle(213, 389, 196, 12);
		final Rectangle sb13s = new Rectangle(213, 402, 196, 12);
		final Rectangle sb14s = new Rectangle(213, 415, 196, 12);
		final Rectangle sb15s = new Rectangle(213, 428, 196, 12);
		final Rectangle sb16s = new Rectangle(213, 441, 196, 12);
		Rectangle[] skillBars = new Rectangle[]{sb1, sb2, sb3, sb4, sb5, sb6,
				sb7, sb8};
		boolean savedStats = false;
		boolean scriptRunning = false;
		boolean checkedCount = false;
		int currentTab = 0;
		int lastTab = 0;
		int[] barIndex = new int[16];
		int[] start_exp = null;
		int[] start_lvl = null;
		int[] gained_exp = null;
		int[] gained_lvl = null;

		Thread mouseWatcher = new Thread();
		final NumberFormat nf = NumberFormat.getInstance();

		final long time_ScriptStart = System.currentTimeMillis();
		long runTime = System.currentTimeMillis() - time_ScriptStart;

		int sine = 0;
		int sineM = 1;

		void paint(final Graphics g) {
			if (!isLoggedIn() || !scriptRunning)
				return;

			// credits to Jacmob for the pulsing
			if (sine >= 84) {
				sine = 84;
				sineM *= -1;
			} else if (sine <= 1) {
				sine = 1;
				sineM *= -1;
			}
			sine += sineM;

			runTime = System.currentTimeMillis() - time_ScriptStart;
			final String formattedTime = formatTime((int) runTime);

			currentTab = paintTab();

			switch (currentTab) {
				case -1: // PAINT OFF
					g.setColor(new Color(0, 0, 0, 150));
					g.fillRect(r1.x, r1.y, r1.width, r1.height);
					g.setColor(Color.WHITE);
					drawString(g, "Show Paint", r1, 5);
					break;
				case 0: // DEFAULT TAB - MAIN
					drawPaint(g, r2c);
					g.setColor(new Color(100, 100, 100, 200));
					g.drawLine(r.x + 204, r.y + 22, r.x + 204, r.y + 109);
					g.setColor(Color.WHITE);
					g.setFont(new Font("sansserif", Font.BOLD, 14));
					drawString(g, properties.name() + " v" + properties.version(),
							r, -40);
					g.setFont(new Font("sansserif", Font.PLAIN, 12));

					drawStringMain(g, "Runtime: ", formattedTime, r, 20, 35, 0,
							true);
					long millis = System.currentTimeMillis() - startTime;
					long hours = millis / (1000 * 60 * 60);
					millis -= hours * (1000 * 60 * 60);
					long minutes = millis / (1000 * 60);
					millis -= minutes * (1000 * 60);
					long seconds = millis / 1000;
					final int XPgained;
					if (startAtkExp == 0) {
						startAtkExp = skills.getCurrentSkillExp(STAT_ATTACK);
					}
					if (startStrExp == 0) {
						startStrExp = skills.getCurrentSkillExp(STAT_STRENGTH);
					}
					if (startDefExp == 0) {
						startDefExp = skills.getCurrentSkillExp(STAT_DEFENSE);
					}
					if (startRangedExp == 0) {
						startRangedExp = skills.getCurrentSkillExp(STAT_RANGE);
					}
					if (startHpExp == 0) {
						startHpExp = skills.getCurrentSkillExp(STAT_HITPOINTS);
					}
					XPgained = (atkExp - startAtkExp) + (strExp - startStrExp)
							+ (defExp - startDefExp) + (rangedExp - startRangedExp);
					atkGained = (atkExp - startAtkExp);
					strGained = (strExp - startStrExp);
					rgeGained = (defExp - startDefExp);
					hpGained = (hpExp - startHpExp);
					experimentKilled = (XPgained / 400);
					final int xpHour = ((int) ((3600000.0 / runTime) * XPgained));
					experimentPerHour = (xpHour / 400);
					atkExp = skills.getCurrentSkillExp(STAT_ATTACK);
					strExp = skills.getCurrentSkillExp(STAT_STRENGTH);
					defExp = skills.getCurrentSkillExp(STAT_DEFENSE);
					rangedExp = skills.getCurrentSkillExp(STAT_RANGE);
					hpExp = skills.getCurrentSkillExp(STAT_HITPOINTS);
					float xpsec = 0;
					if ((minutes > 0 || hours > 0 || seconds > 0) && XPgained > 0) {
						xpsec = ((float) XPgained)
								/ (float) (seconds + (minutes * 60) + (hours * 60 * 60));
					}
					float xpmin = xpsec * 60;
					float xphour = xpmin * 60;
					drawStringMain(g, "Experiments Raped: ", Integer
							.toString(experimentKilled), r, 20, 35, 2, true);
					drawStringMain(g, "Experiments Kills/ Hour: ", Integer
							.toString(experimentPerHour), r, 20, 35, 2, false);
					drawStringMain(g, "Experiments Xp / Hour: ", Integer
							.toString((int) xphour), r, 20, 35, 3, true);
					drawStringMain(g, "Total XP Gained: ", Integer
							.toString(XPgained), r, 20, 35, 3, false);
					drawStringMain(g, "Status: ", Status, r, 20, 35, 4, true);
					drawStringMain(g, "Location: ", Location, r, 20, 35, 4, false);
					break;
				case 1: // INFO
					drawPaint(g, r3c);
					g.setColor(new Color(100, 100, 100, 200));
					g.drawLine(r.x + 204, r.y + 22, r.x + 204, r.y + 109);
					g.setColor(Color.WHITE);
					g.setFont(new Font("sansserif", Font.BOLD, 14));
					drawString(g, properties.name(), r, -40);
					g.setFont(new Font("sansserif", Font.PLAIN, 12));
					drawStringMain(g, "Version: ", Double.toString(properties
							.version()), r, 20, 35, 0, true);
					break;
				case 2: // STATS
					drawPaint(g, r4c);
					drawStats(g);
					hoverMenu(g);
					break;
			}
		}

		void saveStats() {
			nf.setMinimumIntegerDigits(2);
			final String[] stats = Skills.statsArray;
			start_exp = new int[stats.length];
			start_lvl = new int[stats.length];
			for (int i = 0; i < stats.length; i++) {
				start_exp[i] = skills.getCurrentSkillExp(i);
				start_lvl[i] = skills.getCurrentSkillLevel(i);
			}
			for (int i = 0; i < barIndex.length; i++) {
				barIndex[i] = -1;
			}
			savedStats = true;
		}

		int paintTab() {
			final Point mouse = new Point(Bot.getClient().getMouse().x, Bot
					.getClient().getMouse().y);
			if (mouseWatcher.isAlive())
				return currentTab;
			if (r1.contains(mouse)) {
				mouseWatcher = new Thread(new MouseWatcher(r1));
				mouseWatcher.start();
				if (currentTab == -1)
					return lastTab;
				else {
					lastTab = currentTab;
					return -1;
				}
			}
			if (currentTab == -1)
				return currentTab;
			if (r2.contains(mouse))
				return 0;
			if (r3.contains(mouse))
				return 1;
			if (r4.contains(mouse))
				return 2;
			return currentTab;
		}

		void drawPaint(final Graphics g, final Rectangle rect) {
			g.setColor(new Color(0, 0, 0, 230));
			g.fillRect(r1.x, r1.y, r1.width, r1.height);
			g.fillRect(r2.x, r2.y, r2.width, r2.height);
			g.fillRect(r3.x, r3.y, r3.width, r3.height);
			g.fillRect(r4.x, r4.y, r4.width, r4.height);
			g.fillRect(rect.x, rect.y, rect.width, rect.height);
			g.fillRect(r.x, r.y, r.width, r.height);
			g.setColor(Color.WHITE);
			drawString(g, "Hide Paint", r1, 5);
			drawString(g, "MAIN", r2, 5);
			drawString(g, "INFO", r3, 5);
			drawString(g, "STATS", r4, 5);
			g.setColor(new Color(0, 0, 0, 230));
		}

		void drawStat(final Graphics g, final int index, final int count) {
			if (count >= skillBars.length && !checkedCount) {
				skillBars = new Rectangle[]{sb1s, sb2s, sb3s, sb4s, sb5s,
						sb6s, sb7s, sb8s, sb9s, sb10s, sb11s, sb12s, sb13s,
						sb14s, sb15s, sb16s};
				checkedCount = true;
			}
			if (count >= skillBars.length)
				return;
			g.setFont(new Font("serif", Font.PLAIN, 11));
			g.setColor(new Color(100, 100, 100, 150));
			g.fillRect(skillBars[count].x, skillBars[count].y,
					skillBars[count].width, skillBars[count].height);
			final int percent = skills.getPercentToNextLevel(index);
			g.setColor(new Color(255 - 2 * percent,
					(int) (1.7 * percent + sine), 0, 150));
			g.fillRect(skillBars[count].x, skillBars[count].y,
					(int) ((skillBars[count].width / 100.0) * percent),
					skillBars[count].height);
			g.setColor(Color.WHITE);
			final String name = Skills.statsArray[index];
			final String capitalized = name.substring(0, 1).toUpperCase()
					+ name.substring(1);
			g.drawString(capitalized, skillBars[count].x + 2,
					skillBars[count].y + 10);
			drawStringEnd(g, percent + "%", skillBars[count], -2, 4);
			barIndex[count] = index;
		}

		void drawStats(final Graphics g) {
			final String[] stats = Skills.statsArray;
			int count = 0;
			gained_exp = new int[stats.length];
			gained_lvl = new int[stats.length];
			for (int i = 0; i < stats.length; i++) {
				gained_exp[i] = skills.getCurrentSkillExp(i) - start_exp[i];
				gained_lvl[i] = skills.getCurrentSkillLevel(i) - start_lvl[i];
				if (gained_exp[i] > 0) {
					drawStat(g, i, count);
					count++;
				}
			}
		}

		void hoverMenu(final Graphics g) {
			final Point mouse = new Point(Bot.getClient().getMouse().x, Bot
					.getClient().getMouse().y);
			final Rectangle r_main = new Rectangle(mouse.x, mouse.y - 150, 300,
					150);
			for (int i = 0; i < barIndex.length; i++) {
				if (barIndex[i] > -1) {
					if (skillBars[i].contains(mouse)) {
						final int xpTL = skills.getXPToNextLevel(barIndex[i]);
						final int xpHour = ((int) ((3600000.0 / runTime) * gained_exp[barIndex[i]]));
						final int TTL = (int) (((double) xpTL / (double) xpHour) * 3600000);
						g.setColor(new Color(50, 50, 50, 240));
						g.fillRect(r_main.x, r_main.y, r_main.width,
								r_main.height);
						g.setColor(Color.WHITE);
						g.setFont(new Font("sansserif", Font.BOLD, 15));
						drawString(g, Skills.statsArray[barIndex[i]]
								.toUpperCase(), r_main, -58);
						g.setFont(new Font("sansserif", Font.PLAIN, 12));
						hoverDrawString(g, "Current Level: ", skills
								.getCurrentSkillLevel(barIndex[i])
								+ "", r_main, 40, 0);
						hoverDrawString(g, "XP Gained: ",
								gained_exp[barIndex[i]] + "xp", r_main, 40, 1);
						hoverDrawString(g, "XP / Hour: ", xpHour + "xp",
								r_main, 40, 2);
						hoverDrawString(g, "LVL Gained: ",
								gained_lvl[barIndex[i]] + " lvls", r_main, 40,
								3);
						hoverDrawString(g, "XPTL: ", xpTL + "xp", r_main, 40, 4);
						hoverDrawString(g, "TTL: ", formatTime(TTL), r_main,
								40, 5);
					}
				}
			}
		}

		void hoverDrawString(final Graphics g, final String str,
							 final String val, final Rectangle rect, final int offset,
							 final int index) {
			g.setColor(Color.WHITE);
			final FontMetrics font = g.getFontMetrics();
			final Rectangle2D bounds = font.getStringBounds(val, g);
			final int width = (int) bounds.getWidth();
			final int y = rect.y + offset + (20 * index);
			g.drawString(str, rect.x + 5, y);
			g.drawString(val, (rect.x + rect.width) - width - 5, y);
			if (index < 5) {
				g.setColor(new Color(100, 100, 100, 200));
				g.drawLine(rect.x + 5, y + 5, rect.x + rect.width - 5, y + 5);
			}
		}

		void drawString(final Graphics g, final String str,
						final Rectangle rect, final int offset) {
			final FontMetrics font = g.getFontMetrics();
			final Rectangle2D bounds = font.getStringBounds(str, g);
			final int width = (int) bounds.getWidth();
			g.drawString(str, rect.x + ((rect.width - width) / 2), rect.y
					+ ((rect.height / 2) + offset));
		}

		void drawStringEnd(final Graphics g, final String str,
						   final Rectangle rect, final int xOffset, final int yOffset) {
			final FontMetrics font = g.getFontMetrics();
			final Rectangle2D bounds = font.getStringBounds(str, g);
			final int width = (int) bounds.getWidth();
			g.drawString(str, (rect.x + rect.width) - width + xOffset, rect.y
					+ ((rect.height / 2) + yOffset));
		}

		void drawStringMain(final Graphics g, final String str,
							final String val, final Rectangle rect, final int xOffset,
							final int yOffset, final int index, final boolean leftSide) {
			final FontMetrics font = g.getFontMetrics();
			final Rectangle2D bounds = font.getStringBounds(val, g);
			final int indexMult = 17;
			final int width = (int) bounds.getWidth();
			if (leftSide) {
				g.drawString(str, rect.x + xOffset, rect.y + yOffset
						+ (index * indexMult));
				g.drawString(val, rect.x + (rect.width / 2) - width - xOffset,
						rect.y + yOffset + (index * indexMult));
			} else {
				g.drawString(str, rect.x + (rect.width / 2) + xOffset, rect.y
						+ yOffset + (index * indexMult));
				g.drawString(val, rect.x + rect.width - width - xOffset, rect.y
						+ yOffset + (index * indexMult));
			}
		}

		String formatTime(final int milliseconds) {
			final long t_seconds = milliseconds / 1000;
			final long t_minutes = t_seconds / 60;
			final long t_hours = t_minutes / 60;
			final int seconds = (int) (t_seconds % 60);
			final int minutes = (int) (t_minutes % 60);
			final int hours = (int) (t_hours % 60);
			return (nf.format(hours) + ":" + nf.format(minutes) + ":" + nf
					.format(seconds));
		}

		class MouseWatcher implements Runnable {

			Rectangle rect = null;

			MouseWatcher(final Rectangle rect) {
				this.rect = rect;
			}

			public void run() {
				Point mouse = new Point(Bot.getClient().getMouse().x, Bot
						.getClient().getMouse().y);
				while (rect.contains(mouse)) {
					try {
						mouse = new Point(Bot.getClient().getMouse().x, Bot
								.getClient().getMouse().y);
						Thread.sleep(50);
					} catch (Exception e) {
					}
				}
			}
		}

	}

	class AStar { // credits to jacmob for the walking web method :) <3

		private class Node {

			public int x, y;
			public Node parent;
			public double g, f;

			public Node(final int x, final int y) {
				this.x = x;
				this.y = y;
				g = f = 0;
			}

			public boolean isAt(final Node another) {
				return x == another.x && y == another.y;
			}

			public RSTile toRSTile(final int baseX, final int baseY) {
				return new RSTile(x + baseX, y + baseY);
			}

		}

		private int[][] blocks;

		public AStar() {

		}

		private Node cheapestNode(final ArrayList<Node> open) {
			Node c = null;
			for (final Node t : open) {
				if (c == null || t.f < c.f) {
					c = t;
				}
			}
			return c;
		}

		private double diagonalHeuristic(final Node current, final Node end) {
			final double dx = Math.abs(current.x - end.x);
			final double dy = Math.abs(current.y - end.y);
			final double diag = Math.min(dx, dy);
			final double straight = dx + dy;
			return Math.sqrt(2.0) * diag + straight - 2 * diag;
		}

		public RSTile[] findPath(final RSTile cur, final RSTile dest) {
			final int baseX = Bot.getClient().getBaseX(), baseY = Bot
					.getClient().getBaseY();
			final int currX = cur.getX() - baseX, currY = cur.getY() - baseY;
			final int destX = dest.getX() - baseX, destY = dest.getY() - baseY;
			if (currX < 0 || currY < 0 || currX > 103 || currY > 103
					|| destX < 0 || destY < 0 || destX > 103 || destY > 103)
				return null;
			final ArrayList<Node> closed = new ArrayList<Node>(), open = new ArrayList<Node>();
			blocks = Bot.getClient().getRSGroundDataArray()[Bot.getClient()
					.getPlane()].getBlocks();
			Node current = new Node(currX, currY);
			final Node destination = new Node(destX, destY);
			open.add(current);
			while (open.size() > 0) {
				current = cheapestNode(open);
				closed.add(current);
				open.remove(open.indexOf(current));
				for (final Node n : getSurroundingWalkableNodes(current)) {
					if (!isIn(closed, n)) {
						if (!isIn(open, n)) {
							n.parent = current;
							n.g = current.g + getAdditionalCost(n, current);
							n.f = n.g + diagonalHeuristic(n, destination);
							open.add(n);
						} else {
							final Node old = getNode(open, n);
							if (current.g + getAdditionalCost(old, current) < old.g) {
								old.parent = current;
								old.g = current.g
										+ getAdditionalCost(old, current);
								old.f = old.g
										+ diagonalHeuristic(old, destination);
							}
						}
					}
				}
				if (isIn(closed, destination))
					return getPath(closed.get(closed.size() - 1), baseX, baseY);
			}
			return null;
		}

		private double getAdditionalCost(final Node start, final Node end) {
			double cost = 1.0;
			if (!(start.x == end.y) || start.x == end.y) {
				cost = Math.sqrt(2.0);
			}
			return cost;
		}

		private Node getNode(final ArrayList<Node> nodes, final Node key) {
			for (final Node n : nodes) {
				if (n.isAt(key))
					return n;
			}
			return null;
		}

		private RSTile[] getPath(final Node endNode, final int baseX,
								 final int baseY) {
			final ArrayList<RSTile> reversePath = new ArrayList<RSTile>();
			Node p = endNode;
			while (p.parent != null) {
				reversePath.add(p.toRSTile(baseX, baseY));
				final int next = (int) (Math.random() * 4 + 5);
				for (int i = 0; i < next && p.parent != null; i++) {
					p = p.parent;
				}
			}
			final RSTile[] fixedPath = new RSTile[reversePath.size()];
			for (int i = 0; i < fixedPath.length; i++) {
				fixedPath[i] = reversePath.get(fixedPath.length - 1 - i);
			}
			return fixedPath;
		}

		private ArrayList<Node> getSurroundingWalkableNodes(final Node t) {
			final ArrayList<Node> tiles = new ArrayList<Node>();
			final int curX = t.x, curY = t.y;
			if (curX > 0 && curY < 103
					&& (blocks[curX - 1][curY + 1] & 0x1280138) == 0
					&& (blocks[curX - 1][curY] & 0x1280108) == 0
					&& (blocks[curX][curY + 1] & 0x1280120) == 0) {
				tiles.add(new Node(curX - 1, curY + 1));
			}
			if (curY < 103 && (blocks[curX][curY + 1] & 0x1280120) == 0) {
				tiles.add(new Node(curX, curY + 1));
			}
			if (curX > 0 && curY < 103
					&& (blocks[curX - 1][curY + 1] & 0x1280138) == 0
					&& (blocks[curX - 1][curY] & 0x1280108) == 0
					&& (blocks[curX][curY + 1] & 0x1280120) == 0) {
				tiles.add(new Node(curX + 1, curY + 1));
			}
			if (curX > 0 && (blocks[curX - 1][curY] & 0x1280108) == 0) {
				tiles.add(new Node(curX - 1, curY));
			}
			if (curX < 103 && (blocks[curX + 1][curY] & 0x1280180) == 0) {
				tiles.add(new Node(curX + 1, curY));
			}
			if (curX > 0 && curY > 0
					&& (blocks[curX - 1][curY - 1] & 0x128010e) == 0
					&& (blocks[curX - 1][curY] & 0x1280108) == 0
					&& (blocks[curX][curY - 1] & 0x1280102) == 0) {
				tiles.add(new Node(curX - 1, curY - 1));
			}
			if (curY > 0 && (blocks[curX][curY - 1] & 0x1280102) == 0) {
				tiles.add(new Node(curX, curY - 1));
			}
			if (curX < 103 && curY > 0
					&& (blocks[curX + 1][curY - 1] & 0x1280183) == 0
					&& (blocks[curX + 1][curY] & 0x1280180) == 0
					&& (blocks[curX][curY - 1] & 0x1280102) == 0) {
				tiles.add(new Node(curX + 1, curY - 1));
			}
			return tiles;
		}

		private boolean isIn(final ArrayList<Node> nodes, final Node key) {
			return getNode(nodes, key) != null;
		}
	}

	private class CameraRotateThread extends Thread {

		@Override
		public void run() {
			char LR = KeyEvent.VK_RIGHT;
			if (autoRandom(0, 2) == 0) {
				LR = KeyEvent.VK_LEFT;
			}
			Bot.getInputManager().pressKey(LR);
			try {
				Thread.sleep(autoRandom(100, 3000));
			} catch (final Exception ignored) {
			}
			Bot.getInputManager().releaseKey(LR);
		}
	}

	private class CameraHeightThread extends Thread {

		@Override
		public void run() {
			char UD = KeyEvent.VK_UP;
			if (autoRandom(0, 2) == 0) {
				UD = KeyEvent.VK_DOWN;
			}
			Bot.getInputManager().pressKey(UD);
			try {
				Thread.sleep(autoRandom(100, 2000));
			} catch (final Exception ignored) {
			}
			Bot.getInputManager().releaseKey(UD);
		}
	}

	private class ExperimentSlayerChatResponder extends Thread {
		@Override
		public void run() {
			while (getChatMessage() == null) {
				try {
					Thread.sleep(200);
				} catch (Exception ignored) {
				}
			}
			while (getChatMessage() != null) {
				String m = getChatMessage();
				if (m != null && !m.equals(lastMessage)) {
					if (getChatMessage().toLowerCase().contains(
							getMyPlayer().getName())
							|| getChatMessage().toLowerCase().contains("bot")
							|| getChatMessage().toLowerCase().contains("hello")) {
						try {
							String sentText = getResponse(getChatMessage()
									.toLowerCase());
							if (sentText != null) {
								sendText(sentText, true);
								log("Attempted to send: " + sentText);
							}
						} catch (InterruptedException Ignored) {
						}

					}
					if (containsSkill(m)) { // checks if they asked for skill
						if (m.toLowerCase().contains("slay")
								|| m.toLowerCase().contains("slayer")) {
							sendText(""
									+ skills.getCurrentSkillLevel(STAT_SLAYER),
									true);
						}
						if (m.toLowerCase().contains("att")
								|| m.toLowerCase().contains("attack")) {
							sendText(""
									+ skills.getCurrentSkillLevel(STAT_ATTACK),
									true);
						}
						if (m.toLowerCase().contains("str")
								|| m.toLowerCase().contains("strength")) {
							sendText(
									""
											+ skills
											.getCurrentSkillLevel(STAT_STRENGTH),
									true);
						}
						if (m.toLowerCase().contains("def")
								|| m.toLowerCase().contains("defense")
								|| m.toLowerCase().contains("defence")) {
							sendText(
									""
											+ skills
											.getCurrentSkillLevel(STAT_DEFENSE),
									true);
						}
						if (m.toLowerCase().contains("range")
								|| m.toLowerCase().contains("ranged")) {
							sendText(""
									+ skills.getCurrentSkillLevel(STAT_RANGE),
									true);
						}
						if (m.toLowerCase().contains("hp")
								|| m.toLowerCase().contains("hitpoint")) {
							sendText(
									""
											+ skills
											.getCurrentSkillLevel(STAT_HITPOINTS),
									true);
						}
						if (m.toLowerCase().contains("pray")
								|| m.toLowerCase().contains("prey")
								|| m.toLowerCase().contains("prayer")) {
							sendText(""
									+ skills.getCurrentSkillLevel(STAT_PRAYER),
									true);
						}
						if (m.toLowerCase().contains("mage")
								|| m.toLowerCase().contains("magic")) {
							sendText(""
									+ skills.getCurrentSkillLevel(STAT_MAGIC),
									true);
						}
					}
					lastMessage = m;
				}
				try {
					Thread.sleep(200);
				} catch (Exception ignored) {

				}
			}
		}
	}

	public class ExperimentSlayerGUI extends JFrame {

		private static final long serialVersionUID = 1L;

		public ExperimentSlayerGUI() {
			initComponents();
		}

		private void endButtonActionPerformed(ActionEvent e) {
			guiWait = false;
			guiExit = true;
			dispose();
		}

		private void startButtonActionPerformed(ActionEvent e) {

			startTime = System.currentTimeMillis();
			Status = "Setting up Gui";

			doSpec = checkBox10.isSelected();
			Banking = checkBox13.isSelected();
			chatResponder = checkBox14.isSelected();
			getBones = checkBox9.isSelected();

			if (attackcomboBox1.getSelectedItem().equals("Attack")) {
				attackStyle = 0;
			} else if (attackcomboBox1.getSelectedItem().equals("Strength")) {
				attackStyle = 1;
			} else if (attackcomboBox1.getSelectedItem().equals("Defence")) {
				attackStyle = 2;
			} else if (attackcomboBox1.getSelectedItem().equals("Range")) {
				attackStyle = 3;
				if (inventoryContains(avaBag)) {
					clickInventoryItem(avaBag, "Wear");
				}
			}

			if (!textField1.getText().equals("None")) {
				levelToGo = Integer.parseInt(textField1.getText());
				String temp2 = attackcomboBox1.getActionCommand();
				skillToCheck = getSkill(temp2);
				log("Going to Level: " + levelToGo + " with skill: " + temp2);
				log("Current " + temp2 + " level is: "
						+ skills.getRealSkillLevel(skillToCheck));
				if (textField1.getText() == null) {
					log.severe("No Level typed in!");
					log("If do not wish to use, leave value as None");
				}
				if (!textField2.getText().isEmpty()) {
					speed = Integer.parseInt(textField2.getText());
					log("Getting MouseSpeed at " + random(speed - 1, speed + 1));
					if (textField2.getText() == null) {
						log("Invalid MouseSpeed");
						log("Please leave the value in this box default");
						log("If you do not know what to do here");
					}

					if (foodcomboBox3.getSelectedItem().equals("None")) {
						Banking = false;
					} else if (foodcomboBox3.getSelectedItem().equals("Cake")) {
						Food = 1891;
						foodName = "Cake";
						Status = "Eating Cake";
					} else if (foodcomboBox3.getSelectedItem().equals(
							"Chocolate cake")) {
						Food = 1897;
						foodName = "Chocolate cake";
						Status = "Eating Chocolate cake";
					} else if (foodcomboBox3.getSelectedItem().equals(
							"Plain pizza")) {
						Food = 2289;
						foodName = "Plain pizza";
						Status = "Eating Plain pizza";
					} else if (foodcomboBox3.getSelectedItem().equals(
							"Pineapple pizza")) {
						Food = 2301;
						foodName = "Pineapple pizza";
						Status = "Eating Pineapple pizza";
					} else if (foodcomboBox3.getSelectedItem().equals(
							"Meat pizza")) {
						Food = 2293;
						foodName = "Meat pizza";
						Status = "Eating Meat pizza";
					} else if (foodcomboBox3.getSelectedItem()
							.equals("Lobster")) {
						Food = 379;
						foodName = "Lobster";
						Status = "Eating Lobster";
					} else if (foodcomboBox3.getSelectedItem().equals("Salmon")) {
						Food = 329;
						foodName = "Salmon";
						Status = "Eating Salmon";
					} else if (foodcomboBox3.getSelectedItem().equals("Tuna")) {
						Food = 361;
						foodName = "Tuna";
						Status = "Eating Tuna";
					} else if (foodcomboBox3.getSelectedItem().equals("Trout")) {
						Food = 333;
						foodName = "Trout";
						Status = "Eating Trout";
					} else if (foodcomboBox3.getSelectedItem().equals("Sharks")) {
						Food = 385;
						foodName = "Sharks";
						Status = "Eating Sharks";
					} else if (foodcomboBox3.getSelectedItem().equals(
							"Monkfish")) {
						Food = 7946;
						foodName = "Monkfish";
						Status = "Eating Monkfish";
					} else if (foodcomboBox3.getSelectedItem().equals(
							"Manta ray")) {
						Food = 391;
						foodName = "Manta ray";
						Status = "Eating Manta ray";
					} else if (foodcomboBox3.getSelectedItem().equals(
							"Sea turtle")) {
						Food = 397;
						foodName = "Sea turtle";
						Status = "Eating Sea turtle";
					} else if (foodcomboBox3.getSelectedItem().equals(
							"Swordfish")) {
						Food = 373;
						foodName = "Swordfish";
						Status = "Eating Swordfish";
					} else if (foodcomboBox3.getSelectedItem().equals(
							"Rocktail")) {
						Food = 15272;
						foodName = "Rocktail";
						Status = "Eating Rocktail";
					}
					if (checkBox10.isSelected()) {
						doSpec = true;
						log("Special Attacks Enabled");
					} else {
						log("Not doing Spec");
					}
					if (checkBox13.isSelected()) {
						Banking = true;
						getBones = false;
					}
					if (checkBox14.isSelected()) {
						chatResponder = true;
					}
					if (checkBox9.isSelected()) {
						Bones = 526;
						getBones = true;
						Banking = false;
					}
					if (arrowcomboBox2.getSelectedItem()
							.equals("Bronze arrows")) {
						arrowID = bronzeArrow;
						arrowName = "Bronze arrow";
					} else if (arrowcomboBox2.getSelectedItem().equals(
							"Iron arrows")) {
						arrowID = ironArrow;
						arrowName = "Iron arrow";
					} else if (arrowcomboBox2.getSelectedItem().equals(
							"Steel Arrow")) {
						arrowID = steelArrow;
						arrowName = "Steel arrow";
					} else if (arrowcomboBox2.getSelectedItem().equals(
							"Mithril Arrow")) {
						arrowID = mithrilArrow;
						arrowName = "Mithril arrow";
					} else if (arrowcomboBox2.getSelectedItem().equals(
							"Adamant Arrow")) {
						arrowID = adamantArrow;
						arrowName = "Adamant arrow";
					} else if (arrowcomboBox2.getSelectedItem().equals(
							"Rune Arrow")) {
						arrowID = runeArrow;
						arrowName = "Rune arrow";
					} else if (arrowcomboBox2.getSelectedItem().equals(
							"Bronze Bolt")) {
						arrowID = bronzeBolt;
						arrowName = "Bronze bolts";
					} else if (arrowcomboBox2.getSelectedItem().equals(
							"Bluerite Bolt")) {
						arrowID = blueriteBolt;
						arrowName = "Bluerite bolts";
					} else if (arrowcomboBox2.getSelectedItem().equals(
							"Bone Bolt")) {
						arrowID = boneBolt;
						arrowName = "Bone bolts";
					} else if (arrowcomboBox2.getSelectedItem().equals(
							"Iron Bolt")) {
						arrowID = ironBolt;
						arrowName = "Iron bolts";
					} else if (arrowcomboBox2.getSelectedItem().equals(
							"Steel Bolt")) {
						arrowID = steelBolt;
						arrowName = "Steel bolts";
					} else if (arrowcomboBox2.getSelectedItem().equals(
							"Black Bolt")) {
						arrowID = blackBolt;
						arrowName = "Bronze bolts";
					} else if (arrowcomboBox2.getSelectedItem().equals(
							"Mithril Bolt")) {
						arrowID = mithrilBolt;
						arrowName = "Mithril bolts";
					} else if (arrowcomboBox2.getSelectedItem().equals(
							"Adamant Bolt")) {
						arrowID = adamantBolt;
						arrowName = "Adamant bolts";
					} else if (arrowcomboBox2.getSelectedItem().equals(
							"Rune Bolt")) {
						arrowID = runeBolt;
						arrowName = "Rune bolts";
					} else if (arrowcomboBox2.getSelectedItem().equals(
							"Broad Bolt")) {
						arrowID = broadBolt;
						arrowName = "Broad bolts";
					} else if (arrowcomboBox2.getSelectedItem().equals(
							"Bronze Knife")) {
						arrowID = bronzeKnife;
						arrowName = "Bronze knife";
					} else if (arrowcomboBox2.getSelectedItem().equals(
							"Iron Knife")) {
						arrowID = ironKnife;
						arrowName = "Iron knife";
					} else if (arrowcomboBox2.getSelectedItem().equals(
							"Steel Knife")) {
						arrowID = steelKnife;
						arrowName = "Steel knife";
					} else if (arrowcomboBox2.getSelectedItem().equals(
							"Black Knife")) {
						arrowID = blackKnife;
						arrowName = "Black knife";
					} else if (arrowcomboBox2.getSelectedItem().equals(
							"Mithril Knife")) {
						arrowID = mithrilKnife;
						arrowName = "Mithril knife";
					} else if (arrowcomboBox2.getSelectedItem().equals(
							"Adamant Knife")) {
						arrowID = adamantKnife;
						arrowName = "Adamant knife";
					} else if (arrowcomboBox2.getSelectedItem().equals(
							"Rune Knife")) {
						arrowID = runeKnife;
						arrowName = "Rune knife";
					} else if (arrowcomboBox2.getSelectedItem().equals(
							"Bronze Dart")) {
						arrowID = bronzeDart;
						arrowName = "Bronze dart";
					} else if (arrowcomboBox2.getSelectedItem().equals(
							"Iron Dart")) {
						arrowID = ironDart;
						arrowName = "Iron dart";
					} else if (arrowcomboBox2.getSelectedItem().equals(
							"Steel Dart")) {
						arrowID = steelDart;
						arrowName = "Steel dart";
					} else if (arrowcomboBox2.getSelectedItem().equals(
							"Black Dart")) {
						arrowID = blackDart;
						arrowName = "Black dart";
					} else if (arrowcomboBox2.getSelectedItem().equals(
							"Mithril Dart")) {
						arrowID = mithrilDart;
						arrowName = "Mithril dart";
					} else if (arrowcomboBox2.getSelectedItem().equals(
							"Adamant Dart")) {
						arrowID = adamantDart;
						arrowName = "Adamant dart";
					} else if (arrowcomboBox2.getSelectedItem().equals(
							"Rune Dart")) {
						arrowID = runeDart;
						arrowName = "Rune dart";
					}

					guiWait = false;
				}
			}
		}

		private void initComponents() {
			// GEN-BEGIN:initComponents
			attackLabel = new JLabel();
			arrowLabel = new JLabel();
			foodLabel = new JLabel();
			attackcomboBox1 = new JComboBox();
			arrowcomboBox2 = new JComboBox();
			foodcomboBox3 = new JComboBox();
			b2pLabel19 = new JLabel();
			specLabel20 = new JLabel();
			quickchatLabel21 = new JLabel();
			bankLabel22 = new JLabel();
			checkBox13 = new JCheckBox();
			checkBox14 = new JCheckBox();
			checkBox9 = new JCheckBox();
			checkBox10 = new JCheckBox();
			hpeatatLabel4 = new JLabel();
			textField1 = new JTextField();
			mousespeedLabel5 = new JLabel();
			textField2 = new JTextField();
			startButton = new JButton();
			endButton = new JButton();
			label1 = new JLabel();

			// ======== this ========
			setTitle("ExperimentSlayer Pro");
			setResizable(false);
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			Container contentPane = getContentPane();
			contentPane.setLayout(null);

			// ---- attackLabel ----
			attackLabel.setText("Attack Style:");
			attackLabel.setFont(new Font("Algerian", Font.PLAIN, 14));
			contentPane.add(attackLabel);
			attackLabel.setBounds(new Rectangle(new Point(25, 55), attackLabel
					.getPreferredSize()));

			// ---- arrowLabel ----
			arrowLabel.setText("Arrow Pickup:");
			arrowLabel.setFont(new Font("Algerian", Font.PLAIN, 14));
			contentPane.add(arrowLabel);
			arrowLabel.setBounds(new Rectangle(new Point(25, 85), arrowLabel
					.getPreferredSize()));

			// ---- foodLabel ----
			foodLabel.setText("Food Options:");
			foodLabel.setFont(new Font("Algerian", Font.PLAIN, 14));
			contentPane.add(foodLabel);
			foodLabel.setBounds(new Rectangle(new Point(25, 115), foodLabel
					.getPreferredSize()));

			// ---- attackcomboBox1 ----
			attackcomboBox1.setModel(new DefaultComboBoxModel(new String[]{
					"Attack", "Strength", "Defence", "Range"}));
			contentPane.add(attackcomboBox1);
			attackcomboBox1.setBounds(215, 55, 130, attackcomboBox1
					.getPreferredSize().height);

			// ---- arrowcomboBox2 ----
			arrowcomboBox2.setModel(new DefaultComboBoxModel(new String[]{
					"None", "Bronze Arrow", "Iron Arrow", "Steel Arrow",
					"Mithril Arrow", "Adamant Arrow", "Rune Arrow",
					"Bronze Bolt", "Bone Bolt", "Bluerite Bolt", "Iron Bolt",
					"Steel Bolt", "Black Bolt", "Mithril Bolt", "Adamant Bolt",
					"Rune Bolt", "Broad Bolt", "Bronze Knife", "Iron Knife",
					"Steel Knife", "Black Knife", "Mithril Knife",
					"Adamant Knife", "Rune Knife"}));
			contentPane.add(arrowcomboBox2);
			arrowcomboBox2.setBounds(215, 85, 130, arrowcomboBox2
					.getPreferredSize().height);

			// ---- foodcomboBox3 ----
			foodcomboBox3.setModel(new DefaultComboBoxModel(new String[]{
					"None", "Cake", "Chocolate cake", "Plain pizza",
					"Pineapple pizza", "Meat pizza", "Lobster", "Salmon",
					"Tuna", "Trout", "Sharks", "Monkfish", "Manta ray",
					"Sea turtle", "Swordfish", "Rocktail"}));
			contentPane.add(foodcomboBox3);
			foodcomboBox3.setBounds(215, 115, 130, foodcomboBox3
					.getPreferredSize().height);

			// ---- b2pLabel19 ----
			b2pLabel19.setText("Use Bones2peaches");
			b2pLabel19.setFont(new Font("Dialog", Font.BOLD | Font.ITALIC, 12));
			contentPane.add(b2pLabel19);
			b2pLabel19.setBounds(10, 245, 120,
					b2pLabel19.getPreferredSize().height);

			// ---- specLabel20 ----
			specLabel20.setText("Use Special Attacks");
			specLabel20
					.setFont(new Font("Dialog", Font.BOLD | Font.ITALIC, 12));
			contentPane.add(specLabel20);
			specLabel20.setBounds(10, 215, 120,
					specLabel20.getPreferredSize().height);

			// ---- quickchatLabel21 ----
			quickchatLabel21.setText("Use Chat Responder");
			quickchatLabel21.setFont(new Font("Dialog",
					Font.BOLD | Font.ITALIC, 12));
			contentPane.add(quickchatLabel21);
			quickchatLabel21.setBounds(200, 245, 125, quickchatLabel21
					.getPreferredSize().height);

			// ---- bankLabel22 ----
			bankLabel22.setText("GoTo Bank?");
			bankLabel22
					.setFont(new Font("Dialog", Font.BOLD | Font.ITALIC, 12));
			contentPane.add(bankLabel22);
			bankLabel22.setBounds(200, 215, 125,
					bankLabel22.getPreferredSize().height);

			// ---- checkBox13 ----
			checkBox13.setText("Yes");
			contentPane.add(checkBox13);
			checkBox13.setBounds(new Rectangle(new Point(340, 210), checkBox13
					.getPreferredSize()));

			// ---- checkBox14 ----
			checkBox14.setText("Yes");
			contentPane.add(checkBox14);
			checkBox14.setBounds(340, 240, 43, 23);

			// ---- checkBox9 ----
			checkBox9.setText("Yes");
			contentPane.add(checkBox9);
			checkBox9.setBounds(140, 240, 43, 23);

			// ---- checkBox10 ----
			checkBox10.setText("Yes");
			contentPane.add(checkBox10);
			checkBox10.setBounds(new Rectangle(new Point(140, 210), checkBox10
					.getPreferredSize()));

			// ---- hpeatatLabel4 ----
			hpeatatLabel4.setText("Lvl to stop at?");
			hpeatatLabel4.setFont(new Font("Engravers MT", Font.PLAIN, 12));
			contentPane.add(hpeatatLabel4);
			hpeatatLabel4.setBounds(25, 145,
					hpeatatLabel4.getPreferredSize().width, 20);

			// ---- textField1 ----
			textField1.setText("None");
			contentPane.add(textField1);
			textField1.setBounds(255, 145, 30,
					textField1.getPreferredSize().height);

			// ---- mousespeedLabel5 ----
			mousespeedLabel5.setText("Adjust Mouse Speed:");
			mousespeedLabel5.setFont(new Font("Engravers MT", Font.PLAIN, 12));
			contentPane.add(mousespeedLabel5);
			mousespeedLabel5.setBounds(10, 180, mousespeedLabel5
					.getPreferredSize().width, 20);

			// ---- textField2 ----
			textField2.setText("6");
			contentPane.add(textField2);
			textField2.setBounds(255, 180, 30,
					textField2.getPreferredSize().height);

			// ---- startButton ----
			startButton.setText("Start Script");
			startButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					startButtonActionPerformed(e);
				}
			});
			contentPane.add(startButton);
			startButton.setBounds(50, 285, 120, 35);

			// ---- endButton ----
			endButton.setText("End Script");
			endButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					endButtonActionPerformed(e);
				}
			});
			contentPane.add(endButton);
			endButton.setBounds(215, 285, 120, 35);

			// ---- label1 ----
			label1.setText("ExperimentSlayer Pro Settings");
			label1.setFont(new Font("Britannic Bold", Font.PLAIN, 24));
			contentPane.add(label1);
			label1.setBackground(new Color(102, 102, 102));
			label1.setBounds(new Rectangle(new Point(40, 10), label1
					.getPreferredSize()));

			{ // compute preferred size
				Dimension preferredSize = new Dimension();
				for (int i = 0; i < contentPane.getComponentCount(); i++) {
					Rectangle bounds = contentPane.getComponent(i).getBounds();
					preferredSize.width = Math.max(bounds.x + bounds.width,
							preferredSize.width);
					preferredSize.height = Math.max(bounds.y + bounds.height,
							preferredSize.height);
				}
				Insets insets = contentPane.getInsets();
				preferredSize.width += insets.right;
				preferredSize.height += insets.bottom;
				contentPane.setMinimumSize(preferredSize);
				contentPane.setPreferredSize(preferredSize);
			}
			setSize(400, 365);
			setLocationRelativeTo(getOwner());
			// GEN-END:initComponents
		}

		// GEN-BEGIN:variables
		private JLabel attackLabel;
		private JLabel arrowLabel;
		private JLabel foodLabel;
		private JComboBox attackcomboBox1;
		private JComboBox arrowcomboBox2;
		private JComboBox foodcomboBox3;
		private JLabel b2pLabel19;
		private JLabel specLabel20;
		private JLabel quickchatLabel21;
		private JLabel bankLabel22;
		private JCheckBox checkBox13;
		private JCheckBox checkBox14;
		private JCheckBox checkBox9;
		private JCheckBox checkBox10;
		private JLabel hpeatatLabel4;
		private JTextField textField1;
		private JLabel mousespeedLabel5;
		private JTextField textField2;
		private JButton startButton;
		private JButton endButton;
		private JLabel label1;
		// GEN-END:variables
	}
}