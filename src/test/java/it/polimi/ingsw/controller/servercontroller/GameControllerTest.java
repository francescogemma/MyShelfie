package it.polimi.ingsw.controller.servercontroller;

import it.polimi.ingsw.controller.VirtualView;
import it.polimi.ingsw.controller.servercontroller.GameController;
import it.polimi.ingsw.event.EventTransceiver;
import it.polimi.ingsw.event.LocalEventTransceiver;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.data.client.ExitLobbyEventData;
import it.polimi.ingsw.event.data.client.SelectTileEventData;
import it.polimi.ingsw.event.data.game.BoardChangedEventData;
import it.polimi.ingsw.event.data.game.BookshelfHasChangedEventData;
import it.polimi.ingsw.event.data.game.PlayerHasExitLobbyEventData;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.GameView;
import it.polimi.ingsw.model.game.NoPlayerConnectedException;
import it.polimi.ingsw.utils.Coordinate;
import it.polimi.ingsw.utils.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Giacomo Groppi
 * */
class GameControllerTest {
    private GameController gameController;

    private EventTransceiver eventTransceiver1;
    private EventTransceiver eventTransceiver2;
    private EventTransceiver eventTransceiver3;
    private EventTransceiver eventTransceiver4;

    private VirtualView virtualView1;
    private VirtualView virtualView2;
    private VirtualView virtualView3;
    private VirtualView virtualView4;

    private final String username1 = "Giacomo";
    private final String username2 = "Michele";
    private final String username3 = "Cristiano";
    private final String username4 = "Francesco";
    private final String username5 = "Paperino";

    @BeforeEach
    void setUp () {
        this.gameController = new GameController(new Game("Testing", username1));

        eventTransceiver1 = new LocalEventTransceiver();
        eventTransceiver2 = new LocalEventTransceiver();
        eventTransceiver3 = new LocalEventTransceiver();
        eventTransceiver4 = new LocalEventTransceiver();

        virtualView1 = new VirtualView(eventTransceiver1);
        virtualView2 = new VirtualView(eventTransceiver2);
        virtualView3 = new VirtualView(eventTransceiver3);
        virtualView4 = new VirtualView(eventTransceiver4);

        Logger.setShouldPrint(false);
    }

    private VirtualView getNewVirtualView () {
        EventTransceiver transceiver = new LocalEventTransceiver();
        return new VirtualView(transceiver);
    }

    @Test
    void exitGame_ForceExitGameEventData_correctOutput() throws NoPlayerConnectedException {
        LocalEventTransceiver transceiverUsername1 = new LocalEventTransceiver();

        gameController.joinLobby(transceiverUsername1, "Giacomo");

        Assertions.assertFalse(
                gameController.joinLobby(transceiverUsername1, "Giacomo").isOk()
        );

        Assertions.assertEquals(1, gameController.getNumberOfPlayerInLobby());
    }

    @Test
    void insertSelectedTilesInBookshelf__correctOutput() {
        LocalEventTransceiver transceiverUsername1 = new LocalEventTransceiver();

        Assertions.assertFalse(
                gameController.insertSelectedTilesInBookshelf("Giacomo", 3).isOk()
        );

    }

    @Test
    void deselectTile__correctOutput() {
        Assertions.assertFalse(
                gameController.deselectTile("Giacomo", new Coordinate(0, 0)).isOk()
        );
    }

    @Test
    void restartGame__() {
        Assertions.assertFalse(
                gameController.restartGame("Giacomo").isOk()
        );
    }

    @Test
    void reconnectUserDisconnect__() {
        Assertions.assertFalse(
                gameController.rejoinGame("Giacomo", new LocalEventTransceiver()).isOk()
        );
    }

    @Test
    void stopGame_onlyCreatorOnline_correctOutput() {
        gameController.joinLobby(virtualView1, username1);
        gameController.joinLobby(virtualView2, username2);
        gameController.joinLobby(virtualView3, username3);
        gameController.joinLobby(virtualView4, username4);

        gameController.startGame(username1);

        gameController.joinGame(username1);

        Assertions.assertTrue(
                gameController.stopGame(username1).isOk()
        );
    }

