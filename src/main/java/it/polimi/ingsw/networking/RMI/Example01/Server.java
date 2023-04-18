package it.polimi.ingsw.networking.RMI.Example01;

import it.polimi.ingsw.networking.ConnectionAcceptor;

public class Server {
    public static void main( String[] args ) {
        ConnectionAcceptor serverAcceptor = new ConnectionAcceptor(8080, 9080);
    }
}
