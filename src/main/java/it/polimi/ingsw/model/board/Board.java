package it.polimi.ingsw.model.board;

import it.polimi.ingsw.utils.Coordinate;
import it.polimi.ingsw.model.Tile;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Manager for Tile selection
 * @author Giacomo Groppi
 */
class BoardSelector {
    private final List<Coordinate> selected;

    BoardSelector () {
        selected = new ArrayList<>();
    }

    public int sizeSelection () {
        return this.selected.size();
    }

    /**
     * @exception RuntimeException if sizeSelection() != 2
     * @return true if the extraction is Vertical
     */
    protected boolean isVerticalExtraction () {
        if (this.sizeSelection() != 2)
            throw new RuntimeException();

        final int x0 = selected.get(0).getCol();
        final int x1 = selected.get(1).getCol();

        return x0 == x1;
    }

    /**
     * The function checks in all the possible previous selections
     * and returns whether the coordinate has already been selected.
     * @param c the point for check
     * @return true iff c is already selected
     * */
    protected boolean contains(Coordinate c) {
        return this.selected.contains(c);
    }

    /**
     * The function returns the set of coordinates that can be selected for extraction,
     * taking into account the previous extractions.
     * @return List of legal Coordinates for selection.
     * */
    protected List<Coordinate> getAvailableSelection() {
        List<Coordinate> res = new ArrayList<>();
        switch (sizeSelection()) {
            case 0 -> {
                throw new RuntimeException("Can't call this function if sizeSelection() is equals to 0");
            }
            case 1 -> {
                final int r = this.selected.get(0).getRow();
                final int c = this.selected.get(0).getCol();
                res.addAll(Arrays.asList(
                        new Coordinate(r, c + 1),
                        new Coordinate(r ,c - 1),
                        new Coordinate(r + 1, c),
                        new Coordinate(r - 1, c)
                        ));
            }
            case 2 -> {
                if (isVerticalExtraction()) {
                    final int col = this.selected.get(0).getCol();
                    final int biggerRow =   max(selected.get(0).getRow(), selected.get(1).getRow());
                    final int smallerRow =  min(selected.get(0).getRow(), selected.get(1).getRow());
                    res.addAll(Arrays.asList(
                            new Coordinate(smallerRow - 1, col),
                            new Coordinate(biggerRow + 1, col)
                    ));
                } else {
                    final int row = this.selected.get(0).getRow();
                    final int biggerCol =   max(selected.get(0).getCol(), selected.get(1).getCol());
                    final int smallerCol =  min(selected.get(0).getCol(), selected.get(1).getCol());
                    res.addAll(Arrays.asList(
                            new Coordinate(row, smallerCol - 1),
                            new Coordinate(row, biggerCol + 1)
                    ));
                }
            }
            default -> res = new ArrayList<>();
        }
        return res;
    }

