import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.List;
import java.util.Map;
import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSItemTile;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = { "Creative" }, category = "Combat", name = "WarriorGuild Pro", version = 1.5, description = "<html><body><fontcolor=black><center>"
		+ "<h2>"
		+ "WarriorGuild Pro"
		+ " V 1.5</h2>"
		+ "Author: "
		+ "Creative"
		+ "<br><br>Start near kamfreena."
		+ "<br>Have your food, and defender that you are using to gain the higher version of in your inventory!"
		+ "<br>Informative Paint."
		+ "<br>"
		+ "<br>Select the Food you would like to use: "
		+ "<select name=\"food\"><option>Lobster<option>Salmon<option>Tuna<option>Trout<option>Sharks<option>Monkfish<option>Manta ray<option>Sea turtle<option>Cake<option>Chocolate cake<option>Plain pizza<option>Pineapple pizza<option>Meat pizza<option>Rocktail<option>Swordfish</select>"
		+ "<br>"
		+ "<br>Select Animated Armour we be using: "
		+ "<select name=\"Enemy\"><option>Animated Bronze Armour</option><option>Animated Iron Armour</option><option>Animated Steel Armour</option><option>Animated Black Armour</option><option>Animated Mithril Armour</option><option>Animated Adamant Armour</option><option>Animated Rune Armour</option></select>"
		+ "<br>"
		+ "<br>Select the defender you want to collect: "
		+ "<select name=\"defender\"><option>Bronze</option><option>Iron</option><option>Steel</option><option>Black</option><option>Mithril</option><option>Adamant</option><option>Rune</option></select>"
		+ "<br>"
		+ "<br>Pick/Bury Big Bones?<input name='PrayMode' type='checkbox' value='1'>")
