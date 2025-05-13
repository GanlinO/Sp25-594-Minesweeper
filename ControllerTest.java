import org.junit.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

/**
 * Verifies that Controller correctly delegates to Model and never
 * crashes when the real GUI is replaced by a dummy view.
 */
public class ControllerTest {

    private Controller            controller;
    private Model                 mockModel;
    private ControllerToViewGUI   dummyView;

    private static void inject(Object target, String field, Object value)
            throws Exception {
        Field f = Controller.class.getDeclaredField(field);
        f.setAccessible(true);
        f.set(target, value);
    }

    @Before
    public void init() throws Exception {
        /*
         * Controller’s default ctor builds a real ViewGUI → JFrame.
         * Wrap the construction so we can skip the whole class when
         * running in a strictly head-less JVM.
         */
        try {
            controller = new Controller();
        } catch (java.awt.HeadlessException ex) {
            Assume.assumeTrue("Head-less environment – skip Controller tests",
                    false);
            return;
        }

        /* swap in mocked collaborators */
        mockModel = mock(Model.class);
        dummyView = mock(ControllerToViewGUI.class);

        inject(controller, "myModel", mockModel);
        inject(controller, "myView",  dummyView);
    }

    /* ------------------------------------------------------------------ */
    /*  individual tests                                                  */
    /* ------------------------------------------------------------------ */

    @Test
    public void setDifficulty_isForwardedToModel() {
        controller.setDifficulty("expert");
        verify(mockModel).setDifficulty("expert");
    }

    @Test
    public void startGame_returnsModelResult() {
        when(mockModel.startGame()).thenReturn(true);

        assertTrue(controller.startGame());
        verify(mockModel).startGame();
    }

    @Test
    public void getNumMines_delegates() {
        when(mockModel.getNumMines()).thenReturn(99);
        assertEquals(99, controller.getNumMines());
    }

    @Test
    public void getDifficulties_delegates() {
        List<String> diffs = Arrays.asList("beginner", "expert");
        when(mockModel.getDifficulties()).thenReturn(new java.util.ArrayList<>(diffs));

        assertEquals(diffs, controller.getDifficulties());
    }

    @Test
    public void playerLost_and_playerWon_delegate() {
        when(mockModel.playerLost()).thenReturn(true);
        when(mockModel.playerWon()).thenReturn(false);

        assertTrue (controller.playerLost());
        assertFalse(controller.playerWon());

        verify(mockModel).playerLost();
        verify(mockModel).playerWon();
    }

    @Test
    public void tilePressed_forwardsToModel_andRefreshesView() {
        boolean[][] stubExpose = new boolean[0][0];
        when(mockModel.tilePressed(3, 4, 7L)).thenReturn(stubExpose);

        controller.tilePressed(3, 4, 7L);

        verify(mockModel).tilePressed(3, 4, 7L);
        verify(dummyView).refresh(eq(stubExpose), anyString());
    }

    @Test
    public void placeFlag_forwardsToModel() {
        controller.placeFlag(true, 1, 2);
        verify(mockModel).tileFlagged(true, 1, 2);
    }

    @Test
    public void reset_callsModelReset() {
        controller.reset();
        verify(mockModel).resetGame();
    }
}
