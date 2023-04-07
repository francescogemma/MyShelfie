package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.IllegalExtractionException;
import it.polimi.ingsw.model.board.SelectionFullException;
import it.polimi.ingsw.utils.Coordinate;
import it.polimi.ingsw.utils.Pair;
import it.polimi.ingsw.model.goal.CommonGoal;
import it.polimi.ingsw.model.game.GameState;

import java.util.*;

public class Game {
    private GameState gameState;

    public Game(String name) {
        if (name == null)
            throw new NullPointerException();

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

    public void selectTile(final Player player, Collection<Coordinate> position, int col) throws IllegalFlowException, SelectionFullException, IllegalExtractionException {
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
     * In caso un utente di disconnetti dalla partita la funzione viene
     * chiamata quando l'utente si riconnetta alla partita.
     * */
    public void connect(Player player) throws PlayerNotInGameException {
        this.gameState.connect(player);
    }

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
