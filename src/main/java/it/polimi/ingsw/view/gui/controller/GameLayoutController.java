package it.polimi.ingsw.view.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class GameLayoutController {
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

    private final double boardSize = 2965.0;
    private final double boardCellsSize = 2661.0;
    private final double boardResizeFactor = boardCellsSize / boardSize;
    private final double boardRightMarginFactor = 13.0 / boardSize;
    private final double bookshelfCellsWidth = 1074.0;
    private final double bookshelfCellsHeight = 1144.0;
    private final double bookshelfWidth = 1414.0;
    private final double bookshelfHeight = 1411.0;
    private final double bookshelfAspectRatio = bookshelfWidth / bookshelfHeight;
    private final double bookshelfWidthResizeFactor = bookshelfCellsWidth / bookshelfWidth;
    private final double bookshelfHeightResizeFactor = bookshelfCellsHeight / bookshelfHeight;
    private final double bookshelfUpMarginFactor = 38.5 / bookshelfHeight;
    private final double commonGoalWidth = 1385.0;
    private final double commonGoalHeight = 913.0;
    private final double commonGoalAspectRatio = commonGoalWidth / commonGoalHeight;
    private final double tokenCommonGoalResizeFactor = 315.805 / commonGoalWidth;
    private final double tokenCommonGoalAngle = -7.6426;
    private final double tokenCommonGoalUpMarginFactor = 39.5 / commonGoalWidth;
    private final double tokenCommonGoalRightMarginFactor = 326.5 / commonGoalWidth;

    @FXML
    private void initialize() {
        setBoardImageViewListener();

        setBookshelfImageViewListener();

        setTokenImage(8, firstScoringToken);
        setTokenImage(8, secondScoringToken);

        setCommonGoalImageViewsListeners();

        for(int i = 0; i < 6; i++) {
            for(int j = 0; j < 5; j++)
                insertButtonInBookshelf(i, j, "/item tiles/Trofei1.1.png");
        }
        insertButtonInBoard(5, 5, "/item tiles/Gatti1.3.png");
    }

    private void setTokenImage(int token, ImageView tokenImageView) {
        if(token == 0) {
            tokenImageView.setVisible(false);
        } else if(token == 2) {
            tokenImageView.setImage(new Image("/scoring tokens/scoring_2.jpg"));
        } else if(token == 4) {
            tokenImageView.setImage(new Image("/scoring tokens/scoring_4.jpg"));
        } else if(token == 6) {
            tokenImageView.setImage(new Image("/scoring tokens/scoring_6.jpg"));
        } else if(token == 8) {
            tokenImageView.setImage(new Image("/scoring tokens/scoring_8.jpg"));
        } else {
            throw new IllegalArgumentException("Invalid token value");
        }
    }

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

    private void insertButtonInBoard(int row, int column, String url) {
        Button tileButton = setButtonToInsert(url);
        boardGridPane.add(tileButton, column, row);
    }

    private void insertButtonInBookshelf(int row, int column, String url) {
        Button tileButton = setButtonToInsert(url);
        tileButton.setMouseTransparent(true);
        bookshelfGridPane.add(tileButton, column, row);
    }

    private Button setButtonToInsert(String url) {
        Button tileButton = new Button();
        tileButton.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        tileButton.setMinSize(0, 0);
        GridPane.setFillWidth(tileButton, true);
        GridPane.setFillHeight(tileButton, true);

        Image image = new Image(url);
        ImageView img = new ImageView(image);
        img.setPreserveRatio(true);
        img.fitWidthProperty().bind(tileButton.widthProperty());
        img.fitHeightProperty().bind(tileButton.heightProperty());
        tileButton.setGraphic(img);
        tileButton.setStyle("-fx-background-color: transparent; " +
                            "-fx-border-color: transparent; " +
                            "-fx-border-width: 0; " +
                            "-fx-border-radius: 0; " +
                            "-fx-background-radius: 0;");

        return tileButton;
    }
}
