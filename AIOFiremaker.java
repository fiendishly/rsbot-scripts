import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.rsbot.bot.Bot;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Bank;
import org.rsbot.script.Calculations;
import org.rsbot.script.Constants;
import org.rsbot.script.Methods;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSInterfaceComponent;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSPlayer;
import org.rsbot.script.wrappers.RSTile;
import org.rsbot.util.GlobalConfiguration;

/**
 * RSBot AIO Firemaker
 * 
 * @author Jacmob
 * @version 1.31
 */
@ScriptManifest(authors = { "Jacmob" }, category = "Firemaking", name = "AIO Firemaker", version = 1.31, description = "<html><body style='font-family: Arial; padding: 0px; text-align: center; background-color: #EEDDAA;'><div style=\"background-color: #BB3300; width: 100%; height: 34px; border: 3px coral solid;\"><h1 style=\"font-size: 13px; color: #FFFF00;\">AIO Firemaker</h1></div><br />The intelligent, all-in-one firemaker by Jacmob.<br />Select your account and press OK.<br /><br /><small>Your bank must be scrolled all the way up.</small></body></html>")
public class AIOFiremaker extends Script implements PaintListener {

	private enum State {
		FIREMAKE, OPENBANK, BANK
	}

	public final int TINDERBOX = 590;
	public final int FIRE_RING = 13659;

	public final int FLAME_GLOVES = 13660;
	private final int[] FireObjects = { 2732, 2982, 2983, 2984, 2985, 2986,
			1189 };
	private final Color BG = new Color(100, 0, 0, 150);
	private final Color DROP = new Color(20, 0, 0, 255);

	private final Color TEXT = new Color(200, 255, 0, 255);
	private int Logs = 0;
	private String LogName = "";
	private String EQString = "";
	private double XPPerFire = 0;
	private double XPMultiplier = 1;
	private int nextMinRunEnergy = random(20, 50);
	private int currentZone = 0;
	private int sine = 0;
	private int sineM = 1;
	private int scriptStartXP = 0;
	private long scriptStartTime = 0;
	private long lastXPCheckTime = 0;
	private FMLocation Location;
	private RSTile nextTile = null;
	private RSTile blackListedTile = null;
	private boolean itemClicked = false;

	private final AStar pathFinder = new AStar();

	private RSTile checkTile(final RSTile tile) {
		if (distanceTo(tile) < 17) {
			return tile;
		}
		final RSTile loc = getMyPlayer().getLocation();
		final RSTile walk = new RSTile((loc.getX() + tile.getX()) / 2, (loc
				.getY() + tile.getY()) / 2);
		return distanceTo(walk) < 17 ? walk : checkTile(walk);
	}

	private void checkXP() {
		if (System.currentTimeMillis() - lastXPCheckTime < 300000) {
			return;
		}
		lastXPCheckTime = System.currentTimeMillis();
		openTab(Constants.TAB_STATS);
		wait(random(50, 500));
		if (random(0, 5) != 1) {
			if (random(0, 2) == 1) {
				moveMouse(random(575, 695), random(240, 435), 10);
			}
			moveMouse(688, 372, 7, 7);
			wait(random(800, 3400));
		}
		if (random(0, 2) == 0) {
			openTab(Constants.TAB_INVENTORY);
		}
		itemClicked = false;
	}

	private boolean clickInventoryItem(final int itemID) {
		if (moveToInventoryItem(itemID)) {
			clickMouse(true, 5);
			return true;
		}
		return false;
	}

	private RSTile getBestFreeTile() {
		final RSTile nextT = getProceedingTile(getMyPlayer().getLocation());
		if (nextT != null) {
			final RSTile nextT2 = getProceedingTile(nextT);
			if (nextT2 != null && isFreeTile(nextT) && isFreeTile(nextT2)
					&& !isPlayerAt(nextT) && !isPlayerAt(nextT2)) {
				return nextT;
			}
		}
		int start = 0, longest = 0, length = 0;
		RSTile[] bestRow = Location.zones[currentZone].rows[0].tiles;
		outer: for (int i = 0; i < Location.zones[currentZone].rows.length; i++) {
			final RSTile[] tiles = Location.zones[currentZone].rows[i].tiles;
			// log.info("Traversing Row " + i);
			for (int j = 0; j < tiles.length; j++) {
				if (tiles[j] == blackListedTile) {
					blackListedTile = null;
					continue outer;
				}
				if (isFireAt(tiles[j])) {
					if (j - start > length
							|| (j - start >= getInventoryCount(Logs) || j
									- start == length)
							&& distanceTo(tiles[start]) < distanceTo(bestRow[longest])) {
						length = j - start;
						longest = start;
						bestRow = tiles;
						// log.info("  Set Best - " + i + ", " + start + " (" +
						// length + "/" + tiles.length + ")");
					}
					start = j + 1;
				} else if (j == tiles.length - 1) {
					if (j - start > length - 1
							|| (j - start + 1 >= getInventoryCount(Logs) || j
									- start == length - 1)
							&& distanceTo(tiles[start]) < distanceTo(bestRow[longest])) {
						length = j - start + 1;
						longest = start;
						bestRow = tiles;
						// log.info("  Set Best - " + i + ", " + start + " (" +
						// length + "/" + tiles.length + ")");
					}
				}
			}
			start = 0;
		}

		// log("Calculated Best Tile: " + bestRow[longest].getX() + "," +
		// bestRow[longest].getY());
		if (length == 0 && start != 0) {
			return null;
		} else {
			return bestRow[longest];
		}
	}

