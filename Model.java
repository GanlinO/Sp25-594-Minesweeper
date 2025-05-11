import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/* Keeps track of most of the data in the minesweeper program */
public class Model implements ControllerToModel{
	private final ArrayList<String> DIFFICULTIES = new ArrayList<String>(Arrays.asList("beginner","intermediate","expert","custom"));
	private final int BEGINNERMINES = 10;
	private final int INTERMEDIATEMINES = 40;
	private final int EXPERTMINES = 99;
	
	//Tracks game data while instance of the model runs
	private static long gamesPlayed = 1;
	private static long gamesWon = 0;
	private static long bestTimeSecondsBeg = 0;
	private static long bestTimeSecondsInter = 0;
	private static long bestTimeSecondsExpert = 0;
	private static long bestTimeSecondsCustom = 0;
	 
	// Tracks custom game settings the user sets
	private int customMines = 10;
	private int customRows = 9;
	private int customCols = 9;
		
	private int difficultyIndex; // Beginner=0, intermediate=1, expert=2, custom=3
	private int numberMines;
	private int numberRows;
	private int numberCols;
	private int extraLivesLeft; // Starts with value -1
	
	// Decides if a numbered tile is pressed for the first time, 
    // or if it is pressed for autocompletion
	private int [][] timesNumberPressed;
	// Tracks which tiles the user can see the values for
	private boolean[][] exposedTiles;
	// Tracks values of the tiles on the grid
	// (actual placements of mines, empties, numbers)
	private String [][] actualGrid;
	// Tracks cooredinates of the mines in this grid corresponding
	// to actualGrid
	private int[] [] mineLocations;
	// Tracks mines the user hit but did not flag
	// Used mostly for extra lives, initiated as [[-1,-1],[-1,-1],[-1,-1]]
	private int[][] minesHit;
	// Tracks which tiles the user flagged
	private boolean [][] flaggedTiles;
	// Tracks the button last revealed/pressed by the user
	// Initiated with values [-1,-1]
	private int[] lastpressed;
	private boolean won;
	private boolean lost;
	
	// Random generator for the tile grid
	private Random randgen;
}
