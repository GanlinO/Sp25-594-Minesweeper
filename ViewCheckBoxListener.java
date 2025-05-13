import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
//listener for the different check boxes (only extra lives in start frame right now)
public class ViewCheckBoxListener implements ItemListener{
    private ViewGUI myView;

    public ViewCheckBoxListener(ViewGUI view){
        myView = view;
    }
    //listen for check box change
    public void itemStateChanged(ItemEvent e) {
        JCheckBox box = (JCheckBox) e.getSource();

        if (myView != null) {
            if (box.getActionCommand().equals("Extra Lives")) {
                myView.showExtraLives();
            }
            /* ── NEW: Logical-mode toggle ─────────────────────────── */
            else if (box.getActionCommand().startsWith("Logical Mode")) {
                myView.setLogicalMode(box.isSelected());
            }
        }
    }
}