import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;


/**
 * The Model class maintains the game state and implements the core game logic for Minesweeper.
 * Handles the creation of the mine grid, processes player actions, and determines game outcomes.
 * Implements the ControllerToModel interface to communicate with the Controller.
 */
public class Model implements ControllerToModel {
    // Game difficulty settings and corresponding mine counts
    private final ArrayList<String> DIFFICULTIES = new ArrayList<String>(Arrays.asList("beginner", "intermediate", "expert", "custom"));
    private final int BEGINNERMINES = 10;
    private final int INTERMEDIATEMINES = 40;
    private final int EXPERTMINES = 99;

    // Game statistics
    private static long gamesPlayed = 1;
    private static long gamesWon = 0;
    private static long bestTimeSecondsBeg = 0;
    private static long bestTimeSecondsInter = 0;
    private static long bestTimeSecondsExpert = 0;
    private static long bestTimeSecondsCustom = 0;

    // Custom game settings
    private int customMines = 10;
    private int customRows = 9;
    private int customCols = 9;

    // Current game settings
    private int difficultyIndex; // beginner=0, intermediate=1, expert=2, custom=3
    private int numberMines;
    private int numberRows;
    private int numberCols;
    private int extraLivesLeft; // starts with value -1
    private boolean logicalMode = false;

    // Internal state tracking
    private int[][] timesNumberPressed;
    private boolean[][] exposedTiles;
    private String[][] actualGrid;
    private int[][] mineLocations;
    private int[][] minesHit;
    private boolean[][] flaggedTiles;
    private int[] lastpressed;
    private boolean won;
    private boolean lost;
    private Random randgen;

    /**
     * Constructs a new Model with default beginner settings and initializes game state.
     */	
    public Model() {
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
        minesHit[0][0] = lastpressed[0];
        minesHit[0][1] = lastpressed[1];
        minesHit[1][0] = lastpressed[0];
        minesHit[1][1] = lastpressed[1];
        minesHit[2][0] = lastpressed[0];
        minesHit[2][1] = lastpressed[1];
    }

