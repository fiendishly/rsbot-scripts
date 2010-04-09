import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import org.rsbot.bot.Bot;
import org.rsbot.bot.input.Mouse;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Constants;
import org.rsbot.script.GEItemInfo;
import org.rsbot.script.Script;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

@org.rsbot.script.ScriptManifest(authors = { "Epic_" }, category = "Woodcutting", name = "EpicWoodcutter", version = 3.3, description = "<html><head></head><body bgcolor='#222222'><center><table><tr><td width='350' valign='top'><center><div style='font-family:Calibri, Arial;color:#FF3300; font-size:36px'>EpicWoodcutter</div><div style='font-family:Calibri, Arial;color:#FFFFFF; font-size:16px'>v3.3 by Epic_ <br /> <a href='http://www.rsbot.org/vb/showthread.php?t=93865' style='color:#FFFFFF; font-size:14px'>Visit the official thread here.</a></div><div style='font-family:Calibri, Arial;color:#FF3300; font-size:16px'>Quick Select</div><div><table bordercolor='#FF3300' border='1' cellpadding='0' cellspacing='0' ><tr><td align='center' style='font-family:Calibri, Arial;color:#FFFFFF; font-size:12px'><b>Tree Locations</b> <br /><select name='trees'><option>Trees 1<option>Trees 2<option>Trees 3</select> <select name='oaks'><option>Oaks 1</select> <select name='willows'><option>Willows 1<option>Willows 2<option>Willows 3</select> <br /><strong>Hatchet Retrieval</strong> <br />[Mithril<input type='checkbox' name='getMithrilAxeArg' value='true' checked='checked'>][Adamant<input type='checkbox' name='getAdamantAxeArg' value='true' checked='checked'>][Rune<input type='checkbox' name='getRuneAxeArg' value='true' checked='checked'>] <br /><strong>Yews</strong> <br /><select name='cutYewsArg'><option>Yews from 65<option>Yews from 60<option>Yews from 70<option>never</select></td></tr></table></div><div style='font-family:Calibri, Arial;color:#FF3300; font-size:16px'>Instructions</div><div style='font-family:Calibri, Arial;color:#FFFFFF; font-size:11px' align='justify'>This script is a woodcutter that trains around Rimmington. It has a built in deathwalk and will find its way to the trees wherever you start it. It is intended for training your woodcutting from 1 to yews as fast as possible, and trains in the most efficient way to get there. Locations are ranked in order best to worst, so only use other locations if the first ones are full. <br /> <br /> It will retrieve the selected axes from the bank when you reach the required level to use them. Note that it is best to start with the axe in the inventory, not wielded. The detauls of what trees the script will cut can be found below. It is important to not that while it powercuts normal, oak and willow trees, it does bank yew trees for profit. <ul><li> 1-20 - Normal Trees </li><li> 20-35 - Oak Trees </li><li> 35-Selected - Willow Trees </li><li> Selected onwards - Yew Trees </li></ul>If you have any further questions, do not hesitate to post them in the official thread (the link can be found at the top) and I will get on to you as soon as possible. Also, feedback, progress reports and new ideas are extremely welcome!</div></center> </td></tr></table></center></body></html>")
public class EpicWoodcutter extends Script implements PaintListener {

	// //////////////////
	// VARIABLES //
	// //////////////////

	public double version = 3.3;
	public double latestVersion = 0;

	public int startLevel;
	public int startEXP;
	public long EXPGained;
	public int levelsGained;
	public long EXPPerHour;
	public String status = "Starting up";
	public String logType = "logs";
	public boolean getRuneAxe = false;
	public boolean getAdamantAxe = false;
	public boolean getMithrilAxe = false;
	public boolean cutYews = false;
	public int cutYewsAt = 100;
	public int YEWLOGID = 1515;
	public int yewPrice = 0;

	public int[] axeTypes = { 1349, 1351, 1353, 1355, 1357, 1359, 1361, 6739 };
	public int runeAxe = 1359;
	public int adamantAxe = 1357;
	public int mithrilAxe = 1355;
	public long startTime = 0;

