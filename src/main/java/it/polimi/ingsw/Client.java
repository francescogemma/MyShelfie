package it.polimi.ingsw;

import it.polimi.ingsw.utils.Logger;
import it.polimi.ingsw.view.gui.GuiApplication;
import it.polimi.ingsw.view.tui.*;
import it.polimi.ingsw.view.tui.terminal.Terminal;
import it.polimi.ingsw.view.tui.terminal.drawable.app.App;
import it.polimi.ingsw.view.tui.terminal.drawable.app.AppLayoutData;

/**
 * Client application entry point.
 */
public class Client {
    /**
     * Main of the client application.
     * It starts the terminal application to allow the user to choose the interface type and the
     * connection type; then starts another terminal application or the GUI one according to the user choice.
     */
    public static void main(String[] args) {
        Terminal terminal = Terminal.getInstance();

        Logger.setShouldPrint(false);
        Logger.setEnableWriteToFile(false);

        AppLayoutData data = terminal.start(new App(InitialMenuLayout::new));

        if (data.getBoolean("exit")) {
            return;
        }

        if (data.getString("interface").equals("GUI")) {
            GuiApplication.main(new String[]{data.getString("connection")});
        } else {
            terminal.start(new App(data, ConnectionMenuLayout::new, LoginMenuLayout::new,
                AvailableGamesMenuLayout::new, LobbyLayout::new, GameLayout::new,
                GameOverLayout::new));
        }

        System.exit(0);
    }
}
