import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

/**
 * Initial frame displayed when starting the game.
 * Allows the player to select difficulty level, customize game settings,
 * and toggle options like extra lives and logical mode.
 */
public class ViewStartFrame extends JFrame{

    private final int fontSize = 18;
    private final String beginnerDifficulty = "Beginner: 10 mines on a 9 x 9 tile grid";
    private final String interDifficulty = "Intermediate: 40 mines on a 16 x 16 tile grid";
    private final String expertDifficulty = "Expert: 99 mines on a 16 x 30 tile grid";
    private final String customDifficulty = "Custom:";

    private final SpinnerModel rowmodel = new SpinnerNumberModel(9,2,30,1); //initial,min,max,step
    private final SpinnerModel colmodel = new SpinnerNumberModel(9,2,30,1);
    private final SpinnerModel minemodel = new SpinnerNumberModel(10,1,150,1);
    private JSpinner rowspinner;
    private JSpinner colspinner;
    private JSpinner minespinner;
    private JLabel rowlabel; //describing the spinners for the user
    private JLabel collabel;
    private JLabel minelabel;

    private final int frameWidth = 500;
    private final int frameHeight = 500;
    private ViewGUI myView;

    private JCheckBox logicalMode;

    private JRadioButton one; //1 extra life
    private JRadioButton two; //2 extra lives
    private JRadioButton three; //3 extra lives

