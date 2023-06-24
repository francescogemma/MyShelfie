package it.polimi.ingsw.networking;

/**
 * This exception should be thrown in a client-side {@link Connection} constructor when
 * it could not connect to the ip address specified due to a bad ip address.
 * It has the priority over {@link BadPortException} unless the port is out of range.
 *
 * @see ServerNotFoundException
 */
public class BadHostException extends ServerNotFoundException {
    /**
     * Constructs a new exception.
     */
    public BadHostException() { super(); }

    /**
     * Constructs a new exception with the specified detail message.
     */
    public BadHostException(String s) { super(s); }

    /**
     * Constructs a new exception with the specified detail message and cause.
     */
    public BadHostException(String s, Throwable cause) { super(s, cause); }
}
