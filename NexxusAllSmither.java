import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Map;

import org.rsbot.bot.Bot;
import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSCharacter;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSInterfaceComponent;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = { "Nexxus" }, category = "Smithing", name = "Nexxus All Smither", version = 1.10, description = "<html>\n<body bgcolor='#000000'>\n<font size='6' color='red'><center><h2>Nexxus All Smither</h2></center>\n</font><font color='white'><p align='center'>Select the Bar and the Item and Start</p><br /><center><select name='bar'><option>Bronze<option>Iron<option>Steel<option>Mithril<option>Adament<option>Rune</select>   <select name='make'><option>Dagger"
		+ "<option>Sword"
		+ "<option>Longsword"
		+ "<option>2-handed Sword"
		+ "<option>Scimitar"
		+ "<option>Mace"
		+ "<option>Warhammer"
		+ "<option>Battleaxe"
		+ "<option>Hatchet"
		+ "<option>Square Shield"
		+ "<option>Kiteshield"
		+ "<option>Platelegs"
		+ "<option>Plateskirt"
		+ "<option>Platebody"
		+ "<option>Chainbody"
		+ "<option>Medium Helm"
		+ "<option>Full Helm"
		+ "<option>Nails"
		+ "<option>Claws"
		+ "<option>Wire"
		+ "<option>Dart Tips"
		+ "<option>Lantern"
		+ "<option>Grapple Tip"
		+ "<option>Arrow tips"
		+ "<option>Throwing Knife"
		+ "<option>Studs"
		+ "<option>Crossbow Limbs"
		+ "<option>Crossbow Bolts"
		+ "</select></center>"
		+ "<br /><br />"
		+ "<b>Made By:  </b>Nexxus<br/>"
		+ "<b>Version:  </b>1.10<br/><br/>"
		+ "</font>" + "</body>\n" + "</html\n", summary = "")
