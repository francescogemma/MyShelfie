package it.polimi.ingsw;

import it.polimi.ingsw.controller.MenuController;
import it.polimi.ingsw.controller.VirtualView;
import it.polimi.ingsw.event.NetworkEventTransceiver;
import it.polimi.ingsw.networking.Connection;
import it.polimi.ingsw.networking.ConnectionAcceptor;
import it.polimi.ingsw.networking.ConnectionException;

import java.rmi.RemoteException;

public class Server {
    public static void main(String[] args) {
        MenuController menuController = MenuController.getInstance();

        ConnectionAcceptor connectionAcceptor;
        try {
            connectionAcceptor = new ConnectionAcceptor(8080, 8081);
        } catch (RemoteException | ConnectionException e) {
            System.out.println(e);

            System.exit(1);
            return;
        }

        while (true) {
            Connection connection = connectionAcceptor.accept();
            System.out.println("New connection");

            NetworkEventTransceiver transceiver = new NetworkEventTransceiver(connection);

            VirtualView virtualView = new VirtualView(transceiver);

            menuController.join(virtualView);
        }
    }
}
