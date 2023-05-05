package it.polimi.ingsw.view.tui.terminal.drawable.app;

import it.polimi.ingsw.controller.Response;
import it.polimi.ingsw.view.tui.terminal.drawable.*;

import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

public abstract class AppLayout extends FixedLayoutDrawable<FullyResizableDrawable> {
    private String nextName = null;
    private ServerResponseDrawable serverResponseDrawable;

    private Timer timer = null;

    private Coordinate lastFocusedCoordinate = Coordinate.origin();

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

    protected void mustExit() {
        nextName = App.EXIT_NAME;
    }

    protected void switchAppLayout(String nextName) {
        if (!isCurrentLayout) {
            throw new IllegalStateException("Trying to switch layout when you're not the current one");
        }

        this.nextName = nextName;
    }

    public Optional<String> nextAppLayoutName() {
        try {
            return Optional.ofNullable(nextName);
        } finally {
            nextName = null;
        }
    }

    public abstract void setup(String previousLayoutName);
    public abstract void beforeSwitch();

    public abstract String getName();

    private AppLayoutData data = null;

    public final AppLayoutData getData() {
        if (data == null) {
            throw new IllegalStateException("You must set " + getName() + " app layout data in its constructor through"
                + " setData");
        }

        return data;
    }

    protected final void setData(AppLayoutData data) {
        if (this.data != null) {
            throw new IllegalStateException("You can set " + getName() + " app layout data only once");
        }

        this.data = data;
    }

    protected AppDataProvider appDataProvider;

    public void setAppDataProvider(AppDataProvider appDataProvider) {
        this.appDataProvider = appDataProvider;
    }

    private Object lock;

    void setLock(Object lock) {
        this.lock = lock;
    }

    protected Object getLock() {
        return lock;
    }

    private boolean isCurrentLayout = false;

    public void setIsCurrentLayout(boolean isCurrentLayout) {
        this.isCurrentLayout = isCurrentLayout;
    }

    protected boolean isCurrentLayout() {
        return isCurrentLayout;
    }

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
