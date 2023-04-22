package it.polimi.ingsw.view.tui.terminal.drawable.app;

import it.polimi.ingsw.view.tui.terminal.drawable.Coordinate;
import it.polimi.ingsw.view.tui.terminal.drawable.DrawableSize;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class App {
    public static final String START_NAME = "START";
    public static final String EXIT_NAME = "EXIT";

    private AppLayout startAppLayout;
    private AppLayout currentAppLayout;

    private final Map<String, AppLayout> appLayouts = new HashMap<>();
    private final Map<String, AppLayoutData> appLayoutsData = new HashMap<>();

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

    public App(AppLayoutData startAppLayoutData, Supplier<AppLayout> ...appLayoutsSuppliers) {
        this(appLayoutsSuppliers);

        this.appLayoutsData.put(START_NAME, startAppLayoutData);
    }

    public Optional<AppLayout> getNextAppLayout() {
        if (currentAppLayout == null) {
            currentAppLayout = startAppLayout;
            currentAppLayout.setIsCurrentLayout(true);
            currentAppLayout.setup(START_NAME);
        } else {
            Optional<String> nextAppLayoutName = currentAppLayout.nextAppLayoutName();
            if (nextAppLayoutName.isPresent()) {
                currentAppLayout.setIsCurrentLayout(false);
                if (nextAppLayoutName.get().equals(EXIT_NAME)) {
                    currentAppLayout = null;
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

        return Optional.ofNullable(currentAppLayout);
    }

    public void setLock(Object lock) {
        for (AppLayout appLayout : appLayouts.values()) {
            appLayout.setLock(lock);
        }
    }
}
