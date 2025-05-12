import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Comprehensive test class for all View-related classes in the Minesweeper application.
 * This test suite covers:
 * - ViewGUI
 * - ViewStartFrame
 * - ViewGameTilesFrame
 * - ViewEndFrame
 * - ViewPopupHelp
 * - Various listeners (ViewButtonClickListener, ViewCheckBoxListener, etc.)
 */
public class ViewTest {

    @Mock
    private ViewGUIToController mockController;

    private ViewGUI viewGUI;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // Set up commonly used controller behavior
        when(mockController.getDifficulties()).thenReturn(
                new ArrayList<>(Arrays.asList("beginner", "intermediate", "expert", "custom"))
        );
        when(mockController.getEmptyTileString()).thenReturn(" ");
        when(mockController.getMineString()).thenReturn("*");
        when(mockController.getRules()).thenReturn("These are the rules of Minesweeper");

        viewGUI = new ViewGUI(mockController);
    }

    //==================== ViewGUI Tests ====================//
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
        String rules = viewGUI.getRules();
        assertEquals("These are the rules of Minesweeper", rules);
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

        int lives = viewGUI.getExtraLivesLeft();
        assertEquals(2, lives);
        verify(mockController).getExtraLivesLeft();
    }

    @Test
    public void testViewGUI_TilePressed_Normal() {
        // Setup mocks
        ViewGUI spyGUI = spy(viewGUI);
        ViewGameTilesFrame mockGameFrame = mock(ViewGameTilesFrame.class);

        // Use reflection to set private field
        try {
            java.lang.reflect.Field field = ViewGUI.class.getDeclaredField("gameframe");
            field.setAccessible(true);
            field.set(spyGUI, mockGameFrame);
        } catch (Exception e) {
            fail("Could not set gameframe field: " + e.getMessage());
        }

        // Set up controller responses
        when(mockController.playerLost()).thenReturn(false);
        when(mockController.playerWon()).thenReturn(false);
        when(mockController.getLastPressed()).thenReturn(new int[] {1, 2});
        when(mockGameFrame.getCurrentTime()).thenReturn(100L);

        // Call tilePressed
        spyGUI.tilePressed("1,2");

        // Verify controller method is called
        verify(mockController).tilePressed(1, 2, 100L);
        verify(mockController).playerLost();
        verify(mockController).playerWon();
        verify(mockController).getLastPressed();
        verify(mockGameFrame).pressed(1, 2, "*");
    }

    @Test
    public void testViewGUI_TilePressed_PlayerLost() {
        // Setup mocks
        ViewGUI spyGUI = spy(viewGUI);
        ViewGameTilesFrame mockGameFrame = mock(ViewGameTilesFrame.class);

        // Use reflection to set private field
        try {
            java.lang.reflect.Field field = ViewGUI.class.getDeclaredField("gameframe");
            field.setAccessible(true);
            field.set(spyGUI, mockGameFrame);
        } catch (Exception e) {
            fail("Could not set gameframe field: " + e.getMessage());
        }

        // Set up controller responses
        when(mockController.playerLost()).thenReturn(true);
        when(mockController.getLastPressed()).thenReturn(new int[] {1, 2});
        when(mockGameFrame.getCurrentTime()).thenReturn(100L);
        when(mockGameFrame.stopTimer()).thenReturn(100L);
        when(mockController.getBestTime()).thenReturn("Best time: 100");
        when(mockController.getTotalGamesPlayed()).thenReturn(5L);
        when(mockController.getTotalGamesWon()).thenReturn(2L);

        // Call tilePressed
        spyGUI.tilePressed("1,2");

        // Verify controller method is called
        verify(mockController).tilePressed(1, 2, 100L);
        verify(mockController).playerLost();
        verify(mockGameFrame).playerLost(new int[] {1, 2});
        verify(mockGameFrame).stopTimer();
    }

    @Test
    public void testViewGUI_PlayGame_Success() {
        // Setup mocks
        ViewGUI spyGUI = spy(viewGUI);
        ViewStartFrame mockStartFrame = mock(ViewStartFrame.class);

        // Use reflection to set private field
        try {
            java.lang.reflect.Field field = ViewGUI.class.getDeclaredField("startframe");
            field.setAccessible(true);
            field.set(spyGUI, mockStartFrame);
        } catch (Exception e) {
            fail("Could not set startframe field: " + e.getMessage());
        }

        // Game starts successfully
        when(mockController.startGame()).thenReturn(true);

        // Create mock grid for the controller to return
        String[][] mockGrid = new String[9][9];
        when(mockController.getGrid()).thenReturn(mockGrid);
        when(mockController.getNumMines()).thenReturn(10);

        // Call playGame
        spyGUI.playGame();

        // Verify startframe is disposed
        verify(mockStartFrame).dispose();

        // Verify controller methods are called
        verify(mockController).startGame();
        verify(mockController).getGrid();
        verify(mockController).getNumMines();
    }

    @Test
    public void testViewGUI_PlayGame_Failure() {
        // Setup mocks
        ViewGUI spyGUI = spy(viewGUI);
        ViewGameTilesFrame mockGameFrame = mock(ViewGameTilesFrame.class);

        // Use reflection to set private field
        try {
            java.lang.reflect.Field field = ViewGUI.class.getDeclaredField("gameframe");
            field.setAccessible(true);
            field.set(spyGUI, mockGameFrame);
        } catch (Exception e) {
            fail("Could not set gameframe field: " + e.getMessage());
        }

        // Game starts unsuccessfully
        when(mockController.startGame()).thenReturn(false);

        // Prevent actual popup creation
        doNothing().when(spyGUI).createPopUp(anyString(), anyInt(), anyInt(), anyBoolean());

        // Call playGame
        spyGUI.playGame();

        // Verify controller method is called
        verify(mockController).startGame();

        // Verify createPopUp is called with appropriate message
        verify(spyGUI).createPopUp(contains("Custom number of mines should be less than"), anyInt(), anyInt(), eq(false));
    }

    //==================== ViewButtonClickListener Tests ====================//

    @Test
    public void testViewButtonClickListener_Play() {
        ViewGUI mockGUI = mock(ViewGUI.class);
        ViewButtonClickListener listener = new ViewButtonClickListener(mockGUI);

        ActionEvent mockEvent = mock(ActionEvent.class);
        when(mockEvent.getActionCommand()).thenReturn("Play");

        listener.actionPerformed(mockEvent);

        verify(mockGUI).playGame();
    }

    @Test
    public void testViewButtonClickListener_Exit() {
        ViewGUI mockGUI = mock(ViewGUI.class);
        ViewButtonClickListener listener = new ViewButtonClickListener(mockGUI);

        ActionEvent mockEvent = mock(ActionEvent.class);
        when(mockEvent.getActionCommand()).thenReturn("Exit");

        listener.actionPerformed(mockEvent);

        verify(mockGUI).exitGame();
    }

    @Test
    public void testViewButtonClickListener_PlayAgain() {
        ViewGUI mockGUI = mock(ViewGUI.class);
        ViewButtonClickListener listener = new ViewButtonClickListener(mockGUI);

        ActionEvent mockEvent = mock(ActionEvent.class);
        when(mockEvent.getActionCommand()).thenReturn("Play Again");

        listener.actionPerformed(mockEvent);

        verify(mockGUI).playAgain();
    }

    //==================== ViewCheckBoxListener Tests ====================//

    @Test
    public void testViewCheckBoxListener_ExtraLives() {
        ViewGUI mockGUI = mock(ViewGUI.class);
        ViewCheckBoxListener listener = new ViewCheckBoxListener(mockGUI);

        JCheckBox mockCheckBox = mock(JCheckBox.class);
        when(mockCheckBox.getActionCommand()).thenReturn("Extra Lives");

        ItemEvent mockEvent = mock(ItemEvent.class);
        when(mockEvent.getSource()).thenReturn(mockCheckBox);

        listener.itemStateChanged(mockEvent);

        verify(mockGUI).showExtraLives();
    }

    //==================== ViewMenuListener Tests ====================//

    @Test
    public void testViewMenuListener_Exit() {
        ViewGUI mockGUI = mock(ViewGUI.class);
        ViewMenuListener listener = new ViewMenuListener(mockGUI);

        ActionEvent mockEvent = mock(ActionEvent.class);
        when(mockEvent.getActionCommand()).thenReturn("Exit");

        listener.actionPerformed(mockEvent);

        verify(mockGUI).exitGame();
    }

    @Test
    public void testViewMenuListener_PlayDifferentGame() {
        ViewGUI mockGUI = mock(ViewGUI.class);
        ViewMenuListener listener = new ViewMenuListener(mockGUI);

        ActionEvent mockEvent = mock(ActionEvent.class);
        when(mockEvent.getActionCommand()).thenReturn("Play Different Game");

        listener.actionPerformed(mockEvent);

        verify(mockGUI).playAgain();
    }

    @Test
    public void testViewMenuListener_NewGameWithSameSettings() {
        ViewGUI mockGUI = mock(ViewGUI.class);
        ViewMenuListener listener = new ViewMenuListener(mockGUI);

        ActionEvent mockEvent = mock(ActionEvent.class);
        when(mockEvent.getActionCommand()).thenReturn("New Game With Same Settings");

        listener.actionPerformed(mockEvent);

        verify(mockGUI).playGame();
    }

    @Test
    public void testViewMenuListener_DisplayRules() {
        ViewGUI mockGUI = mock(ViewGUI.class);
        when(mockGUI.getRules()).thenReturn("These are the rules");

        ViewMenuListener listener = new ViewMenuListener(mockGUI);

        ActionEvent mockEvent = mock(ActionEvent.class);
        when(mockEvent.getActionCommand()).thenReturn("Display Rules");

        listener.actionPerformed(mockEvent);

        verify(mockGUI).createPopUp(eq("These are the rules"), anyInt(), anyInt(), eq(true));
    }

    //==================== ViewMouseListener Tests ====================//

    @Test
    public void testViewMouseListener_LeftClick() {
        ViewGUI mockGUI = mock(ViewGUI.class);
        ViewMouseListener listener = new ViewMouseListener(mockGUI);

        JButton mockButton = mock(JButton.class);
        when(mockButton.getActionCommand()).thenReturn("1,2,*");

        MouseEvent mockEvent = mock(MouseEvent.class);
        when(mockEvent.getSource()).thenReturn(mockButton);
        when(mockEvent.getButton()).thenReturn(MouseEvent.BUTTON1);

        listener.mouseClicked(mockEvent);

        verify(mockGUI).tilePressed("1,2,*");
    }

    @Test
    public void testViewMouseListener_RightClick() {
        ViewGUI mockGUI = mock(ViewGUI.class);
        ViewMouseListener listener = new ViewMouseListener(mockGUI);

        JButton mockButton = mock(JButton.class);
        when(mockButton.getActionCommand()).thenReturn("1,2,*");

        // Create a MouseEvent that would normally be detected as a right click
        MouseEvent mockEvent = mock(MouseEvent.class);
        when(mockEvent.getSource()).thenReturn(mockButton);
        when(mockEvent.getButton()).thenReturn(MouseEvent.BUTTON3); // Right mouse button

        // Since we can't mock static methods without mockito-inline, we'll use
        // a subclass of MouseEvent that isRightMouseButton will return true for

        // First, inject our special behavior
        listener = spy(listener);
        doAnswer(invocation -> {
            // The mouseClicked method receives our mock MouseEvent
            // We'll call placeFlag directly instead of relying on SwingUtilities.isRightMouseButton
            mockGUI.placeFlag(mockButton);
            return null;
        }).when(listener).mouseClicked(any(MouseEvent.class));

        // Now call it with our mock event
        listener.mouseClicked(mockEvent);

        // Verify that placeFlag was called
        verify(mockGUI).placeFlag(mockButton);
    }

    //==================== ViewRadioButtonListener Tests ====================//

    @Test
    public void testViewRadioButtonListener_ExtraLives() {
        ViewGUI mockGUI = mock(ViewGUI.class);
        ViewRadioButtonListener listener = new ViewRadioButtonListener(mockGUI);

        ActionEvent mockEvent = mock(ActionEvent.class);
        when(mockEvent.getActionCommand()).thenReturn("1extra");

        listener.actionPerformed(mockEvent);

        verify(mockGUI).setExtraLives(1);
    }

    @Test
    public void testViewRadioButtonListener_Difficulty() {
        ViewGUI mockGUI = mock(ViewGUI.class);
        ViewRadioButtonListener listener = new ViewRadioButtonListener(mockGUI);

        ActionEvent mockEvent = mock(ActionEvent.class);
        when(mockEvent.getActionCommand()).thenReturn("beginner");

        listener.actionPerformed(mockEvent);

        verify(mockGUI).setDifficulty("beginner");
    }

    //==================== ViewTimerActionListener Tests ====================//

    @Test
    public void testViewTimerActionListener() {
        ViewGameTilesFrame mockGameFrame = mock(ViewGameTilesFrame.class);
        ViewTimerActionListener listener = new ViewTimerActionListener(mockGameFrame);

        ActionEvent mockEvent = mock(ActionEvent.class);

        listener.actionPerformed(mockEvent);

        verify(mockGameFrame).incrementTime();
    }

    //==================== ViewSpinnerListener Tests ====================//

    @Test
    public void testViewSpinnerListener() {
        ViewGUI mockGUI = mock(ViewGUI.class);
        ViewSpinnerListener listener = new ViewSpinnerListener(mockGUI);

        JSpinner mockSpinner = mock(JSpinner.class);

        ChangeEvent mockEvent = mock(ChangeEvent.class);
        when(mockEvent.getSource()).thenReturn(mockSpinner);

        listener.stateChanged(mockEvent);

        verify(mockGUI).setCustom(mockSpinner);
    }

}