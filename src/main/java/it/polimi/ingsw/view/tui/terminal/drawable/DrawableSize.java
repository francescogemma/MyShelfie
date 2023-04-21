package it.polimi.ingsw.view.tui.terminal.drawable;

public class DrawableSize {
    private final int lines;
    private final int columns;

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

    public static DrawableSize craftSizeByOrientation(Orientation orientation, int parallelSizeComponent,
                                                      int perpendicularSizeComponent) {
        return switch (orientation) {
            case HORIZONTAL -> new DrawableSize(perpendicularSizeComponent, parallelSizeComponent);
            case VERTICAL -> new DrawableSize(parallelSizeComponent, perpendicularSizeComponent);
        };
    }

    public int getLines() {
        return lines;
    }

    public int getColumns() {
        return columns;
    }

    public int getParallelSizeComponent(Orientation orientation) {
        return switch (orientation) {
            case HORIZONTAL -> columns;
            case VERTICAL -> lines;
        };
    }

    public int getPerpendicularSizeComponent(Orientation orientation) {
        return switch (orientation) {
            case HORIZONTAL -> lines;
            case VERTICAL -> columns;
        };
    }

    public boolean isInside(Coordinate coordinate) {
        return coordinate.getLine() <= lines && coordinate.getColumn() <= columns;
    }
}