    /**
     * Sets the game difficulty and corresponding board size and mine count.
     *
     * @param diff Difficulty string ("beginner", "intermediate", "expert", "custom")
     */	
    public void setDifficulty(String diff) {
        if (diff == null)
            System.exit(NULL_EXIT_CODE);
        switch (diff) {
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

    /**
     * Enables or disables logical mode. In logical mode, all games are solvable without guessing.
     *
     * @param b True to enable logical mode, false to disable
     */	
    @Override
    public void setLogicalMode(boolean b) {
        logicalMode = b;
    }

    /**
     * Returns whether logical mode is currently enabled.
     *
     * @return True if logical mode is enabled
     */	
    @Override
    public boolean getLogicalMode() {
        return logicalMode;
    }

    /**
     * Sets custom number of rows for a custom game configuration.
     *
     * @param rows Number of rows
     */
    public void setCustomRows(int rows) {
        if (rows >= 2 && rows <= 30)
            customRows = rows;
    }

    /**
     * Sets custom number of columns for a custom game configuration.
     *
     * @param cols Number of columns
     */
    public void setCustomColumns(int cols) {
        if (cols >= 2 && cols <= 30)
            customCols = cols;
    }

    /**
     * Sets custom number of mines for a custom game configuration.
     *
     * @param mines Number of mines
     */
    public void setCustomMines(int mines) {
        if (mines >= 1 && mines <= 150)
            customMines = mines;
    }

    /**
     * Resets the game state to prepare for a new game.
     */
    public void resetGame() {
        // Another game being played
        gamesPlayed += 1;

        // New random generator
        randgen = new Random(System.currentTimeMillis());

        // Reset difficulty to default
        numberMines = BEGINNERMINES;
        numberRows = 9;
        numberCols = 9;
        difficultyIndex = 0;

        // Reset last tile pressed
        lastpressed = new int[2];
        lastpressed[0] = -1;
        lastpressed[1] = -1;

        // Reset win/loss
        won = false;
        lost = false;
        extraLivesLeft = -1;
        minesHit = new int[3][2];
        minesHit[0][0] = lastpressed[0];
        minesHit[0][1] = lastpressed[1];
        minesHit[1][0] = lastpressed[0];
        minesHit[1][1] = lastpressed[1];
        minesHit[2][0] = lastpressed[0];
        minesHit[2][1] = lastpressed[1];

        // Reset custom
        customMines = 10;
        customRows = 9;
        customCols = 9;
    }

    /**
     * Returns the number of mines currently set for the game.
     *
     * @return Number of mines
     */
    public int getNumMines() {
        return numberMines;
    }

    /**
     * Returns the current exposed tiles grid.
     *
     * @return 2D boolean array representing exposed tiles
     */
    public boolean[][] getExposed() {
        if (exposedTiles == null)
            System.exit(NULL_EXIT_CODE);
        return exposedTiles;
    }
	
    /**
     * Returns the coordinates of the last tile pressed.
     *
     * @return Integer array with row and column of last pressed tile
     */
    public int[] getLastPressed() {
        if (lastpressed == null)
            System.exit(NULL_EXIT_CODE);
        return lastpressed;
    }

    /**
     * Sets the number of extra lives the player has.
     *
     * @param lives Number of extra lives
     */
    public void setExtraLives(int lives) {
        if (extraLivesLeft <= 3 && extraLivesLeft >= -1)
            extraLivesLeft = lives;
    }

    /**
     * Returns a string representation of the best times for all difficulty levels.
     *
     * @return String containing best times per difficulty
     */
    public String getBestTimes() {
        String str = "";
        if (difficultyIndex == 0 || bestTimeSecondsBeg > 0)
            str += ("Beginner best time: " + bestTimeSecondsBeg + " seconds\n");
        if (difficultyIndex == 1 || bestTimeSecondsInter > 0)
            str += ("Intermediate best time: " + bestTimeSecondsInter + " seconds\n");
        if (difficultyIndex == 2 || bestTimeSecondsExpert > 0)
            str += ("Expert best time: " + bestTimeSecondsExpert + " seconds\n");
        if (difficultyIndex == 3 || bestTimeSecondsCustom > 0)
            str += ("Custom best time: " + bestTimeSecondsCustom + " seconds\n");

        return str;
    }

    /**
     * Returns the number of extra lives remaining.
     *
     * @return Extra lives left
     */
    public int getExtraLivesLeft() {
        return extraLivesLeft;
    }

    /**
     * Returns the current game grid.
     *
     * @return 2D array representing the game grid
     */
    public String[][] getGrid() {
        if (actualGrid == null)
            System.exit(NULL_EXIT_CODE);
        return actualGrid;
    }

    /**
     * Initializes the game state and generates a new board based on current settings.
     *
     * @return True if game successfully started, false otherwise
     */
    public boolean startGame() {
        if (numberRows >= 2 && numberRows <= 30 && numberCols <= 30 &&
                numberCols >= 2 && numberMines >= 1 && numberMines <= 150
                && (numberRows * numberCols) > numberMines) {
            timesNumberPressed = new int[numberRows][numberCols];
            for (int i = 0; i < numberRows; i++)
                for (int j = 0; j < numberCols; j++)
                    timesNumberPressed[i][j] = 0;

            flaggedTiles = new boolean[numberRows][numberCols];
            for (int i = 0; i < numberRows; i++)
                for (int j = 0; j < numberCols; j++)
                    flaggedTiles[i][j] = false;

            // Set default for all tiles to be not exposed
            exposedTiles = new boolean[numberRows][numberCols];
            for (int i = 0; i < numberRows; i++)
                for (int j = 0; j < numberCols; j++)
                    exposedTiles[i][j] = false;

            // Populate grid with mines in unique locations
            populateGridWithMines();

            // Populate grid with numbers relating to mines
            populateGridNumbers();

            /* ───────────────── LOGICAL-MODE GUARANTEE ─────────────── */
            if (logicalMode) {

                int boardsTried = 0;                              // how many mine-layouts
                outer:
                while (++boardsTried <= 500) {             // ← try ≤ 500 boards

                    /* ========== ①  collect all connected zero-regions ========== */
                    java.util.List<java.util.List<int[]>> regions = new java.util.ArrayList<>();
                    boolean[][] seen = new boolean[numberRows][numberCols];

                    for (int r = 0; r < numberRows; r++) {
                        for (int c = 0; c < numberCols; c++) {
                            if (!seen[r][c] && actualGrid[r][c].equals(EMPTY)) {

                                java.util.List<int[]> block = new java.util.ArrayList<>();
                                java.util.ArrayDeque<int[]> q = new java.util.ArrayDeque<>();
                                q.add(new int[]{r, c});
                                seen[r][c] = true;

                                while (!q.isEmpty()) {
                                    int[] p = q.remove();
                                    block.add(p);
                                    for (int dr = -1; dr <= 1; dr++)
                                        for (int dc = -1; dc <= 1; dc++) {
                                            if ((dr | dc) == 0) continue;
                                            int nr = p[0] + dr, nc = p[1] + dc;
                                            if (nr < 0 || nr >= numberRows ||
                                                    nc < 0 || nc >= numberCols) continue;
                                            if (!seen[nr][nc] &&
                                                    actualGrid[nr][nc].equals(EMPTY)) {
                                                seen[nr][nc] = true;
                                                q.add(new int[]{nr, nc});
                                            }
                                        }
                                }
                                regions.add(block);
                            }
                        }
                    }
                    regions.sort((a, b) -> b.size() - a.size());      // largest first

                    /* ========== ②  try every zero-region on THIS board ========= */
                    int blockIdx = 0;
                    for (java.util.List<int[]> block : regions) {
                        blockIdx++;

                        /* fresh masks for this attempt */
                        for (boolean[] row : exposedTiles) java.util.Arrays.fill(row, false);
                        for (boolean[] row : flaggedTiles) java.util.Arrays.fill(row, false);
                        timesNumberPressed = new int[numberRows][numberCols];
                        won = lost = false;

                        int[] seed = block.get(0);                    // any zero in region
                        fillOutTiles(true, seed[0], seed[1]);         // one click → flood

                        /* abort this block if the flood touched a mine */
                        boolean mineRevealed = false;
                        for (int m = 0; m < numberMines && !mineRevealed; m++)
                            if (exposedTiles[mineLocations[0][m]][mineLocations[1][m]])
                                mineRevealed = true;

                        if (mineRevealed) continue;                   // next region

                        boolean[][] firstView = deepCopy(exposedTiles);   // save flood

                        boolean solved = logicalSolve();                  // deterministic step

                        if (solved) {
                            /* keep only the initial flood for the user */
                            exposedTiles = firstView;
                            for (boolean[] row : flaggedTiles) java.util.Arrays.fill(row, false);

                            won = false;
                            lost = false;

                            timesNumberPressed = new int[numberRows][numberCols];

                            break outer;
                        }
                    }

                    /* ========== ③  none of the blocks worked → new layout ====== */
                    populateGridWithMines();
                    populateGridNumbers();
                }

            }
            /* ───────────────────────────────────────────────────────── */


            return true;
        } else
            return false;
    }

    /**
     * Populates the grid with mines in unique random locations.
     */
    private void populateGridWithMines() {
        actualGrid = new String[numberRows][numberCols];
        for (int i = 0; i < numberRows; i++)
            for (int j = 0; j < numberCols; j++)
                actualGrid[i][j] = EMPTY;

        mineLocations = new int[2][numberMines];
        int curRow;
        int curCol;
        for (int i = 0; i < numberMines; i++) {
            boolean found;
            do {
                found = false;

                curRow = randgen.nextInt(numberRows);
                curCol = randgen.nextInt(numberCols);

                for (int j = 0; j < i; j++) {
                    if (curRow == mineLocations[0][j] && curCol == mineLocations[1][j]) {
                        found = true;
                        break;
                    }
                }
            } while (found);
            mineLocations[0][i] = curRow;
            mineLocations[1][i] = curCol;
            actualGrid[curRow][curCol] = MINE;
        }
    }

    /**
     * Populates the grid with numbers indicating how many mines surround each tile.
     */
    private void populateGridNumbers() {
        if (actualGrid == null)
            System.exit(NULL_EXIT_CODE);
        for (int i = 0; i < numberRows; i++) {
            for (int j = 0; j < numberCols; j++) {
                if (!actualGrid[i][j].equals(MINE)) {
                    // Each tile either empty or mine so far
                    int num = getNumberOfMines(i, j);
                    if (num > 0)
                        actualGrid[i][j] = "" + num;
                }
            }
        }
    }

    /**
     * Calculates the number of mines surrounding a specific tile.
     *
     * @param row Row index of the tile
     * @param col Column index of the tile
     * @return Number of mines surrounding the tile
     */
    private int getNumberOfMines(int row, int col) {
        if (row == 0) { // Row on edge of table
            if (col == 0) // Check [0][1],[1][1],[1][0] for mines
                return isMine(0, 1) + isMine(1, 1) + isMine(1, 0);
            else if (col == numberCols - 1) // Check [0][numberCols-2],[1][numberCols-2],[1][numberCols-1]
                return isMine(0, numberCols - 2) + isMine(1, numberCols - 2) + isMine(1, numberCols - 1);
            else // Col is not on edge: check all but top 3
                return isMine(0, col - 1) + isMine(0, col + 1) + isMine(1, col - 1) +
                        isMine(1, col) + isMine(1, col + 1);
        } else if (row == numberRows - 1) { // Row,col on edge of table
            if (col == 0) // Check [numberRows-2][0],[numberRows-2][1],[numberRows-1][1]
                return isMine(numberRows - 2, 0) + isMine(numberRows - 2, 1) + isMine(numberRows - 1, 1);
            else if (col == numberCols - 1) // Check [numberRows-2][numberCols-1],[numberRows-2][numberCols-2],[numberRows-1][numbercols-2]
                return isMine(numberRows - 2, numberCols - 1) + isMine(numberRows - 2, numberCols - 2) +
                        isMine(numberRows - 1, numberCols - 2);
            else // Check all but bottom 3
                return isMine(numberRows - 1, col - 1) + isMine(numberRows - 1, col + 1) +
                        isMine(numberRows - 2, col - 1) +
                        isMine(numberRows - 2, col) + isMine(numberRows - 2, col + 1);
        } else if (col == 0) // Row is not on edge of table
            // Check all but left 3
            return isMine(row - 1, col) + isMine(row + 1, col) + isMine(row - 1, col + 1) +
                    isMine(row, col + 1) + isMine(row + 1, col + 1);
        else if (col == numberCols - 1) // Row is not on edge of table
            // Check all but right 3
            return isMine(row - 1, numberCols - 1) + isMine(row + 1, numberCols - 1) +
                    isMine(row - 1, numberCols - 2) +
                    isMine(row, numberCols - 2) + isMine(row + 1, numberCols - 2);
        else // Not on edge of table
        {
            return isMine(row - 1, col - 1) + isMine(row - 1, col) + isMine(row - 1, col + 1) +
                    isMine(row, col - 1) + isMine(row, col + 1) +
                    isMine(row + 1, col - 1) + isMine(row + 1, col) + isMine(row + 1, col + 1);
        }
    }

    /**
     * Checks if a specific tile contains a mine.
     *
     * @param row Row index of the tile
     * @param col Column index of the tile
     * @return 1 if the tile is a mine, 0 otherwise
     */
    private int isMine(int row, int col) {
        if (actualGrid == null)
            System.exit(NULL_EXIT_CODE);
        if (actualGrid[row][col].equals(MINE))
            return 1;
        else
            return 0;
    }

    /**
     * Updates the flag status of a tile.
     *
     * @param flagged True if the tile is being flagged, false if unflagged
     * @param row Row index of the tile
     * @param col Column index of the tile
     */
    public void tileFlagged(boolean flagged, int row, int col) {
        if (flaggedTiles == null)
            System.exit(NULL_EXIT_CODE);
        flaggedTiles[row][col] = flagged;
    }

    /**
     * Returns the list of available difficulty options.
     *
     * @return ArrayList of difficulty strings
     */
    public ArrayList<String> getDifficulties() {
        if (DIFFICULTIES == null)
            System.exit(NULL_EXIT_CODE);
        return DIFFICULTIES;
    }

    /**
     * Checks if the player has lost the game.
     *
     * @return True if player has lost, false otherwise
     */
    public boolean playerLost() {
        return lost;
    }

    /**
     * Checks if the player has won the game.
     *
     * @return True if player has won, false otherwise
     */
    public boolean playerWon() {
        return won;
    }

    /**
     * Returns the total number of games played.
     *
     * @return Number of games played
     */
    public long getTotalGamesPlayed() {
        return gamesPlayed;
    }

    /**
     * Returns the total number of games won.
     *
     * @return Number of games won
     */
    public long getTotalGamesWon() {
        return gamesWon;
    }

    /**
     * Checks if a mine at the specified coordinates was previously hit.
     *
     * @param row Row index to check
     * @param col Column index to check
     * @return True if the mine was previously hit, false otherwise
     */
    private boolean minePreviouslyHit(int row, int col) {
        if (minesHit == null)
            System.exit(NULL_EXIT_CODE);
        if ((minesHit[0][0] == row && minesHit[0][1] == col) ||
                (minesHit[1][0] == row && minesHit[1][1] == col) ||
                (minesHit[2][0] == row && minesHit[2][1] == col))
            return true;
        else
            return false;
    }

    /**
     * Processes a tile press at the specified coordinates.
     * Updates the game state and returns the exposed tiles.
     *
     * @param row Row index of the pressed tile
     * @param col Column index of the pressed tile
     * @param currentTime Current game time in seconds
     * @return 2D boolean array of exposed tiles
     */
    public boolean[][] tilePressed(int row, int col, long currentTime) {
        if (exposedTiles == null)
            System.exit(NULL_EXIT_CODE);
        fillOutTiles(true, row, col);
        if (won) {
            gamesWon += 1;
            switch (difficultyIndex) {
                case 0:
                    if (bestTimeSecondsBeg > currentTime || bestTimeSecondsBeg == 0)
                        bestTimeSecondsBeg = currentTime;
                    break;
                case 1:
                    if (bestTimeSecondsInter > currentTime || bestTimeSecondsInter == 0)
                        bestTimeSecondsInter = currentTime;
                    break;
                case 2:
                    if (bestTimeSecondsExpert > currentTime || bestTimeSecondsExpert == 0)
                        bestTimeSecondsExpert = currentTime;
                    break;
                // Custom
                default:
                    if (bestTimeSecondsCustom > currentTime || bestTimeSecondsCustom == 0)
                        bestTimeSecondsCustom = currentTime;
                    break;
            }
        }
        return exposedTiles;
    }

    /**
     * Fills out tiles based on the game rules when a tile is pressed.
     * Handles mine hits, empty tiles, and number tiles differently.
     *
     * @param playerPressed True if player initiated the action, false for auto-fill
     * @param row Row index of the tile
     * @param col Column index of the tile
     */
    private void fillOutTiles(boolean playerPressed, int row, int col) {
        if (timesNumberPressed == null || minesHit == null || lastpressed == null ||
                exposedTiles == null || flaggedTiles == null || actualGrid == null)
            System.exit(NULL_EXIT_CODE);
        if (playerPressed) {
            lastpressed[0] = row;
            lastpressed[1] = col;
        }
        String numbers = "12345678";
        if (row < 0 || col < 0 || row > numberRows - 1 || col > numberCols - 1)
            return;
        if (exposedTiles[row][col] == true && !numbers.contains(actualGrid[row][col])) // Already did this tile, not a number
            return;

        exposedTiles[row][col] = true;

        if (isMine(row, col) == 1 && flaggedTiles[row][col] == false) { // This tile is a mine and not flagged
            // If auto complete presses the mine, flag was incorrect and it is still player's fault
            if (!playerPressed) {
                return; // AI uncovered a mine while auto-expanding – just ignore it
            }

            if (extraLivesLeft > 0) {
                extraLivesLeft = extraLivesLeft - 1;
                minesHit[extraLivesLeft][0] = row;
                minesHit[extraLivesLeft][1] = col;
                if (!playerPressed) { // Player placed incorrect flag and tried to autocomplete
                    lastpressed[0] = row; // Make lastpressed the position of the mine that was uncovered
                    lastpressed[1] = col;
                }
            } else {
                lost = true;
                if (!playerPressed) {
                    lastpressed[0] = row;
                    lastpressed[1] = col;
                }
            }
        } else if (actualGrid[row][col].equals(EMPTY)) { // If this is an empty tile
            // Fill out all surrounding tiles
            for (int i = 0; i < 3; i++) {
                fillOutTiles(false, row + 1, col - 1 + i);
                fillOutTiles(false, row, col - 1 + i);
                fillOutTiles(false, row - 1, col - 1 + i);
            }
        } else if (numbers.contains(actualGrid[row][col])) { // If this is a number
            if (timesNumberPressed[row][col] == 0) // Initial hit, don't do anything
                timesNumberPressed[row][col] = 1;
            // Hit again, display surrounding tiles if all "mines" flagged (corresponding to numMines)
            else if (timesNumberPressed[row][col] == 1 && playerPressed) {
                int numMines = getNumberOfMines(row, col);
                int currentFlaggedOrHitMines = 0;
                for (int i = 0; i < 3; i++) {
                    // If mine is flagged or hit - not care if it is actually a mine (&& isMine(row+1,col-1+i)==1)
                    if (row + 1 < numberRows && col - 1 + i < numberCols &&
                            col - 1 + i >= 0 && (flaggedTiles[row + 1][col - 1 + i] == true || minePreviouslyHit(row + 1, col - 1 + i))) {
                        currentFlaggedOrHitMines++;
                    }
                    if (row - 1 >= 0 && col - 1 + i < numberCols &&
                            col - 1 + i >= 0 && (flaggedTiles[row - 1][col - 1 + i] == true || minePreviouslyHit(row - 1, col - 1 + i))) {
                        currentFlaggedOrHitMines++;
                    }
                    if (col - 1 + i < numberCols && col - 1 + i >= 0 && i != 1 &&
                            (flaggedTiles[row][col - 1 + i] == true || minePreviouslyHit(row, col - 1 + i)))
                    // Row,col will not be mine because it's a number
                    // So just checks row,col-1 & row,col+1
                    {
                        currentFlaggedOrHitMines++;
                    }
                }

                // Display all surrounding tiles if all "mines" flagged
                if (currentFlaggedOrHitMines >= numMines) { // > just in case
                    for (int i = 0; i < 3; i++) {
                        // Fill out all surrounding tiles
                        fillOutTiles(false, row + 1, col - 1 + i);
                        fillOutTiles(false, row, col - 1 + i);
                        fillOutTiles(false, row - 1, col - 1 + i);
                    }
                }
            }
        }

        won = allTilesFilledOut();
    }

    /**
     * Checks if all non-mine tiles are exposed, indicating a win.
     *
     * @return True if all tiles are properly exposed or flagged, false otherwise
     */
    private boolean allTilesFilledOut() {
        if (exposedTiles == null || flaggedTiles == null)
            System.exit(NULL_EXIT_CODE);
        // For each tile
        for (int i = 0; i < numberRows; i++) {
            for (int j = 0; j < numberCols; j++) {   // If the tile is not exposed
                if (exposedTiles[i][j] == false) {    // If it is not a flagged mine, return false
                    if (flaggedTiles[i][j] == false && isMine(i, j) == 0)
                        return false;
                }
            }
        }
        return true;
    }

    /**
     * Identifies the mine that would unlock the largest expansion wave if flagged.
     *
     * @return Array with coordinates [row, col] of the best mine to flag
     */
    private int[] findBestMine() {
        int bestRow = -1, bestCol = -1, bestGain = 0;

        for (int m = 0; m < numberMines; m++) {
            int r = mineLocations[0][m], c = mineLocations[1][m];
            if (exposedTiles[r][c] || flaggedTiles[r][c]) continue;

             /* The mine is a valid hint only if at least
               three surrounding NON-mine squares are already revealed. */
            int exposedNbrs = 0;
            for (int dr = -1; dr <= 1; dr++) {
                for (int dc = -1; dc <= 1; dc++) {
                    if (dr == 0 && dc == 0) continue;                // Skip self
                    int nr = r + dr, nc = c + dc;
                    if (nr < 0 || nr >= numberRows ||
                            nc < 0 || nc >= numberCols) continue;        // Off board
                    if (isMine(nr, nc) == 0 && exposedTiles[nr][nc]) // Non-mine & shown
                        exposedNbrs++;
                }
            }
            if (exposedNbrs < 3) continue;          // Skip this mine

            // Work on copies
            boolean[][] ex = deepCopy(exposedTiles);
            boolean[][] flg = deepCopy(flaggedTiles);
            int[][] prs = deepCopy(timesNumberPressed);
            flg[r][c] = true;                                // Pretend to flag

            int delta;
            do {
                int before = countTrue(ex);
                expandOnce(actualGrid, ex, flg, prs);
                delta = countTrue(ex) - before;
            } while (delta > 0);

            int gain = countTrue(ex) - countTrue(exposedTiles);
            if (gain > bestGain) {
                bestGain = gain;
                bestRow = r;
                bestCol = c;
            }
        }
        return new int[]{bestRow, bestCol};
    }

    /**
     * Flags the mine that would unlock the largest expansion wave and returns its coordinate.
     *
     * @return Integer array with {row, col} or {-1, -1} if no hint available
     */
    public int[] applyHint() {
        int[] p = findBestMine();
        if (p[0] == -1) return p;             // No mine found

        tileFlagged(true, p[0], p[1]);              // Updates flaggedTiles & HUD data

        // If every mine is now flagged, declare victory
        if (countFlags() == numberMines) won = true;

        return p;
    }

    /**
     * Suggests a cell that can be logically inferred to contain a mine.
     * Uses only the information currently visible to the player.
     *
     * @return Coordinates [row, col] of a cell that must be a mine,
     * or null if none can be deduced.
     */
    public int[] suggestCellToRevealAsMine() {
        for (int row = 0; row < numberRows; row++) {
            for (int col = 0; col < numberCols; col++) {
                if (exposedTiles[row][col] && isNumeric(actualGrid[row][col])) {
                    int number = Integer.parseInt(actualGrid[row][col]);
                    ArrayList<int[]> hidden = hiddenNbrs(row, col);
                    int flaggedCount = flaggedNbrs(row, col);

                    // If every remaining hidden neighbour must be a mine…
                    if (number - flaggedCount == hidden.size() && !hidden.isEmpty()) {
                        for (int[] cell : hidden) {
                            if (!flaggedTiles[cell[0]][cell[1]]) {
                                return cell;          // First certain mine
                            }
                        }
                    }
                }
            }
        }
        /* Subset-overlap reasoning could be added here later */
        return null;
    }

   /**
     * Recursively exposes all cells that are logically safe based on current flags.
     */
    private void expandLogically() {
        boolean changed;
        do {
            changed = false;

            for (int r = 0; r < numberRows; r++) {
                for (int c = 0; c < numberCols; c++) {
                    if (!exposedTiles[r][c] || !isNumeric(actualGrid[r][c]))
                        continue;                                  // Need a visible number

                    int need = Integer.parseInt(actualGrid[r][c]);
                    int have = flaggedNbrs(r, c);

                    /* All required mines are flagged → simulate user's 2nd-click */
                    if (have >= need) {
                        int before = countTrue(exposedTiles);

                        /* IMPORTANT: playerPressed **must be true** here */
                        fillOutTiles(true, r, c);

                        if (countTrue(exposedTiles) > before)
                            changed = true;                       // Keep looping
                    }
                }
            }

        } while (changed);
    }

    /**
     * Solves the current board using only logical inference (no guessing).
     *
     * @return True if solved logically, false otherwise
     */
    public boolean logicalSolve() {
        expandLogically();                       // Initial flood from any zeros

        while (!won) {
            int[] mine = suggestCellToRevealAsMine();
            if (mine == null) {
                return false;                    // Logic stalled – guessing needed
            }
            // Mark the deduced mine
            tileFlagged(true, mine[0], mine[1]);
            expandLogically();                   // Propagate consequences

            if (countFlags() == numberMines && allTilesFilledOut()) {
                won = true;
            }
        }
        return true;                             // Solved without guessing
    }

    /**
     * Dumps the current contents of all per-cell arrays for debugging.
     * Helps follow how a single user action changed the model's state.
     *
     * @param tag Short label that identifies the triggering event
     */
    private void debugPrintState(String tag) {
        System.out.println("\n===== " + tag + " =====");
        System.out.println("actualGrid:");
        for (String[] r : actualGrid) System.out.println(java.util.Arrays.toString(r));

        System.out.println("exposedTiles:");
        for (boolean[] r : exposedTiles) System.out.println(java.util.Arrays.toString(r));

        System.out.println("flaggedTiles:");
        for (boolean[] r : flaggedTiles) System.out.println(java.util.Arrays.toString(r));

        System.out.println("timesNumberPressed:");
        for (int[] r : timesNumberPressed) System.out.println(java.util.Arrays.toString(r));

        System.out.println("extraLivesLeft = " + extraLivesLeft);
        System.out.println("won=" + won + ", lost=" + lost);
        System.out.println("========================================\n");
    }

    /**
     * Creates a deep copy of a 2D reference array.
     * Works for any type because each row is cloned individually.
     *
     * @param src Source array to copy
     * @return Deep copy of the array
     * @param <T> Type of array elements
     */
    private static <T> T[][] deepCopy(T[][] src) {
        if (src == null) return null;

        @SuppressWarnings("unchecked")
        T[][] dest = src.clone();          // Shallow clone of outer array
        for (int i = 0; i < src.length; i++)
            dest[i] = src[i].clone();      // Deep clone each row

        return dest;
    }

    /**
     * Creates a deep copy of a 2D boolean array.
     *
     * @param src Source array to copy
     * @return Deep copy of the array
     */
    private static boolean[][] deepCopy(boolean[][] src) {
        return java.util.Arrays.stream(src).map(boolean[]::clone).toArray(boolean[][]::new);
    }

    /**
     * Creates a deep copy of a 2D integer array.
     *
     * @param src Source array to copy
     * @return Deep copy of the array
     */
    private static int[][] deepCopy(int[][] src) {
        return java.util.Arrays.stream(src).map(int[]::clone).toArray(int[][]::new);
    }

    /**
     * Counts the number of true values in a 2D boolean array.
     *
     * @param a Array to count true values in
     * @return Count of true values
     */
    private static int countTrue(boolean[][] a) {
        int n = 0;
        for (boolean[] r : a) for (boolean b : r) if (b) n++;
        return n;
    }

    /**
     * Counts the number of flagged tiles on the board.
     *
     * @return Number of flagged tiles
     */
    private int countFlags() {
        return countTrue(flaggedTiles);
    }

    /**
     * Counts how many adjacent squares are flagged around a specific tile.
     *
     * @param row Row index of the center tile
     * @param col Column index of the center tile
     * @param flg 2D boolean array of flags
     * @return Count of flagged adjacent tiles
     */
    private int countAdjacentFlags(int row, int col, boolean[][] flg) {
        int cnt = 0;
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;          // Skip self
                int nr = row + dr, nc = col + dc;
                if (nr < 0 || nr >= numberRows ||
                        nc < 0 || nc >= numberCols) continue;  // Off board
                if (flg[nr][nc]) cnt++;
            }
        }
        return cnt;
    }

