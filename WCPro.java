/* WCPro Version 4.11
 * Script support and updates by Taha/Jacmob
 * GUI Help from Pauwelz & Zenzie <3
 * Script base by Deviant
 * Code formatting by Fusion89k
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import org.rsbot.bot.Bot;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Constants;
import org.rsbot.script.GEItemInfo;
import org.rsbot.script.Methods;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSItemTile;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSPlayer;
import org.rsbot.script.wrappers.RSTile;
import org.rsbot.util.GlobalConfiguration;

@ScriptManifest(authors = { "Taha", "Jacmob", "Deviant", "Fusion89k" }, category = "Woodcutting", name = "WCPro", version = 4.11, description = "<html><body style='font-family: Arial; margin: 10px;'><span style='color: #00AA00; font-weight: bold;'>WCPro</span>&nbsp;<strong>Version:&nbsp;4.11</strong><br />Select your account and press OK to configure the script settings.</body></html>")
public class WCPro extends Script implements PaintListener {

	public enum Action {
		OPENDOOR, ANTIBAN, BANK, WALKTOTREES, WALKTOBANK, WAITING, CHOP, DROP, WITHDRAW
	}

	WCProGUI gui;

	public final int[] axeIDs = { 1351, 1349, 1353, 1361, 1355, 1357, 1359,
			4031, 6739, 13470, 14108 }, nestIDs = { 5070, 5071, 5072, 5073,
			5074, 5075, 5076, 7413, 11966 };

	public int[] treesID;

	public final int YEW_ITEM_ID = 1515, TREE_ITEM_ID = 1511,
			WILLOW_ITEM_ID = 1519, MAGIC_ITEM_ID = 1513, OAK_ITEM_ID = 1521;
	public int GambleInt, GambleInt1, startingLevel, lastLevel,
			startingExperience, randomBooth, bankerID, lastExp, currenttab,
			lastTreeIndex, Door, exp = 1, price, nests, chopped, level,
			maxTreeDist = 18, stopVar;
	public boolean booth, dMages, ssMages, clickA, deposit, edge, chest, GE,
			sMages, sYews, depositItem, start, runScript = true, autoChop,
			walkedToWaitTile, power, checkLevel, checkedEquipment, checkedAxe;
	public double x1, x2 = 1.5;
	public RSObject tree;
	public RSNPC npc;
	public RSTile[] bankToTrees, treesToBank, treeLocs, bankTile;

	public Color BG = new Color(159, 189, 209, 150), status = new Color(159,
			189, 209), font = new Color(0, 0, 0, 180), DarkGreen;
	public long startTime = 0;
	public String[] names, chat = new String[100], chat2 = new String[100],
			oldQ;

	public String message, currentState = "Starting up...", chopping = "--";

	public final File settingsFile = new File(new File(
			GlobalConfiguration.Paths.getSettingsDirectory()), "WCProS.txt");

	public boolean atChest(final RSTile tile, final String action) {
		try {
			final Point location = Calculations.tileToScreen(tile);
			if (location.x == -1 || location.y == -1) {
				return false;
			}
			moveMouse(location, 3, 3);
			clickMouse(true);
			return true;
		} catch (final Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean atGate(final RSTile tile, final String action) {
		try {
			final Point location = Calculations.tileToScreen(tile.getX(), tile
					.getY(), 0.5, 1, 0);

			if (location.x == -1 || location.y == -1) {
				return false;
			}
			moveMouse(location, 3, 3);
			clickMouse(true);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean atTree(final RSTile tile) {
		try {
			final Point location = Calculations.tileToScreen(tile.getX(), tile
					.getY(), x1, x2, 0);

			if (location.x == -1 || location.y == -1 || location.x > 513
					|| location.y > 335 || location.x < 0 || location.y < 0) {
				return false;
			}
			if (getMenuActions().get(0).toLowerCase().contains("down")) {
				clickMouse(true, 10);
				wait(random(1000, 2000));
				return true;
			}
			moveMouse(location, 6, 6);
			if (getMyPlayer().isMoving()) {
				if (getMenuActions().get(0).toLowerCase().contains("down")) {
					if (edge) {
						atMenu("down");
					} else {
						clickMouse(true);
					}
					wait(random(1000, 2000));
					return true;
				}
			}
			return false;
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	// Keeps finding midtile until the tile is onMap
	public RSTile checkTile(final RSTile tile) {
		if (distanceTo(tile) < 17) {
			return tile;
		}
		final RSTile loc = getMyPlayer().getLocation();
		final RSTile walk = new RSTile((loc.getX() + tile.getX()) / 2, (loc
				.getY() + tile.getY()) / 2);
		return distanceTo(tile) < 17 ? walk : checkTile(walk);
	}

	public int closestTree(final RSTile[] tiles) {
		int closest = -1;
		for (int i = 0; i < tiles.length; i++) {
			final RSTile tile = tiles[i];
			if (getObjectAt(tile) != null) {
				if (closest == -1 && isTree(getObjectAt(tile).getID())) {
					closest = i;
					continue;
				}
				if (closest == -1) {
					continue;
				}
				final RSObject treeObj = getObjectAt(tile);
				if (treeObj != null
						&& isTree(treeObj.getID())
						&& distanceTo(treeObj) < distanceTo(getObjectAt(tiles[closest]))) {
					closest = i;
				}
			}
		}
		return closest;
	}

	public void depositAllBut(final int... items) {
		int inventory[] = getInventoryArray();
		if (inventory == null || items == null) {
			return;
		}
		for (int index = 0; index < inventory.length; ++index) {
			inventory = getInventoryArray();
			if (inventory == null || items == null) {
				return;
			}
			if (getInventoryCount() < 2) {
				return;
			}
			depositItem = true;
			if (inventory[index] == -1) {
				depositItem = false;
			}
			for (int item = 0; item < items.length; ++item) {
				if (inventory[index] == items[item]) {
					depositItem = false;
				}
			}
			if (depositItem) {
				clickMouse(getDepositInventoryItemPoint(index), false);
				atMenu("Deposit-All");
				wait(random(500, 650));
			}
		}
	}

	public RSTile findBusyTree() {
		int closestTo1 = 0;
		int closestTo2 = 0;
		int closestTo3 = 0;
		try {
			final int[] validPlayers = Bot.getClient().getRSPlayerIndexArray();
			final org.rsbot.accessors.RSPlayer[] players = Bot.getClient()
					.getRSPlayerArray();

			for (final int x : validPlayers) {
				final RSPlayer player = new RSPlayer(players[x]);
				if (Methods.distanceBetween(player.getLocation(), treeLocs[0]) <= 4) {
					closestTo1++;
				}
				if (Methods.distanceBetween(player.getLocation(), treeLocs[1]) <= 4) {
					closestTo2++;
				}
				if (sMages || sYews || GE) {
					if (Methods.distanceBetween(player.getLocation(),
							treeLocs[2]) <= 4) {
						closestTo3++;
					}
				}
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
		if (closestTo1 > closestTo2 && closestTo1 > closestTo3) {
			return treeLocs[0];
		}
		if (closestTo2 > closestTo1 && closestTo2 > closestTo3) {
			return treeLocs[1];
		}
		if (closestTo3 > closestTo2 && closestTo3 > closestTo1) {
			return treeLocs[2];
		} else {
			return treeLocs[0];
		}
	}

	public Action getAction() {
		try {
			if (lastLevel != skills
					.getCurrentSkillExp(Constants.STAT_WOODCUTTING)) {
				lastLevel = skills
						.getCurrentSkillExp(Constants.STAT_WOODCUTTING);
				checkLevel = true;
			}
			if (autoChop && checkLevel) {
				if (skills.getCurrentSkillLevel(Constants.STAT_WOODCUTTING) < 15) {
					booth = true;
					final GEItemInfo tree = grandExchange
							.loadItemInfo(TREE_ITEM_ID);
					price = tree.getMarketPrice();
					exp = 25;
					x1 = 1;
					x2 = 1;
					bankToTrees = new RSTile[] { new RSTile(3093, 3243),
							new RSTile(3102, 3250), new RSTile(3112, 3257) };
					treeLocs = new RSTile[] { new RSTile(3110, 3257),
							new RSTile(3111, 3259), new RSTile(3116, 3260),
							new RSTile(3114, 3256), new RSTile(3118, 3255),
							new RSTile(3115, 3253) };
					bankerID = 2213;
					bankTile = new RSTile[] { new RSTile(3167, 3489) };
					treesID = new int[] { 1278, 1276 };
					treesToBank = reversePath(bankToTrees);
					checkLevel = false;
					chopping = "Trees";
				} else {
					if (skills.getCurrentSkillLevel(Constants.STAT_WOODCUTTING) >= 15
							&& skills
									.getCurrentSkillLevel(Constants.STAT_WOODCUTTING) < 30) {
						booth = true;
						final GEItemInfo oak = grandExchange
								.loadItemInfo(OAK_ITEM_ID);
						price = oak.getMarketPrice();
						exp = 37;
						x1 = 1.5;
						bankToTrees = new RSTile[] { new RSTile(3092, 3245),
								new RSTile(3100, 3245) };
						treeLocs = new RSTile[] { new RSTile(3102, 3242),
								new RSTile(3107, 3248) };
						bankerID = 2213;
						bankTile = new RSTile[] { new RSTile(3091, 3245),
								new RSTile(3091, 3243), new RSTile(3091, 3242) };
						treesID = new int[] { 1281, 1212 };
						treesToBank = reversePath(bankToTrees);
						checkLevel = false;
						chopping = "Oaks";
					}
					if (skills.getCurrentSkillLevel(Constants.STAT_WOODCUTTING) >= 30
							&& skills
									.getCurrentSkillLevel(Constants.STAT_WOODCUTTING) < 60) {
						final GEItemInfo willow = grandExchange
								.loadItemInfo(WILLOW_ITEM_ID);
						price = willow.getMarketPrice();

						exp = 68;
						x1 = 1.2;
						x2 = 1.2;
						bankTile = new RSTile[] { new RSTile(3091, 3245),
								new RSTile(3091, 3243), new RSTile(3091, 3242) };
						bankToTrees = new RSTile[] { new RSTile(3092, 3245),
								new RSTile(3086, 3233) };
						treeLocs = new RSTile[] { new RSTile(3089, 3234),
								new RSTile(3086, 3236), new RSTile(3086, 3237),
								new RSTile(3084, 3238), new RSTile(3088, 3232),
								new RSTile(3089, 3227) };
						booth = true;
						bankerID = 2213;
						treesID = new int[] { 5553, 5551, 5552 };
						treesToBank = reversePath(bankToTrees);
						checkLevel = false;
						chopping = "Willows";
					}
					if (skills.getCurrentSkillLevel(Constants.STAT_WOODCUTTING) >= 60) {
						booth = true;
						final GEItemInfo yew = grandExchange
								.loadItemInfo(YEW_ITEM_ID);
						price = yew.getMarketPrice();
						exp = 175;
						x1 = 1.5;
						bankTile = new RSTile[] { new RSTile(3011, 3354) };
						bankToTrees = new RSTile[] { new RSTile(3012, 3355),
								new RSTile(3012, 3358), new RSTile(3007, 3348),
								new RSTile(3006, 3342), new RSTile(3006, 3334),
								new RSTile(3006, 3329), new RSTile(3007, 3323),
								new RSTile(3004, 3316), new RSTile(2999, 3313),
								new RSTile(2989, 3308), new RSTile(2985, 3306),
								new RSTile(2978, 3294), new RSTile(2971, 3293),
								new RSTile(2963, 3289), new RSTile(2961, 3284),
								new RSTile(2960, 3277), new RSTile(2960, 3271),
								new RSTile(2961, 3264), new RSTile(2961, 3258),
								new RSTile(2960, 3249), new RSTile(2953, 3242),
								new RSTile(2947, 3238), new RSTile(2943, 3234) };
						treeLocs = new RSTile[] { new RSTile(2936, 3230),
								new RSTile(2935, 3226), new RSTile(2934, 3234),
								new RSTile(2940, 3233) };
						bankerID = 11758;
						treesID = new int[] { 1309 };
						treesToBank = reversePath(bankToTrees);
						checkLevel = false;
						chopping = "Yews";
					}
				}
			}
			final int treeIndex = closestTree(treeLocs);

			if (treeIndex == -1 && distanceTo(treeLocs[0]) < maxTreeDist) {
				if (getInventoryCount() > 3 && power) {
					return Action.DROP;
				} else if (!isInventoryFull()) {
					return Action.WAITING;
				}
			}
			if (Door != 0
					&& getNearestObjectByID(Door) != null
					&& distanceTo(getNearestObjectByID(Door).getLocation()) < 14
					&& currentState != "Chopping..." && (deposit || chest)) {
				return Action.OPENDOOR;
			}
			if (isInventoryFull() && power) {
				return Action.DROP;
			}
			if (getInventoryCount() > 12
					&& (tileOnScreenDeviant(bankTile[randomBooth]) || bank
							.isOpen())) {
				return Action.BANK;
			}
			if (!checkedAxe) {
				if (playerHasOneOf(axeIDs)) {
					checkedAxe = true;
				} else {
					if (isInventoryFull()
							&& distanceTo(bankTile[randomBooth]) > 5) {
						return Action.WALKTOBANK;
					} else {
						return Action.WITHDRAW;
					}
				}
			}
			if (!isInventoryFull() && distanceTo(treeLocs[0]) > maxTreeDist) {
				return Action.WALKTOTREES;
			}
			if (isInventoryFull() && distanceTo(bankTile[randomBooth]) > 5) {
				return Action.WALKTOBANK;
			}
			if (treeIndex != -1
					&& distanceTo(treeLocs[treeIndex]) <= maxTreeDist
					&& getMyPlayer().getAnimation() == -1) {
				return Action.CHOP;
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return Action.ANTIBAN;
	}

	public RSObject getClosestTreeByID(final int... ids) {
		RSObject cur = null;
		double dist = -1;

		for (int x = 0; x < 104; x++) {
			outer: for (int y = 0; y < 104; y++) {
				final RSObject o = getObjectAt(x + Bot.getClient().getBaseX(),
						y + Bot.getClient().getBaseY());

				if (o != null) {
					boolean isObject = false;
					for (final int id : ids) {
						if (o.getID() == id) {
							isObject = true;
							break;
						}
					}
					if (isObject) {
						final RSObject tl = getObjectAt(x
								+ Bot.getClient().getBaseX() - 1, y
								+ Bot.getClient().getBaseY());
						final RSObject tb = getObjectAt(x
								+ Bot.getClient().getBaseX(), y
								+ Bot.getClient().getBaseY() - 1);
						final int id = o.getID();
						if (tl != null && tl.getID() == id || tb != null
								&& tb.getID() == id) {
							continue outer;
						}
						final double distTmp = calculateDistance(getMyPlayer()
								.getLocation(), o.getLocation());

						if (cur == null || distTmp < dist) {
							dist = distTmp;
							cur = o;
						}
					}
				}
			}
		}
		return cur;
	}

	public Point getDepositInventoryItemPoint(final int invIndex) {
		final int col = invIndex % 7;
		final int row = invIndex / 7;
		final int x = 121 + col * 48;
		final int y = 85 + row * 50;
		return new Point(x, y);
	}

	public boolean integerArrayContains(final int[] a, final int searchInt) {
		for (int e = 0; e < a.length + 1; e++) {
			if (searchInt == a[e]) {
				return true;
			}
		}
		return false;
	}

	public boolean isMoving() {
		if (!getMyPlayer().isMoving()) {
			return false;
		}
		if (getDestination() != null) {
			if (distanceTo(getDestination()) < 6) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	public boolean isTree(final int treeID) {
		for (final int id : treesID) {
			if (id == treeID) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int loop() {
		try {
			if (getMyPlayer().isInCombat()) {
				currentState = "Evading Combat...";
				if (!isMoving()) {
					walkToBank();
				}
				return random(200, 400);
			}
			int treeIndex;
			setMaxAltitude();
			nest();
			if (getEnergy() > random(30, 50)) {
				setRun(true);
			}
			final Action action = getAction();
			if (action != null) {
				int xx1 = -2, xx2 = 2, yy1 = -2, yy2 = 2;
				switch (action) {
				case CHOP:
					treeIndex = closestTree(treeLocs);

					if (treeIndex != -1 && distanceTo(treeLocs[treeIndex]) < 10
							&& distanceTo(treeLocs[treeIndex]) > 3
							&& getMyPlayer().getAnimation() == -1
							&& !tileOnScreenDeviant(treeLocs[treeIndex])) {
						turnToTile(treeLocs[treeIndex]);
					}

					if (treeIndex != -1 && !isInventoryFull()) {
						if (distanceTo(treeLocs[treeIndex]) < 7) {
							if (getMyPlayer().getAnimation() != -1
									&& lastTreeIndex == treeIndex) {
								return random(200, 300);
							}
							lastTreeIndex = treeIndex;

							if (getMyPlayer().getAnimation() == -1) {
								tree = getClosestTreeByID(treesID);
								if (tree == null) {
									return random(50, 200);
								}
								atTree(tree.getLocation());
								currentState = "Chopping...";
								walkedToWaitTile = false;
								return random(10, 20);
							}
						}

						if (edge) {
							xx1 = 0;
							xx2 = 3;
							if (treeIndex == 1) {
								yy1 = -2;
								yy2 = -4;
							}
							if (treeIndex == 0) {
								yy1 = 2;
								yy2 = 4;
							}
						}

						if (ssMages) {
							if (treeIndex == 0 || treeIndex == 1) {
								xx1 = 2;
								xx2 = 3;
							}
							if (treeIndex == 2 || treeIndex == 3) {
								xx1 = -2;
								xx2 = -3;
							}
						}
						if (distanceTo(treeLocs[treeIndex]) > 4
								&& !getMyPlayer().isMoving()) {
							currentState = "Walking to " + chopping;
							walkTo(checkTile(new RSTile(treeLocs[treeIndex]
									.getX()
									+ random(xx1, xx2), treeLocs[treeIndex]
									.getY()
									+ random(yy1, yy2))));
						}

					}
					break;

				case WALKTOBANK:
					randomBooth = random(0, bankTile.length);
					if (isInventoryFull()
							&& distanceTo(bankTile[randomBooth]) > 5
							&& !isMoving()) {
						currentState = "Walking to bank...";
						walkToBank();
					}
					break;

				case WALKTOTREES:
					if (!isInventoryFull() && distanceTo(treeLocs[0]) > 16
							&& !isMoving()) {
						currentState = "Walking to trees...";
						walkToTrees();
					}
					break;

				case BANK:
					randomBooth = random(0, bankTile.length);
					if (getInventoryCount() > 12
							&& tileOnScreenDeviant(bankTile[randomBooth])
							&& !getMyPlayer().isMoving()) {
						if (booth && !bank.isOpen()) {
							currentState = "Banking...";
							if (!tileOnScreenDeviant(bankTile[randomBooth])) {
								turnToTile(bankTile[randomBooth]);
							}
							if (distanceTo(bankTile[randomBooth]) > 3) {
								walkTo(bankTile[randomBooth]);
							}
							bank.atBankBooth(bankTile[randomBooth],
									"use-quickly");
							return random(500, 800);
						}
						if (chest && !bank.isOpen()) {
							currentState = "Banking...";
							if (!tileOnScreenDeviant(bankTile[randomBooth])) {
								turnToTile(bankTile[randomBooth]);
							}
							atChest(bankTile[randomBooth], "Bank");
							return random(500, 800);
						}
						if (GE && !bank.isOpen()) {
							currentState = "Banking...";
							setCameraRotation(90 + random(-5, 5));
							npc = getNearestNPCByID(bankerID);
							atNPC(npc, "Bank Banker");
							return random(500, 800);
						}
						if (deposit
								&& !RSInterface.getInterface(
										Constants.INTERFACE_DEPOSITBOX)
										.isValid()) {
							currentState = "Banking...";
							atTile(bankTile[randomBooth], "");
							return random(1000, 2000);
						}

						if (RSInterface.getInterface(
								Constants.INTERFACE_DEPOSITBOX).isValid()) {
							if (wieldAxe()) {
								currentState = "Depositing...";
								clickMouse(random(332, 349), random(268, 283),
										true);
								return random(500, 700);
							} else {
								depositAllBut(axeIDs);
								return random(1000, 2000);
							}
						}

						if (bank.isOpen()) {
							if (wieldAxe()) {
								clickMouse(random(430, 458), random(300, 317),
	                                       true);
							} else {
								bank.depositAllExcept(axeIDs);
							}
							return random(500, 700);
						}
					}
					break;

				case WITHDRAW:
					currentState = "Withdrawing an axe...";
					randomBooth = random(0, bankTile.length);
					if (isInventoryFull()
							&& tileOnScreenDeviant(bankTile[randomBooth])
							&& !getMyPlayer().isMoving()) {
						if (!checkedEquipment) {
							openTab(Constants.TAB_EQUIPMENT);
							checkedEquipment = true;
							wait(random(100, 500));
							openTab(Constants.TAB_INVENTORY);
							break;
						}
						if (booth && !bank.getInterface().isValid()) {
							currentState = "Banking...";
							if (!tileOnScreenDeviant(bankTile[randomBooth])) {
								turnToTile(bankTile[randomBooth]);
							}
							if (distanceTo(bankTile[randomBooth]) > 3) {
								walkTo(bankTile[randomBooth]);
							}
							bank.atBankBooth(bankTile[randomBooth],
									"use-quickly");
							return random(500, 800);
						}
						if (chest && !bank.getInterface().isValid()) {
							currentState = "Banking...";
							if (!tileOnScreenDeviant(bankTile[randomBooth])) {
								turnToTile(bankTile[randomBooth]);
							}
							atChest(bankTile[randomBooth], "Bank");
							return random(500, 800);
						}
						if (GE && !bank.getInterface().isValid()) {
							currentState = "Banking...";
							setCameraRotation(90 + random(-5, 5));
							npc = getNearestNPCByID(bankerID);
							atNPC(npc, "Bank Banker");
							return random(500, 800);
						}

						if (deposit
								&& !RSInterface.getInterface(
										Constants.INTERFACE_DEPOSITBOX)
										.isValid()) {
							currentState = "Banking...";
							atTile(bankTile[randomBooth], "");
							return random(1000, 2000);
						}

						if (RSInterface.getInterface(
								Constants.INTERFACE_DEPOSITBOX).isValid()) {
							if (wieldAxe()) {
								currentState = "Depositing...";
								clickMouse(random(332, 349), random(268, 283),
										true);
								return random(500, 700);
							} else {
								depositAllBut(axeIDs);
								return random(1000, 2000);
							}
						}
					}
					if (bank.isOpen()) {
						if (bank.searchItem("hatchet")) {
							int bestAxe = 0;
							for (int a = axeIDs.length - 1; a > -1; --a) {
								if (bank.getItemByID(axeIDs[a]) != null) {
									bestAxe = axeIDs[a];
									break;
								}
							}
							bank.withdraw(bestAxe, 1);
							wait(random(800, 1000));
							if (getInventoryCount(bestAxe) > 0) {
								checkedAxe = true;
								bank.close();
							}
						} else {
							log("Failed to search for a hatchet.");
						}
					}
					break;

				case OPENDOOR:
					if (getNearestObjectByID(Door) != null
							&& currentState != "Chopping..."
							&& currentState != "Walking to " + chopping) {
						currentState = "Opening door...";
						if (chest) {
							openDoor2();
						}
						if (deposit) {
							openDoor();
						}
					}
					break;

				case WAITING:
					treeIndex = closestTree(treeLocs);
					if (treeIndex == -1 && !isInventoryFull()
							&& !getMyPlayer().isMoving()) {
						currentState = "Waiting...";
						final RSTile busyTile = findBusyTree();
						if (clickA) {
							if (sMages || sYews || dMages) {
								if (distanceTo(busyTile) > 4) {
									walkTo(new RSTile(busyTile.getX()
											+ random(-2, 2), busyTile.getY()
											+ random(-2, 2)));
									return random(700, 1000);
								}
							} else if (!walkedToWaitTile) {
								wait(random(100, 2000));
								RSTile randTreeLoc;
								if (treeLocs.length != 2 || lastTreeIndex == -1) {
									randTreeLoc = treeLocs[random(0,
											treeLocs.length)];
								} else {
									randTreeLoc = treeLocs[lastTreeIndex == 0 ? 1
											: 0];
								}

								if (edge) {
									xx1 = 0;
									xx2 = 3;
									if (lastTreeIndex == 1) {
										yy1 = 2;
										yy2 = 4;
									} else {
										yy1 = -1;
										yy2 = -4;
									}
								}

								final RSTile waitTile = new RSTile(randTreeLoc
										.getX()
										+ random(xx1, xx2), randTreeLoc.getY()
										+ random(yy1, yy2));
								if (distanceTo(waitTile) < 3) {
									break;
								}
								walkTo(checkTile(waitTile));
								wait(random(1200, 2500));
								if (treeLocs.length == 2
										&& distanceTo(waitTile) > 5) {
									wait(random(3200, 3500));
									if (distanceTo(waitTile) > 5) {
										walkTo(checkTile(waitTile));
									}
								}
								walkedToWaitTile = true;
							}
						}
					}
					if (random(0, 4) != 1) {
						break;
					}

				case ANTIBAN:
					final int gamble = random(0, 5);
					if (gamble < 2) {
						moveMouse(random(7, 12), random(50, 500), random(100,
								500), 30);
					} else if (gamble == 2) {
						int angle = getCameraAngle() + random(-70, 70);
						if (angle < 0) {
							angle = 0;
						}
						if (angle > 359) {
							angle = 0;
						}
						setCameraRotation(angle);
					}
					break;

				case DROP:
					currentState = "Dropping...";
					dropAllExcept(false, axeIDs);
					break;
				}
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return random(200, 400);
	}

	public void nest() {
		final RSItemTile nest = getGroundItemByID(nestIDs);
		if (nest != null) {
			turnToTile(nest);
			atTile(nest, "Take");
			log("Found a nest!");
			currentState = "Picking up nest...";
			nests++;
		}
	}

	public void onFinish() {
		log("Chopped " + chopped + " " + chopping);
	}

	public void onRepaint(final Graphics g) {
		if (isLoggedIn() && start) {
			long millis = System.currentTimeMillis() - startTime;
			if (startTime == 0) {
				millis = 0;
			}
			if (lastExp == 0) {
				lastExp = skills.getCurrentSkillExp(Constants.STAT_WOODCUTTING);
			}

			if (skills.getCurrentSkillExp(Constants.STAT_WOODCUTTING) > lastExp) {
				lastExp = skills.getCurrentSkillExp(Constants.STAT_WOODCUTTING);
				chopped++;
			}

			if (startingLevel == 0 || startingExperience == 0) {
				startingLevel = skills
						.getCurrentSkillLevel(Constants.STAT_WOODCUTTING);
				startingExperience = skills
						.getCurrentSkillExp(Constants.STAT_WOODCUTTING);
			}
			final long hours = millis / (1000 * 60 * 60);
			millis -= hours * 1000 * 60 * 60;
			final long minutes = millis / (1000 * 60);
			millis -= minutes * 1000 * 60;

			final long seconds = millis / 1000;
			final int topX = 320, topY = 207;
			int y = topY + 5;
			final int x = topX + 5;

			g.setColor(BG);
			g.fill3DRect(topX, topY, 516 - topX, 338 - topY, true);
			g.setFont(new Font("Verdana", Font.BOLD, 12));
			g.setColor(new Color(0, 90, 0, 255));
			g.drawString(getClass().getAnnotation(ScriptManifest.class).name(),
					x, y + 13);
			g.setColor(new Color(0, 60, 0, 255));
			g.drawString("Version "
					+ getClass().getAnnotation(ScriptManifest.class).version(),
					x + 48, y += 13);
			g.setColor(font);
			g.drawString("Runtime: " + hours + "h " + minutes + "min "
					+ seconds + "sec", x, y += 13);
			g.drawString("Chopped: " + chopped + " " + chopping, x, y += 13);
			g.drawString("Collected: " + nests + " Nests", x, y += 13);
			g.drawString("Gained: " + chopped * price + " GP", x, y += 13);
			g
					.drawString(
							"Gained: "
									+ (skills
											.getCurrentSkillLevel(Constants.STAT_WOODCUTTING) - startingLevel)
									+ " Levels", x, y += 13);
			g
					.drawString(
							(int) Math
									.ceil((float) skills
											.getXPToNextLevel(Constants.STAT_WOODCUTTING)
											/ (float) exp)
									+ " "
									+ chopping
									+ " Until Level "
									+ (skills
											.getCurrentSkillLevel(Constants.STAT_WOODCUTTING) + 1)
									+ " WC", x, y += 13);
			final int percentage = skills
					.getPercentToNextLevel(Constants.STAT_WOODCUTTING);
			g.drawString(percentage + "%", x, y += 13);
			final int barx = x + (percentage > 9 ? 39 : 34);
			g.setColor(new Color(255, 0, 0, 160));
			g.fillRect(barx, y -= 9, 110, 9);
			g.setColor(new Color(0, 255, 0, 160));
			g.fillRect(barx, y, (int) (1.1 * percentage), 9);
			g.setColor(new Color(0, 0, 0, 100));
			g.drawRect(barx, y, 110, 9);
			g.setColor(Color.white);
			g.drawString("State: " + currentState, x, y += 23);
		}
	}

	@Override
	public boolean onStart(final Map<String, String> map) {
		try {
			settingsFile.createNewFile();
		} catch (final IOException ignored) {

		}
		log.info("Loading GUI...");
		gui = new WCProGUI(this);
		gui.setLocationRelativeTo(null);
		gui.setVisible(true);
		try {
			new URL("http://www.ipcounter.de/count_js.php?u=63070213")
					.openStream();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		while (!start) {
			wait(500);
		}
		if (runScript) {
			log("WCPro initiliazed!");
			startTime = System.currentTimeMillis();
			return true;
		} else {
			return false;
		}
	}

	public void openDoor() {
		if (!getMyPlayer().isMoving()) {
			if (distanceTo(getNearestObjectByID(Door)) > 2) {
				if (!isInventoryFull()) {
					walkTo(new RSTile(2523, 3571));
				}
				if (isInventoryFull()) {
					walkTo(new RSTile(2521, 3571));
				}
				wait(random(1000, 2000));
			}
		}

		if (!getMyPlayer().isMoving()) {
			setCameraRotation(270 + random(-5, 5));
			atDoor(getNearestObjectByID(Door).getID(), 'e');
			wait(random(1500, 2000));
		}
		if (getMyPlayer().isMoving()) {
			return;
		}

	}

	public void openDoor2() {
		if (!getMyPlayer().isMoving()) {
			if (distanceTo(getNearestObjectByID(27854)) > 2) {
				walkTo(new RSTile(3349, 3279));
				wait(random(1000, 2000));
			} else {
				setCameraRotation(360 + random(-5, 5));
				atGate(getNearestObjectByID(27854).getLocation(), "Open");
				wait(random(1500, 2000));
			}
		}
	}

	public boolean pointOnScreenDeviant(final Point p) {
		return p.x > 20 && p.x < 502 && p.y > 15 && p.y < 330;
	}

	public void setMaxAltitude() {
		Bot.getInputManager().pressKey((char) 38);
		wait(random(500, 1000));
		Bot.getInputManager().releaseKey((char) 38);
	}

	public void start() {
		chopping = gui.treeComboBox.getSelectedItem().toString();
		final String chopLoc = gui.locComboBox.getSelectedItem().toString();

		clickA = gui.smartWalkingCheckBox.isSelected();
		power = gui.powerChopCheckBox.isSelected();
		autoChop = gui.autoChopCheckBox.isSelected();

		// WRITE TO SETTINGS FILE
		try {
			final BufferedWriter out = new BufferedWriter(new FileWriter(
					settingsFile));
			out.write(chopping + ":" + chopLoc + ":"
					+ (clickA ? "true" : "false") + ":"
					+ (power ? "true" : "false") + ":"
					+ (autoChop ? "true" : "false"));
			out.close();
		} catch (final Exception e) {
			e.printStackTrace();
		}
		if (!gui.treeComboBox.isEnabled() || !gui.locComboBox.isEnabled()) {
			start = true;
			return;
		}

		if (chopping.equals("Willows") && chopLoc.equals("Draynor")) {
			final GEItemInfo willow = grandExchange
					.loadItemInfo(WILLOW_ITEM_ID);
			price = willow.getMarketPrice();

			exp = 68;
			x1 = 1.2;
			x2 = 1.2;
			bankTile = new RSTile[] { new RSTile(3091, 3245),
					new RSTile(3091, 3243), new RSTile(3091, 3242) };
			bankToTrees = new RSTile[] { new RSTile(3092, 3245),
					new RSTile(3086, 3233) };
			treeLocs = new RSTile[] { new RSTile(3089, 3234),
					new RSTile(3086, 3236), new RSTile(3086, 3237),
					new RSTile(3084, 3238), new RSTile(3088, 3232),
					new RSTile(3089, 3227) };
			booth = true;
			bankerID = 2213;
			treesID = new int[] { 5553, 5551, 5552 };
			treesToBank = reversePath(bankToTrees);
		}

		if (chopping.equals("Yews") && chopLoc.equals("Grand Exchange")) {
			booth = false;
			final GEItemInfo yew = grandExchange.loadItemInfo(YEW_ITEM_ID);
			price = yew.getMarketPrice();
			exp = 175;
			x1 = 1.5;
			GE = true;
			bankToTrees = new RSTile[] { new RSTile(3168, 3488),
					new RSTile(3174, 3488), new RSTile(3180, 3488),
					new RSTile(3185, 3488), new RSTile(3191, 3488),
					new RSTile(3195, 3491), new RSTile(3196, 3495),
					new RSTile(3209, 3501) };
			treeLocs = new RSTile[] { new RSTile(3210, 3504),
					new RSTile(3205, 3504), new RSTile(3222, 3503) };
			bankerID = 6533;
			bankTile = new RSTile[] { new RSTile(3167, 3489) };
			treesID = new int[] { 1309, 1212 };
			treesToBank = reversePath(bankToTrees);
		}

		if (chopping.equals("Oaks") && chopLoc.equals("Draynor")) {
			booth = true;
			final GEItemInfo oak = grandExchange.loadItemInfo(OAK_ITEM_ID);
			price = oak.getMarketPrice();
			exp = 37;
			x1 = 1.5;
			bankToTrees = new RSTile[] { new RSTile(3092, 3245),
					new RSTile(3100, 3245) };
			treeLocs = new RSTile[] { new RSTile(3102, 3242),
					new RSTile(3107, 3248) };
			bankerID = 2213;
			bankTile = new RSTile[] { new RSTile(3091, 3245),
					new RSTile(3091, 3243), new RSTile(3091, 3242) };
			treesID = new int[] { 1281, 1212 };
			treesToBank = reversePath(bankToTrees);
		}

		if (chopping.equals("Yews") && chopLoc.equals("South Falador")) {
			booth = true;
			final GEItemInfo yew = grandExchange.loadItemInfo(YEW_ITEM_ID);
			price = yew.getMarketPrice();
			exp = 175;
			x1 = 1.5;
			bankTile = new RSTile[] { new RSTile(3011, 3354) };
			bankToTrees = new RSTile[] { new RSTile(3011, 3356),
					new RSTile(3007, 3350), new RSTile(3007, 3342),
					new RSTile(3007, 3330), new RSTile(3007, 3319) };
			treeLocs = new RSTile[] { new RSTile(2997, 3312),
					new RSTile(3020, 3316) };
			bankerID = 11758;
			maxTreeDist = 27;
			treesID = new int[] { 1309, 1212 };
			treesToBank = reversePath(bankToTrees);
		}

		if (chopping.equals("Yews") && chopLoc.equals("Catherby")) {
			booth = true;
			final GEItemInfo yew = grandExchange.loadItemInfo(YEW_ITEM_ID);
			price = yew.getMarketPrice();
			exp = 175;
			x1 = 1.5;
			bankToTrees = new RSTile[] { new RSTile(2808, 3439),
					new RSTile(2805, 3433), new RSTile(2796, 3433),
					new RSTile(2783, 3432), new RSTile(2771, 3430),
					new RSTile(2763, 3429) };
			treeLocs = new RSTile[] { new RSTile(2758, 3434),
					new RSTile(2760, 3428), new RSTile(2761, 3432),
					new RSTile(2756, 3431), new RSTile(2755, 3434),
					new RSTile(2766, 3428) };
			bankerID = 2213;
			bankTile = new RSTile[] { new RSTile(2807, 3442),
					new RSTile(2809, 3442) };
			treesID = new int[] { 1309, 1212 };
			treesToBank = reversePath(bankToTrees);
		}

		if (chopping.equals("Yews") && chopLoc.equals("Seers")) {
			booth = true;
			sYews = true;
			final GEItemInfo yew = grandExchange.loadItemInfo(YEW_ITEM_ID);
			price = yew.getMarketPrice();
			exp = 175;
			x1 = 1.5;
			bankTile = new RSTile[] { new RSTile(2727, 3494),
					new RSTile(2724, 3494) };
			bankToTrees = new RSTile[] { new RSTile(2724, 3492),
					new RSTile(2725, 3477), new RSTile(2721, 3467),
					new RSTile(2715, 3463) };
			treeLocs = new RSTile[] { new RSTile(2715, 3460),
					new RSTile(2706, 3460), new RSTile(2706, 3465) };
			bankerID = 25808;
			treesID = new int[] { 1309, 1212 };
			treesToBank = reversePath(bankToTrees);
		}

		if (chopping.equals("Maples") && chopLoc.equals("Seers")) {
			booth = true;
			price = 35;
			exp = 100;
			x1 = 1.0;
			x2 = 1.5;
			bankTile = new RSTile[] { new RSTile(2727, 3494),
					new RSTile(2724, 3494) };
			bankToTrees = new RSTile[] { new RSTile(2727, 3492),
					new RSTile(2729, 3500) };
			treeLocs = new RSTile[] { new RSTile(2727, 3502),
					new RSTile(2730, 3501), new RSTile(2733, 3500),
					new RSTile(2722, 3501) };
			bankerID = 25808;
			treesID = new int[] { 1307, 1308 };
			treesToBank = reversePath(bankToTrees);
		}

		if (chopping.equals("Magics") && chopLoc.equals("Seers")) {
			booth = true;
			sMages = true;
			final GEItemInfo magic = grandExchange.loadItemInfo(MAGIC_ITEM_ID);
			price = magic.getMarketPrice();
			exp = 250;
			x1 = 1.25;
			x2 = 1.25;
			bankTile = new RSTile[] { new RSTile(2727, 3494),
					new RSTile(2724, 3494) };
			bankToTrees = new RSTile[] { new RSTile(2725, 3492),
					new RSTile(2725, 3485), new RSTile(2725, 3475),
					new RSTile(2723, 3467), new RSTile(2722, 3455),
					new RSTile(2713, 3450), new RSTile(2703, 3441),
					new RSTile(2703, 3430), new RSTile(2698, 3425), };
			treeLocs = new RSTile[] { new RSTile(2697, 3424),
					new RSTile(2692, 3425), new RSTile(2691, 3427),
					new RSTile(2694, 3425) };
			bankerID = 25808;
			treesID = new int[] { 1306, 1308 };
			treesToBank = reversePath(bankToTrees);
		}
		if (chopping.equals("Magics") && chopLoc.equals("South Seers")) {
			booth = true;
			ssMages = true;
			final GEItemInfo magic = grandExchange.loadItemInfo(MAGIC_ITEM_ID);
			price = magic.getMarketPrice();
			exp = 250;
			x1 = 1.25;
			x2 = 1.25;
			bankTile = new RSTile[] { new RSTile(2727, 3494),
					new RSTile(2724, 3494) };
			bankToTrees = new RSTile[] { new RSTile(2726, 3491),
					new RSTile(2727, 3483), new RSTile(2726, 3474),
					new RSTile(2727, 3465), new RSTile(2727, 3457),
					new RSTile(2731, 3450), new RSTile(2731, 3439),
					new RSTile(2723, 3433), new RSTile(2717, 3425),
					new RSTile(2719, 3413), new RSTile(2721, 3404),
					new RSTile(2715, 3396), new RSTile(2702, 3393),
					new RSTile(2702, 3398) };
			treeLocs = new RSTile[] { new RSTile(2699, 3398),
					new RSTile(2699, 3396), new RSTile(2705, 3399),
					new RSTile(2705, 3397) };
			bankerID = 25808;
			treesID = new int[] { 1306, 1308 };
			treesToBank = reversePath(bankToTrees);
		}
		if (chopping.equals("Yews") && chopLoc.equals("Edgeville")) {
			booth = true;
			final GEItemInfo yew = grandExchange.loadItemInfo(YEW_ITEM_ID);
			price = yew.getMarketPrice();
			exp = 175;
			x1 = 1.5;
			edge = true;
			bankTile = new RSTile[] { new RSTile(3095, 3491),
					new RSTile(3095, 3489), new RSTile(3095, 3493) };
			bankToTrees = new RSTile[] { new RSTile(3093, 3491),
					new RSTile(3094, 3478), new RSTile(3088, 3470) };
			treeLocs = new RSTile[] { new RSTile(3087, 3469),
					new RSTile(3087, 3481) };
			bankerID = 26972;
			treesID = new int[] { 1309, 1212 };
			treesToBank = reversePath(bankToTrees);
		}
		if (chopping.equals("Yews") && chopping.equals("Port Sarim")) {
			booth = true;
			final GEItemInfo yew = grandExchange.loadItemInfo(YEW_ITEM_ID);
			price = yew.getMarketPrice();
			exp = 175;
			x1 = 1.5;
			bankTile = new RSTile[] { new RSTile(3091, 3245),
					new RSTile(3091, 3243), new RSTile(3091, 3242) };
			bankToTrees = new RSTile[] { new RSTile(3094, 3244),
					new RSTile(3085, 3248), new RSTile(3078, 3258),
					new RSTile(3074, 3269), new RSTile(3066, 3276),
					new RSTile(3058, 3275) };
			treeLocs = new RSTile[] { new RSTile(3054, 3272) };
			bankerID = 25808;
			treesID = new int[] { 1309, 1212 };
			treesToBank = reversePath(bankToTrees);
		}
		if (chopping.equals("Willows") && chopLoc.equals("Port Sarim")) {
			final GEItemInfo willow = grandExchange
					.loadItemInfo(WILLOW_ITEM_ID);
			price = willow.getMarketPrice();
			exp = 68;
			x1 = 1.2;
			x2 = 1.2;
			bankTile = new RSTile[] { new RSTile(3091, 3245),
					new RSTile(3091, 3243), new RSTile(3091, 3242) };
			bankToTrees = new RSTile[] { new RSTile(3092, 3245),
					new RSTile(3083, 3248), new RSTile(3071, 3251),
					new RSTile(3060, 3254) };
			treeLocs = new RSTile[] { new RSTile(3057, 3255),
					new RSTile(3060, 3255), new RSTile(3063, 3252),
					new RSTile(3061, 3255) };
			booth = true;
			bankerID = 2213;
			treesID = new int[] { 5553, 5551, 5552 };
			treesToBank = reversePath(bankToTrees);
		}
		if (chopping.equals("Yews") && chopLoc.equals("Rimmington")) {
			booth = true;
			final GEItemInfo yew = grandExchange.loadItemInfo(YEW_ITEM_ID);
			price = yew.getMarketPrice();
			exp = 175;
			x1 = 1.5;
			bankTile = new RSTile[] { new RSTile(3011, 3354) };
			bankToTrees = new RSTile[] { new RSTile(3012, 3355),
					new RSTile(3012, 3358), new RSTile(3007, 3348),
					new RSTile(3006, 3342), new RSTile(3006, 3334),
					new RSTile(3006, 3329), new RSTile(3007, 3323),
					new RSTile(3004, 3316), new RSTile(2999, 3313),
					new RSTile(2989, 3308), new RSTile(2985, 3306),
					new RSTile(2978, 3294), new RSTile(2971, 3293),
					new RSTile(2963, 3289), new RSTile(2961, 3284),
					new RSTile(2960, 3277), new RSTile(2960, 3271),
					new RSTile(2961, 3264), new RSTile(2961, 3258),
					new RSTile(2960, 3249), new RSTile(2953, 3242),
					new RSTile(2947, 3238), new RSTile(2943, 3234) };
			treeLocs = new RSTile[] { new RSTile(2936, 3230),
					new RSTile(2935, 3226), new RSTile(2934, 3234),
					new RSTile(2940, 3233) };
			bankerID = 11758;
			treesID = new int[] { 1309 };
			treesToBank = reversePath(bankToTrees);
		}
		if (chopping.equals("Yews") && chopLoc.equals("Falador")) {
			booth = true;
			final GEItemInfo yew = grandExchange.loadItemInfo(YEW_ITEM_ID);
			price = yew.getMarketPrice();
			exp = 175;
			x1 = 1.5;
			bankTile = new RSTile[] { new RSTile(3011, 3354) };
			bankToTrees = new RSTile[] { new RSTile(3012, 3355),
					new RSTile(3012, 3358), new RSTile(3007, 3348),
					new RSTile(3006, 3342), new RSTile(3006, 3334),
					new RSTile(3006, 3329), new RSTile(3007, 3323),
					new RSTile(3007, 3318) };
			treeLocs = new RSTile[] { new RSTile(3040, 3321),
					new RSTile(3018, 3315), new RSTile(2999, 3313) };
			bankerID = 11758;
			treesID = new int[] { 1309 };
			treesToBank = reversePath(bankToTrees);
		}
		if (chopping.equals("Willows") && chopLoc.equals("Barbarian Village")) {
			final GEItemInfo willow = grandExchange
					.loadItemInfo(WILLOW_ITEM_ID);
			price = willow.getMarketPrice();
			exp = 68;
			x1 = 1.2;
			x2 = 1.2;
			Door = 20195;
			deposit = true;
			bankTile = new RSTile[] { new RSTile(2537, 3573) };
			bankToTrees = new RSTile[] { new RSTile(2535, 3574),
					new RSTile(2534, 3574), new RSTile(2532, 3571),
					new RSTile(2520, 3571), new RSTile(2519, 3574),
					new RSTile(2519, 3567), new RSTile(2517, 3563),
					new RSTile(2515, 3559), new RSTile(2519, 3576) };
			treeLocs = new RSTile[] { new RSTile(2517, 3567),
					new RSTile(2513, 3561), new RSTile(2513, 3558),
					new RSTile(2510, 3555), new RSTile(2519, 3578),
					new RSTile(2522, 3582), new RSTile(2517, 3580),
					new RSTile(2517, 3581) };
			bankerID = 20228;
			booth = false;
			treesID = new int[] { 5553, 5551, 5552 };
			treesToBank = reversePath(bankToTrees);

		}
		if (chopping.equals("Yews") && chopLoc.equals("Gnome Village")) {
			booth = true;
			price = 430;
			exp = 175;
			x1 = 1.5;
			bankTile = new RSTile[] { new RSTile(2447, 3427) };
			bankToTrees = new RSTile[] { new RSTile(2431, 3416),
					new RSTile(2433, 2422), new RSTile(2437, 2472),
					new RSTile(2440, 3430), new RSTile(2443, 3435) };
			treeLocs = new RSTile[] { new RSTile(2439, 3436),
					new RSTile(2433, 3441), new RSTile(2433, 3426) };
			bankerID = 25808;
			treesID = new int[] { 1309, 1212 };
			treesToBank = reversePath(bankToTrees);
		}
		if (chopping.equals("Magics") && chopLoc.equals("Duel Arena")) {
			booth = false;
			chest = true;
			Door = 27854;
			dMages = true;
			final GEItemInfo magic = grandExchange.loadItemInfo(MAGIC_ITEM_ID);
			price = magic.getMarketPrice();
			exp = 250;
			x1 = 1.25;
			x2 = 1.25;
			bankTile = new RSTile[] { new RSTile(3381, 3269) };
			bankToTrees = new RSTile[] { new RSTile(3383, 3268),
					new RSTile(3376, 3266), new RSTile(3368, 3265),
					new RSTile(3359, 3265), new RSTile(3353, 3266),
					new RSTile(3350, 3272), new RSTile(3349, 3278),
					new RSTile(3353, 3284), new RSTile(3361, 3286),
					new RSTile(3363, 3292), new RSTile(3367, 3300) };
			treeLocs = new RSTile[] { new RSTile(3369, 3312),
					new RSTile(3356, 3311) };
			bankerID = 25808;
			treesID = new int[] { 1306, 1308 };
			treesToBank = new RSTile[] { new RSTile(3356, 3300),
					new RSTile(3367, 3300), new RSTile(3363, 3292),
					new RSTile(3361, 3286), new RSTile(3353, 3284),
					new RSTile(3349, 3278), new RSTile(3350, 3272),
					new RSTile(3353, 3266), new RSTile(3359, 3265),
					new RSTile(3368, 3265), new RSTile(3376, 3266),
					new RSTile(3383, 3268) };
		}
		if (chopping.equals("Teaks") && chopLoc.equals("Ape Atoll")) {
			final GEItemInfo teak = grandExchange.loadItemInfo(YEW_ITEM_ID);
			price = teak.getMarketPrice();
			exp = 175;
			x1 = 0.35;
			x2 = 0.35;
			treeLocs = new RSTile[] { new RSTile(2773, 2698),
					new RSTile(2776, 2698), new RSTile(2773, 2700) };
			bankerID = 2213;
			bankTile = new RSTile[] { new RSTile(2807, 3442) };
			treesID = new int[] { 9036, 1212 };

		}
		if (chopping.equals("Trees") && chopLoc.equals("Grand Exchange")) {
			booth = false;
			final GEItemInfo tree = grandExchange.loadItemInfo(TREE_ITEM_ID);
			price = tree.getMarketPrice();
			exp = 25;
			x1 = 1;
			x2 = 1;
			GE = true;
			bankToTrees = new RSTile[] { new RSTile(3168, 3488),
					new RSTile(3174, 3488), new RSTile(3180, 3497),
					new RSTile(3184, 3498) };
			treeLocs = new RSTile[] { new RSTile(3186, 3493),
					new RSTile(3187, 3499), new RSTile(3188, 3502),
					new RSTile(3183, 3503), new RSTile(3192, 3505),
					new RSTile(3192, 3509) };
			bankerID = 6533;
			bankTile = new RSTile[] { new RSTile(3167, 3489) };
			treesID = new int[] { 1278, 1276 };
			treesToBank = reversePath(bankToTrees);
		}
		if (chopping.equals("Oaks") && chopLoc.equals("Falador")) {
			booth = true;
			final GEItemInfo oak = grandExchange.loadItemInfo(OAK_ITEM_ID);
			price = oak.getMarketPrice();
			exp = 37;
			x1 = 1.5;
			bankToTrees = new RSTile[] { new RSTile(3012, 3356),
					new RSTile(3009, 3363), new RSTile(3001, 3364),
					new RSTile(2997, 3361) };
			treeLocs = new RSTile[] { new RSTile(3001, 3367),
					new RSTile(2983, 3365) };
			bankTile = new RSTile[] { new RSTile(3011, 3354) };
			bankerID = 11758;
			treesID = new int[] { 1281, 1212 };
			treesToBank = reversePath(bankToTrees);
		}

		if (chopping.equals("Willows") && chopLoc.equals("Catherby")) {

			final GEItemInfo willow = grandExchange
					.loadItemInfo(WILLOW_ITEM_ID);
			price = willow.getMarketPrice();

			exp = 68;
			x1 = 1.2;
			x2 = 1.2;
			bankTile = new RSTile[] { new RSTile(2807, 3442),
					new RSTile(2809, 3442) };
			bankToTrees = new RSTile[] { new RSTile(2808, 3440),
					new RSTile(2803, 3433), new RSTile(2790, 3431),
					new RSTile(2783, 3430) };
			treeLocs = new RSTile[] { new RSTile(2786, 3429),
					new RSTile(2783, 3427), new RSTile(2782, 3428) };
			booth = true;
			bankerID = 2213;
			treesID = new int[] { 5553, 5551, 5552 };
			treesToBank = reversePath(bankToTrees);
		}
		if (chopping.equals("Willows") && chopLoc.equals("Seers")) {

			final GEItemInfo willow = grandExchange
					.loadItemInfo(WILLOW_ITEM_ID);
			price = willow.getMarketPrice();

			exp = 68;
			x1 = 1.2;
			x2 = 1.2;
			bankTile = new RSTile[] { new RSTile(2727, 3494),
					new RSTile(2724, 3494) };
			bankToTrees = new RSTile[] { new RSTile(2725, 3492),
					new RSTile(2719, 3501), new RSTile(2716, 3508) };
			treeLocs = new RSTile[] { new RSTile(2713, 3508),
					new RSTile(2711, 3511), new RSTile(2709, 3511),
					new RSTile(2707, 3514) };
			booth = true;
			bankerID = 2213;
			treesID = new int[] { 5553, 5551, 5552 };
			treesToBank = reversePath(bankToTrees);
		}
		if (chopping.equals("Willows") && chopLoc.equals("Rimmington")) {

			final GEItemInfo willow = grandExchange
					.loadItemInfo(WILLOW_ITEM_ID);
			price = willow.getMarketPrice();

			exp = 68;
			x1 = 1.2;
			x2 = 1.2;
			bankTile = new RSTile[] { new RSTile(3011, 3354) };
			bankToTrees = new RSTile[] { new RSTile(3012, 3355),
					new RSTile(3012, 3358), new RSTile(3007, 3348),
					new RSTile(3006, 3342), new RSTile(3006, 3334),
					new RSTile(3006, 3329), new RSTile(3007, 3323),
					new RSTile(3004, 3316), new RSTile(2999, 3313),
					new RSTile(2989, 3308), new RSTile(2985, 3306),
					new RSTile(2978, 3294), new RSTile(2971, 3293),
					new RSTile(2963, 3289), new RSTile(2961, 3284),
					new RSTile(2960, 3277), new RSTile(2960, 3271),
					new RSTile(2961, 3264), new RSTile(2961, 3258),
					new RSTile(2960, 3249), new RSTile(2956, 3242),
					new RSTile(2957, 3230), new RSTile(2957, 3219),
					new RSTile(2962, 3207), new RSTile(2969, 3196) };
			treeLocs = new RSTile[] { new RSTile(2969, 3194),
					new RSTile(2969, 3192), new RSTile(2971, 3195),
					new RSTile(2973, 3196) };
			bankerID = 11758;
			treesID = new int[] { 5553, 5551, 5552 };
			treesToBank = reversePath(bankToTrees);
		}
		if (chopping.equals("Trees") && chopLoc.equals("Draynor")) {
			booth = true;
			final GEItemInfo tree = grandExchange.loadItemInfo(TREE_ITEM_ID);
			price = tree.getMarketPrice();
			exp = 25;
			x1 = 1;
			x2 = 1;
			bankToTrees = new RSTile[] { new RSTile(3093, 3243),
					new RSTile(3102, 3250), new RSTile(3112, 3257) };
			treeLocs = new RSTile[] { new RSTile(3110, 3257),
					new RSTile(3111, 3259), new RSTile(3116, 3260),
					new RSTile(3114, 3256), new RSTile(3118, 3255),
					new RSTile(3115, 3253) };
			bankerID = 2213;
			bankTile = new RSTile[] { new RSTile(3167, 3489) };
			treesID = new int[] { 1278, 1276 };
			treesToBank = reversePath(bankToTrees);
		}

		// START SCRIPT
		start = true;
	}

	public boolean tileOnScreenDeviant(final RSTile t) {
		return pointOnScreenDeviant(Calculations.tileToScreen(t));
	}

	public void visitButtonActionPerformed(final ActionEvent evt) {
		visitThread();
	}

	public void visitThread() {
		final String URL = "http://www.rsbot.org/vb/showthread.php?t=94181";
		final java.awt.Desktop browser = java.awt.Desktop.getDesktop();
		java.net.URI location = null;
		try {
			location = new java.net.URI(URL);
		} catch (final URISyntaxException a) {
			a.printStackTrace();
		}
		try {
			browser.browse(location);
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	public boolean walkToBank() {
		return walkPathMM(treesToBank, 3, 3);
	}

	public boolean walkToTrees() {
		return walkPathMM(bankToTrees, 3, 3);
	}

	public boolean wieldAxe() {
		return !inventoryContainsOneOf(axeIDs);
	}

	private class WCProGUI extends JFrame {
		WCPro script;
		private final File settingsFile = new File(new File(
				GlobalConfiguration.Paths.getSettingsDirectory()), "WCPro.txt");

		private static final long serialVersionUID = 7165405076486776117L;
		private JPanel northPanel;
		private JTabbedPane centerTabbedPane;
		private JPanel settingsTab;
		private JCheckBox powerChopCheckBox;
		private JCheckBox autoChopCheckBox;
		private JTextArea jTextArea1;
		private JLabel jLabel6;
		private JLabel jLabel5;
		private JLabel jLabel4;
		private JLabel jLabel3;
		private JPanel jPanel1;
		private JButton threadButton;
		private JPanel threadPanel;
		private JPanel creditsPanel;
		private JLabel jLabel2;
		private JLabel jLabel1;
		private JLabel creditsLabel2;
		private JLabel creditsLabel;
		private JCheckBox smartWalkingCheckBox;
		private JPanel southSettings;
		private JScrollPane jScrollPane1;
		private JLabel locLabel;
		private JCheckBox buyAxeCheckBox;
		private JLabel jLabel7;
		private JPanel locSettingLabel;
		private JComboBox locComboBox;
		private JPanel locSetting;
		private JPanel treeSetting;
		private JComboBox treeComboBox;
		private JLabel treeLabel;
		private JPanel treeSettingLabel;
		private JButton exitButton;
		private JButton startButton;
		private JLabel mainLabel;
		private JPanel creditsTab;
		private JPanel updatesTab;
		private JPanel centerPanel;
		private JPanel southPanel;

		{
			// Set Look & Feel
			try {
				javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager
						.getSystemLookAndFeelClassName());
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}

		private WCProGUI(final WCPro scr) {
			super();
			script = scr;
			initGUI();
		}

		private void getChangeLog() {
			try {
				settingsFile.createNewFile();
				final BufferedReader in = new BufferedReader(
						new InputStreamReader(
								new URL(
										"http://www.itaha.com/rsbot/WCProChangeLog.txt")
										.openStream()));
				final BufferedWriter out = new BufferedWriter(new FileWriter(
						settingsFile));
				String temp;
				while ((temp = in.readLine()) != null) {
					out.append(temp);
					out.newLine();
				}
				in.close();
				out.close();
			} catch (final Exception e) {
				System.out.print("Unable to retrieve latest changelog.");
			}
		}

		private void initGUI() {
			try {
				setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				setResizable(false);
				setTitle("WCPro Script Options");
				setAlwaysOnTop(true);
				addWindowListener(new WindowAdapter() {
					public void windowClosing(final WindowEvent ev) {
						Bot.getScriptHandler().stopScript(script.ID);
					}
				});
				this.setLocation(new java.awt.Point(0, 0));
				{
					northPanel = new JPanel();
					getContentPane().add(northPanel, BorderLayout.NORTH);
					northPanel
							.setPreferredSize(new java.awt.Dimension(392, 28));
					northPanel
							.setFont(new java.awt.Font("Comic Sans MS", 0, 12));
					{
						mainLabel = new JLabel();
						northPanel.add(mainLabel);
						mainLabel.setText("WCPro Script Options");
						mainLabel.setForeground(new java.awt.Color(0, 0, 255));
						mainLabel.setFont(new java.awt.Font("Comic Sans MS", 0,
								16));
					}
				}
				{
					southPanel = new JPanel();
					getContentPane().add(southPanel, BorderLayout.SOUTH);
					southPanel
							.setPreferredSize(new java.awt.Dimension(392, 36));
					southPanel
							.setFont(new java.awt.Font("Comic Sans MS", 0, 12));
					{
						startButton = new JButton();
						southPanel.add(startButton);
						startButton.setText("Start!");
						startButton.setFont(new java.awt.Font("Comic Sans MS",
								0, 11));
						startButton.setPreferredSize(new java.awt.Dimension(65,
								22));
						startButton.addActionListener(new ActionListener() {
							public void actionPerformed(final ActionEvent evt) {
								script.start();
								dispose();
							}
						});
					}
					{
						exitButton = new JButton();
						southPanel.add(exitButton);
						exitButton.setText("Exit!");
						exitButton.setFont(new java.awt.Font("Comic Sans MS",
								0, 11));
						exitButton.setPreferredSize(new java.awt.Dimension(59,
								22));
						exitButton.addActionListener(new ActionListener() {
							public void actionPerformed(final ActionEvent evt) {
								script.runScript = false;
								script.start = true;
								dispose();
							}
						});
					}
				}
				{
					centerPanel = new JPanel();
					getContentPane().add(centerPanel, BorderLayout.CENTER);
					centerPanel.setPreferredSize(new java.awt.Dimension(392,
							163));
					centerPanel.setFont(new java.awt.Font("Comic Sans MS", 0,
							12));
					{
						centerTabbedPane = new JTabbedPane();
						centerPanel.add(centerTabbedPane);
						centerTabbedPane
								.setPreferredSize(new java.awt.Dimension(393,
										201));
						{
							settingsTab = new JPanel();
							centerTabbedPane.addTab("Settings", null,
									settingsTab, null);
							settingsTab
									.setPreferredSize(new java.awt.Dimension(
											395, 152));
							settingsTab.setFont(new java.awt.Font(
									"Comic Sans MS", 0, 12));
							{
								treeSettingLabel = new JPanel();
								settingsTab.add(treeSettingLabel);
								treeSettingLabel
										.setPreferredSize(new java.awt.Dimension(
												176, 22));
								{
									treeLabel = new JLabel();
									treeSettingLabel.add(treeLabel);
									treeLabel.setText("Tree:");
									treeLabel.setFont(new java.awt.Font(
											"Comic Sans MS", 0, 12));
									treeLabel
											.setPreferredSize(new java.awt.Dimension(
													35, 18));

								}
							}
							{
								treeSetting = new JPanel();
								settingsTab.add(treeSetting);
								treeSetting
										.setPreferredSize(new java.awt.Dimension(
												181, 29));
								{
									final ComboBoxModel treeComboBoxModel = new DefaultComboBoxModel(
											new String[] { "Trees", "Oaks",
													"Willows", "Yews",
													"Maples", "Magics" });
									treeComboBox = new JComboBox();
									treeSetting.add(treeComboBox);
									treeComboBox.setModel(treeComboBoxModel);
									treeComboBox
											.setPreferredSize(new java.awt.Dimension(
													126, 22));
									treeComboBox.setFont(new java.awt.Font(
											"Comic Sans MS", 0, 12));
									treeComboBox
											.addActionListener(new ActionListener() {
												public void actionPerformed(
														final ActionEvent evt) {
													final String tree = (String) treeComboBox
															.getSelectedItem();
													if (tree == "Trees") {
														locComboBox
																.setModel(new DefaultComboBoxModel(
																		new String[] {
																				"Draynor",
																				"Grand Exchange" }));
													}
													if (tree == "Oaks") {
														locComboBox
																.setModel(new DefaultComboBoxModel(
																		new String[] {
																				"Draynor",
																				"Falador" }));
													}
													if (tree == "Willows") {
														locComboBox
																.setModel(new DefaultComboBoxModel(
																		new String[] {
																				"Draynor",
																				"Barbarian Village",
																				"Port Sarim",
																				"Rimmington",
																				"Catherby",
																				"Seers" }));
													}
													if (tree == "Yews") {
														locComboBox
																.setModel(new DefaultComboBoxModel(
																		new String[] {
																				"Grand Exchange",
																				"Port Sarim",
																				"Rimmington",
																				"Falador",
																				"South Falador",
																				"Catherby",
																				"Edgeville",
																				"Seers",
																				"Gnome Village" }));
													}
													if (tree == "Maples") {
														locComboBox
																.setModel(new DefaultComboBoxModel(
																		new String[] { "Seers" }));
													}
													if (tree == "Magics") {
														locComboBox
																.setModel(new DefaultComboBoxModel(
																		new String[] {
																				"Seers",
																				"South Seers",
																				"Duel Arena" }));
													}
												}
											});
								}
							}
							{
								locSettingLabel = new JPanel();
								settingsTab.add(locSettingLabel);
								locSettingLabel
										.setPreferredSize(new java.awt.Dimension(
												176, 23));
								{
									locLabel = new JLabel();
									locSettingLabel.add(locLabel);
									locLabel.setText("Location:");
									locLabel.setFont(new java.awt.Font(
											"Comic Sans MS", 0, 12));
									locLabel
											.setPreferredSize(new java.awt.Dimension(
													53, 18));
								}
							}
							{
								locSetting = new JPanel();
								settingsTab.add(locSetting);
								locSetting
										.setPreferredSize(new java.awt.Dimension(
												181, 31));
								{
									final ComboBoxModel jComboBox1Model = new DefaultComboBoxModel(
											new String[] { "Draynor",
													"Grand Exchange" });
									locComboBox = new JComboBox();
									locSetting.add(locComboBox);
									locComboBox.setModel(jComboBox1Model);
									locComboBox
											.setPreferredSize(new java.awt.Dimension(
													126, 22));
									locComboBox.setFont(new java.awt.Font(
											"Comic Sans MS", 0, 12));
								}
							}
							{
								southSettings = new JPanel();
								settingsTab.add(southSettings);
								southSettings
										.setPreferredSize(new java.awt.Dimension(
												390, 100));
								{
									smartWalkingCheckBox = new JCheckBox();
									southSettings.add(smartWalkingCheckBox);
									smartWalkingCheckBox
											.setText("Enable Smart Walking");
									smartWalkingCheckBox
											.setPreferredSize(new java.awt.Dimension(
													336, 19));
									smartWalkingCheckBox
											.setFont(new java.awt.Font(
													"Comic Sans MS", 0, 12));
									smartWalkingCheckBox.setSelected(true);
								}
								{
									powerChopCheckBox = new JCheckBox();
									southSettings.add(powerChopCheckBox);
									powerChopCheckBox.setText("Power Chop");
									powerChopCheckBox
											.setPreferredSize(new java.awt.Dimension(
													336, 19));
									powerChopCheckBox
											.setFont(new java.awt.Font(
													"Comic Sans MS", 0, 12));
								}
								{
									autoChopCheckBox = new JCheckBox();
									southSettings.add(autoChopCheckBox);
									autoChopCheckBox
											.setText("AutoChop (Levels WC Level 1-60)");
									autoChopCheckBox
											.setPreferredSize(new java.awt.Dimension(
													336, 19));
									autoChopCheckBox.setFont(new java.awt.Font(
											"Comic Sans MS", 0, 12));
									autoChopCheckBox
											.addActionListener(new ActionListener() {
												public void actionPerformed(
														final ActionEvent evt) {
													treeComboBox
															.setEnabled(!treeComboBox
																	.isEnabled());
													locComboBox
															.setEnabled(!locComboBox
																	.isEnabled());
												}
											});
								}
								{
									buyAxeCheckBox = new JCheckBox();
									southSettings.add(buyAxeCheckBox);
									buyAxeCheckBox
											.setText("Buy axe if not found in bank? (Not Available)");
									buyAxeCheckBox.setFont(new java.awt.Font(
											"Comic Sans MS", 0, 12));
									buyAxeCheckBox
											.setPreferredSize(new java.awt.Dimension(
													336, 19));
									buyAxeCheckBox.setEnabled(false);
								}
							}
						}
						{
							updatesTab = new JPanel();
							centerTabbedPane.addTab("Updates", null,
									updatesTab, null);
							updatesTab.setFont(new java.awt.Font(
									"Comic Sans MS", 0, 12));
							{
								jPanel1 = new JPanel();
								updatesTab.add(jPanel1);
								jPanel1
										.setPreferredSize(new java.awt.Dimension(
												389, 53));
								{
									jLabel3 = new JLabel();
									jPanel1.add(jLabel3);
									jLabel3.setText("Current Script Version:");
									jLabel3
											.setPreferredSize(new java.awt.Dimension(
													235, 20));
								}
								{
									jLabel4 = new JLabel();
									jPanel1.add(jLabel4);
									jLabel4.setText("0.0");
									jLabel4
											.setPreferredSize(new java.awt.Dimension(
													81, 14));
								}
								{
									jLabel6 = new JLabel();
									jPanel1.add(jLabel6);
									jLabel6.setText("Latest Script Version:");
									jLabel6
											.setPreferredSize(new java.awt.Dimension(
													235, 20));
								}
								{
									jLabel5 = new JLabel();
									jPanel1.add(jLabel5);
									jLabel5.setText("0.0");
									jLabel5
											.setPreferredSize(new java.awt.Dimension(
													81, 14));
								}
							}

							jLabel4.setText(Double.toString(script.getClass()
									.getAnnotation(ScriptManifest.class)
									.version()));
							jLabel5.setText(jLabel4.getText());
							jLabel4.setForeground(Color.blue);
							jLabel5.setForeground(Color.blue);
							{
								jScrollPane1 = new JScrollPane();
								updatesTab.add(jScrollPane1);
								{
									jTextArea1 = new JTextArea();
									jScrollPane1.setViewportView(jTextArea1);
									jScrollPane1
											.setPreferredSize(new java.awt.Dimension(
													384, 108));
									jTextArea1
											.setText("Fetching Change Log...");
									jTextArea1.setEditable(false);
									jTextArea1.setFont(new java.awt.Font(
											"Comic Sans MS", 0, 12));
									jTextArea1.setColumns(20);
									jTextArea1.setRows(5);
								}
							}
						}
						{
							creditsTab = new JPanel();
							centerTabbedPane.addTab("Credits", null,
									creditsTab, null);
							creditsTab.setFont(new java.awt.Font(
									"Comic Sans MS", 0, 12));
							creditsTab.setPreferredSize(new java.awt.Dimension(
									395, 115));
							{
								creditsPanel = new JPanel();
								creditsTab.add(creditsPanel);
								creditsPanel
										.setPreferredSize(new java.awt.Dimension(
												368, 123));
								{
									creditsLabel = new JLabel();
									creditsPanel.add(creditsLabel);
									creditsLabel.setText("Credits go to:");
									creditsLabel
											.setPreferredSize(new java.awt.Dimension(
													368, 26));
									creditsLabel.setFont(new java.awt.Font(
											"Comic Sans MS", 0, 13));
									creditsLabel
											.setForeground(new java.awt.Color(
													0, 128, 0));
								}
								{
									creditsLabel2 = new JLabel();
									creditsPanel.add(creditsLabel2);
									creditsLabel2
											.setText("Taha & Jacmob for script support and updates");
									creditsLabel2
											.setPreferredSize(new java.awt.Dimension(
													302, 17));
									creditsLabel2.setFont(new java.awt.Font(
											"Comic Sans MS", 0, 12));
								}
								{
									jLabel1 = new JLabel();
									creditsPanel.add(jLabel1);
									jLabel1.setText("Deviant for script base");
									jLabel1.setFont(new java.awt.Font(
											"Comic Sans MS", 0, 12));
									jLabel1
											.setPreferredSize(new java.awt.Dimension(
													302, 17));
								}
								{
									jLabel2 = new JLabel();
									creditsPanel.add(jLabel2);
									jLabel2
											.setText("Fusion89k for formatting script base");
									jLabel2.setFont(new java.awt.Font(
											"Comic Sans MS", 0, 12));
									jLabel2
											.setPreferredSize(new java.awt.Dimension(
													302, 17));
								}
								{
									jLabel7 = new JLabel();
									creditsPanel.add(jLabel7);
									jLabel7
											.setText("Zenzie + Pauwelz for helping me when in need <3");
									jLabel7.setFont(new java.awt.Font(
											"Comic Sans MS", 0, 12));
									jLabel7
											.setPreferredSize(new java.awt.Dimension(
													302, 17));
								}
							}
							{
								threadPanel = new JPanel();
								creditsTab.add(threadPanel);
								threadPanel
										.setPreferredSize(new java.awt.Dimension(
												177, 29));
							}
							{
								threadButton = new JButton();
								creditsTab.add(threadButton);
								threadButton.setText("RSBot Script Thread");
								threadButton
										.addActionListener(new ActionListener() {
											public void actionPerformed(
													final ActionEvent evt) {
												script.visitThread();
											}
										});
							}
						}
					}
				}
				pack();
				setSize(400, 300);
				// LOAD SAVED SELECTION INFO
				final BufferedReader in = new BufferedReader(new FileReader(
						script.settingsFile));
				String line;
				String[] opts = {};
				while ((line = in.readLine()) != null) {
					if (line.contains(":")) {
						opts = line.split(":");
					}
				}
				in.close();
				if (opts.length == 5) {
					treeComboBox.setSelectedItem(opts[0]);
					locComboBox.setSelectedItem(opts[1]);
					if (opts[2].equals("false")) {
						smartWalkingCheckBox.setSelected(false);
					}
					if (opts[3].equals("true")) {
						powerChopCheckBox.setSelected(true);
					}
					if (opts[4].equals("true")) {
						autoChopCheckBox.setSelected(true);
						treeComboBox.setEnabled(false);
						locComboBox.setEnabled(false);
					}
				}
				// GET CHANGE LOG
				final Thread change = new Thread(new Runnable() {
					public void run() {
						getChangeLog();
						try {
							jTextArea1.read(new BufferedReader(new FileReader(
									settingsFile)), settingsFile);
						} catch (final FileNotFoundException e) {
							System.out.println("Changelog file not found.");
						} catch (final IOException e) {
							System.out.println("Unable to open changelog.");
						}
					}
				});
				change.start();
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}

}