import java.awt.*;
import java.util.*;
import java.io.*;


import org.rsbot.script.*;
import org.rsbot.bot.*;
import org.rsbot.script.wrappers.*;
import org.rsbot.event.listeners.*;
import org.rsbot.script.ScriptManifest;

@ScriptManifest(authors = {"Tekk & B-Warrior"}, category = "Combat", name = "BTekksCrabs", version = 2.8, description = "<i><b>BTekksCrabs v2.8</b> By Tekk & B-Warrior</i><br /><b>Start at the eastern crabs. </b>")
public class BTekksCrabs extends Script implements PaintListener {

    RSTile[] northToSouth = new RSTile[]{new RSTile(2707, 3717),
            new RSTile(2708, 3708), new RSTile(2706, 3697),
            new RSTile(2705, 3690), new RSTile(2706, 3685)};
    RSTile[] southToNorth = reversePath(northToSouth);
    RSTile[] westTocenter = new RSTile[]{new RSTile(2703, 3718),
            new RSTile(2701, 3723), new RSTile(2702, 3721),
            new RSTile(2708, 3719), new RSTile(2701, 3729),
            new RSTile(2707, 3725), new RSTile(2703, 3721)};
    RSTile[] TeleSpotToBank = new RSTile[]{new RSTile(2749, 3477),
            new RSTile(2739, 3481), new RSTile(2728, 3485),
            new RSTile(2726, 3491)};
    RSTile[] BankToCrabs = new RSTile[]{new RSTile(2727, 3484),
            new RSTile(2727, 3484), new RSTile(2735, 3492),
            new RSTile(2741, 3502), new RSTile(2741, 3511),
            new RSTile(2741, 3525), new RSTile(2738, 3537),
            new RSTile(2728, 3543), new RSTile(2720, 3544),
            new RSTile(2711, 3544), new RSTile(2702, 3543),
            new RSTile(2694, 3545), new RSTile(2685, 3546),
            new RSTile(2674, 3551), new RSTile(2668, 3558),
            new RSTile(2660, 3562), new RSTile(2657, 3572),
            new RSTile(2654, 3581), new RSTile(2654, 3589),
            new RSTile(2654, 3602), new RSTile(2663, 3615),
            new RSTile(2667, 3624), new RSTile(2681, 3629),
            new RSTile(2685, 3630), new RSTile(2692, 3631),
            new RSTile(2704, 3636), new RSTile(2706, 3646),
            new RSTile(2707, 3655), new RSTile(2706, 3662),
            new RSTile(2705, 3673), new RSTile(2706, 3679)};
            

    int expStart;
    int hitpointsExpStart;
    int statBeingTrained;
    int status;
    int test;
    public long starttime = System.currentTimeMillis();
    public long millis;
    public long seconds;
    public long minutes;
    public long hours;
    public long Bseconds;
    public long Bminutes;
    public long Bhours;
    private long GainedExp;
    private long nextBreakTime;
    private long BreakBtw;
    private int BreakBtwMin;
    private int BreakBtwMax;
    private long BreakDur;
    private int BreakDurMin;
    private int BreakDurMax;
    private long BreakInMili;
    private long BreakIsbussy;
    private long randMin;
    private int randomWTC;
    private int bankFoodID;
    private int bankTab;
    private int bankCurrentTab;
    private int maxEnergy;
    private int MainWeapon;
    private int SpecWeapon;
    private int precentSpecial;
    private int failcount;

    private gui gui;

    public boolean startScript = false;
    public boolean useantiban = false;
    public boolean eating = false;
    public boolean pickingupcharms = false;
    public boolean powertraining = false;
    public boolean pickingupseaweed = false;
    public boolean takingbreaks = false;
    public boolean isTakingBreak = false;
    public boolean optBanking = false;
    public boolean isBanking = false;
    public boolean isUsingRandomHP = false;
    public boolean wannaStore = false;
    public boolean allTabs = false;
    public boolean useAllFood = false;
    public boolean useOwnEnergy = false;
    public boolean justHaveBeenFighting = false;
    public boolean wantToAttackCrabs = false;
    public boolean wantToRNDAttackCrabs = false;
    public boolean usingSpec = false;
    public boolean SwitchWeapon = false;
    public boolean useRandomSpec = false;

    private int pickupArrowID;
    private String pickupArrowName;
    public final int[] charms = {12158, 12159, 12160, 12163};
    public final int[] BankNPCsID = {495, 494};
    public final int seaweed = 402;
    public final int cammytab = 8010;
    public final int BankBoothID = 25808;
    public static final int[] foodID = {1161, 1965, 1969, 1967, 1895, 1893,
            1891, 1971, 4293, 2142, 4291, 2140, 3228, 9980, 7223, 6297, 6293,
            6295, 6299, 7521, 9988, 7228, 2878, 7568, 2343, 1861, 13433, 315,
            325, 319, 3144, 347, 355, 333, 339, 351, 329, 3381, 361, 10136,
            5003, 379, 365, 373, 7946, 385, 397, 391, 3369, 3371, 3373, 2309,
            2325, 2333, 2327, 2331, 2323, 2335, 7178, 7180, 7188, 7190, 7198,
            7200, 7208, 7210, 7218, 7220, 2003, 2011, 2289, 2291, 2293, 2295,
            2297, 2299, 2301, 2303, 1891, 1893, 1895, 1897, 1899, 1901, 7072,
            7062, 7078, 7064, 7084, 7082, 7066, 7068, 1942, 6701, 6703, 7054,
            6705, 7056, 7060, 2130, 1985, 1993, 1989, 1978, 5763, 5765, 1913,
            5747, 1905, 5739, 1909, 5743, 1907, 1911, 5745, 2955, 5749, 5751,
            5753, 5755, 5757, 5759, 5761, 2084, 2034, 2048, 2036, 2217, 2213,
            2205, 2209, 2054, 2040, 2080, 2277, 2225, 2255, 2221, 2253, 2219,
            2281, 2227, 2223, 2191, 2233, 2092, 2032, 2074, 2030, 2281, 2235,
            2064, 2028, 2187, 2185, 2229};

    private int eatAtHP = random(18, 23);
    private int eatAtRandomHP;
    private int eatmin;
    private int eatmax;

