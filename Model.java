import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
//keep track of most of the data in the minesweeper program
public class Model implements ControllerToModel{
    //constants for the game: may have to change View constants, too,
    //if these are to be changed (or add more get functions to get
    //these values to the View)
    private final ArrayList<String> DIFFICULTIES = new ArrayList<String>(Arrays.asList("beginner","intermediate","expert","custom"));
    private final int BEGINNERMINES = 10;
    private final int INTERMEDIATEMINES = 40;
    private final int EXPERTMINES = 99;

    //keep track of game data while this instance of the model is
    //not exited
    private static long gamesPlayed = 1;
    private static long gamesWon = 0;
    private static long bestTimeSecondsBeg = 0;
    private static long bestTimeSecondsInter = 0;
    private static long bestTimeSecondsExpert = 0;
    private static long bestTimeSecondsCustom = 0;

    //keeps track of custom game settings the user sets
    private int customMines = 10;
    private int customRows = 9;
    private int customCols = 9;

    private int difficultyIndex; //beginner=0, intermediate=1, expert=2,custom=3
    private int numberMines;
    private int numberRows;
    private int numberCols;
    private int extraLivesLeft; //starts with value -1

    //helps determine if a tile with a number is being pressed for the
    //first time, or if it is being pressed for autocompletion
    private int [][] timesNumberPressed;
    //helps keep track of which tiles the user can see the values of
    private boolean[][] exposedTiles;
    //keeps track of the values of the tiles on the grid
    //(actual placements of mines, empties, numbers)
    private String [][] actualGrid;
    //keeps track of the locations of the mines in this grid corresponding
    //to actualGrid
    private int[] [] mineLocations;
    //keeps track of the mines the user hit but did not flag
    //used mostly with extra lives, starts as [[-1,-1],[-1,-1],[-1,-1]]
    private int[][] minesHit;
    //keeps track of which tiles the user flagged
    private boolean [][] flaggedTiles;
    //keeps track of the button that was last revealed/pressed by the user
    //starts with values [-1,-1]
    private int[] lastpressed;
    private boolean won;
    private boolean lost;

    //random generator (based on the time) to generate the grid of tiles
    private Random randgen;

