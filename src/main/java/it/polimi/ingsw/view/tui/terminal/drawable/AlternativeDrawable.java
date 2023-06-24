package it.polimi.ingsw.view.tui.terminal.drawable;

import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Symbol;

import java.util.Optional;

/**
 * Represents a Drawable which allows to display alternatively two child Drawables.
 *
 * @author Cristiano Migali
 */
public class AlternativeDrawable extends Drawable {
    /**
     * Is the array of 2 Drawables which can be displayed alternatively.
     */
    private final Drawable[] drawables = new Drawable[2];

    /**
     * It is the index of current displayed Drawable in drawables array. Since the drawables are 2, it can be 0 or 1.
     */
    private int currentDrawableIndex = 0;

    /**
     * It is true iff the AlternativeDrawable is on focus.
     */
    private boolean onFocus = false;

    /**
     * Constructor of the class.
     * Initializes the two Drawables to be shown alternatively.
     *
     * @param firstDrawable is the first Drawable which is shown by default.
     * @param secondDrawable is the second Drawable which is hidden by default.
     */
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

    /**
     * @return this AlternativeDrawable which has now switched the child Drawable that is being displayed, that is,
     * if the current displayed Drawable was the first one, now it is displaying the second and vice-versa.
     */
    public AlternativeDrawable toggle() {
        if (onFocus) {
            onFocus = drawables[1 - currentDrawableIndex].focus(drawables[currentDrawableIndex]
                    .getFocusedCoordinate().orElse(Coordinate.origin()));

            drawables[currentDrawableIndex].unfocus();
        }

        currentDrawableIndex = 1 - currentDrawableIndex;

        return this;
    }

    /**
     * @return this AlternativeDrawable which is now displaying the first Drawable set in the constructor.
     */
    public AlternativeDrawable first() {
        if (currentDrawableIndex == 1) {
            return toggle();
        }

        return this;
    }

    /**
     * @return this AlternativeDrawable which is now displaying the second Drawable set in the constructor.
     */
    public AlternativeDrawable second() {
        if (currentDrawableIndex == 0) {
            return toggle();
        }

        return this;
    }
}
