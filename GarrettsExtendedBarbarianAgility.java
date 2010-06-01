import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Map;

import org.rsbot.bot.Bot;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.Skills;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSTile;
import org.rsbot.script.wrappers.RSObject;

@ScriptManifest(authors = { "Garrett" }, category = "Agility", name = "Garrett's Extended Barbarian Agility", version = 1.00, description = "<html><head>"
		+ "</head><body>"
		+ "<center><strong><h2>Garrett's Extended Barbarian Agility</h2></strong></center>"
		+ "<strong>Start at the Barbarian Agility Course at the Rope Swing</strong><br />"
		+ "Food ID: <input name='FOODID' type='text' width='10' value='379' /><br />"
		+ "Eat Food Between: <input name='HEALTH1' type='text' width='3' value='15' /> - <input name='HEALTH2' type='text' width='3' value='25' /> HP"
		+ "</body></html>")
public class GarrettsExtendedBarbarianAgility extends Script implements
		PaintListener {

	final ScriptManifest properties = getClass().getAnnotation(
			ScriptManifest.class);

	// OTHER VARIABLES
	private long scriptStartTime = 0;
	private int runEnergy = random(40, 95);
	private boolean setAltitude = true;
	private int FOODID;
	private int HEALTH1;
	private int HEALTH2;
	private int randomHealth = random(15, 20);
	private int startXP = 0;
	private int startLvl = 0;

	private enum State {
		rope, log, wall, climb, spring, walk, jump, slide, error;
	};

	private State getState() {
		if (playerInArea(2535, 3553, 2531, 3553) && getPlane() == 3)
			return State.walk;
		if (playerInArea(2537, 3553, 2535, 3553) && getPlane() == 3)
			return State.jump;
		if (!inventoryContains(FOODID) && getHealth() < randomHealth) {
			log("You do not have any food in your inventory and your health is low.");
			return State.error;
		}
		if (playerInArea(2555, 3559, 2543, 3550))
			return State.rope;
		if (playerInArea(2553, 3549, 2544, 3542))
			return State.log;
		if (playerInArea(2541, 3547, 2536, 3542) && getPlane() == 0)
			return State.wall;
		if (playerInArea(2538, 3547, 2537, 3545) && getPlane() == 2)
			return State.climb;
		if (playerInArea(2537, 3547, 2532, 3545) && getPlane() == 3)
			return State.spring;
		if (playerInArea(2542, 3554, 2538, 3552)
				&& (getPlane() == 2 || getPlane() == 1))
			return State.slide;
		return State.rope;
	}

	// *******************************************************//
	// ON START
	// *******************************************************//
	public boolean onStart(Map<String, String> args) {
		FOODID = Integer.parseInt(args.get("FOODID"));
		HEALTH1 = Integer.parseInt(args.get("HEALTH1"));
		HEALTH2 = Integer.parseInt(args.get("HEALTH2"));
		randomHealth = random(HEALTH1, HEALTH2);
		scriptStartTime = System.currentTimeMillis();
		return true;
	}

	// *******************************************************//
	// MAIN LOOP
	// *******************************************************//
	public int loop() {
		if (!(isLoggedIn()))
			return 50;
		if (startLvl == 0) {
			startXP = skills.getCurrentSkillExp(Skills.getStatIndex("agility"));
			startLvl = skills.getCurrentSkillLevel(Skills
					.getStatIndex("agility"));
			return 50;
		}
		if (setAltitude) {
			setCameraAltitude(true);
			wait(random(250, 500));
			setAltitude = false;
			return 50;
		}
		if (getHealth() < randomHealth) {
			leftClickInventoryItem(FOODID);
			randomHealth = random(HEALTH1, HEALTH2);
			wait(random(600, 800));
			return 50;
		}
		startRunning(runEnergy);
		antiBan();
		switch (getState()) {
		case rope:
			doRope();
			return 50;
		case log:
			doLog();
			return 50;
		case wall:
			doWall();
			return 50;
		case climb:
			doClimb();
			return 50;
		case spring:
			doSpring();
			return 50;
		case walk:
			doWalk();
			return 50;
		case jump:
			doJump();
			return 50;
		case slide:
			doSlide();
			return 50;
		case error:
			return -1;
		}

		return 50;
	}

	// *******************************************************//
	// OTHER METHODS
	// *******************************************************//
	private void doRope() {
		final RSTile rope = new RSTile(2551 + random(0, 2), 3553);
		final RSTile ladder = new RSTile(2547, 9951);
		final RSTile walkHere = new RSTile(2551, 3554);
		if (playerInArea(2555, 9955, 2546, 9948)) {
			if (getCameraAngle() < 85 || getCameraAngle() > 95) {
				setCameraRotation(random(85, 95));
				wait(random(100, 200));
				return;
			}
			if (onTile(ladder, "Ladder", "Climb-up", 0.5, 0.5, 40))
				wait(random(750, 1000));
			return;
		}
		if ((!playerInArea(2554, 3555, 2549, 3554)) && getPlane() == 0) {
			walkTile(walkHere);
			wait(random(700, 1000));
			return;
		}
		if (onTile(rope, "Ropeswing", "Swing-on", 0.5, 0, 450))
			wait(random(3300, 4700));
		while (getMyPlayer().getAnimation() == 751 || getMyPlayer().isMoving())
			wait(100);
		return;
	}

	private void doLog() {
		final RSTile log = new RSTile(2550, 3546);
		if (onTile(log, "Log balance", "Walk-across", 0.5, 0.4, 0))
			wait(random(500, 750));
		while (getMyPlayer().isMoving() || playerInArea(2550, 3546, 2542, 3546))
			wait(100);
		return;
	}

	private void doWall() {
		if (getMyPlayer().isMoving())
			return;
		final RSTile wall = new RSTile(2538, 3541);
		final RSTile walkHere = new RSTile(2538, 3542);
		if (getPlane() == 0 && playerInArea(2541, 3547, 2536, 3542)
				&& (!(playerInArea(2538, 3542, 2538, 3542)))) {
			walkTile(walkHere);
			wait(random(500, 750));
			while (getMyPlayer().isMoving())
				wait(100);
			return;
		}
		if (onTile(wall, "Wall", "Run-up", 0.5, 0.9, 6))
			;
		wait(random(500, 750));
		while ((getMyPlayer().getAnimation() == 10492 || getMyPlayer()
				.getAnimation() == 10493)
				|| getMyPlayer().isMoving())
			wait(100);
		return;
	}

	private void doClimb() {
		if (getMyPlayer().isMoving())
			return;
		final RSTile climb = new RSTile(2537, 3546);
		if (onTile(climb, "Wall", "Climb-up", 0.1, 0.5, 7))
			wait(random(500, 750));
		while (getMyPlayer().isMoving())
			wait(100);
		return;
	}

	private void doSpring() {
		final RSTile spring = new RSTile(2533, 3545);
		if (onTile(spring, "Spring device", "Fire", 0.5, 0.6, 0))
			wait(random(500, 750));
		while (getMyPlayer().isMoving() || getMyPlayer().getAnimation() == 4189)
			wait(100);
		return;
	}

	private void doWalk() {
		if (getMyPlayer().isMoving())
			return;
		int waid = 43527;
		RSObject walk = getNearestObjectByID(waid);
		if (playerInArea(2535, 3553, 2531, 3553)) {
			if (atObject(walk, "Walk-across"))
				wait(random(500, 750));
			while (getMyPlayer().isMoving()
					|| (getMyPlayer().getAnimation() == 10295
							|| getMyPlayer().getAnimation() == 10290
							|| getMyPlayer().getAnimation() == 10477
							|| getMyPlayer().getAnimation() == 10478 || getMyPlayer()
							.getAnimation() == 10482))
				wait(100);
			return;
		}
	}

	private void doJump() {
		int jid = 43531;
		RSObject jump = getNearestObjectByID(jid);
		if (playerInArea(2537, 3553, 2536, 3553)) {
			if (atObject(jump, "Jump-over"))
				wait(random(500, 750));
			while (getMyPlayer().isMoving()
					|| getMyPlayer().getAnimation() == 2588)
				wait(100);
			return;
		}
	}

	private void doSlide() {
		int slid = 43532;
		RSObject slide = getNearestObjectByID(slid);
		if (playerInArea(2542, 3554, 2538, 3552)) {
			if (atObject(slide, "Slide-down"))
				wait(random(500, 750));
			while (getMyPlayer().isMoving()
					|| (getMyPlayer().getAnimation() == 11792
							|| getMyPlayer().getAnimation() == 11791
							|| getMyPlayer().getAnimation() == 11790 || getMyPlayer()
							.getAnimation() == 2588))
				wait(100);
			return;
		}
	}

	private void startRunning(final int energy) {
		if (getEnergy() >= energy && !isRunning()) {
			runEnergy = random(40, 95);
			setRun(true);
			wait(random(500, 750));
		}
	}

	private boolean playerInArea(int maxX, int maxY, int minX, int minY) {
		int x = getMyPlayer().getLocation().getX();
		int y = getMyPlayer().getLocation().getY();
		if (x >= minX && x <= maxX && y >= minY && y <= maxY) {
			return true;
		}
		return false;
	}

	public boolean leftClickInventoryItem(int itemID) {
		if (getCurrentTab() != TAB_INVENTORY)
			return false;
		int[] items = getInventoryArray();
		java.util.List<Integer> possible = new ArrayList<Integer>();
		for (int i = 0; i < items.length; i++) {
			if (items[i] == itemID) {
				possible.add(i);
			}
		}
		if (possible.size() == 0)
			return false;
		int idx = possible.get(possible.size() - 1);
		Point t = getInventoryItemPoint(idx);
		clickMouse(t, 5, 5, true);
		return true;
	}

	private int getHealth() {
		try {
			return Integer.parseInt(RSInterface.getChildInterface(748, 5)
					.getText());
		} catch (Exception e) {
			return 99;
		}
	}

	public boolean onTile(RSTile tile, String search, String action, double dx,
			double dy, int height) {
		if (!tile.isValid()) {
			return false;
		}

		Point checkScreen = null;
		checkScreen = Calculations.tileToScreen(tile, dx, dy, height);
		if (!pointOnScreen(checkScreen)) {
			walkTile(tile);
			wait(random(340, 1310));
		}

		try {
			Point screenLoc = null;
			for (int i = 0; i < 30; i++) {
				screenLoc = Calculations.tileToScreen(tile, dx, dy, height);
				if (!pointOnScreen(screenLoc)) {
					return false;
				}
				if (getMenuItems().get(0).toLowerCase().contains(
						search.toLowerCase())) {
					break;
				}
				if (getMouseLocation().equals(screenLoc)) {
					break;
				}
				moveMouse(screenLoc);
			}
			screenLoc = Calculations.tileToScreen(tile, height);
			if (getMenuItems().size() <= 1) {
				return false;
			}
			wait(random(100, 200));
			if (getMenuItems().get(0).toLowerCase().contains(
					action.toLowerCase())) {
				clickMouse(true);
				return true;
			} else {
				clickMouse(false);
				return atMenu(action);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private void walkTile(final RSTile tile) {
		if (!(distanceTo(getDestination()) <= random(4, 7))) {
			if (getMyPlayer().isMoving())
				return;
		}
		Point screen = Calculations.tileToScreen(tile);
		if (pointOnScreen(screen)) {
			if (getMyPlayer().isMoving())
				return;
			moveMouse(screen, random(-3, 4), random(-3, 4));
			onTile(tile, "here", "Walk");
			wait(random(500, 750));
			return;
		} else {
			walkTileMM(tile);
			wait(random(500, 750));
			return;
		}
	}

	private void antiBan() {
		int random = random(1, 24);

		switch (random) {
		case 1:
			if (random(1, 3) == 1)
				moveMouseRandomly(300);
			return;

		case 2:
			if ((random(1, 3) == 1) && (!(getPlane() == 3)))
				if (getCurrentTab() != TAB_INVENTORY)
					openTab(TAB_INVENTORY);
			return;

		case 3:
			if (random(1, 20) == 1) {
				int angle = getCameraAngle() + random(-90, 90);
				if (angle < 0)
					angle = 0;
				if (angle > 359)
					angle = 0;
				setCameraRotation(angle);
			}
			return;
		default:
			return;
		}
	}

	// *******************************************************//
	// ON FINISH
	// *******************************************************//
	public void onFinish() {
		Bot.getEventManager().removeListener(PaintListener.class, this);
	}

	// *******************************************************//
	// PAINT SCREEN
	// *******************************************************//
	public void onRepaint(Graphics g) {
		long runTime = 0;
		long seconds = 0;
		long minutes = 0;
		long hours = 0;
		int laps = 0;
		int currentXP = 0;
		int currentLVL = 0;
		int gainedXP = 0;
		int gainedLVL = 0;
		int lapsPerHour = 0;
		int hourlyXP = 0;
		final double courseXP = 745.7;

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

		currentXP = skills.getCurrentSkillExp(Skills.getStatIndex("agility"));
		currentLVL = skills
				.getCurrentSkillLevel(Skills.getStatIndex("agility"));
		gainedXP = currentXP - startXP;
		hourlyXP = (int) ((3600000.0 / (double) runTime) * gainedXP);
		gainedLVL = currentLVL - startLvl;
		laps = (int) (gainedXP / courseXP);
		lapsPerHour = (int) ((3600000.0 / (double) runTime) * laps);

		if (getCurrentTab() == TAB_INVENTORY) {
			g.setColor(new Color(0, 0, 0, 175));
			g.fillRoundRect(555, 210, 175, 250, 10, 10);
			g.setColor(Color.WHITE);
			int[] coords = new int[] { 225, 240, 255, 270, 285, 300, 315, 330,
					345, 360, 375, 390, 405, 420, 435, 450 };
			g.drawString(properties.name(), 561, coords[0]);
			g.drawString("Version: " + properties.version(), 561, coords[1]);
			g.drawString("Run Time: " + hours + ":" + minutes + ":" + seconds,
					561, coords[2]);
			g.drawString("Total Laps: " + laps, 561, coords[4]);
			g.drawString("Laps/Hour: " + lapsPerHour, 561, coords[5]);
			g.drawString("Current Lvl: " + currentLVL, 561, coords[7]);
			g.drawString("Lvls Gained: " + gainedLVL, 561, coords[8]);
			g.drawString("XP Gained: " + gainedXP, 561, coords[9]);
			g.drawString("XP per hour: " + hourlyXP, 561, coords[10]);
			g.drawString("XP To Next Level: "
					+ skills.getXPToNextLevel(Skills.getStatIndex("agility")),
					561, coords[11]);
			g.drawString("% To Next Level: "
					+ skills.getPercentToNextLevel(Skills
							.getStatIndex("agility")), 561, coords[12]);
		}
	}
}