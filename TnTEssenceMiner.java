import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.rsbot.bot.Bot;
import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSPlayer;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest( authors = {"TwistedMind"}, category = "Mining", version = 1.34, name = "TnT Essence Miner", description = "This script mines rune essence in Varrock and Yanille.")
public class TnTEssenceMiner extends Script implements PaintListener, ServerMessageListener{
	
	public double version = getClass().getAnnotation(ScriptManifest.class).version();
	public long startTime;
	public int startXP;
	public Antiban antiban;
	public Thread t1;
	final int[] bankerIDs = {5913, 5912, 494, 495};
	final int essenceArea[] = { 2950, 4870, 2870, 4790 };
	final int varrockBankArea[] = { 3257, 3423, 3250, 3420 };
	final int yanilleBankArea[] = { 2613, 3097, 2609, 3088 };
	final int mageGuildX[] = new int[] { 2590, 2593, 2597, 2597, 2597, 2593,
			2586, 2585, 2585, 2586, 2588 };
	final int mageGuildY[] = new int[] { 3094, 3094, 3090, 3088, 3085, 3081,
			3082, 3087, 3088, 3090, 3092 };
	final Polygon mageGuild = new Polygon(mageGuildX, mageGuildY, 11);
	final RSTile varrockDoor = new RSTile(3253, 3399);
	final RSTile yanilleDoor = new RSTile(2597, 3088);
	final RSTile yanilleDoorCheck = new RSTile(2596, 3088);
	final RSTile varrockDoorCheck = new RSTile(3253, 3398);
	final int varrockUpStairsArea[] = { 3257, 3423, 3250, 3416 };
	final RSTile varrockPath[] = { new RSTile(3253, 3421),new RSTile(3258, 3411), new RSTile(3253, 3401), new RSTile(3253, 3400) };
	final RSTile yanillePath[] = {new RSTile(2611, 3093), new RSTile(2604,3090), new RSTile(2597, 3087)};
	final RSTile[] miningTiles = { new RSTile(2927, 4818), new RSTile(2931, 4818), new RSTile(2931, 4814),
			new RSTile(2927, 4814), new RSTile(2897, 4816), new RSTile(2897, 4812), new RSTile(2893, 4812),
			new RSTile(2893, 4816), new RSTile(2895, 4847), new RSTile(2891, 4847), new RSTile(2891, 4851),
			new RSTile(2895, 4851), new RSTile(2925, 4848), new RSTile(2925, 4852), new RSTile(2929, 4852),
			new RSTile(2929, 4848) };
	final int pickaxe[] = { 1265, 1267, 1269, 1296, 1273, 1271, 1275, 15259 };
	final int[] tilesX = { 1, 0, 0, 1, 0, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 0 };
	final int[] tilesY = { 0, 0, 1, 1, 0, 1, 1, 0, 1, 1, 0, 0, 1, 0, 0, 1 };
	int useX = 0;
	int useY = 0;
	public String status = "Loading...";
	public int value, lulz;
	public boolean foundValue, mineVar;

	public enum State{
		Antiban, ExitPortal, Mine, Aubury, Wait, Distentor, BankVarrock, BankYanille, Walk2BankYan, Walk2BankVar
	}

	public State getState(){
		if(getMyPlayer().getAnimation() != -1 || (distanceTo(getDestination()) >= random(3,6) && distanceTo(getDestination()) <= 30)){
			return State.Antiban;
		}
		if(isInventoryFull()){
			if(playerInArea(essenceArea)){
				status = "Exiting Mine";
				return State.ExitPortal;
			}
			if(playerInArea(varrockBankArea) || playerInArea(yanilleBankArea)){
				status = "Banking";
				if(mineVar){
					return State.BankVarrock;
				}else{
					return State.BankYanille;
				}
			}else{
				status = "Walking to Bank";
				if(mineVar){
					return State.Walk2BankVar;
				}else{
					return State.Walk2BankYan;
				}
			}
		}else{
			if(playerInArea(essenceArea)){
				status = "Mining";
				return State.Mine;
			}else{
				if(mineVar){
					status = "Walking to Aubury";
					return State.Aubury;
				}else{
					status = "Walking to Distentor";
					return State.Distentor;
				}
			}
		}
	}

