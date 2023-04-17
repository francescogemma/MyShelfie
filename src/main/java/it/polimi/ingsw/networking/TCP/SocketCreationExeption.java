package it.polimi.ingsw.networking.TCP;

public class SocketCreationExeption extends Exception {
    public SocketCreationExeption() { super(); }
    public SocketCreationExeption(String s) { super(s); }
    public SocketCreationExeption(String s, Throwable cause) { super(s, cause); }
}
