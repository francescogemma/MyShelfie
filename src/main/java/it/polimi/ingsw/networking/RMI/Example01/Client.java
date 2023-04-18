package it.polimi.ingsw.networking.RMI.Example01;

import it.polimi.ingsw.networking.RMI.RMIConnection;

public class Client {
    public static void main( String[] args ) {
        try {
            RMIConnection myClient = new RMIConnection(1098, 1099);

            System.out.println("Received: \"" + myClient.receive());
            myClient.send("Boo!");


        } catch (Exception exception) {
            System.out.println("Connection lost!");
        }


    }
}
