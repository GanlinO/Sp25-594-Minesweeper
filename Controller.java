import java.util.ArrayList;

// The Controller class manages communication between the view (GUI) and the model components.
// It follows the MVC (Model-View-Controller) architecture to handle user inputs and updates.
public class Controller implements ViewGUIToController {
	
	private ControllerToViewGUI myView;
	private ControllerToModel myModel;
	
// Constructor initializes the model and view components.
	// If either component fails to initialize, the program exits with an error.
	public Controller() {
		myModel = new Model();
		myView = new ViewGUI(this);
		if(myModel == null || myView == null)
			System.exit(NULL_EXIT_CODE);
	}
	// Starts the game by initializing the GUI with the list of available difficulties from the model.
	public void go() {
		if(myModel == null || myView == null)
			System.exit(NULL_EXIT_CODE);
		myView.go(myModel.getDifficulties());
	}
	// Instructs the model to set the current game difficulty to the specified value.
	public void setDifficulty(String difficulty) {
		if(myModel == null)
			System.exit(NULL_EXIT_CODE);
		myModel.setDifficulty(difficulty);
	}
		// Retrieves the list of preset difficulty levels from the model.
	public ArrayList<String> getDifficulties() {
		if(myModel == null)
			System.exit(NULL_EXIT_CODE);
		return myModel.getDifficulties();
	}

	// Returns the string used to represent a mine tile from the model.
	public String getMineString() {
		if(myModel == null)
			System.exit(NULL_EXIT_CODE);
		return myModel.MINE;
	}
	// Signals the model that the game is starting and returns true if the game starts successfully.
	public boolean startGame() {
		if(myModel == null)
			System.exit(NULL_EXIT_CODE);
		return myModel.startGame();
	}

		// Returns the number of mines placed on the board, as determined by the model.
	public int getNumMines() {
		if(myModel == null)
			System.exit(NULL_EXIT_CODE);
		return myModel.getNumMines();
	}

		// Retrieves the current state of the game grid from the model as a 2D array of strings.
	public String[][] getGrid() {
		if(myModel == null)
			System.exit(NULL_EXIT_CODE);
		return myModel.getGrid();
	}

	// Called when a tile is pressed.
	// Passes the click event to the model and refreshes the view based on the result.
	public void tilePressed(int row, int col, long currentTime) {
		if(myModel == null || myView == null)
			System.exit(NULL_EXIT_CODE);
		myView.refresh(myModel.tilePressed(row, col, currentTime), myModel.EMPTY);
	}
	
	// Called when a tile is flagged or unflagged.
	// Updates the model to reflect the new flag status at the given coordinates.
	public void placeFlag(boolean flagged, int row, int col) {
		if(myModel == null)
			System.exit(NULL_EXIT_CODE);
		myModel.tileFlagged(flagged, row, col);
	}
	
	// Returns true if the player has triggered a loss condition, according to the model.
	public boolean playerLost() {
		if(myModel == null)
			System.exit(NULL_EXIT_CODE);
		return myModel.playerLost();
	}
	
	// Returns true if the player has fulfilled the win condition, according to the model.
	public boolean playerWon() {
		if(myModel == null)
			System.exit(NULL_EXIT_CODE);
		return myModel.playerWon();
	}
	
	// Resets the game state in the model to allow a new game to be started.
	public void reset() {
		if(myModel == null)
			System.exit(NULL_EXIT_CODE);
		myModel.resetGame();
	}
	
	// Retrieves the row and column of the most recently pressed tile from the model.
	public int[] getLastPressed() {
		if(myModel == null)
			System.exit(NULL_EXIT_CODE);
		return myModel.getLastPressed();
	}
	
	// Sets the number of extra lives allowed in the game via the model.
	public void setExtraLives(int lives) {
		if(myModel == null)
			System.exit(NULL_EXIT_CODE);
		myModel.setExtraLives(lives);
	}
	
	// Returns the number of extra lives remaining, as tracked by the model.
	public int getExtraLivesLeft() {
		if(myModel == null)
			System.exit(NULL_EXIT_CODE);
		return myModel.getExtraLivesLeft();
	}
	
	// Returns the string used to represent an empty tile from the model.
	public String getEmptyTileString() {
		if(myModel == null)
			System.exit(NULL_EXIT_CODE);
		return myModel.EMPTY;
	}
	// Sets the number of rows for a custom game board configuration via the model.
	public void setCustomRows(int rows) {
		if(myModel == null)
			System.exit(NULL_EXIT_CODE);
		myModel.setCustomRows(rows);
	}
	
	// Sets the number of columns for a custom game board configuration via the model.
	public void setCustomColumns(int cols) {
		if(myModel == null)
			System.exit(NULL_EXIT_CODE);
		myModel.setCustomColumns(cols);
	}
	
