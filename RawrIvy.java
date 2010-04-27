import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.*;

import java.awt.event.KeyEvent;

import org.rsbot.bot.Bot;
import org.rsbot.script.*;
import org.rsbot.script.wrappers.*;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.event.events.ServerMessageEvent;

@ScriptManifest(authors = {"RawR"}, category = "Woodcutting", name = "Rawr Ivy Chopper", version = 1.61,
		description =
				"<html><body>" +
						"<center><h2>RawR Ivy Chopper</h2></center>" +
						"This script supports all Ivy cutting locations. <br />" +
						"When starting, face the Ivy and the bot will do the rest.<br /><br />" +
						"<b>Chopping location:</b> <select name='location'><option>Varrock Palace</option><option>Varrock Wall</option><option>N Fally</option><option>S Fally</option><option>Taverly</option><option>Ardougne</option><option>Yanille</option><option>CWars</option></select><br /><br />" +
						"<b>antiBan:</b> <input type='checkbox' name='antiBan' value='true'><br />" +
						"<font size='3'><b>Note:</b> This is extra antiBan, there's one already built in.</font>" +
						"</body></html>")
public class RawrIvy extends Script implements PaintListener, ServerMessageListener {

	//MULTI-THREADING
	RunAntiBan antiBan;
	Thread t;
	//VARIABLES
	public String useAntiBan;
	public String ivyLocation;
	private String STATE = "Loading.";
	private int AMOUNT_CHOPPED;
	private int[] NEST_ID = {5070, 5071, 5072, 5073, 5074, 5075, 5076, 7413, 11966};
	private int[] IVY_ID = {46318, 46320, 46322, 46324};

	//PAINT VARIABLES
	public long startTime = System.currentTimeMillis();
	public long waitTimer = System.currentTimeMillis();
	public int START_XP;
	public int START_LVL;

	public boolean onStart(Map<String, String> args) {
		//DECLARING VARIABLES
		antiBan = new RunAntiBan();
		t = new Thread(antiBan);
		AMOUNT_CHOPPED = 0;
		startTime = System.currentTimeMillis();
		waitTimer = System.currentTimeMillis();
		ivyLocation = args.get("location");
		useAntiBan = args.get("antiBan");
		return true;
	}

	private double getVersion() {
		return getClass().getAnnotation(ScriptManifest.class).version();
	}

	@Override
	public int getMouseSpeed() {
		return random(6, 8);
	}

	public void setCamera() {
		final int curZ = Bot.getClient().getCamPosZ();
		if (curZ <= -950 && curZ >= -1050) {
			return;
		} else {
			final char key = (char) (curZ < -1000 ? KeyEvent.VK_DOWN : KeyEvent.VK_UP);
			input.pressKey(key);
			final int finalZ = -1000 + random(-50, 51);
			while (key == (char) KeyEvent.VK_DOWN ? Bot.getClient().getCamPosZ() < finalZ : Bot.getClient().getCamPosZ() > finalZ) {
				wait(random(10, 20));
			}
			input.releaseKey(key);
		}
	}

	public int gatherNest() {
		RSItemTile birdNest = getGroundItemByID(NEST_ID);
		if (birdNest != null && !isInventoryFull()) {
			STATE = "Nest.";
			atTile(birdNest, "Take ");
			wait(random(1000, 1500));
		}
		return 100;
	}

	public boolean atIvy(final RSObject tree, final String action) {
		try {
			//North
			if (ivyLocation.equals("S Fally") || ivyLocation.equals("CWars")) {
				RSTile loc1 = tree.getLocation();
				RSTile loc4 = new RSTile(loc1.getX(), loc1.getY() + 1);
				final Point screenLoc = Calculations.tileToScreen(loc4.getX(), loc4.getY(), 10);
				if (screenLoc.x == -1 || screenLoc.y == -1) {
					return false;
				}
				moveMouse(screenLoc, 3, 3);
				wait(random(200, 300));
			}
			//South
			if (ivyLocation.equals("Varrock Palace") || ivyLocation.equals("N Fally") || ivyLocation.equals("Yanille")) {
				RSTile loc1 = tree.getLocation();
				RSTile loc4 = new RSTile(loc1.getX(), loc1.getY() - 1);
				final Point screenLoc = Calculations.tileToScreen(loc4.getX(), loc4.getY(), 10);
				if (screenLoc.x == -1 || screenLoc.y == -1) {
					return false;
				}
				moveMouse(screenLoc, 3, 3);
				wait(random(200, 300));
			}
			//East
			if (ivyLocation.equals("Varrock Wall") || ivyLocation.equals("Taverly")) {
				RSTile loc1 = tree.getLocation();
				RSTile loc4 = new RSTile(loc1.getX() + 1, loc1.getY());
				final Point screenLoc = Calculations.tileToScreen(loc4.getX(), loc4.getY(), 10);
				if (screenLoc.x == -1 || screenLoc.y == -1) {
					return false;
				}
				moveMouse(screenLoc, 3, 3);
				wait(random(200, 300));
			}
			//West
			if (ivyLocation.equals("Ardougne")) {
				RSTile loc1 = tree.getLocation();
				RSTile loc4 = new RSTile(loc1.getX() - 1, loc1.getY());
				final Point screenLoc = Calculations.tileToScreen(loc4.getX(), loc4.getY(), 10);
				if (screenLoc.x == -1 || screenLoc.y == -1) {
					return false;
				}
				moveMouse(screenLoc, 3, 3);
				wait(random(200, 300));
			}
			return atMenu(action);
		} catch (final Exception e) {
			log("Small problem...");
			return false;
		}
	}

