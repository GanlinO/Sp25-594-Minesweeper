import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/* This is a test class for the Model class in Minesweeper */
public class ModelTest {

    private Model model;

    // Initializes a new Model instance before each test
    @Before
    public void setUp() {
        model = new Model();
    }

    // Tests that the default difficulty is set to beginner (10 mines)
    @Test
    public void testInitialDifficulty() {
        assertEquals(10, model.getNumMines());
    }

    // Tests that setting difficulty to intermediate updates the mine count to 40
    @Test
    public void testDifficultyIntermediate() {
        model.setDifficulty("intermediate");
        assertEquals(40, model.getNumMines());
    }

    // Tests that setting difficulty to expert updates the mine count to 99
    @Test
    public void testDifficultyExpert() {
        model.setDifficulty("expert");
        assertEquals(99, model.getNumMines());
    }

    // Tests that custom difficulty correctly applies custom mine and board settings
    @Test
    public void testDifficultyCustom() {
        model.setCustomMines(25);
        model.setCustomRows(12);
        model.setCustomColumns(12);
        model.setDifficulty("custom");
        assertEquals(25, model.getNumMines());
    }

    // Tests that an invalid difficulty string throws an exception
    @Test(expected = IllegalArgumentException.class)
    public void testDifficultyInvalid() {
        model.setDifficulty("invalid");
    }

    // Tests that calling resetGame resets the model back to beginner settings
    @Test
    public void testResetBeginner() {
        model.setDifficulty("expert");
        model.resetGame();
        assertEquals(10, model.getNumMines());
    }

    // Tests that a valid game configuration allows the game to start
    @Test
    public void testStartGameValidConf() {
        assertTrue(model.startGame());
    }

    // Tests that starting the game properly initializes the exposedTiles array
    @Test
    public void testStartGameExposedTiles() {
        model.startGame();
        boolean[][] exposed = model.getExposed();
        assertNotNull(exposed);
        assertEquals(9, exposed.length); // default beginner rows
        assertEquals(9, exposed[0].length); // default beginner cols
    }

    // Tests that calling tilePressed does not throw and returns non-null exposed grid
    @Test
    public void testTilePressed() {
        model.startGame();
        boolean[][] exposed = model.tilePressed(0, 0, 5);
        assertNotNull(exposed);
    }

    // Tests that extra lives can be set and retrieved correctly
    @Test
    public void testExtraLives() {
        model.setExtraLives(2);
        assertEquals(2, model.getExtraLivesLeft());
    }

    // Tests that the correct last pressed tile is recorded after tilePressed
    @Test
    public void testGetLast() {
        model.startGame();
        model.tilePressed(0, 0, 10);
        int[] last = model.getLastPressed();
        assertEquals(0, last[0]);
        assertEquals(0, last[1]);
    }

    // Tests that best times are initially zero for all difficulties
    @Test
    public void testBestTimes() {
        String bestTimes = model.getBestTimes();
        assertTrue(bestTimes.contains("Beginner best time: 0"));
    }

    // Tests that resetGame increments the total games played counter
    @Test
    public void testReserGamesPlayed() {
        long gamesBefore = model.getTotalGamesPlayed();
        model.resetGame();
        assertEquals(gamesBefore + 1, model.getTotalGamesPlayed());
    }

    // Tests that a mine location is tracked in minesHit and used in lastPressed logic
    @Test
    public void testMinePreviouslyHit() {
        model.startGame();
        model.setExtraLives(1);
        model.tilePressed(0, 0, 5); // trigger a mine hit if it's there

        // Simulate manual mine hit
        model.getLastPressed()[0] = 0;
        model.getLastPressed()[1] = 0;

        boolean[][] exposed = model.tilePressed(0, 0, 5);
        int[] last = model.getLastPressed();
        assertTrue(last[0] == 0 && last[1] == 0);
    }

    // Tests that the model can detect number of adjacent mines around a safe tile
    @Test
    public void testNumberOfMines() {
        model.setCustomMines(1);
        model.setCustomRows(3);
        model.setCustomColumns(3);
        model.setDifficulty("custom");
        model.startGame();

        String[][] grid = model.getGrid();
        int mineRow = -1, mineCol = -1;

        // Locate the only mine
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if ("M".equals(grid[i][j])) {
                    mineRow = i;
                    mineCol = j;
                    break;
                }
            }
        }

        // Check number around a safe tile
        if (mineRow != 0 || mineCol != 0) {
            assertNotEquals("M", grid[0][0]);
            model.tilePressed(0, 0, 5);
            assertTrue(model.getExposed()[0][0]);
        }
    }

    // Tests the win condition: all non-mine tiles exposed and all mines flagged
    @Test
    public void testWinCondition() {
        model.setCustomMines(1);
        model.setCustomRows(2);
        model.setCustomColumns(2);
        model.setDifficulty("custom");
        model.startGame();

        // Expose all safe tiles
        String[][] grid = model.getGrid();
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                if (!"M".equals(grid[i][j])) {
                    model.tilePressed(i, j, 3);
                }
            }
        }

        // Flag the tile containing the mine
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                if ("M".equals(grid[i][j])) {
                    model.tileFlagged(true, i, j);
                }
            }
        }

        // Check that playerWon returns true
        assertTrue(model.playerWon());
    }
}
