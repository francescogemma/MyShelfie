package it.polimi.ingsw.view.tui;

import it.polimi.ingsw.model.tile.TileColor;
import it.polimi.ingsw.view.tui.terminal.drawable.*;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Color;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.PrimitiveSymbol;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Symbol;

import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * It is a {@link Drawable} used to display a tile inside the {@link BoardDrawable}.
 * A tile is a square which can have different side lengths (3, 4 or 5 terminal cells) in order to fit the
 * available space. The inner of the square is colored with a given {@link TileColor}, on the outer there is
 * a border box. TileDrawable are focusable and can be selected or deselected, selectable or not.
 * When a TileDrawable is on focus, its border turns red.
 * In particular a TileDrawable is focusable iff it is selectable. A TileDrawable can be selected or not:
 * if the user is focusing a TileDrawable which is selectable but not selected and presses enter, the TileDrawable
 * will become selected. Conversely if the TileDrawable is selected and the user presses enter, the TileDrawable
 * will become deselected.
 * The TileDrawable allows to set callbacks to get notified when one of these events happens.
 * When a TileDrawable is selected, the inner of the square has {@link PrimitiveSymbol#HASHTAG} on foreground.
 * A TileDrawable can also be a fill tile, which is used to fill the blank space in the board, keeping other tiles
 * with the right alignment. Fill tiles are displayed as empty space.
 *
 * @author Cristiano Migali
 */
public class TileDrawable extends Drawable {
    /**
     * It is true iff this TileDrawable is a fill tile.
     */
    private final boolean fillTile;

    /**
     * It is the number of the row where this TileDrawable is placed in the board.
     */
    private final int rowInBoard;

    /**
     * It is the number of the column where this TileDrawable is placed in the board.
     */
    private final int columnInBoard;

    /**
     * It is the number of cells occupied by the side of a small TileDrawable (accounting for the border).
     */
    public static final int SMALL_SIDE = 3;

    /**
     * It is the number of cells occupied by the side of a medium TileDrawable (accounting for the border).
     */
    public static final int MEDIUM_SIDE = 4;

    /**
     * It is the number of cells occupied by the side of a large TileDrawable (accounting for the border).
     */
    public static final int LARGE_SIDE = 5;

    /**
     * Constructor of the class.
     *
     * @param fillTile it indicates it this TileDrawable is a fill tile or not.
     * @param rowInBoard it is the number of the row where this TileDrawable is placed in the board.
     * @param columnInBoard it is the number of the column where this TileDrawable is placed in the board.
     */
    public TileDrawable(boolean fillTile, int rowInBoard, int columnInBoard) {
        this.fillTile = fillTile;
        this.rowInBoard = rowInBoard;
        this.columnInBoard = columnInBoard;

        selectable = !fillTile;
    }

    /**
     * It is the {@link TileColor} which the inner of the TileDrawable is colored with.
     */
    private TileColor tileColor = TileColor.EMPTY;

    /**
     * It is true iff the TileDrawable is on focus.
     */
    private boolean onFocus = false;

    /**
     * It is true iff the TileDrawable is selected.
     */
    private boolean selected = false;

    /**
     * It is true iff the TileDrawable is selectable.
     */
    private boolean selectable;

    /**
     * Converts a {@link TileColor} to the corresponding {@link Color} which can be used to format an area of
     * the terminal screen.
     *
     * @param tileColor is the {@link TileColor} that we want to convert.
     * @return the {@link Color} corresponding to the provided {@link TileColor}.
     */
    public static Color tileColorToColor(TileColor tileColor) {
        return switch (tileColor) {
            case GREEN -> Color.GREEN;
            case MAGENTA -> Color.MAGENTA;
            case BLUE -> Color.BLUE;
            case YELLOW -> Color.YELLOW;
            case WHITE -> Color.WHITE;
            case CYAN -> Color.CYAN;
            default -> throw new IllegalArgumentException("You can't convert the empty tile to a color");
        };
    }

    @Override
    public void askForSize(DrawableSize desiredSize) {
        if (desiredSize.getLines() < MEDIUM_SIDE || desiredSize.getColumns() < MEDIUM_SIDE) {
            size = new DrawableSize(SMALL_SIDE, SMALL_SIDE);
            return;
        }

        if (desiredSize.getLines() < LARGE_SIDE || desiredSize.getColumns() < LARGE_SIDE) {
            size = new DrawableSize(MEDIUM_SIDE, MEDIUM_SIDE);
            return;
        }

        size = new DrawableSize(LARGE_SIDE, LARGE_SIDE);
    }

    /**
     * @return the foreground {@link PrimitiveSymbol} used to fill the inner of the square.
     * It correspond to {@link PrimitiveSymbol#EMPTY} is the TileDrawable is not selected,
     * {@link PrimitiveSymbol#HASHTAG} otherwise.
     */
    private Symbol getContent() {
        if (selected) {
            return PrimitiveSymbol.HASHTAG.colorForeground(Color.BLACK);
        }

        return PrimitiveSymbol.EMPTY;
    }

    @Override
    public Symbol getSymbolAt(Coordinate coordinate) {
        if (!size.isInside(coordinate)) {
            throw new OutOfDrawableException(size, coordinate);
        }

        if (fillTile) {
            return getContent();
        }

        return WithBorderBoxDrawable.addBorder(coordinate, size)
            .map(primitiveSymbol -> {
                if (!selectable) {
                    return primitiveSymbol.blur();
                }

                return primitiveSymbol.highlight(Color.FOCUS, onFocus);
            }).orElse(tileColor == TileColor.EMPTY ? getContent() :
                    getContent().colorBackground(tileColorToColor(tileColor)));
    }

    @Override
    public boolean handleInput(String key) {
        if (!selectable) {
            return false;
        }

        if (key.equals("\r")) {
            if (selected) {
                if (ondeselect == null) {
                    return false;
                }

                ondeselect.accept(rowInBoard, columnInBoard);
            } else {
                if (onselect == null) {
                    return false;
                }

                onselect.accept(rowInBoard, columnInBoard);
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean focus(Coordinate desiredCoordinate) {
        if (!selectable) {
            return false;
        }

        onFocus = true;

        return true;
    }

    @Override
    public void unfocus() {
        onFocus = false;
    }

    @Override
    public Optional<Coordinate> getFocusedCoordinate() {
        if (onFocus) {
            return Optional.of(getCenter());
        }

        return Optional.empty();
    }

    /**
     * Allows to select or deselect this TileDrawable.
     *
     * @param selected must be true iff we want to select this TileDrawable.
     * @return this TileDrawable after it has been selected or deselected according to the provided value.
     */
    public TileDrawable selected(boolean selected) {
        if (fillTile) {
            throw new IllegalStateException("You can't select a fill tile");
        }

        this.selected = selected;

        return this;
    }

    /**
     * Allows to set if this TileDrawable is selectable or not.
     *
     * @param selectable must be true iff we want this TileDrawable to be selectable.
     * @return this TileDrawable after it has been set selectable or not according to the provided value.
     */
    public TileDrawable selectable(boolean selectable) {
        if (fillTile) {
            throw new IllegalStateException("A fill tile must be always non-selectable");
        }

        this.selectable = selectable;
        if (!selectable) {
            unfocus();
        }

        return this;
    }

    /**
     * Allows to set the {@link TileColor} used to fill the inner of the square.
     *
     * @param tileColor is the {@link TileColor} used to fill the inner of the square.
     * @return this TileColor after its color has been set.
     */
    public TileDrawable color(TileColor tileColor) {
        this.tileColor = tileColor;

        return this;
    }

    /**
     * @return the row number where this TileDrawable is placed in the board.
     */
    public int getRowInBoard() {
        return rowInBoard;
    }

    /**
     * @return the tile number where this TileDrawable is placed in the board.
     */
    public int getColumnInBoard() {
        return columnInBoard;
    }

    /**
     * Callback which is invoked when the TileDrawable is on focus, not selected and the user presses enter.
     */
    private BiConsumer<Integer, Integer> onselect;

    /**
     * Allows to set the callback which is invoked when the TileDrawable is on focus, not selected and the user
     * presses enter.
     *
     * @param onselect is the callback which is invoked when the TileDrawable is on focus, not selected and the
     *                 user presses enter.
     * @return this TileDrawable after the provided callback has been set.
     */
    public TileDrawable onselect(BiConsumer<Integer, Integer> onselect) {
        if (fillTile) {
            throw new IllegalArgumentException("You can't add onselect callback to a fill tile");
        }

        this.onselect = onselect;

        return this;
    }

    /**
     * Callback which is invoked when the TileDrawable is on focus, selected and the user presses enter.
     */
    private BiConsumer<Integer, Integer> ondeselect;

    /**
     * Allows to set the callback which is invoked when the TileDrawable is on focus, selected and the user
     * presses enter.
     *
     * @param ondeselect is the callback which is invoked when the TileDrawable is on focus, selected and the
     *                   user presses enter.
     * @return this TileDrawable after the provided callback has been set.
     */
    public TileDrawable ondeselect(BiConsumer<Integer, Integer> ondeselect) {
        if (fillTile) {
            throw new IllegalStateException("You can't add ondeselect callback to a fill tile");
        }

        this.ondeselect = ondeselect;

        return this;
    }
}
