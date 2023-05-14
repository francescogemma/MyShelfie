package it.polimi.ingsw.view.gui.controller;

import it.polimi.ingsw.controller.Response;
import it.polimi.ingsw.event.NetworkEventTransceiver;
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
import it.polimi.ingsw.view.tui.LoginMenuLayout;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import it.polimi.ingsw.event.data.game.*;
import it.polimi.ingsw.event.data.client.*;
import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.model.game.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import it.polimi.ingsw.view.displayable.DisplayablePlayer;
import it.polimi.ingsw.model.goal.CommonGoal;
import it.polimi.ingsw.model.game.IllegalFlowException;
import it.polimi.ingsw.view.displayable.DisplayableScoreBoard;

public class GameController extends Controller {
    // Layout:
    @FXML private GridPane boardGridPane;
    @FXML private ImageView gameBoardImageView;
    @FXML private GridPane bookshelfGridPane;
    @FXML private GridPane bookshelfColumnSelectorGridPane;
    @FXML private ImageView bookshelfImageView;
    @FXML private Pane boardBackground;
    @FXML private Label turnLabel;
    @FXML private ImageView firstScoringToken;
    @FXML private ImageView secondScoringToken;
    @FXML private ImageView firstCommonGoalImageView;
    @FXML private ImageView secondCommonGoalImageView;
    @FXML private ImageView personalGoalImageView;
    @FXML private Button stopGameButton;
    @FXML private Button exitButton;
    @FXML private Button nextBookshelfButton;
    @FXML private Button previousBookshelfButton;
    @FXML private ListView<DisplayablePlayer> scoreBoardListView;

    @FXML private Button bookshelfColumn1Button;
    @FXML private Button bookshelfColumn2Button;
    @FXML private Button bookshelfColumn3Button;
    @FXML private Button bookshelfColumn4Button;
    @FXML private Button bookshelfColumn5Button;

    @FXML private Label playerBookshelf;

    private Button[] bookshelfColumnsButton = new Button[BookshelfView.COLUMNS];
    private static final double boardSize = 2965.0;
    private static final double boardCellsSize = 2661.0;
    private static final double boardResizeFactor = boardCellsSize / boardSize;
    private static final double boardRightMarginFactor = 13.0 / boardSize;
    private static final double bookshelfCellsWidth = 1074.0;
    private static final double bookshelfCellsHeight = 1144.0;
    private static final double bookshelfWidth = 1414.0;
    private static final double bookshelfHeight = 1411.0;
    private static final double bookshelfAspectRatio = bookshelfWidth / bookshelfHeight;
    private static final double bookshelfWidthResizeFactor = bookshelfCellsWidth / bookshelfWidth;
    private static final double bookshelfHeightResizeFactor = bookshelfCellsHeight / bookshelfHeight;
    private static final double bookshelfUpMarginFactor = 38.5 / bookshelfHeight;
    private static final double commonGoalWidth = 1385.0;
    private static final double commonGoalHeight = 913.0;
    private static final double commonGoalAspectRatio = commonGoalWidth / commonGoalHeight;
    private static final double tokenCommonGoalResizeFactor = 315.805 / commonGoalWidth;
    private static final double tokenCommonGoalAngle = -7.6426;
    private static final double tokenCommonGoalUpMarginFactor = 39.5 / commonGoalWidth;
    private static final double tokenCommonGoalRightMarginFactor = 326.5 / commonGoalWidth;

    public static final String NAME = "Game";

    private void resizeBoard() {
        double realWidth = Math.min(gameBoardImageView.getFitWidth(), gameBoardImageView.getFitHeight());
        double realHeight = Math.min(gameBoardImageView.getFitHeight(), gameBoardImageView.getFitWidth());

        boardGridPane.setMaxWidth(realWidth * boardResizeFactor);
        boardGridPane.setMaxHeight(realHeight * boardResizeFactor);
        boardGridPane.setPrefWidth(realWidth * boardResizeFactor);
        boardGridPane.setPrefHeight(realHeight * boardResizeFactor);
        boardGridPane.setTranslateX(-realWidth * boardRightMarginFactor);
        boardGridPane.setHgap(boardGridPane.getWidth() * (36.0 / boardCellsSize));
        boardGridPane.setVgap(boardGridPane.getHeight() * (36.0 / boardCellsSize));

        boardBackground.setMaxWidth(realWidth * 1.12);
        boardBackground.setMaxHeight(realHeight * 1.12);
        boardBackground.setPrefWidth(realWidth * 1.12);
        boardBackground.setPrefHeight(realHeight * 1.12);

        turnLabel.setTranslateY(-realWidth * 1.12 / 2 - turnLabel.getHeight() / 2);
    }

