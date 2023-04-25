package it.polimi.ingsw;

import it.polimi.ingsw.view.tui.*;
import it.polimi.ingsw.view.tui.terminal.Terminal;
import it.polimi.ingsw.view.tui.terminal.drawable.app.App;
import it.polimi.ingsw.view.tui.terminal.drawable.app.AppLayoutData;

public class Client {
    public static void main(String[] args) {
        Terminal terminal = Terminal.getInstance();

        AppLayoutData data = terminal.start(new App(InitialMenuLayout::new));

        if (data.getString("interface").equals("GUI")) {
            System.out.println("GUI not yet implemented! I'm going to blow up! 3... 2... 1... BOOM!");
        } else {
            terminal.start(new App(data, ConnectionMenuLayout::new, LoginMenuLayout::new,
                AvailableGamesMenuLayout::new, LobbyLayout::new, GameLayout::new));
        }
    }
}
