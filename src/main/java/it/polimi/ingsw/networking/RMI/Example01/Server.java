package it.polimi.ingsw.networking.RMI.Example01;

import it.polimi.ingsw.networking.Connection;
import it.polimi.ingsw.networking.ConnectionAcceptor;

import java.util.concurrent.TimeUnit;

public class Server {
    public Server() { }
    public static void main( String[] args ) {
        try {
            // create and export our acceptor
            System.out.println("Starting server...");
            ConnectionAcceptor serverAcceptor = new ConnectionAcceptor(9080, 1099);
            System.out.println("Server started.");

            // from here on, we can use the serverAcceptor.
            while (true) {
                System.out.println("Now accepting new connection requests...");
                Connection serverSideConnection = serverAcceptor.accept();
                System.out.println("Accepted.");

                System.out.println("Waiting 10 seconds before starting to receive...");
                TimeUnit.SECONDS.sleep(10);
                
                System.out.println("Response 1: \"" + serverSideConnection.receive() + "\"");
                System.out.println("Response 2: \"" + serverSideConnection.receive() + "\"");

                serverSideConnection.send("Hello there! I'm the server!");
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
