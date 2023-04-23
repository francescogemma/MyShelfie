package it.polimi.ingsw.networking.example01;

import it.polimi.ingsw.networking.TCP.TCPConnection;

import java.util.concurrent.TimeUnit;

public class DisconnectingClientSocket {
    public static void main( String[] args ) {
        try {
            // "null" means localhost. change it to the actual server address.
            TCPConnection myClient = new TCPConnection("localhost", 1234);

            // sending two messages without accepting immediately.
            myClient.send("Hello! I'm Disconnecting Socket Client!");
            myClient.send("Hello again! I'm Disconnecting Socket Client!");

            System.out.println("Waiting 5 seconds before disconnecting...");
            TimeUnit.SECONDS.sleep(5);
            myClient.disconnect();

            System.out.println("Intentionally disconnected.");

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
