package it.polimi.ingsw.view.tui.terminal.drawable;

import it.polimi.ingsw.controller.Response;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.value.TextBox;
import it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.OrientedLayout;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Color;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.PrimitiveSymbol;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Symbol;

import java.util.Optional;

public class ServerResponseDrawable extends FullyResizableDrawable {
    private final static int STATUS_COLUMNS = 8;
    private final static int RESPONSE_COLUMNS = 40;

    private final FullyResizableDrawable toAddServerResponse;

    private final TextBox responseStatusTextBox = new TextBox();
    private final TextBox responseMessageTextBox = new TextBox();

    private final FullyResizableDrawable responseOverlay = new OrientedLayout(Orientation.HORIZONTAL,
        responseStatusTextBox.alignUpLeft().crop().fixSize(new DrawableSize(1, STATUS_COLUMNS))
            .weight(1), responseMessageTextBox.alignUpLeft().crop().fixSize(new DrawableSize(1,
        RESPONSE_COLUMNS - STATUS_COLUMNS)).weight(10)
    ).alignUpLeft().crop().fixSize(new DrawableSize(1, RESPONSE_COLUMNS)).alignDownRight().crop();

    private boolean showResponse = false;

    public ServerResponseDrawable(FullyResizableDrawable toAddServerResponse) {
        this.toAddServerResponse  = toAddServerResponse;
    }

    @Override
    public void askForSize(DrawableSize desiredSize) {
        super.askForSize(desiredSize);

        toAddServerResponse.askForSize(desiredSize);
        responseOverlay.askForSize(desiredSize);
    }

    @Override
    public Symbol getSymbolAt(Coordinate coordinate) {
        if (!size.isInside(coordinate)) {
            throw new OutOfDrawableException(size, coordinate);
        }

        if (!showResponse) {
            return toAddServerResponse.getSymbolAt(coordinate);
        }

        Symbol symbol = responseOverlay.getSymbolAt(coordinate);

        if (symbol == PrimitiveSymbol.EMPTY) {
            return toAddServerResponse.getSymbolAt(coordinate);
        }

        return symbol;
    }

    @Override
    public boolean handleInput(String key) {
        return toAddServerResponse.handleInput(key);
    }

    @Override
    public boolean focus(Coordinate desiredCoordinate) {
        return toAddServerResponse.focus(desiredCoordinate);
    }

    @Override
    public void unfocus() {
        toAddServerResponse.unfocus();
    }

    @Override
    public Optional<Coordinate> getFocusedCoordinate() {
        return toAddServerResponse.getFocusedCoordinate();
    }

    public ServerResponseDrawable showResponse(Response response) {
        showResponse = true;

        responseStatusTextBox.text(response.status().toString());

        if (response.isOk()) {
            responseStatusTextBox.color(Color.GREEN);
        } else {
            responseStatusTextBox.color(Color.RED);
        }

        String message;
        if (response.message() == null) {
            message = "";
        } else if (response.message().length() > RESPONSE_COLUMNS - STATUS_COLUMNS) {
            message = response.message().substring(0, RESPONSE_COLUMNS - STATUS_COLUMNS - 3) + "...";
        } else {
            message = response.message();
        }

        responseMessageTextBox.text(message);

        return this;
    }

    public ServerResponseDrawable hideResponse() {
        showResponse = false;

        return this;
    }
}
