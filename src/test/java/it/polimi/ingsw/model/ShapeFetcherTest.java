package it.polimi.ingsw.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class ShapeFetcherTest {
    private final int[] libraryMock = new int[Library.ROWS * Library.COLUMNS];

    private final static int MAXIMUM_NUM_OF_ORIGINS = 10;

    private static ArrayList<Arguments> shapeOriginsProvider() {
        ArrayList<Arguments> shapesOrigins = new ArrayList<>();

        Random random = new Random(371318);

        for (Arguments arguments : ShapeTest.offsetsHeightWidthProvider()) {
            ArrayList<Offset> offsets = (ArrayList<Offset>) arguments.get()[0];
            int height = (Integer) arguments.get()[1];
            int width = (Integer) arguments.get()[2];

            boolean[] takenOrigin = new boolean[Library.ROWS * Library.COLUMNS];
            Arrays.fill(takenOrigin, false);

            int targetOriginsNum = random.nextInt(MAXIMUM_NUM_OF_ORIGINS) + 1;

            ArrayList<Shelf> origins = new ArrayList<>();

            while (targetOriginsNum > 0) {
                int originRow = random.nextInt(Library.ROWS - height + 1);
                int originColumn = random.nextInt(Library.COLUMNS - width + 1);

                if (!takenOrigin[originRow * Library.COLUMNS + originColumn]) {
                    origins.add(Shelf.getInstance(originRow, originColumn));
                    takenOrigin[originRow * Library.COLUMNS + originColumn] = true;
                }

                targetOriginsNum--;
            }

            shapesOrigins.add(Arguments.arguments(new Shape(offsets), origins));
        }

        return shapesOrigins;
    }

    @BeforeEach
    public void setUp() {
        Arrays.fill(libraryMock, 0);
    }

    @ParameterizedTest
    @DisplayName("Fetch all the shapes from the library mock")
    @MethodSource("shapeOriginsProvider")
    public void fetch_correctOutput(Shape shape, ArrayList<Shelf> origins) {
        for (Shelf origin : origins) {
            for (Offset offset : shape.getOffsets()) {
                Shelf currentShelf = origin.move(offset);

                libraryMock[currentShelf.getRow() * Library.COLUMNS + currentShelf.getColumn()] = 1;
            }
        }

        boolean[] fetchedOrigin = new boolean[origins.size()];
        Arrays.fill(fetchedOrigin, false);

        Fetcher fetcher = new ShapeFetcher(shape);

        int originRow = Library.ROWS;
        int originColumn = Library.COLUMNS;
        ArrayList<Shelf> shelves = new ArrayList<>();

        do {
            Shelf shelf = fetcher.next();

            if (libraryMock[shelf.getRow() * Library.COLUMNS + shelf.getColumn()] == 0) {
                Assertions.assertFalse(fetcher.canFix());

                originRow = Library.ROWS;
                originColumn = Library.COLUMNS;
                shelves.clear();

                continue;
            }

            originRow = Math.min(originRow, shelf.getRow());
            originColumn = Math.min(originColumn, shelf.getColumn());
            shelves.add(shelf);

            if (fetcher.lastShelf()) {
                int fetchedOriginIndex = origins.indexOf(Shelf.getInstance(originRow, originColumn));

                if (fetchedOriginIndex != -1) {
                    fetchedOrigin[fetchedOriginIndex] = true;
                }

                ArrayList<Offset> offsets = new ArrayList<>();
                for (Shelf s : shelves) {
                    offsets.add(Offset.getInstance(s.getRow() - originRow,
                        s.getColumn() - originColumn));
                }

                Assertions.assertEquals(new Shape(offsets), shape);

                originRow = Library.ROWS;
                originColumn = Library.COLUMNS;
                shelves.clear();
            }

        } while (!fetcher.hasFinished());

        for (boolean fetched : fetchedOrigin) {
            Assertions.assertTrue(fetched);
        }
    }
}
