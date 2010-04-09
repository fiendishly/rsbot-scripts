import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import java.util.Map;

import org.rsbot.bot.Bot;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Methods;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.Skills;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = { "Garrett" }, category = "Mining", name = "Garrett's Essence Miner", version = 1.21, description = "<html><head>"
		+ "</head><body>"
		+ "<center><strong><h2>Garrett's Essence Miner</h2></strong></center>"
		+ "<center>Start the script at Yanille or Varrock</center>"
		+ "<center>Have your pickaxe equipped or in the inventory</center>"
		+ "<center>Please sell essence at Mid to Max to keep the prices up</center>"
		+ "<br />Adjust Mouse Speed: <input name='mouseSpeed' type='text' size='2' maxlength='2' value='0' /><br />(The higher the number, the slower the mouse)"
		+ "<br /><input name='disablepaint' type='checkbox' value='1'>Disable Paint"
		+ "<br /><input name='disablemap' type='checkbox' checked='checked' value='1'>Disable Map Paint"
		+ "</body></html>")
public class GarrettsEssenceMiner extends Script implements PaintListener {

	final ScriptManifest properties = getClass().getAnnotation(
			ScriptManifest.class);
	final TheWalker theWalker = new TheWalker();
	final GarrettsPaint thePainter = new GarrettsPaint();

	int runEnergy = random(40, 95);
	int miningCount = -1;
	int useX = 0;
	int useY = 0;
	int failCount = 0;
	int sAdj = 0;
	int value = 30;
	boolean checkPickaxe = false;
	boolean setAltitude = false;
	boolean foundType = false;
	boolean mapPaint = false;
	boolean paint = true;

	final int pickaxe[] = { 1265, 1267, 1269, 1296, 1273, 1271, 1275, 15259 };

	final int portal = 2492;

	final int Aubury = 553;
	final int Distentor = 462;

	final int bankBooth[] = { 11402, 2213 };

	final int essenceArea[] = { 2950, 4870, 2870, 4790 };
	final int varrockUpStairsArea[] = { 3257, 3423, 3250, 3416 };

	final int varrockBankArea[] = { 3257, 3423, 3250, 3420 };
	final RSTile varrockBank = new RSTile(3254, 3420);
	final RSTile varrockPath[] = { new RSTile(3253, 3421),
			new RSTile(3254, 3427), new RSTile(3262, 3421),
			new RSTile(3262, 3415), new RSTile(3259, 3411),
			new RSTile(3257, 3404), new RSTile(3254, 3398),
			new RSTile(3253, 3401) };
	final RSTile varrockDoor = new RSTile(3253, 3399);
	final RSTile varrockDoorCheck = new RSTile(3253, 3398);

	final int yanilleBankArea[] = { 2613, 3097, 2609, 3088 };
	final RSTile yanilleBank = new RSTile(2612, 3093);
	final RSTile yanillePath[] = { new RSTile(2611, 3093),
			new RSTile(2604, 3089), new RSTile(2597, 3088) };
	final RSTile yanilleDoor = new RSTile(2597, 3088);

	final RSTile[] miningTiles = { new RSTile(2927, 4818),
			new RSTile(2931, 4818), new RSTile(2931, 4814),
			new RSTile(2927, 4814), new RSTile(2897, 4816),
			new RSTile(2897, 4812), new RSTile(2893, 4812),
			new RSTile(2893, 4816), new RSTile(2895, 4847),
			new RSTile(2891, 4847), new RSTile(2891, 4851),
			new RSTile(2895, 4851), new RSTile(2925, 4848),
			new RSTile(2925, 4852), new RSTile(2929, 4852),
			new RSTile(2929, 4848) };
	final int[] tilesX = { 1, 0, 0, 1, 0, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 0 };
	final int[] tilesY = { 0, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 0, 1, 0, 0, 1 };

	final int mageGuildX[] = new int[] { 2590, 2593, 2597, 2597, 2597, 2593,
			2586, 2585, 2585, 2586, 2588 };
	final int mageGuildY[] = new int[] { 3094, 3094, 3090, 3088, 3085, 3081,
			3082, 3087, 3088, 3090, 3092 };
	final Polygon mageGuild = new Polygon(mageGuildX, mageGuildY, 11);

	public enum State {
		VARwalkTeleport, VARwalkBank, VARdoTeleport, YANwalkTeleport, YANwalkBank, YANdoTeleport, doMining, doBank, exitMines, error
	}

	public int speed = 10;

	public void getMouseSpeed(final int speed) {
		this.speed = speed;
		getMouseSpeed();
	}

	@Override
	protected int getMouseSpeed() {
		return speed;
	}

