import javax.swing.*;
import java.awt.*;
import java.util.Random;

/**
 * The core graphics panel and algorithm controller.
 * Extends JPanel to render the maze grid and animates generation/solving.
 */
public class MazePanel extends JPanel {
    public static final int ROWS = 20;
    public static final int COLS = 20;
    public static final int CELL_SIZE = 30;

    private final Cell[][] grid;
    private final Random random = new Random();
    private final Timer timer;

    // State callback to notify parent JFrame when algorithms start/stop
    private Runnable onStateChanged;

    // Maze generation states
    private MyStack<Cell> genStack;
    private boolean generating = false;

    // Pathfinding states
    private MyStack<Cell> solveStack;
    private MyQueue<Cell> solveQueue;
    private boolean solving = false;
    private boolean solved = false;
    private String activeAlgorithm = "Breadth-First Search";

    /**
     * Initializes the grid cells and the execution timer.
     */
    public MazePanel() {
        this.grid = new Cell[ROWS][COLS];
        initializeGrid();

        // Preferred size matching grid dimensions
        setPreferredSize(new Dimension(COLS * CELL_SIZE, ROWS * CELL_SIZE));
        setBackground(new Color(30, 30, 30)); // Dark background

        // Timer setup (ticks execute exactly one step of active algorithm)
        this.timer = new Timer(30, e -> {
            if (generating) {
                stepGenerator();
            } else if (solving) {
                stepSolver();
            }
            repaint();
        });
    }

    /**
     * Set a callback to execute whenever the internal state changes.
     */
    public void setOnStateChanged(Runnable callback) {
        this.onStateChanged = callback;
    }

