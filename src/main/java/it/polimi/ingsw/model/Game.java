package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class Game {
    private String name;
    private List<Pair<Player, Boolean>> players;
    private final Board board;
    private SharedObjective []sharedObjective;

    private Optional<Player> winner;
    private Optional<Player> currentPlayer;

    public Game(String name) {
        board = new Board();
        sharedObjective = new SharedObjective[2];
        players = new ArrayList<>();
        winner = Optional.empty();
    }

    Player getStartingPlayer() {
        return this.players.get(0).getKey();
    }

    boolean isOver () {
        return winner.isPresent();
    }

    public void addPlayer(Player player) {
        if (player == null)
            throw new NullPointerException();

        if (this.players.size() >= 4)
            throw new RuntimeException("Player are already 4");

        this.players.add(new Pair<>(player, false));
    }

    private int indexOf(final Player player) {
        int i;
        for (i = 0; i < this.players.size(); i++) {
            if (players.get(i).getKey().equals(player))
                return i;
        }
        return -1;
    }

    public boolean isConnected(Player player) {
        final int index = this.indexOf(player);
        return this.players.get(index).getValue();
    }

    public boolean disconnected(final Player player) {
        return this.players.get(indexOf(player)).getValue();
    }

    public void connect(Player player) {

        final int index = this.indexOf(player);
        this.players.get(index).setValue(true);
    }

    public Optional<Player> getCurrentPlayer() {
        return this.currentPlayer;
    }
}
