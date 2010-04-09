import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import org.rsbot.bot.Bot;
import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Calculations;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSTile;

@ScriptManifest(authors = "KaruLont (~alex~) fixed by kevinkoh", name = "Area Maker", version = 1.00, category = "Development")
public class AreaMaker extends Script implements PaintListener {

	static Polygon area;

	public javax.swing.JFrame theGui = new javax.swing.JFrame();

	public ArrayList<RSTile> pathTiles = new ArrayList<RSTile>();

	// {
	// // Set Look & Feel
	// try {
	// javax.swing.UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	public JPanel jPanel1;

	public JTextArea output;

	public JButton btnAdd;
	public JPanel jPanel2;

	public JButton btnClear;

	private void initGUI() {
		try {

			theGui.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			theGui.getContentPane().setLayout(null);
			theGui.setTitle("Area Maker V" + 1.00
					+ " by KaruLont (~alex~) fixed by kevinkoh");
			{
				jPanel1 = new JPanel();
				final FlowLayout jPanel1Layout = new FlowLayout();
				theGui.getContentPane().add(jPanel1);
				jPanel1.setLayout(jPanel1Layout);
				jPanel1.setBounds(0, 0, 388, 266);
				{
					btnClear = new JButton();
					jPanel1.add(btnClear);
					btnClear.setText("Clear Tiles");
					btnClear.setPreferredSize(new java.awt.Dimension(79, 33));
					btnClear.addActionListener(new

					ActionListener() {

						public void actionPerformed(final ActionEvent e) {

							output.setText("Polygon name=new Polygon();");
							pathTiles.clear();
							AreaMaker.area = new Polygon();
						}
					});

					btnAdd = new JButton();
					jPanel1.add(btnAdd);
					btnAdd.setText("Add Tile");
					btnAdd.setBounds(117, 13, 156, 54);
					btnAdd.setPreferredSize(new java.awt.Dimension(79, 33));
					btnAdd.addActionListener(new

					ActionListener() {

						public void actionPerformed(final ActionEvent e) {
							String outPutText1 = "";
							String outPutText2 = "";
							pathTiles.add(getMyPlayer().getLocation());
							final int pX[] = new int[pathTiles.size()];
							final int pY[] = new int[pathTiles.size()];
							for (int i = 0; i != pathTiles.size(); i++) {
								pX[i] = pathTiles.get(i).getX();
								pY[i] = pathTiles.get(i).getY();
							}
							AreaMaker.area = new Polygon(pX, pY, pathTiles
									.size());
							for (int i = 0; i < pathTiles.size(); i++) {
								if (i != pathTiles.size() - 1) {
									outPutText1 += pathTiles.get(i).getX()
											+ ",";
									outPutText2 += pathTiles.get(i).getY()
											+ ",";
								} else {
									outPutText1 += pathTiles.get(i).getX() + "";
									outPutText2 += pathTiles.get(i).getY() + "";
								}
							}
							output
									.setText("int nameX[] = new int[]{"
											+ outPutText1
											+ "};\nint nameY[] = new int[]{"
											+ outPutText2
											+ "};\nPolygon name=new Polygon(nameX, nameY, "
											+ pathTiles.size() + ");");
						}
					});
				}
				{
					jPanel2 = new JPanel();
					final FlowLayout jPanel2Layout = new FlowLayout();
					jPanel1.add(jPanel2);
					jPanel2.setBounds(6, 78, 382, 188);
					jPanel2.setLayout(jPanel2Layout);
					{
						output = new JTextArea();
						output.setLineWrap(true);
						jPanel2.add(output);

						output.setText("");
						output.setBounds(6, 78, 382, 188);
						output
								.setPreferredSize(new java.awt.Dimension(360,
										161));
					}
				}
			}
			theGui.pack();
			theGui.setSize(400, 300);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public int loop() {
		if (!theGui.isVisible()) {
			return -1;
		} else {
			return random(100, 500);
		}
	}

	@Override
	public void onFinish() {
	}

	public void onRepaint(final Graphics render) {
		if (!isLoggedIn() || AreaMaker.area == null) {
			return;
		}
		render.setColor(Color.CYAN);
		render
				.drawString("Number of points: " + AreaMaker.area.npoints, 10,
						10);
		if (AreaMaker.area.npoints == 0) {
			return;
		}
		// int plane=Bot.getClient().getPlane();
		// int blocks[][] =
		// Bot.getClient().getGroundIntArray()[Bot.getClient().getPlane()];
		// int
		// blocks[][]=Bot.getClient().getGroundDataArray()[plane].getBlocks();
		final int baseX = Bot.getClient().getBaseX();
		final int baseY = Bot.getClient().getBaseY();
		for (int i = 1; i < 103; i++) {
			for (int j = 1; j < 103; j++) {
				if (AreaMaker.area.contains(baseX + i, baseY + j)) {
					// int curBlock = blocks[i][j];
					Point miniBL = Calculations.worldToMinimap(i + baseX - 0.5,
							j + baseY - 0.5);
					if (miniBL.x == -1 || miniBL.y == -1) {
						miniBL = null;
					}
					Point miniBR = Calculations.worldToMinimap(i + baseX - 0.5,
							j + baseY + 0.5);
					if (miniBR.x == -1 || miniBR.y == -1) {
						miniBR = null;
					}
					Point miniTL = Calculations.worldToMinimap(i + baseX + 0.5,
							j + baseY - 0.5);
					if (miniTL.x == -1 || miniTL.y == -1) {
						miniTL = null;
					}
					Point miniTR = Calculations.worldToMinimap(i + baseX + 0.5,
							j + baseY + 0.5);
					if (miniTR.x == -1 || miniTR.y == -1) {
						miniTR = null;
					}
					Point bl = Calculations.tileToScreen(i + baseX, j + baseY,
							0, 0, 0);
					if (bl.x == -1 || bl.y == -1) {
						bl = null;
					}
					Point br = Calculations.tileToScreen(i + baseX, j + 1
							+ baseY, 0, 0, 0);
					if (br.x == -1 || br.y == -1) {
						br = null;
					}
					Point tl = Calculations.tileToScreen(i + 1 + baseX, j
							+ baseY, 0, 0, 0);
					if (tl.x == -1 || tl.y == -1) {
						tl = null;
					}
					Point tr = Calculations.tileToScreen(i + 1 + baseX, j + 1
							+ baseY, 0, 0, 0);
					if (tr.x == -1 || tr.y == -1) {
						tr = null;
					}
					// if ((curBlock & 0x1280100) != 0) {
					if (true) {
						render.setColor(Color.black);
						if (tl != null && br != null && tr != null
								&& bl != null) {
							render.fillPolygon(new int[] { bl.x, br.x, tr.x,
									tl.x },
									new int[] { bl.y, br.y, tr.y, tl.y }, 4);
						}
						if (miniBL != null && miniBR != null && miniTR != null
								&& miniTL != null) {
							render.fillPolygon(new int[] { miniBL.x, miniBR.x,
									miniTR.x, miniTL.x }, new int[] { miniBL.y,
									miniBR.y, miniTR.y, miniTL.y }, 4);
						}
					}
					// if ((blocks[i][j - 1] & 0x1280102) != 0 || (curBlock &
					// 0x1280120) != 0) {
					if (true) {
						render.setColor(Color.RED);
						if (tl != null && bl != null) {
							render.drawLine(bl.x, bl.y, tl.x, tl.y);
						}
						if (miniBL != null && miniTL != null) {
							render.drawLine(miniBL.x, miniBL.y, miniTL.x,
									miniTL.y);
						}
					}
					// if ((blocks[i - 1][j] & 0x1280108) != 0 || (curBlock &
					// 0x1280180) != 0) {
					if (true) {
						render.setColor(Color.RED);
						if (br != null && bl != null) {
							render.drawLine(bl.x, bl.y, br.x, br.y);
						}
						if (miniBR != null && miniBL != null) {
							render.drawLine(miniBL.x, miniBL.y, miniBR.x,
									miniBR.y);
						}
					}
				}
				/*
				 * render.setColor(Color.cyan); if ((curBlock & (1<<20)) != 0) {
				 * if (miniBL != null && miniBR != null && miniTR != null &&
				 * miniTL != null) { render.fillPolygon(new
				 * int[]{miniBL.x,miniBR.x,miniTR.x,miniTL.x}, new
				 * int[]{miniBL.y,miniBR.y,miniTR.y,miniTL.y},4); } if (tl !=
				 * null && br != null && tr != null && bl != null) {
				 * render.fillPolygon(new int[]{bl.x,br.x,tr.x,tl.x}, new
				 * int[]{bl.y,br.y,tr.y,tl.y},4); } }
				 */
				// Point miniCent = Calculations.worldToMinimap(i+ baseX, j+
				// baseY);
				// Point cent = Calculations.tileToScreen(i+ baseX, j+ baseY,
				// 0.5,0.5, 0);
				/*
				 * if (cent.x != -1 && cent.y != -1) {
				 * render.setColor(Color.yellow); render.drawString("" +
				 * Calculations.getRealDistanceTo(cur.getX()-baseX,
				 * cur.getY()-baseY, i, j, false), (int)cent.getX(),
				 * (int)cent.getY()); }
				 */
			}
		}
	}

	public boolean onStart(final String[] args, final Map<String, String> args1) {
		AreaMaker.area = new Polygon();
		initGUI();
		theGui.setVisible(true);
		return true;

	}
}