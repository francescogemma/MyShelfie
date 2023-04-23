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
    private final List<Integer> maskNumbers;
    private boolean masked = false;

    private boolean onFocus = false;

    private int tileSide;

    public BookshelfColumnDrawable(int column) {
        this.column = column;

        tileColors = new ArrayList<>(Bookshelf.ROWS);
        for (int i = 0; i < Bookshelf.ROWS; i++) {
            tileColors.add(TileColor.EMPTY);
        }

        maskNumbers = new ArrayList<>(Bookshelf.ROWS);
        for (int i = 0; i < Bookshelf.ROWS; i++) {
            maskNumbers.add(0);
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
                if (masked) {
                    return (coordinate.getColumn() == 1 ? PrimitiveSymbol.T_RIGHT
                        : PrimitiveSymbol.T_LEFT).blur();
                }

                return (coordinate.getColumn() == 1 ? PrimitiveSymbol.T_RIGHT
                        : PrimitiveSymbol.T_LEFT).highlight(Color.FOCUS, onFocus);
            }
        }

        Optional<PrimitiveSymbol> primitiveSymbol = WithBorderBoxDrawable.addBorder(coordinate, size);
        if (primitiveSymbol.isPresent()) {
            if (masked) {
                return primitiveSymbol.get().blur();
            }

            return primitiveSymbol.get().highlight(Color.FOCUS, onFocus);
        }

        for (int i = 0; i < Bookshelf.ROWS; i++) {
            Coordinate boxOrigin = new Coordinate(1 + i * (tileSide - 1), 1);
            if (boxOrigin.getLine() <= coordinate.getLine() && boxOrigin.getColumn() <= coordinate.getColumn()) {
                primitiveSymbol = WithBorderBoxDrawable.addBorder(coordinate.changeOrigin(boxOrigin),
                    new DrawableSize(tileSide, tileSide));
                if (primitiveSymbol.isPresent()) {
                    if (masked) {
                        return primitiveSymbol.get().blur();
                    }

                    return primitiveSymbol.get().highlight(Color.FOCUS, onFocus);
                }
            }
        }

        int row = (coordinate.getLine() - 1) / (tileSide - 1);
        TileColor tileColor = tileColors.get(row);

        if (tileColor == TileColor.EMPTY) {
            return PrimitiveSymbol.EMPTY;
        }

        if (masked && maskNumbers.get(row) == 0) {
            return PrimitiveSymbol.EMPTY.colorBackground(Color.GREY);
        }

        if (masked) {
            return  PrimitiveSymbol.fromString(String.valueOf(maskNumbers.get(row)))
                .colorForeground(Color.BLACK)
                .colorBackground(TileDrawable.tileColorToColor(tileColor));
        }

        return PrimitiveSymbol.EMPTY.colorBackground(TileDrawable.tileColorToColor(tileColor));

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

    public BookshelfColumnDrawable color(int row, TileColor tileColor) {
        if (!Bookshelf.isRowInsideTheBookshelf(row)) {
            throw new IllegalArgumentException("A bookshelf row must be between 0 and " + (Bookshelf.ROWS - 1) +
                ", got: " + row + " when coloring bookshelf column");
        }

        tileColors.set(row, tileColor);

        return this;
    }

    public BookshelfColumnDrawable mask(int row, int maskNumber) {
        if (!Bookshelf.isRowInsideTheBookshelf(row)) {
            throw new IllegalArgumentException("A bookshelf row must be between 0 and " + (Bookshelf.ROWS - 1) +
                ", got: " + row + " when masking bookshelf column");
        }

        maskNumbers.set(row, maskNumber);

        return this;
    }

    public BookshelfColumnDrawable masked(boolean masked) {
        this.masked = masked;

        return this;
    }
}
