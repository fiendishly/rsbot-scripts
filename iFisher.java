import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.Map;
import java.io.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.rsbot.bot.*;
import org.rsbot.script.*;
import org.rsbot.bot.input.Mouse;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;
import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.util.GlobalConfiguration;
import org.rsbot.script.ScriptManifest;


@ScriptManifest(authors = {"Issues"}, category = "Fishing", name = "iFisher", version = 2.8, description = "<html>" + "<body bgcolor='#B0C4DE'>" + "<h2><font color='#191970' size='6'><center>iFisher</center></font></h2>" + "" + "<center><font color='white' size='3'>All setting's can be configured in the GUI</font></center>" + "<center><font color='white' size='3'>Just select your account and press ok.</font></center>" + "</body>" + "</html>")
public class iFisher extends Script implements PaintListener, ServerMessageListener {

	final ScriptManifest props = getClass().getAnnotation(ScriptManifest.class);
	public long moneyGained, time, xpHour, levelIn;
	public long startTime = System.currentTimeMillis();
	public int runEnergy = random(50, 100);
	public int bassPrice, mackerelPrice, codPrice, gearfails, levelGained, catchesPerHour, startexp, gearMode, fishSpot, fishId, fished, fishEquip, XPToGo, shrimpsPrice, anchoviesPrice, lobsterPrice, swordfishPrice, tunaPrice, herringPrice, sardinesPrice, sharkPrice, troutPrice, salmonPrice, pikePrice;
	public boolean useAntiBan, dropTuna, drop, catherbyLoc, kjMode, paused, barbMode, paint, startScript, fishingGuild;
	public String status, fishing, location2, fishComm = "";
	public RSTile fishTile, bankTile;
	public RSTile[] toBank, toFish;
	private GUI GUI;

	public boolean onStart(Map<String, String> args) {
		GUI = new GUI();
		GUI.setVisible(true);
		while (!startScript) {
			fished = 0;
			wait(10);
		}
		startTime = System.currentTimeMillis();
		return true;
	}

	public void onFinish() {
		GUI.setVisible(false);
	}

	public void onRepaint(Graphics g) {
		long millis = System.currentTimeMillis() - startTime;
		long hours = millis / (1000 * 60 * 60);
		millis -= hours * (1000 * 60 * 60);
		long minutes = millis / (1000 * 60);
		millis -= minutes * (1000 * 60);
		long seconds = millis / 1000;
		int xpGained;
		if (startexp == 0) {
			startexp = skills.getCurrentSkillExp(STAT_FISHING);
		}
		xpGained = skills.getCurrentSkillExp(STAT_FISHING) - startexp;
		XPToGo = skills.getXPToNextLevel(STAT_FISHING);
		time = (System.currentTimeMillis() - startTime) / 1000;

		if (time > 0) {
			catchesPerHour = (int) ((fished * 60 * 60) / time);
			xpHour = (xpGained * 60 * 60) / time;
			levelIn = (long) (60 * 60 * (XPToGo / (double) xpHour));
		}

		Mouse m = Bot.getClient().getMouse();
		Point p = new Point(m.x, m.y);

		//Status Button
		if (startScript && paint) {
			g.setColor(new Color(0, 0, 0));
			g.drawRect(6, 175, 68, 14);
			g.setColor(new Color(25, 25, 112, 100));
			g.fillRect(7, 182, 67, 7);
			g.setColor(new Color(0, 0, 205, 100));
			g.fillRect(7, 176, 67, 6);

			g.setColor(Color.WHITE);
			g.setFont(new Font("Segoe UI", Font.BOLD, 10));
			g.drawString("iFisher v" + props.version(), 9, 186);

			g.setColor(new Color(0, 0, 0));
			g.drawRect(6, 192, 68, 14);
			g.setColor(new Color(25, 25, 112, 100));//Bottom
			g.fillRect(7, 199, 67, 7);
			g.setColor(new Color(0, 0, 205, 100));//Top
			g.fillRect(7, 193, 67, 6);

			if (isWithinBounds(p, 6, 192, 68, 14)) {
				g.setColor(new Color(255, 140, 0));
			} else {
				g.setColor(Color.WHITE);
			}
			g.setFont(new Font("Segoe UI", Font.BOLD, 10));
			g.drawString("- Status", 9, 203);//Text

			//Exp Button
			g.setColor(new Color(0, 0, 0));
			g.drawRect(6, 209, 68, 14);
			g.setColor(new Color(25, 25, 112, 100));//Bottom
			g.fillRect(7, 216, 67, 7);
			g.setColor(new Color(0, 0, 205, 100));//Top
			g.fillRect(7, 210, 67, 6);

			if (isWithinBounds(p, 6, 209, 68, 14)) {
				g.setColor(new Color(255, 140, 0));
			} else {
				g.setColor(Color.WHITE);
			}
			g.setFont(new Font("Segoe UI", Font.BOLD, 10));
			g.drawString("- Experience", 9, 220);//Text

			//Gold Button
			g.setColor(new Color(0, 0, 0));
			g.drawRect(6, 226, 68, 14);
			g.setColor(new Color(25, 25, 112, 100));//Bottom
			g.fillRect(7, 233, 67, 7);
			g.setColor(new Color(0, 0, 205, 100));//Top
			g.fillRect(7, 227, 67, 6);

			if (isWithinBounds(p, 6, 226, 68, 14)) {
				g.setColor(new Color(255, 140, 0));
			} else {
				g.setColor(Color.WHITE);
			}
			g.setFont(new Font("Segoe UI", Font.BOLD, 10));
			g.drawString("- Profit", 9, 237);//Text

			if (isWithinBounds(p, 6, 226, 68, 14)) {//Gold
				g.setColor(new Color(0, 0, 0));
				g.drawRect(75, 226, 113, 65);
				g.setColor(new Color(0, 0, 205, 100));
				g.fillRect(76, 227, 112, 32);
				g.setColor(new Color(25, 25, 112, 100));
				g.fillRect(76, 258, 112, 33);

				g.setFont(new Font("Arial", Font.BOLD, 10));
				g.setColor(Color.WHITE);
				g.drawString("Fish: " + fished, 79, 240);
				g.drawString("Fish/Hour: " + catchesPerHour, 79, 255);
				if (drop) {
					g.drawString("Gold: N/A", 79, 270);
					g.drawString("Gold/Hour: N/A", 79, 285);
				} else {
					g.drawString("Gold: " + moneyGained, 79, 270);
					if (fished > 0)
						g.drawString("Gold/Hour: " + moneyGained / time * 3600, 79, 285);
					else
						g.drawString("Please Wait...", 79, 285);
				}
			}
			if (isWithinBounds(p, 6, 209, 68, 14)) {//EXP

				g.setColor(new Color(0, 0, 0));
				g.drawRect(75, 209, 113, 84);
				g.setColor(new Color(0, 0, 205, 100));
				g.fillRect(76, 210, 112, 34);
				g.setColor(new Color(25, 25, 112, 100));
				g.fillRect(76, 243, 112, 50);

				g.setFont(new Font("Arial", Font.BOLD, 10));
				g.setColor(Color.WHITE);

				g.drawString("Exp Gained: " + xpGained, 79, 223);//Text
				g.drawString("Exp/Hour: " + xpHour, 79, 238);//Text
				if (fished > 0) {
					g.drawString("Level in: " + levelIn / 3600 + ":" + ((levelIn / 60 % 60 < 10) ? "0" : "") + levelIn / 60 % 60 + ":" + ((levelIn % 60 < 10) ? "0" : "") + levelIn % 60, 79, 253);
				} else {
					g.drawString("Level In: 0:00:00", 79, 253);//Text
				}

				g.drawString("Levels Gained: " + levelGained, 79, 268);


				g.setColor(new Color(0, 0, 0));
				g.drawRect(80, 275, 100, 12);
				g.setColor(new Color(255, 0, 0));
				g.fillRect(81, 276, 99, 11);
				g.setColor(new Color(34, 139, 34));
				g.fillRect(81, 276, skills.getPercentToNextLevel(STAT_FISHING), 11);
				g.setColor(Color.BLACK);
				g.drawString("   " + skills.getPercentToNextLevel(STAT_FISHING) + "% to next Level", 79, 285);


			}
			if (isWithinBounds(p, 6, 192, 68, 14)) {//Status
				g.setColor(new Color(0, 0, 0));
				g.drawRect(75, 192, 113, 84);
				g.setColor(new Color(0, 0, 205, 100));
				g.fillRect(76, 193, 112, 34);
				g.setColor(new Color(25, 25, 112, 100));
				g.fillRect(76, 226, 112, 50);

				g.setFont(new Font("Segoe UI", Font.BOLD, 10));
				g.setColor(new Color(255, 140, 0));
				g.drawString("" + status, 79, 208);
				g.setFont(new Font("Arial", Font.BOLD, 10));
				g.setColor(Color.WHITE);
				g.drawString("" + location2, 79, 223);//Text
				g.drawString("" + fishing, 79, 238);//Text
				if (drop) {
					g.drawString("Droping Fish", 79, 253);
				} else {
					g.drawString("Banking Fish", 79, 253);
				}
				g.drawString("Run for: " + hours + ":" + minutes + ":" + seconds + "", 79, 268);

			}
		}
	}

