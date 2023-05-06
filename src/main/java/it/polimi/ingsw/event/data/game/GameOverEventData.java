package it.polimi.ingsw.event.data.game;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;
import it.polimi.ingsw.model.bookshelf.BookshelfMaskSet;
import it.polimi.ingsw.model.game.Player;
import it.polimi.ingsw.utils.Pair;
import it.polimi.ingsw.model.goal.AdjacencyGoal;
import it.polimi.ingsw.model.goal.PersonalGoal;
import it.polimi.ingsw.model.game.Game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Event to notify the client that the game has ended.
 * The event is triggered if the game ends regularly, i.e., by completing a player's bookshelf,
 * or if the game is paused due to disconnections.
 * @author Giacomo Groppi
 * */
public record GameOverEventData(List<Player> winners,
                                List<Player> allPlayers,
                                List<Pair<Integer, BookshelfMaskSet>> personalGoal,
                                List<Pair<Integer, BookshelfMaskSet>> adjacencyGoal) implements EventData {
    public static final String ID = "GAME_OVER";

    /**
     * Constructs a new event to mark the end of the game.
     * @param winners the list of winners of the game.
     * @param allPlayers the list of all players who participated in the game.
     * @param adjacencyGoal a list of the points obtained by each player for completing the {@link AdjacencyGoal adjacency goal}.
     * @param personalGoal a list of the points obtained by each player for completing the {@link PersonalGoal personal goal}.
     *
     * @throws NullPointerException iff
     * <ul>
     *     <li> winners is null </li>
     *     <li> allPlayers is null </li>
     *     <li> personalGoal is null </li>
     *     <li> adjacencyGoal is null </li>
     * </ul>
     *
     * @see Game
     * @see Player
     * @see AdjacencyGoal
     * @see PersonalGoal
     */
    public GameOverEventData(List<Player> winners, List<Player> allPlayers, List<Pair<Integer, BookshelfMaskSet>> personalGoal, List<Pair<Integer, BookshelfMaskSet>> adjacencyGoal) {
        assert personalGoal.size() == adjacencyGoal.size();
        assert personalGoal.size() == allPlayers.size();
        assert winners.size() <= allPlayers.size();

        assert !personalGoal.contains(null);
        assert !adjacencyGoal.contains(null);
        assert !winners.contains(null);
        assert !allPlayers.contains(null);

        Objects.requireNonNull(winners);
        Objects.requireNonNull(allPlayers);
        Objects.requireNonNull(personalGoal);
        Objects.requireNonNull(adjacencyGoal);

        this.winners = new ArrayList<>(winners);
        this.allPlayers = new ArrayList<>(allPlayers);
        this.personalGoal = new ArrayList<>(personalGoal);
        this.adjacencyGoal = new ArrayList<>(adjacencyGoal);
    }

    @Override
    public List<Pair<Integer, BookshelfMaskSet>> personalGoal() {
        return Collections.unmodifiableList(personalGoal);
    }

    @Override
    public List<Player> winners() {
        return Collections.unmodifiableList(this.winners);
    }

    public static CastEventReceiver<GameOverEventData> castEventReceiver(EventReceiver<EventData> receiver) {
        return new CastEventReceiver<>(ID, receiver);
    }

    public static <T extends EventData> Requester<GameOverEventData, T> requester(EventTransmitter transmitter,
                                                                                  EventReceiver<EventData> receiver,
                                                                                  Object responsesLock) {
        return new Requester<>(ID, transmitter, receiver, responsesLock);
    }

    public static <T extends EventData> Responder<GameOverEventData, T> responder(EventTransmitter transmitter,
                                                                                  EventReceiver<EventData> receiver,
                                                                                  Function<GameOverEventData, T> response) {
        return new Responder<>(ID, transmitter, receiver, response);
    }

    /**
     * This method returns a list of usernames of all the winners of the game.
     *
     * @return a new list of all the usernames of the winners of the game.
     */
    public List<String> getWinnersUsername() {
        return this.winners
                .stream()
                .map(Player::getUsername)
                .toList();
    }

    @Override
    public String getId() {
        return ID;
    }
}
