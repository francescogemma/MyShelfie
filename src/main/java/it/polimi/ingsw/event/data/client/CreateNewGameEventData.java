package it.polimi.ingsw.event.data.client;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;

import java.util.function.Function;

public record CreateNewGameEventData(String gameName) implements EventData {
    public static final String ID = "CREATE_NEW_GAME";

    public static CastEventReceiver<CreateNewGameEventData> castEventReceiver(EventReceiver<EventData> receiver) {
        return new CastEventReceiver<>(ID, receiver);
    }

    public static <T extends EventData> Requester<CreateNewGameEventData, T> requester(EventTransmitter transmitter,
                                                                                       EventReceiver<EventData> receiver) {
        return new Requester<>(ID, transmitter, receiver);
    }

    public static <T extends EventData> Responder<CreateNewGameEventData, T> responder(EventTransmitter transmitter,
                                                                                       EventReceiver<EventData> receiver,
                                                                                       Function<CreateNewGameEventData, T> response) {
        return new Responder<>(ID, transmitter, receiver, response);
    }

    @Override
    public String getId() {
        return ID;
    }
}
