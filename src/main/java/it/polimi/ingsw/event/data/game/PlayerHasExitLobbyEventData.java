package it.polimi.ingsw.event.data.game;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;

import java.util.function.Function;

/**
 * Event sent when a player has exit the lobby.
 * @param username The username of the player that has exit the lobby.
 * @author Giacomo Groppi
 * */
public record PlayerHasExitLobbyEventData(String username) implements EventData {
    public static final String ID = "PLAYER_HAS_EXIT_LOBBY";

    public static CastEventReceiver<PlayerHasExitLobbyEventData> castEventReceiver(EventReceiver<EventData> receiver) {
        return new CastEventReceiver<>(ID, receiver);
    }

    public static <T extends EventData> Requester<PlayerHasExitLobbyEventData, T> requester(EventTransmitter transmitter,
                                                                                            EventReceiver<EventData> receiver,
                                                                                            Object responsesLock) {
        return new Requester<>(ID, transmitter, receiver, responsesLock);
    }

    public static <T extends EventData> Responder<PlayerHasExitLobbyEventData, T> responder(EventTransmitter transmitter,
                                                                                            EventReceiver<EventData> receiver,
                                                                                            Function<PlayerHasExitLobbyEventData, T> response) {
        return new Responder<>(ID, transmitter, receiver, response);
    }

    @Override
    public String getId() {
        return ID;
    }
}
