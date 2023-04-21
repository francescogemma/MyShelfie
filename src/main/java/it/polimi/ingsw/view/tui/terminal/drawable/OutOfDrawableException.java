package it.polimi.ingsw.view.tui.terminal.drawable;

public class OutOfDrawableException extends RuntimeException {
    public OutOfDrawableException(DrawableSize size, Coordinate coordinate) {
        super(coordinate + " is out of " + size);
    }
}
