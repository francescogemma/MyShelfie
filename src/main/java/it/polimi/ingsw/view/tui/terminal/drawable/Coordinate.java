package it.polimi.ingsw.view.tui.terminal.drawable;

public class Coordinate {
    private final int line;
    private final int column;

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

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public static Coordinate craftCoordinateByOrientation(Orientation orientation, int parallelComponent,
                                                          int perpendicularComponent) {
        return switch (orientation) {
            case HORIZONTAL -> new Coordinate(perpendicularComponent, parallelComponent);
            case VERTICAL -> new Coordinate(parallelComponent, perpendicularComponent);
        };
    }

    public int getParallelComponent(Orientation orientation) {
        return switch (orientation) {
            case HORIZONTAL -> column;
            case VERTICAL -> line;
        };
    }

    public int getPerpendicularComponent(Orientation orientation) {
        return switch (orientation) {
            case HORIZONTAL -> line;
            case VERTICAL -> column;
        };
    }

    public static Coordinate origin() {
        return new Coordinate(1, 1);
    }

    public Coordinate move(Orientation orientation, int amount) {
        return switch (orientation) {
            case HORIZONTAL -> new Coordinate(line, column + amount);
            case VERTICAL -> new Coordinate(line + amount, column);
        };
    }

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

    public boolean before(Coordinate other) {
        return line < other.line || column < other.column;
    }
}
