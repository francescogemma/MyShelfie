package it.polimi.ingsw.model.game;

import it.polimi.ingsw.event.LocalEventTransceiver;
import it.polimi.ingsw.event.data.game.*;
import it.polimi.ingsw.model.board.FullSelectionException;
import it.polimi.ingsw.model.board.IllegalExtractionException;
import it.polimi.ingsw.model.goal.AdjacencyGoal;
import it.polimi.ingsw.model.goal.CommonGoal;
import it.polimi.ingsw.model.goal.Goal;
import it.polimi.ingsw.model.goal.PersonalGoal;
import it.polimi.ingsw.model.tile.Tile;
import it.polimi.ingsw.utils.Coordinate;

import java.util.*;

public class Game extends GameView{
    private transient LocalEventTransceiver transceiver;

    private final List<Player> players;
    protected final transient List<Integer> personalGoalIndexes;
    protected transient int personalGoalIndex;

    /**
     * Creates a new game with the given name.
     * @param name the name of the game
     * @throws IllegalArgumentException iff the name is empty
     * @throws NullPointerException iff name is null
     */
    public Game(String name, String username) {
        super(name, username);

        this.personalGoalIndexes = new ArrayList<>(Arrays.asList( 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 ));
        this.personalGoalIndex = 0;
        this.players = new ArrayList<>();
        Collections.shuffle(this.personalGoalIndexes);
    }

    public void setTransceiver (LocalEventTransceiver transceiver) {
        assert this.transceiver == null;

        if (transceiver == null)
            throw new NullPointerException();

        this.transceiver = transceiver;
    }

    /**
     * Adds a player to the game with the given username or reconnects that player.
     * @param username the username of the player to add
     * @throws PlayerAlreadyInGameException if the player is already in the game
     * @throws NullPointerException
     *  <ul>
     *      <li> username is null </li>
     *      <li> a player with the name "username" already exists </li>
     *  </ul>
     * @throws IllegalFlowException
     *  <ul>
     *      <li> if the game has already started </li>
     *      <li> if the game is already 4 players </li>
     *  </ul>
     * @return {@link Player player} added
     */
    public Player addPlayer(String username) throws IllegalFlowException, PlayerAlreadyInGameException {
        if (username == null || username.length() == 0)
            throw new NullPointerException("username is null or has length 0");

        // Allows for reconnection of disconnected players
        for (Player otherPlayer : players) {
            if (otherPlayer.getUsername().equals(username)) {
                if (otherPlayer.isConnected()) {
                    throw new PlayerAlreadyInGameException(username);
                }

                otherPlayer.setConnectionState(true);
                this.transceiver.broadcast(new PlayerHasJoinEventData(otherPlayer.getUsername()));
                return otherPlayer;
            }
        }

        if (isStarted) {
            throw new IllegalFlowException("You can't add players when the game has already started");
        }

        if (players.size() == 4) {
            throw new IllegalFlowException("You can't have more than 4 players in the same game");
        }

        Player player = new Player(username);

        player.setPersonalGoal(PersonalGoal.fromIndex(personalGoalIndexes.get(personalGoalIndex)));
        personalGoalIndex++;
        players.add(player);
        super.addPlayerView(player);

        this.transceiver.broadcast(new PlayerHasJoinEventData(username));

        return player;
    }

    private Player getPlayer (String username) {
        for (Player player: players) {
            if (player.getUsername().equals(username))
                return player;
        }
        throw new IllegalArgumentException("Player not in this game");
    }

    public void disconnectPlayer (String username) {
        Player player = this.getPlayer(username);

        if (!player.isConnected()) {
            return;
        }

        player.setConnectionState(false);

        if (isStarted() && (this.players.get(currentPlayerIndex).equals(player))) {
            try {
                calculateNextPlayer();
            } catch (IllegalFlowException e) {
                throw new RuntimeException();
            }

            this.transceiver.broadcast(new CurrentPlayerChangedEventData(players.get(currentPlayerIndex)));
        }

        this.transceiver.broadcast(new PlayerHasDisconnectedEventData(player.getUsername()));
    }

    /**
     * Starts the game by initializing the common goals and the first player index.
     *
     * @param username Name of the user who wants to start the game
     * @throws IllegalFlowException if there are less than two players.
     */
    public void startGame(String username) throws IllegalFlowException {
        if (players.size() < 2)
            throw new IllegalFlowException("You need at least two players in order to start the game");

        if (!this.players.get(0).is(username))
            throw new IllegalFlowException("you can't start the game");

        this.isStarted = true;
        this.refillBoardIfNecessary();
        this.currentPlayerIndex = FIRST_PLAYER_INDEX;
        this.commonGoals = CommonGoal.getTwoRandomCommonGoals(players.size());
        this.transceiver.broadcast(new GameHasStartedEventData());
    }

    public boolean hasPlayerDisconnected () {
        for (Player player: this.players) {
            if (!player.isConnected())
                return true;
        }
        return false;
    }

    public void forgetLastSelection(String username, Coordinate c) throws IllegalFlowException {
        if (!isStarted()) throw new IllegalFlowException("Game is not started");
        if (isOver()) throw new IllegalFlowException("Game has ended");
        if (!players.get(currentPlayerIndex).getUsername().equals(username)) throw new IllegalFlowException("It's not your turn");

        this.board.forgetSelected(c);
        this.transceiver.broadcast(new BoardChangedEventData(board.getView()));
        this.transceiver.broadcast(new PlayerHasDeselectTile(c));
    }

