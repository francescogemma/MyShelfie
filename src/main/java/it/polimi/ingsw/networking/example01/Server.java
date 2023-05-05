package it.polimi.ingsw.networking.example01;

import it.polimi.ingsw.networking.Connection;
import it.polimi.ingsw.networking.ConnectionAcceptor;

import java.util.concurrent.TimeUnit;

public class Server {
    public Server() { }
    public static void main( String[] args ) {
        System.setProperty("java.rmi.server.hostname","x.x.x.x");

        try {
            // create and export our acceptor.
            System.out.println("Starting server...");
            ConnectionAcceptor serverAcceptor = new ConnectionAcceptor(1234, 5678, "x.x.x.x");
            System.out.println("Server started.");

            // from here on, we can use the serverAcceptor.
            while (true) {
                System.out.println("Now accepting new connection requests...");
                Connection serverSideConnection = serverAcceptor.accept();
                System.out.println("Accepted.");

                System.out.println("Waiting 2 seconds before starting to receive...");
                TimeUnit.SECONDS.sleep(2);

                // wait for two messages using the connection created by the accept method.
                System.out.println("Response 1: \"" + serverSideConnection.receive() + "\"");
                System.out.println("Response 2: \"" + serverSideConnection.receive() + "\"");

                // use the same connection to send back a message.
                serverSideConnection.send("Hello there! I'm the server!");
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