	@Override
	public int loop() {
		try{
			if(startXP <= 1){
				if(skills.getCurrentSkillExp(STAT_MINING) <= 0){
					return 1;
				}
			}
			if(!t1.isAlive()){
				t1.start();
				log("Antiban initialized! Bot safe :)");
				log.severe("Bring a pickaxe! The script fails if you don't have one with you!");
				startXP = skills.getCurrentSkillExp(STAT_MINING);
			}
			if(RSInterface.getInterface(620) != null){
				atInterface(RSInterface.getInterface(620).getChild(18));
			}
			if (RSInterface.getInterface(109) != null){
				atInterface(RSInterface.getInterface(109).getChild(13));
			}
			if (getPlane() == 1 && playerInArea(varrockUpStairsArea)) {
				if (onTile(new RSTile(3256, 3421), "Climb", 0.5, 0.5, 0)) {
					wait(random(1500, 2000));
					while (getMyPlayer().isMoving()) {
						wait(random(90, 110));
					}
					wait(random(1500, 2000));
				}
				return random(50, 100);
			}
			
			getMouseSpeed();
			if(!foundValue){
					if (inventoryContainsOneOf(1436)) {
						value = grandExchange.loadItemInfo(1436).getMaxPrice();
						log("Current Rune Essence MAX Price (SELL IT MAX!): " + value + " gp each!");
						foundValue = true;
					} else if (inventoryContainsOneOf(7936)) {
						value = grandExchange.loadItemInfo(7936).getMaxPrice();
						log("Current Pure Essence MAX Price (SELL IT MAX!): " + value + " gp each!");
						foundValue = true;
					}
			}
			switch(getState()){
			case Mine:
				if(getInventoryCount() > 1 && getMyPlayer().getAnimation() == -1){
					wait(random(2000,3000));
					if(getMyPlayer().getAnimation() != -1 || isInventoryFull()){
						return 1;
					}
				}
				RSTile nearestTile = findNearestEssenceTile();
				if(!onTile(nearestTile, "Mine", useX, useY, 0)){
					if(!walkPathMM(randomizePath(generateFixedPath(nearestTile),2,2))){
						turnToTile(nearestTile);
						setCameraAltitude(false);
						walkTileOnScreen(nearestTile);
					}
				}
				waitToMove(1000);
				return random(1000,2000);
			case Aubury:
				if(!doorCheckVar()){
					RSNPC Aubury = getNearestNPCByID(553);
					if(Aubury != null){
						if(tileOnScreen(Aubury.getLocation())){
							if(!atNPC(Aubury, "Teleport")){
								return random(500,800);
							}
							int failCount = 0;
							while(!playerInArea(essenceArea)){
								wait(100);
								failCount++;
								if(failCount >= 50){
									break;
								}
							}
							return random(2000,4000);
						}else{
							walkTileMM(Aubury.getLocation());
							return random(500,800);
						}
					}else{
						if(!playerInArea(essenceArea)){
							if(!walkPathMM(varrockPath)){
								walkTileOnScreen(new RSTile(3253, 3401));
							}
						}
						return random(500,800);
					}
				}
			case Distentor:
				if(playerInArea(yanilleBankArea)){
					walkTileMM(yanillePath[1],2,2);
					return random(1000,2000);
				}
				if(!playerInArea(mageGuild)){
					if (!onTile(yanilleDoor, "Open", random(0.1, 0.2), random(-0.5, 0.5), random(40, 50))){
						walkPathMM(yanillePath);
						return random(1000,2000);
					}
				}else{
					RSNPC Distentor = getNearestNPCByID(462);
					if(Distentor != null){
							if(!atNPC(Distentor, "Teleport")){
								walkTileOnScreen(Distentor.getLocation());
								return random(400,600);
							}
							int failCount = 0;
							while(!playerInArea(essenceArea)){
								wait(100);
								failCount++;
								if(failCount >= 50){
									break;
								}
							}
							return random(2000,4000);
					}
				}
			case ExitPortal:
				RSObject portal = getNearestObjectByID(2492);
				if(portal != null){
					if(!tileOnScreen(portal.getLocation())){
						walk(portal.getLocation());
						turnToTile(portal.getLocation(), 15);
					}else{
						if(!atTile(portal.getLocation(), "Enter")){
							return random(500,800);
						}
						return random(2000,4000);
					}
				}
				return random(500,1000);
			case BankVarrock:
				if(!bank.isOpen()){
					int rand = random(1,3);
					if(rand == 1){
						RSObject booth = getNearestObjectByID(11402);
						if(booth != null){
							if(!atObject(booth, "Use-quickly")){
								if(!walkTileOnScreen(booth.getLocation())){
									return random(300,500);
								}
							}
							int failCount = 0;
							while(!bank.isOpen()){
								wait(100);
								failCount++;
								if(failCount >= 30){
									break;
								}
							}
							return 1;
						}else{
							walk(new RSTile(3253, 3421));
							return random(800, 1200);
						}
					}else{
						RSNPC banker = getNearestNPCByID(bankerIDs);
						if(banker != null){
							if(!atNPC(banker, "Bank Banker")){
								walkTileOnScreen(banker.getLocation());
							}
							int failCount = 0;
							while(!bank.isOpen()){
								wait(100);
								failCount++;
								if(failCount >= 30){
									break;
								}
							}
							return 1;
						}else{
							walk(new RSTile(3253, 3421));
							return random(800, 1200);
						}
					}
				}else{
					if(getInventoryCount(1436) == 28 || getInventoryCount(7936) == 28){
						if(!bank.depositAll()){
							return random(500,800);
						}
					}else{
						if(!bank.depositAllExcept(pickaxe)){
							return random(500,800);
						}
					}
					return random(500,1000);
				}
			case BankYanille:
				if(playerInArea(mageGuild)){
					if (!onTile(yanilleDoor, "Open", random(0.1, 0.2), random(-0.5, 0.5), random(40, 50))){
						walkTileOnScreen(new RSTile(yanilleDoor.getX(), yanilleDoor.getY() + 3));
						return random(1000,2000);
					}
				}else{
					if(!bank.isOpen()){
						int rand = random(1,3);
						if(rand == 1){
						RSObject booth = getNearestObjectByID(2213);
						if(booth != null){
							if(!atObject(booth, "Use-quickly")){
								walkTileOnScreen(booth.getLocation());
							}
							int failCount = 0;
							while(!bank.isOpen()){
								wait(100);
								failCount++;
								if(failCount >= 30){
									break;
								}
							}
							return 1;
						}else{
							walk(new RSTile(2611, 3092));
							return random(800, 1200);
						}
					}else{
						RSNPC banker = getNearestNPCByID(bankerIDs);
						if(banker != null){
							if(!atNPC(banker, "Bank Banker")){
								walkTileOnScreen(banker.getLocation());
							}
							int failCount = 0;
							while(!bank.isOpen()){
								wait(100);
								failCount++;
								if(failCount >= 30){
									break;
								}
							}
							return 1;
						}else{
							walk(new RSTile(2611, 3092));
							return random(800, 1200);
						}
					}
					}else{
						if(getInventoryCount(1436) == 28 || getInventoryCount(7936) == 28){
							if(!bank.depositAll()){
								return random(500,800);
							}
						}else{
							if(!bank.depositAllExcept(pickaxe)){
								return random(500,800);
							}
						}
						return random(500,1000);
					}
				}
			case Walk2BankVar:
				if(!doorCheckVar()){
					if(!walkPathMM(reversePath(varrockPath),2,3)){
						walkTileOnScreen(new RSTile(3253, 3421));
					}
				}
				return random(1000,1500);
			case Walk2BankYan:
				if(playerInArea(mageGuild)){
					if (!onTile(yanilleDoor, "Open", random(0.1, 0.2), random(-0.5, 0.5), random(40, 50))){
						walkTileOnScreen(new RSTile(yanilleDoor.getX(), yanilleDoor.getY() + 3));
						return random(1000,2000);
					}
				}else{
					if(!walkPathMM(reversePath(yanillePath),2,2)){
						walkTileOnScreen(new RSTile(2611, 3092));
					}
				}
				return random(1000,1500);
	        case Antiban:
	        	if(random(1,11)==1){
	        		antiban(1000,1500);
	        	}
	        case Wait:
			}
			return 100;
		}catch(java.lang.Throwable t){
			return 1;
		}
	}
	
