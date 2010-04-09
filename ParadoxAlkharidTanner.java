import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.List;
import java.util.Map;

import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSTile;

/*
 / Paradox' Al Kharid Tanner
 / © 2009 Paradox Scripter
 / info[@]paradox-productions[.]net
 / http://paradox-productions.net/
 */

@ScriptManifest(authors = { "Paradox" }, category = "Money", name = "Paradox' Al Kharid Tanner", version = 1.4, description = "<html><head><title>Paradox Al Kharid Tanner</title><style type=\"text/css\">body { background: #EFEFEF; color: #222; font-family: Calibri, Trebushet MS, Verdana, sans-serif; font-size: 11px; padding: 5px; }select { background: #333; color: lime; font-family: Calibri, Trebushet MS, Verdana, sans-serif; font-size: 10px; border: 1px solid #000; }select option { padding: 2px; }</style></head><body><center><h2>Paradox Al Kharid Tanner V1.4</h2></center><br /><center><h3>Instructions</h3>Start the script in the bank of Al Karid with some money equipped.<br />Make sure the hides you want to use are visible when you open your bank account.<br /><br />Use debug paint:<br /><select name=\"useDebugPaint\"><option>Yes</option><option>No</option></select><br /><br />Type of the hides:<br /><select name=\"typeOfHides\"><option>Cowhides (Soft Leather)</option><option>Cowhides (Hard Leather)</option><option>Green Dragonhides</option><option>Blue Dragonhides</option><option>Red Dragonhides</option><option>Black Dragonhides</option></select></center><br /><center><span style=\"color: #000; font-size: 10px;\">Powered by <a href=\"http://paradox-productions.net/\"title=\"Paradox-Productions.Net Web Developments\">Paradox-Productions.Net</a></span></center></body></html>")
public class ParadoxAlkharidTanner extends Script implements PaintListener {

	// ITEM ID's
	private final int utcow = 1739;
	private final int tcows = 1741;
	private final int tcowh = 1743;

	private final int utgred = 1753;
	private final int tgred = 1745;

	private final int utblud = 1751;
	private final int tblud = 2505;

	private final int utredd = 1749;
	private final int tredd = 2507;

	private final int utblad = 1747;
	private final int tblad = 2509;

	private final int money = 995;

	private int uth = 0;
	private int ttc = 0;

	// NPC ID's
	private final int tanner = 2824;

	// TIME VARS
	private long scriptStartTime = 0;
	private long runTime = 0;
	private long seconds = 0;
	private long minutes = 0;
	private long hours = 0;
	// OTHER VARS
	private boolean debugPaint = false;
	private String action = null;
	private int actionID = 0;
	private final int bankID = 35647;
	private String hidesType;
	private int hidesTanned = 0;
	private int runCount = 0;
	private long hidesH = 0;
	private String errorOnFinish = "";

	// PATHS
	RSTile[] BankToTanner = new RSTile[] { new RSTile(3276, 3169),
			new RSTile(3278, 3180), new RSTile(3282, 3188),
			new RSTile(3275, 3191) };
	RSTile[] TannerToBank = new RSTile[] { new RSTile(3282, 3188),
			new RSTile(3279, 3180), new RSTile(3276, 3170),
			new RSTile(3270, 3167) };

	private boolean clickNPC(final RSNPC npc, final String action) {
		// THANKS TO RUSKI
		try {
			int a;
			final StringBuffer npcCommandBuf = new StringBuffer();
			npcCommandBuf.append(action);
			npcCommandBuf.append(" ");
			npcCommandBuf.append(npc.getName());
			final String npcCommand = npcCommandBuf.toString();
			for (a = 10; a-- >= 0;) {
				final List<String> menuItems = getMenuItems();
				if (menuItems.size() > 1) {
					if (listContainsString(menuItems, npcCommand)) {
						if (menuItems.get(0).contains(npcCommand)) {
							clickMouse(true);
							return true;
						} else {
							// clickMouse(false);
							wait(random(50, 200));
							return atMenu(npcCommand);
						}
					}
				}
				final Point screenLoc = npc.getScreenLocation();
				if (!pointOnScreen(screenLoc)) {
					return false;
				}
				final Point randomP = new Point(random(screenLoc.x - 10,
						screenLoc.x + 10), random(screenLoc.y - 10,
						screenLoc.y + 10));
				if (randomP.x >= 0 && randomP.y >= 0) {
					moveMouse(randomP);
				}
			}
			return false;
		} catch (final Exception e) {
			return false;
		}
	}

