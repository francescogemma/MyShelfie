package it.polimi.ingsw.view.tui.terminal.drawable.app;

import it.polimi.ingsw.controller.Response;
import it.polimi.ingsw.view.tui.terminal.drawable.*;

import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Represents a layout inside a {@link App} which can be drawn on a terminal screen by a {@link it.polimi.ingsw.view.tui.terminal.Terminal}
 * instance and can handle user input (provided through the keyboard).
 * While the {@link it.polimi.ingsw.view.tui.terminal.Terminal} is running an {@link App} there is one and only one
 * displayed AppLayout. An AppLayout is built through a hierarchy of other drawables, composed one into the other, but
 * it is always the root element of this hierarchy.
 *
 * @see it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.OrientedLayout
 */
public abstract class AppLayout extends FixedLayoutDrawable<FullyResizableDrawable> {
    /**
     * Allows the current layout (displayed on the terminal screen) to indicate the next layout that should be shown,
     * by specifying its name. If it is null there should be no switch.
     */
    private String nextName = null;

    /**
     * Drawable used to display server response messages in the right-down corner of the screen.
     */
    private ServerResponseDrawable serverResponseDrawable;

    /**
     * Timer used to clear the serverResponseDrawable after half a second from the latest response.
     */
    private Timer timer = null;

    /**
     * It is the {@link Coordinate} of the last focused area of the screen while this layout is the current one.
     */
    private Coordinate lastFocusedCoordinate = Coordinate.origin();

    /**
     * An AppLayout is supposed to be always on focus (since it is the root of the hierarchy of displayed drawables),
     * hence, on every frame, if there is at least a focusable element on the layout, we should have some element on focus.
     *
     * This method checks if the previous focused element is still on focus (for example it could be not focusable
     * anymore in response to a network event or some user input); if this is not the case it tries to refocus
     * the last focused coordinate, then it updates the last focused coordinate.
     */
    private void refocus() {
        if (getFocusedCoordinate().isEmpty()) {
            unfocus();
            focus(lastFocusedCoordinate);
        }

        lastFocusedCoordinate = getFocusedCoordinate().orElse(Coordinate.origin());
    }

    @Override
    public void askForSize(DrawableSize desiredSize) {
        super.askForSize(desiredSize);

        refocus();
    }

    @Override
    public boolean handleInput(String key) {
        // When there is no focusable element in the layout, we can't handle input.
        if (getFocusedCoordinate().isEmpty()) {
            return false;
        }

        boolean inputHasBeenHandled = super.handleInput(key);

        if (!inputHasBeenHandled && key.equals("\t")) {
            unfocus();
            focus(Coordinate.origin());

            return true;
        }

        return inputHasBeenHandled;
    }

    @Override
    protected void setLayout(FullyResizableDrawable layout) {
        serverResponseDrawable = new ServerResponseDrawable(layout);
        super.setLayout(serverResponseDrawable);
    }

    /**
     * Asks the {@link App} to exit.
     */
    protected void mustExit() {
        nextName = App.EXIT_NAME;
    }

    /**
     * Asks the {@link App} to switch the current layout.
     *
     * @param nextName is the name of the desired next layout to be displayed.
     *
     * @throws IllegalStateException if you try to switch the layout from a layout which is not the current one.
     */
    protected void switchAppLayout(String nextName) {
        if (!isCurrentLayout) {
            throw new IllegalStateException("Trying to switch layout when you're not the current one");
        }

        this.nextName = nextName;
    }

    /**
     * @return an {@link Optional} which is empty if the current layout should not be switched,
     * otherwise it contains the name of the desired next layout to be displayed.
     */
    public Optional<String> nextAppLayoutName() {
        try {
            return Optional.ofNullable(nextName);
        } finally {
            nextName = null;
        }
    }

    /**
     * Initializes all the components of the layout before it will be displayed.
     * This method is invoked by the {@link App} every time the layout is going to be displayed on screen.
     *
     * @param previousLayoutName is the name of the previous displayed {@link AppLayout} or {@value App#START_NAME} if
     *                           this layout is the initial one.
     */
    public abstract void setup(String previousLayoutName);

