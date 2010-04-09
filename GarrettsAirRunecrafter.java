import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Map;

import org.rsbot.bot.Bot;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.Skills;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = { "Garrett" }, category = "Runecraft", name = "Garrett's Air Runecrafter", version = 1.05, description = "<html><head>"
		+ "</head><body>"
		+ "<center><strong><h2>Garrett's Air Runecrafter</h2></strong></center>"
		+ "<center>Start the script in Varrock West Bank.</center>"
		+ "<br /><center>You need to have air tiara equipped and rune essence visible.</center>"
		+ "</body></html>")
public class GarrettsAirRunecrafter extends Script implements PaintListener {

	final ScriptManifest properties = getClass().getAnnotation(
			ScriptManifest.class);

	// OTHER VARIABLES
	private long scriptStartTime;
	private int startLvl;
	private int startXP;
	private int failCount = 0;
	private int totalAirs = 0;
	private int runEnergy = random(50, 80);
	private boolean gotSkillLvl = false;
	private boolean tiaraCheck = false;

	// ITEM ID
	private final int runeEssence = 1436;
	private final int airRune = 556;
	private final int airTiara = 5527;

	// PATHS
	private final RSTile airPath[] = { new RSTile(3182, 3436),
			new RSTile(3182, 3429), new RSTile(3174, 3427),
			new RSTile(3168, 3428), new RSTile(3164, 3424),
			new RSTile(3160, 3420), new RSTile(3154, 3416),
			new RSTile(3147, 3416), new RSTile(3141, 3413),
			new RSTile(3135, 3408), new RSTile(3129, 3405) };

	// TILES
	private final RSTile airRuins[] = { new RSTile(3128, 3405),
			new RSTile(3127, 3405), new RSTile(3126, 3405),
			new RSTile(3127, 3406), new RSTile(3127, 3404) };
	private final RSTile airAltar[] = { new RSTile(2845, 4834),
			new RSTile(2844, 4834), new RSTile(2843, 4834),
			new RSTile(2844, 4835), new RSTile(2844, 4833) };
	private final RSTile airPortal = new RSTile(2841, 4828);

	private final RSTile fallyBank[] = { new RSTile(3181, 3436),
			new RSTile(3181, 3438) };

	// ENUM
	private final int WALKTOALTAR = 1;
	private final int GETINTOALTAR = 2;
	private final int CRAFTRUNES = 3;
	private final int LEAVERUINS = 4;
	private final int WALKTOBANK = 5;
	private final int OPENBANK = 6;
	private final int BANK = 7;
	private int ACTION = WALKTOBANK;

	private int antiBan() {
		final int random = random(1, 24);

		switch (random) {
		case 1:
			if (random(1, 4) == 2) {
				moveMouseRandomly(300);
			}
			return random(100, 500);

		case 2:
			if (getCurrentTab() != Constants.TAB_INVENTORY) {
				openTab(Constants.TAB_INVENTORY);
			}
			return random(100, 500);

		case 3:
			if (random(1, 40) == 30) {
				if (getMyPlayer().isMoving()) {
					return random(750, 1000);
				}
				if (getCurrentTab() != Constants.TAB_STATS) {
					openTab(Constants.TAB_STATS);
				}
				moveMouse(560, 420, 40, 20);
				wait(random(3000, 6000));
				return random(100, 200);
			}

		case 4:
			if (random(1, 30) == 5) {
				int angle = getCameraAngle() + random(-90, 90);
				if (angle < 0) {
					angle = 0;
				}
				if (angle > 359) {
					angle = 0;
				}

				setCameraRotation(angle);
				return random(500, 750);
			}
		}
		return 500;
	}

