package it.polimi.ingsw.model.board;

import it.polimi.ingsw.utils.Coordinate;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

/**
 * @author Giacomo Groppi
 * */
class BoardSelectorView {
    protected List<Coordinate> selected;

    public BoardSelectorView(List<Coordinate> selected) {
        this.selected = selected;
    }

    public BoardSelectorView() {
        this.selected = new ArrayList<>();
    }

    public List<Coordinate> getSelected () {
        return new ArrayList<>(selected);
    }

    /**
     * @return The number of points selected so far.
     * */
    protected int selectionSize() {
        return this.selected.size();
    }

    /*
     * we assume the extraction is legal from now
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
