package it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout;

import it.polimi.ingsw.view.tui.terminal.drawable.*;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Symbol;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents a FullyResizableDrawable obtained by stacking an ordered set of FullyResizableDrawable with a given
 * {@link Orientation}
 * (horizontal or vertical). Each FullyResizableDrawable has a weight which is proportional to the size dimension in the
 * layout orientation that it occupies. For example if the layout has a vertical orientation, its children have
 * height proportional to their weight and fill all the width of the layout.
 * Since its children are FullyResizableDrawables proportions are guaranteed to hold (conversely to what happens with
 * {@link OrientedLayout}), furthermore the whole layout is a FullyResizableDrawable.
 *
 * @author Cristiano Migali
 */
public class FullyResizableOrientedLayout extends FullyResizableDrawable {
    /**
     * Underlying {@link OrientedLayout} whose children are FullyResizableDrawables.
     */
    private final OrientedLayout layout;

    /**
     * Constructor of the class.
     * Initializes the layout with the given orientation (vertical or horizontal) and children.
     *
     * @param orientation is the {@link Orientation} of the layout; it can be vertical or horizontal.
     * @param elements is the ordered set of children of the layout. Each one is a FullyResizableDrawable
     *                 plus a weight proportional to the space that it occupies in the layout.
     */
    public FullyResizableOrientedLayout(Orientation orientation,
                                               FullyResizableOrientedLayoutElement... elements) {
        layout = new OrientedLayout(orientation, elements);
    }

    @Override
    public void askForSize(DrawableSize desiredSize) {
        super.askForSize(desiredSize);

        layout.askForSize(desiredSize);
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
     * @return the list of children in this layout.
     */
    public List<FullyResizableOrientedLayout> getElements() {
        return new ArrayList<>((List) layout.getElements());
    }

    /**
     * Adds a new child at the end (at the bottom if the layout orientation is vertical, to the right
     * if the layout orientation is horizontal) of the layout.
     *
     * @param element is the FullyResizableDrawable with its associated weight to be added at the
     *                end of the layout.
     */
    public void addElement(FullyResizableOrientedLayoutElement element) {
        layout.addElement(element);
    }
}
