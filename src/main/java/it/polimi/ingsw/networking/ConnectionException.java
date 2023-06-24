package it.polimi.ingsw.networking;

/**
 * This exception should be thrown by {@link Connection connection} constructors.
 * It should not be used to identify a disconnection, but a failure in the act of generating a new connection.
 */
public class ConnectionException extends Exception {
    /**
     * Constructs a new exception.
     */
    public ConnectionException() { super(); }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param s the detail message
     */
    public ConnectionException(String s) { super(s); }

    /**
     * Constructs a new exception with the specified detail message and cause.
     *
     * @param s the detail message
     * @param cause the cause
     */
    public ConnectionException(String s, Throwable cause) { super(s, cause); }
}
