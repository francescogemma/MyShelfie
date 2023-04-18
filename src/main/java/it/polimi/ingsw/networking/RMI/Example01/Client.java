package it.polimi.ingsw.networking.RMI.Example01;

import it.polimi.ingsw.networking.RMI.RMIConnection;

public class Client {
    public static void main( String[] args ) {
        RMIConnection clientConnection = new RMIConnection(1098, 1099);
    }
}
