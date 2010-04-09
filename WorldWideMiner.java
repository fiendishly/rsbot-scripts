import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

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
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = { "Sean" }, category = "Mining", name = "World-Wide-Miner", version = 1.82, description = "<html><body><center><b><font size='5' color='black'>World-Wide-Miner</font></b><br></br><font size='4' color='black'>by Sean<br></b></font><br></br><font size='3' color='black'><b> </b>All Options are set on the GUI</b></font></center></body></html>")
public class WorldWideMiner extends Script implements PaintListener,
		ServerMessageListener {

	class Settings {

		String filename = "WorldWideMinerSettings.ini";
		String location = "";
		String rock1 = "";
		String rock2 = "";
		String rock3 = "";
		boolean save = true;
		boolean thirdRockCheck = true;

		public Settings() {
		}

		public String booleanToString(final boolean a) {
			if (a) {
				return "true";
			} else {
				return "false";
			}
		}

		public boolean extractBoolean(final String text) {
			return text.equals("true");
		}

		public int[] extractIntegers(String text) {
			int[] ints = null;
			try {
				text = text.replaceAll(" ", "");
				final String[] strInts = text.split(",");
				ints = new int[strInts.length];
				for (int a = 0; a < strInts.length; a++) {
					ints[a] = Integer.parseInt(strInts[a]);
				}
			} catch (final Exception e) {
			}
			return ints;
		}

		public String[] extractStrings(final String text) {
			return text.split(",");
		}

		public String getSetting(final String settingName) {
			try {
				final Properties p = new Properties();
				p.load(new FileInputStream(filename));
				return p.getProperty(settingName);
			} catch (final IOException ioe) {
				log("Error 'getSetting'  " + ioe);
				return "";
			}
		}

		public String[][] getSettingsArray() {
			final ArrayList<String[]> settingsArray = new ArrayList<String[]>();

			settingsArray.add(new String[] { "LOCATION", location });
			settingsArray.add(new String[] { "ROCKONE", rock1 });
			settingsArray.add(new String[] { "ROCKTWO", rock2 });
			settingsArray.add(new String[] { "ROCKTHREECHECK",
					booleanToString(thirdRockCheck) });
			settingsArray.add(new String[] { "SAVE", booleanToString(save) });
			if (thirdRockCheck) {
				settingsArray.add(new String[] { "ROCKTHREE", rock3 });
			}

			final String[][] stringArray = new String[settingsArray.size()][2];
			for (int a = 0; a < settingsArray.size(); a++) {
				stringArray[a][0] = settingsArray.get(a)[0];
				stringArray[a][1] = settingsArray.get(a)[1];
			}

			return stringArray;
		}

		public String intArrayToString(final int[] array) {
			String intArray = null;
			try {
				if (array.length > 0) {
					intArray = "";
					for (int a = 0; a < array.length; a++) {
						if (array[a] != 0) {
							intArray += array[a];
							if (a != array.length - 1) {
								intArray += ",";
							}
						}
					}
					return intArray;
				}
			} catch (final Exception e) {
			}
			return "";
		}

		public void saveSettings(final String[][] settings,
				final String filename) {
			try {
				final Properties p = new Properties();
				final File file = new File(filename);
				file.createNewFile();
				p.load(new FileInputStream(filename));
				for (final String[] setting : settings) {
					p.setProperty(setting[0], setting[1]);
				}
				final FileOutputStream out = new FileOutputStream(filename);
				p.store(out, "");
			} catch (final IOException ioe) {
			}
		}

		public void setSettings() {
			try {
				location = getSetting("LOCATION");
				save = extractBoolean(getSetting("SAVE"));
				rock1 = getSetting("ROCKONE");
				rock2 = getSetting("ROCKTWO");
				thirdRockCheck = extractBoolean(getSetting("ROCKTHREECHECK"));
				if (thirdRockCheck) {
					rock3 = getSetting("ROCKTHREE");
				}
			} catch (final Exception e) {
				log("Error 'setSettings'  " + e);
			}
		}

		public boolean settingsExist(final String filename) {
			final File settingsFile = new File(filename);
			return settingsFile.exists();
		}

		public String stringArrayToString(final String[] array) {
			String strArray = null;
			try {
				strArray = "";
				if (array.length <= 0) {
					return "";
				}
				for (int a = 0; a < array.length; a++) {
					if (!array[a].equals(null) && !array[a].equals("")) {
						strArray += array[a].trim();
						if (a != array.length - 1) {
							strArray += ",";
						}
					}
				}
			} catch (final Exception e) {
			}
			return strArray;
		}
	}

	private enum Status {
		bankstate, bankstate2, downLadderState, dropstate, minestate, upLadderState, walkToBankstate, walkToDown, walkToPlacestate, walkToUp;

	}

	class WWMGUI extends javax.swing.JFrame {

		private static final long serialVersionUID = -3611215776682748119L;

		// Variables declaration - do not modify
		private javax.swing.JCheckBox chkSettings;

		private javax.swing.JCheckBox chkthird;

		private javax.swing.JComboBox firstRock;

		private javax.swing.JButton jButton1;

		private javax.swing.JButton jButton2;

		private javax.swing.JButton jButton3;

		private javax.swing.JButton jButton4;

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
		private javax.swing.JLabel jLabel28;
		private javax.swing.JLabel jLabel29;
		private javax.swing.JLabel jLabel3;
		private javax.swing.JLabel jLabel30;
		private javax.swing.JLabel jLabel31;
		private javax.swing.JLabel jLabel32;
		private javax.swing.JLabel jLabel33;
		private javax.swing.JLabel jLabel34;
		private javax.swing.JLabel jLabel35;
		private javax.swing.JLabel jLabel36;
		private javax.swing.JLabel jLabel37;
		private javax.swing.JLabel jLabel38;
		private javax.swing.JLabel jLabel4;
		private javax.swing.JLabel jLabel5;
		private javax.swing.JLabel jLabel6;
		private javax.swing.JLabel jLabel7;
		private javax.swing.JLabel jLabel8;
		private javax.swing.JLabel jLabel9;
		private javax.swing.JPanel jPanel1;
		private javax.swing.JPanel jPanel2;
		private javax.swing.JPanel jPanel3;
		private javax.swing.JSeparator jSeparator1;
		private javax.swing.JSeparator jSeparator2;
		private javax.swing.JSeparator jSeparator3;
		private javax.swing.JSeparator jSeparator4;
		private javax.swing.JSeparator jSeparator6;
		private javax.swing.JSeparator jSeparator7;
		private javax.swing.JTabbedPane jTabbedPane1;
		private javax.swing.JComboBox mineLocation;
		private javax.swing.JComboBox secondRock;
		private javax.swing.JComboBox thirdRock;
		private javax.swing.JLabel thirdRockLabel;

		// End of variables declaration
		/** Creates new form WWMGUI */
		public WWMGUI() {
			initComponents();
		}

		private void chkthirdActionPerformed(
				final java.awt.event.ActionEvent evt) {
			if (chkthird.isSelected()) {
				thirdRock.setVisible(true);
				thirdRockLabel.setVisible(true);
			} else if (!chkthird.isSelected()) {
				thirdRock.setVisible(false);
				thirdRockLabel.setVisible(false);
			}
		}

		/**
		 * This method is called from within the constructor to initialize the
		 * form. WARNING: Do NOT modify this code. The content of this method is
		 * always regenerated by the Form Editor.
		 */
		// <editor-fold defaultstate="collapsed" desc="Generated Code">
		private void initComponents() {

			jPanel1 = new javax.swing.JPanel();
			jTabbedPane1 = new javax.swing.JTabbedPane();
			jPanel2 = new javax.swing.JPanel();
			jLabel2 = new javax.swing.JLabel();
			jLabel1 = new javax.swing.JLabel();
			jSeparator1 = new javax.swing.JSeparator();
			jLabel3 = new javax.swing.JLabel();
			jLabel4 = new javax.swing.JLabel();
			jLabel5 = new javax.swing.JLabel();
			jButton1 = new javax.swing.JButton();
			jLabel6 = new javax.swing.JLabel();
			jLabel7 = new javax.swing.JLabel();
			jLabel8 = new javax.swing.JLabel();
			jLabel9 = new javax.swing.JLabel();
			jLabel10 = new javax.swing.JLabel();
			jLabel11 = new javax.swing.JLabel();
			jLabel12 = new javax.swing.JLabel();
			jLabel13 = new javax.swing.JLabel();
			jButton2 = new javax.swing.JButton();
			chkSettings = new javax.swing.JCheckBox();
			jButton4 = new javax.swing.JButton();
			jPanel3 = new javax.swing.JPanel();
			jLabel14 = new javax.swing.JLabel();
			jSeparator2 = new javax.swing.JSeparator();
			jLabel15 = new javax.swing.JLabel();
			jLabel16 = new javax.swing.JLabel();
			mineLocation = new javax.swing.JComboBox();
			jLabel17 = new javax.swing.JLabel();
			firstRock = new javax.swing.JComboBox();
			jLabel18 = new javax.swing.JLabel();
			secondRock = new javax.swing.JComboBox();
			jSeparator3 = new javax.swing.JSeparator();
			jLabel19 = new javax.swing.JLabel();
			jSeparator4 = new javax.swing.JSeparator();
			jLabel20 = new javax.swing.JLabel();
			jLabel21 = new javax.swing.JLabel();
			jLabel22 = new javax.swing.JLabel();
			jLabel23 = new javax.swing.JLabel();
			jLabel24 = new javax.swing.JLabel();
			jLabel25 = new javax.swing.JLabel();
			jLabel26 = new javax.swing.JLabel();
			jLabel27 = new javax.swing.JLabel();
			jLabel28 = new javax.swing.JLabel();
			jLabel29 = new javax.swing.JLabel();
			jLabel30 = new javax.swing.JLabel();
			chkthird = new javax.swing.JCheckBox();
			thirdRock = new javax.swing.JComboBox();
			thirdRockLabel = new javax.swing.JLabel();
			jLabel31 = new javax.swing.JLabel();
			jLabel32 = new javax.swing.JLabel();
			jLabel33 = new javax.swing.JLabel();
			jLabel34 = new javax.swing.JLabel();
			jLabel35 = new javax.swing.JLabel();
			jSeparator6 = new javax.swing.JSeparator();
			jSeparator7 = new javax.swing.JSeparator();
			jLabel36 = new javax.swing.JLabel();
			jLabel37 = new javax.swing.JLabel();
			jLabel38 = new javax.swing.JLabel();
			jButton3 = new javax.swing.JButton();

			setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
			setTitle("WorldWideMiner by Sean");
			setAlwaysOnTop(true);
			setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

			jTabbedPane1.setBorder(javax.swing.BorderFactory
					.createEtchedBorder(new java.awt.Color(0, 102, 102),
							java.awt.Color.darkGray));
			jTabbedPane1.setForeground(new java.awt.Color(0, 51, 51));
			jTabbedPane1
					.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
			jTabbedPane1.setName("Tabs"); // NOI18N
			jTabbedPane1.setVerifyInputWhenFocusTarget(false);

			jLabel2.setFont(new java.awt.Font("Bookman Old Style", 0, 11));
			jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
			jLabel2.setText("By Sean");
			jLabel2.setName("ByLabel"); // NOI18N

			jLabel1.setFont(new java.awt.Font("Tunga", 1, 24));
			jLabel1.setForeground(new java.awt.Color(0, 102, 102));
			jLabel1.setText("World-Wide-Miner!");
			jLabel1.setName("NameLabel"); // NOI18N

			jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11));
			jLabel3.setText("What does the script do?");

			jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			jLabel4
					.setText("This script mines ores from all across the world of RS.  You can choose to either bank or ");
			jLabel4.setVerticalAlignment(javax.swing.SwingConstants.TOP);

			jLabel5.setText("powermine these ores.");

			jButton1.setText("Start Script");
			jButton1.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(final java.awt.event.ActionEvent evt) {
					jButton1ActionPerformed(evt);
				}
			});

			jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11));
			jLabel6.setText("Where should I start the script?");

			jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			jLabel7
					.setText("There are several places where you can start this script.  These would be:");
			jLabel7.setVerticalAlignment(javax.swing.SwingConstants.TOP);

			jLabel8.setText("1. At the Mine (If banking or powermining)");

			jLabel9.setText("2. At the Bank (If banking)");

			jLabel10
					.setText("3. Somewhere between the Bank and the Mine (If banking)");

			jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11));
			jLabel11.setText("How can I repay you for this awesome script?!?");

			jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			jLabel12
					.setText("Donations are very much appreciated!  You can either click the donate button below to donate to ");
			jLabel12.setVerticalAlignment(javax.swing.SwingConstants.TOP);

			jLabel13
					.setText("my paypal, or you can pm me with pins or other rs things.");

			jButton2.setText("Cancel Script");
			jButton2.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(final java.awt.event.ActionEvent evt) {
					jButton2ActionPerformed(evt);
				}
			});

			chkSettings.setSelected(true);
			chkSettings.setText("Save Settings");

			jButton4.setText("Load Settings");
			jButton4.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(final java.awt.event.ActionEvent evt) {
					jButton4ActionPerformed(evt);
				}
			});

			final javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(
					jPanel2);
			jPanel2.setLayout(jPanel2Layout);
			jPanel2Layout
					.setHorizontalGroup(jPanel2Layout
							.createParallelGroup(
									javax.swing.GroupLayout.Alignment.LEADING)
							.addGroup(
									jPanel2Layout
											.createSequentialGroup()
											.addGroup(
													jPanel2Layout
															.createParallelGroup(
																	javax.swing.GroupLayout.Alignment.LEADING)
															.addGroup(
																	jPanel2Layout
																			.createSequentialGroup()
																			.addContainerGap()
																			.addGroup(
																					jPanel2Layout
																							.createParallelGroup(
																									javax.swing.GroupLayout.Alignment.LEADING)
																							.addGroup(
																									jPanel2Layout
																											.createSequentialGroup()
																											.addComponent(
																													jLabel1)
																											.addPreferredGap(
																													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																											.addComponent(
																													jLabel2))
																							.addComponent(
																									jSeparator1,
																									javax.swing.GroupLayout.PREFERRED_SIZE,
																									251,
																									javax.swing.GroupLayout.PREFERRED_SIZE)
																							.addComponent(
																									jLabel3)))
															.addGroup(
																	jPanel2Layout
																			.createSequentialGroup()
																			.addGap(
																					20,
																					20,
																					20)
																			.addGroup(
																					jPanel2Layout
																							.createParallelGroup(
																									javax.swing.GroupLayout.Alignment.LEADING)
																							.addComponent(
																									jLabel5)
																							.addComponent(
																									jLabel4)))
															.addGroup(
																	jPanel2Layout
																			.createSequentialGroup()
																			.addContainerGap()
																			.addGroup(
																					jPanel2Layout
																							.createParallelGroup(
																									javax.swing.GroupLayout.Alignment.LEADING)
																							.addComponent(
																									jLabel6)
																							.addGroup(
																									jPanel2Layout
																											.createSequentialGroup()
																											.addGap(
																													10,
																													10,
																													10)
																											.addGroup(
																													jPanel2Layout
																															.createParallelGroup(
																																	javax.swing.GroupLayout.Alignment.LEADING)
																															.addComponent(
																																	jLabel7)
																															.addGroup(
																																	jPanel2Layout
																																			.createSequentialGroup()
																																			.addGap(
																																					10,
																																					10,
																																					10)
																																			.addGroup(
																																					jPanel2Layout
																																							.createParallelGroup(
																																									javax.swing.GroupLayout.Alignment.LEADING)
																																							.addComponent(
																																									jLabel9)
																																							.addComponent(
																																									jLabel8)
																																							.addComponent(
																																									jLabel10)))))))
															.addGroup(
																	jPanel2Layout
																			.createSequentialGroup()
																			.addContainerGap()
																			.addGroup(
																					jPanel2Layout
																							.createParallelGroup(
																									javax.swing.GroupLayout.Alignment.LEADING)
																							.addComponent(
																									jLabel11)
																							.addGroup(
																									jPanel2Layout
																											.createSequentialGroup()
																											.addGap(
																													10,
																													10,
																													10)
																											.addGroup(
																													jPanel2Layout
																															.createParallelGroup(
																																	javax.swing.GroupLayout.Alignment.LEADING)
																															.addComponent(
																																	jLabel13)
																															.addComponent(
																																	jLabel12))))))
											.addContainerGap(71,
													Short.MAX_VALUE))
							.addGroup(
									javax.swing.GroupLayout.Alignment.TRAILING,
									jPanel2Layout
											.createSequentialGroup()
											.addContainerGap()
											.addComponent(
													jButton1,
													javax.swing.GroupLayout.DEFAULT_SIZE,
													javax.swing.GroupLayout.DEFAULT_SIZE,
													Short.MAX_VALUE)
											.addPreferredGap(
													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
											.addComponent(
													jButton2,
													javax.swing.GroupLayout.DEFAULT_SIZE,
													javax.swing.GroupLayout.DEFAULT_SIZE,
													Short.MAX_VALUE)
											.addPreferredGap(
													javax.swing.LayoutStyle.ComponentPlacement.RELATED,
													234, Short.MAX_VALUE)
											.addGroup(
													jPanel2Layout
															.createParallelGroup(
																	javax.swing.GroupLayout.Alignment.LEADING)
															.addComponent(
																	chkSettings)
															.addComponent(
																	jButton4))
											.addGap(37, 37, 37)));
			jPanel2Layout
					.setVerticalGroup(jPanel2Layout
							.createParallelGroup(
									javax.swing.GroupLayout.Alignment.LEADING)
							.addGroup(
									jPanel2Layout
											.createSequentialGroup()
											.addContainerGap()
											.addGroup(
													jPanel2Layout
															.createParallelGroup(
																	javax.swing.GroupLayout.Alignment.BASELINE)
															.addComponent(
																	jLabel2)
															.addComponent(
																	jLabel1,
																	javax.swing.GroupLayout.PREFERRED_SIZE,
																	26,
																	javax.swing.GroupLayout.PREFERRED_SIZE))
											.addPreferredGap(
													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
											.addComponent(
													jSeparator1,
													javax.swing.GroupLayout.PREFERRED_SIZE,
													10,
													javax.swing.GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(
													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
											.addComponent(jLabel3)
											.addPreferredGap(
													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
											.addComponent(jLabel4)
											.addPreferredGap(
													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
											.addComponent(jLabel5)
											.addPreferredGap(
													javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
											.addComponent(jLabel6)
											.addPreferredGap(
													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
											.addComponent(jLabel7)
											.addPreferredGap(
													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
											.addComponent(jLabel8)
											.addPreferredGap(
													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
											.addComponent(jLabel9)
											.addPreferredGap(
													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
											.addComponent(jLabel10)
											.addPreferredGap(
													javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
											.addComponent(jLabel11)
											.addPreferredGap(
													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
											.addComponent(jLabel12)
											.addPreferredGap(
													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
											.addComponent(jLabel13)
											.addPreferredGap(
													javax.swing.LayoutStyle.ComponentPlacement.RELATED,
													89, Short.MAX_VALUE)
											.addComponent(chkSettings)
											.addPreferredGap(
													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
											.addGroup(
													jPanel2Layout
															.createParallelGroup(
																	javax.swing.GroupLayout.Alignment.BASELINE)
															.addComponent(
																	jButton1)
															.addComponent(
																	jButton2)
															.addComponent(
																	jButton4))
											.addContainerGap()));

			jTabbedPane1.addTab("Script Information", jPanel2);

			jLabel14.setFont(new java.awt.Font("Tunga", 1, 24));
			jLabel14.setForeground(new java.awt.Color(0, 102, 102));
			jLabel14.setText("World-Wide-Miner!");
			jLabel14.setName("NameLabel"); // NOI18N

			jLabel15.setFont(new java.awt.Font("Bookman Old Style", 0, 11));
			jLabel15
					.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
			jLabel15.setText("By Sean");
			jLabel15.setName("ByLabel"); // NOI18N

			jLabel16.setFont(new java.awt.Font("Tahoma", 1, 11));
			jLabel16.setText("Mining Location:");

			mineLocation.setModel(new javax.swing.DefaultComboBoxModel(
					new String[] { "Power-Mining", "Varrock East",
							"Varrock West", "Al Kharid", "Rimmington",
							"Barbarian Village", "Mining Guild",
							"West Lumbridge Swamp" }));

			jLabel17.setFont(new java.awt.Font("Tahoma", 1, 11));
			jLabel17.setText("First rock to mine:");

			firstRock.setModel(new javax.swing.DefaultComboBoxModel(
					new String[] { "Tin", "Copper", "Clay", "Iron", "Silver",
							"Gold", "Coal", "Mithril", "Adamant" }));

			jLabel18.setFont(new java.awt.Font("Tahoma", 1, 11));
			jLabel18.setText("Second rock to mine:");

			secondRock.setModel(new javax.swing.DefaultComboBoxModel(
					new String[] { "None", "Tin", "Copper", "Clay", "Iron",
							"Silver", "Gold", "Coal", "Mithril", "Adamant" }));

			jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);

			jLabel19.setFont(new java.awt.Font("Tahoma", 1, 11));
			jLabel19.setText("Wonder what each mine has?  Look below!");

			jLabel20.setFont(new java.awt.Font("Tahoma", 1, 11));
			jLabel20.setText("Varrock East:");

			jLabel21.setText("6 Tin, 9 Copper, 4 Iron");

			jLabel22.setFont(new java.awt.Font("Tahoma", 1, 11));
			jLabel22.setText("Varrock West:");

			jLabel23.setText("3 Clay, 8 Tin, 3 Iron, 3 Silver");

			jLabel24.setFont(new java.awt.Font("Tahoma", 1, 11));
			jLabel24.setText("Al Kharid:");

			jLabel25
					.setText("1 Tin, 4 Copper, 9 Iron, 3 Coal, 5 Silver, 2 Gold, 2 Mithril,");

			jLabel26.setFont(new java.awt.Font("Tahoma", 1, 11));
			jLabel26.setText("Rimmington:");

			jLabel27.setText("2 Clay, 2 Tin, 5 Copper, 6 Iron, 2 Gold");

			jLabel28.setFont(new java.awt.Font("Tahoma", 1, 11));
			jLabel28.setText("Barbarian Village:");

			jLabel29.setText("5 Tin, 4 Coal");

			jLabel30.setText("2 Adamant");

			chkthird.setFont(new java.awt.Font("Tahoma", 1, 11));
			chkthird.setSelected(true);
			chkthird.setText("Third Rock?");
			chkthird.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(final java.awt.event.ActionEvent evt) {
					chkthirdActionPerformed(evt);
				}
			});

			thirdRock.setModel(new javax.swing.DefaultComboBoxModel(
					new String[] { "Tin", "Copper", "Clay", "Iron", "Silver",
							"Gold", "Coal", "Mithril", "Adamant" }));

			thirdRockLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
			thirdRockLabel.setText("Third rock to mine:");
			thirdRockLabel.setOpaque(true);

			jLabel31.setFont(new java.awt.Font("Tahoma", 1, 11));
			jLabel31.setText("Mining Guild:");

			jLabel32.setText("37 Coal, 5 Mithril");

			jLabel33.setFont(new java.awt.Font("Tahoma", 1, 11));
			jLabel33.setText("West Lumbridge Swamp:");

			jLabel34.setText("7 Coal, 5 Mithril, 2 Adamant");

			jLabel35.setFont(new java.awt.Font("Tahoma", 1, 11));
			jLabel35.setText("Update your script here!");

			jLabel36
					.setText("Click the 'Check for update' button to see if you have the latest script.");

			jLabel37
					.setText("If you don't, You will be asked if you would like to connect to the internet to");

			jLabel38.setText("update.");

			jButton3.setText("Check for update");
			jButton3.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(final java.awt.event.ActionEvent evt) {
				}
			});

			final javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(
					jPanel3);
			jPanel3.setLayout(jPanel3Layout);
			jPanel3Layout
					.setHorizontalGroup(jPanel3Layout
							.createParallelGroup(
									javax.swing.GroupLayout.Alignment.LEADING)
							.addGroup(
									jPanel3Layout
											.createSequentialGroup()
											.addContainerGap()
											.addGroup(
													jPanel3Layout
															.createParallelGroup(
																	javax.swing.GroupLayout.Alignment.LEADING)
															.addComponent(
																	jSeparator2,
																	javax.swing.GroupLayout.PREFERRED_SIZE,
																	251,
																	javax.swing.GroupLayout.PREFERRED_SIZE)
															.addGroup(
																	jPanel3Layout
																			.createSequentialGroup()
																			.addComponent(
																					jLabel14)
																			.addPreferredGap(
																					javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																			.addComponent(
																					jLabel15))
															.addGroup(
																	jPanel3Layout
																			.createSequentialGroup()
																			.addGroup(
																					jPanel3Layout
																							.createParallelGroup(
																									javax.swing.GroupLayout.Alignment.LEADING)
																							.addComponent(
																									jLabel16)
																							.addGroup(
																									jPanel3Layout
																											.createSequentialGroup()
																											.addGap(
																													10,
																													10,
																													10)
																											.addComponent(
																													mineLocation,
																													javax.swing.GroupLayout.PREFERRED_SIZE,
																													javax.swing.GroupLayout.DEFAULT_SIZE,
																													javax.swing.GroupLayout.PREFERRED_SIZE))
																							.addComponent(
																									jLabel17)
																							.addGroup(
																									jPanel3Layout
																											.createSequentialGroup()
																											.addGap(
																													10,
																													10,
																													10)
																											.addComponent(
																													firstRock,
																													javax.swing.GroupLayout.PREFERRED_SIZE,
																													javax.swing.GroupLayout.DEFAULT_SIZE,
																													javax.swing.GroupLayout.PREFERRED_SIZE))
																							.addComponent(
																									jLabel18)
																							.addGroup(
																									jPanel3Layout
																											.createSequentialGroup()
																											.addGap(
																													10,
																													10,
																													10)
																											.addComponent(
																													secondRock,
																													javax.swing.GroupLayout.PREFERRED_SIZE,
																													javax.swing.GroupLayout.DEFAULT_SIZE,
																													javax.swing.GroupLayout.PREFERRED_SIZE))
																							.addComponent(
																									chkthird)
																							.addComponent(
																									thirdRockLabel)
																							.addGroup(
																									jPanel3Layout
																											.createSequentialGroup()
																											.addGap(
																													10,
																													10,
																													10)
																											.addComponent(
																													thirdRock,
																													javax.swing.GroupLayout.PREFERRED_SIZE,
																													javax.swing.GroupLayout.DEFAULT_SIZE,
																													javax.swing.GroupLayout.PREFERRED_SIZE)))
																			.addPreferredGap(
																					javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																			.addComponent(
																					jSeparator3,
																					javax.swing.GroupLayout.PREFERRED_SIZE,
																					12,
																					javax.swing.GroupLayout.PREFERRED_SIZE)
																			.addPreferredGap(
																					javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																			.addGroup(
																					jPanel3Layout
																							.createParallelGroup(
																									javax.swing.GroupLayout.Alignment.LEADING)
																							.addComponent(
																									jSeparator7,
																									javax.swing.GroupLayout.PREFERRED_SIZE,
																									359,
																									javax.swing.GroupLayout.PREFERRED_SIZE)
																							.addComponent(
																									jSeparator6,
																									javax.swing.GroupLayout.PREFERRED_SIZE,
																									359,
																									javax.swing.GroupLayout.PREFERRED_SIZE)
																							.addComponent(
																									jLabel35)
																							.addComponent(
																									jSeparator4,
																									javax.swing.GroupLayout.PREFERRED_SIZE,
																									251,
																									javax.swing.GroupLayout.PREFERRED_SIZE)
																							.addComponent(
																									jLabel19)
																							.addGroup(
																									jPanel3Layout
																											.createSequentialGroup()
																											.addComponent(
																													jLabel20)
																											.addPreferredGap(
																													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																											.addComponent(
																													jLabel21))
																							.addGroup(
																									jPanel3Layout
																											.createSequentialGroup()
																											.addComponent(
																													jLabel22)
																											.addPreferredGap(
																													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																											.addComponent(
																													jLabel23))
																							.addGroup(
																									jPanel3Layout
																											.createSequentialGroup()
																											.addComponent(
																													jLabel24)
																											.addPreferredGap(
																													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																											.addGroup(
																													jPanel3Layout
																															.createParallelGroup(
																																	javax.swing.GroupLayout.Alignment.LEADING)
																															.addComponent(
																																	jLabel30)
																															.addComponent(
																																	jLabel25)))
																							.addGroup(
																									jPanel3Layout
																											.createSequentialGroup()
																											.addComponent(
																													jLabel26)
																											.addPreferredGap(
																													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																											.addComponent(
																													jLabel27))
																							.addGroup(
																									jPanel3Layout
																											.createSequentialGroup()
																											.addComponent(
																													jLabel28)
																											.addPreferredGap(
																													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																											.addComponent(
																													jLabel29))
																							.addGroup(
																									jPanel3Layout
																											.createSequentialGroup()
																											.addComponent(
																													jLabel31)
																											.addPreferredGap(
																													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																											.addComponent(
																													jLabel32))
																							.addGroup(
																									jPanel3Layout
																											.createSequentialGroup()
																											.addComponent(
																													jLabel33)
																											.addPreferredGap(
																													javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																											.addComponent(
																													jLabel34))
																							.addComponent(
																									jLabel36)
																							.addGroup(
																									jPanel3Layout
																											.createParallelGroup(
																													javax.swing.GroupLayout.Alignment.TRAILING,
																													false)
																											.addGroup(
																													javax.swing.GroupLayout.Alignment.LEADING,
																													jPanel3Layout
																															.createSequentialGroup()
																															.addComponent(
																																	jLabel38)
																															.addPreferredGap(
																																	javax.swing.LayoutStyle.ComponentPlacement.RELATED,
																																	javax.swing.GroupLayout.DEFAULT_SIZE,
																																	Short.MAX_VALUE)
																															.addComponent(
																																	jButton3))
																											.addComponent(
																													jLabel37,
																													javax.swing.GroupLayout.Alignment.LEADING)))))
											.addContainerGap(14,
													Short.MAX_VALUE)));
			jPanel3Layout
					.setVerticalGroup(jPanel3Layout
							.createParallelGroup(
									javax.swing.GroupLayout.Alignment.LEADING)
							.addGroup(
									jPanel3Layout
											.createSequentialGroup()
											.addContainerGap()
											.addGroup(
													jPanel3Layout
															.createParallelGroup(
																	javax.swing.GroupLayout.Alignment.BASELINE)
															.addComponent(
																	jLabel15)
															.addComponent(
																	jLabel14,
																	javax.swing.GroupLayout.PREFERRED_SIZE,
																	26,
																	javax.swing.GroupLayout.PREFERRED_SIZE))
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
													jPanel3Layout
															.createParallelGroup(
																	javax.swing.GroupLayout.Alignment.LEADING)
															.addGroup(
																	jPanel3Layout
																			.createSequentialGroup()
																			.addComponent(
																					jLabel19)
																			.addPreferredGap(
																					javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																			.addComponent(
																					jSeparator4,
																					javax.swing.GroupLayout.PREFERRED_SIZE,
																					10,
																					javax.swing.GroupLayout.PREFERRED_SIZE)
																			.addPreferredGap(
																					javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																			.addGroup(
																					jPanel3Layout
																							.createParallelGroup(
																									javax.swing.GroupLayout.Alignment.BASELINE)
																							.addComponent(
																									jLabel20)
																							.addComponent(
																									jLabel21))
																			.addPreferredGap(
																					javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																			.addGroup(
																					jPanel3Layout
																							.createParallelGroup(
																									javax.swing.GroupLayout.Alignment.BASELINE)
																							.addComponent(
																									jLabel22)
																							.addComponent(
																									jLabel23))
																			.addPreferredGap(
																					javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																			.addGroup(
																					jPanel3Layout
																							.createParallelGroup(
																									javax.swing.GroupLayout.Alignment.BASELINE)
																							.addComponent(
																									jLabel24)
																							.addComponent(
																									jLabel25))
																			.addGap(
																					1,
																					1,
																					1)
																			.addComponent(
																					jLabel30)
																			.addPreferredGap(
																					javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																			.addGroup(
																					jPanel3Layout
																							.createParallelGroup(
																									javax.swing.GroupLayout.Alignment.BASELINE)
																							.addComponent(
																									jLabel26)
																							.addComponent(
																									jLabel27))
																			.addPreferredGap(
																					javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																			.addGroup(
																					jPanel3Layout
																							.createParallelGroup(
																									javax.swing.GroupLayout.Alignment.BASELINE)
																							.addComponent(
																									jLabel28)
																							.addComponent(
																									jLabel29))
																			.addPreferredGap(
																					javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																			.addGroup(
																					jPanel3Layout
																							.createParallelGroup(
																									javax.swing.GroupLayout.Alignment.BASELINE)
																							.addComponent(
																									jLabel31)
																							.addComponent(
																									jLabel32))
																			.addPreferredGap(
																					javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																			.addGroup(
																					jPanel3Layout
																							.createParallelGroup(
																									javax.swing.GroupLayout.Alignment.BASELINE)
																							.addComponent(
																									jLabel33)
																							.addComponent(
																									jLabel34))
																			.addGap(
																					62,
																					62,
																					62)
																			.addComponent(
																					jSeparator6,
																					javax.swing.GroupLayout.PREFERRED_SIZE,
																					10,
																					javax.swing.GroupLayout.PREFERRED_SIZE)
																			.addPreferredGap(
																					javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																			.addComponent(
																					jLabel35)
																			.addPreferredGap(
																					javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																			.addComponent(
																					jSeparator7,
																					javax.swing.GroupLayout.PREFERRED_SIZE,
																					10,
																					javax.swing.GroupLayout.PREFERRED_SIZE)
																			.addPreferredGap(
																					javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																			.addComponent(
																					jLabel36)
																			.addPreferredGap(
																					javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																			.addComponent(
																					jLabel37)
																			.addPreferredGap(
																					javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																			.addGroup(
																					jPanel3Layout
																							.createParallelGroup(
																									javax.swing.GroupLayout.Alignment.BASELINE)
																							.addComponent(
																									jLabel38)
																							.addComponent(
																									jButton3))
																			.addGap(
																					15,
																					15,
																					15))
															.addGroup(
																	jPanel3Layout
																			.createSequentialGroup()
																			.addComponent(
																					jLabel16)
																			.addPreferredGap(
																					javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																			.addComponent(
																					mineLocation,
																					javax.swing.GroupLayout.PREFERRED_SIZE,
																					javax.swing.GroupLayout.DEFAULT_SIZE,
																					javax.swing.GroupLayout.PREFERRED_SIZE)
																			.addGap(
																					18,
																					18,
																					18)
																			.addComponent(
																					jLabel17)
																			.addPreferredGap(
																					javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																			.addComponent(
																					firstRock,
																					javax.swing.GroupLayout.PREFERRED_SIZE,
																					javax.swing.GroupLayout.DEFAULT_SIZE,
																					javax.swing.GroupLayout.PREFERRED_SIZE)
																			.addGap(
																					18,
																					18,
																					18)
																			.addComponent(
																					jLabel18)
																			.addPreferredGap(
																					javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																			.addComponent(
																					secondRock,
																					javax.swing.GroupLayout.PREFERRED_SIZE,
																					20,
																					javax.swing.GroupLayout.PREFERRED_SIZE)
																			.addGap(
																					18,
																					18,
																					18)
																			.addComponent(
																					chkthird)
																			.addPreferredGap(
																					javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
																			.addComponent(
																					thirdRockLabel)
																			.addPreferredGap(
																					javax.swing.LayoutStyle.ComponentPlacement.RELATED)
																			.addComponent(
																					thirdRock,
																					javax.swing.GroupLayout.PREFERRED_SIZE,
																					20,
																					javax.swing.GroupLayout.PREFERRED_SIZE)
																			.addContainerGap())
															.addComponent(
																	jSeparator3,
																	javax.swing.GroupLayout.Alignment.TRAILING,
																	javax.swing.GroupLayout.DEFAULT_SIZE,
																	377,
																	Short.MAX_VALUE))));

			jTabbedPane1.addTab("Configuration", jPanel3);

			final javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(
					jPanel1);
			jPanel1.setLayout(jPanel1Layout);
			jPanel1Layout.setHorizontalGroup(jPanel1Layout.createParallelGroup(
					javax.swing.GroupLayout.Alignment.LEADING).addComponent(
					jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 569,
					Short.MAX_VALUE));
			jPanel1Layout.setVerticalGroup(jPanel1Layout.createParallelGroup(
					javax.swing.GroupLayout.Alignment.LEADING).addComponent(
					jTabbedPane1));

			final javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
					getContentPane());
			getContentPane().setLayout(layout);
			layout.setHorizontalGroup(layout.createParallelGroup(
					javax.swing.GroupLayout.Alignment.LEADING).addComponent(
					jPanel1, javax.swing.GroupLayout.Alignment.TRAILING,
					javax.swing.GroupLayout.DEFAULT_SIZE,
					javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
			layout.setVerticalGroup(layout.createParallelGroup(
					javax.swing.GroupLayout.Alignment.LEADING).addComponent(
					jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE,
					javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

			pack();
		}// </editor-fold>

		private void jButton1ActionPerformed(
				final java.awt.event.ActionEvent evt) {
			settings.location = mineLocation.getSelectedItem().toString();
			settings.rock1 = firstRock.getSelectedItem().toString();
			settings.rock2 = secondRock.getSelectedItem().toString();
			settings.thirdRockCheck = chkthird.isSelected();
			settings.rock3 = thirdRock.getSelectedItem().toString();
			settings.save = chkSettings.isSelected();

			if (mineLocation.getSelectedItem() == "Varrock East") {
				Place = "Varrock East";
				bankID = 11402;
				bankrocks = true;
				power = false;
				toPlace = new RSTile[] { new RSTile(3253, 3420),
						new RSTile(3262, 3428), new RSTile(3275, 3427),
						new RSTile(3285, 3420), new RSTile(3290, 3408),
						new RSTile(3291, 3396), new RSTile(3293, 3385),
						new RSTile(3291, 3374), new RSTile(3285, 3365) };
				toBank = reversePath(toPlace);
				bankTile = new RSTile(3253, 3420);
				returnTo = new RSTile(3286, 3365);
				runTo = new RSTile(3287, 3368);
			} else if (mineLocation.getSelectedItem() == "Varrock West") {
				Place = "Varrock West";
				bankID = 11402;
				bankrocks = true;
				power = false;
				toPlace = new RSTile[] { new RSTile(3184, 3437),
						new RSTile(3183, 3429), new RSTile(3173, 3426),
						new RSTile(3172, 3414), new RSTile(3171, 3401),
						new RSTile(3176, 3389), new RSTile(3182, 3380),
						new RSTile(3181, 3371) };
				toBank = reversePath(toPlace);
				bankTile = new RSTile(3182, 3438);
				returnTo = new RSTile(3180, 3371);
				runTo = new RSTile(3180, 3381);
			} else if (mineLocation.getSelectedItem() == "Al Kharid") {
				Place = "Al Kharid";
				bankID = 35647;
				bankrocks = true;
				power = false;
				toPlace = new RSTile[] { new RSTile(3269, 3167),
						new RSTile(3277, 3175), new RSTile(3282, 3189),
						new RSTile(3283, 3204), new RSTile(3289, 3217),
						new RSTile(3291, 3233), new RSTile(3292, 3249),
						new RSTile(3295, 3264), new RSTile(3293, 3278),
						new RSTile(3299, 3294), new RSTile(3298, 3301) };
				toBank = reversePath(toPlace);
				bankTile = new RSTile(3269, 3168);
				returnTo = new RSTile(3300, 3304);
				runTo = new RSTile(3289, 3304);
			} else if (mineLocation.getSelectedItem() == "Rimmington") {
				Place = "Rimmington";
				bankID = 11758;
				bankrocks = true;
				power = false;
				toBank = new RSTile[] { new RSTile(2976, 3240),
						new RSTile(2975, 3249), new RSTile(2977, 3263),
						new RSTile(2981, 3274), new RSTile(2987, 3288),
						new RSTile(2995, 3302), new RSTile(2999, 3311),
						new RSTile(3007, 3320), new RSTile(3008, 3333),
						new RSTile(3007, 3346), new RSTile(3012, 3355) };
				bankTile = new RSTile(3012, 3355);
				toPlace = reversePath(toBank);
				returnTo = new RSTile(2976, 3240);
				runTo = new RSTile(2977, 3251);
			} else if (mineLocation.getSelectedItem() == "Barbarian Village") {
				Place = "Barbarian Village";
				bankID = 26972;
				bankrocks = true;
				power = false;
				toBank = new RSTile[] { new RSTile(3081, 3423),
						new RSTile(3084, 3437), new RSTile(3091, 3449),
						new RSTile(3099, 3464), new RSTile(3099, 3478),
						new RSTile(3093, 3490) };
				bankTile = new RSTile(3094, 3491);
				toPlace = reversePath(toBank);
				returnTo = new RSTile(3081, 3423);
				runTo = new RSTile(3076, 3433);
			} else if (mineLocation.getSelectedItem() == "Mining Guild") {
				Place = "Mining Guild";
				bankID = 11758;
				bankrocks = true;
				power = false;
				bankTile = new RSTile(3012, 3355);
				returnTo = new RSTile(3043, 9737);
			} else if (mineLocation.getSelectedItem() == "West Lumbridge Swamp") {
				Place = "West Lumby Swamp";
				bankID = 2213;
				bankrocks = true;
				power = false;
				toBank = new RSTile[] { new RSTile(3146, 3148),
						new RSTile(3149, 3158), new RSTile(3144, 3167),
						new RSTile(3141, 3181), new RSTile(3140, 3194),
						new RSTile(3136, 3207), new RSTile(3121, 3211),
						new RSTile(3112, 3222), new RSTile(3106, 3235),
						new RSTile(3093, 3243) };
				bankTile = new RSTile(3092, 3244);
				toPlace = reversePath(toBank);
				returnTo = new RSTile(3147, 3149);
				runTo = new RSTile(3162, 3151);
			} else if (mineLocation.getSelectedItem() == "Power-Mining") {
				Place = "Anywhere";
				bankrocks = false;
				power = true;
			}

			if (firstRock.getSelectedItem() == "Tin") {
				rockOne = Tin;
				rockOne1 = "Tin";
			} else if (firstRock.getSelectedItem() == "Copper") {
				rockOne = Copper;
				rockOne1 = "Copper";
			} else if (firstRock.getSelectedItem() == "Clay") {
				rockOne = Clay;
				rockOne1 = "Clay";
			} else if (firstRock.getSelectedItem() == "Iron") {
				rockOne = Iron;
				rockOne1 = "Iron";
			} else if (firstRock.getSelectedItem() == "Silver") {
				rockOne = Silver;
				rockOne1 = "Silver";
			} else if (firstRock.getSelectedItem() == "Gold") {
				rockOne = Gold;
				rockOne1 = "Gold";
			} else if (firstRock.getSelectedItem() == "Coal") {
				rockOne = Coal;
				rockOne1 = "Coal";
			} else if (firstRock.getSelectedItem() == "Mithril") {
				rockOne = Mithril;
				rockOne1 = "Mithril";
			} else if (firstRock.getSelectedItem() == "Adamant") {
				rockOne = Adamant;
				rockOne1 = "Adamant";
			}

			if (secondRock.getSelectedItem() == "Tin") {
				rockTwo = Tin;
				rockTwo2 = "Tin";
			} else if (secondRock.getSelectedItem() == "Copper") {
				rockTwo = Copper;
				rockTwo2 = "Copper";
			} else if (secondRock.getSelectedItem() == "Clay") {
				rockTwo = Clay;
				rockTwo2 = "Clay";
			} else if (secondRock.getSelectedItem() == "Iron") {
				rockTwo = Iron;
				rockTwo2 = "Iron";
			} else if (secondRock.getSelectedItem() == "Silver") {
				rockTwo = Silver;
				rockTwo2 = "Silver";
			} else if (secondRock.getSelectedItem() == "Gold") {
				rockTwo = Gold;
				rockTwo2 = "Gold";
			} else if (secondRock.getSelectedItem() == "Coal") {
				rockTwo = Coal;
				rockTwo2 = "Coal";
			} else if (secondRock.getSelectedItem() == "Mithril") {
				rockTwo = Mithril;
				rockTwo2 = "Mithril";
			} else if (secondRock.getSelectedItem() == "Adamant") {
				rockTwo = Adamant;
				rockTwo2 = "Adamant";
			} else if (secondRock.getSelectedItem() == "None") {
				rockTwo = rockOne;
				rockTwo2 = "None";
			}

			if (chkthird.isSelected()) {
				if (thirdRock.getSelectedItem() == "Tin") {
					rockThree = Tin;
					rockThree3 = "Tin";
				} else if (thirdRock.getSelectedItem() == "Copper") {
					rockThree = Copper;
					rockThree3 = "Copper";
				} else if (thirdRock.getSelectedItem() == "Clay") {
					rockThree = Clay;
					rockThree3 = "Clay";
				} else if (thirdRock.getSelectedItem() == "Iron") {
					rockThree = Iron;
					rockThree3 = "Iron";
				} else if (thirdRock.getSelectedItem() == "Silver") {
					rockThree = Silver;
					rockThree3 = "Silver";
				} else if (thirdRock.getSelectedItem() == "Gold") {
					rockThree = Gold;
					rockThree3 = "Gold";
				} else if (thirdRock.getSelectedItem() == "Coal") {
					rockThree = Coal;
					rockThree3 = "Coal";
				} else if (thirdRock.getSelectedItem() == "Mithril") {
					rockThree = Mithril;
					rockThree3 = "Mithril";
				} else if (thirdRock.getSelectedItem() == "Adamant") {
					rockThree = Adamant;
					rockThree3 = "Adamant";
				} else if (thirdRock.getSelectedItem() == "None") {
					rockThree = rockOne;
					rockThree3 = "None";
				}
			} else if (!chkthird.isSelected()) {
				rockThree = rockOne;
				rockThree3 = "None";
			}
			if (chkSettings.isSelected()) {
				try {
					settings.saveSettings(settings.getSettingsArray(),
							SETTINGS_FILE_NAME);
					log("Successfully saved settings at " + SETTINGS_FILE_NAME);
				} catch (final Exception e) {
					log("saving settings error");
				}
			}
			setVisible(false);
			startScript = true;
		}

		private void jButton2ActionPerformed(
				final java.awt.event.ActionEvent evt) {
			setVisible(false);
			stopScripts();
		}

		private void jButton4ActionPerformed(
				final java.awt.event.ActionEvent evt) {
			if (settings.settingsExist(SETTINGS_FILE_NAME)) {
				try {
					settings.setSettings();
					firstRock.setSelectedItem(settings.rock1);
					secondRock.setSelectedItem(settings.rock2);
					mineLocation.setSelectedItem(settings.location);
					chkthird.setSelected(settings.thirdRockCheck);
					chkSettings.setSelected(settings.save);
					if (settings.thirdRockCheck) {
						thirdRock.setSelectedItem(settings.rock3);
					}
				} catch (final Exception e) {
					log("error getting settings: " + e);
				}
			} else {
			}
		}

	}

	public int[] Adamant = { 11939, 11941, 32435, 32436, 11940, 31083, 31084,
			31085, 31083 };
	// ANIMATIONS
	public int[] Animations = { 624, 625, 626, 627, 628, 629 };
	public int[] axes = { 1265, 1275, 1269, 1271, 1273, 1267 };
	public RSTile Bank;
	public int bankID;
	public boolean bankrocks;
	public RSTile bankTile;
	// MINING GUILD STUFF
	public RSTile[] bankToLadder = new RSTile[] { new RSTile(3012, 3355),
			new RSTile(3023, 3359), new RSTile(3026, 3349),
			new RSTile(3030, 3338), new RSTile(3022, 3338) };
	public int[] Clay = { 711, 9713, 15503, 15504, 15505, 31062, 31063 };
	public int[] Coal = { 11930, 11931, 11932, 11930, 11963, 11964, 2096, 2097,
			14850, 14851, 14852, 32426, 32426, 31068, 31069, 31070, 31068 };
	public int[] Copper = { 11938, 11936, 11963, 11937, 9709, 9708, 9710,
			11960, 11962, 11961, 31080, 31082 };
	public int downLadderID = 2113;
	public double exp = 0;
	public int expgain = 0;
	public int gem = 0;

	public int[] Gold = { 37312, 37310, 9722, 9720, 15505, 15503, 11185, 11184,
			11183, 9720, 9722, 37313, 31065, 31066 };
	public int[] Granite = { 10947 };
	public int[] Iron = { 2093, 2093, 2092, 9717, 9719, 9717, 9718, 11956,
			11955, 11954, 37307, 37309, 31072, 31073, 31071 };
	public RSTile ladder;
	public RSTile ladderLoc = new RSTile(3019, 3338);
	public RSTile ladderLoc2 = new RSTile(3019, 9738);
	public RSTile ladderTile = new RSTile(3019, 3337);
	public RSTile ladderTile2 = new RSTile(3019, 9737);
	public RSTile[] ladderToBank = reversePath(bankToLadder);
	public RSTile[] ladderToMine = new RSTile[] { new RSTile(3019, 9737),
			new RSTile(3026, 9737), new RSTile(3037, 9737) };
	public int mined = 0;
	public RSTile[] mineToLadder = reversePath(ladderToMine);
	public int[] Mithril = { 11942, 11944, 11943, 32438, 32439, 31086, 31087,
			31088 };
	public String name = "";
	public int[] ores = { 453, 434, 449, 436, 444, 440, 447, 451, 442, 438,
			6983, 6981, 6979, 6977, 6971, 6973, 6975 };
	// OTHER MINING STUFF
	public String Place;
	public RSTile playerPos;
	public boolean power;

	public RSTile returnTo;
	public RSTile Rock;
	public int rockID;
	// GENERAL ROCKS AND PICKAXES
	public int rockOne[];
	public String rockOne1;
	public int rockThree[];
	public String rockThree3;
	public int rockTwo[];
	public String rockTwo2;
	public int roll = 0;
	public int[] Runite = { 451 };

	public RSTile runTo;
	public int[] Sandstone = { 10946 };
	private final Settings settings = new Settings();
	private final String SETTINGS_FILE_NAME = "WorldWideMinerSettings.ini";
	public int[] Silver = { 37305, 37304, 37306, 9714, 9716, 9713, 11950,
			11949, 11948, 11950 };
	public int startLevel = 0;
	// OTHER
	public boolean startScript = false;
	// PAINT STUFF
	public long startTime = System.currentTimeMillis();

	public String status = "";
	public int[] Tin = { 11935, 11934, 11933, 11959, 11957, 11959, 11958, 9714,
			9716, 31079, 31077, 31078 };
	// TILES AND PATHS
	public RSTile[] toBank;
	public RSTile[] toPlace;
	public int tries = 0;
	public int upLadderID = 30941;
	public double x1;
	public final int xx1 = random(575, 580);
	public final int xx2 = random(540, 545);
	public final int xx3 = random(575, 580);
	public final int xx4 = random(540, 545);
	public final int xx5 = random(575, 580);
	public final int xx6 = random(540, 545);

	public final int yy1 = random(379, 383);
	public final int yy2 = random(416, 424);
	public final int yy3 = random(402, 407);
	public final int yy4 = random(446, 449);

	public final int yy5 = random(440, 445);

	public final int yy6 = random(458, 463);

	public boolean animationCheck(final int... ids) {
		final int anim = getMyPlayer().getAnimation();
		for (final int id : ids) {
			if (id == anim) {
				return true;
			}
		}
		return false;
	}

	public void antiban() {
		roll = random(0, 20);
		if (roll == 7) {
			setCameraRotation(random(1, 360));
		}
	}

	private int bankstate() {
		try {
			if (!bank.isOpen()) {
				openBank();
				wait(random(500, 900));
				depositMine();
				return 500;
			} else if (bank.isOpen()) {
				depositMine();
				return 500;
			}
		} catch (final Exception e) {
		}
		return 30;
	}

	public void checkenergy() {
		if (!isRunning()) {
			if (getEnergy() >= random(50, 100)) {
				log("You have enough energy, turning on 'run'");
				setRun(true);
			}
		}
	}

	public int clickRock(final RSObject rock) {
		try {
			Rock = rock.getLocation();
			playerPos = getMyPlayer().getLocation();
			final Point location = Calculations.tileToScreen(Rock);
			final Point mouse = getMouseLocation();
			if (location.x == -1 || location.y == -1) {
				return 500;
			}
			if (Math.abs(location.x - mouse.x) > 5) {
				status = "Moving mouse to rock...";
				moveMouse(location, 2, 2);
			}
			if (getMenuActions().contains("Mine")) {
				status = "Clicking rock...";
				if (getMenuIndex("Mine") == 0) {
					atObject(rock, "Mine");
				} else {
					clickMouse(true);
				}
				wait(300);
				while (getMyPlayer().isMoving()) {
					rockThere(rock);
					wait(10);
				}
				wait(300);
				if (power) {
					HoverRock();
				} else if (!power) {
					Hover();
				}
				if (!animationCheck(Animations)) {
					tries++;
				} else if (animationCheck(Animations)) {
					tries = 0;
					rockThere(rock);
				}
				if (tries >= 10) {
					tries = 0;
					setCameraRotation(random(1, 360));
				}
			} else if (!getMenuActions().contains("Mine")) {
				tries++;
				if (tries >= 10) {
					tries = 0;
					setCameraRotation(random(1, 360));
				}
				wait(150);
			}
		} catch (final Exception e) {
		}
		return 50;
	}

	public void depositMine() {
		try {
			if (RSInterface.getInterface(Constants.INTERFACE_BANK).isValid()) {
				status = "Depositing rocks...";
				bank.depositAllExcept(axes);
				wait(400);
				bank.close();
			}
		} catch (final Exception e) {
		}
	}

	// NEEDED RANDOM THINGS

	private int downLadder() {
		final RSObject Ladder = getObjectAt(ladderLoc);
		if (Ladder == null) {
			toLadderFromBank();
		} else if (Ladder != null) {
			try {
				if (getMyPlayer().getLocation() != ladderTile) {
					if (tileOnMap(ladderTile)) {
						walkTo(ladderTile);
						wait(3000);
					}
				}
			} catch (final Exception e) {
			}
			while (getMyPlayer().isMoving()) {
				wait(30);
			}
			setCompass('n');
			ladder = Ladder.getLocation();
			final Point location = Calculations.tileToScreen(ladder);
			if (location.x == -1 || location.y == -1) {
				return 500;
			}
			clickMouse(location, 1, 1, false);
			if (getMenuActions().contains("Climb-down")) {
				atMenu("Climb-down");
				wait(3000);
			} else {
				atMenu("Cancel");
				setCameraRotation(random(1, 360));
				if (tileOnMap(ladderTile)) {
					walkTo(ladderTile);
					wait(3000);
				}
				wait(500);
			}
		}
		return 50;
	}

	public void Drop() { // Thanks Dwuxi
		status = "Dropping rocks...";
		if (isInventoryFull()) {
			if (getCurrentTab() == Constants.TAB_INVENTORY) {
				clickMouse(xx1, yy1, 3, 3, false);
				clickMouse(xx2, yy2, 3, 3, true);
				clickMouse(xx3, yy3, 3, 3, false);
				clickMouse(xx4, yy4, 3, 3, true);
				clickMouse(xx5, yy5, 3, 3, false);
				clickMouse(xx6, yy6, 3, 3, true);
			} else {
				openTab(Constants.TAB_INVENTORY);
			}
		}
	}

	private RSObject findSecondRock(final int... rockIDs) {
		final int X = Bot.getClient().getBaseX();
		final int Y = Bot.getClient().getBaseY();
		RSObject rockA = null;
		RSObject rockB = null;
		double distA = Double.MAX_VALUE;
		double distB = Double.MAX_VALUE;
		RSObject rockT;
		boolean identified;
		double distT;
		try {
			for (int x = 0; x < 104; x++) {
				for (int y = 0; y < 104; y++) {
					if ((rockT = getObjectAt((X + x), (Y + y))) != null) {
						identified = false;
						for (final int ID : rockIDs) {
							if (ID == rockT.getID()) {
								identified = true;
								break;
							}
						}
						if (identified) {
							if ((distT = distanceTo(rockT)) < distA) {
								rockB = rockA;
								distB = distA;
								rockA = rockT;
								distA = distT;
							} else if (distT < distB) {
								rockB = rockT;
								distB = distT;
							}
						}
					}
				}
			}
		} catch (final Exception e) {
		}
		return rockB;
	}

	// GET STUFF

	private Status getState() {
		if (power) {
			if (!isInventoryFull()) {
				return Status.minestate;
			}
			if (isInventoryFull()) {
				return Status.dropstate;
			}
		} else if (bankrocks && Place != "Mining Guild") {
			if (isInventoryFull() && nearBank()) {
				return Status.bankstate;
			}
			if (Place != "Al Kharid") {
				if (distanceTo(returnTo) <= 15 && !isInventoryFull()) {
					return Status.minestate;
				}
			} else if (Place == "Al Kharid") {
				if (distanceTo(returnTo) <= 20 && !isInventoryFull()) {
					return Status.minestate;
				}
			}
			if (!isInventoryFull() && distanceTo(returnTo) > 15) {
				return Status.walkToPlacestate;
			}
			if (isInventoryFull() && !nearBank()) {
				return Status.walkToBankstate;
			}
		} else if (bankrocks && Place == "Mining Guild") {
			if (isInventoryFull() && nearBank()) {
				return Status.bankstate;
			}
			if (!isInventoryFull() && !playerInArea(3024, 3342, 3014, 3336)
					&& getMyPlayer().getLocation().getY() < 9000) {
				return Status.walkToDown;
			}
			if (!isInventoryFull() && playerInArea(3024, 3342, 3014, 3336)) {
				return Status.downLadderState;
			}
			if (!isInventoryFull() && getMyPlayer().getLocation().getY() > 9000
					&& distanceTo(returnTo) > 15) {
				return Status.walkToPlacestate;
			}
			if (!isInventoryFull() && distanceTo(returnTo) <= 15) {
				return Status.minestate;
			}
			if (isInventoryFull() && getMyPlayer().getLocation().getY() > 9000
					&& !playerInArea(3021, 9741, 3017, 9737)) {
				return Status.walkToUp;
			}
			if (isInventoryFull() && getMyPlayer().getLocation().getY() > 9000
					&& playerInArea(3021, 9741, 3017, 9737)) {
				return Status.upLadderState;
			}
			if (isInventoryFull() && getMyPlayer().getLocation().getY() < 9000
					&& !nearBank()) {
				return Status.walkToBankstate;
			}
		}
		return null;
	}

	public int Hover() {
		try {
			final RSObject rock = findSecondRock(rockOne);
			final RSObject rock2 = findSecondRock(rockTwo);
			if (rock == null) {
				if (rock2 != null) {
					Rock = rock2.getLocation();
					final Point location = Calculations.tileToScreen(Rock);
					final Point mouse = getMouseLocation();
					if (location.x == -1 || location.y == -1) {
						return 500;
					}
					if (Math.abs(location.x - mouse.x) <= 5) {
						return 150;
					}
					moveMouse(location, 2, 2);
				}
			} else if (rock != null) {
				Rock = rock.getLocation();
				final Point location = Calculations.tileToScreen(Rock);
				final Point mouse = getMouseLocation();
				if (location.x == -1 || location.y == -1) {
					return 500;
				}
				if (Math.abs(location.x - mouse.x) <= 5) {
					return 150;
				}
				moveMouse(location, 2, 2);
			}
		} catch (final Exception e) {
		}
		return 50;
	}

	public int HoverRock() {

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
				for (final int id : ores) {
					if (items[i] == id) {
						possible.add(i);
					}
				}
			}
			if (possible.size() == 0) {
				return 10;
			}
			final int ida = possible.get(random(0, possible.size()));
			final Point t = getInventoryItemPoint(ida);
			clickMouse(t, 5, 5, false);
			int idx = getMenuIndex("Drop");
			// System.out.println((optionContains + " " + idx + " " +
			// getMenuItems());
			if (idx == -1) {
				idx = getMenuIndex("Cancel");
				final RSTile menu = getMenuLocation();
				final int xOff = random(4, getMenuItems().get(idx).length() * 4);
				final int yOff = random(21, 29) + 15 * idx;
				clickMouse(menu.getX() + xOff, menu.getY() + yOff, 2, 2, true);
				return 10;
			} else {
				final RSTile menu = getMenuLocation();
				final int xOff = random(4, getMenuItems().get(idx).length() * 4);
				final int yOff = random(21, 29) + 15 * idx;
				moveMouse(menu.getX() + xOff, menu.getY() + yOff, 2, 2);
				return 10;
			}
		} catch (final Exception e) {
		}
		return 50;
	}

	public int loop() {
		if (!isLoggedIn()) {
			return random(1000, 15000);
		}

		checkenergy();
		antiban();
		setCameraAltitude(true);
		if (bankrocks && Place != "Mining Guild") {
			switch (getState()) {
			case bankstate:
				status = "Banking...";
				bankstate();
				return 50;
			case walkToBankstate:
				status = "Walking to bank...";
				walktobankstate();
				return 50;
			case minestate:
				status = "Mining...";
				Mine();
				return 50;
			case walkToPlacestate:
				status = "Walking to mine...";
				walktoplacestate();
				return 50;
			default:
				return 50;
			}
		} else if (bankrocks && Place == "Mining Guild") {
			switch (getState()) {
			case bankstate:
				status = "Banking...";
				bankstate();
				return 50;
			case walkToDown:
				status = "Walking to down ladder...";
				toLadderFromBank();
				return 50;
			case downLadderState:
				status = "Going down ladder...";
				downLadder();
				return 50;
			case walkToPlacestate:
				status = "Walking to mine...";
				toMineFromLadder();
				return 50;
			case minestate:
				status = "Mining...";
				Mine();
				return 50;
			case walkToUp:
				status = "Walking to up ladder...";
				toLadderFromMine();
				return 50;
			case upLadderState:
				status = "Going up ladder...";
				upLadder();
				return 50;
			case walkToBankstate:
				status = "Walking to bank...";
				toBankFromLadder();
				return 50;
			default:
				return 50;
			}
		} else if (power) {
			switch (getState()) {
			case minestate:
				status = "Mining...";
				Mine();
				return 50;
			case dropstate:
				status = "Dropping rocks...";
				Drop();
				return 50;
			default:
				return 50;
			}
		}
		return 50;
	}

	public int Mine() {
		runCombat();
		if (!power) {
			if (animationCheck(Animations)) {
				Hover();
				return 10;
			}
		} else if (power) {
			if (animationCheck(Animations)) {
				if (!isMenuOpen()) {
					HoverRock();
					return 10;
				}
				return 10;
			}
		}
		while (getMyPlayer().isMoving()) {
			return 10;
		}
		if (power) {
			if (getMenuActions().contains("Drop")) {
				clickMouse(true);
				wait(200);
			}
		}
		status = "Searching for rock...";
		final RSObject rock1 = getNearestObjectByID(rockOne);
		final RSObject rock2 = getNearestObjectByID(rockTwo);
		final RSObject rock3 = getNearestObjectByID(rockThree);
		if (rock1 == null && rock2 == null && rock3 == null) {
			if (getMyPlayer().getLocation() != returnTo) {
				try {
					if (tileOnMap(returnTo)) {
						walkTo(returnTo);
					}
					return 50;
				} catch (final Exception e) {
				}
			}
		}
		if (rock1 != null) {
			if (tileOnScreen(rock1.getLocation())) {
				clickRock(rock1);
				return 50;
			} else {
				try {
					if (tileOnMap(rock1.getLocation())) {
						walkTo(rock1.getLocation());
					}
					return 50;
				} catch (final Exception e) {
				}

			}
		}
		if (rock2 != null) {
			if (tileOnScreen(rock2.getLocation())) {
				clickRock(rock2);
				return 50;
			} else {
				try {
					if (tileOnMap(rock2.getLocation())) {
						walkTo(rock2.getLocation());
					}
					return 50;
				} catch (final Exception e) {
				}
			}
		}
		if (rock3 != null) {
			if (tileOnScreen(rock3.getLocation())) {
				clickRock(rock3);
				return 50;
			} else {
				try {
					if (tileOnMap(rock3.getLocation())) {
						walkTo(rock3.getLocation());
					}
					return 50;
				} catch (final Exception e) {
				}
			}
		}
		return 50;
	}

	public boolean nearBank() {
		final RSObject banker = getNearestObjectByID(bankID);
		if (banker == null || distanceTo(banker) >= 5) {
			return false;
		} else {
			return true;
		}
	}

	public boolean needtobankMine() {
		if (isInventoryFull()) {
			return true;
		} else {
			return false;
		}
	}

	public void onFinish() {
		Bot.getEventManager().removeListener(PaintListener.class, this);
		return;
	}

	public void onRepaint(final Graphics render) {
		long millis = System.currentTimeMillis() - startTime;
		final long hours = millis / (1000 * 60 * 60);
		millis -= hours * 1000 * 60 * 60;
		final long minutes = millis / (1000 * 60);
		millis -= minutes * 1000 * 60;
		final long seconds = millis / 1000;
		float rocksec = 0;
		if ((minutes > 0 || hours > 0 || seconds > 0) && exp > 0) {
			rocksec = (float) mined
					/ (float) (seconds + minutes * 60 + hours * 60 * 60);
		}
		final float rockmin = rocksec * 60;
		final float rockhour = rockmin * 60;

		final int LevelChange = skills
				.getCurrentSkillLevel(Constants.STAT_MINING)
				- startLevel;
		if (startScript) {
			render.setColor(new Color(215, 218, 231, 85));
			render.fill3DRect(4, 133, 210, 205, true);
			render.setColor(Color.black);
			render.setFont(new Font("sansserif", Font.BOLD, 12));
			render.drawString("World-Wide-Miner", 7, 152);
			render.setColor(Color.cyan);
			render.drawString("World-Wide-Miner", 8, 151);
			render.setColor(Color.black);
			render.setFont(new Font("sansserif", Font.PLAIN, 12));
			render.drawString("Time running: " + hours + " hrs " + minutes
					+ " mins " + seconds + " secs", 7, 169);
			if (bankrocks) {
				render.drawString("Mining Type: Banking", 7, 187);
			} else if (power) {
				render.drawString("Mining Type: Power-Mining", 7, 187);
			}
			render.drawString("Mining Location: " + Place, 7, 205);
			if (rockOne1.equals(rockTwo2) && rockOne1.equals(rockThree3)) {
				render.drawString("Currently Mining: " + rockOne1, 7, 223);
			} else if (!rockOne1.equals(rockTwo2)
					&& rockOne1.equals(rockThree3)) {
				render.drawString("Currently Mining: " + rockOne1 + ", "
						+ rockTwo2, 7, 223);
			} else {
				render.drawString("Currently Mining: " + rockOne1 + ", "
						+ rockTwo2 + ", " + rockThree3, 7, 223);
			}
			render.drawString("Rocks Mined: " + mined, 7, 241);
			render.drawString("Mined per hour: " + (int) rockhour + " Rocks",
					7, 259);
			render.drawString("Exp Gained: " + exp, 7, 277);
			render.drawString("Levels Gained: " + LevelChange, 7, 295);
			render.drawString("Gems found: " + gem, 7, 313);
			render.setColor(Color.cyan);
			render.drawString("Status: " + status, 7, 331);
		}
	}

	// ----------------------------------------------//
	// -----------------STATE STUFF------------------//
	// ----------------------------------------------//

	// ONSTART/FINISH
	public boolean onStart(final Map<String, String> args) {
		new WWMGUI().setVisible(true);
		while (!startScript) {
			wait(10);
		}
		if (isLoggedIn()) {
			startLevel = skills.getCurrentSkillLevel(Constants.STAT_MINING);
		}
		return true;
	}

	public void openBank() {
		try {
			final RSObject bankbooth = getNearestObjectByID(bankID);
			if (bankbooth != null) {
				while (getMyPlayer().isMoving()) {
					wait(10);
				}
				if (getMyPlayer().getLocation() == new RSTile(3010, 3359)
						|| getMyPlayer().getLocation() == new RSTile(3011, 3359)) {
					walkTo(new RSTile(3013, 3359));
				}
				if (distanceTo(bankTile) > 2) {
					if (tileOnMap(bankTile)) {
						walkTo(bankTile);
						wait(300);
						openBank();
					}
				} else if (distanceTo(bankTile) <= 2) {
					status = "Opening bank...";
					final RSObject Banker = getNearestObjectByID(bankID);
					Bank = Banker.getLocation();
					final Point location = Calculations.tileToScreen(Bank);
					if (location.x == -1 || location.y == -1) {
						wait(500);
					}
					clickMouse(location, 2, 2, false);
					wait(300);
					if (getMenuActions().contains("Use-quickly")) {
						atMenu("Use-quickly");
						wait(800);
					} else {
						if (getMenuActions().contains("Use-quickly")) {
							atMenu("Cancel");
							setCameraRotation(random(1, 360));
						}
					}
				}
			} else {
				walkPathMM(randomizePath(toBank, 2, 2), 17);
			}
		} catch (final Exception e) {
		}
	}

	private boolean playerInArea(final int maxX, final int maxY,
			final int minX, final int minY) { //
		final int x = getMyPlayer().getLocation().getX();
		final int y = getMyPlayer().getLocation().getY();
		if (x >= minX && x <= maxX && y >= minY && y <= maxY) {
			return true;
		}
		return false;
	}

	public int rockThere(final RSObject rock) {
		try {
			if (rock != null) {
				rockID = rock.getID();
				Rock = rock.getLocation();
				playerPos = getMyPlayer().getLocation();
				playerPos = new RSTile(playerPos.getX() - 1, playerPos.getY());
				final Point clickStop = Calculations.tileToScreen(playerPos);
				if (getObjectAt(Rock).getID() != rockID) {
					clickMouse(clickStop, 2, 2, true);
					return 200;
				}
			}
		} catch (final Exception e) {
		}
		return 50;
	}

	public void runCombat() {
		try {
			if (distanceTo(returnTo) <= 30) {
				if (getMyPlayer().isInCombat()) {
					log("In Combat, Running!");
					status = "Running from combat...";
					setRun(true);
					if (tileOnMap(runTo)) {
						walkTo(runTo);
						wait(20000);
					}
				}
			}
		} catch (final Exception e) {
		}
	}

	public void serverMessageRecieved(final ServerMessageEvent e) {
		final String word = e.getMessage().toLowerCase();
		if (word.contains("manage to mine") && word.contains("tin")) {
			mined++;
			exp = exp + 17.5;
		}
		if (word.contains("manage to mine") && word.contains("copper")) {
			mined++;
			exp = exp + 17.5;
		}
		if (word.contains("manage to mine") && word.contains("clay")) {
			mined++;
			exp = exp + 5;
		}
		if (word.contains("manage to mine") && word.contains("iron")) {
			mined++;
			exp = exp + 35;
		}
		if (word.contains("manage to mine") && word.contains("silver")) {
			mined++;
			exp = exp + 40;
		}
		if (word.contains("manage to mine") && word.contains("coal")) {
			mined++;
			exp = exp + 50;
		}
		if (word.contains("manage to mine") && word.contains("gold")) {
			mined++;
			exp = exp + 65;
		}
		if (word.contains("manage to mine") && word.contains("mithril")) {
			mined++;
			exp = exp + 80;
		}
		if (word.contains("manage to mine") && word.contains("adamant")) {
			mined++;
			exp = exp + 95;
		}

		if (word.contains("died")) {
			log("You have died!");
			stopScript();
		}
		if (word.contains("you just found")) {
			gem++;
			exp = exp + 65;
		}
	}

	public void stopScripts() {
		System.out.println("Script Stopped");
		Bot.getScriptHandler().stopScript();
	}

	private int toBankFromLadder() {
		try {
			if (distanceTo(getDestination()) > 5) {
				if (getMyPlayer().isMoving()) {
					return 800;
				}
			}
			walkPathMM(randomizePath(ladderToBank, 2, 2), 18);
		} catch (final Exception e) {
		}
		return 50;
	}

	private int toLadderFromBank() {
		try {
			if (distanceTo(getDestination()) > 5) {
				if (getMyPlayer().isMoving()) {
					return 800;
				}
			}
			walkPathMM(randomizePath(bankToLadder, 2, 2), 17);
		} catch (final Exception e) {
		}
		return 50;
	}

	private int toLadderFromMine() {
		try {
			if (distanceTo(getDestination()) > 5) {
				if (getMyPlayer().isMoving()) {
					return 800;
				}
			}
			walkPathMM(randomizePath(mineToLadder, 2, 2), 17);
		} catch (final Exception e) {
		}
		return 50;
	}

	private int toMineFromLadder() {
		try {
			if (distanceTo(returnTo) >= 5 || returnTo == null) {
				if (distanceTo(returnTo) <= 15) {
					if (tileOnMap(returnTo)) {
						walkTo(returnTo);
					}
					return 500;
				} else {
					if (distanceTo(getDestination()) > 5) {
						if (getMyPlayer().isMoving()) {
							return 800;
						}
					}
					walkPathMM(randomizePath(ladderToMine, 2, 2), 17);
					return 500;
				}
			}
		} catch (final Exception e) {
		}
		return 30;
	}

	private int upLadder() {
		final RSObject Ladder = getObjectAt(ladderLoc2);
		if (Ladder == null) {
			toLadderFromMine();
		} else if (Ladder != null) {
			try {
				if (getMyPlayer().getLocation() != ladderTile2) {
					if (tileOnMap(ladderTile2)) {
						walkTo(ladderTile2);
						wait(3000);
					}
				}
			} catch (final Exception e) {
			}
			while (getMyPlayer().isMoving()) {
				wait(30);
			}
			setCompass('n');
			ladder = Ladder.getLocation();
			final Point location = Calculations.tileToScreen(ladder);
			if (location.x == -1 || location.y == -1) {
				return 500;
			}
			clickMouse(location, 1, 1, false);
			if (getMenuActions().contains("Climb-up")) {
				atMenu("Climb-up");
				wait(3000);
			} else {
				atMenu("Cancel");
				setCameraRotation(random(1, 360));
				if (tileOnMap(ladderTile2)) {
					walkTo(ladderTile2);
					wait(3000);
				}
				wait(500);
			}
		}
		return 50;
	}

	// ----------------------------------------------//
	// ---------------END STATE STUFF----------------//
	// ----------------------------------------------//

	private int walktobankstate() {
		try {
			if (distanceTo(getDestination()) > 5) {
				if (getMyPlayer().isMoving()) {
					return 800;
				}
			}
			walkPathMM(randomizePath(toBank, 2, 2), 18);
		} catch (final Exception e) {
		}
		return 50;
	}

	private int walktoplacestate() {
		try {
			if (distanceTo(returnTo) >= 5 || returnTo == null) {
				if (distanceTo(returnTo) <= 15) {
					if (tileOnMap(returnTo)) {
						walkTo(returnTo);
					}
					return 500;
				} else {
					if (distanceTo(getDestination()) > 5) {
						if (getMyPlayer().isMoving()) {
							return 800;
						}
					}
					walkPathMM(randomizePath(toPlace, 2, 2), 17);
					return 500;
				}
			}
		} catch (final Exception e) {
		}
		return 30;
	}
}