    private void resizeBookshelf() {
        double realWidth = Math.min(bookshelfImageView.getFitWidth(), bookshelfImageView.getFitHeight() * bookshelfAspectRatio);
        double realHeight = Math.min(bookshelfImageView.getFitHeight(), bookshelfImageView.getFitWidth() / bookshelfAspectRatio);

        bookshelfGridPane.setMaxWidth(realWidth * bookshelfWidthResizeFactor);
        bookshelfGridPane.setMaxHeight(realHeight * bookshelfHeightResizeFactor);
        bookshelfGridPane.setPrefWidth(realWidth * bookshelfWidthResizeFactor);
        bookshelfGridPane.setPrefHeight(realHeight * bookshelfHeightResizeFactor);
        bookshelfGridPane.setTranslateY(-realHeight * bookshelfUpMarginFactor);
        bookshelfGridPane.setHgap(bookshelfGridPane.getWidth() * (61.0 / bookshelfCellsWidth));
        bookshelfGridPane.setVgap(bookshelfGridPane.getHeight() * (30.0 / bookshelfCellsHeight));

        bookshelfColumnSelectorGridPane.setMaxWidth(realWidth * bookshelfWidthResizeFactor);
        bookshelfColumnSelectorGridPane.setMaxHeight(realHeight * bookshelfHeightResizeFactor);
        bookshelfColumnSelectorGridPane.setPrefWidth(realWidth * bookshelfWidthResizeFactor);
        bookshelfColumnSelectorGridPane.setPrefHeight(realHeight * bookshelfHeightResizeFactor);
        bookshelfColumnSelectorGridPane.setTranslateY(-realHeight * bookshelfUpMarginFactor);
        bookshelfColumnSelectorGridPane.setHgap(bookshelfColumnSelectorGridPane.getWidth() * (61.0 / bookshelfCellsWidth));
    }

    private void resizeCommonGoalToken(ImageView scoringToken) {
        double realWidth = Math.min(firstCommonGoalImageView.getFitWidth(), firstCommonGoalImageView.getFitHeight() * commonGoalAspectRatio);

        scoringToken.setFitWidth(realWidth * tokenCommonGoalResizeFactor);
        scoringToken.setFitHeight(realWidth * tokenCommonGoalResizeFactor);
        scoringToken.setTranslateY(-realWidth * tokenCommonGoalUpMarginFactor);
        scoringToken.setTranslateX(realWidth * tokenCommonGoalRightMarginFactor);
        scoringToken.setRotate(tokenCommonGoalAngle);
    }

    private void setBoardImageViewListener() {
        gameBoardImageView.fitWidthProperty().addListener((observable, oldValue, newValue) -> resizeBoard());
        gameBoardImageView.fitHeightProperty().addListener((observable, oldValue, newValue) -> resizeBoard());
    }

    private void setBookshelfImageViewListener() {
        bookshelfImageView.fitWidthProperty().addListener((observable, oldValue, newValue) -> resizeBookshelf());
        bookshelfImageView.fitHeightProperty().addListener((observable, oldValue, newValue) -> resizeBookshelf());
    }

    private void setCommonGoalImageViewsListeners() {
        firstCommonGoalImageView.fitWidthProperty().addListener((observable, oldValue, newValue) -> resizeCommonGoalToken(firstScoringToken));
        firstCommonGoalImageView.fitHeightProperty().addListener((observable, oldValue, newValue) -> resizeCommonGoalToken(firstScoringToken));

        secondCommonGoalImageView.fitWidthProperty().addListener((observable, oldValue, newValue) -> resizeCommonGoalToken(secondScoringToken));
        secondCommonGoalImageView.fitHeightProperty().addListener((observable, oldValue, newValue) -> resizeCommonGoalToken(secondScoringToken));
    }

