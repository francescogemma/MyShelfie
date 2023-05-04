package it.polimi.ingsw.event.data.game;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;

import java.util.function.Function;

public class PlayerHasJoinLobbyEventData implements EventData {
    public static final String ID = "PLAYER_HAS_JOIN_LOBBY";

    private final String username;

    public String getUsername() {
        return username;
    }

    public PlayerHasJoinLobbyEventData(String username) {
        this.username = username;
    }

    public static CastEventReceiver<PlayerHasJoinLobbyEventData> castEventReceiver(EventReceiver<EventData> receiver) {
        return new CastEventReceiver<>(ID, receiver);
    }

    public static <T extends EventData> Requester<PlayerHasJoinLobbyEventData, T> requester(EventTransmitter transmitter,
                                                                                        EventReceiver<EventData> receiver,
                                                                                        Object responsesLock) {
        return new Requester<>(ID, transmitter, receiver, responsesLock);
    }

    public static <T extends EventData> Responder<PlayerHasJoinLobbyEventData, T> responder(EventTransmitter transmitter,
                                                                                        EventReceiver<EventData> receiver,
                                                                                        Function<PlayerHasJoinLobbyEventData, T> response) {
        return new Responder<>(ID, transmitter, receiver, response);
    }

    @Override
    public String getId() {
        return ID;
    }
}
