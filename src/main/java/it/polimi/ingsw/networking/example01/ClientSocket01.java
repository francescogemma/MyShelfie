package it.polimi.ingsw.networking.example01;

import it.polimi.ingsw.networking.TCP.TCPConnection;

public class ClientSocket01 {
    public static void main( String[] args ) {
        try {
            // "null" means localhost. change it to the actual server address.
            TCPConnection myClient = new TCPConnection("localhost", 1234);

            // sending two messages to target.
            myClient.send("Hello! I'm Socket Client 1!");
            myClient.send("Hello again! I'm Socket Client 1!");

            while (true) {
                System.out.println("Listening...");
                System.out.println("Received: \"" + myClient.receive());
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
