package it.polimi.ingsw.event.data.game;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;
import it.polimi.ingsw.utils.Logger;

import java.util.function.Function;

/**
 * Event sent to the client when someone has disconnected from the game.
 * @param username The username of the player that has disconnected.
 * @param newOwner The username of the new owner of the game.
 * @author Giacomo Groppi
 */
public record PlayerHasDisconnectedEventData(String username, String newOwner) implements EventData {
    public static final String ID = "PLAYER_HAS_DISCONNECTED";

    public PlayerHasDisconnectedEventData {
        assert username != null;
        assert newOwner != null;

        assert !newOwner.equals(username);
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
    public boolean equals (Object other) {
        if (other == this)
            return true;
        if (other == null || other.getClass() != this.getClass())
            return false;
        return this.username.equals( ((PlayerHasDisconnectedEventData) other).username );
    }

    @Override
    public String getId() {
        return ID;
    }
}