	private RSTile getClosestTileInRegion(final RSTile tile) {
		if (tileInRegion(tile)) {
			return tile;
		}
		final RSTile loc = getMyPlayer().getLocation();
		final RSTile walk = new RSTile((loc.getX() + tile.getX()) / 2, (loc
				.getY() + tile.getY()) / 2);
		return tileInRegion(walk) ? walk : getClosestTileInRegion(walk);
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

		if (timeMillis == 1000) {
			type = "second";
		} else if (timeMillis == 60000) {
			type = "minute";
		} else if (timeMillis == 3600000) {
			type = "hour";
		}

		return hoursString + minutesString + secondsString + " " + type;
	}

	private RSTile getProceedingTile(final RSTile location) {
		final FMRow[] rows = Location.zones[currentZone].rows;
		final int x = location.getX(), y = location.getY();
		for (final FMRow row : rows) {
			final int start = Math.min(row.start, row.end);
			final int end = Math.max(row.start, row.end);
			if (Location.zones[currentZone].horizontal && y == row.pos
					&& x >= start && x <= end) {
				if (row.start < row.end && x + 1 <= row.end) {
					return new RSTile(x + 1, y);
				} else if (row.start > row.end && x - 1 >= row.end) {
					return new RSTile(x - 1, y);
				}
				return null;
			} else if (!Location.zones[currentZone].horizontal && x == row.pos
					&& y >= start && y <= end) {
				if (row.start < row.end && y + 1 <= row.end) {
					return new RSTile(x, y + 1);
				} else if (row.start > row.end && y - 1 >= row.end) {
					return new RSTile(x, y - 1);
				}
				return null;
			}
		}
		return null;
	}

	private State getState() {
		if (!inventoryContains(Logs) || !inventoryContains(TINDERBOX)) {
			if (bank.isOpen()) {
				return State.BANK;
			} else {
				return State.OPENBANK;
			}
		} else {
			return State.FIREMAKE;
		}
	}

	private void highlightTile(final Graphics g, final RSTile t,
			final Color outline, final Color fill) {
		final Point pn = Calculations.tileToScreen(t.getX(), t.getY(), 0, 0, 0);
		final Point px = Calculations.tileToScreen(t.getX(), t.getY(), 1, 0, 0);
		final Point py = Calculations.tileToScreen(t.getX(), t.getY(), 0, 1, 0);
		final Point pxy = Calculations
				.tileToScreen(t.getX(), t.getY(), 1, 1, 0);
		if (py.x == -1 || pxy.x == -1 || px.x == -1 || pn.x == -1) {
			return;
		}
		g.setColor(outline);
		g.drawPolygon(new int[] { py.x, pxy.x, px.x, pn.x }, new int[] { py.y,
				pxy.y, px.y, pn.y }, 4);
		g.setColor(fill);
		g.fillPolygon(new int[] { py.x, pxy.x, px.x, pn.x }, new int[] { py.y,
				pxy.y, px.y, pn.y }, 4);
	}

	private boolean isFireAt(final RSTile location) {
		final RSObject obj = getObjectAt(location);
		if (obj == null) {
			return false;
		}
		final int objID = obj.getID();
		for (final int i : FireObjects) {
			if (objID == i) {
				return true;
			}
		}
		return false;
	}

	private boolean isFreeTile(final RSTile location) {
		return isInRow(location) && !isFireAt(location);
	}

	private boolean isInRow(final RSTile location) {
		final FMRow[] rows = Location.zones[currentZone].rows;
		final int x = location.getX(), y = location.getY();
		for (final FMRow row : rows) {
			final int start = Math.min(row.start, row.end);
			final int end = Math.max(row.start, row.end);
			if (Location.zones[currentZone].horizontal && y == row.pos
					&& x >= start && x <= end
					|| !Location.zones[currentZone].horizontal && x == row.pos
					&& y >= start && y <= end) {
				return true;
			}
		}
		return false;
	}

	private boolean isPlayerAt(final RSTile tile) {
		final int[] validPlayers = Bot.getClient().getRSPlayerIndexArray();
		final org.rsbot.accessors.RSPlayer[] players = Bot.getClient()
				.getRSPlayerArray();

		for (final int element : validPlayers) {
			if (players[element] == null) {
				continue;
			}
			final RSPlayer player = new RSPlayer(players[element]);
			try {
				if (Methods.distanceBetween(player.getLocation(), tile) == 0) {
					return true;
				}
			} catch (final Exception ignored) {
			}
		}
		return false;
	}

