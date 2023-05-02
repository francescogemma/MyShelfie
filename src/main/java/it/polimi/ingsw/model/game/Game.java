package it.polimi.ingsw.model.game;

import it.polimi.ingsw.event.EventTransceiver;
import it.polimi.ingsw.event.LocalEventTransceiver;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.data.game.*;
import it.polimi.ingsw.event.transmitter.EventTransmitter;
import it.polimi.ingsw.model.board.FullSelectionException;
import it.polimi.ingsw.model.board.IllegalExtractionException;
import it.polimi.ingsw.model.board.RemoveNotLastSelectedException;
import it.polimi.ingsw.model.bookshelf.BookshelfMaskSet;
import it.polimi.ingsw.model.goal.AdjacencyGoal;
import it.polimi.ingsw.model.goal.CommonGoal;
import it.polimi.ingsw.model.goal.Goal;
import it.polimi.ingsw.model.goal.PersonalGoal;
import it.polimi.ingsw.model.tile.Tile;
import it.polimi.ingsw.utils.Coordinate;
import it.polimi.ingsw.utils.Logger;
import it.polimi.ingsw.utils.Pair;
import it.polimi.ingsw.event.data.client.StartGameEventData;
import it.polimi.ingsw.model.board.Board;

import java.util.*;

public class Game extends GameView {
    /**
     * Transceiver on which to broadcast events
     * */
    private transient EventTransmitter transceiver;

    /**
     * List of all personal goals available
     * This value can be null if the game is loaded from disk
     * */
    protected final transient List<Integer> personalGoalIndexes;

    private List<PersonalGoal> personalGoals;

    /**
     * Creates a new game with the given name.
     * @param name the name of the game
     * @throws IllegalArgumentException iff the name is empty
     * @throws NullPointerException iff name is null
     */
    public Game(String name, String username) {
        super(name, username);

        this.personalGoalIndexes = new ArrayList<>(Arrays.asList( 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 ));
        this.personalGoals = new ArrayList<>();
        Collections.shuffle(this.personalGoalIndexes);
    }

    public void forceStop () {
        this.isStopped = true;
        players.forEach(p -> p.setConnectionState(false));
    }

