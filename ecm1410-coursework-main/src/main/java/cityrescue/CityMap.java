package cityrescue;


/**
 * CityMap represents the 2D grid city with obstacles.
 *
 * <p>The map is defined by a fixed width and height. Each cell in the grid
 * may either be free or blocked (due to an obstacle). Coordinates are zero-based,
 * where (0,0) represents the top left of the grid.
 * </p>
 *
 * <p>Out-of-bounds coordinates are treated as blocked to simplify the movement logic.
 * </p>
 */
public class CityMap {

    private final int width;
    private final int height;
    private final boolean[][] blocked;

    /**
     * <p>onstructs a new CityMap with the given dimensions.</p>
     * <p>All cells are initially unblocked.</p>
     *
     * @param width the width of the map (number of x-coordinates)
     * @param height the height of the map (number of y-coordinates)
     */
    public CityMap(int width, int height) {
        this.width = width;
        this.height = height;
        blocked = new boolean[width][height];
    }

    // ------------------ GRID INFO ------------------

    /**
     * Returns the dimensions of the map
     *
     * @return an array of the length 2 where:
     *      index 0 = width,
     *      index 1 = height
     */
    public int[] getSize() {
        return new int[]{width, height};
    }

    /**
     * Determines whether a coordinate lies within the map boundaries.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return true if the coordinate is within bounds, false otherwise
     */
    public boolean isInBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    /**
     * Checks whether a given coordinate is blocked.
     *
     * <p>If the coordinate is outside the map boundaries,
     * it is treated as blocked.
     * </p>
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return true if the cell is blocked or out-of-bounds,
     *         false if the cell is free
     */
    public boolean isBlocked(int x, int y) {
        if (!isInBounds(x, y)) return true; // treat out-of-bounds as blocked
        return blocked[x][y];
    }

    /**
     * Sets whether a specific cell is blocked.
     * <p>If the coordinate is out-of-bounds, no action is taken.</p>
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param value true to mark the cell as blocked,
     *              false to mark it as free
     */
    public void setBlocked(int x, int y, boolean value) {
        if (!isInBounds(x, y)) return; // extra safety
        blocked[x][y] = value;
    }

    // ------------------ OBSTACLE COUNT ------------------

    /**
     * Counts the total number of blocked cells in the map.
     *
     * @return the number of obstacle cells
     */
    public int countObstacles() {
        int count = 0;
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                if (blocked[i][j]) count++;
        return count;
    }
}