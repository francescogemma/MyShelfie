package it.polimi.ingsw.model.board;

import it.polimi.ingsw.utils.Coordinate;
import it.polimi.ingsw.utils.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static java.lang.Math.*;

/**
 * Manager for Tile selection
 * @author Giacomo Groppi
 */
class BoardSelector extends BoardSelectorView {
    BoardSelector () {
        selected = new ArrayList<>();
    }

    BoardSelector(BoardSelector other) {
        selected = other.selected.stream().toList();
    }

    /**
     * @throws RuntimeException if sizeSelection() != 2
     * @return true if the extraction is Vertical
     */
    protected boolean isVerticalExtraction () {
        assert selectionSize() == 2;

        final int x0 = selected.get(0).getCol();
        final int x1 = selected.get(1).getCol();

        return x0 == x1;
    }

    /**
     * The function returns the set of coordinates that can be selected for extraction,
     * taking into account the previous extractions.
     * @return List of legal Coordinates for selection.
     * */
    protected List<List<Coordinate>> getAvailableSelection() {
        List<List<Coordinate>> res = new ArrayList<>();
        switch (selectionSize()) {
            case 0 -> {
                assert false: "Can't call this function if sizeSelection() is equals to 0";
            }
            case 1 -> {
                final int r = this.selected.get(0).getRow();
                final int c = this.selected.get(0).getCol();

                res.add(new ArrayList<>(Arrays.asList(
                        new Coordinate(r, c + 1),
                        new Coordinate(r, c + 2)
                )));

                res.add(new ArrayList<>(Arrays.asList(
                        new Coordinate(r, c - 1),
                        new Coordinate(r, c - 2)
                )));

                res.add(new ArrayList<>(Arrays.asList(
                        new Coordinate(r + 1, c),
                        new Coordinate(r + 2, c)
                )));

                res.add(new ArrayList<>(Arrays.asList(
                        new Coordinate(r - 1, c),
                        new Coordinate(r - 2, c)
                )));
            }
            case 2 -> {
                if (distanceFromTwoSelectedTile() == 1) {
                    if (isVerticalExtraction()) {
                        final int col = this.selected.get(0).getCol();
                        final int biggerRow =   max(selected.get(0).getRow(), selected.get(1).getRow());
                        final int smallerRow =  min(selected.get(0).getRow(), selected.get(1).getRow());

                        res.add(List.of(new Coordinate(smallerRow - 1, col)));
                        res.add(List.of(new Coordinate(biggerRow + 1, col)));
                    } else {
                        final int row = this.selected.get(0).getRow();
                        final int biggerCol =   max(selected.get(0).getCol(), selected.get(1).getCol());
                        final int smallerCol =  min(selected.get(0).getCol(), selected.get(1).getCol());

                        res.add(List.of(new Coordinate(row, smallerCol - 1)));
                        res.add(List.of(new Coordinate(row, biggerCol  + 1)));
                    }
                } else {
                    if (isVerticalExtraction()) {
                        final int col = this.selected.get(0).getCol();
                        final int biggerRow = max(selected.get(0).getRow(), selected.get(1).getRow());
                        res.add(List.of(new Coordinate(biggerRow - 1, col)));
                    } else {
                        final int row = selected.get(0).getRow();
                        final int biggerCol = max(selected.get(0).getCol(), selected.get(1).getCol());
                        res.add(List.of(new Coordinate(row, biggerCol - 1 )));
                    }
                }
            }
            default -> res = new ArrayList<>();
        }
        return res;
    }

    public List<Coordinate> draw () throws IllegalExtractionException {
        if (selectionSize() == 0)
            throw new IllegalExtractionException("Tiles selected is empty");

        if (!canDraw()) {
            throw new IllegalExtractionException("You can't extract tiles in this order");
        }

        return new ArrayList<>(selected);
    }

    /**
     * The function checks that wrong positions are not selected, but it does not check whether the cell can actually be selected, i.e., whether it has a free side.
     * @throws IllegalExtractionException if the position of the Tile is along the diagonal
     * @throws IllegalArgumentException if we are trying to extract more than 3 tiles
     * @throws IllegalArgumentException if [r, c] is already in the list
     */
    public void select (Coordinate c) throws IllegalExtractionException, FullSelectionException {
        if (selectionSize() > 2) {
            throw new FullSelectionException();
        }

        if (contains(c)) {
            throw new IllegalExtractionException("The specified coordinate already exists");
        }

        if (selectionSize() != 0 &&
                getAvailableSelection()
                        .stream()
                        .flatMap(Collection::stream)
                        .noneMatch(p -> p.equals(c)))
        {
            throw new IllegalExtractionException();
        }

        this.selected.add(c);
    }

    /**
     * The function returns the set of Tiles selected up to this point.
     * The function does not remove the Tiles from the Board.
     * @return it returns a new List with all the selected tiles
     */
    public final List<Coordinate> getSelected() {
        return new ArrayList<>(this.selected);
    }

    public Coordinate lastSelected() {
        if (this.selected.isEmpty())
            throw new IllegalArgumentException("List is empty");
        return this.selected.get(selected.size() - 1);
    }

    // TODO: javadoc
    public void forgetLastSelected() {
        this.selected.remove(selected.size() - 1);
    }

    @Override
    public boolean equals (Object object) {
        if (object == this)
            return true;

        return super.equals(object);
    }
}
