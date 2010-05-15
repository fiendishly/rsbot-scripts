import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;

import org.rsbot.bot.Bot;
import org.rsbot.bot.input.Mouse;
import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSItemTile;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSPlayer;
import org.rsbot.script.wrappers.RSTile;
import org.rsbot.util.ScreenshotUtil;

@ScriptManifest(authors = { "Afflicted H4x, Pervy Shuya" }, category = "Combat", name = "Yak Attack PRo", version = 10, description = "<html><body bgcolor = Black><font color = White><center><h2> WupDummyCurser</h2>"
        + "<h2>"
        + "Yak Attack PRo"
        + " version 10</h2><br>\n"
        + "Author: "
        + "Afflicted H4x, Pervy Shuya"
        + "<br><br>\n"
        + "Start at the Yaks on Neitiznot"
        + "<br>Based off of xX Nicole Xx's Cow Own3r."
        + "<br>We do not guarantee no-bans."
        + "<br>Has built-in Anti-Ban with this script + zenzie botter paradise"
        + "<br>Select Yes or No to eat<br>"
        + "<strong>Eat Food?</strong><br/>"
        + "<select name='eatsies'>"
        + "<option>Yes<option>No</select><br/>"
        + "Pick up arrows? <select name=\"Ranging\"><option>None<option>Bronze arrows<option>Iron arrows<option>Steel Arrow<option>Mithril Arrow<option>Adamant Arrow<option>Rune Arrow<option>Bronze Bolt<option>Bluerite Bolt<option>Bone Bolt<option>Iron Bolt<option>Steel Bolt<option>Black Bolt<option>Mithril Bolt<option>Adamant Bolt<option>Rune Bolt<option>Broad Bolt<option>Bronze Knife<option>Iron Knife<option>Steel Knife<option>Black Knife<option>Mithril Knife<option>Adamant Knife<option>Rune Knife<option>Bronze Dart<option>Iron Dart<option>Black Dart<option>Steel Dart<option>Mithril Dart<option>Adamant Dart<option>Rune Dart</select><br><br><br/>")