public class NexxusAllSmither extends Script implements PaintListener,
		ServerMessageListener {

	final ScriptManifest properties = getClass().getAnnotation(
			ScriptManifest.class);

	private final int bronze = 2349;
	private final int iron = 2351;
	private final int steel = 2353;
	private final int mith = 2359;
	private final int addy = 2361;
	private final int rune = 2363;
	public int anvil = 2783;
	public int booth = 11402;
	public int iFace = 0;
	public int needed = 0;
	public int madeCount = 0;
	public int profit = 0;
	public int walkto = 1;
	public int smith = 2;
	public int walkback = 3;
	public int openbank = 4;
	public int bankmade = 5;
	public int TODO = walkto;
	public int energy = random(35, 75);
	public int startlvl = 0;
	public int gainedlvls = 0;
	public int startxp = 0;
	public int gainedxp = 0;
	public int bar;
	public int actual;
	public int[] tokeep = { 2347, bar };

	public boolean setCam = true;
	public boolean isSmithing = false;

	public String action = "Idle";
	public String making;

	private long scriptStartTime = 0;

	public RSTile boothTile = new RSTile(3189, 3435);
	public RSTile smithTile = new RSTile(3187, 3424);

	private int antiBan() {
		final int random = random(1, 24);

		switch (random) {
		case 1:
			final int x = random(0, 750);
			final int y = random(0, 500);
			if (random(1, 6) == 3) {
				moveMouse(0, 0, x, y);
			}
			return random(1000, 1500);

		case 2:
			if (getCurrentTab() != Constants.TAB_INVENTORY) {
				openTab(Constants.TAB_INVENTORY);
				return random(500, 750);
			} else {
				return random(500, 750);
			}

		case 3:
			if (random(1, 40) == 30) {
				if (getMyPlayer().isMoving()) {
					return random(750, 1000);
				}
				if (getCurrentTab() != Constants.TAB_STATS) {
					openTab(Constants.TAB_STATS);
				}
				moveMouse(random(660, 711), random(260, 288), 3, 3);
				wait(random(2000, 3000));
				openTab(Constants.TAB_INVENTORY);
				return random(100, 200);
			}

		case 4:
			if (random(1, 13) == 5) {
				int angle = getCameraAngle() + random(-90, 90);
				if (angle < 0) {
					angle = 0;
				}
				if (angle > 359) {
					angle = 0;
				}

				setCameraRotation(angle);
				return random(500, 750);
			}
		}
		return 500;
	}

	public boolean atBankItem(final int itemID, final String txt) {
		if (!isLoggedIn() || !bank.isOpen()) {
			return false;
		}
		final int[] itemArray = bank.getItemArray();
		for (int off = 0; off < itemArray.length; off++) {
			if (itemArray[off] == itemID) {
				final Point p = bank.getItemPoint(off);
				if (p.y < 87 || p.y > 291) {
					while (bank.isOpen()) {
						bank.close();
					}
					log("Out of Bars. Terminating.");
					return false;
				}
				moveMouse(p, 5, 5);
				final long waitTime = System.currentTimeMillis()
						+ random(50, 250);
				boolean found = false;
				while (!found && System.currentTimeMillis() < waitTime) {
					wait(15);
					if (getMenuItems().get(0).toLowerCase().contains(
							txt.toLowerCase())) {
						found = true;
					}
				}
				if (found) {
					clickMouse(true);
					wait(random(150, 250));
					return true;
				}
				clickMouse(false);
				wait(random(150, 250));
				return atMenu(txt);
			}
		}
		return false;
	}

	public boolean atMenu(final String optionContains) {
		final int idx = getMenuIndex(optionContains);
		if (!isMenuOpen()) {
			if (idx == -1) {
				return false;
			}
			if (idx == 0) {
				clickMouse(true);
			} else {
				clickMouse(false);
				atMenuItem(idx);
			}
			return true;
		} else {
			if (idx == -1) {
				while (isMenuOpen()) {
					moveMouseRandomly(750);
					wait(random(100, 500));
				}
				return false;
			} else {
				atMenuItem(idx);
				return true;
			}
		}
	}

	public boolean clickBar(final int itemID) {
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
		final int idx = possible.get(0);
		final Point t = getInventoryItemPoint(idx);
		clickMouse(t, 5, 5, true);
		return true;
	}

	public boolean deposit(final int itemID) {
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
		final int idx = possible.get(0);
		final Point t = getInventoryItemPoint(idx);
		clickMouse(t, 5, 5, false);
		wait(random(150, 300));
		return atMenu("Deposit-All");
	}

	private boolean energyCheck() {
		try {
			if (gEnergy() >= energy && !isRunning()) {
				energy = random(35, 65);
				return true;
			} else {
				return false;
			}
		} catch (final Exception e) {
			return false;
		}
	}

	private int gEnergy() {
		return Integer
				.parseInt(RSInterface.getChildInterface(750, 5).getText());
	}

	public int loop() {

		if (!isLoggedIn()
				|| RSInterface.getInterface(378).getChild(45).getAbsoluteX() > 20
				&& !RSInterface.getInterface(149).isValid()) {
			wait(200);
			return random(1200, 3100);
		}

		if (canContinue()) {
			clickContinue();
			return random(544, 1200);
		}

		if (setCam) {
			setCameraAltitude(true);
			wait(random(250, 500));
			setCam = false;
		}

		if (energyCheck()) {
			setRun(true);
			wait(random(750, 1000));
		}

		antiBan();

		runMain();

		return 800;
	}

	private boolean needToScroll() {
		if (iFace > 186) {
			return true;
		} else {
			return false;
		}
	}

	public void onFinish() {
		log("Thank you for using Nexxus Scripts!");
		Bot.getEventManager().removeListener(PaintListener.class, this);
		return;
	}

	public boolean onNPC(final RSCharacter npc, final String npcName,
			final String action) {
		if (npc == null) {
			return false;
		}
		final RSTile tile = npc.getLocation();
		if (!tile.isValid()) {
			return false;
		}

		final Point checkScreen = npc.getScreenLocation();
		if (!pointOnScreen(checkScreen)) {
			walkTo(tile);
			wait(random(340, 700));
		}

		try {
			Point screenLoc = null;
			for (int i = 0; i < 30; i++) {
				screenLoc = npc.getScreenLocation();
				if (!npc.isValid() || !pointOnScreen(screenLoc)) {
					return false;
				}
				if (getMenuItems().get(0).toLowerCase().contains(
						npcName.toLowerCase())) {
					break;
				}
				if (getMouseLocation().equals(screenLoc)) {
					break;
				}
				moveMouse(screenLoc);
			}
			screenLoc = npc.getScreenLocation();
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

	public void onRepaint(final Graphics r) {

		if (!isLoggedIn()) {
			return;
		}

		if (startxp == 0) {
			startlvl = skills.getCurrentSkillLevel(Constants.STAT_SMITHING);
			startxp = skills.getCurrentSkillExp(Constants.STAT_SMITHING);
			return;
		}

		long runTime = 0;
		long seconds = 0;
		long minutes = 0;
		long hours = 0;

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

		gainedxp = skills.getCurrentSkillExp(Constants.STAT_SMITHING) - startxp;

		r.setColor(Color.BLACK);
		r.fillRoundRect(21, 191, 204, 147, 10, 10);
		r.setColor(Color.BLUE);
		r.fillRoundRect(23, 193, 200, 143, 10, 10);
		r.setColor(Color.CYAN);
		r.drawString(properties.name(), 70, 210);
		r.drawString("Version: 1.10", 33, 250);
		r.drawString(properties.authors()[0], 98, 225);
		r.drawString("Action: " + action, 33, 315);
		r.drawString("Run Time: " + hours + ":" + minutes + ":" + seconds, 33,
				330);

		r.drawString("Items Made: " + madeCount, 33, 275);
		r.drawString("Exp Gained: " + gainedxp, 33, 290);

	}

	/*
	 * 
	 * //-------------\\ ||-- Methods --|| \\-------------//
	 */

	public boolean onStart(final Map<String, String> args) {
		startlvl = skills.getCurrentSkillLevel(Constants.STAT_SMITHING);
		startxp = skills.getCurrentSkillExp(Constants.STAT_SMITHING);
		scriptStartTime = System.currentTimeMillis();
		if (args.get("bar").equals("Bronze")) {
			bar = bronze;
		}
		if (args.get("bar").equals("Iron")) {
			bar = iron;
		}
		if (args.get("bar").equals("Steel")) {
			bar = steel;
		}
		if (args.get("bar").equals("Mithril")) {
			bar = mith;
		}
		if (args.get("bar").equals("Adament")) {
			bar = addy;
		}
		if (args.get("bar").equals("Rune")) {
			bar = rune;
		}
		if (args.get("make").equals("Dagger")) {
			needed = 1;
			iFace = 18;
		}
		if (args.get("make").equals("Dart Tips")) {
			needed = 1;
			iFace = 68;
		}
		if (args.get("make").equals("Sword")) {
			needed = 1;
			iFace = 58;
		}
		if (args.get("make").equals("Claws")) {
			needed = 2;
			iFace = 211;
			actual = 146;
		}
		if (args.get("make").equals("Wire")) {
			needed = 1;
			iFace = 98;
		}
		if (args.get("make").equals("Lantern")) {
			needed = 1;
			iFace = 164;
		}

		if (args.get("make").equals("Grapple Tip")) {
			needed = 1;
			iFace = 170;
		}
		if (args.get("make").equals("Longsword")) {
			needed = 2;
			iFace = 130;
		}
		if (args.get("make").equals("Scimitar")) {
			needed = 2;
			iFace = 114;
		}
		if (args.get("make").equals("2-handed Sword")) {
			needed = 3;
			iFace = 218;
			actual = 154;
		}
		if (args.get("make").equals("Mace")) {
			needed = 1;
			iFace = 34;
		}
		if (args.get("make").equals("Warhammer")) {
			needed = 3;
			iFace = 178;
		}
		if (args.get("make").equals("Battleaxe")) {
			needed = 3;
			iFace = 186;
		}
		if (args.get("make").equals("Hatchet")) {
			needed = 1;
			iFace = 26;
		}
		if (args.get("make").equals("Square Shield")) {
			needed = 2;
			iFace = 154;
		}
		if (args.get("make").equals("Kiteshield")) {
			needed = 3;
			iFace = 202;
			actual = 138;
		}
		if (args.get("make").equals("Platelegs")) {
			needed = 3;
			iFace = 234;
			actual = 170;
		}
		if (args.get("make").equals("Plateskirt")) {
			needed = 3;
			iFace = 226;
			actual = 162;
		}
		if (args.get("make").equals("Platebody")) {
			needed = 5;
			iFace = 242;
			actual = 178;
		}
		if (args.get("make").equals("Chainbody")) {
			needed = 3;
			iFace = 194;
			actual = 130;
		}
		if (args.get("make").equals("Medium Helm")) {
			needed = 1;
			iFace = 42;
		}
		if (args.get("make").equals("Full Helm")) {
			needed = 2;
			iFace = 146;
		}
		if (args.get("make").equals("Nails")) {
			needed = 1;
			iFace = 74;
		}
		if (args.get("make").equals("Arrow tips")) {
			needed = 1;
			iFace = 106;
		}
		if (args.get("make").equals("Crossbow Limbs")) {
			needed = 1;
			iFace = 122;
		}
		if (args.get("make").equals("Crossbow Bolts")) {
			needed = 1;
			iFace = 50;
		}
		if (args.get("make").equals("Throwing Knife")) {
			needed = 1;
			iFace = 138;
		}
		making = args.get("make").toLowerCase();
		return true;
	}

	private boolean onTile(final RSTile tile, final String search,
			final String action, final double dx, final double dy,
			final int height) {
		if (!tile.isValid()) {
			return false;
		}

		Point checkScreen = null;
		checkScreen = Calculations.tileToScreen(tile, dx, dy, height);
		if (!pointOnScreen(checkScreen)) {
			walkTo(tile.randomizeTile(1, 1));
			wait(random(340, 1310));
		}

		try {
			Point screenLoc = null;
			for (int i = 0; i < 30; i++) {
				screenLoc = Calculations.tileToScreen(tile, dx, dy, height);
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
			screenLoc = Calculations.tileToScreen(tile, dx, dy, height);
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

	private void runMain() {
		if (TODO == walkto) {
			action = "Walking to Anvils";
			try {
				if (getInventoryCount(bar) < needed) {
					TODO = walkback;
					return;
				}
			} catch (final Exception e) {

			}
			try {
				if (distanceTo(smithTile) > 4) {
					if (!getMyPlayer().isMoving()) {
						walkTo(smithTile);
					}
					return;
				}
			} catch (final Exception e) {
			}
			try {
				if (getMyPlayer().isMoving()) {
					return;
				}
				if (distanceTo(smithTile) < 3) {
					TODO = smith;
					return;
				} else {
					if (!getMyPlayer().isMoving()) {
						walkTo(smithTile);
					}
					return;
				}
			} catch (final Exception e) {
			}
		}
		if (TODO == smith) {
			action = "Smithing";
			try {
				if (getNearestObjectByID(anvil) == null) {
					TODO = walkto;
					return;
				}
			} catch (final Exception e) {
			}
			try {
				if (getInventoryCount(bar) < needed) {
					TODO = walkback;
					return;
				}
			} catch (final Exception e) {
			}
			try {
				smithItems();
				return;
			} catch (final Exception e) {
			}
		}
		if (TODO == walkback) {
			action = "Walking to Bank";
			try {
				if (distanceTo(boothTile) > 5) {
					if (!getMyPlayer().isMoving()) {
						walkTo(boothTile);
					}
					return;
				} else {
					TODO = openbank;
					return;
				}
			} catch (final Exception e) {
			}
		}
		if (TODO == openbank) {
			action = "Opening bank";
			try {
				if (bank.isOpen()) {
					TODO = bankmade;
					return;
				}
				if (getMyPlayer().isMoving()) {
					wait(50);
					return;
				}
				while (!bank.isOpen()) {
					wait(50);
					if (getMyPlayer().isMoving()) {
						wait(50);
						return;
					} else {
						if (onTile(getNearestObjectByID(booth).getLocation(),
								"Bank booth", "Use-quickly", 0.5, 0.5, 0)) {
							;
						}
						wait(random(800, 1200));
						return;
					}
				}
				return;
			} catch (final Exception e) {
			}
		}
		if (TODO == bankmade) {
			action = "Banking Items";
			try {
				if (getInventoryCount(bar) >= needed) {
					TODO = walkto;
					return;
				}
				if (bank.isOpen()) {
					if (bank.depositAllExcept(tokeep)) {
						wait(200);
					} else {
						return;
					}
					if (!(bank.getCount(bar) + getInventoryCount(bar) < needed)) {
						atBankItem(bar, "Withdraw-All");
						return;
					} else {
						while (bank.isOpen()) {
							bank.close();
						}
						log("Out of Bars. Terminating.");
						stopScript();
					}
					return;
				} else {
					wait(random(500, 1000));
					if (!bank.isOpen() && !getMyPlayer().isMoving()) {
						if (onTile(getNearestObjectByID(booth).getLocation(),
								"Bank booth", "Use-quickly", 0.5, 0.5, 0)) {
							while (!bank.isOpen()) {
								wait(50);
								if (getMyPlayer().isMoving()) {
									wait(50);
								}
							}
							return;
						}
					}
				}
			} catch (final Exception e) {
			}
		}
	}

	public boolean scrollBTN(final RSInterface intrface,
			final RSInterfaceChild child) {
		final RSInterfaceComponent[] sdown = RSInterface.getInterface(300)
				.getChild(16).getComponents();
		final Point scroll = new Point(sdown[5].getPoint());

		moveMouse(scroll);
		wait(200);
		clickMouse(true);
		clickMouse(true);
		clickMouse(true);
		clickMouse(true);
		clickMouse(true);
		clickMouse(true);
		clickMouse(true);
		clickMouse(true);
		clickMouse(true);
		wait(500);
		return true;
	}

	public void serverMessageRecieved(final ServerMessageEvent e) {
		final String msg = e.getMessage();
		if (msg.contains("You make a")) {
			final String make = msg.replace("You make a ", "");
			final String make2 = make.replace(".", "");
			making = make2;
			madeCount = madeCount + 1;
			wait(500);
			if (getInventoryCount(bar) < needed) {
				wait(500);
				isSmithing = false;
			} else {
				isSmithing = true;
			}
		}
		if (msg.contains("advanced a Smithing")) {
			isSmithing = false;
			gainedlvls = gainedlvls + 1;
		}

	}

	public boolean smithItems() {
		try {
			if (getInventoryCount(bar) < needed) {
				isSmithing = false;
				return false;
			}
			if (getMyPlayer().isMoving()) {
				return false;
			}
			if (getNearestObjectByID(anvil) == null) {
				if (!getMyPlayer().isMoving()) {
					walkTo(smithTile);
					return false;
				}
			}
			if (!isSmithing == true) {
				try {
					if (getInventoryCount(bar) < needed) {
						return false;
					}
					if (!RSInterface.getInterface(300).isValid()) {
						wait(50);
						if (!isSmithing == true) {
							clickBar(bar);
						}
						wait(200);
						if (onTile(getNearestObjectByID(anvil).getLocation(),
								"Anvil", "Anvil", 0.5, 0.5, 0)) {
							;
						}
						wait(1000);
						return true;
					} else {
						if (needToScroll() == false) {
							if (RSInterface.getChildInterface(300, iFace)
									.isValid()) {
								final Point makexy = new Point(RSInterface
										.getChildInterface(300, iFace)
										.getAbsoluteX(), RSInterface
										.getChildInterface(300, iFace)
										.getAbsoluteY());
								moveMouse(makexy);
								wait(200);
								clickMouse(false);
								if (atMenu("Make All")) {
									;
								}
								wait(5000);
								return true;
							} else {
								return false;
							}
						} else {
							final Point makexy = new Point(RSInterface
									.getChildInterface(300, actual)
									.getAbsoluteX(), RSInterface
									.getChildInterface(300, actual)
									.getAbsoluteY());
							moveMouse(makexy);
							if (getMenuItems().get(0).toLowerCase()
									.toLowerCase().contains(
											making.toLowerCase())) {
								wait(200);
								clickMouse(false);
								if (atMenu("Make All")) {
									;
								}
								wait(5000);
								return true;
							}
							if (scrollBTN(RSInterface.getInterface(300),
									RSInterface.getInterface(300).getChild(16))) {
								;
							}
							wait(200);
							clickMouse(false);
							if (atMenu("Make All")) {
								;
							}
							wait(5000);
							return true;
						}
					}
				} catch (final Exception e) {

				}
			} else {
				wait(50);
				return false;
			}
		} catch (final Exception e) {

		}
		return false;
	}

}