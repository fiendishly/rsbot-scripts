import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.*;

import org.rsbot.script.*;
import org.rsbot.script.wrappers.*;
import org.rsbot.bot.Bot;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.event.events.ServerMessageEvent;

@ScriptManifest(authors = { "RawR" }, category = "Fletching", name = "RawR Fletcher", version = 1.25,
description =
	"<html><body><center><u><h2>RawR Fletcher</h2></u></center>" +
	"The script has the options to do Fletching or Stringing. <br />" +
	"<font size='3'>Please choose your settings below.</font> <br /> <br />" +
	"<b>Method :</b> " +
		"<select name='method'><option>Fletching</option><option>Stringing</option></select><br /><br/>" +
	" <b>Amount:</b> " +
		"<input name='amount' type='text' size='10' maxlength='10' value='1000' /><br /><br />" +
	"<b>Log / Bow :</b> " +
		"<select name='log' style='margin-top: 2px;'><option>Normal</option><option>Oak</option><option>Willow</option><option>Maple</option><option>Yew</option><option>Magic</option></select>" +
		"<select name='bow' style='margin-top: 2px;'><option>Shortbow</option><option>Longbow</option></select> <br /><br />" +
	"<b>Knife :</b> " +
		"<select name='knife' style='margin-top: 2px;'><option>Normal</option><option>Clay</option></select><br /><br />" +
	"<b>antiBan:</b> " +
		"<input type='checkbox' name='antiBan' value='true'>" +
	"</body></html>")

public class RawrFletcher extends Script implements PaintListener, ServerMessageListener {
	//MULTI-THREADING
	RunAntiBan antiBan;
	Thread t;
	//VARIABLES
	public String useAntiBan;
	public String Log;
	public String Bow;
	public String Method;
	private String logName = "None";
	private int amountStrung = 0;
	private int amountFletch = 0;
	private int AMOUNT = 0;
	private int KNIFE;
	private int LOG_ID;
	private int SHORT_ID;
	private int LONG_ID;
	private int BOW_STRING = 1777;
	private int BANKBOOTH[] = { 11758, 11402, 34752, 35647, 2213,
			25808, 2213, 26972, 27663, 4483, 14367, 19230, 29085, 12759, 6084 };
	private int CHEST[] = { 27663, 4483, 12308, 21301, 42192 };
	private int BANKER[] = { 7605, 6532, 6533, 6534, 6535, 5913,
			5912, 2271, 14367, 3824, 44, 45, 2354, 2355, 499, 5488, 8948, 958,
			494, 495, 6362, 5901 };
	//RS VARIABLES
	private RSInterface INTERFACE_FLETCH = RSInterface.getInterface(513);
	private RSInterfaceChild FLETCH_AREA = RSInterface.getChildInterface(513, 3);
	private RSObject bankBooth = getNearestObjectByID(BANKBOOTH);
	private RSObject bankChest = getNearestObjectByID(CHEST);
    private RSNPC banker = getNearestNPCByID(BANKER);
	//PAINT VARIABLES
    public long startTime = System.currentTimeMillis();
	private long waitTimer;
	private int startXP;
	private int startLevel;

	@Override
	public int getMouseSpeed(){
		return random(6, 7);
	}
	
	public boolean onStart(Map<String, String> args) {
		log("Getting information to start script, please wait a few seconds.");
		wait(random(800, 1000));
		//DECLARING VARIABLES
		antiBan = new RunAntiBan();
		t = new Thread(antiBan);
    	startTime = System.currentTimeMillis();
    	waitTimer = System.currentTimeMillis();
    	useAntiBan = args.get("antiBan");
    	Log = args.get("log");
    	Bow = args.get("bow");
    	Method = args.get("method");
    	AMOUNT = Integer.parseInt(args.get("amount"));
    	///////////////////
    	if (args.get("knife").equals("Normal")){
    		KNIFE = 946;
    	} else if (args.get("knife").equals("Clay")){
    		KNIFE = 14111;
    	}
    	///////////////////
    	if (args.get("log").equals("Normal")) {
    		LOG_ID = 1511;
    		SHORT_ID = 50;
    		LONG_ID = 48;
    		logName = "Normal";
    		log("We're going to be using Normal Logs.");
    	} else if (args.get("log").equals("Oak")) {
    		LOG_ID = 1521;
    		SHORT_ID = 54;
    		LONG_ID = 56;
    		logName = "Oak";
    		log("We're going to be using Oak Logs.");
    	} else if (args.get("log").equals("Willow")) {
    		LOG_ID = 1519;
    		SHORT_ID = 60;
    		LONG_ID = 58;
    		logName = "Willow";
    		log("We're going to be using Willow Logs.");
    	} else if (args.get("log").equals("Maple")) {
    		LOG_ID = 1517;
    		SHORT_ID = 64;
    		LONG_ID = 62;
    		logName = "Maple";
    		log("We're going to be using Maple Logs.");
    	} else if (args.get("log").equals("Yew")) {
    		LOG_ID = 1515;
    		SHORT_ID = 68;
    		LONG_ID = 66;
    		logName = "Yew";
    		log("We're going to be using Yew Logs.");
    	} else {
    		LOG_ID = 1513;
    		SHORT_ID = 72;
    		LONG_ID = 70;
    		logName = "Magic";
    		log("We're going to be using Magic Logs.");
    	}
    	/////////////////
		setCameraAltitude(true);
		wait(500);
		log("Your starting level is: " + skills.getRealSkillLevel(STAT_FLETCHING));
		wait(500);
		log("Information recied; script starting!");
		log("...");
		log("..");
		log(".");
        return true;
    }

