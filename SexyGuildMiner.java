import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

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
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSInterfaceComponent;
import org.rsbot.script.wrappers.RSItem;
import org.rsbot.script.wrappers.RSItemTile;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSPlayer;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(name = "Sexy Miner", authors = { "Imasexybeast", "Fusion89k",
		"Asamin", "Fred" }, category = "Mining", version = 1.42, description = "<html><body><b>All settings are on the GUI =)</b><br><img src=\"http://i126.photobucket.com/albums/p98/ketekcomp/sgm.png\" /></body></html>")
public class SexyGuildMiner extends Script implements PaintListener,
		ServerMessageListener {

	public class getupdate extends Thread {
		public void run() {
			URLConnection url = null;
			BufferedReader in2 = null;
			BufferedWriter out = null;
			try {
				url = new URL(
						"http://pzycho.apex-hosting.com.au/sexyguildminer/scripts/SGMVERSION.txt")
						.openConnection();
				in2 = new BufferedReader(new InputStreamReader(url
						.getInputStream()));
				if (Double.parseDouble(in2.readLine()) > Double
						.parseDouble(getVersion())) {
					if (JOptionPane.showConfirmDialog(null,
							"Update found. Do you want to update?") == 0) {
						JOptionPane
								.showMessageDialog(null,
										"Please choose 'ScriptName.java' in your scripts folder and hit 'Open'");
						JFileChooser fc = new JFileChooser();
						if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
							url = new URL(
									"http://pzycho.apex-hosting.com.au/sexyguildminer/scripts/SexyGuildMiner.java")
									.openConnection();
							in2 = new BufferedReader(new InputStreamReader(url
									.getInputStream()));
							out = new BufferedWriter(new FileWriter(fc
									.getSelectedFile().getPath()));
							String inp;
							while ((inp = in2.readLine()) != null) {
								out.write(inp);
								out.newLine();
								out.flush();
							}
							log("Script successfully downloaded. Please recompile and reload your scripts!");
						} else
							log("Update canceled");
					} else
						log("Update canceled");
				}
				if (in2 != null)
					in2.close();
				if (out != null)
					out.close();
			} catch (IOException e) {
				log("Problem getting version :/");
			}
		}
	}

	public class antiban extends Thread {
		public void CameraAlt() {
			try {
				int key = (random(0, 2) == 0) ? KeyEvent.VK_UP
						: KeyEvent.VK_DOWN;
				Bot.getInputManager().pressKey((char) key);
				Thread.sleep(random(1000, 1500));
				Bot.getInputManager().releaseKey((char) key);
			} catch (final InterruptedException e) {
			}
		}

		public void CameraRot() {
			setCameraRotation((int) (getCameraAngle() + (Math.random() * 50 > 25 ? 1
					: -1)
					* (30 + Math.random() * 90)));
		}

		public void mouseoverMM() {
			moveMouse(628 + random(-30, 30), 89 + random(-30, 30));
		}

		public void mouseOverMiningSkill() {
			try {
				openTab(Constants.TAB_STATS);
				// 2 interfaces, one for the pickaxe image, other for the skill
				// text
				final RSInterfaceChild one = RSInterface.getInterface(320)
						.getChild(6);
				final RSInterfaceChild two = RSInterface.getInterface(320)
						.getChild(7);
				if (random(0, 2) == 0) {
					moveMouse(new Point(one.getAbsoluteX()
							+ random(2, one.getWidth() - 1), one.getAbsoluteY()
							+ random(2, one.getHeight() - 1)));
				} else {
					moveMouse(new Point(two.getAbsoluteX()
							+ random(2, two.getWidth() - 1), two.getAbsoluteY()
							+ random(2, two.getHeight() - 1)));
				}
				Thread.sleep(random(750, 1000));
			} catch (final InterruptedException e) {
			}
		}

		public void checkFriendsList() {
			openTab(Constants.TAB_FRIENDS);
			if (random(0, 5) == 5) {
				try {
					moveMouse(random(573, 620), random(439, 457));
					clickMouse(true);
					Thread.sleep(random(750, 1000));
					sendText("", true);
				} catch (final InterruptedException e) {
				}
			} else {
				try {
					moveMouse(random(552, 569), random(227, 420));
					Thread.sleep(random(750, 1000));
					Point mousel2 = getMouseLocation();
					moveMouse(random(584, 624), mousel2.y + random(-3, 3));
				} catch (final InterruptedException e) {
				}
			}
		}

		@Override
		public void run() {
			int chance = 0;
			chance = random(1, 5);
			if (chance >= 3) {
				return;
			}
			if ((System.currentTimeMillis() - antibannext) >= (random(25, 90) * 1000)) {
				if (getMyPlayer().getLocation().getY() < 6000) {
					aboveground();
				} else {
					underground();
				}
				if (nothing == 1) {
					nothing = 0;
				} else {
					antibannext = System.currentTimeMillis();
				}
				// log("Antiban performed =)");
			}
		}

		public void aboveground() {
			int gamble = 0;
			if (getMyPlayer().isMoving()) {
				gamble = random(0, 14);
				if (gamble > 12) {
					checkFriendsList();
					// log("Friends Tab Opened Above");
				} else if (gamble > 7) {
					mouseOverMiningSkill();
					// log("Stats Tab Opened Above");
				} else if (gamble > 4) {
					mouseoverMM();
					// log("Moved Mouse over MM Below");
				} else if (gamble > 0) {
					moveMouseRandomly(150);
					// log("Moved Mouse Randomly Above");
				} else if (gamble == 0) {
					CameraRot();
					// log("Camera Rotated Above");
				}
			}
		}

		public void underground() {
			int gamble = 0;
			if (!mineNewRock() && getMyPlayer().getAnimation() == 624) {
				gamble = random(0, 5);
				if (gamble > 1) {
					if (System.currentTimeMillis() - antibannext2 >= 10000) {
						moveMouseRandomly(12);
						// log("Moved Mouse Slightly Below");
						antibannext2 = System.currentTimeMillis();
						nothing = 1;
					}
				}
				return;
			}
			if (!mineNewRock()) {
				gamble = random(0, 9);
				if (gamble > 7) {
					CameraRot();
					// log("Camera Rotated Below");
				} else if (gamble > 3) {
					mouseoverMM();
					// log("Moved Mouse over MM Below");
				} else if (gamble >= 0) {
					moveMouseRandomly(150);
					// log("Moved Mouse Randomly Below");
				}
				return;
			}
			nothing = 1;
		}
	}

	int[] bankBoothID = { 11758 };
	int[] bankerID = { 6200 };

	RSTile bankTile = new RSTile(3015, 3354);
	RSTile randTile = new RSTile(0001, 0001);
	RSTile nullTile = new RSTile(0000, 0000);
	RSTile buffTile = new RSTile(0002, 0002);
	RSTile doorfront = new RSTile(3009, 3344);
	RSTile doorside = new RSTile(3009, 3346);
	RSTile betweenavg = new RSTile(3022, 3351);
	RSTile[] betweenBankAndLadderTiles = { new RSTile(3024, 3351),
			new RSTile(3024, 3350), new RSTile(3024, 3349) };
	int[] brokenPickaxeID = { 468, 470, 472, 474, 476, 478 };
	int coalBanked = 0;
	int fs = 0;
	int startfs = 0;
	int rest = 0, rest2 = 0;
	int Color = 1;
	int superheat = 0;
	int xsuperheat = 0;
	int xfirer = 0;
	int MithrilBarBanked = 0;
	int highalch = 0;
	int delay1 = 0;
	int delay2 = 0;
	int clanchatfail = 1;
	int nothing = 0;
	long delaytimetonext = System.currentTimeMillis();
	long antibannext = System.currentTimeMillis();
	long antibannext2 = System.currentTimeMillis();
	long depositdelay = System.currentTimeMillis();
	long bankopendelay = System.currentTimeMillis();
	long chatresponddelay = System.currentTimeMillis();
	private gui gui;
	public boolean startScript = false;
	Thread GU = new getupdate();
	Thread Antiban = new antiban();

	int coalDropped = 0;
	int coalID = 453;
	int WHnb = 0;
	int[] coalRockID = { 31068, 31069, 31070 };
	RSTile[] coalToLadderPath = { new RSTile(3046, 9752),
			new RSTile(3044, 9745), new RSTile(3044, 9738),
			new RSTile(3037, 9737), new RSTile(3030, 9737),
			new RSTile(3020, 9739) };
	long downLadderTimer = System.currentTimeMillis();
	boolean dropping = false;
	int energyToRunAt = random(40, 90);
	int energyToRestAt = random(15, 25);
	int[] gemID = { 1617, 1619, 1621, 1623 };
	int gemsBanked = 0;

	int gemsDropped = 0;

	int goldPiecesID = 995;
	int headAttempts;
	int height = 1;

	int invCountToDrop = random(10, 29);

	int[] junkItems = { 1917, 1971 }; // beer, kebab

	int[] ladderID = { 30941 };

	RSTile ladderTile = new RSTile(3021, 3340);
	// RSTile[] topLadderTiles = { new RSTile(3022, 3341), new RSTile(3022,
	// 3337) };
	// RSTile[] bottomLadderTiles = { new RSTile(3021, 9741), new RSTile(3022,
	// 9740) };
	RSTile[] ladderToCoalPath = { new RSTile(3020, 9739),
			new RSTile(3030, 9737), new RSTile(3037, 9737),
			new RSTile(3044, 9738), new RSTile(3044, 9745),
			new RSTile(3046, 9752) };
	RSTile lastRockLocation = new RSTile(0000, 0000);
	RSTile lumbridgeTile = new RSTile(3221, 3218);
	RSTile[] lumbridgeToFalidorPath = { new RSTile(3226, 3218),
			new RSTile(3233, 3220), new RSTile(3233, 3225),
			new RSTile(3231, 3230), new RSTile(3228, 3233),
			new RSTile(3223, 3238), new RSTile(3221, 3242),
			new RSTile(3219, 3247), new RSTile(3218, 3251),
			new RSTile(3217, 3256), new RSTile(3217, 3257),
			new RSTile(3217, 3261), new RSTile(3217, 3266),
			new RSTile(3216, 3272), new RSTile(3216, 3277),
			new RSTile(3210, 3280), new RSTile(3205, 3280),
			new RSTile(3204, 3279), new RSTile(3197, 3279),
			new RSTile(3190, 3282), new RSTile(3184, 3286),
			new RSTile(3178, 3287), new RSTile(3172, 3286),
			new RSTile(3165, 3285), new RSTile(3158, 3291),
			new RSTile(3149, 3296), new RSTile(3143, 3294),
			new RSTile(3137, 3295), new RSTile(3136, 3295),
			new RSTile(3130, 3295), new RSTile(3123, 3299),
			new RSTile(3118, 3296), new RSTile(3112, 3295),
			new RSTile(3106, 3295), new RSTile(3099, 3295),
			new RSTile(3094, 3292), new RSTile(3088, 3290),
			new RSTile(3082, 3289), new RSTile(3076, 3288),
			new RSTile(3074, 3284), new RSTile(3074, 3280),
			new RSTile(3069, 3277), new RSTile(3063, 3277),
			new RSTile(3057, 3276), new RSTile(3050, 3276),
			new RSTile(3047, 3276), new RSTile(3044, 3274),
			new RSTile(3037, 3277), new RSTile(3030, 3278),
			new RSTile(3024, 3276), new RSTile(3018, 3275),
			new RSTile(3013, 3276), new RSTile(3011, 3278),
			new RSTile(3007, 3284), new RSTile(3004, 3289),
			new RSTile(3007, 3294), new RSTile(3006, 3299),
			new RSTile(3004, 3304), new RSTile(3006, 3310),
			new RSTile(3007, 3316), new RSTile(3007, 3321),
			new RSTile(3005, 3326), new RSTile(3007, 3331),
			new RSTile(3007, 3332), new RSTile(3007, 3337),
			new RSTile(3007, 3343), new RSTile(3007, 3349),
			new RSTile(3005, 3355), new RSTile(3007, 3361),
			new RSTile(3012, 3361), new RSTile(3015, 3355) };
	public String rostoption;
	public String Color2;
	public String settings;
	public String clanchat = "";
	int bankfs = 0;
	int rocksteal = 0;
	int xrocksteal = 0;
	int rrocksteal = 0;
	int fs2 = 0;
	boolean miningMith = false;
	boolean priorMith = false;
	boolean priorMith3 = false;
	boolean respondtochat = false;
	boolean Goal = false;
	int Goal2 = 0;
	int Goal3 = 0;
	int Goal4 = 0;
	int mLevel2 = 0;
	int oMined2 = 0;
	long Time2 = -1;
	int mithBanked = 0;
	int mithDropped = 0;
	int mithrilID = 447;
	int[] mithRockID = { 31086, 31087, 31088 };
	boolean needNewPick = false;
	int oldExperience = 0;
	int[] pickaxeHandleID = { 466 };
	int[] pickaxeHeadID = { 480, 482, 484, 486, 488, 490 };
	int getupdate = 0;

	int[] pickaxeID = { 1265, 1267, 1269, 1273, 1271, 1275, 15259, 15261 };
	int[] pickaxeandnatureandfireID = { 1265, 1267, 1269, 1273, 1271, 1275,
			15259, 15261, 561, 1387 };
	boolean powermine = false;
	int priceOfCoal = 0;

	int priceOfMithril = 0;
	int priceOfMB = 0;
	int ranAwayFromCombat = 0;
	RSObject rock;
	RSObject rock2;
	RSObject rock3;
	boolean runAway = false;
	boolean pickFound = false;

	int startingExperience = skills.getCurrentSkillExp(Constants.STAT_MINING);
	int startingLevel = skills.getCurrentSkillLevel(Constants.STAT_MINING);

	long startTime = System.currentTimeMillis();

	long timeIdle = System.currentTimeMillis();

	long Mining = System.currentTimeMillis();

	RSTile topLadderObjectTile = new RSTile(3019, 3339);

	int tries = 0;

	long upLadderTimer = System.currentTimeMillis();

	boolean walkBack = false;

	int xpPerCoal = 50;

	int xpPerMithril = 80;

	public boolean atRock(final RSObject obj) {
		try {
			final Point location = Calculations.tileToScreen(obj.getLocation());
			final Point mlocation = getMouseLocation();
			if (location.x == -1 || location.y == -1) {
				return false;
			}
			if (!getMenuItems().get(0).toLowerCase().contains(
					"ine R".toLowerCase())
					|| (Math.abs(location.x - mlocation.x) > 12 && Math
							.abs(location.y - mlocation.y) > 12)) {
				moveMouse(location, 3, 3);
			}
			wait(random(40, 80));
			boolean validRock = false;
			final RSObject temp = getObjectAt(obj.getLocation());
			if (temp != null) {
				final int id = temp.getID();
				for (final int element : coalRockID) {
					if (id == element) {
						validRock = true;
					}
				}
				if (!validRock) {
					for (final int element : mithRockID) {
						if (id == element) {
							validRock = true;
						}
					}
				}
			}
			if (!validRock) {
				return false;
			}
			if (!getMenuItems().get(0).toLowerCase().contains(
					"ine R".toLowerCase())) {
				moveMouse(location, 6, 6);
				wait(random(40, 80));
				if (!getMenuItems().get(0).toLowerCase().contains(
						"ine R".toLowerCase())) {
					moveMouse(location, 6, 6);
					wait(random(40, 80));
					if (!getMenuItems().get(0).toLowerCase().contains(
							"ine R".toLowerCase())) {
						moveMouse(location, 6, 6);
						wait(random(40, 80));
					}
				}
			}

			if (getMenuItems().get(0).toLowerCase().contains(
					"ine R".toLowerCase())) {
				clickMouse(true);
			} else {
				clickMouse(false);
				if (!atMenu("ine R")) {
					return false;
				}
			}
			return true;
		} catch (final Exception e) {
			return false;
		}
	}

	public boolean atTileModified(final RSTile tile, final String action) {
		try {
			final Point location = Calculations.tileToScreen(tile);
			if (location.x == -1 || location.y == -1) {
				return false;
			}
			int xRand = 0;
			int yRand = 0;
			if (random(0, 2) == 0) {
				xRand = random(-26, -16);
			} else {
				xRand = random(16, 26);
			}
			if (random(0, 2) == 0) {
				yRand = random(-26, -16);
			} else {
				yRand = random(16, 26);
			}
			moveMouse(location, xRand, yRand);
			if (getMenuItems().get(0).toLowerCase().contains(
					action.toLowerCase())) {
				clickMouse(true);
			} else {
				clickMouse(false);
				if (!atMenu(action)) {
					return false;
				}
			}
			waitUntilNotMoving();
			return true;
		} catch (final Exception e) {
			return false;
		}
	}

	public boolean canEquipPickaxe() {
		if (superheat == 1 || highalch == 1) {
			return false;
		}
		if (skills.getCurrentSkillLevel(Constants.STAT_ATTACK) >= 60
				&& inventoryContainsOneOf(pickaxeID[7])
				|| skills.getCurrentSkillLevel(Constants.STAT_ATTACK) >= 60
				&& inventoryContainsOneOf(pickaxeID[6])
				|| skills.getCurrentSkillLevel(Constants.STAT_ATTACK) >= 40
				&& inventoryContainsOneOf(pickaxeID[5])
				|| skills.getCurrentSkillLevel(Constants.STAT_ATTACK) >= 30
				&& inventoryContainsOneOf(pickaxeID[4])
				|| skills.getCurrentSkillLevel(Constants.STAT_ATTACK) >= 20
				&& inventoryContainsOneOf(pickaxeID[3])
				|| skills.getCurrentSkillLevel(Constants.STAT_ATTACK) >= 5
				&& inventoryContainsOneOf(pickaxeID[2])
				|| skills.getCurrentSkillLevel(Constants.STAT_ATTACK) >= 1
				&& inventoryContainsOneOf(pickaxeID[1])
				|| skills.getCurrentSkillLevel(Constants.STAT_ATTACK) >= 1
				&& inventoryContainsOneOf(pickaxeID[0])) {
			return true;
		}
		return false;
	}

	public boolean climbDownLadder() {
		downLadderTimer = System.currentTimeMillis();
		if (!Calculations.onScreen(Calculations
				.tileToScreen(topLadderObjectTile))) {
			return false;
		}
		if (atTileModified(topLadderObjectTile, "down")) {
			while (getMyPlayer().getLocation().getY() < 6000
					&& System.currentTimeMillis() - downLadderTimer < 6000) {
				if (getMyPlayer().getLocation().getY() > 6000) {
					return true;
				}
				if (System.currentTimeMillis() - downLadderTimer >= 6000) {
					return false;
				}
				wait(random(600, 800));
			}
		} else {
			// if(distanceTo(ladderTile) > 6)
			// myWalkTile(ladderTile, 2);
			setCameraRotation(random(1, 359));
			// setCameraAltitude(true);
			// waitUntilNearTile(ladderTile, 0);
		}
		return false;
	}

	public boolean climbUpLadder() {
		upLadderTimer = System.currentTimeMillis();
		final RSObject object = getNearestObjectById(16, ladderID);
		if (object == null) {
			return false;
		}
		if (!Calculations.onScreen(Calculations.tileToScreen(object
				.getLocation()))) {
			myWalkTile(object.getLocation(), 2);
			wait(random(50, 600));
			if (random(0, 4) < 3) {
				moveMouse(random(100, 415), random(100, 237));
			}
			waitUntilNotMoving();
		}
		if (atObject(object, "up")) {
			while (getMyPlayer().getLocation().getY() > 6000
					&& System.currentTimeMillis() - upLadderTimer < 6000) {
				if (getMyPlayer().getLocation().getY() < 6000) {
					return true;
				}
				if (System.currentTimeMillis() - upLadderTimer >= 6000) {
					return false;
				}
				wait(random(600, 800));
			}
		} else {
			setCameraRotation(random(1, 359));
			// setCameraAltitude(true);
		}
		return false;
	}

	public int depositInventory() {
		if (getMyPlayer().getLocation().getY() > 6000) {
			if (getDestination() != null) {
				if (getDestination().getX() <= 3023
						&& getDestination().getY() > 6000) {
					waitUntilNotMoving();
				}
			}
			if (myWalkPath(coalToLadderPath, 15, 3)) {
				waitUntilNotMoving();
				climbUpLadder();
				wait(random(200, 300));
			}
			return random(100, 200);
		}
		if (distanceTo(bankTile) > 4) {
			buffTile = new RSTile(bankTile.getX() + random(-1, 1), bankTile
					.getY()
					+ random(-1, 1));
			if (getDestination() != null) {
				if (getDestination().getX() >= 3009
						&& getDestination().getX() <= 3018
						&& getDestination().getY() >= 3353
						&& getDestination().getY() <= 3357) {
					if (getCameraAngle() > 225 || getCameraAngle() < 135) {
						setCameraRotation(random(135, 225));
					}
					if (getCameraAngle() < 180) {
						int key = KeyEvent.VK_DOWN;
						Bot.getInputManager().pressKey((char) key);
						wait(random(500, 1200));
						Bot.getInputManager().releaseKey((char) key);
					}
					while (getMyPlayer().getLocation().getX() > 3018
							|| getMyPlayer().getLocation().getX() < 3009
							|| getMyPlayer().getLocation().getY() > 3360
							|| getMyPlayer().getLocation().getY() < 3355) {
						wait(10);
					}
					banking();
					return random(200, 300);
				}
				if (getDestination().getX() >= 3022
						&& getDestination().getX() <= 3031
						&& getDestination().getY() >= 3348
						&& getDestination().getY() <= 3356) {
					Point az = tileToMinimap(buffTile);
					while ((az.x == -1 || az.y == -1)
							&& getMyPlayer().isMoving()) {
						wait(10);
						az = tileToMinimap(buffTile);
					}
				}
			}
			randTile = new RSTile(buffTile.getX(), buffTile.getY());
			try {
				SGMWalk(randTile);
			} catch (final Exception ignored) {
			}
			if (random(0, 4) < 3) {
				moveMouse(random(100, 415), random(100, 237));
			}
			Antiban.run();
			return random(200, 300);
		}
		if (distanceTo(bankTile) <= 4
				&& !Calculations.onScreen(Calculations.tileToScreen(bankTile))
				&& !getMyPlayer().isMoving()) {
			try {
				SGMWalk(bankTile);
			} catch (final Exception ignored) {
			}
		}
		if (isInventoryFull() && bank.isOpen()) {
			setCameraAltitude(true);
			fs = 0;
			fs2++;
			if (fs2 > 6) {
				bankfs = 0;
			}
			int total = getInventoryCount(453) + getInventoryCount(447)
					+ getInventoryCount(454) + getInventoryCount(1617)
					+ getInventoryCount(1619) + getInventoryCount(1621)
					+ getInventoryCount(1623);
			if (total == 28 && superheat == 0) {
				if (bankfs == 0) {
					coalBanked += getInventoryCount(coalID);
					mithBanked += getInventoryCount(mithrilID);
					gemsBanked += getInventoryCount(gemID);
					bank.depositAll();
					depositdelay = System.currentTimeMillis();
					while ((System.currentTimeMillis() - depositdelay) < 4000
							&& isInventoryFull()) {
						wait(10);
					}
					if (isInventoryFull()) {
						bank.depositAll();
					}
					bankfs = 1;
					fs2 = 0;
				}
				if (random(0, 5) == 0) {
					bank.close();
				}
				wait(random(25, 100));
			} else {
				if (bankfs == 0) {
					coalBanked += getInventoryCount(coalID);
					mithBanked += getInventoryCount(mithrilID);
					gemsBanked += getInventoryCount(gemID);
					MithrilBarBanked += getInventoryCount(2359);
					bank.depositAllExcept(pickaxeandnatureandfireID);
					while ((System.currentTimeMillis() - depositdelay) < 4000
							&& isInventoryFull()) {
						wait(10);
					}
					if (isInventoryFull()) {
						bank.depositAllExcept(pickaxeandnatureandfireID);
					}
					bankfs = 1;
					fs2 = 0;
				}
				if (random(0, 5) == 0) {
					bank.close();
				}
				wait(random(25, 100));
			}
			wait(random(200, 300));
		}
		return random(25, 100);
	}

	public int dropInventory() {
		if (atInventoryItem(coalID, "Drop")) {
			coalDropped++;
			wait(random(500, 700));
			return random(50, 75);
		} else if (atInventoryItem(mithrilID, "Drop")) {
			mithDropped++;
			wait(random(500, 700));
			return random(50, 75);
		}
		for (final int element : gemID) {
			if (atInventoryItem(element, "Drop")) {
				gemsDropped++;
				wait(random(500, 700));
				return random(50, 75);
			}
		}
		dropping = false;
		invCountToDrop = random(10, 29);
		return random(50, 75);
	}

	public int dropJunk() {
		for (final int junkItem : junkItems) {
			if (inventoryContainsOneOf(junkItem)) {
				if (atInventoryItem(junkItem, "Drop")) {
					return random(550, 700);
				}
			}
		}
		final RSItem item = getInventoryItemByID(goldPiecesID);
		if (item != null) {
			if (item.getStackSize() <= 5000) {
				if (atInventoryItem(goldPiecesID, "Drop")) {
					return random(550, 700);
				}
			}
		}
		return random(40, 100);
	}

	private RSObject findNearestUnoccupiedObject(final RSObject... objects) {
		RSObject nearestObj = null;
		for (final RSObject object : objects) {
			if (isObjectOccupied(object)) {
				continue;
			}
			if (nearestObj == null) {
				nearestObj = object;
			} else if (distanceTo(object.getLocation()) < distanceTo(nearestObj
					.getLocation())) {
				nearestObj = object;
			}
		}
		return nearestObj;
	}

	public RSObject[] findObjects(final int range, final int... ids) {
		final ArrayList<RSObject> matches = new ArrayList<RSObject>();
		final RSTile pos = getMyPlayer().getLocation();
		final int xBegin = pos.getX() - range;
		final int yBegin = pos.getY() - range;
		final int xEnd = pos.getX() + range;
		final int yEnd = pos.getY() + range;
		for (int x = xBegin; x < xEnd; x++) {
			for (int y = yBegin; y < yEnd; y++) {
				final RSTile t = new RSTile(x, y);
				final RSObject obj = getObjectAt(t);
				if (obj == null) {
					continue;
				}
				for (final int objType : ids) {
					if (objType == obj.getID()) {
						matches.add(obj);
					}
				}
			}
		}
		try {
			matches.trimToSize();
			return (RSObject[]) matches.toArray(new RSObject[matches.size()]);
		} catch (final Exception e) {
		}
		return new RSObject[0];
	}

	public int fixPickaxe() {
		final RSItemTile head = getGroundItemByID(17, pickaxeHeadID);
		if (head != null) {
			log("Getting pickaxe head...");
			myWalkTile(head, 1);
			waitUntilNearTile(head, 0);
			headAttempts = 0;
			while (!atTile(head, "Take") || headAttempts <= 10) {
				setCameraRotation(random(1, 359));
				if (random(0, 3) == 0) {
					atTile(randomizeTile(head, 1, 1), "Walk");
				}
				headAttempts++;

			}
		}
		// if(equipmentContains(pickaxeHandleID) ||
		// equipmentContains(brokenPickaxeID))
		unequipWeapon();
		openTab(Constants.TAB_INVENTORY);
		wait(random(200, 300));
		if (getInventoryItemByID(pickaxeHeadID) != null
				&& getInventoryItemByID(pickaxeHandleID) != null) {
			useItem(getInventoryItemByID(pickaxeHeadID),
					getInventoryItemByID(pickaxeHandleID));
		}

		if (skills.getCurrentSkillLevel(Constants.STAT_ATTACK) >= 40
				&& inventoryContainsOneOf(pickaxeID[5])) {
			atInventoryItem(pickaxeID[5], "ield");
		} else if (skills.getCurrentSkillLevel(Constants.STAT_ATTACK) >= 30
				&& inventoryContainsOneOf(pickaxeID[4])) {
			atInventoryItem(pickaxeID[4], "ield");
		} else if (skills.getCurrentSkillLevel(Constants.STAT_ATTACK) >= 20
				&& inventoryContainsOneOf(pickaxeID[3])) {
			atInventoryItem(pickaxeID[3], "ield");
		} else if (skills.getCurrentSkillLevel(Constants.STAT_ATTACK) >= 5
				&& inventoryContainsOneOf(pickaxeID[2])) {
			atInventoryItem(pickaxeID[2], "ield");
		} else if (skills.getCurrentSkillLevel(Constants.STAT_ATTACK) >= 1
				&& inventoryContainsOneOf(pickaxeID[1])) {
			atInventoryItem(pickaxeID[1], "ield");
		} else if (skills.getCurrentSkillLevel(Constants.STAT_ATTACK) >= 1
				&& inventoryContainsOneOf(pickaxeID[0])) {
			atInventoryItem(pickaxeID[0], "ield");
		}
		return random(200, 400);
	}

	public int getMarketPriceOfItem(final int id) {
		String pageSource = "";
		int begin = 0;
		int end = 0;
		try {
			final URL theUrl = new URL(
					"http://services.runescape.com/m=itemdb_rs/Coal/viewitem.ws?obj="
							+ id);
			final URLConnection theUrlConnection = theUrl.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					theUrlConnection.getInputStream()));
			String inputLine = "";
			while ((inputLine = in.readLine()) != null) {
				pageSource += inputLine;
			}
			in.close();
			pageSource = pageSource.replaceAll("\n", "");
			pageSource = pageSource.replaceAll("\t", "");
			pageSource = pageSource.replaceAll(",", "");
			begin = pageSource.indexOf("<b>Market price:</b> ")
					+ "<b>Market price:</b> ".length();
			end = pageSource.indexOf("</span><span><b>Maximum price:</b>");
		} catch (final Exception e) {
			System.out
					.println("http://services.runescape.com/m=itemdb_rs/Coal/viewitem.ws?obj="
							+ id);
		}
		return (int) new Integer(pageSource.substring(begin, end)).intValue();
	}

	@SuppressWarnings("deprecation")
	public RSObject getNearestObjectById(final int range, final int... ids) {
		for (int i = 0; i <= range; i++) {
			final RSObject temp = findObject(i, ids);
			if (temp != null) {
				return temp;
			}
		}
		return null;
	}

    public int getNewPick() {
        if (getMyPlayer().getLocation().getY() > 6000) {
            if (getDestination() != null) {
                if (getDestination().getX() <= 3023 && getDestination().getY() > 6000) {
                    waitUntilNotMoving();
                }
            }
            if (myWalkPath(coalToLadderPath, 15, 3)) {
                waitUntilNotMoving();
                climbUpLadder();
                wait(random(200, 300));
            }
            return random(100, 200);
        }
        if (distanceTo(bankTile) > 4) {
            buffTile = new RSTile(bankTile.getX() + random(-1, 1), bankTile.getY() + random(-1, 1));
            if (getDestination() != null) {
                if (getDestination().getX() >= 3009 && getDestination().getX() <= 3018 && getDestination().getY() >= 3353 && getDestination().getY() <= 3357) {
                    if (getCameraAngle() > 225 || getCameraAngle() < 135) {
                        setCameraRotation(random(135, 225));
                    }
                    if (getCameraAngle() < 180) {
                        int key = KeyEvent.VK_DOWN;
                        Bot.getInputManager().pressKey((char)key);
                        wait(random(500, 1200));
                        Bot.getInputManager().releaseKey((char)key);
                    }
                    while (getMyPlayer().getLocation().getX() > 3018 || getMyPlayer().getLocation().getX() < 3009 || getMyPlayer().getLocation().getY() > 3360 || getMyPlayer().getLocation().getY() < 3355) {
                        wait(10);
                    }
                    banking();
                    return random(200, 300);
                }
                if (getDestination().getX() >= 3022 && getDestination().getX() <= 3031 && getDestination().getY() >= 3348 && getDestination().getY() <= 3356) {
                    Point az = tileToMinimap(buffTile);
                    while ((az.x == -1 || az.y == -1) && getMyPlayer().isMoving()) {
                        wait(10);
                        az = tileToMinimap(buffTile);
                    }
                }
            }
            randTile = new RSTile(buffTile.getX(), buffTile.getY());
            try {
            SGMWalk(randTile);
            } catch (final Exception ignored) {
            }
            if (random(0, 4) < 3) {
                moveMouse(random(100, 415), random(100, 237));
            }
            Antiban.run();
            return random(200, 300);
        }
        if (distanceTo(bankTile) <= 4 && !Calculations.onScreen(Calculations.tileToScreen(bankTile)) && !getMyPlayer().isMoving()) {
            try {
            SGMWalk(bankTile);
            } catch (final Exception ignored) {
            }
        }
        if (bank.isOpen()) {
            setCameraAltitude(true);
            fs = 0;
            fs2++;
            if (fs2 > 6) {
                bankfs = 0;
            }
            int total = getInventoryCount(453) + getInventoryCount(447) + getInventoryCount(454) + getInventoryCount(1617) + getInventoryCount(1619) + getInventoryCount(1621) + getInventoryCount(1623);
            if (total == 28 && superheat == 0) {
                if (bankfs == 0) {
                    coalBanked += getInventoryCount(coalID);
                    mithBanked += getInventoryCount(mithrilID);
                    gemsBanked += getInventoryCount(gemID);
                    bank.depositAll();
                    depositdelay = System.currentTimeMillis();
                    while ((System.currentTimeMillis() - depositdelay) < 4000 && isInventoryFull()) {
                        wait(10);
                    }
                    if (isInventoryFull()) {
                        bank.depositAll();
                    }
                    
                    bankfs = 1;
                    fs2 = 0;
                    RSInterfaceComponent[] bankcomponents = getInterface(762).getChild(87).getComponents();
                    for (int k = 0; k < bankcomponents.length; k++) {
                        for (int l = pickaxeID.length - 1; l >= 0; l--) {
                            if (bankcomponents[k].getComponentID() == pickaxeID[l]) {
                                bank.withdraw(pickaxeID[l], 1);
                                pickFound = true;
                                break;
                            }
                        } 
                    }
                    if (!pickFound) {
                        log("No useable pickaxe, ending script =(");
                        bank.close();
                        printProgressReport();
                        stopScript();
                    }
                }
                if (random(0, 5) == 0) {
                    bank.close();
                }
                wait(random(25, 100));
            } else {
                if (bankfs == 0) {
                    coalBanked += getInventoryCount(coalID);
                    mithBanked += getInventoryCount(mithrilID);
                    gemsBanked += getInventoryCount(gemID);
                    MithrilBarBanked += getInventoryCount(2359);
                    bank.depositAllExcept(pickaxeandnatureandfireID);
                    while ((System.currentTimeMillis() - depositdelay) < 4000 && isInventoryFull()) {
                        wait(10);
                    }
                    if (isInventoryFull()) {
                        bank.depositAllExcept(pickaxeandnatureandfireID);
                    }
                    bankfs = 1;
                    fs2 = 0;
                    RSInterfaceComponent[] bankcomponents = getInterface(762).getChild(87).getComponents();
                    for (int k = 0; k < bankcomponents.length; k++) {
                        for (int l = pickaxeID.length - 1; l >= 0; l--) {
                            if (bankcomponents[k].getComponentID() == pickaxeID[l]) {
                                bank.withdraw(pickaxeID[l], 1);
                                pickFound = true;
                                break;
                            }
                        } 
                    }
                    if (!pickFound) {
                        log("No useable pickaxe, ending script =(");
                        bank.close();
                        printProgressReport();
                        stopScript();
                    }
                }
                if (random(0, 5) == 0) {
                    bank.close();
                }
                wait(random(25, 100));
            }
            wait(random(200, 300));
        }
        return random(200, 300);
    }

	public String getVersion() {
		return "" + getClass().getAnnotation(ScriptManifest.class).version();
	}

	public boolean isCarryingJunk() {
		for (final int junkItem : junkItems) {
			if (inventoryContainsOneOf(junkItem)) {
				return true;
			}
		}
		final RSItem item = getInventoryItemByID(goldPiecesID);
		if (item != null && highalch == 0) {
			if (item.getStackSize() <= 5000) {
				return true;
			}
		}
		return false;
	}

	public boolean isLost() {
		if (distanceTo(lumbridgeTile) <= 17) {
			return false;
		}
		if (distanceTo(bankTile) <= 17) {
			return false;
		}
		if (distanceTo(ladderTile) <= 17) {
			return false;
		}
		for (int i = 0; i < betweenBankAndLadderTiles.length - 1; i++) {
			if (distanceTo(betweenBankAndLadderTiles[i]) <= 17) {
				return false;
			}
		}
		for (int i = 0; i < coalToLadderPath.length - 1; i++) {
			if (distanceTo(coalToLadderPath[i]) <= 17) {
				return false;
			}
		}
		return true;
	}

	private boolean isObjectOccupied(final RSObject obj) {
		if (rocksteal == 1) {
			return false;
		}
		if (xrocksteal == 1) {
			final int[] playerIndex = Bot.getClient().getRSPlayerIndexArray();
			final org.rsbot.accessors.RSPlayer[] players = Bot.getClient()
					.getRSPlayerArray();
			if (obj.getLocation() == null) {
				return true;
			}
			for (final int element : playerIndex) {
				if (players[element] == null) {
					continue;
				}
				final RSPlayer player = new RSPlayer(players[element]);
				try {
					if (Methods.distanceBetween(obj.getLocation(), player
							.getLocation()) <= 1
							&& (player.getAnimation() == 624
									|| player.getAnimation() == 626 || player
									.getAnimation() == 628)) {
						return true;
					}
				} catch (final Exception ignored) {
				}
			}
			return false;
		}
		if (rrocksteal == 1) {
			switch (random(0, 1)) {
			case 0:
				return false;

			case 1:
				final int[] playerIndex = Bot.getClient()
						.getRSPlayerIndexArray();
				final org.rsbot.accessors.RSPlayer[] players = Bot.getClient()
						.getRSPlayerArray();
				if (obj.getLocation() == null) {
					return true;
				}
				for (final int element : playerIndex) {
					if (players[element] == null) {
						continue;
					}
					final RSPlayer player = new RSPlayer(players[element]);
					try {
						if (Methods.distanceBetween(obj.getLocation(), player
								.getLocation()) < 2
								&& (player.getAnimation() == 624
										|| player.getAnimation() == 626 || player
										.getAnimation() == 628)) {
							return true;
						}
					} catch (final Exception ignored) {
					}
				}
				return false;

			default:
				break;

			}
		}
		return false;
	}

	public int loop() {
		if (Goal2 == 1
				&& skills.getCurrentSkillLevel(Constants.STAT_MINING) >= mLevel2) {
			logout();
			stopScript();
		}
		if (Goal3 == 1 && (coalBanked + mithBanked) >= oMined2) {
			logout();
			stopScript();
		}
		if (Goal4 == 1 && (System.currentTimeMillis() - startTime) > Time2) {
			logout();
			stopScript();
		}
		if (!isLoggedIn()) {
			return random(500, 800);
		}
		if (isLoggedIn() && WHnb != 0
				&& getMyPlayer().getLocation().getY() > 6000
				&& Numberofarrays() > WHnb) {
			worldHop();
			return random(500, 800);
		}
		if (startfs == 0 && isLoggedIn()) {
			startingLevel = skills.getCurrentSkillLevel(Constants.STAT_MINING);
			startingExperience = skills
					.getCurrentSkillExp(Constants.STAT_MINING);
			setCameraAltitude(true);
			setCameraRotation(random(1, 359));
			startfs = 1;
		}
		if (!clanchat.equals("")
				&& getInterface(589).getChild(0).containsText("Not in chat")) {
			joinclanchat();
			return random(200, 300);
		}
		if (Failwalk()) {
			if (Failwalk()) {
				WalkTileMM(doorside);
			}
			if (Failwalk()) {
				wait(random(7000, 8000));
			}
			if (Failwalk()) {
				setCameraRotation(90 + random(-5, 5));
				setCameraAltitude(false);
				atDoor(11714, 'w');
				WalkTileMM(doorfront);
				wait(random(6000, 7000));
			}
			if (!Failwalk()) {
				setCameraAltitude(true);
				WalkTileMM(ladderTile);
				waitUntilNearTile(ladderTile, 2);
			}
			return random(500, 800);
		}
		// if(getGroundItemByID(17, pickaxeHeadID) != null &&
		// (isCarryingItem(pickaxeHandleID)))
		if (getGroundItemByID(17, pickaxeHeadID) != null) {
			return fixPickaxe();
		}

		if (getEnergy() >= energyToRunAt && rest == 0) {
			energyToRunAt = random(40, 90);
			setRun(true);
			wait(random(400, 500));
		}
		if (getEnergy() <= energyToRestAt && rest2 == 1) {
			energyToRestAt = random(15, 25);
			setRun(false);
			wait(random(400, 500));
		}
		if (getMyPlayer().getLocation().getY() < 6000 && rest == 1) {
			setRun(false);
			wait(random(400, 500));
		}
		if (getMyPlayer().getLocation().getY() > 6000 && rest == 1) {
			if (getEnergy() >= energyToRunAt) {
				energyToRunAt = random(40, 90);
				setRun(true);
				wait(random(400, 500));
			}
		}

		if (getMyPlayer().isInCombat()) {
			runAway = true;
		}

		if (runAway) {
			return runAway();
		}

		if (distanceTo(lumbridgeTile) <= 17) {
			walkBack = true;
		}

		if (isLost()) {
			if (myGetNextTile(lumbridgeToFalidorPath, 17) == null) {
				return teleportToLumbridge();
			}
			walkBack = true;
		}
		if (isInventoryFull() && highalch == 1) {
			Highalch();
			lastRockLocation = new RSTile(0000, 0000);
			return random(200, 300);
		}
		if ((getInventoryCount(453) >= 4 && getInventoryCount(447) >= 1)
				&& superheat == 1) {
			Superheat();
			return random(200, 300);
		}
		RSObject bankbooth = getNearestObjectById(15, bankBoothID);

		if ((isInventoryFull()) && getMyPlayer().getLocation().getX() <= 3018
				&& getMyPlayer().getLocation().getX() >= 3009
				&& getMyPlayer().getLocation().getY() <= 3360
				&& getMyPlayer().getLocation().getY() >= 3355 && !bank.isOpen()) {
			banking();
		}
		if (bankbooth != null && bankbooth.getLocation() != null) {
			if ((isInventoryFull()) && tileOnScreen(bankbooth.getLocation())) {
				banking();
			}
		}

		if (walkBack) {
			if (myWalkPath(lumbridgeToFalidorPath, 17, 1)) {
				walkBack = false;
				return depositInventory();
			}
		}

		/*
		 * //will never be called since the rsbot equipment interface is fucked
		 * up if(isCarryingItem(brokenPickaxeID) ||
		 * isCarryingItem(pickaxeHandleID) || !isCarryingItem(pickaxeID)) return
		 * getNewPick();
		 */

		if (getInterface(211).isValid()) {
			if (getInterface(211).getChild(1)
					.containsText("You need a Pickaxe")
					&& getGroundItemByID(17, pickaxeHeadID) == null) {
				needNewPick = true;
			}
		}

		if (needNewPick) {
			if (getNewPick() == 99999) {
				needNewPick = false;
				return random(10, 20);
			} else {
				return random(10, 20);
			}
		}

		if (isCarryingJunk()) {
			return dropJunk();
		}

		if (canEquipPickaxe()) {
			return wieldPickaxe();
		}

		if (powermine
				&& (getInventoryCount() >= invCountToDrop || isInventoryFull())
				&& superheat == 0) {
			dropping = true;
		}

		if (dropping) {
			return dropInventory();
		}

		if (isInventoryFull() && !powermine) {
			return depositInventory();
		}

		if (isInventoryFull()) {
			return depositInventory();
		}

		return mineCoal();
	}

	public int mineCoal() {
		if (getMyPlayer().getLocation().getY() < 6000) {
			if (distanceTo(ladderTile) > 10) {
				buffTile = new RSTile(ladderTile.getX() + random(-2, 2),
						ladderTile.getY() + random(-2, 1));
				if (getDestination() != null) {
					if (getDestination().getX() >= 3015
							&& getDestination().getX() <= 3023
							&& getDestination().getY() >= 3336
							&& getDestination().getY() <= 3341) {
						waitUntilNotMoving();
						return random(200, 300);
					}
					if (getDestination().getX() >= 3022
							&& getDestination().getX() <= 3031
							&& getDestination().getY() >= 3348
							&& getDestination().getY() <= 3356) {
						Point ax = tileToMinimap(buffTile);
						while ((ax.x == -1 || ax.y == -1)
								&& getMyPlayer().isMoving()) {
							wait(10);
							ax = tileToMinimap(buffTile);
						}
					}
				}
				randTile = new RSTile(buffTile.getX(), buffTile.getY());
				try {
					SGMWalk(randTile);
				} catch (final Exception ignored) {
				}
				if (random(0, 4) < 3) {
					moveMouse(random(100, 415), random(100, 237));
				}
				Antiban.run();
				return random(200, 300);
			}
			if (distanceTo(ladderTile) <= 10) {
				while (getMyPlayer().isMoving()) {
					wait(100);
				}
				if (!Calculations.onScreen(Calculations
						.tileToScreen(topLadderObjectTile))) {
					try {
						SGMWalk(ladderTile);
					} catch (final Exception ignored) {
					}
				}
				climbDownLadder();
				randTile = new RSTile(nullTile.getX(), nullTile.getY());
				bankfs = 0;
				fs2 = 0;
				fs = 0;
				lastRockLocation = new RSTile(0000, 0000);
			}
			return random(200, 400);
		}

		if (getMyPlayer().getAnimation() != -1) {
			timeIdle = System.currentTimeMillis();
		}
		if (getMyPlayer().getAnimation() == -1) {
			Mining = System.currentTimeMillis();
		}
		if (getObjectAt(lastRockLocation) == null) {
			lastRockLocation = new RSTile(0000, 0000);
		}

		while (!mineNewRock() && !getMyPlayer().isMoving()
				&& getInventoryCount() < 27) {
			moveMouseoverobj();
			if (random(1, 20) == 10) {
				Antiban.run();
			}
			if (getMyPlayer().getAnimation() == -1) {
				Mining = System.currentTimeMillis();
				while ((System.currentTimeMillis() - Mining) < 2000
						&& !mineNewRock()) {
					wait(10);
				}
				if (getMyPlayer().getAnimation() == -1) {
					lastRockLocation = new RSTile(0000, 0000);
					break;
				}
			}
			if (priorMith3) {
				RSObject mithRock4 = getNearestObjectById(16, mithRockID);
				if (mithRock4 != null && !currentRockisMith()) {
					lastRockLocation = new RSTile(0000, 0000);
					break;
				}
			}
			if (delay2 != 0) {
				delaytimetonext = System.currentTimeMillis();
				while ((System.currentTimeMillis() - delaytimetonext) < delay2
						&& !mineNewRock()) {
					wait(10);
				}
			}
			if (respondtochat
					&& (System.currentTimeMillis() - chatresponddelay) > 10000) {
				chatrespond();
				chatresponddelay = System.currentTimeMillis();
			}
			if (mineNewRock()) {
				break;
			}
		}
		if (isInventoryFull()) {
			return random(100, 200);
		}

		if (mineNewRock() || getMyPlayer().getAnimation() == -1
				&& System.currentTimeMillis() - timeIdle >= 4000
				&& !getMyPlayer().isMoving() && !isInventoryFull()) {
			rock = null;
			RSObject mithRock3 = findNearestUnoccupiedObject(findObjects(16,
					mithRockID));
			RSObject mithRock4 = getNearestObjectById(16, mithRockID);
			RSObject coalRock3 = findNearestUnoccupiedObject(findObjects(10,
					coalRockID));
			RSObject coalRock4 = getNearestObjectById(16, coalRockID);
			if ((miningMith || superheat == 1)
					&& (mithRock3 != null || mithRock4 != null)) {
				if (mithRock3 != null) {
					rock = mithRock3;
				} else {
					rock = mithRock4;
				}
			} else {
				if (coalRock3 != null) {
					rock = coalRock3;
				} else {
					rock = coalRock4;
				}
			}
			if (!priorMith && miningMith && superheat == 0) {
				if (mithRock3 != null || mithRock4 != null || coalRock3 != null
						|| coalRock4 != null) {
					if (mithRock3 != null || coalRock3 != null) {
						if (mithRock3 == null && coalRock3 != null) {
							rock = coalRock3;
						}
						if (mithRock3 != null && coalRock3 == null) {
							rock = mithRock3;
						} else if (mithRock3 != null && coalRock3 != null) {
							if (distanceTo(mithRock3) < distanceTo(coalRock3)) {
								rock = mithRock3;
							} else {
								rock = coalRock3;
							}
						}
					} else if (mithRock4 != null || coalRock4 != null) {
						if (mithRock4 == null && coalRock4 != null) {
							rock = coalRock4;
						}
						if (mithRock4 != null && coalRock4 == null) {
							rock = mithRock4;
						} else if (mithRock4 != null && coalRock4 != null) {
							if (distanceTo(mithRock4) < distanceTo(coalRock4)) {
								rock = mithRock4;
							} else {
								rock = coalRock4;
							}
						}
					}
				}
			}
			if (rock == null) {
				if (distanceTo(ladderToCoalPath[ladderToCoalPath.length - 1]) < 6) {
					myWalkPath(coalToLadderPath, 15, 3);
					wait(random(50, 600));
					if (random(0, 4) < 3) {
						moveMouse(random(100, 415), random(100, 237));
					}
					waitUntilNotMoving();
					return random(50, 200);
				}
				if (distanceTo(coalToLadderPath[coalToLadderPath.length - 1]) < 6) {
					myWalkPath(ladderToCoalPath, 15, 3);
					wait(random(50, 600));
					if (random(0, 4) < 3) {
						moveMouse(random(100, 415), random(100, 237));
					}
					waitUntilNotMoving();
					return random(50, 200);
				}
				if (random(0, 2) == 0) {
					myWalkPath(ladderToCoalPath, 17, 3);
				} else {
					myWalkPath(coalToLadderPath, 17, 3);
				}

				wait(random(50, 600));
				if (random(0, 4) < 3) {
					moveMouse(random(100, 415), random(100, 237));
				}
				waitUntilNotMoving();
				return random(50, 200);
			}
			if (rock.getLocation().getY() > 9756) {
				myWalkPath(coalToLadderPath, 17, 3);
				waitUntilNotMoving();
				lastRockLocation = new RSTile(0000, 0000);
				return random(50, 200);
			}
			rock3 = rock;
			if (!Calculations.onScreen(Calculations.tileToScreen(rock
					.getLocation()))) {
				if (!myWalkTile(rock.getLocation(), 2)) {
					lastRockLocation = new RSTile(0000, 0000);
					return random(20, 40);
				}
				if (random(0, 4) == 0) {
					setCameraAltitude(true);
				}
				wait(random(50, 200));
				if (random(0, 4) < 3) {
					moveMouse(random(100, 415), random(100, 237));
				}
				while (isCameraMoving(100, 7) || getMyPlayer().isMoving()) {
				}
				return random(50, 200);
			}
			if (delay1 != 0) {
				wait(delay1 + random(0, 100));
			}
			lastRockLocation = rock.getLocation();
			if (!atRock(rock)) {
				lastRockLocation = new RSTile(0000, 0000);
				setCameraRotation(random(1, 359));
			}
			Antiban.run();
			while (isCameraMoving(100, 7) || getMyPlayer().isMoving()) {
			}
			wait(random(500, 750));
			moveMouseoverobj();
			return random(100, 300);
		}
		Antiban.run();
		return random(200, 300);
	}

	public boolean mineNewRock() {
		final RSObject obj = getObjectAt(lastRockLocation);
		if (obj == null) {
			return true;
		}
		for (final int element : coalRockID) {
			if (obj.getID() == element) {
				return false;
			}
		}
		for (final int element : mithRockID) {
			if (obj.getID() == element) {
				return false;
			}
		}
		return true;
	}

	public boolean currentRockisMith() {
		final RSObject obj = getObjectAt(lastRockLocation);
		if (obj == null) {
			return false;
		}
		for (final int element : mithRockID) {
			if (obj.getID() == element) {
				return true;
			}
		}
		return false;
	}

	public RSTile myGetNextTile(final RSTile[] path, final int maxDist) {
		for (int i = path.length - 1; i >= 0; i--) {
			if (distanceTo(path[i]) <= maxDist) {
				return path[i];
			}
		}
		return null;
	}

	/**
	 * Walks to the next reachable tile in the path
	 * 
	 * @param path
	 *            The path it will walk
	 * @param maxDist
	 *            The max distance it will search for the next tile in the path
	 * @param randomness
	 *            the randomness it will add to clicking the tile
	 * @return if you are within 4 tiles of the destination
	 */
	public boolean myWalkPath(final RSTile[] path, final int maxDist,
			final int randomness) {
		if (distanceTo(path[path.length - 1]) <= 4) {
			return true;
		} else {
			final RSTile theTile = myGetNextTile(path, maxDist);
			if (theTile != null) {
				myWalkTile(theTile, randomness);
				if (distanceTo(randTile) > 2 && getMyPlayer().isMoving()) {
					wait(random(1500, 2500));
				}
			}
			return false;
		}
	}

	public boolean myWalkTile(final RSTile theTile, final int randomness) {
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
						// log("Failed walking to tile: (" + theTile.getX() +
						// ", " + theTile.getY() + ")");
						return false;
					} else {
						return true;
					}
				} else {
					return true;
				}
			} else {
				if (!WalkTileMM(newTile)) {
					// log("Failed walking to tile: (" + theTile.getX() + ", " +
					// theTile.getY() + ")");
					return false;
				} else {
					return true;
				}
			}
		} else {
			// log("Tile out of reach: (" + theTile.getX() + ", " +
			// theTile.getY() + ")");
			return false;
		}
	}

	public void onRepaint(final Graphics g) {
		if (!startScript) {
			g.setColor(new Color(255, 255, 255, 255));
			g.drawString("Loading...", 295, 19);
		} else {
			Mouse m = Bot.getClient().getMouse();
			if (isLoggedIn()) {
				if (startTime == 0) {
					startTime = System.currentTimeMillis();
				}

				if (startingLevel == 0 || startingExperience == 0) {
					startingLevel = skills
							.getCurrentSkillLevel(Constants.STAT_MINING);
				}

				long millis = System.currentTimeMillis() - startTime;
				final long hours = millis / (1000 * 60 * 60);
				millis -= hours * 1000 * 60 * 60;
				final long minutes = millis / (1000 * 60);
				millis -= minutes * 1000 * 60;
				final long seconds = millis / 1000;

				switch (Color) {
				case 1:
					g.setColor(new Color(33, 46, 207, 100));
					break;
				case 2:
					g.setColor(new Color(64, 64, 64, 70));
					break;
				case 3:
					g.setColor(new Color(255, 200, 0, 100));
					break;
				case 4:
					g.setColor(new Color(0, 255, 255, 70));
					break;
				case 5:
					g.setColor(new Color(0, 0, 0, 150));
					break;
				case 6:
					g.setColor(new Color(0, 0, 0, 150));
					break;
				case 7:
					g.setColor(new Color(33, 46, 207, 100));
					break;
				case 8:
					g.setColor(new Color(0, 0, 0, 150));
					break;
				}
				// window is (0, 0) (337, 515)
				// my box is (515 - 260, 0) (337, 200)
				final int x = 515 - 230;
				int y = 4;
				g.fillRoundRect(x, y, 230, height, 15, 15);
				switch (Color) {
				case 1:
					g.setColor(new Color(26, 36, 162, 255));
					break;
				case 2:
					g.setColor(new Color(255, 255, 255, 255));
					break;
				case 3:
					g.setColor(new Color(0, 0, 0, 255));
					break;
				case 4:
					g.setColor(new Color(255, 255, 255, 255));
					break;
				case 5:
					g.setColor(new Color(255, 255, 0, 255));
					break;
				case 6:
					g.setColor(new Color(255, 0, 0, 255));
					break;
				case 7:
					g.setColor(new Color(26, 36, 162, 255));
					break;
				case 8:
					g.setColor(new Color(0, 255, 0, 255));
					break;
				}
				g.drawRoundRect(x, y, 230, height, 20, 15);
				if (m.x > 515 || m.x < 285 || m.y > height + 4 || m.y < y) {
					switch (Color) {
					case 1:
						g.setColor(new Color(255, 255, 255, 255));
						break;
					case 2:
						g.setColor(new Color(255, 255, 255));
						break;
					case 3:
						g.setColor(new Color(255, 200, 0));
						break;
					case 4:
						g.setColor(new Color(255, 0, 255));
						break;
					case 5:
						g.setColor(new Color(255, 0, 0));
						break;
					case 6:
						g.setColor(new Color(255, 0, 255));
						break;
					case 7:
						g.setColor(new Color(240, 100, 150));
						break;
					case 8:
						g.setColor(new Color(255, 255, 255, 250));
						break;
					}
					g.drawString("Sexy Guild Miner v" + getVersion(), x + 57,
							y += 15);
				} else {
					switch (Color) {
					case 1:
						g.setColor(new Color(255, 255, 255, 255));
						break;
					case 2:
						g.setColor(new Color(255, 255, 255));
						break;
					case 3:
						g.setColor(new Color(255, 200, 0));
						break;
					case 4:
						g.setColor(new Color(255, 0, 255));
						break;
					case 5:
						g.setColor(new Color(255, 0, 0));
						break;
					case 6:
						g.setColor(new Color(255, 0, 255));
						break;
					case 7:
						g.setColor(new Color(240, 100, 150));
						break;
					case 8:
						g.setColor(new Color(255, 255, 255, 250));
						break;
					}
					g.drawString("Sexy Guild Miner v" + getVersion(), x + 57,
							y += 15);
					switch (Color) {
					case 1:
						break;
					case 2:
						g.setColor(new Color(0, 0, 0));
						break;
					case 3:
						g.setColor(new Color(0, 0, 0));
						break;
					case 4:
						g.setColor(new Color(0, 0, 255));
						break;
					case 5:
						g.setColor(new Color(255, 255, 0));
						break;
					case 6:
						g.setColor(new Color(255, 0, 0));
						break;
					case 7:
						g.setColor(new Color(0, 255, 255));
						break;
					case 8:
						g.setColor(new Color(0, 255, 0, 250));
						break;
					}
					g.drawString("Running for " + hours + ":" + minutes + ":"
							+ seconds, x + 10, y += 15);
					if (powermine) {
						switch (Color) {
						case 1:
							break;
						case 2:
							g.setColor(new Color(255, 255, 255));
							break;
						case 3:
							g.setColor(new Color(255, 200, 0));
							break;
						case 4:
							g.setColor(new Color(0, 255, 255));
							break;
						case 5:
							break;
						case 6:
							break;
						case 7:
							break;
						case 8:
							break;
						}
						g.drawString("Dropped " + coalDropped + " coal",
								x + 10, y += 15);
						switch (Color) {
						case 1:
							break;
						case 2:
							g.setColor(new Color(0, 0, 0));
							break;
						case 3:
							g.setColor(new Color(0, 0, 0));
							break;
						case 4:
							g.setColor(new Color(0, 255, 0));
							break;
						case 5:
							break;
						case 6:
							break;
						case 7:
							break;
						case 8:
							break;
						}
						g.drawString("Dropped " + mithDropped + " mithril",
								x + 10, y += 15);
						switch (Color) {
						case 1:
							break;
						case 2:
							g.setColor(new Color(255, 255, 255));
							break;
						case 3:
							g.setColor(new Color(255, 200, 0));
							break;
						case 4:
							g.setColor(new Color(255, 255, 0));
							break;
						case 5:
							break;
						case 6:
							break;
						case 7:
							break;
						case 8:
							break;
						}
						g.drawString("Dropped " + gemsDropped + " gems",
								x + 10, y += 15);
					} else {
						switch (Color) {
						case 1:
							break;
						case 2:
							g.setColor(new Color(255, 255, 255));
							break;
						case 3:
							g.setColor(new Color(255, 200, 0));
							break;
						case 4:
							g.setColor(new Color(0, 255, 255));
							break;
						case 5:
							break;
						case 6:
							break;
						case 7:
							break;
						case 8:
							break;
						}
						g.drawString("Banked " + coalBanked + " coal", x + 10,
								y += 15);
						switch (Color) {
						case 1:
							break;
						case 2:
							g.setColor(new Color(0, 0, 0));
							break;
						case 3:
							g.setColor(new Color(0, 0, 0));
							break;
						case 4:
							g.setColor(new Color(0, 255, 0));
							break;
						case 5:
							break;
						case 6:
							break;
						case 7:
							break;
						case 8:
							break;
						}
						g.drawString("Banked " + mithBanked + " mithril",
								x + 10, y += 15);
						switch (Color) {
						case 1:
							break;
						case 2:
							g.setColor(new Color(255, 255, 255));
							break;
						case 3:
							g.setColor(new Color(255, 200, 0));
							break;
						case 4:
							g.setColor(new Color(255, 255, 0));
							break;
						case 5:
							break;
						case 6:
							break;
						case 7:
							break;
						case 8:
							break;
						}
						g.drawString("Banked " + gemsBanked + " gems", x + 10,
								y += 15);
						if (superheat == 1) {
							g.drawString("Banked " + MithrilBarBanked
									+ " Mithril Bars", x + 10, y += 15);
						}
					}
					if (!powermine) {
						switch (Color) {
						case 1:
							break;
						case 2:
							g.setColor(new Color(0, 0, 0));
							break;
						case 3:
							g.setColor(new Color(0, 0, 0));
							break;
						case 4:
							g.setColor(new Color(255, 200, 0));
							break;
						case 5:
							break;
						case 6:
							break;
						case 7:
							break;
						case 8:
							break;
						}
						g.drawString("Gained "
								+ (coalBanked * priceOfCoal + mithBanked
										* priceOfMithril + MithrilBarBanked
										* priceOfMB) + "gp", x + 10, y += 15);
						// averaging __gp per hour
					}
					switch (Color) {
					case 1:
						break;
					case 2:
						g.setColor(new Color(255, 255, 255));
						break;
					case 3:
						g.setColor(new Color(255, 200, 0));
						break;
					case 4:
						g.setColor(new Color(255, 0, 255));
						break;
					case 5:
						break;
					case 6:
						break;
					case 7:
						break;
					case 8:
						break;
					}
					if (rocksteal == 1) {
						g.drawString("Rock-steal: Yes", x + 10, y += 15);
					}
					if (xrocksteal == 1) {
						g.drawString("Rock-steal: No", x + 10, y += 15);
					}
					if (rrocksteal == 1) {
						g.drawString("Rock-steal: Randomized", x + 10, y += 15);
					}
					switch (Color) {
					case 1:
						break;
					case 2:
						g.setColor(new Color(0, 0, 0));
						break;
					case 3:
						g.setColor(new Color(0, 0, 0));
						break;
					case 4:
						g.setColor(new Color(0, 0, 255));
						break;
					case 5:
						break;
					case 6:
						break;
					case 7:
						break;
					case 8:
						break;
					}
					g
							.drawString(
									"Currently level "
											+ skills
													.getCurrentSkillLevel(Constants.STAT_MINING)
											+ " and "
											+ skills
													.getPercentToNextLevel(Constants.STAT_MINING)
											+ "% to next level", x + 10,
									y += 15);
					switch (Color) {
					case 1:
						break;
					case 2:
						g.setColor(new Color(255, 255, 255));
						break;
					case 3:
						g.setColor(new Color(255, 200, 0));
						break;
					case 4:
						g.setColor(new Color(0, 255, 255));
						break;
					case 5:
						break;
					case 6:
						break;
					case 7:
						break;
					case 8:
						break;
					}
					g
							.drawString(
									"Gained "
											+ (skills
													.getCurrentSkillLevel(Constants.STAT_MINING) - startingLevel)
											+ " levels", x + 10, y += 15);
					// ___ until level (skills.getCurrentSkillLevel(STAT_MINING)
					// + 1)
					switch (Color) {
					case 1:
						break;
					case 2:
						g.setColor(new Color(0, 0, 0));
						break;
					case 3:
						g.setColor(new Color(0, 0, 0));
						break;
					case 4:
						g.setColor(new Color(0, 255, 0));
						break;
					case 5:
						break;
					case 6:
						break;
					case 7:
						break;
					case 8:
						break;
					}
					g
							.drawString(
									"Gained "
											+ (skills
													.getCurrentSkillExp(Constants.STAT_MINING) - startingExperience)
											+ " experience", x + 10, y += 15);
					// averaging ____ experience per hour
					switch (Color) {
					case 1:
						break;
					case 2:
						g.setColor(new Color(255, 255, 255));
						break;
					case 3:
						g.setColor(new Color(255, 200, 0));
						break;
					case 4:
						g.setColor(new Color(255, 255, 0));
						break;
					case 5:
						break;
					case 6:
						break;
					case 7:
						break;
					case 8:
						break;
					}
					g.drawString("Ran from combat " + ranAwayFromCombat
							+ " times", x + 10, y += 15);
					switch (Color) {
					case 1:
						break;
					case 2:
						g.setColor(new Color(0, 0, 0));
						break;
					case 3:
						g.setColor(new Color(0, 0, 0));
						break;
					case 4:
						g.setColor(new Color(255, 200, 0));
						break;
					case 5:
						break;
					case 6:
						break;
					case 7:
						break;
					case 8:
						break;
					}
					g.drawString(""
							+ (int) (skills
									.getXPToNextLevel(Constants.STAT_MINING)
									/ xpPerCoal + 1)
							+ " more coal until next level", x + 10, y += 15);
				}
				height = y + 3;

				if (rock3 != null) {
					if (tileOnScreen(rock3.getLocation())) {
						nextRock(g, rock3.getLocation(), new Color(0, 0, 255));
					} else {
						g.setColor(new Color(255, 0, 0, 100));
						g.fillOval(tileToMinimap(rock3.getLocation()).x - 5,
								tileToMinimap(rock3.getLocation()).y - 5, 10,
								10);
						g.drawString("NR",
								tileToMinimap(rock3.getLocation()).x + 5,
								tileToMinimap(rock3.getLocation()).y + 5);
					}
				}
				final Point cross = getMouseLocation();
				g.setColor(new Color(255, 255, 255, 170));
				g.drawLine(0, cross.y, 766, cross.y);
				g.drawLine(cross.x, 0, cross.x, 505);
			}
		}
	}

	// This is based off RCZhang's AIO Runecrafter:
	private void nextRock(final Graphics g, final RSTile t, final Color c) {
		final Point p = Calculations.tileToScreen(t);
		final Point pn = Calculations.tileToScreen(t.getX(), t.getY(), 0, 0, 0);
		final Point px = Calculations.tileToScreen(t.getX() + 1, t.getY(), 0,
				0, 0);
		final Point py = Calculations.tileToScreen(t.getX(), t.getY() + 1, 0,
				0, 0);
		final Point pxy = Calculations.tileToScreen(t.getX() + 1, t.getY() + 1,
				0, 0, 0);
		final Point[] points = { p, pn, px, py, pxy };
		for (final Point point : points) {
			if (!pointOnScreen(point)) {
				return;
			}
		}
		g.setColor(c);
		g.drawPolygon(new int[] { py.x, pxy.x, px.x, pn.x }, new int[] { py.y,
				pxy.y, px.y, pn.y }, 4);
		g.drawString("Next Rock", p.x - 30, p.y - 40);
	}

	public boolean onStart(final Map<String, String> args) {

		gui = new gui();
		gui.setVisible(true);
		final File settingsFile = new File("SexyGuildMiner.ini");
		if (settingsFile.exists()) {
			try {
				String pageSource = "";
				int begin = 0;
				int end = 0;
				BufferedReader in = new BufferedReader(new FileReader(
						"SexyGuildMiner.ini"));
				String inputLine = "";
				while ((inputLine = in.readLine()) != null) {
					pageSource += inputLine;
				}
				in.close();
				begin = pageSource.indexOf("mineMithacd")
						+ "mineMithacd".length();
				end = pageSource.indexOf("agcprior");
				gui.mineMith.setSelected(Boolean.parseBoolean(pageSource
						.substring(begin, end)));
				begin = pageSource.indexOf("priorMithacd")
						+ "priorMithacd".length();
				end = pageSource.indexOf("agcpower");
				gui.priorMith2.setSelected(Boolean.parseBoolean(pageSource
						.substring(begin, end)));
				begin = pageSource.indexOf("powermineacd")
						+ "powermineacd".length();
				end = pageSource.indexOf("agcW");
				gui.powermine2.setSelected(Boolean.parseBoolean(pageSource
						.substring(begin, end)));
				begin = pageSource.indexOf("WHacd") + "WHacd".length();
				end = pageSource.indexOf("agcWHn");
				gui.WH.setSelected(Boolean.parseBoolean(pageSource.substring(
						begin, end)));
				begin = pageSource.indexOf("WHnumberacd")
						+ "WHnumberacd".length();
				end = pageSource.indexOf("agcrost");
				gui.WHnumber.setText(pageSource.substring(begin, end));
				begin = pageSource.indexOf("rostoptionacd")
						+ "rostoptionacd".length();
				end = pageSource.indexOf("agcSuper");
				gui.rostoption
						.setSelectedItem(pageSource.substring(begin, end));
				begin = pageSource.indexOf("Superheatacd")
						+ "Superheatacd".length();
				end = pageSource.indexOf("agcHigh");
				gui.Superheat.setSelected(Boolean.parseBoolean(pageSource
						.substring(begin, end)));
				begin = pageSource.indexOf("Highalchacd")
						+ "Highalchacd".length();
				end = pageSource.indexOf("agcGo");
				gui.Highalch.setSelected(Boolean.parseBoolean(pageSource
						.substring(begin, end)));
				begin = pageSource.indexOf("Goalacd") + "Goalacd".length();
				end = pageSource.indexOf("agccmL");
				gui.Goal.setSelected(Boolean.parseBoolean(pageSource.substring(
						begin, end)));
				begin = pageSource.indexOf("agccmLevelacd")
						+ "agccmLevelacd".length();
				end = pageSource.indexOf("agcmLe");
				gui.cmLevel.setSelected(Boolean.parseBoolean(pageSource
						.substring(begin, end)));
				begin = pageSource.indexOf("agcmLevelacd")
						+ "agcmLevelacd".length();
				end = pageSource.indexOf("agccrM");
				gui.mLevel.setText(pageSource.substring(begin, end));
				begin = pageSource.indexOf("agccrMinedacd")
						+ "agccrMinedacd".length();
				end = pageSource.indexOf("agcrMi");
				gui.crMined.setSelected(Boolean.parseBoolean(pageSource
						.substring(begin, end)));
				begin = pageSource.indexOf("agcrMinedacd")
						+ "agcrMinedacd".length();
				end = pageSource.indexOf("agccT");
				gui.rMined.setText(pageSource.substring(begin, end));
				begin = pageSource.indexOf("cTimeacd") + "cTimeacd".length();
				end = pageSource.indexOf("agcho");
				gui.cTime.setSelected(Boolean.parseBoolean(pageSource
						.substring(begin, end)));
				begin = pageSource.indexOf("hoursacd") + "hoursacd".length();
				end = pageSource.indexOf("agcmi");
				gui.hours.setText(pageSource.substring(begin, end));
				begin = pageSource.indexOf("minsacd") + "minsacd".length();
				end = pageSource.indexOf("agcse");
				gui.mins.setText(pageSource.substring(begin, end));
				begin = pageSource.indexOf("secacd") + "secacd".length();
				end = pageSource.indexOf("agcPa");
				gui.secs.setText(pageSource.substring(begin, end));
				begin = pageSource.indexOf("PaintThemeacd")
						+ "PaintThemeacd".length();
				end = pageSource.indexOf("agcSave");
				gui.PaintTheme
						.setSelectedItem(pageSource.substring(begin, end));
				begin = pageSource.indexOf("SaveSettingsacd")
						+ "SaveSettingsacd".length();
				end = pageSource.indexOf("agcupd");
				gui.SaveSettings.setSelected(Boolean.parseBoolean(pageSource
						.substring(begin, end)));
				begin = pageSource.indexOf("updatecheckacd")
						+ "updatecheckacd".length();
				end = pageSource.indexOf("agcRes");
				gui.updatecheck.setSelected(Boolean.parseBoolean(pageSource
						.substring(begin, end)));
				begin = pageSource.indexOf("Restacd") + "Restacd".length();
				end = pageSource.indexOf("agcDelay1");
				gui.Rest.setSelected(Boolean.parseBoolean(pageSource.substring(
						begin, end)));
				begin = pageSource.indexOf("Delay1acd") + "Delay1acd".length();
				end = pageSource.indexOf("agcDelay2");
				gui.Delay1.setText(pageSource.substring(begin, end));
				begin = pageSource.indexOf("Delay2acd") + "Delay2acd".length();
				end = pageSource.indexOf("agcclanch");
				gui.Delay2.setText(pageSource.substring(begin, end));
				begin = pageSource.indexOf("clanchatcheckacd")
						+ "clanchatcheckacd".length();
				end = pageSource.indexOf("agcjclanch");
				gui.clanchatcheck.setSelected(Boolean.parseBoolean(pageSource
						.substring(begin, end)));
				begin = pageSource.indexOf("jclanchatacd")
						+ "jclanchatacd".length();
				end = pageSource.indexOf("agcRest2");
				gui.jclanchat.setText(pageSource.substring(begin, end));
				begin = pageSource.indexOf("Rest2acd") + "Rest2acd".length();
				end = pageSource.indexOf("agcpriorMithlvl");
				gui.Rest2.setSelected(Boolean.parseBoolean(pageSource
						.substring(begin, end)));
				begin = pageSource.indexOf("priorMithlvlacd")
						+ "priorMithlvlacd".length();
				end = pageSource.indexOf("agcchatres");
				gui.priorMithlvl.setSelectedItem(pageSource.substring(begin,
						end));
				begin = pageSource.indexOf("chatrespondacd")
						+ "chatrespondacd".length();
				end = pageSource.indexOf("agcabc");
				gui.chatrespond.setSelected(Boolean.parseBoolean(pageSource
						.substring(begin, end)));
			} catch (final Exception ignored) {
			}
		}
		while (!startScript) {
			wait(10);
		}
		if (getupdate == 1) {
			GU.run();
		}
		startTime = System.currentTimeMillis();
		priceOfCoal = getMarketPriceOfItem(coalID);
		priceOfMithril = getMarketPriceOfItem(mithrilID);
		priceOfMB = getMarketPriceOfItem(2359);
		return true;
	}

	public void printProgressReport() {
		if (startTime == 0) {
			startTime = System.currentTimeMillis();
		}
		if (startingLevel == 0 || startingExperience == 0) {
			startingLevel = skills.getCurrentSkillLevel(Constants.STAT_MINING);
			startingExperience = skills
					.getCurrentSkillExp(Constants.STAT_MINING);
		}
		long millis = System.currentTimeMillis() - startTime;
		final long hours = millis / (1000 * 60 * 60);
		millis -= hours * 1000 * 60 * 60;
		final long minutes = millis / (1000 * 60);
		millis -= minutes * 1000 * 60;
		final long seconds = millis / 1000;
		log("Sexy Guild Miner v" + getVersion());
		log("Running for " + hours + ":" + minutes + ":" + seconds);
		if (powermine) {
			log("Dropped " + coalDropped + " coal");
			log("Dropped " + mithDropped + " mithril");
			log("Dropped " + gemsDropped + " gems");
		} else {
			log("Banked " + coalBanked + " coal");
			log("Banked " + mithBanked + " mithril");
			log("Banked " + gemsBanked + " gems");
		}
		if (!powermine) {
			log("Gained "
					+ (coalBanked * priceOfCoal + mithBanked * priceOfMithril + MithrilBarBanked
							* priceOfMB) + "gp");
			// averaging __gp per hour
		}
		log("Currently level "
				+ skills.getCurrentSkillLevel(Constants.STAT_MINING) + " and "
				+ skills.getPercentToNextLevel(Constants.STAT_MINING)
				+ "% to next level");
		log("Gained "
				+ (skills.getCurrentSkillLevel(Constants.STAT_MINING) - startingLevel)
				+ " levels");
		// ___ until level (skills.getCurrentSkillLevel(STAT_MINING) + 1)
		log("Gained "
				+ (skills.getCurrentSkillExp(Constants.STAT_MINING) - startingExperience)
				+ " experience");
		// averaging ____ experience per hour
		log("Ran from combat " + ranAwayFromCombat + " times");
		log(""
				+ (int) (skills.getXPToNextLevel(Constants.STAT_MINING)
						/ xpPerCoal + 1) + " more coal until next level");
	}

	public int runAway() {
		log("Running away from combat...");
		setRun(true);
		if (getMyPlayer().getLocation().getY() > 6000) {
			if (!myWalkPath(coalToLadderPath, 17, 2)) {
				return random(200, 300);
			}
			waitUntilNotMoving();
			if (!climbUpLadder()) {
				climbUpLadder();
			}
			wait(random(2200, 3400));
		} else {
			final RSTile curentTile = getMyPlayer().getLocation();
			final int xRand = random(-16, 17);
			int yRand;
			if (random(0, 2) == 0) {
				yRand = -(int) Math.sqrt(16 * 16 - xRand * xRand);
			} else {
				yRand = -(int) Math.sqrt(16 * 16 - xRand * xRand);
			}
			final RSTile runawayTile = new RSTile(curentTile.getX() + xRand,
					curentTile.getY() + yRand);
			myWalkTile(runawayTile, 0);
			waitUntilNearTile(runawayTile, 0);
			wait(random(2200, 3400));
			myWalkTile(curentTile, 1);
			wait(random(50, 600));
			if (random(0, 4) < 3) {
				moveMouse(random(100, 415), random(100, 237));
			}
			waitUntilNotMoving();
		}
		runAway = false;
		ranAwayFromCombat++;
		log("Combat evaded.");
		return random(50, 100);
	}

	public int teleportToLumbridge() {
		if (distanceTo(lumbridgeTile) > 17) {
			castSpell(1);
			wait(random(1500, 2400));
			if (getMyPlayer().getAnimation() != -1) {
				wait(random(12500, 14000));
			} else {
				tries++;
				if (tries >= 4) {
					log("Unable to teleport to lumbridge, stopping script");
					printProgressReport();
					stopScript();
				}
				final int r = random(120000, 400000);
				log("Unable to teleport to lumbridge, will attempt again in "
						+ r / 1000 + "seconds");
				printProgressReport();
				logout();
				wait(r);
				login();
			}
		}
		myWalkTile(lumbridgeTile, 1);
		setRun(true);
		waitUntilNearTile(lumbridgeTile, 2);
		return random(100, 600);
	}

	public void unequipWeapon() {
		openTab(Constants.TAB_EQUIPMENT);
		final RSInterface equip = RSInterface
				.getInterface(Constants.INTERFACE_TAB_EQUIPMENT);
		final RSInterfaceChild weapon = equip.getChild(16);
		atInterface(weapon);
		wait(random(400, 800));
		openTab(Constants.TAB_INVENTORY);
	}

	public void waitUntilNearTile(final RSTile tile, final int dist) {
		wait(random(700, 1000));
		while (distanceTo(tile) > dist && getMyPlayer().isMoving()) {
			wait(random(25, 100));
		}
	}

	public void waitUntilNotMoving() {
		wait(random(700, 1000));
		while (getMyPlayer().isMoving()) {
			wait(random(25, 100));
		}
	}

	public int moveMouseoverobj() {
		rock = null;
		RSObject mithRock = findNearestUnoccupiedObject2(findObjects(16,
				mithRockID));
		RSObject coalRock = findNearestUnoccupiedObject2(findObjects(10,
				coalRockID));
		RSObject mithRock2 = getNearestObjectById(16, mithRockID);
		RSObject coalRock2 = getNearestObjectById(16, coalRockID);
		if ((miningMith || superheat == 1)
				&& (mithRock != null || mithRock2 != null)) {
			if (mithRock != null) {
				rock = mithRock;
			} else {
				if (mithRock2 != null) {
					if (distanceTo(mithRock2.getLocation()) > 1) {
						rock = mithRock2;
					} else {
						if (coalRock != null) {
							rock = coalRock;
						} else {
							rock = coalRock2;
						}
					}
				} else {
					if (coalRock != null) {
						rock = coalRock;
					} else {
						rock = coalRock2;
					}
				}
			}
		} else {
			if (coalRock != null) {
				rock = coalRock;
			} else {
				rock = coalRock2;
			}
		}
		if (!priorMith && miningMith && superheat == 0) {
			if (mithRock != null || mithRock2 != null || coalRock != null
					|| coalRock2 != null) {
				if (mithRock != null || coalRock != null) {
					if (mithRock == null && coalRock != null) {
						rock = coalRock;
					}
					if (mithRock != null && coalRock == null) {
						rock = mithRock;
					} else if (mithRock != null && coalRock != null) {
						if (distanceTo(mithRock.getLocation()) < distanceTo(coalRock
								.getLocation())) {
							rock = mithRock;
						} else {
							rock = coalRock;
						}
					}
				} else if (mithRock2 != null || coalRock2 != null) {
					if (mithRock2 == null && coalRock2 != null) {
						rock = coalRock2;
					}
					if (mithRock2 != null && coalRock2 == null) {
						rock = mithRock2;
					} else if (mithRock2 != null && coalRock2 != null) {
						if (distanceTo(mithRock2.getLocation()) < distanceTo(coalRock2
								.getLocation())) {
							rock = mithRock2;
						} else {
							rock = coalRock2;
						}
					}
				}
			}
		}

		if (rock == null) {
			return random(200, 300);
		}
		rock3 = rock;
		final Point Rockl = Calculations.tileToScreen(rock.getLocation());
		final Point Rockl2 = tileToMinimap(rock.getLocation());
		final Point mousel = getMouseLocation();
		if (Rockl.x == -1 || Rockl.y == -1) {
			if (Math.abs(Rockl2.x - mousel.x) <= 12
					&& Math.abs(Rockl2.y - mousel.y) <= 12) {
				return random(200, 300);
			} else {
				// moveMouse(628 + random(-30, 30), 89 + random(-30, 30));
				if (tileToMinimap(rock.getLocation()).getX() != -1
						&& tileToMinimap(rock.getLocation()).getY() != -1) {
					moveMouse(Rockl2.x + random(-5, 5), Rockl2.y
							+ random(-5, 5));
				}
				return random(200, 300);
			}
		}
		if (getMenuItems().get(0).toLowerCase().contains("ine R".toLowerCase())
				&& Math.abs(Rockl.x - mousel.x) <= 10
				&& Math.abs(Rockl.y - mousel.y) <= 10) {
			return random(200, 300);
		} else {
			moveMouse(Rockl.x, Rockl.y);
			return random(200, 300);
		}
		// return random(200, 300);
	}

	public int wieldPickaxe() {
		if (RSInterface.getInterface(Constants.INTERFACE_BANK).isValid()) {
			bank.close();
		}

		if (skills.getCurrentSkillLevel(Constants.STAT_ATTACK) >= 60
				&& inventoryContainsOneOf(pickaxeID[6])) {
			atInventoryItem(pickaxeID[6], "ield");
		} else if (skills.getCurrentSkillLevel(Constants.STAT_ATTACK) >= 40
				&& inventoryContainsOneOf(pickaxeID[5])) {
			atInventoryItem(pickaxeID[5], "ield");
		} else if (skills.getCurrentSkillLevel(Constants.STAT_ATTACK) >= 30
				&& inventoryContainsOneOf(pickaxeID[4])) {
			atInventoryItem(pickaxeID[4], "ield");
		} else if (skills.getCurrentSkillLevel(Constants.STAT_ATTACK) >= 20
				&& inventoryContainsOneOf(pickaxeID[3])) {
			atInventoryItem(pickaxeID[3], "ield");
		} else if (skills.getCurrentSkillLevel(Constants.STAT_ATTACK) >= 5
				&& inventoryContainsOneOf(pickaxeID[2])) {
			atInventoryItem(pickaxeID[2], "ield");
		} else if (skills.getCurrentSkillLevel(Constants.STAT_ATTACK) >= 1
				&& inventoryContainsOneOf(pickaxeID[1])) {
			atInventoryItem(pickaxeID[1], "ield");
		} else if (skills.getCurrentSkillLevel(Constants.STAT_ATTACK) >= 1
				&& inventoryContainsOneOf(pickaxeID[0])) {
			atInventoryItem(pickaxeID[0], "ield");
		}
		return random(200, 400);
	}

	public boolean SGMWalk(final RSTile t) {
		return SGMWalk(t, 2, 2);
	}

	public boolean SGMWalk(final RSTile t, final int x, final int y) {
		final Point p = tileToMinimap(t);
		if (p.x == -1 || p.y == -1) {
			if (fs <= 5) {
				final RSTile randTile = betweenBankAndLadderTiles[random(0,
						betweenBankAndLadderTiles.length)];
				fs++;
				return SGMWalk(randTile, x, y);
			}
			if (fs > 5) {
				WalkTileMM(getClosestTileOnMap(t), x, y);
				while (getMyPlayer().isMoving()) {
					wait(random(25, 100));
				}
				fs = 0;
				return true;
			}
		}
		clickMouse(p, x, y, true);
		fs = 0;
		return true;
	}

	private RSObject findNearestUnoccupiedObject2(final RSObject... objects) {
		RSObject nearestObj = null;
		for (final RSObject object : objects) {
			if (isObjectOccupied2(object)) {
				continue;
			}
			if (nearestObj == null) {
				nearestObj = object;
			} else if (distanceTo(object.getLocation()) < distanceTo(nearestObj
					.getLocation())) {
				nearestObj = object;
			}
		}
		return nearestObj;
	}

	private boolean isObjectOccupied2(final RSObject obj) {
		if (rocksteal == 1) {
			if (distanceTo(obj.getLocation()) <= 1) {
				return true;
			}
			return false;
		}
		if (xrocksteal == 1) {
			final int[] playerIndex = Bot.getClient().getRSPlayerIndexArray();
			final org.rsbot.accessors.RSPlayer[] players = Bot.getClient()
					.getRSPlayerArray();
			if (obj.getLocation() == null) {
				return true;
			}
			for (final int element : playerIndex) {
				if (players[element] == null) {
					continue;
				}
				final RSPlayer player = new RSPlayer(players[element]);
				try {
					if (distanceTo(obj.getLocation()) <= 1) {
						return true;
					}
					if (Methods.distanceBetween(obj.getLocation(), player
							.getLocation()) <= 1
							&& (player.getAnimation() == 624
									|| player.getAnimation() == 626 || player
									.getAnimation() == 628)) {
						return true;
					}
				} catch (final Exception ignored) {
				}
			}
			return false;
		}
		if (rrocksteal == 1) {
			switch (random(0, 1)) {
			case 0:
				if (distanceTo(obj.getLocation()) <= 1) {
					return true;
				}
				return false;

			case 1:
				final int[] playerIndex = Bot.getClient()
						.getRSPlayerIndexArray();
				final org.rsbot.accessors.RSPlayer[] players = Bot.getClient()
						.getRSPlayerArray();
				if (obj.getLocation() == null) {
					return true;
				}
				for (final int element : playerIndex) {
					if (players[element] == null) {
						continue;
					}
					final RSPlayer player = new RSPlayer(players[element]);
					try {
						if (distanceTo(obj.getLocation()) <= 1) {
							return true;
						}
						if (Methods.distanceBetween(obj.getLocation(), player
								.getLocation()) <= 1
								&& (player.getAnimation() == 624
										|| player.getAnimation() == 626 || player
										.getAnimation() == 628)) {
							return true;
						}
					} catch (final Exception ignored) {
					}
				}
				return false;

			default:
				break;

			}
		}
		return false;
	}

	public int Numberofarrays() {
		if (getMyPlayer().getLocation().getY() > 6000) {
			final int[] playerIndex = Bot.getClient().getRSPlayerIndexArray();
			final org.rsbot.accessors.RSPlayer[] players = Bot.getClient()
					.getRSPlayerArray();
			int numberinarray = 0;
			for (final int element : playerIndex) {
				if (players[element] == null) {
					continue;
				}
				numberinarray++;
			}
			return numberinarray;
		} else {
			return 0;
		}
	}

	public boolean Failwalk() {
		if (getMyPlayer().getLocation().getX() >= 3011
				&& getMyPlayer().getLocation().getX() <= 3022
				&& getMyPlayer().getLocation().getY() >= 3343
				&& getMyPlayer().getLocation().getY() <= 3349) {
			return true;
		}
		return false;
	}

	public int banking() {
	  RSNPC banker = getNearestNPCByID(bankerID);
	  RSObject bankbooth = getNearestObjectById(15, bankBoothID);
		if (!RSInterface.getInterface(Constants.INTERFACE_BANK).isValid()) {
			while ((System.currentTimeMillis() - bankopendelay) < 3000
					&& !bank.isOpen() && !getInterface(13).isValid()) {
				wait(10);
			}
			if (bank.isOpen() || getInterface(13).isValid()) {
				return random(200, 300);
			}
			final int r = random(0, 6);
			if (r == 0) {
				if (banker != null && banker.getLocation() != null) {
					while (!tileOnScreen(banker.getLocation())) {
						wait(10);
					}
					atNPC(banker, "ank Ba");
				}
			} else {
				if (bankbooth != null && bankbooth.getLocation() != null) {
					while (!tileOnScreen(bankbooth.getLocation())) {
						wait(10);
					}
					atObject(bankbooth, "uickl");
				}
			}
			while (!bank.isOpen() && getMyPlayer().isMoving()) {
				wait(100);
			}
			bankopendelay = System.currentTimeMillis();
			return random(200, 300);
		}
		return random(100, 200);
	}

	public int Superheat() {
		int abx = 0;
		int o = 0;
		while (getInventoryCount(453) >= 4 && getInventoryCount(447) >= 1
				&& abx < 10) {
			if (getCurrentTab() != TAB_MAGIC) {
				openTab(TAB_MAGIC);
			}
			castSpell(SPELL_SUPERHEAT_ITEM);
			while (getCurrentTab() != TAB_INVENTORY && o < 6) {
				wait(random(250, 750));
				o++;
			}
			o = 0;
			if (getCurrentTab() != TAB_INVENTORY) {
				openTab(TAB_INVENTORY);
			}
			if (!inventoryContains(561)) {
				log("You are out of nature runes...Stopping script...");
				logout();
				stopScript();
			}
			if (getInventoryCount(453) < 4 || getInventoryCount(447) < 1) {
				clickMouse(true);
				break;
			}
			if (!atInventoryItem(447, "Cast")) {
				abx++;
			}
			wait(random(2000, 2500));
			if (xsuperheat == 1) {
				log("You do not have the smithing lvl required...Stopping script...");
				logout();
				stopScript();
			}
			if (xfirer == 1) {
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
		return random(100, 200);
	}

	public int Highalch() {
		int abx = 0;
		int o = 0;
		while ((getInventoryCount(453) + getInventoryCount(447)
				+ getInventoryCount(1617) + getInventoryCount(1619)
				+ getInventoryCount(1621) + getInventoryCount(1623) + getInventoryCount(2359)) > 0) {
			if (getCurrentTab() != TAB_MAGIC) {
				openTab(TAB_MAGIC);
			}
			castSpell(SPELL_HIGH_LEVEL_ALCHEMY);
			while (getCurrentTab() != TAB_INVENTORY && o < 6) {
				wait(random(250, 750));
				o++;
			}
			o = 0;
			if (getCurrentTab() != TAB_INVENTORY) {
				openTab(TAB_INVENTORY);
			}
			if (!inventoryContains(561)) {
				log("You are out of nature runes...Stopping script...");
				logout();
				stopScript();
			}
			if (xfirer == 1) {
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
			if ((getInventoryCount(453) + getInventoryCount(447)
					+ getInventoryCount(1617) + getInventoryCount(1619)
					+ getInventoryCount(1621) + getInventoryCount(1623) + getInventoryCount(2359)) == 0) {
				break;
			}
			if (getInventoryCount(453) > 0) {
				atInventoryItem(453, "Cast");
				continue;
			}
			if (getInventoryCount(447) > 0) {
				atInventoryItem(447, "Cast");
				continue;
			}
			if (getInventoryCount(1617) > 0) {
				atInventoryItem(1617, "Cast");
				continue;
			}
			if (getInventoryCount(1619) > 0) {
				atInventoryItem(1619, "Cast");
				continue;
			}
			if (getInventoryCount(1621) > 0) {
				atInventoryItem(1621, "Cast");
				continue;
			}
			if (getInventoryCount(1623) > 0) {
				atInventoryItem(1623, "Cast");
				continue;
			}
			if (getInventoryCount(2359) > 0) {
				atInventoryItem(2359, "Cast");
				continue;
			}
		}
		if (abx >= 11) {
			log("Unable to cast High alchemy...Stopping script...");
			logout();
			stopScript();
		}
		return random(100, 200);
	}

	// Credits for the following two functions goes to SS7 ^^
	public boolean mouseInArea(int x, int y, int xx, int yy) {
		int x3 = input.getX();
		int y3 = input.getY();

		if (x3 < x && x3 > xx && y3 < y && y3 > yy) {
			return true;
		} else {
			return false;
		}
	}

	public boolean worldHop() {

		wait(random(1000, 2000));

		if (isLoggedIn()) {
			logout();
			while (isLoggedIn()) {
				wait(random(200, 400));
			}
		}
		final int x = random(345, 415);
		final int y = random(242, 250);

		moveMouse(x, y);
		wait(random(100, 200));
		if (mouseInArea(453, 250, 307, 237)) {
			clickMouse(true);
		} else {
			worldHop();
		}

		wait(random(1000, 2000));

		final int x2 = random(84, 296);
		final int y2 = random(148, 450);

		moveMouse(x2, y2);
		wait(random(100, 200));
		clickMouse(true);
		wait(random(100, 200));

		boolean didWeLogIn = true;
		wait(random(750, 1000));
		if (RSInterface.getInterface(744).getChild(468).isValid()) {

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
			} else if (RSText.contains("PvP")) {
				didWeLogIn = false;
			} else if (RSText.contains("World 113")) {
				didWeLogIn = false;
			} else if (RSText.contains("World 114")) {
				didWeLogIn = false;
			}

			if (!didWeLogIn) {
				return worldHop();
			}
		}
		return isLoginScreen();
	}

	public void joinclanchat() {
		int c = 0, m = 0;
		while (getInterface(589).getChild(0).containsText("Not in chat")) {
			while (getCurrentTab() != TAB_CLAN && c < 5) {
				openTab(TAB_CLAN);
				wait(random(500, 1000));
				c++;
			}
			if (c >= 5) {
				log("Unable to join clan chat");
				clanchat = "";
				return;
			}
			c = 0;
			wait(random(2000, 2500));
			if (!getInterface(589).getChild(0).containsText("Not in chat")) {
				return;
			}
			if (getInterface(589).getChild(11).containsText("Leave Chat")) {
				return;
			}
			atInterface(589, 2);
			wait(random(1000, 2000));
			sendText(clanchat, true);
			wait(random(2000, 3000));
			if (clanchatfail == 1) {
				log("Unable to join clan chat");
				clanchat = "";
				return;
			}
			m++;
			if (m >= 5) {
				log("Unable to join clan chat");
				clanchat = "";
				return;
			}
		}
		m = 0;
	}

	public void chatrespond() {
		int lastresponded = 0;
		int response;
		long waittorespond;
		long responddelay = random(500, 3000);
		boolean quickchatworld = false;
		if (RSInterface.getInterface(550).getChild(6).containsText("96")
				|| RSInterface.getInterface(550).getChild(6)
						.containsText("160")
				|| RSInterface.getInterface(550).getChild(6)
						.containsText("161")) {
			quickchatworld = true;
		}
		String playername = getMyPlayer().getName() + ": <col=0000ff>";
		int miningLevel = skills.getCurrentSkillLevel(Constants.STAT_MINING);
		// Responses to "What is your mining level?" and anything similar:
		String[] responses1 = { "My Mining Level is " + miningLevel,
				"" + miningLevel, "" + miningLevel + "..." };
		// Responses to "My Mining Level is ..." and anything similar:
		String[] responses2 = { "Nice lol", "Cool", "Amazing" };
		// Responses to "Hello" and anything similar:
		String[] responses3 = { "Hey", "Hi", "Hello" };
		// Triggers responding:
		String[] triggers = { "wad", "what", "what's", "you", "mining",
				"minin", "lvl", "level", "hey", "hi", "yo", "hello", "hi",
				"good day", "nice to meet you", "what's up", "my", "mine", "?" };
		for (int i = 0; i < getInterface(137).getChildCount() - 1; i++) {
			if (getInterface(137).getChild(i).containsText(playername)) {
				lastresponded = i;
			}
		}
		for (int i = lastresponded; i < getInterface(137).getChildCount() - 1; i++) {
			if (i <= lastresponded
					|| getInterface(137).getChild(i).getText() == null) {
				continue;
			}
			if (getInterface(137).getChild(i).getText().toLowerCase().contains(
					triggers[3])
					|| getInterface(137).getChild(i).getText().toLowerCase()
							.contains(triggers[18])) {
				for (int x = 0; x < 3; x++) {
					if (getInterface(137).getChild(i).getText().toLowerCase()
							.contains(triggers[x])) {
						for (int y = 4; y < 6; y++) {
							for (int z = 6; z < 8; z++) {
								if (getInterface(137).getChild(i).getText()
										.toLowerCase().contains(triggers[y])
										&& getInterface(137).getChild(i)
												.getText().toLowerCase()
												.contains(triggers[z])) {
									for (int v = 0; v < responses1.length; v++) {
										if (getInterface(137).getChild(
												lastresponded).getText()
												.toLowerCase().contains(
														responses1[v]
																.toLowerCase())) {
											return;
										}
									}
									if (!quickchatworld) {
										response = random(1,
												1 + responses1.length);
									} else {
										response = 1;
									}
									waittorespond = System.currentTimeMillis();
									while (System.currentTimeMillis()
											- waittorespond < responddelay
											&& !mineNewRock()) {
									}
									if (response == 1) {
										sendText("", true);
										wait(random(600, 700));
										sendText("s", false);
										wait(random(600, 700));
										sendText("i", false);
										wait(random(600, 700));
										sendText("2", false);
										wait(random(600, 700));
										return;
									} else {
										sendText(responses1[response - 2], true);
										return;
									}
								}
							}
						}
					}
				}
			}
			if (getInterface(137).getChild(i).getText().toLowerCase().contains(
					triggers[16])
					|| getInterface(137).getChild(i).getText().toLowerCase()
							.contains(triggers[17])) {
				for (int y = 4; y < 6; y++) {
					for (int z = 6; z < 8; z++) {
						if (getInterface(137).getChild(i).getText()
								.toLowerCase().contains(triggers[y])
								&& getInterface(137).getChild(i).getText()
										.toLowerCase().contains(triggers[z])) {
							for (int v = 0; v < responses2.length; v++) {
								if (getInterface(137).getChild(lastresponded)
										.getText().toLowerCase().contains(
												responses2[v].toLowerCase())) {
									return;
								}
							}
							if (!quickchatworld) {
								response = random(1, 4 + responses2.length);
							} else {
								response = random(1, 4);
							}
							waittorespond = System.currentTimeMillis();
							while (System.currentTimeMillis() - waittorespond < responddelay
									&& !mineNewRock()) {
							}
							if (response == 1) {
								sendText("", true);
								wait(random(600, 700));
								sendText("g", false);
								wait(random(600, 700));
								sendText("c", false);
								wait(random(600, 700));
								sendText("g", false);
								wait(random(600, 700));
								sendText("1", false);
								wait(random(600, 700));
								return;
							}
							if (response == 2) {
								sendText("", true);
								wait(random(600, 700));
								sendText("g", false);
								wait(random(600, 700));
								sendText("c", false);
								wait(random(600, 700));
								sendText("e", false);
								wait(random(600, 700));
								sendText("3", false);
								wait(random(600, 700));
								return;
							}
							if (response == 3) {
								sendText("", true);
								wait(random(600, 700));
								sendText("g", false);
								wait(random(600, 700));
								sendText("c", false);
								wait(random(600, 700));
								sendText("e", false);
								wait(random(600, 700));
								sendText("4", false);
								wait(random(600, 700));
								return;
							}
							if (response == 4) {
								sendText("", true);
								wait(random(600, 700));
								sendText("g", false);
								wait(random(600, 700));
								sendText("c", false);
								wait(random(600, 700));
								sendText("e", false);
								wait(random(600, 700));
								sendText("9", false);
								wait(random(600, 700));
								return;
							} else {
								sendText(responses2[response - 5], true);
								return;
							}
						}
					}
				}
			}
			for (int w = 8; w < 16; w++) {
				if (getInterface(137).getChild(i).getText().toLowerCase()
						.equals(triggers[w])) {
					for (int v = 0; v < responses3.length; v++) {
						if (getInterface(137).getChild(lastresponded).getText()
								.toLowerCase().contains(
										responses3[v].toLowerCase())) {
							return;
						}
					}
					if (!quickchatworld) {
						response = random(1, 9 + responses3.length);
					} else {
						response = random(1, 9);
					}
					waittorespond = System.currentTimeMillis();
					while (System.currentTimeMillis() - waittorespond < responddelay
							&& !mineNewRock()) {
					}
					if (response < 10) {
						sendText("", true);
						wait(random(600, 700));
						sendText("g", false);
						wait(random(600, 700));
						sendText("h", false);
						wait(random(600, 700));
						sendText("" + response, false);
						wait(random(600, 700));
						return;
					} else {
						sendText(responses3[response - 10], true);
						return;
					}
				}
			}
		}
	}

	String svrmsg;

	public void serverMessageRecieved(ServerMessageEvent e) {
		svrmsg = e.getMessage();
		if (svrmsg.contains("at least")) {
			xsuperheat = 1;
		}
		if (svrmsg.contains("enough fire")) {
			xfirer = 1;
		}
		if (svrmsg.contains("channel you tried to join does not")) {
			clanchatfail = 1;
		}
	}

	public boolean WalkTileMM(final RSTile t) {
		return WalkTileMM(t, 2, 2);
	}

	public boolean WalkTileMM(final RSTile t, final int x, final int y) {
		final Point p = tileToMinimap(t);
		if (p.x == -1 || p.y == -1) {
			return WalkTileMM(getClosestTileOnMap(t), x, y);
		}
		clickMouse(p, x, y, true);
		return true;
	}

	public boolean isCameraMoving(int time, int maxDist) {
		int CamX = Bot.getClient().getCamPosX();
		int CamY = Bot.getClient().getCamPosY();
		int CamZ = Bot.getClient().getCamPosZ();
		wait(time);
		if (Math.abs(Bot.getClient().getCamPosX() - CamX) > maxDist) {
			return true;
		}
		if (Math.abs(Bot.getClient().getCamPosY() - CamY) > maxDist) {
			return true;
		}
		if (Math.abs(Bot.getClient().getCamPosZ() - CamZ) > maxDist) {
			return true;
		}
		return false;
	}

	public class gui extends javax.swing.JFrame {
		private static final long serialVersionUID = 1L;
		/** Creates new form gui */
		public gui() {
			initComponents();
		}

		/**
		 * This method is called from within the constructor to initialize the
		 * form. WARNING: Do NOT modify this code. The content of this method is
		 * always regenerated by the Form Editor.
		 */
		// <editor-fold defaultstate="collapsed" desc="Generated Code">
		private void initComponents() {

			Title = new javax.swing.JLabel();
			Version = new javax.swing.JLabel();
			mineMith = new javax.swing.JCheckBox();
			priorMith2 = new javax.swing.JCheckBox();
			powermine2 = new javax.swing.JCheckBox();
			WH = new javax.swing.JCheckBox();
			WHnumber = new javax.swing.JTextField();
			people = new javax.swing.JLabel();
			RockSteal = new javax.swing.JLabel();
			rostoption = new javax.swing.JComboBox();
			Rest = new javax.swing.JCheckBox();
			Superheat = new javax.swing.JCheckBox();
			Highalch = new javax.swing.JCheckBox();
			Goal = new javax.swing.JCheckBox();
			cmLevel = new javax.swing.JCheckBox();
			mLevel = new javax.swing.JTextField();
			Goal2Mining = new javax.swing.JLabel();
			crMined = new javax.swing.JCheckBox();
			rMined = new javax.swing.JTextField();
			Goal3OresMined = new javax.swing.JLabel();
			cTime = new javax.swing.JCheckBox();
			hours = new javax.swing.JTextField();
			colon1 = new javax.swing.JLabel();
			mins = new javax.swing.JTextField();
			colon2 = new javax.swing.JLabel();
			secs = new javax.swing.JTextField();
			format = new javax.swing.JLabel();
			Start = new javax.swing.JButton();
			SaveSettings = new javax.swing.JCheckBox();
			PaintThemeText = new javax.swing.JLabel();
			PaintTheme = new javax.swing.JComboBox();
			updatecheck = new javax.swing.JCheckBox();
			Delay1Text = new javax.swing.JLabel();
			Delay1 = new javax.swing.JTextField();
			Delay2Text = new javax.swing.JLabel();
			Delay2 = new javax.swing.JTextField();
			clanchatcheck = new javax.swing.JCheckBox();
			jclanchat = new javax.swing.JTextField();
			Rest2 = new javax.swing.JCheckBox();
			mstext1 = new javax.swing.JLabel();
			mstext2 = new javax.swing.JLabel();
			priorMithlvl = new javax.swing.JComboBox();
			chatrespond = new javax.swing.JCheckBox();

			setTitle("Sexy Guild Miner");
			setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

			Title.setFont(new java.awt.Font("Tahoma", 3, 24));
			Title.setForeground(new java.awt.Color(153, 51, 255));
			Title.setText("Sexy Guild Miner");

			Version.setText("v" + getVersion());

			mineMith.setText("Mine mithril rocks");
			mineMith.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					mineMithActionPerformed(evt);
				}
			});

			priorMith2.setText("Prioritize Mith Ores Lvl:");

			powermine2.setText("Powermine");

			WH.setText("WorldHop if more than");

			WHnumber.setText("8");

			people.setText(" people(Includes you)");

			RockSteal.setText("Rock Steal?");

			rostoption.setModel(new javax.swing.DefaultComboBoxModel(
					new String[] { "No", "Yes", "Randomized" }));

			Rest.setText("Rest(Walk) on the way to bank and back");

			Superheat.setText("Superheat Mithril Bars");

			Highalch.setText("Auto High-Alchemy");

			Goal.setText("Set a Goal: Logout");

			cmLevel.setText("After Level");

			mLevel.setText("0");

			Goal2Mining.setText("Mining");

			crMined.setText("After");

			rMined.setText("0");

			Goal3OresMined.setText("Ores Mined");

			cTime.setText("After");

			hours.setText("0");

			colon1.setText(":");

			mins.setText("0");

			colon2.setText(":");

			secs.setText("0");

			format.setText("(hrs:mins:secs)");

			Start.setText("Start");
			Start.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					StartActionPerformed(evt);
				}
			});

			SaveSettings.setText("Save Settings");

			PaintThemeText.setText("Paint Theme:");

			PaintTheme.setModel(new javax.swing.DefaultComboBoxModel(
					new String[] { "Classic", "Zebra", "Tiger", "Rainbow",
							"Yellow & Black", "Red & Black", "Cyan & Blue",
							"Lime & Black" }));

			updatecheck.setText("Check for Updates");

			Delay1Text.setText("Delay before clicking the next rock:");

			Delay1.setText("0");

			Delay2Text.setText("Delay before finding the next rock again:");

			Delay2.setText("0");

			clanchatcheck.setText("Join Clan Chat:");

			Rest2.setText("Rest(Walk) when below 15-25 energy");

			mstext1.setText("ms");

			mstext2.setText("ms");

			priorMithlvl.setModel(new javax.swing.DefaultComboBoxModel(
					new String[] { "1", "2" }));

			chatrespond.setText("Chat Responder");

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
																					229,
																					229,
																					229)
																			.addComponent(
																					Version))
															.addGroup(
																	layout
																			.createSequentialGroup()
																			.addContainerGap()
																			.addComponent(
																					mineMith)
																			.addPreferredGap(
																					javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																			.addComponent(
																					priorMith2)
																			.addPreferredGap(
																					javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																			.addComponent(
																					priorMithlvl,
																					javax.swing.GroupLayout.PREFERRED_SIZE,
																					javax.swing.GroupLayout.DEFAULT_SIZE,
																					javax.swing.GroupLayout.PREFERRED_SIZE))
															.addGroup(
																	layout
																			.createSequentialGroup()
																			.addGap(
																					40,
																					40,
																					40)
																			.addComponent(
																					Title,
																					javax.swing.GroupLayout.PREFERRED_SIZE,
																					209,
																					javax.swing.GroupLayout.PREFERRED_SIZE))
															.addGroup(
																	layout
																			.createSequentialGroup()
																			.addContainerGap()
																			.addGroup(
																					layout
																							.createParallelGroup(
																									javax.swing.GroupLayout.Alignment.LEADING)
																							.addGroup(
																									layout
																											.createSequentialGroup()
																											.addComponent(
																													WH,
																													javax.swing.GroupLayout.PREFERRED_SIZE,
																													133,
																													javax.swing.GroupLayout.PREFERRED_SIZE)
																											.addPreferredGap(
																													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																											.addComponent(
																													WHnumber,
																													javax.swing.GroupLayout.PREFERRED_SIZE,
																													25,
																													javax.swing.GroupLayout.PREFERRED_SIZE)
																											.addPreferredGap(
																													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																											.addComponent(
																													people))
																							.addGroup(
																									layout
																											.createSequentialGroup()
																											.addPreferredGap(
																													javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																													4,
																													javax.swing.GroupLayout.PREFERRED_SIZE)
																											.addComponent(
																													RockSteal)
																											.addPreferredGap(
																													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																											.addComponent(
																													rostoption,
																													javax.swing.GroupLayout.PREFERRED_SIZE,
																													javax.swing.GroupLayout.DEFAULT_SIZE,
																													javax.swing.GroupLayout.PREFERRED_SIZE))
																							.addComponent(
																									powermine2)))
															.addGroup(
																	layout
																			.createSequentialGroup()
																			.addContainerGap()
																			.addGroup(
																					layout
																							.createParallelGroup(
																									javax.swing.GroupLayout.Alignment.LEADING)
																							.addComponent(
																									Rest)
																							.addGroup(
																									layout
																											.createSequentialGroup()
																											.addComponent(
																													clanchatcheck)
																											.addPreferredGap(
																													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																											.addComponent(
																													jclanchat,
																													javax.swing.GroupLayout.PREFERRED_SIZE,
																													100,
																													javax.swing.GroupLayout.PREFERRED_SIZE))
																							.addComponent(
																									Superheat)
																							.addComponent(
																									Highalch)
																							.addComponent(
																									Rest2)
																							.addComponent(
																									chatrespond)
																							.addGroup(
																									layout
																											.createSequentialGroup()
																											.addGap(
																													21,
																													21,
																													21)
																											.addGroup(
																													layout
																															.createParallelGroup(
																																	javax.swing.GroupLayout.Alignment.LEADING)
																															.addGroup(
																																	layout
																																			.createSequentialGroup()
																																			.addPreferredGap(
																																					javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																																			.addComponent(
																																					cTime)
																																			.addPreferredGap(
																																					javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																																			.addComponent(
																																					hours,
																																					javax.swing.GroupLayout.PREFERRED_SIZE,
																																					25,
																																					javax.swing.GroupLayout.PREFERRED_SIZE)
																																			.addPreferredGap(
																																					javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																																			.addComponent(
																																					colon1)
																																			.addPreferredGap(
																																					javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																																			.addComponent(
																																					mins,
																																					javax.swing.GroupLayout.PREFERRED_SIZE,
																																					25,
																																					javax.swing.GroupLayout.PREFERRED_SIZE)
																																			.addPreferredGap(
																																					javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																																			.addComponent(
																																					colon2)
																																			.addPreferredGap(
																																					javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																																			.addComponent(
																																					secs,
																																					javax.swing.GroupLayout.PREFERRED_SIZE,
																																					25,
																																					javax.swing.GroupLayout.PREFERRED_SIZE))
																															.addGroup(
																																	layout
																																			.createSequentialGroup()
																																			.addComponent(
																																					cmLevel)
																																			.addPreferredGap(
																																					javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																																			.addComponent(
																																					mLevel,
																																					javax.swing.GroupLayout.PREFERRED_SIZE,
																																					25,
																																					javax.swing.GroupLayout.PREFERRED_SIZE)
																																			.addPreferredGap(
																																					javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																																			.addComponent(
																																					Goal2Mining))
																															.addGroup(
																																	layout
																																			.createSequentialGroup()
																																			.addComponent(
																																					crMined)
																																			.addPreferredGap(
																																					javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																																			.addComponent(
																																					rMined,
																																					javax.swing.GroupLayout.PREFERRED_SIZE,
																																					25,
																																					javax.swing.GroupLayout.PREFERRED_SIZE)
																																			.addPreferredGap(
																																					javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																																			.addComponent(
																																					Goal3OresMined)))
																											.addPreferredGap(
																													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																											.addComponent(
																													format))
																							.addGroup(
																									layout
																											.createSequentialGroup()
																											.addComponent(
																													Delay1Text)
																											.addPreferredGap(
																													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																											.addComponent(
																													Delay1,
																													javax.swing.GroupLayout.PREFERRED_SIZE,
																													35,
																													javax.swing.GroupLayout.PREFERRED_SIZE)
																											.addPreferredGap(
																													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																											.addComponent(
																													mstext1))
																							.addGroup(
																									layout
																											.createSequentialGroup()
																											.addComponent(
																													Delay2Text)
																											.addPreferredGap(
																													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																											.addComponent(
																													Delay2,
																													javax.swing.GroupLayout.PREFERRED_SIZE,
																													35,
																													javax.swing.GroupLayout.PREFERRED_SIZE)
																											.addPreferredGap(
																													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																											.addComponent(
																													mstext2))
																							.addGroup(
																									layout
																											.createSequentialGroup()
																											.addComponent(
																													PaintThemeText)
																											.addPreferredGap(
																													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																											.addComponent(
																													PaintTheme,
																													javax.swing.GroupLayout.PREFERRED_SIZE,
																													javax.swing.GroupLayout.DEFAULT_SIZE,
																													javax.swing.GroupLayout.PREFERRED_SIZE))
																							.addGroup(
																									layout
																											.createSequentialGroup()
																											.addComponent(
																													Start)
																											.addPreferredGap(
																													javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																											.addComponent(
																													updatecheck)
																											.addGap(
																													2,
																													2,
																													2)
																											.addComponent(
																													SaveSettings))
																							.addComponent(
																									Goal))))
											.addContainerGap(
													javax.swing.GroupLayout.DEFAULT_SIZE,
													Short.MAX_VALUE)));
			layout
					.setVerticalGroup(layout
							.createParallelGroup(
									javax.swing.GroupLayout.Alignment.LEADING)
							.addGroup(
									layout
											.createSequentialGroup()
											.addContainerGap()
											.addComponent(Title)
											.addPreferredGap(
													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
											.addGroup(
													layout
															.createParallelGroup(
																	javax.swing.GroupLayout.Alignment.TRAILING)
															.addGroup(
																	layout
																			.createSequentialGroup()
																			.addComponent(
																					Version)
																			.addGap(
																					18,
																					18,
																					18)
																			.addComponent(
																					mineMith))
															.addGroup(
																	layout
																			.createParallelGroup(
																					javax.swing.GroupLayout.Alignment.BASELINE)
																			.addComponent(
																					priorMith2)
																			.addComponent(
																					priorMithlvl,
																					javax.swing.GroupLayout.PREFERRED_SIZE,
																					javax.swing.GroupLayout.DEFAULT_SIZE,
																					javax.swing.GroupLayout.PREFERRED_SIZE)))
											.addGap(0, 0, 0)
											.addComponent(
													powermine2,
													javax.swing.GroupLayout.PREFERRED_SIZE,
													23,
													javax.swing.GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(
													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
											.addGroup(
													layout
															.createParallelGroup(
																	javax.swing.GroupLayout.Alignment.LEADING)
															.addGroup(
																	layout
																			.createParallelGroup(
																					javax.swing.GroupLayout.Alignment.BASELINE)
																			.addComponent(
																					WHnumber,
																					javax.swing.GroupLayout.PREFERRED_SIZE,
																					20,
																					javax.swing.GroupLayout.PREFERRED_SIZE)
																			.addComponent(
																					people))
															.addComponent(WH))
											.addPreferredGap(
													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
											.addGroup(
													layout
															.createParallelGroup(
																	javax.swing.GroupLayout.Alignment.BASELINE)
															.addComponent(
																	RockSteal)
															.addComponent(
																	rostoption,
																	javax.swing.GroupLayout.PREFERRED_SIZE,
																	javax.swing.GroupLayout.DEFAULT_SIZE,
																	javax.swing.GroupLayout.PREFERRED_SIZE))
											.addPreferredGap(
													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
											.addComponent(Rest2)
											.addPreferredGap(
													javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
											.addComponent(Rest)
											.addPreferredGap(
													javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
											.addComponent(Superheat)
											.addPreferredGap(
													javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
											.addComponent(Highalch)
											.addGap(2, 2, 2)
											.addGroup(
													layout
															.createParallelGroup(
																	javax.swing.GroupLayout.Alignment.BASELINE)
															.addComponent(
																	jclanchat,
																	javax.swing.GroupLayout.PREFERRED_SIZE,
																	20,
																	javax.swing.GroupLayout.PREFERRED_SIZE)
															.addComponent(
																	clanchatcheck))
											.addGap(2, 2, 2)
											.addComponent(chatrespond)
											.addPreferredGap(
													javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
											.addComponent(Goal)
											.addPreferredGap(
													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
											.addGroup(
													layout
															.createParallelGroup(
																	javax.swing.GroupLayout.Alignment.BASELINE)
															.addComponent(
																	cmLevel)
															.addComponent(
																	mLevel,
																	javax.swing.GroupLayout.PREFERRED_SIZE,
																	20,
																	javax.swing.GroupLayout.PREFERRED_SIZE)
															.addComponent(
																	Goal2Mining))
											.addPreferredGap(
													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
											.addGroup(
													layout
															.createParallelGroup(
																	javax.swing.GroupLayout.Alignment.BASELINE)
															.addComponent(
																	crMined)
															.addComponent(
																	rMined,
																	javax.swing.GroupLayout.PREFERRED_SIZE,
																	20,
																	javax.swing.GroupLayout.PREFERRED_SIZE)
															.addComponent(
																	Goal3OresMined))
											.addPreferredGap(
													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
											.addGroup(
													layout
															.createParallelGroup(
																	javax.swing.GroupLayout.Alignment.BASELINE)
															.addComponent(cTime)
															.addComponent(
																	hours,
																	javax.swing.GroupLayout.PREFERRED_SIZE,
																	20,
																	javax.swing.GroupLayout.PREFERRED_SIZE)
															.addComponent(
																	colon1)
															.addComponent(
																	mins,
																	javax.swing.GroupLayout.PREFERRED_SIZE,
																	20,
																	javax.swing.GroupLayout.PREFERRED_SIZE)
															.addComponent(
																	colon2)
															.addComponent(
																	secs,
																	javax.swing.GroupLayout.PREFERRED_SIZE,
																	20,
																	javax.swing.GroupLayout.PREFERRED_SIZE)
															.addComponent(
																	format))
											.addPreferredGap(
													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
											.addGroup(
													layout
															.createParallelGroup(
																	javax.swing.GroupLayout.Alignment.BASELINE)
															.addComponent(
																	Delay1Text)
															.addComponent(
																	Delay1,
																	javax.swing.GroupLayout.PREFERRED_SIZE,
																	20,
																	javax.swing.GroupLayout.PREFERRED_SIZE)
															.addComponent(
																	mstext1))
											.addPreferredGap(
													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
											.addGroup(
													layout
															.createParallelGroup(
																	javax.swing.GroupLayout.Alignment.BASELINE)
															.addComponent(
																	Delay2Text)
															.addComponent(
																	Delay2,
																	javax.swing.GroupLayout.PREFERRED_SIZE,
																	20,
																	javax.swing.GroupLayout.PREFERRED_SIZE)
															.addComponent(
																	mstext2))
											.addPreferredGap(
													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
											.addGroup(
													layout
															.createParallelGroup(
																	javax.swing.GroupLayout.Alignment.BASELINE)
															.addComponent(
																	PaintThemeText,
																	javax.swing.GroupLayout.PREFERRED_SIZE,
																	14,
																	javax.swing.GroupLayout.PREFERRED_SIZE)
															.addComponent(
																	PaintTheme,
																	javax.swing.GroupLayout.PREFERRED_SIZE,
																	javax.swing.GroupLayout.DEFAULT_SIZE,
																	javax.swing.GroupLayout.PREFERRED_SIZE))
											.addPreferredGap(
													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
											.addGroup(
													layout
															.createParallelGroup(
																	javax.swing.GroupLayout.Alignment.BASELINE)
															.addComponent(Start)
															.addComponent(
																	SaveSettings)
															.addComponent(
																	updatecheck))
											.addContainerGap(
													javax.swing.GroupLayout.DEFAULT_SIZE,
													Short.MAX_VALUE)));

			pack();
		}// </editor-fold>

		private void mineMithActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_mineMithActionPerformed

		}// GEN-LAST:event_mineMithActionPerformed

		private void StartActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_StartActionPerformed
			if (mineMith.isSelected()) {
				miningMith = true;
			} else {
				miningMith = false;
			}
			if (priorMith2.isSelected()) {
				priorMith = true;
				if (priorMithlvl.getSelectedItem().equals("2")) {
					priorMith3 = true;
				}
			}
			if (powermine2.isSelected()) {
				powermine = true;
			}
			if (WH.isSelected()) {
				WHnb = Integer.parseInt(WHnumber.getText()) - 1;
			}
			String rostoption2 = (String) rostoption.getSelectedItem();
			if (rostoption2.equals("No")) {
				xrocksteal = 1;
			}
			if (rostoption2.equals("Yes")) {
				rocksteal = 1;
			}
			if (rostoption2.equals("Randomized")) {
				rrocksteal = 1;
			}
			if (Rest2.isSelected()) {
				rest2 = 1;
			}
			if (Rest.isSelected()) {
				rest = 1;
			}
			if (Superheat.isSelected()) {
				superheat = 1;
			}
			if (Highalch.isSelected()) {
				highalch = 1;
			}
			if (clanchatcheck.isSelected()) {
				clanchat = jclanchat.getText();
			}
			if (chatrespond.isSelected()) {
				respondtochat = true;
			}
			if (Goal.isSelected()) {
				if (cmLevel.isSelected()) {
					Goal2 = 1;
					mLevel2 = Integer.parseInt(mLevel.getText());
				}
				if (crMined.isSelected()) {
					Goal3 = 1;
					oMined2 = Integer.parseInt(rMined.getText());
				}
				if (cTime.isSelected()) {
					Goal4 = 1;
					Time2 = (Integer.parseInt(hours.getText()) * 60 * 60 * 1000)
							+ (Integer.parseInt(mins.getText()) * 60 * 1000)
							+ (Integer.parseInt(secs.getText()) * 1000);
				}
			}
			String PaintTheme2 = (String) PaintTheme.getSelectedItem();
			if (PaintTheme2.equals("Classic")) {
				Color = 1;
			}
			if (PaintTheme2.equals("Zebra")) {
				Color = 2;
			}
			if (PaintTheme2.equals("Tiger")) {
				Color = 3;
			}
			if (PaintTheme2.equals("Rainbow")) {
				Color = 4;
			}
			if (PaintTheme2.equals("Yellow & Black")) {
				Color = 5;
			}
			if (PaintTheme2.equals("Red & Black")) {
				Color = 6;
			}
			if (PaintTheme2.equals("Cyan & Blue")) {
				Color = 7;
			}
			if (PaintTheme2.equals("Lime & Black")) {
				Color = 8;
			}
			if (updatecheck.isSelected()) {
				getupdate = 1;
			}
			if (SaveSettings.isSelected()) {
				settings = "mineMithacd" + mineMith.isSelected() + "agc";
				settings += "priorMithacd" + priorMith2.isSelected() + "agc";
				settings += "powermineacd" + powermine2.isSelected() + "agc";
				settings += "WHacd" + WH.isSelected() + "agc";
				settings += "WHnumberacd" + WHnumber.getText() + "agc";
				settings += "rostoptionacd" + rostoption2 + "agc";
				settings += "Superheatacd" + Superheat.isSelected() + "agc";
				settings += "Highalchacd" + Highalch.isSelected() + "agc";
				settings += "Goalacd" + Goal.isSelected() + "agc";
				settings += "cmLevelacd" + cmLevel.isSelected() + "agc";
				settings += "mLevelacd" + mLevel.getText() + "agc";
				settings += "crMinedacd" + crMined.isSelected() + "agc";
				settings += "rMinedacd" + rMined.getText() + "agc";
				settings += "cTimeacd" + cTime.isSelected() + "agc";
				settings += "hoursacd" + hours.getText() + "agc";
				settings += "minsacd" + mins.getText() + "agc";
				settings += "secacd" + secs.getText() + "agc";
				settings += "PaintThemeacd" + PaintTheme2 + "agc";
				settings += "SaveSettingsacd" + SaveSettings.isSelected()
						+ "agc";
				settings += "updatecheckacd" + updatecheck.isSelected() + "agc";
				settings += "Restacd" + Rest.isSelected() + "agc";
				settings += "Delay1acd" + Delay1.getText() + "agc";
				settings += "Delay2acd" + Delay2.getText() + "agc";
				settings += "clanchatcheckacd" + clanchatcheck.isSelected()
						+ "agc";
				settings += "jclanchatacd" + jclanchat.getText() + "agc";
				settings += "Rest2acd" + Rest2.isSelected() + "agc";
				settings += "priorMithlvlacd" + priorMithlvl.getSelectedItem()
						+ "agc";
				settings += "chatrespondacd" + chatrespond.isSelected()
						+ "agcabc";
				try {
					FileWriter fstream = new FileWriter("SexyGuildMiner.ini");
					BufferedWriter out = new BufferedWriter(fstream);
					out.write(settings);
					out.close();
				} catch (Exception e) {
				}
			}
			delay1 = Integer.parseInt(Delay1.getText());
			delay2 = Integer.parseInt(Delay2.getText());
			startScript = true;
			gui.setVisible(false);
		}// GEN-LAST:event_StartActionPerformed

		/**
		 * @param args
		 *            the command line arguments
		 */
		// public static void main(String args[]) {
		// java.awt.EventQueue.invokeLater(new Runnable() {
		// public void run() {
		// new gui().setVisible(true);
		// }
		// });
		// }

		// Variables declaration - do not modify
		private javax.swing.JTextField Delay1;
		private javax.swing.JLabel Delay1Text;
		private javax.swing.JTextField Delay2;
		private javax.swing.JLabel Delay2Text;
		private javax.swing.JCheckBox Goal;
		private javax.swing.JLabel Goal2Mining;
		private javax.swing.JLabel Goal3OresMined;
		private javax.swing.JCheckBox Highalch;
		private javax.swing.JComboBox PaintTheme;
		private javax.swing.JLabel PaintThemeText;
		private javax.swing.JCheckBox Rest;
		private javax.swing.JCheckBox Rest2;
		private javax.swing.JLabel RockSteal;
		private javax.swing.JCheckBox SaveSettings;
		private javax.swing.JButton Start;
		private javax.swing.JCheckBox Superheat;
		private javax.swing.JLabel Title;
		private javax.swing.JLabel Version;
		private javax.swing.JCheckBox WH;
		private javax.swing.JTextField WHnumber;
		private javax.swing.JCheckBox cTime;
		private javax.swing.JCheckBox chatrespond;
		private javax.swing.JCheckBox clanchatcheck;
		private javax.swing.JCheckBox cmLevel;
		private javax.swing.JLabel colon1;
		private javax.swing.JLabel colon2;
		private javax.swing.JCheckBox crMined;
		private javax.swing.JLabel format;
		private javax.swing.JTextField hours;
		private javax.swing.JTextField jclanchat;
		private javax.swing.JTextField mLevel;
		private javax.swing.JCheckBox mineMith;
		private javax.swing.JTextField mins;
		private javax.swing.JLabel mstext1;
		private javax.swing.JLabel mstext2;
		private javax.swing.JLabel people;
		private javax.swing.JCheckBox powermine2;
		private javax.swing.JCheckBox priorMith2;
		private javax.swing.JComboBox priorMithlvl;
		private javax.swing.JTextField rMined;
		private javax.swing.JComboBox rostoption;
		private javax.swing.JTextField secs;
		private javax.swing.JCheckBox updatecheck;
		// End of variables declaration

	}

}