    @Test
    void stopGame_onlyOneNotCreatorOnline_correctOutput() {
        gameController.joinLobby(virtualView1, username1);
        gameController.joinLobby(virtualView2, username2);
        gameController.joinLobby(virtualView3, username3);
        gameController.joinLobby(virtualView4, username4);

        gameController.startGame(username1);

        gameController.joinGame(username2);

        Assertions.assertTrue(
                gameController.stopGame(username2).isOk()
        );
    }

    @Test
    void stopGame_twoNotCreatorOnline_correctOutput() {
        gameController.joinLobby(virtualView1, username1);
        gameController.joinLobby(virtualView2, username2);
        gameController.joinLobby(virtualView3, username3);
        gameController.joinLobby(virtualView4, username4);

        gameController.startGame(username1);

        gameController.joinGame(username2);
        gameController.joinGame(username3);

        Assertions.assertFalse(
                gameController.stopGame(username3).isOk()
        );

        Assertions.assertTrue(
                gameController.stopGame(username2).isOk()
        );
    }

    @Test
    void stopGame_playernoInThisGame_correctOutput() {
        gameController.joinLobby(virtualView1, username1);
        gameController.joinLobby(virtualView2, username2);
        gameController.joinLobby(virtualView3, username3);
        gameController.joinLobby(virtualView4, username4);

        gameController.startGame(username1);

        gameController.joinGame(username2);
        gameController.joinGame(username3);

        Assertions.assertFalse(
                gameController.stopGame(username5).isOk()
        );
    }


    // exitLobby
    @Test
    void exitLobby_onlyCreatorOnline_correctOutput() {
        List<EventData> events = new ArrayList<>();
        eventTransceiver2.registerListener(events::add);

        gameController.joinLobby(virtualView1, username1);
        gameController.joinLobby(virtualView2, username2);
        gameController.joinLobby(virtualView3, username3);
        gameController.joinLobby(virtualView4, username4);

        Assertions.assertTrue(
                gameController.exitLobby(username1).isOk()
        );

        Assertions.assertTrue(events.size() >= 1);
        Assertions.assertEquals(PlayerHasExitLobbyEventData.ID, events.get(events.size()-1).getId());
    }

    @Test
    void isFull_threePlayerInLobby_correctOutput() {
        gameController.joinLobby(virtualView1, username1);
        gameController.joinLobby(virtualView2, username2);
        gameController.joinLobby(virtualView3, username3);

        Assertions.assertFalse(gameController.isFull());
    }

    @Test
    void isFull_fourPlayerInLobby_correctOutput() {
        gameController.joinLobby(virtualView1, username1);
        gameController.joinLobby(virtualView2, username2);
        gameController.joinLobby(virtualView3, username3);
        gameController.joinLobby(virtualView4, username4);

        Assertions.assertTrue(gameController.isFull());
    }

    @Test
    void isFull_playerExitLobby_correctOutput() {
        gameController.joinLobby(virtualView1, username1);
        gameController.joinLobby(virtualView2, username2);
        gameController.joinLobby(virtualView3, username3);
        gameController.joinLobby(virtualView4, username4);

        Assertions.assertTrue(gameController.isFull());

        gameController.exitLobby(username1);

        Assertions.assertFalse(gameController.isFull());
    }

    @Test
    void exitGame__correctOutput() {
        gameController.joinLobby(virtualView1, username1);
        gameController.joinLobby(virtualView2, username2);
        gameController.joinLobby(virtualView3, username3);
        gameController.joinLobby(virtualView4, username4);

        gameController.startGame(username1);

        gameController.joinGame(username1);
        gameController.joinGame(username2);
        gameController.joinGame(username3);
        gameController.joinGame(username4);

        Assertions.assertTrue(
                gameController.exitGame(username1).isOk()
        );
    }

