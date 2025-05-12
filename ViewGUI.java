import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JSpinner;

public class ViewGUI implements ControllerToViewGUI{

    /* StartFrame:
     *  - Select difficulty
     * 		- beginner 10 mines 9x9 grid
     * 		- intermediate 40 mines 16x16 grid
     * 		- advanced 99 mines 16x30 grid
     * 		- custom
     *  - Extra Lives
     *
     *
     * Game Frame:
     *  - tabs with Game (new game, exit), Help (rules)
     *  - grid of tiles
     *  - number of mines left
     *  - time passed
     *  - number of lives left (if applicable)
     *
     *
     * End Frame:
     *  - message on win/loss
     *  - time
     *  - best time for begin/intermediate/expert/custom
     *  - total games played
     *  - games won
     *  - percentage won
     *  - Button: exit, play again
     */

    //to communicate with the controller for the model data
    private ViewGUIToController myController;

    private ViewStartFrame startframe;
    private ViewGameTilesFrame gameframe;
    private ViewEndFrame endframe;


    public ViewGUI(ViewGUIToController c)
    {
        myController = c;
    }

    //start the logic (start frame for settings)
    public void go(ArrayList<String> diffs)
    {
        startframe = new ViewStartFrame(this, diffs);
        if(startframe==null)
            System.exit(NULL_EXIT_CODE);
    }

    //difficulty was changed, notify controller and startframe if applicable
    public void setDifficulty(String difficulty)
    {
        if(myController==null || difficulty==null || startframe==null)
            System.exit(NULL_EXIT_CODE);
        if(difficulty.equals("custom"))
            startframe.enableCustomSpinners(true);
        else
            startframe.enableCustomSpinners(false);
        myController.setDifficulty(difficulty);
    }

    //extra lives changed, notify controller
    public void setExtraLives(int extralives)
    {
        if(myController==null)
            System.exit(NULL_EXIT_CODE);
        myController.setExtraLives(extralives);
    }

    //a spinner was changed, get the new info from the startframe
    //and notify controller
    public void setCustom(JSpinner spinner)
    {
        if(startframe==null || myController == null)
            System.exit(NULL_EXIT_CODE);
        String info = startframe.getCustomInfo(spinner);
        int myNum;
        try{
            myNum = Integer.parseInt(info.substring(3));
        }catch(NumberFormatException ex)
        {
            System.out.println("Error!");
            myNum = 0;
        }
        if(info.startsWith("row"))
            myController.setCustomRows(myNum);
        else if(info.startsWith("col"))
            myController.setCustomColumns(myNum);
        else //mines
            myController.setCustomMines(myNum);
        myController.setDifficulty("custom");
    }

    //exit the entire game by closing all frames
    public void exitGame()
    {
        if(startframe!=null)
            startframe.dispose();
        if(gameframe!=null)
            gameframe.dispose();
        if(endframe!=null)
            endframe.dispose();
    }

    //user wants to play another game - begin with a new start frame
    public void playAgain()
    {
        if(startframe!=null)
            startframe.dispose();
        if(gameframe!=null)
            gameframe.dispose();
        if(endframe!=null)
            endframe.dispose();

        if(myController==null)
            System.exit(NULL_EXIT_CODE);
        myController.reset();
        startframe = new ViewStartFrame(this, myController.getDifficulties());
    }

    //actually play/start the new game by notifying the controller
    // and making a new game frame with the given grid
    public void playGame()
    {
        if(gameframe!=null)
            gameframe.dispose();
        if(myController==null)
            System.exit(NULL_EXIT_CODE);
        boolean success = myController.startGame();
        if(!success)
        {
            createPopUp("Custom number of mines should be less than rows*columns.", 400,200,false);
        }
        else
        {
            if(startframe!=null)
                startframe.dispose();
            gameframe = new ViewGameTilesFrame(this,myController.getGrid(),myController.getNumMines());
        }
    }

    //get and return the rules from the controller
    public String getRules()
    {
        if(myController!=null)
            return myController.getRules();
        else
            return "";
    }

