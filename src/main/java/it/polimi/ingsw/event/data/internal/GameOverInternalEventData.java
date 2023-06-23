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
 * Event used to notify the game is over.
 * This event is used internally by the server.
 * @author Giacomo Groppi
 */
public class GameOverInternalEventData implements EventData {
    public static final String ID = "GAME_OVER_INTERNAL";

    /**
     * The game controller of the game that is over.
     */
    private final GameController gameController;

    /**
     * Constructs a {@link GameOverInternalEventData}.
     * @param controller The game controller of the game that is over.
     */
    public GameOverInternalEventData(GameController controller) {
        this.gameController = controller;
    }

    /**
     * Returns the game controller of the game that is over.
     * @return The game controller of the game that is over.
     */
    public GameController getGameController() {
        return this.gameController;
    }

    public static CastEventReceiver<GameOverInternalEventData> castEventReceiver(EventReceiver<EventData> receiver) {
        return new CastEventReceiver<>(ID, receiver);
    }

    public static <T extends EventData> Requester<GameOverInternalEventData, T> requester(EventTransmitter transmitter,
                                                                                                    EventReceiver<EventData> receiver,
                                                                                                    Object responsesLock) {
        return new Requester<>(ID, transmitter, receiver, responsesLock);
    }

    public static <T extends EventData> Responder<GameOverInternalEventData, T> responder(EventTransmitter transmitter,
                                                                                                    EventReceiver<EventData> receiver,
                                                                                                    Function<GameOverInternalEventData, T> response) {
        return new Responder<>(ID, transmitter, receiver, response);
    }

    @Override
    public String getId() {
        return ID;
    }
}