	public void serverMessageRecieved(ServerMessageEvent e) {
		String message = e.getMessage().toLowerCase();
		if (message.contains("advanced")) {
			levelGained++;
			wait(random(500, 1000));
			clickContinue();
		}
		if (message.contains("bass")) {
			moneyGained += bassPrice;
			fished++;
		}
		if (message.contains("mackerel")) {
			moneyGained += mackerelPrice;
			fished++;
		}
		if (message.contains("cod")) {
			moneyGained += codPrice;
			fished++;
		}
		if (message.contains("you catch a crayfish")) {
			fished++;
		}
		if (message.contains("shrimps")) {
			moneyGained += shrimpsPrice;
			fished++;
		}
		if (message.contains("anchovies")) {
			moneyGained += anchoviesPrice;
			fished++;
		}
		if (message.contains("you catch a lobster")) {
			moneyGained += lobsterPrice;
			fished++;
		}
		if (message.contains("you catch a tuna") && !dropTuna) {
			moneyGained += tunaPrice;
			fished++;
		}
		if (message.contains("you catch a swordfish")) {
			moneyGained += swordfishPrice;
			fished++;
		}
		if (message.contains("you catch a shark")) {
			moneyGained += sharkPrice;
			fished++;
		}
		if (message.contains("herring")) {
			moneyGained = herringPrice;
			fished++;
		}
		if (message.contains("sardines")) {
			moneyGained += sardinesPrice;
			fished++;
		}
		if (message.contains("trout")) {
			moneyGained += troutPrice;
			fished++;
		}
		if (message.contains("salmon")) {
			moneyGained += salmonPrice;
			fished++;
		}
		if (message.contains("pike")) {
			moneyGained += pikePrice;
			fished++;
		}
	}

