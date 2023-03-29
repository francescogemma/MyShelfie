package it.polimi.ingsw.model.bookshelf;

import it.polimi.ingsw.model.Tile;

import java.util.Arrays;

public class MockBookshelf extends Bookshelf {
    private static Tile indexToTile(int index) {
        if (index == 0) {
            return Tile.EMPTY;
        }

        Tile result = Tile.GREEN;
        boolean foundEmpty = false;
        int i = 0;
        while (i + (foundEmpty ? -1 : 0) < index) {
            if (Tile.values()[i] != Tile.EMPTY) {
                result = Tile.values()[i];
            } else {
                foundEmpty = true;
            }

            i++;
        }

        return result;
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
            for (int i = 0; i < content.length; i++) {
                final int value = content[i][j];
                if (value != 0) {
                    findNonZero = true;
                } else {
                    assert !findNonZero: content[i];
                }
            }
        }

        /*for (int[] ints : content) {
            boolean findNonZero = false;
            for (int value : ints) {
                if (value != 0) {
                    findNonZero = true;
                } else {
                    assert !findNonZero : ints;
                }
            }
        }*/
    }
}
