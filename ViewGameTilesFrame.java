import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.SwingUtilities;

/**
 * Main frame that displays the Minesweeper game grid.
 * Contains the mine grid as buttons, timer, mine counter, extra lives indicator,
 * hint button, and AI solver button.
 */
public class ViewGameTilesFrame extends JFrame{

    /** Font size for labels and buttons */
    private final int fontSize = 18;

    /** Height for the top panel containing game information */
    private final int topHeight = 100;

    /** Button for AI solver functionality */
    private JButton aiBtn;

    /** Button for hint functionality */
    private JButton hintBtn;

    /** Panel containing game information (mines left, time, lives) */
    private JPanel hudPanel;

    /** Reference to the main view */
    private ViewGUI view;

    /** 2D array of buttons representing the game grid */
    private JButton[][] buttons;

    /** Number of rows in the grid */
    private int numrows;

    /** Number of columns in the grid */
    private int numcols;

    /** Width of the game window */
    private int width;

    /** Height of the game window */
    private int cheight;

    /** Label displaying number of mines left */
    private JLabel minesLeft;

    /** Label displaying elapsed time */
    private JLabel time;

    /** Timer to track elapsed game time */
    private Timer timer;

    /** Label displaying number of extra lives remaining */
    private JLabel extralives;

    /**
     * Creates a new game frame with the specified grid and number of mines.
     *
     * @param myView Reference to the main view
     * @param grid 2D array of strings representing the game grid
     * @param mines Number of mines in the game
     */
    public ViewGameTilesFrame(ViewGUI myView,String [][] grid, int mines)
    {
        super("Minesweeper");
        view = myView;
        if(grid!=null)
        {
            numrows = grid.length;
            numcols = grid[0].length;
        }
        else
        {
            numrows = 0;
            numcols = 0;
        }

        // Set window dimensions based on grid size
        width = numcols * 60; // Arbitrary width of 60 for each tile
        cheight = numrows * 50; // Arbitrary height of 50 for each tile
        if(width < 500)
            width = 500; // Minimum to fit all of top panel

        setSize(width,cheight+topHeight);
        setLayout(new BorderLayout(10,10));
        setLocationRelativeTo(null);

        // Create menu bar with "Game", "Help"
        JMenuBar menubar = new JMenuBar();
        setJMenuBar(createMenu(menubar));

        // Create top panel with mines left, time, extra lives
        hudPanel = topPanel(mines);
        add(hudPanel, BorderLayout.PAGE_START);

        // Create center panel with game grid buttons
        if(grid != null)
            add(centerPanel(grid), BorderLayout.CENTER);

        // Handle window close event
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });

        setVisible(true);

        // Add hint button
        hintBtn = new JButton("Hint");
        hintBtn.setForeground(Color.BLUE);
        hintBtn.addActionListener(new ViewHintListener(view));
        hudPanel.add(hintBtn);

        // Add AI solver button
        aiBtn = new JButton("AI solver");
        aiBtn.setForeground(Color.GREEN);
        aiBtn.addActionListener(e -> {
            view.autoSolve();
        });

        hudPanel.add(aiBtn);

        // Only show AI solver button in logical mode
        SwingUtilities.invokeLater(() ->
                aiBtn.setVisible(view.isLogicalMode()));

        // Start the timer
        timer.start();
    }

    /**
     * Highlights a hint by showing a blue flag on the specified tile.
     *
     * @param row Row index of the hint tile
     * @param col Column index of the hint tile
     */
    public void showHint(int row, int col) {
        JButton b = buttons[row][col];
        b.setText("F");
        b.setForeground(Color.BLUE);
    }

    /**
     * Marks a mine with a green flag.
     * Used by the AI solver to indicate detected mines.
     *
     * @param r Row index of the mine
     * @param c Column index of the mine
     */
    public void markMineGreen(int r, int c) {
        JButton b = buttons[r][c];
        b.setText("F");
        b.setForeground(Color.GREEN);
    }

    /**
     * Creates and returns the menu bar for the game.
     *
     * @param menubar JMenuBar to populate
     * @return Populated JMenuBar
     */
    private JMenuBar createMenu(JMenuBar menubar)
    {
        JMenu gameSettings = new JMenu("Game");
        gameSettings.setMnemonic(KeyEvent.VK_G);
        gameSettings.setFont(new Font("Arial",Font.PLAIN,fontSize));

        gameSettings.add(createMenuItem("New Game With Same Settings",KeyEvent.VK_N));
        gameSettings.add(createMenuItem("Play Different Game", KeyEvent.VK_D));
        gameSettings.add(createMenuItem("Exit",KeyEvent.VK_E));

        JMenu help = new JMenu("Help");
        help.setMnemonic(KeyEvent.VK_H);
        help.setFont(new Font("Arial",Font.PLAIN,fontSize));
        help.add(createMenuItem("Display Rules",KeyEvent.VK_R));

        if(menubar!=null)
        {
            menubar.add(gameSettings);
            menubar.add(help);
        }
        return menubar;
    }

    /**
     * Creates a menu item with the specified text and keyboard shortcut.
     *
     * @param text Label text for the menu item
     * @param keyevent Keyboard shortcut key code
     * @return Configured JMenuItem
     */
    private JMenuItem createMenuItem(String text, int keyevent)
    {
        JMenuItem item = new JMenuItem(text);
        item.setActionCommand(text);
        item.setFont(new Font("Arial",Font.PLAIN,fontSize));
        item.setAccelerator(KeyStroke.getKeyStroke(keyevent,ActionEvent.ALT_MASK));
        if(view!=null)
            item.addActionListener(new ViewMenuListener(view));
        return item;
    }

    /**
     * Highlights the last pressed button in red when the player loses.
     *
     * @param lastpressed Array containing [row, col] of the last pressed tile
     */
    public void playerLost(int[] lastpressed)
    {
        if(buttons!=null && lastpressed!=null)
            buttons[lastpressed[0]][lastpressed[1]].setBackground(Color.RED);
    }

    /**
     * Updates the extra lives display.
     *
     * @param lives Number of extra lives remaining
     */
    public void updateExtraLives(int lives)
    {
        if(lives>=0&&extralives!=null) //using extralives
        {
            String text = extralives.getText();
            extralives.setText(text.substring(0, text.length()-1)+lives);
        }
    }

    /**
     * Updates the mines left counter.
     *
     * @param remaining Number of mines remaining
     */
    public void updateMinesLeft(int remaining) {
        if (minesLeft != null)
            minesLeft.setText("Mines Left: " + remaining);
    }

    /**
     * Updates the display to reflect the current exposed tiles.
     *
     * @param exposed 2D array indicating which tiles are exposed
     * @param emptyTileText String representation of empty tiles
     */
    public void refresh(boolean[][] exposed, String emptyTileText)
    {
        if(exposed!=null && emptyTileText!=null && buttons!=null)
        {
            for(int i = 0;i<numrows;i++)
            {
                for(int j= 0;j<numcols;j++)
                {
                    if(exposed[i][j]==true && buttons[i][j].getBackground()!=Color.GRAY &&
                            buttons[i][j].getText().equals(" "))
                    {
                        String buttontext = buttons[i][j].getActionCommand().split(",")[2];
                        buttons[i][j].setText(buttontext);
                        if(buttontext.equals(emptyTileText))
                            buttons[i][j].setBackground(Color.GRAY);
                    }
                }
            }
        }
        repaint();
    }

    /**
     * Creates the top panel with game information.
     *
     * @param mines Number of mines in the game
     * @return Configured JPanel
     */
    private JPanel topPanel(int mines)
    {
        JPanel top = new JPanel();
        top.setSize(width,topHeight);
        top.setLayout(new FlowLayout(FlowLayout.CENTER,70,0));

        minesLeft = new JLabel("Mines Left: "+mines);
        minesLeft.setFont(new Font("Arial",Font.BOLD,fontSize));
        top.add(minesLeft);

        if(view!=null)
        {
            int lives = view.getExtraLivesLeft();
            if(lives>0)
            {
                extralives = new JLabel("Lives Left: "+lives);
                extralives.setFont(new Font("Arial",Font.BOLD,fontSize));
                top.add(extralives);
            }
        }

        time = new JLabel("0");
        time.setFont(new Font("Arial",Font.BOLD,fontSize));
        timer = new Timer(1000, new ViewTimerActionListener(this));
        top.add(time);

        return top;
    }

    /**
     * Creates the game grid with buttons for each tile.
     *
     * @param grid 2D array of strings representing the game grid
     * @return JPanel containing the game grid
     */
    private JPanel centerPanel(String [][] grid)
    {
        JPanel center = new JPanel();

        if(grid!=null)
        {
            buttons = new JButton[numrows][numcols];
            for(int i =0;i<numrows;i++)
            {
                for(int j = 0;j<numcols;j++)
                {
                    JButton mybutton = createButton(i,j,grid[i][j]);
                    buttons[i][j] = mybutton;
                    center.add(mybutton);
                }
            }
            center.setLayout(new GridLayout(numrows,numcols));
        }
        return center;
    }

    /**
     * Creates a single button for a tile in the game grid.
     *
     * @param row Row index of the tile
     * @param col Column index of the tile
     * @param buttontext Value of the tile
     * @return Configured JButton
     */
    private JButton createButton(int row, int col,String buttontext)
    {
        JButton thisbutton;
        thisbutton = new JButton(" ");
        thisbutton.setActionCommand(row+","+col+","+buttontext);
        if(view!=null)
            thisbutton.addMouseListener(new ViewMouseListener(view));
        thisbutton.setFont(new Font("Arial",Font.BOLD,fontSize));
        return thisbutton;
    }

    /**
     * Places or removes a flag on a button.
     * Updates the mines left counter accordingly.
     *
     * @param button The button to flag/unflag
     * @return True if flag was placed, false if removed or operation failed
     */
    public boolean placeFlag(JButton button) {
        if(view != null && minesLeft != null) {
            String minesremain = minesLeft.getText();
            int mines = Integer.parseInt(minesremain.substring(12));

            if(button.getText().equals("F")) {
                // Already a flag - remove it
                mines++;
                button.setText(" ");
                button.setBackground(null);
                minesLeft.setText(minesremain.substring(0, 12) + mines);
                return false;
            } else if(button.getText().equals(view.getEmptyTileString())) {
                // Flag the button
                mines--;
                button.setText("F");
                button.setBackground(Color.WHITE);
                minesLeft.setText(minesremain.substring(0, 12) + mines);
                return true;
            }
        }
        return false; // Not flagged
    }

    /**
     * Stops the timer and returns the current time.
     *
     * @return Current elapsed time
     */
    public long stopTimer() {
        if(timer != null && time != null) {
            timer.stop();
            return Long.parseLong(time.getText());
        }
        return 0;
    }

    /**
     * Starts the timer and returns the current time.
     *
     * @return Current elapsed time
     */
    public long startTimer() {
        if(timer != null && time != null) {
            timer.start();
            return Long.parseLong(time.getText());
        }
        return 0;
    }

    /**
     * Gets the current elapsed time.
     *
     * @return Current elapsed time
     */
    public long getCurrentTime() {
        if(time != null)
            return Long.parseLong(time.getText());
        return 0;
    }

    /**
     * Increments the elapsed time by one second.
     * Exits the game if time overflows.
     */
    public void incrementTime() {
        if(view != null && time != null) {
            long curtime = Long.parseLong(time.getText());
            if(++curtime <= 0) {
                view.exitGame();
            } else {
                time.setText(curtime + "");
            }
        }
    }

    /**
     * Highlights a tile that contains a mine when clicked.
     *
     * @param row Row index of the tile
     * @param col Column index of the tile
     * @param mine String representation of a mine tile
     */
    public void pressed(int row, int col, String mine) {
        if(buttons != null) {
            JButton button = buttons[row][col];
            String text = button.getText().substring(button.getText().length() - mine.length());
            if(text.equals(mine))
                button.setBackground(Color.RED);
        }
    }
}