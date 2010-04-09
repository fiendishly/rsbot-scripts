/*
 * @author Pauwelz
 * Start at Yanille Watchmen without food in inventory since Watchmen give bread, and the script uses that
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
import org.rsbot.script.wrappers.RSArea;
import org.rsbot.script.wrappers.RSItem;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = { "Pauwelz" }, category = "Thieving", name = "Watchman Thiever", version = 0.3, description = "<html><head><style type='text/css'>body{ font-family: sans-serif; font-size:10px; }</style></head><body><h1>Watchman Thiever v.3</h1><h2>By Pauwelz</h2><p>Start at Watchmen, <strong>without</strong> any food in your inventory.</p><p>I would like to have your feedback,<br />so please post it in the script's <a href='http://www.rsbot.org/vb/showthread.php?t=9871'>topic</a>.</p></body></html>")
public class PauwelzWatchmanThiever extends Script implements PaintListener,
		ServerMessageListener {

	private final int ID_BREAD = 2309;
	// IDS
	private final int ID_WATCHMAN = 34;
	private final int S_DOWNSTAIRS = 4;
	private final int S_DROP = 3;
	private final int S_EAT = 2;
	private final int S_FIRSTRUN = 0;

	private final int S_THIEVE = 1;
	// LOG STUFF
	private int startExperience = 0;

	private long startTime;
	// STATES
	private int state = 0;
	private String status = "";
	private boolean stunned = false;

	private void checkHP() {
		if (skills.getCurrentSkillLevel(Constants.STAT_HITPOINTS) < 10) {
			stopScript();
		}
		final double percentage = random(50, 90);
		if (skills.getCurrentSkillLevel(Constants.STAT_HITPOINTS) < percentage
				/ 100 * skills.getRealSkillLevel(Constants.STAT_HITPOINTS)) {

			state = S_EAT;

		}
	}

	private void checkLocation() {
		final RSArea watchmanArea = new RSArea(new RSTile(2543, 3111),
				new RSTile(2550, 3118));

		if (watchmanArea.contains(getMyPlayer().getLocation())
				|| distanceTo(new RSTile(2548, 3119)) < 8) {
			if (getPlane() == 0) {
				state = S_DOWNSTAIRS;
			}
		} else {
			log.severe("We are lost, shutting down");
			stopScript();
		}
	}

	private int drop() {
		final RSItem bread = getInventoryItemByID(ID_BREAD);
		if (bread != null) {
			atInventoryItem(ID_BREAD, "Drop");
			if (getInventoryCount(ID_BREAD) <= random(8, 10)) {
				state = S_THIEVE;
			}
			return random(1000, 1500);
		} else {
			log("Inventory filled with random stuff, won't risk to drop");
			stopScript();
			return random(100, 150);
		}
	}

	private int eat() {
		final RSItem bread = getInventoryItemByID(ID_BREAD);
		if (bread != null) {
			if (skills.getCurrentSkillLevel(Constants.STAT_HITPOINTS) == skills
					.getRealSkillLevel(Constants.STAT_HITPOINTS)) {
				state = S_THIEVE;
				return random(100, 250);
			}
			atInventoryItem(ID_BREAD, "Eat ");
			return random(1000, 1500);
		}
		state = S_THIEVE;
		return random(250, 500);
	}

	private int firstRun() {
		final RSArea watchmanArea = new RSArea(new RSTile(2543, 3111),
				new RSTile(2550, 3118));

		if (watchmanArea.contains(getMyPlayer().getLocation())) {
			startExperience = skills
					.getCurrentSkillExp(Constants.STAT_THIEVING);
			switch (getPlane()) {
			case 0:
				state = S_DOWNSTAIRS;
				break;
			case 1:
				state = S_THIEVE;
				break;
			}
			return random(250, 500);
		} else {
			log("You are not in the watchtower");
			stopScript();
			return random(150, 200);
		}
	}

	private int goUp() {
		final RSArea watchmanArea = new RSArea(new RSTile(2543, 3111),
				new RSTile(2550, 3118));
		if (getPlane() == 0 && getMyPlayer().isIdle()) {
			if (distanceTo(new RSTile(2548, 3119)) < 3) {
				atTile(new RSTile(2548, 3119), "Climb-up Trellis");
				setCameraAltitude(true);
				return random(1500, 2000);
			} else {
				setCameraAltitude(random(10, 20));
				setCameraRotation(random(100, 170));
				walkTo(new RSTile(2548 + random(-1, 1), 3119 + random(-1, 1)));
				return random(1500, 2000);
			}
		}
		if (watchmanArea.contains(getMyPlayer().getLocation())
				&& getPlane() == 1) {
			state = S_THIEVE;
		}
		return random(150, 200);
	}

	@Override
	public int loop() {
		if (stunned) {
			stunned = false;
			final int gamble = random(0, 10);
			if (gamble < 4) {
				state = S_EAT;
				return random(250, 500);
			} else {
				return random(3000, 4000);
			}
		}
		checkLocation();
		checkHP();
		switch (state) {
		case S_FIRSTRUN:
			status = "First Run";
			return firstRun();
		case S_THIEVE:
			status = "Thieving";
			return thieve();
		case S_EAT:
			status = "Eating";
			return eat();
		case S_DROP:
			status = "Dropping food";
			return drop();
		case S_DOWNSTAIRS:
			status = "Going up!";
			return goUp();
		default:
			return random(100, 200);
		}
	}

	public void onRepaint(final Graphics g) {
		long currentTime = System.currentTimeMillis() - startTime;
		final long runningHours = currentTime / (1000 * 60 * 60);
		currentTime -= runningHours * 1000 * 60 * 60;
		final long runningMinutes = currentTime / (1000 * 60);
		currentTime -= runningMinutes * 1000 * 60;
		final long runningSeconds = currentTime / 1000;

		g.setColor(new Color(0, 255, 0, 255));
		g.drawRect(3, 275, 230, 65);
		g.setColor(new Color(0, 255, 0, 50));
		g.fillRect(3, 275, 230, 65);
		g.setColor(Color.YELLOW);
		g.drawString("-- Watchman Thiever by Pauwelz v"
				+ getClass().getAnnotation(ScriptManifest.class).version()
				+ " --", 10, 290);
		g
				.drawString(
						"Experience Gained: "
								+ (skills
										.getCurrentSkillExp(Constants.STAT_THIEVING) - startExperience),
						10, 305);
		g.drawString("Runtime: " + pad(runningHours) + ":"
				+ pad(runningMinutes) + ":" + pad(runningSeconds), 10, 320);
		g.drawString("Status: " + status, 10, 335);
	}

	@Override
	public boolean onStart(final Map<String, String> map) {
		startTime = System.currentTimeMillis();
		state = S_FIRSTRUN;
		return true;
	}

	private String pad(final long n) {
		if (n <= 9) {
			return "0" + n;
		}
		return "" + n;
	}

	public void serverMessageRecieved(final ServerMessageEvent e) {
		final String msg = e.getMessage();
		if (msg.contains("stunned")) {
			stunned = true;
		}
	}

	private int thieve() {
		if (isInventoryFull()) {
			if (skills.getCurrentSkillLevel(Constants.STAT_HITPOINTS) != skills
					.getRealSkillLevel(Constants.STAT_HITPOINTS)) {
				state = S_EAT;
				return random(100, 200);
			} else {
				state = S_DROP;
				return random(100, 200);
			}
		}
		if (getMyPlayer().isInCombat()) {
			return random(250, 500);
		}
		if (getMyPlayer().isIdle()) {
			final RSNPC watchman = getNearestFreeNPCByID(ID_WATCHMAN);
			if (watchman != null) {
				atNPC(watchman, "Pickpocket");
				return random(500, 750);
			}
		}
		return random(250, 500);
	}
}
