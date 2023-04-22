package it.polimi.ingsw.networking;

/**
 * This exception is thrown when a {@link Connection} is attempted to be created
 * but the server is not found.
 *
 * @see ConnectionException
 */
public class ServerNotFoundException extends ConnectionException {
    public ServerNotFoundException() { super(); }
    public ServerNotFoundException(String s) { super(s); }
    public ServerNotFoundException(String s, Throwable cause) { super(s, cause); }
}
