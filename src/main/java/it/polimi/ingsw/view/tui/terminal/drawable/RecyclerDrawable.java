package it.polimi.ingsw.view.tui.terminal.drawable;

import it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.OrientedLayout;
import it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.OrientedLayoutElement;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Symbol;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class RecyclerDrawable<D extends Drawable, T> extends Drawable {
    private final Supplier<D> drawableFactory;
    private static final int INITIAL_DRAWABLES = 10;
    private final OrientedLayout layout;

    public RecyclerDrawable(Orientation orientation, Supplier<D> drawableFactory) {
        this.drawableFactory = drawableFactory;

        OrientedLayoutElement[] initialElements = new OrientedLayoutElement[INITIAL_DRAWABLES];

        for (int i = 0; i < INITIAL_DRAWABLES; i++) {
            initialElements[i] = drawableFactory.get().weight(0);
        }

        layout = new OrientedLayout(
            orientation,
            initialElements
        );
    }

    @Override
    public void askForSize(DrawableSize desiredSize) {
        layout.askForSize(desiredSize);

        size = layout.getSize();
    }

    @Override
    public Symbol getSymbolAt(Coordinate coordinate) {
        return layout.getSymbolAt(coordinate);
    }

    @Override
    public boolean handleInput(String key) {
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

    public RecyclerDrawable populate(List<T> contents, BiConsumer<D, T> fillDrawable) {
        for (int i = 0; i < contents.size(); i++) {
            if (i >= layout.getElements().size()) {
                layout.addElement(drawableFactory.get().weight(0));
            }

            fillDrawable.accept((D) layout.getElements().get(i).getDrawable(), contents.get(i));
            layout.getElements().get(i).setWeight(1);
        }

        for (int i = contents.size(); i < layout.getElements().size(); i++) {
            layout.getElements().get(i).setWeight(0);
        }

        return this;
    }
}