	public int[] NORMALTREES = { 5004, 5005, 5045, 3879, 3881, 3882, 3883,
			3885, 3886, 3887, 3888, 3889, 3890, 3891, 3892, 3893, 3928, 3967,
			3968, 4048, 4049, 4050, 4051, 4052, 4053, 4054, 3033, 3034, 3035,
			3036, 2409, 2447, 2448, 1330, 1331, 1332, 1310, 1305, 1304, 1303,
			1301, 1276, 1277, 1278, 1279, 1280, 8742, 8743, 8973, 8974, 1315,
			1316 };
	public int[] OAKTREES = { 1281, 3037, 8462, 8463, 8464, 8465, 8466, 8467 };
	public int[] WILLOWTREES = { 1308, 5551, 5552, 5553, 8481, 8482, 8483,
			8484, 8485, 8486, 8487, 8488 };
	public int[] YEWTREES = { 1309, 8503, 8504, 8505, 8506, 8507, 8508, 8509,
			8510, 8511, 8512, 8513 };

	public int[] currentTrees;
	public int bankBooth = 11758;

	// RSTiles
	public RSTile normalTreesLocation = new RSTile(2996, 3228); // new
	// RSTile(2990,3206);
	// new
	// RSTile(2995,3252);
	public RSTile oakTreesLocation = new RSTile(2984, 3205);
	public RSTile willowTreesLocation = new RSTile(2989, 3187); // new
	// RSTile(2970,3195);
	// new
	// RSTile(2997,3167);
	public RSTile yewTreesLocation = new RSTile(2938, 3232);

	public RSTile halfWayWalkPoint = new RSTile(3070, 3276);
	public RSTile faladorEastBank = new RSTile(3012, 3355);
	public RSTile lumbridge = new RSTile(3222, 3218);

	public RSTile[] bankToYews = new RSTile[] { new RSTile(3012, 3355),
			new RSTile(3005, 3318), new RSTile(2968, 3284),
			new RSTile(2958, 3241), new RSTile(2938, 3232) };
	public RSTile[] yewsToBank = reversePath(bankToYews);

	private final Color backgroundTop = new Color(255, 255, 255, 30),
			backgroundBottom = new Color(52, 42, 31, 200), title = new Color(
					252, 180, 72, 200), black = new Color(0, 0, 0, 160),
			white = new Color(255, 255, 255, 160), green = new Color(30, 255,
					30, 200), red = new Color(255, 30, 30, 200);

	public int antiBan() {
		final int ranNo = random(0, 20);

		if (ranNo == 2) {
			if (getCurrentTab() != Constants.TAB_STATS) {
				openTab(Constants.TAB_STATS);
				moveMouse(random(670, 690), random(400, 410));
				wait(random(500, 750));
				return random(100, 200);
			}
		} else if (ranNo == 3) {
			openTab(Constants.TAB_INVENTORY);
			return random(200, 400);
		} else if (ranNo == 4) {
			moveMouse(random(0, 700), random(0, 500));
			return random(200, 400);
		} else if (ranNo == 5) {
			moveMouse(random(0, 450), random(0, 400));
			return random(200, 400);
		}
		return random(200, 450);
	}

	public boolean atMenuEpic(final String optionContains) {
		final int idx = getMenuIndex(optionContains);
		if (!isMenuOpen()) {
			if (idx == -1) {
				return false;
			}
			if (idx == 0) {
				clickMouse(true);
			} else {
				clickMouse(false);
				wait(random(200, 400));
				atMenuItem(idx);
			}
			return true;
		} else {
			if (idx == -1) {
				while (isMenuOpen()) {
					moveMouseRandomly(750);
					wait(random(100, 500));
				}
				return false;
			} else {
				atMenuItem(idx);
				return true;
			}
		}
	}

	public boolean atObjectEpic(final RSObject object, final String action) {
		return atTileEpic(object.getLocation(), action);
	}

	public boolean atTileEpic(final RSTile tile, final String action) {
		try {
			final Point location = Calculations.tileToScreen(tile);
			if (location.x == -1 || location.y == -1) {
				return false;
			}
			moveMouse(location, 3, 3);
			clickMouse(false);
			return atMenuEpic(action);
		} catch (final Exception e) {
			return false;
		}
	}