public class WarriorGuildPro extends Script implements PaintListener,
		ServerMessageListener {

	// Npc
	public int cyclops[] = { 6079, 6078, 6080, 4292, 4291, 6081 };
	public int kam = 4289;
	private static final int[] animatedBronze = { 4278 };
	private static final int[] animatedIron = { 4279 };
	private static final int[] animatedSteel = { 4280 };
	private static final int[] animatedBlack = { 4281 };
	private static final int[] animatedMithril = { 4282 };
	private static final int[] animatedAdamant = { 4283 };
	private static final int[] animatedRune = { 4284 };

	// Paint
	public int strengthXP = skills.getCurrentSkillExp(STAT_STRENGTH);
	public int attackXP = skills.getCurrentSkillExp(STAT_ATTACK);
	public int defenseXP = skills.getCurrentSkillExp(STAT_DEFENSE);
	public int hpXP = skills.getCurrentSkillExp(STAT_HITPOINTS);
	public int prayXp = skills.getCurrentSkillExp(STAT_PRAYER);
	public int gainedStr = skills.getCurrentSkillExp(STAT_STRENGTH)
			- strengthXP;
	public int gainedAtt = skills.getCurrentSkillExp(STAT_ATTACK) - attackXP;
	public int gainedDef = skills.getCurrentSkillExp(STAT_DEFENSE) - defenseXP;
	public int gainedHP = skills.getCurrentSkillExp(STAT_HITPOINTS) - hpXP;
	public int gainedPray = skills.getCurrentSkillExp(STAT_PRAYER) - prayXp;

	// Ids
	public int foodID;
	public int pickD;
	public int useD;
	public int[] Defenders = { 8844, 8845, 8846, 8847, 8848, 8849, 8850 };
	public int Tokens = 8851;
	public int bone = 532;
	public int tokenCount = 0;
	public int tokens = 0;
	public int defendGained = 0;
	public int bankBooth = (2213);
	public int[] animatorID;
	public int[] selectionID;
	public int[] bronzeArmour = { 1117, 1075, 1155, 8851 };
	public int[] ironArmour = { 1115, 1067, 1153, 8851 };
	public int[] steelArmour = { 1119, 1069, 1157, 8851 };
	public int[] blackArmour = { 1125, 1077, 1165, 8851 };
	public int[] mithrilArmour = { 1121, 1071, 1159, 8851 };
	public int[] adamantArmour = { 1123, 1073, 1161, 8851 };
	public int[] runeArmour = { 1127, 1079, 1163, 8851 };
	public int[] gear = { 1117, 1075, 1155, 1115, 1067, 1153, 1119, 1069, 1157,
			1121, 1071, 1159, 1123, 1073, 1161, 1127, 1079, 1163, 1125, 1077,
			1165, 8851 };
	public int bronze = 1117;
	public int iron = 1115;
	public int steel = 1119;
	public int black = 1125;
	public int mithril = 1121;
	public int adamant = 1123;
	public int rune = 1127;
	RSItemTile tile;
	RSItemTile tile1;
	RSItemTile tile2;
	final RSObject Animator = getNearestObjectByID(15621);
	final RSObject animatorOutsideDoors = getNearestObjectByID(33438);
	final RSObject animatorInsideDoors = getNearestObjectByID(33439);
	final RSObject topFloorInDoors = getNearestObjectByID(33438);
	final RSObject topFloorOutsideDoors = getNearestObjectByID(33439);
	final RSObject groundFloorStairs = getNearestObjectByID(1738);
	final RSObject middleFloorStairs = getNearestObjectByID(38012);
	final RSObject topFloorStairs = getNearestObjectByID(15638);

	RSTile doorspot = new RSTile(2856, 3545);
	RSTile animatorspot = new RSTile(2854, 3536);
	public RSTile PathToBank[] = { new RSTile(2854, 3546),
			new RSTile(2847, 3546), new RSTile(2843, 3542) };
	public RSTile PathToDoor[] = { new RSTile(2843, 3543),
			new RSTile(2847, 3545), new RSTile(2850, 3546),
			new RSTile(2854, 3546) };

	// Args
	long startTime = System.currentTimeMillis();
	public long runTime = System.currentTimeMillis() - startTime;
	private String status = "";
	public int healthPercent = 0;
	public int randomHealth[] = new int[2];
	public RSTile doorTile1 = new RSTile(2847, 3541);
	public RSTile doorTile2 = new RSTile(2846, 3541);
	public RSTile doorTile3 = new RSTile(2847, 3541);
	private int Lifepoints;
	final RSNPC thing = getNearestFreeNPCByID(cyclops);
	private boolean bury;
	public int TokenAmount;

	// OnStart
	@Override
	public boolean onStart(Map<String, String> args) {
		setCompass('e');
		startTime = System.currentTimeMillis();
		strengthXP = skills.getCurrentSkillExp(STAT_STRENGTH);
		attackXP = skills.getCurrentSkillExp(STAT_ATTACK);
		defenseXP = skills.getCurrentSkillExp(STAT_DEFENSE);
		hpXP = skills.getCurrentSkillExp(STAT_HITPOINTS);

		if (args.get("PrayMode") != null) {
			bury = true;
		}

		if (args.get("food").equals("Cake")) {
			foodID = 1891;
		} else if (args.get("food").equals("Chocolate cake")) {
			foodID = 1897;
		} else if (args.get("food").equals("Plain pizza")) {
			foodID = 2289;
		} else if (args.get("food").equals("Pineapple pizza")) {
			foodID = 2301;
		} else if (args.get("food").equals("Meat pizza")) {
			foodID = 2293;
		} else if (args.get("food").equals("Lobster")) {
			foodID = 379;
		} else if (args.get("food").equals("Salmon")) {
			foodID = 329;
		} else if (args.get("food").equals("Tuna")) {
			foodID = 361;
		} else if (args.get("food").equals("Trout")) {
			foodID = 333;
		} else if (args.get("food").equals("Sharks")) {
			foodID = 385;
		} else if (args.get("food").equals("Monkfish")) {
			foodID = 7946;
		} else if (args.get("food").equals("Manta ray")) {
			foodID = 391;
		} else if (args.get("food").equals("Sea turtle")) {
			foodID = 397;
		} else if (args.get("food").equals("Swordfish")) {
			foodID = 373;
		} else if (args.get("food").equals("Rocktail")) {
			foodID = 15272;
		}

		if (args.get("Enemy").equals("Animated Bronze Armour")) {
			animatorID = animatedBronze;
			selectionID = bronzeArmour;
		} else if (args.get("Enemy").equals("Animated Iron Armour")) {
			animatorID = animatedIron;
			selectionID = ironArmour;
		} else if (args.get("Enemy").equals("Animated Steel Armour")) {
			animatorID = animatedSteel;
			selectionID = steelArmour;
		} else if (args.get("Enemy").equals("Animated Black Armour")) {
			animatorID = animatedBlack;
			selectionID = blackArmour;
		} else if (args.get("Enemy").equals("Animated Mithril Armour")) {
			animatorID = animatedMithril;
			selectionID = mithrilArmour;
		} else if (args.get("Enemy").equals("Animated Adamant Armour")) {
			animatorID = animatedAdamant;
			selectionID = adamantArmour;
		} else if (args.get("Enemy").equals("Animated Rune Armour")) {
			animatorID = animatedRune;
			selectionID = runeArmour;
		}

		if (args.get("defender").equals("Bronze")) {
			pickD = 8844;
		} else if (args.get("defender").equals("Iron")) {
			useD = 8844;
			pickD = 8845;
		} else if (args.get("defender").equals("Steel")) {
			useD = 8845;
			pickD = 8846;
		} else if (args.get("defender").equals("Black")) {
			useD = 8846;
			pickD = 8847;
		} else if (args.get("defender").equals("Mithril")) {
			useD = 8847;
			pickD = 8848;
		} else if (args.get("defender").equals("Adamant")) {
			useD = 8848;
			pickD = 8849;
		} else if (args.get("defender").equals("Rune")) {
			useD = 8849;
			pickD = 8850;
		}
		return true;
	}

	// Methods

	public boolean atDoor() {
		RSObject door = getNearestObjectByID(819);
		if (door == null)
			return false;
		return tileOnScreen(door.getLocation());
	}

	public boolean atBank() {
		RSObject banker = getNearestObjectByID(2213);
		if (banker == null)
			return false;
		return tileOnScreen(banker.getLocation());
	}

	public boolean atAnimator() {
		RSObject anim = getNearestObjectByID(15621);
		if (anim == null)
			return false;
		return tileOnScreen(anim.getLocation());
	}

	public boolean atCyclops() {
		RSNPC beast = getNearestNPCByID(cyclops);
		if (beast == null)
			return false;
		return tileOnScreen(beast.getLocation());
	}

	public int WithBank() {
		final RSObject Banker = getNearestObjectByID(2213);
		if (bank.isOpen()) {
			status = "Depositing...";
			wait(random(200, 300));
			status = "Withdrawing...";
			bank.atItem(foodID, "Withdraw-All");
			wait(random(600, 800));

		}
		if (!(bank.isOpen())) {
			if (Banker != null) {
				atObject(Banker, "Use-Quickly");
				wait(random(500, 700));
			}
			if (Banker == null) {
				return random(100, 200);
			}
		}
		return random(150, 350);
	}

	private boolean inSquare(int maxX, int maxY, int minX, int minY) {
		int x = getMyPlayer().getLocation().getX();
		int y = getMyPlayer().getLocation().getY();
		if (x >= minX && x <= maxX && y >= minY && y <= maxY)
			return true;
		return false;
	}

	private int getCurrentLifepoint() {
		if (RSInterface.getInterface(748).getChild(8).isValid()) {
			if (RSInterface.getInterface(748).getChild(8).getText() != null) {
				Lifepoints = Integer.parseInt(RSInterface.getInterface(748)
						.getChild(8).getText());
			} else {
				log.severe("Getting lifepoints Error");
			}
		} else {
			log.warning("HP Interface is not valid");
		}

		return Lifepoints;
	}

	public void pickUp() {
		tile = getNearestGroundItemByID(Defenders);
		if (tile != null) {
			atTile(tile, "Defender");
			wait(2000);
		}
	}

	public void pickgear() {
		tile2 = getNearestGroundItemByID(gear);

		if (tile2 != null) {
			atTile(tile2, "Take Warrior");
			wait(1500);
			atTile(tile2, "Take");
			wait(1000);
			atTile(tile2, "Take");
			wait(1000);
			atTile(tile2, "Take");
			wait(2000);
		}
	}

	public void pickBury() {
		tile1 = getNearestGroundItemByID(532);
		if (tile1 != null) {
			atTile(tile1, "Take Big");
			wait(2000);
			atInventoryItem(bone, "Bury");
			wait(2500);
		}
	}

	// Credit to whom ever made this. Most effective method ever used =D
	private boolean ATTACKNPC(final RSNPC npc, final String action) {
		final RSTile tile = npc.getLocation();
		tile.randomizeTile(1, 1);
		try {
			final int hoverRand = random(8, 13);
			for (int i = 0; i < hoverRand; i++) {
				final Point screenLoc = npc.getScreenLocation();
				if (!pointOnScreen(screenLoc)) {
					status = "Searching...";
					setCameraRotation(getCameraAngle() + random(-35, 150));
					return true;
				}

				moveMouse(screenLoc, 15, 15);

				final List<String> menuItems = getMenuItems();
				if (menuItems.isEmpty() || menuItems.size() <= 1) {
					continue;
				}
				if (menuItems.get(0).toLowerCase().contains(
						npc.getName().toLowerCase())
						&& getMyPlayer().getInteracting() == null) {
					clickMouse(true);
					return true;
				} else {
					for (int a = 1; a < menuItems.size(); a++) {
						if (menuItems.get(a).toLowerCase().contains(
								npc.getName().toLowerCase())
								&& getMyPlayer().getInteracting() == null) {
							clickMouse(false);
							return atMenu(action);
						}
					}
				}
			}

		} catch (final Exception e) {
			log.warning("ATTACKNPC(RSNPC, String) error: " + e);
			return false;
		}
		return false;
	}

	private boolean isinlocation(int smallestx, int smallesty, int biggestx,
			int biggesty) {
		int x = getMyPlayer().getLocation().getX();
		int y = getMyPlayer().getLocation().getY();
		if (smallestx <= x && x <= biggestx && smallesty <= y && y <= biggesty)
			return true;
		else
			return false;
	}

	private int walkPath(final RSTile[] path) {

		if (distanceTo(getDestination()) <= random(5, 12)
				|| !getMyPlayer().isMoving()) {
			walkPathMM(randomizePath(path, 2, 2), 15);
		}
		return random(400, 600);
	}

	// Paint
	public void onRepaint(final Graphics g) {
		long millis = System.currentTimeMillis() - startTime;
		if (isLoggedIn()) {

			long hours = millis / (1000 * 60 * 60);
			millis -= hours * (1000 * 60 * 60);
			long minutes = millis / (1000 * 60);
			millis -= minutes * (1000 * 60);
			long seconds = millis / 1000;

			int gainedStr = skills.getCurrentSkillExp(STAT_STRENGTH)
					- strengthXP;
			int gainedAtt = skills.getCurrentSkillExp(STAT_ATTACK) - attackXP;
			int gainedDef = skills.getCurrentSkillExp(STAT_DEFENSE) - defenseXP;
			int gainedHP = skills.getCurrentSkillExp(STAT_HITPOINTS) - hpXP;
			int gainedPray = skills.getCurrentSkillExp(STAT_PRAYER) - prayXp;

			if (isLoggedIn()) {
				g.setColor(new Color(0, 0, 0, 175));
				g.fillRoundRect(555, 210, 175, 250, 0, 0);
				g.setColor(Color.cyan);
				g.draw3DRect(555, 210, 175, 250, true);
				g.setFont(new Font("Trebuchet MS", Font.PLAIN, 14));
				g.drawString("WarriorGuild Pro ", 560, 230);
				g.setColor(Color.white);
				g.setFont(new Font("Trebuchet MS", Font.PLAIN, 12));
				g.drawString("Time running: " + hours + ":" + minutes + ":"
						+ seconds, 560, 245);
				g.setColor(Color.red);
				g.drawString("Attack Xp: " + gainedAtt, 560, 270);
				g.setColor(Color.green);
				g.drawString("Strength Xp: " + gainedStr, 560, 295);
				g.setColor(Color.white);
				g.drawString("Defense Xp: " + gainedDef, 560, 320);
				g.setColor(Color.red);
				g.drawString("Hitpoint Xp: " + gainedHP, 560, 345);
				g.setColor(Color.yellow);
				g.drawString("Prayer Xp: " + gainedPray, 560, 370);
				g.setColor(Color.cyan);
				g.drawString("Defenders Gained: " + defendGained, 560, 395);
				g.setColor(Color.yellow);
				g.drawString("Tokens: " + tokens, 560, 420);
				g.setColor(Color.white);
				g.drawString("Status: " + status, 560, 445);
				g.setFont(new Font("Palatino Linotype", Font.ITALIC, 10));
				g.drawString("- Creative", 687, 220);
				g.drawString("V 1.5", 704, 456);
			}
		}
	}

	@Override
	public int loop() {
		setCameraAltitude(true);

		tokens = getInventoryCount(Tokens);

		if (bury
				&& (!isInventoryFull() && getMyPlayer().getInteracting() == null)) {
			pickBury();
		}

		if (inventoryContains(bone)) {
			status = "Burying...";
			atInventoryItem(bone, "Bury");
			wait(2500);
		}

		if (!isInventoryFull()) {
			pickUp();
		}

		if (atCyclops() && (!inventoryContains(foodID))) {
			RSObject escape = getNearestObjectByID(33438);
			walkTileMM(doorTile3);
			wait(1000);
			setCompass('w');
			atObject(escape, "Open");
			wait(1000);

		}

		if (getNearestGroundItemByID(gear) != null) {
			status = "Looting...";
			pickgear();
		}

		if (inventoryContainsOneOf(Defenders)) {
			defendGained = getInventoryCount(pickD);
		}

		if (getGroundItemByID(pickD) != null && (isInventoryFull())) {
			atInventoryItem(foodID, "Eat");
			wait(random(600, 800));
			pickUp();
		}

		if (getMyPlayer().getInteracting() == null
				&& getNearestFreeNPCByID(cyclops) != null) {
			status = "Attacking...";
			ATTACKNPC(getNearestFreeNPCByID(cyclops), "Attack");
		}

		if (getInventoryCount(foodID) >= 1) {
			int RealHP = skills.getRealSkillLevel(STAT_HITPOINTS) * 10;
			if (getCurrentLifepoint() <= random(RealHP / 2, RealHP / 1.5)) {
				status = "Eating Food...";
				atInventoryItem(foodID, "Eat");
				wait(random(1500, 2000));
			}
		}

		if (getMyPlayer().getInteracting() == null
				&& getNearestFreeNPCByID(animatorID) != null) {
			status = "Attacking...";
			atNPC(getNearestFreeNPCByID(animatorID), "Attack");
		}

		if (inSquare(2861, 3545, 2848, 3533)
				&& (inventoryContains(foodID) && (inventoryContains(bronzeArmour)))) {
			setCompass('e');
			status = "On Animator...";
			atInventoryItem(bronze, "Use");
			wait(random(600, 800));
			if (isItemSelected()) {
				atObject(Animator, "Use");
				wait(random(5000, 6000));
			} else {
				moveMouse(random(650, 660), random(180, 190));
				clickMouse(true);
			}
		}
		if (inSquare(2861, 3545, 2848, 3533)
				&& (inventoryContains(foodID) && (inventoryContains(ironArmour)))) {
			setCompass('e');
			status = "On Animator...";
			atInventoryItem(iron, "Use");
			wait(random(600, 800));
			if (isItemSelected()) {
				atObject(Animator, "Use");
				wait(random(5000, 6000));
			} else {
				moveMouse(random(650, 660), random(180, 190));
				clickMouse(true);
			}
		}
		if (inSquare(2861, 3545, 2848, 3533)
				&& (inventoryContains(foodID) && (inventoryContains(steelArmour)))) {
			setCompass('e');
			status = "On Animator...";
			atInventoryItem(steel, "Use");
			wait(random(600, 800));
			if (isItemSelected()) {
				atObject(Animator, "Use");
				wait(random(5000, 6000));
			} else {
				moveMouse(random(650, 660), random(180, 190));
				clickMouse(true);
			}
		}
		if (inSquare(2861, 3545, 2848, 3533)
				&& (inventoryContains(foodID) && (inventoryContains(blackArmour)))) {
			setCompass('e');
			status = "On Animator...";
			atInventoryItem(black, "Use");
			wait(random(600, 800));
			if (isItemSelected()) {
				atObject(Animator, "Use");
				wait(random(5000, 6000));
			} else {
				moveMouse(random(650, 660), random(180, 190));
				clickMouse(true);
			}
		}
		if (inSquare(2861, 3545, 2848, 3533)
				&& (inventoryContains(foodID) && (inventoryContains(mithrilArmour)))) {
			setCompass('e');
			status = "On Animator...";
			atInventoryItem(mithril, "Use");
			wait(random(600, 800));
			if (isItemSelected()) {
				atObject(Animator, "Use");
				wait(random(5000, 6000));
			} else {
				moveMouse(random(650, 660), random(180, 190));
				clickMouse(true);
			}
		}
		if (inSquare(2861, 3545, 2848, 3533)
				&& (inventoryContains(foodID) && (inventoryContains(adamantArmour)))) {
			setCompass('e');
			status = "On Animator...";
			atInventoryItem(adamant, "Use");
			wait(random(600, 800));
			if (isItemSelected()) {
				atObject(Animator, "Use");
				wait(random(5000, 6000));
			} else {
				moveMouse(random(650, 660), random(180, 190));
				clickMouse(true);
			}
		}
		if (inSquare(2861, 3545, 2848, 3533)
				&& (inventoryContains(foodID) && (inventoryContains(runeArmour)))) {
			setCompass('e');
			status = "On Animator...";
			atInventoryItem(rune, "Use");
			wait(random(600, 800));
			if (isItemSelected()) {
				atObject(Animator, "Use");
				wait(random(5000, 6000));
			} else {
				moveMouse(random(650, 660), random(180, 190));
				clickMouse(true);
			}
		}

		if (inSquare(2861, 3545, 2848, 3533)
				&& (!inventoryContains(foodID) && (inventoryContains(selectionID)))) {
			setCompass('n');
			status = "To Door...";
			walkTileMM(doorspot);
			wait(3000);
		}

		if (atDoor() && (!inventoryContains(foodID))) {
			RSObject door = getNearestObjectByID(33438);
			status = "Opening Door...";
			atObject(door, "Open");
			wait(2500);
			status = "To Bank...";
			walkPath(PathToBank);
			wait(4000);
		}

		if (atBank() && (!inventoryContains(foodID))) {
			status = "Banking...";
			WithBank();
		}

		if (isInventoryFull() && (!atAnimator())) {
			status = "To Door...";
			walkPath(PathToDoor);
			wait(6000);
		}

		if (atDoor() && (isInventoryFull())) {
			setCompass('n');
			RSObject door = getNearestObjectByID(33438);
			status = "Opening Door...";
			atObject(door, "Open");
			wait(3000);
			status = "To Animator...";
			walkTileMM(animatorspot);
			wait(3000);
		}

		if (isinlocation(2841, 3537, 2846, 3542)) {
			logout();
			stopScript();
			log("Either no more tokens, or need to start in the area with the Cyclops.");
		}
		return random(300, 400);
	}

	public void serverMessageRecieved(ServerMessageEvent e) {
		String message = e.getMessage();

		if (message
				.contains("Your Time is up, please make your way to the exit")) {
			if (tileOnScreen(topFloorInDoors.getLocation())) {
				atObject(topFloorInDoors, "Open");
			} else {
				walkTo(topFloorInDoors.getLocation());
				if (waitToMove(250)) {
					while (getMyPlayer().isMoving()) {
						wait(random(20, 30));
					}
				}
				atObject(topFloorInDoors, "Open");
			}
		}
	}

}