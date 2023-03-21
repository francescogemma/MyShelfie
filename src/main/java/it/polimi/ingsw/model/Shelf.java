package it.polimi.ingsw.model;

public class Shelf {
    private final int row;
    private final int column;

    private static final Shelf[][] instances = new Shelf[Library.ROWS][Library.COLUMNS];

    private Shelf(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public static Shelf getInstance(int row, int column) {
        if (!Library.isRowInsideTheLibrary(row) || !Library.isColumnInsideTheLibrary(column)) {
            throw new IllegalArgumentException("shelf at (" + row + ", " + column + ") is not inside the library");
        }

        if (instances[row][column] == null) {
            instances[row][column] = new Shelf(row, column);
        }

        return instances[row][column];
    }

    public static Shelf origin() {
        return getInstance(0, 0);
    }

    public Shelf move(Offset offset) {
        if (!Library.isRowInsideTheLibrary(row + offset.getRowOffset()) ||
            !Library.isColumnInsideTheLibrary(column + offset.getColumnOffset())) {
            throw new RuntimeException("Trying to move " + this + " of " + offset);
        }

        return getInstance(row + offset.getRowOffset(), column + offset.getColumnOffset());
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    public boolean before(Shelf other) {
        if (other == null) {
            throw new NullPointerException();
        }

        if (row != other.row) {
            return row < other.row;
        }

        return column < other.column;
    }

    @Override
    public String toString() {
        return "shelf at (" + row + ", " + column + ")";
    }
}
