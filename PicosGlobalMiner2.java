///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//   IMPORTS                                                                                                                                 //
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

import java.awt.Color;
import java.awt.Graphics;
import java.util.Map;

import org.rsbot.bot.Bot;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = { "Pico" }, category = "Mining", name = "Pico's Global Miner 2", version = 2.0, description = "<html><body><center><b><font size='5' color='red'>Pico's Global Miner 2 v2.0</font></b><br></br><font size='4' color='black'>by Pico, AKC_Pico@hotmail.com<br></b></font><center><font size='3' color='red'><i><b>If you cannot see the rest of the information below, make the window height larger.</b></i></font></center><hr></hr><font size='3' color='black'><b>Credits to:</b> PwnZ, Drizzt1112, Drfrijole, USA, Exempt, !@!@, vivalaraza, Nokeo, Dwuxi, Linux_Communist, GSPower, Yanilleiron, Aelin and Fusion89k for scripting/support. I appreciate the time and effort.</b></font><hr></hr><font size='4' color='red'><b>:: Script Settings ::</b></font><hr></hr><b>What would you like to Powermine? </b><br></br><i><font size='4' color='black'>This will mine and drop the specified ore(s) on your screen.</font></i><br></br><br></br><select name='ore'><option>Everything<option>Copper & Tin<option>Copper<option>Tin<option>Gold & Silver<option>Gold<option>Silver<option>Mithril & Adamantite<option>Mithril<option>Adamantite<option>Clay & Coal<option>Clay<option>Coal<option>Sandstone & Granite<option>Sandstone<option>Granite<option>Runite<option>Iron<option>Shilo Gems<option>Rune Essence</select><br></br><hr></hr><font size='4' color='red'><b>:: Script Terms of Use ::</b></font><hr></hr>By using this script I agree that Pico is not held liable for any harm or damage done to me or my account in anyway. (Script will not innitiate otherwise).<br></br><b>Please check, if you agree to the Terms of Use. <input type='checkbox' name='agreement' value='true' align='center'><hr></hr><font size='4' color='red'><b>:: Official thread for Pico's Global Miner V2 ::</b></font><b><center><font size='3' color='blue'><i><b><a href='http://www.rsbot.org/vb/showthread.php?t=18647'>http://www.rsbot.org/vb/showthread.php?t=18647</a></b></i></font></center></center></body></html>")
public class PicosGlobalMiner2 extends Script implements PaintListener {

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Start Script Information //
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/*
	 * SAVE THIS FILE AS: "PicosGlobalMiner2.java" (without "" marks). Created
	 * by Pico - AKC_Pico@hotmail.com (MY ONLY MSN). Created for RSBOT.ORG ONLY
	 * Official Thread Location: http://www.rsbot.org/vb/showthread.php?t=18647
	 * BASE provided by PWNZ. Credits to PwnZ, Drfrijole, TAIOWoodcutter, USA,
	 * Exempt, !@!@, Drizzt1112, Nokeo, Dwuxi, Linux_Communist, ProFisher1,
	 * GSPower, Professional, Yanilleiron, Aelin and Fusion89k for
	 * scripting/support. I appreciate the time and effort. DOES NOT WALK TO
	 * BANK!!! This is a POWER MINER, see Description for more information. I
	 * create my scripts with lots of comments and structure for others to use,
	 * understand and or learn. If you use this script for another base, or any
	 * coding, please leave credits to me. My RSBOT forum name is "Pico",
	 * contact me via MSN anytime for anything. Script Auto checks for current
	 * Version, and has an built in Anti-ban and Global Miner. Image for TESTED
	 * mining areas, if its not here it still is minable, only non tested:
	 * http://www.pico-games.com/PicosGlobalMiner.png. There is ALOT more
	 * information on the official thread, please read and comment. Thankyou.
	 */

	public boolean agreement;

	int avgPerHour;
	long avoidedCombat;
	long avoidedSmokingRocks;
	int checkTime;
	int countToNext;
	private String doing;
	int fails;
	int[] gear = new int[] { 1823, 1825, 1827, 1829 };
	int gems;
	long lastAvgCheck;
	public long lastCheck;
	public String loc;

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Final //
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public boolean logout = false;
	final int MAX_FAILS = 25;

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Int //
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public int miningAnimation = 624;
	int oldExp;
	int oldLevels;
	public RSTile oldPosition = new RSTile(0, 0);
	int oldRockCount;
	public String ore;
	final int[] picks = { 1265, 1267, 1269, 1273, 1271, 1275, 11969, 1823,
			1825, 1827, 1829 };
	int randomRun = random(40, 75);
	private int[] rock = { 2093, 2093, 2092, 9717, 9719, 9717, 9718, 11956,
			11955, 11954, 37307, 37309, 31072, 31073, 31071 };
	int rockCount;
	final int[] smokingRocks = { 11433, 11434, 11435, 11436, 11193 };
	public boolean Sound;
	int standingCount = 0;
	int startExp;

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Long //
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	int startLevel;
	long startTime;
	public int updateCheck = 0;
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// End Script Information //
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Private //
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	double version = 2.0;

