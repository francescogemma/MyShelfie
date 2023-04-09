package it.polimi.ingsw.model.bookshelf;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;

class OffsetTest {
    private static List<Arguments> rowOffsetColumnOffsetProvider() {
        List<Arguments> rowOffsetsColumnOffsets = new ArrayList<>();

        for (int startRow = 0; startRow < Bookshelf.ROWS; startRow++) {
            for (int startColumn = 0; startColumn < Bookshelf.COLUMNS; startColumn++) {
                for (int endRow = 0; endRow < Bookshelf.ROWS; endRow++) {
                    for (int endColumn = 0; endColumn < Bookshelf.COLUMNS; endColumn++) {
                        rowOffsetsColumnOffsets.add(Arguments.arguments(endRow - startRow, endColumn -startColumn));
                    }
                }
            }
        }

        return rowOffsetsColumnOffsets;
    }

    private static List<Arguments> firstRowOffsetColumnOffsetSecondRowOffsetColumnOffsetProvider() {
        List<Arguments> firstOffsetsSecondOffsets = new ArrayList<>();

        for (int firstRow = 0; firstRow < Bookshelf.ROWS; firstRow++) {
            for (int firstColumn = 0; firstColumn < Bookshelf.COLUMNS; firstColumn++) {
                for (int secondRow = 0; secondRow < Bookshelf.ROWS; secondRow++) {
                    for (int secondColumn = 0; secondColumn < Bookshelf.COLUMNS; secondColumn++) {
                        for (int thirdRow = 0; thirdRow < Bookshelf.ROWS; thirdRow++) {
                            for (int thirdColumn = 0; thirdColumn < Bookshelf.COLUMNS; thirdColumn++) {
                                firstOffsetsSecondOffsets.add(Arguments.arguments(
                                    secondRow - firstRow, secondColumn - firstColumn,
                                    thirdRow - secondRow, thirdColumn - secondColumn
                                ));
                            }
                        }
                    }
                }
            }
        }

        return firstOffsetsSecondOffsets;
    }

    private static List<Integer> horizontalAmountProvider() {
        List<Integer> amounts = new ArrayList<>();

        for (int amount = -Bookshelf.COLUMNS + 1; amount < Bookshelf.COLUMNS; amount++) {
            amounts.add(amount);
        }

        return amounts;
    }

    private static List<Integer> verticalAmountProvider() {
        List<Integer> amounts = new ArrayList<>();

        for (int amount = -Bookshelf.ROWS + 1; amount < Bookshelf.ROWS; amount++) {
            amounts.add(amount);
        }

        return amounts;
    }

    @ParameterizedTest(name = "row offset = {0}, column offset = {1}")
    @DisplayName("Getting offset instance with ")
    @MethodSource("rowOffsetColumnOffsetProvider")
    void getInstance_correctRowOffsetColumnOffset_correctOutput(int rowOffset, int columnOffset) {
        Offset offset = Offset.getInstance(rowOffset, columnOffset);

        // This test case also exerts the code in getRowOffset and getColumnOffset
        Assertions.assertEquals(rowOffset, offset.getRowOffset());
        Assertions.assertEquals(columnOffset, offset.getColumnOffset());
    }

    @Test
    @DisplayName("Trying to get offset with too big row offset, should throw exception")
    void getInstance_outOfBoundRowOffset_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Offset.getInstance(Bookshelf.ROWS, 0);
        });
    }

    @Test
    @DisplayName("Trying to get offset with too big column offset, should throw exception")
    void getInstance_outOfBoundColumnOffset_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Offset.getInstance(0, Bookshelf.COLUMNS);
        });
    }

    @Test
    @DisplayName("Getting up offset")
    void up_correctOutput() {
        Assertions.assertEquals(Offset.getInstance(-1, 0), Offset.up());
    }

    @ParameterizedTest(name = "vertical amount = {0}")
    @DisplayName("Getting up offset with ")
    @MethodSource("verticalAmountProvider")
    void up_correctVerticalAmount_correctOutput(int verticalAmount) {
        Assertions.assertEquals(Offset.getInstance(-verticalAmount, 0), Offset.up(verticalAmount));
    }

    @Test
    @DisplayName("Trying to get up offset with too big vertical amount, should throw exception")
    void up_tooBigVerticalAmount_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Offset.up(Bookshelf.ROWS);
        });
    }

    @Test
    @DisplayName("Getting down offset")
    void down_correctOutput() {
        Assertions.assertEquals(Offset.getInstance(1, 0), Offset.down());
    }

    @ParameterizedTest(name = "vertical amount = {0}")
    @DisplayName("Getting down offset with ")
    @MethodSource("verticalAmountProvider")
    void down_correctVerticalAmount_correctOutput(int verticalAmount) {
        Assertions.assertEquals(Offset.getInstance(verticalAmount, 0), Offset.down(verticalAmount));
    }

    @Test
    @DisplayName("Trying to get down offset with too big vertical amount, should throw exception")
    void down_tooBigVerticalAmount_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Offset.right(Bookshelf.ROWS);
        });
    }

    @Test
    @DisplayName("Getting left offset")
    void left_correctOutput() {
        Assertions.assertEquals(Offset.getInstance(0, -1), Offset.left());
    }

    @ParameterizedTest(name = "horizontal amount = {0}")
    @DisplayName("Getting left offset with ")
    @MethodSource("horizontalAmountProvider")
    void left_correctHorizontalAmount_correctOutput(int horizontalAmount) {
        Assertions.assertEquals(Offset.getInstance(0, -horizontalAmount), Offset.left(horizontalAmount));
    }

    @Test
    @DisplayName("Trying to get left offset with too big horizontal amount, should throw exception")
    void left_tooBigHorizontalAmount_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Offset.left(Bookshelf.COLUMNS);
        });
    }

    @Test
    @DisplayName("Getting right offset")
    void right_correctOutput() {
        Assertions.assertEquals(Offset.getInstance(0, 1), Offset.right());
    }

    @ParameterizedTest(name = "horizontal amount = {0}")
    @DisplayName("Getting right offset with ")
    @MethodSource("horizontalAmountProvider")
    void right_correctHorizontalAmount_correctOutput(int horizontalAmount) {
        Assertions.assertEquals(Offset.getInstance(0, horizontalAmount), Offset.right(horizontalAmount));
    }

    @Test
    @DisplayName("Trying to get right offset with too big horizontal amount, should throw exception")
    void right_tooBigHorizontalAmount_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Offset.right(Bookshelf.COLUMNS);
        });
    }

    @ParameterizedTest(name = "first row offset = {0}, first column offset = {1}, second row offset = {2}" +
        ", second column offset = {3}")
    @DisplayName("Adding offsets with ")
    @MethodSource("firstRowOffsetColumnOffsetSecondRowOffsetColumnOffsetProvider")
    void add_correctInput_correctOutput(int firstRowOffset, int firstColumnOffset, int secondRowOffset,
                                               int secondColumnOffset) {
        Offset offset = Offset.getInstance(firstRowOffset, firstColumnOffset)
            .add(Offset.getInstance(secondRowOffset, secondColumnOffset));

        Assertions.assertEquals(firstRowOffset + secondRowOffset, offset.getRowOffset());
        Assertions.assertEquals(firstColumnOffset + secondColumnOffset, offset.getColumnOffset());
    }

    @Test
    @DisplayName("Adding two offsets such that the resulting offset doesn't fit in the bookshelf, should throw exception")
    void add_tooBigResultingOffset_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Offset.right(Bookshelf.COLUMNS - 1).add(Offset.right());
        });
    }
}
