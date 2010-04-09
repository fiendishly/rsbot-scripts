import java.awt.*;
import java.util.*;
import java.io.*;
import java.net.*;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;

import java.awt.event.KeyEvent;
import org.rsbot.bot.Bot;
import org.rsbot.script.*;
import org.rsbot.script.wrappers.*;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.event.events.ServerMessageEvent;

@ScriptManifest(authors = { "RawR" }, category = "Woodcutting", name = "Rawr Ivy Chopper", version = 1.49,
		description = "<html><head></head><body>" +
		"<center><h2>RawR Ivy Chopper</h2></center>This script supports all locations. When starting, face the Ivy and the bot will do the rest.<br /><br />" +
		"<b>Chopping location:</b> <select name='location'><option>Varrock Palace</option><option>Varrock Wall</option><option>N Fally</option><option>S Fally</option><option>Taverly</option><option>Ardougne</option><option>Yanille</option><option>CWars</option></select><br /><br />" +
		"Please have the 'Game' chat option set to <b>All</b>.<br /><br />" +
		"Please post Progress Reports with the new paint on the thread! Thanks. :]" +
		"</body></html\n")
public class RawrIvy extends Script implements PaintListener, ServerMessageListener {

	//INTs
	public double ivyExp = 332.5;
	public int amountChopped;
	public String state = "";
	private long waitTimer;
	public int[] birdNestID = {5070, 5071, 5072, 5073, 5074, 5075, 5076, 7413, 11966};
	public int[] hatchetID = {1349, 1351, 1353, 1355, 1357, 1359, 1361, 6739};
	public int[] ivyID = { 46318, 46320, 46322, 46324 };
	public int[] bankerID = { 6533, 6535 };

	//PAINT INTs
	public long startTime = System.currentTimeMillis();
	public int startexp;
	public int startlvl;
	public String ivyLocation;

