import java.awt.*;
import java.util.*;

import org.rsbot.script.*;
import org.rsbot.bot.*;
import org.rsbot.script.wrappers.*;
import org.rsbot.script.Skills;
import org.rsbot.event.listeners.*;
import org.rsbot.event.events.*;
import org.rsbot.util.*;

@ScriptManifest(authors = {"Frolt"}, category = "Woodcutting", name = "Frolt Any Chopper", version = 1.1, description = ("<html><head>" +
		"<style type=\"text/css\">body {background:url(\"\") no-repeat}</style>"
		+ "<html>\n"
		+ "<b><center><h2>Frolt Any Chopper (1.1)</h2></center></b>"
		+ "<font size=\"3\">"
		+ "<b><u>Information:</u><br></b>"
		+ "<b>This script is designed to just improve your woodcutting skill no matter what tree. If you just want your bot to woodcut then this script is it. This script will chop the supported logs shown below and then drop them. There is paint on this script so it will keep you updated on what is going on with the script. This will count the logs as well no matter what log it is. This script also supports almost all axes. Please look in under the supported trees and axes to see what is and what is not supported.<br><br></b>"
		+ "<b><u>Select Tree Types:</u><br></b>"
		+ "<select name=\"tree\"><option>Normal</option><option>Oak</option><option>Willow</option></select>"
		+ "<br>"
		+ "<b><u>Supported Axe Types:</u><br></b>"
		+ "<b>- Bronze hatchet<br></b>"
		+ "<b>- Iron hatchet<br></b>"
		+ "<b>- Steel hatchet<br></b>"
		+ "<b>- Black hatchet<br></b>"
		+ "<b>- Mithril hatchet<br></b>"
		+ "<b>- Adamant hatchet<br></b>"
		+ "<b>- Rune hatchet<br></b>"
		+ "<b>- Dragon hatchet<br></b>"
		+ "</body></html>"))
public class FroltAnyChopper extends Script implements PaintListener, ServerMessageListener {

	public int[] hatchets = {1351, 1349, 1353, 1361, 1355, 1357, 1359, 6739, 13470};
	private long startTime;
	private int WoodcuttingSkillIndex1, WoodcuttingStartingXP;
	public String status = "Starting";
	int logs = 0;
	int loads = 0;
	int levels = 0;
	public int[] toChop;
	public int[] NormalTree = {5004, 5005, 5045, 3879, 3881, 3882, 3883,
			3885, 3886, 3887, 3888, 3889, 3890, 3891, 3892, 3893, 3928, 3967,
			3968, 4048, 4049, 4050, 4051, 4052, 4053, 4054, 3033, 3034, 3035,
			3036, 2409, 2447, 2448, 1330, 1331, 1332, 1310, 1305, 1304, 1303,
			1301, 1276, 1277, 1278, 1279, 1280, 8742, 8743, 8973, 8974, 1315,
			1316};
	public int[] OakTree = {1281, 3037, 8462, 8463, 8464, 8465, 8466, 8467};
	public int[] WillowTree = {1308, 5551, 5552, 5553, 8481, 8482, 8483,
			8484, 8485, 8486, 8487, 8488};

	protected int getMouseSpeed() {
		return 5;

	}

	public boolean onStart(Map<String, String> args) {
		log(".");
		log("..");
		log("...");
		log("....");
		log(".....");
		log("Starting Frolt Any Chopper!");
		startTime = System.currentTimeMillis();
		WoodcuttingSkillIndex1 = Skills.getStatIndex("woodcutting");
		WoodcuttingStartingXP = skills.getCurrentSkillExp(WoodcuttingSkillIndex1);
		if (args.get("tree").equals("Normal"))
			toChop = NormalTree;
		else if (args.get("tree").equals("Oak"))
			toChop = OakTree;
		else if (args.get("tree").equals("Willow"))
			toChop = WillowTree;
		else
			return false;
		return true;

	}

	public void onFinish() {
		ScreenshotUtil.takeScreenshot(true);
		Bot.getEventManager().removeListener(PaintListener.class, this);

	}

