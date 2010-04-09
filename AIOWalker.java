/*
 * If you would like to add onto this script, please create an issue at: http://code.google.com/p/rsbotsvn/issues/list
 * 
 * Your name will be included in the credits.
 */
import java.util.HashMap;
import java.util.Map;

import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(name = "AIOWalker", authors = "Taha, Pqqqqq, Rakura", version = 1.22, description = "<html><head><style type='text/css'> body {text-align:north; background-color: black; color: white;}<br><font size=\"4\">AIOWalker By: Taha, Pqqqqq, Rakura</font><br><br><center><input name='RestMode' type='checkbox' value='1'>Enable Rest</style><br><br><br><strong>Choose a location:</strong><br /><select name='Walk'><option>Custom Location<option>Agility (Barbarian Outpost)<option>Air Altar<option>Al Kharid<option>Ape Atoll<option>Bandit Camp<option>Barbarian Village<option>Bedabin Camp<option>Bob's Island<option>Body Altar<option>Burthorpe<option>Camelot<option>Camelot Flax Field<option>Canifis<option>Castle Wars<option>Catherby<option>Chaos Altar (N/A)<option>Cooking Guild<option>Cosmic Altar (N/A)<option>Crafting Guild<option>Crandor<option>Death Altar (N/A)<option>Desert City<option>Desert Mining Camp<option>Dig Site<option>Draynor Village<option>Druid's Circle<option>Duel Arena<option>Dwarven Mines<option>Earth Altar<option>East Ardougne<option>Edgeville<option>Elf Camp (Tirannwn)<option>Entrana<option>Exam Centre<option>Falador<option>Fenkenstrain's Castle<option>Fight Arena<option>Fire Altar<option>Fishing Guild<option>Games Room<option>The Grand Exchange<option>Grand Tree (lower)<option>Grand Tree (upper)<option>Gul'Tanoth<option>Hero's Guild<option>Isafdar (Tirannwn)<option>Law Altar (N/A)<option>Lumbridge<option>Mage Arena<option>Mage Store<option>Mind Altar<option>Mining Guild<option>Monastery<option>Monk Guild<option>Mort'ton<option>Pollnivneach<option>Port Khazard<option>Port Phasmatys<option>Port Sarim<option>Prifddinas<option>Pyramid<option>Quest: Black Knight's Fortress (go in, go up two sets of stairs)<option>Quest: The Cook's Assisant<option>Quest: Demon Slayer<option>Quest: Doric's Quest<option>Quest: Dragon Slayer(Champion's Guild)<option>Quest: Ernest the Chicken<option>Quest: Goblin Diplomacy<option>Quest: Imp Catcher(in 2 flights up)<option>Quest: The Knight's Sword<option>Quest: Pirate's Treasure<option>Quest: The Restless Ghost<option>Quest: Romeo & Juliet<option>Quest: Rune Mysteries(Have to Go Upstairs By Urself)<option>Quest: Sheep Shearer<option>Quest: Shield of Arrav<option>Quest: Vampire Slayer<option>Quest: Witch's Potion<option>Ranging Guild<option>Red Bird (Hunting)<option>Rellekka<option>Rimmington<option>Ruins of Uzer<option>Seer's Bank<option>Sophanem<option>Slayer Tower<option>Tree Gnome Stronghold<option>Tree Gnome Village (inside)<option>Tree Gnome Village (outside)<option>Trollheim<option>Tzhaar<option>Varrock<option>Warrior's Guild<option>Water Altar<option>West Ardougne<option>White Wolf Mountain<option>Wierd Altar (N/A)<option>Wizard Tower<option>Yanille<br><br /><br> X: <input name='XCoordinate' type='text' width='1' value='0000'/><center>Y: <input name='YCoordinate' type='text' width='1' value='0000' /></b></center>")
public class AIOWalker extends Script {
	private RSTile walkToTile;
	private boolean rest;
	private RSTile[] path;
	AIOWalkerCheckStuck checkStuck;
	Thread t;

	public boolean onStart(final Map<String, String> args) {
		if (args.get("RestMode") != null) {
			rest = true;
		}
		if (args.get("Walk").equals("Custom Location")) {
			walkToTile = new RSTile(Integer.parseInt(args.get("XCoordinate")),
					Integer.parseInt(args.get("YCoordinate")));
		} else {
			walkToTile = loadPlaces().get(args.get("Walk"));
		}
		checkStuck = new AIOWalkerCheckStuck();
		t = new Thread(checkStuck);
		return walkToTile != null;
	}

	protected int getMouseSpeed() {
		return random(6, 11);
	}

