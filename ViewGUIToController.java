import java.util.ArrayList;
//implemented by the Controller for the ViewGUI to communicate with the controller through
public interface ViewGUIToController {

    public final int NULL_EXIT_CODE = -1;

    public void setDifficulty(String difficulty);
    public ArrayList<String> getDifficulties();
    public boolean startGame();
    public int getNumMines();
    public String [][] getGrid();
    public void tilePressed(int row, int col, long currentTime);
    public void placeFlag(boolean flagged,int row, int col);
    public boolean playerLost();
    public boolean playerWon();
    public void reset();
    public String getRules();
    public int[] getLastPressed();
    public void setExtraLives(int lives);
    public int getExtraLivesLeft();
    public String getMineString();
    public String getEmptyTileString();
    public void setCustomRows(int rows);
    public void setCustomColumns(int cols);
    public void setCustomMines(int mines);
    public long getTotalGamesWon();
    public long getTotalGamesPlayed();
    public String getBestTime();
    /*  Hint feature  */
    /** Flags the mine that unlocks the largest expansion and returns {row,col};
     returns {-1,-1} if no mine remains. */
    int[] applyHint();

    /** Current number of flagged squares (for the Mines-Left HUD). */
    int  getNumFlags();
}

