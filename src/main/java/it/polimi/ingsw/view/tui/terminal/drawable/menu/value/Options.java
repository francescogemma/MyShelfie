package it.polimi.ingsw.view.tui.terminal.drawable.menu.value;

import it.polimi.ingsw.view.tui.terminal.drawable.Coordinate;
import it.polimi.ingsw.view.tui.terminal.drawable.DrawableSize;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Symbol;
import it.polimi.ingsw.view.tui.terminal.Terminal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Options extends ValueDrawable<String> {
    private final TextBox textBox;

    private final List<String> optionsValues;

    private int selectedOptionValueIndex = 0;

    private static final String SPACES = "  ";
    private static final String LEFT_ARROW = "< ";
    private static final String RIGHT_ARROW = " >";

    public Options(String ...optionsValues) {
        if (optionsValues.length < 2) {
            throw new IllegalArgumentException("You must specify at least two options");
        }

        this.optionsValues = Arrays.asList(optionsValues);

        textBox = new TextBox().text(SPACES + optionsValues[selectedOptionValueIndex] + RIGHT_ARROW).unfocusable();

        size = textBox.getSize();
    }

    @Override
    public void askForSize(DrawableSize desiredSize) {
        textBox.askForSize(desiredSize);
    }

    @Override
    public Symbol getSymbolAt(Coordinate coordinate) {
        return textBox.getSymbolAt(coordinate);
    }

    private boolean nextOption(int direction) {
        if (direction != -1 && direction != 1) {
            throw new IllegalArgumentException("Direction must be -1 or 1 when switching options, got: " + direction);
        }

        int nextOptionIndex = selectedOptionValueIndex + direction;
        if (nextOptionIndex < 0 || nextOptionIndex >= optionsValues.size()) {
            return false;
        }

        selectedOptionValueIndex = nextOptionIndex;
        if (selectedOptionValueIndex == 0) {
            textBox.text(SPACES + optionsValues.get(0) + RIGHT_ARROW);
        } else if (selectedOptionValueIndex == optionsValues.size() - 1) {
            textBox.text(LEFT_ARROW + optionsValues.get(optionsValues.size() - 1) + SPACES);
        } else {
            textBox.text(LEFT_ARROW + optionsValues.get(selectedOptionValueIndex) + RIGHT_ARROW);
        }

        return true;
    }

    @Override
    public boolean handleInput(String key) {
        return switch (key) {
            case Terminal.LEFT_ARROW, "a" -> nextOption(-1);
            case Terminal.RIGHT_ARROW, "d" -> nextOption(1);
            default -> false;
        };
    }

    @Override
    public boolean focus(Coordinate desiredCoordinate) {
        return true;
    }

    @Override
    public void unfocus() {
        // Options state does not depend on its focus state
    }

    @Override
    public Optional<Coordinate> getFocusedCoordinate() {
        return Optional.of(Coordinate.origin());
    }

    @Override
    public String getValue() {
        return optionsValues.get(selectedOptionValueIndex);
    }
}
