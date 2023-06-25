package it.polimi.ingsw.view.gui.controller;


import it.polimi.ingsw.model.tile.Tile;
import it.polimi.ingsw.model.tile.TileColor;
import it.polimi.ingsw.model.tile.TileVersion;
import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Utility class to easily retrieve images without needing to constantly reference a directory URL. It's a singleton helper
 * object, and contains a variety of methods, divided for image category. (tiles, goals, ...)
 */
public class ImageProvider {
    /**
     * Used for class synchronization and concurrency checks.
     */
    private static final Object lock = new Object();

    /**
     * The single instance of this class, stored as a constant class variable to implement the singleton pattern.
     * This attribute should be accessed to get the main object, without using any constructors, through the getInstance
     * method.
     */
    private static final ImageProvider INSTANCE;

    /**
     * A map containing all required images, indexed with a string consisting in that image's path URL.
     */
    private final Map<String, Image> images;

    /**
     * Class constructor that initializes the hashmap.
     */
    private ImageProvider() {
        images = new HashMap<>();
    }

    /**
     * Use this method as if it were a constructor. It gets the singleton instance of this class.
     * @return this class' instance.
     */
    public static ImageProvider getInstance() {
        return INSTANCE;
    }

    static {
        synchronized (lock) {
            INSTANCE = new ImageProvider();

            for (int i = 0; i < 12; i++) {
                INSTANCE.images.put(personalGoalToUrl(i), new Image(personalGoalToUrl(i)));
            }

            for (int i = 0; i < 12; i++) {
                INSTANCE.images.put(commonGoalToUrl(i), new Image(commonGoalToUrl(i)));
            }

            for (Tile t: Tile.getTiles()) {
                if (t.getColor() != TileColor.EMPTY) {
                    INSTANCE.images.put(tileToUrl(t), new Image(tileToUrl(t)));
                }
            }
        }
    }

    /**
     * Returns a specific personal goal's image, given its index.
     * @param index is the requested personal goal's image's index.
     * @return an image representing a personal goal with that index.
     */
    final Image getPersonalGoal (int index) {
        synchronized (lock) {
            assert index >= 0 && index < 12;
            assert images.containsKey(personalGoalToUrl(index));

            return images.get(personalGoalToUrl(index));
        }
    }

    /**
     * Returns a specific common goal's image, given its index.
     * @param index is the requested common goal's image's index.
     * @return an image representing a common goal with that index.
     */
    final Image getCommonGoal (int index) {
        synchronized (lock) {
            assert index >= 0 && index < 12;
            assert images.containsKey(commonGoalToUrl(index));

            return images.get(commonGoalToUrl(index));
        }
    }

    /**
     * Returns a tile's image, given its relative tile object.
     * @param tile is the tile for which we need an appropriate image.
     * @return an image representing the given tile.
     */
    final Image getTile(Tile tile) {
        synchronized (lock) {
            Objects.requireNonNull(tile);
            assert images.containsKey(tileToUrl(tile));

            return images.get(tileToUrl(tile));
        }
    }

    /**
     * Utility function to transform a personal goal index to a full path URL.
     * @param index is the requested personal goal's numerical index.
     * @return the full path to that personal goal.
     */
    private static String personalGoalToUrl(int index) {
        if (index > 0) return "/personal goal cards/Personal_Goals" + (index + 1) + ".png";
        return "/personal goal cards/Personal_Goals.png";
    }

    /**
     * Utility function to transform a common goal index to a full path URL.
     * @param index is the requested common goal's numerical index.
     * @return the full path to that common goal.
     */
    private static String commonGoalToUrl(int index) {
        return "/common goal cards/%s.jpg".formatted(index + 1);
    }

    /**
     * Utility function to transform a tile to its full path URL.
     * @param tile is the tile for which we need its image's path URL.
     * @return the full path to that tile's image.
     */
    private static String tileToUrl(Tile tile) {
        if(tile == Tile.getInstance(TileColor.GREEN, TileVersion.FIRST)) {
            return "/item tiles/Gatti1.1.png";
        } else if(tile == Tile.getInstance(TileColor.GREEN, TileVersion.SECOND)) {
            return "/item tiles/Gatti1.2.png";
        } else if(tile == Tile.getInstance(TileColor.GREEN, TileVersion.THIRD)) {
            return "/item tiles/Gatti1.3.png";
        } else if(tile == Tile.getInstance(TileColor.BLUE, TileVersion.FIRST)) {
            return "/item tiles/Cornici1.1.png";
        } else if(tile == Tile.getInstance(TileColor.BLUE, TileVersion.SECOND)) {
            return "/item tiles/Cornici1.2.png";
        } else if(tile == Tile.getInstance(TileColor.BLUE, TileVersion.THIRD)) {
            return "/item tiles/Cornici1.3.png";
        } else if(tile == Tile.getInstance(TileColor.MAGENTA, TileVersion.FIRST)) {
            return "/item tiles/Piante1.1.png";
        } else if(tile == Tile.getInstance(TileColor.MAGENTA, TileVersion.SECOND)) {
            return "/item tiles/Piante1.2.png";
        } else if(tile == Tile.getInstance(TileColor.MAGENTA, TileVersion.THIRD)) {
            return "/item tiles/Piante1.3.png";
        } else if(tile == Tile.getInstance(TileColor.CYAN, TileVersion.FIRST)) {
            return "/item tiles/Trofei1.1.png";
        } else if(tile == Tile.getInstance(TileColor.CYAN, TileVersion.SECOND)) {
            return "/item tiles/Trofei1.2.png";
        } else if(tile == Tile.getInstance(TileColor.CYAN, TileVersion.THIRD)) {
            return "/item tiles/Trofei1.3.png";
        } else if(tile == Tile.getInstance(TileColor.YELLOW, TileVersion.FIRST)) {
            return "/item tiles/Giochi1.1.png";
        } else if(tile == Tile.getInstance(TileColor.YELLOW, TileVersion.SECOND)) {
            return "/item tiles/Giochi1.2.png";
        } else if(tile == Tile.getInstance(TileColor.YELLOW, TileVersion.THIRD)) {
            return "/item tiles/Giochi1.3.png";
        } else if(tile == Tile.getInstance(TileColor.WHITE, TileVersion.FIRST)) {
            return "/item tiles/Libri1.1.png";
        } else if(tile == Tile.getInstance(TileColor.WHITE, TileVersion.SECOND)) {
            return "/item tiles/Libri1.2.png";
        } else if(tile == Tile.getInstance(TileColor.WHITE, TileVersion.THIRD)) {
            return "/item tiles/Libri1.3.png";
        } else {
            throw new IllegalArgumentException("Tile not found");
        }
    }
}