	public boolean atTreeEpic(final RSObject tree, final String action) {
		try {
			final RSTile loc1 = tree.getLocation();
			@SuppressWarnings("unused")
			final RSTile loc2 = new RSTile(loc1.getX() + 1, loc1.getY());
			@SuppressWarnings("unused")
			final RSTile loc3 = new RSTile(loc1.getX(), loc1.getY() + 1);
			final RSTile loc4 = new RSTile(loc1.getX() + 1, loc1.getY() + 1);

			final Point sloc1 = Calculations.tileToScreen(loc1.getX(), loc1
					.getY(), 10);
			final Point sloc2 = Calculations.tileToScreen(loc4.getX(), loc4
					.getY(), 10);
			final Point screenLoc = new Point((sloc1.x + sloc2.x) / 2,
					(sloc1.y + sloc2.y) / 2);
			if (screenLoc.x == -1 || screenLoc.y == -1) {
				return false;
			}

			moveMouse(screenLoc, 3, 3);
			wait(random(200, 300));
			return atMenu(action);
		} catch (final Exception e) {
			log("Small problem encountered, meltdown averted =P");
			return false;
		}
	}

	public boolean atTreeEpic33(final RSObject tree, final String action) {
		try {
			final RSTile loc1 = tree.getLocation();
			final RSTile loc4 = new RSTile(loc1.getX() + 1, loc1.getY() + 1);

			final Point screenLoc = Calculations.tileToScreen(loc4.getX(), loc4
					.getY(), 10);
			if (screenLoc.x == -1 || screenLoc.y == -1) {
				return false;
			}

			moveMouse(screenLoc, 3, 3);
			wait(random(200, 300));
			return atMenu(action);
		} catch (final Exception e) {
			log("Small problem encountered, meltdown averted =P");
			return false;
		}
	}

	public boolean atWelcomButton() {
		final RSInterface welcomeInterface = RSInterface.getInterface(378);
		if (welcomeInterface.getChild(45).getAbsoluteX() > 20
				|| !welcomeInterface.getChild(117).getText().equals(
						"10.1120.190")
				&& !welcomeInterface.getChild(117).getText().equals("")) {
			status = "Welcome Screen";
			clickMouse(random(215, 555), random(420, 440), true);
			return true;
		} else {
			return false;
		}
	}

	// ///////////////////////
	// FUNCTIONS //
	// ///////////////////////

	public boolean chopTree(final int treeIds[]) {
		final RSObject Tree = getNearestObjectByID(treeIds);
		if (Tree == null) {
			return false;
		}
		final RSTile treeLoc = Tree.getLocation();

		/*
		 * if (distanceTo(treeLoc) == 5) { turnToTile(treeLoc); wait(random(300,
		 * 500)); }
		 */
		if (distanceTo(treeLoc) > random(4, 5)) {
			walkTo(treeLoc);
			wait(random(200, 500));
		}
		if (random(1, 20) == 10) {
			walkTo(treeLoc);
			wait(random(200, 500));
		}

		final RSObject finalTree = getLowestCorner(Tree);

		if (logType == "oaks" || logType == "yews") {
			return atTreeEpic33(finalTree, "Chop down");
		} else {
			return atTreeEpic(finalTree, "Chop down");
		}
	}

	public void depositAllExceptEpic(final int items[]) {
		boolean canBank = true;
		int i = 0;
		int j = 0;
		int[] inventoryArray = null;
		Point inventoryItemPoint = null;

		for (i = 0; i < 4; i++) {

			for (j = 0; j < 7; j++) {
				inventoryArray = null;
				inventoryItemPoint = null;
				inventoryArray = getInventoryArray();
				canBank = true;
				if (inventoryArray[j + i * 4] != 0) {
					if (items != null) {
						for (final int k : items) {
							if (inventoryArray[j + i * 4] == k) {
								canBank = false;
							}
						}
					}
					if (inventoryArray[j + i * 4] <= 0) {
						canBank = false;
					}

					if (canBank) {
						inventoryItemPoint = getInventoryItemPoint(j + i * 4);
						inventoryItemPoint.x += random(10, 22);
						inventoryItemPoint.y += random(10, 22);
						if (inventoryItemPoint != null) {
							clickMouse(inventoryItemPoint, false);
							atMenu("Deposit-All");
							wait(random(500, 900));
						}
					}
				}
			}
		}
		return;
	}

