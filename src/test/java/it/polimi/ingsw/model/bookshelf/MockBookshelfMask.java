package it.polimi.ingsw.model.bookshelf;

public class MockBookshelfMask extends BookshelfMask {
    public MockBookshelfMask(Bookshelf bookshelf, int[][] content) {
        super(bookshelf);

        if (content.length != Bookshelf.ROWS) {
            throw new IllegalArgumentException("Mock data inside a mock bookshelf mask must have " +
                    Bookshelf.ROWS + " rows");
        }

        for (int[] row : content) {
            if (row.length != Bookshelf.COLUMNS) {
                throw new IllegalArgumentException("Mock data inside a mock bookshelf mask must have " +
                        Bookshelf.COLUMNS + " columns");
            }
        }

        for (int row = 0; row < Bookshelf.ROWS; row++) {
            for (int column = 0; column < Bookshelf.COLUMNS; column++) {
                if(content[row][column] != 0 && content[row][column] != 1) {
                    throw new IllegalArgumentException("Mock data inside a mock bookshelf mask must be 0 or 1");
                }
                if(content[row][column] == 1) {
                    super.add(Shelf.getInstance(row, column));
                }
            }
        }
    }
}
