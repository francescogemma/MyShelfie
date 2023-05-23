package it.polimi.ingsw.event.data.client;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;

import java.util.function.Function;

/**
 * This event notifies the server that the interface is ready to receive events, as it has entered the menu
 *
 * @author Giacomo Groppi
 * */
public class PlayerHasJoinMenuEventData implements EventData {
    public static final String ID = "PLAYER_HAS_JOIN_MENU";

    public static CastEventReceiver<PlayerHasJoinMenuEventData> castEventReceiver(EventReceiver<EventData> receiver) {
        return new CastEventReceiver<>(ID, receiver);
    }

    public static <T extends EventData> Requester<PlayerHasJoinMenuEventData, T> requester(EventTransmitter transmitter,
                                                                                           EventReceiver<EventData> receiver,
                                                                                           Object responsesLock) {
        return new Requester<>(ID, transmitter, receiver, responsesLock);
    }

    public static <T extends EventData> Responder<PlayerHasJoinMenuEventData, T> responder(EventTransmitter transmitter,
                                                                                           EventReceiver<EventData> receiver,
                                                                                           Function<PlayerHasJoinMenuEventData, T> response) {
        return new Responder<>(ID, transmitter, receiver, response);
    }

    @Override
    public String getId() {
        return ID;
    }
}
