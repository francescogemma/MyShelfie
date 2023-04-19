package it.polimi.ingsw.networking.TCP.Example01;

import it.polimi.ingsw.networking.Connection;
import it.polimi.ingsw.networking.DisconnectedException;
import it.polimi.ingsw.networking.TCP.TCPConnection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    public static void main(String[] args) {
        final int PORT = 1234;
        ServerSocket serverSocket;
        Connection connection;
        Scanner scanner = new Scanner(System.in);

        try {
            serverSocket = new ServerSocket(PORT);
            Socket socket = serverSocket.accept();
            connection = new TCPConnection(socket);
            System.out.println("connected to: " + socket.getInetAddress().getHostAddress());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            System.out.print("Insert a string to send to the client: ");
            connection.send(scanner.nextLine());
            System.out.println(connection.receive());
        } catch (DisconnectedException e) {
            System.out.println("disconnected");
        }

        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
