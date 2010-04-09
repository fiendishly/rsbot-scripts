import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.rsbot.bot.Bot;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Constants;
import org.rsbot.script.Methods;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = { "RcZhang" }, category = "Runecraft", description = "All settings are in the GUI.", name = "RcZhang's AIO Runecrafter Beta", version = 1.9)
public class Runecrafter extends Script implements PaintListener {

	/*
	 * ENUM
	 */
	private enum Action {
		WALK_BANK, WALK_ALTAR, OPEN_BANK, BANK, CRAFT, ENTER_ALTAR, EXIT_ALTAR
	}

	public class antiban extends Thread {

		public void checkFriendsList() {
			openTab(Constants.TAB_FRIENDS);
			moveMouse(random(554, 709), random(227, 444));
		}

		public void checkSkills() {
			openTab(Constants.TAB_STATS);
			moveMouse(random(552, 603), random(420, 449));
		}

		public int getRandomMouseX(final int maxDistance) {
			final Point p = getMouseLocation();
			if (random(0, 2) == 0) {
				return p.x - random(0, p.x < maxDistance ? p.x : maxDistance);
			} else {
				return p.x
						+ random(1, 762 - p.x < maxDistance ? 762 - p.x
								: maxDistance);
			}
		}

		public int getRandomMouseY(final int maxDistance) {
			final Point p = getMouseLocation();
			if (random(0, 2) == 0) {
				return p.y - random(0, p.y < maxDistance ? p.y : maxDistance);
			} else {
				return p.y
						+ random(1, 500 - p.y < maxDistance ? 500 - p.y
								: maxDistance);
			}
		}

		public boolean moveMouseRandomly(int maxDistance) {
			if (maxDistance == 0) {
				return false;
			}
			maxDistance = random(1, maxDistance);
			final Point p = new Point(getRandomMouseX(maxDistance),
					getRandomMouseY(maxDistance));
			if (p.x < 1 || p.y < 1) {
				p.x = p.y = 1;
			}
			moveMouse(p);
			if (random(0, 2) == 0) {
				return false;
			}
			return moveMouseRandomly(maxDistance / 2);
		}

		public long nextTime(final int waitTime) {
			return time() + waitTime;
		}

		public long nextTime(final int min, final int max) {
			return nextTime(random(min, max));
		}

		@Override
		public void run() {
			try {
				final int roll = (int) (Math.random() * 1000);
				if (timeToNext < time()) {
					if (roll > 995) {
					} else if (roll > 990 && getInventoryCount() < 23) {
						checkSkills();
						timeToNext = System.currentTimeMillis()
								+ random(2000, 25000);
					} else if (roll > 985
							&& getInventoryCount() < 23
							&& !RSInterface.getInterface(751).getChild(15)
									.getText().contains("Off")) {
						checkFriendsList();
						timeToNext = System.currentTimeMillis()
								+ random(2000, 25000);
					} else if (roll > 980 && getInventoryCount() < 23) {
						checkSkills();
						timeToNext = System.currentTimeMillis()
								+ random(2000, 25000);
					} else if (roll > 960) {
						if (random(0, 2) == 0) {
							setCameraRotation((int) (getCameraAngle() + (Math
									.random() * 50 > 25 ? 1 : -1)
									* (30 + Math.random() * 90)));
						} else {
							final int key = random(0, 3) < 0 ? KeyEvent.VK_UP
									: KeyEvent.VK_DOWN;
							Bot.getInputManager().pressKey((char) key);
							Thread.sleep(random(1000, 1500));
							Bot.getInputManager().releaseKey((char) key);
						}
					} else if (roll > 940) {
						timeToNext = System.currentTimeMillis()
								+ random(2000, 25000);
						openTab(Constants.TAB_INVENTORY);
					} else if (roll > 890 && !isRunning()
							&& getMyPlayer().isMoving()) {
						if (getEnergy() > 50) {
							clickMouse(random(707, 762), random(90, 121), true);
							timeToNext = nextTime(500, 1200);
						} else if (rest) {
							Rest(100);
							Run(true);
							timeToNext = nextTime(500, 1200);
						}
						Thread.sleep(random(300, 1000));
					} else if (roll > 780) {
						moveMouseRandomly(500);
						timeToNext = nextTime(500, 7500);
					}
				} else {
					Thread.yield();
				}
			} catch (final InterruptedException e) {
				log("Interrupted");
			}
		}

		public long time() {
			return System.currentTimeMillis();
		}

		public boolean timePassed(final long time) {
			return time() > time;
		}

		public void turnCameraRandom() {
			setCameraRotation((int) (getCameraAngle() + (Math.random() * 50 > 25 ? 1
					: -1)
					* (30 + Math.random() * 90)));
		}
	}

	/*
	 * GUI
	 */
	public class GUI extends JFrame {

		private static final long serialVersionUID = -165911940843606286L;

		public GUI() {
			initComponents();
		}

		private void initComponents() {

			jLabel1 = new javax.swing.JLabel();
			jTextField1 = new javax.swing.JTextField();
			jButton1 = new javax.swing.JButton();
			jLabel2 = new javax.swing.JLabel();
			jSlider1 = new javax.swing.JSlider();
			jSeparator1 = new javax.swing.JSeparator();
			jSeparator2 = new javax.swing.JSeparator();
			jComboBox1 = new javax.swing.JComboBox();
			jCheckBox2 = new javax.swing.JCheckBox();
			jCheckBox3 = new javax.swing.JCheckBox();
			jSeparator3 = new javax.swing.JSeparator();
			jSeparator4 = new javax.swing.JSeparator();
			jCheckBox1 = new javax.swing.JCheckBox();
			jSeparator5 = new javax.swing.JSeparator();
			jCheckBox4 = new javax.swing.JCheckBox();
			jCheckBox5 = new javax.swing.JCheckBox();
			jCheckBox6 = new javax.swing.JCheckBox();
			jSeparator6 = new javax.swing.JSeparator();

			setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			setBackground(new java.awt.Color(255, 255, 255));
			setCursor(new java.awt.Cursor(java.awt.Cursor.CROSSHAIR_CURSOR));
			setResizable(false);

			jLabel1
					.setFont(new java.awt.Font("Copperplate Gothic Bold", 1, 18));
			jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			jLabel1.setText("RcZhang's Runecrafter");

			jTextField1.setEditable(false);
			jTextField1.setFont(new java.awt.Font("Copperplate Gothic Light",
					0, 11));
			jTextField1.setText("1.9");
			jTextField1.setFocusable(false);

			jButton1.setFont(new java.awt.Font("Copperplate Gothic Light", 1,
					14));
			jButton1.setText("START");
			jButton1.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(final java.awt.event.ActionEvent evt) {
					jButton1ActionPerformed(evt);
				}
			});

			jLabel2
					.setFont(new java.awt.Font("Copperplate Gothic Light", 1,
							12));
			jLabel2.setText("Mouse Speed");

			jSlider1.setFont(new java.awt.Font("Copperplate Gothic Light", 1,
					10));
			jSlider1.setMajorTickSpacing(1);
			jSlider1.setMaximum(8);
			jSlider1.setMinimum(1);
			jSlider1.setPaintLabels(true);
			jSlider1.setPaintTicks(true);
			jSlider1.setSnapToTicks(true);

