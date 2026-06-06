/**
 * Represents a single square cell in the maze grid.
 */
public class Cell {
    public final int row;
    public final int col;

    // Walls array tracking boundaries: [Top (0), Right (1), Bottom (2), Left (3)]
    public final boolean[] walls;

    // Generation states
    public boolean visitedByGenerator;

    // Pathfinding states
    public boolean isCurrentlySearching;
    public boolean isPartOfFinalPath;

    // Parent cell pointer to reconstruct the final path
    public Cell parent;

    /**
     * Constructs a new Cell with all walls intact and default flags.
     *
     * @param row The row coordinate in the grid.
     * @param col The column coordinate in the grid.
     */
    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        this.walls = new boolean[]{true, true, true, true};
        this.visitedByGenerator = false;
        this.isCurrentlySearching = false;
        this.isPartOfFinalPath = false;
        this.parent = null;
    }

    /**
     * Clears pathfinding flags and parent pointers without altering the maze walls
     * or the generation status.
     */
    public void resetSearchState() {
        this.isCurrentlySearching = false;
        this.isPartOfFinalPath = false;
        this.parent = null;
    }
}
