package it.polimi.ingsw.event.data.gameEvent;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;

import java.util.function.Function;

public class PlayerHasJoinEventData implements EventData {
    private final String username;
    public static final String ID = "PLAYER_HAS_JOIN";

    public PlayerHasJoinEventData(String username) {
        this.username = username;
    }

    public static <T extends PlayerHasJoinEventData> CastEventReceiver<T> castEventReceiver(EventReceiver<EventData> receiver) {
        return new CastEventReceiver<>(ID, receiver);
    }

    public static <T extends EventData> Requester<PlayerHasJoinEventData, T> requester(EventTransmitter transmitter,
                                                                                  EventReceiver<EventData> receiver) {
        return new Requester<>(ID, transmitter, receiver);
    }

    public static <T extends EventData> Responder<PlayerHasJoinEventData, T> responder(EventTransmitter transmitter,
                                                                                  EventReceiver<EventData> receiver,
                                                                                  Function<PlayerHasJoinEventData, T> response) {
        return new Responder<>(ID, transmitter, receiver, response);
    }

    public String getUsername() {
        return this.username;
    }

    @Override
    public String getId() {
        return ID;
    }
}
