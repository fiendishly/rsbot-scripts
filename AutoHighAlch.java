import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Map;

import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSItem;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = { "Jacmob" }, category = "Magic", name = "AutoHighAlch", version = 1.64, description = "<html>\n<head></head>\n<body style='font-family: Arial'>\n<center><h2><span style='color: #AA0000'>High</span> <span style='color: #FF9933'>Level</span> <span style='color: #FFDD00'>Alchemy</span></h2></center>\n<p\n<b>Author:</b> Jacmob<p>\n<b>Version:</b> 1.63\n<p>For more information about this script <a href= 'http://www.rsbot.org/vb/showthread.php?t=19534'>go to the forum thread</a>.<p>\n<b>Alch Item IDs/Zanaris Mode:</b><p><input type='text' name='alchItem' value='856' size=20 /><select name='paint'><option>Large Paint</option><option>Small Paint</option><option>No Paint</option></select><br/>&nbsp;<strong><small>Default is Noted Yew Longbow. Separate multiple IDs with commas.</small></strong></p></body>\n</html>")
public class AutoHighAlch extends Script implements PaintListener,
		ServerMessageListener {

	private final int alchCastXP = 65;

	private int[] alchItem;
	private int alchsCast = 0;
	private final Color BG = new Color(0, 0, 0, 75);
	private long checkTime = 0;
	private final int natureRunes = 561;
	private int paintType = 0;
	private Color PERCBAR = new Color(0, 255, 0, 100);
	private final int[] portals = { 15482, 15480, 15478, 15477 };
	private final Color RED = new Color(75, 0, 0, 75);
	private int startLevel = 0;

	private final long startTime = System.currentTimeMillis();
	private int startXP = 0;

	private boolean atTile3(final RSTile tile, final String action) {
		return atTile3(tile, action, 0);
	}

	private boolean atTile3(final RSTile tile, final String action,
			final int yOffset) {
		try {
			final Point location = Calculations.tileToScreen(tile);
			if (location.x == -1 || location.y == -1) {
				return false;
			}
			moveMouse(location.x, location.y + yOffset, 10, 10);
			wait(random(100, 150));
			getMenuItems();
			final ArrayList<String> mis = getMenuItems();
			if (mis.get(0).contains(action)) {
				clickMouse(true);
			} else {
				for (int i = 1; i < mis.size(); i++) {
					if (mis.get(i).contains(action)) {
						clickMouse(false);
						wait(random(20, 80));
						if (atMenu(action)) {
							return true;
						}
					}
				}
				return false;
			}
			wait(random(100, 300));
			while (getMyPlayer().isMoving()) {
				wait(random(50, 100));
			}
			return true;
		} catch (final Exception e) {
			return false;
		}
	}

	private void bottomPaint(final Graphics g) {
		long millis = System.currentTimeMillis() - startTime;
		final long hours = millis / (1000 * 60 * 60);
		millis -= hours * 1000 * 60 * 60;
		final long minutes = millis / (1000 * 60);
		millis -= minutes * 1000 * 60;
		final long seconds = millis / 1000;

		if (startLevel == 0) {
			startLevel = skills.getCurrentSkillLevel(Constants.STAT_MAGIC);
			startXP = skills.getCurrentSkillExp(Constants.STAT_MAGIC);
		}

		alchsCast = (skills.getCurrentSkillExp(Constants.STAT_MAGIC) - startXP)
				/ alchCastXP;

		g.setColor(BG);
		g.fill3DRect(3, 250, 514, 88, true);

		if (millis != 0) {
			g.setColor(RED);
			g.fill3DRect(4, 235, 513, 15, true);
			g.setColor(PERCBAR);
			g.fill3DRect(4, 236, skills
					.getPercentToNextLevel(Constants.STAT_MAGIC) * 513 / 100,
					13, true);
		}

		g.setColor(Color.WHITE);

		g.setFont(new Font("SansSerif", 1, 12));
		g.drawString("Alched: " + alchsCast + " items", 9, 270);

		g.drawString("Next Level: "
				+ (int) Math.ceil((float) skills
						.getXPToNextLevel(Constants.STAT_MAGIC)
						/ (float) alchCastXP) + " casts", 9, 290);

		g.drawString("Current Magic Level: "
				+ skills.getCurrentSkillLevel(Constants.STAT_MAGIC), 9, 310);

		if (skills.getCurrentSkillLevel(Constants.STAT_MAGIC) - startLevel == 1) {
			g
					.drawString(
							"Gained: "
									+ alchsCast
									* 65
									+ " experience ("
									+ (skills
											.getCurrentSkillLevel(Constants.STAT_MAGIC) - startLevel)
									+ " level" + ")", 9, 330);
		} else {
			g
					.drawString(
							"Gained: "
									+ alchsCast
									* 65
									+ " experience ("
									+ (skills
											.getCurrentSkillLevel(Constants.STAT_MAGIC) - startLevel)
									+ " levels" + ")", 9, 330);
		}

		g.setColor(Color.CYAN);

		g.drawString("Runtime: " + hours + " hrs " + minutes + " min "
				+ seconds + " sec", 325, 270);
	}

	public void castAlchSpell() {

		final int GambleInt = random(1, 200);

		if (GambleInt == 7) {
			moveMouse(520 + random(0, 230), 130 + random(0, 100));
			wait(random(100, 800));
		}
		if (GambleInt <= 3) {
			openTab(Constants.TAB_MAGIC);
			castSpell(Constants.SPELL_HIGH_LEVEL_ALCHEMY);
		} else if (GambleInt == 4) {
			moveCamera();
			openTab(Constants.TAB_MAGIC);
			wait(random(100, 200));
			castSpell(Constants.SPELL_HIGH_LEVEL_ALCHEMY);
		} else if (GambleInt == 5) {
			openTab(Constants.TAB_MAGIC);
			wait(random(50, 150));
			moveCamera();
			wait(random(50, 150));
			castSpell(Constants.SPELL_HIGH_LEVEL_ALCHEMY);
		} else if (GambleInt == 6 && getCurrentTab() != Constants.TAB_STATS
				&& System.currentTimeMillis() > checkTime) {
			openTab(Constants.TAB_STATS);
			openTab(Constants.TAB_STATS);
			wait(random(1, 20));
			moveMouse(567 + random(0, 25), 393 + random(0, 14));
			wait(random(1100, 1800));
			openTab(Constants.TAB_MAGIC);
			castSpell(Constants.SPELL_HIGH_LEVEL_ALCHEMY);
			checkTime = System.currentTimeMillis() + random(30000, 120000);
		} else {
			openTab(Constants.TAB_MAGIC);
			wait(random(150, 300));
			castSpell(Constants.SPELL_HIGH_LEVEL_ALCHEMY);
		}

		wait(random(20, 85));

	}

	public void doAlch() {
		castAlchSpell();
		final Point alchItemPos = getAlchItemPos();
		if (alchItemPos == null) {
			log("No Alchemy Items in Inventory - Shutting Down in 5 Seconds");
			moveMouse(random(50, 500), random(50, 300));
			wait(random(4500, 5500));
			stopScript();
			return;
		} else if (getInventoryCount(natureRunes) == 0) {
			log("You Appear To Have No Nature Runes - Waiting 10 Seconds");
			wait(random(9500, 10500));
			if (getInventoryCount(natureRunes) == 0) {
				log("No Nature Runes in Inventory - Shutting Down");
				stopScript();
			}
			return;
		}
		clickMouse(alchItemPos, true);
	}

	public Point getAlchItemPos() {
		final RSItem[] iItems = getInventoryItems();
		for (final int element : alchItem) {
			final Point nip = getNearInvItemPoint(element);
			if (nip != null) {
				return nip;
			}
			for (int j = 27; j >= 0; j--) {
				if (iItems[j].getID() == element) {
					final Point aip = getInventoryItemPoint(j);
					return new Point(aip.x + random(10, 15), aip.y
							+ random(10, 15));
				}
			}
		}
		return null;
	}

	private String getFormattedTime(final long timeMillis) {
		long millis = timeMillis;
		final long seconds2 = millis / 1000;
		final long hours = millis / (1000 * 60 * 60);
		millis -= hours * 1000 * 60 * 60;
		final long minutes = millis / (1000 * 60);
		millis -= minutes * 1000 * 60;
		final long seconds = millis / 1000;
		String hoursString = "";
		String minutesString = "";
		String secondsString = seconds + "";
		String type = "seconds";

		if (minutes > 0) {
			minutesString = minutes + ":";
			type = "minutes";
		} else if (hours > 0 && seconds2 > 0) {
			minutesString = "0:";
		}
		if (hours > 0) {
			hoursString = hours + ":";
			type = "hours";
		}
		if (minutes < 10 && type != "seconds") {
			minutesString = "0" + minutesString;
		}
		if (hours < 10 && type == "hours") {
			hoursString = "0" + hoursString;
		}
		if (seconds < 10 && type != "seconds") {
			secondsString = "0" + secondsString;
		}

		return hoursString + minutesString + secondsString + " " + type;
	}

	@Override
	protected int getMouseSpeed() {
		return random(8, 11);
	}

	private Point getNearInvItemPoint(final int itemID) {
		final RSItem[] iItems = getInventoryItems();

		// SPELL_HIGH_LEVEL_ALCHEMY
		final int spellX = RSInterface.getInterface(192).getChild(34)
				.getAbsoluteX()
				+ random(7, 16);
		final int spellY = RSInterface.getInterface(192).getChild(34)
				.getAbsoluteY()
				+ random(7, 16);

		final int col = (int) Math.floor((spellX - 559) / 42);
		final int row = (int) Math.floor((spellY - 210) / 36);

		if (col >= 0 && col <= 3 && row >= 0 && row <= 6) {
			if (iItems[(row * 4 + col)].getID() == itemID) {
				return randomiseInventoryItemPoint(getInventoryItemPoint((row * 4 + col)));
			}
		}

		return null;
	}

	private void logInfo() {
		long millis = System.currentTimeMillis() - startTime;
		final long hours = millis / (1000 * 60 * 60);
		millis -= hours * 1000 * 60 * 60;
		final long minutes = millis / (1000 * 60);
		millis -= minutes * 1000 * 60;
		final long seconds = millis / 1000;

		log(skills.getCurrentSkillExp(Constants.STAT_MAGIC) - startXP
				+ " Magic XP Gained in " + hours + " hrs " + minutes + " min "
				+ seconds + " sec (" + alchsCast + " Alchs Cast)");
	}

	@Override
	public int loop() {

		if (RSInterface.getInterface(399).isValid() || !isLoggedIn()
				|| skills.getRealSkillLevel(Constants.STAT_MAGIC) <= 1) {
			return random(100, 110);
		}

		if (distanceTo(new RSTile(2953, 3224)) < 3
				|| distanceTo(new RSTile(2544, 3096)) < 3
				|| distanceTo(new RSTile(2670, 3631)) < 3
				|| distanceTo(new RSTile(2893, 3465)) < 3) {
			final RSObject portal = getNearestObjectByID(portals);
			if (RSInterface.getInterface(232).isValid()) {
				atInterface(232, 3);
				return random(900, 1000);
			} else {
				atTile3(portal.getLocation(), "Enter");
			}
		} else {
			doAlch();
		}
		wait(random(800, 950));
		return 0;
	}

	private void miniPaint(final Graphics g) {
		final int x = 312;
		int y = 4;

		if (startLevel == 0) {
			startLevel = skills.getCurrentSkillLevel(Constants.STAT_MAGIC);
			startXP = skills.getCurrentSkillExp(Constants.STAT_MAGIC);
		}

		final int levelsGained = skills.getRealSkillLevel(Constants.STAT_MAGIC)
				- skills.getLvlByExp(startXP);
		final long runSeconds = (System.currentTimeMillis() - startTime) / 1000;

		alchsCast = (skills.getCurrentSkillExp(Constants.STAT_MAGIC) - startXP)
				/ alchCastXP;

		g.setColor(BG);
		if (runSeconds != 0) {
			g.fill3DRect(x - 6, y, 211, 146, true);
		} else {
			g.fill3DRect(x - 6, y, 211, 105, true);
		}

		g.setColor(Color.WHITE);
		g.setFont(new Font("SansSerif", 1, 12));

		g.drawString(getClass().getAnnotation(ScriptManifest.class).name()
				+ " v"
				+ getClass().getAnnotation(ScriptManifest.class).version(), x,
				y += 17);
		g.drawString(getClass().getAnnotation(ScriptManifest.class).name()
				+ " v"
				+ getClass().getAnnotation(ScriptManifest.class).version(), x,
				y);
		g.drawString("Run Time: "
				+ getFormattedTime(System.currentTimeMillis() - startTime), x,
				y += 20);

		if (levelsGained < 0) {
			startXP = skills.getCurrentSkillExp(Constants.STAT_MAGIC);
		} else if (levelsGained == 1) {
			g
					.drawString(
							"Gained: "
									+ (skills
											.getCurrentSkillExp(Constants.STAT_MAGIC) - startXP)
									+ " XP (" + levelsGained + " lvl)", x,
							y += 20);
		} else {
			g
					.drawString(
							"Gained: "
									+ (skills
											.getCurrentSkillExp(Constants.STAT_MAGIC) - startXP)
									+ " XP (" + levelsGained + " lvls)", x,
							y += 20);
		}

		/*
		 * if (runSeconds > 0) { g.drawString("Averaging: " +
		 * ((skills.getCurrentSkillExp(STAT_MAGIC)-startXP) * 3600 / runSeconds)
		 * + " XP/hr", x, y += 20); }
		 */
		g.drawString("Alched: " + alchsCast + " items", x, y += 20);

		g.drawString("Current level: "
				+ skills.getRealSkillLevel(Constants.STAT_MAGIC), x, y += 20);
		g.drawString("Next level: "
				+ (int) Math.ceil((float) skills
						.getXPToNextLevel(Constants.STAT_MAGIC)
						/ (float) alchCastXP) + " casts", x, y += 20);
		if (runSeconds != 0) {
			g.setColor(RED);
			g.fill3DRect(x, y += 9, 200, 13, true);
			PERCBAR = new Color((int) (255 - Math.floor(2.55 * skills
					.getPercentToNextLevel(Constants.STAT_MAGIC))), 255, 0, 100);
			g.setColor(PERCBAR);
			g.fill3DRect(x, y, skills
					.getPercentToNextLevel(Constants.STAT_MAGIC) * 2, 13, true);
		}
	}

	public void moveCamera() {
		final int r = random(0, 359);
		setCameraRotation(r);
	}

	@Override
	public void onFinish() {
		logInfo();
	}

	public void onRepaint(final Graphics g) {
		if (isLoggedIn()) {
			if (paintType == 1) {
				miniPaint(g);
			} else if (paintType == 0) {
				bottomPaint(g);
			}
		}
	}

	@Override
	public boolean onStart(final Map<String, String> args) {
		if (args.get("alchItem").equals("")) {
			alchItem = new int[] { 856 };
		} else {
			final String text = args.get("alchItem").replaceAll(" ", "");
			try {
				final String[] strInts = text.split(",");
				alchItem = new int[strInts.length];
				for (int i = 0; i < alchItem.length; i++) {
					alchItem[i] = Integer.parseInt(strInts[i]);
				}
			} catch (final Exception e) {
				System.out.print("AlchItems Input Parse Error: " + e);
			}
		}

		if (args.get("paint").equals("Small Paint")) {
			paintType = 1;
		} else if (args.get("paint").equals("No Paint")) {
			paintType = 2;
		}

		if (alchItem.length > 1) {
			log("Alching " + alchItem.length + " Item IDs");
		} else if (alchItem[0] == 856) {
			log("Alching Noted Yew Longbows");
		} else {
			log("Alching Item ID " + alchItem[0]);
		}

		openTab(Constants.TAB_MAGIC);
		wait(random(400, 500));

		return true;
	}

	public void serverMessageRecieved(final ServerMessageEvent e) {
		final String messageEvent = e.getMessage();
		if (messageEvent.contains("You don't have enough coins")) {
			log("You Do Not Have Enough Coins - Shutting Down");
			stopScript();
		} else if (messageEvent.contains("There is no player stock")) {
		} else if (messageEvent.contains("You do not have enough Fire Runes")) {
			log("No Fire Runes - Shutting Down in 10 Seconds");
			wait(random(9000, 10000));
			stopScript();
		} else if (messageEvent.contains("You do not have enough Nature Runes")) {
			log("No Nature Runes - Shutting Down in 10 Seconds");
			wait(random(9000, 10000));
			stopScript();
		}
	}
}
