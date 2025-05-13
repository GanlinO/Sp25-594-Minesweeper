import java.util.ArrayList;

/**
 * Controls communication between the Model and View components.
 * Implements ViewGUIToController interface to process requests from the View
 * and uses ControllerToModel to interact with the Model.
 */
public class Controller implements ViewGUIToController{

    private ControllerToViewGUI myView;
    private ControllerToModel myModel;

    /**
     * Constructs a new Controller, initializing the Model and View components.
     * Exits the application if either component cannot be created.
     */
    public Controller()
    {
        myModel = new Model();
        myView = new ViewGUI(this);
        if(myModel==null || myView==null)
            System.exit(NULL_EXIT_CODE);
    }

    /**
     * Starts the game by initializing the view with available difficulties.
     * Displays the start screen to the user.
     */
    public void go()
    {
        if(myModel==null || myView==null)
            System.exit(NULL_EXIT_CODE);
        myView.go(myModel.getDifficulties());
    }

    /**
     * Sets the difficulty level in the model.
     *
     * @param difficulty The difficulty level to set (beginner, intermediate, expert, or custom)
     */
    public void setDifficulty(String difficulty)
    {
        if(myModel==null)
            System.exit(NULL_EXIT_CODE);
        myModel.setDifficulty(difficulty);
    }

    /**
     * Retrieves available difficulty options from the model.
     *
     * @return ArrayList of available difficulty options
     */
    public ArrayList<String> getDifficulties()
    {
        if(myModel==null)
            System.exit(NULL_EXIT_CODE);
        return myModel.getDifficulties();
    }

    /**
     * Gets the string representation of a mine tile from the model.
     *
     * @return String representation of a mine tile
     */
    public String getMineString()
    {
        if(myModel==null)
            System.exit(NULL_EXIT_CODE);
        return myModel.MINE;
    }

    /**
     * Notifies the model that the game has started.
     *
     * @return boolean indicating if the game was successfully started
     */
    public boolean startGame()
    {
        if(myModel==null)
            System.exit(NULL_EXIT_CODE);
        return myModel.startGame();
    }

    /**
     * Gets the total number of mines in the current game.
     *
     * @return Number of mines
     */
    public int getNumMines()
    {
        if(myModel==null)
            System.exit(NULL_EXIT_CODE);
        return myModel.getNumMines();
    }

    /**
     * Gets the current game grid from the model.
     *
     * @return 2D array representing the game grid
     */
    public String[][] getGrid()
    {
        if(myModel==null)
            System.exit(NULL_EXIT_CODE);
        return myModel.getGrid();
    }

    /**
     * Processes a tile press at the specified coordinates.
     * Updates the view with the exposed tiles.
     *
     * @param row Row index of the pressed tile
     * @param col Column index of the pressed tile
     * @param currentTime Current game time when the tile was pressed
     */
    public void tilePressed(int row, int col, long currentTime)
    {
        if(myModel==null || myView==null)
            System.exit(NULL_EXIT_CODE);
        myView.refresh(myModel.tilePressed(row,col, currentTime), myModel.EMPTY);
    }

    /**
     * Notifies the model when a flag is placed or removed.
     *
     * @param flagged True if flag was placed, false if removed
     * @param row Row index of the flagged tile
     * @param col Column index of the flagged tile
     */
    public void placeFlag(boolean flagged,int row, int col)
    {
        if(myModel==null)
            System.exit(NULL_EXIT_CODE);
        myModel.tileFlagged(flagged,row, col);
    }

    /**
     * Checks if the player has lost the game.
     *
     * @return True if player has lost, false otherwise
     */
    public boolean playerLost()
    {
        if(myModel==null)
            System.exit(NULL_EXIT_CODE);
        return myModel.playerLost();
    }

    /**
     * Checks if the player has won the game.
     *
     * @return True if player has won, false otherwise
     */
    public boolean playerWon()
    {
        if(myModel==null)
            System.exit(NULL_EXIT_CODE);
        return myModel.playerWon();
    }

    /**
     * Tells the model to reset the game state.
     */
    public void reset()
    {
        if(myModel==null)
            System.exit(NULL_EXIT_CODE);
        myModel.resetGame();
    }

    /**
     * Gets the coordinates of the last pressed tile.
     *
     * @return Array containing row and column indices of the last pressed tile
     */
    public int[] getLastPressed()
    {
        if(myModel==null)
            System.exit(NULL_EXIT_CODE);
        return myModel.getLastPressed();
    }

    /**
     * Sets the number of extra lives for the current game.
     *
     * @param lives Number of extra lives
     */
    public void setExtraLives(int lives)
    {
        if(myModel==null)
            System.exit(NULL_EXIT_CODE);
        myModel.setExtraLives(lives);
    }

    /**
     * Gets the number of extra lives remaining.
     *
     * @return Number of extra lives left
     */
    public int getExtraLivesLeft()
    {
        if(myModel==null)
            System.exit(NULL_EXIT_CODE);
        return myModel.getExtraLivesLeft();
    }

    /**
     * Gets the string representation of an empty tile.
     *
     * @return String representation of an empty tile
     */
    public String getEmptyTileString()
    {
        if(myModel==null)
            System.exit(NULL_EXIT_CODE);
        return myModel.EMPTY;
    }

    /**
     * Sets the number of rows for custom game settings.
     *
     * @param rows Number of rows for the custom game
     */
    public void setCustomRows(int rows)
    {
        if(myModel==null)
            System.exit(NULL_EXIT_CODE);
        myModel.setCustomRows(rows);
    }

    /**
     * Sets the number of columns for custom game settings.
     *
     * @param cols Number of columns for the custom game
     */
    public void setCustomColumns(int cols)
    {
        if(myModel==null)
            System.exit(NULL_EXIT_CODE);
        myModel.setCustomColumns(cols);
    }

    /**
     * Sets the number of mines for custom game settings.
     *
     * @param mines Number of mines for the custom game
     */
    public void setCustomMines(int mines)
    {
        if(myModel==null)
            System.exit(NULL_EXIT_CODE);
        myModel.setCustomMines(mines);
    }

    /**
     * Gets the total number of games won.
     *
     * @return Total games won
     */
    public long getTotalGamesWon()
    {
        if(myModel==null)
            System.exit(NULL_EXIT_CODE);
        return myModel.getTotalGamesWon();
    }

    /**
     * Gets the total number of games played.
     *
     * @return Total games played
     */
    public long getTotalGamesPlayed()
    {
        if(myModel==null)
            System.exit(NULL_EXIT_CODE);
        return  myModel.getTotalGamesPlayed();
    }

    /**
     * Gets the best times for all difficulty levels.
     *
     * @return String containing the best times information
     */
    public String getBestTime()
    {
        if(myModel==null)
            System.exit(NULL_EXIT_CODE);
        return myModel.getBestTimes();
    }

    /**
     * Gets the rules of the game.
     *
     * @return String containing the game rules
     */
    public String getRules()
    {
        if(myModel==null)
            System.exit(NULL_EXIT_CODE);
        return myModel.RULES;
    }

}
