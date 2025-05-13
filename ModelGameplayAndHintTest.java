import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/** Exercises tile clicks, extra lives, applyHint(), and logicalSolve(). */
public class ModelGameplayAndHintTest {

    private Model model;

    @Before
    public void freshModel() { model = new Model(); }

    @Test
    public void extraLifeAbsorbsFirstMine() {
        model.setDifficulty("beginner");
        model.setExtraLives(1);
        model.startGame();

        int[] mine = locateMine(model);
        model.tilePressed(mine[0], mine[1], 0);

        assertFalse(model.playerLost());
        assertEquals(0, model.getExtraLivesLeft());     // life was used
    }

    @Test
    public void applyHintFlagsAMine() {
        // 4×4 with 1 mine gives us enough neighbours to satisfy the
        // "≥3 surrounding revealed" rule inside findBestMine().
        model.setCustomRows(4);
        model.setCustomColumns(4);
        model.setCustomMines(1);
        model.setDifficulty("custom");
        model.startGame();

        int[] mine = locateMine(model);

        /* Reveal at least three non-mine neighbours. */
        int revealed = 0;
        for (int dr = -1; dr <= 1; dr++)
            for (int dc = -1; dc <= 1; dc++) {
                int nr = mine[0] + dr, nc = mine[1] + dc;
                if ((dr == 0 && dc == 0) || !inside(model, nr, nc)) continue;
                if (!"M".equals(model.getGrid()[nr][nc])) {
                    model.tilePressed(nr, nc, 0);
                    revealed++;
                    if (revealed == 3) break;
                }
            }

        int flagsBefore = model.getNumFlags();
        int[] hinted = model.applyHint();
        int flagsAfter  = model.getNumFlags();

        assertArrayEquals(mine, hinted);
        assertEquals(flagsBefore + 1, flagsAfter);
    }

    /** logicalSolve() must make at least one safe deduction without causing a loss. */
    @Test
    public void logicalSolveMakesProgress() {
        model.setLogicalMode(true);
        model.setCustomRows(3);
        model.setCustomColumns(3);
        model.setCustomMines(2);
        model.setDifficulty("custom");
        model.startGame();

        // Click the first safe tile found to generate number clues.
        outer:
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                if (!"M".equals(model.getGrid()[r][c])) {
                    model.tilePressed(r, c, 0);
                    break outer;
                }

        int exposedBefore = countTrue(model.getExposed());
        int flagsBefore   = model.getNumFlags();

        model.logicalSolve();

        int exposedAfter  = countTrue(model.getExposed());
        int flagsAfter    = model.getNumFlags();

        assertFalse("solver must never cause a loss", model.playerLost());
        assertTrue("solver made at least one deduction",
                exposedAfter > exposedBefore || flagsAfter > flagsBefore);
    }

    /* helpers */
    private static int countTrue(boolean[][] arr) {
        int n = 0;
        for (boolean[] row : arr)
            for (boolean b : row) if (b) n++;
        return n;
    }

    private static int[] locateMine(Model m) {
        String[][] g = m.getGrid();
        for (int r = 0; r < g.length; r++)
            for (int c = 0; c < g[0].length; c++)
                if ("M".equals(g[r][c])) return new int[]{r, c};
        throw new AssertionError("mine not found");
    }

    private static boolean inside(Model m, int r, int c) {
        return r >= 0 && r < m.getGrid().length &&
                c >= 0 && c < m.getGrid()[0].length;
    }
}
