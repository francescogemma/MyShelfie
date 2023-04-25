package it.polimi.ingsw.event.data.game;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;

import java.util.function.Function;

public class PlayerHasDisconnectedEventData implements EventData {
    private final String username;
    public static final String ID = "PLAYER_HAS_DISCONNECTED";

    public PlayerHasDisconnectedEventData(String username) {
        this.username = username;
    }

    public String getUsername () {
        return this.username;
    }

    public static CastEventReceiver<PlayerHasDisconnectedEventData> castEventReceiver(EventReceiver<EventData> receiver) {
        return new CastEventReceiver<>(ID, receiver);
    }

    public static <T extends EventData> Requester<PlayerHasDisconnectedEventData, T> requester(EventTransmitter transmitter,
                                                                                  EventReceiver<EventData> receiver,
                                                                                               Object responsesLock) {
        return new Requester<>(ID, transmitter, receiver, responsesLock);
    }

    public static <T extends EventData> Responder<PlayerHasDisconnectedEventData, T> responder(EventTransmitter transmitter,
                                                                                  EventReceiver<EventData> receiver,
                                                                                  Function<PlayerHasDisconnectedEventData, T> response) {
        return new Responder<>(ID, transmitter, receiver, response);
    }

    @Override
    public String getId() {
        return ID;
    }
}
