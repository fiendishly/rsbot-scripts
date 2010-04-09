import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Map;

import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Constants;
import org.rsbot.script.GEItemInfo;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = { "Jacmob" }, category = "Crafting", name = "AutoSpinner", version = 1.31, description = "<html><head><style type=\"text/css\">body { font-family: Arial; padding: 10px; }</style></head><body><strong>AutoSpinner V1.31 | Jacmob</strong><br /><br />Start the script in Lumbridge Castle with flax visible at the top of your bank.</body></html>")
public class AutoSpinner extends Script implements PaintListener {

	private enum State {
		WALKTOBANK, WALKTOSPIN, OPENBANK, OPENSPIN, CLIMBUP, BANK, SPIN
	}

	public final int EMOTE_ID = 1563;
	public final int FLAX_ID = 1779;
	public final int BOW_STRING_ID = 1777;
	public final int[] BANK_AREA = { 3207, 3210, 3217, 3220 };
	public final RSTile BANK_TILE = new RSTile(3208, 3221);
	public final RSTile SPINNER_TILE = new RSTile(3209, 3212);
	public final RSTile BANK_WALK_TILE = new RSTile(3208, 3219);
	public final RSTile STAIRCASE_TILE = new RSTile(3205, 3208);
	public final RSTile STAIRCASE_GROUND_TILE = new RSTile(3204, 3208);
	public final RSTile STAIRCASE_GROUND_WALK_TILE = new RSTile(3207, 3210);
	public final RSInterfaceChild SPIN_INTERFACE = RSInterface
			.getChildInterface(459, 16);

	private int scriptStartXP = 0;
	private int nextMinRunEnergy = random(20, 50);
	private int flaxSpun = 0;
	private int flaxPrice = 0;
	private int stringPrice = 0;
	private long scriptStartTime = 0;

	private void antiBan(final int upperBound) {
		final int rand = random(0, upperBound);
		if (rand == 69) {
			if (getCurrentTab() == Constants.TAB_STATS) {
				openTab(Constants.TAB_INVENTORY);
				wait(random(50, 1000));
			}
			final Point screenLoc = Calculations.tileToScreen(getMyPlayer()
					.getLocation());
			moveMouse(screenLoc, 3, 3, 5);
			wait(random(50, 300));
			clickMouse(false);
			wait(random(500, 2500));
			while (isMenuOpen()) {
				moveMouseRandomly(700);
				wait(random(100, 500));
			}
		} else if (rand == 68) {
			if (getCurrentTab() != Constants.TAB_STATS) {
				openTab(Constants.TAB_STATS);
				wait(random(200, 400));
				if (random(0, 2) == 1) {
					moveMouse(random(575, 695), random(240, 435), 10);
				}
				moveMouse(632, 372, 7, 7);
				wait(random(800, 1400));
			} else if (getCurrentTab() == Constants.TAB_STATS) {
				openTab(Constants.TAB_INVENTORY);
				wait(random(800, 1200));
			}
		} else if (rand == 67) {
			final int rand2 = random(1, 3);
			for (int i = 0; i < rand2; i++) {
				moveMouse(random(100, 700), random(100, 500));
				wait(random(200, 700));
			}
			moveMouse(random(0, 800), 647, 50, 100);
			wait(random(100, 1500));
			moveMouse(random(75, 400), random(75, 400), 30);
		} else if (rand == 0) {
			rotateCamera();
		} else if (rand < 4) {
			waveMouse();
		}
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
		if (minutes < 10 && !type.equals("seconds")) {
			minutesString = "0" + minutesString;
		}
		if (hours < 10 && type.equals("hours")) {
			hoursString = "0" + hoursString;
		}
		if (seconds < 10 && !type.equals("seconds")) {
			secondsString = "0" + secondsString;
		}

		return hoursString + minutesString + secondsString + " " + type;
	}

	private State getState() {
		if (getPlane() == 0) {
			return State.CLIMBUP;
		} else if (inventoryContains(FLAX_ID)) {
			if (getPlane() == 2) {
				return State.WALKTOSPIN;
			} else if (SPIN_INTERFACE.isValid()) {
				return State.SPIN;
			} else {
				return State.OPENSPIN;
			}
		} else {
			if (getPlane() == 1) {
				return State.WALKTOBANK;
			} else if (bank.isOpen()) {
				return State.BANK;
			} else {
				return State.OPENBANK;
			}
		}
	}

