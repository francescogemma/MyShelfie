package it.polimi.ingsw.networking;

public class DisconnectedException extends Exception {
    public DisconnectedException() { super(); }
    public DisconnectedException(String s) { super(s); }
    public DisconnectedException(String s, Throwable cause) { super(s, cause); }
}
