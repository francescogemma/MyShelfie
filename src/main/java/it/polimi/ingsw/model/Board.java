package it.polimi.ingsw.model;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    public void select (int r, int c) throws IllegalExtractionException {
        if (sizeSelection() > 2) {
            throw new IllegalArgumentException("More than 3 tiles have already been selected");
        }

        if (contains(new Coordinate(r, c))) {
            throw new IllegalArgumentException("The specified coordinate already exists");
        }

        switch (sizeSelection()) {
            case 0 -> {}
            case 1, 2 -> {
                if (!getAvailableSelection().contains(new Coordinate(r, c))) {
                    throw new IllegalExtractionException();
                }
            }
        }

        this.selected.add(new Coordinate(r, c));
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
    public static final int rowBoard = 9;
    public static final int columnBoard = 9;
    private BoardSelector boardSelector;
    private int occupied;

    /**
     * All the possible cell positions if the players were two.
     * */
    static final int [][] twoPlayerPosition = {
                                    {1, 3}, {1, 4},
                            {2, 2}, {2, 3}, {2, 4}, {2, 5},
                    {3, 1}, {3, 2}, {3, 3}, {3, 4}, {3, 5}, {3, 6}, {3, 7},
            {4, 0}, {4, 1}, {4, 2}, {4, 3}, {4, 4}, {4, 5}, {4, 6}, {4, 7},
                    {5, 1}, {5, 2}, {5, 3}, {5, 4}, {5, 5}, {5, 6},
                                    {6, 3}, {6, 4}, {6, 5},
                                    {7, 3}, {7, 4}, {7, 5}
    };

    /**
     * All the possible cell positions if the players were three.
     * */
    static final int [][] threePlayerPosition = {
                                    {0, 3},
                                                            {2, 6},
                                                                            {3, 8},
            {5, 0},
                            {6, 2},                 {6, 6},
                                                    {8, 5}
    };

    /**
     * All the possible cell positions if the players were four.
     * */
    static final int [][] fourPlayerPosition = {
                                            {0, 4},
                                                    {1, 5},
                                                                            {4, 8},
                                                                    {5, 7},
                                                    {8, 4}
    };

    /**
     * tiles[i][j] will be null if the cell is empty
     */
    // TODO: replace Tile with Optional<Tile>
    private final Tile [][]tiles;

    public Board() {
        tiles = new Tile[rowBoard][columnBoard];
        boardSelector = new BoardSelector();
        occupied = 0;
    }

    /**
     * @return the Tile in position [row, col]
     */
    public Tile tileAt(int row, int col) {
        return this.tiles[row][col];
    }

    /**
     * The function selects the Tile for extraction, it handles both the case where a Tile without
     * a free side is selected, and if a Tile is selected that cannot be extracted together with
     * the Tiles previously selected.
     * @return The {@link Tile Tile} selected.
    * */
    public Tile selectTile(int row, int col) throws IllegalExtractionException {
        if (this.isEmpty(row, col) || numberOfFreeSides(row, col, this::isEmptyExtraction) == 0) {
            throw new IllegalExtractionException("Can't extract tile at: [" + row + ", " + col + "]");
        }

        this.boardSelector.select(row, col);
        return this.tileAt(row, col);
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
                .map((t) -> this.tileAt(t.getCol(), t.getRow()))
                .collect(Collectors.toList());
    }

    /**
     * @return return true if tiles[row][col] is on border
     * */
    private boolean hasEdgeOnBorder(int row, int col) {
        return row == tiles.length || col == tiles.length ||
                row == 0 || col == 0;
    }

    private boolean isEmptyExtraction(int row, int col) {
        if (this.boardSelector.contains(new Coordinate(row, col)))
            return true;
        return this.isEmpty(row, col);
    }

    private boolean isOutOfBoard (int row, int col) {
        return row < 0 || col < 0 || row >= Board.rowBoard || col >= Board.columnBoard;
    }

    /**
     * The function returns a {@link List List} of {@link Tile Tile} containing
     * all the {@link Tile tiles} that can be extracted in a single extraction.
     * In case there were multiple extractions before the call, the function will only return the legal extractions
     * from that point onwards.
     * @return All the {@link Tile tiles} available for extraction.
    * */
    public List<Tile> getSelectableTiles() {
        List<Tile> res = new ArrayList<>();

        if (this.boardSelector.sizeSelection() > 2) {
            return res;
        }

        Consumer<int[][]> action = (int[][] board) -> {
            for (int[] ints : board) {
                final int row = ints[0];
                final int col = ints[1];



                if (this.numberOfFreeSides(row, col, this::isEmptyExtraction) > 0 && tileAt(row, col) != null){
                    res.add(tileAt(row, col));
                }
            }
        };

        switch (boardSelector.sizeSelection()) {
            case 0 -> {
                action.accept(Board.twoPlayerPosition);
                action.accept(Board.threePlayerPosition);
                action.accept(Board.fourPlayerPosition);
            }
            case 1, 2 -> {
                boardSelector.getAvailableSelection()
                        .forEach((t) -> {
                            if (!isOutOfBoard(t.getRow(), t.getCol()) && !isEmpty(t.getRow(), t.getCol()))
                                res.add(tileAt(t.getRow(), t.getCol()));
                        });
            }
        }

        return res;
    }

    /**
     * @param row cell row
     * @param col cell column
     * @return true if there is no tile in position [row, column]
     */
    private boolean isEmpty(int row, int col) {
        return this.tileAt(row, col) == null;
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

        final int s = Board.twoPlayerPosition.length
                + (numPlayer > 2 ? Board.threePlayerPosition.length : 0)
                + (numPlayer == 4 ? Board.fourPlayerPosition.length : 0);

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
                .forEach((t) -> res.add(this.remove(t.getRow(), t.getCol())));

        this.boardSelector = new BoardSelector();

        return res;
    }

    private int numberOfFreeSides(int row, int col, BiFunction<Integer, Integer, Boolean> isEmptyFunc) {
        if (this.isOutOfBoard(row, col))
            throw new IllegalArgumentException("row or col out of bound: row: " + row + " col: " + col);

        int free = 0;
        final boolean onBorder = this.hasEdgeOnBorder(row, col);
        if (onBorder)
            free ++;

        if (col + 1 < Board.columnBoard && isEmptyFunc.apply(row, col+1))
                free ++;

        if (row + 1 < Board.rowBoard && isEmptyFunc.apply(row + 1, col))
                free ++;

        if (row != 0 && isEmptyFunc.apply(row - 1, col))
                free ++;

        if (col != 0 && isEmptyFunc.apply(row, col - 1))
                free++;

        return free;
    }

    private int numberOfFreeSides(int row, int col) {
        return numberOfFreeSides(row, col, this::isEmpty);
    }

    private List<Coordinate> getAvailablePositionInsert(int numPlayer) {
        List<Coordinate> res = new ArrayList<>();

        if (this.occupied == 0) {
            Arrays.stream(Board.twoPlayerPosition)
                    .forEach((t) -> res.add(new Coordinate(t[0], t[1])));
            if (numPlayer > 2) {
                Arrays.stream(Board.threePlayerPosition)
                        .forEach((t) -> res.add(new Coordinate(t[0], t[1])));
            }
            if (numPlayer == 4) {
                Arrays.stream(Board.fourPlayerPosition)
                        .forEach((t) -> res.add(new Coordinate(t[0], t[1])));
            }

            return res;
        }

        Consumer<int[][]> action = (int [][] position) -> {
            for (int[] ints : position) {
                final int row = ints[0];
                final int col = ints[1];

                if (this.isEmpty(row, col)) {
                    final int edge = numberOfFreeSides(row, col);

                    if (edge < 4)
                        res.add(new Coordinate(
                                row, col
                        ));
                }
            }
        };

        action.accept(Board.twoPlayerPosition);

        if (numPlayer > 2) {
            action.accept(Board.threePlayerPosition);
        }

        if (numPlayer == 4) {
            action.accept(Board.fourPlayerPosition);
        }

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

        final List<Coordinate> possible = this.getAvailablePositionInsert(numPlayer);
        final int index = new Random().nextInt(possible.size());

        this.insert(tile,
                possible.get(index).getRow(),
                possible.get(index).getCol()
        );
    }

    private void insert(Tile tile, int row, int col) {
        this.tiles[row][col] = tile;
        this.occupied ++;
    }

    /**
     * The function checks that there are no Tiles within the board
     * that have 4 sides not touching any other Tile.
     * @return true if it is necessary to fill the board
     */
    public boolean needsRefill() {

        Function<int [][], Boolean> checkerEdges =
                (int [][] data) -> Arrays.stream(data)
                    .anyMatch((coor -> {
                        final int r = coor[0];
                        final int c = coor[1];
                        if (tileAt(r, c) != null) {
                            return numberOfFreeSides(r, c) == 4;
                        }
                        return false;
                    }));

        if (this.occupied == 0)
            return true;

        return  checkerEdges.apply(Board.twoPlayerPosition) ||
                checkerEdges.apply(Board.threePlayerPosition) ||
                checkerEdges.apply(Board.fourPlayerPosition);
    }

    private Tile remove (int row, int col) {
        Tile t = tiles[row][col];
        if (t == null)
            throw new IllegalArgumentException();
        this.occupied --;
        this.tiles[row][col] = null;
        return t;
    }

    @Override
    public String toString() {
        int i, k;
        StringBuilder result = new StringBuilder("---------------\n");
        for (i = 0; i < this.tiles.length; i++) {
            for (k = 0; k < this.tiles[i].length; k++) {
                if (isEmpty(i, k))
                    result.append("[ ]");
                else
                    result.append("[").append(this.tiles[i][k].color("#")).append("]");
            }
            result.append("\n");
        }
        return result.toString();
    }
}
