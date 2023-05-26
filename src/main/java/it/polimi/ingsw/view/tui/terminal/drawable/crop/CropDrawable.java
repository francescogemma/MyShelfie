package it.polimi.ingsw.view.tui.terminal.drawable.crop;

import it.polimi.ingsw.view.tui.terminal.drawable.*;
import it.polimi.ingsw.view.tui.terminal.drawable.align.AlignedDrawable;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.PrimitiveSymbol;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Symbol;

import java.util.Optional;

/**
 * A CropDrawable is a {@link FullyResizableDrawable} built starting from an {@link AlignedDrawable}, which is a {@link Drawable}
 * with no maximum size, by taking only the up-left area in the original drawable if the desired size is less than
 * the one reachable by the AlignedDrawable.
 *
 * @see FullyResizableDrawable
 */
public class CropDrawable extends FullyResizableDrawable {
    /**
     * Is the AlignedDrawable which will be cropped, becoming a {@link FullyResizableDrawable}.
     */
    private final AlignedDrawable toCrop;

    /**
     * Constructor of the class. Initializes the AlignedDrawable which will be cropped.
     *
     * @param toCrop is the AlignedDrawable which will be cropped.
     */
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
