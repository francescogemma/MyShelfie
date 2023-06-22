package it.polimi.ingsw.view.tui.terminal.drawable;

import it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.OrientedLayout;
import it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.OrientedLayoutElement;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Symbol;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * It is a Drawable obtained by stacking with a certain {@link Orientation} a list of Drawables of the same kind.
 * The list can be populated dynamically by providing a list of data elements: after the process inside the list
 * there will be one Drawable for each provided data element, that Drawable will be populated with the corresponding
 * data. The user can specify the process of populating a certain Drawable with a certain data element.
 * The name RecyclerDrawable refers to the fact that on every re-population it does not destroy and reconstruct all
 * its Drawable but just repopulates the content of the ones that are already in the list and creates new ones if
 * needed.
 *
 * @param <D> is the type of the Drawables in the list.
 * @param <T> is the type of the data element used to populate the Drawables.
 */
public class RecyclerDrawable<D extends Drawable, T> extends Drawable {
    /**
     * Factory function which allows to create a Drawable to be added inside the list.
     */
    private final Supplier<D> drawableFactory;

    /**
     * Function which populates a Drawable in the list given a data element.
     */
    private final BiConsumer<D, T> fillDrawable;

    /**
     * It is the number of Drawables that gets created by default when the RecyclerDrawable is constructed.
     */
    private static final int INITIAL_DRAWABLES = 10;

    /**
     * The underlying OrientedLayout used to display the Drawables in a list.
     */
    private final OrientedLayout layout;

    /**
     * Constructor of the class.
     *
     * @param orientation is the orientation by which the Drawables in the list will be displayed.
     * @param drawableFactory is a factory function which allows to construct a Drawable which will be added to the list.
     * @param fillDrawable is a function which specifies how to populate a certain Drawable given a data element.
     */
    public RecyclerDrawable(Orientation orientation, Supplier<D> drawableFactory,
                            BiConsumer<D, T> fillDrawable) {
        this.drawableFactory = drawableFactory;
        this.fillDrawable = fillDrawable;

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

    /**
     * Populates the RecyclerDrawable with the given list of data elements.
     * At the end of the process in the list there will be one drawable for every element in contents, which
     * will be populated with the corresponding data through the function provided in the constructor.
     *
     * @param contents is the list of data elements used to populate the list.
     * @return this RecyclerDrawable after it has been populated with the provided data elements.
     */
    public RecyclerDrawable<D, T> populate(List<T> contents) {
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
