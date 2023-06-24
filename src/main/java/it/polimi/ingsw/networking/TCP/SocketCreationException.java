package it.polimi.ingsw.networking.TCP;

import it.polimi.ingsw.networking.ConnectionException;

/**
 * Exception thrown when a {@link java.net.Socket socket} failed to be created.
 * It extends {@link ConnectionException},
 * because it is thrown when a failure occurs in the process of
 * creating a new  {@link it.polimi.ingsw.networking.Connection Connection}.
 *
 * @see ConnectionException
 * @see java.net.Socket Socket
 */
public class SocketCreationException extends ConnectionException {
    /**
     * Creates a new exception.
     */
    public SocketCreationException() { super(); }

    /**
     * Creates a new exception with the specified detail message.
     *
     * @param s the detail message
     */
    public SocketCreationException(String s) { super(s); }

    /**
     * Creates a new exception with the specified detail message and cause.
     *
     * @param s the detail message
     * @param cause the cause
     */
    public SocketCreationException(String s, Throwable cause) { super(s, cause); }
}
