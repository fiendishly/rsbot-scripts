import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URL;
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
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSInterfaceComponent;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;
import org.rsbot.util.ScreenshotUtil;

@ScriptManifest(authors = { "Master T", "Taha" }, category = "Other", name = "Smart Bury", version = 4.3, description = "<html><head><style type=text/css>.style1 {color: #FFFFFF;}.style2 {color: #FFFFFF;font-weight: bold;}</style></head><body bgcolor=black text=white><center><font face=Comic Sans MS color=#ffffff size=18pt>By: Master T &amp; Taha</font><br><br><font color=#ffffff>Click <a href=http://www.rsbot.org/vb/showthread.php?t=39222>here</a> to go to the thread.</font><table border=0><tr><td width=350></p><tr><td class=style2>Bone Bury Method: </td><td width=156 align=left class=style1><select name=order style=width: 205px><option>Bury Bones Randomly</option><option>Bury Bones In Order</option></tr></td><tr><td class=style2>Bone to bury: </td><td width=156 align=left class=style1><select name=bone><option>Normal Bones</option><option>Wolf Bones</option><option>Burnt Bones</option><option>Monkey Bones</option><option>Bat Bones</option><option>Big Bones</option><option>Jogre Bones</option><option>Zogre Bones</option><option>Shaikahan Bones</option><option>Babydragon Bones</option><option>Wyvern Bones</option><option>Dragon Bones</option><option>Fayrg Bones</option><option>Raurg Bones</option><option>Dagannoth Bones</option><option>Ourg Bones</option></select></td></tr><tr><td class=style2>Amount to Bury: </td><td align=right class=style1><input type=text name=amount id=amount VALUE=1000 style=width: 95px></td></tr><tr><td class=style2>Custom Bank ID: </td><td align=right class=style1><input type=text name=bankID id=bankID style=width: 95px></td></tr><tr><td class=style2>Use Custom Bank:</td><td class=style1><INPUT TYPE=checkbox NAME=customBank VALUE=false></td></tr><tr><td class=style2>Turn the computer off when script finishes:</td><td class=style1><INPUT TYPE=checkbox NAME=shutdown VALUE=false></td></tr><tr><td class=style2>Teleport to Lumbridge if bank is not found:</td><td class=style1><INPUT TYPE=checkbox NAME=Tele VALUE=true CHECKED=true></td></tr><tr><td class=style2>Check for Updates:</td><td class=style1><INPUT TYPE=checkbox NAME=update VALUE=true CHECKED=true></td></tr></select></td></tr></table></center></body></html>")
public class SmartBury extends Script implements PaintListener,
		ServerMessageListener {
	private enum Process {
		bury, bank, stop, teleport, walk, noReturn;
	}

	private String State = "Not Yet Started";
	private long StartTime, expGained, buried = 0, xpPerHour;
	private int BoneID, amount, levelsGained, startExp, startLevel,
			BonesUntilLevel, expPerBone, customBankID, teleAttempts;
	private boolean randomBury, Tele, shutDown, useCustomBank, teleNow = true;
	private final RSTile draynorTile = new RSTile(3092, 3243),
			lumbTile = new RSTile(3222, 3218);
	private RSObject bankBooth, bankChest, customBank;
	private RSNPC banker;

	SmartBuryAntiBan antiban;
	Thread t;

	private void deposit() {
		if (getInventoryCountExcept(BoneID) > 0) {
			bank.depositAllExcept(BoneID);
		}
		wait(random(200, 400));
	}

	@Override
	protected int getMouseSpeed() {
		return random(6, 11);
	}

	private Process getProcess() {
		if (buried > amount - 1) {
			return Process.stop;
		} else if (teleNow && !nearBank()) {
			return Process.teleport;
		} else if (!teleNow && !nearBank()) {
			return Process.walk;
		} else if (nearBank()) {
			if (getInventoryCount(BoneID) > 0) {
				return Process.bury;
			} else {
				return Process.bank;
			}
		}
		return Process.noReturn;
	}

	@Override
	@SuppressWarnings("static-access")
	public int loop() {
		getMouseSpeed();
		setCameraAltitude(true);
		if (!t.isAlive()) {
			t.start();
			log("AntiBan initialized!");
		}
		switch (getProcess()) {
		case bury:
			State = "Burying";
			if (RSInterface.getInterface(Constants.INTERFACE_BANK).isValid()) {
				bank.close();
			}
			if (getCurrentTab() != Constants.TAB_INVENTORY) {
				openTab(Constants.TAB_INVENTORY);
			}
			bury(BoneID, randomBury);
			break;

		case bank:
			bankBooth = getNearestObjectByID(bank.BankBooths);
			banker = getNearestNPCByID(bank.Bankers);
			bankChest = getNearestObjectByID(bank.BankChests);
			if (useCustomBank) {
				customBank = getNearestObjectByID(customBankID);
			}
			if ((bankBooth != null && distanceTo(bankBooth) < 8
					&& tileOnMap(bankBooth.getLocation())
					&& canReach(bankBooth, true) || banker != null
					&& distanceTo(banker) < 8
					&& tileOnMap(banker.getLocation())
					&& canReach(banker, true) || bankChest != null
					&& distanceTo(bankChest) < 8
					&& tileOnMap(bankChest.getLocation())
					&& canReach(bankChest, true))
					&& !useCustomBank
					|| useCustomBank
					&& customBank != null
					&& distanceTo(customBank) < 8
					&& tileOnMap(customBank.getLocation())
					&& canReach(customBank, true)) {
				State = "Banking";
				if (!bank.isOpen()) {
					if (getMyPlayer().getAnimation() == -1) {
						if (useCustomBank) {
							bank.atBankBooth(customBank.getLocation(),
									"Use-Quickly");
						} else {
							bank.open();
						}
						wait(random(600, 800));
					}
				} else {
					deposit();
					withdraw();
				}
			} else {
				if (!useCustomBank && bankBooth != null
						&& tileOnMap(bankBooth.getLocation())
						&& canReach(bankBooth, true)
						&& distanceTo(bankBooth) > 8) {
					walkTo(bankBooth.getLocation());
				}
				if (!useCustomBank && banker != null
						&& tileOnMap(banker.getLocation())
						&& canReach(banker, true) && distanceTo(banker) > 8) {
					walkTo(banker.getLocation());
				}
				if (!useCustomBank && bankChest != null
						&& tileOnMap(bankChest.getLocation())
						&& canReach(bankChest, true)
						&& distanceTo(bankChest) > 8) {
					walkTo(bankChest.getLocation());
				}
				if (useCustomBank && customBank != null
						&& tileOnMap(customBank.getLocation())
						&& canReach(customBank, true)
						&& distanceTo(customBank) > 8) {
					walkTo(customBank.getLocation());
				}
			}
			break;

		case teleport:
			if (distanceTo(lumbTile) < 10) {
				teleNow = false;
				return random(200, 400);
			}
			if (Tele && teleAttempts < 20) {
				State = "Teleporting";
				if (getCurrentTab() != Constants.TAB_MAGIC) {
					openTab(Constants.TAB_MAGIC);
				}
				if (getMyPlayer().getAnimation() == -1) {
					atInterface(192, 24);
					wait(random(1500, 2000));
					teleAttempts++;
				} else {
					wait(random(200, 400));
					teleNow = false;
				}
			} else {
				if (teleAttempts >= 20) {
					State = "Closing script";
					log("Exceeded amount of attempts to teleport. Closing script...");
					shutDown();
					stopScript();
				}
				if (!Tele) {
					State = "Closing script";
					log("Bank could not be found. Teleport is disabled. Closing script...");
					shutDown();
					stopScript();
				}
			}
			break;

		case walk:
			if (getMyPlayer().getAnimation() == -1
					&& (distanceTo(getDestination()) < 8 || distanceTo(getDestination()) > 200)) {
				State = "Walking";
				if (distanceTo(draynorTile) > 10) {
					walkPathMM(generateFixedPath(draynorTile));
				} else {
					teleNow = true;
				}
			}
			if (getEnergy() > 30 && random(1, 10) == 1) {
				setRun(true);
			}
			return random(800, 1000);

		case stop:
			State = "Closing script";
			log("Finished amount of bones needed to be buried.");
			shutDown();
			stopScript();
			break;

		case noReturn:
			log("Nothing returned. Waiting 200-400 miliseconds.");
			break;
		}
		return random(200, 400);
	}

	@SuppressWarnings("static-access")
	private boolean nearBank() {
		bankBooth = getNearestObjectByID(bank.BankBooths);
		banker = getNearestNPCByID(bank.Bankers);
		bankChest = getNearestObjectByID(bank.BankChests);
		customBank = getNearestObjectByID(customBankID);
		return bankBooth != null && tileOnMap(bankBooth.getLocation())
				|| banker != null && tileOnMap(banker.getLocation())
				|| bankChest != null && tileOnMap(bankChest.getLocation())
				|| customBank != null && tileOnMap(customBank.getLocation());
	}

	@Override
	public void onFinish() {
		log("Thanks for using the script!");
		log("XP per hour: " + xpPerHour);
		log("Levels Gained: " + levelsGained);
		log("Buried: " + buried + " Bones");
		log("Experience Gained: " + expGained);
		antiban.stopThread = true;
	}

	public void onRepaint(final Graphics gr) {
		final Graphics2D g2 = (Graphics2D) gr;
		if (isLoggedIn()) {
			final long RunTime = System.currentTimeMillis() - StartTime;
			final long TotalSecs = RunTime / 1000;
			final long TotalMins = TotalSecs / 60;
			final long TotalHours = TotalMins / 60;
			final int seconds = (int) TotalSecs % 60;
			final int minutes = (int) TotalMins % 60;
			final int hours = (int) TotalHours % 60;

			final StringBuilder b = new StringBuilder();
			if (hours < 10) {
				b.append('0');
			}
			b.append(hours);
			b.append(':');
			if (minutes < 10) {
				b.append('0');
			}
			b.append(minutes);
			b.append(':');
			if (seconds < 10) {
				b.append('0');
			}
			b.append(seconds);

			if (startExp == 0) {
				startExp = skills.getCurrentSkillExp(Constants.STAT_PRAYER);
			}

			if (startLevel == 0) {
				startLevel = skills.getCurrentSkillLevel(Constants.STAT_PRAYER);
			}

			expGained = skills.getCurrentSkillExp(Constants.STAT_PRAYER)
					- startExp;
			levelsGained = skills.getCurrentSkillLevel(Constants.STAT_PRAYER)
					- startLevel;

			if (expPerBone == 0) {
				expPerBone = skills.getCurrentSkillExp(Constants.STAT_PRAYER)
						- startExp;
			}

			if (expPerBone != 0) {
				BonesUntilLevel = skills
						.getXPToNextLevel(Constants.STAT_PRAYER)
						/ expPerBone;
			}
			final int LvlPct = skills
					.getPercentToNextLevel(Constants.STAT_PRAYER);

			if (expGained != 0) {
				xpPerHour = expGained * 3600 / (RunTime / 1000);
			}

			final String[] strings = new String[] {
					getClass().getAnnotation(ScriptManifest.class).name()
							+ " v "
							+ getClass().getAnnotation(ScriptManifest.class)
									.version(),
					"Experience Gained: " + expGained,
					"Levels Gained: " + levelsGained,
					"XP per hour: " + xpPerHour,
					"Bones Until Level Up: " + BonesUntilLevel,
					"Buried: " + buried + " Bones", "Time Running: " + b,
					"Status: " + State };

			String longestString = "";

			for (int i = 0; i < strings.length; i++) {
				if (strings[i].length() > longestString.length()) {
					longestString = strings[i];
				}
			}
			final int textWidth = (int) gr.getFontMetrics(gr.getFont())
					.getStringBounds(longestString, gr).getWidth() + 10;

			int x = 9;
			int y = 193;

			gr.setFont(new Font("Trebuchet MS", Font.PLAIN, 12));

			final GradientPaint gp = new GradientPaint(x, y, new Color(1, 1,
					240, 255), textWidth + 8, 330,
					new Color(142, 142, 245, 255)); // gradient dark blue to
			// light
			// blue
			g2.setPaint(gp); // setting gradient as paint color
			gr.fillRect(x, y, textWidth + 8, 135); // background box
			gr.setColor(Color.WHITE);
			gr.fillRect(x + 5, y + 10, 4, 100); // background for progress bar
			gr.setColor(Color.BLACK);
			gr.fillRect(x + 5, y + 10, 4, LvlPct); // part of progress bar that
			// should move
			gr.setColor(Color.WHITE);

			x += 15; // This will indent the text. We don't want the text to be
			// too much to the left of the progress report.
			gr.drawString(strings[0], x, y += 16);
			gr.drawString(strings[1], x, y += 16);
			gr.drawString(strings[2], x, y += 16);
			gr.drawString(strings[3], x, y += 16);
			gr.drawString(strings[4], x, y += 16);
			gr.drawString(strings[5], x, y += 16);
			gr.drawString(strings[6], x, y += 16);
			gr.setColor(Color.BLACK);
			gr.drawString(strings[7], x, y += 16);

		}
	}

	@Override
	public boolean onStart(final Map<String, String> args) {
		if (args.get("bone").equals("Normal Bones")) {
			BoneID = 526;
		}
		if (args.get("bone").equals("Wolf Bones")) {
			BoneID = 2859;
		}
		if (args.get("bone").equals("Burnt Bones")) {
			BoneID = 528;
		}
		if (args.get("bone").equals("Monkey Bones")) {
			BoneID = 3183;
		}
		if (args.get("bone").equals("Bat Bones")) {
			BoneID = 530;
		}
		if (args.get("bone").equals("Big Bones")) {
			BoneID = 532;
		}
		if (args.get("bone").equals("Jogre Bones")) {
			BoneID = 3125;
		}
		if (args.get("bone").equals("Zogre Bones")) {
			BoneID = 4812;
		}
		if (args.get("bone").equals("Shaikahan Bones")) {
			BoneID = 3123;
		}
		if (args.get("bone").equals("Babydragon Bones")) {
			BoneID = 534;
		}
		if (args.get("bone").equals("Wvyern Bones")) {
			BoneID = 6812;
		}
		if (args.get("bone").equals("Dragon Bones")) {
			BoneID = 536;
		}
		if (args.get("bone").equals("Fayrg Bones")) {
			BoneID = 4830;
		}
		if (args.get("bone").equals("Raurg Bones")) {
			BoneID = 4832;
		}
		if (args.get("bone").equals("Dagannoth Bones")) {
			BoneID = 6729;
		}
		if (args.get("bone").equals("Ourg Bones")) {
			BoneID = 4834;
		}
		if (args.get("order").equals("Bury Bones Randomly")) {
			randomBury = true;
		}
		if (args.get("customBank") != null) {
			useCustomBank = true;
			customBankID = Integer.valueOf(args.get("bankID"));
		}
		if (args.get("Tele") != null) {
			Tele = true;
		}
		if (args.get("shutdown") != null) {
			shutDown = true;
		}
		amount = Integer.parseInt(args.get("amount"));
		try {
			new URL("http://www.ipcounter.de/count_js.php?u=62709455")
					.openStream();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		antiban = new SmartBuryAntiBan();
		t = new Thread(antiban);
		StartTime = System.currentTimeMillis();
		return true;
	}

	private boolean bury(final int itemID, final boolean randomBury) {
		final RSInterfaceChild inventory = getInventoryInterface();
		if (inventory == null || inventory.getComponents() == null) {
			return false;
		}
		final java.util.List<RSInterfaceComponent> possible = new ArrayList<RSInterfaceComponent>();
		for (final RSInterfaceComponent item : inventory.getComponents()) {
			if (item != null && item.getComponentID() == itemID) {
				possible.add(item);
			}
		}
		if (possible.size() == 0) {
			return false;
		}
		return randomBury ? atInterface(possible
				.get(random(0, possible.size())), "Bury") : atInterface(
				possible.get(0), "Bury");
	}

	public void serverMessageRecieved(final ServerMessageEvent m) {
		if (m.getMessage().contains("bury")) {
			buried++;
		}
		if (m.getMessage().contains("You've just advanced")) {
			ScreenshotUtil.takeScreenshot(isLoggedIn());
		}
	}

	private void shutDown() {
		if (shutDown) {
			try {
				Runtime
						.getRuntime()
						.exec(
								"shutdown -s -t 120 -c \"SmartBury automatic shutdown has initiliazed. To stop the shutdown, hold start and press R, then type in 'shutdown -a' and press OK.\"");
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void withdraw() {
		if (bank.getCount(BoneID) == 0) {
			log("Could not find any bones in the bank.");
			State = "Closing script";
			bank.close();
			shutDown();
			stopScript();
		}
		bank.withdraw(BoneID, 0);
		wait(random(800, 1000));
		if (getInventoryCount(BoneID) > 0) {
			bank.close();
		}
	}

	private class SmartBuryAntiBan implements Runnable {
		public boolean stopThread;

		public void run() {
			while (!stopThread) {
				try {
					if (random(0, 15) == 0) {
						final char[] LR = new char[] { KeyEvent.VK_LEFT,
								KeyEvent.VK_RIGHT };
						final char[] UD = new char[] { KeyEvent.VK_DOWN,
								KeyEvent.VK_UP };
						final char[] LRUD = new char[] { KeyEvent.VK_LEFT,
								KeyEvent.VK_RIGHT, KeyEvent.VK_UP,
								KeyEvent.VK_UP };
						final int random2 = random(0, 2);
						final int random1 = random(0, 2);
						final int random4 = random(0, 4);

						if (random(0, 3) == 0) {
							Bot.getInputManager().pressKey(LR[random1]);
							Thread.sleep(random(100, 400));
							Bot.getInputManager().pressKey(UD[random2]);
							Thread.sleep(random(300, 600));
							Bot.getInputManager().releaseKey(UD[random2]);
							Thread.sleep(random(100, 400));
							Bot.getInputManager().releaseKey(LR[random1]);
						} else {
							Bot.getInputManager().pressKey(LRUD[random4]);
							if (random4 > 1) {
								Thread.sleep(random(300, 600));
							} else {
								Thread.sleep(random(500, 900));
							}
							Bot.getInputManager().releaseKey(LRUD[random4]);
						}
					} else {
						Thread.sleep(random(200, 2000));
					}
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}