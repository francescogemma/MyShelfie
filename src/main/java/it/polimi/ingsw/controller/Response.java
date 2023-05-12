package it.polimi.ingsw.controller;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;
import it.polimi.ingsw.utils.Logger;

import java.util.function.Function;

public class Response implements EventData {
    public static final String ID = "RESPONSE";
    private final String message;
    private final ResponseStatus status;

    public static Response failure(String message) {
        return new Response(message, ResponseStatus.FAILURE);
    }

    protected static final Response notAuthenticated = new Response("Not autenticated", ResponseStatus.FAILURE);
    protected static final Response notInLobby = new Response("Not in lobby", ResponseStatus.FAILURE);
    protected static final Response notInGame  = new Response("Not in game", ResponseStatus.FAILURE);
    protected static final Response alreadyInGame = new Response("Already in game", ResponseStatus.FAILURE);
    protected static final Response alreadyInLobby = new Response("Already in lobby", ResponseStatus.FAILURE);
    protected static final Response alreadyLogIn = new Response("Already log in", ResponseStatus.FAILURE);

    public static Response success(String message) {
        return new Response(message, ResponseStatus.SUCCESS);
    }

    private Response(String message, ResponseStatus status) {
        Logger.writeMessage("status: [%s] message: [%s] from: [%s]".formatted(status, message, Thread.currentThread().getStackTrace()[3]));
        this.status = status;
        this.message = message;
    }

    public String message() {
        return this.message;
    }

    public ResponseStatus status () {
        return status;
    }

    public boolean isOk() {
        return status == ResponseStatus.SUCCESS;
    }

    public static CastEventReceiver<Response> castEventReceiver(EventReceiver<EventData> receiver) {
        return new CastEventReceiver<>(ID, receiver);
    }

    public static <T extends EventData> Requester<Response, T> requester(EventTransmitter transmitter,
                                                                         EventReceiver<EventData> receiver,
                                                                         Object responsesLock) {
        return new Requester<>(ID, transmitter, receiver, responsesLock);
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
