package it.polimi.ingsw.event.data.internal;

import it.polimi.ingsw.controller.servercontroller.GameController;
import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;

import java.util.function.Function;

/**
 * Event used for notifying that the game has been stopped.
 * This event is used internally by the server.
 * @author Giacomo Groppi
 */
public class GameHasBeenStoppedInternalEventData implements EventData {
    public static final String ID = "GAME_STOP_INTERNAL";

    /**
     * The game controller of the game that has been stopped.
     * */
    private final GameController gameController;

    /**
     * Constructs a {@link GameHasBeenStoppedInternalEventData}.
     * @param controller The game controller of the game that has been stopped.
     * */
    public GameHasBeenStoppedInternalEventData(GameController controller) {
        this.gameController = controller;
    }

    /**
     * Returns the game controller of the game that has been stopped.
     * @return The game controller of the game that has been stopped.
     * */
    public GameController getGameController() {
        return this.gameController;
    }

    public static CastEventReceiver<GameHasBeenStoppedInternalEventData> castEventReceiver(EventReceiver<EventData> receiver) {
        return new CastEventReceiver<>(ID, receiver);
    }

    public static <T extends EventData> Requester<GameHasBeenStoppedInternalEventData, T> requester(EventTransmitter transmitter,
                                                                                          EventReceiver<EventData> receiver,
                                                                                          Object responsesLock) {
        return new Requester<>(ID, transmitter, receiver, responsesLock);
    }

    public static <T extends EventData> Responder<GameHasBeenStoppedInternalEventData, T> responder(EventTransmitter transmitter,
                                                                                          EventReceiver<EventData> receiver,
                                                                                          Function<GameHasBeenStoppedInternalEventData, T> response) {
        return new Responder<>(ID, transmitter, receiver, response);
    }

    @Override
    public String getId() {
        return ID;
    }
}
