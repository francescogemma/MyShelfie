package it.polimi.ingsw.view.gui.controller;

import it.polimi.ingsw.controller.Response;
import it.polimi.ingsw.event.NetworkEventTransceiver;
import it.polimi.ingsw.event.data.VoidEventData;
import it.polimi.ingsw.event.data.internal.PlayerDisconnectedInternalEventData;
import it.polimi.ingsw.event.receiver.EventListener;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.model.board.Board;
import it.polimi.ingsw.model.board.BoardView;
import it.polimi.ingsw.model.bookshelf.BookshelfMask;
import it.polimi.ingsw.model.bookshelf.BookshelfMaskSet;
import it.polimi.ingsw.model.bookshelf.BookshelfView;
import it.polimi.ingsw.model.bookshelf.Shelf;
import it.polimi.ingsw.model.goal.PersonalGoal;
import it.polimi.ingsw.model.tile.Tile;
import it.polimi.ingsw.model.tile.TileColor;
import it.polimi.ingsw.model.tile.TileVersion;
import it.polimi.ingsw.networking.DisconnectedException;
import it.polimi.ingsw.utils.Coordinate;
import it.polimi.ingsw.view.displayable.DisplayableGoal;
import it.polimi.ingsw.view.popup.PopUp;
import it.polimi.ingsw.view.popup.PopUpQueue;
import it.polimi.ingsw.view.tui.AvailableGamesMenuLayout;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import it.polimi.ingsw.event.data.game.*;
import it.polimi.ingsw.event.data.client.*;
import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.model.game.Player;

import java.util.*;

import it.polimi.ingsw.view.displayable.DisplayablePlayer;
import it.polimi.ingsw.model.goal.CommonGoal;
import it.polimi.ingsw.model.game.IllegalFlowException;
import it.polimi.ingsw.view.displayable.DisplayableScoreBoard;
import javafx.util.Callback;
import javafx.util.Duration;

public class GameController extends Controller {
    public static final String NAME = "Game";

    // Layout:
    // Left panel:
    @FXML private ImageView firstCommonGoalImageView;
    @FXML private ImageView firstScoringToken;
    @FXML private ImageView secondCommonGoalImageView;
    @FXML private ImageView secondScoringToken;
    @FXML private ImageView personalGoalImageView;

    @FXML private Label gamePopUpLabel;
    @FXML private VBox gamePopUpMessageBackground;

    private static final double COMMON_GOAL_WIDTH = 1385.0;
    private static final double COMMON_GOAL_HEIGHT = 913.0;
    private static final double COMMON_GOAL_ASPECT_RATIO = COMMON_GOAL_WIDTH / COMMON_GOAL_HEIGHT;
    private static final double TOKEN_COMMON_GOAL_RESIZE_FACTOR = 315.805 / COMMON_GOAL_WIDTH;
    private static final double TOKEN_COMMON_GOAL_ANGLE = -7.6426;
    private static final double TOKEN_COMMON_GOAL_UP_MARGIN_FACTOR = 39.5 / COMMON_GOAL_WIDTH;
    private static final double TOKEN_COMMON_GOAL_RIGHT_MARGIN_FACTOR = 326.5 / COMMON_GOAL_WIDTH;

    private void resizeCommonGoalToken(ImageView scoringToken) {
        double realWidth = Math.min(firstCommonGoalImageView.getFitWidth(), firstCommonGoalImageView.getFitHeight() * COMMON_GOAL_ASPECT_RATIO);

        scoringToken.setFitWidth(realWidth * TOKEN_COMMON_GOAL_RESIZE_FACTOR);
        scoringToken.setFitHeight(realWidth * TOKEN_COMMON_GOAL_RESIZE_FACTOR);
        scoringToken.setTranslateY(-realWidth * TOKEN_COMMON_GOAL_UP_MARGIN_FACTOR);
        scoringToken.setTranslateX(realWidth * TOKEN_COMMON_GOAL_RIGHT_MARGIN_FACTOR);
        scoringToken.setRotate(TOKEN_COMMON_GOAL_ANGLE);
    }

    private void setCommonGoalImageViewsListeners() {
        firstCommonGoalImageView.fitWidthProperty().addListener((observable, oldValue, newValue) -> resizeCommonGoalToken(firstScoringToken));
        firstCommonGoalImageView.fitHeightProperty().addListener((observable, oldValue, newValue) -> resizeCommonGoalToken(firstScoringToken));

        secondCommonGoalImageView.fitWidthProperty().addListener((observable, oldValue, newValue) -> resizeCommonGoalToken(secondScoringToken));
        secondCommonGoalImageView.fitHeightProperty().addListener((observable, oldValue, newValue) -> resizeCommonGoalToken(secondScoringToken));
    }

    private void setTokenImage(int points, ImageView tokenImageView) {
        Platform.runLater(() -> {
            if(points == 0) {
                tokenImageView.setVisible(false);
            } else if(points >= 2 && points <= 8) {
                String path = "/scoring tokens/scoring_%s.jpg".formatted(points);
                tokenImageView.setImage(new Image(path));
            } else {
                throw new IllegalArgumentException("Invalid token value");
            }
        });
    }

    private void populateCommonGoalsPointsTextBoxes() {
        setTokenImage((commonGoals[0].getPointStack().isEmpty()) ? 0 :
            commonGoals[0].getPointStack().get(commonGoals[0].getPointStack().size() - 1), firstScoringToken);
        setTokenImage((commonGoals[1].getPointStack().isEmpty()) ? 0 :
            commonGoals[1].getPointStack().get(commonGoals[1].getPointStack().size() - 1), secondScoringToken);
    }

