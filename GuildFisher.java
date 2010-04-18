import org.rsbot.bot.Bot;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.*;
import org.rsbot.script.wrappers.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.Map;

@ScriptManifest(authors = {"Jacmob"}, name = "Guild Fisher", category = "Fishing", version = 1.0,
	description = "<html><body style='font-family: Arial;'>Fishes and banks lobsters at the Fishing Guild.<br /><small>Jacmob</small></body></html>")
public class GuildFisher extends Script implements PaintListener {

	public static final int LOBSTER_POT = 301;
	public static final int RAW_LOBSTER = 377;
	public static final int NPC_CAGE = 312;
	public static final RSTile TILE_BANK = new RSTile(2586, 3422);
	public static final RSTile TILE_BANK_ENTRANCE = new RSTile(2589, 3422);
	public static final RSTile TILE_BANK_BOOTH = new RSTile(2584, 3422);
	public static final RSTile TILE_PORT = new RSTile(2599, 3422);

	private int counter;
	private volatile RSTile last;

    private enum State {
        BANK, FISH, WALK_TO_BANK, WALK_TO_FISH, ANTI_BAN, QUIT
    }

    public int loop() {
		switch (getState()) {
			case BANK:
				if (bank.isOpen()) {
					if (inventoryContains(RAW_LOBSTER)) {
						bank.deposit(RAW_LOBSTER, 0);
					} else {
						bank.close();
					}
				} else {
					if (!atTile(TILE_BANK_BOOTH, "Use-quickly")
							&& !getMyPlayer().isMoving()) {
						if (distanceTo(TILE_BANK) > 2) {
							walkTileMM(TILE_BANK);
						} else {
							turnToTile(TILE_BANK_BOOTH);
						}
					}
				}
				break;
			case FISH:
				RSNPC spot = getNearestNPCByID(NPC_CAGE);
				if (spot != null) {
					RSTile loc = spot.getLocation();
					if (!loc.equals(last) || counter <= 0) {
						if (atTile(loc, "Cage")) {
							counter = 5;
							last = loc;
							wait(2000);
						} else {
							char key = KeyEvent.VK_LEFT;
							Bot.getInputManager().pressKey(key);
							int i = 60;
							while (!tileOnScreen(loc) && --i >= 0) {
								wait(50);
							}
							wait(random(150, 300));
							Bot.getInputManager().releaseKey(key);
							if (i >= 0 && !tileOnScreen(loc)) {
								key = KeyEvent.VK_LEFT;
								i = 20;
								while (!tileOnScreen(loc) && --i >= 0) {
									wait(50);
								}
								Bot.getInputManager().releaseKey(key);
							}
							if (!tileOnScreen(loc)) {
								atTile(getTileOnScreen(loc), "alk");
							}
						}
					} else if (getMyPlayer().getAnimation() == -1) {
						--counter;
					} else {
						counter = 5;
					}
				}
				break;
			case WALK_TO_BANK:
				if (distanceTo(TILE_BANK) > 10) {
					walkTowards(TILE_BANK_ENTRANCE);
				} else if (tileOnScreen(TILE_BANK)) {
					atTile(TILE_BANK, "alk");
				} else if (!getMyPlayer().isMoving()) {
					walkTileMM(TILE_BANK);
				}
				break;
			case WALK_TO_FISH:
				walkTowards(TILE_PORT);
				break;
			case ANTI_BAN:
				switch (random(0, 10)) {
					case 0:
					case 1:
					case 2:
						waveMouse();
						break;
					case 3:
					case 4:
						moveMouseAway(30);
						break;
					case 5:
						rotateCamera();
						break;
					case 6:
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
						break;
					case 7:
						if (getCurrentTab() != Constants.TAB_STATS) {
							openTab(Constants.TAB_STATS);
						}
						break;
					case 8:
					case 9:
						if (getCurrentTab() != Constants.TAB_INVENTORY) {
							openTab(Constants.TAB_INVENTORY);
						}
						break;
				}
				break;
		}
        return random(300, 600);
    }

    private State getState() {
		if (inventoryContains(LOBSTER_POT)) {
			if (getInventoryCount(false) == 28) {
				if (distanceTo(TILE_BANK) > 3) {
					return State.WALK_TO_BANK;
				} else {
					return State.BANK;
				}
			} else {
				if (distanceTo(TILE_PORT) > 5) {
					return State.WALK_TO_FISH;
				} else if (random(0, 100) == 0) {
					return State.ANTI_BAN;
				} else {
					return State.FISH;
				}
			}
		}
		return State.QUIT;
	}

	public RSNPC getNearestNPCByID(int... ids) {
		int dist = 9001; // yes, this is over 9000.
		RSNPC current = null;
		for (RSNPC npc : getNPCArray(false)) {
			for (int id : ids) {
				if (npc.getID() == id) {
					int d = distanceTo(npc.getLocation());
					if (d < dist && npc.getLocation().getY() >= 3419) {
						dist = d;
						current = npc;
					}
					break;
				}
			}
		}
		return current;
	}

	private void walkTowards(final RSTile tile) {
		RSTile next = checkTile(tile);
		RSTile dest = getDestination();
		if (!(getMyPlayer().isMoving() && dest != null &&
				(Methods.distanceBetween(next, dest) <= 2 ||
				Methods.distanceBetween(tile, dest) <= 2))) {
			walkTileMM(next);
		}
	}

    private RSTile checkTile(final RSTile tile) {
        if (distanceTo(tile) < 15) {
            return tile;
        }
        final RSTile loc = getMyPlayer().getLocation();
        final RSTile walk = new RSTile((loc.getX() + tile.getX()) / 2, (loc
                .getY() + tile.getY()) / 2);
        return distanceTo(walk) < 17 ? walk : checkTile(walk);
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

    public boolean onStart(Map<String, String> args) {
        return true;
    }

    public void onRepaint(Graphics g) {
		if (last != null) {
			highlightTile(g, last, new Color(200, 200, 255), new Color(150, 150, 255, 70));
		}
    }

	private void waveMouse() {
		final Point curPos = getMouseLocation();
		moveMouse(random(0, 750), random(0, 500), 20);
		wait(random(100, 300));
		moveMouse(curPos, 20, 20);
	}

	private void moveMouseAway(final int moveDist) {
		final Point pos = getMouseLocation();
		moveMouse(pos.x - moveDist, pos.y - moveDist, moveDist * 2,
				moveDist * 2);
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

}