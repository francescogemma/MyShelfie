package it.polimi.ingsw.networking.example01;

import it.polimi.ingsw.networking.RMI.RMIConnection;

public class ClientRMI02 {
    public static void main( String[] args ) {
        try {
            // "null" means localhost. change it to the actual server address.
            RMIConnection myClient = new RMIConnection("x.x.x.x", 5678);

            // sending two messages to target.
            myClient.send("Hello! I'm RMI Client 2!");
            myClient.send("Hello again! I'm RMI Client 2!");

            while (true) {
                System.out.println("Listening...");
                System.out.println("Received: \"" + myClient.receive());
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