    @SuppressWarnings("deprecation")
	@Override
    public boolean onStart(Map<String, String> args) {
        // Bot.getEventManager().addListener(PaintListener.class, this);
        gui = new gui();
        gui.setVisible(true);

        while (!startScript) {
            wait(10);
        }

        if (gui.chbCharms.isSelected()) {
            pickingupcharms = true;
        }

        if (gui.chbOwnEnergy.isSelected()) {
            useOwnEnergy = true;
        }

        if (gui.chbStore.isSelected()) {
            wannaStore = true;
        }

        if (gui.chbAllTabs.isSelected()) {
            allTabs = true;
        }

        if (gui.chbRandomEating.isSelected()) {
            isUsingRandomHP = true;
        }

        if (gui.chbBank.isSelected()) {
            optBanking = true;
        }

        if (gui.chbBreaks.isSelected()) {
            takingbreaks = true;
        }

        if (gui.chbSeaweed.isSelected()) {
            pickingupseaweed = true;
        }

        if (gui.chbPowerTraining.isSelected()) {
            powertraining = true;
        }

        if (gui.chbAntiban.isSelected()) {
            useantiban = true;
        }

        if (gui.chbEat.isSelected()) {
            eating = true;
        }

        if (gui.chbUseSpec.isSelected()) {
            usingSpec = true;
            if (!gui.tfMinSpec.getText().equals("")) {
            try {
                precentSpecial = Integer.parseInt(gui.tfMinSpec.getText());
                precentSpecial = precentSpecial * 10;
            } catch (NumberFormatException e) {
                log("Nummers only");
                stopAllScripts();
            }
            }

            if (gui.chbUseRandomSpec.isSelected()) {
                useRandomSpec = true;
            }
            if (gui.chbSwitchWeapon.isSelected()) {
                SwitchWeapon = true;
                   if (!gui.tfMainWeapon.getText().equals("")) {
                   try {
                              MainWeapon = Integer.parseInt(gui.tfMainWeapon.getText());
                     } catch (NumberFormatException e) {
                              log("Nummers only");
                              stopAllScripts();
                     }
                   }
                if (!gui.tfSpecWeapon.getText().equals("")) {
                   try {
                              SpecWeapon = Integer.parseInt(gui.tfSpecWeapon.getText());
                     } catch (NumberFormatException e) {
                              log("Nummers only");
                              stopAllScripts();
                     }
                   }
            }


        }
        if (!gui.tfEatMin.getText().equals("")) {
            try {
                eatmin = Integer.parseInt(gui.tfEatMin.getText());
            } catch (NumberFormatException e) {
                log("Nummers only");
                stopAllScripts();
            }
        }

        if (!gui.tfEatMax.getText().equals("")) {
            try {
                eatmax = Integer.parseInt(gui.tfEatMax.getText());
            } catch (NumberFormatException e) {
                log("Nummers only");
                stopAllScripts();
            }
        }

        if (!gui.tfBreakbtwMin.getText().equals("")) {
            try {
                BreakBtwMin = Integer.parseInt(gui.tfBreakbtwMin.getText());
            } catch (NumberFormatException e) {
                log("Nummers only");
                stopAllScripts();
            }
        }

        if (!gui.tfBreakforMin.getText().equals("")) {
            try {
                BreakDurMin = Integer.parseInt(gui.tfBreakforMin.getText());
            } catch (NumberFormatException e) {
                log("Nummers only");
                stopAllScripts();
            }
        }

        if (!gui.tfBreakforMax.getText().equals("")) {
            try {
                BreakDurMax = Integer.parseInt(gui.tfBreakforMin.getText());
            } catch (NumberFormatException e) {
                log("Nummers only");
                stopAllScripts();
            }
        }

        if (!gui.tfBreakbtwMax.getText().equals("")) {
            try {
                BreakBtwMax = Integer.parseInt(gui.tfBreakbtwMax.getText());
            } catch (NumberFormatException e) {
                log("Nummers only");
                stopAllScripts();
            }
        }

        if (!gui.tfBreakbtwMax.getText().equals("")) {
            try {
                BreakBtwMax = Integer.parseInt(gui.tfBreakbtwMax.getText());
            } catch (NumberFormatException e) {
                log("Nummers only");
                stopAllScripts();
            }
        }

        if (!gui.tfArrowID.getText().equals("")) {
            try {
                pickupArrowID = Integer.parseInt(gui.tfArrowID.getText());
            } catch (NumberFormatException e) {
                log("Enter arrow pickup item as an ID, not name!");
                stopAllScripts();
            }
        }

        if (!gui.tfArrowName.getText().equals("")) {
            pickupArrowName = gui.tfArrowName.getText();
        }

        if (!gui.tfEatAt.getText().equals("")) {
            try {
                eatAtHP = Integer.parseInt(gui.tfEatAt.getText());
            } catch (NumberFormatException e) {
                log("You entered the Eat At HP wrong.");
                stopAllScripts();
            }
        }

        if (optBanking) {
            if (!gui.tfBankingFoodID.getText().equals("")) {
                try {
                    bankFoodID = Integer
                            .parseInt(gui.tfBankingFoodID.getText());
                } catch (NumberFormatException e) {
                    log("You entered the foodid wrong.");
                    stopAllScripts();
                }

            } else {
                log("You need to fill in a FoodID if you are useing banking option");
                stopAllScripts();
            }

            if (!gui.tfBankTab.getText().equals("")) {
                try {
                    bankTab = Integer.parseInt(gui.tfBankTab.getText());
                } catch (NumberFormatException e) {
                    log("You entered the Tab wrong.");
                    stopAllScripts();
                }

            } else {
                log("You need to fill in a Tab if you are useing banking option");
                stopAllScripts();
            }
        }

        if (useOwnEnergy) {
            if (!gui.tfMaxEnergy.getText().equals("")) {
                try {
                    maxEnergy = Integer.parseInt(gui.tfMaxEnergy.getText());
                } catch (NumberFormatException e) {
                    log("You did the energy wrong wrong.");
                    stopAllScripts();
                }

            } else {
                log("You need to fill in the energy if you are useing Own Energy option");
                stopAllScripts();
            }

            if (!gui.tfMinEnergy.getText().equals("")) {
                try {
                    Integer.parseInt(gui.tfMinEnergy.getText());
                } catch (NumberFormatException e) {
                    log("You did the energy wrong wrong.");
                    stopAllScripts();
                }

            } else {
                log("You need to fill in the energy if you are useing Own Energy option");
                stopAllScripts();
            }

        } else {
            maxEnergy = 25;
        }

        if (gui.rbRanged.isSelected()) {
            statBeingTrained = Constants.STAT_RANGE;
        } else if (gui.rbAttack.isSelected()) {
            statBeingTrained = Constants.STAT_ATTACK;
        } else if (gui.rbStrength.isSelected()) {
            statBeingTrained = Constants.STAT_STRENGTH;
        } else if (gui.rbDefence.isSelected()) {
            statBeingTrained = Constants.STAT_DEFENSE;
        }
        if (gui.rabAttCrabs.isSelected()) {
           wantToAttackCrabs = true;
        } else if (gui.rabRandomAttack.isSelected()) {
            wantToRNDAttackCrabs = true;
        } 


        while (!isLoggedIn()) {
            wait(10);
        }
        random(6000, 8000);

        if (takingbreaks) {
            BreakBtw = random(BreakBtwMin, BreakBtwMax);
            randMin = random(1000, 60000);
            BreakBtw = BreakBtw * 1000 * 60;
            nextBreakTime = BreakBtw + randMin + System.currentTimeMillis()
                    - starttime;

        }

        if (isUsingRandomHP) {
            eatAtRandomHP = random(eatmin, eatmax);
        } else {
            eatAtRandomHP = eatAtHP;
        }

        expStart = skills.getCurrentSkillExp(statBeingTrained);
        hitpointsExpStart = skills.getCurrentSkillExp(Constants.STAT_HITPOINTS);

        if (!isAutoRetaliationOn()) {
            toggleAutoRetaliation();
        }

        return true;
    }
    
