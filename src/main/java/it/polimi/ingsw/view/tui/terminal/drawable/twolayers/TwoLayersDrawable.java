package it.polimi.ingsw.view.tui.terminal.drawable.twolayers;

import it.polimi.ingsw.view.tui.terminal.drawable.Coordinate;
import it.polimi.ingsw.view.tui.terminal.drawable.Drawable;
import it.polimi.ingsw.view.tui.terminal.drawable.DrawableSize;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.PrimitiveSymbol;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Symbol;

import java.util.Optional;

/**
 * Represents a Drawable composed by two layers: background and foreground. Each layer is a Drawable which can be
 * displayed on the terminal. This Drawable allows to display only the background or the foreground and the background
 * together.
 * In particular, if the foreground at a certain {@link Coordinate} displays {@link PrimitiveSymbol#EMPTY}, it is
 * possible to see through, that is we see the background {@link Symbol} at the same {@link Coordinate}.
 *
 * @author Cristiano Migali
 */
public class TwoLayersDrawable extends Drawable {
    /**
     * It is the background layer Drawable.
     */
    private final Drawable background;

    /**
     * It is the foreground layer Drawable.
     */
    private final Drawable foreground;

    /**
     * It is true iff we should also show the foreground.
     */
    private boolean foregroundToShow = false;

    /**
     * Constructor of the class.
     * Initializes the background and foreground layer.
     *
     * @param background is the Drawable associated with the background layer.
     * @param foreground is the Drawable associated with the foreground layer.
     */
    public TwoLayersDrawable(Drawable background, Drawable foreground) {
        this.background = background;
        this.foreground = foreground;
    }

    @Override
    public void askForSize(DrawableSize desiredSize) {
        background.askForSize(desiredSize);
        foreground.askForSize(desiredSize);

        if (foregroundToShow) {
            size = foreground.getSize();
        } else {
            size = background.getSize();
        }
    }

    @Override
    public Symbol getSymbolAt(Coordinate coordinate) {
        if (foregroundToShow) {
            Symbol symbol = foreground.getSymbolAt(coordinate);

            if (symbol == PrimitiveSymbol.EMPTY) {
                symbol = background.getSymbolAt(coordinate);
            }

            return symbol;
        }

        return background.getSymbolAt(coordinate);
    }

    @Override
    public boolean handleInput(String key) {
        if (foregroundToShow) {
            boolean handled = foreground.handleInput(key);

            if (!handled) {
                return background.handleInput(key);
            }
        }

        return background.handleInput(key);
    }

    @Override
    public boolean focus(Coordinate desiredCoordinate) {
        return foregroundToShow ? foreground.focus(desiredCoordinate) :
            background.focus(desiredCoordinate);
    }

    @Override
    public void unfocus() {
        if (foregroundToShow) {
            foreground.unfocus();
        } else {
            background.unfocus();
        }
    }

    @Override
    public Optional<Coordinate> getFocusedCoordinate() {
        if (foregroundToShow) {
            return foreground.getFocusedCoordinate();
        }

        return background.getFocusedCoordinate();
    }

    /**
     * @return this TwoLayersDrawable which is now displaying also the foreground layer.
     */
    public TwoLayersDrawable showForeground() {
        if (foregroundToShow) {
            return this;
        }

        foregroundToShow = true;

        if (background.getFocusedCoordinate().isPresent()) {
            foreground.focus(background.getFocusedCoordinate().get());
            background.unfocus();
        }

        return this;
    }

    /**
     * @return this TwoLayersDrawable which isn't displaying the foreground layer anymore.
     */
    public TwoLayersDrawable hideForeground() {
        if (!foregroundToShow) {
            return this;
        }

        foregroundToShow = false;

        if (foreground.getFocusedCoordinate().isPresent()) {
            background.focus(foreground.getFocusedCoordinate().get());
            foreground.unfocus();
        }

        return this;
    }
}
