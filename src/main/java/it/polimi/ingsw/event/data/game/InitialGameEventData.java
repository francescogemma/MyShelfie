package it.polimi.ingsw.event.data.game;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;
import it.polimi.ingsw.model.game.GameView;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class InitialGameEventData implements EventData {
    private final GameView gameView;

    public InitialGameEventData(GameView gameView) {
        this.gameView = gameView;
    }

    public GameView getGameView () {
        return this.gameView;
    }

    public static final String ID = "GOAL";

    public static CastEventReceiver<InitialGameEventData> castEventReceiver(EventReceiver<EventData> receiver) {
        return new CastEventReceiver<>(ID, receiver);
    }

    public static <T extends EventData> Requester<InitialGameEventData, T> requester(EventTransmitter transmitter,
                                                                                     EventReceiver<EventData> receiver,
                                                                                     Object responsesLock) {
        return new Requester<>(ID, transmitter, receiver, responsesLock);
    }

    public static <T extends EventData> Responder<InitialGameEventData, T> responder(EventTransmitter transmitter,
                                                                                     EventReceiver<EventData> receiver,
                                                                                     Function<InitialGameEventData, T> response) {
        return new Responder<>(ID, transmitter, receiver, response);
    }

    @Override
    public String getId() {
        return ID;
    }
}
