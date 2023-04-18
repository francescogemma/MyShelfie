package it.polimi.ingsw.model.board;

import it.polimi.ingsw.utils.Coordinate;

import java.util.ArrayList;
import java.util.List;

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
     * The function checks in all the possible previous selections
     * and returns whether the coordinate has already been selected.
     * @param c the point to check
     * @return true iff c is already selected
     * */
    protected boolean contains(Coordinate c) {
        return this.selected.contains(c);
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