	@Override
	public int loop() {
		if (scriptStartTime == -1
				&& skills.getRealSkillLevel(Constants.STAT_FIREMAKING) > 1) {
			final int fireLevel = skills
					.getRealSkillLevel(Constants.STAT_FIREMAKING);
			scriptStartTime = System.currentTimeMillis();
			scriptStartXP = skills
					.getCurrentSkillExp(Constants.STAT_FIREMAKING);

			if (equipmentContains(FIRE_RING, FLAME_GLOVES)) {
				log
						.info("Your ring and gloves will grant you 5% extra XP per log.");
				XPMultiplier = 1.05;
				EQString = " (+5%)";
			} else if (equipmentContains(FIRE_RING)) {
				log
						.info("Your Ring of Fire will grant you 2% extra XP per log.");
				if (fireLevel >= 79) {
					log
							.info("Flame Gloves with your ring would grant you 5% extra XP per log.");
				}
				XPMultiplier = 1.02;
				EQString = " (+2%)";
			} else if (equipmentContains(FLAME_GLOVES)) {
				log
						.info("A Ring of Fire with your gloves would grant you 5% extra XP per log.");
				XPMultiplier = 1.02;
				EQString = " (+2%)";
			} else if (fireLevel >= 79) {
				log
						.info("A Ring of Fire and Flame Gloves would grant you 5% extra XP per log.");
			} else if (fireLevel >= 62) {
				log
						.info("A Ring of Fire from 'All Fired Up' would grant you 2% extra XP per log.");
			}
		}
		final State state = getState();
		switch (state) {
		case FIREMAKE:
			if (!isFreeTile(getMyPlayer().getLocation())) {
				nextTile = getBestFreeTile();
				if (distanceTo(nextTile) == 0) {
					return random(100, 200);
				}
				if (nextTile != null) {
					int iters = 0;
					while (distanceTo(nextTile) > 0) {
						if (isFireAt(nextTile)) {
							blackListedTile = nextTile;
							return random(100, 300);
						}
						if (tileOnScreen(nextTile) && !bank.isOpen()) {
							if (itemClicked) {
								unUse();
							}
							final Point location = Calculations
									.tileToScreen(nextTile);
							if (location.x == -1 || location.y == -1) {
								break;
							} else {
								moveMouse(location, 5, 5);
							}
							atTile(nextTile, "Walk here");
							wait(random(200, 400));
							if (random(0, 3) != 0) {
								moveToInventoryItem(TINDERBOX);
							}
							wait(random(50, 100));
						} else {
							if (getEnergy() > nextMinRunEnergy) {
								setRun(true);
								nextMinRunEnergy = random(20, 50);
							}
							if (distanceTo(nextTile) < 16) {
								walkTo(nextTile, 0, 0);
								wait(random(200, 500));
							} else {
								walkTo(nextTile);
							}
							itemClicked = false;
						}
						int tries = 0;
						while (tries < 10 && distanceTo(nextTile) > 0
								&& getMyPlayer().isMoving()) {
							tries++;
							wait(random(300, 500));
						}
						wait(random(50, 80));
						final RSTile proceeding = getProceedingTile(nextTile);
						if (proceeding != null && distanceTo(proceeding) == 0
								&& isFreeTile(proceeding) || iters > 50
								|| distanceTo(nextTile) > 100) {
							break;
						}
						iters++;
					}
				}
			} else {
				while (bank.isOpen()) {
					bank.close();
				}
				if (random(0, 200) == 0) {
					checkXP();
				}
				if (!itemClicked) {
					clickInventoryItem(TINDERBOX);
					itemClicked = true;
					wait(random(10, 100));
				}
				if (random(0, 40) == 0) {
					new FMAntiBanThread().start();
				}
				final boolean oneLog = getInventoryCount(Logs) == 1;
				final RSTile currTile = getMyPlayer().getLocation();
				nextTile = getProceedingTile(currTile);
				if (nextTile != null && !isFreeTile(nextTile)) {
					nextTile = null;
				}
				RSTile secondTile = nextTile == null ? null
						: getProceedingTile(nextTile);
				if (secondTile != null && !isFreeTile(secondTile)) {
					secondTile = null;
				}
				wait(random(150, 200));
				clickInventoryItem(Logs);
				if (random(0, 40) == 0) {
					new FMAntiBanThread().start();
				}
				itemClicked = false;
				if (random(0, 15) != 0 && secondTile != null && !oneLog) {
					wait(random(150, 400));
					clickInventoryItem(TINDERBOX);
					itemClicked = true;
					if (random(0, 5) != 0) {
						moveToInventoryItem(Logs);
					}
				} else if (random(0, 3) == 0) {
					moveMouse(random(75, 700), random(750, 400), 50);
					wait(random(100, 2500));
					if (random(0, 2) == 1) {
						moveMouse(random(75, 600), random(75, 400), 30);
					}
				}
				if (random(0, 40) == 0) {
					new FMAntiBanThread().start();
				}
				while (getMyPlayer().getAnimation() != -1
						|| getMyPlayer().isMoving()) {
					wait(random(70, 120));
				}
				if (random(0, 200) == 0) {
					checkXP();
				}
				int tries = 0;
				while (tries < 5
						&& (nextTile != null && distanceTo(nextTile) != 0 || distanceTo(currTile) == 0)) {
					wait(random(100, 150));
					tries++;
				}
				return random(175, 250);
			}
			break;
		case OPENBANK:
			final RSTile bankLoc = nearestBank();
			nextTile = bankLoc;
			if (getEnergy() > nextMinRunEnergy) {
				setRun(true);
				nextMinRunEnergy = random(20, 50);
			}
			if (itemClicked) {
				unUse();
			}
			if (bankLoc == null) {
				walkTo(Location.bank);
			} else if (tileOnScreen(bankLoc)) {
				if (openBank()) {
					int tries = 0;
					while (!bank.isOpen() && tries < 10) {
						if (getMyPlayer().isMoving()) {
							wait(random(400, 800));
						} else {
							wait(random(300, 500));
						}
						tries++;
					}
				} else {
					wait(random(200, 400));
					rotateCamera();
				}
			} else if (distanceTo(bankLoc) < 4) {
				rotateTowards(bankLoc);
			} else if (Location.randomness == -1) {
				walkTo(bankLoc, 1, 1);
			} else {
				walkTo(new RSTile(Location.bank.getX()
						+ random(0, Location.randomness + 1), Location.bank
						.getY()
						+ random(0, Location.randomness + 1)));
			}
			break;
		case BANK:
			bank.depositAllExcept(Logs, TINDERBOX);
			while (!inventoryContains(TINDERBOX)) {
				withdraw(TINDERBOX, "Tinderbox", false);
				wait(random(1100, 1500));
			}
			while (!inventoryContains(Logs)) {
				withdraw(Logs, LogName, true);
				wait(random(500, 800));
				if (!inventoryContains(Logs)) {
					wait(random(500, 700));
				}
			}
			if (random(0, 2) == 0) {
				bank.close();
			}
			currentZone = random(0, Location.zones.length);
			break;
		default:
			break;
		}
		return random(500, 1000);
	}

