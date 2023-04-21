package it.polimi.ingsw.view.tui.terminal.drawable.align;

import it.polimi.ingsw.view.tui.terminal.drawable.Drawable;
import it.polimi.ingsw.view.tui.terminal.drawable.crop.CropDrawable;
import it.polimi.ingsw.view.tui.terminal.drawable.crop.ScrollableDrawable;

public abstract class AlignedDrawable extends Drawable {
    public CropDrawable crop() {
        return new CropDrawable(this);
    }

    public ScrollableDrawable scrollable() {
        return new ScrollableDrawable(this);
    }
}
