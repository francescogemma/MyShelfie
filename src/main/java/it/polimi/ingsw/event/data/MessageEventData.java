package it.polimi.ingsw.event.data;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;

import java.util.function.Function;

// TODO: Remove this useless event (use ResponseEventData instead)
public class MessageEventData implements EventData {
    private final String message;

    public MessageEventData(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public static final String ID = "MESSAGE";

    @Override
    public String getId() {
        return ID;
    }

    public static CastEventReceiver<MessageEventData> castEventReceiver(EventReceiver<EventData> receiver) {
        return new CastEventReceiver<>(ID, receiver);
    }

    public static <T extends EventData> Requester<MessageEventData, T> requester(EventTransmitter transmitter,
                                                                                 EventReceiver<EventData> receiver) {
        return new Requester<>(ID, transmitter, receiver);
    }

    public static <T extends EventData> Responder<MessageEventData, T> responder(EventTransmitter transmitter,
                                                                                 EventReceiver<EventData> receiver,
                                                                                 Function<MessageEventData, T> response) {
        return new Responder<>(ID, transmitter, receiver, response);
    }
}