    public void fletchBows() {
    	if (!Log.equals("Magic") && !Log.equals("Normal")) {
            if (Bow.contains("Short")) {
                moveMouse(random(52, 147), random(390, 450));
                wait(random(400, 800));
                atMenu("Make X");
                wait(random(1200, 1400));
                sendText("32", true);
                wait(2000);
            } else {
                moveMouse(random(215, 309), random(390, 450));
                wait(random(400, 800));
                atMenu("Make X");
                wait(random(1200, 1400));
                sendText("32", true);
                wait(2000);
            }
        } else if (Log.equals("Magic")) {
            if (Bow.contains("Short")) {
                moveMouse(random(95, 172), random(376, 462));
                wait(random(400, 800));
                atMenu("Make X");
                wait(random(1200, 1400));
                sendText("32", true);
                wait(2000);
            } else {
                moveMouse(random(341, 432), random(391, 464));
                wait(random(400, 800));
                atMenu("Make X");
                wait(random(1200, 1400));
                sendText("32", true);
                wait(2000);
            }
        } else if (Log.equals("Normal")) {
            if (Bow.contains("Short")) {
                moveMouse(random(170, 225), random(391, 454));
                wait(random(400, 800));
                atMenu("Make X");
                wait(random(1200, 1400));
                sendText("32", true);
                wait(2000);
            } else {
                moveMouse(random(287, 364), random(391, 454));
                wait(random(400, 800));
                atMenu("Make X");
                wait(random(1200, 1400));
                sendText("32", true);
                wait(2000);
            }
        }
    }

    public void openBank() {
		if(bankBooth != null && !bank.isOpen()) {
	    	atObject(bankBooth, "Use-quickly ");
	    	wait(random(800, 1000));
	    	moveMouseSlightly();
	    } else if (bankChest != null && !bank.isOpen()) {
	    	atObject(bankChest, "Use ");
			wait(random(800, 1000));
			moveMouseSlightly();
		} else {
	    	atNPC(banker, "Bank ");
	    	wait(random(800, 1000));
	    	moveMouseSlightly();
	    }
	}

    public void getKnife() {
    	openBank();
    	if (bank.isOpen()) {
    	   	bank.depositAll();
    	   	wait(random(400, 600));
    	   	bank.withdraw(KNIFE, 1);
    	   }
    }

	public void doBankFletching() {
		openBank();
	    if (bank.isOpen()) {
	    	bank.depositAllExcept(KNIFE);
	    	wait(random(800, 1000));
	    	bank.withdraw(LOG_ID, 0);
	    	wait(random(800, 1000));
	    }
    }

    public void doBankStringing() {
    	openBank();
    	if (Bow.equals("Shortbow")) {
	        if (bank.isOpen()) {
	        	bank.depositAll();
	        	wait(random(800, 1000));
	        }
	        if (bank.isOpen() && !inventoryContains(SHORT_ID)) {
    	    	bank.withdraw(SHORT_ID, 14);
    	    	wait(random(800, 1000));
			}
			if (bank.isOpen() && !inventoryContains(BOW_STRING)) {
    	    	bank.withdraw(BOW_STRING, 14);
    	    	wait(random(800, 1000));
			}
    	} else if (Bow.equals("Longbow")) {
	        if (bank.isOpen()) {
	        	bank.depositAll();
	        	wait(random(800, 1000));
	        }
	        if (bank.isOpen() && !inventoryContains(LONG_ID)) {
    	    	bank.withdraw(LONG_ID, 14);
    	    	wait(random(800, 1000));
			}
			if (bank.isOpen() && !inventoryContains(BOW_STRING)) {
				bank.withdraw(BOW_STRING, 14);
    	    	wait(random(800, 1000));
			}
    	}
    }

