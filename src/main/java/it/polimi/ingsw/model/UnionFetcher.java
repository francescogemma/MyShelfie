package it.polimi.ingsw.model;

import java.util.ArrayList;

/**
 * Allows to extract all the {@link BookshelfMask bookshelf masks} that can be extracted by the first
 * {@link Fetcher fetcher} in a given list, then all the ones that can be extracted by the second fetcher in the list,
 * and so on, until the last fetcher.
 *
 * @author Cristiano Migali
 */
public class UnionFetcher implements Fetcher {
    /**
     * The list of fetchers.
     */
    private final ArrayList<Fetcher> fetchers;

    /**
     * The current fetcher in the list from which we are performing the extraction.
     */
    private int currentFetcherIndex = 0;

    /**
     * Constructor of the class.
     *
     * @param fetchers is the list of fetchers from which we are going to perform the extractions.
     * @throws NullPointerException if fetchers is null.
     * @throws IllegalArgumentException if the list of fetchers has less than 2 elements.
     */
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

    /**
     * Sets up the union fetcher to perform the extraction from the next fetcher in the list.
     * When we end the extraction from the last fetcher, this method allows to cycle back to the first one.
     */
    private void nextFetcher() {
        currentFetcherIndex++;

        if (currentFetcherIndex == fetchers.size()) {
            currentFetcherIndex = 0;
        }
    }


    @Override
    public boolean lastShelf() {
        if (fetchers.get(currentFetcherIndex).lastShelf()) {
            if (fetchers.get(currentFetcherIndex).hasFinished()) {
                nextFetcher();
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean hasFinished() {
        return currentFetcherIndex == 0 && fetchers.get(currentFetcherIndex).hasFinished();
    }

    @Override
    public boolean canFix() {
        if (!fetchers.get(currentFetcherIndex).canFix()) {
            if (fetchers.get(currentFetcherIndex).hasFinished()) {
                nextFetcher();
            }

            return false;
        }

        return true;
    }
}
