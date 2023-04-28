package it.polimi.ingsw.event.data.client;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;

import java.util.function.Function;

public class PauseGameEventData implements EventData {
    public static final String ID = "PAUSE_GAME";

    public PauseGameEventData() {

    }

    public static CastEventReceiver<PauseGameEventData> castEventReceiver(EventReceiver<EventData> receiver) {
        return new CastEventReceiver<>(ID, receiver);
    }

    public static <T extends EventData> Requester<PauseGameEventData, T> requester(EventTransmitter transmitter,
                                                                                   EventReceiver<EventData> receiver,
                                                                                   Object responsesLock) {
        return new Requester<>(ID, transmitter, receiver, responsesLock);
    }

    public static <T extends EventData> Responder<PauseGameEventData, T> responder(EventTransmitter transmitter,
                                                                                   EventReceiver<EventData> receiver,
                                                                                   Function<PauseGameEventData, T> response) {
        return new Responder<>(ID, transmitter, receiver, response);
    }

    @Override
    public String getId() {
        return ID;
    }
}
