import java.util.ArrayList;
/* manages communication between the view and the model components */
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
	
	// Start the game
	public void go()
	{
		if(myModel==null || myView==null)
			System.exit(NULL_EXIT_CODE);
		myView.go(myModel.getDifficulties());
	}
	
	// Model sets difficulty level 
	public void setDifficulty(String difficulty)
	{
		if(myModel==null)
			System.exit(NULL_EXIT_CODE);
		myModel.setDifficulty(difficulty);
	}
	
	// Returns the preset difficulty level list from the model
	public ArrayList<String> getDifficulties()
	{
		if(myModel==null)
			System.exit(NULL_EXIT_CODE);
		return myModel.getDifficulties();
	}
	
	// Returns the string representation of a mine tile 
	public String getMineString()
	{
		if(myModel==null)
			System.exit(NULL_EXIT_CODE);
		return myModel.MINE;
	}
	
	// Starts the game and return status code
	public boolean startGame()
	{
		if(myModel==null)
			System.exit(NULL_EXIT_CODE);
		return myModel.startGame();
	}

	// Returns the number of mines from the model
	public int getNumMines()
	{
		if(myModel==null)
			System.exit(NULL_EXIT_CODE);
		return myModel.getNumMines();
	}

	//Returns the string representation for the game grid from the model
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
	
	// If flagged==true, flag has been placed at [row,col];
	// if flagged==false, unflagged [row,col];
	// tell the model that this has occurred
	public void placeFlag(boolean flagged,int row, int col)
	{
		if(myModel==null)
			System.exit(NULL_EXIT_CODE);
		myModel.tileFlagged(flagged,row, col);
	}
	
	// Returns true if player lost and false otherwise
	public boolean playerLost()
	{
		if(myModel==null)
			System.exit(NULL_EXIT_CODE);
		return myModel.playerLost();
	}
	
	// Returns true if player won and false otherwise
	public boolean playerWon()
	{
		if(myModel==null)
			System.exit(NULL_EXIT_CODE);
		return myModel.playerWon();
	}
	
	// Tells model to reset the game
	public void reset()
	{
		if(myModel==null)
			System.exit(NULL_EXIT_CODE);
		myModel.resetGame();
	}
	
	// Returns the int representation of the tile last 
	// pressed as determined by the model
	public int[] getLastPressed()
	{
		if(myModel==null)
			System.exit(NULL_EXIT_CODE);
		return myModel.getLastPressed();
	}
	
	// Make the model set the extra lives to the given value
	public void setExtraLives(int lives)
	{
		if(myModel==null)
			System.exit(NULL_EXIT_CODE);
		myModel.setExtraLives(lives);
	}
	
	// Returns the number of extra lives left 
	// as determined by the model
	public int getExtraLivesLeft()
	{
		if(myModel==null)
			System.exit(NULL_EXIT_CODE);
		return myModel.getExtraLivesLeft();
	}
	
	// Gets the String representation for an empty tile from model
	public String getEmptyTileString()
	{
		if(myModel==null)
			System.exit(NULL_EXIT_CODE);
		return myModel.EMPTY;
	}
	
	// Sets the number of rows for the custom setting to given value in model
	public void setCustomRows(int rows)
	{
		if(myModel==null)
			System.exit(NULL_EXIT_CODE);
		myModel.setCustomRows(rows);
	}
	
	// Sets the number of cols for the custom setting to given value in model
	public void setCustomColumns(int cols)
	{
		if(myModel==null)
			System.exit(NULL_EXIT_CODE);
		myModel.setCustomColumns(cols);
	}
	
	// Sets the number of mines for the custom setting to given value in model
	public void setCustomMines(int mines)
	{
		if(myModel==null)
			System.exit(NULL_EXIT_CODE);
		myModel.setCustomMines(mines);
	}
	
	// Returns the number of total games won as determined by model
	public long getTotalGamesWon()
	{
		if(myModel==null)
			System.exit(NULL_EXIT_CODE);
		return myModel.getTotalGamesWon();
	}
	
	// Returns the number of total games played 
    // as determined by model
	public long getTotalGamesPlayed()
	{
		if(myModel==null)
			System.exit(NULL_EXIT_CODE);
		return  myModel.getTotalGamesPlayed();
	}
	
	// Returns the string representations of the best times
	// as determined by the model
	public String getBestTime()
	{
		if(myModel==null)
			System.exit(NULL_EXIT_CODE);
		return myModel.getBestTimes();
	}
	
	// Gets the rules for the game from the model and return
	public String getRules()
	{
		if(myModel==null)
			System.exit(NULL_EXIT_CODE);
		return myModel.RULES;
	}
	
}

