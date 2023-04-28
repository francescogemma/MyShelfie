package it.polimi.ingsw.event.data.internal;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.data.game.BoardChangedEventData;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;

import java.util.function.Function;

public class PlayerDisconnectedInternalEventData implements EventData {
    public static final String ID = "PLAYER_DISCONNECTED_INTERNAL";

    public PlayerDisconnectedInternalEventData() {
    }

    public static CastEventReceiver<BoardChangedEventData> castEventReceiver(EventReceiver<EventData> receiver) {
        return new CastEventReceiver<>(ID, receiver);
    }

    public static <T extends EventData> Requester<BoardChangedEventData, T> requester(EventTransmitter transmitter,
                                                                                      EventReceiver<EventData> receiver,
                                                                                      Object responsesLock) {
        return new Requester<>(ID, transmitter, receiver, responsesLock);
    }

    public static <T extends EventData> Responder<BoardChangedEventData, T> responder(EventTransmitter transmitter,
                                                                                      EventReceiver<EventData> receiver,
                                                                                      Function<BoardChangedEventData, T> response) {
        return new Responder<>(ID, transmitter, receiver, response);
    }

    @Override
    public String getId() {
        return ID;
    }
}
