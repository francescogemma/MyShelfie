package it.polimi.ingsw;

import it.polimi.ingsw.model.Fetcher;
import it.polimi.ingsw.model.AdjacencyFetcher;
import it.polimi.ingsw.model.Shelf;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class AdjacencyFetcherTest {
    Fetcher fetcher;
    int groupValue;
    boolean anotherGroup;
    final int NUM_SHELVES_IN_LIBRARY = 30;

    @BeforeEach
    public void setUp() {
        fetcher = new AdjacencyFetcher();
        anotherGroup = true;
    }

    @Test
    public void findGroups_oneGroup_correctOutput() {
        int[][] matrix = {
                {1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1}
        };

        fetchGroups(matrix);
        checkAssertions(matrix);
    }

    @Test
    public void findGroups_allGroupsOfOne_correctOutput() {
        int[][] matrix = {
                {1, 2, 3, 4, 5},
                {6, 7, 8, 9, 10},
                {11, 12, 13, 14, 15},
                {16, 17, 18, 19, 20},
                {21, 22, 23, 24, 25},
                {26, 27, 28, 29, 30}
        };

        fetchGroups(matrix);
        checkAssertions(matrix);
    }

    @Test
    public void findGroups_circularGroups_correctOutput() {
        int[][] matrix = {
                {1, 1, 1, 1, 1},
                {1, 2, 2, 2, 1},
                {1, 2, 3, 2, 1},
                {1, 2, 3, 2, 1},
                {1, 2, 2, 2, 1},
                {1, 1, 1, 1, 1}
        };

        fetchGroups(matrix);
        checkAssertions(matrix);
    }

    @Test
    public void findGroups_allColumns_correctOutput() {
        int[][] matrix = {
                {1, 2, 3, 4, 5},
                {1, 2, 3, 4, 5},
                {1, 2, 3, 4, 5},
                {1, 2, 3, 4, 5},
                {1, 2, 3, 4, 5},
                {1, 2, 3, 4, 5}
        };

        fetchGroups(matrix);
        checkAssertions(matrix);
    }

    @Test
    public void findGroups_allRows_correctOutput() {
        int[][] matrix = {
                {1, 1, 1, 1, 1},
                {2, 2, 2, 2, 2},
                {3, 3, 3, 3, 3},
                {4, 4, 4, 4, 4},
                {5, 5, 5, 5, 5},
                {6, 6, 6, 6, 6}
        };

        fetchGroups(matrix);
        checkAssertions(matrix);
    }

    @Test
    public void findGroups_randomGroups_correctOutput() {
        int[][] matrix = {
                {7, 1, 1, 5, 5},
                {1, 1, 1, 3, 2},
                {1, 1, 3, 3, 3},
                {1, 3, 3, 3, 3},
                {4, 4, 4, 4, 4},
                {5, 6, 6, 7, 4}
        };

        fetchGroups(matrix);
        checkAssertions(matrix);
    }

    @Test
    public void findGroups_rectangle_correctOutput() {
        int[][] matrix = {
                {1, 1, 1, 1, 1},
                {1, 3, 3, 3, 1},
                {1, 3, 3, 3, 1},
                {1, 3, 3, 3, 1},
                {1, 3, 3, 3, 1},
                {1, 1, 1, 1, 1}
        };

        fetchGroups(matrix);
        checkAssertions(matrix);
    }

    @Test
    public void findGroups_canFixWithEmptyStack_throwsIllegalStateException() {
        Assertions.assertThrows(IllegalStateException.class, () -> fetcher.canFix());
    }

    @Test
    public void findGroups_nextCalledWithAllVisitedShelves_throwsIllegalStateException() {
        for(int i = 0; i < NUM_SHELVES_IN_LIBRARY; i++) {
            fetcher.next();
        }
        Assertions.assertThrows(IllegalStateException.class, () -> fetcher.next());
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
            }

            if (fetcher.lastShelf()) {
                anotherGroup = true;
            }
        } while (!fetcher.hasFinished());
    }

    private void checkAssertions(int[][] matrix) {
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
                Assertions.assertEquals(0, matrix[i][j], "The value of the shelf at position (" + i + ", " + j + ") is not 0");
            }
        }
    }
}
