import org.rsbot.bot.Bot;
import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.script.*;
import org.rsbot.script.wrappers.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;


@ScriptManifest(authors = {"Fallen"}, category = "Magic", name = "Fallen's Superheater", version = 3.03,
		description = "<html><head>"
				+ "</head><body style='font-family: Eurostile; margin: 10px;'>"
				+ "<center><img src=\"http://img245.imageshack.us/img245/4683/scriptlogo.png\" /></center>"
				+ "<br><strong>Which bars would you like to make? </strong> "
				+ "<br> "
				+ "<select name=\'Bars to make\'><option selected>Bronze<option>Iron<option>Steel<option>Silver<option>Gold<option>Mithril<option>Adamantite<option>Runite</select>"
				+ "<br> "
				+ "<br> "
				+ "Log out after: <input type=\"text\" name=\"CastsUntilLogout\" value=\"0\"> casts."
				+ "<br> (0 = Until runs out of ores/runes.) "
				+ "<br> "
				+ "<br><strong>Version 3.03</strong></b>"
				+ "<br>This script uses the spell 'Superheat', to turn ores into bars."
				+ "<br><strong>Instructions: </strong></b>"
				+ "Start the script next to a bank, have required runes in your inventory (natures). Scroll your bank so that the ores can be seen."
				+ "</body></html>")

public class FallenSuperheater extends Script implements PaintListener, ServerMessageListener {

	final ScriptManifest properties = getClass().getAnnotation(
			ScriptManifest.class);

	private enum State {
		OPENBANK, OUTOFORES, BANK, SUPERHEAT
	}

	//Mostly paint int's
	public int status;
	public long startTime = System.currentTimeMillis();
	private long scriptStartTime = 0;
	private long minutes = 0;
	public int BarCounter = 0;
	private int startXPM;
	private int startXPS;
	public int startLevelM;
	public int levelsGainedM;
	public int startLevelS;
	public int levelsGainedS;
	public int currentLevelM;
	public int currentLevelS;

	public Image title;

	public int BarEXP = 0;

	public String BarType;
	public int AmountOfCasts;

	//Some variables & anti-ban
	boolean start = true;
	int actAFK = 201;
	int speed = 10;

	boolean bankTwice = false;

	public int barType = 0;
	public int BarID = 0;
	public int Ore1 = 0;
	public int Ore2 = 0;
	public int Ore1WD = 0;
	public int Ore2WD = 0;
	public int Ore2PerSpell = 0;
	public int Ore1InvAm = 0;
	public int Ore2InvAm = 0;

	//Ore ID's
	public int copperOre = 436;
	public int tinOre = 438;
	public int ironOre = 440;
	public int silverOre = 442;
	public int goldOre = 444;
	public int coalOre = 453;
	public int mithrilOre = 447;
	public int adamantOre = 449;
	public int runeOre = 451;
	//Bar ID's
	public int bronzeBar = 2349;
	public int ironBar = 2351;
	public int steelBar = 2353;
	public int silverBar = 2355;
	public int goldBar = 2357;
	public int mithrilBar = 2359;
	public int adamantBar = 2361;
	public int runeBar = 2363;

	public int nature = 561;

	//Item prices from G.E
	public int BarPrice = 0;
	public int Ore1Price = 0;
	public int Ore2Price = 0;
	public int naturePrice = 0;

