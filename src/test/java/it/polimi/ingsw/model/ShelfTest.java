package it.polimi.ingsw.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;


public class ShelfTest {
    private static ArrayList<Arguments> rowColumnProvider() {
        ArrayList<Arguments> rowsColumns = new ArrayList<>();

        for (int row = 0; row < Library.ROWS; row++) {
            for (int column = 0; column < Library.COLUMNS; column++) {
                rowsColumns.add(Arguments.arguments(row, column));
            }
        }

        return rowsColumns;
    }

    private static ArrayList<Arguments> rowColumnOffsetProvider() {
        ArrayList<Arguments> rowsColumnsOffsets = new ArrayList<>();

        for (int startRow = 0; startRow < Library.ROWS; startRow++) {
            for (int startColumn = 0; startColumn < Library.COLUMNS; startColumn++) {
                for (int endRow = 0; endRow < Library.ROWS; endRow++) {
                    for (int endColumn = 0; endColumn < Library.COLUMNS; endColumn++) {
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
    public void getInstance_correctRowColumn_correctOutput(int row, int column) {
        Shelf shelf = Shelf.getInstance(row, column);

        Assertions.assertEquals(shelf.getRow(), row);
        Assertions.assertEquals(shelf.getColumn(), column);
    }

    @Test
    @DisplayName("Trying to get shelf at negative row index, should throw exception")
    public void getInstance_negativeRow_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Shelf.getInstance(-1, 0);
        });
    }

    @Test
    @DisplayName("Trying to get shelf at row index greater than " + (Library.ROWS - 1) +
        ", should throw exception")
    public void getInstance_tooBigRow_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Shelf.getInstance(Library.ROWS, 0);
        });
    }

    @Test
    @DisplayName("Trying to get shelf at negative column index, should throw exception")
    public void getInstance_negativeColumn_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Shelf.getInstance(0, -1);
        });
    }

    @Test
    @DisplayName("Trying to get shelf at column index greater than " + (Library.COLUMNS - 1) +
        ", should throw exception")
    public void getInstance_tooBigColumn_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Shelf.getInstance(0, Library.COLUMNS);
        });
    }

    @Test
    @DisplayName("Getting shelf in the origin")
    public void getOrigin_correctOutput() {
        Shelf shelf = Shelf.origin();

        Assertions.assertEquals(shelf.getRow(), 0);
        Assertions.assertEquals(shelf.getColumn(), 0);
    }

    @ParameterizedTest(name = "row {0}, column {1} by {2}")
    @DisplayName("Moving shelf at ")
    @MethodSource("rowColumnOffsetProvider")
    public void move_correctOffset_correctOutput(int row, int column, Offset offset) {
        Shelf shelf = Shelf.getInstance(row, column).move(offset);

        Assertions.assertEquals(shelf.getRow(), row + offset.getRowOffset());
        Assertions.assertEquals(shelf.getColumn(), column + offset.getColumnOffset());
    }

    @Test
    @DisplayName("Trying to move shelf outside the library, should throw exception")
    public void move_tooBigOffset_throwsRuntimeException() {
        Assertions.assertThrows(RuntimeException.class, () -> {
            Shelf.origin().move(Offset.left());
        });
    }

    @ParameterizedTest(name = "row {0}, column {1}")
    @DisplayName("Getting shelf row from shelf at ")
    @MethodSource("rowColumnProvider")
    public void getRow_correctOutput(int row, int colum) {
        Assertions.assertEquals(Shelf.getInstance(row, colum).getRow(), row);
    }

    @ParameterizedTest(name = "row {0}, column {1}")
    @DisplayName("Getting shelf column for shelf at ")
    @MethodSource("rowColumnProvider")
    public void getColumn_correctOutput(int row, int column) {
        Assertions.assertEquals(Shelf.getInstance(row, column).getColumn(), column);
    }

    @ParameterizedTest(name = "row {0}, column {1} is equal to its translated by {2} or not")
    @DisplayName("Checking if shelf at ")
    @MethodSource("rowColumnOffsetProvider")
    public void equals_sameShelf_correctOutput(int row, int column, Offset offset) {
        Assertions.assertEquals(Shelf.getInstance(row, column)
            .equals(Shelf.getInstance(row, column).move(offset)),
            offset.equals(Offset.getInstance(0, 0)));
    }

    @ParameterizedTest(name = "row {0}, column {1} is before its translated by {2} or not")
    @DisplayName("Checking if shelf at ")
    @MethodSource("rowColumnOffsetProvider")
    public void before_otherShelf_correctOutput(int row, int column, Offset offset) {
        Assertions.assertEquals(Shelf.getInstance(row, column)
            .before(Shelf.getInstance(row, column).move(offset)),
            offset.getRowOffset() > 0 || (offset.getRowOffset() == 0 && offset.getColumnOffset() > 0));
    }
}
