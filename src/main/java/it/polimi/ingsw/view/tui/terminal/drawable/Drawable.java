package it.polimi.ingsw.view.tui.terminal.drawable;

import it.polimi.ingsw.view.tui.terminal.drawable.align.CenteredDrawable;
import it.polimi.ingsw.view.tui.terminal.drawable.align.UpLeftAlignedDrawable;
import it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.OrientedLayoutElement;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Symbol;

import java.util.Optional;

public abstract class Drawable {
    protected DrawableSize size;

    public final DrawableSize getSize() {
        return size;
    }

    public final Coordinate getCenter() {
        return new Coordinate(size.getLines() / 2 + 1, size.getColumns() / 2 + 1);
    }

    public abstract void askForSize(DrawableSize desiredSize);

    public abstract Symbol getSymbolAt(Coordinate coordinate);

    public abstract boolean handleInput(String key);

    public abstract boolean focus(Coordinate desiredCoordinate);

    public abstract void unfocus();

    public abstract Optional<Coordinate> getFocusedCoordinate();

    public OrientedLayoutElement weight(int weight) {
        return new OrientedLayoutElement(this, weight);
    }

    public CenteredDrawable center() {
        return new CenteredDrawable(this);
    }

    public UpLeftAlignedDrawable alignUpLeft() {
        return new UpLeftAlignedDrawable(this);
    }

    public WithBorderBoxDrawable addBorderBox() {
        return new WithBorderBoxDrawable(this);
    }

    public BlurrableDrawable blurrable() {
        return new BlurrableDrawable(this);
    }
}
