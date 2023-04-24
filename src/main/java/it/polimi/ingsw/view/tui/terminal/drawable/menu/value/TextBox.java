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

    private int cursorColumn = 0;
    private int cursorLine = 0;

    private boolean showCursor = true;

    private boolean focusable = true;

    private boolean onFocus = false;

    private boolean textHidden = false;

    private Color color = Color.WHITE;

    private boolean acceptsNewLinesAndSpaces = false;

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

        if (showCursor && onFocus && coordinate.getColumn() - 1 == cursorColumn &&
            coordinate.getLine() - 1 == cursorLine) {

            return primitiveSymbol.highlightBackground().colorForeground(Color.BLACK);
        }

        return primitiveSymbol.colorForeground(color);
    }

    @Override
    public boolean handleInput(String key) {
        if (!showCursor) {
            return false;
        }

        int start = 0;
        for (int line = 0; line < cursorLine; line++) {
            start += getLineOfText(line + 1).length() + 1;
        }

        if (Terminal.TEXT.contains(key) || Terminal.NUMBERS.contains(key)
            || (acceptsNewLinesAndSpaces && key.equals(" "))) {
            text.insert(start + cursorColumn, key);
            cursorColumn++;

            calculateSize();

            return true;
        }

        if (key.equals("\b")) {
            if (cursorColumn == 0) {
                if (cursorLine == 0) {
                    return false;
                }

                text.replace(start - 2, start, "");

                cursorLine--;
                cursorColumn = getLineOfText(cursorLine + 1).length() - 1;
            } else {
                text.deleteCharAt(start + cursorColumn - 1);
                cursorColumn--;
            }

            calculateSize();

            return true;
        }

        if (acceptsNewLinesAndSpaces && key.equals("\r")) {
            text.insert(start + cursorColumn, " \n");
            cursorLine++;
            cursorColumn = 0;

            calculateSize();

            return true;
        }

        switch (key) {
            case Terminal.RIGHT_ARROW -> {
                if (cursorColumn == getLineOfText(cursorLine + 1).length() - 1) {
                    if (cursorLine == size.getLines() - 1) {
                        return false;
                    }

                    cursorLine++;
                    cursorColumn = 0;
                } else {
                    cursorColumn++;
                }

                calculateSize();

                return true;
            }
            case Terminal.LEFT_ARROW -> {
                if (cursorColumn == 0) {
                    if (cursorLine == 0) {
                        return false;
                    }

                    cursorLine--;
                    cursorColumn = getLineOfText(cursorLine + 1).length() - 1;
                } else {
                    cursorColumn--;
                }

                calculateSize();

                return true;
            }
            case Terminal.UP_ARROW -> {
                if (cursorLine == 0) {
                    return false;
                }

                cursorLine--;
                if (cursorColumn >= getLineOfText(cursorLine + 1).length()) {
                    cursorColumn = getLineOfText(cursorLine + 1).length() - 1;
                }

                calculateSize();

                return true;
            }
            case Terminal.DOWN_ARROW -> {
                if (cursorLine == size.getLines() - 1) {
                    return false;
                }

                cursorLine++;
                if (cursorColumn >= getLineOfText(cursorLine + 1).length()) {
                    cursorColumn = getLineOfText(cursorLine + 1).length() - 1;
                }

                calculateSize();

                return true;
            }
        }

        return false;
    }

    @Override
    public boolean focus(Coordinate desiredCoordinate) {
        if (!focusable) {
            return false;
        }

        onFocus = true;

        cursorLine = desiredCoordinate.getLine() - 1;

        if (cursorLine >= size.getLines()) {
            cursorLine = size.getLines() - 1;
        }

        if (desiredCoordinate.getColumn() <= getLineOfText(cursorLine + 1).length()) {
            cursorColumn = desiredCoordinate.getColumn() - 1;
        } else {
            cursorColumn = getLineOfText(cursorLine + 1).length() - 1;
        }

        return true;
    }

    @Override
    public void unfocus() {
        onFocus = false;
    }

    @Override
    public Optional<Coordinate> getFocusedCoordinate() {
        return onFocus ? Optional.of(new Coordinate(1, cursorColumn + 1)) : Optional.empty();
    }

    @Override
    public String getValue() {
        return getLineOfText(1).strip();
    }

    public TextBox text(String text) {
        this.text = new StringBuilder(text.replaceAll("\n", " \n") + " ");

        calculateSize();

        return this;
    }

    public TextBox hideCursor() {
        showCursor = false;
        cursorColumn = 0;

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

    public TextBox acceptsNewLinesAndSpaces() {
        acceptsNewLinesAndSpaces = true;

        return this;
    }
}