	public void depositYews() {
		try {
			final RSObject bankbooth = getNearestObjectByID(bankBooth);
			if (bankbooth != null && !bank.getInterface().isValid()) {
				atObjectEpic(bankbooth, "use-quickly");
			}
			wait(random(800, 1000));

			// RSInterface bankInterface=
			// RSInterface.getInterface(Constants.INTERFACE_BANK);

			if (bank.getInterface().isValid()) {
				depositAllExceptEpic(axeTypes);
				wait(random(400, 800));
			}
		} catch (final Exception e) {
		}
		return;
	}

	public void drawStringWithShadow(final String text, final int x,
			final int y, final Graphics g) {
		final Color col = g.getColor();
		g.setColor(black);
		g.drawString(text, x + 1, y + 1);
		g.setColor(col);
		g.drawString(text, x, y);
	}

	public void dropAllExcept_(final int... items) {
		try {
			boolean canDrop = true;
			int i = 0;
			int j = 0;
			int[] inventoryArray = null;
			Point inventoryItemPoint = null;

			if (getCurrentTab() != Constants.TAB_INVENTORY) {
				openTab(Constants.TAB_INVENTORY);
			}

			for (i = 0; i < 4; i++) {

				for (j = 0; j < 7; j++) {
					inventoryArray = null;
					inventoryItemPoint = null;

					inventoryArray = getInventoryArray();

					canDrop = true;

					if (inventoryArray[i + j * 4] != 0) {

						if (items != null) {
							for (final int k : items) {
								if (inventoryArray[i + j * 4] == k) {
									canDrop = false;
								}
							}
						}
						if (canDrop) {
							inventoryItemPoint = getInventoryItemPoint(i + j
									* 4);
							inventoryItemPoint.x += random(10, 22);
							inventoryItemPoint.y += random(10, 22);
							if (inventoryItemPoint != null) {
								clickMouse(inventoryItemPoint, false);
								atMenu("Drop");
							}
						}
					}
				}
			}
		} catch (final Exception e) {
			log("Small problem encountered, meltdown averted =P (not sure what it's going to do now =S)");
		}
		return;
	}

	public void getLatestVersion() {
		URLConnection url = null;
		BufferedReader in = null;
		try {
			// Open the version text file
			url = new URL("http://epic-scripting.webs.com/EpicWoodcutterV.txt")
					.openConnection();
			// Create an input stream for it
			in = new BufferedReader(new InputStreamReader(url.getInputStream()));
			// Check if the current version is outdated
			latestVersion = Double.parseDouble(in.readLine());
		} catch (final Exception e) {
			log("Error loading version data.");
		}
	}

	public RSObject getLowestCorner(final RSObject testObj) {
		RSObject cur = null;
		final int testID = testObj.getID();
		int lowestX = -1;
		int lowestY = -1;
		int x, y;

		for (x = 0; x < 5; x++) {
			final RSObject o = getObjectAt(testObj.getLocation().getX() - x,
					testObj.getLocation().getY());
			if (o != null) {
				if (o.getID() != testID && lowestX == -1) {
					lowestX = testObj.getLocation().getX() - x + 1;
				}
			} else if (lowestX == -1) {
				lowestX = testObj.getLocation().getX() - x + 1;
			}

		}
		for (y = 0; y < 5; y++) {
			final RSObject o = getObjectAt(lowestX, testObj.getLocation()
					.getY()
					- y);
			if (o != null) {
				if (o.getID() != testID && lowestY == -1) {
					lowestY = testObj.getLocation().getY() - y + 1;
				}
			} else if (lowestY == -1) {
				lowestY = testObj.getLocation().getY() - y + 1;
			}
		}

		cur = getObjectAt(lowestX, lowestY);
		return cur;

	}

	public boolean isWithinBounds(final Point p, final int x, final int y,
			final int w, final int h) {
		if (p.x > x && p.x < x + w && p.y > y && p.y < y + h) {
			return true;
		} else {
			return false;
		}
	}

