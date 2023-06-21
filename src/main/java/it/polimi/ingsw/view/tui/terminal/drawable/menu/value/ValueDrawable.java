package it.polimi.ingsw.view.tui.terminal.drawable.menu.value;

import it.polimi.ingsw.view.tui.terminal.drawable.Drawable;

/**
 * Represents a {@link Drawable} which allows the user to specify an input value.
 * This value can be chosen by the user among a set of possibilities or entered from scratch.
 *
 * @param <T> is the type (an integer, a string, a boolean) of the value provided by the Drawable.
 *
 * @see TextBox
 * @see Options
 *
 * @author Cristiano Migali
 */
public abstract class ValueDrawable<T> extends Drawable {
    public abstract T getValue();
}
