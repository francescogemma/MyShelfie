package it.polimi.ingsw.view.tui.terminal.drawable.menu.value;

import it.polimi.ingsw.view.tui.terminal.Terminal;
import it.polimi.ingsw.view.tui.terminal.drawable.Coordinate;
import it.polimi.ingsw.view.tui.terminal.drawable.DrawableSize;
import it.polimi.ingsw.view.tui.terminal.drawable.OutOfDrawableException;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.PrimitiveSymbol;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Symbol;

import java.util.Optional;

public class TextBox extends ValueDrawable<String> {
    private StringBuilder text;

    private int cursorPosition = 0;

    private boolean showCursor = true;

    private boolean focusable = true;

    private boolean onFocus = false;

    private boolean textHidden = false;

    private void calculateSize() {
        size = new DrawableSize(1, text.length());
    }

    public TextBox() {
        text = new StringBuilder(" ");
        calculateSize();
    }

    @Override
    public void askForSize(DrawableSize desiredSize) {
        // TextBoxes size depends only on the text which is inside it
    }

    @Override
    public Symbol getSymbolAt(Coordinate coordinate) {
        if (!size.isInside(coordinate)) {
            throw new OutOfDrawableException(size, coordinate);
        }

        return PrimitiveSymbol.fromString(String.valueOf(textHidden && coordinate.getColumn() < text.length()
                ? "*" : text.charAt(coordinate.getColumn() - 1)))
            .highlightBackground(showCursor && onFocus && coordinate.getColumn() - 1 == cursorPosition);
    }

    private boolean moveCursor(int direction) {
        if (direction != -1 && direction != 1) {
            throw new IllegalArgumentException("Direction must be 1 or -1 when moving the cursor, got: " + direction);
        }

        int nextCursorPosition = cursorPosition + direction;

        if (nextCursorPosition < 0 || nextCursorPosition >= text.length()) {
            return false;
        }

        cursorPosition = nextCursorPosition;

        return true;
    }

    @Override
    public boolean handleInput(String key) {
        if (Terminal.TEXT.contains(key)) {
            text.insert(cursorPosition, key);
            cursorPosition++;

            calculateSize();
            return true;
        }

        if (key.equals("\b")) {
            if (cursorPosition == 0) {
                return false;
            }

            text.deleteCharAt(cursorPosition - 1);
            cursorPosition--;

            calculateSize();

            return true;
        }

        return switch (key) {
            case Terminal.LEFT_ARROW -> moveCursor(-1);
            case Terminal.RIGHT_ARROW -> moveCursor(1);
            default -> false;
        };
    }

    @Override
    public boolean focus(Coordinate desiredCoordinate) {
        if (!focusable) {
            return false;
        }

        onFocus = true;

        if (desiredCoordinate.getColumn() < text.length()) {
            cursorPosition = desiredCoordinate.getColumn();
        }

        return true;
    }

    @Override
    public void unfocus() {
        onFocus = false;
    }

    @Override
    public Optional<Coordinate> getFocusedCoordinate() {
        return onFocus ? Optional.of(new Coordinate(1, cursorPosition + 1)) : Optional.empty();
    }

    @Override
    public String getValue() {
        return text.toString().strip();
    }

    public TextBox text(String text) {
        this.text = new StringBuilder(text + " ");
        calculateSize();

        return this;
    }

    public TextBox hideCursor() {
        showCursor = false;
        cursorPosition = 0;

        return this;
    }

    public TextBox unfocusable() {
        focusable = false;

        return this;
    }

    public TextBox hideText() {
        textHidden = true;

        return this;
    }
}
