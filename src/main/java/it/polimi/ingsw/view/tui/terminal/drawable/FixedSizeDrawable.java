package it.polimi.ingsw.view.tui.terminal.drawable;

import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Symbol;

import java.util.Optional;

/**
 * Represents a Drawable which has a fixed size (number of lines and columns occupied on the terminal screen).
 * It is realized by ignoring the requests of resize (through {@link Drawable#askForSize(DrawableSize)}) to an underlying
 * FullyResizableDrawable after it has been resized to the desired size.
 *
 * @author Cristiano Migali
 */
public class FixedSizeDrawable extends Drawable {
    /**
     * It is the underlying FullyResizableDrawable which won't receive resize requests (through {@link Drawable#askForSize(DrawableSize)})
     * anymore.
     */
    private final FullyResizableDrawable toFixSize;

    /**
     * Constructor of the class.
     * It initializes the underlying FullyResizableDrawable whose size will be fixed.
     *
     * @param toFixSize is the underlying Drawable whose size will be fixed.
     * @param size is the size the underlying FullyResizableDrawable will be resized to.
     */
    public FixedSizeDrawable(FullyResizableDrawable toFixSize, DrawableSize size) {
        this.toFixSize = toFixSize;
        this.size = size;
    }

    @Override
    public void askForSize(DrawableSize desiredSize) {
        toFixSize.askForSize(size);
    }

    @Override
    public Symbol getSymbolAt(Coordinate coordinate) {
        return toFixSize.getSymbolAt(coordinate);
    }

    @Override
    public boolean handleInput(String key) {
        return toFixSize.handleInput(key);
    }

    @Override
    public boolean focus(Coordinate desiredCoordinate) {
        return toFixSize.focus(desiredCoordinate);
    }

    @Override
    public void unfocus() {
        toFixSize.unfocus();
    }

    @Override
    public Optional<Coordinate> getFocusedCoordinate() {
        return toFixSize.getFocusedCoordinate();
    }
}
