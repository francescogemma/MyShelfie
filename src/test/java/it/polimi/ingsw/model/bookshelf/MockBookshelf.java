package it.polimi.ingsw.model.bookshelf;

import it.polimi.ingsw.model.tile.Tile;
import it.polimi.ingsw.model.tile.TileColor;
import it.polimi.ingsw.model.tile.TileVersion;

public class MockBookshelf extends Bookshelf {
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
                super.content[row][column] = Tile.getInstance(TileColor.indexToTileColor(content[row][column]),
                    TileVersion.FIRST);
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