	private boolean deposit() {
		if (bank.isOpen()) {
			if (inventoryContains(ttc) == true) {
				hidesTanned = hidesTanned + getInventoryCount(ttc);
				bank.depositAllExcept(money);
				runCount = runCount + 1;
			}
			wait(random(400, 800));
			if (!inventoryContains(uth) && bank.getCount(uth) == 0) {
				bank.close();
				errorOnFinish = "Out of hides, mission accomplished.";
				stopScript();
			} else {
				bank.atItem(uth, "Withdraw-All");
			}
			if (!inventoryContains(utcow)) {
				if (getInventoryCount(money) < 20) {
					errorOnFinish = "Not enough money to tan hides.";
					bank.close();
					stopScript();
				}
			} else {
				if (getInventoryCount(money) < 1) {
					errorOnFinish = "Not enough money to tan hides.";
					bank.close();
					stopScript();
				}
			}
			bank.close();
		}
		return true;
	}

	private boolean isInArea(final int maxX, final int minY, final int minX,
			final int maxY) {
		final int x = getMyPlayer().getLocation().getX();
		final int y = getMyPlayer().getLocation().getY();
		if (x >= minX && x <= maxX && y >= minY && y <= maxY) {
			return true;
		}
		return false;
	}

	private boolean listContainsString(final List<String> list,
			final String string) {
		// THANKS TO RUSKI
		try {
			int a;
			for (a = list.size() - 1; a-- >= 0;) {
				if (list.get(a).contains(string)) {
					return true;
				}
			}
		} catch (final Exception e) {
		}
		return false;
	}

	// LOOP
	@Override
	public int loop() {
		mainSystem();
		if (actionID == 1) {
			wait(random(100, 250));
			walkPath(TannerToBank);
		}
		if (actionID == 2) {
			tradeTanner();
			wait(random(500, 600));
			tanHides();
		}
		if (actionID == 3) {
			if (bank.isOpen()) {
				deposit();
			} else {
				openBank();
			}
		}
		if (actionID == 4) {
			wait(random(100, 250));
			walkPath(BankToTanner);
		}
		return 100;
	}

	private void mainSystem() {
		try {
			if (isInArea(3277, 3189, 3273, 3194) && !inventoryContains(uth)) {
				action = "Walking to bank..";
				actionID = 1;
			} else if (isInArea(3277, 3189, 3273, 3194)
					&& inventoryContains(uth)) {
				action = "Tanning..";
				actionID = 2;
			} else if (isInArea(3272, 3164, 3269, 3173)
					&& !inventoryContains(uth)) {
				action = "Banking..";
				actionID = 3;
			} else if (isInArea(3272, 3164, 3269, 3173)
					&& inventoryContains(uth)) {
				action = "Walking to tanner..";
				actionID = 4;
			}
		} catch (final Exception e) {
		}
		return;
	}

	@Override
	public void onFinish() {
		if (errorOnFinish != "") {
			log(errorOnFinish);
		}
		log("Ran for " + hours + ":" + minutes + ":" + seconds + ".");
		log("Tanned " + hidesTanned + " " + hidesType + " in a total of "
				+ runCount + " runs.");
		log("Thank you for using "
				+ getClass().getAnnotation(ScriptManifest.class).name() + " V"
				+ getClass().getAnnotation(ScriptManifest.class).version());
	}