	private boolean moveToInventoryItem(final int itemID) {
		if (getCurrentTab() != Constants.TAB_INVENTORY) {
			openTab(Constants.TAB_INVENTORY);
		}
		final int[] items = getInventoryArray();
		for (int i = 0; i < items.length; i++) {
			if (items[i] == itemID) {
				final Point loc = getInventoryItemPoint(i);
				moveMouse(loc.x + 12, loc.y + 12, 5, 5);
				return true;
			}
		}
		return false;
	}

	private RSTile nearestBank() {
		final RSObject Booth = getNearestObjectByID(Bank.BankBooths);
		RSNPC BankerNPC = getNearestNPCByID(Bank.Bankers);
		RSObject BankChest = getNearestObjectByID(Bank.BankChests);
		int minDist = 30;
		if (BankChest != null) {
			minDist = distanceTo(BankChest);
		}
		if (BankerNPC != null && distanceTo(BankerNPC) < minDist) {
			minDist = distanceTo(BankerNPC);
			BankChest = null;
		}
		if (Booth != null && distanceTo(Booth) < minDist) {
			BankerNPC = null;
		}
		if (BankerNPC != null) {
			return BankerNPC.getLocation();
		}
		if (BankChest != null) {
			return BankChest.getLocation();
		}
		if (Booth != null) {
			return Booth.getLocation();
		}
		return null;
	}

	@Override
	public void onFinish() {
		log
				.info("AIO Firemaker Stopped. You gained "
						+ (skills.getCurrentSkillExp(Constants.STAT_FIREMAKING) - scriptStartXP)
						+ " XP in "
						+ getFormattedTime(System.currentTimeMillis()
								- scriptStartTime) + ".");
	}

	public void onRepaint(final Graphics g) {
		if (isLoggedIn()) {
			if (bank.isOpen()) {
				final RSInterfaceChild logIF = bank.getItemByID(Logs);
				final RSInterfaceChild tbIF = bank.getItemByID(TINDERBOX);
				g.setColor(Color.green);
				if (logIF != null && logIF.getAbsoluteY() < 270) {
					g.drawRect(logIF.getAbsoluteX() - 1,
							logIF.getAbsoluteY() - 1, logIF.getWidth() + 2,
							logIF.getHeight() + 2);
				}
				g.setColor(Color.blue);
				if (tbIF != null && tbIF.getAbsoluteY() < 270) {
					g.drawRect(tbIF.getAbsoluteX() - 1,
							tbIF.getAbsoluteY() - 1, tbIF.getWidth() + 2, tbIF
									.getHeight() + 2);
				}
			} else if (nextTile != null) {
				highlightTile(g, nextTile, new Color(255, 0, 0, 20), new Color(
						255, 255, 0, 20));
			}

			if (scriptStartTime == -1) {
				return;
			}

			final String title = "AIOFiremaker "
					+ getClass().getAnnotation(ScriptManifest.class).version()
					+ " by Jacmob";
			final int x = 13;
			int y = 26;

			if (sine >= 84) {
				sine = 84;
				sineM *= -1;
			} else if (sine <= 1) {
				sine = 1;
				sineM *= -1;
			}

			sine += sineM;

			g.setColor(BG);
			g.fill3DRect(x - 6, y, 211, 25, true);

			g.setColor(DROP);
			g.drawString(title, x + 1, y += 18);
			g.setColor(TEXT);
			g.drawString(title, x, y -= 1);

			if (XPPerFire == 0) {
				return;
			}

			final int fireLevel = skills
					.getRealSkillLevel(Constants.STAT_FIREMAKING);
			final int levelsGained = fireLevel
					- skills.getLvlByExp(scriptStartXP);
			final int XPGained = skills
					.getCurrentSkillExp(Constants.STAT_FIREMAKING)
					- scriptStartXP;
			final int lvlPerc = skills
					.getPercentToNextLevel(Constants.STAT_FIREMAKING);
			final int XPToLevel = skills
					.getXPToNextLevel(Constants.STAT_FIREMAKING);
			final long RunMillis = System.currentTimeMillis() - scriptStartTime;

			final int LogsBurned = (int) Math.round(XPGained
					/ (XPPerFire * XPMultiplier));
			String lvlStr = levelsGained + " lvls";
			if (levelsGained == 1) {
				lvlStr = "1 lvl";
			}

			g.setColor(BG);
			g.fill3DRect(x - 6, y += 11, 211, 112, true);

			g.setColor(TEXT);
			if (scriptStartTime == 0) {
				g.drawString("Waiting For > Lvl 1...", x, y += 17);
			} else {
				g.drawString("Runtime: " + getFormattedTime(RunMillis), x,
						y += 17);
			}
			g.drawString("Gained: " + XPGained + " XP (" + lvlStr + ")", x,
					y += 17);
			g.drawString("Burned: " + LogsBurned + " Logs" + EQString, x,
					y += 17);
			g.drawString("Next Level: " + (fireLevel + 1) + " ("
					+ (int) Math.ceil(XPToLevel / (XPPerFire * XPMultiplier))
					+ " Fires)", x, y += 17);
			g.drawString("Next Level: " + XPToLevel + " XP", x, y += 17);

			g.setColor(new Color(0, 0, 0, 150));
			g.fill3DRect(x, y += 8, 199, 12, true);

			g.setColor(new Color(255 - 2 * lvlPerc,
					(int) (1.7 * lvlPerc + sine), 0, 150));
			g.fillRect(x + 1, y += 1, (int) (1.97 * lvlPerc), 10);
			g.setColor(new Color(255, 255, 255, 50));
			g.fillRect(x + 1, y, (int) (1.97 * lvlPerc), 5);

			g.setColor(BG);

			if (XPGained >= 1000) {

				final int XPPerSecond = (int) (XPGained * 1000 / RunMillis);
				final int SecsToLevel = XPToLevel / XPPerSecond;

				g.fill3DRect(x - 6, y += 21, 211, 59, true);
				g.setColor(TEXT);
				g.drawString("Next Level: "
						+ getFormattedTime(SecsToLevel * 1000), x, y += 17);
				g.drawString("Averaging: " + XPPerSecond * 3600 + " XP/hr", x,
						y += 17);
				g.drawString("Averaging: "
						+ (int) Math.ceil(LogsBurned * 360000D / RunMillis)
						+ "0 Logs/hr", x, y += 17);
			} else {
				g.fill3DRect(x - 6, y += 21, 211, 25, true);
				g.setColor(TEXT);
				g.drawString("Calculating...", x, y += 17);
			}

		}
	}

