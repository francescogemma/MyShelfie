package it.polimi.ingsw.view.tui.terminal.drawable;

import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Symbol;

import java.util.Optional;

public class AlternativeDrawable extends Drawable {
    private final Drawable[] drawables = new Drawable[2];
    private int currentDrawableIndex = 0;

    private boolean onFocus = false;

    public AlternativeDrawable(Drawable firstDrawable, Drawable secondDrawable) {
        drawables[0] = firstDrawable;
        drawables[1] = secondDrawable;
    }

    @Override
    public void askForSize(DrawableSize desiredSize) {
        drawables[0].askForSize(desiredSize);
        drawables[1].askForSize(desiredSize);

        size = drawables[currentDrawableIndex].getSize();
    }

    @Override
    public Symbol getSymbolAt(Coordinate coordinate) {
        return drawables[currentDrawableIndex].getSymbolAt(coordinate);
    }

    @Override
    public boolean handleInput(String key) {
        if (!onFocus) {
            return false;
        }

        return drawables[currentDrawableIndex].handleInput(key);
    }

    @Override
    public boolean focus(Coordinate desiredCoordinate) {
        if (drawables[currentDrawableIndex].focus(desiredCoordinate)) {
            onFocus = true;

            return true;
        }

        return false;
    }

    @Override
    public void unfocus() {
        if (onFocus) {
            onFocus = false;

            drawables[currentDrawableIndex].unfocus();
        }
    }

    @Override
    public Optional<Coordinate> getFocusedCoordinate() {
        if (!onFocus) {
            return Optional.empty();
        }

        return drawables[currentDrawableIndex].getFocusedCoordinate();
    }

    public AlternativeDrawable toggle() {
        if (onFocus) {
            onFocus = drawables[1 - currentDrawableIndex].focus(drawables[currentDrawableIndex]
                    .getFocusedCoordinate().orElse(Coordinate.origin()));

            drawables[currentDrawableIndex].unfocus();
        }

        currentDrawableIndex = 1 - currentDrawableIndex;

        return this;
    }

    public AlternativeDrawable first() {
        if (currentDrawableIndex == 1) {
            return toggle();
        }

        return this;
    }

    public AlternativeDrawable second() {
        if (currentDrawableIndex == 0) {
            return toggle();
        }

        return this;
    }
}
