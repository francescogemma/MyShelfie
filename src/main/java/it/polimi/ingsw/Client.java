package it.polimi.ingsw;

import it.polimi.ingsw.controller.Response;
import it.polimi.ingsw.controller.ResponseStatus;
import it.polimi.ingsw.event.MockNetworkEventTransceiver;
import it.polimi.ingsw.event.data.LoginEventData;
import it.polimi.ingsw.view.tui.AvailableGamesMenuLayout;
import it.polimi.ingsw.view.tui.ConnectionMenuLayout;
import it.polimi.ingsw.view.tui.InitialMenuLayout;
import it.polimi.ingsw.view.tui.LoginMenuLayout;
import it.polimi.ingsw.view.tui.terminal.Terminal;
import it.polimi.ingsw.view.tui.terminal.drawable.app.App;
import it.polimi.ingsw.view.tui.terminal.drawable.app.AppLayoutData;

public class Client {
    private static final MockNetworkEventTransceiver transceiver = new MockNetworkEventTransceiver();

    public static MockNetworkEventTransceiver getTransceiver() {
        return transceiver;
    }

    static {
        LoginEventData.responder(transceiver, transceiver, data -> {
            if (data.getUsername().equals("foo") && data.getPassword().equals("bar")) {
                return new Response("You're now logged in!", ResponseStatus.SUCCESS);
            }

            return new Response("Bad credentials!", ResponseStatus.FAILURE);
        });
    }

    public static void main(String[] args) {
        Terminal terminal = Terminal.getInstance();

        AppLayoutData data = terminal.start(new App(InitialMenuLayout::new));

        if (data.getString("interface").equals("GUI")) {
            System.out.println("GUI not yet implemented! I'm going to blow up! 3... 2... 1... BOOM!");
        } else {
            terminal.start(new App(data, ConnectionMenuLayout::new, LoginMenuLayout::new,
                AvailableGamesMenuLayout::new));
        }

        transceiver.disconnect();
    }
}