    private void populateGoalsPanel() {
        Platform.runLater(() -> {
            firstCommonGoalImageView.setImage(ImageProvider.getInstance().getCommonGoal(commonGoals[0].getIndex()));
            Tooltip tooltip = new Tooltip(commonGoals[0].getDescription());
            tooltip.setShowDelay(Duration.seconds(0));
            Tooltip.install(firstCommonGoalImageView, tooltip);

            secondCommonGoalImageView.setImage(ImageProvider.getInstance().getCommonGoal(commonGoals[1].getIndex()));
            tooltip = new Tooltip(commonGoals[1].getDescription());
            tooltip.setShowDelay(Duration.seconds(0));
            Tooltip.install(secondCommonGoalImageView, tooltip);

            personalGoalImageView.setImage(ImageProvider.getInstance().getPersonalGoal(this.personalGoal.getIndex()));
        });

        populateCommonGoalsPointsTextBoxes();
    }

    private void displayGoal(DisplayableGoal displayableGoal) {
        isDisplayingCommonGoal = true;

        // TODO: Blur every player except for the one who has completed the goal
        scoreBoard.addAdditionalPoints(displayableGoal.getPlayerName(), displayableGoal.getPoints());

        Platform.runLater(() -> {
            scoreBoardListView.getItems().setAll(scoreBoard.getDisplayablePlayers());

            selectedBookshelfIndex = scoreBoard.getDisplayablePlayer(displayableGoal.getPlayerName())
                .getOriginalIndex();

            previousBookshelfButton.setDisable(true);
            nextBookshelfButton.setDisable(true);
        });

        populateBookshelfPanel();

        populateBookshelfPanelWithMask(displayableGoal.getMaskSet());
    }

    private void resetLayoutAfterDisplayingGoal() {
        isDisplayingCommonGoal = false;

        for (DisplayablePlayer displayablePlayer : scoreBoard.getDisplayablePlayers()) {
            // TODO: Unblur every player
            scoreBoard.sumPoints(displayablePlayer.getName());
        }

        Platform.runLater(() -> {
            scoreBoardListView.getItems().setAll(scoreBoard.getDisplayablePlayers());
        });

        populateBookshelfPanel();
    }

    // Board (center panel):
    @FXML private Label turnLabel;
    @FXML private Pane boardBackground;
    @FXML private GridPane boardGridPane;
    @FXML private ImageView gameBoardImageView;
    @FXML private Button stopGameButton;
    @FXML private Button exitButton;
    private static final double FULL_BOARD_IMAGE_SIZE = 2965.0;
    private static final double BOARD_SIZE = 2661.0;
    private static final double BOARD_RESIZE_FACTOR = BOARD_SIZE / FULL_BOARD_IMAGE_SIZE;
    private static final double BOARD_RIGHT_MARGIN_FACTOR = 13.0 / FULL_BOARD_IMAGE_SIZE;
    private Button[][] boardButtons = new Button[Board.BOARD_ROWS][Board.COLUMN_BOARDS];

    private void resizeBoard() {
        double realWidth = Math.min(gameBoardImageView.getFitWidth(), gameBoardImageView.getFitHeight());
        double realHeight = Math.min(gameBoardImageView.getFitHeight(), gameBoardImageView.getFitWidth());

        boardGridPane.setMaxWidth(realWidth * BOARD_RESIZE_FACTOR);
        boardGridPane.setMaxHeight(realHeight * BOARD_RESIZE_FACTOR);
        boardGridPane.setPrefWidth(realWidth * BOARD_RESIZE_FACTOR);
        boardGridPane.setPrefHeight(realHeight * BOARD_RESIZE_FACTOR);
        boardGridPane.setTranslateX(-realWidth * BOARD_RIGHT_MARGIN_FACTOR);
        boardGridPane.setHgap(boardGridPane.getWidth() * (36.0 / BOARD_SIZE));
        boardGridPane.setVgap(boardGridPane.getHeight() * (36.0 / BOARD_SIZE));

        boardBackground.setMaxWidth(realWidth * 1.12);
        boardBackground.setMaxHeight(realHeight * 1.12);
        boardBackground.setPrefWidth(realWidth * 1.12);
        boardBackground.setPrefHeight(realHeight * 1.12);

        turnLabel.setTranslateY(-realWidth * 1.12 / 2 - turnLabel.getHeight() / 2 - 5);
    }

    private void setBoardImageViewListener() {
        gameBoardImageView.fitWidthProperty().addListener((observable, oldValue, newValue) -> resizeBoard());
        gameBoardImageView.fitHeightProperty().addListener((observable, oldValue, newValue) -> resizeBoard());
    }

    private void populateTurnTextBox() {
        Platform.runLater(() -> {
            turnLabel.setText(
                "It's " + (scoreBoard.isClientPlaying() ? "your" : scoreBoard.getPlayingPlayer().getName() + "'s") + " turn"
            );

            turnLabel.setStyle("-fx-text-fill: %s".formatted(
                scoreBoard.isClientPlaying() ? "#a51c18" : "#403f28"
            ));
        });
    }

