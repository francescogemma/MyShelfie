package it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout;

import it.polimi.ingsw.view.tui.terminal.drawable.FullyResizableDrawable;

public class FullyResizableOrientedLayoutElement extends OrientedLayoutElement {
    public FullyResizableOrientedLayoutElement(FullyResizableDrawable drawable, int weight) {
        super(drawable, weight);
    }

    @Override
    public FullyResizableDrawable getDrawable() {
        return (FullyResizableDrawable) super.getDrawable();
    }
}
