import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.ListCellRenderer;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;

import org.rsbot.bot.Bot;
import org.rsbot.bot.input.Mouse;
import org.rsbot.script.Calculations;
import org.rsbot.script.Methods;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.Skills;
import org.rsbot.script.wrappers.RSCharacter;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSInterfaceComponent;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSPlayer;
import org.rsbot.script.wrappers.RSTile;

import org.rsbot.accessors.Client;
import org.rsbot.accessors.LDModel;
import org.rsbot.accessors.Node;
import org.rsbot.accessors.NodeCache;
import org.rsbot.accessors.RSAnimable;
import org.rsbot.accessors.RSAnimableNode;
import org.rsbot.accessors.RSGround;
import org.rsbot.accessors.RSNPCNode;

import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;

import org.rsbot.util.GlobalConfiguration;

@ScriptManifest(name = "ParaFishNCook", authors = {"Parameter"}, version = ParaFishNCook.VERSION,
		category = "Fishing", description = "<html><head><style type='text/css'>" +
		"body {background-color: #DAE2F0} " +
		"h1 {font-family: 'Verdana'} " +
		"div.title {background-color: #B9CBED} " +
		"div.descr {background-color: 055BFA; color: #FFFFFF; font-weight: bold} " +
		"</style></head><body>" +
		"<div class='title'><h1>ParaFishNCook</h1></div> " +
		"<div class='descr'>v" + ParaFishNCook.VERSION + " | Parameter | PowerFisher + Cooker</div>" +
		"<p>Start near the fishing spot you want to use. " +
		"If you're cooking, make sure that there are also some trees which you can chop " +
		"and you have a hatchet and a tinderbox in your inventory.<br/>" +
		"<b>Note: This is a powerfisher - it will drop the fish after " +
		"it has been fished/cooked.<br/><br/>" +
		"All options can be found in GUI.</b></p></body></html>")
public class ParaFishNCook extends Script implements PaintListener, ServerMessageListener {

	public static final double VERSION = 1.15;

	private static final int SETTINGS_VERSION = 3;
	
	private static final int FISHING = 0, CHOPPING = 1,
		FIRING = 2, COOKING = 3, DROPPING = 4;
	private int status = FISHING;

	private static final int TINDERBOX_ID = 590, FIRE_ID = 2732,
		FIRING_ANIMATION = 733, COOKING_ANIMATION = 897,
		UNSET_OBJECTIVE_TEXTURE = 65535;

	private final RSInterface magicIface = getInterface(INTERFACE_TAB_MAGIC);
	private final RSInterfaceChild cookingIface = getInterface(513, 73),
		levelUpIface = getInterface(740, 3), objectivePicIface = getInterface(891, 14),
		objectiveBarIface = getInterface(891, 19), prayerIface = getInterface(271, 8);
	private Rectangle cookingIfaceArea;
	private static final Rectangle failCookingIfaceArea = new Rectangle(0, 0, 59, 78);

	private RSNPC fishingSpot;
	private RSTile fishingSpotTile;

	private Set<Tree> trees = new HashSet<Tree>();
	private final Set<Tree> unavailableTrees = new HashSet<Tree>();

	private boolean update = true, hasHatchet = true, run;

	private long nextMouseTime = System.currentTimeMillis() + random(500, 7500),
		nextExamineTime = System.currentTimeMillis() + random(5000, 120000),
		nextStatTime = System.currentTimeMillis() + random(2500, 180000),
		nextObjectiveTime = System.currentTimeMillis() + random(30000, 500000),
		nextPrayerTime = System.currentTimeMillis() + random(60000, 1800000),
		nextMagicTime = System.currentTimeMillis() + random(60000, 1800000);
	private boolean viewObjective = true;
	private int minimumRunPercent = random(15, 61);
	private final int[] primaryStatIfaceIDs = {38, 62},
		secondaryStatIfaceIDs = {85, 102},
		objectiveTexIDs = {1492, 1493, 1494, 1495},
		prayerLevels = {1, 4, 7, 8, 9, 10, 13, 16, 19, 22, 25, 26,
			27, 28, 31, 34, 35, 37, 40, 43, 44, 45, 46, 49, 52, 60, 70};

	private static Methods m;
	{
		m = this;
	}

	private FontMetrics fontMetrics;

	private int trips, fishCaught, fishingLvlsGained,
		logsChopped, wcLvlsGained, logsBurned, fmLvlsGained,
		fishCooked, cookingLvlsGained, defaultTab = -1;
	private double fishingXPGained, wcXPGained, fmXPGained, cookingXPGained;
	private long startTime = System.currentTimeMillis();
	private boolean mouseClicked = false;

	private int hoverInvCount = -1;

	private FishingStyle curStyle;
	private int curStyleIndex;
	private int mouseSpeed = 5;
	private boolean cook = true, fastPowerFish = false, drawPaint = true, drawTrees, check2ndSkills = true;
	private Point guiLocation = new Point(250, 250);

	private int gearFails, tinderBoxFails;

	private final File settingsFile = new File(GlobalConfiguration.Paths.getSettingsDirectory() +
			File.separator + "ParaFishNCook.dat");

	private final CameraAntiBan cameraAntiBan = new CameraAntiBan();
	private final Gui gui = new Gui();

	private static final class LevelInfo {
		private final int statID, levelRequired /*, xpGain*/;
		//private int xpGained;

		public LevelInfo(final int statID, final int levelRequired, final int xpGain) {
			this.statID = statID;
			this.levelRequired = levelRequired;
			//this.xpGain = xpGain;
		}

		public LevelInfo(final int statID, final int levelRequired) {
			this(statID, levelRequired, -1);
		}

		public boolean check() {
			return statID == -1 || m.skills.getCurrentSkillLevel(statID) >= levelRequired;
		}

		public int getStatID() {
			return this.statID;
		}

		public int getLevelRequired() {
			return this.levelRequired;
		}

		/*public void addXPGain() {
			if(xpGain != -1) {
				xpGained += xpGain;
			}
		}

		public int getXPGain() {
			return this.xpGain;
		}*/
	}

	private static enum FishingStyle {
		NET        ("Net Fishing", "Net", 1, new int[] {621}, new int[] {303},
				new Fish[] {Fish.SHRIMPS, Fish.ANCHOVIES}, 323, 325, 327),
		CRAYFISH   ("Crayfish Fishing", "Cage", 1, new int[] {10009}, new int[] {13431},
				new Fish[] {Fish.CRAYFISH}, 6996, 6267),
		SEA_BAIT   ("Sea Bait Fishing", "Bait", 5, new int[] {622, 623}, new int[] {307, 313},
				new Fish[] {Fish.SARDINE, Fish.HERRING}, 323, 327),
		RIVER_BAIT ("River Bait Fishing", "Bait", 25, new int[] {622, 623}, new int[] {307, 313},
				new Fish[] {Fish.PIKE}, 328, 329),
		LURE       ("Fly Fishing", "Lure", 20, new int[] {622, 623}, new int[] {309, 314},
				new Fish[] {Fish.TROUT, Fish.SALMON}, 328, 329),
		HARPOON    ("Harpoon Fishing", "Harpoon", 35, new int[] {618}, new int[] {311},
				new Fish[] {Fish.TUNA, Fish.SWORDFISH}, 324),
		CAGE       ("Lobster Cage Fishing", "Cage", 40, new int[] {619}, new int[] {301},
				new Fish[] {Fish.LOBSTER}, 324),
		HEAVY_ROD  ("Heavy Rod Fishing", "Use-rod",
				new LevelInfo[] {
					new LevelInfo(STAT_FISHING, 48),
					new LevelInfo(STAT_AGILITY, 15),
					new LevelInfo(STAT_STRENGTH, 15)
				}, new int[] {622, 623}, new int[] {11323, 314},
				new Fish[] {Fish.LEAPING_TROUT, Fish.LEAPING_SALMON, Fish.LEAPING_STURGEON},
				2722);

		private final String name, action;
		private final LevelInfo[] levelInformation;
		private final int[] animations, gearIDs, spotIDs;
		private final Fish[] catches;
		private final int minFishingLvl, maxFishingLvl;
		private final boolean containsAnyCookableFish;

		private FishingStyle(final String name, final String action,
				final LevelInfo[] levelInformation, final int[] animations,
				final int[] gearIDs, final Fish[] catches, final int... spotIDs) {
			this.name = name;
			this.action = action;
			this.levelInformation = levelInformation;
			this.animations = animations;
			this.gearIDs = gearIDs;
			this.catches = catches;
			this.spotIDs = spotIDs;
			this.minFishingLvl = calculateMinFishingLvl(catches);
			this.maxFishingLvl = calculateMaxFishingLvl(catches);

			boolean cookable = false;
			for(final Fish f : catches) {
				if(!cookable && f.isCookable()) {
					cookable = true;
					break;
				}
			}

			this.containsAnyCookableFish = cookable;
		}

		private FishingStyle(final String name, final String action,
				final int fishingLvl, final int[] animations,
				final int[] gearIDs, final Fish[] catches, final int... spotIDs) {
			this(name, action,
					new LevelInfo[] {
						new LevelInfo(STAT_FISHING, fishingLvl)},
						animations, gearIDs, catches, spotIDs);
		}

		private static int calculateMinFishingLvl(final Fish... catches) {
			int minLvl = 99;
			for(final Fish f : catches) {
				if(f.getFishingLvl() < minLvl) {
					minLvl = f.getFishingLvl();
				}
			}
			return minLvl;
		}

		private static int calculateMaxFishingLvl(final Fish... catches) {
			int maxLvl = 1;
			for(final Fish f : catches) {
				if(f.getFishingLvl() > maxLvl) {
					maxLvl = f.getFishingLvl();
				}
			}
			return maxLvl;
		}

		public String getName() {
			return this.name;
		}

		public LevelInfo[] getLevelInformation() {
			return this.levelInformation;
		}

		public int[] getAnimations() {
			return this.animations;
		}

		public int[] getGearIDs() {
			return this.gearIDs;
		}

		public Fish[] getCatches() {
			return this.catches;
		}

		public int[] getCatchIDs() {
			final int[] ids = new int[catches.length * 3];
			for(int i = 0; i < catches.length; i++) {
				ids[i * 3] = catches[i].getID();
				ids[i * 3 + 1] = catches[i].getCookedID();
				ids[i * 3 + 2] = catches[i].getBurntID();
			}
			return ids;
		}

