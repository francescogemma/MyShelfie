package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.utils.Pair;
import it.polimi.ingsw.model.goal.CommonGoal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Game {
    private final String name;
    private List<Pair<Player, Boolean>> players;
    private final Board board;
    private CommonGoal []commonGoal;
    private static final int INDEX_FIRST_PLAYER = 0;
    private Optional<Player> winner;
    private Optional<Player> currentPlayer;

    public Game(String name) {
        if (name == null)
            throw new NullPointerException();

        if (name.length() == 0)
            throw new IllegalArgumentException("String is empty");

        this.name = name;
        board = new Board();
        commonGoal = new CommonGoal[2];
        players = new ArrayList<>();
        winner = Optional.empty();
    }

    Player getStartingPlayer() {
        return this.players.get(INDEX_FIRST_PLAYER).getKey();
    }

    boolean isOver () {
        return winner.isPresent();
    }

    public void addPlayer(Player player) {
        if (player == null)
            throw new NullPointerException();

        if (this.players.size() >= 4)
            throw new RuntimeException("Player are already 4");

        this.players.add(new Pair<>(player, true));
        this.connect(player);
    }

    private int indexOf(final Player player) {
        int i;
        for (i = 0; i < this.players.size(); i++) {
            if (players.get(i).getKey().equals(player))
                return i;
        }
        return -1;
    }

    public boolean isConnected(Player player) throws PlayerNotInGameException {
        final int index = this.indexOf(player);

        if (index == -1)
            throw new PlayerNotInGameException();

        return this.players.get(index).getValue();
    }

    public boolean disconnected(final Player player) throws PlayerNotInGameException {
        return !isConnected(player);
    }

    public void connect(Player player) {
        final int index = this.indexOf(player);
        this.players.get(index).setValue(true);
    }

    public String getName() {
        return this.name;
    }

    public Optional<Player> getCurrentPlayer() {
        return this.currentPlayer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Game game = (Game) o;

        return Objects.equals(name, game.name);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
