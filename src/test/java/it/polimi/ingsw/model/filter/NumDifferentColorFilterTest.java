package it.polimi.ingsw.model.filter;

import it.polimi.ingsw.model.Tile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NumDifferentColorFilterTest {
    @Test
    @DisplayName("Try to construct a filter with negative min colors, should throw exception")
    void constructor_negativeMinColors_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new NumDifferentColorFilter(-1, 1);
        });
    }

    @Test
    @DisplayName("Try to construct a filter with non-positive max colors, should throw exception")
    void constructor_nonPositiveMaxColors_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new NumDifferentColorFilter(0, 0);
        });
    }

    private NumDifferentColorFilter filter;

    @BeforeEach
    void setUp() {
        filter = new NumDifferentColorFilter(2, 3);
    }

    @Test
    @DisplayName("Add empty tile to filter")
    void add_emptyTile_notSatisfied() {;
        Assertions.assertTrue(filter.add(Tile.EMPTY));

        Assertions.assertFalse(filter.isSatisfied());
    }

    @Test
    @DisplayName("Add correct tiles which satisfy the filter")
    void add_correctTiles_isSatisfied() {
        Assertions.assertFalse(filter.add(Tile.GREEN));
        Assertions.assertFalse(filter.add(Tile.BLUE));
        Assertions.assertFalse(filter.add(Tile.GREEN));
        Assertions.assertFalse(filter.add(Tile.GREEN));
        Assertions.assertFalse(filter.add(Tile.YELLOW));

        Assertions.assertTrue(filter.isSatisfied());
    }

    @Test
    @DisplayName("Add not enough tiles to filter")
    void add_notEnoughTiles_notSatisfied() {
        Assertions.assertFalse(filter.add(Tile.WHITE));
        Assertions.assertFalse(filter.add(Tile.WHITE));
        Assertions.assertFalse(filter.add(Tile.WHITE));

        Assertions.assertFalse(filter.isSatisfied());
    }

    @Test
    @DisplayName("Add too many tiles to the filter")
    void add_tooManyTiles_notSatisfied() {
        Assertions.assertFalse(filter.add(Tile.CYAN));
        Assertions.assertFalse(filter.add(Tile.MAGENTA));
        Assertions.assertFalse(filter.add(Tile.BLUE));
        Assertions.assertTrue(filter.add(Tile.WHITE));

        Assertions.assertFalse(filter.isSatisfied());
    }

    @Test
    @DisplayName("Add too many tiles to the filter and then forget last")
    void addAndForgetLastTile_tooManyTiles_isSatisfied() {
        Assertions.assertFalse(filter.add(Tile.YELLOW));
        Assertions.assertFalse(filter.add(Tile.CYAN));
        Assertions.assertFalse(filter.add(Tile.GREEN));
        Assertions.assertTrue(filter.add(Tile.MAGENTA));

        filter.forgetLastTile();

        Assertions.assertTrue(filter.isSatisfied());
    }

    @Test
    @DisplayName("Add correct tiles and then clear")
    void addAndClear_correctTiles_notSatisfied() {
        Assertions.assertFalse(filter.add(Tile.WHITE));
        Assertions.assertFalse(filter.add(Tile.BLUE));

        Assertions.assertTrue(filter.isSatisfied());

        filter.clear();

        Assertions.assertFalse(filter.isSatisfied());
    }
}