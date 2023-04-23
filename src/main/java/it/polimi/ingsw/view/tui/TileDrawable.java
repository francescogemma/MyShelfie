package it.polimi.ingsw.view.tui;

import it.polimi.ingsw.model.tile.TileColor;
import it.polimi.ingsw.view.tui.terminal.drawable.*;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Color;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.PrimitiveSymbol;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Symbol;

import java.util.Optional;
import java.util.function.BiConsumer;

public class TileDrawable extends Drawable {
    private final boolean fillTile;
    private final int rowInBoard;
    private final int columnInBoard;

    public static final int SMALL_SIDE = 3;
    public static final int MEDIUM_SIDE = 4;
    public static final int LARGE_SIDE = 5;

    public TileDrawable(boolean fillTile, int rowInBoard, int columnInBoard) {
        this.fillTile = fillTile;
        this.rowInBoard = rowInBoard;
        this.columnInBoard = columnInBoard;

        selectable = !fillTile;
    }

    private TileColor tileColor = TileColor.EMPTY;

    private boolean onFocus = false;
    private boolean selected = false;
    private boolean selectable;

    private Color getColor() {
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

    @Override
    public Symbol getSymbolAt(Coordinate coordinate) {
        if (!size.isInside(coordinate)) {
            throw new OutOfDrawableException(size, coordinate);
        }

        if (fillTile) {
            return PrimitiveSymbol.EMPTY;
        }

        return WithBorderBoxDrawable.addBorder(coordinate, size)
            .map(primitiveSymbol ->
                primitiveSymbol.highlight(selected ? Color.SELECTED : Color.FOCUS,
                    selected || onFocus))
                .orElse(tileColor == TileColor.EMPTY ? PrimitiveSymbol.EMPTY :
                    PrimitiveSymbol.EMPTY.colorBackground(getColor()));
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

    public TileDrawable selected(boolean selected) {
        if (fillTile) {
            throw new IllegalStateException("You can't select a fill tile");
        }

        this.selected = selected;

        return this;
    }

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

    public TileDrawable color(TileColor tileColor) {
        this.tileColor = tileColor;

        return this;
    }

    private BiConsumer<Integer, Integer> onselect;

    public TileDrawable onselect(BiConsumer<Integer, Integer> onselect) {
        if (fillTile) {
            throw new IllegalArgumentException("You can't add onselect callback to a fill tile");
        }

        this.onselect = onselect;

        return this;
    }

    private BiConsumer<Integer, Integer> ondeselect;

    public TileDrawable ondeselect(BiConsumer<Integer, Integer> ondeselect) {
        if (fillTile) {
            throw new IllegalStateException("You can't add ondeselect callback to a fill tile");
        }

        this.ondeselect = ondeselect;

        return this;
    }
}
