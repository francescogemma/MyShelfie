package it.polimi.ingsw.event.data.internal;

import it.polimi.ingsw.controller.servercontroller.GameController;
import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;

import java.util.function.Function;

public class GameOverInternalEventData implements EventData {
    public static final String ID = "GAME_OVER_INTERNAL";

    private final GameController gameController;

    public GameOverInternalEventData(GameController controller) {
        this.gameController = controller;
    }

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
