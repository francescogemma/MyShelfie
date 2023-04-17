package it.polimi.ingsw.controller;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.data.gameEvent.GameHasStartedEventData;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;

import java.util.function.Function;

public class Response implements EventData {
    private final String message;
    private final ResponseStatus status;
    public static final String ID = "RESPONSE";

    public Response(String message, ResponseStatus status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public static <T extends Response> CastEventReceiver<T> castEventReceiver(EventReceiver<EventData> receiver) {
        return new CastEventReceiver<>(ID, receiver);
    }

    public static <T extends EventData> Requester<Response, T> requester(EventTransmitter transmitter,
                                                                                        EventReceiver<EventData> receiver) {
        return new Requester<>(ID, transmitter, receiver);
    }

    public static <T extends EventData> Responder<Response, T> responder(EventTransmitter transmitter,
                                                                                        EventReceiver<EventData> receiver,
                                                                                        Function<Response, T> response) {
        return new Responder<>(ID, transmitter, receiver, response);
    }

    @Override
    public String getId() {
        return ID;
    }
}
