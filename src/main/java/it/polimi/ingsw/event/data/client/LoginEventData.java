package it.polimi.ingsw.event.data.client;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;

import java.util.function.Function;

/**
 * Represents the data needed to perform a login or registration for the client, that is
 * account's username and account's password.
 * In particular if the specified username is not already registered on the server, registration
 * is performed instead of a login.
 *
 * @author Cristiano Migali
 */
public class LoginEventData implements EventData {
    /**
     * The account's username.
     */
    private final String username;

    /**
     * The account's password.
     */
    private final String password;

    /**
     * Constructor of the class.
     * Initializes username and password.
     *
     * @param username is the account's username.
     * @param password is the account's password.
     */
    public LoginEventData(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * @return account's username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return account's password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Unique identifier for the EventData, added for serialization purposes accordingly to {@link EventData}
     * interface contract.
     */
    public static final String ID = "LOGIN";

    @Override
    public String getId() {
        return ID;
    }

    /**
     * {@link CastEventReceiver} factory method for the EventData, added accordingly to {@link EventData}
     * interface contract.
     *
     * @param receiver is the {@link EventReceiver} which will receive instances of this EventData.
     * @return a {@link CastEventReceiver} which filters the events received by receiver and provides those
     * of this EventData type to an {@link it.polimi.ingsw.event.receiver.EventListener} after a cast.
     */
    public static CastEventReceiver<LoginEventData> castEventReceiver(EventReceiver<EventData> receiver) {
        return new CastEventReceiver<>(ID, receiver);
    }

    /**
     * {@link Requester} factory method that performs requests which receive responses of this EventData, added
     * accordingly to {@link EventData} interface contract.
     *
     * @param transmitter is the {@link EventTransmitter} on which the request is sent.
     * @param receiver is the {@link EventReceiver} which receives the response to the request.
     * @param responsesLock is the lock Object on which the {@link Requester} synchronizes to wait for the
     *                      response.
     * @return a {@link Requester} which is capable of performing requests which receive responses of this
     * EventData.
     * @param <T> is the type of the request EventData.
     */
    public static <T extends EventData> Requester<LoginEventData, T> requester(EventTransmitter transmitter,
                                                             EventReceiver<EventData> receiver,
                                                                               Object responsesLock) {
        return new Requester<>(ID, transmitter, receiver, responsesLock);
    }

    /**
     * {@link Responder} factory method which allows to respond to requests of this EventData, added accordingly
     * to {@link EventData} interface contract.
     *
     * @param transmitter is the {@link EventTransmitter} on which the response is sent.
     * @param receiver is the {@link EventReceiver} which receives the request.
     * @param response is the function which allows to compute the appropriate response to the received request.
     * @return a {@link Responder} which is capable of answering requests of this EventData.
     * @param <T> is the type of the response EventData.
     */
    public static <T extends EventData> Responder<LoginEventData, T> responder(EventTransmitter transmitter,
                                                                               EventReceiver<EventData> receiver,
                                                                               Function<LoginEventData, T> response) {
        return new Responder<>(ID, transmitter, receiver, response);
    }
}
