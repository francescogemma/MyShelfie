package it.polimi.ingsw.view.tui.terminal.drawable.app;

import java.util.Map;

/**
 * Allows data exchange between all the {@link AppLayout} instances of an {@link App}.
 * It does so by containing the {@link AppLayoutData} provided by every {@link AppLayout} in the {@link App};
 * access to some data in some layout is reachable (by other layouts) by specifying the layout name and the data label.
 */
public class AppDataProvider {
    /**
     * Maps the name of every {@link AppLayout} to its {@link AppLayoutData}.
     */
    private final Map<String, AppLayoutData> appLayoutsData;

    /**
     * Constructor of the class. It initializes {@link AppDataProvider#appLayoutsData} maps, which allows mapping
     * between layouts' names and their data ({@link AppLayoutData}).
     *
     * @param appLayoutsData is a map which maps every layout's name to its data (the provided {@link AppLayoutData}).
     */
    public AppDataProvider(Map<String, AppLayoutData> appLayoutsData) {
        this.appLayoutsData = appLayoutsData;
    }

    /**
     * @param appLayoutName the name of the layout which provides the desired data.
     * @return the {@link AppLayoutData} provided by the layout whose name has been specified.
     *
     * @throws AppLayoutNotFoundException if there is no layout with the specified name.
     */
    private AppLayoutData getAppLayoutData(String appLayoutName) {
        if (!appLayoutsData.containsKey(appLayoutName)) {
            throw new AppLayoutNotFoundException(appLayoutName);
        }

        return appLayoutsData.get(appLayoutName);
    }

    /**
     * @param appLayoutName the name of the layout which provides the desired object.
     * @param label is the label associated with the desired object in the specified layout.
     * @return the object with the specified label in the specified layout.
     *
     * @throws AppLayoutNotFoundException if there is no layout with the specified name.
     * @throws NoDataAtLabelException if there is no data with the given label in the specified layout.
     */
    public Object get(String appLayoutName, String label) {
        return getAppLayoutData(appLayoutName).get(label);
    }

    /**
     * @param appLayoutName the name of the layout which provides the desired integer.
     * @param label is the label associated with the desired integer in the specified layout.
     * @return the integer with the specified label in the specified layout.
     *
     * @throws AppLayoutNotFoundException if there is no layout with the specified name.
     * @throws NoDataAtLabelException if there is no data with the given label in the specified layout.
     * @throws WrongDataTypeAtLabelException if the data with the specified label isn't an integer.
     */
    public int getInt(String appLayoutName, String label) {
        return getAppLayoutData(appLayoutName).getInt(label);
    }

    /**
     * @param appLayoutName the name of the layout which provides the desired string.
     * @param label is the label associated with the desired string in the specified layout.
     * @return the string with the specified label in the specified layout.
     *
     * @throws AppLayoutNotFoundException if there is no layout with the specified name.
     * @throws NoDataAtLabelException if there is no data with the given label in the specified layout.
     * @throws WrongDataTypeAtLabelException if the data with the specified label isn't a string.
     */
    public String getString(String appLayoutName, String label) {
        return getAppLayoutData(appLayoutName).getString(label);
    }

    /**
     * @param appLayoutName the name of the layout which provides the desired boolean.
     * @param label is the label associated with the desired boolean in the specified layout.
     * @return the boolean with the specified label in the specified layout.
     *
     * @throws AppLayoutNotFoundException if there is no layout with the specified name.
     * @throws NoDataAtLabelException if there is no data with the given label in the specified layout.
     * @throws WrongDataTypeAtLabelException if the data with the specified label isn't a boolean.
     */
    public boolean getBoolean(String appLayoutName, String label) {
        return getAppLayoutData(appLayoutName).getBoolean(label);
    }
}