	/*-------------------------------------------------------------------
		 * ------------------   P   A   I   N   T   -------------------------
		 ------------------------------------------------------------------*/
	//This variable is used for Antialiasing. DO NOT DELETE!
	/*private final RenderingHints rh = new RenderingHints(
		RenderingHints.KEY_TEXT_ANTIALIASING,
		RenderingHints.VALUE_TEXT_ANTIALIAS_ON); */
	public void onRepaint(Graphics g) {
		long runTime = System.currentTimeMillis() - scriptStartTime;
		long seconds1 = runTime / 1000;
		int XPGainedSmithing;
		int XPGainedMagic;
		if (seconds1 >= 60) {
			minutes = seconds1 / 60;
			seconds1 -= minutes * 60;
		}
		if (minutes >= 60) {
			long hours = minutes / 60;
			minutes -= hours * 60;
		}
		if (startXPM == 0) {
			startXPM = skills.getCurrentSkillExp(STAT_MAGIC);
		}
		if (startXPS == 0) {
			startXPS = skills.getCurrentSkillExp(STAT_SMITHING);
		}
		if (startLevelM == 0) {
			startLevelM = skills.getCurrentSkillLevel(STAT_MAGIC);
		}
		if (startLevelS == 0) {
			startLevelS = skills.getCurrentSkillLevel(STAT_SMITHING);
		}
		XPGainedMagic = (skills.getCurrentSkillExp(STAT_MAGIC) - startXPM);
		final int XPHRM = (int) ((XPGainedMagic) * 3600000D / (System.currentTimeMillis() - startTime));
		XPGainedSmithing = (skills.getCurrentSkillExp(STAT_SMITHING) - startXPS);
		final int XPHRS = (int) ((XPGainedSmithing) * 3600000D / (System.currentTimeMillis() - startTime));
		final int profit = ((BarPrice) - (Ore1Price) - (Ore2Price) - (naturePrice));
		levelsGainedM = skills.getCurrentSkillLevel(STAT_MAGIC) - startLevelM;
		levelsGainedS = skills.getCurrentSkillLevel(STAT_SMITHING) - startLevelS;
		currentLevelM = skills.getCurrentSkillLevel(STAT_MAGIC);
		currentLevelS = skills.getCurrentSkillLevel(STAT_SMITHING);
		//((Graphics2D)g).setRenderingHints(rh);
		if (isLoggedIn()) {
			long millis = System.currentTimeMillis() - startTime;
			long hours = millis / (1000 * 60 * 60);
			millis -= hours * (1000 * 60 * 60);
			long minutes = millis / (1000 * 60);
			millis -= minutes * (1000 * 60);
			long seconds = millis / 1000;
			g.drawImage(title, 9, 185, null);
			g.setFont(new Font("Arial", 0, 11));
			g.setColor(Color.GREEN);
			g.drawString("Version 3.03", 248, 202);
			//Time
			g.setFont(new Font("Verdana", 0, 12));
			g.setColor(Color.WHITE);
			g.drawString("Time: " + hours + ":" + minutes + ":"
					+ seconds + ".", 405, 202);
			//HOUR RATES
			g.setColor(Color.WHITE);
			g.setFont(new Font("Verdana", 0, 11));
			g.setColor(Color.CYAN);
			g.drawString("Magic EXP/H: " + XPHRM, 42, 240);
			g.setColor(Color.WHITE);
			g.drawString("Smith EXP/H: " + XPHRS, 215, 240);
			g.setColor(Color.YELLOW);
			g.drawString("Profit/H: " + XPHRM / 53 * +profit, 392, 240);
			//Magic
			g.setFont(new Font("Verdana", 0, 10));
			g.setColor(Color.CYAN);
			g.drawString("Exp/Cast: 53", 20, 275);
			g.drawString("Magic level: " + currentLevelM + " (" + levelsGainedM + ")", 20, 290);
			g.drawString("EXP to level: " + skills.getXPToNextLevel(STAT_MAGIC), 20, 305);
			g.drawString("Magic EXP gained: " + (BarCounter * 53), 20, 320);
			//Smithing
			g.setColor(Color.WHITE);
			g.drawString("Exp/" + BarType + " bar: " + BarEXP, 190, 275);
			g.drawString("Smithing level: " + currentLevelS + " (" + levelsGainedS + ")", 190, 290);
			g.drawString("EXP to level: " + skills.getXPToNextLevel(STAT_SMITHING), 190, 305);
			g.drawString("Smith EXP gained: " + (BarCounter * BarEXP), 190, 320);
			//Bars & Profit
			g.setColor(Color.YELLOW);
			g.drawString("Profit/" + BarType + " Bar: " + profit, 362, 275);
			g.drawString("" + BarType + " bars/Hour: " + XPHRM / 53, 362, 290);
			g.drawString("Bars made: " + BarCounter + " " + BarType, 362, 305);
			g.drawString("Total profit: " + BarCounter * +profit, 362, 320);
			if (AmountOfCasts != 0) {
				g.setColor(Color.WHITE);
				g.drawString("Casts left: " + ((AmountOfCasts) - (BarCounter)), 420, 15);
			}
		}
	}