	@Override
	public boolean onStart(final Map<String, String> map) {
		log.info("Waiting for options to be set...");
		final FMFrame gui = new FMFrame("AIO Firemaker");
		while (gui.isVisible()) {
			wait(500);
		}
		Location = gui.getSelectedLocation();
		if (Location == null) {
			return false;
		}
		Logs = (int) gui.getSelectedLogInfo()[0];
		XPPerFire = gui.getSelectedLogInfo()[1];
		LogName = gui.getSelectedLogName();
		scriptStartTime = -1;
		log.info("Options Loaded - Burning " + LogName + "...");
		return true;
	}

	private boolean openBank() {
		final RSObject booth = getNearestObjectByID(Bank.BankBooths);
		if (distanceTo(new RSTile(3183, 3434)) > 40) {
			return bank.open();
		} else if (booth == null) {
			return false;
		} else if (tileOnScreen(booth.getLocation())) {
			return atObject(booth, "Use-quickly");
		} else {
			walkTo(checkTile(booth.getLocation()));
		}
		return false;
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

	private void rotateTowards(final RSTile tile) {
		final int angle = getAngleToTile(tile);
		char LR = KeyEvent.VK_LEFT;
		if (angle < getCameraAngle()) {
			LR = KeyEvent.VK_RIGHT;
		}
		Bot.getInputManager().pressKey(LR);
		wait(random(200, 400));
		Bot.getInputManager().releaseKey(LR);
		wait(random(50, 100));
	}

	private boolean tileInRegion(final RSTile tile) {
		final int tileX = tile.getX() - Bot.getClient().getBaseX(), tileY = tile
				.getY()
				- Bot.getClient().getBaseY();
		return !(tileX < 0 || tileY < 0 || tileX > 103 || tileY > 103);
	}

	private void unUse() {
		final int rand = random(0, 2);
		if (getCurrentTab() != Constants.TAB_INVENTORY) {
			openTab(Constants.TAB_INVENTORY);
		} else if (rand == 0) {
			openTab(Constants.TAB_FRIENDS);
			wait(random(200, 500));
			if (random(0, 3) != 0) {
				moveMouse(random(575, 695), random(238, 418));
			}
			openTab(Constants.TAB_INVENTORY);
		} else {
			checkXP();
		}
		itemClicked = false;
	}

	@Override
	public boolean walkTo(final RSTile tile) {
		RSTile dest = getMyPlayer().getLocation();
		RSTile[] path = null;
		if (Methods.distanceBetween(tile, dest) > 1) {
			dest = getClosestTileInRegion(tile);
			path = pathFinder.findPath(getMyPlayer().getLocation(), dest);
		}
		if (path == null) {
			return false;
		}
		for (int i = path.length - 1; i >= 0; i--) {
			if (distanceTo(path[i]) < 17
					&& getRealDistanceTo(path[i], false) < 60) {
				final RSTile currDest = getDestination();
				if (currDest != null) {
					if (Methods.distanceBetween(currDest, path[i]) <= 3) {
						break;
					}
				}
				walkTo(checkTile(path[i]), 1, 1);
				wait(random(200, 400));
				final RSTile cdest = getDestination();
				if (cdest != null && distanceTo(cdest) > 6) {
					wait(random(300, 500));
				}
				break;
			}
		}
		return true;
	}

	private void withdraw(final int ID, final String name, final boolean all) {
		if (!bank.isOpen()) {
			return;
		}
		final RSInterfaceComponent[] scrollbox = getChildInterface(
				Constants.INTERFACE_BANK, Constants.INTERFACE_BANK_SCROLLBAR)
				.getComponents();
		if (scrollbox.length > 1) {
			final int scrollTop = scrollbox[0].getAbsoluteY();
			if (scrollbox[1].getAbsoluteY() > scrollTop + 1) {
				clickMouse(scrollbox[0].getAbsoluteX() + random(2, 4),
						scrollTop + random(2, 4), true);
			}
		}
		RSInterfaceChild bankItem = bank.getItemByID(ID);
		if (bankItem == null) {
			log.severe("Unable to withraw " + name
					+ ". You will be logged out in ten seconds.");
			wait(random(9000, 11000));
			while (bank.isOpen()) {
				bank.close();
			}
			stopScript();
		} else if (bankItem.getAbsoluteY() > 270
				&& atInterface(getChildInterface(Constants.INTERFACE_BANK,
						Constants.INTERFACE_BANK_BUTTON_SEARCH), "Search")) {
			wait(random(1200, 1500));
			final int rand = random(0, Math.min(4, name.length() - 2));
			sendText(name.toLowerCase().substring(0, name.length() - rand),
					false);
			wait(random(900, 1200));
			bankItem = bank.getItemByID(ID);
			if (bankItem == null
					|| bankItem.getAbsoluteY() > 270
					|| !bank.atItem(ID, all ? "Withdraw-All " + name
							: "Withdraw-1 " + name)
					&& !bank.atItem(ID, all ? "Withdraw-All " + name
							: "Withdraw-1 " + name)) {
				log.severe("Unable to withraw " + name
						+ ". You will be logged out in ten seconds.");
				wait(random(9000, 11000));
				while (bank.isOpen()) {
					bank.close();
				}
				stopScript();
			} else {
				atInterface(getChildInterface(Constants.INTERFACE_BANK, 50));
			}
		} else {
			bank
					.atItem(ID, all ? "Withdraw-All " + name : "Withdraw-1 "
							+ name);
		}
	}

}

class AStar {

