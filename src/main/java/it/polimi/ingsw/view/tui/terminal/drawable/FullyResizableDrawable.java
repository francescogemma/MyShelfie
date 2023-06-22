package it.polimi.ingsw.view.tui.terminal.drawable;

import it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.FullyResizableOrientedLayoutElement;

/**
 * Represents a Drawable which can resize exactly to every requested size through {@link Drawable#askForSize(DrawableSize)}.
 * That is, after a call to {@link Drawable#askForSize(DrawableSize)} with a certain size, we are guaranteed that
 * the DrawableSize will be equal to the requested one.
 *
 * @author Cristiano Migali
 */
public abstract class FullyResizableDrawable extends Drawable {
    @Override
    public void askForSize(DrawableSize desiredSize) {
        size = desiredSize;
    }

    @Override
    public FullyResizableOrientedLayoutElement weight(int weight) {
        return new FullyResizableOrientedLayoutElement(this, weight);
    }

    /**
     * @param size is the {@link DrawableSize} this FullyResizableDrawable will be fixed to.
     * @return a {@link FixedSizeDrawable} whose underlying FullyResizableDrawable is this one, which now has
     * the size specified through size.
     */
    public FixedSizeDrawable fixSize(DrawableSize size) {
        return new FixedSizeDrawable(this, size);
    }
}
