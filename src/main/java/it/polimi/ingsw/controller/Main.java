package it.polimi.ingsw.controller;

import it.polimi.ingsw.controller.db.DBManager;
import it.polimi.ingsw.event.LocalEventTransceiver;
import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.data.LoginEventData;

public class Main {
    public static void main(String []arg) {
        /*LocalEventTransceiver transceiver = new LocalEventTransceiver();
        MenuController menuController = MenuController.getInstance();
        VirtualView virtualView = new VirtualView(transceiver);

        menuController.join(virtualView);

        Requester<Response, LoginEventData> loginOnNetwork = Response.requester(virtualView.getNetworkTransmitter(), virtualView.getNetworkReceiver());

        Response response = loginOnNetwork.request(new LoginEventData("Pippo", "PasswordSegreta"));
        System.out.println("is ok: " + response.isOk());*/
    }
}