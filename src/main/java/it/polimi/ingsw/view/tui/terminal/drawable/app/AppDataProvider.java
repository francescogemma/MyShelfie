package it.polimi.ingsw.view.tui.terminal.drawable.app;

import java.util.Map;

public class AppDataProvider {
    private final Map<String, AppLayoutData> appLayoutsData;

    public AppDataProvider(Map<String, AppLayoutData> appLayoutsData) {
        this.appLayoutsData = appLayoutsData;
    }

    private AppLayoutData getAppLayoutData(String appLayoutName) {
        if (!appLayoutsData.containsKey(appLayoutName)) {
            throw new AppLayoutNotFoundException(appLayoutName);
        }

        return appLayoutsData.get(appLayoutName);
    }

    public Object get(String appLayoutName, String label) {
        return getAppLayoutData(appLayoutName).get(label);
    }

    public int getInt(String appLayoutName, String label) {
        return getAppLayoutData(appLayoutName).getInt(label);
    }

    public String getString(String appLayoutName, String label) {
        return getAppLayoutData(appLayoutName).getString(label);
    }

    public boolean getBoolean(String appLayoutName, String label) {
        return getAppLayoutData(appLayoutName).getBoolean(label);
    }
}
