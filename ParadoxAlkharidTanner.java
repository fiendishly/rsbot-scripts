/*
 / Project: Paradox' Al Kharid Tanner
 / Email: info@paradox-productions.net
 / Website: http://rsbot.paradox-productions.net/
 / © 2009 - 2010 Wouter De Schuyter
 / NOTE: Please don't steal code!!
 */
import java.awt.Font;
import java.awt.Color;
import java.awt.Graphics;

import java.util.Map;

import java.net.URI;
import java.net.URL;

import java.lang.Math;

import org.rsbot.util.ScreenshotUtil;
import org.rsbot.script.Script;
import org.rsbot.script.Constants;
import org.rsbot.script.GEItemInfo;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSTile;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.ServerMessageListener;

@ScriptManifest(authors = { "Paradox" }, category = "Money", name = "Paradox' Al Kharid Tanner", version = 2.00, description = "<html>"
		+ "<head>"
		+ "<title>Paradox Al Kharid Tanner</title>"
		+ "<style type=\"text/css\">"
		+ "body {"
		+ "background: #EFEFEF;"
		+ "color: #222;"
		+ "font-family: Trebushet MS, Verdana, sans-serif;"
		+ "font-size: 10px;"
		+ "padding: 0;"
		+ "margin: 0;"
		+ "}"
		+ "select {"
		+ "font-family: Trebushet MS, Verdana, sans-serif;"
		+ "}"
		+ "div#header {"
		+ "background: #222;"
		+ "color: #EFEFEF;"
		+ "text-align: center;"
		+ "padding: 7px;"
		+ "}"
		+ "div#content {"
		+ "text-align: center;"
		+ "padding: 5px;"
		+ "font-family: Trebushet MS, Verdana, sans-serif;"
		+ "}"
		+ "div#footer {"
		+ "background: #222;"
		+ "padding: 3.5px;"
		+ "color: #EFEFEF;"
		+ "text-align: center;"
		+ "}"
		+ "</style>"
		+ "</head>"
		+ "<body>"
		+ "<div id=\"header\">"
		+ "<h2>Paradox Al Kharid Tanner</h2>"
		+ "</div>"
		+ "<div id=\"content\">"
		+ "<h3>Instructions</h3>"
		+ "Start the script in the bank of Al Karid with some money equipped.<br />"
		+ "Make sure the hides you want to use are visible when you open your bank account.<br /><br />"
		+ "<table>"
		+ "<tr>"
		+ "<td>Use debug paint:</td>"
		+ "<td>"
		+ "<select name=\"useDebugPaint\">"
		+ "<option>Yes</option>"
		+ "<option>No</option>"
		+ "</select>"
		+ "</td>"
		+ "</tr>"
		+ "<tr>"
		+ "<td>Type of the hides:</td>"
		+ "<td>"
		+ "<select name=\"typeOfHides\">"
		+ "<option>Cowhides (Soft Leather)</option>"
		+ "<option>Cowhides (Hard Leather)</option><option>Green Dragonhides</option>"
		+ "<option>Blue Dragonhides</option><option>Red Dragonhides</option>"
		+ "<option>Black Dragonhides</option>"
		+ "</select>"
		+ "</td>"
		+ "</tr>"
		+ "<tr>"
		+ "<td>Nickname <span style=\"color: #FF0000;\">*</span></td>"
		+ "<td><input type=\"text\" name=\"stats_nickname\" style=\"text-align: center;\" /></td>"
		+ "</tr>"
		+ "</table>"
		+ "<div style=\"background: #FFE5E5; color: #222222; font-size: 9px; margin: 10px 5px 0 5px; padding: 5px;\">"
		+ "<span style=\"color: #FF0000;\">* The script will communicate with an external webserver and collect data about runs, times, hides/hour ratio etc..<br />"
		+ "This will allow you to see stats on the project page and help me improve the script.<br />If left blank it will count as \"default\" user.<br /><b>DO NOT</b> USE YOUR RUNESCAPE USERNAME.</span>"
		+ "</div>"
		+ "<div style=\"background: #FFF5CC; font-size: 9px; padding: 5px; margin: 0 5px 5px 5px;\">On finish the script will open a page with stats from the current session.</div>"
		+ "</div>"
		+ "<div id=\"footer\">"
		+ "<a href=\"http://paradox-projects.tk/\" title=\"www.paradox-projects.tk\" style=\"font-size: 9px; color: #FFFFFF;\">www.paradox-projects.tk</a>"
		+ "</div>" + "</body>" + "</html>\n")
