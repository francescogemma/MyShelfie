package it.polimi.ingsw.controller;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.data.VoidEventData;
import it.polimi.ingsw.event.data.wrapper.EventDataWrapper;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;
import it.polimi.ingsw.utils.Logger;

import java.util.function.Function;

/**
 * An event sent in many responses.
 * It contains a textual message and a {@link ResponseStatus status}.
 * The object is thread-safe and immutable.
 *
 * @see EventData
 * @see ResponseStatus
 * @author Cristiano Migali
 * @author Giacomo Groppi
 * */
public class Response <T extends EventData> extends EventDataWrapper<T> {
    /**
     * Event id
     * */
    public static final String WRAPPER_ID = "RESPONSE";

    /**
     * A message set by the constructor
     * */
    private final String message;

    /**
     * A status set by the constructor
     * */
    private final ResponseStatus status;

    /**
     * Default message for not authenticated user
     * */
    protected static final String notAuthenticated = "Not autenticated";

    /**
     * Default message for client not in lobby
     */
    protected static final String notInLobby = "Not in lobby";

    /**
     * Default message for client not in game
     * */
    protected static final String notInGame  = "Not in game";

    /**
     * Default message for client not in game
     * */
    protected static final String alreadyInGame = "Already in game";

    /**
     * Default message for client already in game
     * */
    protected static final String alreadyInLobby = "Already in lobby";

    /**
     * Default failure message for client already log in
     * */
    protected static final String alreadyLogIn = "Already log in";

    /**
     * Creates a new Response object with a success status.
     *
     * @param message The message to be set.
     * @return A new response object with the message "message" and a status of {@link ResponseStatus#SUCCESS}.
     * @author Giacomo Groppi
     * */
    public static Response<VoidEventData> success(String message) {
        return new Response<>(message, ResponseStatus.SUCCESS, new VoidEventData());
    }

    /**
     * Creates a new Response object with a failure status.
     *
     * @param message The message to be set.
     * @return A new response object with the message "message" and a status of {@link ResponseStatus#FAILURE}.
     * @author Giacomo Groppi
     */
    public static Response<VoidEventData> failure(String message) {
        return new Response<>(message, ResponseStatus.FAILURE, new VoidEventData());
    }

    /**
     * Creates a new Response object with the specified message and status.
     *
     * @param message The message to be set as the response.
     * @param status The status that the message should have.
     * @author Cristiano Migali
     * */
    public Response(String message, ResponseStatus status, T data) {
        super(data);
        Logger.writeMessage("status: [%s] message: [%s] from: [%s]".formatted(status, message, Thread.currentThread().getStackTrace()[3]));
        this.status = status;
        this.message = message;
    }

    /**
     * Returns the message of the response.
     *
     * @return The message contained in the response.
     * @author Cristiano Migali
     * */
    public String message() {
        return this.message;
    }

    /**
     * This method returns the status of the request.
     *
     * @return The status contained in the response.
     * @see ResponseStatus
     * @author Cristiano Migali
     * */
    public ResponseStatus status () {
        return status;
    }

    /**
     * Use this method to check if the status is {@link ResponseStatus#SUCCESS}.
     *
     * @return true if the internal status is {@link ResponseStatus#SUCCESS}, false otherwise.
     * @author Giacomo Groppi
     * */
    public boolean isOk() {
        return status == ResponseStatus.SUCCESS;
    }

    public static <T extends EventData> Requester<Response<VoidEventData>, T> requester(EventTransmitter transmitter,
                                                                         EventReceiver<EventData> receiver,
                                                                         Object responsesLock) {
        return new Requester<>(WRAPPER_ID + "_" + VoidEventData.ID,
                transmitter,
                receiver,
                responsesLock);
    }

    public static <T extends EventData, Z extends EventData> Requester<Response<Z>, T>
            requester(EventTransmitter transmitter,
                      EventReceiver<EventData> receiver,
                      String wrappedEventId,
                      Object responsesLock) {
        return new Requester<>(WRAPPER_ID + "_" + wrappedEventId,
                transmitter,
                receiver,
                responsesLock);
    }

    @Override
    public String getWrapperId() {
        return WRAPPER_ID;
    }
}