    //create a popup instance to display the given msg
    //the frame will have this width and height
    public void createPopUp(String msg, int width, int height, boolean timerUsage)
    {
        if(timerUsage && gameframe !=null)
            gameframe.stopTimer();
        new ViewPopupHelp(this, msg, width, height, timerUsage);
    }

    //popup has been closed
    //if timerUsage, start the timer back up
    public void endHelpPopup(boolean timerUsage)
    {
        if(timerUsage && gameframe!=null)
            gameframe.startTimer();
    }

    //a tile has been pressed
    //get the row,col of the tile pressed, notify controller, update
    //game frame
    public void tilePressed(String rowcol)
    {
        if(myController==null || gameframe==null)
            System.exit(NULL_EXIT_CODE);
        int row = Integer.parseInt(rowcol.split(",")[0]);
        int col = Integer.parseInt(rowcol.split(",")[1]);
        myController.tilePressed(row,col,gameframe.getCurrentTime());

        int extralives = myController.getExtraLivesLeft();
        if(extralives>=0) //using extralives
        {
            gameframe.updateExtraLives(extralives);
        }

        if(myController.playerLost())
        {
            gameframe.playerLost(myController.getLastPressed());
            endframe = new ViewEndFrame(this,false,gameframe.stopTimer(),myController.getBestTime(),myController.getTotalGamesPlayed(),myController.getTotalGamesWon());
        }
        else if(myController.playerWon())
        {
            endframe = new ViewEndFrame(this,true,gameframe.stopTimer(),myController.getBestTime(),myController.getTotalGamesPlayed(),myController.getTotalGamesWon());
        }
        else
        {
            //check if last pressed is a mine
            //row,col might be last pressed, but if the user is trying to
            //autocomplete, may have flagged a wrong tile and autocompleted
            //a mine (not row,col but actually in last pressed now)
            int [] last = myController.getLastPressed();
            gameframe.pressed(last[0],last[1],myController.getMineString());
        }

    }

    //user wants to place a flag on this button
    //notify controller and game frame of flag place
    public void placeFlag(JButton button)
    {
        if(myController==null || gameframe==null)
            System.exit(NULL_EXIT_CODE);
        int row = Integer.parseInt(button.getActionCommand().split(",")[0]);
        int col = Integer.parseInt(button.getActionCommand().split(",")[1]);
        boolean flagged = gameframe.placeFlag(button);
        myController.placeFlag(flagged,row,col);

    }

    //return the empty tile string from the controller
    public String getEmptyTileString()
    {
        if(myController==null)
            System.exit(NULL_EXIT_CODE);
        return myController.getEmptyTileString();
    }

    //make the game frame refresh the tiles to correspond to exposed
    public void refresh(boolean [][] exposed, String emptyTileText)
    {
        if(gameframe==null)
            System.exit(NULL_EXIT_CODE);
        gameframe.refresh(exposed, emptyTileText);
    }

    //extra lives option should be enabled for the user, notifies
    //start frame
    public void showExtraLives()
    {
        if(startframe==null)
            System.exit(NULL_EXIT_CODE);
        startframe.extraLives();
    }

    //return the extra lives left from the controller
    public int getExtraLivesLeft()
    {
        if(myController==null)
            System.exit(NULL_EXIT_CODE);
        return myController.getExtraLivesLeft();
    }
}

    /**
     * Called when the user requests a hint
     * Gets hint from the controller and displays it in the game frame
     */
    public void requestHint() {
        if(myController == null || gameframe == null)
            System.exit(NULL_EXIT_CODE);

        int[] hint = myController.getHint();

        if(hint != null) {
            // Show hint in the game frame
            gameframe.highlightHintCell(hint[0], hint[1], false);
            createPopUp("Hint: Try the highlighted cell at row " + (hint[0]+1) + ", column " + (hint[1]+1), 400, 150, true);
        } else {
            createPopUp("No logical hint available. Try using some strategy!", 400, 150, true);
        }
    }
}
