package it.polimi.ingsw.view.gui.controller;

import it.polimi.ingsw.controller.Response;
import it.polimi.ingsw.event.NetworkEventTransceiver;
import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.data.VoidEventData;
import it.polimi.ingsw.event.data.client.ExitLobbyEventData;
import it.polimi.ingsw.event.data.client.JoinLobbyEventData;
import it.polimi.ingsw.event.data.client.RestartGameEventData;
import it.polimi.ingsw.event.data.client.StartGameEventData;
import it.polimi.ingsw.event.data.game.GameHasStartedEventData;
import it.polimi.ingsw.event.data.game.PlayerHasExitLobbyEventData;
import it.polimi.ingsw.event.data.game.PlayerHasJoinLobbyEventData;
import it.polimi.ingsw.event.data.internal.PlayerDisconnectedInternalEventData;
import it.polimi.ingsw.event.receiver.EventListener;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.networking.DisconnectedException;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.util.Optional;

/**
 * This menu is part of the game's graphical use interface. It shows a list of players inside a lobby. All players are
 * displayed using a ListView object.
 */
public class GameLobbyMenuController extends Controller {
    /**
     * A text label marking the name of the current game. All players displayed below this label are currently waiting
     * for this specific game to start.
     */
    @FXML private Label gameNameLabel;

    /**
     * A button to navigate to the previous menu, and exit the lobby. It brings the user back to the list of available
     * games.
     */
    @FXML private Button backToAvailableGamesButton;

    /**
     * A button to start the game. If the game is already running, this button is switched with a "resume game" button.
     */
    @FXML private Button startGameButton;

    /**
     * A button to resume the game, if the game is already running. If the game is not running, this button is not shown,
     * and an appropriate "start game" button is displayed instead.
     */
    @FXML private Button restartGameButton;

    /**
     * A list containing the name of all players.
     */
    private ObservableList<String> players;

    /**
     * The actual JavaFX list object that is rendered on screen: a list of horizontal bars containing a textual string,
     * with the name of all players that are currently waiting in the lobby.
     */
    @FXML private ListView<String> playersListView;

    /**
     * Utility class attribute that stores this menu's name. This attribute is often used by other layouts, to switch to
     * the next menu without needing to rewrite and consequentially expose its name.
     */
    public static final String NAME = "GameLobbyMenu";

    // Data:
    /**
     * The current active transceiver is stored here. This object is initialized in the "initialize" method within this
     * class.
     */
    private NetworkEventTransceiver transceiver = null;

    // Utilities:
    /**
     * Used to inform the server that the current user is trying to access the lobby.
     */
    private Requester<Response<VoidEventData>, JoinLobbyEventData> joinLobbyRequester = null;

    /**
     * Used to inform the server that the current user (expected to be the user that created the lobby) is trying to
     * start the game.
     */
    private Requester<Response<VoidEventData>, StartGameEventData> startGameRequester = null;

    /**
     * Used to inform the server that a game that has been paused should be resumed.
     */
    private Requester<Response<VoidEventData>, RestartGameEventData> restartGameRequester = null;

    /**
     * If the user exits the menu through the backToAvailableGamesButton, all other users need to be notified of this
     * event, since their respective list of players in the lobby has to be updated.
     */
    private Requester<Response<VoidEventData>, ExitLobbyEventData> exitLobbyRequester = null;

    /**
     * Receives the event relative to a new player entering the lobby.
     */
    private EventReceiver<PlayerHasJoinLobbyEventData> playerHasJoinLobbyReceiver = null;

    /**
     * Receives an event informing the client that the game has started. This means that the menu needs to be switched
     * to the main game's menu.
     */
    private EventReceiver<GameHasStartedEventData> gameHasStartedReceiver = null;

    /**
     * Receiver waiting for the event related to another player exiting the lobby. This is needed to update the list view
     * containing all online players' names.
     */
    private EventReceiver<PlayerHasExitLobbyEventData> playerHasExitLobbyReceiver = null;

    // Listeners:

    /**
     * Listener that automatically adds the newly entered player's name to the list view, upon arrival.
     */
    private final EventListener<PlayerHasJoinLobbyEventData> playerHasJoinLobbyListener = data -> {
        Platform.runLater(() -> {
            players.add(data.username());
        });
    };

    /**
     * Listener that automatically changes the game's layout to the full game menu, after the host decides to start the
     * game.
     */
    private final EventListener<GameHasStartedEventData> gameHasStartedListener = data -> {
        switchLayout(GameController.NAME);
    };

    /**
     * Removes a player from this menu's list view, if that player has just exited the lobby.
     */
    private final EventListener<PlayerHasExitLobbyEventData> playerHasExitLobbyListener = data -> {
        Platform.runLater(() -> {
            players.remove(data.username());
            System.out.println(players); // TODO: remove this
        });
    };