	public int antiBan() {
		status = "AntiBan activated";
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
				openTab(Constants.TAB_INVENTORY);
				return random(200, 400);
			case 4:
				if (getMyPlayer().isMoving()) {
					return random(750, 1000);
				}
			case 5:
				moveMouse(x, y);
				int checkTime = 0;
				long lastCheck = 0;
				if (System.currentTimeMillis() - lastCheck >= checkTime) {
					lastCheck = System.currentTimeMillis();
					checkTime = random(60000, 180000);
				}
			case 6:
				if (getCurrentTab() != Constants.TAB_STATS) {
					openTab(Constants.TAB_STATS);
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
				openTab(Constants.TAB_STATS);
				return random(100, 500);
			case 2:
				openTab(Constants.TAB_ATTACK);
				return random(100, 500);
			case 3:
				openTab(Constants.TAB_QUESTS);
				return random(100, 500);
			case 4:
				openTab(Constants.TAB_EQUIPMENT);
				return random(100, 500);
			case 5:
				openTab(Constants.TAB_INVENTORY);
				return random(100, 500);
			case 6:
				openTab(Constants.TAB_PRAYER);
				return random(100, 500);
			case 7:
				openTab(Constants.TAB_MAGIC);
				return random(100, 500);
			case 8:
				openTab(Constants.TAB_SUMMONING);
				return random(100, 500);
			case 9:
				openTab(Constants.TAB_FRIENDS);
				return random(100, 500);
			case 10:
				openTab(Constants.TAB_IGNORE);
				return random(100, 500);
			case 11:
				openTab(Constants.TAB_CLAN);
				return random(100, 500);
			case 12:
				openTab(Constants.TAB_CONTROLS);
				return random(100, 500);
			case 13:
				openTab(Constants.TAB_MUSIC);
				return random(100, 500);
			case 14:
				openTab(Constants.TAB_OPTIONS);
				return random(100, 500);
		}
		return random(100, 300);

	}

	public void onRepaint(Graphics g) {
		if (isLoggedIn()) {
			long millis = System.currentTimeMillis() - startTime;
			long hours = millis / (1000 * 60 * 60);
			millis -= hours * (1000 * 60 * 60);
			long minutes = millis / (1000 * 60);
			millis -= minutes * (1000 * 60);
			long seconds = millis / 1000;
			if (getCurrentTab() == TAB_INVENTORY) {
				g.setColor(new Color(0, 0, 0, 175));
				g.fillRoundRect(555, 210, 175, 250, 10, 10);
				g.setColor(Color.white);
				g.drawString("Frolt Any Chopper:", 561, 225);
				g.drawString("Version: 1.1", 561, 235);

				g.drawString("Current Status:", 561, 255);
				g.drawString("" + status + ".", 561, 265);

				g.drawString("Current Chopping Log:", 561, 285);
				g.drawString("Logs Chopped: " + logs, 561, 295);
				g.drawString("Loads: " + loads, 561, 305);

				g.drawString("Experience/Level Log:", 561, 325);
				g.drawString("Levels Gained: " + levels, 561, 335);
				g.drawString("XP Gained: " + (skills.getCurrentSkillExp(WoodcuttingSkillIndex1) - WoodcuttingStartingXP), 561, 345);
				g.drawString("Current Level: " + skills.getCurrentSkillLevel(STAT_WOODCUTTING), 561, 355);
				g.drawString("Percent Till Next Level: " + skills.getPercentToNextLevel(STAT_WOODCUTTING) + "%", 561, 365);
				g.drawString("XP Untill Level: " + skills.getXPToNextLevel(STAT_WOODCUTTING), 561, 375);

				g.drawString("Time Running:", 561, 395);
				g.drawString("" + hours + ":" + minutes + ":" + seconds + "", 561, 405);
			}
			Point p = getMouseLocation();
			long timeSince = Bot.getClient().getMouse().getMousePressTime();
			if (timeSince > System.currentTimeMillis() - 500)
				g.setColor(new Color(255, 255, 255, 125));
			else
				g.setColor(new Color(0, 0, 0, 125));
			g.drawLine(0, p.y, 762, p.y);
			g.drawLine(p.x, 0, p.x, 500);

		}

	}

	public void serverMessageRecieved(final ServerMessageEvent arg0) {
		String serverString = arg0.getMessage();
		if (serverString.contains("You get some")) {
			logs++;
		}
		if (serverString.contains("Your inventory is too full to hold")) {
			loads++;
		}
		if (serverString.contains("You've just advanced a Woodcutting level!")) {
			levels++;
		}


	}

	public boolean chopping() {
		if (!isIdle())
			return true;
		RSObject Tree = getNearestObjectByID(toChop);
		if (Tree == null)
			return false;
		atObject(Tree, "hop");
		return true;

	}

	public int loop() {
		setCameraAltitude(true);
		if (getEnergy() > random(30, 60)) {
			setRun(true);
		}
		if (getMyPlayer().getAnimation() != -1) {
			return random(800, 2000);
		}
		if (isInventoryFull()) {
			status = "Inventory is full";
			dropAllExcept(true, hatchets);
		} else {
			chopping();
			status = "Cutting tree";
			wait(2000);
			antiBan();
			wait(2000);
		}
		return 300;
	}
}