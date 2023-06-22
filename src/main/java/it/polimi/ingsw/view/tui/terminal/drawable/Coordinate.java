package it.polimi.ingsw.view.tui.terminal.drawable;

/**
 * Represents a cell inside a terminal screen, identified through a line and column number.
 * They are both 1-based.
 * Coordinates are immutable.
 *
 * @author Cristiano Migali
 */
public class Coordinate {
    /**
     * Is the line number of the terminal screen cell.
     */
    private final int line;

    /**
     * Is the column number of the terminal screen cell.
     */
    private final int column;

    /**
     * Constructor of the class.
     * Crafts a coordinate that represents a cell in a terminal screen with the given line and column number.
     *
     * @param line is the 1-based line number of the terminal cell.
     * @param column is the 1-based column number of the terminal cell.
     */
    public Coordinate(int line, int column) {
        if (line <= 0) {
            throw new IllegalArgumentException("The line component of a coordinate for a drawable must be positive");
        }

        if (column <= 0) {
            throw new IllegalArgumentException("The column component of a coordinate for a drawable must be positive");
        }

        this.line = line;
        this.column = column;
    }

    /**
     * @return the line number of the represented terminal screen cell.
     */
    public int getLine() {
        return line;
    }

    /**
     * @return the column number of the represented terminal screen cell.
     */
    public int getColumn() {
        return column;
    }

    /**
     * Allows to identify a Coordinate with an {@link Orientation} (vertical or horizontal) and a parallel and
     * perpendicular component instead of a line and column number.
     * If the orientation is vertical then the parallel component is the line number (conversely it is the column number),
     * while the perpendicular component is the column number (conversely it is the line number).
     *
     * @param orientation is the orientation which indicates the correspondence between line and column number and
     *                    parallel and perpendicular component.
     * @param parallelComponent corresponds to the line number of the Coordinate if the orientation is vertical, the
     *                          column number otherwise.
     * @param perpendicularComponent corresponds to the column number of the Coordinate if the orientation is horizontal,
     *                               the line number otherwise.
     * @return the Coordinate identified by the given orientation, parallel component and perpendicular component as
     * described above.
     */
    public static Coordinate craftCoordinateByOrientation(Orientation orientation, int parallelComponent,
                                                          int perpendicularComponent) {
        return switch (orientation) {
            case HORIZONTAL -> new Coordinate(perpendicularComponent, parallelComponent);
            case VERTICAL -> new Coordinate(parallelComponent, perpendicularComponent);
        };
    }

    /**
     * @param orientation is the orientation which indicates the correspondence between line and column number and
     *                    parallel component.
     * @return the line number of this Coordinate if the orientation is vertical, the column number otherwise.
     */
    public int getParallelComponent(Orientation orientation) {
        return switch (orientation) {
            case HORIZONTAL -> column;
            case VERTICAL -> line;
        };
    }

    /**
     * @param orientation is the orientation which indicates the correspondence between line and column number and
     *                    perpendicular component.
     * @return the column number of this Coordinate if the orientation is vertical, the line number otherwise.
     */
    public int getPerpendicularComponent(Orientation orientation) {
        return switch (orientation) {
            case HORIZONTAL -> line;
            case VERTICAL -> column;
        };
    }

    /**
     * @return the Coordinate with line number 1 and column number 1, which corresponds to the upper left cell in
     * the terminal screen.
     */
    public static Coordinate origin() {
        return new Coordinate(1, 1);
    }

    /**
     * Shifts this Coordinate by the specified amount in the given direction.
     *
     * @param orientation is the direction of the shift.
     * @param amount is the amount of the shift.
     * @return a new Coordinate which has the same column number of the current one, and line number which is obtained
     * by adding amount to the line number of the current Coordinate if the orientation is vertical.
     * Otherwise, if orientation is horizontal, the new Coordinate has the same line number of the current one, and
     * column number which is obtained by adding amount to the column number of the current Coordinate.
     */
    public Coordinate move(Orientation orientation, int amount) {
        return switch (orientation) {
            case HORIZONTAL -> new Coordinate(line, column + amount);
            case VERTICAL -> new Coordinate(line + amount, column);
        };
    }

    /**
     * @param origin is the Coordinate in the absolute reference frame of the origin [(1, 1)] of the reference frame with
     *               respect to which we want to retrieve
     *               the corresponding line and column number of this Coordinate.
     *               That is, if this Coordinate is at (4, 4) and origin is (4, 4), then this Coordinate lies
     *               at the origin of the specified reference frame, hence its components in that frame are (1, 1)
     *               (remember that Coordinate's line and column number are 1-based).
     *               If instead this Coordinate is at (5, 6) and origin is still at (4, 4), then this Coordinate has
     *               components (2, 3) in the specified reference frame.
     * @return a new Coordinate with the components of this Coordinate in the reference frame specified by origin.
     */
    public Coordinate changeOrigin(Coordinate origin) {
        if (line < origin.line || column < origin.column) {
            throw new IllegalArgumentException(this + " isn't in the reference frame defined by the origin " + origin);
        }

        return new Coordinate(line - (origin.line - 1), column - (origin.column - 1));
    }

    @Override
    public String toString() {
        return "(" + line + ", " + column + ")";
    }

    /**
     * @param other is the Coordinate we want to compare this Coordinate with.
     * @return true iff the other Coordinate has line and column number which are both less that those of this
     * Coordinate.
     */
    public boolean before(Coordinate other) {
        return line < other.line || column < other.column;
    }
}
