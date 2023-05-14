package it.polimi.ingsw.view.gui.controller;

import it.polimi.ingsw.controller.Response;
import it.polimi.ingsw.event.NetworkEventTransceiver;
import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.data.client.ExitLobbyEventData;
import it.polimi.ingsw.event.data.client.JoinLobbyEventData;
import it.polimi.ingsw.event.data.client.RestartGameEventData;
import it.polimi.ingsw.event.data.client.StartGameEventData;
import it.polimi.ingsw.event.data.game.GameHasBeenCreatedEventData;
import it.polimi.ingsw.event.data.game.GameHasStartedEventData;
import it.polimi.ingsw.event.data.game.PlayerHasExitLobbyEventData;
import it.polimi.ingsw.event.data.game.PlayerHasJoinLobbyEventData;
import it.polimi.ingsw.event.data.internal.PlayerDisconnectedInternalEventData;
import it.polimi.ingsw.event.receiver.EventListener;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.networking.DisconnectedException;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
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
import javafx.scene.layout.Priority;
import javafx.util.Callback;

import java.util.Optional;

public class GameLobbyMenuController extends Controller {
    @FXML private Label gameNameLabel;
    @FXML private Button backToAvailableGamesButton;
    @FXML private Button startGameButton;
    @FXML private Button restartGameButton;
    private ObservableList<String> players;
    @FXML private ListView<String> playersListView;

    public static final String NAME = "GameLobbyMenu";

    // Data:
    private NetworkEventTransceiver transceiver = null;

    // Utilities:
    private Requester<Response, JoinLobbyEventData> joinLobbyRequester = null;
    private Requester<Response, StartGameEventData> startGameRequester = null;
    private Requester<Response, RestartGameEventData> restartGameRequester = null;
    private Requester<Response, ExitLobbyEventData> exitLobbyRequester = null;

    private EventReceiver<PlayerHasJoinLobbyEventData> playerHasJoinLobbyReceiver = null;
    private EventReceiver<GameHasStartedEventData> gameHasStartedReceiver = null;
    private EventReceiver<PlayerHasExitLobbyEventData> playerHasExitLobbyReceiver = null;

    // Listeners:
    private final EventListener<PlayerHasJoinLobbyEventData> playerHasJoinLobbyListener = data -> {
        Platform.runLater(() -> {
            players.add(data.getUsername());
        });
    };

    private final EventListener<GameHasStartedEventData> gameHasStartedListener = data -> {
        switchLayout(GameController.NAME);
    };

    private final EventListener<PlayerHasExitLobbyEventData> playerHasExitLobbyListener = data -> {
        Platform.runLater(() -> {
            players.remove(data.username());
            System.out.println(players); // TODO: remove this
        });
    };

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

    @FXML
    private void exit() {
        Platform.exit();
        System.exit(0);
    }

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
