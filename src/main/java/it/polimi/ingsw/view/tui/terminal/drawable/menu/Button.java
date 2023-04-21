package it.polimi.ingsw.view.tui.terminal.drawable.menu;

import it.polimi.ingsw.view.tui.terminal.drawable.*;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.value.TextBox;

public class Button extends FixedLayoutDrawable<Drawable> {
    private Runnable onpress;

    public Button(String label) {
        setLayout(new TextBox().text(" " + label).hideCursor().center().crop().fixSize(
            new DrawableSize(3, label.length() + 2)
        ).addBorderBox());
    }

    @Override
    public boolean handleInput(String key) {
        if (onpress == null) {
            return false;
        }

        if (key.equals("\r")) {
            onpress.run();

            return true;
        }

        return false;
    }

    public Button onpress(Runnable onpress) {
        this.onpress = onpress;

        return this;
    }
}
