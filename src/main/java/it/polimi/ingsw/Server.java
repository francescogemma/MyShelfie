package it.polimi.ingsw;

import it.polimi.ingsw.controller.servercontroller.MenuController;
import it.polimi.ingsw.controller.VirtualView;
import it.polimi.ingsw.event.NetworkEventTransceiver;
import it.polimi.ingsw.networking.Connection;
import it.polimi.ingsw.networking.ConnectionAcceptor;
import it.polimi.ingsw.networking.ConnectionException;
import it.polimi.ingsw.utils.Logger;

import java.rmi.RemoteException;

public class Server {
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
