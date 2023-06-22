package it.polimi.ingsw.view.tui.terminal.drawable;

/**
 * Represents the space occupied by a {@link Drawable} on a terminal screen, expressed as a number of lines and
 * columns.
 * DrawableSize is immutable.
 *
 * @author Cristiano Migali
 */
public class DrawableSize {
    /**
     * The number of lines occupied by the {@link Drawable} on screen.
     */
    private final int lines;

    /**
     * The number of columns occupied by the {@link Drawable} on screen.
     */
    private final int columns;

    /**
     * Constructor of the class.
     * Sets the number of lines and columns occupied by the Drawable on the terminal screen.
     *
     * @param lines is the number of lines occupied by the Drawable.
     * @param columns is the number of columns occupied by the Drawable.
     *
     * @throws IllegalArgumentException if lines or columns are negative.
     */
    public DrawableSize(int lines, int columns) {
        if (lines < 0) {
            throw new IllegalArgumentException("Lines in drawable size must be non-negative");
        }

        if (columns < 0) {
            throw new IllegalArgumentException("Columns in drawable size must be non-negative");
        }

        this.lines = lines;
        this.columns = columns;
    }

    /**
     * Allows to identify a DrawableSize by specifying an {@link Orientation} (vertical or horizontal) and
     * a parallel and perpendicular component instead of lines and columns numbers.
     * In particular, if the orientation is vertical, the parallel component corresponds to the number of lines
     * of the DrawableSize while the perpendicular component corresponds to the number of columns;
     * otherwise, if the orientation is horizontal, the parallel component corresponds to the number of columns
     * of the DrawableSize while the perpendicular component corresponds to the number of lines.
     *
     * @param orientation is the {@link Orientation} (vertical or horizontal) which specifies the correspondence
     *                    between lines and columns numbers and parallel and perpendicular component.
     * @param parallelSizeComponent corresponds to the number of lines of the DrawableSize if the orientation is
     *                              vertical, to the number of columns otherwise.
     * @param perpendicularSizeComponent corresponds to the number of columns of the DrawableSize if the orientation
     *                                   is vertical, to the number of lines otherwise.
     * @return a DrawableSize with lines and columns numbers set according to orientation, parallel and perpendicular
     * component as described above.
     */
    public static DrawableSize craftSizeByOrientation(Orientation orientation, int parallelSizeComponent,
                                                      int perpendicularSizeComponent) {
        return switch (orientation) {
            case HORIZONTAL -> new DrawableSize(perpendicularSizeComponent, parallelSizeComponent);
            case VERTICAL -> new DrawableSize(parallelSizeComponent, perpendicularSizeComponent);
        };
    }

    /**
     * @return the number of lines occupied by the Drawable on the terminal screen.
     */
    public int getLines() {
        return lines;
    }

    /**
     * @return the number of columns occupied by the Drawable on the terminal screen.
     */
    public int getColumns() {
        return columns;
    }

    /**
     * @param orientation is the {@link Orientation} (vertical or horizontal) which specifies the correspondence
     *                    between lines and columns numbers and parallel component.
     * @return the number of lines of the DrawableSize if the orientation is vertical, the number of
     * columns otherwise.
     */
    public int getParallelSizeComponent(Orientation orientation) {
        return switch (orientation) {
            case HORIZONTAL -> columns;
            case VERTICAL -> lines;
        };
    }

    /**
     * @param orientation is the {@link Orientation} (vertical or horizontal) which specifies the correspondence
     *                    between lines and columns numbers and perpendicular component.
     * @return the number of columns of the DrawableSize if the orientation is vertical, the number of
     * lines otherwise.
     */
    public int getPerpendicularSizeComponent(Orientation orientation) {
        return switch (orientation) {
            case HORIZONTAL -> lines;
            case VERTICAL -> columns;
        };
    }

    /**
     * @param coordinate is the {@link Coordinate} for which we want to check if it is inside the Drawable with this
     *                   DrawableSize or not.
     * @return true iff coordinate is inside a rectangle with upper left corner at (1, 1) and dimensions corresponding
     * to the number of lines and columns of this DrawableSize.
     */
    public boolean isInside(Coordinate coordinate) {
        return coordinate.getLine() <= lines && coordinate.getColumn() <= columns;
    }
}