	public void openBank() {
		if (kjMode) {
			if (RSInterface.getInterface(11).isValid()) {
				atInterface(RSInterface.getInterface(11).getChild(15));
				wait(random(500, 1000));
			}
			RSObject depositbox = getNearestObjectByID(36788);
			if (inventoryContains(fishId)) {
				atInventoryItem(fishId, "Use");
				wait(random(300, 500));
				if (depositbox != null) {
					if (!atObject(depositbox, "-> Bank")) {
						setCameraAltitude(true);
						setCameraRotation(getCameraAngle() + random(-90, 90));
					}
					wait(random(2000, 3000));
					if (RSInterface.getInterface(232).isValid()) {
						atInterface(RSInterface.getInterface(232).getChild(5));
						wait(random(1000, 1500));
					}
				}
			}
			if (inventoryContains(359)) {//Tunas
				atInventoryItem(359, "Use");
				wait(random(300, 500));
				if (depositbox != null) {
					if (!atObject(depositbox, "-> Bank")) {
						setCameraAltitude(true);
						setCameraRotation(getCameraAngle() + random(-90, 90));
					}
					wait(random(2000, 3000));
					if (RSInterface.getInterface(232).isValid()) {
						atInterface(RSInterface.getInterface(232).getChild(5));
						wait(random(1000, 1500));
					}
				}
			}
		} else {
			RSNPC banker = getNearestNPCByID(494, 495, 496, 5912, 5913);
			if (banker != null) {
				if (!atNPC(banker, "Bank Banker")) {
					setCameraAltitude(true);
					setCameraRotation(getCameraAngle() + random(-90, 90));
				}
				wait(random(500, 1000));
			} else {
				wait(random(1000, 1500));
			}
		}
	}

	public void fish() {
		if (getMyPlayer().getAnimation() == -1 && !getMyPlayer().isMoving()) {
			RSNPC nSpot = getNearestNPCByID(fishSpot);
			if (nSpot == null) {
				if (catherbyLoc) {
					walkTileMM(new RSTile(2847, 3422));//If not found walk here
				}
				wait(random(50, 150));
			}
			if (nSpot != null) {
				if (distanceTo(nSpot.getLocation()) > 4 || !nSpot.isOnScreen()) {
					RSTile destination = randomizeTile(nSpot.getLocation(), 2, 2);
					walkTileMM(destination);
					waitToStop();
					wait(random(500, 1000));
				}
				if (nSpot.isOnScreen()) {
					atTile(nSpot.getLocation(), fishComm);
					waitToStop();
					wait(random(2000, 3500));
				}
			}
		}
	}

	public int walkFish() {
		if (takeBoatToKaramja())
			return random(5000, 6000);

		if (distanceTo(getDestination()) < random(8, 12) || distanceTo(getDestination()) > 40) {
			if (!walkPathMM(toFish)) {
				walkToClosestTile(toFish);
				return random(50, 150);
			}
		}
		return random(50, 150);
	}

	public int walkBank() {
		if (takeBoatFromKaramja())
			return random(5000, 6000);

		if (dropTunas())
			return random(200, 300);

		if (fishingGuild) {
			if (distanceTo(getDestination()) < random(5, 8) || distanceTo(getDestination()) > 40) {
				if (!walkPathMM(toBank)) {
					walkToClosestTile(toBank, 1, 1);
					return random(50, 150);
				}
			}
		} else {
			if (distanceTo(getDestination()) < random(8, 12) || distanceTo(getDestination()) > 40) {
				if (!walkPathMM(toBank)) {
					walkToClosestTile(toBank);
					return random(50, 150);
				}
			}
		}
		return random(50, 150);
	}

	public void gearCheck() {
		if (kjMode && getInventoryCount(995) < 30) {
			log("Not enough Gold to pay the fee.");
			gearfails++;
		}
		if (!barbMode) {
			if (gearMode == 2) {//Bait
				if (!inventoryContains(313, 307)) {
					log("No Rod or Bait...");
					gearfails++;
					return;
				}
			}
			if (gearMode == 1) {//Feathers
				if (!inventoryContains(314, 309)) {
					log("No Fly Rod or Feathers...");
					gearfails++;
					return;
				}
			}
			if (gearMode == 3) {
				if (!inventoryContains(303)) {
					log("No Net...");
					gearfails++;
					return;
				}
			}
			if (gearMode == 4) {
				if (!inventoryContains(301)) {
					log("No Lobster Cage...");
					gearfails++;
					return;
				}
			}
			if (gearMode == 5) {
				if (!inventoryContains(311)) {
					log("No Harpoon...");
					gearfails++;
					return;
				}
			}
			if (gearMode == 6) {
				if (!inventoryContains(305)) {
					log("No Net...");
					gearfails++;
					return;
				}
			}
			if (gearMode == 7) {
				if (!inventoryContains(13431)) {
					log("No Crayfish Cage...");
					gearfails++;
					return;
				}
			}
			gearfails = 0;
		}
	}

	public boolean inArea(int maxX, int minY, int minX, int maxY) {
		int x = getMyPlayer().getLocation().getX();
		int y = getMyPlayer().getLocation().getY();
		return x >= minX && x <= maxX && y >= minY && y <= maxY;
	}

	public boolean isWithinBounds(Point p, int x, int y, int w, int h) {
		return p.x > x && p.x < x + w && p.y > y && p.y < y + h;
	}

	public void waitToStop() {
		while (getMyPlayer().isMoving()) {
			wait(150);
		}
	}

	public int loop() {
		if (atWelcomButton()) {
			return 500;
		}
		if (!paused && startScript) {
			gearCheck();
			if (!isRunning() && getEnergy() >= runEnergy) {
				runEnergy = random(50, 100);
				setRun(true);
				wait(random(300, 700));
			}
			switch (getState()) {
				case FISH:
					fish();
					break;
				case GOTO_FISH:
					walkFish();
					break;
				case GOTO_BANK:
					walkBank();
					break;
				case IDLE:
					wait(random(1000, 2000));
					if (useAntiBan) {
						antiBan();
					}
					break;
				case OPEN_BANK:
					openBank();
					break;
				case USE_BANK:
					if (kjMode) {
						bank.depositAllExcept(fishEquip, 314, 309, 307, 303, 313, 311, 301, 995);//Money
					} else {
						bank.depositAllExcept(fishEquip, 314, 309, 307, 303, 313, 311, 301);
					}
					break;
				case DROP:
					dropAllExcept_(fishEquip, 314, 309, 307, 303, 313, 311, 301, 13431);
					break;
			}
		}
		return (random(500, 1000));
	}

