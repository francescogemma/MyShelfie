package it.polimi.ingsw.event;


import it.polimi.ingsw.controller.Response;
import it.polimi.ingsw.event.data.client.LoginEventData;
import it.polimi.ingsw.event.data.game.BoardChangedEventData;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventListener;
import it.polimi.ingsw.networking.DisconnectedException;

import java.util.function.Consumer;

public class Main {
    /*
    public static void main(String[] args) throws DisconnectedException {
        EventTransceiver eventTransceiver = new LocalEventTransceiver();
        Requester<Response, LoginEventData> requester = Response.requester(eventTransceiver, eventTransceiver, new Object());

        Response response = requester.request(new LoginEventData("Giacomo", "Ciao"));
    }
    */

    public static void main(String[] args) {
        BoardChangedEventData.castEventReceiver(new LocalEventTransceiver()).registerListener(event -> {

        });

        CastEventReceiver<BoardChangedEventData> castEventReceiver = BoardChangedEventData.castEventReceiver(new LocalEventTransceiver());

        EventListener<BoardChangedEventData> listener = event -> {

        };

        castEventReceiver.registerListener(listener);

        castEventReceiver.unregisterListener(listener);
        castEventReceiver.unregisterAllListeners();
    }
}
