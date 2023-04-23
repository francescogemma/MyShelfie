package it.polimi.ingsw.view.tui.terminal.drawable.menu.value;

import it.polimi.ingsw.view.tui.terminal.Terminal;
import it.polimi.ingsw.view.tui.terminal.drawable.Coordinate;
import it.polimi.ingsw.view.tui.terminal.drawable.DrawableSize;
import it.polimi.ingsw.view.tui.terminal.drawable.OutOfDrawableException;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Color;
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

    private Color color = Color.WHITE;

    private String getLineOfText(int line) {
        int start = 0;

        int countNewLines = 1;
        while (countNewLines < line) {
            start++;
            if (text.charAt(start - 1) == '\n') {
                countNewLines++;
            }
        }

        int end = start;
        for (; end < text.length(); end++) {
            if (text.charAt(end) == '\n') {
                break;
            }
        }

        return text.substring(start, end);
    }

    private void calculateSize() {
        int lines = 1;
        int columns = 1;

        int column = 1;
        for (int i = 0; i < text.length(); i++) {
            columns = Math.max(columns, column);

            if (text.charAt(i) == '\n') {
                lines++;
                column = 1;
            } else {
                column++;
            }
        }

        size = new DrawableSize(lines, columns);
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

        PrimitiveSymbol primitiveSymbol;
        if (textHidden) {
            if (coordinate.getColumn() < getLineOfText(coordinate.getLine()).length()) {
                primitiveSymbol = PrimitiveSymbol.STAR;
            } else {
                primitiveSymbol = PrimitiveSymbol.EMPTY;
            }
        } else {
            if (coordinate.getColumn() - 1 < getLineOfText(coordinate.getLine()).length()) {
                primitiveSymbol = PrimitiveSymbol.fromString(String.valueOf(getLineOfText(coordinate.getLine())
                    .charAt(coordinate.getColumn() - 1)));
            } else {
                primitiveSymbol = PrimitiveSymbol.EMPTY;
            }
        }

        return primitiveSymbol
            .highlightBackground(showCursor && onFocus && coordinate.getColumn() - 1 == cursorPosition)
            .colorForeground(color);
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
        if (!showCursor) {
            return false;
        }

        if (Terminal.TEXT.contains(key) || Terminal.NUMBERS.contains(key)) {
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
        this.text = new StringBuilder(text.replaceAll("\n", " \n") + " ");

        if (text.contains("\n")) {
            focusable = false;
        }

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

    public TextBox color(Color color) {
        this.color = color;

        return this;
    }
}
