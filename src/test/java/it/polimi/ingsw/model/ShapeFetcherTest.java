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

class ShapeFetcherTest {
    private final int[] bookshelfMock = new int[Bookshelf.ROWS * Bookshelf.COLUMNS];

    private final static int MAXIMUM_NUM_OF_ORIGINS = 10;

    private static ArrayList<Arguments> shapeOriginsProvider() {
        ArrayList<Arguments> shapesOrigins = new ArrayList<>();

        Random random = new Random(371318);

        for (Arguments arguments : ShapeTest.offsetsHeightWidthProvider()) {
            ArrayList<Offset> offsets = (ArrayList<Offset>) arguments.get()[0];
            int height = (Integer) arguments.get()[1];
            int width = (Integer) arguments.get()[2];

            boolean[] takenOrigin = new boolean[Bookshelf.ROWS * Bookshelf.COLUMNS];
            Arrays.fill(takenOrigin, false);

            int targetOriginsNum = random.nextInt(MAXIMUM_NUM_OF_ORIGINS) + 1;

            ArrayList<Shelf> origins = new ArrayList<>();

            while (targetOriginsNum > 0) {
                int originRow = random.nextInt(Bookshelf.ROWS - height + 1);
                int originColumn = random.nextInt(Bookshelf.COLUMNS - width + 1);

                if (!takenOrigin[originRow * Bookshelf.COLUMNS + originColumn]) {
                    origins.add(Shelf.getInstance(originRow, originColumn));
                    takenOrigin[originRow * Bookshelf.COLUMNS + originColumn] = true;
                }

                targetOriginsNum--;
            }

            shapesOrigins.add(Arguments.arguments(new Shape(offsets), origins));
        }

        return shapesOrigins;
    }

    @BeforeEach
    public void setUp() {
        Arrays.fill(bookshelfMock, 0);
    }

    @ParameterizedTest
    @DisplayName("Fetch all the shapes from the bookshelf mock")
    @MethodSource("shapeOriginsProvider")
    void fetch_correctOutput(Shape shape, ArrayList<Shelf> origins) {
        for (Shelf origin : origins) {
            for (Offset offset : shape.getOffsets()) {
                Shelf currentShelf = origin.move(offset);

                bookshelfMock[currentShelf.getRow() * Bookshelf.COLUMNS + currentShelf.getColumn()] = 1;
            }
        }

        boolean[] fetchedOrigin = new boolean[origins.size()];
        Arrays.fill(fetchedOrigin, false);

        Fetcher fetcher = new ShapeFetcher(shape);

        int originRow = Bookshelf.ROWS;
        int originColumn = Bookshelf.COLUMNS;
        ArrayList<Shelf> shelves = new ArrayList<>();

        do {
            Shelf shelf = fetcher.next();

            if (bookshelfMock[shelf.getRow() * Bookshelf.COLUMNS + shelf.getColumn()] == 0) {
                Assertions.assertFalse(fetcher.canFix());

                originRow = Bookshelf.ROWS;
                originColumn = Bookshelf.COLUMNS;
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

                originRow = Bookshelf.ROWS;
                originColumn = Bookshelf.COLUMNS;
                shelves.clear();
            }

        } while (!fetcher.hasFinished());

        for (boolean fetched : fetchedOrigin) {
            Assertions.assertTrue(fetched);
        }
    }
}
