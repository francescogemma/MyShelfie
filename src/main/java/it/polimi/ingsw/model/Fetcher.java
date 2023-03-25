package it.polimi.ingsw.model;

/**
 * Allows the extraction of {@link LibraryMask library masks} from a library, according to a certain
 * criteria. The extraction happens one {@link Shelf shelf} at the time.
 * A fetcher is cyclic, that is, after it has extracted all the library masks following the criteria, it starts again
 * from the first. The library masks get extracted always in the same order.
 *
 * <p>We say that the fetcher is in its <b>equilibrium state</b> if:
 * <ul>
 *     <li>
 *         {@link Fetcher#next()} has never been invoked since the creation of the fetcher.
 *     </li>
 *     <li>
 *         the fetcher has extracted the last shelf of the last library mask that it had to extract
 *         following the criteria and, after that extraction, {@link Fetcher#lastShelf()} has been invoked
 *         (in order to check that the extracted shelf was the last) or a call to {@link Fetcher#canFix()}
 *         returned false after the extraction of a shelf belonging to the last extractable library mask.
 *     </li>
 * </ul>
 * </p>
 *
 * @author Cristiano Migali
 */
public interface Fetcher {
    /**
     * Extracts the next {@link Shelf shelf}. Remember to invoke {@link Fetcher#lastShelf()} or
     * {@link Fetcher#canFix()} after each call to next.
     *
     * @return the next {@link Shelf shelf} which belongs to the {@link LibraryMask library mask} that the
     * fetcher is extracting.
     */
    Shelf next();

    /**
     * Checks if the last extracted {@link Shelf shelf} is the last one of the {@link LibraryMask library mask} that
     * the fetcher is extracting. Remember to call {@link Fetcher#next} before invoking this method.
     *
     * @return true iff the last extracted {@link Shelf shelf}, returned by the method {@link Fetcher#next}, is
     * also the last shelf of the library mask that we are constructing.
     */
    boolean lastShelf();

    /**
     * Allows to check if the fetcher is in its equilibrium state. Note that, by the definition of equilibrium state,
     * a fetcher is always in its equilibrium state right after it has been created or when it has finished the previous
     * extraction. Hence, if we want to use a fetcher in a cycle until the end of the extraction, we must use a do while
     * loop instead of a while loop (where the condition is {@code !fetcher.hasFinished()}).
     * In this way we always execute {@link Fetcher#next()} before calling hasFinished (which, before the first cycle,
     * would always return true, since no call to {@link Fetcher#next()} happened before). With the call to
     * {@link Fetcher#next()} we put the fetcher off its equilibrium state, hence hasFinished will return true again
     * when the extraction has finished.
     *
     * @return true iff the fetcher is in its equilibrium state.
     *
     * @see Fetcher Definition of fetcher's equilibrium state
     */
    boolean hasFinished();

    /**
     * Checks if the fetcher can adjust the {@link LibraryMask library mask} that it is extracting in such a way that
     * it doesn't contain the last extracted {@link Shelf shelf}. Note that, after a call to canFix that returned
     * true, we should invoke {@link Fetcher#lastShelf()} since a possible adjustment is ignoring the last extracted
     * shelf and consider the library mask finished before that.
     *
     * @return true iff the fetcher can still extract a {@link LibraryMask library mask} which satisfies the criteria,
     * contains all the {@link Shelf shelves} extracted before the last one and doesn't contain the last extracted shelf.
     */
    boolean canFix();
}
