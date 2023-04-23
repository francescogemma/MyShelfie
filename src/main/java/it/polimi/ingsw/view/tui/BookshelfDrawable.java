package it.polimi.ingsw.view.tui;

import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.view.tui.terminal.drawable.Coordinate;
import it.polimi.ingsw.view.tui.terminal.drawable.DrawableSize;
import it.polimi.ingsw.view.tui.terminal.drawable.FixedLayoutDrawable;
import it.polimi.ingsw.view.tui.terminal.drawable.Orientation;
import it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.OrientedLayout;
import it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.OrientedLayoutElement;

public class BookshelfDrawable extends FixedLayoutDrawable<OrientedLayout> {
    private boolean focusable = true;

    private final OrientedLayout layout;

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

    public BookshelfDrawable focusable(boolean focusable) {
        this.focusable = focusable;

        if (!focusable) {
            layout.unfocus();
        }

        return this;
    }
}
