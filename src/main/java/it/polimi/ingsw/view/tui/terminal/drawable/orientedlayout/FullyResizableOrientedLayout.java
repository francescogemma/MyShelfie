package it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout;

import it.polimi.ingsw.view.tui.terminal.drawable.*;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Symbol;

import java.util.Optional;

public class FullyResizableOrientedLayout extends FullyResizableDrawable {
    private final OrientedLayout layout;

    public FullyResizableOrientedLayout(Orientation orientation,
                                               FullyResizableOrientedLayoutElement... elements) {
        layout = new OrientedLayout(orientation, elements);
    }

    @Override
    public void askForSize(DrawableSize desiredSize) {
        super.askForSize(desiredSize);

        layout.askForSize(desiredSize);
    }

    @Override
    public Symbol getSymbolAt(Coordinate coordinate) {
        return layout.getSymbolAt(coordinate);
    }

    @Override
    public boolean handleInput(String key) {
        return layout.handleInput(key);
    }

    @Override
    public boolean focus(Coordinate desiredCoordinate) {
        return layout.focus(desiredCoordinate);
    }

    @Override
    public void unfocus() {
        layout.unfocus();
    }

    @Override
    public Optional<Coordinate> getFocusedCoordinate() {
        return layout.getFocusedCoordinate();
    }
}