	public boolean onStart(Map<String, String> args){
	////////////////////
	//AUTO UPDATER
	////////////////////
	URLConnection url = null;
    BufferedReader in = null;
    BufferedWriter out = null;
    if(JOptionPane.showConfirmDialog(null, "Would you like to check for updates?\nNote: This connects to the internet and will write files!") == 0){
        try{
            url = new URL("http://rawrivy.webs.com/scripts/RawrIvyVERSION.txt").openConnection();
            in = new BufferedReader(new InputStreamReader(url.getInputStream()));
            if(Double.parseDouble(in.readLine()) > getVersion()) {
                if(JOptionPane.showConfirmDialog(null, "Update found. Do you want to update?") == 0){
                    JOptionPane.showMessageDialog(null, "Please choose 'RawrIvy.java' in your scripts folder and hit 'Open'");
                    JFileChooser fc = new JFileChooser();
                        if(fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
                            url = new URL("http://rawrivy.webs.com/scripts/RawrIvy.java").openConnection();
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
                    JOptionPane.showMessageDialog(null, "You have the latest version. ;]");
                if(in != null)
                    in.close();
                if(out != null)
                    out.close();
            } catch (IOException e){
                log("Problem getting version :[");
                return false;
            }
        }
	/////////////////////////////////////////////////
	waitTimer = System.currentTimeMillis();
	startTime = System.currentTimeMillis();
	ivyLocation = args.get("location");
		return true;
	}

	private double getVersion(){
		return getClass().getAnnotation(ScriptManifest.class).version();
	}

	@Override
	public int getMouseSpeed(){
		return random(4, 6);
	}

	public void setCamera(){
		final int curZ = Bot.getClient().getCamPosZ();
		if (curZ <= -950 && curZ >= -1050) {
			return;
		} else {
			final char key = (char)(curZ < -1000 ? KeyEvent.VK_DOWN : KeyEvent.VK_UP);
			input.pressKey(key);
			final int finalZ = -1000 + random(-50, 51);
			while(key == (char)KeyEvent.VK_DOWN ? Bot.getClient().getCamPosZ() < finalZ : Bot.getClient().getCamPosZ() > finalZ) {
				wait(random(10, 20));
			}
			input.releaseKey(key);
		}
	}

	public int gatherNest(){
		RSItemTile birdNest = getGroundItemByID(birdNestID);
		if (birdNest != null && !isInventoryFull()){
			state = "Getting nest.";
			atTile(birdNest, "Take");
			wait(random(1000, 1500));
		}
		return 100;
	}

	public boolean atIvyNorth(final RSObject tree, final String action) {
		try {
			final RSTile loc1 = tree.getLocation();
			final RSTile loc4 = new RSTile(loc1.getX(), loc1.getY() + 1);

			final Point screenLoc = Calculations.tileToScreen(loc4.getX(), loc4.getY(), 10);
			if (screenLoc.x == -1 || screenLoc.y == -1) {
				return false;
			}
			moveMouse(screenLoc, 3, 3);
			wait(random(200, 300));
			return atMenu(action);
		} catch (final Exception e) {
			log("Small problem...");
			return false;
		}
	}

	public boolean atIvySouth(final RSObject tree, final String action) {
		try {
			final RSTile loc1 = tree.getLocation();
			final RSTile loc4 = new RSTile(loc1.getX(), loc1.getY() - 1);

			final Point screenLoc = Calculations.tileToScreen(loc4.getX(), loc4.getY(), 10);
			if (screenLoc.x == -1 || screenLoc.y == -1) {
				return false;
			}
			moveMouse(screenLoc, 3, 3);
			wait(random(200, 300));
			return atMenu(action);
		} catch (final Exception e) {
			log("Small problem...");
			return false;
		}
	}

	public boolean atIvyEast(final RSObject tree, final String action) {
		try {
			final RSTile loc1 = tree.getLocation();
			final RSTile loc4 = new RSTile(loc1.getX() + 1, loc1.getY());

			final Point screenLoc = Calculations.tileToScreen(loc4.getX(), loc4.getY(), 10);
			if (screenLoc.x == -1 || screenLoc.y == -1) {
				return false;
			}
			moveMouse(screenLoc, 3, 3);
			wait(random(200, 300));
			return atMenu(action);
		} catch (final Exception e) {
			log("Small problem...");
			return false;
		}
	}

	public boolean atIvyWest(final RSObject tree, final String action) {
		try {
			final RSTile loc1 = tree.getLocation();
			final RSTile loc4 = new RSTile(loc1.getX() - 1, loc1.getY());

			final Point screenLoc = Calculations.tileToScreen(loc4.getX(), loc4.getY(), 10);
			if (screenLoc.x == -1 || screenLoc.y == -1) {
				return false;
			}
			moveMouse(screenLoc, 3, 3);
			wait(random(200, 300));
			return atMenu(action);
		} catch (final Exception e) {
			log("Small problem...");
			return false;
		}
	}

	public void dropLogs(){
		if(inventoryContainsOneOf(1511)){
			atInventoryItem(1511, "Drop");
			wait(random(500, 700));
		}
	}

	public int loop(){
		setCamera();
		gatherNest();
		dropLogs();

		if (getEnergy() > random(50, 60)){
			setRun(true);
		}

		/////////////////////
		//VARROCK PALACE -
		/////////////////////
		if ( ivyLocation.equals("Varrock Palace") ){
			RSTile[] varrockPalace = {new RSTile(3219, 3498), new RSTile(3218, 3498), new RSTile(3217, 3498), new RSTile(3216, 3498) };
			RSObject Ivy = getNearestIvyByID(varrockPalace, ivyID);
			if (Ivy != null && distanceTo(Ivy) < 10 && (System.currentTimeMillis() - waitTimer) > random(1100, 1500)) {
				state = "Looking for Ivy.";
				setCompass('s');
				atIvySouth(Ivy, "Chop Ivy");
				wait(random(2000, 2500));
			}
			if (Ivy != null && getMyPlayer().getAnimation() != -1){
				state = "Chopping Ivy.";
				antiBan();
				waitTimer = System.currentTimeMillis();
			}
			return random(100, 200);
		}
		////////////////////
		//VARROCK WALL -
		////////////////////
		if ( ivyLocation.equals("Varrock Wall") ){
			RSTile[] varrockWall = { new RSTile(3233, 3461), new RSTile(3233, 3460), new RSTile(3233, 3459), new RSTile(3233, 3457), new RSTile(3233, 3456) };
			RSObject Ivy = getNearestIvyByID(varrockWall, ivyID);
			if (Ivy != null && distanceTo(Ivy) < 10 && (System.currentTimeMillis() - waitTimer) > random(1100, 1500)){
				state = "Looking for Ivy.";
				setCompass('e');
				atIvyEast(Ivy, "Chop Ivy");
				wait(random(2000, 2500));
			}
			if (Ivy != null && getMyPlayer().getAnimation() != -1){
				state = "Chopping Ivy.";
				antiBan();
				waitTimer = System.currentTimeMillis();
			}
			return random(100, 200);
		}
		/////////////////
		//N FALLY -
		/////////////////
		if ( ivyLocation.equals("N Fally") ){
			RSTile[] nFally = { new RSTile(3011, 3392), new RSTile(3012, 3392), new RSTile(3014, 3392), new RSTile(3015, 3392), new RSTile(3016, 3392), new RSTile(3017, 3392), new RSTile(3018, 3392) };
			RSObject Ivy = getNearestIvyByID(nFally, ivyID);
			if (Ivy != null && distanceTo(Ivy) < 10 && (System.currentTimeMillis() - waitTimer) > random(1100, 1500)){
				state = "Looking for Ivy.";
				setCompass('s');
				atIvySouth(Ivy, "Chop Ivy");
				wait(random(2000, 2500));
			}
			if (Ivy != null && getMyPlayer().getAnimation() != -1){
				state = "Chopping Ivy.";
				antiBan();
				waitTimer = System.currentTimeMillis();
			}
			return random(100, 200);
		}
		///////////////
		//S FALLY -
		///////////////
		if ( ivyLocation.equals("S Fally") ){
			RSTile[] sFally = { new RSTile(3052, 3328), new RSTile(3051, 3328), new RSTile(3049, 3328), new RSTile(3048, 3328), new RSTile(3047, 3328), new RSTile(3045, 3328), new RSTile(3044, 3328) };
			RSObject Ivy = getNearestIvyByID(sFally, ivyID);
			if (Ivy != null && distanceTo(Ivy) < 10 && (System.currentTimeMillis() - waitTimer) > random(1100, 1500)){
				state = "Looking for Ivy.";
				setCompass('n');
				atIvyNorth(Ivy, "Chop Ivy");
				wait(random(2000, 2500));
			}
			if (Ivy != null && getMyPlayer().getAnimation() != -1){
				state = "Chopping Ivy.";
				antiBan();
				waitTimer = System.currentTimeMillis();
			}
			return random(100, 200);
		}
		///////////////
		//TAVERLY -
		///////////////
		if ( ivyLocation.equals("Taverly") ){
			RSTile[] taverly = { new RSTile(2943, 3420), new RSTile(2943, 3419), new RSTile(2943, 3418), new RSTile(2943, 3417), new RSTile(2943, 3416) };
			RSObject Ivy = getNearestIvyByID(taverly, ivyID);
			if (Ivy != null && distanceTo(Ivy) < 10 && (System.currentTimeMillis() - waitTimer) > random(1100, 1500)){
				state = "Looking for Ivy.";
				setCompass('e');
				atIvyEast(Ivy, "Chop Ivy");
				wait(random(2000, 2500));
			}
			if (Ivy != null && getMyPlayer().getAnimation() != -1){
				state = "Chopping Ivy.";
				antiBan();
				waitTimer = System.currentTimeMillis();
			}
			return random(100, 200);
		}
		//////////////
		//ARDOUGNE -
		//////////////
		if ( ivyLocation.equals("Ardougne") ){
			RSTile[] ardougne = { new RSTile(2622, 3304), new RSTile(2622, 3305), new RSTile(2622, 3307), new RSTile(2622, 3308), new RSTile(2622, 3310) };
			RSObject Ivy = getNearestIvyByID(ardougne, ivyID);
			if (Ivy != null && distanceTo(Ivy) < 10 && (System.currentTimeMillis() - waitTimer) > random(1100, 1500)){
				state = "Looking for Ivy.";
				setCompass('w');
				atIvyWest(Ivy, "Chop Ivy");
				wait(random(2000, 2500));
			}
			if (Ivy != null && getMyPlayer().getAnimation() != -1){
				state = "Chopping Ivy.";
				antiBan();
				waitTimer = System.currentTimeMillis();
			}
			return random(100, 200);
		}
		////////////
		//YANILLE -
		////////////
		if ( ivyLocation.equals("Yanille") ){
			RSTile[] yanille = { new RSTile(2597, 3111), new  RSTile(2596, 3111), new RSTile(2595, 3111), new RSTile(2593, 3111), new RSTile(2592, 3111), new RSTile(2591, 3111) };
			RSObject Ivy = getNearestIvyByID(yanille, ivyID);
			if (Ivy != null && distanceTo(Ivy) < 10 && (System.currentTimeMillis() - waitTimer) > random(1100, 1500)){
				state = "Looking for Ivy.";
				setCompass('s');
				atIvySouth(Ivy, "Chop Ivy");
				wait(random(2000, 2500));
			}
			if (Ivy != null && getMyPlayer().getAnimation() != -1){
				state = "Chopping Ivy.";
				antiBan();
				waitTimer = System.currentTimeMillis();
			}
			return random(100, 200);
		}
		/////////////
		//CWARS -
		/////////////
		if ( ivyLocation.equals("CWars") ){
			RSTile[] cwars = {new RSTile(2430, 3068), new RSTile(2429, 3068), new RSTile(2428, 3068), new RSTile(2426, 3068), new RSTile(2425, 3068), new RSTile(2424, 3068), new RSTile(2423, 3068) };
			RSObject Ivy = getNearestIvyByID(cwars, ivyID);
			if (Ivy != null && distanceTo(Ivy) < 10  && (System.currentTimeMillis() - waitTimer) > random(1100, 1500)){
				state = "Looking for Ivy.";
				setCompass('n');
				atIvyNorth(Ivy, "Chop Ivy");
				wait(random(2000, 3000));

			}
			if (Ivy != null && getMyPlayer().getAnimation() != -1){
				state = "Chopping Ivy.";
				antiBan();
				waitTimer = System.currentTimeMillis();
			}
			return random(100, 200);
		}
		return random(100, 200);
	}

	public void serverMessageRecieved(final ServerMessageEvent arg0){
		final String serverString = arg0.getMessage();
		if (serverString.toLowerCase().contains("chop away some ivy")){
			amountChopped++;
		}
	}

	public void antiBan(){
		int randomNum = random(1, 30);
		int r = random(1,35);
		if (randomNum == 6){
			if (r == 1){
				if (getCurrentTab() != Constants.TAB_STATS){
					openTab(Constants.TAB_STATS);
					moveMouse(random(670, 690), random(400, 410));
					wait(random(1000, 1500));
				}
			}
			if (r == 2){
				openTab(random(1, 14));
				wait(random(5000, 10000));
			}
			if (r == 3){
				int x = input.getX();
				int y = input.getY();
				moveMouse(x + random(-90, 90), y + random(-90, 90));
				wait(random(1000, 1500));
			}
			if (r == 4){
				int x2 = input.getX();
				int y2 = input.getY();
				moveMouse(x2 + random(-90, 90), y2 + random(-90, 90));
				wait(random(1000, 1500));
			}
			if (r == 5){
				int x3 = input.getX();
				int y3 = input.getY();
				moveMouse(x3 + random(-80, 80), y3 + random(-80, 80));
				wait(random(1000, 1500));
			}
			if (r == 6){
				int x3 = input.getX();
				int y3 = input.getY();
				moveMouse(x3 + random(-100, 100), y3 + random(-100, 100));
				wait(random(1000, 1500));
			}
			if (r == 7){
				int x3 = input.getX();
				int y3 = input.getY();
				moveMouse(x3 + random(-100, 100), y3 + random(-80, 80));
				wait(random(1000, 1500));
			}
			if (r == 8){
				setCameraRotation(random(100, 360));
				wait(random(1000, 1500));
			}
			if (r == 9){
				setCameraRotation(random(100, 360));
				wait(random(1000, 1500));
			}
			if (r == 10){
				setCameraRotation(random(100, 360));
				wait(random(1000, 1500));
			}
		}
	}

	public void onFinish(){
		log("Thanks for using my script - RawR.");
		log("Ivy cut: " + amountChopped + ".");
	}

	//As usual a WRONG @override attribute
	public void onRepaint(Graphics g) {
		//////////////
		//TIMER INTs
		//////////////
		long millis = System.currentTimeMillis() - startTime;
		long hours = millis / (1000 * 60 * 60);
		millis -= hours * (1000 * 60 * 60);
		long minutes = millis / (1000 * 60);
		millis -= minutes * (1000 * 60);
		long seconds = millis / 1000;
		//////////////
		//COLOR INTs
		//////////////
		// Color BG = new Color(0, 139, 0, 75);
		Color RED = new Color(255, 0, 0, 255);
		Color GREEN = new Color(0, 255, 0, 255);
		Color BLACK = new Color(0, 0, 0, 255);
		if (isLoggedIn()){
			//////////////
			//EXP INTs
			//////////////
			int expGained = 0;
			if(startexp == 0){
				startexp = skills.getCurrentSkillExp(STAT_WOODCUTTING);
			}
			expGained = skills.getCurrentSkillExp(STAT_WOODCUTTING) - startexp;
			int lvlsGained = 0;
			if(startlvl == 0){
				startlvl = skills.getCurrentSkillLevel(STAT_WOODCUTTING);
			}
			lvlsGained = skills.getCurrentSkillLevel(STAT_WOODCUTTING) - startlvl;
			int xpToLvl = skills.getXPToNextLevel(STAT_WOODCUTTING);
			///////////////////////////
			final int xpHr = (int) ((expGained) * 3600000D / (System.currentTimeMillis() - startTime));
			////////////////////////////
				//background
				g.setColor(new Color(72, 61, 139, 125));
				g.fillRoundRect(5, 137, 165, 200, 15, 15);
				g.setColor(new Color(47, 47, 89, 200));
				g.fillRoundRect(15, 147, 145, 180, 15, 15);
				//% bar
				g.setColor(RED);
				g.fill3DRect(20, 247, 100, 11, true);
				g.setColor(GREEN);
				g.fill3DRect(20, 247, skills.getPercentToNextLevel(Constants.STAT_WOODCUTTING), 11, true);
				g.setColor(BLACK);
				g.drawString(skills.getPercentToNextLevel(Constants.STAT_WOODCUTTING) + "%  to " + (skills.getCurrentSkillLevel(Constants.STAT_WOODCUTTING) + 1), 41, 257);
				//others
				g.setColor(Color.WHITE);
				g.setFont(new Font("Palatino Linotype", Font.BOLD, 16));
				g.drawString("Ivy Chopper", 40, 159);
				g.setFont(new Font("Palatino Linotype", Font.ITALIC, 11));
				g.drawString("by RawR" , 79, 171);
				g.setFont(new Font("Trajan Pro", Font.PLAIN, 13));
				g.drawString("XP to level: " + xpToLvl, 20, 294);
				g.drawString("Xp / Hr: " + xpHr, 20, 276);
				g.drawString("Time running: " + hours + ":" + minutes + ":" + seconds, 20, 240);
				g.drawString("Ivy Chopped: "+ amountChopped, 20, 222);
				g.drawString("Exp Gained: "+ expGained, 20, 204);
				g.drawString("Status: " + state, 20, 186);
				g.drawString("Levels Gained: " + lvlsGained, 20, 312);
		}
	}
	////////////////////////////////
	//Credits: Killa
	////////////////////////////////
	private RSObject getFenceAt3(int x, int y) {
		org.rsbot.accessors.RSObject rsObj;
		org.rsbot.accessors.RSInteractable obj;
		RSObject thisObject = null;
		final org.rsbot.accessors.Client client = Bot.getClient();
		try {
			final org.rsbot.accessors.RSGround rsGround = client
			.getRSGroundArray()[client.getPlane()][x
			                                       - client.getBaseX()][y - client.getBaseY()];
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
						final double distTmp = calculateDistance(getMyPlayer()
								.getLocation(), o.getLocation());
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
}