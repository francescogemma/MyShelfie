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

/**
 * It is a Drawable which allows to display a single column of the game bookshelf.
 * The column is realized through a set of square tiles of solid color, stacked vertically and separated through a
 * border box.
 * Columns are focusable: the border box turns red when they are on focus; they allow to register a callback which
 * is triggered when the user presses enter while focusing a column.
 * Furthermore the column can be masked, allowing to highlight only certain tiles and add to them a foreground number,
 * while other tiles are blurred. This is useful when we want to display common goals.
 *
 * @author Cristiano Migali
 */
public class BookshelfColumnDrawable extends Drawable {
    /**
     * It is the index of the column in the bookshelf.
     */
    private final int column;

    /**
     * It is the list of {@link TileColor}s of the tiles displayed in this column. The first color in the list
     * corresponds to the tile on top of the column.
     */
    private final List<TileColor> tileColors;

    /**
     * It is the list of integers to be displayed on the foreground of the tiles iff the column is being
     * masked. In particular if the mask number is 0, no number has to be shown on the foreground of the corresponding
     * tile, and it has to be blurred, otherwise the tile can remain colored and the mask number should be displayed
     * on foreground.
     */
    private final List<Integer> maskNumbers;

    /**
     * It is true iff the column is being masked.
     */
    private boolean masked = false;

    /**
     * It is true iff the column is on focus.
     */
    private boolean onFocus = false;

    /**
     * It is equivalent to the number of lines (or columns since tiles have a square shape) that a single tile
     * of the column occupies on the terminal screen.
     */
    private int tileSide;

    /**
     * Constructor of the class.
     * It initializes all the tiles to {@link TileColor#EMPTY}.
     *
     * @param column is the index of this column in the bookshelf.
     */
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

    /**
     * Callback invoked when the user presses enter while the column is on focus.
     */
    private Consumer<Integer> onselect;

    /**
     * Allows to set the callback which is invoked when the user presses enter while the column is on focus.
     *
     * @param onselect is the callback invoked when the user presses enter while the column in on focus.
     * @return this BookshelfColumnDrawable after the specified callback has been set.
     */
    public BookshelfColumnDrawable onselect(Consumer<Integer> onselect) {
        this.onselect = onselect;

        return this;
    }

    /**
     * Colors the tile at the specified row in this column with the required {@link TileColor}.
     *
     * @param row is the row number of the tile to be colored.
     * @param tileColor is the desired color for the tile to be colored.
     * @return this BookshelfColumnDrawable after the specified tile has been colored.
     */
    public BookshelfColumnDrawable color(int row, TileColor tileColor) {
        if (!Bookshelf.isRowInsideTheBookshelf(row)) {
            throw new IllegalArgumentException("A bookshelf row must be between 0 and " + (Bookshelf.ROWS - 1) +
                ", got: " + row + " when coloring bookshelf column");
        }

        tileColors.set(row, tileColor);

        return this;
    }

    /**
     * Allows to set the mask number for the tile at the specified row in this column.
     *
     * @param row is the row number of the tile for which we want to specify the mask number.
     * @param maskNumber is the mask number that will be associated with the specified tile, in particular it should
     *                   be 0 if you want to blur the tile, greater than 0 if you want to display such a number
     *                   on the foreground of the tile.
     * @return this BookshelfColumnDrawable after the mask number has been set for the specified tile.
     */
    public BookshelfColumnDrawable mask(int row, int maskNumber) {
        if (!Bookshelf.isRowInsideTheBookshelf(row)) {
            throw new IllegalArgumentException("A bookshelf row must be between 0 and " + (Bookshelf.ROWS - 1) +
                ", got: " + row + " when masking bookshelf column");
        }

        maskNumbers.set(row, maskNumber);

        return this;
    }

    /**
     * Allows to mask or unmask the column. In particular, if masked is false, mask numbers for column tiles will be
     * ignored and no tile will be blurred nor numbers will be shown on foreground, otherwise tiles which have mask
     * number 0 will be blurred, while other will have the mask number displayed on foreground.
     *
     * @param masked it must be true iff we want to mask this column as described above.
     * @return this BookshelfColumnDrawable after it has been masked or unmasked according to the value of maked.
     */
    public BookshelfColumnDrawable masked(boolean masked) {
        this.masked = masked;

        return this;
    }
}
