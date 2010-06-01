import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Map;
import org.rsbot.bot.Bot;
import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = { "Cblair91" }, name = "CB-Cookeh", category = "Cooking", version = 2.5, description = "<html><body><b><center>CB-Cookeh</center></b><br />"
		+ "<center>by Cblair91</center><p>thread: http://www.rsbot.org/vb/showthread.php?t=250576</p><p>"
		+ "<b>Stop at level?</b><br>"
		+ "<select name='stop'>"
		+ "<option>NONE</option>"
		+ "<option>2</option>"
		+ "<option>3</option>"
		+ "<option>4</option>"
		+ "<option>5</option>"
		+ "<option>6</option>"
		+ "<option>7</option>"
		+ "<option>8</option>"
		+ "<option>9</option>"
		+ "<option>10</option>"
		+ "<option>11</option>"
		+ "<option>12</option>"
		+ "<option>13</option>"
		+ "<option>14</option>"
		+ "<option>15</option>"
		+ "<option>16</option>"
		+ "<option>17</option>"
		+ "<option>18</option>"
		+ "<option>19</option>"
		+ "<option>20</option>"
		+ "<option>21</option>"
		+ "<option>22</option>"
		+ "<option>23</option>"
		+ "<option>24</option>"
		+ "<option>25</option>"
		+ "<option>26</option>"
		+ "<option>27</option>"
		+ "<option>28</option>"
		+ "<option>29</option>"
		+ "<option>30</option>"
		+ "<option>31</option>"
		+ "<option>32</option>"
		+ "<option>33</option>"
		+ "<option>34</option>"
		+ "<option>35</option>"
		+ "<option>36</option>"
		+ "<option>37</option>"
		+ "<option>38</option>"
		+ "<option>39</option>"
		+ "<option>40</option>"
		+ "<option>41</option>"
		+ "<option>42</option>"
		+ "<option>43</option>"
		+ "<option>44</option>"
		+ "<option>45</option>"
		+ "<option>46</option>"
		+ "<option>47</option>"
		+ "<option>48</option>"
		+ "<option>49</option>"
		+ "<option>50</option>"
		+ "<option>51</option>"
		+ "<option>52</option>"
		+ "<option>53</option>"
		+ "<option>54</option>"
		+ "<option>55</option>"
		+ "<option>56</option>"
		+ "<option>57</option>"
		+ "<option>58</option>"
		+ "<option>59</option>"
		+ "<option>60</option>"
		+ "<option>61</option>"
		+ "<option>62</option>"
		+ "<option>63</option>"
		+ "<option>64</option>"
		+ "<option>65</option>"
		+ "<option>66</option>"
		+ "<option>67</option>"
		+ "<option>68</option>"
		+ "<option>69</option>"
		+ "<option>70</option>"
		+ "<option>71</option>"
		+ "<option>72</option>"
		+ "<option>73</option>"
		+ "<option>74</option>"
		+ "<option>75</option>"
		+ "<option>76</option>"
		+ "<option>77</option>"
		+ "<option>78</option>"
		+ "<option>79</option>"
		+ "<option>80</option>"
		+ "<option>81</option>"
		+ "<option>82</option>"
		+ "<option>83</option>"
		+ "<option>84</option>"
		+ "<option>85</option>"
		+ "<option>86</option>"
		+ "<option>87</option>"
		+ "<option>88</option>"
		+ "<option>89</option>"
		+ "<option>90</option>"
		+ "<option>91</option>"
		+ "<option>92</option>"
		+ "<option>93</option>"
		+ "<option>94</option>"
		+ "<option>95</option>"
		+ "<option>96</option>"
		+ "<option>97</option>"
		+ "<option>98</option>"
		+ "<option>99</option>"
		+ "<br><b>Location:</b><br>"
		+ "<select name='location'>"
		+ "<option>Al Kharid</option>"
		+ "<option>Rogues Den*</option>"
		+ "</select><p>"
		+ "Food to Cook:<br>"
		+ "<select name='food'>"
		+ "<option>Anchovies</option>"
		+ "<option>Bass*</option>"
		+ "<option>Beef</option>"
		+ "<option>Bread</option>"
		+ "<option>Cave eel*</option>"
		+ "<option>Cave Fish*</option>"
		+ "<option>Chicken</option>"
		+ "<option>Cod*</option>"
		+ "<option>Crayfish</option>"
		+ "<option>Curry*</option>"
		+ "<option>Herring</option>"
		+ "<option>Jubbly*</option>"
		+ "<option>Karambwan*</option>"
		+ "<option>Lobster</option>"
		+ "<option>Mackerel*</option>"
		+ "<option>Manta Ray*</option>"
		+ "<option>Monkfish*</option>"
		+ "<option>Pawya*</option>"
		+ "<option>Pike</option>"
		+ "<option>Rabbit*</option>"
		+ "<option>Rainbow Fish*</option>"
		+ "<option>Rocktail*</option>"
		+ "<option>Salmon</option>"
		+ "<option>Sardine</option>"
		+ "<option>Sea Turtle*</option>"
		+ "<option>Shark*</option>"
		+ "<option>Shrimp</option>"
		+ "<option>Slimy eel*</option>"
		+ "<option>Stew</option>"
		+ "<option>Swordfish</option>"
		+ "<option>Tuna</option>"
		+ "<option>Trout</option>"
		+ "</select><br>* = Members only item/location<br>"
		+ "If Cooking at Al-Kharid:<br>Start in Al-Kharid Bank, at the range<br>"
		+ "If Cooking at Rogues Den:<br>"
		+ "Start at Den near fire and banker!<br>"
		+ "If cooking in Catherby:<br>" + "Be in the bank or at the range")