    @Override
    public void onFinish() {
        Bot.getEventManager().removeListener(PaintListener.class, this);
    }

    /*
      * ##########################################################################
      * Loop
      * #####################################################################
      * #####
      */
    @SuppressWarnings("deprecation")
	public int loop() {
        randomWTC = random(0, 6);

        if (needToTakeABreak()) {
            gonnatakebreak();
        }

        if (isTakingBreak) {
            BreakIsbussy = nextBreakTime
                    - (System.currentTimeMillis() - starttime);
            while (BreakIsbussy > 0) {
                BreakIsbussy = nextBreakTime
                        - (System.currentTimeMillis() - starttime);
                if (isLoggedIn()) {
                    logout();
                }
                wait(random(500, 700));
            }
        }

        if (!isTakingBreak && isLoggedIn()) {
            if (isFightingCrab()) {
                if (eating) {
                    eatFood();
                }
                CheckForSpec();
                if (useantiban) {
                    antiban();
                }
                justHaveBeenFighting = true;
                return random(20, 40);
            }
            if (walkingToCrab()) {
                return random(500, 700);
            }
            if (needToWalkSouth()) {
                walkSouth();
                return random(200, 400);
            }
            if (needToWalkNorth()) {
                walkNorth();
                return random(200, 400);
            }
            if (!powertraining) {
                if (needToPickUpItems()) {
                    pickUpItems();
                    return random(50, 70);
                }
            }
            if (eating) {
                eatFood();
            }
            if (isLoggedIn() && !inCrabArea() && !isBanking) {
                log("You are not in the Crab Area anymore");
                stopAllScripts();
            }
            // we're not doing anything
            findAndAttackCrabs();
        }

        return random(20, 40);

    }

    private int antiban() {
        final int random = random(1, 700);
        final Point rndPoint = new Point(random(580, 704), random(250, 442));
        final Point strPoint = new Point(random(565, 601), random(268, 290));
        final Point attPoint = new Point(random(564, 601), random(240, 256));
        final Point defPoint = new Point(random(564, 601), random(300, 319));
        final Point ranPoint = new Point(random(564, 601), random(334, 351));
        final Point cbPoint = new Point(random(564, 601), random(240, 351));
        final Point hpPoint = new Point(random(612, 654), random(236, 253));

        if (random == 1) {
            setCameraRotation(random(1, 60));
        }
        if (random == 233) {
            setCameraRotation(random(1, 400));
        }
        if (random == 23) {
            setCameraAltitude(true);
        }
        if (random == 230) {
            setCameraAltitude(true);
        }
        if (random == 320) {
            setCameraAltitude(true);
        }
        if (random == 200) {
            setCameraAltitude(true);
        }
        if (random == 140) {
            Point randomMouse;
            final int rndMovement = random(1, 5);
            for (int a = 0; a < rndMovement; a++) {
                randomMouse = new Point(random(15, 730), random(15, 465));
                moveMouse(randomMouse);
                wait(random(50, 800));
            }
            return random(130, 810);

        }
        if (random == 456) {
            int currentAngle = getCameraAngle();
            Bot.getClient().getCamPosZ();
            switch (random(0, 1)) {
                case 0:
                    setCameraRotation(currentAngle + random(0, 230));
                    return random(434, 578);
                case 1:
                    setCameraRotation(currentAngle - random(0, 230));
                    return random(434, 678);
            }
        }
        if (random == 360) {
            int currentAngle = getCameraAngle();
            Bot.getClient().getCamPosZ();
            switch (random(0, 1)) {
                case 0:
                    setCameraRotation(currentAngle + random(0, 630));
                    setCameraAltitude(random(20, 80));
                    return random(434, 678);
                case 1:
                    setCameraRotation(currentAngle - random(0, 630));
                    setCameraAltitude(random(20, 80));
                    return random(434, 678);
            }
        }
        if (random == 388) {
            final int skillrandom = random(1, 5);

            if (skillrandom == 1 && gui.rbRanged.isSelected()) {
                if (getCurrentTab() != Constants.TAB_STATS) {
                    openTab(Constants.TAB_STATS);
                    wait(random(310, 610));
                }
                moveMouse(ranPoint);
                wait(random(1500, 4100));
                return random(400, 2300);
            }
            if (skillrandom == 2 && gui.rbAttack.isSelected()) {
                if (getCurrentTab() != Constants.TAB_STATS) {
                    openTab(Constants.TAB_STATS);
                    wait(random(310, 610));
                }
                moveMouse(attPoint);
                wait(random(1500, 4100));
                return random(400, 2300);

            }
            if (skillrandom == 3 && gui.rbDefence.isSelected()) {
                if (getCurrentTab() != Constants.TAB_STATS) {
                    openTab(Constants.TAB_STATS);
                    wait(random(310, 610));
                }
                moveMouse(defPoint);
                wait(random(1500, 4100));
                return random(400, 2300);
            }
            if (skillrandom == 4 && gui.rbStrength.isSelected()) {
                if (getCurrentTab() != Constants.TAB_STATS) {
                    openTab(Constants.TAB_STATS);
                    wait(random(310, 610));
                }

                moveMouse(strPoint);
                wait(random(1500, 4100));
                return random(400, 2300);
            }
            if (skillrandom == 5) {
                final int rndskill = random(0, 5);
                if (rndskill == 2) {
                    final int rndskill2 = random(0, 10);
                    if (rndskill2 == 5) {
                        if (getCurrentTab() != Constants.TAB_STATS) {
                            openTab(Constants.TAB_STATS);
                            wait(random(310, 610));
                        }
                        moveMouse(rndPoint);
                        wait(random(1500, 4100));
                        return random(400, 2300);
                    }
                }
                if (rndskill == 5) {
                    if (getCurrentTab() != Constants.TAB_STATS) {
                        openTab(Constants.TAB_STATS);
                        wait(random(310, 610));
                    }
                    moveMouse(cbPoint);
                    wait(random(1500, 4100));
                    return random(400, 2300);
                }
                if (rndskill == 3) {
                    if (getCurrentTab() != Constants.TAB_STATS) {
                        openTab(Constants.TAB_STATS);
                        wait(random(310, 610));
                    }
                    moveMouse(hpPoint);
                    wait(random(1500, 4100));
                    return random(400, 2300);
                }
            }

        }

        return -1;
    }

    private boolean needToTakeABreak() {
        return takingbreaks
                && (System.currentTimeMillis() - starttime) > nextBreakTime;
    }

