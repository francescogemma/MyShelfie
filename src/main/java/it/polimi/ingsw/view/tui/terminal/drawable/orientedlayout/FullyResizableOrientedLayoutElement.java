package it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout;

import it.polimi.ingsw.view.tui.terminal.drawable.FullyResizableDrawable;

/**
 * Represents a {@link FullyResizableOrientedLayout} element, that is a {@link FullyResizableDrawable} with an
 * associated weight proportional to the space that it occupies in the layout.
 *
 * @author Cristiano Migali
 */
public class FullyResizableOrientedLayoutElement extends OrientedLayoutElement {
    /**
     * Constructor of the class.
     * Associates the FullyResizableDrawable with the given weight.
     *
     * @param drawable is the {@link FullyResizableDrawable} to which a weight will be associated.
     * @param weight is the weight associated to the FullyResizableDrawable; it is proportional to the space that
     *               the FullyResizableDrawable will occupy in the layout.
     */
    public FullyResizableOrientedLayoutElement(FullyResizableDrawable drawable, int weight) {
        super(drawable, weight);
    }

    @Override
    public FullyResizableDrawable getDrawable() {
        return (FullyResizableDrawable) super.getDrawable();
    }
}
