package it.polimi.ingsw.networking;

/**
 * This exception should be thrown by {@link Connection connection} constructors.
 * It should not be used to identify a disconnection, but a failure in the act of generating a new connection.
 */
public class ConnectionException extends Exception {
    public ConnectionException() { super(); }
    public ConnectionException(String s) { super(s); }
    public ConnectionException(String s, Throwable cause) { super(s, cause); }
}
