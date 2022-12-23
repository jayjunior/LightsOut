
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public final class LightsOutGUI {

	public static void main(String[] args) {
		new LightsOutInner(args);
	}

	static {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Throwable ignored) {
		}
	}

	private LightsOutGUI() {
	}

	private static final class LightsOutInner extends JFrame implements ActionListener, Runnable {
		private static final long serialVersionUID = 666L;
		private static final String FRAME_TITLE = "Lights Out";
		private static final Border RAISED = new EtchedBorder(EtchedBorder.RAISED);
		private static final Border LOWERED = new EtchedBorder(EtchedBorder.LOWERED);
		private static final Color ON = new Color(192, 255, 192);
		private static final Color OFF = new Color(64, 128, 64);
		private static final Color BLOCKED = new Color(64, 64, 64);
		private static final Color HIGHLIGHT = new Color(255, 128, 128);

		private long mask;
		private int rows, cols, moves;
		private double lightDensity, maskDensity;
		private LightsOut lightsOut;
		private JButton[][] matrixGUI;
		private JButton restart;
		private JButton hint;
		private JButton auto;
		private JButton quit;
		private boolean showAll = false;

		private LightsOutInner(String[] args) {
			super(FRAME_TITLE);
			setDefaultCloseOperation(EXIT_ON_CLOSE);
			try {
				rows = cols = 4;
				rows = Integer.parseInt(args[0]);
				if (rows < 2 || rows > 42) {
					rows = 4;
				}
			} catch (Throwable ignored) {
			}
			try {
				cols = rows;
				cols = Integer.parseInt(args[1]);
				if (cols < 2 || cols > 42) {
					cols = 4;
				}
			} catch (Throwable ignored) {
			}
			try {
				lightDensity = 0.6;
				lightDensity = Double.parseDouble(args[2]);
				if (lightDensity < 0 || lightDensity >= 1) {
					lightDensity = 0.6;
				}
			} catch (Throwable ignored) {
			}
			try {
				maskDensity = 0.15;
				maskDensity = Double.parseDouble(args[3]);
				if (maskDensity < 0 || maskDensity >= 1) {
					maskDensity = 0.15;
				}
			} catch (Throwable ignored) {
			}
			createGUI();
			startNewGame();
			pack();
			setVisible(true);
		}

		private void createGUI() {
			JPanel controlPanel = new JPanel();
			controlPanel.setLayout(new GridLayout(1, 4));
			restart = new JButton("New", UIManager.getIcon("FileView.fileIcon"));
			restart.setFocusable(false);
			restart.addActionListener(this);
			controlPanel.add(restart);
			hint = new JButton("Tip?", UIManager.getIcon("OptionPane.questionIcon"));
			hint.setFocusable(false);
			hint.addActionListener(this);
			controlPanel.add(hint);
			auto = new JButton("Auto!", UIManager.getIcon("FileView.computerIcon"));
			auto.setFocusable(false);
			auto.addActionListener(this);
			controlPanel.add(auto);
			quit = new JButton("Quit", UIManager.getIcon("OptionPane.errorIcon"));
			quit.setFocusable(false);
			quit.addActionListener(this);
			controlPanel.add(quit);
			JPanel matrixPanel = new JPanel();
			int width = 666, height = 666;
			if (rows < cols) {
				height = width * rows / cols;
			} else {
				width = height * cols / rows;
			}
			matrixPanel.setPreferredSize(new Dimension(width, height));
			matrixPanel.setLayout(new GridLayout(rows, cols));
			matrixGUI = new JButton[rows][cols];
			for (int r = 0; r < rows; r++) {
				for (int c = 0; c < cols; c++) {
					matrixGUI[r][c] = new JButton();
					matrixPanel.add(matrixGUI[r][c]);
					matrixGUI[r][c].setContentAreaFilled(false);
					matrixGUI[r][c].setOpaque(true);
					matrixGUI[r][c].setFocusable(false);
					matrixGUI[r][c].addActionListener(this);
				}
			}
			GridBagLayout gridBagLayout = new GridBagLayout();
			getContentPane().setLayout(gridBagLayout);
			GridBagConstraints gridBagConstraints;
			gridBagConstraints = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTH,
					GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
			gridBagLayout.setConstraints(controlPanel, gridBagConstraints);
			getContentPane().add(controlPanel);
			gridBagConstraints = new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
			gridBagLayout.setConstraints(matrixPanel, gridBagConstraints);
			getContentPane().add(matrixPanel);
		}

		private void startNewGame() {
			mask = 0;
			moves = 0;
			long state = 0;
			for (int p = 0; p < rows * cols; p++) {
				if (Math.random() < lightDensity) {
					state = BitOps.set(p, state);
				}
				if (Math.random() < maskDensity) {
					mask = BitOps.set(p, mask);
				}
			}
			lightsOut = new LightsOut(rows, cols, mask, state);
			restart.setEnabled(true);
			hint.setEnabled(true);
			auto.setEnabled(true);
			quit.setEnabled(true);
			paintMatrix();
		}

		private void paintMatrix() {
			for (int r = 0; r < rows; r++) {
				for (int c = 0; c < cols; c++) {
					if (BitOps.isSet(r * cols + c, mask)) {
						matrixGUI[r][c].setBackground(BLOCKED);
						matrixGUI[r][c].setBorder(LOWERED);
					} else if (BitOps.isSet(r * cols + c, lightsOut.getState())) {
						matrixGUI[r][c].setBackground(ON);
						matrixGUI[r][c].setBorder(RAISED);
					} else {
						matrixGUI[r][c].setBackground(OFF);
						matrixGUI[r][c].setBorder(LOWERED);
					}
				}
			}
			getRootPane().invalidate();
			setTitle(FRAME_TITLE + " - moves: " + moves);
		}

		@Override
		public synchronized void actionPerformed(ActionEvent e) {
			if (e != null && e.getSource() == restart) {
				startNewGame();
			} else if (e != null && e.getSource() == hint) {
				restart.setEnabled(false);
				hint.setEnabled(false);
				auto.setEnabled(false);
				quit.setEnabled(false);
				showAll = false;
				new Thread(this).start();
			} else if (e != null && e.getSource() == auto) {
				restart.setEnabled(false);
				hint.setEnabled(false);
				auto.setEnabled(false);
				quit.setEnabled(false);
				showAll = true;
				new Thread(this).start();
			} else if (e != null && e.getSource() == quit) {
				System.exit(0);
			} else if (e != null) {
				for (int r = 0; r < rows; r++) {
					for (int c = 0; c < cols; c++) {
						if (e.getSource() == matrixGUI[r][c]) {
							moves++;
							lightsOut.toggle(r, c);
							paintMatrix();
							if (lightsOut.getState() == 0) {
								JOptionPane.showMessageDialog(this,
										"Congratulations!\nYou solved this puzzle.\nTry another one...", "GAME OVER",
										JOptionPane.INFORMATION_MESSAGE);
								startNewGame();
							}
							return;
						}
					}
				}
			}
		}

		@Override
		public void run() {
			if (lightsOut.getState() == 0) {
				JOptionPane.showMessageDialog(this, "Nothing to do here.\nTry another one...", "GAME OVER",
						JOptionPane.WARNING_MESSAGE);
				startNewGame();
				return;
			}
			IntegerSequenceMemory solution = lightsOut.solve();
			Integer[] moves = solution == null ? null : solution.recallAll();
			if (moves == null || moves.length == 0) {
				JOptionPane.showMessageDialog(this, "Sorry, there is no solution for this puzzle.\nTry another one...",
						"GAME OVER", JOptionPane.ERROR_MESSAGE);
				startNewGame();
				return;
			} else if (showAll) {
				for (int move : moves) {
					toggle(move);
				}
			} else {
				toggle(moves[0]);
			}
			restart.setEnabled(true);
			hint.setEnabled(true);
			auto.setEnabled(true);
			quit.setEnabled(true);
		}

		private void toggle(int move) {
			matrixGUI[move / cols][move % cols].setBackground(HIGHLIGHT);
			try {
				Thread.sleep(666);
			} catch (Throwable ignored) {
			}
			moves++;
			lightsOut.toggle(move / cols, move % cols);
			paintMatrix();
		}
	}
}