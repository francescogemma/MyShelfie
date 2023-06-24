package it.polimi.ingsw.model.board;

import it.polimi.ingsw.utils.Coordinate;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

/**
 * @author Giacomo Groppi
 * */
class BoardSelectorView {
    /**
     * It is the list of selected {@link Coordinate coordinates}.
     */
    protected List<Coordinate> selected;

    /**
     * Constructor of {@link BoardSelectorView}.
     * */
    public BoardSelectorView() {
        this.selected = new ArrayList<>();
    }

    /**
     * @return A list of coordinates in the order of selection of the selected coordinates up to this point.
     * */
    public List<Coordinate> getSelected () {
        return new ArrayList<>(selected);
    }

    /**
     * @return The number of points selected so far.
     * */
    protected int selectionSize() {
        return this.selected.size();
    }

    /**
     * The method requires that the extraction is legal and that {@link BoardSelectorView#selected} is equal to 2.
     *
     * @return If the extraction is vertical, the method returns the distance between the rows of the two selected
     *  coordinates. If the extraction is horizontal, it returns the horizontal distance between the two selected
     *  coordinates.
     * */
    protected int distanceFromTwoSelectedTile () {
        assert selected.size() == 2;
        final Coordinate c1 = selected.get(0);
        final Coordinate c2 = selected.get(1);

        if (c1.getCol() == c2.getCol()) {
            return abs(c1.getRow() - c2.getRow());
        } else {
            return abs(c1.getCol() - c2.getCol());
        }
    }

    /**
     * The function checks in all the possible previous selections
     * and returns whether the coordinate has already been selected.
     * @param c the point to check
     * @return true iff c is already selected
     * */
    protected boolean contains(Coordinate c) {
        return this.selected.contains(c);
    }

    /**
     * Use this method to understand if the current selection can be drawn
     *
     * @return true iff the selected tiles can be drawn from the board.
     * */
    public boolean canDraw () {
        if (selectionSize() == 0)
            return false;

        if (selectionSize() == 2 && distanceFromTwoSelectedTile() > 1) {
            return false;
        }

        return true;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this)
            return true;

        if (other == null || this.getClass() != other.getClass())
            return false;

        return (((BoardSelectorView) other).selected.equals(this.selected));
    }
}