    /**
     * Frees all the resources used by the layout and which are not necessary when it not being displayed.
     * This method is invoked by the {@link App} every time the layout is going to be hidden from the screen.
     */
    public abstract void beforeSwitch();

    /**
     * @return the name of the layout.
     */
    public abstract String getName();

    /**
     * Is the {@link AppLayoutData} provided by this layout, which will be added to the {@link AppDataProvider},
     * allowing other layouts to retrieved the desired data in this layout.
     */
    private AppLayoutData data = null;

    /**
     * @return the {@link AppLayoutData} provided by this layout. It allows to retrieve all the data exposed by
     * this layout through their label.
     */
    public final AppLayoutData getData() {
        if (data == null) {
            throw new IllegalStateException("You must set " + getName() + " app layout data in its constructor through"
                + " setData");
        }

        return data;
    }

    /**
     * Sets the {@link AppLayoutData} provided by this layout. It allows to expose some data generated inside this layout
     * to other layouts in the {@link App}.
     * The {@link AppLayoutData} can be set only once, an exception is thrown otherwise.
     *
     * @param data is the {@link AppLayoutData} that will be provided by this layout.
     *
     * @throws IllegalStateException if you try to set the {@link AppLayoutData} more than once.
     */
    protected final void setData(AppLayoutData data) {
        if (this.data != null) {
            throw new IllegalStateException("You can set " + getName() + " app layout data only once");
        }

        this.data = data;
    }

    protected AppDataProvider appDataProvider;

    /**
     * Sets the {@link AppDataProvider} crafted by the {@link App} in this layout.
     *
     * This method should be invoked only by the {@link App} after it craft the {@link AppDataProvider} by adjoining
     * the {@link AppLayoutData} from every layout.
     *
     * @param appDataProvider is the {@link AppDataProvider} crafted by the {@link App} by adjoining the
     *                        {@link AppLayoutData} from every layout.
     */
    void setAppDataProvider(AppDataProvider appDataProvider) {
        this.appDataProvider = appDataProvider;
    }

    /**
     * Is the lock object held by the terminal while drawing. Should be held in order to modify displayed elements
     * from other threads.
     */
    private Object lock;

    /**
     * Sets the drawing lock for this layout.
     *
     * @param lock is the lock object jeld by the terminal while drawing.
     */
    void setLock(Object lock) {
        this.lock = lock;
    }

    /**
     * You should retrieve the lock provided by this method and synchronize on it every time you want to
     * modify an element that affects what is being displayed on screen from another thread (that is not inside
     * {@link AppLayout#setup(String)}, {@link Drawable#handleInput(String)}).
     *
     * @return the drawing lock held by the {@link it.polimi.ingsw.view.tui.terminal.Terminal} instance while
     * drawing.
     */
    protected Object getLock() {
        return lock;
    }

    /**
     * True iff this the layout which is currently being displayed on screen.
     */
    private boolean isCurrentLayout = false;

    /**
     * Allows to specify if this layout is the current one or not.
     *
     * @param isCurrentLayout is true iff this layout is the current one (the one being displayed on screen).
     */
    public void setIsCurrentLayout(boolean isCurrentLayout) {
        this.isCurrentLayout = isCurrentLayout;
    }

    /**
     * @return true iff this the current layout which is being displayed on screen.
     */
    protected boolean isCurrentLayout() {
        return isCurrentLayout;
    }

    /**
     * Displays the server response on the down-right side of the screen for half a second.
     * The method should be invoked while holding the drawing lock provided by {@link AppLayout#getLock()}.
     *
     * @param response is the response from the server that we wish to display on screen.
     */
    protected void displayServerResponse(Response response) {
        serverResponseDrawable.showResponse(response);

        if (timer != null) {
            timer.cancel();
        }

        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                synchronized (getLock()) {
                    serverResponseDrawable.hideResponse();

                    timer = null;
                }
            }
        }, 500);
    }
}
