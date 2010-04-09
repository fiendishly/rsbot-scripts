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
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = { "Garrett" }, category = "Smithing", name = "Garrett's Cannonball Smither", version = 1.02, description = "<html><head>"
		+ "</head><body>"
		+ "<center><strong><h2>Garrett's Cannonball Smither</h2></strong></center>"
		+ "<center><strong>Start the script at a supported location with mould<br />"
		+ "in inventory and steel bars visible in the bank</strong></center><br />"
		+ "<strong>Supported Locations:</strong><br />"
		+ " - Al-Kahrid<br />"
		+ " - Edgeville" + "</body></html>")
public class GarrettsCannonballSmither extends Script implements PaintListener {

	private enum State {
		walkTo, smelt, walkBank, bank, error;
	}

	final ScriptManifest properties = getClass().getAnnotation(
			ScriptManifest.class);
	// OTHER VARIABLES
	private long scriptStartTime = 0;
	private int runEnergy = random(40, 95);
	private int startXP = 0;
	private int startLvl = 0;
	private int stopCount = 0;
	private int failCount = 0;
	private boolean setAltitude = true;
	private int cannonballPrice = 0;
	private int steelbarPrice = 0;
	private final int anim1 = 899;
	private final int anim2 = 827;
	private final int cannonball = 2;
	private final int ammomould = 4;
	private final int steelbar = 2353;
	private final RSTile furnace[] = { new RSTile(3110, 3502),
			new RSTile(3273, 3186) };
	private final RSTile bankBooth[] = { new RSTile(3097, 3496),
			new RSTile(3269, 3169) };

	private final RSTile furnaceBldg[] = { new RSTile(3108, 3500),
			new RSTile(3275, 3184) };;

	private void antiBan() {
		final int random = random(1, 24);

		switch (random) {
		case 1:
			if (random(1, 3) == 1) {
				moveMouseRandomly(300);
			}
			return;

		case 2:
			if (random(1, 10) == 5) {
				if (getCurrentTab() != Constants.TAB_INVENTORY) {
					openTab(Constants.TAB_INVENTORY);
				}
			}
			return;

		case 3:
			if (random(1, 20) == 10) {
				int angle = getCameraAngle() + random(-90, 90);
				if (angle < 0) {
					angle = 0;
				}
				if (angle > 359) {
					angle = 0;
				}
				setCameraRotation(angle);
			}
			return;
		default:
			return;
		}
	}

	private RSTile closestTile(final RSTile tiles[]) {
		int dist = 999;
		RSTile closest = null;
		for (final RSTile tile : tiles) {
			try {
				final int distance = distanceTo(tile);
				if (distance < dist) {
					dist = distance;
					closest = tile;
				}
			} catch (final Exception e) {
			}
		}
		return closest;
	}

	// *******************************************************//
	// OTHER METHODS
	// *******************************************************//
	private void doBank() {
		int failCount = 0;
		try {
			if (!bank.isOpen()) {
				if (bank.open()) {
					wait(random(500, 750));
				}
			}
			while (!bank.isOpen()) {
				wait(50);
				failCount++;
				if (failCount > 30) {
					return;
				}
			}
			if (bank.isOpen()) {
				wait(random(500, 750));
				bank.depositAllExcept(ammomould, steelbar);
				if (bank.withdraw(steelbar, 0)) {
					wait(random(500, 750));
				}
			}
		} catch (final Exception e) {
		}
	}

	private State getState() {
		if (!inventoryContains(ammomould)) {
			log("You do not have an Ammo Mould in your inventory.");
			return State.error;
		}
		if (inventoryContains(steelbar)) {
			if (tileOnScreen(closestTile(furnace))) {
				return State.smelt;
			} else {
				return State.walkTo;
			}
		} else {
			if (tileOnScreen(closestTile(bankBooth))) {
				return State.bank;
			} else {
				return State.walkBank;
			}
		}
	}

	// *******************************************************//
	// MAIN LOOP
	// *******************************************************//
	@Override
	public int loop() {
		if (!isLoggedIn()) {
			return 50;
		}

		if (startLvl == 0) {
			startXP = skills
					.getCurrentSkillExp(Skills.getStatIndex("smithing"));
			startLvl = skills.getCurrentSkillLevel(Skills
					.getStatIndex("smithing"));
		}

		if (setAltitude) {
			setCameraAltitude(true);
			wait(random(250, 500));
			setAltitude = false;
		}

		startRunning(runEnergy);

		switch (getState()) {
		case walkTo:
			walkTile(closestTile(furnaceBldg));
			return 50;
		case smelt:
			makeCannonball();
			return 50;
		case walkBank:
			walkTile(closestTile(bankBooth));
			return 50;
		case bank:
			doBank();
			return 50;
		case error:
			return -1;
		}

		return 50;
	}

