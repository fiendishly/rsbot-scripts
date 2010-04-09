/*
 Owner of Script: Afflicted aka Afflicted H4x
 Updater: Pervy Shuya
 Edited Version from: v1.5 to v2.2
 Date Script modified: 02/18/10 Time: 10:00pm GMT European timezone
 Edited Features to script are: 
 *Built-in antiban
 *Pickup and attacking made faster, 
 *Added more info to paint i.e killcount, kills per hour, total exp gained and average exp per hour
 *Attacks only Yaks that are not in combat
 *Added Range support for most Arrows/Knifes & Bolts
 *Eats any Food now you may have in Bag
 *Randomised mousespeed
 *Logs out when out of food & arrows/knifes/bolts or dead
 *Takes screen on script finish & on level up
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;

import org.rsbot.bot.Bot;
import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSItemTile;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSTile;
import org.rsbot.util.ScreenshotUtil;

@ScriptManifest(authors = { "Afflicted H4x, Pervy Shuya" }, category = "Combat", name = "Yak Attack", version = 2.2, description = "<html><body bgcolor = Black><font color = White><center><h2> WupDummyCurser</h2>"
		+ "<h2>"
		+ "Yak Attack"
		+ " version 2.2</h2><br>\n"
		+ "Author: "
		+ "Afflicted H4x, Pervy Shuya"
		+ "<br><br>\n"
		+ "Start at the Yaks on Neitiznot"
		+ "<br>Based off of xX Nicole Xx's Cow Own3r."
		+ "<br>We do not guarantee no-bans."
		+ "<br>Has built-in Anti-Ban with this script."
		+ "<br>Select Yes or No to eat<br>"
		+ "<strong>Eat Food?</strong><br/>"
		+ "<select name='eatsies'>"
		+ "<option>Yes<option>No</select><br/>"
		+ "Pick up arrows? <select name=\"Ranging\"><option>None<option>Bronze arrows<option>Iron arrows<option>Steel Arrow<option>Mithril Arrow<option>Adamant Arrow<option>Rune Arrow<option>Bronze Bolt<option>Bluerite Bolt<option>Bone Bolt<option>Iron Bolt<option>Steel Bolt<option>Black Bolt<option>Mithril Bolt<option>Adamant Bolt<option>Rune Bolt<option>Broad Bolt<option>Bronze Knife<option>Iron Knife<option>Steel Knife<option>Black Knife<option>Mithril Knife<option>Adamant Knife<option>Rune Knife</select><br><br><br/>")
public class YakAttack extends Script implements PaintListener,
		ServerMessageListener {

	YakAttackAntiBan antiban;
	Thread t;
	private final int KILLYAKS = 0, KILLSCRIPT = 1;
	final ScriptManifest properties = getClass().getAnnotation(
			ScriptManifest.class);
	private final int[] yakID = { 5529 };

	int checkTime, Hour, Minute, Second;
	private int startLevel = 0, startXP = 0, Action = 0, hpLvl, atkExp, atkLvl,
			defExp, defLvl, hpExp, strExp, rangedExp, strLvl, rangedLvl,
			strGained, atkGained, rgeGained, defGained, hpGained, startAtkExp,
			startDefExp, startStrExp, startRangedExp, startHpExp, yaksKilled,
			yaksPerHour, arrowID = -1, bronzeArrow = 882, ironArrow = 884,
			steelArrow = 886, mithrilArrow = 888, adamantArrow = 890,
			runeArrow = 892, bronzeBolt = 877, boneBolt = 8882,
			blueriteBolt = 9139, ironBolt = 9140, steelBolt = 9141,
			blackBolt = 13083, mithrilBolt = 9142, adamantBolt = 9143,
			runeBolt = 9144, broadBolt = 13280, bronzeKnife = 864,
			ironKnife = 863, steelKnife = 865, blackKnife = 869,
			mithrilKnife = 866, adamantKnife = 867, runeKnife = 868;
	final int XPChange = skills.getCurrentSkillExp(14) - startXP,
			levelChange = skills.getCurrentSkillLevel(14) - startLevel;
	private long startTime = System.currentTimeMillis(), time = System
			.currentTimeMillis(), hours, minutes, seconds;

	private String Status = "Starting";

	private final RSTile yakTile = new RSTile(2324, 3792);

	private boolean wants2Eat;

	private int[] foodID = { 1895, 1893, 1891, 4293, 2142, 291, 2140, 3228,
			9980, 7223, 6297, 6293, 6295, 6299, 7521, 9988, 7228, 2878, 7568,
			2343, 1861, 13433, 315, 325, 319, 3144, 347, 355, 333, 339, 351,
			329, 3381, 361, 10136, 5003, 379, 365, 373, 7946, 385, 397, 391,
			3369, 3371, 3373, 2309, 2325, 2333, 2327, 2331, 2323, 2335, 7178,
			7180, 7188, 7190, 7198, 7200, 7208, 7210, 7218, 7220, 2003, 2011,
			2289, 2291, 2293, 2295, 2297, 2299, 2301, 2303, 1891, 1893, 1895,
			1897, 1899, 1901, 7072, 7062, 7078, 7064, 7084, 7082, 7066, 7068,
			1942, 6701, 6703, 7054, 6705, 7056, 7060, 2130, 1985, 1993, 1989,
			1978, 5763, 5765, 1913, 5747, 1905, 5739, 1909, 5743, 1907, 1911,
			5745, 2955, 5749, 5751, 5753, 5755, 5757, 5759, 5761, 2084, 2034,
			2048, 2036, 2217, 2213, 2205, 2209, 2054, 2040, 2080, 2277, 2225,
			2255, 2221, 2253, 2219, 2281, 2227, 2223, 2191, 2233, 2092, 2032,
			2074, 2030, 2281, 2235, 2064, 2028, 2187, 2185, 2229, 6883, 1971,
			4608, 1883, 1885 };

	protected int getMouseSpeed() {
		return random(6, 10);
	}

	private void checkEat() {
		final int cHealth = skills
				.getCurrentSkillLevel(Constants.STAT_HITPOINTS);
		final int randomInt = random(27, 30);
		if (cHealth <= randomInt) {
			Status = "Eating Food";
			clickInventoryItem(foodID, "Eat");

		}
	}

	private void handleArrows() {
		if (arrowID != -1 && !getMyPlayer().isInCombat()) {
			RSItemTile rangeStuff = getNearestGroundItemByID(5, arrowID);
			Status = "Picking up Arrows";
			log("Picking up Arrows");
			atTile(rangeStuff, "Take");
			wait(random(400, 600));
			return;
		}
	}

	private boolean clickInventoryItem(final int[] ids, final String command) {
		try {
			if (getCurrentTab() != Constants.TAB_INVENTORY
					&& !RSInterface.getInterface(Constants.INTERFACE_BANK)
							.isValid()
					&& !RSInterface.getInterface(Constants.INTERFACE_STORE)
							.isValid()) {
				openTab(Constants.TAB_INVENTORY);
			}
			final int[] items = getInventoryArray();
			final java.util.List<Integer> possible = new ArrayList<Integer>();
			for (int i = 0; i < items.length; i++) {
				for (final int item : ids) {
					if (items[i] == item) {
						possible.add(i);
					}
				}
			}
			if (possible.size() == 0) {
				return false;
			}
			final int idx = possible.get(random(0, possible.size()));
			final Point t = getInventoryItemPoint(idx);
			moveMouse(t, 5, 5);
			wait(random(100, 290));
			if (getMenuActions().get(0).equals(command)) {
				clickMouse(true);
				return true;
			} else {
				// clickMouse(false);
				return atMenu(command);
			}
		} catch (final Exception e) {
			log.log(Level.SEVERE, "clickInventoryFood(int...) error: ", e);
			return false;
		}
	}

	private boolean clickNPC(final RSNPC npc, final String action) {
		if (npc == null) {
			return false;
		}
		final RSTile tile = npc.getLocation();
		if (!tile.isValid()) {
			return false;
		}

		try {
			Point screenLoc = npc.getScreenLocation();
			if (distanceTo(tile) > 6 || !pointOnScreen(screenLoc)) {
				turnToTile(tile);
			}
			if (distanceTo(tile) > 20) {
				walkTileMM(tile);
				return false;
			}
			for (int i = 0; i < 12; i++) {
				screenLoc = npc.getScreenLocation();
				if (!npc.isValid() || !pointOnScreen(screenLoc)) {
					return false;
				}
				moveMouse(screenLoc, 5, 5);
				if (getMenuItems().get(0).toLowerCase().contains(
						npc.getName().toLowerCase())) {
					break;
				}
				if (getMouseLocation().equals(screenLoc)) {
					break;
				}
			}
			final List<String> menuItems = getMenuItems();
			if (menuItems.isEmpty()) {
				return false;
			}
			for (String menuItem : menuItems) {
				if (menuItem.toLowerCase()
						.contains(npc.getName().toLowerCase())) {
					if (menuItems.get(0).toLowerCase().contains(
							action.toLowerCase())) {
						clickMouse(true);
						return true;
					} else {
						clickMouse(false);
						return atMenu(action);
					}
				}
			}
		} catch (final Exception e) {
			log.log(Level.SEVERE, "clickNPC(RSNPC, String) error: ", e);
			return false;
		}
		return false;
	}

	private int getAction() {
		if (distanceTo(yakTile) < 50) {
			return KILLYAKS;
		} else {
			return KILLSCRIPT;
		}
	}

	public int loop() {
		if (wants2Eat) {
			if (getInventoryCount(foodID) >= 1) {
				checkEat();
			} else {
				if (getInventoryCount(foodID) == 0) {
					Status = "Out of food! shutting down";
					log("We are out of food! logging out");
					wait(8000);
					logout();
					stopScript();
				}
			}
		}
		getMouseSpeed();

		Action = getAction();
		switch (Action) {
		case KILLYAKS:
			runControl();
			handleArrows();
			RSItemTile rangeStuff = getNearestGroundItemByID(arrowID);
			if (rangeStuff != null) {
				return 100;
			}
			if (inventoryContains(arrowID)
					&& getInventoryCount(arrowID) == random(50, 100)) {
				if (getCurrentTab() != TAB_INVENTORY) {
					openTab(TAB_INVENTORY);
				}
				Status = "Equiping Arrows";
				atInventoryItem(arrowID, "Wield");
			}
			if (getMyPlayer().getInteracting() != null) {
				return random(300, 450);
			}

			final RSNPC yak = getNearestFreeNPCByID(yakID);
			if (yak != null) {
				if (yak.getInteracting() != null
						&& getMyPlayer().getInteracting() == null) {
					return random(100, 200);
				}

				if (getMyPlayer().getInteracting() == null) {
					Status = "Attacking Yaks";
					clickNPC(yak, "attack");
					return random(800, 1400);
				}
				return random(200, 400);
			}
			return random(500, 1000);

		case KILLSCRIPT:
			log("Stopping script get to the Yak Pen on Neitiznot.");
			stopScript();
			return random(100, 200);
		}

		return random(400, 800);
	}

	public boolean onStart(final Map<String, String> args) {
		log("Started Yak Attack");
		if (args.get("eatsies").equals("Yes")) {
			log("Eating");
			wants2Eat = true;
		} else {
			if (args.get("eatsies").equals("No")) {
				log("Not Eating");
				wants2Eat = false;
			}
			if (args.get("Ranging").equals("Bronze arrows")) {
				arrowID = bronzeArrow;
			}
			if (args.get("Ranging").equals("Iron arrows")) {
				arrowID = ironArrow;
			}
			if (args.get("Ranging").equals("Steel Arrow")) {
				arrowID = steelArrow;
			}
			if (args.get("Ranging").equals("Mithril Arrow")) {
				arrowID = mithrilArrow;
			}
			if (args.get("Ranging").equals("Adamant Arrow")) {
				arrowID = adamantArrow;
			}
			if (args.get("Ranging").equals("Rune Arrow")) {
				arrowID = runeArrow;
			}
			if (args.get("Ranging").equals("Bronze Bolt")) {
				arrowID = bronzeBolt;
			}
			if (args.get("Ranging").equals("Bluerite Bolt")) {
				arrowID = blueriteBolt;
			}
			if (args.get("Ranging").equals("Bone Bolt")) {
				arrowID = boneBolt;
			}
			if (args.get("Ranging").equals("Iron Bolt")) {
				arrowID = ironBolt;
			}
			if (args.get("Ranging").equals("Steel Bolt")) {
				arrowID = steelBolt;
			}
			if (args.get("Ranging").equals("Black Bolt")) {
				arrowID = blackBolt;
			}
			if (args.get("Ranging").equals("Mithril Bolt")) {
				arrowID = mithrilBolt;
			}
			if (args.get("Ranging").equals("Adamant Bolt")) {
				arrowID = adamantBolt;
			}
			if (args.get("Ranging").equals("Rune Bolt")) {
				arrowID = runeBolt;
			}
			if (args.get("Ranging").equals("Broad Bolt")) {
				arrowID = broadBolt;
			}
			if (args.get("Ranging").equals("Bronze Knife")) {
				arrowID = bronzeKnife;
			}
			if (args.get("Ranging").equals("Iron Knife")) {
				arrowID = ironKnife;
			}
			if (args.get("Ranging").equals("Steel Knife")) {
				arrowID = steelKnife;
			}
			if (args.get("Ranging").equals("Black Knife")) {
				arrowID = blackKnife;
			}
			if (args.get("Ranging").equals("Mithril Knife")) {
				arrowID = mithrilKnife;
			}
			if (args.get("Ranging").equals("Adamant Knife")) {
				arrowID = adamantKnife;
			}
			if (args.get("Ranging").equals("Rune Knife")) {
				arrowID = runeKnife;
			}
			antiban = new YakAttackAntiBan();
			t = new Thread(antiban);
		}
		return true;
	}

	private void runControl() {
		if (!isRunning() && getEnergy() > random(20, 30)) {
			setRun(true);
		}
	}

	public void serverMessageRecieved(ServerMessageEvent arg0) {
		String serverString = arg0.getMessage();

		if (serverString.contains("<col=ffff00>System update in")) {
			log("There will be a system update soon, so we logged out");
			logout();
			stopScript();
		}
		if (serverString.contains("Oh dear, you are dead!")) {
			Status = "Dead";
			log("We somehow died :S, shutting down");
			logout();
			stopScript();
		}
		if (serverString.contains("There is no ammo left in your quiver.")) {
			log("We have no arrows left, shutting down!");
			logout();
			stopScript();
		}
		if (serverString.contains("You've just advanced")) {
			log("Congrats on level up, Screenshot taken!");
			ScreenshotUtil.takeScreenshot(true);
			wait(random(1500, 2500));
			if (canContinue()) {
				clickContinue();
			}

		}
	}

	// *******************************************************//
	// PAINT SCREEN
	// *******************************************************//
	public void onRepaint(Graphics g) {
		final int xpGained;
		atkExp = skills.getCurrentSkillExp(Constants.STAT_ATTACK);
		strExp = skills.getCurrentSkillExp(Constants.STAT_STRENGTH);
		defExp = skills.getCurrentSkillExp(Constants.STAT_DEFENSE);
		hpExp = skills.getCurrentSkillExp(Constants.STAT_HITPOINTS);
		rangedExp = skills.getCurrentSkillExp(Constants.STAT_RANGE);
		xpGained = (atkExp - startAtkExp) + (strExp - startStrExp)
				+ (defExp - startDefExp) + (rangedExp - startRangedExp)
				+ (hpExp - startHpExp);
		atkGained = (atkExp - startAtkExp);
		strGained = (strExp - startStrExp);
		defGained = (defExp - startDefExp);
		rgeGained = (rangedExp - startRangedExp);
		hpGained = (hpExp - startHpExp);
		time = System.currentTimeMillis() - startTime;
		seconds = time / 1000;
		if (seconds >= 60) {
			minutes = seconds / 60;
			seconds -= minutes * 60;
		}
		if (minutes >= 60) {
			hours = minutes / 60;
			minutes -= hours * 60;
		}
		if (startAtkExp == 0) {
			startAtkExp = skills.getCurrentSkillExp(Constants.STAT_ATTACK);
		}
		if (startStrExp == 0) {
			startStrExp = skills.getCurrentSkillExp(Constants.STAT_STRENGTH);
		}
		if (startDefExp == 0) {
			startDefExp = skills.getCurrentSkillExp(Constants.STAT_DEFENSE);
		}
		if (startHpExp == 0) {
			startHpExp = skills.getCurrentSkillExp(Constants.STAT_HITPOINTS);
		}
		if (startRangedExp == 0) {
			startRangedExp = skills.getCurrentSkillExp(Constants.STAT_RANGE);
		}

		final int xpHour = ((int) ((3600000.0 / (double) time) * xpGained));
		float xpSec = 0;
		if ((minutes > 0 || hours > 0 || seconds > 0) && xpGained > 0) {
			xpSec = ((float) xpGained)
					/ (float) (seconds + (minutes * 60) + (hours * 60 * 60));
		}
		float xpMin = xpSec * 60;
		float xphour = xpMin * 60;
		yaksKilled = (xpGained / 200);
		yaksPerHour = (xpHour / 200);

		if (getCurrentTab() == TAB_INVENTORY) {
			g.setColor(new Color(0, 0, 0, 175));
			g.fillRoundRect(555, 210, 175, 250, 10, 10);
			g.setColor(Color.WHITE);
			int[] coords = new int[] { 225, 240, 255, 270, 285, 300, 315, 330,
					345, 360, 375, 390, 405, 420, 435, 450 };
			g.drawString(properties.name(), 561, coords[0]);
			g.drawString("Version: " + properties.version(), 561, coords[1]);
			g.drawString("Run Time: " + hours + ":" + minutes + ":" + seconds,
					561, coords[3]);
			g.drawString("Attack exp gained: " + atkGained, 561, coords[5]);
			g.drawString("strength exp gained: " + strGained, 561, coords[6]);
			g.drawString("defence exp gained: " + defGained, 561, coords[7]);
			g.drawString("HP exp gained: " + hpGained, 561, coords[8]);
			g.drawString("ranged exp gained: " + rgeGained, 561, coords[9]);
			g.drawString("Exp PerHour: " + Integer.toString((int) xphour), 561,
					coords[10]);
			g.drawString("Total xpGained: " + Integer.toString(xpGained), 561,
					coords[11]);
			g.drawString(
					"Yaks Kills PerHour: " + Integer.toString(yaksPerHour),
					561, coords[12]);
			g.drawString("Yaks Slayed: " + Integer.toString(yaksKilled), 561,
					coords[13]);
			g.drawString("Status: " + Status, 561, coords[15]);
		}
	}

	public void onFinish() {
		final int xpGained;
		final int lvlGained;
		xpGained = (atkExp - startAtkExp) + (strExp - startStrExp)
				+ (defExp - startDefExp) + (rangedExp - startRangedExp)
				+ (hpExp - startHpExp);
		lvlGained = (atkLvl) + (strLvl) + (defLvl) + (rangedLvl) + (hpLvl);
		ScreenshotUtil.takeScreenshot(true);
		log.info(": You have gained " + xpGained + " Experince + " + lvlGained
				+ " Levels.");
		antiban.stopThread = true;
		logout();
	}

	private class YakAttackAntiBan implements Runnable {
		private boolean stopThread;

		public void run() {
			Random random = new Random();
			while (!stopThread) {
				try {
					if (random.nextInt(Math.abs(15 - 0)) == 0) {
						final char[] LR = new char[] { KeyEvent.VK_LEFT,
								KeyEvent.VK_RIGHT };
						final char[] UD = new char[] { KeyEvent.VK_DOWN,
								KeyEvent.VK_UP };
						final char[] LRUD = new char[] { KeyEvent.VK_LEFT,
								KeyEvent.VK_RIGHT, KeyEvent.VK_UP,
								KeyEvent.VK_UP };
						final int random2 = random.nextInt(Math.abs(2 - 0));
						final int random1 = random.nextInt(Math.abs(2 - 0));
						final int random4 = random.nextInt(Math.abs(4 - 0));

						if (random.nextInt(Math.abs(3 - 0)) == 0) {
							Bot.getInputManager().pressKey(LR[random1]);
							Thread.sleep(random.nextInt(Math.abs(400 - 100)));
							Bot.getInputManager().pressKey(UD[random2]);
							Thread.sleep(random.nextInt(Math.abs(600 - 300)));
							Bot.getInputManager().releaseKey(UD[random2]);
							Thread.sleep(random.nextInt(Math.abs(400 - 100)));
							Bot.getInputManager().releaseKey(LR[random1]);
						} else {
							Bot.getInputManager().pressKey(LRUD[random4]);
							if (random4 > 1) {
								Thread.sleep(random
										.nextInt(Math.abs(600 - 300)));
							} else {
								Thread.sleep(random
										.nextInt(Math.abs(900 - 500)));
							}
							Bot.getInputManager().releaseKey(LRUD[random4]);
						}
					} else {
						Thread.sleep(random.nextInt(Math.abs(2000 - 200)));
					}
				} catch (Exception e) {
					System.out.println("AntiBan error detected!");
				}
			}
		}
	}
}
 	  	 