		public int[] getSpotIDs() {
			return this.spotIDs;
		}

		public int getMinFishingLvl() {
			return this.minFishingLvl;
		}

		public int getMaxFishingLvl() {
			return this.maxFishingLvl;
		}

		public boolean containsAnyCookableFish() {
			return this.containsAnyCookableFish;
		}

		public String toString() {
			return this.action;
		}
	}

	private static enum Fish {
		// Net fishing
		SHRIMPS       ("Shrimps", "Burnt shrimp", 317, 315, 7954, 1, 1, 10, 30),
		ANCHOVIES     ("Anchovies", 321, 319, 323, 15, 15, 40, 30),
		// Crayfish fishing
		CRAYFISH      ("Crayfish", "Burnt crayfish", 13435, 13433, 13437, 1, 1, 10, 30),
		// Sea Bait fishing
		SARDINE       ("Sardine", 327, 325, 369, 5, 1, 20, 40),
		HERRING       ("Herring", 345, 347, 357, 10, 5, 30, 50),
		// River Bait fishing
		PIKE          ("Pike", 349, 351, 343, 25, 20, 60, 80),
		// Fly fishing
		TROUT         ("Trout", 335, 333, 343, 20, 15, 50, 70),
		SALMON        ("Salmon", 331, 329, 343, 30, 25, 70, 90),
		// Harpoon fishing
		TUNA          ("Tuna", 359, 361, 367, 35, 30, 80, 100),
		SWORDFISH     ("Swordfish", "Burnt swordfish", 371, 373, 375, 50, 45, 100, 140),
		// Lobster cage fishing
		LOBSTER       ("Lobster", "Burnt lobster", 377, 379, 381, 40, 40, 90, 120),
		// Heavy rod fishing
		LEAPING_TROUT  ("Leaping trout", 11328, 48, 50),
		LEAPING_SALMON ("Leaping salmon", 11330, 58, 70),
		LEAPING_STURGEON ("Leaping sturgeon", 11332, 70, 80);

		private final String name, burntName;
		private final int id, cookedId, burntId, fishingLvl, cookingLvl;
		private final double fishingXPGain, cookingXPGain;
		private final boolean cookable;

		private Fish(final String name, final String burntName,
				final int id, final int cookedId, final int burntId,
				final int fishingLvl, final int cookingLvl,
				final double fishingXPGain, final double cookingXPGain) {
			this.name = name;
			this.burntName = burntName;
			this.id = id;
			this.cookedId = cookedId;
			this.burntId = burntId;
			this.fishingLvl = fishingLvl;
			this.cookingLvl = cookingLvl;
			this.fishingXPGain = fishingXPGain;
			this.cookingXPGain = cookingXPGain;
			this.cookable = true;
		}

		private Fish(final String name, final int id, final int cookedId,
				final int burntId, final int fishingLvl, final int cookingLvl,
				final double fishingXPGain, final double cookingXPGain) {
			this(name, "Burnt fish", id, cookedId, burntId,
					fishingLvl, cookingLvl, fishingXPGain, cookingXPGain);
		}

		private Fish(final String name, final int id, final int fishingLvl, final int fishingXPGain) {
			this.name = name;
			this.burntName = "UNUSED";
			this.id = id;
			this.cookedId = 0;
			this.burntId = 0;
			this.fishingLvl = fishingLvl;
			this.cookingLvl = 0;
			this.fishingXPGain = fishingXPGain;
			this.cookingXPGain = 0;
			this.cookable = false;
		}

		public String toString() {
			return this.name;
		}

		public String getBurntName() {
			return this.burntName;
		}

		public int getID() {
			return this.id;
		}

		public int getCookedID() {
			return this.cookedId;
		}

		public int getBurntID() {
			return this.burntId;
		}

		public double getFishingXPGain() {
			return this.fishingXPGain;
		}

		public double getCookingXPGain() {
			return this.cookingXPGain;
		}

		public int getFishingLvl() {
			return this.fishingLvl;
		}

		public int getCookingLvl() {
			return this.cookingLvl;
		}

		public boolean isCookable() {
			return this.cookable;
		}

		public static Fish getCookableInvFish() {
			final int lvl = m.skills.getCurrentSkillLevel(STAT_COOKING);

			for(final Fish f : values()){
				if(f.cookingLvl <= lvl && m.inventoryContains(f.id)) {
					return f;
				}
			}

			return null;
		}

		public static int getJunkInvFishID() {
			for(final Fish f : values()) {
				if(m.inventoryContains(f.burntId)) {
					return f.burntId;
				}
			}
			for(final Fish f : values()) {
				if(m.inventoryContains(f.cookedId)) {
					return f.cookedId;
				}
			}
			for(final Fish f : values()) {
				if(m.inventoryContains(f.id)) {
					return f.id;
				}
			}
			return -1;
		}
	}

	private static final class Tree {
		private final RSTile location;
		private final TreeType type;

		private Tree(final RSTile location, final TreeType type) {
			this.location = location;
			this.type = type;
		}

		public RSTile getLocation() {
			return this.location;
		}

		public TreeType getType() {
			return this.type;
		}

		public boolean isAvailable() {
			final RSObject obj = m.getObjectAt(this.location);
			return obj != null && arrayContains(obj.getID(), type.availableIDs);
		}

		public int hashCode() {
			return location.hashCode();
		}

		public boolean equals(final Object obj) {
			if(this == obj) {
				return true;
			}
			if(!(obj instanceof Tree)) {
				return false;
			}
			final Tree t = (Tree)obj;
			return t.location.equals(this.location);
		}

		public static Set<Tree> scanForTrees() {
			final Set<Tree> trees = new HashSet<Tree>();

			try {
				System.out.println("---------------------------------");
				final Client client = Bot.getClient();
				final RSGround[][] rsGround = client.getRSGroundArray()[client.getPlane()];
				final int baseX = client.getBaseX(), baseY = client.getBaseY();

				for(int x = 0; x < 104; x++) {
					for(int y = 0; y < 104; y++) {
						final RSTile curTile = new RSTile(x + baseX, y + baseY);
						if(containsTreeTile(trees, curTile)) {
							continue;
						}
						for(RSAnimableNode node = rsGround[x][y].getRSAnimableList();
								node != null; node = node.getNext()) {
							final RSAnimable animable = node.getRSAnimable();
							if(animable != null) {
								org.rsbot.accessors.RSObject rsObj = null;
								try {
									rsObj = (org.rsbot.accessors.RSObject)animable;
								} catch (final ClassCastException e) {
									continue;
								}
								final TreeType type = TreeType.idToType(rsObj.getID());
								if(type != null) {
									final RSTile t = curTile;
									trees.add(new Tree(t, type));
									//System.out.println("Found tree at: " + t + " [" + type + "] Edge: " +
									//		type.getEdgeSize());
								}
							}
						}
					}
				}
			} catch (final Exception e) {
				//System.out.println(e.getMessage());
				//e.printStackTrace(System.out);
			}

			return trees;
		}

		private static boolean containsTreeTile(final Collection<Tree> trees, final RSTile tile) {
			for(final Tree tree : trees) {
				final RSTile t = tree.getLocation();
				final int edgeSize = tree.getType().getEdgeSize();
				for(int x = t.getX(); x < t.getX() + edgeSize; x++) {
					for(int y = t.getY(); y < t.getY() + edgeSize; y++) {
						if(tile.getX() == x && tile.getY() == y) {
							return true;
						}
					}
				}
			}

			return false;
		}

		public static Tree getNearestUsableTree(final Set<Tree> trees, final Set<Tree> treesToAvoid) {
			final int wcLevel = m.skills.getCurrentSkillLevel(STAT_WOODCUTTING);
			final int fmLevel = m.skills.getCurrentSkillLevel(STAT_FIREMAKING);
			int lastDistance = 999;
			Tree lastTree = null;

			for(final Tree tree : trees) {
				if(treesToAvoid.contains(tree) && m.tileOnScreen(tree.getLocation()) &&
						tree.isAvailable()) {
					treesToAvoid.remove(tree);
				}
				if(!treesToAvoid.contains(tree) &&
						tree.getType().getWcLevel() <= wcLevel &&
						tree.getType().getLog().getFmLevel() <= fmLevel) {
					final int dist = m.distanceTo(tree.getLocation());
					if(lastTree == null || dist < lastDistance) {
						lastTree = tree;
						lastDistance = dist;
					}
				}
			}

			return lastTree;
		}
	}

	private static enum TreeType {
		TREE             ("Tree", 1, Log.NORMAL, new int[] {1276, 1278},
					     new int[] {1342}, 2),
		SMALL_DEADTREE   ("Dead tree", 1, Log.NORMAL, new int[] {1286},
					     new int[] {1351}, 1),
		BIG_DEADTREE     ("Dead tree", 1, Log.NORMAL, new int[] {1282},
						 new int[] {1347}, 2),
		OAK              ("Oak", 15, Log.OAK_LOGS, new int[] {1281, 1383},
						 new int[] {1356, 1358}, 3),
		WILLOW           ("Willow", 30, Log.WILLOW_LOGS, new int[] {5551, 5552, 5553},
						 new int[] {5554}, 2);

		private final String name;
		private final int wcLevel, edgeSize;
		private final Log log;
		private final int[] availableIDs, unavailableIDs;

		private TreeType(final String name, final int wcLevel, final Log log,
				final int[] availableIDs, final int[] unavailableIDs,
				final int edgeSize) {
			this.name = name;
			this.wcLevel = wcLevel;
			this.log = log;
			this.availableIDs = availableIDs;
			this.unavailableIDs = unavailableIDs;
			this.edgeSize = edgeSize;
		}

		public int getWcLevel() {
			return this.wcLevel;
		}

		public Log getLog() {
			return this.log;
		}

		public String toString() {
			return this.name;
		}

		public int getEdgeSize() {
			return this.edgeSize;
		}

		public boolean idMatches(final int id) {
			return id != -1 &&
				(arrayContains(id, availableIDs) || arrayContains(id, unavailableIDs));
		}

		public static TreeType idToType(final int id) {
			if(id == -1) {
				return null;
			}
			for(final TreeType type : values()) {
				if(type.idMatches(id)) {
					return type;
				}
			}
			return null;
		}
	}

	private static enum Log {
		OAK_LOGS    ("Oak logs", 1521, 15, 37.5, 60),
		NORMAL      ("Logs", 1511, 1, 25, 40),
		WILLOW_LOGS ("Willow logs", 1519, 30, 67.5, 30);