    private void populateBoard() {
        Platform.runLater(() -> {
            for (int row = 0; row < Board.BOARD_ROWS; row++) {
                for (int column = 0; column < Board.COLUMN_BOARDS; column++) {
                    Coordinate boardCoordinate = new Coordinate(row, column);

                    Tile tile = board.tileAt(boardCoordinate);

                    if (tile != null) {
                        ((ImageView) boardButtons[row][column].getGraphic()).setImage(new Image(
                            toUrl(tile)
                        ));
                        boardButtons[row][column].setVisible(true);

                        if (board.getSelectableCoordinate().contains(boardCoordinate)
                            || board.getSelectedCoordinates().contains(boardCoordinate)) {
                            boardButtons[row][column].setRotate(45.d);
                            boardButtons[row][column].setMouseTransparent(false);
                        } else {
                            boardButtons[row][column].setRotate(0.d);
                            boardButtons[row][column].setMouseTransparent(true);
                        }
                    } else {
                        boardButtons[row][column].setVisible(false);
                    }
                }
            }
        });
    }

    private void populateBookshelfPanel() {
        Platform.runLater(() -> {
            for (int row = 0; row < BookshelfView.ROWS; row++) {
                for (int column = 0; column < BookshelfView.COLUMNS; column++) {
                    bookshelfButtons[row][column].setOpacity(1.d);

                    Tile tile = bookshelves.get(selectedBookshelfIndex)
                        .getTileAt(Shelf.getInstance(row, column));

                    if (tile == Tile.getInstance(TileColor.EMPTY, TileVersion.FIRST)) {
                        bookshelfButtons[row][column].setVisible(false);
                    } else {
                        ((ImageView) bookshelfButtons[row][column].getGraphic()).setImage(new Image(
                            toUrl(tile)
                        ));
                        bookshelfButtons[row][column].setVisible(true);
                    }
                }
            }
        });
    }

    private void populateBookshelfPanelWithMask(BookshelfMaskSet maskSet) {
        Platform.runLater(() -> {
            for (int row = 0; row < BookshelfView.ROWS; row++) {
                for (int column = 0; column < BookshelfView.COLUMNS; column++) {
                    bookshelfButtons[row][column].setOpacity(0.5d);
                }
            }

            int count = 0;
            for (BookshelfMask mask : maskSet.getBookshelfMasks()) {
                for (Shelf shelf : mask.getShelves()) {
                    bookshelfButtons[shelf.getRow()][shelf.getColumn()].setOpacity(1.0d);
                }

                count++;
            }
        });
    }

    private void setTokenImage(int points, ImageView tokenImageView) {
        Platform.runLater(() -> {
            if(points == 0) {
                tokenImageView.setVisible(false);
            } else if(points == 2) {
                tokenImageView.setImage(new Image("/scoring tokens/scoring_2.jpg"));
            } else if(points == 4) {
                tokenImageView.setImage(new Image("/scoring tokens/scoring_4.jpg"));
            } else if(points == 6) {
                tokenImageView.setImage(new Image("/scoring tokens/scoring_6.jpg"));
            } else if(points == 8) {
                tokenImageView.setImage(new Image("/scoring tokens/scoring_8.jpg"));
            } else {
                throw new IllegalArgumentException("Invalid token value");
            }
        });
    }

    private void populateCommonGoalsPointsTextBoxes() {
        setTokenImage((commonGoals[0].getPointStack().isEmpty()) ? 0 :
            commonGoals[0].getPointStack().get(commonGoals[0].getPointStack().size() - 1), firstScoringToken);
        setTokenImage((commonGoals[1].getPointStack().isEmpty()) ? 0 :
            commonGoals[1].getPointStack().get(commonGoals[0].getPointStack().size() - 1), secondScoringToken);
    }

    private void populateGoalsPanel() {
        Platform.runLater(() -> {
            // first common goal
            firstCommonGoalImageView.setImage(new Image(commonGoalToUrl(commonGoals[0].getIndex())));
            Tooltip.install(firstCommonGoalImageView, new Tooltip(commonGoals[0].getDescription()));

            // second common goal
            secondCommonGoalImageView.setImage(new Image(commonGoalToUrl(commonGoals[1].getIndex())));
            Tooltip.install(secondCommonGoalImageView, new Tooltip(commonGoals[1].getDescription()));

            personalGoalImageView.setImage(new Image(personalGoalToUrl(this.personalGoal.getIndex())));
        });

        populateCommonGoalsPointsTextBoxes();
    }

