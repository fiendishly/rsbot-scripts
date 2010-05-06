/*
* ZombieFisherEXTREME V7.4
*
* Credits:
* BamBino/cronshaw1234/Zorlix - Updaters
* Carmera Spin/Harpoon update - Lone Spartan
* Welcome - Ruski
* TBT and Aelin for scripting this.
* The Immortal for letting me use his paint thingy :D
*/

import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.awt.Color;
import java.awt.Graphics;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import javax.swing.JOptionPane;

import org.rsbot.util.ScreenshotUtil;
import org.rsbot.bot.Bot;
import org.rsbot.script.ScriptManifest;
import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSItemTile;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSPlayer;
import org.rsbot.script.wrappers.RSTile;
import org.rsbot.script.*;


@ScriptManifest(authors = {"ZombieKnight"}, category = "Fishing", name = "ZombieFisherEXTREME", version = 7.4, description = "<html><head><style type='text/css'> hr {color: white} p {margin-left: 20px}</style></head><body><center><b><font size='4' color='Blue'>ZombieFisherEXTREME v7.4</font></b><br></center><center><table border='0'><tr><td colspan='2'><center><font size='4'><b>:: Script Settings ::</b></font></center></td></tr><tr><td colspan='2'><hr></td></tr><tr><td><center><table border='0'><tr><td colspan='2'><center><font size='4'><b>Contact me at ZombieKnight_RSBot@hotmail.com</b></font></center></td></tr><tr><td colspan='2'><hr></td></tr><tr><td><tr><td><b>Location: </b></td><td><center><select name='locationName'><option>Al-Kharid<option>Barb Village(EV)<option>Catherby<option>Draynor<option>Fishing Guild<option>Karamja<option>[STILES]Karamja<option>Shilo<option>Piscatoris</select></center></td></tr><tr><td><b>Catch: </b></td><td><center><select name='catchName'><option>Pike<option>Bass/Cod/Mackerel<option>Shrimp/Anchovies<option>Herring/Sardines<option>Trout/Salmon<option>Tuna/Swordfish<option>Tuna/Swordfish(CHARPOON)<option>Lobsters<option>Sharks<option>Sharks(CHARPOON)<option>Rainbow Fish<option>Monkfish</select></center></td></tr><tr><td><b>Paint Color: </b></td><td><center><select name='pColor'><option>PinkPanther<option>SunKist<option>ClearSky<option>Monochrome<option>Nightmare<option>BloodShed</select></center></td></tr><tr><td><b>AntiTunas:</b></td><td><center><input type=\"checkbox\" name=\"antiTunas\" value=\"true\"><B>Yes</b></center></td></tr><tr><td><b>Powerfishing Mode:</b></td><td><center><input type='checkbox' name='powerFishing' value='true'><B>Yes</b></center></td></tr><tr><td><b>Barbarian Fishing/Barb-Tail:</b></td><td><center><input type='checkbox' name='barbarianMode' value='true'><B>Yes</b></center></td></tr><tr><td><b>Paint Report:</b></td><td><center><input type='checkbox' name='usePaint' checked='true' value='true'><B>Yes</b></center></td></tr></table><center><p>For Support/Comments, Pls click <a href='http://www.rsbot.org/vb/showthread.php?t=52649'>HERE</a></p><center><center><p>For Suggestions/Requests/Bug reports, Pls click <a href='http://www.rsbot.org/vb/showthread.php?t=48599'>HERE</a></p><center></center></body></html>")
public class ZombieFisher extends Script implements ServerMessageListener, PaintListener {
	int randomInt;
	int GambleInt;

	// State constants:
	public final int S_WALKTO_BANK = 100;
	public final int S_WALKTO_SPOT = 200;
	public final int S_FISH = 300;
	public final int S_TUNA = 400;
	public final int S_THROW_TUNAS = 500;
	public final int S_DROP_ALL = 600;
	public final int S_USE_BANK = 700;
	public final int S_DEPOSIT = 800;
	public final int S_WITHDRAW = 900;


	// Bait constants:
	public final int BAIT_NONE = -1;
	public final int BAIT_BAIT = 313;
	public final int BAIT_FEATHERS = 314;
	public final int BAIT_STRIPY = 10087;

	// Gear constants:
	public final int GEAR_NET = 303;
	public final int GEAR_ROD = 307;
	public final int GEAR_FLYROD = 309;
	public final int GEAR_CAGE = 301;
	public final int GEAR_CHARPOON = 14109;
	public final int GEAR_HARPOON = 311;
	public final int GEAR_BIGNET = 305;
	public final int GEAR_BARB = 10129;
	public final int GEAR_NONE = -1;

	// Paths and tiles:
	public RSTile[] toBank;
	public RSTile[] toArea;
	RSTile[] lostTiles = new RSTile[]{new RSTile(2860, 3428), new RSTile(2863, 2978), new RSTile(2835, 2975)};
	RSTile[] recoverTiles = new RSTile[]{new RSTile(2849, 3430), new RSTile(2855, 2971), new RSTile(2850, 2970)};

	// Runtime configuration.
	public int currentGear;
	public int currentBait;
	public String currentCommand;
	public int fishingSpotID;
	public int bankID;
	public int shopID;
	public boolean usesNPCBanking;

	// Script configuration.
	public String locationName;
	public String catchName;
	public String pColor;
	public boolean barbarianMode;
	public boolean powerFishing;
	public boolean antiTunas;
	public boolean usePaint;
	public boolean Sound;
	public boolean isPvP;
	public boolean hasEquipped;
	public long oldCatches = 0;
	public long catches = 0;

	// Misc variables.
	public int currentFails = 0;
	public int randomRunEnergy;
	public int state = S_FISH;
	public boolean runningFromCombat;
	public long scriptStartTime;
	public int playerStartXP;
	public int numberOfCatches;
	public long lastCheck;
	public long checkTime;
	public int countToNext = 0;
	public int timesAvoidedCombat;
	public int whirlpoolsAvoided;
	public int timesRecoveredGear;
	public int startLevel;
	public int lastExp;
	public int xpPerCatch = 0;
	public int oldExp;
	public int updateCheck = 0;
	public RSPlayer PvPPlayer;
	public int sCB;
	public int startExp;
	public boolean sRM;
	public boolean StartedY;


	public int[] whirlpools = new int[]{
			403, 404, 406, 406};

	int[] equipItems = {10129, 14109};

	int[] itemIDs = {10129, 14109};

