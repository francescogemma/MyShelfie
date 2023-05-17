package it.polimi.ingsw.view.gui.controller;

import it.polimi.ingsw.event.NetworkEventTransceiver;
import it.polimi.ingsw.event.data.game.GameHasBeenCreatedEventData;
import it.polimi.ingsw.event.data.internal.PlayerDisconnectedInternalEventData;
import it.polimi.ingsw.view.displayable.DisplayablePlayer;
import it.polimi.ingsw.view.displayable.DisplayableScoreBoard;
import javafx.application.Platform;
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


public class ScoreboardMenuController extends Controller {
    public static final String NAME = "ScoreboardMenu";

    @FXML private Button backToAvailableGamesButton;
    @FXML private ListView<DisplayablePlayer> scoreBoardListView;

    // Data:
    private NetworkEventTransceiver transceiver = null;

    @FXML
    private void backToAvailableGames() {
        switchLayout(AvailableGamesMenuController.NAME);
    }

    @FXML
    private void exit() {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    private void initialize() {

        if (transceiver == null) {
            transceiver = getValue("transceiver");

            PlayerDisconnectedInternalEventData.castEventReceiver(transceiver).registerListener(data -> {
                transceiver = null;

                if (isCurrentLayout()) {
                    switchLayout(ConnectionMenuController.NAME);
                }
            });
        }

        scoreBoardListView.getItems().setAll(((DisplayableScoreBoard) getValue("scoreboard"))
            .getDisplayablePlayers());

        scoreBoardListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<DisplayablePlayer> call(ListView<DisplayablePlayer> displayablePlayerListView) {
                return new ListCell<>() {
                    private Optional<DisplayablePlayer> availablePlayer = Optional.empty();
                    @Override
                    protected void updateItem(DisplayablePlayer player, boolean b) {
                        super.updateItem(player, b);
                        if (player == null || b) {
                            availablePlayer = Optional.empty();
                            setGraphic(null);
                            return;
                        }
                        
                        if (availablePlayer.isPresent() && availablePlayer.get().equals(player)) {
                            return;
                        }

                        availablePlayer = Optional.of(player);

                        // main box
                        HBox playerHBox = new HBox();
                        playerHBox.setPadding(new Insets(30, 30, 30, 30));
                        playerHBox.getStyleClass().add("playerBox");

                        // score position
                        Label playerPositionLabel = new Label("[#" + player.getPosition() + "] ");
                        playerPositionLabel.setStyle("-fx-font-size: 26");

                        // player name
                        Label playerNameLabel = new Label(player.getName());
                        playerNameLabel.setStyle("");

                        if (!player.isConnected()) {
                            playerNameLabel.setStyle("-fx-font-size: 26; -fx-text-fill: #808080");
                        } else if (player.isClientPlayer()) {
                            playerNameLabel.setStyle("-fx-font-size: 26; -fx-text-fill: #ff0000");
                        } else if (player.isWinner()) {
                            playerNameLabel.setStyle("-fx-font-size: 26; -fx-text-fill: #00FF00");
                        } else {
                            playerNameLabel.setStyle("-fx-font-size: 26");
                        }

                        // box for left-side fields
                        HBox leftBox = new HBox();
                        HBox.setHgrow(leftBox, Priority.ALWAYS);
                        leftBox.setAlignment(Pos.CENTER);
                        leftBox.getChildren().add(playerPositionLabel);
                        leftBox.getChildren().add(playerNameLabel);

                        // points
                        Label pointsLabel = new Label("Score: " + player.getPoints());
                        pointsLabel.setStyle("-fx-font-size: 26");
                        pointsLabel.setPadding(new Insets(0, 0, 0, 10));
                        pointsLabel.setAlignment(Pos.CENTER_RIGHT);

                        // additional points
                        int additionalPoints = player.getAdditionalPoints();
                        Label additionalPointsLabel = new Label(" + " + additionalPoints);
                        additionalPointsLabel.setStyle("-fx-font-size: 26; -fx-text-fill: #00FF00");
                        additionalPointsLabel.setVisible(additionalPoints > 0);
                        additionalPointsLabel.setPadding(new Insets(0, 0, 0, 10));
                        additionalPointsLabel.setAlignment(Pos.CENTER_RIGHT);

                        // box for right-side fields
                        HBox rightBox = new HBox();
                        HBox.setHgrow(rightBox, Priority.ALWAYS);
                        rightBox.setAlignment(Pos.CENTER);
                        rightBox.getChildren().add(pointsLabel);
                        rightBox.getChildren().add(additionalPointsLabel);

                        // finish it up
                        setGraphic(playerHBox);
                        playerHBox.getChildren().addAll(leftBox, rightBox);
                    }
                };
            }
        });
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void beforeSwitch() {

    }
}