	// *******************************************************//
	// OTHER METHODS
	// *******************************************************//
	private void doAirCrafting(final boolean abyssal) {
		final int randomTile = random(0, 5);
		if (abyssal) {

		} else {
			if (ACTION == WALKTOALTAR) {
				if (distanceTo(airRuins[randomTile]) < 15) {
					ACTION = GETINTOALTAR;
					return;
				}
				if (playerInArea(2845, 4845, 2835, 4820)) {
					ACTION = CRAFTRUNES;
					return;
				}
				walkPath(airPath, false);
				return;
			}
			if (ACTION == GETINTOALTAR) {
				if (playerInArea(2845, 4845, 2835, 4820)) {
					ACTION = CRAFTRUNES;
					return;
				}
				if (distanceTo(airRuins[randomTile]) <= 7) {
					if (!getMyPlayer().isMoving()) {
						gTile(airRuins[randomTile], "Mysterious", "Enter");
						wait(random(400, 600));
						return;
					} else {
						antiBan();
					}
				} else {
					if (!getMyPlayer().isMoving()) {
						walkTo(airRuins[randomTile]);
						wait(random(150, 300));
						return;
					} else {
						antiBan();
					}
				}
				return;
			}
			if (ACTION == CRAFTRUNES) {
				if (getInventoryCount(runeEssence) == 0) {
					ACTION = LEAVERUINS;
					return;
				}
				final Point location = Calculations
						.tileToScreen(airAltar[randomTile]);
				if (pointOnScreen(location)) {
					if (!getMyPlayer().isMoving()) {
						final int count = getInventoryCount(airRune);
						wait(100);
						if (gTile(airAltar[randomTile], "Altar", "Craft-rune")) {
							wait(random(400, 600));
						}
						wait(random(400, 600));
						if (count != getInventoryCount(airRune)) {
							totalAirs += getInventoryCount(airRune) - count;
						}
						return;
					} else {
						antiBan();
					}
				} else {
					if (!getMyPlayer().isMoving()) {
						walkTo(airAltar[randomTile]);
						return;
					} else {
						antiBan();
					}
				}
				return;
			}
			if (ACTION == LEAVERUINS) {
				if (playerInArea(3135, 3415, 3115, 3395)) {
					ACTION = WALKTOBANK;
					return;
				}
				final Point location = Calculations.tileToScreen(airPortal);
				if (pointOnScreen(location)) {
					if (!getMyPlayer().isMoving()) {
						gTile(airPortal, "Portal", "Enter");
						wait(random(400, 600));
						return;
					} else {
						antiBan();
					}
				} else {
					if (!getMyPlayer().isMoving()) {
						walkTo(airPortal);
						return;
					} else {
						antiBan();
					}
				}
				return;
			}
			if (ACTION == WALKTOBANK) {
				if (getInventoryCount(runeEssence) > 0) {
					ACTION = WALKTOALTAR;
					failCount = 0;
					return;
				}
				final Point location = Calculations.tileToScreen(fallyBank[0]);
				if (pointOnScreen(location)) {
					ACTION = OPENBANK;
					return;
				} else {
					if (distanceTo(fallyBank[0]) < 15) {
						if (!getMyPlayer().isMoving()
								|| distanceTo(getDestination()) <= random(2, 5)) {
							walkTo(fallyBank[0]);
						}
					}
				}
				walkPath(airPath, true);
				return;
			}
			if (ACTION == OPENBANK) {
				if (getInventoryCount(runeEssence) > 0
						&& getInventoryCount(airRune) <= 0) {
					ACTION = WALKTOALTAR;
					failCount = 0;
					return;
				}
				if (bank.isOpen()) {
					ACTION = BANK;
					return;
				}
				openBank(fallyBank);
				wait(random(500, 750));
			}
			if (ACTION == BANK) {
				if (getInventoryCount(runeEssence) > 0
						&& getInventoryCount(airRune) <= 0) {
					bank.close();
					wait(random(150, 300));
					ACTION = WALKTOALTAR;
					failCount = 0;
					return;
				}
				if (bank.isOpen()) {
					if (getInventoryCount() != 0) {
						bank.depositAll();
					}
					wait(random(750, 1000));
					if (bank.atItem(runeEssence, "Withdraw-All")) {
						wait(random(750, 1000));
						failCount = 0;
						return;
					} else {
						failCount++;
						if (failCount >= 5) {
							stopScript();
						} else {
							return;
						}
					}
					return;
				} else {
					ACTION = OPENBANK;
				}
				return;
			}
		}
	}

	private boolean energyCheck() {
		try {
			if (gEnergy() >= runEnergy && !isRunning()) {
				runEnergy = random(35, 65);
				return true;
			} else {
				return false;
			}
		} catch (final Exception e) {
			return false;
		}
	}

	private int gEnergy() {
		return Integer
				.parseInt(RSInterface.getChildInterface(750, 5).getText());
	}

