package it.polimi.ingsw.networking.TCP.Example01;

import it.polimi.ingsw.networking.DisconnectedException;
import it.polimi.ingsw.networking.TCP.SocketCreationException;
import it.polimi.ingsw.networking.TCP.TCPConnection;

import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        final String ADDRESS = "localhost";
        final int PORT = 1234;
        TCPConnection connection;
        Scanner scanner = new Scanner(System.in);

        try {
            connection = new TCPConnection(ADDRESS, PORT);
        } catch (SocketCreationException e) {
            throw new RuntimeException(e);
        }

        try {
            System.out.println(connection.receive());
            System.out.print("Insert a string to send to the server: ");
            connection.send(scanner.nextLine());
        } catch (DisconnectedException e) {
            System.out.println("disconnected");
        }

        connection.disconnect();
    }
}
