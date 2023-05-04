package it.polimi.ingsw.event.data.game;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;
import it.polimi.ingsw.utils.Logger;

import java.util.function.Function;

public record PlayerHasJoinGameEventData(String username) implements EventData {
    public static final String ID = "PLAYER_HAS_JOIN_GAME";

    public PlayerHasJoinGameEventData {
        Logger.writeMessage("Event: %s".formatted(username));
    }

    public static CastEventReceiver<PlayerHasJoinGameEventData> castEventReceiver(EventReceiver<EventData> receiver) {
        return new CastEventReceiver<>(ID, receiver);
    }

    public static <T extends EventData> Requester<PlayerHasJoinGameEventData, T> requester(EventTransmitter transmitter,
                                                                                           EventReceiver<EventData> receiver,
                                                                                           Object responsesLock) {
        return new Requester<>(ID, transmitter, receiver, responsesLock);
    }

    public static <T extends EventData> Responder<PlayerHasJoinGameEventData, T> responder(EventTransmitter transmitter,
                                                                                           EventReceiver<EventData> receiver,
                                                                                           Function<PlayerHasJoinGameEventData, T> response) {
        return new Responder<>(ID, transmitter, receiver, response);
    }

    @Override
    public String getId() {
        return ID;
    }
}