	@Override
	public boolean onStart(Map <String, String> args){
		Object[] options = {"Varrock", "Yanille"};
		if(JOptionPane.showOptionDialog(null, "Are you mining in Varrock or Yanille?", "Varrock or Yanille?", 0, 3, null, options, options[0]) == 0){
			mineVar = true;
		}
		if(mineVar){
			log("You will be mining in Varrock");
		}else{
			log("You will be mining in Yanille");
		}
		
    	// All credits to Enfilade for his Updating snippet!
        URLConnection url = null;
        BufferedReader in = null;
        BufferedWriter out = null;
        if(JOptionPane.showConfirmDialog(null, "Would you like to check for updates?\nPlease Note this requires an internet connection and the script will write files to your harddrive!") == 0){
            try{
                url = new URL("http://tntscripting.webs.com/version/TnTEssenceMinerVERSION.txt").openConnection();
                in = new BufferedReader(new InputStreamReader(url.getInputStream()));
                if(Double.parseDouble(in.readLine()) > version) {
                    if(JOptionPane.showConfirmDialog(null, "Update found. Do you want to update?") == 0){
                           JOptionPane.showMessageDialog(null, "Please choose 'TnTEssenceMiner.java' in your scripts folder and hit 'Open'");
                           JFileChooser fc = new JFileChooser();
                           if(fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
                               url = new URL("http://tntscripting.webs.com/scripts/TnTEssenceMiner.java").openConnection();
                            in = new BufferedReader(new InputStreamReader(url.getInputStream()));
                            out = new BufferedWriter(new FileWriter(fc.getSelectedFile().getPath()));
                            String inp;
                            while((inp = in.readLine()) != null){
                                out.write(inp);
                                out.newLine();
                                out.flush();
                            }
                               log("Script successfully downloaded. Please recompile and reload your scripts!");
                            return false;
                           } else log("Update canceled");
                    } else log("Update canceled");
                } else
                    JOptionPane.showMessageDialog(null, "You have the latest version. :)");
                if(in != null)
                    in.close();
                if(out != null)
                    out.close();
            } catch (IOException e){
                log("Problem getting version :/");
                return false;
            }
        } 

		startTime = System.currentTimeMillis();
		antiban = new Antiban();
		t1 = new Thread(antiban);
		return true;
	}
	@Override
	public void onFinish(){
		antiban.stopThread = true;
	}

