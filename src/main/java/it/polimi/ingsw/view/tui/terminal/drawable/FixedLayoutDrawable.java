package it.polimi.ingsw.view.tui.terminal.drawable;

import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Symbol;

import java.util.Optional;

public abstract class FixedLayoutDrawable<T extends Drawable> extends Drawable {
    private Drawable layout;

    protected void setLayout(T layout) {
        this.layout = layout;
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
