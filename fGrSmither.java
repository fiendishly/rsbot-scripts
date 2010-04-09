import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Map;

import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = { "Fagerheim" }, category = "Smithing", name = "fGrSmither", version = 1.15, description = "<html>\n<body>\n<font size='6' color='black'><center><b>fGrSmither v1.15</b></center></font><font color='black'><p align='center'>Select the Bar type, Item and Hammer!</p><br /><center><select name='bar'><option>Bronze<option>Iron<option>Steel<option>Mithril<option>Adament<option>Rune</select>   <select name='make'>"
		+ "<option>Item"
		+ "<option>Dagger"
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
		// + "<option>Claws"
		+ "<option>Wire"
		+ "<option>Iron Spit"
		+ "<option>Dart Tip"
		+ "<option>Lantern"
		+ "<option>Grapple Tip"
		+ "<option>Arrow tips"
		+ "<option>Throwing Knife"
		+ "<option>Studs"
		+ "<option>Crossbow Limb"
		+ "<option>Crossbow Bolt"
		+ "</select>    <select name='hammer'><option>Normal Hammer</select><br>Always smith best item: <input type=\"checkbox\" name=\"awbest\" value=\"true\"></center>"
		+ "<br/>"
		+ "<font color='black'><center><b>Instructions:</b><br/>"
		+ "1. Stand inside Varrock West bank or at anvils.<br/>"
		+ "2. Make sure your hammer and bars are clearly visible at first bank page!<br/>"
		+ "3. Choose bar type, item to smith or 'Alyways smith best item' and hammer type.<br/>"
		+ "4. Start the bot</font>" + "</font>" + "</body>\n" + "</html\n", summary = "")
public class fGrSmither extends Script implements PaintListener {
	private final int bronze = 2349;
	private final int iron = 2351;
	private final int steel = 2353;
	private final int mith = 2359;
	private final int addy = 2361;
	private final int rune = 2363;
	public boolean awbest;
	public int anvil = 2783;
	public int bar;
	public int bars2 = 0;
	public int hammer;
	public int chooseIdle = 0;
	public int needed = 1;
	public int toScroll = 0;
	public int energy = random(11, 90);
	public int smithIdle = 0;
	public int level = 0;
	public int startingExp = 0;
	public float secExp = 0;
	public float minuteExp = 0;
	public float hourExp = 0;
	public int gainedExp = 0;
	public int avgPerHour = 0;
	public int antall;
	public int oldExp = 0;
	public long lastAvgCheck = 0;
	public String make;
	public String bars;
	public int xpbar = 25;
	public int[] tokeep = { 2347, 2349, 2351, 2353, 2359, 2361, 2363 };
	public int startingLevel = skills
			.getCurrentSkillLevel(Constants.STAT_SMITHING);
	int smithx = 0;
	int smithy = 0;
	int smithx2 = 0;
	int smithy2 = 0;
	long startTime = 0;
	long runTime = 0, seconds = 0, minutes = 0, hours = 0;

	public RSTile boothTile = new RSTile(3189, 3435);
	public RSTile smithTile = new RSTile(3187, 3424);

