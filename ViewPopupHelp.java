import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;

/**
 * Creates popup windows to display help text or messages to the user.
 * Used for showing game rules and error messages.
 */
public class ViewPopupHelp extends JFrame {

    /** Font size for text in the popup */
    private final int fontSize = 18;

    /** Reference to the main view */
    private ViewGUI myView;

    /**
     * Creates a new popup window with the specified message and dimensions.
     *
     * @param view Reference to the main view
     * @param msg Message to display in the popup
     * @param frameWidth Width of the popup window
     * @param frameHeight Height of the popup window
     * @param timer Whether to pause the game timer while showing this popup
     */
    public ViewPopupHelp(ViewGUI view, String msg, int frameWidth, int frameHeight, boolean timer) {
        super("Help Window");
        myView = view;

        // Create text area to display the message
        JTextArea help = new JTextArea(msg);
        help.setBackground(getBackground());
        help.setFont(new Font("Arial", Font.BOLD, fontSize));
        help.setLineWrap(true);
        help.setWrapStyleWord(true); // Wrap at word, not characters
        help.setEditable(false);

        // Create OK button for user to close the popup
        JButton ok = new JButton("Okay");
        if(view != null) {
            ok.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    myView.endHelpPopup(timer);
                    dispose();
                }
            });
        }
        ok.setFont(new Font("Arial", Font.BOLD, fontSize));
        ok.setMnemonic(KeyEvent.VK_ENTER);
        ok.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Set up window properties
        setSize(frameWidth, frameHeight);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        add(help, BorderLayout.PAGE_START);
        add(ok, BorderLayout.PAGE_END);

        // Handle window close event
        if(myView != null) {
            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent windowEvent) {
                    myView.endHelpPopup(timer);
                    dispose();
                }
            });
        }

        setVisible(true);
    }
}