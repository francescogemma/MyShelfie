package it.polimi.ingsw.networking.TCP;

import it.polimi.ingsw.networking.ConnectionException;

public class SocketCreationException extends ConnectionException {
    public SocketCreationException() { super(); }
    public SocketCreationException(String s) { super(s); }
    public SocketCreationException(String s, Throwable cause) { super(s, cause); }
}
