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

    /**
     * The number of rows in the board.
     * */
    public static final int BOARD_ROWS = 9;

    /**
     * The number of columns in the board.
     * */
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
     * Construct a new Board equals to the one passed as parameter.
     * @param other The Board to copy.
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
     *
     * @return The {@link Tile Tile} selected.
     *
     * @throws IllegalExtractionException
     *  <ul>
     *      <li> There are no selection coordinate </li>
     *      <li> Coordinate can't be selected </li>
     *  </ul>
     *
     * @throws IllegalArgumentException iff coordinate is outside the border
     *
     * @param coordinate The {@link Coordinate Coordinate} of the {@link Tile Tile} to select.
     *
     * @see #getSelectableCoordinate()
    * */
    public Tile selectTile(Coordinate coordinate) throws IllegalExtractionException, FullSelectionException {
        if (isOutOfBoard(coordinate))
            throw new IllegalArgumentException("It's out of the board");

        if (this.isEmpty(coordinate))
            throw new IllegalExtractionException("Can't extract tile at: [" + coordinate.getRow() + ", " + coordinate.getCol() + "] because tile is empty");

        if (!getSelectableCoordinate().contains(coordinate))
            throw new IllegalExtractionException("Can't extract tile at: [" + coordinate.getRow() + ", " + coordinate.getCol() + "]");

        this.boardSelector.select(coordinate);
        return this.tileAt(coordinate);
    }

    /**
     *
     * */
    public Tile selectTile (int row, int col) throws IllegalExtractionException, FullSelectionException {
        return this.selectTile(new Coordinate(row, col));
    }

    /**
     * The function removes the tiles from the board and return a List of selected {@link Tile tiles}.
     * @return The list of all selected {@link Tile tiles}.
     */
    public List<Tile> draw() throws IllegalExtractionException {
        List<Tile> res = new ArrayList<>();

        this.boardSelector
                .draw()
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
        assert !isEmpty(c);

        Tile t = this.tileAt(c);
        this.occupied --;
        this.tiles[c.getRow()][c.getCol()] = null;
        return t;
    }

    /**
     * Use this method to deselect a coordinate from the current extraction
     * @param coordinate the coordinate to deselect
     * @throws IllegalArgumentException iff coordinate is not selected
     * @throws RemoveNotLastSelectedException iff coordinate is not the last Coordinate selected
     * */
    public void forgetSelected (Coordinate coordinate) {
        if (coordinate == null)
            throw new NullPointerException();
        if (!this.boardSelector.contains(coordinate))
            throw new IllegalArgumentException("Coordinate is not selected");

        if (!this.boardSelector.lastSelected().equals(coordinate))
            throw new RemoveNotLastSelectedException();

        this.boardSelector.forgetLastSelected();
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
