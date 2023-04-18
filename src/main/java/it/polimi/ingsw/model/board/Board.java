package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.tile.Tile;
import it.polimi.ingsw.utils.Coordinate;

import java.util.*;

/**
 * Board manager
 * @author Giacomo Groppi
 * */
public class Board extends BoardView {
    // TODO: JavaDoc for some private fields is missing (we must add it :( [Laboratorio 2 - Javadoc-1.pdf, slide number 9])

    public static final int BOARD_ROWS = 9;
    public static final int COLUMN_BOARDS = 9;

    /**
     * All the possible cell positions if the players were two.
     */
    public static final List<Coordinate> TWO_PLAYER_POSITIONS = Coordinate.toList(Arrays.asList(
            new int [][] {
                                {1, 3}, {1, 4},
                        {2, 2}, {2, 3}, {2, 4}, {2, 5},
                {3, 1}, {3, 2}, {3, 3}, {3, 4}, {3, 5}, {3, 6}, {3, 7},
        {4, 0}, {4, 1}, {4, 2}, {4, 3}, {4, 4}, {4, 5}, {4, 6}, {4, 7},
                {5, 1}, {5, 2}, {5, 3}, {5, 4}, {5, 5}, {5, 6},
                                {6, 3}, {6, 4}, {6, 5},
                                {7, 3}, {7, 4}, {7, 5}
    }));

    /**
     * All the possible cell positions if the players were three.
     */
    public static final List<Coordinate> THREE_PLAYER_POSITIONS = Coordinate.toList(Arrays.asList(
            new int[][] {
                                    {0, 3},
                                                            {2, 6},
                                                                            {3, 8},
            {5, 0},
                            {6, 2},                 {6, 6},
                                                    {8, 5}
            }
    ));

    /**
     * All the possible cell positions if the players were four.
     */
    public static final List<Coordinate> FOUR_PLAYER_POSITIONS = Coordinate.toList(Arrays.asList(
        new int[][] {
                {0, 4}, {1, 5}, {4, 8}, {5, 7}, {8, 4}
    }));

    /**
     * Constructor of the Board class.
     * Initially, it represents an empty Board, so it will be necessary
     * to call the fillRandomly function to fill it.
     *
     * @see #fillRandomly(Tile, int)
     */
    public Board() {
        boardSelector = new BoardSelector();
        occupied = 0;
    }

    /**
     * TODO: add javadoc
     * */
    public Board (Board other) {
        this.boardSelector = new BoardSelector(other.boardSelector);
        this.occupied = other.occupied;
        for (int i = 0; i < tiles.length; i ++) {
            System.arraycopy(other.tiles[i], 0, tiles[i], 0, tiles[i].length);
        }
    }

    /**
     * The function selects the Tile for extraction, it handles both the case where a Tile without
     * a free side is selected, and if a Tile is selected that cannot be extracted together with
     * the Tiles previously selected.
     * @return The {@link Tile Tile} selected.
    * */
    public Tile selectTile(Coordinate c) throws IllegalExtractionException, FullSelectionException {
        if (isOutOfBoard(c)) {
            throw new IllegalArgumentException("It's out of the board");
        }

        if (this.isEmpty(c))
            throw new IllegalExtractionException("Can't extract tile at: [" + c.getRow() + ", " + c.getCol() + "] because tile is empty");

        if (numberOfFreeSides(c) == 0) {
            throw new IllegalExtractionException("Can't extract tile at: [" + c.getRow() + ", " + c.getCol() + "]");
        }

        this.boardSelector.select(c);
        return this.tileAt(c);
    }

    public Tile selectTile (int row, int col) throws IllegalExtractionException, FullSelectionException {
        return this.selectTile(new Coordinate(row, col));
    }

    /**
     * The function removes the tiles from the board and return a List of selected {@link Tile tiles}.
     * @return The list of all selected {@link Tile tiles}.
     */
    public List<Tile> draw() {
        List<Tile> res = new ArrayList<>();

        this.boardSelector
                .getSelected()
                .forEach(t -> res.add(this.remove(t)));

        this.boardSelector = new BoardSelector();

        return res;
    }

    private List<Coordinate> getAvailablePositionInsert(int numPlayer) {
        List<Coordinate> res = new ArrayList<>();

        Board.TWO_PLAYER_POSITIONS
                .stream()
                .filter(this::isEmpty)
                .forEach(res::add);

        if (numPlayer > 2)
            Board.THREE_PLAYER_POSITIONS
                    .stream()
                    .filter(this::isEmpty)
                    .forEach(res::add);

        if (numPlayer == 4)
            Board.FOUR_PLAYER_POSITIONS
                    .stream()
                    .filter(this::isEmpty)
                    .forEach(res::add);

        return res;
    }

    /**
     * The function places the {@link Tile tile} in a random position within the
     * board.
     * The function places the {@link Tile tile} in a legal position, therefore it is guaranteed that the
     * {@link Tile tile} inserted will have at least one occupied side, if such position exists.
    */
    public void fillRandomly(final Tile tile, final int numPlayer) {
        if (this.isFull(numPlayer))
            throw new IllegalArgumentException("Board is full");

        final List<Coordinate> possible = getAvailablePositionInsert(numPlayer);
        int index = new Random().nextInt(possible.size());

        this.insert(tile,
                possible.get(index)
        );
    }

    private void insert(Tile tile, Coordinate c) {
        this.tiles[c.getRow()][c.getCol()] = tile;
        this.occupied ++;
    }

    private Tile remove (Coordinate c) {
        if (this.isEmpty(c))
            throw new IllegalArgumentException();

        Tile t = this.tileAt(c);
        this.occupied --;
        this.tiles[c.getRow()][c.getCol()] = null;
        return t;
    }

    // TODO: javadoc
    public void forgetSelected (Coordinate c) {
        if (!this.boardSelector.contains(c))
            throw new IllegalArgumentException("Cooridnate is not selected");

        if (!this.boardSelector.lastSelected().equals(c))
            throw new RemoveNotLastSelectedException();

        this.boardSelector.forgetLastSelected();
    }

    // TODO: javadoc
    // TODO: Allow to deselect one tile at the time
    public void forgetSelection () {
        this.boardSelector = new BoardSelector();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("---------------\n");
        for (int i = 0; i < this.tiles.length; i++) {
            for (int k = 0; k < this.tiles[i].length; k++) {
                if (isEmpty(new Coordinate(i, k)))
                    result.append("[ ]");
                else
                    result.append("[").append(this.tiles[i][k].getColor().color("#")).append("]");
            }
            result.append("\n");
        }
        return result.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this)
            return true;
        if (other == null || other.getClass() != this.getClass())
            return false;
        return super.equals(other);
    }
}