	private class Node {

		public int x, y;
		public Node parent;
		public double g, f;

		public Node(final int x, final int y) {
			this.x = x;
			this.y = y;
			g = f = 0;
		}

		public boolean isAt(final Node another) {
			return x == another.x && y == another.y;
		}

		public RSTile toRSTile(final int baseX, final int baseY) {
			return new RSTile(x + baseX, y + baseY);
		}

	}

	private int[][] blocks;

	public AStar() {

	}

	private Node cheapestNode(final ArrayList<Node> open) {
		Node c = null;
		for (final Node t : open) {
			if (c == null || t.f < c.f) {
				c = t;
			}
		}
		return c;
	}

	private double diagonalHeuristic(final Node current, final Node end) {
		final double dx = Math.abs(current.x - end.x);
		final double dy = Math.abs(current.y - end.y);
		final double diag = Math.min(dx, dy);
		final double straight = dx + dy;
		return Math.sqrt(2.0) * diag + straight - 2 * diag;
	}

	public RSTile[] findPath(final RSTile cur, final RSTile dest) {
		final int baseX = Bot.getClient().getBaseX(), baseY = Bot.getClient()
				.getBaseY();
		final int currX = cur.getX() - baseX, currY = cur.getY() - baseY;
		final int destX = dest.getX() - baseX, destY = dest.getY() - baseY;
		if (currX < 0 || currY < 0 || currX > 103 || currY > 103 || destX < 0
				|| destY < 0 || destX > 103 || destY > 103) {
			return null;
		}
		final ArrayList<Node> closed = new ArrayList<Node>(), open = new ArrayList<Node>();
		blocks = Bot.getClient().getRSGroundDataArray()[Bot.getClient()
				.getPlane()].getBlocks();
		Node current = new Node(currX, currY);
		final Node destination = new Node(destX, destY);
		open.add(current);
		while (open.size() > 0) {
			current = cheapestNode(open);
			closed.add(current);
			open.remove(open.indexOf(current));
			for (final Node n : getSurroundingWalkableNodes(current)) {
				if (!isIn(closed, n)) {
					if (!isIn(open, n)) {
						n.parent = current;
						n.g = current.g + getAdditionalCost(n, current);
						n.f = n.g + diagonalHeuristic(n, destination);
						open.add(n);
					} else {
						final Node old = getNode(open, n);
						if (current.g + getAdditionalCost(old, current) < old.g) {
							old.parent = current;
							old.g = current.g + getAdditionalCost(old, current);
							old.f = old.g + diagonalHeuristic(old, destination);
						}
					}
				}
			}
			if (isIn(closed, destination)) {
				return getPath(closed.get(closed.size() - 1), baseX, baseY);
			}
		}
		return null;
	}

	private double getAdditionalCost(final Node start, final Node end) {
		double cost = 1.0;
		if (!(start.x == end.y) || start.x == end.y) {
			cost = Math.sqrt(2.0);
		}
		return cost;
	}

	private Node getNode(final ArrayList<Node> nodes, final Node key) {
		for (final Node n : nodes) {
			if (n.isAt(key)) {
				return n;
			}
		}
		return null;
	}

	private RSTile[] getPath(final Node endNode, final int baseX,
			final int baseY) {
		final ArrayList<RSTile> reversePath = new ArrayList<RSTile>();
		Node p = endNode;
		while (p.parent != null) {
			reversePath.add(p.toRSTile(baseX, baseY));
			final int next = (int) (Math.random() * 4 + 5);
			for (int i = 0; i < next && p.parent != null; i++) {
				p = p.parent;
			}
		}
		final RSTile[] fixedPath = new RSTile[reversePath.size()];
		for (int i = 0; i < fixedPath.length; i++) {
			fixedPath[i] = reversePath.get(fixedPath.length - 1 - i);
		}
		return fixedPath;
	}

	private ArrayList<Node> getSurroundingWalkableNodes(final Node t) {
		final ArrayList<Node> tiles = new ArrayList<Node>();
		final int curX = t.x, curY = t.y;
		if (curX > 0 && curY < 103
				&& (blocks[curX - 1][curY + 1] & 0x1280138) == 0
				&& (blocks[curX - 1][curY] & 0x1280108) == 0
				&& (blocks[curX][curY + 1] & 0x1280120) == 0) {
			tiles.add(new Node(curX - 1, curY + 1));
		}
		if (curY < 103 && (blocks[curX][curY + 1] & 0x1280120) == 0) {
			tiles.add(new Node(curX, curY + 1));
		}
		if (curX > 0 && curY < 103
				&& (blocks[curX - 1][curY + 1] & 0x1280138) == 0
				&& (blocks[curX - 1][curY] & 0x1280108) == 0
				&& (blocks[curX][curY + 1] & 0x1280120) == 0) {
			tiles.add(new Node(curX + 1, curY + 1));
		}
		if (curX > 0 && (blocks[curX - 1][curY] & 0x1280108) == 0) {
			tiles.add(new Node(curX - 1, curY));
		}
		if (curX < 103 && (blocks[curX + 1][curY] & 0x1280180) == 0) {
			tiles.add(new Node(curX + 1, curY));
		}
		if (curX > 0 && curY > 0
				&& (blocks[curX - 1][curY - 1] & 0x128010e) == 0
				&& (blocks[curX - 1][curY] & 0x1280108) == 0
				&& (blocks[curX][curY - 1] & 0x1280102) == 0) {
			tiles.add(new Node(curX - 1, curY - 1));
		}
		if (curY > 0 && (blocks[curX][curY - 1] & 0x1280102) == 0) {
			tiles.add(new Node(curX, curY - 1));
		}
		if (curX < 103 && curY > 0
				&& (blocks[curX + 1][curY - 1] & 0x1280183) == 0
				&& (blocks[curX + 1][curY] & 0x1280180) == 0
				&& (blocks[curX][curY - 1] & 0x1280102) == 0) {
			tiles.add(new Node(curX + 1, curY - 1));
		}
		return tiles;
	}

