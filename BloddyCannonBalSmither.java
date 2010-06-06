import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import javax.imageio.ImageIO;

import org.rsbot.bot.Bot;
import org.rsbot.bot.input.Mouse;
import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.script.Constants;
import org.rsbot.script.GrandExchange;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.Skills;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSPlayer;
import org.rsbot.script.wrappers.RSTile;
import org.rsbot.util.ScreenshotUtil;

@ScriptManifest(authors = { "Bloddyharry" }, name = "Bloddy Cannonbal Smither", category = "Smithing", version = 1.2, description = "<html>\n"
		+ "<body style='font-family: Calibri; color:white; padding: 0px; text-align: center; background-color: black;'>"
		+ "<h2>"
		+ "Bloddy Cannonbal Smither 1.2"
		+ "</h2>\n"
		+ "Made by Bloddyharry"
		+ "<br><br>\n"
		+ "<b>start in the selected bank with Steel bars in your bank.</b>\n"
		+ "<br><br>\n"
		+ "Amount:"
		+ "<br><br>\n"
		+ "<input name='amount' type='text' size='10' maxlength='10' value='2000' /><br /><br />"
		+ "<br><br>\n"
		+ "Location:"
		+ "<select name='Location'><option>Alkharid</option><option>Edgeville</option></select><br /><br/>")
