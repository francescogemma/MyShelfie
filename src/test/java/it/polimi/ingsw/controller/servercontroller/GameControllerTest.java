package it.polimi.ingsw.controller.servercontroller;

import it.polimi.ingsw.controller.VirtualView;
import it.polimi.ingsw.controller.servercontroller.GameController;
import it.polimi.ingsw.event.EventTransceiver;
import it.polimi.ingsw.event.LocalEventTransceiver;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.model.game.Game;
import it.polimi.ingsw.model.game.NoPlayerConnectedException;
import it.polimi.ingsw.utils.Coordinate;
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


    @BeforeEach
    void setUp () {
        this.gameController = new GameController(new Game("Testing", "Giacomo"));
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
    void selectTile__correctOutput() {
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
    void insertSelectedTilesInBookshelf__correctOutput() {
        Assertions.assertFalse(gameController.insertSelectedTilesInBookshelf("Giacomo", 4).isOk());
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
}