	/*------------------------------------------------------------
		 * ------------------  O N   S T A R T  ----------------------
		 -----------------------------------------------------------*/
	@Override
	public boolean onStart(final Map<String, String> args) {
		try {
			title = ImageIO.read(new URL("http://img691.imageshack.us/img691/8546/spaint3.png"));
		} catch (final java.io.IOException e) {
			e.printStackTrace();
		}
		//Options (ARGS)
		if (args.get("Bars to make").equals("Bronze")) {
			barType = 1;
			BarID = bronzeBar;
			Ore1 = copperOre;
			Ore2 = tinOre;
			Ore1WD = 13;
			Ore2WD = 13;
			bankTwice = true;
			BarType = "Bronze";
			BarEXP = (int) 6.25;
			Ore2PerSpell = 1;
			Ore1InvAm = 13;
			Ore2InvAm = 13;
		}
		if (args.get("Bars to make").equals("Iron")) {
			barType = 2;
			BarID = ironBar;
			Ore1 = ironOre;
			//Ore2 = ironOre;
			Ore1WD = 0;
			//Ore2WD = 0;
			bankTwice = false;
			BarType = "Iron";
			BarEXP = (int) 12.5;
			Ore2PerSpell = 0;
			Ore1InvAm = 27;
			Ore2InvAm = 0;
		}
		if (args.get("Bars to make").equals("Steel")) {
			barType = 3;
			BarID = steelBar;
			Ore1 = ironOre;
			Ore2 = coalOre;
			Ore1WD = 9;
			Ore2WD = 0;
			bankTwice = true;
			BarType = "Steel";
			BarEXP = (int) 17.5;
			Ore2PerSpell = 2;
			Ore1InvAm = 9;
			Ore2InvAm = 18;
		}
		if (args.get("Bars to make").equals("Silver")) {
			barType = 4;
			BarID = silverBar;
			Ore1 = silverOre;
			//Ore2 = silverOre;
			Ore1WD = 0;
			//Ore2WD = 0;
			bankTwice = false;
			BarType = "Silver";
			BarEXP = (int) 13.7;
			Ore2PerSpell = 0;
			Ore1InvAm = 27;
			Ore2InvAm = 0;
		}
		if (args.get("Bars to make").equals("Gold")) {
			barType = 5;
			BarID = goldBar;
			Ore1 = goldOre;
			//Ore2 = goldOre;
			Ore1WD = 0;
			//Ore2WD = 0;
			bankTwice = false;
			BarType = "Gold";
			BarEXP = (int) 22.5;
			Ore2PerSpell = 0;
			Ore1InvAm = 27;
			Ore2InvAm = 0;
		}
		if (args.get("Bars to make").equals("Mithril")) {
			barType = 6;
			BarID = mithrilBar;
			Ore1 = mithrilOre;
			Ore2 = coalOre;
			Ore1WD = 5;
			Ore2WD = 0;
			bankTwice = true;
			BarType = "Mithril";
			BarEXP = 30;
			Ore2PerSpell = 4;
			Ore1InvAm = 5;
			Ore2InvAm = 22;
		}
		if (args.get("Bars to make").equals("Adamantite")) {
			barType = 7;
			BarID = adamantBar;
			Ore1 = adamantOre;
			Ore2 = coalOre;
			Ore1WD = 3;
			Ore2WD = 0;
			bankTwice = true;
			BarType = "Adamant";
			BarEXP = (int) 37.5;
			Ore2PerSpell = 6;
			Ore1InvAm = 3;
			Ore2InvAm = 24;
		}
		if (args.get("Bars to make").equals("Runite")) {
			barType = 8;
			BarID = runeBar;
			Ore1 = runeOre;
			Ore2 = coalOre;
			Ore1WD = 3;
			Ore2WD = 0;
			bankTwice = true;
			BarType = "Runite";
			BarEXP = 50;
			Ore2PerSpell = 8;
			Ore1InvAm = 3;
			Ore2InvAm = 24;
		}
		AmountOfCasts = Integer.parseInt(args.get("CastsUntilLogout"));
		log("Obtaining G.E prices...");
		final GEItemInfo BarGE = grandExchange.loadItemInfo(BarID);
		BarPrice = BarGE.getMarketPrice();
		final GEItemInfo Ore1GE = grandExchange.loadItemInfo(Ore1);
		Ore1Price = Ore1GE.getMarketPrice();
		final GEItemInfo Ore2GE = grandExchange.loadItemInfo(Ore2);
		Ore2Price = Ore2GE.getMarketPrice() * Ore2PerSpell;
		final GEItemInfo natureGE = grandExchange.loadItemInfo(nature);
		naturePrice = natureGE.getMarketPrice();
		log("... prices retrieved!");
		log("Profit / Bar: " + (BarPrice) + " - " + (Ore1Price) + " - " + (Ore2Price) + " - " + (naturePrice) + " = " + (BarPrice - Ore1Price - Ore2Price - naturePrice));
		log(" ~ Anti-ban synchronized! ~");
		log("------------------------------------------------");
		log("Fallen's Superheater is now running!");
		if (AmountOfCasts != 0) {
			log("Logging out after " + AmountOfCasts + " casts!");
		}
		return true;
	}

/* ------------------------------------------------------------------------------------------------------------------------------
 * ------------------------------------------------------------------------------------------------------------------------------
 * ---------------------------------------     M   E   T   H   O   D   S     ----------------------------------------------------
 * ------------------------------------------------------------------------------------------------------------------------------
 ------------------------------------------------------------------------------------------------------------------------------*/


