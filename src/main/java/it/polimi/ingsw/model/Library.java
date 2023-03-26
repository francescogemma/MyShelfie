package it.polimi.ingsw.model;

import java.util.ArrayList;

/**
 * Represents a library in the game. It is composed of a grid of shelves with {@value ROWS} rows and
 * {@value COLUMNS} columns; that is, there are {@value ROWS} rows, each of which has {@value COLUMNS} shelves,
 * disposed along the row.
 * Rows and columns are enumerated starting from 0, in particular, row 0 is the top row in the library and column 0
 * is the left-most column in the library.
 * All the shelves have the same size. Each shelf can contain a {@link Tile tile} (or be empty).
 * If a shelf is empty, all the shelves on the same column, in the rows above, must be empty too. In fact tiles can be
 * inserted in a library only in such a way that, to fill a shelf, you must also fill all the shelves below.
 *
 * @author Cristiano Migali
 */
public class Library {
    /**
     * The number of rows in the library grid.
     */
    public static final int ROWS = 6;

    /**
     * The number of columns in the library grid.
     */
    public static final int COLUMNS = 5;

    /**
     * The maximum number of tiles that can be inserted in the library within a single insertion.
     */
    private static final int MAX_INSERTION_SIZE = 3;

    /**
     * @param row is the index of the row that we want to check.
     * @return true if the argument row corresponds to the index of a row which is inside the library,
     * false otherwise.
     */
    public static boolean isRowInsideTheLibrary(int row) {
        return row >= 0 && row < Library.ROWS;
    }

    /**
     * @param column is the index of the column that we want to check.
     * @return true if the argument column corresponds to the index of a column which is inside the library,
     * false otherwise.
     */
    public static boolean isColumnInsideTheLibrary(int column) {
        return column >= 0 && column < Library.COLUMNS;
    }

    /**
     * The content of the library. In particular inside content[i][j] is stored the tile that is on
     * the shelf at the ith row and jth column, inside the library.
     * (Remember that rows and columns are enumerated starting from 0).
     */
    private final Tile[][] content = new Tile[ROWS][COLUMNS];

    /**
     * Constructor of the class.
     * Creates a new library where every shelf is empty.
     */
    public Library() {
        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNS; column++) {
                content[row][column] = Tile.EMPTY;
            }
        }
    }

    /**
     * @param shelf where we want to know what kind of {@link Tile tile} is in it.
     * @return the {@link Tile tile} inside the specified shelf.
     */
    public Tile get(Shelf shelf) {
        if (shelf == null) {
            throw new NullPointerException("When retrieving a tile from the library, shelf must be non-null");
        }

        return content[shelf.getRow()][shelf.getColumn()];
    }

    /**
     * @param column is the index of the column where we want to count the number of empty shelves.
     * @return the number of empty shelves in the specified column.
     * @throws IllegalArgumentException if column is not an index of a column inside the library.
     */
    private int countEmptyShelves(int column) {
        if (!isColumnInsideTheLibrary(column)) {
            throw new IllegalArgumentException("countEmptyShelves requires a column inside the library");
        }

        /*
         * Because of the invariant property described in the comment above the class definition,
         * if a shelf is not empty, all the shelves below it must be non-empty. Then we can start from the top
         * and count until we reach a shelf which is not empty.
         */
        for (int row = 0; row < Library.ROWS; row++) {
            if (content[row][column] != Tile.EMPTY) {
                return row;
            }
        }

        return Library.ROWS;
    }

    /**
     * Insert {@link Tile tiles} inside the library in such a way to preserve the invariant property of the library:
     * if a shelf is non-empty, all the shelves below it must be non-empty. We put the first tile inside tiles in the
     * empty shelf at the specified column which is in the lowest row, and keep going like this until we have inserted
     * all the tiles in tile. So the tiles at the beginning of the tiles list will be inserted in lower rows than the
     * ones at the end of the tiles list.
     *
     * @param tiles is the list of the tiles that we are going to insert.
     * @param column is the index of the column where we want to do the insertion.
     * @throws IllegalArgumentException if column isn't the index of a column inside the library.
     * @throws IllegalArgumentException if the number of tiles in tiles is bigger than the maximum insertion size:
     * {@value MAX_INSERTION_SIZE}.
     * @throws IllegalArgumentException if there is an empty tile inside tiles.
     * @throws RuntimeException if there isn't enough space inside the specified column to put all the tiles.
     */
    public void insertTiles(ArrayList<Tile> tiles, int column) {
        if (!isColumnInsideTheLibrary(column)) {
            throw new IllegalArgumentException("Given column index outside the library when performing an insertion");
        }

        if (tiles.size() > MAX_INSERTION_SIZE) {
            throw new IllegalArgumentException("It is not allowed to insert more than " + MAX_INSERTION_SIZE
                + " tiles");
        }

        for (Tile tile : tiles) {
            if (tile == Tile.EMPTY) {
                throw new IllegalArgumentException("You can't insert an empty tile inside a library");
            }
        }

        final int numEmptyShelves = countEmptyShelves(column);
        if (numEmptyShelves < tiles.size()) {
            throw new RuntimeException("column " + column + " has not enough free space to insert "
                + tiles.size() + " tiles");
        }

        int row = numEmptyShelves - 1;
        for (Tile tile : tiles) {
            content[row][column] = tile;
            row--;
        }
    }
}
