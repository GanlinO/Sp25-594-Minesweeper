import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/** Covers difficulty changes, reset(), and statistics. */
public class ModelConfigAndStatsTest {

    private Model model;

    @Before
    public void init() { model = new Model(); }

    @Test
    public void beginnerIsDefault() {
        assertEquals(10, model.getNumMines());
        assertEquals("Beginner best time: 0 seconds",
                model.getBestTimes().split("\n")[0]);
    }

    @Test
    public void difficultyCyclesCorrectly() {
        model.setDifficulty("intermediate");
        assertEquals(40, model.getNumMines());

        model.setDifficulty("expert");
        assertEquals(99, model.getNumMines());

        model.setCustomRows(5);
        model.setCustomColumns(5);
        model.setCustomMines(3);
        model.setDifficulty("custom");
        assertEquals(3, model.getNumMines());
    }

    @Test(expected = IllegalArgumentException.class)
    public void unknownDifficultyRejected() {
        model.setDifficulty("nightmare");
    }

    @Test
    public void resetIncrementsGameCounter() {
        long before = model.getTotalGamesPlayed();
        model.resetGame();
        assertEquals(before + 1, model.getTotalGamesPlayed());
    }

    /** Win a 2×2 with 1 mine and verify best-time bookkeeping. */
    @Test
    public void bestTimeRecordedOnWin() {
        setupTinyBoard(model);

        // expose the three safe squares
        for (int r = 0; r < 2; r++)
            for (int c = 0; c < 2; c++)
                if (!"M".equals(model.getGrid()[r][c]))
                    model.tilePressed(r, c, /*elapsed sec*/5);

        // flag the single mine
        for (int r = 0; r < 2; r++)
            for (int c = 0; c < 2; c++)
                if ("M".equals(model.getGrid()[r][c]))
                    model.tileFlagged(true, r, c);

        assertTrue(model.playerWon());
        assertTrue(model.getBestTimes().contains("Custom best time: 5 seconds"));
    }

    /* helper */
    private static void setupTinyBoard(Model m) {
        m.setCustomRows(2);
        m.setCustomColumns(2);
        m.setCustomMines(1);
        m.setDifficulty("custom");
        assertTrue(m.startGame());
    }
}

