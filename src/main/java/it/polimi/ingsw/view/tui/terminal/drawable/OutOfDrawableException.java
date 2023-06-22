package it.polimi.ingsw.view.tui.terminal.drawable;

/**
 * Exception thrown when we try to get the symbol in a {@link Drawable} at a {@link Coordinate} outside from it.
 *
 * @author Cristiano Migali
 */
public class OutOfDrawableException extends RuntimeException {
    /**
     * Constructor of the class.
     * Sets the exception message.
     *
     * @param size is the {@link DrawableSize} of the Drawable.
     * @param coordinate is the {@link Coordinate} outside of the Drawable.
     */
    public OutOfDrawableException(DrawableSize size, Coordinate coordinate) {
        super(coordinate + " is out of " + size);
    }
}
