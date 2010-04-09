import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.rsbot.accessors.Node;
import org.rsbot.accessors.RSNPCNode;
import org.rsbot.bot.Bot;
import org.rsbot.bot.input.CanvasWrapper;
import org.rsbot.bot.input.Mouse;
import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.Skills;
import org.rsbot.script.wrappers.RSInterfaceComponent;
import org.rsbot.script.wrappers.RSItemTile;
import org.rsbot.script.wrappers.RSNPC;

@ScriptManifest(authors = { "Foulwerp" }, category = "Combat", name = "FoulFighter", version = 2.05, description = "Settings In GUI")
public class FoulFighter extends Script implements PaintListener,
		ServerMessageListener {
	/*
	 * Credits to Ruski for Food ID's
	 */
	final int[] foodID = { 1895, 1893, 1891, 4293, 2142, 291, 2140, 3228, 9980,
			7223, 6297, 6293, 6295, 6299, 7521, 9988, 7228, 2878, 7568, 2343,
			1861, 13433, 315, 325, 319, 3144, 347, 355, 333, 339, 351, 329,
			3381, 361, 10136, 5003, 379, 365, 373, 7946, 385, 397, 391, 3369,
			3371, 3373, 2309, 2325, 2333, 2327, 2331, 2323, 2335, 7178, 7180,
			7188, 7190, 7198, 7200, 7208, 7210, 7218, 7220, 2003, 2011, 2289,
			2291, 2293, 2295, 2297, 2299, 2301, 2303, 1891, 1893, 1895, 1897,
			1899, 1901, 7072, 7062, 7078, 7064, 7084, 7082, 7066, 7068, 1942,
			6701, 6703, 7054, 6705, 7056, 7060, 2130, 1985, 1993, 1989, 1978,
			5763, 5765, 1913, 5747, 1905, 5739, 1909, 5743, 1907, 1911, 5745,
			2955, 5749, 5751, 5753, 5755, 5757, 5759, 5761, 2084, 2034, 2048,
			2036, 2217, 2213, 2205, 2209, 2054, 2040, 2080, 2277, 2225, 2255,
			2221, 2253, 2219, 2281, 2227, 2223, 2191, 2233, 2092, 2032, 2074,
			2030, 2281, 2235, 2064, 2028, 2187, 2185, 2229, 6883, 1971, 4608,
			1883, 1885, 15272 };
	final int[] pBones = { 526, 532, 530, 528, 3183, 2859 };
	final int[] bBones = { 526, 532, 530, 528, 3183, 2859, 534, 3125, 4834,
			14793, 4812, 3123, 4832, 6729, 536 };
	final int[] fRune = { 554, 4694, 4699, 4697 };
	final int[] charm = { 12158, 12159, 12160, 12163 };
	final int[] fStaffs = { 1387, 1401, 1393, 3054, 3053, 11738, 11736 };
	final int[] sPot = { 113, 115, 117, 119, 2440, 157, 159, 161, 9739, 9741,
			9743, 9745 };
	final int[] dPot = { 2442, 163, 165, 167, 137, 135, 133, 2432 };
	final int[] aPot = { 2436, 145, 147, 149, 2428, 121, 123, 125, 9739, 9741,
			9743, 9745 };
	final int[] bPTab = { 8015 };
	private int[] itemids = {};
	private String[] itemnames = {};
	private int[] alchable = {};
	private int[] npcID = {};
	private int startExp[] = null;
	public int spec, getExpH, xpph, alchNum, rndSpecCtr, origWeap, origShield,
			specWeap, specPercent;
	private boolean useFood, useBTP, buryBones, charms, attack, strength,
			defence, usepotion, useSpec;
	private boolean paint = true;
	private RSNPC npc;
	private RSItemTile tileB, tile;
	private long start;
	private FoulFighterGUI gui;
	private String state = "Nothing";
	private int rndSpec = 100;
	private int tempRndSpec;

	private double getVersion() {
		return 2.05;
	}

	private int slayerLeft() {
		return getSetting(394);
	}

	private enum State {
		FIGHTING, ATTACK, PICKUP, POTION, BURY, BTP, BONES, ALCH, SPECIAL
	}

	private State getState() {
		if (getMyPlayer().getInteracting() != null) {
			if (useSpec && getSetting(300) >= rndSpec * 10) {
				rndSpecCtr = 0;
				return State.SPECIAL;
			} else {
				state = "   FIGHTING";
				return State.FIGHTING;
			}
		} else if (itemsOnGround()) {
			state = "    PICKUP";
			return State.PICKUP;
		} else if (((getInventoryCount() < 26) && useBTP && bonesOnGround())
				|| ((getInventoryCount() < 26) && buryBones && bonesOnGround())) {
			state = "     BONES";
			return State.BONES;
		} else if ((getInventoryCount() >= 26) && buryBones
				&& (getInventoryCount(bBones) != 0)) {
			state = "   BURYING";
			return State.BURY;
		} else if ((getInventoryCount(foodID) == 0) && useBTP
				&& (getInventoryCount(pBones) != 0)) {
			state = "      BTP";
			return State.BTP;
		} else if (usepotion && hasPotions()) {
			if ((strength && (skills.getRealSkillLevel(STAT_STRENGTH)
					+ (random(3, 5)) >= skills
					.getCurrentSkillLevel(STAT_STRENGTH)))
					|| (attack && (skills.getRealSkillLevel(STAT_ATTACK)
							+ (random(3, 5)) >= skills
							.getCurrentSkillLevel(STAT_ATTACK)))
					|| (defence && (skills.getRealSkillLevel(STAT_DEFENSE)
							+ (random(3, 5)) >= skills
							.getCurrentSkillLevel(STAT_DEFENSE)))) {
				state = "    POTION";
				return State.POTION;
			}
		}
		if ((getInventoryCount(alchable) != 0)
				&& (getMyPlayer().getInteracting() == null)) {
			state = "   ALCHING";
			return State.ALCH;
		}
		state = " ATTACKING";
		return State.ATTACK;
	}

	@Override
	public boolean onStart(final Map<String, String> s) {
		start = System.currentTimeMillis();
		gui = new FoulFighterGUI();
		while (gui.isVisible()) {
			wait(random(100, 500));
		}
		for (int i = 0; i < itemids.length; i++) {
			log(itemids[i] + "," + itemnames[i]);
		}
		startExp = new int[20];
		for (int i = 0; i < 20; i++) {
			startExp[i] = skills.getCurrentSkillExp(i);
		}
		if (origWeap != 0) {
			log("Original Weapon - " + origWeap);
		}
		if (origShield != 0) {
			log("Original Shield - " + origShield);
		}
		if (specWeap != 0) {
			log("Special Weapon - " + specWeap);
		}
		if (specPercent != 0) {
			log("Special Percent - " + specPercent + "%");
		}
		return true;
	}

	@Override
	public int loop() {
		if (!isLoggedIn()) {
			return random(200, 700);
		}
		if (!isRunning() && getEnergy() > random(50, 75)) {
			setRun(true);
			wait(random(400, 800));
		}
		if (itemSelected() != 0) {
			atTile(getMyPlayer().getLocation(), "Cancel");
			wait(random(300, 600));
		}
		if ((getInventoryCount(bBones) != 0) && (getInventoryCount() >= 27)) {
			doInventoryItem(bBones, "Drop");
			wait(random(600, 750));
		}
		if (useSpec) {
			if (rndSpecCtr == 0 && getSetting(300) / 10 >= specPercent) {
				if (getSetting(301) != 1) {
					rndSpec = getSetting(300) / 10;
				} else if (getSetting(301) == 1) {
					tempRndSpec = (getSetting(300) / 10) - specPercent;
					if (tempRndSpec >= specPercent) {
						rndSpec = tempRndSpec;
					} else {
						getRndSpec();
					}
				}
				rndSpecCtr = 1;
			} else {
				if (getSetting(300) < specPercent * 10) {
					while (inventoryContains(origWeap)) {
						atInventoryItem(origWeap, "Wield");
						wait(random(1000, 1100));
					}
					while (inventoryContains(origShield)) {
						atInventoryItem(origShield, "Wear");
						wait(random(1000, 1100));
					}
					if (rndSpecCtr == 0) {
						getRndSpec();
						rndSpecCtr = 1;
					}
				}
			}
		}
		switch (getState()) {
		case FIGHTING:
			if (useFood) {
				if (skills.getCurrentSkillLevel(STAT_HITPOINTS) <= random(
						skills.getRealSkillLevel(STAT_HITPOINTS) / 2, skills
								.getRealSkillLevel(STAT_HITPOINTS) / 1.5)) {
					if (getInventoryCount(foodID) != 0) {
						if (getCurrentTab() != TAB_INVENTORY) {
							openTab(TAB_INVENTORY);
							wait(random(400, 600));
						}
						doInventoryItem(foodID, "Eat");
						if (waitForAnim(829) != -1) {
							while (getMyPlayer().getAnimation() != -1) {
								wait(random(300, 600));
							}
							if (getInteractingNPC() != null) {
								atNPC(getInteractingNPC(), "Attack");
								if (waitToMove(750)) {
									while (getMyPlayer().isMoving()) {
										wait(random(20, 30));
									}
								}
							}
						}
					} else {
						if ((getInventoryCount(foodID) == 0) && isLoggedIn()) {
							if (useBTP) {
								if (getInventoryCount(bPTab) == 0) {
									log("Out of Bones to Peaches Tabs! Stopping Script!");
									stopScript();
								} else {
									if (getInventoryCount(pBones) == 0) {
										log("Out of Bones for Bones to Peaches! Stopping Script!");
										stopScript();
									} else {
										if (getInventoryCount(bPTab) != 0) {
											if ((getInventoryCount(foodID) == 0)
													&& (getInventoryCount(pBones) != 0)) {
												if (getCurrentTab() != TAB_INVENTORY) {
													openTab(TAB_INVENTORY);
													wait(random(300, 500));
												}
												doInventoryItem(bPTab, "Break");
												if (waitForAnim(1500) != -1) {
													while (getMyPlayer()
															.getAnimation() != -1) {
														wait(random(1250, 1500));
													}
												}
												if (getInventoryCount(foodID) != 0) {
													log("Used a Bones to Peaches Tab!");
												}
											}
										}
									}
								}
							} else {
								log("Out of Food! Stopping Script!");
								stopScript();
							}
						}
					}
				}
			}
			return antiban();
		case PICKUP:
			for (int i = 0; i < itemids.length; i++) {
				while ((tile = getGroundItemByID(itemids[i])) != null) {
					if (!tileOnScreen(tile)) {
						walkTileOnScreen(tile.randomizeTile(1, 1));
						if (waitToMove(1000)) {
							while (getMyPlayer().isMoving()) {
								wait(random(20, 60));
							}
						}
					}
					if (isInventoryFull()) {
						if ((getInventoryCount(tile.getItem().getID()) == 0)
								|| (getInventoryItemByID(tile.getItem().getID())
										.getStackSize() == 1)) {
							if (buryBones && (getInventoryCount(bBones) > 0)) {
								doInventoryItem(bBones, "Bury");
								wait(random(1000, 1500));
							} else {
								if (useBTP && (getInventoryCount(pBones) > 0)) {
									doInventoryItem(pBones, "Drop");
									wait(random(750, 1000));
								} else {
									if (useFood
											&& (getInventoryCount(foodID) > 0)) {
										doInventoryItem(foodID, "Eat");
										wait(random(750, 1000));
									} else {
										break;
									}
								}
							}
						}
					}
					atTile(tile, "Take " + itemnames[i]);
					if (waitToMove(1000)) {
						while (getMyPlayer().isMoving()) {
							wait(random(20, 60));
						}
					}
				}
			}
			if (charms) {
				for (int i = 0; i < charm.length; i++) {
					while ((tile = getGroundItemByID(charm[i])) != null) {
						if (!tileOnScreen(tile)) {
							walkTileOnScreen(tile.randomizeTile(1, 1));
							if (waitToMove(1000)) {
								while (getMyPlayer().isMoving()) {
									wait(random(20, 60));
								}
							}
						}
						if (isInventoryFull()
								&& (getInventoryCount(tile.getItem().getID()) == 0)) {
							if (buryBones && (getInventoryCount(bBones) > 0)) {
								doInventoryItem(bBones, "Bury");
								wait(random(1000, 1500));
							} else {
								if (useBTP && (getInventoryCount(pBones) > 0)) {
									doInventoryItem(pBones, "Drop");
									wait(random(750, 1000));
								} else {
									if (useFood
											&& (getInventoryCount(foodID) > 0)) {
										doInventoryItem(foodID, "Eat");
										wait(random(750, 1000));
									} else {
										break;
									}
								}
							}
						}
						String action2 = "charm";
						atTile(tile, action2);
						if (waitToMove(1000)) {
							while (getMyPlayer().isMoving()) {
								wait(random(20, 60));
							}
						}
					}
				}
			}
			break;
		case BONES:
			if (useBTP) {
				for (int i = 0; i < pBones.length; i++) {
					while ((tileB = getNearestGroundItemByID(pBones[i])) != null) {
						if (getInventoryCount() >= 26) {
							break;
						}
						if (!tileOnScreen(tileB)) {
							break;
						}
						String action = "ones";
						atTile(tileB, action);
						if (waitToMove(1000)) {
							while (getMyPlayer().isMoving()) {
								wait(random(20, 60));
							}
						}
					}
				}
			}
			if (buryBones) {
				for (int i = 0; i < bBones.length; i++) {
					while ((tileB = getNearestGroundItemByID(bBones[i])) != null) {
						if (getInventoryCount() >= 26) {
							break;
						}
						if (!tileOnScreen(tileB)) {
							break;
						}
						String action = "ones";
						atTile(tileB, action);
						if (waitToMove(1000)) {
							while (getMyPlayer().isMoving()) {
								wait(random(20, 60));
							}
						}
					}
				}
			}
			break;
		case ALCH:
			if ((getInventoryCount(alchable) > getInventoryCount(561))
					|| (getInventoryCount(alchable) > getInventoryCount(fRune))) {
				alchNum = getInventoryCount(561);
			} else {
				alchNum = getInventoryCount(alchable);
			}
			if ((getInventoryCount(561) >= alchNum)
					&& (getInventoryCount(fRune) >= alchNum * 5)) {
				while (alchNum > 0) {
					if (getCurrentTab() != Constants.TAB_MAGIC) {
						openTab(Constants.TAB_MAGIC);
						wait(random(500, 750));
					}
					castSpell(Constants.SPELL_HIGH_LEVEL_ALCHEMY);
					doInventoryItem(alchable, "Cast");
					if (waitForAnim(1000) != -1) {
						wait(random(1400, 1600));
					}
					alchNum--;
				}
			}
			break;
		case BURY:
			if (buryBones) {
				if (getInventoryCount() >= 26) {
					if (getMyPlayer().getInteracting() == null) {
						while (getInventoryCount(bBones) != 0) {
							if (getInteractingNPC() != null) {
								break;
							}
							if (doInventoryItem(bBones, "Bury")) {
								if (waitForAnim(300) != -1) {
									while (getMyPlayer().getAnimation() != -1) {
										wait(random(100, 300));
									}
								}
							}
						}
						log("Bury Bones Complete!");
					}
				}
			}
			break;
		case POTION:
			if (strength) {
				if (skills.getCurrentSkillLevel(STAT_STRENGTH) <= skills
						.getRealSkillLevel(STAT_STRENGTH)
						+ random(3, 5)) {
					if (getInventoryCount(sPot) != 0) {
						if (getCurrentTab() != TAB_INVENTORY) {
							openTab(TAB_INVENTORY);
							wait(random(200, 300));
						}
						doInventoryItem(sPot, "Drink");
						if (waitForAnim(829) != -1) {
							while (getMyPlayer().getAnimation() != -1) {
								wait(random(100, 300));
							}
						}
					}
				}
			}
			if (defence) {
				if (skills.getCurrentSkillLevel(STAT_DEFENSE) <= skills
						.getRealSkillLevel(STAT_DEFENSE)
						+ random(3, 5)) {
					if (getInventoryCount(dPot) != 0) {
						if (getCurrentTab() != TAB_INVENTORY) {
							openTab(TAB_INVENTORY);
							wait(random(200, 300));
						}
						doInventoryItem(dPot, "Drink");
						if (waitForAnim(829) != -1) {
							while (getMyPlayer().getAnimation() != -1) {
								wait(random(100, 300));
							}
						}
					}
				}
			}
			if (attack) {
				if (skills.getCurrentSkillLevel(STAT_ATTACK) <= skills
						.getRealSkillLevel(STAT_ATTACK)
						+ random(3, 5)) {
					if (getInventoryCount(aPot) != 0) {
						if (getCurrentTab() != TAB_INVENTORY) {
							openTab(TAB_INVENTORY);
							wait(random(200, 300));
						}
						doInventoryItem(aPot, "Drink");
						if (waitForAnim(829) != -1) {
							while (getMyPlayer().getAnimation() != -1) {
								wait(random(100, 300));
							}
						}
					}
				}
			}
			break;
		case BTP:
			if (useBTP) {
				if (getInventoryCount(bPTab) != 0
						&& getCurrentTab() == TAB_INVENTORY) {
					if ((getInventoryCount(foodID) == 0)
							&& (getInventoryCount(pBones) != 0)) {
						if (getCurrentTab() != TAB_INVENTORY) {
							openTab(TAB_INVENTORY);
							wait(random(300, 500));
						}
						doInventoryItem(bPTab, "Break");
						if (waitForAnim(1500) != -1) {
							while (getMyPlayer().getAnimation() != -1) {
								wait(random(1250, 1500));
							}
						}
						log("Used a Bones to Peaches Tab!");
					}
				} else {
					log("Out of Bones to Peaches tabs stopping Script!");
					stopScript();
				}
			}
			break;
		case ATTACK:
			if (getMyPlayer().getInteracting() == null) {
				if (getInteractingNPC() != null) {
					npc = getInteractingNPC();
				} else {
					npc = getNearestFreeNPCToAttackByID(npcID);
				}
			}
			if (npc == null) {
				return antiban();
			}
			if (!pointOnScreen(npc.getScreenLocation())
					&& (getMyPlayer().getInteracting() == null)) {
				walk();
				if (waitToMove(1000)) {
					while (getMyPlayer().isMoving()) {
						wait(random(20, 30));
					}
				}
			}
			if (pointOnScreen(npc.getScreenLocation())
					&& (getMyPlayer().getInteracting() == null)) {
				atNPC(npc, "Attack " + npc.getName());
				if (waitToMove(1000)) {
					while (getMyPlayer().isMoving()) {
						wait(random(20, 30));
					}
				}
			} else {
				wait(random(20, 30));
			}
			break;
		case SPECIAL:
			while (inventoryContains(specWeap)) {
				atInventoryItem(specWeap, "Wield");
				wait(random(1000, 1100));
			}
			while (getMyPlayer().getInteracting() != null
					&& getSetting(300) >= specPercent * 10) {
				if (getMyPlayer().getInteracting() == null) {
					break;
				}
				if (getCurrentTab() != TAB_ATTACK) {
					openTab(TAB_ATTACK);
					wait(random(400, 600));
				}
				if (getSetting(301) != 1) {
					atInterface(884, 4);
					wait(random(900, 1000));
				} else {
					wait(random(100, 300));
				}
			}
			break;
		}
		return 100;
	}

	private int antiban() {
		int i = random(0, 30);
		int ii = random(0, 25);
		if (i == 2) {
			moveMouse(random(0, CanvasWrapper.getGameWidth()), random(0,
					CanvasWrapper.getGameHeight()));
			return random(0, 400);
		} else if ((ii == 3) || (ii == 12)) {
			char dir = 37;
			if (random(0, 3) == 2) {
				dir = 39;
			}
			Bot.getInputManager().pressKey(dir);
			wait(random(500, 2000));
			Bot.getInputManager().releaseKey(dir);
			return random(0, 500);
		} else if ((i == 7) || (i == 4)) {
			setCameraAltitude(random(35, 150));
			return random(0, 500);
		} else if ((i == 5) || (i == 10) || (i == 11) || (i == 13) || (i == 18)
				|| (i == 27)) {
			moveMouseRandomly(random(-4, 4));
		} else if ((i == 1) || (i == 8) || (i == 15) || (i == 20)) {
			Thread camera = new Thread() {
				@Override
				public void run() {
					char dir = 37;
					if (random(0, 3) == 2) {
						dir = 39;
					}
					Bot.getInputManager().pressKey(dir);
					try {
						Thread.sleep(random(500, 2000));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Bot.getInputManager().releaseKey(dir);
				}
			};
			Thread mouse = new Thread() {
				@Override
				public void run() {
					moveMouse(random(0, CanvasWrapper.getGameWidth()), random(
							0, CanvasWrapper.getGameHeight()));
				}
			};
			if ((i == 7) || (i == 20)) {
				camera.start();
			}
			if (i == 1) {
				mouse.start();
			}
			while (camera.isAlive() || mouse.isAlive()) {
				wait(random(100, 300));
				return random(300, 700);
			}
		}
		return random(1000, 1500);
	}

	private void walk() {
		npc = getNearestFreeNPCToAttackByID(npcID);
		if ((distanceTo(npc.getLocation()) <= 10)) {
			walkTileOnScreen(npc.getLocation().randomizeTile(1, 1));
		} else {
			if (!tileOnScreen(npc.getLocation())
					&& tileOnMap(npc.getLocation())) {
				walkTileMM(npc.getLocation().randomizeTile(2, 2));
			} else {
				return;
			}
		}
	}

	@Override
	public int getMouseSpeed() {
		return (random(6, 8));
	}

	private boolean menuContains(String item) {
		try {
			for (String s : getMenuItems()) {
				if (s.toLowerCase().contains(item.toLowerCase())) {
					return true;
				}
			}
		} catch (Exception e) {
			return menuContains(item);
		}
		return false;
	}

	private int itemSelected() {
		for (final RSInterfaceComponent com : getInventoryInterface()
				.getComponents()) {
			if (com.getBorderThickness() == 2) {
				return com.getComponentID();
			}
		}
		return 0;
	}

	private boolean doInventoryItem(int[] ids, String action) {
		ArrayList<RSInterfaceComponent> possible = new ArrayList<RSInterfaceComponent>();
		for (RSInterfaceComponent com : getInventoryInterface().getComponents()) {
			for (int i : ids) {
				if (i == com.getComponentID()) {
					possible.add(com);
				}
			}
		}
		if (possible.size() == 0) {
			return false;
		}
		RSInterfaceComponent winner = possible.get(random(0,
				possible.size() - 1));
		Rectangle loc = winner.getArea();
		moveMouse((int) loc.getX() + 3, (int) loc.getY() + 3, (int) loc
				.getWidth() - 3, (int) loc.getHeight() - 3);
		wait(random(100, 300));
		String top = getMenuItems().get(0).toLowerCase();
		if (top.contains(action.toLowerCase())) {
			clickMouse(true);
			return true;
		} else if (menuContains(action)) {
			return atMenu(action);
		}
		return false;
	}

	private RSNPC getInteractingNPC() {
		final int[] validNPCs = Bot.getClient().getRSNPCIndexArray();

        for (final int element : validNPCs) {
        	Node node = Calculations.findNodeByID(Bot.getClient().getRSNPCNC(), element);
            if (node == null || !(node instanceof RSNPCNode)) {
                continue;
            }
            final RSNPC Monster = new RSNPC(((RSNPCNode) node).getRSNPC());
			if (Monster.getInteracting() != null) {
				if (Monster.getInteracting().equals(getMyPlayer())) {
					return Monster;
				}
			}
		}
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	private boolean itemsOnGround() {
		for (int i = 0; i < itemids.length; i++) {
			while (((tile = getGroundItemByID(itemids[i])) != null)) {
				return true;
			}
		}
		if (charms) {
			while (((tile = getGroundItemByID(charm)) != null)
					&& tileOnScreen(tile)) {
				return true;
			}
		}
		return false;
	}

	private boolean bonesOnGround() {
		if (useBTP) {
			while (((tile = getGroundItemByID(pBones)) != null)
					&& tileOnScreen(tile)) {
				return true;
			}
		} else if (buryBones) {
			while (((tile = getGroundItemByID(bBones)) != null)
					&& tileOnScreen(tile)) {
				return true;
			}
		}
		return false;
	}

	private void getRndSpec() {
		rndSpec = random(specPercent, 100);
		log("Special will be used next at " + rndSpec + "%");
	}

	public void serverMessageRecieved(ServerMessageEvent arg0) {
		String serverString = arg0.getMessage();
		if (serverString.contains("You've just")
				|| serverString.contains("Congratulations")) {
			log("You just advanced a level, attempting to click continue!");
			wait(random(1500, 2500));
			if (canContinue()) {
				clickContinue();
			}
		}
	}

	private boolean hasPotions() {
		if ((attack && (getInventoryCount(aPot) != 0))
				|| (strength && (getInventoryCount(sPot) != 0))
				|| (defence && (getInventoryCount(dPot) != 0))) {
			return true;
		}
		return false;
	}

	public void onRepaint(Graphics g) {
		if (paint) {
			final Mouse mouse = Bot.getClient().getMouse();
			final int mouse_x = mouse.getMouseX();
			final int mouse_y = mouse.getMouseY();
			final int mouse_press_x = mouse.getMousePressX();
			final int mouse_press_y = mouse.getMousePressY();
			final long mouse_press_time = mouse.getMousePressTime();
			g.setFont(new Font("Century Gothic", Font.BOLD, 13));
			if (System.currentTimeMillis() - mouse_press_time < 100) {
				g.setColor(new Color(70, 130, 180, 250));
				g.drawString("C", mouse_press_x, mouse_press_y);
			} else if ((System.currentTimeMillis() - mouse_press_time < 200)
					&& (System.currentTimeMillis() - mouse_press_time > 99)) {
				g.setColor(new Color(70, 130, 180, 225));
				g.drawString("Cl", mouse_press_x, mouse_press_y);
			} else if ((System.currentTimeMillis() - mouse_press_time < 300)
					&& (System.currentTimeMillis() - mouse_press_time > 199)) {
				g.setColor(new Color(70, 130, 180, 200));
				g.drawString("Cli", mouse_press_x, mouse_press_y);
			} else if ((System.currentTimeMillis() - mouse_press_time < 400)
					&& (System.currentTimeMillis() - mouse_press_time > 299)) {
				g.setColor(new Color(70, 130, 180, 175));
				g.drawString("Clic", mouse_press_x, mouse_press_y);
			} else if ((System.currentTimeMillis() - mouse_press_time < 500)
					&& (System.currentTimeMillis() - mouse_press_time > 399)) {
				g.setColor(new Color(70, 130, 180, 150));
				g.drawString("Click", mouse_press_x, mouse_press_y);
			} else if ((System.currentTimeMillis() - mouse_press_time < 600)
					&& (System.currentTimeMillis() - mouse_press_time > 499)) {
				g.setColor(new Color(70, 130, 180, 125));
				g.drawString("Click", mouse_press_x, mouse_press_y);
			} else if ((System.currentTimeMillis() - mouse_press_time < 700)
					&& (System.currentTimeMillis() - mouse_press_time > 599)) {
				g.setColor(new Color(70, 130, 180, 100));
				g.drawString("Click", mouse_press_x, mouse_press_y);
			} else if ((System.currentTimeMillis() - mouse_press_time < 800)
					&& (System.currentTimeMillis() - mouse_press_time > 699)) {
				g.setColor(new Color(70, 130, 180, 75));
				g.drawString("Click", mouse_press_x, mouse_press_y);
			} else if ((System.currentTimeMillis() - mouse_press_time < 900)
					&& (System.currentTimeMillis() - mouse_press_time > 799)) {
				g.setColor(new Color(70, 130, 180, 50));
				g.drawString("Click", mouse_press_x, mouse_press_y);
			} else if ((System.currentTimeMillis() - mouse_press_time < 1000)
					&& (System.currentTimeMillis() - mouse_press_time > 899)) {
				g.setColor(new Color(70, 130, 180, 25));
				g.drawString("Click", mouse_press_x, mouse_press_y);
			}
			Polygon po = new Polygon();
			po.addPoint(mouse_x, mouse_y);
			po.addPoint(mouse_x, mouse_y + 15);
			po.addPoint(mouse_x + 10, mouse_y + 10);
			g.setColor(new Color(70, 130, 180, 125));
			g.fillPolygon(po);
			g.drawPolygon(po);
			int lY = (int) getInterface(752).getChild(7).getArea().getY();
			int lW = (int) getInterface(137).getChild(0).getArea().getWidth();
			g.setFont(new Font("Century Gothic", Font.PLAIN, 13));
			g.setColor(Color.BLACK);
			g.drawRoundRect(lW - 90, lY - 15, 100, 15, 10, 10);
			g.setColor(new Color(0, 0, 0, 90));
			g.fillRoundRect(lW - 90, lY - 15, 100, 15, 10, 10);
			g.setColor(Color.WHITE);
			g.drawString(state, lW - 81, lY - 3);
			int x = 0;
			int y = 0;
			long millis = System.currentTimeMillis() - start;
			final long hours = millis / (1000 * 60 * 60);
			millis -= hours * 1000 * 60 * 60;
			final long minutes = millis / (1000 * 60);
			millis -= minutes * 1000 * 60;
			final long seconds = millis / 1000;
			paintBar(g, x, y, "FoulFighter Time Running : " + hours + ":"
					+ minutes + ":" + seconds);
			String ver = Double.toString(getVersion());
			if (slayerLeft() != 0) {
				String sl = Integer.toString(slayerLeft());
				g.drawString("Left of Task: " + sl, 240, y + 13);
			}
			g.drawString("Version " + ver, 436, y + 13);
			y += 15;
			for (int i = 0; i < 7; i++) {
				if ((startExp != null)
						&& ((skills.getCurrentSkillExp(i) - startExp[i]) > 0)) {
					paintSkillBar(g, x, y, i, startExp[i]);
					y += 15;
				}
			}
			if ((startExp != null)
					&& (skills.getCurrentSkillExp(18) - startExp[18] > 0)) {
				paintSkillBar(g, x, y, 18, startExp[18]);
				y += 15;
			}
			g.setColor(new Color(255, 0, 0, 90));
			g.fillRoundRect(416, y + 3, 100, 9, 10, 10);
			g.setColor(Color.GREEN);
			g.fillRoundRect(416, y + 3, getSetting(300) / 10, 9, 10, 10);
			g.setColor(Color.BLACK);
			g.drawRoundRect(380, y, 136, 15, 10, 10);
			g.setColor(Color.BLACK);
			g.drawRoundRect(416, y + 3, getSetting(300) / 10, 9, 10, 10);
			g.setColor(Color.BLACK);
			g.drawRoundRect(416, y + 3, 100, 9, 10, 10);
			g.setColor(new Color(0, 0, 0, 90));
			g.fillRoundRect(380, y, 136, 15, 10, 10);
			g.setColor(Color.WHITE);
			g.drawString(Integer.toString(getSetting(300) / 10) + "%", 385,
					y + 13);
		}
	}

	public void paintSkillBar(Graphics g, int x, int y, int skill, int start) {
		if (paint) {
			g.setFont(new Font("Century Gothic", Font.PLAIN, 13));
			int gained = (skills.getCurrentSkillExp(skill) - start);
			String s = SkillToString(skill) + " Exp Gained: " + gained;
			String firstLetter = s.substring(0, 1);
			String remainder = s.substring(1);
			String capitalized = firstLetter.toUpperCase() + remainder;
			String exp = Integer.toString(skills.getXPToNextLevel(skill));
			g.setColor(new Color(255, 0, 0, 90));
			g.fillRoundRect(416, y + 3, 100, 9, 10, 10);
			g.setColor(Color.BLACK);
			g.drawRoundRect(416, y + 3, 100, 9, 10, 10);
			g.setColor(new Color(0, 255, 0, 255));
			g.fillRoundRect(416, y + 3, skills.getPercentToNextLevel(skill), 9,
					10, 10);
			g.setColor(Color.BLACK);
			g.drawRoundRect(416, y + 3, skills.getPercentToNextLevel(skill), 9,
					10, 10);
			g.setColor(new Color(0, 200, 255));
			paintBar(g, x, y, capitalized);
			g.drawString("Exp To Level: " + exp, 240, y + 13);
		}
	}

	public void paintBar(Graphics g, int x, int y, String s) {
		g.setFont(new Font("Century Gothic", Font.PLAIN, 13));
		int width = 516;
		int height = (int) g.getFontMetrics().getStringBounds(s, g).getHeight();
		g.setColor(Color.BLACK);
		g.drawRoundRect(0, y, width, height, 10, 10);

		g.setColor(new Color(0, 0, 0, 90));
		g.fillRoundRect(0, y, width, height, 10, 10);

		g.setColor(new Color(255, 255, 255));
		g.drawString(s, x + 7, y + height - 2);
	}

	private String SkillToString(int skill) {
		return Skills.statsArray[skill];
	}

	class FoulFighterGUI extends JFrame implements ListSelectionListener,
			ActionListener {

		private static final long serialVersionUID = 1L;
		private DefaultListModel model;
		private DefaultListModel model1;
		private DefaultListModel model2;
		private DefaultListModel model3;
		private JPanel contentPane;
		private JTextField txtItemId;
		private JTextField txtAlchable;
		private JTextField txtItemName;
		private JTextField txtOrigWeap;
		private JTextField txtSpecWeap;
		private JTextField txtOrigShield;
		private JTextField txtSpecPercent;
		private JList list;
		private JList list_1;
		private JList list_2;
		private JList list_4;
		private JButton btnStartScript;
		private JButton btnAdd;
		private JButton btnAdd2;
		private JButton btnLoad;
		private JScrollPane scrollPane;
		private JScrollPane scrollPane_1;
		private JScrollPane scrollPane_2;
		private JCheckBox chckbxUseFood;
		private JCheckBox chckbxuseBonesToPeaches;
		private JCheckBox chckbxBuryBones;
		private JCheckBox chckbxCharms;
		private JCheckBox chckbxUsePotion;
		private JCheckBox chckbxStrength;
		private JCheckBox chckbxDefence;
		private JCheckBox chckbxAttack;
		private JCheckBox chckbxPaint;
		private JCheckBox chckbxUseSpec;
		private JLabel MobsInArea;
		private JLabel MobsToAttack;
		private JLabel ItemPickup;
		private JLabel AddItem;
		private JLabel Alch;
		private JLabel AddAlch;
		private JLabel AddNote;

		public FoulFighterGUI() {
			setTitle("FoulFighter");
			setBounds(100, 100, 450, 450);
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			contentPane.setLayout(null);
			{
				JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
				tabbedPane.setBounds(10, 10, 414, 354);
				contentPane.add(tabbedPane);
				{
					JPanel panel = new JPanel();
					tabbedPane.addTab("Fighting", null, panel, null);
					panel.setLayout(null);
					{
						model = new DefaultListModel();
						{
							MobsToAttack = new JLabel();
							MobsToAttack.setBounds(10, 150, 390, 9);
							MobsToAttack
									.setText("These are the NPC's you will attack. Click a NPC to remove it from the attack list");
							panel.add(MobsToAttack);
							scrollPane = new JScrollPane();
							scrollPane.setBounds(10, 160, 390, 125);
							panel.add(scrollPane);
							list_1 = new JList(model);
							scrollPane.setViewportView(list_1);
							list_1.addListSelectionListener(this);
							list_1
									.setBorder(new LineBorder(
											new Color(0, 0, 0)));
						}
					}
					{
						model1 = new DefaultListModel();
						{
							MobsInArea = new JLabel();
							MobsInArea.setBounds(10, 10, 390, 9);
							MobsInArea
									.setText("These are the NPC's in your area. Click a NPC to add it to the attack list");
							panel.add(MobsInArea);
							scrollPane_1 = new JScrollPane();
							scrollPane_1.setBounds(10, 20, 390, 125);
							panel.add(scrollPane_1);
							list_2 = new JList(model1);
							scrollPane_1.setViewportView(list_2);
							list_2.addListSelectionListener(this);
							list_2
									.setBorder(new LineBorder(
											new Color(0, 0, 0)));
						}
					}
					{
						chckbxUseFood = new JCheckBox(
								"Eat Food (Eats when your HP is between HPLvl/2 and HPLvl/1.5)");
						chckbxUseFood.setBounds(10, 300, 390, 13);
						panel.add(chckbxUseFood);
					}
				}
				{
					JPanel panel = new JPanel();
					tabbedPane.addTab("Items", null, panel, null);
					panel.setLayout(null);
					{
						scrollPane_2 = new JScrollPane();
						model2 = new DefaultListModel();
						ItemPickup = new JLabel();
						ItemPickup.setBounds(10, 7, 390, 12);
						ItemPickup
								.setText("These Items are to be picked up. Click an Item to remove it from the pickup list");
						panel.add(ItemPickup);
						list = new JList(model2);
						list.setBorder(new LineBorder(new Color(0, 0, 0)));
						scrollPane_2.setBounds(10, 20, 390, 125);
						panel.add(scrollPane_2);
						scrollPane_2.setViewportView(list);
						list.addListSelectionListener(this);
					}
					{
						AddItem = new JLabel();
						AddItem.setBounds(10, 150, 100, 12);
						AddItem.setText("Item ID");
						panel.add(AddItem);
					}
					{
						AddItem = new JLabel();
						AddItem.setBounds(130, 150, 100, 12);
						AddItem.setText("Item Name");
						panel.add(AddItem);
					}
					{
						btnAdd = new JButton("Add");
						btnAdd.addActionListener(this);
						btnAdd.setBounds(230, 163, 79, 23);
						panel.add(btnAdd);
					}
					{
						btnLoad = new JButton("Load");
						btnLoad.addActionListener(this);
						btnLoad.setBounds(319, 163, 79, 23);
						panel.add(btnLoad);
					}
					{
						txtItemId = new JTextField();
						txtItemId.setBounds(10, 163, 100, 19);
						panel.add(txtItemId);
						txtItemId.setColumns(20);
					}
					{
						txtItemName = new JTextField();
						txtItemName.setBounds(120, 163, 100, 19);
						panel.add(txtItemName);
						txtItemName.setColumns(20);
					}
					{
						chckbxBuryBones = new JCheckBox(
								"Bury Bones (Will bury when inventory is full)");
						chckbxBuryBones.addActionListener(this);
						chckbxBuryBones.setBounds(10, 217, 390, 13);
						panel.add(chckbxBuryBones);
					}
					{
						chckbxCharms = new JCheckBox(
								"Pickup Charms (Gold, Green, Crimson, Blue)");
						chckbxCharms.addActionListener(this);
						chckbxCharms.setBounds(10, 188, 390, 13);
						panel.add(chckbxCharms);
					}
					{
						chckbxuseBonesToPeaches = new JCheckBox(
								"Bones to Peaches (Uses tab when out of food)");
						chckbxuseBonesToPeaches.addActionListener(this);
						chckbxuseBonesToPeaches.setBounds(10, 247, 390, 13);
						panel.add(chckbxuseBonesToPeaches);
					}
				}
				{
					JPanel panel = new JPanel();
					tabbedPane.addTab("Alch Items", null, panel, null);
					panel.setLayout(null);
					{
						model3 = new DefaultListModel();
						Alch = new JLabel();
						Alch.setBounds(10, 7, 380, 12);
						Alch
								.setText("These are the Items you will cast high alch on if in your inventory");
						panel.add(Alch);
						list_4 = new JList(model3);
						list_4.setBorder(new LineBorder(new Color(0, 0, 0)));
						list_4.setBounds(10, 20, 390, 125);
						panel.add(list_4);
						list_4.addListSelectionListener(this);
					}
					{
						btnAdd2 = new JButton("Add");
						btnAdd2.addActionListener(this);
						btnAdd2.setBounds(180, 163, 89, 23);
						panel.add(btnAdd2);
					}
					{
						AddAlch = new JLabel();
						AddAlch.setBounds(10, 150, 390, 12);
						AddAlch
								.setText("If adding an item to alch only add the items ID, item name is not needed");
						panel.add(AddAlch);
					}
					{
						txtAlchable = new JTextField();
						txtAlchable.setBounds(10, 163, 150, 19);
						panel.add(txtAlchable);
						txtAlchable.setText("");
						txtAlchable.setColumns(20);
					}
					{
						AddNote = new JLabel();
						AddNote.setBounds(10, 200, 390, 12);
						AddNote
								.setText("Note: High Alching currently only works with runes does not support staffs");
						panel.add(AddNote);
					}
				}
				{
					JPanel panel = new JPanel();
					tabbedPane.addTab("Potions", null, panel, null);
					panel.setLayout(null);
					{
						chckbxUsePotion = new JCheckBox("Use Potions");
						chckbxUsePotion.addActionListener(this);
						chckbxUsePotion.setBounds(10, 10, 390, 13);
						panel.add(chckbxUsePotion);
					}
					{
						chckbxStrength = new JCheckBox(
								"Strength Potions (Super, Regular or Combat)");
						chckbxStrength.addActionListener(this);
						chckbxStrength.setBounds(30, 50, 370, 13);
						panel.add(chckbxStrength);
					}
					{
						chckbxAttack = new JCheckBox(
								"Attack Potion (Super, Regular or Combat)");
						chckbxAttack.addActionListener(this);
						chckbxAttack.setBounds(30, 70, 370, 13);
						panel.add(chckbxAttack);
					}
					{
						chckbxDefence = new JCheckBox(
								"Defence Potion (Super and Regular)");
						chckbxDefence.addActionListener(this);
						chckbxDefence.setBounds(30, 90, 370, 13);
						panel.add(chckbxDefence);
					}
					{
						btnStartScript = new JButton("Start Script");
						btnStartScript.addActionListener(this);
						btnStartScript.setBounds(10, 375, 414, 23);
						contentPane.add(btnStartScript);
					}
				}
				{
					JPanel panel = new JPanel();
					tabbedPane.addTab("Spec", null, panel, null);
					panel.setLayout(null);
					{
						chckbxUseSpec = new JCheckBox("Use Weap Special");
						chckbxUseSpec.addActionListener(this);
						chckbxUseSpec.setBounds(10, 10, 390, 13);
						panel.add(chckbxUseSpec);
					}
					{
						AddNote = new JLabel();
						AddNote.setBounds(30, 38, 390, 12);
						AddNote.setText("Original Weap ID");
						panel.add(AddNote);
					}
					{
						txtOrigWeap = new JTextField();
						txtOrigWeap.setBounds(30, 50, 100, 19);
						panel.add(txtOrigWeap);
						txtOrigWeap.setColumns(20);
					}
					{
						AddNote = new JLabel();
						AddNote.setBounds(30, 74, 390, 12);
						AddNote.setText("Original Shield ID");
						panel.add(AddNote);
					}
					{
						txtOrigShield = new JTextField();
						txtOrigShield.setBounds(30, 87, 100, 19);
						panel.add(txtOrigShield);
						txtOrigShield.setColumns(20);
					}
					{
						AddNote = new JLabel();
						AddNote.setBounds(30, 111, 390, 12);
						AddNote.setText("Spec Weapon ID");
						panel.add(AddNote);
					}
					{
						txtSpecWeap = new JTextField();
						txtSpecWeap.setBounds(30, 124, 100, 19);
						panel.add(txtSpecWeap);
						txtSpecWeap.setColumns(20);
					}
					{
						AddNote = new JLabel();
						AddNote.setBounds(30, 148, 390, 12);
						AddNote.setText("Weapon Spec %");
						panel.add(AddNote);
					}
					{
						txtSpecPercent = new JTextField();
						txtSpecPercent.setBounds(30, 161, 100, 19);
						panel.add(txtSpecPercent);
						txtSpecPercent.setColumns(20);
					}
				}
				{
					JPanel panel = new JPanel();
					tabbedPane.addTab("Other Options", null, panel, null);
					panel.setLayout(null);
					{
						chckbxPaint = new JCheckBox("Disable Paint");
						chckbxPaint.addActionListener(this);
						chckbxPaint.setBounds(10, 10, 390, 13);
						panel.add(chckbxPaint);
					}
					setVisible(true);
					npcupdater.start();
				}
			}
		}

		public void valueChanged(final ListSelectionEvent arg0) {
			if (arg0.getSource() == list) {
				String i = (String) list.getSelectedValue();
				if (i == null) {
					return;
				}
				model2.remove(list.getSelectedIndex());
			}
			if (arg0.getSource() == list_2) {
				String text = (String) list_2.getSelectedValue();
				if ((text == null) || text.isEmpty()) {
					return;
				}
				model.addElement(text);
				model1.remove(list_2.getSelectedIndex());
			}
			if (arg0.getSource() == list_1) {
				String text = (String) list_1.getSelectedValue();
				if ((text == null) || text.isEmpty()) {
					return;
				}
				model1.addElement(text);
				model.remove(list_1.getSelectedIndex());
			}
			if (arg0.getSource() == list_4) {
				Integer i = (Integer) list_4.getSelectedValue();
				if (i == null) {
					return;
				}
				model3.remove(list_4.getSelectedIndex());
			}
		}

		public void actionPerformed(final ActionEvent arg0) {
			if (arg0.getSource() == chckbxuseBonesToPeaches) {
				useBTP = chckbxuseBonesToPeaches.isSelected();
			}
			if (arg0.getSource() == chckbxBuryBones) {
				buryBones = chckbxBuryBones.isSelected();
			}
			if (arg0.getSource() == chckbxPaint) {
				paint = !chckbxPaint.isSelected();
			}
			if (arg0.getSource() == chckbxUsePotion) {
				usepotion = chckbxUsePotion.isSelected();
			}
			if (arg0.getSource() == chckbxStrength) {
				strength = chckbxStrength.isSelected();
			}
			if (arg0.getSource() == chckbxDefence) {
				defence = chckbxDefence.isSelected();
			}
			if (arg0.getSource() == chckbxAttack) {
				attack = chckbxAttack.isSelected();
			}
			if (arg0.getSource() == chckbxCharms) {
				charms = chckbxCharms.isSelected();
			}
			if (arg0.getSource() == chckbxUseSpec) {
				useSpec = chckbxUseSpec.isSelected();
			}
			if (arg0.getSource() == btnAdd) {
				try {
					String s = (txtItemId.getText());
					String j = (txtItemName.getText());
					String firstLetter = j.substring(0, 1);
					String remainder = j.substring(1);
					String n = firstLetter.toUpperCase()
							+ remainder.toLowerCase();
					model2.addElement(s + "," + n);
					txtItemId.setText("");
					txtItemName.setText("");
				} catch (Exception ignored) {
				}
			}
			if (arg0.getSource() == btnAdd2) {
				try {
					int i = Integer.parseInt(txtAlchable.getText());
					model3.addElement(i);
				} catch (Exception ignored) {
				}
			}
			if (arg0.getSource() == btnLoad) {
				try {
					JFileChooser fc = new JFileChooser();
					if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
						BufferedReader in = new BufferedReader(new FileReader(
								fc.getSelectedFile().getPath()));
						String str;
						while ((str = in.readLine()) != null) {
							if (!model2.contains(str)) {
								String delims = "[,]";
								String[] tokens = str.split(delims);
								String s = (tokens[1]);
								String firstLetter = s.substring(0, 1);
								String remainder = s.substring(1);
								String cap = firstLetter.toUpperCase()
										+ remainder.toLowerCase();
								model2.addElement(tokens[0] + "," + cap);
							}
						}
						in.close();
					}
				} catch (IOException e) {
				}
			}
			if (arg0.getSource() == btnStartScript) {
				npcID = new int[model.size()];
				for (int i = 0; i < model.getSize(); i++) {
					String idNameLevel = (String) model.get(i);
					String delims = "[ -]";
					String[] tokens = idNameLevel.split(delims);
					npcID[i] = Integer.parseInt(tokens[0]);
				}
				itemids = new int[model2.size()];
				for (int i = 0; i < model2.getSize(); i++) {
					String delims = "[,]";
					String[] tokens = ((String) model2.get(i)).split(delims);
					int in = Integer.parseInt(tokens[0]);
					itemids[i] = in;
				}
				itemnames = new String[model2.size()];
				for (int i = 0; i < model2.getSize(); i++) {
					String delims = "[,]";
					String[] tokens = ((String) model2.get(i)).split(delims);
					itemnames[i] = tokens[1];
				}
				alchable = new int[model3.size()];
				for (int i = 0; i < model3.getSize(); i++) {
					int in = (Integer) model3.get(i);
					alchable[i] = in;
					log("Alching - " + alchable[i]);
				}
				if (txtOrigWeap.getText() == null
						|| txtOrigWeap.getText().isEmpty()) {
					origWeap = 0;
				} else {
					origWeap = Integer.parseInt(txtOrigWeap.getText());
				}
				if (txtOrigShield.getText() == null
						|| txtOrigShield.getText().isEmpty()) {
					origShield = 0;
				} else {
					origShield = Integer.parseInt(txtOrigShield.getText());
				}
				if (txtSpecWeap.getText() == null
						|| txtSpecWeap.getText().isEmpty()) {
					specWeap = 0;
				} else {
					specWeap = Integer.parseInt(txtSpecWeap.getText());
				}
				if (txtSpecPercent.getText() == null
						|| txtSpecPercent.getText().isEmpty()) {
					specPercent = 0;
				} else {
					specPercent = Integer.parseInt(txtSpecPercent.getText());
				}
				useFood = chckbxUseFood.isSelected();
				dispose();
			}
		}

		Thread npcupdater = new Thread() {
			@Override
			public void run() {
				while (isVisible()) {
					final int[] validNPCs = Bot.getClient()
							.getRSNPCIndexArray();

			        for (final int element : validNPCs) {
			        	Node node = Calculations.findNodeByID(Bot.getClient().getRSNPCNC(), element);
			            if (node == null || !(node instanceof RSNPCNode)) {
			                continue;
			            }
			            final RSNPC Monster = new RSNPC(((RSNPCNode) node).getRSNPC());
						if (!model1.contains(Monster.getID() + " -" + " Name: "
								+ Monster.getName() + " [" + Monster.getLevel()
								+ "]")
								&& !model.contains(Monster.getID() + " -"
										+ " Name: " + Monster.getName() + " ["
										+ Monster.getLevel() + "]")
								&& (Monster.getLevel() != 0)) {
							model1.add(model1.getSize(), Monster.getID() + " -"
									+ " Name: " + Monster.getName() + " ["
									+ Monster.getLevel() + "]");
						}
					}
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
	}
}