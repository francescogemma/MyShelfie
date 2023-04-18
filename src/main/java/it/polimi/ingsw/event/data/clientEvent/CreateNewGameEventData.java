package it.polimi.ingsw.event.data.clientEvent;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;

import java.util.function.Function;

public class CreateNewGameEventData implements EventData {
    public static final String ID = "CREATE_NEW_GAME";

    private final String gameName;

    public CreateNewGameEventData(String gameName) {
        this.gameName = gameName;
    }

    public String getGameName() {
        return this.gameName;
    }

    public static <T extends CreateNewGameEventData> CastEventReceiver<T> castEventReceiver(EventReceiver<EventData> receiver) {
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
