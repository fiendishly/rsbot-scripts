import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Map;

import org.rsbot.bot.Bot;
import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceComponent;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSPlayer;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = { "Exempt", "DaRkY^" }, category = "Fletching", name = "E_n_D_Fletcher", version = 1.72, description = "<html>\n<head></head>\n<body style=\"text-align: center; font-family: Arial; padding: 5px;\">\n<p style=\"text-align: center;\"><b>E_n_D_Fletcher v1.72</b><br />Author: Exempt / DaRkY^<br /><br /><small>Updated by Jacmob</small><br /><br />You must be logged in to start the script.<br /><br />Fletching: <input type=checkbox name=fletchOp value=true></input><br />Using Clay Knife: <input type=checkbox name=clayKnifePick value=true></input><br /><br />What to fletch:<select name='fletchPick'><option>None<option>Short Bow<option>Long Bow</select><br />What type of logs:<select name='logPick'><option>None<option>Normal Logs<option>Oak Logs<option>Willow Logs<option>Maple Logs<option>Yew Logs<option>Magic Logs</select><br /><br /><hr /><br />Stringing: <input type=checkbox name=stringOp value=true></input><br /><br />What to string: <select name='stringPick'><option>None<option>Normal Short Bow<option>Normal Long Bow<option>Oak Short Bow<option>Oak Long Bow<option>Willow Short Bow<option>Willow Long Bow<option>Maple Short Bow<option>Maple Long Bow<option>Yew Short Bow<option>Yew Long Bow<option>Magic Short Bow<option>Magic Long Bow</select><br /><br /><hr /><br />If you use the built in antiban, turn your current antiban off.<br />Disable Status Display: <input type=checkbox name=paint value=true></input>Disable Antiban: <input type=checkbox name=antiban value=true></input><br /><br /><small>Adding to the delay will slow the script for laggers (1000 = 1 second)</small><br />Script Delay: <input type=text name=waitTime><br /><br /><br /></p></font></body>\n</html>")
public class E_n_D_Fletcher extends Script implements PaintListener,
		ServerMessageListener {

	private enum State {
		Bank, Fletch, String, Walk
	}

	// Variables
	int startXp;
	int startStatLvl;
	int wait1 = 250;
	int wait2 = 500;
	int fails = 0;
	int logPick = 0;
	int stringPick = 0;
	int knifePick = 0;
	int amountStrung = 0;
	int amountFletch = 0;
	boolean paint = true;
	boolean antiban = true;
	boolean fletchOp = false;
	boolean stringOp = false;
	boolean multiOp = false;
	boolean movingNPC = false;
	long startTime;
	long curTime;
	RSTile startTile;
	Point fletchPick;
	String status = "Starting.";

	final RSPlayer self = getMyPlayer();
	// final log ids
	final static int NORMAL = 1511;
	final static int OAK = 1521;
	final static int WILLOW = 1519;
	final static int MAPLE = 1517;
	final static int YEW = 1515;

	final static int MAGIC = 1513;
	// final knife id
	final static int KNIFE = 946;

	final static int CLAY_KNIFE = 14111;
	// final bow ids
	final static int NORMAL_SHORT = 50;
	final static int NORMAL_LONG = 48;
	final static int OAK_SHORT = 54;
	final static int OAK_LONG = 56;
	final static int WILLOW_SHORT = 60;
	final static int WILLOW_LONG = 58;
	final static int MAPLE_SHORT = 64;
	final static int MAPLE_LONG = 62;
	final static int YEW_SHORT = 68;
	final static int YEW_LONG = 66;
	final static int MAGIC_SHORT = 72;

	final static int MAGIC_LONG = 70;

	// final bow string id
	final static int BOW_STRING = 1777;

	// final bank booth ids
	final static int[] BANKS = { 11402, 2271, 36831, 26972, 42192, 11758,
			25808, 2213, 34752, 34752, 4735, 4736, 1252, 1189, 1187, 4483,
			27663, 12308, 14367, 35647, 36786 };
	// Final NPC Bankers
	final static int[] BANKNPCS = { 6535, 2605, 6532, 6533, 6534 };

	final static int[] MOVING_NPC = { 7605, 2271, 6538 };
	// final points
	final Point NORM_SHORT_BOW_P = new Point(200, 415);
	final Point NORM_LONG_BOW_P = new Point(320, 410);
	final Point SHORT_BOW_P = new Point(100, 420);
	final Point LONG_BOW_P = new Point(255, 410);
	final Point MAGIC_SHORT_BOW_P = new Point(131, 410);
	final Point MAGIC_LONG_BOW_P = new Point(391, 410);

	final Point STRING_BOW_P = new Point(260, 420);

	// Interfaces
	final RSInterface IFACE = RSInterface.getInterface(513);

	// Strings
	final String[] clickable = { "longbow", "shortbow", "Longbow", "Shortbow",
			"Kinfe", "Bow string" };

	Point stat = new Point(576, 185);

	Point inventory = new Point(644, 185);

	Point friend = new Point(573, 487);

	Point exp = new Point(630, 400);

	long antiTime = System.currentTimeMillis();

	public void antiban() {
		if (antiban && System.currentTimeMillis() - antiTime > 10000) {
			final char[] rotateLR = new char[] { KeyEvent.VK_LEFT,
					KeyEvent.VK_RIGHT };
			final int rand = random(0, 2);

			if (random(0, 7) == 1 && self.getAnimation() != -1
					|| self.isMoving()) {
				setCameraAltitude(true);
				Bot.getInputManager().pressKey(rotateLR[rand]);
				try {
					Thread.sleep(random(450, 1200));
				} catch (final Exception ignored) {
				}
				Bot.getInputManager().releaseKey(rotateLR[rand]);
			}
			if (random(0, 25) == 7 && self.getAnimation() != -1) {

				try {
					clickMouse(stat, 5, 5, true);
					Thread.sleep(random(450, 600));
					moveMouse(exp, 5, 5);
					Thread.sleep(random(1500, 2500));
					clickMouse(inventory, 5, 5, true);
				} catch (final Exception ignored) {
				}
			}
			if (random(0, 50) == 13 && self.getAnimation() != -1
					|| self.isMoving()) {
				try {
					clickMouse(friend, 5, 5, true);
					Thread.sleep(random(800, 1600));
					clickMouse(inventory, 5, 5, true);
				} catch (final Exception ignored) {
				}
			}
			if (random(0, 5) == 2 && self.getAnimation() != -1
					|| self.isMoving()) {
				moveMouse(random(0, 762), random(0, 502));
				try {
					Thread.sleep(random(450, 800));
				} catch (final Exception ignored) {
				}
			}
			antiTime = System.currentTimeMillis();
		}
	}

	@Override
	public boolean atInventoryItem(final int itemID, final String option) {
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
				if (items[i] == itemID) {
					possible.add(i);
				}
			}
			if (possible.size() == 0) {
				return false;
			}
			final int idx = possible.get(random(0, possible.size()));
			final Point t = getInventoryItemPoint(idx);
			moveMouse(t.x + 7, t.y + 7, 3, 3);
			getMenuItems();
			if (getMenuItems().get(0).contains(option)) {
				clickMouse(t, 5, 5, true);
				return true;
			} else {
				clickMouse(t, 5, 5, false);
				return atMenu(option);
			}
		} catch (final Exception e) {
			log("atInventoryItem(final int itemID, final String option) Error: "
					+ e);
			return false;
		}
	}

	public void bankClose() {
		final Point p = new Point(489, 35);
		clickMouse(p, 5, 5, true);
	}

	private State getState() {
		if (distanceTo(startTile) > 0
				&& self.getAnimation() == -1
				&& !RSInterface.getInterface(Constants.INTERFACE_BANK)
						.isValid()) {
			status = "Moving to start tile.";
			return State.Walk;
		}
		if (getInventoryCount(knifePick) > 0 && getInventoryCount(logPick) > 0
				&& fletchOp) {
			status = "Fletching.";
			return State.Fletch;
		}
		if (fletchOp && getInventoryCount(logPick) <= 0) {
			status = "Banking.";
			return State.Bank;
		}
		if (getInventoryCount(E_n_D_Fletcher.BOW_STRING) > 0
				&& getInventoryCount(stringPick) > 0 && stringOp) {
			status = "Stringing.";
			return State.String;
		}
		if (stringOp && getInventoryCount(logPick) <= 0
				|| getInventoryCount(E_n_D_Fletcher.BOW_STRING) <= 0) {
			status = "Banking.";
			return State.Bank;
		}
		return null;
	}

	@Override
	public int loop() {
		if (!isRunning() && getEnergy() > random(20, 30)) {
			setRun(true);
		}
		if (getMyPlayer().getAnimation() != -1) {
			wait(random(wait1 * 3, wait2 * 3));
		}
		antiban();
		switch (getState()) {
		case Bank:
			useBank();
			return random(wait1, wait2);
		case Fletch:
			startFletch();
			return random(wait1, wait2);
		case String:
			startString();
			return random(wait1, wait2);
		case Walk:
			if (!movingNPC) {
				walkTo(startTile);
				return random(wait1, wait2);
			} else {
				final RSTile moveTo = getNearestNPCByID(
						E_n_D_Fletcher.MOVING_NPC).getLocation();
				walkTo(moveTo);
				return random(wait1, wait2);
			}
		default:
			return random(wait1, wait2);
		}
	}

	@Override
	public void onFinish() {
		Bot.getEventManager().removeListener(PaintListener.class, this);
		log("Stopping " + "E_n_D_Fletcher");
	}

	public void onRepaint(final Graphics g) {
		if (isLoggedIn() && paint) {
			long millis = System.currentTimeMillis() - startTime;
			final long hours = millis / (1000 * 60 * 60);
			millis -= hours * 1000 * 60 * 60;
			final long minutes = millis / (1000 * 60);
			millis -= minutes * 1000 * 60;
			final long seconds = millis / 1000;

			final int levelChange = skills
					.getCurrentSkillLevel(Constants.STAT_FLETCHING)
					- startStatLvl;
			final int XPChange = skills
					.getCurrentSkillExp(Constants.STAT_FLETCHING)
					- startXp;
			float xpPerSec = 0;
			if ((minutes > 0 || hours > 0 || seconds > 0) && XPChange > 0) {
				xpPerSec = (float) XPChange
						/ (float) (seconds + minutes * 60 + hours * 60 * 60);
			}
			final float xpPerMin = xpPerSec * 60;
			final float xpPerHour = xpPerMin * 60;

			g.setColor(Color.black);
			g.fill3DRect(516, 0, 248, 167, true);
			g.setColor(Color.red);
			g.setFont(new Font("Batang", Font.BOLD, 14));
			g.drawString("E_n_D_Fletcher" + " v" + "1.72", 520, 15);
			g.setFont(new Font("Batang", Font.BOLD, 12));
			g.drawString("Runtime: " + hours + " : " + minutes + " : "
					+ seconds + "s ", 520, 30);
			g
					.drawString(
							"Exp Gained: "
									+ (skills
											.getCurrentSkillExp(Constants.STAT_FLETCHING) - startXp),
							520, 45);
			g.drawString("Exp Per Hour:   " + (int) xpPerHour, 520, 60);
			g.drawString("Percent TNL:   %"
					+ skills.getPercentToNextLevel(Constants.STAT_FLETCHING),
					520, 75);
			g.drawString("Bows Strung: " + amountStrung, 520, 90);
			g.drawString("Logs Fletched: " + amountFletch, 520, 105);
			g.drawString("Levels Gained: " + levelChange, 520, 120);
			g.drawString("Status: " + status, 520, 135);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onStart(final Map<String, String> args) {
		Bot.getEventManager().addListener(PaintListener.class, this);
		if (!isLoggedIn()
				|| RSInterface.getInterface(378).getChild(45).getAbsoluteX() > 20
				&& !RSInterface.getInterface(149).isValid()) {
			log("You must be logged in to start this script.");
			return false;
		} else {
			startTime = System.currentTimeMillis();
			curTime = System.currentTimeMillis();
			startXp = skills.getCurrentSkillExp(Constants.STAT_FLETCHING);
			startTile = self.getLocation();
			startStatLvl = skills
					.getCurrentSkillLevel(Constants.STAT_FLETCHING);
			log("Starting tile was saved. " + startTile);
		}
		final RSObject booth = getNearestObjectByID(E_n_D_Fletcher.BANKS);
		final RSNPC banker = getNearestNPCByID(E_n_D_Fletcher.BANKNPCS);
		final RSNPC mover = getNearestNPCByID(E_n_D_Fletcher.MOVING_NPC);
		if (booth == null && banker == null && mover == null) {
			log("Please be sure to start at one of the supported banks.");
			return false;
		}
		if (mover != null) {
			movingNPC = true;
		}
		if (args.get("fletchOp") == null && args.get("stringOp") == null) {
			log("You must choose to fletch or string before starting.");
			return false;
		}
		if (args.get("paint") != null) {
			paint = false;
			log("Paint Disabled");
		} else {
			log("Paint Enabled.");
		}
		if (args.get("antiban") != null) {
			antiban = false;
			log("Antiban Disabled");
		} else {
			log("Antiban Enabled.");
		}
		if (!args.get("waitTime").equals("")) {
			final int waitTime = Integer.parseInt(args.get("waitTime"));
			wait1 += waitTime;
			wait2 += waitTime;
			log("Adding " + waitTime + " to the delay.");
		} else {
			log("Using normal script delay");
		}
		if (args.get("fletchOp") != null) {
			fletchOp = true;
			log("Fletching.");
			if (args.get("clayKnifePick") != null) {
				log("Using clay knives.");
				knifePick = E_n_D_Fletcher.CLAY_KNIFE;
			} else {
				knifePick = E_n_D_Fletcher.KNIFE;
			}
			final String sFletchPick = args.get("fletchPick");
			final String sLogPick = args.get("logPick");
			if (sFletchPick.equals("None")) {
				log("You must select the type of bow to fletch.");
				return false;
			}
			if (sFletchPick.equals("Short Bow")
					&& !sLogPick.equals("Normal Logs")) {
				fletchPick = SHORT_BOW_P;
				log("Making short bows.");
			}
			if (sFletchPick.equals("Long Bow")
					&& !sLogPick.equals("Normal Logs")) {
				fletchPick = LONG_BOW_P;
				System.out.print("Making long bows.");
			}
			if (sFletchPick.equals("Short Bow")
					&& sLogPick.equals("Normal Logs")) {
				fletchPick = NORM_SHORT_BOW_P;
				log("Making short bows.");
			}
			if (sFletchPick.equals("Long Bow")
					&& sLogPick.equals("Normal Logs")) {
				fletchPick = NORM_LONG_BOW_P;
				System.out.print("Making long bows.");
			}
			if (sFletchPick.equals("Short Bow")
					&& sLogPick.equals("Magic Logs")) {
				fletchPick = MAGIC_SHORT_BOW_P;
				log("Making short bows.");
			}
			if (sFletchPick.equals("Long Bow") && sLogPick.equals("Magic Logs")) {
				fletchPick = MAGIC_LONG_BOW_P;
				System.out.print("Making long bows.");
			}
			if (sLogPick.equals("None")) {
				log("You must select a log type.");
				return false;
			} else if (sLogPick.equals("Normal Logs")) {
				log("Using normal logs.");
				logPick = E_n_D_Fletcher.NORMAL;
			} else if (sLogPick.equals("Oak Logs")) {
				log("Using oak logs.");
				logPick = E_n_D_Fletcher.OAK;
			} else if (sLogPick.equals("Willow Logs")) {
				log("Using willow longs.");
				logPick = E_n_D_Fletcher.WILLOW;
			} else if (sLogPick.equals("Maple Logs")) {
				log("Using maple logs.");
				logPick = E_n_D_Fletcher.MAPLE;
			} else if (sLogPick.equals("Yew Logs")) {
				log("Using yew logs.");
				logPick = E_n_D_Fletcher.YEW;
			} else if (sLogPick.equals("Magic Logs")) {
				log("Using magic logs.");
				logPick = E_n_D_Fletcher.MAGIC;
			}
		}
		if (args.get("stringOp") != null) {
			stringOp = true;
			log("Stringing.");
			final String sStringPick = args.get("stringPick");
			if (sStringPick.equals("None")) {
				log("You must select a bow type to string.");
				return false;
			} else if (sStringPick.equals("Normal Short Bow")) {
				log("Stringing normal short bows.");
				stringPick = E_n_D_Fletcher.NORMAL_SHORT;
			} else if (sStringPick.equals("Normal Long Bow")) {
				log("Stringing normal long bows.");
				stringPick = E_n_D_Fletcher.NORMAL_LONG;
			} else if (sStringPick.equals("Oak Short Bow")) {
				log("Stringing oak short bows.");
				stringPick = E_n_D_Fletcher.OAK_SHORT;
			} else if (sStringPick.equals("Oak Long Bow")) {
				log("Stringing oak long bows.");
				stringPick = E_n_D_Fletcher.OAK_LONG;
			} else if (sStringPick.equals("Willow Short Bow")) {
				log("Stringng willow short bows.");
				stringPick = E_n_D_Fletcher.WILLOW_SHORT;
			} else if (sStringPick.equals("Willow Long Bow")) {
				log("Stringing willow long bows.");
				stringPick = E_n_D_Fletcher.WILLOW_LONG;
			} else if (sStringPick.equals("Maple Short Bow")) {
				log("String maple short bows.");
				stringPick = E_n_D_Fletcher.MAPLE_SHORT;
			} else if (sStringPick.equals("Maple Long Bow")) {
				log("Stringing maple long bows.");
				stringPick = E_n_D_Fletcher.MAPLE_LONG;
			} else if (sStringPick.equals("Yew Short Bow")) {
				log("Stringing yew short bows.");
				stringPick = E_n_D_Fletcher.YEW_SHORT;
			} else if (sStringPick.equals("Yew Long Bow")) {
				log("Stringing yew long bows.");
				stringPick = E_n_D_Fletcher.YEW_LONG;
			} else if (sStringPick.equals("Magic Short Bow")) {
				log("Stringing magic short bows.");
				stringPick = E_n_D_Fletcher.MAGIC_SHORT;
			} else if (sStringPick.equals("Magic Long Bow")) {
				log("Stringing magic long bows.");
				stringPick = E_n_D_Fletcher.MAGIC_LONG;
			}
			if (args.get("fletchOp") != null && args.get("stringOp") != null) {
				log("Fletching all logs then stringing.");
				multiOp = true;
				stringOp = false;
			}
		}
		log("E_n_D_Fletcher" + " was successfully started!");
		return true;
	}

	private boolean sendTextCheck(final String text) {
		try {
			sendText(text, false);
			wait(random(200, 400));
			final String sent = RSInterface.getChildInterface(
					Constants.INTERFACE_BANK_SEARCH, 5).getText();
			if (sent.equals(text)) {
				sendKey((char) KeyEvent.VK_ENTER);
				return true;
			}
			for (int i = 0; i < text.length() + 1; i++) {
				sendKey((char) KeyEvent.VK_BACK_SPACE);
			}
		} catch (final Exception e) {
			log("sendTextCheck(String text) Error: " + e);
		}
		return false;
	}

	public void serverMessageRecieved(final ServerMessageEvent e) {
		final String word = e.getMessage().toLowerCase();
		if (word.contains("shortbow") || word.contains("longbow")) {
			amountFletch++;
		}
		if (word.contains("string")) {
			amountStrung++;
		}
	}

	public void startFletch() {
		try {
			if (RSInterface.getInterface(Constants.INTERFACE_BANK).isValid()) {
				bankClose();
			}
			if (self.getAnimation() != -1) {
				curTime = System.currentTimeMillis();
			}
			if (System.currentTimeMillis() - curTime > 3000) {
				if (IFACE.isValid()) {
					clickMouse(fletchPick, false);
					wait(random(wait1, wait2));
					final int count = getInventoryCount(logPick);
					if (count > 10) {
						atMenu("Make X");
						wait(random(wait1 * 3, wait2 * 3));
						sendTextCheck("" + count);
						curTime = System.currentTimeMillis();
					} else {
						atMenu("10");
						curTime = System.currentTimeMillis();
					}
				} else {
					atInventoryItem(knifePick, "Use");
					wait(random(wait1, wait2));
					atInventoryItem(logPick, "Use");
					wait(random(wait1 * 2, wait2 * 2));
				}
			}
		} catch (final Exception e) {
			log("startFletch(Point fletchType, int logType) Error: " + e);
		}
	}

	public void startString() {
		try {
			if (RSInterface.getInterface(Constants.INTERFACE_BANK).isValid()) {
				bankClose();
			}
			if (self.getAnimation() != -1) {
				curTime = System.currentTimeMillis();
			}
			if (System.currentTimeMillis() - curTime > 3000) {
				if (IFACE.isValid()) {
					clickMouse(STRING_BOW_P, false);
					wait(random(wait1, wait2));
					atMenu("Make All");
					curTime = System.currentTimeMillis();
				} else {
					atInventoryItem(stringPick, "Use");
					wait(random(wait1, wait2));
					atInventoryItem(E_n_D_Fletcher.BOW_STRING, "Use");
					wait(random(wait1, wait2));
				}
			}
		} catch (final Exception e) {
			log("startString(int stringType) Error: " + e);
		}
	}

	public int useBank() {
		try {
			if (fails < 5) {
				bank.open();
				waitForIface(getInterface(Constants.INTERFACE_BANK), 1500);
				wait(random(wait1, wait2));
				if (!RSInterface.getInterface(Constants.INTERFACE_BANK)
						.isValid()) {
					fails += 1;
					return random(wait1 * 3, wait2 * 3);
				}
				if (fletchOp) {
					if (RSInterface.getInterface(Constants.INTERFACE_BANK)
							.isValid()) {
						if (getInventoryCount(knifePick) > 1) {
							bank.depositAllExcept(logPick);
						}
						if (getInventoryCount(logPick) > 27) {
							bank.depositAllExcept(knifePick);
						}
						if (getInventoryCount(logPick)
								+ getInventoryCount(knifePick) < getInventoryCount()) {
							bank.depositAllExcept(knifePick);
						}
						if (getInventoryCount(knifePick) <= 0) {
							withdraw("1", knifePick);
							wait(random(wait1, wait2));
						}
						if (getInventoryCount(logPick) < 27) {
							withdraw("27", logPick);
						}
						wait(random(wait1 * 3, wait2 * 3));
						if (getInventoryCount(logPick) <= 0
								|| getInventoryCount(knifePick) <= 0) {
							fails += 1;
							return random(wait1 * 3, wait2 * 3);
						} else {
							fails = 0;
							bankClose();
						}
					} else {
						return random(wait1, wait2);
					}
				}
				if (stringOp) {
					if (RSInterface.getInterface(Constants.INTERFACE_BANK)
							.isValid()) {
						if (getInventoryCount(stringPick) > 14) {
							bank.depositAllExcept(E_n_D_Fletcher.BOW_STRING);
						}
						if (getInventoryCount(E_n_D_Fletcher.BOW_STRING) > 14) {
							bank.depositAllExcept(stringPick);
						}
						if (getInventoryCount() > getInventoryCount(E_n_D_Fletcher.BOW_STRING)
								+ getInventoryCount(stringPick)) {
							final Point t = new Point(398, 310);
							clickMouse(t, 5, 5, true);
						}
						if (getInventoryCount(stringPick) < 14) {
							withdraw("14", stringPick);
							wait(random(wait1, wait2));
						}
						if (getInventoryCount(E_n_D_Fletcher.BOW_STRING) < 14) {
							withdraw("14", E_n_D_Fletcher.BOW_STRING);
						}
						wait(random(wait1 * 3, wait2 * 3));
						if (getInventoryCount(stringPick) <= 0
								|| getInventoryCount(E_n_D_Fletcher.BOW_STRING) <= 0) {
							fails += 1;
							return random(wait1 * 3, wait2 * 3);
						} else {
							fails = 0;
							bankClose();
						}
					} else {
						return random(wait1, wait2);
					}
				}
			} else {
				if (!multiOp) {
					if (!RSInterface.getInterface(Constants.INTERFACE_BANK)
							.isValid()) {
						log("Failed to bank more then 5 times.");
						logout();
						stopScript();
					} else {
						bankClose();
						log("Failed to bank more then 5 times.");
						logout();
						stopScript();
					}
				} else {
					fails = 0;
					fletchOp = false;
					stringOp = true;
					multiOp = false;
					return random(wait1 * 3, wait2 * 3);
				}
			}
			return random(wait1, wait2);
		} catch (final Exception e) {
			log("useBank() Error: " + e);
			return random(wait1, wait2);
		}
	}

	private boolean withdraw(final String amount, final int id) {
		try {
			if (bank.getItemByID(id) == null) {
				log("Null Item: " + id);
				return false;
			}
			try {
				final int num = Integer.parseInt(amount);
				if (num == 1) {
					final RSInterfaceComponent item = bank.getItemByID(id);
					clickMouse(item.getPosition(), true);
					return true;
				}
			} catch (final NumberFormatException ignored) {
			}
			if (!bank.atItem(id, amount)) {
				bank.atItem(id, "X");
				wait(random(1500, 1750));
				sendTextCheck(amount);
				return true;
			}
			return false;
		} catch (final Exception e) {
			log("withdraw(String amount, int id) Error: " + e);
			return false;
		}
	}
}