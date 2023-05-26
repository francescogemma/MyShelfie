package it.polimi.ingsw.view.tui.terminal.drawable.app;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Represents the set of data provided by an {@link AppLayout}. Labels are associated to data (which could be an
 * object, a string, an integer, ...) in order to retrieve it.
 * Instead of directly setting data in correspondence to certain labels, a supplier is provided which can be invoked
 * to retrieve (or recalculate) the desired data; in this way we ensure that data is updated.
 */
public class AppLayoutData {
    /**
     * Maps the label of the relative data to its supplier.
     */
    private final Map<String, Supplier<Object>> labelsValues;

    /**
     * Constructor of the class. Initializes the map which maps every label to the corresponding supplier which allows
     * to retrieve the desired data.
     *
     * @param labelsValues is the map which maps every label to the corresponding data supplier.
     */
    public AppLayoutData(Map<String, Supplier<Object>> labelsValues) {
        this.labelsValues = new HashMap<>(labelsValues);
    }

    /**
     * @param label is the label associated with the supplier of the desired data.
     *
     * @return the data provided by the supplier associated by the given label.
     *
     * @throws NoDataAtLabelException if there is no supplier associated with the given label.
     */
    private Supplier<Object> getSupplier(String label) {
        Supplier<Object> supplier = labelsValues.get(label);

        if (supplier == null) {
            throw new NoDataAtLabelException(label);
        }

        return supplier;
    }

    /**
     * @param label is the label associated with the desired data.
     *
     * @return the data with the desired label.
     *
     * @throws NoDataAtLabelException if there is no data associated with the given label.
     */
    public Object get(String label) {
        return getSupplier(label).get();
    }

    /**
     * @param label is the label associated with the desired integer.
     *
     * @return the integer with the desired label.
     *
     * @throws NoDataAtLabelException if there is no data associated with the given label.
     * @throws WrongDataTypeAtLabelException if the data at the given label is not an integer.
     */
    public int getInt(String label) {
        try {
            return (Integer) getSupplier(label).get();
        } catch (ClassCastException e) {
            throw new WrongDataTypeAtLabelException(label, "integer");
        }
    }

    /**
     * @param label is the label associated with the desired string.
     *
     * @return the string with the desired label.
     *
     * @throws NoDataAtLabelException if there is no data associated with the given label.
     * @throws WrongDataTypeAtLabelException if the data at the given label is not a string.
     */
    public String getString(String label) {
        try {
            return (String) getSupplier(label).get();
        } catch (ClassCastException e) {
            throw new WrongDataTypeAtLabelException(label, "string");
        }
    }

    /**
     * @param label is the label associated with the desired boolean.
     *
     * @return the boolean with the desired label.
     *
     * @throws NoDataAtLabelException if there is no data associated with the given label.
     * @throws WrongDataTypeAtLabelException if the data at the given label is not a boolean.
     */
    public boolean getBoolean(String label) {
        try {
            return (Boolean) getSupplier(label).get();
        } catch (ClassCastException e) {
            throw new WrongDataTypeAtLabelException(label, "boolean");
        }
    }
}
