/**
 * @(#)Save as MHTYChef.java!
 *
 *
 * @author 0x098b4a40
 */
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.rsbot.bot.Bot;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.Skills;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSItem;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSPlayer;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = { "0x098b4a40" }, category = "Cooking", name = "MHTY - The Master Chef", version = 3.8, description = "Thanks for downloading. All options will be chosen in the GUI after starting.")
public class MHTYChef extends Script implements PaintListener {
	public class Area {
		private final int north, south, east, west;

		public Area(final int north, final int west, final int south,
				final int east) {
			this.north = north;
			this.south = south;
			this.east = east;
			this.west = west;
		}

		public int getEast() {
			return east;
		}

		public int getNorth() {
			return north;
		}

		public int getSouth() {
			return south;
		}

		public int getWest() {
			return west;
		}
	}

	public class BankArea extends Area {
		public int[] ids;
		public int type;

		// 0 = booth, 1 = chest, 2 = NPC
		public BankArea(final int north, final int west, final int south,
				final int east, final int type, final int... ids) {
			super(north, west, south, east);
			this.ids = ids;
			this.type = type;
		}

		public boolean isBanker() {
			return type == 2;
		}

		public boolean isBooth() {
			return type == 0;
		}

		public boolean isChest() {
			return type == 1;
		}

		public boolean isChest2() {
			return type == 3;
		}

		public boolean isChest3() {
			return type == 4;
		}

		public boolean isEmeraldBenedict() {
			return type == 5;
		}
	}

	public class FMArea {
		private final Area a;
		private final BankArea bank;

		public FMArea(final Area a, final BankArea bank) {
			this.a = a;
			this.bank = bank;
		}

		public Area getArea() {
			return a;
		}

		public BankArea getBank() {
			return bank;
		}
	}

	public class MHTYGUI extends javax.swing.JFrame {
		private static final long serialVersionUID = 2069665291031830182L;

		private javax.swing.JButton change;

		private javax.swing.JButton checkForUpdates;

		private javax.swing.JComboBox food;

		private javax.swing.JLabel foodText;

		private javax.swing.JTextField hours;

		private javax.swing.JButton jButton1;

		private javax.swing.JComboBox location;

		private javax.swing.JLabel locationText;

		private javax.swing.JTextField minutes;

		private javax.swing.JCheckBox paint;

		private boolean paintChecked, timeChecked;

		// Variables declaration - do not modify
		private javax.swing.JButton Start;
		private javax.swing.JCheckBox time;

		// End of variables declaration
		/** Creates new form NewJFrame */
		public MHTYGUI() {
			initComponents();
		}

		private void changeActionPerformed(final java.awt.event.ActionEvent evt) {
			// TODO add your handling code here:
			painting = paintChecked;
			if (!paintChecked) {
				Bot.getEventManager().removeListener(PaintListener.class, s);
			}
			if (timeChecked) {
				int h = 0, m = 0;
				stopping = false;
				try {
					h = Integer.parseInt(hours.getText());
					m = Integer.parseInt(minutes.getText());
				} finally {
					timeToStop = System.currentTimeMillis() + m * 60000 + h
							* 60000 * 60;
					stopping = true;
				}
			} else {
				stopping = false;
			}
			final String areaName = (String) location.getItemAt(location
					.getSelectedIndex());
			log("Changing to area: " + areaName);
			foodName = ((String) food.getItemAt(food.getSelectedIndex()))
					.toLowerCase();
			log("Now cooking: " + foodName);
			if (areaName.equals("Varrock West Bank - North Side")) {
				chosenArea = varrockwestnorth;
			} else if (areaName.equals("Varrock West Bank - South Side")) {
				chosenArea = varrockwestsouth;
			} else if (areaName.equals("Varrock East Bank")) {
				chosenArea = varrockeast;
			} else if (areaName.equals("Al Kharid Bank")) {
				chosenArea = alkharid;
			} else if (areaName.equals("Draynor Village Bank")) {
				chosenArea = draynor;
			} else if (areaName.equals("Falador East Bank")) {
				chosenArea = faladoreast;
			} else if (areaName.equals("Falador West Bank")) {
				chosenArea = faladorwest;
			} else if (areaName.equals("Grand Exchange - East Side")) {
				chosenArea = geeast;
			} else if (areaName.equals("Grand Exchange - West Side")) {
				chosenArea = gewest;
			} else if (areaName.equals("Edgeville Bank - West Side")) {
				chosenArea = edgevillewest;
			} else if (areaName.equals("Edgeville Bank - East Side")) {
				chosenArea = edgevilleeast;
			} else if (areaName.equals("Edgeville Bank - North Side")) {
				chosenArea = edgevillenorth;
			} else if (areaName.equals("*Fishing Guild")) {
				chosenArea = fishingguild;
			} else if (areaName.equals("Duel Arena")) {
				chosenArea = duelarena;
			} else if (areaName.equals("Bounty Hunter")) {
				chosenArea = bountyhunter;
			} else if (areaName.equals("*Ardougne South Bank")) {
				chosenArea = ardougnesouth;
			} else if (areaName.equals("*Ardougne North Bank")) {
				chosenArea = ardougnenorth;
			} else if (areaName.equals("*Lletya Bank")) {
				chosenArea = lletya;
			} else if (areaName.equals("*Yanille Bank")) {
				chosenArea = yanille;
			} else if (areaName.equals("*Seer's Village Bank")) {
				chosenArea = seersvillage;
			} else if (areaName.equals("Fist of Guthix")) {
				chosenArea = fistofguthix;
			} else if (areaName.equals("*Catherby")) {
				chosenArea = catherby;
			} else if (areaName.equals("*Shilo Village")) {
				chosenArea = shilovillage;
			} else if (areaName.equals("*TzHaar")) {
				chosenArea = tzhaar;
			} else if (areaName.equals("*Jatizso")) {
				chosenArea = jatizso;
			} else if (areaName.equals("*Neitiznot")) {
				chosenArea = neitiznot;
			} else if (areaName.equals("*Miscellania")) {
				chosenArea = miscellania;
			} else if (areaName.equals("*Lunar Isle")) {
				chosenArea = lunarisle;
			} else if (areaName.equals("*Lumbridge Basement")) {
				chosenArea = lumbridgebasement;
			} else if (areaName.equals("*Canifis")) {
				chosenArea = canifis;
			} else if (areaName.equals("*Port Phasmatys (Reqs Ammy!)")) {
				chosenArea = portphasmatys;
			} else if (areaName.equals("*Castle Wars")) {
				chosenArea = castlewars;
			} else if (areaName.equals("*Pest Control")) {
				chosenArea = pestcontrol;
			} else if (areaName.equals("*Oo'glog")) {
				chosenArea = ooglog;
			} else if (areaName.equals("Mos Le'Harmless")) {
				chosenArea = mosleharmless;
			} else if (areaName.equals("*Nardah")) {
				chosenArea = nardah;
			} else if (areaName.equals("*Zanaris")) {
				chosenArea = zanaris;
			} else if (areaName.equals("*Dorgesh-Kaan (Goblin City)")) {
				chosenArea = dorgeshkaan;
			} else if (areaName.equals("*Keldagrim")) {
				chosenArea = keldagrim;
			} else if (areaName.equals("*Rogues Den")) {
				chosenArea = roguesden;
			}

			// Foods
			if (foodName.equals("anchovies")) {
				foodID = 321;
			} else if (foodName.equals("bass")) {
				foodID = 363;
			} else if (foodName.equals("bear meat")) {
				foodID = 2136;
			} else if (foodName.equals("beef")) {
				foodID = 2132;
			} else if (foodName.equals("cave eel")) {
				foodID = 5001;
			} else if (foodName.equals("chicken")) {
				foodID = 2138;
			} else if (foodName.equals("cod")) {
				foodID = 341;
			} else if (foodName.equals("crayfish")) {
				foodID = 13435;
			} else if (foodName.equals("herring")) {
				foodID = 345;
			} else if (foodName.equals("lobster")) {
				foodID = 377;
			} else if (foodName.equals("mackerel")) {
				foodID = 353;
			} else if (foodName.equals("manta ray")) {
				foodID = 389;
			} else if (foodName.equals("monkfish")) {
				foodID = 7944;
			} else if (foodName.equals("pawya meat")) {
				foodID = 12535;
			} else if (foodName.equals("pike")) {
				foodID = 349;
			} else if (foodName.equals("rabbit")) {
				foodID = 3226;
			} else if (foodName.equals("rainbow fish")) {
				foodID = 10138;
			} else if (foodName.equals("rat meat")) {
				foodID = 2134;
			} else if (foodName.equals("salmon")) {
				foodID = 331;
			} else if (foodName.equals("sardine")) {
				foodID = 327;
			} else if (foodName.equals("sea turtle")) {
				foodID = 395;
			} else if (foodName.equals("shark")) {
				foodID = 383;
			} else if (foodName.equals("shrimps")) {
				foodID = 317;
			} else if (foodName.equals("swordfish")) {
				foodID = 371;
			} else if (foodName.equals("trout")) {
				foodID = 335;
			} else if (foodName.equals("tuna")) {
				foodID = 359;
			} else if (foodName.equals("ugthanki meat")) {
				foodID = 1859;
			} else if (foodName.equals("yak meat")) {
				foodID = 10816;
			}
			savedItems[savedItems.length - 1] = foodID;
			PrintWriter out = null;
			try {
				out = new PrintWriter(new FileWriter("MHTYChefConfig.txt"));
				final String[] settings = { "" + (paintChecked ? 1 : 0),
						"" + (timeChecked ? 1 : 0), hours.getText(),
						minutes.getText(), food.getSelectedIndex() + "",
						location.getSelectedIndex() + "" };
				for (final String line : settings) {
					out.println(line);
				}
				out.close();
				log("Configuration saved to MHTYChefConfig.txt!");
			} catch (final IOException e) {
				log("Saving configuration failed!");
			}
		}