	public void onRepaint(Graphics g) {
        try{
            if(isLoggedIn()){
	            //---------Timer--------------------
	            long millis = System.currentTimeMillis() - startTime;
	            long runTime = millis;
	            long hours = millis / (1000 * 60 * 60);
	            millis -= hours * 1000 * 60 * 60;
	            long minutes = millis / (1000 * 60);
	            millis -= minutes * 1000 * 60;
	            long seconds = millis / 1000;
	            int expGained = skills.getCurrentSkillExp(STAT_MINING) - startXP;
	            int totalEss = expGained / 5;
	            int totalMoney = totalEss * value;
	            //Ensure a high quality and Smooth paint :)
	            ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	            ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
	            ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	            //Mouse crosshair
	            g.setColor(Color.BLACK);
	            g.drawLine(getMouseLocation().x, 0, getMouseLocation().x, 500);
	            g.drawLine(0, getMouseLocation().y, 762, getMouseLocation().y);
	            g.setColor(new Color(0,0,255,50));
	            g.fillRect(320,7,190,120);
	            g.setColor(Color.WHITE);
	            g.drawLine(365, 45, 510, 45);
	            g.drawRect(320,7,190,120);
	            g.setFont(new Font("Century Gothic", Font.BOLD, 16));
	            g.drawString("TnT Essence miner",345,30);
	            g.setFont(new Font("Microsoft Sans Serif", Font.PLAIN, 8));
	            g.drawString("V. " + Double.toString(version), 399, 41);//391,41
	            g.setFont(new Font("Microsoft Sans Serif", Font.PLAIN, 10));
	            g.drawString("Timer: " + hours + ":" + minutes + ":" + seconds, 330, 60);
	            g.drawString("Status: " + status, 330, 70);
	            g.drawString("Essence Mined: " + totalEss, 330, 80);
	            g.drawString("Experience gained: " + expGained, 330,90);
	            g.drawString("Money gained: " + totalMoney,330,100);
	            final int essPerHour = (int) (3600000.0 / runTime * totalEss);
	            g.drawString("Ess / Hour: " + Integer.toString(essPerHour),330, 110);
	            final int moneyPerHour = (int) (3600000.0 / runTime * totalMoney);
	            g.drawString("Money / Hour: " + Integer.toString(moneyPerHour), 330, 120);
	            g.setColor(Color.WHITE);
	            g.drawLine(320, 45, 510, 45);
	            g.setColor(Color.GRAY);
	            g.drawLine(321,44,509,44);
	            g.drawRect(321, 46, 188, 80);
            }
        }catch(java.lang.Throwable t){
            t.printStackTrace();
            log.severe("Paint error!");
        }
	}
	