    @Test
    void exitGame_playerNotInGame_correctOutput() {
        gameController.joinLobby(virtualView1, username1);
        gameController.joinLobby(virtualView2, username2);
        gameController.joinLobby(virtualView3, username3);
        gameController.joinLobby(virtualView4, username4);

        gameController.startGame(username1);

        gameController.joinGame(username1);
        gameController.joinGame(username2);
        gameController.joinGame(username3);
        gameController.joinGame(username4);

        Assertions.assertFalse(
                gameController.exitGame(username5).isOk()
        );
    }

    @Test
    void exitGame_lastPlayerOnline_correctOutput() {
        gameController.joinLobby(virtualView1, username1);
        gameController.joinLobby(virtualView2, username2);
        gameController.joinLobby(virtualView3, username3);
        gameController.joinLobby(virtualView4, username4);

        gameController.startGame(username1);

        gameController.joinGame(username1);
        gameController.joinGame(username2);
        gameController.joinGame(username3);
        gameController.joinGame(username4);

        Assertions.assertTrue(gameController.exitGame(username1).isOk());
        Assertions.assertTrue(gameController.exitGame(username2).isOk());
        Assertions.assertTrue(gameController.exitGame(username3).isOk());
        Assertions.assertTrue(gameController.exitGame(username4).isOk());

        Assertions.assertTrue(
                gameController.getGameView().isStopped()
        );
    }

    @Test
    void joinLobby_gameAlreadyStartedAndNotStopped_correctOutput() {
        gameController.joinLobby(virtualView1, username1);
        gameController.joinLobby(virtualView2, username2);

        gameController.startGame(username1);

        gameController.joinGame(username1);

        Assertions.assertFalse(
                gameController.joinLobby(virtualView3, username3).isOk()
        );
    }

    @Test
    void joinLobby_gameStoppedPlayerNotInGameOriginaly_correctOutput() {
        gameController.joinLobby(virtualView1, username1);
        gameController.joinLobby(virtualView2, username2);

        gameController.startGame(username1);

        gameController.joinGame(username1);

        gameController.stopGame(username1);

        Assertions.assertFalse(
                gameController.joinLobby(virtualView3, username3).isOk()
        );
    }

    @Test
    void selectTile__correctOutput() {
        gameController.joinLobby(virtualView1, username1);
        gameController.joinLobby(virtualView2, username2);

        gameController.startGame(username1);

        gameController.joinGame(username1);
        gameController.joinGame(username2);

        GameView gameView = gameController.getGameView();

        Assertions.assertTrue(
                gameController.selectTile(username1, gameView.getBoard().getSelectableCoordinate().get(0)).isOk()
        );
    }

    @Test
    void selectTile_playerNotInGame_correctOutput() {
        gameController.joinLobby(virtualView1, username1);
        gameController.joinLobby(virtualView2, username2);

        gameController.startGame(username1);

        gameController.joinGame(username1);
        gameController.joinGame(username2);

        GameView gameView = gameController.getGameView();

        Assertions.assertFalse(
                gameController.selectTile(username3, gameView.getBoard().getSelectableCoordinate().get(0)).isOk()
        );
    }

    @Test
    void selectTile_cantSelectTile_correctOutput() {
        gameController.joinLobby(virtualView1, username1);
        gameController.joinLobby(virtualView2, username2);

        gameController.startGame(username1);

        gameController.joinGame(username1);
        gameController.joinGame(username2);


        Assertions.assertFalse(
                gameController.selectTile(username1, new Coordinate(0,0)).isOk()
        );
    }

    @Test
    void deselectTile_tileNotSelected_correctOutput() {
        gameController.joinLobby(virtualView1, username1);
        gameController.joinLobby(virtualView2, username2);

        gameController.startGame(username1);

        gameController.joinGame(username1);
        gameController.joinGame(username2);

        GameView gameView = gameController.getGameView();

        gameController.selectTile(username1, gameView.getBoard().getSelectableCoordinate().get(0));

        Assertions.assertFalse(
                gameController.deselectTile(username1, new Coordinate(0, 0)).isOk()
        );
    }

