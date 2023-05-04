package it.polimi.ingsw.event.data.client;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;

import java.util.function.Function;

public class RestartGameEventData implements EventData {
    public static final String ID = "RESTART_GAME";

    public static CastEventReceiver<RestartGameEventData> castEventReceiver(EventReceiver<EventData> receiver) {
        return new CastEventReceiver<>(ID, receiver);
    }

    public static <T extends EventData> Requester<RestartGameEventData, T> requester(EventTransmitter transmitter,
                                                                                   EventReceiver<EventData> receiver,
                                                                                   Object responsesLock) {
        return new Requester<>(ID, transmitter, receiver, responsesLock);
    }

    public static <T extends EventData> Responder<RestartGameEventData, T> responder(EventTransmitter transmitter,
                                                                                   EventReceiver<EventData> receiver,
                                                                                   Function<RestartGameEventData, T> response) {
        return new Responder<>(ID, transmitter, receiver, response);
    }

    @Override
    public String getId() {
        return ID;
    }
}
