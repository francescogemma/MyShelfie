package it.polimi.ingsw.model.filter;

import it.polimi.ingsw.model.tile.TileColor;

/**
 * Allows to check if a finite sequence of {@link TileColor tile colors} satisfies a certain criteria. From a mathematical
 * perspective every filter defines a subset of the set of all possible finite tile colors sequences.
 *
 * @author Cristiano Migali
 */
public interface Filter {
    /**
     * Adds a {@link TileColor tile color} to the sequence.
     *
     * @param tileColor is the tile color that we are going to add to the sequence.
     * @return true iff, even by adding more tiles to the sequence, the criteria can't be satisfied without
     * removing the tile just added.
     */
    boolean add(TileColor tileColor);

    /**
     * Removes the last {@link TileColor tile color} added to the sequence.
     */
    void forgetLastTile();

    /**
     * @return true iff the tile colors sequence satisfies the criteria.
     */
    boolean isSatisfied();

    /**
     * Removes all the tile colors from the sequence.
     */
    void clear();
}
