import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;

import org.rsbot.bot.Bot;
import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.Skills;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSPlayer;
import org.rsbot.script.wrappers.RSTile;
import org.rsbot.util.ScreenshotUtil;

@ScriptManifest(authors = {"Famous"}, category = "Mining", name = "Famous Miner", version = 3.35, description = "<html><head>"
		+ "</head><body>"
		+ "<center>"
		+ "<b><font size=\"5\" color=\"green\">"
		+ "Famous Miner"
		+ " V3.35"
		+ "</font></b>"
		+ "<br></br>"
		+ "<i><font size=\"4\" color=\"black\">Mines and banks ores in various locations with the option to PowerMine. This script checks for the latest updates.</font></i>"
		+ "<br></br>"
		+ "<b><font size=\"4\" color=\"black\">Which location would you like to mine in?</font></b>"
		+ "<br></br>"
		+ "<select name='location'>"
		+ "<option>East Varrock"
		+ "<option>West Varrock"
		+ "<option>Rimmington"
		+ "<option>Al Kharid"
		+ "<option>Falador"
		+ "<option>West Lumbridge"
		+ "<option>Draynor"
		+ "<option>Barbarian Village"
		+ "<option>Varrock Dungeon"
		+ "<br></br>"
		+ "<b><font size=\"4\" color=\"black\">Which rock would you like to mine?</font></b>"
		+ "<br></br>"
		+ "<select name='Rock'>"
		+ "<option>Copper"
		+ "<option>Tin"
		+ "<option>Iron"
		+ "<option>Coal"
		+ "<option>Clay"
		+ "<option>Gold"
		+ "<option>Silver"
		+ "<option>Addy"
		+ "<option>Mith"
		+ "<br></br>"
		+ "<b><font size=\"4\" color=\"black\">Would you like to Power Mine?</font></b>"
		+ "<br></br>"
		+ "<select name='powerMine'>"
		+ "<option>No"
		+ "<option>Yes"
		+ "<br></br>"
		+ "<b><font size=\"4\" color=\"black\">Would you like to enable paint?</font></b>"
		+ "<br></br>"
		+ "<select name='paintE'>"
		+ "<option>Yes"
		+ "<option>No"
		+ "<br></br>"
		+ "</body></html>")

