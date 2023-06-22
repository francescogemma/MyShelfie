package it.polimi.ingsw.view.tui;

import it.polimi.ingsw.model.bookshelf.*;
import it.polimi.ingsw.view.tui.terminal.drawable.Coordinate;
import it.polimi.ingsw.view.tui.terminal.drawable.DrawableSize;
import it.polimi.ingsw.view.tui.terminal.drawable.FixedLayoutDrawable;
import it.polimi.ingsw.view.tui.terminal.drawable.Orientation;
import it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.OrientedLayout;
import it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.OrientedLayoutElement;

/**
 * It is a Drawable which allows to display a game bookshelf.
 * It is realized through a set of 5 {@link BookshelfColumnDrawable} stacked horizontally.
 * It allows to switch the focused column through keyboard arrows.
 * Furthermore the bookshelf can be masked with a provided {@link BookshelfMaskSet}, every {@link BookshelfMask} in the
 * set will be associated with a positive number (starting from 1) which will be displayed on the foreground of the
 * corresponding tiles; tiles which aren't in any {@link BookshelfMask} will be blurred. The precondition is that
 * there is no intersection among any of the {@link BookshelfMask}s in the {@link BookshelfMaskSet}.
 *
 * @author Cristiano Migali
 */
public class BookshelfDrawable extends FixedLayoutDrawable<OrientedLayout> {
    /**
     * It is true iff the BookshelfDrawable is focusable, that is it allows to focus a specific column and
     * trigger its "on press" callback.
     */
    private boolean focusable = false;

    /**
     * The underlying {@link OrientedLayout} used to stack the {@link BookshelfColumnDrawable} together.
     */
    private final OrientedLayout layout;

    /**
     * Constructor of the class.
     * It initializes the underlying {@link OrientedLayout} which allows to stack the {@link BookshelfColumnDrawable}
     * together.
     */
    public BookshelfDrawable() {
        OrientedLayoutElement[] columns = new OrientedLayoutElement[Bookshelf.COLUMNS];

        for (int column = 0; column < Bookshelf.COLUMNS; column++) {
            columns[column] = new BookshelfColumnDrawable(column).weight(1);
        }

        layout = new OrientedLayout(Orientation.HORIZONTAL, columns);
        setLayout(layout);
    }

    @Override
    public void askForSize(DrawableSize desiredSize) {
        int tileSide;

        if (desiredSize.getLines() < (TileDrawable.MEDIUM_SIDE - 1) * Bookshelf.ROWS + 1 ||
            desiredSize.getColumns()  < TileDrawable.MEDIUM_SIDE * Bookshelf.COLUMNS) {
            tileSide = TileDrawable.SMALL_SIDE;
        } else if (desiredSize.getLines() < (TileDrawable.LARGE_SIDE - 1) * Bookshelf.ROWS + 1 ||
            desiredSize.getColumns() < TileDrawable.LARGE_SIDE * Bookshelf.COLUMNS) {
            tileSide = TileDrawable.MEDIUM_SIDE;
        } else {
            tileSide = TileDrawable.LARGE_SIDE;
        }

        size = new DrawableSize((tileSide - 1) * Bookshelf.ROWS + 1, tileSide * Bookshelf.COLUMNS);

        layout.askForSize(size);
    }

    @Override
    public boolean focus(Coordinate desiredCoordinate) {
        if (!focusable) {
            return false;
        }

        return super.focus(desiredCoordinate);
    }

    /**
     * Allows to set if this BookshelfDrawable is focusable or not.
     * If the BookshelfDrawable is focusable it allows to focus single columns and trigger their "on press" callback.
     *
     * @param focusable should be true iff you want to make this BookshelfDrawable focusable.
     * @return this BookshelfDrawable after it has set focusable or not focusable.
     */
    public BookshelfDrawable focusable(boolean focusable) {
        this.focusable = focusable;

        if (!focusable) {
            layout.unfocus();
        }

        return this;
    }

    /**
     * @param column is the column index of the desired {@link BookshelfColumnDrawable}.
     * @return the {@link BookshelfColumnDrawable} in this BookshelfDrawable at the specified column index.
     */
    public BookshelfColumnDrawable getColumn(int column) {
        if (!Bookshelf.isColumnInsideTheBookshelf(column)) {
            throw new IllegalArgumentException("Trying to get column: " + column +
                " for bookshelf drawable");
        }

        return ((BookshelfColumnDrawable) layout.getElements().get(column)
                .getDrawable());
    }

    /**
     * Populates this BookshelfDrawable according to the data in the provided {@link BookshelfView}.
     *
     * @param bookshelf is the {@link BookshelfView} which describes how to populate this BookshelfDrawable.
     * @return this BookshelfDrawable after it has been populated according to the data in the provided
     * {@link BookshelfView}.
     */
    public BookshelfDrawable populate(BookshelfView bookshelf) {
        for (int column = 0; column < BookshelfView.COLUMNS; column++) {
            BookshelfColumnDrawable columnDrawable = getColumn(column);

            for (int row = 0; row < BookshelfView.ROWS; row++) {
                columnDrawable.color(row, bookshelf.getTileColorAt(Shelf.getInstance(row, column)));
                columnDrawable.mask(row, 0);
                columnDrawable.masked(false);
            }
        }

        return this;
    }

    /**
     * Masks this BookshelfDrawable according to the data in the provided {@link BookshelfMaskSet}.
     * A precondition of this method is that there aren't two {@link BookshelfMask} with non-null intersection.
     * Every {@link BookshelfMask} in the {@link BookshelfMaskSet} will be associated with a positive integer
     * (starting from 1), this integer will be displayed on the foreground of all the tiles in this BookshelfDrawable
     * which are in the {@link BookshelfMask}.
     * If a tile isn't in any {@link BookshelfMask} it will be blurred.
     * This method is useful to display goal completion.
     *
     * @param bookshelfMaskSet is the {@link BookshelfMaskSet} which describes how to mask this BookshelfDrawable.
     * @return this BookshelfDrawable after it has been masked according to the data in the provided {@link BookshelfMaskSet}.
     */
    public BookshelfDrawable mask(BookshelfMaskSet bookshelfMaskSet) {
        if (bookshelfMaskSet.getBookshelfMasks().isEmpty()) {
            throw new IllegalArgumentException("You can't mask a bookshelf with an empty bookshelf mask set");
        }

        BookshelfMask firstMask = bookshelfMaskSet.getBookshelfMasks().get(0);
        for (int column = 0; column < Bookshelf.COLUMNS; column++) {
            BookshelfColumnDrawable columnDrawable = getColumn(column);

            for (int row = 0; row < Bookshelf.ROWS; row++) {
                columnDrawable.color(row, firstMask.getTileColorAt(Shelf.getInstance(row, column)));
                columnDrawable.masked(true);
            }
        }

        int count = 1;
        for (BookshelfMask mask : bookshelfMaskSet.getBookshelfMasks()) {
            for (Shelf shelf : mask.getShelves()) {
                getColumn(shelf.getColumn()).mask(shelf.getRow(), count);
            }
            count++;
        }

        focusable = false;

        return this;
    }
}
