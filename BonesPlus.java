 //imports
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.imageio.ImageIO;

import org.rsbot.bot.Bot;
import org.rsbot.bot.input.Mouse;
import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;

@ScriptManifest(authors = { "Javac" }, category = "Prayer", name = "BonesPlus", version = 1.5, description = "<html><head></head><body><center>"
	+ "<img src=http://i834.photobucket.com/albums/zz270/rsbothelper/Prayer_cape.png								"
	+ "< Bone Types"
	+ "<select name=bones><option>Bones<option>Big Bones<option>Fayrg Bones<option>Dragon Bones<option>Baby Dragon Bones<option>Burnt Bones<option>Jogre Bones<option>Wyvern Bones<option>Dagannoth Bones<option>Bat Bones<option>Raurg Bones<option>Shaikahan Bones<option>Wolf Bones<option>Zogre Bones<option>Monkey Bones</select>"
	+ "</center></body></html>")
	public class BonesPlus extends Script implements PaintListener,
	ServerMessageListener {

	public long timeRunning = 0, secounds = 0, minutes = 0, hours = 0;
	long startTime = System.currentTimeMillis();
	public int expGained, startexp;
	public float exphour, expmin, expsec;
	public int exp, boned, bones, bonesPerHour;
	BufferedImage normal = null, clicked = null;

	@Override
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
		} else if (args.get("bones").equals("Bat Bones")) {
			bones = 530;
		} else if (args.get("bones").equals("Raurg Bones")) {
			bones = 4832;
		} else if (args.get("bones").equals("Shaikahan Bones")) {
			bones = 3123;
		} else if (args.get("bones").equals("Wolf Bones")) {
			bones = 2859;
		} else if (args.get("bones").equals("Zogre Bones")) {
			bones = 4812;
		} else if (args.get("bones").equals("Monkey Bones")) {
			bones = 3183;
		}
		try {
			final URL cursorURL = new URL(
			"http://dl.dropbox.com/u/3900566/Mouse.png");
			final URL cursor80URL = new URL(
			"http://dl.dropbox.com/u/3900566/click.png");
			normal = ImageIO.read(cursorURL);
			clicked = ImageIO.read(cursor80URL);
		} catch (MalformedURLException e) {
			log("Unable to buffer cursor.");
		} catch (IOException e) {
			log("Unable to open cursor image.");
		}
		startTime = System.currentTimeMillis();
		return true;
	}

	@Override
	public int loop() {
		if (!isIdle())
			return random(50, 200);
		else if (bank.isOpen()) {
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

	@Override
	public void onFinish() {

	}

	@SuppressWarnings("unused")
	private void drawMouse(final Graphics g) {
		final Point loc = getMouseLocation();
		if (System.currentTimeMillis()
				- Bot.getClient().getMouse().getMousePressTime() < 500) {
			g.setColor(new Color(0, 0, 0, 50));
			g.fillOval(loc.x - 5, loc.y - 5, 10, 10);
		} else {
			g.setColor(Color.BLACK);
		}
		g.drawLine(0, loc.y, 766, loc.y);
		g.drawLine(loc.x, 0, loc.x, 505);
	}

	public void onRepaint(Graphics render) {

		((Graphics2D) render).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		if (normal != null) {
			final Mouse mouse = Bot.getClient().getMouse();
			final int mouse_x = mouse.getMouseX();
			final int mouse_y = mouse.getMouseY();
			final int mouse_x2 = mouse.getMousePressX();
			final int mouse_y2 = mouse.getMousePressY();
			final long mpt = System.currentTimeMillis()
			- mouse.getMousePressTime();
			if (mouse.getMousePressTime() == -1 || mpt >= 1000) {
				render.drawImage(normal, mouse_x - 8, mouse_y - 8, null);
			}
			if (mpt < 1000) {
				render.drawImage(clicked, mouse_x2 - 8, mouse_y2 - 8, null);
				render.drawImage(normal, mouse_x - 8, mouse_y - 8, null);
			}
		}

		if (isLoggedIn()) {
			render.setColor(new Color(0, 0, 0, 175));
			render.fillRoundRect(1, 231, 125, 370, 10, 10);
			final int percent = skills
			.getPercentToNextLevel(Constants.STAT_PRAYER);
			render.setColor(Color.black);
			render.fillRoundRect(9, 345, 100, 10, 15, 15); // these must be on
			// same coordinates
			render.setColor(Color.blue);
			render.fillRoundRect(9, 345, percent, 10, 15, 15); // these must be
			// on same
			// coordinates
			render.setColor(Color.white);
			render.drawString("" + percent, 50, 355); // this must be on the
			// center of the bar
			render.drawRoundRect(9, 345, 100, 10, 15, 15); // these must be on
			// same coordinates
			render.drawRoundRect(9, 345, percent, 10, 15, 15); // these must be
			// on same
			// coordinates
			if (startexp == 0) {
				startexp = skills.getCurrentSkillExp(Constants.STAT_PRAYER);
			}
			expGained = skills.getCurrentSkillExp(Constants.STAT_PRAYER)
			- startexp;

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
				expsec = ((float) expGained)
				/ (float) (secounds + (minutes * 60) + (hours * 60 * 60));
			}
			exphour = expmin * 60;
			expmin = expsec * 60;
			bonesPerHour = (int) ((3600000.0 / timeRunning) * boned);

			long millis = System.currentTimeMillis() - startTime;
			long hours = millis / (1000 * 60 * 60);
			millis -= hours * (1000 * 60 * 60);
			long minutes = millis / (1000 * 60);
			millis -= minutes * (1000 * 60);
			render.setColor(Color.white);

			render.setFont(new Font("Times", Font.PLAIN, 12));
			render.drawString("BonesPlus ", 19 - 1, 240 - 1);
			render.drawString("Time Running: " + hours + ":" + minutes + ":"
					+ secounds, 9 - 1, 285 - 1);
			render.drawString("XP Gained: " + expGained, 9 - 1, 300 - 1);
			render.drawString("XP Per hour: " + (int) exphour, 9 - 1, 315 - 1);
			render.drawString("Bones Buried: " + boned, 9 - 1, 330 - 1);
			render.drawString("Bones Buried Per hour: "
					+ Integer.toString(bonesPerHour), 9 - 1, 345 - 1);

		}

	}

	public void serverMessageRecieved(ServerMessageEvent e) {
		String serverMessage = e.getMessage();

		if (serverMessage.contains("You bury the bones.")) {
			boned++;
		}

	}
}