	public int loop() {
		setCamera();
		gatherNest();

		if (useAntiBan != null) {
			if (!t.isAlive()) {
				t.start();
				wait(500);
				log("antiBan has been initialized! You're now human-like.");
			}
		}
		//VARROCK PALACE -
		if (ivyLocation.equals("Varrock Palace")) {
			RSTile[] varrockPalace = {new RSTile(3219, 3498), new RSTile(3218, 3498), new RSTile(3217, 3498), new RSTile(3216, 3498)};
			RSObject Ivy = getNearestIvyByID(varrockPalace, IVY_ID);
			if (Ivy != null && distanceTo(Ivy) < 10 && (System.currentTimeMillis() - waitTimer) > random(1850, 1950)) {
				STATE = "Looking.";
				setCompass('s');
				atIvy(Ivy, "Chop Ivy");
				wait(random(2000, 2500));
			}
			if (Ivy != null && getMyPlayer().getAnimation() != -1) {
				STATE = "Chopping.";
				waitTimer = System.currentTimeMillis();
				performHumanAction();
			}
		}
		//VARROCK WALL -
		if (ivyLocation.equals("Varrock Wall")) {
			RSTile[] varrockWall = {new RSTile(3233, 3461), new RSTile(3233, 3460), new RSTile(3233, 3459), new RSTile(3233, 3457), new RSTile(3233, 3456)};
			RSObject Ivy = getNearestIvyByID(varrockWall, IVY_ID);
			if (Ivy != null && distanceTo(Ivy) < 10 && (System.currentTimeMillis() - waitTimer) > random(1850, 1950)) {
				STATE = "Looking for Ivy.";
				setCompass('e');
				atIvy(Ivy, "Chop Ivy");
				wait(random(2000, 2500));
			}
			if (Ivy != null && getMyPlayer().getAnimation() != -1) {
				STATE = "Chopping.";
				waitTimer = System.currentTimeMillis();
				performHumanAction();
			}
		}
		//N FALLY -
		if (ivyLocation.equals("N Fally")) {
			RSTile[] nFally = {new RSTile(3011, 3392), new RSTile(3012, 3392), new RSTile(3014, 3392), new RSTile(3015, 3392), new RSTile(3016, 3392), new RSTile(3017, 3392), new RSTile(3018, 3392)};
			RSObject Ivy = getNearestIvyByID(nFally, IVY_ID);
			if (Ivy != null && distanceTo(Ivy) < 10 && (System.currentTimeMillis() - waitTimer) > random(1850, 1950)) {
				STATE = "Looking for Ivy.";
				setCompass('s');
				atIvy(Ivy, "Chop Ivy");
				wait(random(2000, 2500));
			}
			if (Ivy != null && getMyPlayer().getAnimation() != -1) {
				STATE = "Chopping.";
				waitTimer = System.currentTimeMillis();
				performHumanAction();
			}
		}
		//S FALLY -
		if (ivyLocation.equals("S Fally")) {
			RSTile[] sFally = {new RSTile(3052, 3328), new RSTile(3051, 3328), new RSTile(3049, 3328), new RSTile(3048, 3328), new RSTile(3047, 3328), new RSTile(3045, 3328), new RSTile(3044, 3328)};
			RSObject Ivy = getNearestIvyByID(sFally, IVY_ID);
			if (Ivy != null && distanceTo(Ivy) < 10 && (System.currentTimeMillis() - waitTimer) > random(1850, 1950)) {
				STATE = "Looking for Ivy.";
				setCompass('n');
				atIvy(Ivy, "Chop Ivy");
				wait(random(2000, 2500));
			}
			if (Ivy != null && getMyPlayer().getAnimation() != -1) {
				STATE = "Chopping.";
				waitTimer = System.currentTimeMillis();
				performHumanAction();
			}
		}
		//TAVERLY -
		if (ivyLocation.equals("Taverly")) {
			RSTile[] taverly = {new RSTile(2943, 3420), new RSTile(2943, 3419), new RSTile(2943, 3418), new RSTile(2943, 3417), new RSTile(2943, 3416)};
			RSObject Ivy = getNearestIvyByID(taverly, IVY_ID);
			if (Ivy != null && distanceTo(Ivy) < 10 && (System.currentTimeMillis() - waitTimer) > random(1850, 1950)) {
				STATE = "Looking for Ivy.";
				setCompass('e');
				atIvy(Ivy, "Chop Ivy");
				wait(random(2000, 2500));
			}
			if (Ivy != null && getMyPlayer().getAnimation() != -1) {
				STATE = "Chopping.";
				waitTimer = System.currentTimeMillis();
				performHumanAction();
			}
		}
		//ARDOUGNE -
		if (ivyLocation.equals("Ardougne")) {
			RSTile[] ardougne = {new RSTile(2622, 3304), new RSTile(2622, 3305), new RSTile(2622, 3307), new RSTile(2622, 3308), new RSTile(2622, 3310)};
			RSObject Ivy = getNearestIvyByID(ardougne, IVY_ID);
			if (Ivy != null && distanceTo(Ivy) < 10 && (System.currentTimeMillis() - waitTimer) > random(1850, 1950)) {
				STATE = "Looking for Ivy.";
				setCompass('w');
				atIvy(Ivy, "Chop Ivy");
				wait(random(2000, 2500));
			}
			if (Ivy != null && Ivy != null && getMyPlayer().getAnimation() != -1) {
				STATE = "Chopping.";
				waitTimer = System.currentTimeMillis();
				performHumanAction();
			}
		}
		//YANILLE -
		if (ivyLocation.equals("Yanille")) {
			RSTile[] yanille = {new RSTile(2597, 3111), new RSTile(2596, 3111), new RSTile(2595, 3111), new RSTile(2593, 3111), new RSTile(2592, 3111), new RSTile(2591, 3111)};
			RSObject Ivy = getNearestIvyByID(yanille, IVY_ID);
			if (Ivy != null && distanceTo(Ivy) < 10 && (System.currentTimeMillis() - waitTimer) > random(1850, 1950)) {
				STATE = "Looking for Ivy.";
				setCompass('s');
				atIvy(Ivy, "Chop Ivy");
				wait(random(2000, 2500));
			}
			if (Ivy != null && getMyPlayer().getAnimation() != -1) {
				STATE = "Chopping.";
				waitTimer = System.currentTimeMillis();
				performHumanAction();
			}
		}
		//CWARS -
		if (ivyLocation.equals("CWars")) {
			RSTile[] cwars = {new RSTile(2430, 3068), new RSTile(2429, 3068), new RSTile(2428, 3068), new RSTile(2426, 3068), new RSTile(2425, 3068), new RSTile(2424, 3068), new RSTile(2423, 3068)};
			RSObject Ivy = getNearestIvyByID(cwars, IVY_ID);
			if (Ivy != null && distanceTo(Ivy) < 10 && (System.currentTimeMillis() - waitTimer) > random(1850, 1950)) {
				STATE = "Looking for Ivy.";
				setCompass('n');
				atIvy(Ivy, "Chop Ivy");
				wait(random(2000, 3000));

			}
			if (Ivy != null && getMyPlayer().getAnimation() != -1) {
				STATE = "Chopping.";
				waitTimer = System.currentTimeMillis();
				performHumanAction();
			}
		}
		return 100;
	}

