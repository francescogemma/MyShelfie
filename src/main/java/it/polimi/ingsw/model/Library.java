package it.polimi.ingsw.model;

import java.util.ArrayList;

public class Library {
    public static final int ROWS = 6;
    public static final int COLUMNS = 5;

    private static final int MAX_INSERTION_SIZE = 3;

    public static boolean isRowInsideTheLibrary(int row) {
        return row >= 0 && row < Library.ROWS;
    }

    public static boolean isColumnInsideTheLibrary(int column) {
        return column >= 0 && column < Library.COLUMNS;
    }

    private final Tile[][] content = new Tile[ROWS][COLUMNS];

    public Library() {
        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNS; column++) {
                content[row][column] = Tile.EMPTY;
            }
        }
    }

    public Tile get(Shelf shelf) {
        if (shelf == null) {
            throw new NullPointerException("When retrieving a tile from the library, shelf must be non-null");
        }

        return content[shelf.getRow()][shelf.getColumn()];
    }

    private int countEmptyShelves(int column) {
        if (!isColumnInsideTheLibrary(column)) {
            throw new IllegalArgumentException("countEmptyShelves requires a column inside the library");
        }

        for (int row = 0; row < Library.ROWS; row++) {
            if (content[row][column] != Tile.EMPTY) {
                return row;
            }
        }

        return Library.ROWS;
    }

    public void insertTiles(ArrayList<Tile> tiles, int column) {
        if (tiles.size() > MAX_INSERTION_SIZE) {
            throw new IllegalArgumentException("It is not allowed to insert more than " + MAX_INSERTION_SIZE
                + " tiles");
        }

        final int numEmptyShelves = countEmptyShelves(column);
        if (numEmptyShelves < tiles.size()) {
            throw new RuntimeException("column " + column + " has not enough free space to insert "
                + tiles.size() + " tiles");
        }

        // TODO: Check that there are no empty tiles that are going to be inserted

        int row = numEmptyShelves - 1;
        for (Tile tile : tiles) {
            content[row][column] = tile;
            row--;
        }
    }
}
