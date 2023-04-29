package it.polimi.ingsw.event.data.game;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;
import it.polimi.ingsw.utils.Logger;

import java.util.function.Function;

public record GameIsNoLongerAvailableEventData(String gameName) implements EventData {
    public static final String ID = "GAME_IS_NO_LONGER_AVAILABLE";

    public GameIsNoLongerAvailableEventData {
    }

    public static CastEventReceiver<GameIsNoLongerAvailableEventData> castEventReceiver(EventReceiver<EventData> receiver) {
        return new CastEventReceiver<>(ID, receiver);
    }

    public static <T extends EventData> Requester<GameIsNoLongerAvailableEventData, T> requester(EventTransmitter transmitter,
                                                                                                 EventReceiver<EventData> receiver,
                                                                                                 Object responsesLock) {
        return new Requester<>(ID, transmitter, receiver, responsesLock);
    }

    public static <T extends EventData> Responder<GameIsNoLongerAvailableEventData, T> responder(EventTransmitter transmitter,
                                                                                                 EventReceiver<EventData> receiver,
                                                                                                 Function<GameIsNoLongerAvailableEventData, T> response) {
        return new Responder<>(ID, transmitter, receiver, response);
    }

    @Override
    public String getId() {
        return ID;
    }
}
