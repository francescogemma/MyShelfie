package it.polimi.ingsw.model.fetcher;

import it.polimi.ingsw.model.bookshelf.Shelf;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;

class AdjacencyFetcherTest {
    private Fetcher fetcher;
    private int groupValue;
    private int numGroups;
    private boolean anotherGroup;
    private final int NUM_SHELVES_IN_BOOKSHELF = 30;

    @BeforeEach
    public void setUp() {
        fetcher = new AdjacencyFetcher();
        anotherGroup = true;
        numGroups = 0;
    }

    @Test
    @DisplayName("One single group of 30 cells")
    void findGroups_oneGroup_correctOutput() {
        int[][] matrix = {
                {1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1}
        };

        fetchGroups(matrix);
        checkAssertions(matrix, 1);
    }

    @Test
    @DisplayName("30 groups, each with one cell")
    void findGroups_allGroupsOfOne_correctOutput() {
        int[][] matrix = {
                {1, 2, 3, 4, 5},
                {6, 7, 8, 9, 10},
                {11, 12, 13, 14, 15},
                {16, 17, 18, 19, 20},
                {21, 22, 23, 24, 25},
                {26, 27, 28, 29, 30}
        };

        fetchGroups(matrix);
        checkAssertions(matrix, 30);
    }

    @Test
    @DisplayName("3 concentric rectangles like groups")
    void findGroups_concentricRectanglesGroups_correctOutput() {
        int[][] matrix = {
                {1, 1, 1, 1, 1},
                {1, 2, 2, 2, 1},
                {1, 2, 3, 2, 1},
                {1, 2, 3, 2, 1},
                {1, 2, 2, 2, 1},
                {1, 1, 1, 1, 1}
        };

        fetchGroups(matrix);
        checkAssertions(matrix, 3);
    }

    @Test
    @DisplayName("5 groups, each formed by a full column")
    void findGroups_allColumns_correctOutput() {
        int[][] matrix = {
                {1, 2, 3, 4, 5},
                {1, 2, 3, 4, 5},
                {1, 2, 3, 4, 5},
                {1, 2, 3, 4, 5},
                {1, 2, 3, 4, 5},
                {1, 2, 3, 4, 5}
        };

        fetchGroups(matrix);
        checkAssertions(matrix, 5);
    }

    @Test
    @DisplayName("6 groups, each formed by a full row")
    void findGroups_allRows_correctOutput() {
        int[][] matrix = {
                {1, 1, 1, 1, 1},
                {2, 2, 2, 2, 2},
                {3, 3, 3, 3, 3},
                {4, 4, 4, 4, 4},
                {5, 5, 5, 5, 5},
                {6, 6, 6, 6, 6}
        };

        fetchGroups(matrix);
        checkAssertions(matrix, 6);
    }

    @Test
    @DisplayName("matrix with diagonal lines, 30 groups each with one cell")
    void findGroups_diagonalLines_correctOutput() {
        int[][] matrix = {
                {1, 2, 3, 4, 5},
                {2, 1, 2, 3, 4},
                {3, 2, 1, 2, 3},
                {4, 3, 2, 1, 2},
                {5, 4, 3, 2, 1},
                {6, 5, 4, 3, 2}
        };

        fetchGroups(matrix);
        checkAssertions(matrix, 30);
    }

    @Test
    @DisplayName("Pseudo-random groups, 9 groups")
    void findGroups_randomGroups_correctOutput() {
        int[][] matrix = {
                {7, 1, 1, 5, 5},
                {1, 1, 1, 3, 2},
                {1, 1, 3, 3, 3},
                {1, 3, 3, 3, 3},
                {4, 4, 4, 4, 4},
                {5, 6, 6, 7, 4}
        };

        fetchGroups(matrix);
        checkAssertions(matrix, 9);
    }

    @Test
    @DisplayName("2 groups, one of which is a rectangle")
    void findGroups_rectangle_correctOutput() {
        int[][] matrix = {
                {1, 1, 1, 1, 1},
                {1, 3, 3, 3, 1},
                {1, 3, 3, 3, 1},
                {1, 3, 3, 3, 1},
                {1, 3, 3, 3, 1},
                {1, 1, 1, 1, 1}
        };

        fetchGroups(matrix);
        checkAssertions(matrix, 2);
    }