	public int loop() {
		if (atWelcomButton()) {
			return 500;
		}

		if (isLoggedIn() && skills.getCurrentSkillLevel(8) != 0) {

			setCameraAltitude(true);

			if (getEnergy() > random(30, 60)) {
				setRun(true);
			}

			if (startTime == 0 && skills.getCurrentSkillLevel(8) != 0) {
				startTime = System.currentTimeMillis();
				startLevel = skills.getCurrentSkillLevel(8);
				startEXP = skills.getCurrentSkillExp(8);
			}

			if (getRuneAxe == true) {
				if (skills.getCurrentSkillLevel(8) >= 41
						&& !inventoryContains(runeAxe)) {
					status = "Walking to bank";
					walkToTile(faladorEastBank);
					wait(random(1000, 2000));
					swapAxe(runeAxe);
					return random(500, 800);
				}
			}
			if (getAdamantAxe == true) {
				if (skills.getCurrentSkillLevel(8) >= 31
						&& skills.getCurrentSkillLevel(8) < 41
						&& !inventoryContains(adamantAxe)) {
					status = "Walking to bank";
					walkToTile(faladorEastBank);
					wait(random(1000, 2000));
					swapAxe(adamantAxe);
					return random(500, 800);
				}
			}
			if (getMithrilAxe == true) {
				if (skills.getCurrentSkillLevel(8) >= 21
						&& skills.getCurrentSkillLevel(8) < 31
						&& !inventoryContains(mithrilAxe)) {
					status = "Walking to bank";
					walkToTile(faladorEastBank);
					wait(random(1000, 2000));
					swapAxe(mithrilAxe);
					return random(500, 800);
				}
			}

			if (getMyPlayer().getLocation().getX() > halfWayWalkPoint.getX() + 5) {
				status = "Walking";
				walkToTile(halfWayWalkPoint);
				return random(200, 500);
			}

			if (skills.getCurrentSkillLevel(8) < 20) {
				logType = "logs";
			} else if (skills.getCurrentSkillLevel(8) < 35) {
				logType = "oaks";
			} else if (skills.getCurrentSkillLevel(8) < cutYewsAt
					|| cutYews == false) {
				logType = "willows";
			} else {
				logType = "yews";
			}

			if (isInventoryFull() || bank.getInterface().isValid()
					&& getInventoryCount() == 14) {
				if (logType == "yews") {
					status = "Walking to bank";
					walkPathEpic(yewsToBank);
					if (distanceTo(faladorEastBank) < random(2, 5)) {
						status = "Banking yews";
						depositYews();
					} else {
						walkTo(faladorEastBank);
					}
					return random(300, 800);
				} else {
					status = "Dropping " + logType;
					dropAllExcept_(axeTypes);
				}
			}

			if (skills.getCurrentSkillLevel(8) < 20) {
				currentTrees = NORMALTREES;
				if (distanceTo(normalTreesLocation) > 12) {
					status = "Walking to trees";
					walkToTile(normalTreesLocation);
					// status = "Return";
					return random(500, 800);
				}
			} else if (skills.getCurrentSkillLevel(8) < 35) {
				currentTrees = OAKTREES;
				if (distanceTo(oakTreesLocation) > 12) {
					status = "Walking to oaks";
					walkToTile(oakTreesLocation);
					// status = "Return";
					return random(500, 800);
				}
			} else if (skills.getCurrentSkillLevel(8) < cutYewsAt
					|| cutYews == false) {
				currentTrees = WILLOWTREES;
				if (distanceTo(willowTreesLocation) > 12) {
					status = "Walking to willows";
					walkToTile(willowTreesLocation);
					// status = "Return";
					return random(500, 800);
				}
			} else {
				currentTrees = YEWTREES;
				if (distanceTo(yewTreesLocation) > 12 && !isInventoryFull()) {
					status = "Walking to yews";
					walkPathEpic(bankToYews);
					// status = "Return";
					return random(500, 800);
				}
			}

			if (getMyPlayer().getAnimation() != -1 || getMyPlayer().isMoving()) {
				return antiBan();
			} else {
				status = "Cutting " + logType;
				chopTree(currentTrees);
				return random(500, 800);
			}
		}
		return random(500, 800);
	}