public class ParadoxAlkharidTanner extends Script implements PaintListener,
		ServerMessageListener {

	/*
	 * Variables**********
	 */
	// Items
	private final int utcow = 1739; // Cowhide Untanned
	private final int tcows = 1741; // Cowhide Tanned Soft
	private final int tcowh = 1743; // Cowhide Tanned Hard

	private final int utgred = 1753; // Green Dragon Hide Untanned
	private final int tgred = 1745; // Green Dragon Hide Tanned

	private final int utblud = 1751; // Blue Dragon Hide Untanned
	private final int tblud = 2505; // Blue Dragon Hide Tanned

	private final int utredd = 1749; // Red Dragon Hide Untanned
	private final int tredd = 2507; // Red Dragon Hide Tanned

	private final int utblad = 1747; // Black Dragon Hide Untanned
	private final int tblad = 2509; // Black Dragon Hide Tanned

	// Money
	private final int money = 995;

	// Objects
	private final int bankID = 35647;

	// In Program Used
	private int uth = 0; // Untanned
	private int ttc = 0; // Tanned

	// NPC
	private final int tanner = 2824; // Tanner

	// Time Vars
	private long scriptStartTime = 0;
	private long runTime = 0;
	private long seconds = 0;
	private long minutes = 0;
	private long hours = 0;

	// Paint Vars
	private int hidesLeft = 0;
	private int runCount = 0;
	private double profitH = 0;
	private double hidesH = 0;
	private int hidesTanned = 0;
	private double profitMade = 0;
	private int hideTanningCost = 20;

	// Update Vars
	private double lastProfit = 0;
	private int lastTanned = 0;
	private long lastUpdate = 0;
	private long lastRuntime = 0;
	private int lastRuncount = 0;

	// Profit Calc Vars
	private int tannedPrice = 0;
	private int untannedPrice = 0;
	private int profitPerHide = 0;

	// Other
	private boolean debugPaint = false;
	private String action = null;
	private String prevAction = null;
	private String errorOnFinish = "";
	private String statsNick = "default";
	private String API_URL = "http://rsbot.paradox-productions.net/API.php?action=";
	private String hidesType;
	private int actionID = 1;

	// Paths
	RSTile[] BankToTanner = new RSTile[] { new RSTile(3278, 3170),
			new RSTile(3282, 3181), new RSTile(3274, 3191) };
	RSTile[] TannerToBank = new RSTile[] { new RSTile(3282, 3181),
			new RSTile(3278, 3170), new RSTile(3270, 3166) };

	public boolean onStart(Map<String, String> args) {
		// Stat timer script
		scriptStartTime = System.currentTimeMillis();
		// Startup Check
		if (isLoggedIn()) {
			// Debugpaint enabled?
			if (args.get("useDebugPaint").equals("Yes")) {
				debugPaint = true;
			}
			// Set nickname for stats if not empty
			if (args.get("stats_nickname").equals("")) {
				statsNick = "default";
			} else {
				statsNick = args.get("stats_nickname");
			}
			// Check money
			if (!inventoryContains(money)) {
				log("You must have money in your inventory to start the script!");
				return false;
			} else {
				// Check hides
				if (args.get("typeOfHides").equals("Cowhides (Soft Leather)")) {
					uth = utcow;
					ttc = tcows;
					hidesType = "Cowhides (Soft Leather)";
					hideTanningCost = 1;
					log("Startup check: OK");
					log("Note: NEVER sell hides below mid, try selling at max!");
					return true;
				} else if (args.get("typeOfHides").equals(
						"Cowhides (Hard Leather)")) {
					uth = utcow;
					ttc = tcowh;
					hidesType = "Cowhides (Hard Leather)";
					hideTanningCost = 3;
					log("Startup check: OK");
					log("Note: NEVER sell hides below mid, try selling at max!");
					return true;
				} else if (args.get("typeOfHides").equals("Green Dragonhides")) {
					uth = utgred;
					ttc = tgred;
					hidesType = "Green Dragonhides";
					log("Startup check: OK");
					log("Note: NEVER sell hides below mid, try selling at max!");
					return true;
				} else if (args.get("typeOfHides").equals("Blue Dragonhides")) {
					uth = utblud;
					ttc = tblud;
					hidesType = "Blue Dragonhides";
					log("Startup check: OK");
					log("Note: NEVER sell hides below mid, try selling at max!");
					return true;
				} else if (args.get("typeOfHides").equals("Red Dragonhides")) {
					uth = utredd;
					ttc = tredd;
					hidesType = "Red Dragonhides";
					log("Startup check: OK");
					log("Note: NEVER sell hides below mid, try selling at max!");
					return true;
				} else if (args.get("typeOfHides").equals("Black Dragonhides")) {
					uth = utblad;
					ttc = tblad;
					hidesType = "Black Dragonhides";
					log("Startup check: OK");
					log("Note: NEVER sell hides below mid, try selling at max!");
					return true;
				} else {
					log("No hides type found.");
					return false;
				}
			}
		} else {
			log("Please login first.");
			log("Startup check: FAILED");
			return false;
		}
	}

	private int calcProfit(final int untanned, final int tanned) {
		try {
			GEItemInfo untannedInfo = grandExchange.loadItemInfo(untanned);
			untannedPrice = untannedInfo.getMarketPrice();

			GEItemInfo tannedInfo = grandExchange.loadItemInfo(tanned);
			tannedPrice = tannedInfo.getMarketPrice();
		} catch (Exception e) {
			return 0;
		}
		return (tannedPrice - (untannedPrice + (hidesTanned * hideTanningCost)));
	}

	public boolean moneyCheck() {
		if (hidesType.equals("Cowhides (Soft Leather)")) {
			if (getInventoryCount(money) < (1 * getInventoryCount(uth))) {
				errorOnFinish = "Not enough money to tan hides.";
				stopScript();
			}
		} else if (hidesType.equals("Cowhides (Hard Leather)")) {
			if (getInventoryCount(money) < (3 * getInventoryCount(uth))) {
				stopScript();
			}
		} else {
			if (getInventoryCount(money) < (20 * getInventoryCount(uth))) {
				stopScript();
			}
		}
		return true;
	}

	public boolean openBank() {
		final RSObject bank = getNearestObjectByID(bankID);
		if (bank == null) {
			return false;
		}
		if (!tileOnScreen(bank.getLocation())) {
			turnToTile(bank.getLocation(), 15);
			return false;
		} else {
			if (!getMyPlayer().isMoving()) {
				atTile(bank.getLocation(), "Use-quickly");
				wait(random(500, 750));
				return true;
			}
		}
		return false;
	}

	public boolean turnRunOn(final boolean running, final int energy) {
		if (running == false) {
			if (energy > random(65, 100)) {
				setRun(true);
				return true;
			}
		}
		return false;
	}

	public boolean banking() {
		if (RSInterface.getInterface(Constants.INTERFACE_BANK).isValid()) {
			if (inventoryContains(ttc) == true) {
				hidesTanned = hidesTanned + getInventoryCount(ttc);
				runCount++;
				bank.depositAllExcept(money);
				wait(random(150, 200));
			} else if (inventoryContains(uth) != true) {
				if (bank.getCount(uth) == 0) {
					bank.close();
					errorOnFinish = "All hides have been tanned, mission accomplished.";
					stopScript();
				} else {
					bank.atItem(uth, "Withdraw-All");
					hidesLeft = bank.getCount(uth);
					moneyCheck();
					wait(random(100, 200));
					bank.close();
				}
			}
		} else {
			openBank();
			wait(random(200, 500));
		}
		return true;
	}

	public boolean clickNPC(final RSNPC npc, final String action) {
		if (npc.getAnimation() != 0 && !getMyPlayer().isMoving()) {
			wait(100);
			moveMouseSlightly();
			atNPC(npc, action);
			if (waitToMove(750)) {
				while (getMyPlayer().isMoving()) {
					wait(random(100, 200));
				}
			}
			return true;
		}
		return false;
	}

	private boolean walkPath2(final RSTile[] path, final int randX,
			final int randY) {
		while (distanceTo(path[path.length - 1]) > 4) {
			if (!walkPathMM(path, randX, randY)
					|| !waitToMove(random(1500, 3000))) {
				// Failed to move, return...
				return false;
			}
			RSTile dest; // Used to get destination (red flag in minimap)
			while ((dest = getDestination()) != null
					&& distanceTo(dest) > random(2, 6)) {
				// Destination flag is found and the player is not near to it...
				wait(random(100, 250)); // Use sleep time to reduce CPU usage...
			}
		}
		// Successfully walked all the tiles...
		return true;
	}

	private boolean isInArea(final int minX, final int minY, final int maxX,
			final int maxY) {
		final int x = getMyPlayer().getLocation().getX();
		final int y = getMyPlayer().getLocation().getY();
		if (x >= minX && x <= maxX && y >= minY && y <= maxY) {
			return true;
		}
		return false;
	}

	private boolean tradeTanner() {
		if (!RSInterface.getInterface(324).isValid()) {
			if (inventoryContains(uth)) {
				if (!getMyPlayer().isMoving()) {
					try {
						clickNPC(getNearestNPCByID(tanner), "Trade");
						wait(random(100, 200));
						return true;
					} catch (final Exception e) {
					}
				}
			} else {
				actionID = 1;
			}
		}
		return false;
	}

	private boolean tanHides(final String hides, final int unTannedHides) {
		if (RSInterface.getInterface(324).isValid()) {
			if (inventoryContains(unTannedHides)) {
				try {
					if (hides.equals("Cowhides (Soft Leather)")
							&& unTannedHides == utcow) {
						atInterface(324, 1, "All");
						wait(100, 200);
					} else if (hides.equals("Cowhides (Hard Leather)")
							&& unTannedHides == utcow) {
						atInterface(324, 2, "All");
						wait(100, 200);
					} else if (hides.equals("Green Dragonhides")
							&& unTannedHides == utgred) {
						atInterface(324, 5, "All");
						wait(100, 200);
					} else if (hides.equals("Blue Dragonhides")
							&& unTannedHides == utblud) {
						atInterface(324, 6, "All");
						wait(100, 200);
					} else if (hides.equals("Red Dragonhides")
							&& unTannedHides == utredd) {
						atInterface(324, 7, "All");
						wait(100, 200);
					} else if (hides.equals("Black Dragonhides")
							&& unTannedHides == utblad) {
						atInterface(324, 8, "All");
						wait(100, 200);
					}
				} catch (final Exception e) {
				}
			} else {
				actionID = 1;
			}
		}
		return true;
	}

	private boolean antiBan(final int random) {
		return false;
	}

	private boolean updateStats(boolean update) {
		// Update every 300000 milliseconds (5min) unless update is true
		if (System.currentTimeMillis() - lastUpdate > 300000 || update) {
			String curURL = API_URL + "submit_stats" + "&nickname=" + statsNick
					+ "&hidesTanned=" + (hidesTanned - lastTanned)
					+ "&runCount=" + (runCount - lastRuncount) + "&profit="
					+ (profitMade - lastProfit) + "&time="
					+ (runTime - lastRuntime) + "&typeOfHides="
					+ hidesType.replace(" ", "%20");
			try {
				new URL(curURL).openStream();
				log("Updating stats..");
			} catch (final Exception e) {
				return false;
			}
			lastTanned = hidesTanned;
			lastProfit = profitMade;
			lastRuncount = runCount;
			lastRuntime = runTime;
			lastUpdate = System.currentTimeMillis();
			curURL = "";
		}
		return false;
	}

	private void mainEngine() {
		if (isInArea(3278, 3184, 3283, 3194)) {
			if (!inventoryContains(uth)) {
				action = "Walking to bank..";
				prevAction = action;
				actionID = 1;
			}
		} else if (isInArea(3270, 3189, 3277, 3194)) {
			action = "Tanning..";
			prevAction = action;
			if (inventoryContains(uth)) {
				actionID = 2;
			} else {
				actionID = 1;
				action = "Walking to bank..";
			}
		} else if (isInArea(3268, 3161, 3272, 3173)) {
			action = "Banking..";
			prevAction = action;
			if (inventoryContains(uth)) {
				actionID = 4;
				action = "Walking to tanner..";
			} else {
				actionID = 3;
			}
		} else if (isInArea(3273, 3161, 3279, 3173) && inventoryContains(uth)) {
			action = "Walking to tanner..";
			prevAction = action;
			actionID = 4;
		}
	}

	public int loop() {
		setCameraAltitude(true);
		mainEngine();
		moneyCheck();
		switch (actionID) {
		case 1:
			turnRunOn(isRunning(), getEnergy());
			try {
				walkPath2(TannerToBank, 1, 1);
			} catch (final Exception e) {
				log("Error walking to path.");
			}
			if (antiBan(random(1, 3))) {
				action = prevAction;
			}
			break;

		case 2:
			tradeTanner();
			wait(random(100, 200));
			tanHides(hidesType, uth);
			break;

		case 3:
			banking();
			if (inventoryContains(uth)) {
				actionID = 4;
			}
			break;

		case 4:
			turnRunOn(isRunning(), getEnergy());
			try {
				walkPath2(BankToTanner, 1, 1);
			} catch (final Exception e) {
				log("Error walking to path.");
			}
			if (antiBan(random(1, 3))) {
				action = prevAction;
			}
			break;
		}
		if (profitPerHide == 0) {
			profitPerHide = calcProfit(uth, ttc);
		}
		updateStats(false);
		return 100;
	}

	public void serverMessageRecieved(final ServerMessageEvent arg0) {
		
	}

	public static double round(double val, int places) {
		long factor = (long) Math.pow(10, places);
		val = val * factor;
		long tmp = Math.round(val);

		return (double) tmp / factor;
	}

	public void onRepaint(Graphics g) {
		runTime = System.currentTimeMillis() - scriptStartTime;
		seconds = runTime / 1000;
		if (seconds >= 60) {
			minutes = seconds / 60;
			seconds -= minutes * 60;
		}
		if (minutes >= 60) {
			hours = minutes / 60;
			minutes -= hours * 60;
		}
		if (seconds > 0) {
			hidesH = (hidesTanned * 3600)
					/ (hours * 60 * 60 + minutes * 60 + seconds);
		}
		profitMade = profitPerHide * hidesTanned;
		if (seconds > 0) {
			profitH = (profitMade * 3600)
					/ (hours * 60 * 60 + minutes * 60 + seconds);
		}
		if (debugPaint == true) {
			g.setColor(new Color(70, 110, 160, 155));
			g.fillRect(350, 10, 160, 155);
			g.setColor(Color.WHITE);
			g.drawRect(350, 10, 160, 155);
			g.setColor(Color.YELLOW);
			g.setFont(new Font("Trebuchet MS", Font.BOLD, 12));
			g.drawString(getClass().getAnnotation(ScriptManifest.class).name(),
					355, 25);
			g.setColor(Color.WHITE);
			g.setFont(new Font("Trebuchet MS", Font.PLAIN, 12));
			g.drawString("Version: "
					+ getClass().getAnnotation(ScriptManifest.class).version(),
					355, 40);
			g.drawString("Runtime: " + hours + ":" + minutes + ":" + seconds,
					355, 55);
			g.drawString("Total runs: " + runCount, 355, 70);
			g.drawString("Untanned hides left: " + hidesLeft, 355, 85);
			g.drawString("Tanned hides: " + hidesTanned, 355, 100);
			g.drawString("Hides / hour: " + hidesH, 355, 115);
			if (profitMade > 10000) {
				g.drawString("Profit: " + round((profitMade / 1000), 2) + "K",
						355, 130);
			} else {
				g.drawString("Profit: " + round(profitMade, 1) + " GP", 355,
						130);
			}
			if (profitH > 10000) {
				g.drawString("Profit / hour: " + round((profitH / 1000), 2)
						+ "K/h", 355, 145);
			} else {
				g.drawString("Profit / hour: " + round(profitH, 1) + " GP/h",
						355, 145);
			}
			g.drawString("Action: " + action, 355, 160);
		}
	}

	public void onFinish() {
		ScreenshotUtil.takeScreenshot(true);
		updateStats(true);
		if (errorOnFinish != "") {
			log(errorOnFinish);
		}
		log("Ran for " + hours + " hours, " + minutes + " minutes, " + seconds
				+ " seconds.");
		log("Tanned " + hidesTanned + " " + hidesType + " in a total of "
				+ runCount + " runs.");
		log("You have made a profit of " + round((profitMade / 1000), 2)
				+ "K with an average of " + round((profitH / 1000), 2) + "K/h.");
		log("Thank you for using "
				+ getClass().getAnnotation(ScriptManifest.class).name() + " V"
				+ getClass().getAnnotation(ScriptManifest.class).version() + "");
		if (!statsNick.equals("default")) {
			try {
				java.awt.Desktop
						.getDesktop()
						.browse(
								new URI(
										"http://rsbot.paradox-productions.net/ParadoxAlkharidTanner/?p=Session&nickname="
												+ statsNick.replace(" ", "%20")
												+ "&typeOfHides="
												+ hidesType.replace(" ", "%20")
												+ "&profit="
												+ profitMade
												+ "&time="
												+ runTime
												+ "&runs="
												+ runCount
												+ "&hcount="
												+ hidesTanned));
			} catch (Exception e) {
				log("Unable open session stats page.");
			}
		}
	}
}