    @Test
    @DisplayName("5 groups, one of which is a circle")
    void findGroups_circle_correctOutput() {
        int[][] matrix = {
                {1, 1, 3, 1, 1},
                {1, 3, 3, 3, 1},
                {3, 3, 3, 3, 3},
                {3, 3, 3, 3, 3},
                {1, 3, 3, 3, 1},
                {1, 1, 3, 1, 1}
        };

        fetchGroups(matrix);
        checkAssertions(matrix, 5);
    }

    @Test
    @DisplayName("Snake style group, 4 groups")
    void findGroups_snakeStyleGroup_correctOutput() {
        int[][] matrix = {
                {1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1},
                {7, 7, 7, 7, 1},
                {1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1},
                {7, 7, 5, 5, 1}
        };

        fetchGroups(matrix);
        checkAssertions(matrix, 4);
    }

    @Test
    @DisplayName("Call canFix() with empty stack -> throws IllegalStateException")
    void canFixMethod_canFixWithEmptyStack_throwsIllegalStateException() {
        Assertions.assertThrows(IllegalStateException.class, () -> fetcher.canFix());
    }

    @Test
    @DisplayName("Call next() with all surrounding cells visited -> throws IllegalStateException")
    void nextMethod_nextCalledWithAllVisitedShelves_throwsIllegalStateException() {
        for(int i = 0; i < NUM_SHELVES_IN_BOOKSHELF; i++) {
            fetcher.next();
        }
        Assertions.assertThrows(IllegalStateException.class, () -> fetcher.next());
    }

    @Test
    @DisplayName("next() must return a valid cell")
    void nextMethod_severalNextCalls_validCellsReturned() {
        Shelf next = fetcher.next();
        int row = next.getRow();
        int column = next.getColumn();
        for(int i = 0; i < NUM_SHELVES_IN_BOOKSHELF - 1; i++) {
            next = fetcher.next();

            Assertions.assertFalse(next.getRow() == row + 1 && next.getColumn() == column + 1);
            Assertions.assertFalse(next.getRow() == row - 1 && next.getColumn() == column - 1);
            Assertions.assertFalse(next.getRow() == row - 1 && next.getColumn() == column + 1);
            Assertions.assertFalse(next.getRow() == row + 1 && next.getColumn() == column - 1);

            Assertions.assertFalse(next.getRow() == row && next.getColumn() == column);

            Assertions.assertTrue(Math.abs(next.getRow() - row) <= 1 && Math.abs(next.getColumn() - column) <= 1);

            row = next.getRow();
            column = next.getColumn();
        }
    }

    @Test
    @DisplayName("hasFinished() return true in the equilibrium state (initial state)")
    void hasFinished_hasFinishedInTheInitialState_correctOutput() {
        Assertions.assertTrue(fetcher.hasFinished());
    }

    @ParameterizedTest(name = "after {0} next() calls")
    @DisplayName("clear() method works correctly")
    @MethodSource("numCallsProvider")
    void clear_clearDuringFetch_correctOutput(int numCalls) {
        Assertions.assertTrue(fetcher.hasFinished());
        for(int i = 0; i < numCalls; i++) {
            fetcher.next();
            Assertions.assertFalse(fetcher.hasFinished());
        }
        fetcher.clear();
        Assertions.assertTrue(fetcher.hasFinished());
    }

    private void fetchGroups(int[][] matrix) {
        do {
            Shelf next = fetcher.next();
            if(anotherGroup) {
                groupValue = matrix[next.getRow()][next.getColumn()];
                anotherGroup = false;
            }

            if(matrix[next.getRow()][next.getColumn()] != groupValue) {
                boolean flag = fetcher.canFix();
                Assertions.assertTrue(flag, "canFix() returned false when it should have returned true");
            } else {
                matrix[next.getRow()][next.getColumn()] -= groupValue;
                Assertions.assertEquals(0, matrix[next.getRow()][next.getColumn()], "The value of the cell at position (" + next.getRow() + ", " + next.getColumn() + ") is not 0");
            }

            if (fetcher.lastShelf()) {
                numGroups++;
                anotherGroup = true;
            }
        } while (!fetcher.hasFinished());
    }

    private void checkAssertions(int[][] matrix, int expectedNumGroups) {
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
                Assertions.assertEquals(0, matrix[i][j], "The value of the cell at position (" + i + ", " + j + ") is not 0");
            }
        }
        Assertions.assertEquals(expectedNumGroups, numGroups, "The number of groups is not correct");
    }

    private static List<Integer> numCallsProvider() {
        List<Integer> list = new ArrayList<>();
        for(int i = 0; i <= 30; i++) {
            list.add(i);
        }
        return list;
    }
}