	@Override
	public int loop() {
		try {
			if (!t.isAlive()) {
				t.start();
			}
			if (path == null) {
				log("Generating a path from your current location...");
				path = cleanPath(generateFixedPath(walkToTile));
				log("Mission started.");
			}
			if (distanceTo(path[path.length - 1]) > 5) {
				getMouseSpeed();
				setCameraAltitude(true);
				if (rest
						&& (getEnergy() < 60 && random(1, 20) == 0 || getEnergy() < 20)) {
					rest(random(85, 95));
				}
				if (getEnergy() > 60 && random(1, 10) == 0 || getEnergy() > 80) {
					setRun(true);
				}
				if (distanceTo(getDestination()) < random(5, 12)
						|| distanceTo(getDestination()) > 40) {
					if (!walkPathMM(path)) {
						walkToClosestTile(path);
					}
				}
				wait(random(200, 400));
			} else {
				log("Mission accomplished.");
				return -1;
			}
		} catch (final Exception e) {
			log("Uh oh! Found an exception! Contact the script author if this persists!");
		}
		return random(400, 600);
	}

	public void onFinish() {
		checkStuck.stopThread = true;
	}

	private Map<String, RSTile> loadPlaces() {
		final Map<String, RSTile> ret = new HashMap<String, RSTile>();
		ret.put("Varrock", new RSTile(3214, 3424));
		ret.put("Lumbridge", new RSTile(3221, 3219));
		ret.put("Camelot", new RSTile(2964, 3380));
		ret.put("Falador", new RSTile(2964, 3378));
		ret.put("Yanille", new RSTile(2604, 3094));
		ret.put("The Grand Exchange", new RSTile(3165, 3487));
		ret.put("East Ardougne", new RSTile(2661, 3300));
		ret.put("Burthorpe", new RSTile(2899, 3546));
		ret.put("Rellekka", new RSTile(2643, 3677));
		ret.put("Edgeville", new RSTile(3094, 3493));
		ret.put("Mage Store", new RSTile(3253, 3402));
		ret.put("Draynor Village", new RSTile(3093, 3243));
		ret.put("Rimmington", new RSTile(2957, 3214));
		ret.put("Port Sarim", new RSTile(3023, 3208));
		ret.put("Al Kharid", new RSTile(3293, 3170));
		ret.put("Barbarian Village", new RSTile(3082, 3419));
		ret.put("Quest: Vampire Slayer", new RSTile(3099, 3269));
		ret.put("Quest: Ernest the Chicken", new RSTile(3111, 3329));
		ret.put("Quest: The Restless Ghost", new RSTile(3242, 3205));
		ret.put("Quest: The Cook's Assisant", new RSTile(3208, 3216));
		ret.put("Quest: Rune Mysteries(Have to Go Upstairs By Urself)",
				new RSTile(3205, 3209));
		ret.put("Quest: Sheep Shearer", new RSTile(3189, 3276));
		ret.put("Quest: Demon Slayer", new RSTile(3203, 3423));
		ret.put("Quest: Romeo & Juliet", new RSTile(3213, 3424));
		ret.put("Quest: Shield of Arrav", new RSTile(3210, 3489));
		ret.put("Quest: Dragon Slayer (Champion's Guild)", new RSTile(3191,
				3363));
		ret.put("Quest: The Knight's Sword", new RSTile(2978, 3343));
		ret
				.put(
						"Quest: Black Knight's Fortress (go in, go up two sets of stairs)",
						new RSTile(2965, 3339));
		ret.put("Quest: Doric's Quest", new RSTile(2949, 3450));
		ret.put("Quest: Goblin Diplomacy", new RSTile(2958, 3509));
		ret.put("Quest: Imp Catcher(in 2 flights up)", new RSTile(3109, 3167));
		ret.put("Quest: Pirate's Treasure", new RSTile(3052, 3248));
		ret.put("Quest: Witch's Potion", new RSTile(2964, 3206));
		ret.put("Wizard Tower", new RSTile(3109, 3167));
		ret.put("Dwarven Mines", new RSTile(3018, 3450));
		ret.put("Seer's Bank", new RSTile(2725, 3486));
		ret.put("Druid's Circle", new RSTile(2926, 3482));
		ret.put("White Wolf Mountain", new RSTile(2848, 3498));
		ret.put("Catherby", new RSTile(2813, 3447));
		ret.put("Ranging Guild", new RSTile(2665, 3430));
		ret.put("Fishing Guild", new RSTile(2603, 3414));
		ret.put("Agility (Barbarian Outpost)", new RSTile(2541, 3546));
		ret.put("Grand Tree (upper)", new RSTile(2480, 3488));
		ret.put("Grand Tree (lower)", new RSTile(2466, 3490));
		ret.put("Tree Gnome Stronghold", new RSTile(2461, 3443));
		ret.put("West Ardougne", new RSTile(2535, 3305));
		ret.put("Prifddinas", new RSTile(2242, 3278));
		ret.put("Elf camp (Tirannwn)", new RSTile(2197, 3252));
		ret.put("Isafdar (Tirannwn)", new RSTile(2241, 3238));
		ret.put("Duel Arena", new RSTile(3360, 3213));
		ret.put("Desert Mining Camp", new RSTile(3286, 3023));
		ret.put("Bedabin Camp", new RSTile(3171, 3026));
		ret.put("Bandit Camp", new RSTile(3176, 2987));
		ret.put("Pollnivneach", new RSTile(3365, 2970));
		ret.put("Pyramid", new RSTile(3233, 2901));
		ret.put("Sophanem", new RSTile(3305, 2755));
		ret.put("Ruins of Uzer", new RSTile(3490, 3090));
		ret.put("Mort'ton", new RSTile(3489, 3288));
		ret.put("Canifis", new RSTile(3506, 3496));
		ret.put("Port Phasmatys", new RSTile(3687, 3502));
		ret.put("Fenkenstrain's Castle", new RSTile(3550, 3548));
		ret.put("Dig Site", new RSTile(3354, 3402));
		ret.put("Exam Centre", new RSTile(3354, 3344));
		ret.put("Crafting Guild", new RSTile(2933, 3285));
		ret.put("Fight Arena", new RSTile(2585, 3150));
		ret.put("Tree Gnome Village (outside)", new RSTile(2521, 3177));
		ret.put("Tree Gnome Village (inside)", new RSTile(2525, 3167));
		ret.put("Port Khazard", new RSTile(2665, 3161));
		ret.put("Monastery", new RSTile(3051, 3490));
		ret.put("Crandor", new RSTile(2851, 3238));
		ret.put("Tzhaar", new RSTile(2480, 5175));
		ret.put("Bob's Island", new RSTile(2526, 4777));
		ret.put("Mining Guild", new RSTile(3049, 9737));
		ret.put("Ape Atoll", new RSTile(2801, 2704));
		ret.put("Cooking Guild", new RSTile(3143, 3442));
		ret.put("Monk Guild", new RSTile(3050, 3487));
		ret.put("Hero's Guild", new RSTile(2902, 3510));
		ret.put("Death Altar (N/A)", new RSTile(2207, 4836));
		ret.put("Cosmic Altar (N/A)", new RSTile(2162, 4833));
		ret.put("Air Altar", new RSTile(3129, 3405));
		ret.put("Water Altar", new RSTile(3185, 3163));
		ret.put("Earth Altar", new RSTile(3304, 3473));
		ret.put("Fire Altar", new RSTile(3313, 3253));
		ret.put("Body Altar", new RSTile(3053, 3443));
		ret.put("Law Altar (N/A)", new RSTile(2464, 4834));
		ret.put("Mind Altar", new RSTile(2980, 3514));
		ret.put("Weird Altar (N/A)", new RSTile(2528, 4833));
		ret.put("Chaos Altar (N/A)", new RSTile(2269, 4843));
		ret.put("Desert City", new RSTile(3291, 2764));
		ret.put("Games Room", new RSTile(2196, 4961));
		ret.put("Slayer Tower", new RSTile(3429, 3429));
		ret.put("Gul'Tanoth", new RSTile(2516, 3044));
		ret.put("Entrana", new RSTile(2834, 3335));
		ret.put("Mage Arena", new RSTile(3107, 3937));
		ret.put("Camalot Flax Field", new RSTile(2744, 3444));
		ret.put("Red Bird (Hunting)", new RSTile(2354, 3585));
		ret.put("Castle Wars", new RSTile(2400, 3103));
		ret.put("Trollheim", new RSTile(2910, 3612));
		ret.put("Warrior's Guild", new RSTile(2877, 3546));
		return ret;
	}

	private class AIOWalkerCheckStuck implements Runnable {
		private boolean stopThread;

		public void run() {
			while (!stopThread) {
				try {
					if (getMyPlayer().getAnimation() != 12108
							&& getMyPlayer().getAnimation() != 2033
							&& getMyPlayer().getAnimation() != 2716
							&& getMyPlayer().getAnimation() != 11786
							&& getMyPlayer().getAnimation() != 5713) {
						RSTile oldLoc = getMyPlayer().getLocation();
						int i = 0;
						for (i = 0; i < 20; i++) {
							Thread.sleep(random(500, 1000));
							if (getMyPlayer().getLocation().equals(oldLoc)) {
								i += 2;
							}
						}
						if (i > 20) {
							log("Detected the same player coordinates for quite some time!");
							walkTileMM(getClosestTileOnMap(path[path.length - 1]));
							log("Generating a new path...");
							path = cleanPath(generateFixedPath(walkToTile));
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

}