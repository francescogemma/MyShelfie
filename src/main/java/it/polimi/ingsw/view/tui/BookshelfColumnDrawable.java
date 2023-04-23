package it.polimi.ingsw.view.tui;

import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.tile.TileColor;
import it.polimi.ingsw.view.tui.terminal.drawable.Coordinate;
import it.polimi.ingsw.view.tui.terminal.drawable.Drawable;
import it.polimi.ingsw.view.tui.terminal.drawable.DrawableSize;
import it.polimi.ingsw.view.tui.terminal.drawable.WithBorderBoxDrawable;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Color;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.PrimitiveSymbol;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Symbol;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class BookshelfColumnDrawable extends Drawable {
    private final int column;

    private final List<TileColor> tileColors;
    private boolean onFocus = false;

    private int tileSide;

    public BookshelfColumnDrawable(int column) {
        this.column = column;

        tileColors = new ArrayList<>(Bookshelf.ROWS);
        for (int i = 0; i < Bookshelf.ROWS; i++) {
            tileColors.add(TileColor.EMPTY);
        }
    }

    @Override
    public void askForSize(DrawableSize desiredSize) {
        if (desiredSize.getLines() < (TileDrawable.MEDIUM_SIDE - 1) * Bookshelf.ROWS + 1 ||
            desiredSize.getColumns() < TileDrawable.MEDIUM_SIDE) {
            tileSide = TileDrawable.SMALL_SIDE;
        } else if (desiredSize.getLines() < (TileDrawable.LARGE_SIDE - 1) * Bookshelf.ROWS + 1 ||
            desiredSize.getColumns() < TileDrawable.LARGE_SIDE) {
            tileSide = TileDrawable.MEDIUM_SIDE;
        } else {
            tileSide = TileDrawable.LARGE_SIDE;
        }

        size = new DrawableSize((tileSide - 1) * Bookshelf.ROWS + 1, tileSide);
    }

    @Override
    public Symbol getSymbolAt(Coordinate coordinate) {
        if ((coordinate.getColumn() == 1 || coordinate.getColumn() == size.getColumns()) &&
            (coordinate.getLine() - 1) % (tileSide - 1) == 0) {
            int row = (coordinate.getLine() - 1) / (tileSide - 1);
            if (1 <= row && row < Bookshelf.ROWS) {
                return (coordinate.getColumn() == 1 ? PrimitiveSymbol.T_RIGHT
                        : PrimitiveSymbol.T_LEFT).highlight(Color.FOCUS, onFocus);
            }
        }

        Optional<PrimitiveSymbol> primitiveSymbol = WithBorderBoxDrawable.addBorder(coordinate, size);
        if (primitiveSymbol.isPresent()) {
            return primitiveSymbol.get().getPrimitiveSymbol().highlight(Color.FOCUS, onFocus);
        }

        for (int i = 0; i < Bookshelf.ROWS; i++) {
            Coordinate boxOrigin = new Coordinate(1 + i * (tileSide - 1), 1);
            if (boxOrigin.getLine() <= coordinate.getLine() && boxOrigin.getColumn() <= coordinate.getColumn()) {
                primitiveSymbol = WithBorderBoxDrawable.addBorder(coordinate.changeOrigin(boxOrigin),
                    new DrawableSize(tileSide, tileSide));
                if (primitiveSymbol.isPresent()) {
                    return primitiveSymbol.get().getPrimitiveSymbol().highlight(Color.FOCUS, onFocus);
                }
            }
        }

        TileColor tileColor = tileColors.get(coordinate.getLine() / tileSide);
        return (tileColor == TileColor.EMPTY) ? PrimitiveSymbol.EMPTY : PrimitiveSymbol.EMPTY
            .colorBackground(TileDrawable.tileColorToColor(tileColor));

    }

    @Override
    public boolean handleInput(String key) {
        if (key.equals("\r") && onselect != null) {
            onselect.accept(column);

            return true;
        }

        return false;
    }

    @Override
    public boolean focus(Coordinate desiredCoordinate) {
        onFocus = true;

        return true;
    }

    @Override
    public void unfocus() {
        onFocus = false;
    }

    @Override
    public Optional<Coordinate> getFocusedCoordinate() {
        return Optional.of(getCenter());
    }

    private Consumer<Integer> onselect;

    public BookshelfColumnDrawable onselect(Consumer<Integer> onselect) {
        this.onselect = onselect;

        return this;
    }
}
