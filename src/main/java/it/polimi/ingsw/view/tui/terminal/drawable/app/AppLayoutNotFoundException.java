package it.polimi.ingsw.view.tui.terminal.drawable.app;

/**
 * Exception thrown there is no {@link AppLayout} in the {@link App} with the specified name.
 *
 * @author Cristiano Migali
 */
public class AppLayoutNotFoundException extends RuntimeException {
    /**
     * @param appLayoutName is the specified name, which doesn't correspond to any {@link AppLayout} in the {@link App}-
     */
    public AppLayoutNotFoundException(String appLayoutName) {
        super(appLayoutName + " not found in the app");
    }
}