	//--- Methods ---
	public boolean walk(RSTile tile){
		return walkPathMM(randomizePath(generateProperPath(tile),1,1));
	}
	
    @Override
    protected int getMouseSpeed(){
        return random(5,12);
    }
	
	public int antiban(int min, int max){
		//Check run:
		if(getEnergy() >= random(60,90)){
			setRun(true);
		}
		//Move Camera:
	    if(random(1,10) == 1){
	        setCameraRotation(random(1,359));
	    }
	    //Move mouse:
	    final int gamble = random(0, 10);
	    if (gamble < 2) {
	    moveMouse(random(7, 12), random(50, 500), random(100,
	            500), 30);
	    }
	    //Right click other players
	    final int chance2 = random(1,10);
	    Point mousePosition;
	    if(chance2 == 1){
	        mousePosition = getMouseLocation();
	        RSPlayer player = getNearestPlayerByLevel(random(3, 80), random(80,126));
	        if (player != null && distanceTo(player) != 0) {
	            moveMouse(player.getScreenLocation(), 5, 5);
	            wait(random(300, 700));
	            clickMouse(false);
	            wait(random(750, 1000));
	            moveMouse(mousePosition, 15, 15);
	        }
	    }
	    //Check Mining level:
	    if(random(1,50) == 1 && getMyPlayer().getAnimation() != -1){
		    if(getCurrentTab() != 1){
		    	openTab(1);
		    	moveMouse(new Point(703,222),29,11);
		    	wait(random(1500,3000));
		    }
	    }
	    
	    return random(min,max);
	}
		//Credits to Garrett
	public boolean doorCheckVar(){
		int failCount;
		if (getObjectAt(varrockDoorCheck) != null && distanceTo(new RSTile(3253, 3402)) <= 3) {
			if (onTile(varrockDoor, "Open", random(0.39, 0.61),	random(0, 0.05), random(20, 50))) {
				failCount = 0;
				while (getObjectAt(varrockDoor) == null && failCount < 40) {
					wait(random(50, 100));
					failCount++;
				}
			}
			if (getObjectAt(varrockDoor) == null) {
				wait(random(50, 100));
			}
		}else{
			return false;
		}
		return true;
	}
	
	public boolean doorCheckYan(){
		int failCount;
		if (getObjectAt(yanilleDoorCheck) != null && distanceTo(new RSTile(3253, 3402)) <= 3) {
			if (onTile(yanilleDoor, "Open", random(0.39, 0.61),	random(0, 0.05), random(20, 50))) {
				failCount = 0;
				while (getObjectAt(yanilleDoor) == null && failCount < 40) {
					wait(random(50, 100));
					failCount++;
				}
			}
			if (getObjectAt(yanilleDoor) == null) {
				wait(random(50, 100));
			}
		}else{
			return false;
		}
		return true;
	}

