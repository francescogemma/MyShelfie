package it.polimi.ingsw.model.goal;

import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.bookshelf.BookshelfMask;
import it.polimi.ingsw.model.bookshelf.Shelf;
import it.polimi.ingsw.model.evaluator.Evaluator;
import it.polimi.ingsw.model.fetcher.Fetcher;
import it.polimi.ingsw.model.filter.Filter;
import it.polimi.ingsw.model.game.Player;

public abstract class Goal {
    private Fetcher fetcher;
    private Filter filter;
    private Evaluator evaluator;

    public Goal(Fetcher fetcher, Filter filter, Evaluator evaluator) {
        this.fetcher = fetcher;
        this.filter = filter;
        this.evaluator = evaluator;
    }

    public final void calculateAndAddPoints(Player player) {
        Bookshelf bookshelf = player.getBookshelf();

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
                if (filter.isSatisfied()) {
                    if (evaluator.add(mask)) {
                        break;
                    }
                }
                filter.clear();
                mask.clear();
            }
        } while (!fetcher.hasFinished());

        player.addPoints(evaluator.getPoints());
    }
}
