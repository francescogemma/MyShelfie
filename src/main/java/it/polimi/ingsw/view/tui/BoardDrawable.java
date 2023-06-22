package it.polimi.ingsw.view.tui;

import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.tile.TileColor;
import it.polimi.ingsw.view.tui.terminal.drawable.*;
import it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.OrientedLayout;
import it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.OrientedLayoutElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Drawable which allows to display the game board.
 * It is a 9 x 9 square of {@link TileDrawable} (some of these tiles are hidden).
 *
 * @author Cristiano Migali
 */
public class BoardDrawable extends FixedLayoutDrawable<OrientedLayout> {
    /**
     * Bitmask which indicates the tiles that are actually in the game board from the ones that serve merely to fill
     * the free space, ensuring that the visible board has the right shape.
     */
    private final int[][] fillTilesMap = new int[][]{
        { 0, 0, 0, 1, 1, 0, 0, 0, 0 },
        { 0, 0, 0, 1, 1, 1, 0, 0, 0 },
        { 0, 0, 1, 1, 1, 1, 1, 0, 0 },
        { 0, 1, 1, 1, 1, 1, 1, 1, 1 },
        { 1, 1, 1, 1, 1, 1, 1, 1, 1 },
        { 1, 1, 1, 1, 1, 1, 1, 1, 0 },
        { 0, 0, 1, 1, 1, 1, 1, 0, 0 },
        { 0, 0, 0, 1, 1, 1, 0, 0, 0 },
        { 0, 0, 0, 0, 1, 1, 0, 0, 0 }
    };

    /**
     * Underlying {@link OrientedLayout} used to build the board by stacking several {@link TileDrawable}.
     */
    private final OrientedLayout layout;

    /**
     * Constructor of the class.
     * It initializes the underlying {@link OrientedLayout}, stacking all the required {@link TileDrawable}.
     */
    public BoardDrawable() {
        OrientedLayoutElement[] rows = new OrientedLayoutElement[Board.BOARD_ROWS];
        for (int row = 0; row < Board.BOARD_ROWS; row++) {
            OrientedLayoutElement[] columns = new OrientedLayoutElement[Board.COLUMN_BOARDS];
            for (int column = 0; column < Board.COLUMN_BOARDS; column++) {
                columns[column] = new TileDrawable(fillTilesMap[row][column] == 0,
                    row, column).color(TileColor.EMPTY).weight(1);
            }
            rows[row] = new OrientedLayout(Orientation.HORIZONTAL, columns).weight(1);
        }

        layout = new OrientedLayout(Orientation.VERTICAL, rows);

        setLayout(layout);
    }

    @Override
    public void askForSize(DrawableSize desiredSize) {
        int lines;
        int columns;

        if (desiredSize.getLines() < Board.BOARD_ROWS * TileDrawable.MEDIUM_SIDE ||
            desiredSize.getColumns() < Board.COLUMN_BOARDS * TileDrawable.MEDIUM_SIDE) {
            lines = Board.BOARD_ROWS * TileDrawable.SMALL_SIDE;
            columns = Board.COLUMN_BOARDS * TileDrawable.SMALL_SIDE;
        } else if (desiredSize.getLines() < Board.BOARD_ROWS * TileDrawable.LARGE_SIDE ||
            desiredSize.getColumns() < Board.COLUMN_BOARDS * TileDrawable.LARGE_SIDE) {
            lines = Board.BOARD_ROWS * TileDrawable.MEDIUM_SIDE;
            columns = Board.COLUMN_BOARDS * TileDrawable.MEDIUM_SIDE;
        } else {
            lines = Board.BOARD_ROWS * TileDrawable.LARGE_SIDE;
            columns = Board.COLUMN_BOARDS * TileDrawable.LARGE_SIDE;
        }

        size = new DrawableSize(lines, columns);

        layout.askForSize(size);
    }

    /**
     * @param row is the row number of a coordinate in the board.
     * @param column is the column number of a coordinate in the board.
     * @return the {@link TileDrawable} at the specified coordinate in the board.
     *
     * @throws IllegalArgumentException if the coordinate specified through row and column number is outside of
     * the board.
     */
    public TileDrawable getTileDrawableAt(int row, int column) {
        if (row < 0 || row >= Board.BOARD_ROWS) {
            throw new IllegalArgumentException("A board row must be between 0 and " + (Board.BOARD_ROWS - 1));
        }

        if (column < 0 || column >= Board.COLUMN_BOARDS) {
            throw new IllegalArgumentException("A board column must be between 0 and " + (Board.COLUMN_BOARDS - 1));
        }

        return (TileDrawable) ((OrientedLayout) layout.getElements()
                    .get(row).getDrawable()).getElements().get(column).getDrawable();
    }

    /**
     * @return a list with all the {@link TileDrawable} in the board which are actually tiles of the game board,
     * conversely to the ones used to fill the empty space in the square to guarantee the alignment of actual tiles.
     */
    public List<TileDrawable> getNonFillTileDrawables() {
        List<TileDrawable> nonFillTilesDrawables = new ArrayList<>();

        for (int row = 0; row < Board.BOARD_ROWS; row++) {
            for (int column = 0; column < Board.COLUMN_BOARDS; column++) {
                if (fillTilesMap[row][column] == 1) {
                    nonFillTilesDrawables.add(getTileDrawableAt(row, column));
                }
            }
        }

        return nonFillTilesDrawables;
    }
}