	private enum State {
		OPEN_BANK, USE_BANK, FISH, GOTO_BANK, GOTO_FISH, IDLE, DROP
	}

	private State getState() {
		if (isInventoryFull() && drop) {
			status = "Droping fish";
			return State.DROP;
		}
		if (isInventoryFull()) {
			if (bank.isOpen() && !kjMode) {
				status = "Depositing fish";
				return State.USE_BANK;
			} else if (distanceTo(bankTile) < 8 && !getMyPlayer().isMoving()) {
				status = "Opening bank";
				return State.OPEN_BANK;
			} else {
				status = "Walking to bank";
				return State.GOTO_BANK;
			}
		} else {
			if (getMyPlayer().getAnimation() != -1) {
				return State.IDLE;
			} else if (catherbyLoc && inArea(2862, 3422, 2834, 3434) || distanceTo(fishTile) < 10) {
				status = "Fishing";
				return State.FISH;
			} else {
				status = "Walking to fish";
				return State.GOTO_FISH;
			}
		}
	}

	public void dropAllExcept_(final int... items) {
		try {
			for (int c = 0; c < 4; c++) {
				for (int r = 0; r < 7; r++) {
					boolean found = false;
					for (int i = 0; i < items.length && !found; i++) {
						found = items[i] == getInventoryArray()[c + r * 4];
					}
					if (!found) {
						dropItem(c, r);
					}
				}
			}
		} catch (final Exception e) {
			log("Prevented fatal error.");
		}
	}

	public boolean atWelcomButton() {
		RSInterface welcomeInterface = RSInterface.getInterface(378);
		if (welcomeInterface.getChild(45).getAbsoluteX() > 20 || (!welcomeInterface.getChild(117).getText().equals("10.1120.190") && !welcomeInterface.getChild(117).getText().equals(""))) {
			clickMouse(random(215, 555), random(420, 440), true);
			return true;
		} else {
			return false;
		}
	}

	public boolean takeBoatFromKaramja() {
		try {
			RSNPC customsOfficer = getNearestNPCByID(380);
			RSObject plank = getNearestObjectByID(2084);

			if (plank != null) {
				if (!getMyPlayer().isMoving()) {
					atObject(plank, "Cross");
					wait(random(1000, 1500));
					return true;
				}
			}
			if (RSInterface.getInterface(228).isValid()) {
				atInterface(RSInterface.getInterface(228).getChild(2));
				wait(random(200, 300));
				return true;
			}
			if (RSInterface.getInterface(242).isValid()) {
				atInterface(RSInterface.getInterface(242).getChild(6));
				wait(random(200, 300));
				return true;
			}
			if (RSInterface.getInterface(230).isValid()) {
				atInterface(RSInterface.getInterface(230).getChild(3));
				wait(random(200, 300));
				return true;
			}
			if (RSInterface.getInterface(241).isValid()) {
				atInterface(RSInterface.getInterface(241).getChild(5));
				wait(random(200, 300));
				return true;
			}
			if (RSInterface.getInterface(64).isValid()) {
				atInterface(RSInterface.getInterface(64).getChild(5));
				wait(random(200, 300));
				gearfails = 0;
				return true;
			}
			if (RSInterface.getInterface(228).isValid()) {
				atInterface(RSInterface.getInterface(228).getChild(2));
				wait(random(200, 300));
				return false;
			}
			if (RSInterface.getInterface(241).isValid()) {
				atInterface(RSInterface.getInterface(241).getChild(5));
				wait(random(200, 300));
				return false;
			}
			if (customsOfficer != null) {
				if (tileOnScreen(customsOfficer.getLocation())) {
					atNPC(customsOfficer, "Pay-Fare");
					waitToStop();
					wait(random(500, 1000));
					return true;
				} else {
					walkTileMM(randomizeTile(customsOfficer.getLocation(), 2, 2));
					waitToStop();
					wait(random(500, 1000));
					return true;
				}
			}
			wait(random(2000, 3000));
		} catch (Exception e) {
			//IGNORED
		}
		return false;
	}

	public boolean takeBoatToKaramja() {
		try {
			int[] seamanIDs = new int[]{376, 377, 378};
			RSNPC seaman = getNearestNPCByID(seamanIDs);
			RSObject plank = getNearestObjectByID(2082);

			if (plank != null) {
				if (!getMyPlayer().isMoving()) {
					atObject(plank, "Cross");
					wait(random(1000, 1500));
					return true;
				}
			}
			if (RSInterface.getInterface(64).isValid()) {
				atInterface(RSInterface.getInterface(64).getChild(5));
				wait(random(6000, 7000));
				gearfails = 0;
				return true;
			}
			if (RSInterface.getInterface(228).isValid()) {
				atInterface(RSInterface.getInterface(228).getChild(2));
				wait(random(200, 300));
				return true;
			}
			if (RSInterface.getInterface(241).isValid()) {
				atInterface(RSInterface.getInterface(241).getChild(5));
				wait(random(200, 300));
				return true;
			}

			if (seaman != null) {
				if (tileOnScreen(seaman.getLocation())) {
					atNPC(seaman, "Pay-fare");
					waitToStop();
					wait(random(500, 1000));
					return true;
				} else {
					walkTileMM(randomizeTile(seaman.getLocation(), 2, 2));
					waitToStop();
					wait(random(500, 1000));
					return true;
				}
			}
			wait(random(2000, 3000));
		} catch (Exception e) {
			//IGNORED
		}
		return false;
	}

	public boolean dropTunas() {
		int[] keep = new int[]{311, 301, 995, 377, 371};
		if (dropTuna) {
			if (inventoryContains(359)) {
				dropAllExcept_(keep);
				dropAllExcept_(keep);//Drop 3 times to make sure all are gone.
				dropAllExcept_(keep);
				return true;
			}
		}
		return false;
	}

