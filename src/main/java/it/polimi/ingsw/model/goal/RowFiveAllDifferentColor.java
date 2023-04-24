package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.bookshelf.Shape;
import it.polimi.ingsw.model.bookshelf.Shelf;
import it.polimi.ingsw.model.evaluator.AtLeastEvaluator;
import it.polimi.ingsw.model.fetcher.ShapeFetcher;
import it.polimi.ingsw.model.filter.NumDifferentColorFilter;
import it.polimi.ingsw.model.tile.TileColor;

import java.util.Map;

import static java.util.Map.entry;

/**
 * Two rows, each consisting of 5 different types of tiles.
 * @author Giacomo Groppi
 * */
public class RowFiveAllDifferentColor extends CommonGoal{
    protected RowFiveAllDifferentColor() {
        super(
                new ShapeFetcher(Shape.getRow(5)),
                new NumDifferentColorFilter(5, 5),
                new AtLeastEvaluator(2, bookshelfMask -> bookshelfMask.getShelves().size() >= 2),
                "Two rows,\n each consisting of\n 5 different types\n of tiles.",
                Map.ofEntries(
                        entry(Shelf.getInstance(1, 0), TileColor.GREEN),
                        entry(Shelf.getInstance(1, 1), TileColor.BLUE),
                        entry(Shelf.getInstance(1, 2), TileColor.YELLOW),
                        entry(Shelf.getInstance(1, 3), TileColor.CYAN),
                        entry(Shelf.getInstance(1, 4), TileColor.MAGENTA),
                        entry(Shelf.getInstance(3, 0), TileColor.BLUE),
                        entry(Shelf.getInstance(3, 1), TileColor.GREEN),
                        entry(Shelf.getInstance(3, 2), TileColor.CYAN),
                        entry(Shelf.getInstance(3, 3), TileColor.YELLOW),
                        entry(Shelf.getInstance(3, 4), TileColor.WHITE)
                )
        );
    }
}
