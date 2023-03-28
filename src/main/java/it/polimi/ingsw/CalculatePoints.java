package it.polimi.ingsw;

import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.bookshelf.BookshelfMask;
import it.polimi.ingsw.model.bookshelf.Shelf;
import it.polimi.ingsw.model.evaluator.Evaluator;
import it.polimi.ingsw.model.fetcher.Fetcher;
import it.polimi.ingsw.model.filter.Filter;

public class CalculatePoints {
    public static void calculatePoints(Bookshelf bookshelf, Fetcher fetcher, Filter filter) {
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
                    System.out.println(mask);
                }
                filter.clear();
                mask.clear();
            }
        } while (!fetcher.hasFinished());
    }

    public static void calculatePointsWithEvaluator(Bookshelf bookshelf, Fetcher fetcher, Filter filter,
                                                    Evaluator evaluator) {
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
                    System.out.println(mask);
                    if (evaluator.add(mask)) {
                        break;
                    }
                }
                filter.clear();
                mask.clear();
            }
        } while (!fetcher.hasFinished());

        System.out.println("Points: " + evaluator.getPoints());
    }
}
