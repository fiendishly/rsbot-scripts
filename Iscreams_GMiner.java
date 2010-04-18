import java.awt.*;

import java.util.ArrayList;
import java.util.Map;

import org.rsbot.bot.*;
import org.rsbot.event.listeners.*;
import org.rsbot.script.*;
import org.rsbot.script.wrappers.*;

@ScriptManifest(authors = "Iscream", name = "Iscreams Guild Miner", version = 1.3, category = "Mining", description = "Version 1.3 Has Arrived! All Options Are In GUI")
public class Iscreams_GMiner extends Script implements PaintListener {

	RSTile Ladder = new RSTile(3021, 3338);
	RSTile CLadder = new RSTile(3021, 9739);
	RSTile BankTile = new RSTile(3016, 3355);
	RSTile InBeet = new RSTile(3025, 3350);
	RSTile lastRockLocation = new RSTile(0000, 0000);
	RSTile NearLadder = new RSTile(3020, 3338);
	RSTile nearbotladder = new RSTile(3032, 9739);
	RSTile botladder2 = new RSTile(3021, 9739);
	RSTile centercoal = new RSTile(3041, 9735);

	public RSTile nearesttoladder = new RSTile(3024, 9739);
	public RSTile Rock;
	public RSTile randoma2 = new RSTile(0000, 0000);
	public RSTile randoma = new RSTile(0000, 0000);
	public RSTile mid1 = new RSTile(3048, 9739);
	public RSTile mid2 = new RSTile(3050, 9741);
	public RSTile mid3 = new RSTile(3046, 9735);
	public RSTile nearbank = new RSTile(3024, 3351);
	public RSTile neartopladder = new RSTile(3030, 3344);
	// center of coal polygon
	public int[] cx = {3026, 3026, 3060, 3060};
	public int[] cy = {9720, 9760, 9760, 9720};
	Polygon coalcenter = new Polygon(cx, cy, 4);
	// bottum ladder in guild polygon
	public int[] botladderx = {3023, 3023, 3015, 3015};
	public int[] botladdery = {9744, 9732, 9732, 9744};
	Polygon botladder = new Polygon(botladderx, botladdery, 4);
	// larger then the original bot ladder of the guild polygon
	public int[] botladderx2 = {3025, 3025, 3014, 3014};
	public int[] botladdery2 = {9746, 9734, 9734, 9746};
	Polygon botladder22 = new Polygon(botladderx2, botladdery2, 4);
	// top ladder of the guild polygon
	public int[] topladderx = {3027, 3027, 3013, 3013};
	public int[] topladdery = {3334, 3347, 3347, 3334};
	Polygon topladder = new Polygon(topladderx, topladdery, 4);
	// the bank polygon
	public int[] bankx = {3008, 3018, 3018, 3009};
	public int[] banky = {3360, 3360, 3355, 3355};
	Polygon bankp = new Polygon(bankx, banky, 4);
	// larger then the bank polygon to detect if the bot has stopped
	public int[] bankx2 = {3004, 3025, 3025, 3004};
	public int[] banky2 = {3370, 3370, 3350, 3350};
	Polygon bankp2 = new Polygon(bankx2, banky2, 4);
	// in beetween the bank and top ladder polygon
	public int[] centerx = {3022, 3018, 3029, 3032};
	public int[] centery = {3354, 3350, 3340, 3351};
	Polygon center = new Polygon(centerx, centery, 4);
	// top ladder big polygon
	public int[] topladderbigx = {3026, 3026, 3013, 3013};
	public int[] topladderbigy = {3326, 3345, 3345, 3326};
	Polygon topladderbig = new Polygon(topladderbigx, topladderbigy, 4);

	public RSObject objrand;
	public RSObject finrockid = null;
	public RSObject mith;
	public RSObject coal;
	public RSObject rock;
	public RSObject rock2;

	int[] coalRockID = {31068, 31069, 31070};
	int[] mithRockID = {31086, 31087, 31088};
	int[] bankBoothID = {11758};
	int[] axeIDs = {1265, 1267, 1269, 1273, 1271, 1275};
	int[] gemIDs = {1617, 1619, 1621, 1623};
	private int startXP = 0;
	private int startLVL = 0;
	public int coalbanked = 0;
	public int mithbanked = 0;
	public int coalperh = 0;
	public int xpperh = 0;
	public int gemsbanked = 0;

	public boolean mine = true;
	public boolean showpaint;
	public boolean check = true;
	boolean miningMith = false;
	boolean powermine = false;

	private long scriptStartTIME = 0;
	public long scriptstart = 0;
	public long idle = System.currentTimeMillis();
	public long idle2 = System.currentTimeMillis();
	public long idle3 = System.currentTimeMillis();
	public long idle4 = System.currentTimeMillis();
	public long runTime = 0;
	public long abs2 = 0;
	public long timeIdle = System.currentTimeMillis();
	public Point randomb;
	public javax.swing.JFrame ISGUI = new javax.swing.JFrame();
	public double version = getClass().getAnnotation(ScriptManifest.class)
			.version();
	public ArrayList<String> a;

	// gui vars
	// Variables declaration - do not modify
	private javax.swing.JButton jButton1;
	private javax.swing.JButton jButton2;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JLabel jLabel4;
	private javax.swing.JRadioButton jRadioButton1;
	private javax.swing.JRadioButton jRadioButton2;
	private javax.swing.JRadioButton jRadioButton3;

	// End of variables declaration

