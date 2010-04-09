//  <GranitePro>
//  by Decurse 
//  PURCHASE THE EXCLUSIVE VERSION HERE: http://e-junkie.com/decurse/
/*
 ^^ FEATURES ^^
 ->  Runs from combat!
 ->  Logs out when your last full waterskin has been sipped from.
 ->  Features all anti-ban (that are currently working).
 ->  Will turn screen to avoid bot-like features.
 ->  Checks Mining stats to avoid bot-like features.
 ->  Includes a progress report on the left hand side!
 */

import java.awt.Color;
import java.awt.Graphics;
import java.util.Map;

import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = { "Decurse" }, category = "Mining", name = "GranitePro", version = 1.5, description = "<html>\n<head>\n</head>\n<body bgcolor='#eeeeee'>\n<center><font size='6' family='serif'><b>GranitePro</b></font><br><font size='3' family='sans-serif'>by Decurse</font> <b><font color=red> Buy the exclusive:  <a href='http://e-junkie.com/decurse/'>http://e-junkie.com/decurse/</a> </b></font></center>\n<br><center><form><font size='3'>Sandstone:</font> <input type='radio' name='mode' value='1'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font size='3'>Granite:</font><input type='radio' name='mode' value='0'></form><br></center><center>Using Enchanted tiara? <input type='checkbox' name='tiara' value='0'></center><b><font size='5'><center>Terms of Use</center></font></b><center>I agree to the Terms of Use: <input type='checkbox' name='agreement' value='true' align='center'> <br><br> <small>The user Decurse and/or Decurse Networking are not held responsible for damages caused to your computer, network, or account in usage.</small><b><font size='5'><center>Features</center></font></b><div align='left'><ul><li>Runs from combat!<li>Logs out when your last full waterskin has been sipped from.<li>Features all anti-ban (that are currently working).<li>Will turn screen to avoid bot-like features.<li>Checks Mining stats to avoid bot-like features.<li>Includes a progress report on the left hand side!</ul></div>\n\n</body>\n</html>\n")
public class GranitePro extends Script implements PaintListener,
		ServerMessageListener {
	public boolean agreement;
	int avgPerHour;

	long avoidedCombat;
	long avoidedSmokingRocks;
	int checkTime;
	int countToNext;
	int fails;
	int[] gear = new int[] { 1265, // Bronze Pickaxe
			1267, // Iron pickaxe.
			1269, // Steel Pickaxe
			1273, // Mithril Pickaxe
			1271, // Adamanite Pickaxe.
			1275, // Rune pickaxe
			13661, // Inferno Adze pickaxe.
			14107, // SC pickaxe
			1823, 1825, 1827, 1829, 1831, // Waterskins
			995, // GP (money)
			14664, // Reward box from randoms
			2528 // Genie Lamp

	};
	long lastAvgCheck;
	long lastCheck;
	final int MAX_FAILS = 10;
	int mode;
	int oldExp;
	int oldLevels;
	int oldRockCount;
	boolean powerMode = false;
	int randomRun = random(40, 75);
	int rockCount;
	int[][] rocks = new int[][] {
	// Granite
			new int[] { 10947 },
			// Sandstone
			new int[] { 10946 } };
	final int S_DROP = 3000;
	final int S_MINE = 2000;
	final int S_WALKTO_MINE = 1000;
	final int[] smokingRocks = { 11433, 11434, 11435, 11436, 11193 };
	int startExp;
	int startLevel;
	long startTime;
	int state = S_MINE;
	int tiara;
	RSTile[] toMine;
	boolean useTiara = false;
	public final int WATERSKIN = 1823;
	int xpPerRock;

	private int antiBan() {
		final int gamble = random(1, 12);

		switch (gamble) {
		case 1:
			return random(500, 750);

		case 2:
			final int x = random(0, 750);
			final int y = random(0, 500);
			moveMouse(0, 0, x, y);
			return random(500, 750);

		case 3:
			openTab(Constants.TAB_INVENTORY);
			return random(500, 750);

		case 4:
			if (getMyPlayer().isMoving()) {
				return random(750, 1000);
			}

			if (System.currentTimeMillis() - lastCheck >= checkTime) {
				lastCheck = System.currentTimeMillis();
				checkTime = random(60000, 180000);

				if (getCurrentTab() != Constants.TAB_STATS) {
					openTab(Constants.TAB_STATS);
				}
				moveMouse(660, 227, 50, 28);
				return random(2000, 2800);
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

		return random(500, 750);
	}

	public String getTermsAndConditions() {
		return "By using this script you agree the the user Decurse nor Decurse Networking be held liable for any damages caused to your computer, network, or account used.";
	}

	@Override
	public int loop() {
		if (!useTiara) {
			if (!inventoryContains(WATERSKIN)) {
				// Do we have any full Waterskins? If not, log out.
				log("Out of any full waterskins - Logging out to keep you alive.");
				logout();
				stopScript();
				return -1;
			}
		}

		// Do we have energy to run? If so, let's run.
		if (getEnergy() >= randomRun && !isRunning()) {
			// All conditions met, set run mode.
			log("Now running..");
			setRun(true);

			randomRun = random(40, 75);
			log("Running again at " + randomRun + " energy.");
			return random(500, 750);
		}

		// Perform script actions based on state.
		switch (state) {
		case S_WALKTO_MINE:
			if (walkPath(toMine)) {
				log("Now mining..");
				state = S_MINE;
				return random(200, 400);
			} else {
				antiBan();
				return random(250, 500);
			}

		case S_MINE:
			final RSObject rock = getNearestObjectByID(rocks[mode]);
			final RSObject sr = getNearestObjectByID(smokingRocks);

			// Is the inventory full? Switch to drop mode and get rid of some
			// things.
			if (isInventoryFull()) {
				if (powerMode) {
					log("Inventory is full.  Now dropping..");
					state = S_DROP;
					return random(1, 100);
				}
			}

			// Are we in combat? Let's run.
			if (getMyPlayer().isInCombat() && !getMyPlayer().isMoving()) // If
			{
				log("In combat, attempting to run..");
				runAway();
				avoidedCombat++;
				return random(400, 700);

			}

			if (getMyPlayer().isMoving() || getMyPlayer().getAnimation() != -1) {
				if (sr != null && distanceTo(sr.getLocation()) <= 1) {
					log("Rocks are smoking, trying to avoid..");
					avoidedSmokingRocks++;
				} else {
					return antiBan();
				}
			}
			// Can we find any rocks? If not, try again.
			if (rock != null) {
				if (tileOnScreen(rock.getLocation())) {
					fails = 0;
					atObject(rock, "Mine");
					return random(250, 320);
				} else {
					walkTileRand(rock.getLocation());
					return random(500, 750);
				}
			} else {
				fails++;
				log("Failed to find a rock, trying " + (MAX_FAILS - fails)
						+ " times.");
				return random(750, 1500);
			}
			// Drop mode.
		case S_DROP:
			log("Dropping all items except gear.");
			dropAllExcept(gear);
			state = S_MINE;
			return random(1, 100);

		}

		return random(500, 750);
	}

	public void onRepaint(final Graphics g) {
		// local objects:
		final Color WHITE = new Color(255, 255, 255, 255);
		final int index = Constants.STAT_MINING;
		int exp;
		int levels;
		long hours = 0, minutes = 0, seconds;
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
			startLevel = skills.getCurrentSkillLevel(index);
			startExp = skills.getCurrentSkillExp(index);
			oldExp = 0;
		}

		// Calculate catches based on experience changes.
		exp = skills.getCurrentSkillExp(index) - startExp;
		if (exp > oldExp) {
			xpPerRock = exp - oldExp;
			oldExp = exp;
			rockCount++;
			countToNext = skills.getXPToNextLevel(Constants.STAT_MINING)
					/ xpPerRock + 1;

		}

		// Calculate levels gained.
		levels = skills.getCurrentSkillLevel(index) - startLevel;
		if (levels > oldLevels) {
			oldLevels = levels;
		}

		// trytohide: copyright decurse.net

		// Draw information.
		g.setColor(new Color(64, 64, 64, 170));
		g.fillRoundRect(3, 180, 160, 160, 9, 5);
		g.setColor(WHITE);
		g.drawString(getClass().getAnnotation(ScriptManifest.class).name()
				+ " v"
				+ getClass().getAnnotation(ScriptManifest.class).version(), 9,
				203);
		g.drawString("             by Decurse ", 9, 219);
		g.drawString("XP Gained: " + exp, 9, 235);
		g.drawString("Levels Gained: " + levels, 9, 251);
		g.drawString("Percent to next level: "
				+ skills.getPercentToNextLevel(index), 9, 267);
		g.drawString("Times avoided combat: " + avoidedCombat, 9, 283);
		g.drawString("Smoking Rocks Avoided: " + avoidedSmokingRocks, 9, 299);
		g.drawString("Time running: " + hours + ":" + minutes + ":" + seconds,
				9, 315);
		g.drawString("  >>  http://decurse.net  <<", 9, 331);
	}

	@Override
	public boolean onStart(final Map<String, String> args) {
		try {
			mode = Integer.parseInt(args.get("mode"));
			powerMode = true;

			tiara = Integer.parseInt(args.get("tiara"));
			useTiara = true;

			// tos
			agreement = args.get("agreement") != null;
			if (!agreement) {
				log("The agreement to use GranitePro was not accepted. \n By not accepting the Terms of Use, you are not allowed to run the script.");
				return false;
			}
			log("  -  Agreement accepted.");
			log("  -  Powermining activated.");
			log("  -  Thank you for using GranitePro v"
					+ getClass().getAnnotation(ScriptManifest.class).version());

			return true;
			// end
		} catch (final Exception ignored) {

		}
		return true;
	}

	public void runAway() { // running away and running back after a while
		RSTile foo, bar;
		foo = getMyPlayer().getLocation();
		setRun(true);

		bar = new RSTile(foo.getX() + random(10, 12), foo.getY()
				+ random(10, 12));

		walkTo(bar);
		while (getMyPlayer().isMoving()) {
			wait(random(2000, 4000));
		}
		wait(random(3000, 5000));
		walkTo(foo);
	}

	public void serverMessageRecieved(final ServerMessageEvent e) {
		final String word = e.getMessage().toLowerCase();
		if (word.contains("died")) {
			log("You have died!  Logging you out to prevent you from being banned.");
			logout();
			stopScript();
		}
	}

	private boolean walkPath(final RSTile[] path) {
		if (distanceTo(path[path.length - 1]) <= 2) {
			return true;
		} else {
			if (!getMyPlayer().isMoving()) {
				walkPathMM(randomizePath(path, 2, 2), 20);
			}
		}

		return false;
	}

	private void walkTileRand(final RSTile tile) {
		// Local objects:
		final RSTile temp = new RSTile(tile.getX() + random(-2, 2), tile.getY()
				+ random(-2, 2));

		walkTo(temp);
	}

}
