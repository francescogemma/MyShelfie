package it.polimi.ingsw.event;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.event.data.InsertTilesEventData;
import it.polimi.ingsw.event.data.LoginEventData;
import it.polimi.ingsw.event.data.MessageEventData;
import it.polimi.ingsw.event.data.wrapper.SyncEventDataWrapper;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.model.Tile;

import javax.swing.plaf.synth.ColorType;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        MockNetworkEventTransceiver networkTransceiver = new MockNetworkEventTransceiver();
        Controller controller = new Controller();
        VirtualView virtualView = new VirtualView(networkTransceiver, controller);

        Requester<MessageEventData, LoginEventData> loginOnNetwork = MessageEventData.requester(networkTransceiver, networkTransceiver);
        Requester<MessageEventData, InsertTilesEventData> insertTiles = MessageEventData.requester(networkTransceiver,
            networkTransceiver);

        System.out.println(loginOnNetwork.request(new LoginEventData("foo", "notBar")).getMessage());
        System.out.println(insertTiles.request(new InsertTilesEventData(0, Arrays.asList(Tile.MAGENTA, Tile.GREEN))).getMessage());

        System.out.println(loginOnNetwork.request(new LoginEventData("foo", "bar")).getMessage());

        networkTransceiver.broadcast(new MessageEventData("Hello, I'm authenticated"));

        System.out.println(insertTiles.request(new InsertTilesEventData(0, Arrays.asList(Tile.MAGENTA, Tile.GREEN))).getMessage());
    }
}

class VirtualView {
    private final MockNetworkEventTransceiver networkTransceiver;
    private final Controller controller;
    private String username = null;

    public VirtualView(MockNetworkEventTransceiver networkTransceiver, Controller controller) {
        this.networkTransceiver = networkTransceiver;
        this.controller = controller;

        LoginEventData.responder(networkTransceiver, networkTransceiver, data -> {
                if (isAuthenticated()) {
                    return new MessageEventData("You're already authenticated!");
                }

                if (controller.authenticate(data.getUsername(), data.getPassword())) {
                    username = data.getUsername();

                    return new MessageEventData("Successful login!");
                }

                return new MessageEventData("Login failed!");
            });

        MessageEventData.castEventReceiver(networkTransceiver).registerListener(data -> {
            if (isAuthenticated()) {
                controller.printMessage(username, data.getMessage());
            }
        });

        InsertTilesEventData.responder(networkTransceiver, networkTransceiver, data -> {
            if (!isAuthenticated()) {
                return new MessageEventData("You can't perform this while you're not authenticated");
            }

            controller.printTiles(data.getTiles());

            return new MessageEventData("Nice insertion!");
        });
    }

    public boolean isAuthenticated() {
        return username != null;
    }
}

class Controller {
    private final static Map<String, String> credentials = Map.of("foo", "bar");

    public boolean authenticate(String username, String password) {
        return credentials.get(username) != null && credentials.get(username).equals(password);
    }

    public void printMessage(String username, String message) {
        System.out.println(username + ": " + message);
    }

    public void printTiles(List<Tile> tiles) {
        for (Tile tile : tiles) {
            System.out.println(tile.color(tile.toString()));
        }
    }
}
