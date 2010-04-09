import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import org.rsbot.accessors.Node;
import org.rsbot.accessors.RSNPCNode;
import org.rsbot.bot.Bot;
import org.rsbot.bot.input.CanvasWrapper;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Methods;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSArea;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSItemTile;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSPlayer;
import org.rsbot.script.wrappers.RSTile;

/**
 * Created by IntelliJ IDEA. User: Bool Date: 30-okt-2009 Time: 23:39:10
 */
@ScriptManifest(authors = { "Bool" }, category = "Combat", name = "BPestControlV2", version = 2.0, description = "<html><body style=\"font-family: Arial; padding: 10px;\">Select your account and press OK to show the GUI.</body></html>")
public class BPestControlV2 extends Script implements PaintListener {
	ScriptManifest sm = getClass().getAnnotation(ScriptManifest.class);
	double version = sm.version();

	class animationChecker implements Runnable {
		public void run() {
			while (isActive) {
				if (getMyPlayer().getAnimation() != -1) {
					lastAniChange = System.currentTimeMillis();
				}
				try {
					Thread.sleep(500);
				} catch (Exception ignored) {
				}
			}
		}

	}

	class BGUI extends JFrame implements ActionListener {
		private static final long serialVersionUID = 1L;
		private JPanel contentPane;
		private JTabbedPane tabbedPane;
		private JTable table;
		private JTextField textField;
		private JTextField textField_1;
		private JButton btnAddBreak;
		private DefaultTableModel model;
		private DefaultListModel jobmodel;

		private JButton btnAddAntiban;
		private JComboBox comboBox_1;
		private JList list_1;
		private JButton btnAddJob;
		private JList list;
		private JComboBox comboBox;

		public BGUI() {
			initGUI();
		}

		private void initGUI() {
			setAlwaysOnTop(true);
			setResizable(false);
			try {
				UIManager.setLookAndFeel(UIManager
						.getSystemLookAndFeelClassName());
			} catch (Exception ignored) {
			}
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setBounds(100, 100, 450, 288);
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			contentPane.setLayout(null);
			tabbedPane = new JTabbedPane(SwingConstants.LEFT);
			tabbedPane.setBounds(10, 10, 414, 218);
			contentPane.add(tabbedPane);

			final JPanel panel = new JPanel();
			tabbedPane.addTab("Main", null, panel, null);

			btnStartScript = new JButton("Start script");
			btnStartScript.addActionListener(this);
			btnStartScript.setBounds(327, 234, 97, 23);
			contentPane.add(btnStartScript);
			panel.setLayout(null);

			final JPanel panel_2 = new JPanel();
			tabbedPane.addTab("Antiban", null, panel_2, null);

			chckbxUseSpecialAttack = new JCheckBox("Use special attack");
			chckbxUseSpecialAttack.setBounds(10, 10, 126, 17);
			panel.add(chckbxUseSpecialAttack);

			chckbxUseQuickPrayer = new JCheckBox("Use quick prayer");
			chckbxUseQuickPrayer.setBounds(10, 27, 126, 17);
			panel.add(chckbxUseQuickPrayer);

			comboBox = new JComboBox();
			comboBox.setModel(new DefaultComboBoxModel(jobsStrings));
			comboBox.setBounds(10, 50, 126, 24);
			panel.add(comboBox);

			btnAddJob = new JButton("Add Job");
			btnAddJob.setBounds(20, 80, 103, 23);
			btnAddJob.addActionListener(this);
			panel.add(btnAddJob);

			final JScrollPane scrollPane = new JScrollPane();
			scrollPane.setBounds(180, 10, 126, 193);
			panel.add(scrollPane);

			jobmodel = new DefaultListModel();
			list = new JList(jobmodel);
			scrollPane.setViewportView(list);
			list.setBorder(new TitledBorder(null, "", TitledBorder.LEADING,
					TitledBorder.TOP, null, null));

			final JPanel panel_7 = new JPanel();
			tabbedPane.addTab("Break handler", null, panel_7, null);
			panel_2.setLayout(null);
			m = new DefaultListModel();
			list_1 = new JList(m);
			list_1.setBorder(new LineBorder(new Color(0, 0, 0)));
			list_1.setBounds(10, 10, 307, 164);
			panel_2.add(list_1);

			btnAddAntiban = new JButton("Add antiban");
			btnAddAntiban.addActionListener(this);
			btnAddAntiban.setBounds(10, 180, 97, 23);
			panel_2.add(btnAddAntiban);

			comboBox_1 = new JComboBox();
			comboBox_1.setModel(new DefaultComboBoxModel(new String[] {
					"rotate compass", "move mouse",
					"move mouse and rotate compass", "rest",
					"right click object", "right click npc",
					"right click player" }));
			comboBox_1.setBounds(113, 180, 204, 23);
			panel_2.add(comboBox_1);
			panel_7.setLayout(null);

			table = new JTable();
			table.setBorder(new LineBorder(new Color(0, 0, 0)));
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			table.setBounds(10, 10, 301, 164);
			model = new DefaultTableModel();
			table.setModel(model);
			model.addColumn("");
			model.addColumn("");
			panel_7.add(table);

			btnAddBreak = new JButton("Add break");
			btnAddBreak.addActionListener(this);
			btnAddBreak.setBounds(10, 180, 89, 23);
			panel_7.add(btnAddBreak);

			final JLabel lblBreakFor = new JLabel("Break for");
			lblBreakFor.setBounds(105, 183, 52, 16);
			panel_7.add(lblBreakFor);

			textField = new JTextField();
			textField.setBounds(158, 181, 27, 21);
			panel_7.add(textField);
			textField.setColumns(10);

			final JLabel lblAfter = new JLabel("after ");
			lblAfter.setBounds(190, 183, 27, 16);
			panel_7.add(lblAfter);

			textField_1 = new JTextField();
			textField_1.setBounds(218, 181, 26, 21);
			panel_7.add(textField_1);
			textField_1.setColumns(10);

			btnRemove = new JButton("Delete");
			btnRemove.addActionListener(this);
			btnRemove.setBounds(248, 181, 65, 23);
			panel_7.add(btnRemove);

			comboBox_2 = new JComboBox();
			comboBox_2.setModel(new DefaultComboBoxModel(new String[] {
					"Novice", "Intermediate", "Veteran" }));
			comboBox_2.setBounds(10, 181, 141, 22);
			panel.add(comboBox_2);

			chckbxAttackPortals = new JCheckBox("Attack portals");
			chckbxAttackPortals.setBounds(10, 148, 105, 17);
			panel.add(chckbxAttackPortals);

			specialtxt = new JTextField();
			specialtxt.setText("  %");
			specialtxt.setBounds(137, 10, 31, 19);
			panel.add(specialtxt);
			specialtxt.setColumns(10);
			chckbxattackspinners = new JCheckBox("Attack spinners");
			chckbxattackspinners.setBounds(10, 160, 113, 17);
			panel.add(chckbxattackspinners);
		}

		DefaultListModel m;
		private JButton btnRemove;