    /**
     * Creates a new start frame with the specified difficulty options.
     *
     * @param view Reference to the main view
     * @param difficulties List of available difficulty options
     */
    public ViewStartFrame(ViewGUI view, ArrayList<String> difficulties)
    {
        super("Welcome to Minesweeper!");

        myView = view;

        setSize(frameWidth,frameHeight);
        setLayout(new GridLayout(4,1));
        setLocationRelativeTo(null);

        addDifficulties(difficulties); // Different difficulties user can select from
        addCheckBoxes(); // Additional options (logical mode, extra lives) for user to select
        addPlayExitButtons(); // Buttons user can press

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent){
                System.exit(0);
            }
        });

        setVisible(true);
    }

    /**
     * Creates and adds difficulty radio buttons to the frame.
     *
     * @param difficulties List of available difficulty options
     */
    private void addDifficulties(ArrayList<String> difficulties) {
        ButtonGroup bgroup = new ButtonGroup();

        // Beginner difficulty (default)
        JRadioButton beginner = createRadioButton(beginnerDifficulty, difficulties.get(0), true, KeyEvent.VK_B, true);
        bgroup.add(beginner);

        // Intermediate difficulty
        JRadioButton intermed = createRadioButton(interDifficulty, difficulties.get(1), true, KeyEvent.VK_I, false);
        bgroup.add(intermed);

        // Expert difficulty
        JRadioButton expert = createRadioButton(expertDifficulty, difficulties.get(2), true, KeyEvent.VK_E, false);
        bgroup.add(expert);

        // Custom difficulty
        JRadioButton custom = createRadioButton(customDifficulty, difficulties.get(3), true, KeyEvent.VK_C, false);
        bgroup.add(custom);

        // Custom difficulty spinners
        JPanel spinners = createSpinners();

        // Add all radio buttons to a panel
        JPanel radiopanel = new JPanel();
        radiopanel.setLayout(new BoxLayout(radiopanel, BoxLayout.Y_AXIS));
        radiopanel.add(beginner);
        radiopanel.add(intermed);
        radiopanel.add(expert);
        radiopanel.add(custom);

        add(radiopanel); // Add to this JFrame (start frame)
        add(spinners);
    }

    /**
     * Creates spinners for the custom difficulty option.
     *
     * @return Panel containing row, column, and mine count spinners
     */
    private JPanel createSpinners()
    {
        rowspinner = new JSpinner(rowmodel);
        colspinner = new JSpinner(colmodel);
        minespinner = new JSpinner(minemodel);

        rowspinner.setFont(new Font("Arial",Font.BOLD,fontSize));
        colspinner.setFont(new Font("Arial",Font.BOLD,fontSize));
        minespinner.setFont(new Font("Arial",Font.BOLD,fontSize));

        if(myView!=null)
        {
            rowspinner.addChangeListener(new ViewSpinnerListener(myView));
            colspinner.addChangeListener(new ViewSpinnerListener(myView));
            minespinner.addChangeListener(new ViewSpinnerListener(myView));
        }

        // Disabled until custom difficulty is selected
        rowspinner.setEnabled(false);
        colspinner.setEnabled(false);
        minespinner.setEnabled(false);

        rowlabel = new JLabel("Rows: ");
        rowlabel.setFont(new Font("Arial",Font.BOLD,fontSize));
        collabel = new JLabel("Columns: ");
        collabel.setFont(new Font("Arial",Font.BOLD,fontSize));
        minelabel = new JLabel("Mines: ");
        minelabel.setFont(new Font("Arial",Font.BOLD,fontSize));

        rowlabel.setEnabled(false);
        collabel.setEnabled(false);
        minelabel.setEnabled(false);

        JPanel panel = new JPanel();
        panel.add(rowlabel);
        panel.add(rowspinner);
        panel.add(collabel);
        panel.add(colspinner);
        panel.add(minelabel);
        panel.add(minespinner);
        return panel;
    }

    /**
     * Creates a radio button with the specified parameters.
     *
     * @param rbtext Button text
     * @param actioncommand Command to execute when button is clicked
     * @param useMnemonic Whether to use keyboard shortcut
     * @param mnemonic Keyboard shortcut key code
     * @param selected Whether button should be initially selected
     * @return Configured JRadioButton
     */
    private JRadioButton createRadioButton(String rbtext, String actioncommand,boolean useMnemonic, int mnemonic,boolean selected)
    {
        JRadioButton rbutton;
        if(rbtext==null)
            rbutton = new JRadioButton(" ");
        else
            rbutton = new JRadioButton(rbtext);
        if(actioncommand!=null)
            rbutton.setActionCommand(actioncommand);
        if(useMnemonic)
            rbutton.setMnemonic(mnemonic);
        rbutton.setSelected(selected);
        rbutton.setAlignmentX(Component.LEFT_ALIGNMENT);
        rbutton.setFont(new Font("Arial",Font.BOLD,fontSize));
        if(myView!=null)
            rbutton.addActionListener(new ViewRadioButtonListener(myView));
        return rbutton;
    }

    /**
     * Enables or disables the custom spinner controls.
     *
     * @param enable True to enable spinners, false to disable
     */
    public void enableCustomSpinners(boolean enable)
    {
        if(rowspinner!=null&&colspinner!=null&&minespinner!=null &&
                rowlabel!=null&&collabel!=null&&minelabel!=null)
        {
            if(rowspinner.isEnabled()!=enable)
            {
                rowspinner.setEnabled(enable);
                colspinner.setEnabled(enable);
                minespinner.setEnabled(enable);
                rowlabel.setEnabled(enable);
                collabel.setEnabled(enable);
                minelabel.setEnabled(enable);
            }
        }
    }

    /**
     * Gets the value from a spinner with an identifier based on which model it uses.
     *
     * @param spinner The spinner to get value from
     * @return String containing the spinner type and value
     */
    public String getCustomInfo(JSpinner spinner)
    {
        if(spinner!=null)
        {
            try
            {
                SpinnerModel mymodel = spinner.getModel();
                if(mymodel.equals(rowmodel))
                {
                    int val = (int) spinner.getValue();
                    if(val<2)
                        spinner.setValue(2);
                    else if(val>30)
                        spinner.setValue(30);
                    return "row"+spinner.getValue();
                }
                else if(mymodel.equals(colmodel))
                {
                    int val = (int) spinner.getValue();
                    if(val<2)
                        spinner.setValue(2);
                    else if(val>30)
                        spinner.setValue(30);
                    return "col"+spinner.getValue();
                }
                else //minemodel
                {
                    int val = (int) spinner.getValue();
                    if(val<1)
                        spinner.setValue(1);
                    else if(val>150)
                        spinner.setValue(150);
                    return "min"+spinner.getValue();
                }
            }catch(Exception ex)
            {
                return "ERROR!";
            }
        }
        return "ERROR!";
    }

    /**
     * Adds checkboxes and accompanying radio buttons to the frame.
     * Includes logical mode and extra lives options.
     */
    private void addCheckBoxes() {
        // Logical mode checkbox
        logicalMode = createCheckBox("Logical Mode (no random guessing)",
                KeyEvent.VK_M,   // mnemonic: Alt+M
                false);          // not selected by default
        if(myView != null)
            logicalMode.addItemListener(new ViewCheckBoxListener(myView));

        // Extra lives checkbox
        JCheckBox extralives = createCheckBox("Extra Lives", KeyEvent.VK_L, false);
        if(myView != null)
            extralives.addItemListener(new ViewCheckBoxListener(myView));

        // Extra lives radio buttons (disabled by default)
        ButtonGroup bgroup = new ButtonGroup();
        one = createRadioButton("1", "1extra", false, 0, false);
        two = createRadioButton("2", "2extra", false, 0, false);
        three = createRadioButton("3", "3extra", false, 0, false);
        one.setEnabled(false);
        two.setEnabled(false);
        three.setEnabled(false);
        bgroup.add(one);
        bgroup.add(two);
        bgroup.add(three);

        JPanel checkboxpanel = new JPanel();
        checkboxpanel.setLayout(new BoxLayout(checkboxpanel, BoxLayout.Y_AXIS));
        checkboxpanel.add(logicalMode);
        checkboxpanel.add(extralives);
        checkboxpanel.add(one);
        checkboxpanel.add(two);
        checkboxpanel.add(three);

        add(checkboxpanel); // Add to the start frame
    }

    /**
     * Creates a checkbox with the specified parameters.
     *
     * @param boxtext Checkbox text
     * @param mnemonic Keyboard shortcut key code
     * @param selected Whether checkbox should be initially selected
     * @return Configured JCheckBox
     */
    private JCheckBox createCheckBox(String boxtext, int mnemonic, boolean selected)
    {
        JCheckBox thisbox;
        if(boxtext!=null)
            thisbox = new JCheckBox(boxtext);
        else
            thisbox = new JCheckBox(" ");
        thisbox.setMnemonic(mnemonic);
        thisbox.setSelected(selected);
        thisbox.setFont(new Font("Arial",Font.BOLD,fontSize));
        thisbox.setAlignmentX(Component.LEFT_ALIGNMENT);
        return thisbox;
    }

    /**
     * Adds Play and Exit buttons to the frame.
     */
    private void addPlayExitButtons()
    {
        JButton playbutton = createButton("Play");
        playbutton.setMnemonic(KeyEvent.VK_ENTER);
        JButton exitbutton = createButton("Exit");

        JPanel buttonpanel = new JPanel();
        buttonpanel.add(playbutton);
        buttonpanel.add(exitbutton);

        add(buttonpanel);

    }

    /**
     * Creates a button with the specified text.
     *
     * @param buttontext Button text
     * @return Configured JButton
     */
    private JButton createButton(String buttontext)
    {
        JButton thisbutton;
        if(buttontext!=null)
            thisbutton = new JButton(buttontext);
        else
            thisbutton = new JButton(" ");
        if(myView!=null)
            thisbutton.addActionListener(new ViewButtonClickListener(myView));
        thisbutton.setFont(new Font("Arial",Font.BOLD,fontSize));
        return thisbutton;
    }

    /**
     * Toggles the extra lives radio buttons.
     * If they are disabled, enables them and selects "1".
     * If they are enabled, disables them.
     */
    public void extraLives()
    {
        if(myView!=null)
        {
            boolean enable = false;
            if(!one.isEnabled())
            {
                enable = true;
                myView.setExtraLives(1);
            }
            else
            {
                myView.setExtraLives(-1);
            }
            one.setSelected(enable);
            one.setEnabled(enable);
            two.setEnabled(enable);
            three.setEnabled(enable);
        }
    }

}