	private void performHumanAction() {
		int randomNum = random(1, 30);
		int r = random(1, 35);
		if (randomNum == 6) {
			if (r == 1) {
				if (getCurrentTab() != Constants.TAB_STATS) {
					openTab(Constants.TAB_STATS);
					moveMouse(random(680, 730), random(355, 370));
					wait(random(1000, 1500));
				}
			}
			if (r == 2) {
				openTab(random(1, 14));
			}
			if (r == 3) {
				int x = input.getX();
				int y = input.getY();
				moveMouse(x + random(-90, 90), y + random(-90, 90));
			}
			if (r == 4) {
				int x2 = input.getX();
				int y2 = input.getY();
				moveMouse(x2 + random(-90, 90), y2 + random(-90, 90));
			}
			if (r == 5) {
				int x3 = input.getX();
				int y3 = input.getY();
				moveMouse(x3 + random(-80, 80), y3 + random(-80, 80));
			}
			if (r == 6) {
				int x3 = input.getX();
				int y3 = input.getY();
				moveMouse(x3 + random(-100, 100), y3 + random(-100, 100));
			}
			if (r == 7) {
				int x3 = input.getX();
				int y3 = input.getY();
				moveMouse(x3 + random(-100, 100), y3 + random(-80, 80));
			}
			if (r == 8) {
				setCameraRotation(random(100, 360));
			}
			if (r == 9) {
				setCameraRotation(random(100, 360));
			}
			if (r == 10) {
				setCameraRotation(random(100, 360));
			}
		}
	}

