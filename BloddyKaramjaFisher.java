import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
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
import org.rsbot.script.GrandExchange;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.Skills;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSPlayer;
import org.rsbot.script.wrappers.RSTile;
import org.rsbot.util.ScreenshotUtil;

@ScriptManifest(authors = { "Bloddyharry" }, name = "Bloddy Karamja Fisher", category = "Fishing", version = 1.6, description = "<html>\n"
		+ "<body style='font-family: Calibri; background-color: black; color:white; padding: 0px; text-align:'>"
		+ "<h2>"
		+ "Bloddy Karamja Fisher 1.6"
		+ "</h2>\n"
		+ "Made by Bloddyharry"
		+ "<br><br>\n"
		+ "<b>start in at the fishing spots on Karamja with a harpoon in your inventory. uses Stiles!</b>\n"
		+ "<br><br>\n"
		+ "<b>Has great AntiBan and paint!</b>\n"
		+ "<br><br>\n"
		+ "fish: <select name='fishtype'><option>Lobster</option><option>Tuna/swordy</option></select><br /><br/>"
		+ "profit to show: <select name='profitshow'><option>Lobster</option><option>Tuna</option><option>Swordfish</option></select><br /><br/>"
		+ "<br><br>\n"
		+ "use random mouse Speed between: <input name='mousespeed' type='text' width='3' value='6' /><br /> <input name='mousespeed2' type='text' width='3' value='7' /><br />"
		+ "if you selected the box for fishing an amount of fish, fill in the amount:  <input name='AMOUNTID' type='text' width='10' value='' /><br />")