	public void antiBan() {
		if (random(1, 12) == 1) {
			setCameraRotation(random(1, 359));
		}
		if (shouldRun()) {
			setRun(true);
		}
		if (random(1, 11) == 1) {
			final int x = (int) getMouseLocation().getX();
			final int y = (int) getMouseLocation().getY();
			moveMouse(x + random(-100, 100), y + random(-50, 50));
		}
		if (random(1, 60) == 1) {
			openTab(Constants.TAB_STATS);
			moveMouse(random(669, 706), random(264, 290));
			wait(random(731, 2313));
			openTab(Constants.TAB_INVENTORY);
		}
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
					log("No Hammer or out of Bars. Terminating.");
					stopScript();
					return false;
				}
				moveMouse(p, 5, 5);
				final long waitTime = System.currentTimeMillis()
						+ random(50, 250);
				boolean found = false;
				while (!found && System.currentTimeMillis() < waitTime) {
					wait(random(15, 20));
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

	private boolean bank() {
		if (bank.isOpen()) {
			if (bank.depositAllExcept(tokeep)) {
				wait(random(200, 300));
			}
			if (getInventoryCount(hammer) != 1) {
				atBankItem(hammer, "Withdraw-1");
				wait(random(200, 300));
			}
			if (!(bank.getCount(bar) + getInventoryCount(bar) < needed)) {
				atBankItem(bar, "Withdraw-All");
				wait(random(200, 300));
				if (random(1, 6) == 1) {
					bank.close();
				}
			} else {
				while (bank.isOpen()) {
					bank.close();
				}
				log("No Hammer or out of Bars. Terminating.");
				stopScript();
			}
		} else {
			openBank();
		}
		return false;

	}

	public boolean choosesmith() {
		if (make.equals("Dagger") || make.equals("Medium Helm")
				|| make.equals("Studs") || make.equals("Dart Tip")
				|| make.equals("Sword") || make.equals("Wire")
				|| make.equals("Lantern") || make.equals("Grapple Tip")
				|| make.equals("Mace") || make.equals("Hatchet")
				|| make.equals("Arrow tips") || make.equals("Crossbow Limb")
				|| make.equals("Crossbow Bolt") || make.equals("Nails")
				|| make.equals("Throwing Knife")) {
			needed = 1;
		}
		if (make.equals("Claws") || make.equals("Longsword")
				|| make.equals("Scimitar") || make.equals("Square Shield")
				|| make.equals("Full Helm")) {
			needed = 2;
		}
		if (make.equals("Warhammer") || make.equals("Battleaxe")) {
			needed = 3;
		}
		if (make.equals("2-handed Sword") || make.equals("Kiteshield")
				|| make.equals("Platelegs") || make.equals("Plateskirt")
				|| make.equals("Chainbody")) {
			needed = 3;
			toScroll = 1;
		}
		if (make.equals("Platebody")) {
			needed = 5;
			toScroll = 1;
		}
		if (make.equals("Dagger") || make.equals("Iron Spit")
				|| make.equals("Wire") || make.equals("Crossbow Bolt")
				|| make.equals("Studs") || make.equals("Longsword")
				|| make.equals("Lantern") || make.equals("Chainbody")
				|| make.equals("Plateskirt")) {
			smithx = 117;
			smithx2 = 119;
		}
		if (make.equals("Dagger") || make.equals("Hatchet")
				|| make.equals("Mace") || make.equals("Medium Helm")) {
			smithy = 67;
			smithy2 = 92;
		}
		if (make.equals("Hatchet") || make.equals("Grapple Tip")
				|| make.equals("Sword") || make.equals("Arrow tips")
				|| make.equals("Throwing Knife") || make.equals("Kiteshield")
				|| make.equals("Platelegs")) {
			smithx = 133;
			smithx2 = 236;
		}
		if (make.equals("Crossbow Bolt") || make.equals("Sword")
				|| make.equals("Nails") || make.equals("Dart Tip")) {
			smithy = 119;
			smithy2 = 144;
		}
		if (make.equals("Mace") || make.equals("Scimitar")
				|| make.equals("Full Helm") || make.equals("Warhammer")
				|| make.equals("Platebody") || make.equals("Dart Tip")) {
			smithx = 251;
			smithx2 = 353;
		}
		if (make.equals("Studs") || make.equals("Wire")
				|| make.equals("Iron Spit") || make.equals("Arrow tips")
				|| make.equals("Scimitar") || make.equals("Crossbow Limb")) {
			smithy = 171;
			smithy2 = 196;
		}
		if (make.equals("Medium Helm") || make.equals("Nails")
				|| make.equals("Crossbow Limb") || make.equals("Square Shield")
				|| make.equals("2-handed Sword") || make.equals("Battleaxe")) {
			smithx = 381;
			smithx2 = 483;
		}
		if (make.equals("Longsword") || make.equals("Throwing Knife")
				|| make.equals("Full Helm") || make.equals("Square Shield")) {
			smithy = 223;
			smithy2 = 248;
		}
		if (make.equals("Lantern") || make.equals("Grapple Tip")
				|| make.equals("Warhammer") || make.equals("Battleaxe")
				|| make.equals("Crossbow Limb")) {
			smithy = 275;
			smithy2 = 300;
		}
		if (make.equals("Chainbody") || make.equals("Kiteshield")
				|| make.equals("2-handed Sword")) {
			smithy = 216;
			smithy2 = 241;
		}
		if (make.equals("Plateskirt") || make.equals("Platelegs")
				|| make.equals("Platebody")) {
			smithy = 268;
			smithy2 = 293;
		}
		return true;
	}

	public boolean clickInventoryItem(final int itemID) {
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
		moveMouse(t, 5, 5);
		wait(random(50, 100));
		if (topTextContains("->")) {
			return true;
		}
		clickMouse(true);
		wait(random(50, 100));
		return true;
	}

	public int gEnergy() {
		int a;
		try {
			a = Integer.parseInt(RSInterface.getChildInterface(750, 5)
					.getText());
		} catch (final Exception e) {
			a = 0;
		}
		return a;
	}

	protected int getMouseSpeed() {
		return 6;
	}

	public int loop() {
		if (RSInterface.getInterface(740).getChild(0).isValid()) {
			chooseIdle = 0;
			wait(random(300, 600));
			atInterface(RSInterface.getInterface(740).getChild(3), "continue");
			level = skills.getCurrentSkillLevel(Constants.STAT_SMITHING);
		}
		if (chooseIdle != 1) {
			if (awbest) {
				smithbest();
			}
			choosesmith();
			chooseIdle = 1;
		}
		antiBan();
		while (getMyPlayer().isMoving()) {
			return random(500, 1500);
		}
		if (needToWalk()) {
			walkTo(boothTile);
			return random(285, 321);
		}
		if (getInventoryCount(hammer) == 1 && getInventoryCount(bar) >= needed
				&& distanceTo(smithTile) > 5) {
			walkTo(smithTile);
			return random(236, 1026);
		}

		if (getInventoryCount(bar) <= needed && distanceTo(boothTile) < 5
				|| getInventoryCount(hammer) != 1 && distanceTo(boothTile) < 5) {
			bank();
			return random(562, 1074);
		}

		if (getInventoryCount(hammer) == 1 && getInventoryCount(bar) >= needed
				&& distanceTo(smithTile) < 5) {
			smith();
			return random(546, 2023);
		}
		return random(262, 314);
	}

	public boolean needToWalk() {
		if (getInventoryCount(bar) < needed && distanceTo(boothTile) > 5
				|| getInventoryCount(hammer) != 1 && distanceTo(boothTile) > 5) {
			smithIdle = 0;
			return true;
		}
		return false;
	}

	public void onFinish() {
		log("Thanks for useing fGrSmither!");
	}

	public void onRepaint(final Graphics g) {
		final Color BG = new Color(0, 0, 0, 200);
		final Color RED = new Color(255, 0, 0, 255);
		final Color WHITE = new Color(255, 255, 255, 255);

		if ((minutes > 0 || hours > 0 || seconds > 0) && gainedExp > 0) {
			secExp = (float) gainedExp
					/ (float) (seconds + minutes * 60 + hours * 60 * 60);
		}
		minuteExp = secExp * 60;
		hourExp = minuteExp * 60;

		if (System.currentTimeMillis() - lastAvgCheck >= 500) {
			lastAvgCheck = System.currentTimeMillis();
			avgPerHour = gainedExp - oldExp;
			oldExp = gainedExp;
		}
		if (startTime == 0) {
			startTime = System.currentTimeMillis();
		}
		runTime = System.currentTimeMillis() - startTime;
		seconds = runTime / 1000;
		if (seconds >= 60) {
			minutes = seconds / 60;
			seconds -= minutes * 60;
		}
		if (minutes >= 60) {
			hours = minutes / 60;
			minutes -= hours * 60;
		}

		if (bars2 == 1 || bars2 == 3 || bars2 == 5) {
			antall = skills.getXPToNextLevel(Constants.STAT_SMITHING) / xpbar
					/ needed * 2 + 2;
		}
		if (bars2 == 2 || bars2 == 4 || bars2 == 6) {
			antall = skills.getXPToNextLevel(Constants.STAT_SMITHING) / xpbar
					/ needed * +1;
		}

		gainedExp = skills.getCurrentSkillExp(Constants.STAT_SMITHING)
				- startingExp;
		g.setColor(BG);
		g.fill3DRect(305, 180, 205, 150, true);
		g.setColor(RED);
		g.drawString("fGrSmither v 1.15", 360, 195);
		g.setColor(WHITE);
		g.drawString("Running for: " + hours + ":" + minutes + ":" + seconds,
				310, 210);
		g.drawString("Making: " + bars + " " + make + "'s", 310, 225);
		g.drawString("Currently Level: " + level, 310, 240);
		g.drawString(skills.getPercentToNextLevel(Constants.STAT_SMITHING)
				+ "% to level " + (level + 1), 310, 255);
		g
				.drawString(
						"Gained "
								+ (skills
										.getCurrentSkillLevel(Constants.STAT_SMITHING) - startingLevel)
								+ " levels", 310, 270);
		g.drawString("Gained " + gainedExp + " Exp", 310, 285);
		g.drawString("Average Exp per hour: " + (int) hourExp, 310, 300);
		g.drawString(
				(int) antall + " " + make + "s until level " + (level + 1),
				310, 315);
	}

	public boolean onStart(final Map<String, String> args) {
		setCameraAltitude(true);
		level = startingLevel;
		startingExp = skills.getCurrentSkillExp(Constants.STAT_SMITHING);
		awbest = args.get("awbest") != null ? true : false;
		final String hammers = args.get("hammer").trim();
		if (hammers.equals("Normal Hammer")) {
			hammer = 2347;
		}
		if (!awbest) {
			make = args.get("make");
		}
		bars = args.get("bar");
		if (bars.equals("Bronze")) {
			bar = bronze;
			bars2 = 1;
			xpbar = 25;
		}
		if (bars.equals("Iron")) {
			bar = iron;
			bars2 = 2;
			xpbar = 25;
		}
		if (bars.equals("Steel")) {
			bar = steel;
			bars2 = 3;
			xpbar = 75;
		}
		if (bars.equals("Mithril")) {
			bar = mith;
			bars2 = 4;
			xpbar = 50;
		}
		if (bars.equals("Adament")) {
			bar = addy;
			bars2 = 5;
			xpbar = 125;
		}
		if (bars.equals("Rune")) {
			bar = rune;
			bars2 = 6;
			xpbar = 75;
		}
		return true;
	}

	public boolean openBank() {
		final RSObject bank = getNearestObjectByID(11402);
		if (bank == null) {
			return false;
		}
		if (!tileOnScreen(bank.getLocation())) {
			turnToTile(bank.getLocation(), 15);
		}

		return atTile(bank.getLocation(), "Use-quickly");

	}

	public boolean shouldRun() {
		if (gEnergy() > energy + random(-10, 10)) {
			return true;
		}
		return false;
	}

	public int smith() {
		wait(random(200, 350));
		if (smithIdle != 1) {
			clickInventoryItem(bar);
			wait(random(250, 500));
			final RSObject anvill = getNearestObjectByID(anvil);
			atObject(anvill, "Use");
			wait(random(2500, 3000));
			if (RSInterface.getInterface(300).isValid() == false) {
				setCameraRotation(random(1, 359));
			}
			if (RSInterface.getInterface(300).isValid()) {
				if (toScroll == 1) {
					clickMouse(random(492, 504), random(250, 300), true);
					toScroll = 0;
				}
				clickMouse(random(smithx, smithx2), random(smithy, smithy2),
						false);
				atMenu("All");
				smithIdle = 1;
			}
		}
		return random(50, 100);
	}

	public boolean smithbest() {
		if (bars2 == 1) {
			if (level >= 18) {
				make = "Platebody";
			}
			if (level == 17) {
				make = "Platelegs";
			}
			if (level == 16) {
				make = "Platelegs";
			}
			if (level == 15) {
				make = "2-handed Sword";
			}
			if (level == 14) {
				make = "2-handed Sword";
			}
			if (level == 13) {
				make = "Kiteshield";
			}
			if (level == 12) {
				make = "Kiteshield";
			}
			if (level == 11) {
				make = "Chainbody";
			}
			if (level == 10) {
				make = "Battleaxe";
			}
			if (level == 9) {
				make = "Warhammer";
			}
			if (level == 8) {
				make = "Square Shield";
			}
			if (level == 7) {
				make = "Full Helm";
			}
			if (level == 6) {
				make = "Long Sword";
			}
			if (level == 5) {
				make = "Scimitar";
			}
			if (level == 4) {
				make = "Sword";
			}
			if (level == 3) {
				make = "Medium Helm";
			}
			if (level == 2) {
				make = "Mace";
			}
			if (level == 1) {
				make = "Hatchet";
			}
		}
		if (bars2 == 2) {
			if (level >= 33) {
				make = "Platebody";
			}
			if (level == 32) {
				make = "Platelegs";
			}
			if (level == 31) {
				make = "Platelegs";
			}
			if (level == 30) {
				make = "2-handed Sword";
			}
			if (level == 29) {
				make = "2-handed Sword";
			}
			if (level == 28) {
				make = "Kiteshield";
			}
			if (level == 27) {
				make = "Kiteshield";
			}
			if (level == 26) {
				make = "Chainbody";
			}
			if (level == 25) {
				make = "Battleaxe";
			}
			if (level == 24) {
				make = "Warhammer";
			}
			if (level == 23) {
				make = "Square Shield";
			}
			if (level == 22) {
				make = "Full Helm";
			}
			if (level == 21) {
				make = "Long Sword";
			}
			if (level == 20) {
				make = "Scimitar";
			}
			if (level == 19) {
				make = "Sword";
			}
			if (level == 18) {
				make = "Medium Helm";
			}
			if (level == 17) {
				make = "Mace";
			}
			if (level == 16) {
				make = "Hatchet";
			}
			if (level == 15) {
				make = "dagger";
			}
		}
		if (bars2 == 3) {
			if (level >= 48) {
				make = "Platebody";
			}
			if (level == 47) {
				make = "Platelegs";
			}
			if (level == 46) {
				make = "Platelegs";
			}
			if (level == 45) {
				make = "2-handed Sword";
			}
			if (level == 44) {
				make = "2-handed Sword";
			}
			if (level == 43) {
				make = "Kiteshield";
			}
			if (level == 42) {
				make = "Kiteshield";
			}
			if (level == 41) {
				make = "Chainbody";
			}
			if (level == 40) {
				make = "Battleaxe";
			}
			if (level == 39) {
				make = "Warhammer";
			}
			if (level == 38) {
				make = "Square Shield";
			}
			if (level == 37) {
				make = "Full Helm";
			}
			if (level == 36) {
				make = "Long Sword";
			}
			if (level == 35) {
				make = "Scimitar";
			}
			if (level == 34) {
				make = "Sword";
			}
			if (level == 33) {
				make = "Medium Helm";
			}
			if (level == 32) {
				make = "Mace";
			}
			if (level == 31) {
				make = "Hatchet";
			}
			if (level == 30) {
				make = "dagger";
			}
		}
		if (bars2 == 4) {
			if (level >= 68) {
				make = "Platebody";
			}
			if (level == 67) {
				make = "Platelegs";
			}
			if (level == 66) {
				make = "Platelegs";
			}
			if (level == 65) {
				make = "2-handed Sword";
			}
			if (level == 64) {
				make = "2-handed Sword";
			}
			if (level == 63) {
				make = "Kiteshield";
			}
			if (level == 62) {
				make = "Kiteshield";
			}
			if (level == 61) {
				make = "Chainbody";
			}
			if (level == 60) {
				make = "Battleaxe";
			}
			if (level == 59) {
				make = "Warhammer";
			}
			if (level == 58) {
				make = "Square Shield";
			}
			if (level == 57) {
				make = "Full Helm";
			}
			if (level == 56) {
				make = "Long Sword";
			}
			if (level == 55) {
				make = "Scimitar";
			}
			if (level == 54) {
				make = "Sword";
			}
			if (level == 53) {
				make = "Medium Helm";
			}
			if (level == 52) {
				make = "Mace";
			}
			if (level == 51) {
				make = "Hatchet";
			}
			if (level == 50) {
				make = "dagger";
			}
		}
		if (bars2 == 5) {
			if (level >= 88) {
				make = "Platebody";
			}
			if (level == 87) {
				make = "Platelegs";
			}
			if (level == 86) {
				make = "Platelegs";
			}
			if (level == 85) {
				make = "2-handed Sword";
			}
			if (level == 84) {
				make = "2-handed Sword";
			}
			if (level == 83) {
				make = "Kiteshield";
			}
			if (level == 82) {
				make = "Kiteshield";
			}
			if (level == 81) {
				make = "Chainbody";
			}
			if (level == 80) {
				make = "Battleaxe";
			}
			if (level == 79) {
				make = "Warhammer";
			}
			if (level == 78) {
				make = "Square Shield";
			}
			if (level == 77) {
				make = "Full Helm";
			}
			if (level == 76) {
				make = "Long Sword";
			}
			if (level == 75) {
				make = "Scimitar";
			}
			if (level == 74) {
				make = "Sword";
			}
			if (level == 73) {
				make = "Medium Helm";
			}
			if (level == 72) {
				make = "Mace";
			}
			if (level == 71) {
				make = "Hatchet";
			}
			if (level == 70) {
				make = "dagger";
			}
		}
		if (bars2 == 6) {
			if (level == 99) {
				make = "Platebody";
			}
			if (level == 98) {
				make = "Kiteshield";
			}
			if (level == 97) {
				make = "Kiteshield";
			}
			if (level == 96) {
				make = "Chainbody";
			}
			if (level == 95) {
				make = "Battleaxe";
			}
			if (level == 94) {
				make = "Warhammer";
			}
			if (level == 93) {
				make = "Square Shield";
			}
			if (level == 92) {
				make = "Full Helm";
			}
			if (level == 91) {
				make = "Long Sword";
			}
			if (level == 90) {
				make = "Scimitar";
			}
			if (level == 89) {
				make = "Sword";
			}
			if (level == 88) {
				make = "Medium Helm";
			}
			if (level == 87) {
				make = "Mace";
			}
			if (level == 86) {
				make = "Hatchet";
			}
			if (level == 85) {
				make = "dagger";
			}
		}
		return true;
	}

	public boolean topTextContains(final String s) {
		for (int a = 0; a < 10; a++) {
			if (getMenuItems().get(0).toLowerCase().toLowerCase().contains(
					s.toLowerCase())) {
				return true;
			}
		}
		return false;
	}
}
