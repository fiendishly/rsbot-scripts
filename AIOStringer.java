/**			AIOStringer by Speed
 *
 *	A Speed's Scripting Production...
 *
 *
 *	Support & help can be found at: http://www.rsbot.org/vb/showthread.php?t=134704
 *
 *
 *	Made on 19th September 2009.
 *	Updated to Version 1.28 on 26th Feb 2010.
 *
 *	Thanks to Dave, for letting me use some of his paint. (I disliked the one that I used in the Super Series).
 *	Thanks to Durka Durka Mahn for update system.
 *
 *
 *	Version 1.01 - Changed banking & trading.
 *	Version 1.02 - Updated some imports and fixed some minor bugs.
 *	Version 1.03 - Fixed banking and made it not click on bows while fletching.
 *	Version 1.04 - Faster clicking, and better clicking, no more random antiban when banking :D.
 *	Version 1.05 - Banking improved, no longer spams 14.
 *	Version 1.06 - Updated antibans so it'll only execute 3 antibans per round. More human like banking.
 *	Version 1.10 - Fixed banking after like 20 tries. Also, now will finish properly. Any bank allowed. Paint updated.
 *	Version 1.11 - Updated banking, and re-wrote some methods. Updated description.
 *	Version 1.12 - Added some more banks, now works with soul wars.
 *	Version 1.15 - Changed to use RSBot's withdrawal method, will write my own soon. Changed to use bank booth names instead of booth ID's.
 *	Version 1.16 - Uses Id's again as objectdef is broken
 *	Version 1.19 - Clicking improved, fixed loads of bugs and improved banking. Should work at most banks.
 *	Version 1.20 - Banking fixed, after a very long fight with my own method and rewriting void banking() over 10 times, I have finally done it :).
 *	Version 1.22 - Updated antiban (not only 3 executed, uses random numbers) and now uses RSBot's atInventoryItem (it's a updated version of my atInventoryItem method), also improved banking and fixed a logging out glitch and update description and onFinish.
 *	Version 1.23 - Fixed bug that makes you logout randomly, fixed a bug that causes misclicks.
 *	Version 1.24 - Fixed another bug that would mess up banking and updated antiban. Fixed some random bugs like the misclicks.
 *	Version 1.25 - Doesn't use any deprecated methods any more. Changed stopAllScripts to stopScripts
 *	Version 1.26 - Fixed some misclicks, almost flawless!
 *	Version 1.28 - Made to comply with Java conventions, fixed some bugs, 95% flawless. Made more random.
 */

import java.awt.*;
import java.util.*;
import java.awt.event.KeyEvent;
import javax.swing.*;
import java.io.*;
import java.net.*;

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

@ScriptManifest(authors = { "Speed" }, category = "Fletching", name = "AIO Stringer", version = 1.28, description = "<html>"
		+ "<head></head><body>"
		+ "<font color='red'><h1><center><b>AIO Stringer</b></h1><br></font><font size='4'><center>By Speed<br><br></center></center></h2><br></font>"
		+ "<center><b><font size ='3'>What type of bow would you like to string?:</b><br>"
		+ "<select name='whatFletching'><option>Shortbow</option><option>Longbow</option><option>Oak shortbow</option><option>Oak longbow</option><option>Willow shortbow</option><option>Willow longbow</option><option>Maple shortbow</option><option>Maple longbow</option><option>Yew shortbow</option><option>Yew longbow</option><option>Magic shortbow</option><option>Magic longbow</option></select><br></font>"
		+ "<font size ='4'><b><br> Sponsored By: <a href = http://team-deathmatch.com> http://team-deathmatch.com</a>.<br></b><br><b>Works at almost ANY bank!</b><br><Run in SD and fixed screen<br>Thanks for using AIOStringer by Speed.</center></font></body></html\n")
