import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.rsbot.event.listeners.PaintListener;
import org.rsbot.script.Script;
import org.rsbot.script.ScriptManifest;
import org.rsbot.script.wrappers.RSInterface;
import org.rsbot.script.wrappers.RSInterfaceChild;
import org.rsbot.script.wrappers.RSInterfaceComponent;

@ScriptManifest(authors = { "joku.rules" }, category = "Development", name = "Interface Explorer 2", version = 0.1, description = "<html>\n<head></head>\n<body>\n<center><h2>Interface Explorer 2</h2></center>\n<p\n<b>Author:</b> joku.rules<p>\n <b>Version:</b> 0.1<p>\nThis script is used to fetch various interface data.\n</body>\n </html>")
public class InterfaceExplorer2 extends Script implements PaintListener {
	private class InterfaceTreeModel implements TreeModel {
		private final Object root = new Object();
		private final ArrayList<TreeModelListener> treeModelListeners = new ArrayList<TreeModelListener>();

		// only call getAllInterfaces() once per GUI update, because
		// otherwise closed interfaces might mess up the indexes
		private final ArrayList<RSInterfaceWrap> interfaces = new ArrayList<RSInterfaceWrap>();

		public void addTreeModelListener(final TreeModelListener l) {
			treeModelListeners.add(l);
		}

		private void fireTreeStructureChanged(final Object oldRoot) {
			treeModelListeners.size();
			final TreeModelEvent e = new TreeModelEvent(this,
					new Object[] { oldRoot });
			for (final TreeModelListener tml : treeModelListeners) {
				tml.treeStructureChanged(e);
			}
		}

		public Object getChild(final Object parent, final int index) {
			if (parent == root) {
				return interfaces.get(index);
			} else if (parent instanceof RSInterfaceWrap) {
				return new RSInterfaceChildWrap(
						((RSInterfaceWrap) parent).wrapped.getChildren()[index]);
			} else if (parent instanceof RSInterfaceChildWrap) {
				return new RSInterfaceComponentWrap(
						((RSInterfaceChildWrap) parent).wrapped.getComponents()[index]);
			}
			return null;
		}

		public int getChildCount(final Object parent) {
			if (parent == root) {
				return interfaces.size();
			} else if (parent instanceof RSInterfaceWrap) {
				return ((RSInterfaceWrap) parent).wrapped.getChildren().length;
			} else if (parent instanceof RSInterfaceChildWrap) {
				return ((RSInterfaceChildWrap) parent).wrapped.getComponents().length;
			}
			return 0;
		}

		public int getIndexOfChild(final Object parent, final Object child) {
			if (parent == root) {
				return interfaces.indexOf(child);
			} else if (parent instanceof RSInterfaceWrap) {
				return Arrays.asList(
						((RSInterfaceWrap) parent).wrapped.getChildren())
						.indexOf(((RSInterfaceChildWrap) child).wrapped);
			} else if (parent instanceof RSInterfaceChildWrap) {
				return Arrays
						.asList(
								((RSInterfaceChildWrap) parent).wrapped
										.getComponents()).indexOf(
								((RSInterfaceComponentWrap) child).wrapped);
			}
			return -1;
		}

		public Object getRoot() {
			return root;
		}

		public boolean isLeaf(final Object o) {
			if (o instanceof RSInterfaceChildWrap) {
				return ((RSInterfaceChildWrap) o).wrapped.getComponents().length == 0;
			} else if (o instanceof RSInterfaceComponentWrap) {
				return ((RSInterfaceComponentWrap) o).wrapped.getComponents().length == 0;
			}
			return false;
		}

		public void removeTreeModelListener(final TreeModelListener l) {
			treeModelListeners.remove(l);
		}

		public boolean searchMatches(final RSInterfaceChild iface,
				final String contains) {
			return iface.getText().toLowerCase().contains(
					contains.toLowerCase());
		}

		public void update(final String search) {
			interfaces.clear();

			for (final RSInterface iface : RSInterface.getAllInterfaces()) {
				toBreak: for (final RSInterfaceChild child : iface
						.getChildren()) {
					if (searchMatches(child, search)) {
						interfaces.add(new RSInterfaceWrap(iface));
						break;
					}

					for (final RSInterfaceComponent component : child
							.getComponents()) {
						if (searchMatches(component, search)) {
							interfaces.add(new RSInterfaceWrap(iface));
							break toBreak;
						}
					}
				}
			}
			fireTreeStructureChanged(root);
		}

		public void valueForPathChanged(final TreePath path,
				final Object newValue) {
			// tree represented by this model isn't editable
		}
	}

	private class RSInterfaceChildWrap {
		public RSInterfaceChild wrapped;

		public RSInterfaceChildWrap(final RSInterfaceChild wrapped) {
			this.wrapped = wrapped;
		}

		@Override
		public boolean equals(final Object o) {
			return o instanceof RSInterfaceChildWrap ? wrapped == ((RSInterfaceChildWrap) o).wrapped
					: false;
		}

		@Override
		public String toString() {
			return "Child " + wrapped.getIndex();
		}
	}

	private class RSInterfaceComponentWrap {
		public RSInterfaceComponent wrapped;

