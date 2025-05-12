import java.awt.event.*;
class ViewHintListener implements ActionListener {
    private final ViewGUI view;
    ViewHintListener(ViewGUI v) { this.view = v; }
    @Override public void actionPerformed(ActionEvent e) { view.hint(); }
}
