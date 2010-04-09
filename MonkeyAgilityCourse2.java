import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Map;

import org.rsbot.bot.Bot;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSPlayer;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = { "Jacmob/PeetRoot" }, category = "Agility", name = "Monkey Agility", version = 3.39, description = "<html>\n<body style=\"font-family: Arial; background-color: #DDDDFF;\">\n<div style=\"width: 100%; height: 35px; background-color: #BBEEBB; text-align: center;\"\n<h2 style=\"color: #118811;\">Monkey Agility Course</h2>\n</div>\n<div style=\"width:100%; background-color: #007700; text-align:center; color: #FFFFFF; height: 15px;\">Jacmob/PeetRoot | Version 3.39</div>\n<div style=\"width: 100%; padding: 10px; padding-bottom: 12px; background-color: #EEEEFF;\">Start in the Monkey Agility Course.<br><br>Bring a knife if you are below level 75.<br><br>Food and energy potions are supported.</div>\n<div style=\"width: 100%; padding: 10px;\">\n<h3>Auto Stop (Enter Runtime to Enable)</h3><input type=\"text\" name=\"hours\" id=\"hrs\" size=3 /><label for=\"hrs\" > : </label><input type=\"text\" name=\"mins\" id=\"mins\" size=3 /><label for=\"mins\"> : </label><input type=\"text\" name=\"secs\" id=\"secs\" size=3 /><label for=\"secs\"> (hrs:mins:secs)</label><br /><br /><input type=\"checkbox\" name=\"chkXP\" id=\"chkXP\" value=\"true\" /><label for=\"debug\">Check XP (Extra AntiBan)</label></div>\n</body>\n</html>")
public class MonkeyAgilityCourse2 extends Script implements PaintListener {
	public static final int[] energyPot = new int[] { 3014, 3012, 3010, 3008,
			3022, 3020, 3018, 3016 };
	public static final int[] Food = new int[] { 333, 385, 379, 285, 373, 365,
			7946, 361, 397, 391, 1963, 329, 2118 };
	public static final int[] knifeID = new int[] { 946 };
	public static final int[] PAID = new int[] { 2114 };
	private final Color BG = new Color(123, 123, 123, 100);

	private boolean checkXP = false;
	private int currentFails = 0;
	private int DrinkingEnergy = -1;
	private final Color GREEN = new Color(0, 255, 0, 255);
	private final Color GREENBAR = new Color(0, 255, 0, 150);
	private boolean lapJustDone = false;
	public int LapsDone = 0;
	private boolean notStarted = true;
	private int pickWhenBelow = 8;
	private final Color RED = new Color(255, 0, 0, 150);

	private int RunningEnergy = random(15, 30);
	private int startingxp = -1;
	private long startTime = -1;
	private long stopTime = -1;

	private boolean atTile2(final RSTile tile, final int h, final double xd,
			final double yd, final String action) {
		try {
			final Point location = Calculations.tileToScreen(tile.getX(), tile
					.getY(), .5, .5, h);
			if (location.x == -1 || location.y == -1) {
				return false;
			}
			moveMouse(location, 5, 5);
			if (getMenuItems().get(0).toLowerCase().contains(
					action.toLowerCase())) {
				wait(random(100, 150));
				clickMouse(true);
				wait(random(100, 150));
			} else {
				moveMouse(location, 5, 5);
				if (getMenuItems().get(0).toLowerCase().contains(
						action.toLowerCase())) {
					wait(random(100, 150));
				}
				clickMouse(true);
				return false;
			}
			wait(random(100, 300));
			while (getMyPlayer().isMoving()) {
				wait(random(50, 100));
			}
			return true;
		} catch (final Exception e) {
			return false;
		}
	}

	private void Drink() {
		while (getInventoryCount(MonkeyAgilityCourse2.energyPot) >= 1
				&& getEnergy() <= DrinkingEnergy) {
			DrinkingEnergy = random(10, 40);
			for (final int element : MonkeyAgilityCourse2.energyPot) {
				if (getInventoryCount(element) == 0) {
					continue;
				}
				log("Drinking energy potion.");
				atInventoryItem(element, "Drink");
				wait(random(500, 800));
				break;
			}
		}
	}