	public boolean waitForMageTab(int timeout) {
		long start = System.currentTimeMillis();

		while (System.currentTimeMillis() - start < timeout) {
			if (getCurrentTab() == TAB_MAGIC) {
				break;
			}
			antiBan();
		}
		return false;
	}

	public boolean atLastInventoryItem(int itemID, String option) {
		try {
			if (getCurrentTab() != TAB_INVENTORY && !RSInterface.getInterface(INTERFACE_BANK).isValid() && !RSInterface.getInterface(INTERFACE_STORE).isValid()) {
				openTab(TAB_INVENTORY);
			}

			RSInterfaceChild inventory = getInventoryInterface();
			if (inventory == null || inventory.getComponents() == null)
				return false;

			java.util.List<RSInterfaceComponent> possible = new ArrayList<RSInterfaceComponent>();
			for (RSInterfaceComponent item : inventory.getComponents()) {
				if (item != null && item.getComponentID() == itemID) {
					possible.add(item);
				}
			}

			if (possible.size() == 0)
				return false;

			RSInterfaceComponent item = possible.get(random(possible.size() - 1, possible.size()));
			return atInterface(item, option);
		} catch (Exception e) {
			log("atInventoryItem(int itemID, String option) Error: " + e);
			return false;
		}
	}


	private boolean withdraw(int itemID, int amount) {
		String s = amount + "";
		if (bank.isOpen()) {
			if (amount > 0) {
				if (!bank.atItem(itemID, s)) {
					if (bank.atItem(itemID, "X")) {
						wait(random(500, 800));
						sendText(s, false);
						wait(random(100, 200));
						sendText("", true);
						return true;
					}
					return true;
				}
			} else {
				return bank.atItem(itemID, "All");
			}
		}
		return false;
	}

	public int waitForWithdrawnItem(int item, int timeout) {
		long start = System.currentTimeMillis();

		while (System.currentTimeMillis() - start < timeout) {
			if (getInventoryCount(item) > 0) {
				break;
			}
			wait(100);
		}
		return getInventoryCount(item);
	}


