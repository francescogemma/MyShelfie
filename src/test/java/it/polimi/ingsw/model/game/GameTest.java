package it.polimi.ingsw.model.game;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.MenuController;
import it.polimi.ingsw.controller.User;
import it.polimi.ingsw.event.LocalEventTransceiver;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.data.game.*;
import it.polimi.ingsw.event.transmitter.EventTransmitter;
import it.polimi.ingsw.model.bag.Bag;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.BoardView;
import it.polimi.ingsw.model.board.FullSelectionException;
import it.polimi.ingsw.model.board.IllegalExtractionException;
import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.tile.Tile;
import it.polimi.ingsw.utils.Coordinate;
import it.polimi.ingsw.utils.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.parallel.ExecutionMode.CONCURRENT;

/**
 * Test for Game
 * @author Giacomo Groppi
 * */
@Execution(CONCURRENT)
class GameTest {
    private Game game;
    private LocalEventTransceiver transceiver;
    private final String creator = "Giacomo";

    private static final int numberOfRun = 1000;

    private Board getBoardFull (int numberOfPlayers) {
        Bag bag = new Bag();
        Board board = new Board();

        while (!bag.isEmpty()) {
            Tile t = bag.getRandomTile();
            try {
                board.fillRandomly(t, numberOfPlayers);
            } catch (Exception e) {
                break;
            }
        }
        return board;
    }

    private boolean isPresentEvent(List<EventData> events, String id) {
        for (EventData event: events) {
            if (event.getId().equals(id))
                return true;
        }
        return false;
    }

    @BeforeEach
    void setUp () {
        game = new Game("testing", creator);
        transceiver = new LocalEventTransceiver();
        game.setTransceiver(transceiver);
        Logger.setShouldPrint(false);
    }

    @Test
    void getName__correctOutput() {
        Game g2 = new Game("testing2", creator);
        Assertions.assertEquals("testing2", g2.getName());
    }

