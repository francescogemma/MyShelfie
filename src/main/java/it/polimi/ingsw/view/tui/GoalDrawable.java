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

/**
 * It is a {@link Drawable} which allows to display a personal or common goal on a terminal screen.
 * In particular the goal is displayed as a bookshelf where every tile occupies exactly one terminal cell,
 * the bookshelf is surrounded by a border box. It provides control on the color of every tile in such a bookshelf.
 * Representing a personal goal is straightforward, we just need to color the bookshelf tiles accordingly.
 * For common goals a set of {@link it.polimi.ingsw.model.bookshelf.BookshelfMask}s which would satisfy the goal
 * is represented instead.
 *
 * @author Cristiano Migali
 */
public class GoalDrawable extends Drawable {
    /**
     * Map which associates every {@link Shelf} of the represented bookshelf with the corresponding {@link TileColor}
     * with which it should be colored.
     */
    private Map<Shelf, TileColor> tilesColorMask = Map.of();

    /**
     * Constructor of the class.
     * It initializes the size of the Drawable.
     */
    public GoalDrawable() {
        size = new DrawableSize(Bookshelf.ROWS + 3, Bookshelf.COLUMNS + 3);
    }

    @Override
    public void askForSize(DrawableSize desiredSize) {
        // Goal drawables have fixed size.
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
        // Goal drawables can't be focused
    }

    @Override
    public Optional<Coordinate> getFocusedCoordinate() {
        return Optional.empty();
    }

    /**
     * Allows to populate the GoalDrawable display specifying the color of each tile in the bookshelf.
     *
     * @param tilesColorMask is a map which indicates the correspondence between every {@link Shelf} in the bookshelf
     *                       display and its color.
     * @return this GoalDrawable after the color of each one of the shelves in its bookshelf have been set.
     */
    public GoalDrawable populate(Map<Shelf, TileColor> tilesColorMask) {
        this.tilesColorMask = tilesColorMask;

        return this;
    }
}
