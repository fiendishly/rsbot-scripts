/* Changelog:
 * 1.0 Basic script
 * 1.1 Modified the picking up a bit More human now.
 * 1.2 Fixed some nullpointers
 * 1.3 Added antiban
 * 1.4 Fixed need one slot bug.
 * 1.5 Better clicking...
 * 1.6 Changed back and added turning.
 * 1.7 Improved fail detection.
 * 2.0 Runetek 5
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Map;

import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSItemTile;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = { "Drizzt1112" }, category = "Hunter", name = "LizardDrizzt2", version = 2.0)
public class LizardDrizzt2 extends Script implements ServerMessageListener,
		PaintListener {

	boolean AntiBanDebug = true;
	int avgPerHour;
	long avoidedCombat;
	long avoidedSmokingRocks;
	int Caught = 19675;
	int checkTime;
	int countToNext;
	boolean Debug = false;
	int FailCount = 0;
	long lastAvgCheck;
	long lastCheck;

	int Lizard = 10149;
	int Lizards = 0;
	int Net = 303;
	int NormalTree = 19679;
	int oldExp;
	int oldLevels;
	int oldRockCount;
	int Points = 0;
	String PointsString = "LuLWuT?";
	boolean powerMode = false;
	int randomRun = random(40, 75);
	int rockCount;
	int Rope = 954;
	int startExp = 0;
	public int startLevel = 0;
	int StartPoints = 0;
	public long startTime = System.currentTimeMillis();
	public int startXP = 0;
	public long time = System.currentTimeMillis();
	int Trapped = 19678;
	RSTile Tree1 = new RSTile(3553, 3450);
	RSTile Tree2 = new RSTile(3553, 3453);
	RSTile Tree3 = new RSTile(3550, 3449);
	int xpPerRock;

	public void AntiBan(int r, final boolean Random) { // Made By Drizzt1112
		if (Random) {
			r = random(0, 15);
		}
		if (AntiBanDebug) {
			log("Antiban Case: " + r);
		}
		switch (r) { // Ewww i am using case -.-
		case 1:
			setCameraRotation(random(1, 359));
			return;
		case 2:
			setCameraAltitude(random(1.0, 99.0));
			return;
		case 3:
			moveMouse(random(1, 760), random(1, 499));
			return;
		case 4:
			openTab(random(0, 12));
			return;
		case 5:
			setCameraRotation(random(1, 359));
			setCameraAltitude(random(1.0, 99.0));
			return;
		case 6:
			int x = input.getX();
			int y = input.getY();
			moveMouse(x + random(-70, 70), y + random(-70, 70));
			x = input.getX();
			y = input.getY();
			moveMouse(x + random(-70, 70), y + random(-70, 70));
			x = input.getX();
			y = input.getY();
			moveMouse(x + random(-70, 70), y + random(-70, 70));
			x = input.getX();
			y = input.getY();
			moveMouse(x + random(-70, 70), y + random(-70, 70));
			wait(random(50, 150));
			return;
		case 7:
			if (getCurrentTab() != Constants.TAB_STATS) {
				openTab(Constants.TAB_STATS);
			}
			clickMouse(random(716, 721), random(415, 430), true);
			moveMouse(random(613, 633), random(421, 441));
			wait(random(1000, 2000));
			return;
		case 8:
			final int x2 = input.getX();
			final int y2 = input.getY();
			moveMouse(x2 + random(-80, 80), y2 + random(-80, 80));
			wait(random(50, 150));
			return;
		case 9:
			final int x3 = input.getX();
			final int y3 = input.getY();
			moveMouse(x3 + random(-80, 80), y3 + random(-80, 80));
			wait(random(50, 150));
			return;
		case 10:
			final int x4 = input.getX();
			final int y4 = input.getY();
			moveMouse(x4 + random(-80, 80), y4 + random(-80, 80));
			wait(random(50, 150));
			return;
		case 11:
			final int x5 = input.getX();
			final int y5 = input.getY();
			moveMouse(x5 + random(-80, 80), y5 + random(-80, 80));
			wait(random(50, 150));
			return;
		case 12:
			final int x6 = input.getX();
			final int y6 = input.getY();
			moveMouse(x6 + random(-80, 80), y6 + random(-80, 80));
			wait(random(50, 150));
			return;
		default:
			return;
		}
	}

	public boolean atTileDrizzt(final RSTile tile, final String action) {
		AntiBan(random(1, 50), false);
		int TempCounter = 0;
		while (true) {
			if (!getMyPlayer().isMoving()) {
				break;
			}
			wait(random(200, 400));
		}
		final int y = getMyPlayer().getLocation().getX(); // / WTF did i write
		// here?
		final int tileY = tile.getX(); // / WTF did i write here?
		// setCameraRotation(90);
		try {
			Point location = Calculations.tileToScreen(new RSTile(
					tile.getX() - 1, tile.getY()));
			if (!tileOnScreen(tile) || location.x == -1 || location.y == -1) {
				walkTo(tile);
				return false;
			}

			// if to the left
			if (y > tileY) {
				if (Debug) {
					log("Left");
				}
				moveMouse(location, 15, 15);
				while (!getMenuItems().get(0).toLowerCase().contains(
						action.toLowerCase())
						&& TempCounter < 7) {
					location = Calculations.tileToScreen(tile);
					if (getMenuItems().get(0).toLowerCase().contains(
							"Dismantle".toLowerCase())) {
						return false;
					}
					moveMouse(location, random(-15, 15), random(-15, 15));
					TempCounter++;
				}
			}

			// To the right
			if (y < tileY) {
				if (Debug) {
					log("Right");
				}
				moveMouse(location, 15, 15);
				while (!getMenuItems().get(0).toLowerCase().contains(
						action.toLowerCase())
						&& TempCounter < 7) {
					location = Calculations.tileToScreen(tile);
					if (getMenuItems().get(0).toLowerCase().contains(
							"Dismantle".toLowerCase())) {
						return false;
					}
					moveMouse(location, random(-15, 15), random(-15, 15));
					TempCounter++;
				}
			}

			if (y == tileY) {
				if (Debug) {
					log("Center");
				}
				moveMouse(location, 15, 15);
				while (!getMenuItems().get(0).toLowerCase().contains(
						action.toLowerCase())
						&& TempCounter < 7) {
					location = Calculations.tileToScreen(tile);
					if (getMenuItems().get(0).toLowerCase().contains(
							"Dismantle".toLowerCase())) {
						return false;
					}
					moveMouse(location, random(-15, 15), random(-15, 15));
					TempCounter++;
				}
			}
			wait(random(50, 100));
			clickMouse(true);
			return true;
		} catch (final Exception e) {
			return false;
		}
	}

	public boolean atTileTake(final RSTile tile, final String action) {
		int TempCounter = 0;
		while (true) {
			if (!getMyPlayer().isMoving()) {
				break;
			}
			wait(random(200, 400));
		}
		try {
			Point location = Calculations.tileToScreen(tile);
			if (location.x == -1 || location.y == -1 || !tileOnScreen(tile)) {
				return false;
			}
			moveMouse(location, 8, 8);
			wait(random(50, 100));
			while (!getMenuItems().get(0).toLowerCase().contains(
					action.toLowerCase())
					&& TempCounter < 5) {
				location = Calculations.tileToScreen(tile);
				moveMouse(location, 8, 8);
				TempCounter++;
			}
			if (getMenuItems().get(0).toLowerCase().contains(
					action.toLowerCase())) {
				clickMouse(true);
			} else {
				clickMouse(false);
				atMenu(action);
			}
			wait(random(50, 100));
			return true;
		} catch (final Exception e) {
			return false;
		}
	}

	@Override
	public int loop() {
		final RSObject Trap1 = getObjectAt(Tree1);
		final RSObject Trap2 = getObjectAt(Tree2);
		final RSObject Trap3 = getObjectAt(Tree3);
		if (FailCount > 20) {
			log("Failed");
			logout();
			stopScript();
		}
		if (distanceTo(Tree1) < 18) {
			if (getMyPlayer().getAnimation() == 5215
					|| getMyPlayer().getAnimation() == 5207) {
				FailCount = 0;
				return random(200, 400);
			}
			if (inventoryContains(Lizard) && getInventoryCount() > 25) {
				FailCount = 0;
				atInventoryItem(Lizard, "Release");
				wait(random(200, 900));
				return random(200, 400);
			}
			final RSItemTile rope = getGroundItemByID(Rope);
			if (rope != null && !isInventoryFull()) {
				FailCount = 0;
				atTileTake(rope, "Take");
				wait(random(200, 900));
				return random(200, 400);
			}
			final RSItemTile net = getGroundItemByID(Net);
			if (net != null && !isInventoryFull()) {
				atTileTake(net, "Take");
				wait(random(200, 900));
				return random(200, 400);
			}
			if (Trap1.getID() == NormalTree && inventoryContains(Rope)
					&& inventoryContains(Net)) {
				atTileDrizzt(Tree1, "Set-Trap");
				FailCount = 0;
				wait(random(1400, 1900));
				return random(200, 400);
			}
			if (Trap2.getID() == NormalTree && inventoryContains(Rope)
					&& inventoryContains(Net)) {
				atTileDrizzt(Tree2, "Set-Trap");
				FailCount = 0;
				wait(random(1400, 1900));
				return random(200, 400);
			}
			if (skills.getCurrentSkillLevel(Constants.STAT_HUNTER) > 39) {
				if (Trap3.getID() == NormalTree && inventoryContains(Rope)
						&& inventoryContains(Net)) {
					atTileDrizzt(Tree3, "Set-Trap");
					FailCount = 0;
					wait(random(1400, 1900));
					return random(200, 400);
				}
			}
			if (Trap1.getID() == Caught) {
				atTileDrizzt(Tree1, "Check");
				FailCount = 0;
				wait(random(1000, 1400));
				return random(200, 400);
			}
			if (Trap2.getID() == Caught) {
				atTileDrizzt(Tree2, "Check");
				FailCount = 0;
				wait(random(1000, 1400));
				return random(200, 400);
			}
			if (skills.getCurrentSkillLevel(Constants.STAT_HUNTER) > 39) {
				if (Trap3.getID() == Caught) {
					atTileDrizzt(Tree3, "Check");
					FailCount = 0;
					wait(random(1000, 1400));
					return random(200, 400);
				}
			}
			if (inventoryContains(Lizard)) {
				atInventoryItem(Lizard, "Release");
				wait(random(200, 900));
				return random(200, 400);
			}
			if (Trap1.getID() == Trapped) {
				wait(random(100, 140));
				return random(200, 400);
			}
			if (Trap2.getID() == Trapped) {
				wait(random(100, 140));
				return random(200, 400);
			}
			if (skills.getCurrentSkillLevel(Constants.STAT_HUNTER) > 39) {
				if (Trap3.getID() == Trapped) {
					wait(random(100, 140));
					return random(200, 400);
				}
			}
		}
		FailCount++;
		log("Failed " + FailCount + " Times");
		wait(random(3000, 8000));
		return random(100, 150);
	}

	public void onRepaint(final Graphics g) {
		if (isLoggedIn()) {

			// Ty to AELIN for these
			// local objects:
			final int index = 21;
			int exp = 0;
			int levels;
			long hours = 0, minutes = 0, seconds = 0;
			long time;

			// Has start time been previously set?
			if (startTime == 0) {
				startTime = System.currentTimeMillis();
			}

			// Calculate hours, minutes and seconds.
			time = System.currentTimeMillis() - startTime;
			seconds = time / 1000;
			if (seconds >= 60) {
				minutes = seconds / 60;
				seconds -= minutes * 60;
			}
			if (minutes >= 60) {
				hours = minutes / 60;
				minutes -= hours * 60;
			}

			// Has variables been previously defined?
			if (startLevel == 0 || startExp == 0) {
				// No, so define them now.
				startLevel = skills.getCurrentSkillLevel(21);
				startExp = skills.getCurrentSkillExp(21);
				oldExp = 0;
			}

			// Calculate catches based on experience changes.
			exp = skills.getCurrentSkillExp(21) - startExp;
			if (exp > oldExp) {
				xpPerRock = exp - oldExp;
				oldExp = exp;
				rockCount++;
				countToNext = skills.getXPToNextLevel(Constants.STAT_HUNTER)
						/ xpPerRock + 1;
			}

			// Calculate levels gained.
			levels = skills.getCurrentSkillLevel(index) - startLevel;
			if (levels > oldLevels) {
				oldLevels = levels;
			}

			if (System.currentTimeMillis() - lastAvgCheck >= 60000) {
				lastAvgCheck = System.currentTimeMillis();
				avgPerHour = (rockCount - oldRockCount) * 60;
				oldRockCount = rockCount;
			}

			final Color BG = new Color(0, 0, 0, 75);
			final Color RED = new Color(255, 0, 0, 255);
			final Color GREEN = new Color(0, 255, 0, 255);
			final Color BLACK = new Color(0, 0, 0, 255);

			final int type = 21;
			long millis = System.currentTimeMillis() - startTime;
			millis -= hours * 1000 * 60 * 60;
			millis -= minutes * 1000 * 60;
			final int LevelChange = skills.getCurrentSkillLevel(type)
					- startLevel;
			final int perNextLvl = skills.getPercentToNextLevel(type);
			final int nextLvl = skills.getCurrentSkillLevel(type) + 1;
			g.setColor(BG);
			g.fill3DRect(5, 25, 205, 143, true);
			g.setColor(Color.white);
			g.drawString(getClass().getAnnotation(ScriptManifest.class).name()
					+ " v."
					+ getClass().getAnnotation(ScriptManifest.class).version()
					+ " by Drizzt1112", 10, 40);
			g.drawString("Running for: " + hours + "h " + minutes + "m "
					+ seconds + "s", 10, 60);
			g.drawString(
					"XP Gained: " + exp + "   Lvls Gained: " + LevelChange, 10,
					80);
			g.drawString("XP To next lvl: " + skills.getXPToNextLevel(type),
					10, 100);
			g.drawString("Progress to next lvl:", 10, 120);
			g.setColor(RED);
			g.fill3DRect(10, 130, 100, 10, true);
			g.setColor(GREEN);
			g.fill3DRect(10, 130, perNextLvl, 10, true);
			g.setColor(BLACK);
			g.drawString(perNextLvl + "%  to " + nextLvl, 40, 139);
			g.setColor(Color.white);
			g.drawString("Lizards Caught: " + Lizards, 10, 160);

		}
	}

	@Override
	public boolean onStart(final Map<String, String> args) {
		return true;
	}

	public void serverMessageRecieved(
			final ServerMessageEvent servermessageevent) {
		final String s = servermessageevent.getMessage().toLowerCase();
		if (s.contains("darts away")) {
			Lizards++;
		}
	}
}
