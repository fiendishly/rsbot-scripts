import java.awt.*;
import java.util.*;

import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.*;

@ScriptManifest(authors = {"Javac"}, category = "Prayer", name = "BonesPlus", version = 1.3, description =
		"<html><head></head><body><center>"
				+ "<img src=http://i834.photobucket.com/albums/zz270/rsbothelper/Prayer_cape.png								"
				+ "< Bone Types"
				+ "<select name=bones><option>Bones<option>Big Bones<option>Fayrg Bones<option>Dragon Bones<option>Baby Dragon Bones<option>Burnt Bones<option>Jogre Bones<option>Wyvern Bones<option>Dagannoth Bones</select>"
				+ "</center></body></html>")

public class BonesPlus extends Script implements PaintListener {

	public long timeRunning = 0, secounds = 0, minutes = 0, hours = 0;
	long startTime = System.currentTimeMillis();
	private String status = "Online";
	public int expGained;
	public int startexp;
	public float exphour;
	public float expmin;
	public float expsec;
	public int exp;

	private int bones;

	public boolean onStart(Map<String, String> args) {
		if (args.get("bones").equals("Bones")) {
			bones = 526;
		} else if (args.get("bones").equals("Big Bones")) {
			bones = 532;
		} else if (args.get("bones").equals("Fayrg Bones")) {
			bones = 4830;
		} else if (args.get("bones").equals("Dragon Bones")) {
			bones = 536;
		} else if (args.get("bones").equals("Baby Dragon Bones")) {
			bones = 534;
		} else if (args.get("bones").equals("Burnt Bones")) {
			bones = 528;
		} else if (args.get("bones").equals("Jogre Bones")) {
			bones = 3125;
		} else if (args.get("bones").equals("Wyvern Bones")) {
			bones = 6812;
		} else if (args.get("bones").equals("Dagannoth Bones")) {
			bones = 6729;
		}
		startTime = System.currentTimeMillis();
		return true;
	}

	public int loop() {
		if (!isIdle()) {
			return random(50, 200);
		} else if (bank.isOpen()) {
			if (inventoryContains(bones)) {
				bank.close();
				return random(200, 500);
			} else {
				bank.withdraw(bones, 0);
				return random(300, 600);
			}
		} else {
			if (inventoryContains(bones)) {
				atInventoryItem(bones, "Bury");
				return random(200, 150);
			} else {
				bank.open();
				return random(300, 800);
			}
		}
	}

	public void onRepaint(Graphics render) {
		((Graphics2D) render).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if (isLoggedIn()) {
			if (startexp == 0) {
				startexp = skills.getCurrentSkillExp(Constants.STAT_PRAYER);
			}
			expGained = skills.getCurrentSkillExp(Constants.STAT_PRAYER) - startexp;

			timeRunning = System.currentTimeMillis() - startTime;
			secounds = timeRunning / 1000;

			if (secounds >= 60) {
				minutes = secounds / 60;
				secounds -= minutes * 60;
			}
			if (minutes >= 60) {
				hours = minutes / 60;
				minutes -= hours * 60;
			}

			if ((minutes > 0 || hours > 0 || secounds > 0) && expGained > 0) {
				expsec = ((float) expGained) / (float) (secounds + (minutes * 60) + (hours * 60 * 60));
			}
			exphour = expmin * 60;
			expmin = expsec * 60;

			long millis = System.currentTimeMillis() - startTime;
			long hours = millis / (1000 * 60 * 60);
			millis -= hours * (1000 * 60 * 60);
			long minutes = millis / (1000 * 60);
			millis -= minutes * (1000 * 60);
			render.setColor(Color.red);

			render.setFont(new Font("Times", Font.PLAIN, 12));
			render.drawString("Time Running: " + hours + ":" + minutes + ":" + secounds, 9 - 1, 285 - 1);
			render.drawString("XP Gained: " + expGained, 9 - 1, 300 - 1);
			render.drawString("XP Per hour: " + (int) exphour, 9 - 1, 315 - 1);
			render.drawString("Status: " + status, 9 - 1, 330 - 1);

		}

	}
}