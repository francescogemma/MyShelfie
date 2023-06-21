package it.polimi.ingsw.view.tui.terminal.drawable.menu.value;

import it.polimi.ingsw.view.tui.terminal.drawable.Drawable;
import it.polimi.ingsw.view.tui.terminal.drawable.DrawableSize;
import it.polimi.ingsw.view.tui.terminal.drawable.FixedLayoutDrawable;
import it.polimi.ingsw.view.tui.terminal.drawable.Orientation;
import it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.FullyResizableOrientedLayout;

/**
 * Represents a Drawable which displays a {@link ValueDrawable} beside a label (a string) describing the role of the
 * input value that the user has to specify.
 * It is useful to build menu interfaces.
 *
 * @param <T> is the type of the input value.
 *
 * @author Cristiano Migali
 */
public class ValueMenuEntry<T> extends FixedLayoutDrawable<Drawable> {
    /**
     * Is the ValueDrawable displayed beside the label in the menu entry.
     */
    private final ValueDrawable<T> valueDrawable;

    /**
     * Constructor of the class.
     * It initializes a layout with the specified label, displayed through a non-editable TextBox, beside the specified
     * {@link ValueDrawable} through which the user can enter the desired value.
     *
     * @param label is the label that will be shown beside the {@link ValueDrawable}.
     * @param valueDrawable is the {@link ValueDrawable} through which the user can enter the desired value.
     */
    public ValueMenuEntry(String label, ValueDrawable<T> valueDrawable) {
        setLayout(new FullyResizableOrientedLayout(Orientation.HORIZONTAL,
            new TextBox().text(label + ": ").unfocusable().alignUpLeft().crop().weight(40),
            valueDrawable.alignUpLeft().crop().weight(60)
        ).addBorderBox().center().crop().fixSize(new DrawableSize(3, 50)));

        this.valueDrawable = valueDrawable;
    }

    /**
     * @return the value that the user has entered in the {@link ValueDrawable}.
     */
    public T getValue() {
        return valueDrawable.getValue();
    }
}
