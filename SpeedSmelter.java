import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Map;

import org.rsbot.bot.Bot;
import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = { "SpeedWing" }, category = "Smithing", name = "SpeedSmelter", version = 1.110, description = "<html>\n"
		+ "<body style='font-family: Calibri; color:white; padding: 0px; text-align: center; background-color: black;'>"
		+ "<h2>"
		+ "SpeedSmelter 1.110"
		+ "</h2>\n"
		+ "Author: SpeedWing"
		+ "<br><br>\n"
		+ "<b>Always sell the bars above medium or high</b>"
		+ "<br><br>"
		+ "Start this script with an empty inventory,<br> either in Al Kharid bank,Falador West bank or Edgeville Bank*"
		+ "<br>"
		+ "<br>Select the type of bar to smelt : "
		+ "<select name=\"bar\"><option>Bronze<option>Iron<option>Silver<option>Steel<option>Gold<option>Mithril<option>Adamant<option>Rune</select>"
		+ "<br>Use Rest? : <input type='radio' name='rst' value='2'>"
		+ "<br>Does your character has Goldsmith Gaunts equipped? <input type='radio' name='gold' value='2'>"
		+ "<br><br>Credits go to Noobielul, Jacmob, Genka and Garrett."
		+ "<br><br>"
		+ "(*) You need to finish the Varrock Achievement Easy to be able to smelt here")
