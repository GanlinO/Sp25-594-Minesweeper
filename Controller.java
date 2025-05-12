import java.util.ArrayList;
//manages communication between the view and the model components

public class Controller implements ViewGUIToController{

    private ControllerToViewGUI myView;
    private ControllerToModel myModel;

    public Controller()
    {
        myModel = new Model();
        myView = new ViewGUI(this);
        if(myModel==null || myView==null)
            System.exit(NULL_EXIT_CODE);
    }

    //start the game
    public void go()
    {
        if(myModel==null || myView==null)
            System.exit(NULL_EXIT_CODE);
        myView.go(myModel.getDifficulties());
    }

    //have the model set the game difficulty to the given value
    public void setDifficulty(String difficulty)
    {
        if(myModel==null)
            System.exit(NULL_EXIT_CODE);
        myModel.setDifficulty(difficulty);
    }

    //return the preset difficulty list from the model
    public ArrayList<String> getDifficulties()
    {
        if(myModel==null)
            System.exit(NULL_EXIT_CODE);
        return myModel.getDifficulties();
    }

    //return the string representation for a mine tile
    //in the game from the model
    public String getMineString()
    {
        if(myModel==null)
            System.exit(NULL_EXIT_CODE);
        return myModel.MINE;
    }

    //the game has started
    //tell the model that the game started and return the success code
    public boolean startGame()
    {
        if(myModel==null)
            System.exit(NULL_EXIT_CODE);
        return myModel.startGame();
    }

    //return the number of mines from the model
    public int getNumMines()
    {
        if(myModel==null)
            System.exit(NULL_EXIT_CODE);
        return myModel.getNumMines();
    }

    //return the string representation for the game grid from the model
    public String[][] getGrid()
    {
        if(myModel==null)
            System.exit(NULL_EXIT_CODE);
        return myModel.getGrid();
    }

    public void tilePressed(int row, int col, long currentTime)
    {
        if(myModel==null || myView==null)
            System.exit(NULL_EXIT_CODE);
        myView.refresh(myModel.tilePressed(row,col, currentTime), myModel.EMPTY);
    }

    //if flagged==true, flag has been placed at [row,col]
    //if flagged==false, unflagged [row,col]
    //tell the model that this has occurred
    public void placeFlag(boolean flagged,int row, int col)
    {
        if(myModel==null)
            System.exit(NULL_EXIT_CODE);
        myModel.tileFlagged(flagged,row, col);
    }

    //return true if player lost or false if player has not lost
    //as determined by the model
    public boolean playerLost()
    {
        if(myModel==null)
            System.exit(NULL_EXIT_CODE);
        return myModel.playerLost();
    }

    //return true if player won or false if player has not won
    //as determined by the model
    public boolean playerWon()
    {
        if(myModel==null)
            System.exit(NULL_EXIT_CODE);
        return myModel.playerWon();
    }

    //tell model to reset the game
    public void reset()
    {
        if(myModel==null)
            System.exit(NULL_EXIT_CODE);
        myModel.resetGame();
    }

    //return the int representation of the tile last
    //pressed as determined by the model
    public int[] getLastPressed()
    {
        if(myModel==null)
            System.exit(NULL_EXIT_CODE);
        return myModel.getLastPressed();
    }

    //make the model set the extra lives to the given value
    public void setExtraLives(int lives)
    {
        if(myModel==null)
            System.exit(NULL_EXIT_CODE);
        myModel.setExtraLives(lives);
    }

    //return the number of extra lives left
    //as determined by the model
    public int getExtraLivesLeft()
    {
        if(myModel==null)
            System.exit(NULL_EXIT_CODE);
        return myModel.getExtraLivesLeft();
    }

    //get the String representation for an empty tile from model
    public String getEmptyTileString()
    {
        if(myModel==null)
            System.exit(NULL_EXIT_CODE);
        return myModel.EMPTY;
    }

    //set the number of rows for the custom setting to given value in model
    public void setCustomRows(int rows)
    {
        if(myModel==null)
            System.exit(NULL_EXIT_CODE);
        myModel.setCustomRows(rows);
    }

    //set the number of cols for the custom setting to given value in model
    public void setCustomColumns(int cols)
    {
        if(myModel==null)
            System.exit(NULL_EXIT_CODE);
        myModel.setCustomColumns(cols);
    }

    //set the number of mines for the custom setting to given value in model
    public void setCustomMines(int mines)
    {
        if(myModel==null)
            System.exit(NULL_EXIT_CODE);
        myModel.setCustomMines(mines);
    }

    //return number of total games won as determined by model
    public long getTotalGamesWon()
    {
        if(myModel==null)
            System.exit(NULL_EXIT_CODE);
        return myModel.getTotalGamesWon();
    }

    //return number of total games played as determined by model
    public long getTotalGamesPlayed()
    {
        if(myModel==null)
            System.exit(NULL_EXIT_CODE);
        return  myModel.getTotalGamesPlayed();
    }

    //return the string representations of the best times for the games
    //as determined by the model
    public String getBestTime()
    {
        if(myModel==null)
            System.exit(NULL_EXIT_CODE);
        return myModel.getBestTimes();
    }

    //get the rules for the game from the model and return
    public String getRules()
    {
        if(myModel==null)
            System.exit(NULL_EXIT_CODE);
        return myModel.RULES;
    }

    /*  Hint feature  */
    public int[] applyHint() {
        if (myModel == null) System.exit(NULL_EXIT_CODE);
        return myModel.applyHint();
    }

    public int getNumFlags() {
        if (myModel == null) System.exit(NULL_EXIT_CODE);
        return myModel.getNumFlags();
    }

}