	public State getState() {
		try {
			if (playerInArea(essenceArea)) {
				if (isInventoryFull()) {
					return State.exitMines;
				}
				return State.doMining;
			}
			miningCount = -1;
			if (distanceTo(varrockBank) < 75) {
				if (isInventoryFull()) {
					if (playerInArea(varrockBankArea)
							|| getNearestObjectByID(bankBooth) != null
							&& tileOnScreen(getNearestObjectByID(bankBooth)
									.getLocation())) {
						return State.doBank;
					}
					return State.VARwalkBank;
				}
				if (getNearestNPCByID(Aubury) != null) {
					if (getNearestNPCByID(Aubury).isOnScreen()) {
						return State.VARdoTeleport;
					}
				}
				return State.VARwalkTeleport;
			} else {
				if (isInventoryFull()) {
					if (playerInArea(yanilleBankArea)) {
						return State.doBank;
					}
					return State.YANwalkBank;
				}
				if (getNearestNPCByID(Distentor) != null) {
					if (!playerInArea(mageGuild) && tileOnScreen(yanilleDoor)) {
						return State.YANdoTeleport;
					}
					if (getNearestNPCByID(Distentor).isOnScreen()) {
						return State.YANdoTeleport;
					}
				}
				return State.YANwalkTeleport;
			}
		} catch (final Exception e) {
		}
		return State.error;
	}

	@Override
	public boolean onStart(final Map<String, String> args) {
		sAdj = Integer.parseInt(args.get("mouseSpeed"));
		paint = args.get("disablepaint") == null;
		mapPaint = args.get("disablemap") == null;
		return true;
	}

	@Override
	public int loop() {
		try {
			getMouseSpeed(random(8 + sAdj, 11 + sAdj));

			if (!isLoggedIn()) {
				return random(50, 100);
			}

			if (!setAltitude) {
				setCameraAltitude(true);
				wait(random(250, 500));
				setAltitude = true;
				return random(50, 100);
			}

			if (!checkPickaxe) {
				if (!equipmentContainsOneOf(pickaxe)
						&& !inventoryContainsOneOf(pickaxe)) {
					log("Pickaxe was not found.");
					return -1;
				}
				checkPickaxe = true;
				return random(50, 100);
			}

			if (!foundType) {
				if (inventoryContainsOneOf(1436)) {
					value = grandExchange.loadItemInfo(1436).getMarketPrice();
					foundType = true;
				} else if (inventoryContainsOneOf(7936)) {
					value = grandExchange.loadItemInfo(7936).getMarketPrice();
					foundType = true;
				}
			}

			thePainter.scriptRunning = true;

			if (!thePainter.savedStats) {
				thePainter.saveStats();
			}

			if (getPlane() == 1 && playerInArea(varrockUpStairsArea)) {
				if (onTile(new RSTile(3256, 3421), "Climb", 0.5, 0.5, 0)) {
					wait(random(1500, 2000));
					while (getMyPlayer().isMoving()) {
						wait(random(90, 110));
					}
					wait(random(1500, 2000));
				}
				return random(50, 100);
			}
		} catch (final Exception e) {
		}

		try {
			if (getPlane() == 1 && playerInArea(mageGuild)) {
				if (onTile(new RSTile(2590, 3091), "Climb", 0.2, 0.5, 0)) {
					wait(random(1500, 2000));
					while (getMyPlayer().isMoving()) {
						wait(random(90, 110));
					}
					wait(random(1500, 2000));
				}
				return 100;
			}
		} catch (final Exception e) {
		}

		startRunning(runEnergy);

		try {
			switch (getState()) {
			case VARwalkTeleport:
				walkPath(varrockPath);
				return random(50, 100);
			case VARwalkBank:
				final Point checkScreen1 = Calculations.tileToScreen(
						varrockDoor, 0.5, 0, 50);
				if (pointOnScreen(checkScreen1)) {
					if (getObjectAt(varrockDoorCheck) != null
							&& distanceTo(new RSTile(3253, 3402)) <= 3) {
						if (onTile(varrockDoor, "Open", random(0.39, 0.61),
								random(0, 0.05), random(20, 50))) {
							failCount = 0;
							while (getObjectAt(varrockDoor) == null
									&& failCount < 40) {
								wait(random(50, 100));
								failCount++;
							}
						}
						if (getObjectAt(varrockDoor) == null) {
							return random(50, 100);
						}
					}
				}
				if (playerInArea(3257, 3419, 3250, 3419)) {
					if (getMyPlayer().isMoving()) {
						return random(50, 100);
					}
					walkTileMM(varrockBank);
					waitToMove(1000);
					return random(50, 100);
				}
				if (!walkPath(reversePath(varrockPath))) {
					if (!getMyPlayer().isMoving()) {
						if (theWalker
								.walkTo(new RSTile[] { varrockBank }, true)) {
							waitToMove(1000);
						}
					}
				}
				return random(50, 100);
			case VARdoTeleport:
				final Point checkScreen2 = Calculations.tileToScreen(
						varrockDoor, 0.5, 0, 50);
				if (pointOnScreen(checkScreen2)) {
					if (getObjectAt(varrockDoorCheck) != null) {
						if (onTile(varrockDoor, "Open", random(0.39, 0.61),
								random(0, 0.05), random(20, 50))) {
							failCount = 0;
							while (getObjectAt(varrockDoor) == null
									&& failCount < 40) {
								wait(random(50, 100));
								failCount++;
							}
						}
						if (getObjectAt(varrockDoor) == null) {
							return random(50, 100);
						}
					}
				}
				failCount = 0;
				if (getNearestNPCByID(Aubury).isOnScreen()) {
					if (onNPC(getNearestNPCByID(Aubury), "eleport")) {
						waitToMove(1000);
						wait(random(500, 750));
						if (getMyPlayer().getInteracting() == null) {
							return random(50, 100);
						}
						failCount = 0;
						while (!playerInArea(essenceArea) && failCount < 50) {
							wait(random(90, 110));
							if (getMyPlayer().isMoving()) {
								failCount = 0;
							}
							failCount++;
						}
					}
				} else {
					theWalker.walkTo(new RSTile[] { getNearestNPCByID(Aubury)
							.getLocation() }, true);
				}
				return random(50, 100);
			case YANwalkTeleport:
				if (walkPath(yanillePath)) {
					waitToMove(1000);
				}
				return random(50, 100);
			case YANwalkBank:
				if (playerInArea(mageGuild)) {
					if (onTile(yanilleDoor, "Open", random(0.1, 0.2), random(
							-0.5, 0.5), random(40, 50))) {
						waitToMove(1000);
						failCount = 0;
						while (playerInArea(mageGuild) && failCount < 20) {
							wait(random(90, 110));
							if (getMyPlayer().isMoving()) {
								failCount = 0;
							}
							failCount++;
						}
					}
					return random(50, 100);
				}
				if (!walkPath(reversePath(yanillePath))) {
					if (!getMyPlayer().isMoving()
							|| distanceTo(getDestination()) <= 5) {
						if (theWalker
								.walkTo(new RSTile[] { yanilleBank }, true)) {
							waitToMove(1000);
						}
					}
				}
				return random(50, 100);
			case YANdoTeleport:
				failCount = 0;
				if (!playerInArea(mageGuild)) {
					if (onTile(yanilleDoor, "Open", random(0.1, 0.2), random(
							-0.5, 0.5), random(40, 50))) {
						waitToMove(1000);
						failCount = 0;
						while (!playerInArea(mageGuild) && failCount < 20) {
							wait(random(90, 110));
							if (getMyPlayer().isMoving()) {
								failCount = 0;
							}
							failCount++;
						}
					}
					return random(50, 100);
				}
				if (getNearestNPCByID(Distentor).isOnScreen()) {
					if (onNPC(getNearestNPCByID(Distentor), "eleport")) {
						waitToMove(1000);
						wait(random(500, 750));
						if (getMyPlayer().getInteracting() == null) {
							return random(50, 100);
						}
						failCount = 0;
						while (!playerInArea(essenceArea) && failCount < 50) {
							wait(random(90, 110));
							if (getMyPlayer().isMoving()) {
								failCount = 0;
							}
							failCount++;
						}
					}
				} else {
					theWalker.walkTo(
							new RSTile[] { getNearestNPCByID(Distentor)
									.getLocation() }, true);
				}
				return random(50, 100);
			case doMining:
				if (miningCount == -1 || miningCount > 20) {
					if (onTile(findNearestEssenceTile(), "Mine", useX, useY, 0)) {
						miningCount = 0;
					}
				} else {
					miningCount++;
					if (getMyPlayer().getAnimation() != -1) {
						antiBan();
						miningCount = 0;
					} else {
						wait(random(90, 140));
					}
				}
				return random(50, 100);
			case doBank:
				if (playerInArea(3257, 3419, 3250, 3419)) {
					if (getMyPlayer().isMoving()) {
						return random(50, 100);
					}
					walkTileMM(varrockBank);
					waitToMove(1000);
					return random(50, 100);
				}
				doBank();
				return random(50, 100);
			case exitMines:
				if (onTile(getNearestObjectByID(portal).getLocation(), "Enter",
						0.5, 0.5, 0)) {
					waitToMove(1000);
					failCount = 0;
					while (playerInArea(essenceArea) && failCount < 30) {
						wait(random(90, 110));
						if (getMyPlayer().isMoving()) {
							failCount = 0;
						}
						failCount++;
					}
				}
				return random(50, 100);
			}
		} catch (final Exception e) {
		}

		return random(50, 100);
	}