public class SpeedSmelter extends Script implements ServerMessageListener,
		PaintListener {

	final ScriptManifest properties = getClass().getAnnotation(
			ScriptManifest.class);
	// paths
	RSTile[] ToBank;
	RSTile[] ToFurnace;
	RSTile TB;
	RSTile TE = new RSTile(3110, 3502);
	RSTile TA = new RSTile(3275, 3185);
	RSTile TF = new RSTile(2973, 3370);
	RSTile[] TFE = { new RSTile(3102, 3498), new RSTile(3108, 3500) };
	RSTile[] TBE = { new RSTile(3103, 3498), new RSTile(3097, 3496) };
	RSTile[] TFF = { new RSTile(2950, 3376), new RSTile(2961, 3379),
			new RSTile(2970, 3378), new RSTile(2973, 3370) };
	RSTile[] TBF = { new RSTile(2964, 3378), new RSTile(2951, 3378),
			new RSTile(2946, 3369) };
	RSTile[] TFA = { new RSTile(3276, 3176), new RSTile(3275, 3186) };
	RSTile[] TBA = { new RSTile(3276, 3174), new RSTile(3270, 3167) };
	// Item ID's
	int firstore;
	int secondore;
	int bar;

	final int ring = 2568;
	final int copper = 436;
	final int tin = 438;
	final int iron = 440;
	final int silver = 442;
	final int coal = 453;
	final int gold = 444;
	final int mithril = 447;
	final int adamant = 449;
	final int runite = 451;
	final int bronzeb = 2349;
	final int ironb = 2351;
	final int silverb = 2355;
	final int steelb = 2353;
	final int goldb = 2357;
	final int mithrilb = 2359;
	final int adamantb = 2361;
	final int runiteb = 2363;
	// Object ID's
	int[] furnace = { 11666, 26814 };
	int Abnk = 35647;
	int Fbnk = 11758;
	int Ebnk = 26972;
	int bnk;
	// Others
	boolean usering = false;
	boolean getring = false;
	boolean ringcheck = true;
	int g;
	boolean woot = false;
	int runEnergy = 65;
	int minEnergy = 20;
	boolean resting = false;
	boolean rest = false;
	int min;
	int smelting = 3243; // smelting animation
	String barname;
	String place;
	String bara;
	int done;
	int tosmelt;
	int firstamount;
	int secondamount;
	int toclick;
	long startTime = System.currentTimeMillis();
	int startLvl = skills.getCurrentSkillLevel(Constants.STAT_SMITHING);
	int startExp = skills.getCurrentSkillExp(Constants.STAT_SMITHING);
	long expraise = skills.getCurrentSkillExp(Constants.STAT_SMITHING)
			- startExp;
	double barxp;
	int midprice;
	int profit;
	boolean bronzebaruse;

	private int antiBan() {
		final int random = random(1, 8);
		switch (random) {
		case 1:
			final int x = random(0, 750);
			final int y = random(0, 500);
			if (random(1, 10) == 5) {
				moveMouse(0, 0, x, y);
			}
			return random(1000, 1500);
		case 2:
			if (getCurrentTab() != Constants.TAB_INVENTORY) {
				openTab(Constants.TAB_INVENTORY);
				return random(500, 750);
			} else {
				return random(500, 750);
			}
		case 3:
			if (random(1, 50) == 30) {
				if (getMyPlayer().isMoving()) {
					return random(750, 1000);
				}
				if (getCurrentTab() != Constants.TAB_STATS) {
					openTab(Constants.TAB_STATS);
				}
				moveMouse(665, 260, 40, 20);
				wait(random(1000, 2000));
				openTab(Constants.TAB_INVENTORY);
				return random(100, 200);
			}
		case 4:
			if (random(1, 20) == 10) {
				int angle = getCameraAngle() + random(-90, 90);
				if (angle < 0) {
					angle = 0;
				}
				if (angle > 359) {
					angle = 0;
				}
				setCameraRotation(angle);
				return random(500, 750);
			}
		}
		return 500;
	}

	public boolean checkInventory() {
		int i, current;
		int[] inv;
		inv = getInventoryArray();
		for (i = 0; i <= inv.length - 1; i++) {
			current = inv[i];
			if (current != 0 && current != firstore && current != secondore) {
				return false;
			}
		}
		return true;
	}

	public int checkRest() {
		if (getEnergy() >= runEnergy) {
			resting = false;
			return random(200, 300);
		}
		return random(200, 300);
	}

	private boolean energyCheck() {
		try {
			if (runEnergy() <= minEnergy && rest) {
				minEnergy = random(1, 10);
				if (bank.isOpen()) {
					bank.close();
				}
				while (!resting) {
					clickMouse(720, 110, 10, 10, false);
					wait(random(85, 165));
					if (atMenu("Rest")) {
						resting = true;
					} else {
						if (bank.isOpen()) {
							bank.close();
						} else {
							moveMouse(random(400, 600), random(1, 200));
						}
					}
				}
				return false;
			} else {
				if (runEnergy() >= runEnergy && !isRunning()) {
					runEnergy = random(25, 45);
					setRun(true);
					return true;
				}
				return true;
			}
		} catch (final Exception e) {
			return false;
		}
	}

	public void expcalc() {
		if (expraise < 0) {
			expraise = 0;
		}
	}

	public boolean hassecondore() {
		return secondore == 0 || getInventoryCount(secondore) >= min;
	}

	public boolean isSmelting() {
		int i, j;
		j = 0;
		for (i = 1; i <= 10; i++) {
			if (getMyPlayer().getAnimation() == smelting) {
				j = j + 1;
			}
			wait(random(50, 100));
		}
		return j > 0;
	}

	public void cameraheight() {
		if (Bot.getClient().getCamPosZ() > -1900) {
			setCameraAltitude(true);
		}
	}

	@Override
	public int loop() {

		cameraheight();

		if (!isLoggedIn()) {
			return random(100, 200);
		}
		if (bnk == 0) {
			if (getMyPlayer().getLocation().getX() >= 3255) {
				TB = TA;
				bnk = Abnk;
				ToBank = TBA;
				ToFurnace = TFA;
			} else {
				if (getMyPlayer().getLocation().getX() <= 2985) {
					TB = TF;
					bnk = Fbnk;
					ToBank = TBF;
					ToFurnace = TFF;
				} else {
					if (getMyPlayer().getLocation().getX() >= 3080) {
						TB = TE;
						bnk = Ebnk;
						ToBank = TBE;
						ToFurnace = TFE;
					} else {
						// thx to Genka for edgeville
						log("Not at a place to smelt.");
						stopScript();
					}
				}
			}
		}
		if (usering && ringcheck) {
			getring = getEquipmentCount(ring) == 0;
			if (!getring) {
				ringcheck = false;
			}
		}

		if (isSmelting() && !getring) {
			antiBan();
			return random(100, 200);
		}
		if (resting) {
			return checkRest();
		}
		if (inventoryContains(firstore) && hassecondore() && !getring) {
			final RSObject furn = getNearestObjectByID(furnace);
			if (furn != null && distanceTo(TB) <= 10) {
				if (getDestination() != null) {
					if (getDestination().getX() > TB.getX() + 2
							|| getDestination().getX() < TB.getX() - 2
							|| getDestination().getY() > TB.getY() + 2
							|| getDestination().getY() < TB.getY() - 2) {
						walkTile(randomizeTile(TB, 2, 2));
						return random(150, 250);
					}
				}
				if (!isSmelting() && !getMyPlayer().isMoving()) {
					if (RSInterface.getInterface(311).isValid()) {
						final RSInterfaceChild hitme = getInterface(311,
								toclick);
						if (getInventoryCount(firstore) <= 5) {
							if (!atInterface(hitme, "Smelt 5")) {
								return random(200, 300);
							}
							if (bar == bronzeb) {
								return random(40600, 41000);
							}
							if (bar == goldb || bar == silverb || bar == ironb) {
								return random(81500, 82000);
							}
							if (bar == steelb) {
								return random(23000, 23500);
							}
							if (bar == mithrilb) {
								return random(14500, 15000);
							}
							if (bar == adamantb) {
								return random(11700, 12000);
							}
							if (bar == runiteb) {
								return random(8700, 8900);
							}
						}
						if (getInventoryCount(firstore) <= 10) {
							if (!atInterface(hitme, "Smelt 10")) {
								return random(200, 300);
							}
							if (bar == bronzeb) {
								return random(40600, 41000);
							}
							if (bar == goldb || bar == silverb || bar == ironb) {
								return random(81500, 82000);
							}
							if (bar == steelb) {
								return random(23000, 23500);
							}
							if (bar == mithrilb) {
								return random(14500, 15000);
							}
							if (bar == adamantb) {
								return random(11700, 12000);
							}
							if (bar == runiteb) {
								return random(8700, 8900);
							}
						}
						if (!atInterface(hitme, "Smelt X")) {
							return random(200, 300);
						}
						wait(random(650, 950));
						sendText(String.valueOf(tosmelt), true);
						return random(2000, 3000);
					} else {
						final RSObject frn = getNearestObjectByID(furnace);
						if (frn == null) {
							return random(40, 100);
						}
						if (distanceTo(frn.getLocation()) <= 5) {
							if (onTile(frn.getLocation(), "Furnace", "Smelt",
									0.5, 0.5, 0)) {
								return random(100, 150);
							} else {
								int angle = getCameraAngle() + random(-90, 90);
								if (angle < 0) {
									angle = 0;
								}
								if (angle > 359) {
									angle = 0;
								}
								setCameraRotation(angle);
								return random(100, 150);
							}
						} else {
							walkTile(randomizeTile(TB, 1, 1));
							return random(100, 150);
						}
					}
				}
			} else {
				if (!energyCheck()) {
					return random(200, 300);
				}
				if (needToWalk()) {
					walkPath(ToFurnace, false);
				}
			}
		} else {
			if (getInventoryCount(ring) > 0) {
				if (atInventoryItem(ring, "Wear")) {
					ringcheck = false;
				}
				return random(175, 275);
			}
			final RSObject bn = getNearestObjectByID(bnk);
			if (bn != null && distanceTo(bn) <= 10) {
				if (!getMyPlayer().isMoving()) {
					if (RSInterface.getInterface(762).isValid()) {
						if (getInventoryCount(bar) > 0
								|| getInventoryCount(firstore) > firstamount
								|| getInventoryCount(secondore) > 27 || getring
								&& getInventoryCount() >= 28) {
							bank.depositAll();
							return random(75, 145);
						} else {
							if (bank.getCount(firstore)
									+ getInventoryCount(firstore) == 0
									|| bank.getCount(secondore)
											+ getInventoryCount(secondore) < min
									|| getring && bank.getCount(ring) == 0) {
								log("Out of ores"
										+ (usering ? " or rings." : "."));
								bank.close();
								stopScript();
							}
							if (getring) {
								bank.atItem(ring, "Withdraw-1");
								wait(random(200, 350));
								bank.close();
								return random(200, 300);
							}
							if (getInventoryCount(firstore) == 0) {
								if (bank.atItem(firstore, "Withdraw-"
										+ firstamount)) {
									return random(100, 200);
								} else {
									bank.atItem(firstore, "Withdraw-X");
									wait(random(1000, 1500));
									sendText(String.valueOf(firstamount), true);
									if (secondamount == 0) {
										while (!woot && g <= 10) {
											wait(random(30, 60));
											g++;
										}
										g = 0;
										woot = false;
										wait(random(500, 700));
										if (isInventoryFull()) {
											if (!checkInventory()) {
												bank.depositAll();
												return random(75, 175);
											}
										}
									}
									return random(75, 175);
								}
							}
							if (secondamount > 0) {
								if (getInventoryCount(secondore) != secondamount) {
									bank.atItem(secondore, "Withdraw-All");
									if (bronzebaruse) {
										bank.close();
									}
									while (!woot && g <= 10) {
										wait(random(60, 90));
										g++;
									}
									g = 0;
									woot = false;
									wait(random(300, 500));
									if (isInventoryFull()) {
										if (!checkInventory()) {
											bank.depositAll();
											return random(85, 175);
										}
										return random(100, 200);
									}
								}
							}
						}
					} else {
						final RSObject bk = getNearestObjectByID(bnk);
						if (!tileOnScreen(bk.getLocation())) {
							walkTile(randomizeTile(bk.getLocation(), 1, 1));
							return random(85, 175);
						} else {
							if (!openBank(bnk)) {
								int angle = getCameraAngle() + random(-90, 90);
								if (angle < 0) {
									angle = 0;
								}
								if (angle > 359) {
									angle = 0;
								}
								setCameraRotation(angle);
							}
							return random(100, 200);
						}
					}
				}
			} else {
				if (!energyCheck()) {
					return random(200, 300);
				}
				if (needToWalk()) {
					walkPath(ToBank, false);
				}
			}
		}
		return random(100, 200);
	}

	public boolean needToWalk() {
		return !getMyPlayer().isMoving()
				|| distanceTo(getDestination()) <= random(4, 7);
	}

	@Override
	public void onFinish() {
		// Idea from Jacmob
		log
				.info("You have gained "
						+ (skills.getCurrentSkillExp(Constants.STAT_SMITHING) - startExp)
						+ " XP and "
						+ (skills.getCurrentSkillLevel(Constants.STAT_SMITHING) - startLvl)
						+ " smithing levels.");
		log.info("You have made a profit of " + done * profit + " GP");
	}

	public void onRepaint(final Graphics g) {
		if (isLoggedIn()) {
			if (startTime == 0) {
				startTime = System.currentTimeMillis();
			}
			if (startLvl <= 0 || startExp <= 0) {
				startLvl = skills.getCurrentSkillLevel(Constants.STAT_SMITHING);
				startExp = skills.getCurrentSkillExp(Constants.STAT_SMITHING);
			}
			long millis = System.currentTimeMillis() - startTime;
			final long hours = millis / (1000 * 60 * 60);
			millis -= hours * 1000 * 60 * 60;
			final long minutes = millis / (1000 * 60);
			millis -= minutes * 1000 * 60;
			final long seconds = millis / 1000;
			final int x = 294;// upper left x location
			int y = 4;// upper left y location
			final int xl = 222;// length
			final int yl = 142;// height
			g.setColor(new Color(0, 0, 0, 175));
			g.fillRect(x, y, xl, yl);
			g.setColor(new Color(29, 177, 0));
			g.drawRect(x, y, xl, yl);
			g.setFont(new Font("Calibri", Font.PLAIN, 13));
			g.drawString("SpeedSmelter " + properties.version(), x + 10,
					y += 15);
			g.drawString(
					"Running for " + hours + ":" + minutes + ":" + seconds,
					x + 10, y += 15);
			g.drawString("Level: "
					+ skills.getCurrentSkillLevel(Constants.STAT_SMITHING)
					+ " || Bars to Lvl: "
					+ (int) Math.round(skills
							.getXPToNextLevel(Constants.STAT_SMITHING)
							/ barxp), x + 10, y += 15);
			g
					.drawString(
							"Levels up: "
									+ (skills
											.getCurrentSkillLevel(Constants.STAT_SMITHING) - startLvl),
							x + 10, y += 15);
			g.setColor(new Color(19, 92, 0));
			g.fillRect(x + 11, y + 4, (skills
					.getPercentToNextLevel(Constants.STAT_SMITHING) * 2), 13);
			g.setColor(new Color(29, 177, 0));
			g.drawString(skills.getPercentToNextLevel(Constants.STAT_SMITHING)
					+ "%", x + 105, y + 15);
			g.drawRect(x + 11, y + 4, 200, 13);
			y = y + 15;
			g
					.drawString(
							"Gained exp: "
									+ (skills
											.getCurrentSkillExp(Constants.STAT_SMITHING) - startExp)
									+ " || Exp/Hour: "
									+ (int) ((skills
											.getCurrentSkillExp(Constants.STAT_SMITHING) - startExp) * 3600000D / ((double) System
											.currentTimeMillis() - (double) startTime)),
							x + 10, y += 15);
			g.drawString("Smelted "
					+ done
					+ " Bars"
					+ " || Bars/Hour : "
					+ (int) (done * 3600000D / ((double) System
							.currentTimeMillis() - (double) startTime)),
					x + 10, y += 15);
			g.drawString("Profit: "
					+ done
					* profit
					+ " || Profit/Hour: "
					+ (int) (profit * done * 3600000D / ((double) System
							.currentTimeMillis() - (double) startTime)),
					x + 10, y += 15);
			g.drawString("Profit/Bar: " + profit, x + 10, y += 15);
		}
	}

	@Override
	public boolean onStart(final Map<String, String> args) {

		if (args.get("bar").equals("Bronze")) {
			min = 1;
			tosmelt = random(14, 99);
			toclick = 3;
			barname = "bronze";
			bar = bronzeb;
			firstore = tin;
			secondore = copper;
			firstamount = 14;
			secondamount = 14;
			barxp = 6.2;
			loadGEInfo(copper, bronzeb, tin, 1);
			bronzebaruse = true;
		}
		if (args.get("bar").equals("Iron")) {
			min = 0;
			tosmelt = random(28, 99);
			toclick = 5;
			barname = "iron";
			bar = ironb;
			firstore = iron;
			secondore = 0;
			firstamount = 28;
			secondamount = 0;
			barxp = 12.5;
			loadGEInfo(iron, ironb);
			bronzebaruse = false;
		}
		if (args.get("bar").equals("Silver")) {
			min = 0;
			tosmelt = random(28, 99);
			toclick = 6;
			barname = "silver";
			bar = silverb;
			firstore = silver;
			secondore = 0;
			firstamount = 28;
			secondamount = 0;
			barxp = 13.7;
			loadGEInfo(silver, silverb);
			bronzebaruse = false;
		}
		if (args.get("bar").equals("Steel")) {
			min = 2;
			tosmelt = 10;
			toclick = 7;
			barname = "steel";
			bar = steelb;
			firstore = iron;
			secondore = coal;
			firstamount = 10;
			secondamount = 18;
			barxp = 17.5;
			loadGEInfo(iron, steelb, coal, 2);
			bronzebaruse = false;
		}
		if (args.get("bar").equals("Gold")) {
			min = 0;
			tosmelt = random(28, 99);
			toclick = 8;
			barname = "gold";
			bar = goldb;
			firstore = gold;
			secondore = 0;
			firstamount = 28;
			secondamount = 0;
			barxp = 22.5;
			loadGEInfo(gold, goldb, coal, 0);
			bronzebaruse = false;
			if (args.get("gold") != null) {
				barxp = 56.2;
			}
		}
		if (args.get("bar").equals("Mithril")) {
			min = 4;
			tosmelt = 5;
			toclick = 9;
			barname = "mithril";
			bar = mithrilb;
			firstore = mithril;
			secondore = coal;
			firstamount = 5;
			secondamount = 20;
			barxp = 30;
			loadGEInfo(mithril, mithrilb, coal, 4);
			bronzebaruse = false;
		}
		if (args.get("bar").equals("Adamant")) {
			min = 6;
			tosmelt = 5;
			toclick = 10;
			barname = "adamantite";
			bar = adamantb;
			firstore = adamant;
			secondore = coal;
			firstamount = 4;
			secondamount = 24;
			barxp = 37.5;
			loadGEInfo(adamant, adamantb, coal, 6);
			bronzebaruse = false;
		}
		if (args.get("bar").equals("Runite")) {
			min = 8;
			tosmelt = 5;
			toclick = 11;
			barname = "runite";
			bar = runiteb;
			firstore = runite;
			secondore = coal;
			firstamount = 3;
			secondamount = 24;
			barxp = 50;
			loadGEInfo(runite, runiteb, coal, 8);
			bronzebaruse = false;
		}
		if (args.get("rng") != null) {
			usering = true;
		}
		return true;
	}

	private void loadGEInfo(final int oreID, final int barID) {
		loadGEInfo(oreID, barID, 0, 0);
	}

	private void loadGEInfo(final int oreID, final int barID, final int secID,
			final int secCount) {
		midprice = profit = 1;
		new Thread(new Runnable() {
			public void run() {
				final int bar = grandExchange.loadItemInfo(barID)
						.getMarketPrice();
				final int ore = grandExchange.loadItemInfo(oreID)
						.getMarketPrice();
				midprice = bar;
				if (secCount > 0) {
					final int secondore = grandExchange.loadItemInfo(secID)
							.getMarketPrice();
					profit = midprice - (secondore * secCount + ore);
				} else {
					profit = bar - ore;
				}
			}
		}).start();
	}

	public boolean onTile(final RSTile tile, final String search,
			final String action, final double dx, final double dy,
			final int height) {
		if (!tile.isValid()) {
			return false;
		}
		Point checkScreen;
		checkScreen = Calculations.tileToScreen(tile, dx, dy, height);
		if (!pointOnScreen(checkScreen)) {
			walkTile(tile);
			wait(random(340, 1310));
		}
		try {
			Point screenLoc;
			for (int i = 0; i < 30; i++) {
				screenLoc = Calculations.tileToScreen(tile, dx, dy, height);
				if (!pointOnScreen(screenLoc)) {
					return false;
				}
				if (getMenuItems().get(0).toLowerCase().contains(
						search.toLowerCase())) {
					break;
				}
				if (getMouseLocation().equals(screenLoc)) {
					break;
				}
				moveMouse(screenLoc);
			}
			if (getMenuItems().get(0).toLowerCase().contains(
					action.toLowerCase())) {
				clickMouse(true);
				return true;
			} else {
				clickMouse(false);
				return atMenu(action);
			}
		} catch (final Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean openBank(final int obj) {
		if (getNearestObjectByID(obj).getLocation() == null) {
			return false;
		}
		final RSTile tile = getNearestObjectByID(obj).getLocation();
		final Point location = Calculations.tileToScreen(tile);
		if (pointOnScreen(location)) {
			if (!getMyPlayer().isMoving()) {
				if (onTile(tile, "Bank booth", "Use-quickly", 0.5, 0.5, 0)) {
					return true;
				}
				wait(random(500, 780));
			}
		} else {
			if (!getMyPlayer().isMoving()) {
				walkTile(tile);
				return false;
			}
		}
		return false;
	}

	private int runEnergy() {
		return Integer
				.parseInt(RSInterface.getChildInterface(750, 5).getText());
	}

	public void serverMessageRecieved(final ServerMessageEvent e) {
		final String message = e.getMessage();
		if (message.contains("You retrieve a bar of " + barname)) {
			done++;
		} else if (message
				.contains("You don't have enough inventory space to withdraw that many.")) {
			woot = true;
		} else if (message.toLowerCase().contains("ring of forging")) {
			ringcheck = true;
		}
	}

	@Override
	public boolean walkTo(final RSTile t, final int x, final int y) {
		final Point p = tileToMinimap(t);
		if (p.x == -1 || p.y == -1) {
			return walkTo(getClosestTileOnMap(t), x, y);
		}
		clickMouse(p, x, y, true);
		return true;
	}

	private void walkPath(final RSTile[] path, final boolean reverse) {
		if (!reverse) {
			if (!getMyPlayer().isMoving()
					|| distanceTo(getDestination()) <= random(7, 8)) {
				walkPathMM(path);
			}
		} else {
			if (!getMyPlayer().isMoving()
					|| distanceTo(getDestination()) <= random(7, 8)) {
				walkPathMM(reversePath(path));
			}
		}
	}

	private void walkTile(final RSTile tile) {
		final Point screen = Calculations.tileToScreen(tile);
		if (pointOnScreen(screen)) {
			moveMouse(screen, 5, 5);
			onTile(tile, "here", "Walk", 0.5, 0.5, 0);
		} else {
			walkTo(tile);
		}
	}
}