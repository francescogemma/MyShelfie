package it.polimi.ingsw.networking;

/**
 * This exception should be thrown in a client-side {@link Connection} constructor when
 * it could not connect to the server due to a bad port.
 * It has the priority over {@link BadHostException} only if the port is out of range;
 * if not, {@link BadHostException} has the priority.
 *
 * @see ServerNotFoundException
 */
public class BadPortException extends ServerNotFoundException {
    /**
     * Constructs a new exception.
     */
    public BadPortException() { super(); }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param s the detail message
     */
    public BadPortException(String s) { super(s); }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param s the detail message
     * @param cause the cause
     */
    public BadPortException(String s, Throwable cause) { super(s, cause); }
}