	public void doBank() {
		int failCount = 0;
		try {
			if (!bank.isOpen()) {
				if (onTile(getNearestObjectByID(bankBooth).getLocation(),
						"Use-quickly", 0.5, 0.5, 0)) {
					waitToMove(1000);
				}
				while (!bank.isOpen() && failCount < 10) {
					wait(random(90, 110));
					if (getMyPlayer().isMoving()) {
						failCount = 0;
					}
					failCount++;
				}
			}
			if (bank.isOpen()) {
				wait(random(500, 750));
				if (inventoryContainsOneOf(pickaxe)) {
					if (bank.depositAllExcept(pickaxe)) {
						wait(random(500, 750));
					}
				} else {
					if (bank.depositAll()) {
						wait(random(500, 750));
					}
				}
			}
		} catch (final Exception e) {
		}
	}

	public void startRunning(final int energy) {
		if (getEnergy() >= energy && !isRunning()) {
			runEnergy = random(40, 95);
			setRun(true);
			wait(random(500, 750));
		}
	}

	public double pointDistance(final Point point) {
		if (point == null) {
			return Integer.MAX_VALUE;
		}
		final Point mouse = new Point(Bot.getInputManager().getX(), Bot
				.getInputManager().getY());
		return mouse.distance(point);
	}

