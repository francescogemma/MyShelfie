package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.lang.Math.abs;

class BoardSelector {
    private final List<Coordinates> selected;

    BoardSelector () {
        selected = new ArrayList<>();
    }

    public int sizeSelection () {
        return this.selected.size();
    }

    private boolean isVerticalExtraction () {
        if (this.sizeSelection() != 2)
            throw new RuntimeException();

        return this.selected.get(0).getY() == this.selected.get(1).getY();
    }

    public void select (int r, int c) throws IllegalExtractionException {
        if (sizeSelection() > 2) {
            throw new IllegalArgumentException("More than 3 tiles have already been selected");
        }

        if (selected.contains(new Coordinates(r, c))) {
            throw new IllegalArgumentException("The specified coordinate already exists");
        }

        switch (selected.size()) {
            case 1 -> {
                Coordinates alreadySelected = this.selected.get(0);
                final int alreadySelectedRow = alreadySelected.getY();
                final int alreadySelectedCol = alreadySelected.getX();

                if (abs(alreadySelectedRow - r) + abs(alreadySelectedCol - c) != 1)
                    throw new IllegalExtractionException();
            }
            case 2 -> {
                if (this.isVerticalExtraction()) {
                    if (this.selected.get(0).getX() == c && (
                            abs(selected.get(0).getY() - r) == 1 ||
                            abs(selected.get(1).getY() - r) == 1)
                    ) {
                        throw new IllegalExtractionException();
                    }
                } else {
                    if (this.selected.get(0).getY() == r && (
                            abs(selected.get(0).getX() - c) == 1 ||
                            abs(selected.get(1).getX() - c) == 1)
                    ){
                        throw new IllegalExtractionException();
                    }
                }
            }
            default -> throw new RuntimeException();
        }

        this.selected.add(new Coordinates(r, c));
    }

    public final List<Coordinates> getSelected() {
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
     * tiles[i][j] will be null if and only if the cell is empty
     */
    private final Tile [][]tiles;

    public Board() {
        tiles = new Tile[rowBoard][columnBoard];
        boardSelector = new BoardSelector();
        occupied = 0;
    }

    public Tile typeAt(int row, int col) {
        return this.tiles[row][col];
    }

    public Tile selectTile(int row, int col) throws IllegalExtractionException {
        if (this.isEmpty(row, col) || this.freeSide(row, col) == 0) {
            throw new IllegalExtractionException();
        }

        this.boardSelector.select(row, col);
        return this.typeAt(row, col);
    }

    public List<Tile> getSelectedTiles() {
        return this
                .boardSelector
                .getSelected()
                .stream()
                .map((t) -> this.typeAt(t.getX(), t.getY()))
                .collect(Collectors.toList());
    }

    /**
     * @return  it returns true if tiles[row + deltaRow, col + deltaCol] is outside of the board or
     *          tiles[row + deltaRow, col + deltaCol] is empty
     * */
    private boolean isSideFree(int row, int col, int deltaRow, int deltaCol) {
        if (abs(deltaRow) == 1 ^ abs(deltaCol) == 1)
            throw new IllegalArgumentException();

        if (deltaCol + col >= columnBoard || deltaCol + col < 0)
            return true;
        if (deltaRow + row >= rowBoard || deltaRow + row < 0)
            return true;

        return isEmpty(row + deltaRow, col + deltaCol);
    }

    /**
     * @return return true if tiles[row][col] is on border
     * */
    private boolean hasEdgeOnBorder(int row, int col) {
        return row == tiles.length || col == tiles.length ||
                row == 0 || col == 0;
    }

    /**
     * @return the number of free side in position (row, col)
     *          if the cell is on the edge of the board the function will always return > 0
     * */
    private int freeSide(int row, int col) {
        final Coordinates[] pos = new Coordinates[]{
                new Coordinates(0, -1),
                new Coordinates(-1, 0),
                new Coordinates(1, 0),
                new Coordinates(0, 1)
        };

        int free = 0;

        for (Coordinates coordinates: pos) {
            if (isSideFree(row, col, coordinates.getY(), coordinates.getY()))
                free ++;
        }

        return free;
    }

    public List<Tile> getSelectableTiles() {
        List<Tile> res = new ArrayList<>();

        Consumer<int[][]> action = (int[][] board) -> {
            for (int[] ints : board) {
                final int row = ints[0];
                final int col = ints[1];
                if (freeSide(row, col) > 0) {
                    res.add(typeAt(row, col));
                }
            }
        };

        action.accept(Board.twoPlayerPosition);
        action.accept(Board.threePlayerPosition);
        action.accept(Board.fourPlayerPosition);

        return res;
    }

    private boolean isEmpty(int row, int col) {
        return this.typeAt(row, col) == null;
    }

    private boolean isFullCoordinates(final int [][] coordinates) {
        return Arrays
                .stream(coordinates)
                .anyMatch(
                        (coordinate) -> isEmpty(
                                coordinate[0],
                                coordinate[1]
                        )
                );
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

    public List<Tile> draw() {
        List<Tile> res = new ArrayList<>();

        this.boardSelector
                .getSelected()
                .forEach((t) -> res.add(this.remove(t.getY(), t.getX())));

        this.boardSelector = new BoardSelector();

        return res;
    }

    private int edgesOccupied (int row, int col) {
        int free = 0;
        final boolean onBorder = this.hasEdgeOnBorder(row, col);
        if (onBorder)
            free ++;

        if (col + 1< Board.columnBoard && this.isEmpty(row, col+1))
                free ++;

        if (row + 1< Board.rowBoard && this.isEmpty(row+1, col))
                free ++;


        if (row != 0 && this.isEmpty(row - 1, col))
                free ++;

        if (col != 0 && this.isEmpty(row, col - 1))
                free++;

        return free;
    }

    private List<Coordinates> getAvailablePositionInsert(int numPlayer) {
        List<Coordinates> res = new ArrayList<>();

        if (this.occupied == 0) {
            Arrays.stream(Board.twoPlayerPosition)
                    .forEach((t) -> res.add(new Coordinates(t[0], t[1])));
            if (numPlayer > 2) {
                Arrays.stream(Board.threePlayerPosition)
                        .forEach((t) -> res.add(new Coordinates(t[0], t[1])));
            }
            if (numPlayer == 4) {
                Arrays.stream(Board.fourPlayerPosition)
                        .forEach((t) -> res.add(new Coordinates(t[0], t[1])));
            }

            return res;
        }

        Consumer<int[][]> action = (int [][] position) -> {
            for (int[] ints : position) {
                final int row = ints[0];
                final int col = ints[1];

                if (this.isEmpty(row, col)) {
                    final int edge = edgesOccupied(row, col);

                    if (edge < 4)
                        res.add(new Coordinates(
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

        final List<Coordinates> possible = this.getAvailablePositionInsert(numPlayer);
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

    public boolean needsRefill() {
        int r, c;

        for (r = 0; r < rowBoard; r++) {
            for (c = 0; c < columnBoard; c++) {
                if (this.typeAt(r, c) != null) {
                    if (freeSide(r, c) == 4) {
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
