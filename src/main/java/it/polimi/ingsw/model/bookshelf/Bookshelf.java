package it.polimi.ingsw.model.bookshelf;

import it.polimi.ingsw.model.tile.Tile;
import it.polimi.ingsw.model.tile.TileColor;
import it.polimi.ingsw.model.tile.TileVersion;

import java.util.List;

/**
 * Represents a bookshelf in the game. It is composed of a grid of shelves with {@value ROWS} rows and
 * {@value COLUMNS} columns; that is, there are {@value ROWS} rows, each of which has {@value COLUMNS} shelves,
 * disposed along the row.
 * Rows and columns are enumerated starting from 0, in particular, row 0 is the top row in the bookshelf and column 0
 * is the left-most column in the bookshelf.
 * All the shelves have the same size. Each shelf can contain a {@link Tile tile} (or be empty).
 * If a shelf is empty, all the shelves on the same column, in the rows above, must be empty too. In fact tiles can be
 * inserted in a bookshelf only in such a way that, to fill a shelf, you must also fill all the shelves below.
 *
 * @author Cristiano Migali
 */
public class Bookshelf extends BookshelfView {
    /**
     * @param row is the index of the row that we want to check.
     * @return true if the argument row corresponds to the index of a row which is inside the bookshelf,
     * false otherwise.
     */
    public static boolean isRowInsideTheBookshelf(int row) {
        return row >= 0 && row < Bookshelf.ROWS;
    }

    /**
     * @param column is the index of the column that we want to check.
     * @return true if the argument column corresponds to the index of a column which is inside the bookshelf,
     * false otherwise.
     */
    public static boolean isColumnInsideTheBookshelf(int column) {
        return column >= 0 && column < Bookshelf.COLUMNS;
    }

    /**
     * Constructor of the class.
     * Creates a new bookshelf where every shelf is empty.
     */
    public Bookshelf() {

    }

    public Bookshelf(Bookshelf other) {
        super(other);
    }

    /**
     * @param column is the index of the column where we want to count the number of empty shelves.
     * @return the number of empty shelves in the specified column.
     * @throws IllegalArgumentException if column is not an index of a column inside the bookshelf.
     */
    private int countEmptyShelves(int column) {
        if (!isColumnInsideTheBookshelf(column)) {
            throw new IllegalArgumentException("countEmptyShelves requires a column inside the bookshelf");
        }

        /*
         * Because of the invariant property described in the comment above the class definition,
         * if a shelf is not empty, all the shelves below it must be non-empty. Then we can start from the top
         * and count until we reach a shelf which is not empty.
         */
        for (int row = 0; row < Bookshelf.ROWS; row++) {
            if (content[row][column].getColor() != TileColor.EMPTY) {
                return row;
            }
        }

        return Bookshelf.ROWS;
    }

    /**
     * Insert {@link Tile tiles} inside the bookshelf in such a way to preserve the invariant property of the bookshelf:
     * if a shelf is non-empty, all the shelves below it must be non-empty. We put the first tile inside tiles in the
     * empty shelf at the specified column which is in the lowest row, and keep going like this until we have inserted
     * all the tiles in tile. So the tiles at the beginning of the tiles list will be inserted in lower rows than the
     * ones at the end of the tiles list.
     *
     * @param tiles is the list of the tiles that we are going to insert.
     * @param column is the index of the column where we want to do the insertion.
     * @throws IllegalArgumentException
     * <ul>
     *     <li> if column isn't the index of a column inside the bookshelf. </li>
     *     <li> if tiles is empty </li>
     *     <li> if the number of tiles in tiles is bigger than the maximum insertion size: {@value MAX_INSERTION_SIZE}. </li>
     *     <li> if there is an empty tile inside tiles. </li>
     * </ul>
     * @throws NotEnoughSpaceInColumnException if there isn't enough space inside the specified column to put all the tiles.
     */
    public void insertTiles(List<Tile> tiles, int column) {
        if (!isColumnInsideTheBookshelf(column)) {
            throw new IllegalArgumentException("Given column index outside the bookshelf when performing an insertion");
        }

        if (tiles.isEmpty()) {
            throw new IllegalArgumentException("You can't perform an empty insertion");
        }

        if (tiles.size() > MAX_INSERTION_SIZE) {
            throw new IllegalArgumentException("It is not allowed to insert more than " + MAX_INSERTION_SIZE
                + " tiles");
        }

        for (Tile tile : tiles) {
            if (tile.getColor() == TileColor.EMPTY) {
                throw new IllegalArgumentException("You can't insert an empty tile inside a bookshelf");
            }
        }

        final int numEmptyShelves = countEmptyShelves(column);
        if (numEmptyShelves < tiles.size()) {
            throw new NotEnoughSpaceInColumnException(column, tiles.size());
        }

        int row = numEmptyShelves - 1;
        for (Tile tile : tiles) {
            content[row][column] = tile;
            row--;
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == this)
            return true;
        if (other == null || other.getClass() != this.getClass())
            return false;

        Bookshelf b = (Bookshelf) other;

        for (int i = 0; i < this.content.length; i++) {
            for (int j = 0; j < this.content[i].length; j++) {
                final Tile t1 = b.content[i][j];
                final Tile t2 =   content[i][j];
                if (!t1.equals(t2)) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Tile[] tiles : content) {
            for (Tile tile : tiles) {
                stringBuilder.append(tile);
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}
