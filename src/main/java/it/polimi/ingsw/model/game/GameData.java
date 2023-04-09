package it.polimi.ingsw.model.game;

import it.polimi.ingsw.model.bag.Bag;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.utils.Pair;
import jdk.jshell.spi.ExecutionControl;
import it.polimi.ingsw.model.goal.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// TODO: Translate this JavaDoc to english

/**
 * Nella funzione vengono definiti tutti i dati e i getter disponibili dalla classe Game
 * */
public class GameData {
    protected static final int INDEX_FIRST_PLAYER = 0;
    protected final String name;
    protected List<Pair<Player, Boolean>> players;
    protected Bag bag;
    protected Optional<Player> winner;
    protected CommonGoal []commonGoal;

    protected Board board;

    public GameData(String name) {
        this.name = name;
        commonGoal = new CommonGoal[2];
        winner = Optional.empty();
        bag = new Bag();
        players = new ArrayList<>();
        board = new Board();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj.getClass() != this.getClass())
            return false;
        return ((GameData) obj).name.equals(this.name);
    }
}
