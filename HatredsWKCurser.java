import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.util.Map;

import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = { "Hatred" }, category = "Magic", name = "Hatred's WKCurser", version = 1.0,
        description = "<html><body><center><b>Hatred's WKCurser!<br>Now supports magical balances!</b><br><br>This script WILL check for updates, but will NOT download them.<br><br>Curse type: <select name='OPT'><option>Curse</option><option>Stun</option></select><br><br>Use Elemental Balance: <select name='EB'><option>No</option><option>Yes</option></select><br><br>F2P/P2P: <select name='ALL'><option>F2P</option><option>P2P</option></select></center></body></html>")
public class HatredsWKCurser extends Script implements PaintListener, ServerMessageListener {
	
	//Scripts static variables
	private static final int WHITEKNIGHT = 19;
	private static final int ELEMENTALBALANCE1 = 13721;
	
	//Scripts dynamic variables
	public int cursesCast = 0;
	public int startExp = skills.getCurrentSkillExp(STAT_MAGIC);
	public long startTime = System.currentTimeMillis();
	public boolean stun = false;
	public boolean members = false;
	public boolean usingEB = false;
	
	public RSTile start = null;
	
	/**
	 * Checks if a random antiban would be in use.
	 * The chance of the antiban actually doing something, is 21/2000 or 1.05%.	 
	 */
	private void antiban() {
		switch(random(0, 2000)) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
			moveMouse(new Point(random(555, 579), random(170, 200)));
			wait(random(400, 700));
			clickMouse(true);
			wait(random(500, 700));
			moveMouse(new Point(random(552, 609), random(352, 376)));
			wait(random(1500, 2000));
			moveMouse(new Point(random(734, 757), random(170, 200)));
			wait(random(400, 700));
			clickMouse(true);
			wait(random(500, 900));
		break;
		case 6:
		case 7:
		case 8:
		case 9:
		case 10:
			moveMouse(new Point(random(523, 548), random(470, 500)));
			wait(random(500, 600));
			clickMouse(true);
			wait(random(600, 700));
			moveMouse(new Point(random(594, 699), random(399, 401)));
			wait(random(1600, 2100));
			moveMouse(new Point(random(734, 757), random(170, 200)));
			wait(random(400, 700));
			clickMouse(true);
			wait(random(500, 900));
		break;
		case 11:
		case 12:
		case 13:
		case 14:
		case 15:
		case 16:
			//Moves camera slightly.
			setCameraRotation(getCameraAngle()+random(-50, 80));
		break;
		case 17:
		case 18:
		case 19:
		case 20:
		case 21:
			moveMouseSlightly();
		break;
		default:
		break;
		}
	}
	
	/**
	 * This method will curse the knights.
	 */
	private void curseKnights() {
		if (getMyPlayer().isMoving()) {
			walkTileOnScreen(start);
		}
		RSObject EB = null;
		RSNPC knight = null;
		try {
		if (!usingEB) {
			do { //It will do this until the knight variable is no longer equal to null
				try {
					knight = getNearestNPCByID(WHITEKNIGHT);
					//The following checks that the player IS on a gate tile and that the
					//knight (declared above) is north of the player.
					if (knight.getLocation().getY() == (getMyPlayer().getLocation().getY()+1)) {							
					} else {
						knight = null;
					}
				} catch (Exception e) {	}			
			} while (knight == null);
		} else {
			EB = getNearestObjectByID(ELEMENTALBALANCE1);
		}
			if (getMyPlayer().isMoving()) {
				walkTileOnScreen(start);
			}
			if(getCurrentTab() != 7) {
				moveMouse(new Point(random(734, 757), random(170, 200)));
				wait(random(400, 700));
				clickMouse(true);
				wait(random(500, 900));
			}
			//Checks for either stunning or cursing, and checks if their members
			//due to the recent update that removed the Bolt Enchanting spell for F2P
			if (stun == false) {
				if (members == true) {
					moveMouse(new Point(random(564, 579), random(270, 286)));
					wait(random(400, 600));
					clickMouse(true);
				} else {
					moveMouse(new Point(random(687, 700), random(245, 262)));
					wait(random(400, 600));
					clickMouse(true);
				}
			} else {
				moveMouse(new Point(random(662, 678), random(352, 371)));
				wait(random(400, 600));
				clickMouse(true);
			}
			wait(random(500, 700));
			if (!usingEB) atNPC(knight, "Cast");
			else atObject(EB, "Cast");
			wait(random(200, 500));
			cursesCast++;
			if (getMyPlayer().isMoving()) {
				walkTileOnScreen(start);
			}
		} catch (Exception e) {
			
		}
	}
	
	@Override
	public int loop() {
		antiban();
		if (getMyPlayer().isMoving()) {
			walkTileOnScreen(start);
		}
		curseKnights();
		return random(200, 300);
	}

	private final RenderingHints rh = new RenderingHints(
		RenderingHints.KEY_TEXT_ANTIALIASING,
		RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	
	public boolean onStart(final Map<String, String> args) {
		if (args.get("OPT").equals("Stun")) {
			stun = true;
		}
		if (args.get("ALL").equals("P2P")) {
			members = true;
		}
		if (args.get("EB").equals("Yes")) {
			usingEB = true;
		}
		start = getMyPlayer().getLocation();
		return true;
	}
	
	public void onRepaint(Graphics g) {
		long timeRunning = System.currentTimeMillis() - startTime;
		long expGained = skills.getCurrentSkillExp(STAT_MAGIC) - startExp;
		long hours = timeRunning / (60 * 60 * 1000);
		timeRunning -= hours * (60 * 60 * 1000);
		long minutes = timeRunning / (60 * 1000);
		timeRunning -= minutes * (60 * 1000);
		long seconds = timeRunning / 1000;
		timeRunning -= seconds * 1000;
		((Graphics2D)g).setRenderingHints(rh);
		g.setColor(new Color(0, 0, 0));
		g.fillRoundRect(242, 214, 273, 123, 4, 4);
		g.setColor(new Color(255, 255, 255));
		g.drawRoundRect(241, 213, 274, 124, 4, 4);
		g.setFont(new Font("Calibri", 0, 20));
		g.setColor(new Color(255, 255, 255));
		g.drawString("      Hatred's Curser & Stunner", 250, 233);
		g.setFont(new Font("Calibri", 0, 15));
		g.setColor(new Color(255, 255, 255));
		g.drawString("Time Running:", 248, 325);
		g.setFont(new Font("Calibri", 0, 15));
		g.setColor(new Color(255, 255, 255));
		g.drawString(""+hours+":"+minutes+":"+seconds, 344, 325);
		g.setFont(new Font("Calibri", 0, 15));
		g.setColor(new Color(255, 255, 255));
		g.drawString("Curses Cast:", 248, 257);
		g.setFont(new Font("Calibri", 0, 15));
		g.setColor(new Color(255, 255, 255));
		g.drawString(""+cursesCast, 330, 257);
		g.setFont(new Font("Calibri", 0, 15));
		g.setColor(new Color(255, 255, 255));
		g.drawString("Magic Experience:", 248, 274);
		g.setFont(new Font("Calibri", 0, 15));
		g.setColor(new Color(255, 255, 255));
		g.drawString(""+expGained, 368, 274);
		g.setFont(new Font("Calibri", 0, 15));
		g.setColor(new Color(255, 255, 255));
		g.drawString("Curses Per Hour:", 248, 291);
		g.setFont(new Font("Calibri", 0, 15));
		g.setColor(new Color(255, 255, 255));
		g.drawString("Magic EXP Per Hour:", 248, 308);
		g.setFont(new Font("Calibri", 0, 15));
		g.setColor(new Color(255, 255, 255));
		g.drawString(""+(cursesCast*3600)/(((System.currentTimeMillis()-startTime)+1000)/1000), 359, 291);
		g.setFont(new Font("Calibri", 0, 15));
		g.setColor(new Color(255, 255, 255));
		g.drawString(""+(expGained*3600)/(((System.currentTimeMillis()-startTime)+1000)/1000), 379, 308);
	}

	public void serverMessageRecieved(ServerMessageEvent e) {
		if (e.getMessage().contains("You do not have")) { //Out of runes.
			logout();
			stopScript();
			log("You have ran out of runes, logging you out.");
		}
	}

}