    /**
     * @return true iff at least one bookshelf is full
     * */
    public boolean atLeastOneBookshelfIsFull() {
        return players.stream().anyMatch(p -> p.getBookshelf().isFull());
    }

    private void refillBoardIfNecessary () {
        if (!this.board.needsRefill())
            return;

        boolean hasChanged = false;

        while (!bag.isEmpty()) {
            Tile t = this.bag.getRandomTile();

            try {
                this.board.fillRandomly(t, this.players.size());
                hasChanged = true;
            } catch (IllegalArgumentException e) {
                bag.forgetLastExtraction();
                break;
            }
        }

        if (hasChanged) {
            this.transceiver.broadcast(new BoardChangedEventData(this.board.getView()));
        }
    }

    private int getNextPlayerOnline (int currentPlayerIndex) throws NoPlayerConnectedException {
        for (int i = 1; i < this.players.size(); i++) {
            final int index = (currentPlayerIndex + i) % this.players.size();
            if (this.players.get(index).isConnected) {
                return index;
            }
        }
        throw new NoPlayerConnectedException();
    }

    private void calculateNextPlayer() throws IllegalFlowException {
        assert this.players.size() >= 2 && this.players.size() <= 4;
        assert this.isStarted;
        assert currentPlayerIndex >=  0 && currentPlayerIndex < players.size();

        refillBoardIfNecessary();

        if (atLeastOneBookshelfIsFull() && this.currentPlayerIndex + 1 == this.players.size()) {
            // Game ending logic:
            for (Player player : players) {
                int personalGoalPoints = player.getPersonalGoal().calculatePoints(player.getBookshelf());
                if (personalGoalPoints > 0) {
                    player.addPoints(personalGoalPoints);

                    this.transceiver.broadcast(new PlayerPointsChangeEventData(player, player.getPersonalGoal().getPointMasks()));
                }

                Goal adjacencyGoal = new AdjacencyGoal();
                int adjacencyGoalPoints = adjacencyGoal.calculatePoints(player.getBookshelf());

                if (adjacencyGoalPoints > 0) {
                    player.addPoints(adjacencyGoalPoints);

                    this.transceiver.broadcast(new PlayerPointsChangeEventData(player, adjacencyGoal.getPointMasks()));
                }
            }

            for (Player player : players) {
                int max = this.winners.isEmpty() ? 0 : winners.get(0).getPoints();

                if (player.getPoints() >= max) {
                    if (player.getPoints() > max)
                        winners.clear();

                    winners.add(player);
                }
            }

            this.transceiver.broadcast(new GameOverEventData(this.getWinners()));
        } else {
            this.refillBoardIfNecessary();

            int index;

            try {
                index = getNextPlayerOnline(this.currentPlayerIndex);
            } catch (NoPlayerConnectedException e) {
                // there is no players connected
                index = (currentPlayerIndex + 1) % players.size();
            }

            if (players.get(index).equals(players.get(currentPlayerIndex))) {
                // there is only one player connected
                index = (currentPlayerIndex + 1) % players.size();
            }

            // set new turn
            this.currentPlayerIndex = index;

            this.transceiver.broadcast(new CurrentPlayerChangedEventData(players.get(currentPlayerIndex).getView()));
        }
    }

    /**
     * TODO: javadoc
     * insert tile selected
     * */
    public void insertTile (String username, int col) throws IllegalFlowException, IllegalExtractionException {
        final Player player = getPlayer(username);

        if (!this.players.get(currentPlayerIndex).equals(player))
            throw new IllegalFlowException();

        List<Tile> t = board.getSelectedTiles();

        if (t.isEmpty())
            throw new IllegalExtractionException("You have select 0 tiles");

        player.getBookshelf().insertTiles(t, col);
        board.draw();

        this.transceiver.broadcast(new BoardChangedEventData(board.getView()));
        this.transceiver.broadcast(new BookshelfHasChangedEventData(username, player.getBookshelf()));

        for (CommonGoal commonGoal: this.commonGoals) {
            int points;
            try {
                points = commonGoal.calculatePoints(player.getBookshelf());
            } catch (Exception e) {
                points = 0;
            }

            if (points > 0) {
                player.addPoints(points);

                this.transceiver.broadcast(new PlayerPointsChangeEventData(player, commonGoal.getPointMasks()));
            }
        }

        this.calculateNextPlayer();
    }

    public int getPersonalGoal(String username) {
        for (Player player: players) {
            if (player.is(username)) {
                return player.getPersonalGoal().getIndex();
            }
        }
        throw new IllegalArgumentException("Player is not in this game");
    }

    public GameView createView () {
        return super.createView();
    }

    /**
     * TODO: javadoc
     * select a tile from the board
     * */
    public void selectTile (String username, Coordinate coordinate) throws IllegalFlowException, IllegalExtractionException, FullSelectionException {
        if (!this.isStarted)
            throw new IllegalFlowException("Game is not started");
        if (isOver())
            throw new IllegalFlowException("Game is over");
        if (!this.players.get(currentPlayerIndex).getUsername().equals(username))
            throw new IllegalFlowException("It's not your turn");

        this.board.selectTile(coordinate);
        this.transceiver.broadcast(new BoardChangedEventData(board.getView()));
    }
}