    public Model()
    {
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

    public void setDifficulty(String diff)
    {
        if(diff==null)
            System.exit(NULL_EXIT_CODE);
        switch(diff)
        {
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

    //user wants to play a custom game, change rows
    public void setCustomRows(int rows)
    {
        if(rows>=2 && rows<=30)
            customRows = rows;
    }

    //user wants to play a custom game, change cols
    public void setCustomColumns(int cols)
    {
        if(cols>=2 && cols<=30)
            customCols = cols;
    }

    //user wants to play a custom game, change mines
    public void setCustomMines(int mines)
    {
        if(mines>=1 && mines<=150)
            customMines = mines;
    }

    //play another game, reset game data
    public void resetGame()
    {
        //another game being played
        gamesPlayed += 1;

        //new random generator
        randgen = new Random(System.currentTimeMillis());

        //reset difficulty to default
        numberMines = BEGINNERMINES;
        numberRows = 9;
        numberCols = 9;
        difficultyIndex = 0;

        //reset last tile pressed
        lastpressed = new int[2];
        lastpressed[0] = -1;
        lastpressed[1] = -1;

        //reset win/loss
        won = false;
        lost = false;
        extraLivesLeft = -1;
        minesHit = new int[3][2];
        minesHit[0][0] = lastpressed[0];  minesHit[0][1] = lastpressed[1];
        minesHit[1][0] = lastpressed[0];  minesHit[1][1] = lastpressed[1];
        minesHit[2][0] = lastpressed[0];  minesHit[2][1] = lastpressed[1];

        //reset custom
        customMines = 10;
        customRows = 9;
        customCols = 9;
    }

    public int getNumMines()
    {
        return numberMines;
    }

    public boolean [][] getExposed()
    {
        if(exposedTiles==null)
            System.exit(NULL_EXIT_CODE);
        return exposedTiles;
    }

    public int[] getLastPressed()
    {
        if(lastpressed==null)
            System.exit(NULL_EXIT_CODE);
        return lastpressed;
    }

    //user wants to change the number of extra lives
    public void setExtraLives(int lives)
    {
        if(extraLivesLeft<=3 && extraLivesLeft>=-1)
            extraLivesLeft = lives;
    }

    public String getBestTimes()
    {
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

    public int getExtraLivesLeft()
    {
        return extraLivesLeft;
    }

    public String [][] getGrid()
    {
        if(actualGrid==null)
            System.exit(NULL_EXIT_CODE);
        return actualGrid;
    }

    public boolean startGame()
    {
        if(numberRows>=2 && numberRows<=30 && numberCols<=30 &&
                numberCols>=2 && numberMines>=1 && numberMines<=150
                && (numberRows*numberCols)>numberMines)
        {
            timesNumberPressed = new int [numberRows][numberCols];
            for(int i=0; i<numberRows;i++)
                for(int j=0;j<numberCols;j++)
                    timesNumberPressed[i][j] = 0;

            flaggedTiles = new boolean [numberRows][numberCols];
            for(int i=0; i<numberRows;i++)
                for(int j=0;j<numberCols;j++)
                    flaggedTiles[i][j] = false;

            //set default for all tiles to be not exposed
            exposedTiles = new boolean[numberRows][numberCols];
            for(int i=0; i<numberRows;i++)
                for(int j=0;j<numberCols;j++)
                    exposedTiles[i][j] = false;

            //populate grid with mines in unique locations
            populateGridWithMines();

            //populate grid with numbers relating to mines
            populateGridNumbers();
            return true;
        }
        else
            return false;
    }

    //populate grid with mines in unique locations
    private void populateGridWithMines()
    {
        actualGrid = new String[numberRows][numberCols];
        for(int i=0;i<numberRows;i++)
            for(int j=0;j<numberCols;j++)
                actualGrid[i][j] = EMPTY;

        mineLocations = new int[2][numberMines];
        int curRow;
        int curCol;
        for(int i=0;i<numberMines;i++)
        {
            boolean found;
            do
            {
                found = false;

                curRow = randgen.nextInt(numberRows);
                curCol = randgen.nextInt(numberCols);

                for(int j = 0;j<i;j++)
                {
                    if(curRow==mineLocations[0][j] && curCol==mineLocations[1][j])
                    {
                        found = true;
                        break;
                    }
                }
            }while(found);
            mineLocations[0][i] = curRow;
            mineLocations[1][i] = curCol;
            actualGrid[curRow][curCol] = MINE;
        }

    }

    //once mines are set in the grid, put in the numbers corresponding to
    //how many mines surround that tile
    private void populateGridNumbers()
    {
        if(actualGrid==null)
            System.exit(NULL_EXIT_CODE);
        for(int i =0;i<numberRows;i++)
        {
            for(int j=0; j<numberCols;j++)
            {
                if(!actualGrid[i][j].equals(MINE))
                {
                    //each tile either empty or mine so far
                    int num = getNumberOfMines(i,j);
                    if(num>0)
                        actualGrid[i][j] = ""+num;
                }
            }
        }
    }

    //given the tile in this row and this col, return the number of mines
    //surrounding it
    private int getNumberOfMines(int row, int col)
    {
        if(row==0)//row on edge of table
        {
            if(col==0)//check [0][1],[1][1],[1][0] for mines
                return isMine(0,1)+isMine(1,1)+isMine(1,0);
            else if(col==numberCols-1) //check [0][numberCols-2],[1][numberCols-2],[1][numberCols-1]
                return isMine(0,numberCols-2)+isMine(1,numberCols-2)+isMine(1,numberCols-1);
            else //col is not on edge: check all but top 3
                return isMine(0,col-1)+isMine(0,col+1)+isMine(1,col-1)+
                        isMine(1,col)+isMine(1,col+1);
        }
        else if(row==numberRows-1)//row,col on edge of table
        {
            if(col==0)//check [numberRows-2][0],[numberRows-2][1],[numberRows-1][1]
                return isMine(numberRows-2,0)+isMine(numberRows-2,1)+isMine(numberRows-1,1);
            else if(col==numberCols-1)//check [numberRows-2][numberCols-1],[numberRows-2][numberCols-2],[numberRows-1][numbercols-2]
                return isMine(numberRows-2,numberCols-1)+isMine(numberRows-2,numberCols-2)+
                        isMine(numberRows-1,numberCols-2);
            else//check all but bottom 3
                return isMine(numberRows-1,col-1)+isMine(numberRows-1,col+1)+
                        isMine(numberRows-2,col-1)+
                        isMine(numberRows-2,col)+isMine(numberRows-2,col+1);
        }
        else if(col==0) //row is not on edge of table
            //check all but left 3
            return isMine(row-1,col)+isMine(row+1,col)+isMine(row-1,col+1)+
                    isMine(row,col+1)+isMine(row+1,col+1);
        else if(col == numberCols-1) //row is not on edge of table
            //check all but right 3
            return isMine(row-1,numberCols-1)+isMine(row+1,numberCols-1)+
                    isMine(row-1,numberCols-2)+
                    isMine(row,numberCols-2)+isMine(row+1,numberCols-2);
        else //not on edge of table
        {
            return isMine(row-1,col-1)+isMine(row-1,col)+isMine(row-1,col+1)+
                    isMine(row,col-1)+isMine(row,col+1)+
                    isMine(row+1,col-1)+isMine(row+1,col)+isMine(row+1,col+1);
        }
    }

    //return 1 if tile at row,col is a mine, 0 otherwise
    private int isMine(int row, int col)
    {
        if(actualGrid==null)
            System.exit(NULL_EXIT_CODE);
        if(actualGrid[row][col].equals(MINE))
            return 1;
        else
            return 0;
    }

    //if flagged == true, tile at row,col has been flagged; if false, unflagged
    public void tileFlagged(boolean flagged,int row, int col)
    {
        if(flaggedTiles==null)
            System.exit(NULL_EXIT_CODE);
        flaggedTiles[row][col] = flagged;
//        debugPrintState("tileFlagged @ (" + row + "," + col + ")");
    }

    public ArrayList<String> getDifficulties()
    {
        if(DIFFICULTIES==null)
            System.exit(NULL_EXIT_CODE);
        return DIFFICULTIES;
    }

    //returns true if player lost, false otherwise
    public boolean playerLost()
    {
        return lost;
    }

    //returns true if player won, false otherwise
    //need playerLost and playerWon b/c the player could do either
    //or neither (not both)
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

    //return true if tile at row,col is a mine that was previously hit
    //return false otherwise
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

    //a tile was pressed at row,col at currentTime
    //fill in the tiles based on what was pressed
    //if the player won, update gamesWon and the best times
    //return exposedTiles
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
//        debugPrintState("tilePressed @ (" + row + "," + col + ")");
        return exposedTiles;
    }

    //tile needs to be filled in at row,col
    //playerPressed is true if player pressed a button to fill out tiles
    //false if auto filling out tiles
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
        if(exposedTiles[row][col] == true && !numbers.contains(actualGrid[row][col])) //already did this tile, not a number
            return;

        exposedTiles[row][col] = true;

        if(isMine(row,col)==1 && flaggedTiles[row][col]==false) //this tile is a mine and not flagged
        { //if auto complete presses the mine, flag was incorrect and it is still player's fault
            if(extraLivesLeft>0)
            {
                extraLivesLeft=extraLivesLeft-1;
                minesHit[extraLivesLeft][0] = row;
                minesHit[extraLivesLeft][1] = col;
                if(!playerPressed) //player placed incorrect flag and tried to autocomplete
                {
                    lastpressed[0] = row; //make lastpressed the position of the mine that was uncovered
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
        else if(actualGrid[row][col].equals(EMPTY)) //if this is an empty tile
        {
            //fill out all surrounding tiles
            for(int i = 0;i<3;i++)
            {
                fillOutTiles(false,row+1,col-1+i);
                fillOutTiles(false,row,col-1+i);
                fillOutTiles(false,row-1,col-1+i);
            }
        }
        else if(numbers.contains(actualGrid[row][col])) //if this is a number
        {
            if(timesNumberPressed[row][col]==0) //initial hit, don't do anything
                timesNumberPressed[row][col]=1;

                //hit again, display surrounding tiles if all "mines" flagged (corresponding to numMines)
            else if(timesNumberPressed[row][col]==1 && playerPressed)
            {
                int numMines = getNumberOfMines(row,col);
                int currentFlaggedOrHitMines = 0;
                for(int i = 0;i<3;i++)
                {
                    //if mine is flagged or hit - not care if it is actually a mine (&& isMine(row+1,col-1+i)==1)
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
                    //row,col will not be mine b/c number
                    //so just checks row,col-1 & row,col+1
                    {
                        currentFlaggedOrHitMines++;
                    }
                }

                //display all surrounding tiles if all "mines" flagged
                if(currentFlaggedOrHitMines>=numMines) //> just in case
                {
                    for(int i = 0;i<3;i++)
                    {
                        //fill out all surrounding tiles
                        fillOutTiles(false,row+1,col-1+i);
                        fillOutTiles(false,row,col-1+i);
                        fillOutTiles(false,row-1,col-1+i);
                    }
                }

            }
        }

        won = allTilesFilledOut();
    }

    //return true if user has won, false otherwise
    private boolean allTilesFilledOut()
    {
        if(exposedTiles==null||flaggedTiles==null)
            System.exit(NULL_EXIT_CODE);
        //for each tile
        for(int i = 0; i<numberRows;i++)
        {
            for(int j = 0;j<numberCols;j++)
            {   //if the tile is not exposed
                if(exposedTiles[i][j]==false)
                {	//if it is not a flagged mine, return false
                    if(flaggedTiles[i][j]==false && isMine(i,j)==0)
                        return false;
                }
            }
        }
        return true;
    }

    private int[] findBestMine() {
        int bestRow = -1, bestCol = -1, bestGain = 0;

        for (int m = 0; m < numberMines; m++) {
            int r = mineLocations[0][m], c = mineLocations[1][m];
            if (exposedTiles[r][c] || flaggedTiles[r][c]) continue;

             /* ── the mine is a valid hint only if at least
       three surrounding NON-mine squares are already revealed. ── */
            int exposedNbrs = 0;
            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    if (dr == 0 && dc == 0) continue;                // skip self
                    int nr = r + dr, nc = c + dc;
                    if (nr < 0 || nr >= numberRows ||
                            nc < 0 || nc >= numberCols) continue;        // off board
                    if (isMine(nr, nc) == 0 && exposedTiles[nr][nc]) // non-mine & shown
                        exposedNbrs++;
                }
            }
            if (exposedNbrs < 3) continue;          //  ←  **skip this mine**

            // work on copies
            boolean[][] ex   = deepCopy(exposedTiles);
            boolean[][] flg  = deepCopy(flaggedTiles);
            int[][]     prs  = deepCopy(timesNumberPressed);
            flg[r][c] = true;                                // pretend to flag

            int delta;
            do {
                int before = countTrue(ex);
                expandOnce(actualGrid, ex, flg, prs);
                delta = countTrue(ex) - before;
            } while (delta > 0);

            int gain = countTrue(ex) - countTrue(exposedTiles);
            if (gain > bestGain) { bestGain = gain; bestRow = r; bestCol = c; }
        }
        return new int[]{bestRow, bestCol};
    }

    /**
     * Flags the mine that would unlock the largest expansion wave and
     * returns its coordinate {row,col}.  {-1,-1} if nothing left.
     */
    public int[] applyHint() {
        int[] p = findBestMine();
        if (p[0] == -1) return p;             // no mine found

        tileFlagged(true, p[0], p[1]);              // updates flaggedTiles & HUD data

        // if every mine is now flagged, declare victory
        if (countFlags() == numberMines) won = true;

        return p;
    }

// debug helper
    /**
     * Dumps the current contents of all per-cell arrays so that you can follow
     * how a single user action changed the model’s state.
     *
     * @param tag short label that identifies the triggering event
     */
    private void debugPrintState(String tag) {
        System.out.println("\n===== " + tag + " =====");
        System.out.println("actualGrid:");
        for (String[] r : actualGrid)   System.out.println(java.util.Arrays.toString(r));

        System.out.println("exposedTiles:");
        for (boolean[] r : exposedTiles) System.out.println(java.util.Arrays.toString(r));

        System.out.println("flaggedTiles:");
        for (boolean[] r : flaggedTiles) System.out.println(java.util.Arrays.toString(r));

        System.out.println("timesNumberPressed:");
        for (int[] r : timesNumberPressed) System.out.println(java.util.Arrays.toString(r));

//		System.out.println("extraLivesLeft = " + extraLivesLeft);
//		System.out.println("won=" + won + ", lost=" + lost);
        System.out.println("========================================\n");
    }

    // ── generic deep-copy helpers ────────────────────────────────────────────
    /* ────────────────────────────────────────────────────────────── */
    /*  Deep-copy a 2-D reference array without triggering            */
    /*  ‘generic array creation’ compile errors.                      */
    /*  Works for any T because each row is cloned individually.      */
    /* ────────────────────────────────────────────────────────────── */
    private static <T> T[][] deepCopy(T[][] src) {
        if (src == null) return null;

        @SuppressWarnings("unchecked")
        T[][] dest = src.clone();          // shallow clone of outer array
        for (int i = 0; i < src.length; i++)
            dest[i] = src[i].clone();      // deep clone each row

        return dest;
    }

    private static boolean[][] deepCopy(boolean[][] src) {
        return java.util.Arrays.stream(src).map(boolean[]::clone).toArray(boolean[][]::new);
    }
    private static int[][] deepCopy(int[][] src) {
        return java.util.Arrays.stream(src).map(int[]::clone).toArray(int[][]::new);
    }
    private static int countTrue(boolean[][] a) {
        int n = 0; for (boolean[] r : a) for (boolean b : r) if (b) n++; return n;
    }
    private int countFlags() { return countTrue(flaggedTiles); }

    /* ──────────────────────────────────────────────────────────────── */
    /*  count how many adjacent squares are flagged        */
    private int countAdjacentFlags(int row, int col, boolean[][] flg) {
        int cnt = 0;
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;          // skip self
                int nr = row + dr, nc = col + dc;
                if (nr < 0 || nr >= numberRows ||
                        nc < 0 || nc >= numberCols) continue;  // off board
                if (flg[nr][nc]) cnt++;
            }
        }
        return cnt;
    }

    /* ──────────────────────────────────────────────────────────────── */
    /*  reveal the 8-neighbour “ring” around a number      */
    private void revealRing(int row, int col,
                            String[][] g,      // immutable truth grid
                            boolean[][] ex,    // exposedTiles COPY
                            boolean[][] flg,   // flaggedTiles COPY
                            int[][] press) {   // timesNumberPressed COPY
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;
                int nr = row + dr, nc = col + dc;
                if (nr < 0 || nr >= numberRows ||
                        nc < 0 || nc >= numberCols) continue;

                if (ex[nr][nc] || flg[nr][nc]) continue;   // already open or flagged
                ex[nr][nc] = true;                         // reveal neighbour
                press[nr][nc] = 1;                         // mark one click

                // flood-fill further if it was a blank square
                if (g[nr][nc].equals(EMPTY))
                    revealRing(nr, nc, g, ex, flg, press);
            }
        }
    }


    /* ──────────────────────────────────────────────────────────────── */
    /*  – one global sweep looking for auto-open candidates  */
    private void expandOnce(String[][] g, boolean[][] ex,
                            boolean[][] flg, int[][] press) {

        for (int r = 0; r < numberRows; r++) {
            for (int c = 0; c < numberCols; c++) {
                if (!ex[r][c]) continue;                    // number must be visible
                char ch = g[r][c].charAt(0);
                if (ch < '1' || ch > '8') continue;         // not a number tile

                int need = ch - '0';
                int have = countAdjacentFlags(r, c, flg);
                if (have >= need) {
                    // mimic the user’s second click on that number
                    revealRing(r, c, g, ex, flg, press);
                }
            }
        }
    }

    @Override
    public int getNumFlags() {
        return countFlags();          // countFlags() helper already exists
    }

}