    @Test
    void constructor__throwNullPointerException() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            new Game(null, creator);
        });

        Assertions.assertThrows(NullPointerException.class, () -> {
            new Game("prova", null);
        });

        Assertions.assertThrows(NullPointerException.class, () -> {
            new Game(null, null);
        });
    }

    @Test
    void contructor__throwIllegalArgumentException () {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new Game("", "");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new Game("foo", "");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new Game("", "bar");
        });
    }

    @Test
    void getStartingPlayer__correctOutput () throws IllegalFlowException, PlayerAlreadyInGameException {
        this.game.addPlayer("Giacomo");
        Assertions.assertEquals("Giacomo", game.getStartingPlayer().getUsername());
    }

    @Test
    void getStartingPlayer__throw () {
        Assertions.assertThrows(IllegalFlowException.class, () -> {
            game.getStartingPlayer();
        });
    }

    @Test
    void getPlayers__correctOutput () throws IllegalFlowException, PlayerAlreadyInGameException {
        game.addPlayer("Giacomo");
        Assertions.assertEquals(1, game.getPlayers().size());
        Assertions.assertEquals("Giacomo", game.getPlayers().get(0).getUsername());
    }

    @Test
    void isOver__correctOutput () {
        Assertions.assertFalse(game.isOver());
    }

    @Test
    void addPlayer__throwPlayerAlreadyInGameException() throws IllegalFlowException, PlayerAlreadyInGameException {
        game.addPlayer("Giacomo");
        Assertions.assertThrows(PlayerAlreadyInGameException.class, () -> {
            game.addPlayer("Giacomo");
        });
    }

    @Test
    void addPlayer_nullPointer_throwNullPointerException () {
        Assertions.assertThrows(NullPointerException.class, () -> {
            game.addPlayer(null);
        });
    }

    @Test
    void addPlayer_stringEmpty_throwNullPointerException() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            game.addPlayer("");
        });
    }

    @Test
    void addPlayer_gameAlreadyStarted_throwIllegalFlowException() throws IllegalFlowException, PlayerAlreadyInGameException {
        game.addPlayer("Giacomo");
        game.addPlayer("Cristiano");
        game.addPlayer("Michele");

        game.startGame("Giacomo");

        Assertions.assertThrows(IllegalFlowException.class, () -> {
            game.addPlayer("Francesco");
        });
    }

    @Test
    void addPlayer_already4_throwIllegalFlowException() throws IllegalFlowException, PlayerAlreadyInGameException {
        game.addPlayer("Giacomo");
        game.addPlayer("Cristiano");
        game.addPlayer("Michele");
        game.addPlayer("Francesco");

        Assertions.assertThrows(IllegalFlowException.class, () -> {
            game.addPlayer("Paperino");
        });
    }

    @Test
    void startGame_onePlayer_throwIllegalFlowException () throws IllegalFlowException, PlayerAlreadyInGameException {
        game.addPlayer("Giacomo");
        Assertions.assertThrows(IllegalFlowException.class, () -> {
            game.startGame("Giacomo");
        });
    }

    @Test
    void startGame_0Players_throwIllegalFlowException () {
        Assertions.assertThrows(IllegalFlowException.class, () -> {
            game.startGame("");
        });
    }

    @Test
    void getCurrentPlayer__correctOutput () throws IllegalFlowException, PlayerAlreadyInGameException {
        game.addPlayer("Giacomo");
        game.addPlayer("Michele");

        game.startGame("Giacomo");
        Assertions.assertEquals("Giacomo", game.getCurrentPlayer().getUsername());
    }

    @Test
    void getCurrentPlayer_gameNotStarted_throwsIllegalFlowException () {
        Assertions.assertThrows(IllegalFlowException.class, () -> {
            game.getCurrentPlayer();
        });
    }

    @Test
    void setTransceiver_nullPointer_throws () {
        this.game = new Game("testing", creator);
        Assertions.assertThrows(NullPointerException.class, () -> {
            game.setTransceiver(null);
        });
    }

    @Test
    void addPlayer_forReconnection_correctOutput() throws IllegalFlowException, PlayerAlreadyInGameException, PlayerNotInGameException {
        game.addPlayer("Giacomo");
        game.addPlayer("Michele");

        AtomicReference<String> username = new AtomicReference<>("");

        PlayerHasDisconnectedEventData.castEventReceiver(transceiver).registerListener(
                event -> {
                    username.set(event.username());
                }
        );

        game.startGame("Giacomo");
        game.connectPlayer("Giacomo");

        game.disconnectPlayer("Giacomo");

        Assertions.assertEquals("Giacomo", username.get());
    }

    @Test
    void getLastPlayer__correctOutput() throws IllegalFlowException, PlayerAlreadyInGameException {
        game.addPlayer("Giacomo");
        game.addPlayer("Michele");
        game.addPlayer("Cristiano");

        Assertions.assertThrows(IllegalFlowException.class, () -> {
            game.getLastPlayer();
        });

        game.startGame("Giacomo");

        Assertions.assertEquals("Cristiano", game.getLastPlayer().getUsername());
    }

    @Test
    void disconnectPlayer__correctOutput() throws IllegalFlowException, PlayerAlreadyInGameException, PlayerNotInGameException {
        game.addPlayer("Giacomo");
        game.addPlayer("Francesco");
        game.addPlayer("Cristiano");
        game.addPlayer("Michele");


        game.startGame("Giacomo");

        Assertions.assertEquals("Giacomo", game.getCurrentPlayer().getUsername());

        game.connectPlayer("Giacomo");
        game.connectPlayer("Francesco");
        game.connectPlayer("Cristiano");
        game.connectPlayer("Michele");

        game.disconnectPlayer("Giacomo");



        Assertions.assertEquals("Francesco", game.getCurrentPlayer().getUsername());
    }

    @Test
    void disconnectPlayer_twoPlayerDisconnected_correctOutput() throws IllegalFlowException, PlayerAlreadyInGameException, PlayerNotInGameException {
        game.addPlayer("Giacomo");
        game.addPlayer("Francesco");
        game.addPlayer("Cristiano");
        game.addPlayer("Michele");


        game.startGame("Giacomo");
        game.connectPlayer("Giacomo");
        game.connectPlayer("Francesco");
        game.connectPlayer("Cristiano");
        game.connectPlayer("Michele");

        Assertions.assertEquals("Giacomo", game.getCurrentPlayer().getUsername());

        game.disconnectPlayer("Giacomo");
        game.disconnectPlayer("Francesco");

        Assertions.assertEquals("Cristiano", game.getCurrentPlayer().getUsername());
    }


    @Test
    void disconnectPlayer_fourPlayerDisconnected_correctOutput() throws IllegalFlowException, PlayerAlreadyInGameException, PlayerNotInGameException {
        game.addPlayer("Giacomo");
        game.addPlayer("Francesco");
        game.addPlayer("Cristiano");
        game.addPlayer("Michele");


        game.startGame("Giacomo");

        game.connectPlayer("Giacomo");
        game.connectPlayer("Francesco");
        game.connectPlayer("Cristiano");
        game.connectPlayer("Michele");

        Assertions.assertEquals("Giacomo", game.getCurrentPlayer().getUsername());

        game.disconnectPlayer("Giacomo");
        game.disconnectPlayer("Francesco");
        game.disconnectPlayer("Cristiano");
        game.disconnectPlayer("Michele");

        Assertions.assertTrue(game.isStopped());

    }

    @Test
    void selectTile_signalsEmitted_correctOutput() throws IllegalFlowException, PlayerAlreadyInGameException, IllegalExtractionException, FullSelectionException {
        AtomicBoolean call = new AtomicBoolean(false);
        AtomicReference<BoardView> boardViewCall = new AtomicReference<>(null);

        game.addPlayer("Giacomo");
        game.addPlayer("Michele");
        game.addPlayer("Cristiano");

        BoardChangedEventData.castEventReceiver(transceiver).registerListener(event -> {
            call.set(true);
            boardViewCall.set(event.board());
        });

        try {
            game.selectTile("Giacomo", new Coordinate(4, 0));
        } catch (IllegalFlowException e) {

        }

        Assertions.assertFalse(call.get());

        game.startGame("Giacomo");

        call.set(false);
        try {
            game.selectTile("Michele", new Coordinate(4, 0));
        } catch (IllegalFlowException e) {

        }

        Assertions.assertFalse(call.get());

        game.selectTile("Giacomo", new Coordinate(4, 0));

        Assertions.assertTrue(call.get());
        Assertions.assertEquals(boardViewCall.get(), this.game.getBoard());

        call.set(false);
        boardViewCall.set(null);

        try {
            game.selectTile("Giacomo", new Coordinate(4, 3));
            assert false: "This functions should throw IllegalExtractionException";
        } catch (IllegalExtractionException e) {

        }

        Assertions.assertFalse(call.get());
    }

    @Test
    void insertTile_signalsEmitted_correctOutput () throws IllegalFlowException, PlayerAlreadyInGameException, IllegalExtractionException, FullSelectionException, PlayerNotInGameException {
        AtomicBoolean call = new AtomicBoolean(false);
        AtomicReference<BoardView> boardViewCall = new AtomicReference<>(null);

        game.addPlayer("Giacomo");
        game.addPlayer("Michele");
        game.addPlayer("Cristiano");

        BoardChangedEventData.castEventReceiver(transceiver).registerListener(event -> {
            call.set(true);
            boardViewCall.set(event.board());
        });

        game.startGame("Giacomo");

        game.connectPlayer("Giacomo");
        game.connectPlayer("Michele");
        game.connectPlayer("Cristiano");

        Assertions.assertThrows(IllegalExtractionException.class, () -> {
            game.insertTile("Giacomo", 0);
        });

        game.selectTile("Giacomo", new Coordinate(4, 0));
        game.selectTile("Giacomo", new Coordinate(5, 0));

        Assertions.assertThrows(IllegalFlowException.class, () -> {
            game.insertTile("Michele", 0);
        });

        game.insertTile("Giacomo", 4);

        Assertions.assertTrue(call.get());
        Assertions.assertEquals(game.getBoard(), boardViewCall.get());
    }

    @Test
    void addPlayer_signalsEmitted_correctOutput() throws IllegalFlowException, PlayerAlreadyInGameException {
        transceiver.registerListener(event -> {
            Assertions.assertEquals(PlayerHasJoinGameEventData.ID, event.getId());
            Assertions.assertEquals("Giacomo", ((PlayerHasJoinGameEventData) event).username());
        });

        game.addPlayer("Giacomo");
    }

    @Test
    void addPlayer_playerAlreadyInGameButDisconnected_correctOutput () throws IllegalFlowException, PlayerAlreadyInGameException, PlayerNotInGameException {
        AtomicBoolean set = new AtomicBoolean(false);
        game.addPlayer("Giacomo");
        game.addPlayer("Michele");
        game.addPlayer("Cristiano");

        transceiver.registerListener(event -> {
            if (set.get()) {
                Assertions.assertEquals(PlayerHasJoinGameEventData.ID, event.getId());
                Assertions.assertEquals("Giacomo", ((PlayerHasJoinGameEventData) event).username());
            }
        });

        game.startGame("Giacomo");

        game.connectPlayer("Michele");
        game.connectPlayer("Cristiano");
        game.connectPlayer("Giacomo");

        game.disconnectPlayer("Giacomo");

        set.set(true);
        game.connectPlayer("Giacomo");
    }

    /*
    @Test
    void hasPlayerDisconnected_onePlayerDisconnected_correctOutput () throws IllegalFlowException, PlayerAlreadyInGameException, PlayerNotInGameException {
        game.addPlayer("Giacomo");
        game.addPlayer("Michele");

        game.connectPlayer("Giacomo");
        game.connectPlayer("Michele");

        game.disconnectPlayer("Giacomo");

        Assertions.assertTrue(game.hasPlayerDisconnected());
        game.disconnectPlayer("Michele");
        Assertions.assertTrue(game.hasPlayerDisconnected());

        game.connectPlayer("Giacomo");
        game.connectPlayer("Michele");

        Assertions.assertFalse(game.hasPlayerDisconnected());
    }
*/
    @RepeatedTest(numberOfRun)
    void isOver_signalsEmittedLastPlayerCompleteBookshelf_correctOutput () throws IllegalFlowException, PlayerAlreadyInGameException, IllegalExtractionException, FullSelectionException, PlayerNotInGameException {
        final String usernameUser1 = "Giacomo";
        final String usernameUser2 = "Michele";

        game.addPlayer(usernameUser1);
        game.addPlayer(usernameUser2);

        game.startGame("Giacomo");

        game.connectPlayer("Giacomo");
        game.connectPlayer("Michele");

        game.selectTile(usernameUser1, new Coordinate(4, 0));
        game.insertTile(usernameUser1, 0);

        game.selectTile(usernameUser2, new Coordinate(3, 1));
        game.selectTile(usernameUser2, new Coordinate(4, 1));
        game.selectTile(usernameUser2, new Coordinate(5, 1));

        game.insertTile(usernameUser2, 0);

        game.selectTile(usernameUser1, new Coordinate(2, 2));
        game.insertTile(usernameUser1, 0);

        game.selectTile(usernameUser2, new Coordinate(3, 2));
        game.selectTile(usernameUser2, new Coordinate(4, 2));
        game.selectTile(usernameUser2, new Coordinate(5, 2));
        game.insertTile(usernameUser2, 0);

        game.selectTile(usernameUser1, new Coordinate(7, 3));
        game.insertTile(usernameUser1, 0);

        game.selectTile(usernameUser2, new Coordinate(6, 3));
        game.selectTile(usernameUser2, new Coordinate(5, 3));
        game.selectTile(usernameUser2, new Coordinate(4, 3));
        game.insertTile(usernameUser2, 1);

        game.selectTile(usernameUser1, new Coordinate(1, 4));
        game.insertTile(usernameUser1, 0);

        game.selectTile(usernameUser2, new Coordinate(1, 3));
        game.selectTile(usernameUser2, new Coordinate(2, 3));
        game.selectTile(usernameUser2, new Coordinate(3, 3));
        game.insertTile(usernameUser2, 1);

        game.selectTile(usernameUser1, new Coordinate(3, 7));
        game.insertTile(usernameUser1, 0);

        game.selectTile(usernameUser2, new Coordinate(2, 4));
        game.selectTile(usernameUser2, new Coordinate(3, 4));
        game.selectTile(usernameUser2, new Coordinate(4, 4));
        game.insertTile(usernameUser2, 2);

        game.selectTile(usernameUser1, new Coordinate(4, 7));
        game.insertTile(usernameUser1, 0);

        game.selectTile(usernameUser2, new Coordinate(2, 5));
        game.selectTile(usernameUser2, new Coordinate(3, 5));
        game.selectTile(usernameUser2, new Coordinate(4, 5));
        game.insertTile(usernameUser2, 2);

        game.selectTile(usernameUser1, new Coordinate(3, 6));
        game.selectTile(usernameUser1, new Coordinate(4, 6));
        game.insertTile(usernameUser1, 1);

        game.selectTile(usernameUser2, new Coordinate(5, 6));
        game.selectTile(usernameUser2, new Coordinate(5, 5));
        game.selectTile(usernameUser2, new Coordinate(5, 4));
        game.insertTile(usernameUser2, 3);

        game.selectTile(usernameUser1, new Coordinate(6, 4));
        game.selectTile(usernameUser1, new Coordinate(6, 5));
        game.insertTile(usernameUser1, 2);

        game.selectTile(usernameUser2, new Coordinate(7, 4));
        game.selectTile(usernameUser2, new Coordinate(7, 5));
        game.insertTile(usernameUser2, 3);

        game.selectTile(usernameUser1, new Coordinate(4, 0));
        game.insertTile(usernameUser1, 1);

        game.selectTile(usernameUser2, new Coordinate(3, 1));
        game.selectTile(usernameUser2, new Coordinate(4, 1));
        game.selectTile(usernameUser2, new Coordinate(5, 1));

        game.insertTile(usernameUser2, 4);

        game.selectTile(usernameUser1, new Coordinate(2, 2));
        game.insertTile(usernameUser1, 3);

        game.selectTile(usernameUser2, new Coordinate(3, 2));
        game.selectTile(usernameUser2, new Coordinate(4, 2));
        game.selectTile(usernameUser2, new Coordinate(5, 2));
        game.insertTile(usernameUser2, 4);

        game.selectTile(usernameUser1, new Coordinate(7, 3));
        game.insertTile(usernameUser1, 3);

        AtomicBoolean call = new AtomicBoolean(false);
        GameOverEventData.castEventReceiver(transceiver).registerListener(event -> {
            call.set(true);
            // we can't test the winner name because it depends on the tile in the board.
        });

        game.selectTile(usernameUser2, new Coordinate(6, 3));
        try {
            game.insertTile(usernameUser2, 3);
        } catch (IllegalStateException e) {
            Assertions.fail();
        }

        Assertions.assertTrue(call.get());
        Assertions.assertTrue(game.isOver());
    }


    @Test
    void forgetLastSelection_correctDeselect_correctOutput() throws IllegalFlowException, PlayerAlreadyInGameException, IllegalExtractionException, FullSelectionException {
        List<EventData> events = new ArrayList<>();

        BoardChangedEventData.castEventReceiver(transceiver).registerListener(events::add);
        PlayerHasDeselectTile.castEventReceiver(transceiver).registerListener(events::add);

        final String username1 = "Giacomo";
        final String username2 = "Michele";

        this.game.addPlayer(username1);
        this.game.addPlayer(username2);

        Assertions.assertThrows(IllegalFlowException.class, () -> {
            game.forgetLastSelection(username1, new Coordinate(4, 0));
        });

        this.game.startGame("Giacomo");

        this.game.selectTile(username1, new Coordinate(4, 0));

        // not current player
        Assertions.assertThrows(IllegalFlowException.class, () -> {
            game.forgetLastSelection(username2, new Coordinate(4, 0));
        });

        this.game.forgetLastSelection(username1, new Coordinate(4, 0));

        Assertions.assertTrue(this.isPresentEvent(events, BoardChangedEventData.ID));
        Assertions.assertTrue(this.isPresentEvent(events, PlayerHasDeselectTile.ID));
    }

    @Test
    void startGame_incorrectOwnership_correctOutput() throws IllegalFlowException, PlayerAlreadyInGameException {
        game = new Game("testing", "Giacomo");

        game.setTransceiver(new LocalEventTransceiver());

        game.addPlayer("Giacomo");
        game.addPlayer("Michele");

        Assertions.assertThrows(IllegalFlowException.class, () -> {
            game.startGame("Michele");
        });

        game.startGame("Giacomo");
    }

    @Test
    void isPause_onlyOnePlayerConnectedAfterStart_correctOutput () throws IllegalFlowException, PlayerAlreadyInGameException, PlayerNotInGameException {
        game.addPlayer("Giacomo");
        game.addPlayer("Michele");
        game.addPlayer("Cristiano");

        game.startGame("Giacomo");

        game.connectPlayer("Michele");

        Assertions.assertTrue(game.isPause());
    }

    @Test
    void isPause_twoPlayerConnectedAfterStart_correctOutput () throws IllegalFlowException, PlayerAlreadyInGameException, PlayerNotInGameException {
        game.addPlayer("Giacomo");
        game.addPlayer("Michele");
        game.addPlayer("Cristiano");

        game.startGame("Giacomo");

        game.connectPlayer("Michele");
        game.connectPlayer("Cristiano");

        Assertions.assertFalse(game.isPause());
    }

    @RepeatedTest(4)
    void isPause_afterTimeFirstPlayerLoseTurn_correctOutput () throws IllegalFlowException, PlayerAlreadyInGameException, PlayerNotInGameException, InterruptedException, NoSuchFieldException, IllegalAccessException {
        game.addPlayer("Giacomo");
        game.addPlayer("Michele");
        game.addPlayer("Cristiano");

        game.startGame("Giacomo");

        game.connectPlayer("Michele");
        game.connectPlayer("Cristiano");

        Assertions.assertFalse(game.isPause());
        Assertions.assertEquals("Giacomo", game.getCurrentPlayer().getUsername());

        Thread.sleep(Game.TIME_FIRST_PLAYER_CONNECT + 1000);

        Assertions.assertEquals("Michele", game.getCurrentPlayer().getUsername());
    }

    @RepeatedTest(4)
    void isOver_afterAPause_correctOutput() throws IllegalFlowException, PlayerAlreadyInGameException, PlayerNotInGameException, InterruptedException {
        game.addPlayer("Giacomo");
        game.addPlayer("Michele");
        game.addPlayer("Cristiano");

        game.startGame("Giacomo");

        game.connectPlayer("Giacomo");
        game.connectPlayer("Michele");

        game.disconnectPlayer("Giacomo");

        Assertions.assertTrue(game.isPause());

        Thread.sleep(GameView.TIME_PAUSE_BEFORE_WIN + 1000);

        Assertions.assertTrue(game.isOver());
        Assertions.assertEquals(1, game.getWinners().size());
        Assertions.assertEquals("Michele", game.getWinners().get(0).getUsername());
    }

    @RepeatedTest(numberOfRun)
    void stopGame_allPlayerConnected_correctOutput() throws PlayerNotInGameException, IllegalFlowException, PlayerAlreadyInGameException {
        game.addPlayer("Giacomo");
        game.addPlayer("Michele");
        game.addPlayer("Cristiano");

        game.startGame("Giacomo");

        game.connectPlayer("Giacomo");
        game.connectPlayer("Michele");
        game.connectPlayer("Cristiano");

        game.stopGame("Giacomo");
        Assertions.assertTrue(game.isStopped());
    }

    @RepeatedTest(numberOfRun)
    void stopGame_OnePlayerConnected_correctOutput() throws PlayerNotInGameException, IllegalFlowException, PlayerAlreadyInGameException {
        game.addPlayer("Giacomo");
        game.addPlayer("Michele");
        game.addPlayer("Cristiano");

        game.startGame("Giacomo");

        game.connectPlayer("Michele");

        game.stopGame("Michele");
        Assertions.assertTrue(game.isStopped());
    }

    @RepeatedTest(numberOfRun)
    void stopGame_PlayerAskNotOwner_throwIllegalFlowException() throws PlayerNotInGameException, IllegalFlowException, PlayerAlreadyInGameException {
        game.addPlayer("Giacomo");
        game.addPlayer("Michele");
        game.addPlayer("Cristiano");

        game.startGame("Giacomo");

        game.connectPlayer("Michele");
        game.connectPlayer("Giacomo");
        game.connectPlayer("Cristiano");

        Assertions.assertThrows(IllegalFlowException.class, () -> {
            game.stopGame("Michele");
        });
    }

    @RepeatedTest(numberOfRun)
    void removePlayer__correctOutput() throws IllegalFlowException, PlayerAlreadyInGameException {
        game.addPlayer("Giacomo");
        game.removePlayer("Giacomo");

        Assertions.assertEquals(0, game.getPlayers().size());
    }

    @RepeatedTest(numberOfRun)
    void removePlayer_removeNotInGame_correctOutput() throws IllegalFlowException, PlayerAlreadyInGameException {
        game.addPlayer("Giacomo");
        game.addPlayer("Michele");

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            game.removePlayer("Cristiano");
        });
    }

    @RepeatedTest(numberOfRun)
    void removePlayer_gameAlreadyStarted_throwsIllegalFlowException() throws IllegalFlowException, PlayerAlreadyInGameException {
        game.addPlayer("Giacomo");
        game.addPlayer("Michele");

        game.startGame("Giacomo");

        Assertions.assertThrows(IllegalFlowException.class, () -> {
            game.addPlayer("Pippo");
        });
    }

    @RepeatedTest(numberOfRun)
    void isPlayerConnected__correctOutput() throws IllegalFlowException, PlayerAlreadyInGameException, PlayerNotInGameException {
        game.addPlayer("Giacomo");
        game.addPlayer("Michele");

        game.startGame("Giacomo");

        Assertions.assertFalse(
                game.isPlayerConnected("Giacomo")
        );

        game.connectPlayer("Giacomo");
        game.connectPlayer("Michele");

        Assertions.assertTrue(
            game.isPlayerConnected("Giacomo")
        );
    }

    @RepeatedTest(numberOfRun)
    void restartGame__correctOutput() throws IllegalFlowException, PlayerAlreadyInGameException, PlayerNotInGameException {
        game.addPlayer("Giacomo");
        game.addPlayer("Michele");

        game.startGame("Giacomo");

        game.connectPlayer("Giacomo");
        game.connectPlayer("Michele");

        game.stopGame("Giacomo");

        Assertions.assertTrue(game.isStopped());

        game.restartGame("Giacomo");

        Assertions.assertTrue(game.isStarted());
        Assertions.assertFalse(game.isPlayerConnected("Giacomo"));
        Assertions.assertFalse(game.isPlayerConnected("Michele"));
    }

    @RepeatedTest(numberOfRun)
    void restartGame_skipTurnPlayerNotConnected_correctOutput() throws IllegalFlowException, PlayerAlreadyInGameException, PlayerNotInGameException, IllegalExtractionException, FullSelectionException {
        game.addPlayer("Giacomo");
        game.addPlayer("Michele");
        game.addPlayer("Cristiano");

        game.startGame("Giacomo");

        game.connectPlayer("Giacomo");
        game.connectPlayer("Michele");

        game.selectTile("Giacomo", game.getBoard().getSelectableCoordinate().get(0));
        game.insertTile("Giacomo", 0);

        game.selectTile("Michele", game.getBoard().getSelectableCoordinate().get(0));
        game.insertTile("Michele", 0);

        game.stopGame("Giacomo");

        game.setPlayersToWait(Arrays.asList("Giacomo", "Michele"));
        game.restartGame("Giacomo");

        Assertions.assertTrue(game.isStarted());
        Assertions.assertFalse(game.isPlayerConnected("Giacomo"));
        Assertions.assertFalse(game.isPlayerConnected("Michele"));

        Assertions.assertEquals("Giacomo", game.getCurrentPlayer().getUsername());
    }

    @RepeatedTest(numberOfRun)
    void restartGame_playerNotOwner_throwsIllegalFlowException() throws IllegalFlowException, PlayerAlreadyInGameException, PlayerNotInGameException, IllegalExtractionException, FullSelectionException {
        game.addPlayer("Giacomo");
        game.addPlayer("Michele");
        game.addPlayer("Cristiano");

        game.startGame("Giacomo");

        game.connectPlayer("Giacomo");
        game.connectPlayer("Michele");

        game.selectTile("Giacomo", game.getBoard().getSelectableCoordinate().get(0));
        game.insertTile("Giacomo", 0);

        game.selectTile("Michele", game.getBoard().getSelectableCoordinate().get(0));
        game.insertTile("Michele", 0);

        game.stopGame("Giacomo");

        game.setPlayersToWait(Arrays.asList("Giacomo", "Michele"));

        Assertions.assertThrows(IllegalFlowException.class, () -> {
            game.restartGame("Michele");
        });

        Assertions.assertTrue(game.isStarted());
        Assertions.assertTrue(game.isStopped());
    }

    @RepeatedTest(numberOfRun)
    void createView__correctOutput() throws IllegalFlowException, PlayerAlreadyInGameException, PlayerNotInGameException {
        game.addPlayer("Giacomo");
        game.addPlayer("Michele");

        game.startGame("Giacomo");

        game.connectPlayer("Giacomo");
        game.connectPlayer("Michele");

        GameView view = game.createView();

        Assertions.assertEquals(view.getPlayers(), game.getPlayers());
        Assertions.assertEquals(view.isStarted(), game.isStarted());
        Assertions.assertEquals(view.getName(), game.getName());
        Assertions.assertEquals(view.getBoard(), game.getBoard());
    }

    @Test
    void hasPlayerDisconnected__correctOutput() throws IllegalFlowException, PlayerAlreadyInGameException {
        game.addPlayer("Giacomo");
        game.addPlayer("Michele");

        game.startGame("Giacomo");

        Assertions.assertTrue(game.hasPlayerDisconnected());
    }

    @Test
    void canStartGame_playerNull_throwNullPointerException() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            game.canStartGame(null);
        });
    }

    @Test
    void isAvailableForJoin__correctOutput() throws IllegalFlowException, PlayerAlreadyInGameException {
        Assertions.assertTrue(game.isAvailableForJoin("Giacomo"));

        game.addPlayer("Giacomo");
        game.addPlayer("Michele");

        game.startGame("Giacomo");

        Assertions.assertFalse(game.isAvailableForJoin("Cristiano"));
    }

    @Test
    void getCurrentPlayer_gameOver_throwIllegalFlowException() throws IllegalFlowException, PlayerAlreadyInGameException, PlayerNotInGameException, InterruptedException {
        game.addPlayer("Giacomo");
        game.addPlayer("Michele");

        game.startGame("Giacomo");

        game.connectPlayer("Giacomo");

        Thread.sleep(GameView.TIME_PAUSE_BEFORE_WIN + 1000);

        Assertions.assertThrows(IllegalFlowException.class, () -> {
            game.getCurrentPlayer();
        });
    }
}
