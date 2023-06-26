package it.polimi.ingsw.model.game;

import it.polimi.ingsw.event.EventTransceiver;
import it.polimi.ingsw.event.LocalEventTransceiver;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.data.game.*;
import it.polimi.ingsw.event.transmitter.EventTransmitter;
import it.polimi.ingsw.model.board.FullSelectionException;
import it.polimi.ingsw.model.board.IllegalExtractionException;
import it.polimi.ingsw.model.board.RemoveNotLastSelectedException;
import it.polimi.ingsw.model.bookshelf.Bookshelf;
import it.polimi.ingsw.model.bookshelf.BookshelfMaskSet;
import it.polimi.ingsw.model.goal.*;
import it.polimi.ingsw.model.tile.Tile;
import it.polimi.ingsw.utils.Coordinate;
import it.polimi.ingsw.utils.Logger;
import it.polimi.ingsw.utils.Pair;
import it.polimi.ingsw.event.data.client.StartGameEventData;
import it.polimi.ingsw.model.board.Board;

import java.util.*;

/**
 * The class implements all the game mechanics of a match.
 * It is possible to be notified of changes inside this object by passing an {@link EventTransmitter eventTransmitter}
 * and registering listeners to it. Each game supports up to 4 players per match and has three possible states:
 * playing, the game is stopped because there are no connected users or it has been requested to stop,
 * there is only one connected player and the others are waiting to connect to the game.
 * Each match has an owner, assigned at its creation and cannot be modified, who has the honor of being able to
 * stop or start the game at any time.
 * It is possible to create an immutable object of the instance by calling the function {@link Game#createView()}.
 * The object is synchronized for each call.
 *
 * @see GameView
 * @see Player
 * @see Goal
 * @see EventTransmitter
 * @author Giacomo Groppi
 * */
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

    /**
     * Timer triggered if a player is left alone in the game. At the end of this timer,
     * the game ends and the remaining player becomes the only winner.
     * */
    private transient Timer timerEndGame;

    /**
     * Timer after which the turn is lost.
     * */
    private transient Timer playTimer;

    /**
     * This is an instance variable that represents a list of personal goals for each player in the game.
     */
    private final List<PersonalGoal> personalGoals;

    /**
     * It's true iff the game has just been started or restarted
    */
    private boolean hasRestarted = false;

    /**
     * Creates a new game with the given name.
     * @param name the name of the game
     * @param username is the username of the {@link Player} who is creating the game.
     *
     * @throws IllegalArgumentException iff the name is empty
     * @throws NullPointerException iff name is null
     */
    public Game(String name, String username) {
        super(name, username);

        this.personalGoalIndexes = new ArrayList<>(Arrays.asList( 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 ));
        this.personalGoals = new ArrayList<>();
        Collections.shuffle(this.personalGoalIndexes);
    }

    /**
     * This method forces the game to stop by removing
     * any possible pause state and marking all players
     * as disconnected
     * */
    public synchronized void forceStop () {
        this.isStopped = true;
        removeFromWaitingForReconnections();

        players.forEach(p -> p.setConnectionState(false));
    }

    /**
     * This method stops the game. The only way to resume it is
     * @param username The player that wants to stop the game
     * @throws IllegalFlowException iff username can't stop the game
     * @see StartGameEventData
     */
    public synchronized void stopGame (String username) throws IllegalFlowException {
        Logger.writeMessage("Game name: %s Prima %s".formatted(this.name, username));

        if (!canStopGame(username))
            throw new IllegalFlowException("%s can't stop the game".formatted(username));

        setStopped();
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
    public synchronized void setTransceiver (EventTransmitter transceiver) {
        assert this.transceiver == null;

        if (transceiver == null)
            throw new NullPointerException();

        this.transceiver = transceiver;
    }

    /**
     * Use this method to add a player to the game
     *
     * @param username is the username of the {@link Player} that you want to add to the game.
     *
     * @return The added player
     * @throws IllegalFlowException
     * <ul>
     *     <li> The game has already started </li>
     *     <li> The maximum number of players has already been reached </li>
     * </ul>
     *
     * @throws PlayerAlreadyInGameException iff the player is already in the game
     * @throws NullPointerException iff the username is null
     */
    public synchronized Player addPlayer(String username) throws IllegalFlowException, PlayerAlreadyInGameException {
        Objects.requireNonNull(username);

        if (isStarted())
            throw new IllegalFlowException("You can't add players when the game has already started");

        if (players.size() == 4)
            throw new IllegalFlowException("You can't have more than 4 players in the same game");

        if (players.stream().anyMatch(p -> p.is(username)))
            throw new PlayerAlreadyInGameException(username);

        Player player = new Player(username);

        this.personalGoals.add(
                PersonalGoal.fromIndex(
                        personalGoalIndexes.remove(0)
                )
        );

        super.players.add(player);

        return player;
    }

    /**
     * The function removes the player with username "username" from the game.
     *
     * @param username is the username of the {@link Player} that you want to remove from the game.
     *
     * @throws IllegalFlowException iff game is already started.
     * @throws NullPointerException iff username is null.
     */
    public synchronized void removePlayer (String username) throws IllegalFlowException {
        Objects.requireNonNull(username);

        requireStatus(EnumSet.of(RequiredState.NOT_STARTED));

        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).is(username)) {
                personalGoalIndexes.add(personalGoals.get(i).getIndex());
                personalGoals.remove(i);
                players.remove(i);

                Collections.shuffle(personalGoalIndexes);
                return;
            }
        }

        throw new IllegalArgumentException("Player not in this game");
    }

    /**
     * This function is called to declare the victory of the only online player.
     * The points will be calculated based on {@link PersonalGoal} and {@link AdjacencyGoal},
     * and the sole winner will be the only online player, regardless of the number of points.
     */
    private synchronized void awardTimeWinner() {
        assert isWaitingForReconnections();
        assert numberOfPlayerOnline() == 1;

        Player onlyOnline = players.stream().filter(Player::isConnected).toList().get(0);
        winners.add(onlyOnline);
        broadcast(
                new GameOverEventData(
                        winners,
                        players,
                        calculatePoints((List) personalGoals),
                        calculatePoints(players.stream().map(p -> (Goal) new AdjacencyGoal()).toList())
                )
        );
    }

    /**
     * This function sets the game to "waiting for reconnection" state, starts a timer to determine,
     * if the game is not removed from that state, the automatic victory of the only connected player.
     * It is required that the number of online players is equal to 1.
     *
     * @see Game#TIME_WAITING_FOR_RECONNECTIONS_BEFORE_WIN
     */
    private synchronized void setWaitingForReconnections() {
        assert players.stream().filter(Player::isConnected).toList().size() == 1;
        isWaitingForReconnections = true;
        broadcast(new GameHasBeenPauseEventData());

        this.timerEndGame = new Timer();
        this.timerEndGame.schedule(new TimerTask() {
            @Override
            public void run() {
                awardTimeWinner();
            }
        }, TIME_WAITING_FOR_RECONNECTIONS_BEFORE_WIN);
    }

    /**
     * This method removes the "waiting for reconnection" state from the game.
     */
    private synchronized void removeFromWaitingForReconnections() {
        if (timerEndGame != null) {
            timerEndGame.cancel();
            timerEndGame = null;
        }
        isWaitingForReconnections = false;
    }

    /**
     * This method sets the game as stopped and notifies the clients that the game has
     * been paused using the {@link GameHasBeenStoppedEventData} event.
     * The method disables timers if they are waiting to finish.
     */
    private synchronized void setStopped () {
        forceStop();

        if (this.timerEndGame != null)
            this.timerEndGame.cancel();
        this.timerEndGame = null;

        if (this.playTimer != null)
            this.playTimer.cancel();
        this.playTimer = null;

        Logger.writeMessage("game: %s is stopped".formatted(name));
        broadcast(new GameHasBeenStoppedEventData());
    }

    /**
     * This method disconnects a player from the game.
     * The method notifies all players that "username" has disconnected.
     * If the number of remaining players is 0, the game is put in stop.
     * If the number of remaining players is 1, the game is put in waitingForReconnection.
     * If the number of remaining players is > 1 and the disconnected player was the current player, they lose their turn.
     *
     * @param username is the username of the {@link Player} that you want to disconnect.
     *
     * @throws NullPointerException iff username is null
     * @throws IllegalFlowException iff player is already disconnected
     * */
    public synchronized void disconnectPlayer (String username) throws IllegalFlowException {
        Objects.requireNonNull(username);

        Player player = this.getPlayer(username);

        if (player.isDisconnected()) {
            throw new IllegalFlowException("player already disconnected");
        }

        player.setConnectionState(false);

        if (numberOfPlayerOnline() != 0) {
            Optional<String> currentOwner = getCurrentOwner();
            assert currentOwner.isPresent();
            this.transceiver.broadcast(new PlayerHasDisconnectedEventData(player.getUsername(), currentOwner.get()));
        }

        if (numberOfPlayerOnline() == 0) {
            removeFromWaitingForReconnections();
            setStopped();
        } else if (numberOfPlayerOnline() == 1) {
            setWaitingForReconnections();
            if (this.players.get(currentPlayerIndex).equals(player)) {
                calculateNextPlayer();
                this.transceiver.broadcast(new CurrentPlayerChangedEventData(players.get(currentPlayerIndex)));
            }
        } else if (isStarted() && (this.players.get(currentPlayerIndex).equals(player))) {
            calculateNextPlayer();

            this.transceiver.broadcast(new CurrentPlayerChangedEventData(players.get(currentPlayerIndex)));
        }
    }

    /**
     * This method allows to remove the stop state from a game.
     * The method notifies all players that the game has restarted with the {@link GameHasStartedEventData} event.
     *
     * @param username is the username of the {@link Player} which is trying to restart the game.
     * @param usernameToWait is the list of usernames whose connection has to be waited when restarting the game.
     *
     * @throws NullPointerException iff username is null
     * @throws IllegalFlowException iff
     *  <ul>
     *      <li> username cannot restart the game, because they are not the creator </li>
     *      <li> the game has not started yet </li>
     *      <li> the number of players who want to join the game after restarting it is less than 2 </li>
     *  </ul>
     */
    public synchronized void restartGame(String username, List<String> usernameToWait) throws IllegalFlowException {
        Objects.requireNonNull(username);
        Objects.requireNonNull(usernameToWait);
        assert !usernameToWait.contains(null);

        requireStatus(EnumSet.of(RequiredState.STARTED));

        if (usernameToWait.size() < 2)
            throw new IllegalFlowException("Number of player can't be lower than 2");

        if (!this.creator.equals(username))
            throw new IllegalFlowException("You can't restart this game");


        for (int i = 0; i < players.size(); i++) {
            if (usernameToWait.contains(players.get(currentPlayerIndex).getUsername())) {
                break;
            } else {
                currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            }
        }

        hasRestarted = true;
        isStopped = false;
        broadcast(new GameHasStartedEventData());
    }

    /**
     * This method returns the connection status of the player "username".
     *
     * @param username is the username of the {@link Player} for which we want to check the connection state.
     *
     * @return True iff the player "username" is connected
     * @throws PlayerNotInGameException iff "username" is not in this game.
     * */
    public synchronized boolean isPlayerConnected(String username) throws PlayerNotInGameException {
        for (Player player: players) {
            if (player.is(username))
                return player.isConnected();
        }

        throw new PlayerNotInGameException(username, name);
    }

    /**
     * This method allows changing the current player if the current player has not connected to the game within
     * {@link Game#TIME_FIRST_PLAYER_CONNECT}.
     */
    private synchronized void timerEndForTurn () {
        if (!isStopped() && (players.get(currentPlayerIndex).isDisconnected())) {
            broadcast(new PlayerHasDisconnectedEventData(
                    players.get(currentPlayerIndex).getUsername(),
                    getCurrentOwner().get()
            ));

            calculateNextPlayer();
        }
    }

    /**
     * This function connects a player to the game.
     * Initially, when the game is started for the first time, every player is marked as disconnected,
     * and as soon as a player connects, {@link Game#connectPlayer(String)} must be called to effectively connect
     * them to the game.
     * When the first player of the game connects, the game is waiting for reconnections.
     * If the second user who connects is not the current player, i.e. the first player, since the game has just started
     * and was on [waitingForReconnections], a timer of TIME_FIRST_PLAYER_CONNECT milliseconds is launched, which at the end switches
     * the turn to the first connected player.
     *
     * @param username the username of the player who wants to connect
     * @throws NullPointerException iff username is null
     * @throws PlayerAlreadyInGameException if the player is already connected
     * @throws IllegalFlowException if the game is stopped
     * @throws PlayerNotInGameException if the player is not in this game.
     */
    public synchronized void connectPlayer(String username) throws PlayerAlreadyInGameException, IllegalFlowException,
        PlayerNotInGameException
    {
        Objects.requireNonNull(username);

        requireStatus(EnumSet.of(RequiredState.NOT_STOP));

        for (Player player : players) {
            if (!player.is(username))
                continue;

            if (player.isConnected()) {
                throw new PlayerAlreadyInGameException(username);
            }

            player.setConnectionState(true);
            this.transceiver.broadcast(new PlayerHasJoinGameEventData(player.getUsername(), getCurrentOwner().get()));

            if (numberOfPlayerOnline() == 1) {
                setWaitingForReconnections();
            } else if (isWaitingForReconnections()) {
                assert numberOfPlayerOnline() == 2;
                assert playTimer == null;

                removeFromWaitingForReconnections();

                if (players.get(currentPlayerIndex).isDisconnected() && hasRestarted) {
                    this.playTimer = new Timer();
                    this.playTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            timerEndForTurn();
                        }}, TIME_FIRST_PLAYER_CONNECT);
                }

                hasRestarted = false;
            } else {
                if (playTimer != null) {
                    this.playTimer.cancel();
                    this.playTimer = null;
                }
            }

            return;
        }
        throw new PlayerNotInGameException(username, name);
    }

    /**
     * Starts the game by initializing the common goals and the first player index.
     *
     * @param username Name of the user who wants to start the game
     * @throws IllegalFlowException if there are less than two players.
     */
    public synchronized void startGame(String username) throws IllegalFlowException {
        requireStatus(EnumSet.of(RequiredState.NOT_STARTED));

        if (players.size() < 2)
            throw new IllegalFlowException("You need at least two players in order to start the game");

        if (!this.canStartGame(username))
            throw new IllegalFlowException("you can't start the game");

        Logger.writeMessage("Number of player online:"  + numberOfPlayerOnline());

        this.isStarted = true;
        this.hasRestarted = true;

        this.currentPlayerIndex = FIRST_PLAYER_INDEX;

        CommonGoal [] c = CommonGoal.getTwoRandomCommonGoals(players.size());
        this.commonGoals[0] = c[0];
        this.commonGoals[1] = c[1];

        this.refillBoardIfNecessary();
    }

    /**
     * Use this method to remove the last selected tile from the tile selection
     *
     * @param username is the username of the {@link Player} which wants to deselect their last selected tile.
     * @param coordinate is the {@link Coordinate} of the tile in the {@link Board} that the player wants to deselect.
     *
     * @throws IllegalFlowException
     * <ul>
     *  <li> Game is not started </li>
     *  <li> Game is stopped </li>
     *  <li> username is not the current player </li>
     * </ul>
     * @throws RemoveNotLastSelectedException iff you can't deselect coordinate
     * @throws IllegalArgumentException iff coordinate is not selected
     */
    public synchronized void forgetLastSelection(String username, Coordinate coordinate) throws IllegalFlowException {
        requireStatus(EnumSet.of(RequiredState.STARTED, RequiredState.NOT_STOP, RequiredState.NOT_WAITING_FOR_RECONNECTION, RequiredState.NOT_OVER));
        if (!players.get(currentPlayerIndex).getUsername().equals(username)) throw new IllegalFlowException("It's not your turn");

        this.board.forgetSelected(coordinate);
        this.transceiver.broadcast(new BoardChangedEventData(board.createView()));
        this.transceiver.broadcast(new PlayerHasDeselectTile(coordinate));
    }

    /**
     * This method is used to fill the board if necessary. If the board changes,
     * a {@link BoardChangedEventData} event will be sent.
     */
    private synchronized void refillBoardIfNecessary () {
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

    /**
     * This method is used to perform a broadcast on the {@link Game#transceiver} of the game using the
     * provided event data as a parameter.
     * @param eventData The event for which the broadcast needs to be performed
     * @see Game#transceiver
     */
    private synchronized void broadcast(EventData eventData) {
        this.transceiver.broadcast(eventData);
    }

    /**
     * @return True iff the current player is the last player connected
     */
    private synchronized boolean isLastPlayerSelected() {
        Player lastConnected = null;

        for (Player player : players) {
            if (player.isConnected())
                lastConnected = player;
        }

        assert lastConnected != null: "isLastPlayerSelected fail";

        return players.get(currentPlayerIndex).equals(lastConnected);
    }

    /**
     * This method calculates the points for each player i-th with the i-th goal.
     * It is required that the goals list passed as a parameter has the same size as the players list.
     *
     * @param goals is the list of {@link Goal goals} that have to be checked to calculate the points that every player has
     *              scored.
     *
     * @return A list of the same size as the players list, where each position i contains the
     * number of points obtained by the i-th player for completing the i-th goal, and the
     * corresponding {@link BookshelfMaskSet} for that goal.
     */
    private List<Pair<Integer, BookshelfMaskSet>> calculatePoints(List<Goal> goals) {
        List<Pair<Integer, BookshelfMaskSet>> res = new ArrayList<>();
        assert goals.size() == players.size();

        for (int i = 0; i < players.size(); i++) {
            Bookshelf bookshelf = players.get(i).getBookshelf();
            final int points = goals.get(i).calculatePoints(bookshelf);

            res.add(new Pair<>(points, points > 0 ? goals.get(i).getPointMasks() : null));

            players.get(i).addPoints(points);
        }

        return res;
    }

    /**
     * The method calculates and sets the next player after the current one.
     * If the current player is the last one who needs to play because there is a player who has finished filling
     * the {@link Bookshelf} and was the last one to join the game, the points are calculated, and the end of the
     * game is notified.
     */
    private synchronized void calculateNextPlayer() {
        assert this.players.size() >= 2 && this.players.size() <= 4;
        assert this.isStarted();
        assert !isStopped();
        assert currentPlayerIndex >=  0 && currentPlayerIndex < players.size();

        if (atLeastOneBookshelfIsFull() && isLastPlayerSelected()) {
            // Game ending logic:
            List<Pair<Integer, BookshelfMaskSet>> pointsAchievePersonal = calculatePoints((List) this.personalGoals);
            List<Pair<Integer, BookshelfMaskSet>> pointsAchieveAdjacency = calculatePoints(this.players.stream().map(p -> (Goal) new AdjacencyGoal()).toList());

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
                            winners,
                            players,
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
                Logger.writeCritical("Call");
                throw new IllegalStateException("No player connected");
            }

            // set new turn
            if (!board.getSelectedCoordinates().isEmpty()) {
                board.forgetSelection();
                this.transceiver.broadcast(new BoardChangedEventData(this.board.createView()));
            }

            this.currentPlayerIndex = index;

            this.transceiver.broadcast(new CurrentPlayerChangedEventData(players.get(currentPlayerIndex)));
        }
    }

    /**
     * This method is used for insert a selection of tile in che bookshelf
     *
     * @param username is the username of the {@link Player} that wants to insert the selected tiles in their {@link Bookshelf}.
     * @param col is the index of the column in the player's bookshelf where they want to insert the selected tiles.
     *
     * @throws IllegalFlowException
     *  <ul>
     *      <li> Game is stopped or waitingForReconnections </li>
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
    public synchronized void insertTile (String username, int col) throws IllegalFlowException, IllegalExtractionException {
        final Player player = getPlayer(username);

        requireStatus (EnumSet.of(RequiredState.STARTED, RequiredState.NOT_STOP, RequiredState.NOT_WAITING_FOR_RECONNECTION, RequiredState.NOT_OVER));

        if (!this.players.get(currentPlayerIndex).equals(player))
            throw new IllegalFlowException();

        List<Tile> selectedTiles = board.getSelectedTiles();

        if (selectedTiles.isEmpty())
            throw new IllegalExtractionException("You have select 0 tiles");

        if (!board.canDraw()) {
            throw new IllegalExtractionException("You can't draw this tiles");
        }

        player.getBookshelf().insertTiles(selectedTiles, col);
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

        /*
         * we need to set one additional point to the first
         * player that complete the bookshelf
         */
        if (player.getBookshelf().isFull() && firstPlayerCompleteBookshelf == -1) {
            firstPlayerCompleteBookshelf = currentPlayerIndex;
            player.addPoints(1);
            broadcast(new FirstFullBookshelfEventData(player.getUsername()));
        }

        this.calculateNextPlayer();
    }

    /**
     * This method returns personal goal of player username
     *
     * @param username is the username of the {@link Player} for which we want to retrieve the {@link PersonalGoal}.
     *
     * @return Index the player's personal goal
     * @throws IllegalArgumentException iff username is not in this game.
     * */
    public synchronized int getPersonalGoal(String username) {
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
     *
     * @param username is the username of the {@link Player} who wants to select a tile on the {@link Board}.
     * @param coordinate is the {@link Coordinate} of the tile in the board that the player wants to select.
     *
     * @throws IllegalFlowException iff
     * <ul>
     *     <li> game is not started </li>
     *     <li> game is stopped </li>
     *     <li> game is waiting for reconnection </li>
     *     <li> game is over </li>
     *     <li> current player is not username </li>
     * </ul>
     * @throws IllegalExtractionException iff coordinate is invalid
     * @throws FullSelectionException iff it's already selected maximum number of tiles
     *
     * @see Board
     */
    public synchronized void selectTile (String username, Coordinate coordinate) throws IllegalFlowException, IllegalExtractionException, FullSelectionException {
        requireStatus(EnumSet.of(RequiredState.NOT_WAITING_FOR_RECONNECTION, RequiredState.NOT_STOP, RequiredState.STARTED, RequiredState.NOT_OVER));
        if (!this.players.get(currentPlayerIndex).getUsername().equals(username))
            throw new IllegalFlowException("It's not your turn");

        this.board.selectTile(coordinate);
        this.transceiver.broadcast(new BoardChangedEventData(board.createView()));
    }
}
