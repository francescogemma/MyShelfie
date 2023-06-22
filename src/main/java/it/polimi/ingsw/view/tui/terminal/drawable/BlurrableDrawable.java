package it.polimi.ingsw.view.tui.terminal.drawable;

import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Symbol;

import java.util.Optional;

/**
 * Represents a Drawable which can blur a child Drawable.
 * That is, all its {@link Symbol}s can be formatted with a foreground grey shade, simulating a blur effect.
 *
 * @author Cristiano Migali
 */
public class BlurrableDrawable extends Drawable {
    /**
     * It is true iff the BlurrableDrawable is being blurred.
     */
    private boolean blurred = false;

    /**
     * It is the underlying child Drawable which can be blurred.
     */
    private final Drawable toBlur;

    /**
     * Constructor of the class.
     * Initializes the underlying Drawable which can be blurred.
     *
     * @param toBlur is the underlying Drawable which can be blurred.
     */
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
        if (!size.isInside(coordinate)) {
            throw new OutOfDrawableException(size, coordinate);
        }

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

    /**
     * Allows to specify if the underlying Drawable must be blurred or not.
     *
     * @param blurred must be true iff we want to blur the underlying Drawable.
     */
    public void blur(boolean blurred) {
        if (blurred) {
            toBlur.unfocus();
        }

        this.blurred = blurred;
    }
}
