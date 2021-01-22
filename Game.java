/**
 * CIS 120 Game HW
 * (c) University of Pennsylvania
 * @version 2.1, Apr 2017
 */

// imports necessary libraries for Java swing
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Game Main class that specifies the frame and widgets of the GUI
 */
public class Game implements Runnable {
    public void run() {

        // Top-level frame in which game components live
        final JFrame frame = new JFrame("MINESWEEPER");
        frame.setLocation(50, 100);

        // Status and rules panel
        final JPanel lower_panel = new JPanel();
        lower_panel.setLayout(new BorderLayout());
        frame.add(lower_panel, BorderLayout.SOUTH);

        // Status label
        final JLabel status = new JLabel("Running...", SwingConstants.CENTER);
        status.setPreferredSize(new Dimension(640,25));
        lower_panel.add(status,  BorderLayout.NORTH);

        // Rules text box
        final String rules = "Some squares contain bombs, others do not. \n"
                + "If you click a square containing a bomb, you lose. \n"
                + "If you manage to click on all squares without clicking on any bombs, you win. \n"
                + "Clicking a square that does not contain a bomb reveals the number of "
                + "neighboring \n "
                + "squares containing bombs. Left click a square to open it, "
                + "right click to mark a "
                + "square as a bomb.";

        JTextArea rulesBox =  new JTextArea();
        rulesBox.setText(rules);
        rulesBox.setPreferredSize(new Dimension(640,110));
        rulesBox.setEditable(false);
        lower_panel.add(rulesBox, BorderLayout.SOUTH);

        // Mines left, timer and reset button panel
        final JPanel upper_panel = new JPanel();
        upper_panel.setLayout(new BorderLayout());
        frame.add(upper_panel, BorderLayout.NORTH);

        // Mines left and timer panel 
        final JPanel status_panel = new JPanel();
        status_panel.setLayout(new BorderLayout(20,5));
        upper_panel.add(status_panel, BorderLayout.WEST);

        // Mines left label
        final JLabel flagsNeeded = new JLabel("Flags needed: 40");
        status_panel.add(flagsNeeded,  BorderLayout.WEST);

        // Timer label
        final JLabel timeRunning = new JLabel("Timer: 0");
        status_panel.add(timeRunning,  BorderLayout.EAST);

        // Main playing area
        final GameCourt court = new GameCourt(status, flagsNeeded, timeRunning);
        frame.add(court, BorderLayout.CENTER);

        // Note here that when we add an action listener to the reset button, we define it as an
        // anonymous inner class that is an instance of ActionListener with its actionPerformed()
        // method overridden. When the button is pressed, actionPerformed() will be called.
        final JButton reset = new JButton("Reset");
        reset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                court.reset();
            }
        });
        upper_panel.add(reset, BorderLayout.EAST);


        // Put the frame on the screen
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Start game
        court.reset();
    }

    /**
     * Main method run to start and run the game. Initializes the GUI elements specified in Game and
     * runs it. IMPORTANT: Do NOT delete! You MUST include this in your final submission.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Game());
    }
}