package it.polimi.ingsw.model.tile;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;

class TileTest {
    private static List<Arguments> tileColorTileVersionProvider() {
        List<Arguments> tileColorsTileVersions = new ArrayList<>();

        tileColorsTileVersions.add(Arguments.arguments(TileColor.EMPTY, TileVersion.FIRST));

        for (TileColor tileColor : TileColor.values()) {
            if (tileColor != TileColor.EMPTY) {
                for (TileVersion tileVersion : TileVersion.values()) {
                    tileColorsTileVersions.add(Arguments.arguments(tileColor, tileVersion));
                }
            }
        }

        return tileColorsTileVersions;
    }

    @ParameterizedTest
    @DisplayName("Get all instances of tiles")
    @MethodSource("tileColorTileVersionProvider")
    void getInstance_correctInput_correctOutput(TileColor tileColor, TileVersion tileVersion) {
        Tile tile = Tile.getInstance(tileColor, tileVersion);
        Assertions.assertEquals(tileColor, tile.getColor());
        Assertions.assertEquals(tileVersion, tile.getVersion());
    }

    @Test
    @DisplayName("Trying to get empty tile with version two, should throw exception")
    void getInstance_emptyTileVersionTwo_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
                Tile.getInstance(TileColor.EMPTY, TileVersion.SECOND);
            });
    }

    @Test
    @DisplayName("Assert that nonEmptyTileToIndex is the inverse of indexToNonEmptyTile")
    void  indexToNonEmptyTIle_nonEmptyTileToIndex_correctOutput() {
        for (TileColor tileColor : TileColor.values()) {
            if (tileColor != TileColor.EMPTY) {
                for (TileVersion tileVersion : TileVersion.values()) {
                    Tile tile = Tile.getInstance(tileColor, tileVersion);

                    Assertions.assertEquals(tile,
                        Tile.indexToNonEmptyTile(Tile.nonEmptyTileToIndex(tile)));
                }
            }
        }
    }

    @Test
    @DisplayName("Try to get the index of empty tile, should throw exception")
    void nonEmptyTileToIndex_emptyTile_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Tile.nonEmptyTileToIndex(Tile.getInstance(TileColor.EMPTY, TileVersion.FIRST));
        });
    }

    @Test
    @DisplayName("Try to get a tile with negative index, should throw exception")
    void indexToNonEmptyTile_negativeIndex_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Tile.indexToNonEmptyTile(-1);
        });
    }

    @Test
    @DisplayName("Try to get a tile with too big index, should throw exception")
    void indexToNonEmptyTile_tooBigIndex_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
           Tile.indexToNonEmptyTile(Tile.NUM_OF_DIFFERENT_NON_EMPTY_TILES + 1);
        });
    }
}
