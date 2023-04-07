package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.Tile;
import it.polimi.ingsw.model.board.IllegalExtractionException;
import it.polimi.ingsw.model.board.SelectionFullException;
import it.polimi.ingsw.utils.Coordinate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public abstract class SelectTileState extends GameState{
    protected final Player currentPlayer;
    protected boolean selectNext;

    protected SelectTileState(GameData data, Player nextPlayer) {
        super(data);
        this.currentPlayer = nextPlayer;
        this.selectNext = false;
    }

    /**
     * This function does not check if the game is ended.
     * @return The player after currentPlayer
     * */
    protected Player getNextPlayer () {
        int currentIndex = 0;

        try {
            currentIndex = this.indexOf(this.currentPlayer);
        } catch (PlayerNotInGameException ignored) {
            assert false: "nextPlayer is not a player in this game :(";
        }

        return this.gameData.players.get(
                (currentIndex + 1) % 4
        ).getKey();
    }

    public Collection<Coordinate> getSelectableTile () throws IllegalFlowException {
        return this
                .gameData
                .board
                .getSelectableCoordinate();
    }

    @Override
    public void selectTile(Player player, Collection<Coordinate> positions, int col) throws IllegalFlowException, IllegalExtractionException, SelectionFullException {
        if (player == null)
            throw new NullPointerException();

        assert !selectNext: "This function has already been called";

        if (this.currentPlayer != player) {
            throw new IllegalFlowException();
        }

        for (Coordinate pos : positions) {
            try {
                this.gameData.board.selectTile(pos);
            } catch (SelectionFullException | IllegalExtractionException e) {
                this.gameData.board.forgetSelection();
                throw e;
            }
        }

        // TODO remove new ArrayList<>() and pass a collection
        ArrayList<Tile> tileSelected = new ArrayList<>(this.gameData.board.getSelectedTiles());

        try {
            currentPlayer.getBookshelf().insertTiles(tileSelected, col);
        } catch (RuntimeException e) {
            // TODO change RuntimeException with the correct throw
            this.gameData.board.forgetSelection();
            throw e;
        }

        this.gameData.board.draw();

        selectNext = true;
    }

    @Override
    public Player getCurrentPlayer() {
        return this.currentPlayer;
    }
}
