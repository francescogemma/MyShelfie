package it.polimi.ingsw.view.tui.terminal.drawable;

import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Symbol;

import java.util.Optional;

/**
 * Represents a Drawable which is obtained by combining other kinds of Drawable in a specified layout
 * (for example with an {@link it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.OrientedLayout}).
 *
 * @param <T> is the type of underlying Drawable at the root of the layout.
 *
 * @author Cristiano Migali
 */
public abstract class FixedLayoutDrawable<T extends Drawable> extends Drawable {
    /**
     * It is the Drawable of the layout.
     */
    private T layout;

    /**
     * Allows to specify the layout for this FixedLayoutDrawable.
     * The layout can be set only once.
     *
     * @param layout is the layout of this FixedLayoutDrawable.
     *
     * @throws IllegalStateException if you try to set a layout more the once.
     */
    protected void setLayout(T layout) {
        if (this.layout != null) {
            throw new IllegalStateException("You can set the layout of a fixed layout drawable only once");
        }

        this.layout = layout;
    }

    /**
     * @return the underlying layout of this FixedLayoutDrawable.
     */
    public T getLayout() {
        return layout;
    }

    @Override
    public void askForSize(DrawableSize desiredSize) {
        if (layout == null) {
            throw new IllegalStateException("The layout of a fixed layout drawable must be set before invoking askForSize");
        }

        layout.askForSize(desiredSize);

        size = layout.getSize();
    }

    @Override
    public Symbol getSymbolAt(Coordinate coordinate) {
        if (layout == null) {
            throw new IllegalStateException("The layout of a fixed layout drawable must be set before invoking getSymbolAt");
        }

        return layout.getSymbolAt(coordinate);
    }

    @Override
    public boolean handleInput(String key) {
        if (layout == null) {
            throw new IllegalStateException("The layout of a fixed layout drawable must be set before invoking handleInput");
        }

        return layout.handleInput(key);
    }

    @Override
    public boolean focus(Coordinate desiredCoordinate) {
        return layout.focus(desiredCoordinate);
    }

    @Override
    public void unfocus() {
        layout.unfocus();
    }

    @Override
    public Optional<Coordinate> getFocusedCoordinate() {
        return layout.getFocusedCoordinate();
    }
}
