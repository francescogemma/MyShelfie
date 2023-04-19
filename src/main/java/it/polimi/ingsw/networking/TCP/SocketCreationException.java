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
    public SocketCreationException() { super(); }
    public SocketCreationException(String s) { super(s); }
    public SocketCreationException(String s, Throwable cause) { super(s, cause); }
}
