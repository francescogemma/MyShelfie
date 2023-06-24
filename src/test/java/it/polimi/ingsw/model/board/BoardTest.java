package it.polimi.ingsw.model.board;

import it.polimi.ingsw.model.tile.Tile;
import it.polimi.ingsw.model.tile.TileColor;
import it.polimi.ingsw.model.bag.Bag;

import it.polimi.ingsw.model.tile.TileVersion;
import it.polimi.ingsw.utils.Coordinate;
import jdk.jfr.Description;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {
    private Board board;
    private static final int numberOfRun = 500;

    @BeforeEach
    public void setUp() {
        board = new Board();
    }

    private void removeAndDraw (int r, int c) throws IllegalExtractionException, FullSelectionException {
        board.selectTile(new Coordinate(r, c));
        board.draw();
    }

    private void removeAndDraw (Coordinate coordinate) throws IllegalExtractionException, FullSelectionException {
        assert coordinate != null;
        board.selectTile(coordinate);
        board.draw();
    }

    private void removeAndDraw(List<Coordinate> coordinates) throws IllegalExtractionException, FullSelectionException {
        for (Coordinate coordinate: coordinates) {
            board.selectTile(coordinate);
            board.draw();
        }
    }

    @RepeatedTest(numberOfRun)
    void fillRandomly__correctOutput() {
        Bag bag = new Bag();

        for (int i = 0; i < 33; i++) {
            assertFalse(board.isFull(2));
            board.fillRandomly(bag.getRandomTile(), 2);
        }

        assertTrue(board.isFull(2));
        assertFalse(board.isFull(3));
        assertFalse(board.isFull(4));

        for (int i = 0; i < 7; i++) {
            assertFalse(board.isFull(3));
            board.fillRandomly(bag.getRandomTile(), 3);
        }

        assertTrue(board.isFull(3));
        assertFalse(board.isFull(4));

        for (int i = 0; i < 5; i++) {
            assertFalse(board.isFull(4));
            board.fillRandomly(bag.getRandomTile(), 4);
        }

        assertTrue(board.isFull(4));
    }

    @RepeatedTest(numberOfRun)
    void fillRandomly_boardIsFull_ShouldThrowException() {
        fillBoard(board);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            board.fillRandomly(
                Tile.getInstance(TileColor.BLUE, TileVersion.FIRST),
                4);
        });
    }

    static void fillBoard(Board board, int numPleyer) {
        assert board != null;
        Bag bag = new Bag();

        final int s = (33) +
                (numPleyer > 2 ? 7 : 0) +
                (numPleyer == 4 ? 5 : 0);

        for (int i = 0; i < s; i++) {
            board.fillRandomly(bag.getRandomTile(), numPleyer);
        }

        assertTrue(board.isFull(numPleyer));
    }

    static void fillBoard(Board board) {
        fillBoard(board, 4);
    }

    @RepeatedTest(numberOfRun)
    void getSelectableTiles__correctOutput() throws IllegalExtractionException, FullSelectionException {
        fillBoard(board);

        Assertions.assertEquals(20, board.getSelectableTiles().size());

        board.selectTile(4, 0);

        Assertions.assertEquals( 1, board.getSelectableTiles().size());

        board.selectTile(5, 0);

        Assertions.assertEquals(0, board.getSelectableTiles().size());
    }

    @Test
    void selectTile_tileEmpty_throwsIllegalExtractionException() throws IllegalExtractionException, FullSelectionException {
        fillBoard(board, 4);

        Coordinate pos = board.getSelectableCoordinate().get(0);

        board.selectTile(pos);
        board.selectTile(board.getSelectableCoordinate().get(0));

        board.draw();

        Assertions.assertThrows(IllegalExtractionException.class, () -> {
            board.selectTile(pos);
        });
    }

    @RepeatedTest(numberOfRun)
    void selectTile_tileWith0FreeSides_throwsIllegalExtractionException() {
        fillBoard(this.board);

        Assertions.assertThrows(IllegalExtractionException.class, () -> {
            board.selectTile(5, 5);
        });
    }

    @RepeatedTest(numberOfRun)
    void selectTile_diagonal_throwsIllegalExtractionException() throws IllegalExtractionException, FullSelectionException {
        fillBoard(this.board);
        board.selectTile(5, 1);

        Assertions.assertThrows(IllegalExtractionException.class, () -> {
            board.selectTile(4, 2);
        });
    }

    @RepeatedTest(numberOfRun)
    void selectTile_illegalStructure_ShouldThrowException() throws IllegalExtractionException, FullSelectionException {
        fillBoard(this.board, 2);
        board.selectTile(7, 3);
        board.selectTile(7, 4);
        Assertions.assertThrows(IllegalExtractionException.class, () -> {
            board.selectTile(6, 4);
        });
    }

    @RepeatedTest(numberOfRun)
    void selectTile_verticalExtraction_correctOutput() throws IllegalExtractionException, FullSelectionException {
        fillBoard(this.board, 4);
        board.selectTile(4, 0);
        board.selectTile(5, 0);
        board.draw();

        board.selectTile(3, 1);
        board.selectTile(4, 1);
        board.selectTile(5, 1);
    }

    @RepeatedTest(numberOfRun)
    void selectTile_horizontalExtraction_correctOutput() throws IllegalExtractionException, FullSelectionException {
        fillBoard(this.board, 2);
        board.selectTile(7, 3);
        board.selectTile(7, 4);
        board.selectTile(7, 5);
    }

    @RepeatedTest(numberOfRun)
    void draw_oneSelected_correctOutput() throws IllegalExtractionException, FullSelectionException {
        fillBoard(this.board);
        board.selectTile(7, 3);
        Assertions.assertEquals(1, board.draw().size());
    }

    @RepeatedTest(numberOfRun)
    void selectTile_tripleExtractionTwoPlayers_correctOutput() throws IllegalExtractionException, FullSelectionException {
        fillBoard(this.board, 2);

        board.selectTile(3, 7);
        board.selectTile(4, 7);
        Assertions.assertEquals(2, board.draw().size());

        board.selectTile(5, 6);
        board.selectTile(4, 6);
        board.selectTile(3, 6);
        Assertions.assertEquals(0, board.getSelectableTiles().size());
        Assertions.assertEquals(3, board.draw().size());
    }

    @RepeatedTest(numberOfRun)
    void needRefill_onlyOneCellWith4FreeBorder_correctOutput() throws IllegalExtractionException, FullSelectionException {
        fillBoard(this.board);
        assertFalse(board.needsRefill());

        board.selectTile(0, 3);
        board.selectTile(0, 4);

        Assertions.assertEquals(2, board.draw().size());
        assertFalse(board.needsRefill());

        board.selectTile(1, 3);
        board.selectTile(1, 4);
        board.selectTile(1, 5);

        Assertions.assertEquals(3, board.draw().size());

        assertFalse(board.needsRefill());

        board.selectTile(3, 7); board.draw();
        board.selectTile(4, 7); board.draw();
        board.selectTile(5, 7); board.draw();

        assertFalse(board.needsRefill());

        board.selectTile(3, 8); board.draw();
        assertFalse(board.needsRefill());
    }

    @RepeatedTest(numberOfRun)
    void selectTile_tileAlreadyInThisSelection_throwsIllegalExtractionException() throws IllegalExtractionException, FullSelectionException {
        fillBoard(this.board);

        board.selectTile(4, 0);
        board.selectTile(5, 0);

        Assertions.assertThrows(IllegalExtractionException.class, () ->  {
            board.selectTile(4, 0);
        });
    }

    @RepeatedTest(numberOfRun)
    void selectTile_selectMoreThan4Tile_ShouldThrowException() throws IllegalExtractionException, FullSelectionException {
        fillBoard(this.board, 2);

        board.selectTile(4, 0); board.draw();

        board.selectTile(5, 1);
        board.selectTile(4, 1);
        board.selectTile(3, 1);
        board.draw();

        board.selectTile(2, 2);
        board.selectTile(3, 2);
        board.selectTile(4, 2);

        Assertions.assertThrows(IllegalExtractionException.class, () -> {
            this.board.selectTile(5, 2);
        });

        board.draw();
    }

    @RepeatedTest(numberOfRun)
    @Description("Make sure that getSelectedTiles does not return null.")
    void getSelectedTiles_testCombination_correctOutput() throws FullSelectionException, IllegalExtractionException {
        fillBoard(board, 2);
        board.selectTile(4, 0); board.draw();

        board.selectTile(5, 1);
        board.selectTile(4, 1);
        board.selectTile(3, 1);

        assertFalse(board.getSelectedTiles().contains(null));
        Assertions.assertEquals(3, board.getSelectedTiles().size());
    }

    @RepeatedTest(numberOfRun)
    @Description("Make sure that needsRefill return false if we didn't touch any Tile on Board")
    void needsRefill_testZeroExtraction_correctOutput () throws FullSelectionException, IllegalExtractionException {
        fillBoard(board);
        for (int i = 0; i < Board.TWO_PLAYER_POSITIONS.size() + Board.THREE_PLAYER_POSITIONS.size() + Board.FOUR_PLAYER_POSITIONS.size(); i++) {
            board.selectTile(
                    board.getSelectableCoordinate().get(0)
            );
            board.draw();
        }

        assertEquals(0, board.getSelectableCoordinate().size());

        assertTrue(board.needsRefill());
    }

    @RepeatedTest(numberOfRun)
    void needsRefill_testMoreThanFourPlayer_ShouldThrowException() {
        fillBoard(board);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            board.isFull(5);
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            board.isFull(1);
        });
    }

    @RepeatedTest(numberOfRun)
    void selectTile_coordinateOutside_ShouldThrowException() {
        fillBoard(board);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            board.selectTile(-1, 5);
        });
    }

    @RepeatedTest(numberOfRun)
    void selectTile_selectDistanceMoreThan1Vertical_correctOutput () throws IllegalExtractionException, FullSelectionException {
        fillBoard(board, 3);

        this.removeAndDraw(Arrays.asList(
                new Coordinate(4, 0),
                new Coordinate(5, 0),
                new Coordinate(3, 1),
                new Coordinate(4, 1),
                new Coordinate(5, 1)
        ));

        board.selectTile(3, 2);
        board.selectTile(5, 2);
        board.selectTile(4, 2);
    }

    @RepeatedTest(numberOfRun)
    void selectTile_selectDistanceMoreThan1Horizontal_correctOutput () throws IllegalExtractionException, FullSelectionException {
        fillBoard(board, 3);

        this.removeAndDraw(Arrays.asList(
                new Coordinate(0, 3),
                new Coordinate(1, 3),
                new Coordinate(1, 4)
        ));

        board.selectTile(2, 2);
        board.selectTile(2, 4);
        board.selectTile(2, 3);
        board.draw();
    }

    @RepeatedTest(numberOfRun)
    void getSelectableCoordinate_selectDistanceMoreThan1Horizontal_correctOutput () throws IllegalExtractionException, FullSelectionException {
        fillBoard(board, 4);

        this.removeAndDraw(Arrays.asList(
                new Coordinate(4, 0),
                new Coordinate(5, 0)
        ));

        board.selectTile(3, 1);

        Assertions.assertEquals(2, board.getSelectableCoordinate().size());
        Assertions.assertTrue(board.getSelectableCoordinate().contains(new Coordinate(5, 1)));
        Assertions.assertTrue(board.getSelectableCoordinate().contains(new Coordinate(4, 1)));
    }

    @RepeatedTest(numberOfRun)
    void draw_distanceMoreThan1Vertical_throwIllegalExtractionException () throws IllegalExtractionException, FullSelectionException {
        fillBoard(board, 3);

        this.removeAndDraw(Arrays.asList(
                new Coordinate(4, 0),
                new Coordinate(5, 0)
        ));

        board.selectTile(3, 1);
        board.selectTile(5, 1);

        Assertions.assertThrows(IllegalExtractionException.class, () -> {
            board.draw();
        });

        board.selectTile(4, 1);

        List<Coordinate> coordinates = board.getSelectedCoordinates();

        Assertions.assertEquals(new Coordinate(3, 1), coordinates.get(0));
        Assertions.assertEquals(new Coordinate(5, 1), coordinates.get(1));
        Assertions.assertEquals(new Coordinate(4, 1), coordinates.get(2));

        List<Tile> tiles = board.draw();

        Assertions.assertEquals(3, tiles.size());
    }

    @RepeatedTest(numberOfRun)
    void draw_distanceMoreThan1Horizontal_throwIllegalExtractionException() throws IllegalExtractionException, FullSelectionException {
        fillBoard(board, 4);

        this.removeAndDraw(Arrays.asList(
                new Coordinate(0, 3),
                new Coordinate(0, 4)
        ));

        board.selectTile(1, 3);
        board.selectTile(1, 5);

        Assertions.assertThrows(IllegalExtractionException.class, () -> {
            board.draw();
        });

        board.selectTile(1, 4);

        List<Coordinate> coordinates = board.getSelectedCoordinates();

        Assertions.assertEquals(new Coordinate(1, 3), coordinates.get(0));
        Assertions.assertEquals(new Coordinate(1, 5), coordinates.get(1));
        Assertions.assertEquals(new Coordinate(1, 4), coordinates.get(2));

        List<Tile> tiles = board.draw();

        Assertions.assertEquals(3, tiles.size());
    }

    @RepeatedTest(numberOfRun)
    void needsRefill__correctOutput () throws IllegalExtractionException, FullSelectionException {
        fillBoard(board, 3);

        List<Coordinate> toBeRemove = new ArrayList<>();
        toBeRemove.add(new Coordinate(5, 0));
        toBeRemove.add(new Coordinate(3, 1));
        toBeRemove.add(new Coordinate(3, 2));
        toBeRemove.add(new Coordinate(4, 2));
        toBeRemove.add(new Coordinate(5, 2));
        toBeRemove.add(new Coordinate(5, 3));
        toBeRemove.add(new Coordinate(6, 3));
        toBeRemove.add(new Coordinate(3, 3));
        toBeRemove.add(new Coordinate(2, 3));
        toBeRemove.add(new Coordinate(1, 3));
        toBeRemove.add(new Coordinate(2, 4));
        toBeRemove.add(new Coordinate(2, 5));
        toBeRemove.add(new Coordinate(3, 5));
        toBeRemove.add(new Coordinate(3, 6));
        toBeRemove.add(new Coordinate(3, 8));
        toBeRemove.add(new Coordinate(5, 4));
        toBeRemove.add(new Coordinate(4, 4));
        toBeRemove.add(new Coordinate(5, 5));
        toBeRemove.add(new Coordinate(4, 5));
        toBeRemove.add(new Coordinate(4, 7));
        toBeRemove.add(new Coordinate(5, 6));
        toBeRemove.add(new Coordinate(6, 6));
        toBeRemove.add(new Coordinate(7, 5));
        toBeRemove.add(new Coordinate(6, 4));
        toBeRemove.add(new Coordinate(4, 1));

        for (Coordinate coordinate: toBeRemove) {
            board.selectTile(coordinate);
            board.draw();
            Assertions.assertFalse(board.needsRefill());
        }

        board.selectTile(new Coordinate(7, 4));
        board.draw();

        Assertions.assertTrue(board.needsRefill());
    }

    @Test
    void selectTile_distanceMoreThan1TileBetwine0FreeSideVertical_correctOutput () throws IllegalExtractionException, FullSelectionException {
        fillBoard(board, 3);

        board.selectTile(new Coordinate(5, 0));
        board.draw();

        board.selectTile(new Coordinate(5, 1));

        Assertions.assertThrows(IllegalExtractionException.class, () -> {
            board.selectTile(new Coordinate(3, 1));
        });

        Assertions.assertEquals(0, board.getSelectableCoordinate().size());
    }

    @Test
    void selectTile_distanceMoreThan1TileBetwine0FreeSideHorizontal_correctOutput () throws IllegalExtractionException, FullSelectionException {
        fillBoard(board, 3);

        this.removeAndDraw(new Coordinate(5, 0));
        this.removeAndDraw(new Coordinate(2, 2));
        this.removeAndDraw(new Coordinate(3, 2));

        board.selectTile(3, 1);

        Assertions.assertThrows(IllegalExtractionException.class, () -> {
            board.selectTile(3, 3);
        });

        Assertions.assertEquals(0, board.getSelectableCoordinate().size());
    }

    @Test
    void selectTile_verticalDistance0_correctOutput () throws IllegalExtractionException, FullSelectionException {
        fillBoard(board, 4);

        board.selectTile(4, 0);
        board.selectTile(5, 0);

        board.draw();
    }

    @Test
    void getSelectableCoordinate__OnTopWith0FreeSides_correctOutput () throws IllegalExtractionException, FullSelectionException {
        fillBoard(board, 4);
        removeAndDraw(4, 0);
        removeAndDraw(4, 1);
        removeAndDraw(5, 0);
        removeAndDraw(5, 1);

        board.selectTile(5, 2);

        Assertions.assertEquals(2, board.getSelectableCoordinate().size());

        Assertions.assertTrue(board.getSelectableCoordinate().contains(new Coordinate(6, 2)));
        Assertions.assertTrue(board.getSelectableCoordinate().contains(new Coordinate(4, 2)));
    }

    @Test
    void getSelectableCoordinate__OnTopWith1FreeSides_correctOutput () throws IllegalExtractionException, FullSelectionException {
        fillBoard(board, 4);
        removeAndDraw(4, 0);
        removeAndDraw(4, 1);
        removeAndDraw(5, 0);
        removeAndDraw(5, 1);
        removeAndDraw(3, 1);

        board.selectTile(5, 2);

        Assertions.assertEquals(3, board.getSelectableCoordinate().size());

        Assertions.assertTrue(board.getSelectableCoordinate().contains(new Coordinate(6, 2)));
        Assertions.assertTrue(board.getSelectableCoordinate().contains(new Coordinate(4, 2)));
        Assertions.assertTrue(board.getSelectableCoordinate().contains(new Coordinate(3, 2)));
    }

    @Test
    void forgetSelected_coordinateNull_throwNullPointerException () {
        Assertions.assertThrows(NullPointerException.class, () -> {
            board.forgetSelected(null);
        });
    }

    @Test
    void forgetSelected_coordinateNotSelected_throwIllegalArgumentException () throws IllegalExtractionException, FullSelectionException {
        fillBoard(board, 4);

        board.selectTile(board.getSelectableCoordinate().get(0));
        board.selectTile(board.getSelectableCoordinate().get(0));

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            board.forgetSelected(new Coordinate(5, 5));
        });
    }

    @Test
    void forgetSelected_coordinateNotLast_throwRemoveNotLastSelectedException() throws IllegalExtractionException, FullSelectionException {
        fillBoard(board, 4);

        Coordinate firstSelected = board.getSelectableCoordinate().get(0);
        board.selectTile(firstSelected);
        board.selectTile(board.getSelectableCoordinate().get(0));

        Assertions.assertThrows(RemoveNotLastSelectedException.class, () -> {
            board.forgetSelected(firstSelected);
        });
    }

    @Test
    void forgetSelected__correctOutput() throws IllegalExtractionException, FullSelectionException {
        fillBoard(board, 4);

        board.selectTile(board.getSelectableCoordinate().get(0));
        Coordinate secondSelected = board.getSelectableCoordinate().get(0);
        board.selectTile(secondSelected);

        board.forgetSelected(secondSelected);
    }

    @RepeatedTest(numberOfRun)
    void equals_boardEquals_correctOutput() {
        fillBoard(board, 4);
        Board board1 = new Board(board);

        Assertions.assertEquals(board1, board);
    }

    @Test
    void equals_objectNull_correctOutput() {
        Assertions.assertFalse(board.equals(null));
    }

    @RepeatedTest(numberOfRun)
    void equals_withView_correctOutput() {
        fillBoard(board, 4);
        Assertions.assertNotEquals(board, board.createView());
    }

    @RepeatedTest(numberOfRun)
    void forgetSelection__correctOutput() throws IllegalExtractionException, FullSelectionException {
        fillBoard(board, 3);

        Coordinate s = board.getSelectableCoordinate().get(0);

        board.selectTile(s);
        board.forgetSelection();

        Assertions.assertEquals(0, board.getSelectedTiles().size());
    }

    @Test
    void canDraw__correctOutput() throws IllegalExtractionException, FullSelectionException {
        fillBoard(board, 4);

        Assertions.assertFalse(board.canDraw());

        board.selectTile(board.getSelectableCoordinate().get(0));
        Assertions.assertTrue(board.canDraw());
    }

    @Test
    void canDraw_horizontal_correctOutput() throws IllegalExtractionException, FullSelectionException {
        fillBoard(board, 4);

        board.selectTile(0, 3);
        board.selectTile(0, 4);

        board.draw();

        board.selectTile(1, 3);
        board.selectTile(1, 4);
        board.selectTile(1, 5);

        Assertions.assertTrue(board.canDraw());
    }

    @Test
    void getSelectedCoordinates__correctOutput() throws IllegalExtractionException, FullSelectionException {
        fillBoard(board, 2);

        board.selectTile(1, 3);
        board.selectTile(1, 4);

        board.draw();

        board.selectTile(2, 2);
        board.selectTile(2, 3);
        board.selectTile(2, 4);

        Assertions.assertEquals(3, board.getSelectedCoordinates().size());

        Assertions.assertEquals(new Coordinate(2, 2), board.getSelectedCoordinates().get(0));
        Assertions.assertEquals(new Coordinate(2, 3), board.getSelectedCoordinates().get(1));
        Assertions.assertEquals(new Coordinate(2, 4), board.getSelectedCoordinates().get(2));
    }
}
