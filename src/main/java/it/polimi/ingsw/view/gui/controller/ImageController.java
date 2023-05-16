package it.polimi.ingsw.view.gui.controller;


import it.polimi.ingsw.model.tile.Tile;
import it.polimi.ingsw.model.tile.TileColor;
import it.polimi.ingsw.model.tile.TileVersion;
import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class ImageController {
    private static final ImageController INSTANCE;
    private final Map<String, Image> images;
    private ImageController() {
        images = new HashMap<>();
    }

    public static ImageController getInstance() {
        return INSTANCE;
    }

    static {
        INSTANCE = new ImageController();

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

    final Image getPersonalGoal (int index) {
        assert index >= 0 && index < 12;
        assert images.containsKey(personalGoalToUrl(index));

        return images.get(personalGoalToUrl(index));
    }

    final Image getCommonGoal (int index) {
        assert index >= 0 && index < 12;
        assert images.containsKey(commonGoalToUrl(index));

        return images.get(commonGoalToUrl(index));
    }

    final Image getTile(Tile tile) {
        Objects.requireNonNull(tile);
        assert images.containsKey(tileToUrl(tile));

        return images.get(tileToUrl(tile));
    }

    private static String personalGoalToUrl(int index) {
        if (index > 0) return "/personal goal cards/Personal_Goals" + (index + 1) + ".png";
        return "/personal goal cards/Personal_Goals.png";
    }

    private static String commonGoalToUrl(int index) {
        return "/common goal cards/%s.jpg".formatted(index + 1);
    }

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
