import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.rsbot.bot.Bot;
import org.rsbot.bot.input.Listener;
import org.rsbot.bot.input.Mouse;
import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;
import org.rsbot.util.GlobalConfiguration;
import org.rsbot.util.ScreenshotUtil;

/**
 * @author SpeedWing
 * @version 3.033 (c)2009-2010 SpeedWing, No one except SpeedWing has the right
 *          to modify and/or spread this script without the permission of
 *          SpeedWing.
 */
@ScriptManifest(authors = { "SpeedWing" }, category = "Runecraft", name = "RuneSpeed AIO Runecrafter", version = 3.033, description = "<html>\n"
		+ "<body style='font-family: Calibri; color:white; padding: 0px; text-align: center; background-color: black;'>"
		+ "<img src=\"http://speedwing.ucoz.com/RuneSpeed/RuneSpeed_description.png\" /><br>")
public class RuneSpeed extends Script implements PaintListener,
		ServerMessageListener {
	// INTS
	int essence[] = { 1436, 7936 }, Runes[] = { 556, 558, 555, 557, 554, 559 },
			AllDone, Rune, Tiara, AirTiara = 5527, EarthTiara = 5535,
			FireTiara = 5537, WaterTiara = 5531, MindTiara = 5529,
			BodyTiara = 5533, CurBank, CurOutside, CurAltar, ExitPortal,
			AirBankID = 11402, AirOutsideID = 2452, AirAltarID = 2478,
			AirExit = 2465, EarthBankID = 11402, EarthOutsideID = 2455,
			EarthAltarID = 2481, EarthExit = 2468, FireBankID = 35647,
			FireOutsideID = 2456, FireAltarID = 2482, FireExit = 2469,
			WaterBankID = 2213, WaterOutsideID = 2454, WaterAltarID = 2480,
			WaterExit = 2467, MindBankID = 11758, MindOutsideID = 2453,
			MindAltarID = 2479, MindExit = 2466, BodyBankID = 26972,
			BodyOutsideID = 2457, BodyAltarID = 2483, BodyExit = 2470,
			essleft = 0, essInInv = 0, ExtraTimout = 0, essprice = 0,
			runeprice, runeID = 0, essID, Profit = 0, SamePath = 0,
			WaitForEnergy = 0, mouseSpeed, clickOrPress = 0;
	String globalday, globalhour, globalminute, globalsecond;
	int airRune = 556, mindRune = 558, waterRune = 555, earthRune = 557,
			fireRune = 554, bodyRune = 559;
	// STATES//
	boolean banking, bankwalk, altarwalk, crafting, getessence, grabitems,
			inbank, exitaltar, walking, run, tiaracheck, tiarabank = false,
			isTiaraOn = false, openit = false, enterit = false,
			portalturn = true, gotPrices = false, CameraTurned = false,
			gotActivity = false, shutDown = false;
	// special transportation
	boolean tele = false, varrockTeleport = false, fallyTeleport = false,
			getTele = false, gotTeleStats = false;
	int runesInInv = 0, thirdTeleRune, teleports = 0, telePrice = 0;
	boolean gloryTeleport = false, getGlory = false;
	int glory4 = 1712, glory3 = 1710, glory2 = 1708, glory1 = 1706,
			glory0 = 1704;
	int[] gloryArray = { glory1, glory2, glory3, glory4 };
	boolean tabTeleport = false, tabClicked = false;
	int curTab, airTab = 13599, mindTab = 13600, waterTab = 13601,
			earthTab = 13602, fireTab = 13603, bodyTab = 13604;
	boolean ringTeleport = false, stileCrossed = false;
	int explorerRing = 13562, stileID = 7527;
	boolean duelTeleport = false, getDuel = false, duelArrived = false;
	int duel1 = 2566, duel2 = 2564, duel3 = 2562, duel4 = 2560, duel5 = 2558,
			duel6 = 2556, duel7 = 2554, duel8 = 2552;
	int[] duelArray = { duel2, duel3, duel4, duel5, duel6, duel7, duel8 };
	int[] duelleaveArray = { duel1, duel2, duel3, duel4, duel5, duel6, duel7,
			duel8 };
	boolean antibanstate = true;
	boolean rest = true;
	double GlobalChance;
	RSTile GlobalTile;
	boolean TurnTheCamera = false;
	AntiBan antiban;
	Thread t;
	boolean RestStat = false;
	boolean RestCamera = false;
	// Running
	char CONTROL = KeyEvent.VK_CONTROL;
	String TiaraName = "";
	int MusicianID[] = { 8699, 8700, 8707, 5442 };
	// ///////
	// PATHS//
	RSTile[][] path;
	RSTile[] RealPath;
	RSTile[] magicTelePath;
	RSTile[] prevPath = new RSTile[50];
	int pathar[] = new int[3];
	RSTile[] AirAltar1 = { new RSTile(3185, 3434), new RSTile(3182, 3430),
			new RSTile(3176, 3429), new RSTile(3170, 3428),
			new RSTile(3163, 3422), new RSTile(3158, 3418),
			new RSTile(3152, 3416), new RSTile(3148, 3415),
			new RSTile(3143, 3413), new RSTile(3141, 3410),
			new RSTile(3138, 3406), new RSTile(3135, 3406),
			new RSTile(3131, 3405) };
	RSTile[] AirAltar2 = { new RSTile(3185, 3434), new RSTile(3182, 3429),
			new RSTile(3177, 3429), new RSTile(3172, 3426),
			new RSTile(3168, 3424), new RSTile(3165, 3420),
			new RSTile(3163, 3421), new RSTile(3158, 3418),
			new RSTile(3152, 3416), new RSTile(3148, 3416),
			new RSTile(3143, 3412), new RSTile(3136, 3406),
			new RSTile(3131, 3405) };
	RSTile[] AirAltar3 = { new RSTile(3185, 3434), new RSTile(3172, 3429),
			new RSTile(3162, 3423), new RSTile(3156, 3422),
			new RSTile(3148, 3417), new RSTile(3140, 3408),
			new RSTile(3131, 3405) };
	RSTile[] AirTelePath = { new RSTile(3185, 3434), new RSTile(3196, 3429),
			new RSTile(3207, 3429) };
	RSTile[][] AirPath = { AirAltar1, AirAltar2, AirAltar3 };
	RSTile[] EarthAltar1 = { new RSTile(3253, 3422), new RSTile(3254, 3428),
			new RSTile(3260, 3429), new RSTile(3266, 3428),
			new RSTile(3272, 3428), new RSTile(3277, 3430),
			new RSTile(3278, 3439), new RSTile(3283, 3446),
			new RSTile(3286, 3456), new RSTile(3297, 3463),
			new RSTile(3304, 3472) };
	RSTile[] EarthAltar2 = { new RSTile(3253, 3422), new RSTile(3254, 3429),
			new RSTile(3265, 3429), new RSTile(3275, 3430),
			new RSTile(3274, 3438), new RSTile(3281, 3445),
			new RSTile(3287, 3453), new RSTile(3293, 3462),
			new RSTile(3304, 3472) };
	RSTile[] EarthAltar3 = { new RSTile(3253, 3422), new RSTile(3260, 3429),
			new RSTile(3270, 3429), new RSTile(3278, 3428),
			new RSTile(3286, 3435), new RSTile(3286, 3444),
			new RSTile(3289, 3453), new RSTile(3295, 3462),
			new RSTile(3297, 3467), new RSTile(3304, 3472) };
	RSTile[][] EarthPath = { EarthAltar1, EarthAltar2, EarthAltar3 };
	RSTile[] EarthTelePath = { new RSTile(3253, 3422), new RSTile(3253, 3428),
			new RSTile(3247, 3430), new RSTile(3237, 3430),
			new RSTile(3226, 3429) };
	RSTile[] FireAltar1 = { new RSTile(3271, 3167), new RSTile(3277, 3174),
			new RSTile(3282, 3182), new RSTile(3283, 3195),
			new RSTile(3286, 3207), new RSTile(3295, 3218),
			new RSTile(3300, 3232), new RSTile(3303, 3244),
			new RSTile(3311, 3252) };
	RSTile[] FireAltar2 = { new RSTile(3271, 3167), new RSTile(3276, 3176),
			new RSTile(3281, 3191), new RSTile(3285, 3199),
			new RSTile(3290, 3213), new RSTile(3296, 3220),
			new RSTile(3301, 3229), new RSTile(3306, 3239),
			new RSTile(3311, 3252) };
	RSTile[] FireAltar3 = { new RSTile(3271, 3167), new RSTile(3277, 3174),
			new RSTile(3282, 3183), new RSTile(3284, 3195),
			new RSTile(3286, 3200), new RSTile(3290, 3210),
			new RSTile(3298, 3217), new RSTile(3300, 3227),
			new RSTile(3304, 3237), new RSTile(3307, 3241),
			new RSTile(3311, 3252) };
	RSTile[][] FirePath = { FireAltar1, FireAltar2, FireAltar3 };
	RSTile[] FireTelePath = { new RSTile(3271, 3167), new RSTile(3276, 3174),
			new RSTile(3283, 3183), new RSTile(3292, 3182),
			new RSTile(3292, 3168) };
	RSTile[] FireDuelPath = { new RSTile(3307, 3236), new RSTile(3307, 3241),
			new RSTile(3309, 3251), new RSTile(3311, 3252) };
	RSTile[] WaterAltar1 = { new RSTile(3094, 3244), new RSTile(3098, 3236),
			new RSTile(3102, 3225), new RSTile(3110, 3220),
			new RSTile(3117, 3211), new RSTile(3124, 3207),
			new RSTile(3135, 3207), new RSTile(3144, 3207),
			new RSTile(3151, 3194), new RSTile(3159, 3187),
			new RSTile(3162, 3178), new RSTile(3166, 3169),
			new RSTile(3174, 3166), new RSTile(3182, 3167) };
	RSTile[] WaterAltar2 = { new RSTile(3094, 3244), new RSTile(3096, 3234),
			new RSTile(3102, 3226), new RSTile(3109, 3223),
			new RSTile(3115, 3218), new RSTile(3127, 3219),
			new RSTile(3136, 3218), new RSTile(3144, 3209),
			new RSTile(3151, 3201), new RSTile(3156, 3191),
			new RSTile(3158, 3184), new RSTile(3164, 3176),
			new RSTile(3172, 3175), new RSTile(3173, 3167),
			new RSTile(3182, 3167) };
	RSTile[] WaterAltar3 = { new RSTile(3094, 3244), new RSTile(3098, 3238),
			new RSTile(3101, 3229), new RSTile(3113, 3223),
			new RSTile(3122, 3220), new RSTile(3135, 3219),
			new RSTile(3143, 3211), new RSTile(3147, 3205),
			new RSTile(3153, 3196), new RSTile(3157, 3186),
			new RSTile(3163, 3178), new RSTile(3163, 3172),
			new RSTile(3170, 3168), new RSTile(3175, 3165),
			new RSTile(3182, 3167) };
	RSTile[][] WaterPath = { WaterAltar1, WaterAltar2, WaterAltar3 };
	RSTile[] WaterTelePath = { new RSTile(3094, 3244), new RSTile(3083, 3248),
			new RSTile(3080, 3251), new RSTile(3073, 3257),
			new RSTile(3072, 3265), new RSTile(3073, 3273),
			new RSTile(3067, 3276), new RSTile(3063, 3280),
			new RSTile(3063, 3286) };
	RSTile[] MindAltar1 = { new RSTile(2945, 3370), new RSTile(2955, 3382),
			new RSTile(2965, 3393), new RSTile(2973, 3405),
			new RSTile(2982, 3417), new RSTile(2987, 3430),
			new RSTile(2987, 3442), new RSTile(2982, 3451),
			new RSTile(2975, 3460), new RSTile(2972, 3471),
			new RSTile(2974, 3483), new RSTile(2978, 3495),
			new RSTile(2980, 3504), new RSTile(2980, 3511) };
	RSTile[] MindAltar2 = { new RSTile(2945, 3370), new RSTile(2952, 3377),
			new RSTile(2965, 3383), new RSTile(2965, 3390),
			new RSTile(2971, 3398), new RSTile(2973, 3407),
			new RSTile(2983, 3417), new RSTile(2989, 3430),
			new RSTile(2985, 3440), new RSTile(2984, 3450),
			new RSTile(2979, 3457), new RSTile(2973, 3469),
			new RSTile(2969, 3480), new RSTile(2973, 3492),
			new RSTile(2977, 3504), new RSTile(2980, 3511) };
	RSTile[] MindAltar3 = { new RSTile(2945, 3370), new RSTile(2950, 3375),
			new RSTile(2963, 3383), new RSTile(2966, 3397),
			new RSTile(2967, 3406), new RSTile(2975, 3410),
			new RSTile(2985, 3420), new RSTile(2987, 3430),
			new RSTile(2983, 3440), new RSTile(2977, 3451),
			new RSTile(2974, 3461), new RSTile(2972, 3473),
			new RSTile(2973, 3482), new RSTile(2977, 3494),
			new RSTile(2979, 3504), new RSTile(2980, 3511) };
	RSTile[][] MindPath = { MindAltar1, MindAltar2, MindAltar3 };
	RSTile[] BodyAltar1 = { new RSTile(3092, 3491), new RSTile(3082, 3485),
			new RSTile(3081, 3477), new RSTile(3084, 3466),
			new RSTile(3088, 3452), new RSTile(3074, 3451),
			new RSTile(3068, 3442), new RSTile(3055, 3443) };
	RSTile[] BodyAltar2 = { new RSTile(3092, 3491), new RSTile(3081, 3485),
			new RSTile(3081, 3477), new RSTile(3087, 3466),
			new RSTile(3075, 3460), new RSTile(3071, 3451),
			new RSTile(3064, 3437), new RSTile(3055, 3443) };
	RSTile[] BodyAltar3 = { new RSTile(3092, 3491), new RSTile(3087, 3490),
			new RSTile(3079, 3481), new RSTile(3081, 3476),
			new RSTile(3087, 3465), new RSTile(3077, 3461),
			new RSTile(3072, 3455), new RSTile(3067, 3447),
			new RSTile(3063, 3449), new RSTile(3055, 3443) };
	RSTile[][] BodyPath = { BodyAltar1, BodyAltar2, BodyAltar3 };

	public double latestVersion;
	public double CurrentVersion = getClass().getAnnotation(
			ScriptManifest.class).version();
	// ///////
	// PAINT//
	int paintX = 294;
	int paintY = 4;
	long startTime = 0;
	long expraise;
	int startLvl;
	int startExp;
	int crafted;
	int essused;
	double runexp;
	long EXPPerHour;
	String status = "";
	BufferedImage overlay = null;
	BufferedImage cursor = null;
	BufferedImage cursor80 = null;
	BufferedImage cursor60 = null;
	BufferedImage cursor40 = null;
	BufferedImage cursor20 = null;
	RSTile[] mapPath = new RSTile[200];

	// GUI SETTINGS
	boolean Done = false, ini = false, close = false;
	public static String SelectedRune;
	public static String SelectedRest;
	public static String SelectedTiara;
	public static String SelectedTab;
	public static String SelectedCamera;
	public static String SelectedOverlay;
	public static String SelectedOverlayColour;
	public static String SelectedLogout;
	public static double SelectedRuinsCamera;
	public static double SelectedAltarCamera;
	public static double SelectedBankCamera;
	public static long LevelLogout;
	public static long EssenceLogout;
	public static long MinutesLogout;
	long minutesStop = 0;

	public class GUI extends JFrame {
		private static final long serialVersionUID = -234234L;
		private JComboBox RuneCombo;
		private JComboBox RestCombo;
		private JComboBox TiaraCombo;
		private JComboBox OverlayCombo;
		private JComboBox OverlayColourCombo;
		private JComboBox RuinsCameraCombo;
		private JComboBox CameraCombo;
		private JComboBox BankCameraCombo;
		private JComboBox AltarCameraCombo;
		private JTextField HourTextField;
		private JTextField MinutesTextField;
		private JTextField LevelTextField;
		private JTextField EssenceTextField;
		private JTextArea changeLogTextArea;
		private JComboBox TabCombo;
		private JComboBox logoutCombo;
		public Properties RSpeedIni = new Properties();
		private final File changeLogFile = new File(new File(
				GlobalConfiguration.Paths.getSettingsDirectory()),
				"RuneSpeedChangeLog.txt");// credits to Taha(SmartSmelter)

		public boolean loadSettings() {
			try {
				RSpeedIni.load(new FileInputStream(new File(
						GlobalConfiguration.Paths.getSettingsDirectory(),
						"RuneSpeed.ini")));
			} catch (FileNotFoundException e) {
				ini = true;
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			if (RSpeedIni.getProperty("Rune") != null) {
				RuneCombo.setSelectedIndex(Integer.valueOf(RSpeedIni
						.getProperty("Rune")));
			}
			if (RSpeedIni.getProperty("Rest") != null) {
				RestCombo.setSelectedIndex(Integer.valueOf(RSpeedIni
						.getProperty("Rest")));
			}
			if (RSpeedIni.getProperty("Tiara") != null) {
				TiaraCombo.setSelectedIndex(Integer.valueOf(RSpeedIni
						.getProperty("Tiara")));
			}
			if (RSpeedIni.getProperty("Tab") != null) {
				TabCombo.setSelectedIndex(Integer.valueOf(RSpeedIni
						.getProperty("Tab")));
			}
			return true;
		}

		public void loadChangeLog() {
			try {
				changeLogFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			BufferedReader in = null;
			try {
				in = new BufferedReader(new InputStreamReader(new URL(
						"http://speedwing.ucoz.com/RuneSpeed/RuneSpeedLog.txt")
						.openStream()));
			} catch (MalformedURLException e1) {
				log("ChangeLog not found.");
			} catch (IOException e1) {
				log("Unable to Download ChangeLog");
			}
			try {
				final BufferedWriter out = new BufferedWriter(new FileWriter(
						changeLogFile));
				String temp;
				while ((temp = in.readLine()) != null) {
					out.append(temp);
					out.newLine();
				}
				out.close();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				changeLogTextArea.read(new BufferedReader(new FileReader(
						changeLogFile)), changeLogFile);
			} catch (FileNotFoundException e) {
				log("ChangeLog not Found.");
			} catch (IOException e) {
				log("Unable to open the ChangeLog");
			}
		}// credits to Taha(SmartSmelter)

		public void OpenUrl(final String URL) {
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

		/**
		 * Create the frame.
		 */
		public GUI() {
			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosed(WindowEvent arg0) {
					close = true;
					Done = true;
				}
			});
			setResizable(false);
			setTitle("RuneSpeed by SpeedWing");
			setBounds(100, 100, 456, 258);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			getContentPane().setLayout(null);

			JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.LEFT);
			tabbedPane.setBounds(10, 11, 419, 180);
			tabbedPane.setFont(new Font("Calibri", Font.BOLD, 11));
			getContentPane().add(tabbedPane);

			Panel panel = new Panel();
			tabbedPane
					.addTab(
							"Main Options",
							new ImageIcon(
									GUI.class
											.getResource("/javax/swing/plaf/metal/icons/ocean/computer.gif")),
							panel, null);
			panel.setLayout(null);

			JLabel lblMainOptions = new JLabel("Main Options");
			lblMainOptions.setBounds(10, 11, 307, 37);
			lblMainOptions.setHorizontalAlignment(SwingConstants.CENTER);
			lblMainOptions.setFont(new Font("Calibri", Font.BOLD, 15));
			panel.add(lblMainOptions);

			JLabel lblRuneToCraft = new JLabel("What to Craft:");
			lblRuneToCraft.setBounds(56, 52, 101, 22);
			lblRuneToCraft.setHorizontalAlignment(SwingConstants.CENTER);
			lblRuneToCraft.setFont(new Font("Calibri", Font.PLAIN, 11));
			panel.add(lblRuneToCraft);

			JLabel lblResting = new JLabel("Resting:");
			lblResting.setBounds(56, 73, 101, 22);
			lblResting.setHorizontalAlignment(SwingConstants.CENTER);
			lblResting.setFont(new Font("Calibri", Font.PLAIN, 11));
			panel.add(lblResting);

			JLabel lblTiaraChecking = new JLabel("Tiara Checking:");
			lblTiaraChecking.setBounds(56, 95, 101, 22);
			lblTiaraChecking.setHorizontalAlignment(SwingConstants.CENTER);
			lblTiaraChecking.setFont(new Font("Calibri", Font.PLAIN, 11));
			panel.add(lblTiaraChecking);

			RuneCombo = new JComboBox();
			RuneCombo.setToolTipText("");
			RuneCombo.setBounds(169, 52, 105, 22);
			RuneCombo.setModel(new DefaultComboBoxModel(new String[] { "Air",
					"Air(tele)", "Mind", "Mind(tele)", "Earth", "Earth(tele)",
					"Water", "Water(glory)", "Water(cabbage)", "Fire",
					"Fire(glory)", "Fire(ring)", "Body", "Body(glory)" }));
			RuneCombo.setFont(new Font("Calibri", Font.PLAIN, 11));
			panel.add(RuneCombo);

			RestCombo = new JComboBox();
			RestCombo.setBounds(169, 73, 105, 22);
			RestCombo.setModel(new DefaultComboBoxModel(new String[] { "On",
					"Off" }));
			RestCombo.setFont(new Font("Calibri", Font.PLAIN, 11));
			panel.add(RestCombo);

			TiaraCombo = new JComboBox();
			TiaraCombo.setBounds(169, 95, 105, 22);
			TiaraCombo.setModel(new DefaultComboBoxModel(new String[] { "On",
					"Off" }));
			TiaraCombo.setFont(new Font("Calibri", Font.PLAIN, 11));
			panel.add(TiaraCombo);

			TabCombo = new JComboBox();
			TabCombo.setModel(new DefaultComboBoxModel(new String[] { "On",
					"Off" }));
			TabCombo.setFont(new Font("Calibri", Font.PLAIN, 11));
			TabCombo.setBounds(169, 116, 105, 22);
			panel.add(TabCombo);

			JLabel lblTeleTabs = new JLabel("Altar Tabs:");
			lblTeleTabs.setHorizontalAlignment(SwingConstants.CENTER);
			lblTeleTabs.setFont(new Font("Calibri", Font.PLAIN, 11));
			lblTeleTabs.setBounds(56, 116, 101, 22);
			panel.add(lblTeleTabs);

			Panel panel_3 = new Panel();
			tabbedPane.addTab("Camera Anti-Ban", null, panel_3, null);
			panel_3.setLayout(null);

			JLabel lblCameraBank = new JLabel("Camera Bank Chance:");
			lblCameraBank.setHorizontalAlignment(SwingConstants.CENTER);
			lblCameraBank.setFont(new Font("Calibri", Font.PLAIN, 11));
			lblCameraBank.setBounds(12, 75, 152, 22);
			panel_3.add(lblCameraBank);

			JLabel lblOnlyAdjustThese = new JLabel(
					"Adjust these only if you know what you are doing else just");
			lblOnlyAdjustThese.setIcon(null);
			lblOnlyAdjustThese.setHorizontalAlignment(SwingConstants.CENTER);
			lblOnlyAdjustThese.setForeground(Color.RED);
			lblOnlyAdjustThese.setFont(new Font("Calibri", Font.PLAIN, 11));
			lblOnlyAdjustThese.setBounds(12, 49, 297, 14);
			panel_3.add(lblOnlyAdjustThese);

			BankCameraCombo = new JComboBox();
			BankCameraCombo
					.setToolTipText("Will set the chance to turn the camera upon banking");
			BankCameraCombo.setModel(new DefaultComboBoxModel(new String[] {
					"100", "95", "90", "85", "80", "75", "70", "65", "60",
					"55", "50", "45", "40", "35", "30", "25", "20", "15", "10",
					"5", "0" }));
			BankCameraCombo.setSelectedIndex(13);
			BankCameraCombo.setFont(new Font("Calibri", Font.PLAIN, 11));
			BankCameraCombo.setBounds(176, 75, 55, 22);
			panel_3.add(BankCameraCombo);

			RuinsCameraCombo = new JComboBox();
			RuinsCameraCombo
					.setToolTipText("Will set the chance to turn the camera upon Entering the Ruins");
			RuinsCameraCombo.setModel(new DefaultComboBoxModel(new String[] {
					"100", "95", "90", "85", "80", "75", "70", "65", "60",
					"55", "50", "45", "40", "35", "30", "25", "20", "15", "10",
					"5", "0" }));
			RuinsCameraCombo.setSelectedIndex(14);
			RuinsCameraCombo.setFont(new Font("Calibri", Font.PLAIN, 11));
			RuinsCameraCombo.setBounds(176, 98, 55, 22);
			panel_3.add(RuinsCameraCombo);

			JLabel lblCameraRuinsChance = new JLabel("Camera Ruins Chance:");
			lblCameraRuinsChance.setHorizontalAlignment(SwingConstants.CENTER);
			lblCameraRuinsChance.setFont(new Font("Calibri", Font.PLAIN, 11));
			lblCameraRuinsChance.setBounds(12, 98, 152, 22);
			panel_3.add(lblCameraRuinsChance);

			JLabel lblCameraAltarLeave = new JLabel(
					"Camera inside Altar Chance:");
			lblCameraAltarLeave.setHorizontalAlignment(SwingConstants.CENTER);
			lblCameraAltarLeave.setFont(new Font("Calibri", Font.PLAIN, 11));
			lblCameraAltarLeave.setBounds(12, 122, 152, 22);
			panel_3.add(lblCameraAltarLeave);

			JLabel label_1 = new JLabel("Camera Anti-Ban");
			label_1.setHorizontalAlignment(SwingConstants.CENTER);
			label_1.setFont(new Font("Calibri", Font.PLAIN, 11));
			label_1.setBounds(32, 13, 101, 22);
			panel_3.add(label_1);

			CameraCombo = new JComboBox();
			CameraCombo.setModel(new DefaultComboBoxModel(new String[] { "On",
					"Off" }));
			CameraCombo.setFont(new Font("Calibri", Font.PLAIN, 11));
			CameraCombo.setBounds(174, 11, 55, 22);
			panel_3.add(CameraCombo);

			JLabel lblElseJustLeave = new JLabel("leave them as they are.");
			lblElseJustLeave.setHorizontalAlignment(SwingConstants.CENTER);
			lblElseJustLeave.setForeground(Color.RED);
			lblElseJustLeave.setFont(new Font("Calibri", Font.PLAIN, 11));
			lblElseJustLeave.setBounds(12, 60, 297, 14);
			panel_3.add(lblElseJustLeave);

			AltarCameraCombo = new JComboBox();
			AltarCameraCombo.setModel(new DefaultComboBoxModel(new String[] {
					"100", "95", "90", "85", "80", "75", "70", "65", "60",
					"55", "50", "45", "40", "35", "30", "25", "20", "15", "10",
					"5", "0" }));
			AltarCameraCombo.setSelectedIndex(13);
			AltarCameraCombo
					.setToolTipText("Will set the chance to turn the camera when clicking the altar & leaving the altar");
			AltarCameraCombo.setFont(new Font("Calibri", Font.PLAIN, 11));
			AltarCameraCombo.setBounds(176, 120, 55, 22);
			panel_3.add(AltarCameraCombo);

			JLabel label_2 = new JLabel("%");
			label_2.setHorizontalAlignment(SwingConstants.LEFT);
			label_2.setFont(new Font("Calibri", Font.PLAIN, 11));
			label_2.setBounds(234, 75, 71, 22);
			panel_3.add(label_2);

			JLabel label_3 = new JLabel("%");
			label_3.setHorizontalAlignment(SwingConstants.LEFT);
			label_3.setFont(new Font("Calibri", Font.PLAIN, 11));
			label_3.setBounds(234, 98, 71, 22);
			panel_3.add(label_3);

			JLabel label_4 = new JLabel("%");
			label_4.setHorizontalAlignment(SwingConstants.LEFT);
			label_4.setFont(new Font("Calibri", Font.PLAIN, 11));
			label_4.setBounds(234, 122, 71, 22);
			panel_3.add(label_4);

			Panel panel_1 = new Panel();
			tabbedPane
					.addTab(
							"Overlay",
							new ImageIcon(
									GUI.class
											.getResource("/javax/swing/plaf/metal/icons/ocean/maximize-pressed.gif")),
							panel_1, null);
			panel_1.setLayout(null);

			JLabel lblPaint = new JLabel("Overlay Options");
			lblPaint.setBounds(10, 11, 297, 37);
			lblPaint.setFont(new Font("Calibri", Font.BOLD, 15));
			lblPaint.setHorizontalAlignment(SwingConstants.CENTER);
			panel_1.add(lblPaint);

			JLabel lblOverlayColour = new JLabel("Overlay:");
			lblOverlayColour.setHorizontalAlignment(SwingConstants.CENTER);
			lblOverlayColour.setFont(new Font("Calibri", Font.PLAIN, 11));
			lblOverlayColour.setBounds(58, 81, 101, 22);
			panel_1.add(lblOverlayColour);

			OverlayCombo = new JComboBox();
			OverlayCombo.setModel(new DefaultComboBoxModel(new String[] { "On",
					"Off" }));
			OverlayCombo.setSelectedIndex(1);
			OverlayCombo.setFont(new Font("Calibri", Font.PLAIN, 11));
			OverlayCombo.setBounds(169, 81, 55, 22);
			panel_1.add(OverlayCombo);

			JLabel label = new JLabel("Overlay Colour:");
			label.setHorizontalAlignment(SwingConstants.CENTER);
			label.setFont(new Font("Calibri", Font.PLAIN, 11));
			label.setBounds(58, 104, 101, 22);
			panel_1.add(label);

			OverlayColourCombo = new JComboBox();
			OverlayColourCombo.setModel(new DefaultComboBoxModel(new String[] {
					"Grey", "Blue", "Red", "Green", "Pink" }));
			OverlayColourCombo.setFont(new Font("Calibri", Font.PLAIN, 11));
			OverlayColourCombo.setBounds(169, 104, 55, 22);
			panel_1.add(OverlayColourCombo);

			JLabel lblUsingTheOverlay = new JLabel(
					"The overlay decreases the FPS of Runescape!");
			lblUsingTheOverlay.setForeground(Color.RED);
			lblUsingTheOverlay.setHorizontalAlignment(SwingConstants.CENTER);
			lblUsingTheOverlay.setFont(new Font("Calibri", Font.PLAIN, 11));
			lblUsingTheOverlay.setBounds(10, 48, 297, 22);
			panel_1.add(lblUsingTheOverlay);

			JPanel panel_5 = new JPanel();
			tabbedPane.addTab("Logout Options", null, panel_5, null);
			panel_5.setLayout(null);

			JLabel lblLogOut = new JLabel("Logout after Time:");
			lblLogOut.setHorizontalAlignment(SwingConstants.CENTER);
			lblLogOut.setFont(new Font("Calibri", Font.PLAIN, 11));
			lblLogOut.setBounds(46, 86, 85, 14);
			panel_5.add(lblLogOut);

			MinutesTextField = new JTextField();
			MinutesTextField.setText("0");
			MinutesTextField.setFont(new Font("Calibri", Font.PLAIN, 12));
			MinutesTextField.setBounds(211, 82, 30, 22);
			panel_5.add(MinutesTextField);

			LevelTextField = new JTextField();
			LevelTextField.setText("0");
			LevelTextField.setFont(new Font("Calibri", Font.PLAIN, 12));
			LevelTextField.setBounds(155, 107, 43, 22);
			panel_5.add(LevelTextField);

			JLabel lblEssenceDoneTill = new JLabel("Logout on level:");
			lblEssenceDoneTill.setHorizontalAlignment(SwingConstants.CENTER);
			lblEssenceDoneTill.setFont(new Font("Calibri", Font.PLAIN, 11));
			lblEssenceDoneTill.setBounds(56, 111, 74, 14);
			panel_5.add(lblEssenceDoneTill);

			EssenceTextField = new JTextField();
			EssenceTextField.setText("0");
			EssenceTextField.setFont(new Font("Calibri", Font.PLAIN, 12));
			EssenceTextField.setBounds(155, 132, 86, 22);
			panel_5.add(EssenceTextField);

			JLabel label_5 = new JLabel("Essence done till Logout:");
			label_5.setHorizontalAlignment(SwingConstants.CENTER);
			label_5.setFont(new Font("Calibri", Font.PLAIN, 11));
			label_5.setBounds(10, 136, 116, 14);
			panel_5.add(label_5);

			JLabel lblSetOnOre = new JLabel(
					"conditions to make the script stop at a condition.");
			lblSetOnOre.setHorizontalAlignment(SwingConstants.CENTER);
			lblSetOnOre.setFont(new Font("Calibri", Font.PLAIN, 11));
			lblSetOnOre.setBounds(46, 26, 230, 14);
			panel_5.add(lblSetOnOre);

			JLabel lblIfYouWant = new JLabel(
					"If you want to stop at a certain point, then set one or more");
			lblIfYouWant.setHorizontalAlignment(SwingConstants.CENTER);
			lblIfYouWant.setFont(new Font("Calibri", Font.PLAIN, 11));
			lblIfYouWant.setBounds(23, 11, 272, 14);
			panel_5.add(lblIfYouWant);

			JLabel lblItWillLogout = new JLabel(
					"It will logout on the first condition that takes place.");
			lblItWillLogout.setHorizontalAlignment(SwingConstants.CENTER);
			lblItWillLogout.setFont(new Font("Calibri", Font.PLAIN, 11));
			lblItWillLogout.setBounds(37, 41, 239, 14);
			panel_5.add(lblItWillLogout);

			JLabel lblTurnOffPc = new JLabel("Turn off pc:");
			lblTurnOffPc.setHorizontalAlignment(SwingConstants.CENTER);
			lblTurnOffPc.setFont(new Font("Calibri", Font.PLAIN, 11));
			lblTurnOffPc.setBounds(56, 61, 89, 14);
			panel_5.add(lblTurnOffPc);

			logoutCombo = new JComboBox();
			logoutCombo.setModel(new DefaultComboBoxModel(new String[] { "On",
					"Off" }));
			logoutCombo.setSelectedIndex(1);
			logoutCombo.setFont(new Font("Calibri", Font.PLAIN, 11));
			logoutCombo.setBounds(155, 57, 55, 22);
			panel_5.add(logoutCombo);
			logoutCombo.setSelectedItem("Off");

			HourTextField = new JTextField();
			HourTextField.setText("0");
			HourTextField.setFont(new Font("Calibri", Font.PLAIN, 12));
			HourTextField.setBounds(155, 82, 30, 22);
			panel_5.add(HourTextField);

			JLabel lblH = new JLabel("H:");
			lblH.setFont(new Font("Calibri", Font.PLAIN, 11));
			lblH.setBounds(139, 86, 10, 14);
			panel_5.add(lblH);

			JLabel lblM = new JLabel("M:");
			lblM.setFont(new Font("Calibri", Font.PLAIN, 11));
			lblM.setBounds(195, 86, 12, 14);
			panel_5.add(lblM);

			JPanel panel_4 = new JPanel();
			tabbedPane.addTab("Change Log", null, panel_4, null);
			panel_4.setLayout(null);

			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setBounds(0, 0, 316, 175);
			panel_4.add(scrollPane);

			changeLogTextArea = new JTextArea();
			scrollPane.setViewportView(changeLogTextArea);
			changeLogTextArea.setText("Loading...");
			changeLogTextArea.setEditable(false);
			changeLogTextArea.setRows(8);
			changeLogTextArea.setColumns(8);
			changeLogTextArea.setCursor(Cursor
					.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			changeLogTextArea.setWrapStyleWord(true);
			changeLogTextArea.setLineWrap(true);
			changeLogTextArea.setFont(new Font("Calibri", Font.PLAIN, 11));

			Panel panel_2 = new Panel();
			tabbedPane.addTab("Info", null, panel_2, null);
			panel_2.setLayout(null);

			JLabel lblRunespeed = new JLabel("RuneSpeed");
			lblRunespeed.setHorizontalAlignment(SwingConstants.CENTER);
			lblRunespeed.setFont(new Font("Calibri", Font.BOLD, 15));
			lblRunespeed.setBounds(10, 11, 297, 37);
			panel_2.add(lblRunespeed);

			JLabel lblMadeBySpeedwing = new JLabel("Made by: SpeedWing");
			lblMadeBySpeedwing.setFont(new Font("Calibri", Font.PLAIN, 11));
			lblMadeBySpeedwing.setHorizontalAlignment(SwingConstants.CENTER);
			lblMadeBySpeedwing.setBounds(10, 48, 297, 23);
			panel_2.add(lblMadeBySpeedwing);

			JButton btnRunespeedRsbotTopic = new JButton(
					"Go to RuneSpeed RSBot Topic");
			btnRunespeedRsbotTopic.setFont(new Font("Calibri", Font.PLAIN, 12));
			btnRunespeedRsbotTopic.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					OpenUrl("http://www.rsbot.org/vb/showthread.php?p=1766279");
				}
			});
			btnRunespeedRsbotTopic.setBounds(10, 78, 297, 30);
			panel_2.add(btnRunespeedRsbotTopic);

			JButton btnDownloadNewestRunespeed = new JButton(
					"Download Newest RuneSpeed");
			btnDownloadNewestRunespeed.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					OpenUrl("http://speedwing.ucoz.com/RuneSpeed/RuneSpeed.java");
				}
			});
			btnDownloadNewestRunespeed.setFont(new Font("Calibri", Font.PLAIN,
					12));
			btnDownloadNewestRunespeed.setBounds(10, 109, 297, 30);
			panel_2.add(btnDownloadNewestRunespeed);

			JButton btnStart = new JButton("Start");
			btnStart.setBounds(340, 196, 89, 23);
			btnStart.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					RuneSpeed.SelectedCamera = (String) CameraCombo
							.getSelectedItem();
					RuneSpeed.SelectedTiara = (String) TiaraCombo
							.getSelectedItem();
					RuneSpeed.SelectedTab = (String) TabCombo.getSelectedItem();
					RuneSpeed.SelectedRune = (String) RuneCombo
							.getSelectedItem();
					RuneSpeed.SelectedRest = (String) RestCombo
							.getSelectedItem();
					RuneSpeed.SelectedOverlay = (String) OverlayCombo
							.getSelectedItem();
					RuneSpeed.SelectedOverlayColour = (String) OverlayColourCombo
							.getSelectedItem();
					RuneSpeed.SelectedRuinsCamera = Double
							.valueOf((String) RuinsCameraCombo
									.getSelectedItem());
					RuneSpeed.SelectedBankCamera = Double
							.valueOf((String) BankCameraCombo.getSelectedItem());
					RuneSpeed.SelectedAltarCamera = Double
							.valueOf((String) AltarCameraCombo
									.getSelectedItem());
					RuneSpeed.LevelLogout = Long.valueOf(LevelTextField
							.getText());
					RuneSpeed.EssenceLogout = Long.valueOf(EssenceTextField
							.getText());
					RuneSpeed.MinutesLogout = (Long.valueOf(HourTextField
							.getText()) * 60)
							+ Long.valueOf(MinutesTextField.getText());
					RuneSpeed.SelectedLogout = String.valueOf(logoutCombo
							.getSelectedItem());
					RSpeedIni.setProperty("Rune", String.valueOf(RuneCombo
							.getSelectedIndex()));
					RSpeedIni.setProperty("Rest", String.valueOf(RestCombo
							.getSelectedIndex()));
					RSpeedIni.setProperty("Tiara", String.valueOf(TiaraCombo
							.getSelectedIndex()));
					RSpeedIni.setProperty("Tab", String.valueOf(TabCombo
							.getSelectedIndex()));
					try {
						RSpeedIni.store(new FileWriter(new File(
								GlobalConfiguration.Paths
										.getSettingsDirectory(),
								"RuneSpeed.ini")),
								"The GUI Settings for RuneSpeed");
						if (ini) {
							log("Generated settings file!");
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					Done = true;
				}
			});
			btnStart.setFont(new Font("Calibri", Font.PLAIN, 12));
			getContentPane().add(btnStart);

			JLabel lblLoadingVersion = new JLabel("Your Version: "
					+ CurrentVersion + "    Newest Version: " + latestVersion);
			lblLoadingVersion.setBounds(10, 202, 222, 19);
			lblLoadingVersion.setFont(new Font("Calibri", Font.PLAIN, 11));
			getContentPane().add(lblLoadingVersion);

			if (CurrentVersion < latestVersion) {
				JOptionPane.showMessageDialog(null,
						"You do not have the latest version of RuneSpeed!");
				tabbedPane.setSelectedIndex(5);
			}
			TabCombo.setSelectedIndex(1);
			loadSettings();
			loadChangeLog();
		}
	}

	public class AntiBan implements Runnable {
		private boolean stopThread;
		Random random = new Random();

		public void run() {
			while (!stopThread) {
				if (TurnTheCamera) {
					final int chance = (int) GlobalChance * 10;
					if (chance <= random(1, 1001)) {
						mouseSpeed = random(5, 7);
						turnToTile(GlobalTile, random(3, 12));
					} else if (chance == 1000) {
						turnToTile(GlobalTile, random(3, 12));
					}
					TurnTheCamera = false;
					CameraTurned = true;
				}
				if (WaitForEnergy > 0
						&& (getMyPlayer().getAnimation() == 12108
								|| getMyPlayer().getAnimation() == 2033
								|| getMyPlayer().getAnimation() == 2716
								|| getMyPlayer().getAnimation() == 11786 || getMyPlayer()
								.getAnimation() == 5713)) {
					if (RestCamera) {
						if (random(0, 3) < 2) {
							final char[] LR = new char[] { KeyEvent.VK_LEFT,
									KeyEvent.VK_RIGHT };
							final int rand = random(0, 2);
							Bot.getInputManager().pressKey(LR[rand]);
							try {
								Thread.sleep(random.nextInt(Math
										.abs(1950 - 550)));
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							Bot.getInputManager().releaseKey(LR[rand]);
						}
						moveMouse(tileToMinimap(getMyPlayer().getLocation()).x
								+ random(-50, 50), tileToMinimap(getMyPlayer()
								.getLocation()).y
								+ random(-50, 50));

						RestCamera = false;
					}
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void AntiBanCamera(final RSTile tile, final double ChanceTo) {
		final int chance = (int) ChanceTo * 10;
		if (chance <= random(1, 1001)) {
			turnToTile(tile, random(3, 12));
		}
	}

	public int arrayBankItemByID(final int array[]) {
		try {
			for (int anArray : array) {
				if (bank.getCount(anArray) > 0) {
					return anArray;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public int arrayInvCount(final int array[]) {
		try {
			for (int anArray : array) {
				if (invCount(anArray) > 0) {
					return invCount(anArray);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public int arrayInvItemByID(final int array[]) {
		try {
			for (int anArray : array) {
				if (invCount(anArray) > 0) {
					return anArray;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public boolean bankSave() {
		RSTile Loc = getMyPlayer().getLocation();
		if (Rune == earthRune) {
			if (Loc.equals(new RSTile(3250, 3419))
					|| Loc.equals(new RSTile(3257, 3419))) {
				if (walkTo(new RSTile(3253 + random(-1, 2),
						3421 + random(-1, 2)))) {
					waitToMove(1000);
				}
			}
		}
		return false;
	}

	public void cameraHeight() {
		if (antibanstate && Bot.getClient().getCamPosZ() > -1600) {
			setCameraAltitude(true);
		}
	}

	public void changePath() {
		if (RealPath == magicTelePath) {
			RealPath = path[random(0, 3)];
		}
		final RSTile[] curPath = RealPath;
		int iPath = random(0, 3);
		RealPath = path[iPath];
		boolean done = false;
		if (pathar[0] + pathar[1] > 3 || pathar[0] + pathar[2] > 3
				|| pathar[1] + pathar[2] > 3) {
			if (SamePath > 3) {
				if (curPath == path[0]) {
					iPath = random(1, 3);
					RealPath = path[iPath];
				}
				if (curPath == path[1]) {
					iPath = 1;
					while (iPath == 1) {
						iPath = random(0, 3);
					}
					RealPath = path[iPath];
				}
				if (curPath == path[2]) {
					iPath = random(0, 2);
					RealPath = path[iPath];
				}
				SamePath = 0;
				done = true;
			}
			if (!done) {
				for (int i = 0; i < pathar.length; i++) {
					pathar[i] = 0;
				}
				if (pathar[0] + pathar[1] > 4) {
					iPath = 2;
				}
				if (pathar[0] + pathar[2] > 4) {
					iPath = 1;
				}
				if (pathar[1] + pathar[2] > 4) {
					iPath = 0;
				}
				RealPath = path[iPath];
			}
		}
		if (curPath == RealPath) {
			SamePath++;
		}
		pathar[iPath]++;
	}

	public boolean climbStile() {
		if (mapPath[0] == null) {
			mapPath[0] = getMyPlayer().getLocation();
		}
		if (!isInZone(getMyPlayer().getLocation(), 3044, 3068, 3283, 3298)) {
			stileCrossed = true;
			return true;
		}
		if (getNearestObjectByID(stileID) != null) {
			if (tileOnScreen(getNearestObjectByID(stileID).getLocation())
					&& !getMyPlayer().isMoving() && getDestination() == null) {
				wait(300);
				mouseSpeed = random(3, 5);
				clickMouse(Calculations.tileToScreen(getNearestObjectByID(
						stileID).getLocation(), 0.5, 0.05, 0), true);
				if (waitForZone(5000, 3061, 3065, 3275, 3282)) {
					stileCrossed = true;
					return true;
				}
			} else {
				if ((distanceTo(getDestination()) < 2 || !getMyPlayer()
						.isMoving())
						&& !tileOnScreen(getNearestObjectByID(stileID)
								.getLocation())) {
					if (getDestination() != null) {
						if ((distanceBetween(getDestination(),
								getNearestObjectByID(stileID).getLocation()) < 4)) {
							return false;
						}
					}
					walkTo(
							new RSTile(getNearestObjectByID(stileID)
									.getLocation().getX(),
									getNearestObjectByID(stileID).getLocation()
											.getY() + 1), 0, 0);
					return false;
				}
			}
		}

		return false;
	}

	public boolean craftRunes() {
		try {
			final RSObject InsideAltar = getNearestObjectByID(CurAltar);
			if (InsideAltar == null) {
				return false;
			}
			final RSTile tile = getNearestObjectByID(CurAltar).getLocation();
			if (tile == null) {
				return false;
			}
			final Point location = Calculations.tileToScreen(tile);
			if (location == null) {
				return false;
			}
			// anti-ban
			if (!pointOnScreen(location) && antibanstate && !CameraTurned) {
				GlobalTile = tile;
				GlobalChance = SelectedAltarCamera;
				TurnTheCamera = true;
				wait(300, 500);
				return false;
			}
			CameraTurned = true;
			if (pointOnScreen(location)) {
				mouseSpeed = random(4, 6);
				if (atObject(InsideAltar, "Craft-Rune")) {
					waitForCrafting(3000);
					altarwalk = false;
					return false;
				}
			}
			if (!pointOnScreen(location) && distanceTo(InsideAltar) > 3
					&& getMyPlayer().getAnimation() == -1) {
				if (!getMyPlayer().isMoving()
						|| distanceTo(getDestination()) < 7) {
					if (getDestination() != null) {
						if (distanceBetween(tile, getDestination()) < 3) {
							return false;
						}
					}
					walkTo(tile);
				}
			}
		} catch (Exception e) {
			/* e.printStackTrace(); */
		}
		return true;
	}

	public int drawLogoutPaint(final boolean Time, final boolean Essence,
			final boolean Level, final long startT, final long minutes,
			final int x, int y, final int xl, final Graphics g) {
		int amount = 0;
		boolean[] opts = { Time, Essence, Level };
		for (int i = 0; i < opts.length; i++) {
			if (opts[i] == true) {
				amount++;
			}
		}
		if (amount == 0) {
			return y;
		}
		y += 11;
		String m = "minutes";
		String l = " levels";
		final long curmillis = System.currentTimeMillis() - startTime;
		final long curminutes = curmillis / (1000 * 60);
		if ((MinutesLogout - curminutes) == 1) {
			m = "minute";
		}
		if ((LevelLogout - skills.getCurrentSkillLevel(STAT_RUNECRAFTING)) == 1) {
			l = " level";
		}
		g.setColor(new Color(0, 0, 0, 147));
		g.fillRoundRect(x, y, xl, 7 + (amount * 15), 2, 2);
		g.setColor(new Color(255, 255, 255));
		g.drawRoundRect(x, y, xl, 7 + (amount * 15), 2, 2);
		if (Time) {
			drawStringWithShadow(MinutesLogout - curminutes + " " + m
					+ " until Logout", x + 10, y += 15, g);
		}
		if (Essence) {
			drawStringWithShadow(EssenceLogout - essused
					+ " Essence until Logout", x + 10, y += 15, g);
		}
		if (Level) {
			drawStringWithShadow(LevelLogout
					- skills.getCurrentSkillLevel(STAT_RUNECRAFTING) + l
					+ " until Logout", x + 10, y += 15, g);
		}
		return y;
	}

	public void drawStringWithShadow(final String text, final int x,
			final int y, final Graphics g) {
		final Color col = g.getColor();
		g.setColor(new Color(0, 0, 0));
		g.drawString(text, x + 1, y + 1);
		g.setColor(col);
		g.drawString(text, x, y);
	}// credits to Epic_

	public void drawWalkedPath(final Graphics g) {
		if (bankwalk || altarwalk) {
			for (int i = 0; i < (mapPath.length - 1); i++) {
				if (distanceTo(mapPath[i]) > 2 && mapPath[i + 1] == null) {
					mapPath[i + 1] = getMyPlayer().getLocation();
				}
			}
			// painting the lines between the tiles
			for (int paintTile = 0; mapPath[paintTile] != null
					&& paintTile < mapPath.length - 1; paintTile++) {
				if (mapPath[paintTile] != null
						&& mapPath[paintTile + 1] != null) {
					if (tileOnMap(mapPath[paintTile])
							&& tileOnMap(mapPath[paintTile + 1])) {

						g.setColor(new Color(18, 255, 0,
								255 - distanceTo(mapPath[paintTile + 1]) * 7));
						g.drawLine(tileToMinimap(mapPath[paintTile]).x + 1,
								tileToMinimap(mapPath[paintTile]).y + 1,
								tileToMinimap(mapPath[paintTile + 1]).x + 1,
								tileToMinimap(mapPath[paintTile + 1]).y + 1);
					}
				}
			}
			// painting the tiles
			for (int paintTile = 0; mapPath[paintTile] != null
					&& paintTile < mapPath.length - 1; paintTile++) {
				if (mapPath[paintTile] != null) {
					if (tileOnMap(mapPath[paintTile])) {
						g.setColor(new Color(0, 0, 0,
								255 - distanceTo(mapPath[paintTile]) * 11));
						g.fillRect(tileToMinimap(mapPath[paintTile]).x,
								tileToMinimap(mapPath[paintTile]).y, 3, 3);
					}
				}
			}
		}
	}

	public boolean duelTeleport() {
		int iChild = 2, xo1 = 2435, xo2 = 2451, yo1 = 3050, yo2 = 3100;
		if (altarwalk) {
			iChild = 1;
			xo1 = 3299;
			xo2 = 3325;
			yo1 = 3226;
			yo2 = 3248;
			RealPath = FireDuelPath;
		}
		if (isInZone(getMyPlayer().getLocation(), xo1, xo2, yo1, yo2)) {
			teleports++;
			exitaltar = false;
			portalturn = true;
			if (essused > 0) {
				crafted = (crafted + invCount(Rune) - runesInInv);
			}
			if (runeID == 0) {
				runeID = Rune;
			}
			if (runCheck()) {
				run = true;
			}
			if (!altarwalk) {
				bankwalk = true;
				exitaltar = false;
			} else {
				altarwalk = true;
			}
			walking = true;
			CameraTurned = false;
			duelArrived = true;
		}
		if (arrayInvCount(duelleaveArray) != 0) {
			final int curDuel = arrayInvItemByID(duelleaveArray);
			if (!RSInterface.getInterface(238).isValid()) {
				atInventoryItem(curDuel, "Rub");
				waitForIface(RSInterface.getInterface(238), random(2000, 2400));
			}
			if (RSInterface.getInterface(238).isValid()) {
				if (atInterface(238, iChild)) {
					if (waitForZone(6000 + random(-1500, 200), xo1, xo2, yo1,
							yo2)) {
						teleports++;
						exitaltar = false;
						portalturn = true;
						if (essused > 0) {
							crafted = (crafted + invCount(Rune) - runesInInv);
						}
						if (runeID == 0) {
							runeID = Rune;
						}
						if (runCheck()) {
							run = true;
						}
						if (!altarwalk) {
							banking = true;
							exitaltar = false;
							status = "Walking to Bank";
						}
						walking = true;
						CameraTurned = false;
						duelArrived = true;
					}
				}
			}
		} else {
			log.warning("Stopping Script, no Duel Ring in Inventory");
			stopScript();

		}

		return false;
	}

	public String[] getFormattedTime(final long timeMillis) {
		long millis = timeMillis;
		final long days = millis / (24 * 1000 * 60 * 60);
		millis -= days * (24 * 1000 * 60 * 60);
		final long hours = millis / (1000 * 60 * 60);
		millis -= hours * 1000 * 60 * 60;
		final long minutes = millis / (1000 * 60);
		millis -= minutes * 1000 * 60;
		final long seconds = millis / 1000;
		String dayString = String.valueOf(days);
		String hoursString = String.valueOf(hours);
		String minutesString = String.valueOf(minutes);
		String secondsString = String.valueOf(seconds);
		if (hours < 10) {
			hoursString = 0 + hoursString;
		}
		if (minutes < 10) {
			minutesString = 0 + minutesString;
		}
		if (seconds < 10) {
			secondsString = 0 + secondsString;
		}
		return new String[] { dayString, hoursString, minutesString,
				secondsString };
	}

	public void getLatestVersion() {
		URLConnection url;
		BufferedReader in;
		try {
			url = new URL("http://speedwing.ucoz.com/RuneSpeed/RuneSpeedV.txt")
					.openConnection();
			in = new BufferedReader(new InputStreamReader(url.getInputStream()));
			latestVersion = Double.parseDouble(in.readLine());
		} catch (final Exception e) {
			log("Error loading version data.");
		}
	}// Credits to Epic

	@Override
	protected int getMouseSpeed() {
		return mouseSpeed;
	}

	/**
	 * * @return 1,1 on teleported, return 1,0 on glory but no teleport, return
	 * 0,0 on no glory
	 */
	public int[] gloryTeleport() {
		int x1o = 0, x2o = 0, y1o = 0, y2o = 0, iChild = 0;
		if (Rune == waterRune) {
			x1o = 3096;
			x2o = 3108;
			y1o = 3243;
			y2o = 3254;
			iChild = 3;
		}
		if (Rune == bodyRune) {
			x1o = 3082;
			x2o = 3099;
			y1o = 3485;
			y2o = 3505;
			iChild = 1;
		}
		if (Rune == fireRune) {
			x1o = 3280;
			x2o = 3306;
			y1o = 3156;
			y2o = 3180;
			iChild = 4;
			RealPath = FireTelePath;
		}
		if (isInZone(getMyPlayer().getLocation(), x1o, x2o, y1o, y2o)) {
			teleports++;
			status = "Walking to Bank";
			exitaltar = false;
			portalturn = true;
			if (essused > 0) {
				crafted = (crafted + invCount(Rune) - runesInInv);
			}
			if (runeID == 0) {
				runeID = Rune;
			}
			// in case you leave the altar with a miss click
			if (arrayInvCount(essence) > 0) {
				altarwalk = true;
				walking = true;
			}
			if (runCheck()) {
				run = true;
			}
			bankwalk = true;
			walking = true;
			CameraTurned = false;
			return new int[] { 1, 1 };
		}
		if (arrayInvCount(gloryArray) != 0) {
			final int curGlory = arrayInvItemByID(gloryArray);
			if (!RSInterface.getInterface(238).isValid()) {
				atInventoryItem(curGlory, "Rub");
				waitForIface(RSInterface.getInterface(238), 3500);
			}
			if (RSInterface.getInterface(238).isValid()) {// glory
				// interface

				if (atInterface(238, iChild)) {
					if (waitForZone(7000, x1o, x2o, y1o, y2o)) {
						teleports++;
						status = "Walking to Bank";
						exitaltar = false;
						portalturn = true;
						if (essused > 0) {
							crafted = (crafted + invCount(Rune) - runesInInv);
						}
						if (runeID == 0) {
							runeID = Rune;
						}
						// in case you leave the altar with a miss click
						if (arrayInvCount(essence) > 0) {
							altarwalk = true;
							walking = true;
						}
						if (runCheck()) {
							run = true;
						}
						bankwalk = true;
						walking = true;
						CameraTurned = false;
						return new int[] { 1, 1 };
					}
				}

			}
		}
		log("Walking Back, no Glory in Inventory");
		return new int[] { 0, 0 };
	}

	public boolean intoAltar() {
		try {
			essInInv = arrayInvCount(essence);
			final RSObject TheAltar = getNearestObjectByID(CurOutside);

			if (TheAltar == null || !altarwalk) {
				return false;
			}
			final RSTile tile = getNearestObjectByID(CurOutside).getLocation();

			final Point location = Calculations.tileToScreen(tile);

			if (!pointOnScreen(location) && distanceTo(TheAltar) > 6) {
				return false;
			}
			// anti-ban
			if (distanceTo(TheAltar) <= 4 && !CameraTurned && antibanstate) {
				GlobalChance = SelectedRuinsCamera;
				GlobalTile = tile;
				TurnTheCamera = true;
			}
			CameraTurned = true;
			if (pointOnScreen(location) && location != null) {
				atObject(TheAltar, "Enter Mysterious ruins");
				if (waitForAltar(random(1200, 1800))) {
					return true;
				}
			}
			if (!getMyPlayer().isMoving() && !pointOnScreen(location)) {
				walkTileMM(tile);
				return false;
			}
		} catch (Exception e) {
			/* e.printStackTrace(); */
		}
		return true;
	}

	public int invCount(final int InvItem) {
		return getInventoryCount(InvItem);
	}

	// returns true if the player is in the zone
	public boolean isInZone(final RSTile t, final int x1, final int x2,
			final int y1, final int y2) {
		return t.getX() >= x1 && t.getX() <= x2 && t.getY() >= y1
				&& t.getY() <= y2;
	}

	public boolean leaveAltar() {
		try {
			if (!tele) {
				if (varrockTeleport || fallyTeleport) {
					final int[] teleported = magicTeleport();
					if (teleported == new int[] { 1, 1 }) {
						return true;
					}
					if (teleported == new int[] { 1, 0 }) {
						return false;
					}
				}
				if (gloryTeleport) {
					final int[] glored = gloryTeleport();
					if (glored == new int[] { 1, 1 }) {
						return true;
					}
					if (glored == new int[] { 1, 0 }) {
						return false;
					}
				}
			}
			if (ringTeleport) {
				ringTeleport();
				return true;
			}
			if (duelTeleport) {
				duelTeleport();
				return true;
			}
			tele = true;
			if (tele) {
				final RSObject ExitPortalObj = getNearestObjectByID(ExitPortal);
				if (ExitPortalObj == null) {
					return false;
				}
				final RSTile tile = getNearestObjectByID(ExitPortal)
						.getLocation();
				if (tile == null) {
					return false;
				}
				final Point location = Calculations.tileToScreen(tile);
				if (location == null) {
					return false;
				}
				// anti-ban
				if (!pointOnScreen(location) && !CameraTurned && antibanstate) {
					GlobalTile = tile;
					GlobalChance = SelectedAltarCamera;
					TurnTheCamera = true;
				}
				if (pointOnScreen(location)) {
					if (atObject(ExitPortalObj, "Enter Portal")) {
						waitForBankwalk(3000);
					}
				}
				if (!pointOnScreen(location) && distanceTo(ExitPortalObj) > 3
						&& getMyPlayer().getAnimation() == -1) {
					if (!CameraTurned) {
						GlobalTile = tile;
						GlobalChance = 95;
						TurnTheCamera = true;
					}
					if (!getMyPlayer().isMoving()
							|| distanceTo(getDestination()) < 7
							&& !getMyPlayer().isMoving()) {
						walkTo(tile);
					}
				}
			}
		} catch (Exception e) {
			/* e.printStackTrace(); */
		}
		return true;
	}

	public void loadGEInfo(final int essID, final int runeID) {
		try {
			new Thread(new Runnable() {
				public void run() {
					essprice = grandExchange.loadItemInfo(essID)
							.getMarketPrice();
					runeprice = grandExchange.loadItemInfo(runeID)
							.getMarketPrice();
				}
			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadTeleGEInfo() {
		try {
			new Thread(new Runnable() {
				public void run() {
					telePrice = grandExchange.loadItemInfo(thirdTeleRune)
							.getMarketPrice()
							+ grandExchange.loadItemInfo(563).getMarketPrice();
					if (Rune == airRune) {
						telePrice += 3 * grandExchange.loadItemInfo(runeID)
								.getMarketPrice();
					}
				}
			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int loop() {
		if (isLoggedIn() && (varrockTeleport || fallyTeleport) && !gotTeleStats) {
			if (skills.getCurrentSkillLevel(Constants.STAT_MAGIC) < 25
					&& varrockTeleport) {
				log
						.warning("Your Magic level is too low to use Varrock Teleport.");
				log.warning("RuneSpeed is going to walk instead of teleport.");
				varrockTeleport = false;
			}
			if (skills.getCurrentSkillLevel(Constants.STAT_MAGIC) < 37
					&& fallyTeleport) {
				log
						.warning("Your Magic level is too low to use Falador Teleport.");
				log.warning("RuneSpeed is going to walk instead of teleport.");
				fallyTeleport = false;
			}
			gotTeleStats = true;
		}
		if (!t.isAlive()) {
			t.start();
		}
		mouseSpeed = random(4, 8);
		if (MinutesLogout != 0 || LevelLogout != 0 || EssenceLogout != 0) {
			final long curmillis = System.currentTimeMillis() - startTime;
			final long curminutes = curmillis / (1000 * 60);
			if (curminutes >= MinutesLogout && MinutesLogout != 0 && getessence) {
				if (MinutesLogout == 1) {
					log("Stopped the script after " + MinutesLogout
							+ " minute.");
				} else {
					log("Stopped the script after " + MinutesLogout
							+ " minutes.");
				}
				ScreenshotUtil.takeScreenshot(true);
				stopScript();
			}
			if (essused >= EssenceLogout && EssenceLogout != 0 && getessence) {
				log("Stopped the script after using " + essused + " essence.");
				ScreenshotUtil.takeScreenshot(true);
				stopScript();
			}
			if (skills.getCurrentSkillLevel(STAT_RUNECRAFTING) >= LevelLogout
					&& LevelLogout != 0 && getessence) {
				log("Stopped the script after reaching " + LevelLogout
						+ " runecrafting.");
				ScreenshotUtil.takeScreenshot(true);
				stopScript();
			}
		}
		try {
			cameraHeight();
			if (tiaracheck) {
				if (getCurrentTab() != Constants.TAB_EQUIPMENT && !tiarabank) {
					if (equipmentContainsOneOf(Tiara)) {
						log("Succesfully found " + TiaraName
								+ " Tiara in equipment.");
						status = "Checking Activity..";
						tiaracheck = false;
						tiarabank = true;
						isTiaraOn = true;
						banking = true;
						return 100;
					}
				}
				if (!tiarabank && !isTiaraOn) {
					if (invCount(Tiara) != 0) {
						atInventoryItem(Tiara, "Wear");
						tiaracheck = false;
						banking = true;
						return 100;
					}
					if (invCount(Tiara) == 0) {
						tiarabank = true;
						log("Searching in the bank for a " + TiaraName
								+ " Tiara.");
					}
				}

				if (tiarabank) {
					if (!bank.isOpen()) {
						openBank(CurBank);
						return random(1700, 1800);
					}
					if (!bank.searchItem(TiaraName + " tiara")) {
						log
								.warning("You don't have a " + TiaraName
										+ " Tiara.");
						stopScript();
						return random(500, 600);
					}
					if (getInventoryCount() == 28) {
						bank.depositAll();
					}
					if (bank.atItem(Tiara, "Withdraw-1")) {
						log("Succesfully found " + TiaraName
								+ " Tiara in the Bank.");
						bank.close();
						wait(500);
						atInventoryItem(Tiara, "Wear");
						tiaracheck = true;
						tiarabank = false;
						return 500;
					}
				}
				return 250;
			}

			if (banking) {
				run = false;
				duelArrived = false;
				if (!gotActivity) {
					if (arrayInvCount(essence) > 0) {
						final RSObject InsideAltar = getNearestObjectByID(CurAltar);
						if (InsideAltar != null) {
							banking = false;
							crafting = true;
							essInInv = arrayInvCount(essence);
							gotActivity = true;
							return random(50, 100);
						}
						banking = false;
						altarwalk = true;
						walking = true;
						gotActivity = true;
						return random(50, 100);
					}
					if (arrayInvCount(essence) == 0) {
						final RSObject InsideAltar = getNearestObjectByID(CurAltar);
						if (InsideAltar != null) {
							banking = false;
							exitaltar = true;
							gotActivity = true;
							return random(50, 100);
						}
						if (duelTeleport) {
							banking = true;
							gotActivity = true;
							status = "Walking to Bank";
							return random(10, 90);
						} else {
							banking = false;
							bankwalk = true;
							walking = true;
							gotActivity = true;
							status = "Walking to Bank";
						}
						return random(50, 100);
					}
				}
				if (bank.isOpen()) {
					banking = false;
					inbank = true;
					return random(100, 150);
				}
				if (openBank(CurBank)) {
					banking = false;
					inbank = true;
					return random(500, 600);
				}
			}

			if (inbank) {
				if (!bank.isOpen() && getMyPlayer().getAnimation() == -1) {
					inbank = false;
					banking = true;
					return random(50, 65);
				}
				if (bank.isOpen()) {
					if (mapPath[0] != null) {
						nullMapPath();
					}
					if (!status.equals("Banking..")) {
						status = "Banking..";
					}
					if (getInventoryCount() > 0) {
						bank.depositAll();
					}
					inbank = false;
					if (varrockTeleport || fallyTeleport) {
						getTele = true;
					} else if (gloryTeleport) {
						getGlory = true;
						waitForEmptyInv(2000);
						wait(300);
					} else if (duelTeleport) {
						getDuel = true;
						waitForEmptyInv(2000);
						wait(300);
					} else {
						getessence = true;
					}
					return random(100, 150);
				}
			}
			if (getDuel && bank.isOpen()) {
				if (!bank.isOpen()) {
					getDuel = false;
					banking = true;
				}
				if (getInventoryCount() != 0) {
					getDuel = false;
					inbank = true;
					return 1;
				}
				for (int i = 0; i < duelArray.length; i++) {
					final RSInterfaceChild Item = bank
							.getItemByID(duelArray[i]);
					if (Item != null) {
						final int ringplace = Item.getRelativeY();
						if (ringplace > 270 || ringplace < 1) {
							log
									.warning("Place all your Dueling Rings(2)-(8) visibile in the bank");
							stopScript();
						}
					}
				}
				int lowestDuel = arrayBankItemByID(duelArray);
				if (lowestDuel == 0) {
					log.warning("No Dueling Rings found, Stopping script.");
					ScreenshotUtil.takeScreenshot(true);
					stopScript();
				} else if (invCount(duel1) == 0 && invCount(duel2) == 0
						&& invCount(duel3) == 0 && invCount(duel4) == 0
						&& invCount(duel5) == 0 && invCount(duel6) == 0
						&& invCount(duel7) == 0 && invCount(duel8) == 0) {
					bank.withdraw(lowestDuel, 1);
					getDuel = false;
					getessence = true;
				}
				return random(50, 150);
			}

			if (getGlory && bank.isOpen()) {
				if (!bank.isOpen()) {
					getGlory = false;
					banking = true;
				}
				int lowestGlory = arrayBankItemByID(gloryArray);
				if (lowestGlory == 0) {
					log
							.warning("No charged Glorys found, continueing without using Glorys.");
					gloryTeleport = false;
					getGlory = false;
					getessence = true;
					return 1;
				}
				for (int i = 0; i < gloryArray.length; i++) {
					final RSInterfaceChild Item = bank
							.getItemByID(gloryArray[i]);
					if (Item != null) {
						final int itemPlace = Item.getRelativeY();
						if (itemPlace > 270 || itemPlace < 1) {
							log
									.warning("Place all your Glorys visibile in the bank");
							stopScript();
						}
					}
				}
				if (invCount(glory1) == 0 && invCount(glory2) == 0
						&& invCount(glory3) == 0 && invCount(glory4) == 0) {
					bank.withdraw(lowestGlory, 1);
					getGlory = false;
					getessence = true;
					return random(100, 120);
				}
				return random(100, 150);
			}

			if (getTele && bank.isOpen()) {
				if (!bank.isOpen()) {
					getTele = false;
					banking = true;
				}
				changePath();
				boolean noRunes = false;
				if (Rune != airRune) {
					if ((bank.getCount(563) == 0 || bank
							.getCount(thirdTeleRune) == 0)
							&& bank.isOpen()) {
						noRunes = true;
					}
				} else if (bank.getCount(563) == 0 && bank.isOpen()) {
					noRunes = true;
				}
				if (noRunes) {
					log
							.warning("Not enough Runes for Teleporting, continueing without Teleporting.");
					varrockTeleport = false;
					getTele = false;
					getessence = true;
					return 1;
				}
				if (invCount(563) == 0 || invCount(thirdTeleRune) == 0) { // laws
					mouseSpeed = random(3, 5);
					bank.withdraw(563, 1);
					if (Rune != airRune) {
						bank.withdraw(thirdTeleRune, 1);
					}
					getTele = false;
					getessence = true;
				}
				return random(60, 150);
			}

			if (getessence) {
				if (!bank.isOpen()) {
					getessence = false;
					banking = true;
				}
				if (tabTeleport && !duelTeleport) {
					if (bank.getCount(curTab) == 0) {
						log
								.warning("Not enough tabs for Teleporting, continueing without teleport tabs.");
						tabTeleport = false;
					} else {
						final RSInterfaceChild Item = bank.getItemByID(curTab);
						if (Item != null) {
							final int itemPlace = Item.getRelativeY();
							if (itemPlace > 270 || itemPlace < 1) {
								log("Place the " + TiaraName
										+ " Tabs Visible in the bank.");
								stopScript();
							}
						}
						if (invCount(curTab) == 0) {
							bank.withdraw(curTab, 1);
						}
					}
				}
				final RSInterfaceChild Item = bank
						.getItemByID(arrayBankItemByID(essence));
				if (Item != null) {
					final int essplace = Item.getRelativeY();
					if (essplace > 270 || essplace < 1) {
						log.warning("Place the Essence visibile in the Bank");
						stopScript();
					}
				}
				if (arrayInvCount(essence) > 1) {
					changePath();
					getessence = false;
					altarwalk = true;
					walking = true;
					return random(100, 150);
				}
				if (bank.getCount(essence) == 0 && getessence && bank.isOpen()) {
					if (getInventoryCount() > 0) {
						bank.depositAll();
					}
					log.warning("Out of Essence");
					ScreenshotUtil.takeScreenshot(true);
					stopScript();
				}
				if (arrayInvCount(essence) < 1 && getessence) {
					essleft = bank.getCount(essence);
					if (bank.atItem(arrayBankItemByID(essence), "Withdraw-All")) {
						if (waitForEssGrab(1500)) {
							if (gloryTeleport) {
								if (invCount(glory1) == 0
										&& invCount(glory2) == 0
										&& invCount(glory3) == 0
										&& invCount(glory4) == 0) {
									getessence = false;
									banking = true;
									altarwalk = false;
									walking = false;
								}
							}
							if (varrockTeleport || fallyTeleport) {
								if (Rune != airRune) {
									if ((invCount(563) == 0 || invCount(thirdTeleRune) == 0)
											&& bank.isOpen()) {
										getessence = false;
										banking = true;
										altarwalk = false;
										walking = false;
									}
								} else if (invCount(563) == 0 && bank.isOpen()) {
									getessence = false;
									banking = true;
									altarwalk = false;
									walking = false;
								}
							}
							if (tabTeleport) {
								if (invCount(curTab) == 0) {
									getessence = false;
									banking = true;
									altarwalk = false;
									walking = false;
								}
							}
							if (duelTeleport) {
								if (invCount(duel2) == 0
										&& invCount(duel3) == 0
										&& invCount(duel4) == 0
										&& invCount(duel5) == 0
										&& invCount(duel6) == 0
										&& invCount(duel7) == 0
										&& invCount(duel8) == 0) {
									getessence = false;
									banking = true;
									altarwalk = false;
									walking = false;
								}
							}
							return random(30, 80);
						}
					}
				}
				return random(100, 150);
			}

			if (crafting && getMyPlayer().getAnimation() == -1) {
				duelArrived = false;
				if (mapPath[0] != null) {
					nullMapPath();
				}
				if (run) {
					run = false;
				}

				craftRunes();
				return random(250, 350);
			}

			if (altarwalk) {
				runesInInv = invCount(Rune);
				if (tabTeleport && !tabClicked) {
					if (bank.isOpen()) {
						bank.close();
					}
					if (!bank.isOpen()) {
						status = "Clicking Teleport Tab";
						tabTeleport();
						return random(50, 100);
					}
				}
				if (duelTeleport && !duelArrived) {
					if (bank.isOpen()) {
						bank.close();
					}
					if (!bank.isOpen()) {
						status = "Ring to Ruins";
						duelTeleport();
						return random(50, 100);
					}
				}
				if (!status.toLowerCase().equals("walking to ruins")) {
					status = "Walking to Ruins";
				}
				if (stileCrossed) {
					stileCrossed = false;
				}
				if (intoAltar()) {
					return random(75, 150);
				}
				if (arrayInvCount(essence) == 0) {
					bankwalk = true;
					altarwalk = false;
				}
				final RSObject TheAltar = getNearestObjectByID(CurOutside);
				if (TheAltar != null && distanceTo(TheAltar) < 3) {
					walking = false;
				}
				if (walking) {
					restingCheck(RealPath);
					walkPath(RealPath, false);
				}
				return random(200, 300);
			}

			if (bankwalk) {
				if (ringTeleport) {
					if (!stileCrossed) {
						RealPath = WaterTelePath;
						if (!climbStile()) {
							return random(100, 200);
						}
					}
				}
				if (tabClicked) {
					tabClicked = false;
				}
				if (!status.toLowerCase().equals("walking to bank")) {
					status = "Walking to Bank";
				}
				if (exitaltar) {
					exitaltar = false;
				}
				if (tele) {
					tele = false;
				}
				final RSObject bankObj = getNearestObjectByID(CurBank);

				if (bankObj != null && distanceTo(bankObj) > 5
						&& distanceTo(bankObj) < 7) {
					walking = false;
				}
				if (bankObj != null && distanceTo(bankObj) <= 6) {
					walking = false;
					bankwalk = false;
					banking = true;
				}
				if (walking) {
					restingCheck(reversePath(RealPath));
					walkPath(RealPath, true);
				}
				return random(200, 300);
			}

			if (exitaltar) {
				if (arrayInvCount(essence) == 0
						&& getMyPlayer().getAnimation() != 791) {
					if (crafting) {
						crafting = false;
					}
					if (leaveAltar()) {
						return random(50, 150);
					} else {
						return random(250, 350);
					}
				}
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}

		return random(100, 200);
	}

	// first value returns 1 on enough runes, second retuns 1 on teleported.
	public int[] magicTeleport() {
		boolean gotThirdRune = false;
		if (Rune == airRune || invCount(thirdTeleRune) > 0) {
			gotThirdRune = true;
		}
		if (invCount(563) > 0 && gotThirdRune) {
			if (getCurrentTab() != Constants.TAB_MAGIC) {
				if (clickOrPress == 0) {
					openTab(Constants.TAB_MAGIC);
				} else {
					Bot.getInputManager().pressKey((char) KeyEvent.VK_F4);
					wait(random(50, 75));
					Bot.getInputManager().releaseKey((char) KeyEvent.VK_F4);
				}
				wait(100);
			}
			if (getCurrentTab() == Constants.TAB_MAGIC) {
				int Spell, x1, x2, y1, y2;
				boolean arrived = false;
				if (varrockTeleport) {
					Spell = Constants.SPELL_VARROCK_TELEPORT;
					x1 = 3205;
					x2 = 3321;
					y1 = 3420;
					y2 = 3437;
				} else {
					Spell = Constants.SPELL_FALADOR_TELEPORT;
					x1 = 2961;
					x2 = 2969;
					y1 = 3373;
					y2 = 3390;
				}
				if (isInZone(getMyPlayer().getLocation(), x1, x2, y1, y2)) {
					arrived = true;
				}
				if (!arrived && castSpell(Spell)) {
					wait(random(25, 75));
					Bot.getInputManager().pressKey((char) KeyEvent.VK_F1);
					wait(random(50, 75));
					Bot.getInputManager().releaseKey((char) KeyEvent.VK_F1);
					if (waitForZone(7000, x1, x2, y1, y2)) {
						arrived = true;
					}
				}
				if (arrived) {
					teleports++;
					if (varrockTeleport) {
						RealPath = magicTelePath;
					}
					status = "Walking to Bank";
					exitaltar = false;
					portalturn = true;
					if (Rune == airRune) {
						crafted += 3;
					}
					if (essused > 0) {
						crafted = (crafted + invCount(Rune) - runesInInv);
					}
					if (runeID == 0) {
						runeID = Rune;
					}
					// in case you leave the altar with a miss click
					if (arrayInvCount(essence) > 0) {
						altarwalk = true;
						walking = true;
					}
					if (runCheck()) {
						run = true;
					}
					bankwalk = true;
					walking = true;
					CameraTurned = false;
					return new int[] { 1, 1 };
				} else {
					return new int[] { 1, 0 };
				}
			}
		}
		prevPath = null;
		log("Walking Back, no Tele Runes in Inventory");
		return new int[] { 0, 0 };
	}

	public void mouseMoveRandom(final double tempVal, final int maxDist) {
		int chance = (int) tempVal * 10;
		if (chance <= random(1, 1001)) {
			moveMouseRandomly(maxDist);
		} else if (chance == 1000) {
			moveMouseRandomly(maxDist);
		}
	}

	public boolean musicianWalk(final RSTile[] path) {
		if (getNearestNPCByID(MusicianID) != null && rest) {
			final RSNPC musician = getNearestNPCByID(MusicianID);
			if (distanceTo(musician.getLocation()) < 31
					&& getEnergy() < random(22, 25)) {
				final int PlayerDest = distanceBetween(getMyPlayer()
						.getLocation(), path[path.length - 1]);
				final int MusicianDest = distanceBetween(
						musician.getLocation(), path[path.length - 1]);
				if (MusicianDest - PlayerDest > 5
						&& distanceTo(musician.getLocation()) > 1
						&& getEnergy() > 10) {
					return false;
				}
				walkTo(musician.getLocation(), 0, 0);
				while (distanceTo(musician.getLocation()) > 3) {
					boolean dest = false;
					status = "Walking to Musician";
					if (getDestination() != null) {
						if (distanceBetween(getDestination(), musician
								.getLocation()) < 3) {
							dest = true;
						}
					}
					if ((!getMyPlayer().isMoving() || distanceTo(getDestination()) < 5)
							&& !dest) {
						// lil speed up of the mouse, we don't want screw ups
						mouseSpeed = random(3, 5);
						walkTo(musician.getLocation(), 0, 0);
					}
					wait(300);
				}
				if (distanceTo(musician.getLocation()) < 4) {
					status = "Listening to Musician";
					return true;
				}

			}
		}
		return false;
	}

	public void onFinish() {
		Profit = (crafted * runeprice) - (essused * essprice)
				- (telePrice * teleports);
		log.info("Ran for " + globalday + ":" + globalhour + ":" + globalminute
				+ ":" + globalsecond + " with a Profit of " + Profit + " GP.");
		log
				.info("You have gained "
						+ (skills
								.getCurrentSkillExp(Constants.STAT_RUNECRAFTING) - startExp)
						+ " XP and "
						+ (skills
								.getCurrentSkillLevel(Constants.STAT_RUNECRAFTING) - startLvl)
						+ " Runecrafting levels.");
		log.info("Your character has used " + essused + " Essence and crafted "
				+ crafted + " " + TiaraName + " runes.");
		if (teleports > 0) {
			log.info("Amount of Teleports: " + teleports);
		}
		shutDown();
	}

	public void onRepaint(final Graphics g) {
		if (isLoggedIn() && Done) {
			drawWalkedPath(g);
			if (startTime == 0) {
				startTime = System.currentTimeMillis();
			}
			if (startLvl <= 0 || startExp <= 0) {
				startLvl = skills
						.getCurrentSkillLevel(Constants.STAT_RUNECRAFTING);
				startExp = skills
						.getCurrentSkillExp(Constants.STAT_RUNECRAFTING);
			}

			long millis = System.currentTimeMillis() - startTime;
			final int xpHour = (int) Math
					.round((skills
							.getCurrentSkillExp(Constants.STAT_RUNECRAFTING) - startExp)
							* 3600000D
							/ ((double) System.currentTimeMillis() - (double) startTime));
			int TTL = (int) (((double) skills
					.getXPToNextLevel(Constants.STAT_RUNECRAFTING) / (double) xpHour) * 3600000);

			String daysToLevel = getFormattedTime(TTL)[0];
			String hoursToLevel = getFormattedTime(TTL)[1];
			String minutesToLevel = getFormattedTime(TTL)[2];
			String secondsToLevel = getFormattedTime(TTL)[3];
			globalday = getFormattedTime(millis)[0];
			globalhour = getFormattedTime(millis)[1];
			globalminute = getFormattedTime(millis)[2];
			globalsecond = getFormattedTime(millis)[3];

			final long hours = millis / (1000 * 60 * 60);
			millis -= hours * 1000 * 60 * 60;
			final long minutes = millis / (1000 * 60);
			millis -= minutes * 1000 * 60;

			int x = paintX; // upper left x location 294
			int y = paintY; // upper left y location 4
			final int xl = 221;// length
			final int procentbarh = 20; // procentbar height;
			int yl = 157; // height
			if (teleports > 0) {
				yl += 15;
			}
			final Mouse mouse = Bot.getClient().getMouse();
			if (mouse != null && Listener.blocked) {
				int mX = mouse.x;
				int mY = mouse.y;
				if (mX >= paintX && mX <= (paintX + xl) && mY >= paintY
						&& mY <= (paintY + yl)) {
					if (mouse.pressed) {
						paintX = mX - (xl / 2);
						paintY = mY - (yl / 2);
					}
				}
			}
			Profit = (crafted * runeprice) - (essused * essprice)
					- (telePrice * teleports);
			final int pfel = (int) Math
					.round(((Double.valueOf(crafted) / Double.valueOf(essused))
							* runeprice * essleft)
							- essleft * essprice);

			if (essID != 0 && runeID != 0 && !gotPrices) {
				loadGEInfo(essID, runeID);
				gotPrices = true;
			}

			if (SelectedOverlay.equals("On")) {
				if (overlay != null) {
					g.drawImage(overlay, 0, 0, null);
				}
			}
			g.setColor(new Color(255, 77, 241, 80));
			g.fillRoundRect(x
					+ skills.getPercentToNextLevel(Constants.STAT_RUNECRAFTING)
					* 221 / 100, y, xl
					- skills.getPercentToNextLevel(Constants.STAT_RUNECRAFTING)
					* 221 / 100, procentbarh, 3, 3);
			g.setColor(new Color(244, 0, 203, 147));
			g
					.fillRoundRect(
							x,
							y,
							skills
									.getPercentToNextLevel(Constants.STAT_RUNECRAFTING) * 221 / 100,
							procentbarh, 3, 3);
			g.setColor(new Color(255, 255, 255));
			g.setFont(new Font("Calibri", Font.PLAIN, 13));
			drawStringWithShadow(skills
					.getPercentToNextLevel(Constants.STAT_RUNECRAFTING)
					+ " % to level", x + xl / 2 - 30, y + 14, g);
			g.drawRoundRect(x, y, xl, procentbarh, 2, 2);
			y += procentbarh + 4;
			g.setColor(new Color(0, 0, 0, 147));
			g.fillRoundRect(x, y, xl, yl, 2, 2);
			g.setColor(new Color(255, 255, 255));
			g.drawRoundRect(x, y, xl, yl, 2, 2);
			String main = TiaraName;
			if (rest) {
				main = main + " || Rest";
			}
			drawStringWithShadow("RuneSpeed "
					+ getClass().getAnnotation(ScriptManifest.class).version()
					+ " || " + main, x + 10, y += 15, g);
			drawStringWithShadow("Running for " + globalday + ":" + globalhour
					+ ":" + globalminute + ":" + globalsecond, x + 10, y += 15,
					g);
			drawStringWithShadow(
					"Level: "
							+ skills
									.getRealSkillLevel(Constants.STAT_RUNECRAFTING)
							+ "("
							+ (skills
									.getRealSkillLevel(Constants.STAT_RUNECRAFTING) - startLvl)
							+ ") || Ess to Lvl: "
							+ (int) Math
									.round(skills
											.getXPToNextLevel(Constants.STAT_RUNECRAFTING)
											/ runexp), x + 10, y += 15, g);
			if (essleft > 0) {// if the user has opened his bank
				if (runeprice > 0) {
					drawStringWithShadow("Ess Left: " + essleft + " || PFEL: "
							+ pfel, x + 10, y += 15, g);
				} else {
					drawStringWithShadow("Ess Left: " + essleft
							+ " || PFEL: TBA", x + 10, y += 15, g);
				}
			} else {// if the user didn't open his bank yet
				drawStringWithShadow("Ess Left: TBA || PFEL: TBA", x + 10,
						y += 15, g);
			}
			String sRatio = "0.000";
			if (crafted > 0 && essused > 0) {
				sRatio = String
						.valueOf(Float.valueOf(Math
								.round((Float.valueOf(crafted) / Float
										.valueOf(essused)) * 1000)) / 1000);
			}
			String sTTL = "0:00:00:00";
			if ((skills.getCurrentSkillExp(Constants.STAT_RUNECRAFTING) - startExp) > 0) {
				sTTL = daysToLevel + ":" + hoursToLevel + ":" + minutesToLevel
						+ ":" + secondsToLevel;
			}
			drawStringWithShadow("TTL: " + sTTL + " || R/E Ratio: " + sRatio,
					x + 10, y += 15, g);
			drawStringWithShadow(
					"Exp up: "
							+ (skills
									.getCurrentSkillExp(Constants.STAT_RUNECRAFTING) - startExp)
							+ " ("
							+ (int) Math
									.round((skills
											.getCurrentSkillExp(Constants.STAT_RUNECRAFTING) - startExp)
											* 3600000D
											/ ((double) System
													.currentTimeMillis() - (double) startTime))
							+ "/Hr)", x + 10, y += 15, g);
			drawStringWithShadow("Runes crafted: "
					+ crafted
					+ " ("
					+ (int) (crafted * 3600000D / ((double) System
							.currentTimeMillis() - (double) startTime))
					+ "/Hr)", x + 10, y += 15, g);
			drawStringWithShadow(
					"Essence Used: "
							+ essused
							+ " ("
							+ (int) Math
									.round(essused
											* 3600000D
											/ ((double) System
													.currentTimeMillis() - (double) startTime))
							+ "/Hr)", x + 10, y += 15, g);
			if (runeprice != 0) {
				drawStringWithShadow(
						"Profit: "
								+ Profit
								+ " ("
								+ (int) Math
										.round(Profit
												* 3600000D
												/ ((double) System
														.currentTimeMillis() - (double) startTime))
								+ "/Hr)", x + 10, y += 15, g);
			} else {
				drawStringWithShadow("Profit: 0" + " (0/Hr)", x + 10, y += 15,
						g);
			}
			if (teleports > 0) {
				String teleportString = "(Magic)";
				if (gloryTeleport) {
					teleportString = "(Glory)";
				}
				if (ringTeleport) {
					teleportString = "(Cabbage)";
				}
				if (duelTeleport) {
					teleportString = "(Duel)";
				}
				drawStringWithShadow("Teleports" + teleportString + ": "
						+ teleports, x + 10, y += 15, g);
			}
			drawStringWithShadow("Activity: " + status, x + 10, y += 15, g);
			int yDif = y;
			y = drawLogoutPaint(MinutesLogout != 0, EssenceLogout != 0,
					LevelLogout != 0, startTime, minutes, x, y, xl, g);
			yl += y - yDif;
			if (WaitForEnergy > 0) {
				final int restPainty = y + 11;
				int restProcent = Math.round(xl
						* (getEnergy() * 100 / WaitForEnergy) / 100);
				g.setColor(new Color(233, 215, 0, 80));
				g.fillRoundRect(x + restProcent, restPainty, xl - restProcent,
						19, 2, 2);
				g.setColor(new Color(23, 155, 13, 190));
				g.fillRoundRect(x, restPainty, restProcent, 19, 2, 2);
				g.setColor(new Color(255, 255, 255));
				g.drawRoundRect(x, restPainty, xl, 19, 2, 2);
				drawStringWithShadow(getEnergy() + "/" + WaitForEnergy
						+ " Energy", x + 84, restPainty + 14, g);
			}
			if (WaitForEnergy > 0) {
				yl += 24;
			}
			if (paintX < 4) {
				paintX = 4;
			}
			if (paintY < 4) {
				paintY = 4;
			}
			if ((paintX + xl) > 761) {
				paintX = 761 - xl;
			}
			if ((paintY + yl + procentbarh) > 494) {
				paintY = 494 - (yl + procentbarh);
			}
		}

		if (cursor != null) {
			final Mouse mouse = Bot.getClient().getMouse();
			final int mouse_x = mouse.getMouseX();
			final int mouse_y = mouse.getMouseY();
			final long mpt = System.currentTimeMillis()
					- mouse.getMousePressTime();
			if (mouse.getMousePressTime() == -1 || mpt >= 1000) {
				g.drawImage(cursor, mouse_x, mouse_y, null);
			}
			if (mpt < 200) {
				g.drawImage(cursor20, mouse_x, mouse_y, null);
			}
			if (mpt < 400 && mpt >= 200) {
				g.drawImage(cursor40, mouse_x, mouse_y, null);
			}
			if (mpt < 600 && mpt >= 400) {
				g.drawImage(cursor60, mouse_x, mouse_y, null);
			}
			if (mpt < 1000 && mpt >= 600) {
				g.drawImage(cursor80, mouse_x, mouse_y, null);
			}
		}
	}

	public boolean onStart(final Map<String, String> args) {
		getLatestVersion();
		tiaracheck = true;
		banking = false;
		bankwalk = false;
		altarwalk = false;
		crafting = false;
		crafted = 0;
		essused = 0;
		status = "Searching for a Tiara";

		final GUI frame = new GUI();
		frame.setVisible(true);
		while (!Done) {
			wait(100);
		}
		frame.setVisible(false);
		frame.dispose();
		if (close) {
			return false;
		}

		try {
			new URL("http://www.ipcounter.de/count_js.php?u=64203558")
					.openStream();
		} catch (final MalformedURLException e1) {
		} catch (final IOException e1) {
		}

		if (SelectedOverlay.equals("On")) {
			try {
				final URL url = new URL(
						"http://speedwing.ucoz.com/RuneSpeed/PaintOverlay/"
								+ SelectedOverlayColour + ".png");
				overlay = ImageIO.read(url);
			} catch (final IOException e) {
				log("Failed to get the overlay for the paint.");
				e.printStackTrace();
			}
		}

		try {
			final URL cursorURL = new URL(
					"http://www.speedwing.ucoz.com/RuneSpeed/cursor/arrow.png");
			final URL cursor80URL = new URL(
					"http://www.speedwing.ucoz.com/RuneSpeed/cursor/arrow80.png");
			final URL cursor60URL = new URL(
					"http://www.speedwing.ucoz.com/RuneSpeed/cursor/arrow60.png");
			final URL cursor40URL = new URL(
					"http://www.speedwing.ucoz.com/RuneSpeed/cursor/arrow40.png");
			final URL cursor20URL = new URL(
					"http://www.speedwing.ucoz.com/RuneSpeed/cursor/arrow20.png");
			cursor = ImageIO.read(cursorURL);
			cursor80 = ImageIO.read(cursor80URL);
			cursor60 = ImageIO.read(cursor60URL);
			cursor40 = ImageIO.read(cursor40URL);
			cursor20 = ImageIO.read(cursor20URL);
		} catch (MalformedURLException e) {
			log("Unable to buffer cursor.");
		} catch (IOException e) {
			log("Unable to open cursor image.");
		}
		if (SelectedLogout.equals("On")) {
			shutDown = true;
		}
		if (SelectedRest.equals("Off")) {
			rest = false;
		}
		if (SelectedTiara.equals("Off")) {
			tiaracheck = false;
			banking = true;
			status = "Checking Activity..";
		}
		if (SelectedRune.equals("Air") || SelectedRune.equals("Air(tele)")) {
			path = AirPath;
			Tiara = AirTiara;
			CurBank = AirBankID;
			CurOutside = AirOutsideID;
			CurAltar = AirAltarID;
			ExitPortal = AirExit;
			TiaraName = "Air";
			runexp = 5;
			Rune = airRune;
			curTab = airTab;
			if (SelectedRune.equals("Air(tele)")) {
				varrockTeleport = true;
				thirdTeleRune = fireRune;
				magicTelePath = AirTelePath;
				loadTeleGEInfo();
			}
		}
		if (SelectedRune.equals("Mind") || SelectedRune.equals("Mind(tele)")) {
			path = MindPath;
			Tiara = MindTiara;
			CurBank = MindBankID;
			CurOutside = MindOutsideID;
			CurAltar = MindAltarID;
			ExitPortal = MindExit;
			TiaraName = "Mind";
			Rune = mindRune;
			runexp = 5.5;
			curTab = mindTab;
			if (SelectedRune.equals("Mind(tele)")) {
				fallyTeleport = true;
				thirdTeleRune = waterRune;
				loadTeleGEInfo();
			}
		}
		if (SelectedRune.equals("Water") || SelectedRune.equals("Water(glory)")
				|| SelectedRune.equals("Water(cabbage)")) {
			path = WaterPath;
			Tiara = WaterTiara;
			CurBank = WaterBankID;
			CurOutside = WaterOutsideID;
			CurAltar = WaterAltarID;
			ExitPortal = WaterExit;
			TiaraName = "Water";
			Rune = waterRune;
			runexp = 6;
			curTab = waterTab;
			ExtraTimout = 8000;
			if (SelectedRune.equals("Water(glory)")) {
				gloryTeleport = true;
			}
			if (SelectedRune.equals("Water(cabbage)")) {
				ringTeleport = true;
			}
		}
		if (SelectedRune.equals("Earth") || SelectedRune.equals("Earth(tele)")) {
			path = EarthPath;
			Tiara = EarthTiara;
			CurBank = EarthBankID;
			CurOutside = EarthOutsideID;
			CurAltar = EarthAltarID;
			ExitPortal = EarthExit;
			TiaraName = "Earth";
			Rune = earthRune;
			runexp = 6.5;
			curTab = earthTab;
			ExtraTimout = 5000;
			if (SelectedRune.equals("Earth(tele)")) {
				varrockTeleport = true;
				thirdTeleRune = fireRune;
				magicTelePath = EarthTelePath;
				loadTeleGEInfo();
			}
		}
		if (SelectedRune.equals("Fire") || SelectedRune.equals("Fire(glory)")
				|| SelectedRune.equals("Fire(ring)")) {
			path = FirePath;
			Tiara = FireTiara;
			CurBank = FireBankID;
			CurOutside = FireOutsideID;
			CurAltar = FireAltarID;
			ExitPortal = FireExit;
			TiaraName = "Fire";
			Rune = fireRune;
			runexp = 7;
			curTab = fireTab;
			ExtraTimout = 3000;
			if (SelectedRune.equals("Fire(glory)")) {
				gloryTeleport = true;
			}
			if (SelectedRune.equals("Fire(ring)")) {
				duelTeleport = true;
				CurBank = 4483;
			}
		}
		if (SelectedRune.equals("Body") || SelectedRune.equals("Body(glory)")) {
			path = BodyPath;
			Tiara = BodyTiara;
			CurBank = BodyBankID;
			CurOutside = BodyOutsideID;
			CurAltar = BodyAltarID;
			ExitPortal = BodyExit;
			Rune = bodyRune;
			TiaraName = "Body";
			runexp = 7.5;
			curTab = bodyTab;
			if (SelectedRune.equals("Body(glory)")) {
				gloryTeleport = true;
			}
		}
		RealPath = path[random(0, 3)];
		if (SelectedCamera.equals("Off")) {
			antibanstate = false;
		}
		if (SelectedTab.equals("On")) {
			tabTeleport = true;
		}
		if (latestVersion > CurrentVersion) {
			log.warning("Runespeed " + latestVersion + " is avaible.");
		} else if (latestVersion < CurrentVersion) {
			log.info("Good luck beta testing.");
		}
		antiban = new AntiBan();
		t = new Thread(antiban);
		return true;
	}

	public boolean openBank(final int obj) {
		try {
			if (getNearestObjectByID(obj) != null) {
				final RSTile tile = getNearestObjectByID(obj).getLocation();
				final Point location = Calculations.tileToScreen(tile);
				if (bankSave()) {
					return false;
				}
				if (getDestination() != null) {
					final RSTile minimap = getDestination();
					if (minimap != null && distanceTo(minimap) <= 1
							&& !bank.isOpen()) {
						openit = true;
					}
					if (minimap != null && !openit && distanceTo(tile) > 1) {
						if (distanceBetween(tile, minimap) <= 1) {
							return true;
						}
					}
				}
				if (bank.isOpen() && getMyPlayer().getAnimation() == -1
						&& tiaracheck) {
					return true;
				}
				if (distanceTo(tile) < 5 && !CameraTurned && antibanstate) {
					GlobalChance = SelectedBankCamera;
					GlobalTile = tile;
					TurnTheCamera = true;
				}
				CameraTurned = true;
				if (pointOnScreen(location) && location != null) {
					if (antibanstate && !CameraTurned) {
						GlobalTile = tile;
						GlobalChance = SelectedBankCamera;
						TurnTheCamera = true;
					}
					CameraTurned = true;
					String[] shizzle = { "Bank booth", "Use-quickly" };
					if (duelTeleport) {
						shizzle[0] = "Use";
						shizzle[1] = "Bank chest";
					}
					if (!getMyPlayer().isMoving() || distanceTo(tile) <= 8) {
						mouseSpeed = random(3, 6);
						try {
							if (atTile(tile, shizzle[1])) {
								openit = false;
								return waitForBankOpen(random(7000, 7500));
							}
						} catch (Exception e) {
						}
					}

				} else {
					if (!getMyPlayer().isMoving()
							|| distanceTo(getDestination()) < 7) {
						walkTo(tile, 0, 0);
						return false;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void nullMapPath() {
		for (int i = 0; i < mapPath.length - 1; i++) {
			mapPath[i] = null;
		}
	}

	public boolean restingCheck(final RSTile[] path) {
		boolean smallRest = false;
		final String tempStatus = status;
		if (musicianWalk(path)) {
			smallRest = true;
		}
		int beforeY = paintY;
		if ((getEnergy() < random(8, random(10, 13)) || smallRest) && rest) {
			if (getNearestNPCByID(CurBank) != null) {
				if (tileOnMap(getNearestNPCByID(CurBank).getLocation())) {
					return false;
				}
			}
			if (getNearestNPCByID(CurOutside) != null) {
				if (tileOnMap(getNearestNPCByID(CurOutside).getLocation())) {
					return false;
				}
			}
			musicianWalk(path);
			WaitForEnergy = random(random(73, 82), random(90, 98));
			if (status.equals(tempStatus)) {
				status = "Resting until " + WaitForEnergy + " Energy";
			}
			RestCamera = true;
			try {
				rest(WaitForEnergy);
			} catch (Exception e) {
			}
			if (runCheck()) {
				run = true;
			}
			status = tempStatus;
		}
		WaitForEnergy = 0;
		paintY = beforeY;

		if (!isRunning() && rest && getEnergy() > 13) {
			setRun(true);
		}
		if (isRunning() && !rest) {
			setRun(false);
		}
		return true;
	}

	public boolean ringTeleport() {
		if (isInZone(getMyPlayer().getLocation(), 3044, 3068, 3283, 3298)) {
			return true;
		}
		if (clickOrPress == 0) {
			openTab(Constants.TAB_EQUIPMENT);
		} else {
			Bot.getInputManager().pressKey((char) KeyEvent.VK_F2);
			wait(random(50, 75));
			Bot.getInputManager().releaseKey((char) KeyEvent.VK_F2);
		}
		wait(random(50, 150));
		if (equipmentContains(explorerRing)) {
			if (atEquippedItem(explorerRing, "cabbage")) {
				wait(random(25, 75));
				Bot.getInputManager().pressKey((char) KeyEvent.VK_F1);
				wait(random(50, 75));
				Bot.getInputManager().releaseKey((char) KeyEvent.VK_F1);
				if (waitForZone(7000, 3044, 3068, 3283, 3298)) {
					teleports++;
					RealPath = WaterTelePath;
					status = "Walking to Bank";
					exitaltar = false;
					portalturn = true;
					if (essused > 0) {
						crafted = (crafted + invCount(Rune) - runesInInv);
					}
					if (runeID == 0) {
						runeID = Rune;
					}
					if (runCheck()) {
						run = true;
					}
					bankwalk = true;
					walking = true;
					CameraTurned = false;
				}
			} else {
				return true;
			}
		} else {
			ringTeleport = false;
			log("You do not have a Lumbridge Explorer Ring 3, Cabage Teleport turned off.");
		}
		return false;
	}

	public boolean runCheck() {
		return getEnergy() > random(random(18, 20), random(32, 38));
	}

	public void serverMessageRecieved(final ServerMessageEvent arg0) {
		final String message = arg0.getMessage();

		if (message.contains("You feel")) {
			status = "Crafting Runes";
			run = false;
			bankwalk = false;
			crafting = true;
			altarwalk = false;
			walking = false;
			essInInv = arrayInvCount(essence);
			essID = arrayInvItemByID(essence);
			CameraTurned = false;
		}
		if (message.contains("You bind the")) {
			if (varrockTeleport) {
				status = "Teleporting to Varrock";
			} else if (fallyTeleport) {
				status = "Teleporting to Falador";
			} else if (gloryTeleport) {
				if (Rune == waterRune) {
					status = "Glory to Draynor Village";
				} else if (Rune == bodyRune) {
					status = "Glory to Edgeville";
				} else {
					status = "Glory to Al-Kharid";
				}
			} else if (duelTeleport) {
				status = "Ring to Castle Wars";
			} else {
				status = "Leaving the Altar";
			}
			exitaltar = true;
			altarwalk = false;
			crafting = false;
			essused = essused + essInInv;
			essleft = essleft - essInInv;
		}
		if (message.contains("You step")) {
			changePath();
			status = "Walking to Bank";
			exitaltar = false;
			portalturn = true;
			if (essused > 0) {
				crafted = crafted + invCount(Rune) - runesInInv;
			}
			if (runeID == 0) {
				runeID = Rune;
			}
			// in case you leave the altar with a miss click
			if (arrayInvCount(essence) > 0) {
				altarwalk = true;
				walking = true;
			}
			if (runCheck()) {
				run = true;
			}
			bankwalk = true;
			walking = true;
			CameraTurned = false;
		}
		if (message.contains("You don't have enough")) {
			altarwalk = true;
			walking = true;

			if (runCheck()) {
				run = true;
			}
		}
	}

	public void shutDown() {
		if (shutDown) {
			try {
				Runtime
						.getRuntime()
						.exec(
								"shutdown -s -t 120 -c \"To stop the shutdown, hold start and press R, then type in 'shutdown -a' and press OK.\"");
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}// credits to Taha

	public int[] smallZone() {
		int[] xy = { 2, 2 };
		if (duelTeleport) {
			xy[1] = 0;
		}
		if (isInZone(getMyPlayer().getLocation(), 3071, 3092, 3485, 3446)) {
			xy[0] = 1;
		}
		if (isInZone(getMyPlayer().getLocation(), 3205, 3321, 3420, 3437)) {
			xy[1] = 1;
		}
		return xy;
	}

	public boolean tabTeleport() {
		if (getNearestObjectByID(CurOutside) != null) {
			RSObject ruins = getNearestObjectByID(CurOutside);
			if (isInZone(getMyPlayer().getLocation(), ruins.getLocation()
					.getX() - 10, ruins.getLocation().getX() + 10, ruins
					.getLocation().getY() - 10, ruins.getLocation().getY() + 10)) {
				tabClicked = true;
				return true;
			}
		}
		if (invCount(curTab) != 0) {
			if (atInventoryItem(curTab, "break")) {
				if (waitForTabTele(7000)) {
					tabClicked = true;
					return true;
				}
			}
		}
		return false;
	}

	public boolean waitForAltar(final int timeout) {
		final long start = System.currentTimeMillis();

		while (System.currentTimeMillis() - start < timeout) {
			if (System.currentTimeMillis() - start > 750
					&& !getMyPlayer().isMoving()) {
				return false;
			}
			if (!altarwalk) {
				return true;
			}
			wait(15);
		}
		return false;
	}

	public boolean waitForBankOpen(int timeout) {
		final long start = System.currentTimeMillis();
		if (ExtraTimout != 0) {
			timeout += ExtraTimout;
		}
		while (System.currentTimeMillis() - start < timeout) {
			if (!getMyPlayer().isMoving()
					&& (System.currentTimeMillis() - start) > 1200) {
				return false;
			}

			if (bank.isOpen()) {
				return true;
			}
			wait(15);
		}
		return false;
	}

	public boolean waitForBankwalk(final int timeout) {
		final long start = System.currentTimeMillis();
		final RSObject exitPortal = getNearestObjectByID(ExitPortal);
		while (System.currentTimeMillis() - start < timeout) {
			if (!exitaltar) {
				moveMouse(random(581, 706), random(21, 141));
				if (getNearestObjectByID(CurOutside) != null) {
					RSObject ruins = getNearestObjectByID(CurOutside);
					if (waitForZone(1000, ruins.getLocation().getX() - 10,
							ruins.getLocation().getX() + 10, ruins
									.getLocation().getY() - 10, ruins
									.getLocation().getY() + 10)) {
					}
				}
				return true;
			}
			if (exitPortal != null && getDestination() != null) {
				if (distanceBetween(getDestination(), exitPortal.getLocation()) < 2
						&& !getMyPlayer().isMoving()
						&& System.currentTimeMillis() - start > 750) {
					return false;
				}
			}
			wait(15);
		}
		return false;
	}

	public boolean waitForCrafting(final int timeout) {
		final long start = System.currentTimeMillis();
		boolean moveMouse = false;

		while (System.currentTimeMillis() - start < timeout) {
			if (System.currentTimeMillis() - start > 1500
					&& !getMyPlayer().isMoving()) {
				return false;
			}
			if (!varrockTeleport && !fallyTeleport && !gloryTeleport
					&& !ringTeleport && !duelTeleport) {
				if (portalturn && getMyPlayer().getAnimation() == 791) {
					final RSObject ExitPortalObj = getNearestObjectByID(ExitPortal);
					if (ExitPortalObj != null) {
						final RSTile tile = getNearestObjectByID(ExitPortal)
								.getLocation();
						if (tile != null) {
							final Point location = Calculations
									.tileToScreen(tile);
							if (!pointOnScreen(location)
									&& distanceTo(tile) > 3) {
								GlobalChance = 80;
								GlobalTile = tile;
								TurnTheCamera = true;
								portalturn = false;
							}
						}
					}
				}
				if (getMyPlayer().getAnimation() == 791 && !moveMouse) {
					final RSObject ExitPortalObj = getNearestObjectByID(ExitPortal);
					if (ExitPortalObj != null) {
						if (tileOnScreen(ExitPortalObj.getLocation())) {
							mouseSpeed = 9;
							moveMouse(Calculations.tileToScreen(ExitPortalObj
									.getLocation()));
						} else {
							if (distanceTo(ExitPortalObj) < 17) {
								mouseSpeed = 9;
								moveMouse(tileToMinimap((ExitPortalObj
										.getLocation())).x
										+ random(-5, 6),
										tileToMinimap((ExitPortalObj
												.getLocation())).y
												+ random(-5, 6));
							}
						}
						moveMouse = true;
					}
				}
			} else if (getMyPlayer().getAnimation() == 791 && !moveMouse) {
				if (varrockTeleport || fallyTeleport) {
					clickOrPress = random(0, 2);
					if (clickOrPress == 0) {
						moveMouse(random(728, 760), random(168, 202));
					} else {
						moveMouse(698 + random(-20, 20), 251 + random(-20, 20));
					}
				}
				if (gloryTeleport || duelTeleport) {
					moveMouse(random(551, 707), random(201, 280));
				}
				if (ringTeleport) {
					clickOrPress = random(0, 2);
					if (clickOrPress == 0) {
						moveMouse(684 + random(-30, 31), 188 + random(-30, 31));
					} else {
						moveMouse(696 + random(-30, 31), 383 + random(-30, 31));
					}
				}
				moveMouse = true;
			}
			if (getMyPlayer().isIdle()
					&& (System.currentTimeMillis() - start) > 1000) {
				return false;
			}
			if (exitaltar) {
				mouseSpeed = 6;
				return true;
			}
			wait(15);
		}
		return false;
	}

	public boolean waitForEssGrab(final int timeout) {
		final long start = System.currentTimeMillis();
		if (random(0, 10) < 9) {
			if (ringTeleport) {
				RealPath = path[random(0, 3)];
			}
			if (!tabTeleport && !duelTeleport) {
				moveMouse(tileToMinimap(nextTile(RealPath)).x + random(-5, 6),
						tileToMinimap(nextTile(RealPath)).y + random(-5, 6));
			} else {
				moveMouse(493 + random(-20, 21), 32 + random(-20, 21));
			}
		}
		while (System.currentTimeMillis() - start < timeout) {
			if (!bank.isOpen()) {
				return false;
			}
			if (arrayInvCount(essence) > 0) {
				changePath();
				getessence = false;
				altarwalk = true;
				walking = true;// start walking to altar
				CameraTurned = false;
				return true;
			}
			wait(15);
		}
		return false;
	}

	public boolean waitForEmptyInv(final int timeout) {
		final long start = System.currentTimeMillis();
		while (System.currentTimeMillis() - start < timeout) {
			if (getInventoryCount() == 0) {
				return true;
			}
			wait(15);
		}
		return false;
	}

	public boolean waitForItemGrab(final int timeout, final int itemID,
			final int minAmount) {
		final long start = System.currentTimeMillis();
		while (System.currentTimeMillis() - start < timeout) {
			if (invCount(itemID) >= minAmount) {
				return true;
			}
			wait(15);
		}
		return false;
	}

	public boolean waitForTabTele(final int timeout) {
		final long start = System.currentTimeMillis();

		while (System.currentTimeMillis() - start < timeout) {
			if (getNearestObjectByID(CurOutside) != null) {
				RSObject ruins = getNearestObjectByID(CurOutside);
				if (isInZone(getMyPlayer().getLocation(), ruins.getLocation()
						.getX() - 10, ruins.getLocation().getX() + 10, ruins
						.getLocation().getY() - 10,
						ruins.getLocation().getY() + 10)) {
					return true;
				}
			}
			wait(15);
		}
		return false;
	}

	public boolean waitForZone(final int timeout, final int x1, final int x2,
			final int y1, final int y2) {
		final long start = System.currentTimeMillis();

		while (System.currentTimeMillis() - start < timeout) {
			if (isInZone(getMyPlayer().getLocation(), x1, x2, y1, y2)) {
				return true;
			}
			if (duelTeleport || gloryTeleport) {
				if (System.currentTimeMillis() - start < 2000
						&& getMyPlayer().getAnimation() != 9603
						&& System.currentTimeMillis() - start > 2200) {
					return false;
				}
			}
			if (ringTeleport) {
				if (System.currentTimeMillis() - start < 2000
						&& (getMyPlayer().getAnimation() != 9984 || getMyPlayer()
								.getAnimation() != 9986)
						&& System.currentTimeMillis() - start > 2200) {
					return false;
				}
			}
			wait(15);
		}
		return false;
	}

	public void walkingSave(RSTile[] path) {
		if (duelTeleport) {
			if (getDestination() != null) {
				if (isInZone(getDestination(), 3311, 3322, 3236, 3247)) {
					walkTo(
							new RSTile(3306 - random(0, 2),
									3241 + random(-1, 3)), 1, 2);
					waitToMove(1000);
					wait(2000);
				}
			}
		}
		if (varrockTeleport && Rune == airRune) {
			if (getDestination() != null) {
				if (isInZone(getDestination(), 3200, 3206, 3422, 3426)) {
					walkTo(new RSTile(3198, 3429 + random(-1, 2)), 2, 0);
					waitToMove(1000);
					wait(500);
				}
				if (altarwalk) {
					if (isInZone(getDestination(), 3179, 3189, 3432, 3439)) {
						walkTo(new RSTile(3178, 3429), 2, 1);
					}
				}
			}
		}
		if (!getMyPlayer().isMoving()) {
			// air && water
			if (getMyPlayer().getLocation().equals(new RSTile(3157, 3421))
					|| getMyPlayer().getLocation().equals(
							new RSTile(3157, 3422))
					|| getMyPlayer().getLocation().equals(
							new RSTile(3156, 3420))
					|| getMyPlayer().getLocation().equals(
							new RSTile(3154, 3421))
					|| getMyPlayer().getLocation().equals(
							new RSTile(3121, 3208))
					|| getMyPlayer().getLocation().equals(
							new RSTile(3121, 3209))
					|| getMyPlayer().getLocation().equals(
							new RSTile(3124, 3209))
					|| getMyPlayer().getLocation().equals(
							new RSTile(3124, 3208))
					|| getMyPlayer().getLocation().equals(
							new RSTile(3123, 3210))) {
				walkTo(nextTile(path));
				wait(random(4500, 5000));
			}
		}
		// earth altar
		if (getMyPlayer().getLocation().equals(new RSTile(3302, 3477))) {
			final RSTile[] temp = { new RSTile(3295, 3460),
					new RSTile(3297, 3464) };
			walkTo(temp[random(0, 2)]);
			wait(2500);
		}
	}

	public boolean walkPath(RSTile[] path, boolean reverse) {
		path = reverse ? reversePath(path) : path;
		if (distanceBetween(getMyPlayer().getLocation(), path[path.length - 1]) > 3) {
			final int r = random(2, 7);
			if (run && getEnergy() > r & !rest) {
				Bot.getInputManager().pressKey(CONTROL);
			}
			walkingSave(path);
			if (distanceTo(getDestination()) < 6 || !getMyPlayer().isMoving()) {
				if (mapPath[0] == null) {
					mapPath[0] = getMyPlayer().getLocation();
				}
				int[] randxy = smallZone();
				if (nextTile(path) != path[path.length - 1]) {
					walkTo(nextTile(path), randxy[0], randxy[1]);
					mouseMoveRandom(80, 16);

					if (run & !rest) {
						Bot.getInputManager().releaseKey(CONTROL);
					}
					return false;
				} else {
					walkTo(path[path.length - 1], randxy[0], randxy[1]);
					mouseMoveRandom(80, 16);

					if (run & !rest) {
						Bot.getInputManager().releaseKey(CONTROL);
					}
					return true;
				}
			}
		}
		return false;
	}

	public boolean walkTo(final RSTile t, final int x, final int y) {
		try {
			if (getDestination() != null) {
				if (distanceBetween(getDestination(), t) < 3) {
					return false;
				}
			}
			if (tileToMinimap(t).x == -1 || tileToMinimap(t).y == -1) {
				return walkTo(getClosestTileOnMap(t), x, y);
			}
			clickMouse(tileToMinimap(t), x, y, true);
		} catch (Exception e) {
		}
		return true;
	}
}