	int xpPerRock;

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// START HTML Script Information found in Script Selector run in RSBot. //
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private int antiBan() {
		final int gamble = random(1, 12);

		switch (gamble) {
		case 1:
			return random(500, 750);

		case 2:
			final int x = random(0, 750);
			final int y = random(0, 500);
			moveMouse(0, 0, x, y);
			return random(500, 750);

		case 3:
			openTab(Constants.TAB_INVENTORY);
			return random(500, 750);

		case 4:
			if (getMyPlayer().isMoving()) {
				return random(750, 1000);
			}

			if (System.currentTimeMillis() - lastCheck >= checkTime) {
				lastCheck = System.currentTimeMillis();
				checkTime = random(60000, 180000);

				if (getCurrentTab() != Constants.TAB_STATS) {
					openTab(Constants.TAB_STATS);
				}
				moveMouse(660, 227, 50, 28);
				return random(5000, 8000);
			}

		case 5:
			if (random(1, 8) == 2) {
				int angle = getCameraAngle() + random(-90, 90);
				if (angle < 0) {
					angle = 0;
				}
				if (angle > 359) {
					angle = 0;
				}

				setCameraRotation(angle);
			}

			return random(500, 750);
		}

		return random(500, 750);
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// END HTML Script Information found in Script Selector run in RSBot. //
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public void beep(final int count) {
		if (!Sound) {
			return;
		}
		for (int i = 0; i < count; i++) {
			java.awt.Toolkit.getDefaultToolkit().beep();
			wait(250);
		}
		wait(random(100, 500));
		return;
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// END Agreement for Terms of Usage - Thanks to FreshPrince and USA/PwnZ //
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// START Check for Updates on Pico's Global Miner - Thanks to ProFisher
	// Version 2.0 //
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public String getTermsAndConditions() {
		return "By using this script Pico is not held liable for any harm or damage done to you in anyway.";
	}

	@Override
	public int loop() {
		if (getEnergy() == random(31, 56) || getEnergy() > 56) {
			setRun(true);
		}
		standingStill();

		if (getMyPlayer().isMoving()) {
			return antiBan();
		}

		if (getMyPlayer().getAnimation() == miningAnimation
				|| getMyPlayer().getAnimation() == 625) {
			doing = "Mining an " + ore + " ore.";
			return antiBan();
		}

		if (isInventoryFull()) {
			log("We are dropping your " + ore + "ores...");
			doing = "Dropping an " + ore + " ore.";
			randomStuff();
			antiBan();
			dropAllExcept(picks);
			return antiBan();
		}

		final RSObject rocks = getNearestObjectByID(rock);
		doing = "Looking for an " + ore + " ore.";
		if (rocks == null) {
			return 800;
		}
		atObject(rocks, "Mine");
		return 500;
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// END Check for Updates on Pico's Global Miner - Thanks to ProFisher
	// Version 2.0 //
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// START PAINT - Thanks to ProFisher Version 2.0, and ProMiner //
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void onFinish() {
		Bot.getEventManager().removeListener(PaintListener.class, this);
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// END PAINT - Thanks to ProFisher Version 2.0, and ProMiner //
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// START ANTIBAN - Thanks to Granite Sandstone Power //
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public void onRepaint(final Graphics g) {

		new Color(255, 255, 255, 255);
		final int index = Constants.STAT_MINING;
		int exp = 0;
		int levels;
		long hours = 0, minutes = 0, seconds = 0;
		long time;

		if (startTime == 0) {
			startTime = System.currentTimeMillis();
		}

		time = System.currentTimeMillis() - startTime;
		seconds = time / 1000;
		if (seconds >= 60) {
			minutes = seconds / 60;
			seconds -= minutes * 60;
		}
		if (minutes >= 60) {
			hours = minutes / 60;
			minutes -= hours * 60;
		}

		if (startLevel == 0 || startExp == 0) {
			// No, so define them now.
			startLevel = skills.getCurrentSkillLevel(index);
			startExp = skills.getCurrentSkillExp(index);
			oldExp = 0;
		}

		exp = skills.getCurrentSkillExp(index) - startExp;
		if (exp > oldExp) {
			xpPerRock = exp - oldExp;
			oldExp = exp;
			rockCount++;
			countToNext = skills.getXPToNextLevel(Constants.STAT_MINING)
					/ xpPerRock + 1;
		}

		levels = skills.getCurrentSkillLevel(index) - startLevel;
		if (levels > oldLevels) {
			oldLevels = levels;
		}

		if (System.currentTimeMillis() - lastAvgCheck >= 60000) {
			lastAvgCheck = System.currentTimeMillis();
			avgPerHour = (rockCount - oldRockCount) * 60;
			oldRockCount = rockCount;
		}

		// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Draw Information - Thanks to ProFisher Version 2.0, and ProMiner //
		// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		g.setColor(new Color(255, 0, 0, 160));
		final int[][] paint = new int[][] {
				new int[] { 136, 152, 168, 184, 200, 216, 232, 248, 264, 280,
						296, 312, 328 }, new int[] { 152, 186 } };
		g.fillRoundRect(4, paint[1][0], 200, paint[1][1], 10, 10);
		g.setColor(Color.WHITE);
		g.drawString(getClass().getAnnotation(ScriptManifest.class).name()
				+ " v"
				+ getClass().getAnnotation(ScriptManifest.class).version(), 9,
				171);
		g.drawString("Time running: " + hours + ":" + minutes + ":" + seconds,
				9, 315);
		g.drawString("Rocks Mined: " + rockCount, 9, 235);
		g.drawString("XP Gained: " + exp, 9, 187);
		g.drawString("Levels Gained: " + levels, 9, 203);
		g.drawString("Percent to next level: " + "%"
				+ skills.getPercentToNextLevel(index), 9, 219);
		g.drawString("Rocks to next level: " + countToNext, 9, 267);
		g.drawString("Times avoided combat: " + avoidedCombat, 9, 283);
		g.drawString("Status: " + doing, 9, 299);
		g.drawString("Average per hour: " + avgPerHour, 9, 331);
		g.drawString("Mining for: " + ore, 9, 251);
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// END ANTIBAN - Thanks to Granite Sandstone Power //
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public boolean onStart(final Map<String, String> args) {

		// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// START Load script configuration from arguements. //
		// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		ore = args.get("ore");
		loc = args.get("ore");

		// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// END Load script configuration from arguements. //
		// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// STARI dentification Numbers for Ores //
		// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		if (args.get("ore").equals("Copper & Tin")) {
			rock = new int[] { 11935, 11934, 11933, 11938, 11936, 11963, 11937,
					9709, 9708, 9710, 11960, 11962, 11961, 11958, 31080, 31082,
					31079, 31078, 31077 };
		}
		if (args.get("ore").equals("Copper")) {
			rock = new int[] { 11938, 11936, 11963, 11937, 9709, 9708, 9710,
					11960, 11962, 11961, 31080, 31082 };
		}
		if (args.get("ore").equals("Tin")) {
			rock = new int[] { 11935, 11934, 11933, 11959, 11957, 11959, 11958,
					9714, 9716, 31079, 31077, 31078 };
		}
		if (args.get("ore").equals("Gold & Silver")) {
			rock = new int[] { 37312, 37310, 37305, 37304, 37306, 2311, 9722,
					9720, 11950, 9720, 9722, 37313, 9714, 9716, 9713, 11950,
					11949, 11948, 15505, 15503, 11185, 11184, 11183, 31065,
					31066 };
		}
		if (args.get("ore").equals("Gold")) {
			rock = new int[] { 37312, 37310, 9722, 9720, 15505, 15503, 11185,
					11184, 11183, 9720, 9722, 37313, 31065, 31066 };
		}
		if (args.get("ore").equals("Silver")) {
			rock = new int[] { 37305, 37304, 37306, 2311, 9714, 9716, 9713,
					11950, 11949, 11948, 11950 };
		}
		if (args.get("ore").equals("Mithril & Adamantite")) {
			rock = new int[] { 11942, 11944, 11939, 11941, 32438, 32439, 32435,
					32436, 31086, 31088, 31083 };
		}
		if (args.get("ore").equals("Mithril")) {
			rock = new int[] { 11942, 11944, 11943, 32438, 32439, 31086, 31088 };
		}
		if (args.get("ore").equals("Adamantite")) {
			rock = new int[] { 11939, 11941, 32435, 32436, 11940, 31083, 31084,
					31085, 31083 };
		}
		if (args.get("ore").equals("Clay & Coal")) {
			rock = new int[] { 9711, 9713, 11930, 11931, 15503, 15504, 15505,
					11930, 11932, 11963, 11964, 2096, 2097, 14850, 14851,
					14852, 32426, 32426, 31068, 31069, 31070, 31062, 31063,
					31068 };
		}
		if (args.get("ore").equals("Clay")) {
			rock = new int[] { 9711, 9713, 15503, 15504, 15505, 31062, 31063 };
		}
		if (args.get("ore").equals("Coal")) {
			rock = new int[] { 11930, 11931, 11932, 11930, 11963, 11964, 2096,
					2097, 14850, 14851, 14852, 32426, 32426, 31068, 31069,
					31070, 31068 };
		}
		if (args.get("ore").equals("Sandstone & Granite")) {
			rock = new int[] { 10946, 10947 };
		}
		if (args.get("ore").equals("Sandstone")) {
			rock = new int[] { 10946 };
		}
		if (args.get("ore").equals("Granite")) {
			rock = new int[] { 10947 };
		}
		if (args.get("ore").equals("Runite")) {
			rock = new int[] { 451 };
		}
		if (args.get("ore").equals("Shilo Gems")) {
			rock = new int[] { 11195, 11194, 11364 };
		}
		if (args.get("ore").equals("Rune Essence")) {
			rock = new int[] { 2491, 7936 };
		}
		if (args.get("ore").equals("Everything")) {
			rock = new int[] { 11935, 2491, 11195, 11194, 11364, 11934, 11933,
					11930, 11932, 31086, 37307, 15503, 31083, 31072, 31088,
					31065, 31066, 31073, 31068, 15504, 31078, 15505, 37309,
					31071, 2092, 31062, 31063, 31080, 31079, 31082, 11963,
					2096, 11950, 32435, 32436, 32426, 32438, 32439, 31068,
					31069, 31070, 32427, 14850, 14851, 14852, 2097, 11964,
					11931, 11185, 11184, 11963, 11930, 11959, 11183, 11957,
					11959, 11942, 11943, 2093, 11960, 11958, 11961, 11962,
					11954, 11950, 11956, 11955, 11949, 11948, 15505, 15503,
					2093, 9717, 11959, 11957, 11959, 9719, 9717, 9718, 9711,
					9713, 11944, 11939, 11941, 11938, 11936, 11963, 11937,
					9709, 9708, 9710, 37312, 37310, 37305, 37304, 37306, 2311,
					9722, 9720, 9714, 9716, 9713 };
		}

		// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// START Agreement for Terms of Usage - Thanks to FreshPrince and
		// USA/PwnZ //
		// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		agreement = args.get("agreement") != null ? true : false;
		if (!agreement) {
			log("The Agreement for Pico's Global Miner was not accepted, the script will not be started!");
			return false;
		}
		return true;
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// END RandomStuff //
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// START Run by Exempt //
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// START RandomStuff //
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void randomStuff() {
		final int temp = random(1, 50);
		switch (temp) {
		case 1:
			moveMouse(random(150, 450), random(100, 300));
			break;
		case 2:
			openTab(Constants.TAB_STATS);
			moveMouse(random(170, 470), random(200, 400));
			break;
		case 3:
			openTab(random(0, 13));
			break;
		}
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// END Run by Exempt //
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Start Loop Start loop Start loop Start loop...... //
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public void rotate() {
		final int button = random(37, 40);
		Bot.getInputManager().pressKey((char) button);
		wait(random(500, 1500));
		Bot.getInputManager().releaseKey((char) button);

	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// START Standstill by drizzt1112 (thankyou) //
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public void standingStill() {
		// log("working????");

		if (oldPosition.getX() == getMyPlayer().getLocation().getX()
				&& oldPosition.getY() == getMyPlayer().getLocation().getY()) {
			standingCount++;
			// log("ADDING TO COUNT!!!");
		}
		if (oldPosition.getX() != getMyPlayer().getLocation().getX()
				&& oldPosition.getY() != getMyPlayer().getLocation().getY()) {
			// log("We are moving to our old position...");
			oldPosition = new RSTile(getMyPlayer().getLocation().getX(),
					getMyPlayer().getLocation().getY());
			standingCount = 0;
		}
		if (getMyPlayer().getInteracting() != null
				|| getMyPlayer().getAnimation() != -1) {
			// log("Our stand count is 0!!!");
			standingCount = 0;
		}
		if (standingCount > 5 && standingCount < 20) {
			log("We are not doing anything... Must find ore, SPIN AROUND!");
			rotate();
		}
		// if (standingCount > 15) beep();
		if (standingCount > 30) {
			log("We are stuck, so will logout before the BANHAMMER.");
			log("Please post where you got stuck on the forums :)");
			stopScript();
			logout = true;
		}

		// log("X:"+oldPosition.getX() + "  Y:" + oldPosition.getY() );
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// END Standstill by drizzt1112 (thankyou) //
	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// End Loop Start loop Start loop Start loop...... //
// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
