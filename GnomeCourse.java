import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Map;

import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSPlayer;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = { "Jacmob" }, category = "Agility", name = "Gnome Course", version = 1.0, description = "<html>\n<body style=\"font-family: Arial; background-color: #DDFFDD;\">\n<div style=\"width: 100%; height: 35px; background-color: #BBEEBB; text-align: center;\"\n<h2 style=\"color: #118811;\">Gnome Agility Course</h2>\n</div>\n<div style=\"width:100%; background-color: #007700; text-align:center; color: #FFFFFF; height: 15px;\">Jacmob | Version 1.0</div>\n<div style=\"width: 100%; padding: 10px; padding-bottom: 12px; background-color: #EEFFEE;\">Start at the beginning of the Gnome Agility Course.<br><br>Energy potions are supported, but not really needed.</div>\n<div style=\"width: 100%; padding: 10px;\">\n<h3>Auto Stop (Enter Runtime to Enable)</h3><input type=\"text\" name=\"hours\" id=\"hrs\" size=3 /><label for=\"hrs\" > : </label><input type=\"text\" name=\"mins\" id=\"mins\" size=3 /><label for=\"mins\"> : </label><input type=\"text\" name=\"secs\" id=\"secs\" size=3 /><label for=\"secs\"> (hrs:mins:secs)</label><br /><br /><input type=\"checkbox\" name=\"chkXP\" id=\"chkXP\" value=\"true\" /><label for=\"debug\">Check XP (Extra AntiBan)</label></div>\n</body>\n</html>")
public class GnomeCourse extends Script implements PaintListener {
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
	private final Color GREEN = new Color(0, 200, 0, 255);
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
		while (getInventoryCount(GnomeCourse.energyPot) >= 1
				&& getEnergy() <= DrinkingEnergy) {
			DrinkingEnergy = random(10, 40);
			for (final int element : GnomeCourse.energyPot) {
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

		final RSTile StartCoord = new RSTile(2474, 3436);
		final RSTile NetCoord = new RSTile(2474, 3427);
		final RSTile BranchCoord = new RSTile(2473, 3423);
		final RSTile RopeCoord = new RSTile(2473, 3420);
		final RSTile TreeCoord = new RSTile(2484, 3420);
		final RSTile Net2Coord = new RSTile(2487, 3423);
		final RSTile PipeCoord = new RSTile(2485, 3429);
		final RSTile EndCoord = new RSTile(2485, 3436);

		if (distanceTo(StartCoord) < 5) {
			return 1;
		}
		if (distanceTo(NetCoord) < 3 && getPlane() == 0) {
			return 2;
		}
		if (distanceTo(BranchCoord) < 4 && getPlane() == 1) {
			return 3;
		}
		if (distanceTo(RopeCoord) < 6 && getPlane() == 2) {
			return 4;
		}
		if (distanceTo(TreeCoord) < 5 && getPlane() == 2) {
			return 5;
		}
		if (distanceTo(Net2Coord) < 7 && getPlane() == 0
				&& getMyPlayer().getLocation().getY() <= 3425) {
			return 6;
		}
		if (distanceTo(PipeCoord) < 7 && getPlane() == 0
				&& getMyPlayer().getLocation().getY() <= 3431) {
			return 7;
		}
		if (distanceTo(EndCoord) < 5 && getPlane() == 0) {
			return 8;
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
		try {

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

			final RSPlayer me = getMyPlayer();
			final int state = getState();

			if (me.getAnimation() != -1 || me.isMoving()) {
				if (lapJustDone) {
					setCameraRotation(random(175, 185));
					if (lapBegun) {
						LapsDone++;
					}
					lapBegun = false;
					lapJustDone = false;
				}
				return random(50, 200);
			}

			switch (state) {
			case 0:
				break;
			case -1: // Failure!
				if (currentFails > 30 && getPlane() == 0) {
					walkPathMM(randomizePath(
							generatePath(new RSTile(2474, 3437)), 1, 1), 16);
				}
				currentFails++;
				break;
			case 1: // Log
				if (atTile3(new RSTile(2474, 3435), "Walk-across")) {
					setCameraRotation(random(175, 185));
					wait(random(1200, 1450));
					moveMouse(random(240, 340), random(14, 29));
				} else {
					currentFails++;
				}
				lapBegun = true;
				break;
			case 2: // Net
				if (atTile3(new RSTile(2474 + random(0, -1), 3425),
						"Climb-over", 0, -10)) {
					wait(random(900, 1100));
				} else {
					currentFails++;
				}
				break;
			case 3: // Branch
				if (atTile3(new RSTile(2473, 3422), "Climb", 0, -10)) {
					wait(random(650, 800));
				} else {
					currentFails++;
				}
				break;
			case 4: // Rope
				if (!tileOnScreen(new RSTile(2478, 3420))) {
					turnToTile(new RSTile(2478, 3420));
				}
				int var = 3;
				if (currentFails > 10) {
					var = 15;
				}
				if (atTile3(new RSTile(2478, 3420), "Walk-on", 1, 6, var)) {
					wait(random(600, 850));
					moveMouse(random(85, 135), random(105, 150));
					int waited = 0;
					while (getState() != 5 && waited < 15) {
						wait(random(70, 100));
						waited++;
					}
				} else {
					currentFails++;
				}
				break;
			case 5: // Tree
				if (atTile3(new RSTile(2486, 3419), "Climb-down", -5, -2)) {
					wait(random(800, 1000));
				} else if (currentFails > 0 && currentFails % 10 == 0) {
					turnToTile(new RSTile(2486, 3419));
				} else {
					currentFails++;
				}
				break;
			case 6: // Net2
				final RSTile wtt = randomizeTile(new RSTile(2486, 3423), 2, 2);
				if (me.getLocation().getY() < 3423) {
					if (tileOnScreen(wtt)) {
						if (atTile(wtt, "Walk Here")) {
							wait(random(500, 900));
						}
					} else {
						walkTo(wtt);
					}
				}
				final RSTile net2Loc = getNearestObjectByID(2286).getLocation();
				if (!tileOnScreen(net2Loc)) {
					turnToTile(net2Loc, 40);
				}
				int yOff = -5;
				if (net2Loc.getY() == 3425) {
					yOff = -37;
				}
				if (atTile3(net2Loc, "Climb-over", 0, yOff)) {
					wait(random(1300, 1700));
				} else {
					currentFails++;
				}
				break;
			case 7: // Pipe
				final RSTile pipeLoc = getNearestObjectByID(43544, 43543)
						.getLocation();
				if (!tileOnScreen(pipeLoc)) {
					turnToTile(pipeLoc, 10);
				}
				if (atTile3(pipeLoc, "Squeeze-through", 0, -3)) {
					wait(random(1400, 2000));
				} else {
					currentFails++;
				}
				break;
			case 8:
				currentFails = 0;
				lapJustDone = true;
				walkTo(checkTile(new RSTile(2473, 3437)));
				wait(random(150, 350));
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
			g.drawString("GnomeCourse v1.0", x, y += 20);
			g.drawString("GnomeCourse v1.0", x, y);
			g.drawString("Runtime: "
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