	public boolean onTile(final RSTile tile, final String action,
			final double dx, final double dy, final int height) {
		if (!tile.isValid()) {
			return false;
		}
		Point checkScreen;
		try {
			checkScreen = Calculations.tileToScreen(tile, dx, dy, height);
			if (!pointOnScreen(checkScreen)) {
				if (distanceTo(tile) <= 8) {
					if (getMyPlayer().isMoving()) {
						return false;
					}
					walkTileMM(tile);
					waitToMove(1000);
					return false;
				}
				if (theWalker.walkTo(new RSTile[] { tile }, true)) {
					waitToMove(1000);
				}
				return false;
			}
		} catch (final Exception e) {
		}
		getMouseSpeed(random(8 + sAdj, 10 + sAdj));
		try {
			boolean stop = false;
			for (int i = 0; i <= 50; i++) {
				checkScreen = Calculations.tileToScreen(tile, dx, dy, height);
				if (!pointOnScreen(checkScreen)) {
					return false;
				}
				if (pointDistance(checkScreen) < 150) {
					getMouseSpeed(random(6 + sAdj, 8 + sAdj));
				}
				if (pointDistance(checkScreen) < 50) {
					getMouseSpeed(random(5 + sAdj, 7 + sAdj));
				}
				moveMouse(checkScreen);
				final Object[] menuItems = getMenuItems().toArray();
				for (int a = 0; a < menuItems.length; a++) {
					if (menuItems[a].toString().toLowerCase().contains(
							action.toLowerCase())) {
						stop = true;
						break;
					}
				}
				if (stop) {
					break;
				}
			}
		} catch (final Exception e) {
		}
		try {
			return atMenu(action);
		} catch (final Exception e) {
		}
		return false;
	}

	public boolean onNPC(final RSNPC npc, final String action) {
		if (npc == null) {
			return false;
		}
		Point checkScreen = null;
		try {
			checkScreen = npc.getScreenLocation();
			if (!pointOnScreen(checkScreen)) {
				if (distanceTo(npc.getLocation()) <= 8) {
					if (getMyPlayer().isMoving()) {
						return false;
					}
					walkTileMM(npc.getLocation());
					waitToMove(1000);
					return false;
				}
				if (theWalker.walkTo(new RSTile[] { npc.getLocation() }, true)) {
					waitToMove(1000);
				}
				return false;
			}
		} catch (final Exception e) {
		}
		getMouseSpeed(random(8 + sAdj, 10 + sAdj));
		try {
			boolean stop = false;
			for (int i = 0; i <= 50; i++) {
				checkScreen = npc.getScreenLocation();
				if (!pointOnScreen(checkScreen)) {
					return false;
				}
				if (pointDistance(checkScreen) < 150) {
					getMouseSpeed(random(6 + sAdj, 8 + sAdj));
				}
				if (pointDistance(checkScreen) < 50) {
					getMouseSpeed(random(5 + sAdj, 7 + sAdj));
				}
				moveMouse(checkScreen);
				final Object[] menuItems = getMenuItems().toArray();
				for (int a = 0; a < menuItems.length; a++) {
					if (menuItems[a].toString().toLowerCase().contains(
							action.toLowerCase())) {
						stop = true;
						break;
					}
				}
				if (stop) {
					break;
				}
			}
		} catch (final Exception e) {
		}
		try {
			return atMenu(action);
		} catch (final Exception e) {
		}
		return false;
	}

	public RSTile findNearestEssenceTile() {
		RSTile tile = null;
		int closest = 999;
		for (int i = 0; i < miningTiles.length; i++) {
			if (distanceTo(miningTiles[i]) < closest) {
				closest = distanceTo(miningTiles[i]);
				tile = miningTiles[i];
				useX = tilesX[i];
				useY = tilesY[i];
			}
		}
		return tile;
	}

	public boolean walkPath(final RSTile[] path) {
		if (!getMyPlayer().isMoving() || distanceTo(getDestination()) <= 5) {
			return theWalker.walkTo(path, true);
		}
		return false;
	}

	public boolean playerInArea(final int maxX, final int maxY, final int minX,
			final int minY) {
		final int x = getMyPlayer().getLocation().getX();
		final int y = getMyPlayer().getLocation().getY();
		if (x >= minX && x <= maxX && y >= minY && y <= maxY) {
			return true;
		}
		return false;
	}

	public boolean playerInArea(final int[] area) {
		final int x = getMyPlayer().getLocation().getX();
		final int y = getMyPlayer().getLocation().getY();
		if (x >= area[2] && x <= area[0] && y >= area[3] && y <= area[1]) {
			return true;
		}
		return false;
	}

	public boolean playerInArea(final Polygon area) {
		return area.contains(new Point(getMyPlayer().getLocation().getX(),
				getMyPlayer().getLocation().getY()));
	}

	public void antiBan() {
		getMouseSpeed(random(9 + sAdj, 12 + sAdj));
		final int random = random(1, 24);
		switch (random) {
		case 1:
			if (random(1, 10) != 1) {
				break;
			}
			moveMouse(random(10, 750), random(10, 495));
			break;
		case 2:
			if (random(1, 40) != 1) {
				break;
			}
			int angle = getCameraAngle() + random(-90, 90);
			if (angle < 0) {
				angle = random(0, 10);
			}
			if (angle > 359) {
				angle = random(0, 10);
			}
			setCameraRotation(angle);
			break;
		case 3:
			if (random(1, 3) != 1) {
				break;
			}
			moveMouseSlightly();
			break;
		default:
			break;
		}
		getMouseSpeed(random(8 + sAdj, 11 + sAdj));
	}