    private void populateBoard() {
        Platform.runLater(() -> {
            for (int row = 0; row < Board.BOARD_ROWS; row++) {
                for (int column = 0; column < Board.COLUMN_BOARDS; column++) {
                    Coordinate boardCoordinate = new Coordinate(row, column);

                    Tile tile = board.tileAt(boardCoordinate);

                    if (tile != null) {
                        ((ImageView) boardButtons[row][column].getGraphic()).setImage(
                                ImageProvider.getInstance().getTile(tile)
                        );
                        boardButtons[row][column].setVisible(true);

                        boardButtons[row][column].getStyleClass().clear();
                        boardButtons[row][column].getStyleClass().add("baseTile");

                        if (board.getSelectedCoordinates().contains(boardCoordinate)) {
                            boardButtons[row][column].getStyleClass().add("selectedTile");
                            ((ImageView) boardButtons[row][column].getGraphic()).fitWidthProperty()
                                    .bind(boardButtons[row][column].widthProperty().subtract(8));
                            ((ImageView) boardButtons[row][column].getGraphic()).fitHeightProperty()
                                    .bind(boardButtons[row][column].heightProperty().subtract(8));
                        } else {
                            ((ImageView) boardButtons[row][column].getGraphic()).fitWidthProperty()
                                    .bind(boardButtons[row][column].widthProperty());
                            ((ImageView) boardButtons[row][column].getGraphic()).fitHeightProperty()
                                    .bind(boardButtons[row][column].heightProperty());
                        }

                        if (scoreBoard.isClientPlaying() && (board.getSelectableCoordinate().contains(boardCoordinate)
                            || board.getSelectedCoordinates().contains(boardCoordinate))) {
                            boardButtons[row][column].setMouseTransparent(false);
                        } else {
                            boardButtons[row][column].setMouseTransparent(true);
                            boardButtons[row][column].getStyleClass().add("unselectableTile");
                        }
                    } else {
                        boardButtons[row][column].setVisible(false);
                    }
                }
            }
        });
    }

    // Right panel:
    // Score board:
    @FXML private ListView<DisplayablePlayer> scoreBoardListView;

    // Bookshelves:
    @FXML private Label selectedBookshelfLabel;
    @FXML private GridPane bookshelfGridPane;
    @FXML private ImageView bookshelfImageView;
    @FXML private GridPane bookshelfColumnSelectorGridPane;
    @FXML private Button nextBookshelfButton;
    @FXML private Button previousBookshelfButton;
    @FXML private Button bookshelfColumn1Button;
    @FXML private Button bookshelfColumn2Button;
    @FXML private Button bookshelfColumn3Button;
    @FXML private Button bookshelfColumn4Button;
    @FXML private Button bookshelfColumn5Button;
    private static final double FULL_BOOKSHELF_IMAGE_WIDTH = 1414.0;
    private static final double FULL_BOOKSHELF_IMAGE_HEIGHT = 1411.0;
    private static final double BOOKSHELF_WIDTH = 1074.0;
    private static final double BOOKSHELF_HEIGHT = 1144.0;
    private static final double BOOKSHELF_ASPECT_RATIO = FULL_BOOKSHELF_IMAGE_WIDTH / FULL_BOOKSHELF_IMAGE_HEIGHT;
    private static final double BOOKSHELF_WIDTH_RESIZE_FACTOR = BOOKSHELF_WIDTH / FULL_BOOKSHELF_IMAGE_WIDTH;
    private static final double BOOKSHELF_HEIGHT_RESIZE_FACTOR = BOOKSHELF_HEIGHT / FULL_BOOKSHELF_IMAGE_HEIGHT;
    private static final double BOOKSHELF_UP_MARGIN_FACTOR = 38.5 / FULL_BOOKSHELF_IMAGE_HEIGHT;
    private final Button[][] bookshelfButtons = new Button[BookshelfView.ROWS][BookshelfView.COLUMNS];
    private final Button[] bookshelfColumnsButton = new Button[BookshelfView.COLUMNS];

    private void resizeBookshelf() {
        double realWidth = Math.min(bookshelfImageView.getFitWidth(), bookshelfImageView.getFitHeight() * BOOKSHELF_ASPECT_RATIO);
        double realHeight = Math.min(bookshelfImageView.getFitHeight(), bookshelfImageView.getFitWidth() / BOOKSHELF_ASPECT_RATIO);

        bookshelfGridPane.setMaxWidth(realWidth * BOOKSHELF_WIDTH_RESIZE_FACTOR);
        bookshelfGridPane.setMaxHeight(realHeight * BOOKSHELF_HEIGHT_RESIZE_FACTOR);
        bookshelfGridPane.setPrefWidth(realWidth * BOOKSHELF_WIDTH_RESIZE_FACTOR);
        bookshelfGridPane.setPrefHeight(realHeight * BOOKSHELF_HEIGHT_RESIZE_FACTOR);
        bookshelfGridPane.setTranslateY(-realHeight * BOOKSHELF_UP_MARGIN_FACTOR);
        bookshelfGridPane.setHgap(bookshelfGridPane.getWidth() * (61.0 / BOOKSHELF_WIDTH));
        bookshelfGridPane.setVgap(bookshelfGridPane.getHeight() * (30.0 / BOOKSHELF_HEIGHT));

        bookshelfColumnSelectorGridPane.setMaxWidth(realWidth * BOOKSHELF_WIDTH_RESIZE_FACTOR);
        bookshelfColumnSelectorGridPane.setMaxHeight(realHeight * BOOKSHELF_HEIGHT_RESIZE_FACTOR);
        bookshelfColumnSelectorGridPane.setPrefWidth(realWidth * BOOKSHELF_WIDTH_RESIZE_FACTOR);
        bookshelfColumnSelectorGridPane.setPrefHeight(realHeight * BOOKSHELF_HEIGHT_RESIZE_FACTOR);
        bookshelfColumnSelectorGridPane.setTranslateY(-realHeight * BOOKSHELF_UP_MARGIN_FACTOR);
        bookshelfColumnSelectorGridPane.setHgap(bookshelfColumnSelectorGridPane.getWidth() * (61.0 / BOOKSHELF_WIDTH));
    }

