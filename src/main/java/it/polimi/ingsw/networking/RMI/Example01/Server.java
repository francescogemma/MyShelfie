package it.polimi.ingsw.networking.RMI.Example01;

import it.polimi.ingsw.networking.Connection;
import it.polimi.ingsw.networking.ConnectionAcceptor;
import it.polimi.ingsw.networking.RMI.NameProvidingRemote;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server {
    public Server() { }
    public static void main( String[] args ) {
        try {
            // create and export our stringContainer
            ConnectionAcceptor serverAcceptor = new ConnectionAcceptor();
            NameProvidingRemote stub = (NameProvidingRemote) UnicastRemoteObject.exportObject(
                    serverAcceptor, 1099
            );

            Registry registry = LocateRegistry.createRegistry(1099);
            registry.bind("SERVER", stub);

            // let the user know that everything worked.
            System.out.println("Server started.");

            // from here on, we can use the serverAcceptor.
            Connection myServer = serverAcceptor.accept();
            System.out.println("Accepted.");

            myServer.send("Hello client! I'm the server!");

            System.out.println("Response: \"" + myServer.receive() + "\"");

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
