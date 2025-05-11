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
	
	// Returns 1 if tile at (row, col) is a mine, 0 otherwise
	private int isMine(int row, int col)
	{
		if(actualGrid==null)
			System.exit(NULL_EXIT_CODE);
		if(actualGrid[row][col].equals(MINE))
			return 1;
		else
			return 0;
	}
	
	// If flagged == true, tile at (row, col) has been flagged; if false, unflagged
	public void tileFlagged(boolean flagged,int row, int col)
	{
		if(flaggedTiles==null)
			System.exit(NULL_EXIT_CODE);
		flaggedTiles[row][col] = flagged;
	}
	
	public ArrayList<String> getDifficulties()
	{
		if(DIFFICULTIES==null)
			System.exit(NULL_EXIT_CODE);
		return DIFFICULTIES;
	}
	
	// Returns true if player lost, false otherwise
	public boolean playerLost()
	{
		return lost;
	}
	
	// Returns true if player won, false otherwise
	// need playerLost and playerWon b/c the player could do either
	// or neither (not both)
	public boolean playerWon()
	{
		return won;
	}
	
	public long getTotalGamesPlayed()
	{
		return gamesPlayed;
	}
	
	public long getTotalGamesWon()
	{
		return gamesWon;
	}
	
	// Returns true if tile at (row, col) is a mine that was previously hit
	private boolean minePreviouslyHit(int row, int col)
	{
		if(minesHit==null)
			System.exit(NULL_EXIT_CODE);
		if( (minesHit[0][0] == row && minesHit[0][1] == col) ||
				(minesHit[1][0]==row && minesHit[1][1]==col) ||
				(minesHit[2][0]==row && minesHit[2][1]==col) )
			return true;
		else
			return false;
	}
	
	// A tile was chosen at (row, col) at currentTime
	// fill in the tiles based on what was pressed
	// if the player won, update gamesWon and the best time
	// return exposedTiles
	public boolean [][] tilePressed(int row, int col, long currentTime)
	{
		if(exposedTiles==null)
			System.exit(NULL_EXIT_CODE);
		fillOutTiles(true,row,col);
		if(won)
		{
			gamesWon += 1;
			switch(difficultyIndex)
			{
			case 0:
				if(bestTimeSecondsBeg>currentTime || bestTimeSecondsBeg == 0)
					bestTimeSecondsBeg = currentTime;
				break;
			case 1:
				if(bestTimeSecondsInter>currentTime || bestTimeSecondsInter == 0)
					bestTimeSecondsInter = currentTime;
				break;
			case 2:
				if(bestTimeSecondsExpert>currentTime || bestTimeSecondsExpert == 0)
					bestTimeSecondsExpert = currentTime;
				break;
			//custom
			default:
				if(bestTimeSecondsCustom>currentTime || bestTimeSecondsCustom == 0)
					bestTimeSecondsCustom = currentTime;
				break;
			}
			
		}
		return exposedTiles;
	}
	
	// Tile needs to be filled in at (row, col)
	// playerPressed is true if player pressed a button to fill out tiles
	// false if auto filling out tiles
	private void fillOutTiles(boolean playerPressed,int row, int col)
	{
		if(timesNumberPressed==null||minesHit==null||lastpressed==null||
				exposedTiles==null||flaggedTiles==null||actualGrid==null)
			System.exit(NULL_EXIT_CODE);
		if(playerPressed)
		{
			lastpressed[0] = row;
			lastpressed[1] = col;
		}
		String numbers = "12345678";
		if(row<0||col<0||row>numberRows-1||col>numberCols-1)
			return;
		if(exposedTiles[row][col] == true && !numbers.contains(actualGrid[row][col]))
			return;
		
		exposedTiles[row][col] = true;
		
		if(isMine(row,col)==1 && flaggedTiles[row][col]==false){ // The tile is a mine and has not been flagged
		// If auto complete presses the mine, flag was incorrect and it is still player's fault
			if(extraLivesLeft>0)
			{
				extraLivesLeft=extraLivesLeft-1;
				minesHit[extraLivesLeft][0] = row;
				minesHit[extraLivesLeft][1] = col;
				if(!playerPressed) // Player placed incorrect flag and tried to autocomplete
				{
					lastpressed[0] = row; // Makes lastpressed the position of the mine that was revealed
					lastpressed[1] = col;
				}
			}
			else
			{
				lost = true;
				if(!playerPressed)
				{
					lastpressed[0] = row;
					lastpressed[1] = col;
				}
			}
			
		}
		else if(actualGrid[row][col].equals(EMPTY))
		{
			// Fill out all surrounding tiles if this is an empty tile
			for(int i = 0;i<3;i++)
			{
				fillOutTiles(false,row+1,col-1+i);
				fillOutTiles(false,row,col-1+i);
				fillOutTiles(false,row-1,col-1+i);
			}
		}
		else if(numbers.contains(actualGrid[row][col])) // If this is a number
		{
			if(timesNumberPressed[row][col]==0) //Initial hit no action
				timesNumberPressed[row][col]=1;
			
			// Hit again, display surrounding tiles if all "mines" flagged (corresponding to numMines)
			else if(timesNumberPressed[row][col]==1 && playerPressed)
			{
				int numMines = getNumberOfMines(row,col);
				int currentFlaggedOrHitMines = 0;
				for(int i = 0;i<3;i++)
				{
					// If mine is flagged or hit - not care if it is actually a mine (&& isMine(row+1,col-1+i)==1)
					if(row+1<numberRows && col-1+i<numberCols &&
							col-1+i>=0 && (flaggedTiles[row+1][col-1+i]==true || minePreviouslyHit(row+1,col-1+i)))
					{
						currentFlaggedOrHitMines++;
					}
					if(row-1>=0 && col-1+i<numberCols &&
							col-1+i>=0 && (flaggedTiles[row-1][col-1+i]==true || minePreviouslyHit(row-1,col-1+i)))
					{
						currentFlaggedOrHitMines++;
					}
					if(col-1+i<numberCols && col-1+i>=0 && i!=1 &&
							(flaggedTiles[row][col-1+i]==true || minePreviouslyHit(row,col-1+i))) 
	
					{
						currentFlaggedOrHitMines++;				
					}
				}
				
				// Displays all surrounding tiles if all "mines" flagged
				if(currentFlaggedOrHitMines>=numMines){
					for(int i = 0;i<3;i++){
						// Fills out all surrounding tiles
						fillOutTiles(false,row+1,col-1+i);
						fillOutTiles(false,row,col-1+i);
						fillOutTiles(false,row-1,col-1+i);
					}
				}
					
			}
		}
		
		won = allTilesFilledOut();
	}
	
	// Returs true if user has won, false otherwise
	private boolean allTilesFilledOut()
	{
		if(exposedTiles==null||flaggedTiles==null)
			System.exit(NULL_EXIT_CODE);
		for(int i = 0; i<numberRows;i++){
			for(int j = 0;j<numberCols;j++){
			    // If the tile is not exposed 
				if(exposedTiles[i][j]==false){
				 	// If it is not a flagged mine, return false
					if(flaggedTiles[i][j]==false && isMine(i,j)==0)
						return false;
				}
			}
		}
		return true;
	}
}
