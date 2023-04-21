package it.polimi.ingsw.view.tui.terminal.drawable.crop;

import it.polimi.ingsw.view.tui.terminal.drawable.*;
import it.polimi.ingsw.view.tui.terminal.drawable.align.AlignedDrawable;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.PrimitiveSymbol;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Symbol;

import java.util.Optional;

public class CropDrawable extends FullyResizableDrawable {
    private final Drawable toCrop;

    public CropDrawable(AlignedDrawable toCrop) {
        this.toCrop = toCrop;
    }

    @Override
    public void askForSize(DrawableSize desiredSize) {
        super.askForSize(desiredSize);

        toCrop.askForSize(desiredSize);
    }

    @Override
    public Symbol getSymbolAt(Coordinate coordinate) {
        try {
            return toCrop.getSymbolAt(coordinate);
        } catch (OutOfDrawableException e) {
            return PrimitiveSymbol.EMPTY;
        }
    }

    @Override
    public boolean handleInput(String key) {
        return toCrop.handleInput(key);
    }

    @Override
    public boolean focus(Coordinate desiredCoordinate) {
        return toCrop.focus(desiredCoordinate);
    }

    @Override
    public void unfocus() {
        toCrop.unfocus();
    }

    @Override
    public Optional<Coordinate> getFocusedCoordinate() {
        return toCrop.getFocusedCoordinate();
    }
}
