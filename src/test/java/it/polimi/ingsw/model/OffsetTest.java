package it.polimi.ingsw.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;

public class OffsetTest {
    private static ArrayList<Arguments> rowOffsetColumnOffsetProvider() {
        ArrayList<Arguments> rowOffsetsColumnOffsets = new ArrayList<>();

        for (int startRow = 0; startRow < Library.ROWS; startRow++) {
            for (int startColumn = 0; startColumn < Library.COLUMNS; startColumn++) {
                for (int endRow = 0; endRow < Library.ROWS; endRow++) {
                    for (int endColumn = 0; endColumn < Library.COLUMNS; endColumn++) {
                        rowOffsetsColumnOffsets.add(Arguments.arguments(endRow - startRow, endColumn -startColumn));
                    }
                }
            }
        }

        return rowOffsetsColumnOffsets;
    }

    private static ArrayList<Arguments> firstRowOffsetColumnOffsetSecondRowOffsetColumnOffsetProvider() {
        ArrayList<Arguments> firstOffsetsSecondOffsets = new ArrayList<>();

        for (int firstRow = 0; firstRow < Library.ROWS; firstRow++) {
            for (int firstColumn = 0; firstColumn < Library.COLUMNS; firstColumn++) {
                for (int secondRow = 0; secondRow < Library.ROWS; secondRow++) {
                    for (int secondColumn = 0; secondColumn < Library.COLUMNS; secondColumn++) {
                        for (int thirdRow = 0; thirdRow < Library.ROWS; thirdRow++) {
                            for (int thirdColumn = 0; thirdColumn < Library.COLUMNS; thirdColumn++) {
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

    private static ArrayList<Integer> horizontalAmountProvider() {
        ArrayList<Integer> amounts = new ArrayList<>();

        for (int amount = -Library.COLUMNS + 1; amount < Library.COLUMNS; amount++) {
            amounts.add(amount);
        }

        return amounts;
    }

    private static ArrayList<Integer> verticalAmountProvider() {
        ArrayList<Integer> amounts = new ArrayList<>();

        for (int amount = -Library.ROWS + 1; amount < Library.ROWS; amount++) {
            amounts.add(amount);
        }

        return amounts;
    }

    @ParameterizedTest(name = "row offset = {0}, column offset = {1}")
    @DisplayName("Getting offset instance with ")
    @MethodSource("rowOffsetColumnOffsetProvider")
    public void getInstance_correctRowOffsetColumnOffset_correctOutput(int rowOffset, int columnOffset) {
        Offset offset = Offset.getInstance(rowOffset, columnOffset);

        // This test case also exerts the code in getRowOffset and getColumnOffset
        Assertions.assertEquals(rowOffset, offset.getRowOffset());
        Assertions.assertEquals(columnOffset, offset.getColumnOffset());
    }

    @Test
    @DisplayName("Trying to get offset with too big row offset, should throw exception")
    public void getInstance_outOfBoundRowOffset_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Offset.getInstance(Library.ROWS, 0);
        });
    }

    @Test
    @DisplayName("Trying to get offset with too big column offset, should throw exception")
    public void getInstance_outOfBoundColumnOffset_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Offset.getInstance(0, Library.COLUMNS);
        });
    }

    @Test
    @DisplayName("Getting up offset")
    public void up_correctOutput() {
        Assertions.assertEquals(Offset.getInstance(-1, 0), Offset.up());
    }

    @ParameterizedTest(name = "vertical amount = {0}")
    @DisplayName("Getting up offset with ")
    @MethodSource("verticalAmountProvider")
    public void up_correctVerticalAmount_correctOutput(int verticalAmount) {
        Assertions.assertEquals(Offset.getInstance(-verticalAmount, 0), Offset.up(verticalAmount));
    }

    @Test
    @DisplayName("Trying to get up offset with too big vertical amount, should throw exception")
    public void up_tooBigVerticalAmount_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Offset.up(Library.ROWS);
        });
    }

    @Test
    @DisplayName("Getting down offset")
    public void down_correctOutput() {
        Assertions.assertEquals(Offset.getInstance(1, 0), Offset.down());
    }

    @ParameterizedTest(name = "vertical amount = {0}")
    @DisplayName("Getting down offset with ")
    @MethodSource("verticalAmountProvider")
    public void down_correctVerticalAmount_correctOutput(int verticalAmount) {
        Assertions.assertEquals(Offset.getInstance(verticalAmount, 0), Offset.down(verticalAmount));
    }

    @Test
    @DisplayName("Trying to get down offset with too big vertical amount, should throw exception")
    public void down_tooBigVerticalAmount_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Offset.right(Library.ROWS);
        });
    }

    @Test
    @DisplayName("Getting left offset")
    public void left_correctOutput() {
        Assertions.assertEquals(Offset.getInstance(0, -1), Offset.left());
    }

    @ParameterizedTest(name = "horizontal amount = {0}")
    @DisplayName("Getting left offset with ")
    @MethodSource("horizontalAmountProvider")
    public void left_correctHorizontalAmount_correctOutput(int horizontalAmount) {
        Assertions.assertEquals(Offset.getInstance(0, -horizontalAmount), Offset.left(horizontalAmount));
    }

    @Test
    @DisplayName("Trying to get left offset with too big horizontal amount, should throw exception")
    public void left_tooBigHorizontalAmount_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Offset.left(Library.COLUMNS);
        });
    }

    @Test
    @DisplayName("Getting right offset")
    public void right_correctOutput() {
        Assertions.assertEquals(Offset.getInstance(0, 1), Offset.right());
    }

    @ParameterizedTest(name = "horizontal amount = {0}")
    @DisplayName("Getting right offset with ")
    @MethodSource("horizontalAmountProvider")
    public void right_correctHorizontalAmount_correctOutput(int horizontalAmount) {
        Assertions.assertEquals(Offset.getInstance(0, horizontalAmount), Offset.right(horizontalAmount));
    }

    @Test
    @DisplayName("Trying to get right offset with too big horizontal amount, should throw exception")
    public void right_tooBigHorizontalAmount_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Offset.right(Library.COLUMNS);
        });
    }

    @ParameterizedTest(name = "first row offset = {0}, first column offset = {1}, second row offset = {2}" +
        ", second column offset = {3}")
    @DisplayName("Adding offsets with ")
    @MethodSource("firstRowOffsetColumnOffsetSecondRowOffsetColumnOffsetProvider")
    public void add_correctInput_correctOutput(int firstRowOffset, int firstColumnOffset, int secondRowOffset,
                                               int secondColumnOffset) {
        Offset offset = Offset.getInstance(firstRowOffset, firstColumnOffset)
            .add(Offset.getInstance(secondRowOffset, secondColumnOffset));

        Assertions.assertEquals(firstRowOffset + secondRowOffset, offset.getRowOffset());
        Assertions.assertEquals(firstColumnOffset + secondColumnOffset, offset.getColumnOffset());
    }

    @Test
    @DisplayName("Adding two offsets such that the resulting offset doesn't fit in the library, should throw exception")
    public void add_tooBigResultingOffset_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Offset.right(Library.COLUMNS - 1).add(Offset.right());
        });
    }
}