	public void serverMessageRecieved(final ServerMessageEvent arg0) {
		final String serverString = arg0.getMessage();
		if (serverString.toLowerCase().contains("chop away some ivy")) {
			AMOUNT_CHOPPED++;
		}
		if (serverString.toLowerCase().contains("What is your level in Woodcutting")) {
			sendText("" + skills.getRealSkillLevel(STAT_WOODCUTTING), true);
		}
		if (serverString.toLowerCase().contains("What is your level in woodcutting")) {
			sendText("" + skills.getRealSkillLevel(STAT_WOODCUTTING), true);
		}
		if (serverString.toLowerCase().contains("Wc?") || serverString.toLowerCase().contains("wc?")) {
			sendText("" + skills.getRealSkillLevel(STAT_WOODCUTTING), true);
		}
		if (serverString.toLowerCase().contains("Wc lvl") || serverString.toLowerCase().contains("wc lvl")) {
			sendText("" + skills.getRealSkillLevel(STAT_WOODCUTTING), true);
		}
	}

	public void onFinish() {
		antiBan.stopThread = true;
		Bot.getEventManager().removeListener(ServerMessageListener.class, this);
		Bot.getEventManager().removeListener(PaintListener.class, this);
		log("Thanks for using my script - RawR.");
		log("Ivy cut: " + AMOUNT_CHOPPED + ".");
	}

	public void drawMouse(final Graphics g) {
		final Point loc = getMouseLocation();
		Color ORANGE = new Color(255, 140, 0);
		if (System.currentTimeMillis() - Bot.getClient().getMouse().getMousePressTime() < 500) {
			g.setColor(ORANGE);
			g.fillRect(loc.x - 3, loc.y - 3, 5, 5);
			g.fillRect(loc.x - 7, loc.y - 1, 17, 5);
			g.fillRect(loc.x - 1, loc.y - 7, 5, 17);
		}
		g.setColor(ORANGE);
		g.fillRect(loc.x - 6, loc.y, 15, 3);
		g.fillRect(loc.x, loc.y - 6, 3, 15);
	}

	public void onRepaint(Graphics g) {
		//TIMER INTs
		long millis = System.currentTimeMillis() - startTime;
		long hours = millis / (1000 * 60 * 60);
		millis -= hours * (1000 * 60 * 60);
		long minutes = millis / (1000 * 60);
		millis -= minutes * (1000 * 60);
		long seconds = millis / 1000;
		//COLOR INTs
		Color BACKGROUND = new Color(0, 0, 0, 75);
		Color GREEN = new Color(0, 139, 0, 255);
		if (isLoggedIn()) {
			drawMouse(g);
			//XP / LEVELS
			int XP_GAINED = 0;
			int LVLS_GAINED = 0;
			if (START_XP == 0) {
				START_XP = skills.getCurrentSkillExp(STAT_WOODCUTTING);
			}
			XP_GAINED = skills.getCurrentSkillExp(STAT_WOODCUTTING) - START_XP;
			if (START_LVL == 0) {
				START_LVL = skills.getRealSkillLevel(STAT_WOODCUTTING);
			}
			LVLS_GAINED = skills.getRealSkillLevel(STAT_WOODCUTTING) - START_LVL;
			int XP_TNL = skills.getXPToNextLevel(STAT_WOODCUTTING);
			int XP_HOUR = (int) ((XP_GAINED) * 3600000D / (System.currentTimeMillis() - startTime));
			//BACKGROUND
			g.setColor(Color.WHITE);
			g.drawRect(3, 160, 175, 148);
			g.setColor(BACKGROUND);
			g.fillRect(4, 161, 173, 146);
			//% BAR
			g.setColor(Color.WHITE);
			g.drawRect(3, 312, 175, 25);
			g.setColor(BACKGROUND);
			g.fillRect(4, 313, 174, 24);
			g.setColor(GREEN);
			g.fillRect(4, 313, ((skills.getPercentToNextLevel(STAT_WOODCUTTING) * 2) - 25), 24);
			g.setColor(Color.WHITE);
			g.drawString(skills.getPercentToNextLevel(STAT_WOODCUTTING) + " % to " + (skills.getRealSkillLevel(STAT_WOODCUTTING) + 1) + " Woodcutting.", 23, 330);
			//STATISTICS
			g.setColor(GREEN);
			g.setFont(new Font("Palatino Linotype", Font.BOLD, 16));
			g.drawString("Ivy Chopper", 45, 180);
			g.setColor(Color.WHITE);
			g.setFont(new Font("Arial", Font.PLAIN, 12));
			g.drawString("Time running: " + hours + ":" + minutes + ":" + seconds, 10, 200);
			g.drawString("State: " + STATE, 10, 215);
			g.drawString("Chopped " + AMOUNT_CHOPPED + " Ivy.", 10, 230);
			g.drawString("WC LVL: " + skills.getRealSkillLevel(STAT_WOODCUTTING) + " || Gained: " + LVLS_GAINED + " LVLS.", 10, 245);
			g.drawString("XP Gained: " + XP_GAINED, 10, 260);
			g.drawString("XP / HR: " + XP_HOUR, 10, 275);
			g.drawString("XP TNL: " + XP_TNL, 10, 290);
			g.setFont(new Font("Palatino Linotype", Font.ITALIC, 10));
			g.drawString("- RawR", 130, 300);
		}
	}