	@Override
	public void onFinish() {
		Bot.getEventManager().removeListener(PaintListener.class, this);
	}

	public void onRepaint(final Graphics g) {
		if (mapPaint) {
			theWalker.drawMap(g);
		}
		if (paint) {
			thePainter.paint(g);
		}
	}

	// If you use my paint please give credit.
	public class GarrettsPaint {

		final Rectangle r = new Rectangle(7, 345, 408, 114);
		final Rectangle r1 = new Rectangle(420, 345, 77, 25);
		final Rectangle r2 = new Rectangle(420, 374, 77, 26);
		final Rectangle r3 = new Rectangle(420, 404, 77, 26);
		final Rectangle r4 = new Rectangle(420, 434, 77, 25);
		final Rectangle r2c = new Rectangle(415, 374, 5, 26);
		final Rectangle r3c = new Rectangle(415, 404, 5, 26);
		final Rectangle r4c = new Rectangle(415, 434, 5, 25);
		final Rectangle sb1 = new Rectangle(12, 350, 398, 12);
		final Rectangle sb2 = new Rectangle(12, 363, 398, 12);
		final Rectangle sb3 = new Rectangle(12, 376, 398, 12);
		final Rectangle sb4 = new Rectangle(12, 389, 398, 12);
		final Rectangle sb5 = new Rectangle(12, 402, 398, 12);
		final Rectangle sb6 = new Rectangle(12, 415, 398, 12);
		final Rectangle sb7 = new Rectangle(12, 428, 398, 12);
		final Rectangle sb8 = new Rectangle(12, 441, 398, 12);
		final Rectangle sb1s = new Rectangle(12, 350, 196, 12);
		final Rectangle sb2s = new Rectangle(12, 363, 196, 12);
		final Rectangle sb3s = new Rectangle(12, 376, 196, 12);
		final Rectangle sb4s = new Rectangle(12, 389, 196, 12);
		final Rectangle sb5s = new Rectangle(12, 402, 196, 12);
		final Rectangle sb6s = new Rectangle(12, 415, 196, 12);
		final Rectangle sb7s = new Rectangle(12, 428, 196, 12);
		final Rectangle sb8s = new Rectangle(12, 441, 196, 12);
		final Rectangle sb9s = new Rectangle(213, 350, 196, 12);
		final Rectangle sb10s = new Rectangle(213, 363, 196, 12);
		final Rectangle sb11s = new Rectangle(213, 376, 196, 12);
		final Rectangle sb12s = new Rectangle(213, 389, 196, 12);
		final Rectangle sb13s = new Rectangle(213, 402, 196, 12);
		final Rectangle sb14s = new Rectangle(213, 415, 196, 12);
		final Rectangle sb15s = new Rectangle(213, 428, 196, 12);
		final Rectangle sb16s = new Rectangle(213, 441, 196, 12);
		Rectangle[] skillBars = new Rectangle[] { sb1, sb2, sb3, sb4, sb5, sb6,
				sb7, sb8 };
		boolean savedStats = false;
		boolean scriptRunning = false;
		boolean checkedCount = false;
		int currentTab = 0;
		int lastTab = 0;
		int[] barIndex = new int[16];
		int[] start_exp = null;
		int[] start_lvl = null;
		int[] gained_exp = null;
		int[] gained_lvl = null;

		Thread mouseWatcher = new Thread();
		final NumberFormat nf = NumberFormat.getInstance();

		final long time_ScriptStart = System.currentTimeMillis();
		long runTime = System.currentTimeMillis() - time_ScriptStart;

		int sine = 0;
		int sineM = 1;

		public void paint(final Graphics g) {
			if (!isLoggedIn() || !scriptRunning) {
				return;
			}

			// credits to Jacmob for the pulsing
			if (sine >= 84) {
				sine = 84;
				sineM *= -1;
			} else if (sine <= 1) {
				sine = 1;
				sineM *= -1;
			}
			sine += sineM;

			runTime = System.currentTimeMillis() - time_ScriptStart;
			final String formattedTime = formatTime((int) runTime);

			currentTab = paintTab();

			switch (currentTab) {
			case -1: // PAINT OFF
				g.setColor(new Color(0, 0, 0, 150));
				g.fillRect(r1.x, r1.y, r1.width, r1.height);
				g.setColor(Color.WHITE);
				drawString(g, "Show Paint", r1, 5);
				break;
			case 0: // DEFAULT TAB - MAIN
				drawPaint(g, r2c);
				g.setColor(new Color(100, 100, 100, 200));
				g.drawLine(r.x + 204, r.y + 22, r.x + 204, r.y + 109);
				g.setColor(Color.WHITE);
				g.setFont(new Font("sansserif", Font.BOLD, 14));
				drawString(g, properties.name(), r, -40);
				g.setFont(new Font("sansserif", Font.PLAIN, 12));
				drawStringMain(g, "Runtime: ", formattedTime, r, 20, 35, 0,
						true);
				int essPerHour = 0;
				int moneyPerHour = 0;
				final int gainedEXP = skills.getCurrentSkillExp(Skills
						.getStatIndex("mining"))
						- start_exp[Skills.getStatIndex("mining")];
				final int totalEssence = gainedEXP / 5;
				final int totalMoney = totalEssence * value;
				if (runTime / 1000 > 0) {
					essPerHour = (int) (3600000.0 / runTime * totalEssence);
					moneyPerHour = (int) (3600000.0 / runTime * totalMoney);
				}
				drawStringMain(g, "Essence Mined: ", Integer
						.toString(totalEssence), r, 20, 35, 2, true);
				drawStringMain(g, "Essence / Hour: ", Integer
						.toString(essPerHour), r, 20, 35, 3, true);

				drawStringMain(g, "Money Gained: ", Integer
						.toString(totalMoney), r, 20, 35, 2, false);
				drawStringMain(g, "Money / Hour: ", Integer
						.toString(moneyPerHour), r, 20, 35, 3, false);
				break;
			case 1: // INFO
				drawPaint(g, r3c);
				g.setColor(new Color(100, 100, 100, 200));
				g.drawLine(r.x + 204, r.y + 22, r.x + 204, r.y + 109);
				g.setColor(Color.WHITE);
				g.setFont(new Font("sansserif", Font.BOLD, 14));
				drawString(g, properties.name(), r, -40);
				g.setFont(new Font("sansserif", Font.PLAIN, 12));
				drawStringMain(g, "Version: ", Double.toString(properties
						.version()), r, 20, 35, 0, true);
				break;
			case 2: // STATS
				drawPaint(g, r4c);
				drawStats(g);
				hoverMenu(g);
				break;
			}
		}

