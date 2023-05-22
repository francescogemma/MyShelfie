package it.polimi.ingsw.event.data.client;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;

import java.util.function.Function;

public class UsernameEventData implements EventData {
    public static final String ID = "USERNAME";
    private final String username;

    public UsernameEventData(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

    public static CastEventReceiver<UsernameEventData> castEventReceiver(EventReceiver<EventData> receiver) {
        return new CastEventReceiver<>(ID, receiver);
    }

    public static <T extends EventData> Requester<UsernameEventData, T> requester(EventTransmitter transmitter,
                                                                                EventReceiver<EventData> receiver,
                                                                                Object responsesLock) {
        return new Requester<>(ID, transmitter, receiver, responsesLock);
    }

    public static <T extends EventData> Responder<UsernameEventData, T> responder(EventTransmitter transmitter,
                                                                                EventReceiver<EventData> receiver,
                                                                                Function<UsernameEventData, T> response) {
        return new Responder<>(ID, transmitter, receiver, response);
    }

    @Override
    public String getId() {
        return ID;
    }
}
