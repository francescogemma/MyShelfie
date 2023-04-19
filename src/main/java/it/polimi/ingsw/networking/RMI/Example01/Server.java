package it.polimi.ingsw.networking.RMI.Example01;

import it.polimi.ingsw.networking.Connection;
import it.polimi.ingsw.networking.ConnectionAcceptor;

import java.util.concurrent.TimeUnit;

/**
 * This example should clarify the steps required to initiate a connectionAcceptor, and how its
 * methods work in relation to the creation of new connections, and how to handle them in general.
 */
public class Server {
    public Server() { }
    public static void main( String[] args ) {
        try {
            // create and export our acceptor
            ConnectionAcceptor serverAcceptor = new ConnectionAcceptor();
            System.out.println("Server started.");

            // from here on, we can use the serverAcceptor.
            while (true) {
                Connection serverSideConnection = serverAcceptor.accept();
                System.out.println("Accepted.");

                System.out.println("Waiting 10 seconds before starting to receive.");
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