public class BloddyKaramjaFisher extends Script implements PaintListener,
		ServerMessageListener {

	final ScriptManifest properties = getClass().getAnnotation(
			ScriptManifest.class);

	public boolean showInventory = false;
	BufferedImage normal = null;
	BufferedImage clicked = null;

	public int profit = 0;
	long runTime = 0;
	long seconds = 0;
	long minutes = 0;
	long hours = 0;
	int monkFishHour = 0;
	int currentXP = 0;
	int currentLVL = 0;
	int gainedXP = 0;
	int gainedLVL = 0;
	int xpPerHour = 0;
	int profitHour = 0;

	private int AMOUNTID;
	private int mousespeed;
	private int mousespeed2;
	public int[] fuckedFishID = { 371, 359 };
	public String status = "";
	final GrandExchange grandExchange = new GrandExchange();
	private int startLvl;
	public int fishAdded = 0;
	private int startXP = 0;
	public long startTime = System.currentTimeMillis();
	public String fishComm;
	public int bBoothID = 49018;
	public int fishSpotID = 324;
	public int[] harpoonID = { 311, 14109, 10129, 301 };
	public int[] fishID = { 277, 359, 371 };
	public int lobsterID = 377;
	public int tunaID = 359;
	public int swordfishID = 371;
	public int[] wealthyStuff = { 383, 311, 14109, 10129 };
	public int stilesID = 11267;
	public int runEnergy = random(75, 95);
	public boolean dolobsterfishing = false;
	public boolean dotunafishing = false;
	public boolean dobothfishing = false;
	public boolean doAmountFishing = false;
	public int fishCost;
	RSTile stilesTile = new RSTile(2853, 3142);
	RSTile fishSpotTile = new RSTile(2924, 3179);
	RSTile[] fishSpotToStiles = { new RSTile(2924, 3179),
			new RSTile(2917, 3176), new RSTile(2906, 3172),
			new RSTile(2895, 3168), new RSTile(2884, 3160),
			new RSTile(2874, 3153), new RSTile(2863, 3150),
			new RSTile(2857, 3144), new RSTile(2853, 3142) };
	RSTile[] stilesToFishSpot = reversePath(fishSpotToStiles);
	public String fishtype;
	public String profitshow;

	public double getVersion() {
		return 2.5;
	}

	protected int getMouseSpeed() {
		return random(mousespeed, mousespeed2);
	}

	public boolean onStart(Map<String, String> args) {
		try {
			final URL cursorURL = new URL("http://i48.tinypic.com/313623n.png");
			final URL cursor80URL = new URL("http://i46.tinypic.com/9prjnt.png");
			normal = ImageIO.read(cursorURL);
			clicked = ImageIO.read(cursor80URL);
		} catch (MalformedURLException e) {
			log("Unable to buffer cursor.");
		} catch (IOException e) {
			log("Unable to open cursor image.");
		}
		if (args.get("fishAmount") != null) {
			doAmountFishing = true;
		}
		if (args.get("fishtype").equals("Tuna/swordy")) {
			fishComm = "Harpoon";
		}
		if (args.get("profitshow").equals("Tuna")) {
			fishCost = grandExchange.loadItemInfo(tunaID).getMarketPrice();
		}
		if (args.get("profitshow").equals("Swordfish")) {
			fishCost = grandExchange.loadItemInfo(swordfishID).getMarketPrice();
		}
		if (args.get("profitshow").equals("Lobster")) {
			fishCost = grandExchange.loadItemInfo(lobsterID).getMarketPrice();
		}
		if (args.get("fishtype").equals("Lobster")) {
			fishComm = "Cage";
		}
		if (doAmountFishing == true) {
			AMOUNTID = Integer.parseInt(args.get("AMOUNTID"));
		}
		mousespeed = Integer.parseInt(args.get("mousespeed"));
		mousespeed2 = Integer.parseInt(args.get("mousespeed2"));
		startTime = System.currentTimeMillis();
		if (isLoggedIn()) {
			startLvl = skills.getCurrentSkillLevel(Skills
					.getStatIndex("fishing"));
			startXP = skills.getCurrentSkillExp(Skills.getStatIndex("fishing"));
		}
		return true;
	}

	public void onFinish() {
		ScreenshotUtil.takeScreenshot(true);
		log("Thank you for using Bloddy Karamja Fisher!");
		wait(1000);
		log("-------------------------------------------");
		wait(1000);
		log("ran for " + hours + ":" + minutes + ":" + seconds);
		wait(1000);
		log("Caught " + fishAdded + " fishes");
		wait(1000);
		log("Gained " + gainedLVL + " lvls");
		wait(1000);
		log("your lvl is " + currentLVL);
		wait(1000);
		log("gained " + gainedXP + "XP");
		wait(1000);
		log("-------------------------------------------");

	}

	@Override
	public int loop() {
		setCameraAltitude(true);
		animation();
		checkAmount();
		checkInventory();
		if (getEnergy() == random(50, 100)) {
			setRun(true);
		}
		if (!isInventoryFull() && getMyPlayer().getAnimation() == -1) {
			if (atFishSpot() && getMyPlayer().getAnimation() == -1) {
				fish();
			} else if (!atFishSpot()) {
				status = "walking to fishspots";
				if (distanceTo(getDestination()) < random(5, 12)
						|| distanceTo(getDestination()) > 40) {
					if (!walkPathMM(stilesToFishSpot)) {
						walkToClosestTile(randomizePath(stilesToFishSpot, 2, 2));
						return random(250, 500);
					}
				}
			}
		}
		return 0;
	}

	public int checkInventory() {
		if (isInventoryFull()) {
			if (atStiles()) {
				exchangeFish();
			} else if (!atStiles()) {
				status = "walking to Stiles";
				if (distanceTo(getDestination()) < random(5, 12)
						|| distanceTo(getDestination()) > 40) {
					if (!walkPathMM(fishSpotToStiles)) {
						walkToClosestTile(randomizePath(fishSpotToStiles, 2, 2));
						return random(250, 500);
					}
				}
			}
		}
		return 0;
	}

	private void exchangeFish() {
		RSNPC stiles = getNearestNPCByID(stilesID);
		if (!getMyPlayer().isMoving() && getMyPlayer().getAnimation() == -1) {
			atNPC(stiles, "Exchange");
			wait(random(600, 1000));
			if (canContinue()) {
				clickContinue();
				wait(random(400, 700));
			}

		}

	}

	public boolean animation() {
		if (getMyPlayer().getAnimation() != -1
				&& getMyPlayer().getAnimation() != 11786
				&& getMyPlayer().getAnimation() != 5713) {
			status = "Fishing";
			wait(random(500, 2500));
			antiBan();
		}
		return true;
	}

	public boolean atStiles() {
		return distanceTo(stilesTile) <= 4;
	}

	public boolean atFishSpot() {
		return distanceTo(fishSpotTile) <= 10;
	}

	public boolean fish() {
		if (getMyPlayer().getAnimation() == -1 && !getMyPlayer().isMoving()) {
			RSNPC nSpot = getNearestNPCByID(fishSpotID);
			if (nSpot == null) {
				walkTileMM(new RSTile(2335, 3696));// If not found walk here.
													// give another id. a bit
													// further away. ;)
													// <-------------------------------
			}
			wait(random(50, 150));
		}
		RSNPC nSpot = getNearestNPCByID(fishSpotID);
		if (nSpot != null && !getMyPlayer().isMoving()) {
			status = "Walking to fishspot";
			if (distanceTo(nSpot.getLocation()) > 4 || !nSpot.isOnScreen()) {
				RSTile destination = randomizeTile(nSpot.getLocation(), 2, 2);
				walkTileMM(destination);
				waitToStop();
				wait(random(500, 1000));
			}
			if (nSpot.isOnScreen()) {
				status = "Clicking > Fishspot";
				atTile(nSpot.getLocation(), fishComm);
				antiBan2();
				waitToStop();
				wait(random(300, 500));
				if (getMyPlayer().getAnimation() != -1) {
					return animation();
				} else
					wait(random(1000, 1300));
			}
		}
		return true;
	}

	public boolean antiBan2() {
		int randomNumber = random(1, 11);
		if (randomNumber <= 11) {
			if (randomNumber == 1) {
				setCameraRotation(random(1, 360));
			}
			if (randomNumber == 2) {
				moveMouse(random(50, 700), random(50, 450), 2, 2);
				wait(random(200, 400));
				moveMouse(163, 111, 150, 150);
			}
			if (randomNumber == 3) {
				moveMouse(163, 111, 150, 150);
			}
			if (randomNumber == 4) {
				moveMouse(163, 111, 150, 150);
			}
			if (randomNumber == 5) {
				moveMouse(163, 111, 150, 150);
			}
		}
		return true;
	}

	public void checkAmount() {
		if (doAmountFishing == true) {
			if (fishAdded >= AMOUNTID) {
				ScreenshotUtil.takeScreenshot(true);
				log("w00t, we mined the amount of ores! logging out!");
				wait(random(500, 1000));
				logOut();
			}
		}
	}

	public boolean checkAnimation() {
		if (getMyPlayer().getAnimation() != -1) {
			status = "Fishing";
			wait(random(500, 2500));
			antiBan();
		}
		return true;
	}

	public void waitToStop() {
		while (getMyPlayer().isMoving()) {
			wait(150);
		}
	}

	public void logOut() {
		moveMouse(754, 10, 10, 10);
		clickMouse(true);
		moveMouse(642, 378, 20, 15);
		clickMouse(true);
		wait(random(2000, 3000));
		stopScript();
	}

	public boolean antiBan() {
		int randomNumber = random(1, 18);
		if (randomNumber <= 18) {
			if (randomNumber == 1) {
				randomHoverPlayer();
			}
			if (randomNumber == 2) {
				moveMouse(random(50, 700), random(50, 450), 2, 2);
				wait(random(1000, 1500));
				moveMouse(random(50, 700), random(50, 450), 2, 2);
			}
			if (randomNumber == 3) {
				openRandomTab();
				wait(random(100, 500));
				moveMouse(522, 188, 220, 360);
				wait(random(500, 2800));
			}
			if (randomNumber == 4) {
				wait(random(100, 200));
				moveMouse(random(50, 700), random(50, 450), 2, 2);
				setCameraRotation(random(1, 360));
				moveMouse(random(50, 700), random(50, 450), 2, 2);
			}
			if (randomNumber == 6) {
				moveMouse(random(50, 700), random(50, 450), 2, 2);
			}
			if (randomNumber == 7) {
				moveMouse(random(50, 700), random(50, 450), 2, 2);
			}
			if (randomNumber == 8) {
				wait(random(100, 200));
				moveMouse(random(50, 700), random(50, 450), 2, 2);
				wait(random(200, 500));
				if (randomNumber == 9) {
					wait(random(100, 200));
					moveMouse(random(50, 700), random(50, 450), 2, 2);
					if (randomNumber == 10) {
						moveMouse(random(50, 700), random(50, 450), 2, 2);
					}
					if (randomNumber == 11) {
						setCameraRotation(random(1, 360));
						moveMouse(random(50, 700), random(50, 450), 2, 2);
					}
					if (randomNumber == 12) {
						openTab(TAB_STATS);
						wait(random(50, 100));
						moveMouse(675, 268, 20, 20);
						wait(random(500, 1700));
					}
					if (randomNumber == 13) {
						moveMouse(random(50, 700), random(50, 450), 2, 2);
						setCameraRotation(random(1, 360));
					}
					if (randomNumber == 14) {
						openTab(TAB_STATS);
						wait(random(50, 100));
						moveMouse(675, 268, 20, 20);
						wait(random(500, 1700));
					}
					if (randomNumber == 15) {
						randomHoverPlayer();
					}
				}

			}
		}
		return true;
	}

	private void randomHoverPlayer() {
		int randomNumber = random(1, 10);
		if (randomNumber <= 10) {
			if (randomNumber == 1) {
				RSPlayer p = getNearestPlayerByLevel(1, 130);
				if ((p != null) && tileOnScreen(p.getLocation())) {
					moveMouse(p.getScreenLocation(), 40, 40);
					wait(random(450, 650));
				}
				if (randomNumber == 2) {
					if ((p != null) && tileOnScreen(p.getLocation())) {
						moveMouse(p.getScreenLocation(), 40, 40);
						wait(random(100, 400));
						clickMouse(false);
						wait(random(1000, 1700));
						moveMouse(random(50, 700), random(50, 450), 2, 2);
					}
				}
			}
		}
	}

	private void openRandomTab() {
		int randomNumber = random(1, 11);
		if (randomNumber <= 11) {
			if (randomNumber == 1) {
				openTab(TAB_STATS);
				wait(random(100, 200));
				moveMouse(675, 268, 20, 20);
				wait(random(500, 1700));
			}
			if (randomNumber == 2) {
				openTab(TAB_ATTACK);
			}
			if (randomNumber == 3) {
				openTab(TAB_EQUIPMENT);
			}
			if (randomNumber == 4) {
				openTab(TAB_FRIENDS);
			}
			if (randomNumber == 6) {
				openTab(TAB_MAGIC);
			}
			if (randomNumber == 7) {
				openTab(TAB_STATS);
			}
			if (randomNumber == 8) {
				openTab(TAB_QUESTS);
			}
			if (randomNumber == 9) {
				openTab(TAB_CLAN);
			}
			if (randomNumber == 10) {
				openTab(TAB_MUSIC);
			}
			if (randomNumber == 11) {
				openTab(TAB_ACHIEVEMENTDIARIES);
			}
		}
	}

	// Credits to Garrett
	public void onRepaint(Graphics g) {
		profit = (fishAdded * fishCost);
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

		currentXP = skills.getCurrentSkillExp(Skills.getStatIndex("fishing"));
		currentLVL = skills
				.getCurrentSkillLevel(Skills.getStatIndex("fishing"));
		gainedXP = currentXP - startXP;
		gainedLVL = currentLVL - startLvl;
		xpPerHour = (int) ((3600000.0 / (double) runTime) * gainedXP);
		monkFishHour = (int) ((3600000.0 / (double) runTime) * fishAdded);
		profitHour = (int) ((3600000.0 / (double) runTime) * profit);
		if (normal != null) {
			final Mouse mouse = Bot.getClient().getMouse();
			final int mouse_x = mouse.getMouseX();
			final int mouse_y = mouse.getMouseY();
			final int mouse_x2 = mouse.getMousePressX();
			final int mouse_y2 = mouse.getMousePressY();
			final long mpt = System.currentTimeMillis()
					- mouse.getMousePressTime();
			if (mouse.getMousePressTime() == -1 || mpt >= 1000) {
				g.drawImage(normal, mouse_x - 8, mouse_y - 8, null); // this
				// show
				// the
				// mouse
				// when
				// its
				// not
				// clicked
			}
			if (mpt < 1000) {
				g.drawImage(clicked, mouse_x2 - 8, mouse_y2 - 8, null); // this
				// show
				// the
				// four
				// squares
				// where
				// you
				// clicked.
				g.drawImage(normal, mouse_x - 8, mouse_y - 8, null); // this
				// show
				// the
				// mouse
				// as
				// normal
				// when
				// its/just
				// clicked
			}
			if (getCurrentTab() == TAB_INVENTORY) {
				if (showInventory == false) {
					g.setColor(new Color(0, 0, 255, 150));
					g.fillRoundRect(555, 210, 175, 250, 0, 0);

					g.setColor(Color.RED);
					g.draw3DRect(555, 210, 175, 250, true);

					g.setColor(Color.WHITE);
					int[] coords = new int[] { 225, 240, 255, 270, 285, 300,
							315, 330, 345, 360, 375, 390, 405, 420, 435, 450,
							465, 480 };
					g.setColor(Color.RED);
					g.setFont(new Font("Segoe Print", Font.BOLD, 14));
					g.drawString(properties.name(), 561, coords[0]);
					g.drawString("Version: " + properties.version(), 561,
							coords[1]);
					g.setFont(new Font("Lucida Calligraphy", Font.PLAIN, 12));
					g.setColor(Color.LIGHT_GRAY);
					g.drawString("Run Time: " + hours + ":" + minutes + ":"
							+ seconds, 561, coords[2]);
					g.setColor(Color.RED);
					g.drawString(fishAdded + " fish caught", 561, coords[4]);
					g.setColor(Color.LIGHT_GRAY);
					g.drawString("fish/hour: " + monkFishHour, 561, coords[5]);
					g.setColor(Color.RED);
					g.drawString("Profit: " + profit, 561, coords[6]);
					g.setColor(Color.LIGHT_GRAY);
					g.drawString("Profit/hour: " + profitHour, 561, coords[7]);
					g.setColor(Color.RED);
					g.drawString("XP Gained: " + gainedXP, 561, coords[8]);
					g.setColor(Color.LIGHT_GRAY);
					g.drawString("XP/Hour: " + xpPerHour, 561, coords[9]);
					g.setColor(Color.RED);
					g
							.drawString("Your level is " + currentLVL, 561,
									coords[10]);
					g.setColor(Color.LIGHT_GRAY);
					g.drawString("Lvls Gained: " + gainedLVL, 561, coords[11]);
					g.setColor(Color.RED);
					g.drawString("XP To Next Level: "
							+ skills.getXPToNextLevel(Skills
									.getStatIndex("fishing")), 561, coords[13]);
					g.setColor(Color.LIGHT_GRAY);
					g.drawString("% To Next Level: "
							+ skills.getPercentToNextLevel(Skills
									.getStatIndex("fishing")), 561, coords[14]);
					g.setColor(Color.RED);
					g.drawString("Status: " + status, 561, coords[15]);
				}
				g.setFont(new Font("Lucida Calligraphy", Font.PLAIN, 12));
				g.setColor(new Color(0, 0, 255, 150));
				g.fillRoundRect(6, 315, 120, 20, 0, 0);
				g.setColor(Color.red);
				g.draw3DRect(6, 315, 120, 20, true);
				g.setColor(Color.white);
				g.drawString("See inventory", 10, 330);

				Mouse m = Bot.getClient().getMouse();
				if (m.x >= 6 && m.x < 6 + 120 && m.y >= 315 && m.y < 315 + 30) {
					showInventory = true;
				} else {
					showInventory = false;
				}
				if (hours == 2 && minutes == 0 && seconds == 0) {
					log("w00t! ran for 2 hours! taking screenie :)");
					ScreenshotUtil.takeScreenshot(true);
				}
				if (hours == 3 && minutes == 0 && seconds == 0) {
					log("awesome! ran for 3 hours! taking screenie :)");
					ScreenshotUtil.takeScreenshot(true);
				}
				if (hours == 4 && minutes == 0 && seconds == 0) {
					log("Epic! ran for 4 hours! taking screenie :)");
					ScreenshotUtil.takeScreenshot(true);
				}
				if (hours == 5 && minutes == 0 && seconds == 0) {
					log("Hell yeaH! ran for 5 hours! taking screenie :)");
					ScreenshotUtil.takeScreenshot(true);
				}
				if (hours == 6 && minutes == 0 && seconds == 0) {
					log("keep it up! ran for 6 hours! taking screenie :)");
					ScreenshotUtil.takeScreenshot(true);
				}
				if (hours == 7 && minutes == 0 && seconds == 0) {
					log("NICE NICE! ran for 7 hours! taking screenie :)");
					ScreenshotUtil.takeScreenshot(true);
				}
				if (hours == 8 && minutes == 0 && seconds == 0) {
					log("SICK! ran for 8 hours! taking screenie :)");
					ScreenshotUtil.takeScreenshot(true);
				}
				if (hours == 9 && minutes == 0 && seconds == 0) {
					log("DA PERFECT PROGGY! ran for 9 hours! taking screenie :)");
					ScreenshotUtil.takeScreenshot(true);
				}
				if (hours == 10 && minutes == 0 && seconds == 0) {
					log("FUCKING AWESOME DUDE! ran for 10 hours! taking screenie :)");
					ScreenshotUtil.takeScreenshot(true);
				}
			}
		}
	}

	public void serverMessageRecieved(ServerMessageEvent e) {
		final String serverString = e.getMessage();
		if (serverString.contains("You catch")) {
			fishAdded++;
		}
		if (serverString.contains("You've just advanced")) {
			log("Congrats on level up, Screenshot taken!");
			ScreenshotUtil.takeScreenshot(true);
			wait(random(1500, 2500));
			if (canContinue()) {
				clickContinue();
			}
		}
	}
}