	public void turnCamera() {
		char[] LR = new char[]{KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT};
		char[] UD = new char[]{KeyEvent.VK_DOWN, KeyEvent.VK_UP};
		char[] LRUD = new char[]{KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT,
				KeyEvent.VK_UP, KeyEvent.VK_DOWN};
		int random2 = random(0, 2);
		int random1 = random(0, 2);
		int random4 = random(0, 4);

		if (random(0, 2) == 2) {
			Bot.getInputManager().pressKey(LR[random1]);
			try {
				Thread.sleep(random(100, 400));
			} catch (Exception e) {
			}
			Bot.getInputManager().pressKey(UD[random2]);
			try {
				Thread.sleep(random(300, 600));
			} catch (Exception e) {
			}
			Bot.getInputManager().releaseKey(UD[random2]);
			try {
				Thread.sleep(random(100, 400));
			} catch (Exception e) {
			}
			Bot.getInputManager().releaseKey(LR[random1]);
		} else {
			Bot.getInputManager().pressKey(LRUD[random4]);
			if (random4 > 1) {
				try {
					Thread.sleep(random(300, 600));
				} catch (Exception e) {
				}
			} else {
				try {
					Thread.sleep(random(500, 900));
				} catch (Exception e) {
				}
			}
			Bot.getInputManager().releaseKey(LRUD[random4]);
		}
	}