    private void setBookshelfImageViewListener() {
        bookshelfImageView.fitWidthProperty().addListener((observable, oldValue, newValue) -> resizeBookshelf());
        bookshelfImageView.fitHeightProperty().addListener((observable, oldValue, newValue) -> resizeBookshelf());
    }

    private void populateBookshelfPanel() {
        Platform.runLater(() -> {
            previousBookshelfButton.setDisable(selectedBookshelfIndex == 0);

            nextBookshelfButton.setDisable(selectedBookshelfIndex == bookshelves.size() - 1);

            for (int column = 0; column < BookshelfView.COLUMNS; column++) {
                bookshelfColumnsButton[column].setDisable(!scoreBoard.isClientPlaying() ||
                    selectedBookshelfIndex != scoreBoard.getClientPlayer().getOriginalIndex());
            }

            for (int row = 0; row < BookshelfView.ROWS; row++) {
                for (int column = 0; column < BookshelfView.COLUMNS; column++) {
                    bookshelfButtons[row][column].setOpacity(1.d);

                    Tile tile = bookshelves.get(selectedBookshelfIndex)
                        .getTileAt(Shelf.getInstance(row, column));

                    if (tile == Tile.getInstance(TileColor.EMPTY, TileVersion.FIRST)) {
                        bookshelfButtons[row][column].setVisible(false);
                    } else {
                        ((ImageView) bookshelfButtons[row][column].getGraphic()).setImage(
                                ImageProvider.getInstance().getTile(tile)
                        );
                        bookshelfButtons[row][column].setVisible(true);
                    }
                }
            }

            selectedBookshelfLabel.setText(scoreBoard.getDisplayablePlayer(selectedBookshelfIndex).getName());
        });
    }

    private void populateBookshelfPanelWithMask(BookshelfMaskSet maskSet) {
        Platform.runLater(() -> {
            for (int row = 0; row < BookshelfView.ROWS; row++) {
                for (int column = 0; column < BookshelfView.COLUMNS; column++) {
                    bookshelfButtons[row][column].setOpacity(0.3d);
                }
            }

            // TODO: Display the different masks which led to goal completion
            int count = 0;
            for (BookshelfMask mask : maskSet.getBookshelfMasks()) {
                for (Shelf shelf : mask.getShelves()) {
                    bookshelfButtons[shelf.getRow()][shelf.getColumn()].setOpacity(1.0d);
                }

                count++;
            }

            previousBookshelfButton.setDisable(true);
            nextBookshelfButton.setDisable(true);
        });
    }

    private Button craftTileButton(boolean isMouseTransparent) {
        Button tileButton = new Button();

        tileButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        tileButton.setMinSize(0, 0);
        GridPane.setFillWidth(tileButton, true);
        GridPane.setFillHeight(tileButton, true);

        ImageView imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.fitWidthProperty().bind(tileButton.widthProperty());
        imageView.fitHeightProperty().bind(tileButton.heightProperty());
        tileButton.setGraphic(imageView);

        tileButton.getStyleClass().add("baseTile");

        tileButton.setMouseTransparent(isMouseTransparent);

        tileButton.setVisible(false);

        return tileButton;
    }

    // Data:
    private NetworkEventTransceiver transceiver = null;
    private PersonalGoal personalGoal;
    private CommonGoal[] commonGoals;
    private DisplayableScoreBoard scoreBoard;
    private String gameName;
    private int selectedBookshelfIndex;
    private PopUp waitingForReconnectionsPopUp;
    private List<BookshelfView> bookshelves;
    private BoardView board;

    private boolean isDisplayingCommonGoal;

    private boolean gameHasBeenStopped;

    // Utilities:
    private Requester<Response<VoidEventData>, JoinGameEventData> joinGameRequester = null;
    private Requester<Response<VoidEventData>, SelectTileEventData> selectTileRequester = null;
    private Requester<Response<VoidEventData>, DeselectTileEventData> deselectTileRequester = null;
    private Requester<Response<VoidEventData>, InsertTileEventData> insertTileRequester = null;
    private Requester<Response<VoidEventData>, PauseGameEventData> pauseGameRequester = null;
    private Requester<Response<VoidEventData>, PlayerExitGame> playerExitGameRequester = null;

    private EventReceiver<InitialGameEventData> initialGameReceiver = null;
    private EventReceiver<PersonalGoalSetEventData> personalGoalSetReceiver = null;
    private EventReceiver<PlayerHasJoinGameEventData> playerHasJoinGameReceiver = null;
    private EventReceiver<BoardChangedEventData> boardChangedReceiver = null;
    private EventReceiver<BookshelfHasChangedEventData> bookshelfHasChangedReceiver = null;
    private EventReceiver<CurrentPlayerChangedEventData> currentPlayerChangedReceiver = null;
    private EventReceiver<CommonGoalCompletedEventData> commonGoalCompletedReceiver = null;
    private EventReceiver<FirstFullBookshelfEventData> firstFullBookshelfReceiver = null;
    private EventReceiver<GameOverEventData> gameOverReceiver = null;
    private EventReceiver<PlayerHasDisconnectedEventData> playerHasDisconnectedReceiver = null;
    private EventReceiver<GameHasBeenPauseEventData> gameHasBeenPauseReceiver = null;
    private EventReceiver<GameHasBeenStoppedEventData> gameHasBeenStoppedReceiver = null;