	private boolean isIn(final ArrayList<Node> nodes, final Node key) {
		return getNode(nodes, key) != null;
	}
}

class FMAntiBanThread extends Thread {

	private final Methods methods = new Methods();

	@Override
	public void run() {
		final int camAlt = Bot.getClient().getCamPosZ();
		char LR = KeyEvent.VK_LEFT;
		char UD;
		if (camAlt > -1600) {
			UD = KeyEvent.VK_UP;
		} else if (camAlt < -2215 || methods.random(0, 2) == 0) {
			UD = KeyEvent.VK_DOWN;
		} else {
			UD = KeyEvent.VK_UP;
		}
		if (methods.random(0, 2) == 0) {
			LR = KeyEvent.VK_RIGHT;
		}

		Bot.getInputManager().pressKey(LR);
		try {
			Thread.sleep(methods.random(50, 400));
		} catch (final Exception ignored) {
		}
		Bot.getInputManager().pressKey(UD);
		try {
			Thread.sleep(methods.random(300, 700));
		} catch (final Exception ignored) {
		}
		Bot.getInputManager().releaseKey(UD);
		try {
			Thread.sleep(methods.random(100, 400));
		} catch (final Exception ignored) {
		}
		Bot.getInputManager().releaseKey(LR);
	}

}

class FMFrame extends JFrame {

	/**
	 * AIO Firemaker Swing GUI Class
	 */
	private static final long serialVersionUID = -6177554547983230084L;
	private int currentLogs;
	private String currentLogName;
	private final JComboBox logsBox;
	private final JComboBox locationsBox;
	private FMLocation currentLocation;
	private final HashMap<Integer, Double> XPMap;
	private final HashMap<String, Integer> logMap;
	private final HashMap<String, FMLocation> locationMap;

	public final FMLocation[] LOCATIONS;
	public final File SETTINGS_FILE = new File(new File(
			GlobalConfiguration.Paths.getSettingsDirectory()), "AIOFM.txt");

	public FMFrame(final String title) {
		super(title);

		/*
		 * LOCATIONS ARRAY
		 * 
		 * FMLocation("Name", FMZone[], (RSTile)bankLocation, (int)randomness)
		 * FMZone(FMRow[], (boolean)horizontal) FMRow((int)startpos,
		 * (int)endpos, (int)otheraxispos)
		 * 
		 * If a zone is horizontal, starpos and endpos for all rows will refer
		 * to the x axis and otheraxispos will refer to the y position. As far
		 * as I have seen all firemaking runs from east to west, so horizontal =
		 * true && endpos < startpos The randomness for each location determins
		 * how it will walk to the bank tile. If -1 is specified, it will walk
		 * directly to the found bank if possible, otherwise the script will
		 * always walk to bankLocation, with the randomness specified. 0 = no
		 * randomness, 1 = one tile randomness etc.
		 */
		LOCATIONS = new FMLocation[] {

				new FMLocation("Grand Exchange", new FMZone[] {
						new FMZone(new FMRow[] { new FMRow(3172, 3157, 3484),
								new FMRow(3178, 3151, 3483),
								new FMRow(3178, 3151, 3482),
								new FMRow(3169, 3161, 3481) }, true),
						new FMZone(new FMRow[] { new FMRow(3173, 3156, 3494),
								new FMRow(3172, 3157, 3495),
								new FMRow(3178, 3151, 3496),
								new FMRow(3178, 3151, 3497),
								new FMRow(3168, 3161, 3498) }, true) },
						new RSTile(3162, 3490), 1),

				new FMLocation("Draynor Village", new FMZone[] { new FMZone(
						new FMRow[] { new FMRow(3097, 3077, 3249),
								new FMRow(3098, 3072, 3248),
								new FMRow(3095, 3081, 3247) }, true) },
						new RSTile(3093, 3244), 0),

				new FMLocation("Fist of Guthix", new FMZone[] { new FMZone(
						new FMRow[] { new FMRow(1717, 1676, 5601),
								new FMRow(1718, 1676, 5600),
								new FMRow(1718, 1676, 5599),
								new FMRow(1718, 1676, 5598),
								new FMRow(1718, 1676, 5597) }, true) },
						new RSTile(1703, 5599), -1),

				new FMLocation("Varrock West", new FMZone[] { new FMZone(
						new FMRow[] { new FMRow(3199, 3175, 3431),
								new FMRow(3199, 3168, 3430),
								new FMRow(3199, 3168, 3429),
								new FMRow(3199, 3168, 3428) }, true) },
						new RSTile(3183, 3435), 0),

				new FMLocation("Varrock East", new FMZone[] { new FMZone(
						new FMRow[] { new FMRow(3265, 3241, 3429),
								new FMRow(3265, 3241, 3428),
								new FMRow(3257, 3255, 3427),
								new FMRow(3252, 3250, 3427) }, true) },
						new RSTile(3253, 3421), 0),

				new FMLocation("Falador East", new FMZone[] { new FMZone(
						new FMRow[] { new FMRow(3032, 3005, 3359),
								new FMRow(3032, 3005, 3360),
								new FMRow(3032, 3001, 3361),
								new FMRow(3032, 3001, 3362),
								new FMRow(3032, 3001, 3363) }, true) },
						new RSTile(3012, 3356), 0),

				new FMLocation("Yanille", new FMZone[] { new FMZone(
						new FMRow[] { new FMRow(2606, 2577, 3099),
								new FMRow(2606, 2577, 3098),
								new FMRow(2606, 2578, 3097) }, true) },
						new RSTile(2612, 3092), 0)

		};

		currentLocation = null;
		logsBox = new JComboBox();
		locationsBox = new JComboBox();
		XPMap = new HashMap<Integer, Double>();
		logMap = new HashMap<String, Integer>();
		locationMap = new HashMap<String, FMLocation>();
		setupFrame();
		setVisible(true);
	}

