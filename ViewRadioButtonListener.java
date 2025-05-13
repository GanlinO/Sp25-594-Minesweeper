import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * ActionListener for radio button changes.
 * Handles selection of difficulty levels and extra lives options.
 */
public class ViewRadioButtonListener implements ActionListener {

    /** Reference to the main view */
    private ViewGUI myView;

    /**
     * Creates a new ViewRadioButtonListener with a reference to the main view.
     *
     * @param v Reference to the main view
     */
    public ViewRadioButtonListener(ViewGUI v) {
        myView = v;
    }

    /**
     * Processes radio button selection events.
     * Routes radio button commands to appropriate methods in the main view.
     *
     * @param e ActionEvent containing information about the selected radio button
     */
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if(myView != null) {
            // Extra lives selection
            if(command.equals("1extra") || command.equals("2extra") || command.equals("3extra"))
                myView.setExtraLives(Integer.parseInt(command.substring(0, 1)));
                // Difficulty selection
            else
                myView.setDifficulty(command);
        }
    }
}