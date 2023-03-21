package it.polimi.ingsw.model;

import java.util.ArrayList;

public class UnionFetcher implements Fetcher {
    private final ArrayList<Fetcher> fetchers;
    private int currentFetcherIndex = 0;

    public UnionFetcher(ArrayList<Fetcher> fetchers) {
        if (fetchers == null) {
            throw new NullPointerException("fetchers must be non-null when creating a UnionFetcher");
        }

        if (fetchers.size() < 2) {
            throw new IllegalArgumentException("at least 2 fetchers are required to construct a UnionFetcher");
        }

        this.fetchers = new ArrayList<>(fetchers);
    }

    @Override
    public Shelf next() {
        return fetchers.get(currentFetcherIndex).next();
    }

    @Override
    public boolean lastShelf() {
        return fetchers.get(currentFetcherIndex).lastShelf();
    }

    @Override
    public boolean hasFinished() {
        if (!fetchers.get(currentFetcherIndex).hasFinished()) {
            return false;
        }

        currentFetcherIndex++;
        if (currentFetcherIndex == fetchers.size()) {
            currentFetcherIndex = 0;
        }

        return currentFetcherIndex == 0;
    }

    @Override
    public boolean canFix() {
        return fetchers.get(currentFetcherIndex).canFix();
    }
}
