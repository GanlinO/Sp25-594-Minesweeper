import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * ActionListener for the main game frame's menu bar.
 * Handles menu selections like Exit, Play Different Game, and Display Rules.
 */
public class ViewMenuListener implements ActionListener {

    /** Reference to the main view */
    private ViewGUI myView;

    /**
     * Creates a new ViewMenuListener with a reference to the main view.
     *
     * @param view Reference to the main view
     */
    public ViewMenuListener(ViewGUI view) {
        myView = view;
    }

    /**
     * Processes menu item selections.
     * Routes menu commands to appropriate methods in the main view.
     *
     * @param e ActionEvent containing information about the selected menu item
     */
    public void actionPerformed(ActionEvent e) {
        if(myView != null) {
            String command = e.getActionCommand();
            if(command.equals("Exit")) {
                myView.exitGame();
            } else if(command.equals("Play Different Game")) {
                myView.playAgain();
            } else if(command.equals("New Game With Same Settings")) {
                myView.playGame();
            } else if(command.equals("Display Rules")) {
                myView.createPopUp(myView.getRules(), 600, 600, true);
            }
        }
    }
}