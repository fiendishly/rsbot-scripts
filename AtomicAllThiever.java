import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.rsbot.bot.Bot;
import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSPlayer;

@ScriptManifest(authors = { "Atomic Sky" }, category = "Thieving", name = "All Thiever", version = 1.2, description = "<html><head><center><b>All Thiever 1.2 by Atomic Sky</b></center></head><body><div class='main'<div class='leftcol'> NPC ID(s):</div><div class='rightcol'> <input type='text' name='npcid1' value='' size=10><input type='text' name='npcid2' value='' size=10><input type='text' name='npcid3' value='' size=10></div><div class='leftcol'>Food ID(s):</div><div class='rightcol'><input type='text' name='foodid1' value='' size=10><input type='text' name='foodid2' value='' size=10><input type='text' name='foodid3' value='' size=10> <input type='checkbox' name='doeat' value='true'><B>Eat food? </b></div><div class='leftcol'> Eat HP:</div><div class='rightcol'><input type='text' name='eathp' value='' size=10><input type='checkbox' name='dostop' value='true'><B>Stop when out of food? </b></div><div class='rightcol'><input type='checkbox' name='dodebug' value='true'><B>Debug? </b></div></div></center></body></html>")
public class AtomicAllThiever extends Script implements ServerMessageListener,
		PaintListener {
	boolean caught = false;
	boolean debug;
	boolean doeat;
	boolean dostop;
	int eathp;
	int exp;
	int foodid1;
	int foodid2;
	int foodid3;
	int loops = 0;
	int loops2 = 0;
	int npc1;
	int npc2;
	int npc3;
	int picked;
	boolean picking = false;
	public long startTime = System.currentTimeMillis();
	int task;

	public void antiBan() {

		switch (random(0, 50)) {
		case 0:
			task = 4;
			moveMouse(random(0, 400), random(0, 400));
			loops++;
			break;
		case 1:
			task = 4;
			moveMouse(random(0, 400), random(0, 400));
			loops++;
			break;
		case 2:
			task = 4;
			moveMouse(random(0, 400), random(0, 400));
			loops++;
			break;
		case 3:
			task = 4;
			moveMouse(random(0, 400), random(0, 400));
			loops++;
			break;
		case 4:
			task = 4;
			setCameraRotation(random(1, 300));
			loops++;
			break;
		case 5:
			task = 4;
			setCameraRotation(random(1, 300));
			loops++;
			break;
		case 7:
			task = 4;
			setCameraAltitude(true);
			loops++;
			break;
		case 8:
			task = 4;
			openTab(Constants.TAB_INVENTORY);
			loops++;
			break;
		case 9:
			task = 4;
			openTab(Constants.TAB_STATS);
			loops++;
			break;
		case 10:
			task = 4;
			openTab(random(0, 12));
			loops++;
			break;
		}
	}

	private void checks() {

		if (doeat) {
			final int health = skills
					.getCurrentSkillLevel(Constants.STAT_HITPOINTS);

			if (health <= eathp) {

				if (getInventoryCount(foodid1) == 0
						&& getInventoryCount(foodid2) == 0
						&& getInventoryCount(foodid3) == 0) {
					debug("  -No food in inventory.");

					if (dostop) {
						debug("Out of food, stopping script.");
						stopScript();
					}
				}

				else {
					debug("Eating.");
					task = 3;
					if (getInventoryCount(foodid1) != 0) {
						atInventoryItem(foodid1, "Eat");
						loops++;
					} else if (getInventoryCount(foodid2) != 0) {
						atInventoryItem(foodid2, "Eat");
						loops++;
					} else if (getInventoryCount(foodid3) != 0) {
						atInventoryItem(foodid3, "Eat");
						loops++;
					}
					wait(random(800, 1200));
				}
			}
		}

		if (dostop) {

			if (getInventoryCount(foodid1) == 0
					&& getInventoryCount(foodid2) == 0
					&& getInventoryCount(foodid3) == 0) {
				debug("Out of food, stopping script.");
				stopScript();
			}
		}
	}

	private boolean clickNPC(final RSNPC npc, final String action) { // By
		// Ruskis,
		// edited
		// somewhat.
		try {
			final int hoverRand = random(8, 13);
			for (int i = 0; i < hoverRand; i++) {
				final Point screenLoc = npc.getScreenLocation();
				if (!pointOnScreen(screenLoc)) {
					return false;
				}

				moveMouse(screenLoc, 15, 15);

				final List<String> menuItems = getMenuItems();
				for (int a = 1; a < menuItems.size(); a++) {
					if (menuItems.get(a).toLowerCase().contains(
							npc.getName().toLowerCase())) {
						clickMouse(false);
						return atMenu(action);
					}
				}
			}

		} catch (final Exception e) {
			log.log(Level.WARNING, "clickNPC(RSNPC, String) error: ", e);
			return false;
		}
		return false;
	}

	private void debug(final String s) {
		if (debug) {
			log(s);
		}
	}

	@Override
	public int loop() {
		debug("Main loop.");
		checks();

		if (getEnergy() > 10) {
			setRun(true);
		}

		while (caught) {
			debug("Player caught...");
			task = 2;

			if (loops > 4) {
				caught = false;
				picking = false;
				loops = 0;
			}

			else {
				antiBan();
				loops++;
				wait(random(200, 600));
			}
		}

		while (!caught) {
			debug("Player not caught...");

			if (myPlayer().getAnimation() == 881) {
				picking = true;
			}

			else {

				if (!picking) {
					debug("Attempting to find NPC...");
					task = 1;
					final RSNPC npc1a = getNearestNPCByID(npc1);

					if (npc1a != null) {

						if (tileOnScreen(npc1a.getLocation())) {
							clickNPC(npc1a, "Pick");
							wait(random(800, 1200));
						}

						else {

							if (myPlayer().isIdle()) {
								walkTo(npc1a.getLocation());
								wait(random(800, 1200));
							}
						}
					}

					else {
						final RSNPC npc2a = getNearestNPCByID(npc2);

						if (npc2a != null) {

							if (tileOnScreen(npc2a.getLocation())) {
								clickNPC(npc2a, "Pick");
								wait(random(800, 1200));
							}

							else {

								if (myPlayer().isIdle()) {
									walkTo(npc2a.getLocation());
									wait(random(800, 1200));
								}
							}
						}

						else {
							final RSNPC npc3a = getNearestNPCByID(npc3);

							if (npc3a != null) {

								if (tileOnScreen(npc3a.getLocation())) {
									clickNPC(npc3a, "Pick");
									wait(random(800, 1200));
								}

								else {

									if (myPlayer().isIdle()) {
										walkTo(npc3a.getLocation());
										wait(random(800, 1200));
									}
								}
							}

							else {
								debug("NPC not found.");
								stopScript();
							}
						}
					}
				}

				else if (picking) {

					if (myPlayer().isIdle()) {
						picking = false;
					}
				}
			}
			wait(random(200, 400));
		}
		return random(250, 500);
	}

	private RSPlayer myPlayer() {
		return new RSPlayer(Bot.getClient().getMyRSPlayer());
	}

	@Override
	public void onFinish() {
		Bot.getEventManager().removeListener(ServerMessageListener.class, this);
		Bot.getEventManager().removeListener(PaintListener.class, this);
	}

	public void onRepaint(final Graphics g) {
		if (isLoggedIn()) {
			if (exp == 0) {
				exp = skills.getCurrentSkillExp(Constants.STAT_THIEVING);
			}
			final int xp = skills.getCurrentSkillExp(Constants.STAT_THIEVING);
			final int x = 388;
			final int y = 20;
			long millis = System.currentTimeMillis() - startTime;
			final long hours = millis / (1000 * 60 * 60);
			millis -= hours * 1000 * 60 * 60;
			final long minutes = millis / (1000 * 60);
			millis -= minutes * 1000 * 60;
			final long seconds = millis / 1000;

			g.setColor(Color.green);
			g.drawString("Runtime: " + hours + ":" + minutes + ":" + seconds
					+ "", x, y + 105);
			g.drawString("Picked: " + picked / 2, x, y + 120);
			g.drawString("Exp: " + (xp - exp), x, y + 135);
			g.setColor(Color.red);

			if (task == 1) {

				g.setColor(Color.green);

				g.draw3DRect(x, y, 60, 14, true); // Picking
				g.fill3DRect(x, y, 60, 14, true);

				g.setColor(Color.white);

				g.draw3DRect(x, y + 20, 60, 14, true); // Caught
				g.fill3DRect(x, y + 20, 60, 14, true);

				g.draw3DRect(x, y + 40, 60, 14, true); // Eating
				g.fill3DRect(x, y + 40, 60, 14, true);

				g.draw3DRect(x, y + 60, 60, 14, true); // Antiban
				g.fill3DRect(x, y + 60, 60, 14, true);

				g.setColor(Color.black);
				g.drawString("Picking", x + 10, y + 11);
				g.drawString("Caught", x + 10, y + 32);
				g.drawString("Eating", x + 10, y + 52);
				g.drawString("Antiban", x + 10, y + 72);
			}

			else if (task == 2) {

				g.setColor(Color.white);

				g.draw3DRect(x, y, 60, 14, true); // Picking
				g.fill3DRect(x, y, 60, 14, true);

				g.setColor(Color.green);

				g.draw3DRect(x, y + 20, 60, 14, true); // Caught
				g.fill3DRect(x, y + 20, 60, 14, true);

				g.setColor(Color.white);

				g.draw3DRect(x, y + 40, 60, 14, true); // Eating
				g.fill3DRect(x, y + 40, 60, 14, true);

				g.draw3DRect(x, y + 60, 60, 14, true); // Antiban
				g.fill3DRect(x, y + 60, 60, 14, true);

				g.setColor(Color.black);
				g.drawString("Picking", x + 10, y + 11);
				g.drawString("Caught", x + 10, y + 32);
				g.drawString("Eating", x + 10, y + 52);
				g.drawString("Antiban", x + 10, y + 72);

			}

			else if (task == 3) {

				g.setColor(Color.white);

				g.draw3DRect(x, y, 60, 14, true); // Picking
				g.fill3DRect(x, y, 60, 14, true);

				g.draw3DRect(x, y + 20, 60, 14, true); // Caught
				g.fill3DRect(x, y + 20, 60, 14, true);

				g.setColor(Color.green);

				g.draw3DRect(x, y + 40, 60, 14, true); // Eating
				g.fill3DRect(x, y + 40, 60, 14, true);

				g.setColor(Color.white);

				g.draw3DRect(x, y + 60, 60, 14, true); // Antiban
				g.fill3DRect(x, y + 60, 60, 14, true);

				g.setColor(Color.black);
				g.drawString("Picking", x + 10, y + 11);
				g.drawString("Caught", x + 10, y + 32);
				g.drawString("Eating", x + 10, y + 52);
				g.drawString("Antiban", x + 10, y + 72);

			}

			else if (task == 4) {

				g.setColor(Color.white);

				g.draw3DRect(x, y, 60, 14, true); // Picking
				g.fill3DRect(x, y, 60, 14, true);

				g.setColor(Color.green);

				g.draw3DRect(x, y + 20, 60, 14, true); // Caught
				g.fill3DRect(x, y + 20, 60, 14, true);

				g.setColor(Color.white);

				g.draw3DRect(x, y + 40, 60, 14, true); // Eating
				g.fill3DRect(x, y + 40, 60, 14, true);

				g.setColor(Color.yellow);

				g.draw3DRect(x, y + 60, 60, 14, true); // Antiban
				g.fill3DRect(x, y + 60, 60, 14, true);

				g.setColor(Color.black);
				g.drawString("Picking", x + 10, y + 11);
				g.drawString("Caught", x + 10, y + 32);
				g.drawString("Eating", x + 10, y + 52);
				g.drawString("Antiban", x + 10, y + 72);

			}
		}
	}

	@Override
	public boolean onStart(final Map<String, String> args) {
		doeat = args.get("doeat") != null ? true : false;
		dostop = args.get("dostop") != null ? true : false;
		debug = args.get("dodebug") != null ? true : false;
		if (!args.get("npcid1").equals("")) {
			npc1 = Integer.parseInt(args.get("npcid1"));
		}
		if (!args.get("npcid2").equals("")) {
			npc2 = Integer.parseInt(args.get("npcid2"));
		}
		if (!args.get("npcid3").equals("")) {
			npc3 = Integer.parseInt(args.get("npcid3"));
		}
		if (!args.get("eathp").equals("")) {
			eathp = Integer.parseInt(args.get("eathp"));
		}
		if (!args.get("foodid1").equals("")) {
			foodid1 = Integer.parseInt(args.get("foodid1"));
		}
		if (!args.get("foodid2").equals("")) {
			foodid2 = Integer.parseInt(args.get("foodid2"));
		}
		if (!args.get("foodid3").equals("")) {
			foodid3 = Integer.parseInt(args.get("foodid3"));
		}
		log(" [-------------------- Settings --------------------]");
		log(" [ NPC IDs   = " + npc1 + ", " + npc2 + ", " + npc3);
		log(" [ Food IDs  = " + foodid1 + ", " + foodid2 + ", " + foodid3);
		log(" [ Eat HP    = " + eathp);
		log(" [----------------- Script started! ----------------]");
		return true;
	}

	public void serverMessageRecieved(final ServerMessageEvent e) {

		if (e.getMessage().contains("space")) {
			debug("Not enough space in inventory.");
			stopScript();
		} else if (e.getMessage().contains("stunned")) {
			caught = true;
		} else if (e.getMessage().contains("You pick")) {
			picked++;
			picking = false;
			debug("Picked!");
		}
	}
}
