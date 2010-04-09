import java.awt.Point;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import org.rsbot.bot.Bot;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterfaceComponent;
import org.rsbot.script.wrappers.RSItemTile;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(name = "TISolver", authors = { "Fred" }, category = "Other", version = 1.05, description = "<html><body>To find out your country code, go to this site: http://pastebin.com/f6bc0778e<br><br>"
		+ "<input type=\"checkbox\" name=\"Auto\" value=\"yes\">Auto Create Account?<br><table width=\"239\" border=\"0\"><tr><td width=\"7\"></td><td width=\"216\">Prefix: <input type=\"text\" name=\"prefix\" id=\"pref\" size=10 /></td></tr><tr><td></td><td>Password: <input type=\"text\" name=\"password\" id=\"pass\" size=10 /><br>Country Code: <input type=\"text\" name=\"country\" id=\"cc\" size=3 /></td></tr></table></body></html>")
public class TISolver extends Script {
	int state = 0;
	int key;
	int suffix = 0, accepted = 0, retry = 0, create = 0, country = 1;
	RSTile original, middle;
	RSObject hole, plank, bag;
	RSNPC Vant, Goblin, Vant2, Roddeck;
	int[] NPCIDs = { 8850, 8862, 8848, 8867 };
	int[] armoursonfloor = { 1117, 1173, 1277 };
	RSInterfaceComponent[] Components;
	FileWriter fstream;
	BufferedWriter out;
	BufferedReader in;
	Point mouse;
	String prefix, accountname, password;
	boolean originalRandomState = Bot.disableRandoms;

	public boolean onStart(final Map<String, String> args) {

		Bot.disableRandoms = true;
		if (args.get("Auto") != null) {
			create = 1;
			prefix = args.get("prefix");
			password = args.get("password");
			state = 10;
			country = Integer.parseInt(args.get("country")) + 1;
		}
		return true;
	}

	public void onFinish() {
		Bot.disableRandoms = originalRandomState;
	}