	public int waitForWithdrawnItem2(int item, int timeout) {
		final int currentAmount;
		long start = System.currentTimeMillis();
		currentAmount = getInventoryCount(item);

		while (System.currentTimeMillis() - start < timeout) {
			if (getInventoryCount(item) > currentAmount) {
				break;
			}
			wait(100);
		}
		return getInventoryCount(item);
	}

	public void CheckOres() {
		if (bank.isOpen() && bank.getCount(Ore1) < Ore1InvAm) {
			wait(random(1000, 1500));
			log("Out of ores - Check: 1/3");
			if (bank.isOpen() && bank.getCount(Ore1) < Ore1InvAm) {
				wait(random(1000, 1500));
				log("Out of ores - Check: 2/3");
				if (bank.isOpen() && bank.getCount(Ore1) < Ore1InvAm) {
					wait(random(1000, 1500));
					log("Out of ores - Check: 3/3!");
					bank.close();
					stopScript();
				}
			}
		}
		if (bankTwice) {
			if (bank.isOpen() && bank.getCount(Ore2) < Ore2InvAm) {
				wait(random(1000, 1500));
				log("Out of ores - Check: 1/3");
				if (bank.isOpen() && bank.getCount(Ore2) < Ore2InvAm) {
					wait(random(1000, 1500));
					log("Out of ores - Check: 2/3");
					if (bank.isOpen() && bank.getCount(Ore2) < Ore2InvAm) {
						wait(random(1000, 1500));
						log("Out of ores - Check: 3/3!");
						bank.close();
						stopScript();
					}
				}
			}
		}
	}


	private void bank() {
		bankingAntiBan();
		bank.depositAllExcept(nature, Ore1, Ore2);
		//wait(random(400,600));
		if (bankTwice) {
			if (getInventoryCount(Ore1) == 0) {
				withdraw(Ore1, Ore1WD);
				waitForWithdrawnItem(Ore1, 1500);
			} else if (getInventoryCount(Ore1) > 0 && getInventoryCount(Ore1) < Ore1WD) {
				bank.depositAllExcept(nature, Ore2);
				withdraw(Ore1, Ore1WD);
				waitForWithdrawnItem(Ore1, 1500);
			} else if (getInventoryCount(Ore1) > Ore1InvAm) {
				bank.depositAllExcept(nature, Ore2);
			}
			if (getInventoryCount(Ore1) == Ore1WD) {
				if (getInventoryCount(Ore2) == 0) {
					withdraw(Ore2, Ore2WD);
					waitForWithdrawnItem(Ore2, 1500);
				} else if (getInventoryCount(Ore2) > 7 && getInventoryCount(Ore2) != Ore2InvAm) {
					bank.depositAllExcept(nature, Ore1);
					withdraw(Ore2, Ore2WD);
					waitForWithdrawnItem2(Ore2, 1500);
				} else if (getInventoryCount(Ore2) > 0 && getInventoryCount(Ore2) < 7) {
					withdraw(Ore2, Ore2WD);
					waitForWithdrawnItem2(Ore2, 1500);
				} else if (getInventoryCount(Ore2) > Ore2InvAm) {
					bank.depositAllExcept(nature, Ore1);
				}
			}
			if (getInventoryCount(Ore1) >= 1 && getInventoryCount(Ore2) >= Ore2PerSpell) {
				bank.close();
			}
		}
		if (!bankTwice) {
			if (getInventoryCount(Ore1) == 0) {
				withdraw(Ore1, Ore1WD);
				waitForWithdrawnItem(Ore1, 1500);
			} else if (getInventoryCount(Ore1) > 0 && getInventoryCount(Ore1) < Ore1WD) {
				bank.depositAllExcept(nature);
				withdraw(Ore1, Ore1WD);
				waitForWithdrawnItem(Ore1, 1500);
			} else if (getInventoryCount(Ore1) > Ore1InvAm) {
				bank.depositAllExcept(nature);
			}
			if (inventoryContains(Ore1)) {
				bank.close();
			}
		}

	}


