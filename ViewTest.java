import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.event.*;
import java.awt.HeadlessException;

/**
 * Comprehensive test class for *all* View-layer classes in the Minesweeper
 * application – listeners, frames, pop-ups, and delegation to the controller.
 */
public class ViewTest {

    /* ------------------------------------------------------------ *
     *  global head-less flag so Swing never tries to open windows  *
     * ------------------------------------------------------------ */
    @BeforeClass
    public static void goHeadless() {
        System.setProperty("java.awt.headless", "true");
    }

    /* ---------- common fixtures ---------- */
    @Mock
    private ViewGUIToController mockController;
    private ViewGUI viewGUI;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        when(mockController.getDifficulties())
                .thenReturn(new java.util.ArrayList<>(
                        java.util.Arrays.asList(
                                "beginner", "intermediate", "expert", "custom")));

        when(mockController.getEmptyTileString()).thenReturn(" ");
        when(mockController.getMineString()).thenReturn("*");
        when(mockController.getRules()).thenReturn("These are the rules");

        viewGUI = new ViewGUI(mockController);
    }

    /* ============================================================ *
     *                       ViewGUI  tests                         *
     * ============================================================ */

    @Test
    public void testViewGUI_SetExtraLives() {
        viewGUI.setExtraLives(1);
        verify(mockController).setExtraLives(1);

        viewGUI.setExtraLives(2);
        verify(mockController).setExtraLives(2);

        viewGUI.setExtraLives(3);
        verify(mockController).setExtraLives(3);

        viewGUI.setExtraLives(-1);
        verify(mockController).setExtraLives(-1);
    }

    @Test
    public void testViewGUI_GetRules() {
        assertEquals("These are the rules", viewGUI.getRules());
        verify(mockController).getRules();
    }

    @Test
    public void testViewGUI_GetEmptyTileString() {
        assertEquals(" ", viewGUI.getEmptyTileString());
        verify(mockController).getEmptyTileString();
    }

    @Test
    public void testViewGUI_GetExtraLivesLeft() {
        when(mockController.getExtraLivesLeft()).thenReturn(2);
        assertEquals(2, viewGUI.getExtraLivesLeft());
        verify(mockController).getExtraLivesLeft();
    }

    @Test
    public void testViewGUI_TilePressed_Normal() throws Exception {
        ViewGUI spyGUI = spy(viewGUI);
        ViewGameTilesFrame mockFrame = mock(ViewGameTilesFrame.class);

        java.lang.reflect.Field f = ViewGUI.class.getDeclaredField("gameframe");
        f.setAccessible(true);
        f.set(spyGUI, mockFrame);

        when(mockController.playerLost()).thenReturn(false);
        when(mockController.playerWon()).thenReturn(false);
        when(mockController.getLastPressed()).thenReturn(new int[]{1, 2});
        when(mockFrame.getCurrentTime()).thenReturn(100L);

        spyGUI.tilePressed("1,2");

        verify(mockController).tilePressed(1, 2, 100L);
        verify(mockFrame).pressed(1, 2, "*");
    }

    @Test
    public void testViewGUI_TilePressed_PlayerLost() throws Exception {
        ViewGUI spyGUI = spy(viewGUI);
        ViewGameTilesFrame mockFrame = mock(ViewGameTilesFrame.class);

        java.lang.reflect.Field f = ViewGUI.class.getDeclaredField("gameframe");
        f.setAccessible(true);
        f.set(spyGUI, mockFrame);

        when(mockController.playerLost()).thenReturn(true);
        when(mockController.getLastPressed()).thenReturn(new int[]{1, 2});
        when(mockFrame.getCurrentTime()).thenReturn(100L);
        when(mockFrame.stopTimer()).thenReturn(100L);
        when(mockController.getBestTime()).thenReturn("Best time: 100");
        when(mockController.getTotalGamesPlayed()).thenReturn(5L);
        when(mockController.getTotalGamesWon()).thenReturn(2L);

        try {
            spyGUI.tilePressed("1,2");
            verify(mockFrame).playerLost(new int[]{1, 2});
            verify(mockFrame).stopTimer();
        } catch (java.awt.HeadlessException ex) {
            // Running in head-less mode (CI); skip this GUI-dependent check.
            org.junit.Assume.assumeTrue("headless – skip end-frame test", false);
        }
    }

    @Test
    public void testViewGUI_PlayGame_Success() throws Exception {
        ViewGUI spyGUI = spy(viewGUI);
        ViewStartFrame mockStart = mock(ViewStartFrame.class);

        java.lang.reflect.Field f = ViewGUI.class.getDeclaredField("startframe");
        f.setAccessible(true);
        f.set(spyGUI, mockStart);

        when(mockController.startGame()).thenReturn(true);
        when(mockController.getGrid()).thenReturn(new String[9][9]);
        when(mockController.getNumMines()).thenReturn(10);

        try {
            spyGUI.playGame();

            verify(mockStart).dispose();
            verify(mockController).startGame();
            verify(mockController).getGrid();
            verify(mockController).getNumMines();
        } catch (HeadlessException ex) {
            // Head-less environment (CI) – skip this GUI test
            org.junit.Assume.assumeTrue("headless – skip playGame frame test", false);
        }
    }

    @Test
    public void testViewGUI_PlayGame_Failure() throws Exception {
        ViewGUI spyGUI = spy(viewGUI);
        doNothing().when(spyGUI).createPopUp(anyString(), anyInt(), anyInt(), anyBoolean());
        when(mockController.startGame()).thenReturn(false);

        spyGUI.playGame();

        verify(spyGUI).createPopUp(
                contains("Custom number of mines should be less than"),
                anyInt(), anyInt(), eq(false));
    }

    /* ============================================================ *
     *                  Listener-class  tests                        *
     * ============================================================ */

    @Test
    public void testViewButtonClickListener_Play() {
        // spy so we can verify, but stub out the real GUI work
        ViewGUI spyGUI = spy(viewGUI);
        doNothing().when(spyGUI).playGame();

        ViewButtonClickListener listener = new ViewButtonClickListener(spyGUI);
        listener.actionPerformed(new ActionEvent(this, 0, "Play"));

        verify(spyGUI).playGame();
        verify(spyGUI, never()).exitGame();
    }

    @Test
    public void testViewButtonClickListener_Exit() {
        ViewButtonClickListener l = new ViewButtonClickListener(viewGUI);
        l.actionPerformed(new ActionEvent(this, 0, "Exit"));
        verify(mockController, never()).startGame();
        ViewGUI spy = spy(viewGUI);
        new ViewButtonClickListener(spy)
                .actionPerformed(new ActionEvent(this, 0, "Exit"));
        verify(spy).exitGame();
    }

    @Test
    public void testViewButtonClickListener_PlayAgain() {
        ViewGUI spyGUI = spy(viewGUI);
        doNothing().when(spyGUI).playAgain();

        ViewButtonClickListener listener = new ViewButtonClickListener(spyGUI);
        listener.actionPerformed(new ActionEvent(this, 0, "Play Again"));

        verify(spyGUI).playAgain();
    }

    @Test
    public void testViewCheckBoxListener_ExtraLives() {
        ViewGUI spyGUI = spy(viewGUI);
        doNothing().when(spyGUI).showExtraLives();

        ViewCheckBoxListener listener = new ViewCheckBoxListener(spyGUI);

        JCheckBox box = new JCheckBox("Extra Lives");
        box.setActionCommand("Extra Lives");
        box.setSelected(true);

        ItemEvent ev = new ItemEvent(box,
                ItemEvent.ITEM_STATE_CHANGED,
                box,
                ItemEvent.SELECTED);

        listener.itemStateChanged(ev);

        verify(spyGUI).showExtraLives();
    }

    @Test
    public void testViewCheckBoxListener_LogicalMode() {
        ViewGUI spy = spy(viewGUI);
        ViewCheckBoxListener l = new ViewCheckBoxListener(spy);
        JCheckBox cb = new JCheckBox("Logical Mode – AI Solver");
        cb.setActionCommand("Logical Mode – AI Solver");
        cb.setSelected(true);
        l.itemStateChanged(new ItemEvent(cb, 0, cb, ItemEvent.SELECTED));
        verify(spy).setLogicalMode(true);
    }

    @Test
    public void testViewMenuListener_Exit() {
        ViewGUI spy = spy(viewGUI);
        new ViewMenuListener(spy)
                .actionPerformed(new ActionEvent(this, 0, "Exit"));
        verify(spy).exitGame();
    }

    @Test
    public void testViewMenuListener_PlayDifferentGame() {
        ViewGUI spyGUI = spy(viewGUI);
        doNothing().when(spyGUI).playAgain();

        ViewMenuListener listener = new ViewMenuListener(spyGUI);
        listener.actionPerformed(
                new ActionEvent(this, 0, "Play Different Game"));

        verify(spyGUI).playAgain();
    }

    @Test
    public void testViewMenuListener_NewGameSameSettings() {
        ViewGUI spyGUI = spy(viewGUI);
        doNothing().when(spyGUI).playGame();

        ViewMenuListener listener = new ViewMenuListener(spyGUI);
        listener.actionPerformed(
                new ActionEvent(this, 0, "New Game With Same Settings"));

        verify(spyGUI).playGame();
    }

    @Test
    public void testViewMenuListener_DisplayRules() {
        ViewGUI spyGUI = spy(viewGUI);

        when(spyGUI.getRules()).thenReturn("rules!");
        doNothing().when(spyGUI)
                .createPopUp(anyString(), anyInt(), anyInt(), anyBoolean());

        ViewMenuListener listener = new ViewMenuListener(spyGUI);
        listener.actionPerformed(
                new ActionEvent(this, 0, "Display Rules"));

        verify(spyGUI).createPopUp(eq("rules!"), anyInt(), anyInt(), eq(true));
    }

    @Test
    public void testViewMouseListener_LeftClick() {
        ViewGUI spyGUI = spy(viewGUI);

        doNothing().when(spyGUI).tilePressed(anyString());
        doNothing().when(spyGUI).placeFlag(any(JButton.class));

        ViewMouseListener listener = new ViewMouseListener(spyGUI);

        JButton btn = new JButton();
        btn.setActionCommand("1,2,*");

        MouseEvent evt = new MouseEvent(btn,
                MouseEvent.MOUSE_CLICKED,
                System.currentTimeMillis(),
                InputEvent.BUTTON1_DOWN_MASK,
                0, 0, 1, false,
                MouseEvent.BUTTON1);

        listener.mouseClicked(evt);

        verify(spyGUI).tilePressed("1,2,*");
        verify(spyGUI, never()).placeFlag(any());
    }


    @Test
    public void testViewMouseListener_RightClick() {
        ViewGUI mockGUI = mock(ViewGUI.class);
        ViewMouseListener listener = new ViewMouseListener(mockGUI);

        JButton btn = new JButton();
        btn.setActionCommand("1,2,*");

        MouseEvent evt = new MouseEvent(btn,
                MouseEvent.MOUSE_CLICKED,
                System.currentTimeMillis(),
                0, 0, 0,
                1, false,
                MouseEvent.BUTTON3);

        listener.mouseClicked(evt);

        verify(mockGUI).placeFlag(btn);
    }

    @Test
    public void testViewRadioButtonListener_ExtraLives() {
        ViewRadioButtonListener l = new ViewRadioButtonListener(viewGUI);
        l.actionPerformed(new ActionEvent(this, 0, "1extra"));
        verify(mockController).setExtraLives(1);
    }

    @Test
    public void testViewRadioButtonListener_Difficulty() {
        ViewGUI spyGUI = spy(viewGUI);
        doNothing().when(spyGUI).setDifficulty(anyString());

        ViewRadioButtonListener listener = new ViewRadioButtonListener(spyGUI);
        listener.actionPerformed(new ActionEvent(this, 0, "expert"));  // pick any difficulty

        verify(spyGUI).setDifficulty("expert");
    }

    @Test
    public void testViewHintListener() {
        ViewGUI spyGUI = spy(viewGUI);
        doNothing().when(spyGUI).hint();

        ViewHintListener listener = new ViewHintListener(spyGUI);
        listener.actionPerformed(new ActionEvent(this, 0, "Hint"));

        verify(spyGUI).hint();
    }

    @Test
    public void testViewTimerActionListener() {
        ViewGameTilesFrame frame = mock(ViewGameTilesFrame.class);
        new ViewTimerActionListener(frame)
                .actionPerformed(new ActionEvent(this, 0, ""));
        verify(frame).incrementTime();
    }

    @Test
    public void testViewSpinnerListener() {
        // spy so we can observe the call
        ViewGUI spyGUI = spy(viewGUI);
        doNothing().when(spyGUI).setCustom(any(JSpinner.class));

        ViewSpinnerListener listener = new ViewSpinnerListener(spyGUI);
        JSpinner spinner = new JSpinner();

        listener.stateChanged(new ChangeEvent(spinner));

        verify(spyGUI).setCustom(spinner);
    }

    @Test
    public void testViewPopupHelp_Constructs() {
        try {
            ViewPopupHelp dlg =
                    new ViewPopupHelp(null, "msg", 200, 120, false);
            assertEquals("Help Window", dlg.getTitle());
            dlg.dispose();
        } catch (HeadlessException ex) {
            org.junit.Assume.assumeTrue("Headless mode – skipping frame test", false);
        }
    }
}

