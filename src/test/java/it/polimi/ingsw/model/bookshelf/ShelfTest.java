package it.polimi.ingsw.model.bookshelf;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;


class ShelfTest {
    private static List<Arguments> rowColumnProvider() {
        List<Arguments> rowsColumns = new ArrayList<>();

        for (int row = 0; row < Bookshelf.ROWS; row++) {
            for (int column = 0; column < Bookshelf.COLUMNS; column++) {
                rowsColumns.add(Arguments.arguments(row, column));
            }
        }

        return rowsColumns;
    }

    private static List<Arguments> rowColumnOffsetProvider() {
        List<Arguments> rowsColumnsOffsets = new ArrayList<>();

        for (int startRow = 0; startRow < Bookshelf.ROWS; startRow++) {
            for (int startColumn = 0; startColumn < Bookshelf.COLUMNS; startColumn++) {
                for (int endRow = 0; endRow < Bookshelf.ROWS; endRow++) {
                    for (int endColumn = 0; endColumn < Bookshelf.COLUMNS; endColumn++) {
                        rowsColumnsOffsets.add(Arguments.arguments(startRow, startColumn,
                            Offset.getInstance(endRow - startRow, endColumn - startColumn)));
                    }
                }
            }
        }

        return rowsColumnsOffsets;
    }

    @ParameterizedTest(name = "row {0}, column {1}")
    @DisplayName("Getting shelf instance at ")
    @MethodSource("rowColumnProvider")
    void getInstance_correctRowColumn_correctOutput(int row, int column) {
        Shelf shelf = Shelf.getInstance(row, column);

        // This test case also exerts the code in getRow and getColumn
        Assertions.assertEquals(row, shelf.getRow());
        Assertions.assertEquals(column, shelf.getColumn());
    }

    @Test
    @DisplayName("Trying to get shelf at negative row index, should throw exception")
    void getInstance_negativeRow_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Shelf.getInstance(-1, 0);
        });
    }

    @Test
    @DisplayName("Trying to get shelf at row index greater than " + (Bookshelf.ROWS - 1) +
        ", should throw exception")
    void getInstance_tooBigRow_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Shelf.getInstance(Bookshelf.ROWS, 0);
        });
    }

    @Test
    @DisplayName("Trying to get shelf at negative column index, should throw exception")
    void getInstance_negativeColumn_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Shelf.getInstance(0, -1);
        });
    }

    @Test
    @DisplayName("Trying to get shelf at column index greater than " + (Bookshelf.COLUMNS - 1) +
        ", should throw exception")
    void getInstance_tooBigColumn_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Shelf.getInstance(0, Bookshelf.COLUMNS);
        });
    }

    @Test
    @DisplayName("Getting shelf in the origin")
    void getOrigin_correctOutput() {
        Shelf shelf = Shelf.origin();

        Assertions.assertEquals(0, shelf.getRow());
        Assertions.assertEquals(0, shelf.getColumn());
    }

    @ParameterizedTest(name = "row {0}, column {1} by {2}")
    @DisplayName("Moving shelf at ")
    @MethodSource("rowColumnOffsetProvider")
    void move_correctOffset_correctOutput(int row, int column, Offset offset) {
        Shelf shelf = Shelf.getInstance(row, column).move(offset);

        Assertions.assertEquals(row + offset.getRowOffset(), shelf.getRow());
        Assertions.assertEquals(column + offset.getColumnOffset(), shelf.getColumn());
    }

    @Test
    @DisplayName("Trying to move shelf outside the bookshelf, should throw exception")
    void move_tooBigOffset_throwsRuntimeException() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            Shelf.origin().move(Offset.left());
        });
    }

    @ParameterizedTest(name = "row {0}, column {1} is equal to its translated by {2} or not")
    @DisplayName("Checking if shelf at ")
    @MethodSource("rowColumnOffsetProvider")
    void equals_sameShelf_correctOutput(int row, int column, Offset offset) {
        Assertions.assertEquals(offset.equals(Offset.getInstance(0, 0)),
            Shelf.getInstance(row, column)
            .equals(Shelf.getInstance(row, column).move(offset)));
    }

    @ParameterizedTest(name = "row {0}, column {1} is before its translated by {2} or not")
    @DisplayName("Checking if shelf at ")
    @MethodSource("rowColumnOffsetProvider")
    void before_otherShelf_correctOutput(int row, int column, Offset offset) {
        Assertions.assertEquals(offset.getRowOffset() > 0 || (offset.getRowOffset() == 0 && offset.getColumnOffset() > 0),
            Shelf.getInstance(row, column)
            .before(Shelf.getInstance(row, column).move(offset)));
    }
}
