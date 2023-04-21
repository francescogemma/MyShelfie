package it.polimi.ingsw.view.tui.terminal.drawable.app;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class AppLayoutData {
    private final Map<String, Supplier<Object>> labelsValues;

    public AppLayoutData(Map<String, Supplier<Object>> labelsValues) {
        this.labelsValues = new HashMap<>(labelsValues);
    }

    private Supplier<Object> getSupplier(String label) {
        Supplier<Object> supplier = labelsValues.get(label);

        if (supplier == null) {
            throw new NoDataAtLabelException(label);
        }

        return supplier;
    }

    public Object get(String label) {
        return getSupplier(label).get();
    }

    public int getInt(String label) {
        try {
            return (Integer) getSupplier(label).get();
        } catch (ClassCastException e) {
            throw new WrongDataTypeAtLabelException(label, "integer");
        }
    }

    public String getString(String label) {
        try {
            return (String) getSupplier(label).get();
        } catch (ClassCastException e) {
            throw new WrongDataTypeAtLabelException(label, "string");
        }
    }

    public boolean getBoolean(String label) {
        try {
            return (Boolean) getSupplier(label).get();
        } catch (ClassCastException e) {
            throw new WrongDataTypeAtLabelException(label, "boolean");
        }
    }
}