	/*
		 * Pre-runtime configuration takes place within this method.
		 */
	public boolean onStart(final Map<String, String> args) {

		final int welcome = JOptionPane
				.showConfirmDialog(
						null,
						"Before using my script, would you like to thank me\nby clicking some adverts?",
						"Welcome", JOptionPane.YES_NO_OPTION);
		if (welcome == 0) {
			final String message = "<html><h1>Thank you for your support!</h1><br/>"
					+ "<p>You will now be redirected to my adverts page. <br/>"
					+ "Click the adverts on the page few times a day if you can.</p>"
					+ "</html>";
			JOptionPane.showMessageDialog(null, message);
			openURL("http://a2ea4421.linkbucks.com");
		}


		Reset();
		//checkupdate();
		// Set script start time
		scriptStartTime = System.currentTimeMillis();
		// Load script configuration from arguements.
		locationName = args.get("locationName");
		catchName = args.get("catchName");
		pColor = args.get("pColor");
		barbarianMode = args.get("barbarianMode") != null ? true : false;
		powerFishing = args.get("powerFishing") != null ? true : false;
		antiTunas = args.get("antiTunas") != null ? true : false;
		usePaint = args.get("usePaint") != null ? true : false;
		isPvP = args.get("PvPWorld") != null ? true : false;
		Sound = args.get("wSound") != null ? true : false;

		if (catchName.equals("Trout/Salmon")) {
			log("Please buy your feathers for the lowest price in the Grand Exchange.");
			log("I thank you for doing that, the current price for feathers is too high.");
			log("So, please help us out in the quest of bringing it down.");

		}


		// Al Kharid locations:
		if (locationName.equals("Al-Kharid")) {
			log("Setting Mummyfied paths for Al-Kharid.");
			log("[Reminder]Pls start in the bank or at the fishing spots.");
			toBank = new RSTile[]{new RSTile(3271, 3144), new RSTile(3276, 3157), new RSTile(3270, 3167)};
			toArea = reversePath(toBank);
			usesNPCBanking = true;

			if (catchName.equals("Shrimp/Anchovies")) {
				currentGear = GEAR_NET;
				currentBait = BAIT_NONE;
				fishingSpotID = 330;
				currentCommand = "Net";
				bankID = 496;
				return true;
			}

			if (catchName.equals("Herring/Sardines")) {
				currentGear = GEAR_ROD;
				currentBait = BAIT_BAIT;
				fishingSpotID = 330;
				currentCommand = "Bait";
				bankID = 496;
				return true;
			}
		}


		// Barbarian Village locations:
		if (locationName.equals("Barb Village(EV)")) {
			log("Setting Mummyfied paths for Barbarian Village(EV).");
			log("[Reminder]Pls start in the bank or at the fishing spots.");
			toBank = new RSTile[]{
					new RSTile(3102, 3426), new RSTile(3098, 3436), new RSTile(3091, 3448), new RSTile(3087, 3461),
					new RSTile(3080, 3473), new RSTile(3081, 3484), new RSTile(3094, 3492)}; //bank
			toArea = reversePath(toBank);
			usesNPCBanking = true;


			if (catchName.equals("Pike")) {
				currentGear = GEAR_ROD;
				currentBait = BAIT_BAIT;
				fishingSpotID = 328;
				currentCommand = "Bait";
				bankID = 5912;
				return true;
			}

			if (catchName.equals("Trout/Salmon")) {
				currentGear = GEAR_FLYROD;
				currentBait = BAIT_FEATHERS;
				fishingSpotID = 328;
				currentCommand = "Lure";
				bankID = 5912;
				return true;
			}

			if (catchName.equals("Rainbow Fish")) {
				currentGear = GEAR_FLYROD;
				currentBait = BAIT_STRIPY;
				fishingSpotID = 328;
				currentCommand = "Lure";
				bankID = 5912;
				return true;
			}
		}

		// Catherby locations:
		if (locationName.equals("Catherby")) {
			log("Setting Mummyfied paths for Catherby.");
			log("[Reminder]Pls start in the bank or at the fishing spots.");
			toBank = new RSTile[]{new RSTile(2843, 3431), new RSTile(2835, 3435), new RSTile(2824, 3436), new RSTile(2810, 3441)};
			toArea = new RSTile[]{new RSTile(2817, 3438), new RSTile(2829, 3438), new RSTile(2842, 3432)};
			usesNPCBanking = true;


			if (catchName.equals("Bass/Cod/Mackerel")) {
				currentGear = GEAR_BIGNET;
				currentBait = BAIT_NONE;
				fishingSpotID = 322;
				currentCommand = "Net";
				bankID = 495;
				return true;
			}

			if (catchName.equals("Shrimp/Anchovies")) {
				currentGear = GEAR_NET;
				currentBait = BAIT_NONE;
				fishingSpotID = 320;
				currentCommand = "Net";
				bankID = 495;
				return true;
			}

			if (catchName.equals("Herring/Sardines")) {
				currentGear = GEAR_ROD;
				currentBait = BAIT_BAIT;
				fishingSpotID = 320;
				currentCommand = "Bait";
				bankID = 495;
				return true;
			}

			if (catchName.equals("Lobsters")) {
				currentGear = GEAR_CAGE;
				currentBait = BAIT_NONE;
				fishingSpotID = 321;
				currentCommand = "Cage";
				bankID = 495;
				return true;
			}

			if (catchName.equals("Tuna/Swordfish")) {
				currentGear = GEAR_HARPOON;
				currentBait = BAIT_NONE;
				fishingSpotID = 321;
				currentCommand = "Harpoon";
				bankID = 495;
				return true;
			}


			if (catchName.equals("Tuna/Swordfish(CHARPOON")) {
				currentGear = GEAR_CHARPOON;
				currentBait = BAIT_NONE;
				fishingSpotID = 321;
				currentCommand = "Harpoon";
				bankID = 495;
				return true;
			}

			if (catchName.equals("Sharks")) {
				currentGear = GEAR_HARPOON;
				currentBait = BAIT_NONE;
				fishingSpotID = 322;
				currentCommand = "Harpoon";
				bankID = 495;
				return true;
			}


			if (catchName.equals("Sharks(CHARPOON")) {
				currentGear = GEAR_CHARPOON;
				currentBait = BAIT_NONE;
				fishingSpotID = 322;
				currentCommand = "Harpoon";
				bankID = 495;
				return true;
			}

		}


		// Draynor locations.
		if (locationName.equals("Draynor")) {
			// Setup draynor paths.
			log("Setting Mummyfied paths for Draynor.");
			log("[Reminder]Pls start in the bank or at the fishing spots.");
			toBank = new RSTile[]{new RSTile(3086, 3232), new RSTile(3093, 3242)};
			toArea = reversePath(toBank);
			usesNPCBanking = true;

			if (catchName.equals("Shrimp/Anchovies")) {
				currentGear = GEAR_NET;
				currentBait = BAIT_NONE;
				fishingSpotID = 327;
				currentCommand = "Net";
				bankID = 495;
				return true;
			}

			if (catchName.equals("Herring/Sardines")) {
				currentGear = GEAR_ROD;
				currentBait = BAIT_BAIT;
				fishingSpotID = 327;
				currentCommand = "Bait";
				bankID = 495;
				return true;
			}
		}

		if (locationName.equals("Fishing Guild")) {
			log("Setting Mummyfied paths for the Fishing Guild.");
			log("[Reminder]Pls start in the bank or at the fishing spots.");

			toBank = new RSTile[]{new RSTile(2594, 3415), new RSTile(2588, 3420)};
			toArea = new RSTile[]{new RSTile(2597, 3420)};
			usesNPCBanking = true;

			if (catchName.equals("Bass/Cod/Mackerel")) {
				currentGear = GEAR_BIGNET;
				currentBait = BAIT_NONE;
				fishingSpotID = 313;
				currentCommand = "Net";
				bankID = 494;
				return true;
			}
			if (catchName.equals("Lobsters")) {
				currentGear = GEAR_CAGE;
				currentBait = BAIT_NONE;
				fishingSpotID = 312;
				currentCommand = "Cage";
				bankID = 494;
				return true;
			}

			if (catchName.equals("Tuna/Swordfish")) {
				currentGear = GEAR_HARPOON;
				currentBait = BAIT_NONE;
				fishingSpotID = 312;
				currentCommand = "Harpoon";
				bankID = 494;
				return true;
			}

			if (catchName.equals("Tuna/Swordfish(CHARPOON)")) {
				currentGear = GEAR_CHARPOON;
				currentBait = BAIT_NONE;
				fishingSpotID = 312;
				currentCommand = "Harpoon";
				bankID = 494;
				return true;
			}
			if (catchName.equals("Sharks")) {
				currentGear = GEAR_HARPOON;
				currentBait = BAIT_NONE;
				fishingSpotID = 313;
				currentCommand = "Harpoon";
				bankID = 494;
				return true;
			}

			if (catchName.equals("Sharks(CHARPOON)")) {
				currentGear = GEAR_CHARPOON;
				currentBait = BAIT_NONE;
				fishingSpotID = 313;
				currentCommand = "Harpoon";
				bankID = 494;
				return true;
			}
		}

                if (locationName.equals("[STILES]Karamja")) {
			log("Setting Mummyfied paths for Karamja");
			log("[Reminder]Pls start in the bank or at the fishing spots.");
			toBank = new RSTile[]{new RSTile(2916, 3169), new RSTile(2906, 3172), new RSTile(2893, 3169),
                                              new RSTile(2880, 3163), new RSTile(2872, 3154), new RSTile(2862, 3147),
                                              new RSTile(2851, 3142)};


			toArea = reversePath(toBank);
			usesNPCBanking = true;


			if (catchName.equals("Lobsters")) {
				currentGear = GEAR_CAGE;
				currentBait = BAIT_NONE;
				fishingSpotID = 324;
				currentCommand = "Cage";
				bankID = 11267;
				return true;
			}

			if (catchName.equals("Tuna/Swordfish")) {
				currentGear = GEAR_HARPOON;
				currentBait = BAIT_NONE;
				fishingSpotID = 324;
				currentCommand = "Harpoon";
				bankID = 11267;
				return true;
			}

		}


		if (locationName.equals("Karamja")) {
			log("Setting Mummyfied paths for Karamja");
			log("[Reminder]Pls start in the bank or at the fishing spots.");
			toBank = new RSTile[]{new RSTile(2925, 3177), new RSTile(2924, 3166), new RSTile(2929, 3152),
					new RSTile(2942, 3146),

					/*new RSTile(2954, 3146), new RSTile(3032, 3217),*/

					new RSTile(3029, 3217), new RSTile(3027, 3222), new RSTile(3027, 3230),
					new RSTile(3041, 3238), new RSTile(3051, 3246), new RSTile(3068, 3248),
					new RSTile(3080, 3250), new RSTile(3092, 3243)};//PortSarim


			toArea = reversePath(toBank);
			toArea = reversePath(toBank);
			usesNPCBanking = true;

			if (catchName.equals("Shrimp/Anchovies")) {
				currentGear = GEAR_NET;
				currentBait = BAIT_NONE;
				fishingSpotID = 323;
				currentCommand = "Net";
				bankID = 495;
				return true;
			}

			if (catchName.equals("Herring/Sardines")) {
				currentGear = GEAR_ROD;
				currentBait = BAIT_BAIT;
				fishingSpotID = 323;
				currentCommand = "Bait";
				bankID = 495;
				return true;
			}

			if (catchName.equals("Lobsters")) {
				currentGear = GEAR_CAGE;
				currentBait = BAIT_NONE;
				fishingSpotID = 324;
				currentCommand = "Cage";
				bankID = 495;
				return true;
			}

			if (catchName.equals("Tuna/Swordfish")) {
				currentGear = GEAR_HARPOON;
				currentBait = BAIT_NONE;
				fishingSpotID = 324;
				currentCommand = "Harpoon";
				bankID = 495;
				return true;
			}

		}


		if (locationName.equals("Shilo")) {
			log("Setting Mummyfied paths for Shilo.");
			log("[Reminder]Pls start in the bank or at the fishing spots.");
			toBank = new RSTile[]{new RSTile(2864, 2971),
					new RSTile(2850, 2967), new RSTile(2852, 2953)};

			toArea = reversePath(toBank);
			usesNPCBanking = true;

			if (catchName.equals("Trout/Salmon")) {
				currentGear = GEAR_FLYROD;
				currentBait = BAIT_FEATHERS;
				fishingSpotID = 317;
				currentCommand = "Lure";
				bankID = 499;
				return true;
			}

			if (catchName.equals("Pike")) {
				currentGear = GEAR_ROD;
				currentBait = BAIT_BAIT;
				fishingSpotID = 317;
				currentCommand = "Bait";
				bankID = 499;
				return true;
			}
		}

		if (locationName.equals("Piscatoris")) {
			log("Setting Mummyfied paths for Piscatoris");
			log("[Reminder]Pls start in the bank or at the fishing spots.");
			toBank = new RSTile[]{new RSTile(2339, 3697), new RSTile(2322, 3696), new RSTile(2331, 3689)};
			toArea = new RSTile[]{new RSTile(2339, 3697)};
			usesNPCBanking = true;

			if (catchName.equals("Monkfish")) {
				currentGear = GEAR_NET;
				currentBait = BAIT_NONE;
				fishingSpotID = 3848;
				currentCommand = "Net";
				bankID = 3824;
				return true;
			}

			if (catchName.equals("Tuna/Swordfish")) {
				currentGear = GEAR_HARPOON;
				currentBait = BAIT_NONE;
				fishingSpotID = 3848;
				currentCommand = "Harpoon";
				bankID = 3824;
				return true;
			}
			if (catchName.equals("Tuna/Swordfish(BARB)")) {
				currentGear = GEAR_NONE;
				currentBait = BAIT_NONE;
				fishingSpotID = 3848;
				currentCommand = "Harpoon";
				bankID = 3824;
				return true;
			}
			if (catchName.equals("Tuna/Swordfish(CHARPOON)")) {
				currentGear = GEAR_CHARPOON;
				currentBait = BAIT_NONE;
				fishingSpotID = 3848;
				currentCommand = "Harpoon";
				bankID = 3824;
				return true;
			}


		}

		log("Unable to start script: Invalid combination of parameters.");
		return false;
	}