	public void onRepaint(final Graphics g) {
		if (isLoggedIn()) {
			long millis = System.currentTimeMillis() - startTime;
			final long hours = millis / (1000 * 60 * 60);
			millis -= hours * 1000 * 60 * 60;
			final long minutes = millis / (1000 * 60);
			millis -= minutes * 1000 * 60;
			final long seconds = millis / 1000;

			/*
			 * g.setColor(new Color(0, 0, 0, 100)); g.fillRoundRect(10, 209,
			 * 110, 123, 10, 10);
			 */

			final long totalSeconds = (System.currentTimeMillis() - startTime) / 1000;

			EXPGained = skills.getCurrentSkillExp(8) - startEXP;
			levelsGained = skills.getCurrentSkillLevel(8) - startLevel;
			if (totalSeconds == 0) {
				EXPPerHour = 0;
			} else {
				EXPPerHour = EXPGained * 3600 / totalSeconds;
			}

			long secondsToLvl;

			if (EXPPerHour == 0) {
				secondsToLvl = 0;
			} else {
				secondsToLvl = skills.getXPToNextLevel(8) * 3600 / EXPPerHour;
			}
			;
			final long hoursToLvl = secondsToLvl / (60 * 60);
			secondsToLvl -= hoursToLvl * 60 * 60;
			final long minutesToLvl = secondsToLvl / 60;
			secondsToLvl -= minutesToLvl * 60;

			g.setColor(backgroundBottom);
			g.fillRect(0, 319, 519, 20);

			g.setColor(new Color(255, 30, 30, 200));
			g.fillRoundRect(15, 324, 100, 10, 5, 10);
			g.setColor(new Color(30, 255, 30, 200));
			g
					.fillRoundRect(15, 324, skills.getPercentToNextLevel(8),
							10, 5, 10);
			g.setColor(new Color(200, 200, 200, 200));
			g.drawRoundRect(15, 324, 100, 10, 5, 10);

			g.setFont(new Font("Calibri", Font.BOLD, 14));

			g.setColor(white);
			drawStringWithShadow("" + skills.getPercentToNextLevel(8) + "%",
					125, 333, g);

			g.drawRect(517, 319, 1, 19);
			g.drawRect(417, 319, 1, 19);
			g.drawRect(317, 319, 1, 19);
			g.drawRect(217, 319, 1, 19);

			g.setFont(new Font("Calibri", Font.BOLD, 16));
			g.setColor(title);
			drawStringWithShadow("Status", 245, 334, g);
			drawStringWithShadow("Experience", 332, 334, g);
			drawStringWithShadow("Profit", 450, 334, g);

			g.setColor(backgroundTop);
			g.fillRect(0, 319, 519, 10);

			int y = 220;
			int x;
			final int spacing = 15;

			g.setFont(new Font("Calibri", Font.BOLD, 12));

			final Mouse m = Bot.getClient().getMouse();
			final Point p = new Point(m.x, m.y);

			if (isWithinBounds(p, 217, 319, 100, 20)) {
				g.setColor(backgroundBottom);
				g.fillRect(217, 235, 102, 84);
				x = 222;
				y = 235;
				g.setColor(green);
				drawStringWithShadow("" + status, x, y += spacing, g);
				g.setColor(title);
				drawStringWithShadow("Runtime: " + hours + ":" + minutes + ":"
						+ seconds, x, y += spacing, g);
				drawStringWithShadow("Version: " + version, x, y += spacing, g);
				if (version < latestVersion) {
					g.setColor(red);
					drawStringWithShadow("***Outdated***", x, y += spacing, g);
					drawStringWithShadow(
							"v" + latestVersion + " is available!", x,
							y += spacing, g);
					g.setColor(title);
				} else {
					drawStringWithShadow("Fully updated", x, y += spacing, g);
					drawStringWithShadow("Latest version", x, y += spacing, g);
				}

			}
			if (isWithinBounds(p, 317, 319, 100, 20)) {
				g.setColor(backgroundBottom);
				g.fillRect(317, 235, 102, 84);
				x = 322;
				y = 235;
				g.setColor(title);
				drawStringWithShadow("Current Level: "
						+ skills.getCurrentSkillLevel(8), x, y += spacing, g);
				drawStringWithShadow("Levels Gained: " + levelsGained, x,
						y += spacing, g);
				drawStringWithShadow("EXP Gain: " + EXPGained, x, y += spacing,
						g);
				drawStringWithShadow("EXP/Hour: " + EXPPerHour, x,
						y += spacing, g);
				drawStringWithShadow("Level in: " + hoursToLvl + ":"
						+ minutesToLvl + ":" + secondsToLvl, x, y += spacing, g);
			}
			if (isWithinBounds(p, 417, 319, 100, 20)) {
				g.setColor(backgroundBottom);
				g.fillRect(417, 235, 102, 84);
				x = 422;
				y = 235;
				g.setColor(title);
				if (logType == "yews") {
					drawStringWithShadow("Log Type: Yews", x, y += spacing, g);
					drawStringWithShadow("Log Price: " + yewPrice + "gp", x,
							y += spacing, g);
					drawStringWithShadow("Logs Banked: " + EXPGained / 175, x,
							y += spacing, g);
					drawStringWithShadow("Profit: " + EXPGained / 175
							* yewPrice + "gp", x, y += spacing, g);
					drawStringWithShadow("GP/Hour: " + EXPPerHour * yewPrice
							/ 175, x, y += spacing, g);
				} else {
					drawStringWithShadow("Log Type: " + logType, x,
							y += spacing, g);
					drawStringWithShadow("Log Price: N/A", x, y += spacing, g);
					drawStringWithShadow("Logs Banked: N/A", x, y += spacing, g);
					drawStringWithShadow("Profit: N/A", x, y += spacing, g);
					drawStringWithShadow("GP/Hour: N/A", x, y += spacing, g);
				}
			}
		}
	}

