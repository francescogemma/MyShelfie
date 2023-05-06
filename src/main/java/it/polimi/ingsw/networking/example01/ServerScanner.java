package it.polimi.ingsw.networking.example01;

import it.polimi.ingsw.networking.Connection;
import it.polimi.ingsw.networking.ConnectionAcceptor;
import it.polimi.ingsw.networking.DisconnectedException;

import java.util.Scanner;

public class ServerScanner {
    public static void main(String[] args) {
        final int TCP_PORT = 1234;
        final int RMI_PORT = 5678;

        Connection connection;
        Scanner scanner = new Scanner(System.in);

        try {
            ConnectionAcceptor.initialize("x.x.x.x");
            ConnectionAcceptor serverAcceptor = new ConnectionAcceptor(TCP_PORT, RMI_PORT);
            System.out.println("waiting for connection...");
            connection = serverAcceptor.accept();
            System.out.println("connected");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        try {
            System.out.print("Insert a string to send to the client: ");
            connection.send(scanner.nextLine());
            System.out.println("received: " + connection.receive());
        } catch (DisconnectedException e) {
            e.printStackTrace();
            System.out.println("disconnected");
        }
    }
}