public class CBCookeh extends Script implements ServerMessageListener,
		PaintListener {
	
	private RSTile[] toRange = { new RSTile(3269, 3169),
			new RSTile(3276, 3175), new RSTile(3274, 3180) };
	private RSTile[] toBank = reversePath(toRange);
	private RSTile Bank;
	private RSTile Range;
	private RSTile rogueFire = new RSTile(3043, 4973);
	private int RANGE;
	private int BANK;
	private int STOP;
	private static final int COOKANIM = 896;
	private int cookXP = 0;
	private int foodCooked = 0;
	private int foodBurnt = 0;
	private int startLevel;
	private int foodID = 0;
	private int foodXP;
	private String cooking;
	private String spot;
	public int tries = 0;
	public int count;
	public int roll = 0;

	private enum Status {
		bank, cook, walkb, walkr;
	}

	public long startTime = System.currentTimeMillis();
	public String status = "";

	@Override
	public boolean onStart(Map<String, String> args) {
		log("Starting CB-Cookeh, please wait");
		// Script and suggestion by "Aion" of RSBot.
		// His suggestion was appreciated and saved a lot of lines :D
		if (args.get("stop").equals("NONE")) {
			STOP = 200;
		} else {
			try {
				STOP = Integer.parseInt(args.get("stop"));
			} catch (Exception e) {
				STOP = 200;
			}
		}
		// End script and sugestion by "Aion" of RSBOT.
		if (args.get("location").equals("Al Kharid")) {
			BANK = 35647;
			RANGE = 25730;
			spot = "kharid";
		} else if (args.get("location").equals("Catherby*")) {
			BANK = 3440;
			RANGE = 3435;
			spot = "catherby";
		} else if (args.get("location").equals("Rogues Den*")) {
			RANGE = 2732;
			BANK = 2271;
			spot = "rogue";
		}
		if (args.get("food").equals("Shrimp")) {
			foodID = 317;
			foodXP = 30;
			cooking = "Shrimp";
		} else if (args.get("food").equals("Beef")) {
			foodID = 2132;
			foodXP = 30;
			cooking = "Beef";
		} else if (args.get("food").equals("Bread")) {
			foodID = 2307;
			foodXP = 40;
			cooking = "Bread";
		} else if (args.get("food").equals("Sardine")) {
			foodID = 327;
			foodXP = 40;
			cooking = "Sardine";
		} else if (args.get("food").equals("Crayfish")) {
			foodID = 13435;
			foodXP = 30;
			cooking = "Crayfish";
		} else if (args.get("food").equals("Chicken")) {
			foodID = 2138;
			foodXP = 30;
			cooking = "Chicken";
		} else if (args.get("food").equals("Rabbit*")) {
			foodID = 0;
			foodXP = 30;
			cooking = "Rabbit";
		} else if (args.get("food").equals("Anchovies")) {
			foodID = 321;
			foodXP = 30;
			cooking = "Anchovies";
		} else if (args.get("food").equals("Karambwan*")) {
			foodID = 3142;
			foodXP = 190;
			cooking = "Karambwan";
		} else if (args.get("food").equals("Herring")) {
			foodID = 345;
			foodXP = 50;
			cooking = "Herring";
		} else if (args.get("food").equals("Mackerel*")) {
			foodID = 353;
			foodXP = 60;
			cooking = "Mackerel";
		} else if (args.get("food").equals("Trout")) {
			foodID = 335;
			foodXP = 70;
			cooking = "Trout";
		} else if (args.get("food").equals("Cod*")) {
			foodID = 341;
			foodXP = 75;
			cooking = "Cod";
		} else if (args.get("food").equals("Pike")) {
			foodID = 349;
			foodXP = 80;
			cooking = "Pike";
		} else if (args.get("food").equals("Salmon")) {
			foodID = 331;
			foodXP = 90;
			cooking = "Salmon";
		} else if (args.get("food").equals("Tuna")) {
			foodID = 359;
			foodXP = 100;
			cooking = "Tuna";
		} else if (args.get("food").equals("Rainbow Fish*")) {
			foodID = 10138;
			foodXP = 110;
			cooking = "Rainbow Fish";
		} else if (args.get("food").equals("Lobster")) {
			foodID = 377;
			foodXP = 120;
			cooking = "Lobster";
		} else if (args.get("food").equals("Bass*")) {
			foodID = 363;
			foodXP = 130;
			cooking = "Bass";
		} else if (args.get("food").equals("Swordfish")) {
			foodID = 371;
			foodXP = 140;
			cooking = "Swordfish";
		} else if (args.get("food").equals("Monkfish*")) {
			foodID = 7944;
			foodXP = 150;
			cooking = "Monkfish";
		} else if (args.get("food").equals("Shark*")) {
			foodID = 383;
			foodXP = 210;
			cooking = "Shark";
		} else if (args.get("food").equals("Sea Turtle*")) {
			foodID = 395;
			foodXP = 211;
			cooking = "Sea Turtle";
		} else if (args.get("food").equals("Cave Fish*")) {
			foodID = 15264;
			foodXP = 214;
			cooking = "Cave Fish";
		} else if (args.get("food").equals("Manta Ray*")) {
			foodID = 389;
			foodXP = 216;
			cooking = "Manta Ray";
		} else if (args.get("food").equals("Rocktail*")) {
			foodID = 15270;
			foodXP = 225;
			cooking = "Rocktail";
		} else if (args.get("food").equals("Curry*")) {
			foodID = 15270;
			foodXP = 280;
			cooking = "Curry";
		} else if (args.get("food").equals("Stew")) {
			foodID = 2001;
			foodXP = 117;
			cooking = "Stew";
		} else if (args.get("food").equals("Pawya*")) {
			foodID = 12535;
			foodXP = 30;
			cooking = "Pawya";
		} else if (args.get("food").equals("Cave eel*")) {
			foodID = 5001;
			foodXP = 115;
			cooking = "Cave eel";
		} else if (args.get("food").equals("Jubbly*")) {
			foodID = 7566;
			foodXP = 160;
			cooking = "Jubbly";
		} else if (args.get("food").equals("Slimy eel*")) {
			foodID = 3379;
			foodXP = 95;
			cooking = "Slimy eel";
		}
		if (isLoggedIn()) {
			int startLevel = 0;
			if (startLevel == 0) {
				startLevel = skills.getRealSkillLevel(STAT_COOKING);
			}
			log("You are cooking " + cooking);
		}
		return true;
	}

	public void onFinish() {
		Bot.getEventManager().removeListener(PaintListener.class, this);
		return;
	}

	public void serverMessageRecieved(final ServerMessageEvent e) {
		final String word = e.getMessage().toLowerCase();
		if (word.contains("successfully cook")) {
			foodCooked++;
			cookXP = cookXP + foodXP;
		}
		if (word.contains("manage to cook")) {
			foodCooked++;
			cookXP = cookXP + foodXP;
		}
		if (word.contains("roast")) {
			foodCooked++;
			cookXP = cookXP + foodXP;
		}
		if (word.contains("manage to burn")) {
			foodBurnt++;
		}
		if (word.contains("accidentally burn")) {
			foodBurnt++;
		}
	}

	public void stopScripts() {
		System.out.println("All Scripts Stopped");
		Bot.getScriptHandler().stopScript();
	}

	public boolean animationCheck(final int id) {
		final int anim = getMyPlayer().getAnimation();
		if (id == anim) {
			return true;
		}
		return false;
	}

	public void antiban2() {
		roll = random(0, 20);
		if (roll == 7) {
			setCameraRotation(random(1, 45));
		}
	}

	public void checkenergy() {
		if (!isRunning()) {
			if (getEnergy() >= random(75, 100)) {
				log("Turning on run");
				setRun(true);
			}
		}
	}

	private int Banking() {
		try {
			if (!bank.isOpen()) {
				openBank();
				wait(random(400, 500));
				useBank();
				return 400;
			} else if (bank.isOpen()) {
				useBank();
				return 400;
			}
		} catch (final Exception e) {
		}
		return 30;
	}

	public boolean nearBank() {
		if (spot == "kharid") {
			RSObject banker = getNearestObjectByID(BANK);
			if (banker == null || distanceTo(banker) >= 5) {
				return false;
			} else {
				return true;
			}
		} else if (spot == "catherby") {
			RSObject banker = getNearestObjectByID(BANK);
			if (banker == null || distanceTo(banker) >= 5) {
				return false;
			} else {
				return true;
			}
		} else {
			RSNPC banker = getNearestNPCByID(BANK);
			if (banker == null || distanceTo(banker) >= 7) {
				return false;
			} else {
				return true;
			}
		}
	}

	public void openBank() {
		try {
			if (spot == "kharid") {
				final RSObject bankbooth = getNearestObjectByID(BANK);
				if (bankbooth != null) {
					while (getMyPlayer().isMoving()) {
						wait(10);
					}
					if (nearBank()) {
						status = "About to Bank";
						Bank = bankbooth.getLocation();
						final Point location = Calculations.tileToScreen(Bank);
						if (location.x == -1 || location.y == -1) {
							wait(500);
						}
						clickMouse(location, 2, 2, false);
						wait(200);
						if (getMenuActions().contains("Use-quickly")) {
							atMenu("Use-quickly");
							wait(500);
						} else {
							atMenu("Cancel");
							setCameraRotation(random(1, 360));
						}
					}
				} else if (spot == "catherby") {
					if (bankbooth != null) {
						while (getMyPlayer().isMoving()) {
							wait(10);
						}
						if (nearBank()) {
							status = "About to Bank";
							Bank = bankbooth.getLocation();
							final Point location = Calculations
									.tileToScreen(Bank);
							if (location.x == -1 || location.y == -1) {
								wait(500);
							}
							clickMouse(location, 2, 2, false);
							wait(200);
							if (getMenuActions().contains("Use-quickly")) {
								atMenu("Use-quickly");
								wait(500);
							} else {
								atMenu("Cancel");
								setCameraRotation(random(1, 360));
							}
						}
					}
				} else {
					walkPathMM(randomizePath(toBank, 2, 2), 17);
				}
			} else {
				final RSNPC bankbooth = getNearestNPCByID(BANK);
				if (bankbooth != null) {
					while (getMyPlayer().isMoving()) {
						wait(10);
					}
					if (nearBank()) {
						status = "About to Bank";
						Bank = bankbooth.getLocation();
						final Point location = Calculations.tileToScreen(Bank);
						if (location.x == -1 || location.y == -1) {
							wait(500);
						}
						clickMouse(location, 2, 2, false);
						wait(200);
						if (getMenuActions().contains("Bank")) {
							atMenu("Bank");
							wait(500);
						} else {
							atMenu("Cancel");
							setCameraRotation(random(1, 360));
						}
					}
				}
			}
		} catch (final Exception e) {
		}
	}

	public void useBank() {
		try {
			if (RSInterface.getInterface(Constants.INTERFACE_BANK).isValid()) {
				status = "Banking";
				if (getInventoryCount(foodID) > 1) {
					bank.close();
					return;
				} else if (getInventoryCount(foodID) == 0) {
					if (getInventoryCount() == 0) {
						if (bank.getCount(foodID) == 0) {
							log("There is no " + cooking + "(" + foodID
									+ ") left, Script has stopped");
							stopScripts();
						}
						for (int i = 0; i < 1; i++) {
							bank.withdraw(foodID, 0);
							wait(1000);
							bank.close();
						}
					} else {
						bank.depositAll();
						wait(200);
						if (bank.getCount(foodID) == 0) {
							log("There is no " + cooking + "(" + foodID
									+ ") left, Script has stopped");
							stopScripts();
						}
						bank.withdraw(foodID, 0);
						wait(1000);
						bank.close();
					}
				}
			}
			bank.close();
		} catch (final Exception e) {
		}
	}

	public boolean nearRange() {
		if (spot == "kharid") {
			final RSObject range = getNearestObjectByID(RANGE);
			if (range == null || distanceTo(range) >= 5) {
				return false;
			} else {
				return true;
			}
		} else if (spot == "catherby") {
			final RSObject range = getNearestObjectByID(RANGE);
			if (range == null || distanceTo(range) >= 5) {
				return false;
			} else {
				return true;
			}
		} else {
			final RSObject range = getObjectAt(rogueFire);
			if (range == null || distanceTo(range) >= 7) {
				return false;
			} else {
				return true;
			}
		}
	}

	public boolean atInventoryItemUse(int itemID) {
		if (getCurrentTab() != TAB_INVENTORY
				&& !RSInterface.getInterface(INTERFACE_BANK).isValid()
				&& !RSInterface.getInterface(INTERFACE_STORE).isValid()) {
			openTab(TAB_INVENTORY);
		}
		int[] items = getInventoryArray();
		java.util.List<Integer> possible = new ArrayList<Integer>();
		for (int i = 0; i < items.length; i++) {
			if (items[i] == itemID) {
				possible.add(i);
			}
		}
		if (possible.size() == 0)
			return false;
		int idx = possible.get(random(0, possible.size()));
		Point t = getInventoryItemPoint(idx);
		int x = (int) t.getX();
		int y = (int) t.getY();
		moveMouse(x + 15, y + 15, 2, 2);
		wait(100);
		if (getMenuIndex("Use") == 0) {
			clickMouse(true);
		} else {
			atInventoryItemUse(foodID);
		}
		return true;
	}

	public void moveRange() {
		if (getInventoryCount(foodID) > 0) {
			if (spot == "kharid") {
				final RSObject range = getNearestObjectByID(RANGE);
				Range = range.getLocation();
				final Point location = Calculations.tileToScreen(Range);
				if (location.x == -1 || location.y == -1) {
					wait(100);
				}
				moveMouse(location, 2, 2);
				wait(100);
				if (getMenuIndex("Use") == 0) {
					clickMouse(false);
					atMenu("Range");
					tries = 0;
				} else {
					if (tries <= 5) {
						tries++;
						moveRange();
					} else {
						tries = 0;
						Cook();
					}
				}
			} else if (spot == "catherby") {
				final RSObject range = getNearestObjectByID(RANGE);
				Range = range.getLocation();
				final Point location = Calculations.tileToScreen(Range);
				if (location.x == -1 || location.y == -1) {
					wait(100);
				}
				moveMouse(location, 2, 2);
				wait(100);
				if (getMenuIndex("Use") == 0) {
					clickMouse(false);
					atMenu("Range");
					tries = 0;
				} else {
					if (tries <= 5) {
						tries++;
						moveRange();
					} else {
						tries = 0;
						Cook();
					}
				}
			} else {
				final RSObject range = getObjectAt(rogueFire);
				Range = range.getLocation();
				final Point location = Calculations.tileToScreen(Range);
				if (location.x == -1 || location.y == -1) {
					wait(100);
				}
				moveMouse(location, 2, 2);
				wait(100);
				if (getMenuIndex("Use") == 0) {
					clickMouse(false);
					atMenu("Fire");
					tries = 0;
				} else {
					if (tries <= 5) {
						tries++;
						moveRange();
					} else {
						tries = 0;
						Cook();
					}
				}
			}
		}
	}

	public void Cook() {
		try {
			if (getInventoryCount(foodID) > 0) {
				if (spot == "kharid") {
					final RSObject range = getNearestObjectByID(RANGE);
					if (range != null) {
						while (getMyPlayer().isMoving()) {
							wait(10);
						}
						if (nearRange()) {
							status = "Ready";
							atInventoryItemUse(foodID);
							moveRange();
							wait(1000);
							while (getMyPlayer().isMoving()) {
								wait(20);
							}
							wait(500);
							if (RSInterface.getInterface(513).isValid()) {
								clickMouse(260, 415, 10, 10, false);
								if (getMenuActions().contains("Cook All")) {
									atMenu("Cook All");
									status = "Cooking";
									count = getInventoryCount(foodID);
									for (int i = 0; i <= count; i++) {
										if (RSInterface.getInterface(
												Constants.INTERFACE_LEVELUP)
												.isValid()) {
											clickContinue();
										}
										antiBan();
										wait(2250);
									}
								} else {
									atMenu("Cancel");
									setCameraRotation(random(1, 360));
								}

							}
						}
					} else if (spot == "catherby") {
						if (range != null) {
							while (getMyPlayer().isMoving()) {
								wait(10);
							}
							if (nearRange()) {
								status = "Ready";
								atInventoryItemUse(foodID);
								moveRange();
								wait(1000);
								while (getMyPlayer().isMoving()) {
									wait(20);
								}
								wait(500);
								if (RSInterface.getInterface(513).isValid()) {
									clickMouse(260, 415, 10, 10, false);
									if (getMenuActions().contains("Cook All")) {
										atMenu("Cook All");
										status = "Cooking";
										count = getInventoryCount(foodID);
										for (int i = 0; i <= count; i++) {
											if (RSInterface
													.getInterface(
															Constants.INTERFACE_LEVELUP)
													.isValid()) {
												clickContinue();
											}
											antiBan();
											wait(2250);
										}
									}
								} else {
									atMenu("Cancel");
									setCameraRotation(random(1, 360));
								}
							}
						}
					} else {
						walkPathMM(randomizePath(toRange, 2, 2), 17);
					}
				} else {
					final RSObject range = getObjectAt(rogueFire);
					if (range != null) {
						while (getMyPlayer().isMoving()) {
							wait(10);
						}
						if (nearRange()) {
							status = "Ready to cook";
							atInventoryItemUse(foodID);
							moveRange();
							wait(1000);
							if (getMyPlayer().isMoving()) {
								wait(500);
							}
							wait(1000);
							if (RSInterface.getInterface(513).isValid()) {
								clickMouse(260, 415, 10, 10, false);
								if (getMenuActions().contains("Cook All")) {
									atMenu("Cook All");
									status = "Cooking";
									count = getInventoryCount(foodID);
									for (int i = 0; i <= count; i++) {
										if (RSInterface.getInterface(
												Constants.INTERFACE_LEVELUP)
												.isValid()) {
											clickContinue();
										}
										antiBan();
										wait(2250);
									}
								} else {
									atMenu("Cancel");
									setCameraRotation(random(1, 360));
								}
							}
						}
					}
				}
			}
		} catch (final Exception e) {
		}
	}

	private int walkToBank() {
		try {
			if (distanceTo(getDestination()) > 2) {
				if (getMyPlayer().isMoving()) {
					return 500;
				}
			}
			walkPathMM(randomizePath(toBank, 2, 2), 18);
		} catch (final Exception e) {
		}
		return 50;
	}

	private int walkToRange() {
		try {
			if (distanceTo(getDestination()) > 1) {
				if (getMyPlayer().isMoving()) {
					return 500;
				}
			}
			walkPathMM(randomizePath(toRange, 1, 1), 18);
		} catch (final Exception e) {
		}
		return 50;
	}

	private Status getState() {
		if (getInventoryCount(foodID) == 0 && nearBank()) {
			return Status.bank;
		}
		if (getInventoryCount(foodID) >= 1
				&& nearRange()
				&& !RSInterface.getInterface(Constants.INTERFACE_BANK)
						.isValid()) {
			return Status.cook;
		}
		if (spot == "kharid") {
			if (getInventoryCount(foodID) >= 1 && !nearRange()) {
				return Status.walkr;
			}
			if (getInventoryCount(foodID) == 0 && !nearBank()) {
				return Status.walkb;
			}
		}
		if (spot == "catherby") {
			if (getInventoryCount(foodID) >= 1 && !nearRange()) {
				return Status.walkr;
			}
			if (getInventoryCount(foodID) == 0 && !nearBank()) {
				return Status.walkb;
			}
		}
		return null;
	}

	public int loop() {
		try {
			if (!isLoggedIn()) {
				return random(1000, 15000);
			}
			if (RSInterface.getInterface(Constants.INTERFACE_BANK).isValid()
					&& getInventoryCount(foodID) >= 1) {
				bank.close();
			}
			if (RSInterface.getInterface(Constants.INTERFACE_LEVELUP).isValid()) {
				clickContinue();
			}

			while (animationCheck(COOKANIM)) {
				wait(1238);
			}
			checkenergy();
			antiBan();
			antiban2();
			levelStop();
			setCameraAltitude(true);
			switch (getState()) {
			case bank:
				status = "Banking";
				Banking();
				return 50;
			case walkb:
				status = "Going to bank";
				walkToBank();
				return 50;
			case cook:
				status = "Cooking";
				Cook();
				return 50;
			case walkr:
				status = "Going to range";
				walkToRange();
				return 50;
			default:
				return 50;
			}
		} catch (final Exception e) {
		}
		return 4;
	}

	public boolean clickcontinue() {
		if (getContinueChildInterface() != null) {
			if (getContinueChildInterface().getText().contains("to continue")) {
				return atInterface(getContinueChildInterface());
			}
		}
		return false;
	}

	public void onRepaint(final Graphics render) {
		if (isLoggedIn()) {
			long millis = System.currentTimeMillis() - startTime;
			final long hours = millis / (1000 * 60 * 60);
			millis -= hours * 1000 * 60 * 60;
			final long minutes = millis / (1000 * 60);
			millis -= minutes * 1000 * 60;
			final long seconds = millis / 1000;
			float cooksec = 0;
			if ((minutes > 0 || hours > 0 || seconds > 0) && cookXP > 0) {
				cooksec = (float) foodCooked
						/ (float) (seconds + minutes * 60 + hours * 60 * 60);
			}
			final float cookmin = cooksec * 60;
			final float cookhour = cookmin * 60;
			float expsec = 0;
			if ((minutes > 0 || hours > 0 || seconds > 0) && cookXP > 0) {
				expsec = (float) cookXP
						/ (float) (seconds + minutes * 60 + hours * 60 * 60);
			}
			final float expmin = expsec * 60;
			final float exphour = expmin * 60;
			final int levelsGained = skills.getRealSkillLevel(STAT_COOKING)
					- startLevel;
			render.setColor(new Color(215, 218, 231, 85));
			render.fill3DRect(4, 133, 210, 205, true);
			render.setColor(Color.black);
			render.setFont(new Font("sansserif", Font.BOLD, 12));
			render.drawString("CB-Cookeh", 7, 152);
			render.setColor(Color.blue);
			render.drawString("CB-Cookeh", 8, 151);
			render.setColor(Color.black);
			render.setFont(new Font("sansserif", Font.PLAIN, 12));
			render.drawString("Time running: " + hours + " hrs " + minutes
					+ " mins " + seconds + " secs", 7, 169);
			render.drawString("Cooking: " + cooking, 7, 187);
			render.drawString("Food Cooked: " + foodCooked, 7, 205);
			render.drawString("Food Burnt: " + foodBurnt, 7, 223);
			render.drawString("Cooked per hour: " + (int) cookhour, 7, 241);
			render.drawString("Exp. Gained: " + cookXP, 7, 259);
			render.drawString("Exp. per hour: " + (int) exphour, 7, 277);
			render.drawString("Levels Gained: " + levelsGained, 7, 295);
			render.setColor(Color.blue);
			render.drawString("Status: " + status, 7, 313);
		}
	}

	private int levelStop() {
		final int levelss = skills.getCurrentSkillLevel(Constants.STAT_COOKING);
		if (STOP == levelss) {
			log("You have successfuly leveled to " + levelss);
			log("Thanks for using CB-Cookeh");
			stopScripts();
		}
		return 50;
	}

	private int antiBan() {
		int random = random(1, 4);
		switch (random) {
		case 1:
			if (random(1, 4) == 2) {
				moveMouseRandomly(300);
				return random(100, 500);
			}
		case 2:
			if (random(1, 10) == 5) {
				if (getCurrentTab() != TAB_INVENTORY) {
					openTab(TAB_INVENTORY);
					return random(100, 500);
				} else {
					return random(100, 500);
				}
			}
		case 3:
			if (random(1, 200) == 100) {
				if (getMyPlayer().isMoving()) {
					return random(100, 500);
				}
				if (getCurrentTab() != TAB_STATS) {
					openTab(TAB_STATS);
				}
				moveMouse(686, 306, 20, 10);
				wait(random(3000, 6000));
				if (getCurrentTab() != TAB_INVENTORY) {
					openTab(TAB_INVENTORY);
				}
				return random(100, 500);
			}
		case 4:
			if (random(1, 150) == 75) {
				openTab(random(0, 8));
				wait(random(3000, 5000));
				return random(100, 500);
			}
		}
		return 50;
	}
}