		private final String name;
		private final int id, firemakingLvl;
		private final double wcXPGain, fmXPGain;

		private Log(final String name, final int id, final int firemakingLvl,
				final double wcXPGain, final double fmXPGain) {
			this.name = name;
			this.id = id;
			this.firemakingLvl = firemakingLvl;
			this.wcXPGain = wcXPGain;
			this.fmXPGain = fmXPGain;
		}

		public int getID() {
			return this.id;
		}

		public int getFmLevel() {
			return this.firemakingLvl;
		}

		public double getWcXPGain() {
			return this.wcXPGain;
		}

		public double getFmXPGain() {
			return this.fmXPGain;
		}

		public String toString() {
			return this.name;
		}

		public static Log getUsableInvLog() {
			final int fmLevel = m.skills.getCurrentSkillLevel(STAT_FIREMAKING);
			for(final Log log : values()) {
				if(log.firemakingLvl <= fmLevel &&
						m.inventoryContains(log.getID())) {
					return log;
				}
			}
			return null;
		}
	}

	private class CameraAntiBan implements Runnable {
		public static final int OFF = 0, WALKROTATING = 1, RANDOMROTATING = 2;

		private final Thread thread = new Thread(this);

		private int mode = OFF;
		private boolean exit = false;

		public void run() {
			while(!Thread.interrupted() || !exit) {
				if(isPaused) {
					try {
						Thread.sleep(500);
					} catch (final InterruptedException e) {
						if(exit) return;
					}
					continue;
				}
				switch(mode) {
				case OFF:
					try {
						while(mode == OFF) {
							synchronized(this) {
								wait();
							}
						}
					} catch (final InterruptedException e) {
						if(exit) return;
					}
					break;
				case WALKROTATING:
					try {
						Thread.sleep(random(500, 7500));
					} catch (final InterruptedException e) {
						if(exit) return;
						continue;
					}
					final RSTile d = getDestination();
					if(d != null && random(0, 3) != 0) {
						try {
							setCameraRotation(getAngleToTile(d) + random(-50, 51));
						} catch (final InterruptedException e) {
							if(exit) return;
						}
						break;
					}
				case RANDOMROTATING:
					if(mode == RANDOMROTATING) {
						try {
							Thread.sleep(random(500, 7500));
						} catch (final InterruptedException e) {
							if(exit) return;
							continue;
						}
					}
					final char key = (char)(random(0, 3) == 0 ?
							(random(0, 3) == 0 ? KeyEvent.VK_DOWN : KeyEvent.VK_UP) :
								(random(0, 2) == 0 ? KeyEvent.VK_LEFT : KeyEvent.VK_RIGHT));
					input.pressKey(key);
					try {
						Thread.sleep(random(50, 1250));
					} catch (final InterruptedException e) {
						if(exit) return;
						continue;
					} finally {
						input.releaseKey(key);
					}
					break;
				}
			}
		}

		public void start() {
			thread.start();
		}

		public void stop() {
			this.exit = true;
			thread.interrupt();
			try {
				thread.join();
			} catch (final InterruptedException ignored) {
			}
		}

		public void setMode(final int mode) {
			if(this.mode == mode || mode < OFF || mode > RANDOMROTATING) return;
			final int oldMode = this.mode;
			this.mode = mode;
			if(oldMode == OFF) {
				synchronized(this) {
					this.notifyAll();
				}
			} else {
				thread.interrupt();
			}
		}

		private void setCameraRotation(int degrees) throws InterruptedException {
			final char left = 37;
			final char right = 39;
			char whichDir = left;
			int start = getCameraAngle();
			/*
			 * Some of this shit could be simplified, but it's easier to wrap my
			 * mind around it this way
			 */
			if (start < 180) {
				start += 360;
			}
			if (degrees < 180) {
				degrees += 360;
			}
			if (degrees > start) {
				if (degrees - 180 < start) {
					whichDir = right;
				}
			} else if (start > degrees) {
				if (start - 180 >= degrees) {
					whichDir = right;
				}
			}
			degrees %= 360;
			Bot.getInputManager().pressKey(whichDir);
			int timeWaited = 0;
			try {
				while (getCameraAngle() > degrees + 5 || getCameraAngle() < degrees - 5) {
					Thread.sleep(10);
					timeWaited += 10;
					if (timeWaited > 500) {
						int time = timeWaited - 500;
						if (time == 0) {
							Bot.getInputManager().pressKey(whichDir);
						} else if (time % 40 == 0) {
							Bot.getInputManager().pressKey(whichDir);
						}
					}
				}
			} finally {
				Bot.getInputManager().releaseKey(whichDir);
			}
		}
	}

	private class Gui extends JFrame implements ActionListener, ListCellRenderer {
		private static final long serialVersionUID = -6133534749232966475L;

		private final JComboBox styleComboBox = new JComboBox(FishingStyle.values());
		private final JCheckBox cookCheckBox = new JCheckBox("Cook the fish"),
				fastPowerFishCheckBox = new JCheckBox("Fast powerfishing (fish 2, drop 2)"),
				drawPaintCheckBox = new JCheckBox("Draw paint"),
				drawTreesCheckBox = new JCheckBox("Draw rectangles over detected trees."),
				check2ndSkillsCheckBox = new JCheckBox("Check secondary skills (e.g fm & wc)");
		private final JSpinner mouseSpeedSpinner = new JSpinner();
		private final JButton stateButton = new JButton("Start"),
				applyButton = new JButton("Apply");

		public Gui() {
			super("ParaFishNCook");
			setResizable(false);
			setLocation(guiLocation);
		}

		private void initComponents() {
			styleComboBox.setActionCommand("checkcooking");
			styleComboBox.addActionListener(this);
			styleComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
			styleComboBox.setRenderer(this);
			styleComboBox.setSelectedIndex(curStyleIndex);

			if(!FishingStyle.values()[curStyleIndex].
					containsAnyCookableFish()) {
				cookCheckBox.setEnabled(false);
				cookCheckBox.setSelected(false);
			}

			cookCheckBox.setMnemonic(KeyEvent.VK_C);
			cookCheckBox.setSelected(cook);
			cookCheckBox.setActionCommand("checkfastpowerfish");
			cookCheckBox.addActionListener(this);
			fastPowerFishCheckBox.setMnemonic(KeyEvent.VK_W);
			fastPowerFishCheckBox.setSelected(fastPowerFish);
			fastPowerFishCheckBox.setEnabled(!cook);
			drawPaintCheckBox.setMnemonic(KeyEvent.VK_P);
			drawPaintCheckBox.setSelected(drawPaint);
			drawTreesCheckBox.setMnemonic(KeyEvent.VK_T);
			drawTreesCheckBox.setSelected(drawTrees);
			check2ndSkillsCheckBox.setMnemonic(KeyEvent.VK_H);
			check2ndSkillsCheckBox.setSelected(check2ndSkills);

			mouseSpeedSpinner.setModel(
					new SpinnerNumberModel(mouseSpeed, 0, 15, 1));
			mouseSpeedSpinner.setMaximumSize(new Dimension(50,
					mouseSpeedSpinner.getPreferredSize().height));
			mouseSpeedSpinner.setAlignmentX(Component.LEFT_ALIGNMENT);

			stateButton.addActionListener(this);
			stateButton.setActionCommand("changestate");
			stateButton.setMnemonic(KeyEvent.VK_S);
			getRootPane().setDefaultButton(stateButton);

			applyButton.addActionListener(this);
			applyButton.setActionCommand("apply");
			applyButton.setMnemonic(KeyEvent.VK_A);
			applyButton.setEnabled(false);
		}

		public void init() {
			initComponents();

			final JPanel pane = new JPanel(new BorderLayout(5, 5));

			final JPanel topPanel = new JPanel();
			topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.PAGE_AXIS));

			final JPanel generalPanel = new JPanel();
			generalPanel.setLayout(new BoxLayout(generalPanel, BoxLayout.PAGE_AXIS));
			generalPanel.setBorder(BorderFactory.createTitledBorder("General"));
			generalPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
			final JLabel styleLabel = new JLabel("Choose the style to use:");
			styleLabel.setDisplayedMnemonic(KeyEvent.VK_Y);
			styleLabel.setLabelFor(styleComboBox);
			generalPanel.add(styleLabel);
			generalPanel.add(styleComboBox);
			generalPanel.add(cookCheckBox);
			generalPanel.add(fastPowerFishCheckBox);

