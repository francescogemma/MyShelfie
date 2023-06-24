package it.polimi.ingsw.networking;

/**
 * This exception should be thrown when a disconnection has occurred and either the client or the server
 * are trying to "send" or "receive" a message. This exception should not be thrown if the client fails to
 * initiate its connection, but only if an already present connection ceases to exist.
 */
public class DisconnectedException extends Exception {
    /**
     * Constructs a new exception.
     */
    public DisconnectedException() { super(); }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param s the detail message
     */
    public DisconnectedException(String s) { super(s); }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param s the detail message
     * @param cause the cause
     */
    public DisconnectedException(String s, Throwable cause) { super(s, cause); }
}