	private RSObject getFenceAt3(int x, int y) {
		org.rsbot.accessors.RSObject rsObj;
		org.rsbot.accessors.RSInteractable obj;
		RSObject thisObject = null;
		final org.rsbot.accessors.Client client = Bot.getClient();
		try {
			final org.rsbot.accessors.RSGround rsGround = client.getRSGroundArray()[client.getPlane()][x - client.getBaseX()][y - client.getBaseY()];
			if (client.getRSGroundArray() == null) {
				return null;
			}
			if (rsGround != null) {
				obj = rsGround.getRSObject3_0();
				if (obj != null) {
					rsObj = (org.rsbot.accessors.RSObject) obj;
					if (rsObj.getID() != -1) {
						thisObject = new RSObject(rsObj, x, y, 3);
					}
				}
			}
		} catch (final Exception ignored) {
		}
		return thisObject;
	}

	private RSObject getNearestIvyByID(RSTile[] ivyLoc, final int... ids) {
		RSObject nearest = null;
		double dist = -1;

		for (int i = 0; i < ivyLoc.length; i++) {
			final RSObject o = getFenceAt3(ivyLoc[i].getX(), ivyLoc[i].getY());
			if (o != null) {
				for (int id : ids) {
					if (o.getID() == id) {
						final double distTmp = calculateDistance(getMyPlayer().getLocation(), o.getLocation());
						if (nearest == null) {
							dist = distTmp;
							nearest = o;
						} else if (distTmp < dist) {
							nearest = o;
							dist = distTmp;
						}
					}
				}
			}
		}
		return nearest;
	}

	private class RunAntiBan implements Runnable {
		public boolean stopThread;

		public void run() {
			while (!stopThread) {
				try {
					if (random(0, 15) == 0) {
						final char[] LR = new char[]{KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT};
						final char[] UD = new char[]{KeyEvent.VK_DOWN, KeyEvent.VK_UP};
						final char[] LRUD = new char[]{KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_UP, KeyEvent.VK_UP};
						final int random2 = random(0, 2);
						final int random1 = random(0, 2);
						final int random4 = random(0, 4);
						final int randNum = random(0, 10);
						if (random(0, 3) == 0) {
							Bot.getInputManager().pressKey(LR[random1]);
							Thread.sleep(random(100, 400));
							Bot.getInputManager().pressKey(UD[random2]);
							Thread.sleep(random(300, 600));
							Bot.getInputManager().releaseKey(UD[random2]);
							Thread.sleep(random(100, 400));
							Bot.getInputManager().releaseKey(LR[random1]);
						} else {
							Bot.getInputManager().pressKey(LRUD[random4]);
							if (random4 > 1) {
								Thread.sleep(random(300, 600));
							} else {
								Thread.sleep(random(500, 900));
							}
							if (randNum == random(3, 4)) {
								int x = input.getX();
								int y = input.getY();
								moveMouse(x + random(-100, 100), y + random(-100, 100));
							} else {
								Thread.sleep(random(400, 700));
							}
							Bot.getInputManager().releaseKey(LRUD[random4]);
						}
					} else {
						Thread.sleep(random(200, 2000));
					}
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}