public class AIOStringer extends Script implements PaintListener,
		ServerMessageListener {
	private int ubowID;
	private int amountFletched;
	private long startTime;
	private int startLevel;
	private int startXp;
	private int fails;
	private int GambleInt;
	private long hours;
	private int amountneeded = 1000;
	private long minutes;
	private int antibans;
	private int expGained;
	private long seconds;
	private long runTime;
	private int antibanInt;
	private int levelsGained;
	private final int bowstring = 1777;
	private final ScriptManifest properties = getClass().getAnnotation(
			ScriptManifest.class);

	private double getVersion() {
		return properties.version();
	}

	public boolean onStart(Map<String, String> args) {
		startXp = skills.getCurrentSkillExp(STAT_FLETCHING);
		startLevel = skills.getCurrentSkillLevel(STAT_FLETCHING);
		amountFletched = 0;
		if (args.get("whatFletching").equals("Shortbow"))// by speed
		{
			ubowID = 50;
		}
		if (args.get("whatFletching").equals("Longbow"))// by speed
		{
			ubowID = 48;
		}
		if (args.get("whatFletching").equals("Oak shortbow")) {
			ubowID = 54;
		}
		if (args.get("whatFletching").equals("Oak longbow")) {
			ubowID = 56;
		}
		if (args.get("whatFletching").equals("Willow shortbow")) {
			ubowID = 60;
		}
		if (args.get("whatFletching").equals("Willow longbow")) {
			ubowID = 58;
		}
		if (args.get("whatFletching").equals("Maple shortbow")) {
			ubowID = 64;
		}
		if (args.get("whatFletching").equals("Maple longbow")) {
			ubowID = 62;
		}
		if (args.get("whatFletching").equals("Yew shortbow")) {
			ubowID = 68;
		}
		if (args.get("whatFletching").equals("Yew longbow")) {
			ubowID = 66;
		}
		if (args.get("whatFletching").equals("Magic shortbow")) {
			ubowID = 72;
		}
		if (args.get("whatFletching").equals("Magic longbow")) {
			ubowID = 70;
		}
		startTime = System.currentTimeMillis();
		log("Thanks for using AIOStringer, you have version "
				+ properties.version() + ".");
		URLConnection url = null;
		BufferedReader in = null;
		BufferedWriter out = null;
		if (JOptionPane
				.showConfirmDialog(
						null,
						"Would you like to check for updates?\nPlease Note this requires an internet connection and the script will write files to your harddrive!") == 0) {
			try {
				url = new URL(
						"http://www.scapemarket.info/scripts/AIOStringerVERSION.txt")
						.openConnection();
				in = new BufferedReader(new InputStreamReader(url
						.getInputStream()));
				if (Double.parseDouble(in.readLine()) > getVersion()) {
					if (JOptionPane.showConfirmDialog(null,
							"Update found. Do you want to update?") == 0) {
						// If so, allow the user to choose the file to be
						// updated.
						JOptionPane
								.showMessageDialog(null,
										"Please choose 'AIOStringer.java' in your scripts folder and hit 'Open'");
						JFileChooser fc = new JFileChooser();
						// Make sure "Open" was clicked.
						if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
							// If so, set up the URL for the .java file and set
							// up the IO.
							url = new URL(
									"http://www.scapemarket.info/scripts/AIOStringer.java")
									.openConnection();
							in = new BufferedReader(new InputStreamReader(url
									.getInputStream()));
							out = new BufferedWriter(new FileWriter(fc
									.getSelectedFile().getPath()));
							String inp;
							/*
							 * Until we reach the end of the file, write the
							 * next line in the file and add a new line. Then
							 * flush the buffer to ensure we lose no data in the
							 * process.
							 */
							while ((inp = in.readLine()) != null) {
								out.write(inp);
								out.newLine();
								out.flush();
							}
							// Notify the user that the script has been updated,
							// and a recompile and reload is needed.
							log("Script successfully downloaded. Please recompile and reload your scripts!");
							return false;
						} else
							log("Update canceled");
					} else
						log("Update canceled");
				} else
					JOptionPane.showMessageDialog(null,
							"You have the latest version. :)"); // User has the
				// latest
				// version. Tell
				// them!
				if (in != null)
					in.close();
				if (out != null)
					out.close();
			} catch (IOException e) {
				log("Problem getting version :/");
				return false; // Return false if there was a problem
			}

		}
		return true;
	}

	public void onFinish() {
		log("Bows Strung: " + amountFletched + ".");
		log("Time Taken: " + hours + ":" + minutes + ":" + seconds + ".");
		log("Thanks for using AIOStringer by Speed.");
	}

	@Override
	protected int getMouseSpeed() {
		return random(4, 6);
	}

	private boolean closeBank() {
		RSInterfaceChild closebutton = RSInterface.getChildInterface(
				INTERFACE_BANK, INTERFACE_BANK_BUTTON_CLOSE);
		if (closebutton.isValid()) {
			atInterface(closebutton);
			return !bank.isOpen();
		}
		return closebutton.isValid();
	}

	private boolean isBusy() {//by Speed.
		boolean flag = false;
		for (int i = 0; i < 4; i++) {
			if (getMyPlayer().getAnimation() != -1) {
				flag = true;
				break;
			}
			wait(random(200, 250));
		}
		return flag;
	}

	private boolean clickInventoryItem(int itemID, boolean click) {
		if (getCurrentTab() != TAB_INVENTORY
				&& !RSInterface.getInterface(INTERFACE_BANK).isValid()
				&& !RSInterface.getInterface(INTERFACE_STORE).isValid()) {
			openTab(TAB_INVENTORY);
		}
		int[] items = getInventoryArray();
		int slot = -1;
		for (int i = 0; i < items.length; i++) {
			if (items[i] == itemID) {
				slot = i;
				break;
			}
		}
		if (slot == -1) {
			return false;
		}
		Point t = randomizePoint(getInventoryItemPoint(slot));
		clickMouse(t, 5, 5, click);
		return true;
	}

	private int getSelectedInvIndex() {
		final RSInterfaceComponent[] components = getInventoryInterface()
				.getComponents();
		for (int i = 0; i < 28; i++) {
			if (components[i].getBorderThickness() == 2)
				return i;
		}
		return -1;
	}

	private int getSelectedInvItem() {
		return getSelectedInvIndex() == -1 ? -1
				: getInventoryArray()[getSelectedInvIndex()];
	}

	private void fletching() {
		final RSInterfaceChild FLETCH_AREA = RSInterface.getChildInterface(513,
				3);
		if (!isBusy() && inventoryContains(ubowID)
				&& inventoryContains(bowstring)) {
			if (!FLETCH_AREA.isValid()) {
				if (getSelectedInvItem() == -1
						|| getSelectedInvItem() == bowstring) {
					if (!FLETCH_AREA.isValid()) {
						atInventoryItem(ubowID, "Use");
						wait(random(500, 700));
					}
				}
				if (getSelectedInvItem() == ubowID) {
					if (!FLETCH_AREA.isValid()) {
						atInventoryItem(bowstring, "Use");
						wait(random(500, 700));
					}
				}
				wait(random(300, 900));
			} else {
				if (!atInterface(FLETCH_AREA, "Make All")
						&& getSelectedInvItem() != -1) {
					atInventoryItem(getSelectedInvItem(), "Use");
				} else {
					wait(random(800, 1200));
				}
			}
		}
	}

	private boolean isEmpty() {
		return getInventoryCount() == 0;
	}

	private boolean isThere(final int item) {
		return getInventoryCount(item) != 0 && getInventoryCount(item) != 0;
	}

	private void banking() {
		if (bank.isOpen()) {
			fails = 0;
			if (!isEmpty()) {
				bank.depositAll();
			}
			if (!isThere(ubowID) && isEmpty()) {
				amountneeded = bank.getCount(ubowID);
				wait(random(1000, 1300));
				if (!isThere(ubowID)) {
					withdraw(ubowID, 14);
				}
			}
			if (!isThere(bowstring) && getInventoryCount(ubowID) == 14) {
				wait(random(1000, 1300));
				if (!isThere(bowstring)) {
					withdraw(bowstring, 14);
				}
			}
			if (getInventoryCount(ubowID) > 14
					|| getInventoryCount(bowstring) > 14) {
				bank.depositAll();
			}
		}
	}

	private Point randomizePoint(final Point p) {
		return new Point(p.x + random(-2, -3), p.y + random(-2, 3));
	}

	public int loop() {
		final RSInterfaceChild FLETCH_ARE = RSInterface.getChildInterface(513,
				3);
		if (amountneeded < 20) {
			log("Finished script, cannot find more than 20 unstrungs in bank.");
			stopScript();
			logout();
		}
		if (!inventoryContains(ubowID) && !inventoryContains(bowstring)
				&& !isBusy() && !bank.isOpen()) {
			bank.open();
		}
		if (bank.isOpen() && !inventoryContains(bowstring)
				&& !inventoryContains(ubowID)) {
			banking();
		}
		if (inventoryContains(ubowID) && inventoryContains(bowstring)
				&& getInterface(Constants.INTERFACE_BANK).isValid()) {
			closeBank();
		}
		if (inventoryContains(ubowID) && inventoryContains(bowstring)
				&& !isBusy() && !bank.isOpen()) {
			fletching();
		}
		if (isBusy()) {
			antiban();
		}
		if (inventoryContains(ubowID) && !inventoryContains(bowstring)
				&& !bank.isOpen() || !bank.isOpen()
				&& inventoryContains(bowstring) && !inventoryContains(ubowID)) {
			bank.open();
		}
		if (inventoryContains(ubowID) && !inventoryContains(bowstring)
				&& bank.isOpen()) {
			withdraw(bowstring, 14);
		}
		if (!inventoryContains(ubowID) && inventoryContains(bowstring)
				&& bank.isOpen()) {
			withdraw(ubowID, 14);
		}
		if (Bot.getClient().isItemSelected() == bowstring
				&& FLETCH_ARE.isValid()
				|| Bot.getClient().isItemSelected() == ubowID
				&& FLETCH_ARE.isValid()) {
			clickInventoryItem(Bot.getClient().isItemSelected(), true);
			wait(random(300, 900));
		}
		return 100;
	}

	private boolean withdraw(int itemID, int amount) {
		String s = amount + "";
		if (bank.isOpen()) {
			if (!bank.atItem(itemID, s)) {
				if (bank.atItem(itemID, "X")) {
					wait(random(500, 800));
					sendText(s, true);
					return true;
				}
				return true;
			} else {
				return true;
			}
		}
		return false;
	}

	public void serverMessageRecieved(final ServerMessageEvent e) {
		final String word = e.getMessage().toLowerCase();
		if (word.contains("string")) {
			amountFletched++;
			amountneeded--;
		}
		if (word.contains("Nothing")) {
			fails++;
		}
	}

	public void onRepaint(Graphics g) { // needs updating, looks nerdy.
		runTime = System.currentTimeMillis() - startTime;
		seconds = runTime / 1000;
		if (seconds >= 60) {
			minutes = seconds / 60;
			seconds -= (minutes * 60);
		}
		if (minutes >= 60) {
			hours = minutes / 60;
			minutes -= (hours * 60);
		}

		expGained = skills.getCurrentSkillExp(STAT_FLETCHING) - startXp;
		g.setColor(new Color(0, 0, 0, 50));
		g.fillRoundRect(3, 180, 155, 130, 5, 5);

		// Calculate levels gained
		levelsGained = skills.getCurrentSkillLevel(STAT_FLETCHING) - startLevel;

		g.setColor(Color.WHITE);
		g.drawString("Run time: " + hours + ":" + minutes + ":" + seconds, 12,
				216);
		g.drawString("Strung " + amountFletched + " bows.", 12, 232);
		g.drawString("XP Gained: " + expGained, 12, 248);
		g.drawString("Levels Gained: " + levelsGained, 12, 264);
		g.drawString("Percent to next level: "
				+ skills.getPercentToNextLevel(STAT_FLETCHING), 12, 280);
		g.drawString("Amount left to fletch: " + amountneeded, 12, 296);

	}

	private void antiban() {
		antibanInt = random(2, 7);
		if (antibans < antibanInt) {
			GambleInt = random(1, 11);
			if (GambleInt == 1) {
				turnCamera();
				antibans++;
			}

			if (GambleInt == 2) {
				final int xA = random(0, 750);
				final int yA = random(0, 500);
				moveMouse(xA, yA);
				turnCamera();
				antibans++;
			}

			if (GambleInt == 3) {
				if (getCurrentTab() != Constants.TAB_INVENTORY) {
					openTab(Constants.TAB_INVENTORY);
					turnCamera();
					antibans++;
				}
			}

			if (GambleInt == 4) {
				clickCharacter(getNearestPlayerByLevel(1, 130), "Cancel");
				wait(random(500, 1750));
				antibans++;
			}

			if (GambleInt == 9) {
				turnCamera();
				openTab(random(0, 13));
				wait(random(1000, 1200));
				turnCamera();
				antibans++;
			}

			if (GambleInt == 10) {
				turnCamera();
				moveMouse(random(0, 450), random(0, 450));
				antibans++;
			}
			if (GambleInt == 5) {
				turnCamera();
				final int xA = random(0, 750);
				final int yA = random(0, 500);
				moveMouse(xA, yA);
				antibans++;
			}

			if (GambleInt == 6) {
				turnCamera();
				antibans++;
			}

			if (GambleInt == 7) {
				openTab(random(0, 13));
				antibans++;
			}

			if (GambleInt == 8) {
				moveMouse(random(0, 450), random(0, 450));
				antibans++;
			}
		}
	}

	// credits to WarXperiment
	private void turnCamera() {
		final char[] LR = new char[] { KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT };
		final char[] UD = new char[] { KeyEvent.VK_UP, KeyEvent.VK_DOWN };
		final char[] LRUD = new char[] { KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT,
				KeyEvent.VK_UP, KeyEvent.VK_DOWN };
		final int random2 = random(0, 2);
		final int random1 = random(0, 2);
		final int random4 = random(0, 4);

		if (random(0, 3) == 0) {
			Bot.getInputManager().pressKey(LR[random1]);
			try {
				wait(random(100, 400));
			} catch (final Exception ignored) {
			}
			Bot.getInputManager().pressKey(UD[random2]);
			try {
				wait(random(300, 600));
			} catch (final Exception ignored) {
			}
			Bot.getInputManager().releaseKey(UD[random2]);
			try {
				wait(random(100, 400));
			} catch (final Exception ignored) {
			}
			Bot.getInputManager().releaseKey(LR[random1]);
		} else {
			Bot.getInputManager().pressKey(LRUD[random4]);
			if (random4 > 1) {
				try {
					wait(random(300, 600));
				} catch (final Exception ignored) {
				}
			} else {
				try {
					wait(random(500, 900));
				} catch (final Exception ignored) {
				}
			}
			Bot.getInputManager().releaseKey(LRUD[random4]);
		}
	}
}