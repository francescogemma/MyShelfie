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
     * Unique identifier for {@link LoginEventData}.
     */
    public static final String ID = "LOGIN";

    @Override
    public String getId() {
        return ID;
    }

    /**
     * @param receiver is the receiver that will receive the login data that we want to listen for.
     *
     * @return a receiver which listens only for login events and provides the data already caster.
     *
     * @see CastEventReceiver
     */
    public static CastEventReceiver<LoginEventData> castEventReceiver(EventReceiver<EventData> receiver) {
        return new CastEventReceiver<>(ID, receiver);
    }

    /**
     * @param transmitter is the transmitter that will send the synchronous request encapsulating the login data.
     * @param receiver is the receiver that will receive the synchronous response to the login request.
     * @param responsesLock is the lock on which the {@link Requester} will invoke {@link Object#wait()} while
     *                      waiting for the response. Furthermore the lock will be used for synchronization with
     *                      the thread listening for incoming responses.
     * @return a requester capable of performing login requests.
     * @param <T> the type of the response we except to receive.
     */
    public static <T extends EventData> Requester<LoginEventData, T> requester(EventTransmitter transmitter,
                                                             EventReceiver<EventData> receiver,
                                                                               Object responsesLock) {
        return new Requester<>(ID, transmitter, receiver, responsesLock);
    }

    /**
     * @param transmitter is the transmitter that will send the synchronous response to the incoming login request.
     * @param receiver is the receiver that will receive the login request we will answer for.
     * @param response is the function which will craft the appropriate response to the received login requests.
     * @return a responder capable of answering incoming login requests.
     * @param <T> the type of the response we will send as an answer to the incoming login request.
     */
    public static <T extends EventData> Responder<LoginEventData, T> responder(EventTransmitter transmitter,
                                                                               EventReceiver<EventData> receiver,
                                                                               Function<LoginEventData, T> response) {
        return new Responder<>(ID, transmitter, receiver, response);
    }
}
