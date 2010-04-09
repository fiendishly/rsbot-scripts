// For some strange reason I had to add TehMedia's name onto my script. So there you go TehMedia.
// Also if you see LiteralKlined, tell him Frolt won as the DDos attack.

import java.awt.Color;
import java.awt.Graphics;
import java.util.Map;

import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.Skills;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = { "Frolt" }, category = "Fishing", name = "Frolt Fisher", version = 1.00, description = "<html><body><img src=\"http://i545.photobucket.com/albums/hh370/junkdump_2008/FroltFisher-1.png\" /></body></html>")
public class FroltFisher extends Script implements PaintListener,
		ServerMessageListener {

	final ScriptManifest properties = getClass().getAnnotation(
			ScriptManifest.class);

	public long startTime = System.currentTimeMillis();
	private int FishingSkillIndex1, FishingStartingXP;
	public int lobsterID = 321;
	public int BankBoothID = 2213;
	int swordfishcount = 0;
	int tunacount = 0;
	int[] FishingTools = { 311, 10129 };
	public int fishingAnimation = 618;
	public String status = "Starting script";

	RSTile[] bankToLobster = new RSTile[] { new RSTile(2809, 3441),
			new RSTile(2822, 3435), new RSTile(2837, 3432),
			new RSTile(2845, 3430), new RSTile(2854, 3424),
			new RSTile(2860, 3427) };

	RSTile[] lobsterToBank = new RSTile[] { new RSTile(2860, 3427),
			new RSTile(2854, 3424), new RSTile(2845, 3430),
			new RSTile(2837, 3432), new RSTile(2822, 3435),
			new RSTile(2809, 3441) };

	@Override
	public boolean onStart(final Map<String, String> args) {
		FishingSkillIndex1 = Skills.getStatIndex("fishing");
		FishingStartingXP = skills.getCurrentSkillExp(FishingSkillIndex1);
		log(".");
		log("..");
		log("...");
		log("....");
		log("Starting Frolt Fisher!");
		return true;

	}

	public void onRepaint(final Graphics g) {
		if (isLoggedIn()) {
			long millis = System.currentTimeMillis() - startTime;
			final long hours = millis / (1000 * 60 * 60);
			millis -= hours * 1000 * 60 * 60;
			final long minutes = millis / (1000 * 60);
			millis -= minutes * 1000 * 60;
			final long seconds = millis / 1000;

			if (getCurrentTab() == TAB_INVENTORY) {
				g.setColor(new Color(0, 0, 0, 175));
				g.fillRoundRect(555, 210, 175, 250, 10, 10);
				g.setColor(Color.white);
				g.drawString("Frolt Fisher:", 561, 225);
				g.drawString("Version: 1.0", 561, 235);

				g.drawString("Current Status:", 561, 255);
				g.drawString("" + status + ".", 561, 265);

				g.drawString("Current Fish Catchs:", 561, 285);
				g.drawString("Swordfish: " + swordfishcount, 561, 295);
				g.drawString("Tuna: " + tunacount, 561, 305);

				g.drawString("Experience/Level Log:", 561, 325);
				g.drawString("Current Level: "
						+ skills.getCurrentSkillLevel(STAT_FISHING), 561, 335);
				g
						.drawString(
								"XP Gained: "
										+ (skills
												.getCurrentSkillExp(FishingSkillIndex1) - FishingStartingXP),
								561, 345);
				g.drawString("Percent Till Next Level: "
						+ skills.getPercentToNextLevel(STAT_FISHING) + "%",
						561, 355);
				g.drawString("XP Untill Level: "
						+ skills.getXPToNextLevel(STAT_FISHING), 561, 365);

				g.drawString("Time Running:", 561, 385);
				g.drawString("" + hours + ":" + minutes + ":" + seconds + "",
						561, 395);
			}
		}

	}

	public void serverMessageRecieved(final ServerMessageEvent e) {
		final String word = e.getMessage().toLowerCase();
		if (word.contains("catch a swordfish")) {
			swordfishcount++;
		}
		if (word.contains("catch a tuna")) {
			tunacount++;
		}
	}

	public boolean needToBank() {
		return isInventoryFull();
	}

	public boolean atLobster() {
		final RSNPC lobster = getNearestNPCByID(lobsterID);
		return lobster != null && tileOnScreen(lobster.getLocation());

	}

	public boolean atBank() {
		status = "Banking";
		final RSObject bank = getNearestObjectByID(BankBoothID);
		return bank != null && tileOnScreen(bank.getLocation());

	}

	public boolean handleBank() {
		final RSObject bankBooth = getNearestObjectByID(BankBoothID);
		if (bank == null) {
			return false;
		}
		if (bank.isOpen()) {
			bank.depositAllExcept(FishingTools);
		} else {
			atObject(bankBooth, "uickly");
		}
		return true;

	}

	public boolean fishLobster() {
		status = "Fishing";
		final RSNPC fishLobster = getNearestNPCByID(lobsterID);
		return fishLobster != null && atNPC(fishLobster, "Harpoon");

	}

	public void bankToLobster() {
		status = "Walking";
		final RSTile[] randomizedPath = randomizePath(bankToLobster, 2, 2);
		while (distanceTo(randomizedPath[randomizedPath.length]) < 3) {
			if (distanceTo(getDestination()) < random(5, 12)) {
				walkPathMM(randomizedPath);
			}
			wait(random(200, 400));
		}
	}

	public boolean walkLobster() {
		status = "Walking";
		setCameraAltitude(true);
		final RSTile[] randomizedPath = randomizePath(bankToLobster, 2, 2);
		return walkPathMM(randomizedPath, 25);

	}

	public void walkToBank() {
		status = "Walking";
		final RSTile[] randomizedPath = randomizePath(lobsterToBank, 2, 2);
		while (distanceTo(randomizedPath[randomizedPath.length]) < 3) {
			if (distanceTo(getDestination()) < random(5, 12)) {
				walkPathMM(randomizedPath);
			}
			wait(random(200, 400));
		}
	}

	public int antiBan() {
		status = "Anitban activated";
		final int gamble = random(1, 15);
		final int x = random(0, 750);
		final int y = random(0, 500);
		final int xx = random(554, 710);
		final int yy = random(230, 444);
		final int screenx = random(1, 510);
		final int screeny = random(1, 450);
		switch (gamble) {
		case 1:
			return random(500, 750);
		case 2:
			moveMouse(x, y);
			return random(500, 750);
		case 3:
			openTab(TAB_INVENTORY);
			return random(200, 400);
		case 4:
			if (getMyPlayer().isMoving()) {
				return random(750, 1000);
			}
		case 5:
			moveMouse(x, y);
		case 6:
			if (getCurrentTab() != TAB_STATS) {
				openTab(TAB_STATS);
				moveMouse(xx, yy);
				return random(500, 800);

			}
		case 7:
			if (random(1, 8) == 2) {
				int angle = getCameraAngle() + random(-90, 90);
				if (angle < 0) {
					angle = 0;
				}
				if (angle > 359) {
					angle = 0;
				}

				setCameraRotation(angle);
			}
			return random(500, 750);
		case 8:
			moveMouse(screenx, screeny);
			return random(100, 320);
		case 9:
			moveMouse(screenx, screeny);
			return random(100, 320);
		case 10:
			randomTab();
			wait(random(4000, 6000));
			return random(120, 250);
		case 11:
			wait(random(4000, 6000));
			moveMouse(screenx, screeny);
			return random(100, 320);
		case 12:
			wait(random(4000, 6000));
			moveMouse(screenx, screeny);
			return random(100, 320);
		case 13:
			wait(random(4500, 7000));
			moveMouse(screenx, screeny);
			return random(100, 320);

		}
		return random(500, 750);

	}

	public int randomTab() {
		final int random1 = random(1, 23);
		switch (random1) {
		case 1:
			openTab(TAB_STATS);
			return random(100, 500);
		case 2:
			openTab(TAB_ATTACK);
			return random(100, 500);
		case 3:
			openTab(TAB_QUESTS);
			return random(100, 500);
		case 4:
			openTab(TAB_EQUIPMENT);
			return random(100, 500);
		case 5:
			openTab(TAB_INVENTORY);
			return random(100, 500);
		case 6:
			openTab(TAB_PRAYER);
			return random(100, 500);
		case 7:
			openTab(TAB_MAGIC);
			return random(100, 500);
		case 8:
			openTab(TAB_SUMMONING);
			return random(100, 500);
		case 9:
			openTab(TAB_FRIENDS);
			return random(100, 500);
		case 10:
			openTab(TAB_IGNORE);
			return random(100, 500);
		case 11:
			openTab(TAB_CLAN);
			return random(100, 500);
		case 12:
			openTab(TAB_CONTROLS);
			return random(100, 500);
		case 13:
			openTab(TAB_MUSIC);
			return random(100, 500);
		case 14:
			openTab(TAB_OPTIONS);
			return random(100, 500);
		}
		return random(100, 300);

	}

	@Override
	public int loop() {
		setRun(true);
		if (getMyPlayer().isMoving()) {
			return random(800, 1600);
		}
		if (getMyPlayer().getAnimation() == fishingAnimation) {
			return random(1500, 2000);
		}
		antiBan();
		if (needToBank()) {
			if (atBank()) {
				if (handleBank()) {
					return random(800, 1000);
				}
			} else {
				walkToBank();
				return random(800, 1000);
			}
		} else {
			if (atLobster()) {
				if (fishLobster()) {
					antiBan();
				}
				return random(1500, 2000);
			} else {
				if (walkLobster()) {
					return random(800, 1000);
				}
			}
		}
		return random(800, 1000);
	}
}