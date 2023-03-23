package it.polimi.ingsw.model;

import jdk.jshell.spi.ExecutionControl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.lang.Math.abs;
import static java.lang.Math.max;

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

        final int x0 = selected.get(0).getX();
        final int x1 = selected.get(1).getX();

        return x0 == x1;
    }

    protected boolean contains(Coordinate c) {
        return this.selected.contains(c);
    }

    /**
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

        switch (selected.size()) {
            case 0 -> {}
            case 1 -> {
                Coordinate alreadySelected = this.selected.get(0);
                final int alreadySelectedRow = alreadySelected.getY();
                final int alreadySelectedCol = alreadySelected.getX();

                if (abs(alreadySelectedRow - r) + abs(alreadySelectedCol - c) != 1)
                    throw new IllegalExtractionException();
            }
            case 2 -> {
                if (this.isVerticalExtraction()) {
                    if (!(this.selected.get(0).getX() == c && (
                            abs(selected.get(0).getY() - r) == 1 ||
                            abs(selected.get(1).getY() - r) == 1)
                    )) {
                        throw new IllegalExtractionException();
                    }
                } else {
                    Coordinate c0 = selected.get(0);
                    Coordinate c1 = selected.get(1);
                    if (!(c0.getY() == r && (
                            abs(c0.getX() - c) == 1 ||
                            abs(c1.getX() - c) == 1)
                    )) {
                        throw new IllegalExtractionException();
                    }
                }
            }
            default -> throw new RuntimeException();
        }

        this.selected.add(new Coordinate(r, c));
    }

    /**
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


    static final int [][] twoPlayerPosition = {
                                    {1, 3}, {1, 4},
                            {2, 2}, {2, 3}, {2, 4}, {2, 5},
                    {3, 1}, {3, 2}, {3, 3}, {3, 4}, {3, 5}, {3, 6}, {3, 7},
            {4, 0}, {4, 1}, {4, 2}, {4, 3}, {4, 4}, {4, 5}, {4, 6}, {4, 7},
                    {5, 1}, {5, 2}, {5, 3}, {5, 4}, {5, 5}, {5, 6},
                                    {6, 3}, {6, 4}, {6, 5},
                                    {7, 3}, {7, 4}, {7, 5}
    };

    static final int [][] threePlayerPosition = {
                                    {0, 3},
                                                            {2, 6},
                                                                            {3, 8},
            {5, 0},
                            {6, 2},                 {6, 6},
                                                    {8, 5}
    };

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
    public Tile typeAt(int row, int col) {
        return this.tiles[row][col];
    }

    /**
     * 
    * */
    public Tile selectTile(int row, int col) throws IllegalExtractionException {
        if (this.isEmpty(row, col) || numberOfFreeSides(row, col, this::isEmptyExtraction) == 0) {
            System.out.println(this);
            throw new IllegalExtractionException("Can't extract tile at: [" + row + ", " + col + "]");
        }

        this.boardSelector.select(row, col);
        return this.typeAt(row, col);
    }

    /**
     * @return All Tiles selected so far.
     */
    public List<Tile> getSelectedTiles() {
        return this
                .boardSelector
                .getSelected()
                .stream()
                .map((t) -> this.typeAt(t.getX(), t.getY()))
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

    /**
     * @return All the Tiles on the board.
     */
    public List<Tile> getSelectableTiles() {
        List<Tile> res = new ArrayList<>();

        Consumer<int[][]> action = (int[][] board) -> {
            for (int[] ints : board) {
                final int row = ints[0];
                final int col = ints[1];
                if (row < 0 || row > Board.rowBoard)
                    continue;
                if (col < 0 || col > Board.columnBoard)
                    continue;
                if (this.numberOfFreeSides(row, col, this::isEmptyExtraction) > 0 && typeAt(row, col) != null){
                    res.add(typeAt(row, col));
                }
            }
        };

        if (this.boardSelector.sizeSelection() == 0) {
            action.accept(Board.twoPlayerPosition);
            action.accept(Board.threePlayerPosition);
            action.accept(Board.fourPlayerPosition);
        } else if (this.boardSelector.sizeSelection() == 1) {
            final List<Coordinate> s = boardSelector.getSelected();
            final int sy = s.get(0).getY();
            final int sx = s.get(0).getX();

            final int[][] selectable = {
                    {sy + 1, sx},
                    {sy - 1, sx},
                    {sy, sx + 1},
                    {sy, sx - 1}
            };
            action.accept(selectable);
        } else if (boardSelector.sizeSelection() == 2) {
            final List<Coordinate> s = boardSelector.getSelected();
            final int x0 = s.get(0).getX();
            final int y0 = s.get(0).getY();

            if (boardSelector.isVerticalExtraction()) {
                final int sy1 = s.get(1).getY();

                final int [][] selectable = {
                        {
                            Math.max(y0, sy1) + 1,
                            x0
                        },
                        {
                            Math.min(y0, sy1) - 1,
                            x0
                        }
                };
                action.accept(selectable);
            }
            else{
                final int sx1 = s.get(1).getX();

                final int[][] selectable = {
                        {
                            y0,
                            Math.max(x0, sx1) + 1
                        },
                        {
                            y0,
                            Math.min(x0, sx1) - 1
                        }
                };
                action.accept(selectable);
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
        return this.typeAt(row, col) == null;
    }

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
     * The function removes the tiles from the board
     * @return The list of all Tiles selected.
     */
    public List<Tile> draw() {
        List<Tile> res = new ArrayList<>();

        this.boardSelector
                .getSelected()
                .forEach((t) -> res.add(this.remove(t.getY(), t.getX())));

        this.boardSelector = new BoardSelector();

        return res;
    }

    private int numberOfFreeSides(int row, int col, BiFunction<Integer, Integer, Boolean> isEmptyFunc) {
        if (row < 0 || row > Board.rowBoard || col < 0 || col > Board.columnBoard)
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

    public void fillRandomly(final Tile tile, final int numPlayer) {
        if (this.isFull(numPlayer))
            throw new IllegalArgumentException("Board is full");

        final List<Coordinate> possible = this.getAvailablePositionInsert(numPlayer);
        final int index = new Random().nextInt(possible.size());

        this.insert(tile,
                possible.get(index).getY(),
                possible.get(index).getX()
        );
    }

    private void insert(Tile tile, int row, int col) {
        this.tiles[row][col] = tile;
        this.occupied ++;
    }

    /**
     * 
     * @return true if it is necessary to fill the board
     */
    public boolean needsRefill() {
        int r, c;

        for (r = 0; r < rowBoard; r++) {
            for (c = 0; c < columnBoard; c++) {
                if (this.typeAt(r, c) != null) {
                    if (numberOfFreeSides(r, c) == 4) {
                        return true;
                    }
                }
            }
        }

        return occupied == 0;
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
