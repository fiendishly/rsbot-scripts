import java.io.*;
import java.awt.*;
import java.util.Map;
import java.awt.event.*;

import org.rsbot.bot.*;
import org.rsbot.script.*;
import org.rsbot.bot.input.*;
import org.rsbot.event.listeners.*;
import org.rsbot.script.wrappers.*;
import org.rsbot.util.GlobalConfiguration;
import org.rsbot.event.events.ServerMessageEvent;

@ScriptManifest(authors = { "Issues" }, category = "Fishing", name = "iFisher", version = 3.4, description = "<html>"
		+ "<body bgcolor='#B0C4DE'>"
		+ "<h2><font color='#191970' size='6'><center>iFisher</center></font></h2>"
		+ ""
		+ "<center><font color='white' size='3'>All setting's can be configured in the GUI</font></center>"
		+ "<center><font color='white' size='3'>Just select your account and press ok.</font></center>"
		+ "</body>" + "</html>")
public class iFisher extends Script implements PaintListener,
		ServerMessageListener {

	final ScriptManifest props = getClass().getAnnotation(ScriptManifest.class);

	private long moneyGained, time, xpHour, levelIn, moneyHour;
	private String status = "Waiting", fishing, location2, fishComm;
	private long startTime;
	private long lastCheck, checkTime;

	private GUI GUI;
	private RSTile fishTile, bankTile;
	private RSTile[] toBank, toFish;
	private int fishPrice1, fishPrice2, fishPrice3;
	private int levelGained, catchesPerHour, startexp, fishSpot, fishId, fished, XPToGo;
	private boolean useAntiBan, dropTuna, drop, catherbyLoc, kjMode, paused, barbMode, paint, started, fishingGuild, useStiles;

	private int fishEquip, fishBait;
	private final int BAIT_NONE = -1, BAIT_BAIT = 313, BAIT_FEATHERS = 314;
	private final int GEAR_NET = 303, GEAR_ROD = 307, GEAR_FLYROD = 309,GEAR_CAGE = 301, GEAR_CCAGE = 13431, GEAR_HARPOON = 311,GEAR_BIGNET = 305;

	public boolean onStart(Map<String, String> args) {
		GUI = new GUI();
		GUI.setVisible(true);
		while (!started) {
			fished = 0;
			wait(10);
		}
		startTime = System.currentTimeMillis();
		return true;
	}

	public void onFinish() {
		GUI.setVisible(false);
	}

	private Color paintColor1 = new Color(25, 25, 112, 100);
	private Color paintColor2 = new Color(0, 0, 205, 100);

	private void drawButton(int x, int y, int w, int h, Graphics g) {
		g.setColor(new Color(0, 0, 0));
		g.drawRect(x, y, w, h);
		g.setColor(paintColor1);
		g.fillRect(x + 1, y + 7, w - 1, h - 7);
		g.setColor(paintColor2);
		g.fillRect(x + 1, y + 1, w - 1, h - 8);
	}

	private void paintBG(int x, int y, int w, int h, Graphics g) {
		g.setColor(new Color(0, 0, 0));
		g.drawRect(x, y, w, h);
		g.setColor(paintColor2);
		g.fillRect(x + 1, y + 1, w - 1, h - 42);
		g.setColor(paintColor1);
		g.fillRect(x + 1, y + 43, w - 1, h - 42);
	}

	private String insertCommas(String str) {
        if(str.length() < 4){
            return str;
        }
        return insertCommas(str.substring(0, str.length() - 3)) + "," + str.substring(str.length() - 3, str.length());
    }

	public void onRepaint(Graphics g) {
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		long millis = System.currentTimeMillis() - startTime;
		long hours = millis / (1000 * 60 * 60);
		millis -= hours * (1000 * 60 * 60);
		long minutes = millis / (1000 * 60);
		millis -= minutes * (1000 * 60);
		long seconds = millis / 1000;
		int xpGained;
		int xpToNext = skills.getXPToNextLevel(Constants.STAT_FISHING);
		if (startexp == 0)
			startexp = skills.getCurrentSkillExp(STAT_FISHING);
		xpGained = skills.getCurrentSkillExp(STAT_FISHING) - startexp;
		XPToGo = skills.getXPToNextLevel(STAT_FISHING);
		time = (System.currentTimeMillis() - startTime) / 1000;
		if (time > 0) {
			catchesPerHour = (int) ((fished * 60 * 60) / time);
			xpHour = (xpGained * 60 * 60) / time;
			levelIn = (long) (60 * 60 * (XPToGo / (double) xpHour));
			moneyHour = (moneyGained * 60 * 60) / time;
		}

		Mouse m = Bot.getClient().getMouse();
		Point p = new Point(m.x, m.y);

		String paint1 = Integer.toString(xpGained);
		String paint2 = Long.toString(xpHour);
		String paint3 = Long.toString(catchesPerHour);
		String paint4 = Integer.toString(fished);
		String paint5 = Long.toString(moneyGained);
		String paint6 = Long.toString(moneyHour);
		String paint7 = Long.toString(xpToNext);

		if (started && paint) {

			drawButton(6, 321, 68, 14, g);//Status Button
			drawButton(77, 321, 68, 14, g);//EXP Button
			drawButton(148, 321, 68, 14, g);//Profit Button

			if (isWithinBounds(p, 6, 321, 68, 14)) {
				paintBG(6, 237, 210, 84, g);
				g.setFont(new Font("Arial", Font.BOLD, 10));
				g.setColor(Color.WHITE);
				g.drawString("Status: " + status, 10, 252);
				g.drawString("Locstion: " + location2, 10, 267);
				g.drawString("Fishing: " + fishing, 10, 282);
				if (drop)
					g.drawString("Mode: Power Fishing", 10, 297);
				else
					g.drawString("Mode: Banking", 10, 297);
				g.drawString("Run Time: " + hours + ":" + minutes + ":" + seconds + "", 10, 312);

				g.setColor(new Color(255, 140, 0));
			} else
				g.setColor(Color.WHITE);
			g.setFont(new Font("Segoe UI", Font.BOLD, 10));
			g.drawString("- Status", 9, 332);

			if (isWithinBounds(p, 77, 321, 68, 14)) {
				paintBG(6, 237, 210, 84, g);
				g.setFont(new Font("Arial", Font.BOLD, 10));
				g.setColor(Color.WHITE);
				g.drawString("EXP Gained: " + insertCommas(paint1), 10, 252);
				g.drawString("EXP Hourly: " + insertCommas(paint2), 10, 267);
				g.drawString("EXP TNL: " + insertCommas(paint7), 10, 282);
				if (fished > 0)
					g.drawString("Level in: " + levelIn / 3600 + ":" + ((levelIn / 60 % 60 < 10) ? "0" : "") + levelIn	/ 60 % 60 + ":" + ((levelIn % 60 < 10) ? "0" : "") + levelIn % 60, 10, 297);
				else
					g.drawString("Level In: 0:0:0", 10, 297);
				g.drawString("Current Level: " + skills.getCurrentSkillLevel(STAT_FISHING) + " (" + levelGained + ")", 10, 312);

				g.setColor(new Color(255, 140, 0));
			} else
				g.setColor(Color.WHITE);
			g.setFont(new Font("Segoe UI", Font.BOLD, 10));
			g.drawString("- Experience", 80, 332);

			if (isWithinBounds(p, 148, 321, 68, 14)) {
				paintBG(6, 237, 210, 84, g);
				g.setFont(new Font("Arial", Font.BOLD, 10));
				g.setColor(Color.WHITE);
				g.drawString("", 10, 252);
				g.drawString("Fish Gained: " + insertCommas(paint4), 10, 267);
				g.drawString("Fish Hourly: " + insertCommas(paint3), 10, 282);
				if (!drop) {
					g.drawString("Gold Gained: " + insertCommas(paint5), 10, 297);
					g.drawString("Gold Hourly: " + insertCommas(paint6), 10, 312);
				} else {
					g.drawString("Gold Gained: N/A", 10, 297);
					g.drawString("Gold Hourly: N/A", 10, 312);
				}
				g.setColor(new Color(255, 140, 0));
			} else
				g.setColor(Color.WHITE);
			g.setFont(new Font("Segoe UI", Font.BOLD, 10));
			g.drawString("- Profit", 151, 332);

			g.setColor(new Color(0, 0, 0));
			g.drawRect(219, 321, 100, 14);
			g.setColor(new Color(255, 0, 0, 100));
			g.fillRect(220, 322, 99, 13);
			g.setColor(new Color(34, 139, 34, 100));
			g.fillRect(220, 322, skills.getPercentToNextLevel(STAT_FISHING), 13);
			g.setColor(Color.BLACK);
			g.drawString("   " + skills.getPercentToNextLevel(STAT_FISHING) + "% to next Level", 218, 332);
		}
	}

	public void serverMessageRecieved(ServerMessageEvent e) {
		String message = e.getMessage().toLowerCase();
		if (message.contains("advanced"))
			levelGained++;
		if (message.contains("you catch a")) {
			if (message.contains("bass"))
				moneyGained += fishPrice1;
			if (message.contains("mackerel"))
				moneyGained += fishPrice3;
			if (message.contains("cod"))
				moneyGained += fishPrice2;
			if (message.contains("shrimps"))
				moneyGained += fishPrice1;
			if (message.contains("anchovies"))
				moneyGained += fishPrice2;
			if (message.contains("lobster"))
				moneyGained += fishPrice1;
			if (message.contains("tuna") && !dropTuna)
				moneyGained += fishPrice1;
			if (message.contains("swordfish"))
				moneyGained += fishPrice2;
			if (message.contains("shark"))
				moneyGained += fishPrice1;
			if (message.contains("herring"))
				moneyGained = fishPrice1;
			if (message.contains("sardines"))
				moneyGained += fishPrice2;
			if (message.contains("trout"))
				moneyGained += fishPrice1;
			if (message.contains("salmon"))
				moneyGained += fishPrice2;
			if (message.contains("pike"))
				moneyGained += fishPrice1;
			fished++;
		}
	}

	private boolean useDepositBox(int id, int item) {
		RSObject depositbox = getNearestObjectByID(id);
		if (RSInterface.getInterface(11).isValid()) {
			atInterface(RSInterface.getInterface(11).getChild(15));
			wait(random(500, 700));
		}
		if (inventoryContains(item)) {
			atInventoryItem(item, "Use");
			wait(random(300, 500));
			if (depositbox != null) {
				atObject(depositbox, "-> Bank");
				wait(random(1000, 2000));
				if (RSInterface.getInterface(232).isValid()) {
					atInterface(RSInterface.getInterface(232).getChild(5));
					wait(random(500, 750));
				}
			}
			return true;
		}
		return false;
	}

	private void openBank() {
		if (useStiles) {
			bankStiles();
		} else if (kjMode) {
			useDepositBox(36788, fishId);
			useDepositBox(36788, 359);
		} else {
			RSNPC banker = getNearestNPCByID(494, 495, 496, 5912, 5913);
			if (banker != null) {
				if (!atNPC(banker, "Bank Banker"))
					setCameraRotation(getCameraAngle() + random(-90, 90));
				wait(random(500, 1000));
			}
		}
	}

	private void fish() {
		if (getMyPlayer().getAnimation() == -1 && !getMyPlayer().isMoving()) {
			RSNPC nSpot = getNearestNPCByID(fishSpot);
			if (nSpot == null) {
				if (catherbyLoc)
					walkTileMM(new RSTile(2847, 3422));
				wait(random(50, 150));
			}
			if (nSpot != null) {
				if (distanceTo(nSpot.getLocation()) > 4 || !nSpot.isOnScreen()) {
					RSTile destination = randomizeTile(nSpot.getLocation(), 2,
							2);
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

	private int walkFish() {
		if (takeBoatToKaramja())
			return random(5000, 6000);
		if (distanceTo(getDestination()) < random(9, 12) || distanceTo(getDestination()) > 40) {
			if (!walkPathMM(toFish))
				walkToClosestTile(toFish);
		}
		return random(50, 150);
	}

	private int walkBank() {
		if (takeBoatFromKaramja())
			return random(5000, 6000);
		if (dropTunas())
			return random(200, 300);
		if (fishingGuild) {
			if (distanceTo(getDestination()) < random(5, 8) || distanceTo(getDestination()) > 40) {
				if (!walkPathMM(toBank))
					walkToClosestTile(toBank, 1, 1);
			}
		} else {
			if (distanceTo(getDestination()) < random(8, 12) || distanceTo(getDestination()) > 40) {
				if (!walkPathMM(toBank))
					walkToClosestTile(toBank);
			}
		}
		return random(50, 150);
	}

	private boolean gearCheck() {
		if (kjMode && getInventoryCount(995) < 30) {
			log.severe("Not enough Gold to pay the fee.");
			return false;
		}
		if (!barbMode) {
			if (!inventoryContains(fishEquip)) {
				log.severe("Do NOT have primary fish gear!");
				return false;
			}
			if (fishBait != -1) {
				if (!inventoryContains(fishBait)) {
					log.severe("Do NOT have secondary fish gear!");
					return false;
				}
			}
		}
		return true;
	}

	private boolean inArea(int maxX, int minY, int minX, int maxY) {
		int x = getMyPlayer().getLocation().getX();
		int y = getMyPlayer().getLocation().getY();
		return x >= minX && x <= maxX && y >= minY && y <= maxY;
	}

	private boolean isWithinBounds(Point p, int x, int y, int w, int h) {
		return p.x > x && p.x < x + w && p.y > y && p.y < y + h;
	}

	private void waitToStop() {
		while (getMyPlayer().isMoving())
			wait(150);
	}

	public int loop() {
		if (!paused && started) {
			gearCheck();
			if (getEnergy() > random(70, 100) && getSetting(173) != 1)
				setRun(true);
			if (RSInterface.getInterface(INTERFACE_LEVELUP).isValid()) {
				atInterface(RSInterface.getInterface(INTERFACE_LEVELUP).getChild(3));
				return (random(2000, 3000));
			}
			switch (getState()) {
			case FISH:
				fish();
				break;
			case GOTO_FISH:
				if (distanceTo(getDestination()) < random(5, 8) && useStiles)
					walkPathMM(randomizePath(toFish,2,2));
				else
					walkFish();
				break;
			case GOTO_BANK:
				if (distanceTo(getDestination()) < random(5, 8) && useStiles)
					walkPathMM(randomizePath(toBank,2,2));
				else
					walkBank();
				break;
			case IDLE:
				antiBan();
				break;
			case OPEN_BANK:
				openBank();
				break;
			case USE_BANK:
				if (barbMode)
					bank.depositAll();
				else
					bank.depositAllExcept(fishEquip, fishBait);
				break;
			case DROP:
				dropAllExcept_(fishEquip, fishBait);
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
			status = "Droping Fish";
			return State.DROP;
		}
		if (isInventoryFull()) {
			if (bank.isOpen() && !kjMode) {
				status = "Depositing Fish";
				return State.USE_BANK;
			} else if (distanceTo(bankTile) < 8 && !getMyPlayer().isMoving()) {
				status = "Opening Bank";
				return State.OPEN_BANK;
			} else {
				status = "Walking To Bank";
				return State.GOTO_BANK;
			}
		} else {
			if (getMyPlayer().getAnimation() != -1) {
				return State.IDLE;
			} else if (catherbyLoc && inArea(2862, 3422, 2834, 3434)
					|| distanceTo(fishTile) < 10) {
				status = "Fishing";
				return State.FISH;
			} else {
				status = "Walking To Fishing Area";
				return State.GOTO_FISH;
			}
		}
	}

	private void dropAllExcept_(final int... items) {
		try {
			for (int c = 0; c < 4; c++) {
				for (int r = 0; r < 7; r++) {
					boolean found = false;
					for (int i = 0; i < items.length && !found; i++)
						found = items[i] == getInventoryArray()[c + r * 4];
					if (!found)
						dropItem(c, r);
				}
			}
		} catch (final Exception e) {
			log.severe("Prevented fatal error.");
		}
	}

	private boolean bankStiles() {
		RSNPC stiles = getNearestNPCByID(11267);
		if (stiles != null) {
			if (atNPC(stiles, "Exchange")) {
				wait(random(500, 1000));
				return true;
			}
			if (RSInterface.getInterface(241).isValid()) {
				atInterface(RSInterface.getInterface(241).getChild(5));
				wait(random(200, 300));
				return true;
			}
		}
		return false;
	}

	private boolean takeBoatFromKaramja() {
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
			if (RSInterface.getInterface(64).getText().toLowerCase().contains(
					"ok.")) {
				atInterface(RSInterface.getInterface(64).getChild(5));
				wait(random(7000, 8000));
				return true;
			}
			if (RSInterface.getInterface(64).isValid()) {
				atInterface(RSInterface.getInterface(64).getChild(5));
				wait(random(200, 300));
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
		}
		return false;
	}

	private boolean takeBoatToKaramja() {
		try {
			int[] seamanIDs = new int[] { 376, 377, 378 };
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
		}
		return false;
	}

	private boolean dropTunas() {
		int[] keep = new int[] { 311, 301, 995, 377, 371 };
		if (dropTuna) {
			if (inventoryContains(359)) {
				dropAllExcept_(keep);
				dropAllExcept_(keep);
				return true;
			}
		}
		return false;
	}

	private void antiBan() {
		if (random(0, 10) == 0) {
			if (useAntiBan) {
				final char[] LR = new char[] { KeyEvent.VK_LEFT,
						KeyEvent.VK_RIGHT };
				final char[] UD = new char[] { KeyEvent.VK_DOWN, KeyEvent.VK_UP };
				final char[] LRUU = new char[] { KeyEvent.VK_LEFT,
						KeyEvent.VK_RIGHT, KeyEvent.VK_UP, KeyEvent.VK_UP };
				final int random2 = random(0, 2);
				final int random1 = random(0, 2);
				final int random4 = random(0, 4);
				if (random(0, 5) == 0) {
					Bot.getInputManager().pressKey(LR[random1]);
					wait(random(100, 400));
					Bot.getInputManager().pressKey(UD[random2]);
					wait(random(300, 600));
					Bot.getInputManager().releaseKey(UD[random2]);
					wait(random(100, 400));
					Bot.getInputManager().releaseKey(LR[random1]);
				} else {
					Bot.getInputManager().pressKey(LRUU[random4]);
					if (random4 > 1) {
						wait(random(300, 600));
					} else {
						wait(random(500, 900));
					}
					Bot.getInputManager().releaseKey(LRUU[random4]);
				}
				if (random(0, 2) == 0) {
					int x = random(0, 750);
					int y = random(0, 500);
					moveMouse(x, y);
				}
				if (random(0, 15) == 0) {
					openTab(random(0, 15));
					wait(random(3000, 5000));
				}
				if (System.currentTimeMillis() - lastCheck >= checkTime && getInventoryCount() <= 20) {
					lastCheck = System.currentTimeMillis();
					checkTime = random(60000, 180000);
					if (getCurrentTab() != 1) {
						openTab(1);
					}
					moveMouse(677, 268, 50, 20);
					wait(random(5000, 8000));
				}
			}
		} else {
			wait(random(200, 2000));
		}
	}

	private class GUI extends javax.swing.JFrame {

		private static final long serialVersionUID = 1L;
		private final File settingsFile = new File(new File(GlobalConfiguration.Paths.getSettingsDirectory()), "iFisher.txt");

	    private GUI() {
	        initComponents();
	    }

	    private void initComponents() {

	        startButton = new javax.swing.JButton();
	        pauseButton = new javax.swing.JButton();
	        applyButton = new javax.swing.JButton();
	        jLabel1 = new javax.swing.JLabel();
	        locationCBox = new javax.swing.JComboBox();
	        fishingCBox = new javax.swing.JComboBox();
	        jLabel2 = new javax.swing.JLabel();
	        dropTunaBox = new javax.swing.JCheckBox();
	        dropBox = new javax.swing.JCheckBox();
	        barbBox = new javax.swing.JCheckBox();
	        paintBox = new javax.swing.JCheckBox();
	        antibanBox = new javax.swing.JCheckBox();

	        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
	        setAlwaysOnTop(true);
	        setResizable(false);
	        setTitle("iFisher ");

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

	        jLabel1.setText("Locations:");

	        locationCBox.setMaximumRowCount(5);
	        locationCBox.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Al-Kharid", "Barbarian Village", "Catherby", "Draynor Village", "Fishing Guild", "Lumbridge", "Karamja", "Karamja [Stiles]", "Seers' Village"}));
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

	        jLabel2.setText("Fishing:");

	        dropTunaBox.setText("Anti-Tuna");

	        dropBox.setText("Power Fishing");

	        barbBox.setText("Barbarian Mode");

	        paintBox.setSelected(true);
	        paintBox.setText("Paint Progress");

	        antibanBox.setSelected(true);
	        antibanBox.setText("Use Anti-Ban");

	        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
	        getContentPane().setLayout(layout);
	        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(barbBox).addGroup(layout.createSequentialGroup().addComponent(startButton).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(pauseButton).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(applyButton)).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(locationCBox, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabel1).addComponent(dropTunaBox).addComponent(dropBox)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(antibanBox).addComponent(paintBox).addComponent(jLabel2).addComponent(fishingCBox, 0, 169, Short.MAX_VALUE)))).addContainerGap()));
	        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel1).addComponent(jLabel2)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(locationCBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(fishingCBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(antibanBox).addComponent(dropBox)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(paintBox).addComponent(dropTunaBox)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(barbBox).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(startButton).addComponent(pauseButton).addComponent(applyButton)).addContainerGap()));
			BufferedReader in;
			try {
				in = new BufferedReader(new FileReader(settingsFile));
				String[] settings = new String[7];
				String line;
				for (int i = 0; i < settings.length && (line = in.readLine()) != null; i++)
					settings[i] = line;
				try {
					locationCBox.setSelectedIndex(Integer.parseInt(settings[1]));
					fishingCBox.setSelectedIndex(Integer.parseInt(settings[0]));
					barbBox.setSelected(settings[2].equals("1"));
					dropBox.setSelected(settings[3].equals("1"));
					dropTunaBox.setSelected(settings[4].equals("1"));
					paintBox.setSelected(settings[5].equals("1"));
					antibanBox.setSelected(settings[6].equals("1"));
					makeChanges();
				} catch (Exception e) { }
				in.close();
			} catch (IOException e) {
				log("No Settings File Found!");
			}
			makeChanges();
	        pack();
	    }

	    private void setFishing(String f, String fc, int fs, int fe, int fb) {
			fishing = f;
			fishComm = fc;
			fishSpot = fs;
			fishEquip = fe;
			fishBait = fb;
		}

		private void makeChanges() {
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
				toBank = new RSTile[] { new RSTile(3104, 3431),
						new RSTile(3100, 3434), new RSTile(3094, 3439),
						new RSTile(3091, 3445), new RSTile(3090, 3451),
						new RSTile(3090, 3457), new RSTile(3087, 3463),
						new RSTile(3093, 3464), new RSTile(3099, 3474),
						new RSTile(3100, 3480), new RSTile(3093, 3484),
						new RSTile(3090, 3490), new RSTile(3094, 3491) };
				toFish = reversePath(toBank);

				if (fish.equals("Trout & Salmon")) {
					setFishing("Trout & Salmon", "Lure", 328, GEAR_FLYROD,
							BAIT_FEATHERS);
				}
				if (fish.equals("Pike")) {
					setFishing("Pike", "Bait", 328, GEAR_ROD, BAIT_BAIT);
				}
			}
			if (location.equals("Lumbridge")) {
				location2 = "Lumbridge";
				drop = true;
				if (fish.equals("Trout & Salmon")) {
					fishTile = new RSTile(3240, 3247);
					setFishing("Trout & Salmon", "Lure", 329, GEAR_FLYROD,
							BAIT_FEATHERS);
				}
				if (fish.equals("Pike")) {
					fishTile = new RSTile(3240, 3247);
					setFishing("Pike", "Bait", 329, GEAR_ROD, BAIT_BAIT);
				}
				if (fish.equals("Shrimp & Anchovies")) {
					fishTile = new RSTile(3240, 3152);
					setFishing("Shrimp & Anchovies", "Net", 4908, GEAR_NET,
							BAIT_NONE);
				}
				if (fish.equals("Herring & Sardines")) {
					fishTile = new RSTile(3240, 3152);
					setFishing("Herring & Sardines", "Bait", 4908, GEAR_ROD,
							BAIT_BAIT);
				}
				if (fish.equals("Crayfish")) {
					fishTile = new RSTile(3256, 3205);
					setFishing("Crayfish", "Cage", 6267, GEAR_CCAGE, BAIT_NONE);
				}
				toFish = generateFixedPath(fishTile);
			}
			if (location.equals("Draynor Village")) {
				location2 = "Draynor";
				bankTile = new RSTile(3093, 3243);
				fishTile = new RSTile(3087, 3228);
				toBank = new RSTile[] { new RSTile(3086, 3238),
						new RSTile(3092, 3243) };
				toFish = new RSTile[] { new RSTile(3086, 3238),
						new RSTile(3088, 3230) };
				if (fish.equals("Shrimp & Anchovies")) {
					setFishing("Shrimp & Anchovies", "Net", 327, GEAR_NET,
							BAIT_NONE);
				}
				if (fish.equals("Herring & Sardines")) {
					setFishing("Herring & Sardines", "Bait", 327, GEAR_ROD,
							BAIT_BAIT);
				}
			}
			if (location.equals("Al-Kharid")) {
				location2 = "Al-Kharid";
				bankTile = new RSTile(3269, 3166);
				fishTile = new RSTile(3271, 3145);
				toBank = new RSTile[] { new RSTile(3271, 3144),
						new RSTile(3276, 3157), new RSTile(3270, 3167) };
				toFish = reversePath(toBank);
				if (fish.equals("Shrimp & Anchovies")) {
					setFishing("Shrimp & Anchovies", "Net", 330, GEAR_NET,
							BAIT_NONE);
				}
				if (fish.equals("Herring & Sardines")) {
					setFishing("Herring & Sardines", "Bait", 330, GEAR_ROD,
							BAIT_NONE);
				}
			}
			if (location.equals("Fishing Guild")) {
				fishingGuild = true;
				location2 = "Fishing Guild";
				fishTile = new RSTile(2603, 3416);
				bankTile = new RSTile(2589, 3418);
				toBank = new RSTile[] { new RSTile(2594, 3415),
						new RSTile(2588, 3419) };
				toFish = new RSTile[] { new RSTile(2599, 3421) };
				if (fish.equals("Sharks")) {
					setFishing("Sharks", "Harpoon", 313, GEAR_HARPOON,
							BAIT_NONE);
				}
				if (fish.equals("Swordfish & Tuna")) {
					setFishing("Swordfish & Tuna", "Harpoon", 312,
							GEAR_HARPOON, BAIT_NONE);
				}
				if (fish.equals("Lobster")) {
					setFishing("Lobster", "Cage", 312, GEAR_CAGE, BAIT_NONE);
				}
				if (fish.equals("Bass & Cod & Mackerel")) {
					setFishing("Bass & Cod & Mackerel", "Net", 313,
							GEAR_BIGNET, BAIT_NONE);
				}
			} else {
				fishingGuild = false;
			}
			if (location.equals("Catherby")) {
				location2 = "Catherby";
				catherbyLoc = true;
				fishTile = new RSTile(2847, 3430);
				bankTile = new RSTile(2809, 3440);
				toBank = new RSTile[] { new RSTile(2853, 3428),
						new RSTile(2849, 3431), new RSTile(2843, 3433),
						new RSTile(2837, 3435), new RSTile(2830, 3436),
						new RSTile(2824, 3437), new RSTile(2817, 3436),
						new RSTile(2811, 3436), new RSTile(2809, 3440) };
				toFish = new RSTile[] { new RSTile(2811, 3435),
						new RSTile(2818, 3436), new RSTile(2824, 3437),
						new RSTile(2830, 3436), new RSTile(2835, 3435),
						new RSTile(2837, 3432) };
				if (fish.equals("Sharks")) {
					setFishing("Sharks", "Harpoon", 322, GEAR_HARPOON,
							BAIT_NONE);
				}
				if (fish.equals("Swordfish & Tuna")) {
					setFishing("Swordfish & Tuna", "Harpoon", 321,
							GEAR_HARPOON, BAIT_NONE);
				}
				if (fish.equals("Lobster")) {
					setFishing("Lobster", "Cage", 321, GEAR_CAGE, BAIT_NONE);
				}
				if (fish.equals("Shrimp & Anchovies")) {
					setFishing("Shrimp & Anchovies", "Net", 320, GEAR_NET,
							BAIT_NONE);
				}
				if (fish.equals("Herring & Sardines")) {
					setFishing("Herring & Sardines", "Bait", 320, GEAR_ROD,
							BAIT_BAIT);
				}
				if (fish.equals("Bass & Cod & Mackerel")) {
					setFishing("Bass & Cod & Mackerel", "Net", 322,
							GEAR_BIGNET, BAIT_NONE);
				}
			} else {
				catherbyLoc = false;
			}
			if (location.equals("Karamja [Stiles]")) {
				location2 = "Karamja";
				useStiles = true;
				bankTile = new RSTile(2851, 3142);
				fishTile = new RSTile(2925, 3177);
				toFish = new RSTile[] {new RSTile(2851, 3142), new RSTile(2861, 3147),
						new RSTile(2870, 3151), new RSTile(2878, 3157), new RSTile(2886, 3163),
						new RSTile(2893, 3169), new RSTile(2903, 3171), new RSTile(2913, 3171),
						new RSTile(2922, 3172), new RSTile(2924, 3179)};
				toBank = reversePath(toFish);

				if (fish.equals("Lobster")) {
					fishId = 377;
					setFishing("Lobster", "Cage", 324, GEAR_CAGE, BAIT_NONE);
				}
				if (fish.equals("Swordfish & Tuna")) {
					fishId = 371;
					setFishing("Swordfish & Tuna", "Harpoon", 324,
							GEAR_HARPOON, BAIT_NONE);
				}
			} else  {
				useStiles = false;
			}
			if (location.equals("Karamja")) {
				location2 = "Karamja";
				kjMode = true;
				bankTile = new RSTile(3047, 3235);
				fishTile = new RSTile(2925, 3177);
				toFish = new RSTile[] { new RSTile(3046,3236), new RSTile(3037,3236), new RSTile(3028,3232),
						new RSTile(3028,3222), new RSTile(2952,3146), new RSTile(2941,3146), new RSTile(2932,3149),
						new RSTile(2923,3150), new RSTile(2916,3156), new RSTile(2922,3163), new RSTile(2924,3173)};
				toBank = reversePath(toFish);
				if (fish.equals("Shrimp & Anchovies")) {
					setFishing("Shrimp & Anchovies", "Net", 323, GEAR_NET,
							BAIT_NONE);
				}
				if (fish.equals("Herring & Sardines")) {
					setFishing("Herring & Sardines", "Bait", 323, GEAR_ROD,
							BAIT_BAIT);
				}
				if (fish.equals("Lobster")) {
					fishId = 377;
					setFishing("Lobster", "Cage", 324, GEAR_CAGE, BAIT_NONE);
				}
				if (fish.equals("Swordfish & Tuna")) {
					fishId = 371;
					setFishing("Swordfish & Tuna", "Harpoon", 324,
							GEAR_HARPOON, BAIT_NONE);
				}
			} else {
				kjMode = false;
			}
			if (location.equals("Seers' Village")) {
				location2 = "Seers' Village";
				bankTile = new RSTile(2726, 3493);
				fishTile = new RSTile(2722, 3529);
				toBank = new RSTile[] { new RSTile(2722, 3529),
						new RSTile(2727, 3528), new RSTile(2732, 3526),
						new RSTile(2736, 3521), new RSTile(2740, 3514),
						new RSTile(2740, 3510), new RSTile(2742, 3504),
						new RSTile(2738, 3495), new RSTile(2735, 3490),
						new RSTile(2728, 3485), new RSTile(2725, 3491) };
				toFish = reversePath(toBank);
				if (fish.equals("Trout & Salmon")) {
					setFishing("Trout & Salmon", "Lure", 315, GEAR_FLYROD,
							BAIT_FEATHERS);
				}
				if (fish.equals("Pike")) {
					setFishing("Pike", "Bait", 315, GEAR_ROD, BAIT_BAIT);
				}
			}
			PrintWriter out;
			try {
				out = new PrintWriter(new FileWriter(settingsFile));
				String[] settings = { "" + fishingCBox.getSelectedIndex(),
						"" + locationCBox.getSelectedIndex(),
						barbBox.isSelected() ? "1" : "0",
						dropBox.isSelected() ? "1" : "0",
						dropTunaBox.isSelected() ? "1" : "0",
						paintBox.isSelected() ? "1" : "0",
						antibanBox.isSelected() ? "1" : "0" };
				for (String line : settings) {
					out.println(line);
				}
				out.close();
			} catch (IOException e) {
				log.severe("Unable to save settings!");
			}
			getFishPrices();
		}

		private void getFishPrices() {
			String fish = (String) fishingCBox.getSelectedItem();
			if (fish.equals("Bass & Cod & Mackerel")) {
				GEItemInfo rawFish = grandExchange.loadItemInfo(363);
				fishPrice1 = rawFish.getMarketPrice();
				GEItemInfo rawFish2 = grandExchange.loadItemInfo(341);
				fishPrice2 = rawFish2.getMarketPrice();
				GEItemInfo rawFish3 = grandExchange.loadItemInfo(353);
				fishPrice3 = rawFish3.getMarketPrice();
			}
			if (fish.equals("Shrimp & Anchovies")) {
				GEItemInfo rawFish = grandExchange.loadItemInfo(317);
				fishPrice1 = rawFish.getMarketPrice();
				GEItemInfo rawFish2 = grandExchange.loadItemInfo(321);
				fishPrice2 = rawFish2.getMarketPrice();
			}
			if (fish.equals("Lobster")) {
				GEItemInfo rawFish = grandExchange.loadItemInfo(377);
				fishPrice1 = rawFish.getMarketPrice();
			}
			if (fish.equals("Swordfish & Tuna")) {
				GEItemInfo rawFish = grandExchange.loadItemInfo(359);
				fishPrice1 = rawFish.getMarketPrice();
				GEItemInfo rawFish2 = grandExchange.loadItemInfo(371);
				fishPrice2 = rawFish2.getMarketPrice();
			}
			if (fish.equals("Herring & Sardines")) {
				GEItemInfo rawFish = grandExchange.loadItemInfo(345);
				fishPrice1 = rawFish.getMarketPrice();
				GEItemInfo rawFish2 = grandExchange.loadItemInfo(345);
				fishPrice2 = rawFish2.getMarketPrice();
			}
			if (fish.equals("Trout & Salmon")) {
				GEItemInfo rawFish = grandExchange.loadItemInfo(335);
				fishPrice1 = rawFish.getMarketPrice();
				GEItemInfo rawFish2 = grandExchange.loadItemInfo(331);
				fishPrice2 = rawFish2.getMarketPrice();
			}
			if (fish.equals("Pike")) {
				GEItemInfo rawFish = grandExchange.loadItemInfo(349);
				fishPrice1 = rawFish.getMarketPrice();
			}
			if (fish.equals("Sharks")) {
				GEItemInfo rawFish = grandExchange.loadItemInfo(383);
				fishPrice1 = rawFish.getMarketPrice();
			}
		}

		private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {
			if (!started) {
				started = true;
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
				fishingCBox.setModel(new javax.swing.DefaultComboBoxModel(
				new String[] { "Crayfish",
				                "Shrimp & Anchovies",
								"Herring & Sardines", "Trout & Salmon",
								"Pike" }));
				dropBox.setEnabled(false);
				dropBox.setSelected(true);
			} else {
				dropBox.setEnabled(true);
				dropBox.setSelected(false);
			}
			if (location.equals("Al-Kharid")) {
				fishingCBox.setModel(new javax.swing.DefaultComboBoxModel(
						new String[] { "Shrimp & Anchovies",
								"Herring & Sardines" }));
			}
			if (location.equals("Barbarian Village")
					|| location.equals("Seers' Village")) {
				fishingCBox.setModel(new javax.swing.DefaultComboBoxModel(
						new String[] { "Trout & Salmon", "Pike" }));
			}
			if (location.equals("Draynor Village")) {
				fishingCBox.setModel(new javax.swing.DefaultComboBoxModel(
						new String[] { "Shrimp & Anchovies",
								"Herring & Sardines" }));
			}
			if (location.equals("Karamja")) {
				fishingCBox.setModel(new javax.swing.DefaultComboBoxModel(
						new String[] { "Shrimp & Anchovies",
								"Herring & Sardines", "Lobster",
								"Swordfish & Tuna" }));
			}
			if (location.equals("Karamja [Stiles]")) {
				fishingCBox.setModel(new javax.swing.DefaultComboBoxModel(
						new String[] { "Lobster", "Swordfish & Tuna" }));
			}
			if (location.equals("Catherby")) {
				fishingCBox.setModel(new javax.swing.DefaultComboBoxModel(
						new String[] { "Shrimp & Anchovies",
								"Herring & Sardines", "Bass & Cod & Mackerel",
								"Lobster", "Swordfish & Tuna", "Sharks" }));
			}
			if (location.equals("Fishing Guild")) {
				fishingCBox.setModel(new javax.swing.DefaultComboBoxModel(
						new String[] { "Bass & Cod & Mackerel", "Lobster",
								"Swordfish & Tuna", "Sharks" }));
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
	    private javax.swing.JLabel jLabel1;
	    private javax.swing.JLabel jLabel2;
	    private javax.swing.JComboBox locationCBox;
	    private javax.swing.JCheckBox paintBox;
	    private javax.swing.JButton pauseButton;
	    private javax.swing.JButton startButton;
	}
}