	public FMLocation getSelectedLocation() {
		return currentLocation;
	}

	public double[] getSelectedLogInfo() {
		return new double[] { currentLogs, XPMap.get(currentLogs) };
	}

	public String getSelectedLogName() {
		return currentLogName;
	}

	private void setupFrame() {

		// FRAME

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setResizable(false);
		setAlwaysOnTop(true);
		setLocationRelativeTo(null);
		setSize(200, 120);

		// START BUTTON

		final JButton startButton = new JButton("Start");
		add(startButton, BorderLayout.SOUTH);
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent evt) {

				currentLocation = locationMap.get(locationsBox
						.getSelectedItem().toString());
				currentLogs = logMap.get(logsBox.getSelectedItem().toString());
				currentLogName = logsBox.getSelectedItem().toString();

				// WRITE TO SETTINGS FILE

				try {
					final BufferedWriter out = new BufferedWriter(
							new FileWriter(SETTINGS_FILE));
					out.write(currentLogName + ":"
							+ locationsBox.getSelectedItem().toString());
					out.close();
				} catch (final Exception ignored) {
				}

				// DISPOSE

				setVisible(false);
				dispose();
			}
		});

		// COMBO BOXES

		final String[] locations = new String[LOCATIONS.length];

		/*
		 * LOG ARRAYS
		 * 
		 * The following three arrays must be the same length, with
		 * <code>logs</code> referring to the log name, <code>logIDs</code>
		 * referring to the log ID, and <code>logXPs</code> referring to the
		 * firemaking XP gained each time a log of that kind is burned.
		 * 
		 * Each string in (String[] logs) must be the correct name of the item
		 * specified in logIDs or the script will be unable to withdraw the logs
		 * from the bank.
		 */
		final String[] logs = { "Logs", "Oak logs", "Willow logs",
				"Maple logs", "Yew logs", "Magic logs" };
		final int[] logIDs = { 1511, 1521, 1519, 1517, 1515, 1513 };
		final double[] logXPs = { 40.0, 60.0, 90.0, 135.0, 202.5, 303.8 };

		for (int i = 0; i < logs.length; i++) {
			logMap.put(logs[i], logIDs[i]);
			XPMap.put(logIDs[i], logXPs[i]);
		}

		for (int i = 0; i < locations.length; i++) {
			locations[i] = LOCATIONS[i].name;
			locationMap.put(locations[i], LOCATIONS[i]);
		}

		locationsBox.setModel(new DefaultComboBoxModel(locations));
		add(locationsBox, BorderLayout.CENTER);

		logsBox.setModel(new DefaultComboBoxModel(logs));
		add(logsBox, BorderLayout.NORTH);

		// LOAD SAVED SELECTIONS FROM SETTINGS FILE

		try {

			final BufferedReader in = new BufferedReader(new FileReader(
					SETTINGS_FILE));
			String line;
			String[] opts = {};

			while ((line = in.readLine()) != null) {
				if (line.contains(":")) {
					opts = line.split(":");
				}
			}
			in.close();
			if (opts.length == 2) {
				logsBox.setSelectedItem(opts[0]);
				locationsBox.setSelectedItem(opts[1]);
			}
		} catch (final IOException ignored) {
		}

	}

}

class FMLocation {
	public String name;
	public FMZone[] zones;
	public RSTile bank;
	public int randomness;

	public FMLocation(final String name, final FMZone[] zones,
			final RSTile bank, final int randomness) {
		this.name = name;
		this.zones = zones;
		this.bank = bank;
		this.randomness = randomness;
	}
}

class FMRow {
	public RSTile[] tiles;
	public int start, end, pos;

	public FMRow(final int start, final int end, final int pos) {
		this.start = start;
		this.end = end;
		this.pos = pos;
	}

	public void generateTiles(final boolean horizontal) {
		final int length = Math.abs(end - start) + 1;
		tiles = new RSTile[length];
		if (end > start) {
			if (horizontal) {
				for (int i = 0; i < length; i++) {
					tiles[i] = new RSTile(start + i, pos);
				}
			} else {
				for (int i = 0; i < length; i++) {
					tiles[i] = new RSTile(pos, start + i);
				}
			}
		} else {
			if (horizontal) {
				for (int i = 0; i < length; i++) {
					tiles[i] = new RSTile(start - i, pos);
				}
			} else {
				for (int i = 0; i < length; i++) {
					tiles[i] = new RSTile(pos, start - i);
				}
			}
		}
	}

}

class FMZone {
	public FMRow[] rows;
	public boolean horizontal;

	public FMZone(final FMRow[] fmRows, final boolean horizontal) {
		rows = fmRows;
		this.horizontal = horizontal;
		for (final FMRow r : rows) {
			r.generateTiles(horizontal);
		}
	}

}