public class YakAttack extends Script implements PaintListener,
        ServerMessageListener {

    YakAttackAntiBan antiban;
    Thread t;
    private final int KILLYAKS = 0, KILLSCRIPT = 1;
    final ScriptManifest properties = getClass().getAnnotation(
            ScriptManifest.class);
    private final int[] yakID = { 5529 };

    int autoRandom(int min, int max) {
        return min + (int) (java.lang.Math.random() * (max - min));
    }

    int checkTime, Hour, Minute, Second;
    private int startLevel = 0, startXP = 0, Action = 0, hpLvl, atkExp, atkLvl,
            defExp, defLvl, hpExp, strExp, rangedExp, strLvl, rangedLvl,
            strGained, atkGained, rgeGained, defGained, hpGained, startAtkExp,
            startDefExp, startStrExp, startRangedExp, startHpExp, yaksKilled,
            yaksPerHour, arrowID = -1, bronzeArrow = 882, ironArrow = 884,
            steelArrow = 886, mithrilArrow = 888, adamantArrow = 890,
            runeArrow = 892, bronzeBolt = 877, boneBolt = 8882,
            blueriteBolt = 9139, ironBolt = 9140, steelBolt = 9141,
            blackBolt = 13083, mithrilBolt = 9142, adamantBolt = 9143,
            runeBolt = 9144, broadBolt = 13280, bronzeKnife = 864,
            ironKnife = 863, steelKnife = 865, blackKnife = 869,
            mithrilKnife = 866, adamantKnife = 867, runeKnife = 868,
            bronzeDart = 806, ironDart = 807, steelDart = 808,
            blackDart = 3093, mithrilDart = 809, adamantDart = 810,
            runeDart = 811, Lifepoints, speed, randomTime = random(240000,
                    750000);
    final int XPChange = skills.getCurrentSkillExp(14) - startXP,
            levelChange = skills.getCurrentSkillLevel(14) - startLevel;
    private long startTime = System.currentTimeMillis(), time = System
            .currentTimeMillis(), hours, minutes, seconds, lastCheck = System
            .currentTimeMillis();
    private String Status = "Starting", arrowName;

    BufferedImage normal = null, clicked = null;

    private final RSTile yakTile = new RSTile(2324, 3792);

    private boolean wants2Eat;

    private int[] foodID = { 1895, 1893, 1891, 4293, 2142, 291, 2140, 3228,
            9980, 7223, 6297, 6293, 6295, 6299, 7521, 9988, 7228, 2878, 7568,
            2343, 1861, 13433, 315, 325, 319, 3144, 347, 355, 333, 339, 351,
            329, 3381, 361, 10136, 5003, 379, 365, 373, 7946, 385, 397, 391,
            3369, 3371, 3373, 2309, 2325, 2333, 2327, 2331, 2323, 2335, 7178,
            7180, 7188, 7190, 7198, 7200, 7208, 7210, 7218, 7220, 2003, 2011,
            2289, 2291, 2293, 2295, 2297, 2299, 2301, 2303, 1891, 1893, 1895,
            1897, 1899, 1901, 7072, 7062, 7078, 7064, 7084, 7082, 7066, 7068,
            1942, 6701, 6703, 7054, 6705, 7056, 7060, 2130, 1985, 1993, 1989,
            1978, 5763, 5765, 1913, 5747, 1905, 5739, 1909, 5743, 1907, 1911,
            5745, 2955, 5749, 5751, 5753, 5755, 5757, 5759, 5761, 2084, 2034,
            2048, 2036, 2217, 2213, 2205, 2209, 2054, 2040, 2080, 2277, 2225,
            2255, 2221, 2253, 2219, 2281, 2227, 2223, 2191, 2233, 2092, 2032,
            2074, 2030, 2281, 2235, 2064, 2028, 2187, 2185, 2229, 6883, 1971,
            4608, 1883, 1885 };
    int[] tabs = { TAB_ATTACK, TAB_CLAN, TAB_IGNORE, TAB_FRIENDS, TAB_OPTIONS,
            TAB_QUESTS, TAB_MAGIC, TAB_MUSIC, TAB_PRAYER, TAB_EQUIPMENT,
            INTERFACE_TAB_EMOTES };
    int maxYTab = 52, maxXTab = 64;
    int[] stats = { STAT_ATTACK, STAT_DEFENSE, STAT_STRENGTH, STAT_HITPOINTS,
            STAT_RANGE, STAT_PRAYER, STAT_MAGIC, STAT_COOKING,
            STAT_WOODCUTTING, STAT_FLETCHING, STAT_FISHING, STAT_FIREMAKING,
            STAT_CRAFTING, STAT_SMITHING, STAT_MINING, STAT_HERBLORE,
            STAT_AGILITY, STAT_THIEVING, STAT_SLAYER, STAT_FARMING,
            STAT_RUNECRAFTING, STAT_HUNTER, STAT_CONSTRUCTION, STAT_SUMMONING };

    private int getCurrentLifepoint() {
        if (RSInterface.getInterface(748).getChild(8).isValid()) {
            if (RSInterface.getInterface(748).getChild(8).getText() != null) {
                Lifepoints = Integer.parseInt(RSInterface.getInterface(748)
                        .getChild(8).getText());
            } else {
                log.severe("Getting lifepoints Error");
            }
        } else {
            log.warning("HP Interface is not valid");
        }

        return Lifepoints;
    }

    private int getAngleToCoord(RSTile loc) {
        int x1 = getMyPlayer().getLocation().getX();
        int y1 = getMyPlayer().getLocation().getY();
        int x = x1 - loc.getX();
        int y = y1 - loc.getY();
        double angle = Math.toDegrees(Math.atan2(y, x));
        log("Yak's Angle: " + (int) angle + "\u00B0");
        return (int) angle;
    }

    private void handleArrows() {
        if (arrowID != -1 && !getMyPlayer().isInCombat()) {
            RSItemTile rangeStuff = getNearestGroundItemByID(5, arrowID);
            Status = "Picking up Arrows";
            log("Picking up Arrows");
            atTile(rangeStuff, "Take " + arrowName);
            wait(random(400, 600));
            return;
        }
    }

    private boolean clickInventoryItem(final int[] ids, final String command) {
        try {
            if (getCurrentTab() != Constants.TAB_INVENTORY
                    && !RSInterface.getInterface(Constants.INTERFACE_BANK)
                            .isValid()
                    && !RSInterface.getInterface(Constants.INTERFACE_STORE)
                            .isValid()) {
                openTab(Constants.TAB_INVENTORY);
            }
            final int[] items = getInventoryArray();
            final java.util.List<Integer> possible = new ArrayList<Integer>();
            for (int i = 0; i < items.length; i++) {
                for (final int item : ids) {
                    if (items[i] == item) {
                        possible.add(i);
                    }
                }
            }
            if (possible.size() == 0)
                return false;
            final int idx = possible.get(random(0, possible.size()));
            final Point t = getInventoryItemPoint(idx);
            moveMouse(t, 5, 5);
            wait(random(100, 290));
            if (getMenuActions().get(0).equals(command)) {
                clickMouse(true);
                return true;
            } else
                // clickMouse(false);
                return atMenu(command);
        } catch (final Exception e) {
            log.log(Level.SEVERE, "clickInventoryFood(int...) error: ", e);
            return false;
        }
    }

    int getStatX(int id) {
        switch (id) {
        case STAT_ATTACK:
            return 552;
        case STAT_STRENGTH:
            return 552;
        case STAT_DEFENSE:
            return 552;
        case STAT_RANGE:
            return 552;
        case STAT_PRAYER:
            return 552;
        case STAT_MAGIC:
            return 552;
        case STAT_RUNECRAFTING:
            return 552;
        case STAT_HITPOINTS:
            return 606;
        case STAT_AGILITY:
            return 606;
        case STAT_HERBLORE:
            return 606;
        case STAT_THIEVING:
            return 606;
        case STAT_CRAFTING:
            return 606;
        case STAT_FLETCHING:
            return 606;
        case STAT_SLAYER:
            return 606;
        case STAT_MINING:
            return 660;
        case STAT_SMITHING:
            return 660;
        case STAT_FISHING:
            return 660;
        case STAT_COOKING:
            return 660;
        case STAT_FIREMAKING:
            return 660;
        case STAT_WOODCUTTING:
            return 660;
        case STAT_FARMING:
            return 660;
        }
        log
                .warning("Stats Tab - Error getting stats X-coordinates - Random move on screen");
        return autoRandom(1, 760);
    }

    int getStatY(int id) {
        switch (id) {
        case STAT_ATTACK:
            return 229;
        case STAT_STRENGTH:
            return 262;
        case STAT_DEFENSE:
            return 294;
        case STAT_RANGE:
            return 326;
        case STAT_PRAYER:
            return 358;
        case STAT_MAGIC:
            return 390;
        case STAT_RUNECRAFTING:
            return 422;
        case STAT_HITPOINTS:
            return 229;
        case STAT_AGILITY:
            return 262;
        case STAT_HERBLORE:
            return 294;
        case STAT_THIEVING:
            return 326;
        case STAT_CRAFTING:
            return 358;
        case STAT_FLETCHING:
            return 390;
        case STAT_SLAYER:
            return 422;
        case STAT_MINING:
            return 229;
        case STAT_SMITHING:
            return 262;
        case STAT_FISHING:
            return 294;
        case STAT_COOKING:
            return 326;
        case STAT_FIREMAKING:
            return 358;
        case STAT_WOODCUTTING:
            return 390;
        case STAT_FARMING:
            return 422;
        }
        log
                .warning("Stats Tab - Error getting stats Y-coordinates - Random move on screen");
        return autoRandom(1, 500);
    }

    private boolean hoverPlayer() {
        RSPlayer player = null;
        int[] validPlayers = Bot.getClient().getRSPlayerIndexArray();
        org.rsbot.accessors.RSPlayer[] players = Bot.getClient()
                .getRSPlayerArray();

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
                    log("Hover Player - Right click on " + name);
                } else {
                    continue;
                }
                return true;
            } catch (Exception ignored) {
            }
        }
        return player != null;
    }

    private RSTile examineRandomObject(int scans) {
        RSTile start = getMyPlayer().getLocation();
        ArrayList<RSTile> possibleTiles = new ArrayList<RSTile>();
        for (int h = 1; h < scans * scans; h += 2) {
            for (int i = 0; i < h; i++) {
                for (int j = 0; j < h; j++) {
                    int offset = (h + 1) / 2 - 1;
                    if (i > 0 && i < h - 1) {
                        j = h - 1;
                    }
                    RSTile tile = new RSTile(start.getX() - offset + i, start
                            .getY()
                            - offset + j);
                    RSObject objectToList = getObjectAt(tile);

                    if (objectToList != null && objectToList.getType() == 0
                            && tileOnScreen(objectToList.getLocation())
                            && objectToList.getLocation().isValid()) {
                        possibleTiles.add(objectToList.getLocation());
                    }
                }
            }
        }
        if (possibleTiles.size() == 0) {
            log.warning("Examine Object - Found no object");
            return null;
        }
        if (possibleTiles.size() > 0 && possibleTiles != null) {
            final RSTile objectLoc = possibleTiles.get(autoRandom(0,
                    possibleTiles.size()));
            Point objectPoint = objectLoc.getScreenLocation();
            if (objectPoint != null) {
                log("Examine Object - Found object at: RSTile "
                        + objectLoc.getX() + ", " + objectLoc.getY());
                try {
                    moveMouse(objectPoint);
                    if (atMenu("xamine")) {
                        log("Examine Object - Examined object");
                    } else {
                        log("Examine Object - Error examine");
                    }
                    wait(random(500, 1000));
                } catch (NullPointerException ignored) {
                }
            }
        }
        return null;
    }

    private boolean clickNPC(final RSNPC npc, final String action) {
        if (npc == null)
            return false;
        final RSTile tile = npc.getLocation();
        if (!tile.isValid())
            return false;

        try {
            Point screenLoc = npc.getScreenLocation();
            if (distanceTo(tile) > 6 || !pointOnScreen(screenLoc)) {
                turnToTile(tile);
            }
            if (distanceTo(tile) > 20) {
                walkTileMM(tile);
                return false;
            }
            for (int i = 0; i < 12; i++) {
                screenLoc = npc.getScreenLocation();
                if (!npc.isValid() || !pointOnScreen(screenLoc))
                    return false;
                moveMouse(screenLoc, 5, 5);
                if (getMenuItems().get(0).toLowerCase().contains(
                        npc.getName().toLowerCase())) {
                    break;
                }
                if (getMouseLocation().equals(screenLoc)) {
                    break;
                }
            }
            final List<String> menuItems = getMenuItems();
            if (menuItems.isEmpty())
                return false;
            for (String menuItem : menuItems) {
                if (menuItem.toLowerCase()
                        .contains(npc.getName().toLowerCase())) {
                    if (menuItems.get(0).toLowerCase().contains(
                            action.toLowerCase())) {
                        clickMouse(true);
                        return true;
                    } else {
                        clickMouse(false);
                        return atMenu(action);
                    }
                }
            }
        } catch (final Exception e) {
            log.log(Level.SEVERE, "clickNPC(RSNPC, String) error: ", e);
            return false;
        }
        return false;
    }

    private int getAction() {
        if (distanceTo(yakTile) < 50)
            return KILLYAKS;
        else
            return KILLSCRIPT;
    }

    @Override
    public int loop() {
        if (wants2Eat) {
            if (getInventoryCount(foodID) >= 1) {
                int RealHP = skills.getRealSkillLevel(STAT_HITPOINTS) * 10;
                if (getCurrentLifepoint() <= random(RealHP / 2, RealHP / 1.5)) {
                    Status = "Eating Food";
                    clickInventoryItem(foodID, "Eat");
                } else {
                    if (getInventoryCount(foodID) == 0) {
                        Status = "Out of food! shutting down";
                        log("We are out of food! logging out");
                        wait(8000);
                        logout();
                        stopScript();
                    }
                }
            }
        }
        if (isLoggedIn()
                && (System.currentTimeMillis() - lastCheck) >= randomTime) {
            lastCheck = System.currentTimeMillis();
            randomTime = random(240000, 750000);

            int antiBan = autoRandom(0, 9);
            switch (antiBan) {
            case 1:
                speed = (autoRandom(6, 10));
                getMouseSpeed();
                log("Mouse Speed - Changed mouse speed to: " + speed);
                break;

            case 2:
                int movedMouse = 0;
                for (int i = 1; i < 11; i++) {
                    int randomMouse = autoRandom(0, 3);
                    if (randomMouse == 1) {
                        movedMouse++;
                        moveMouse(1, 1, 760, 500);
                        wait(random(500, 1000));
                        if (movedMouse <= 1) {
                            log("Moved Mouse - " + movedMouse + " Time");
                        } else {
                            log("Moved Mouse - " + movedMouse + " Times");
                        }
                    }
                }
                break;

            case 3:
                int randomShit = autoRandom(0, 4);
                if (randomShit == 1) {
                    int randTab = tabs[autoRandom(0, tabs.length)];
                    if (getCurrentTab() != randTab) {
                        openTab(randTab);
                        log("Random Tab - Opened random tab");
                    }
                    int moveMouseOrNot = autoRandom(0, 4);
                    if (moveMouseOrNot == 1 || moveMouseOrNot == 2) {
                        moveMouse(550, 209, 730, 465);
                        log("Random Tab - Moved mouse in tab");
                    }
                    int backToInvent = autoRandom(0, 4);
                    if (backToInvent == 1 || backToInvent == 2) {
                        openTab(Constants.TAB_INVENTORY);
                        log("Random Tab - Switched back to invent");
                    }
                }
                break;

            case 4:
                hoverPlayer();
                wait(autoRandom(750, 3000));
                while (isMenuOpen()) {
                    moveMouseRandomly(750);
                    wait(random(100, 500));
                }
                break;

            case 5:
                examineRandomObject(5);
                wait(autoRandom(750, 3000));
                int moveMouseAfter2 = autoRandom(0, 4);
                wait(autoRandom(200, 3000));
                if (moveMouseAfter2 == 1 && moveMouseAfter2 == 2) {
                    moveMouse(1, 1, 760, 500);
                    log("Examine Object - Moved mouse after");
                }
                break;

            case 6:
                if (getCurrentTab() != Constants.TAB_INVENTORY
                        && !RSInterface.getInterface(Constants.INTERFACE_BANK)
                                .isValid()
                        && !RSInterface.getInterface(Constants.INTERFACE_STORE)
                                .isValid()) {
                    openTab(Constants.TAB_INVENTORY);
                    log("Hover item - Opened Inventory");
                }

                int[] items = getInventoryArray();
                java.util.List<Integer> possible = new ArrayList<Integer>();
                for (int i = 0; i < items.length; i++) {
                    if (items[i] > 1) {
                        possible.add(i);
                    }
                }
                if (possible.size() == 0) {
                    log("Hover Item - No items in inventory");
                }
                if (possible != null && possible.size() >= 1) {
                    int idx = possible.get(random(0, possible.size()));
                    Point t = getInventoryItemPoint(idx);
                    try {
                        if (idx != -1) {
                            moveMouse(t, 5, 5);
                            int rightClickOrNot = autoRandom(0, 3);
                            if (rightClickOrNot == 1 || rightClickOrNot == 2) {
                                clickMouse(false);
                                log("Hover item - Right clicked item");
                            } else {
                                log("Hover item - Hovered item");
                            }
                            int moveAfter = autoRandom(0, 3);
                            if (moveAfter == 1 || moveAfter == 2) {
                                moveMouse(1, 1, 760, 500);
                                log("Hover item - Moved mouse after");
                            }
                        } else {
                            log("Hover item - No items in inventory");
                        }
                    } catch (final Exception e) {
                        log.severe("Hover item - Error hovering item");
                    }
                }
                break;

            case 7:
                if (getCurrentTab() != TAB_STATS) {
                    openTab(TAB_STATS);
                    log("Stats Tab - Opened stats tab");
                    int hoveredSkill = 0;
                    int shouldHover = autoRandom(0, 4);
                    for (int i = 1; i < 5; i++) {
                        if (shouldHover == 1 || shouldHover == 2
                                || shouldHover == 3) {
                            int randomStat = stats[autoRandom(0, stats.length)];
                            hoveredSkill++;
                            moveMouse(getStatX(randomStat),
                                    getStatY(randomStat), maxXTab, maxYTab);
                            if (hoveredSkill <= 1) {
                                log("Stats Tab - Hovered " + hoveredSkill
                                        + " skill");
                            } else {
                                log("Stats Tab - Hovered " + hoveredSkill
                                        + " skills");
                            }
                            wait(autoRandom(500, 7000));
                        }
                    }
                }
                int backToInvent = autoRandom(0, 3);
                if (backToInvent == 1) {
                    openTab(Constants.TAB_INVENTORY);
                    log("Stats Tab - Switched back to inventory");
                }
                break;

            default: // Default, skipping
                log("Skipped AntiBan");
                break;
            }
            return random(300, 800);
        }

        Action = getAction();
        switch (Action) {
        case KILLYAKS:
            runControl();
            handleArrows();
            RSItemTile rangeStuff = getNearestGroundItemByID(arrowID);
            if (rangeStuff != null)
                return 100;
            if (inventoryContains(arrowID)
                    && getInventoryCount(arrowID) == random(50, 100)) {
                if (getCurrentTab() != TAB_INVENTORY) {
                    openTab(TAB_INVENTORY);
                }
                Status = "Equiping Arrows";
                atInventoryItem(arrowID, "Wield");
            }
            if (getMyPlayer().getInteracting() != null)
                return random(300, 450);

            final RSNPC yak = getNearestFreeNPCByID(yakID);
            if (yak != null) {
                if (yak.getInteracting() != null
                        && getMyPlayer().getInteracting() == null)
                    return random(100, 200);

                if (pointOnScreen(yak.getScreenLocation())
                        && getMyPlayer().getInteracting() == null) {
                    Status = "Attacking Yaks";
                    clickNPC(yak, "attack");
                    return random(800, 1400);
                } else if (!pointOnScreen(yak.getScreenLocation())
                        && getMyPlayer().getInteracting() == null) {
                    int yakAngle = getAngleToCoord(yak.getLocation());
                    Status = "Setting view to yaks";
                    setCameraRotation(yakAngle);
                } else {
                    moveMouseSlightly();
                }
                return random(200, 400);
            }
            return random(500, 1000);

        case KILLSCRIPT:
            log("Stopping script get to the Yak Pen on Neitiznot.");
            stopScript();
            return random(100, 200);
        }

        return random(400, 800);
    }

    @Override
    public boolean onStart(final Map<String, String> args) {
        if (args.get("eatsies").equals("Yes")) {
            log("Eating");
            wants2Eat = true;
        } else {
            if (args.get("eatsies").equals("No")) {
                log("Not Eating");
                wants2Eat = false;
            }
            if (args.get("Ranging").equals("Bronze arrows")) {
                arrowID = bronzeArrow;
                arrowName = "Bronze arrow";
            } else if (args.get("Ranging").equals("Iron arrows")) {
                arrowID = ironArrow;
                arrowName = "Iron arrow";
            } else if (args.get("Ranging").equals("Steel Arrow")) {
                arrowID = steelArrow;
                arrowName = "Steel arrow";
            } else if (args.get("Ranging").equals("Mithril Arrow")) {
                arrowID = mithrilArrow;
                arrowName = "Mithril arrow";
            } else if (args.get("Ranging").equals("Adamant Arrow")) {
                arrowID = adamantArrow;
                arrowName = "Adamant arrow";
            } else if (args.get("Ranging").equals("Rune Arrow")) {
                arrowID = runeArrow;
                arrowName = "Rune arrow";
            } else if (args.get("Ranging").equals("Bronze Bolt")) {
                arrowID = bronzeBolt;
                arrowName = "Bronze bolts";
            } else if (args.get("Ranging").equals("Bluerite Bolt")) {
                arrowID = blueriteBolt;
                arrowName = "Bluerite bolts";
            } else if (args.get("Ranging").equals("Bone Bolt")) {
                arrowID = boneBolt;
                arrowName = "Bone bolts";
            } else if (args.get("Ranging").equals("Iron Bolt")) {
                arrowID = ironBolt;
                arrowName = "Iron bolts";
            } else if (args.get("Ranging").equals("Steel Bolt")) {
                arrowID = steelBolt;
                arrowName = "Steel bolts";
            } else if (args.get("Ranging").equals("Black Bolt")) {
                arrowID = blackBolt;
                arrowName = "Bronze bolts";
            } else if (args.get("Ranging").equals("Mithril Bolt")) {
                arrowID = mithrilBolt;
                arrowName = "Mithril bolts";
            } else if (args.get("Ranging").equals("Adamant Bolt")) {
                arrowID = adamantBolt;
                arrowName = "Adamant bolts";
            } else if (args.get("Ranging").equals("Rune Bolt")) {
                arrowID = runeBolt;
                arrowName = "Rune bolts";
            } else if (args.get("Ranging").equals("Broad Bolt")) {
                arrowID = broadBolt;
                arrowName = "Broad bolts";
            } else if (args.get("Ranging").equals("Bronze Knife")) {
                arrowID = bronzeKnife;
                arrowName = "Bronze knife";
            } else if (args.get("Ranging").equals("Iron Knife")) {
                arrowID = ironKnife;
                arrowName = "Iron knife";
            } else if (args.get("Ranging").equals("Steel Knife")) {
                arrowID = steelKnife;
                arrowName = "Steel knife";
            } else if (args.get("Ranging").equals("Black Knife")) {
                arrowID = blackKnife;
                arrowName = "Black knife";
            } else if (args.get("Ranging").equals("Mithril Knife")) {
                arrowID = mithrilKnife;
                arrowName = "Mithril knife";
            } else if (args.get("Ranging").equals("Adamant Knife")) {
                arrowID = adamantKnife;
                arrowName = "Adamant knife";
            } else if (args.get("Ranging").equals("Rune Knife")) {
                arrowID = runeKnife;
                arrowName = "Rune knife";
            } else if (args.get("Ranging").equals("Bronze Dart")) {
                arrowID = bronzeDart;
                arrowName = "Bronze dart";
            } else if (args.get("Ranging").equals("Iron Dart")) {
                arrowID = ironDart;
                arrowName = "Iron dart";
            } else if (args.get("Ranging").equals("Steel Dart")) {
                arrowID = steelDart;
                arrowName = "Steel dart";
            } else if (args.get("Ranging").equals("Black Dart")) {
                arrowID = blackDart;
                arrowName = "Black dart";
            } else if (args.get("Ranging").equals("Mithril Dart")) {
                arrowID = mithrilDart;
                arrowName = "Mithril dart";
            } else if (args.get("Ranging").equals("Adamant Dart")) {
                arrowID = adamantDart;
                arrowName = "Adamant dart";
            } else if (args.get("Ranging").equals("Rune Dart")) {
                arrowID = runeDart;
                arrowName = "Rune dart";
            }
            Bot.disableBreakHandler = true;
            t = new Thread(new YakAttackAntiBan());
            if (!t.isAlive()) {
                t.start();
                log("Fagex Antiban Hammer initialized!");
            }
        }
        if (isLoggedIn()) {
            log("************************************************");
            log("********YakAttack PRo V" + properties.version()
                    + " started!**************");
            log("*************Let's Slay some Yak****************");
            log("************************************************");
            return true;
        } else {
            log.warning("You must be logged in to START this script.");
            return false;
        }
    }

    private void runControl() {
        if (!isRunning() && getEnergy() > random(20, 30)) {
            setRun(true);
        }
    }

    public void serverMessageRecieved(ServerMessageEvent e) {
        String serverString = e.getMessage();

        if (serverString.contains("<col=ffff00>System update in")) {
            log.warning("There will be a system update soon, so we logged out");
            logout();
            stopScript();
        }
        if (serverString.contains("Oh dear, you are dead!")) {
            Status = "Dead";
            log.warning("We somehow died :S, shutting down");
            logout();
            stopScript();
        }
        if (serverString.contains("Someone else is fighting that")) {
            Status = "Random Clicking";
            log("We click on someone yak, randomly clicking");
            log("so we don't look like we botting :)");
            if (getNearestFreeNPCByID(yakID) != null) {
                walkTo(getNearestFreeNPCByID(yakID).getLocation());
            } else {
                moveMouseRandomly(random(-4, 4));
            }
        }
        if (serverString.contains("I can't reach that!")) {
            final RSObject Gate = getNearestObjectByID(21600);
            if (Gate != null
                    && pointOnScreen(Gate.getLocation().getScreenLocation())) {
                atObject(Gate, "Open");
            } else if (!pointOnScreen(Gate.getLocation().getScreenLocation())) {
                walkTo(Gate.getLocation());
                wait(500);
                atObject(Gate, "Open");
            }
        }
        if (serverString.contains("already under attack")) {
            wait(random(2000, 3000));
            RSNPC yak = getNearestFreeNPCByID(yakID);
            if (!yak.isValid()) {
                yak = null;
            }
        }
        if (serverString.contains("There is no ammo left in your quiver.")) {
            log.warning("We have no arrows left, shutting down!");
            logout();
            stopScript();
        }
        if (serverString
                .contentEquals("You can't log out until 10 seconds after the end of combat.")) {
            log("Waiting 10 seconds before logging out");
            wait(random(10100, 11000));
            logout();
            stopScript();
        }
        if (serverString.contains("You've just advanced")) {
            log("Congrats on level up, Screenshot taken!");
            ScreenshotUtil.takeScreenshot(true);
            wait(random(1500, 2500));
            if (canContinue()) {
                clickContinue();
            }

        }
    }

    // *******************************************************//
    // PAINT SCREEN
    // *******************************************************//
    public void onRepaint(Graphics g) {
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        final Mouse mouse = Bot.getClient().getMouse();
        final int mouse_x = mouse.getMouseX();
        final int mouse_y = mouse.getMouseY();
        final int mouse_press_x = mouse.getMousePressX();
        final int mouse_press_y = mouse.getMousePressY();
        final long mouse_press_time = mouse.getMousePressTime();
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

        final int xpGained;
        atkExp = skills.getCurrentSkillExp(Constants.STAT_ATTACK);
        strExp = skills.getCurrentSkillExp(Constants.STAT_STRENGTH);
        defExp = skills.getCurrentSkillExp(Constants.STAT_DEFENSE);
        hpExp = skills.getCurrentSkillExp(Constants.STAT_HITPOINTS);
        rangedExp = skills.getCurrentSkillExp(Constants.STAT_RANGE);
        xpGained = (atkExp - startAtkExp) + (strExp - startStrExp)
                + (defExp - startDefExp) + (rangedExp - startRangedExp)
                + (hpExp - startHpExp);
        atkGained = (atkExp - startAtkExp);
        strGained = (strExp - startStrExp);
        defGained = (defExp - startDefExp);
        rgeGained = (rangedExp - startRangedExp);
        hpGained = (hpExp - startHpExp);
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
        if (startAtkExp == 0) {
            startAtkExp = skills.getCurrentSkillExp(Constants.STAT_ATTACK);
        }
        if (startStrExp == 0) {
            startStrExp = skills.getCurrentSkillExp(Constants.STAT_STRENGTH);
        }
        if (startDefExp == 0) {
            startDefExp = skills.getCurrentSkillExp(Constants.STAT_DEFENSE);
        }
        if (startHpExp == 0) {
            startHpExp = skills.getCurrentSkillExp(Constants.STAT_HITPOINTS);
        }
        if (startRangedExp == 0) {
            startRangedExp = skills.getCurrentSkillExp(Constants.STAT_RANGE);
        }

        final int xpHour = ((int) ((3600000.0 / time) * xpGained));
        float xpSec = 0;
        if ((minutes > 0 || hours > 0 || seconds > 0) && xpGained > 0) {
            xpSec = ((float) xpGained)
                    / (float) (seconds + (minutes * 60) + (hours * 60 * 60));
        }
        float xpMin = xpSec * 60;
        float xphour = xpMin * 60;
        yaksKilled = (xpGained / 200);
        yaksPerHour = (xpHour / 200);

        if (getCurrentTab() == TAB_INVENTORY) {
            g.setColor(new Color(0, 0, 0, 175));
            g.fillRoundRect(555, 210, 175, 250, 10, 10);
            g.setColor(Color.WHITE);
            int[] coords = new int[] { 225, 240, 255, 270, 285, 300, 315, 330,
                    345, 360, 375, 390, 405, 420, 435, 450 };
            g.drawString(properties.name(), 561, coords[0]);
            g.drawString("Version: " + properties.version(), 561, coords[1]);
            g.drawString("Run Time: " + hours + ":" + minutes + ":" + seconds,
                    561, coords[3]);
            if (atkGained != 0) {
                g.drawString("Attack exp gained: " + atkGained, 561, coords[5]);
            }
            if (strGained != 0) {
                g.drawString("strength exp gained: " + strGained, 561,
                        coords[6]);
            }
            if (defGained != 0) {
                g
                        .drawString("defence exp gained: " + defGained, 561,
                                coords[7]);
            }
            if (rgeGained != 0) {
                g.drawString("ranged exp gained: " + rgeGained, 561, coords[8]);
            }
            g.drawString("HP exp gained: " + hpGained, 561, coords[9]);
            g.drawString("Exp PerHour: " + Integer.toString((int) xphour), 561,
                    coords[10]);
            g.drawString("Total xpGained: " + Integer.toString(xpGained), 561,
                    coords[11]);
            g.drawString(
                    "Yaks Kills PerHour: " + Integer.toString(yaksPerHour),
                    561, coords[12]);
            g.drawString("Yaks Slayed: " + Integer.toString(yaksKilled), 561,
                    coords[13]);
            g.drawString("Status: " + Status, 561, coords[15]);
        }
    }

    @Override
    public void onFinish() {
        long millis = System.currentTimeMillis() - startTime;
        long hours = millis / (1000 * 60 * 60);
        millis -= hours * (1000 * 60 * 60);
        long minutes = millis / (1000 * 60);
        millis -= minutes * (1000 * 60);
        long seconds = millis / 1000;
        final int xpGained;
        final int lvlGained;
        ScreenshotUtil.takeScreenshot(true);
        xpGained = (atkExp - startAtkExp) + (strExp - startStrExp)
                + (defExp - startDefExp) + (rangedExp - startRangedExp)
                + (hpExp - startHpExp);
        lvlGained = (atkLvl) + (strLvl) + (defLvl) + (rangedLvl) + (hpLvl);
        log.info(": You have gained " + xpGained + " Experince + " + lvlGained
                + " Levels.");
        log.info("Ran for " + hours + ":" + minutes + ":" + seconds);
        antiban.stopThread = true;
        Bot.getEventManager().removeListener(PaintListener.class, this);
        Bot.getEventManager().removeListener(ServerMessageListener.class, this);
        logout();
        stopScript();
    }

    private class YakAttackAntiBan implements Runnable {
        private boolean stopThread;

        public void run() {
            Random random = new Random();
            while (!stopThread) {
                try {
                    if (random.nextInt(Math.abs(15 - 0)) == 0) {
                        final char[] LR = new char[] { KeyEvent.VK_LEFT,
                                KeyEvent.VK_RIGHT };
                        final char[] UD = new char[] { KeyEvent.VK_DOWN,
                                KeyEvent.VK_UP };
                        final char[] LRUD = new char[] { KeyEvent.VK_LEFT,
                                KeyEvent.VK_RIGHT, KeyEvent.VK_UP,
                                KeyEvent.VK_UP };
                        final int random2 = random.nextInt(Math.abs(2 - 0));
                        final int random1 = random.nextInt(Math.abs(2 - 0));
                        final int random4 = random.nextInt(Math.abs(4 - 0));

                        if (random.nextInt(Math.abs(3 - 0)) == 0) {
                            Bot.getInputManager().pressKey(LR[random1]);
                            Thread.sleep(random.nextInt(Math.abs(400 - 100)));
                            Bot.getInputManager().pressKey(UD[random2]);
                            Thread.sleep(random.nextInt(Math.abs(600 - 300)));
                            Bot.getInputManager().releaseKey(UD[random2]);
                            Thread.sleep(random.nextInt(Math.abs(400 - 100)));
                            Bot.getInputManager().releaseKey(LR[random1]);
                        } else {
                            Bot.getInputManager().pressKey(LRUD[random4]);
                            if (random4 > 1) {
                                Thread.sleep(random
                                        .nextInt(Math.abs(600 - 300)));
                            } else {
                                Thread.sleep(random
                                        .nextInt(Math.abs(900 - 500)));
                            }
                            Bot.getInputManager().releaseKey(LRUD[random4]);
                        }
                    } else {
                        Thread.sleep(random.nextInt(Math.abs(2000 - 200)));
                    }
                } catch (Exception e) {
                    System.out.println("AntiBan error detected!");
                }
            }
        }
    }
} 