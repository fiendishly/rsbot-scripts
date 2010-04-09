import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Map;

import org.rsbot.bot.Bot;
import org.rsbot.script.Calculations;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSPlayer;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = { "Jacmob" }, category = "Agility", name = "Barbarian Course", version = 1.0, description = "<html>\n<body style=\"font-family: Arial; background-color: #DDFFDD;\">\n<div style=\"width: 100%; height: 35px; background-color: #BBEEBB; text-align: center;\"\n<h2 style=\"color: #118811;\">Barbarian Course</h2>\n</div>\n<div style=\"width:100%; background-color: #007700; text-align:center; color: #FFFFFF; height: 15px;\">Jacmob"
		+ " | Version 1.0</div>\n<div style=\"width: 100%; padding: 10px; padding-bottom: 12px; background-color: #EEFFEE;\">Start in the Barbarian Agility Course.<br><br>Food and energy potions are supported.</div>\n<div style=\"width: 100%; padding: 10px;\">\n<h3>Auto Stop (Enter Runtime to Enable)</h3><input type=\"text\" name=\"hours\" id=\"hrs\" size=3 /><label for=\"hrs\" > : </label><input type=\"text\" name=\"mins\" id=\"mins\" size=3 /><label for=\"mins\"> : </label><input type=\"text\" name=\"secs\" id=\"secs\" size=3 /><label for=\"secs\"> (hrs:mins:secs)</label><br /><br /><input type=\"checkbox\" name=\"chkXP\" id=\"chkXP\" value=\"true\" /><label for=\"debug\">Check XP (Extra AntiBan)</label></div>\n</body>\n</html")
public class BarbarianCourse extends Script {
	public static final int[] Food = new int[] { 333, 385, 379, 285, 373, 365,
			7946, 361, 397, 391, 1963, 329, 2118 };
	public static final int[] energyPot = new int[] { 3014, 3012, 3010, 3008,
			3022, 3020, 3018, 3016 };
	public int LapsDone = 0;

	private boolean lapJustDone = false;
	private boolean lapBegun = false;
	private boolean checkXP = false;
	private int RunningEnergy = random(15, 30);
	private int DrinkingEnergy = -1;
	private int currentFails = 0;
	private int startingxp = -1;
	private long startTime = -1;
	private long stopTime = -1;

	private final Color BG = new Color(123, 123, 123, 100);
	private final Color GREEN = new Color(90, 200, 0, 255);
	private final Color GREENBAR = new Color(0, 255, 0, 150);
	private final Color RED = new Color(255, 0, 0, 150);

	private boolean atTile3(final RSTile tile, final String action) {
		return atTile3(tile, action, 0, 0, 10);
	}

	private boolean atTile3(final RSTile tile, final String action,
			final int xOffset, final int yOffset) {
		return atTile3(tile, action, xOffset, yOffset, 10);
	}

	// atTile3 -Jacmob
	private boolean atTile3(final RSTile tile, final String action,
			final int xOffset, final int yOffset, final int variation) {
		try {
			final Point location = Calculations.tileToScreen(tile);
			if (location.x == -1 || location.y == -1) {
				return false;
			}
			moveMouse(location.x + xOffset, location.y + yOffset, variation,
					variation);
			wait(random(30, 60));
			getMenuItems();
			final ArrayList<String> mis = getMenuItems();
			if (mis.get(0).contains(action)) {
				clickMouse(true);
			} else {
				for (int i = 1; i < mis.size(); i++) {
					if (mis.get(i).contains(action)) {
						clickMouse(false);
						if (atMenu(action)) {
							return true;
						}
					}
				}
				return false;
			}
			return true;
		} catch (final Exception e) {
			return false;
		}
	}

	private RSTile checkTile(final RSTile tile) { // most credits to Fusion89k
		if (distanceTo(tile) < 15) {
			return tile;
		}
		final RSTile loc = getMyPlayer().getLocation();
		final RSTile walk = new RSTile((loc.getX() + tile.getX()) / 2, (loc
				.getY() + tile.getY()) / 2);
		return tileOnMap(walk) ? walk : checkTile(walk);
	}