	private void initComponents() {

		jButton1 = new javax.swing.JButton();
		jButton2 = new javax.swing.JButton();
		jLabel1 = new javax.swing.JLabel();
		jRadioButton2 = new javax.swing.JRadioButton();
		jLabel2 = new javax.swing.JLabel();
		jRadioButton3 = new javax.swing.JRadioButton();
		jLabel3 = new javax.swing.JLabel();
		jLabel4 = new javax.swing.JLabel();
		jRadioButton1 = new javax.swing.JRadioButton();

		ISGUI
				.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		jButton1.setText("Start");
		jButton1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				powermine = jRadioButton2.isSelected();
				showpaint = jRadioButton3.isSelected();
				miningMith = jRadioButton1.isSelected();

				ISGUI.setVisible(false);
			}
		});

		jButton2.setText("Cancel");
		jButton2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				ISGUI.setVisible(false);
			}
		});

		jLabel1.setFont(new java.awt.Font("Ravie", 1, 12)); // NOI18N
		jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		jLabel1.setText("Iscreams G Miner");
		jLabel1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

		jRadioButton2.setText("PowerMine");
		jRadioButton2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
			}
		});

		jLabel2.setFont(new java.awt.Font("Kristen ITC", 1, 11)); // NOI18N
		jLabel2.setText("PowerMine?");

		jRadioButton3.setText("Yes,Display");
		jRadioButton3.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
			}
		});

		jLabel3.setFont(new java.awt.Font("Kristen ITC", 1, 11)); // NOI18N
		jLabel3.setText("Display Paint");

		jLabel4.setFont(new java.awt.Font("Kristen ITC", 1, 11)); // NOI18N
		jLabel4.setText("Coal And Mith Ore?");

		jRadioButton1.setText("Yes,Mine Both");
		jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
			}
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(ISGUI
				.getContentPane());
		ISGUI.getContentPane().setLayout(layout);
		layout
				.setHorizontalGroup(layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
								layout
										.createSequentialGroup()
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addGroup(
																layout
																		.createSequentialGroup()
																		.addGroup(
																				layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addGroup(
																								layout
																										.createSequentialGroup()
																										.addGap(
																												10,
																												10,
																												10)
																										.addComponent(
																										jRadioButton2))
																						.addGroup(
																						layout
																								.createSequentialGroup()
																								.addContainerGap()
																								.addComponent(
																								jLabel2)))
																		.addGap(
																				38,
																				38,
																				38)
																		.addGroup(
																				layout
																						.createParallelGroup(
																								javax.swing.GroupLayout.Alignment.LEADING)
																						.addComponent(
																								jLabel4)
																						.addComponent(
																						jRadioButton1,
																						javax.swing.GroupLayout.PREFERRED_SIZE,
																						108,
																						javax.swing.GroupLayout.PREFERRED_SIZE))
																		.addGap(
																		17,
																		17,
																		17))
														.addGroup(
														layout
																.createSequentialGroup()
																.addContainerGap()
																.addComponent(
																jButton1,
																javax.swing.GroupLayout.PREFERRED_SIZE,
																67,
																javax.swing.GroupLayout.PREFERRED_SIZE)))
										.addGap(1, 1, 1)
										.addGroup(
												layout
														.createParallelGroup(
																javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(jLabel3)
														.addGroup(
														layout
																.createParallelGroup(
																		javax.swing.GroupLayout.Alignment.TRAILING)
																.addComponent(
																		jButton2)
																.addComponent(
																jRadioButton3)))
										.addContainerGap(
										javax.swing.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)).addComponent(
						jLabel1,
						javax.swing.GroupLayout.Alignment.TRAILING,
						javax.swing.GroupLayout.DEFAULT_SIZE, 365,
						Short.MAX_VALUE));
		layout
				.setVerticalGroup(layout
						.createParallelGroup(
								javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(
						layout
								.createSequentialGroup()
								.addContainerGap()
								.addComponent(
										jLabel1,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										17,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(13, 13, 13)
								.addGroup(
										layout
												.createParallelGroup(
														javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(jLabel4)
												.addComponent(jLabel2)
												.addComponent(jLabel3))
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(
										layout
												.createParallelGroup(
														javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(
														jRadioButton2)
												.addComponent(
														jRadioButton3)
												.addComponent(
												jRadioButton1,
												javax.swing.GroupLayout.PREFERRED_SIZE,
												23,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGap(18, 18, 18)
								.addGroup(
										layout
												.createParallelGroup(
														javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(jButton1)
												.addComponent(jButton2))
								.addContainerGap(
								javax.swing.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)));

		ISGUI.pack();
	}// </editor-fold>

	public boolean onStart(Map<String, String> args) {
		initComponents();
		ISGUI.setVisible(true);
		if (isLoggedIn()) {
			lastRockLocation = new RSTile(0000, 0000);
			scriptStartTIME = System.currentTimeMillis();
			startXP = skills.getCurrentSkillExp(Skills.getStatIndex("mining"));
			startLVL = skills.getCurrentSkillLevel(Skills
					.getStatIndex("mining"));
			scriptstart = System.currentTimeMillis();
			rock = IsGetNObject(coalRockID);
			rock2 = IsGetNObject(mithRockID);
			finrockid = IsGetNObject(coalRockID);
			return true;
		} else {
			log("Please Start Script Logged In");
			return false;
		}
	}

	public int loop() {
		if (ISGUI.isVisible()) {
			return 1000;
		} else {
			quickchatrespond();
			if (isLoggedIn()) {
				RSObject booth = getNearestObjectByID(bankBoothID);
				// If character is lost it will tell it what to do
				if (distanceTo(new RSTile(3021, 3330)) <= 5) {
					if (tileOnMap(new RSTile(3020, 3338))) {
						IsTileMM(new RSTile(3020, 3338));
					}
				}


				if (getInventoryCount() == 27 && getMyPlayer().getAnimation() != -1 &&
						coalcenter.contains(getMyPlayer().getLocation().getX(),
						getMyPlayer().getLocation().getY())) {
					invantiban();
				}


				if (!isInventoryFull()
						&& topladderbig.contains(getMyPlayer().getLocation()
						.getX(), getMyPlayer().getLocation().getY())
						&& !topladder.contains(getMyPlayer().getLocation()
						.getX(), getMyPlayer().getLocation().getY())
						&& isLoggedIn() && NM()) {
					wait(random(40, 70));
					walktotopladder();
					return (random(100, 300));
				}

				if (isInventoryFull()
						&& coalcenter.contains(getMyPlayer().getLocation()
						.getX(), getMyPlayer().getLocation().getY())
						&& isLoggedIn() && !powermine) {
					wait(random(40, 70));
					walktobotladder();
					return (random(100, 300));
				}
				if (isInventoryFull()
						&& NM()
						&& isLoggedIn()
						&& tileOnMap(BankTile)
						&& bankp2.contains(getMyPlayer().getLocation().getX(),
						getMyPlayer().getLocation().getY())
						&& !bankp.contains(getMyPlayer().getLocation().getX(),
						getMyPlayer().getLocation().getY())) {
					// wait(random(40,70));
					IsTileMM(BankTile);
					return (random(100, 300));
				}
				if (!isInventoryFull()
						&& NM()
						&& isLoggedIn()
						&& tileOnMap(nearbotladder)
						&& botladder22.contains(getMyPlayer().getLocation()
						.getX(), getMyPlayer().getLocation().getY())
						&& !botladder.contains(getMyPlayer().getLocation()
						.getX(), getMyPlayer().getLocation().getY())) {
					// wait(random(40,70));
					IsTileMM(nearbotladder);
					return (random(100, 300));
				}

				if (isInventoryFull() && isLoggedIn() && bank.isOpen()) {
					// wait(random(40,70));
					deposit();
					return (random(100, 300));
				}

				if (isInventoryFull()
						&& isLoggedIn()
						&& !bank.isOpen()
						&& bankp.contains(getMyPlayer().getLocation().getX(),
						getMyPlayer().getLocation().getY())) {
					// wait(random(40,70));
					bankore();
					return (random(100, 300));
				}
				if (booth != null) {
					if (tileOnScreen(booth.getLocation()) && isInventoryFull()) {
						bankore();
						wait(random(500, 650));
					}
				}
				if (!isInventoryFull()
						&& isLoggedIn()
						&& NM()
						&& bankp.contains(getMyPlayer().getLocation().getX(),
						getMyPlayer().getLocation().getY())) {
					// wait(random(40,70));
					walktotopladder();
					// wait(random(100,300));
					return (random(100, 300));
				}

				if (!isInventoryFull()
						&& isLoggedIn()
						&& topladder.contains(getMyPlayer().getLocation()
						.getX(), getMyPlayer().getLocation().getY())) {
					wait(random(40, 70));
					godown();
					return (random(100, 300));
				}

				if (isInventoryFull()
						&& isLoggedIn()
						&& NM()
						&& topladder.contains(getMyPlayer().getLocation()
						.getX(), getMyPlayer().getLocation().getY())) {
					wait(random(800, 850));
					walktobank();
					return (random(100, 300));
				}

				if (!isInventoryFull()
						&& isLoggedIn()
						&& center.contains(getMyPlayer().getLocation().getX(),
						getMyPlayer().getLocation().getY())) {
					walktotopladder();
					return (random(100, 300));
				}

				if (isInventoryFull()
						&& isLoggedIn()
						&& center.contains(getMyPlayer().getLocation().getX(),
						getMyPlayer().getLocation().getY())) {
					walktobank();
					return (random(600, 800));
				}

				if (isInventoryFull()
						&& isLoggedIn()
						&& botladder.contains(getMyPlayer().getLocation()
						.getX(), getMyPlayer().getLocation().getY())) {
					goup();
					return (random(500, 550));
				}

				if (!isInventoryFull()
						&& isLoggedIn()
						&& NM()
						&& botladder.contains(getMyPlayer().getLocation()
						.getX(), getMyPlayer().getLocation().getY())) {
					wait(100);
					walktocoal();
					lastRockLocation = new RSTile(0000, 0000);
					return (random(100, 300));
				}
				if (powermine && isInventoryFull()) {
					dropinv();
					return (random(100, 300));
				}
				if (getEnergy() >= random(40, 90) && !isRunning()) {
					setRun(true);
					return 1000;
				}
				if (getMyPlayer().getAnimation() != -1) {
					timeIdle = System.currentTimeMillis();
				}
				if (getMyPlayer().getAnimation() == -1
						&& (System.currentTimeMillis() - timeIdle >= 7000)) {
					lastRockLocation = new RSTile(0000, 0000);
					timeIdle = System.currentTimeMillis();
				}
				while (!NeedNewRock() && !isInventoryFull() && NM()) {
					wait(random(300, 400));
					HoverOverRock();
					if (NeedNewRock()) {
						break;
					}
				}

				antiBan();
				return Mine();
			} else {
				wait(random(20000, 30000));
				log("You Arent Logged In. Please Log In");
				return 500;
			}
		}
	}

	public void onRepaint(Graphics g) {
		if (!showpaint) {
			return;
		}
		if (!isLoggedIn()) {
			g.setColor(new Color(0, 0, 0, 175));
			g.fillRoundRect(1, 62, 125, 215, 10, 10);
			g.setColor(Color.WHITE);
			g.drawString("~ Paint Stopped ~", 8, 77);
			g.drawString("Please Log In", 18, 104);
			abs2 = System.currentTimeMillis() - runTime - scriptStartTIME;
			return;
		}
		long seconds;
		long minutes = 0;
		long hours = 0;

		if (isLoggedIn()) {
			runTime = System.currentTimeMillis() - abs2 - scriptStartTIME;
			seconds = runTime / 1000;
			if (seconds >= 60) {
				minutes = seconds / 60;
				seconds -= (minutes * 60);
			}
			if (minutes >= 60) {
				hours = minutes / 60;
				minutes -= (hours * 60);
			}

			final int currentXP = skills.getCurrentSkillExp(Skills
					.getStatIndex("mining"));
			final int currentLVL = skills.getCurrentSkillLevel(Skills
					.getStatIndex("mining"));
			final int nextlvl = skills.getPercentToNextLevel(Skills
					.getStatIndex("mining"));
			final int fill = (int) (.8 * (double) nextlvl);
			;
			final int XPgained = currentXP - startXP;
			final int LVLgained = currentLVL - startLVL;
			final int mithxp = mithbanked * 80;
			final int coalxpgained = XPgained - mithxp;
			if (miningMith) {
				coalbanked = coalxpgained / 50;
			} else {
				coalbanked = XPgained / 50;
			}
			if (System.currentTimeMillis() - scriptstart >= 8000
					&& isLoggedIn()) {
				coalperh = (int) ((3600000.0 / (double) runTime) * coalbanked);
				xpperh = (int) ((3600000.0 / (double) runTime) * XPgained);
				scriptstart = System.currentTimeMillis();
			}

			g.setColor(new Color(0, 0, 0, 175));
			g.fillRoundRect(1, 62, 125, 230, 10, 10);
			g.setColor(Color.WHITE);
			g.drawString("Iscreams Guild Miner", 5, 77);
			g.drawString("Version: " + Double.toString(version), 5, 105);
			g.drawString("Run Time: " + hours + ":" + minutes + ":" + seconds,
					5, 132);

			g
					.drawString("Rocks Per Hour:" + Integer.toString(coalperh),
							5, 159);
			g.drawString("XP Per Hour:" + Integer.toString(xpperh), 5, 174);

			g
					.drawString("Levels Gained:" + Integer.toString(LVLgained),
							5, 201);
			g.drawString("XP Gained:" + Integer.toString(XPgained), 5, 216);

			g.setColor(Color.cyan);
			g.fillRect(5, 219, 80, 13);
			g.setColor(Color.blue);
			g.fillRect(5, 219, fill, 13);
			if (fill > 20) {
				g.setColor(Color.white);
			} else {
				g.setColor(Color.black);
			}
			g.drawString(Integer.toString(nextlvl) + "%", 5, 231);
			g.setColor(Color.magenta);
			g.drawRect(5, 219, 80, 13);
			g.setColor(Color.white);
			g.drawString("Coal Mined:" + Integer.toString(coalbanked), 5, 258);
			g.drawString("Mith Mined:" + Integer.toString(mithbanked), 5, 273);
			g.drawString("Gems Found:" + Integer.toString(gemsbanked), 5, 288);
		}
	}

	public int HoverOverRock() {
		// hovers over a rock

		hoverantiban();
		if (getMyPlayer().getAnimation() != -1) {
			timeIdle = System.currentTimeMillis();
		}
		if (getMyPlayer().getAnimation() == -1
				&& (System.currentTimeMillis() - timeIdle >= 5000)) {
			lastRockLocation = new RSTile(0000, 0000);
			timeIdle = System.currentTimeMillis();
		}
		if (!isInventoryFull() && !NeedNewRock() && getInventoryCount() != 27) {
			try {
				rock = IsGetNObject(coalRockID);
				rock2 = IsGetNObject(mithRockID);
				if (rock2 != null && rock2.getLocation() != null && tileOnScreen(rock2.getLocation()) && miningMith) {
					Rock = rock2.getLocation();
					final Point location = Calculations.tileToScreen(Rock);
					final Point mouse = getMouseLocation();
					if (location.x == -1 || location.y == -1) {
						return 500;
					}
					if (Math.abs(location.x - mouse.x) <= 12
							|| Math.abs(location.y - mouse.y) <= 12) {
						return 500;
					} else {
						if (!isInventoryFull() && !isplayerunderm(rock2)) {
							moveMouse(location.x, location.y);
							// mine = false;
							// wait(random(500,1000));
						} else {
							return 100;
						}
					}
				} else {
					if (rock != null && rock.getLocation() != null && rock2.getLocation() != null && tileOnScreen(rock.getLocation())
							&& !tileOnScreen(rock2.getLocation())) {
						Rock = rock.getLocation();
						final Point location = Calculations.tileToScreen(Rock);
						final Point mouse = getMouseLocation();
						if (location.x == -1 || location.y == -1) {
							return 500;
						}
						if (Math.abs(location.x - mouse.x) <= 12
								|| Math.abs(location.y - mouse.y) <= 12) {
							return 500;
						} else {
							if (!isInventoryFull() && !isplayerunderm(rock)) {
								moveMouse(location.x, location.y);
								// mine = false;
								// wait(random(500,1000));
							} else {
								return 100;
							}
						}
					}
					return 100;
				}
			} catch (final Exception e) {
				Mine();
			}
		}

		return 100;
	}

	public void quickchatrespond() {
		String name;
		String secondname;
		int r;
		if (isLoggedIn()) {
			secondname = getMyPlayer().getName();
			name = secondname + ": <col=0000ff>";
			r = random(1, 2);
		} else {
			return;
		}
		if (RSInterface.getInterface(550).getChild(6).isValid() && check) {
			if (RSInterface.getInterface(550).getChild(6).containsText("96") ||
					RSInterface.getInterface(550).getChild(6).containsText("160") ||
					RSInterface.getInterface(550).getChild(6).containsText("161")) {
				if (RSInterface.getInterface(137).containsText(name + "My Mining level is")) {
				} else {
					if (RSInterface.getInterface(137).containsText("What is your level in Mining?")) {
						check = false;
						sendText("", true);
						wait(random(400, 800));
						sendText("S", false);
						wait(random(400, 800));
						sendText("I", false);
						wait(random(400, 800));
						sendText("2", false);
						wait(random(400, 800));
						check = true;
					}
				}

				if (RSInterface.getInterface(137).containsText(name + "That rocks!") ||
						RSInterface.getInterface(137).containsText(name + "That's good.") ||
						RSInterface.getInterface(137).containsText(name + "My Mining level is")) {
				} else {
					if (RSInterface.getInterface(137).containsText("My Mining level is")) {
						if (r == 1) {
							check = false;
							sendText("", true);
							wait(random(400, 800));
							sendText("g", false);
							wait(random(400, 800));
							sendText("c", false);
							wait(random(400, 800));
							sendText("g", false);
							wait(random(400, 800));
							sendText("1", false);
							wait(random(400, 800));
							check = true;
						}
						if (r == 2) {
							check = false;
							sendText("", true);
							wait(random(400, 800));
							sendText("g", false);
							wait(random(400, 800));
							sendText("c", false);
							wait(random(400, 800));
							sendText("e", false);
							wait(random(400, 800));
							sendText("9", false);
							wait(random(400, 800));
							check = true;
						}
					}
				}

				if (RSInterface.getInterface(137).containsText(name + "That rocks!") ||
						RSInterface.getInterface(137).containsText(name + "That's good.")) {
				} else {
					if (RSInterface.getInterface(137).containsText("experience to get my next Mining level.")) {
						if (r == 1) {
							check = false;
							sendText("", true);
							wait(random(400, 800));
							sendText("g", false);
							wait(random(400, 800));
							sendText("c", false);
							wait(random(400, 800));
							sendText("g", false);
							wait(random(400, 800));
							sendText("1", false);
							wait(random(400, 800));
							check = true;
						}
						if (r == 2) {
							check = false;
							sendText("", true);
							wait(random(400, 800));
							sendText("g", false);
							wait(random(400, 800));
							sendText("c", false);
							wait(random(400, 800));
							sendText("e", false);
							wait(random(400, 800));
							sendText("9", false);
							wait(random(400, 800));
							check = true;
						}
					}
				}
			}
		}
	}

	public void CheckMiningSkill() {
		// checks mining skill
		if (!NeedNewRock()) {
			openTab(TAB_STATS);
			RSInterfaceChild one = RSInterface.getInterface(320).getChild(9);
			RSInterfaceChild two = RSInterface.getInterface(320).getChild(10);
			if (random(0, 2) == 0) {
				moveMouse(new Point(one.getAbsoluteX()
						+ random(2, one.getWidth() - 1), one.getAbsoluteY()
						+ random(2, one.getHeight() - 1)));
				if (!NeedNewRock()) {
					wait(random(1000, 2000));
				}
			} else {
				moveMouse(new Point(two.getAbsoluteX()
						+ random(2, two.getWidth() - 1), two.getAbsoluteY()
						+ random(2, two.getHeight() - 1)));
				if (!NeedNewRock()) {
					wait(random(1000, 2000));
				}
			}
		}
	}

	public boolean checkunderm() {
		// makes sure that a proper unmined rock is under the mouse
		final Point p = getMouseLocation();
		if (!pointOnScreen(p)) {
			return false;
		}
		randoma = getTileUnderMouse();
		if (randoma != null) {
			objrand = getObjectAt(randoma);
		}
		if (objrand == null) {
			return false;
		} else {
			if (randoma != null) {
				for (int aCoalRockID : coalRockID) {
					if (objrand.getID() == aCoalRockID)
						// lastRockLocation = obj.getLocation();
						if (rock != null && objrand.getLocation() != null &&
								rock.getLocation() != null && distanceBetween(randoma, rock.getLocation()) < 2
								|| finrockid != null && objrand.getLocation() != null &&
								finrockid.getLocation() != null && distanceBetween(objrand.getLocation(),
								finrockid.getLocation()) < 2) {
							if (NeedNewRock() && isrock(objrand)) {
								lastRockLocation = objrand.getLocation();
								return true;
							}
						}
				}
				for (int aMithRockID : mithRockID) {
					if (objrand.getID() == aMithRockID)
						if (rock2 != null && objrand.getLocation() != null &&
								rock2.getLocation() != null && distanceBetween(randoma, rock2.getLocation()) < 2
								|| finrockid != null && objrand.getLocation() != null &&
								finrockid.getLocation() != null && distanceBetween(objrand.getLocation(),
								finrockid.getLocation()) < 2) {
							if (NeedNewRock() && isrock(objrand)) {
								lastRockLocation = objrand.getLocation();
								return true;
							}
						}
				}

			}
		}

		return false;

	}

	public boolean isrock(RSObject c) {
		if (c == null) {
			return false;
		}
		for (int aMithRockID : mithRockID) {
			if (c.getID() == aMithRockID) {
				return true;
			}
		}
		for (int aCoalRockID : coalRockID) {
			if (c.getID() == aCoalRockID) {
				return true;
			}
		}
		return false;
	}

	public int Mine() {
		if (isLoggedIn()) {
			// performs mining and hovering
			mith = IsGetNObject(mithRockID);
			coal = IsGetNObject(coalRockID);
			rock = IsGetNObject(coalRockID);
			rock2 = IsGetNObject(mithRockID);
			//a = getMenuItems();
			if (getTileUnderMouse() != null) {
				randoma = getTileUnderMouse();
			}
			if (randoma == null) {
			} else {
				objrand = getObjectAt(randoma);
			}
			if (!isInventoryFull()
					&& coalcenter.contains(getMyPlayer().getLocation().getX(),
					getMyPlayer().getLocation().getY())) {

				if (getMyPlayer().getAnimation() != -1) {
					timeIdle = System.currentTimeMillis();
				}

				if (miningMith && mith != null && mith.getLocation() != null
						&& tileOnScreen(mith.getLocation())) {
					finrockid = mith;
				} else {
					if (coal != null && coal.getLocation() != null && tileOnScreen(coal.getLocation())) {
						finrockid = coal;
					}
				}
				if (coal != null && mith != null && coal.getLocation() != null &&
						mith.getLocation() != null && !tileOnScreen(coal.getLocation())
						&& !tileOnScreen(mith.getLocation()) || coal == null && mith == null) {
					walkmid();
					lastRockLocation = new RSTile(0000, 0000);
					wait(random(1300, 1700));
					return 100;
				}
				if (finrockid != null && finrockid.getLocation() != null && tileOnScreen(finrockid.getLocation())) {
					if (NeedNewRock()) {
						if (randoma != null && checkunderm()) {
							//if (checkunderm() && randoma != null) {
							if (checkunderm() && !isplayerunderm(objrand) && isrock(objrand)) {
								Point d = getMouseLocation();
								moveMouse(d.x + random(-3, 3), d.y
										+ random(-3, 3));
								clickMouse(true);
								mine = true;
								antiBan();
								wait(random(600, 900));
								while (!NM()) {
									HoverOverRock();
									if (getMyPlayer().getAnimation() != -1) {
										HoverOverRock();
										break;
									}
								}
								//if (NM()) {
								wait(random(600, 900));
								HoverOverRock();
								//}
							} else {
								if (!isplayerunderm(objrand)) {
									if (finrockid != null && finrockid.getLocation() != null &&
											isrock(finrockid) && atTile(finrockid.getLocation(), "mine")) {
										lastRockLocation = finrockid.getLocation();
										mine = true;
										antiBan();
										wait(random(600, 900));
										while (!NM()) {
											HoverOverRock();
											if (getMyPlayer().getAnimation() != -1) {
												HoverOverRock();
												break;
											}
										}
										//if (NM()) {
										wait(random(600, 900));
										HoverOverRock();
										//}
									} else {
										mine = true;
										lastRockLocation = new RSTile(0000, 0000);
									}
								}
							}
						} else {
							if (finrockid != null && finrockid.getLocation() != null &&
									isrock(finrockid) && atTile(finrockid.getLocation(), "mine")) {
								mine = true;
								lastRockLocation = finrockid.getLocation();
								antiBan();
								wait(random(600, 900));
								while (!NM()) {
									HoverOverRock();
									if (getMyPlayer().getAnimation() != -1) {
										HoverOverRock();
										break;
									}
								}

								//if (NM()) {
								wait(random(600, 900));
								HoverOverRock();
								//}
							}
						}
					}
				}
			}
			if (getObjectAt(lastRockLocation) == null
					&& (System.currentTimeMillis() - timeIdle >= 6000)) {
				lastRockLocation = new RSTile(0000, 0000);
				timeIdle = System.currentTimeMillis();
				mine = true;
				Mine();
				return 1000;
			}
			if (getMyPlayer().getAnimation() == -1
					&& (System.currentTimeMillis() - timeIdle >= 6000)
					&& !getMyPlayer().isMoving()) {
				if (getMyPlayer().getAnimation() == -1) {
					mine = true;
					lastRockLocation = new RSTile(0000, 0000);
				}
				timeIdle = System.currentTimeMillis();
				Mine();
				return 1000;
			}
		}

		return 1000;
	}

	public void walktotopladder() {
		// walking to ladder
		RSTile v = randomizetile(NearLadder);
		RSTile x = randomizetile(InBeet);
		RSTile n = randomizetile(nearbank);
		if (!topladder.contains(getMyPlayer().getLocation().getX(),
				getMyPlayer().getLocation().getY())
				&& !tileOnScreen(new RSTile(3020, 3338))) {
			if (tileOnMap(v)
					&& bankp.contains(getMyPlayer().getLocation().getX(),
					getMyPlayer().getLocation().getY()) && NM()
					|| distanceTo(InBeet) <= 5 && (System.currentTimeMillis() - idle >= 5000)) {
				if (tileOnMap(v)) {
					IsTileMM(v);
					wait(random(1000, 1200));
					idle = System.currentTimeMillis();
				}
			} else {
				if (tileOnMap(x) && !tileOnMap(v) && NM()) {
					IsTileMM(x);
					wait(random(800, 1200));
				}
				if (!tileOnMap(x)
						&& !tileOnMap(v)
						&& NM()
						&& bankp.contains(getMyPlayer().getLocation().getX(),
						getMyPlayer().getLocation().getY())) {
					IsTileMM(n);
				}
				if (!tileOnMap(x)
						&& !tileOnMap(n)
						&& tileOnMap(v)
						&& NM()
						&& topladderbig.contains(getMyPlayer().getLocation()
						.getX(), getMyPlayer().getLocation().getY())) {
					IsTileMM(v);
				}
				wait(random(200, 300));
			}
		}
		walkingantiban();
	}

	public boolean checkbanktile() {
		RSObject booth = getNearestObjectByID(bankBoothID);
		if (booth != null) {
			if (booth.getLocation() != null &&
					!tileOnScreen(booth.getLocation())) {
				return true;
			}
		}
		return false;
	}

	public void walktobank() {
		// walks to the bank
		RSTile z = randomizetile(BankTile);
		RSTile x = randomizetile(InBeet);
		RSTile c = randomizetile(neartopladder);
		RSTile v = randomizetile(new RSTile(3030, 3348));
		if (!bankp.contains(getMyPlayer().getLocation().getX(), getMyPlayer()
				.getLocation().getY())
				&& checkbanktile()) {

			if (tileOnMap(z)
					&& topladder.contains(getMyPlayer().getLocation().getX(),
					getMyPlayer().getLocation().getY()) && NM()
					|| inrnge(InBeet) && (System.currentTimeMillis() - idle >= 7000)) {
				if (tileOnMap(z)) {
					IsTileMM(z);
					wait(random(600, 700));
					idle = System.currentTimeMillis();
				}
			} else {
				if (tileOnMap(x) && !tileOnMap(z) && NM()) {
					if (tileOnMap(x)) {
						IsTileMM(x);
					}
				}
				if (!tileOnMap(x)
						&& !tileOnMap(z)
						&& tileOnMap(c)
						&& NM()
						&& topladderbig.contains(getMyPlayer().getLocation()
						.getX(), getMyPlayer().getLocation().getY())) {
					IsTileMM(c);
				}
				if (!tileOnMap(x)
						&& !tileOnMap(z)
						&& NM()
						&& !topladderbig.contains(getMyPlayer().getLocation()
						.getX(), getMyPlayer().getLocation().getY())
						&& tileOnMap(v)) {
					IsTileMM(v);
				}
			}
			wait(random(200, 300));
		}
		walkingantiban();
	}

	public void deposit() {
		// deposits inventory
		if (isInventoryFull() && bank.isOpen() && isLoggedIn()) {
			int rando = getInventoryCount(axeIDs);
			wait(random(20, 40));
			if (rando >= 1 && isInventoryFull()) {
				mithbanked = mithbanked + getInventoryCount(447);
				gemsbanked = gemsbanked + getInventoryCount(gemIDs);
				bank.depositAllExcept(axeIDs);
				wait(random(400, 800));
			} else {
				if (random(1, 15) == 15) {
					mithbanked = mithbanked + getInventoryCount(447);
					gemsbanked = gemsbanked + getInventoryCount(gemIDs);
					bank.depositAllExcept(axeIDs);
					wait(random(400, 800));
				} else {
					bank.depositAll();
					mithbanked = mithbanked + getInventoryCount(447);
					gemsbanked = gemsbanked + getInventoryCount(gemIDs);
					wait(random(400, 800));
				}
			}
		}
	}

	public void bankore() {
		// clicks on bankers
		int b = random(1, 6);
		RSNPC banker = getNearestFreeNPCByID(6200);
		RSObject booth = getNearestObjectByID(bankBoothID);

		if (!bankp.contains(getMyPlayer().getLocation().getX(), getMyPlayer()
				.getLocation().getY())
				&& isInventoryFull() && checkbanktile() && isLoggedIn()) {
			walktobank();
		} else {

			if (bankp.contains(getMyPlayer().getLocation().getX(),
					getMyPlayer().getLocation().getY())
					&& isInventoryFull()
					&& isLoggedIn()
					|| checkbanktile()
					&& isInventoryFull() && isLoggedIn()) {

				if (b == 1 && banker != null && tileOnScreen(banker.getLocation())) {
					if (banker.getLocation() != null && atTile(banker.getLocation(), "Bank")) {
						wait(random(1000, 1200));
					} else {
						if (random(0, 5) < random(2, 4)) {
							setCameraRotation(getCameraAngle()
									+ (random(0, 9) < random(6, 8) ? random(-20, 20)
									: random(-360, 360)));
						}
					}
				} else {
					if (booth != null && booth.getLocation() != null && tileOnScreen(booth.getLocation())) {
						if (atTile(booth.getLocation(), "quick")) {
							wait(random(1000, 1200));
							while (!NM()) {
								wait(random(100, 200));
							}
						} else {
							if (random(0, 5) < random(2, 4)) {
								setCameraRotation(getCameraAngle()
										+ (random(0, 9) < random(6, 8) ? random(-20, 20)
										: random(-360, 360)));
							}
						}
					}
				}
			}
		}
	}

	public void godown() {
		// goes down ladder
		RSObject ladder2 = getNearestObjectByID(2113);
		if (!isInventoryFull() && distanceTo(Ladder) < 10 && ladder2 != null
				&& ladder2.getLocation() != null && isLoggedIn() && tileOnScreen(ladder2.getLocation())) {
			if (atLadder()) {
				wait(random(300, 301));
			} else {

				if (random(0, 5) < random(2, 4)) {
					setCameraRotation(getCameraAngle()
							+ (random(0, 9) < random(6, 8) ? random(-20, 20)
							: random(-360, 360)));
				}
				wait(random(700, 900));
			}
			if (distanceTo(Ladder) > 8) {
				move();
			}
		} else {
			move();
		}
	}

	public void goup() {
		RSObject ladder = getNearestObjectByID(30941);
		// goes up ladder
		if (distanceTo(CLadder) < 10 && ladder != null && ladder.getLocation() != null && isLoggedIn()
				&& tileOnScreen(ladder.getLocation()) && isInventoryFull()) {
			if (atObject(ladder, "Climb")) {
				wait(random(2000, 2200));
			} else {
				if (random(0, 5) < random(2, 4)) {
					setCameraRotation(getCameraAngle()
							+ (random(0, 9) < random(6, 8) ? random(-20, 20)
							: random(-360, 360)));
				}
				wait(random(1500, 1600));
			}
		} else {
			move();
		}
	}

	public void walktobotladder() {
		// walks to the bottum ladder in the mine if inventory is full
		RSTile m = randomizetile(botladder2);
		RSTile d = randomizetile(centercoal);
		RSTile s = randomizetile(nearbotladder);

		if (!botladder.contains(getMyPlayer().getLocation().getX(),
				getMyPlayer().getLocation().getY())) {
			if (tileOnMap(m)
					&& coalcenter.contains(getMyPlayer().getLocation().getX(),
					getMyPlayer().getLocation().getY()) && NM()
					|| inrnge(nearbotladder) && (System.currentTimeMillis() - idle2 >= 5000)) {
				if (tileOnMap(m)) {
					IsTileMM(m);
					wait(random(800, 900));
				}
				idle2 = System.currentTimeMillis();
			} else {
				if (tileOnMap(d) && !tileOnMap(m) && !tileOnMap(s) && NM()) {
					IsTileMM(d);
					wait(random(600, 900));
				}
				if (tileOnMap(s) && !tileOnMap(m) && NM() || inrnge(centercoal) && (System.currentTimeMillis() - idle >= 5000)) {
					if (tileOnMap(s)) {
						IsTileMM(s);
						idle = System.currentTimeMillis();
						wait(random(600, 900));
					}
				}
				if (!tileOnMap(s) && !tileOnMap(m) && !tileOnMap(d) && NM()) {
					if (tileOnMap(new RSTile(3046, 9743))) {
						IsTileMM(new RSTile(3046, 9743));
						wait(random(600, 900));
					}
				}
			}
		}
		wait(random(100, 200));
		walkingantiban();
	}

	public void walktocoal() {
		// walks to coal if at the bottum ladder
		RSTile f = randomizetile(nearesttoladder);
		RSTile g = randomizetile(centercoal);
		RSTile h = randomizetile(nearbotladder);
		if (!coalcenter.contains(getMyPlayer().getLocation().getX(),
				getMyPlayer().getLocation().getY())) {
			if (tileOnMap(g)
					&& botladder.contains(getMyPlayer().getLocation().getX(),
					getMyPlayer().getLocation().getY()) && NM()
					|| inrnge(nearbotladder)) {
				if (tileOnMap(g)) {
					IsTileMM(g);
					wait(random(600, 900));
				}
			} else {

				if (tileOnMap(h) && !tileOnMap(g) && NM()) {
					if (tileOnMap(h)) {
						IsTileMM(h);
						if (random(1, 3) > 2) {
							wait(random(1200, 1400));
							if (tileOnMap(g)) {
								IsTileMM(g);
								wait(random(600, 900));
							}
						} else {
							wait(random(600, 900));
						}
					}
				}

			}
			if (!tileOnMap(f)
					&& !tileOnMap(g)
					&& NM()
					&& botladder.contains(getMyPlayer().getLocation().getX(),
					getMyPlayer().getLocation().getY())) {
				if (tileOnMap(f)) {
					IsTileMM(f);
					wait(random(600, 900));
				}
			}
		}
		wait(random(500, 600));
		if (!NM()) {
			if (random(1, 3) == 1) {
				if (tileOnMap(g)) {
					IsTileMM(g);
					wait(random(600, 900));
				}
			}
			wait(random(200, 400));
		}
		Mine();
		//walkingantiban();
	}

	public RSTile randomizetile(RSTile a) {
		int x = a.getX() + random(-3, 2);
		int y = a.getY() + random(-3, 2);
		return new RSTile(x, y);
	}

	public boolean IsTileMM(final RSTile t) {
		// Iscreams tile to minimap clicker/runner
		return IsTileMM(t, 2, 2);
	}

	public boolean IsTileMM(final RSTile t, final int x, final int y) {
		// Iscreams tile to minimap clicker
		final Point p = tileToMinimap(t);
		if (p.x == -1 || p.y == -1) {
			return IsTileMM(getClosestTileOnMap(t), x, y);
		}
		clickMouse(p, x, y, true);
		return true;
	}

	public boolean atLadder() {
		// perfect ladder clicking at the mining guild
		if (getCameraAngle() > 0 && getCameraAngle() < 45) {
			// south
			randoma2 = new RSTile(3019, 3338);
			if (tileOnScreen(randoma2)) {
				randomb = Calculations.tileToScreen(randoma2);

				if (randomb.x != -1 || randomb.y != -1) {
					moveMouse(randomb.x, randomb.y + random(3, 5));
				}
			}
		}
		if (getCameraAngle() > 45 && getCameraAngle() < 135) {
			// east
			randoma2 = new RSTile(3020, 3339);
			if (tileOnScreen(randoma2)) {
				randomb = Calculations.tileToScreen(randoma2);

				if (randomb.x != -1 || randomb.y != -1) {
					moveMouse(randomb.x, randomb.y + random(3, 5));
				}
			}
		}
		if (getCameraAngle() > 135 && getCameraAngle() < 225) {
			// north
			randoma2 = new RSTile(3019, 3340);
			if (tileOnScreen(randoma2)) {
				randomb = Calculations.tileToScreen(randoma2);

				if (randomb.x != -1 || randomb.y != -1) {
					moveMouse(randomb.x, randomb.y + random(3, 5));
				}
			}
		}
		if (getCameraAngle() > 225 && getCameraAngle() < 315) {
			// west
			randoma2 = new RSTile(3018, 3339);
			if (tileOnScreen(randoma2)) {
				randomb = Calculations.tileToScreen(randoma2);

				if (randomb.x != -1 || randomb.y != -1) {
					moveMouse(randomb.x, randomb.y + random(3, 5));
				}
			}
		}
		if (getCameraAngle() > 315) {
			// south
			randoma2 = new RSTile(3019, 3338);
			if (tileOnScreen(randoma2)) {
				randomb = Calculations.tileToScreen(randoma2);

				if (randomb.x != -1 || randomb.y != -1) {
					moveMouse(randomb.x, randomb.y + random(3, 5));
				}
			}
		}

		if (atMenu("Climb")) {
			wait(random(2500, 3000));
			return true;
		} else {
			return false;
		}
	}

	public int antiBan() {
		// random movements
		Point d = getMouseLocation();
		switch (random(0, 100)) {

			case 0:
				moveMouse(d.x - 1, d.y - 1, 12, 12);
				break;
			case 1:
				moveMouse(d.x - 1, d.y - 1, 12, 12);
				break;
			case 2:
				moveMouse(d.x - 1, d.y - 1, 12, 12);
				break;
			case 3:
				moveMouse(d.x - 1, d.y - 1, 12, 12);
				break;
			case 4:
				moveMouse(d.x - 1, d.y - 1, 12, 12);
				break;
			case 5:
				if (!NeedNewRock()) {
					setCameraRotation(random(1, 359));
				}
				break;
			case 6:
				moveMouse(d.x - 1, d.y - 1, 12, 12);
				break;
			case 7:
				moveMouse(d.x - 1, d.y - 1, 12, 12);
				break;
			case 8:
				moveMouse(d.x - 1, d.y - 1, 12, 12);
				break;
			case 9:
				moveMouse(d.x - 1, d.y - 1, 12, 12);
				break;
			case 10:
				moveMouse(d.x - 1, d.y - 1, 12, 12);
				break;
			case 11:
				setCameraRotation(random(1, 359));
				break;
			case 12:
				if (!NeedNewRock()) {
					setCameraRotation(random(1, 359));
				}
				break;
			case 13:
				CheckMiningSkill();
				break;
			case 14:
				if (!NeedNewRock()) {
					setCameraRotation(random(1, 359));
				}
				break;
			case 15:
				if (!NeedNewRock()) {
					setCameraRotation(random(1, 359));
				}
				break;
			case 16:
				moveMouse(random(5, 300), random(5, 300), 12, 12);
				break;
			case 17:
				moveMouse(random(5, 300), random(5, 300), 12, 12);
				break;

			default:
				break;
		}
		return random(50, 200);
	}

	public void hoverantiban() {
		// acts as a human like anti ban while hovering
		if (random(0, 2500) <= 1) {
			Point d = getMouseLocation();
			moveMouse(d.x - 1, d.y - 1, 12, 12);
		}
	}


	public void walkingantiban() {
//Moves mouse near the minimap
		Point d = getMouseLocation();
		switch (random(0, 40)) {

			case 0:
				moveMouse(d.x - 1, d.y - 1, 17, 17);
				break;
			case 1:
				moveMouse(d.x - 1, d.y - 1, 17, 17);
				break;
			case 2:
				moveMouse(d.x - 1, d.y - 1, 17, 17);
				break;
			case 3:
				moveMouse(d.x - 1, d.y - 1, 17, 17);
				break;
			case 4:
				moveMouse(d.x - 1, d.y - 1, 17, 14);
				break;
		}
	}

	public void invantiban() {
		//Moves mouse near the minimap
		if ((System.currentTimeMillis() - idle4 >= 10000)) {
			moveMouse(634, 80, 100, 100);
			idle4 = System.currentTimeMillis();
		}
	}

	public void move() {
		RSObject ladder = getNearestObjectByID(30941);
		RSObject ladder2 = getNearestObjectByID(2113);
		if (ladder2 != null) {
			if (topladder.contains(getMyPlayer().getLocation().getX(),
					getMyPlayer().getLocation().getY())
					&& !tileOnScreen(ladder2.getLocation())) {
				IsTileMM(new RSTile(3021, 3339));
			}
		}
		if (ladder != null) {
			if (botladder.contains(getMyPlayer().getLocation().getX(),
					getMyPlayer().getLocation().getY())
					&& !tileOnScreen(ladder.getLocation())) {
				IsTileMM(new RSTile(3021, 3739));
			}
		}
	}

	public RSObject IsGetNObject(final int... ids) {
		// Iscreams find nearest free object
		RSObject cur = null;
		double dist = -1;
		for (int x = 0; x < 104; x++) {
			for (int y = 0; y < 104; y++) {
				final RSObject o = getObjectAt(x + Bot.getClient().getBaseX(),
						y + Bot.getClient().getBaseY());
				if (o != null) {
					boolean isObject = false;
					for (final int id : ids) {
						if (o.getID() == id) {
							isObject = true;
							break;
						}
					}
					if (isObject) {
						final double distTmp = calculateDistance(getMyPlayer()
								.getLocation(), o.getLocation());
						if (cur == null
								&& distanceBetween(o.getLocation(),
								lastRockLocation) != 0
								&& !isplayerunderm(o)) {
							dist = distTmp;
							cur = o;
						} else if (distTmp < dist
								&& distanceBetween(o.getLocation(),
								lastRockLocation) != 0
								&& !isplayerunderm(o)) {
							cur = o;
							dist = distTmp;
						}
					}
				}
			}
		}
		return cur;
	}

	public int dropinv() {
		// drops inventory
		IsdropAllExcept(996);
		return 100;
	}

	public boolean IsdropAllExcept(final int... items) {
		// Iscreams way of dropping
		dropAllExcept(false, items);
		return true;
	}

	public boolean NM() {
		// Character Not Moving..returns true
		return !getMyPlayer().isMoving();
	}

	public boolean inrnge(RSTile t1) {
		// returns true if the tile is in range to myplayer()
		return distanceTo(t1) <= 4;
	}

	public boolean NeedNewRock() {
		// decides if a new rock must be clicked or not
		// Animation checking doesnt work anymore because it resets constantly
		RSObject obj = getObjectAt(lastRockLocation);
		if (lastRockLocation == new RSTile(0000, 0000)) {
			return true;
		}
		if (obj == null) {
			return true;
		} else {
			for (int aCoalRockID : coalRockID) {
				if (obj.getID() == aCoalRockID)
					return false;
			}
			for (int aMithRockID : mithRockID) {
				if (obj.getID() == aMithRockID)
					return false;
			}
		}

		return true;
	}

	public boolean isrockmith(RSObject b) {
		if (b == null) {
			return false;
		}
		for (int aMithRockID : mithRockID) {
			if (b.getID() == aMithRockID) {
				return true;
			}
		}
		return false;
	}

	public boolean isplayerunderm(RSObject a) {
		final int[] validPlayers = Bot.getClient().getRSPlayerIndexArray();
		final org.rsbot.accessors.RSPlayer[] players = Bot.getClient()
				.getRSPlayerArray();
		int n = 0;
		if (a != null) {
			if (a.getLocation() == null) {
				return false;
			}
			for (final int element : validPlayers) {
				if (players[element] == null) {
					continue;
				}
				final RSPlayer player = new RSPlayer(players[element]);
				if (player != null) {
					try {
						if (distanceBetween(a.getLocation(), player
								.getLocation()) < 2
								&& player.getAnimation() != -1 && !isrockmith(a)) {
							n++;
						}
						if (random(1, 2) == n) {
							return true;
						}
					} catch (final Exception ignored) {
					}
				}
			}
		}
		return false;
	}

	public int walkmid() {
		// walks to the middle of the mine
		RSTile z = randomizetile(mid1);
		RSTile x = randomizetile(mid2);
		RSTile c = randomizetile(mid3);
		int r = random(1, 3);
		if ((System.currentTimeMillis() - idle3 >= 6000)) {
			if (r == 1) {
				if (tileOnMap(z)
						&& NM()
						|| coalcenter.contains(getMyPlayer().getLocation().getX(),
						getMyPlayer().getLocation().getY())
						&& !isInventoryFull() && NM()) {
					if (IsTileMM(z)) {
					}
				}
			}
			if (r == 2) {
				if (tileOnMap(x)
						&& NM()
						|| coalcenter.contains(getMyPlayer().getLocation().getX(),
						getMyPlayer().getLocation().getY())
						&& !isInventoryFull() && NM()) {
					if (IsTileMM(x)) {
						wait(random(1000, 1200));
						if (tileOnMap(c)) {
							IsTileMM(c);
						}
					}
				}
			}
			if (r == 3) {
				if (tileOnMap(c)
						&& NM()
						|| coalcenter.contains(getMyPlayer().getLocation().getX(),
						getMyPlayer().getLocation().getY())
						&& !isInventoryFull() && NM()) {
					if (IsTileMM(c)) {
					}

				}
			}
			idle3 = System.currentTimeMillis();
		}
		wait(random(400, 700));
		return 1;

	}

}