    /**
     * Reveals the 8-neighbor "ring" around a number tile.
     * Used for cascade revealing when a numbered tile is clicked
     * with the corresponding number of mines flagged.
     *
     * @param row Row index of the center tile
     * @param col Column index of the center tile
     * @param g The grid showing tile contents
     * @param ex Boolean array tracking exposed tiles
     * @param flg Boolean array tracking flagged tiles
     * @param press Counter for tile presses
     */
    private void revealRing(int row, int col,
                          String[][] g,      // Immutable truth grid
                          boolean[][] ex,    // exposedTiles COPY
                          boolean[][] flg,   // flaggedTiles COPY
                          int[][] press) {   // timesNumberPressed COPY
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;
                int nr = row + dr, nc = col + dc;
                if (nr < 0 || nr >= numberRows ||
                        nc < 0 || nc >= numberCols) continue;

                if (ex[nr][nc] || flg[nr][nc]) continue;   // Already open or flagged
                ex[nr][nc] = true;                         // Reveal neighbour
                press[nr][nc] = 1;                         // Mark one click

                // Flood-fill further if it was a blank square
                if (g[nr][nc].equals(EMPTY))
                    revealRing(nr, nc, g, ex, flg, press);
            }
        }
    }

    /**
     * Performs one global sweep looking for auto-open candidates.
     * Used for simulation and hint generation.
     *
     * @param g The grid showing tile contents
     * @param ex Boolean array tracking exposed tiles
     * @param flg Boolean array tracking flagged tiles
     * @param press Counter for tile presses
     */
    private void expandOnce(String[][] g, boolean[][] ex,
                          boolean[][] flg, int[][] press) {

        for (int r = 0; r < numberRows; r++) {
            for (int c = 0; c < numberCols; c++) {
                if (!ex[r][c]) continue;                    // Number must be visible
                char ch = g[r][c].charAt(0);
                if (ch < '1' || ch > '8') continue;         // Not a number tile

                int need = ch - '0';
                int have = countAdjacentFlags(r, c, flg);
                if (have >= need) {
                    // Mimic the user's second click on that number
                    revealRing(r, c, g, ex, flg, press);
                }
            }
        }
    }

    /**
     * Returns the current number of flags placed on the board.
     *
     * @return Number of flags placed
     */
    @Override
    public int getNumFlags() {
        return countFlags();          // countFlags() helper already exists
    }

    // LOGIC-AI UTILITIES
    private static final int[][] NBR = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1}, {0, 1},
            {1, -1}, {1, 0}, {1, 1}
    };

    /**
     * Checks if coordinates are within the valid grid boundaries.
     *
     * @param r Row index to check
     * @param c Column index to check
     * @param rows Number of rows in grid
     * @param cols Number of columns in grid
     * @return True if coordinates are valid, false otherwise
     */
    private static boolean isValid(int r, int c, int rows, int cols) {
        return r >= 0 && r < rows && c >= 0 && c < cols;
    }

    /**
     * Checks if a string can be parsed as an integer.
     *
     * @param s String to check
     * @return True if string represents a number, false otherwise
     */
    private static boolean isNumeric(String s) {
        if (s == null) return false;
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Returns a list of hidden neighboring cells around a specified position.
     *
     * @param r Row index of center cell
     * @param c Column index of center cell
     * @return ArrayList of coordinate arrays for hidden neighbors
     */
    private ArrayList<int[]> hiddenNbrs(int r, int c) {
        ArrayList<int[]> list = new ArrayList<>();
        for (int[] d : NBR) {
            int nr = r + d[0], nc = c + d[1];
            if (isValid(nr, nc, numberRows, numberCols) && !exposedTiles[nr][nc])
                list.add(new int[]{nr, nc});
        }
        return list;
    }

    /**
     * Counts the number of flagged neighbors around a specified position.
     *
     * @param r Row index of center cell
     * @param c Column index of center cell
     * @return Number of flagged neighbors
     */
    private int flaggedNbrs(int r, int c) {
        int n = 0;
        for (int[] d : NBR) {
            int nr = r + d[0], nc = c + d[1];
            if (isValid(nr, nc, numberRows, numberCols) && flaggedTiles[nr][nc]) n++;
        }
        return n;
    }

    /**
     * Propagates logical consequences of a move and updates win state if board is solved.
     */
    public void propagateLogicalConsequences() {
        expandLogically();               // Existing private helper
        // Update win state in case the board just solved itself
        if (countFlags() == numberMines && allTilesFilledOut()) won = true;
    }

    /**
     * Returns the count of exposed tiles on the board.
     *
     * @return Number of exposed tiles
     */
    private int exposedCount() {
        return countTrue(exposedTiles);
    }

    /**
     * Returns the count of flagged tiles on the board.
     *
     * @return Number of flagged tiles
     */
    private int flagCount() {
        return countTrue(flaggedTiles);
    }

    /**
     * Returns the coordinates of the next logically deduced mine.
     *
     * @return Integer array {row, col} or null if logic is stalled
     */
    public int[] nextLogicalMine() {
        int[] m = suggestCellToRevealAsMine();
        return m == null ? null : m;
    }

    /**
     * Flags every still-hidden mine whose eight neighbors are already
     * exposed (or are mines themselves).
     *
     * @return List of {row,col} coordinates that were newly flagged.
     */
    public java.util.List<int[]> flagIsolatedMines() {

        java.util.List<int[]> added = new java.util.ArrayList<>();

        for (int i = 0; i < numberMines; i++) {
            int r = mineLocations[0][i], c = mineLocations[1][i];
            if (flaggedTiles[r][c]) continue;           // Already done

            boolean hiddenNeighbour = false;
            for (int dr = -1; dr <= 1 && !hiddenNeighbour; dr++)
                for (int dc = -1; dc <= 1; dc++) {
                    if (dr == 0 && dc == 0) continue;
                    int nr = r + dr, nc = c + dc;
                    if (nr < 0 || nr >= numberRows ||
                            nc < 0 || nc >= numberCols) continue;
                    if (!exposedTiles[nr][nc] && isMine(nr, nc) == 0) {
                        hiddenNeighbour = true;         // Still grey square
                        break;
                    }
                }

            if (!hiddenNeighbour) {                    // Safe to flag
                flaggedTiles[r][c] = true;
                added.add(new int[]{r, c});
            }
        }

        /* Update win state in case we just closed the last gap */
        if (countFlags() == numberMines && allTilesFilledOut())
            won = true;

        return added;
    }
}
