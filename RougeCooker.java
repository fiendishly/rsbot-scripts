/**			RougeCooker by Speed
 *
 *	A Speed's Scripting Production...
 *
 *
 *	Support & help can be found at: http://www.rsbot.org/vb/showthread.php?t=152146
 *
 *
 *	Made on 15th October 2009.
 *	Updated to Version 1.03 on 20th Feb 2010.
 *
 *	Thanks to Exempt for some variables and IDs (couldn't be bothered to get my own).
 *
 *	
 *	Version 1.00 - Script made.
 *	Version 1.01 - Updated antiban.
 *	Version 1.02 - Made more efficient. Redid some methods.
 *	Version 1.03 - Made sure everything is checked for nulls, fixed most bugs, etc. Made way faster.
 */

import java.awt.Color;
import java.awt.Graphics;
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
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSInterfaceComponent;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = { "Speed" }, category = "Cooking", name = "Rouge Cooker", version = 1.03, description = "<html>"
	+ "<head></head><body>"
	+ "<h1> Rouge Cooker by Speed </h1>"
	+ "<br><br>Run in Rogue's Den. Enter ID of Raw material here:"
	+ "<br>Food ID: <input type=\"text\" name=\"foodID\"><br><br>"
	+ "<b> Sponsored by: <a href = http://team-deathmatch.com>http://team-deathmatch.com</a></body></html>")
public class RougeCooker extends Script implements PaintListener, ServerMessageListener {
	private int tries = 0;
	private final int BANK_ID = 2271;
	private final int FIRE_ID = 2732;
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
	private RSTile fireTile = new RSTile(3043, 4972);
	private long startTime = System.currentTimeMillis();
	private int foodID;
	private int cookPerHour;
	private int xpPerHour;
	private final ScriptManifest properties = getClass().getAnnotation(ScriptManifest.class);

	public double getVersion() {
		return properties.version();
	}

	public int getMouseSpeed() {
		return random(4, 6);
	}

	public boolean onStart(Map<String, String> args) {
		URLConnection url = null;
		BufferedReader in = null;
		BufferedWriter out = null;
		//Ask the user if they'd like to check for an update...
		if(JOptionPane.showConfirmDialog(null, "Would you like to check for updates?\nPlease Note this requires an internet connection and the script will write files to your harddrive!") == 0){ //If they would, continue
			try{
				//Open the version text file
				url = new URL("http://www.scapemarket.info/scripts/RougeCookerVERSION.txt").openConnection();
				//Create an input stream for it
				in = new BufferedReader(new InputStreamReader(url.getInputStream()));
				//Check if the current version is outdated
				if(Double.parseDouble(in.readLine()) > getVersion()) {
					//If it is, check if the user would like to update.
					if(JOptionPane.showConfirmDialog(null, "Update found. Do you want to update?") == 0){
						//If so, allow the user to choose the file to be updated.
						JOptionPane.showMessageDialog(null, "Please choose 'RougeCooker.java' in your scripts folder and hit 'Open'");
						JFileChooser fc = new JFileChooser();
						//Make sure "Open" was clicked.
						if(fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
							//If so, set up the URL for the .java file and set up the IO.
							url = new URL("http://www.scapemarket.info/scripts/RougeCooker.java").openConnection();
							in = new BufferedReader(new InputStreamReader(url.getInputStream()));
							out = new BufferedWriter(new FileWriter(fc.getSelectedFile().getPath()));
							String inp;
							/* Until we reach the end of the file, write the next line in the file
							 * and add a new line. Then flush the buffer to ensure we lose
							 * no data in the process.
							 */
							while((inp = in.readLine()) != null){
								out.write(inp);
								out.newLine();
								out.flush();
							}
							//Notify the user that the script has been updated, and a recompile and reload is needed.
							log("Script successfully downloaded. Please recompile and reload your scripts!");
							return false;
						} else log("Update canceled");
					} else log("Update canceled");
				} else
					JOptionPane.showMessageDialog(null, "You have the latest version. :)"); //User has the latest version. Tell them!
				if(in != null)
					in.close();
				if(out != null)
					out.close();
			} catch (IOException e){
				log("Problem getting version :/");
				return false; //Return false if there was a problem
			}
		}  
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
		RSObject range = getNearestObjectByID(FIRE_ID);
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
		RSNPC Banker = getNearestNPCByID(BANK_ID);
		if(getSelectedInvItem() != -1) {
			atInventoryItem(getSelectedInvItem(), "Use");
		}
		if (Banker != null) {
			if (!bank.isOpen()) {
				RSTile bankerT = Banker.getLocation();
				if(distanceTo(bankerT) < 3) {
					if(tileOnScreen(Banker.getLocation())) {
						atNPC(Banker, "Bank");
					} else {
						turnToTile(Banker.getLocation());
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
		try{
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
		} catch(Exception e) {
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
