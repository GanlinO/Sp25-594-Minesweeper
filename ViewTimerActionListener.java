import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * ActionListener for the timer object in the game frame.
 * Increments the game time every second.
 */
public class ViewTimerActionListener implements ActionListener {

    /** Reference to the game frame */
    private ViewGameTilesFrame myView;

    /**
     * Creates a new ViewTimerActionListener with a reference to the game frame.
     *
     * @param view Reference to the game frame
     */
    public ViewTimerActionListener(ViewGameTilesFrame view) {
        myView = view;
    }

    /**
     * Processes timer events.
     * Increments the game time by one second.
     *
     * @param e ActionEvent containing information about the timer event
     */
    public void actionPerformed(ActionEvent e) {
        if(myView != null)
            myView.incrementTime();
    }
}