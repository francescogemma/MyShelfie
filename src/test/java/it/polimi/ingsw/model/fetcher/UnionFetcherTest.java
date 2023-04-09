package it.polimi.ingsw.model.fetcher;

import it.polimi.ingsw.model.tile.TileColor;
import it.polimi.ingsw.model.bookshelf.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;

class UnionFetcherTest {
    private final static int NUM_OF_FETCHES = 1000;

    private static List<int[][]> mockBookshelfProvider() {
        Random random = new Random(368865);

        List<int[][]> mockBookshelves = new ArrayList<>();

        for (int i = 0; i < NUM_OF_FETCHES; i++) {
            int[][] mockBookshelf = new int[Bookshelf.ROWS][Bookshelf.COLUMNS];

            for (int row = 0; row < Bookshelf.ROWS; row++) {
                for (int column = 0; column < Bookshelf.COLUMNS; column++) {
                    if (row > 0 && row < Bookshelf.ROWS - 1 && column > 0 && column < Bookshelf.COLUMNS - 1) {
                        int rowOffset = 1 - 2 * random.nextInt(2);
                        int columnOffset = 1 - 2 * random.nextInt(2);

                        if (random.nextInt(5) < 4) {
                            Shelf shelf = Shelf.getInstance(row, column)
                                .move(Offset.getInstance(rowOffset, columnOffset));
                            mockBookshelf[row][column] = mockBookshelf[shelf.getRow()][shelf.getColumn()];
                            continue;
                        }
                    }

                    mockBookshelf[row][column] = random.nextInt(TileColor.values().length);
                }
            }

            mockBookshelves.add(mockBookshelf);
        }

        return mockBookshelves;
    }

    private List<BookshelfMask> fetchAllMasks(Fetcher fetcher, int[][] mockBookshelf) {
        List<BookshelfMask> masks = new ArrayList<>();

        BookshelfMask mask = new BookshelfMask(new Bookshelf());
        Optional<Integer> currentGroupValue = Optional.empty();
        do {
            Shelf shelf = fetcher.next();
            if (currentGroupValue.isPresent() &&
                mockBookshelf[shelf.getRow()][shelf.getColumn()] != currentGroupValue.get()) {
                if (!fetcher.canFix()) {
                    mask.clear();
                    currentGroupValue = Optional.empty();
                    continue;
                }
            } else {
                mask.add(shelf);
                currentGroupValue = Optional.of(mockBookshelf[shelf.getRow()][shelf.getColumn()]);
            }

            if (fetcher.lastShelf()) {
                masks.add(new BookshelfMask(mask));
                mask.clear();
                currentGroupValue = Optional.empty();
            }
        } while (!fetcher.hasFinished());

        return masks;
    }

    @ParameterizedTest
    @DisplayName("Fetch all the masks from an adjacency fetcher and two shape fetchers, " +
        " then check that we get the same masks with a union fetcher")
    @MethodSource("mockBookshelfProvider")
    void fetch_correctOutput(int[][] mockBookshelf) {
        Fetcher firstFetcher = new ShapeFetcher(Shape.SQUARE);
        Fetcher secondFetcher = new AdjacencyFetcher();
        Fetcher thirdFetcher = new ShapeFetcher(Shape.TETROMINOES.get(2));

        List<BookshelfMask> fetchedOneByOneMasks = fetchAllMasks(firstFetcher, mockBookshelf);
        fetchedOneByOneMasks.addAll(fetchAllMasks(secondFetcher, mockBookshelf));
        fetchedOneByOneMasks.addAll(fetchAllMasks(thirdFetcher, mockBookshelf));

        Fetcher unionFetcher = new UnionFetcher(List.of(
            firstFetcher, secondFetcher, thirdFetcher
        ));

        List<BookshelfMask> fetchedAllTogetherMasks = fetchAllMasks(unionFetcher, mockBookshelf);

        Assertions.assertEquals(fetchedOneByOneMasks, fetchedAllTogetherMasks);
    }

    @Test
    @DisplayName("Try to construct a UnionFetcher with not enoguh fetchers")
    void constructor_notEnoughFetchers_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
           new UnionFetcher(List.of(new ShapeFetcher(Shape.SQUARE)));
        });
    }

    @Test
    @DisplayName("Clear the fetcher and assert that it is at the equilibrium state")
    void clear_duringFetch_inEquilibrium() {
        Fetcher fetcher = new UnionFetcher(List.of(new ShapeFetcher(Shape.DOMINOES.get(0)),
            new ShapeFetcher(Shape.DOMINOES.get(1))));

        for (int i = 0; i < 7; i++) {
            fetcher.next();
            fetcher.lastShelf();
        }

        fetcher.clear();

        Assertions.assertTrue(fetcher.hasFinished());
    }
}
