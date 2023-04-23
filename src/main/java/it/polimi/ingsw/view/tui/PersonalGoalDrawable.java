package it.polimi.ingsw.view.tui;

import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.bookshelf.Shelf;
import it.polimi.ingsw.model.tile.TileColor;
import it.polimi.ingsw.view.tui.terminal.drawable.Coordinate;
import it.polimi.ingsw.view.tui.terminal.drawable.Drawable;
import it.polimi.ingsw.view.tui.terminal.drawable.DrawableSize;
import it.polimi.ingsw.view.tui.terminal.drawable.WithBorderBoxDrawable;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.PrimitiveSymbol;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Symbol;

import java.util.Map;
import java.util.Optional;

public class PersonalGoalDrawable extends Drawable {
    private Map<Shelf, TileColor> tilesColorMask = Map.of();

    public PersonalGoalDrawable() {
        size = new DrawableSize(Bookshelf.ROWS + 3, Bookshelf.COLUMNS + 3);
    }

    @Override
    public void askForSize(DrawableSize desiredSize) {
        // Personal goal drawables have fixed size.
    }

    @Override
    public Symbol getSymbolAt(Coordinate coordinate) {
        if (coordinate.getLine() == 1 && coordinate.getColumn() >= 3 && coordinate.getColumn() <
            Bookshelf.COLUMNS + 3) {
            return PrimitiveSymbol.fromString(String.valueOf(coordinate.getColumn() - 2));
        }

        if (coordinate.getColumn() == 1 && coordinate.getLine() >= 3 && coordinate.getLine() <
            Bookshelf.ROWS + 3) {
            return PrimitiveSymbol.fromString(String.valueOf(coordinate.getLine() - 2));
        }

        if (coordinate.getLine() >= 2 && coordinate.getColumn() >= 2) {
            return WithBorderBoxDrawable.addBorder(new Coordinate(
                coordinate.getLine() - 1,
                coordinate.getColumn() - 1
            ), new DrawableSize(Bookshelf.ROWS + 2, Bookshelf.COLUMNS + 2))
                .map(Symbol.class::cast)
                .orElseGet(
                () -> {
                    if (coordinate.getLine() >= 3 && coordinate.getLine() < Bookshelf.ROWS + 3 &&
                        coordinate.getColumn() >= 3 && coordinate.getColumn() < Bookshelf.COLUMNS + 3 &&
                        tilesColorMask.containsKey(Shelf.getInstance(coordinate.getLine() - 3,
                        coordinate.getColumn() - 3))) {

                        return PrimitiveSymbol.EMPTY
                            .colorBackground(TileDrawable.tileColorToColor(
                                tilesColorMask.get(Shelf.getInstance(coordinate.getLine() -3,
                                coordinate.getColumn() - 3))
                            ));
                    }

                    return PrimitiveSymbol.EMPTY;
                }
            );
        }

        return PrimitiveSymbol.EMPTY;
    }

    @Override
    public boolean handleInput(String key) {
        return false;
    }

    @Override
    public boolean focus(Coordinate desiredCoordinate) {
        return false;
    }

    @Override
    public void unfocus() {
        // Personal goal drawables can't be focused
    }

    @Override
    public Optional<Coordinate> getFocusedCoordinate() {
        return Optional.empty();
    }

    public PersonalGoalDrawable populate(Map<Shelf, TileColor> tilesColorMask) {
        this.tilesColorMask = tilesColorMask;

        return this;
    }
}