	private boolean Eat() {
		for (final int element : MonkeyAgilityCourse2.Food) {
			if (getInventoryCount(element) >= 1) {
				atInventoryItem(element, "Eat");
				return true;
			}
		}
		return false;
	}

	private RSTile[] generatePath(int startX, int startY,
			final int destinationX, final int destinationY) { // most credits to
		// aftermath
		double dx, dy;
		final ArrayList<RSTile> list = new ArrayList<RSTile>();

		list.add(new RSTile(startX, startY));
		while (Math.hypot(destinationY - startY, destinationX - startX) > 8) {
			dx = destinationX - startX;
			dy = destinationY - startY;
			final int gamble = random(14, 17);
			while (Math.hypot(dx, dy) > gamble) {
				dx *= .95;
				dy *= .95;
			}
			startX += (int) dx;
			startY += (int) dy;
			list.add(new RSTile(startX, startY));
		}
		list.add(new RSTile(destinationX, destinationY));
		return list.toArray(new RSTile[list.size()]);

	}

	private RSTile[] generatePath(final RSTile tile) {
		return generatePath(getMyPlayer().getLocation().getX(), getMyPlayer()
				.getLocation().getY(), tile.getX(), tile.getY());
	}

	private String getFormattedTime(final long timeMillis) {
		long millis = timeMillis;
		final long seconds2 = millis / 1000;
		final long hours = millis / (1000 * 60 * 60);
		millis -= hours * 1000 * 60 * 60;
		final long minutes = millis / (1000 * 60);
		millis -= minutes * 1000 * 60;
		final long seconds = millis / 1000;
		String hoursString = "";
		String minutesString = "";
		String secondsString = seconds + "";
		String type = "seconds";

		if (minutes > 0) {
			minutesString = minutes + ":";
			type = "minutes";
		} else if (hours > 0 && seconds2 > 0) {
			minutesString = "0:";
		}
		if (hours > 0) {
			hoursString = hours + ":";
			type = "hours";
		}
		if (minutes < 10 && type != "seconds") {
			minutesString = "0" + minutesString;
		}
		if (hours < 10 && type == "hours") {
			hoursString = "0" + hoursString;
		}
		if (seconds < 10 && type != "seconds") {
			secondsString = "0" + secondsString;
		}

		return hoursString + minutesString + secondsString + " " + type;
	}

	private int getState() {

		final RSTile StartCoord = new RSTile(2767, 2744);
		final RSTile HillCoord = new RSTile(2759, 2742);
		final RSTile Hill2Coord = new RSTile(2759, 2745);
		final RSTile Fall1Coord = new RSTile(2757, 2748);
		final RSTile StoneCoord = new RSTile(2756, 2743);
		final RSTile TreeCoord = new RSTile(2753, 2742);
		final RSTile Tree2Coord = new RSTile(2752, 2742);
		final RSTile SkullyCoord = new RSTile(2747, 2741);
		final RSTile Skully2Coord = new RSTile(2743, 2741);
		final RSTile Rope1Coord = new RSTile(2751, 2731);
		final RSTile Rope2Coord = new RSTile(2757, 2732);
		final RSTile Rope3Coord = new RSTile(2745, 2734);
		final RSTile Rope4Coord = new RSTile(2757, 2736);
		final RSTile AppleCoord = new RSTile(2774, 2748);

		if (distanceTo(StartCoord) < 6
				&& getPlane() == 0
				&& (getInventoryCount() >= 9 || skills
						.getCurrentSkillLevel(Constants.STAT_AGILITY) >= 75)) {
			return 1;
		}
		if (distanceTo(StoneCoord) < 3 && getPlane() == 0) {
			return 1;
		}
		if (distanceTo(Fall1Coord) < 7 && getPlane() == 0) {
			return 1;
		}
		if (distanceTo(HillCoord) < 5 && getPlane() == 0) {
			return 1;
		}
		if (distanceTo(Hill2Coord) < 5 && getPlane() == 0) {
			return 1;
		}
		if (distanceTo(TreeCoord) < 1 && getPlane() == 0) {
			return 2;
		}
		if (distanceTo(Tree2Coord) < 1 && getPlane() == 0) {
			return 2;
		}
		if (distanceTo(TreeCoord) < 2 && getPlane() == 2) {
			return 3;
		}
		if (distanceTo(SkullyCoord) < 2 && getPlane() == 0) {
			return 4;
		}
		if (distanceTo(Skully2Coord) < 3 && getPlane() == 0) {
			return 5;
		}
		if (distanceTo(Rope3Coord) <= 6 && getPlane() == 0) {
			return 5;
		}
		if (distanceTo(Rope1Coord) < 4 && getPlane() == 0) {
			return 5;
		}
		if (distanceTo(Rope2Coord) < 4 && getPlane() == 0) {
			return 6;
		}
		if (distanceTo(Rope4Coord) < 3 && getPlane() == 0) {
			return 6;
		}
		if (distanceTo(StartCoord) < 7 && getPlane() == 0
				&& getInventoryCount(MonkeyAgilityCourse2.Food) < pickWhenBelow
				&& skills.getCurrentSkillLevel(Constants.STAT_AGILITY) < 75) {
			return 7;
		}
		if (distanceTo(AppleCoord) < 3 && getInventoryCount() < 25
				&& skills.getCurrentSkillLevel(Constants.STAT_AGILITY) < 75) {
			return 7;
		}
		if (distanceTo(AppleCoord) < 5 && getInventoryCount() >= 25) {
			while (distanceTo(StartCoord) > 3) {
				walkTo(StartCoord);
				wait(random(900, 1200));
			}
			return 1;
		}
		return -1;
	}

