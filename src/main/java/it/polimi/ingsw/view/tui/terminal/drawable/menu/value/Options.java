package it.polimi.ingsw.view.tui.terminal.drawable.menu.value;

import it.polimi.ingsw.view.tui.terminal.drawable.Coordinate;
import it.polimi.ingsw.view.tui.terminal.drawable.DrawableSize;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Symbol;
import it.polimi.ingsw.view.tui.terminal.Terminal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * It is a {@link ValueDrawable} which allows to display a box with a finite predetermined set of values to choose from.
 * These values are shown one at the time.
 * We can switch between values using keyboard arrows.
 *
 * @author Cristiano Migali
 */
public class Options extends ValueDrawable<String> {
    /**
     * The underlying TextBox which contains the current displayed value surrounded by arrow symbols which indicate
     * allowed "directions" to switch values.
     */
    private final TextBox textBox;

    /**
     * Is the list representing the finite set of values that this ValueDrawable allows to choose from.
     */
    private final List<String> optionsValues;

    /**
     * Is the index of the current selected value with respect to the {@link Options#optionsValues} list.
     */
    private int selectedOptionValueIndex = 0;

    /**
     * Spaces to add to the left of the first value or to the right of the last value in the list when they are
     * being displayed on screen. This allows to keep the alignment consistent even if we don't have an arrow symbol
     * to show.
     */
    private static final String SPACES = "  ";

    /**
     * Arrow symbol to add to the left of the displayed value iff it is not the first in the list.
     */
    private static final String LEFT_ARROW = "< ";

    /**
     * Arrow symbol to add to the right of the displayed value iff it is not the last in the list.
     */
    private static final String RIGHT_ARROW = " >";

    /**
     * Constructor of the class. Creates an Options Drawable with the specified set of values to choose from.
     *
     * @param optionsValues is the list of values that the Options Drawable will allow to choose from.
     */
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

    /**
     * Switches to the next value in the list in the specified direction.
     *
     * @param direction is the direction to the next value in the list we will switch to.
     *                  Allowed values are only +1 (right) or -1 (left).
     * @return true iff the specified direction leads to a value inside the list (this is not the case for example
     * if we specify direction = -1 while the first value in the list is being shown).
     * @throws IllegalArgumentException if direction != -1 and direction != 1.
     */
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
