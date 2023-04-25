package it.polimi.ingsw.networking;

/**
 * This exception should be thrown in a client-side {@link Connection} constructor when
 * it could not connect to the ip address specified due to a bad ip address.
 * It has the priority over {@link BadPortException} unless the port is out of range.
 *
 * @see ServerNotFoundException
 */
public class BadHostException extends ServerNotFoundException {
    public BadHostException() { super(); }
    public BadHostException(String s) { super(s); }
    public BadHostException(String s, Throwable cause) { super(s, cause); }
}
