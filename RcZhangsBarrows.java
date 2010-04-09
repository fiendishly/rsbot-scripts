import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.rsbot.accessors.Node;
import org.rsbot.accessors.RSNPCNode;
import org.rsbot.bot.Bot;
import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSItem;
import org.rsbot.script.wrappers.RSItemTile;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSPlayer;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = { "RcZhang" }, category = "Combat", name = "RcZhang's Barrows", version = 1.2, description = "<hmtl><center><br><b>RcZhang's Barrows</b></br><br> Version: 1.2</br><br><i>~An RcZhang Production~</i></br></center></html>")
public class RcZhangsBarrows extends Script implements PaintListener,
		ServerMessageListener {
	// Foods: Shark, Monkfish, Lobster, Swordfish, Tuna, Tuna Potato, Seaturtle,
	// Karambwan
	public static final int[] food = { 385, 7946, 379, 373, 361, 7060, 397,
			3144 };
	public static final int[] bones = new int[] { 526 };
	public static final int[] Charms = new int[] { 12158, 12159, 12160, 12163 };
	public static final int[] monsters = new int[] { 4920, 4921, 2031, 2032,
			2033, 2034, 2035, 2036, 2037 };
	public static final int peach = 6883;
	public int strexp;
	public int strExp;
	public int atkexp;
	public int atkExp;
	public int defexp;
	public int defExp;
	public int hpexp;
	public int hpExp;
	public int oldatkExp;
	public int olddefExp;
	public int oldhpExp;
	public int oldrangedExp;
	public int oldstrExp;
	public int startatkExp;
	public int startdefExp;
	public int starthpExp;
	public int startpeaches;
	public int startrangedExp;
	public int startstrExp;
	public int rangedexp;
	public int rangedExp;
	public boolean reach = false;
	private int runEnergy;
	public boolean wait = false;
	public boolean npcCheck;
	RSNPC currentNPC;

	private long scriptStartTime = 0;

	// ENUM
	private final int FIGHT = 1;
	private final int PICKUP = 2;
	private final int BTP = 3;
	private final int EAT = 4;
	private final int PICKUPBONE = 5;
	private int ACTION = FIGHT;

	private int antiBan() {
		final int random = random(1, 7);
		switch (random) {
		case 1:
			if (random(1, 5) == 2) {
				final int x = random(0, 750);
				final int y = random(0, 500);
				moveMouse(0, 0, x, y);
				return random(1000, 1500);
			}

		case 2:
			if (getCurrentTab() != Constants.TAB_INVENTORY) {
				openTab(Constants.TAB_INVENTORY);
				return random(500, 750);
			} else {
				return random(500, 750);
			}

		case 3:
			if (random(1, 4) == 2) {
				final int x3 = random(0, 750);
				final int y3 = random(0, 500);
				moveMouse(0, 0, x3, y3);
				return random(1000, 1500);
			}

		case 4:
			if (random(1, 3) == 2) {
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
		wait(150);
		return 0;
	}

	public boolean clickInventoryItem(final int itemID, final boolean click) {
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
		final int idx = possible.get(0);
		final Point t = getInventoryItemPoint(idx);
		clickMouse(t, 5, 5, click);
		return true;
	}

	public boolean clickItem(final RSItem item) {
		if (getCurrentTab() != Constants.TAB_INVENTORY) {
			openTab(Constants.TAB_INVENTORY);
		}
		return atInventoryItem(item.getID(), "");
	}

	private boolean clickNPC(final RSNPC npc, final String action) {
		try {
			int a;
			final StringBuffer npcCommandBuf = new StringBuffer();
			npcCommandBuf.append(action);
			npcCommandBuf.append(" ");
			npcCommandBuf.append(npc.getName());
			final String npcCommand = npcCommandBuf.toString();
			for (a = 10; a-- >= 0;) {
				if (getMyPlayer().isInCombat()) {
					return false;
				}
				if (npc.getInteracting() != null
						&& !npc.isInteractingWithLocalPlayer()) {
					return false;
				}
				final List<String> menuItems = getMenuItems();
				if (menuItems.size() > 1) {
					if (listContainsString(menuItems, npcCommand)) {
						if (menuItems.get(0).contains(npcCommand)) {
							clickMouse(true);
							return true;
						} else {
							// clickMouse(false);
							wait(random(230, 520));
							return atMenu(npcCommand);
						}
					}
				}
				final Point screenLoc = npc.getScreenLocation();
				if (!pointOnScreen(screenLoc)) {
					return false;
				}
				final Point randomP = new Point(random(screenLoc.x - 15,
						screenLoc.x + 15), random(screenLoc.y - 15,
						screenLoc.y + 15));
				if (randomP.x >= 0 && randomP.y >= 0) {
					moveMouse(randomP);
				}
			}
			return false;
		} catch (final Exception e) {
			log.log(Level.SEVERE, "clickNPC(RSNPC, String) error: ", e);
			return false;
		}
	}

	private boolean energyCheck() {
		try {
			if (gEnergy() >= runEnergy && !isRunning()) {
				runEnergy = random(35, 65);
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

	public void getback() {
		if (!getMyPlayer().isInCombat() && !getMyPlayer().isMoving()) {
			final int first = random(3534, 3536);
			final int second = random(9694, 9696);
			final RSTile afk = new RSTile(first, second);
			atTile(afk, "ere");
		}
	}

	public RSNPC getNearestNPC(final int[] ids, final boolean inCage,
			final boolean isInteracting) {
		int Dist = 99999;
		RSNPC closest = null;
		final int[] validNPCs = Bot.getClient().getRSNPCIndexArray();

        for (final int element : validNPCs) {
        	Node node = Calculations.findNodeByID(Bot.getClient().getRSNPCNC(), element);
            if (node == null || !(node instanceof RSNPCNode)) {
                continue;
            }
            final RSNPC Monster = new RSNPC(((RSNPCNode) node).getRSNPC());
			if (Monster.getHPPercent() == 0) {
				continue;
			}
			if (isInteracting && Monster.isInteractingWithLocalPlayer()
					&& canReach(Monster.getLocation(), false)) {
				return Monster;
			}
			if (!inCage && !canReach(Monster.getLocation(), false)) {
				continue;
			}
			try {
				for (final int id : ids) {
					if (id != Monster.getID() || Monster.isInCombat()) {
						continue;
					}
					final int distance = distanceTo(Monster);
					if (distance < Dist) {
						Dist = distance;
						closest = Monster;
					}
				}
			} catch (final Exception e) {
			}
		}
		return closest;
	}

	public boolean hpcheck() {
		if (skills.getCurrentSkillLevel(3) <= random(30, 50)) {
			return true;
		}
		return false;
	}

	private boolean listContainsString(final List<String> list,
			final String string) {
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

	@Override
	public int loop() {
		if (reach) {
			getback();
			wait(random(2000, 3000));
			reach = false;
		}
		if (wait) {
			wait(random(700, 900));
			wait = false;
		}
		if (energyCheck()) {
			setRun(true);
			wait(random(750, 1000));
			return random(50, 150);
		} else {
			try {
				run();
			} catch (final Exception e) {
			}
		}
		return 0;
	}

	RSNPC npc() {
		final RSNPC npc = getNearestNPC(RcZhangsBarrows.monsters, false, true);
		if (npc != null) {
			npcCheck = false;
			try {
				final Point npcScreen = npc.getScreenLocation();
				if (pointOnScreen(npcScreen)
						|| distanceTo(npc.getLocation()) <= 1) {
					currentNPC = npc;
				}
			} catch (final Exception e) {
				log.log(Level.SEVERE, "attacking npc error: ", e);
				wait(random(1300, 1900));
			}
		}
		return currentNPC;
	}

	public void onRepaint(final Graphics g) {
		atkexp = skills.getCurrentSkillExp(Constants.STAT_ATTACK) - startatkExp;
		strexp = skills.getCurrentSkillExp(Constants.STAT_STRENGTH)
				- startstrExp;
		defexp = skills.getCurrentSkillExp(Constants.STAT_DEFENSE)
				- startdefExp;
		hpexp = skills.getCurrentSkillExp(Constants.STAT_HITPOINTS)
				- starthpExp;
		rangedexp = skills.getCurrentSkillExp(Constants.STAT_RANGE)
				- startrangedExp;
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

		if (startatkExp == 0) {
			startatkExp = skills.getCurrentSkillExp(Constants.STAT_ATTACK);
			oldatkExp = 0;
		}

		if (startstrExp == 0) {
			startstrExp = skills.getCurrentSkillExp(Constants.STAT_STRENGTH);
			oldstrExp = 0;
		}

		if (startdefExp == 0) {
			startdefExp = skills.getCurrentSkillExp(Constants.STAT_DEFENSE);
			olddefExp = 0;
		}

		if (starthpExp == 0) {
			starthpExp = skills.getCurrentSkillExp(Constants.STAT_HITPOINTS);
			oldhpExp = 0;
		}

		if (startrangedExp == 0) {
			startrangedExp = skills.getCurrentSkillExp(Constants.STAT_RANGE);
			oldrangedExp = 0;
		}
		int i = 0;
		if (getCurrentTab() == Constants.TAB_INVENTORY) {
			g.setColor(new Color(0, 0, 0, 175));
			g.fillRoundRect(555, 210, 175, 250, 10, 10);
			g.setColor(Color.WHITE);
			final int[] coords = new int[] { 225, 240, 255, 270, 285, 300, 315,
					330, 345, 360, 375, 390, 405, 420, 435, 450 };
			g.drawString(getClass().getAnnotation(ScriptManifest.class).name(),
					561, coords[0]);
			g.drawString("Version "
					+ getClass().getAnnotation(ScriptManifest.class).version(),
					561, coords[1]);
			g.drawString("Run Time: " + hours + ":" + minutes + ":" + seconds,
					561, coords[2]);
			// g.drawString("Current Level: " + getMyPlayer().getCombatLevel(),
			// 561, coords[4]);
			if (atkexp > 0) {
				i++;
				g.drawString("Attack exp gained: " + atkexp, 561,
						coords[(4 + i)]);
			}
			if (strexp > 0) {
				i++;
				g.drawString("Strength exp gained: " + strexp, 561,
						coords[(4 + i)]);
			}
			if (defexp > 0) {
				i++;
				g.drawString("Defence exp gained: " + defexp, 561,
						coords[(4 + i)]);
			}
			if (hpexp > 0) {
				i++;
				g.drawString("HP exp gained: " + hpexp, 561, coords[(4 + i)]);
			}
			if (rangedexp > 0) {
				i++;
				g.drawString("ranged exp gained: " + rangedexp, 561,
						coords[4 + i]);
			}
			g.drawString("Action: " + ACTION, 561, coords[12]);
		}
	}

	@Override
	public boolean onStart(final Map<String, String> args) {
		scriptStartTime = System.currentTimeMillis();
		runEnergy = random(50, 80);
		return true;
	}

	public void run() {
		if (ACTION == FIGHT) {
			final RSItemTile Bones = getGroundItemByID(RcZhangsBarrows.bones);
			final RSItemTile charms = getGroundItemByID(RcZhangsBarrows.Charms);
			if (!hpcheck() && shouldFight() & !getMyPlayer().isMoving()) {
				if (!clickNPC(npc(), "tack")) {
					wait(random(100, 300));
					antiBan();
					return;
				} else {
					wait(random(1000, 2500));
					return;
				}
			}
			final RSNPC Monsters = getNearestNPCByID(RcZhangsBarrows.monsters);
			if (Monsters == null) {
				getback();
				antiBan();
				return;
			}
			if (hpcheck()) {
				ACTION = EAT;
			}
			if (Bones != null && !isInventoryFull()
					&& !getMyPlayer().isInCombat()) {
				final Point loc = Calculations.tileToScreen(Bones);
				if (pointOnScreen(loc) && !getMyPlayer().isMoving()) {
					atTile(Bones, "ke");
					wait(random(700, 900));
					return;
				}
			}
			if (getMyPlayer().isInCombat()) {
				wait(random(300, 400));
				antiBan();
				return;
			}
			final RSItemTile charms1 = getGroundItemByID(RcZhangsBarrows.Charms);
			if (!hpcheck() && charms1 != null && !getMyPlayer().isInCombat()) {
				final Point loc = Calculations.tileToScreen(charms1);
				if (pointOnScreen(loc) && !getMyPlayer().isMoving()) {
					atTile(charms1, "ke");
					wait(random(700, 900));
					return;
				}
				if (!pointOnScreen(loc)) {
					return;
				}
				if (charms == null) {
					return;
				}
			}
			return;
		}
		if (ACTION == EAT) {
			if (inventoryContains(RcZhangsBarrows.peach) && hpcheck()
					&& !inventoryContains(RcZhangsBarrows.food)) {
				clickInventoryItem(RcZhangsBarrows.peach, true);
				wait(random(300, 600));
				moveMouse(0, 0, random(0, 750), random(0, 500));
				return;
			}
			if (inventoryContains(RcZhangsBarrows.food) && hpcheck()) {
				final RSItem Food = getInventoryItemByID(RcZhangsBarrows.food);
				clickItem(Food);
				wait(random(300, 600));
				return;
			}
			if (!getMyPlayer().isInCombat()
					&& !inventoryContains(RcZhangsBarrows.peach)) {
				ACTION = BTP;
			}
			if (!hpcheck()) {
				getback();
				ACTION = FIGHT;
			}
			return;
		}
		if (ACTION == BTP) {
			final RSItemTile Bone = getGroundItemByID(526);
			if (!inventoryContains(8015)) {
				log("Out of BTP Tabs!");
				stopScript();
			}
			if (inventoryContains(8015) && inventoryContains(526)) {
				clickInventoryItem(8015, true);
				wait(random(1000, 1500));
				ACTION = EAT;
			}
			if (!inventoryContains(526) && getInventoryCount() != 28
					&& Bone != null) {
				ACTION = PICKUPBONE;
			}
			return;
		}
		if (ACTION == PICKUPBONE) {
			final RSItemTile Bone = getGroundItemByID(526);
			if (isInventoryFull()) {
				ACTION = BTP;
			}
			while (!isInventoryFull() && Bone != null
					&& !getMyPlayer().isMoving()
					&& !inventoryContains(RcZhangsBarrows.peach)) {
				final RSItemTile bone = getGroundItemByID(526);
				atTile(bone, "one");
				wait(random(500, 1000));
				return;
			}
			if (Bone == null && inventoryContains(RcZhangsBarrows.bones)) {
				ACTION = BTP;
			}
			if (Bone == null && !inventoryContains(RcZhangsBarrows.bones)) {
				getback();
				ACTION = FIGHT;
			}
			if (inventoryContains(RcZhangsBarrows.peach)) {
				ACTION = EAT;
			}
			return;
		}
		if (ACTION == PICKUP) {
			final RSItemTile charms = getGroundItemByID(RcZhangsBarrows.Charms);
			final Point loc = Calculations.tileToScreen(charms);
			if (pointOnScreen(loc) && !getMyPlayer().isMoving()) {
				atTile(charms, "arm");
				wait(random(700, 900));
				ACTION = FIGHT;
			}
			if (!pointOnScreen(loc)) {
				ACTION = FIGHT;
			}
			if (charms == null) {
				ACTION = FIGHT;
			}
			return;
		}
	}

	public void serverMessageRecieved(final ServerMessageEvent arg0) {
		final String r = arg0.getMessage();
		if (r.contains("reach") || r.contains("can't") || r.contains("that!")) {
			reach = true;
		}
		if (r.contains("someone") || r.contains("else")) {
			reach = true;
		}
		if (r.contains("already") || r.contains("under")) {
			wait = true;
		}
	}

	public boolean shouldFight() {
		final RSItemTile Bones = getGroundItemByID(RcZhangsBarrows.bones);
		final RSItemTile charms = getGroundItemByID(RcZhangsBarrows.Charms);
		if (isInventoryFull() && charms == null) {
			return true;
		}
		if (charms != null) {
			return false;
		}
		if (!isInventoryFull() && Bones != null) {
			return false;
		}
		if (charms == null && Bones == null) {
			return true;
		}
		return false;
	}

	public boolean waitForAnimation(final int timeout) {
		final long start = System.currentTimeMillis();
		final RSPlayer myPlayer = getMyPlayer();
		@SuppressWarnings("unused")
		int anim = -1;

		while (System.currentTimeMillis() - start < timeout) {
			if ((anim = myPlayer.getAnimation()) != -1) {
				return true;
			}
			wait(50);
		}
		if ((anim = myPlayer.getAnimation()) == -1) {
			return false;
		}
		return false;
	}
}
