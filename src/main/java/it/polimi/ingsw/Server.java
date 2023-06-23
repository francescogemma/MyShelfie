package it.polimi.ingsw;

import it.polimi.ingsw.controller.servercontroller.MenuController;
import it.polimi.ingsw.controller.VirtualView;
import it.polimi.ingsw.event.NetworkEventTransceiver;
import it.polimi.ingsw.networking.Connection;
import it.polimi.ingsw.networking.ConnectionAcceptor;
import it.polimi.ingsw.networking.ConnectionException;
import it.polimi.ingsw.utils.Logger;

import java.rmi.RemoteException;

/**
 * Server application entry point.
 */
public class Server {
    /**
     * Main of the server application.
     * It initializes the {@link MenuController}.
     * It starts the {@link ConnectionAcceptor} which waits for incoming TCP or RMI connections from users.
     * For every incoming connection initializes a corresponding {@link VirtualView} and sets its {@link MenuController}.
     *
     * @param args allow to optionally specify the hostname of the server used to set "java.rmi.server.hostname" property.
     *             It is sufficient to enter an IP or hostname as the only argument.
     */
    public static void main(String[] args) {
        MenuController menuController = MenuController.getInstance();
        ConnectionAcceptor connectionAcceptor;

        try {
            if (args.length > 0) {
                ConnectionAcceptor.initialize(args[0]);
            }
            connectionAcceptor = new ConnectionAcceptor(8080, 8081);
        } catch (RemoteException | ConnectionException e) {
            Logger.writeCritical(e.toString());

            System.exit(1);
            return;
        }

        Logger.writeMessage("Server start");

        while (true) {
            Connection connection = connectionAcceptor.accept();
            Logger.writeMessage("New connection");

            NetworkEventTransceiver transceiver = new NetworkEventTransceiver(connection, new Object());

            new VirtualView(transceiver);
        }
    }
}