	public int loop() {
		switch (state) {
		case 0:
			if (getInterface(243).getChild(4).containsText("I need you")) {
				state = 1;
				return random(200, 300);
			}
			if (getInterface(897).getChild(6).isValid()) {
				key = (random(0, 1) == 0) ? KeyEvent.VK_UP : KeyEvent.VK_DOWN;
				Bot.getInputManager().pressKey((char) key);
				wait(random(1000, 1500));
				Bot.getInputManager().releaseKey((char) key);
				key = (random(0, 1) == 0) ? KeyEvent.VK_LEFT
						: KeyEvent.VK_RIGHT;
				Bot.getInputManager().pressKey((char) key);
				wait(random(1000, 1500));
				Bot.getInputManager().releaseKey((char) key);
				return random(200, 300);
			}
			if (getInterface(241).isValid() || getInterface(242).isValid()
					|| getInterface(243).isValid()) {
				Continue();
				return random(200, 300);
			}
			if (getInterface(64).isValid()) {
				Continue2();
				return random(200, 300);
			}
			if (getInterface(896).getChild(1).isValid()) {
				Vant = getNearestNPCByID(NPCIDs[0]);
				if (Vant == null) {
					return random(200, 300);
				}
				walkTo(Vant.getLocation());
				waitUntilNotMoving();
				atNPC(Vant, "Talk");
				wait(random(1000, 2000));
				return random(200, 300);
			}
			return random(200, 300);
		case 1:
			if (getInterface(243).isValid()) {
				Continue();
			}
			RSItemTile stuff = getNearestGroundItemByID(armoursonfloor);
			while (stuff != null) {
				atTile(stuff, "Take");
				wait(random(3000, 3500));
				atTile(stuff, "Take");
				wait(random(1000, 1500));
				atTile(stuff, "Take");
				wait(random(1000, 1500));
				stuff = getNearestGroundItemByID(armoursonfloor);
			}
			openTab(Constants.TAB_INVENTORY);
			while (getInventoryCount(1117) == 1) {
				atInventoryItem(1117, "Wear");
				wait(random(500, 750));
			}
			while (getInventoryCount(1173) == 1) {
				atInventoryItem(1173, "Wield");
				wait(random(500, 750));
			}
			while (getInventoryCount(1277) == 1) {
				atInventoryItem(1277, "Wield");
				wait(random(500, 750));
			}
			openTab(Constants.TAB_EQUIPMENT);
			Goblin = getNearestNPCByID(NPCIDs[1]);
			if (Goblin == null) {
				state = 2;
				return random(200, 300);
			}
			if (!atNPC(Goblin, "Attack")) {
				state = 2;
				return random(200, 300);
			}
			wait(random(3000, 4000));
			while (getMyPlayer().isInCombat()) {
				wait(10);
			}
			Goblin = getNearestNPCByID(NPCIDs[1]);
			if (Goblin != null) {
				return random(200, 300);
			}
			state = 2;
			return random(200, 300);
		case 2:
			if (getInterface(66).getChild(6).containsText("Off I go")) {
				state = 3;
				Continue2();
				wait(random(2000, 3000));
				return random(200, 300);
			}
			if (getInterface(243).isValid() || getInterface(242).isValid()) {
				Continue();
				wait(random(500, 1000));
				return random(200, 300);
			}
			if (getInterface(65).isValid() || getInterface(66).isValid()) {
				Continue2();
				wait(random(500, 1000));
				return random(200, 300);
			}
			if (getInterface(896).getChild(1).isValid()) {
				Vant = getNearestNPCByID(NPCIDs[0]);
				if (Vant == null) {
					state = 3;
					return random(200, 300);
				}
				if (!tileOnScreen(Vant.getLocation())) {
					walkTo(Vant.getLocation());
					waitUntilNotMoving();
				}
				atNPC(Vant, "Talk");
				waitUntilNotMoving();
				wait(random(1000, 2000));
				return random(200, 300);
			}
		case 3:
			wait(random(4000, 5000));
			moveMouse(random(586, 605), random(177, 197));
			clickMouse(true);
			wait(random(2000, 3000));
			moveMouse(random(564, 664), random(309, 314));
			clickMouse(true);
			wait(random(4000, 5000));
			moveMouse(random(457, 470), random(67, 79));
			clickMouse(true);
			wait(random(2000, 3000));
			moveMouse(random(536, 548), random(134, 148));
			clickMouse(true);
			wait(random(4000, 5000));
			Components = getInterface(896).getChild(1).getComponents();
			while (Components[0] != null
					&& !Components[0].getText().contains("Time to get on")) {
				moveMouse(random(742, 752), random(13, 23));
				clickMouse(true);
				wait(random(6000, 7000));
				moveMouse(random(538, 548), random(19, 31));
				clickMouse(true);
				wait(random(4000, 5000));
				Components = getInterface(896).getChild(1).getComponents();
			}
			bag = getNearestObjectByID(45783);
			while (getInventoryCount(15277) == 0) {
				if (!tileOnScreen(bag.getLocation())) {
					walkTo(bag.getLocation());
					waitUntilNotMoving();
				}
				atObject(bag, "Search");
				wait(random(6000, 7000));
			}
			if (getInterface(519).isValid()) {
				Continue();
			}
			hole = getNearestObjectByID(45603);
			plank = getNearestObjectByID(45652);
			atObject(hole, "Climb");
			wait(random(4000, 5000));
			setCameraAltitude(true);
			atObject(plank, "Take");
			openTab(Constants.TAB_INVENTORY);

			wait(random(2000, 3000));
			while (getInventoryCount(15276) == 1) {
				atInventoryItem(15276, "Use");
				wait(random(750, 1000));
				atObject(hole, "Use");
				wait(random(2000, 3000));
			}
			atObject(hole, "Climb");
			wait(random(3000, 4000));
			Continue();
			state = 4;
			return random(200, 300);
		case 4:
			original = new RSTile(getMyPlayer().getLocation().getX(),
					getMyPlayer().getLocation().getY());
			middle = new RSTile(getMyPlayer().getLocation().getX() - 8,
					getMyPlayer().getLocation().getY() + 12);
			walkTo(middle);
			waitUntilNotMoving();
			setRun(true);
			walkTo(middle);
			waitUntilNotMoving();
			RSObject Bowl = getNearestObjectByID(45795);
			walkTo(Bowl.getLocation());
			waitUntilNotMoving();
			while (getInventoryCount(15277) == 1) {
				if (!tileOnScreen(Bowl.getLocation())
						|| getMyPlayer().getLocation().getY() > (Bowl
								.getLocation().getY() + 2)
						|| getMyPlayer().getLocation().getY() < (Bowl
								.getLocation().getY() - 2)) {
					walkTo(Bowl.getLocation());
					waitUntilNotMoving();
				}
				atInventoryItem(15277, "Use");
				wait(random(2000, 3000));
				atObject(Bowl, "Use");
				wait(random(5000, 6000));
			}
			walkTo(middle);
			waitUntilNotMoving();
			while (getMyPlayer().getLocation().getY() > (original.getY() + 4)) {
				walkTo(original);
				waitUntilNotMoving();
			}
			wait(random(2000, 3000));
			while (getMyPlayer().getLocation().getY() > (original.getY() - 2)) {
				atObject(hole, "Climb");
				wait(random(5000, 6000));
			}
			while (!getInterface(242).getChild(4).containsText(
					"Have you placed")) {
				atNPC(Vant, "Talk");
				wait(random(3000, 4000));
			}
			Continue();
			wait(random(750, 1000));
			moveMouse(random(204, 315), random(396, 405));
			clickMouse(true);
			wait(random(750, 1000));
			Continue2();
			wait(random(500, 750));
			Continue();
			if (getInventoryCount(15277) == 1) {
				atObject(hole, "Climb");
				wait(random(3000, 4000));
				return random(200, 300);
			}
			Continue();
			wait(random(1000, 2000));
			Continue();
			wait(random(1000, 2000));
			Continue();
			state = 5;
			return random(200, 300);
		case 5:
			Vant2 = getNearestNPCByID(NPCIDs[2]);
			while (Vant2 == null) {
				Vant2 = getNearestNPCByID(NPCIDs[2]);
				wait(10);
			}
			while (!getInterface(242).getChild(4).containsText("Thank you")) {
				wait(10);
			}
			wait(random(1500, 2000));
			Vant2 = getNearestNPCByID(NPCIDs[2]);
			Continue();
			wait(random(2000, 3000));
			walkTo(Vant2.getLocation());
			waitUntilNotMoving();
			while (!getInterface(243).getChild(4).containsText(
					"As I mentioned earlier")) {
				if (!tileOnScreen(Vant2.getLocation())) {
					walkTo(Vant2.getLocation());
					waitUntilNotMoving();
				}
				atNPC(Vant2, "Talk");
				wait(random(750, 1000));
			}
			Continue();
			wait(random(750, 1000));
			Continue();
			wait(random(750, 1000));
			bag = getNearestObjectByID(45783);
			while (getInventoryCount(590) < 1) {
				if (!tileOnScreen(bag.getLocation())) {
					walkTo(bag.getLocation());
					waitUntilNotMoving();
				}
				atObject(bag, "Search");
				wait(random(3000, 4000));
			}
			wait(random(7000, 9000));
			walkTo(new RSTile(getMyPlayer().getLocation().getX() - 6,
					getMyPlayer().getLocation().getY() + 12));
			waitUntilNotMoving();
			wait(random(2000, 2500));
			Vant2 = getNearestNPCByID(NPCIDs[2]);
			walkTo(Vant2.getLocation());
			waitUntilNotMoving();
			setCameraRotation(90 + random(0, 10));
			wait(random(1000, 2000));
			atNPC(Vant2, "Talk");
			wait(random(2000, 3000));
			Continue();
			wait(random(750, 1000));
			Continue2();
			wait(random(750, 1000));
			Continue();
			wait(random(750, 1000));
			Continue();
			wait(random(750, 1000));
			Continue();
			wait(random(750, 1000));
			Continue();
			wait(random(750, 1000));
			while (getInventoryCount(15278) == 1) {
				atInventoryItem(15278, "Rip");
				wait(random(1000, 1500));
			}
			while (getInventoryCount(15280) > 0) {
				atInventoryItem(15284, "Use");
				wait(random(1000, 1500));
				atInventoryItem(15280, "Use");
				wait(random(1000, 1500));
			}
			RSObject Pillar = getNearestObjectByID(45564);
			while (getInventoryCount(15281) > 1) {
				atInventoryItem(15281, "Use");
				wait(random(1000, 1500));
				atObject(Pillar, "Use");
				wait(random(1000, 1500));
			}
			while (getInventoryCount(15282) == 1) {
				atInventoryItem(15282, "Use");
				wait(random(1000, 1500));
				atObject(Pillar, "Use");
				wait(random(1000, 1500));
			}
			RSTile pillarfront = new RSTile(getMyPlayer().getLocation().getX(),
					getMyPlayer().getLocation().getY() + 8);
			walkTo(pillarfront);
			waitUntilNotMoving();
			while (getInventoryCount(15281) == 1) {
				atInventoryItem(15281, "Use");
				wait(random(1000, 1500));
				Pillar = getNearestObjectByID(45565);
				atObject(Pillar, "Use");
				wait(random(1000, 1500));
			}
			while (getInventoryCount(15283) == 1) {
				atInventoryItem(15283, "Use");
				wait(random(1000, 1500));
				atObject(Pillar, "Use");
				wait(random(1000, 1500));
			}
			walkTo(Vant2.getLocation());
			waitUntilNotMoving();
			while (getInventoryCount(590) > 1) {
				atInventoryItem(590, "Use");
				wait(random(1000, 1500));
				atNPC(Vant2, "Use");
				wait(random(5000, 6000));
			}
			Continue();
			wait(random(2000, 3000));
			walkTo(pillarfront);
			waitUntilNotMoving();
			while (!getInterface(242).getChild(5).containsText("Run away")) {
				atInventoryItem(590, "Use");
				wait(random(1000, 1500));
				atObject(Pillar, "Use");
				wait(random(7000, 8000));
			}
			Continue();
			walkTo(new RSTile(getMyPlayer().getLocation().getX() + 9,
					getMyPlayer().getLocation().getY() + 9));
			state = 6;
			return random(200, 300);
		case 6:
			while (!getInterface(243).isValid()) {
				wait(10);
			}
			wait(random(1000, 2000));
			Continue();
			wait(random(750, 1000));
			Continue();
			wait(random(750, 1000));
			Continue();
			wait(random(3000, 4000));
			Roddeck = getNearestNPCByID(NPCIDs[3]);
			atNPC(Roddeck, "Talk");
			wait(random(2000, 3000));
			Continue();
			wait(random(750, 1000));
			Continue2();
			wait(random(750, 1000));
			Continue();
			wait(random(750, 1000));
			Continue();
			wait(random(750, 1000));
			moveMouse(random(730, 739), random(5, 14));
			clickMouse(true);
			wait(random(2000, 3000));
			moveMouse(random(490, 498), random(10, 19));
			clickMouse(true);
			wait(random(2000, 3000));
			Continue();
			wait(random(750, 1000));
			Continue();
			wait(random(750, 1000));
			openTab(Constants.TAB_EQUIPMENT);
			wait(random(2000, 3000));
			while (equipmentContains(1117)) {
				atEquippedItem(1117, "Remove");
			}
			while (equipmentContains(1173)) {
				atEquippedItem(1173, "Remove");
			}
			while (equipmentContains(1277)) {
				atEquippedItem(1277, "Remove");
			}
			openTab(Constants.TAB_INVENTORY);
			Vant2 = getNearestNPCByID(NPCIDs[2]);
			while (getInventoryCount(590) == 1) {
				atInventoryItem(590, "Use");
				wait(random(1000, 1500));
				atNPC(Vant2, "Use");
				wait(random(5000, 6000));
			}
			Continue();
			wait(random(1500, 2000));
			Continue2();
			wait(random(1500, 2000));
			Continue();
			wait(random(1500, 2000));
			Continue();
			wait(random(1500, 2000));
			Continue();
			wait(random(1500, 2000));
			moveMouse(random(204, 315), random(396, 405));
			clickMouse(true);
			wait(random(1500, 2000));
			Continue2();
			wait(random(1500, 2000));
			while (getInventoryCount(1265) < 1) {
				atNPC(Roddeck, "Talk");
				wait(random(2000, 3000));
				Continue();
				wait(random(1500, 2000));
				Continue();
				wait(random(1500, 2000));
				int gamble = 1;
				if (gamble == 1) {
					moveMouse(random(204, 315), random(396, 405));
					clickMouse(true);
					wait(random(1500, 2000));
					Continue2();
					wait(random(1500, 2000));
					Continue();
					wait(random(1500, 2000));
					state = 7;
				} else {
					moveMouse(random(204, 315), random(437, 444));
					clickMouse(true);
					wait(random(1500, 2000));
					Continue2();
					wait(random(1500, 2000));
					Continue();
					wait(random(1500, 2000));
					state = 8;
				}
			}
			return random(200, 300);
		case 7:
			walkTo(new RSTile(getMyPlayer().getLocation().getX() - 11,
					getMyPlayer().getLocation().getY() + 1));
			waitUntilNotMoving();
			wait(random(3000, 4000));
			RSObject Rock = getNearestObjectByID(45595);
			while (getMyPlayer().getAnimation() != 12278) {
				Rock = getNearestObjectByID(45595);
				atObject(Rock, "Mine");
				wait(random(1000, 2000));
			}
			while (!getInterface(211).getChild(1).containsText("You'll need")) {
				wait(10);
			}
			wait(random(1000, 2000));
			Continue();
			wait(random(1000, 2000));
			openTab(Constants.TAB_STATS);
			wait(random(1000, 2000));
			moveMouse(random(526, 546), random(472, 495));
			clickMouse(true);
			wait(random(1000, 2000));
			moveMouse(random(730, 739), random(5, 14));
			clickMouse(true);
			wait(random(2000, 3000));
			moveMouse(random(41, 150), random(112, 117));
			clickMouse(true);
			wait(random(2000, 3000));
			Continue();
			wait(random(1000, 1500));
			Continue();
			wait(random(1000, 1500));
			moveMouse(random(490, 498), random(10, 19));
			clickMouse(true);
			wait(random(1000, 2000));
			RSObject Rocks = getNearestObjectByID(45583);
			while (skills.getCurrentSkillLevel(Constants.STAT_MINING) < 2) {
				atObject(Rocks, "Mine");
				wait(random(2000, 3000));
				while (getMyPlayer().getAnimation() == 6747) {
					wait(10);
				}
			}
			wait(random(1000, 2000));
			Continue();
			wait(random(2000, 3000));
			Continue();
			wait(random(1000, 2000));
			while (getInventoryCount(15286) > 0) {
				atInventoryItem(15286, "Drop");
				wait(random(1000, 2000));
				Continue();
				wait(random(1000, 2000));
			}
			atObject(Rock, "Mine");
			while (!getInterface(211).getChild(1).containsText("You succeed")) {
				wait(10);
			}
			wait(random(1000, 2000));
			Continue();
			wait(random(1000, 2000));
			Continue();
			wait(random(1000, 2000));
			setCameraAltitude(true);
			RSObject Gap = getNearestObjectByID(45597);
			original = new RSTile(getMyPlayer().getLocation().getX(),
					getMyPlayer().getLocation().getY());
			while (getMyPlayer().getLocation().getX() > original.getX() - 2) {
				atObject(Gap, "Climb");
				wait(random(3000, 4000));
			}
			wait(random(2000, 3000));
			state = 9;
			return random(200, 300);
		case 9:
			wait(random(5000, 6000));
			setCameraRotation(180 + random(-5, 5));
			setCameraAltitude(false);
			wait(random(2000, 3000));
			Vant2 = getNearestNPCByID(NPCIDs[2]);
			walkTo(new RSTile(Vant2.getLocation().getX() - 4, Vant2
					.getLocation().getY()));
			waitUntilNotMoving();
			while (!getInterface(243).getChild(6).containsText("get outside")) {
				atDoor(45784, 's');
				clickMouse(true);
				wait(random(2000, 3000));
			}
			wait(random(1000, 2000));
			Continue();
			while (!getInterface(137).getChild(66).containsText(
					"You have unlocked a new")) {
				wait(10);
			}
			wait(random(1000, 2000));
			moveMouse(random(202, 317), random(445, 451));
			clickMouse(true);
			wait(random(1000, 2000));
			moveMouse(random(438, 451), random(71, 82));
			clickMouse(true);
			wait(random(1000, 2000));
			moveMouse(random(202, 317), random(445, 451));
			clickMouse(true);
			wait(random(1000, 2000));
			setCameraRotation(1 + random(1, 5));
			setCameraAltitude(true);
			wait(random(2000, 3000));
			Roddeck = getNearestNPCByID(NPCIDs[3]);
			atNPC(Roddeck, "Talk");
			wait(random(3000, 4000));
			Continue();
			wait(random(1000, 2000));
			Vant2 = getNearestNPCByID(NPCIDs[2]);
			atNPC(Vant2, "Talk");
			wait(random(3000, 4000));
			Continue();
			wait(random(1000, 2000));
			Continue();
			wait(random(2000, 3000));
			Continue();
			wait(random(1000, 2000));
			atNPC(Roddeck, "Talk");
			wait(random(3000, 4000));
			Continue();
			wait(random(1000, 2000));
			Continue();
			wait(random(1000, 2000));
			moveMouse(random(526, 546), random(472, 495));
			clickMouse(true);
			wait(random(1000, 2000));
			moveMouse(random(582, 704), random(422, 430));
			clickMouse(true);
			wait(random(2000, 3000));
			moveMouse(random(372, 474), random(190, 270));
			clickMouse(true);
			wait(random(2000, 3000));
			moveMouse(random(730, 739), random(5, 14));
			clickMouse(true);
			wait(random(2000, 3000));
			moveMouse(random(490, 498), random(10, 19));
			clickMouse(true);
			wait(random(2000, 3000));
			setCameraAltitude(false);
			wait(random(2000, 3000));
			while (getMyPlayer().getLocation().getY() != 3246) {
				atDoor(45801, 'n');
				clickMouse(true);
				wait(random(500, 750));
			}
			wait(random(1000, 2000));
			Continue();
			if (create == 0) {
				log("Tutorial Finished! =)");
				stopScript();
			} else {
				File accounts = new File("Accounts.txt");
				if (accounts.exists()) {
					try {
						fstream = new FileWriter("Accounts.txt", true);
						out = new BufferedWriter(fstream);
						out.newLine();
						out.write(accountname);
						out.close();
					} catch (IOException e) {
					}
				} else {
					try {
						fstream = new FileWriter("Accounts.txt", false);
						BufferedWriter out = new BufferedWriter(fstream);
						out.write(accountname);
						out.close();
					} catch (IOException e) {
					}
				}
				log("Account " + accountname + " has been saved");
				logout();

				while (!getInterface(744).isValid()) {
					wait(10);
				}
				wait(random(5000, 6000));
				state = 10;
				return random(200, 300);
			}

		case 10:
			moveMouse(random(323, 438), random(215, 225));
			clickMouse(true);
			wait(random(2000, 3000));
			while (accepted == 0) {
				if (retry == 1) {
					key = KeyEvent.VK_BACK_SPACE;
					for (int i = 0; i < 13; i++) {
						Bot.getInputManager().pressKey((char) key);
						wait(random(400, 500));
						Bot.getInputManager().releaseKey((char) key);
					}
				}
				retry = 0;
				if (suffix == 0) {
					accountname = prefix;
				} else {
					accountname = prefix + Integer.toString(suffix);
				}
				suffix++;
				if (accountname.length() > 12) {
					log("Used up all account names =/");
					stopScript();
				}
				sendText(accountname, false);
				wait(random(1000, 2000));
				sendText("", true);
				wait(random(6000, 7000));
				if (!getInterface(744).getChild(61).containsText("Sorry, that")) {
					accepted = 1;
				} else {
					retry = 1;
				}
			}
			accepted = 0;
			sendText(password, false);
			wait(random(1000, 2000));
			sendText("", true);
			wait(random(1000, 2000));
			sendText(password, false);
			wait(random(1000, 2000));
			sendText("", true);
			wait(random(1000, 2000));
			key = KeyEvent.VK_DOWN;
			Bot.getInputManager().pressKey((char) key);
			wait(random(3000, 4000));
			Bot.getInputManager().releaseKey((char) key);
			moveMouse(random(259, 335), random(289, 299));
			clickMouse(true);
			wait(random(1000, 2000));
			Bot.getInputManager().pressKey((char) key);
			wait(random(2000, 3000));
			Bot.getInputManager().releaseKey((char) key);
			sendText("", true);
			wait(random(1000, 2000));
			moveMouse(random(358, 378), random(287, 296));
			clickMouse(true);
			wait(random(1000, 2000));
			sendText(Integer.toString(random(1979, 1994)), false);
			wait(random(1000, 2000));
			sendText("", true);
			wait(random(1000, 2000));
			for (int o = 0; o < country; o++) {
				Bot.getInputManager().pressKey((char) key);
				wait(10);
				Bot.getInputManager().releaseKey((char) key);
			}
			moveMouse(random(452, 659), random(412, 418));
			clickMouse(true);
			wait(random(6000, 7000));
			moveMouse(random(78, 85), random(360, 366));
			clickMouse(true);
			wait(random(1000, 2000));
			moveMouse(random(78, 85), random(381, 387));
			clickMouse(true);
			wait(random(1000, 2000));
			moveMouse(random(456, 653), random(439, 447));
			clickMouse(true);
			wait(random(6000, 7000));
			moveMouse(random(322, 438), random(343, 350));
			clickMouse(true);
			wait(random(1000, 2000));
			sendText(accountname, false);
			wait(random(1000, 2000));
			sendText("", true);
			wait(random(1000, 2000));
			sendText(password, false);
			wait(random(1000, 2000));
			sendText("", true);
			wait(random(10000, 12000));
			moveMouse(random(320, 436), random(399, 417));
			clickMouse(true);
			wait(random(3000, 4000));
			state = 0;
			return random(200, 300);
		default:
			break;
		}
		return random(200, 300);
	}

	public void waitUntilNotMoving() {
		wait(random(700, 1000));
		while (getMyPlayer().isMoving()) {
			wait(random(25, 100));
		}
	}

	public void Continue() {
		mouse = getMouseLocation();
		if (mouse.x < 232 || mouse.x > 375 || mouse.y < 450 || mouse.y > 458) {
			moveMouse(random(232, 375), random(450, 458));
			clickMouse(true);
		} else {
			clickMouse(true);
		}
		wait(random(1000, 2000));
	}

	public void Continue2() {
		mouse = getMouseLocation();
		if (mouse.x < 138 || mouse.x > 283 || mouse.y < 450 || mouse.y > 458) {
			moveMouse(random(138, 283), random(450, 458));
			clickMouse(true);
		} else {
			clickMouse(true);
		}
		wait(random(1000, 2000));
	}
}