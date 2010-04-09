/**			SMonkFisher
 *
 *	A Speed's Scripting Production...
 *
 *
 *	Support & help can be found at: http://www.rsbot.org/vb/showthread.php?t=150042
 *
 *
 *	Made on 12th October 2009.
 *	Updated to Version 1.05 on 22nd November 2009.
 *
 *	Thanks to Durka Durka Mahn for update system.
 *	Thanks to Gnarly for base of script. - This is a continuation of his script.
 *
 *	Version 1.00 - Script updated to work with latest RSBot and released.
 *	Version 1.01 - Changed mouse speed to make it slower.
 *	Version 1.03 - Made the script actually walk inside bank, changed some antiban.
 *	Version 1.04 - Changed description and updated antiban.
 *	Version 1.05 - Version fix and stopAllScripts() is now stopScript() and walking methods redone. (This update is by whoever updated all the scripts for these reasons).
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.rsbot.bot.Bot;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Constants;
import org.rsbot.script.GEItemInfo;
import org.rsbot.script.Methods;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = { "Speed" }, category = "Fishing", name = "SMonkFisher", version = 1.04, description = "<html><head>SMonkFisher v1.04</head><h1>SMonkFisher v1.04</h1><br><br><p>By Speed (continuation of <a href =http://www.rsbot.org/vb/member.php?u=948>Gnarly's</a> Script)</p><br><body><p>Run in the fishing colony bank or at the spots<p><p>Support and help can be found here: http://www.rsbot.org/vb/showthread.php?t=150042 <br><b>This script is sponsored by <a>http://www.scapemarket.info</a> - free advertisement for your RuneScape sales.</b></p></body></html>")
public class SMonkFisher extends Script implements PaintListener {

	private static final DecimalFormat format = new DecimalFormat("00");

	private static final int ARNOLD_LYDSPOR = 3824;
	private static final int FISHING_ANIMATION = 621;
	private static final int FISHING_SPOT = 3848;
	private static final int RAW_MONKFISH = 7944;
	private static final int SMALL_FISHING_NET = 303;

	private RSTile currentFishingTile;
	private RSNPC currentFishingTileNPC;

	private int randomRun;
	private int rawMonkfishPrice;
	private boolean scriptIsRunning;
	private boolean firstTick;

	private long startingFishingExp;
	private long startingFishingLevel;

	private long startTime;
	private ThreadPoolExecutor threadPool;
	final ScriptManifest properties = getClass().getAnnotation(
			ScriptManifest.class);

	public double getVersion() {
		return properties.version();
	}

	private boolean clickOnNPC(final RSNPC npc, final String action) {
		if (npc == null) {
			return false;
		}
		final RSTile tile = npc.getLocation();
		if (!tile.isValid()) {
			return false;
		}
		try {
			while (getMyPlayer().isMoving()) {
				wait(1000);
			}
			Point screenLoc = npc.getScreenLocation();
			if (distanceTo(tile) > 6 || !pointOnScreen(screenLoc)) {
				turnToTile(tile, 30);
				if (random(0, 5) == 0) {
					threadPool.execute(new Runnable() {
						public void run() {
							setCameraAltitude(false);
						}
					});
				}
			}
			if (!pointOnScreen(screenLoc)
					&& getMyPlayer().getInteracting() == null) {
				walkTile(randomizeTile(tile, 3, 3));
				return pointOnScreen(screenLoc);
			}
			for (int i = 0; i < 12; i++) {
				screenLoc = npc.getScreenLocation();
				if (!npc.isValid() || !pointOnScreen(screenLoc)) {
					return false;
				}
				moveMouse(screenLoc, 5, 5);
				if (getMenuItems().get(0).toLowerCase().contains(
						npc.getName().toLowerCase())) {
					break;
				}
				if (getMouseLocation().equals(screenLoc)) {
					break;
				}
			}
			final List<String> menuItems = getMenuItems();
			if (menuItems.isEmpty()) {
				return false;
			}
			for (final String menuItem : menuItems) {
				if (menuItem.toLowerCase()
						.contains(npc.getName().toLowerCase())) {
					if (menuItems.get(0).toLowerCase().contains(
							action.toLowerCase())) {
						clickMouse(true);
						return true;
					} else {
						clickMouse(false);
						return atMenu(action);
					}
				}
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private void executeAntiban(final int type) {
		threadPool.execute(new Runnable() {
			public void run() {
				switch (type) {
				case 0: // Changes camera rotation depending on current rotation
					setCameraRotation((int) (getCameraAngle() + (Math.random() * 50 > 25 ? 1
							: -1)
							* (30 + Math.random() * 90)));
					break;
				case 1: // Moves mouse location depending on current mouse
					// location
					moveMouseSlightly();
					break;
				case 2: // Opens up the inventory if not already open
					openTab(Constants.TAB_INVENTORY);
					break;
				case 3: // Checks a random stat, needs to be changed to fishing.
					openTab(Constants.TAB_STATS);
					if (random(0, 3) == 0) {
						moveMouse(random(0, 450), random(0, 450));
					}
					break;
				case 4: // Checks friends list
					openTab(random(0, 13));
					if (random(0, 1) == 0) {
						final int xAzx = random(0, 750);
						final int yAzy = random(0, 500);
						moveMouse(xAzx, yAzy);
					}
					break;
				case 5:
					break;
				case 6: // Randomly moves the mouse
					final int xA = random(0, 750);
					final int yA = random(0, 500);
					moveMouse(xA, yA);
					break;
				case 7: // Rotate screen a bit
					turnCamera();
					break;
				case 8: // Randomly moves the mouse then rotates screen
					final int xAx = random(0, 750);
					final int yAy = random(0, 500);
					moveMouse(xAx, yAy);
					turnCamera();
					break;
				default:
					break;
				}
			}
		});
	}

	public void turnCamera() {
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

	private void findFishingSpot() {
		final RSNPC fishingSpot = getNearestNPCByID(SMonkFisher.FISHING_SPOT);
		if (!(fishingSpot == null)) {
			if (!isFishing() || fishingSpotMoved()) {
				clickOnNPC(fishingSpot, "net");
				currentFishingTile = fishingSpot.getLocation();
				currentFishingTileNPC = fishingSpot;
			}
		} else {
			walkTile(randomizeTile(new RSTile(2334, 3697), 5, 5));
		}
	}

	private boolean fishingSpotMoved() {
		return currentFishingTile == null
				|| currentFishingTileNPC == null
				|| !currentFishingTileNPC.getLocation().equals(
						currentFishingTile);
	}

	private RSTile generateTilePath(final RSTile tile) {
		if (tileOnMap(tile)) {
			return tile;
		}
		final RSTile newTile = new RSTile(
				(getMyPlayer().getLocation().getX() + tile.getX()) / 2,
				(getMyPlayer().getLocation().getY() + tile.getY()) / 2);
		return tileOnMap(newTile) ? newTile : generateTilePath(newTile);
	}

	@Override
	public int getMouseSpeed() {
		return random(4, 6);
	}

	private boolean isFishing() {
		return getMyPlayer().getAnimation() == SMonkFisher.FISHING_ANIMATION;
	}

	@Override
	public int loop() {
		if (scriptIsRunning && isLoggedIn() && firstTick) {
			startingFishingExp = skills
					.getCurrentSkillExp(Constants.STAT_FISHING);
			startingFishingLevel = skills
					.getCurrentSkillLevel(Constants.STAT_FISHING);
			firstTick = false;
		}
		if (getEnergy() > randomRun && !isRunning()) {
			setRun(true);
			randomRun = random(50, 100);
		}
		if (isFishing()) {
			if (random(0, 10) == 0) {
				executeAntiban(1);
				if (random(0, 2) == 0) {
					executeAntiban(0);
				}
			} else if (random(0, 15) == 0) {
				executeAntiban(2);
			} else if (random(0, 30) == 0) {
				executeAntiban(3);
			} else if (random(0, 30) == 0) {
				executeAntiban(4);
			}
		}
		if (bank.getInterface().isValid()) {
			bank.depositAllExcept(SMonkFisher.SMALL_FISHING_NET);
			if (random(0, 3) == 0) {
				walkTile(randomizeTile(new RSTile(2334, 3697), 5, 5));
			} else {
				bank.close();
			}
		} else {
			if (isInventoryFull()) {
				final RSNPC arnold = getNearestNPCByID(SMonkFisher.ARNOLD_LYDSPOR);
				if (arnold != null && this.distanceTo(arnold) > 2) {
					clickOnNPC(arnold, "bank");
				} else {
					walkTile(new RSTile(2330, 3689));
				}
			} else {
				if (fishingSpotMoved()) {
					findFishingSpot();
				} else {
					findFishingSpot();
				}
				wait(random(2000, 2500));
			}
		}
		return random(500, 800);
	}

	@Override
	public void moveMouseSlightly() {
		final int moveX = (int) (getMouseLocation().getX() + (Math.random() * 50 > 25 ? 1
				: -1)
				* (30 + Math.random() * 90));
		final int moveY = (int) (getMouseLocation().getY() + (Math.random() * 50 > 25 ? 1
				: -1)
				* (30 + Math.random() * 90));
		final Point p = new Point(moveX, moveY);
		if (p.getX() < 1 || p.getY() < 1 || p.getX() > 761 || p.getY() > 499) {
			moveMouseSlightly();
			return;
		}
		moveMouse(p);
	}

	@Override
	public void onFinish() {
		Bot.getEventManager().removeListener(PaintListener.class, this);
		threadPool.shutdownNow();
		scriptIsRunning = false;
	}

	public void onRepaint(final Graphics render) {
		if (!scriptIsRunning || !isLoggedIn()) {
			return;
		}
		final Graphics2D g = (Graphics2D) render;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		final long timeInSeconds = (System.currentTimeMillis() - startTime) / 1000;
		long estimatedExpPerHour = 0;
		if (timeInSeconds > 0) {
			estimatedExpPerHour = (skills
					.getCurrentSkillExp(Constants.STAT_FISHING) - startingFishingExp)
					* 60 * 60 / timeInSeconds;
		}
		g.setColor(new Color(0, 0, 0, 150));
		g.drawOval((int) getMouseLocation().getX() - 25,
				(int) getMouseLocation().getY() - 25, 50, 50);
		g.drawLine((int) getMouseLocation().getX() - 25,
				(int) getMouseLocation().getY(), (int) getMouseLocation()
						.getX() + 25, (int) getMouseLocation().getY());
		g.drawLine((int) getMouseLocation().getX(), (int) getMouseLocation()
				.getY() - 25, (int) getMouseLocation().getX(),
				(int) getMouseLocation().getY() + 25);
		g.setColor(new Color(255, 255, 255, 50));
		g.fillOval((int) getMouseLocation().getX() - 25,
				(int) getMouseLocation().getY() - 25, 50, 50);
		g.setColor(new Color(0, 0, 0, 125));
		g.fillRect(4, 124, 16, 100);
		g.setColor(new Color(0, 0, 0));
		g.drawRect(4, 124, 16, 100);
		g.setColor(new Color(0, 0, 0, 50));
		g.drawRect(5, 125, 14, 98);
		g.setColor(new Color(17, 255, 0, 150 / 2));
		g
				.fillRect(
						6,
						126 + 96 - (int) (96 * skills
								.getPercentToNextLevel(Constants.STAT_FISHING) / 100.0),
						12,
						(int) (96 * skills
								.getPercentToNextLevel(Constants.STAT_FISHING) / 100.0));
		g.setColor(new Color(0, 0, 0));
		g
				.drawRect(
						6,
						126 + 96 - (int) (96 * skills
								.getPercentToNextLevel(Constants.STAT_FISHING) / 100.0),
						12,
						(int) (96 * skills
								.getPercentToNextLevel(Constants.STAT_FISHING) / 100.0));
		g.setColor(new Color(255, 255, 255, 150 / 4));
		g.fillRect(4, 124, 16, 50);
		g.setColor(new Color(0, 0, 0, 25));
		g.fillRect(4, 224, 30, 16);
		g.setColor(new Color(0, 0, 0));
		g.drawRect(4, 224, 30, 16);
		g.setColor(new Color(0, 0, 0, 10));
		g.drawRect(5, 225, 28, 14);
		g.setColor(new Color(255, 255, 255));
		g.drawString(
				skills.getPercentToNextLevel(Constants.STAT_FISHING) + "%", 8,
				237);
		g.setColor(new Color(255, 255, 255, 65));
		g.fillRect(4, 224, 30, 8);
		int paintItemY = 20;
		int paintItems = 0;
		g.setColor(new Color(255, 255, 255, 200));
		final String[] paintItem = {
				getClass().getAnnotation(ScriptManifest.class).name()
						+ " v"
						+ getClass().getAnnotation(ScriptManifest.class)
								.version(),
				"Time: " + timeSince(startTime),
				"Exp Gained: "
						+ NumberFormat
								.getInstance()
								.format(
										skills
												.getCurrentSkillExp(Constants.STAT_FISHING)
												- startingFishingExp),
				"Levels Gained: "
						+ (skills.getCurrentSkillLevel(Constants.STAT_FISHING) - startingFishingLevel),
				"Fishing level: "
						+ skills.getRealSkillLevel(Constants.STAT_FISHING),
				"Exp to next level: "
						+ NumberFormat
								.getInstance()
								.format(
										skills
												.getXPToNextLevel(Constants.STAT_FISHING)),
				"Total Catches: "
						+ NumberFormat
								.getInstance()
								.format(
										(skills
												.getCurrentSkillExp(Constants.STAT_FISHING) - startingFishingExp) / 120),
				"Catches until levelup: "
						+ NumberFormat
								.getInstance()
								.format(
										skills
												.getXPToNextLevel(Constants.STAT_FISHING) / 120),
				"Money Made: "
						+ NumberFormat
								.getInstance()
								.format(
										(skills
												.getCurrentSkillExp(Constants.STAT_FISHING) - startingFishingExp)
												/ 120 * rawMonkfishPrice),
				"Exp per Hour: "
						+ NumberFormat.getInstance()
								.format(estimatedExpPerHour),
				"Catches per Hour: "
						+ NumberFormat.getInstance().format(
								estimatedExpPerHour / 120) };
		for (final String item : paintItem) {
			g.drawString(item, 350, paintItemY);
			paintItemY += 12;
			paintItems++;
		}
		g.setColor(new Color(0, 255, 0, 150 / 2));
		g.fillRoundRect(347, 8, 166, (int) (paintItemY - paintItems * 1.5), 8,
				8);
		g.setColor(new Color(255, 255, 255, 150 / 4));
		g.fillRoundRect(348, 9, 165, (int) (paintItemY - paintItems * 1.5) / 2,
				8, 8);
	}

	@Override
	public boolean onStart(final Map<String, String> map) {
		if (!isLoggedIn()) {
			log("Please start the script while logged in!");
			return false;
		} else {
			startTime = System.currentTimeMillis();
			log.info("Getting the market price for raw monkfishes.");
			final GEItemInfo rawMonkfish = grandExchange
					.loadItemInfo(SMonkFisher.RAW_MONKFISH);
			rawMonkfishPrice = rawMonkfish.getMarketPrice();
			log.info("Done getting market prices!");
			log.info("	-Raw Monkfish: " + rawMonkfishPrice + "gp");
			log.info("Done loading, initiating script...");
			threadPool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
			threadPool.setKeepAliveTime(5000, TimeUnit.MILLISECONDS);
			randomRun = random(50, 100);
			scriptIsRunning = true;
			firstTick = true;
			updateCheck();
			return true;
		}
	}

	void updateCheck() {
		URLConnection url;
		BufferedReader in;
		BufferedWriter out = null;
		// Ask the user if they'd like to check for an update...
		if (JOptionPane
				.showConfirmDialog(
						null,
						"Would you like to check for updates?\nPlease note: this requires an internet connection and the script will write files to your harddrive!") == 0) { // If
																																												// they
																																												// would,
																																												// continue
			try {
				// Open the version text file
				url = new URL(
						"http://www.scapemarket.info/scripts/SMonkFisherVERSION.txt")
						.openConnection();
				// Create an input stream for it
				in = new BufferedReader(new InputStreamReader(url
						.getInputStream()));
				// Check if the current version is outdated
				if (Double.parseDouble(in.readLine()) > getVersion()) {
					// If it is, check if the user would like to update.
					if (JOptionPane.showConfirmDialog(null,
							"Update found. Do you want to update?") == 0) {
						// If so, allow the user to choose the file to be
						// updated.
						JOptionPane
								.showMessageDialog(null,
										"Please choose 'SMonkFisher.java' in your scripts folder and hit 'Open'");
						final JFileChooser fc = new JFileChooser();
						// Make sure "Open" was clicked.
						if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
							// If so, set up the URL for the .java file and set
							// up the IO.
							url = new URL(
									"http://www.scapemarket.info/scripts/SMonkFisher.java")
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
							stopScript();
						} else {
							log("Update canceled");
						}
					} else {
						log("Update canceled");
					}
				} else {
					JOptionPane.showMessageDialog(null,
							"You have the latest version. :)"); // User has the
																// latest
																// version. Tell
																// them!
				}
				if (in != null) {
					in.close();
				}
				if (out != null) {
					out.close();
				}
			} catch (final IOException e) {
				log("Problem getting version :/");
			}
		}
	}

	private String timeSince(final long time) {
		final int seconds = (int) ((System.currentTimeMillis() - time) / 1000);
		final int minutes = seconds / 60;
		final int hours = minutes / 60;
		return SMonkFisher.format.format(hours % 24) + ":"
				+ SMonkFisher.format.format(minutes % 60) + ":"
				+ SMonkFisher.format.format(seconds % 60);
	}

	private void walkTile(final RSTile tile) {
		if (!(Methods.distanceBetween(getMyPlayer().getLocation(), tile) < 6)
				&& !getMyPlayer().isMoving()) {
			if (distanceTo(tile) < 16) {
				walkTo(randomizeTile(tile, 1, 1));
			} else {
				walkTo(generateTilePath(randomizeTile(tile, 1, 1)));
			}
		}
	}

}