		public void actionPerformed(final ActionEvent e) {
			if (e.getSource() == btnStartScript) {
				start = true;
				attackPortals = chckbxAttackPortals.isSelected();
				attackSpinner = chckbxattackspinners.isSelected();
				if (pickupArrows) {
					if (getCurrentTab() != TAB_EQUIPMENT)
						openTab(TAB_EQUIPMENT);
					arrowID = getEquipmentArray()[13];
				}
				if (DBaxe) {
					if (getCurrentTab() != TAB_EQUIPMENT)
						openTab(TAB_EQUIPMENT);
					oldID = getEquipmentArray()[3];
				}
				usePray = chckbxUseQuickPrayer.isSelected();
				if (chckbxUseSpecialAttack.isSelected()) {
					try {
						specialPercent = Integer.parseInt(specialtxt.getText());
					} catch (Exception check) {
						specialPercent = 0;
					}
				} else {
					specialPercent = 0;
				}
				for (int i = 0; i < jobmodel.getSize(); i++) {
					int index = 0;
					for (int j = 0; j < jobsStrings.length; j++)
						if (jobsStrings[j].equals(jobmodel.get(i).toString()))
							index = j;
					jobQ.add(new Job(jobmodel.get(i).toString(),
							jobsPoints[index]));
				}
				for (int i = 0; i < m.getSize(); i++) {
					antibanQ.add(m.get(i).toString());
				}
				int i = comboBox_2.getSelectedIndex();
				log("" + i);
				if (i == 0) {
					plankID = 14315;
					boatArea = new RSArea(2660, 2638, 3, 5);
					Squire = 3802;
				} else if (i == 1) {
					plankID = 25631;
					boatArea = new RSArea(2638, 2642, 3, 5);
					Squire = 6140;
				} else {
					plankID = 25632;
					boatArea = new RSArea(2632, 2649, 3, 5);
					Squire = 6141;
				}
				dispose();
			}
			if (e.getSource() == btnAddJob) {
				jobmodel.addElement(comboBox.getSelectedItem());
			}
			if (e.getSource() == btnRemove) {
				if (table.getSelectedRow() != -1)
					model.removeRow(table.getSelectedRow());
			}
			if (e.getSource() == btnAddAntiban) {
				m.addElement(comboBox_1.getSelectedItem());
			}
			if (e.getSource() == btnAddBreak) {
				model.addRow(new Object[] { textField.getText(),
						textField_1.getText() });
			}
		}

		double version = 1.0;
		private JTextField specialtxt;
		private JButton btnStartScript;
		private JCheckBox chckbxUseSpecialAttack;
		private JCheckBox chckbxUseQuickPrayer;
		private JCheckBox chckbxAttackPortals;
		private JComboBox comboBox_2;
		private JCheckBox chckbxattackspinners;

	}

	private class Job {

		private final String n;
		private boolean done = false;
		public int points;

		public Job(final String name, int points) {
			n = name;
			this.points = points;
		}

		public final String getName() {
			return n;
		}

		public boolean isFinished() {
			return done;
		}

		public String toString() {
			return "Name " + getName();
		}
	}

	private class Portal {

		private final RSTile Location;
		private final String Name;
		private final RSTile gateLoc;

		public Portal(final RSTile t, final String name, final RSTile g) {
			Location = t;
			Name = name;
			gateLoc = g;
		}

		public boolean equals(final Portal p) {
			return p.getLocation().getX() == getLocation().getX()
					&& p.getLocation().getY() == getLocation().getY();
		}

		public RSArea getArea() {
			RSArea a = null;
			if (getName().equals("E")) {
				a = new RSArea(voidLoc.getX() + 24, voidLoc.getY() - 20, 3, 5);
			} else if (getName().equals("W")) {
				a = new RSArea(voidLoc.getX() - 25, voidLoc.getY() - 15, 4, 3);
			} else if (getName().contains("SE")) {
				a = new RSArea(voidLoc.getX() + 16, voidLoc.getY() - 34, 4, 3);
			} else if (getName().contains("SW")) {
				a = new RSArea(voidLoc.getX() - 8, voidLoc.getY() - 35, 5, 3);
			}
			return a;
		}

		public RSObject getGate() {
			return getObjectAt(getGateLocation());
		}

		public RSTile getGateLocation() {
			return gateLoc;
		}

		public RSTile getLocation() {
			return Location;
		}

		public String getName() {
			return Name;
		}

		public RSNPC getNPC() {
			if (!attackPortals) {
				return null;
			}
			final RSNPC portal = getNearestNPCByName("Portal");
			if (portal != null) {
				if (portal.isInCombat()) {
					return portal;
				} else {
					return null;
				}
			}
			return null;
		}

		public RSTile getRandom() {
			final RSTile[][] tiles = getArea().getTiles();
			final int y = random(0, tiles.length - 1);
			final int x = random(0, tiles[y].length - 1);
			try {
				return tiles[x][y];
			} catch (final Exception e) {
				return getRandom();
			}
		}

		public RSArea getRealArea() {
			RSArea a = null;
			if (getName().equals("E")) {
				a = new RSArea(voidLoc.getX() + 16, voidLoc.getY() - 23, 14, 14);
			} else if (getName().equals("W")) {
				a = new RSArea(voidLoc.getX() - 30, voidLoc.getY() - 24, 16, 19);
			} else if (getName().contains("SE")) {
				a = new RSArea(voidLoc.getX(), voidLoc.getY() - 45, 28, 21);
			} else if (getName().contains("SW")) {
				a = new RSArea(voidLoc.getX() - 17, voidLoc.getY() - 44, 17, 20);
			}
			return a;
		}

		public boolean isGateOpen() {
			return getGate() == null || getGate().getID() > 14240;
		}

		public boolean isOpen() {
			try {
				if (getName().equals("W")) {
					if (Integer.parseInt(getInterface(408, 13).getText()) < 10) {
						return false;
					}
				}
				if (getName().equals("E")) {
					if (Integer.parseInt(getInterface(408, 14).getText()) < 10) {
						return false;
					}
				}
				if (getName().equals("SE")) {
					if (Integer.parseInt(getInterface(408, 15).getText()) < 10) {
						return false;
					}
				}
				if (getName().equals("SW")) {
					if (Integer.parseInt(getInterface(408, 16).getText()) < 10) {
						return false;
					}
				}
			} catch (final Exception e) {
				return false;
			}
			return true;
		}

		private void openGate() {
			while (inPest() && !isGateOpen()) {
				if (getMyPlayer().isMoving()) {
					try {
						Thread.sleep(random(500, 600));
					} catch (Exception e) {

					}
					continue;
				}
				final Point p = Calculations.tileToScreen(getGateLocation());
				if (!pointOnScreen(p)) {
					walkTileMM(getGateLocation());
					try {
						Thread.sleep(random(500, 700));
					} catch (InterruptedException ignored) {
					}
					continue;
				}
				doAction(p, "Open");
			}
		}

