import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Map;

import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSNPC;

@ScriptManifest(authors = { "RcZhang" }, category = "Crafting", name = "GemZ", version = 1.61, description = "<hmtl><center>"
		+ "<p>============================="
		+ "<br>--GemZ--"
		+ "<br>By RcZhang"
		+ "<br>-----------------------------"
		+ "<br>Version 1.61"
		+ "<br>-----------------------------</p>"
		+ "<p>Start in any bank with Chisel in Inventory and uncut gems visible in current bank tab.</p>"
		+ "<p>-----------------------------</p>"
		+ "<table>"
		+ "<tr><td><select name='gem'>"
		+ "<option>Sapphire"
		+ "<option>Emerald"
		+ "<option>Ruby"
		+ "<option>Diamond"
		+ "<option>Dragonstone"
		+ "<option>Onyx"
		+ "<option>Opal"
		+ "<option>Jade"
		+ "<option>Red Topaz"
		+ "</select></td></tr>"
		+ "</table>"
		+ "<p>=============================</p>"
		+ "</center></html>")
public class GemZ extends Script implements PaintListener {

	public boolean finished;

	public int Uncut;

	public int Cut;

	public int Chisel = 1755;

	public int[] Bank = { 6532, 6533, 6534, 6535 };

	public int CutAnimation = 888;

	public int startLevel = 0;

	public long startTime;

	public int startXP = 0;
	Color BG = new Color(0, 0, 0, 100);

	public boolean clickInventoryItem(final int itemID, final boolean click) { // credits
		// to
		// bug5532/drizzt1112
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

	public void deposit() {
		if (bank.isOpen()) {
			bank.depositAllExcept(Chisel);
		} else {
			bank.open();
		}
	}

	public void handleBank() {
		final RSNPC banker = getNearestNPCByID(Bank);
		if (banker == null) {
			wait(random(100, 300));
		}
		if (!bank.isOpen()) {
			atNPC(banker, "Bank");
			wait(random(200, 400));
		}
		if (bank.isOpen()) {
			wait(random(100, 300));
		}
	}

	@Override
	public int loop() {
		if (finished) {
			log("No more uncut gems available.  Script is stopping.");
			return -1;
		}

		if (inventoryContains(Uncut) && !inventoryContains(Cut)) {
			clickInventoryItem(Chisel, true);
			wait(random(100, 300));
			clickInventoryItem(Uncut, true);
			wait(random(300, 600));
			clickMouse(225 + random(0, 75), 380 + random(0, 70), false);
			atMenu("Make All");
			wait(random(1000, 2000));
		}
		if (getMyPlayer().getAnimation() == CutAnimation) {
			wait(random(300, 700));
		}
		if (!inventoryContains(Uncut) && inventoryContains(Cut)) {
			deposit();
		}
		if (!inventoryContains(Uncut) && !inventoryContains(Cut)) {
			withdraw();
		}
		if (!inventoryContains(Chisel)) {
			withdrawChisel();
		}
		return random(100, 300);
	}

	public void onRepaint(final Graphics g) {
		long millis = System.currentTimeMillis() - startTime;
		final long hours = millis / (1000 * 60 * 60);
		millis -= hours * 1000 * 60 * 60;
		final long minutes = millis / (1000 * 60);
		millis -= minutes * 1000 * 60;
		final long seconds = millis / 1000;
		final int XPChange = skills.getCurrentSkillExp(14) - startXP;
		final int LevelChange = skills.getCurrentSkillLevel(14) - startLevel;
		@SuppressWarnings("unused")
		final int topX = 465 - 270, topY = 445 - 80, x = topX + 5;
		@SuppressWarnings("unused")
		final int y = topY + 5;
		g.setColor(BG);
		g.fill3DRect(topX, topY, 465 - topX, 445 - topY, true);
		g.setFont(new Font("Verdana", Font.BOLD, 11));
		g.setColor(Color.green);
		g.drawString("Runtime: " + hours + " hours " + minutes + " minutes "
				+ seconds + " seconds.", 200, 380);
		g.drawString("Current: "
				+ skills.getCurrentSkillLevel(Constants.STAT_CRAFTING)
				+ " levels and "
				+ skills.getCurrentSkillExp(Constants.STAT_CRAFTING) + " exp.",
				200, 395);
		g.drawString("Gained: " + LevelChange + " levels and " + XPChange
				+ " exp.", 200, 410);
		g.drawString(skills.getXPToNextLevel(Constants.STAT_CRAFTING)
				+ " XP to next level", 200, 425);
		g.drawString("We are "
				+ skills.getPercentToNextLevel(Constants.STAT_CRAFTING)
				+ "% to next level.", 200, 440);
	}

	@Override
	public boolean onStart(final Map<String, String> args) {
		finished = false;

		System.out.println("Starting GemZ");
		if (args.get("gem").equals("Sapphire")) {
			Uncut = 1623;
			Cut = 1607;
		}
		if (args.get("gem").equals("Emerald")) {
			Uncut = 1621;
			Cut = 1605;
		}
		if (args.get("gem").equals("Ruby")) {
			Uncut = 1619;
			Cut = 1603;
		}
		if (args.get("gem").equals("Diamond")) {
			Uncut = 1617;
			Cut = 1601;
		}
		if (args.get("gem").equals("Dragonstone")) {
			Uncut = 1631;
			Cut = 1615;
		}
		if (args.get("gem").equals("Onyx")) {
			Uncut = 6571;
			Cut = 6573;
		}
		if (args.get("gem").equals("Opal")) {
			Uncut = 1625;
			Cut = 1609;
		}
		if (args.get("gem").equals("Jade")) {
			Uncut = 1627;
			Cut = 1611;
		}
		if (args.get("gem").equals("Red Topaz")) {
			Uncut = 1629;
			Cut = 1613;
		}
		startTime = System.currentTimeMillis();
		if (isLoggedIn()) {
			startLevel = skills.getCurrentSkillLevel(Constants.STAT_CRAFTING);
			startXP = skills.getCurrentSkillExp(Constants.STAT_CRAFTING);

		}
		return true;
	}

	public void withdraw() {
		if (bank.isOpen()) {
			wait(random(200, 400));

			if (bank.getItemByID(Uncut) == null) {
				finished = true;
				return;
			}

			bank.atItem(Uncut, "Withdraw-All");
			wait(random(200, 400));
			bank.close();
		} else {
			bank.open();
		}
	}

	public void withdrawChisel() {
		if (bank.isOpen()) {
			wait(random(200, 400));
			bank.atItem(Chisel, "Withdraw-1");
			wait(random(200, 400));
		} else {
			bank.open();
		}
	}
}