    /**
     * Resets the entire grid to a clean slate (all walls intact, unvisited).
     */
    private void initializeGrid() {
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                grid[r][c] = new Cell(r, c);
            }
        }
    }

    /**
     * Starts the Randomized DFS maze generation from the top-left cell.
     */
    public void startGeneration() {
        if (generating || solving) return;

        timer.stop();
        initializeGrid();
        solved = false;

        genStack = new MyStack<>();
        Cell start = grid[0][0];
        start.visitedByGenerator = true;
        genStack.push(start);

        generating = true;
        if (onStateChanged != null) onStateChanged.run();
        timer.start();
    }

    /**
     * Starts the pathfinding solver (BFS or DFS) on the generated maze.
     * Searches from grid[0][0] to grid[ROWS-1][COLS-1].
     */
    public void startSolving(String algorithm) {
        if (generating || solving) return;

        timer.stop();
        this.activeAlgorithm = algorithm;

        // Clear previous search states while keeping the maze walls
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                grid[r][c].resetSearchState();
            }
        }
        solved = false;

        Cell start = grid[0][0];
        start.isCurrentlySearching = true;

        if ("Breadth-First Search".equals(algorithm)) {
            solveQueue = new MyQueue<>();
            solveQueue.enqueue(start);
        } else {
            solveStack = new MyStack<>();
            solveStack.push(start);
        }

        solving = true;
        if (onStateChanged != null) onStateChanged.run();
        timer.start();
    }

    /**
     * Dynamically adjusts the speed of the timer execution.
     */
    public void setSpeed(int delayMs) {
        timer.setDelay(delayMs);
    }

    public boolean isBusy() {
        return generating || solving;
    }

    public boolean isSolved() {
        return solved;
    }

    /**
     * Single step of the randomized DFS generator.
     */
    private void stepGenerator() {
        if (!genStack.isEmpty()) {
            Cell current = genStack.peek();
            Cell[] neighbors = getUnvisitedNeighbors(current);

            if (neighbors.length > 0) {
                // Select a random unvisited neighbor
                Cell next = neighbors[random.nextInt(neighbors.length)];
                removeWalls(current, next);
                next.visitedByGenerator = true;
                genStack.push(next);
            } else {
                // Backtrack
                genStack.pop();
            }
        } else {
            // Done generating
            generating = false;
            timer.stop();
            if (onStateChanged != null) onStateChanged.run();
        }
    }

    /**
     * Single step of the active solver (BFS or DFS).
     */
    private void stepSolver() {
        if ("Breadth-First Search".equals(activeAlgorithm)) {
            stepBFS();
        } else {
            stepDFS();
        }
    }

    private void stepBFS() {
        if (!solveQueue.isEmpty()) {
            Cell current = solveQueue.dequeue();

            // Check if goal reached
            if (current.row == ROWS - 1 && current.col == COLS - 1) {
                reconstructPath(current);
                solving = false;
                solved = true;
                timer.stop();
                if (onStateChanged != null) onStateChanged.run();
                return;
            }

            Cell[] neighbors = getAccessibleNeighbors(current);
            for (Cell next : neighbors) {
                next.isCurrentlySearching = true;
                next.parent = current;
                solveQueue.enqueue(next);
            }
        } else {
            // Unsolvable (should not happen in a perfect maze)
            solving = false;
            timer.stop();
            if (onStateChanged != null) onStateChanged.run();
        }
    }

    private void stepDFS() {
        if (!solveStack.isEmpty()) {
            Cell current = solveStack.pop();

            // Check if goal reached
            if (current.row == ROWS - 1 && current.col == COLS - 1) {
                reconstructPath(current);
                solving = false;
                solved = true;
                timer.stop();
                if (onStateChanged != null) onStateChanged.run();
                return;
            }

            Cell[] neighbors = getAccessibleNeighbors(current);
            for (Cell next : neighbors) {
                next.isCurrentlySearching = true;
                next.parent = current;
                solveStack.push(next);
            }
        } else {
            // Unsolvable
            solving = false;
            timer.stop();
            if (onStateChanged != null) onStateChanged.run();
        }
    }

    /**
     * Traces back parent pointers from the end cell to highlight the path.
     */
    private void reconstructPath(Cell endCell) {
        Cell curr = endCell;
        while (curr != null) {
            curr.isPartOfFinalPath = true;
            curr = curr.parent;
        }
    }

    /**
     * Finds unvisited neighbors in 4 cardinal directions during generation.
     */
    private Cell[] getUnvisitedNeighbors(Cell cell) {
        Cell[] temp = new Cell[4];
        int count = 0;
        int r = cell.row;
        int c = cell.col;

        // Top
        if (r > 0 && !grid[r - 1][c].visitedByGenerator) {
            temp[count++] = grid[r - 1][c];
        }
        // Right
        if (c < COLS - 1 && !grid[r][c + 1].visitedByGenerator) {
            temp[count++] = grid[r][c + 1];
        }
        // Bottom
        if (r < ROWS - 1 && !grid[r + 1][c].visitedByGenerator) {
            temp[count++] = grid[r + 1][c];
        }
        // Left
        if (c > 0 && !grid[r][c - 1].visitedByGenerator) {
            temp[count++] = grid[r][c - 1];
        }

        Cell[] result = new Cell[count];
        System.arraycopy(temp, 0, result, 0, count);
        return result;
    }

    /**
     * Finds neighbors that are accessible (no wall blockages) and unvisited
     * by the search wavefront.
     */
    private Cell[] getAccessibleNeighbors(Cell cell) {
        Cell[] temp = new Cell[4];
        int count = 0;
        int r = cell.row;
        int c = cell.col;

        // Top
        if (r > 0 && !cell.walls[0] && !grid[r - 1][c].isCurrentlySearching) {
            temp[count++] = grid[r - 1][c];
        }
        // Right
        if (c < COLS - 1 && !cell.walls[1] && !grid[r][c + 1].isCurrentlySearching) {
            temp[count++] = grid[r][c + 1];
        }
        // Bottom
        if (r < ROWS - 1 && !cell.walls[2] && !grid[r + 1][c].isCurrentlySearching) {
            temp[count++] = grid[r + 1][c];
        }
        // Left
        if (c > 0 && !cell.walls[3] && !grid[r][c - 1].isCurrentlySearching) {
            temp[count++] = grid[r][c - 1];
        }

        Cell[] result = new Cell[count];
        System.arraycopy(temp, 0, result, 0, count);
        return result;
    }

    /**
     * Carves path between cells by setting common wall boundary to false.
     */
    private void removeWalls(Cell a, Cell b) {
        int rowDiff = a.row - b.row;
        int colDiff = a.col - b.col;

        if (rowDiff == 1) { // b is above a
            a.walls[0] = false;
            b.walls[2] = false;
        } else if (rowDiff == -1) { // b is below a
            a.walls[2] = false;
            b.walls[0] = false;
        } else if (colDiff == 1) { // b is left of a
            a.walls[3] = false;
            b.walls[1] = false;
        } else if (colDiff == -1) { // b is right of a
            a.walls[1] = false;
            b.walls[3] = false;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw Cell Backgrounds
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Cell cell = grid[r][c];
                int x = c * CELL_SIZE;
                int y = r * CELL_SIZE;

                if (cell.isPartOfFinalPath) {
                    g.setColor(new Color(46, 125, 50)); // Emerald green for path
                } else if (cell.isCurrentlySearching) {
                    g.setColor(new Color(0, 150, 136)); // Cyan/teal for wavefront
                } else if (cell.visitedByGenerator) {
                    g.setColor(Color.WHITE); // Maze path
                } else {
                    g.setColor(new Color(43, 43, 43)); // Unvisited grid cell
                }
                g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
            }
        }

        // Draw Cell Walls (drawn on top to prevent overlap gaps)
        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(2)); // Sharper wall lines
        g2d.setColor(new Color(20, 20, 20)); // Dark borders for contrast

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                Cell cell = grid[r][c];
                int x = c * CELL_SIZE;
                int y = r * CELL_SIZE;

                if (cell.walls[0]) g2d.drawLine(x, y, x + CELL_SIZE, y); // Top
                if (cell.walls[1]) g2d.drawLine(x + CELL_SIZE, y, x + CELL_SIZE, y + CELL_SIZE); // Right
                if (cell.walls[2]) g2d.drawLine(x + CELL_SIZE, y + CELL_SIZE, x, y + CELL_SIZE); // Bottom
                if (cell.walls[3]) g2d.drawLine(x, y + CELL_SIZE, x, y); // Left
            }
        }

        // Start/End Marker Graphics
        int margin = 6;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Start Node (Top-Left)
        g2d.setColor(new Color(25, 118, 210)); // Royal Blue Start Dot
        g2d.fillOval(margin, margin, CELL_SIZE - 2 * margin, CELL_SIZE - 2 * margin);

        // End Node (Bottom-Right)
        g2d.setColor(new Color(198, 40, 40)); // Ruby Red End Dot
        int lastX = (COLS - 1) * CELL_SIZE + margin;
        int lastY = (ROWS - 1) * CELL_SIZE + margin;
        g2d.fillOval(lastX, lastY, CELL_SIZE - 2 * margin, CELL_SIZE - 2 * margin);
    }
}