		public void walkTo() {
			final Portal p = getNearestPortal();
			boolean died = false;
			final RSNPC npc = getNearestNPCByID(squire);
			if (npc != null) {
				if (distanceTo(npc) < 10) {
					died = true;
				}
			}
			boolean atDoor = false;
			while (inPest() && distanceTo(getLocation()) > 10 && isOpen()) {
				if (getMyPlayer().isInCombat()
						&& getMyPlayer().getHPPercent() < 2
						&& distanceTo(getNearestNPCByID(squire)) > 15) {
					return;
				}
				if (died) {
					try {
						enablePrayer();
						Thread.sleep(random(300, 800));
						setRun(true);
						Thread.sleep(random(300, 800));
						doDragonBattleAxe();
						Thread.sleep(random(300, 800));
					} catch (Exception e) {
					}
					if (!atDoor) {
						walkPath(genPath(getGateLocation()));
						if (!isGateOpen() && tileOnScreen(getGateLocation())) {
							openGate();
							if (isGateOpen()) {
								atDoor = true;
							}
						}
						if (distanceTo(getGateLocation()) < 10 && isGateOpen()) {
							atDoor = true;
						}
					}
					if (atDoor) {
						walkPath(genPath(getRandom()));
					}
				} else if (p.getName().equals("E") && getName().equals("W")) {
					while (inPest() && getNearestPortal().getName().equals("E")) {
						final Portal se = getPortalByName("SE");
						if (!walkPath(genPath(se.getRandom()))) {
							wait(400, 700);
						}
					}
					while (inPest()
							&& getNearestPortal().getName().equals("SE")) {
						final Portal sw = getPortalByName("SW");
						if (!walkPath(genPath(sw.getRandom()))) {
							wait(400, 700);
						}
					}
				} else if (p.getName().equals("W") && getName().equals("E")) {
					while (inPest() && getNearestPortal().getName().equals("W")) {
						final Portal sw = getPortalByName("SW");
						if (!walkPath(genPath(sw.getRandom()))) {
							wait(400, 700);
						}
					}
					while (inPest()
							&& getNearestPortal().getName().equals("SW")) {
						final Portal se = getPortalByName("SE");
						if (!walkPath(genPath(se.getRandom()))) {
							wait(400, 700);
						}
					}
					while (!walkPath(genPath(getRandom()))) {
						wait(400, 700);
					}
				} else if (p.getName().equals("E") && getName().equals("SE")) {
					if (!walkPath(genPath(getLocation()))) {
						wait(400, 700);
					}
				} else if (p.getName().equals("SE") && getName().equals("E")) {
					while (!walkPath(genPath(getLocation()))) {
						wait(400, 700);
					}
				} else if (p.getName().equals("E") && getName().equals("SW")) {
					while (inPest() && getNearestPortal().getName().equals("E")) {
						final Portal se = getPortalByName("SE");
						if (!walkPath(genPath(se.getRandom()))) {
							wait(400, 700);
						}
					}
					while (!walkPath(genPath(getRandom()))) {
						wait(400, 700);
					}
				} else if (p.getName().equals("SW") && getName().equals("E")) {
					while (inPest()
							&& getNearestPortal().getName().equals("SW")) {
						final Portal se = getPortalByName("SE");
						if (!walkPath(genPath(se.getRandom()))) {
							wait(400, 700);
						}
					}
					while (!walkPath(genPath(getRandom()))) {
						wait(400, 700);
					}
				} else if (p.getName().equals("W") && getName().equals("SW")) {

					if (!walkPath(genPath(getLocation()))) {
						wait(400, 700);
					}
				} else if (p.getName().equals("SW") && getName().equals("W")) {

					if (!walkPath(genPath(getLocation()))) {
						wait(400, 700);
					}
				} else if (p.getName().equals("SE") && getName().equals("W")) {
					while (inPest()
							&& getNearestPortal().getName().equals("SE")) {
						final Portal se = getPortalByName("SW");
						if (!walkPath(genPath(se.getRandom()))) {
							wait(400, 700);
						}
					}
					while (!walkPath(genPath(getRandom()))) {
						wait(400, 700);
					}
				} else if (p.getName().equals("W") && getName().equals("SE")) {

					while (inPest() && getNearestPortal().getName().equals("W")) {
						final Portal se = getPortalByName("SW");
						if (!walkPath(genPath(se.getRandom()))) {
							wait(400, 700);
						}
					}
					while (!walkPath(genPath(getRandom()))) {
						wait(400, 700);
					}
				} else if (p.getName().equals("SW") && getName().equals("SE")) {

					if (!walkPath(genPath(getLocation()))) {
						wait(400, 700);
					}
				} else if (p.getName().equals("SE") && getName().equals("SW")) {
					if (!walkPath(genPath(getLocation()))) {
						wait(400, 700);
					}
				}
			}
		}

		public void wait(int min, int max) {
			try {
				Thread.sleep(random(min, max));
			} catch (Exception ignored) {
			}
		}
	}

	private final LinkedList<Job> jobQ = new LinkedList<Job>();
	private final LinkedList<Portal> portalQ = new LinkedList<Portal>();
	private final LinkedList<Break> breakQ = new LinkedList<Break>();
	private final ArrayList<String> antibanQ = new ArrayList<String>();

	private Portal current;
	private boolean start = false;

	private boolean attackPortals = false;

	private boolean usePray = false;
	private boolean played = false;
	private boolean got250 = false;
	private boolean attackSpinner = false;
	private boolean DBaxe = false;
	private boolean pickupArrows = false;
	private RSTile[] path;
	private final int Interface_250 = 213;
	private int plankID = 14315;
	private final int squire = 3781;
	private int Squire;
	private int failsafe = 0;
	private int points = 0;
	private int specialPercent = 0;
	private int arrowID = 0;
	private int oldPoints = 0;
	private int gainedPoints = 0;

	private int lost = 0;
	private int won = 0;
	private RSArea boatArea = null;
	private RSTile voidLoc = null;
	long lastNPC = 0;

	private long lastAniChange = 0;

	private long startTime = 0;

	private long gc = 0;
	public final int ROTATE_COMPASS = 0;
	public final int MOVE_MOUSE = 1;
	public final int MOVE_MOUSE_AND_ROTATE_COMPASS = 2;
	public final int RIGHT_CLICK_OBJECT = 3;
	public final int RIGHT_CLICK_NPC = 4;
	public final int RIGHT_CLICK_PLAYER = 5;
	private final String[] jobsStrings = new String[] { "Attack", "Defence",
			"Magic", "Prayer", "Strength", "Ranged", "Hitpoints", "Top",
			"Robe", "Range Helm", "Melee Helm", "Mage Helm", "Glove" };
	private final int[] jobsPoints = new int[] { 100, 100, 100, 100, 100, 100,
			100, 250, 250, 200, 200, 200, 150 };
	private int width = 0;

	private int height = 0;

	private Break curBreak;

	Pathfinder pf;

	boolean allJobsFinished() {
		Job j = null;
		for (Job job : jobQ) {
			if (!(j = job).isFinished()) {
				break;
			}
		}

		return j == null;
	}

