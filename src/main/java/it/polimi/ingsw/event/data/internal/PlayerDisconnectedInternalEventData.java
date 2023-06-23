package it.polimi.ingsw.event.data.internal;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;

import java.util.function.Function;

/**
 * This event is broadcasted when the connection falls between the server and a client.
 * @author Giacomo Groppi
 */
public class PlayerDisconnectedInternalEventData implements EventData {
    public static final String ID = "PLAYER_DISCONNECTED_INTERNAL";

    /**
     * Constructs a {@link PlayerDisconnectedInternalEventData}.
     */
    public PlayerDisconnectedInternalEventData() {

    }

    public static CastEventReceiver<PlayerDisconnectedInternalEventData> castEventReceiver(EventReceiver<EventData> receiver) {
        return new CastEventReceiver<>(ID, receiver);
    }

    public static <T extends EventData> Requester<PlayerDisconnectedInternalEventData, T> requester(EventTransmitter transmitter,
                                                                                      EventReceiver<EventData> receiver,
                                                                                      Object responsesLock) {
        return new Requester<>(ID, transmitter, receiver, responsesLock);
    }

    public static <T extends EventData> Responder<PlayerDisconnectedInternalEventData, T> responder(EventTransmitter transmitter,
                                                                                                    EventReceiver<EventData> receiver,
                                                                                                    Function<PlayerDisconnectedInternalEventData, T> response) {
        return new Responder<>(ID, transmitter, receiver, response);
    }

    @Override
    public String getId() {
        return ID;
    }
}