	private void antiBan() {
		if (random(0, 10) == 0) {
			final char[] LR = new char[]{KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT};
			final char[] UD = new char[]{KeyEvent.VK_DOWN, KeyEvent.VK_UP};
			final char[] LRUD = new char[]{KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_UP, KeyEvent.VK_UP};
			final int random2 = random(0, 2);
			final int random1 = random(0, 2);
			final int random4 = random(0, 4);
			if (random(0, 3) == 0) {
				Bot.getInputManager().pressKey(LR[random1]);
				wait(random(100, 400));
				Bot.getInputManager().pressKey(UD[random2]);
				wait(random(300, 600));
				Bot.getInputManager().releaseKey(UD[random2]);
				wait(random(100, 400));
				Bot.getInputManager().releaseKey(LR[random1]);
			} else {
				Bot.getInputManager().pressKey(LRUD[random4]);
				if (random4 > 1) {
					wait(random(300, 600));
				} else {
					wait(random(500, 900));
				}
				Bot.getInputManager().releaseKey(LRUD[random4]);
			}
			if (random(0, 1) == 0) {
				int x = random(0, 750);
				int y = random(0, 500);
				moveMouse(x, y);
			}
		} else {
			wait(random(200, 2000));
		}
	}

	public class GUI extends javax.swing.JFrame {

		private final File settingsFile = new File(new File(GlobalConfiguration.Paths.getSettingsDirectory()), "iFisher.txt");

		public GUI() {
			initComponents();
		}

