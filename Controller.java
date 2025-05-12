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

	
	
