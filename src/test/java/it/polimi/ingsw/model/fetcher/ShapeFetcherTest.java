package it.polimi.ingsw.model.fetcher;

import it.polimi.ingsw.model.bookshelf.ShapeTest;
import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.bookshelf.Offset;
import it.polimi.ingsw.model.bookshelf.Shape;
import it.polimi.ingsw.model.bookshelf.Shelf;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

class ShapeFetcherTest {
    private final int[] mockBookshelf = new int[Bookshelf.ROWS * Bookshelf.COLUMNS];

    private final static int MAXIMUM_NUM_OF_ORIGINS = 10;

    private static List<Arguments> shapeOriginsProvider() {
        List<Arguments> shapesOrigins = new ArrayList<>();

        Random random = new Random(371318);

        for (Arguments arguments : ShapeTest.offsetsHeightWidthProvider()) {
            List<Offset> offsets = (List<Offset>) arguments.get()[0];
            int height = (Integer) arguments.get()[1];
            int width = (Integer) arguments.get()[2];

            boolean[] takenOrigin = new boolean[Bookshelf.ROWS * Bookshelf.COLUMNS];
            Arrays.fill(takenOrigin, false);

            int targetOriginsNum = random.nextInt(MAXIMUM_NUM_OF_ORIGINS) + 1;

            List<Shelf> origins = new ArrayList<>();

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
        Arrays.fill(mockBookshelf, 0);
    }

    @ParameterizedTest
    @DisplayName("Fetch all the shapes from the bookshelf mock")
    @MethodSource("shapeOriginsProvider")
    void fetch_correctOutput(Shape shape, List<Shelf> origins) {
        for (Shelf origin : origins) {
            for (Offset offset : shape.getOffsets()) {
                Shelf currentShelf = origin.move(offset);

                mockBookshelf[currentShelf.getRow() * Bookshelf.COLUMNS + currentShelf.getColumn()] = 1;
            }
        }

        boolean[] fetchedOrigin = new boolean[origins.size()];
        Arrays.fill(fetchedOrigin, false);

        Fetcher fetcher = new ShapeFetcher(shape);

        int originRow = Bookshelf.ROWS;
        int originColumn = Bookshelf.COLUMNS;
        List<Shelf> shelves = new ArrayList<>();

        do {
            Shelf shelf = fetcher.next();

            if (mockBookshelf[shelf.getRow() * Bookshelf.COLUMNS + shelf.getColumn()] == 0) {
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

                List<Offset> offsets = new ArrayList<>();
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

    @Test
    @DisplayName("Call to next without invoking lastShelf or canFix afterwards, should throw exception")
    void next_withoutLastShelfOrCanFix_throwsIllegalStateException() {
        Fetcher fetcher = new ShapeFetcher(Shape.SQUARE);

        for (int i = 0; i < Shape.SQUARE.getOffsets().size(); i++) {
            fetcher.next();
        }

        Assertions.assertThrows(IllegalStateException.class, () -> {
            fetcher.next();
        });
    }

    @ParameterizedTest
    @DisplayName("Clear the fetcher")
    @ValueSource(ints = { 14, 3, 7, 19 })
    void clear_duringFetch_inEquilibrium(int numOfNextCalls) {
        Fetcher fetcher = new ShapeFetcher(Shape.SQUARE);

        while (numOfNextCalls > 0) {
            fetcher.next();

            fetcher.lastShelf();

            numOfNextCalls--;
        }

        fetcher.clear();

        Assertions.assertTrue(fetcher.hasFinished());
    }
}
