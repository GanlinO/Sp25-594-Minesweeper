import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JSpinner;
import javax.swing.SwingUtilities;

/**
 * Main view class that manages all UI components of the Minesweeper game.
 * Creates and manages frames for game start, gameplay, and end game screens.
 * Implements ControllerToViewGUI to receive updates from the Controller.
 */
public class ViewGUI implements ControllerToViewGUI {

    /* Frame hierarchy and responsibilities:
     *  StartFrame:
     *   - Select difficulty (beginner, intermediate, advanced, custom)
     *   - Configure extra lives option
     *   - Toggle logical mode
     *
     *  Game Frame:
     *   - Menu bar with Game (new game, exit), Help (rules)
     *   - Grid of tile buttons
     *   - HUD with mines left counter, timer, lives indicator
     *   - Hint and AI solver buttons
     *
     *  End Frame:
     *   - Win/loss message
     *   - Game time
     *   - Best times for each difficulty
     *   - Statistics (games played, won, percentage)
     *   - Play again and exit buttons
     */

    /** Reference to the controller for model data and game logic */
    private ViewGUIToController myController;

    /** UI frames for different game states */
    private ViewStartFrame startframe;
    private ViewGameTilesFrame gameframe;
    private ViewEndFrame endframe;

    /** Flag indicating if logical mode is enabled */
    private boolean logicalMode = false;

    /**
     * Sets the logical mode state.
     *
     * @param b True to enable logical mode, false to disable
     */
    public void setLogicalMode(boolean b) {
        logicalMode = b;
    }

    /**
     * Creates a new ViewGUI with a reference to the controller.
     *
     * @param c Reference to the controller
     */
    public ViewGUI(ViewGUIToController c) {
        myController = c;
    }

    /**
     * Checks if logical mode is enabled.
     *
     * @return True if logical mode is enabled, false otherwise
     */
    public boolean isLogicalMode() {
        return logicalMode;
    }

    /**
     * Initializes the game by creating the start frame with available difficulties.
     * Displays the initial settings screen to the user.
     *
     * @param diffs List of available difficulty options
     */
    public void go(ArrayList<String> diffs) {
        startframe = new ViewStartFrame(this, diffs);
        if(startframe == null)
            System.exit(NULL_EXIT_CODE);
    }

    /**
     * Handles difficulty selection from the start frame.
     * Enables or disables custom settings spinners as needed.
     *
     * @param difficulty The selected difficulty level
     */
    public void setDifficulty(String difficulty) {
        if(myController == null || difficulty == null || startframe == null)
            System.exit(NULL_EXIT_CODE);
        if(difficulty.equals("custom"))
            startframe.enableCustomSpinners(true);
        else
            startframe.enableCustomSpinners(false);
        myController.setDifficulty(difficulty);
    }

    /**
     * Updates the extra lives setting in the controller.
     *
     * @param extralives Number of extra lives to set
     */
    public void setExtraLives(int extralives) {
        if(myController == null)
            System.exit(NULL_EXIT_CODE);
        myController.setExtraLives(extralives);
    }

    /**
     * Processes spinner value changes for custom game settings.
     *
     * @param spinner The spinner component that was changed
     */
    public void setCustom(JSpinner spinner) {
        if(startframe == null || myController == null)
            System.exit(NULL_EXIT_CODE);
        String info = startframe.getCustomInfo(spinner);
        int myNum;
        try {
            myNum = Integer.parseInt(info.substring(3));
        } catch(NumberFormatException ex) {
            myNum = 0;
        }
        if(info.startsWith("row"))
            myController.setCustomRows(myNum);
        else if(info.startsWith("col"))
            myController.setCustomColumns(myNum);
        else // mines
            myController.setCustomMines(myNum);
        myController.setDifficulty("custom");
    }

    /**
     * Exits the game by disposing all active frames.
     */
    public void exitGame() {
        if(startframe != null)
            startframe.dispose();
        if(gameframe != null)
            gameframe.dispose();
        if(endframe != null)
            endframe.dispose();
    }

