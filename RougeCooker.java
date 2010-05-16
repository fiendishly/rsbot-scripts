/**			RougeCooker by Speed
 *
 *	A Speed's Scripting Production...
 *
 *
 *	Support & help can be found at: http://www.rsbot.org/vb/showthread.php?t=152146
 *
 *
 *	Made on 15th October 2009.
 *	Updated to Version 1.04 on 15th May 2010.
 *
 *	Thanks to Exempt for some variables and IDs (couldn't be bothered to get my own).
 *
 *	
 *	Version 1.00 - Script made.
 *	Version 1.01 - Updated antiban.
 *	Version 1.02 - Made more efficient. Redid some methods.
 *	Version 1.03 - Made sure everything is checked for nulls, fixed most bugs, etc. Made way faster.
 *	Version 1.04 - Cleaned code a little.
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.Map;


import org.rsbot.bot.Bot;
import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSInterfaceComponent;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = { "Speed" }, category = "Cooking", name = "Rouge Cooker", version = 1.04, description = "<html>"
	+ "<head></head><body>"
	+ "<h1> Rouge Cooker by Speed </h1>"
	+ "<br><br>Run in Rogue's Den. Enter ID of Raw material here:"
	+ "<br>Food ID: <input type=\"text\" name=\"foodID\"><br><br>"
	+ "<b> Enjoy</b></html>")
public class RougeCooker extends Script implements PaintListener, ServerMessageListener {
	private int tries = 0;
	private static final int BANK_ID = 2271;
	private static final int FIRE_ID = 2732;
	private int antibans;
	private long seconds;
	private long runTime;
	private long minutes;
	private long hours;
	private int levelChange;
	private int GambleInt;
	private int foodCooked;
	private long curTime = System.currentTimeMillis();
	private int XpGained;
	private int startExp = skills.getCurrentSkillExp(STAT_COOKING);
	private int startStatLvl = skills.getCurrentSkillLevel(STAT_COOKING);
	private long startTime = System.currentTimeMillis();
	private int foodID;
	private int cookPerHour;
	private int xpPerHour;

	public int getMouseSpeed() {
		return random(4, 6);
	}

	public boolean onStart(Map<String, String> args) {
		startExp = skills.getCurrentSkillExp(STAT_COOKING);
		startStatLvl = skills.getCurrentSkillLevel(STAT_COOKING);
		startTime = System.currentTimeMillis();
		foodID = args.get("foodID") != null ? Integer.parseInt(args.get("foodID")) : 0;
		return foodID != 0;
	}


	private boolean isBusy() {
		boolean flag = false;
		for (int i = 0; i < 4; i++) {
			if (getMyPlayer().getAnimation() != -1) {
				flag = true;
				break;
			}
			wait(random(300, 600));
		}
		return flag;
	}
	private boolean useItem(final int item, final RSObject targetObject) {
		if (getCurrentTab() != Constants.TAB_INVENTORY) {
			openTab(Constants.TAB_INVENTORY);
		}
		if(getSelectedInvItem() == item) {
			if(distanceTo(targetObject.getLocation()) > 5) {
				walkTileMM(targetObject.getLocation());
			}
			if(tileOnScreen(targetObject.getLocation())) {
				return atObject(targetObject, "Fire");
			} else {
				turnToTile(targetObject.getLocation());
				return atObject(targetObject, "Fire");
			}
		} else {
			atInventoryItem(item, "Use");
			if(getSelectedInvItem() == item) {
				return atObject(targetObject, "Fire");
			}
		}
		return false;
	}
	private int getSelectedInvIndex() {
		final RSInterfaceComponent[] components = getInventoryInterface()
				.getComponents();
		for (int i = 0; i < 28; i++) {
			if (components[i].getBorderThickness() == 2)
				return i;
		}
		return -1;
	}

	private int getSelectedInvItem() {
		final int index = getSelectedInvIndex();
		return index == -1 ? -1 : getInventoryArray()[index];
	}

	private boolean useFire() {
		if(bank.isOpen()) {
			bank.close();
		}
		if (getMyPlayer().getAnimation() != -1) {
			curTime = System.currentTimeMillis();
		}
		final RSObject range = getNearestObjectByID(FIRE_ID);
		final RSInterfaceChild LOL_AREA = RSInterface.getChildInterface(513, 3);
		if (range != null && !getMyPlayer().isMoving()  && (System.currentTimeMillis() - curTime) > 3500 && !LOL_AREA.isValid()) {
			if(useItem(foodID, range) && !LOL_AREA.isValid()) {
				wait(random(800, 1000));
			}
		}
		if(LOL_AREA.isValid()) {
			if(!atInterface(LOL_AREA, "All")) {
				if(getSelectedInvItem() != -1) {
					atInventoryItem(getSelectedInvItem(), "Use");
				}
			}
		}
		return true;
	}

	private void antiban() {
		if (antibans < random(5, 8)) {
			GambleInt = random(0, 12);
			if (GambleInt == 1) {
				turnCamera();
				antibans++;
			}

			if (GambleInt == 2) {
				final int xA = random(0, 750);
				final int yA = random(0, 500);
				moveMouse(xA, yA);
				turnCamera();
				antibans++;
			}

			if (GambleInt == 3) {
				if (getCurrentTab() != Constants.TAB_INVENTORY) {
					openTab(Constants.TAB_INVENTORY);
					turnCamera();
					antibans++;
				}
			}

			if (GambleInt == 4) {
				turnCamera();
				wait(random(500, 1750));
				antibans++;
			}

			if (GambleInt == 9) {
				turnCamera();
				openTab(random(0, 13));
				turnCamera();
				antibans++;
			}

			if (GambleInt == 5) {
				turnCamera();
				final int xA = random(0, 750);
				final int yA = random(0, 500);
				moveMouse(xA, yA);
				antibans++;
			}

			if (GambleInt == 6) {
				turnCamera();
				antibans++;
			}

			if (GambleInt == 7) {
				openTab(random(0, 13));
				antibans++;
			}

			if (GambleInt == 8) {
				moveMouse(random(0, 450), random(0, 450));
				antibans++;
			}
			if (GambleInt == 9) {
				openTab(random(0, 14));
				turnCamera();
				antibans++;
			}
		}
	}

	// credits WarXperiment
	private void turnCamera() {
		final char[] LR = new char[] { KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT };
		final char[] UD = new char[] { KeyEvent.VK_UP, KeyEvent.VK_DOWN };
		final char[] LRUD = new char[] { KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT,
				KeyEvent.VK_UP, KeyEvent.VK_DOWN };
		final int random2 = random(0, 2);
		final int random1 = random(0, 2);
		final int random4 = random(0, 4);

		if (random(0, 3) == 0) {
			Bot.getInputManager().pressKey(LR[random1]);
			try {
				wait(random(100, 400));
			} catch (final Exception e) {
			}
			Bot.getInputManager().pressKey(UD[random2]);
			try {
				wait(random(300, 600));
			} catch (final Exception e) {
			}
			Bot.getInputManager().releaseKey(UD[random2]);
			try {
				wait(random(100, 400));
			} catch (final Exception e) {
			}
			Bot.getInputManager().releaseKey(LR[random1]);
		} else {
			Bot.getInputManager().pressKey(LRUD[random4]);
			if (random4 > 1) {
				try {
					wait(random(300, 600));
				} catch (final Exception e) {
				}
			} else {
				try {
					wait(random(500, 900));
				} catch (final Exception e) {
				}
			}
			Bot.getInputManager().releaseKey(LRUD[random4]);
		}
	}
	
	private boolean isEmpty() {
		return getInventoryCount() == 0;
	}

	private void useBank() {
		RSNPC banker = getNearestNPCByID(BANK_ID);
		if(getSelectedInvItem() != -1) {
			atInventoryItem(getSelectedInvItem(), "Use");
		}
		if (banker != null) {
			if (!bank.isOpen()) {
				RSTile bankerT = banker.getLocation();
				if(distanceTo(bankerT) < 3) {
					if(tileOnScreen(bankerT)) {
						atNPC(banker, "Bank");
					} else {
						turnToTile(bankerT);
					}
				} else {
					walkTileMM(bankerT);
				}
			} else {
				if (tries < 3) {
					if(!isEmpty()) {
						bank.depositAll();
					}
					if(!inventoryContains(foodID)) {
						bank.withdraw(foodID, 0);
					}
					antibans = 0;
					bank.close();
					if (!inventoryContains(foodID)) {
						tries++;
					} else {
						tries = 0;
					}
				} else {
					log("Out of food or failed banking more then 5 times.");
					stopScript();
				}
			}
		}
	}

	public void serverMessageRecieved(ServerMessageEvent e) {
		String word = e.getMessage().toLowerCase();
		if (word.contains("cook") || word.contains("burn") || word.contains("roast")) {
			foodCooked++;
		}
	}


	public int loop() {
		getMouseSpeed();
		if (getCurrentTab() != Constants.TAB_INVENTORY) {
			openTab(Constants.TAB_INVENTORY);
		}
		if(!isBusy() && inventoryContains(foodID)){
			useFire();
		}
		if(!isBusy() && !inventoryContains(foodID)){
			useBank();
		}
		if(isBusy()){
			antiban();
		}
		return random(100, 400);
	}

	public void onRepaint( Graphics g ) { 
		runTime = System.currentTimeMillis( ) - startTime;
		seconds = runTime / 1000;
		if ( seconds >= 60 ) {
			minutes = seconds / 60;
			seconds -= (minutes * 60);
		}
		if ( minutes >= 60 ) {
			hours = minutes / 60;
			minutes -= (hours * 60);
		}
		XpGained = skills.getCurrentSkillExp(STAT_COOKING) - startExp;
		if(XpGained > 0 && foodCooked > 0) {
			xpPerHour = (int) ((3600000.0 / (double) runTime) * XpGained);
			cookPerHour = (int) ((3600000.0 / (double) runTime) * foodCooked);
		}
		g.setColor( new Color(0, 0, 0, 50) );
		g.fillRoundRect(3, 180, 155, 130, 5, 5); 
		levelChange = skills.getCurrentSkillLevel(STAT_COOKING) - startStatLvl;
		g.setColor( Color.WHITE );
		g.drawString("Run time: " + hours + ":" + minutes + ":" + seconds, 12, 200);
		g.drawString("Cooked " + foodCooked + " raw food.", 12, 216);
		g.drawString("XP Gained: " + XpGained, 12, 232);
		g.drawString("Levels Gained: " + levelChange, 12, 245);
		g.drawString("Percent to next level: " + skills.getPercentToNextLevel(STAT_COOKING), 12, 264);
		g.drawString("XP/hour: " + xpPerHour, 12, 280);
		g.drawString("Cooked/hour: " + cookPerHour, 12, 296);
	}


}