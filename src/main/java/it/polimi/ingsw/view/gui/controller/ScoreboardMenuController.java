package it.polimi.ingsw.view.gui.controller;

import it.polimi.ingsw.event.NetworkEventTransceiver;
import it.polimi.ingsw.event.data.internal.PlayerDisconnectedInternalEventData;
import it.polimi.ingsw.view.displayable.DisplayablePlayer;
import it.polimi.ingsw.view.displayable.DisplayableScoreBoard;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;


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
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void beforeSwitch() {

    }
}