	public void performAntiban(String antiban) throws InterruptedException {
		log("Doing : " + antiban.toLowerCase().replace('_', ' '));
		int ab = AntibanStringToInteger(antiban);
		switch (ab) {
		case ROTATE_COMPASS:
			char dir = 37;
			if (random(0, 3) == 2)
				dir = 39;
			Bot.getInputManager().pressKey(dir);
			Thread.sleep(random(500, 2000));
			Bot.getInputManager().releaseKey(dir);
			break;
		case MOVE_MOUSE:
			moveMouse(random(0, CanvasWrapper.getGameWidth()), random(0,
					CanvasWrapper.getGameHeight()));
			break;
		case MOVE_MOUSE_AND_ROTATE_COMPASS:
			Thread camera = new Thread() {
				@Override
				public void run() {
					char dir = 37;
					if (random(0, 3) == 2)
						dir = 39;
					Bot.getInputManager().pressKey(dir);
					try {
						Thread.sleep(random(500, 2000));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					Bot.getInputManager().releaseKey(dir);
				}
			};

			Thread mouse = new Thread() {
				@Override
				public void run() {
					moveMouse(random(0, CanvasWrapper.getGameWidth()), random(
							0, CanvasWrapper.getGameHeight()));
				}
			};
			camera.start();
			mouse.start();
			while (camera.isAlive() || mouse.isAlive())
				wait(random(100, 300));
			break;
		case RIGHT_CLICK_OBJECT:
			RSObject obj = getObjOnScreen();
			if (obj != null) {
				clickMouse(Calculations.tileToScreen(obj.getLocation()), false);
				Thread.sleep(random(500, 2000));
				while (isMenuOpen()) {
					moveMouseRandomly(20);
				}
			}
			break;
		case RIGHT_CLICK_NPC:
			RSNPC npc = getNPCOnScreen();
			if (npc != null) {
				clickMouse(npc.getScreenLocation(), false);
				Thread.sleep(random(500, 2000));
				while (isMenuOpen()) {
					moveMouseRandomly(20);
				}
			}
			break;
		case RIGHT_CLICK_PLAYER:
			RSPlayer player = getPlayerOnScreen();
			if (player != null) {
				clickMouse(player.getScreenLocation(), false);
				Thread.sleep(random(500, 2000));
				while (isMenuOpen()) {
					moveMouseRandomly(20);
				}
			}
			break;
		default:
			break;
		}
	}

	public RSObject getObjOnScreen() {
		ArrayList<RSObject> result = new ArrayList<RSObject>();
		for (int x = 0; x < 104; x++) {
			for (int y = 0; y < 104; y++) {
				RSObject obj = getObjectAt(x + Bot.getClient().getBaseX(), y
						+ Bot.getClient().getBaseY());
				if (obj != null) {
					Point p = Calculations.tileToScreen(obj.getLocation());
					if (p.x != -1)
						result.add(obj);
				}
			}
		}
		if (result.size() == 0)
			return null;
		return result.get(random(0, result.size()));
	}

	public RSNPC getNPCOnScreen() {
		final int[] validNpcs = Bot.getClient().getRSNPCIndexArray();
		final ArrayList<RSNPC> p = new ArrayList<RSNPC>();
		for (final int element : validNpcs) {
			Node node = Calculations.findNodeByID(Bot.getClient().getRSNPCNC(), element);
			if (node == null || !(node instanceof RSNPCNode)) {
				continue;
			}
			final RSNPC player = new RSNPC(((RSNPCNode) node).getRSNPC());
			try {
				if (!tileOnScreen(player.getLocation())) {
					continue;
				}
				p.add(player);
			} catch (final Exception ignored) {
			}
		}
		if (p.size() == 0) {
			return null;
		} else if (p.size() == 1) {
			return p.get(0);
		}
		return p.get(random(0, p.size() - 1));
	}

	public int AntibanStringToInteger(String a) {
		if (a.contains("rotate compass"))
			return ROTATE_COMPASS;
		else if (a.contains("move mouse"))
			return MOVE_MOUSE;
		else if (a.contains("MOVE_MOUSE_AND_ROTATE_COMPASS"
				.replaceAll("_", " ").toLowerCase()))
			return MOVE_MOUSE_AND_ROTATE_COMPASS;
		else if (a.contains("RIGHT_CLICK_OBJECT".replaceAll("_", " ")
				.toLowerCase()))
			return RIGHT_CLICK_OBJECT;
		else if (a.contains("RIGHT_CLICK_NPC".replaceAll("_", " ")
				.toLowerCase()))
			return RIGHT_CLICK_NPC;
		else if (a.contains("RIGHT_CLICK_PLAYER".replaceAll("_", " ")
				.toLowerCase()))
			return RIGHT_CLICK_PLAYER;
		return -1;
	}

	public boolean arrayContains(final String[] array, final String option) {
		if (array == null) {
			return false;
		}
		for (final String s : array) {
			if (s.contains(option)) {
				return true;
			}
		}
		return false;
	}

	boolean atPortal() {
		for (final Portal p : portalQ) {
			if (p.getRealArea().contains(getMyPlayer().getLocation())) {
				return true;
			}
		}
		return false;
	}

	public Break getCurrentBreak() {
		if ((curBreak == null || curBreak.isDone) && breakQ.size() == 0)
			return null;
		if (curBreak == null || curBreak.isDone)
			curBreak = breakQ.remove();
		if (curBreak.Time == null)
			curBreak.init();
		return curBreak;
	}

	boolean clickNPC(final RSNPC n, final String action) {
		if (!tileOnScreen(n.getLocation())) {
			while (inPest() && atPortal()
					&& !walkPath(genPath(n.getLocation()))) {
				wait(random(200, 500));
			}
		}
		try {
			Point p = n.getScreenLocation();
			while ((p = n.getScreenLocation()) != null && p.x != -1
					&& getMouseLocation().distance(n.getScreenLocation()) > 8) {
				moveMouse(p, 5, 5);
			}
			if (!pointOnScreen(getMouseLocation())) {
				return false;
			}

			if (getTopText().contains(action)) {
				clickMouse(true);
			} else if (menuContains(action)) {
				clickMouse(false);
				return atMenu(action);
			} else {
				return false;
			}
		} catch (final Exception ignored) {
		}
		return true;
	}

	@Override
	public int getMouseSpeed() {
		return random(5, 7);
	}

	void clickRandomPlayer() {
		RSPlayer p;
		if ((p = getPlayerOnScreen()) != null) {
			if (p.getScreenLocation().x != -1 && p.getScreenLocation().y != -1) {
				moveMouse(p.getScreenLocation());
				clickMouse(false);
				wait(random(800, 1000));
				moveMouse(random(0, 763), random(0, 503));
			}
		}
	}

	boolean doAction(final Point p, final String action) {
		if (p.x == -1 || p.y == -1) {
			return false;
		}
		while (p.x != -1 && getMouseLocation().distance(p) > 8) {
			moveMouse(p);
		}
		if (!pointOnScreen(getMouseLocation())) {
			return false;
		}
		if (getTopText().contains(action)) {
			clickMouse(true);
		} else if (menuContains(action)) {
			clickMouse(false);
			return atMenu(action);
		} else {
			return false;
		}
		return true;
	}

	void doJob() {
		final Job j = getJob();
		if (j == null) {
			return;
		}
		if (j.points == 250 && !got250)
			return;
		if (j.points >= points)
			return;
		while (!getInterface(267).isValid() || getInterface(267).isValid()
				&& getInterface(267, 34).getAbsoluteX() < 10) {
			if (getMyPlayer().getInteracting() != null)
				continue;
			if (getNearestNPCByID(3788) == null) {
				walkPath(genPath(new RSTile(2661, 2650)), 2);
			} else if (distanceTo(getNearestNPCByID(3788)) > 3) {
				walkPath(genPath(getNearestNPCByID(3788).getLocation()), 2);
			}
			if (clickNPC(getNearestNPCByID(3788), "Exchange")) {
				wait(random(800, 1200));
			} else {
				wait(random(300, 700));
			}
		}
		wait(random(800, 1000));
		int i = 0;
		boolean skip = false;
		;
		if (j.getName().equals(jobsStrings[i])) {
			clickMouse(random(188, 223), random(67, 72), true);
			skip = true;
		}
		i++;
		if (j.getName().equals(jobsStrings[i])) {
			clickMouse(random(188, 223), random(104, 112), true);
			skip = true;
		}
		i++;
		if (j.getName().equals(jobsStrings[i])) {
			clickMouse(random(188, 223), random(144, 151), true);
			skip = true;
		}
		i++;
		if (j.getName().equals(jobsStrings[i])) {
			clickMouse(random(188, 223), random(180, 188), true);
			skip = true;
		}
		i++;
		if (j.getName().equals(jobsStrings[i])) {
			clickMouse(random(406, 444), random(67, 72), true);
			skip = true;
		}
		i++;
		if (j.getName().equals(jobsStrings[i])) {
			clickMouse(random(406, 444), random(104, 112), true);
			skip = true;
		}
		i++;
		if (j.getName().equals(jobsStrings[i])) {
			clickMouse(random(406, 444), random(144, 151), true);
			skip = true;
		}
		if (!skip) {
			clickMouse(481, 179, true);
			wait(random(800, 900));
		}
		i++;
		if (j.getName().equals(jobsStrings[i])) {
			clickMouse(random(300, 340), random(111, 119), true);
		}
		i++;
		if (j.getName().equals(jobsStrings[i])) {
			clickMouse(random(80, 120), random(150, 155), true);
		}
		i++;
		if (j.getName().equals(jobsStrings[i])) {
			clickMouse(random(300, 340), random(190, 197), true);
		}
		i++;

		if (j.getName().equals(jobsStrings[i])) {
			clickMouse(random(80, 120), random(188, 193), true);
		}
		i++;
		if (j.getName().equals(jobsStrings[i])) {
			clickMouse(random(80, 120), random(225, 234), true);
		}
		i++;
		if (j.getName().equals(jobsStrings[i])) {
			clickMouse(random(300, 340), random(151, 156), true);
		}
		jobQ.remove(j);
		wait(random(800, 1000));
		clickMouse(random(190, 320), random(285, 320), true);
		oldPoints = 0;
		played = false;
		points = 0;
		got250 = false;
	}

	@Override
	public boolean tileOnMap(RSTile tile) {
		Point center = tileToMinimap(getLocation());
		return tileToMinimap(tile).distance(center) <= 70;
	}

	void enablePrayer() {
		if (!PrayerEnabled() && usePray) {
			clickMouse(random(715, 752), random(60, 80), true);
		}
	}

	int enterBoat() {
		portalQ.clear();
		final RSObject o = getPlank();
		if (o != null && !onBoat()) {
			final RSTile t = o.getLocation();
			final Point p = Calculations.tileToScreen(t);
			if (p.x == -1 || p.y == -1) {
				walkTileMM(t);
			}
			if (doAction(p, "Cross")) {
				return random(1000, 1300);
			}
		}
		return random(600, 800);
	}

	RSObject getPlank() {
		for (int x = 0; x < 104; x++) {
			for (int y = 0; y < 104; y++) {
				RSObject obj = getObjectAt(x + Bot.getClient().getBaseX(), y
						+ Bot.getClient().getBaseY());
				if (obj != null && obj.getID() == plankID)
					return obj;
			}
		}
		return null;
	}

	void fillPortalList() {
		if (portalQ.size() < 1) {
			portalQ.add(new Portal(new RSTile(voidLoc.getX() - 26, voidLoc
					.getY() - 15), "W", new RSTile(voidLoc.getX() - 12, voidLoc
					.getY() - 15)));
			portalQ.add(new Portal(new RSTile(voidLoc.getX() + 26, voidLoc
					.getY() - 18), "E", new RSTile(voidLoc.getX() + 15, voidLoc
					.getY() - 15)));
			portalQ.add(new Portal(new RSTile(voidLoc.getX() + 15, voidLoc
					.getY() - 36), "SE", new RSTile(voidLoc.getX() + 1, voidLoc
					.getY() - 22)));
			portalQ.add(new Portal(new RSTile(voidLoc.getX() - 9, voidLoc
					.getY() - 37), "SW", new RSTile(voidLoc.getX() + 1, voidLoc
					.getY() - 22)));
		} else {
			portalQ.clear();
			fillPortalList();
		}
	}

	RSTile[] genPath(final RSTile t) {
		RSTile[] temp = pf.findPath(t);
		if (temp != null)
			return temp;
		RSTile current = getMyPlayer().getLocation();
		final ArrayList<RSTile> tiles = new ArrayList<RSTile>();
		final ArrayList<RSTile> path = new ArrayList<RSTile>();

		while (Methods.distanceBetween(t, current) > 2) {
			final int x = current.getX();
			final int y = current.getY();
			tiles.add(new RSTile(x, y - 1));
			tiles.add(new RSTile(x - 1, y));
			tiles.add(new RSTile(x + 1, y));
			tiles.add(new RSTile(x, y + 1));
			tiles.add(new RSTile(x - 1, y - 1));
			tiles.add(new RSTile(x + 1, y - 1));
			tiles.add(new RSTile(x + 1, y + 1));
			tiles.add(new RSTile(x - 1, y + 1));
			final RSTile tile = getNearest(tiles, t);
			path.add(tile);
			this.path = path.toArray(new RSTile[path.size()]);
			current = tile;
			tiles.clear();
		}
		this.path = path.toArray(new RSTile[path.size()]);
		return this.path;
	}

	Portal getCurrentPortal() {
		return current;
	}

	Job getJob() {
		if (jobQ.size() == 0)
			return null;
		return jobQ.getFirst();
	}

	RSTile getNearest(final ArrayList<RSTile> tiles, final RSTile t) {
		RSTile nearest = tiles.get(0);
		for (RSTile tile : tiles) {
			if (Methods.distanceBetween(tile, t) < Methods.distanceBetween(
					nearest, t)) {
				nearest = tile;
			}
		}
		return nearest;
	}

	Portal getNearestOpenPortal() {
		if (portalQ.size() < 1) {
			return null;
		}
		Portal winner = null;
		for (final Portal p : portalQ) {
			if (p.isOpen()) {
				winner = p;
			}
		}
		if (winner == null) {
			return null;
		}

		for (final Portal p : portalQ) {
			if (!p.isOpen()) {
				continue;
			}
			if (distanceTo(p.getLocation()) < distanceTo(winner.getLocation())) {
				winner = p;
			}
		}
		current = winner;
		return winner;
	}

	Portal getNearestPortal() {
		if (portalQ.size() < 1) {
			return null;
		}
		Portal winner = portalQ.get(0);
		for (final Portal p : portalQ) {
			if (distanceTo(p.getLocation()) < distanceTo(winner.getLocation())) {
				winner = p;
			}
		}
		return winner;
	}

	RSTile getNext(final RSTile[] path) {
		RSTile nearest = path[0];
		for (final RSTile element : path) {
			if (Methods.distanceBetween(element, path[path.length - 1]) < Methods
					.distanceBetween(nearest, path[path.length - 1])) {
				if (tileOnMap(element)) {
					nearest = element;
				}
			}
		}
		return nearest;
	}

	RSPlayer getPlayerOnScreen() {
		final int[] validPlayers = Bot.getClient().getRSPlayerIndexArray();
		final org.rsbot.accessors.RSPlayer[] players = Bot.getClient()
				.getRSPlayerArray();
		final ArrayList<RSPlayer> p = new ArrayList<RSPlayer>();
		for (final int element : validPlayers) {
			if (players[element] == null) {
				continue;
			}
			final RSPlayer player = new RSPlayer(players[element]);
			try {
				if (!tileOnScreen(player.getLocation())) {
					continue;
				}
				p.add(player);
			} catch (final Exception ignored) {
			}
		}
		if (p.size() == 0) {
			return null;
		} else if (p.size() == 1) {
			return p.get(0);
		}
		return p.get(random(0, p.size() - 1));
	}

	Portal getPortalByName(final String name) {
		if (portalQ.size() < 1) {
			return null;
		}
		for (final Portal p : portalQ) {
			if (p.getName().equalsIgnoreCase(name)) {
				return p;
			}
		}
		return null;
	}

	public RSTile getRandom(final RSArea a) {
		try {
			final RSTile[][] tiles = a.getTiles();
			final int y = random(0, tiles.length - 1);
			final int x = random(0, tiles[y].length - 1);
			return tiles[x][y];
		} catch (final Exception ignored) {
		}
		return new RSTile(-1, -1);
	}

	Portal getRandomOpenPortal() {
		if (portalQ.size() < 1) {
			return null;
		}
		final ArrayList<Portal> po = new ArrayList<Portal>();
		for (final Portal p : portalQ) {
			if (p.isOpen()) {
				po.add(p);
			}
		}
		current = po.get(random(0, po.size()));
		return current;
	}

	String getTopText() {
		try {
			final long start = System.currentTimeMillis();
			String[] menuItems = getMenuItems().toArray(
					new String[getMenuItems().size()]);
			if (menuItems.length == 0) {
				return "Cancel";
			}
			while (menuItems[0].contains("Cancel")
					&& pointOnScreen(getMouseLocation())
					&& System.currentTimeMillis() - start < 700) {
				menuItems = getMenuItems().toArray(
						new String[getMenuItems().size()]);
			}
			return menuItems[0];
		} catch (Exception e) {
			return getTopText();
		}
	}

	boolean inPest() {
		return getNearestNPCByID(Squire) == null;
	}

	@Override
	public int loop() {
		try {
			if (gc == 0)
				gc = System.currentTimeMillis();

			if (!onBoat() && !inPest()) {
				Break b = getCurrentBreak();
				if (b != null && b.needsBreak())
					b.takeBreak();
			}

			if (this.getInterface(Interface_250).isValid()) {
				got250 = true;
			}

			if (getMyPlayer().isInCombat() && getMyPlayer().getHPPercent() < 2
					&& distanceTo(getNearestNPCByID(squire)) > 15) {
				return random(600, 800);
			}

			if (!onBoat() && !inPest() && getJob() != null) {
				doJob();
			}

			if (getJob() == null && got250) {
				logout();
				log("Thanks for using BPestControll");
				log("Reached 250 points and out of jobs");
			}
			if (isMenuOpen()) {
				moveMouse(random(5, 760), random(5, 500));
			}
			if (!inPest() && !onBoat()) {
				return enterBoat();
			}
			if (onBoat()) {
				String s = "";
				if (getInterface(407, 16) != null) {
					s = getInterface(407, 16).getText();
				}
				points = Integer.parseInt(s.replace("Pest Points: ", ""));
				if (played) {
					if (points > oldPoints) {
						gainedPoints += points - oldPoints;
					}
					if (points - oldPoints == 0) {
						lost++;
					} else {
						won++;
					}
				}
				oldPoints = points;
				played = false;
				return random(400, 700);
			}
			if (getNearestNPCByID(squire) != null) {
				voidLoc = getNearestNPCByID(squire).getLocation();
				fillPortalList();
			}
			if (inPest()) {
				return pestLoop();
			}
		} catch (Exception ignored) {
		}
		return random(100, 800);
	}

	boolean menuContains(final String a) {
		try {
			final long start = System.currentTimeMillis();
			String[] menuItems = getMenuItems().toArray(
					new String[getMenuItems().size()]);
			while (menuItems[0].contains("Cancel")
					&& pointOnScreen(getMouseLocation())
					&& System.currentTimeMillis() - start < 700) {
				menuItems = getMenuItems().toArray(
						new String[getMenuItems().size()]);
			}
			for (final String element : menuItems) {
				if (element.contains(a)) {
					return true;
				}
			}
		} catch (final Exception ignored) {

		}
		return false;
	}

	@Override
	public void moveMouse(final int x, final int y) {
		if (x != -1 && y != -1) {
			moveMouse(x, y, 0, 0);
		}
	}

	@Override
	public void moveMouse(final Point p) {
		if (p.x != -1 && p.y != -1) {
			moveMouse(p.x, p.y, 0, 0);
		}
	}

	@Override
	public void moveMouse(final Point p, final int rX, final int rY) {
		final int X = p.x + random(-(rX / 2), (rX / 2));
		final int Y = p.y + random(-(rY / 2), (rY / 2));
		moveMouse(X, Y);
	}

	boolean onBoat() {
		return boatArea != null
				&& boatArea.contains(getMyPlayer().getLocation());
	}

	@Override
	public void onFinish() {
		log("Gained " + gainedPoints + " points.");
	}

	public void onRepaint(final Graphics g) {
		if (getCurrentBreak() != null) {
			g.drawString(getCurrentBreak().getTimeTillBreak(), 10, 400);
			g.drawString("" + getCurrentBreak().needsBreak(), 10, 415);
		}
		int y = 120;
		final int x = 20;
		g.setColor(new Color(51, 153, 255, 170));
		g.fillRoundRect(15, 120, width, height, 5, 5);
		g.setColor(Color.WHITE);
		g.drawRoundRect(15, 120, width, height, 5, 5);
		width = 0;
		height = 0;
		if (startTime == 0) {
			startTime = System.currentTimeMillis();
		}
		long millis = System.currentTimeMillis() - startTime;
		try {
			g.setColor(Color.WHITE);
			g.setFont(new Font("Arial", Font.BOLD, 14));
			g.drawString("BPestControlV2 made by Bool", x, y += g
					.getFontMetrics().getHeight());
			height += g.getFontMetrics().getHeight();
			width = (int) g.getFontMetrics().getStringBounds(
					"BPestControlV2 made by Bool", g).getWidth();
			g.setFont(new Font("Arial", Font.PLAIN, 12));
			g.drawString("Lost " + lost + " Games", x, y += g.getFontMetrics()
					.getHeight());
			height += g.getFontMetrics().getHeight();
			g.drawString("won " + won + " Games", x, y += g.getFontMetrics()
					.getHeight());
			height += g.getFontMetrics().getHeight();
			g.drawString("Gained " + gainedPoints + " points", x, y += g
					.getFontMetrics().getHeight());
			height += g.getFontMetrics().getHeight();
			g.drawString("Time running : " + timeToString(millis), x, y += g
					.getFontMetrics().getHeight());
			height += g.getFontMetrics().getHeight();
			if (getJob() != null) {
				if (g.getFontMetrics().getStringBounds(
						"Spending points on " + getJob().getName(), g)
						.getWidth() > width) {
					width = (int) g.getFontMetrics().getStringBounds(
							"Spending points on " + getJob().getName(), g)
							.getWidth();
				}
				g.drawString("Spending points on " + getJob().getName() + " "
						+ points, x, y += g.getFontMetrics().getHeight());
				height += g.getFontMetrics().getHeight();
			}
			height += 5;
			width += 10;
		} catch (final Exception ignored) {

		}
	}

	@Override
	public boolean onStart(final Map<String, String> l) {
		start = false;
		final BGUI gui = new BGUI();
		gui.setVisible(true);
		while (gui.isVisible()) {
			wait(random(500, 600));
		}
		if (!start) {
			return false;
		}
		if (start) {
			new Thread(new animationChecker()).start();
			antiban.start();
		}
		log("starting script");
		pf = new Pathfinder();
		return start;
		// return true;
	}

	int pestLoop() {
		if (!played) {
			played = true;
		}
		try {
			if (!atPortal()) {
				final Portal p = getRandomOpenPortal();
				p.walkTo();
				return random(100, 600);
			}
			if (!getCurrentPortal().equals(getNearestPortal())) {
				if (getNearestOpenPortal() != null) {
					getNearestOpenPortal().walkTo();
				}
				return random(100, 600);
			}
			if (!getCurrentPortal().isOpen()) {
				if (getNearestOpenPortal() != null) {
					getNearestOpenPortal().walkTo();
				}
				return random(100, 600);
			}
			checkSpecial();
			if (getMyPlayer().getInteracting() == null) {
				RSItemTile t = getGroundItem(arrowID);
				if (t != null && t.isValid()) {
					atTile(t, "Take");
					return random(600, 800);
				}
				path = null;
				if (!inPest()) {
					return random(100, 500);
				}
				failsafe = 0;
				RSNPC c;
				if (getCurrentPortal().getNPC() != null) {
					c = getCurrentPortal().getNPC();
				} else {
					c = getNPC(attackSpinner);
				}
				if (c != null) {
					if (clickNPC(c, "Attack")) {
						return random(600, 1200);
					}
				}
				return random(400, 700);
			} else {
				RSNPC n = getNPC(true);
				if (n != null && n.getName().contains("Spinner")) {
					RSNPC inter = (RSNPC) getMyPlayer().getInteracting();
					if ((inter != null && !inter.getName().equals(n.getName()))
							|| inter == null)
						if (clickNPC(n, "Attack")) {
							return random(600, 1200);
						}
				}
				path = null;
				if (failsafe > 4) {
					walkTileMM(getNearestOpenPortal().getRandom());
				}
				final RSNPC npc = (RSNPC) getMyPlayer().getInteracting();
				if (getMyPlayer().isMoving()
						|| getMyPlayer().getInteracting() != null
						|| System.currentTimeMillis() - lastAniChange < 2000
						|| npc != null && npc.getName().contains("ortal")) {
					failsafe = 0;
				} else {
					failsafe++;
					wait(random(600, 800));
				}
				return random(400, 700);
			}
		} catch (final Exception ignored) {
		}
		return random(400, 700);
	}

	boolean PrayerEnabled() {
		return getSetting(1395) > 0;
	}

	private int i = 0;

	boolean walkPath(final RSTile[] path) {
		final char left = 37;
		final char right = 39;
		RSTile tile = getNext(path);
		moveMouse(Calculations.worldToMinimap(tile.getX(), tile.getY()));
		if (!inPest()) {
			return true;
		}
		if (i == 7) {
			char dir = random(0, 3) == 2 ? left : right;
			Bot.getInputManager().pressKey(dir);
			wait(random(800, 1200));
			Bot.getInputManager().releaseKey(dir);
			i = 0;
			return false;
		}
		if (getTopText().contains("ancel")) {
			i++;
			return false;
		}
		try {
			walkTileMM(getNext(path));
			wait(random(200, 500));
			while (getMyPlayer().isMoving() && distanceTo(getDestination()) > 7) {
				wait(random(600, 800));
			}
		} catch (final Exception ignored) {
		}
		return distanceTo(path[path.length - 1]) < 10 || !inPest();
	}

	void checkSpecial() {
		if (specialPercent == 0 || isSpecialEnabled())
			return;
		int percent = (getSetting(300) / 10);
		if (percent > specialPercent) {
			RSInterfaceChild child = RSInterface.getChildInterface(884, 4);
			if (getCurrentTab() != TAB_ATTACK)
				openTab(TAB_ATTACK);
			atInterface(child);
			wait(random(400, 700));
		}
	}

	boolean isSpecialEnabled() {
		return getSetting(301) == 1;
	}

	void walkPath(final RSTile[] path, final int i) {
		try {
			walkTileMM(getNext(path));
			wait(random(200, 500));
			while (getMyPlayer().isMoving() && distanceTo(getDestination()) > 7) {
				wait(random(600, 800));
			}
		} catch (final Exception ignored) {
		}
	}

	int daxeID = 0;
	int oldID = 0;

	public void doDragonBattleAxe() {
		if (!DBaxe)
			return;
		if (getCurrentTab() != TAB_INVENTORY)
			openTab(TAB_INVENTORY);
		atInventoryItem(daxeID, "");
		if (getCurrentTab() != TAB_ATTACK)
			openTab(TAB_ATTACK);
		RSInterfaceChild child = RSInterface.getChildInterface(884, 4);
		atInterface(child);
		wait(random(1200, 1500));
		if (getCurrentTab() != TAB_INVENTORY)
			openTab(TAB_INVENTORY);
		atInventoryItem(oldID, "");

	}

	RSNPC getNPC(boolean spinner) {
		final String[] names = { "Shifter", "Defiler", "Torcher", "Brawler",
				"Ravager", "Spinner" };
		RSNPC closest = null;
		final int[] validNPCs = Bot.getClient().getRSNPCIndexArray();
		ArrayList<RSNPC> result = new ArrayList<RSNPC>();

        for (final int element : validNPCs) {
        	Node node = Calculations.findNodeByID(Bot.getClient().getRSNPCNC(), element);
            if (node == null || !(node instanceof RSNPCNode)) {
                continue;
            }
            final RSNPC Monster = new RSNPC(((RSNPCNode) node).getRSNPC());
			try {
				for (final String name : names) {
					if (!name.equals(Monster.getName())
							|| distanceBetween(
									getCurrentPortal().getLocation(), Monster
											.getLocation()) > 10
							|| Monster.isInCombat()
							&& Monster.getHPPercent() < 10) {
						continue;
					}
					result.add(Monster);
				}
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
		if (result.size() == 0)
			return null;
		RSNPC s = null;
		for (RSNPC npc : result) {
			if (spinner) {
				if (npc.getName().equalsIgnoreCase("Spinner")) {
					if (s == null || distanceTo(s) > distanceTo(npc))
						s = npc;
				}
			}
			if (closest == null || distanceTo(closest) > distanceTo(npc))
				closest = npc;
		}
		if (spinner && s != null) {
			return s;
		}
		return closest;
	}

	class Break {
		public boolean isDone = false;
		int time, after;
		public Timer Time;
		long end;

		public Break(int time, int after) {
			this.time = time;
			this.after = after;
			end = System.currentTimeMillis() + after;
		}

		public void init() {
			Time = new Timer(time);
			end = System.currentTimeMillis() + after;
		}

		public void takeBreak() {
			while (isLoggedIn())
				logout();
			Time.reset();
			log("Taking break for " + Time.toString());
			while (!Time.isDone()) {
				try {
					Thread.sleep(random(500, 800));
				} catch (InterruptedException e) {
				}
			}
			isDone = true;
		}

		public String getTimeTillBreak() {
			return timeToString(end - System.currentTimeMillis());
		}

		public boolean needsBreak() {
			return System.currentTimeMillis() > end;
		}
	}

	class Timer {

		private long start;
		private int time;

		public Timer(int time) {
			start = System.currentTimeMillis();
			this.time = time;
		}

		public Timer() {
			this(0);
		}

		public boolean isDone() {
			return (System.currentTimeMillis() - start) > time;
		}

		public void reset() {
			start = System.currentTimeMillis();
		}

		@Override
		public String toString() {
			return timeToString((System.currentTimeMillis() - start));
		}

		public String timeRemaining() {
			return timeToString(((System.currentTimeMillis() - start) + time));
		}
	}

	public String timeToString(long time) {
		final long hours = time / (1000 * 60 * 60);
		time -= hours * 1000 * 60 * 60;
		final long minutes = time / (1000 * 60);
		time -= minutes * 1000 * 60;
		final long seconds = time / 1000;
		String str = "";
		if (hours < 10)
			str += "0";
		str += hours + ":";
		if (minutes < 10)
			str += "0";
		str += minutes + ":";
		if (seconds < 10)
			str += "0";
		str += seconds;
		return str;
	}

	public boolean needUpdate() {
		try {
			URL url = new URL("http://boolscripts.site11.com/Scripts/version");
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					url.openStream()));
			String line = reader.readLine();
			double i = Double.parseDouble(line);
			if (i > version)
				return true;
		} catch (Exception e) {
		}
		return true;
	}

	Thread antiban = new Thread() {
		@Override
		public void run() {
			setName("Antiban");
			while (isActive) {
				try {
					if (!isLoggedIn()) {
						Thread.sleep(300, 400);
						continue;
					}
					int i = random(0, 150);
					if (i == 4 || i == 9)
						performAntiban(antibanQ.get(random(0, antibanQ.size())));
					sleep(random(100, 2000));
				} catch (InterruptedException ignored) {
				}
			}
		}
	};

	class Pathfinder {

		public int basex, basey;
		public int[][] blocks;

		public Pathfinder() {
			reload();
		}

		public void reload() {
			basex = Bot.getClient().getBaseX();
			basey = Bot.getClient().getBaseY();
			blocks = Bot.getClient().getRSGroundDataArray()[Bot.getClient()
					.getPlane()].getBlocks();
		}

		public RSTile[] findPath(RSTile dest) {
			if (!isValid())
				reload();
			return findPath(getLocation(), dest);
		}

		public RSTile[] findPath(RSTile start, RSTile dest) {
			if (!isValid())
				reload();
			return findPath(
					new Node(start.getX() - basex, start.getY() - basey),
					new Node(dest.getX() - basex, dest.getY() - basey));
		}

		public RSTile[] findPath(Node start, Node dest) {
			if (!isValid())
				reload();
			if (!canReach(new RSTile(dest.x + basex, dest.y + basey), false)) {
				return null;
			}
			ArrayList<Node> closed = new ArrayList<Node>();
			ArrayList<Node> open = new ArrayList<Node>();

			Node current = start;
			open.add(current);
			while (open.size() != 0) {
				current = getBestNode(open);
				closed.add(current);
				open.remove(current);
				for (Node node : getNodesAround(current)) {
					if (!closed.contains(node)) {
						if (!open.contains(node)) {
							node.parent = current;
							node.cost = current.cost
									+ getMovementCost(node, current);
							node.heuristic = node.cost
									+ getHeuristicCost(node, dest);
							open.add(node);
						} else {
							if (current.cost + getMovementCost(node, current) < node.cost) {
								node.parent = current;
								node.cost = current.cost
										+ getMovementCost(node, current);
								node.heuristic = node.cost
										+ getHeuristicCost(node, dest);
							}
						}
					}
				}
				if (closed.contains(dest)) {
					final ArrayList<RSTile> result = new ArrayList<RSTile>();
					Node node = closed.get(closed.size() - 1);
					while (node.parent != null) {
						result.add(new RSTile(node.x + basex, node.y + basey));
						node = node.parent;
					}
					path = reversePath(result
							.toArray(new RSTile[result.size()]));
					return path;
				}
			}
			return null;
		}

		public Node getBestNode(ArrayList<Node> nodes) {
			Node winner = null;
			for (Node node : nodes) {
				if (winner == null || node.cost < winner.cost) {
					winner = node;
				}
			}
			return winner;
		}

		public double getHeuristicCost(Node current, Node dest) {
			float dx = dest.x - current.x;
			float dy = dest.y - current.y;
			return (double) (Math.sqrt((dx * dx) + (dy * dy)));
		}

		public double getMovementCost(Node current, Node dest) {
			return (double) Math.hypot(dest.x - current.x, dest.y - current.y);
		}

		/**
		 * credits to jacmob
		 */
		public ArrayList<Node> getNodesAround(Node node) {
			final ArrayList<Node> tiles = new ArrayList<Node>();
			final int curX = node.x, curY = node.y;
			if (curX > 0 && curY < 103
					&& (blocks[curX - 1][curY + 1] & 0x1280138) == 0
					&& (blocks[curX - 1][curY] & 0x1280108) == 0
					&& (blocks[curX][curY + 1] & 0x1280120) == 0) {
				tiles.add(new Node(curX - 1, curY + 1));
			}
			if (curY < 103 && (blocks[curX][curY + 1] & 0x1280120) == 0) {
				tiles.add(new Node(curX, curY + 1));
			}
			if (curX > 0 && curY < 103
					&& (blocks[curX - 1][curY + 1] & 0x1280138) == 0
					&& (blocks[curX - 1][curY] & 0x1280108) == 0
					&& (blocks[curX][curY + 1] & 0x1280120) == 0) {
				tiles.add(new Node(curX + 1, curY + 1));
			}
			if (curX > 0 && (blocks[curX - 1][curY] & 0x1280108) == 0) {
				tiles.add(new Node(curX - 1, curY));
			}
			if (curX < 103 && (blocks[curX + 1][curY] & 0x1280180) == 0) {
				tiles.add(new Node(curX + 1, curY));
			}
			if (curX > 0 && curY > 0
					&& (blocks[curX - 1][curY - 1] & 0x128010e) == 0
					&& (blocks[curX - 1][curY] & 0x1280108) == 0
					&& (blocks[curX][curY - 1] & 0x1280102) == 0) {
				tiles.add(new Node(curX - 1, curY - 1));
			}
			if (curY > 0 && (blocks[curX][curY - 1] & 0x1280102) == 0) {
				tiles.add(new Node(curX, curY - 1));
			}
			if (curX < 103 && curY > 0
					&& (blocks[curX + 1][curY - 1] & 0x1280183) == 0
					&& (blocks[curX + 1][curY] & 0x1280180) == 0
					&& (blocks[curX][curY - 1] & 0x1280102) == 0) {
				tiles.add(new Node(curX + 1, curY - 1));
			}
			return tiles;

		}

		public boolean validTile(RSTile tile) {
			if (!isValid())
				reload();
			int x = tile.getX() - basex;
			int y = tile.getY() - basey;
			return x > 0 && x < 105 && y > 0 && y < 105;
		}

		public boolean isValid() {
			return basex == Bot.getClient().getBaseX()
					&& basey == Bot.getClient().getBaseY();
		}

		class Node {

			public final int x, y;
			public double cost = 0, heuristic = 0;
			public Node parent;

			public Node(int x, int y) {
				this.x = x;
				this.y = y;
			}

			public boolean equals(Object other) {
				if (other instanceof Node) {
					Node o = (Node) other;
					return x == o.x && y == o.y;
				}
				return false;
			}
		}
	}

}