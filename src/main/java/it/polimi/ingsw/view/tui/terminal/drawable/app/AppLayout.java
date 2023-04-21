package it.polimi.ingsw.view.tui.terminal.drawable.app;

import it.polimi.ingsw.view.tui.terminal.drawable.Coordinate;
import it.polimi.ingsw.view.tui.terminal.drawable.FixedLayoutDrawable;
import it.polimi.ingsw.view.tui.terminal.drawable.FullyResizableDrawable;

import java.util.Optional;

public abstract class AppLayout extends FixedLayoutDrawable<FullyResizableDrawable> {
    private String nextName = null;

    @Override
    public boolean handleInput(String key) {
        if (!super.handleInput(key) && key.equals("\t")) {
            unfocus();
            focus(Coordinate.origin());
        }

        return true;
    }

    protected void mustExit() {
        nextName = App.EXIT_NAME;
    }

    protected void switchAppLayout(String nextName) {
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
}
