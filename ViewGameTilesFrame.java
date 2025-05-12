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
//represent a frame for the actual game of minesweeper
public class ViewGameTilesFrame extends JFrame{

    private final int fontSize = 18;
    private final int topHeight = 100; //height for the top of the game
    //where extralives, time passed, mines left are put

    private ViewGUI view;
    private JButton[][] buttons; //buttons for the tile grid
    private int numrows; //number of rows in the grid
    private int numcols; //number of cols in the grid
    private int width; //width of the grid
    private int cheight; //height of the grid
    private JLabel minesLeft; //mines left unflagged and unhit in the grid
    private JLabel time; //time passed so far in the game
    private Timer timer; //has action listener to increment time every second
    //can be stopped and started through this class
    private JLabel extralives; //number of extra lives user has left
    private JButton hintButton; // Button to request a hint

    //given the view, actual grid of tile strings, number of mines in the grid
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
        width = numcols*60; //arbitrary width of 60 for each tile
        cheight = numrows*50; //arbitrary height of 50 for each tile
        if(width<500)
            width = 500; //minimum to fit all of top panel

        setSize(width,cheight+topHeight);
        setLayout(new BorderLayout(10,10));
        setLocationRelativeTo(null);

        //menu bar with "Game", "Help"
        JMenuBar menubar = new JMenuBar();
        setJMenuBar(createMenu(menubar));

        //holds the extra lives (if applicable), mines left, time passed
        add(topPanel(mines),BorderLayout.PAGE_START);
        if(grid!=null)
            add(centerPanel(grid),BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent){
                System.exit(0);
            }
        });

        setVisible(true);
        timer.start();
    }

    //create and return the menu bar for the game
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

    //create and return a single menu item with the given text and keyevent
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

    //assuming player has lost, set the background color of the last
    //pressed button (which should be a mine)
    public void playerLost(int[] lastpressed)
    {
        if(buttons!=null && lastpressed!=null)
            buttons[lastpressed[0]][lastpressed[1]].setBackground(Color.RED);
    }

    public void updateExtraLives(int lives)
    {
        if(lives>=0&&extralives!=null) //using extralives
        {
            String text = extralives.getText();
            extralives.setText(text.substring(0, text.length()-1)+lives);
        }
    }

    //make sure all tiles shown to user (true in exposed[][]) are displayed
    //correctly
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

        // Highlight a cell recommended as a hint
    public void highlightHintCell(int row, int col, boolean isMine) {
        if (buttons != null && row >= 0 && col >= 0 && row < numrows && col < numcols) {
            // Use a distinctive color for the hint
            Color hintColor = isMine ? new Color(255, 200, 200) : new Color(200, 255, 200);
            buttons[row][col].setBackground(hintColor);

            // After 2 seconds, remove the highlight if the tile hasn't been clicked
            new Timer(2000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!buttons[row][col].getText().equals(buttons[row][col].getActionCommand().split(",")[2])) {
                        buttons[row][col].setBackground(null);
                    }
                    ((Timer)e.getSource()).stop();
                }
            }).start();
        }
    }


    //create and return the top panel with the number of mines left,
    //time passed, lives left (if applicable)
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

        // Add hint button
        hintButton = new JButton("Hint");
        hintButton.setFont(new Font("Arial", Font.BOLD, fontSize));
        hintButton.setActionCommand("Hint");
        if (view != null) {
    h        intButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            view.requestHint();
        }
    });
}
top.add(hintButton);

        return top;
    }

    //create and return the center panel with the grid tiles as buttons
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

    //create and return a single button with the given information
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

    //place a flag at the given button if it is not exposed and not flagged
    //and return true, else if the button is already flagged,
    //unflag the button and return false, else return false
    //(not flagged, already exposed)
    //also update minesLeft
    public boolean placeFlag(JButton button)
    {
        if(view!=null && minesLeft!=null)
        {
            String minesremain = minesLeft.getText();
            int mines = Integer.parseInt(minesremain.substring(12));

            if(button.getText().equals("F"))
            {
                mines++;
                button.setText(" ");
                button.setBackground(null);
                minesLeft.setText( minesremain.substring(0,12)+mines);
                return false; //already a flag
            }
            else if(button.getText().equals(view.getEmptyTileString()))
            {//cannot flag something already clicked
                mines--;
                button.setText("F"); //flag
                button.setBackground(Color.WHITE);
                minesLeft.setText( minesremain.substring(0,12)+ mines);
                return true; //change to a flag
            }
        }
        return false; //not flagged
    }

    //stop the timer and return the current time
    public long stopTimer()
    {
        if(timer!=null&&time!=null)
        {
            timer.stop();
            return Long.parseLong(time.getText());
        }
        return 0;
    }

    //start the timer and return the current time
    public long startTimer()
    {
        if(timer!=null&&time!=null)
        {
            timer.start();
            return Long.parseLong(time.getText());
        }
        return 0;
    }

    //return the current time
    public long getCurrentTime()
    {
        if(time!=null)
            return Long.parseLong(time.getText());
        return 0;
    }

    //increment the time
    public void incrementTime()
    {
        if(view!=null&&time!=null)
        {
            long curtime = Long.parseLong(time.getText());
            if(++curtime <=0)
            {
                System.out.println("You took way too long!");
                view.exitGame();
            }
            else
            {
                time.setText(curtime+"");
            }
        }
    }

    //tile at row,col was pressed - if it is a mine, make the button
    //background red
    public void pressed(int row,int col,String mine)
    {
        if(buttons!=null)
        {
            JButton button = buttons[row][col];
            String text = button.getText().substring(button.getText().length()-mine.length());
            if(text.equals(mine))
                button.setBackground(Color.RED);
        }
    }

}
