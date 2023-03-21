package it.polimi.ingsw.model;

import java.util.Arrays;

public class NumDifferentColorFilter implements Filter {
    private final int minColors;
    private final int maxColors;

    private int countBeforeLast;
    private final boolean[] yetAddedBeforeLast = new boolean[Tile.values().length];
    private int count;
    private final boolean[] yetAdded = new boolean[Tile.values().length];

    public NumDifferentColorFilter(int minColors, int maxColors) {
        this.minColors = minColors;
        this.maxColors = maxColors;

        clear();
    }

    @Override
    public boolean add(Tile tile) {
        countBeforeLast = count;
        for (Tile t : Tile.values()) {
            yetAddedBeforeLast[t.ordinal()] = yetAdded[t.ordinal()];
        }

        if (!yetAdded[tile.ordinal()]) {
            count++;
        }

        yetAdded[tile.ordinal()] = true;

        return count > maxColors;
    }

    @Override
    public void forgetLastTile() {
        count = countBeforeLast;
        for (Tile t : Tile.values()) {
            yetAdded[t.ordinal()] = yetAddedBeforeLast[t.ordinal()];
        }
    }

    @Override
    public boolean isSatisfied() {
        return minColors <= count && count <= maxColors;
    }

    @Override
    public void clear() {
        countBeforeLast = 0;
        count = 0;
        Arrays.fill(yetAddedBeforeLast, false);
        Arrays.fill(yetAdded, false);
    }
}
