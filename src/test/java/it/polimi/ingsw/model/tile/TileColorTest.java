package it.polimi.ingsw.model.tile;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TileColorTest {

    @Test
    @DisplayName("Check that the tile color with index 0 is the empty tile color")
    void indexToTileColor_zero_emptyTile() {
        Assertions.assertEquals(TileColor.EMPTY, TileColor.indexToTileColor(0));
    }

    @Test
    @DisplayName("Assert that indexToTileColor is the inverse of tileColorToIndex")
    void indexToTileColor_tileToIndexColor_correctOutput() {
        for (TileColor tileColor : TileColor.values()) {
            Assertions.assertEquals(tileColor, TileColor.indexToTileColor(TileColor.tileColorToIndex(tileColor)));
        }
    }
}
