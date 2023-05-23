package it.polimi.ingsw.view.tui.terminal.drawable.align;

import it.polimi.ingsw.view.tui.terminal.drawable.Drawable;
import it.polimi.ingsw.view.tui.terminal.drawable.crop.CropDrawable;
import it.polimi.ingsw.view.tui.terminal.drawable.crop.ScrollableDrawable;

/**
 * Represents a {@link Drawable} which doesn't have a maximum size. Concrete implementations usually take another Drawable
 * in the constructor and transform it in a Drawable with no maximum size. This is usually achieved by adding
 * surrounding empty space (through {@link it.polimi.ingsw.view.tui.terminal.drawable.symbol.PrimitiveSymbol#EMPTY})
 * when the desired size is greater than the maximum size of the inner Drawable.
 * AlignedDrawable's implementations differs from the way in which they place (align) the inner Drawable inside
 * the available space when there is more of it than required (for example the inner Drawable could be centered in
 * the free space), this is where the name comes from.
 */
public abstract class AlignedDrawable extends Drawable {
    /**
     * @return a {@link CropDrawable} which contains the AlignedDrawable on which this method has been invoked.
     */
    public CropDrawable crop() {
        return new CropDrawable(this);
    }

    /**
     * @return a {@link ScrollableDrawable} which contains the AlignedDrawable on which this method has been invoked.
     */
    public ScrollableDrawable scrollable() {
        return new ScrollableDrawable(this);
    }
}
