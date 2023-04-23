package it.polimi.ingsw.view.tui;

import it.polimi.ingsw.model.tile.TileColor;
import it.polimi.ingsw.view.tui.terminal.drawable.*;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Color;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.PrimitiveSymbol;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Symbol;

import java.util.Optional;

public class TileDrawable extends Drawable {
    private final boolean fillTile;

    public TileDrawable(boolean fillTile) {
        this.fillTile = fillTile;
    }

    private TileColor tileColor = TileColor.EMPTY;

    private boolean onFocus = false;
    private boolean selected = false;

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
        if (desiredSize.getLines() < 4 || desiredSize.getColumns() < 4) {
            size = new DrawableSize(3, 3);
            return;
        }

        if (desiredSize.getLines() < 5 || desiredSize.getColumns() < 5) {
            size = new DrawableSize(4, 4);
            return;
        }

        size = new DrawableSize(5, 5);
    }

    @Override
    public Symbol getSymbolAt(Coordinate coordinate) {
        if (!size.isInside(coordinate)) {
            throw new OutOfDrawableException(size, coordinate);
        }

        if (fillTile) {
            return PrimitiveSymbol.EMPTY;
        }

        return WithBorderBoxDrawable.addBorder(coordinate, size).map(primitiveSymbol ->
                primitiveSymbol.highlight(Color.FOCUS, onFocus))
            .orElse(tileColor == TileColor.EMPTY ? PrimitiveSymbol.EMPTY :
                PrimitiveSymbol.EMPTY.colorBackground(getColor()));
    }

    @Override
    public boolean handleInput(String key) {
        return false;
    }

    @Override
    public boolean focus(Coordinate desiredCoordinate) {
        return false;
    }

    @Override
    public void unfocus() {

    }

    @Override
    public Optional<Coordinate> getFocusedCoordinate() {
        return Optional.empty();
    }
}