	private void hoverAgility() {
		final RSInterfaceChild agitab = RSInterface.getInterface(320).getChild(
				134);
		openTab(Constants.TAB_STATS);
		moveMouse(new Point(agitab.getAbsoluteX()
				+ random(2, agitab.getWidth() - 1), agitab.getAbsoluteY()
				+ random(2, agitab.getHeight() - 1)));
		wait(random(900, 2000));
		openTab(Constants.TAB_INVENTORY);
	}

	@Override
	public int loop() {
		if (currentFails > 100) {
			log("The script has failed multiple times. Logging off.");
			stopScript();
		}

		if (stopTime != -1 && startTime != -1
				&& System.currentTimeMillis() - startTime > stopTime) {
			log("Stop Time Reached. Logging off in 10 seconds.");
			wait(random(10000, 12000));
			stopScript();
		}

		if (checkXP && random(1, 1000) == 1) {
			hoverAgility();
		}

		if (getEnergy() >= RunningEnergy && !isRunning()) {
			setRun(true);
			RunningEnergy = random(15, 30);
			wait(random(400, 500));
		}

		if (getEnergy() != 0 && getEnergy() <= DrinkingEnergy) {
			Drink();
		}

		if (skills.getCurrentSkillLevel(3) <= 15) {
			log("Health is below 15. Eating food...");
			if (!Eat()) {
				log("No food to eat. Waiting 10 seconds.");
				wait(random(10000, 12000));
				if (skills.getCurrentSkillLevel(3) > 17) {
					log("Fail correction activated. Logout cancelled.");
					return random(100, 200);
				}
				logout();
				log("No food to eat. Logged out.");
				Bot.getScriptHandler().stopScript(ID);
			} else {
				wait(random(800, 1000));
				Eat();
				return random(400, 500);
			}
		}
		final RSPlayer me = getMyPlayer();
		if (me.getAnimation() != -1 || me.isMoving()) {
			if (distanceTo(new RSTile(2749, 2741)) < 2) {
				setCameraRotation(random(73, 77));
			} else if (distanceTo(new RSTile(2744, 2741)) < 3
					&& getMouseLocation().getX() < 570) {
				moveMouse(570 + random(0, 40), 120 + random(0, 25));
				wait(random(200, 400));
			} else if (distanceTo(new RSTile(2760, 2737)) < 3
					&& getPlane() == 1 && getMouseLocation().getX() < 570) {
				moveMouse(570 + random(0, 30), 100 + random(0, 25));
				wait(random(200, 400));
			} else if (distanceTo(new RSTile(2750, 2731)) < 5
					&& getMouseLocation().getX() > 569) {
				moveMouse(240 + random(0, 40), 120 + random(0, 40));
				wait(random(200, 300));
			}
			return random(50, 100);
		} else {
			wait(random(100, 200));
		}

		final int state = getState();
		if (lapJustDone) {
			if (state != -1 && state != 6) {
				lapJustDone = false;
			}
		}
		final RSTile spot = tile(state);
		switch (state) {
		case 0:
			break;
		case -1: // failure!
			if (currentFails > 30) {
				walkPathMM(randomizePath(generatePath(new RSTile(2756, 2743)),
						2, 2), 16);
			}
			currentFails++;
			break;

		case 1: // Stone
			if (distanceTo(new RSTile(2754, 2742)) == 0) {
				final RSTile tile = new RSTile(2752, 2741);
				final Point location = Calculations.tileToScreen(tile.getX(),
						tile.getY(), .5, .5, 0);
				if (location.x == -1 || location.y == -1) {
					return random(20, 50);
				}
				moveMouse(location, 3, 3);
				return random(50, 100);
			}
			if (distanceTo(new RSTile(2757, 2748)) == 0) {
				currentFails = 0;
			}
			final Point location1 = Calculations.tileToScreen(spot);
			if (location1.x == -1 || location1.y == -1
					|| distanceTo(new RSTile(2755, 2742)) > 2) {
				if (!walkTo(new RSTile(2755 + random(0, 1), 2742))) {
					walkPathMM(randomizePath(generatePath(spot), 2, 2), 16);
				}
				wait(random(200, 300));
				if (!walkTo(new RSTile(2755 + random(0, 1), 2742))) {
					walkPathMM(randomizePath(generatePath(spot), 2, 2), 16);
				}
				wait(random(50, 100));
				setCameraRotation(random(80, 100));
			} else {
				moveMouse(location1.x + 6, location1.y + 3, 2, 2);
				wait(random(20, 50));
				if (getMenuItems().get(0).toLowerCase().contains(
						"Jump-".toLowerCase())) {
					clickMouse(true);
					wait(random(600, 800));
				} else {
					turnToTile(spot);
					wait(random(20, 40));
					currentFails++;
				}
			}

			break;

		case 2: // Tree
			if (getMenuItems().get(0).toLowerCase().contains("climb")) {
				clickMouse(true);
				wait(random(2000, 2200));
			} else {
				if (!atTile2(spot, random(50, 200), .5, .5, "Climb")) {
					if (distanceTo(spot) > 6) {
						walkPathMM(randomizePath(generatePath(spot), 2, 2), 16);
					} else {
						turnToTile(spot);
						currentFails++;
					}
				} else {
					wait(random(2000, 2200));
				}
			}
			break;

		case 3: // Bars
			if (!atTile2(spot, random(125, 135), .5, .5, "Swing")) {
				if (distanceTo(spot) > 6) {
					walkPathMM(randomizePath(generatePath(spot), 2, 2), 16);
				} else {
					turnToTile(spot);
					currentFails++;
				}
			} else {
				wait(random(600, 800));
			}
			break;

		case 4: // Skulls
			wait(random(20, 50));
			setCameraRotation(random(73, 77));
			wait(random(20, 50));
			if (!atTile2(spot, 35, 5.0, .0, "Climb-up")) {
				if (distanceTo(spot) > 6) {
					walkPathMM(randomizePath(generatePath(spot), 2, 2), 16);
				} else {
					currentFails++;
				}
				wait(random(30, 80));
			} else {
				wait(random(300, 400));
			}
			break;

		case 5: // Rope
			final Point location2 = Calculations.tileToScreen(spot);
			if (location2.x == -1 || location2.y == -1
					|| distanceTo(new RSTile(2751, 2731)) > 0) {
				if (distanceTo(new RSTile(2751, 2731)) > 1) {
					walkTo(new RSTile(2751, 2731));
				} else {
					atTile2(new RSTile(2751, 2731), 0, 5, 5, "Walk here");
				}
				setCameraRotation(random(255, 275));
				setCameraAltitude(random(20, 30));
				wait(random(400, 700));
			} else {
				moveMouse((int) location2.getX() + random(-1, 1),
						(int) location2.getY() + random(-30, -15));
				wait(random(30, 60));
				if (getMenuItems().get(0).toLowerCase().contains("swing")) {
					clickMouse(true);
					wait(random(1300, 1400));
				} else {
					if (currentFails > 5) {
						turnToTile(spot);
					}
					wait(random(10, 20));
					currentFails++;
				}
			}

			break;

		case 6: // Vine
			if (!lapJustDone) {
				LapsDone++;
				lapJustDone = true;
				currentFails = 0;
				setCameraAltitude(random(30, 70));
			}
			if (!atTile2(spot, random(50, 200), .5, .5, "Climb-down")) {
				if (distanceTo(spot) > 6) {
					walkPathMM(randomizePath(generatePath(spot), 2, 2), 16);
				} else {
					turnToTile(spot);
				}
			} else {
				wait(random(500, 700));
			}
			break;

		case 7: // apple eating
			if (distanceTo(spot) > 2) {
				walkPathMM(randomizePath(generatePath(spot), 2, 2), 16);
			} else {
				if (getInventoryItemByID(MonkeyAgilityCourse2.knifeID) == null) {
					log("You have no knife and no food. Logging out.");
					logout();
					stopScript();
				}
				while (distanceTo(getNearestObjectByID(4827).getLocation()) > 3) {
					wait(random(300, 500));
				}
				if (!atTile2(spot, random(-20, 50), .5, .5, "Pick Pi")) {
					clickMouse(true);
					wait(random(10, 100));
					setCameraRotation(random(260, 280));
				}
				wait(random(500, 700));
				if (getInventoryItemByID(MonkeyAgilityCourse2.PAID) != null) {
					while (getInventoryCount(MonkeyAgilityCourse2.PAID) > 1) {
						atInventoryItem(MonkeyAgilityCourse2.PAID[0], "Drop");
						wait(random(700, 1000));
					}
					useItem(getInventoryItemByID(MonkeyAgilityCourse2.knifeID),
							getInventoryItemByID(MonkeyAgilityCourse2.PAID));
					moveMouse(130, 390, 20, 20);
					wait(random(500, 800));
					clickMouse(true);
					wait(random(1000, 1500));
				}
			}
			break;

		default: // Stop Script
			return -1;
		}
		return random(100, 200);
	}

