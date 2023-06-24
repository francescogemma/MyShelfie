package it.polimi.ingsw.model.bookshelf;

import it.polimi.ingsw.model.tile.Tile;
import it.polimi.ingsw.model.tile.TileColor;
import it.polimi.ingsw.model.tile.TileVersion;

/**
 * It is an immutable view on a {@link Bookshelf} which exposes only its getter methods.
 *
 * @author Giacomo Groppi
 */
public class BookshelfView {
    /**
     * The number of rows in the bookshelf grid.
     */
    public static final int ROWS = 6;

    /**
     * The number of columns in the bookshelf grid.
     */
    public static final int COLUMNS = 5;

    /**
     * The maximum number of tiles that can be inserted in the bookshelf within a single insertion.
     */
    protected static final int MAX_INSERTION_SIZE = 3;

    /**
     * The content of the bookshelf. In particular inside content[i][j] is stored the tile that is on
     * the shelf at the ith row and jth column, inside the bookshelf.
     * (Remember that rows and columns are enumerated starting from 0).
     * The field is protected to allow direct access from a mock subclass, used for testing purpose.
     */
    protected final Tile[][] content = new Tile[ROWS][COLUMNS];

    /**
     * Constructor of the class.
     * It initializes a BookshelfView which is completely empty.
     */
    public BookshelfView() {
        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNS; column++) {
                content[row][column] = Tile.getInstance(TileColor.EMPTY, TileVersion.FIRST);
            }
        }
    }

    /**
     * Copy constructor of the class.
     *
     * @param other is the other BookshelfView that has to be copied.
     */
    public BookshelfView(BookshelfView other) {
        for (int r = 0; r < content.length; r ++) {
            for (int c = 0; c < content[r].length; c ++) {
                content[r][c] = other.content[r][c];
            }
        }
    }

    /**
     * @return true iff it is not possible to insert new Tiles in the bookshelf.
     * @author Giacomo Groppi
     * */
    public boolean isFull () {
        for (int i = 0; i < Bookshelf.COLUMNS; i++) {
            TileColor tile = getTileColorAt(Shelf.getInstance(0, i));
            if (tile.equals(TileColor.EMPTY)){
                return false;
            }
        }

        return true;
    }

    /**
     * @param shelf where we want to know what kind of {@link TileColor color} is the {@link Tile tile} in it.
     * @return the {@link TileColor color of the tile} inside the specified shelf.
     * @throws NullPointerException if shelf is null.
     */
    public TileColor getTileColorAt(Shelf shelf) {
        return content[shelf.getRow()][shelf.getColumn()].getColor();
    }

    /**
     * @param shelf where we want to know which is the {@link Tile tile} in it.
     * @return the {@link Tile tile} inside the specified shelf.
     */
    public Tile getTileAt(Shelf shelf) {
        return content[shelf.getRow()][shelf.getColumn()];
    }

    /**
     * @return a copy of this {@link BookshelfView}.
     */
    public BookshelfView createView () {
        return new BookshelfView(this);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this)
            return true;
        if (other == null || other.getClass() != this.getClass())
            return false;

        BookshelfView b = (BookshelfView) other;

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
}
