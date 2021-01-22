/**
 * CIS 120 Game HW
 * (c) University of Pennsylvania
 * @version 2.1, Apr 2017
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.LinkedList;

/**
 * GameCourt
 * 
 * This class holds the primary game logic for how different objects interact with one another. Take
 * time to understand how the timer interacts with the different methods and how it repaints the GUI
 * on every tick().
 */
@SuppressWarnings("serial")
public class GameCourt extends JPanel {

    // the state of the game logic

    private boolean playing = true; // whether the game is running 

    private int numFlaggedMines = 40; // number of mines remaining
    private JLabel status; // Current status text, i.e. "Running..."
    private JLabel timeRunning; // Current game time text
    private JLabel flagsNeeded; // Current number of flags needed to win

    // Game constants
    public static final int COURT_WIDTH = 640;
    public static final int COURT_HEIGHT = 640;

    // Update interval for timer, in milliseconds
    private double time;
    public static final int INTERVAL = 1000;

    // Storing cells
    private Cell[][] cells;
    private LinkedList<Cell> uncoveredCells;
    private LinkedList<Cell> mineCells;

    MouseAdapter mouseAdapter = new MouseAdapter() {
        public void mousePressed(MouseEvent e) {

            // get mouse position
            int x = ((int) (e.getPoint()).getX()) / 40;
            int y = ((int) (e.getPoint()).getY()) / 40;

            //  left click uncovers mine, right click marks mine
            if (SwingUtilities.isLeftMouseButton(e)) {
                uncover(x, y);
            } else {
                flag(x, y);
            }
        }
    };