	private boolean gTile(final RSTile tile, final String search,
			final String action) {
		if (!tile.isValid()) {
			return false;
		}

		Point checkScreen = null;
		checkScreen = Calculations.tileToScreen(tile);
		if (!pointOnScreen(checkScreen)) {
			walkTo(tile);
			wait(random(340, 750));
		}

		try {
			Point screenLoc = null;
			for (int i = 0; i < 30; i++) {
				screenLoc = Calculations.tileToScreen(tile);
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
			screenLoc = Calculations.tileToScreen(tile);
			if (getMenuItems().size() <= 1) {
				return false;
			}
			if (getMenuItems().get(0).toLowerCase().contains(
					action.toLowerCase())) {
				clickMouse(true);
				return true;
			} else {
				clickMouse(false);
				return atMenu(action);
			}
		} catch (final Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// *******************************************************//
	// MAIN LOOP
	// *******************************************************//
	@Override
	public int loop() {

		try {
			if (isLoggedIn() && !gotSkillLvl) {
				startLvl = skills.getCurrentSkillLevel(Skills
						.getStatIndex("runecrafting"));
				startXP = skills.getCurrentSkillExp(Skills
						.getStatIndex("runecrafting"));
				gotSkillLvl = true;
				return random(50, 150);
			}
		} catch (final Exception e) {
		}

		try {
			if (isLoggedIn() && !tiaraCheck) {
				if (equipmentContainsOneOf(airTiara)) {
					tiaraCheck = true;
					return random(50, 150);
				} else {
					log("You need to have an air tiara equipped!");
					wait(10000);
					return random(50, 150);
				}
			}
		} catch (final Exception e) {
		}

		try {
			if (energyCheck()) {
				setRun(true);
				wait(random(750, 1000));
				return random(50, 150);
			}
		} catch (final Exception e) {
		}

		try {
			doAirCrafting(false);
		} catch (final Exception e) {
		}

		return random(50, 150);
	}

	// *******************************************************//
	// ON FINISH
	// *******************************************************//
	@Override
	public void onFinish() {
		Bot.getEventManager().removeListener(PaintListener.class, this);
	}

	// *******************************************************//
	// PAINT SCREEN
	// *******************************************************//
	public void onRepaint(final Graphics g) {

		long runTime = 0;
		long seconds = 0;
		long minutes = 0;
		long hours = 0;
		int airsPerHour = 0;
		runTime = System.currentTimeMillis() - scriptStartTime;
		seconds = runTime / 1000;
		if (seconds >= 60) {
			minutes = seconds / 60;
			seconds -= minutes * 60;
		}
		if (minutes >= 60) {
			hours = minutes / 60;
			minutes -= hours * 60;
		}

		final int currentXP = skills.getCurrentSkillExp(Skills
				.getStatIndex("runecrafting"));
		final int XPgained = currentXP - startXP;
		if (runTime / 1000 > 0) {
			airsPerHour = (int) (3600000.0 / runTime * (XPgained / 5));
		}

		if (getCurrentTab() == Constants.TAB_INVENTORY) {
			g.setColor(new Color(0, 0, 0, 175));
			g.fillRoundRect(555, 210, 175, 250, 10, 10);
			g.setColor(Color.WHITE);

			final int[] coords = new int[] { 225, 240, 255, 270, 285, 300, 315,
					330, 345, 360, 375, 390, 405, 420, 435, 450 };

			g.drawString(properties.name(), 561, coords[0]);
			g.drawString("Version: " + properties.version(), 561, coords[1]);
			g.drawString("Run Time: " + hours + ":" + minutes + ":" + seconds,
					561, coords[2]);
			g.drawString("Essence Used: " + XPgained / 5, 561, coords[3]);
			g.drawString("Current Lvl: "
					+ skills.getCurrentSkillLevel(Skills
							.getStatIndex("runecrafting")), 561, coords[4]);
			g.drawString("Lvls Gained: "
					+ (skills.getCurrentSkillLevel(Skills
							.getStatIndex("runecrafting")) - startLvl), 561,
					coords[5]);
			g.drawString("XP Gained: " + XPgained, 561, coords[6]);
			g.drawString("EssUsed/Hour: " + airsPerHour, 561, coords[7]);
		}
	}

	// *******************************************************//
	// ON START
	// *******************************************************//
	@Override
	public boolean onStart(final Map<String, String> args) {
		scriptStartTime = System.currentTimeMillis();
		return true;
	}

	private void openBank(final RSTile[] loc) {
		int randomTile = 1;
		if (distanceTo(loc[0]) < distanceTo(loc[1])) {
			randomTile = 0;
		}
		final Point location = Calculations.tileToScreen(loc[randomTile]);
		if (pointOnScreen(location)) {
			if (!getMyPlayer().isMoving()) {
				gTile(loc[randomTile], "Bank booth", "Use-quickly");
				wait(random(400, 600));
			} else {
				antiBan();
			}
		} else {
			if (!getMyPlayer().isMoving()) {
				walkTo(loc[randomTile]);
			} else {
				antiBan();
			}
		}
	}

	private boolean playerInArea(final int maxX, final int maxY,
			final int minX, final int minY) {
		final int x = getMyPlayer().getLocation().getX();
		final int y = getMyPlayer().getLocation().getY();
		if (x >= minX && x <= maxX && y >= minY && y <= maxY) {
			return true;
		}
		return false;
	}

	private void walkPath(final RSTile[] path, final boolean reverse) {
		if (!reverse) {
			if (!getMyPlayer().isMoving()
					|| distanceTo(getDestination()) <= random(4, 7)) {
				walkPathMM(randomizePath(path, 2, 2), 17);
			} else {
				antiBan();
			}
		} else {
			if (!getMyPlayer().isMoving()
					|| distanceTo(getDestination()) <= random(4, 7)) {
				walkPathMM(randomizePath(reversePath(path), 2, 2), 17);
			} else {
				antiBan();
			}
		}
	}
}