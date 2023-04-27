package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.tile.Tile;
import it.polimi.ingsw.utils.Coordinate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static it.polimi.ingsw.model.board.Board.BOARD_ROWS;
import static it.polimi.ingsw.model.board.Board.COLUMN_BOARDS;

/**
 * @author Giacomo Groppi
 * */
public class BoardView {
    protected BoardSelector boardSelector;
    protected int occupied;
    /**
     * tiles[i][j] will be null if the cell is empty
     */
    protected final Tile[][] tiles = new Tile[BOARD_ROWS][COLUMN_BOARDS];

    BoardView () {

    }

    BoardView(BoardView other) {
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                tiles[i][j] = other.tiles[i][j];
            }
        }
        this.occupied = other.occupied;
        this.boardSelector = new BoardSelector(other.boardSelector);
    }

    /**
     * @return the Tile in position [row, col]
     */
    public Tile tileAt(Coordinate c) {
        return this.tiles[c.getRow()][c.getCol()];
    }

    // TODO: Add testing for this new method
    /**
     * @return all board coordinates selected so far [sorted by selection].
     */
    public List<Coordinate> getSelectedCoordinates() {
        return this
            .boardSelector
            .getSelected();
    }

    /**
     * The function returns the set of tiles selected up to this point.
     * The function does not remove the objects from the board.
     * @return All Tiles selected so far [sorted by selection].
     */
    public List<Tile> getSelectedTiles() {
        return this
                .boardSelector
                .getSelected()
                .stream()
                .map(this::tileAt)
                .toList();
    }

    /**
     * @param c cell coordinate
     * @return true if there is no tile in position [row, column]
     */
    protected boolean isEmpty(Coordinate c) {
        return this.tileAt(c) == null;
    }

    protected int numberOfFreeSides(Coordinate c) {
        if (this.isOutOfBoard(c))
            throw new IllegalArgumentException("row or col out of bound: row: " + c.getRow() + " col: " + c.getCol());

        int free = 0;
        final boolean onBorder = this.hasEdgeOnBorder(c);
        if (onBorder)
            free++;

        if (c.getCol() + 1 < Board.COLUMN_BOARDS &&
                isEmpty(c.right())) {
            free++;
        }

        if (c.getRow() + 1 < Board.BOARD_ROWS &&
                isEmpty(c.down())) {
            free++;
        }

        if (c.getRow() != 0 &&
                isEmpty(c.top())) {
            free ++;
        }

        if (c.getCol() != 0 &&
                isEmpty(c.left())) {
            free++;
        }

        return free;
    }

    /**
     * @return return true if tiles[row][col] is on border
     * */
    protected boolean hasEdgeOnBorder(Coordinate c) {
        return c.getRow() + 1 == tiles.length || c.getCol() + 1== tiles.length ||
                c.getRow() == 0 || c.getCol() == 0;
    }

    /* TODO: Also cells selected after the first must have at least free side (at the beginning of the round),
     * that is they must have an adjacent cell which is empty.
     * The code now allows to select tiles that haven't any free side at the beginning of the round but get some
     * because of previous selections.
     */
    public List<Coordinate> getSelectableCoordinate() {
        List<Coordinate> res = new ArrayList<>();

        if (this.boardSelector.selectionSize() > 2) {
            return new ArrayList<>();
        }

        switch (boardSelector.selectionSize()) {
            case 0 -> {
                Consumer<Coordinate> a = p -> {
                    if (canExtractForNumberOfFreeSides(p) && !isEmpty(p))
                        res.add(p);
                };
                Board.TWO_PLAYER_POSITIONS.forEach(a);
                Board.THREE_PLAYER_POSITIONS.forEach(a);
                Board.FOUR_PLAYER_POSITIONS.forEach(a);
            }
            case 1, 2 -> {
                for (List<Coordinate> positions : boardSelector.getAvailableSelection()) {
                    for (Coordinate coordinate: positions) {
                        if (isOutOfBoard(coordinate) ||
                                isEmpty(coordinate) ||
                                !canExtractForNumberOfFreeSides(coordinate))
                            break;
                        res.add(coordinate);
                    }
                }
            }
        }

        return res;
    }

    protected boolean canExtractForNumberOfFreeSides (Coordinate coordinate) {
        return this.numberOfFreeSides(coordinate) > 0;
    }

    protected boolean isOutOfBoard (Coordinate c) {
        return c.getRow() < 0 || c.getCol() < 0 || c.getRow() >= Board.BOARD_ROWS || c.getCol() >= Board.COLUMN_BOARDS;
    }

    /**
     * The function checks that there are no Tiles within the board
     * that have 4 sides not touching any other Tile.
     * @return true if it is necessary to fill the board
     */
    public boolean needsRefill() {
        Predicate<Coordinate> checkEdges = c ->
            isEmpty(c) || numberOfFreeSides(c) == 4;

        if (this.occupied == 0)
            return true;

        return  Board.TWO_PLAYER_POSITIONS.stream().allMatch(checkEdges) &&
                Board.THREE_PLAYER_POSITIONS.stream().allMatch(checkEdges) &&
                Board.FOUR_PLAYER_POSITIONS.stream().allMatch(checkEdges);
    }

    /**
     * The function returns a {@link List List} of {@link Tile tiles} containing
     * all the {@link Tile tiles} that can be extracted in a single extraction.
     * In case there were multiple extractions before the call, the function will only return the legal extractions
     * from that point onwards.
     * @return All the {@link Tile tiles} available for extraction.
     * */
    public List<Tile> getSelectableTiles() {
        return this.getSelectableCoordinate()
                .stream()
                .map(this::tileAt)
                .toList();
    }

    /**
     * The function checks that the board is full for the number of players passed.
     * @return Return true iff the board is full for numPlayer players.
     * @param numPlayer number of players
     * @throws IllegalArgumentException if numPlayer is bigger than 4 or numPlayer is lower then 2
     * */
    public boolean isFull(final int numPlayer) {
        if (numPlayer < 2 || numPlayer > 4) {
            throw new IllegalArgumentException("The number of players must be between 2 and 4");
        }

        final int s = Board.TWO_PLAYER_POSITIONS.size()
                + (numPlayer > 2 ? Board.THREE_PLAYER_POSITIONS.size() : 0)
                + (numPlayer == 4 ? Board.FOUR_PLAYER_POSITIONS.size() : 0);

        return s == this.occupied;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this)
            return true;
        if (other == null || other.getClass() != this.getClass())
            return false;

        return Arrays.deepEquals(((BoardView) other).tiles, this.tiles) &&
                ((BoardView) other).occupied == this.occupied &&
                ((BoardView) other).boardSelector.equals(this.boardSelector);
    }

    public BoardView getView () {
        return new BoardView(this);
    }

    @Override
    public String toString () {
        StringBuilder stringBuilder = new StringBuilder().append("\n");
        for (Tile[] tile : this.tiles) {
            for (Tile value : tile) {
                if (value == null) {
                    stringBuilder.append("[ ]");
                } else {
                    stringBuilder.append(value.getColor().color("[#]"));
                }
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}
