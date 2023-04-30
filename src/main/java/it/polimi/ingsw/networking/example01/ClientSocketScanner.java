package it.polimi.ingsw.networking.example01;

import it.polimi.ingsw.networking.Connection;
import it.polimi.ingsw.networking.DisconnectedException;
import it.polimi.ingsw.networking.TCP.TCPConnection;

import java.util.Scanner;

public class ClientSocketScanner {
    public static void main(String[] args) {
        final String ADDRESS = "x.x.x.x";
        final int PORT = 1234;
        Connection connection;
        Scanner scanner = new Scanner(System.in);

        try {
            connection = new TCPConnection(ADDRESS, PORT);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        try {
            System.out.println("received: " + connection.receive());
            System.out.print("Insert a string to send to the server: ");
            connection.send(scanner.nextLine());
        } catch (DisconnectedException e) {
            e.printStackTrace();
            System.out.println("disconnected");
        }

        connection.disconnect();
    }
}
