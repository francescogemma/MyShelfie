package it.polimi.ingsw.event.data.client;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;

import java.util.function.Function;

public class PlayerExitGame implements EventData {
    public static final String ID = "PLAYER_EXIT_GAME";

    public PlayerExitGame () {

    }

    public static CastEventReceiver<PlayerExitGame> castEventReceiver(EventReceiver<EventData> receiver) {
        return new CastEventReceiver<>(ID, receiver);
    }

    public static <T extends EventData> Requester<PlayerExitGame, T> requester(EventTransmitter transmitter,
                                                                                  EventReceiver<EventData> receiver,
                                                                                  Object responsesLock) {
        return new Requester<>(ID, transmitter, receiver, responsesLock);
    }

    public static <T extends EventData> Responder<PlayerExitGame, T> responder(EventTransmitter transmitter,
                                                                               EventReceiver<EventData> receiver,
                                                                               Function<PlayerExitGame, T> response) {
        return new Responder<>(ID, transmitter, receiver, response);
    }

    @Override
    public String getId() {
        return ID;
    }
}
