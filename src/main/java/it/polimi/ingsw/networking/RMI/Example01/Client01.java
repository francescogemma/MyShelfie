package it.polimi.ingsw.networking.RMI.Example01;

import it.polimi.ingsw.networking.RMI.RMIConnection;

public class Client01 {
    public static void main( String[] args ) {
        try {
            RMIConnection myClient = new RMIConnection(1099);

            myClient.send("Hello! I'm Client 1!");
            System.out.println("Received: \"" + myClient.receive());

        } catch (Exception exception) {
            System.out.println("Connection lost!");
        }
    }
}