	public boolean onStart(final Map<String, String> args) {

		if (args.get("getRuneAxeArg") != null) {
			if (args.get("getRuneAxeArg").equals("true")) {
				getRuneAxe = true;
			}
		}
		if (args.get("getAdamantAxeArg") != null) {
			if (args.get("getAdamantAxeArg").equals("true")) {
				getAdamantAxe = true;
			}
		}
		if (args.get("getMithrilAxeArg") != null) {
			if (args.get("getMithrilAxeArg").equals("true")) {
				getMithrilAxe = true;
			}
		}

		if (args.get("cutYewsArg").equals("Yews from 60")) {
			cutYewsAt = 60;
			cutYews = true;
		}
		if (args.get("cutYewsArg").equals("Yews from 65")) {
			cutYewsAt = 65;
			cutYews = true;
		}
		if (args.get("cutYewsArg").equals("Yews from 70")) {
			cutYewsAt = 70;
			cutYews = true;
		}
		if (args.get("cutYewsArg").equals("never")) {
			cutYewsAt = 100;
			cutYews = false;
		}

		if (cutYews == true) {
			final GEItemInfo yew = grandExchange.loadItemInfo(YEWLOGID);
			yewPrice = yew.getMarketPrice();
		}

		if (args.get("trees").equals("Trees 1")) {
			normalTreesLocation = new RSTile(2996, 3228);
		}
		if (args.get("trees").equals("Trees 2")) {
			normalTreesLocation = new RSTile(2995, 3252);
		}
		if (args.get("trees").equals("Trees 3")) {
			normalTreesLocation = new RSTile(2990, 3206);
		}

		if (args.get("oaks").equals("Oaks 1")) {
			oakTreesLocation = new RSTile(2984, 3205);
		}

		if (args.get("willows").equals("Willows 1")) {
			willowTreesLocation = new RSTile(2989, 3187);
		}
		if (args.get("willows").equals("Willows 2")) {
			willowTreesLocation = new RSTile(2998, 3166);
		}
		if (args.get("willows").equals("Willows 3")) {
			willowTreesLocation = new RSTile(2970, 3195);
		}

		getLatestVersion();
		if (latestVersion > version) {
			log("This version of EpicWoodcutter is now outdated!");
			log("Please visit the RSBot site (www.rsbot.org) for the update!");
		}
		return true;
	}