		public void saveStats() {
			nf.setMinimumIntegerDigits(2);
			final String[] stats = Skills.statsArray;
			start_exp = new int[stats.length];
			start_lvl = new int[stats.length];
			for (int i = 0; i < stats.length; i++) {
				start_exp[i] = skills.getCurrentSkillExp(i);
				start_lvl[i] = skills.getCurrentSkillLevel(i);
			}
			for (int i = 0; i < barIndex.length; i++) {
				barIndex[i] = -1;
			}
			savedStats = true;
		}

		public int paintTab() {
			final Point mouse = new Point(Bot.getClient().getMouse().x, Bot
					.getClient().getMouse().y);
			if (mouseWatcher.isAlive()) {
				return currentTab;
			}
			if (r1.contains(mouse)) {
				mouseWatcher = new Thread(new MouseWatcher(r1));
				mouseWatcher.start();
				if (currentTab == -1) {
					return lastTab;
				} else {
					lastTab = currentTab;
					return -1;
				}
			}
			if (currentTab == -1) {
				return currentTab;
			}
			if (r2.contains(mouse)) {
				return 0;
			}
			if (r3.contains(mouse)) {
				return 1;
			}
			if (r4.contains(mouse)) {
				return 2;
			}
			return currentTab;
		}

		public void drawPaint(final Graphics g, final Rectangle rect) {
			g.setColor(new Color(0, 0, 0, 230));
			g.fillRect(r1.x, r1.y, r1.width, r1.height);
			g.fillRect(r2.x, r2.y, r2.width, r2.height);
			g.fillRect(r3.x, r3.y, r3.width, r3.height);
			g.fillRect(r4.x, r4.y, r4.width, r4.height);
			g.fillRect(rect.x, rect.y, rect.width, rect.height);
			g.fillRect(r.x, r.y, r.width, r.height);
			g.setColor(Color.WHITE);
			drawString(g, "Hide Paint", r1, 5);
			drawString(g, "MAIN", r2, 5);
			drawString(g, "INFO", r3, 5);
			drawString(g, "STATS", r4, 5);
			g.setColor(new Color(0, 0, 0, 230));
		}

		public void drawStat(final Graphics g, final int index, final int count) {
			if (count >= skillBars.length && !checkedCount) {
				skillBars = new Rectangle[] { sb1s, sb2s, sb3s, sb4s, sb5s,
						sb6s, sb7s, sb8s, sb9s, sb10s, sb11s, sb12s, sb13s,
						sb14s, sb15s, sb16s };
				checkedCount = true;
			}
			if (count >= skillBars.length) {
				return;
			}
			g.setFont(new Font("serif", Font.PLAIN, 11));
			g.setColor(new Color(100, 100, 100, 150));
			g.fillRect(skillBars[count].x, skillBars[count].y,
					skillBars[count].width, skillBars[count].height);
			final int percent = skills.getPercentToNextLevel(index);
			g.setColor(new Color(255 - 2 * percent,
					(int) (1.7 * percent + sine), 0, 150));
			g.fillRect(skillBars[count].x, skillBars[count].y,
					(int) (skillBars[count].width / 100.0 * percent),
					skillBars[count].height);
			g.setColor(Color.WHITE);
			final String name = Skills.statsArray[index];
			final String capitalized = name.substring(0, 1).toUpperCase()
					+ name.substring(1);
			g.drawString(capitalized, skillBars[count].x + 2,
					skillBars[count].y + 10);
			drawStringEnd(g, percent + "%", skillBars[count], -2, 4);
			barIndex[count] = index;
		}