    private int getIndex (String username) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).is(username))
                return i;
        }

        throw  new IllegalArgumentException("Player not in this game");
    }

    /**
     * This method stops the game. The only way to resume it is
     * @param username The player that wants to stop the game
     * @see StartGameEventData
     * */
    public boolean stopGame (String username) {
        String playerOnline;

        try {
            // If the creator has disconnected, the possibility of stopping the game should be
            // given in the order of entry into the game.
            if (getPlayer(creator).isConnected()) {
                playerOnline = creator;
            } else {
                playerOnline = super.players
                        .get(this.getNextPlayerOnline(0))
                        .getUsername();
            }
        } catch (NoPlayerConnectedException ignored) {
            // there is no player except [username]
            playerOnline = username;
        }

        if (username.equals(playerOnline)) {
            setStopped();
            return true;
        }

        return false;
    }

    /**
     * This method sets the internal transceiver on which to broadcast the game events.
     * @throws NullPointerException iff transceiver is null
     * @param transceiver Transceiver on which to broadcast events.
     *
     * @see EventTransmitter
     * @see EventTransceiver
     * @see LocalEventTransceiver
     */
    public void setTransceiver (EventTransmitter transceiver) {
        assert this.transceiver == null;

        if (transceiver == null)
            throw new NullPointerException();

        this.transceiver = transceiver;
    }

    public void forceDisconnectAllPlayer() {
        for (Player player: players) {
            player.setConnectionState(false);
        }
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
            if (otherPlayer.is(username)) {
                if (otherPlayer.isConnected()) {
                    throw new PlayerAlreadyInGameException(username);
                }

                otherPlayer.setConnectionState(true);
                this.transceiver.broadcast(new PlayerHasJoinEventData(otherPlayer.getUsername()));
                return otherPlayer;
            }
        }

        if (isStarted() || isStopped()) {
            throw new IllegalFlowException("You can't add players when the game has already started");
        }

        if (players.size() == 4) {
            throw new IllegalFlowException("You can't have more than 4 players in the same game");
        }

        Player player = new Player(username);

        this.personalGoals.add(PersonalGoal.fromIndex(personalGoalIndexes.get(0)));
        this.personalGoalIndexes.remove(0);

        super.players.add(player);

        this.transceiver.broadcast(new PlayerHasJoinEventData(username));

        return player;
    }

    private Player getPlayer (String username) {
        final int index = getIndex(username);
        return players.get(index);
    }

    public boolean containPlayer (String username) {
        return players.stream().anyMatch(p -> p.getUsername().equals(username));
    }

    public void removePlayer (String username) throws IllegalFlowException {
        if (isStarted())
            throw new IllegalFlowException("Game has already started");

        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).is(username)) {
                personalGoalIndexes.add(personalGoals.get(i).getIndex());
                personalGoals.remove(i);
                players.remove(i);
                broadcast(new PlayerHasDisconnectedEventData(username));
                return;
            }
        }

        throw new IllegalArgumentException("Player not in this game");
    }

    private void setStopped () {
        isStopped = true;
        players.forEach(p -> p.setConnectionState(false));
        broadcast(new GameHasBeenStoppedEventData());
    }

    /**
     * @return true iff there are no player connected
     * */
    public boolean disconnectPlayer (String username) {
        boolean res = false;
        Player player = this.getPlayer(username);

        if (player.isDisconnected()) {
            return false;
        }

        player.setConnectionState(false);

        if (!isStarted()) {
            this.transceiver.broadcast(new PlayerHasDisconnectedEventData(player.getUsername()));

            return false;
        }

        if (numberOfPlayerOnline() < 2) {
            setStopped();
            res = true;
        } else if (isStarted() && (this.players.get(currentPlayerIndex).equals(player))) {
            try {
                calculateNextPlayer();
            } catch (IllegalFlowException e) {
                Logger.writeCritical("This function should not be throw IllegalFlowException");
                assert false;
            } catch (NoPlayerConnectedException e) {
                setStopped();
                res = true;
            }

            this.transceiver.broadcast(new CurrentPlayerChangedEventData(players.get(currentPlayerIndex)));
        }

        this.transceiver.broadcast(new PlayerHasDisconnectedEventData(player.getUsername()));
        return res;
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

        if (!this.canStartGame(username))
            throw new IllegalFlowException("you can't start the game");

        if (numberOfPlayerOnline() < 2)
            throw new IllegalFlowException("Player online can't be 1 or 0");

        this.isStarted = true;

        if (isStopped()) {
            isStopped = false;
            if (players.get(currentPlayerIndex).isDisconnected()) {
                try {
                    calculateNextPlayer();
                } catch (IllegalFlowException | NoPlayerConnectedException e) {
                    Logger.writeCritical("This method should not fail here");
                }
            }
        } else {
            this.currentPlayerIndex = FIRST_PLAYER_INDEX;
            this.commonGoals = CommonGoal.getTwoRandomCommonGoals(players.size());
        }

        this.isStopped = false;

        this.refillBoardIfNecessary();
        this.transceiver.broadcast(new GameHasStartedEventData());
    }

    /**
     * @throws IllegalFlowException
     * <ul>
     *  <li> Game is not started </li>
     *  <li> Game is stopped </li>
     *  <li> username is not the current player </li>
     * </ul>
     * @throws RemoveNotLastSelectedException iff you can't deselect coordinate
     * @throws IllegalArgumentException iff coordinate is not selected
     */
    public void forgetLastSelection(String username, Coordinate coordinate) throws IllegalFlowException {
        if (!isStarted() || isStopped()) throw new IllegalFlowException("Game is not started or is stopped");
        if (isOver()) throw new IllegalFlowException("Game has ended");
        if (!players.get(currentPlayerIndex).getUsername().equals(username)) throw new IllegalFlowException("It's not your turn");

        this.board.forgetSelected(coordinate);
        this.transceiver.broadcast(new BoardChangedEventData(board.createView()));
        this.transceiver.broadcast(new PlayerHasDeselectTile(coordinate));
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
            this.transceiver.broadcast(new BoardChangedEventData(this.board.createView()));
        }
    }

    private void broadcast(EventData eventData) {
        this.transceiver.broadcast(eventData);
    }

    private boolean isLastPlayerSelected() throws NoPlayerConnectedException {
        Player lastConnected = null;

        for (Player player : players) {
            if (player.isConnected())
                lastConnected = player;
        }

        if (lastConnected == null)
            throw new NoPlayerConnectedException();

        return players.get(currentPlayerIndex).equals(lastConnected);
    }

    /*
     * @throws NoPlayerConnectedException iff there are no other connected players besides the current one.
     * */
    private void calculateNextPlayer() throws IllegalFlowException, NoPlayerConnectedException {
        assert this.players.size() >= 2 && this.players.size() <= 4;
        assert this.isStarted() && !isStopped();
        assert currentPlayerIndex >=  0 && currentPlayerIndex < players.size();

        refillBoardIfNecessary();

        if (atLeastOneBookshelfIsFull() && isLastPlayerSelected()) {
            // Game ending logic:
            List<Pair<Integer, BookshelfMaskSet>> pointsAchievePersonal = new ArrayList<>();
            List<Pair<Integer, BookshelfMaskSet>> pointsAchieveAdjacency = new ArrayList<>();

            for (int i = 0; i < players.size(); i++) {
                final Player player = players.get(i);
                final PersonalGoal personalGoal = personalGoals.get(i);

                final int personalGoalPoints = personalGoal.calculatePoints(player.getBookshelf());

                if (personalGoalPoints > 0) {
                    pointsAchievePersonal.add(new Pair<>(personalGoalPoints, personalGoal.getPointMasks()));
                    player.addPoints(personalGoalPoints);
                } else pointsAchievePersonal.add(new Pair<>(0, null));

                Goal adjacencyGoal = new AdjacencyGoal();
                int adjacencyGoalPoints = adjacencyGoal.calculatePoints(player.getBookshelf());

                if (adjacencyGoalPoints > 0) {
                    pointsAchieveAdjacency.add(new Pair<>(adjacencyGoalPoints, adjacencyGoal.getPointMasks()));
                    player.addPoints(adjacencyGoalPoints);
                } else pointsAchieveAdjacency.add(new Pair<>(0, null));
            }

            for (Player player : players) {
                int max = this.winners.isEmpty() ? 0 : winners.get(0).getPoints();

                if (player.getPoints() >= max) {
                    if (player.getPoints() > max)
                        winners.clear();

                    winners.add(player);
                }
            }

            broadcast(
                    new GameOverEventData(
                            this.getWinners(),
                            pointsAchievePersonal,
                            pointsAchieveAdjacency
                    )
            );
        } else {
            this.refillBoardIfNecessary();

            int index;

            try {
                index = getNextPlayerOnline(this.currentPlayerIndex);
            } catch (NoPlayerConnectedException e) {
                // there are no players connected
                this.currentPlayerIndex = (currentPlayerIndex + 1) % players.size();

                // we need to notify internally that game is stopped
                this.isStopped = true;

                throw e;
            }

            if (players.get(index).equals(players.get(currentPlayerIndex))) {
                // there is only one player connected
                index = (currentPlayerIndex + 1) % players.size();
            }

            // set new turn
            this.currentPlayerIndex = index;

            this.transceiver.broadcast(new CurrentPlayerChangedEventData(players.get(currentPlayerIndex)));
        }
    }

    /**
     * This method is used for insert a selection of tile in che bookshelf
     * @throws IllegalFlowException
     *  <ul>
     *      <li> Game is stopped </li>
     *      <li> username is not the current player </li>
     *  </ul>
     * @throws IllegalExtractionException
     *  <ul>
     *      <li> Selection is empty </li>
     *      <li> Current selection can't be draw </li>
     *  </ul>
     *
     * @see Board
     * */
    public void insertTile (String username, int col) throws IllegalFlowException, IllegalExtractionException {
        final Player player = getPlayer(username);

        if (isStopped())
            throw new IllegalFlowException("Game is stopped");

        if (!this.players.get(currentPlayerIndex).equals(player))
            throw new IllegalFlowException();

        List<Tile> t = board.getSelectedTiles();

        if (t.isEmpty())
            throw new IllegalExtractionException("You have select 0 tiles");

        if (!board.canDraw()) {
            throw new IllegalExtractionException("You can't draw this tiles");
        }

        player.getBookshelf().insertTiles(t, col);
        board.draw();

        broadcast(new BoardChangedEventData(board.createView()));
        broadcast(new BookshelfHasChangedEventData(username, player.getBookshelf()));

        for (int i = 0; i < commonGoals.length; i++){
            if (player.hasAchievedCommonGoal(i))
                continue;

            final int points = commonGoals[i].calculatePoints(player.getBookshelf());

            if (points > 0) {
                player.addPoints(points);
                player.setAchievedCommonGoals(i);

                broadcast(
                        new CommonGoalCompletedEventData(
                            player,
                            points,
                            commonGoals[i].getPointMasks(),
                            commonGoals[i].getIndex()
                        )
                );
            }
        }

        if (player.getBookshelf().isFull() && firstPlayerCompleteBookshelf == -1) {
            firstPlayerCompleteBookshelf = currentPlayerIndex;
            player.addPoints(1);
            broadcast(new FirstFullBookshelfEventData(player.getUsername()));
        }

        /**
         * In theory, this method should be called with at least 2 players online,
         * so the method calculateNextPlayer() should never fail.
         * This is because if the disconnection of the second-to-last player online occurs,
         * the game will automatically stop.
         */
        try {
            this.calculateNextPlayer();
        } catch (NoPlayerConnectedException e) {
            // there is no other player online, we need to stop this game.
            Logger.writeCritical("calculateNextPlayer failed.");
            assert false: "check log";
        }
    }

    /**
     * This method returns personal goal of player username
     * @return Index the player's personal goal
     * @throws IllegalArgumentException iff username is not in this game.
     * */
    public int getPersonalGoal(String username) {
        for (int i = 0; i < players.size(); i++){
            final Player player = this.players.get(i);
            final PersonalGoal personalGoal = this.personalGoals.get(i);

            if (player.is(username)) {
                return personalGoal.getIndex();
            }
        }
        throw new IllegalArgumentException("Player is not in this game");
    }

    /**
     * This method selects a Tile from the board
     * @throws IllegalFlowException iff game is started or stopped
     * @throws IllegalExtractionException iff coordinate is invalid
     * @throws FullSelectionException iff it's already selected maximum number of tiles
     *
     * @see Board
     */
    public void selectTile (String username, Coordinate coordinate) throws IllegalFlowException, IllegalExtractionException, FullSelectionException {
        if (!this.isStarted() || isStopped())
            throw new IllegalFlowException("Game is not started");
        if (isOver())
            throw new IllegalFlowException("Game is over");
        if (!this.players.get(currentPlayerIndex).getUsername().equals(username))
            throw new IllegalFlowException("It's not your turn");

        this.board.selectTile(coordinate);
        this.transceiver.broadcast(new BoardChangedEventData(board.createView()));
    }
}
