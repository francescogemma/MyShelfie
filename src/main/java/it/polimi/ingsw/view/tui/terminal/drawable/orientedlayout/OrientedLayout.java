package it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout;

import it.polimi.ingsw.view.tui.terminal.Terminal;
import it.polimi.ingsw.view.tui.terminal.drawable.*;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.PrimitiveSymbol;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Symbol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Represents a Drawable obtained by stacking an ordered set of children Drawables with a given {@link Orientation}
 * (horizontal or vertical). Each child Drawable has a weight which is proportional to the size dimension
 * in the layout orientation that it should occupy. For example if the layout has a vertical orientation, its children
 * should have height proportional to their weight, ideally filling all the width of the layout.
 * Note that the children of an OrientedLayout are {@link Drawable}, not {@link FullyResizableDrawable},
 * so there is no guarantee that a certain child will occupy an amount of space proportional to its weight.
 * In particular the layout tries to resize its children according to their weights through {@link Drawable#askForSize(DrawableSize)};
 * then it retrieves the actual size of the child after the resize, computes the remaining space and tries to split
 * it among the remaining children still according to their weights.
 * The order of resize is: the first child in the set (which is on top if the orientation is vertical or on the
 * left if the orientation is horizontal); the last child in the set (which is at the bottom if the orientation
 * is vertical or on the right if the orientation is horizontal) and then all the others
 * children from the second to the second last.
 *
 * @see OrientedLayoutElement
 *
 * @author Cristiano Migali
 */
public class OrientedLayout extends Drawable {
    /**
     * It is the orientation of the layout: vertical or horizontal.
     */
    private final Orientation orientation;

    /**
     * It is the list of children Drawables with their weights in the layout.
     */
    private final List<OrientedLayoutElement> elements;

    /**
     * It is a list of {@link Coordinate} components "parallel" to the orientation of the layout
     * (that is line component if the orientation is vertical, column component if the orientation is horizontal).
     * Every component corresponds to the upper left {@link Coordinate} of the ith children in the layout.
     * There is also one more component at the end for a fictional starting "at the end" of the layout which
     * simplifies the resizing code.
     */
    private final List<Integer> elementsOriginParallelComponent;

    /**
     * It is the index corresponding to the child of the layout which is on focus or -1 if no child is on focus.
     */
    private int elementOnFocusIndex = -1;

    /**
     * Constructor of the class.
     * Initializes a layout with the given orientation and set of children.
     *
     * @param orientation is the {@link Orientation} (vertical or horizontal) of the layout by which its children will
     *                    be stacked.
     * @param elements is the ordered set of children of the layout, each one is a Drawable with its weight proportional to
     *                 the occupied space in the layout.
     */
    public OrientedLayout(Orientation orientation, OrientedLayoutElement... elements) {
        this.orientation = orientation;
        this.elements = new ArrayList<>(Arrays.asList(elements));
        elementsOriginParallelComponent = new ArrayList<>(elements.length + 1);
        for (int i = 0; i < elements.length + 1; i++) {
            elementsOriginParallelComponent.add(0);
        }
    }

    @Override
    public void askForSize(DrawableSize desiredSize) {
        int remainingParallelSizeComponent = desiredSize.getParallelSizeComponent(orientation);
        int maxPerpendicularSizeComponent = 0;

        /* Calculating the origin parallel component for the ith element
         * and resizing all elements.
         * We set also the origin parallel component for an additional fictional element at the end, which starts
         * in the coordinate just after the layout.
         *
         * The first element always starts at (1, 1), hence the origin parallel component is 1 (whatever the orientation).
         */
        elementsOriginParallelComponent.set(0, 1);

        int totalWeight = 0;
        for (OrientedLayoutElement element : elements) {
            totalWeight += element.getWeight();
        }
        int actualLastElementParallelSizeComponent = 0;
        if (totalWeight != 0 && elements.get(elements.size() - 1).getWeight() > 0) {
            int desiredLastElementParallelSizeComponent = remainingParallelSizeComponent *
                elements.get(elements.size() - 1).getWeight() / totalWeight;

            elements.get(elements.size() - 1).getDrawable().askForSize(DrawableSize.craftSizeByOrientation(orientation,
                desiredLastElementParallelSizeComponent,
                desiredSize.getPerpendicularSizeComponent(orientation)));

            actualLastElementParallelSizeComponent = elements.get(elements.size() - 1).getDrawable()
                .getSize().getParallelSizeComponent(orientation);

            maxPerpendicularSizeComponent = elements.get(elements.size() - 1).getDrawable()
                .getSize().getPerpendicularSizeComponent(orientation);
        }

        remainingParallelSizeComponent -= actualLastElementParallelSizeComponent;
        if (remainingParallelSizeComponent < 0) {
            remainingParallelSizeComponent = 0;
        }

        for (int i = 0; i < elements.size() - 1; i++) {
            if (elements.get(i).getWeight() == 0) {
                elementsOriginParallelComponent.set(i + 1, elementsOriginParallelComponent.get(i));

                continue;
            }

            int remainingWeight = 0;
            // We do not consider the last element since we have already assigned its size.
            for (int j = i; j < elements.size() - 1; j++) {
                remainingWeight += elements.get(j).getWeight();
            }

            int desiredElementParallelSizeComponent = 0;

            if (remainingWeight != 0) {
                desiredElementParallelSizeComponent = remainingParallelSizeComponent * elements.get(i).getWeight() /
                    remainingWeight;
            }

            elements.get(i).getDrawable().askForSize(DrawableSize.craftSizeByOrientation(orientation,
                desiredElementParallelSizeComponent,
                desiredSize.getPerpendicularSizeComponent(orientation)
            ));

            DrawableSize actualSize = elements.get(i).getDrawable().getSize();

            remainingParallelSizeComponent -= actualSize.getParallelSizeComponent(orientation);
            if (remainingParallelSizeComponent < 0) {
                remainingParallelSizeComponent = 0;
            }

            maxPerpendicularSizeComponent = Math.max(maxPerpendicularSizeComponent,
                actualSize.getPerpendicularSizeComponent(orientation));

            elementsOriginParallelComponent.set(i + 1, elementsOriginParallelComponent.get(i) +
                actualSize.getParallelSizeComponent(orientation));
        }
        elementsOriginParallelComponent.set(elements.size(), elementsOriginParallelComponent.get(elements.size() - 1) +
            actualLastElementParallelSizeComponent);

        size = DrawableSize.craftSizeByOrientation(orientation, elementsOriginParallelComponent
            .get(elementsOriginParallelComponent.size() - 1) - 1, maxPerpendicularSizeComponent);
    }

    @Override
    public Symbol getSymbolAt(Coordinate coordinate) {
        if (!size.isInside(coordinate)) {
            throw new OutOfDrawableException(size, coordinate);
        }

        for (int i = 0; i < elements.size(); i++) {
            if (elementsOriginParallelComponent.get(i) <= coordinate.getParallelComponent(orientation) &&
                coordinate.getParallelComponent(orientation) < elementsOriginParallelComponent.get(i + 1)) {

                if (coordinate.getPerpendicularComponent(orientation) > elements.get(i)
                    .getDrawable().getSize().getPerpendicularSizeComponent(orientation)) {
                    return PrimitiveSymbol.EMPTY;
                }

                return elements.get(i).getDrawable().getSymbolAt(coordinate.changeOrigin(
                   Coordinate.craftCoordinateByOrientation(
                       orientation,
                       elementsOriginParallelComponent.get(i),
                       1)
                ));
            }
        }

        throw new IllegalStateException("There must be an element corresponding to the requested coordinate");
    }

    /**
     * Tries to focus the specified child of the layout at a desired Coordinate.
     *
     * @param elementToFocusIndex is the index of the child of the layout that should be focused; it cal also be
     *                            -1 or equal to the number of elements, in that case there is no corresponding
     *                            child to focus.
     * @param desiredCoordinate is the desired {@link Coordinate} where we want to focus the specified child.
     * @return true iff the specified child has been focused.
     */
    private boolean focusElement(int elementToFocusIndex, Coordinate desiredCoordinate) {
        if (elementToFocusIndex < 0 || elementToFocusIndex >= elements.size()
            || elements.get(elementToFocusIndex).getWeight() == 0) {
            return false;
        }

        return elements.get(elementToFocusIndex).getDrawable().focus(desiredCoordinate);
    }

    /**
     * Tries to focus the child of the layout which is in the specified direction with respect to the current
     * child in focus.
     *
     * @param direction indicates the direction of the child to focus with respect to the focused one; it can be
     *                  +1 (which corresponds to down if orientation is vertical or right if orientation is horizontal)
     *                  or -1 (which corresponds to up if orientation is vertical or left if orientation is horizontal).
     * @param desiredCoordinate is the desired {@link Coordinate} where we want to focus the next child.
     * @return true iff the child in the specified direction has been focused.
     *
     * @throws IllegalArgumentException iff direction is not +1 or -1.
     */
    private boolean focusNextElement(int direction, Coordinate desiredCoordinate) {
        if (direction != 1 && direction != -1) {
            throw new IllegalArgumentException("When changing element on focus, direction must be 1 or -1, got: " +
                direction);
        }

        int nextElementOnFocusIndex = elementOnFocusIndex + direction;

        for (; 0 <= nextElementOnFocusIndex && nextElementOnFocusIndex < elements.size();
             nextElementOnFocusIndex += direction) {

            if (focusElement(nextElementOnFocusIndex, desiredCoordinate)) {

                elements.get(elementOnFocusIndex).getDrawable().unfocus();
                elementOnFocusIndex = nextElementOnFocusIndex;

                return true;
            }
        }

        return false;
    }

    /**
     * Tries to focus the child of the layout which is in the specified direction with respect to the current child
     * in focus. In particular the desired focus {@link Coordinate} is the one, inside the
     * next child, closest to the previous focus {@link Coordinate}.
     *
     * @param direction indicates the direction of the child to focus with respect to the focused one; it can be
     *                  +1 (which corresponds to down if orientation is vertical or right if orientation is horizontal)
     *                  or -1 (which corresponds to up if orientation is vertical or left if orientation is horizontal).
     * @return true iff the child in the specified direction has been focused.
     *
     * @throws IllegalArgumentException iff direction is not +1 or -1.
     */
    private boolean focusNextElement(int direction) {
        int previousElementWithNonNullWeightIndex = -1;

        for (int i = elementOnFocusIndex - 1; i >= 0; i--) {
            if (elements.get(i).getWeight() > 0) {
                previousElementWithNonNullWeightIndex = i;
                break;
            }
        }

        int desiredParallelComponent = (direction == 1 || previousElementWithNonNullWeightIndex == -1) ? 1
            : Math.max(elements.get(previousElementWithNonNullWeightIndex)
                .getDrawable().getSize().getParallelSizeComponent(orientation), 1);

        return focusNextElement(direction, elements.get(elementOnFocusIndex).getDrawable().getFocusedCoordinate()
            .map(coordinate -> Coordinate.craftCoordinateByOrientation(orientation,
                desiredParallelComponent,
                coordinate.getPerpendicularComponent(orientation)
            ))
            .orElse(Coordinate.origin()));
    }

    @Override
    public boolean handleInput(String key) {
        if (elements.get(elementOnFocusIndex).getDrawable().handleInput(key)) {
            return true;
        }

        if ((key.equals(Terminal.UP_ARROW) || key.equals(Terminal.DOWN_ARROW) || key.equals("w")
            || key.equals("s")) && orientation == Orientation.HORIZONTAL) {
            return false;
        }

        if ((key.equals(Terminal.RIGHT_ARROW) || key.equals(Terminal.LEFT_ARROW) || key.equals("d")
            || key.equals("a")) && orientation == Orientation.VERTICAL) {
            return false;
        }

        return switch (key) {
            case Terminal.UP_ARROW, Terminal.LEFT_ARROW, "w", "a" -> focusNextElement(-1);

            case Terminal.DOWN_ARROW, Terminal.RIGHT_ARROW, "s", "d" -> focusNextElement(1);

            case "\t" -> focusNextElement(1, Coordinate.origin());

            default -> false;
        };
    }

    @Override
    public boolean focus(Coordinate desiredCoordinate) {
        int candidateElementOnFocusIndex = -1;
        Optional<Coordinate> inCandidateElementDesiredCoordinate = Optional.empty();

        for (int i = 0; i < elements.size(); i++) {
            if (elementsOriginParallelComponent.get(i) <= desiredCoordinate.getParallelComponent(orientation) &&
                desiredCoordinate.getParallelComponent(orientation) < elementsOriginParallelComponent.get(i + 1)) {
                candidateElementOnFocusIndex = i;

                inCandidateElementDesiredCoordinate = Optional.of(desiredCoordinate.changeOrigin(
                    Coordinate.craftCoordinateByOrientation(orientation,
                        elementsOriginParallelComponent.get(i),
                        1
                    )
                ));

                if (focusElement(i, inCandidateElementDesiredCoordinate.get())) {
                    elementOnFocusIndex = candidateElementOnFocusIndex;

                    return true;
                }
            }
        }

        for (int d = 1; candidateElementOnFocusIndex - d >= 0 ||
            candidateElementOnFocusIndex + d < elements.size(); d++) {

            if (focusElement(candidateElementOnFocusIndex - d, inCandidateElementDesiredCoordinate
                .orElse(Coordinate.origin()))) {

                elementOnFocusIndex = candidateElementOnFocusIndex - d;
                return true;
            }

            if (focusElement(candidateElementOnFocusIndex + d, inCandidateElementDesiredCoordinate
                .orElse(Coordinate.origin()))) {

                elementOnFocusIndex = candidateElementOnFocusIndex + d;
                return true;
            }
        }

        return false;
    }

    @Override
    public void unfocus() {
        if (elementOnFocusIndex != -1) {
            elements.get(elementOnFocusIndex).getDrawable().unfocus();
            elementOnFocusIndex = -1;
        }
    }

    @Override
    public Optional<Coordinate> getFocusedCoordinate() {
        if (elementOnFocusIndex == -1) {
            return Optional.empty();
        }

        return elements.get(elementOnFocusIndex).getDrawable().getFocusedCoordinate().map(coordinate ->
            Coordinate.craftCoordinateByOrientation(orientation,
                coordinate.getParallelComponent(orientation) +
                    elementsOriginParallelComponent.get(elementOnFocusIndex) - 1,
                coordinate.getPerpendicularComponent(orientation)
        ));
    }

    /**
     * @return the list of children of the layout.
     */
    public List<OrientedLayoutElement> getElements() {
        return new ArrayList<>(elements);
    }

    /**
     * Adds an element at the end of the layout (which is at the bottom if the orientation is vertical, on the right
     * if the orientation is horizontal).
     *
     * @param element is the {@link OrientedLayoutElement}, that is a Drawable with a weight, to be added at the end
     *                of the layout.
     */
    public void addElement(OrientedLayoutElement element) {
        elements.add(element);
        elementsOriginParallelComponent.add(0);
    }
}
