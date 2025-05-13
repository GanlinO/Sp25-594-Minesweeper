import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

/**
 * MouseAdapter for handling mouse clicks on game tiles.
 * Differentiates between left clicks (reveal tile) and right clicks (place flag).
 */
public class ViewMouseListener extends MouseAdapter {

    /** Reference to the main view */
    private ViewGUI myView;

    /**
     * Creates a new ViewMouseListener with a reference to the main view.
     *
     * @param view Reference to the main view
     */
    public ViewMouseListener(ViewGUI view) {
        myView = view;
    }

    /**
     * Handles mouse click events on game tiles.
     * Left-click reveals a tile, right-click places or removes a flag.
     *
     * @param e MouseEvent containing information about the click
     */
    public void mouseClicked(MouseEvent e) {
        if(e.getButton() != MouseEvent.NOBUTTON && myView != null) {
            try {
                JButton button = (JButton)e.getSource();
                if(SwingUtilities.isRightMouseButton(e))
                    myView.placeFlag(button); // Right-click to place flag
                else
                    myView.tilePressed(button.getActionCommand()); // Left-click to reveal tile
            } catch(Exception ex) {
                ex.printStackTrace(System.out);
            }
        }
    }
}