    private void displayGoal(DisplayableGoal displayableGoal) {
        isDisplayingCommonGoal = true;

        for (DisplayablePlayer displayablePlayer : scoreBoard.getDisplayablePlayers()) {
            if (!displayablePlayer.getName().equals(displayableGoal.getPlayerName())) {
                scoreBoard.blur(displayablePlayer.getName());
            }
        }
        scoreBoard.addAdditionalPoints(displayableGoal.getPlayerName(), displayableGoal.getPoints());

        Platform.runLater(() -> {
            scoreBoardListView.getItems().setAll(scoreBoard.getDisplayablePlayers());

            playerBookshelf.setText(displayableGoal.getPlayerName());
            selectedBookshelfIndex = scoreBoard.getDisplayablePlayer(displayableGoal.getPlayerName())
                .getOriginalIndex();
            previousBookshelfButton.setDisable(true);
            nextBookshelfButton.setDisable(true);
        });

        populateBookshelfPanelWithMask(displayableGoal.getMaskSet());
    }

    private void resetLayoutAfterDisplayingGoal() {
        isDisplayingCommonGoal = false;

        for (DisplayablePlayer displayablePlayer : scoreBoard.getDisplayablePlayers()) {
            scoreBoard.unblur(displayablePlayer.getName());
            scoreBoard.sumPoints(displayablePlayer.getName());
        }

        Platform.runLater(() -> {
            scoreBoardListView.getItems().setAll(scoreBoard.getDisplayablePlayers());
        });

        populateBookshelfPanel();
    }

    private void populateTurnTextBox() {
        Platform.runLater(() -> {
            turnLabel.setText(
                "It's " + (scoreBoard.isClientPlaying() ? "your" : scoreBoard.getPlayingPlayer().getName() + "'s") + "turn"
            );

            turnLabel.setStyle("-fx-text-fill: %s".formatted(
                scoreBoard.isClientPlaying() ? "#403f28" : "#f0ede3"
            ));
        });
    }

    private ToggleButton[][] boardButtons = new ToggleButton[Board.BOARD_ROWS][Board.COLUMN_BOARDS];
    private ToggleButton[][] bookshelfButtons = new ToggleButton[BookshelfView.ROWS][BookshelfView.COLUMNS];

    private ToggleButton craftTileButton(boolean isMouseTrasparent) {
        ToggleButton tileButton = new ToggleButton();

        tileButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        tileButton.setMinSize(0, 0);
        GridPane.setFillWidth(tileButton, true);
        GridPane.setFillHeight(tileButton, true);

        ImageView imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.fitWidthProperty().bind(tileButton.widthProperty());
        imageView.fitHeightProperty().bind(tileButton.heightProperty());
        tileButton.setGraphic(imageView);

        tileButton.setMouseTransparent(isMouseTrasparent);

        tileButton.setVisible(false);

        return tileButton;
    }

    private String commonGoalToUrl(int index) {
        return "/common goal cards/%s.jpg".formatted(index);
    }

    private String personalGoalToUrl(int index) {
        return "/personal goal cards/PersonalGoals%s".formatted(index);
    }

