import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * Frame displayed at the end of a game.
 * Shows game results, statistics, best times, and options to play again or exit.
 */
public class ViewEndFrame extends JFrame {

    /** Font size for all text elements */
    private final int fontSize = 18;

    /** Reference to the main view */
    private ViewGUI myView;

    /**
     * Creates a new end frame with game results and statistics.
     *
     * @param view Reference to the main view
     * @param won True if the player won, false if lost
     * @param timeTaken Time taken to complete the game in seconds
     * @param bestTimes String containing best times for each difficulty
     * @param gamesPlayed Total number of games played
     * @param gamesWon Total number of games won
     */
    public ViewEndFrame(ViewGUI view, boolean won, long timeTaken, String bestTimes, long gamesPlayed, long gamesWon) {
        super();
        myView = view;

        setSize(700, 350);
        setLayout(new GridLayout(1, 2));
        setLocationRelativeTo(null);

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        addLabel("\n", left);
        addLabel("\n", right);

        // Set title and message based on game outcome
        if(won) {
            setTitle("Congrats, you won!");
            addLabel("Congrats, you won!", left);
        } else {
            setTitle("Sorry, try again.");
            addLabel("Sorry, you lost. Try again.", left);
        }

        addLabel("\n", left);
        addLabel("Time taken: " + timeTaken + " seconds", left);
        addLabel("\n", left);
        addPlayAgainExitButtons(left);

        // Statistics panel
        addLabel("Total games played: " + gamesPlayed, right);
        addLabel("\n", right);
        addLabel("Total games won: " + gamesWon, right);
        addLabel("\n", right);

        // Calculate win percentage
        if(gamesPlayed > 0) {
            double fraction = (double) gamesWon / gamesPlayed;
            int percent = (int) (fraction * 100);
            addLabel("Percent games won: " + percent + "%", right);
            addLabel("\n", right);
        }

        // Best times display
        if(bestTimes != null) {
            JTextArea times = new JTextArea(bestTimes);
            times.setBackground(getBackground());
            times.setFont(new Font("Arial", Font.BOLD, fontSize));
            times.setLineWrap(true);
            times.setWrapStyleWord(true); // Wrap at word, not characters
            times.setEditable(false);

            right.add(times);
            addLabel("\n", right);
        }

        add(left);
        add(right);

        // Handle window close
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });

        setVisible(true);
    }

    /**
     * Adds a label with the specified text to a panel.
     *
     * @param msg Text for the label
     * @param panel Panel to add the label to
     */
    private void addLabel(String msg, JPanel panel) {
        if(panel != null && msg != null) {
            JLabel label = new JLabel(msg);
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            label.setFont(new Font("Arial", Font.BOLD, fontSize));
            panel.add(label);
        }
    }

    /**
     * Adds Play Again and Exit buttons to the specified panel.
     *
     * @param buttonpanel Panel to add the buttons to
     */
    private void addPlayAgainExitButtons(JPanel buttonpanel) {
        if(buttonpanel != null) {
            JPanel subpanel = new JPanel();
            JButton playbutton = createButton("Play Again", "Play Again");
            if(playbutton != null) {
                playbutton.setMnemonic(KeyEvent.VK_ENTER);
                JButton exitbutton = createButton("Exit", "Exit Game");
                subpanel.add(playbutton);
                if(exitbutton != null)
                    subpanel.add(exitbutton);
            }
            buttonpanel.add(subpanel);
        }
    }

    /**
     * Creates a button with the specified text and action command.
     *
     * @param buttontext Text to display on the button
     * @param actiontext Action command for the button
     * @return Configured JButton
     */
    private JButton createButton(String buttontext, String actiontext) {
        if(buttontext != null && actiontext != null) {
            JButton thisbutton = new JButton(buttontext);
            thisbutton.setActionCommand(actiontext);
            if(myView != null)
                thisbutton.addActionListener(new ViewButtonClickListener(myView));
            thisbutton.setFont(new Font("Arial", Font.BOLD, fontSize));
            return thisbutton;
        }
        return null;
    }
}