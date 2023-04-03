package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.bookshelf.BookshelfMask;
import it.polimi.ingsw.model.bookshelf.BookshelfMaskSet;
import it.polimi.ingsw.model.bookshelf.Shelf;
import it.polimi.ingsw.model.evaluator.Evaluator;
import it.polimi.ingsw.model.fetcher.Fetcher;
import it.polimi.ingsw.model.filter.Filter;

public abstract class Goal {
    private final Fetcher fetcher;
    private final Filter filter;
    private final Evaluator evaluator;
    private BookshelfMaskSet pointMasks;

    protected Goal(Fetcher fetcher, Filter filter, Evaluator evaluator) {
        this.fetcher = fetcher;
        this.filter = filter;
        this.evaluator = evaluator;
        pointMasks = new BookshelfMaskSet((a, b) -> true);
    }

    public final int calculatePoints(Bookshelf bookshelf) {
        BookshelfMask mask = new BookshelfMask(bookshelf);

        do {
            Shelf next = fetcher.next();
            if (filter.add(bookshelf.get(next))) {
                if (fetcher.canFix()) {
                    filter.forgetLastTile();
                } else {
                    filter.clear();
                    mask.clear();
                    continue;
                }
            } else mask.add(next);

            if (fetcher.lastShelf()) {
                if (filter.isSatisfied() && evaluator.add(mask)) {
                    fetcher.clear();
                    break;
                }
                filter.clear();
                mask.clear();
            }
        } while (!fetcher.hasFinished());

        filter.clear();

        try {
            pointMasks = evaluator.getPointMasks();
            return evaluator.getPoints();
        } finally {
            evaluator.clear();
        }
    }

    public final BookshelfMaskSet getPointMasks() {
        return pointMasks;
    }
}
