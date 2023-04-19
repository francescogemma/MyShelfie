package it.polimi.ingsw.networking.RMI.Example01;

import it.polimi.ingsw.networking.RMI.RMIConnection;

public class Client02 {
    public static void main( String[] args ) {
        try {
            RMIConnection myClient = new RMIConnection(1099);

            // sending two messages without accepting immediately
            myClient.send("Hello! I'm Client 2!");
            myClient.send("Hello again! I'm Client 2!");
            System.out.println("Received: \"" + myClient.receive());

        } catch (Exception exception) {
            System.out.println("Connection lost!");
        }
    }
}