		public RSInterfaceComponentWrap(final RSInterfaceComponent wrapped) {
			this.wrapped = wrapped;
		}

		@Override
		public boolean equals(final Object o) {
			return o instanceof RSInterfaceComponentWrap ? wrapped == ((RSInterfaceComponentWrap) o).wrapped
					: false;
		}

		@Override
		public String toString() {
			return "Component " + wrapped.getComponentIndex();
		}
	}

	// these wrappers just add toString() methods
	private class RSInterfaceWrap {
		public RSInterface wrapped;

		public RSInterfaceWrap(final RSInterface wrapped) {
			this.wrapped = wrapped;
		}

		@Override
		public boolean equals(final Object o) {
			return o instanceof RSInterfaceWrap ? wrapped == ((RSInterfaceWrap) o).wrapped
					: false;
		}

		@Override
		public String toString() {
			return "Interface " + wrapped.getIndex();
		}
	}

	private JFrame window;
	private JTree tree;

	private InterfaceTreeModel treeModel;

	private JPanel infoArea;

	private JTextField searchBox;

	private Rectangle highlightArea = null;

	@Override
	public int loop() {
		while (window.isVisible()) {
			return 1000;
		}
		return -1;
	}

	public void onRepaint(final Graphics g) {
		if (highlightArea != null) {
			g.setColor(Color.ORANGE);
			g.drawRect(highlightArea.x, highlightArea.y, highlightArea.width,
					highlightArea.height);
		}
	}

	@Override
	public boolean onStart(final Map<String, String> map) {
		window = new JFrame("Interface Explorer 2");

		treeModel = new InterfaceTreeModel();
		treeModel.update("");
		tree = new JTree(treeModel);
		tree.setRootVisible(false);
		tree.setEditable(false);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			private void addInfo(final String key, final String value) {
				final JPanel row = new JPanel();
				row.setAlignmentX(Component.LEFT_ALIGNMENT);
				row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));

				for (final String data : new String[] { key, value }) {
					final JLabel label = new JLabel(data);
					label.setAlignmentY(Component.TOP_ALIGNMENT);
					row.add(label);
				}
				infoArea.add(row);
			}

			public void valueChanged(final TreeSelectionEvent e) {
				final Object node = tree.getLastSelectedPathComponent();
				if (node == null || node instanceof RSInterfaceWrap) {
					return;
				}
				// at this point the node can only be an instace of
				// RSInterfaceChildWrap
				// or of RSInterfaceComponentWrap

				infoArea.removeAll();
				RSInterfaceChild iface = null;
				if (node instanceof RSInterfaceChildWrap) {
					highlightArea = ((RSInterfaceChildWrap) node).wrapped
							.getArea();
					iface = ((RSInterfaceChildWrap) node).wrapped;
				} else if (node instanceof RSInterfaceComponentWrap) {
					highlightArea = ((RSInterfaceComponentWrap) node).wrapped
							.getArea();
					iface = ((RSInterfaceComponentWrap) node).wrapped;
				}
				addInfo("Action type: ", "-1" /* + iface.getActionType() */);
				addInfo("Type: ", "" + iface.getType());
				addInfo("SpecialType: ", "" + iface.getSpecialType());
				addInfo("Bounds Index: ", "" + iface.getBoundsArrayIndex());
				addInfo("Model ID: ", "" + iface.getModelID());
				addInfo("Texture ID: ", "" + iface.getBackgroundColor());
				addInfo("Text: ", "" + iface.getText());
				addInfo("Tooltip: ", "" + iface.getTooltip());
				addInfo("SelActionName: ", "" + iface.getSelectedActionName());
				if (iface.getActions() != null) {
					String actions = "";
					for (final String action : iface.getActions()) {
						if (!actions.equals("")) {
							actions += "\n";
						}
						actions += action;
					}
					addInfo("Actions: ", actions);
				}
				addInfo("Component ID: ", "" + iface.getComponentID());

				infoArea.validate();
				infoArea.repaint();
			}
		});

		JScrollPane scrollPane = new JScrollPane(tree);
		scrollPane.setPreferredSize(new Dimension(250, 500));
		window.add(scrollPane, BorderLayout.WEST);

		infoArea = new JPanel();
		infoArea.setLayout(new BoxLayout(infoArea, BoxLayout.Y_AXIS));
		scrollPane = new JScrollPane(infoArea);
		scrollPane.setPreferredSize(new Dimension(250, 500));
		window.add(scrollPane, BorderLayout.CENTER);

		final ActionListener actionListener = new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				treeModel.update(searchBox.getText());
				infoArea.removeAll();
				infoArea.validate();
				infoArea.repaint();
			}
		};

		final JPanel toolArea = new JPanel();
		toolArea.setLayout(new FlowLayout(FlowLayout.LEFT));
		toolArea.add(new JLabel("Filter:"));

		searchBox = new JTextField(20);
		searchBox.addActionListener(actionListener);
		toolArea.add(searchBox);

		final JButton updateButton = new JButton("Update");
		updateButton.addActionListener(actionListener);
		toolArea.add(updateButton);
		window.add(toolArea, BorderLayout.NORTH);

		window.pack();
		window.setVisible(true);
		return true;
	}
}