    @Test
    void deselectTile_deselectLastSelected_correctOutput() {
        gameController.joinLobby(virtualView1, username1);
        gameController.joinLobby(virtualView2, username2);

        gameController.startGame(username1);

        gameController.joinGame(username1);
        gameController.joinGame(username2);

        GameView gameView = gameController.getGameView();
        Coordinate coordinate = gameView.getBoard().getSelectableCoordinate().get(0);

        gameController.selectTile(username1, coordinate);

        Assertions.assertTrue(
                gameController.deselectTile(username1, coordinate).isOk()
        );
    }

    @Test
    void insertSelectedTilesInBookshelf_correctSelection_correctOutput() {
        List<String> events = new ArrayList<>();

        eventTransceiver1.registerListener(event -> {
            events.add(event.getId());
        });

        gameController.joinLobby(virtualView1, username1);
        gameController.joinLobby(virtualView2, username2);
        gameController.joinLobby(virtualView3, username3);
        gameController.joinLobby(virtualView4, username4);

        gameController.startGame(username1);

        gameController.joinGame(username1);
        gameController.joinGame(username2);
        gameController.joinGame(username3);
        gameController.joinGame(username4);

        GameView gameView = gameController.getGameView();

        gameController.selectTile(username1, gameView.getBoard().getSelectableCoordinate().get(0));
        Assertions.assertEquals(BoardChangedEventData.ID, events.get(events.size() - 1));
        gameController.selectTile(username1, gameView.getBoard().getSelectableCoordinate().get(1));
        Assertions.assertEquals(BoardChangedEventData.ID, events.get(events.size() - 1));
        gameController.selectTile(username1, gameView.getBoard().getSelectableCoordinate().get(2));
        Assertions.assertEquals(BoardChangedEventData.ID, events.get(events.size() - 1));

        Assertions.assertTrue(
                gameController.insertSelectedTilesInBookshelf(username1, 0).isOk()
        );

        Assertions.assertTrue(events.contains(BookshelfHasChangedEventData.ID));
    }

    @Test
    void insertSelectedTilesInBookshelf_noTileSelected_correctOutput() {
        List<String> events = new ArrayList<>();

        eventTransceiver1.registerListener(event -> {
            events.add(event.getId());
        });

        gameController.joinLobby(virtualView1, username1);
        gameController.joinLobby(virtualView2, username2);
        gameController.joinLobby(virtualView3, username3);
        gameController.joinLobby(virtualView4, username4);

        gameController.startGame(username1);

        gameController.joinGame(username1);
        gameController.joinGame(username2);
        gameController.joinGame(username3);
        gameController.joinGame(username4);

        Assertions.assertFalse(
                gameController.insertSelectedTilesInBookshelf(username1, 0).isOk()
        );

        Assertions.assertFalse(events.contains(BookshelfHasChangedEventData.ID));
    }

    @Test
    void restartGame_gameNotStarted_correctOutput() {
        gameController.joinLobby(virtualView1, username1);

        Assertions.assertFalse(
                gameController.restartGame(username1).isOk()
        );
    }

    @Test
    void restartGame__correctOutput() {
        gameController.joinLobby(virtualView1, username1);
        gameController.joinLobby(virtualView2, username2);
        gameController.joinLobby(virtualView3, username3);
        gameController.joinLobby(virtualView4, username4);

        gameController.startGame(username1);

        gameController.joinGame(username1);
        gameController.joinGame(username2);
        gameController.joinGame(username3);
        gameController.joinGame(username4);

        gameController.stopGame(username1);

        gameController.joinLobby(virtualView1, username1);
        gameController.joinLobby(virtualView2, username2);
        gameController.joinLobby(virtualView3, username3);

        Assertions.assertTrue(
                gameController.restartGame(username1).isOk()
        );
    }
}