	public void superHeat() {
		//antiBan();
		if (getCurrentTab() != TAB_MAGIC) {
			openTab(TAB_MAGIC);
		}
		castSpell(Constants.SPELL_SUPERHEAT_ITEM);
		if (atLastInventoryItem(Ore1, "Cast")) {
			BarCounter++;
			if ((AmountOfCasts) != 0) {
				if (BarCounter >= AmountOfCasts) {
					log("Goal achieved!");
					stopScript();
				}
			}
			waitForMageTab(1000);
			if (!bank.isOpen()) {
				superHeat();
			}
		} else {
			final int GambleInt1 = random(0, 100);
			if (GambleInt1 >= 50) {
				moveMouse(658, 187, 10, 10);
				clickMouse(true);
				wait(random(200, 300));
			}
			if (GambleInt1 <= 49) {
				moveMouse(646, 377, 50, 15);
				clickMouse(true);
				wait(random(100, 200));
			}
		}
	}


	/* ---------------------------------------------------------------------------------------------------
		 * ---------------------------------------------------------------------------------------------------
		 * --------------------------------   A N T I - B A N   ----------------------------------------------
		 * ----------------------------------------------------------- Standard & While banking --------------
		   -------------------------------------------------------------------------------------------------*/

	/* ----------------------------------
		 * ------------ Standard ------------
		   --------------------------------*/
	public int antiBan() { //The AntiBan method.. You can change the chance of having an antiban function activated. Read below..
		int chckObj = random(1, 701); // The check objective antiban, won't activate if you haven't checked it.
		int hover = random(1, 301); // Hover a player.
		int checkxp = random(1, 501); //Hover Magic exp
		int afk = random(1, actAFK); //Small Afk times
		int camera = random(1, 16); //Camera turning
		int mspeed = random(1, 16); //Mousespeed Changing

		// Change the second number in random(1,..) if you want a certain antiban to activate more often, or less.
		// The higher the number, the less it activates.
		// Read second number as in alches, so 1,501 means it happens once in 501 alches (roughly).

		if (chckObj == 1) {
			openTab(Constants.TAB_SUMMONING);
			wait(random(300, 500));
			moveMouse(644, 394, 51, 6);
			wait(random(900, 1600));
			moveMouse(644, 394, 51, 6);
			wait(random(500, 1000));
			moveMouseRandomly(500);
			wait(random(400, 900));
			openTab(Constants.TAB_MAGIC);
		} else if (hover == 1) {
			hoverPlayer();
			wait(random(1150, 2800));
			while (isMenuOpen()) {
				moveMouseRandomly(750);
				wait(random(400, 1000));
			}
		} else if (checkxp == 1) {
			openTab(TAB_STATS);
			wait(random(400, 800));
			moveMouse(552, 390, 25, 20);
			wait(random(500, 1000));
			moveMouse(552, 390, 25, 20);
			wait(random(900, 1750));
			moveMouseRandomly(700);
			wait(random(300, 800));
			openTab(Constants.TAB_MAGIC);

		} else if (afk == 1) {
			switch (random(1, 3)) {
				case 1:
					wait(random(2250, 8000));
					break;
				case 2:
					wait(random(500, 1000));
					moveMouseRandomly(750);
					wait(random(2000, 7500));
					break;
			}
		} else if (camera == 1) {
			int randomTurn = random(1, 4);
			switch (randomTurn) {
				case 1:
					new CameraRotateThread().start();
					break;
				case 2:
					new CameraHeightThread().start();
					break;
				case 3:
					int randomFormation = random(0, 2);
					if (randomFormation == 0) {
						new CameraRotateThread().start();
						new CameraHeightThread().start();
					} else {
						new CameraHeightThread().start();
						new CameraRotateThread().start();
					}
			}
		} else if (mspeed == 1) {
			speed = random(10, 12);
			getMouseSpeed();
		}
		for (int i = 0; i < 10; i++) {
			if (getCurrentTab() != TAB_MAGIC) {
				wait(300);
			} else {
				break;
			}
		}
		return random(100, 200);
	}

	public class CameraRotateThread extends Thread {

