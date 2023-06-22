package it.polimi.ingsw.view.tui.terminal.drawable;

import it.polimi.ingsw.controller.Response;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.value.TextBox;
import it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.OrientedLayout;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Color;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.PrimitiveSymbol;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Symbol;

import java.util.Optional;

/**
 * Allows to display the {@link Response} from the server on the right down corner of the screen on top of an
 * underlying FullyResizableDrawable.
 * In particular the {@link Response} is displayed with a {@link TextBox} with the status of the response (SUCCESS
 * or FAILURE) properly formatted (green if success or red if failure) followed by the message.
 * It is useful to build layouts for clients which interact with the server in a synchronous manner.
 *
 * @author Cristiano Migali
 */
public class ServerResponseDrawable extends FullyResizableDrawable {
    /**
     * It is the number of columns occupied by the status of the response in the {@link TextBox}.
     */
    private static final int STATUS_COLUMNS = 8;

    /**
     * It is the number of columns occupied by the whole response in the {@link TextBox}.
     */
    private static final int RESPONSE_COLUMNS = 40;

    /**
     * It is the underlying FullyResizableDrawable on top of which we will display the server's {@link Response}.
     */
    private final FullyResizableDrawable toAddServerResponse;

    /**
     * It is the {@link TextBox} used to display the status of the response.
     */
    private final TextBox responseStatusTextBox = new TextBox();

    /**
     * It is the {@link TextBox} used to display the message of the response.
     */
    private final TextBox responseMessageTextBox = new TextBox();

    /**
     * It is the foreground layer to add on top of the underlying FullyResizableDrawable which allows to display
     * the server's {@link Response}.
     */
    private final FullyResizableDrawable responseOverlay = new OrientedLayout(Orientation.HORIZONTAL,
        responseStatusTextBox.alignUpLeft().crop().fixSize(new DrawableSize(1, STATUS_COLUMNS))
            .weight(1), responseMessageTextBox.alignUpLeft().crop().fixSize(new DrawableSize(1,
        RESPONSE_COLUMNS - STATUS_COLUMNS)).weight(10)
    ).alignUpLeft().crop().fixSize(new DrawableSize(1, RESPONSE_COLUMNS)).alignDownRight().crop();

    /**
     * It is true iff we are showing a response from the server.
     */
    private boolean showResponse = false;

    /**
     * Constructor of the class.
     * It initializes the underlying FullyResizableDrawable.
     *
     * @param toAddServerResponse it is the FullyResizableDrawable on top of which we will display {@link Response}s
     *                            from the server.
     */
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

    /**
     * Shows the given response in the bottom right corner of the screen as explained in this class description.
     *
     * @param response is the response to be shown.
     * @return this ServerResponseDrawable which is now displaying the provided response.
     */
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

    /**
     * Hides the response which is currently shown in the bottom right corner of the screen.
     *
     * @return this ServerResponseDrawable which isn't displaying a response anymore.
     */
    public ServerResponseDrawable hideResponse() {
        showResponse = false;

        return this;
    }
}
