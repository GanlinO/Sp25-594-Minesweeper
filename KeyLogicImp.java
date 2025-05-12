import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Comparator;

/**
 * The KeyLogicImp class implements logical analysis for Minesweeper game.
 * It provides methods for suggesting moves to the player, both for safety
 * and strategic advantage.
 */
public class KeyLogicImp {

    // Constants
    private static final String MINE = "M";
    private static final String EMPTY = " ";

    /**
     * Suggests a cell that can be logically inferred to contain a mine.
     * Uses only the information currently visible to the player.
     *
     * @param actualGrid The complete game grid with all cell values
     * @param exposedTiles Boolean matrix indicating which tiles are exposed
     * @param flaggedTiles Boolean matrix indicating which tiles are flagged
     * @return Coordinates [row, col] of a cell that must be a mine, or null if none found
     */
    public static int[] suggestCellToRevealAsMine(String[][] actualGrid, boolean[][] exposedTiles, boolean[][] flaggedTiles) {
        // Only use information that is visible to the player (exposed tiles)
        int rows = actualGrid.length;
        int cols = actualGrid[0].length;

        // Check each exposed numbered cell
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (exposedTiles[row][col] && isNumeric(actualGrid[row][col])) {
                    int number = Integer.parseInt(actualGrid[row][col]);

                    // Get all hidden neighbors and flagged neighbors
                    ArrayList<int[]> hiddenNeighbors = getHiddenNeighbors(row, col, rows, cols, exposedTiles);
                    int flaggedCount = countFlaggedNeighbors(row, col, rows, cols, flaggedTiles);

                    // If (number - flaggedCount) equals remaining hidden cells,
                    // then all hidden cells must be mines
                    if (number - flaggedCount == hiddenNeighbors.size() && hiddenNeighbors.size() > 0) {
                        for (int[] neighbor : hiddenNeighbors) {
                            if (!flaggedTiles[neighbor[0]][neighbor[1]]) {
                                return neighbor; // Return the first unflagged mine
                            }
                        }
                    }
                }
            }
        }

        // More complex inference logic for overlapping constraints
        return findMineByConstraintOverlap(actualGrid, exposedTiles, flaggedTiles);
    }

    /**
     * Uses constraint overlap to find mines when simple methods fail.
     * This method handles more complex logical inferences.
     */
    private static int[] findMineByConstraintOverlap(String[][] actualGrid, boolean[][] exposedTiles, boolean[][] flaggedTiles) {
        int rows = actualGrid.length;
        int cols = actualGrid[0].length;

        // For each pair of adjacent numbered cells, check for constraint overlap
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (exposedTiles[row][col] && isNumeric(actualGrid[row][col])) {
                    int number1 = Integer.parseInt(actualGrid[row][col]);
                    ArrayList<int[]> hiddenNeighbors1 = getHiddenNeighbors(row, col, rows, cols, exposedTiles);
                    int flaggedCount1 = countFlaggedNeighbors(row, col, rows, cols, flaggedTiles);
                    int remainingMines1 = number1 - flaggedCount1;

                    // Check all neighbors that are also numbered cells
                    for (int[] offset : getNeighborOffsets()) {
                        int r2 = row + offset[0];
                        int c2 = col + offset[1];

                        // Check if the neighbor is valid and is an exposed number
                        if (isValidCell(r2, c2, rows, cols) &&
                                exposedTiles[r2][c2] &&
                                isNumeric(actualGrid[r2][c2])) {

                            int number2 = Integer.parseInt(actualGrid[r2][c2]);
                            ArrayList<int[]> hiddenNeighbors2 = getHiddenNeighbors(r2, c2, rows, cols, exposedTiles);
                            int flaggedCount2 = countFlaggedNeighbors(r2, c2, rows, cols, flaggedTiles);
                            int remainingMines2 = number2 - flaggedCount2;

                            // Find cells that are neighbors of cell1 but not of cell2
                            ArrayList<int[]> uniqueToCell1 = new ArrayList<>();
                            for (int[] neighbor : hiddenNeighbors1) {
                                boolean found = false;
                                for (int[] neighbor2 : hiddenNeighbors2) {
                                    if (neighbor[0] == neighbor2[0] && neighbor[1] == neighbor2[1]) {
                                        found = true;
                                        break;
                                    }
                                }
                                if (!found) {
                                    uniqueToCell1.add(neighbor);
                                }
                            }

                            // Find common hidden neighbors
                            ArrayList<int[]> commonHidden = new ArrayList<>();
                            for (int[] neighbor : hiddenNeighbors1) {
                                for (int[] neighbor2 : hiddenNeighbors2) {
                                    if (neighbor[0] == neighbor2[0] && neighbor[1] == neighbor2[1]) {
                                        commonHidden.add(neighbor);
                                        break;
                                    }
                                }
                            }

                            // If cell1 has remainingMines1 exactly equal to its unique cells,
                            // then all common cells must not be mines
                            if (uniqueToCell1.size() == remainingMines1 && uniqueToCell1.size() > 0 && !uniqueToCell1.isEmpty()) {
                                // All unique cells must be mines
                                for (int[] mine : uniqueToCell1) {
                                    if (!flaggedTiles[mine[0]][mine[1]]) {
                                        return mine;
                                    }
                                }
                            }

                            // If cell2 requires more mines than are available in its non-common cells,
                            // then some common cells must be mines
                            ArrayList<int[]> uniqueToCell2 = new ArrayList<>();
                            for (int[] neighbor : hiddenNeighbors2) {
                                boolean found = false;
                                for (int[] neighbor1 : hiddenNeighbors1) {
                                    if (neighbor[0] == neighbor1[0] && neighbor[1] == neighbor1[1]) {
                                        found = true;
                                        break;
                                    }
                                }
                                if (!found) {
                                    uniqueToCell2.add(neighbor);
                                }
                            }

                            // If cell2's remaining mines minus its unique cells is greater than zero,
                            // and that equals the number of common cells, all common cells must be mines
                            if (remainingMines2 > uniqueToCell2.size() &&
                                    (remainingMines2 - uniqueToCell2.size() == commonHidden.size()) &&
                                    !commonHidden.isEmpty()) {
                                // All common cells must be mines
                                for (int[] mine : commonHidden) {
                                    if (!flaggedTiles[mine[0]][mine[1]]) {
                                        return mine;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return null; // No definite mine found through constraint overlap
    }


    /**
     * Suggests the next mine to reveal that would lead to the largest expansion.
     * This uses all information in the grid (not just visible information).
     *
     * @param actualGrid The complete game grid with all cell values
     * @param exposedTiles Boolean matrix indicating which tiles are exposed
     * @param flaggedTiles Boolean matrix indicating which tiles are flagged
     * @return Coordinates [row, col] of the mine that would lead to the largest expansion
     */
    public static int[] suggestNextMineToReveal(String[][] actualGrid, boolean[][] exposedTiles, boolean[][] flaggedTiles) {
        int rows = actualGrid.length;
        int cols = actualGrid[0].length;

        // Use a priority queue to rank mines by their expansion potential
        PriorityQueue<int[]> mineQueue = new PriorityQueue<>(new Comparator<int[]>() {
            @Override
            public int compare(int[] mine1, int[] mine2) {
                // Higher expansion score is better
                return Integer.compare(
                        calculateExpansionScore(mine2[0], mine2[1], actualGrid, exposedTiles),
                        calculateExpansionScore(mine1[0], mine1[1], actualGrid, exposedTiles)
                );
            }
        });

        // Add all mines to the queue
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (actualGrid[row][col].equals(MINE) && !exposedTiles[row][col]) {
                    mineQueue.add(new int[]{row, col});
                }
            }
        }

        // Return the mine with the highest expansion score
        return mineQueue.isEmpty() ? null : mineQueue.poll();
    }

    /**
     * Calculates how many tiles would be revealed if a specific mine was revealed.
     * This is used to prioritize mines for the hint system.
     */
    private static int calculateExpansionScore(int mineRow, int mineCol, String[][] actualGrid, boolean[][] exposedTiles) {
        int rows = actualGrid.length;
        int cols = actualGrid[0].length;
        int score = 0;

        // Look at neighboring numbered cells of this mine
        for (int[] offset : getNeighborOffsets()) {
            int row = mineRow + offset[0];
            int col = mineCol + offset[1];

            if (isValidCell(row, col, rows, cols) && isNumeric(actualGrid[row][col]) && exposedTiles[row][col]) {
                // Simulate clicking this numbered cell after the mine is revealed
                score += countExpansionFromCell(row, col, actualGrid, exposedTiles);
            }
        }

        return score;
    }

    /**
     * Estimates how many cells would be revealed by clicking a numbered cell.
     * Uses a simulation of the flood fill algorithm.
     */
    private static int countExpansionFromCell(int row, int col, String[][] actualGrid, boolean[][] exposedTiles) {
        int rows = actualGrid.length;
        int cols = actualGrid[0].length;
        boolean[][] visited = new boolean[rows][cols];
        int count = 0;

        // Count all flagged or exposed mines around this cell
        int surroundingMines = 0;
        for (int[] offset : getNeighborOffsets()) {
            int r = row + offset[0];
            int c = col + offset[1];

            if (isValidCell(r, c, rows, cols) &&
                    actualGrid[r][c].equals(MINE) &&
                    (exposedTiles[r][c] || isMineNeighborHighlighted(r, c, actualGrid, exposedTiles))) {
                surroundingMines++;
            }
        }

        // If all mines are accounted for, simulate expansion
        if (surroundingMines == Integer.parseInt(actualGrid[row][col])) {
            count = simulateFloodFill(row, col, actualGrid, exposedTiles, visited);
        }

        return count;
    }

    /**
     * Simulates the flood fill that would occur if a cell was clicked.
     */
    private static int simulateFloodFill(int row, int col, String[][] actualGrid, boolean[][] exposedTiles, boolean[][] visited) {
        int rows = actualGrid.length;
        int cols = actualGrid[0].length;
        int count = 0;

        // Process all neighbors
        for (int[] offset : getNeighborOffsets()) {
            int r = row + offset[0];
            int c = col + offset[1];

            if (isValidCell(r, c, rows, cols) &&
                    !visited[r][c] &&
                    !exposedTiles[r][c] &&
                    !actualGrid[r][c].equals(MINE)) {

                visited[r][c] = true;
                count++; // Count this cell

                // If this is an empty cell, recursively expand
                if (actualGrid[r][c].equals(EMPTY)) {
                    count += simulateFloodFill(r, c, actualGrid, exposedTiles, visited);
                }
            }
        }

        return count;
    }

    /**
     * Checks if a specific cell position is valid within the grid.
     */
    private static boolean isValidCell(int row, int col, int rows, int cols) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    /**
     * Checks if a string is numeric.
     */
    private static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Gets all hidden (unexposed) neighbors of a cell.
     */
    private static ArrayList<int[]> getHiddenNeighbors(int row, int col, int rows, int cols, boolean[][] exposedTiles) {
        ArrayList<int[]> neighbors = new ArrayList<>();

        for (int[] offset : getNeighborOffsets()) {
            int r = row + offset[0];
            int c = col + offset[1];

            if (isValidCell(r, c, rows, cols) && !exposedTiles[r][c]) {
                neighbors.add(new int[]{r, c});
            }
        }

        return neighbors;
    }

    /**
     * Counts how many flagged cells are adjacent to a given cell.
     */
    private static int countFlaggedNeighbors(int row, int col, int rows, int cols, boolean[][] flaggedTiles) {
        int count = 0;

        for (int[] offset : getNeighborOffsets()) {
            int r = row + offset[0];
            int c = col + offset[1];

            if (isValidCell(r, c, rows, cols) && flaggedTiles[r][c]) {
                count++;
            }
        }

        return count;
    }

    /**
     * Checks if a mine is already highlighted by its neighboring cells.
     * A mine is considered highlighted if one of its exposed neighbor cells
     * has a number and all other mines around that number are accounted for.
     */
    private static boolean isMineNeighborHighlighted(int mineRow, int mineCol, String[][] actualGrid, boolean[][] exposedTiles) {
        int rows = actualGrid.length;
        int cols = actualGrid[0].length;

        for (int[] offset : getNeighborOffsets()) {
            int row = mineRow + offset[0];
            int col = mineCol + offset[1];

            if (isValidCell(row, col, rows, cols) &&
                    exposedTiles[row][col] &&
                    isNumeric(actualGrid[row][col])) {

                int number = Integer.parseInt(actualGrid[row][col]);
                int surroundingMines = 0;

                // Count mines around this numbered cell
                for (int[] offset2 : getNeighborOffsets()) {
                    int r = row + offset2[0];
                    int c = col + offset2[1];

                    if (isValidCell(r, c, rows, cols) && actualGrid[r][c].equals(MINE)) {
                        surroundingMines++;
                    }
                }

                // If number matches mines, this mine is highlighted
                if (surroundingMines == number) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Returns the eight possible neighbor offsets.
     */
    private static int[][] getNeighborOffsets() {
        return new int[][] {
                {-1, -1}, {-1, 0}, {-1, 1},
                {0, -1},           {0, 1},
                {1, -1},  {1, 0},  {1, 1}
        };
    }
}
