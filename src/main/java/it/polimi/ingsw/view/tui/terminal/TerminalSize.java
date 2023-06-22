package it.polimi.ingsw.view.tui.terminal;

import it.polimi.ingsw.view.tui.terminal.drawable.DrawableSize;

/**
 * Represents the size of a terminal window by the number of lines and columns.
 * This class is immutable.
 */
public class TerminalSize {
    /**
     * It is the number of lines of the terminal screen.
     */
    public final int lines;

    /**
     * It is the number of columns of the terminal screen.
     */
    public final int columns;

    /**
     * Constructor of the class.
     * Initializes the number of lines and columns of the terminal screen.
     *
     * @param lines is the number of lines of the terminal screen.
     * @param columns is the number of columns of the terminal screen.
     * @throws IllegalArgumentException iff the number of lines or columns is negative.
     */
    public TerminalSize(int lines, int columns) {
        if (lines < 0) {
            throw new IllegalArgumentException("Terminal lines must be greater than or equal to zero");
        }

        if (columns < 0) {
            throw new IllegalArgumentException("Terminal columns must be greater than or equal to zero");
        }

        this.lines = lines;
        this.columns = columns;
    }

    @Override
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

    /**
     * @return a {@link DrawableSize} with the same number of lines and columns as this TerminalSize.
     */
    public DrawableSize toDrawableSize() {
        return new DrawableSize(lines, columns);
    }
}