	@Override
	public void onFinish() {
		log("Gained "
				+ (skills.getCurrentSkillExp(Constants.STAT_AGILITY) - startingxp)
				+ " XP ("
				+ (skills.getRealSkillLevel(Constants.STAT_AGILITY) - skills
						.getLvlByExp(startingxp)) + " levels) in "
				+ getFormattedTime(System.currentTimeMillis() - startTime)
				+ ".");
	}

	public void onRepaint(final Graphics g) {
		if (isLoggedIn()) {
			if (startingxp == -1 && notStarted) {
				if (skills.getRealSkillLevel(Constants.STAT_AGILITY) == 1) {
					return;
				} else if (skills.getRealSkillLevel(Constants.STAT_AGILITY) < 60) {
					pickWhenBelow = 14;
				} else if (skills.getRealSkillLevel(Constants.STAT_AGILITY) < 50) {
					pickWhenBelow = 17;
				}
				if (skills.getRealSkillLevel(Constants.STAT_AGILITY) < 75) {
					log("Picking pineapples when less than " + pickWhenBelow
							+ " food items are in the inventory.");
				} else if (skills.getRealSkillLevel(Constants.STAT_AGILITY) < 80) {
					log("Pinapple picking disabled, you're too good now.");
				} else {
					log("Pinapple picking disabled, you're far too good for them.");
				}
				startingxp = skills.getCurrentSkillExp(Constants.STAT_AGILITY);
				startTime = System.currentTimeMillis();
				notStarted = false;
				DrinkingEnergy = random(10, 40);
				if (getState() == -1) {
					walkPathMM(randomizePath(
							generatePath(new RSTile(2756, 2743)), 2, 2), 16);
				}
			}

			final int x = 13;
			int y = 21;

			final int levelsGained = skills
					.getRealSkillLevel(Constants.STAT_AGILITY)
					- skills.getLvlByExp(startingxp);
			final long runSeconds = (System.currentTimeMillis() - startTime) / 1000;

			g.setColor(BG);
			if (runSeconds != 0) {
				g.fill3DRect(8, 25, 210, 164, true);
			} else {
				g.fill3DRect(8, 25, 210, 123, true);
			}

			g.setColor(GREEN);
			g.drawString(getClass().getAnnotation(ScriptManifest.class).name()
					+ " v"
					+ getClass().getAnnotation(ScriptManifest.class).version(),
					x, y += 20);
			g.drawString(getClass().getAnnotation(ScriptManifest.class).name()
					+ " v"
					+ getClass().getAnnotation(ScriptManifest.class).version(),
					x, y);
			g.drawString("Running for "
					+ getFormattedTime(System.currentTimeMillis() - startTime)
					+ ".", x, y += 20);

			if (levelsGained < 0) {
				startingxp = skills.getCurrentSkillExp(Constants.STAT_AGILITY);
			} else if (levelsGained == 1) {
				g
						.drawString(
								"Gained: "
										+ (skills
												.getCurrentSkillExp(Constants.STAT_AGILITY) - startingxp)
										+ " XP (" + levelsGained + " lvl)", x,
								y += 20);
			} else {
				g
						.drawString(
								"Gained: "
										+ (skills
												.getCurrentSkillExp(Constants.STAT_AGILITY) - startingxp)
										+ " XP (" + levelsGained + " lvls)", x,
								y += 20);
			}

			if (runSeconds > 0) {
				g
						.drawString(
								"Averaging: "
										+ (skills
												.getCurrentSkillExp(Constants.STAT_AGILITY) - startingxp)
										* 3600 / runSeconds + " XP/hr", x,
								y += 20);
			}

			g.drawString("Laps done: " + LapsDone, x, y += 20);
			g.drawString("Current level: "
					+ skills.getRealSkillLevel(Constants.STAT_AGILITY), x,
					y += 20);
			g.drawString("Next level: "
					+ skills.getXPToNextLevel(Constants.STAT_AGILITY) + " XP",
					x, y += 20);
			if (runSeconds != 0) {
				g.setColor(RED);
				g.fill3DRect(x, y += 9, 200, 13, true);
				g.setColor(GREENBAR);
				g.fill3DRect(x, y, skills
						.getPercentToNextLevel(Constants.STAT_AGILITY) * 2, 13,
						true);
			}
		}
	}

