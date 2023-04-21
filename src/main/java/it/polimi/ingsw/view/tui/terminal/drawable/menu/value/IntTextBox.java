package it.polimi.ingsw.view.tui.terminal.drawable.menu.value;

import it.polimi.ingsw.view.tui.terminal.Terminal;
import it.polimi.ingsw.view.tui.terminal.drawable.Coordinate;
import it.polimi.ingsw.view.tui.terminal.drawable.DrawableSize;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Symbol;

import java.util.Optional;

public class IntTextBox extends ValueDrawable<Integer> {
    private final TextBox textBox = new TextBox();

    @Override
    public void askForSize(DrawableSize desiredSize) {
        textBox.askForSize(desiredSize);

        size = textBox.getSize();
    }

    @Override
    public Symbol getSymbolAt(Coordinate coordinate) {
        return textBox.getSymbolAt(coordinate);
    }

    @Override
    public boolean handleInput(String key) {
        if (Terminal.TEXT.contains(key)) {
            return false;
        }

        return textBox.handleInput(key);
    }

    @Override
    public boolean focus(Coordinate desiredCoordinate) {
        return textBox.focus(desiredCoordinate);
    }

    @Override
    public void unfocus() {
        textBox.unfocus();
    }

    @Override
    public Optional<Coordinate> getFocusedCoordinate() {
        return textBox.getFocusedCoordinate();
    }

    @Override
    public Integer getValue() {
        return Integer.valueOf(textBox.getValue().equals(" ") ? "0" : textBox.getValue());
    }

    public IntTextBox integer(int integer) {
        textBox.text(String.valueOf(integer));

        return this;
    }
}
