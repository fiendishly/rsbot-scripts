import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.text.NumberFormat;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.rsbot.bot.Bot;
import org.rsbot.event.events.ServerMessageEvent;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.event.listeners.ServerMessageListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Constants;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.Skills;
import org.rsbot.script.randoms.WelcomeScreen;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSItemTile;
import org.rsbot.script.wrappers.RSNPC;
import org.rsbot.script.wrappers.RSObject;
import org.rsbot.script.wrappers.RSTile;
import org.rsbot.util.ScreenshotUtil;

//		BeanMan's xChickenSlaughter
//		Version 4.44
//		Member of Xscripting team
//		http://www.Xscripting.com

@ScriptManifest(authors = { "BeanMan Xscripting Inc." }, category = "Combat", name = "XChickenSlaughter", version = 4.44, description = "<html><head>"
		+ "</head><body>"
		+ "<center><img src=\"http://binaryx.nl/xscripting/beanman/XChickenSlaughter/scriptdescription.png\" /></center>"
		+ "</body></html>")
public class XChickenSlaughter extends Script implements PaintListener,
		ServerMessageListener, Constants {
	// Credits to Garrett because of the nice paint he made!
	public class GarrettsPaint {

		public class MouseWatcher implements Runnable {

			Rectangle rect = null;

			MouseWatcher(final Rectangle rect) {
				this.rect = rect;
			}

			public void run() {
				Point mouse = new Point(Bot.getClient().getMouse().x, Bot
						.getClient().getMouse().y);
				while (rect.contains(mouse)) {
					try {
						mouse = new Point(Bot.getClient().getMouse().x, Bot
								.getClient().getMouse().y);
						Thread.sleep(50);
					} catch (final Exception e) {
					}
				}
			}

		}

		final Rectangle r = new Rectangle(7, 345, 408, 114);
		final Rectangle r1 = new Rectangle(420, 345, 77, 25);
		final Rectangle r2 = new Rectangle(420, 374, 77, 26);
		final Rectangle r3 = new Rectangle(420, 404, 77, 26);
		final Rectangle r4 = new Rectangle(420, 434, 77, 25);
		final Rectangle r2c = new Rectangle(415, 374, 5, 26);
		final Rectangle r3c = new Rectangle(415, 404, 5, 26);
		final Rectangle r4c = new Rectangle(415, 434, 5, 25);
		final Rectangle sb1 = new Rectangle(12, 350, 398, 12);
		final Rectangle sb2 = new Rectangle(12, 363, 398, 12);
		final Rectangle sb3 = new Rectangle(12, 376, 398, 12);
		final Rectangle sb4 = new Rectangle(12, 389, 398, 12);
		final Rectangle sb5 = new Rectangle(12, 402, 398, 12);
		final Rectangle sb6 = new Rectangle(12, 415, 398, 12);
		final Rectangle sb7 = new Rectangle(12, 428, 398, 12);
		final Rectangle sb8 = new Rectangle(12, 441, 398, 12);
		final Rectangle sb1s = new Rectangle(12, 350, 196, 12);
		final Rectangle sb2s = new Rectangle(12, 363, 196, 12);
		final Rectangle sb3s = new Rectangle(12, 376, 196, 12);
		final Rectangle sb4s = new Rectangle(12, 389, 196, 12);
		final Rectangle sb5s = new Rectangle(12, 402, 196, 12);
		final Rectangle sb6s = new Rectangle(12, 415, 196, 12);
		final Rectangle sb7s = new Rectangle(12, 428, 196, 12);
		final Rectangle sb8s = new Rectangle(12, 441, 196, 12);
		final Rectangle sb9s = new Rectangle(213, 350, 196, 12);
		final Rectangle sb10s = new Rectangle(213, 363, 196, 12);
		final Rectangle sb11s = new Rectangle(213, 376, 196, 12);
		final Rectangle sb12s = new Rectangle(213, 389, 196, 12);
		final Rectangle sb13s = new Rectangle(213, 402, 196, 12);
		final Rectangle sb14s = new Rectangle(213, 415, 196, 12);
		final Rectangle sb15s = new Rectangle(213, 428, 196, 12);
		final Rectangle sb16s = new Rectangle(213, 441, 196, 12);
		Rectangle[] skillBars = new Rectangle[] { sb1, sb2, sb3, sb4, sb5, sb6,
				sb7, sb8 };
		boolean savedStats = false;
		boolean scriptRunning = false;
		boolean checkedCount = false;
		int currentTab = 0;
		int lastTab = 0;
		int[] barIndex = new int[16];
		int[] start_exp = null;
		int[] start_lvl = null;
		int[] gained_exp = null;

		int[] gained_lvl = null;
		Thread mouseWatcher = new Thread();

		final NumberFormat nf = NumberFormat.getInstance();
		final long time_ScriptStart = System.currentTimeMillis();

		long runTime = System.currentTimeMillis() - time_ScriptStart;
		int sine = 0;

		int sineM = 1;

		public void drawMouse(final Graphics g) {
			final Point loc = getMouseLocation();
			g.setColor(Color.BLACK);
			g.drawLine(0, loc.y, 766, loc.y);
			g.drawLine(loc.x, 0, loc.x, 505);

		}

		public void drawPaint(final Graphics g, final Rectangle rect) {
			g.setColor(new Color(0, 0, 0, 230));
			g.fillRect(r1.x, r1.y, r1.width, r1.height);
			g.fillRect(r2.x, r2.y, r2.width, r2.height);
			g.fillRect(r3.x, r3.y, r3.width, r3.height);
			g.fillRect(r4.x, r4.y, r4.width, r4.height);
			g.fillRect(rect.x, rect.y, rect.width, rect.height);
			g.fillRect(r.x, r.y, r.width, r.height);
			g.setColor(Color.WHITE);
			drawString(g, "Hide Paint", r1, 5);
			drawString(g, "MAIN", r2, 5);
			drawString(g, "INFO", r3, 5);
			drawString(g, "STATS", r4, 5);
			g.setColor(new Color(0, 0, 0, 230));
		}

		public void drawPlayer(final Graphics g) {
			final RSTile t = getMyPlayer().getLocation();
			Calculations.tileToScreen(t);
			final Point pn = Calculations.tileToScreen(t.getX(), t.getY(), 0,
					0, 0);
			final Point px = Calculations.tileToScreen(t.getX() + 1, t.getY(),
					0, 0, 0);
			final Point py = Calculations.tileToScreen(t.getX(), t.getY() + 1,
					0, 0, 0);
			final Point pxy = Calculations.tileToScreen(t.getX() + 1,
					t.getY() + 1, 0, 0, 0);
			getMyPlayer().getHeight();
			g.setColor(Color.BLACK);
			g.drawPolygon(new int[] { py.x, pxy.x, px.x, pn.x }, new int[] {
					py.y, pxy.y, px.y, pn.y }, 4);
			g.setColor(new Color(240, 240, 240, 75));
			g.fillPolygon(new int[] { py.x, pxy.x, px.x, pn.x }, new int[] {
					py.y, pxy.y, px.y, pn.y }, 4);
		}

		public void drawStat(final Graphics g, final int index, final int count) {
			if (count >= skillBars.length && !checkedCount) {
				skillBars = new Rectangle[] { sb1s, sb2s, sb3s, sb4s, sb5s,
						sb6s, sb7s, sb8s, sb9s, sb10s, sb11s, sb12s, sb13s,
						sb14s, sb15s, sb16s };
				checkedCount = true;
			}
			if (count >= skillBars.length) {
				return;
			}
			g.setFont(new Font("serif", Font.PLAIN, 11));
			g.setColor(new Color(100, 100, 100, 150));
			g.fillRect(skillBars[count].x, skillBars[count].y,
					skillBars[count].width, skillBars[count].height);
			final int percent = skills.getPercentToNextLevel(index);
			g.setColor(new Color(255 - 2 * percent,
					(int) (1.7 * percent + sine), 0, 150));
			g.fillRect(skillBars[count].x, skillBars[count].y,
					(int) (skillBars[count].width / 100.0 * percent),
					skillBars[count].height);
			g.setColor(Color.WHITE);
			final String name = Skills.statsArray[index];
			final String capitalized = name.substring(0, 1).toUpperCase()
					+ name.substring(1);
			g.drawString(capitalized, skillBars[count].x + 2,
					skillBars[count].y + 10);
			drawStringEnd(g, percent + "%", skillBars[count], -2, 4);
			barIndex[count] = index;
		}

		public void drawStats(final Graphics g) {
			final String[] stats = Skills.statsArray;
			int count = 0;
			gained_exp = new int[stats.length];
			gained_lvl = new int[stats.length];
			for (int i = 0; i < stats.length; i++) {
				gained_exp[i] = skills.getCurrentSkillExp(i) - start_exp[i];
				gained_lvl[i] = skills.getCurrentSkillLevel(i) - start_lvl[i];
				if (gained_exp[i] > 0) {
					drawStat(g, i, count);
					count++;
				}
			}
		}

		public void drawString(final Graphics g, final String str,
				final Rectangle rect, final int offset) {
			final FontMetrics font = g.getFontMetrics();
			final Rectangle2D bounds = font.getStringBounds(str, g);
			final int width = (int) bounds.getWidth();
			g.drawString(str, rect.x + (rect.width - width) / 2, rect.y
					+ rect.height / 2 + offset);
		}

		public void drawStringEnd(final Graphics g, final String str,
				final Rectangle rect, final int xOffset, final int yOffset) {
			final FontMetrics font = g.getFontMetrics();
			final Rectangle2D bounds = font.getStringBounds(str, g);
			final int width = (int) bounds.getWidth();
			g.drawString(str, rect.x + rect.width - width + xOffset, rect.y
					+ rect.height / 2 + yOffset);
		}

		public void drawStringMain(final Graphics g, final String str,
				final String val, final Rectangle rect, final int xOffset,
				final int yOffset, final int index, final boolean leftSide) {
			final FontMetrics font = g.getFontMetrics();
			final Rectangle2D bounds = font.getStringBounds(val, g);
			final int indexMult = 17;
			final int width = (int) bounds.getWidth();
			if (leftSide) {
				g.drawString(str, rect.x + xOffset, rect.y + yOffset + index
						* indexMult);
				g.drawString(val, rect.x + rect.width / 2 - width - xOffset,
						rect.y + yOffset + index * indexMult);
			} else {
				g.drawString(str, rect.x + rect.width / 2 + xOffset, rect.y
						+ yOffset + index * indexMult);
				g.drawString(val, rect.x + rect.width - width - xOffset, rect.y
						+ yOffset + index * indexMult);
			}
		}

		public String formatTime(final int milliseconds) {
			final long t_seconds = milliseconds / 1000;
			final long t_minutes = t_seconds / 60;
			final long t_hours = t_minutes / 60;
			final int seconds = (int) (t_seconds % 60);
			final int minutes = (int) (t_minutes % 60);
			final int hours = (int) (t_hours % 60);
			return nf.format(hours) + ":" + nf.format(minutes) + ":"
					+ nf.format(seconds);
		}

		public void hoverDrawString(final Graphics g, final String str,
				final String val, final Rectangle rect, final int offset,
				final int index) {
			g.setColor(Color.WHITE);
			final FontMetrics font = g.getFontMetrics();
			final Rectangle2D bounds = font.getStringBounds(val, g);
			final int width = (int) bounds.getWidth();
			final int y = rect.y + offset + 20 * index;
			g.drawString(str, rect.x + 5, y);
			g.drawString(val, rect.x + rect.width - width - 5, y);
			if (index < 5) {
				g.setColor(new Color(100, 100, 100, 200));
				g.drawLine(rect.x + 5, y + 5, rect.x + rect.width - 5, y + 5);
			}
		}

		public void hoverMenu(final Graphics g) {
			final Point mouse = new Point(Bot.getClient().getMouse().x, Bot
					.getClient().getMouse().y);
			final Rectangle r_main = new Rectangle(mouse.x, mouse.y - 150, 300,
					150);
			for (int i = 0; i < barIndex.length; i++) {
				if (barIndex[i] > -1) {
					if (skillBars[i].contains(mouse)) {
						final int xpTL = skills.getXPToNextLevel(barIndex[i]);
						final int xpHour = (int) (3600000.0 / runTime * gained_exp[barIndex[i]]);
						final int TTL = (int) ((double) xpTL / (double) xpHour * 3600000);
						g.setColor(new Color(50, 50, 50, 240));
						g.fillRect(r_main.x, r_main.y, r_main.width,
								r_main.height);
						g.setColor(Color.WHITE);
						g.setFont(new Font("sansserif", Font.BOLD, 15));
						drawString(g, Skills.statsArray[barIndex[i]]
								.toUpperCase(), r_main, -58);
						g.setFont(new Font("sansserif", Font.PLAIN, 12));
						hoverDrawString(g, "Current Level: ", skills
								.getCurrentSkillLevel(barIndex[i])
								+ "", r_main, 40, 0);
						hoverDrawString(g, "XP Gained: ",
								gained_exp[barIndex[i]] + "xp", r_main, 40, 1);
						hoverDrawString(g, "XP / Hour: ", xpHour + "xp",
								r_main, 40, 2);
						hoverDrawString(g, "LVL Gained: ",
								gained_lvl[barIndex[i]] + " lvl(s)", r_main,
								40, 3);
						hoverDrawString(g, "XPTL: ", xpTL + "xp", r_main, 40, 4);
						hoverDrawString(g, "TTL: ", formatTime(TTL), r_main,
								40, 5);
					}
				}
			}
		}

		public void overlayTile(final Graphics g, final RSTile t, final Color c) {
			final Point p = Calculations.tileToScreen(t);
			final Point pn = Calculations.tileToScreen(t.getX(), t.getY(), 0,
					0, 0);
			final Point px = Calculations.tileToScreen(t.getX() + 1, t.getY(),
					0, 0, 0);
			final Point py = Calculations.tileToScreen(t.getX(), t.getY() + 1,
					0, 0, 0);
			final Point pxy = Calculations.tileToScreen(t.getX() + 1,
					t.getY() + 1, 0, 0, 0);
			final Point[] points = { p, pn, px, py, pxy };
			for (final Point point : points) {
				if (!pointOnScreen(point)) {
					return;
				}
			}
			g.setColor(c);
			g.fillPolygon(new int[] { py.x, pxy.x, px.x, pn.x }, new int[] {
					py.y, pxy.y, px.y, pn.y }, 4);
			g.drawPolygon(new int[] { py.x, pxy.x, px.x, pn.x }, new int[] {
					py.y, pxy.y, px.y, pn.y }, 4);
		}

		public void paint(final Graphics g) {
			if (!isLoggedIn() || !scriptRunning) {
				return;
			}

			// credits to Jacmob for the pulsing
			if (sine >= 84) {
				sine = 84;
				sineM *= -1;
			} else if (sine <= 1) {
				sine = 1;
				sineM *= -1;
			}
			sine += sineM;

			runTime = System.currentTimeMillis() - time_ScriptStart;
			final String formattedTime = formatTime((int) runTime);

			if (advancedPaint) {
				drawPlayer(g);
				drawMouse(g);
				if (chickenPresent()) {
					if (tileOnScreen(getNearestFreeNPCToAttackByName("Chicken")
							.getLocation())) {
						overlayTile(g, getNearestFreeNPCToAttackByName(
								"Chicken").getLocation(), new Color(255, 0,
								255, 75));

					}
					if (tileOnMap(getNearestFreeNPCToAttackByName("Chicken")
							.getLocation())) {
						g.setColor(new Color(255, 0, 255, 75));
						g.fillOval(
								tileToMinimap(getNearestFreeNPCToAttackByName(
										"Chicken").getLocation()).x - 3,
								tileToMinimap(getNearestFreeNPCToAttackByName(
										"Chicken").getLocation()).y - 1, 2, 2);
					}

				}
				if (itemPresent(bonesID)
						&& ((takeBones2 && getNearestFreeNPCToAttackByName("Chicken") == null) || takeBones1)) {
					RSItemTile bonestile = getNearestGroundItemByID(bonesID);
					int bonestilex = bonestile.getX();
					int bonestiley = bonestile.getY();
					RSTile bonestilexy = new RSTile(bonestilex, bonestiley);
					if (tileOnScreen(bonestilexy)) {
						overlayTile(g, bonestilexy, new Color(255, 220, 0, 75));

					}
					if (tileOnMap(bonestilexy)) {
						g.setColor(new Color(255, 220, 0, 75));
						g.fillOval(tileToMinimap(bonestilexy).x - 3,
								tileToMinimap(bonestilexy).y - 1, 2, 2);
					}

				}
				if (itemPresent(feathersID) && takeFeathers) {
					RSItemTile feathertile = getNearestGroundItemByID(feathersID);
					int feathertilex = feathertile.getX();
					int feathertiley = feathertile.getY();
					RSTile feathertilexy = new RSTile(feathertilex,
							feathertiley);
					if (tileOnScreen(feathertilexy)) {
						overlayTile(g, feathertilexy, new Color(86, 243, 220,
								75));

					}
					if (tileOnMap(feathertilexy)) {
						g.setColor(new Color(86, 243, 220, 75));
						g.fillOval(tileToMinimap(feathertilexy).x - 3,
								tileToMinimap(feathertilexy).y - 1, 2, 2);
					}
				}

			}

			currentTab = paintTab();

			switch (currentTab) {
			case -1: // PAINT OFF
				g.setColor(new Color(0, 0, 0, 150));
				g.fillRect(r1.x, r1.y, r1.width, r1.height);
				g.setColor(Color.WHITE);
				drawString(g, "Show Paint", r1, 5);
				break;
			case 0: // DEFAULT TAB - MAIN

				drawPaint(g, r2c);
				g.setColor(new Color(100, 100, 100, 200));
				g.drawLine(r.x + 204, r.y + 22, r.x + 204, r.y + 109);
				g.setColor(Color.WHITE);
				g.setFont(new Font("sansserif", Font.BOLD, 14));
				drawString(g, properties.name() + " V" + properties.version(),
						r, -40);
				g.setFont(new Font("sansserif", Font.PLAIN, 12));
				drawStringMain(g, "Runtime: ", formattedTime, r, 20, 35, 0,
						true);
				drawStringMain(g, "Status: ", status, r, 20, 35, 1, true);
				drawStringMain(g, "MouseSpeed: ", Integer
						.toString(actualMouseSpeed), r, 20, 35, 1, false);

				if (getCurrentTab() == TAB_INVENTORY) {
					int featherPerHour = 0;
					int moneyPerHour = 0;

					if (startFeathers == 0) {
						startFeathers = getInventoryCount(feathersID);
					}

					final int totalFeathers = getInventoryCount(feathersID)
							- startFeathers;
					final int totalMoney = totalFeathers * featherMarketPrice;
					if (runTime / 1000 > 0) {
						featherPerHour = (int) (3600000.0 / runTime * totalFeathers);
						moneyPerHour = (int) (3600000.0 / runTime * totalMoney);
					}
					drawStringMain(g, "Feathers gained: ", Integer
							.toString(totalFeathers), r, 20, 35, 3, true);
					drawStringMain(g, "Feathers / Hour: ", Integer
							.toString(featherPerHour), r, 20, 35, 4, true);

					drawStringMain(g, "Money Gained: ", Integer
							.toString(totalMoney), r, 20, 35, 3, false);
					drawStringMain(g, "Money / Hour: ", Integer
							.toString(moneyPerHour), r, 20, 35, 4, false);
				}

				break;
			case 1: // INFO
				drawPaint(g, r3c);
				g.setColor(new Color(100, 100, 100, 200));
				g.drawLine(r.x + 204, r.y + 22, r.x + 204, r.y + 109);
				g.setColor(Color.WHITE);
				g.setFont(new Font("sansserif", Font.BOLD, 14));
				drawString(g, properties.name() + " V" + properties.version(),
						r, -40);
				g.setFont(new Font("sansserif", Font.PLAIN, 12));
				drawStringMain(g, "Version: ", Double.toString(properties
						.version()), r, 20, 35, 0, true);
				g.setFont(new Font("sansserif", Font.ITALIC, 12));
				drawStringMain(g, "Script made by BeanMan ", "", r, 20, 35, 1,
						true);
				drawStringMain(g, "XScripting Inc. ", "", r, 20, 35, 1, false);
				drawStringMain(g, "Credits to Garrett for his nice paint ", "",
						r, 20, 35, 3, true);

				break;
			case 2: // STATS
				drawPaint(g, r4c);
				drawStats(g);
				hoverMenu(g);
				g.setFont(new Font("serif", Font.PLAIN, 11));
				drawStringMain(
						g,
						"Hover your mouse over the progressbar(s) to see more details !",
						"", r, 20, 35, 4, true);
				break;
			}
		}

		public int paintTab() {
			final Point mouse = new Point(Bot.getClient().getMouse().x, Bot
					.getClient().getMouse().y);
			if (mouseWatcher.isAlive()) {
				return currentTab;
			}
			if (r1.contains(mouse)) {
				mouseWatcher = new Thread(new MouseWatcher(r1));
				mouseWatcher.start();
				if (currentTab == -1) {
					return lastTab;
				} else {
					lastTab = currentTab;
					return -1;
				}
			}
			if (currentTab == -1) {
				return currentTab;
			}
			if (r2.contains(mouse)) {
				return 0;
			}
			if (r3.contains(mouse)) {
				return 1;
			}
			if (r4.contains(mouse)) {
				return 2;
			}
			return currentTab;
		}

		public void saveStats() {
			nf.setMinimumIntegerDigits(2);
			final String[] stats = Skills.statsArray;
			start_exp = new int[stats.length];
			start_lvl = new int[stats.length];
			for (int i = 0; i < stats.length; i++) {
				start_exp[i] = skills.getCurrentSkillExp(i);
				start_lvl[i] = skills.getCurrentSkillLevel(i);
			}
			for (int i = 0; i < barIndex.length; i++) {
				barIndex[i] = -1;
			}
			savedStats = true;
		}

	}

	public class KillDaChicksGUI extends JFrame {

		private static final long serialVersionUID = 1L;

		public KillDaChicksGUI() {
			initComponents();
		}

		private void button1ActionPerformed(ActionEvent e) {
			takeFeathers = radioButton2.isSelected();
			takeBones1 = radioButton3.isSelected();
			takeBones2 = radioButton4.isSelected();
			antibanGui = radioButton6.isSelected();
			mouseSpeed = slider1.getValue();
			if (comboBox2.getSelectedIndex() == 0) {
				stopScriptAtLevel = false;
			} else if (comboBox2.getSelectedIndex() == 1) {
				SELECTED_STAT = STAT_ATTACK;
				stopAtLevel = Integer.parseInt(textField1.getText());
			} else if (comboBox2.getSelectedIndex() == 2) {
				SELECTED_STAT = STAT_STRENGTH;
				stopAtLevel = Integer.parseInt(textField1.getText());
			} else if (comboBox2.getSelectedIndex() == 3) {
				SELECTED_STAT = STAT_DEFENSE;
				stopAtLevel = Integer.parseInt(textField1.getText());
			} else if (comboBox2.getSelectedIndex() == 4) {
				SELECTED_STAT = STAT_MAGIC;
				stopAtLevel = Integer.parseInt(textField1.getText());
			} else if (comboBox2.getSelectedIndex() == 5) {
				SELECTED_STAT = STAT_RANGE;
				stopAtLevel = Integer.parseInt(textField1.getText());
			} else if (comboBox2.getSelectedIndex() == 6) {
				SELECTED_STAT = STAT_HITPOINTS;
				stopAtLevel = Integer.parseInt(textField1.getText());
			} else if (comboBox2.getSelectedIndex() == 7) {
				SELECTED_STAT = STAT_PRAYER;
				stopAtLevel = Integer.parseInt(textField1.getText());
			}
			advancedPaint = radioButton13.isSelected();
			hoverMouse1 = radioButton8.isSelected();
			hoverMouse2 = radioButton9.isSelected();
			location = comboBox1.getSelectedItem().toString();
			if (location.equals("Lumbridge East(near cowfield)")) {

				Xmin = 3225;
				Xmax = 3236;
				Ymin = 3291;
				Ymax = 3301;

			} else if (location.equals("South of Falador")) {

				Xmin = 3014;
				Xmax = 3020;
				Ymin = 3282;
				Ymax = 3298;

			} else {

				Xmin = 3195;
				Xmax = 3198;
				Ymin = 3352;
				Ymax = 3359;
			}
			bronzeArrow = checkBox1.isSelected();
			ironArrow = checkBox2.isSelected();
			steelArrow = checkBox3.isSelected();
			mithrilArrow = checkBox4.isSelected();
			addyArrow = checkBox5.isSelected();
			runeArrow = checkBox6.isSelected();
			if (bronzeArrow || ironArrow || steelArrow || mithrilArrow
					|| addyArrow || runeArrow) {
				log("We will take selected arrows");
				takeArrow = true;
			}
			guiWait = false;

			dispose();
		}

		private void button2ActionPerformed(ActionEvent e) {
			guiWait = false;
			guiExit = true;
			stopScript();
			dispose();
		}

		private void visitthreadActionPerformed(ActionEvent e) {
			final int redirect = JOptionPane.showConfirmDialog(null,
					"Are you sure you want to visit the thread?",
					"Redirecting", JOptionPane.YES_NO_OPTION);
			if (redirect == 0) {
				openURL("http://www.rsbot.org/vb/showthread.php?t=212893");
			}
		}

		private void initComponents() {
			// GEN-BEGIN:initComponents
			// Generated using JFormDesigner Evaluation license - Bert De Geyter
			button1 = new JButton();
			button2 = new JButton();
			tabbedPane1 = new JTabbedPane();
			panel1 = new JPanel();
			label3 = new JLabel();
			radioButton2 = new JRadioButton();
			radioButton1 = new JRadioButton();
			label4 = new JLabel();
			radioButton3 = new JRadioButton();
			radioButton4 = new JRadioButton();
			radioButton5 = new JRadioButton();
			label5 = new JLabel();
			label12 = new JLabel();
			radioButton6 = new JRadioButton();
			radioButton7 = new JRadioButton();
			label24 = new JLabel();
			label29 = new JLabel();
			comboBox1 = new JComboBox();
			label30 = new JLabel();
			radioButton13 = new JRadioButton();
			radioButton14 = new JRadioButton();
			label31 = new JLabel();
			button3 = new JButton();
			separator1 = new JSeparator();
			separator2 = new JSeparator();
			separator3 = new JSeparator();
			separator4 = new JSeparator();
			separator5 = new JSeparator();
			checkBox1 = new JCheckBox();
			checkBox2 = new JCheckBox();
			checkBox3 = new JCheckBox();
			checkBox4 = new JCheckBox();
			checkBox5 = new JCheckBox();
			checkBox6 = new JCheckBox();
			separator7 = new JSeparator();
			panel2 = new JPanel();
			label6 = new JLabel();
			slider1 = new JSlider();
			label7 = new JLabel();
			label13 = new JLabel();
			label21 = new JLabel();
			radioButton8 = new JRadioButton();
			radioButton9 = new JRadioButton();
			radioButton10 = new JRadioButton();
			label26 = new JLabel();
			separator9 = new JSeparator();
			separator10 = new JSeparator();
			comboBox2 = new JComboBox();
			label14 = new JLabel();
			label15 = new JLabel();
			textField1 = new JTextField();
			label16 = new JLabel();
			separator11 = new JSeparator();
			button4 = new JButton();
			panel3 = new JPanel();
			label2 = new JLabel();
			label8 = new JLabel();
			label9 = new JLabel();
			label10 = new JLabel();
			label11 = new JLabel();
			label32 = new JLabel();
			label17 = new JLabel();

			// ======== this ========
			setTitle("XChicken Slaugher GUI - BeanMan Xscripting Inc.");
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
			setBackground(Color.white);
			setResizable(false);
			Container contentPane = getContentPane();
			contentPane.setLayout(null);

			// ---- button1 ----
			button1.setText("Start!");
			button1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					button1ActionPerformed(e);
				}
			});
			contentPane.add(button1);
			button1.setBounds(100, 525, 75, 28);

			// ---- button2 ----
			button2.setText("Exit");
			button2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					button2ActionPerformed(e);
				}
			});
			contentPane.add(button2);
			button2.setBounds(230, 525, 70, 28);

			// ======== tabbedPane1 ========
			{

				// ======== panel1 ========
				{

					panel1.setLayout(null);

					// ---- label3 ----
					label3.setText("Take feathers:");
					panel1.add(label3);
					label3.setBounds(new Rectangle(new Point(5, 130), label3
							.getPreferredSize()));

					// ---- radioButton2 ----
					radioButton2.setText("Yes");
					panel1.add(radioButton2);
					radioButton2.setBounds(new Rectangle(new Point(125, 125),
							radioButton2.getPreferredSize()));

					// ---- radioButton1 ----
					radioButton1.setText("No");
					radioButton1.setSelected(true);
					panel1.add(radioButton1);
					radioButton1.setBounds(new Rectangle(new Point(210, 125),
							radioButton1.getPreferredSize()));

					// ---- label4 ----
					label4.setText("Take & bury bones:");
					panel1.add(label4);
					label4.setBounds(new Rectangle(new Point(5, 160), label4
							.getPreferredSize()));

					// ---- radioButton3 ----
					radioButton3
							.setText("Yes, take them as soon as they appear");
					panel1.add(radioButton3);
					radioButton3.setBounds(new Rectangle(new Point(125, 160),
							radioButton3.getPreferredSize()));

					// ---- radioButton4 ----
					radioButton4
							.setText("Yes, take them when no chickens are around");
					radioButton4.setSelected(true);
					panel1.add(radioButton4);
					radioButton4.setBounds(new Rectangle(new Point(125, 185),
							radioButton4.getPreferredSize()));

					// ---- radioButton5 ----
					radioButton5.setText("No, I don't want any bones");
					panel1.add(radioButton5);
					radioButton5.setBounds(new Rectangle(new Point(125, 210),
							radioButton5.getPreferredSize()));

					// ---- label5 ----
					label5
							.setText("Note: taking & burying bones slows exp down!");
					label5.setFont(label5.getFont().deriveFont(
							label5.getFont().getSize() - 1f));
					panel1.add(label5);
					label5.setBounds(new Rectangle(new Point(135, 240), label5
							.getPreferredSize()));

					// ---- label12 ----
					label12.setText("AntiBan:");
					panel1.add(label12);
					label12.setBounds(new Rectangle(new Point(5, 100), label12
							.getPreferredSize()));

					// ---- radioButton6 ----
					radioButton6.setText("Yes");
					radioButton6.setSelected(true);
					panel1.add(radioButton6);
					radioButton6.setBounds(new Rectangle(new Point(125, 95),
							radioButton6.getPreferredSize()));

					// ---- radioButton7 ----
					radioButton7.setText("No");
					panel1.add(radioButton7);
					radioButton7.setBounds(new Rectangle(new Point(210, 95),
							radioButton7.getPreferredSize()));

					// ---- label24 ----
					label24
							.setText("Select the arrows you would like to pick up:");
					panel1.add(label24);
					label24.setBounds(new Rectangle(new Point(5, 270), label24
							.getPreferredSize()));

					// ---- label29 ----
					label29.setText("Location:");
					panel1.add(label29);
					label29.setBounds(new Rectangle(new Point(5, 20), label29
							.getPreferredSize()));

					// ---- comboBox1 ----
					comboBox1.setModel(new DefaultComboBoxModel(new String[] {
							"Lumbridge East(near cowfield)",
							"South of Falador", "Champions Guild" }));
					panel1.add(comboBox1);
					comboBox1.setBounds(new Rectangle(new Point(130, 15),
							comboBox1.getPreferredSize()));

					// ---- label30 ----
					label30.setText("Would you like a ");
					panel1.add(label30);
					label30.setBounds(new Rectangle(new Point(5, 55), label30
							.getPreferredSize()));

					// ---- radioButton13 ----
					radioButton13.setText("Yes");
					radioButton13.setSelected(true);
					panel1.add(radioButton13);
					radioButton13.setBounds(new Rectangle(new Point(125, 55),
							radioButton13.getPreferredSize()));

					// ---- radioButton14 ----
					radioButton14.setText("No");
					panel1.add(radioButton14);
					radioButton14.setBounds(new Rectangle(new Point(210, 55),
							radioButton14.getPreferredSize()));

					// ---- label31 ----
					label31.setText(" more advanced paint:");
					panel1.add(label31);
					label31.setBounds(new Rectangle(new Point(5, 70), label31
							.getPreferredSize()));

					// ---- button3 ----
					button3
							.setText("Visit thread on forum to say thanks if you like my script!");
					button3.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							visitthreadActionPerformed(e);
						}
					});
					panel1.add(button3);
					button3.setBounds(new Rectangle(new Point(40, 330), button3
							.getPreferredSize()));
					panel1.add(separator1);
					separator1.setBounds(0, 45, 370, separator1
							.getPreferredSize().height);
					panel1.add(separator2);
					separator2.setBounds(0, 90, 370, separator2
							.getPreferredSize().height);
					panel1.add(separator3);
					separator3.setBounds(0, 120, 370, 2);
					panel1.add(separator4);
					separator4.setBounds(0, 150, 370, 2);
					panel1.add(separator5);
					separator5.setBounds(0, 260, 370, 2);

					// ---- checkBox1 ----
					checkBox1.setText("Bronze");
					panel1.add(checkBox1);
					checkBox1.setBounds(new Rectangle(new Point(5, 290),
							checkBox1.getPreferredSize()));

					// ---- checkBox2 ----
					checkBox2.setText("Iron");
					panel1.add(checkBox2);
					checkBox2.setBounds(new Rectangle(new Point(65, 290),
							checkBox2.getPreferredSize()));

					// ---- checkBox3 ----
					checkBox3.setText("Steel");
					panel1.add(checkBox3);
					checkBox3.setBounds(new Rectangle(new Point(120, 290),
							checkBox3.getPreferredSize()));

					// ---- checkBox4 ----
					checkBox4.setText("Mithril");
					panel1.add(checkBox4);
					checkBox4.setBounds(new Rectangle(new Point(180, 290),
							checkBox4.getPreferredSize()));

					// ---- checkBox5 ----
					checkBox5.setText("Adamant");
					panel1.add(checkBox5);
					checkBox5.setBounds(new Rectangle(new Point(240, 290),
							checkBox5.getPreferredSize()));

					// ---- checkBox6 ----
					checkBox6.setText("Rune");
					panel1.add(checkBox6);
					checkBox6.setBounds(new Rectangle(new Point(315, 290),
							checkBox6.getPreferredSize()));
					panel1.add(separator7);
					separator7.setBounds(0, 320, 370, separator7
							.getPreferredSize().height);

					{ // compute preferred size
						Dimension preferredSize = new Dimension();
						for (int i = 0; i < panel1.getComponentCount(); i++) {
							Rectangle bounds = panel1.getComponent(i)
									.getBounds();
							preferredSize.width = Math.max(bounds.x
									+ bounds.width, preferredSize.width);
							preferredSize.height = Math.max(bounds.y
									+ bounds.height, preferredSize.height);
						}
						Insets insets = panel1.getInsets();
						preferredSize.width += insets.right;
						preferredSize.height += insets.bottom;
						panel1.setMinimumSize(preferredSize);
						panel1.setPreferredSize(preferredSize);
					}
				}
				tabbedPane1.addTab("Basic Settings", panel1);

				// ======== panel2 ========
				{
					panel2.setLayout(null);

					// ---- label6 ----
					label6.setText("MouseSpeed:");
					panel2.add(label6);
					label6.setBounds(new Rectangle(new Point(5, 10), label6
							.getPreferredSize()));

					// ---- slider1 ----
					slider1.setMaximum(10);
					slider1.setMinorTickSpacing(1);
					slider1.setMajorTickSpacing(1);
					slider1.setPaintLabels(true);
					slider1.setSnapToTicks(true);
					slider1.setToolTipText("MousSpeed");
					slider1.setValue(5);
					panel2.add(slider1);
					slider1.setBounds(new Rectangle(new Point(130, 0), slider1
							.getPreferredSize()));

					// ---- label7 ----
					label7.setText("Note: Lower = Faster, 5 = default");
					label7.setFont(label7.getFont().deriveFont(
							label7.getFont().getSize() - 1f));
					panel2.add(label7);
					label7.setBounds(new Rectangle(new Point(140, 45), label7
							.getPreferredSize()));

					// ---- label13 ----
					label13
							.setText("Choose the skill and the desired lvl you want the script to stop at:");
					panel2.add(label13);
					label13.setBounds(new Rectangle(new Point(5, 65), label13
							.getPreferredSize()));

					// ---- label21 ----
					label21.setText("Use HoverMouse :");
					panel2.add(label21);
					label21.setBounds(new Rectangle(new Point(5, 165), label21
							.getPreferredSize()));

					// ---- radioButton8 ----
					radioButton8.setText("Yes, always");
					panel2.add(radioButton8);
					radioButton8.setBounds(new Rectangle(new Point(115, 160),
							radioButton8.getPreferredSize()));

					// ---- radioButton9 ----
					radioButton9.setText("Yes, sometimes");
					radioButton9.setSelected(true);
					panel2.add(radioButton9);
					radioButton9.setBounds(new Rectangle(new Point(115, 185),
							radioButton9.getPreferredSize()));

					// ---- radioButton10 ----
					radioButton10.setText("No");
					panel2.add(radioButton10);
					radioButton10.setBounds(new Rectangle(new Point(115, 210),
							radioButton10.getPreferredSize()));

					// ---- label26 ----
					label26
							.setText("Note: option 2 requires AntiBan enabled, look at basic settings");
					label26.setFont(label26.getFont().deriveFont(
							label26.getFont().getSize() - 1f));
					panel2.add(label26);
					label26.setBounds(new Rectangle(new Point(5, 230), label26
							.getPreferredSize()));
					panel2.add(separator9);
					separator9.setBounds(0, 60, 370, 5);
					panel2.add(separator10);
					separator10.setBounds(0, 150, 370, 5);

					// ---- comboBox2 ----
					comboBox2.setModel(new DefaultComboBoxModel(new String[] {
							"I don't want to stop the script!", "Attack",
							"Strength", "Defense", "Magic", "Ranged",
							"Hitpoints", "Prayer" }));
					panel2.add(comboBox2);
					comboBox2.setBounds(new Rectangle(new Point(95, 90),
							comboBox2.getPreferredSize()));

					// ---- label14 ----
					label14.setText("Skill:");
					panel2.add(label14);
					label14.setBounds(new Rectangle(new Point(25, 95), label14
							.getPreferredSize()));

					// ---- label15 ----
					label15.setText("Level:");
					panel2.add(label15);
					label15.setBounds(new Rectangle(new Point(25, 125), label15
							.getPreferredSize()));

					// ---- textField1 ----
					textField1.setText("0");
					panel2.add(textField1);
					textField1.setBounds(95, 120, 50, textField1
							.getPreferredSize().height);

					// ---- label16 ----
					label16.setText("(only numbers)");
					label16.setFont(label16.getFont().deriveFont(
							label16.getFont().getSize() - 1f));
					panel2.add(label16);
					label16.setBounds(new Rectangle(new Point(160, 125),
							label16.getPreferredSize()));
					panel2.add(separator11);
					separator11.setBounds(0, 250, 370, separator11
							.getPreferredSize().height);

					// ---- button4 ----
					button4
							.setText("Visit thread on forum to say thanks if you like my script!");
					button4.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							visitthreadActionPerformed(e);
						}
					});
					panel2.add(button4);
					button4.setBounds(new Rectangle(new Point(30, 290), button4
							.getPreferredSize()));

					{ // compute preferred size
						Dimension preferredSize = new Dimension();
						for (int i = 0; i < panel2.getComponentCount(); i++) {
							Rectangle bounds = panel2.getComponent(i)
									.getBounds();
							preferredSize.width = Math.max(bounds.x
									+ bounds.width, preferredSize.width);
							preferredSize.height = Math.max(bounds.y
									+ bounds.height, preferredSize.height);
						}
						Insets insets = panel2.getInsets();
						preferredSize.width += insets.right;
						preferredSize.height += insets.bottom;
						panel2.setMinimumSize(preferredSize);
						panel2.setPreferredSize(preferredSize);
					}
				}
				tabbedPane1.addTab("Extra's", panel2);

				// ======== panel3 ========
				{
					panel3.setLayout(null);

					// ---- label2 ----
					label2.setText("Made By BeanMan");
					panel3.add(label2);
					label2.setBounds(new Rectangle(new Point(10, 5), label2
							.getPreferredSize()));

					// ---- label8 ----
					label8.setText("Xscripting Inc.");
					panel3.add(label8);
					label8.setBounds(new Rectangle(new Point(10, 20), label8
							.getPreferredSize()));

					// ---- label9 ----
					label9.setText("Credits to Taha, Epic_ for their antiban,");
					panel3.add(label9);
					label9.setBounds(80, 205, 195, 30);

					// ---- label10 ----
					label10
							.setText("and Durka Durka Mahn for his auto-updating feature,");
					panel3.add(label10);
					label10.setBounds(new Rectangle(new Point(55, 235), label10
							.getPreferredSize()));

					// ---- label11 ----
					label11
							.setText("Thank you to anyone at Xscripting team !!!");
					panel3.add(label11);
					label11.setBounds(new Rectangle(new Point(75, 190), label11
							.getPreferredSize()));

					// ---- label32 ----
					label32.setText("and Garret for his nice paint.");
					panel3.add(label32);
					label32.setBounds(new Rectangle(new Point(95, 255), label32
							.getPreferredSize()));

					{ // compute preferred size
						Dimension preferredSize = new Dimension();
						for (int i = 0; i < panel3.getComponentCount(); i++) {
							Rectangle bounds = panel3.getComponent(i)
									.getBounds();
							preferredSize.width = Math.max(bounds.x
									+ bounds.width, preferredSize.width);
							preferredSize.height = Math.max(bounds.y
									+ bounds.height, preferredSize.height);
						}
						Insets insets = panel3.getInsets();
						preferredSize.width += insets.right;
						preferredSize.height += insets.bottom;
						panel3.setMinimumSize(preferredSize);
						panel3.setPreferredSize(preferredSize);
					}
				}
				tabbedPane1.addTab("Xscripting Inc. + Credits", panel3);

			}
			contentPane.add(tabbedPane1);
			tabbedPane1.setBounds(15, 130, 375, 390);

			// ---- label17 ----
			label17
					.setText("<html><img src=http://binaryx.nl/xscripting/beanman/XChickenSlaughter/xchickenslaughter.png /></html>");
			contentPane.add(label17);
			label17.setBounds(new Rectangle(new Point(30, 10), label17
					.getPreferredSize()));

			{ // compute preferred size
				Dimension preferredSize = new Dimension();
				for (int i = 0; i < contentPane.getComponentCount(); i++) {
					Rectangle bounds = contentPane.getComponent(i).getBounds();
					preferredSize.width = Math.max(bounds.x + bounds.width,
							preferredSize.width);
					preferredSize.height = Math.max(bounds.y + bounds.height,
							preferredSize.height);
				}
				Insets insets = contentPane.getInsets();
				preferredSize.width += insets.right;
				preferredSize.height += insets.bottom;
				contentPane.setMinimumSize(preferredSize);
				contentPane.setPreferredSize(preferredSize);
			}
			setSize(415, 595);
			setLocationRelativeTo(getOwner());

			// ---- buttonGroup1 ----
			ButtonGroup buttonGroup1 = new ButtonGroup();
			buttonGroup1.add(radioButton2);
			buttonGroup1.add(radioButton1);

			// ---- buttonGroup2 ----
			ButtonGroup buttonGroup2 = new ButtonGroup();
			buttonGroup2.add(radioButton3);
			buttonGroup2.add(radioButton4);
			buttonGroup2.add(radioButton5);

			// ---- buttonGroup3 ----
			ButtonGroup buttonGroup3 = new ButtonGroup();
			buttonGroup3.add(radioButton6);
			buttonGroup3.add(radioButton7);

			// ---- buttonGroup6 ----
			ButtonGroup buttonGroup6 = new ButtonGroup();
			buttonGroup6.add(radioButton13);
			buttonGroup6.add(radioButton14);

			// ---- buttonGroup4 ----
			ButtonGroup buttonGroup4 = new ButtonGroup();
			buttonGroup4.add(radioButton8);
			buttonGroup4.add(radioButton9);
			buttonGroup4.add(radioButton10);
			// GEN-END:initComponents
		}

		// GEN-BEGIN:variables
		// Generated using JFormDesigner Evaluation license - Bert De Geyter
		private JButton button1;
		private JButton button2;
		private JTabbedPane tabbedPane1;
		private JPanel panel1;
		private JLabel label3;
		private JRadioButton radioButton2;
		private JRadioButton radioButton1;
		private JLabel label4;
		private JRadioButton radioButton3;
		private JRadioButton radioButton4;
		private JRadioButton radioButton5;
		private JLabel label5;
		private JLabel label12;
		private JRadioButton radioButton6;
		private JRadioButton radioButton7;
		private JLabel label24;
		private JLabel label29;
		private JComboBox comboBox1;
		private JLabel label30;
		private JRadioButton radioButton13;
		private JRadioButton radioButton14;
		private JLabel label31;
		private JButton button3;
		private JSeparator separator1;
		private JSeparator separator2;
		private JSeparator separator3;
		private JSeparator separator4;
		private JSeparator separator5;
		private JCheckBox checkBox1;
		private JCheckBox checkBox2;
		private JCheckBox checkBox3;
		private JCheckBox checkBox4;
		private JCheckBox checkBox5;
		private JCheckBox checkBox6;
		private JSeparator separator7;
		private JPanel panel2;
		private JLabel label6;
		private JSlider slider1;
		private JLabel label7;
		private JLabel label13;
		private JLabel label21;
		private JRadioButton radioButton8;
		private JRadioButton radioButton9;
		private JRadioButton radioButton10;
		private JLabel label26;
		private JSeparator separator9;
		private JSeparator separator10;
		private JComboBox comboBox2;
		private JLabel label14;
		private JLabel label15;
		private JTextField textField1;
		private JLabel label16;
		private JSeparator separator11;
		private JButton button4;
		private JPanel panel3;
		private JLabel label2;
		private JLabel label8;
		private JLabel label9;
		private JLabel label10;
		private JLabel label11;
		private JLabel label32;
		private JLabel label17;

		// GEN-END:variables
	}

	private enum State {
		FIGHTING, ATTACK, PICKUPFEATHERS, PICKUPBONES, PICKBRONZEARROW, PICKIRONARROW, PICKSTEELARROW, PICKMITHRILARROW, PICKADDYARROW, PICKRUNEARROW, BURY, DROP, WAIT, SETRUN, WELCOMESCREEN, TOLOCATION, EQUIPBRONZE, EQUIPIRON, EQUIPSTEEL, EQUIPMITHRIL, EQUIPADDY, EQUIPRUNE, STOPSCRIPT, TOLUMBRIDGE, TOFALADOR
	}

	private class XChickenSlaughterAntiBan implements Runnable {
		// CREDITS TO MULTITHREADING ANTIBAN TO TAHA
		public boolean stopThread;

		public void run() {
			while (!stopThread) {
				try {
					if (random(0, 12) == 0) {
						final char[] LR = new char[] { KeyEvent.VK_LEFT,
								KeyEvent.VK_RIGHT };
						final char[] UD = new char[] { KeyEvent.VK_DOWN,
								KeyEvent.VK_UP };
						final char[] LRUD = new char[] { KeyEvent.VK_LEFT,
								KeyEvent.VK_RIGHT, KeyEvent.VK_UP,
								KeyEvent.VK_UP };
						final int random2 = random(0, 2);
						final int random1 = random(0, 2);
						final int random4 = random(0, 4);

						if (random(0, 3) == 0) {
							Bot.getInputManager().pressKey(LR[random1]);
							Thread.sleep(random(100, 400));
							Bot.getInputManager().pressKey(UD[random2]);
							Thread.sleep(random(300, 600));
							Bot.getInputManager().releaseKey(UD[random2]);
							Thread.sleep(random(100, 400));
							Bot.getInputManager().releaseKey(LR[random1]);
						} else {
							Bot.getInputManager().pressKey(LRUD[random4]);
							if (random4 > 1) {
								Thread.sleep(random(300, 600));
							} else {
								Thread.sleep(random(500, 900));
							}
							Bot.getInputManager().releaseKey(LRUD[random4]);
						}
					} else {
						Thread.sleep(random(200, 2000));
					}
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public ScriptManifest properties = getClass().getAnnotation(
			ScriptManifest.class);
	public int feathersID = 314, bonesID = 526, featherMarketPrice,
			startFeathers, mouseSpeed, actualMouseSpeed, SELECTED_STAT,
			stopAtLevel, Xmin, Xmax, Ymin, Ymax, bronzeArrowID = 882,
			ironArrowID = 884, steelArrowID = 886, mithrilArrowID = 888,
			addyArrowID = 890, runeArrowID = 892;
	public int arrowID[] = { bronzeArrowID, ironArrowID, steelArrowID,
			mithrilArrowID, addyArrowID, runeArrowID };
	public int thingsToDrop[];
	public int thingsWithoutBonesToDrop[] = { 2138, 1944, 1351 };
	public int thingsWithBonesToDrop[] = { 2138, 1944, 1351, bonesID };
	public boolean takeFeathers, takeBones1, takeBones2, guiWait = true,
			guiExit, antibanGui, hoverMouse1, hoverMouse2, advancedPaint,
			bronzeArrow, ironArrow, steelArrow, mithrilArrow, addyArrow,
			runeArrow, takeArrow, noAmmo, stopScriptAtLevel = true;
	public String location;
	public RSTile lumbridge = new RSTile(3238, 3295);
	public RSTile guild = new RSTile(3197, 3355);
	public RSTile lumbridgeCenter = new RSTile(3234, 3296);
	public RSTile falador = new RSTile(3027, 3286);
	public long startTime = System.currentTimeMillis();

	private String status = "Starting up...";

	XChickenSlaughterAntiBan antiban;

	Thread t;

	final GarrettsPaint thePainter = new GarrettsPaint();

	KillDaChicksGUI gui;

	public int antiBan() {

		// CREDITS TO EPIC_ FOR ANTIBAN

		final int ranNo = random(0, 25);

		if (ranNo == 2) {

			moveCameraSlightly();
			return random(200, 400);

		} else if (ranNo == 3) {

			moveCameraSlightly();
			return random(200, 400);
		} else if (ranNo == 4) {

			moveMouse(random(0, 700), random(0, 500));
			return random(200, 400);
		} else if (ranNo == 5) {

			moveMouse(random(0, 450), random(0, 400));
			return random(200, 400);
		} else if (ranNo == 6) {

			setCameraRotation(random(-360, 360));
			return random(200, 400);
		} else if (ranNo == 7 && hoverMouse2) {

			RSNPC chicken = getNearestFreeNPCToAttackByName("Chicken");
			RSItemTile feathers = getNearestGroundItemByID(feathersID);
			RSItemTile bones = getNearestGroundItemByID(bonesID);
			if (feathers != null && takeFeathers) {
				Point featherspoint = feathers.getScreenLocation();
				if (!pointOnScreen(featherspoint)) {
					turnToCharacter(chicken, 5);
					wait(random(500, 1000));

				} else {

					while (getMyPlayer().getInteracting() != null) {
						RSItemTile feathers1 = getNearestGroundItemByID(feathersID);
						Point featherspoint1 = feathers1.getScreenLocation();
						moveMouse(featherspoint1);
					}

				}
			} else if (chickenPresent()) {
				Point chickenpoint = chicken.getScreenLocation();
				if (!pointOnScreen(chickenpoint)) {
					turnToCharacter(chicken, 5);
					wait(random(500, 1000));

				} else {

					while (getMyPlayer().getInteracting() != null) {
						RSNPC chicken1 = getNearestFreeNPCToAttackByName("Chicken");
						Point chickenpoint1 = chicken1.getScreenLocation();
						moveMouse(chickenpoint1);
					}

				}
			} else if (bones != null && (takeBones1 || takeBones2)) {
				Point bonespoint = bones.getScreenLocation();
				if (!pointOnScreen(bonespoint)) {
					turnToCharacter(chicken, 5);
					wait(random(500, 1000));

				} else {
					while (getMyPlayer().getInteracting() != null) {
						moveMouse(bonespoint);
						RSItemTile feathers1 = getNearestGroundItemByID(feathersID);
						Point featherspoint1 = feathers1.getScreenLocation();
						moveMouse(featherspoint1);
					}

				}
			}
			return random(200, 400);
		}
		return random(200, 450);
	}

	public boolean chickenPresent() {

		RSNPC chicken1 = getNearestFreeNPCToAttackByName("Chicken");

		if (chicken1 != null) {

			RSTile chickentile1 = chicken1.getLocation();

			int chickentileX1 = chickentile1.getX();
			int chickentileY1 = chickentile1.getY();
			if (Xmin <= chickentileX1 && chickentileX1 <= Xmax
					&& Ymax >= chickentileY1 && chickentileY1 >= Ymin) {

				return true;

			} else {

				return false;

			}

		}

		return false;

	}

	@Override
	protected int getMouseSpeed() {
		actualMouseSpeed = random(mouseSpeed - 1, mouseSpeed + 2);
		return actualMouseSpeed;
	}

	private State getState() {
		if (RSInterface.getInterface(WelcomeScreen.WELCOME_SCREEN_ID).getChild(
				WelcomeScreen.WELCOME_SCREEN_BUTTON_PLAY).getAbsoluteY() > 2) {
			status = "WelcomeScreen";
			return State.WELCOMESCREEN;
		}
		if (!isRunning() && getEnergy() >= 20) {
			status = "Activating run";
			return State.SETRUN;
		}
		if (!playerInLocation()) {
			if (location.equals("Lumbridge East(near cowfield)")) {
				status = "Walking to lumbridge";
				return State.TOLUMBRIDGE;
			} else if (location.equals("Champions Guild")) {
				status = "Stopping script";
				log("------>READ: ");
				log("Pls start the script when you already are inside the champion guild !");
				return State.STOPSCRIPT;
			} else if (location.equals("South of Falador")) {
				status = "Walking to Falador!";
				return State.TOFALADOR;

			}

		}

		if (noAmmo && takeArrow) {
			if (bronzeArrow) {
				if (inventoryContains(bronzeArrowID)) {
					return State.EQUIPBRONZE;
				} else {
					return State.STOPSCRIPT;
				}
			}
			if (ironArrow) {
				if (inventoryContains(ironArrowID)) {
					return State.EQUIPIRON;
				} else {
					return State.STOPSCRIPT;
				}

			}
			if (steelArrow) {
				if (inventoryContains(steelArrowID)) {
					return State.EQUIPSTEEL;
				} else {
					return State.STOPSCRIPT;
				}

			}
			if (mithrilArrow) {
				if (inventoryContains(mithrilArrowID)) {
					return State.EQUIPMITHRIL;
				} else {
					return State.STOPSCRIPT;
				}

			}
			if (addyArrow) {
				if (inventoryContains(addyArrowID)) {
					return State.EQUIPADDY;
				} else {
					return State.STOPSCRIPT;
				}

			}
			if (runeArrow) {
				if (inventoryContains(runeArrowID)) {
					return State.EQUIPRUNE;
				} else {
					return State.STOPSCRIPT;
				}

			}

		}

		if (getMyPlayer().getInteracting() != null) {
			status = "Fighting";
			return State.FIGHTING;
		}

		if (getInventoryCount() == 28 && getInventoryCount(bonesID) != 0) {
			status = "Burying";
			return State.BURY;
		}
		if (takeFeathers
				&& itemPresent(feathersID)
				&& (getInventoryCount() <= 27 || inventoryContainsOneOf(feathersID))) {
			status = "Picking up feathers";
			return State.PICKUPFEATHERS;
		}
		if (takeArrow) {
			if (bronzeArrow) {
				if (itemPresent(bronzeArrowID)
						&& (getInventoryCount() <= 27 || inventoryContainsOneOf(bronzeArrowID))) {
					status = "Picking up arrows";
					return State.PICKBRONZEARROW;

				}
			}
			if (ironArrow) {
				if (itemPresent(ironArrowID)
						&& (getInventoryCount() <= 27 || inventoryContainsOneOf(ironArrowID))) {
					status = "Picking up arrows";
					return State.PICKIRONARROW;

				}

			}
			if (steelArrow) {
				if (itemPresent(steelArrowID)
						&& (getInventoryCount() <= 27 || inventoryContainsOneOf(steelArrowID))) {
					status = "Picking up arrows";
					return State.PICKSTEELARROW;

				}

			}
			if (mithrilArrow) {
				if (itemPresent(mithrilArrowID)
						&& (getInventoryCount() <= 27 || inventoryContainsOneOf(mithrilArrowID))) {
					status = "Picking up arrows";
					return State.PICKMITHRILARROW;

				}

			}
			if (addyArrow) {
				if (itemPresent(addyArrowID)
						&& (getInventoryCount() <= 27 || inventoryContainsOneOf(addyArrowID))) {
					status = "Picking up arrows";
					return State.PICKADDYARROW;

				}

			}
			if (runeArrow) {
				if (itemPresent(runeArrowID)
						&& (getInventoryCount() <= 27 || inventoryContainsOneOf(runeArrowID))) {
					status = "Picking up arrows";
					return State.PICKRUNEARROW;

				}

			}

		}

		if (takeBones1 && itemPresent(bonesID) && getInventoryCount() <= 27) {
			status = "Picking up bones";
			return State.PICKUPBONES;
		}

		if (chickenPresent()) {
			status = "Attacking";
			return State.ATTACK;
		}
		if (inventoryContainsOneOf(thingsToDrop)) {
			status = "Dropping junk";
			return State.DROP;
		}
		if (takeBones2 && itemPresent(bonesID) && getInventoryCount() <= 27) {
			status = "Picking up bones";
			return State.PICKUPBONES;
		}

		status = "Waiting...";
		return State.WAIT;

	}

	public double getVersion() {
		return properties.version();
	}

	public boolean itemPresent(final int itemID) {

		RSItemTile item = getNearestGroundItemByID(itemID);

		if (item != null) {
			int itemX = item.getX();
			int itemY = item.getY();
			if (Xmin <= itemX && itemX <= Xmax && Ymax >= itemY
					&& itemY >= Ymin) {

				return true;

			} else {

				return false;

			}

		}

		return false;

	}

	public int loop() {
		try {

			if (antibanGui) {
				if (!t.isAlive()) {
					t.start();
					log("AntiBan initialized!");
				}
			}

			if (stopScriptAtLevel) {
				if (skills.getCurrentSkillLevel(SELECTED_STAT) >= stopAtLevel) {
					log("Desired level reached, stopping script!");
					logout();
					stopScript(true);
				}
			}

			thePainter.scriptRunning = true;

			if (!thePainter.savedStats) {
				thePainter.saveStats();
			}

			switch (getState()) {
			case TOFALADOR:
				RSTile loc = getMyPlayer().getLocation();
				int locX = loc.getX();
				int locY = loc.getY();
				setCameraAltitude(true);
				if (3021 <= locX && locX <= 3025 && 3296 >= locY
						&& locY >= 3291) {
					RSObject door4 = getObjectAt(new RSTile(3020, 3293));

					if (door4 != null) {
						if (!tileOnScreen(door4.getLocation())) {
							walkTileOnScreen(new RSTile(3021, 3293));
							wait(random(1000, 1500));
							break;
						} else {

							atDoor(8695, 'e');
							wait(random(1000, 1500));
							break;
						}

					} else {
						walkTileOnScreen(randomizeTile(new RSTile(3018, 3292),
								1, 1));
						wait(random(1000, 1500));
					}

					break;

				} else if (3022 <= locX && locX <= 3025 && 3290 >= locY
						&& locY >= 3286) {
					RSObject door3 = getObjectAt(new RSTile(3024, 3291));

					if (door3 != null) {
						if (!tileOnScreen(door3.getLocation())) {
							walkTileOnScreen(new RSTile(3024, 3290));
							wait(random(1000, 1500));
							break;
						} else {
							atDoor(8695, 's');
							wait(random(1000, 1500));
							break;
						}

					} else {
						walkTileOnScreen(new RSTile(3021, 3293));
						wait(random(1000, 1500));
					}

					break;

				} else if (3026 <= locX && locX <= 3028 && 3288 >= locY
						&& locY >= 3285) {
					RSObject door2 = getObjectAt(new RSTile(3026, 3287));

					if (door2 != null) {
						if (!tileOnScreen(door2.getLocation())) {
							walkTileOnScreen(new RSTile(3026, 3287));
							wait(random(1000, 1500));
							break;
						} else {

							atDoor(8695, 'w');
							wait(random(1000, 1500));
							break;
						}

					} else {
						walkTileOnScreen(new RSTile(3024, 3290));
						wait(random(1000, 1500));
					}

					break;

				} else {
					walkTo(new RSTile(3026, 3287));
					if (waitToMove(random(1000, 1500))) {
						while (getMyPlayer().isMoving()) {
							wait(random(20, 30));
						}
					}
				}
				break;

			case TOLUMBRIDGE:
				RSTile loc2 = getMyPlayer().getLocation();
				int loc2X = loc2.getX();
				int loc2Y = loc2.getY();
				if (3237 <= loc2X && loc2X <= 3239 && 3297 >= loc2Y
						&& loc2Y >= 3294) {
					RSObject door5 = getObjectAt(new RSTile(3237, 3295));
					if (door5 != null) {
						if (!tileOnScreen(door5.getLocation())) {
							walkTileOnScreen(new RSTile(3238, 3295));
							wait(random(1000, 1500));
							break;
						} else {
							atDoor(45206, 'w');
							wait(random(1000, 1500));
							break;
						}

					} else {
						walkTileOnScreen(new RSTile(3233, 3297));
						wait(random(1000, 1500));
						break;

					}
				} else {
					walkTo(new RSTile(3238, 3295));
					if (waitToMove(random(1000, 1500))) {
						while (getMyPlayer().isMoving()) {
							wait(random(20, 30));
						}
					}
					break;
				}

			case FIGHTING:

				if (hoverMouse1) {
					status = "Hovering mouse";
					RSNPC chicken = getNearestFreeNPCToAttackByName("Chicken");
					RSItemTile feathers = getNearestGroundItemByID(feathersID);
					RSItemTile bones = getNearestGroundItemByID(bonesID);
					if (feathers != null && takeFeathers) {
						Point featherspoint = feathers.getScreenLocation();
						if (!pointOnScreen(featherspoint)) {
							turnToCharacter(chicken, 5);
							wait(random(500, 1000));

						} else {

							while (getMyPlayer().getInteracting() != null) {
								RSItemTile feathers1 = getNearestGroundItemByID(feathersID);
								Point featherspoint1 = feathers1
										.getScreenLocation();
								moveMouse(featherspoint1);
							}

						}
					} else if (chickenPresent()) {
						Point chickenpoint = chicken.getScreenLocation();
						if (!pointOnScreen(chickenpoint)) {
							turnToCharacter(chicken, 5);
							wait(random(500, 1000));

						} else {

							while (getMyPlayer().getInteracting() != null) {
								RSNPC chicken1 = getNearestFreeNPCToAttackByName("Chicken");
								Point chickenpoint1 = chicken1
										.getScreenLocation();
								moveMouse(chickenpoint1);
							}

						}
					} else if (bones != null && (takeBones1 || takeBones2)) {
						Point bonespoint = bones.getScreenLocation();
						if (!pointOnScreen(bonespoint)) {
							turnToCharacter(chicken, 5);
							wait(random(500, 1000));

						} else {
							while (getMyPlayer().getInteracting() != null) {
								moveMouse(bonespoint);
								RSItemTile feathers1 = getNearestGroundItemByID(feathersID);
								Point featherspoint1 = feathers1
										.getScreenLocation();
								moveMouse(featherspoint1);
							}

						}
					}
					break;
				}
				if (hoverMouse2 && antibanGui) {
					status = "AntiBan";
					antiBan();
					break;
				}
				status = "Fighting";

				break;

			case ATTACK:

				RSNPC chicken = getNearestFreeNPCToAttackByName("Chicken");
				RSTile chickentile = chicken.getLocation();
				if (!pointOnScreen(chicken.getScreenLocation())) {
					walkTileOnScreen(chickentile);
					wait(random(1000, 1500));

				} else {
					if (getMyPlayer().getInteracting() == null) {
						atNPC(chicken, "Attack");
						moveMouseSlightly();
						if (waitToMove(random(1000, 1500))) {
							while (getMyPlayer().isMoving()) {
								wait(random(20, 30));
							}
						}
					}

				}

				break;

			case PICKUPFEATHERS:
				pickItem(feathersID, "Take Feather");
				break;

			case PICKBRONZEARROW:
				pickItem(bronzeArrowID, "Take Bronze arrow");
				break;

			case PICKIRONARROW:
				pickItem(ironArrowID, "Take Iron arrow");
				break;

			case PICKSTEELARROW:
				pickItem(steelArrowID, "Take Steel arrow");
				break;

			case PICKMITHRILARROW:
				pickItem(mithrilArrowID, "Take Mithril arrow");
				break;

			case PICKADDYARROW:
				pickItem(addyArrowID, "Take Adamant arrow");
				break;
			case PICKRUNEARROW:
				pickItem(runeArrowID, "Take Rune arrow");
				break;

			case PICKUPBONES:
				pickItem(bonesID, "Take Bones");
				break;

			case STOPSCRIPT:
				stopScript();
				break;

			case EQUIPBRONZE:
				atInventoryItem(bronzeArrowID, "Wield");
				wait(500);
				noAmmo = false;
				break;

			case EQUIPIRON:
				atInventoryItem(ironArrowID, "Wield");
				wait(500);
				noAmmo = false;
				break;

			case EQUIPSTEEL:
				atInventoryItem(steelArrowID, "Wield");
				wait(500);
				noAmmo = false;
				break;

			case EQUIPMITHRIL:
				atInventoryItem(mithrilArrowID, "Wield");
				wait(500);
				noAmmo = false;
				break;

			case EQUIPADDY:
				atInventoryItem(addyArrowID, "Wield");
				wait(500);
				noAmmo = false;
				break;

			case EQUIPRUNE:
				atInventoryItem(runeArrowID, "Wield");
				wait(500);
				noAmmo = false;
				break;

			case BURY:
				while (getInventoryCount(bonesID) != 0) {
					if (atInventoryItem(bonesID, "Bury")) {
						wait(random(200, 400));
						if (waitForAnim(random(750, 900)) != -1) {
							while (getMyPlayer().getAnimation() != -1) {
								wait(random(100, 300));
							}
						}
					}
				}

				break;

			case DROP:
				for (int i = 0; i < thingsToDrop.length; i++) {
					if (inventoryContains(thingsToDrop[i])) {
						atInventoryItem(thingsToDrop[i], "Drop");

						wait(random(800, 1200));
					}
				}

				break;

			case SETRUN:
				setRun(true);
				wait(random(800, 1000));
				break;

			case WELCOMESCREEN:
				atInterface(378, 137);
				wait(random(500, 1000));
				break;

			case WAIT:
				break;

			}

		} catch (Exception ignored) {
		}
		return 50;
	}

	public void moveCameraSlightly() {
		int angle = getCameraAngle() + random(-45, 45);
		if (angle < 0)
			angle = 0;
		if (angle > 359)
			angle = 0;
		setCameraRotation(angle);
	}

	public void onFinish() {
		ScreenshotUtil.takeScreenshot(true);
		antiban.stopThread = true;
		Bot.getEventManager().removeListener(PaintListener.class, this);
	}

	public void onRepaint(final Graphics g) {

		thePainter.paint(g);

	}

	public boolean onStart(Map<String, String> args) {
		URLConnection url = null;
		BufferedReader in = null;
		BufferedWriter out = null;

		try {

			url = new URL(
					"http://www.binaryx.nl/xscripting/beanman/XChickenSlaughter/XChickenSlaughterVERSION.txt")
					.openConnection();

			in = new BufferedReader(new InputStreamReader(url.getInputStream()));

			if (Double.parseDouble(in.readLine()) > getVersion()) {

				if (JOptionPane.showConfirmDialog(null,
						"Update found. Do you want to update?") == 0) {

					JOptionPane
							.showMessageDialog(null,
									"Please choose 'XChickenSlaughter.java' in your scripts folder and hit 'Open'");
					JFileChooser fc = new JFileChooser();

					if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {

						url = new URL(
								"http://www.binaryx.nl/xscripting/beanman/XChickenSlaughter/XChickenSlaughter.java")
								.openConnection();
						in = new BufferedReader(new InputStreamReader(url
								.getInputStream()));
						out = new BufferedWriter(new FileWriter(fc
								.getSelectedFile().getPath()));
						String inp;

						while ((inp = in.readLine()) != null) {
							out.write(inp);
							out.newLine();
							out.flush();
						}

						log("Script successfully downloaded. Please recompile and reload your scripts!");
						return false;
					} else
						log("Update canceled");
				} else
					log("Update canceled");
			} else
				JOptionPane.showMessageDialog(null,
						"You have the latest version. :)");
			if (in != null)
				in.close();
			if (out != null)
				out.close();
		} catch (IOException e) {
			log("Problem getting version :/");
			return false;
		}

		gui = new KillDaChicksGUI();
		gui.setVisible(true);
		while (guiWait) {
			wait(100);
		}
		antiban = new XChickenSlaughterAntiBan();
		t = new Thread(antiban);
		startTime = System.currentTimeMillis();
		featherMarketPrice = grandExchange.loadItemInfo(314).getMarketPrice();
		if (!takeBones1 && !takeBones2) {
			thingsToDrop = thingsWithBonesToDrop;
		} else {
			thingsToDrop = thingsWithoutBonesToDrop;
		}
		if (location.equals("Champions Guild")) {
			log("-----------------------------------------------------------------------");
			log("The bot is not able to walk to Champions Guild itself ");
			log("You must start the bot when your player is already in the guild!!! ");
			log("-----------------------------------------------------------------------");
		} else if (location.equals("South of Falador")) {
			log("--------------------------------");
			log("Location: South of Falador");
			log("--------------------------------");
		} else {
			log("--------------------------------");
			log("Location: East of Lumbridge");
			log("--------------------------------");
		}

		return !guiExit;
	}

	public void openURL(final String url) {

		final String osName = System.getProperty("os.name");

		try {

			if (osName.startsWith("Mac OS")) {

				final Class<?> fileMgr = Class
						.forName("com.apple.eio.FileManager");

				final Method openURL = fileMgr.getDeclaredMethod("openURL",

				new Class[] { String.class });

				openURL.invoke(null, new Object[] { url });

			} else if (osName.startsWith("Windows")) {
				Runtime.getRuntime().exec(

				"rundll32 url.dll,FileProtocolHandler " + url);
			} else {

				final String[] browsers = { "firefox", "opera", "konqueror",

				"epiphany", "mozilla", "netscape" };

				String browser = null;

				for (int count = 0; count < browsers.length && browser == null; count++) {
					if (Runtime.getRuntime().exec(

					new String[] { "which", browsers[count] })

					.waitFor() == 0) {
						browser = browsers[count];
					}
				}

				if (browser == null) {
					throw new Exception("Could not find web browser");
				} else {
					Runtime.getRuntime().exec(new String[] { browser, url });
				}

			}

		} catch (final Exception e) {

		}
	}

	public void pickItem(final int itemID, final String action) {
		RSItemTile item = getNearestGroundItemByID(itemID);

		if (item != null) {
			int itemX = item.getX();
			int itemY = item.getY();
			RSTile itemtile = new RSTile(itemX, itemY);
			if (!tileOnScreen(itemtile)) {
				turnToTile(itemtile, 5);
				if (!tileOnScreen(itemtile)) {
					walkTileOnScreen(randomizeTile(itemtile, 1, 1));
					wait(random(800, 1200));

				}
			} else {
				atTile(itemtile, action);
				moveMouseSlightly();
				if (waitToMove(random(1000, 1500))) {
					while (getMyPlayer().isMoving()) {
						wait(random(20, 30));
					}
				}
			}
		}
	}

	public boolean playerInLocation() {
		RSTile loc = getMyPlayer().getLocation();
		int locX = loc.getX();
		int locY = loc.getY();
		if (Xmin <= locX && locX <= Xmax && Ymax >= locY && locY >= Ymin) {
			return true;
		} else {
			return false;
		}
	}

	public void serverMessageRecieved(ServerMessageEvent e) {
		String msg = e.getMessage();
		if (msg.contains("no ammo") || msg.contains("do not have enough")) {
			log("Out of ammo: wielding your arrows in inventory");
			noAmmo = true;
		}
	}
}