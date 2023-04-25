package it.polimi.ingsw.view.tui.terminal.drawable;

import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Symbol;

import java.util.Optional;

public class BlurrableDrawable extends Drawable {
    private boolean blurred = false;
    private final Drawable toBlur;

    public BlurrableDrawable(Drawable toBlur) {
        this.toBlur = toBlur;
    }

    @Override
    public void askForSize(DrawableSize desiredSize) {
        toBlur.askForSize(desiredSize);

        size = toBlur.getSize();
    }

    @Override
    public Symbol getSymbolAt(Coordinate coordinate) {
        if (blurred) {
            return toBlur.getSymbolAt(coordinate).getPrimitiveSymbol().blur();
        }
        return toBlur.getSymbolAt(coordinate);
    }

    @Override
    public boolean handleInput(String key) {
        if (blurred) {
            return false;
        }

        return toBlur.handleInput(key);
    }

    @Override
    public boolean focus(Coordinate desiredCoordinate) {
        if (blurred) {
            return false;
        }

        return toBlur.focus(desiredCoordinate);
    }

    @Override
    public void unfocus() {
        toBlur.unfocus();
    }

    @Override
    public Optional<Coordinate> getFocusedCoordinate() {
        if (blurred) {
            return Optional.empty();
        }

        return toBlur.getFocusedCoordinate();
    }

    public void blur(boolean blurred) {
        if (blurred) {
            toBlur.unfocus();
        }

        this.blurred = blurred;
    }
}
