import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Map;

import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSObject;

@ScriptManifest(authors = { "Dwu" }, category = "Mining", name = "Iron powerminer - Reloaded", version = 1.0, description = "<html><body><h2>Iron powerminer - Reloaded</h2><hr>by Dwu<br><p>Powermines iron - does granite too (no waterskin support yet) <p><form><input type='checkbox' name='use2Drop' value='true' >Use 2 ore spot ?</input><br><input type='checkbox' name='perse' value='true'>Use 3 rock surround(experimental) ?</input><br><br><p>3 rocksurround speed management;<p><br><input type='checkbox' name='vitunhidas' value='true'>very slow  </input><input type='checkbox' name='hidas' value='true'>slow  </input><input type='checkbox' name='normi' value='true' >normal  </input><input type='checkbox' name='keski' value='true' >mid  </input><input type='checkbox' name='nopee' value='true'>fast  </input><input type='checkbox' name='eeppinen' value='true'>epic  </input><input type='checkbox' name='ylitappo' value='true'>overkill  </input><br><br><p>How far is the script allowed to wander ( how far do you want the script to go for rocks )<p><br><input type='checkbox' name='wander1' value='true'>1 tile   </input><input type='checkbox' name='wander2' value='true'>2 tile   </input><input type='checkbox' name='wander3' value='true'>3 tile   </input><input type='checkbox' name='wander4' value='true' checked='true'>4 tile   </input><input type='checkbox' name='wander5' value='true'>5 tile   </input><input type='checkbox' name='wander6' value='true'>6 tile   </input><input type='checkbox' name='wander7' value='true'>7 tile   </input><br><p>mousespeed<p><br><input type='checkbox' name='mspe1' value='true'>1 </input><input type='checkbox' name='mspe2' value='true'>2 </input><input type='checkbox' name='mspe3' value='true'>3 </input><input type='checkbox' name='mspe4' value='true'>4 </input><input type='checkbox' name='mspe5' value='true'>5 </input><input type='checkbox' name='mspe6' value='true'>6 </input><input type='checkbox' name='mspe7' value='true'>7 </input><input type='checkbox' name='mspe8' value='true' checked = 'true'>8 </input></form></body></html>")
public class DPowerIronReloaded extends Script implements PaintListener {

	public static int MouseSpeed = 4;
	public static final int[] Rock = new int[] { 2093, 2093, 2092, 9717, 9719,
			9718, 11956, 11955, 11954, 11557, 37307, 37309, 10947, 31071,
			31072, 31073, 37308, 14913, 14914 };
	public boolean ardy = false;
	public boolean epic = false;
	public boolean fast = false;
	public boolean Kaks;
	public boolean mid = false;
	public int mspe;
	public boolean msped1 = false;
	public boolean msped2 = false;
	public boolean msped3 = false;
	public boolean msped4 = false;
	public boolean msped5 = false;
	public boolean msped6 = false;
	public boolean msped7 = false;
	public boolean msped8 = false;
	public boolean normal = false;
	public boolean overkill = false;
	public boolean pertti = false;
	public boolean slow = false;
	public int startLevel = 0;
	public long startTime = System.currentTimeMillis();
	public int startXP = 0;
	public boolean use2 = false;
	public boolean vslow = false;
	public int wait1 = 0;
	public int wait2 = 0;
	public boolean wand1 = false;
	public boolean wand2 = false;
	public boolean wand3 = false;
	public boolean wand4 = false;

	public boolean wand5 = false;

	public boolean wand6 = false;

	public boolean wand7 = false;
	public int wanderfar = 0;

	int xx1 = random(575, 580);
	int xx2 = random(540, 545);
	int xx3 = random(575, 580);

	int xx4 = random(540, 545);

	int xx5 = random(575, 580);

	int xx6 = random(540, 545);

	public boolean Yks;

	int yy1 = random(379, 383);
	int yy2 = random(416, 424);
	int yy3 = random(402, 407);
	int yy4 = random(446, 449);
	int yy5 = random(440, 445);
	int yy6 = random(458, 463);

	public void Drop() {
		if (getCurrentTab() == Constants.TAB_INVENTORY) {

			moveMouse(xx1, yy1, 3, 3);
			clickMouse(false);

			moveMouse(xx2, yy2, 3, 3);
			clickMouse(true);

			moveMouse(xx3, yy3, 3, 3);
			clickMouse(false);

			moveMouse(xx4, yy4, 3, 3);
			clickMouse(true);

			moveMouse(xx5, yy5, 3, 3);
			clickMouse(false);

			moveMouse(xx6, yy6, 3, 3);
			clickMouse(true);

		} else {

			openTab(Constants.TAB_INVENTORY);
		}

	}

	public void Drop2() {
		if (getCurrentTab() == Constants.TAB_INVENTORY) {

			moveMouse(xx3, yy3, 3, 3);
			clickMouse(false);

			moveMouse(xx4, yy4, 3, 3);
			clickMouse(true);

			moveMouse(xx5, yy5, 3, 3);
			clickMouse(false);

			moveMouse(xx6, yy6, 3, 3);
			clickMouse(true);
		} else {

			openTab(Constants.TAB_INVENTORY);
		}

	}

	@Override
	protected int getMouseSpeed() {
		return mspe;
	}

	@Override
	public int loop() {
		if (!isInventoryFull()) {
			Mine();
		}

		if (isInventoryFull()) {
			if (ardy) {
				atInventoryItem(440, "Drop");
			} else {
				if (use2) {
					Drop2();
				} else {
					Drop();
				}
			}
		}

		return random(100, 200);
	}

