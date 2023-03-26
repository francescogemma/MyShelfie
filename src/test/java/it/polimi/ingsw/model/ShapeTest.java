package it.polimi.ingsw.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class ShapeTest {
    private final static int NUM_OF_SHAPES_TO_GENERATE = 10000;

    public static ArrayList<Arguments> offsetsHeightWidthProvider() {
        Random random = new Random(315346);

        ArrayList<Arguments> offsetsListsHeightsWidths = new ArrayList<>();

        for (int i = 0; i < NUM_OF_SHAPES_TO_GENERATE; i++) {
            int height = random.nextInt(Library.ROWS) + 1;
            int width = random.nextInt(Library.COLUMNS) + 1;

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

            ArrayList<Integer> availableIndexes = new ArrayList<>();
            for (int j = 1; j < height * width - 1; j++) {
                availableIndexes.add(j);
            }

            while (shelvesToTake > 0) {
                takenShelves[availableIndexes.remove(random.nextInt(availableIndexes.size()))] = true;

                shelvesToTake--;
            }

            ArrayList<Offset> offsets = new ArrayList<>();
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

    private static ArrayList<Integer> heightProvider() {
        ArrayList<Integer> heights = new ArrayList<>();

        for (int i = 1; i <= Library.ROWS; i++) {
            heights.add(i);
        }

        return heights;
    }

    private static ArrayList<Integer> widthProvider() {
        ArrayList<Integer> widths = new ArrayList<>();

        for (int i = 1; i <= Library.COLUMNS; i++) {
            widths.add(i);
        }

        return widths;
    }

    private static ArrayList<Integer> sizeProvider() {
        return widthProvider().size() > heightProvider().size() ? heightProvider() : widthProvider();
    }

    @Test
    @DisplayName("Construct a shape with a correct list of offsets")
    public void constructor_correctOffsets_correctOutput() {
        ArrayList<Offset> offsets = new ArrayList<>(Arrays.asList(Offset.getInstance(0, 1),
            Offset.getInstance(1, 0), Offset.getInstance(1, 1), Offset.getInstance(1, 2),
            Offset.getInstance(2, 0), Offset.getInstance(2, 2)));

        Shape shape = new Shape(offsets);

        // This test case also exerts getOffsets code
        Assertions.assertEquals(offsets, shape.getOffsets());
    }

    @Test
    @DisplayName("Try to construct a shape with a null offsets list, should throw exception")
    public void constructor_nullOffsets_throwsNullPointerException() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            new Shape(null);
        });
    }

    @Test
    @DisplayName("Try to construct a shape with an empty offsets list, should throw exception")
    public void constructor_emptyOffsets_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new Shape(new ArrayList<>());
        });
    }

    @Test
    @DisplayName("Try to construct a shape with an offsets list that has non-monotonic row offsets, " +
        "should throw exception")
    public void constructor_nonMonotonicRowOffsets_throwsIllegalArgumentException() {
        ArrayList<Offset> offsets = new ArrayList<>(Arrays.asList(
            Offset.getInstance(0, 0), Offset.getInstance(1, 0),
            Offset.getInstance(0, 1)
        ));

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new Shape(offsets);
        });
    }

    @Test
    @DisplayName("Try to construct a shape with an offsets list that has non-monotonic column offsets " +
        "in the same row, should throw exception")
    public void constructor_nonMonotonicColumnInSameRowOffsets_throwsIllegalArgumentException() {
        ArrayList<Offset> offsets = new ArrayList<>(Arrays.asList(
            Offset.getInstance(0, 0), Offset.getInstance(1, 0),
            Offset.getInstance(1, 2), Offset.getInstance(1, 1)
        ));

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new Shape(offsets);
        });
    }

    @Test
    @DisplayName("Try to construct a shape with an offsets list that has duplicated offsets in adjacent positions, " +
        "should throw exception")
    public void constructor_duplicatedOffsets_throwsIllegalArgumentException() {
        ArrayList<Offset> offsets = new ArrayList<>(Arrays.asList(
            Offset.getInstance(0, 0), Offset.getInstance(1, 0),
            Offset.getInstance(1, 1), Offset.getInstance(1, 1)
        ));

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new Shape(offsets);
        });
    }

    @Test
    @DisplayName("Try to construct a shape with an offsets list that is not relative to the top-left shelf of the " +
        "bounding box instance, should throw exception")
    public void construct_nonTopLeftDefinedOffsets_throwsIllegalArgumentException() {
        ArrayList<Offset> offsets = new ArrayList<>(Arrays.asList(
            Offset.getInstance(0, 1), Offset.getInstance(1, 1),
            Offset.getInstance(1, 2)
        ));

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new Shape(offsets);
        });
    }

    @ParameterizedTest
    @DisplayName("Get width of random generated shape")
    @MethodSource("offsetsHeightWidthProvider")
    public void getHeight_correctOutput(ArrayList<Offset> offsets, int height, int width) {
        Assertions.assertEquals(height, new Shape(offsets).getHeight());
    }

    @ParameterizedTest
    @DisplayName("Get width of random generated shape")
    @MethodSource("offsetsHeightWidthProvider")
    public void getWidth_correctOutput(ArrayList<Offset> offsets, int height, int width) {
        Assertions.assertEquals(width, new Shape(offsets).getWidth());
    }

    @ParameterizedTest
    @DisplayName("Vertical flip of random generated shape")
    @MethodSource("offsetsHeightWidthProvider")
    public void verticalFlip_correctOutput(ArrayList<Offset> offsets, int height, int width) {
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
    public void getColumn_correctHeight_correctOutput(int height) {
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
    public void getColumn_negativeHeight_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Shape.getColumn(-1);
        });
    }

    @Test
    @DisplayName("Try to get column which doesn't fit inside the library, should throw exception")
    public void getColumn_tooBigHeight_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Shape.getColumn(Library.ROWS + 1);
        });
    }

    @ParameterizedTest(name = "size = {0}")
    @DisplayName("Get main diagonal with ")
    @MethodSource("sizeProvider")
    public void getMainDiagonal_correctSize_correctSize(int size) {
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
    public void getMainDiagonal_negativeSize_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
           Shape.getMainDiagonal(-1);
        });
    }

    @Test
    @DisplayName("Try to get main diagonal with too big size, should throw exception")
    public void getMainDiagonal_tooBigSize_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Shape.getMainDiagonal(Math.max(Library.COLUMNS, Library.ROWS) + 1);
        });
    }

    @ParameterizedTest(name = "width = {0}")
    @DisplayName("Get row with ")
    @MethodSource("widthProvider")
    public void getRow_correctWidth_correctOutput(int width) {
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
    public void getRow_negativeWidth_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Shape.getRow(-1);
        });
    }

    @Test
    @DisplayName("Try to get row with too big width, should throw exception")
    public void getRow_tooBigWidth_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Shape.getRow(Library.COLUMNS + 1);
        });
    }
}
