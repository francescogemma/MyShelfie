package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.board.IllegalExtractionException;
import it.polimi.ingsw.model.board.FullSelectionException;
import it.polimi.ingsw.utils.Coordinate;

import java.util.*;

public class Game {
    // TODO: Add JavaDoc for this class

    private GameState gameState;

    public Game(String name) {
        if (name.length() == 0)
            throw new IllegalArgumentException("String is empty");

        gameState = new AddPlayerState(name);
    }

    Player getStartingPlayer() {
        return this.gameState.getStartingPlayer();
    }

    boolean isOver () {
        return this.gameState.isOver();
    }

    public void addPlayer(Player player) throws IllegalFlowException {
        this.gameState.addPlayer(player);
        this.gameState = gameState.getNextState();
    }

    public void selectTile(final Player player, Collection<Coordinate> position, int col) throws IllegalFlowException, FullSelectionException, IllegalExtractionException {
        this.gameState.selectTile(player, position, col);
        gameState = gameState.getNextState();
    }

    public void startGame () throws IllegalFlowException {
        gameState.startGame();
        gameState = gameState.getNextState();
    }

    public boolean isConnected(Player player) throws PlayerNotInGameException {
        return this.gameState.isConnected(player);
    }

    public void disconnect(final Player player) throws PlayerNotInGameException {
        this.gameState.disconnect(player);
    }

    /**
     * Function used to reconnect a player, already in the game, who was disconnected from the network.
     * @throws PlayerNotInGameException iff player is not in this game
     * */
    public void connect(Player player) throws PlayerNotInGameException {
        this.gameState.connect(player);
    }

    /**
     * @return Name of the game.
     */
    public String getName() {
        return this.gameState.getName();
    }

    public Player getCurrentPlayer() throws IllegalFlowException {
        Player p = this.gameState.getCurrentPlayer();
        gameState = gameState.getNextState();
        return p;
    }

    public Player getWinner () throws IllegalFlowException {
        return gameState.getWinner();
    }

    public Collection<Coordinate> getSelectableTile () throws IllegalFlowException {
        return gameState.getSelectableTile();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Game game = (Game) o;
        return this.gameState.equals(game.gameState);
    }
}