	public void Mine() {
		if (getEnergy() > random(25, 100)) {
			setRun(true);
		}
		if (getMyPlayer().getAnimation() == -1 && !getMyPlayer().isMoving()) {
			final RSObject obj = getNearestObjectByID(DPowerIronReloaded.Rock);
			if (obj != null) {
				if (distanceTo(obj.getLocation()) > wanderfar) {
					log("No rocks near, waiting.");
					wait(10);
				} else {
					final Point Yks = new Point(Calculations.tileToScreen(obj
							.getLocation()));
					final Point Kaks = new Point(Yks.x + random(-3, 3), Yks.y
							+ random(-3, 3));
					moveMouse(Kaks);
					clickMouse(true);
					wait(random(500, 750));
					if (ardy) {
						wait(random(wait1, wait2));
						atInventoryItem(440, "Drop");
					}
					if (!ardy) {
						if (getMyPlayer().isMoving()) {
							wait(random(900, 1100));
						}
					}

				}
			}
		}
	}

	public void onRepaint(final Graphics g) {
		long millis = System.currentTimeMillis() - startTime;
		final long hours = millis / (1000 * 60 * 60);
		millis -= hours * 1000 * 60 * 60;
		final long minutes = millis / (1000 * 60);
		millis -= minutes * 1000 * 60;
		final long seconds = millis / 1000;
		final float totalMin = hours * 60 * 60 + minutes * 60 + seconds;
		final int XPChange = skills.getCurrentSkillExp(14) - startXP;
		final int LevelChange = skills.getCurrentSkillLevel(14) - startLevel;
		final float perHour = XPChange / (totalMin / 60);

		g.setColor(Color.black);
		g.drawString("Dwu's PowerIronReloaded", 200, 365);
		g.drawString("Runtime: " + hours + " hours " + minutes + " minutes "
				+ seconds + " seconds.", 200, 380);
		g.drawString("Current: " + skills.getCurrentSkillLevel(14)
				+ " levels and " + skills.getCurrentSkillExp(14) + " exp.",
				200, 395);
		g.drawString("Gained: " + LevelChange + " levels and " + XPChange
				+ " exp.", 200, 410);
		g.drawString(skills.getXPToNextLevel(14) + " XP to next level, we are "
				+ skills.getPercentToNextLevel(14) + "% to next level.", 200,
				425);
		g
				.drawString("Averaging at " + perHour * 60 + " xp per hour.",
						200, 440);
	}

	@Override
	public boolean onStart(final Map<String, String> args) {
		use2 = args.get("use2Drop") != null;
		ardy = args.get("perse") != null;
		vslow = args.get("vitunhidas") != null;
		slow = args.get("hidas") != null;
		normal = args.get("normi") != null;
		fast = args.get("nopee") != null;
		epic = args.get("eeppinen") != null;
		mid = args.get("keski") != null;
		overkill = args.get("ylitappo") != null;
		wand1 = args.get("wander1") != null;
		wand2 = args.get("wander2") != null;
		wand3 = args.get("wander3") != null;
		wand4 = args.get("wander4") != null;
		wand5 = args.get("wander5") != null;
		wand6 = args.get("wander6") != null;
		wand7 = args.get("wander7") != null;
		msped1 = args.get("mspe1") != null;
		msped2 = args.get("mspe2") != null;
		msped3 = args.get("mspe3") != null;
		msped4 = args.get("mspe4") != null;
		msped5 = args.get("mspe5") != null;
		msped6 = args.get("mspe6") != null;
		msped7 = args.get("mspe7") != null;
		msped8 = args.get("mspe8") != null;

		if (use2) {
			log("We are going to use 2 ore drop system");
		} else {
			if (ardy) {
				log("we are using 3 rock surround drop");
			}
			log("We are going to use 3 ore drop system");

		}

		if (vslow && ardy) {
			log("Instant drop, mode: very slow");
			wait1 = 2500;
			wait2 = 3000;
		}

		if (slow && ardy) {
			log("Instant drop, mode: slow");
			wait1 = 2000;
			wait2 = 2500;
		}

		if (normal && ardy) {
			log("Instant drop, mode: normal");
			wait1 = 1500;
			wait2 = 2000;
		}

		if (mid && ardy) {
			log("Instant drop, mode: mid");
			wait1 = 1100;
			wait2 = 1300;
		}

		if (fast && ardy) {
			log("Instant drop, mode: fast");
			wait1 = 900;
			wait2 = 1100;
		}

		if (epic && ardy) {
			log("Instant drop, mode: epic");
			wait1 = 600;
			wait2 = 800;
		}

		if (overkill && ardy) {
			log("Instant drop, mode: overkill");
			wait1 = 500;
			wait2 = 650;
		}

		if (wand1) {
			wanderfar = 1;
		}

		if (wand2) {
			wanderfar = 2;
		}

		if (wand3) {
			wanderfar = 3;
		}

		if (wand4) {
			wanderfar = 4;
		}

		if (wand5) {
			wanderfar = 5;
		}

		if (wand6) {
			wanderfar = 6;
		}

		if (wand7) {
			wanderfar = 7;
		}

		if (msped1) {
			mspe = 1;
		}

		if (msped2) {
			mspe = 2;
		}

		if (msped3) {
			mspe = 3;
		}

		if (msped4) {
			mspe = 4;
		}

		if (msped5) {
			mspe = 5;
		}

		if (msped6) {
			mspe = 6;
		}

		if (msped7) {
			mspe = 7;
		}

		if (msped8) {
			mspe = 8;
		}

		log("Welcome to powermining world, we got cookies.");
		startTime = System.currentTimeMillis();
		if (isLoggedIn()) {
			startLevel = skills.getCurrentSkillLevel(14);
			startXP = skills.getCurrentSkillExp(14);
		}
		return true;
	}
}