			final JPanel settingsPanel = new JPanel();
			settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.PAGE_AXIS));
			settingsPanel.setBorder(BorderFactory.createTitledBorder("Settings"));
			settingsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
			final JLabel mouseSpeedLabel = new JLabel("Mouse speed (lower the faster):");
			mouseSpeedLabel.setDisplayedMnemonic(KeyEvent.VK_M);
			mouseSpeedLabel.setLabelFor(mouseSpeedSpinner);
			settingsPanel.add(mouseSpeedLabel);
			settingsPanel.add(mouseSpeedSpinner);
			settingsPanel.add(drawPaintCheckBox);
			settingsPanel.add(drawTreesCheckBox);
			settingsPanel.add(check2ndSkillsCheckBox);

			topPanel.add(generalPanel);
			topPanel.add(Box.createRigidArea(new Dimension(0, 5)));
			topPanel.add(settingsPanel);

			final JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
			buttonPanel.add(new JLabel("Parameter (v" + VERSION + ")"));
			buttonPanel.add(Box.createHorizontalGlue());
			buttonPanel.add(stateButton);
			buttonPanel.add(applyButton);

			pane.add(topPanel, BorderLayout.CENTER);
			pane.add(buttonPanel, BorderLayout.PAGE_END);
			setContentPane(pane);

			pack();
			setVisible(true);
		}

		public void actionPerformed(final ActionEvent e) {
			final String command = e.getActionCommand();
			if(command.equals("apply")) {
				applySettings();
			} else if(command.equals("changestate")) {
				if(run) { // Stop script
					stateButton.setText("Start");
					applyButton.setEnabled(false);
					getRootPane().setDefaultButton(stateButton);
				} else { // Start script
					stateButton.setText("Stop");
					applyButton.setEnabled(true);
					getRootPane().setDefaultButton(applyButton);
					applySettings();

					// Reset paint variables
					startTime = System.currentTimeMillis();
					trips = fishCaught = fishingLvlsGained = logsChopped = wcLvlsGained =
						logsBurned = fmLvlsGained = fishCooked = cookingLvlsGained =
							gearFails = tinderBoxFails = 0;
					status = FISHING;

					hoverInvCount = -1;
				}
				run = !run;
			} else if(command.equals("checkcooking")) {
				if(((FishingStyle)styleComboBox.getSelectedItem()).
						containsAnyCookableFish()) {
					cookCheckBox.setEnabled(true);
				} else {
					cookCheckBox.setEnabled(false);
					cookCheckBox.setSelected(false);
				}
			} else if(command.equals("checkfastpowerfish")) {
				if(cookCheckBox.isSelected()) {
					fastPowerFishCheckBox.setEnabled(false);
					fastPowerFishCheckBox.setSelected(false);
				} else {
					fastPowerFishCheckBox.setEnabled(true);
				}
			}
		}

		private void applySettings() {
			curStyle = (FishingStyle)styleComboBox.getSelectedItem();
			curStyleIndex = styleComboBox.getSelectedIndex();
			cook = cookCheckBox.isSelected();
			fastPowerFish = !cook && fastPowerFishCheckBox.isSelected();
			drawPaint = drawPaintCheckBox.isSelected();
			drawTrees = drawTreesCheckBox.isSelected();
			check2ndSkills = check2ndSkillsCheckBox.isSelected();
			mouseSpeed = (Integer)mouseSpeedSpinner.getValue();
			log("Settings applied!");
			log("Fishing style: " + curStyle.getName() + ", [" + (cook ? "X" : " ") + "] Cook, " +
					"[" + (drawPaint ? "X" : " ") + "] Draw paint, ");
			log("[" + (fastPowerFish ? "X" : " ") + "] Fast Powerfish, " +
					"[" + (drawTrees ? "X" : " ") + "] Draw trees, " +
					"[" + (check2ndSkills ? "X" : " ") + "] Check secondary skills.");
			log("Mouse speed: " + mouseSpeed);
		}

		public Component getListCellRendererComponent(final JList list,
				final Object value, final int index,
				final boolean isSelected, final boolean hasFocus) {
			final FishingStyle style = (FishingStyle)value;
			final int fishingLvl = skills.getCurrentSkillLevel(STAT_FISHING);
			final JLabel label = new JLabel("<html>" +
					"<b>" + style.getName() + "</b><br/>" +
					"<small>Fishing lvl: " +
					"<font color='" + (fishingLvl >= style.getMinFishingLvl() ? "green" : "red") + "'>" +
					style.getMinFishingLvl() + "</font> " +
					(style.getMinFishingLvl() != style.getMaxFishingLvl() ?
							"- <font color='" + (fishingLvl >= style.getMaxFishingLvl() ? "green" : "red") + "'>" +
									style.getMaxFishingLvl() + "</font>" :
								"") +
					"</small></html>");

			label.setOpaque(index != -1);
			if(isSelected) {
				label.setBackground(list.getSelectionBackground());
				label.setForeground(list.getSelectionForeground());
			} else {
				label.setBackground(list.getBackground());
				label.setForeground(list.getForeground());
			}

			return label;
		}
	}

	protected int getMouseSpeed() {
		return mouseSpeed;
	}

	public boolean onStart(final Map<String,String> args) {
		loadSettings(settingsFile);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				gui.init();
			}
		});
		cameraAntiBan.start();
		return true;
	}

	public void onFinish() {
		cameraAntiBan.stop();
		gui.dispose();
		saveSettings(settingsFile);
	}

	private void loadSettings(final File file) {
		if(!file.exists()) {
			return;
		}
		DataInputStream in = null;
		try {
			in = new DataInputStream(new BufferedInputStream(
					new FileInputStream(file)));

			if(in.readInt() != SETTINGS_VERSION) {
				log("Incompatible settings file version.");
				log("Default settings will be used.");
				return;
			}

			defaultTab = in.readInt();
			curStyleIndex = in.readInt();
			mouseSpeed = in.readInt();
			cook = in.readBoolean();
			fastPowerFish = in.readBoolean();
			drawPaint = in.readBoolean();
			drawTrees = in.readBoolean();
			check2ndSkills = in.readBoolean();
			guiLocation = new Point(in.readInt(), in.readInt());
		} catch (final IOException e) {
			log("Can't read settings: " + e.getMessage());
		} finally {
			try {
				in.close();
			} catch (final IOException e) {
				log("Can't close settings stream: " + e);
			}
		}
	}

	private void saveSettings(final File file) {
		DataOutputStream out = null;
		try {
			out = new DataOutputStream(new BufferedOutputStream(
					new FileOutputStream(file)));

			out.writeInt(SETTINGS_VERSION);
			out.writeInt(defaultTab);
			out.writeInt(curStyleIndex);
			out.writeInt(mouseSpeed);
			out.writeBoolean(cook);
			out.writeBoolean(fastPowerFish);
			out.writeBoolean(drawPaint);
			out.writeBoolean(drawTrees);
			out.writeBoolean(check2ndSkills);
			final Point p = gui.getLocation();
			out.writeInt(p.x);
			out.writeInt(p.y);
		} catch (final IOException e) {
			log("Can't save settings: " + e.getMessage());
		} finally {
			try {
				out.close();
			} catch (final IOException e) {
				log("Can't close settings stream: " + e);
			}
		}
	}

	private boolean atPoint(final Point p, final String action) {
		moveMouse(p);
		wait(random(15, 75));
		final List<String> items = getMenuItems();
		if(items.get(0).contains(action)) {
			clickMouse(true);
			return true;
		} else {
			clickMouse(false);
			return atMenu(action);
		}
	}

	private Point getModelPoint(final RSObject obj) {
		final LDModel model = (LDModel)obj.getModel();
		final int[] xPoints = model.getXPoints(),
		yPoints = model.getYPoints(),
		zPoints = model.getZPoints();

		final int i = random(0, model.getIndices3().length);
		final int i1 = model.getIndices1()[i],
		i2 = model.getIndices2()[i],
		i3 = model.getIndices3()[i];

		final RSAnimable animable = (RSAnimable)obj.getObject();
		final int ax = animable.getX(), ay = animable.getY();

		final Point[] indicePoints = new Point[3];
		indicePoints[0] = Calculations.w2s(xPoints[i1] + ax,
				yPoints[i1] + Calculations.tileHeight(ax, ay),
				zPoints[i1] + ay);
		indicePoints[1] = Calculations.w2s(xPoints[i2] + ax,
				yPoints[i2] + Calculations.tileHeight(ax, ay),
				zPoints[i2] + ay);
		indicePoints[2] = Calculations.w2s(xPoints[i3] + ax,
				yPoints[i3] + Calculations.tileHeight(ax, ay),
				zPoints[i3] + ay);

		final int xPoint = blend(min(indicePoints[0].x, indicePoints[1].x, indicePoints[2].x),
				max(indicePoints[0].x, indicePoints[1].x, indicePoints[2].x), random(0.0, 1.0));
		final int[][] xIndexes = new int[2][2];
		for(int xIndex = 0, xIndexCount = 0; xIndex < 3 && xIndexCount < 2; xIndex++) {
			final int x1 = indicePoints[xIndex].x;
			final int x2 = indicePoints[xIndex == 2 ? 0 : xIndex + 1].x;
			if(Math.min(x1, x2) <= Math.max(x1, x2)) {
				xIndexes[xIndexCount++] = new int[] {xIndex, xIndex == 2 ? 0 : xIndex + 1};
			}
		}
		final int d1 = Math.min(indicePoints[xIndexes[0][0]].x, indicePoints[xIndexes[0][1]].x) +
				Math.abs(indicePoints[xIndexes[0][0]].x - indicePoints[xIndexes[0][1]].x);
		final int d2 = Math.min(indicePoints[xIndexes[1][0]].x, indicePoints[xIndexes[1][1]].x) +
				Math.abs(indicePoints[xIndexes[1][0]].x - indicePoints[xIndexes[1][1]].x);
		final double xRatio1 = d1 == 0 ? 0.0 : xPoint / d1;
		final double xRatio2 = d2 == 0 ? 0.0 : xPoint / d2;
		final int yLimit1 = (int)(Math.abs(indicePoints[xIndexes[0][0]].y - indicePoints[xIndexes[0][1]].y) * xRatio1);
		final int yLimit2 = (int)(Math.abs(indicePoints[xIndexes[1][0]].y - indicePoints[xIndexes[1][1]].y) * xRatio2);

		final int yPoint = min(indicePoints[0].y, indicePoints[1].y,
				indicePoints[2].y) + random(yLimit1, yLimit2);

		return new Point(xPoint, yPoint);
	}

	private int blend(final int a, final int b, final double factor) {
		return (int)(Math.min(a, b) + Math.abs(a - b) * factor);
	}

	private int min(final int... values) {
		int min = values[0];
		for(final int value : values) {
			if(value < min) {
				min = value;
			}
		}

		return min;
	}

	private int max(final int... values) {
		int max = values[0];
		for(final int value : values) {
			if(value > max) {
				max = value;
			}
		}

		return max;
	}

	private void doAntiBan() {
		if(System.currentTimeMillis() >= nextMouseTime) {
			moveMouseRandomly(200);
			nextMouseTime = System.currentTimeMillis() + random(500, 7500);
		}
		if(System.currentTimeMillis() >= nextExamineTime) {
			cameraAntiBan.setMode(CameraAntiBan.OFF);
			switch(random(0, 2)) {
			case 0:    // Examine/hover objects
				//log("Hovering/Examining objects.");
				final RSObject obj = pickRandomObject(10);
				if(obj != null) {
					if(random(0, 3) == 0) {
						atObject(obj, "Examine");
					} else {
						moveMouse(Calculations.tileToScreen(obj.getLocation()));
					}
				}
				break;
			case 1:    // Hover NPCs/Players
				//log("Hovering NPCs/Players.");
				final RSCharacter character = pickRandomCharacter();
				if(character != null) {
					moveMouse(character.getScreenLocation());
				}
				break;
			}
			nextExamineTime = System.currentTimeMillis() + random(60000, 300000);
			cameraAntiBan.setMode(CameraAntiBan.RANDOMROTATING);
		}
		if(System.currentTimeMillis() >= nextStatTime) {
			//log("Hovering stat(s).");
			final List<Integer> ifaceIDs = new LinkedList<Integer>();
			for(final int s : primaryStatIfaceIDs) {
				if(!cook || random(0, 2) == 0) {
					ifaceIDs.add(s);
				}
				if(!cook) break;
			}
			if(check2ndSkills) {
				for(final int s : secondaryStatIfaceIDs) {
					if(random(0, 3) == 0) {
						ifaceIDs.add(s);
					}
				}
			}
			Collections.shuffle(ifaceIDs);
			while(!ifaceIDs.isEmpty()) {
				hoverSkill(ifaceIDs.remove(0));
				wait(random(500, 2500));
			}
			nextStatTime = System.currentTimeMillis() + random(30000, 300000);
		}
		if(viewObjective && System.currentTimeMillis() >= nextObjectiveTime) {
			//log("Hovering objective.");
			if(getCurrentTab() != 8) {
				openTab(8);
				wait(random(250, 750));
			}
			if(objectivePicIface.getBackgroundColor() != UNSET_OBJECTIVE_TEXTURE) {
				hoverIface(objectiveBarIface);
				wait(random(500, 2500));
			}
			if(!arrayContains(objectivePicIface.getBackgroundColor(),
					objectiveTexIDs)) {
				viewObjective = false;
			}
			nextObjectiveTime = System.currentTimeMillis() + random(120000, 600000);
		}
		if(System.currentTimeMillis() >= nextPrayerTime) {
			//log("Hovering prayer(s).");
			if(getCurrentTab() != TAB_PRAYER){
				openTab(TAB_PRAYER);
				wait(random(250, 750));
			}
			final List<RSInterfaceComponent> prayers = new LinkedList<RSInterfaceComponent>();
			final RSInterfaceComponent[] prayerComponents = prayerIface.getComponents();
			final int prayLvl = skills.getRealSkillLevel(STAT_PRAYER);
			final int count = random(1, 6);
			for(int i = 0; i < Math.min(prayerComponents.length, prayerLevels.length) &&
					prayers.size() < count; i++) {
				if(random(0, prayLvl <= prayerLevels[i] ? 7 : 15) == 0) {
					prayers.add(prayerComponents[i]);
				}
			}
			Collections.shuffle(prayers);
			while(!prayers.isEmpty()) {
				hoverIface(prayers.remove(0));
				wait(random(500, 2500));
			}
			nextPrayerTime = System.currentTimeMillis() + random(120000, 3600000);
		}
		if(System.currentTimeMillis() >= nextMagicTime) {
			//log("Hovering spell(s).");
			if(getCurrentTab() != TAB_MAGIC) {
				openTab(TAB_MAGIC);
				wait(random(250, 750));
			}
			final List<Integer> spells = new LinkedList<Integer>();
			final int count = random(1, 6);
			for(int i = 24; i < 92 && spells.size() < count; i++) {
				if(random(0, 10) == 0 &&
						magicIface.getChild(1).getArea().
						contains(magicIface.getChild(i).getArea())) {
					spells.add(i);
				}
			}
			Collections.shuffle(spells);
			while(!spells.isEmpty()) {
				hoverIface(magicIface.getChild(spells.remove(0)));
				wait(random(500, 2500));
			}
			nextMagicTime = System.currentTimeMillis() + random(120000, 3600000);
		}
	}

	private void hoverSkill(final int skillID) {
		if(getCurrentTab() != TAB_STATS) {
			openTab(TAB_STATS);
			wait(random(250, 750));
		}
		hoverIface(getInterface(320, skillID));
	}

	private void hoverIface(final RSInterfaceChild iface) {
		moveMouse(iface.getAbsoluteX() + random(0, iface.getWidth()),
				iface.getAbsoluteY() + random(0, iface.getHeight()));
	}

	private RSObject pickRandomObject(final int range) {
		final List<RSObject> objects = new ArrayList<RSObject>();

		final RSTile l = getLocation();

		for(int x = l.getX() - range; x <= l.getX() + range; x++) {
			for(int y = l.getY() - range; y <= l.getY() + range; y++) {
				final RSObject curObj = getObjectAt(x, y);
				if(curObj != null && curObj.getType() == 0 &&
						tileOnScreen(curObj.getLocation())) {
					objects.add(curObj);
				}
			}
		}

		return objects.isEmpty() ? null : objects.get(random(0, objects.size()));
	}

	private RSCharacter pickRandomCharacter() {
		final List<RSCharacter> characters = new ArrayList<RSCharacter>();

		final int[] validNpcs = Bot.getClient().getRSNPCIndexArray();
		final NodeCache npcNodeCache = Bot.getClient().getRSNPCNC();

		for(final int i : validNpcs) {
			final Node n = Calculations.findNodeByID(npcNodeCache, i);
			if(n == null || !(n instanceof RSNPCNode)) {
				continue;
			}
			final RSNPC rsNpc = new RSNPC(((RSNPCNode)n).getRSNPC());
			if(pointOnScreen(rsNpc.getScreenLocation())) {
				characters.add(rsNpc);
			}
		}

		final org.rsbot.accessors.RSPlayer[] players = Bot.getClient().getRSPlayerArray();
		final int[] validPlayers = Bot.getClient().getRSPlayerIndexArray();

		for(final int i : validPlayers) {
			if(players[i] == null) {
				continue;
			}
			final RSPlayer rsPlayer = new RSPlayer(players[i]);
			if(!getMyPlayer().equals(rsPlayer) &&
					pointOnScreen(rsPlayer.getScreenLocation())) {
				characters.add(rsPlayer);
			}
		}

		return characters.isEmpty() ? null : characters.get(random(0, characters.size()));
	}

	private boolean inventoryContainsOneOf(final Fish... fish) {
		for(final Fish f : fish) {
			if(inventoryContainsOneOf(f.getID(), f.getCookedID(), f.getBurntID())) {
				return true;
			}
		}
		return false;
	}

	private int getInventoryCount(final Fish[] fish,
			final boolean raw, final boolean cooked, final boolean burnt) {
		final int[] invArray = getInventoryArray();
		int count = 0;
		for(final int id : invArray) {
			for(final Fish f : fish) {
				if((raw && f.getID() == id) ||
						(cooked && f.getCookedID() == id) ||
						(burnt && f.getBurntID() == id)) {
					count++;
				}
			}
		}
		return count;
	}

	private int getInventoryCount(final Fish[] fish) {
		return getInventoryCount(fish, true, true, true);
	}

	private int getFirstVerticalIndexFor(final int... ids) {
		final int[] invArray = getInventoryArray();
		for(int x = 0; x < 4; x++) {
			for(int y = 0; y < 7; y++) {
				if(arrayContains(invArray[y * 4 + x], ids)) {
					return y * 4 + x;
				}
			}
		}
		return -1;
	}

	private Point getInvPointAt(final int index) {
		final Point p = getInventoryItemPoint(index);
		if(p.x == -1 || p.y == -1 || index == -1) return new Point(-1, -1);
		return new Point(p.x + random(12, 20), p.y + random(10, 18));
	}

	private void clickInvItemAt(final int index, final boolean leftClick) {
		if(index == -1) {
			return;
		}
		clickMouse(getInvPointAt(index), leftClick);
	}

	private int getSelectedInvIndex() {
		final RSInterfaceChild[] items = getInventoryInterface().getComponents();
		for(int i = 0; i < Math.min(28, items.length); i++) {
			if(items[i].getBorderThickness() == 2) {
				return i;
			}
		}
		return -1;
	}

	private int getSelectedInvItem() {
		final int index = getSelectedInvIndex();
		return index == -1 ? -1 : getInventoryArray()[index];
	}

	private boolean clickSelectedInvItem() {
		final int index = getSelectedInvIndex();
		if(index == -1) {
			return false;
		}
		clickInvItemAt(index, true);
		return true;
	}

	private void dropFish(final Fish... fish) {
		final int[] invArray = getInventoryArray();
		for(int x = 0; x < 4; x++) {
		yLoop:
			for(int y = 0; run && y < 7; y++) {
				final int index = y * 4 + x;
				final int id = invArray[index];
				if(id == -1) {
					continue;
				}
				if(clickSelectedInvItem()) {
					wait(random(100, 500));
				}
				for(final Fish f : fish) {
					if(id == f.getID() || id == f.getCookedID() || id == f.getBurntID()) {
						List<String> mItems = getMenuItems();
						int mIndex = getIndexFor(mItems, "Drop",
								id == f.getBurntID() ? f.getBurntName() : f.toString());
						if(!isMenuOpen() || mIndex == -1) {
							clickInvItemAt(index, false);
							mItems = getMenuItems();
							mIndex = getIndexFor(getMenuItems(), "Drop",
									id == f.getBurntID() ? f.getBurntName() : f.toString());
						}
						if(mIndex == -1) {
							continue yLoop;
						}
						atMenu(mItems.get(mIndex));
						continue yLoop;
					}
				}
			}
		}
	}

	private int getIndexFor(final List<String> list, String start, String end) {
		start = start.toLowerCase();
		end = end.toLowerCase();
		for(final ListIterator<String> it = list.listIterator(); it.hasNext(); ) {
			final String str = it.next().toLowerCase();
			if(str.startsWith(start) && str.endsWith(end)) {
				return it.previousIndex();
			}
		}
		return -1;
	}

	private static boolean arrayContains(final int value, final int... values) {
		for(final int v : values) {
			if(v == value) {
				return true;
			}
		}
		return false;
	}

	private boolean isFiremakeable(final int x, final int y) {
		final RSObject obj = getObjectAt(x, y);
		return obj == null || obj.getType() == 1;
	}

	private boolean isFiremakeable(final RSTile tile) {
		return isFiremakeable(tile.getX(), tile.getY());
	}

	private Set<RSTile> getFiremakeableTiles(final int maxDist) {
		final Set<RSTile> tiles = new HashSet<RSTile>();

		try {
			final int startX = getLocation().getX(),
			startY = getLocation().getY();
			for(int y = startY - maxDist; y < startY + maxDist + 1; y++) {
				for(int x = startX - maxDist; x < startX + maxDist + 1; x++) {
					if(isFiremakeable(x, y)) {
						tiles.add(new RSTile(x, y));
					}
				}
			}
		} catch (final Exception ignored) {
		}

		return tiles;
	}

	private RSTile getNearestTile(final Set<RSTile> tiles, final int maxDist) {
		final List<RSTile> nearestTiles = new ArrayList<RSTile>();
		int lastDist = maxDist;

		for(final RSTile tile : tiles) {
			int curDist = distanceTo(tile);
			if(curDist < lastDist) {
				nearestTiles.clear();
				lastDist = curDist;
			}
			if(curDist == lastDist) {
				nearestTiles.add(tile);
			}
		}

		return nearestTiles.isEmpty() ? null :
			nearestTiles.get(random(0, nearestTiles.size()));
	}

	private Set<RSNPC> getNPCsAt(final RSTile tile) {
		final Set<RSNPC> npcSet = new HashSet<RSNPC>();
		final int[] validNPCs = Bot.getClient().getRSNPCIndexArray();
		final NodeCache npcNodeCache = Bot.getClient().getRSNPCNC();

		for(final int i : validNPCs) {
			final Node n = Calculations.findNodeByID(npcNodeCache, i);
			if(n == null || !(n instanceof RSNPCNode)) {
				continue;
			}
			final RSNPC npc = new RSNPC(((RSNPCNode)n).getRSNPC());
			if(npc.getLocation().equals(tile)) {
				npcSet.add(npc);
			}
		}

		return npcSet;
	}

	private boolean hasToMove(final RSTile tile) {
		final RSTile l = getLocation();
		return distanceTo(tile) > 1 ||
			(tile.getX() != l.getX() && tile.getY() != l.getY());
	}

	private boolean walk2(final RSTile t, final int x, final int y) {
		final Point p = tileToMinimap(t);
		if (p.x == -1 || p.y == -1) {
			final RSTile[] temp = cleanPath(generateFixedPath(t));
			for (int i = 0; i < 10; i++) {
				if (distanceTo(temp[temp.length - 1]) < 6)
					return true;
				cameraAntiBan.setMode(CameraAntiBan.OFF);
				final RSTile next = nextTile(temp, 16);
				if (next != null) {
					if (walkTileMM(next, x, y))
						return true;
					else {
						final RSTile l = getLocation();
						if(walkTileMM(new RSTile((l.getX() + next.getX()) / 2,
								(l.getY() + next.getY()) / 2), x, y))
							return true;
					}
				} else {
					final RSTile n = nextTile(temp, 20);
					if(!walkTileMM(n)) {
						final RSTile l = getLocation();
						walkTileMM(new RSTile((l.getX() + n.getX()) / 2,
								(l.getY() + n.getY()) / 2));
					}
				}
				cameraAntiBan.setMode(CameraAntiBan.WALKROTATING);
				wait(random(200, 400));
			}
			return false;
		}
		clickMouse(p, x, y, true);
		return true;
	}

	private boolean isCooking(final Fish fish, final RSTile tile, final int timeout) {
		final long startTime = System.currentTimeMillis();
		int fireFails = 0;
		while(System.currentTimeMillis() - startTime < timeout &&
				inventoryContains(fish.getID()) && fireFails < 3) {
			if(getMyPlayer().getAnimation() == COOKING_ANIMATION ||
					levelUpIface.isValid()) {
				return true;
			}
			final RSObject fire = getObjectAt(tile);
			if(fire == null || fire.getID() != FIRE_ID) {
				fireFails++;
			} else {
				fireFails = 0;
			}
			wait(random(100, 300));
		}
		return false;
	}

	private void waitForStanding() {
		long nextTime = System.currentTimeMillis() + random(0, 7500);
		RSTile d;
		while((d = getDestination()) == null || distanceTo(d) < 5 ?
				getMyPlayer().isMoving() : waitToMove(random(750, 1500))) {
			wait(random(100, 750));
			if(!isRunning() && getEnergy() >= minimumRunPercent) {
				setRun(true);
				if(waitForRunning(random(750, 1500))) {
					minimumRunPercent = random(15, 61);
				}
			}
			if(System.currentTimeMillis() >= nextTime) {
				moveMouseRandomly(200);
				nextTime = System.currentTimeMillis() + random(500, 7500);
			}
		}
	}

	private boolean waitForRunning(final int timeout) {
		final long startTime = System.currentTimeMillis();
		while(System.currentTimeMillis() - startTime < timeout) {
			if(isRunning()) {
				return true;
			}
			wait(random(100, 750));
		}
		return false;
	}

	private boolean waitForChopped(final Tree tree) {
		final Log log = tree.getType().getLog();
		while(getMyPlayer().getAnimation() != -1
				&& tree.isAvailable()) {
			if(inventoryContains(log.getID())) {
				return true;
			}
			wait(random(100, 300));
		}
		return false;
	}

	private boolean waitForLighted(final RSTile tile, final int timeout) {
		long startTime = System.currentTimeMillis();
		while(System.currentTimeMillis() - startTime < timeout) {
			RSObject obj;
			if(!getLocation().equals(tile) &&
					(obj = getObjectAt(tile)) != null &&
					obj.getID() == FIRE_ID) {
				return true;
			}
			if(getMyPlayer().getAnimation() == FIRING_ANIMATION) {
				startTime = System.currentTimeMillis();
			}
			wait(random(200, 750));
		}
		return false;
	}

	private boolean waitForFish(final Fish[] fish, final boolean appear, final int timeout) {
		final long startTime = System.currentTimeMillis();
		while(System.currentTimeMillis() - startTime < timeout) {
			if(inventoryContainsOneOf(fish) == appear) {
				return true;
			}
			wait(random(150, 300));
		}
		return false;
	}

	public boolean checkLevels() {
		boolean hasLevelsRequired = true;
		for(final LevelInfo li :
				curStyle.getLevelInformation()) {
			if(!li.check()) {
				if(hasLevelsRequired) {
					log("To use " + curStyle.getName() + ", you must meet these level requirements:");
					hasLevelsRequired = false;
				}
				log("At least lvl " + li.getLevelRequired() + " in " +
						Skills.statsArray[li.getStatID()] + ".");
			}
		}
		return hasLevelsRequired;
	}

	public int loop() {
		if(!run) {
			return random(750, 1500);
		}
		if(!isLoggedIn()) {
			update = true;
			return random(500, 1500);
		}
		if(cook) {
			if(update) {
				trees = Tree.scanForTrees();
				if(trees.isEmpty()) {
					log("No trees found around!");
					logout();
					return -1;
				}
				fishingSpotTile = getLocation();
				update = false;
			}
			if(!hasHatchet) {
				log("You do not have a (usable) hatchet.");
				logout();
				return -1;
			}
			if(inventoryContains(TINDERBOX_ID)) {
				if(tinderBoxFails != 0) {
					tinderBoxFails = 0;
					log("Tinderbox found.");
				}
			} else {
				if(tinderBoxFails >= 5) {
					log("You do not have a tinderbox.");
					logout();
					return -1;
				}
				log("Checking for tinderbox. (" + ++tinderBoxFails + "/5)");
				return random(750, 1250);
			}
			final int cookingLvl = skills.getCurrentSkillLevel(STAT_COOKING);
			boolean canCook = false;
			for(final Fish f : curStyle.getCatches()) {
				if(f.getCookingLvl() <= cookingLvl) {
					canCook = true;
					break;
				}
			}
			if(!canCook) {
				log("You can't cook any of the catches of the chosen fishing style.");
				logout();
				return -1;
			}
		}
		if(hoverInvCount == -1) {
			final int otherItemsCount = getInventoryCount() -
				getInventoryCount(curStyle.getCatches());
			hoverInvCount = fastPowerFish ? random(1, 3) :
				random(25 - otherItemsCount, 28 - otherItemsCount);
		}
		final RSObject fire = cook ? getNearestObjectByID(FIRE_ID) : null;
		switch(status) {
		case FISHING:
			if(fastPowerFish && getInventoryCount(curStyle.getCatches()) >= 2 ||
					getInventoryCount() >=
					(cook && fire != null && tileOnScreen(fire.getLocation()) ? 28 : 27)) {
				status = cook ? CHOPPING : DROPPING;
				break;
			}
			if(!checkLevels()) {
				logout();
				return -1;
			}
			if(inventoryContains(curStyle.getGearIDs())) {
				if(gearFails != 0) {
					gearFails = 0;
					log("Fishing gear found.");
				}
			} else {
				if(gearFails >= 5) {
					log("You don't have all the fishing gear/supplies needed.");
					logout();
					return -1;
				}
				log("Checking for gear. (" + ++gearFails + "/5)");
				return random(750, 1250);
			}
			if(!animationIs(curStyle.getAnimations()) ||
					(fishingSpot != null && !getNPCsAt(fishingSpotTile).contains(fishingSpot))) {
				fishingSpot = getNearestNPCByID(curStyle.getSpotIDs());
				if(fishingSpot != null) {
					fishingSpotTile = fishingSpot.getLocation();
				} else {
					log("Fishing spot not found.");
				}
				if(!tileOnScreen(fishingSpotTile)) {
					if(distanceTo(fishingSpotTile) < random(5, 10)) {
						turnToTile(fishingSpotTile);
					}
					if(!tileOnScreen(fishingSpotTile)) {
						if(!walk2(fishingSpotTile, 1, 1) || !waitToMove(random(1500, 3000))) {
							break;
						}
						cameraAntiBan.setMode(CameraAntiBan.WALKROTATING);
						waitForStanding();
					}
					break;
				}
				if(clickSelectedInvItem()) {
					wait(random(100, 750));
				}
				cameraAntiBan.setMode(CameraAntiBan.OFF);
				if(fishingSpot == null ||
						!atTile(fishingSpotTile, 0, random(0.2, 0.7), random(0.2, 0.7),
						curStyle + " Fishing spot")) {
					break;
				}
				if(hasToMove(fishingSpotTile) && waitToMove(random(1500, 3000))) {
					cameraAntiBan.setMode(CameraAntiBan.WALKROTATING);
					waitForStanding();
				}
				waitForAnim(random(1750, 3500));
			} else {
				cameraAntiBan.setMode(CameraAntiBan.RANDOMROTATING);
				if(levelUpIface.isValid()) {
					wait(random(250, 1250));
					atInterface(levelUpIface);
				}
				if(!cook && getInventoryCount(curStyle.getCatches()) >= hoverInvCount) {
					final Point invPoint = getInvPointAt(getFirstVerticalIndexFor(curStyle.getCatchIDs()));
					if(invPoint.distance(getMouseLocation()) >= random(40, 81)) {
						moveMouse(invPoint);
					}
					return random(50, 300);
				} else {
					doAntiBan();
				}
				return random(250, 1000);
			}
			break;
		case CHOPPING:
			if(fire != null && tileOnScreen(fire.getLocation())) {
				status = COOKING;
				unavailableTrees.clear();
				break;
			}
			if(Log.getUsableInvLog() != null) {
				status = FIRING;
				unavailableTrees.clear();
				break;
			}
			final Tree tree = Tree.getNearestUsableTree(trees, unavailableTrees);
			if(tree == null) {
				if(!unavailableTrees.isEmpty() &&
						Tree.getNearestUsableTree(trees, Collections.<Tree>emptySet()) != null) {
					unavailableTrees.clear();
					break;
				} else {
					log("Usable tree was not found!");
					logout();
					return -1;
				}
			}
			if(clickSelectedInvItem()) {
				wait(random(100, 750));
			}
			if(isInventoryFull()) {
				final int junkID = Fish.getJunkInvFishID();
				if(junkID == -1) {
					log("No fish found to drop! Stopping.");
					logout();
					return -1;
				}
				if(!atInventoryItem(junkID, "Drop")) {
					break;
				}
				wait(random(500, 1000));
			}
			final RSTile loc = tree.getLocation();
			final RSObject obj = getObjectAt(loc.getX() + random(0, tree.getType().getEdgeSize()),
					loc.getY() + random(0, tree.getType().getEdgeSize()));
			if(obj == null) {
				break;
			}
			final Point p = getModelPoint(obj);
			cameraAntiBan.setMode(CameraAntiBan.OFF);
			if(!pointOnScreen(p)) {
				if(distanceTo(loc) <= random(5, 10)) {
					turnToTile(loc);
				}
				if(!pointOnScreen(p)) {
					if(!walk2(loc, 2, 2) || !waitToMove(random(1500, 3000))) {
						break;
					}
					cameraAntiBan.setMode(CameraAntiBan.RANDOMROTATING);
					waitForStanding();
				}
				break;
			}
			if(!tree.isAvailable()) {
				unavailableTrees.add(tree);
				break;
			}
			if(!atPoint(p, "Chop down " + tree.getType())) {
				break;
			}
			if(hasToMove(loc) && waitToMove(random(1500, 3000))) {
				cameraAntiBan.setMode(CameraAntiBan.WALKROTATING);
				waitForStanding();
			}
			if(waitForAnim(random(1750, 3500)) == -1) {
				break;
			}
			cameraAntiBan.setMode(CameraAntiBan.RANDOMROTATING);
			waitForChopped(tree);
			break;
		case FIRING:
			cameraAntiBan.setMode(CameraAntiBan.RANDOMROTATING);
			if(fire != null && tileOnScreen(fire.getLocation())) {
				status = COOKING;
				break;
			}
			final Log usableLog = Log.getUsableInvLog();
			if(usableLog == null) {
				status = CHOPPING;
				break;
			}
			if(!isFiremakeable(getLocation())) {
				final RSTile firemakeableTile = getNearestTile(getFiremakeableTiles(5), 5);
				if(firemakeableTile == null) {
					log("Can't find any nearby firemakeable tiles.");
					logout();
					return -1;
				}
				if(clickSelectedInvItem()) {
					wait(random(100, 750));
				}
				if(!atTile(firemakeableTile, "Walk here") || !waitToMove(random(1500, 3000))) {
					break;
				}
				waitForStanding();
				break;
			}
			final boolean reversed = random(0, 2) == 0;
			final int id1 = reversed ? usableLog.getID() : TINDERBOX_ID;
			final int id2 = reversed ? TINDERBOX_ID : usableLog.getID();
			final String name1 = reversed ? usableLog.toString() : "Tinderbox";
			final String name2 = reversed ? "Tinderbox" : usableLog.toString();
			if(getSelectedInvItem() != id1) {
				if(clickSelectedInvItem()) {
					wait(random(100, 750));
				}
				if(!atInventoryItem(id1, "Use " + name1)) {
					break;
				}
				wait(random(100, 750));
			}
			if(getSelectedInvItem() != id1 ||
					!atInventoryItem(id2, "Use " + name1 + " -> " + name2)) {
				break;
			}
			if(waitForLighted(getLocation(), random(1500, 3000))) {
				logsBurned++;
				fmXPGained += usableLog.getFmXPGain();
			}
			break;
		case COOKING:
			final Fish cookableFish = Fish.getCookableInvFish();
			if(cookableFish == null) {
				status = DROPPING;
				break;
			}
			if(fire == null || !tileOnScreen(fire.getLocation())) {
				status = FIRING;
				break;
			}
			if(!cookingIface.isValid() ||
					cookingIface.getArea().equals(failCookingIfaceArea)) {
				cameraAntiBan.setMode(CameraAntiBan.OFF);
				if(getSelectedInvItem() != cookableFish.getID()) {
					if(clickSelectedInvItem()) {
						wait(random(100, 750));
					}
					if(!atInventoryItem(cookableFish.getID(), "Use Raw " + cookableFish)) {
						break;
					}
					wait(random(100, 750));
				}
				if(getSelectedInvItem() != cookableFish.getID() ||
						!atObject(fire, " -> Fire")) {
					break;
				}
				if(cookingIfaceArea != null) {
					wait(random(100, 750));
					moveMouse(cookingIfaceArea.x + random(0, cookingIfaceArea.width),
							cookingIfaceArea.y + random(0, cookingIfaceArea.height));
				}
				if(hasToMove(fire.getLocation()) && waitToMove(random(1500, 3000))) {
					cameraAntiBan.setMode(CameraAntiBan.WALKROTATING);
					waitForStanding();
				}
				if(getInventoryCount(cookableFish.getID()) > 1 &&
						!waitForIface(cookingIface.getParInterface(), random(1500, 3000))) {
					break;
				}
			}
			if(cookingIface.isValid() &&
					!cookingIface.getArea().equals(failCookingIfaceArea)) {
				cookingIfaceArea = cookingIface.getArea();
				atInterface(cookingIface, "Cook All");
				if(waitForAnim(random(1500, 3000)) != COOKING_ANIMATION) {
					break;
				}
			}
			cameraAntiBan.setMode(CameraAntiBan.RANDOMROTATING);
			while(run && isCooking(cookableFish, fire.getLocation(), random(1500, 3000))) {
				if(levelUpIface.isValid()) {
					wait(random(250, 1250));
					atInterface(levelUpIface);
				}
				if(getInventoryCount(curStyle.getCatches(), false, true, true) >= hoverInvCount) {
					final Point invPoint = getInvPointAt(getFirstVerticalIndexFor(curStyle.getCatchIDs()));
					if(invPoint.distance(getMouseLocation()) >= random(40, 81)) {
						moveMouse(invPoint);
					}
					wait(random(50, 300));
				} else {
					doAntiBan();
					wait(random(150, 750));
				}
			}
			break;
		case DROPPING:
			cameraAntiBan.setMode(CameraAntiBan.OFF);
			if(!inventoryContainsOneOf(Fish.values())) {
				status = FISHING;
				trips++;
				hoverInvCount = -1;
				break;
			}
			dropFish(Fish.values());
			fishingSpot = getNearestNPCByID(curStyle.getSpotIDs());
			if(fishingSpot != null && tileOnScreen(fishingSpot.getLocation())) {
				moveMouse(Calculations.tileToScreen(fishingSpot.getLocation()), 5, 5);
			}
			waitForFish(Fish.values(), false, random(1500, 3000));
			break;
		}
		return random(100, 400);
	}

	private String getStatus() {
		switch(status) {
		case FISHING: return "Fishing";
		case CHOPPING: return "Chopping";
		case FIRING: return "Firing";
		case COOKING: return "Cooking";
		case DROPPING: return "Dropping";
		default: return "";
		}
	}

	private int getWidth(final String[] lines) {
		int width = 0;
		for(final String line : lines) {
			final int curWidth = fontMetrics.stringWidth(line);
			if(curWidth > width) {
				width = curWidth;
			}
		}
		return width;
	}


	private String formatTime(final long time) {
		final int sec = (int)(time / 1000),
		h = sec / 3600, m = sec / 60 % 60, s = sec % 60;
		return (h < 10 ? "0" + h : h) + ":" + (m < 10 ? "0" + m : m) + ":" + (s < 10 ? "0" + s : s);
	}

	private String formatTime(final double time) {
		return formatTime((long)time);
	}

	private String formatAmount(final int amount) {
		if(amount < 1000) return String.valueOf(amount);
		final int len = String.valueOf(amount).length();
		if(len >= 6) {
			return ((int)(amount / (len == 6 ? 1000 : 1000000))) + (len == 6 ? "k" : "m");
		}
		return String.format(Locale.US, "%." + (3 - len % 3) + "f%c",
				amount / (len < 7 ? 1000.0 : 1000000.0),
				len < 7 ? 'k' : 'm');
	}

	private String formatAmount(final double amount) {
		return formatAmount((int)amount);
	}

	private void highlightTile(final Graphics g, final int x, final int y) {
		final Point p1 = Calculations.tileToScreen(x, y, 0.0, 0.0, 0),
		p2 = Calculations.tileToScreen(x, y, 1.0, 0.0, 0),
		p3 = Calculations.tileToScreen(x, y, 1.0, 1.0, 0),
		p4 = Calculations.tileToScreen(x, y, 0.0, 1.0, 0);
		if(p1.x != -1 && p1.y != -1 && p2.x != -1 && p2.y != -1 &&
				p3.x != -1 && p3.y != -1 && p4.x != -1 && p4.y != -1) {
			g.setColor(new Color(0, 0, 0, 75));
			g.fillPolygon(new int[] {p1.x, p2.x, p3.x, p4.x},
					new int[] {p1.y, p2.y, p3.y, p4.y}, 4);
			g.setColor(Color.YELLOW);
			g.drawPolygon(new int[] {p1.x, p2.x, p3.x, p4.x},
					new int[] {p1.y, p2.y, p3.y, p4.y}, 4);
		}
	}

	public void onRepaint(final Graphics g) {
		if(!run) return;

		if(drawPaint || drawTrees) {
			/* Thanks to Gnarly for this! :) */
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}

		if(drawTrees) {
			for(final Tree tree : trees) {
				final int lx = tree.getLocation().getX(),
				ly = tree.getLocation().getY(),
				edgeSize = tree.getType().getEdgeSize();
				for(int x = lx; x < lx + edgeSize; x++) {
					for(int y = ly; y < ly + edgeSize; y++) {
						highlightTile(g, x, y);
					}
				}
			}
		}

		if(drawPaint) {
			if(fontMetrics == null) {
				fontMetrics = g.getFontMetrics();
			}

			g.setColor(new Color(0, 0, 0, 175));
			g.fillRect(494, 234, 24, 105);
			g.setColor(Color.LIGHT_GRAY);
			g.drawRect(494, 234, 24, 105);

			final Color[] tabColors = {
					Color.WHITE, Color.BLUE, Color.GREEN, Color.RED, Color.ORANGE
			};

			final Mouse m = Bot.getClient().getMouse();

			final int selTab = 495 <= m.x && m.x <= 517 &&
			235 <= m.y && m.y <= 340 ? (m.y - 235) / 21 : defaultTab;
			int curTab = 0;
			int curY = 235;
			for(final Color color : tabColors) {
				final boolean isHovered = 495 <= m.x && m.x <= 517 &&
				curY <= m.y && m.y < curY + 21;
				if(isHovered) {
					if(m.pressed) {
						mouseClicked = true;
					} else if (mouseClicked) {
						defaultTab = defaultTab == selTab ? -1 : curTab;
						mouseClicked = false;
					}
				}

				g.setColor(curTab == defaultTab ?
						new Color(25, 25, isHovered ? 75 : 25) :
							new Color(75, 75, isHovered ? 125 : 75));
				g.drawRect(495, curY, 22, 20);
				g.setColor(color);
				g.fillOval(498, curY + 2, 16, 16);
				g.setColor(curTab == defaultTab ?
						new Color(175, 175, 175, isHovered ? 150 : 100) :
							new Color(255, 255, 255, isHovered ? 150 : 100));
				g.fillRect(495, curTab == defaultTab ? curY + 10 : curY, 22, 10);

				curY += 21;
				curTab++;
			}

			final long time = System.currentTimeMillis() - startTime;
			String[] lines = null;
			switch(selTab) {
			case 0: // General info
				lines = new String [] {
						"ParaFishNCook v" + VERSION,
						"-",
						"Trips: " + formatAmount(trips) +
						" (" + formatAmount(3600000.0 / time * trips) + " trips/h)",
						"Trees found: " + (cook ? trees.size() : "--"),
						"Levels gained: " + (fishingLvlsGained +
								wcLvlsGained + fmLvlsGained + cookingLvlsGained),
								"Status: " + getStatus(),
								"Time running: " + formatTime(time)
				};
				break;
			case 1: // Fishing
				double xpPerMs = fishingXPGained / time;
				lines = new String[] {
						"ParaFishNCook v" + VERSION,
						"-",
						"Fish caught: " + formatAmount(fishCaught) +
						" (" + formatAmount((int)(3600000.0 / time * fishCaught)) + " fish/h)",
						"Fishing lvl: " + skills.getCurrentSkillLevel(STAT_FISHING) +
						" (gained: " + fishingLvlsGained + ")",
						"Level in: " + (xpPerMs != 0 ?
								formatTime(skills.getXPToNextLevel(STAT_FISHING) / xpPerMs) :
						"--:--:--"),
						"XP gained: " + formatAmount(fishingXPGained) +
						" (" + formatAmount(3600000.0 / time * fishingXPGained) + " xp/h)",
						"%fishing"
				};
				break;
			case 2: // Woodcutting
				xpPerMs = wcXPGained / time;
				lines = new String[] {
						"ParaFishNCook v" + VERSION,
						"-",
						"Logs chopped: " + formatAmount(logsChopped) +
						" (" + formatAmount(3600000.0 / time * logsChopped) + " logs/h)",
						"Wc level: " + skills.getCurrentSkillLevel(STAT_WOODCUTTING) +
						" (gained: " + wcLvlsGained + ")",
						"Level in: " + (xpPerMs != 0 ?
								formatTime(skills.getXPToNextLevel(STAT_WOODCUTTING) / xpPerMs) :
						"--:--:--"),
						"XP gained: " + formatAmount(wcXPGained) +
						" (" + formatAmount(3600000.0 / time * wcXPGained) + " xp/h)",
						"%wc",
				};
				break;
			case 3: // Firemaking
				xpPerMs = fmXPGained / time;
				lines = new String[] {
						"ParaFishNCook v" + VERSION,
						"-",
						"Logs burned: " + formatAmount(logsBurned) +
						" (" + formatAmount(3600000.0 / time * logsBurned) + " logs/h)",
						"Fm level: " + skills.getCurrentSkillLevel(STAT_FIREMAKING) +
						" (gained: " + fmLvlsGained + ")",
						"Level in: " + (xpPerMs != 0 ?
								formatTime(skills.getXPToNextLevel(STAT_WOODCUTTING) / xpPerMs) :
						"--:--:--"),
						"XP gained: " + formatAmount(fmXPGained) +
						" (" + formatAmount(3600000.0 / time * fmXPGained) + " xp/h)",
						"%fm"
				};
				break;
			case 4: // Cooking
				xpPerMs = cookingXPGained / time;
				lines = new String[] {
						"ParaFishNCook v" + VERSION,
						"-",
						"Fish cooked: " + formatAmount(fishCooked) +
						" (" + formatAmount(3600000.0 / time * fishCooked) + " fish/h)",
						"Cooking lvl: " + skills.getCurrentSkillLevel(STAT_COOKING) +
						" (gained: " + cookingLvlsGained + ")",
						"Level in: " + (xpPerMs != 0 ?
								formatTime(skills.getXPToNextLevel(STAT_COOKING) / xpPerMs) :
						"--:--:--"),
						"XP gained: " + formatAmount(cookingXPGained) +
						" (" + formatAmount(3600000.0 / time * cookingXPGained) + " xp/h)",
						"%cooking"
				};
				break;
			}
			if(lines != null) {
				final int width = getWidth(lines) + 6,
				x = 494 - width;
				curY = 249;

				g.setColor(new Color(0, 0, 0, 175));
				g.fillRect(x, 234, width, 105);
				g.setColor(new Color(255, 255, 255, 100));
				g.fillRect(x, 234, width, 57);
				g.setColor(Color.LIGHT_GRAY);
				g.drawRect(x, 234, width, 105);

				g.setColor(Color.WHITE);
				for(final String line : lines) {
					if(line.equals("-")) {
						g.drawLine(x + 3, curY - 5, x + width - 6, curY - 5);
						curY += 11;
						continue;
					} else if(line.startsWith("%")) {
						Color c = null;
						int percent = 0, xpLeft = 0;
						if(line.equals("%fishing")) {
							c = new Color(118, 164, 239, 150);
							percent = skills.getPercentToNextLevel(STAT_FISHING);
							xpLeft = skills.getXPToNextLevel(STAT_FISHING);
						} else if(line.equals("%wc")) {
							c = new Color(113, 242, 57, 150);
							percent = skills.getPercentToNextLevel(STAT_WOODCUTTING);
							xpLeft = skills.getXPToNextLevel(STAT_WOODCUTTING);
						} else if(line.equals("%fm")) {
							c = new Color(239, 5, 5, 150);
							percent = skills.getPercentToNextLevel(STAT_FIREMAKING);
							xpLeft = skills.getXPToNextLevel(STAT_FIREMAKING);
						} else if(line.equals("%cooking")) {
							c = new Color(231, 236, 10, 150);
							percent = skills.getPercentToNextLevel(STAT_COOKING);
							xpLeft = skills.getXPToNextLevel(STAT_COOKING);
						}
						g.setColor(new Color(0, 0, 0, 200));
						g.fillRect(x + 3, curY - 10, width - 6, 15);
						g.setColor(c);
						g.fillRect(x + 5, curY - 8, (int)((width - 10) * (percent / 100.0)), 11);
						g.setColor(new Color(255, 255, 255, 50));
						g.fillRect(x + 3, curY - 10, width - 6, 7);
						g.setColor(Color.WHITE);
						g.drawString("XP left: " + formatAmount(xpLeft) + " - " + percent + "%",
								x + 7, curY + 2);
					} else {
						g.drawString(line, x + 3, curY);
					}
					curY += 15;
				}
			}
			if(!gui.isVisible()) { // Draw little Gui button
				final boolean isHovered = 494 <= m.x && m.x < 514 &&
						224 <= m.y && m.y < 234;
				g.setFont(new Font(null, Font.PLAIN, 7));
				g.setColor(new Color(0, 0, 0, 175));
				g.fillRect(494, 224, 24, 10);
				g.setColor(Color.LIGHT_GRAY);
				g.drawRect(494, 224, 24, 10);
				g.setColor(new Color(255, 255, 255, isHovered ? 150 : 100));
				g.fillRect(494, 224, 24, 5);
				g.setColor(Color.WHITE);
				g.drawString("GUI", 498, 231);

				if(isHovered && m.pressed){
					gui.setVisible(true);
				}
			}
		}
	}

	public void serverMessageRecieved(final ServerMessageEvent s) {
		if(!run) {
			return;
		}
		final String msg = s.getMessage();
		if(msg.startsWith("You catch")) {
			for(final Fish f : curStyle.getCatches()) {
				if(msg.contains(f.toString().toLowerCase())) {
					fishCaught++;
					fishingXPGained += f.getFishingXPGain();
					break;
				}
			}
		} else if (msg.startsWith("You successfully cook") ||
				msg.startsWith("You manage to cook") ||
				msg.startsWith("You roast")) {
			for(final Fish f : curStyle.getCatches()) {
				if(msg.contains(f.toString().toLowerCase())) {
					fishCooked++;
					cookingXPGained += f.getCookingXPGain();
					break;
				}
			}
		} else if (msg.startsWith("You've just advanced a")) {
			if(msg.contains("Fishing")) {
				fishingLvlsGained++;
			} else if (msg.contains("Woodcutting")) {
				wcLvlsGained++;
			} else if (msg.contains("Firemaking")) {
				fmLvlsGained++;
			} else if (msg.contains("Cooking")) {
				cookingLvlsGained++;
			}
		} else if (msg.startsWith("You get some")) {
			for(final Log l : Log.values()) {
				if(msg.contains(l.toString().toLowerCase())) {
					logsChopped++;
					wcXPGained += l.getWcXPGain();
					break;
				}
			}
		} else if (msg.startsWith("You do not have a hatchet")) {
			hasHatchet = false;
		}
	}
}