    private String toUrl(Tile tile) {
        if(tile == Tile.getInstance(TileColor.GREEN, TileVersion.FIRST)) {
            return "/item tiles/Gatti1.1.png";
        } else if(tile == Tile.getInstance(TileColor.GREEN, TileVersion.SECOND)) {
            return "/item tiles/Gatti1.2.png";
        } else if(tile == Tile.getInstance(TileColor.GREEN, TileVersion.THIRD)) {
            return "/item tiles/Gatti1.3.png";
        } else if(tile == Tile.getInstance(TileColor.BLUE, TileVersion.FIRST)) {
            return "/item tiles/Cornici1.1.png";
        } else if(tile == Tile.getInstance(TileColor.BLUE, TileVersion.SECOND)) {
            return "/item tiles/Cornici1.2.png";
        } else if(tile == Tile.getInstance(TileColor.BLUE, TileVersion.THIRD)) {
            return "/item tiles/Cornici1.3.png";
        } else if(tile == Tile.getInstance(TileColor.MAGENTA, TileVersion.FIRST)) {
            return "/item tiles/Piante1.1.png";
        } else if(tile == Tile.getInstance(TileColor.MAGENTA, TileVersion.SECOND)) {
            return "/item tiles/Piante1.2.png";
        } else if(tile == Tile.getInstance(TileColor.MAGENTA, TileVersion.THIRD)) {
            return "/item tiles/Piante1.3.png";
        } else if(tile == Tile.getInstance(TileColor.CYAN, TileVersion.FIRST)) {
            return "/item tiles/Trofei1.1.png";
        } else if(tile == Tile.getInstance(TileColor.CYAN, TileVersion.SECOND)) {
            return "/item tiles/Trofei1.2.png";
        } else if(tile == Tile.getInstance(TileColor.CYAN, TileVersion.THIRD)) {
            return "/item tiles/Trofei1.3.png";
        } else if(tile == Tile.getInstance(TileColor.YELLOW, TileVersion.FIRST)) {
            return "/item tiles/Giochi1.1.png";
        } else if(tile == Tile.getInstance(TileColor.YELLOW, TileVersion.SECOND)) {
            return "/item tiles/Giochi1.2.png";
        } else if(tile == Tile.getInstance(TileColor.YELLOW, TileVersion.THIRD)) {
            return "/item tiles/Giochi1.3.png";
        } else if(tile == Tile.getInstance(TileColor.WHITE, TileVersion.FIRST)) {
            return "/item tiles/Libri1.1.png";
        } else if(tile == Tile.getInstance(TileColor.WHITE, TileVersion.SECOND)) {
            return "/item tiles/Libri1.2.png";
        } else if(tile == Tile.getInstance(TileColor.WHITE, TileVersion.THIRD)) {
            return "/item tiles/Libri1.3.png";
        } else {
            throw new IllegalArgumentException("Tile not found");
        }
    }