	// Sets the number of mines for a custom game board configuration via the model.
	public void setCustomMines(int mines) {
		if(myModel == null)
			System.exit(NULL_EXIT_CODE);
		myModel.setCustomMines(mines);
	}
	
	// Retrieves the total number of games won, as stored by the model.
	public long getTotalGamesWon() {
		if(myModel == null)
			System.exit(NULL_EXIT_CODE);
		return myModel.getTotalGamesWon();
	}
	
	// Retrieves the total number of games played, as stored by the model.
	public long getTotalGamesPlayed() {
		if(myModel == null)
			System.exit(NULL_EXIT_CODE);
		return myModel.getTotalGamesPlayed();
	}
	
	// Returns a string summary of the best recorded game times, as stored by the model.
	public String getBestTime() {
		if(myModel == null)
			System.exit(NULL_EXIT_CODE);
		return myModel.getBestTimes();
	}
	
	// Returns the game rules as a formatted string from the model.
	public String getRules() {
		if(myModel == null)
			System.exit(NULL_EXIT_CODE);
		return myModel.RULES;
	}
/**
 * Provides a hint to the player based on the current game state.
 * Uses the KeyLogicImp class to analyze the game board and suggest a safe move.
 * 
 * @return int[] coordinates [row, col] of the suggested move
 */
public int[] getHint() {
    if(myModel == null)
        System.exit(NULL_EXIT_CODE);
    
    // Get the current game state from the model
    String[][] actualGrid = myModel.getGrid();
    boolean[][] exposedTiles = myModel.getExposed();
    boolean[][] flaggedTiles = new boolean[actualGrid.length][actualGrid[0].length];
    
    // We need to recreate the flagged tiles array since Model doesn't provide direct access
    for (int i = 0; i < actualGrid.length; i++) {
        for (int j = 0; j < actualGrid[0].length; j++) {
            // A tile is considered flagged if it's not exposed and has a flag on it
            // This is an approximation - in a full implementation, you would track flags in the Model
            if (!exposedTiles[i][j]) {
                // Check if this position is in the list of flagged positions
                // In a real implementation, you'd have access to the actual flagged array
                // For now, this is a placeholder
                // flaggedTiles[i][j] = model.isFlagged(i, j);
            }
        }
    }
    
    // First try to find a safe cell to click
    int[] safeCell = findSafeCell(actualGrid, exposedTiles, flaggedTiles);
    if (safeCell != null) {
        return safeCell;
    }
    
    // If no safe cell is found, try to find a cell that should be flagged
    int[] mineCell = KeyLogicImp.suggestCellToRevealAsMine(actualGrid, exposedTiles, flaggedTiles);
    if (mineCell != null) {
        // We found a mine, but we don't want to tell the player to click on a mine
        // Instead, we'll return it so the UI can highlight it as a flag suggestion
        return mineCell;
    }
    
    return null;
}

/**
 * Helper method to find a safe cell to click based on current game state
 */
private int[] findSafeCell(String[][] actualGrid, boolean[][] exposedTiles, boolean[][] flaggedTiles) {
    int rows = actualGrid.length;
    int cols = actualGrid[0].length;
    
    // Check each exposed numbered cell
    for (int row = 0; row < rows; row++) {
        for (int col = 0; col < cols; col++) {
            if (exposedTiles[row][col] && isNumeric(actualGrid[row][col])) {
                int number = Integer.parseInt(actualGrid[row][col]);
                
                // Count flagged neighbors
                int flaggedCount = 0;
                ArrayList<int[]> hiddenNeighbors = new ArrayList<>();
                
                // Check all 8 surrounding cells
                for (int dr = -1; dr <= 1; dr++) {
                    for (int dc = -1; dc <= 1; dc++) {
                        if (dr == 0 && dc == 0) continue; // Skip the cell itself
                        
                        int r = row + dr;
                        int c = col + dc;
                        
                        if (r >= 0 && r < rows && c >= 0 && c < cols) {
                            if (flaggedTiles[r][c]) {
                                flaggedCount++;
                            } else if (!exposedTiles[r][c]) {
                                hiddenNeighbors.add(new int[]{r, c});
                            }
                        }
                    }
                }
                
                // If this numbered cell has exactly the right number of flags around it,
                // all other hidden neighbors must be safe
                if (flaggedCount == number && !hiddenNeighbors.isEmpty()) {
                    return hiddenNeighbors.get(0);
                }
            }
        }
    }
    
    return null;
}

/**
 * Helper method to check if a string is numeric
 */
private boolean isNumeric(String str) {
    if (str == null) {
        return false;
    }
    try {
        Integer.parseInt(str);
        return true;
    } catch (NumberFormatException e) {
        return false;
    }
}

	
	
