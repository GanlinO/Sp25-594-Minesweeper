import java.awt.event.*;

/**
 * ActionListener for the hint button.
 * Activates the hint feature to help the player by suggesting a mine location.
 */
public class ViewHintListener implements ActionListener {

    /** Reference to the main view */
    private final ViewGUI view;

    /**
     * Creates a new ViewHintListener with a reference to the main view.
     *
     * @param v Reference to the main view
     */
    ViewHintListener(ViewGUI v) {
        this.view = v;
    }

    /**
     * Processes hint button click events.
     * Activates the hint feature in the main view.
     *
     * @param e ActionEvent containing information about the button click
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        view.hint();
    }
}