    private PopUpQueue popUpQueue;

    // Listeners:
    private final EventListener<InitialGameEventData> initialGameListener = data -> {
        commonGoals = data.gameView().getCommonGoals();

        for (int i = 0; i < data.gameView().getPlayers().size(); i++) {
            scoreBoard.addDisplayablePlayer(data.gameView().getPlayers().get(i), i);
        }

        try {
            scoreBoard.setPlayingPlayerOriginalIndex(data.gameView().getCurrentPlayer().getUsername());
        } catch (IllegalFlowException e) {
            throw new IllegalStateException("It should be always possible to retrieve playing player");
        }

        board = data.gameView().getBoard();

        gameName = data.gameView().getName();

        selectedBookshelfIndex = scoreBoard.getClientPlayer().getOriginalIndex();

        bookshelves = new ArrayList<>(data.gameView().getPlayers().stream().map(Player::getBookshelf)
                .toList());

        if (data.gameView().isWaitingForReconnections()) {
            waitForReconnections();
        }

        if (data.gameView().isStopped()) {
            stopGame();
        }

        populateTurnTextBox();

        populateBoard();

        populateBookshelfPanel();

        Platform.runLater(() -> {
            scoreBoardListView.getItems().setAll(scoreBoard.getDisplayablePlayers());

            stopGameButton.setDisable(false);
            if (data.gameView().getOwner()
                .equals(scoreBoard.getClientPlayer().getName())) {

                stopGameButton.setManaged(true);
                stopGameButton.setVisible(true);
            }

            exitButton.setDisable(false);
        });
    };

    private final EventListener<PersonalGoalSetEventData> personalGoalSetListener = data -> {
        personalGoal = PersonalGoal.fromIndex(data.personalGoal());

        populateGoalsPanel();
    };

    private final EventListener<PlayerHasJoinGameEventData> playerHasJoinGameListener = data -> {
        scoreBoard.setConnectionState(data.username(), true);

        Platform.runLater(() -> {
            scoreBoardListView.getItems().setAll(scoreBoard.getDisplayablePlayers());

            stopGameButton.setManaged(data.owner().equals(scoreBoard.getClientPlayer().getName()));
            stopGameButton.setVisible(data.owner().equals(scoreBoard.getClientPlayer().getName()));
        });

        if (waitingForReconnectionsPopUp != null) {
            waitingForReconnectionsPopUp.askToHide();
            waitingForReconnectionsPopUp = null;
        }
    };

    private final EventListener<BoardChangedEventData> boardChangedListener = data -> {
        board = data.board();

        populateBoard();
    };

    private final EventListener<BookshelfHasChangedEventData> bookshelfHasChangedListener = data -> {
        bookshelves.set(scoreBoard.getDisplayablePlayer(data.username()).getOriginalIndex(), data.bookshelf());

        if (!isDisplayingCommonGoal) {
            populateBookshelfPanel();
        }
    };

    private final EventListener<CurrentPlayerChangedEventData> currentPlayerChangedListener = data -> {
        scoreBoard.setPlayingPlayerOriginalIndex(data.getUsername());

        populateTurnTextBox();

        populateBoard();

        if (!isDisplayingCommonGoal) {
            populateBookshelfPanel();
        }
    };