    /**
     * The function checks that wrong positions are not selected, but it does not check whether the cell can actually be selected, i.e., whether it has a free side.
     * @exception IllegalExtractionException if the position of the Tile is along the diagonal
     * @exception IllegalArgumentException if we are trying to extract more than 3 tiles
     * @exception IllegalArgumentException if [r, c] is already in the list
     */
    public void select (Coordinate c) throws IllegalExtractionException, SelectionFullException {
        if (sizeSelection() > 2) {
            throw new SelectionFullException();
        }

        if (contains(c)) {
            throw new IllegalArgumentException("The specified coordinate already exists");
        }

        switch (sizeSelection()) {
            case 0 -> {}
            case 1, 2 -> {
                if (!getAvailableSelection().contains(c)) {
                    throw new IllegalExtractionException();
                }
            }
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
}

/**
 * Board manager
 * @author Giacomo Groppi
 * */
public class Board {
    public static final int ROW_BOARD = 9;
    public static final int COLUMN_BOARD = 9;
    private BoardSelector boardSelector;
    private int occupied;

    /**
     * All the possible cell positions if the players were two.
     */
    public static final List<Coordinate> TWO_PLAYER_POSITION = Coordinate.toList(Arrays.asList(
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
    public static final List<Coordinate> THREE_PLAYER_POSITION = Coordinate.toList(Arrays.asList(
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
    public static final List<Coordinate> FOUR_PLAYER_POSITION = Coordinate.toList(Arrays.asList(
        new int[][] {
                {0, 4}, {1, 5}, {4, 8}, {5, 7}, {8, 4}
    }));

    /**
     * tiles[i][j] will be null if the cell is empty
     */
    private final Tile[][]tiles = new Tile[ROW_BOARD][COLUMN_BOARD];

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
     * @return the Tile in position [row, col]
     */
    public Tile tileAt(Coordinate c) {
        return this.tiles[c.getRow()][c.getCol()];
    }

    /**
     * The function selects the Tile for extraction, it handles both the case where a Tile without
     * a free side is selected, and if a Tile is selected that cannot be extracted together with
     * the Tiles previously selected.
     * @return The {@link Tile Tile} selected.
    * */
    public Tile selectTile(Coordinate c) throws IllegalExtractionException, SelectionFullException {
        if (isOutOfBoard(c) || this.isEmptyExtraction(c)  || numberOfFreeSides(c, this::isEmptyExtraction) == 0) {
            throw new IllegalExtractionException("Can't extract tile at: [" + c.getRow() + ", " + c.getCol() + "]");
        }

        this.boardSelector.select(c);
        return this.tileAt(c);
    }

    public Tile selectTile (int row, int col) throws IllegalExtractionException, SelectionFullException {
        return this.selectTile(new Coordinate(row, col));
    }

    /**
     * The function returns the set of tiles selected up to this point.
     * The function does not remove the objects from the board.
     * @return All Tiles selected so far.
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
     * @return return true if tiles[row][col] is on border
     * */
    private boolean hasEdgeOnBorder(Coordinate c) {
        return c.getRow() == tiles.length || c.getCol() == tiles.length ||
                c.getRow() == 0 || c.getCol() == 0;
    }

    private boolean isEmptyExtraction(Coordinate c) {
        if (this.boardSelector.contains(c))
            return true;
        return this.isEmpty(c);
    }

    private boolean isOutOfBoard (Coordinate c) {
        return c.getRow() < 0 || c.getCol() < 0 || c.getRow() >= Board.ROW_BOARD || c.getCol() >= Board.COLUMN_BOARD;
    }

    public List<Coordinate> getSelectableCoordinate() {
        List<Coordinate> res = new ArrayList<>();

        if (this.boardSelector.sizeSelection() > 2) {
            return new ArrayList<>();
        }

        Consumer<Coordinate> a = p -> {
            if (numberOfFreeSides(p, this::isEmptyExtraction) > 0 && !isEmpty(p))
                res.add(p);
        };

        switch (boardSelector.sizeSelection()) {
            case 0 -> {
                Board.TWO_PLAYER_POSITION.forEach(a);
                Board.THREE_PLAYER_POSITION.forEach(a);
                Board.FOUR_PLAYER_POSITION.forEach(a);
            }
            case 1, 2 -> {
                boardSelector
                        .getAvailableSelection()
                        .stream()
                        .filter(p -> !isOutOfBoard(p))
                        .filter(p -> !isEmpty(p))
                        .forEach(res::add);
            }
        }

        return res;
    }

    /**
     * The function returns a {@link List List} of {@link Tile Tile} containing
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
     * @param c cell coordinate
     * @return true if there is no tile in position [row, column]
     */
    private boolean isEmpty(Coordinate c) {
        return this.tileAt(c) == null;
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

        final int s = Board.TWO_PLAYER_POSITION.size()
                + (numPlayer > 2 ? Board.THREE_PLAYER_POSITION.size() : 0)
                + (numPlayer == 4 ? Board.FOUR_PLAYER_POSITION.size() : 0);

        return s == this.occupied;
    }

    /**
     * The function removes the tiles from the board and return a List of {@link Tile tile} selected.
     * @return The list of all {@link Tile tile} selected.
     */
    public List<Tile> draw() {
        List<Tile> res = new ArrayList<>();

        this.boardSelector
                .getSelected()
                .forEach(t -> res.add(this.remove(t)));

        this.boardSelector = new BoardSelector();

        return res;
    }

    private int numberOfFreeSides(Coordinate c, Predicate<Coordinate> isEmptyFunc) {
        if (this.isOutOfBoard(c))
            throw new IllegalArgumentException("row or col out of bound: row: " + c.getRow() + " col: " + c.getCol());

        int free = 0;
        final boolean onBorder = this.hasEdgeOnBorder(c);
        if (onBorder)
            free ++;

        if (c.getCol() + 1 < Board.COLUMN_BOARD && isEmptyFunc.test(new Coordinate(c.getRow(), c.getCol()+1)))
                free ++;

        if (c.getRow() + 1 < Board.ROW_BOARD && isEmptyFunc.test(new Coordinate(c.getRow() + 1, c.getCol())))
                free ++;

        if (c.getRow() != 0 && isEmptyFunc.test(new Coordinate(c.getRow() - 1, c.getCol())))
                free ++;

        if (c.getCol() != 0 && isEmptyFunc.test(new Coordinate(c.getRow(), c.getCol() - 1)))
                free++;

        return free;
    }

    private int numberOfFreeSides(Coordinate c) {
        return numberOfFreeSides(c, this::isEmpty);
    }

    private List<Coordinate> getAvailablePositionInsert(int numPlayer) {
        List<Coordinate> res = new ArrayList<>();

        Board.TWO_PLAYER_POSITION
                .stream()
                .filter(this::isEmpty)
                .forEach(res::add);

        if (numPlayer > 2)
            Board.THREE_PLAYER_POSITION
                    .stream()
                    .filter(this::isEmpty)
                    .forEach(res::add);

        if (numPlayer == 4)
            Board.FOUR_PLAYER_POSITION
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

    /**
     * The function checks that there are no Tiles within the board
     * that have 4 sides not touching any other Tile.
     * @return true if it is necessary to fill the board
     */
    public boolean needsRefill() {
        Predicate<Coordinate> checkEdges = (final Coordinate c) ->
                !isEmpty(c) && (numberOfFreeSides(c) == 4);

        if (this.occupied == 0)
            return true;

        return Board.TWO_PLAYER_POSITION.stream().anyMatch(checkEdges) ||
                Board.THREE_PLAYER_POSITION.stream().anyMatch(checkEdges) ||
                Board.FOUR_PLAYER_POSITION.stream().anyMatch(checkEdges);
    }

    private Tile remove (Coordinate c) {
        if (this.isEmpty(c))
            throw new IllegalArgumentException();

        Tile t = this.tileAt(c);
        this.occupied --;
        this.tiles[c.getRow()][c.getCol()] = null;
        return t;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("---------------\n");
        for (int i = 0; i < this.tiles.length; i++) {
            for (int k = 0; k < this.tiles[i].length; k++) {
                if (isEmpty(new Coordinate(i, k)))
                    result.append("[ ]");
                else
                    result.append("[").append(this.tiles[i][k].color("#")).append("]");
            }
            result.append("\n");
        }
        return result.toString();
    }
}