/**
 * wRedChins.java
 *
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

import org.rsbot.bot.Bot;
import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSInterfaceComponent;
import org.rsbot.script.wrappers.RSItemTile;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = { "Weirded" }, category = "Hunter", name = "wRedChins", description = "<html><head><style type='text/css'> body {font-family: 'Tahoma'; font-size: 10px; margin-left: 10px;}</style></head><body><h3>wRedChins by Weirded<h3><br/><b>Timeout: </b><input type=\"text\" name=\"timeout\" value=\"3000\"/>(Increase this if you lag and have problems)<br /></p><p><b>Notice: </b>Make sure you turn off BreakHandler unless you want to lose alot of traps!</p><br /><p>Log in and stand where you want to hunt before starting.</p><br /><p><b>Purchase MultiHunterPro at http://www.rsbot.org/vb/showthread.php?t=123802</b></p></body></html>", version = 3.2)
public class wRedChins extends Script implements PaintListener,
		ServerMessageListener {

	private final ScriptManifest scriptInfo = getClass().getAnnotation(
			ScriptManifest.class);

	private final int trapItem = 10008;
	private final int trapFailed = 19192;
	private final int trapCaught = 19190;
	private final int trapSetup = 19187;
	private final double expPer = 265;

	private int numCaught = 0;
	private boolean notOurs = false;
	private boolean ohNoes = false;

	private int timeout = 3000;

	private final ArrayList<RSTile> setTraps = new ArrayList<RSTile>();
	private final RSTile[] prefPos = new RSTile[5];
	private int numTraps = 0;

	private RSTile homeTile = null;

	private long startTime;
	private int startExp;
	private int startLevel;
	public long runTime = 0, seconds = 0, minutes = 0, hours = 0;
	public int gainedExp = 0;
	public int expToLevel = 0;
	public long secToLevel = 0;
	public long minutesToLevel = 0;
	public long hoursToLevel = 0;
	public float secExp = 0;
	public float minuteExp = 0;
	public float hourExp = 0;

	@Override
	protected int getMouseSpeed() {
		return random(5, 7);
	}

	@Override
	public boolean onStart(final Map<String, String> args) {
		if (!isLoggedIn()) {
			log("Log in and stand where you want to hunt before starting.");
			return false;
		} else {
			timeout = Integer.parseInt(args.get("timeout"));
			homeTile = getMyPlayer().getLocation();
			startTime = System.currentTimeMillis();
			startLevel = skills.getCurrentSkillLevel(STAT_HUNTER);
			startExp = skills.getCurrentSkillExp(STAT_HUNTER);
			return true;
		}
	}

	@Override
	public void onFinish() {
		Bot.getEventManager().removeListener(PaintListener.class, this);
		Bot.getEventManager().removeListener(ServerMessageListener.class, this);
		log("Caught: " + numCaught);
	}

	/**
	 * Do something at inventory item
	 *
	 * @param itemID
	 *            The ID of the item
	 * @param hover
	 *            Do we want to hover or click?
	 * @return Success
	 */
	public boolean doInventoryItem(final int itemID, final boolean hover) {
		try {
			if (getCurrentTab() != Constants.TAB_INVENTORY
					&& !RSInterface.getInterface(Constants.INTERFACE_BANK)
							.isValid()
					&& !RSInterface.getInterface(Constants.INTERFACE_STORE)
							.isValid()) {
				openTab(Constants.TAB_INVENTORY);
			}

			final RSInterfaceChild inventory = getInventoryInterface();
			if (inventory == null || inventory.getComponents() == null) {
				return false;
			}

			final java.util.List<RSInterfaceComponent> possible = new ArrayList<RSInterfaceComponent>();
			for (final RSInterfaceComponent item : inventory.getComponents()) {
				if (item != null && item.getComponentID() == itemID) {
					possible.add(item);
				}
			}

			if (possible.size() == 0) {
				return false;
			}

			final RSInterfaceComponent item = possible.get(random(0, Math.min(
					2, possible.size())));

			if (!item.isValid()) {
				return false;
			}
			final Rectangle pos = item.getArea();
			if (pos.x == -1 || pos.y == -1 || pos.width == -1
					|| pos.height == -1) {
				return false;
			}

			final int dx = (int) (pos.getWidth() - 4) / 2;
			final int dy = (int) (pos.getHeight() - 4) / 2;
			final int midx = (int) (pos.getMinX() + pos.getWidth() / 2);
			final int midy = (int) (pos.getMinY() + pos.getHeight() / 2);

			moveMouse(midx + random(-dx, dx), midy + random(-dy, dy));

			if (!hover) {
				clickMouse(true);
			}

		} catch (final Exception e) {
			log("atInventoryItem(final int itemID, final String option) Error: "
					+ e);
			return false;
		}

		return true;
	}

	/**
	 * Estimates the menu length.
	 *
	 * @return A pixel estimate of the menu length.
	 */
	public int getMenuLengthEstimate() {
		int longest = 0;
		for (int i = 1; i < getMenuItems().size(); i++) {
			if (getMenuItems().get(i).length() > getMenuItems().get(longest)
					.length()) {
				longest = i;
			}
		}

		return getMenuItems().get(longest).length() * 7;
	}

	@Override
	public boolean atMenuItem(final int i) {
		if (!isMenuOpen()) {
			return false;
		}
		try {
			if (getMenuItems().size() < i) {
				return false;
			}
			final RSTile menu = getMenuLocation();
			final int menuLength = getMenuLengthEstimate();
			final int xOff = random((menuLength / 2 - 15),
					(menuLength / 2 + 15));
			final int yOff = random(21, 29) + 15 * i;
			moveMouse(menu.getX() + xOff, menu.getY() + yOff, 2, 2);
			if (!isMenuOpen()) {
				return false;
			}
			clickMouse(true);
			return true;
		} catch (final Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Run if we ain't
	 */
	public void doRun() {
		if (!isRunning()) {
			setRun(true);
			wait(random(750, 1000));
		}
	}

	/**
	 * Simple tile walk function Picks between minimap or tile if it's on
	 * screen.
	 *
	 * @param tile
	 *            The RSTile
	 */
	public void walkTile(final RSTile tile) {
		if (tileOnScreen(tile)) {
			atTile(tile, "Walk");
		} else {
			walkTo(tile);
		}
	}

	/**
	 * @param tile
	 *            The RSTile
	 * @return If the tile has a trap.
	 */
	public boolean hasTrap(final RSTile tile) {
		RSObject obj;
		if (tile != null) {
			obj = getObjectAt(tile);
			if (obj != null) {
				if (obj.getID() >= 19170 && obj.getID() <= 19210) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @param tile
	 *            The tile to check.
	 * @return If the tile has a trap setup.
	 */
	public boolean hasTrapSetup(final RSTile tile) {
		RSObject obj;
		if (tile != null) {
			obj = getObjectAt(tile);
			if (obj != null) {
				if (obj.getID() == trapSetup) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @param tile
	 *            The tile to check.
	 * @return If the tile has a trap item.
	 */
	public boolean hasTrapItem(final RSTile tile) {
		if (tile == null) {
			return false;
		}
		final RSItemTile[] itemtile = getGroundItemsAt(tile);
		for (final RSItemTile anItemtile : itemtile) {
			if (anItemtile.getItem().getID() == trapItem) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @param tile
	 *            The tile to check.
	 * @return If the tile has an object that could get in the way.
	 */
	public boolean hasInterfereObject(final RSTile tile) {
		RSObject obj;
		if (tile != null) {
			obj = getObjectAt(tile);
			if (obj != null) {
				if (obj.getType() == 0 || obj.getType() == 2) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Wait until we setup a trap.
	 *
	 * @param tile
	 *            The tile we're setting it on.
	 * @return <tt>true</tt> if the trap was set; <tt>false</tt> after timeout.
	 */
	public boolean waitOnSetup(final RSTile tile) {
		final long start = System.currentTimeMillis();
		while (!hasTrapSetup(tile)) {
			if (System.currentTimeMillis() - start > timeout + 2000) {
				return false;
			}
			wait(10);
		}
		return true;
	}

	/**
	 * Wait til we have retrieved trap.
	 *
	 * @param tile
	 *            The tile to retrieve the trap from.
	 * @return <tt>true</tt> if the trap was retrieved; <tt>false</tt> after
	 *         timeout.
	 */
	public boolean waitOnRetrieve(final RSTile tile) {
		final long start = System.currentTimeMillis();
		while (hasTrap(tile)) {
			if (System.currentTimeMillis() - start > timeout
					|| getMyPlayer().isIdle()) {
				return false;
			}
			wait(10);
		}
		return true;
	}

	/**
	 * Wait while we're moving.
	 *
	 * @return <tt>true</tt> if the player stopped; <tt>false</tt> after
	 *         timeout.
	 */
	public boolean waitWhileMoving() {
		final long start = System.currentTimeMillis();
		while (getMyPlayer().isMoving()) {
			if (System.currentTimeMillis() - start > timeout) {
				return false;
			}
			wait(10);
		}
		return true;
	}

	/**
	 * Wait til we arrive at a given tile.
	 *
	 * @param tile
	 *            The RSTile to arrive at.
	 * @return <tt>true</tt> if the player arrived at the tile; <tt>false</tt>
	 *         after timeout.
	 */
	public boolean waitToArrive(final RSTile tile) {
		final long start = System.currentTimeMillis();
		while (distanceTo(tile) > 0) {
			if (System.currentTimeMillis() - start > timeout
					|| getMyPlayer().isIdle()) {
				return false;
			}
			wait(10);
		}
		return true;
	}

	/**
	 * Wait until character is idle..
	 *
	 * @return <tt>true</tt> if the player is idle; <tt>false</tt> after
	 *         timeout.
	 */
	public boolean waitTilIdle() {
		final long start = System.currentTimeMillis();
		while (!getMyPlayer().isIdle()) {
			if (System.currentTimeMillis() - start > timeout) {
				return false;
			}
			wait(10);
		}
		return true;
	}

	/**
	 * Finds a fallen trap, if it's mine lay it else pick it up.
	 *
	 * @return True if we succeeded.
	 */
	public boolean fixTrap() {
		final RSItemTile fallenTrap = getGroundItemByID(trapItem);

		if (fallenTrap == null) {
			return false;
		}

		if (!waitTilIdle()) {
			return false;
		}

		if (setTraps.contains(fallenTrap) && !hasTrap(fallenTrap)) {
			atTile(fallenTrap, "Lay");
			if (waitForAnim(timeout) == -1) {
				return false;
			}
			if (!waitOnSetup(fallenTrap)) {
				setTraps.remove(fallenTrap);
			}
			return true;
		} else if (setTraps.contains(fallenTrap) && hasTrap(fallenTrap)) {
			if (getInventoryCount() < 28 - numTrapsTotal()) {
				atTile(fallenTrap, "Take");
				waitToMove(timeout);
				waitWhileMoving();
				setTraps.remove(fallenTrap);
			}
		/*} else {
			if (getInventoryCount() < 28 - numTrapsTotal()) {
				atTile(fallenTrap, "Take");
				if (!waitToMove(timeout)) {
					return false;
				}
				waitWhileMoving();
			}*/
		}

		return false;
	}

	/**
	 * Retrieve a trap.
	 *
	 * @return True if we succeeded.
	 */
	public boolean retrieveTrap() {
		final RSObject trap = getNearestTrap(trapCaught, trapFailed);
		String action;

		if (trap == null) {
			return false;
		}

		if (!waitTilIdle()) {
			return false;
		}

		final RSTile loc = trap.getLocation();

		if (setTraps.contains(loc)) {
			if (trap.getID() == trapFailed) {
				action = "Dismantle";
			} else if (trap.getID() == trapCaught) {
				action = "Check";
			} else {
				return false;
			}

			atTile(loc, action);
			if (waitForAnim(timeout) == -1) {
				if (notOurs) {
					setTraps.remove(loc);
					notOurs = false;
				}
				return false;
			}
			if (!waitOnRetrieve(loc)) {
				if (notOurs) {
					setTraps.remove(loc);
					notOurs = false;
				}
				return false;
			}
			if (!hasTrap(loc)) {
				setTraps.remove(loc);
				return true;
			}
		}

		return false;
	}

	/**
	 * Lay a trap.
	 *
	 * @return True if we succeeded.
	 */
	public boolean layTrap() {

		final RSTile freeTile = findFreeSpot();

		if (freeTile == null) {
			return false;
		}
		if (hasTrap(freeTile) || hasTrapItem(freeTile)) {
			return false;
		}
		if (!freeTile.equals(getMyPlayer().getLocation())) {
			walkTile(freeTile);
			if (!waitToMove(timeout)) {
				return false;
			}
			waitToArrive(freeTile);
		}
		if (freeTile.equals(getMyPlayer().getLocation())) {
			doInventoryItem(trapItem, false);
			if (waitForAnim(timeout) == -1) {
				return false;
			}
			mouseToTile(homeTile);
			waitOnSetup(freeTile);
			if (hasTrapSetup(freeTile)) {
				if (hasTrapItem(freeTile)) {
					fixTrap();
					return false;
				}
				setTraps.add(freeTile);
				return true;
			}
		}

		return false;
	}

	/**
	 * Find a free spot.
	 *
	 * @return The RSTile.
	 */
	public RSTile findFreeSpot() {
		return getNearestPrefTile();
	}

	/**
	 * Find a free preferred tile to lay a trap on.
	 *
	 * @return The RSTile.
	 */
	public RSTile getNearestPrefTile() {
		RSTile tile = null;
		double dist = -1;
		double distTemp;

		for (int i = 0; i < numTraps; i++) {
			if (!setTraps.contains(prefPos[i])
					&& !hasInterfereObject(prefPos[i])) {
				distTemp = calculateDistance(getMyPlayer().getLocation(),
						prefPos[i]);
				if (distTemp < dist || tile == null) {
					dist = distTemp;
					tile = prefPos[i];
				} else if (distTemp == dist) {
					final int rand = random(1, 3);
					if (rand == 2) {
						tile = prefPos[i];
					}
				}
			}
		}

		return tile;
	}

	/**
	 * Finds the nearest trap of ours.
	 *
	 * @param ids
	 *            Which ids to look for.
	 * @return The RSObject.
	 */
	public RSObject getNearestTrap(final int... ids) {
		RSObject o;
		RSObject cur = null;
		double dist = -1;
		for (final RSTile setTrap : setTraps) {
			o = getObjectAt(setTrap);
			if (o != null) {
				boolean isObject = false;
				for (final int id : ids) {
					if (o.getID() == id) {
						isObject = true;
						break;
					}
				}
				if (isObject) {
					final double distTmp = calculateDistance(getMyPlayer()
							.getLocation(), o.getLocation());
					if (distTmp < dist || cur == null) {
						dist = distTmp;
						cur = o;
					} else if (distTmp == dist) {
						final int rand = random(1, 3);
						if (rand == 2) {
							cur = o;
						}
					}
				}
			}
		}

		return cur;
	}

	/**
	 * Move mouse to a tile.
	 *
	 * @param tile
	 *            Which tile we want to move to.
	 */
	public void mouseToTile(final RSTile tile) {
		final Point p = Calculations.tileToScreen(tile);
		moveMouse(p, 7, 7);
	}

	/**
	 * How many traps are down total?
	 *
	 * @return The amount of traps currently setup.
	 */
	public int numTrapsTotal() {
		int count = 0;
		int num;
		if (setTraps.size() < numTraps) {
			num = setTraps.size();
		} else {
			num = numTraps;
		}
		for (int i = 0; i < num; i++) {
			if (hasTrap(setTraps.get(i)) || hasTrapItem(setTraps.get(i))) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Setup preferred spots and number of traps based on level.
	 */
	public void setupStuff() {
		if (skills.getCurrentSkillLevel(Constants.STAT_HUNTER) < 63) {
			log("Hunter level must be grater than 63");
			// stopScript();
		} else if (skills.getCurrentSkillLevel(Constants.STAT_HUNTER) < 80) {
			numTraps = 4;
		} else {
			numTraps = 5;
		}

		switch (numTraps) {
		case 5:
			prefPos[4] = new RSTile(homeTile.getX(), homeTile.getY());

		case 4:
			prefPos[3] = new RSTile(homeTile.getX() - 1, homeTile.getY() - 1);
			prefPos[2] = new RSTile(homeTile.getX() + 1, homeTile.getY() - 1);
			prefPos[1] = new RSTile(homeTile.getX() - 1, homeTile.getY() + 1);
			prefPos[0] = new RSTile(homeTile.getX() + 1, homeTile.getY() + 1);
			break;
		default:
			break;
		}

		doRun();
		cleanUp();
		if (ohNoes) {
			log("Finding our other trap");
			doTheChaCha();
		}
	}

	/**
	 * Fail-safe.
	 */
	public void cleanUp() {
		// Remove any empty spots.
		for (int i = 0; i < setTraps.size(); i++) {
			if (!hasTrap(setTraps.get(i)) && !hasTrapItem(setTraps.get(i))) {
				setTraps.remove(i);
			}
		}

		// Remove any duplicates.
		final HashSet<RSTile> h = new HashSet<RSTile>(setTraps);
		setTraps.clear();
		setTraps.addAll(h);

		// Remove any extra spots that might be caused from a trap that dropped
		// during lag.
		if (setTraps.size() > numTraps) {
			for (int i = 0; i < setTraps.size(); i++) {
				if (hasTrapItem(setTraps.get(i))) {
					setTraps.remove(i);
				}
			}
		}
	}

	/**
	 * Another Fail-safe, better safe than sorry.
	 */
	public void doTheChaCha() {
		RSTile tile;
		final int range = 2;
		final int pX = homeTile.getX();
		final int pY = homeTile.getY();
		final int minX = pX - range;
		final int minY = pY - range;
		final int maxX = pX + range;
		final int maxY = pY + range;
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				tile = new RSTile(x, y);
				if (!setTraps.contains(tile)) {
					if (getObjectAt(tile) == null) {
						continue;
					} else if (getObjectAt(tile).getID() == trapCaught) {
						atTile(tile, "Check");
					} else if (getObjectAt(tile).getID() == trapFailed
							|| getObjectAt(tile).getID() == trapSetup) {
						atTile(tile, "Dismantle");
					} else {
						continue;
					}
					if (waitForAnim(timeout) == -1) {
						if (notOurs) {
							notOurs = false;
						}
						continue;
					}
					if (waitOnRetrieve(tile)) {
						break;
					}
				}
			}
		}
		ohNoes = false;
	}

	@Override
	public int loop() {
		if (!isLoggedIn()) {
			return random(2500, 3000);
		}

		// In case we move away from the hunting spot somehow.
		if (distanceTo(homeTile) >= 4) {
			walkTile(homeTile);
			waitToMove(timeout);
			waitWhileMoving();
			return random(200, 400);
		}

		setupStuff();

		if (!fixTrap()) {
			if (numTrapsTotal() < numTraps) {
				if (!layTrap()) {
					retrieveTrap();
				}
			} else {
				retrieveTrap();
			}
		}

		return random(75, 100);
	}

	public void onRepaint(final Graphics g) {
		if (!isLoggedIn() || g == null) {
			return;
		}

		g.setFont(new Font("Tahoma", Font.PLAIN, 10));
		g.setColor(Color.WHITE);

		for (final RSTile setTrap : setTraps) {
			try {
				final Point screen = Calculations.tileToScreen(setTrap, 10);
				if (!pointOnScreen(screen)) {
					continue;
				}
				if (getObjectAt(setTrap) == null) {
					g.setColor(Color.WHITE);
				} else if (hasTrapSetup(setTrap)) {
					g.setColor(Color.GREEN);
				} else if (getObjectAt(setTrap).getID() == trapCaught) {
					g.setColor(Color.ORANGE);
				} else if (hasTrap(setTrap)) {
					g.setColor(Color.YELLOW);
				} else if (hasTrapItem(setTrap)) {
					g.setColor(Color.RED);
				} else {
					g.setColor(Color.BLACK);
				}
				g.drawRect((int) screen.getX(), (int) screen.getY(), 4, 4);
			} catch (final Exception ignored) {
			}
		}

		final Color BG = new Color(0, 0, 0, 75);
		final Color RED = new Color(255, 0, 0, 255);
		final Color GREEN = new Color(0, 255, 0, 255);
		final Color BLACK = new Color(0, 0, 0, 255);

		if (startTime == 0) {
			startTime = System.currentTimeMillis();
		}

		if (startExp == 0) {
			startExp = skills.getCurrentSkillExp(Constants.STAT_HUNTER);
		}

		if (startLevel == 0) {
			startLevel = skills.getCurrentSkillLevel(Constants.STAT_HUNTER);
		}

		runTime = System.currentTimeMillis() - startTime;
		seconds = runTime / 1000;
		if (seconds >= 60) {
			minutes = seconds / 60;
			seconds -= minutes * 60;
		}
		if (minutes >= 60) {
			hours = minutes / 60;
			minutes -= hours * 60;
		}

		gainedExp = skills.getCurrentSkillExp(Constants.STAT_HUNTER) - startExp;
		expToLevel = skills.getXPToNextLevel(Constants.STAT_HUNTER);

		if ((minutes > 0 || hours > 0 || seconds > 0) && gainedExp > 0) {
			secExp = (float) gainedExp
					/ (float) (seconds + minutes * 60 + hours * 60 * 60);
		}
		minuteExp = secExp * 60;
		hourExp = minuteExp * 60;

		if (secExp > 0) {
			secToLevel = (int) (expToLevel / secExp);
		}
		if (secToLevel >= 60) {
			minutesToLevel = secToLevel / 60;
			secToLevel -= minutesToLevel * 60;
		} else {
			minutesToLevel = 0;
		}
		if (minutesToLevel >= 60) {
			hoursToLevel = minutesToLevel / 60;
			minutesToLevel -= hoursToLevel * 60;
		} else {
			hoursToLevel = 0;
		}

		g.setFont(new Font("Tahoma", Font.PLAIN, 10));
		g.setColor(BG);
		g.fill3DRect(345, 10, 160, 140, true);
		g.setColor(BLACK);
		g.drawString(scriptInfo.name() + " v" + scriptInfo.version(), 350 + 1,
				25 + 1);
		g.setColor(Color.white);
		g.drawString(scriptInfo.name() + " v" + scriptInfo.version(), 350, 25);
		g.drawString("Running for: " + hours + ":" + minutes + ":" + seconds,
				350, 40);
		g
				.drawString(
						"Exp Gained: "
								+ gainedExp
								+ " ("
								+ (skills
										.getCurrentSkillLevel(Constants.STAT_HUNTER) - startLevel)
								+ ")", 350, 55);
		g.drawString("Stuff Caught: " + numCaught, 350, 70);
		g.drawString("Exp per hour: " + (int) hourExp, 350, 85);
		g.drawString("Exp to level: " + expToLevel + " ("
				+ (int) (expToLevel / expPer + 0.5) + " catches)", 350, 100);
		g.drawString("Time to level: " + hoursToLevel + ":" + minutesToLevel
				+ ":" + secToLevel, 350, 115);
		g.drawString("Progress to next level:", 350, 130);
		g.setColor(RED);
		g.fill3DRect(350, 135, 100, 11, true);
		g.setColor(GREEN);
		g.fill3DRect(350, 135, skills
				.getPercentToNextLevel(Constants.STAT_HUNTER), 11, true);
		g.setColor(BLACK);
		g.drawString(skills.getPercentToNextLevel(Constants.STAT_HUNTER)
				+ "%  to "
				+ (skills.getCurrentSkillLevel(Constants.STAT_HUNTER) + 1),
				380, 144);
	}

	public void serverMessageRecieved(final ServerMessageEvent e) {
		final String message = e.getMessage().toLowerCase();
		if (message.contains("caught")) {
			numCaught++;
		}
		if (message.contains("isn't your trap")) {
			notOurs = true;
		}
		if (message.contains("high enough")
				|| message.contains("have more than")) {
			ohNoes = true;
		}
	}

}