    private final EventListener<CommonGoalCompletedEventData> commonGoalCompletedListener = data -> {
        DisplayableGoal displayableGoal = new DisplayableGoal(data.getPlayer().getUsername(),
            DisplayableGoal.GoalType.COMMON, data.getCommonGoalCompleted() == commonGoals[0].getIndex(),
            data.getPoints(), data.getBookshelfMaskSet());

        popUpQueue.add(displayableGoal.toText(),
            popUp -> {
                List<Integer> newPointStack = commonGoals[displayableGoal.isFirstCommonGoal() ? 0
                    : 1].getPointStack();
                newPointStack.remove(newPointStack.size() - 1);
                commonGoals[displayableGoal.isFirstCommonGoal() ? 0
                    : 1].setPointStack(newPointStack);

                populateCommonGoalsPointsTextBoxes();

                displayGoal(displayableGoal);

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        popUp.askToHide();
                    }
                }, 10000);
            },
            popUp -> resetLayoutAfterDisplayingGoal()
        );
    };

    private final EventListener<FirstFullBookshelfEventData> firstFullBookshelfListener = data -> {
        popUpQueue.add(data.username() + " is the first who filled the bookshelf!",
            popUp -> {
                scoreBoard.addAdditionalPoints(data.username(), 1);

                Platform.runLater(() -> {
                    scoreBoardListView.getItems().setAll(scoreBoard.getDisplayablePlayers());
                });

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        popUp.askToHide();
                    }
                }, 4000);
            },
            popUp -> {
                scoreBoard.sumPoints(data.username());

                Platform.runLater(() -> {
                    scoreBoardListView.getItems().setAll(scoreBoard.getDisplayablePlayers());
                });
            });
    };

    private final EventListener<GameOverEventData> gameOverListener = data -> {
        for (DisplayablePlayer displayablePlayer : scoreBoard.getDisplayablePlayers()) {
            scoreBoard.setIsWinner(displayablePlayer.getName(),
                data.winners().stream().map(Player::getUsername).anyMatch(playerName ->
                    playerName.equals(displayablePlayer.getName())));
        }

        if (waitingForReconnectionsPopUp != null) {
            waitingForReconnectionsPopUp.askToHide();
            waitingForReconnectionsPopUp = null;

            setProperty("scoreboard", scoreBoard);
            switchLayout(ScoreboardMenuController.NAME);
            return;
        }

        List<DisplayableGoal> displayableGoals = new ArrayList<>();

        // Add final goal
        for (int i = 0; i < scoreBoard.getDisplayablePlayers().size(); i++) {

            if (data.personalGoal().get(i).getKey() > 0) {
                displayableGoals.add(new DisplayableGoal(scoreBoard.getDisplayablePlayer(i).getName(),
                    DisplayableGoal.GoalType.PERSONAL, false,
                    data.personalGoal().get(i).getKey(), data.personalGoal().get(i).getValue()));
            }

            if (data.adjacencyGoal().get(i).getKey() > 0) {
                displayableGoals.add(new DisplayableGoal(scoreBoard.getDisplayablePlayer(i).getName(),
                    DisplayableGoal.GoalType.ADJACENCY, false,
                    data.adjacencyGoal().get(i).getKey(), data.adjacencyGoal().get(i).getValue()));
            }
        }

        if (displayableGoals.isEmpty()) {
            setProperty("scoreboard", scoreBoard);
            switchLayout(ScoreboardMenuController.NAME);
            return;
        }

        for (int i = 0; i < displayableGoals.size(); i++) {
            popUpQueue.add(displayableGoals.get(i).toText(), displayableGoals.get(i),
                popUp -> {
                        displayGoal((DisplayableGoal) popUp.getData().get());

                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                popUp.askToHide();
                            }
                        }, 10000);
                    },
                (i == displayableGoals.size() - 1) ? popUp -> {
                    resetLayoutAfterDisplayingGoal();
                    setProperty("scoreboard", scoreBoard);
                    switchLayout(ScoreboardMenuController.NAME);
                } : popUp -> {
                    resetLayoutAfterDisplayingGoal();
                }
            );
        }
    };

    private final EventListener<PlayerHasDisconnectedEventData> playerHasDisconnectedListener = data -> {
        popUpQueue.add(data.username() + " has disconnected", data.username().toUpperCase() +
                "_HAS_DISCONNECTED",
            PopUp.hideAfter(2000),
            popUp -> {}
        );

        scoreBoard.setConnectionState(data.username(), false);

        Platform.runLater(() -> {
            scoreBoardListView.getItems().setAll(scoreBoard.getDisplayablePlayers());

            stopGameButton.setManaged(data.newOwner().equals(scoreBoard.getClientPlayer().getName()));
            stopGameButton.setVisible(data.newOwner().equals(scoreBoard.getClientPlayer().getName()));
        });
    };

    private final EventListener<GameHasBeenPauseEventData> gameHasBeenPauseListener = data -> waitForReconnections();

    private final EventListener<GameHasBeenStoppedEventData> gameHasBeenStoppedListener = data -> stopGame();

    public void waitForReconnections() {
        popUpQueue.add("Waiting for reconnection of other players",
            popUp ->{
                Platform.runLater(() -> {
                    if (scoreBoard.getDisplayablePlayers().stream().filter(DisplayablePlayer::isConnected)
                        .count() < 2 && !gameHasBeenStopped) {
                        waitingForReconnectionsPopUp = popUp;
                    } else {
                        popUp.askToHide();
                    }
                });
            },
            popUp -> {});
    }

    public void stopGame() {
        gameHasBeenStopped = true;

        if (waitingForReconnectionsPopUp != null) {
            waitingForReconnectionsPopUp.askToHide();
            waitingForReconnectionsPopUp = null;
        }

        popUpQueue.add("Game has been stopped",
            popUp -> {
                Platform.runLater(() -> {
                    stopGameButton.setDisable(true);
                    exitButton.setDisable(true);
                });

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        popUp.askToHide();
                    }
                }, 2000);
            },
            popUp -> switchLayout(AvailableGamesMenuController.NAME)
        );
    }

    @Override
    public String getName() {
        return NAME;
    }

    @FXML private Pane gameBackgroundBlurPane;

    @FXML
    private void initialize() {
        setBoardImageViewListener();

        setBookshelfImageViewListener();

        setCommonGoalImageViewsListeners();

        nextBookshelfButton.setOnAction(event -> {
            selectedBookshelfIndex++;

            populateBookshelfPanel();
        });

        previousBookshelfButton.setOnAction(event -> {
            selectedBookshelfIndex--;

            populateBookshelfPanel();
        });

        // Fill board with tile buttons:
        for (int row = 0; row < Board.BOARD_ROWS; row++) {
            for (int column = 0; column < Board.COLUMN_BOARDS; column++) {
                boardButtons[row][column] = craftTileButton(false);
                boardGridPane.add(boardButtons[row][column], column, row);

                if (!BoardView.isAlwaysEmpty(new Coordinate(row, column))) {
                    Integer rowInBoard = row;
                    Integer columnInBoard = column;

                    boardButtons[row][column].setOnAction(event -> {
                        new Thread(new Task<Void>() {
                            @Override
                            protected Void call() {
                                try {
                                    Coordinate coordinateInBoard = new Coordinate(rowInBoard, columnInBoard);

                                    if (board.getSelectedCoordinates().contains(coordinateInBoard)) {
                                        showResponse(deselectTileRequester.request(new DeselectTileEventData(
                                            coordinateInBoard
                                        )));
                                    } else {
                                        showResponse(selectTileRequester.request(new SelectTileEventData(
                                            coordinateInBoard
                                        )));
                                    }
                                } catch (DisconnectedException e) {
                                    showResponse(Response.failure("Disconnected!"));
                                }

                                return null;
                            }
                        }).start();
                    });
                }
            }
        }

        // Fill bookshelf with tile buttons:
        for (int row = 0; row < BookshelfView.ROWS; row++) {
            for (int column = 0; column < BookshelfView.COLUMNS; column++) {
                bookshelfButtons[row][column] = craftTileButton(true);
                bookshelfGridPane.add(bookshelfButtons[row][column], column, row);
            }
        }

        bookshelfColumnsButton[0] = this.bookshelfColumn1Button;
        bookshelfColumnsButton[1] = this.bookshelfColumn2Button;
        bookshelfColumnsButton[2] = this.bookshelfColumn3Button;
        bookshelfColumnsButton[3] = this.bookshelfColumn4Button;
        bookshelfColumnsButton[4] = this.bookshelfColumn5Button;

        for (int bookshelfColumn = 0; bookshelfColumn < BookshelfView.COLUMNS; bookshelfColumn++) {
            bookshelfColumnsButton[bookshelfColumn].setDisable(true);

            Integer column = bookshelfColumn;

            bookshelfColumnsButton[bookshelfColumn].setOnAction(event -> {
                new Thread(new Task<Void>() {
                    @Override
                    protected Void call() {
                        try {
                            showResponse(insertTileRequester.request(new InsertTileEventData(column)));
                        } catch (DisconnectedException e) {
                            showResponse(Response.failure("Disconnected!"));
                        }

                        return null;
                    }
                }).start();
            });
        }

        stopGameButton.setOnAction(event -> {
            try {
                showResponse(pauseGameRequester.request(new PauseGameEventData()));
            } catch (DisconnectedException e) {
                showResponse(Response.failure("Disconnected!"));
            }
        });

        exitButton.setOnAction(event -> {
            try {
                Response response = playerExitGameRequester.request(new PlayerExitGame());

                if (!response.isOk()) {
                    showResponse(response);
                    return;
                }

                if (waitingForReconnectionsPopUp != null) {
                    waitingForReconnectionsPopUp.askToHide();
                    waitingForReconnectionsPopUp = null;
                }

                switchLayout(AvailableGamesMenuController.NAME);
            } catch (DisconnectedException e) {
                showResponse(Response.failure("Disconnected!"));
            }
        });

        // Setup:
        isDisplayingCommonGoal = false;
        waitingForReconnectionsPopUp = null;

        exitButton.setDisable(true);
        stopGameButton.setDisable(true);
        stopGameButton.setVisible(false);
        stopGameButton.setManaged(false);

        previousBookshelfButton.setDisable(true);
        nextBookshelfButton.setDisable(true);

        if (transceiver == null) {
            transceiver = getValue("transceiver");

            joinGameRequester = Response.requester(transceiver, transceiver, new Object());
            selectTileRequester = Response.requester(transceiver, transceiver, new Object());
            deselectTileRequester = Response.requester(transceiver, transceiver, new Object());
            insertTileRequester = Response.requester(transceiver, transceiver, new Object());
            pauseGameRequester = Response.requester(transceiver, transceiver, new Object());
            playerExitGameRequester = Response.requester(transceiver, transceiver, new Object());

            initialGameReceiver = InitialGameEventData.castEventReceiver(transceiver);
            personalGoalSetReceiver = PersonalGoalSetEventData.castEventReceiver(transceiver);
            playerHasJoinGameReceiver = PlayerHasJoinGameEventData.castEventReceiver(transceiver);
            boardChangedReceiver = BoardChangedEventData.castEventReceiver(transceiver);
            bookshelfHasChangedReceiver = BookshelfHasChangedEventData.castEventReceiver(transceiver);
            currentPlayerChangedReceiver = CurrentPlayerChangedEventData.castEventReceiver(transceiver);
            commonGoalCompletedReceiver = CommonGoalCompletedEventData.castEventReceiver(transceiver);
            firstFullBookshelfReceiver = FirstFullBookshelfEventData.castEventReceiver(transceiver);
            gameOverReceiver = GameOverEventData.castEventReceiver(transceiver);
            playerHasDisconnectedReceiver = PlayerHasDisconnectedEventData.castEventReceiver(transceiver);
            gameHasBeenPauseReceiver = GameHasBeenPauseEventData.castEventReceiver(transceiver);
            gameHasBeenStoppedReceiver = GameHasBeenStoppedEventData.castEventReceiver(transceiver);

            PlayerDisconnectedInternalEventData.castEventReceiver(transceiver).registerListener(data -> {
                transceiver = null;

                joinGameRequester = null;
                selectTileRequester = null;
                deselectTileRequester = null;
                insertTileRequester = null;
                pauseGameRequester = null;
                playerExitGameRequester = null;

                initialGameReceiver = null;
                personalGoalSetReceiver = null;
                playerHasJoinGameReceiver = null;
                boardChangedReceiver = null;
                bookshelfHasChangedReceiver = null;
                currentPlayerChangedReceiver = null;
                commonGoalCompletedReceiver = null;
                firstFullBookshelfReceiver = null;
                gameOverReceiver = null;
                playerHasDisconnectedReceiver = null;
                gameHasBeenPauseReceiver = null;
                gameHasBeenStoppedReceiver = null;

                if (isCurrentLayout()) {
                    switchLayout(ConnectionMenuController.NAME);
                }
            });
        }

        scoreBoard = new DisplayableScoreBoard(getValue("username"));

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
                        playerHBox.setPadding(new Insets(5, 5, 5, 5));
                        playerHBox.getStyleClass().add("playerBox");

                        // score position
                        Label playerPositionLabel = new Label("[#" + player.getPosition() + "] ");

                        // player name
                        Label playerNameLabel = new Label(player.getName());

                        if (!player.isConnected()) {
                            playerNameLabel.setStyle("-fx-text-fill: light-grey");
                        } else if (player.isWinner()) {
                            playerNameLabel.setStyle("-fx-text-fill: palette-green");
                        } else if (player.isClientPlayer()) {
                            playerNameLabel.setStyle("-fx-text-fill: palette-light-red");
                        }

                        // box for left-side fields
                        HBox leftBox = new HBox();
                        HBox.setHgrow(leftBox, Priority.ALWAYS);
                        leftBox.setAlignment(Pos.CENTER);
                        leftBox.getChildren().add(playerPositionLabel);
                        leftBox.getChildren().add(playerNameLabel);

                        // points
                        Label pointsLabel = new Label("Score: " + player.getPoints());
                        pointsLabel.setPadding(new Insets(0, 0, 0, 10));
                        pointsLabel.setAlignment(Pos.CENTER_RIGHT);

                        // additional points
                        int additionalPoints = player.getAdditionalPoints();
                        Label additionalPointsLabel = new Label(" + " + additionalPoints);
                        additionalPointsLabel.setStyle("-fx-text-fill: palette-green");
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

        joinGameRequester.registerAllListeners();
        selectTileRequester.registerAllListeners();
        deselectTileRequester.registerAllListeners();
        insertTileRequester.registerAllListeners();
        pauseGameRequester.registerAllListeners();
        playerExitGameRequester.registerAllListeners();

        initialGameReceiver.registerListener(initialGameListener);
        personalGoalSetReceiver.registerListener(personalGoalSetListener);
        playerHasJoinGameReceiver.registerListener(playerHasJoinGameListener);
        boardChangedReceiver.registerListener(boardChangedListener);
        bookshelfHasChangedReceiver.registerListener(bookshelfHasChangedListener);
        currentPlayerChangedReceiver.registerListener(currentPlayerChangedListener);
        commonGoalCompletedReceiver.registerListener(commonGoalCompletedListener);
        firstFullBookshelfReceiver.registerListener(firstFullBookshelfListener);
        gameOverReceiver.registerListener(gameOverListener);
        playerHasDisconnectedReceiver.registerListener(playerHasDisconnectedListener);
        gameHasBeenPauseReceiver.registerListener(gameHasBeenPauseListener);
        gameHasBeenStoppedReceiver.registerListener(gameHasBeenStoppedListener);

        popUpQueue = new PopUpQueue(
            text -> Platform.runLater(() -> {
                gamePopUpLabel.setText(text);
                gamePopUpMessageBackground.setVisible(true);
            }),
            () -> Platform.runLater(() -> {
                gamePopUpMessageBackground.setVisible(false);
            }),
            new Object()
        );

        Response response;

        try {
            response = joinGameRequester.request(new JoinGameEventData(getValue("selectedgamename")));

            showResponse(response);

            if (!response.isOk()) {
                switchLayout(AvailableGamesMenuLayout.NAME);
            }
        } catch (DisconnectedException e) {
            showResponse(Response.failure("Disconnected!"));
        }

        gameHasBeenStopped = false;
    }

    @Override
    public void beforeSwitch() {
        if (transceiver != null) {
            initialGameReceiver.unregisterListener(initialGameListener);
            personalGoalSetReceiver.unregisterListener(personalGoalSetListener);
            playerHasJoinGameReceiver.unregisterListener(playerHasJoinGameListener);
            boardChangedReceiver.unregisterListener(boardChangedListener);
            bookshelfHasChangedReceiver.unregisterListener(bookshelfHasChangedListener);
            currentPlayerChangedReceiver.unregisterListener(currentPlayerChangedListener);
            commonGoalCompletedReceiver.unregisterListener(commonGoalCompletedListener);
            firstFullBookshelfReceiver.unregisterListener(firstFullBookshelfListener);
            gameOverReceiver.unregisterListener(gameOverListener);
            playerHasDisconnectedReceiver.unregisterListener(playerHasDisconnectedListener);
            gameHasBeenPauseReceiver.unregisterListener(gameHasBeenPauseListener);
            gameHasBeenStoppedReceiver.unregisterListener(gameHasBeenStoppedListener);

            joinGameRequester.unregisterAllListeners();
            selectTileRequester.unregisterAllListeners();
            deselectTileRequester.unregisterAllListeners();
            insertTileRequester.unregisterAllListeners();
            pauseGameRequester.unregisterAllListeners();
            playerExitGameRequester.unregisterAllListeners();
        }

        popUpQueue.disable();
        popUpQueue = null;
    }
}
