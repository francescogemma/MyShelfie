package it.polimi.ingsw.view.tui.terminal;

import it.polimi.ingsw.view.tui.terminal.drawable.DrawableSize;

/**
 * Represents the size of a terminal window by the number of lines and columns.
 */
public class TerminalSize {
    public final int lines;
    public final int columns;

    public TerminalSize(int lines, int columns) throws IllegalArgumentException {
        if (lines < 0) {
            throw new IllegalArgumentException("Terminal lines must be greater than or equal to zero");
        }

        if (columns < 0) {
            throw new IllegalArgumentException("Terminal columns must be greater than or equal to zero");
        }

        this.lines = lines;
        this.columns = columns;
    }

    public String toString() {
        return "[" + lines + ", " + columns + "]";
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }

        if (!(other instanceof TerminalSize)) {
            return false;
        }

        TerminalSize otherSize = (TerminalSize) other;
        return lines == otherSize.lines && columns == otherSize.columns;
    }

    public DrawableSize toDrawableSize() {
        return new DrawableSize(lines, columns);
    }
}
