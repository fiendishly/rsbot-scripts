/*
 * Made by Wuppiet
 * Credits to Dharok and Marneus901 for using some methots from there scripts
 *
 * Version history:
 * 	Beta's: Working but not very stable.
 *	V1: Updated it to work with the RuneTek 5 update.
 *	V1.1 Added better target choosing + an other failsafe for attacking dummys that are already beeing attacked.
 *	V1.11 Now works without disabling Randoms!!
 *	V1.12 Fixed for the 573.2 update
 *	V1.13 Added version checking and changed some other stuff
 *  V1.14 changed something on walking
 *  V1.15 Added mouse speed + hopefully fixed random stopping
 *  V1.16 Deleted something that caused random stopping
 *  V1.17 Fixed an other bug
 *  V1.18 Now wont miss click that much anymore :)
 *  V1.19 More random + better methot for dummys
 */
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Map;

import org.rsbot.accessors.Node;
import org.rsbot.accessors.RSNPCNode;
import org.rsbot.bot.Bot;
import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Constants;
import org.rsbot.script.Random;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = { "Wuppiet" }, category = "Magic", name = "WupDummyCurser", version = 1.19, description = "<html><body bgcolor = Black><font color = White><center><h2> WupDummyCurser V1.19 by Wuppiet</h2>"
		+ "<tr><td><b>Spell : </b></td><td><center><select name=\"spell\">"
		+ "<option>Confuse"
		+ "<option>Weaken"
		+ "<option>Curse"
		+ "<option>Vulnerability"
		+ "<option>Enfeeble"
		+ "<option>Stun"
		+ "</select></center></td></tr>"
		+ "<tr><td><b>Use antiban </b></td><td><center>"
		+ "<input type=\"checkbox\" name=\"antiban\" value=\"true\"></font><BR />"
		+ "</center></td></tr>"
		+ "<tr><td><b>Check magic lvl after leveled up </b></td><td><center>"
		+ "<input type=\"checkbox\" name=\"checklvl\" value=\"true\"></font><BR />"
		+ "</center></td></tr>"
		+ "<tr><td><b>Mouse speed: </b><tr><td><center>"
		+ "<input type=\"text\" name=\"amount\" value=\"9\"></center><br>"
		+ "Made by Wuppiet<BR /><BR />"
		+ "<font size = \"3\">Note: Set your filter on Combat only for better using </font>"
		+ "<font size = \"3\">Note2: this will check for updates </font>")
