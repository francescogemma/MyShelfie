package it.polimi.ingsw.networking.Example01;

import it.polimi.ingsw.networking.TCP.TCPConnection;

import java.util.concurrent.TimeUnit;

public class ClientSocket {
    public static void main( String[] args ) {
        try {
            // "null" means localhost. change it to the actual server address.
            TCPConnection myClient = new TCPConnection("localhost", 1234);

            // sending two messages without accepting immediately
            myClient.send("Hello! I'm TCPClient 1!");
            myClient.send("Hello again! I'm TCPClient 1!");
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