		public void drawStats(final Graphics g) {
			final String[] stats = Skills.statsArray;
			int count = 0;
			gained_exp = new int[stats.length];
			gained_lvl = new int[stats.length];
			for (int i = 0; i < stats.length; i++) {
				gained_exp[i] = skills.getCurrentSkillExp(i) - start_exp[i];
				gained_lvl[i] = skills.getCurrentSkillLevel(i) - start_lvl[i];
				if (gained_exp[i] > 0) {
					drawStat(g, i, count);
					count++;
				}
			}
		}

		public void hoverMenu(final Graphics g) {
			final Point mouse = new Point(Bot.getClient().getMouse().x, Bot
					.getClient().getMouse().y);
			final Rectangle r_main = new Rectangle(mouse.x, mouse.y - 150, 300,
					150);
			for (int i = 0; i < barIndex.length; i++) {
				if (barIndex[i] > -1) {
					if (skillBars[i].contains(mouse)) {
						final int xpTL = skills.getXPToNextLevel(barIndex[i]);
						final int xpHour = (int) (3600000.0 / runTime * gained_exp[barIndex[i]]);
						final int TTL = (int) ((double) xpTL / (double) xpHour * 3600000);
						g.setColor(new Color(50, 50, 50, 240));
						g.fillRect(r_main.x, r_main.y, r_main.width,
								r_main.height);
						g.setColor(Color.WHITE);
						g.setFont(new Font("sansserif", Font.BOLD, 15));
						drawString(g, Skills.statsArray[barIndex[i]]
								.toUpperCase(), r_main, -58);
						g.setFont(new Font("sansserif", Font.PLAIN, 12));
						hoverDrawString(g, "Current Level: ", skills
								.getCurrentSkillLevel(barIndex[i])
								+ "", r_main, 40, 0);
						hoverDrawString(g, "XP Gained: ",
								gained_exp[barIndex[i]] + "xp", r_main, 40, 1);
						hoverDrawString(g, "XP / Hour: ", xpHour + "xp",
								r_main, 40, 2);
						hoverDrawString(g, "LVL Gained: ",
								gained_lvl[barIndex[i]] + " lvls", r_main, 40,
								3);
						hoverDrawString(g, "XPTL: ", xpTL + "xp", r_main, 40, 4);
						hoverDrawString(g, "TTL: ", formatTime(TTL), r_main,
								40, 5);
					}
				}
			}
		}

		public void hoverDrawString(final Graphics g, final String str,
				final String val, final Rectangle rect, final int offset,
				final int index) {
			g.setColor(Color.WHITE);
			final FontMetrics font = g.getFontMetrics();
			final Rectangle2D bounds = font.getStringBounds(val, g);
			final int width = (int) bounds.getWidth();
			final int y = rect.y + offset + 20 * index;
			g.drawString(str, rect.x + 5, y);
			g.drawString(val, rect.x + rect.width - width - 5, y);
			if (index < 5) {
				g.setColor(new Color(100, 100, 100, 200));
				g.drawLine(rect.x + 5, y + 5, rect.x + rect.width - 5, y + 5);
			}
		}

		public void drawString(final Graphics g, final String str,
				final Rectangle rect, final int offset) {
			final FontMetrics font = g.getFontMetrics();
			final Rectangle2D bounds = font.getStringBounds(str, g);
			final int width = (int) bounds.getWidth();
			g.drawString(str, rect.x + (rect.width - width) / 2, rect.y
					+ rect.height / 2 + offset);
		}

		public void drawStringEnd(final Graphics g, final String str,
				final Rectangle rect, final int xOffset, final int yOffset) {
			final FontMetrics font = g.getFontMetrics();
			final Rectangle2D bounds = font.getStringBounds(str, g);
			final int width = (int) bounds.getWidth();
			g.drawString(str, rect.x + rect.width - width + xOffset, rect.y
					+ rect.height / 2 + yOffset);
		}

		public void drawStringMain(final Graphics g, final String str,
				final String val, final Rectangle rect, final int xOffset,
				final int yOffset, final int index, final boolean leftSide) {
			final FontMetrics font = g.getFontMetrics();
			final Rectangle2D bounds = font.getStringBounds(val, g);
			final int indexMult = 17;
			final int width = (int) bounds.getWidth();
			if (leftSide) {
				g.drawString(str, rect.x + xOffset, rect.y + yOffset + index
						* indexMult);
				g.drawString(val, rect.x + rect.width / 2 - width - xOffset,
						rect.y + yOffset + index * indexMult);
			} else {
				g.drawString(str, rect.x + rect.width / 2 + xOffset, rect.y
						+ yOffset + index * indexMult);
				g.drawString(val, rect.x + rect.width - width - xOffset, rect.y
						+ yOffset + index * indexMult);
			}
		}

		public String formatTime(final int milliseconds) {
			final long t_seconds = milliseconds / 1000;
			final long t_minutes = t_seconds / 60;
			final long t_hours = t_minutes / 60;
			final int seconds = (int) (t_seconds % 60);
			final int minutes = (int) (t_minutes % 60);
			final int hours = (int) (t_hours % 60);
			return nf.format(hours) + ":" + nf.format(minutes) + ":"
					+ nf.format(seconds);
		}

		public class MouseWatcher implements Runnable {

			Rectangle rect = null;

			MouseWatcher(final Rectangle rect) {
				this.rect = rect;
			}

