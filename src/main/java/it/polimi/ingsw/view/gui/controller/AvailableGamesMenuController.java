package it.polimi.ingsw.view.gui.controller;

import it.polimi.ingsw.controller.Response;
import it.polimi.ingsw.event.NetworkEventTransceiver;
import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.data.VoidEventData;
import it.polimi.ingsw.event.data.client.*;
import it.polimi.ingsw.event.data.game.GameHasBeenCreatedEventData;
import it.polimi.ingsw.event.data.game.GameIsNoLongerAvailableEventData;
import it.polimi.ingsw.event.data.internal.PlayerDisconnectedInternalEventData;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventListener;
import it.polimi.ingsw.networking.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Callback;

import java.util.List;
import java.util.Optional;

public class AvailableGamesMenuController extends Controller {
    @FXML private Label loggedUsername;
    @FXML private Button createNewGameButton;
    @FXML private TextField gameNameTextField;
    @FXML private Button backToLoginButton;
    @FXML private HBox noAvailableGamesHBox;
    @FXML private ListView<GameHasBeenCreatedEventData.AvailableGame> availableGamesListView;

    public static final String NAME = "AvailableGamesMenu";

    // Data:
    private NetworkEventTransceiver transceiver = null;

    // Utilities:
    private Requester<Response<VoidEventData>, CreateNewGameEventData> responseCreateNewGameEventDataRequester;
    private Requester<Response<VoidEventData>, LogoutEventData> responseLogoutEventDataRequester;

    private CastEventReceiver<GameHasBeenCreatedEventData> gameHasBeenCreatedEventDataCastEventReceiver;
    private CastEventReceiver<GameIsNoLongerAvailableEventData> gameIsNoLongerAvailableEventDataCastEventReceiver;

    // Listeners:
    private final EventListener<GameHasBeenCreatedEventData> gameHasBeenCreatedListener = data -> {
        Platform.runLater(() -> {
            availableGamesListView.getItems().addAll(data.getNames());
            manageNoAvailableGames();
        });
    };

    private final EventListener<GameIsNoLongerAvailableEventData> gameIsNoLongerAvailableListener = data -> {
        Platform.runLater(() -> {
            List<GameHasBeenCreatedEventData.AvailableGame> list = availableGamesListView.getItems();

            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).name().equals(data.gameName())) {
                    list.remove(i);
                    break;
                }
            }
            manageNoAvailableGames();
        });
    };

    private void manageNoAvailableGames() {
        if (availableGamesListView.getItems().isEmpty()) {
            availableGamesListView.setVisible(false);
            noAvailableGamesHBox.setVisible(true);
        } else {
            availableGamesListView.setVisible(true);
            noAvailableGamesHBox.setVisible(false);
        }
    }

    @FXML
    private void initialize() {
        loggedUsername.setText((String) getScene().getProperties().get("username"));

        if(transceiver == null) {
            transceiver = (NetworkEventTransceiver) getScene().getProperties().get("transceiver");
            responseLogoutEventDataRequester = Response.requester(transceiver, transceiver, new Object());
            responseCreateNewGameEventDataRequester = Response.requester(transceiver, transceiver, new Object());

            gameHasBeenCreatedEventDataCastEventReceiver = GameHasBeenCreatedEventData.castEventReceiver(transceiver);
            gameIsNoLongerAvailableEventDataCastEventReceiver = GameIsNoLongerAvailableEventData.castEventReceiver(transceiver);

            PlayerDisconnectedInternalEventData.castEventReceiver(transceiver).registerListener(data -> {
                transceiver = null;
                gameHasBeenCreatedEventDataCastEventReceiver = null;
                gameIsNoLongerAvailableEventDataCastEventReceiver = null;
                responseCreateNewGameEventDataRequester = null;
                responseLogoutEventDataRequester = null;

                if(isCurrentLayout()) {
                    switchLayout(ConnectionMenuController.NAME);
                }
            });
        }

        gameHasBeenCreatedEventDataCastEventReceiver.registerListener(gameHasBeenCreatedListener);
        gameIsNoLongerAvailableEventDataCastEventReceiver.registerListener(gameIsNoLongerAvailableListener);

        responseCreateNewGameEventDataRequester.registerAllListeners();
        responseLogoutEventDataRequester.registerAllListeners();

        availableGamesListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<GameHasBeenCreatedEventData.AvailableGame> call(ListView<GameHasBeenCreatedEventData.AvailableGame> stringListView) {
                return new ListCell<>() {
                    private Optional<GameHasBeenCreatedEventData.AvailableGame> availableGame = Optional.empty();
                    @Override
                    protected void updateItem(GameHasBeenCreatedEventData.AvailableGame game, boolean b) {
                        super.updateItem(game, b);
                        if (game == null || b) {
                            availableGame = Optional.empty();
                            setGraphic(null);
                            return;
                        }

                        if (availableGame.isPresent() && availableGame.get().equals(game)) {
                            return;
                        }

                        availableGame = Optional.of(game);
                        HBox gameHBox = new HBox();
                        Label gameNameLabel = new Label(game.name());
                        gameNameLabel.setStyle("-fx-font-size: 26");
                        HBox labelHBox = new HBox(gameNameLabel);
                        HBox.setHgrow(labelHBox, Priority.ALWAYS);
                        labelHBox.setAlignment(Pos.CENTER);
                        gameHBox.setPadding(new Insets(30, 30, 30, 30));
                        gameHBox.getStyleClass().add("availableGame");
                        setGraphic(gameHBox);

                        Button joinButton;
                        String buttonLabel;
                        String nextLayoutName;

                        if (game.isStarted() && !game.isStopped()) {
                            buttonLabel = "Join game";
                            nextLayoutName = GameController.NAME;
                        } else {
                            buttonLabel = "Join lobby";
                            nextLayoutName = GameLobbyMenuController.NAME;
                        }

                        joinButton = new Button(buttonLabel);
                        joinButton.setOnAction(event -> {
                            setProperty("selectedgamename", game.name());
                            setProperty("selectedgameowner", game.owner());
                            setProperty("isselectedgamestopped", game.isStopped());
                            switchLayout(nextLayoutName);
                        });

                        gameHBox.getChildren().addAll(labelHBox, joinButton);
                    }
                };
            }
        });

        transceiver.broadcast(new PlayerHasJoinMenuEventData());
    }

    @FXML
    private void backToLogin() {
        new Thread(new Task<>() {
            @Override
            protected Void call() {
                Response response;
                try {
                    response = responseLogoutEventDataRequester.request(new LogoutEventData());

                    if (response.isOk()) {
                        switchLayout(UserLoginMenuController.NAME);
                    } else {
                        showResponse(response);
                    }
                } catch (DisconnectedException e) {
                    showResponse(Response.failure("Disconnected"));
                }
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
    private void createNewGame() {
        new Thread(new Task<>() {
            @Override
            protected Void call() {
                try {
                    String gameName = gameNameTextField.getText();
                    Response response = responseCreateNewGameEventDataRequester.request(new CreateNewGameEventData(gameName));

                    showResponse(response);
                } catch (DisconnectedException e) {
                    showResponse(Response.failure("Disconnected"));
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
            gameIsNoLongerAvailableEventDataCastEventReceiver.unregisterListener(gameIsNoLongerAvailableListener);
            gameHasBeenCreatedEventDataCastEventReceiver.unregisterListener(gameHasBeenCreatedListener);
            responseLogoutEventDataRequester.unregisterAllListeners();
            responseCreateNewGameEventDataRequester.unregisterAllListeners();
        }
    }
}
