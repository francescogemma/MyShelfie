package it.polimi.ingsw.view.tui.terminal.drawable.app;

import it.polimi.ingsw.view.tui.terminal.drawable.Coordinate;
import it.polimi.ingsw.view.tui.terminal.drawable.DrawableSize;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Represents a full TUI Application composed of different displayable layouts, which can handle user input (provided
 * through the keyboard).
 * More formally an App is a set of {@link AppLayout} plus a specified starting layout, which is the one that will be
 * displayed first. Every {@link AppLayout} is capable of handling user input (and being displayed on the terminal
 * of course) and can interact with the App instance to change the current layout or exit.
 * Furthermore an App provides a mechanism (through {@link AppDataProvider}) of data exchange between layouts.
 * When a layout asks the App to exit, the data that it (the layout) produces (for example a layout about login could
 * produce data like a username) are returned to the caller which started the app.
 *
 * An App can be started through {@link it.polimi.ingsw.view.tui.terminal.Terminal#start(App)}.
 * Note that you can't start the same App instance more than once.
 *
 * @author Cristiano Migali
 */
public class App {
    /**
     * Default name used to tell a layout that is the starting one. In other terms, it is the name of the layout "before"
     * the starting one.
     */
    public static final String START_NAME = "START";

    /**
     * Default name used to tell the app instance to exit. In other terms, it is the name of the layout "after" the last
     * one.
     */
    public static final String EXIT_NAME = "EXIT";

    /**
     * The initial layout of the app. That is the first layout that will be displayed on screen when the app starts.
     */
    private AppLayout startAppLayout;

    /**
     * The current layout, that is the one which is being displayed on screen.
     */
    private AppLayout currentAppLayout;

    /**
     * The Map which maps layouts' names to their instances.
     */
    private final Map<String, AppLayout> appLayouts = new HashMap<>();

    /**
     * THe Map which maps layouts' names to their provided data (through an {@link AppLayout}).
     * This map is used to build that {@link AppDataProvider} which is available to every layout and allows it
     * to retrieve data provided by other layouts.
     */
    private final Map<String, AppLayoutData> appLayoutsData = new HashMap<>();

    /**
     * Constructor of the class. Creates the instances for all the layouts in the app, initializes the starting
     * layout, the {@link App#appLayouts} map, the {@link App#appLayoutsData} map and then creates the
     * {@link AppDataProvider} for the app. Finally it sets the AppDataProvider for every layout in the class.
     *
     * @param appLayoutsSuppliers is the list which contains a producer (usually the constructor) for every
     * {@link AppLayout} in the App. The App indeed constructs {@link AppLayout} instances itself, preventing "exposure
     *                            of rep".
     *                            The first producer indicates the starting {@link AppLayout} of the App.
     */
    public App(Supplier<AppLayout> ...appLayoutsSuppliers) {
        if (appLayoutsSuppliers.length == 0) {
            throw new IllegalArgumentException("There must be at least on app layout in order to build an app");
        }

        for (int i = 0; i < appLayoutsSuppliers.length; i++) {
            AppLayout appLayout = appLayoutsSuppliers[i].get();

            if (i == 0) {
                startAppLayout = appLayout;
            }

            appLayouts.put(appLayout.getName(), appLayout);
            appLayoutsData.put(appLayout.getName(), appLayout.getData());

            appLayout.askForSize(new DrawableSize(0, 0));
            appLayout.focus(Coordinate.origin());
        }

        AppDataProvider appDataProvider = new AppDataProvider(appLayoutsData);

        for (AppLayout appLayout : appLayouts.values()) {
            appLayout.setAppDataProvider(appDataProvider);
        }
    }

    /**
     * Constructor of the class. In addition to what is done by the constructor which takes only {@link AppLayout}
     * producers, it allows to specify a set of initial data (through startAppLayoutData) which can be accesed by
     * every layout in the App specifying the layout name {@value App#START_NAME} in the {@link AppDataProvider}.
     *
     * @param startAppLayoutData is a set of initial data which can be accessed by every layout in the App specifying
     *                           the layout name {@value App#START_NAME} in the {@link AppDataProvider}.
     * @param appLayoutsSuppliers is the list which contains a producer (usually the constructor) for every
     * {@link AppLayout} in the App. The App indeed constructs {@link AppLayout} instances itself, preventing "exposure
     *                            of rep".
     *                            The first producer indicates the starting {@link AppLayout} of the App.
     */
    public App(AppLayoutData startAppLayoutData, Supplier<AppLayout> ...appLayoutsSuppliers) {
        this(appLayoutsSuppliers);

        this.appLayoutsData.put(START_NAME, startAppLayoutData);
    }

    /**
     * Allows to retrieve the next {@link AppLayout} which can be the same as before or a new one if the current
     * layout asked for a switch in response to a certain event.
     * This method is meant to be used internally by the {@link it.polimi.ingsw.view.tui.terminal.Terminal}
     * instance to draw the correct layout on screen.
     *
     * @return the current layout in the App, which could be the same as before or a new one.
     */
    public AppLayout getNextAppLayout() {
        if (currentAppLayout == null) {
            currentAppLayout = startAppLayout;
            currentAppLayout.setIsCurrentLayout(true);
            currentAppLayout.setup(START_NAME);
        } else {
            Optional<String> nextAppLayoutName = currentAppLayout.nextAppLayoutName();
            if (nextAppLayoutName.isPresent()) {
                currentAppLayout.beforeSwitch();
                currentAppLayout.setIsCurrentLayout(false);
                if (nextAppLayoutName.get().equals(EXIT_NAME)) {
                    mustExit = true;
                } else {
                    if (!appLayouts.containsKey(nextAppLayoutName.get())) {
                        throw new AppLayoutNotFoundException(nextAppLayoutName.get());
                    }

                    AppLayout oldAppLayout = currentAppLayout;
                    currentAppLayout = appLayouts.get(nextAppLayoutName.get());
                    currentAppLayout.setIsCurrentLayout(true);
                    currentAppLayout.setup(oldAppLayout.getName());
                }
            }
        }

        return currentAppLayout;
    }

    /**
     * True iff the current layout asked for exit.
     */
    private boolean mustExit = false;

    /**
     * @return true iff the current layout asked for exit.
     *
     * This method should be invoked by the {@link it.polimi.ingsw.view.tui.terminal.Terminal} instance which is
     * displaying the app in order to know when to stop drawing and restore original terminal configuration.
     */
    public boolean mustExit() {
        return mustExit;
    }

    /**
     * Sets the drawing lock object (held during drawing by the terminal) for every {@link AppLayout}.
     * In fact, every component which desires to modify data which can be displayed on screen must do it while
     * the terminal isn't drawing. In particular {@link AppLayout} instances hold the lock in order to display server
     * responses on screen.
     *
     * This method should be invoked by the {@link it.polimi.ingsw.view.tui.terminal.Terminal} instance which is
     * displaying the App, since it's the only component which has access to the drawing lock.
     *
     * @param lock is the drawing lock object held by the {@link it.polimi.ingsw.view.tui.terminal.Terminal} instance
     *             while drawing.
     */
    public void setLock(Object lock) {
        for (AppLayout appLayout : appLayouts.values()) {
            appLayout.setLock(lock);
        }
    }
}
