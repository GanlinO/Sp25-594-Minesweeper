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

	// Initialize a model to play the game
	public Model(){
		randgen = new Random(System.currentTimeMillis());
		numberMines = BEGINNERMINES;
		numberRows = 9;
		numberCols = 9;
		difficultyIndex = 0;
		
		won = false;
		lost = false;
		
		lastpressed = new int[2];
		lastpressed[0] = -1;
		lastpressed[1] = -1;
		extraLivesLeft = -1;
		
		minesHit = new int[3][2];
		minesHit[0][0] = lastpressed[0];  minesHit[0][1] = lastpressed[1];
		minesHit[1][0] = lastpressed[0];  minesHit[1][1] = lastpressed[1];
		minesHit[2][0] = lastpressed[0];  minesHit[2][1] = lastpressed[1];
	}

	// Set difficulty level
	public void setDifficulty(String diff){
		if(diff==null)
			System.exit(NULL_EXIT_CODE);
		switch(diff){
		case "beginner":
			numberMines = BEGINNERMINES;
			numberRows = 9;
			numberCols = 9;
			difficultyIndex = 0;
			break;
		case "intermediate":
			numberMines = INTERMEDIATEMINES;
			numberRows = 16;
			numberCols = 16;
			difficultyIndex = 1;
			break;
		case "expert":
			numberMines = EXPERTMINES;
			numberRows = 16;
			numberCols = 30;
			difficultyIndex = 2;
			break;
		case "custom":
			numberMines = customMines;
			numberRows = customRows;
			numberCols = customCols;
			difficultyIndex = 3;
			break;
		default:
			throw new IllegalArgumentException("Difficulty not correct!");
		}
	}

	// If user wants to play a custom game, change rows
	public void setCustomRows(int rows){
		if(rows>=2 && rows<=30)
			customRows = rows;
	}
	
	// If user wants to play a custom game, change cols
	public void setCustomColumns(int cols){
		if(cols>=2 && cols<=30)
			customCols = cols;
	}
	
	// If user wants to play a custom game, change mines
	public void setCustomMines(int mines){
		if(mines>=1 && mines<=150)
			customMines = mines;
	}

	// Resets game data to play another game
	public void resetGame(){
		gamesPlayed += 1;

		randgen = new Random(System.currentTimeMillis());
		
		// Reset difficulty level to default
		numberMines = BEGINNERMINES;
		numberRows = 9;
		numberCols = 9;
		difficultyIndex = 0;
		
		// Rest last tile pressed
		lastpressed = new int[2];
		lastpressed[0] = -1;
		lastpressed[1] = -1;
		
		// Reset win/loss
		won = false;
		lost = false;
		extraLivesLeft = -1;
		minesHit = new int[3][2];
		minesHit[0][0] = lastpressed[0];  minesHit[0][1] = lastpressed[1];
		minesHit[1][0] = lastpressed[0];  minesHit[1][1] = lastpressed[1];
		minesHit[2][0] = lastpressed[0];  minesHit[2][1] = lastpressed[1];
	
		// Reset custom settings
		customMines = 10;
		customRows = 9;
		customCols = 9;
	}

	public int getNumMines(){
		return numberMines;
	}
	
	public boolean [][] getExposed(){
		if(exposedTiles==null)
			System.exit(NULL_EXIT_CODE);
		return exposedTiles;
	}
	
	public int[] getLastPressed(){
		if(lastpressed==null)
			System.exit(NULL_EXIT_CODE);
		return lastpressed;
	}

	// If user wants to change the number of extra lives
	public void setExtraLives(int lives){
		if(extraLivesLeft<=3 && extraLivesLeft>=-1)
			extraLivesLeft = lives;
	}

	public String getBestTimes(){
		String str = "";
		if(difficultyIndex == 0 || bestTimeSecondsBeg>0)
			str += ("Beginner best time: "+ bestTimeSecondsBeg+ " seconds\n" );
		if(difficultyIndex == 1 || bestTimeSecondsInter>0)
			str += ("Intermediate best time: "+ bestTimeSecondsInter+ " seconds\n" );
		if(difficultyIndex == 2 || bestTimeSecondsExpert>0)
			str += ("Expert best time: "+ bestTimeSecondsExpert+ " seconds\n" );
		if(difficultyIndex == 3 || bestTimeSecondsCustom>0)
			str += ("Custom best time: "+ bestTimeSecondsCustom+ " seconds\n" );
		
		return str;
	}
	
	public int getExtraLivesLeft(){
		return extraLivesLeft;
	}
	
	public String [][] getGrid(){
		if(actualGrid==null)
			System.exit(NULL_EXIT_CODE);
		return actualGrid;
	}

	public boolean startGame(){
		if(numberRows>=2 && numberRows<=30 && numberCols<=30 &&
				numberCols>=2 && numberMines>=1 && numberMines<=150
				&& (numberRows*numberCols)>numberMines){
			timesNumberPressed = new int [numberRows][numberCols];
			for(int i=0; i<numberRows;i++)
				for(int j=0;j<numberCols;j++)
					timesNumberPressed[i][j] = 0;
			
			flaggedTiles = new boolean [numberRows][numberCols];
			for(int i=0; i<numberRows;i++)
				for(int j=0;j<numberCols;j++)
					flaggedTiles[i][j] = false;
			
			// Sets default for all tiles to be not exposed
			exposedTiles = new boolean[numberRows][numberCols];
			for(int i=0; i<numberRows;i++)
				for(int j=0;j<numberCols;j++)
					exposedTiles[i][j] = false;
	
			// Populates grid with mines in unique locations
			populateGridWithMines();
			
			// Populates grid with numbers relating to mines
			populateGridNumbers();
			return true;
		}
		else
			return false;
	}
	
	// Populates grid with mines in unique locations
	private void populateGridWithMines(){
		actualGrid = new String[numberRows][numberCols];
		for(int i=0;i<numberRows;i++)
			for(int j=0;j<numberCols;j++)
				actualGrid[i][j] = EMPTY;
		
		mineLocations = new int[2][numberMines];
		int curRow;
		int curCol;
		for(int i=0;i<numberMines;i++){
			boolean found;
			do{
				found = false;

				curRow = randgen.nextInt(numberRows);
				curCol = randgen.nextInt(numberCols);

				for(int j = 0;j<i;j++){
					if(curRow==mineLocations[0][j] && curCol==mineLocations[1][j])
					{
						found = true;
						break;
					}
				}
			} while(found);
			mineLocations[0][i] = curRow;
			mineLocations[1][i] = curCol;
			actualGrid[curRow][curCol] = MINE;
		}
		
	}
	
	// Once mines are set in the grid, put in the numbers corresponding to
	// the number of mines around that tile
	private void populateGridNumbers(){
		if(actualGrid==null)
			System.exit(NULL_EXIT_CODE);
		for(int i =0;i<numberRows;i++){
			for(int j=0; j<numberCols;j++){
				if(!actualGrid[i][j].equals(MINE)){
					//each tile either empty or mine so far
					int num = getNumberOfMines(i,j);
					if(num>0)
						actualGrid[i][j] = ""+num;		
				}
			}
		}
	}
	
	// Given the tile coordinate, return the number of mines around it
	private int getNumberOfMines(int row, int col){
		if(row==0) 
		{
			if(col==0)
				return isMine(0,1)+isMine(1,1)+isMine(1,0);
			else if(col==numberCols-1) 
				return isMine(0,numberCols-2)+isMine(1,numberCols-2)+isMine(1,numberCols-1);
			else 
				return isMine(0,col-1)+isMine(0,col+1)+isMine(1,col-1)+
						isMine(1,col)+isMine(1,col+1);
		}
		else if(row==numberRows-1)
		{
			if(col==0)
				return isMine(numberRows-2,0)+isMine(numberRows-2,1)+isMine(numberRows-1,1);
			else if(col==numberCols-1)
				return isMine(numberRows-2,numberCols-1)+isMine(numberRows-2,numberCols-2)+
						isMine(numberRows-1,numberCols-2);
			else
				return isMine(numberRows-1,col-1)+isMine(numberRows-1,col+1)+
						isMine(numberRows-2,col-1)+
						isMine(numberRows-2,col)+isMine(numberRows-2,col+1);
		}
		else if(col==0) 
			return isMine(row-1,col)+isMine(row+1,col)+isMine(row-1,col+1)+
					isMine(row,col+1)+isMine(row+1,col+1);
		else if(col == numberCols-1)
			return isMine(row-1,numberCols-1)+isMine(row+1,numberCols-1)+
					isMine(row-1,numberCols-2)+
					isMine(row,numberCols-2)+isMine(row+1,numberCols-2);
		else
		{
			return isMine(row-1,col-1)+isMine(row-1,col)+isMine(row-1,col+1)+
					isMine(row,col-1)+isMine(row,col+1)+
					isMine(row+1,col-1)+isMine(row+1,col)+isMine(row+1,col+1);
		}
	}






	
}
