package it.polimi.ingsw.view.tui.terminal.drawable.app;

public class AppLayoutNotFoundException extends RuntimeException {
    public AppLayoutNotFoundException(String appLayoutName) {
        super(appLayoutName + " not found in the app");
    }
}
