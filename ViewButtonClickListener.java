import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * ActionListener for button clicks in the view.
 * Handles user interactions with buttons such as Play, Exit, and Play Again.
 */
public class ViewButtonClickListener implements ActionListener {

    /** Reference to the main view */
    ViewGUI myView;

    /**
     * Creates a new ViewButtonClickListener with a reference to the main view.
     *
     * @param view Reference to the main view
     */
    public ViewButtonClickListener(ViewGUI view) {
        myView = view;
    }

    /**
     * Processes button click events.
     * Routes button commands to appropriate methods in the main view.
     *
     * @param e ActionEvent containing information about the clicked button
     */
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if(myView != null) {
            if(command.equals("Play")) {
                myView.playGame();
            } else if(command.equals("Exit")) {
                myView.exitGame();
            } else if(command.equals("Play Again")) {
                myView.playAgain();
            } else if(command.equals("Exit Game")) {
                myView.exitGame();
            }
        }
    }
}
