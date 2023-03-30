package it.polimi.ingsw.model.bookshelf;

import it.polimi.ingsw.model.Tile;

import java.util.Arrays;

public class MockBookshelf extends Bookshelf {
    public static Tile indexToTile(int index) {
        if (index == 0) {
            return Tile.EMPTY;
        }

        Tile tile = Tile.EMPTY;
        for (Tile t : Tile.values()) {
            if (t != Tile.EMPTY) {
                tile = t;
                index--;

                if (index == 0) {
                    break;
                }
            }
        }

        if (tile == Tile.EMPTY) {
            throw new IllegalStateException("Result of indexToTile must be empty iff index is 0");
        }

        return tile;
    }

    public static int tileToIndex(Tile tile) {
        if (tile == Tile.EMPTY) {
            return 0;
        }

        int index = 1;
        for (Tile t : Tile.values()) {
            if (t != Tile.EMPTY) {
                if (t == tile) {
                    return index;
                }

                index++;
            }
        }

        throw new IllegalStateException("We must find tile in Tile.values when converting a tile to an index");
    }

    public MockBookshelf(int[][] content) {
        if (content.length != Bookshelf.ROWS) {
            throw new IllegalArgumentException("Mock data inside a mock bookshelf must have " +
                Bookshelf.ROWS + " rows");
        }

        for (int[] row : content) {
            if (row.length != Bookshelf.COLUMNS) {
                throw new IllegalArgumentException("Mock data inside a mock bookshelf must have " +
                    Bookshelf.COLUMNS + " columns");
            }
        }

        for (int row = 0; row < Bookshelf.ROWS; row++) {
            for (int column = 0; column < Bookshelf.COLUMNS; column++) {
                super.content[row][column] = indexToTile(content[row][column]);
            }
        }

        for (int j = 0; j < content[0].length; j++) {
            boolean findNonZero = false;
            for (int[] ints : content) {
                final int value = ints[j];
                if (value != 0) {
                    findNonZero = true;
                } else {
                    assert !findNonZero : ints;
                }
            }
        }
    }
}
