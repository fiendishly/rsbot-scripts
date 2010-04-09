/*
 * TODO: Add Range Pots - Low Priority
 *       Walk Out Of Cage Failsafe -Done!
 *       GUI to add donations - Done!
 *       spec at 55% - Done!
 *     move paint out of chat box - Done!
 *       
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.List;
import java.util.Map;

import org.rsbot.bot.Bot;
import org.rsbot.bot.input.Mouse;
import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.script.*;
import org.rsbot.script.wrappers.*;


@ScriptManifest(authors = { "Nobody, maintained by elf2345" }, category = "Combat", name = "Ogre Cage Ranger", version = 3.3, description = 
      "<html><body bgcolor = #556B2F><font color = #FFD700><center><h2>Nobody's Ogre Cage Ranger: </h2>"
    + "<h3>Maintained by elf2345.</h3><hr />"
    + "<font size =\"4\">Start in Traning Ground with Range Equipment,airs & laws.<br /><hr /><table border='0'>"
    + "<tr><td>Tele-Grab Seeds: </td><td><center><input type='checkbox' name='Seed' value='true'></font><br />"
    + "<tr><td>Special Attack: </td><td><center><input type='checkbox' name='Spec' value='true'></font><br />"
    + "<tr><td>Auto Teleport: </td><td><center><input type='checkbox' name='Tele' value='true'></font><br /></table>"
    + "<font size =\"2.5\">Please remember to bring extra runes if using teleport!<br>"
    + "<font size =\"2.5\">Please donate/send cash as a gift to: Lucas.Bastida@hotmail.com"
    + "<hr /><tr><td><em>Scripter(s) not liable for any loss!!</em></td><td><center></font><br />"
    + "</font></body></html>")

public class OgreCageRanger extends Script implements ServerMessageListener, PaintListener {

    //SpecTimer//
    long nextSpecialTime = 0;
    
    //RSTile//
    RSTile StartSpot;
    
    //Paint Times//
    public long startTime = System.currentTimeMillis();
    
    public Point mousePos;

    //Booleans// returns true if selected. 
    public boolean Spec = false;
    public boolean Seed = false;
    public boolean Tele = false;
    
    //Ints//
    int seed;
    int Ogre = 2801;
    int LawRune = 563;
    int AirRune = 556;

    public int amountleft;
    public int RanarrID = 5295;
    public int WaterID = 5321;
    public int SnapeID = 5300;
    public int KwuarmID = 5299;
    public int LimpID = 5100;
    public int StrawbID = 5323;
    public int LoopkID = 987;
    public int ToothkID = 985;
    public int ToadfID = 5296;
    public int LongBoneID = 10976;
    public int CurvedBoneID = 10977;
    public int TorstolID = 5304;
    public int specialCost = 0;
    public int lastSpecialValue = 0;
    public int statIndex = 0;
    public int paintSelection = 1;
    public int startexp;
    public int expgained;
    public int HPstartexp;
    public int HPexpgained;
    public int speed = 300;
    public int walkSpeed = 300;
    public int antiban;
    
    //Paint Colors//
    //By Epic_    //
    private final Color background = new Color(0,0,0,200),
    blue = new Color(51, 204, 255, 200),
    white = new Color(255,255,255,200),
    green = new Color(30,255,30,200);

    //Status string//
    public String status = "Loading script";

    //Do Not edit or script will fail//
    @Override
    protected int getMouseSpeed() {
        return random(7, 9);
    }
    
    public boolean onStart(Map<String, String> args) {
        StartSpot = getMyPlayer().getLocation();
        log("*******************************************************************************");
        log(" ");
        log("You are Using Ogre Cage Ranger, Created by Nobody & Maintained by elf2345!");
        log("             Please donate genrously for more future updates!");
        log("              Remember to post progress and any suggestions!");
        log("*******************************************************************************");
        
        if (args.get("Seed") != null && inventoryContains(LawRune)
                && inventoryContains(AirRune)) {
            Seed = true;
        }
        if (args.get("Spec") != null ) {
            Spec = true;
        }
        if (args.get("Tele") != null && inventoryContains(LawRune)
                && inventoryContains(AirRune)) {
            Tele = true;
        }
        
        seed = 0;
        return true;
    }

    public void onFinish() {
        Bot.getEventManager().removeListener(PaintListener.class, this);
        Bot.getEventManager().removeListener(ServerMessageListener.class, this);
    }

    //Ruski   <- i think
    @Override
    public RSInterfaceChild getInventoryInterface() {
        if (getInterface(Constants.INVENTORY_COM_X).isValid()) {
            return RSInterface.getChildInterface(Constants.INVENTORY_COM_X,
                    Constants.INVENTORY_COM_Y);
        }

        return RSInterface.getChildInterface(Constants.INVENTORY_X,
                Constants.INVENTORY_Y);
    }

    //if Using Give Credits
    public boolean CheckSeed() {
        RSTile Ranarr = getGroundItemByID(5295);
        RSTile Water = getGroundItemByID(5321);
        RSTile Snape = getGroundItemByID(5300);
        RSTile Kwuarm = getGroundItemByID(5299);
        RSTile Strawb = getGroundItemByID(5323);
        RSTile Toothk = getGroundItemByID(985);
        RSTile Loopk = getGroundItemByID(987);
        RSTile Limp = getGroundItemByID(5100);
        RSTile Toadf = getGroundItemByID(5296);
        RSTile LongBone = getGroundItemByID(10976);
        RSTile CurvedBone = getGroundItemByID(10977);
        RSTile Torstol = getGroundItemByID(5304);
        
        if (Seed) {
            if (Ranarr != null || Water != null || Snape != null
                    || Kwuarm != null || Limp != null || Strawb != null
                    || Toothk != null || Loopk != null || Limp != null
                    || Toadf != null || LongBone != null || CurvedBone != null
                    || Torstol != null) {
                return false;
            }
        }
        return true;
    }


    public boolean rightClickTile(RSTile tile, String action) {
        Point p = Calculations.tileToScreen(tile);
        clickMouse(p, false);
        wait(random(500, 800));
        return atMenu(action);
    }

    @Override
    public int loop() {
        antiBan();
        checkForLvlUpMessage();
        if (distanceTo(StartSpot) >= 2) {
            if (!walkTileMM(StartSpot)) {
                walkTo(StartSpot);
            }
        }
        //should work else  checkForLvlMessage will activate as failsafe
        if (canContinue()){
            status = "Click Continue";
            clickContinue();
        }
        if (Spec) { 
            doSpec();
        }

        if (Tele) {
            doTele();
        }
        
        if (Seed) {
            RSTile Ranarr = getGroundItemByID(RanarrID);
            RSTile Water = getGroundItemByID(WaterID);
            RSTile Snape = getGroundItemByID(SnapeID);
            RSTile Kwuarm = getGroundItemByID(KwuarmID);
            RSTile Limp = getGroundItemByID(LimpID);
            RSTile Strawb = getGroundItemByID(StrawbID);
            RSTile Toothk = getGroundItemByID(ToothkID);
            RSTile Loopk = getGroundItemByID(LoopkID);
            RSTile Toadf = getGroundItemByID(ToadfID);
            RSTile LongBone = getGroundItemByID(LongBoneID);
            RSTile CurvedBone = getGroundItemByID(CurvedBoneID);
            RSTile Torstol = getGroundItemByID(TorstolID);
            
            if (Toadf != null) {
                status = "Tele-Grab";
                openTab(Constants.TAB_MAGIC);
                    if (getCurrentTab() == Constants.TAB_MAGIC) {
                        castSpell(Constants.SPELL_TELEKINETIC_GRAB);
                        rightClickTile(Toadf, "Grab -> Toadflax");
                        seed++;
                        openTab(Constants.TAB_INVENTORY);
                        wait(random(1400, 2200));
                }
            }
            if (Ranarr != null) {
                status = "Tele-Grab";
                openTab(Constants.TAB_MAGIC);
                    if (getCurrentTab() == Constants.TAB_MAGIC) {
                        castSpell(Constants.SPELL_TELEKINETIC_GRAB);
                        rightClickTile(Ranarr, "Grab -> Ranarr");
                        seed++;
                        openTab(Constants.TAB_INVENTORY);
                        wait(random(1400, 2200));
                }
            }
            if (Loopk != null) {
                status = "Tele-Grab";
                openTab(Constants.TAB_MAGIC);
                    if (getCurrentTab() == Constants.TAB_MAGIC) {
                        castSpell(Constants.SPELL_TELEKINETIC_GRAB);
                        rightClickTile(Loopk, "Grab -> Loop");
                        seed++;
                        openTab(Constants.TAB_INVENTORY);
                        wait(random(1400, 2200));
                }
            }
            if (Toothk != null) {
                status = "Tele-Grab";
                openTab(Constants.TAB_MAGIC);
                    if (getCurrentTab() == Constants.TAB_MAGIC) {
                        castSpell(Constants.SPELL_TELEKINETIC_GRAB);
                        rightClickTile(Toothk, "Grab -> Tooth");
                        seed++;
                        openTab(Constants.TAB_INVENTORY);
                        wait(random(1400, 2200));
                }
            }
            if (Strawb != null) {
                status = "Tele-Grab";
                openTab(Constants.TAB_MAGIC);
                    if (getCurrentTab() == Constants.TAB_MAGIC) {
                        castSpell(Constants.SPELL_TELEKINETIC_GRAB);
                        rightClickTile(Strawb, "Grab -> Strawberry");
                        seed++;
                        openTab(Constants.TAB_INVENTORY);
                        wait(random(1400, 2200));
                }
            }
            if (Water != null) {
                status = "Tele-Grab";
                openTab(Constants.TAB_MAGIC);
                    if (getCurrentTab() == Constants.TAB_MAGIC) {
                        castSpell(Constants.SPELL_TELEKINETIC_GRAB);
                        rightClickTile(Water, "Grab -> Watermelon");
                        seed++;
                        openTab(Constants.TAB_INVENTORY);
                        wait(random(1400, 2200));
                }
            }
            if (Snape != null) {
                status = "Tele-Grab";
                openTab(Constants.TAB_MAGIC);
                    if (getCurrentTab() == Constants.TAB_MAGIC) {
                        castSpell(Constants.SPELL_TELEKINETIC_GRAB);
                        rightClickTile(Snape, "Grab -> Snapdragon");
                        seed++;
                        openTab(Constants.TAB_INVENTORY);
                        wait(random(1400, 2200));
                }
            }
            if (Kwuarm != null) {
                status = "Tele-Grab";
                openTab(Constants.TAB_MAGIC);
                    if (getCurrentTab() == Constants.TAB_MAGIC) {
                        castSpell(Constants.SPELL_TELEKINETIC_GRAB);
                        rightClickTile(Kwuarm, "Grab -> Kwuarm");
                        seed++;
                        openTab(Constants.TAB_INVENTORY);
                        wait(random(1400, 2200));
                }
            }
            if (Limp != null) {
                status = "Tele-Grab";
                openTab(Constants.TAB_MAGIC);
                    if (getCurrentTab() == Constants.TAB_MAGIC) {
                        castSpell(Constants.SPELL_TELEKINETIC_GRAB);
                        rightClickTile(Limp, "Grab -> Limp");
                        seed++;
                        openTab(Constants.TAB_INVENTORY);
                        wait(random(1400, 2200));
                }
            }
            if (LongBone != null) {
                status = "Tele-Grab";
                openTab(Constants.TAB_MAGIC);
                    if (getCurrentTab() == Constants.TAB_MAGIC) {
                        castSpell(Constants.SPELL_TELEKINETIC_GRAB);
                        rightClickTile(LongBone, "Grab -> Long");
                        seed++;
                        openTab(Constants.TAB_INVENTORY);
                        wait(random(1400, 2200));
                }
            }
            if (CurvedBone != null) {
                status = "Tele-Grab";
                openTab(Constants.TAB_MAGIC);
                    if (getCurrentTab() == Constants.TAB_MAGIC) {
                        castSpell(Constants.SPELL_TELEKINETIC_GRAB);
                        rightClickTile(CurvedBone, "Grab -> Curved");
                        seed++;
                        openTab(Constants.TAB_INVENTORY);
                        wait(random(1400, 2200));
                }
            }
            if (Torstol != null) {
                status = "Tele-Grab";
                openTab(Constants.TAB_MAGIC);
                    if (getCurrentTab() == Constants.TAB_MAGIC) {
                        castSpell(Constants.SPELL_TELEKINETIC_GRAB);
                        rightClickTile(Torstol, "Grab -> Torstol");
                        seed++;
                        openTab(Constants.TAB_INVENTORY);
                        wait(random(1400, 2200));
                }
            }
        }
        if (CheckSeed() && getMyPlayer().getInteracting() == null
                && getNearestFreeNPCByID(Ogre) != null) {
            status = "Fighting...";
            clickNPC(getNearestFreeNPCByID(Ogre), "Attack");
        }
        return 800;
    }

    //needs to be updated since it causes mayor freeze/lagg
    public void serverMessageRecieved(ServerMessageEvent e) {
        String message = e.getMessage();
        if (message.contains("There is no ammo left in your quiver.")) {
            log("No Arrows!!");
            status = "Ending...";
            stopScript();
        }
        if (message.contains("That was your last one!")) {
            log("No knives!!");
            status = "Ending...";
            stopScript();
        }
    }

    public boolean activateCondition() {
        if (getMyPlayer().isMoving()) {
            return random(1, walkSpeed) == 1;
        } else {
            return random(1, speed) == 1;
        }
    }
    
    //If using Give Credits
    public boolean listContainsString(List<String> list, String string) {
        try {
            int a;
            for (a = list.size() - 1; a-- >= 0;) {
                if (list.get(a).contains(string)) {
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    //If Using Give Credits
    private boolean clickNPC(final RSNPC npc, final String action) {
        final RSTile tile = npc.getLocation();
        tile.randomizeTile(1, 1);
        try {
            final int hoverRand = random(8, 13);
            for (int i = 0; i < hoverRand; i++) {
                final Point screenLoc = npc.getScreenLocation();
                if (!pointOnScreen(screenLoc)) {
                    setCameraRotation(getCameraAngle() + random(-35, 150));
                    return true;
                }

                moveMouse(screenLoc, 15, 15);

                final List<String> menuItems = getMenuItems();
                if (menuItems.isEmpty() || menuItems.size() <= 1) {
                    continue;
                }
                if (menuItems.get(0).toLowerCase().contains(
                        npc.getName().toLowerCase())
                        && getMyPlayer().getInteracting() == null) {
                    clickMouse(true);
                    return true;
                } else {
                    for (int a = 1; a < menuItems.size(); a++) {
                        if (menuItems.get(a).toLowerCase().contains(
                                npc.getName().toLowerCase())
                                && getMyPlayer().getInteracting() == null) {
                            clickMouse(false);
                            return atMenu(action);
                        }
                    }
                }
            }

        } catch (final Exception e) {
            log.warning("ClickNPC error: " + e);
            return false;
        }
        return false;
    }    
    


    //If Using Give Credits
    public void antiBan(){
        antiban = random(0, 100);
        if(antiban == 20) {
            setCameraRotation(random(1,360));
        }  
        if(antiban == 3) {
            moveMouseRandomly(20);
        }
        if(antiban == 28) {
            setCameraRotation(random(1, 100));
        }
        if(antiban == 10) {
            moveMouseRandomly(750);
        }
        if(antiban == 52) {
            setCameraRotation(random(1,176));
            moveMouseRandomly(750);
        }
        if(antiban == 63) {
            openTab(TAB_STATS);
            moveMouse(random(553,603),random(327,355));
            wait(random(100,200));
        }
        if(antiban == 70) {
            openTab(TAB_ATTACK);
            wait(random(100,200));
        }
        if(antiban == 35) {
            openTab(TAB_MAGIC);
            wait(random(100,200));
        }
        if(antiban == 90) {
            openTab(TAB_STATS);
            moveMouse(random(607,658),random(232,257));
            wait(random(100,200));
        }
    }
    
    public int getSpec() {
        return getSetting(300);
    }
    
    public boolean specEnabled() {
        return (getSetting(301)) == 1;
    }
    
    public void doSpec() {
        if (getSpec() >= 550 && !specEnabled() ) {
            openTab(TAB_ATTACK);
            clickMouse(645 + random(0, 4), 425 + random(0, 4), true);
        }
        return;
    }
    
    public void doTele() {
        if (skills.getCurrentSkillLevel(3) <= 20) {
                openTab(Constants.TAB_MAGIC);
                if (getCurrentTab() == Constants.TAB_MAGIC) {
                        if (skills.getCurrentSkillLevel (6) >= 45) {
                        castSpell(Constants.SPELL_CAMELOT_TELEPORT);
                        log("We are in cage and about to die!! - Emergency teleporting!!");
                        wait(random(10000,12000));
                        logout();
                        stopScript();
                                            }
                                        }
                                }
    return;
                }
    
    //Credits to Epic_ tooken from Myr Seagull Killer
    public void onRepaint(Graphics g) {
        if (isLoggedIn()) {    

      int expgained = 0;
      int HPexpgained = 0;


        if ( startexp == 0) {
          startexp = skills.getCurrentSkillExp(STAT_RANGE);
          statIndex = STAT_RANGE;
        }
        expgained = skills.getCurrentSkillExp(STAT_RANGE) - startexp;

      if ( HPstartexp == 0) {
        HPstartexp = skills.getCurrentSkillExp(STAT_HITPOINTS);
      }
      HPexpgained = skills.getCurrentSkillExp(STAT_HITPOINTS) - HPstartexp;

      long millis = System.currentTimeMillis() - startTime;
            long hours = millis / (1000 * 60 * 60);
            millis -= hours * (1000 * 60 * 60);
            long minutes = millis / (1000 * 60);
            millis -= minutes * (1000 * 60);
            long seconds = millis / 1000;



      long totalSeconds = ((System.currentTimeMillis() - startTime) / 1000);

      int EXPGained = skills.getCurrentSkillExp(statIndex) - startexp;
      long EXPPerHour;
      if (totalSeconds == 0) {
        EXPPerHour = 0;
      } else {
        EXPPerHour = (EXPGained * 3600) / totalSeconds;
      }

      long secondsToLvl;

      if (EXPPerHour == 0) {
        secondsToLvl = 0;
      } else {
        secondsToLvl = (skills.getXPToNextLevel(statIndex) * 3600) / EXPPerHour;
      }
      ;
      long hoursToLvl = secondsToLvl / (60 * 60);
      secondsToLvl -= hoursToLvl * (60 * 60);
      long minutesToLvl = secondsToLvl / (60);
      secondsToLvl -= minutesToLvl * (60);



      Mouse m = Bot.getClient().getMouse();
      Point p = new Point(m.x,m.y);

      if (isWithinBounds(p,375,235,70,20)) {
        paintSelection = 0;
      }
      if (isWithinBounds(p,445,235,70,20)) {
        paintSelection = 1;
      }

      g.setColor(background);
      g.fillRoundRect(375,235,140,20,5,5);
      g.fillRoundRect(375,260,140,85,5,5);

      g.setFont(new Font("sans serif", Font.PLAIN, 12));
      g.setColor(white);
      g.drawString("EXP", 400, 250);
      g.drawString("INFO", 470, 250);
      g.fillRect(445,235,1,20);


      g.setFont(new Font("sans serif", Font.PLAIN, 12));
      int y = 260;
      if (paintSelection == 0) {
        g.setColor(blue);
        g.drawString("Combat EXP: " + expgained, 380, y += 14);
        g.drawString("Hitpoints EXP: " + HPexpgained, 380, y += 14);
        g.drawString("EXP Per Hour: " + EXPPerHour, 380, y += 14);
        g.drawString("Level in: " + hoursToLvl + ":" + minutesToLvl + ":" + secondsToLvl, 380, y += 14);

        g.setColor(white);
        g.drawString("" + skills.getPercentToNextLevel(statIndex)+"%", 490, y += 20);
        g.setColor(green);
        g.fillRoundRect(380, y -= 10, skills.getPercentToNextLevel(statIndex), 10, 5, 10);
      }

      if (paintSelection == 1) {
        g.setColor(white);
        g.drawString("Ogre Cage Ranger", 380, y += 14);
        g.setColor(blue);
        g.drawString("Seeds: " + seed, 380, y += 14);
        g.drawString("Time Running: " + hours + ":" + minutes + ":" + seconds , 380, y += 14);
        g.drawString("Status: " + status, 380, y += 14);
        g.setColor(white);
        g.drawString("Created by Nobody", 380, y += 20);
      }
        }
    }  

    public boolean isWithinBounds(Point p, int x, int y, int w, int h) {
        if (p.x > x && p.x < x+w && p.y > y && p.y < y+h) {
          return true;
        } else {
          return false;
        }
      }
    
    //credits to DDM
    public void checkForLvlUpMessage() {
        if(RSInterface.getInterface(INTERFACE_LEVELUP).isValid()) {
            wait(random(800, 2000)); //reaction time
            atInterface(INTERFACE_LEVELUP, 3);
            wait(random(2000, 4000));
        }
    }
}