public class BloddyCannonBalSmither extends Script implements PaintListener,
		ServerMessageListener {

	final ScriptManifest properties = getClass().getAnnotation(
			ScriptManifest.class);

	final GrandExchange grandExchange = new GrandExchange();

	public boolean showInventory = false;
	BufferedImage normal = null;
	BufferedImage clicked = null;
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
	public int expToLevel = 0;
	public long secToLevel = 0;
	public long minutesToLevel = 0;
	public long hoursToLevel = 0;
	public float secExp = 0;
	public float minuteExp = 0;
	public float hourExp = 0;

	public String status = "";
	private int startXP = 0;
	public int goldID;
	private int startLvl;
	public int goldBarID = 2353;
	private int AMOUNT;
	public int animation = 832;
	public int bBoothID = 35647;
	public int furnanceID;
	public int profit = 0;
	int mouldID = 4;
	public static final int INTERFACE_LEVELUP = 740;
	public int runEnergy = random(65, 90);
	public boolean useRest = false;
	public boolean madeRings = false;
	RSTile bankTile = new RSTile(3269, 3167);
	RSTile furnanceTile = new RSTile(3275, 3185);
	RSTile[] bankToFurnance = { new RSTile(3269, 3167), new RSTile(3277, 3176),
			new RSTile(3275, 3185) };
	RSTile[] furnanceToBank = reversePath(bankToFurnance);
	RSTile[] bankToFurnance2 = { new RSTile(3097, 3496), new RSTile(3109, 3501) };
	RSTile[] furnanceToBank2 = reversePath(bankToFurnance2);
	RSTile bankTile2 = new RSTile(3097, 3496);
	RSTile furnanceTile2 = new RSTile(3108, 3500);
	public long startTime = System.currentTimeMillis();
	int ballsAdded = 0;
	public String what;
	int ballID = 2;
	int steelBarzAdded = 0;
	public String location;
	public int ballsCost = grandExchange.loadItemInfo(ballID).getMarketPrice();
	public int steelbarCost = grandExchange.loadItemInfo(goldBarID)
			.getMarketPrice();
	final RSObject bankBooth = getNearestObjectByID(bBoothID);

	protected int getMouseSpeed() {
		return random(6, 7);
	}

	public double getVersion() {
		return 1.2;
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
		location = args.get("Location");
		if (location.equals("Alkharid")) {
			furnanceID = 11666;
			bBoothID = 35647;
		} else if (location.equals("Edgeville")) {
			furnanceID = 26814;
			bBoothID = 26972;
		}
		what = args.get("what");
		startTime = System.currentTimeMillis();
		AMOUNT = Integer.parseInt(args.get("amount"));
		if (isLoggedIn()) {
			startXP = skills
					.getCurrentSkillExp(Skills.getStatIndex("smithing"));
			startLvl = skills.getCurrentSkillLevel(Skills
					.getStatIndex("smithing"));
		}
		return true;
	}

	public void onFinish() {
		ScreenshotUtil.takeScreenshot(true);
		log("Thank you for using Bloddy Cannonbal Smither!");
		wait(1000);
		log("-------------------------------------------");
		wait(1000);
		log("made " + ballsAdded + " cannonBallz");
		wait(1000);
		log("ran for " + hours + ":" + minutes + ":" + seconds);
		wait(1000);
		log("your lvl is " + currentLVL);
		wait(1000);
		log("gained " + gainedXP + " XP");
		wait(1000);
		log("-------------------------------------------");

	}

	public void checkAmountMade() {
		if (steelBarzAdded >= AMOUNT) {
			ScreenshotUtil.takeScreenshot(true);
			log("made amount you filled in, stopping script!");
			logOut();
			stopScript();

		}
	}

	public void checkAnimation() {
		if (getMyPlayer().getAnimation() != -1) {
			status = "Making Ballz";
			wait(random(500, 1500));
			antiBan();
		}
	}

	@Override
	public int loop() {
		setCameraAltitude(true);
		checkAnimation();
		checkAmountMade();
		if (!inventoryContains(goldBarID)) {
			if (!atBank()) {
				status = "Walking to Bank";
				if (getEnergy() == random(60, 100)) {
					setRun(true);
				}
				walkToBank2();
				walkToBank();
			} else if (atBank()) {
				openBank();
				doBankGoldBar();
			}
		}
		if (inventoryContains(goldBarID) && getMyPlayer().getAnimation() == -1
				&& !inventoryContains(ballID)) {
			if (!atFurnance()) {
				status = "Walking to Furnance";
				if (getEnergy() >= 65) {
					setRun(true);
				}
				walkToFurnance2();
				walkToFurnance();

			} else if (atFurnance() && getMyPlayer().getAnimation() == -1
					&& !inventoryContains(ballID)) {
				makeBalls();
			}
		}
		return random(100, 200);
	}

	public void openBank() {
		status = "Banking";
		final RSObject bankBooth = getNearestObjectByID(bBoothID);
		if (bankBooth != null && !bank.isOpen()) {
			atObject(bankBooth, "Use-quickly ");
			wait(random(1000, 1200));
		}
	}

	public void doBankGoldBar() {
		if (bank.isOpen()) {
			bank.depositAllExcept(mouldID);
			wait(random(800, 1000));
			if (!inventoryContains(goldBarID)) {
				bank.atItem(goldBarID, "Withdraw-All");
				wait(random(300, 400));
				if (inventoryContains(goldBarID))
					;
				return;
			} else {
				wait(random(300, 500));
				log("misclicked.. trying again :/");
				return;

			}
		}
	}

	private void logOut() {
		moveMouse(754, 10, 10, 10);
		clickMouse(true);
		moveMouse(642, 378, 10, 10);
		clickMouse(true);
		wait(random(2000, 3000));
		stopScript();

	}

	public int walkToFurnance() {
		if (location.equals("Alkharid")) {
			walkTo(furnanceTile);
		}
		return random(700, 800);
	}

	public int walkToBank() {
		if (location.equals("Alkharid")) {
			walkTo(bankTile);
		}
		return random(700, 800);
	}

	public int walkToFurnance2() {
		if (location.equals("Edgeville")) {
			walkTo(furnanceTile2);
		}
		return random(700, 800);
	}

	public int walkToBank2() {
		if (location.equals("Edgeville")) {
			walkTo(bankTile2);
		}
		return random(700, 800);
	}

	public boolean Resting() {
		if (restCheck() && atBank()) {
			moveMouse(726, 110, 10, 10);
			clickMouse(false);
			moveMouse(674, 154, 10, 10);
			clickMouse(true);
			wait(random(2300, 3000));
			while (getMyPlayer().getAnimation() != -1) {
				status = "Resting";
				wait(random(1000, 2500));
				antiBan();
				if (onEnergyCheck() > 94) {
					break;
				}
			}
		}
		return true;
	}

	// Methods
	public boolean atInventoryItemUse(int goldBarID) {
		if (getCurrentTab() != TAB_INVENTORY
				&& !RSInterface.getInterface(INTERFACE_BANK).isValid()
				&& !RSInterface.getInterface(INTERFACE_STORE).isValid()) {
			openTab(TAB_INVENTORY);
		}
		int[] items = getInventoryArray();
		java.util.List<Integer> possible = new ArrayList<Integer>();
		for (int i = 0; i < items.length; i++) {
			if (items[i] == goldBarID) {
				possible.add(i);
			}
		}
		if (possible.size() == 0)
			return false;
		int idx = possible.get(random(0, possible.size()));
		Point t = getInventoryItemPoint(idx);
		clickMouse(t, 5, 5, true);
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

	public boolean atBank() {
		if (location.equals("Alkharid")) {
			return distanceTo(bankTile) <= 5;
		} else if (location.equals("Edgeville")) {
			return distanceTo(bankTile2) <= 5;
		}
		return true;
	}

	public boolean atFurnance() {
		if (location.equals("Alkharid")) {
			return distanceTo(furnanceTile) <= 5;
		} else if (location.equals("Edgeville")) {
			return distanceTo(furnanceTile2) <= 3;
		}
		return true;
	}

	private int onEnergyCheck() {
		return Integer
				.parseInt(RSInterface.getChildInterface(750, 5).getText());
	}

	private boolean restCheck() {
		return onEnergyCheck() < 20;
	}

	public int bank() {
		status = "Banking";
		final RSObject bankBooth = getNearestObjectByID(bBoothID);
		if (bank.isOpen() && !inventoryContainsOneOf(goldBarID)) {
			bank.depositAllExcept(mouldID);
			wait(random(1400, 1600));
			bank.atItem(goldBarID, "Withdraw-All");
		}
		if (!(bank.isOpen() && !inventoryContainsOneOf(goldBarID))) {
			if (bankBooth != null) {
				atObject(bankBooth, "Use-Quickly");
				wait(random(200, 300));
			}
			if (bankBooth == null) {
				return random(100, 200);
			}
		}
		return random(150, 350);
	}

	public boolean makeBalls() {
		RSObject obj = getNearestObjectByID(furnanceID);
		status = "Steel bar > Furnance";
		if (obj != null && getMyPlayer().getAnimation() == -1) {
			setCameraRotation(random(1, 360));
			if (getMyPlayer().getAnimation() == -1) {
				atInventoryItemUse(goldBarID);
				doSomethingObj(furnanceID, "Use");
				wait(random(1500, 2000));
				moveMouse(237, 396, 50, 50);
				wait(random(100, 200));
				atMenu("Make All");
				wait(random(100, 300));
				antiBan2();
				wait(random(1000, 1500));
				if (getMyPlayer().getAnimation() != -1) {
					steelBarzAdded += 27;
					return true;
				} else
					return makeBalls();
			}
		}
		return true;
	}

	// Methods
	public boolean doSomethingObj(int fountainID, String action) {
		RSObject obj = getNearestObjectByID(fountainID);
		if (obj == null)
			return false;

		if (obj != null && getMyPlayer().getAnimation() == -1) {
			int random = random(1, 20);
			if (random > 16)
				antiBan();
			atObject(obj, action);
			return true;
		}
		return false;
	}

	// Credits to Garrett
	public void onRepaint(Graphics g) {
		profit = (ballsAdded * 442);
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
		seconds = runTime / 1000;
		if (seconds >= 60) {
			minutes = seconds / 60;
			seconds -= minutes * 60;
		}
		if (minutes >= 60) {
			hours = minutes / 60;
			minutes -= hours * 60;
		}

		if ((minutes > 0 || hours > 0 || seconds > 0) && gainedXP > 0) {
			secExp = (float) gainedXP
					/ (float) (seconds + minutes * 60 + hours * 60 * 60);
		}
		minuteExp = secExp * 60;
		hourExp = minuteExp * 60;
		expToLevel = skills.getXPToNextLevel(Constants.STAT_SMITHING);
		currentXP = skills.getCurrentSkillExp(Skills.getStatIndex("smithing"));
		currentLVL = skills.getCurrentSkillLevel(Skills
				.getStatIndex("smithing"));
		gainedXP = currentXP - startXP;
		gainedLVL = currentLVL - startLvl;
		xpPerHour = (int) ((3600000.0 / (double) runTime) * gainedXP);
		monkFishHour = (int) ((3600000.0 / (double) runTime) * ballsAdded);
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
					g.drawString("Bloddy Ballz Smither", 561, coords[0]);
					g.drawString("Version: " + properties.version(), 561,
							coords[1]);
					g.setFont(new Font("Lucida Calligraphy", Font.PLAIN, 12));
					g.setColor(Color.LIGHT_GRAY);
					g.drawString("Run Time: " + hours + ":" + minutes + ":"
							+ seconds, 561, coords[2]);
					g.setColor(Color.RED);
					g.drawString(ballsAdded + " Ballz Made", 561, coords[4]);
					g.setColor(Color.LIGHT_GRAY);
					g.drawString("Ballz/hour: " + monkFishHour, 561, coords[5]);
					g.setColor(Color.RED);
					g.drawString("XP Gained: " + gainedXP, 561, coords[6]);
					g.setColor(Color.LIGHT_GRAY);
					g.drawString("XP/Hour: " + xpPerHour, 561, coords[7]);
					g.setColor(Color.RED);
					g.drawString("Your level is " + currentLVL, 561, coords[9]);
					g.setColor(Color.LIGHT_GRAY);
					g.drawString("Lvls Gained: " + gainedLVL, 561, coords[10]);
					g.setColor(Color.RED);
					g
							.drawString("XP To Next Level: "
									+ skills.getXPToNextLevel(Skills
											.getStatIndex("smithing")), 561,
									coords[11]);
					g.setColor(Color.LIGHT_GRAY);
					g
							.drawString("% To Next Level: "
									+ skills.getPercentToNextLevel(Skills
											.getStatIndex("smithing")), 561,
									coords[12]);
					g.setColor(Color.RED);
					g.drawString("Status: " + status, 561, coords[14]);
					g.setColor(Color.RED);
					g.drawString("Made by Bloddyharry", 561, coords[15]);
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

	public void serverMessageRecieved(ServerMessageEvent arg0) {
		String serverString = arg0.getMessage();

		if (serverString.contains("<col=ffff00>System update in")) {
			log("There will be a system update soon, so we logged out");
			logout();
			stopScript();
		}
		if (serverString.contains("Oh dear, you are dead!")) {
			status = "Dead";
			log("We somehow died :S, shutting down");
			logout();
			stopScript();
		}
		if (serverString.contains("You remove")) {
			ballsAdded += 4;
		}
		if (serverString.contains("You've just advanced")) {
			wait(random(3500, 4000));
			log("Congrats on level up, Screenshot taken!");
			wait(random(1000, 1500));
			if (canContinue()) {
				ScreenshotUtil.takeScreenshot(true);
				clickContinue();
			}

		}
	}

}