	public void onFinish() {
		// Takes a screen shot when u stop the script.
		ScreenshotUtil.takeScreenshot(true);

		// Remove listeners.
		Bot.getEventManager().removeListener(PaintListener.class, this);
		Bot.getEventManager().removeListener(ServerMessageListener.class, this);
	}


	final ScriptManifest props = getClass().getAnnotation(
			ScriptManifest.class);

	public void checkupdate() {
		double curV = getOVersion();
		if (curV > props.version()) {
			log("Please update your ZombieFisher to v" + curV);
			return;
		} else {
			log("You've got latest ZombieFisher");
		}
		return;
	}

	public void checkupdate2() {
		double curV = getOVersion();
		if (curV > props.version()) {
			log("A new update was just released! Pls refer to the thread for more details.");
			beep(5);
			return;
		}
		return;
	}

	//If the URL doesnt work, try this http://zombiebboi12.webs.com/Version
	public static double getOVersion() {
		try {
			URL url = new URL("http://preview8.awardspace.com/zombiebboi12.co.cc/");
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new BufferedInputStream(url.openConnection().getInputStream())));
			double ver = Double.parseDouble(br.readLine().trim());
			br.close();
			return ver;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void Reset() {
		StartedY = false;
		currentFails = 0;
	}

	public void openURL(final String url) { // Credits to Dave who gave credits
		// to
		// some guy who made this.
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
			} else { // assume Unix or Linux
				final String[] browsers = {"firefox", "opera", "konqueror",
						"epiphany", "mozilla", "netscape"};
				String browser = null;
				for (int count = 0; count < browsers.length && browser == null; count++) {
					if (Runtime.getRuntime().exec(
							new String[]{"which", browsers[count]})
							.waitFor() == 0) {
						browser = browsers[count];
					}
				}
				if (browser == null) {
					throw new Exception("Could not find web browser");
				} else {
					Runtime.getRuntime().exec(new String[]{browser, url});
				}
			}
		} catch (final Exception e) {
		}
	}


	public int loop() {

		randomInt = random(1, 17);
		GambleInt = random(1, 17);
		if (GambleInt == 1) {
			turnCamera();
		}

		if (!isLoggedIn() || isWelcomeButton() || !StartedY) {
			StartedY = true;
			wait(1000);
			return random(250, 500);
		}
		if (barbarianMode && currentGear != GEAR_NONE) {


		}
		if (barbarianMode && currentGear != GEAR_NONE) {


		}

		if (currentFails >= 1000) {
			log("The script failed 100 times, and will now stop as a failsafe.");
			ScreenshotUtil.takeScreenshot(isLoggedIn());
			if (checkForLogout()) stopScript();
		}


		switch (state) {
			case S_WALKTO_BANK:
				return walkToBank();

			case S_THROW_TUNAS:
				return throwTunas();

			case S_WALKTO_SPOT:
				return walkToSpots();

			case S_FISH:
				if (antiTunas)
					return stateTuna();
				else
					return stateFish();

			case S_DROP_ALL:
				return dropAll();

			case S_USE_BANK:
				if (usesNPCBanking)
                                  if (locationName.equals("[STILES]Karamja"))
					return tradeAllKaramja();
				else if (locationName.equals("Piscatoris"))
						return useBankNPCPiscatoris();
					else
						return useBankNPC();
				else
					return useBank();

			case S_DEPOSIT:

				 if (locationName.equals("Karamja"))
					return depositAllKaramja();
				else
					return depositAllRest();

			case S_WITHDRAW:
				stopScript();
				ScreenshotUtil.takeScreenshot(isLoggedIn());
		}

		return random(500, 1000);
	}

	int antiBan() {
		int GambleInt = random(1, 6);
		switch (GambleInt) {
			case 1:
				wait(random(2000, 2500));
				break;
			case 2:
				if (random(1, 4) == 1) {
					int x = random(0, 750);
					int y = random(0, 500);
					moveMouse(0, 0, x, y);

				}
				return random(1300, 1600);
			case 3:
				// Is the current tab the inventory?
				if (getCurrentTab() != TAB_INVENTORY) {
					// No, so switch to the inventory tab.
					openTab(TAB_INVENTORY);
					return random(500, 750);
				} else {
					// No, so return
					return random(500, 750);
				}
			case 4:
				// If the player is moving, then abort.
				if (getMyPlayer().isMoving()) {
					return random(750, 1000);
				}

				if (System.currentTimeMillis() - lastCheck >= checkTime) {
					lastCheck = System.currentTimeMillis();
					checkTime = random(60000, 180000);

					if (getCurrentTab() != Constants.TAB_STATS) {
						openTab(Constants.TAB_STATS);
					}
					moveMouse(700, 280, 50, 28);
					return random(5000, 8000);
				}


			case 5:
				if (random(1, 8) == 2) {
					int angle = getCameraAngle() + random(-90, 90);
					if (angle < 0) {
						angle = 0;
					}
					if (angle > 359) {
						angle = 0;

					}
					setCameraRotation(angle);
				}
				return random(500, 750);
		}
		return random(500, 1000);
	}

	public int useShopNPCKaramja() {
		RSNPC Shopkeeper = getNearestNPCByID(shopID);

		if (getMyPlayer().isMoving())
			return random(250, 500);

		if (runningFromCombat) {
			state = S_WALKTO_SPOT;
			return random(500, 750);
		}

		if (RSInterface.getInterface(620).isValid()) {
			state = S_DEPOSIT;
			return 500;
		}

		if (Shopkeeper != null) {
			if (atNPC(Shopkeeper, "trade")) {
				log("Accessed shop.");
				state = S_DEPOSIT;
				currentFails = 0;
				return random(500, 1000);
			} else {
				log("Misclicked shopkeeper, trying again.");
				currentFails++;
				return random(500, 750);
			}
		}
		return random(500, 1000);
	}

	public int useBankNPCPiscatoris() {
		RSNPC banker = getNearestNPCByID(bankID);

		if (getMyPlayer().isMoving())
			return random(250, 500);

		if (runningFromCombat) {
			state = S_WALKTO_SPOT;
			return random(500, 750);
		}

		if (RSInterface.getInterface(INTERFACE_BANK).isValid()) {
			state = S_DEPOSIT;
			return 500;
		}

		if (banker != null) {
			if (atNPC(banker, "bank")) {
				log("Accessed bank account.");
				state = S_DEPOSIT;
				currentFails = 0;
				return random(500, 1000);
			} else {
				log("Misclicked bank, trying again.");
				currentFails++;
				return random(500, 750);
			}
		} else {
			log("Unable to find bankbooth");
			currentFails++;
			return random(500, 750);
		}

	}

	public int useBankNPC() {
		RSNPC banker = getNearestNPCByID(bankID);

		if (getMyPlayer().isMoving())
			return random(250, 500);

		if (runningFromCombat) {
			state = S_WALKTO_SPOT;
			return random(500, 750);
		}

		if (RSInterface.getInterface(INTERFACE_BANK).isValid()) {
			state = S_DEPOSIT;
			return 500;
		}

		if (banker != null) {
			if (atNPC(banker, "bank banker")) {
				log("Accessed bank account.");
				state = S_DEPOSIT;
				currentFails = 0;
				return random(500, 1000);
			} else {
				log("Misclicked bank, trying again.");
				currentFails++;
				return random(500, 750);
			}
		} else {
			log("Unable to find bankbooth");
			currentFails++;
			return random(500, 750);
		}

	}

	@SuppressWarnings("deprecation")
	public int useBank() {
		RSObject bankBooth = findObject(bankID);

		if (getMyPlayer().isMoving())
			return random(250, 500);

		if (runningFromCombat) {
			state = S_WALKTO_SPOT;
			return random(500, 750);
		}

		if (RSInterface.getInterface(INTERFACE_BANK).isValid()) {
			state = S_DEPOSIT;
			return 500;
		}

		if (bankBooth != null) {
			turnToObject(bankBooth, 40);
			if (atObject(bankBooth, "Use-quickly")) {
				log("Accessed bank account.");
				state = S_DEPOSIT;
				currentFails = 0;
				return random(500, 1000);
			} else {
				setCameraRotation(getCameraAngle() + random(-90, 90));
				log("Misclicked bank, trying again.");
				currentFails++;
				return random(500, 750);
			}
		} else {
			log("Unable to find bankbooth");
			currentFails++;
			return random(500, 750);
		}
	}

	public int walkToSpots() {
		if (takeBoatToKaramja())
			return random(2000, 2500);

		if (randomRunEnergy <= getEnergy() && !isRunning()) {
			setRun(true);
			randomRunEnergy = random(20, 60);
			return random(750, 1000);
		}

		if (distanceTo(toArea[toArea.length - 1]) <= 3) {
			log("Arriving at fishing spots, continuing to fish.");
			state = S_FISH;
			currentFails = 0;
			return random(250, 750);
		}

		if (!getMyPlayer().isMoving())
			walkPathMM(randomizePath(toArea, 2, 2), 20);

		return random(50, 150);
	}

	public int walkToBank() {
		if (takeBoatFromKaramja())
			return random(2000, 2500);

		// TODO: bug fix.
		/*if ( distanceTo(new RSTile(2953, 3147)) <= 4 )
					return random(250, 500);*/

		if (randomRunEnergy <= getEnergy() && !isRunning()) {
			setRun(true);
			randomRunEnergy = random(20, 60);
			return random(750, 1000);
		}

		if (runningFromCombat && !getMyPlayer().isInCombat()) {
			log("No longer in combat, heading back to fishing spots.");
			state = S_WALKTO_SPOT;
			runningFromCombat = false;
			return random(250, 750);
		}

		if (distanceTo(toBank[toBank.length - 1]) <= 4) {

			log("Arriving at bank, Accessing account.");
			state = S_USE_BANK;
			return random(750, 1500);
		}

		if (!getMyPlayer().isMoving() || (getDestination() != null && distanceTo(getDestination()) < 3))
			walkPathMM(randomizePath(toBank, 2, 2), 20);

		return random(50, 150);
	}

	public int dropAll() {
		// Make an array of items to keep.
		int[] thingsToKeep = new int[]{currentGear, currentBait, 995};

		// Drop all, twice to make sure nothing is missed.
		dropAllExcept(thingsToKeep);
		dropAllExcept(thingsToKeep);
		dropAllExcept(thingsToKeep);
		dropAllExcept(thingsToKeep);

		state = S_FISH;

		return random(500, 750);
	}

	public int throwTunas() {
		// Make an array of items to keep.
		int[] thingsToKeep = new int[]{currentGear, currentBait, 995, 331, 335, 317, 321, 377, 371, 383, 14664, 7944, 363, 341, 353, 327, 345, 349};

		// Drop all, twice to make sure nothing is missed.
		dropAllExcept(thingsToKeep);
		dropAllExcept(thingsToKeep);
		dropAllExcept(thingsToKeep);
		dropAllExcept(thingsToKeep);

		state = S_FISH;

		return random(500, 750);
	}

	public int tradeAllKaramja() {
               RSNPC stiles = getNearestNPCByID(11267);
               RSNPC fishingSpot = getNearestNPCByID(fishingSpotID);

		if (getMyPlayer().isMoving())
			return random(250, 500);

		if (runningFromCombat) {
			state = S_WALKTO_SPOT;
			return random(500, 750);
		}


		if (stiles != null) {
			if (atNPC(stiles, "exchange")) {
                                wait(random(500, 2500));
				log("Clicked Stiles.");
				state = S_WALKTO_SPOT;

                              if (distanceTo(toArea[toArea.length - 1]) <= 3) {
			log("Arriving at fishing spots, continuing to fish.");
			state = S_FISH;
			currentFails = 0;
			return random(250, 750);
		}
				currentFails = 0;
				return random(500, 1000);
			} else {
				log("Misclicked stiles, trying again.");
				currentFails++;
				return random(500, 750);
			}
		} else {
			log("Unable to find stiles");
			currentFails++;
			return random(500, 750);
		}

	}


	public int depositAllKaramja() {

		// Make an array of items to keep.
		int[] thingsToKeep = new int[]{currentGear, currentBait, 995};

		if (!RSInterface.getInterface(INTERFACE_BANK).isValid()) {
			state = S_USE_BANK;
			return 500;
		}
		// Deposit all.
		bank.depositAllExcept(thingsToKeep);

		// Only switch states if thet deposit was successfull.
		if (!isInventoryFull())
			state = S_WALKTO_SPOT;
		else
			state = S_USE_BANK;

		return random(500, 750);
	}

	public int depositAllRest() {

		// Make an array of items to keep.
		int[] thingsToKeep = new int[]{currentGear, currentBait};

		if (!RSInterface.getInterface(INTERFACE_BANK).isValid()) {
			state = S_USE_BANK;
			return 500;
		}
		// Deposit all.
		bank.depositAllExcept(thingsToKeep);

		// Only switch states if thet deposit was successfull.
		if (!isInventoryFull())
			state = S_WALKTO_SPOT;
		else
			state = S_USE_BANK;

		return random(500, 750);
	}

	public int stateFish() {
		// Find fishing spot.
		RSNPC fishingSpot = getNearestNPCByID(fishingSpotID);
		//
		if (currentBait != BAIT_NONE && getInventoryCount(currentBait) == 0) {
			log("No bait for current mode.");
			log("Please buy your feathers for the lowest price in the Grand Exchange.");
			log("I thank you for doing that, the current price for feathers is too high.");
			log("So, please help us out in the quest of bringing it down.");

			currentFails += 5;
			return random(250, 500);
		}

		//
		if (checkAndRecoverGear())
			return random(1000, 1500);

		if (currentGear != GEAR_NONE && getInventoryCount(currentGear) == 0 && !barbarianMode) {
			log("No gear for the current mode.");
			currentFails += 5;
			return random(250, 500);
		}

		// Is the player currently in combat?
		if (getMyPlayer().isInCombat()) {
			log("Running from combat.");
			runningFromCombat = true;
			state = S_WALKTO_BANK;
			timesAvoidedCombat++;
			return random(250, 500);
		}

		// Is the player's inventory full?
		if (isInventoryFull()) {
			openTab(TAB_INVENTORY);
			log("The inventory is full, " + (powerFishing == true ? "dropping all catches." : "heading to the bank."));
			state = (powerFishing == true) ? S_DROP_ALL : S_WALKTO_BANK;
			return random(250, 500);

		}


		// Is the player current busy? If so, do antiban.
		if (getMyPlayer().getAnimation() != -1 && !checkAndAvoidWhirlpools()
				|| getMyPlayer().isMoving())
			return antiBan();


		if (fishingSpot == null) {
			if (checkLostAndRecover())
				return random(500, 750);
			if (distanceTo(toBank[toBank.length - 1]) <= 3) {
				state = S_WALKTO_SPOT;
				return random(750, 1500);
			}

			currentFails++;
			log("Unable to find fishing spot.");
			return random(250, 500);
		} else {
			if (tileOnScreen(fishingSpot.getLocation())) {
				if (!atNPC(fishingSpot, currentCommand)) setCameraRotation(random(1, 359));
				currentFails = 0;
				return random(2150, 2350);
			} else {
				RSTile destination = randomizeTile(fishingSpot.getLocation(), 2, 2);
				walkTileMM(destination);
				return random(500, 1000);
			}
		}
	}

	public int stateTuna() {
// Find fishing spot.
		RSNPC fishingSpot = getNearestNPCByID(fishingSpotID);
		//
		if (currentBait != BAIT_NONE && getInventoryCount(currentBait) == 0) {
			log("No bait for current mode.");
			log("Please buy your feathers for the lowest price in the Grand Exchange.");
			log("I thank you for doing that, the current price for feathers is too high.");
			log("So, please help us out in the quest of bringing it down.");

			currentFails += 5;
			return random(250, 500);
		}

		//
		if (checkAndRecoverGear())
			return random(1000, 1500);

		if (currentGear != GEAR_NONE && getInventoryCount(currentGear) == 0 && !barbarianMode) {
			log("No gear for the current mode.");
			currentFails += 5;
			return random(250, 500);
		}

		// Is the player currently in combat?
		if (getMyPlayer().isInCombat()) {
			log("Running from combat.");
			runningFromCombat = true;
			state = S_WALKTO_BANK;
			timesAvoidedCombat++;
			return random(250, 500);
		}

		// Is the player's inventory full?

		// Is the player's inventory full?
		if (!isInventoryFull()) {
			state = S_THROW_TUNAS;
		} else {
			state = S_WALKTO_BANK;
			log("The inventory is full, dropping all Tunas.");
			return random(250, 500);
		}


		// Is the player current busy? If so, do antiban.
		if (getMyPlayer().getAnimation() != -1 && !checkAndAvoidWhirlpools()
				|| getMyPlayer().isMoving())
			return antiBan();


		if (fishingSpot == null) {
			if (checkLostAndRecover())
				return random(500, 750);
			if (distanceTo(toBank[toBank.length - 1]) <= 3) {
				state = S_WALKTO_SPOT;
				return random(750, 1500);
			}

			currentFails++;
			log("Unable to find fishing spot.");
			return random(250, 500);
		} else {
			if (tileOnScreen(fishingSpot.getLocation())) {
				if (!atNPC(fishingSpot, currentCommand)) setCameraRotation(random(1, 359));
				currentFails = 0;
				return random(1500, 1700);
			} else {
				RSTile destination = randomizeTile(fishingSpot.getLocation(), 2, 2);
				walkTileMM(destination);
				return random(500, 1000);
			}
		}
	}


	@SuppressWarnings("deprecation")
	public boolean hasEquipped(int GEAR_BARB) {
		int[] equipItems = RSInterface.getInterface(387).getChild(29)
				.getInventory();
		for (int j = 0; j < itemIDs.length; j++) {
			for (int i = 0; i < equipItems.length; i++)
				if (i == j)
					return true;
		}
		return false;
	}


	public boolean checkAndAvoidWhirlpools() {
		RSTile playerLocation = getMyPlayer().getLocation();
		RSTile tileToTest1 = new RSTile(playerLocation.getX() + 1, playerLocation.getY());
		RSTile tileToTest2 = new RSTile(playerLocation.getX() - 1, playerLocation.getY());
		RSTile tileToTest3 = new RSTile(playerLocation.getX(), playerLocation.getY() + 1);
		RSTile tileToTest4 = new RSTile(playerLocation.getX(), playerLocation.getY() - 1);

		for (int id : whirlpools) {
			RSNPC whirlpool = getNearestNPCByID(id);

			if (whirlpool == null) continue;

			if (whirlpool.getLocation().equals(tileToTest1) || whirlpool.getLocation().equals(tileToTest2) ||
					whirlpool.getLocation().equals(tileToTest3) || whirlpool.getLocation().equals(tileToTest4)) {
				whirlpoolsAvoided++;
				log("Found whirlpool");
				return true;
			}
		}

		return false;
	}


	public boolean checkLostAndRecover() {
		int index = 0;
		for (RSTile lost : lostTiles) {
			if (distanceTo(lost) <= 2) {
				RSTile destination = randomizeTile(recoverTiles[index], 2, 2);
				log("Lost at " + lost.getX() + ", " + lost.getY() + ", recovering.");
				walkTileMM(destination);
				return true;
			}

			index++;
		}

		return false;

	}


	public boolean checkAndRecoverGear() {
		RSItemTile itemTile = getGroundItemByID(currentGear);

		if (barbarianMode) return false;

		if (getInventoryCount(currentGear) == 0 && itemTile != null) {
			if (tileOnScreen(itemTile)) {
				timesRecoveredGear++;
				log("Recovered gear.");
				atTile(itemTile, "Take");
				return true;
			} else {
				log("Walking to gear location.");
				walkTileMM(itemTile);
				return true;
			}
		} else {
			return false;
		}
	}


	@SuppressWarnings("deprecation")
	public boolean takeBoatFromKaramja() {
		RSNPC customsOfficer = getNearestNPCByID(380);
		@SuppressWarnings("unused")
		RSObject plank = findObject(242);
		RSTile location = new RSTile(3031, 3217);


		if (!locationName.equals("Karamja"))
			return false;

		if (getInventoryCount(995) < 30) {
			log("Not enough GP for a boat ride.");
		}

		if (distanceTo(location) <= 20 && !getMyPlayer().getLocation().equals(new RSTile(3029, 3217))) {
			if (tileOnScreen(location)) {
				atTile(location, "Cross");
				log("Arriving at Port Sarim.");
				return true;
			}


		}

		if (RSInterface.getInterface(228).isValid()) {
			atInterface(RSInterface.getInterface(228).getChild(2));
			return true;
		}
		if (RSInterface.getInterface(242).isValid()) {
			atInterface(RSInterface.getInterface(242).getChild(6));
			return true;
		}

		if (RSInterface.getInterface(230).isValid()) {
			atInterface(RSInterface.getInterface(230).getChild(3));
			return true;
		}

		if (RSInterface.getInterface(241).isValid()) {
			atInterface(RSInterface.getInterface(241).getChild(5));
			return true;
		}

		if (RSInterface.getInterface(64).isValid()) {
			atInterface(RSInterface.getInterface(64).getChild(5));
			return true;
		}

		if (RSInterface.getInterface(228).isValid()) {
			atInterface(RSInterface.getInterface(228).getChild(2));
			return false;
		}

		if (RSInterface.getInterface(241).isValid()) {
			atInterface(RSInterface.getInterface(241).getChild(5));
			return false;
		}

		if (customsOfficer != null) {
			if (tileOnScreen(customsOfficer.getLocation())) {
				atNPC(customsOfficer, "Pay-Fare");
				return true;
			} else {
				walkTileMM(randomizeTile(customsOfficer.getLocation(), 2, 2));
				return true;
			}

		}

		return false;
	}

	@SuppressWarnings("deprecation")
	public boolean takeBoatToKaramja() {
		int[] seamanIDs = new int[]{376, 377, 378}; // Pay-fare
		RSNPC seaman = getNearestNPCByID(seamanIDs);
		RSObject plank = findObject(2082);

		if (!locationName.equals("Karamja"))
			return false;

		if (getInventoryCount(995) < 30) {
			log("Not enough GP for a boat ride.");
		}

		if (plank != null) {
			log("Arriving at Karamja.");
			atObject(plank, "Cross");
			return true;
		}

		if (RSInterface.getInterface(64).isValid()) {
			atInterface(RSInterface.getInterface(64).getChild(5));
			return true;
		}

		if (RSInterface.getInterface(228).isValid()) {
			atInterface(RSInterface.getInterface(228).getChild(2));
			return true;
		}

		if (RSInterface.getInterface(241).isValid()) {
			atInterface(RSInterface.getInterface(241).getChild(5));
			return true;
		}
		if (seaman != null) {
			if (tileOnScreen(seaman.getLocation())) {
				atNPC(seaman, "Pay-fare");
				return true;
			} else {
				walkTileMM(randomizeTile(seaman.getLocation(), 2, 2));
				return true;
			}
		}

		return false;
	}

	public boolean checkForLogout() {
		for (int failed = 0; failed < 3; failed++) {
			logout();
			beep(3);
			wait(500);
			if (!isLoggedIn()) {
				return true;
			}
		}
		return false;
	}


	public void onRepaint(Graphics g) {

		long runTime = 0;
		long seconds = 0;
		long minutes = 0;
		long hours = 0;
		int index = Skills.getStatIndex("Fishing");
		@SuppressWarnings("unused")
		long untilhour = 0, untilmin = 0, untilsec = 0;
		int exp = 0;
		int expGained = 0;
		int levelsGained;

		//
		if (lastExp == 0)
			lastExp = skills.getCurrentSkillExp(STAT_FISHING);

		if (skills.getCurrentSkillExp(STAT_FISHING) > lastExp) {
			lastExp = skills.getCurrentSkillExp(STAT_FISHING);
			numberOfCatches++;
		}

		if (countToNext == 0) {
			untilsec = 0;
			untilhour = 0;
			untilmin = 0;
		}

		// Return if paint is disabled.
		if (!usePaint) return;

		//
		if (playerStartXP == 0)
			playerStartXP = skills.getCurrentSkillExp(STAT_FISHING);

		if (startLevel == 0)
			startLevel = skills.getCurrentSkillLevel(STAT_FISHING);

		// Calculate current runtime.
		runTime = System.currentTimeMillis() - scriptStartTime;
		seconds = runTime / 1000;
		if (seconds >= 60) {
			minutes = seconds / 60;
			seconds -= (minutes * 60);
		}
		if (minutes >= 60) {
			hours = minutes / 60;
			minutes -= (hours * 60);
		}
		exp = skills.getCurrentSkillExp(index) - startExp;
		if (exp > oldExp) {
			xpPerCatch = exp - oldExp;
			oldExp = exp;
			catches++;
			countToNext = skills.getXPToNextLevel(STAT_FISHING) / xpPerCatch
					+ 1;
		}


		// Calculate experience gained.
		expGained = skills.getCurrentSkillExp(STAT_FISHING) - playerStartXP;
		if (pColor.equals("SunKist")) {
			g.setColor(new Color(253, 196, 0, 100));
		}
		if (pColor.equals("PinkPanther")) {
			g.setColor(new Color(255, 100, 255, 53));
		}
		if (pColor.equals("ClearSky")) {
			g.setColor(new Color(60, 155, 159, 50));
		}
		if (pColor.equals("Monochrome")) {
			g.setColor(new Color(0, 0, 0, 175));
		}
		if (pColor.equals("BloodShed")) {
			g.setColor(new Color(255, 0, 0, 50));
		}
		if (pColor.equals("Nightmare")) {
			g.setColor(new Color(0, 0, 0, 175));
		}

		int[][] paint = new int[][]{new int[]{136, 152, 168, 184, 200, 216, 232, 248, 264, 280, 296, 312, 328}, new int[]{152, 186}};
		if (barbarianMode) {
			paint[1][0] -= 16;
			paint[1][1] += 16;
		}
		if (powerFishing) {
			paint[1][0] -= 16;
			paint[1][1] += 16;
		}
		g.fillRoundRect(4, paint[1][0], 200, paint[1][1], 45, 45);

		// Calculate levels gained
		levelsGained = skills.getCurrentSkillLevel(STAT_FISHING) - startLevel;
		if (pColor.equals("SunKist")) {
			g.setColor(Color.WHITE);
		}
		if (pColor.equals("PinkPanther")) {
			g.setColor(Color.BLACK);
		}
		if (pColor.equals("ClearSky")) {
			g.setColor(Color.BLACK);
		}
		if (pColor.equals("Monochrome")) {
			g.setColor(Color.WHITE);
		}
		if (pColor.equals("BloodShed")) {
			g.setColor(Color.BLACK);
		}
		if (pColor.equals("Nightmare")) {
			g.setColor(Color.GREEN);
		}
		final ScriptManifest props = getClass().getAnnotation(
				ScriptManifest.class);
		if (barbarianMode || powerFishing) {
			if (powerFishing && !barbarianMode) {
				g.drawString(props.name() + " v" + props.version(), 12, paint[0][1]);
				g.drawString("Fishing location: " + locationName, 12, paint[0][2]);
				g.drawString("Fishing for: " + catchName, 12, paint[0][3]);
				g.drawString("Powerfishing Mode Active", 12, paint[0][4]);
				g.drawString("Run time: " + hours + ":" + minutes + ":" + seconds, 12, paint[0][5]);
				g.drawString("Catches: " + numberOfCatches, 12, paint[0][6]);
				g.drawString("Catches to next level: " + countToNext, 12, paint[0][7]);
				g.drawString("XP Gained: " + expGained, 12, paint[0][8]);
				g.drawString("Levels Gained: " + levelsGained, 12, paint[0][9]);
				g.drawString("Percent to next level: " + skills.getPercentToNextLevel(STAT_FISHING), 12, paint[0][10]);
				g.drawString("Times Avoided Combat: " + timesAvoidedCombat, 12, paint[0][11]);

			}
			if (!powerFishing && barbarianMode) {
				g.drawString(props.name() + " v" + props.version(), 12, paint[0][1]);
				g.drawString("Fishing location: " + locationName, 12, paint[0][2]);
				g.drawString("Fishing for: " + catchName, 12, paint[0][3]);
				g.drawString("Barbarian Mode Active", 12, paint[0][4]);
				g.drawString("Run time: " + hours + ":" + minutes + ":" + seconds, 12, paint[0][5]);
				g.drawString("Catches: " + numberOfCatches, 12, paint[0][6]);
				g.drawString("Catches to next level: " + countToNext, 12, paint[0][7]);
				g.drawString("XP Gained: " + expGained, 12, paint[0][8]);
				g.drawString("Levels Gained: " + levelsGained, 12, paint[0][9]);
				g.drawString("Percent to next level: " + skills.getPercentToNextLevel(STAT_FISHING), 12, paint[0][10]);
				g.drawString("Times Avoided Combat: " + timesAvoidedCombat, 12, paint[0][11]);

			}
			if (powerFishing && barbarianMode) {
				g.drawString(props.name() + " v" + props.version(), 12, paint[0][0]);
				g.drawString("Fishing location: " + locationName, 12, paint[0][1]);
				g.drawString("Fishing for: " + catchName, 12, paint[0][2]);
				g.drawString("Powerfishing Mode Active", 12, paint[0][3]);
				g.drawString("Barbarian Mode Active", 12, paint[0][4]);
				g.drawString("Run time: " + hours + ":" + minutes + ":" + seconds, 12, paint[0][5]);
				g.drawString("Catches: " + numberOfCatches, 12, paint[0][6]);
				g.drawString("Catches to next level: " + countToNext, 12, paint[0][7]);
				g.drawString("XP Gained: " + expGained, 12, paint[0][8]);
				g.drawString("Levels Gained: " + levelsGained, 12, paint[0][9]);
				g.drawString("Percent to next level: " + skills.getPercentToNextLevel(STAT_FISHING), 12, paint[0][10]);
				g.drawString("Times Avoided Combat: " + timesAvoidedCombat, 12, paint[0][11]);

			}
		} else {
			g.drawString(props.name() + " v" + props.version(), 12, paint[0][2]);
			g.drawString("Fishing location: " + locationName, 12, paint[0][3]);
			g.drawString("Fishing for: " + catchName, 12, paint[0][4]);
			g.drawString("Run time: " + hours + ":" + minutes + ":" + seconds, 12, paint[0][5]);
			g.drawString("Catches: " + numberOfCatches, 12, paint[0][6]);
			g.drawString("Catches to next level: " + countToNext, 12, paint[0][7]);
			g.drawString("XP Gained: " + expGained, 12, paint[0][8]);
			g.drawString("Levels Gained: " + levelsGained, 12, paint[0][9]);
			g.drawString("Percent to next level: " + skills.getPercentToNextLevel(STAT_FISHING), 12, paint[0][10]);
			g.drawString("Times Avoided Combat: " + timesAvoidedCombat, 12, paint[0][11]);

		}
	}


	public boolean clickcontinue() {
		if (getContinueChildInterface() != null) {
			if (getContinueChildInterface().getText().contains("to continue")) {
				return atInterface(getContinueChildInterface());
			}
		}
		return false;
	}

	public void beep(int count) {
		if (!Sound) return;
		for (int i = 0; i < count; i++) {
			java.awt.Toolkit.getDefaultToolkit().beep();
			wait(250);
		}
		wait(random(100, 500));
		return;
	}


	public boolean isWelcomeButton() {
		RSInterface welcomeInterface = RSInterface.getInterface(378);
		if (welcomeInterface.getChild(45).getAbsoluteX() > 20 || (!welcomeInterface.getChild(117).getText().equals("10.1120.190") && !welcomeInterface.getChild(117).getText().equals(""))) {
			log("We still are in Welcome Screen");
			return true;
		} else {
			return false;
		}
	}


	public void serverMessageRecieved(ServerMessageEvent arg0) {
		String serverString = arg0.getMessage();
		if (serverString.contains("You've just advanced")) {
			log("Another lvl by ZombieFisher");
			beep(1);
			clickcontinue();
		}
		if (serverString.contains("Oh dear")) {
			log("Oh crap u were killed");
			beep(5);
		}
	}
}