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

    private transient Timer timer;
    private transient Timer playTimer;

    private final List<PersonalGoal> personalGoals;

    private transient List<String> playersToWait;

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

    public synchronized void forceStop () {
        this.isStopped = true;
        this.isPause = false;
        players.forEach(p -> p.setConnectionState(false));
    }

    private synchronized int getIndex (String username) {
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
    public synchronized boolean stopGame (String username) {
        String playerOnline;

        Logger.writeMessage("Game name: %s Prima %s".formatted(this.name, username));

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
            Logger.writeCritical("This exception should not be throw");
            assert false;
            return false;
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
    public synchronized void setTransceiver (EventTransmitter transceiver) {
        assert this.transceiver == null;

        if (transceiver == null)
            throw new NullPointerException();

        this.transceiver = transceiver;
    }

    public synchronized void setPlayersToWait(List<String> usernames) {
        this.playersToWait = new ArrayList<>(usernames);
    }

    /**
     * Adds a player to the game with the given username or reconnects that player.
     * @param username the username of the player to add
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
    public synchronized Player addPlayer(String username) throws IllegalFlowException {
        if (username == null || username.length() == 0)
            throw new NullPointerException("username is null or has length 0");

        if (isStarted()) {
            throw new IllegalFlowException("You can't add players when the game has already started");
        }

        if (players.size() == 4) {
            throw new IllegalFlowException("You can't have more than 4 players in the same game");
        }

        Player player = new Player(username);

        this.personalGoals.add(
                PersonalGoal.fromIndex(
                        personalGoalIndexes.remove(0)
                )
        );

        super.players.add(player);

        return player;
    }

    private synchronized Player getPlayer (String username) {
        final int index = getIndex(username);
        return players.get(index);
    }

    public synchronized boolean containPlayer (String username) {
        return players.stream().anyMatch(p -> p.getUsername().equals(username));
    }

    public synchronized void removePlayer (String username) throws IllegalFlowException {
        if (isStarted())
            throw new IllegalFlowException("Game has already started");

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

    private synchronized void awardTimeWinner() {
        assert isPause();
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

    private synchronized void setPause() {
        assert players.stream().filter(Player::isConnected).toList().size() == 1;
        isPause = true;
        broadcast(new GameHasBeenPauseEventData());

        this.timer = new Timer();
        this.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                awardTimeWinner();
            }
        }, 60000);
    }

    private synchronized void setStopped () {
        forceStop();

        if (timer != null)
            this.timer.cancel();
        timer = null;

        Logger.writeMessage("Game is stopped!!!");
        broadcast(new GameHasBeenStoppedEventData());
    }

    public synchronized void disconnectPlayer (String username) throws IllegalFlowException {
        Player player = this.getPlayer(username);

        if (player.isDisconnected()) {
            throw new IllegalFlowException("player already disconnected");
        }

        player.setConnectionState(false);

        this.transceiver.broadcast(new PlayerHasDisconnectedEventData(player.getUsername()));

        if (numberOfPlayerOnline() == 0) {
            if (timer != null)
                timer.cancel();
            isPause = false;
            setStopped();
        }

        if (numberOfPlayerOnline() == 1) {
            setPause();
        } else if (isStarted() && (this.players.get(currentPlayerIndex).equals(player))) {
            calculateNextPlayer();

            this.transceiver.broadcast(new CurrentPlayerChangedEventData(players.get(currentPlayerIndex)));
        }
    }

    public synchronized void restartGame(String username) throws IllegalFlowException {
        if (!this.creator.equals(username))
            throw new IllegalFlowException("You can't restart this game");

        if (!isStarted())
            throw new IllegalFlowException("Game is not started");

        for (int i = 0; i < players.size(); i++) {
            if (this.playersToWait.contains(players.get(currentPlayerIndex).getUsername())) {
                break;
            } else {
                currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            }
        }

        isStopped = false;
        broadcast(new GameHasStartedEventData());
    }

    public synchronized boolean isPlayerConnected(String username) throws PlayerNotInGameException {
        for (Player player: players) {
            if (player.is(username))
                return player.isConnected();
        }

        throw new PlayerNotInGameException(username, name);
    }

    private synchronized void timerEndForTurn () {
        if (!isStopped()) {
            try {
                if (getCurrentPlayer().isDisconnected()) {
                    calculateNextPlayer();
                }
            } catch (IllegalFlowException e) {

            }
        }
    }

    public synchronized void connectPlayer(String username) throws PlayerAlreadyInGameException, IllegalFlowException,
        PlayerNotInGameException
    {
        // Allows for reconnection of disconnected players
        if (isStopped()) {
            Logger.writeWarning("View ask to reconnect while stopped");
            throw new IllegalFlowException("Game is stopped you need to restart it!");
        }

        for (Player otherPlayer : players) {
            if (otherPlayer.is(username)) {
                if (otherPlayer.isConnected()) {
                    throw new PlayerAlreadyInGameException(username);
                }

                otherPlayer.setConnectionState(true);
                this.transceiver.broadcast(new PlayerHasJoinGameEventData(otherPlayer.getUsername()));

                if (numberOfPlayerOnline() == 1) {
                    this.playTimer = new Timer();
                    this.playTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            timerEndForTurn();
                        }
                    }, 3000);

                    setPause();
                } else if (isPause()) {
                    assert timer != null;
                    assert numberOfPlayerOnline() == 2;

                    this.isPause = false;
                    timer.cancel();
                    timer = null;
                }

                return;
            }
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
        if (isStarted())
            throw new IllegalFlowException("Game already started...");

        if (players.size() < 2)
            throw new IllegalFlowException("You need at least two players in order to start the game");

        if (!this.canStartGame(username))
            throw new IllegalFlowException("you can't start the game");

        Logger.writeMessage("Number of player online:"  + numberOfPlayerOnline());

        this.isStarted = true;

        this.currentPlayerIndex = FIRST_PLAYER_INDEX;

        CommonGoal [] c = CommonGoal.getTwoRandomCommonGoals(players.size());
        this.commonGoals[0] = c[0];
        this.commonGoals[1] = c[1];

        this.refillBoardIfNecessary();
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
    public synchronized void forgetLastSelection(String username, Coordinate coordinate) throws IllegalFlowException {
        if (!isStarted() || isStopped() || isPause()) throw new IllegalFlowException("Game is not started or is stopped");
        if (isOver()) throw new IllegalFlowException("Game has ended");
        if (!players.get(currentPlayerIndex).getUsername().equals(username)) throw new IllegalFlowException("It's not your turn");

        this.board.forgetSelected(coordinate);
        this.transceiver.broadcast(new BoardChangedEventData(board.createView()));
        this.transceiver.broadcast(new PlayerHasDeselectTile(coordinate));
    }

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

    private synchronized void broadcast(EventData eventData) {
        this.transceiver.broadcast(eventData);
    }

    private synchronized boolean isLastPlayerSelected() {
        Player lastConnected = null;

        for (Player player : players) {
            if (player.isConnected())
                lastConnected = player;
        }

        assert lastConnected != null: "isLastPlayerSelected fail";

        return players.get(currentPlayerIndex).equals(lastConnected);
    }

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

    /*
     * @throws NoPlayerConnectedException iff there are no other connected players besides the current one.
     * */
    private synchronized void calculateNextPlayer() {
        assert this.players.size() >= 2 && this.players.size() <= 4;
        assert this.isStarted() && !isStopped() && !isPause();
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

            try {
                broadcast(
                        new GameOverEventData(
                                this.getWinners(),
                                players,
                                pointsAchievePersonal,
                                pointsAchieveAdjacency
                        )
                );
            } catch (IllegalFlowException e) {
                Logger.writeCritical("call " + e);
                assert false;
            }
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
    public synchronized void insertTile (String username, int col) throws IllegalFlowException, IllegalExtractionException {
        final Player player = getPlayer(username);

        if (!isStarted() || isStopped() || isPause())
            throw new IllegalFlowException("Game is stopped or pause");

        if (isOver())
            throw new IllegalFlowException("Game is over!");

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

        this.calculateNextPlayer();
    }

    /**
     * This method returns personal goal of player username
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
     * @throws IllegalFlowException iff game is started or stopped
     * @throws IllegalExtractionException iff coordinate is invalid
     * @throws FullSelectionException iff it's already selected maximum number of tiles
     *
     * @see Board
     */
    public synchronized void selectTile (String username, Coordinate coordinate) throws IllegalFlowException, IllegalExtractionException, FullSelectionException {
        if (!isStarted() || isStopped() || isPause())
            throw new IllegalFlowException("Game is not started");
        if (isOver())
            throw new IllegalFlowException("Game is over");
        if (!this.players.get(currentPlayerIndex).getUsername().equals(username))
            throw new IllegalFlowException("It's not your turn");

        this.board.selectTile(coordinate);
        this.transceiver.broadcast(new BoardChangedEventData(board.createView()));
    }
}
