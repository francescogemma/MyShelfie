package it.polimi.ingsw.networking.example01;

import it.polimi.ingsw.networking.RMI.RMIConnection;

import java.util.concurrent.TimeUnit;

public class DisconnectingClientRMI {
    public static void main( String[] args ) {
        try {
            // "null" means localhost. change it to the actual server address.
            RMIConnection myClient = new RMIConnection(null, 1099);

            // sending two messages without accepting immediately.
            myClient.send("Hello! I'm Disconnecting RMI Client!");
            myClient.send("Hello again! I'm Disconnecting RMI Client!");

            System.out.println("Waiting 5 seconds before disconnecting...");
            TimeUnit.SECONDS.sleep(5);
            myClient.disconnect();

            System.out.println("Intentionally disconnected.");

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
