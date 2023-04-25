package it.polimi.ingsw.networking;

/**
 * This exception should be thrown in a client-side {@link Connection} constructor when:
 * <ul>
 *     <li> there are not any servers listening on the port </li>
 *     <li> the port specified is not in a valid range </li>
 *     <li> the are servers listening on the port, but the ip is incorrect </li>
 *     <li> the ip address specified is not a valid ip address </li>
 * </ul>
 * It should not be thrown directly, but it should be extended by
 * {@link BadHostException} or {@link BadPortException}.
 *
 * @see ConnectionException
 */
public class ServerNotFoundException extends ConnectionException {
    public ServerNotFoundException() { super(); }
    public ServerNotFoundException(String s) { super(s); }
    public ServerNotFoundException(String s, Throwable cause) { super(s, cause); }
}
