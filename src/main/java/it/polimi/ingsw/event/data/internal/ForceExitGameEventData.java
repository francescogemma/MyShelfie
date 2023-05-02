package it.polimi.ingsw.event.data.internal;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;

import java.util.function.Function;

public class ForceExitGameEventData implements EventData {
    public static final String ID = "FORCE_EXIT_GAME";

    public ForceExitGameEventData() {
    }

    public static CastEventReceiver<ForceExitGameEventData> castEventReceiver(EventReceiver<EventData> receiver) {
        return new CastEventReceiver<>(ID, receiver);
    }

    public static <T extends EventData> Requester<ForceExitGameEventData, T> requester(EventTransmitter transmitter,
                                                                                                    EventReceiver<EventData> receiver,
                                                                                                    Object responsesLock) {
        return new Requester<>(ID, transmitter, receiver, responsesLock);
    }

    public static <T extends EventData> Responder<ForceExitGameEventData, T> responder(EventTransmitter transmitter,
                                                                                                    EventReceiver<EventData> receiver,
                                                                                                    Function<ForceExitGameEventData, T> response) {
        return new Responder<>(ID, transmitter, receiver, response);
    }

    @Override
    public String getId() {
        return ID;
    }
}