	@Override
	public int loop() {
		final State state = getState();
		int tries = 0;
		antiBan(65);
		switch (state) {
		case WALKTOSPIN:
			if (getEnergy() > nextMinRunEnergy) {
				setRun(true);
				nextMinRunEnergy = random(20, 50);
			}
			if (tileOnScreen(STAIRCASE_TILE)) {
				if (bank.isOpen()) {
					bank.close();
				}
				if (!atTile(STAIRCASE_TILE, "Climb-down")
						&& !getMyPlayer().isMoving()) {
					rotateCamera();
				}
				while (getPlane() != 1 && tries < 10) {
					tries++;
					wait(random(100, 200));
				}
			} else {
				walkTileSmart(STAIRCASE_TILE);
				while (distanceTo(STAIRCASE_TILE) > 4 && tries < 10) {
					tries++;
					wait(random(400, 500));
				}
			}
			break;
		case WALKTOBANK:
			if (SPIN_INTERFACE.isValid()) {
				clickMouse(random(480, 485), random(42, 48), true);
			}
			if (tileOnScreen(STAIRCASE_TILE)) {
				if (!atTile(STAIRCASE_TILE, "Climb-up")
						&& !getMyPlayer().isMoving()) {
					rotateCamera();
					break;
				} else {
					if (random(0, 5) != 1) {
						moveMouse(random(608, 640), random(50, 90), 5);
						if (random(0, 5) != 1) {
							moveMouseAway(5);
						}
					}
				}
				while (getPlane() != 2 && tries < 15) {
					tries++;
					wait(random(200, 400));
				}
			} else {
				walkTileSmart(STAIRCASE_TILE);
				while (distanceTo(STAIRCASE_TILE) > 4 && tries < 10) {
					tries++;
					wait(random(400, 500));
				}
			}
			break;
		case OPENBANK:
			if (playerIsInArea(BANK_AREA)) {
				atTile(BANK_TILE, "Use-quickly");
				while (!bank.isOpen() && tries < 5) {
					tries++;
					wait(random(400, 600));
				}
			} else {
				walkTileSmart(BANK_WALK_TILE);
				wait(random(200, 700));
				moveMouse(getMouseSpeed() + random(-1, 2), 200, 200, 80, 80, 10);
				while (!playerIsInArea(BANK_AREA) && tries < 10) {
					tries++;
					wait(random(400, 600));
				}
			}
			break;
		case OPENSPIN:
			if (tileOnScreen(SPINNER_TILE)) {
				if (atTile(SPINNER_TILE, "Spin")) {
					moveMouseAway(50);
				} else {
					break;
				}
				while (!SPIN_INTERFACE.isValid() && tries < 10) {
					if (getMyPlayer().isMoving()) {
						tries = 2;
					}
					tries++;
					wait(random(400, 600));
					antiBan(65);
				}
			} else {
				turnToTile(SPINNER_TILE, 20);
			}
			break;
		case SPIN:
			int stringsPreviouslyHeld = getInventoryCount(BOW_STRING_ID);
			if (atInterface(SPIN_INTERFACE, "Make All")) {
				if (random(0, 2) == 1) {
					moveMouseAway(50);
				}
				wait(random(1000, 1200));
				if (SPIN_INTERFACE.isValid()) {
					break;
				}
			} else {
				break;
			}
			while (tries < 15 && inventoryContains(FLAX_ID)) {
				if (getMyPlayer().getAnimation() == EMOTE_ID) {
					tries = 0;
				} else {
					tries++;
				}
				wait(random(300, 500));
				antiBan(80);
				flaxSpun += getInventoryCount(BOW_STRING_ID)
						- stringsPreviouslyHeld;
				stringsPreviouslyHeld = getInventoryCount(BOW_STRING_ID);
			}
			break;
		case BANK:
			while (getInventoryCount() > 0) {
				bank.depositAll();
				wait(random(290, 520));
			}
			while (getInventoryCount(FLAX_ID) == 0) {
				bank.atItem(FLAX_ID, "Withdraw-All");
				wait(random(800, 1200));
				if (getInventoryCount(FLAX_ID) == 0
						&& bank.getItemByID(FLAX_ID) == null) {
					while (bank.isOpen()) {
						bank.close();
						wait(random(200, 500));
					}
					log.info("No Flax Found");
					stopScript();
					break;
				}
			}
			wait(random(150, 400));
			if (random(0, 3) == 0) {
				bank.close();
			}
		case CLIMBUP:
			if (tileOnScreen(STAIRCASE_TILE)) {
				if (!atTile(STAIRCASE_GROUND_TILE, "Climb-up")
						&& !getMyPlayer().isMoving()) {
					rotateCamera();
				}
				wait(random(400, 600));
			} else {
				walkTo(getClosestTileOnMap(STAIRCASE_GROUND_WALK_TILE), 1, 1);
				wait(random(1000, 2000));
			}
			break;
		default:
			break;
		}
		return random(400, 700);
	}

	private void moveMouseAway(final int moveDist) {
		final Point pos = getMouseLocation();
		moveMouse(pos.x - moveDist, pos.y - moveDist, moveDist * 2,
				moveDist * 2);
	}

	@Override
	public int getMouseSpeed() {
		return random(5, 8);
	}

