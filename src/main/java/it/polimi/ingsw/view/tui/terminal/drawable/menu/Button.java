package it.polimi.ingsw.view.tui.terminal.drawable.menu;

import it.polimi.ingsw.view.tui.terminal.drawable.*;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.value.TextBox;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Symbol;

public class Button extends FixedLayoutDrawable<Drawable> {
    private Runnable onpress;

    private boolean onFocus = false;

    private boolean focusable = true;

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

    public Button onpress(Runnable onpress) {
        this.onpress = onpress;

        return this;
    }

    public Button focusable(boolean focusable) {
        this.focusable = focusable;

        if (!focusable && onFocus) {
            onFocus = false;

            super.unfocus();
        }

        return this;
    }
}
