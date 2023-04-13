package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.game.Game;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tester for Game
 * @author Giacomo Groppi
 * */
class GameTest {
    private Game g;

    @BeforeEach
    void setUp () {
        g = new Game("testing");
    }

    @Test
    void getName__correctOutput() {
        Game g2 = new Game("testing2");
        Assertions.assertEquals("testing2", g2.getName());
    }

    @Test
    void constructor__throwNullPointerException() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            new Game(null);
        });
    }

    @Test
    void contructor__throwIllegalArgumentException () {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new Game("");
        });
    }

    @Test
    void getStartingPlayer__correctOutput () throws IllegalFlowException, PlayerAlreadyInGameException {
        this.g.addPlayer("Giacomo");
        Assertions.assertEquals("Giacomo", g.getStartingPlayer().getUsername());
    }

    @Test
    void getStartingPlayer__throw () {
        Assertions.assertThrows(IllegalFlowException.class, () -> {
            g.getStartingPlayer();
        });
    }

    @Test
    void getPlayers__correctOutput () throws IllegalFlowException, PlayerAlreadyInGameException {
        g.addPlayer("Giacomo");
        Assertions.assertEquals(1, g.getPlayers().size());
        Assertions.assertEquals("Giacomo", g.getPlayers().get(0).getUsername());
    }

    @Test
    void isOver__correctOutput () {
        Assertions.assertFalse(g.isOver());
    }

    @Test
    void addPlayer__throwPlayerAlreadyInGameException() throws IllegalFlowException, PlayerAlreadyInGameException {
        g.addPlayer("Giacomo");
        Assertions.assertThrows(PlayerAlreadyInGameException.class, () -> {
            g.addPlayer("Giacomo");
        });
    }

    @Test
    void addPlayer_nullPointer_throwNullPointerException () throws IllegalFlowException, PlayerAlreadyInGameException {
        Assertions.assertThrows(NullPointerException.class, () -> {
            g.addPlayer(null);
        });
    }

    @Test
    void addPlayer_stringEmpty_throwNullPointerException() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            g.addPlayer("");
        });
    }

    @Test
    void addPlayer_gameAlreadyStarted_throwIllegalFlowException() throws IllegalFlowException, PlayerAlreadyInGameException {
        g.addPlayer("Giacomo");
        g.addPlayer("Cristiano");
        g.addPlayer("Michele");

        g.startGame();

        Assertions.assertThrows(IllegalFlowException.class, () -> {
            g.addPlayer("Francesco");
        });
    }

    @Test
    void addPlayer_already4_throwIllegalFlowException() throws IllegalFlowException, PlayerAlreadyInGameException {
        g.addPlayer("Giacomo");
        g.addPlayer("Cristiano");
        g.addPlayer("Michele");
        g.addPlayer("Francesco");

        Assertions.assertThrows(IllegalFlowException.class, () -> {
            g.addPlayer("Paperino");
        });
    }

    @Test
    void startGame_onePlayer_throwIllegalFlowException () throws IllegalFlowException, PlayerAlreadyInGameException {
        g.addPlayer("Giacomo");
        Assertions.assertThrows(IllegalFlowException.class, () -> {
            g.startGame();
        });
    }

    @Test
    void startGame_0Players_throwIllegalFlowException () {
        Assertions.assertThrows(IllegalFlowException.class, () -> {
            g.startGame();
        });
    }

    @Test
    void getCurrentPlayer__correctOutput () throws IllegalFlowException, PlayerAlreadyInGameException {
        g.addPlayer("Giacomo");
        g.addPlayer("Michele");
        g.startGame();
        Assertions.assertEquals("Giacomo", g.getCurrentPlayer().getUsername());
    }

    @Test
    void getCurrentPlayer_secondHand_correctOutput () throws IllegalFlowException, PlayerAlreadyInGameException {
        g.addPlayer("Giacomo");
        g.addPlayer("Michele");
        g.startGame();
        Assertions.assertEquals("Giacomo", g.getCurrentPlayer().getUsername());

        g.nextTurn();

        Assertions.assertEquals("Michele", g.getCurrentPlayer().getUsername());
    }

    @Test
    void getCurrentPlayer_gameNotStarted_throwsIllegalFlowException () {
        Assertions.assertThrows(IllegalFlowException.class, () -> {
            g.getCurrentPlayer();
        });
    }

    @Test
    void getCurrentPlayer_gameIsOver_throwsIllegalFlowException () throws IllegalFlowException, PlayerAlreadyInGameException {
        Player p;
        g.addPlayer("Giacomo");
        g.addPlayer("Michele");
        p = g.getPlayers().get(0);

        g.startGame();

        g.setWinner(p);

        Assertions.assertThrows(IllegalFlowException.class, () -> {
            g.getCurrentPlayer();
        });
    }

    @Test
    void getWinner__correctOutput () throws IllegalFlowException, PlayerAlreadyInGameException {
        Player p = g.addPlayer("Giacomo");
        g.addPlayer("Michele");

        g.startGame();
        g.setWinner(p);

        Assertions.assertEquals(p, g.getWinner());
    }

    @Test
    void setWinner_gameNotStarted_throwIllegalFlowException () throws IllegalFlowException, PlayerAlreadyInGameException {
        Player p = g.addPlayer("Giacomo");
        g.addPlayer("Michele");

        Assertions.assertThrows(IllegalFlowException.class, () -> {
            g.setWinner(p);
        });
    }

    @Test
    void setWinner_playerNotInGame_throwIllegalArgumentException () throws IllegalFlowException, PlayerAlreadyInGameException {
        Player p = g.addPlayer("Giacomo");
        g.addPlayer("Michele");

        g.startGame();

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            g.setWinner(new Player("Cristiano"));
        });
    }

    @Test
    void setWinner_winnerAlreadySet_throwIllegalFlowException () throws IllegalFlowException, PlayerAlreadyInGameException {
        Player p = g.addPlayer("Giacomo");
        g.addPlayer("Michele");

        g.startGame();

        g.setWinner(p);

        Assertions.assertThrows(IllegalFlowException.class, () -> {
            g.setWinner(p);
        });
    }

    @Test
    void getWinner_gameNotOver_throw () throws IllegalFlowException, PlayerAlreadyInGameException {
        Player p = g.addPlayer("Giacomo");
        g.addPlayer("Michele");

        g.startGame();

        Assertions.assertThrows(IllegalFlowException.class, () -> {
            g.getWinner();
        });
    }
}

