package it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout;

import it.polimi.ingsw.view.tui.terminal.Terminal;
import it.polimi.ingsw.view.tui.terminal.drawable.*;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.PrimitiveSymbol;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Symbol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class OrientedLayout extends Drawable {
    private final Orientation orientation;
    private final List<OrientedLayoutElement> elements;
    private final List<Integer> elementsOriginParallelComponent;

    private int elementOnFocusIndex = -1;

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

    private boolean focusElement(int elementToFocusIndex, Coordinate desiredCoordinate) {
        if (elementToFocusIndex < 0 || elementToFocusIndex >= elements.size()
            || elements.get(elementToFocusIndex).getWeight() == 0) {
            return false;
        }

        return elements.get(elementToFocusIndex).getDrawable().focus(desiredCoordinate);
    }

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

    private boolean focusNextElement(int direction) {
        if (elementOnFocusIndex == -1 || elements.get(elementOnFocusIndex).getDrawable().getFocusedCoordinate().isEmpty()) {

            throw new IllegalStateException("An oriented layout can handle input only when it has at least one focusable" +
                " element and is on focus");
        }

        return focusNextElement(direction, elements.get(elementOnFocusIndex).getDrawable().getFocusedCoordinate().get());
    }

    @Override
    public boolean handleInput(String key) {
        if (elementOnFocusIndex == -1) {
            return false;
        }

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
        if (elementOnFocusIndex == -1 || elements.get(elementOnFocusIndex).getDrawable()
            .getFocusedCoordinate().isEmpty()) {
            throw new IllegalStateException("You can't unfocus an oriented layout which hasn't any element in focus");
        }

        elements.get(elementOnFocusIndex).getDrawable().unfocus();

        elementOnFocusIndex = -1;
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

    public List<OrientedLayoutElement> getElements() {
        return new ArrayList<>(elements);
    }

    public void addElement(OrientedLayoutElement element) {
        elements.add(element);
        elementsOriginParallelComponent.add(0);
    }
}
