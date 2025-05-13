import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * ChangeListener for spinner value changes in the custom difficulty settings.
 * Updates the model with new row, column, and mine count values.
 */
public class ViewSpinnerListener implements ChangeListener {

    /** Reference to the main view */
    private ViewGUI myView;

    /**
     * Creates a new ViewSpinnerListener with a reference to the main view.
     *
     * @param view Reference to the main view
     */
    public ViewSpinnerListener(ViewGUI view) {
        myView = view;
    }

    /**
     * Processes spinner value change events.
     * Notifies the main view of the new spinner value.
     *
     * @param e ChangeEvent containing information about the spinner value change
     */
    public void stateChanged(ChangeEvent e) {
        if(myView != null) {
            // Only used by spinners
            JSpinner spinner = (JSpinner) e.getSource();
            myView.setCustom(spinner);
        }
    }
}