			public void run() {
				Point mouse = new Point(Bot.getClient().getMouse().x, Bot
						.getClient().getMouse().y);
				while (rect.contains(mouse)) {
					try {
						mouse = new Point(Bot.getClient().getMouse().x, Bot
								.getClient().getMouse().y);
						Thread.sleep(50);
					} catch (final Exception e) {
					}
				}
			}

		}

	}

	// Please give credit if you decide to use
	public class TheWalker {

		Methods methods = new Methods();
		Thread walker = null;
		RSTile[] path = null;

		public void drawMap(final Graphics g) {
			if (walker != null && walker.isAlive()) {
				final Point myTile = tileToMinimap(getMyPlayer().getLocation());
				final Point center = new Point(myTile.x + 2, myTile.y + 2);
				g.drawOval(center.x - 70, center.y - 70, 140, 140);
				if (path == null) {
					return;
				}
				for (int i = 0; i < path.length; i++) {
					final RSTile tile = path[i];
					final Point p = tileToMinimap(tile);
					if (p.x != -1 && p.y != -1) {
						g.setColor(Color.BLACK);
						g.fillRect(p.x + 1, p.y + 1, 3, 3);
						if (i > 0) {
							final Point p1 = tileToMinimap(path[i - 1]);
							g.setColor(Color.ORANGE);
							if (p1.x != -1 && p1.y != -1) {
								g
										.drawLine(p.x + 2, p.y + 2, p1.x + 2,
												p1.y + 2);
							}
						}
					}
				}
				final Point tile = tileToMinimap(nextTile(path));
				g.setColor(Color.RED);
				if (tile.x != -1 && tile.y != -1) {
					g.fillRect(tile.x + 1, tile.y + 1, 3, 3);
				}
				g.setColor(Color.BLACK);
			}
		}

		public boolean walkTo(final RSTile[] path, final boolean waitUntilDest) {
			final Walker walkto = new Walker(path, 3, 10000);
			walker = new Thread(walkto);
			walker.start();
			waitToMove(random(800, 1200));
			if (waitUntilDest) {
				while (walker.isAlive()) {
					methods.wait(random(300, 600));
				}
				return walkto.done;
			} else {
				return true;
			}
		}

		public Point tileToMM(final RSTile tile) {
			return new Point(tileToMinimap(tile).x + 2,
					tileToMinimap(tile).y + 2);
		}

		public boolean tileOnMM(final RSTile tile) {
			return pointOnMM(tileToMM(tile));
		}

		public boolean pointOnMM(final Point point) {
			final Point myTile = tileToMM(getMyPlayer().getLocation());
			final Point center = new Point(myTile.x, myTile.y);
			return center.distance(point) < 70 ? true : false;
		}

		public RSTile getClosestTileOnMap(final RSTile tile) {
			if (isLoggedIn() && !tileOnMM(tile)) {
				try {
					final RSTile loc = getMyPlayer().getLocation();
					final RSTile walk = new RSTile(
							(loc.getX() + tile.getX()) / 2, (loc.getY() + tile
									.getY()) / 2);
					return tileOnMM(walk) ? walk : getClosestTileOnMap(walk);
				} catch (final Exception e) {
				}
			}
			return tile;
		}

		public RSTile nextTile(final RSTile[] path) {
			for (int i = path.length - 1; i >= 0; i--) {
				if (tileOnMM(path[i])) {
					return path[i];
				}
			}
			return getClosestTileOnMap(path[0]);
		}

		public class Walker implements Runnable {

			RSTile tile = null;
			boolean done = false;
			boolean stop = false;
			int movementTimer = 10000;
			int distanceTo = 3;

			Walker(final RSTile[] userpath) {
				tile = userpath[userpath.length - 1];
				path = userpath;
			}

			Walker(final RSTile[] userpath, final int distanceTo,
					final int movementTimer) {
				tile = userpath[userpath.length - 1];
				this.movementTimer = movementTimer;
				this.distanceTo = distanceTo;
				path = userpath;
			}

			public void run() {
				long timer = System.currentTimeMillis();
				RSTile lastTile = getMyPlayer().getLocation();
				int randomReturn = random(5, 8);
				while (distanceTo(tile) > distanceTo && !stop) {
					if (!getMyPlayer().isMoving() || getDestination() == null
							|| distanceTo(getDestination()) < randomReturn) {
						final RSTile nextTile = nextTile(path);
						if (getDestination() != null
								&& distanceBetween(getDestination(), nextTile) <= distanceTo) {
							continue;
						}
						getMouseSpeed(random(6 + sAdj, 8 + sAdj));
						walkTileMM(nextTile);
						getMouseSpeed(random(8 + sAdj, 10 + sAdj));
						waitToMove(random(800, 1200));
						randomReturn = random(5, 8);
					}
					final RSTile myLoc = getMyPlayer().getLocation();
					if (myLoc != lastTile) {
						if (distanceBetween(myLoc, lastTile) > 30) {
							log("Teleportation Detected. Stopping The Walking Loop.");
							stop = true;
						}
						timer = System.currentTimeMillis();
						lastTile = myLoc;
					}
					if (System.currentTimeMillis() - timer > movementTimer) {
						stop = true;
					}
					methods.wait(random(20, 40));
				}
				if (distanceTo(tile) <= distanceTo) {
					done = true;
				}
			}

		}

	}

}