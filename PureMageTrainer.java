import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.Map;

import org.rsbot.bot.Bot;
import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = { "Marneus901" }, category = "Magic", name = "Mage Trainer", version = 0.1, description = "<html><head><style type='text/css'> hr {color: white} p {margin-left: 20px}</style></head><body><center><b><font size='4' color='red'>Mage Trainer v.01</font></b><br></center><center><table border='0'><tr><td colspan='2'><center><font size='4'><b>:: Script Settings ::</b></font><BR><font size='2' color='red'>Thanks to Aelin for this script setup screen!</font></center></td></tr><tr><td colspan='2'><hr></td></tr><tr><td><center><table border='0'><tr><td colspan='2'><font size='4' color='blue'>This script will cast the chosen spell on<BR>																				   the Monk of Zamorak inside varrock castle.<BR>																				   Just stand in front of the gate (or bot will<BR>																				   logout, adding the lost feature next version).</font></center></td></tr><tr><td><b>Spell : </b></td><td><center><select name='spell'><option>Confuse<option>Weaken<option>Curse<option>Vulnerability<option>Enfeeble<option>Stun</select></center></td></tr><tr><td><b>How many casts?</b><tr><td><center><input type='text' name='amount' value='1000'></center><br></table></center></body></html>")
public class PureMageTrainer extends Script implements ServerMessageListener,
		PaintListener {
	public boolean b_spellChosen = false;
	public int castAmount;
	public int Casts = 0;
	public int gainedXP = 0;
	public int Spell;
	public String spellChosen;
	public int startLevel = skills.getCurrentSkillLevel(Constants.STAT_MAGIC);
	public long startTime, Hours, Minutes, Seconds;
	public int startXP = skills.getCurrentSkillExp(Constants.STAT_MAGIC);

	RSTile theSpot = new RSTile(3214, 3476);

	public boolean atSpellInterface(final int spell, final String actionContains) {
		final RSInterfaceChild i = RSInterface.getChildInterface(192, spell);
		if (!i.isValid()) {
			return false;
		}
		final Rectangle pos = i.getArea();
		if (pos.x == -1 || pos.y == -1 || pos.width == -1 || pos.height == -1) {
			return false;
		}
		moveMouse((int) random(pos.getMinX(), pos.getMaxX()), (int) random(pos
				.getMinY(), pos.getMaxY()));
		clickMouse(true);
		return true;
	}

	public void attackNPC() {
		if (!b_spellChosen) {
			Cast(Spell);
		}
		final RSNPC Monk = getNearestNPCByID(189);
		moveMouse(Monk.getScreenLocation().x + random(-10, 10), Monk
				.getScreenLocation().y
				+ random(-10, 10));
		clickMouse(true);
		wait(random(200, 300));
		if (skills.getCurrentSkillExp(Constants.STAT_MAGIC) - startXP > gainedXP) {
			Casts++;
			gainedXP = skills.getCurrentSkillExp(Constants.STAT_MAGIC)
					- startXP;
			b_spellChosen = false;
			wait(random(200, 300));
		}
	}

	public void Cast(final int Spell) {
		while (getCurrentTab() != Constants.TAB_MAGIC) {
			openTab(Constants.TAB_MAGIC);
			wait(random(50, 50));
		}
		while (!atSpellInterface(Spell, "Cast")) {
			b_spellChosen = true;
		}
	}

	public boolean clickcontinue() {
		if (getContinueChildInterface() != null) {
			if (getContinueChildInterface().getText().contains("to continue")) {
				return atInterface(getContinueChildInterface());
			}
		}
		return false;
	}

	public boolean doingSpell() {
		if (getMyPlayer().getAnimation() == 1164) {
			wait(random(150, 200));
			return true;
		}
		return false;
	}

	public boolean isatSpot() {
		if (!getMyPlayer().getLocation().equals(theSpot)) {
			log("We are not at the spot!" + getMyPlayer().getLocation().getX()
					+ " " + getMyPlayer().getLocation().getY());
			return false;
		}
		return true;
		// Add Varrock teleport to get back to location.
	}

	public boolean isWalking() {
		if (getMyPlayer().getAnimation() == 1) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isWelcomeButton() {
		final RSInterface welcomeInterface = RSInterface.getInterface(378);
		if (welcomeInterface.getChild(45).getAbsoluteX() > 20
				|| !welcomeInterface.getChild(117).getText().equals(
						"10.1120.190")
				&& !welcomeInterface.getChild(117).getText().equals("")) {
			log("We still are in Welcome Screen");
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int loop() {
		if (!isLoggedIn() || isWelcomeButton()) {
			wait(1000);
			return random(250, 500);
		}
		if (!isatSpot()) {
			stopScript();
		}
		if (Casts >= castAmount) {
			log("We're done casting.");
			stopScript();
		}
		Bot.getInputManager().pressKey((char) KeyEvent.VK_UP);
		wait(random(150, 200));
		Bot.getInputManager().releaseKey((char) KeyEvent.VK_UP);
		if (getCameraAngle() > 250 || getCameraAngle() < 290) {
			setCameraRotation(270);
		}
		if (!doingSpell()) {
			attackNPC();
		}
		return random(350, 400);
	}

	@Override
	public void onFinish() {
		Bot.getEventManager().removeListener(PaintListener.class, this);
		Bot.getEventManager().removeListener(ServerMessageListener.class, this);
		return;
	}

	public void onRepaint(final Graphics g) {
		final int X = 310;
		int Y = 20;
		g.setColor(Color.YELLOW);
		g.drawString("Marneus901's Mage Trainer v0.1", X, Y);
		Y += 15;
		g.drawString("Casts : " + Casts, X, Y);
		Y += 15;
		g.drawString("XP Gain : " + gainedXP, X, Y);
		Y += 15;
		g.drawString("XP until level : "
				+ skills.getXPToNextLevel(Constants.STAT_MAGIC), X, Y);
		Y += 15;
		g
				.drawString(
						"Levels Gained : "
								+ (skills
										.getCurrentSkillLevel(Constants.STAT_MAGIC) - startLevel),
						X, Y);
	}

	@Override
	public boolean onStart(final Map<String, String> args) {
		spellChosen = args.get("spell");
		castAmount = Integer.parseInt(args.get("amount"));
		if (spellChosen.equals("Confuse")) {
			Spell = 2;
		} else if (spellChosen.equals("Weaken")) {
			Spell = 7;
		} else if (spellChosen.equals("Curse")) {
			Spell = 11;
		} else if (spellChosen.equals("Vulnerability")) {
			Spell = 51;
		} else if (spellChosen.equals("Enfeeble")) {
			Spell = 54;
		} else if (spellChosen.equals("Stun")) {
			Spell = 58;
		} else {
			return false;
		}
		startTime = System.currentTimeMillis();
		return true;
	}

	public void serverMessageRecieved(final ServerMessageEvent arg0) {
		final String serverString = arg0.getMessage();
		if (serverString.contains("Your foe's")) {
			log("Wait a second, his stat is too low...");
			b_spellChosen = false;
		}
		wait(random(500, 1000));
	}
}