public class WupDummyCurser extends Script implements ServerMessageListener,
		PaintListener {
	final ScriptManifest properties = getClass().getAnnotation(
			ScriptManifest.class);
	public int randomcast1 = random(0, 20) + 50;
	public int Casts = 0;
	public int antibanCasts = 0;
	public int Spell;
	public int speed;
	public String SpellUsing;
	public int startXP = skills.getCurrentSkillExp(STAT_MAGIC);
	public int castxp = 0;
	public int gainedXP = 0;
	public int startLevel = skills.getCurrentSkillLevel(STAT_MAGIC);
	public long startTime;
	public String spellChosen;
	public String useantiban;
	public String Check;
	private boolean Useantiban = false;
	private boolean checkMlvl = false;
	public boolean b_spellChosen = false;
	public boolean move = false;
	private int startxp;
	public int startlvl = 0;
	public int fail = 0;
	private boolean checklvl = false;
	Color BG = new Color(0, 0, 0, 100);

	@Override
	public int getMouseSpeed() {
		return speed + random(-1, 1);
	}

	public void onRepaint(Graphics g) {
		if (isLoggedIn()) {
			long millis = System.currentTimeMillis() - startTime;
			long hours = millis / (1000 * 60 * 60);
			millis -= hours * (1000 * 60 * 60);
			long minutes = millis / (1000 * 60);
			millis -= minutes * (1000 * 60);
			long seconds = millis / 1000;
			int LevelChange = skills.getCurrentSkillLevel(STAT_MAGIC)
					- startlvl;
			int XPChange = skills.getCurrentSkillExp(STAT_MAGIC) - startxp;
			float XPperSec = 0;
			if ((minutes > 0 || hours > 0 || seconds > 0) && XPChange > 0) {
				XPperSec = ((float) XPChange)
						/ (float) (seconds + (minutes * 60) + (hours * 60 * 60));
			}

			float XPperMin = XPperSec * 60;
			float XPperHour = XPperMin * 60;
			float timeTillLvl = 9999;
			float secsTillLvl = (int) timeTillLvl;
			float minsTillLvl = (int) timeTillLvl;
			float hoursTillLvl = (int) timeTillLvl;

			if (XPperSec > 0) {
				secsTillLvl = skills.getXPToNextLevel(STAT_MAGIC) / XPperSec;
			}
			if (XPperMin > 0) {
				minsTillLvl = skills.getXPToNextLevel(STAT_MAGIC) / XPperMin;
			}
			if (XPperHour > 0) {
				hoursTillLvl = skills.getXPToNextLevel(STAT_MAGIC) / XPperHour;
			}
			secsTillLvl -= (int) minsTillLvl * 60;
			minsTillLvl -= (int) hoursTillLvl * 60;

			if (minsTillLvl < 0)
				minsTillLvl = 0;
			g.setColor(BG);
			g.fill3DRect(12, 222, 184, 111, true);
			g.setColor(Color.white);
			g.drawString("Runtime: " + hours + "h " + minutes + "m " + seconds
					+ "s.", 15, 234);
			g.drawString("Spell casting: " + SpellUsing, 15, 246);
			g.drawString("Casts: " + Casts, 15, 258);
			g.drawString("Magic levels gained: " + LevelChange + " ("
					+ skills.getCurrentSkillLevel(STAT_MAGIC) + ")", 15, 270);
			g.drawString("Magic EXP gained: "
					+ (skills.getCurrentSkillExp(STAT_MAGIC) - startxp), 15,
					282);
			g.drawString("EXP per hour: " + (int) XPperHour, 15, 294);
			g.drawString("Casts per hour: " + ((int) XPperHour / castxp), 15,
					306);
			g.drawString("XP until level: "
					+ skills.getXPToNextLevel(STAT_MAGIC), 15, 318);
			g.drawString(skills.getPercentToNextLevel(Constants.STAT_MAGIC)
					+ "%", 15, 330);

			int barsize = 100;
			int barheight = 8;
			int percentage = skills.getPercentToNextLevel(Constants.STAT_MAGIC);
			int transparancy = 160;
			int xBar = 45;
			int yBar = 321;
			g.setColor(new Color(255, 0, 0, transparancy));
			g.fillRect(xBar, yBar, barsize, barheight);
			g.setColor(new Color(0, 255, 0, transparancy));
			g.fillRect(xBar, yBar, barsize / 100 * percentage, barheight);
			g.setColor(new Color(255, 255, 255, transparancy));
			g.drawRect(xBar, yBar, barsize, barheight);

		}
	}

	public void serverMessageRecieved(ServerMessageEvent e) {
		if (e.getMessage().contains("You do not have enough")) {
			fail++;
		}
		if (e.getMessage().contains("Magic level! You have")) {
			checklvl = true;
		}
		if (e.getMessage().contains("Someone else is fighting that")) {
			move = true;
		}
	}

	/*
	 * Credits to Bool for this :)
	 */
	public void stopRandom(String name) {
		ArrayList<Random> randoms = (ArrayList<Random>) Bot.getScriptHandler()
				.getRandoms();
		for (Random r : randoms) {
			if (r.getClass().getAnnotation(ScriptManifest.class).name()
					.contains(name)) {
				r.isUsed = false;
				break;
			}
		}
	}

	public void startRandom(String name) {
		ArrayList<Random> randoms = (ArrayList<Random>) Bot.getScriptHandler()
				.getRandoms();
		for (Random r : randoms) {
			if (r.getClass().getAnnotation(ScriptManifest.class).name()
					.contains(name)) {
				r.isUsed = true;
				break;
			}
		}
	}

	public boolean doingSpell() {
		if (getMyPlayer().getAnimation() == 1164) {
			wait(random(150, 200));
			return true;
		}
		return false;
	}

	public boolean atSpellInterface(int spell) {
		RSInterfaceChild i = RSInterface.getChildInterface(192, spell);
		if (!i.isValid()) {
			return false;
		}
		Rectangle pos = i.getArea();
		if (pos.x == -1 || pos.y == -1 || pos.width == -1 || pos.height == -1) {
			return false;
		}
		moveMouse((int) random(pos.getMinX(), pos.getMaxX()), (int) random(pos
				.getMinY(), pos.getMaxY()));
		clickMouse(true);
		return true;
	}

	public void Cast(int Spell) {
		while (getCurrentTab() != TAB_MAGIC) {
			openTab(TAB_MAGIC);
			wait(random(50, 50));
		}
		while (!atSpellInterface(Spell)) {
			b_spellChosen = true;
		}
	}

	public RSNPC wupGetNearestFreeNPCByID(final int... ids) {
		int Dist = 20;
		RSNPC closest = null;
		final int[] validNPCs = Bot.getClient().getRSNPCIndexArray();

        for (final int element : validNPCs) {
        	Node node = Calculations.findNodeByID(Bot.getClient().getRSNPCNC(), element);
            if (node == null || !(node instanceof RSNPCNode)) {
                continue;
            }
            final RSNPC Monster = new RSNPC(((RSNPCNode) node).getRSNPC());
			try {
				for (final int id : ids) {
					if (id != Monster.getID() || Monster.isInCombat()) {
						continue;
					}
					int distancex = getMyPlayer().getLocation().getX()
							- Monster.getLocation().getX();
					if (distancex < 0) {
						distancex = Monster.getLocation().getX()
								- getMyPlayer().getLocation().getX();
					}
					int distancey = getMyPlayer().getLocation().getY()
							- Monster.getLocation().getY();
					if (distancey < 0) {
						distancey = Monster.getLocation().getY()
								- getMyPlayer().getLocation().getY();
					}
					final int distance = distancex + distancey;
					if (distance < Dist) {
						Dist = distance;
						closest = Monster;
					}
				}
			} catch (final Exception ignored) {
			}
		}
		return closest;
	}

	public void attackNPC() {
		if (!b_spellChosen) {
			Cast(Spell);
		}

		RSNPC Dummy = wupGetNearestFreeNPCByID(4474);
		if (Dummy != null) {
			atNPC(Dummy, "Cast " + spellChosen + " -> Magic dummy");
		}
		wait(random(200, 300));

		if ((skills.getCurrentSkillExp(STAT_MAGIC) - startXP) > gainedXP) {
			fail = 0;
			Casts++;
			antibanCasts++;
			gainedXP = (skills.getCurrentSkillExp(STAT_MAGIC) - startXP);
			b_spellChosen = false;
			wait(random(200, 300));
		}
	}

	public boolean walk_other_tile() {
		wait(random(100, 1000));
		RSTile random = new RSTile((3213 + random(-1, 1)),
				(3254 - random(0, 2)));
		if (tileOnScreen(random)) {
			return walkTileOnScreen(random);
		} else if (tileOnMap(random)) {
			return walkTileMM(random);
		} else {
			return walkTo(random);
		}
	}

	@Override
	public boolean onStart(Map<String, String> args) {
		URLConnection url;
		BufferedReader in;
		try {
			url = new URL(
					"http://www.wuppiet.webs.com/scripts/WupDummyCurserVERSION.txt")
					.openConnection();
			in = new BufferedReader(new InputStreamReader(url.getInputStream()));
			if (Double.parseDouble(in.readLine()) > properties.version()) {
				log("Update found. Please get new version");
				log("http://www.rsbot.org/vb/showthread.php?t=66220");
			} else
				log("You have the latest version.");
		} catch (IOException e) {
			log("Problem getting version.");
		}

		stopRandom("eward");
		spellChosen = args.get("spell");
		useantiban = args.get("antiban");
		Check = args.get("checklvl");
		speed = Integer.parseInt(args.get("amount"));
		if (spellChosen.equals("Confuse")) {
			Spell = 26;
			castxp = 13;
			SpellUsing = "Confuse";
		} else if (spellChosen.equals("Weaken")) {
			Spell = 31;
			castxp = 21;
			SpellUsing = "Weaken";
		} else if (spellChosen.equals("Curse")) {
			Spell = 35;
			castxp = 29;
			SpellUsing = "Curse";
		} else if (spellChosen.equals("Vulnerability")) {
			Spell = 75;
			castxp = 76;
			SpellUsing = "Vulnerability";
		} else if (spellChosen.equals("Enfeeble")) {
			Spell = 78;
			castxp = 83;
			SpellUsing = "Enfeeble";
		} else if (spellChosen.equals("Stun")) {
			Spell = 82;
			castxp = 90;
			SpellUsing = "Stun";
		} else {
			return false;
		}

		if (useantiban != null) {
			Useantiban = true;
		}

		if (Check != null) {
			checkMlvl = true;
		}
		while (!isLoggedIn()) {
			login();
		}

		startxp = skills.getCurrentSkillExp(STAT_MAGIC);
		startlvl = skills.getCurrentSkillLevel(STAT_MAGIC);
		startTime = System.currentTimeMillis();
		setCameraAltitude(true);
		return true;
	}

	@Override
	public void onFinish() {
		startRandom("eward");
	}

	public boolean isInArea(int minX, int minY, int maxX, int maxY) {
		int x = getMyPlayer().getLocation().getX();
		int y = getMyPlayer().getLocation().getY();
		return x >= minX && x <= maxX && y >= minY && y <= maxY;
	}

	public void antiBan() {
		int randomNum = random(1, 11);
		if (antibanCasts == randomcast1 && Useantiban) {
			switch (randomNum) {
			case 1:
				setCameraAltitude(true);
				int newCamera = getCameraAngle() + random(0, 270);
				if (newCamera >= 360) {
					newCamera -= 360;
				}
				setCameraRotation(newCamera);
				randomcast1 = random(0, 20) + random(10, 25);
				antibanCasts = 0;
				return;
			case 2:
				int i = random(0, 14);
				if (getCurrentTab() != i) {
					openTab(i);
				}
				randomcast1 = random(0, 20) + random(40, 60);
				antibanCasts = 0;
				return;
			case 3:
				int x1 = random(250, 750);
				int y1 = random(222, 500);
				moveMouse(0, 0, x1, y1);
				randomcast1 = random(0, 20) + random(10, 20);
				antibanCasts = 0;
				return;
			case 4:
				if (getCurrentTab() != TAB_STATS) {
					openTab(TAB_STATS);
					moveMouse(random(558, 600), random(393, 408));
					wait(2527);
					openTab(TAB_MAGIC);

				}
				randomcast1 = random(0, 20) + random(50, 100);
				antibanCasts = 0;
				return;
			case 5:
				int x2 = random(0, 250);
				int y2 = random(0, 250);
				moveMouse(0, 0, x2, y2);
				randomcast1 = random(0, 20) + random(10, 20);
				antibanCasts = 0;
				return;
			case 6:
				if (getCurrentTab() != TAB_INVENTORY) {
					openTab(TAB_INVENTORY);
					moveMouse(random(560, 725), random(225, 450));
					wait(random(1000, 2500));
				}
				randomcast1 = random(0, 20) + random(40, 60);
				antibanCasts = 0;
				return;
			case 7:
				int angle = getCameraAngle() + random(-90, 90);
				if (angle < 0) {
					angle = 0;
				}
				if (angle > 359) {
					angle = 0;
				}
				setCameraRotation(angle);
				randomcast1 = random(0, 20) + random(30, 45);
				antibanCasts = 0;
				return;
			case 8:
				moveMouse(random(0, 762), random(0, 502));
				randomcast1 = random(0, 20) + random(10, 20);
				antibanCasts = 0;
				return;
			case 9:
				moveMouse(random(0, 762), random(0, 502));
				randomcast1 = random(0, 20) + random(10, 20);
				antibanCasts = 0;
				return;
			case 10:
				setCameraAltitude(true);
				randomcast1 = random(0, 20) + random(10, 20);
				antibanCasts = 0;
				return;
			case 11:
				setCameraAltitude(false);
				wait(random(50, 500));
				setCameraAltitude(true);
				randomcast1 = random(0, 20) + random(25, 35);
				antibanCasts = 0;
				return;
			}
			antibanCasts = 0;
		}
	}

	public int loop() {
		try {
			if (fail > 3) {
				log("Not enough runes");
				stopScript();
			}
			if (checklvl && checkMlvl) {
				openTab(TAB_STATS);
				moveMouse(random(558, 600), random(393, 408));
				clickMouse(true);
				moveMouse(random(70, 400), random(40, 335));
				wait(random(4900, 6200));
				atInterface(741, 9);
				wait(random(50, 500));
				openTab(TAB_MAGIC);
				checklvl = false;
			}

			if (getMyPlayer().isMoving()) {
				return random(300, 400);
			}
			if (getMyPlayer().isInCombat() && getPlane() == 0) {
				if (atTile(new RSTile(3212, 3256), "Climb-up")) {
					return random(30000, 40000);
				}
				return random(300, 400);
			}

			if (getPlane() == 1) {
				atTile(new RSTile(3213, 3256), "Climb-down");
				return random(500, 600);
			}

			if (!isInArea(3209, 3250, 3214, 3256)) {
				RSTile t = new RSTile(3213, (3254 - random(0, 2)));
				if (tileOnScreen(t)) {
					walkTileOnScreen(t);
				}

				else if (tileOnMap(t)) {
					walkTileMM(t);
				} else {
					walkTo(t);
				}
				return random(600, 700);
			}
			if (move) {
				if (walk_other_tile()) {
					move = false;
					return random(1500, 2500);
				}
			}
			if (!doingSpell()) {
				attackNPC();
				antiBan();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return random(350, 400);
	}
}