	// DEBUG PAINT
	public void onRepaint(final Graphics g) {
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
			hidesH = hidesTanned * 3600
					/ (hours * 60 * 60 + minutes * 60 + seconds);
		}
		if (debugPaint == true) {
			g.setColor(new Color(70, 110, 160, 155));
			g.fillRect(350, 10, 160, 110);
			g.setColor(new Color(75, 184, 255, 200));
			g.drawRect(350, 10, 160, 110);
			g.setColor(Color.YELLOW);
			g.setFont(new Font("Arial", Font.PLAIN, 12));
			g.drawString(getClass().getAnnotation(ScriptManifest.class).name(),
					355, 25);
			g.setColor(Color.WHITE);
			g.drawString("Version: "
					+ getClass().getAnnotation(ScriptManifest.class).version(),
					355, 40);
			g.drawString("Runtime: " + hours + ":" + minutes + ":" + seconds,
					355, 55);
			g.drawString("Total runs: " + runCount, 355, 70);
			g.drawString("Tanned hides banked: " + hidesTanned, 355, 85);
			g.drawString("Hides / hour: " + hidesH, 355, 100);
			g.drawString("Action: " + action, 355, 115);
		}
	}

	// STARTUP CHECK
	@Override
	public boolean onStart(final Map<String, String> args) {
		scriptStartTime = System.currentTimeMillis();
		if (isLoggedIn()) {
			setCompass('s');
			if (args.get("useDebugPaint").equals("Yes")) {
				debugPaint = true;
			}
			if (args.get("typeOfHides").equals("Cowhides (Soft Leather)")) {
				uth = utcow;
				ttc = tcows;
				hidesType = "Cowhides (Soft Leather)";
				return true;
			} else if (args.get("typeOfHides")
					.equals("Cowhides (Hard Leather)")) {
				uth = utcow;
				ttc = tcowh;
				hidesType = "Cowhides (Hard Leather)";
				return true;
			} else if (args.get("typeOfHides").equals("Green Dragonhides")) {
				uth = utgred;
				ttc = tgred;
				hidesType = "Green Dragonhides";
				return true;
			} else if (args.get("typeOfHides").equals("Blue Dragonhides")) {
				uth = utblud;
				ttc = tblud;
				hidesType = "Blue Dragonhides";
				return true;
			} else if (args.get("typeOfHides").equals("Red Dragonhides")) {
				uth = utredd;
				ttc = tredd;
				hidesType = "Red Dragonhides";
				return true;
			} else if (args.get("typeOfHides").equals("Black Dragonhides")) {
				uth = utblad;
				ttc = tblad;
				hidesType = "Black Dragonhides";
				return true;
			} else {
				return false;
			}
		} else {
			log("Please login first.");
			return false;
		}
	}

	@Override
	public boolean onTile(final RSTile tile, final String search,
			final String action) {
		if (!tile.isValid()) {
			return false;
		}
		final Point checkScreen = Calculations.tileToScreen(tile);
		if (!pointOnScreen(checkScreen)) {
			walkTo(tile);
			wait(random(340, 700));
		}
		try {
			Point screenLoc = null;
			for (int i = 0; i < 30; i++) {
				screenLoc = Calculations.tileToScreen(tile);
				if (!pointOnScreen(screenLoc)) {
					return false;
				}
				if (getMenuItems().get(0).toLowerCase().contains(
						search.toLowerCase())) {
					break;
				}
				if (getMouseLocation().equals(screenLoc)) {
					break;
				}
				moveMouse(screenLoc);
			}
			screenLoc = Calculations.tileToScreen(tile);
			if (getMenuItems().size() <= 1) {
				return false;
			}
			if (getMenuItems().get(0).toLowerCase().contains(
					action.toLowerCase())) {
				clickMouse(true);
				return true;
			} else {
				clickMouse(false);
				return atMenu(action);
			}
		} catch (final Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private void openBank() {
		wait(random(400, 700));
		onTile(getNearestObjectByID(bankID).getLocation(), "Bank booth",
				"quickly");
		wait(random(400, 600));
	}

	private boolean run(final int min) {
		if (isRunning() == false) {
			if (min <= getEnergy()) {
				setRun(true);
			}
		}
		return true;
	}

	private void tanHides() {
		if (!inventoryContains(ttc)) {
			if (RSInterface.getInterface(324).isValid()) {
				int xcor = 0;
				int ycor = 0;
				if (hidesType == "Cowhides (Soft Leather)") {
					xcor = random(73, 105);
					ycor = random(78, 115);
				} else if (hidesType == "Cowhides (Hard Leather)") {
					xcor = random(184, 220);
					ycor = random(78, 115);
				} else if (hidesType == "Green Dragonhides") {
					xcor = random(73, 105);
					ycor = random(215, 245);
				} else if (hidesType == "Blue Dragonhides") {
					xcor = random(184, 220);
					ycor = random(215, 245);
				} else if (hidesType == "Red Dragonhides") {
					xcor = random(298, 333);
					ycor = random(215, 245);
				} else if (hidesType == "Black Dragonhides") {
					xcor = random(411, 442);
					ycor = random(215, 245);
				}
				clickMouse(xcor, ycor, false);
				atMenu("All");
				wait(random(300, 500));
			}
		} else {
			actionID = 1;
		}
	}

	private void tradeTanner() {
		if (inventoryContains(money)) {
			if (!inventoryContains(utcow)) {
				if (getInventoryCount(money) < 20) {
					errorOnFinish = "Not enough money to tan hides.";
					bank.close();
					stopScript();
				}
			} else {
				if (getInventoryCount(money) < 3) {
					errorOnFinish = "Not enough money to tan hides.";
					bank.close();
					stopScript();
				}
			}
		}
		if (inventoryContains(ttc)) {
			actionID = 1;
		} else if (!RSInterface.getInterface(324).isValid()) {
			clickNPC(getNearestNPCByID(tanner), "Trade");
			wait(random(400, 650));
		}
	}

	// OTHER METHODS
	private void walkPath(final RSTile[] path) {
		run(random(70, 80));
		if (distanceTo(getDestination()) <= random(5, 7)
				|| !getMyPlayer().isMoving()) {
			walkPathMM(randomizePath(path, 2, 2), 15);
		}
	}
}