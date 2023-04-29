package it.polimi.ingsw;

import it.polimi.ingsw.utils.Logger;
import it.polimi.ingsw.view.gui.GuiApplication;
import it.polimi.ingsw.view.tui.*;
import it.polimi.ingsw.view.tui.terminal.Terminal;
import it.polimi.ingsw.view.tui.terminal.drawable.app.App;
import it.polimi.ingsw.view.tui.terminal.drawable.app.AppLayoutData;

public class Client {
    public static void main(String[] args) {
        Terminal terminal = Terminal.getInstance();

        Logger.setShouldPrint(false);

        AppLayoutData data = terminal.start(new App(InitialMenuLayout::new));

        if (data.getString("interface").equals("GUI")) {
            GuiApplication.main(args);
        } else {
            terminal.start(new App(data, ConnectionMenuLayout::new, LoginMenuLayout::new,
                AvailableGamesMenuLayout::new, LobbyLayout::new, GameLayout::new,
                GameOverLayout::new));
        }
    }
}
