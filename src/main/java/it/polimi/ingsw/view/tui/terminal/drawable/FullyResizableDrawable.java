package it.polimi.ingsw.view.tui.terminal.drawable;

import it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.FullyResizableOrientedLayoutElement;

public abstract class FullyResizableDrawable extends Drawable {
    @Override
    public void askForSize(DrawableSize desiredSize) {
        size = desiredSize;
    }

    @Override
    public FullyResizableOrientedLayoutElement weight(int weight) {
        return new FullyResizableOrientedLayoutElement(this, weight);
    }

    public FixedSizeDrawable fixSize(DrawableSize size) {
        return new FixedSizeDrawable(this, size);
    }
}