public class FamousMiner extends Script implements PaintListener,
		ServerMessageListener {

	// Variables
	public int miningAnimation = 625;
	public int[] gemID = {1617, 1619, 1621, 1623};
	public int[] pickaxe = {1265, 1267, 1269, 1271, 1273, 1275, 1296, 380302, 379433, 379181};
	public int rocksMined = 0;
	public int gemsMined = 0;
	public int levelsGained = 0;
	public int bankID;
	public int banker;
	private int mouseSpeed = 9;
	String Location;
	String Rock = "";
	String Tile = "";
	String location = "";
	String status = "";
	public int[] rock;
	public boolean powerMine;
	public boolean paintE;
	public String update = "Checking for updates...";
	final ScriptManifest properties = getClass().getAnnotation(
			ScriptManifest.class);

	//AntiBan
	FamousMinerAntiBan antiban;
	Thread t;

	// Paint Variables
	public static final int MINING_STAT = Skills.getStatIndex("Mining");
	public long startTime = System.currentTimeMillis();
	int startLVL = skills.getCurrentSkillLevel(Constants.STAT_MINING);
	int nextLVL = skills.getXPToNextLevel(Constants.STAT_MINING);

	// Paths
	RSTile lumTile = new RSTile(3221, 3219);
	RSTile bankTile = new RSTile(3270, 3168);

	public RSTile mineTile;
	public RSTile clayTile;
	public RSTile coalTile;
	public RSTile ironTile;
	public RSTile goldTile;
	public RSTile mithTile;
	public RSTile silverTile;
	public RSTile copperTile;
	public RSTile tinTile;
	public RSTile addyTile;
	public RSTile gateTile;
	public RSTile gateTile2;
	public RSTile guardTile;
	public RSTile[] bankToMine;
	public RSTile[] mineToBank;

	private boolean atBank() {
		return distanceTo(bankTile) < 10;
	}

	public boolean atLumbridge() {
		return distanceTo(lumTile) <= 8;
	}

	@Override
	public int getMouseSpeed() {
		return mouseSpeed;
	}

	private boolean bank() {
		if (RSInterface.getInterface(Constants.INTERFACE_BANK).isValid()) {
			bank.depositAllExcept(pickaxe);
		} else {
			openBank();
		}
		return false;
	}

	public static double getScriptVersion() {
		try {
			URL url = new URL("http://famousscripts.webs.com/scripts/FamousMinerVERSION.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(new
					BufferedInputStream(url.openConnection().getInputStream())));
			double ver = Double.parseDouble(br.readLine().trim());
			br.close();
			return ver;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public boolean failFix() {
		final RSTile dest = getDestination();
		return dest == null || distanceTo(getDestination()) < random(2, 8)
				|| !getMyPlayer().isMoving();
	}

	private boolean gettingAttacked() {
		return getMyPlayer().isInCombat();
	}

	public double getVersion() {
		return 3.35;
	}

	public void openDoor(RSTile a, RSTile b) {
		long st = System.currentTimeMillis();
		do {
			if ((System.currentTimeMillis() - st) > 750) {
				setCameraRotation(random(0, 360));
				st = System.currentTimeMillis();
			}
			moveMouse(midPoint(Calculations.tileToScreen(a), Calculations
					.tileToScreen(b)), 3, 3);
		} while (!listContainsString(getMenuItems(), "pen"));
		while (listContainsString(getMenuItems(), "ire") || listContainsString(getMenuItems(), "hop down")) {
			setCameraRotation(random(0, 360));
			wait(random(200, 500));
		}
		clickMouse(true);
		wait(random(100, 200));
	}

	private boolean listContainsString(final java.util.List<String> list,
			final String string) {
		try {
			int a;
			for (a = list.size() - 1; a-- >= 0;) {
				if (list.get(a).contains(string)) {
					return true;
				}
			}
		} catch (final Exception e) {
		}
		return false;
	}

	public Point midPoint(Point p1, Point p2) {
		return (new Point((p1.x + p2.x) / 2, (p1.y + p2.y) / 2));
	}

	public boolean mineRocks() {
		final RSObject Rock = getNearestObjectByID(rock);
		if (Rock == null) {
			return false;
		}

		if (getMyPlayer().getAnimation() == miningAnimation || getMyPlayer().getAnimation() == 626 || getMyPlayer().getAnimation() == 624 ||getMyPlayer().getAnimation() == 627) {
			return false;

		} else {
			if (getMyPlayer().getAnimation() != miningAnimation && Rock != null) {
				atObject(Rock, "Mine");
				wait(random(950, 1100));
				return true;
		}
	}
		return false;
	}

	public boolean inSquare(final int maxX, final int maxY, final int minX,
							final int minY) {
		final int x = getMyPlayer().getLocation().getX();
		final int y = getMyPlayer().getLocation().getY();
		return x >= minX && x <= maxX && y >= minY && y <= maxY;
	}

	public int drop(){
	int[] toKeep = new int[] {1265, 1267, 1269, 1271, 1273, 1275, 1296, 380302, 379433, 379181 , 995 };
	dropAllExcept(toKeep);
	dropAllExcept(toKeep);
	dropAllExcept(toKeep);
	dropAllExcept(toKeep);
	return random(510, 760);
	}

	// Loop
	public int loop() {
		if (!t.isAlive()) {
			t.start();
			log("Antiban Started.");
		}
		if (getMyPlayer().getAnimation() == miningAnimation || getMyPlayer().getAnimation() == 624 ||getMyPlayer().getAnimation() == 627) {
			return 1800;
		}
		if(getInterface(211).containsText("You need a Pickaxe to mine this")) {
			log("You do not have a pickaxe. Stopping Script.");
			stopScript();
		}

		if (Location.equals("East Varrock")) {
			if (isInventoryFull()) {
				if (powerMine) {
						status = "Dropping Ore";
							drop();
				} else {
					if (inSquare(3257, 3423, 3250, 3420)) {
						status = "Banking";
						bank();
						return random(500, 2200);
					} else {
						if (failFix()) {
							walkPathMM(randomizePath(
									fixPath(generateProperPath(new RSTile(3254,
											3421))), 2, 2), 15);
						}
						status = "Walking to Bank";
						walkPath(mineToBank);
					}
					return random(1000, 2000);
				}
				return random(210, 1502);
			}
			if (distanceTo(mineTile) > 10) {
				if (failFix()) {
					status = "Walking to Mine";
					walkPath(bankToMine);
					walkTileMM(mineTile);
				}
				return random(187, 2172);
			}
			if (getMyPlayer().isMoving()) {
				return random(1232, 2310);
			}
			if (gettingAttacked()) {
				status = "Avoiding Combat";
				setRun(true);
				walkTo(randomizeTile(mineTile, 2, 1));
				return random(1400, 2000);
			}
			setRun(true);
			mineRocks();
			status = "Mining";
			return 900;

		} else if (Location.equals("Rimmington")) {
			if (isInventoryFull()) {
				if (powerMine) {
						status = "Dropping Ore";
							drop();
				} else {
					if (inSquare(3018, 3358, 3009, 3355)) {
						status = "Banking";
						bank();
						return random(500, 2200);
					} else {
						if (failFix()) {
							walkPathMM(randomizePath(
									fixPath(generateProperPath(new RSTile(3013,
											3356))), 2, 2), 15);
						}
						status = "Walking to Bank";
						walkPath(mineToBank);
					}
					return random(1000, 2000);
				}
				return random(210, 1502);
			}
			if (distanceTo(mineTile) > 10) {
				if (failFix()) {
					status = "Walking to Mine";
					walkPath(bankToMine);
					walkTileMM(mineTile);
				}
				return random(187, 2172);
			}
			if (getMyPlayer().isMoving()) {
				return random(1232, 2310);
			}
			setRun(true);
			mineRocks();
			status = "Mining";
			return 900;

		} else if (Location.equals("Al Kharid")) {
			if (isInventoryFull()) {
				if (powerMine) {
						status = "Dropping Ore";
							drop();
				} else {
					if (atBank()) {
						status = "Banking";
						bank();
						return random(500, 2200);
					} else {
						if (failFix()) {
							walkPathMM(randomizePath(
									fixPath(generateProperPath(new RSTile(3270,
											3168))), 2, 2), 15);
						}
						status = "Walking to Bank";
						walkPath(mineToBank);
					}
					return random(1000, 2000);
				}
				return random(210, 1502);
			}
			if (distanceTo(mineTile) > 10) {
				if (failFix()) {
					status = "Walking to Mine";
					walkPath(bankToMine);
					walkTileMM(mineTile);
				}
				return random(187, 2172);
			}
			if (getMyPlayer().isMoving()) {
				return random(1232, 2310);
			}
			setRun(true);
			mineRocks();
			status = "Mining";
			return 900;

		} else if (Location.equals("West Varrock")) {
			if (isInventoryFull()) {
				if (powerMine) {
						status = "Dropping Ore";
							drop();
				} else {
					if (atBank()) {
						status = "Banking";
						bank();
						return random(500, 2200);
					} else {
						if (failFix()) {
							walkPathMM(randomizePath(
									fixPath(generateProperPath(new RSTile(3185,
											3435))), 2, 2), 15);
						}
						status = "Walking to Bank";
						walkPath(mineToBank);
					}
					return random(1000, 2000);
				}
				return random(210, 1502);
			}
			if (distanceTo(mineTile) > 10) {
				if (failFix()) {
					status = "Walking to Mine";
					walkPath(bankToMine);
					walkTileMM(mineTile);
				}
				return random(187, 2172);
		}
			if (getMyPlayer().isMoving()) {
				return random(1232, 2310);
			}
			setRun(true);
			mineRocks();
			status = "Mining";
			return 900;

		} else if (Location.equals("Falador")) {
			if (isInventoryFull()) {
				if (powerMine) {
						status = "Dropping Ore";
							drop();
				}
			}
			if (getMyPlayer().isMoving()) {
				return random(1232, 2310);
			}
			setRun(true);
			mineRocks();
			status = "Mining";
			return 900;

		} else if (Location.equals("West Lumbridge")) {
			if (isInventoryFull()) {
				if (powerMine) {
						status = "Dropping Ore";
							drop();
				}
			}
			if (getMyPlayer().isMoving()) {
				return random(1232, 2310);
			}
			setRun(true);
			mineRocks();
			status = "Mining";
			return 900;

		} else if (Location.equals("Varrock Dungeon")) {
			if (isInventoryFull()) {
				if (powerMine) {
						status = "Dropping Ore";
							drop();
				}
			}
			if (getMyPlayer().isMoving()) {
				return random(1232, 2310);
			}
			setRun(true);
			mineRocks();
			status = "Mining";
			return 900;

		} else if (Location.equals("Barbarian Village")) {
			if (isInventoryFull()) {
				if (powerMine) {
						status = "Dropping Ore";
							drop();
				} else {
					if (atBank()) {
						status = "Banking";
						bank();
						return random(500, 2200);
					} else {
						if (failFix()) {
							walkPathMM(randomizePath(
									fixPath(generateProperPath(new RSTile(3094,
											3490))), 2, 2), 15);
						}
						status = "Walking to Bank";
						walkPath(mineToBank);
					}
					return random(1000, 2000);
				}
				return random(210, 1502);
			}
			if (distanceTo(mineTile) > 10) {
				if (failFix()) {
					status = "Walking to Mine";
					walkPath(bankToMine);
					walkTileMM(mineTile);
				}
				return random(187, 2172);
			}
			if (getMyPlayer().isMoving()) {
				return random(1232, 2310);
			}
			setRun(true);
			mineRocks();
			status = "Mining";
			return 900;

		} else if (Location.equals("Draynor")) {
			if (isInventoryFull()) {
				if (powerMine) {
						status = "Dropping Ore";
							drop();
				} else {
					if (atBank()) {
						status = "Banking";
						bank();
						return random(500, 2200);
					} else {
						if (failFix()) {
							walkPathMM(randomizePath(
									fixPath(generateProperPath(new RSTile(3093, 3243))), 2, 2), 15);
						}
						status = "Walking to Bank";
						walkPath(mineToBank);
					}
					return random(1000, 2000);
				}
				return random(210, 1502);
			}
			if (distanceTo(mineTile) > 10) {
				if (failFix()) {
					status = "Walking to Mine";
					walkPath(bankToMine);
					walkTileMM(mineTile);
				}
				return random(187, 2172);
			}
			if (getMyPlayer().isMoving()) {
				return random(1232, 2310);
			}
			setRun(true);
			mineRocks();
			status = "Mining";
			return 900;
		}
		return 50;
	}

	// onFinish
	public void onFinish() {
		ScreenshotUtil.takeScreenshot(true);
		Bot.getEventManager().removeListener(PaintListener.class, this);
		Bot.getEventManager().removeListener(ServerMessageListener.class, this);
		antiban.stopThread = true;
		log("Thank You for using Famous Miner!");
		log("Ores Mined: " + rocksMined);
		log("Levels Gained: " + levelsGained);
	}

	//Credits to purefocus for progress bar.
	public void ProgBar(Graphics g,int posX, int posY, int width, int height, int Progress, Color color1, Color color2, Color text){
		int[] c1 = {color1.getRed() , color1.getGreen() , color1.getBlue() , 150};
		int[] c2 = {color2.getRed() , color2.getGreen() , color2.getBlue() , 150};
		if(c1[0]>230){c1[0]=230;}if(c1[1]>230){c1[1]=230;}if(c1[2]>230){c1[2]=230;}
		if(c2[0]>230){c2[0]=230;}if(c2[1]>230){c2[1]=230;}if(c2[2]>230){c2[2]=230;}
		g.setColor(new Color(c1[0],c1[1],c1[2],200));
		g.fillRoundRect(posX, posY, width, height, 5, 12);
		g.setColor(new Color(c1[0]+25,c1[1]+25,c1[2]+25,200));
		g.fillRoundRect(posX, posY, width, height/2, 5, 12);
		g.setColor(new Color(c2[0],c2[1],c2[2],200));
		g.fillRoundRect(posX, posY, (Progress*width)/100, height, 5, 12);
		g.setColor(new Color(c2[0]+25,c2[1]+25,c2[2]+25,150));
		g.fillRoundRect(posX, posY, (Progress*width)/100, height/2, 5, 12);
		g.setColor(Color.LIGHT_GRAY);
		g.drawRoundRect(posX, posY, width, height, 5, 12);
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, height));
		g.setColor(text);
		g.drawString(Progress + "%", posX+(width/6), posY+(height+height/20));
	}

	// Paint
	public void onRepaint(final Graphics g) {
		if (!paintE) {
			return;
		}
		long millis = System.currentTimeMillis() - startTime;
		final long hours = millis / (1000 * 60 * 60);
		millis -= hours * 1000 * 60 * 60;
		final long minutes = millis / (1000 * 60);
		millis -= minutes * 1000 * 60;
		final long seconds = millis / 1000;
		final int xx = 561;
		final int yy = 225;
		g.setColor(new Color(0, 0, 0, 175));
		g.fillRoundRect(555, 210, 175, 250, 10, 10);
		g.setColor(Color.black);
		g.drawString("Famous Miner", xx + 1, yy + 1);
		g.setColor(Color.white);
		g.drawString("Famous Miner", xx, yy);
		g.setColor(Color.white);
		g.drawString("Current Level: "
				+ skills.getCurrentSkillLevel(Constants.STAT_MINING) + "", 561,
				255);
		g.drawString("Ores Mined: " + Integer.toString(rocksMined) + "", 561,
				275);
		g.drawString("Levels Gained: " + Integer.toString(levelsGained) + "",
				561, 295);
		g
				.drawString("EXP Till Next Level: "
						+ skills.getXPToNextLevel(Constants.STAT_MINING) + "",
						561, 315);
		g.drawString("Gems Mined: "
			+ Integer.toString(gemsMined) + "",561, 365);
		g.drawString("Time Running:", 561, 335);
		g.drawString("" + hours + ":" + minutes + ":" + seconds + "", 561, 345);
		g.drawString("Status: " + status, 561, 395);
		g.setColor(Color.red);
		g.drawString("" + update + ".", 561, 450);
		final int percent = skills.getPercentToNextLevel(Constants.STAT_MINING);
		ProgBar(g, 11, 323, 145, 11, percent, Color.black, Color.green, Color.white);
	}

	public boolean onStart(final Map<String, String> args) {
		status = "Starting up";
		antiban = new FamousMinerAntiBan();
		t = new Thread(antiban);
		mouseSpeed = 9;
		log("Famous Miner Started");
		rocksMined = 0;
		levelsGained = 0;
		gemsMined = 0;
		final String FIM = args.get("powerMine");
		final String FIM2 = args.get("location");
		final String FIM4 = args.get("paintE");
		final String FIM5 = args.get("Rock");

		powerMine = FIM.equals("Yes");

		if (FIM2.equals("East Varrock")) {
			Location = "East Varrock";
			bankID = 11402;
			banker = 5912;
			bankToMine = new RSTile[]{new RSTile(3254, 3421),
					new RSTile(3262, 3429), new RSTile(3274, 3429),
					new RSTile(3283, 3427), new RSTile(3286, 3415),
					new RSTile(3291, 3407), new RSTile(3292, 3394),
					new RSTile(3293, 3387), new RSTile(3291, 3375),
					new RSTile(3285, 3366)};
			mineToBank = reversePath(bankToMine);
			mineTile = new RSTile(3285, 3367);
			gateTile = new RSTile(3273, 3429);
			gateTile2 = new RSTile(3273, 3428);
			guardTile = new RSTile(3292, 3384);

		} else if (FIM2.equals("West Varrock")) {
			Location = "West Varrock";
			bankID = 11402;
			banker = 5912;
			bankToMine = new RSTile[]{new RSTile(3184, 3436),
					new RSTile(3176, 3429), new RSTile(3167, 3419),
					new RSTile(3169, 3409), new RSTile(3169, 3399),
					new RSTile(3175, 3389), new RSTile(3179, 3383),
					new RSTile(3182, 3372)};
			mineToBank = reversePath(bankToMine);
			bankTile = new RSTile(3185, 3435);

		} else if (FIM2.equals("Rimmington")) {
			Location = "Rimmington";
			bankID = 11758;
			bankToMine = new RSTile[]{new RSTile(3012, 3356),
					new RSTile(3007, 3350), new RSTile(3007, 3339),
					new RSTile(3007, 3327), new RSTile(3005, 3317),
					new RSTile(3004, 3306), new RSTile(2998, 3295),
					new RSTile(2995, 3286), new RSTile(2993, 3273),
					new RSTile(2992, 3261), new RSTile(2981, 3257),
					new RSTile(2976, 3250), new RSTile(2971, 3241)};
			mineToBank = reversePath(bankToMine);

		} else if (FIM2.equals("Al Kharid")) {
			Location = "Al Kharid";
			bankID = 35647;
			bankToMine = new RSTile[]{new RSTile(3270, 3167),
					new RSTile(3276, 3174), new RSTile(3283, 3185),
					new RSTile(3282, 3196), new RSTile(3279, 3209),
					new RSTile(3277, 3220), new RSTile(3275, 3231),
					new RSTile(3278, 3242), new RSTile(3278, 3252),
					new RSTile(3282, 3262), new RSTile(3288, 3272),
					new RSTile(3296, 3279), new RSTile(3298, 3293),
					new RSTile(3300, 3304), new RSTile(3297, 3312)};
			mineToBank = reversePath(bankToMine);
			bankTile = new RSTile(3270, 3168);

		} else if (FIM2.equals("Falador")) {
			Location = "Falador";
			bankID = 11758;
			ironTile = new RSTile(3299, 3310);
			bankTile = new RSTile(3270, 3168);

		} else if (FIM2.equals("West Lumbridge")) {
			Location = "West Lumbridge";

		} else if (FIM2.equals("Draynor")) {
			Location = "Draynor";
			bankID = 2213;
			bankToMine = new RSTile[]{new RSTile(3093, 3243),
					new RSTile(3100, 3235), new RSTile(3109, 3229),
					new RSTile(3121, 3226), new RSTile(3130, 3219),
					new RSTile(3136, 3209), new RSTile(3140, 3199),
					new RSTile(3144, 3189), new RSTile(3147, 3182),
					new RSTile(3149, 3169), new RSTile(3150, 3158),
					new RSTile(3147, 3148)};
			mineToBank = reversePath(bankToMine);
			bankTile = new RSTile(3093, 3243);

		} else if (FIM2.equals("Barbarian Village")) {
			Location = "Barbarian Village";
			bankID = 26972;
			bankToMine = new RSTile[]{new RSTile(3094, 3490),
					new RSTile(3093, 3481), new RSTile(3099, 3477),
					new RSTile(3095, 3466), new RSTile(3090, 3457),
					new RSTile(3091, 3446), new RSTile(3092, 3437),
					new RSTile(3089, 3429), new RSTile(3080, 3422)};
			mineToBank = reversePath(bankToMine);
			bankTile = new RSTile(3094, 3490);

		} else if (FIM2.equals("Varrock Dungeon")) {
			Location = "Varrock Dungeon";
			bankID = 11402;
			bankTile = new RSTile(3185, 3435);
		}

		if (FIM5.equals("Tin")) {
			rock = new int[]{9714, 9716, 31077, 31078, 11933, 11934, 11935, 11958, 11959, 11957, 29227, 29229};
		} else if (FIM5.equals("Copper")) {
			rock = new int[]{9708, 9709, 9710, 31080, 31081, 31082, 11936, 11937, 11938, 11960, 11962, 11961, 29230, 29231};
		} else if (FIM5.equals("Iron")) {
			rock = new int[]{11954, 11955, 11956, 9719, 9717, 9718, 29221, 29222, 29223, 31071, 31072, 31073, 37307,
					37308, 37309};
		} else if (FIM5.equals("Gold")) {
			rock = new int[]{9720, 9722, 37310, 37312};
		} else if (FIM5.equals("Coal")) {
			rock = new int[]{31070, 31068, 11930, 11931, 11932, 29215, 29216, 29217};
		} else if (FIM5.equals("Clay")) {
			rock = new int[]{31062, 31063, 15503, 15505, 9711, 9713};
		} else if (FIM5.equals("Addy")) {
			rock = new int[]{19939, 11941, 29233, 29235, 31083, 31085};
		} else if (FIM5.equals("Silver")) {
			rock = new int[]{37304, 37305, 37306, 11948, 11949, 29224, 29225, 29226, 11950};
		} else if (FIM5.equals("Mith")) {
			rock = new int[]{11939, 11942, 11944, 11943, 29236, 32438, 32439, 31086, 31088};
		}

		//Mine Tile Check
		if (FIM2.equals("East Varrock") && (FIM5.equals("Tin"))) {
			mineTile = new RSTile(3283, 3363);
		} else if (FIM2.equals("East Varrock") && (FIM5.equals("Copper"))) {
			mineTile = new RSTile(3287, 3363);
		} else if (FIM2.equals("East Varrock") && (FIM5.equals("Iron"))) {
			mineTile = new RSTile(3286, 3368);
			//////////////////////////
			///////Al Kharid//////////
			//////////////////////////
		} else if (FIM2.equals("Al Kharid") && (FIM5.equals("Copper"))) {
			mineTile = new RSTile(3297, 3314);
		} else if (FIM2.equals("Al Kharid") && (FIM5.equals("Iron"))) {
			mineTile = new RSTile(3298, 3311);
		} else if (FIM2.equals("Al Kharid") && (FIM5.equals("Addy"))) {
			mineTile = new RSTile(3299, 3316);
		} else if (FIM2.equals("Al Kharid") && (FIM5.equals("Silver"))) {
			mineTile = new RSTile(3302, 3313);
		} else if (FIM2.equals("Al Kharid") && (FIM5.equals("Coal"))) {
			mineTile = new RSTile(3300, 3299);
		} else if (FIM2.equals("Al Kharid") && (FIM5.equals("Mith"))) {
			mineTile = new RSTile(3302, 3305);
		} else if (FIM2.equals("Al Kharid") && (FIM5.equals("Tin"))) {
			mineTile = new RSTile(3299, 3316);
			///////////////////////////
			///////Rimmington//////////
			///////////////////////////
		} else if (FIM2.equals("Rimmington") && (FIM5.equals("Gold"))) {
			mineTile = new RSTile(2976, 3235);
		} else if (FIM2.equals("Rimmington") && (FIM5.equals("Tin"))) {
			mineTile = new RSTile(2986, 3237);
		} else if (FIM2.equals("Rimmington") && (FIM5.equals("Iron"))) {
			mineTile = new RSTile(2971, 3240);
		} else if (FIM2.equals("Rimmington") && (FIM5.equals("Copper"))) {
			mineTile = new RSTile(2979, 3246);
		} else if (FIM2.equals("Rimmington") && (FIM5.equals("Clay"))) {
			mineTile = new RSTile(2986, 3237);
			///////////////////////////
			///////West Varrock////////
			///////////////////////////
		} else if (FIM2.equals("West Varrock") && (FIM5.equals("Silver"))) {
			mineTile = new RSTile(3177, 3367);
		} else if (FIM2.equals("West Varrock") && (FIM5.equals("Clay"))) {
			mineTile = new RSTile(3180, 3371);
		} else if (FIM2.equals("West Varrock") && (FIM5.equals("Tin"))) {
			mineTile = new RSTile(3182, 3376);
		} else if (FIM2.equals("West Varrock") && (FIM5.equals("Iron"))) {
			mineTile = new RSTile(3175, 3367);
			///////////////////////////
			///////West Lumbridge//////
			///////////////////////////
		} else if (FIM2.equals("West Lumbridge") && (FIM5.equals("Tin"))) {
			mineTile = new RSTile(3227, 3417);
		} else if (FIM2.equals("West Lumbridge") && (FIM5.equals("Copper"))) {
			mineTile = new RSTile(3228, 3417);
			///////////////////////////
			/////////Draynor///////////
			///////////////////////////
		} else if (FIM2.equals("Draynor") && (FIM5.equals("Coal"))) {
			mineTile = new RSTile(3146, 3150);
		} else if (FIM2.equals("Draynor") && (FIM5.equals("Mith"))) {
			mineTile = new RSTile(3146, 3147);
		} else if (FIM2.equals("Draynor") && (FIM5.equals("Addy"))) {
			mineTile = new RSTile(3147, 3147);
			///////////////////////////
			//////Barbarian Village////
			///////////////////////////
		} else if (FIM2.equals("Barbarian Village") && (FIM5.equals("Tin"))) {
			mineTile = new RSTile(3080, 3419);
		} else if (FIM2.equals("Barbarian Village") && (FIM5.equals("Coal"))) {
			mineTile = new RSTile(3082, 3422);
			////////////////////////////
			//////Dungeon///////////////
			////////////////////////////
		} else if (FIM2.equals("Varrock Dungeon") && (FIM5.equals("Coal"))) {
			mineTile = new RSTile(3137, 9869);
		} else if (FIM2.equals("Varrock Dungeon") && (FIM5.equals("Mith"))) {
			mineTile = new RSTile(3135, 9871);
		} else if (FIM2.equals("Varrock Dungeon") && (FIM5.equals("Addy"))) {
			mineTile = new RSTile(3139, 9873);
		} else if (FIM2.equals("Varrock Dungeon") && (FIM5.equals("Iron"))) {
			mineTile = new RSTile(3139, 9873);
		} else if (FIM2.equals("Varrock Dungeon") && (FIM5.equals("Tin"))) {
			mineTile = new RSTile(3139, 9873);
		} else if (FIM2.equals("Varrock Dungeon") && (FIM5.equals("Copper"))) {
			mineTile = new RSTile(3141, 9879);
		} else if (FIM2.equals("Varrock Dungeon") && (FIM5.equals("Silver"))) {
			mineTile = new RSTile(3137, 9879);
		}

		paintE = FIM4.equals("Yes");

		if (getCurrentTab() != Constants.TAB_INVENTORY) {
			wait(random(2000, 4000));
			openTab(Constants.TAB_INVENTORY);
			wait(random(200, 400));
		}

		if (getScriptVersion() == getVersion()) {
			update = "You have the latest version";
		} else {
			update = "You need to update this script";
			log("Your script needs to be updated. Please visit the link below:");
			log("Check Famousscripts.forumotion.com to obtain the latest version.");
		}

		return true;
	}

	public boolean openBank() {
		final RSObject bank = getNearestObjectByID(bankID);
		if (bank == null) {
			return false;
		}
		if (!tileOnScreen(bank.getLocation())) {
			turnToTile(bank.getLocation(), 15);
		}
		return atTile(bank.getLocation(), "Use-quickly");
	}

	// Server Messages
	public void serverMessageRecieved(final ServerMessageEvent arg0) {
		final String serverString = arg0.getMessage();
		if (serverString.contains("You've just advanced")) {
			levelsGained++;
		}
		if (serverString.contains("You manage to mine")) {
			rocksMined++;
		}
		if (serverString.contains("wishes to trade with you")) {
			sendText("No thanks", true);
		}
		if (serverString.contains("You just found a")) {
			gemsMined++;
		}
	}

	private int start(final RSTile[] path) {
		int start = 0;
		for (int a = path.length - 1; a > 0; a--) {
			if (tileOnMinimap(path[a])) {
				start = a;
				break;
			}
		}
		return start;
	}

	private boolean tileOnMinimap(final RSTile tile) {
		final Point p = tileToMinimap(tile);
		return Math.sqrt(Math.pow(627 - p.x, 2) + Math.pow(85 - p.y, 2)) < random(
				60, 74);
	}

	private RSTile checkTile(final RSTile tile) {
		if (tileOnMap(tile)) {
			return tile;
		}
		final RSTile loc = getMyPlayer().getLocation();
		final RSTile walk = new RSTile((loc.getX() + tile.getX()) / 2, (loc
				.getY() + tile.getY()) / 2);
		return tileOnMap(walk) ? walk : checkTile(walk);
	}

	private boolean walkPath(final RSTile[] path) {
		for (int i = start(path); i < path.length; i++) {
			if (!isRunning() && getEnergy() > random(40, 60)) {
				clickMouse(random(707, 762), random(90, 121), true);
			}
			walkTo(randomizeTile(path[i], 1, 1));
			waitToMove(2000);
			if (path[i] == path[path.length - 1]) {
				break;
			}
			while (!tileOnMinimap(path[i + 1])) {
				if (!getMyPlayer().isMoving()) {
					walkTo(checkTile(randomizeTile(path[i + 1], 1, 1)));
				}
			}
		}
		return distanceTo(path[path.length - 1]) <= 4;
	}

	public boolean hoverPlayer() {
        RSPlayer player = null;
        int[] validPlayers = Bot.getClient().getRSPlayerIndexArray();
        org.rsbot.accessors.RSPlayer[] players = Bot.getClient().getRSPlayerArray();

        for (int element : validPlayers) {
            if (players[element] == null) {
                continue;
            }

            player = new RSPlayer(players[element]);
            String playerName = player.getName();
            String myPlayerName = getMyPlayer().getName();
            if(playerName.equals(myPlayerName)) {
                continue;
            }
            try {
                RSTile targetLoc = player.getLocation();
                Point checkPlayer = Calculations.tileToScreen(targetLoc);
                if(pointOnScreen(checkPlayer) && checkPlayer != null) {
                    clickMouse(checkPlayer, 5, 5, false);
                } else {
                    continue;
                }
            return true;
            } catch (Exception ignored) {
            }
        }
        return player != null;
    }


	private class FamousMinerAntiBan implements Runnable {
		public boolean stopThread;

		public void run() {
			while (!stopThread) {
				try {
					if (random(0, 15) == 0) {
						final char[] LR = new char[]{KeyEvent.VK_LEFT,
								KeyEvent.VK_RIGHT};
						final char[] UD = new char[]{KeyEvent.VK_DOWN,
								KeyEvent.VK_UP};
						final char[] LRUD = new char[]{KeyEvent.VK_LEFT,
								KeyEvent.VK_RIGHT, KeyEvent.VK_UP,
								KeyEvent.VK_UP};
						final int random2 = random(0, 2);
						final int random1 = random(0, 2);
						final int random4 = random(0, 4);

						if (random(0, 3) == 0) {
							moveMouseSlightly();
							Bot.getInputManager().pressKey(LR[random1]);
							Thread.sleep(random(100, 400));
							Bot.getInputManager().pressKey(UD[random2]);
							Thread.sleep(random(300, 600));
							Bot.getInputManager().releaseKey(UD[random2]);
							Thread.sleep(random(100, 400));
							Bot.getInputManager().releaseKey(LR[random1]);

						if(random(0, 8) == 0) {
							openTab(Constants.TAB_STATS);
							moveMouse(660, 227, 50, 28);
							Thread.sleep(random(3000, 6000));
						}

						if(random(0, 10) == 0) {
							hoverPlayer();
		                    Thread.sleep(random(750,3000));
		                    while (isMenuOpen()) {
		                        moveMouseRandomly(750);
		                        Thread.sleep(random(100, 500));
		                }
						}
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