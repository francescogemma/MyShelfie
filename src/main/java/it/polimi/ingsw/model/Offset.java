package it.polimi.ingsw.model;

public class Offset {
    private final int rowOffset;
    private final int columnOffset;

    private static final Offset[][] instances = new Offset[2*Library.ROWS - 1][2*Library.COLUMNS - 1];

    private Offset(int rowOffset, int columnOffset) {
        this.rowOffset = rowOffset;
        this.columnOffset = columnOffset;
    }

    public static Offset getInstance(int rowOffset, int columnOffset) {
        if (rowOffset <= - Library.ROWS || rowOffset >= Library.ROWS) {
            throw new IllegalArgumentException("row offset must be between " + (-Library.ROWS + 1) + " and "
            + (Library.ROWS - 1));
        }

        if (columnOffset <= -Library.COLUMNS || columnOffset >= Library.COLUMNS) {
            throw new IllegalArgumentException("column offset must be between " + (-Library.COLUMNS + 1) + " and "
            + (Library.COLUMNS - 1));
        }

        if (instances[Library.ROWS - 1 + rowOffset][Library.COLUMNS - 1 + columnOffset] == null) {
            instances[Library.ROWS - 1 + rowOffset][Library.COLUMNS - 1 + columnOffset] =
                new Offset(rowOffset, columnOffset);
        }

        return instances[Library.ROWS - 1 + rowOffset][Library.COLUMNS - 1 + columnOffset];
    }

    public static Offset up() {
        return up(1);
    }

    public static Offset up(int amount) {
        return getInstance(-amount, 0);
    }

    public static Offset down() {
        return down(1);
    }

    public static Offset down(int amount) {
        return getInstance(amount, 0);
    }

    public static Offset left() {
        return left(1);
    }

    public static Offset left(int amount) {
        return getInstance(0, -amount);
    }

    public static Offset right() {
        return right(1);
    }

    public static Offset right(int amount) {
        return getInstance(0, amount);
    }

    public int getRowOffset() {
        return rowOffset;
    }

    public int getColumnOffset() {
        return columnOffset;
    }

    public Offset add(Offset other) {
        return getInstance(rowOffset + other.rowOffset, columnOffset + other.columnOffset);
    }

    @Override
    public String toString() {
        return rowOffset + " V, " + columnOffset + " >";
    }
}