    /**
     * Resets the game to start a new one with initial settings screen.
     */
    public void playAgain() {
        if(startframe != null)
            startframe.dispose();
        if(gameframe != null)
            gameframe.dispose();
        if(endframe != null)
            endframe.dispose();

        if(myController == null)
            System.exit(NULL_EXIT_CODE);
        myController.reset();
        startframe = new ViewStartFrame(this, myController.getDifficulties());
    }

    /**
     * Starts a new game with the current settings.
     * Creates the game frame and initializes the game grid.
     */
    public void playGame() {
        if(gameframe != null)
            gameframe.dispose();
        if(myController == null)
            System.exit(NULL_EXIT_CODE);
        myController.setLogicalMode(logicalMode);
        boolean success = myController.startGame();
        if(!success) {
            createPopUp("Custom number of mines should be less than rows*columns.", 400, 200, false);
        } else {
            if(startframe != null)
                startframe.dispose();

            gameframe = new ViewGameTilesFrame(
                    this,
                    myController.getGrid(),
                    myController.getNumMines());

            /* If logical mode is enabled, reveal the pre-expanded front immediately */
            if(logicalMode) {
                gameframe.refresh(
                        myController.getExposed(),
                        myController.getEmptyTileString());
            }
        }
    }

    /**
     * Gets the game rules from the controller.
     *
     * @return String containing the game rules
     */
    public String getRules() {
        if(myController != null)
            return myController.getRules();
        else
            return "";
    }

    /**
     * Creates a popup window to display a message to the user.
     *
     * @param msg Message to display
     * @param width Width of the popup window
     * @param height Height of the popup window
     * @param timerUsage Whether to pause the game timer while showing the popup
     */
    public void createPopUp(String msg, int width, int height, boolean timerUsage) {
        if(timerUsage && gameframe != null)
            gameframe.stopTimer();
        new ViewPopupHelp(this, msg, width, height, timerUsage);
    }

    /**
     * Handles the closing of a popup window.
     * Restarts the game timer if necessary.
     *
     * @param timerUsage Whether the timer was paused for this popup
     */
    public void endHelpPopup(boolean timerUsage) {
        if(timerUsage && gameframe != null)
            gameframe.startTimer();
    }

    /**
     * Processes a tile click event.
     * Updates game state and checks for win/loss conditions.
     *
     * @param rowcol String containing the row and column of the clicked tile
     */
    public void tilePressed(String rowcol) {
        if(myController == null || gameframe == null)
            System.exit(NULL_EXIT_CODE);
        int row = Integer.parseInt(rowcol.split(",")[0]);
        int col = Integer.parseInt(rowcol.split(",")[1]);
        myController.tilePressed(row, col, gameframe.getCurrentTime());

        int extralives = myController.getExtraLivesLeft();
        if(extralives >= 0) // if using extra lives
        {
            gameframe.updateExtraLives(extralives);
        }

        if(myController.playerLost()) {
            gameframe.playerLost(myController.getLastPressed());
            endframe = new ViewEndFrame(this, false, gameframe.stopTimer(),
                    myController.getBestTime(), myController.getTotalGamesPlayed(),
                    myController.getTotalGamesWon());
        } else if(myController.playerWon()) {
            endframe = new ViewEndFrame(this, true, gameframe.stopTimer(),
                    myController.getBestTime(), myController.getTotalGamesPlayed(),
                    myController.getTotalGamesWon());
        } else {
            // Check if last pressed is a mine
            // This handles cases where autocomplete reveals a mine
            int[] last = myController.getLastPressed();
            gameframe.pressed(last[0], last[1], myController.getMineString());
        }
    }

    /**
     * Handles right-click events to place or remove flags.
     *
     * @param button The button that was right-clicked
     */
    public void placeFlag(JButton button) {
        if(myController == null || gameframe == null)
            System.exit(NULL_EXIT_CODE);
        int row = Integer.parseInt(button.getActionCommand().split(",")[0]);
        int col = Integer.parseInt(button.getActionCommand().split(",")[1]);
        boolean flagged = gameframe.placeFlag(button);
        myController.placeFlag(flagged, row, col);
    }

