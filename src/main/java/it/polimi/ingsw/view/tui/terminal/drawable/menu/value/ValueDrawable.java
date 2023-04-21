package it.polimi.ingsw.view.tui.terminal.drawable.menu.value;

import it.polimi.ingsw.view.tui.terminal.drawable.Drawable;

public abstract class ValueDrawable<T> extends Drawable {
    public abstract T getValue();
}
