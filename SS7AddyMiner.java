import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import org.rsbot.bot.Bot;
import org.rsbot.bot.input.Mouse;
import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.gui.AccountManager;
import org.rsbot.script.Calculations;
import org.rsbot.script.Constants;
import org.rsbot.script.GrandExchange;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.randoms.antiban.LoginBot;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceComponent;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;
import org.rsbot.util.GlobalConfiguration;

@ScriptManifest(authors = { "SS7" }, category = "Mining", name = "SS7's Adamantite And Mithril Miner", version = 4.04, description = "<html><head><style type=\"text/css\"> hr {color: white} p {margin-left: 20px}</style></head><body><center><b><font size=\"4\" color=\"black\">All setting's can be configured in the GUI</font></b></table></center></body></html>")
public class SS7AddyMiner extends Script implements PaintListener,
	ServerMessageListener {

    class SS7AddyMinerSettings {
	final File settingsFile = new File(new File(GlobalConfiguration.Paths
		.getSettingsDirectory()), SETTINGS_FILE_NAME);

	String oreToMine = "";
	String location = "";
	String miningMethod = "";
	String antibanSpeed = "";

	boolean allWorlds = false;
	int[] worlds = new int[50];

	boolean superheat = false;
	boolean mithBars = false;
	boolean addyBars = false;
	boolean alch = false;
	boolean alchGems = false;
	boolean alchBars = false;
	boolean alchOres = false;
	boolean alchCoal = false;

	boolean member = false;
	boolean showPaint = false;
	boolean useRest = false;
	boolean dropGems = false;
	boolean dropDiamonds = false;
	boolean dropRubies = false;
	boolean dropEmeralds = false;
	boolean dropSapphires = false;

	String reportSetting = "";

	public SS7AddyMinerSettings() {
	}

	public String booleanToString(final boolean a) {
	    if (a) {
		return "true";
	    } else {
		return "false";
	    }
	}

	public boolean extractBoolean(final String text) {
	    return text.equals("true");
	}

	public int[] extractIntegers(String text) {
	    int[] ints = null;
	    try {
		text = text.replaceAll(" ", "");
		final String[] strInts = text.split(",");
		ints = new int[strInts.length];
		for (int a = 0; a < strInts.length; a++) {
		    ints[a] = Integer.parseInt(strInts[a]);
		}
	    } catch (final Exception e) {
		log.log(Level.SEVERE, "extractIntegers(String) error: ", e);
	    }
	    return ints;
	}

	public String[] extractStrings(final String text) {
	    return text.split(",");
	}

	public String getSetting(final String settingName) {
	    try {
		final Properties p = new Properties();
		p.load(new FileInputStream(settingsFile));
		return p.getProperty(settingName);
	    } catch (final IOException ioe) {
		log.log(Level.SEVERE, "loadSettings(String) error: ", ioe);
		return "";
	    }
	}

	public String[][] getSettingsArray() {
	    final ArrayList<String[]> settingsArray = new ArrayList<String[]>();

	    settingsArray.add(new String[] { "ORETOMINE", oreToMine });
	    settingsArray.add(new String[] { "MININGMETHOD", miningMethod });
	    settingsArray.add(new String[] { "LOCATION", location });
	    settingsArray.add(new String[] { "ANTIBANSPEED", antibanSpeed });

	    settingsArray.add(new String[] { "ALLWORLDS",
		    booleanToString(allWorlds) });
	    if (!allWorlds) {
		settingsArray.add(new String[] { "WORLDS",
			intArrayToString(worlds) });
	    }

	    settingsArray.add(new String[] { "SUPERHEAT",
		    booleanToString(superheat) });
	    if (superheat) {
		settingsArray.add(new String[] { "MITHBARS",
			booleanToString(mithBars) });
		settingsArray.add(new String[] { "ADDYBARS",
			booleanToString(addyBars) });
	    }
	    settingsArray.add(new String[] { "ALCH", booleanToString(alch) });
	    if (alch) {
		settingsArray.add(new String[] { "ALCHGEMS",
			booleanToString(alchGems) });
		settingsArray.add(new String[] { "ALCHBARS",
			booleanToString(alchBars) });
		settingsArray.add(new String[] { "ALCHORES",
			booleanToString(alchOres) });
		settingsArray.add(new String[] { "ALCHCOAL",
			booleanToString(alchCoal) });
	    }

	    settingsArray.add(new String[] { "DROPGEMS",
		    booleanToString(dropGems) });
	    if (dropGems) {
		settingsArray.add(new String[] { "DROPDIAMOND",
			booleanToString(dropDiamonds) });
		settingsArray.add(new String[] { "DROPRUBY",
			booleanToString(dropRubies) });
		settingsArray.add(new String[] { "DROPEMERALD",
			booleanToString(dropEmeralds) });
		settingsArray.add(new String[] { "DROPSAPPHIRE",
			booleanToString(dropSapphires) });
	    }
	    settingsArray.add(new String[] { "USEREST",
		    booleanToString(useRest) });
	    settingsArray.add(new String[] { "SHOWPAINT",
		    booleanToString(showPaint) });
	    settingsArray
		    .add(new String[] { "MEMBER", booleanToString(member) });

	    settingsArray.add(new String[] { "REPORTSETTING", reportSetting });

	    final String[][] stringArray = new String[settingsArray.size()][2];
	    for (int a = 0; a < settingsArray.size(); a++) {
		stringArray[a][0] = settingsArray.get(a)[0];
		stringArray[a][1] = settingsArray.get(a)[1];
	    }
	    return stringArray;
	}

	public String intArrayToString(final int[] array) {
	    String intArray = null;
	    try {
		if (array.length > 0) {
		    intArray = "";
		    for (int a = 0; a < array.length; a++) {
			if (array[a] != 0) {
			    intArray += array[a];
			    if (a != array.length - 1) {
				intArray += ",";
			    }
			}
		    }
		    return intArray;
		}
	    } catch (final Exception e) {
	    }
	    return "";
	}

	public void saveSettings(final String[][] settings) {
	    try {
		final Properties p = new Properties();

		settingsFile.createNewFile();
		p.load(new FileInputStream(settingsFile));
		for (final String[] setting : settings) {
		    p.setProperty(setting[0], setting[1]);
		}
		final FileOutputStream out = new FileOutputStream(settingsFile);
		p.store(out, "");
	    } catch (final IOException ioe) {
		log.log(Level.SEVERE, "saveSettings(String[][]) error: ", ioe);
	    }
	}

	public void setSettings() {
	    try {
		oreToMine = getSetting("ORETOMINE");
		miningMethod = getSetting("MININGMETHOD");
		location = getSetting("LOCATION");
		antibanSpeed = getSetting("ANTIBANSPEED");

		allWorlds = extractBoolean(getSetting("ALLWORLDS"));
		if (!allWorlds) {
		    worlds = extractIntegers(getSetting("WORLDS"));
		}

		superheat = extractBoolean(getSetting("SUPERHEAT"));
		if (superheat) {
		    mithBars = extractBoolean(getSetting("MITHBARS"));
		    addyBars = extractBoolean(getSetting("ADDYBARS"));
		}
		alch = extractBoolean(getSetting("ALCH"));
		if (alch) {
		    alchGems = extractBoolean(getSetting("ALCHGEMS"));
		    alchBars = extractBoolean(getSetting("ALCHBARS"));
		    alchOres = extractBoolean(getSetting("ALCHORES"));
		    alchCoal = extractBoolean(getSetting("ALCHCOAL"));
		}

		dropGems = extractBoolean(getSetting("DROPGEMS"));
		if (dropGems) {
		    dropDiamonds = extractBoolean(getSetting("DROPDIAMOND"));
		    dropRubies = extractBoolean(getSetting("DROPRUBY"));
		    dropEmeralds = extractBoolean(getSetting("DROPEMERALD"));
		    dropSapphires = extractBoolean(getSetting("DROPSAPPHIRE"));
		}
		useRest = extractBoolean(getSetting("USEREST"));
		showPaint = extractBoolean(getSetting("SHOWPAINT"));
		member = extractBoolean(getSetting("MEMBER"));
	    } catch (final Exception e) {
		log.log(Level.SEVERE, "setSettings error: ", e);
	    }
	}

	public boolean settingsExist() {
	    return settingsFile.exists();
	}

	public String stringArrayToString(final String[] array) {
	    String strArray = null;
	    try {
		strArray = "";
		if (array.length <= 0) {
		    return "";
		}
		for (int a = 0; a < array.length; a++) {
		    if (!array[a].equals(null) && !array[a].equals("")) {
			strArray += array[a].trim();
			if (a != array.length - 1) {
			    strArray += ",";
			}
		    }
		}
	    } catch (final Exception e) {
	    }
	    return strArray;
	}
    }

    private class getMarketPrice implements Runnable {
	public boolean stop;

	public void run() {
	    while (!stop) {
		try {
		    addyPrice = ge.loadItemInfo(449).getMarketPrice();
		    mithPrice = ge.loadItemInfo(447).getMarketPrice();
		    diamondPrice = ge.loadItemInfo(1617).getMarketPrice();
		    rubyPrice = ge.loadItemInfo(1619).getMarketPrice();
		    emeraldPrice = ge.loadItemInfo(1621).getMarketPrice();
		    sapphirePrice = ge.loadItemInfo(1623).getMarketPrice();
		    stop = true;
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	}
    }

    private class SS7AddyMinerAntiBan implements Runnable {
	public boolean stopThread;

	public void run() {
	    while (!stopThread && isLoggedIn() && antiBanSpeed > 0) {
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

    SS7AddyMinerAntiBan antiban;
    Thread t;
    getMarketPrice price;
    Thread p;

    // ROCK ID'S
    int[] ironRock = { 37307, 37308, 37309, 31071, 31072, 31073 };
    int[] addyRock = { 11941, 11939, 31083, 31085, 31083, 31085 };
    int[] coalRock = { 11930, 11931, 11932 };
    int[] mithRock = { 11944, 11943, 11942, 31086, 31088 };
    int[] mithAndAddyRock = { 11941, 11939, 31083, 31085, 31083, 31085, 11944,
	    11943, 11942, 31086, 31088 };
    int[] oreToMine2;

    // MINING
    RSTile lastRock = new RSTile(0000, 0000);
    int rocksteal = 0;
    int xrocksteal = 0;
    int rrocksteal = 0;
    int superheatError = 0;
    int wieldStaff = 0;
    int scroll = 2;
    int[] gems = { 1617, 1619, 1621, 1623 };
    int[] bars = { 2361, 2359 };
    private final int PASSWORD_INTERFACE = 457, USERNAME_INTERFACE = 451,
	    TEXT_INTERFACE = 445;
    private int failSafeCount = 0;
    boolean checkMining = false;
    boolean checkSmithing = false;
    boolean checkMagic = false;
    int[] dontBank = { 1387, 561, 1265, 1267, 1269, 1271, 1273, 1275, 1296, 995 };
    int total;
    boolean done = false;
    private final String SETTINGS_FILE_NAME = "SS7AddyMinerSettings.txt";
    private final SS7AddyMinerSettings settings = new SS7AddyMinerSettings();
    String accountName;

    private javax.swing.JCheckBox addyBars;
    private javax.swing.JCheckBox alch;
    private javax.swing.JCheckBox alchBars;
    private javax.swing.JCheckBox alchGOres;
    private javax.swing.JCheckBox alchGems;
    private javax.swing.JCheckBox alchSOres;
    private javax.swing.JComboBox antibanSpeed;
    private javax.swing.JButton cancelButton;
    private javax.swing.JCheckBox dropDia;
    private javax.swing.JCheckBox dropEme;
    private javax.swing.JCheckBox dropGems;
    private javax.swing.JCheckBox dropRub;
    private javax.swing.JCheckBox dropSapp;
    private javax.swing.JFrame jFrame1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTextField listOfWorlds;
    private javax.swing.JComboBox location;
    private javax.swing.JCheckBox member;
    private javax.swing.JCheckBox mithBars;
    private javax.swing.JComboBox oreToMine;
    private javax.swing.JCheckBox showPaint;
    private javax.swing.JButton startButton;
    private javax.swing.JCheckBox superheat;
    private javax.swing.JCheckBox useRest;
    private javax.swing.JButton visitThread;
    private javax.swing.JCheckBox worldCheck;
    private javax.swing.JComboBox worldhop;

    // WORLD HOP STRINGS
    String customWorlds[];

    // PATHS
    // ALKHARID
    RSTile[] bankToMine = new RSTile[] { new RSTile(3270, 3167),
	    new RSTile(3276, 3174), new RSTile(3283, 3185),
	    new RSTile(3282, 3196), new RSTile(3279, 3209),
	    new RSTile(3277, 3220), new RSTile(3275, 3231),
	    new RSTile(3278, 3242), new RSTile(3278, 3252),
	    new RSTile(3282, 3262), new RSTile(3288, 3272),
	    new RSTile(3296, 3279), new RSTile(3298, 3293),
	    new RSTile(3300, 3304), new RSTile(3297, 3312) };
    RSTile[] mineToBank = reversePath(bankToMine);

    // DRAYNOR
    RSTile[] cabbageToStile = new RSTile[] { new RSTile(3054, 3289),
	    new RSTile(3058, 3288), new RSTile(3063, 3284) };
    RSTile[] stileToBank = new RSTile[] { new RSTile(3071, 3277),
	    new RSTile(3073, 3271), new RSTile(3074, 3264),
	    new RSTile(3076, 3257), new RSTile(3081, 3253),
	    new RSTile(3086, 3249), new RSTile(3091, 3248),
	    new RSTile(3092, 3244) };
    RSTile[] draynorToBank = new RSTile[] { new RSTile(3146, 3148),
	    new RSTile(3150, 3153), new RSTile(3145, 3163),
	    new RSTile(3141, 3171), new RSTile(3140, 3179),
	    new RSTile(3139, 3186), new RSTile(3138, 3194),
	    new RSTile(3135, 3201), new RSTile(3131, 3209),
	    new RSTile(3123, 3215), new RSTile(3116, 3219),
	    new RSTile(3108, 3223), new RSTile(3096, 3234),
	    new RSTile(3093, 3243) };
    RSTile[] bankToDraynor = reversePath(draynorToBank);
    // FALADOR
    RSTile[] bankToDoor = new RSTile[] { new RSTile(3013, 3356),
	    new RSTile(3012, 3360), new RSTile(3016, 3362),
	    new RSTile(3022, 3364), new RSTile(3028, 3366),
	    new RSTile(3034, 3368), new RSTile(3040, 3370),
	    new RSTile(3047, 3369), new RSTile(3052, 3369),
	    new RSTile(3057, 3370), new RSTile(3061, 3374),
	    new RSTile(3061, 3377) };
    RSTile[] stairsToMine = new RSTile[] { new RSTile(3058, 9776),
	    new RSTile(3052, 9774), new RSTile(3046, 9772),
	    new RSTile(3039, 9773) };
    RSTile[] doorToBank = reversePath(bankToDoor);
    RSTile[] mineToStairs = reversePath(stairsToMine);
    // DEATHWALK
    RSTile[] alkharidDeathwalk = new RSTile[] {};
    RSTile[] draynorDeathwalk = new RSTile[] {};

    // LOCATION BOOLEANS
    boolean draynor, alkharid, falador = false;

    // ITEMS
    int[] thingsToKeep = { 1275, 449, 1623, 1621, 1619, 1617, 447, 1387, 561,
	    2361, 2359, 995, 1265, 1267, 1269, 1271, 1273, 1275, 1296 };

    // BANK ITEMS
    int[] bankBoothID = { 35647, 35648, 11758, 2213 };

    // PAINT VARIABLES
    int startingXP, addyMined, mithMined, startingLevel, profit, levelsGained,
	    moneyGained, currentXP, XPGained, currentLevel, nextLevel,
	    XPTillNextLevel, oresTillNextLevel, avgXPHour, avgMoneyHour,
	    avgOresHour, percentTillNextLevel, addyPrice, mithPrice,
	    diamondPrice, rubyPrice, emeraldPrice, sapphirePrice, diamondMined,
	    rubyMined, emeraldMined, sapphireMined = 0;
    long startTime;

    // ANTIBAN VARIABLES
    int antiBanSpeed;

    // STRINGS
    String status;

    // LONGS
    long stairsUpTime = System.currentTimeMillis();
    long stairsDownTime = System.currentTimeMillis();

    // OBJECTS
    RSObject rock;

    // OPTIONS
    boolean member2, rest, paint, addy, mith, both, worldHop, diamond, ruby,
	    emerald, sapphire, superheat2, alch2, addyBar, mithBar, alchG,
	    alchB, alchGO, alchSO, ring = false;

    // GUI
    boolean startScript = false;

    // GRAND EXCHANGE
    final GrandExchange ge = new GrandExchange();

    // STRING EXTRACTION
    public String[] extractStrings(final String text) {
	return text.split(", ");
    }

    // LOOP
    public boolean ableToCast() {
	if (superheat2) {
	    if (addyBar) {
		if (getInventoryCount(449) >= 1 && getInventoryCount(453) >= 7) {
		    return true;
		}
	    } else if (mithBar) {
		if (getInventoryCount(447) >= 1 && getInventoryCount(453) >= 4) {
		    return true;
		}
	    } else {
		return false;
	    }
	}
	return false;
    }

    public boolean ableToAlch() {
	if (alch2) {
	    if (getInventoryCount() > random(22, 28)) {
		if (alchG) {
		    if (getInventoryCount(gems) > 0) {
			return true;
		    }
		}
		if (alchB) {
		    if (getInventoryCount(bars) > 0) {
			return true;
		    }
		}
		if (alchGO) {
		    if (getInventoryCount(449, 447) > 0) {
			return true;
		    }
		}
		if (alchSO) {
		    if (getInventoryCount(453) > 0) {
			return true;
		    }
		}
	    } else {
		return false;
	    }
	} else {
	    return false;
	}
	return false;
    }

    boolean s = false;

    public int loop() {

	try {
	    if (!t.isAlive()) {
		t.start();
	    }
	} catch (Exception e) {

	}

	if (!s) {
	    if (equipmentContains(13562)) {
		ring = true;
	    }
	    s = true;
	}

	antiBan();
	runCheck();
	dropGems();
	combat();

	if (getMyPlayer().getAnimation() == 624) {
	    status = "Mining";
	}

	if ((checkMining) || (checkMagic) || (checkSmithing)) {
	    if (random(-10, 10) > 0) {
		clickStat();
		checkMining = false;
		checkMagic = false;
		checkSmithing = false;
	    } else {
		checkMining = false;
		checkMagic = false;
		checkSmithing = false;
	    }
	}

	if (superheat2) {
	    if (ableToCast()) {
		castSuperHeat();
		wait(random(200, 300));
		lastRock = new RSTile(0, 0);
	    }
	}

	if (alch2) {
	    if (ableToAlch()) {
		Highalch();
		wait(random(200, 300));
		lastRock = new RSTile(0, 0);
	    }
	}

	if (isInventoryFull()) {
	    if (worldHop) {
		if (atBank()) {
		    handleBank();
		} else {
		    status = "Walking To The Bank";
		    walkToBank();
		}
	    } else {
		if (getInventoryCount(thingsToKeep) >= 28) {
		    if (atBank()) {
			handleBank();
		    } else {
			status = "Walking To The Bank";
			walkToBank();
		    }
		} else {
		    status = "Dropping Ores";
		    dropAllExcept(thingsToKeep);
		}
	    }
	} else {
	    if (atMine()) {
		rockMining();
	    } else {
		status = "Walking To The Mine";
		walkToMine();
	    }
	}
	return random(400, 800);
    }

    // ANTIBAN
    public void antiBan() {
	if (antiBanSpeed != 0) {
	    switch (random(0, antiBanSpeed)) {
	    case 1:
	    case 2:
	    case 3:
	    case 4:
	    case 5:
	    case 6:
	    case 7:
		moveMouse();
		break;
	    case 8:
	    case 9:
	    case 10:
	    case 11:
	    case 12:
	    case 13:
		turnCamera();
		break;
	    case 14:
	    case 15:
		openTab(Constants.TAB_STATS);
		wait(random(200, 400));
		if (random(-10, 10) > 0) {
		    moveMouse(688, 246, 10, 10);
		}
		wait(random(800, 1200));
		break;
	    case 16:
		openTab(Constants.TAB_FRIENDS);
		wait(random(800, 1220));
		break;
	    case 17:
		openTab(Constants.TAB_INVENTORY);
		wait(random(1000, 2000));
		break;
	    case 18:
		openTab(Constants.TAB_INVENTORY);
		wait(random(1000, 2000));
		openTab(Constants.TAB_STATS);
		wait(random(200, 400));
		moveMouse(688, 246, 5, 5);
		wait(random(800, 1200));
		break;
	    case 19:
		moveMouse(random(0, 800), random(0, 800));
		wait(random(200, 400));
		moveMouse(random(0, 800), random(0, 800));
		wait(random(150, 300));
		break;
	    case 20:
		int randomTab = random(1, 14);
		openTab(randomTab);
		break;
	    case 22:
		if (alch2) {
		    if (ableToCast()) {
			return;
		    }
		    Highalch();
		}
	    case 23:
		if (superheat2) {
		    castSuperHeat();
		}
	    }
	}
    }

    public void clickStat() {
	openTab(TAB_STATS);
	if (checkMining) {
	    moveMouse(688, 246, 10, 10);
	} else if (checkSmithing) {
	    moveMouse(692, 278, 10, 10);
	} else {
	    moveMouse(580, 408, 10, 10);
	}
	clickMouse(true);
	wait(random(200, 300));
	moveMouse(random(70, 400), random(40, 335));
	wait(random(4900, 6200));
	atInterface(741, 9);
	wait(random(50, 500));
    }

    public void clickMainMenu() {
	do {
	    moveMouse(random(296, 472), random(410, 422), 5, 5);
	} while (!mouseInArea(472, 422, 296, 410));
	clickMouse(true);
    }

    public int Login() {
	while (!isLoggedIn()) {
	    if (isLoggedIn()) {
		while (RSInterface.getChildInterface(
			LoginBot.WELCOME_SCREEN_ID,
			LoginBot.WELCOME_SCREEN_BUTTON_PLAY)
			.getAbsoluteY() < 2) {
		    wait(random(1500, 2500));
		}
		return -1;
	    }
	    if (failSafeCount > 19) {
		log("Can't log in... Stopping script.");
		stopScript();
		return -1;
	    }
	    if (RSInterface.getInterface(744).getChild(TEXT_INTERFACE)
		    .getText().contains("update")) {
		log("There was a update. Stopping script.");
		stopScript();
		return -1;
	    }
	    if (RSInterface.getInterface(744).getChild(TEXT_INTERFACE)
		    .getText().contains("disable")) {
		log("Can't log in... stopping script");
		stopScript();
		return -1;
	    }
	    if (RSInterface.getInterface(744).getChild(TEXT_INTERFACE)
		    .getText().contains("members")) {
		member2 = false;
		clickMainMenu();
		worldHop();
	    }
	    if (RSInterface.getInterface(744).getChild(TEXT_INTERFACE)
		    .getText().contains("incorrect")) {
		log("Can't log in... stopping script.");
		stopScript();
		return -1;
	    }
	    if (RSInterface.getInterface(744).getChild(TEXT_INTERFACE)
		    .getText().contains("invalid")) {
		failSafeCount++;
	    }
	    if (RSInterface.getInterface(744).getChild(TEXT_INTERFACE)
		    .getText().contains("full")) {
		clickMainMenu();
		worldHop();
	    }
	    if (RSInterface.getInterface(744).getChild(TEXT_INTERFACE)
		    .getText().contains("Error connecting")) {
		boolean connected = false;
		log("Internet connection lost.. waiting for connection");
		while (!connected) {
		    try {
			final URL rs = new URL(
				"http://www.runescape.com");
			final URLConnection connection = rs
				.openConnection();
			final BufferedReader reader = new BufferedReader(
				new InputStreamReader(connection
					.getInputStream()));
			String contents = "";
			String s = "";
			while ((s = reader.readLine()) != null) {
			    contents = contents.concat(s);
			}
			if (contents != null) {
			    log("Connection restored!");
			    connected = true;
			}
		    } catch (final Exception e) {
			log("Unable to connect, retrying connection");
			wait(random(5000, 10000));
		    }
		}
	    }
	    if (RSInterface.getInterface(744).getChild(TEXT_INTERFACE)
		    .getText().contains("world")) {
		while (RSInterface.getInterface(744).getChild(TEXT_INTERFACE)
			.getText().contains("world")) {
		    wait(random(200, 500));
		}
	    }
	    if (RSInterface.getInterface(744).getChild(TEXT_INTERFACE)
		    .getText().contains("Performing login")) {
		wait(random(1500, 2500));
		if (RSInterface.getInterface(744).getChild(TEXT_INTERFACE)
			.getText().contains("Performing login")) {
		    done = true;
		    return -1;
		} else {
		    wait(random(100, 200));
		}
	    }
	    if (RSInterface.getInterface(744).getChild(TEXT_INTERFACE)
		    .getText().contains("transferred")) {
		while (RSInterface.getInterface(744).getChild(TEXT_INTERFACE)
			.getText().contains("transferred")) {
		    wait(random(200, 500));
		}
	    }
	    int typingPropType = 0;
	    if (isLoginScreen()) {
		int propTrys = 0;
		while (true) {
		    propTrys++;
		    if (RSInterface.getInterface(744).getChild(
			    USERNAME_INTERFACE).getText().endsWith("|")) {
			typingPropType = 1;
			break;
		    }
		    if (RSInterface.getInterface(744).getChild(
			    PASSWORD_INTERFACE).getText().endsWith("|")) {
			typingPropType = 2;
			break;
		    }
		    if (propTrys == 50) {
			break;
		    }
		    wait(random(10, 20));
		}
		if (typingPropType == 0) {
		    moveMouse(299, 180, 166, 18);
		    clickMouse(true);
		}
		if (typingPropType == 1) {
		    sendUserProp(
			    accountName.replaceAll("_", " ").toLowerCase(),
			    USERNAME_INTERFACE);
		}
		if (typingPropType == 2) {
		    sendUserProp(AccountManager.getPassword(accountName),
			    PASSWORD_INTERFACE);
		}
	    }
	    return random(250, 500);
	}
	return random(250, 500);
    }

    private void sendUserProp(final String data, final int type) {
	String boxText = RSInterface.getInterface(744).getChild(type).getText();
	if (boxText.endsWith("|")) {
	    boxText = boxText.substring(0, boxText.length() - 1);
	}
	final int boxTextLength = boxText.length();
	if (boxTextLength == data.length()) {
	    sendText("", true);
	    return;
	}
	if (boxTextLength > 0 && boxTextLength != data.length()) {
	    for (int i = 0; i <= boxTextLength; i++) {
		sendText("\b", false);
		wait(random(5, 10));
	    }
	}
	sendText(data, false);
	sendUserProp(data, type);
    }

    // GEM DROPPING METHOD
    public void dropGems() {
	if (diamond) {
	    if (getInventoryCount(1617) > 0) {
		atInventoryItem(1623, "Drop");
	    }
	}
	if (ruby) {
	    if (getInventoryCount(1619) > 0) {
		atInventoryItem(1621, "Drop");
	    }
	}
	if (emerald) {
	    if (getInventoryCount(1621) > 0) {
		atInventoryItem(1619, "Drop");
	    }
	}
	if (sapphire) {
	    if (getInventoryCount(1623) > 0) {
		atInventoryItem(1617, "Drop");
	    }
	}
    }

    public int getNumberOfAlchableItems() {
	int i = 0;
	if (alchG) {
	    i = getInventoryCount(gems) + i;
	}
	if (alchB) {
	    i = getInventoryCount(bars) + i;
	}
	if (alchGO) {
	    if (mith) {
		i = getInventoryCount(447) + i;
	    } else if (addy) {
		i = getInventoryCount(449) + i;
	    } else {
		i = getInventoryCount(449) + i + getInventoryCount(447);
	    }
	}
	if (alchSO) {
	    i = getInventoryCount(453) + i;
	}
	return i;
    }

    // HIGH ALCH METHOD
    public int Highalch() {
	int abx = 0;
	int o = 0;
	while (getNumberOfAlchableItems() >= 1) {
	    if (ableToCast()) {
		return -1;
	    }
	    if (getMyPlayer().getAnimation() == 624) {
		return -1;
	    }
	    if (!inventoryContains(561)) {
		log("Unable to complete task... shutting down");
		stopScript();
	    }
	    castSpell(SPELL_HIGH_LEVEL_ALCHEMY);
	    while (getCurrentTab() != TAB_INVENTORY && o < 6) {
		wait(random(250, 750));
		o++;
	    }
	    o = 0;
	    if (wieldStaff == 1) {
		if (!inventoryContains(1387)) {
		    if (equipmentContains(1387)) {
			continue;
		    }
		    log("Unable to complete task... shutting down");
		    stopScript();
		} else {
		    atInventoryItem(1387, "ield");
		}
	    }
	    if (getNumberOfAlchableItems() == 0) {
		break;
	    } else {
		if (alchSO) {
		    if (getInventoryCount(453) > 0) {
			atInventoryItem(453, "Cast");
			while (getMyPlayer().getAnimation() == 9633) {
			    wait(random(20, 30));
			}
			while (getCurrentTab() != TAB_MAGIC) {
			    wait(random(20, 30));
			}
			continue;
		    }
		}
		if (alchGO) {
		    if (getInventoryCount(447) > 0) {
			atInventoryItem(447, "Cast");
			while (getMyPlayer().getAnimation() == 9633) {
			    wait(random(20, 30));
			}
			while (getCurrentTab() != TAB_MAGIC) {
			    wait(random(20, 30));
			}
			continue;
		    }
		}
		if (alchGO) {
		    if (getInventoryCount(449) > 0) {
			atInventoryItem(449, "Cast");
			while (getMyPlayer().getAnimation() == 9633) {
			    wait(random(20, 30));
			}
			while (getCurrentTab() != TAB_MAGIC) {
			    wait(random(20, 30));
			}
			continue;
		    }
		}
		if (alchG) {
		    if (getInventoryCount(1617) > 0) {
			atInventoryItem(1617, "Cast");
			while (getMyPlayer().getAnimation() == 9633) {
			    wait(random(20, 30));
			}
			while (getCurrentTab() != TAB_MAGIC) {
			    wait(random(20, 30));
			}
			continue;
		    }
		    if (getInventoryCount(1619) > 0) {
			atInventoryItem(1619, "Cast");
			while (getMyPlayer().getAnimation() == 9633) {
			    wait(random(20, 30));
			}
			while (getCurrentTab() != TAB_MAGIC) {
			    wait(random(20, 30));
			}
			continue;
		    }
		    if (getInventoryCount(1621) > 0) {
			atInventoryItem(1621, "Cast");
			while (getMyPlayer().getAnimation() == 9633) {
			    wait(random(20, 30));
			}
			while (getCurrentTab() != TAB_MAGIC) {
			    wait(random(20, 30));
			}
			continue;
		    }
		    if (getInventoryCount(1623) > 0) {
			atInventoryItem(1623, "Cast");
			while (getMyPlayer().getAnimation() == 9633) {
			    wait(random(20, 30));
			}
			while (getCurrentTab() != TAB_MAGIC) {
			    wait(random(20, 30));
			}
			continue;
		    }
		}
		if (alchB) {
		    if (getInventoryCount(2359) > 0) {
			atInventoryItem(2359, "Cast");
			while (getMyPlayer().getAnimation() == 9633) {
			    wait(random(20, 30));
			}
			while (getCurrentTab() != TAB_MAGIC) {
			    wait(random(20, 30));
			}
			continue;
		    }
		    if (getInventoryCount(2361) > 0) {
			atInventoryItem(0, "Cast");
			while (getMyPlayer().getAnimation() == 9633) {
			    wait(random(20, 30));
			}
			while (getCurrentTab() != TAB_MAGIC) {
			    wait(random(20, 30));
			}
			continue;
		    }
		}
	    }
	}
	if (abx >= 11) {
	    log("Unable to complete task... shutting down");
	    stopScript();
	}
	return random(500, 1000);
    }

    // SCROLLING METHOD
    public void scroll() {
	try {
	    Point point = null;
	    final RSInterfaceComponent[] click = RSInterface.getInterface(744)
		    .getChild(178).getComponents();
	    point = new Point(click[0].getPoint());
	    moveMouse(point, 0, 100);
	    wait(random(200, 500));
	    clickMouse(true);
	} catch (final Exception e) {
	    return;
	}
    }

    // REORDER WORLD LIST - TYPE
    public void reorderTypeList() {
	final Point point = new Point(RSInterface.getInterface(744).getChild(
		184).getAbsoluteX(), RSInterface.getInterface(744)
		.getChild(184).getAbsoluteY());
	moveMouse(point, 1, 1);
	wait(random(100, 300));
	clickMouse(true);
    }

    public void reorderTypeListMember() {
	final Point point = new Point(RSInterface.getInterface(744).getChild(
		184).getAbsoluteX(), RSInterface.getInterface(744)
		.getChild(183).getAbsoluteY());
	moveMouse(point, 1, 1);
	wait(random(100, 300));
	clickMouse(true);
    }

    public int getNumberOfItems() {
	int item = 0;
	if (mithBar) {
	    item = item + getInventoryCount(447);
	}
	if (addyBar) {
	    item = item + getInventoryCount(449);
	}
	return item;
    }

    public int getNumberOfCoal() {
	int coal = 0;
	if (mithBar && getInventoryCount(447) > 0) {
	    coal = 4;
	}
	if (addyBar && getInventoryCount(449) > 0) {
	    coal = 7;
	}
	return coal;
    }

    // SUPER HEATING METHOD

    public int castSuperHeat() {
	int abx = 0;
	int o = 0;
	int item = 0;

	while (getInventoryCount(453) >= getNumberOfCoal()
		&& getNumberOfItems() >= 1 && abx < 10) {
	    while (getMyPlayer().getAnimation() == 624) {
		wait(random(100, 500));
	    }
	    if (!inventoryContains(561)) {
		log("You are out of nature runes...Stopping script...");
		logout();
		stopScript();
	    }
	    status = "Superheating";
	    castSpell(SPELL_SUPERHEAT_ITEM);
	    while (getCurrentTab() != TAB_INVENTORY && o < 6) {
		wait(random(250, 750));
		o++;
	    }
	    o = 0;
	    if (getCurrentTab() != TAB_INVENTORY) {
		openTab(TAB_INVENTORY);
	    }
	    if (mithBar) {
		if (getInventoryCount(447) >= 1 && getInventoryCount(453) >= 4) {
		    item = 447;
		}
	    }
	    if (addyBar) {
		if (getInventoryCount(449) >= 1 && getInventoryCount(453) >= 7) {
		    item = 449;
		}
	    }
	    if (!atInventoryItem(item, "Cast")) {
		abx++;
	    }
	    wait(random(2000, 2500));
	    if (superheatError == 1) {
		log("You do not have the smithing lvl required...Stopping script...");
		logout();
		stopScript();
	    }
	    if (wieldStaff == 1) {
		if (!inventoryContains(1387)) {
		    if (equipmentContains(1387)) {
			continue;
		    }
		    log("You do not have a fire staff...Stopping script...");
		    logout();
		    stopScript();
		} else {
		    atInventoryItem(1387, "ield");
		}
	    }
	}
	if (abx >= 11) {
	    log("Unable to cast superheat...Stopping script...");
	    logout();
	    stopScript();
	}
	return random(500, 1000);
    }

    // AREA METHODS
    public boolean atBank() {
	if (alkharid) {
	    if (isInArea(3271, 3162, 3269, 3173)) {
		return true;
	    }
	} else if (draynor) {
	    if (isInArea(3095, 3240, 3092, 3246)) {
		return true;
	    }
	} else if (falador) {
	    if (isInArea(3018, 3355, 3009, 3358)) {
		return true;
	    }
	}
	return false;
    }

    public boolean atMine() {
	if (alkharid) {
	    if (isInArea(3308, 3283, 3293, 3319)) {
		return true;
	    }
	} else if (draynor) {
	    if (isInArea(3150, 3144, 3143, 3154)) {
		return true;
	    }
	} else if (falador) {
	    if (isInArea(3046, 9759, 3033, 9780)) {
		return true;
	    }
	}
	return false;
    }

    public boolean isInArea(final int maxX, final int minY, final int minX,
	    final int maxY) {
	final int x = getMyPlayer().getLocation().getX();
	final int y = getMyPlayer().getLocation().getY();
	if (x >= minX && x <= maxX && y >= minY && y <= maxY) {
	    return true;
	}
	return false;
    }

    public boolean inArea(final Point p, final int x, final int y, final int w,
	    final int h) {
	if (p.x > x && p.x < x + w && p.y > y && p.y < y + h) {
	    return true;
	} else {
	    return false;
	}
    }

    public boolean mouseInArea(int x, int y, int xx, int yy) {
	int x3 = input.getX();
	int y3 = input.getY();

	if (x3 < x && x3 > xx && y3 < y && y3 > yy) {
	    return true;
	} else {
	    return false;
	}
    }

    // WALKING METHODS
    public boolean isResting() {
	if (getMyPlayer().getAnimation() == 12108
		|| getMyPlayer().getAnimation() == 2033
		|| getMyPlayer().getAnimation() == 2716
		|| getMyPlayer().getAnimation() == 11786
		|| getMyPlayer().getAnimation() == 5713) {
	    return true;
	}
	return false;
    }

    public void runCheck() {
	int tries = 0;
	int replenishes = 0;
	int currentEnergy = 0;
	boolean rest = true;
	if (getEnergy() > 21 + random(1, 29) || getEnergy() >= 51) {
	    setRun(true);
	}
	if (getEnergy() <= 1 && !atMine() && !atBank() && rest
		&& settings.useRest) {
	    if (ring && replenishes <= 3) {
		do {
		    openTab(TAB_EQUIPMENT);
		    wait(random(200, 300));
		    tries++;
		} while (getCurrentTab() != TAB_EQUIPMENT && tries < 10);
		tries = 0;
		currentEnergy = getEnergy();
		atEquippedItem(13562, "un-replenish");
		wait(random(250, 500));
		if (getEnergy() > currentEnergy) {
		    replenishes++;
		} else {
		    replenishes = 3;
		}
	    } else {
		if (random(-10, 10) > 0) {
		    rest(random(40 + random(5, 20), 80 + random(5, 19)));
		}
	    }
	    rest = false;
	}
	if (getEnergy() > 1) {
	    rest = true;
	}
	if (getEnergy() == 100 && isResting()) {
	    walk();
	}
    }

    public void walk() {
	atTile(new RSTile(getMyPlayer().getLocation().getX() + random(-1, 1),
		getMyPlayer().getLocation().getY() + random(-1, 1)), "alk Here");
    }

    public boolean nearTile(final RSTile tile) {
	wait(random(700, 1000));
	while (distanceTo(tile) >= 3 && getMyPlayer().isMoving()) {
	    return false;
	}
	return true;
    }

    public boolean nearTile2(final RSTile tile) {
	wait(random(700, 1000));
	while (distanceTo(tile) >= 1 && getMyPlayer().isMoving()) {
	    return false;
	}
	return true;
    }

    public RSTile getNextTile(final RSTile[] path, final int maxDist) {
	for (int i = path.length - 1; i >= 0; i--) {
	    if (distanceTo(path[i]) <= maxDist) {
		return path[i];
	    }
	}
	return null;
    }

    public boolean walkPath(final RSTile[] path, final int maxDist,
	    final int randomness) {
	if (distanceTo(path[path.length - 1]) <= 4) {
	    return true;
	} else {
	    final RSTile theTile = getNextTile(path, maxDist);
	    if (theTile != null) {
		walkTile(theTile, randomness);
	    }
	    return false;
	}
    }

    public boolean walkPath(final RSTile[] path) {
	return walkPath(path, 15, 1);
    }

    public boolean WalkTileMM(final RSTile t, final int x, final int y) {
	final Point p = tileToMinimap(t);
	if (p.x == -1 || p.y == -1) {
	    return WalkTileMM(getClosestTileOnMap(t), x, y);
	}
	clickMouse(p, x, y, true);
	return true;
    }

    public boolean WalkTileMM(final RSTile t) {
	return WalkTileMM(t, 2, 2);
    }

    public boolean walkTile(final RSTile theTile) {
	return walkTile(theTile, 1);
    }

    public boolean walkTile(final RSTile theTile, final int randomness) {
	if (theTile == null) {
	    return false;
	}
	if (distanceTo(theTile) <= 17) {
	    RSTile newTile;
	    if (randomness == 0) {
		newTile = new RSTile(theTile.getX(), theTile.getY());
	    } else {
		newTile = new RSTile(theTile.getX()
			+ random(-randomness, randomness + 1), theTile.getY()
			+ random(-randomness, randomness + 1));
	    }

	    if (random(0, 30) == 0) {
		turnToTile(newTile, 15);
	    }

	    if (tileOnScreen(newTile) && random(0, 10) == 0) {
		if (!atTile(newTile, "Walk")) {
		    if (!WalkTileMM(newTile)) {
			return false;
		    } else {
			return true;
		    }
		} else {
		    return true;
		}
	    } else {
		if (!WalkTileMM(newTile)) {
		    return false;
		} else {
		    return true;
		}
	    }
	} else {
	    return false;
	}
    }

    public boolean walkToBank() {
	int tries = 0;
	boolean jumped = false;
	if (alkharid) {
	    walkPath(fixPath(generateProperPath(new RSTile(3270, 3166))));
	} else if (draynor) {
	    if (ring) {
		do {
		    openTab(TAB_EQUIPMENT);
		    wait(random(200, 300));
		    tries++;
		} while (getCurrentTab() != TAB_EQUIPMENT && tries < 10);
		tries = 0;
		RSTile currentLocation = getMyPlayer().getLocation();
		atEquippedItem(13562, "abbage-port");
		wait(random(2500, 5000));
		while (getMyPlayer().getLocation() == currentLocation
			&& tries <= 10) {
		    wait(random(300, 500));
		    tries++;
		}
		tries = 0;
		while (distanceTo(new RSTile(3063, 3283)) > 4) {
		    walkPath(cabbageToStile);
		    wait(random(500, 600));
		}
		while (!jumped) {
		    jumpStile(new RSTile(3063, 3283), new RSTile(3063, 3282));
		    while (tries <= 20) {
			wait(random(200, 300));
			tries++;
			if (getMyPlayer().getLocation().getX() == 3063
				&& getMyPlayer().getLocation().getY() == 3282) {
			    jumped = true;
			    break;
			}
		    }
		    tries = 0;
		}
		while (!atBank()) {
		    walkPath(stileToBank);
		    wait(random(500, 1000));
		}
		jumped = false;
	    } else {
		walkPath(draynorToBank);
	    }
	} else if (falador) {
	    if (atBank()) {
		return false;
	    } else if (isInArea(3065, 3359, 3005, 3374)) {
		walkPath(doorToBank);
	    } else if (isInArea(3062, 3375, 3058, 3381)) {
		try {
		    if (!isDoorOpen(new RSTile(3061, 3375), 11715)) {
			openDoor(new RSTile(3061, 3374), new RSTile(3061, 3375));
		    } else {
			walkPath(doorToBank);
		    }
		} catch (Exception e) {

		}
	    } else if (getMyPlayer().getLocation().getY() > 6000) {
		if (isInArea(3059, 9774, 3056, 9779)) {
		    useStairs();
		} else {
		    walkPath(mineToStairs);
		}
	    }
	}
	return false;
    }

    public boolean walkToMine() {
	if (alkharid) {
	    walkPath(fixPath(generateProperPath(new RSTile(3300, 3311))));
	} else if (draynor) {
	    walkPath(bankToDraynor);
	} else if (falador) {
	    if (!(atMine() && atBank() && isInArea(3062, 3375, 3058, 3381)
		    && isInArea(3064, 3371, 3058, 3374)
		    && isInArea(3065, 3359, 3005, 3374) && getMyPlayer()
		    .getLocation().getY() > 6000)) {
		if (tileOnScreen(new RSTile(3061, 3373))) {
		    walkTo(new RSTile(3061, 3373));
		    while (!nearTile2(new RSTile(3061, 3373))) {
			wait(random(10, 15));
		    }
		} else {
		    walkPath(bankToDoor);
		}
	    }
	    if (atMine()) {
		return false;
	    } else if (getMyPlayer().getLocation().getY() > 6000) {
		walkPath(stairsToMine);
	    } else if (isInArea(3062, 3375, 3058, 3381)) {
		useStairs();
	    } else if (isInArea(3064, 3371, 3058, 3374)) {
		try {
		    if (!isDoorOpen(new RSTile(3061, 3375), 11715)) {
			openDoor(new RSTile(3061, 3374), new RSTile(3061, 3375));
		    } else {
			walkPath(bankToDoor);
		    }
		} catch (Exception e) {

		}
	    } else if (isInArea(3065, 3359, 3005, 3374)) {
		walkPath(bankToDoor);
	    } else if (atBank()) {
		walkPath(bankToDoor);
	    }
	}
	return false;
    }

    // ANTICOMBAT METHOD
    public void combat() {
	RSTile runAwayTile = new RSTile(0, 0);
	if (falador) {
	    runAwayTile = new RSTile(3051, 9774);
	} else if (alkharid) {
	    runAwayTile = new RSTile(3299, 3298);
	}
	if (falador || alkharid) {
	    if (getMyPlayer().isInCombat()) {
		if (tileOnMap(runAwayTile)) {
		    walkTile(runAwayTile);
		} else {
		    walkPath(randomizePath(
			    fixPath(generateProperPath(runAwayTile)), 0, 0));
		}
		while (!nearTile(runAwayTile)) {
		    wait(random(10, 15));
		}
		wait(random(10000, 15000));
		worldHop();
	    }
	}
    }

    // DOOR AND STAIRS METHODS
    public boolean isDoorOpen(RSTile location, int openDoor) {
	RSObject o = getObjectAt(location);
	int door = o.getID();
	if (o != null && door == openDoor) {
	    return true;
	} else {
	    return false;
	}
    }

    public boolean useStairs() {
	stairsUpTime = System.currentTimeMillis();
	stairsDownTime = System.currentTimeMillis();
	final RSObject object;
	if (getMyPlayer().getLocation().getY() > 6000) {
	    object = getNearestObjectByID(16, 30943);
	} else {
	    object = getNearestObjectByID(16, 30944);
	}
	if (object == null) {
	    return false;
	}
	if (!Calculations.onScreen(Calculations.tileToScreen(object
		.getLocation()))) {
	    walkTile(object.getLocation());
	    wait(random(50, 600));
	    if (random(0, 4) < 3) {
		moveMouse(random(100, 415), random(100, 237));
	    }
	    while (!nearTile(object.getLocation())) {
		wait(random(10, 15));
	    }
	}
	if (getMyPlayer().getLocation().getY() > 6000) {
	    if (atObject(object, "Climb-up")) {
		while (getMyPlayer().getLocation().getY() > 6000
			&& System.currentTimeMillis() - stairsUpTime < 6000) {
		    if (getMyPlayer().getLocation().getY() < 6000) {
			return true;
		    }
		    if (System.currentTimeMillis() - stairsUpTime >= 6000) {
			return false;
		    }
		}
	    } else {
		setCameraRotation(random(1, 359));
	    }
	} else {
	    if (atObject(object, "Climb-down")) {
		while (getMyPlayer().getLocation().getY() > 6000
			&& System.currentTimeMillis() - stairsDownTime < 6000) {
		    if (getMyPlayer().getLocation().getY() < 6000) {
			return true;
		    }
		    if (System.currentTimeMillis() - stairsDownTime >= 6000) {
			return false;
		    }
		}
	    } else {
		setCameraRotation(random(1, 359));
	    }
	}
	while (getMyPlayer().isMoving()) {
	    wait(random(200, 600));
	}
	return false;
    }

    public boolean jumpStile(RSTile a, RSTile b) {
	long st = System.currentTimeMillis();
	do {
	    if ((System.currentTimeMillis() - st) > 750) {
		setCameraRotation(random(0, 360));
		st = System.currentTimeMillis();
	    }
	    moveMouse(midPoint(Calculations.tileToScreen(a), Calculations
		    .tileToScreen(b)), 3, 3);
	} while (!listContainsString(getMenuItems(), "limb-over"));
	clickMouse(true);
	wait(random(100, 200));
	return true;
    }

    public boolean openDoor(RSTile a, RSTile b) {
	long st = System.currentTimeMillis();
	do {
	    if ((System.currentTimeMillis() - st) > 750) {
		setCameraRotation(random(0, 360));
		st = System.currentTimeMillis();
	    }
	    moveMouse(midPoint(Calculations.tileToScreen(a), Calculations
		    .tileToScreen(b)), 3, 3);
	} while (!listContainsString(getMenuItems(), "pen"));
	clickMouse(true);
	wait(random(100, 200));
	return true;
    }

    private boolean listContainsString(final java.util.List<String> list,
	    final String string) {
	try {
	    int a;
	    for (a = list.size() - 1; a-- >= 0;) {
		if (list.get(a).contains(string)) {
		    return true;
		}
	    }
	} catch (final Exception e) {
	}
	return false;
    }

    public Point midPoint(Point p1, Point p2) {
	return (new Point((p1.x + p2.x) / 2, (p1.y + p2.y) / 2));
    }

    // BANKING METHODS
    public void handleBank() {
	int tries = 0;
	if (!bank.isOpen()) {
	    RSObject bankObject = getNearestObjectByID(bankBoothID);
	    if (bank != null) {
		if (!atTile(bankObject.getLocation(), "quickly")) {
		    atObject(bankObject, "quickly");
		}
	    }
	    while (!bank.isOpen() && tries < 10) {
		wait(random(200, 500));
		tries++;
	    }
	    if (!bank.isOpen()) {
		bank.open();
	    }
	}
	if (getInventoryCount(dontBank) > 0) {
	    bank.depositAllExcept(dontBank);
	} else {
	    bank.depositAll();
	}
	if (random(-10, 10) > 0) {
	    bank.close();
	}
    }

    // MINING METHODs
    public boolean nextRock() {
	final RSObject obj = getObjectAt(lastRock);
	if (obj == null) {
	    return true;
	}
	for (final int element : oreToMine2) {
	    if (obj.getID() == element) {
		return false;
	    }
	}
	for (final int element : ironRock) {
	    if (obj.getID() == element) {
		return false;
	    }
	}
	for (final int element : coalRock) {
	    if (obj.getID() == element) {
		return false;
	    }
	}
	return true;
    }

    public RSObject oreToMine() {
	RSObject r;
	if (both) {
	    r = getNearestObjectByID(addyRock);
	    if (r != null) {
		return r;
	    } else {
		r = getNearestObjectByID(mithRock);
		if (r != null) {
		    return r;
		}
	    }
	} else if (addy) {
	    r = getNearestObjectByID(addyRock);
	    if (r != null) {
		return r;
	    }
	} else {
	    r = getNearestObjectByID(mithRock);
	    if (r != null) {
		return r;
	    }
	}
	return null;
    }

    public void clickRock(RSObject o) {
	if (!atMine() || isInventoryFull()) {
	    return;
	}
	if (distanceTo(o.getLocation()) >= 10) {
	    walkTile(new RSTile(o.getLocation().getX() + random(-1, 1), o
		    .getLocation().getY()
		    + random(-1, 1)));
	    while (!nearTile(new RSTile(o.getLocation().getX() + random(-1, 1),
		    o.getLocation().getY() + random(-1, 1)))) {
		wait(random(15, 30));
	    }
	}
	if (!tileOnScreen(o.getLocation())) {
	    turnToObject(o);
	}
	lastRock = o.getLocation();
	try {
	    if (Bot.getClient().isSpellSelected()) {
		int randomTab = 0;
		do {
		    randomTab = random(1, 6);
		} while (randomTab == getCurrentTab());
		do {
		    openTab(randomTab);
		} while (Bot.getClient().isSpellSelected());
	    }
	    if (!atTile(o.getLocation(), "Mine")) {
		atObject(o, "Mine");
	    }
	    int tries = 0;
	    while (getMyPlayer().getAnimation() != 624 && tries < 10) {
		wait(random(100, 300));
		tries++;
	    }
	} catch (Exception e) {
	}
    }

    public void rockMining() {
	if (getObjectAt(lastRock) == null) {
	    lastRock = new RSTile(0000, 0000);
	}
	if (nextRock() || getMyPlayer().getAnimation() == -1
		&& !getMyPlayer().isMoving()) {
	    rock = null;
	    rock = oreToMine();

	    if (rock == null) {
		if (worldHop) {
		    rock = getNearestObjectByID(oreToMine2);
		    if (falador) {
			if (rock == null
				|| distanceTo(rock.getLocation()) >= 20) {
			    status = "World Hopping";
			    wait(random(1000, 2000));
			    worldHop();
			}
		    } else if (draynor || alkharid) {
			if (rock == null) {
			    status = "World Hopping";
			    wait(random(1000, 2000));
			    worldHop();
			}
		    }
		} else {
		    if (alkharid) {
			RSObject iron = getNearestObjectByID(ironRock);
			if (iron == null) {
			    antiBan();
			    wait(random(400, 800));
			} else {
			    clickRock(iron);
			}
		    } else if (draynor) {
			RSObject rock = getNearestObjectByID(coalRock);
			if (rock == null) {
			    antiBan();
			    wait(random(400, 800));
			} else {
			    clickRock(rock);
			}
		    } else if (falador) {
			RSObject rock = getNearestObjectByID(ironRock);
			if (rock == null) {
			    antiBan();
			    wait(random(400, 800));
			} else {
			    clickRock(rock);
			}
		    }
		}
	    } else {
		clickRock(rock);
	    }
	}
    }

    // CAMERA AND MOUSE MOVEMENT METHODS
    public void turnCamera() {
	final char[] LR = new char[] { KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT };
	final char[] UD = new char[] { KeyEvent.VK_DOWN, KeyEvent.VK_UP };
	final char[] LRUD = new char[] { KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT,
		KeyEvent.VK_UP, KeyEvent.VK_DOWN };
	final int random2 = random(0, 2);
	final int random1 = random(0, 2);
	final int random4 = random(0, 4);

	if (random(0, 3) == 0) {
	    Bot.getInputManager().pressKey(LR[random1]);
	    wait(random(100, 400));
	    Bot.getInputManager().pressKey(UD[random2]);
	    wait(random(100, 200));
	    Bot.getInputManager().releaseKey(UD[random2]);
	    wait(random(100, 400));
	    Bot.getInputManager().releaseKey(LR[random1]);
	} else {
	    Bot.getInputManager().pressKey(LRUD[random4]);
	    wait(random(500, 900));
	    Bot.getInputManager().releaseKey(LRUD[random4]);
	}
    }

    public void moveMouse() {
	moveMouse(random(0, 750), random(0, 750), 50, 50);
	wait(random(200, 600));
    }

    // ON FINISH
    public void onFinish() {
	Bot.getEventManager().removeListener(PaintListener.class, this);
	antiban.stopThread = true;
	price.stop = true;
	if (random(-10, 10) > 0) {
	    final int x = 0;
	    final int y = random(0, 500);
	    moveMouse(x, y);
	} else {
	    final int x = random(0, 750);
	    final int y = 0;
	    moveMouse(x, y);
	}
	return;
    }

    // PAINT
    public void onRepaint(Graphics g) {
	if (paint) {
	    if (startScript) {

		if (startTime == 0) {
		    startTime = System.currentTimeMillis();
		}
		if (startingLevel <= 0 || startingXP <= 0) {
		    startingLevel = skills
			    .getCurrentSkillLevel(Constants.STAT_RUNECRAFTING);
		    startingXP = skills
			    .getCurrentSkillExp(Constants.STAT_RUNECRAFTING);
		}

		long runTime = System.currentTimeMillis() - startTime;

		long millis = System.currentTimeMillis() - startTime;
		long hours = millis / (1000 * 60 * 60);
		millis -= hours * (1000 * 60 * 60);
		long minutes = millis / (1000 * 60);
		millis -= minutes * (1000 * 60);
		long seconds = millis / 1000;

		currentXP = skills.getCurrentSkillExp(14);
		XPGained = currentXP - startingXP;
		currentLevel = skills.getCurrentSkillLevel(14);
		levelsGained = currentLevel - startingLevel;
		moneyGained = ((mithMined * mithPrice)
			+ (addyMined * addyPrice)
			+ (diamondMined * diamondPrice)
			+ (rubyMined * rubyPrice)
			+ (emeraldMined * emeraldPrice) + (sapphireMined * sapphirePrice));
		nextLevel = skills.getCurrentSkillLevel(14) + 1;
		XPTillNextLevel = skills.getXPToNextLevel(14);
		percentTillNextLevel = skills.getPercentToNextLevel(14);

		if (worldHop) {
		    if (mith) {
			oresTillNextLevel = ((int) skills.getXPToNextLevel(14) / 80);
		    } else if (addy) {
			oresTillNextLevel = ((int) skills.getXPToNextLevel(14) / 95);
		    } else {
			int first = ((int) skills.getXPToNextLevel(14) / 80);
			int second = ((int) skills.getXPToNextLevel(14) / 95);
			oresTillNextLevel = ((int) (first + second) / 2);
		    }
		} else {
		    oresTillNextLevel = ((int) skills.getXPToNextLevel(14) / 50);
		}

		avgXPHour = (int) (3600000 / runTime * XPGained);
		avgOresHour = (int) (3600000 / runTime * (mithMined + addyMined));
		avgMoneyHour = (int) (3600000 / runTime * moneyGained);

		final Mouse m = Bot.getClient().getMouse();
		final Point p = new Point(m.x, m.y);

		g.setColor(new Color(0, 191, 255, 170));
		g.fill3DRect(1, 314, 517, 25, true);
		g.fill3DRect(307, 5, 210, 21, true);
		g.fill3DRect(307, 30, 210, 21, true);
		g.fill3DRect(307, 55, 210, 21, true);
		g.setColor(Color.WHITE);
		g.fill3DRect(1, 314, 3, 25, true);
		g.fill3DRect(221, 314, 3, 25, true);
		g.fill3DRect(319, 314, 3, 25, true);
		g.fill3DRect(418, 314, 3, 25, true);
		g.fill3DRect(517, 314, 3, 25, true);

		g.setFont(new Font("Trebuchet MS", Font.PLAIN, 15));

		if (hours > 0) {
		    g.drawString("Runtime : " + hours + " : " + minutes + " : "
			    + seconds + " " + "Hours", 315, 21);
		} else if (minutes > 0) {
		    g.drawString("Runtime : " + minutes + " : " + seconds + " "
			    + "Minutes", 315, 21);
		} else if (seconds > 0) {
		    g.drawString("Runtime : " + seconds + " " + "Seconds", 315,
			    21);
		} else {
		    g.drawString("Runtime : ", 315, 21);
		}

		g.drawString("Status : " + status, 315, 46);

		if (addy) {
		    g.drawString("Mined : " + addyMined + " Addy", 315, 71);
		} else if (mith) {
		    g.drawString("Mined : " + mithMined + " Mith", 315, 71);
		} else {
		    g.drawString("Mined : " + addyMined + " Addy and "
			    + mithMined + " Mith", 315, 71);
		}
		g.setFont(new Font("Calibri", Font.PLAIN, 17));
		g.drawString("Gained", 246, 332);
		g.drawString("Next Level", 335, 332);
		g.drawString("Averaging", 437, 332);

		g.setColor(Color.BLUE);
		g.fill3DRect(10, 314 + 6, 130, 13, true);
		g.setColor(Color.WHITE);
		g.fill3DRect(10, 314 + 6, (int) ((int) skills
			.getPercentToNextLevel(14) * 1.3), 13, true);
		g.setFont(new Font("Trebuchet MS", Font.PLAIN, 14));
		g.drawString(percentTillNextLevel + "% Done", 150, 331);

		if (inArea(p, 221, 314, 99, 25)) {
		    g.setColor(new Color(51, 153, 255, 170));
		    g.fill3DRect(211, 254, 122, 60, true);
		    g.setColor(Color.WHITE);
		    g.setFont(new Font("Calibri", Font.PLAIN, 13));
		    g.drawString("Gained : " + XPGained + " XP", 216, 272);
		    g.drawString("Gained : " + levelsGained + " Levels", 216,
			    289);
		    g.drawString("Gained : " + moneyGained + " GP", 216, 306);
		} else if (inArea(p, 319, 314, 99, 25)) {
		    g.setColor(new Color(51, 153, 255, 170));
		    g.fill3DRect(303, 254, 127, 60, true);
		    g.setColor(Color.WHITE);
		    g.setFont(new Font("Calibri", Font.PLAIN, 13));
		    g.drawString("Next Level : " + nextLevel, 308, 272);
		    g.drawString("Next Level : " + XPTillNextLevel + " XP",
			    308, 289);
		    g.drawString("Next Level : " + oresTillNextLevel + " Ores",
			    308, 306);
		} else if (inArea(p, 418, 314, 99, 25)) {
		    g.setColor(new Color(51, 153, 255, 170));
		    g.fill3DRect(387, 254, 162, 60, true);
		    g.setColor(Color.WHITE);
		    g.setFont(new Font("Calibri", Font.PLAIN, 13));
		    g.drawString("Averaging : " + avgXPHour + " XP/Hr", 392,
			    272);
		    g.drawString("Averaging : " + avgOresHour + " Ores/Hr",
			    392, 289);
		    g.drawString("Averaging : " + avgMoneyHour + " GP/Hr", 392,
			    306);
		}

		final Point loc = getMouseLocation();
		g.setColor(new Color(51, 153, 255, 170));
		g.drawLine(0, loc.y, 766, loc.y);
		g.drawLine(loc.x, 0, loc.x, 505);

	    }
	}
    }

    // ONSTART SUB-METHOD
    public void onStartChecks() {
	startingLevel = skills.getCurrentSkillLevel(14);
	startingXP = skills.getCurrentSkillExp(14);
    }

    // ONSTART
    @SuppressWarnings("deprecation")
    public boolean onStart(final Map<String, String> args) {

	accountName = Bot.getAccountName();

	antiban = new SS7AddyMinerAntiBan();
	t = new Thread(antiban);
	price = new getMarketPrice();
	p = new Thread(price);

	Bot.getEventManager().addListener(PaintListener.class, this);
	try {
	    if (!p.isAlive()) {
		p.start();
	    }
	} catch (Exception e) {
	    log("Error getting prices... Paint will be inaccurate due to this");
	}
	if (settings.settingsExist()) {
	    new s().setVisible(true);
	    try {

		settings.setSettings();

		oreToMine.setSelectedItem(settings.oreToMine);
		location.setSelectedItem(settings.location);
		worldhop.setSelectedItem(settings.miningMethod);
		antibanSpeed.setSelectedItem(settings.antibanSpeed);

		worldCheck.setSelected(settings.allWorlds);
		if (!settings.allWorlds) {
		    listOfWorlds.setText(settings
			    .intArrayToString(settings.worlds));
		}

		superheat.setSelected(settings.superheat);
		if (settings.superheat) {
		    mithBars.setSelected(settings.mithBars);
		    addyBars.setSelected(settings.addyBars);
		}
		alch.setSelected(settings.alch);
		if (settings.alch) {
		    alchGems.setSelected(settings.alchGems);
		    alchBars.setSelected(settings.alchBars);
		    alchGOres.setSelected(settings.alchOres);
		    alchSOres.setSelected(settings.alchCoal);
		}

		dropGems.setSelected(settings.dropGems);
		if (settings.dropGems) {
		    dropDia.setSelected(settings.dropDiamonds);
		    dropRub.setSelected(settings.dropRubies);
		    dropEme.setSelected(settings.dropEmeralds);
		    dropSapp.setSelected(settings.dropSapphires);
		}
		useRest.setSelected(settings.useRest);
		showPaint.setSelected(settings.showPaint);
		member.setSelected(settings.member);
	    } catch (final Exception e) {
		log.log(Level.SEVERE, "error getting settings: ", e);
	    }
	} else {
	    new s().setVisible(true);
	}

	while (!startScript) {
	    wait(15);
	}

	onStartChecks();
	startTime = System.currentTimeMillis();

	return true;
    }

    // SERVER MESSAGE
    public void serverMessageRecieved(final ServerMessageEvent e) {
	final String CM = e.getMessage();
	if (CM.contains("You manage to mine some adam")) {
	    addyMined++;
	}
	if (CM.contains("You manage to mine some mithril")) {
	    mithMined++;
	}
	if (CM.contains("diamond")) {
	    diamondMined++;
	}
	if (CM.contains("ruby")) {
	    rubyMined++;
	}
	if (CM.contains("emerald")) {
	    emeraldMined++;
	}
	if (CM.contains("sapphire")) {
	    sapphireMined++;
	}
	if (CM.contains("you need a")) {
	    superheatError = 1;
	}
	if (CM.contains("enough fire")) {
	    wieldStaff = 1;
	}
	if (CM.contains("Magic level!")) {
	    checkMagic = true;
	}
	if (CM.contains("Smithing level!")) {
	    checkSmithing = true;
	}
	if (CM.contains("Mining level!")) {
	    checkMining = true;
	}
    }

    // WORLD HOPPPPPPPPPPPPPPPPING METHOD
    public boolean worldHop() {
	final RSObject rock = getNearestObjectByID(oreToMine2);
	int tries = 0;

	if (rock != null && distanceTo(rock.getLocation()) <= 10) {
	    return false;
	}

	while (isLoggedIn() && tries < 10) {
	    logout();
	    while (isLoggedIn()) {
		wait(random(1000, 1300));
		tries++;
	    }
	}

	logout();

	while (isLoggedIn()) {
	    wait(random(200, 300));
	}

	final int x = random(345, 415);
	final int y = random(242, 250);

	do {
	    moveMouse(x, y, 5, 5);
	} while (!mouseInArea(453, 250, 307, 237));

	clickMouse(true);
	wait(random(1000, 2000));

	int random = random(1, 70);
	if (random < 15 && random > 0) {
	    if (!member2) {
		scroll();
	    }
	}
	if (random < 35 && random > 25) {
	    if (!member2) {
		reorderTypeList();
	    } else {
		reorderTypeListMember();
	    }
	}

	final int x2 = random(84, 296);
	final int y2 = random(148, 450);

	do {
	    moveMouse(x2, y2, 5, 5);
	} while (!mouseInArea(534, 449, 83, 150));

	clickMouse(true);
	wait(random(1000, 2000));

	boolean didWeLogIn = true;

	final String RSText = RSInterface.getInterface(744).getChild(468)
		.getText();

	if (RSText.equals("World 1")) {
	    didWeLogIn = false;
	} else if (RSText.equals("World 2")) {
	    didWeLogIn = false;
	} else if (RSText.equals("World 3")) {
	    didWeLogIn = false;
	} else if (RSText.equals("World 144")) {
	    didWeLogIn = false;
	} else if (RSText.equals("World 44")) {
	    didWeLogIn = false;
	} else if (RSText.contains("World 65")) {
	    didWeLogIn = false;
	} else if (RSText.contains("World 26")) {
	    didWeLogIn = false;
	} else if (RSText.contains("World 86")) {
	    didWeLogIn = false;
	} else if (RSText.contains("World 124")) {
	    didWeLogIn = false;
	} else if (RSText.contains("World 18")) {
	    didWeLogIn = false;
	} else if (RSText.contains("World 72")) {
	    didWeLogIn = false;
	} else if (RSText.contains("World 137")) {
	    didWeLogIn = false;
	} else if (RSText.contains("World 136")) {
	    didWeLogIn = false;
	} else if (RSText.contains("World 57")) {
	    didWeLogIn = false;
	} else if (RSText.contains("World 32")) {
	    didWeLogIn = false;
	} else if (RSText.contains("World 21")) {
	    didWeLogIn = false;
	} else if (RSText.contains("World 17")) {
	    didWeLogIn = false;
	} else if (RSText.equals("World 31")) {
	    didWeLogIn = false;
	} else if (RSText.equals("World 9")) {
	    didWeLogIn = false;
	} else if (RSText.equals("World 6")) {
	    didWeLogIn = false;
	} else if (RSText.contains("PVP")) {
	    didWeLogIn = false;
	} else if (RSText.equals("World 113")) {
	    didWeLogIn = false;
	} else if (RSText.equals("World 114")) {
	    didWeLogIn = false;
	}

	if (!didWeLogIn) {
	    return worldHop();
	}
	while (!isLoggedIn() && !done) {
	    Login();
	}
	done = false;
	wait(random(1000, 2000));
	return isLoggedIn();
    }

    // WEBSITE OPENING
    public void openURL(final String url) {

	final String osName = System.getProperty("os.name");

	try {

	    if (osName.startsWith("Mac OS")) {

		final Class<?> fileMgr = Class
			.forName("com.apple.eio.FileManager");

		final Method openURL = fileMgr.getDeclaredMethod("openURL",

		new Class[] { String.class });

		openURL.invoke(null, new Object[] { url });

	    } else if (osName.startsWith("Windows")) {
		Runtime.getRuntime().exec(

		"rundll32 url.dll,FileProtocolHandler " + url);
	    } else {

		final String[] browsers = { "firefox", "opera", "konqueror",

		"epiphany", "mozilla", "netscape" };

		String browser = null;

		for (int count = 0; count < browsers.length && browser == null; count++) {
		    if (Runtime.getRuntime().exec(

		    new String[] { "which", browsers[count] })

		    .waitFor() == 0) {
			browser = browsers[count];
		    }
		}

		if (browser == null) {
		    throw new Exception("Could not find web browser");
		} else {
		    Runtime.getRuntime().exec(new String[] { browser, url });
		}

	    }

	} catch (final Exception e) {

	}
    }

    // GUI VARIABLES AND SUCH

    public class s extends javax.swing.JFrame {
	private static final long serialVersionUID = 1L;

	public s() {
	    initComponents();
	}

	private void initComponents() {

	    new javax.swing.JTabbedPane();
	    jFrame1 = new javax.swing.JFrame();
	    jLabel1 = new javax.swing.JLabel();
	    jLabel2 = new javax.swing.JLabel();
	    jTabbedPane2 = new javax.swing.JTabbedPane();
	    jPanel2 = new javax.swing.JPanel();
	    location = new javax.swing.JComboBox();
	    worldhop = new javax.swing.JComboBox();
	    oreToMine = new javax.swing.JComboBox();
	    jLabel3 = new javax.swing.JLabel();
	    jLabel15 = new javax.swing.JLabel();
	    jLabel28 = new javax.swing.JLabel();
	    jLabel10 = new javax.swing.JLabel();
	    antibanSpeed = new javax.swing.JComboBox();
	    jPanel4 = new javax.swing.JPanel();
	    worldCheck = new javax.swing.JCheckBox();
	    jLabel5 = new javax.swing.JLabel();
	    jLabel6 = new javax.swing.JLabel();
	    listOfWorlds = new javax.swing.JTextField();
	    jLabel7 = new javax.swing.JLabel();
	    jPanel5 = new javax.swing.JPanel();
	    alch = new javax.swing.JCheckBox();
	    superheat = new javax.swing.JCheckBox();
	    alchGems = new javax.swing.JCheckBox();
	    mithBars = new javax.swing.JCheckBox();
	    addyBars = new javax.swing.JCheckBox();
	    alchBars = new javax.swing.JCheckBox();
	    alchGOres = new javax.swing.JCheckBox();
	    alchSOres = new javax.swing.JCheckBox();
	    jPanel6 = new javax.swing.JPanel();
	    dropGems = new javax.swing.JCheckBox();
	    dropDia = new javax.swing.JCheckBox();
	    dropRub = new javax.swing.JCheckBox();
	    dropSapp = new javax.swing.JCheckBox();
	    dropEme = new javax.swing.JCheckBox();
	    useRest = new javax.swing.JCheckBox();
	    member = new javax.swing.JCheckBox();
	    showPaint = new javax.swing.JCheckBox();
	    startButton = new javax.swing.JButton();
	    visitThread = new javax.swing.JButton();
	    cancelButton = new javax.swing.JButton();

	    javax.swing.GroupLayout jFrame1Layout = new javax.swing.GroupLayout(
		    jFrame1.getContentPane());
	    jFrame1.getContentPane().setLayout(jFrame1Layout);
	    jFrame1Layout.setHorizontalGroup(jFrame1Layout.createParallelGroup(
		    javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 400,
		    Short.MAX_VALUE));
	    jFrame1Layout.setVerticalGroup(jFrame1Layout.createParallelGroup(
		    javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 300,
		    Short.MAX_VALUE));

	    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
	    setTitle("SS7's Adamantite And Mithril Miner GUI");
	    setAlwaysOnTop(false);
	    setBackground(new java.awt.Color(255, 255, 255));
	    setMinimumSize(new java.awt.Dimension(555, 235));
	    setResizable(false);
	    setUndecorated(false);

	    jLabel1.setFont(new java.awt.Font("Calibri", 0, 32));
	    jLabel1.setForeground(new java.awt.Color(0, 204, 255));
	    jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
	    jLabel1.setText("SS7's Adamantite And Mithril Miner v4.04");

	    jLabel2.setFont(new java.awt.Font("Century Gothic", 0, 14));
	    jLabel2.setForeground(new java.awt.Color(0, 0, 255));
	    jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
	    jLabel2
		    .setText("The Only World Hopping Addy And Mith Miner Out There!");

	    jTabbedPane2.setBackground(new java.awt.Color(0, 204, 255));

	    jPanel2.setBackground(new java.awt.Color(0, 204, 255));

	    location.setFont(new java.awt.Font("Verdana", 0, 11));
	    location.setModel(new javax.swing.DefaultComboBoxModel(
		    new String[] { "Draynor", "Al-Kharid", "Falador" }));

	    worldhop.setFont(new java.awt.Font("Verdana", 0, 11));
	    worldhop.setModel(new javax.swing.DefaultComboBoxModel(
		    new String[] { "World Hop", "Mine Other Rocks" }));

	    oreToMine.setFont(new java.awt.Font("Verdana", 0, 11)); // NOI18N
	    oreToMine.setModel(new javax.swing.DefaultComboBoxModel(
		    new String[] { "Adamantite", "Mithril",
			    "Adamantite And Mithril" }));

	    jLabel3.setFont(new java.awt.Font("Verdana", 1, 12));
	    jLabel3.setForeground(new java.awt.Color(255, 255, 255));
	    jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
	    jLabel3.setText("Ore To Mine: ");

	    jLabel15.setFont(new java.awt.Font("Verdana", 1, 12));
	    jLabel15.setForeground(new java.awt.Color(255, 255, 255));
	    jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
	    jLabel15.setText("Mining Method:");

	    jLabel28.setFont(new java.awt.Font("Verdana", 1, 12));
	    jLabel28.setForeground(new java.awt.Color(255, 255, 255));
	    jLabel28.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
	    jLabel28.setText("Location :");

	    jLabel10.setFont(new java.awt.Font("Verdana", 1, 12));
	    jLabel10.setForeground(new java.awt.Color(255, 255, 255));
	    jLabel10.setText("Antiban Speed:");

	    antibanSpeed.setFont(new java.awt.Font("Verdana", 0, 11));
	    antibanSpeed.setModel(new javax.swing.DefaultComboBoxModel(
		    new String[] { "Very Fast", "Fast", "Average", "Slow",
			    "Very Slow", "Don't Use" }));

	    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(
		    jPanel2);
	    jPanel2.setLayout(jPanel2Layout);
	    jPanel2Layout
		    .setHorizontalGroup(jPanel2Layout
			    .createParallelGroup(
				    javax.swing.GroupLayout.Alignment.LEADING)
			    .addGroup(
				    jPanel2Layout
					    .createSequentialGroup()
					    .addContainerGap()
					    .addGroup(
						    jPanel2Layout
							    .createParallelGroup(
								    javax.swing.GroupLayout.Alignment.LEADING)
							    .addComponent(
								    jLabel3)
							    .addComponent(
								    jLabel28))
					    .addPreferredGap(
						    javax.swing.LayoutStyle.ComponentPlacement.RELATED)
					    .addGroup(
						    jPanel2Layout
							    .createParallelGroup(
								    javax.swing.GroupLayout.Alignment.LEADING,
								    false)
							    .addComponent(
								    location,
								    0,
								    javax.swing.GroupLayout.DEFAULT_SIZE,
								    Short.MAX_VALUE)
							    .addComponent(
								    oreToMine,
								    javax.swing.GroupLayout.PREFERRED_SIZE,
								    151,
								    javax.swing.GroupLayout.PREFERRED_SIZE))
					    .addGap(18, 18, 18)
					    .addGroup(
						    jPanel2Layout
							    .createParallelGroup(
								    javax.swing.GroupLayout.Alignment.TRAILING)
							    .addComponent(
								    jLabel15)
							    .addComponent(
								    jLabel10))
					    .addPreferredGap(
						    javax.swing.LayoutStyle.ComponentPlacement.RELATED)
					    .addGroup(
						    jPanel2Layout
							    .createParallelGroup(
								    javax.swing.GroupLayout.Alignment.LEADING,
								    false)
							    .addComponent(
								    antibanSpeed,
								    0,
								    javax.swing.GroupLayout.DEFAULT_SIZE,
								    Short.MAX_VALUE)
							    .addComponent(
								    worldhop,
								    javax.swing.GroupLayout.PREFERRED_SIZE,
								    144,
								    javax.swing.GroupLayout.PREFERRED_SIZE))
					    .addContainerGap(
						    javax.swing.GroupLayout.DEFAULT_SIZE,
						    Short.MAX_VALUE)));
	    jPanel2Layout
		    .setVerticalGroup(jPanel2Layout
			    .createParallelGroup(
				    javax.swing.GroupLayout.Alignment.LEADING)
			    .addGroup(
				    javax.swing.GroupLayout.Alignment.TRAILING,
				    jPanel2Layout
					    .createSequentialGroup()
					    .addContainerGap(30,
						    Short.MAX_VALUE)
					    .addGroup(
						    jPanel2Layout
							    .createParallelGroup(
								    javax.swing.GroupLayout.Alignment.LEADING)
							    .addGroup(
								    jPanel2Layout
									    .createSequentialGroup()
									    .addGroup(
										    jPanel2Layout
											    .createParallelGroup(
												    javax.swing.GroupLayout.Alignment.BASELINE)
											    .addComponent(
												    jLabel3)
											    .addComponent(
												    oreToMine,
												    javax.swing.GroupLayout.PREFERRED_SIZE,
												    javax.swing.GroupLayout.DEFAULT_SIZE,
												    javax.swing.GroupLayout.PREFERRED_SIZE))
									    .addPreferredGap(
										    javax.swing.LayoutStyle.ComponentPlacement.RELATED)
									    .addGroup(
										    jPanel2Layout
											    .createParallelGroup(
												    javax.swing.GroupLayout.Alignment.LEADING)
											    .addComponent(
												    jLabel28)
											    .addComponent(
												    location,
												    javax.swing.GroupLayout.PREFERRED_SIZE,
												    20,
												    javax.swing.GroupLayout.PREFERRED_SIZE)))
							    .addGroup(
								    jPanel2Layout
									    .createSequentialGroup()
									    .addGroup(
										    jPanel2Layout
											    .createParallelGroup(
												    javax.swing.GroupLayout.Alignment.BASELINE)
											    .addComponent(
												    jLabel15)
											    .addComponent(
												    worldhop,
												    javax.swing.GroupLayout.PREFERRED_SIZE,
												    javax.swing.GroupLayout.DEFAULT_SIZE,
												    javax.swing.GroupLayout.PREFERRED_SIZE))
									    .addPreferredGap(
										    javax.swing.LayoutStyle.ComponentPlacement.RELATED)
									    .addGroup(
										    jPanel2Layout
											    .createParallelGroup(
												    javax.swing.GroupLayout.Alignment.BASELINE)
											    .addComponent(
												    antibanSpeed,
												    javax.swing.GroupLayout.PREFERRED_SIZE,
												    19,
												    javax.swing.GroupLayout.PREFERRED_SIZE)
											    .addComponent(
												    jLabel10))))
					    .addGap(27, 27, 27)));

	    jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL,
		    new java.awt.Component[] { location, worldhop });

	    jTabbedPane2.addTab("Main Settings", jPanel2);

	    jPanel4.setBackground(new java.awt.Color(0, 204, 255));

	    worldCheck.setBackground(new java.awt.Color(0, 204, 255));
	    worldCheck.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
	    worldCheck.setForeground(new java.awt.Color(255, 255, 255));
	    worldCheck.setSelected(true);
	    worldCheck.setText("Use all worlds except those already excluded?");

	    jLabel5.setFont(new java.awt.Font("Verdana", 1, 12));
	    jLabel5.setForeground(new java.awt.Color(255, 255, 255));
	    jLabel5
		    .setText("Uncheck if you want the script to NOT log into specific worlds");

	    jLabel6.setFont(new java.awt.Font("Verdana", 1, 12));
	    jLabel6.setForeground(new java.awt.Color(255, 255, 255));
	    jLabel6.setText("Worlds to exclude:");

	    jLabel7.setFont(new java.awt.Font("Verdana", 1, 12));
	    jLabel7.setForeground(new java.awt.Color(255, 255, 255));
	    jLabel7
		    .setText("Seperate each world with a coma and a space (Ex. 25, 34, 92)");

	    javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(
		    jPanel4);
	    jPanel4.setLayout(jPanel4Layout);
	    jPanel4Layout
		    .setHorizontalGroup(jPanel4Layout
			    .createParallelGroup(
				    javax.swing.GroupLayout.Alignment.LEADING)
			    .addGroup(
				    jPanel4Layout
					    .createSequentialGroup()
					    .addGroup(
						    jPanel4Layout
							    .createParallelGroup(
								    javax.swing.GroupLayout.Alignment.LEADING)
							    .addGroup(
								    jPanel4Layout
									    .createSequentialGroup()
									    .addContainerGap()
									    .addComponent(
										    worldCheck))
							    .addGroup(
								    jPanel4Layout
									    .createSequentialGroup()
									    .addContainerGap()
									    .addComponent(
										    jLabel5))
							    .addGroup(
								    jPanel4Layout
									    .createSequentialGroup()
									    .addGap(
										    21,
										    21,
										    21)
									    .addComponent(
										    jLabel6)
									    .addGap(
										    18,
										    18,
										    18)
									    .addComponent(
										    listOfWorlds,
										    javax.swing.GroupLayout.PREFERRED_SIZE,
										    356,
										    javax.swing.GroupLayout.PREFERRED_SIZE)))
					    .addContainerGap(11,
						    Short.MAX_VALUE)).addGroup(
				    javax.swing.GroupLayout.Alignment.TRAILING,
				    jPanel4Layout.createSequentialGroup()
					    .addContainerGap(62,
						    Short.MAX_VALUE)
					    .addComponent(jLabel7).addGap(53,
						    53, 53)));
	    jPanel4Layout
		    .setVerticalGroup(jPanel4Layout
			    .createParallelGroup(
				    javax.swing.GroupLayout.Alignment.LEADING)
			    .addGroup(
				    jPanel4Layout
					    .createSequentialGroup()
					    .addContainerGap()
					    .addComponent(worldCheck)
					    .addPreferredGap(
						    javax.swing.LayoutStyle.ComponentPlacement.RELATED)
					    .addComponent(jLabel5)
					    .addPreferredGap(
						    javax.swing.LayoutStyle.ComponentPlacement.RELATED)
					    .addGroup(
						    jPanel4Layout
							    .createParallelGroup(
								    javax.swing.GroupLayout.Alignment.LEADING)
							    .addComponent(
								    jLabel6)
							    .addComponent(
								    listOfWorlds,
								    javax.swing.GroupLayout.PREFERRED_SIZE,
								    javax.swing.GroupLayout.DEFAULT_SIZE,
								    javax.swing.GroupLayout.PREFERRED_SIZE))
					    .addPreferredGap(
						    javax.swing.LayoutStyle.ComponentPlacement.RELATED)
					    .addComponent(jLabel7)
					    .addContainerGap(
						    javax.swing.GroupLayout.DEFAULT_SIZE,
						    Short.MAX_VALUE)));

	    jTabbedPane2.addTab("World Hopping Settings", jPanel4);

	    jPanel5.setBackground(new java.awt.Color(0, 204, 255));

	    alch.setBackground(new java.awt.Color(0, 204, 255));
	    alch.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
	    alch.setForeground(new java.awt.Color(255, 255, 255));
	    alch.setText("Use alch spells?");

	    superheat.setBackground(new java.awt.Color(0, 204, 255));
	    superheat.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
	    superheat.setForeground(new java.awt.Color(255, 255, 255));
	    superheat.setText("Use super heat item?");

	    alchGems.setBackground(new java.awt.Color(0, 204, 255));
	    alchGems.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
	    alchGems.setForeground(new java.awt.Color(255, 255, 255));
	    alchGems.setText("Alch gems?");

	    mithBars.setBackground(new java.awt.Color(0, 204, 255));
	    mithBars.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
	    mithBars.setForeground(new java.awt.Color(255, 255, 255));
	    mithBars.setText("Super heat for mith bars?");

	    addyBars.setBackground(new java.awt.Color(0, 204, 255));
	    addyBars.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
	    addyBars.setForeground(new java.awt.Color(255, 255, 255));
	    addyBars.setText("Super heat for addy bars?");

	    alchBars.setBackground(new java.awt.Color(0, 204, 255));
	    alchBars.setFont(new java.awt.Font("Verdana", 1, 12));
	    alchBars.setForeground(new java.awt.Color(255, 255, 255));
	    alchBars.setText("Alch bars?");

	    alchGOres.setBackground(new java.awt.Color(0, 204, 255));
	    alchGOres.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
	    alchGOres.setForeground(new java.awt.Color(255, 255, 255));
	    alchGOres.setText("Alch addy, mith ores?");

	    alchSOres.setBackground(new java.awt.Color(0, 204, 255));
	    alchSOres.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
	    alchSOres.setForeground(new java.awt.Color(255, 255, 255));
	    alchSOres.setText("Alch coal ores?");

	    javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(
		    jPanel5);
	    jPanel5.setLayout(jPanel5Layout);
	    jPanel5Layout
		    .setHorizontalGroup(jPanel5Layout
			    .createParallelGroup(
				    javax.swing.GroupLayout.Alignment.LEADING)
			    .addGroup(
				    jPanel5Layout
					    .createSequentialGroup()
					    .addContainerGap()
					    .addGroup(
						    jPanel5Layout
							    .createParallelGroup(
								    javax.swing.GroupLayout.Alignment.LEADING)
							    .addComponent(
								    superheat)
							    .addGroup(
								    jPanel5Layout
									    .createSequentialGroup()
									    .addGap(
										    21,
										    21,
										    21)
									    .addGroup(
										    jPanel5Layout
											    .createParallelGroup(
												    javax.swing.GroupLayout.Alignment.LEADING)
											    .addComponent(
												    mithBars)
											    .addComponent(
												    addyBars))))
					    .addGap(18, 18, 18)
					    .addGroup(
						    jPanel5Layout
							    .createParallelGroup(
								    javax.swing.GroupLayout.Alignment.LEADING)
							    .addComponent(alch)
							    .addGroup(
								    jPanel5Layout
									    .createSequentialGroup()
									    .addGap(
										    21,
										    21,
										    21)
									    .addGroup(
										    jPanel5Layout
											    .createParallelGroup(
												    javax.swing.GroupLayout.Alignment.LEADING)
											    .addComponent(
												    alchGems)
											    .addComponent(
												    alchBars))
									    .addPreferredGap(
										    javax.swing.LayoutStyle.ComponentPlacement.RELATED)
									    .addGroup(
										    jPanel5Layout
											    .createParallelGroup(
												    javax.swing.GroupLayout.Alignment.LEADING)
											    .addComponent(
												    alchSOres)
											    .addComponent(
												    alchGOres))))
					    .addGap(9, 9, 9)));
	    jPanel5Layout
		    .setVerticalGroup(jPanel5Layout
			    .createParallelGroup(
				    javax.swing.GroupLayout.Alignment.LEADING)
			    .addGroup(
				    jPanel5Layout
					    .createSequentialGroup()
					    .addContainerGap()
					    .addGroup(
						    jPanel5Layout
							    .createParallelGroup(
								    javax.swing.GroupLayout.Alignment.BASELINE)
							    .addComponent(
								    superheat)
							    .addComponent(alch))
					    .addPreferredGap(
						    javax.swing.LayoutStyle.ComponentPlacement.RELATED)
					    .addGroup(
						    jPanel5Layout
							    .createParallelGroup(
								    javax.swing.GroupLayout.Alignment.BASELINE)
							    .addComponent(
								    addyBars)
							    .addComponent(
								    alchGems)
							    .addComponent(
								    alchGOres))
					    .addPreferredGap(
						    javax.swing.LayoutStyle.ComponentPlacement.RELATED)
					    .addGroup(
						    jPanel5Layout
							    .createParallelGroup(
								    javax.swing.GroupLayout.Alignment.BASELINE)
							    .addComponent(
								    mithBars)
							    .addComponent(
								    alchBars)
							    .addComponent(
								    alchSOres))
					    .addContainerGap(23,
						    Short.MAX_VALUE)));

	    jTabbedPane2.addTab("Superheat and Alching Settings", jPanel5);

	    jPanel6.setBackground(new java.awt.Color(0, 204, 255));

	    dropGems.setBackground(new java.awt.Color(0, 204, 255));
	    dropGems.setFont(new java.awt.Font("Verdana", 1, 12)); // NOI18N
	    dropGems.setForeground(new java.awt.Color(255, 255, 255));
	    dropGems.setText("Drop Gems?");

	    dropDia.setBackground(new java.awt.Color(0, 204, 255));
	    dropDia.setFont(new java.awt.Font("Verdana", 1, 12));
	    dropDia.setForeground(new java.awt.Color(255, 255, 255));
	    dropDia.setText("Drop Diamonds?");

	    dropRub.setBackground(new java.awt.Color(0, 204, 255));
	    dropRub.setFont(new java.awt.Font("Verdana", 1, 12));
	    dropRub.setForeground(new java.awt.Color(255, 255, 255));
	    dropRub.setText("Drop Rubies?");

	    dropSapp.setBackground(new java.awt.Color(0, 204, 255));
	    dropSapp.setFont(new java.awt.Font("Verdana", 1, 12));
	    dropSapp.setForeground(new java.awt.Color(255, 255, 255));
	    dropSapp.setText("Drop Sapphires?");

	    dropEme.setBackground(new java.awt.Color(0, 204, 255));
	    dropEme.setFont(new java.awt.Font("Verdana", 1, 12));
	    dropEme.setForeground(new java.awt.Color(255, 255, 255));
	    dropEme.setText("Drop Emeralds?");

	    useRest.setBackground(new java.awt.Color(0, 204, 255));
	    useRest.setFont(new java.awt.Font("Verdana", 1, 12));
	    useRest.setForeground(new java.awt.Color(255, 255, 255));
	    useRest.setSelected(true);
	    useRest.setText("Use resting feature?");

	    member.setBackground(new java.awt.Color(0, 204, 255));
	    member.setFont(new java.awt.Font("Verdana", 1, 12));
	    member.setForeground(new java.awt.Color(255, 255, 255));
	    member.setText("Are you a member?");

	    showPaint.setBackground(new java.awt.Color(0, 204, 255));
	    showPaint.setFont(new java.awt.Font("Verdana", 1, 12));
	    showPaint.setForeground(new java.awt.Color(255, 255, 255));
	    showPaint.setSelected(true);
	    showPaint.setText("Show Paint");

	    javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(
		    jPanel6);
	    jPanel6.setLayout(jPanel6Layout);
	    jPanel6Layout
		    .setHorizontalGroup(jPanel6Layout
			    .createParallelGroup(
				    javax.swing.GroupLayout.Alignment.LEADING)
			    .addGroup(
				    jPanel6Layout
					    .createSequentialGroup()
					    .addContainerGap()
					    .addGroup(
						    jPanel6Layout
							    .createParallelGroup(
								    javax.swing.GroupLayout.Alignment.LEADING)
							    .addComponent(
								    dropGems)
							    .addGroup(
								    jPanel6Layout
									    .createSequentialGroup()
									    .addGap(
										    21,
										    21,
										    21)
									    .addGroup(
										    jPanel6Layout
											    .createParallelGroup(
												    javax.swing.GroupLayout.Alignment.LEADING)
											    .addComponent(
												    dropDia)
											    .addComponent(
												    dropRub))
									    .addPreferredGap(
										    javax.swing.LayoutStyle.ComponentPlacement.RELATED)
									    .addGroup(
										    jPanel6Layout
											    .createParallelGroup(
												    javax.swing.GroupLayout.Alignment.LEADING)
											    .addComponent(
												    dropSapp)
											    .addComponent(
												    dropEme))))
					    .addPreferredGap(
						    javax.swing.LayoutStyle.ComponentPlacement.RELATED,
						    37, Short.MAX_VALUE)
					    .addGroup(
						    jPanel6Layout
							    .createParallelGroup(
								    javax.swing.GroupLayout.Alignment.LEADING)
							    .addComponent(
								    useRest)
							    .addComponent(
								    member)
							    .addComponent(
								    showPaint))
					    .addGap(39, 39, 39)));
	    jPanel6Layout
		    .setVerticalGroup(jPanel6Layout
			    .createParallelGroup(
				    javax.swing.GroupLayout.Alignment.LEADING)
			    .addGroup(
				    jPanel6Layout
					    .createSequentialGroup()
					    .addGap(15, 15, 15)
					    .addGroup(
						    jPanel6Layout
							    .createParallelGroup(
								    javax.swing.GroupLayout.Alignment.BASELINE)
							    .addComponent(
								    dropGems)
							    .addComponent(
								    useRest))
					    .addPreferredGap(
						    javax.swing.LayoutStyle.ComponentPlacement.RELATED)
					    .addGroup(
						    jPanel6Layout
							    .createParallelGroup(
								    javax.swing.GroupLayout.Alignment.BASELINE)
							    .addComponent(
								    dropDia)
							    .addComponent(
								    dropEme)
							    .addComponent(
								    member))
					    .addPreferredGap(
						    javax.swing.LayoutStyle.ComponentPlacement.RELATED)
					    .addGroup(
						    jPanel6Layout
							    .createParallelGroup(
								    javax.swing.GroupLayout.Alignment.BASELINE)
							    .addComponent(
								    dropRub)
							    .addComponent(
								    dropSapp)
							    .addComponent(
								    showPaint))
					    .addContainerGap(15,
						    Short.MAX_VALUE)));

	    jTabbedPane2.addTab("Other Settings", jPanel6);

	    startButton.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
	    startButton.setText("Start Script");
	    startButton.addActionListener(new java.awt.event.ActionListener() {
		public void actionPerformed(java.awt.event.ActionEvent evt) {
		    startButtonActionPerformed(evt);
		}
	    });

	    visitThread.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
	    visitThread.setText("Visit Thread");
	    visitThread.addActionListener(new java.awt.event.ActionListener() {
		public void actionPerformed(java.awt.event.ActionEvent evt) {
		    visitThreadActionPerformed(evt);
		}
	    });

	    cancelButton.setFont(new java.awt.Font("Century Gothic", 0, 14)); // NOI18N
	    cancelButton.setText("Cancel Script");
	    cancelButton.addActionListener(new java.awt.event.ActionListener() {
		public void actionPerformed(java.awt.event.ActionEvent evt) {
		    cancelButtonActionPerformed(evt);
		}
	    });

	    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
		    getContentPane());
	    getContentPane().setLayout(layout);
	    layout
		    .setHorizontalGroup(layout
			    .createParallelGroup(
				    javax.swing.GroupLayout.Alignment.LEADING)
			    .addGroup(
				    layout
					    .createSequentialGroup()
					    .addGroup(
						    layout
							    .createParallelGroup(
								    javax.swing.GroupLayout.Alignment.LEADING)
							    .addGroup(
								    layout
									    .createSequentialGroup()
									    .addGap(
										    109,
										    109,
										    109)
									    .addComponent(
										    visitThread,
										    javax.swing.GroupLayout.PREFERRED_SIZE,
										    137,
										    javax.swing.GroupLayout.PREFERRED_SIZE)
									    .addPreferredGap(
										    javax.swing.LayoutStyle.ComponentPlacement.RELATED)
									    .addComponent(
										    cancelButton,
										    javax.swing.GroupLayout.PREFERRED_SIZE,
										    137,
										    javax.swing.GroupLayout.PREFERRED_SIZE)
									    .addPreferredGap(
										    javax.swing.LayoutStyle.ComponentPlacement.RELATED)
									    .addComponent(
										    startButton,
										    javax.swing.GroupLayout.PREFERRED_SIZE,
										    137,
										    javax.swing.GroupLayout.PREFERRED_SIZE))
							    .addGroup(
								    javax.swing.GroupLayout.Alignment.TRAILING,
								    layout
									    .createSequentialGroup()
									    .addContainerGap()
									    .addComponent(
										    jLabel1,
										    javax.swing.GroupLayout.DEFAULT_SIZE,
										    535,
										    Short.MAX_VALUE))
							    .addGroup(
								    layout
									    .createSequentialGroup()
									    .addContainerGap()
									    .addComponent(
										    jLabel2,
										    javax.swing.GroupLayout.DEFAULT_SIZE,
										    535,
										    Short.MAX_VALUE))
							    .addGroup(
								    javax.swing.GroupLayout.Alignment.TRAILING,
								    layout
									    .createSequentialGroup()
									    .addContainerGap(
										    javax.swing.GroupLayout.DEFAULT_SIZE,
										    Short.MAX_VALUE)
									    .addComponent(
										    jTabbedPane2,
										    javax.swing.GroupLayout.PREFERRED_SIZE,
										    535,
										    javax.swing.GroupLayout.PREFERRED_SIZE)))
					    .addContainerGap()));

	    layout.linkSize(javax.swing.SwingConstants.HORIZONTAL,
		    new java.awt.Component[] { cancelButton, startButton,
			    visitThread });

	    layout
		    .setVerticalGroup(layout
			    .createParallelGroup(
				    javax.swing.GroupLayout.Alignment.LEADING)
			    .addGroup(
				    layout
					    .createSequentialGroup()
					    .addGap(4, 4, 4)
					    .addComponent(
						    jLabel1,
						    javax.swing.GroupLayout.PREFERRED_SIZE,
						    26,
						    javax.swing.GroupLayout.PREFERRED_SIZE)
					    .addPreferredGap(
						    javax.swing.LayoutStyle.ComponentPlacement.RELATED)
					    .addComponent(jLabel2)
					    .addPreferredGap(
						    javax.swing.LayoutStyle.ComponentPlacement.RELATED)
					    .addComponent(
						    jTabbedPane2,
						    javax.swing.GroupLayout.PREFERRED_SIZE,
						    133,
						    javax.swing.GroupLayout.PREFERRED_SIZE)
					    .addPreferredGap(
						    javax.swing.LayoutStyle.ComponentPlacement.RELATED)
					    .addGroup(
						    layout
							    .createParallelGroup(
								    javax.swing.GroupLayout.Alignment.BASELINE)
							    .addComponent(
								    visitThread,
								    javax.swing.GroupLayout.PREFERRED_SIZE,
								    23,
								    javax.swing.GroupLayout.PREFERRED_SIZE)
							    .addComponent(
								    cancelButton,
								    javax.swing.GroupLayout.PREFERRED_SIZE,
								    23,
								    javax.swing.GroupLayout.PREFERRED_SIZE)
							    .addComponent(
								    startButton,
								    javax.swing.GroupLayout.PREFERRED_SIZE,
								    23,
								    javax.swing.GroupLayout.PREFERRED_SIZE))
					    .addContainerGap(12,
						    Short.MAX_VALUE)));

	    pack();
	}// </editor-fold>

	private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {
	    if (alch.isSelected()) {
		alch2 = true;
		if (alchGems.isSelected()) {
		    alchG = true;
		}
		if (alchBars.isSelected()) {
		    alchB = true;
		}
		if (alchGOres.isSelected()) {
		    alchGO = true;
		}
		if (alchSOres.isSelected()) {
		    alchSO = true;
		}
	    }

	    if (location.getSelectedItem() == "Draynor") {
		draynor = true;
	    } else if (location.getSelectedItem() == "Al-Kharid") {
		alkharid = true;
	    } else if (location.getSelectedItem() == "Falador") {
		falador = true;
	    }

	    if (oreToMine.getSelectedItem() == "Adamantite") {
		oreToMine2 = addyRock;
		addy = true;
	    } else if (oreToMine.getSelectedItem() == "Mithril") {
		oreToMine2 = mithRock;
		mith = true;
	    } else if (oreToMine.getSelectedItem() == "Adamantite And Mithril") {
		oreToMine2 = mithAndAddyRock;
		both = true;
	    }

	    if (antibanSpeed.getSelectedItem() == "Very Fast") {
		antiBanSpeed = 100;
	    } else if (antibanSpeed.getSelectedItem() == "Fast") {
		antiBanSpeed = 150;
	    } else if (antibanSpeed.getSelectedItem() == "Average") {
		antiBanSpeed = 250;
	    } else if (antibanSpeed.getSelectedItem() == "Slow") {
		antiBanSpeed = 350;
	    } else if (antibanSpeed.getSelectedItem() == "Very Slow") {
		antiBanSpeed = 450;
	    } else if (antibanSpeed.getSelectedItem() == "Don't Use") {
		antiBanSpeed = 0;
	    }

	    if (member.isSelected()) {
		member2 = true;
	    }

	    if (showPaint.isSelected()) {
		paint = true;
	    } else {
		paint = false;
	    }

	    if (worldhop.getSelectedItem() == "World Hop") {
		worldHop = true;
	    } else {
		worldHop = false;
	    }

	    if (superheat.isSelected()) {
		superheat2 = true;
		worldHop = false;
		if (addyBars.isSelected()) {
		    addyBar = true;
		}
		if (mithBars.isSelected()) {
		    mithBar = true;
		}
	    }

	    if (!worldCheck.isSelected()) {
		customWorlds = extractStrings(listOfWorlds.getText());
	    }

	    if (dropGems.isSelected()) {
		if (dropDia.isSelected()) {
		    diamond = true;
		}
		if (dropRub.isSelected()) {
		    ruby = true;
		}
		if (dropEme.isSelected()) {
		    emerald = true;
		}
		if (dropSapp.isSelected()) {
		    sapphire = true;
		}
	    }

	    settings.oreToMine = oreToMine.getSelectedItem().toString();
	    settings.location = location.getSelectedItem().toString();
	    settings.miningMethod = worldhop.getSelectedItem().toString();
	    settings.antibanSpeed = antibanSpeed.getSelectedItem().toString();

	    settings.allWorlds = worldCheck.isSelected();
	    if (!settings.allWorlds) {
		settings.worlds = settings.extractIntegers(listOfWorlds
			.getText());
	    }

	    settings.superheat = superheat.isSelected();
	    if (settings.superheat) {
		settings.mithBars = mithBars.isSelected();
		settings.addyBars = addyBars.isSelected();
	    }
	    settings.alch = alch.isSelected();
	    if (settings.alch) {
		settings.alchBars = alchBars.isSelected();
		settings.alchGems = alchGems.isSelected();
		settings.alchOres = alchGOres.isSelected();
		settings.alchCoal = alchSOres.isSelected();
	    }

	    settings.dropGems = dropGems.isSelected();
	    if (settings.dropGems) {
		settings.dropDiamonds = dropDia.isSelected();
		settings.dropRubies = dropRub.isSelected();
		settings.dropEmeralds = dropEme.isSelected();
		settings.dropSapphires = dropSapp.isSelected();
	    }
	    settings.useRest = useRest.isSelected();
	    settings.showPaint = showPaint.isSelected();
	    settings.member = member.isSelected();

	    try {
		settings.saveSettings(settings.getSettingsArray());
	    } catch (final Exception e) {
		log.log(Level.SEVERE, "saving settings error: ", e);
	    }

	    setVisible(false);
	    startScript = true;

	}

	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
	    setVisible(false);
	    stopScript();
	}

	private void visitThreadActionPerformed(java.awt.event.ActionEvent evt) {
	    final int redirect = JOptionPane.showConfirmDialog(null,
		    "Are you sure you want to visit the thread?",
		    "Redirecting", JOptionPane.YES_NO_OPTION);
	    if (redirect == 0) {
		final String message = "<html><h1>Redirecting</h1><br/>"
			+ "<p>You will now be redirected to the Website. <br/>"
			+ "</p>" + "</html>";
		JOptionPane.showMessageDialog(null, message);
		openURL("http://www.rsbot.org/vb/showthread.php?p=1059067");
	    }
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public void main(String args[]) {
	    java.awt.EventQueue.invokeLater(new Runnable() {

		public void run() {
		    new s().setVisible(true);
		}
	    });
	}
    }
}