    /**
     * Gets the string representation of an empty tile.
     *
     * @return String representing an empty tile
     */
    public String getEmptyTileString() {
        if(myController == null)
            System.exit(NULL_EXIT_CODE);
        return myController.getEmptyTileString();
    }

    /**
     * Updates the game display to reflect the current exposed tiles.
     *
     * @param exposed 2D array indicating which tiles are exposed
     * @param emptyTileText String representation of empty tiles
     */
    public void refresh(boolean[][] exposed, String emptyTileText) {
        if(gameframe == null)
            System.exit(NULL_EXIT_CODE);
        gameframe.refresh(exposed, emptyTileText);
    }

    /**
     * Toggles the extra lives option in the start frame.
     */
    public void showExtraLives() {
        if(startframe == null)
            System.exit(NULL_EXIT_CODE);
        startframe.extraLives();
    }

    /**
     * Gets the number of extra lives remaining.
     *
     * @return Number of extra lives left
     */
    public int getExtraLivesLeft() {
        if(myController == null)
            System.exit(NULL_EXIT_CODE);
        return myController.getExtraLivesLeft();
    }

    /**
     * Activates the hint feature to help the player.
     * Flags a strategically useful mine and updates the display.
     */
    public void hint() {
        int[] rc = myController.applyHint();        // Get hint coordinates
        if(rc[0] == -1) {
            createPopUp("No hint available – all mines flagged!", 300, 150, false);
            return;
        }
        gameframe.showHint(rc[0], rc[1]);           // Highlight the hint
        gameframe.updateMinesLeft(
                myController.getNumMines() - myController.getNumFlags());

        if(myController.playerWon()) endGame(true);   // Check for win
    }

    /**
     * Activates the AI solver to automatically solve the game.
     * Only functions in logical mode.
     */
    public void autoSolve() {
        if(!logicalMode) return;                 // Only available in logical mode

        new Thread(() -> {                        // Run in a separate thread
            myController.propagateLogicalConsequences();     // Initial expansion

            while(!myController.playerWon() && !myController.playerLost()) {
                int[] m = myController.nextLogicalMine();    // Find next certain mine
                if(m == null) break;                        // No more certain mines

                int r = m[0], c = m[1];

                // Visualize the flag
                SwingUtilities.invokeLater(() ->
                        gameframe.markMineGreen(r, c));

                // Update the model
                myController.placeFlag(true, r, c);
                myController.propagateLogicalConsequences();  // Cascade opens

                // Update the display
                SwingUtilities.invokeLater(() -> {
                    gameframe.refresh(myController.getExposed(),
                            myController.getEmptyTileString());
                    gameframe.updateMinesLeft(
                            myController.getNumMines() - myController.getNumFlags());
                });

                pause(350);                                   // Delay for visibility
            }

            // Ensure final state is displayed
            SwingUtilities.invokeLater(() ->
                    gameframe.refresh(myController.getExposed(),
                            myController.getEmptyTileString()));

            // Flag any isolated mines
            java.util.List<int[]> extra =
                    myController.flagIsolatedMines();

            SwingUtilities.invokeLater(() -> {
                for(int[] p : extra)
                    gameframe.markMineGreen(p[0], p[1]);
                gameframe.updateMinesLeft(
                        myController.getNumMines() - myController.getNumFlags());
            });

            // Check for win
            if(myController.playerWon()) {
                SwingUtilities.invokeLater(() -> endGame(true));
            }
        }).start();
    }

    /**
     * Utility method to pause execution briefly.
     * Used by the AI solver to create a visual delay between moves.
     *
     * @param ms Milliseconds to pause
     */
    private static void pause(long ms) {
        try {
            Thread.sleep(ms);
        } catch(Exception e) {
            // Ignore interruptions
        }
    }

    /**
     * Ends the current game and displays the end game screen.
     *
     * @param won True if the player won, false if lost
     */
    private void endGame(boolean won) {
        if(gameframe == null) return;
        long roundTime = gameframe.stopTimer();
        endframe = new ViewEndFrame(this, won,
                roundTime,
                myController.getBestTime(),
                myController.getTotalGamesPlayed(),
                myController.getTotalGamesWon());
        gameframe.dispose();
    }
}