	private void Drink() {
		while (getInventoryCount(BarbarianCourse.energyPot) >= 1
				&& getEnergy() <= DrinkingEnergy) {
			DrinkingEnergy = random(10, 40);
			for (final int element : BarbarianCourse.energyPot) {
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
		for (final int element : BarbarianCourse.Food) {
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

		final RSTile StartCoord = new RSTile(2552, 3554);
		final RSTile FailCoord = new RSTile(2548, 9951);
		final RSTile Fail2Coord = new RSTile(2548, 3551);
		final RSTile LogCoord = new RSTile(2552, 3549);
		final RSTile Log2Coord = new RSTile(2551, 3546);
		final RSTile LogFailCoord = new RSTile(2545, 3542);
		final RSTile NetCoord = new RSTile(2541, 3546);
		final RSTile BalanceCoord = new RSTile(2537, 3546);
		final RSTile LadderCoord = new RSTile(2532, 3546);
		final RSTile WallCoord = new RSTile(2532, 3546);
		final RSTile Wall2Coord = new RSTile(2536, 3553);
		final RSTile Wall3Coord = new RSTile(2538, 3553);
		final RSTile EndCoord = new RSTile(2543, 3553);

		if (distanceTo(StartCoord) < 3) {
			return 1;
		}
		if (distanceTo(Fail2Coord) < 2 && getPlane() == 0) {
			return 10;
		}
		if ((distanceTo(LogCoord) < 3 || distanceTo(Log2Coord) < 2)
				&& getPlane() == 0) {
			return 2;
		}
		if (distanceTo(LogFailCoord) < 4 && getPlane() == 0) {
			return 3;
		}
		if (distanceTo(NetCoord) < 4 && getPlane() == 0) {
			return 4;
		}
		if (distanceTo(BalanceCoord) < 4 && getPlane() == 1) {
			return 5;
		}
		if (distanceTo(LadderCoord) < 3 && getPlane() == 1) {
			return 6;
		}
		if (distanceTo(WallCoord) < 4 && getPlane() == 0) {
			return 7;
		}
		if (distanceTo(Wall2Coord) < 4
				&& getMyPlayer().getLocation().getX() < 2538 && getPlane() == 0) {
			return 8;
		}
		if (distanceTo(Wall3Coord) < 6
				&& getMyPlayer().getLocation().getX() < 2543 && getPlane() == 0) {
			return 9;
		}
		if (distanceTo(EndCoord) < 5 && getPlane() == 0) {
			return 10;
		}
		if (distanceTo(FailCoord) < 20) {
			return 11;
		}

		return -1;
	}

	public double getVersion() {
		return 1.0;
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
		try {

			if (currentFails > 80) {
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
						log("Failure correction activated. Logout cancelled.");
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
			final int state = getState();

			if (me.getAnimation() != -1 || me.isMoving()) {
				if (lapJustDone) {
					setCameraRotation(random(175, 185));
					LapsDone++;
					lapJustDone = false;
				}
				return random(50, 200);
			}

			switch (state) {
			case 0:
				break;
			case -1: // Failure!
				if (currentFails > 30 && getPlane() == 0) {
					log("Unknown Location - Returning To Start");
					walkPathMM(randomizePath(
							generatePath(new RSTile(2552, 3554)), 0, 1), 16);
				}
				currentFails++;
				break;
			case 1: // Rope
				if (distanceTo(new RSTile(2552, 3554)) > 1) {
					walkTo(new RSTile(2552, 3554));
					wait(random(50, 100));
				} else if (atTile3(new RSTile(2552, 3552), "Swing-on")) {
					wait(random(800, 1000));
				} else {
					setCameraRotation(random(175, 185));
					currentFails++;
				}
				lapBegun = true;
				return random(50, 100);
			case 2: // Log
				if (atTile3(new RSTile(2550, 3546), "Walk-across", 0, 5)) {
					setCameraRotation(random(85, 95));
					wait(random(500, 600));
				} else {
					currentFails++;
				}
				break;
			case 3: // Log Failed
				walkTo(new RSTile(2551, 3546));
				wait(random(500, 700));
				break;
			case 4: // Net
				if (atTile3(new RSTile(2538, 3546), "Climb-over", 0, -10)) {
					wait(random(500, 600));
				} else {
					currentFails++;
				}
				wait(random(500, 600));
				break;
			case 5: // Ledge
				if (atTile3(new RSTile(2535, 3547), "Walk-across", 7, 0)) {
					wait(random(700, 900));
				} else if (currentFails > 0 && currentFails % 20 == 0) {
					turnToTile(new RSTile(2535, 3547));
				} else {
					currentFails++;
				}
				break;
			case 6: // Ladder
				if (atTile3(new RSTile(2532, 3545), "Climb-down")) {
					wait(random(700, 900));
				} else if (currentFails > 0 && currentFails % 20 == 0) {
					turnToTile(new RSTile(2532, 3545));
				} else {
					currentFails++;
				}
				break;
			case 7: // Walk To Crumbling Wall
				walkTo(new RSTile(2536, 3553));
				wait(random(50, 200));
				setCameraRotation(random(265, 285));
				wait(random(100, 400));
				break;
			case 8: // First Crumbling Wall
				if (atTile3(new RSTile(2538, 3553), "Climb-over", -4, 15)) {
					wait(random(400, 600));
				} else if (currentFails > 0 && currentFails % 20 == 0) {
					turnToTile(new RSTile(2538, 3553));
				} else {
					currentFails++;
				}
				break;
			case 9: // Second Crumbling Wall
				if (atTile3(new RSTile(2543, 3553), "Climb-over", 0, 15)) {
					wait(random(400, 600));
					if (lapBegun) {
						lapJustDone = true;
					}
					lapBegun = false;
				} else if (currentFails > 0 && currentFails % 20 == 0) {
					turnToTile(new RSTile(2543, 3553));
				} else {
					currentFails++;
				}
				break;
			case 10: // Return To Start
				currentFails = 0;
				walkTo(checkTile(new RSTile(2552, 3554)));
				setCameraRotation(random(175, 185));
				wait(random(400, 750));
				break;
			case 11:
				if (distanceTo(new RSTile(2547, 9951)) > 5) {
					walkTo(new RSTile(2548, 9951));
				}
				if (atTile3(new RSTile(2547, 9951), "Climb-up")) {
					wait(random(400, 600));
				} else if (currentFails > 0 && currentFails % 20 == 0) {
					turnToTile(new RSTile(2547, 9951));
				} else {
					currentFails++;
				}
				break;
			default: // Stop Script
				return -1;
			}

		} catch (final Exception e) {
			log.severe("SCRIPT ERROR");
			return 0;
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
		if (isLoggedIn()
				&& skills.getRealSkillLevel(Constants.STAT_AGILITY) > 1) {
			if (startingxp == -1) {
				startingxp = skills.getCurrentSkillExp(Constants.STAT_AGILITY);
				startTime = System.currentTimeMillis();
				DrinkingEnergy = random(10, 40);
				if (getState() == -1) {
					walkPathMM(randomizePath(
							generatePath(new RSTile(2474, 3436)), 2, 2), 16);
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
					+ " v" + getVersion(), x, y += 20);
			g.drawString(getClass().getAnnotation(ScriptManifest.class).name()
					+ " v" + getVersion(), x, y);
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
}