	@Override
	public void onFinish() {
		log
				.info(flaxSpun
						+ " flax spun in "
						+ getFormattedTime(System.currentTimeMillis()
								- scriptStartTime) + ".");
	}

	public void onRepaint(final Graphics g) {
		if (isLoggedIn()
				&& skills.getCurrentSkillLevel(Constants.STAT_CRAFTING) > 1) {
			if (scriptStartTime == 0) {
				scriptStartTime = System.currentTimeMillis();
				scriptStartXP = skills
						.getCurrentSkillExp(Constants.STAT_CRAFTING);
			}

			final Color BG = new Color(50, 50, 50, 150);
			final Color TEXT = new Color(200, 255, 0, 255);

			final int x = 13;
			int y = 26;

			final int levelsGained = skills
					.getRealSkillLevel(Constants.STAT_CRAFTING)
					- skills.getLvlByExp(scriptStartXP);
			final long runSeconds = (System.currentTimeMillis() - scriptStartTime) / 1000;

			g.setColor(BG);
			g.fill3DRect(x - 6, y, 211, 26, true);

			g.setColor(TEXT);
			g.drawString("AutoSpinner v1.2", x, y += 17);

			y += 20;
			g.setColor(BG);
			g.fill3DRect(x - 6, y, 211, 86, true);

			y -= 3;
			g.setColor(TEXT);
			g.drawString("Runtime: "
					+ getFormattedTime(System.currentTimeMillis()
							- scriptStartTime), x, y += 20);
			g.drawString("Spun: " + flaxSpun + " Flax", x, y += 20);

			if (levelsGained < 0) {
				scriptStartXP = skills
						.getCurrentSkillExp(Constants.STAT_CRAFTING);
			} else if (levelsGained == 1) {
				g
						.drawString(
								"Gained: "
										+ (skills
												.getCurrentSkillExp(Constants.STAT_CRAFTING) - scriptStartXP)
										+ " XP (" + levelsGained + " lvl)", x,
								y += 20);
			} else {
				g
						.drawString(
								"Gained: "
										+ (skills
												.getCurrentSkillExp(Constants.STAT_CRAFTING) - scriptStartXP)
										+ " XP (" + levelsGained + " lvls)", x,
								y += 20);
			}

			if (runSeconds > 10 && flaxSpun > 0) {
				g
						.drawString(
								"Averaging: "
										+ (skills
												.getCurrentSkillExp(Constants.STAT_CRAFTING) - scriptStartXP)
										* 3600 / runSeconds + " XP/hr", x,
								y += 20);
				if (flaxPrice != 0 && stringPrice != 0) {
					y += 20;
					g.setColor(BG);
					g.fill3DRect(x - 6, y, 211, 66, true);
					y -= 3;
					g.setColor(TEXT);
					final int profit = flaxSpun * (stringPrice - flaxPrice);
					g.drawString("Gained: " + profit + " GP", x, y += 20);
					g.drawString("Averaging: " + flaxSpun * 3600 / runSeconds
							+ " spins/hr", x, y += 20);
					g.drawString("Averaging: " + profit * 3600 / runSeconds
							+ " GP/hr", x, y += 20);
				}
			} else {
				g.drawString("Gathering Data...", x, y += 20);
			}
		}
	}

	@Override
	public boolean onStart(final Map<String, String> args) {
		final GEItemInfo stringGE = grandExchange.loadItemInfo(BOW_STRING_ID);
		stringPrice = stringGE.getMarketPrice();
		log
				.info("Each bow string will be valued at the current GE market price of "
						+ stringPrice + " coins.");
		final GEItemInfo flaxGE = grandExchange.loadItemInfo(FLAX_ID);
		flaxPrice = flaxGE.getMarketPrice();
		log
				.info("Each piece of flax will be valued at the current GE market price of "
						+ flaxPrice + " coins.");
		if (flaxPrice == 0 || stringPrice == 0) {
			log
					.info("Grand Exchange prices could not be loaded - some features of the paint will be disabled.");
		}
		return true;
	}

	private boolean playerIsInArea(final int[] bounds) {
		final RSTile pos = getMyPlayer().getLocation();
		return pos.getX() >= bounds[0] && pos.getX() <= bounds[1]
				&& pos.getY() >= bounds[2] && pos.getY() <= bounds[3];
	}

	private void rotateCamera() {
		int angle = getCameraAngle() + random(-40, 40);
		if (angle < 0) {
			angle += 359;
		}
		if (angle > 359) {
			angle -= 359;
		}

		setCameraRotation(angle);
	}

	private boolean walkTileSmart(final RSTile t) {
		if (tileOnScreen(t)) {
			return atTile(t, "Walk");
		}
		return walkTo(t);
	}

	private void waveMouse() {
		final Point curPos = getMouseLocation();
		moveMouse(random(0, 750), random(0, 500), 20);
		wait(random(100, 300));
		moveMouse(curPos, 20, 20);
	}
}