	public void swapAxe(final int axeID) {
		try {
			if (distanceTo(faladorEastBank) < random(2, 4)
					&& !bank.getInterface().isValid()) {
				walkTo(faladorEastBank);
				wait(random(200, 500));
			}
			final RSObject bankbooth = getNearestObjectByID(bankBooth);
			if (bankbooth != null && !bank.getInterface().isValid()) {
				atObjectEpic(bankbooth, "use-quickly");
			}
			wait(random(800, 1600));

			if (bank.getInterface().isValid()) {
				if (bank.getCount(axeID) == 0) {
					if (axeID == runeAxe) {
						getRuneAxe = false;
						log("Could not find rune hatchet in bank. Next time don't select it at the start!");
					}
					if (axeID == adamantAxe) {
						getAdamantAxe = false;
						log("Could not find adamant hatchet in bank. Next time don't select it at the start!");
					}
					if (axeID == mithrilAxe) {
						getMithrilAxe = false;
						log("Could not find mithril hatchet in bank. Next time don't select it at the start!");
					}

					wait(500);
					atInterface(Constants.INTERFACE_BANK,
							Constants.INTERFACE_BANK_BUTTON_CLOSE);
					return;
				}
				bank.depositAll();
				wait(random(400, 800));
				bank.atItem(axeID, "Withdraw-1");
				wait(random(1500, 2100));
				atInterface(Constants.INTERFACE_BANK,
						Constants.INTERFACE_BANK_BUTTON_CLOSE);
			}
		} catch (final Exception e) {
		}
		return;
	}

	public RSTile tileOnMap2(RSTile tile) {
		if (distanceTo(tile) < 15) {
			return tile;
		}

		for (int i = 0; i < 100; i++) {
			final RSTile tileLoc = getMyPlayer().getLocation();
			final RSTile tile2 = new RSTile(
					(tile.getX() * 2 + tileLoc.getX()) / 3,
					(tile.getY() * 2 + tileLoc.getY()) / 3);
			if (distanceTo(tile2) < 15) {
				return tile2;
			}
			tile = tile2;
		}

		return tile;
	}

	public void walkPathEpic(final RSTile tilePath[]) {
		int nearest = 0;
		int distance = 999;

		for (int i = 0; i < tilePath.length; i++) {
			if (distanceTo(tilePath[i]) < distance) {
				nearest = i;
				distance = distanceTo(tilePath[i]);
			}
		}

		for (int j = nearest; j < tilePath.length; j++) {
			if (walkToTile(tilePath[j]) == false) {
				return;
			}
		}

	}

	public boolean walkToTile(final RSTile tile) {
		int failsRemaining = 6;
		int locationFailsRemaining = 5;
		RSTile lastLocation = getMyPlayer().getLocation();
		while (distanceTo(tile) > 8) {
			if (getEnergy() > random(30, 60)) {
				setRun(true);
			}
			if (walkTo(tileOnMap2(randomizeTile(tile, 2, 2))) == false) {
				failsRemaining -= 1;
				if (failsRemaining <= 0) {
					status = "Walking failed";
					log("Walking failed (invalid location), checking for randoms");
					if (random(1, 3) == 2) {
						walkTo(new RSTile(
								getMyPlayer().getLocation().getX() + 2,
								getMyPlayer().getLocation().getY() + 2));
					}
					return false;
				}
				wait(random(300, 800));
			} else {
				failsRemaining = 5;
			}
			if (getMyPlayer().getLocation().getX() == lastLocation.getX()
					&& getMyPlayer().getLocation().getY() == lastLocation
							.getY()) {
				locationFailsRemaining -= 1;
				if (locationFailsRemaining <= 0) {
					status = "Walking failed";
					log("Walking failed (not moving), checking for randoms");
					return false;
				}
				wait(random(300, 800));
			} else {
				lastLocation = getMyPlayer().getLocation();
				locationFailsRemaining = 5;
			}
			int countDown = 100;
			while (distanceTo(getDestination()) > 8 && getMyPlayer().isMoving()) {
				wait(200);
				countDown -= 1;
				if (countDown <= 0) {
					if (distanceTo(lumbridge) > 40) {
						return false;
					}
				}
			}
		}
		if (distanceTo(tile) > 5) {
			walkTo(tile);
		}
		return true;
	}
}