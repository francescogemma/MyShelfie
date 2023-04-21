package it.polimi.ingsw.view.tui.terminal.drawable;

import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Symbol;

import java.util.Optional;

public class FixedSizeDrawable extends Drawable {
    private final FullyResizableDrawable toFixSize;

    public FixedSizeDrawable(FullyResizableDrawable toFixSize, DrawableSize size) {
        this.toFixSize = toFixSize;
        this.size = size;
    }

    @Override
    public void askForSize(DrawableSize desiredSize) {
        toFixSize.askForSize(size);
    }

    @Override
    public Symbol getSymbolAt(Coordinate coordinate) {
        return toFixSize.getSymbolAt(coordinate);
    }

    @Override
    public boolean handleInput(String key) {
        return toFixSize.handleInput(key);
    }

    @Override
    public boolean focus(Coordinate desiredCoordinate) {
        return toFixSize.focus(desiredCoordinate);
    }

    @Override
    public void unfocus() {
        toFixSize.unfocus();
    }

    @Override
    public Optional<Coordinate> getFocusedCoordinate() {
        return toFixSize.getFocusedCoordinate();
    }
}
