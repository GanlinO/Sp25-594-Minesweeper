import java.util.ArrayList;
//implemented by the Model and used by the Controller to communicate with the Model
public interface ControllerToModel {

    public final int NULL_EXIT_CODE = -1;

    public final String MINE = "M"; //represents a mine tile in the game
    public final String EMPTY = " "; //represents an empty tile in the game
    public final String RULES = "Rules for Minesweeper:\n\n"
            + "In Minesweeper, you are given a board of tiles. "
            + "These tiles may contain mines ("+MINE+") or numbers, "
            + "or be empty ("+EMPTY+"). "
            + "If you click on a tile with a mine, and you do not "
            + "have any more lives, you lose. "
            + "Tiles with numbers (1-8) indicate how many bombs are "
            + "immediately adjacent to that tile (touching a side "
            + "or a corner of that tile). "
            + "If you click on a tile and an empty square appears, "
            + "it indicates that no bombs are immediately adjacent "
            + "to that tile, and so all surrounding tiles "
            + "will be displayed. "
            + "The objective is to fill in all non-mine tiles by "
            + "clicking them (and optionally flagging the mine tiles). "
            + "To flag a tile you think is a mine, point and right-click. "
            + "To unflag a tile you previously flagged, point and right-click again. "
            + "You can click a numbered tile after it is initially opened "
            + "to display all adjacent tiles if the correct number of "
            + "flags have been placed (and/or mines hit in extra-lives "
            + "mode) on surrounding tiles. "
            + "Tiles on the edge of the board have fewer adjacent tiles "
            + "(the board does not wrap around the edges). "
            + "The number of mines minus the number of flags used, the number "
            + "of lives left (if applicable), and the time passed are "
            + "displayed at the top of the game.\n"
            + "Good luck and have fun!";


    public void setDifficulty(String difficulty);
    public ArrayList<String> getDifficulties();
    public boolean startGame();
    public int getNumMines();
    public String [][] getGrid();
    public boolean [][] tilePressed(int row, int col, long currentTime);
    public void tileFlagged(boolean flagged,int row, int col);
    public boolean playerLost();
    public boolean playerWon();
    public void resetGame();
    public int[] getLastPressed();
    public void setExtraLives(int lives);
    public int getExtraLivesLeft();
    public void setCustomRows(int rows);
    public void setCustomColumns(int cols);
    public void setCustomMines(int mines);
    public long getTotalGamesWon();
    public long getTotalGamesPlayed();
    public String getBestTimes();
    public int[] applyHint();
    public int getNumFlags();
}