    @FXML
    private void initialize() {
        if (transceiver == null) {
            transceiver = (NetworkEventTransceiver) getValue("transceiver");

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

        setBoardImageViewListener();

        setBookshelfImageViewListener();

        setCommonGoalImageViewsListeners();

        // Fill board with tile buttons:
        for (int row = 0; row < Board.BOARD_ROWS; row++) {
            for (int column = 0; column < Board.COLUMN_BOARDS; column++) {
                boardButtons[row][column] = craftTileButton(false);
                boardGridPane.add(boardButtons[row][column], column, row);

                if (!BoardView.isAlwaysEmpty(new Coordinate(row, column))) {
                    Integer rowInBoard = row;
                    Integer columnInBoard = column;

                    Boolean isSelected = boardButtons[row][column].isSelected();

                    boardButtons[row][column].setOnKeyPressed(event -> {
                        new Thread(new Task<Void>() {
                            @Override
                            protected Void call() {
                                try {
                                    if (isSelected) {
                                        showResponse(deselectTileRequester.request(new DeselectTileEventData(
                                            new Coordinate(rowInBoard, columnInBoard)
                                        )));
                                    } else {
                                        showResponse(selectTileRequester.request(new SelectTileEventData(
                                            new Coordinate(rowInBoard, columnInBoard)
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

            bookshelfColumnsButton[bookshelfColumn].setOnKeyPressed(event -> {
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

        exitButton.setDisable(true);
        stopGameButton.setDisable(true);

        previousBookshelfButton.setDisable(true);
        previousBookshelfButton.setOnKeyPressed(event -> {
            selectedBookshelfIndex--;
        });

        nextBookshelfButton.setDisable(true);
        nextBookshelfButton.setOnKeyPressed(event -> {
            selectedBookshelfIndex++;
        });

        for (int column = 0; column < BookshelfView.COLUMNS; column++) {
            bookshelfColumnsButton[column].setDisable(true);
            bookshelfColumnsButton[column].setOnKeyPressed(event -> {

            });
        }

        popUpQueue = new PopUpQueue(
            text -> {
                Platform.runLater(() -> {
                    userLoginPopUpLabel.setText(text);
                    userLoginBackgroundBlurPane.setVisible(true);
                    userLoginPopUpMessageBackground.setVisible(true);
                });
            },
            () -> {
                Platform.runLater(() -> {
                    userLoginBackgroundBlurPane.setVisible(false);
                    userLoginPopUpMessageBackground.setVisible(false);
                });
            },
            new Object()
        );

        scoreBoard = new DisplayableScoreBoard(getValue(
                "username"
        ));

        joinGameRequester.registerAllListeners();
        selectTileRequester.registerAllListeners();
        deselectTileRequester.registerAllListeners();;
        insertTileRequester.registerAllListeners();;
        pauseGameRequester.registerAllListeners();;
        playerExitGameRequester.registerAllListeners();;

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



        // ask for join game.
        Response response;
        try {
            response = joinGameRequester.request(new JoinGameEventData(
                    getValue("selectedgame")));

            showResponse(response);

            if (!response.isOk()) {
                switchLayout(AvailableGamesMenuLayout.NAME);
            }
        } catch (DisconnectedException e) {
            showResponse(Response.failure("Disconnected!"));
        }
    }

    @FXML
    private void exit() {
        Platform.exit();
        System.exit(0);
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

    // Utilities:
    private Requester<Response, JoinGameEventData> joinGameRequester = null;
    private Requester<Response, SelectTileEventData> selectTileRequester = null;
    private Requester<Response, DeselectTileEventData> deselectTileRequester = null;
    private Requester<Response, InsertTileEventData> insertTileRequester = null;
    private Requester<Response, PauseGameEventData> pauseGameRequester = null;
    private Requester<Response, PlayerExitGame> playerExitGameRequester = null;

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

    // listeners:
    private final EventListener<InitialGameEventData> initialGameListener = data -> {
        commonGoals = data.gameView().getCommonGoals();

        Platform.runLater(() -> {
            for (int i = 0; i < data.gameView().getPlayers().size(); i++) {
                scoreBoard.addDisplayablePlayer(data.gameView().getPlayers().get(i), i);
            }
            try {
                scoreBoard.setPlayingPlayerOriginalIndex(data.gameView().getCurrentPlayer().getUsername());
            } catch (IllegalFlowException e) {
                throw new IllegalStateException("It should be always possible to retrieve playing player");
            }
            populateTurnTextBox();
            scoreBoardRecyclerDrawable.populate(scoreBoard.getDisplayablePlayers());

            board = data.gameView().getBoard();
            populateBoard();

            stopGameButton.setDisable(false);
            if (data.gameView().getOwner()
                .equals(scoreBoard.getClientPlayer().getName())) {

                stopGameButton.setManaged(true);
                // stopGameButtonLayoutElement.setWeight(2);
            }
            exitButton.setDisable(false);

            gameName = data.gameView().getName();

            selectedBookshelfIndex = scoreBoard.getClientPlayer().getOriginalIndex();

            bookshelves = new ArrayList<>(data.gameView().getPlayers().stream().map(Player::getBookshelf)
                .toList());
            populateBookshelfPanel();

            if (data.gameView().isWaitingForReconnections()) {
                waitForReconnections();
            }

            if (data.gameView().isStopped()) {
                stopGame();
            }
        });
    };

    private final EventListener<PersonalGoalSetEventData> personalGoalSetListener = data -> {
        personalGoal = PersonalGoal.fromIndex(data.personalGoal());

        populateGoalsPanel();
    };

    private final EventListener<PlayerHasJoinGameEventData> playerHasJoinGameListener = data -> {
        scoreBoard.setConnectionState(data.username(), true);
        scoreBoardRecyclerDrawable.populate(scoreBoard.getDisplayablePlayers());

        if (waitingForReconnectionsPopUp != null) {
            waitingForReconnectionsPopUp.askToHide();
            waitingForReconnectionsPopUp = null;
        }

        if (data.owner().equals(scoreBoard.getClientPlayer().getName())) {
            stopGameButton.setManaged(true);
        } else {
            stopGameButton.setManaged(false);
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

        if (!isDisplayingCommonGoal) {
            populateBookshelfPanel();
        }
    };

    private final EventListener<CommonGoalCompletedEventData> commonGoalCompletedListener = data -> {
        DisplayableGoal displayableGoal = new DisplayableGoal(data.getPlayer().getUsername(),
            DisplayableGoal.GoalType.COMMON, data.getCommonGoalCompleted() == commonGoals[0].getIndex(),
            data.getPoints(), data.getBookshelfMaskSet());

        popUpQueue.add(displayableGoal.toText(),
            popUp ->
                Platform.runLater(() -> {
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
                }),
            popUp -> Platform.runLater(() -> resetLayoutAfterDisplayingGoal())
        );
    };

    private final EventListener<FirstFullBookshelfEventData> firstFullBookshelfListener = data -> {
        popUpQueue.add(data.username() + " is the first who filled the bookshelf!",
            popUp -> {
                Platform.runLater(() -> {
                    scoreBoard.addAdditionalPoints(data.username(), 1);
                    scoreBoardRecyclerDrawable.populate(scoreBoard.getDisplayablePlayers());

                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            popUp.askToHide();
                        }
                    }, 4000);
                });
            },
            popUp -> {
                Platform.runLater(() -> {
                    scoreBoard.sumPoints(data.username());
                    scoreBoardRecyclerDrawable.populate(scoreBoard.getDisplayablePlayers());
                });
            });
    };

    private final EventListener<GameOverEventData> gameOverListener = data -> {
        // TODO: scoreboard
        for (DisplayablePlayer displayablePlayer : scoreBoard.getDisplayablePlayers()) {
            scoreBoard.setIsWinner(displayablePlayer.getName(),
                data.winners().stream().map(Player::getUsername).anyMatch(playerName ->
                    playerName.equals(displayablePlayer.getName())));
        }

        if (waitingForReconnectionsPopUp != null) {
            waitingForReconnectionsPopUp.askToHide();
            waitingForReconnectionsPopUp = null;

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
            switchLayout(ScoreboardMenuController.NAME);
            return;
        }

        for (int i = 0; i < displayableGoals.size(); i++) {
            popUpQueue.add(displayableGoals.get(i).toText(), displayableGoals.get(i),
                popUp -> Platform.runLater(() -> {
                        displayGoal((DisplayableGoal) popUp.getData().get());

                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                popUp.askToHide();
                            }
                        }, 10000);
                    }),
                (i == displayableGoals.size() - 1) ? popUp -> {
                    Platform.runLater(() -> {
                        resetLayoutAfterDisplayingGoal();
                        switchLayout(ScoreboardMenuController.NAME);
                    });
                } : popUp -> {
                    Platform.runLater(() -> {
                        resetLayoutAfterDisplayingGoal();
                    });
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
        scoreBoardRecyclerDrawable.populate(scoreBoard.getDisplayablePlayers());

        if (data.newOwner().equals(scoreBoard.getClientPlayer().getName())) {
            stopGameButton.setManaged(true);
        } else {
            stopGameButton.setManaged(false);
        }
    };

    private final EventListener<GameHasBeenPauseEventData> gameHasBeenPauseListener = data -> {
        waitForReconnections();
    };

    private final EventListener<GameHasBeenStoppedEventData> gameHasBeenStoppedListener = data -> {
        stopGame();
    };

    public void waitForReconnections() {
        popUpQueue.add("Waiting for reconnection of other players",
            popUp -> Platform.runLater(() -> {
                if (scoreBoard.getDisplayablePlayers().stream().filter(DisplayablePlayer::isConnected)
                    .count() < 2) {
                    waitingForReconnectionsPopUp = popUp;
                } else {
                    popUp.askToHide();
                }
            }),
            popUp -> {});
    }

    public void stopGame() {
        if (waitingForReconnectionsPopUp != null) {
            waitingForReconnectionsPopUp.askToHide();
            waitingForReconnectionsPopUp = null;
        }

        popUpQueue.add("Game has been stopped",
            popUp -> Platform.runLater(() -> {
                stopGameButton.setDisable(true);
                exitButton.setDisable(true);

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        popUp.askToHide();
                    }
                }, 2000);
            }),
            popUp -> Platform.runLater(() -> switchLayout(AvailableGamesMenuController.NAME)));
    }

    @Override
    public String getName() {
        return NAME;
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
