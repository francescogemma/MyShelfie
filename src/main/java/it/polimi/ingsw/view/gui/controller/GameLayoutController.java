package it.polimi.ingsw.view.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class GameLayoutController {
    @FXML private GridPane boardGridPane;
    @FXML private ImageView gameBoardImageView;
    @FXML private GridPane bookshelfGridPane;
    @FXML private ImageView bookshelfImageView;

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

    @FXML
    private void initialize() {
        gameBoardImageView.fitWidthProperty().addListener((observable, oldValue, newValue) -> {
            double realWidth = Math.min(gameBoardImageView.getFitWidth(), gameBoardImageView.getFitHeight());
            double realHeight = Math.min(gameBoardImageView.getFitHeight(), gameBoardImageView.getFitWidth());

            boardGridPane.setMaxWidth(realWidth * boardResizeFactor);
            boardGridPane.setMaxHeight(realHeight * boardResizeFactor);
            boardGridPane.setPrefWidth(realWidth * boardResizeFactor);
            boardGridPane.setPrefHeight(realHeight * boardResizeFactor);
            boardGridPane.setTranslateX(-realWidth * boardRightMarginFactor);
            boardGridPane.setHgap(boardGridPane.getWidth() * (36.0 / boardCellsSize));
            boardGridPane.setVgap(boardGridPane.getHeight() * (36.0 / boardCellsSize));
        });

        gameBoardImageView.fitHeightProperty().addListener((observable, oldValue, newValue) -> {
            double realWidth = Math.min(gameBoardImageView.getFitWidth(), gameBoardImageView.getFitHeight());
            double realHeight = Math.min(gameBoardImageView.getFitHeight(), gameBoardImageView.getFitWidth());

            boardGridPane.setMaxWidth(realWidth * boardResizeFactor);
            boardGridPane.setMaxHeight(realHeight * boardResizeFactor);
            boardGridPane.setPrefWidth(realWidth * boardResizeFactor);
            boardGridPane.setPrefHeight(realHeight * boardResizeFactor);
            boardGridPane.setTranslateX(-realWidth * boardRightMarginFactor);
            boardGridPane.setHgap(boardGridPane.getWidth() * (36.0 / boardCellsSize));
            boardGridPane.setVgap(boardGridPane.getHeight() * (36.0 / boardCellsSize));
        });


        bookshelfImageView.fitWidthProperty().addListener((observable, oldValue, newValue) -> {
            double realWidth = Math.min(bookshelfImageView.getFitWidth(), bookshelfImageView.getFitHeight() * bookshelfAspectRatio);
            double realHeight = Math.min(bookshelfImageView.getFitHeight(), bookshelfImageView.getFitWidth() / bookshelfAspectRatio);

            bookshelfGridPane.setMaxWidth(realWidth * bookshelfWidthResizeFactor);
            bookshelfGridPane.setMaxHeight(realHeight * bookshelfHeightResizeFactor);
            bookshelfGridPane.setPrefWidth(realWidth * bookshelfWidthResizeFactor);
            bookshelfGridPane.setPrefHeight(realHeight * bookshelfHeightResizeFactor);
            bookshelfGridPane.setTranslateY(-realHeight * bookshelfUpMarginFactor);
            bookshelfGridPane.setHgap(bookshelfGridPane.getWidth() * (61.0 / bookshelfCellsWidth));
            bookshelfGridPane.setVgap(bookshelfGridPane.getHeight() * (30.0 / bookshelfCellsHeight));
        });

        bookshelfImageView.fitHeightProperty().addListener((observable, oldValue, newValue) -> {
            double realWidth = Math.min(bookshelfImageView.getFitWidth(), bookshelfImageView.getFitHeight() * bookshelfAspectRatio);
            double realHeight = Math.min(bookshelfImageView.getFitHeight(), bookshelfImageView.getFitWidth() / bookshelfAspectRatio);

            bookshelfGridPane.setMaxWidth(realWidth * bookshelfWidthResizeFactor);
            bookshelfGridPane.setMaxHeight(realHeight * bookshelfHeightResizeFactor);
            bookshelfGridPane.setPrefWidth(realWidth * bookshelfWidthResizeFactor);
            bookshelfGridPane.setPrefHeight(realHeight * bookshelfHeightResizeFactor);
            bookshelfGridPane.setTranslateY(-realHeight * bookshelfUpMarginFactor);
            bookshelfGridPane.setHgap(bookshelfGridPane.getWidth() * (61.0 / bookshelfCellsWidth));
            bookshelfGridPane.setVgap(bookshelfGridPane.getHeight() * (30.0 / bookshelfCellsHeight));
        });

        insertInBoard(5, 5, "/item tiles/Gatti1.3.png");

        insertInBookshelf(0, 0, "/item tiles/Gatti1.3.png");
    }

    private void insertInBoard(int row, int column, String url) {
        Button button = new Button();
        button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        button.setMinSize(0, 0);
        GridPane.setFillWidth(button, true);
        GridPane.setFillHeight(button, true);

        Image image = new Image(url);
        ImageView img = new ImageView(image);
        img.setPreserveRatio(true);
        img.fitWidthProperty().bind(button.widthProperty());
        img.fitHeightProperty().bind(button.heightProperty());
        button.setGraphic(img);

        boardGridPane.add(button, column, row);
    }

    private void insertInBookshelf(int row, int column, String url) {
        Button button = new Button();
        button.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        button.setMinSize(0, 0);
        GridPane.setFillWidth(button, true);
        GridPane.setFillHeight(button, true);

        Image image = new Image(url);
        ImageView img = new ImageView(image);
        img.setPreserveRatio(true);
        img.fitWidthProperty().bind(button.widthProperty());
        img.fitHeightProperty().bind(button.heightProperty());
        button.setGraphic(img);

        bookshelfGridPane.add(button, column, row);
    }
}