	public boolean playerInArea(final int[] area) {
		final int x = getMyPlayer().getLocation().getX();
		final int y = getMyPlayer().getLocation().getY();
		if (x >= area[2] && x <= area[0] && y >= area[3] && y <= area[1]) {
			return true;
		}
		return false;
	}
	
	public boolean playerInArea(final Polygon area) {
		return area.contains(new Point(getMyPlayer().getLocation().getX(),
				getMyPlayer().getLocation().getY()));
	}
	
	public RSTile findNearestEssenceTile(){
		RSTile tile = null;
		int closest = 999;
		for (int i = 0; i < miningTiles.length; i++) {
			if (distanceTo(miningTiles[i]) < closest) {
				closest = distanceTo(miningTiles[i]);
				tile = miningTiles[i];
				useX = tilesX[i];
				useY = tilesY[i];
			}
		}
		return tile;
	}
	
	public boolean onTile(final RSTile tile, final String action,
			final double dx, final double dy, final int height) {
		if (!tile.isValid()) {
			return false;
		}
		Point checkScreen;
		try {
			checkScreen = Calculations.tileToScreen(tile, dx, dy, height);
			if (!pointOnScreen(checkScreen)) {
				if (distanceTo(tile) <= 8) {
					if (getMyPlayer().isMoving()) {
						return false;
					}
					walkTileMM(tile);
					waitToMove(1000);
					return false;
				}
				return false;
			}
		} catch (final Exception e) {
		}
		try {
			boolean stop = false;
			for (int i = 0; i <= 50; i++) {
				checkScreen = Calculations.tileToScreen(tile, dx, dy, height);
				if (!pointOnScreen(checkScreen)) {
					return false;
				}
				moveMouse(checkScreen);
				final Object[] menuItems = getMenuItems().toArray();
				for (int a = 0; a < menuItems.length; a++) {
					if (menuItems[a].toString().toLowerCase().contains(action.toLowerCase())) {
						stop = true;
						break;
					}
				}
				if (stop) {
					break;
				}
			}
		} catch (final Exception e) {
		}
		try {
			return atMenu(action);
		} catch (final Exception e) {
		}
		return false;
	}
	
	public double pointDistance(final Point point) {
		if (point == null) {
			return Integer.MAX_VALUE;
		}
		final Point mouse = new Point(Bot.getInputManager().getX(), Bot
				.getInputManager().getY());
		return mouse.distance(point);
	}
	//------------------
    public class Antiban implements Runnable{
    	public boolean stopThread = false;
		public void run() {
            while(!stopThread){
                    try{
                    	if(isLoggedIn()){
	                        if (random(0, 10) == 0) {
	                                final char[] LR = new char[] { KeyEvent.VK_LEFT,
	                                                KeyEvent.VK_RIGHT };
	                                final char[] UD = new char[] { KeyEvent.VK_DOWN,
	                                                KeyEvent.VK_UP };
	                                final char[] LRUD = new char[] { KeyEvent.VK_LEFT,
	                                                KeyEvent.VK_RIGHT, KeyEvent.VK_UP,
	                                                KeyEvent.VK_UP };
	                                final int random1 = random(0, 2);
	                                final int random2 = random(0, 2);
	                                final int random4 = random(0, 4);
	
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
	                                        Bot.getInputManager().releaseKey(LRUD[random4]);
	                                }
	                        } else {
	                                Thread.sleep(random(200, 2000));
	                        }
                    	}else{
                        	Thread.sleep(random(200,2000));
                        }
                } catch (final Exception e) {
                        e.printStackTrace();
                }
            }
        }
    }

	public void serverMessageRecieved(ServerMessageEvent e) {
		String msg = e.getMessage();
		if(msg.contains("You've advanced a")){
			if(!isInventoryFull()){
				wait(random(1000,3000));
				onTile(findNearestEssenceTile(), "Mine", useX, useY, 0);
			}
		}
	}
}