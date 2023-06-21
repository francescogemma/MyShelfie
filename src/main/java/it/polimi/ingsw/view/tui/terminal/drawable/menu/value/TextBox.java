package it.polimi.ingsw.view.tui.terminal.drawable.menu.value;

import it.polimi.ingsw.view.tui.terminal.Terminal;
import it.polimi.ingsw.view.tui.terminal.drawable.Coordinate;
import it.polimi.ingsw.view.tui.terminal.drawable.DrawableSize;
import it.polimi.ingsw.view.tui.terminal.drawable.OutOfDrawableException;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Color;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.PrimitiveSymbol;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Symbol;

import java.util.Optional;

/**
 * It is a ValueDrawable which allows to display text on screen.
 * TextBoxes support editing, so the user can type inside a TextBox to change its content.
 * Editable TextBoxes have a cursor in a certain position; by typing users will add the specified character at the
 * cursor position, they can also delete characters behind the cursor.
 * They can also be used to display unchangeable text.
 *
 * @author Cristiano Migali
 */
public class TextBox extends ValueDrawable<String> {
    /**
     * It contains the text that is being displayed by the TextBox.
     */
    private StringBuilder text;

    /**
     * It is the column component of the coordinate (which is 0 based conversely to {@link Coordinate}) of the
     * edit cursor inside the TextBox.
     */
    private int cursorColumn = 0;

    /**
     * It is the line component of the coordinate (which is 0 based conversely to {@link Coordinate}) of the
     * edit cursors inside the TextBox.
     */
    private int cursorLine = 0;

    /**
     * It is true iff the edit cursor is being shown on screen by making the corresponding symbol background white.
     */
    private boolean showCursor = true;

    /**
     * It is true iff this TextBox can be on focus (usually it is equivalent to be an editable TextBox).
     */
    private boolean focusable = true;

    /**
     * It is true iff the TextBox is on focus.
     */
    private boolean onFocus = false;

    /**
     * If true displays the {@link PrimitiveSymbol#STAR} instead of actual characters inside the TextBox. It is
     * useful for password fields.
     */
    private boolean textHidden = false;

    /**
     * It is the color of the text that is being displayed inside the TextBox.
     */
    private Color color = Color.WHITE;

    /**
     * It is true iff the TextBox supports multi line text.
     */
    private boolean acceptsNewLinesAndSpaces = false;

    /**
     * It is true iff the text inside the TextBox must be displayed in bold.
     */
    private boolean bold = false;

    /**
     * @param line is the line coordinate (0 based) of the desired line inside the text contained in the TextBox.
     * @return the line of text contained in the TextBox at the specified line coordinate.
     */
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

    /**
     * Adjusts the size of the TextBox Drawable to fit the contained text.
     */
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

    /**
     * Constructor of the class.
     * Initializes an empty TextBox.
     */
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

        if (primitiveSymbol == PrimitiveSymbol.EMPTY) {
            return PrimitiveSymbol.EMPTY;
        }

        Symbol symbol = primitiveSymbol.colorForeground(color);

        if (bold) {
            return symbol.bold();
        }

        return symbol;
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
        if (onFocus) {
            if (showCursor) {
                return Optional.of(new Coordinate(1, cursorColumn + 1));
            }

            return Optional.of(getCenter());
        }

        return Optional.empty();
    }

    @Override
    public String getValue() {
        return getLineOfText(1).strip();
    }

    /**
     * @param text is the text that we want to set inside this TextBox.
     * @return this TextBox which will contain the specified text.
     */
    public TextBox text(String text) {
        this.text = new StringBuilder(text.replaceAll("\n", " \n") + " ");

        calculateSize();

        return this;
    }

    /**
     * @return this TextBox which now is hiding the edit cursor.
     */
    public TextBox hideCursor() {
        showCursor = false;
        cursorLine = 0;
        cursorColumn = 0;

        return this;
    }

    /**
     * @return this TextBox which is now not focusable.
     */
    public TextBox unfocusable() {
        focusable = false;
        onFocus = false;

        return this;
    }

    /**
     * @return this TextBox which now displays {@link PrimitiveSymbol#STAR} instead of the actual text characters.
     * It is useful for password fields.
     */
    public TextBox hideText() {
        textHidden = true;

        return this;
    }

    /**
     * @param color is the desired {@link Color} for the text inside this TextBox.
     * @return this TextBox which now displays text with the specified color.
     */
    public TextBox color(Color color) {
        this.color = color;

        return this;
    }

    /**
     * @return this TextBox which now accepts new lines and spaces that the user can type while editing.
     */
    public TextBox acceptsNewLinesAndSpaces() {
        acceptsNewLinesAndSpaces = true;

        return this;
    }

    /**
     * @return this TextBox which now displays text in bold.
     */
    public TextBox bold() {
        bold = true;

        return this;
    }
}