			jComboBox1.setFont(new java.awt.Font("Copperplate Gothic Light", 1,
					14)); // NOI18N
			jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(
					new String[] { "~ Air ~", "~ Mind ~", "~ Water ~",
							"~ Earth ~", "~ Fire ~", "~ Body ~" }));

			jCheckBox2.setFont(new java.awt.Font("Copperplate Gothic Light", 1,
					12)); // NOI18N
			jCheckBox2.setText("Use Paint?");
			jCheckBox2.setSelected(true);

			jCheckBox3.setFont(new java.awt.Font("Copperplate Gothic Light", 1,
					12)); // NOI18N
			jCheckBox3.setText("Check For Update?");
			jCheckBox3
					.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

			jCheckBox1.setFont(new java.awt.Font("Copperplate Gothic Light", 1,
					12)); // NOI18N
			jCheckBox1.setText("Use Talisman?");
			jCheckBox1
					.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

			jCheckBox4.setFont(new java.awt.Font("Copperplate Gothic Light", 1,
					12)); // NOI18N
			jCheckBox4.setText("Use Antiban?");
			jCheckBox4.setSelected(true);

			jCheckBox5.setFont(new java.awt.Font("Copperplate Gothic Light", 1,
					12)); // NOI18N
			jCheckBox5.setText("Use Rest?");

			jCheckBox6.setFont(new java.awt.Font("Copperplate Gothic Light", 1,
					12)); // NOI18N
			jCheckBox6.setText("Lite Paint?");

			final javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
					getContentPane());
			getContentPane().setLayout(layout);
			layout
					.setHorizontalGroup(layout
							.createParallelGroup(
									javax.swing.GroupLayout.Alignment.LEADING)
							.addGroup(
									layout
											.createSequentialGroup()
											.addContainerGap()
											.addGroup(
													layout
															.createParallelGroup(
																	javax.swing.GroupLayout.Alignment.LEADING)
															.addComponent(
																	jCheckBox3,
																	javax.swing.GroupLayout.DEFAULT_SIZE,
																	278,
																	Short.MAX_VALUE)
															.addComponent(
																	jSeparator6,
																	javax.swing.GroupLayout.DEFAULT_SIZE,
																	278,
																	Short.MAX_VALUE)
															.addGroup(
																	layout
																			.createSequentialGroup()
																			.addComponent(
																					jCheckBox4)
																			.addPreferredGap(
																					javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																					62,
																					Short.MAX_VALUE)
																			.addComponent(
																					jCheckBox5))
															.addComponent(
																	jTextField1,
																	javax.swing.GroupLayout.DEFAULT_SIZE,
																	278,
																	Short.MAX_VALUE)
															.addComponent(
																	jLabel1,
																	javax.swing.GroupLayout.DEFAULT_SIZE,
																	278,
																	Short.MAX_VALUE)
															.addComponent(
																	jSlider1,
																	javax.swing.GroupLayout.DEFAULT_SIZE,
																	278,
																	Short.MAX_VALUE)
															.addComponent(
																	jLabel2)
															.addComponent(
																	jSeparator1,
																	javax.swing.GroupLayout.DEFAULT_SIZE,
																	278,
																	Short.MAX_VALUE)
															.addComponent(
																	jSeparator2,
																	javax.swing.GroupLayout.Alignment.TRAILING,
																	javax.swing.GroupLayout.DEFAULT_SIZE,
																	278,
																	Short.MAX_VALUE)
															.addComponent(
																	jButton1,
																	javax.swing.GroupLayout.DEFAULT_SIZE,
																	278,
																	Short.MAX_VALUE)
															.addGroup(
																	layout
																			.createSequentialGroup()
																			.addComponent(
																					jCheckBox2,
																					javax.swing.GroupLayout.PREFERRED_SIZE,
																					113,
																					javax.swing.GroupLayout.PREFERRED_SIZE)
																			.addPreferredGap(
																					javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																					60,
																					Short.MAX_VALUE)
																			.addComponent(
																					jCheckBox6))
															.addComponent(
																	jSeparator4,
																	javax.swing.GroupLayout.DEFAULT_SIZE,
																	278,
																	Short.MAX_VALUE)
															.addGroup(
																	layout
																			.createSequentialGroup()
																			.addComponent(
																					jComboBox1,
																					javax.swing.GroupLayout.PREFERRED_SIZE,
																					javax.swing.GroupLayout.DEFAULT_SIZE,
																					javax.swing.GroupLayout.PREFERRED_SIZE)
																			.addPreferredGap(
																					javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																			.addComponent(
																					jCheckBox1,
																					javax.swing.GroupLayout.DEFAULT_SIZE,
																					163,
																					Short.MAX_VALUE))
															.addComponent(
																	jSeparator3,
																	javax.swing.GroupLayout.DEFAULT_SIZE,
																	278,
																	Short.MAX_VALUE)
															.addComponent(
																	jSeparator5,
																	javax.swing.GroupLayout.DEFAULT_SIZE,
																	278,
																	Short.MAX_VALUE))
											.addContainerGap()));
			layout
					.setVerticalGroup(layout
							.createParallelGroup(
									javax.swing.GroupLayout.Alignment.LEADING)
							.addGroup(
									layout
											.createSequentialGroup()
											.addContainerGap()
											.addComponent(jLabel1)
											.addPreferredGap(
													javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
											.addComponent(
													jTextField1,
													javax.swing.GroupLayout.PREFERRED_SIZE,
													27,
													javax.swing.GroupLayout.PREFERRED_SIZE)
											.addGap(14, 14, 14)
											.addComponent(
													jSeparator1,
													javax.swing.GroupLayout.PREFERRED_SIZE,
													6,
													javax.swing.GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(
													javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
											.addComponent(jLabel2)
											.addPreferredGap(
													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
											.addComponent(
													jSlider1,
													javax.swing.GroupLayout.PREFERRED_SIZE,
													javax.swing.GroupLayout.DEFAULT_SIZE,
													javax.swing.GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(
													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
											.addComponent(
													jSeparator2,
													javax.swing.GroupLayout.PREFERRED_SIZE,
													10,
													javax.swing.GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(
													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
											.addGroup(
													layout
															.createParallelGroup(
																	javax.swing.GroupLayout.Alignment.BASELINE)
															.addComponent(
																	jCheckBox2)
															.addComponent(
																	jCheckBox6))
											.addPreferredGap(
													javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
											.addComponent(
													jSeparator5,
													javax.swing.GroupLayout.PREFERRED_SIZE,
													javax.swing.GroupLayout.DEFAULT_SIZE,
													javax.swing.GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(
													javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
											.addGroup(
													layout
															.createParallelGroup(
																	javax.swing.GroupLayout.Alignment.BASELINE)
															.addComponent(
																	jCheckBox4)
															.addComponent(
																	jCheckBox5))
											.addPreferredGap(
													javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
											.addComponent(
													jSeparator6,
													javax.swing.GroupLayout.PREFERRED_SIZE,
													10,
													javax.swing.GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(
													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
											.addComponent(jCheckBox3)
											.addPreferredGap(
													javax.swing.LayoutStyle.ComponentPlacement.RELATED,
													javax.swing.GroupLayout.DEFAULT_SIZE,
													Short.MAX_VALUE)
											.addComponent(
													jSeparator3,
													javax.swing.GroupLayout.PREFERRED_SIZE,
													12,
													javax.swing.GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(
													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
											.addGroup(
													layout
															.createParallelGroup(
																	javax.swing.GroupLayout.Alignment.BASELINE)
															.addComponent(
																	jComboBox1,
																	javax.swing.GroupLayout.PREFERRED_SIZE,
																	javax.swing.GroupLayout.DEFAULT_SIZE,
																	javax.swing.GroupLayout.PREFERRED_SIZE)
															.addComponent(
																	jCheckBox1))
											.addPreferredGap(
													javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
											.addComponent(
													jSeparator4,
													javax.swing.GroupLayout.PREFERRED_SIZE,
													10,
													javax.swing.GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(
													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
											.addComponent(
													jButton1,
													javax.swing.GroupLayout.PREFERRED_SIZE,
													35,
													javax.swing.GroupLayout.PREFERRED_SIZE)
											.addContainerGap()));

			pack();
		}

		private void jButton1ActionPerformed(
				final java.awt.event.ActionEvent evt) {
			openMenu = false;
		}

	}

	/*
	 * PATHS - I used ALOT of paths from various sources(Both KBot and RSBot) -
	 * Huge credits to KBot's AIO Runecrafter, as I used paths from that script.
	 */
	public static class RcPaths {
		/*
		 * AIR PATHS
		 */
		static RSTile[] air1 = { new RSTile(3186, 3438),
				new RSTile(3179, 3430), new RSTile(3168, 3425),
				new RSTile(3159, 3420), new RSTile(3149, 3417),
				new RSTile(3139, 3410), new RSTile(3129, 3407) };

		static RSTile[] air2 = { new RSTile(3185, 3436),
				new RSTile(3184, 3430), new RSTile(3177, 3430),
				new RSTile(3170, 3425), new RSTile(3168, 3419),
				new RSTile(3157, 3415), new RSTile(3149, 3415),
				new RSTile(3140, 3410), new RSTile(3131, 3408),
				new RSTile(3125, 3405) };

		static RSTile[] air3 = { new RSTile(3186, 3444),
				new RSTile(3186, 3430), new RSTile(3170, 3424),
				new RSTile(3156, 3417), new RSTile(3142, 3413),
				new RSTile(3131, 3404) };

		static RSTile[][] airPath = { RcPaths.air1, RcPaths.air2, RcPaths.air3 };
		/*
		 * MIND PATHS
		 */
		static RSTile[] mind1 = { new RSTile(2946, 3368),
				new RSTile(2946, 3373), new RSTile(2949, 3377),
				new RSTile(2953, 3381), new RSTile(2958, 3382),
				new RSTile(2961, 3386), new RSTile(2964, 3390),
				new RSTile(2965, 3395), new RSTile(2965, 3400),
				new RSTile(2968, 3404), new RSTile(2972, 3407),
				new RSTile(2976, 3411), new RSTile(2979, 3416),
				new RSTile(2984, 3418), new RSTile(2987, 3422),
				new RSTile(2987, 3428), new RSTile(2983, 3431),
				new RSTile(2981, 3437), new RSTile(2978, 3441),
				new RSTile(2976, 3446), new RSTile(2976, 3451),
				new RSTile(2973, 3455), new RSTile(2973, 3460),
				new RSTile(2973, 3465), new RSTile(2972, 3470),
				new RSTile(2972, 3475), new RSTile(2973, 3480),
				new RSTile(2971, 3485), new RSTile(2973, 3490),
				new RSTile(2976, 3494), new RSTile(2977, 3499),
				new RSTile(2977, 3504), new RSTile(2979, 3509),
				new RSTile(2985, 3512) };

		static RSTile[] mind2 = { new RSTile(2945, 3368),
				new RSTile(2945, 3373), new RSTile(2949, 3376),
				new RSTile(2953, 3379), new RSTile(2958, 3379),
				new RSTile(2961, 3384), new RSTile(2962, 3389),
				new RSTile(2964, 3394), new RSTile(2967, 3398),
				new RSTile(2970, 3402), new RSTile(2972, 3407),
				new RSTile(2976, 3410), new RSTile(2980, 3413),
				new RSTile(2983, 3417), new RSTile(2987, 3421),
				new RSTile(2987, 3426), new RSTile(2984, 3430),
				new RSTile(2981, 3434), new RSTile(2980, 3439),
				new RSTile(2976, 3445), new RSTile(2973, 3449),
				new RSTile(2972, 3454), new RSTile(2972, 3459),
				new RSTile(2972, 3464), new RSTile(2972, 3469),
				new RSTile(2972, 3474), new RSTile(2969, 3478),
				new RSTile(2971, 3483), new RSTile(2973, 3488),
				new RSTile(2976, 3492), new RSTile(2977, 3497),
				new RSTile(2980, 3501), new RSTile(2979, 3506),
				new RSTile(2979, 3511), new RSTile(2984, 3512) };

		static RSTile[] mind3 = { new RSTile(2945, 3371),
				new RSTile(2951, 3376), new RSTile(2954, 3381),
				new RSTile(2961, 3382), new RSTile(2966, 3387),
				new RSTile(2965, 3393), new RSTile(2968, 3398),
				new RSTile(2968, 3403), new RSTile(2970, 3409),
				new RSTile(2976, 3414), new RSTile(2984, 3418),
				new RSTile(3013, 3356), new RSTile(2988, 3429),
				new RSTile(2986, 3435), new RSTile(2980, 3441),
				new RSTile(2976, 3447), new RSTile(2976, 3452),
				new RSTile(2972, 3457), new RSTile(2972, 3463),
				new RSTile(3013, 3356), new RSTile(2973, 3469),
				new RSTile(2972, 3474), new RSTile(2973, 3480),
				new RSTile(2975, 3485), new RSTile(2975, 3491),
				new RSTile(2978, 3497), new RSTile(3013, 3356),
				new RSTile(2980, 3503), new RSTile(3013, 3356),
				new RSTile(2979, 3509), new RSTile(2984, 3515) };

		static RSTile[][] mindPath = { RcPaths.mind1, RcPaths.mind2,
				RcPaths.mind3 };
		/*
		 * WATER PATHS
		 */
		static RSTile[] water1 = { new RSTile(3092, 3245),
				new RSTile(3096, 3248), new RSTile(3098, 3243),
				new RSTile(3100, 3238), new RSTile(3102, 3233),
				new RSTile(3104, 3228), new RSTile(3109, 3225),
				new RSTile(3114, 3222), new RSTile(3117, 3218),
				new RSTile(3122, 3217), new RSTile(3123, 3212),
				new RSTile(3127, 3209), new RSTile(3132, 3208),
				new RSTile(3137, 3206), new RSTile(3142, 3204),
				new RSTile(3145, 3200), new RSTile(3150, 3197),
				new RSTile(3151, 3192), new RSTile(3155, 3188),
				new RSTile(3158, 3184), new RSTile(3163, 3182),
				new RSTile(3167, 3179), new RSTile(3172, 3176),
				new RSTile(3174, 3171), new RSTile(3176, 3166),
				new RSTile(3181, 3165) };

		static RSTile[] water2 = { new RSTile(3092, 3244),
				new RSTile(3096, 3247), new RSTile(3098, 3242),
				new RSTile(3101, 3237), new RSTile(3105, 3234),
				new RSTile(3110, 3231), new RSTile(3113, 3227),
				new RSTile(3115, 3222), new RSTile(3117, 3217),
				new RSTile(3120, 3213), new RSTile(3124, 3209),
				new RSTile(3128, 3206), new RSTile(3133, 3205),
				new RSTile(3138, 3202), new RSTile(3141, 3198),
				new RSTile(3146, 3195), new RSTile(3150, 3192),
				new RSTile(3153, 3188), new RSTile(3156, 3183),
				new RSTile(3159, 3179), new RSTile(3162, 3175),
				new RSTile(3164, 3170), new RSTile(3168, 3166),
				new RSTile(3172, 3163), new RSTile(3176, 3159),
				new RSTile(3181, 3157), new RSTile(3183, 3163) };

		static RSTile[] water3 = { new RSTile(3092, 3243),
				new RSTile(3095, 3247), new RSTile(3098, 3243),
				new RSTile(3098, 3238), new RSTile(3098, 3233),
				new RSTile(3101, 3229), new RSTile(3106, 3227),
				new RSTile(3111, 3225), new RSTile(3114, 3221),
				new RSTile(3117, 3217), new RSTile(3120, 3212),
				new RSTile(3124, 3208), new RSTile(3129, 3206),
				new RSTile(3134, 3205), new RSTile(3138, 3202),
				new RSTile(3139, 3197), new RSTile(3141, 3192),
				new RSTile(3145, 3188), new RSTile(3150, 3186),
				new RSTile(3153, 3182), new RSTile(3155, 3177),
				new RSTile(3159, 3174), new RSTile(3163, 3170),
				new RSTile(3167, 3167), new RSTile(3172, 3167),
				new RSTile(3177, 3167), new RSTile(3183, 3165) };

		static RSTile[][] waterPath = { RcPaths.water1, RcPaths.water2,
				RcPaths.water3 };
		/*
		 * EARTH PATHS
		 */
		static RSTile[] earth1 = { new RSTile(3253, 3420),
				new RSTile(3253, 3425), new RSTile(3257, 3428),
				new RSTile(3262, 3428), new RSTile(3267, 3428),
				new RSTile(3272, 3429), new RSTile(3275, 3433),
				new RSTile(3275, 3438), new RSTile(3279, 3441),
				new RSTile(3281, 3446), new RSTile(3281, 3451),
				new RSTile(3283, 3456), new RSTile(3286, 3461),
				new RSTile(3290, 3465), new RSTile(3295, 3466),
				new RSTile(3300, 3469), new RSTile(3304, 3474) };

		static RSTile[] earth2 = { new RSTile(3252, 3420),
				new RSTile(3253, 3425), new RSTile(3257, 3428),
				new RSTile(3262, 3428), new RSTile(3267, 3429),
				new RSTile(3272, 3429), new RSTile(3275, 3433),
				new RSTile(3275, 3438), new RSTile(3279, 3441),
				new RSTile(3282, 3445), new RSTile(3284, 3450),
				new RSTile(3286, 3455), new RSTile(3289, 3459),
				new RSTile(3291, 3464), new RSTile(3296, 3467),
				new RSTile(3301, 3469), new RSTile(3304, 3473) };

		static RSTile[][] earthPath = { RcPaths.earth1, RcPaths.earth2 };
		/*
		 * FIRE PATHS
		 */
		static RSTile[] fire1 = { new RSTile(3383, 3269),
				new RSTile(3380, 3266), new RSTile(3375, 3266),
				new RSTile(3370, 3266), new RSTile(3365, 3266),
				new RSTile(3360, 3266), new RSTile(3355, 3266),
				new RSTile(3349, 3266), new RSTile(3344, 3266),
				new RSTile(3339, 3266), new RSTile(3334, 3266),
				new RSTile(3329, 3266), new RSTile(3325, 3262),
				new RSTile(3325, 3257), new RSTile(3325, 3252),
				new RSTile(3325, 3247), new RSTile(3324, 3242),
				new RSTile(3320, 3238), new RSTile(3315, 3237),
				new RSTile(3310, 3235), new RSTile(3309, 3240),
				new RSTile(3309, 3245), new RSTile(3309, 3250),
				new RSTile(3311, 3255) };

		static RSTile[] fire2 = { new RSTile(3383, 3269),
				new RSTile(3379, 3266), new RSTile(3374, 3266),
				new RSTile(3369, 3266), new RSTile(3364, 3266),
				new RSTile(3359, 3266), new RSTile(3354, 3266),
				new RSTile(3349, 3266), new RSTile(3344, 3266),
				new RSTile(3339, 3266), new RSTile(3334, 3266),
				new RSTile(3329, 3266), new RSTile(3325, 3262),
				new RSTile(3325, 3257), new RSTile(3325, 3252),
				new RSTile(3325, 3247), new RSTile(3323, 3242),
				new RSTile(3319, 3239), new RSTile(3314, 3237),
				new RSTile(3309, 3239), new RSTile(3309, 3244),
				new RSTile(3308, 3249), new RSTile(3313, 3253) };

		static RSTile[][] firePath = { RcPaths.fire1, RcPaths.fire2 };

		/*
		 * BODY PATHS
		 */
		static RSTile[] body1 = { new RSTile(3094, 3491),
				new RSTile(3090, 3488), new RSTile(3095, 3487),
				new RSTile(3099, 3484), new RSTile(3100, 3479),
				new RSTile(3100, 3474), new RSTile(3099, 3469),
				new RSTile(3096, 3465), new RSTile(3091, 3465),
				new RSTile(3087, 3462), new RSTile(3082, 3462),
				new RSTile(3077, 3461), new RSTile(3074, 3457),
				new RSTile(3071, 3453), new RSTile(3071, 3448),
				new RSTile(3067, 3445), new RSTile(3063, 3448),
				new RSTile(3058, 3450), new RSTile(3055, 3446) };

		static RSTile[] body2 = { new RSTile(3094, 3491),
				new RSTile(3090, 3488), new RSTile(3095, 3486),
				new RSTile(3098, 3482), new RSTile(3100, 3477),
				new RSTile(3099, 3472), new RSTile(3099, 3467),
				new RSTile(3094, 3465), new RSTile(3089, 3465),
				new RSTile(3085, 3462), new RSTile(3080, 3462),
				new RSTile(3075, 3459), new RSTile(3072, 3454),
				new RSTile(3071, 3449), new RSTile(3070, 3444),
				new RSTile(3066, 3441), new RSTile(3061, 3438),
				new RSTile(3056, 3440), new RSTile(3055, 3445) };

		static RSTile[] body3 = { new RSTile(3093, 3490),
				new RSTile(3100, 3484), new RSTile(3101, 3477),
				new RSTile(3100, 3467), new RSTile(3093, 3464),
				new RSTile(3088, 3456), new RSTile(3087, 3448),
				new RSTile(3079, 3448), new RSTile(3074, 3446),
				new RSTile(3070, 3440), new RSTile(3065, 3438),
				new RSTile(3060, 3437), new RSTile(3056, 3441),
				new RSTile(3052, 3443) };

		static RSTile[][] bodyPath = { RcPaths.body1, RcPaths.body2,
				RcPaths.body3 };

	}

	public long timeToNext;

	Action action = Action.WALK_BANK;

	RSTile[][] Path;
	RSTile[] path;
	RSTile[] BankPath;

	/*
	 * RUINS IDs
	 */
	static int[] RuinIDs = { 2452, 2453, 2454, 2455, 2456, 2457 };

	/*
	 * RUINS TILES
	 */
	RSTile[] Ruins;
	static RSTile[] mindRuins = { new RSTile(2981, 3514),
			new RSTile(2982, 3514), new RSTile(2983, 3514),
			new RSTile(2982, 3513), new RSTile(2982, 3515) };
	static RSTile[] airRuins = { new RSTile(3128, 3405),
			new RSTile(3127, 3405), new RSTile(3126, 3405),
			new RSTile(3127, 3404), new RSTile(3127, 3406) };
	static RSTile[] bodyRuins = { new RSTile(3052, 3445),
			new RSTile(3053, 3445), new RSTile(3054, 3445),
			new RSTile(3053, 3444), new RSTile(3053, 3446) };
	static RSTile[] waterRuins = { new RSTile(3183, 3165),
			new RSTile(3184, 3165), new RSTile(3185, 3165),
			new RSTile(3184, 3164), new RSTile(3184, 3166) };
	static RSTile[] earthRuins = { new RSTile(3305, 3474),
			new RSTile(3306, 3474), new RSTile(3307, 3474),
			new RSTile(3305, 3473), new RSTile(3305, 3475) };
	static RSTile[] fireRuins = { new RSTile(3312, 3255),
			new RSTile(3313, 3255), new RSTile(3314, 3255),
			new RSTile(3313, 3254), new RSTile(3313, 3256) };

	/*
	 * ALTAR IDs
	 */
	static int[] AltarIDs = { 2478, 2479, 2480, 2481, 2482, 2483 };

	/*
	 * ALTAR TILES
	 */
	RSTile[] Altars;
	static RSTile[] mindAltar = { new RSTile(2785, 4841),
			new RSTile(2786, 4841), new RSTile(2787, 4841),
			new RSTile(2786, 4840), new RSTile(2786, 4842) };
	static RSTile[] airAltar = { new RSTile(2843, 4834),
			new RSTile(2844, 4834), new RSTile(2845, 4834),
			new RSTile(2844, 4833), new RSTile(2844, 4835) };
	static RSTile[] bodyAltar = { new RSTile(2522, 4840),
			new RSTile(2523, 4840), new RSTile(2524, 4840),
			new RSTile(2523, 4839), new RSTile(2523, 4841) };
	static RSTile[] waterAltar = { new RSTile(3483, 4836),
			new RSTile(3484, 4836), new RSTile(3485, 4836),
			new RSTile(3484, 4835), new RSTile(3484, 4837) };
	static RSTile[] earthAltar = { new RSTile(2657, 4841),
			new RSTile(2658, 4841), new RSTile(2659, 4841),
			new RSTile(2658, 4840), new RSTile(2658, 4842) };
	static RSTile[] fireAltar = { new RSTile(2584, 4838),
			new RSTile(2585, 4838), new RSTile(2586, 4838),
			new RSTile(2585, 4837), new RSTile(2585, 4839) };

	/*
	 * PORTAL IDS
	 */
	static int[] Portals = { 2465, 2466, 2467, 2468, 2469, 2470 };
	/*
	 * RUNECRAFTING ID'S
	 */
	int Rune;
	static int Essence = 1436;
	static int airRune = 556;
	static int mindRune = 558;
	static int waterRune = 555;
	static int earthRune = 557;
	static int fireRune = 554;
	static int bodyRune = 559;

	/*
	 * Talismans
	 */
	int Talisman;
	static int Air_Talisman = 1438;
	static int Body_Talisman = 1446;
	static int Earth_Talisman = 1440;
	static int Fire_Talisman = 1442;
	static int Mind_Talisman = 1448;
	static int Water_Talisman = 1444;

	/*
	 * BANK IDS
	 */
	static int[] Banks = { 11758, 2213, 11402, 27663, 26972 };

	/*
	 * GUI
	 */
	private javax.swing.JButton jButton1;
	private javax.swing.JCheckBox jCheckBox1;
	private javax.swing.JCheckBox jCheckBox2;
	private javax.swing.JCheckBox jCheckBox3;
	private javax.swing.JCheckBox jCheckBox4;
	private javax.swing.JCheckBox jCheckBox5;
	private javax.swing.JCheckBox jCheckBox6;
	private javax.swing.JComboBox jComboBox1;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JSeparator jSeparator1;
	private javax.swing.JSeparator jSeparator2;
	private javax.swing.JSeparator jSeparator3;
	private javax.swing.JSeparator jSeparator4;
	private javax.swing.JSeparator jSeparator5;
	private javax.swing.JSeparator jSeparator6;
	private javax.swing.JSlider jSlider1;
	private javax.swing.JTextField jTextField1;

	/*
	 * OTHER
	 */
	boolean openMenu = true;
	boolean paint, update, talisman, antiban, lite, rest;
	int mouseSpeed;
	int startXp;
	int startLv;
	int trip;
	int random;
	int crafted = 0;
	double oVersion;
	long startTime;
	Thread Antiban = new antiban();
	Point deviation = new Point(2, 2);
	String status;
	String banking = "Use-quickly";

	static int timeout = 2000;

	public boolean Animation(final boolean isDefualt) {
		try {
			final long startTime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startTime < Runecrafter.timeout) {
				if (isDefualt) {
					while (getMyPlayer().getAnimation() != -1) {
						checkForLevelUpMessage();
						wait(random(200, 230));
					}
					return true;
				} else {
					while (getMyPlayer().getAnimation() == -1) {
						checkForLevelUpMessage();
						wait(random(200, 230));
					}
					return true;
				}
			}
			return false;
		} catch (final Exception e) {
			return false;
		}
	}

	public boolean Bank(final boolean open) {
		try {
			final long startTime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startTime < Runecrafter.timeout) {
				if (open) {
					while (!bank.isOpen()) {
						wait(random(200, 230));
					}
					return true;
				} else {
					while (bank.isOpen()) {
						wait(random(200, 230));
					}
					return true;
				}
			}
			return false;
		} catch (final Exception e) {
			return false;
		}
	}

	public void checkForLevelUpMessage() {
		if (RSInterface.getInterface(Constants.INTERFACE_LEVELUP).isValid()) {
			wait(random(800, 2000)); // sim reaction time
			atInterface(Constants.INTERFACE_LEVELUP, 3);
			openTab(Constants.TAB_STATS);
			wait(random(700, 900));
			clickMouse(random(555, 600), random(420, 445), true);
			if (Bot.disableRandoms) {
				wait(random(800, 2000));
				return;
			} else {
				clickMouse(random(490, 505), random(15, 30), true);
			}
			wait(random(500, 1000));
		}
	}

	private RSTile checkTile(final RSTile tile) {
		if (tileOnMap(tile)) {
			return tile;
		}
		final RSTile loc = getMyPlayer().getLocation();
		final RSTile walk = new RSTile((loc.getX() + tile.getX()) / 2, (loc
				.getY() + tile.getY()) / 2);
		return tileOnMap(walk) ? walk : checkTile(walk);
	}

	/*
	 * METHODS
	 */
	private boolean click(final RSTile tile, final String action) {
		try {
			int fail = 0;
			do {
				final Point loc = Calculations.tileToScreen(tile);
				if (!pointOnScreen(loc) || loc.x == -1 || loc.y == -1) {
					walkToTile(tile);
					return false;
				}
				/*
				 * ONLY RETURNS TRUE HERE BECAUSE OF HOW I USE IT IN THIS SCRIPT
				 */
				if (bank.isOpen()) {
					return true;
				}
				if (fail > 5) {
					return false;
				}
				moveMouse(loc, 7, 7);
				fail++;
			} while (!listContainsString(items(), action));

			if (items().get(0).contains(action)) {
				clickMouse(true);
				return true;
			} else {
				clickMouse(false);
				while (!isMenuOpen()) {
					wait(15);
				}
				return atMenu(action);
			}
		} catch (final Exception e) {
			return false;
		}
	}

	private void drawMouse(final Graphics g) {
		final Point loc = getMouseLocation();
		if (System.currentTimeMillis()
				- Bot.getClient().getMouse().getMousePressTime() < 500) {
			g.setColor(new Color(0, 0, 0, 50));
			g.fillOval(loc.x - 5, loc.y - 5, 10, 10);
		} else {
			g.setColor(Color.BLACK);
		}
		g.drawLine(0, loc.y, 766, loc.y);
		g.drawLine(loc.x, 0, loc.x, 505);
	}

	private void drawPlayer(final Graphics g) {
		final RSTile t = getMyPlayer().getLocation();
		Calculations.tileToScreen(t);
		final Point pn = Calculations.tileToScreen(t.getX(), t.getY(), 0, 0, 0);
		final Point px = Calculations.tileToScreen(t.getX() + 1, t.getY(), 0,
				0, 0);
		final Point py = Calculations.tileToScreen(t.getX(), t.getY() + 1, 0,
				0, 0);
		final Point pxy = Calculations.tileToScreen(t.getX() + 1, t.getY() + 1,
				0, 0, 0);
		getMyPlayer().getHeight();
		g.setColor(Color.BLACK);
		g.drawPolygon(new int[] { py.x, pxy.x, px.x, pn.x }, new int[] { py.y,
				pxy.y, px.y, pn.y }, 4);
		g.setColor(new Color(240, 240, 240, 75));
		g.fillPolygon(new int[] { py.x, pxy.x, px.x, pn.x }, new int[] { py.y,
				pxy.y, px.y, pn.y }, 4);
	}

	@Override
	protected int getMouseSpeed() {
		return mouseSpeed;
	}

	// For Updater
	private double getVersion() {
		return getClass().getAnnotation(ScriptManifest.class).version();
	}

	private List<String> items() {
		return getMenuItems();
	}

	private boolean listContainsString(final List<String> list,
			final String string) {
		try {
			int a;
			for (a = list.size() - 1; a-- >= 0;) {
				if (list.get(a).contains(string)) {
					return true;
				}
			}
		} catch (final Exception e) {
		}
		return false;
	}

	@Override
	public int loop() {
		switch (action) {
		case WALK_BANK:
			BankPath = reversePath(Path[random(0, Path.length)]);
			final RSObject booth = getNearestObjectByID(Runecrafter.Banks);
			if (inventoryContains(Runecrafter.Essence)) {
				action = Action.WALK_ALTAR;
				break;
			}
			if (booth != null
					&& pointOnScreen(Calculations.tileToScreen(booth
							.getLocation()))
					&& distanceTo(booth.getLocation()) < 7) {
				if (inventoryContains(Runecrafter.Essence)) {
					action = Action.WALK_ALTAR;
				} else {
					action = Action.OPEN_BANK;
				}
				break;
			}
			status = "Walking to Bank";
			if (getDestination() == null
					|| booth == null
					|| Methods.distanceBetween(getDestination(), booth
							.getLocation()) > 3) {
				walkPath(BankPath);
			}
			break;

		case WALK_ALTAR:
			path = Path[random(0, Path.length)];
			if (getDestination() != null
					&& Methods.distanceBetween(getDestination(), Ruins[1]) < 4) {
				action = Action.ENTER_ALTAR;
				break;
			}
			if (distanceTo(Ruins[1]) < 5) {
				action = Action.ENTER_ALTAR;
				break;
			}
			status = "Walking to Altar";
			if (getDestination() == null
					|| Methods.distanceBetween(getDestination(), Ruins[1]) > 3) {
				walkPath(path);
			}
			break;

		case CRAFT:
			if (inventoryContains(Runecrafter.Essence)
					&& getMyPlayer().getAnimation() == -1) {
				status = "Crafting Runes";
				if (click(Altars[random(0, 2) == 0 ? 1 : random(0, 4)],
						"Craft-rune")) {
					Animation(false);
					break;
				}
				/*
				 * else { if(getNearestObjectByID(AltarIDs) == null){ action =
				 * Action.ENTER_ALTAR; break; }
				 * if(distanceTo(getNearestObjectByID(AltarIDs).getLocation()) >
				 * random(3,5)) walkToTile(Altars[0]); }
				 */
			} else if (inventoryContains(Rune)) {
				action = Action.EXIT_ALTAR;
				break;
			}
			break;

		case OPEN_BANK:
			status = "Opening Bank";
			while (getMyPlayer().isMoving()) {
				wait(15);
			}
			if (bank.isOpen()) {
				action = Action.BANK;
				break;
			}
			if (click(getNearestObjectByID(Runecrafter.Banks).getLocation(),
					banking)) {
				if (waitToMove(1500)) {
					while (getMyPlayer().isMoving()) {
						wait(15);
					}
				} else {
					if (bank.isOpen()) {
						break;
					}
				}
			} else {
				return random(500, 1000);
			}

		case BANK:
			status = "Banking";
			while (!inventoryContains(Runecrafter.Essence)) {
				if (!bank.isOpen()) {
					action = Action.OPEN_BANK;
					return random(100, 300);
				}
				if (bank.getCount(Runecrafter.Essence) == 0) {
					log("Out of Essence.");
					while (bank.isOpen()) {
						bank.close();
						wait(random(1200, 1500));
						continue;
					}
					stopScript();
				}
				if (inventoryContains(Rune)) {
					if (talisman) {
						bank.depositAllExcept(Talisman);
					} else {
						bank.depositAll();
					}
					wait(random(700, 900));
					continue;
				} else {
					// Had to wait, it's TOO fast
					wait(random(300, 600));
					if (!inventoryContains(Runecrafter.Essence)) {
						if (bank.atItem(Runecrafter.Essence, "Withdraw-All")) {
							wait(random(700, 900));
							continue;
						} else {
							return random(500, 1000);
						}
					} else {
						break;
					}
				}
			}
			action = Action.WALK_ALTAR;
			break;

		case ENTER_ALTAR:
			if (getNearestObjectByID(Runecrafter.AltarIDs) != null) {
				status = "Entered Altar";
				action = Action.CRAFT;
				break;
			}
			status = "Entering Ruins";
			String s = "Enter";
			if (talisman && !listContainsString(getMenuItems(), "alisman")) {
				atInventoryItem(Talisman, "se");
				s = "uins";
			}
			setCameraAltitude(true);
			click(Ruins[random(0, 1) == 0 ? 1 : random(0, 4)], s);
			if (waitToMove(1200)) {
				while (getMyPlayer().isMoving()) {
					Antiban.run();
				}
			}
			wait(random(500, 1000));
			break;

		case EXIT_ALTAR:
			status = "Exiting Altar";
			Animation(true);
			if (getNearestObjectByID(Runecrafter.Portals) == null
					&& (inventoryContains(Rune) || !inventoryContains(Runecrafter.Essence))) {
				action = Action.WALK_BANK;
				break;
			}
			if (!getMyPlayer().isMoving()) {
				if (tileOnScreen(getNearestObjectByID(Runecrafter.Portals)
						.getLocation())) {
					click(getNearestObjectByID(Runecrafter.Portals)
							.getLocation(), "Enter");
					if (waitToMove(1200)) {
						while (getMyPlayer().isMoving()) {
							Antiban.run();
						}
					}
					wait(random(500, 1000));
					break;
				} else {
					walkToTile(getNearestObjectByID(Runecrafter.Portals)
							.getLocation());
					return random(500, 1000);
				}
			}
		}
		return 0;
	}

	public boolean Movement(final boolean moving) {
		try {
			final long startTime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startTime < Runecrafter.timeout) {
				if (moving) {
					while (!getMyPlayer().isMoving()) {
						wait(random(200, 230));
					}
					return true;
				} else {
					while (getMyPlayer().isMoving()) {
						wait(random(200, 230));
					}
					return true;
				}
			}
			return false;
		} catch (final Exception e) {
			return false;
		}
	}

	/***************************************
	 * Behold, My Overly Extravagant Paint!*
	 ***************************************/
	public void onRepaint(final Graphics g) {
		if (paint) {
			long runTime = 0;
			long seconds = 0;
			long minutes = 0;
			long hours = 0;

			runTime = System.currentTimeMillis() - startTime;
			seconds = runTime / 1000;

			if (seconds >= 60) {
				minutes = seconds / 60;
				seconds -= minutes * 60;
			}
			if (minutes >= 60) {
				hours = minutes / 60;
				minutes -= hours * 60;
			}
			if (!lite) {
				drawPlayer(g);
				drawMouse(g);
				if (getNearestObjectByID(Runecrafter.Banks) != null
						&& (action.equals(Action.WALK_BANK) || action
								.equals(Action.OPEN_BANK))) {
					if (tileOnScreen(getNearestObjectByID(Runecrafter.Banks)
							.getLocation())) {
						overlayTile(g, getNearestObjectByID(Runecrafter.Banks)
								.getLocation(), new Color(255, 255, 0, 75));
					}
					if (tileOnMap(getNearestObjectByID(Runecrafter.Banks)
							.getLocation())) {
						g.setColor(new Color(255, 255, 0, 75));
						g
								.fillOval(
										tileToMinimap(getNearestObjectByID(
												Runecrafter.Banks)
												.getLocation()).x - 3,
										tileToMinimap(getNearestObjectByID(
												Runecrafter.Banks)
												.getLocation()).y - 1, 2, 2);
					}
				}
				if (getNearestObjectByID(Runecrafter.Portals) != null) {
					if (tileOnScreen(getNearestObjectByID(Runecrafter.Portals)
							.getLocation())) {
						overlayTile(g,
								getNearestObjectByID(Runecrafter.Portals)
										.getLocation(), new Color(255, 255, 0,
										75));
					}
					if (tileOnMap(getNearestObjectByID(Runecrafter.Portals)
							.getLocation())) {
						g.setColor(new Color(255, 255, 0, 75));
						g
								.fillOval(
										tileToMinimap(getNearestObjectByID(
												Runecrafter.Portals)
												.getLocation()).x - 3,
										tileToMinimap(getNearestObjectByID(
												Runecrafter.Portals)
												.getLocation()).y - 3, 6, 6);
					}
				}
				if (tileOnMap(Altars[0])) {
					g.setColor(new Color(0, 0, 200, 100));
					for (final RSTile tile : Altars) {
						g.fillOval(tileToMinimap(tile).x - 3,
								tileToMinimap(tile).y - 3, 6, 6);
					}
				}
				if (tileOnMap(Ruins[0])) {
					g.setColor(new Color(0, 0, 200, 100));
					for (final RSTile tile : Ruins) {
						g.fillOval(tileToMinimap(tile).x - 3,
								tileToMinimap(tile).y - 3, 6, 6);
					}
				}
				if (tileOnScreen(Ruins[0])) {
					for (final RSTile tile : Ruins) {
						overlayTile(g, tile, new Color(200, 0, 0, 75));
					}
				}
				if (tileOnScreen(Altars[0])) {
					for (final RSTile tile : Altars) {
						overlayTile(g, tile, new Color(200, 0, 0, 75));
					}
				}
				if (action.equals(Action.WALK_ALTAR)) {
					if (path != null) {
						final ArrayList<RSTile> paths = new ArrayList<RSTile>();
						for (final RSTile tile : path) {
							if (tileOnScreen(tile)) {
								overlayTile(g, tile, new Color(0, 200, 0, 150));
							}
							if (tileOnMap(tile)) {
								g.fillOval(tileToMinimap(tile).x - 3,
										tileToMinimap(tile).y - 3, 6, 6);
								paths.add(tile);
							}
						}
						g.setColor(new Color(0, 200, 0, 150));
						if (paths.size() >= 2) {
							for (int i = 1; i < paths.size(); i++) {
								final Point pre = tileToMinimap(paths
										.get(i - 1));
								final Point loc = tileToMinimap(paths.get(i));
								g.drawLine(loc.x, loc.y, pre.x, pre.y);
							}
						}
					}
				}
				if (action.equals(Action.WALK_BANK)) {
					if (BankPath != null) {
						final ArrayList<RSTile> paths = new ArrayList<RSTile>();
						for (final RSTile tile : BankPath) {
							if (tileOnScreen(tile)) {
								overlayTile(g, tile, new Color(0, 200, 0, 150));
							}
							if (tileOnMap(tile)) {
								g.fillOval(tileToMinimap(tile).x - 3,
										tileToMinimap(tile).y - 3, 6, 6);
								paths.add(tile);
							}
						}
						g.setColor(new Color(0, 200, 0, 150));
						if (paths.size() >= 2) {
							for (int i = 1; i < paths.size(); i++) {
								final Point pre = tileToMinimap(paths
										.get(i - 1));
								final Point loc = tileToMinimap(paths.get(i));
								g.drawLine(loc.x, loc.y, pre.x, pre.y);
							}
						}
					}
				}

			}
			g.setColor(new Color(0, 0, 0, 175));
			g.fill3DRect(4, 3, 517 - 4, 3 + 17, true);
			g.fill3DRect(1, 339, 518, 141, true);
			g.setColor(Color.WHITE);
			g.drawString(""
					+ getClass().getAnnotation(ScriptManifest.class).name(), 6,
					18);
			final int x = 10;
			int y = 355 - 15;
			g.drawString("Run Time: " + hours + ":" + minutes + ":" + seconds,
					x, y += 15);
			g
					.drawString(
							"Levels Gained "
									+ (skills
											.getCurrentSkillLevel(Constants.STAT_RUNECRAFTING) - startLv),
							x, y += 30);
			g
					.drawString(
							"Xp Gained "
									+ (skills
											.getCurrentSkillExp(Constants.STAT_RUNECRAFTING) - startXp),
							x, y += 15);
			g.setColor(new Color(0, 0, 0, 75));
			g.fill3DRect(x, y += 28, 254, 12, true);
			g.setColor(new Color(255, 0, 255, 100));
			g.fillRect(x + 2, y + 2, (int) (skills
					.getPercentToNextLevel(Constants.STAT_RUNECRAFTING) * 2.5),
					8);
			g.setColor(Color.WHITE);
			g
					.drawString(
							"Percent till next level: "
									+ skills
											.getPercentToNextLevel(Constants.STAT_RUNECRAFTING),
							x, y -= 5);
			// g.drawString("Crafted: " + crafted, x, y += 32);
			g.drawString("Status: " + status, x, y += 15 + 32);
		}
	}

	/*
	 * ON-START
	 */
	@Override
	public boolean onStart(final Map<String, String> args) {

		if (!isLoggedIn()) {
			log("Please log in first");
			return false;
		}

		final JFrame gui = new GUI();

		gui.setVisible(true);

		while (openMenu) {
			jCheckBox6.setEnabled(jCheckBox2.isSelected());
			jCheckBox5.setEnabled(jCheckBox4.isSelected());
			wait(100);
		}

		gui.setVisible(false);
		gui.dispose();

		startTime = System.currentTimeMillis();
		lite = jCheckBox6.isSelected();
		antiban = jCheckBox4.isSelected();
		rest = jCheckBox5.isSelected();
		talisman = jCheckBox1.isSelected();
		paint = jCheckBox2.isSelected();
		update = jCheckBox3.isSelected();
		mouseSpeed = jSlider1.getValue();
		startXp = skills.getCurrentSkillExp(Constants.STAT_RUNECRAFTING);
		startLv = skills.getCurrentSkillLevel(Constants.STAT_RUNECRAFTING);

		if (jComboBox1.getSelectedItem().equals("~ Air ~")) {
			Altars = Runecrafter.airAltar;
			Ruins = Runecrafter.airRuins;
			Rune = Runecrafter.airRune;
			Path = RcPaths.airPath;
			Talisman = Runecrafter.Air_Talisman;
		}
		if (jComboBox1.getSelectedItem().equals("~ Mind ~")) {
			Altars = Runecrafter.mindAltar;
			Ruins = Runecrafter.mindRuins;
			Rune = Runecrafter.mindRune;
			Path = RcPaths.mindPath;
			Talisman = Runecrafter.Mind_Talisman;
		}
		if (jComboBox1.getSelectedItem().equals("~ Water ~")) {
			Altars = Runecrafter.waterAltar;
			Ruins = Runecrafter.waterRuins;
			Rune = Runecrafter.waterRune;
			Path = RcPaths.waterPath;
			Talisman = Runecrafter.Water_Talisman;
		}
		if (jComboBox1.getSelectedItem().equals("~ Earth ~")) {
			Altars = Runecrafter.earthAltar;
			Ruins = Runecrafter.earthRuins;
			Rune = Runecrafter.earthRune;
			Path = RcPaths.earthPath;
			Talisman = Runecrafter.Earth_Talisman;
		}
		if (jComboBox1.getSelectedItem().equals("~ Fire ~")) {
			Altars = Runecrafter.fireAltar;
			Ruins = Runecrafter.fireRuins;
			Rune = Runecrafter.fireRune;
			Path = RcPaths.firePath;
			Talisman = Runecrafter.Fire_Talisman;
			banking = "Bank";
			deviation = new Point(1, 1);
		}
		if (jComboBox1.getSelectedItem().equals("~ Body ~")) {
			Altars = Runecrafter.bodyAltar;
			Ruins = Runecrafter.bodyRuins;
			Rune = Runecrafter.bodyRune;
			Path = RcPaths.bodyPath;
			Talisman = Runecrafter.Body_Talisman;
		}
		if (update) {
			URLConnection url;
			BufferedReader in;
			BufferedWriter out = null;
			// Ask the user if they'd like to check for an update...
			if (JOptionPane
					.showConfirmDialog(
							null,
							"Would you like to check for updates?\nPlease Note this requires an internet connection and the script will write files to your harddrive!") == 0) { // If
				// they
				// would,
				// continue
				try {
					// Open the version text file
					url = new URL(
							"http://www.rczhang.webs.com/Scripts/RunecrafterVersion.txt")
							.openConnection();
					// Create an input stream for it
					in = new BufferedReader(new InputStreamReader(url
							.getInputStream()));
					// Check if the current version is outdated
					if (Double.parseDouble(in.readLine()) > getVersion()) {
						// If it is, check if the user would like to update.
						if (JOptionPane.showConfirmDialog(null,
								"Update found. Do you want to update?") == 0) {
							// If so, allow the user to choose the file to be
							// updated.
							JOptionPane
									.showMessageDialog(null,
											"Please choose 'ScriptName.java' in your scripts folder and hit 'Open'");
							final JFileChooser fc = new JFileChooser();
							// Make sure "Open" was clicked.
							if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
								// If so, set up the URL for the .java file and
								// set up the IO.
								url = new URL(
										"http://www.rczhang.webs.com/Scripts/Runecrafter.java")
										.openConnection();
								in = new BufferedReader(new InputStreamReader(
										url.getInputStream()));
								out = new BufferedWriter(new FileWriter(fc
										.getSelectedFile().getPath()));
								String inp;
								/*
								 * Until we reach the end of the file, write the
								 * next line in the file and add a new line.
								 * Then flush the buffer to ensure we lose no
								 * data in the process.
								 */
								while ((inp = in.readLine()) != null) {
									out.write(inp);
									out.newLine();
									out.flush();
								}
								// Notify the user that the script has been
								// updated, and a recompile and reload is
								// needed.
								log("Script successfully downloaded. Please recompile and reload your scripts!");
								return false;
							} else {
								log("Update canceled");
							}
						} else {
							log("Update canceled");
						}
					} else {
						JOptionPane.showMessageDialog(null,
								"You have the latest version. :)"); // User has
						// the
						// latest
						// version.
						// Tell
						// them!
					}
					if (in != null) {
						in.close();
					}
					if (out != null) {
						out.close();
					}
				} catch (final IOException e) {
					log("Problem getting version :/");
					return false; // Return false if there was a problem
				}
			}
		}
		return true;
	}

	private void overlayTile(final Graphics g, final RSTile t, final Color c) {
		final Point p = Calculations.tileToScreen(t);
		final Point pn = Calculations.tileToScreen(t.getX(), t.getY(), 0, 0, 0);
		final Point px = Calculations.tileToScreen(t.getX() + 1, t.getY(), 0,
				0, 0);
		final Point py = Calculations.tileToScreen(t.getX(), t.getY() + 1, 0,
				0, 0);
		final Point pxy = Calculations.tileToScreen(t.getX() + 1, t.getY() + 1,
				0, 0, 0);
		final Point[] points = { p, pn, px, py, pxy };
		for (final Point point : points) {
			if (!pointOnScreen(point)) {
				return;
			}
		}
		g.setColor(c);
		g.fillPolygon(new int[] { py.x, pxy.x, px.x, pn.x }, new int[] { py.y,
				pxy.y, px.y, pn.y }, 4);
		g.drawPolygon(new int[] { py.x, pxy.x, px.x, pn.x }, new int[] { py.y,
				pxy.y, px.y, pn.y }, 4);
	}

	public void Rest(final int stopEnergy) {
		rest(stopEnergy);
	}

	public boolean Run(final boolean running) {
		try {
			final long startTime = System.currentTimeMillis();
			while (System.currentTimeMillis() - startTime < Runecrafter.timeout) {
				if (running) {
					while (!isRunning()) {
						wait(random(200, 230));
					}
					return true;
				} else {
					while (isRunning()) {
						wait(random(200, 230));
					}
					return true;
				}
			}
			return false;
		} catch (final Exception e) {
			return false;
		}
	}

	private int start(final RSTile[] path) {
		int start = 0;
		for (int a = path.length - 1; a > 0; a--) {
			if (tileOnMinimap(path[a])) {
				start = a;
				break;
			}
		}
		return start;
	}

	private boolean tileOnMinimap(final RSTile tile) {
		final Point p = tileToMinimap(tile);
		return Math.sqrt(Math.pow(627 - p.x, 2) + Math.pow(85 - p.y, 2)) < random(
				60, 74);
	}

	private boolean walkPath(final RSTile[] path) {
		for (int i = start(path); i < path.length; i++) {
			if (!isRunning() && getEnergy() > random(40, 60)) {
				clickMouse(random(707, 762), random(90, 121), true);
			}
			walkTo(randomizeTile(path[i], 1, 1));
			waitToMove(2000);
			if (path[i] == path[path.length - 1]) {
				break;
			}
			while (!tileOnMinimap(path[i + 1])) {
				if (!getMyPlayer().isMoving()) {
					walkTo(checkTile(randomizeTile(path[i + 1], 1, 1)));
				}
				if (antiban) {
					Antiban.run();
				}
			}
		}
		while (getMyPlayer().isMoving()) {
			Antiban.run();
		}
		return distanceTo(path[path.length - 1]) <= 4;
	}

	private boolean walkToTile(final RSTile tile) {
		if (!tileOnMap(tile)) {
			return false;
		}
		walkTo(tile);
		if (waitToMove(1500)) {
			while (getMyPlayer().isMoving()) {
				wait(15);
			}
			return true;
		}
		return false;
	}

}