package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.board.IllegalExtractionException;
import it.polimi.ingsw.model.board.SelectionFullException;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.Player;
import it.polimi.ingsw.utils.Coordinate;
import it.polimi.ingsw.utils.Pair;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Tester for Game
 * @author Giacomo Groppi
 * */
class GameTest {
    private Game game;

    private void addPlayer (int number) throws IllegalFlowException {
        assert number > 1 && number < 5;
        int i;

        for (i = 0; i < number; i++) {
            Player p = new Player("Name" + i);
            game.addPlayer(p);

            try {
                Assertions.assertTrue(game.isConnected(p));
            } catch (PlayerNotInGameException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @BeforeEach
    public void setUp() {
        game = new Game("TestingGame");
        Assertions.assertEquals("TestingGame", game.getName());
    }

    @Test
    void testCurrentPlayer() throws IllegalFlowException {
        Assertions.assertThrows(IllegalFlowException.class, () -> {
            game.getCurrentPlayer();
        });
        addPlayer(3);

        Assertions.assertThrows(IllegalFlowException.class, () -> {
             game.getCurrentPlayer();
        });

        game.startGame();
        Assertions.assertNotNull(game.getCurrentPlayer());
    }

    @Test
    void isOver_empty_correctOutput() throws IllegalFlowException {
        addPlayer(2);
    }

    @Test
    void getStartingPlayer_empty_correctOutput() throws IllegalFlowException {
        addPlayer(2);
        Assertions.assertNotNull(this.game.getStartingPlayer());
    }

    @Test
    void addPlayer_nullPlayer_ShouldThrowException() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            this.game.addPlayer(null);
        });
    }

    @Test
    void addPlayer_moreThanFourPlayers_ShouldThrowException() throws IllegalFlowException {
        addPlayer(4);
        Assertions.assertThrows(IllegalFlowException.class, () -> {
            this.game.addPlayer(new Player("test"));
        });
    }

    @Test
    void isConnected_playerNotExistsConnected_ShouldThrowException() throws IllegalFlowException {
        addPlayer(3);
        Assertions.assertThrows(PlayerNotInGameException.class, () -> {
            game.isConnected(new Player("Test"));
        });
    }

    @Test
    void isEquals__correctOutput() {
        Game g1 = new Game("test1");
        Game g2 = new Game("test1");
        Game g3 = new Game("test2");

        Assertions.assertEquals(g1, g2);
        Assertions.assertNotEquals(g3, g2);
    }

    @Test
    void addPlayer_gameAlreadyStarted_ShouldThrowException () throws IllegalFlowException {
        this.game.addPlayer(new Player("t1"));
        this.game.addPlayer(new Player("t2"));
        this.game.addPlayer(new Player("t3"));

        this.game.startGame();

        Assertions.assertThrows(IllegalFlowException.class, () -> {
            this.game.addPlayer(new Player("t4"));
        });
    }

    @Test
    void addPlayer_playerNull_ShouldThrowException() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            this.game.addPlayer(null);
        });
    }

    /* waiting for an implementation of equals in player.
    @Test
    void addPlayer_playerAlreadyInGame_ShouldThrowException() throws IllegalFlowException {
        this.game.addPlayer(new Player("t1"));

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            this.game.addPlayer(new Player("t1"));
        });
    }
    */

    @Test
    void startGame_noPlayer_ShouldThrowException() {
        Assertions.assertThrows(IllegalFlowException.class, () -> {
            this.game.startGame();
        });
    }

    @Test
    void startGame_onePlayer_ShouldThrowException() throws IllegalFlowException {
        this.game.addPlayer(new Player("t1"));
        Assertions.assertThrows(IllegalFlowException.class, () -> {
            this.game.startGame();
        });
    }


    @Test
    void insertTile__correctOutput() throws IllegalFlowException, SelectionFullException, IllegalExtractionException {
        Player p1 = new Player("t1");
        Player p2 = new Player("t2");

        game.addPlayer(p1);
        game.addPlayer(p2);

        this.game.startGame();
        this.game.selectTile(p1, new ArrayList<>(Arrays.asList(
                new Coordinate(1, 3),
                new Coordinate(1, 4)
        )), 0);
    }

    @Test
    void insertTile__ShouldThrowException() throws IllegalFlowException {
        Player p1 = new Player("t1");
        Player p2 = new Player("t2");

        game.addPlayer(p1);
        game.addPlayer(p2);

        this.game.startGame();
        Assertions.assertThrows(IllegalFlowException.class, () -> {
            this.game.selectTile(p2, new ArrayList<>(Arrays.asList(
                    new Coordinate(4, 1),
                    new Coordinate(4, 2),
                    new Coordinate(4, 3)
            )), 0);
        });
    }

    @Test
    void insertTile_playerNull_ShouldThrowException() throws IllegalFlowException {
        Player p1 = new Player("t1");
        Player p2 = new Player("t2");

        game.addPlayer(p1);
        game.addPlayer(p2);

        this.game.startGame();

        Assertions.assertThrows(NullPointerException.class, () -> {
            this.game.selectTile(null, new ArrayList<>(Arrays.asList(
                    new Coordinate(4, 1),
                    new Coordinate(4, 2),
                    new Coordinate(4, 3)
            )), 0);
        });
    }

    @Test
    void getWinner_gameIsNotOver_ShouldThrowException() throws IllegalFlowException {
        Player p1 = new Player("t1");
        Player p2 = new Player("t2");

        game.addPlayer(p1);
        game.addPlayer(p2);

        Assertions.assertFalse(this.game.isOver());

        Assertions.assertThrows(IllegalFlowException.class, () -> {
            game.getWinner();
        });
    }

    @Test
    void getWinner_gameIsNotOver_correctOutput() throws IllegalFlowException, SelectionFullException, IllegalExtractionException {
        Player p1 = new Player("t1");
        Player p2 = new Player("t2");

        game.addPlayer(p1);
        game.addPlayer(p2);

        game.startGame();

        game.selectTile(p1, new ArrayList<>(
                Arrays.asList(
                        new Coordinate(4, 0),
                        new Coordinate(4, 1),
                        new Coordinate(4, 2)
                )
        ), 0);
    }
}