    /**
     * Helper function to encapsulate the act of starting a new game, using this class' appropriate requester.
     */
    @FXML
    private void startGame() {
        new Thread(new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    showResponse(startGameRequester.request(new StartGameEventData()));
                } catch (DisconnectedException e) {
                    showResponse(Response.failure("Disconnected!"));
                }

                return null;
            }
        }).start();
    }

    /**
     * Helper function to encapsulate the act of resuming a game, using this class' appropriate requester.
     */
    @FXML
    private void restartGame() {
        new Thread(new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    showResponse(restartGameRequester.request(new RestartGameEventData()));
                } catch (DisconnectedException e) {
                    showResponse(Response.failure("Disconnected!"));
                }

                return null;
            }
        }).start();
    }

    /**
     * Helper function to encapsulate the act of exiting the lobby, using this class' appropriate requester.
     * This makes the current user exit the lobby: all other players act independently.
     */
    @FXML
    private void backToAvailableGames() {
        new Thread(new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    Response response = exitLobbyRequester.request(new ExitLobbyEventData());
                    showResponse(response);

                    if (!response.isOk()) {
                        return null;
                    }
                } catch (DisconnectedException e) {
                    showResponse(Response.failure("Disconnected!"));
                    return null;
                }

                switchLayout(AvailableGamesMenuController.NAME);
                return null;
            }
        }).start();
    }

    /**
     * This method is called if the user requests to close the window in any way, and ends the process.
     */
    @FXML
    private void exit() {
        Platform.exit();
        System.exit(0);
    }

    /**
     * Set up the menu by adding the game name header, booting all receivers and requesters, and setting a cell
     * factory to the list view, in order to apply the correct styling and size to its objects.
     */
    @FXML
    private void initialize() {
        String gameName = (String) getValue("selectedgamename");
        gameNameLabel.setText(gameName);

        players = FXCollections.observableArrayList();
        playersListView.setItems(players);

        startGameButton.setManaged(false);
        restartGameButton.setManaged(false);

        boolean isOwner = getValue("selectedgameowner").equals(getValue("username"));
        if (isOwner) {
            if ((Boolean) getValue("isselectedgamestopped")) {
                restartGameButton.setManaged(true);
                restartGameButton.disableProperty().bind(Bindings.size(players).lessThan(2));
            } else {
                startGameButton.setManaged(true);
                startGameButton.disableProperty().bind(Bindings.size(players).lessThan(2));
            }
        }

        if (transceiver == null) {
            transceiver = (NetworkEventTransceiver) getValue("transceiver");

            joinLobbyRequester = Response.requester(transceiver, transceiver, new Object());
            startGameRequester = Response.requester(transceiver, transceiver, new Object());
            restartGameRequester = Response.requester(transceiver, transceiver, new Object());
            exitLobbyRequester = Response.requester(transceiver, transceiver, new Object());

            playerHasJoinLobbyReceiver = PlayerHasJoinLobbyEventData.castEventReceiver(transceiver);
            gameHasStartedReceiver = GameHasStartedEventData.castEventReceiver(transceiver);
            playerHasExitLobbyReceiver = PlayerHasExitLobbyEventData.castEventReceiver(transceiver);

            PlayerDisconnectedInternalEventData.castEventReceiver(transceiver).registerListener(data -> {
                transceiver = null;

                joinLobbyRequester = null;
                startGameRequester = null;
                restartGameRequester = null;
                exitLobbyRequester = null;

                playerHasJoinLobbyReceiver = null;
                gameHasStartedReceiver = null;
                playerHasExitLobbyReceiver = null;

                if (isCurrentLayout()) {
                    switchLayout(ConnectionMenuController.NAME);
                }
            });
        }

        joinLobbyRequester.registerAllListeners();
        startGameRequester.registerAllListeners();
        restartGameRequester.registerAllListeners();
        exitLobbyRequester.registerAllListeners();

        playerHasJoinLobbyReceiver.registerListener(playerHasJoinLobbyListener);
        gameHasStartedReceiver.registerListener(gameHasStartedListener);
        playerHasExitLobbyReceiver.registerListener(playerHasExitLobbyListener);

        playersListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<String> call(ListView<String> stringListView) {
                return new ListCell<>() {
                    private Optional<String> playerName = Optional.empty();
                    @Override
                    protected void updateItem(String player, boolean b) {
                        super.updateItem(player, b);

                        if (player == null || b) {
                            playerName = Optional.empty();
                            setGraphic(null);
                            return;
                        }

                        if (playerName.isPresent() && playerName.get().equals(player))
                            return;

                        playerName = Optional.of(player);
                        Label playerLabel = new Label(player);
                        playerLabel.setStyle("-fx-font-size: 26");

                        HBox gameHBox = new HBox(playerLabel);
                        gameHBox.setPadding(new Insets(30, 30, 30, 30));
                        gameHBox.getStyleClass().add("player");
                        gameHBox.setAlignment(Pos.CENTER);
                        setGraphic(gameHBox);
                    }
                };
            }
        });

        new Thread(new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    Response response = joinLobbyRequester.request(new JoinLobbyEventData(gameName));
                    showResponse(response);

                    if (!response.isOk()) {
                        switchLayout(AvailableGamesMenuController.NAME);
                        return null;
                    }
                } catch (DisconnectedException e) {
                    showResponse(Response.failure("Disconnected!"));
                }

                return null;
            }
        }).start();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void beforeSwitch() {
        if (transceiver != null) {
            joinLobbyRequester.unregisterAllListeners();
            startGameRequester.unregisterAllListeners();
            restartGameRequester.unregisterAllListeners();
            exitLobbyRequester.unregisterAllListeners();

            playerHasJoinLobbyReceiver.unregisterListener(playerHasJoinLobbyListener);
            gameHasStartedReceiver.unregisterListener(gameHasStartedListener);
            playerHasExitLobbyReceiver.unregisterListener(playerHasExitLobbyListener);
        }
    }
}
