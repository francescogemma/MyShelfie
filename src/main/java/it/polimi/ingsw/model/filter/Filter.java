package it.polimi.ingsw.model.filter;

import it.polimi.ingsw.model.Tile;

/**
 * Allows to check if a finite sequence of {@link Tile tiles} satisfies a certain criteria. From a mathematical
 * perspective every filter defines a subset of the set of all possible finite tile sequences.
 *
 * @author Cristiano Migali
 */
public interface Filter {
    /**
     * Adds a {@link Tile tile} to the sequence.
     *
     * @param tile is the tile that we are going to add to the sequence.
     * @return true iff, even by adding more tiles to the sequence, the criteria can't be satisfied without
     * removing the tile just added.
     */
    boolean add(Tile tile);

    /**
     * Removes the last {@link Tile tile} added to the sequence.
     */
    void forgetLastTile();

    /**
     * @return true iff the tile sequence satisfies the criteria.
     */
    boolean isSatisfied();

    /**
     * Removes all the tiles from the sequence.
     */
    void clear();
}
