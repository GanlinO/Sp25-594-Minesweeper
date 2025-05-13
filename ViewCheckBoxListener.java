import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;

/**
 * ItemListener for checkbox state changes.
 * Handles toggling of options like extra lives and logical mode.
 */
public class ViewCheckBoxListener implements ItemListener {

    /** Reference to the main view */
    private ViewGUI myView;

    /**
     * Creates a new ViewCheckBoxListener with a reference to the main view.
     *
     * @param view Reference to the main view
     */
    public ViewCheckBoxListener(ViewGUI view) {
        myView = view;
    }

    /**
     * Processes checkbox state change events.
     * Routes checkbox commands to appropriate methods in the main view.
     *
     * @param e ItemEvent containing information about the checkbox state change
     */
    public void itemStateChanged(ItemEvent e) {
        JCheckBox box = (JCheckBox) e.getSource();

        if(myView != null) {
            if(box.getActionCommand().equals("Extra Lives")) {
                myView.showExtraLives();
            }
            // Logical mode toggle
            else if(box.getActionCommand().startsWith("Logical Mode")) {
                myView.setLogicalMode(box.isSelected());
            }
        }
    }
}