	@Override
	public boolean onStart(final Map<String, String> args) {
		if (args.get("chkXP") == null) {
			checkXP = false;
		}
		if (!(args.get("hours").equals("") && args.get("mins").equals("") && args
				.get("secs").equals(""))) {
			int sHours = 0, sMins = 0, sSecs = 0;
			if (!args.get("hours").equals("")) {
				sHours = Integer.parseInt(args.get("hours"));
			}
			if (!args.get("mins").equals("")) {
				sMins = Integer.parseInt(args.get("mins"));
			}
			if (!args.get("secs").equals("")) {
				sSecs = Integer.parseInt(args.get("secs"));
			}
			stopTime = sHours * 3600000 + sMins * 60000 + sSecs * 1000;
			log("Script will stop after " + getFormattedTime(stopTime));
		}
		return true;
	}

	private RSTile tile(final int state) {
		switch (state) {
		case 1: // steppingstone
			return new RSTile(2754, 2742);
		case 2: // treeclimb
			return new RSTile(2752, 2741);
		case 3: // monkeybars
			return new RSTile(2752, 2741);
		case 4: // skullpod
			return new RSTile(2746, 2741);
		case 5: // ropeswing
			return new RSTile(2752, 2731);
		case 6: // thevine
			return new RSTile(2757, 2734);
		case 7: // pineapples
			return new RSTile(2775, 2748);

		}
		return null;
	}
}