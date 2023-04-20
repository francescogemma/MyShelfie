package it.polimi.ingsw.networking.Example01;

import it.polimi.ingsw.networking.RMI.RMIConnection;

import java.util.concurrent.TimeUnit;

public class Client01 {
    public static void main( String[] args ) {
        try {
            // "null" means localhost. change it to the actual server address.
            RMIConnection myClient = new RMIConnection(null, 1099);

            // sending two messages without accepting immediately
            myClient.send("Hello! I'm Client 1!");
            myClient.send("Hello again! I'm Client 1!");
            System.out.println("Received: \"" + myClient.receive());


            System.out.println("Waiting 3 seconds...");
            TimeUnit.SECONDS.sleep(3);
            myClient.disconnect();
            System.out.println("Intentionally disconnected.");

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