		@Override
		public void run() {
			char LR = KeyEvent.VK_RIGHT;
			if (random(0, 2) == 0) {
				LR = KeyEvent.VK_LEFT;
			}
			Bot.getInputManager().pressKey(LR);
			try {
				Thread.sleep(random(450, 2600));
			} catch (final Exception ignored) {
			}
			Bot.getInputManager().releaseKey(LR);
		}
	}

	public class CameraHeightThread extends Thread {

		@Override
		public void run() {
			char UD = KeyEvent.VK_UP;
			if (random(0, 2) == 0) {
				UD = KeyEvent.VK_DOWN;
			}
			Bot.getInputManager().pressKey(UD);
			try {
				Thread.sleep(random(450, 1700));
			} catch (final Exception ignored) {
			}
			Bot.getInputManager().releaseKey(UD);
		}
	}

	boolean hoverPlayer() {
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
			if (playerName.equals(myPlayerName)) {
				continue;
			}
			try {
				RSTile targetLoc = player.getLocation();
				String name = player.getName();
				Point checkPlayer = Calculations.tileToScreen(targetLoc);
				if (pointOnScreen(checkPlayer) && checkPlayer != null) {
					clickMouse(checkPlayer, 5, 5, false);
					log("ANTIBAN - Hover Player - Right click on " + name);
				} else {
					continue;
				}
				return true;
			} catch (Exception ignored) {
			}
		}
		return player != null;
	}


	public void serverMessageRecieved(ServerMessageEvent msg) {
		String message = msg.getMessage().toLowerCase();
		if (message.contains("have enough nat")) {
			log("Out of nature runes!");
			stopScript();
		} else if (message.contains("have enough fir")) {
			log("Out of fire runes!");
			log("Please equip a staff with unlimited fire runes.");
			stopScript();
		}
	}

	/* ----------------------------------
		 * -------- While banking --------
		   --------------------------------*/
	public void bankingAntiBan() {
		int randomTurn = random(1, 10);
		switch (randomTurn) {
			case 1:
				new CameraRotateThread().start();
				break;
			case 2:
				new CameraHeightThread().start();
				break;
			case 3:
				int randomFormation = random(0, 2);
				if (randomFormation == 0) {
					new CameraRotateThread().start();
					new CameraHeightThread().start();
				} else {
					new CameraHeightThread().start();
					new CameraRotateThread().start();
				}
		}
	}

	/* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		  * ~~~~~~~~~~~~~~~~~~~~~~   Basic script structure:  getState() ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
			~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

	private State getState() {
		if (getInventoryCount(Ore1) < 1 || getInventoryCount(Ore2) < Ore2PerSpell || getInventoryCount(Ore1) > Ore1InvAm || getInventoryCount(Ore2) > Ore2InvAm) {
			if (bank.isOpen()) {
				if (bank.getCount(Ore1) > 0 && bank.getCount(Ore2) >= Ore2PerSpell) {
					return State.BANK;
				} else {
					return State.OUTOFORES;
				}
			} else {
				return State.OPENBANK;
			}
		} else {
			if (!bank.isOpen()) {
				return State.SUPERHEAT;
			} else {
				return State.BANK;
			}
		}
	}


	/* ---------------------------------------------------------------------------------------
	 * ---------------------------------------------------------------------------------------
	 * -------------------------  loop()   and    onFinish()  --------------------------------
	 * ---------------------------------------------------------------------------------------
	   -------------------------------------------------------------------------------------*/
	@Override
	public int loop() {
		final State state = getState(); //Gets the state
		try {
			switch (state) { //Switches between these states based on getState
				case SUPERHEAT:
					superHeat();
					break;
				case OPENBANK:
					bank.open();
					waitForIface(getInterface(Constants.INTERFACE_BANK), 2000);
					wait(random(100, 200));
					break;
				case OUTOFORES:
					CheckOres();
				case BANK:
					bank();
					break;
			}
		} catch (Exception ignored) {
		}
		return random(100, 300);
	}

	public void onFinish() {
		log(+BarCounter + " " + BarType + " bars made.");
		log("Thank you for using Fallen's Superheater.");
	}
}