		private void foodActionPerformed(final java.awt.event.ActionEvent evt) {
		}

		private void formWindowClosed(final java.awt.event.WindowEvent evt) {
		}

		/**
		 * This method is called from within the constructor to initialize the
		 * form. WARNING: Do NOT modify this code. The content of this method is
		 * always regenerated by the Form Editor.
		 */

		private void initComponents() {
			paintChecked = timeChecked = false;
			locationText = new javax.swing.JLabel();
			foodText = new javax.swing.JLabel();
			location = new javax.swing.JComboBox();
			food = new javax.swing.JComboBox();
			checkForUpdates = new javax.swing.JButton();
			time = new javax.swing.JCheckBox();
			hours = new javax.swing.JTextField();
			minutes = new javax.swing.JTextField();
			paint = new javax.swing.JCheckBox();
			Start = new javax.swing.JButton();
			jButton1 = new javax.swing.JButton();
			change = new javax.swing.JButton();
			setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			setTitle("More Human Than You - The Master Chef!");
			setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
			setResizable(false);
			addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosed(final java.awt.event.WindowEvent evt) {
					formWindowClosed(evt);
				}
			});

			locationText.setText("Location");

			foodText.setText("Food");

			location.setModel(new javax.swing.DefaultComboBoxModel(
					new String[] { "Grand Exchange - East Side",
							"Grand Exchange - West Side",
							"Varrock West Bank - North Side",
							"Varrock West Bank - South Side",
							"Varrock East Bank", "Al Kharid Bank",
							"Draynor Village Bank", "Falador East Bank",
							"Falador West Bank", "Edgeville Bank - West Side",
							"Edgeville Bank - East Side",
							"Edgeville Bank - North Side", "Duel Arena",
							"*Castle Wars", "*Pest Control", "*Rogues Den",
							"*Lumbridge Basement", "*Ardougne South Bank",
							"*Ardougne North Bank", "*Lletya Bank",
							"*Yanille Bank", "*Seer's Village Bank",
							"*Catherby", "*Shilo Village", "*TzHaar",
							"*Jatizso", "*Neitiznot", "*Miscellania",
							"*Lunar Isle", "*Canifis", "*Port Phasmatys",
							"*Oo'glog", "*Mos Le'Harmless", "*Nardah",
							"*Zanaris", "*Dorgesh-Kaan (Goblin City)",
							"*Keldagrim" }));
			location.setToolTipText("Where do you want to run the script?");
			location.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(final java.awt.event.ActionEvent evt) {
					locationActionPerformed(evt);
				}
			});

			food.setModel(new javax.swing.DefaultComboBoxModel(new String[] {
					"Anchovies", "Bass", "Bear", "Meat", "Beef", "Cave Eel",
					"Chicken", "Cod", "Crayfish", "Herring", "Lobster",
					"Mackerel", "Manta Ray", "Monkfish", "Pike", "Rabbit",
					"Rainbow Fish", "Rat Meat", "Salmon", "Sardine",
					"Sea Turtle", "Shark", "Shrimps", "Swordfish", "Trout",
					"Tuna", "Ugthanki Meat", "Yak Meat" }));
			food.setToolTipText("What type of food would you like to cook?");
			food.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(final java.awt.event.ActionEvent evt) {
					foodActionPerformed(evt);
				}
			});

			checkForUpdates.setText("Check for Updates");
			checkForUpdates
					.setToolTipText("Click here to check for script version updates. (Optional) You can also download it.");
			checkForUpdates
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(
								final java.awt.event.ActionEvent evt) {
						}
					});

			time.setText("Log out after");
			time
					.setToolTipText("Tick this to log out after the amount of time below.");
			time.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(final java.awt.event.ActionEvent evt) {
					timeActionPerformed(evt);
				}
			});

			hours.setColumns(3);
			hours.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
			hours.setText("H");
			hours
					.setToolTipText("The amount of hours until the script purposely logs out. Used by the feature above.");
			hours.setSelectionEnd(1);
			hours.setSelectionStart(1);

			minutes.setColumns(3);
			minutes.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
			minutes.setText("M");
			minutes
					.setToolTipText("The amount of minutes until the script purposely logs out. Used by the feature above.");

			paint.setText("Paint Progress");
			paint.setToolTipText("Tick this to paint the script's progress.");
			paint.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(final java.awt.event.ActionEvent evt) {
					paintActionPerformed(evt);
				}
			});

			Start.setText("Start Script");
			Start.setToolTipText("Click here to Start/Stop the script.");
			Start.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(final java.awt.event.ActionEvent evt) {
					StartActionPerformed(evt);
				}
			});

			jButton1.setText("Pause Script");
			jButton1.setToolTipText("Click this to pause/resume the script.");
			jButton1.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(final java.awt.event.ActionEvent evt) {
					jButton1ActionPerformed(evt);
				}
			});
			change.setText("Apply");
			change.setToolTipText("Applies the settings.");
			change.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(final java.awt.event.ActionEvent evt) {
					changeActionPerformed(evt);
				}
			});

			BufferedReader in = null;
			try {
				in = new BufferedReader(new FileReader("MHTYChefConfig.txt"));
				final String[] settings = new String[6];
				String line = "";
				for (int i = 0; i < settings.length
						&& (line = in.readLine()) != null; i++) {
					settings[i] = line;
				}
				paint.setSelected(settings[0].equals("1"));
				time.setSelected(settings[1].equals("1"));
				timeChecked = settings[1].equals("1");
				paintChecked = settings[0].equals("1");
				hours.setText(settings[2]);
				minutes.setText(settings[3]);
				food.setSelectedIndex(Integer.parseInt(settings[4]));
				location.setSelectedIndex(Integer.parseInt(settings[5]));
				in.close();
				log("Configuration found and loaded!");
			} catch (final IOException e) {
				log("No configuration found.");
			}

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
															.addGroup(
																	layout
																			.createSequentialGroup()
																			.addComponent(
																					Start)
																			.addPreferredGap(
																					javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																			.addComponent(
																					jButton1)
																			.addPreferredGap(
																					javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																			.addComponent(
																					change)
																			.addPreferredGap(
																					javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																			.addComponent(
																					checkForUpdates))
															.addComponent(
																	location,
																	javax.swing.GroupLayout.PREFERRED_SIZE,
																	javax.swing.GroupLayout.DEFAULT_SIZE,
																	javax.swing.GroupLayout.PREFERRED_SIZE)
															.addComponent(
																	foodText)
															.addGroup(
																	layout
																			.createSequentialGroup()
																			.addGroup(
																					layout
																							.createParallelGroup(
																									javax.swing.GroupLayout.Alignment.LEADING)
																							.addComponent(
																									food,
																									javax.swing.GroupLayout.PREFERRED_SIZE,
																									javax.swing.GroupLayout.DEFAULT_SIZE,
																									javax.swing.GroupLayout.PREFERRED_SIZE)
																							.addComponent(
																									locationText))
																			.addPreferredGap(
																					javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																			.addGroup(
																					layout
																							.createParallelGroup(
																									javax.swing.GroupLayout.Alignment.LEADING)
																							.addComponent(
																									paint)
																							.addGroup(
																									layout
																											.createSequentialGroup()
																											.addComponent(
																													time)
																											.addPreferredGap(
																													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																											.addComponent(
																													hours,
																													javax.swing.GroupLayout.PREFERRED_SIZE,
																													javax.swing.GroupLayout.DEFAULT_SIZE,
																													javax.swing.GroupLayout.PREFERRED_SIZE)
																											.addPreferredGap(
																													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																											.addComponent(
																													minutes,
																													javax.swing.GroupLayout.PREFERRED_SIZE,
																													javax.swing.GroupLayout.DEFAULT_SIZE,
																													javax.swing.GroupLayout.PREFERRED_SIZE)))))
											.addContainerGap(24,
													Short.MAX_VALUE)));
			layout
					.setVerticalGroup(layout
							.createParallelGroup(
									javax.swing.GroupLayout.Alignment.LEADING)
							.addGroup(
									layout
											.createSequentialGroup()
											.addContainerGap()
											.addComponent(foodText)
											.addPreferredGap(
													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
											.addGroup(
													layout
															.createParallelGroup(
																	javax.swing.GroupLayout.Alignment.BASELINE)
															.addComponent(
																	food,
																	javax.swing.GroupLayout.PREFERRED_SIZE,
																	javax.swing.GroupLayout.DEFAULT_SIZE,
																	javax.swing.GroupLayout.PREFERRED_SIZE)
															.addComponent(time)
															.addComponent(
																	minutes,
																	javax.swing.GroupLayout.PREFERRED_SIZE,
																	javax.swing.GroupLayout.DEFAULT_SIZE,
																	javax.swing.GroupLayout.PREFERRED_SIZE)
															.addComponent(
																	hours,
																	javax.swing.GroupLayout.PREFERRED_SIZE,
																	javax.swing.GroupLayout.DEFAULT_SIZE,
																	javax.swing.GroupLayout.PREFERRED_SIZE))
											.addPreferredGap(
													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
											.addGroup(
													layout
															.createParallelGroup(
																	javax.swing.GroupLayout.Alignment.BASELINE)
															.addComponent(
																	locationText)
															.addComponent(paint))
											.addPreferredGap(
													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
											.addComponent(
													location,
													javax.swing.GroupLayout.PREFERRED_SIZE,
													javax.swing.GroupLayout.DEFAULT_SIZE,
													javax.swing.GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(
													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
											.addGroup(
													layout
															.createParallelGroup(
																	javax.swing.GroupLayout.Alignment.BASELINE)
															.addComponent(Start)
															.addComponent(
																	jButton1)
															.addComponent(
																	checkForUpdates)
															.addComponent(
																	change))
											.addContainerGap(
													javax.swing.GroupLayout.DEFAULT_SIZE,
													Short.MAX_VALUE)));

			pack();
		}// </editor-fold>

		private void jButton1ActionPerformed(
				final java.awt.event.ActionEvent evt) {
			paused = !paused;
			if (!paused) {
				jButton1.setText("Pause Script");
				log("Script resumed.");
			} else {
				jButton1.setText("Resume Script");
				log("Script paused.");
			}
		}

		private void locationActionPerformed(
				final java.awt.event.ActionEvent evt) {
			// TODO add your handling code here:
		}

		private void paintActionPerformed(final java.awt.event.ActionEvent evt) {
			// TODO add your handling code here:
			paintChecked = !paintChecked;
		}

		private void StartActionPerformed(final java.awt.event.ActionEvent evt) {
			// TODO add your handling code here:
			if (!started) {
				log("Starting...");
				changeActionPerformed(evt);
				started = true;
				Start.setText("Stop Script");
				log("Successfully started! Here we go!");
			} else {
				log("Stopped script.");
				loggingOut = false;
				quit = true;
			}
		}

		private void timeActionPerformed(final java.awt.event.ActionEvent evt) {
			// TODO add your handling code here:
			timeChecked = !timeChecked;
		}

	}

	public class NPCArea extends Area {
		private final int[] ids;
		private final int radius;

		public NPCArea(final int radius, final int... ids) {
			super(0, 0, 0, 0);
			this.ids = ids;
			this.radius = radius;
		}

		public int getEast() {
			RSNPC npc;
			if ((npc = getNearestFreeNPCByID(ids)) == null) {
				return -1;
			}
			return npc.getLocation().getX() + radius;
		}

		public int getNorth() {
			RSNPC npc;
			if ((npc = getNearestFreeNPCByID(ids)) == null) {
				return -1;
			}
			return npc.getLocation().getY() + radius;
		}

		public int getSouth() {
			RSNPC npc;
			if ((npc = getNearestFreeNPCByID(ids)) == null) {
				return -1;
			}
			return npc.getLocation().getY() - radius;
		}

		public int getWest() {
			RSNPC npc;
			if ((npc = getNearestFreeNPCByID(ids)) == null) {
				return -1;
			}
			return npc.getLocation().getX() - radius;
		}
	}

	public class NPCBankArea extends BankArea {
		private final int radius;

		public NPCBankArea(final int radius, final int... ids) {
			super(0, 0, 0, 0, 2, ids);
			this.radius = radius;
		}

		public int getEast() {
			RSNPC npc;
			if ((npc = getNearestFreeNPCByID(ids)) == null) {
				return -1;
			}
			return npc.getLocation().getX() + radius;
		}

		public int getNorth() {
			RSNPC npc;
			if ((npc = getNearestFreeNPCByID(ids)) == null) {
				return -1;
			}
			return npc.getLocation().getY() + radius;
		}

		public int getSouth() {
			RSNPC npc;
			if ((npc = getNearestFreeNPCByID(ids)) == null) {
				return -1;
			}
			return npc.getLocation().getY() - radius;
		}

		public int getWest() {
			RSNPC npc;
			if ((npc = getNearestFreeNPCByID(ids)) == null) {
				return -1;
			}
			return npc.getLocation().getX() - radius;
		}
	}

	public class RoguesDenArea extends BankArea {
		private final int radius;

		public RoguesDenArea(final int radius, final int... ids) {
			super(0, 0, 0, 0, 5, ids);
			this.radius = radius;
		}

		public int getEast() {
			RSNPC npc;
			if ((npc = getNearestFreeNPCByID(ids)) == null) {
				return -1;
			}
			return npc.getLocation().getX() + radius;
		}

		public int getNorth() {
			RSNPC npc;
			if ((npc = getNearestFreeNPCByID(ids)) == null) {
				return -1;
			}
			return npc.getLocation().getY() + radius;
		}

		public int getSouth() {
			RSNPC npc;
			if ((npc = getNearestFreeNPCByID(ids)) == null) {
				return -1;
			}
			return npc.getLocation().getY() - radius;
		}

		public int getWest() {
			RSNPC npc;
			if ((npc = getNearestFreeNPCByID(ids)) == null) {
				return -1;
			}
			return npc.getLocation().getX() - radius;
		}
	}

	private enum State {
		BANKING, COOKING, LIGHTING, STARTING, WALKTOBANK;
	}

	private static final int fireID = 2732, lightingAnim = 733,
			cookingAnim = 897, tinderboxID = 590;
	private static final int[] logIDs = { 1511, 1513, 1517, 1521, 1519, 1515 };
	private FMArea chosenArea;
	private final FMArea fishingguild = new FMArea(new Area(3417, 2593, 3413,
			2595), new BankArea(3422, 2586, 3418, 2586, 0, 2213)),
			draynor = new FMArea(new Area(3250, 3090, 3247, 3095),
					new BankArea(3245, 3092, 3242, 3093, 0, 2213)),
			varrockwestnorth = new FMArea(new Area(3451, 3179, 3447, 3192),
					new BankArea(3444, 3182, 3443, 3184, 0, 11402)),
			varrockwestsouth = new FMArea(new Area(3430, 3179, 3428, 3192),
					new BankArea(3436, 3187, 3435, 3189, 0, 11402)),
			varrockeast = new FMArea(new Area(3427, 3255, 3425, 3257),
					new BankArea(3422, 3251, 3420, 3254, 0, 11402)),
			alkharid = new FMArea(new Area(3168, 3275, 3165, 3277),
					new BankArea(3170, 3269, 3164, 3270, 0, 35647)),
			faladorwest = new FMArea(new Area(3375, 2945, 3374, 2948),
					new BankArea(3368, 2945, 3368, 2949, 0, 11758)),
			faladoreast = new FMArea(new Area(3361, 3010, 3359, 3015),
					new BankArea(3356, 3011, 3356, 3014, 0, 11758)),
			edgevillewest = new FMArea(new Area(3492, 3087, 3488, 3090),
					new BankArea(3493, 3093, 3489, 3094, 0, 26972)),
			edgevilleeast = new FMArea(new Area(3500, 3099, 3494, 3102),
					new BankArea(3497, 3095, 3496, 3097, 0, 26972)),
			edgevillenorth = new FMArea(new Area(3501, 3091, 3500, 3094),
					new BankArea(3494, 3093, 3492, 3094, 0, 26972)),
			geeast = new FMArea(new Area(3490, 3168, 3489, 3169), new BankArea(
					3490, 3167, 3489, 3167, 2, 6534, 6533)),
			gewest = new FMArea(new Area(3490, 3160, 3489, 3161), new BankArea(
					3490, 3162, 3489, 3162, 2, 6535, 6532)),
			duelarena = new FMArea(new Area(3266, 3381, 3265, 3385),
					new BankArea(3269, 3381, 3267, 3382, 1, 27663)),
			ardougnesouth = new FMArea(new Area(3285, 2645, 3282, 2648),
					new BankArea(3287, 2654, 3282, 2655, 0, 34752)),
			ardougnenorth = new FMArea(new Area(3338, 2615, 3336, 2618),
					new BankArea(3332, 2615, 3332, 2619, 0, 34752)),
			lletya = new FMArea(new Area(3285, 2645, 3282, 2648), new BankArea(
					3164, 2351, 3164, 2354, 2, 2355, 2354)),
			yanille = new FMArea(new Area(3094, 2606, 3091, 2608),
					new BankArea(3094, 2612, 3091, 2613, 0, 2213)),
			seersvillage = new FMArea(new Area(3486, 2724, 3484, 2727),
					new BankArea(3493, 2722, 3492, 2729, 0, 25808)),
			fistofguthix = new FMArea(new NPCArea(2, 7605), new NPCBankArea(1,
					7605)),
			catherby = new FMArea(new Area(3435, 2808, 3437, 2810),
					new BankArea(3440, 2807, 3441, 2811, 0, 2213)),
			shilovillage = new FMArea(new Area(3094, 2606, 3091, 2608),
					new BankArea(2956, 2851, 2953, 2853, 2, 499)),
			tzhaar = new FMArea(new NPCArea(2, 2619), new NPCBankArea(1, 2619)),
			jatizso = new FMArea(new Area(3803, 2412, 3801, 2413),
					new BankArea(3802, 2415, 3801, 2418, 2, 5488)),
			neitiznot = new FMArea(new Area(3804, 2335, 3802, 2337),
					new BankArea(3807, 2334, 3807, 2339, 3, 21301)),
			miscellania = new FMArea(new Area(3897, 2616, 3895, 2617),
					new BankArea(3896, 2619, 3894, 2619, 2, 1360)),
			lunarisle = new FMArea(new Area(3916, 2099, 3914, 2101),
					new BankArea(3919, 2097, 3919, 2099, 0, 16700)),
			lumbridgebasement = new FMArea(new Area(9623, 3218, 9623, 3218),
					new BankArea(3094, 2612, 3091, 2613, 4, 12308)),
			canifis = new FMArea(new Area(3481, 3505, 3479, 3506),
					new BankArea(3483, 3511, 3478, 3512, 0, 24914)),
			portphasmatys = new FMArea(new Area(3474, 3689, 3472, 3691),
					new BankArea(3467, 3687, 3466, 3691, 0, 5276)),
			castlewars = new FMArea(new Area(3084, 2442, 3083, 2443),
					new BankArea(3083, 2444, 3083, 2444, 3, 4483)),
			pestcontrol = new FMArea(new Area(2655, 2663, 2654, 2664),
					new BankArea(3094, 2612, 3091, 2613, 0, 14367)),
			ooglog = new FMArea(new Area(2843, 2555, 2842, 2557), new BankArea(
					2841, 2556, 2839, 2556, 0, 29085)),
			mosleharmless = new FMArea(new Area(2983, 3677, 2981, 3678),
					new BankArea(2983, 3680, 2981, 3680, 0, 11338)),
			nardah = new FMArea(new Area(2893, 3431, 2890, 3433), new BankArea(
					2894, 3427, 2889, 3427, 0, 10517)), zanaris = new FMArea(
					new Area(4459, 2388, 4457, 2389), new NPCBankArea(1, 498)),
			dorgeshkaan = new FMArea(new Area(3094, 2606, 3091, 2608),
					new BankArea(5352, 2701, 5347, 2702, 0, 22819)),
			keldagrim = new FMArea(new Area(10211, 2842, 10208, 2843),
					new BankArea(10208, 2836, 10207, 2838, 0, 6084)),
			bountyhunter = new FMArea(new NPCArea(2, 6538), new NPCBankArea(1,
					6538)), roguesden = new FMArea(new Area(4973, 3043, 4973,
					3043), new RoguesDenArea(1, 2271));

	private int foodID;

	private String foodName;

	private MHTYGUI frame;

	private PaintListener s;

	private int[] savedItems;

	private boolean started, paused, painting, stopping, quit, loggingOut;

	private int startingExperience, cookingSkillIndex, startingLevel;

	private long startTime;
	private State state;

	private long timeToNext;

	private long waitTime, timeToStop;

	public void actHuman() {
		long time;
		final int roll = (int) (Math.random() * 1000);
		if (timeToNext < System.currentTimeMillis()) {
			if (roll > 995 && state != State.LIGHTING
					&& state != State.STARTING && state != State.WALKTOBANK
					&& state != State.BANKING) {

				final RSPlayer observed = getNearestPlayerByLevel(3, 138);
				if (observed != null && tileOnScreen(observed.getLocation())) {
					final int rand = random(0, 20);
					time = System.currentTimeMillis() + random(2000, 4000);
					while (System.currentTimeMillis() < time
							&& inventoryContains(foodID)
							&& tileOnScreen(observed.getLocation())) {
						moveMouse(Calculations.tileToScreen(observed
								.getLocation()).x, Calculations
								.tileToScreen(observed.getLocation()).y
								- rand);
					}
				}
				timeToNext = System.currentTimeMillis() + random(2000, 4000);
			} else if (roll > 990 && state != State.LIGHTING
					&& state != State.STARTING && state != State.WALKTOBANK
					&& state != State.BANKING) {

				openTab(Constants.TAB_STATS);
				moveMouse(random(554, 709), random(227, 444));
				timeToNext = System.currentTimeMillis() + random(2000, 4000);
			} else if (roll > 985 && state != State.LIGHTING
					&& state != State.STARTING && state != State.WALKTOBANK
					&& state != State.BANKING) {

				openTab(Constants.TAB_FRIENDS);
				moveMouse(random(554, 709), random(227, 444));
				timeToNext = System.currentTimeMillis() + random(2000, 4000);
			} else if (roll > 980 && state != State.LIGHTING
					&& state != State.STARTING && state != State.WALKTOBANK
					&& state != State.BANKING) {
				openTab(Constants.TAB_STATS);
				moveMouse(random(662, 709), random(323, 353));
				timeToNext = System.currentTimeMillis() + random(2000, 4000);
			} else if (roll > 960) {
				setCameraRotation((int) (getCameraAngle() + (Math.random() * 50 > 25 ? 1
						: -1)
						* (30 + Math.random() * 90)));
			} else if (roll > 800) {
				moveMouse(random(0, 750), random(0, 500));
			}
			timeToNext = System.currentTimeMillis() + random(200, 1500);
		}

	}

	public boolean atBankItem(final int itemID, final String txt) {
		if (!isLoggedIn() || !bank.isOpen()) {
			return false;
		}
		final int[] itemArray = bank.getItemArray();
		for (int off = 0; off < itemArray.length; off++) {
			if (itemArray[off] == itemID) {
				final Point p = bank.getItemPoint(off);
				if (p.y < 87 || p.y > 291) {
					while (bank.isOpen()) {
						bank.close();
					}
					while (isLoggedIn()) {
						logout();
					}
					log("Item not on bank screen. Logging out!");
					stopScript();
					return false;
				}
				moveMouse(p, 5, 5);
				final long waitTime = System.currentTimeMillis()
						+ random(50, 250);
				boolean found = false;
				while (!found && System.currentTimeMillis() < waitTime) {
					wait(15);
					if (getMenuItems().get(0).toLowerCase().contains(
							txt.toLowerCase())) {
						found = true;
					}
				}
				if (found) {
					clickMouse(true);
					wait(random(150, 250));
					return true;
				}
				clickMouse(false);
				wait(random(150, 250));
				return atMenu(txt);
			}
		}
		return false;
	}

	public boolean atInventoryItem(final int itemID, final String option) {
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
			if (items[i] == itemID) {
				possible.add(i);
			}
		}
		if (possible.size() == 0) {
			return false;
		}
		final int idx = possible.get(random(0, possible.size()));
		final Point t = getInventoryItemPoint(idx);
		moveMouse(t, 5, 5);
		getMenuItems();
		final long waitTime = System.currentTimeMillis() + random(50, 250);
		boolean found = false;
		while (!found && System.currentTimeMillis() < waitTime) {
			wait(15);
			if (getMenuItems().get(0).toLowerCase().contains(
					option.toLowerCase())) {
				found = true;
			}
		}
		if (found) {
			clickMouse(true);
			wait(random(150, 250));
			return true;
		}
		clickMouse(false);
		wait(random(150, 250));
		return atMenu(option);
	}

	public boolean atMenuExact(final String optionContains) {
		int idx = getMenuIndexExact(optionContains);
		// log.info((optionContains + " " + idx + " " + getMenuItems());
		if (!isMenuOpen()) {
			if (idx == -1) {
				return false;
			}
			if (idx == 0) {
				clickMouse(true);
			} else {
				clickMouse(false);
				atMenuItem(idx);
			}
			return true;
		} else {
			if (idx == -1) {
				idx = getMenuIndex("Cancel");
				atMenuItem(idx);
				return false;
			} else {
				atMenuItem(idx);
				return true;
			}
		}
	}

	public boolean atMenuItem(final int i) {
		if (!isMenuOpen()) {
			return false;
		}
		try {
			final RSTile menu = getMenuLocation();
			final List<String> items = getMenuItems();
			int longest = 0;
			for (final String s : items) {
				if (s.length() > longest) {
					longest = s.length();
				}
			}
			final int xOff = random(4, longest * 4);
			final int yOff = random(21, 29) + 15 * i;
			clickMouse(menu.getX() + xOff, menu.getY() + yOff, 2, 2, true);
			return true;
		} catch (final Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean atTile(final RSTile tile, final String option) {
		final Point p = Calculations.tileToScreen(tile);
		p.x += random(-5, 6);
		p.y += random(-5, 6);
		if (p.x < 0 || p.y < 0) {
			return false;
		}
		moveMouse(p);
		// wait(random(100,150));
		getMenuItems();
		final long waitTime = System.currentTimeMillis() + random(50, 250);
		boolean found = false;
		while (System.currentTimeMillis() < waitTime && !found) {
			wait(15);
			found = getMenuItems().get(0).toLowerCase().contains(
					option.toLowerCase());
		}
		if (found) {
			clickMouse(true);
			wait(random(150, 300));
			return true;
		}
		clickMouse(false);
		wait(random(150, 300));
		return atMenu(option);
	}

	public boolean atTileExact(final RSTile tile, final String option) {
		final Point p = Calculations.tileToScreen(tile);
		p.x += random(-5, 6);
		p.y += random(-5, 6);
		if (p.x < 0 || p.y < 0) {
			return false;
		}
		moveMouse(p);
		// wait(random(100,150));
		getMenuItems();
		final long waitTime = System.currentTimeMillis() + random(50, 250);
		boolean found = false;
		while (System.currentTimeMillis() < waitTime && !found) {
			wait(15);
			found = getMenuItems().get(0).toLowerCase().equals(
					option.toLowerCase());
		}
		if (found) {
			clickMouse(true);
			wait(random(150, 300));
			return true;
		}
		clickMouse(false);
		wait(random(150, 300));
		return atMenuExact(option);
	}

	public int bankCount(final int itemID) {
		final int[] itemArray = bank.getItemArray();
		int count = 0;
		for (final int element : itemArray) {
			if (element == itemID) {
				count++;
			}
		}
		return count;
	}

	public RSObject findFire(final int... ids) {
		RSObject cur = null;
		int dist = -1;
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
						final int distTmp = getRealDistanceTo(o.getLocation(),
								true);
						if (distTmp != -1
								&& tileIn(o.getLocation(), chosenArea.getArea())) {
							if (cur == null) {
								dist = distTmp;
								cur = o;
							} else if (distTmp < dist) {
								cur = o;
								dist = distTmp;
							}
						}
					}
				}
			}
		}
		return cur;
	}

	public int getMenuIndexExact(String optionContains) {
		optionContains = optionContains.toLowerCase();
		final java.util.List<String> actions = getMenuItems();
		for (int i = 0; i < actions.size(); i++) {
			final String action = actions.get(i);
			if (action.toLowerCase().equals(optionContains)) {
				return i;
			}
		}
		return -1;
	}

	public boolean hasLog() {
		for (final int log : MHTYChef.logIDs) {
			if (inventoryContains(log)) {
				return true;
			}
		}
		return false;
	}

	public boolean interrupted() {
		return paused || quit;
	}

	public int loop() {

		if ((!started || paused) && !quit) {
			return 15;
		}
		if (!isLoggedIn()) {
			return random(2000, 5000);
		}
		if (stopping && timeToStop < System.currentTimeMillis()) {
			quit = true;
			log("Script ran for the given amount of time. Exiting!");
		}
		if (quit) {
			if (loggingOut) {
				while (bank.isOpen()) {
					bank.close();
				}
				while (isLoggedIn()) {
					logout();
				}
				frame.dispose();
			}
			return -1;
		}
		if (RSInterface.getInterface(109).getChild(15).getText().contains(
				"Grand Exchange - Collection Box")) {
			clickMouse(random(419, 434), random(67, 81), true);
		}
		switch (state) {
		case BANKING:
			RSObject bankObject;
			if (!bank.isOpen()) {
				setRun(true);
				if (chosenArea.getBank().isBanker()) {
					final RSNPC npc = getNearestFreeNPCByID(chosenArea
							.getBank().ids);
					if (npc != null) {
						if (!tileOnScreen(npc.getLocation())) {
							state = State.WALKTOBANK;
							return 0;
						}
						atTile(npc.getLocation(), "bank banker");
					}
				} else if (chosenArea.getBank().isChest()) {
					bankObject = getNearestObjectByID(chosenArea.getBank().ids);
					if (!tileOnScreen(bankObject.getLocation())) {
						state = State.WALKTOBANK;
						return 0;
					}
					atObject(bankObject, "bank bank chest");
				} else if (chosenArea.getBank().isChest2()) {
					bankObject = getNearestObjectByID(chosenArea.getBank().ids);
					if (!tileOnScreen(bankObject.getLocation())) {
						state = State.WALKTOBANK;
						return 0;
					}
					atObject(bankObject, "use bank chest");
				} else if (chosenArea.getBank().isChest3()) {
					bankObject = getNearestObjectByID(chosenArea.getBank().ids);
					if (!tileOnScreen(bankObject.getLocation())) {
						state = State.WALKTOBANK;
						return 0;
					}
					atObject(bankObject, "bank chest");
				} else if (chosenArea.getBank().isEmeraldBenedict()) {
					final RSNPC npc = getNearestFreeNPCByID(chosenArea
							.getBank().ids);
					if (npc != null) {
						if (!tileOnScreen(npc.getLocation())) {
							state = State.WALKTOBANK;
							return 0;
						}
						atTile(npc.getLocation(), "bank emerald benedict");
					}
				} else {
					bankObject = getNearestObjectByID(chosenArea.getBank().ids);
					if (!tileOnScreen(bankObject.getLocation())) {
						state = State.WALKTOBANK;
						return 0;
					}
					atObject(bankObject, "use-quickly");
				}
				if (waitToMove(random(800, 1200))) {
					while (getMyPlayer().isMoving()) {
						actHuman();
						wait(15);
					}
				}
				if (RSInterface.getInterface(109).getChild(15).getText()
						.contains("Grand Exchange - Collection Box")) {
					clickMouse(random(419, 434), random(67, 81), true);
				}
				if (!bank.isOpen()) {
					return 0;
				}
			}
			if (isInventoryFull()
					&& (!hasLog() || !inventoryContains(MHTYChef.tinderboxID))
					&& chosenArea != roguesden) {
				bank.depositAllExcept(MHTYChef.tinderboxID);
			} else if (chosenArea == roguesden) {
				clickMouse(random(379, 413), random(296, 320), true);
			} else {
				bank.depositAllExcept(savedItems);
			}
			if (!inventoryContains(MHTYChef.tinderboxID)
					&& chosenArea != roguesden) {
				if (bankCount(MHTYChef.tinderboxID) == 0) {
					log("You have no tinderbox in your inventory or in your bank. Logging out and stopping script.");
					quit = true;
					return 0;
				} else {
					while (!atBankItem(MHTYChef.tinderboxID,
							"withdraw-1 tinderbox")
							&& !interrupted()) {
						;
					}
				}
			}
			boolean found = false;
			if (!hasLog() && chosenArea != roguesden) {
				int index = 0;
				for (int i = 0; i < MHTYChef.logIDs.length && !found; i++) {
					found = bankCount(MHTYChef.logIDs[i]) > 0;
					index = i;
				}
				if (!found) {
					log("Out of logs. Logging out and stopping script!");
					quit = true;
					return 0;
				} else {
					while (!atBankItem(MHTYChef.logIDs[index], "withdraw-1 ")
							&& !interrupted()) {
						wait(random(800, 1000));
					}
				}
			}
			found = false;
			if (!inventoryContains(foodID) || !isInventoryFull()) {
				if (bankCount(foodID) == 0 && !inventoryContains(foodID)) {
					log("Out of food. Logging out and stopping script!");
					quit = true;
					return 0;
				} else {
					while (!isInventoryFull()
							&& !(bankCount(foodID) == 0 && !isInventoryFull())
							&& !interrupted()) {
						atBankItem(foodID, "withdraw-all raw " + foodName);
						wait(random(800, 1000));
					}
				}
			}
			wait(random(250, 300));
			if (inventoryContains(foodID)
					&& (inventoryContains(MHTYChef.tinderboxID) && hasLog() || chosenArea == roguesden)) {
				while (bank.isOpen()) {
					bank.close();
				}
			} else {
				return 0;
			}
			if (inventoryContains(foodID)
					&& (inventoryContains(MHTYChef.tinderboxID) && hasLog() || chosenArea == roguesden)) {
				if (chosenArea != roguesden) {
					state = State.LIGHTING;
				} else {
					state = State.STARTING;
				}
			}
			break;
		case LIGHTING:
			RSTile fireTile;
			RSObject fire = findFire(MHTYChef.fireID);
			if (fire == null) {
				while (!tileIn(getMyPlayer().getLocation(), chosenArea
						.getArea())
						&& !interrupted()) {
					fireTile = randomTileFromArea(chosenArea.getArea());
					if (!getMyPlayer().isMoving()) {
						if (!tileOnScreen(fireTile)) {
							walkTo(fireTile, 0, 0);
						} else {
							atTile(fireTile, "walk here");
						}
					}
					if (!waitToMove(1000)) {
						return 0;
					}
					while (getMyPlayer().isMoving()) {
						actHuman();
						wait(15);
					}
				}
				final RSItem log = getInventoryItemByID(MHTYChef.logIDs);
				if (log == null) {
					state = State.WALKTOBANK;
					return 0;
				}
				atInventoryItem(MHTYChef.tinderboxID, "use tinderbox");
				atInventoryItem(log.getID(), "use tinderbox ->");
				found = false;
				long waitTime = System.currentTimeMillis() + random(1000, 1500);
				while (waitTime > System.currentTimeMillis() && !found
						&& !interrupted()) {
					if (getMyPlayer().getAnimation() == MHTYChef.lightingAnim) {
						found = true;
					}
				}
				if (!found) {
					return 0;
				}
				while (getMyPlayer().getAnimation() == MHTYChef.lightingAnim
						&& findFire(MHTYChef.fireID) == null && !interrupted()) {
					actHuman();
					wait(15);
				}
				waitTime = System.currentTimeMillis() + random(1000, 5000);
				found = false;
				while (System.currentTimeMillis() < waitTime && !found) {
					if ((fire = findFire(MHTYChef.fireID)) != null) {
						found = true;
					}
				}
				if (found) {
					state = State.STARTING;
				}
			} else {
				if (!tileIn(getMyPlayer().getLocation(), chosenArea.getArea())
						&& !tileOnScreen(fire.getLocation())) {
					final int randX = random(0, 2);
					final int randY = random(0, 2);
					fireTile = new RSTile(fire.getLocation().getX() + randX,
							fire.getLocation().getY() + randY);
					if (!tileOnScreen(fireTile)) {
						walkTo(fireTile, 0, 0);
					} else {
						atTile(fireTile, "walk here");
					}
					if (waitToMove(500)) {
						while (getMyPlayer().isMoving() && !interrupted()) {
							actHuman();
							wait(15);
						}
					}
				}
				state = State.STARTING;
			}
			break;
		case STARTING:
			fire = findFire(MHTYChef.fireID);
			if (fire != null && tileOnScreen(fire.getLocation())) {
				final RSItem food = getInventoryItemByID(foodID);
				if (food == null) {
					log("No food in inventory! Logging out and stopping script.");
					quit = true;
					return 0;
				}
				while (!RSInterface.getInterface(513).getChild(2).getText()
						.contains("How many would you like to cook?")
						&& !interrupted()) {
					atInventoryItem(foodID, "use raw " + foodName);
					fire = findFire(MHTYChef.fireID);
					if (fire == null || !tileOnScreen(fire.getLocation())) {
						if (hasLog()) {
							state = State.LIGHTING;
						} else {
							state = State.WALKTOBANK;
						}
						return 0;
					}
					atTileExact(fire.getLocation(), "use raw " + foodName
							+ " -> fire");
					moveMouse(random(165, 352), random(389, 464));

					if (waitToMove(500)) {
						while (getMyPlayer().isMoving()) {
							actHuman();
							wait(15);
						}
					}
					if (!inventoryContains(foodID)) {
						break;
					}
					final long waitTime = System.currentTimeMillis()
							+ random(500, 800);
					while (System.currentTimeMillis() < waitTime
							&& !RSInterface.getInterface(513).getChild(2)
									.getText().contains(
											"How many would you like to cook?")
							&& !interrupted()) {
						wait(15);
					}
				}
				int tries = 0;
				while (!atMenu("Cook All") && inventoryContains(foodID)
						&& !interrupted()) {
					if (!RSInterface.getInterface(513).getChild(2).getText()
							.contains("How many would you like to cook?")) {
						return 0;
					}
					clickMouse(random(165, 352), random(389, 464), false);
					tries++;
					if (tries > 3) {
						log("Problem clicking the Cook icon. Logging out and stopping script.");
						quit = true;
						return 0;
					}
				}
			}
			state = State.COOKING;
			break;
		case COOKING:
			found = false;
			waitTime = System.currentTimeMillis() + random(1000, 1500);
			int itemsBefore = 0,
			itemsAfter = 0;
			for (final int itemID : getInventoryArray()) {
				if (itemID == foodID) {
					itemsBefore++;
				}
			}
			while (System.currentTimeMillis() < waitTime && !found
					&& itemsBefore != 0 && !interrupted()) {
				wait(15);
				itemsAfter = 0;
				for (final int itemID : getInventoryArray()) {
					if (itemID == foodID) {
						itemsAfter++;
					}
				}
				if (getMyPlayer().getAnimation() == MHTYChef.cookingAnim
						|| itemsAfter < itemsBefore) {
					found = true;
				}
			}
			if (!found) {
				fire = findFire(MHTYChef.fireID);
				if (!inventoryContains(foodID)) {
					state = State.WALKTOBANK;
				} else if (fire != null && tileOnScreen(fire.getLocation())) {
					state = State.STARTING;
				} else if (hasLog()) {
					state = State.LIGHTING;
				} else {
					state = State.WALKTOBANK;
				}
			} else {
				waitTime = System.currentTimeMillis() + random(2000, 2200);
				itemsBefore = 0;
				for (final int itemID : getInventoryArray()) {
					if (itemID == foodID) {
						itemsBefore++;
					}
				}
				while (waitTime > System.currentTimeMillis()
						&& itemsBefore != 0 && !interrupted()) {
					itemsBefore = 0;
					for (final int itemID : getInventoryArray()) {
						if (itemID == foodID) {
							itemsBefore++;
						}
					}
					actHuman();
					wait(15);
				}
			}

			break;
		case WALKTOBANK:
			RSTile bankTile = null;
			if (chosenArea.getBank().isBanker()) {
				final RSNPC npc = getNearestFreeNPCByID(chosenArea.getBank().ids);
				if (npc != null) {
					bankTile = npc.getLocation();
				}
			} else if (chosenArea.getBank().isChest()) {
				bankTile = getNearestObjectByID(chosenArea.getBank().ids).getLocation();
			} else if (chosenArea.getBank().isChest2()) {
				bankTile = getNearestObjectByID(chosenArea.getBank().ids).getLocation();
			} else if (chosenArea.getBank().isChest3()) {
				bankTile = getNearestObjectByID(chosenArea.getBank().ids).getLocation();
			} else if (chosenArea.getBank().isEmeraldBenedict()) {
				final RSNPC npc = getNearestFreeNPCByID(chosenArea.getBank().ids);
				if (npc != null) {
					bankTile = npc.getLocation();
				}
			} else {
				bankTile = getNearestObjectByID(chosenArea.getBank().ids).getLocation();
			}
			final RSTile someBankTile = randomTileFromArea(chosenArea.getBank());
			if (!tileOnScreen(bankTile)) {
				turnToTile(bankTile);
				if (!tileOnScreen(bankTile)) {
					walkTo(someBankTile, 0, 0);
					if (waitToMove(random(800, 1200))) {
						while (getMyPlayer().isMoving() && !interrupted()) {
							actHuman();
							wait(15);
						}
					}
				}
			}
			state = State.BANKING;
			break;
		}
		return 0;
	}

	public void onFinish() {
		log("Thanks for using this script. :)");
		Bot.getEventManager().removeListener(PaintListener.class, this);
		frame.dispose();
	}

	public void onRepaint(final Graphics g) {
		if (painting) {
			final long time = (System.currentTimeMillis() - startTime) / 1000;
			g.setColor(new Color(0, 0, 0, 125));
			g.fillRect(4, 124, 16, 100);
			g.setColor(new Color(0, 0, 0));
			g.drawRect(4, 124, 16, 100);
			g.setColor(new Color(0, 0, 0, 50));
			g.drawRect(5, 125, 14, 98);
			g.setColor(new Color(0, 144, 255, 125));
			g
					.fillRect(
							6,
							126 + 96 - (int) (96 * skills
									.getPercentToNextLevel(cookingSkillIndex) / 100.0),
							12,
							(int) (96 * skills
									.getPercentToNextLevel(cookingSkillIndex) / 100.0));
			g.setColor(new Color(0, 0, 0));
			g
					.drawRect(
							6,
							126 + 96 - (int) (96 * skills
									.getPercentToNextLevel(cookingSkillIndex) / 100.0),
							12,
							(int) (96 * skills
									.getPercentToNextLevel(cookingSkillIndex) / 100.0));
			g.setColor(new Color(0, 66, 117, 125));
			g
					.drawRect(
							7,
							127 + 94 - (int) (94 * skills
									.getPercentToNextLevel(cookingSkillIndex) / 100.0),
							10,
							(int) (94 * skills
									.getPercentToNextLevel(cookingSkillIndex) / 100.0));
			g.setColor(new Color(255, 255, 255, 65));
			g.fillRect(4, 124, 16, 50);
			g.setColor(new Color(0, 0, 0, 125));
			g.fillRect(4, 224, 30, 16);
			g.setColor(new Color(0, 0, 0));
			g.drawRect(4, 224, 30, 16);
			g.setColor(new Color(0, 0, 0, 50));
			g.drawRect(5, 225, 28, 14);

			g.setColor(Color.WHITE);
			g.drawString(skills.getPercentToNextLevel(cookingSkillIndex) + "%",
					8, 237);
			g.setColor(new Color(255, 255, 255, 65));
			g.fillRect(4, 224, 30, 8);
			g.setColor(new Color(0, 0, 0, 125));
			g.fillRect(330, 305, 170, 16);
			g.setColor(new Color(0, 0, 0));
			g.drawRect(330, 305, 170, 16);
			g.setColor(new Color(0, 0, 0, 50));
			g.drawRect(331, 306, 168, 14);
			g.setColor(new Color(255, 255, 255));
			g.drawString("MHTY - The Master Chef", 350, 318);
			g.setColor(new Color(255, 255, 255, 65));
			g.fillRect(330, 305, 170, 8);
			g.setColor(new Color(0, 0, 0, 125));
			g.fillRect(4, 321, 511, 16);
			g.setColor(new Color(0, 0, 0));
			g.drawRect(4, 321, 511, 16);
			g.setColor(new Color(0, 0, 0, 50));
			g.drawRect(5, 322, 509, 14);
			g.setColor(new Color(255, 255, 255));
			g
					.drawString(
							"Time running: "
									+ time
									/ 3600
									+ ":"
									+ (time / 60 % 60 < 10 ? "0" : "")
									+ time
									/ 60
									% 60
									+ ":"
									+ (time % 60 < 10 ? "0" : "")
									+ time
									% 60
									+ " - XP Earned: "
									+ (skills
											.getCurrentSkillExp(cookingSkillIndex) - startingExperience)
									+ " - XP to Level: "
									+ skills
											.getXPToNextLevel(cookingSkillIndex)
									+ " - Levels gained: "
									+ (skills
											.getRealSkillLevel(cookingSkillIndex) - startingLevel),
							14, 334);
			g.setColor(new Color(255, 255, 255, 65));
			g.fillRect(4, 321, 511, 8);
		}
	}

	public boolean onStart(final Map<String, String> args) {
		s = this;
		timeToNext = 0;
		timeToStop = 0;
		loggingOut = true;
		started = painting = stopping = paused = quit = false;
		startTime = System.currentTimeMillis();
		cookingSkillIndex = Skills.getStatIndex("cooking");
		startingExperience = skills.getCurrentSkillExp(cookingSkillIndex);
		startingLevel = skills.getRealSkillLevel(cookingSkillIndex);
		frame = new MHTYGUI();
		frame.setVisible(true);
		if (!inventoryContains(MHTYChef.tinderboxID) && chosenArea != roguesden) {
			state = State.WALKTOBANK;
		}
		savedItems = new int[MHTYChef.logIDs.length + 2];
		for (int i = 0; i < MHTYChef.logIDs.length; i++) {
			savedItems[i] = MHTYChef.logIDs[i];
		}
		savedItems[savedItems.length - 2] = MHTYChef.tinderboxID;
		savedItems[savedItems.length - 1] = foodID;
		if (!inventoryContains(MHTYChef.tinderboxID) && chosenArea != roguesden
				&& !bank.isOpen()) {
			state = State.WALKTOBANK;
		} else if (bank.isOpen()) {
			state = State.BANKING;
		} else if (inventoryContains(foodID)
				&& (hasLog() || chosenArea == roguesden)) {
			state = State.COOKING;
		} else {
			state = State.WALKTOBANK;
		}
		log("Showing GUI...");
		return true;
	}

	public RSObject randomObject() {
		final RSTile loc = getMyPlayer().getLocation();
		final ArrayList<RSObject> objs = new ArrayList<RSObject>();
		RSObject next;
		for (int y = loc.getY() - 10; y < loc.getY() + 11; y++) {
			for (int x = loc.getX() - 10; x < loc.getX() + 11; x++) {
				next = getObjectAt(new RSTile(x, y));
				if (next != null && tileOnScreen(next.getLocation())) {
					objs.add(next);
				}
			}
		}
		return objs.get((int) (Math.random() * objs.size()));
	}

	public RSTile randomTileFromArea(final Area a) {
		return new RSTile(random(a.west, a.east + 1), random(a.south,
				a.north + 1));
	}

	public boolean tileIn(final RSTile tile, final Area area) {
		return !(tile.getY() > area.getNorth() || tile.getY() < area.getSouth()
				|| tile.getX() > area.getEast() || tile.getX() < area.getWest());
	}

}