		@SuppressWarnings("unchecked")
		private void initComponents() {

			startButton = new javax.swing.JButton();
			pauseButton = new javax.swing.JButton();
			applyButton = new javax.swing.JButton();
			barbBox = new javax.swing.JCheckBox();
			dropTunaBox = new javax.swing.JCheckBox();
			dropBox = new javax.swing.JCheckBox();
			locationCBox = new javax.swing.JComboBox();
			fishingCBox = new javax.swing.JComboBox();
			locationLabel = new javax.swing.JLabel();
			fishingLabel = new javax.swing.JLabel();
			paintBox = new javax.swing.JCheckBox();
			antibanBox = new javax.swing.JCheckBox();

			setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			setTitle("iFisher v2.8");
			setIconImages(null);
			setResizable(false);

			startButton.setText("Start Script");
			startButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					startButtonActionPerformed(evt);
				}
			});

			pauseButton.setText("Pause Script");
			pauseButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					pauseButtonActionPerformed(evt);
				}
			});

			applyButton.setText("Apply Settings");
			applyButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					applyButtonActionPerformed(evt);
				}
			});

			barbBox.setText("Barbarian Mode");

			dropTunaBox.setText("Anti-Tuna");

			dropBox.setText("Power Fishing");

			locationCBox.setMaximumRowCount(5);
			locationCBox.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Al-Kharid", "Barbarian Village", "Catherby", "Draynor Village", "Fishing Guild", "Lumbridge", "Karamja", "Seers' Village"}));
			locationCBox.setAutoscrolls(true);
			locationCBox.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
			locationCBox.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					locationCBoxActionPerformed(evt);
				}
			});

			fishingCBox.setMaximumRowCount(5);
			fishingCBox.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Shrimp & Anchovies", "Herring & Sardines"}));
			fishingCBox.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					fishingCBoxActionPerformed(evt);
				}
			});

			locationLabel.setText("Location:");

			fishingLabel.setText("Fishing:");

			paintBox.setSelected(true);
			paintBox.setText("Paint Progress");

			antibanBox.setSelected(true);
			antibanBox.setText("Anti-Ban");

			javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
			getContentPane().setLayout(layout);
			layout.setHorizontalGroup(
					layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
							.addGroup(layout.createSequentialGroup()
							.addContainerGap()
							.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
									.addGroup(layout.createSequentialGroup()
											.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
													.addComponent(locationCBox, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
													.addComponent(barbBox)
													.addComponent(dropBox)
													.addComponent(dropTunaBox)
													.addComponent(locationLabel))
											.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
											.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
											.addComponent(antibanBox)
											.addComponent(paintBox)
											.addComponent(fishingLabel)
											.addComponent(fishingCBox, 0, 162, Short.MAX_VALUE)))
									.addGroup(layout.createSequentialGroup()
									.addComponent(startButton)
									.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
									.addComponent(pauseButton)
									.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
									.addComponent(applyButton)))
							.addGap(10, 10, 10))
			);
			layout.setVerticalGroup(
					layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
							.addGroup(layout.createSequentialGroup()
							.addContainerGap()
							.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
									.addComponent(fishingLabel)
									.addComponent(locationLabel))
							.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
							.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
									.addComponent(locationCBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
									.addComponent(fishingCBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
							.addGap(7, 7, 7)
							.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
									.addComponent(paintBox)
									.addComponent(dropTunaBox))
							.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
							.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
									.addComponent(antibanBox)
									.addComponent(dropBox))
							.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(barbBox)
							.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
							.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
									.addComponent(pauseButton)
									.addComponent(applyButton)
									.addComponent(startButton))
							.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
			);
			BufferedReader in;
			try {
				in = new BufferedReader(new FileReader(settingsFile));
				String[] settings = new String[7];
				String line;
				for (int i = 0; i < settings.length && (line = in.readLine()) != null; i++) {
					settings[i] = line;
				}
				try {
					locationCBox.setSelectedIndex(Integer.parseInt(settings[1]));
					makeChanges();
					fishingCBox.setSelectedIndex(Integer.parseInt(settings[0]));
					makeChanges();
					barbBox.setSelected(settings[2].equals("1"));
					dropBox.setSelected(settings[3].equals("1"));
					dropTunaBox.setSelected(settings[4].equals("1"));
					paintBox.setSelected(settings[5].equals("1"));
					antibanBox.setSelected(settings[6].equals("1"));
				} catch (Exception e) {
					dropTunaBox.setSelected(false);
				}
				in.close();
			} catch (IOException e) {
				log("No Settings File Found!");
			}
			makeChanges();
			pack();
		}

		public void makeChanges() {
			String fish = (String) fishingCBox.getSelectedItem();
			if (fish.equals("Swordfish & Tuna")) {
				dropTunaBox.setEnabled(true);
				dropTunaBox.setSelected(false);
			} else {
				dropTunaBox.setEnabled(false);
				dropTunaBox.setSelected(false);
			}
			if (fish.equals("Swordfish & Tuna") || fish.equals("Sharks")) {
				barbBox.setEnabled(true);
				barbBox.setSelected(false);
			} else {
				barbBox.setEnabled(false);
				barbBox.setSelected(false);
			}
		}

		private void applyChanges() {
			barbMode = barbBox.isSelected();
			dropTuna = dropTunaBox.isSelected();
			drop = dropBox.isSelected();
			paint = paintBox.isSelected();
			useAntiBan = antibanBox.isSelected();
			String fish = (String) fishingCBox.getSelectedItem();
			String location = (String) locationCBox.getSelectedItem();

			if (location.equals("Barbarian Village")) {
				location2 = "Barbarian Village";
				bankTile = new RSTile(3094, 3490);
				fishTile = new RSTile(3104, 3431);
				toBank = new RSTile[]{new RSTile(3104, 3431), new RSTile(3100, 3434), new RSTile(3094, 3439), new RSTile(3091, 3445), new RSTile(3090, 3451), new RSTile(3090, 3457), new RSTile(3087, 3463), new RSTile(3093, 3464), new RSTile(3099, 3474), new RSTile(3100, 3480), new RSTile(3093, 3484), new RSTile(3090, 3490), new RSTile(3094, 3491)};
				toFish = reversePath(toBank);

				if (fish.equals("Trout & Salmon")) {
					fishing = "Trout & Salmon";
					fishSpot = 328;
					fishComm = "Lure";
					gearMode = 1;
				}
				if (fish.equals("Pike")) {
					fishing = "Pike";
					fishSpot = 328;
					fishComm = "Bait";
					gearMode = 2;
				}
			}
			if (location.equals("Lumbridge")) {
				location2 = "Lumbridge";
				drop = true;
				if (fish.equals("Trout & Salmon")) {
					fishTile = new RSTile(3240, 3247);
					fishing = "Trout & Salmon";
					fishSpot = 329;
					fishComm = "Lure";
					gearMode = 1;
				}
				if (fish.equals("Pike")) {
					fishTile = new RSTile(3240, 3247);
					fishing = "Pike";
					fishSpot = 329;
					fishComm = "Bait";
					gearMode = 2;
				}
				if (fish.equals("Shrimp & Anchovies")) {
					fishTile = new RSTile(3240, 3152);
					fishing = "Shrimp & Anchovies";
					fishSpot = 4908;
					fishComm = "Net";
					gearMode = 3;
				}
				if (fish.equals("Herring & Sardines")) {
					fishTile = new RSTile(3240, 3152);
					fishing = "Herring & Sardines";
					fishSpot = 4908;
					fishComm = "Bait";
					gearMode = 2;
				}
				if (fish.equals("Crayfish")) {
					fishTile = new RSTile(3256, 3205);
					fishing = "Crayfish";
					fishSpot = 6267;
					fishComm = "Cage";
					gearMode = 7;
				}
				toFish = generateFixedPath(fishTile);//To stop from wandering off..
			}
			if (location.equals("Draynor Village")) {
				location2 = "Draynor";
				bankTile = new RSTile(3093, 3243);
				fishTile = new RSTile(3087, 3228);
				toBank = new RSTile[]{new RSTile(3086, 3238), new RSTile(3092, 3243)};
				toFish = new RSTile[]{new RSTile(3086, 3238), new RSTile(3088, 3230)};
				if (fish.equals("Shrimp & Anchovies")) {
					fishing = "Shrimp & Anchovies";
					fishSpot = 327;
					fishComm = "Net";
					gearMode = 3;
				}
				if (fish.equals("Herring & Sardines")) {
					fishing = "Herring & Sardines";
					fishSpot = 327;
					fishComm = "Bait";
					gearMode = 2;
				}
			}
			if (location.equals("Al-Kharid")) {
				location2 = "Al-Kharid";
				bankTile = new RSTile(3269, 3166);
				fishTile = new RSTile(3271, 3145);
				toBank = new RSTile[]{new RSTile(3271, 3144), new RSTile(3276, 3157), new RSTile(3270, 3167)};
				toFish = reversePath(toBank);
				if (fish.equals("Shrimp & Anchovies")) {
					fishing = "Shrimp & Anchovies";
					fishSpot = 330;
					fishComm = "Net";
					gearMode = 3;
				}
				if (fish.equals("Herring & Sardines")) {
					fishing = "Herring & Sardines";
					fishSpot = 330;
					fishComm = "Bait";
					gearMode = 2;
				}
			}
			if (location.equals("Fishing Guild")) {
				fishingGuild = true;
				location2 = "Fishing Guild";
				fishTile = new RSTile(2603, 3416);
				bankTile = new RSTile(2589, 3418);
				toBank = new RSTile[]{new RSTile(2594, 3415), new RSTile(2588, 3419)};
				toFish = new RSTile[]{new RSTile(2599, 3421)};
				if (fish.equals("Sharks")) {
					fishSpot = 313;
					fishComm = "Harpoon";
					gearMode = 5;
					fishing = "Sharks";
				}
				if (fish.equals("Swordfish & Tuna")) {
					fishSpot = 312;
					fishComm = "Harpoon";
					gearMode = 5;//Harpoon
					fishing = "Swordfish & Tuna";
				}
				if (fish.equals("Lobster")) {
					fishSpot = 312;
					fishComm = "Cage";
					gearMode = 4;//Harpoon
					fishing = "Lobster";
				}
				if (fish.equals("Bass & Cod & Mackerel")) {
					fishing = "Bass & Cod & Mackerel";
					fishSpot = 313;
					fishComm = "Net";
					gearMode = 6;
				}
			} else {
				fishingGuild = false;
			}
			if (location.equals("Catherby")) {
				location2 = "Catherby";
				catherbyLoc = true;
				fishTile = new RSTile(2847, 3430);
				bankTile = new RSTile(2809, 3440);
				toBank = new RSTile[]{new RSTile(2853, 3428), new RSTile(2849, 3431), new RSTile(2843, 3433), new RSTile(2837, 3435), new RSTile(2830, 3436), new RSTile(2824, 3437), new RSTile(2817, 3436), new RSTile(2811, 3436), new RSTile(2809, 3440)};
				toFish = new RSTile[]{new RSTile(2811, 3435), new RSTile(2818, 3436), new RSTile(2824, 3437), new RSTile(2830, 3436), new RSTile(2835, 3435), new RSTile(2837, 3432)};
				if (fish.equals("Sharks")) {
					fishSpot = 322;
					fishComm = "Harpoon";
					gearMode = 5;
					fishing = "Sharks";
				}
				if (fish.equals("Swordfish & Tuna")) {
					fishSpot = 321;
					fishComm = "Harpoon";
					gearMode = 5;
					fishing = "Swordfish & Tuna";
				}
				if (fish.equals("Lobster")) {
					fishSpot = 321;
					fishComm = "Cage";
					gearMode = 4;
					fishing = "Lobster";
				}
				if (fish.equals("Shrimp & Anchovies")) {
					fishing = "Shrimp & Anchovies";
					fishSpot = 320;
					fishComm = "Net";
					gearMode = 3;
				}
				if (fish.equals("Herring & Sardines")) {
					fishing = "Herring & Sardines";
					fishSpot = 320;
					fishComm = "Bait";
					gearMode = 2;
				}
				if (fish.equals("Bass & Cod & Mackerel")) {
					fishing = "Bass & Cod & Mackerel";
					fishSpot = 322;
					fishComm = "Net";
					gearMode = 6;
				}
			} else {
				catherbyLoc = false;
			}
			if (location.equals("Karamja")) {
				location2 = "Karamja";
				kjMode = true;
				bankTile = new RSTile(3047, 3235);
				fishTile = new RSTile(2925, 3177);
				toFish = new RSTile[]{new RSTile(3047, 3235), new RSTile(3041, 3236),
						new RSTile(3034, 3236), new RSTile(3028, 3236),
						new RSTile(3028, 3231), new RSTile(3028, 3227),
						new RSTile(3028, 3222), new RSTile(3027, 3217),
						new RSTile(2954, 3146), new RSTile(2948, 3146),
						new RSTile(2943, 3145), new RSTile(2937, 3146),
						new RSTile(2932, 3149), new RSTile(2925, 3150), //The longer the better...
						new RSTile(2919, 3152), new RSTile(2915, 3156),
						new RSTile(2919, 3160), new RSTile(2920, 3165),
						new RSTile(2922, 3169), new RSTile(2925, 3173),
						new RSTile(2925, 3179)};
				toBank = reversePath(toFish);
				if (fish.equals("Shrimp & Anchovies")) {
					fishing = "Shrimp & Anchovies";
					fishSpot = 323;
					fishComm = "Net";
					gearMode = 3;//Net
				}
				if (fish.equals("Herring & Sardines")) {
					fishing = "Herring & Sardines";
					fishSpot = 323;
					fishComm = "Bait";
					gearMode = 2;//net
				}
				if (fish.equals("Lobster")) {
					fishId = 377;
					fishing = "Lobster";
					fishSpot = 324;
					fishComm = "Cage";
					gearMode = 4;//Lobster Cage
				}
				if (fish.equals("Swordfish & Tuna")) {
					fishId = 371;
					fishing = "Swordfish & Tuna";
					fishSpot = 324;
					fishComm = "Harpoon";
					gearMode = 5;//Harpoon
				}
			} else {
				kjMode = false;
			}
			if (location.equals("Seers' Village")) {
				location2 = "Seers' Village";
				bankTile = new RSTile(2726, 3493);
				fishTile = new RSTile(2722, 3529);
				toBank = new RSTile[]{new RSTile(2722, 3529), new RSTile(2727, 3528), new RSTile(2732, 3526), new RSTile(2736, 3521), new RSTile(2740, 3514), new RSTile(2740, 3510), new RSTile(2742, 3504), new RSTile(2738, 3495), new RSTile(2735, 3490), new RSTile(2728, 3485), new RSTile(2725, 3491)};
				toFish = reversePath(toBank);
				if (fish.equals("Trout & Salmon")) {
					fishing = "Trout & Salmon";
					fishSpot = 315;
					fishComm = "Lure";
					gearMode = 1;
				}
				if (fish.equals("Pike")) {
					fishing = "Pike";
					fishSpot = 315;
					fishComm = "Bait";
					gearMode = 2;
				}
			}
			PrintWriter out;
			try {//Saves
				out = new PrintWriter(new FileWriter(settingsFile));
				String[] settings = {
						"" + fishingCBox.getSelectedIndex(),
						"" + locationCBox.getSelectedIndex(),
						barbBox.isSelected() ? "1" : "0",
						dropBox.isSelected() ? "1" : "0",
						dropTunaBox.isSelected() ? "1" : "0",
						paintBox.isSelected() ? "1" : "0",
						antibanBox.isSelected() ? "1" : "0",
				};
				for (String line : settings) {
					out.println(line);
				}
				out.close();
			} catch (IOException e) {
				log("Saveing settings file failed!");
			}
			getPrices();
		}

		public void getPrices() {
			String fish = (String) fishingCBox.getSelectedItem();
			if (fish.equals("Bass & Cod & Mackerel")) {//Bass
				GEItemInfo rawFish = grandExchange.loadItemInfo(363);
				bassPrice = rawFish.getMarketPrice();
			}
			if (fish.equals("Bass & Cod & Mackerel")) {//Cod
				GEItemInfo rawFish = grandExchange.loadItemInfo(341);
				codPrice = rawFish.getMarketPrice();
			}
			if (fish.equals("Bass & Cod & Mackerel")) {//Mackerel
				GEItemInfo rawFish = grandExchange.loadItemInfo(353);
				mackerelPrice = rawFish.getMarketPrice();
			}
			if (fish.equals("Shrimp & Anchovies")) {
				GEItemInfo rawFish = grandExchange.loadItemInfo(317);
				shrimpsPrice = rawFish.getMarketPrice();
			}
			if (fish.equals("Shrimp & Anchovies")) {
				GEItemInfo rawFish = grandExchange.loadItemInfo(321);
				anchoviesPrice = rawFish.getMarketPrice();
			}
			if (fish.equals("Lobster")) {
				GEItemInfo rawFish = grandExchange.loadItemInfo(377);
				lobsterPrice = rawFish.getMarketPrice();
			}
			if (fish.equals("Swordfish & Tuna")) {
				GEItemInfo rawFish = grandExchange.loadItemInfo(359);
				tunaPrice = rawFish.getMarketPrice();
			}
			if (fish.equals("Swordfish & Tuna")) {
				GEItemInfo rawFish = grandExchange.loadItemInfo(371);
				swordfishPrice = rawFish.getMarketPrice();
			}
			if (fish.equals("Herring & Sardines")) {
				GEItemInfo rawFish = grandExchange.loadItemInfo(345);
				herringPrice = rawFish.getMarketPrice();
			}
			if (fish.equals("Herring & Sardines")) {
				GEItemInfo rawFish = grandExchange.loadItemInfo(345);
				sardinesPrice = rawFish.getMarketPrice();
			}
			if (fish.equals("Trout & Salmon")) {
				GEItemInfo rawFish = grandExchange.loadItemInfo(335);
				troutPrice = rawFish.getMarketPrice();
			}
			if (fish.equals("Trout & Salmon")) {
				GEItemInfo rawFish = grandExchange.loadItemInfo(331);
				salmonPrice = rawFish.getMarketPrice();
			}
			if (fish.equals("Pike")) {
				GEItemInfo rawFish = grandExchange.loadItemInfo(349);
				pikePrice = rawFish.getMarketPrice();
			}
			if (fish.equals("Sharks")) {
				GEItemInfo rawFish = grandExchange.loadItemInfo(383);
				sharkPrice = rawFish.getMarketPrice();
			}
		}

		private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {
			if (!startScript) {
				startScript = true;
				applyChanges();
				startButton.setText("Stop Script");
			} else {
				stopScript();
			}
		}

		private void pauseButtonActionPerformed(java.awt.event.ActionEvent evt) {
			if (!paused) {
				paused = true;
				pauseButton.setText("Resume Script");
			} else {
				paused = false;
				pauseButton.setText("Pause Script");
			}
		}

		private void applyButtonActionPerformed(java.awt.event.ActionEvent evt) {
			applyChanges();
		}

		private void locationCBoxActionPerformed(java.awt.event.ActionEvent evt) {
			String location = (String) locationCBox.getSelectedItem();
			if (location.equals("Lumbridge")) {
				fishingCBox.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Crayfish", "Shrimp & Anchovies", "Herring & Sardines", "Trout & Salmon", "Pike"}));
				dropBox.setEnabled(false);
				dropBox.setSelected(true);
			} else {
				dropBox.setEnabled(true);
				dropBox.setSelected(false);
			}
			if (location.equals("Al-Kharid")) {
				fishingCBox.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Shrimp & Anchovies", "Herring & Sardines"}));
			}
			if (location.equals("Barbarian Village") || location.equals("Seers' Village")) {
				fishingCBox.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Trout & Salmon", "Pike"}));
			}
			if (location.equals("Draynor Village")) {
				fishingCBox.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Shrimp & Anchovies", "Herring & Sardines"}));
			}
			if (location.equals("Karamja")) {
				fishingCBox.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Shrimp & Anchovies", "Herring & Sardines", "Lobster", "Swordfish & Tuna"}));
			}
			if (location.equals("Catherby")) {
				fishingCBox.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Shrimp & Anchovies", "Herring & Sardines", "Bass & Cod & Mackerel", "Lobster", "Swordfish & Tuna", "Sharks"}));
			}
			if (location.equals("Fishing Guild")) {
				fishingCBox.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Bass & Cod & Mackerel", "Lobster", "Swordfish & Tuna", "Sharks"}));
			}
			makeChanges();
		}

		private void fishingCBoxActionPerformed(java.awt.event.ActionEvent evt) {
			makeChanges();
		}

		private javax.swing.JCheckBox antibanBox;
		private javax.swing.JButton applyButton;
		private javax.swing.JCheckBox barbBox;
		private javax.swing.JCheckBox dropBox;
		private javax.swing.JCheckBox dropTunaBox;
		private javax.swing.JComboBox fishingCBox;
		private javax.swing.JLabel fishingLabel;
		private javax.swing.JComboBox locationCBox;
		private javax.swing.JLabel locationLabel;
		private javax.swing.JCheckBox paintBox;
		private javax.swing.JButton pauseButton;
		private javax.swing.JButton startButton;

	}
}