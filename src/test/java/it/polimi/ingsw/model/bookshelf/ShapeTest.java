package it.polimi.ingsw.model.bookshelf;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ShapeTest {
    private final static int NUM_OF_SHAPES_TO_GENERATE = 10000;

    public static List<Arguments> offsetsHeightWidthProvider() {
        Random random = new Random(315346);

        List<Arguments> offsetsListsHeightsWidths = new ArrayList<>();

        for (int i = 0; i < NUM_OF_SHAPES_TO_GENERATE; i++) {
            int height = random.nextInt(Bookshelf.ROWS) + 1;
            int width = random.nextInt(Bookshelf.COLUMNS) + 1;

            /*
             * We need at least 2 shelves to "force" height and width of the shape by placing
             * the first in the top-left corner and the second in the down-right one.
             * Instead, if the shape has height and width equal to 1, we need just a shelf.
             */
            int shelvesToTake = height * width == 1 ? 1 : random.nextInt(height * width - 1) + 2;

            boolean[] takenShelves = new boolean[height * width];
            Arrays.fill(takenShelves, false);

            takenShelves[0] = true;
            shelvesToTake--;

            if (height * width  > 1) {
                takenShelves[height * width - 1] = true;
                shelvesToTake--;
            }

            List<Integer> availableIndexes = new ArrayList<>();
            for (int j = 1; j < height * width - 1; j++) {
                availableIndexes.add(j);
            }

            while (shelvesToTake > 0) {
                takenShelves[availableIndexes.remove(random.nextInt(availableIndexes.size()))] = true;

                shelvesToTake--;
            }

            List<Offset> offsets = new ArrayList<>();
            for (int row = 0; row < height; row++) {
                for (int column = 0; column < width; column++) {
                    if (takenShelves[row * width + column]) {
                        offsets.add(Offset.getInstance(row, column));
                    }
                }
            }

            offsetsListsHeightsWidths.add(Arguments.arguments(offsets, height, width));
        }

        return offsetsListsHeightsWidths;
    }

    private static List<Integer> heightProvider() {
        List<Integer> heights = new ArrayList<>();

        for (int i = 1; i <= Bookshelf.ROWS; i++) {
            heights.add(i);
        }

        return heights;
    }

    private static List<Integer> widthProvider() {
        List<Integer> widths = new ArrayList<>();

        for (int i = 1; i <= Bookshelf.COLUMNS; i++) {
            widths.add(i);
        }

        return widths;
    }

    private static List<Integer> sizeProvider() {
        return widthProvider().size() > heightProvider().size() ? heightProvider() : widthProvider();
    }

    @Test
    @DisplayName("Construct a shape with a correct list of offsets")
    void constructor_correctOffsets_correctOutput() {
        List<Offset> offsets = List.of(Offset.getInstance(0, 1),
            Offset.getInstance(1, 0), Offset.getInstance(1, 1), Offset.getInstance(1, 2),
            Offset.getInstance(2, 0), Offset.getInstance(2, 2));

        Shape shape = new Shape(offsets);

        // This test case also exerts getOffsets code
        Assertions.assertEquals(offsets, shape.getOffsets());
    }

    @Test
    @DisplayName("Try to construct a shape with a null offsets list, should throw exception")
    void constructor_nullOffsets_throwsNullPointerException() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            new Shape(null);
        });
    }

    @Test
    @DisplayName("Try to construct a shape with an empty offsets list, should throw exception")
    void constructor_emptyOffsets_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new Shape(List.of());
        });
    }

    @Test
    @DisplayName("Try to construct a shape with an offsets list that has non-monotonic row offsets, " +
        "should throw exception")
    void constructor_nonMonotonicRowOffsets_throwsIllegalArgumentException() {
        List<Offset> offsets = List.of(
            Offset.getInstance(0, 0), Offset.getInstance(1, 0),
            Offset.getInstance(0, 1)
        );

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new Shape(offsets);
        });
    }

    @Test
    @DisplayName("Try to construct a shape with an offsets list that has non-monotonic column offsets " +
        "in the same row, should throw exception")
    void constructor_nonMonotonicColumnInSameRowOffsets_throwsIllegalArgumentException() {
        List<Offset> offsets = List.of(
            Offset.getInstance(0, 0), Offset.getInstance(1, 0),
            Offset.getInstance(1, 2), Offset.getInstance(1, 1)
        );

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new Shape(offsets);
        });
    }

    @Test
    @DisplayName("Try to construct a shape with an offsets list that has duplicated offsets in adjacent positions, " +
        "should throw exception")
    void constructor_duplicatedOffsets_throwsIllegalArgumentException() {
        List<Offset> offsets = List.of(
            Offset.getInstance(0, 0), Offset.getInstance(1, 0),
            Offset.getInstance(1, 1), Offset.getInstance(1, 1)
        );

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new Shape(offsets);
        });
    }

    @Test
    @DisplayName("Try to construct a shape with an offsets list that is not relative to the top-left shelf of the " +
        "bounding box instance (with positive offsets), should throw exception")
    void construct_nonTopLeftDefinedPositiveOffsets_throwsIllegalArgumentException() {
        List<Offset> offsets = List.of(
            Offset.getInstance(0, 1), Offset.getInstance(1, 1),
            Offset.getInstance(1, 2)
        );

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new Shape(offsets);
        });
    }

    @Test
    @DisplayName("Try to construct a shape with an offsets list that is not relative to the top-left shelf of the " +
        "bounding box instance (with negative offsets), should throw exception")
    void construct_nonTopLeftDefinedNegativeOffsets_throwsIllegalArgumentException() {
        List<Offset> offsets = List.of(
            Offset.getInstance(0, -1), Offset.getInstance(0, 0),
            Offset.getInstance(1, -1)
        );

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new Shape(offsets);
        });
    }

    @ParameterizedTest
    @DisplayName("Get width of random generated shape")
    @MethodSource("offsetsHeightWidthProvider")
    void getHeight_correctOutput(List<Offset> offsets, int height, int width) {
        Assertions.assertEquals(height, new Shape(offsets).getHeight());
    }

    @ParameterizedTest
    @DisplayName("Get width of random generated shape")
    @MethodSource("offsetsHeightWidthProvider")
    void getWidth_correctOutput(List<Offset> offsets, int height, int width) {
        Assertions.assertEquals(width, new Shape(offsets).getWidth());
    }

    @ParameterizedTest
    @DisplayName("Vertical flip of random generated shape")
    @MethodSource("offsetsHeightWidthProvider")
    void verticalFlip_correctOutput(List<Offset> offsets, int height, int width) {
        Shape originalShape = new Shape(offsets);
        Shape flippedShape = originalShape.verticalFlip();

        Assertions.assertEquals(height, flippedShape.getHeight());
        Assertions.assertEquals(width, flippedShape.getWidth());

        // This test case also exerts equals code
        Assertions.assertEquals(originalShape, flippedShape.verticalFlip());

        int[] originalShapeTakenShelves = new int[height * width];
        Arrays.fill(originalShapeTakenShelves, 0);

        for (Offset offset : originalShape.getOffsets()) {
            int currentShelfRow = Shelf.origin().move(offset).getRow();
            int currentShelfColumn = Shelf.origin().move(offset).getColumn();

            originalShapeTakenShelves[currentShelfRow * width + currentShelfColumn] += 1;
        }

        for (Offset offset : flippedShape.getOffsets()) {
            int currentFlippedShelfRow = Shelf.origin().move(offset).getRow();
            int currentFlippedShelfColumn = Shelf.origin().move(offset).getColumn();

            originalShapeTakenShelves[currentFlippedShelfRow * width +
                (width - 1 - currentFlippedShelfColumn)] -= 1;
        }

        for (int taken : originalShapeTakenShelves) {
            Assertions.assertEquals(0, taken);
        }
    }

    @ParameterizedTest(name = "height = {0}")
    @DisplayName("Get column with ")
    @MethodSource("heightProvider")
    void getColumn_correctHeight_correctOutput(int height) {
        Shape column = Shape.getColumn(height);

        Assertions.assertEquals(height, column.getHeight());
        Assertions.assertEquals(1, column.getWidth());

        boolean[] takenShelf = new boolean[height];
        Arrays.fill(takenShelf, false);

        for (Offset offset : column.getOffsets()) {
            takenShelf[Shelf.origin().move(offset).getRow()] = true;
        }

        for (boolean taken : takenShelf) {
            Assertions.assertTrue(taken);
        }
    }

    @Test
    @DisplayName("Try to get column with negative height, should throw exception")
    void getColumn_negativeHeight_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Shape.getColumn(-1);
        });
    }

    @Test
    @DisplayName("Try to get column which doesn't fit inside the bookshelf, should throw exception")
    void getColumn_tooBigHeight_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Shape.getColumn(Bookshelf.ROWS + 1);
        });
    }

    @ParameterizedTest(name = "size = {0}")
    @DisplayName("Get main diagonal with ")
    @MethodSource("sizeProvider")
    void getMainDiagonal_correctSize_correctSize(int size) {
        Shape mainDiagonal = Shape.getMainDiagonal(size);

        Assertions.assertEquals(size, mainDiagonal.getHeight());
        Assertions.assertEquals(size, mainDiagonal.getWidth());

        boolean[] takenShelf = new boolean[size];
        Arrays.fill(takenShelf, false);

        for (Offset offset : mainDiagonal.getOffsets()) {
            Shelf currentShelf = Shelf.origin().move(offset);

            Assertions.assertEquals(currentShelf.getColumn(), currentShelf.getRow());

            takenShelf[currentShelf.getRow()] = true;
        }

        for (boolean taken : takenShelf) {
            Assertions.assertTrue(taken);
        }
    }

    @Test
    @DisplayName("Try to get main diagonal with negative size, should throw exception")
    void getMainDiagonal_negativeSize_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
           Shape.getMainDiagonal(-1);
        });
    }

    @Test
    @DisplayName("Try to get main diagonal with too big size, should throw exception")
    void getMainDiagonal_tooBigSize_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Shape.getMainDiagonal(Math.max(Bookshelf.COLUMNS, Bookshelf.ROWS) + 1);
        });
    }

    @ParameterizedTest(name = "width = {0}")
    @DisplayName("Get row with ")
    @MethodSource("widthProvider")
    void getRow_correctWidth_correctOutput(int width) {
        Shape row = Shape.getRow(width);

        Assertions.assertEquals(1, row.getHeight());
        Assertions.assertEquals(width, row.getWidth());

        boolean[] takenShelf = new boolean[width];
        Arrays.fill(takenShelf, false);

        for (Offset offset : row.getOffsets()) {
            takenShelf[Shelf.origin().move(offset).getColumn()] = true;
        }

        for (boolean taken : takenShelf) {
            Assertions.assertTrue(taken);
        }
    }

    @Test
    @DisplayName("Try to get row with negative width, should throw exception")
    void getRow_negativeWidth_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Shape.getRow(-1);
        });
    }

    @Test
    @DisplayName("Try to get row with too big width, should throw exception")
    void getRow_tooBigWidth_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Shape.getRow(Bookshelf.COLUMNS + 1);
        });
    }

    @Test
    @DisplayName("Check if a shape is equal to null")
    void equals_null_false() {
        Assertions.assertFalse(Shape.X.equals(null));
    }

    @Test
    @DisplayName("Check if a shape is equal to an object which is not a shape")
    void equals_NotAShape_false() {
        Assertions.assertFalse(Shape.SQUARE.equals(new Object()));
    }
}
