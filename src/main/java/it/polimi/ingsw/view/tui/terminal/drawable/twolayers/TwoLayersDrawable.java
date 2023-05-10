package it.polimi.ingsw.view.tui.terminal.drawable.twolayers;

import it.polimi.ingsw.view.tui.terminal.drawable.Coordinate;
import it.polimi.ingsw.view.tui.terminal.drawable.Drawable;
import it.polimi.ingsw.view.tui.terminal.drawable.DrawableSize;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.PrimitiveSymbol;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Symbol;

import java.util.Optional;

public class TwoLayersDrawable extends Drawable {
    private final Drawable background;
    private final Drawable foreground;

    private boolean foregroundToShow = false;

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
