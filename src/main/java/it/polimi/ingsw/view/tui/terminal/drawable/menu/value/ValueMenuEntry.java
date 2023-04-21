package it.polimi.ingsw.view.tui.terminal.drawable.menu.value;

import it.polimi.ingsw.view.tui.terminal.drawable.Drawable;
import it.polimi.ingsw.view.tui.terminal.drawable.DrawableSize;
import it.polimi.ingsw.view.tui.terminal.drawable.FixedLayoutDrawable;
import it.polimi.ingsw.view.tui.terminal.drawable.Orientation;
import it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.FullyResizableOrientedLayout;

public class ValueMenuEntry<T> extends FixedLayoutDrawable<Drawable> {
    private final ValueDrawable<T> valueDrawable;

    public ValueMenuEntry(String label, ValueDrawable<T> valueDrawable) {
        setLayout(new FullyResizableOrientedLayout(Orientation.HORIZONTAL,
            new TextBox().text(label + ": ").unfocusable().alignUpLeft().crop().weight(40),
            valueDrawable.alignUpLeft().crop().weight(60)
        ).addBorderBox().center().crop().fixSize(new DrawableSize(3, 50)));

        this.valueDrawable = valueDrawable;
    }

    public T getValue() {
        return valueDrawable.getValue();
    }
}
