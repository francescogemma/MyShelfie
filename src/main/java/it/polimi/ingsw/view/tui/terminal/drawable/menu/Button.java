package it.polimi.ingsw.view.tui.terminal.drawable.menu;

import it.polimi.ingsw.view.tui.terminal.drawable.*;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.value.TextBox;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Symbol;

/**
 * Represents a button that can be displayed on screen and which the user can interact with. It is composed by a
 * label surrounded by a border box. The border box turns red when the button is on focus.
 * When a button is on focus and the user presses enter, the corresponding (on press) callback will be triggered.
 */
public class Button extends FixedLayoutDrawable<Drawable> {
    /**
     * It is the callback which is invoked when the user presses enter while this button is on focus.
     */
    private Runnable onpress;

    /**
     * It is true iff this button is on focus.
     */
    private boolean onFocus = false;

    /**
     * It is true iff this button is focusable.
     */
    private boolean focusable = true;

    /**
     * Constructor of the class.
     * It initializes a layout with the button label surrounded by a border box.
     *
     * @param label is the label of the button.
     */
    public Button(String label) {
        setLayout(new TextBox().text(" " + label).hideCursor().center().crop().fixSize(
            new DrawableSize(3, label.length() + 2)
        ).addBorderBox());
    }

    @Override
    public Symbol getSymbolAt(Coordinate coordinate) {
        Symbol symbol = super.getSymbolAt(coordinate);

        if (!focusable) {
            symbol = symbol.getPrimitiveSymbol().blur();
        }

        return symbol;
    }

    @Override
    public boolean handleInput(String key) {
        if (!onFocus) {
            return false;
        }

        if (onpress == null) {
            return false;
        }

        if (key.equals("\r")) {
            onpress.run();

            return true;
        }

        return false;
    }

    @Override
    public boolean focus(Coordinate desiredCoordinate) {
        if (!focusable) {
            return false;
        }

        onFocus = true;
        return super.focus(desiredCoordinate);
    }

    /**
     * Sets the specified callback which gets invoked when the user presses enter while this button is on focus.
     *
     * @param onpress the callback to be invoked when the button has been pressed.
     * @return this Button after the specified callback has been set.
     */
    public Button onpress(Runnable onpress) {
        this.onpress = onpress;

        return this;
    }

    /**
     * Sets if this Button is focusable or not.
     *
     * @param focusable must be true if we want to make this Button focusable, false otherwise.
     * @return this Button after its focusable property has been set with the specified value.
     */
    public Button focusable(boolean focusable) {
        this.focusable = focusable;

        if (!focusable && onFocus) {
            onFocus = false;

            super.unfocus();
        }

        return this;
    }
}