    public int loop() {
    	if (useAntiBan != null) {
	    	if (!t.isAlive()) {
	            t.start();
	            log("antiBan has been initialized! You're now human-like.");
	    	}
    	}
    	
    	if (amountFletch == AMOUNT || amountStrung == AMOUNT) {
    		stopScript();
    	}

    	if (Method.equals("Fletching")) {
    		if (inventoryContainsOneOf(KNIFE) && inventoryContains(LOG_ID) && (System.currentTimeMillis() - waitTimer) > 2000 && !INTERFACE_FLETCH.isValid() && !bank.isOpen()) {
    			atInventoryItem(KNIFE, "Use");
    			wait(random(500, 600));
    			if (isItemSelected()) {
        			atInventoryItem(LOG_ID, "Use");
        			wait(random(1000, 1300));
    			} else {
        			moveMouse(random(650, 660), random(180, 190));
        			clickMouse(true);
    			}
    		}
    		if (INTERFACE_FLETCH.isValid()) {
    			fletchBows();
    		}
    		if (!inventoryContainsOneOf(LOG_ID) && (System.currentTimeMillis() - waitTimer) > 1000) {
    			doBankFletching();
    		}
    		if (inventoryContainsOneOf(LOG_ID) && bank.isOpen()) {
    	    	bank.close();
    	    	wait(random(100, 200));
    	    }
    		if (!inventoryContainsOneOf(KNIFE) && (System.currentTimeMillis() - waitTimer) > 1000) {
    			log("You don't have a knife, getting one.");
    			getKnife();
    		}
    		if (isItemSelected() && (System.currentTimeMillis() - waitTimer) > random(3500, 4000)) {
    			log("Doing Failsafe.");
    			moveMouse(random(650, 660), random(180, 190));
    			clickMouse(true);
    		}
    		if (getMyPlayer().getAnimation() != -1) {
    			waitTimer = System.currentTimeMillis();
    		}
    	}
    	
    	if (Method.equals("Stringing")) {
    		if (Bow.equals("Shortbow")) {
    			if (inventoryContains(SHORT_ID) && inventoryContains(BOW_STRING) && (System.currentTimeMillis() - waitTimer) > 2000 && !FLETCH_AREA.isValid() && !bank.isOpen()) {
    				atInventoryItem(SHORT_ID, "Use");
    				wait(random(500, 600));
    				if (isItemSelected()) {
    					atInventoryItem(BOW_STRING, "Use");
    					wait(random(1000, 1200));
    				} else {
    					log("Doing Failsafe.");
    	    			moveMouse(random(650, 660), random(180, 190));
    	    			clickMouse(true);
    				}
    			}
    			if (FLETCH_AREA.isValid()) {
    				atInterface(FLETCH_AREA, "Make All");
    				wait(random(3000, 3500));
    				moveMouseSlightly();
    			}
    			if (!inventoryContainsOneOf(SHORT_ID) && (System.currentTimeMillis() - waitTimer) > 1000 || !inventoryContainsOneOf(BOW_STRING) && (System.currentTimeMillis() - waitTimer) > 1000) {
    				doBankStringing();
    			}

    			if (inventoryContainsOneOf(SHORT_ID) && inventoryContainsOneOf(BOW_STRING) && bank.isOpen()) {
    		    	bank.close();
    		    	wait(random(100, 200));
    		    }

    			if (isItemSelected() && (System.currentTimeMillis() - waitTimer) > random(3500, 4000)) {
    				log("Doing Failsafe.");
    				moveMouse(random(650, 660), random(180, 190));
    				clickMouse(true);
    			}

    			if (getMyPlayer().getAnimation() != -1) {
    				waitTimer = System.currentTimeMillis();
    			}
    		}
    		
    		if (Bow.equals("Longbow")) {
    			if (inventoryContains(LONG_ID) && inventoryContains(BOW_STRING) && (System.currentTimeMillis() - waitTimer) > 2000 && !FLETCH_AREA.isValid() && !bank.isOpen()) {
    				atInventoryItem(LONG_ID, "Use");
    				wait(random(500, 600));
    				if (isItemSelected()) {
    					atInventoryItem(BOW_STRING, "Use");
    					wait(random(800, 1000));
    				} else {
    					log("Doing Failsafe.");
    	    			moveMouse(random(650, 660), random(180, 190));
    	    			clickMouse(true);
    				}
    			}
    			if (FLETCH_AREA.isValid()) {
    				atInterface(FLETCH_AREA, "Make All");
    				wait(random(3000, 3500));
    				moveMouseSlightly();
    			}
    			if (!inventoryContainsOneOf(LONG_ID) && (System.currentTimeMillis() - waitTimer) > 1000 || !inventoryContainsOneOf(BOW_STRING) && (System.currentTimeMillis() - waitTimer) > 1000) {
    				doBankStringing();
    			}
    			if (inventoryContainsOneOf(LONG_ID) && inventoryContainsOneOf(BOW_STRING) && bank.isOpen()) {
    		    	bank.close();
    		    	wait(random(100, 200));
    		    }
    			if (isItemSelected() && (System.currentTimeMillis() - waitTimer) > random(3500, 4000)) {
    				log("Doing Failsafe.");
    				moveMouse(random(650, 660), random(180, 190));
    				clickMouse(true);
    			}
    			if (getMyPlayer().getAnimation() != -1) {
    				waitTimer = System.currentTimeMillis();
    			}
    		}
    	}
        return random(100, 200);
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
		//TIMER VARIABLES
		long millis = System.currentTimeMillis() - startTime;
		long hours = millis / (1000 * 60 * 60);
		millis -= hours * (1000 * 60 * 60);
		long minutes = millis / (1000 * 60);
		millis -= minutes * (1000 * 60);
		long seconds = millis / 1000;
		//COLORS
		Color BACKGROUND = new Color(0, 0, 0, 75);
		Color GREEN = new Color(0, 139, 0, 255);
		if(isLoggedIn()){
			drawMouse(g);
			//XP / LEVELS
			int XPGained = 0;
			int LVLSGained = 0;
			int XPTNL = skills.getXPToNextLevel(STAT_FLETCHING);
			
			if (startXP == 0){
				startXP = skills.getCurrentSkillExp(STAT_FLETCHING);
			}
			
			XPGained = (skills.getCurrentSkillExp(STAT_FLETCHING) - startXP);
			
			if (startLevel == 0){
				startLevel = skills.getRealSkillLevel(STAT_FLETCHING);
			}
			
			LVLSGained = (skills.getRealSkillLevel(STAT_FLETCHING) - startLevel);
			final int XPHR = (int) ((XPGained) * 3600000D / (System.currentTimeMillis() - startTime));
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
	    	g.fillRect(4, 313, ((skills.getPercentToNextLevel(STAT_FLETCHING) * 2) - 25), 24);
	    	g.setColor(Color.WHITE);
	    	g.drawString(skills.getPercentToNextLevel(STAT_FLETCHING) + " % to " + (skills.getRealSkillLevel(STAT_FLETCHING) + 1) + " Fletching.", 30, 330);
			//STATISTICS
			g.setColor(GREEN);
			g.setFont(new Font("Palatino Linotype", Font.BOLD, 16));
			g.drawString("RawR Fletcher", 40, 180);
			g.setColor(Color.WHITE);
        	g.setFont(new Font("Arial", Font.PLAIN, 12));
        	g.drawString("Time running: " + hours + ":" + minutes + ":" + seconds, 10, 200);
        	g.drawString("Log Type: " + logName, 10, 215);
        	if (Method.equals("Fletching")) {
        		try {
				g.drawString(logName + "'s " + "Fletched: "+ amountFletch, 10, 230);
        		} catch(final Exception e) { e.printStackTrace(); }
			} else {
				try {
				g.drawString(logName + "'s " + "Strung: "+ amountStrung, 10, 230);
				} catch(final Exception e) { e.printStackTrace(); }
			}
        	g.drawString("Fletch lvl: " + skills.getRealSkillLevel(STAT_FLETCHING) + " Gained: " + LVLSGained + " lvls", 10, 245);
			g.drawString("XP Gained: "+ XPGained, 10, 260);
			g.drawString("XP / HR: " + XPHR, 10, 275);
			g.drawString("XP TNL: " + XPTNL, 10, 290);
			g.setFont(new Font("Palatino Linotype", Font.ITALIC, 10));
			g.drawString("- RawR" , 130, 300);
		}
    }

    public void serverMessageRecieved(ServerMessageEvent e) {
        String word = e.getMessage().toLowerCase();
        if (word.contains("shortbow") || word.contains("longbow")) {
            amountFletch++;
        }
        if (word.contains("string")) {
        	amountStrung++;
        }
    }
    
    public void onFinish() {
    	antiBan.stopThread = true;
        log("Thanks for using RawR Fletcher!");
        wait(500);
        log("Here's what the script did for you - ");
		if (Method.equals("Fletching")) {
			log("	Logs Fletched: " + amountFletch);
		} else {
			log("	Bows Strung: " + amountStrung);
		}
    }
    
    //Thanks Taha, I <3 MultiThreading!
    private class RunAntiBan implements Runnable {
        public boolean stopThread;
        public void run() {
        	while (!stopThread) {
        		try {
        			if (random(0, 15) == 0) {
                    final char[] LR = new char[] { KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT };
                    final char[] UD = new char[] { KeyEvent.VK_DOWN, KeyEvent.VK_UP };
                    final char[] LRUD = new char[] { KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_UP, KeyEvent.VK_UP };
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