	private void makeCannonball() {
		if (getInventoryCount(steelbar) > 0) {
			if (!getInterface(513).getChild(15).getText().contains("Steel bar")) {
				if (!atInventoryItem(steelbar, "Use")) {
					return;
				}
				if (onTile(closestTile(furnace), "Furnace", "Use", 0.75, 0.5, 0)) {
					failCount = 0;
					while (!getInterface(513).getChild(15).getText().contains(
							"Steel bar")) {
						wait(50);
						failCount++;
						if (failCount > 40) {
							return;
						}
					}
					failCount = 0;
				}
				wait(random(500, 750));
			} else {
				try {
					final int x1 = getInterface(513).getChild(3).getArea().x + 5;
					final int y1 = getInterface(513).getChild(3).getArea().y + 5;
					final int h1 = getInterface(513).getChild(3).getArea().height - 5;
					final int w1 = getInterface(513).getChild(3).getArea().width - 5;
					clickMouse(random(x1, (x1 + w1)), random(y1, (y1 + h1)),
							false);
					wait(random(200, 400));
				} catch (final Exception e) {
				}
				if (atMenu("Make All")) {
					wait(random(800, 1600));
					stopCount = 0;
					while (stopCount <= 10) {
						wait(100);
						antiBan();
						if (getMyPlayer().getAnimation() == -1) {
							stopCount++;
						}
						if (getMyPlayer().getAnimation() == anim1
								|| getMyPlayer().getAnimation() == anim2) {
							stopCount = 0;
						}
						if (!inventoryContains(steelbar)) {
							break;
						}
					}
					stopCount = 0;
				}
			}
		}
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
		int cannonballs = 0;
		int currentXP = 0;
		int currentLVL = 0;
		int gainedXP = 0;
		int ballsPerHour = 0;
		int profit = 0;
		final double xpGain = 25.5;

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

		currentLVL = skills.getCurrentSkillLevel(Skills
				.getStatIndex("smithing"));
		currentXP = skills.getCurrentSkillExp(Skills.getStatIndex("smithing"));
		gainedXP = currentXP - startXP;
		cannonballs = (int) (gainedXP / xpGain * 4);
		ballsPerHour = (int) (3600000.0 / runTime * cannonballs);
		profit = (cannonballPrice * 4 - steelbarPrice) * (cannonballs / 4);

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
			g.drawString("Cannonball: " + cannonballs, 561, coords[4]);
			g.drawString("Cannonball/Hour: " + ballsPerHour, 561, coords[5]);
			g.drawString("Total Profit: " + profit, 561, coords[6]);
			g.drawString("Current Lvl: " + currentLVL, 561, coords[8]);
			g.drawString("Lvls Gained: " + (currentLVL - startLvl), 561,
					coords[9]);
			g.drawString("XP Gained: " + gainedXP, 561, coords[10]);
			g.drawString("XP To Next Level: "
					+ skills.getXPToNextLevel(Skills.getStatIndex("smithing")),
					561, coords[11]);
			g.drawString("% To Next Level: "
					+ skills.getPercentToNextLevel(Skills
							.getStatIndex("smithing")), 561, coords[12]);
		}
	}

	// *******************************************************//
	// ON START
	// *******************************************************//
	@Override
	public boolean onStart(final Map<String, String> args) {
		scriptStartTime = System.currentTimeMillis();

		cannonballPrice = grandExchange.loadItemInfo(cannonball)
				.getMarketPrice();
		steelbarPrice = grandExchange.loadItemInfo(steelbar).getMarketPrice();
		log("Cannon Ball Value: " + cannonballPrice);
		log("Steel Bar Value: " + steelbarPrice);

		return true;
	}

	public boolean onTile(final RSTile tile, final String search,
			final String action, final double dx, final double dy,
			final int height) {
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

	private void startRunning(final int energy) {
		if (getEnergy() >= energy && !isRunning()) {
			runEnergy = random(40, 95);
			setRun(true);
			wait(random(500, 750));
		}
	}

	private void walkTile(final RSTile tile) {
		if (!(distanceTo(getDestination()) <= random(4, 7))) {
			if (getMyPlayer().isMoving()) {
				return;
			}
		}
		final Point screen = Calculations.tileToScreen(tile);
		if (pointOnScreen(screen)) {
			if (getMyPlayer().isMoving()) {
				return;
			}
			moveMouse(screen, random(-3, 4), random(-3, 4));
			onTile(tile, "here", "alk");
			wait(random(500, 750));
			return;
		} else {
			walkTo(tile);
			wait(random(500, 750));
			return;
		}
	}
}