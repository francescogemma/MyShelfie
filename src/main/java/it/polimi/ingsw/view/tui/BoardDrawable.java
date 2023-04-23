package it.polimi.ingsw.view.tui;

import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.tile.TileColor;
import it.polimi.ingsw.view.tui.terminal.drawable.*;
import it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.OrientedLayout;
import it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.OrientedLayoutElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BoardDrawable extends FixedLayoutDrawable<OrientedLayout> {
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

    private final OrientedLayout layout;

    public BoardDrawable() {
        OrientedLayoutElement[] rows = new OrientedLayoutElement[Board.BOARD_ROWS];
        for (int row = 0; row < Board.BOARD_ROWS; row++) {
            OrientedLayoutElement[] columns = new OrientedLayoutElement[Board.COLUMN_BOARDS];
            for (int column = 0; column < Board.COLUMN_BOARDS; column++) {
                columns[column] = new TileDrawable(fillTilesMap[row][column] == 0,
                    row, column).color(TileColor.values()[new Random().nextInt(7)]).weight(1);
            }
            rows[row] = new OrientedLayout(Orientation.HORIZONTAL, columns).weight(1);
        }

        layout = new OrientedLayout(Orientation.VERTICAL, rows);

        setLayout(layout);
    }

    @Override
    public void askForSize(DrawableSize desiredSize) {
        int lines = -1;
        int columns = -1;

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