    private boolean needToPickUpItems() {
        RSItemTile arrowToPickUp = getGroundItemByID(3, pickupArrowID);
        if (getMyPlayer().getLocation().getY() >= 3710) {
            if (arrowToPickUp != null && !isInventoryFull()) {
                status = 3;
                return true;
            }else{
             if(isInventoryFull()&& inventoryContains(pickupArrowID) ) {
                status = 3;
                return true;
             }
            }
        }
        if (pickingupcharms) {
            RSItemTile charmToPickUp = getGroundItemByID(3, charms);
            if (getMyPlayer().getLocation().getY() >= 3710) {
                if (charmToPickUp != null && !isInventoryFull()) {
                    status = 3;
                    return true;
                }
            }
        }
        if (pickingupseaweed) {
            RSItemTile seaweedToPickUp = getGroundItemByID(3, seaweed);
            if (getMyPlayer().getLocation().getY() >= 3710) {
                if (seaweedToPickUp != null && !isInventoryFull()) {
                    status = 3;
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isAutoRetaliationOn() {
        return getSetting(172) == 0;
    }


    public int getSpec() {
	return getSetting(300);
    }

    public boolean clickedSpec() {
	return (getSetting(301)) == 1;
    }

    public void CheckForSpec() {
	if (getSpec() >= precentSpecial && !clickedSpec() && usingSpec == true) {
            if(!SwitchWeapon){
		openTab(TAB_ATTACK);
              while(getSpec() >= precentSpecial){
                   atInterface(884,4);
                   failcount = 0;
                   while(clickedSpec()){
                      if(failcount > 200){
                          return;
                      }
                       wait(10);
                       failcount++;
                   }
                   wait(random(500,600));
                }
              wait(random(300,500));
            }else{
                openTab(TAB_INVENTORY);
                atInventoryItem(SpecWeapon, "Wield");
                while(getEquipmentCount(SpecWeapon) == 0){
                    wait(10);
                }
                wait(random(400,600));
                openTab(TAB_ATTACK);
                while(getSpec() >= precentSpecial){
                   atInterface(884,4);
                   failcount = 0;
                   while(clickedSpec()){
                      if(failcount > 200){
                          return;
                      }
                       wait(10);
                       failcount++;
                   }
                }
              wait(random(400,600));
                atInventoryItem(MainWeapon, "Wield");
                while(getEquipmentCount(MainWeapon) == 0){
                    wait(10);
                }
            }
	}
            return;
    }

    private boolean justwokeacrab() {
        if (getMyPlayer().isMoving() && getNearestNPCByID(1265, 1267) != null
                && distanceTo(getNearestNPCByID(1265, 1267)) <= 2) {
            wait(random(600, 900));
            return getNearestNPCByID(1265, 1267) != null
                    && distanceTo(getNearestNPCByID(1265, 1267)) <= 2;
        }
        return false;
    }

    public void openBankScreen() {
        status = 7;
        RSObject bankBooth = getNearestObjectByID(BankBoothID);
        RSTile BankBoothTile = bankBooth.getLocation();

        if (tileOnScreen(BankBoothTile)) {
            if (!bank.isOpen()) {
                atObject(bankBooth, "uickly");
                wait(random(1500, 2000));
            }
            if (!bank.isOpen()) {
                openBankScreen();
            }
        } else {
            walkTileMM(BankBoothTile, 2, 2);
            while (getMyPlayer().isMoving()) {
                wait(10);
            }
            openBankScreen();
        }

    }

      @SuppressWarnings("unchecked")
	public void save(Map<String, String> args) {
            try {
            FileWriter fstream = new FileWriter("BTekksCrabs.ini" ,false);
            BufferedWriter out = new BufferedWriter(fstream);
            Iterator it = args.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry)it.next();
                out.write(pairs.getKey() + " = " + pairs.getValue()+"\n");
            }
            out.close();
            }catch (Exception e){//Catch exception if any
                System.err.println("Error: " + e.getMessage());
            }
        }


    @SuppressWarnings("deprecation")
	public void withdrawnFood() {

        if (!inventoryContains(cammytab) && allTabs) {
            log("stopping script just used last tab");
            stopAllScripts();
        }

        if (!bank.isOpen()) {
            openBankScreen();
        }

        RSInterfaceComponent bankFood = bank.getItemByID(bankFoodID);
        RSInterfaceComponent bankCammyID = bank.getItemByID(cammytab);
        if (bankFood == null) {
            log("out of food stopping script");
            stopAllScripts();
        }
        if (bankCammyID == null && !allTabs) {
            log("out of tabs stopping script");
            stopAllScripts();
        }
        bank.setWithdrawModeToItem();
        bankCurrentTab = bank.getCurrentTab() + 1;
        if (wannaStore) {
            if (allTabs) {
                bank.depositAllExcept(cammytab);
                if (bankCurrentTab != bankTab) {
                    bank.openTab(bankTab);
                }
                bank.atItem(bankFoodID, "Withdraw-All");
                bank.close();
            } else {
                bank.depositAll();
                if (bankCurrentTab != bankTab) {
                    bank.openTab(bankTab);
                }
                bank.atItem(cammytab, "Withdraw-1");
                bank.atItem(bankFoodID, "Withdraw-All");
                bank.close();
            }
        } else {
            if (allTabs) {
                bank.depositAllExcept(12158, 12159, 12160, 12163, cammytab,
                        seaweed);
                if (bankCurrentTab != bankTab) {
                    bank.openTab(bankTab);
                }
                bank.atItem(bankFoodID, "Withdraw-All");
                bank.close();
            } else {
                bank.depositAllExcept(12158, 12159, 12160, 12163, seaweed);
                if (bankCurrentTab != bankTab) {
                    bank.openTab(bankTab);
                }
                bank.atItem(cammytab, "Withdraw-1");
                bank.atItem(bankFoodID, "Withdraw-All");
                bank.close();
            }
        }

    }

    public void toggleAutoRetaliation() {
        if (getCurrentTab() != Constants.TAB_ATTACK) {
            openTab(TAB_ATTACK);
        }
        clickMouse(random(579, 706), random(363, 395), true);
        wait(random(600, 800));
    }

    private boolean inCrabArea() {
        return getMyPlayer().getLocation().getY() >= 3645
                && getMyPlayer().getLocation().getY() <= 3736
                && getMyPlayer().getLocation().getX() >= 2690
                && getMyPlayer().getLocation().getX() <= 2735;

    }
    public boolean WalkSmart(final RSTile t2) {
		final Point p = tileToMinimap(t2);
		final RSTile t = new RSTile(t2.getX() + random(-1, 1), t2.getY());
		if (tileOnScreen(t)) {
			if (atTile(t, "Walk Here")) {
				while (getMyPlayer().isMoving()) {
					wait(random(200, 400));
				}
				return true;
			}
		}
		if (p.x == -1 || p.y == -1) {
			return false;
		}
		clickMouse(p, 2, 2, true);
		while (getMyPlayer().isMoving()) {
			wait(random(200, 400));
		}
		return true;
	}

    private boolean inCamelot() {
        return getMyPlayer().getLocation().getY() >= 3474
                && getMyPlayer().getLocation().getY() <= 3481
                && getMyPlayer().getLocation().getX() >= 2755
                && getMyPlayer().getLocation().getX() <= 2760;

    }

    private void gonnatakebreak() {
        if (!isTakingBreak) {
            while (getMyPlayer().isInCombat() || justwokeacrab()) {
                wait(10);
            }
            wait(random(10000, 12000));
            if (!isFightingCrab()) {
                logout();
                log("Taking Break");

                BreakDur = random(BreakDurMin, BreakDurMax);
                randMin = random(1000, 60000);
                BreakDur = BreakDur * 1000 * 60;
                nextBreakTime = BreakDur + randMin + System.currentTimeMillis()
                        - starttime;

                isTakingBreak = true;
                return;
            }
            if (isFightingCrab()) {

                walkSouth();
                logout();
                log("Taking Break");

                BreakDur = random(BreakDurMin, BreakDurMax);
                randMin = random(1000, 60000);
                BreakDur = BreakDur * 1000 * 60;
                nextBreakTime = BreakDur + randMin + System.currentTimeMillis()
                        - starttime;

                isTakingBreak = true;
            }
        } else {

            login();
            BreakBtw = random(BreakBtwMin, BreakBtwMax);
            randMin = random(1000, 60000);
            BreakBtw = BreakBtw * 1000 * 60;
            nextBreakTime = BreakBtw + randMin + System.currentTimeMillis()
                    - starttime;

            isTakingBreak = false;

        }
    }

    private void pickUpItems() {
        status = 3;
        if(isInventoryFull()&& inventoryContains(pickupArrowID) ) {
        RSItemTile arrowToPickUp = getGroundItemByID(3, pickupArrowID);
        atTile(arrowToPickUp, pickupArrowName);
        }
        if(!isInventoryFull()) {
        RSItemTile arrowToPickUp = getGroundItemByID(3, pickupArrowID);
        atTile(arrowToPickUp, pickupArrowName);
        }
        if (pickingupcharms && !isInventoryFull()) {
            RSItemTile charmToPickUp = getGroundItemByID(3, charms);
            atTile(charmToPickUp, "charm");
            wait(random(500, 800));
        }
        if (pickingupseaweed && !isInventoryFull() ) {
            RSItemTile seaweedToPickUp = getGroundItemByID(3, seaweed);
            atTile(seaweedToPickUp, "Seaweed");
            wait(random(500, 800));
        }
        wait(random(200, 400));
        if (inventoryContains(pickupArrowID))
            atInventoryItem(pickupArrowID, "Wield");
    }

    private boolean clickInventoryItem(int itemID, boolean click) { // Unknown
        // author
        if (getCurrentTab() != TAB_INVENTORY
                && !RSInterface.getInterface(INTERFACE_BANK).isValid()
                && !RSInterface.getInterface(INTERFACE_STORE).isValid()) {
            openTab(TAB_INVENTORY);
        }
        int[] items = getInventoryArray();
        int slot = -1;
        for (int i = 0; i < items.length; i++) {
            if (items[i] == itemID) {
                slot = i;
                break;
            }
        }
        if (slot == -1) {
            return false;
        }
        Point t = getInventoryItemPoint(slot);
        clickMouse(t, 5, 5, click);
        return true;
    }



    @SuppressWarnings("deprecation")
	private void eatFood() {
        int currentHP = skills.getCurrentSkillLevel(STAT_HITPOINTS);
        if (currentHP <= eatAtRandomHP) {
            if (getInventoryCount(foodID) >= 1) {
                for (int i : foodID) {
                    if (getInventoryCount(i) == 0) {
                        continue;
                    }
                    clickInventoryItem(i, true);
                }
                if (isUsingRandomHP) {
                    eatAtRandomHP = random(eatmin, eatmax);
                } else {
                    eatAtRandomHP = eatAtHP;
                }
            } else {
                if (optBanking) {
                    if (inventoryContains(cammytab))
                        atInventoryItem(cammytab, "");
                    while (!inCamelot()) {
                        wait(10);
                    }
                    setCameraAltitude(true);
                    setCompass('N');
                    isBanking = true;
                    moveMouseSlightly();
                    wait(random(500, 800));
                    walkToBank();
                    openBankScreen();
                    withdrawnFood();
                    walkToCrabs();
                    isBanking = false;
                } else {
                    log("No food and we are almost dead, stopping script.");
                    walkSouth();
                    stopAllScripts();
                }
            }
        }
    }

    public boolean clickItem(RSItem item) {
        if (getCurrentTab() != Constants.TAB_INVENTORY) {
            openTab(Constants.TAB_INVENTORY);
        }
        return atInventoryItem(item.getID(), "");
    }

    private void walkNorth() {
        status = 5;
        RSTile[] randomSTNPath = randomizePath(southToNorth, 2, 2);
        for (RSTile aRandomSTNPath : randomSTNPath) {
            while (distanceTo(aRandomSTNPath) > 5) {
                walkTileMM(aRandomSTNPath);
                moveMouseSlightly();
                wait(random(300, 600));
            }
        }
    }

    private void walkToBank() {
        status = 6;
        RSTile[] randomTTBPath = randomizePath(TeleSpotToBank, 1, 1);
        if (getEnergy() > random(maxEnergy, maxEnergy) && !isRunning()) {
            setRun(true);
        }
        for (RSTile aRandomTTBPath : randomTTBPath) {
            walkTileMM(aRandomTTBPath);
            while (distanceTo(aRandomTTBPath) > 3) {
                wait(random(300, 600));
            }
        }
    }

    private void walkToCrabs() {
        status = 8;
        RSTile[] randomBTCPath = randomizePath(BankToCrabs, 1, 1);
        if (getEnergy() > random(maxEnergy, maxEnergy) && !isRunning()) {
            setRun(true);
        }
        for (RSTile aRandomBTCPath : randomBTCPath) {
            walkTileMM(aRandomBTCPath);
            while (distanceTo(aRandomBTCPath) > 4) {
                if (!getMyPlayer().isMoving()) {
                    walkTileMM(aRandomBTCPath);
                }
                wait(random(300, 600));
            }
        }
    }

    private boolean needToWalkNorth() {
        if (getMyPlayer().getLocation().getY() < 3710) {
            status = 5;
            return true;
        }
        return false;
    }

    private void walkSouth() {
        status = 4;
        RSTile[] randomNTSPath = randomizePath(northToSouth, 2, 2);
        for (RSTile aRandomNTSPath : randomNTSPath) {
            while (distanceTo(aRandomNTSPath) > 5) {
                walkTileMM(aRandomNTSPath);
                moveMouseSlightly();
                wait(random(300, 600));
            }
        }
    }

    private boolean needToWalkSouth() {
        if (getNearestNPCByID(1266, 1268) != null) {
            if (distanceTo(getNearestNPCByID(1266, 1268)) < 2) {
                wait(random(2000, 3000));
                if (!getMyPlayer().isInCombat()) {
                    status = 4;
                    return true;
                }
            }
        }
        return false;
    }

    private void findAndAttackCrabs() {
        status = 2;
        RSNPC newCrab = getNearestFreeNPCByID(1266, 1268);
        RSTile[] randomWTCPath = randomizePath(westTocenter, 2, 2);
        if (!justHaveBeenFighting) {
            if (getNearestNPCByID(1265, 1267) != null
                    && distanceTo(getNearestNPCByID(1265, 1267)) <= 2) {
                if(wantToAttackCrabs){
                clickRSNPC(getNearestNPCByID(1265, 1267),"Attack");
                }else if(wantToRNDAttackCrabs){
                 int rndAtt = random(1,3);
                 int rndNum = random(1,4);
                 if (rndNum == rndAtt){
                    clickRSNPC(getNearestNPCByID(1265, 1267),"Attack");
                 }else{
                   wait(random(1000, 1500));
                 }
                }else{
                wait(random(1000, 1500));
                }
            }
        }
        if (newCrab != null && newCrab.getLocation().getX() > 2691
                && !isFightingCrab() && getMyPlayer().getAnimation() == -1
                && !justwokeacrab()) {
            RSTile crabLocation = newCrab.getLocation();
  
            if (getEnergy() > random(20, 25) && !isRunning()) {
                setRun(true);
            }
            walkTileMM(crabLocation);
            moveMouseSlightly();
            justHaveBeenFighting = false;
     
        }
        if (getMyPlayer().isInCombat() && getNearestNPCByID(1265, 1267) != null
                && distanceTo(getNearestNPCByID(1265, 1267)) <= 2) {
            wait(random(800, 1200));
        }
        if (newCrab != null && newCrab.getLocation().getX() < 2691
                && !isFightingCrab() && getMyPlayer().getAnimation() == -1
                && !justwokeacrab()) {
            wait(random(2000, 3000));
            if (!isFightingCrab()) {
                if (getEnergy() > random(20, 25) && !isRunning()) {
                    setRun(true);
                }
                walkTileMM(randomWTCPath[randomWTC]);
                moveMouseSlightly();
                justHaveBeenFighting = false;
            }
        }

    }

    private boolean walkingToCrab() {
        if (getMyPlayer().isMoving()) {
            status = 2;
            return true;
        }
        return false;
    }

    private boolean isFightingCrab() {
        if (!powertraining) {
            if (getMyPlayer().isInCombat()
                    && getNearestNPCByID(1265, 1267) != null
                    && distanceTo(getNearestNPCByID(1265, 1267)) <= 2) {
                status = 1;
                return true;
            }
        } else {
            if (getMyPlayer().getInteracting() != null
                    && getMyPlayer().getInteracting().getHPPercent() != 0) {
                status = 1;
                return true;
            }
        }
        return false;
    }

@SuppressWarnings("serial")
public class gui extends javax.swing.JFrame {

    /** Creates new form guipublic class gui extends javax.swing.JFrame {
 */
    public gui() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {

        jTabbedPane3 = new javax.swing.JTabbedPane();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jRadioButton1 = new javax.swing.JRadioButton();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        rbAttack = new javax.swing.JRadioButton();
        rbStrength = new javax.swing.JRadioButton();
        rbDefence = new javax.swing.JRadioButton();
        rbRanged = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        chbCharms = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        tfArrowID = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        tfArrowName = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        tfEatAt = new javax.swing.JTextField();
        chbPowerTraining = new javax.swing.JCheckBox();
        chbSeaweed = new javax.swing.JCheckBox();
        chbEat = new javax.swing.JCheckBox();
        jPanel3 = new javax.swing.JPanel();
        chbBreaks = new javax.swing.JCheckBox();
        tfBreakbtwMin = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        tfBreakbtwMax = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        tfBreakforMin = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        tfBreakforMax = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        chbAntiban = new javax.swing.JCheckBox();
        chbRandomEating = new javax.swing.JCheckBox();
        tfEatMin = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        tfEatMax = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        chbBank = new javax.swing.JCheckBox();
        chbStore = new javax.swing.JCheckBox();
        chbAllTabs = new javax.swing.JCheckBox();
        tfBankingFoodID = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        tfBankTab = new javax.swing.JTextField();
        chbOwnEnergy = new javax.swing.JCheckBox();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        tfMinEnergy = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        tfMaxEnergy = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        tfSpecWeapon = new javax.swing.JTextField();
        chbUseRandomSpec = new javax.swing.JCheckBox();
        tfMinSpec = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        chbUseSpec = new javax.swing.JCheckBox();
        chbSwitchWeapon = new javax.swing.JCheckBox();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        tfMainWeapon = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        rabDontAtt = new javax.swing.JRadioButton();
        rabAttCrabs = new javax.swing.JRadioButton();
        rabRandomAttack = new javax.swing.JRadioButton();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        btStart1 = new javax.swing.JButton();

        jRadioButton1.setText("jRadioButton1");

        jLabel22.setText("jLabel22");

        jLabel23.setText("jLabel23");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("BTekksCrabs");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        buttonGroup1.add(rbAttack);
        rbAttack.setText("Attack");

        buttonGroup1.add(rbStrength);
        rbStrength.setSelected(true);
        rbStrength.setText("Strength");

        buttonGroup1.add(rbDefence);
        rbDefence.setText("Defence");
        rbDefence.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbDefenceActionPerformed(evt);
            }
        });

        buttonGroup1.add(rbRanged);
        rbRanged.setText("Ranged");

        jLabel1.setText("Wich skill are your training ? ");

        chbCharms.setText("Want to pickup Charms ?");

        jLabel3.setText("Arrow ID:");

        jLabel4.setText("Arrow name:");

        jLabel5.setForeground(new java.awt.Color(255, 0, 0));
        jLabel5.setText("Warning! Arrow name is case sensitive");

        jLabel6.setForeground(new java.awt.Color(255, 0, 0));
        jLabel6.setText("bronze arrow is not the same as Bronze arrow");

        jLabel7.setText("Eat at:");

        chbPowerTraining.setText("Power training? (only use this if you dont want pickup items)");

        chbSeaweed.setText("Want to pickup seaweed ?");

        chbEat.setText("Eat ?");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(181, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(chbEat)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(chbSeaweed)
                            .addContainerGap())
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(chbCharms)
                                .addContainerGap())
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(chbPowerTraining)
                                    .addContainerGap())
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel1)
                                        .addComponent(rbAttack)
                                        .addComponent(rbStrength)
                                        .addComponent(rbDefence)
                                        .addComponent(rbRanged))
                                    .addGap(104, 104, 104)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel7)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(tfArrowName, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
                                            .addComponent(jLabel4)
                                            .addComponent(tfArrowID, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE)
                                            .addComponent(jLabel3))
                                        .addComponent(tfEatAt, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addContainerGap(48, Short.MAX_VALUE)))))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbAttack)
                    .addComponent(tfArrowID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbStrength, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfArrowName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rbDefence))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(rbRanged))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tfEatAt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(chbEat)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chbSeaweed)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chbCharms)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chbPowerTraining, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(85, 85, 85)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addGap(17, 17, 17))
        );

        jTabbedPane2.addTab("Settings", jPanel1);

        chbBreaks.setText("Do you wish to take breaks ?");

        tfBreakbtwMin.setText("70");

        jLabel2.setText("Take breaks between");

        jLabel8.setText("and");

        tfBreakbtwMax.setText("130");

        jLabel9.setText("minutes");

        jLabel10.setText("Take a break for");

        tfBreakforMin.setText("5");

        jLabel11.setText("-");

        tfBreakforMax.setText("15");

        jLabel12.setText("minutes");

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 18));
        jLabel13.setForeground(new java.awt.Color(255, 0, 0));
        jLabel13.setText("If using start logged in!");

        chbAntiban.setSelected(true);
        chbAntiban.setText("Use own build-in Antiban ?");

        chbRandomEating.setText("Eat at random HP");
        chbRandomEating.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chbRandomEatingActionPerformed(evt);
            }
        });

        jLabel14.setText("Eat between");

        jLabel15.setText("and");

        jLabel16.setText("HP");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chbBreaks)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel10))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(tfBreakbtwMin, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tfBreakforMin, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(jLabel8)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(tfBreakbtwMax, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel9))
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(jLabel11)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(tfBreakforMax, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel12))))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(89, 89, 89)
                        .addComponent(jLabel13))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(chbAntiban))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(chbRandomEating))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tfEatMin, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfEatMax, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel16)))
                .addContainerGap(136, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(chbBreaks)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(tfBreakbtwMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(tfBreakbtwMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfBreakforMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(tfBreakforMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(jLabel10))
                .addGap(18, 18, 18)
                .addComponent(jLabel13)
                .addGap(18, 18, 18)
                .addComponent(chbAntiban)
                .addGap(18, 18, 18)
                .addComponent(chbRandomEating)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(tfEatMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(tfEatMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addContainerGap(144, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("AntiBans", jPanel3);

        chbBank.setText("Do you want to bank for food ?");

        chbStore.setText("Do you want to deposit all ?");

        chbAllTabs.setText("Have you got your tele tabs with you ?(Camelot Teletabs)");

        jLabel17.setText("Enter food ID (The one that is withdrawn from bank):");

        jLabel18.setText("In which slot can the script find your food at ?(First slot = 1) :");

        chbOwnEnergy.setText("Use built-in random run energy");

        jLabel19.setText("This means if youre run energy is gone when may the script activate it again");

        jLabel20.setText("Between: ");

        jLabel21.setText("and");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chbOwnEnergy)
                    .addComponent(chbBank)
                    .addComponent(chbStore)
                    .addComponent(chbAllTabs)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfBankingFoodID, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfBankTab, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfMinEnergy, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfMaxEnergy, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jLabel19)))
                .addContainerGap(15, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chbBank)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chbStore)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chbAllTabs)
                .addGap(39, 39, 39)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(tfBankingFoodID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(tfBankTab, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addComponent(chbOwnEnergy)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(tfMinEnergy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21)
                    .addComponent(tfMaxEnergy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(128, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Banking", jPanel4);

        jLabel29.setText("Weapon you want to spec with:");

        chbUseRandomSpec.setText("Use random spec?");

        jLabel30.setText("Minimum % needed:");

        chbUseSpec.setText("Using special attack ?");

        chbSwitchWeapon.setText("Switch other weapon for spec ?");

        jLabel25.setFont(new java.awt.Font("Tahoma", 2, 11));
        jLabel25.setText("(ID)");

        jLabel26.setText("Main weapon :");

        jLabel27.setFont(new java.awt.Font("Tahoma", 2, 11));
        jLabel27.setText("(ID)");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jLabel29)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfSpecWeapon, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel25))
                    .addComponent(chbUseSpec)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel30)
                            .addComponent(chbUseRandomSpec))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfMinSpec, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chbSwitchWeapon)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(jLabel26)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tfMainWeapon, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel27)))
                .addContainerGap(155, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(chbUseSpec)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30)
                    .addComponent(tfMinSpec, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(chbUseRandomSpec)
                .addGap(18, 18, 18)
                .addComponent(chbSwitchWeapon)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29)
                    .addComponent(tfSpecWeapon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(tfMainWeapon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel27))
                .addContainerGap(199, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Special Attack", jPanel6);

        jLabel24.setText("Method to att crabs");

        buttonGroup2.add(rabDontAtt);
        rabDontAtt.setSelected(true);
        rabDontAtt.setText("Dont attack other peoples crabs");

        buttonGroup2.add(rabAttCrabs);
        rabAttCrabs.setText("Attack other peoples crabs");

        buttonGroup2.add(rabRandomAttack);
        rabRandomAttack.setText("Random attack other peoples crabs");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rabAttCrabs)
                            .addComponent(rabDontAtt)
                            .addComponent(jLabel24)
                            .addComponent(rabRandomAttack)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 286, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(66, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel24)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rabDontAtt)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rabAttCrabs)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rabRandomAttack)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(273, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("More Settings", jPanel5);

        jTextArea1.setColumns(20);
        jTextArea1.setEditable(false);
        jTextArea1.setRows(5);
        jTextArea1.setText("Version 1.0\n- Release\n\nVersion 1.1\n- Added Power Training\n\nVersion 1.2 \n- Added Seaweed pickup option\n- Changed looks of paint\n\nVersion 1.3\n- Made antiban better\n- Normaly it supports all types of food \n\nVersion 1.4\n- Made some methods better\n- Added Breakhandler\n\nVersion 1.5\n- Breakhandler is working now\n\nVersion 1.6\n- Added random eating option\n\nVersion 2.0\n- Added banking\n\nVersion 2.1\n- Fixed some grammar (credits for Jozhua)\n- Fixed login when not using randoms (normally)\n\nVersion 2.2\n- Imporved fast att method again(sinds some updates it getted back slower)\n\nVersion 2.3\n- Made attack method even faster\n\nVersion 2.4\n- Fixed full inventory bugg\n\nVersion 2.5\n- Added option for attack methods\n\nVersion 2.6\n- Added spec option\n\nVersion 2.8\n- Fixed Banking");
        jScrollPane1.setViewportView(jTextArea1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 392, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 329, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(46, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Info", jPanel2);

        btStart1.setText("Start Script");
        btStart1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btStart1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 417, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(layout.createSequentialGroup()
                .addGap(155, 155, 155)
                .addComponent(btStart1, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 414, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btStart1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>

    private void chbRandomEatingActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
}

    private void rbDefenceActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
}

    private void btStart1ActionPerformed(java.awt.event.ActionEvent evt) {
        startActionPerformed(evt);
    }

   public void startActionPerformed(java.awt.event.ActionEvent evt) {

        setVisible(false);
        startScript = true;

}


    // Variables declaration - do not modify
    private javax.swing.JButton btStart1;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JCheckBox chbAllTabs;
    private javax.swing.JCheckBox chbAntiban;
    private javax.swing.JCheckBox chbBank;
    private javax.swing.JCheckBox chbBreaks;
    private javax.swing.JCheckBox chbCharms;
    private javax.swing.JCheckBox chbEat;
    private javax.swing.JCheckBox chbOwnEnergy;
    private javax.swing.JCheckBox chbPowerTraining;
    private javax.swing.JCheckBox chbRandomEating;
    private javax.swing.JCheckBox chbSeaweed;
    private javax.swing.JCheckBox chbStore;
    private javax.swing.JCheckBox chbSwitchWeapon;
    private javax.swing.JCheckBox chbUseRandomSpec;
    private javax.swing.JCheckBox chbUseSpec;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    @SuppressWarnings("unused")
	private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    @SuppressWarnings("unused")
	private javax.swing.JTabbedPane jTabbedPane3;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JRadioButton rabAttCrabs;
    private javax.swing.JRadioButton rabDontAtt;
    private javax.swing.JRadioButton rabRandomAttack;
    private javax.swing.JRadioButton rbAttack;
    private javax.swing.JRadioButton rbDefence;
    private javax.swing.JRadioButton rbRanged;
    private javax.swing.JRadioButton rbStrength;
    private javax.swing.JTextField tfArrowID;
    private javax.swing.JTextField tfArrowName;
    private javax.swing.JTextField tfBankTab;
    private javax.swing.JTextField tfBankingFoodID;
    private javax.swing.JTextField tfBreakbtwMax;
    private javax.swing.JTextField tfBreakbtwMin;
    private javax.swing.JTextField tfBreakforMax;
    private javax.swing.JTextField tfBreakforMin;
    private javax.swing.JTextField tfEatAt;
    private javax.swing.JTextField tfEatMax;
    private javax.swing.JTextField tfEatMin;
    private javax.swing.JTextField tfMainWeapon;
    private javax.swing.JTextField tfMaxEnergy;
    private javax.swing.JTextField tfMinEnergy;
    private javax.swing.JTextField tfMinSpec;
    private javax.swing.JTextField tfSpecWeapon;
}

    public void onRepaint(Graphics render) {
        BreakInMili = nextBreakTime - (System.currentTimeMillis() - starttime);
        if (BreakInMili < 0) {
            BreakInMili = 0;
        }
        Bhours = BreakInMili / (1000 * 60 * 60);
        BreakInMili -= Bhours * (1000 * 60 * 60);
        Bminutes = BreakInMili / (1000 * 60);
        BreakInMili -= Bminutes * (1000 * 60);
        Bseconds = BreakInMili / 1000;

        millis = System.currentTimeMillis() - starttime;
        hours = millis / (1000 * 60 * 60);
        millis -= hours * (1000 * 60 * 60);
        minutes = millis / (1000 * 60);
        millis -= minutes * (1000 * 60);
        seconds = millis / 1000;

        Color background = new Color(0, 0, 0, 115);

        render.setColor(background);
        render.fill3DRect(10, 10, 175, 115, true);
        render.setColor(Color.white);
        render.drawString("BTekksCrabs v2.8", 15, 25);
        render.drawString("Time running: " + hours + ":" + minutes + ":"
                + seconds, 15, 40);
        render.drawString("XP gained: "
                + (skills.getCurrentSkillExp(statBeingTrained) - expStart), 15,
                55);
        render.drawString("XP to level "
                + (skills.getCurrentSkillLevel(statBeingTrained) + 1) + " : "
                + skills.getXPToNextLevel(statBeingTrained), 15, 70);
        render
                .drawString(
                        "Hitpoints XP gained: "
                                + (skills.getCurrentSkillExp(STAT_HITPOINTS) - hitpointsExpStart),
                        15, 85);
        if (takingbreaks) {
            if (isTakingBreak) {
                render.drawString("Time untill Break is ending: " + Bhours
                        + ":" + Bminutes + ":" + Bseconds, 15, 115);
            } else {
                render.drawString("Time till Break: " + Bhours + ":" + Bminutes
                        + ":" + Bseconds, 15, 115);
            }
        }
        GainedExp = (skills.getCurrentSkillExp(statBeingTrained) - expStart);
        float xpsec;
        if ((minutes > 0 || hours > 0 || seconds > 0) && GainedExp > 0) {
            xpsec = ((float) GainedExp)
                    / (float) (seconds + (minutes * 60) + (hours * 60 * 60));
            float xpmin = xpsec * 60;
            float xphour = xpmin * 60;
            render.drawString("XP/hour: " + (int) xphour, 15, 100);
        }
        if (!isBanking) {
            if (status == 1) {
                render.setColor(Color.red);
            } else {
                render.setColor(background);
            }
            render.fill3DRect(10, 320, 80, 20, true);
            render.setColor(Color.white);
            render.drawString("Fighting", 30, 335);
            if (status == 2) {
                render.setColor(Color.red);
            } else {
                render.setColor(background);
            }
            render.fill3DRect(100, 320, 80, 20, true);
            render.setColor(Color.white);
            render.drawString("Searching", 110, 335);
            if (status == 3) {
                render.setColor(Color.red);
            } else {
                render.setColor(background);
            }
            render.fill3DRect(200, 320, 80, 20, true);
            render.setColor(Color.white);
            render.drawString("Picking up", 210, 335);
            if (status == 4) {
                render.setColor(Color.red);
            } else {
                render.setColor(background);
            }
            render.fill3DRect(300, 320, 80, 20, true);
            render.setColor(Color.white);
            render.drawString("Going South", 305, 335);
            if (status == 5) {
                render.setColor(Color.red);
            } else {
                render.setColor(background);
            }
            render.fill3DRect(400, 320, 80, 20, true);
            render.setColor(Color.white);
            render.drawString("Going North", 405, 335);
        } else {
            if (status == 6) {
                render.setColor(Color.red);
            } else {
                render.setColor(background);
            }
            render.fill3DRect(10, 320, 80, 20, true);
            render.setColor(Color.white);
            render.drawString("Going Bank", 15, 335);
            if (status == 7) {
                render.setColor(Color.red);
            } else {
                render.setColor(background);
            }
            render.fill3DRect(100, 320, 80, 20, true);
            render.setColor(Color.white);
            render.drawString("Banking", 120, 335);
            if (status == 8) {
                render.setColor(Color.red);
            } else {
                render.setColor(background);
            }
            render.fill3DRect(200, 320, 80, 20, true);
            render.setColor(Color.white);
            render.drawString("Going Crabs", 205, 335);

        }

    }

}