    public GameCourt(JLabel status, JLabel timeRunning, JLabel flagsNeeded) {
        // creates border around the court area, JComponent method
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        Timer timer = new Timer(INTERVAL, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tick();
            }
        });

        timer.start();

        addMouseListener(mouseAdapter);

        // Enable mouse focus on the court area.
        setFocusable(true);

        this.status = status;
        this.timeRunning = timeRunning;
        this.flagsNeeded = flagsNeeded;

        time = 0;

        cells = new Cell[16][16];
        uncoveredCells = new LinkedList<Cell>();
        mineCells = new LinkedList<Cell>();
    }

    // generate random mines
    public void generateMines() {
        int mineCount = 40;

        while (mineCount > 0) {
            int rand = (int) (Math.random() * 256);
            int x = rand / 16;
            int y = rand % 16;
            Cell curr = cells[x][y];

            // only set as mine if not already a mine
            if (!mineCells.contains(curr)) {
                mineCells.add(curr);
                mineCount--;
            }
        }
    }

    // find number of neighboring mines
    public void findNumNeighbors() {
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                // check if surrounding cells are mines
                int count = 0;

                int iLeft = i - 1;
                int iRight = i + 1;
                int jUp = j - 1;
                int jDown = j + 1;

                if (i <= 0) {
                    iLeft = 0;
                }
                if (i >= 15) { 
                    iRight = 15; 
                }
                if (j <= 0) {
                    jUp = 0;
                }
                if (j >= 15) {
                    jDown = 15;
                }

                for (int x = iLeft; x <= iRight; x++) {
                    for (int y = jUp; y <= jDown; y++) {
                        if (mineCells.contains(cells[x][y])) {
                            count++;
                        }
                    }
                }

                // don't count the cell itself as a mine (only looking for neighbors)
                if (mineCells.contains(cells[i][j]) && count > 0) {
                    count -= 1;
                }

                cells[i][j].setNumNeighbors(count);
            }
        }
    }

    // flagging a mine
    public void flag(int x, int y) {
        if (playing) {
            Cell curr = cells[x][y];

            // cell can't be flagged if it's already uncovered
            if (!curr.isCovered()) {
                return;
            } 

            // flag or unflag cell
            curr.setFlagged(!curr.isFlagged()); 

            // update "flags needed: " text
            if (curr.isFlagged()) {
                numFlaggedMines -= 1;
                flagsNeeded.setText("Flags needed: " + numFlaggedMines);
            } else {
                numFlaggedMines += 1;
                flagsNeeded.setText("Flags needed: " + numFlaggedMines);
            }

            repaint();
        }
    }

    // uncovering a cell
    public void uncover(int x, int y) {
        if (playing) {
            Cell curr = cells[x][y];

            //nothing happens if cell is flagged as mine
            if (curr.isFlagged()) { 
                return; 
            } else if (curr.getNumNeighbors() > 0 || mineCells.contains(curr)) {
                uncoveredCells.add(curr);
                curr.uncover();
            } else { 
                uncoveredCells.add(curr);
                curr.uncover();

                //flood fill
                if (x > 0 && y > 0 && !uncoveredCells.contains(cells[x - 1][y - 1])) { 
                    uncover(x - 1, y - 1); 
                }
                if (x > 0 && !uncoveredCells.contains(cells[x - 1][y])) { 
                    uncover(x - 1, y); 
                }
                if (x > 0 && y < 15 && !uncoveredCells.contains(cells[x - 1][y + 1])) { 
                    uncover(x - 1, y + 1); 
                }
                if (y > 0 && !uncoveredCells.contains(cells[x][y - 1])) { 
                    uncover(x, y - 1); 
                }
                if (y < 15 && !uncoveredCells.contains(cells[x][y + 1])) { 
                    uncover(x, y + 1); 
                }
                if (x < 15 && y > 0 && !uncoveredCells.contains(cells[x + 1][y - 1])) { 
                    uncover(x + 1, y - 1); 
                }
                if (x < 15 && !uncoveredCells.contains(cells[x + 1][y])) { 
                    uncover(x + 1, y); 
                }
                if (x < 15 && y < 15 && !uncoveredCells.contains(cells[x + 1][y + 1])) { 
                    uncover(x + 1, y + 1); 
                }
            }

            // reveal cell
            curr.uncover(); 

            repaint();
        }
    }

    /**
     * (Re-)set the game to its initial state.
     */
    public void reset() {

        playing = true;

        status.setText("Running...");
        timeRunning.setText("Timer: 0");
        flagsNeeded.setText("Flags needed: 40");
        time = 0;
        numFlaggedMines = 40;


        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                cells[i][j] = new Cell();
            }
        }

        uncoveredCells = new LinkedList<Cell>();
        mineCells = new LinkedList<Cell>();

        generateMines();
        findNumNeighbors();

        // Make sure that this component has the keyboard focus
        requestFocusInWindow();

        repaint();
    }

    /**
     * This method is called every time the timer defined in the constructor triggers.
     */
    void tick() {
        if (playing) {

            time += 1;
            timeRunning.setText("Timer: " + time);

            // check for the game end conditions

            // check if any uncovered cells are mines (game lost)
            for (Cell c : uncoveredCells) {
                if (mineCells.contains(c)) {
                    repaint();
                    playing = false;
                    status.setText("You lost :(");
                }
            }

            // check if game won
            if (isGameWon()) {
                repaint();
                playing = false;
                status.setText("You won :)");
            }

            // update the display
            if (playing) {
                repaint();
            }
        }
    }

    // check if game has been won
    public boolean isGameWon() {
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                if (cells[i][j].isCovered() && !mineCells.contains(cells[i][j])) {
                    // if there are still covered cells that are not mines, game continues
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // cells
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                Cell curr = cells[i][j];
                int posX = i * 40;
                int posY = j * 40;

                // flagged cells
                if (curr.isFlagged()) {
                    g.setColor(Color.YELLOW);
                    g.fillRect(posX, posY, 40, 40);
                } else if (!playing && mineCells.contains(curr)) {
                    g.setColor(Color.RED);
                    g.fillRect(posX, posY, 40, 40);
                } else if (curr.isCovered()) {
                    g.setColor(Color.DARK_GRAY);
                    g.fillRect(posX, posY, 40, 40);
                } else {
                    if (mineCells.contains(curr)) {
                        g.setColor(Color.RED);
                        g.fillRect(posX, posY, 40, 40);
                    } else {
                        g.setColor(Color.WHITE);
                        g.fillRect(posX, posY, 40, 40);
                        g.setColor(Color.BLACK);
                        if (curr.getNumNeighbors() != 0) {
                            g.drawString("" + curr.getNumNeighbors(), posX + 17, posY + 24);
                        }
                    }
                }
            }
        }
        // grid border
        g.setColor(Color.WHITE);
        for (int i = 0; i < 16; i++) {
            int pos = i * 40;
            g.drawLine(pos, 0, pos, 640);
            g.drawLine(0, pos, 640, pos);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(COURT_WIDTH, COURT_HEIGHT);
    }
}