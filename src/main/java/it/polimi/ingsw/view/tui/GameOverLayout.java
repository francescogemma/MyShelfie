package it.polimi.ingsw.view.tui;

import it.polimi.ingsw.event.NetworkEventTransceiver;
import it.polimi.ingsw.event.data.internal.PlayerDisconnectedInternalEventData;
import it.polimi.ingsw.view.displayable.DisplayablePlayer;
import it.polimi.ingsw.view.displayable.DisplayableScoreBoard;
import it.polimi.ingsw.view.tui.terminal.drawable.*;
import it.polimi.ingsw.view.tui.terminal.drawable.app.AppLayout;
import it.polimi.ingsw.view.tui.terminal.drawable.app.AppLayoutData;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.Button;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.value.TextBox;
import it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.OrientedLayout;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Color;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.PrimitiveSymbol;

import java.util.Map;

/**
 * {@link AppLayout} to display the final score board after the game has ended.
 *
 * @author Cristiano Migali
 */
public class GameOverLayout extends AppLayout {
    /**
     * Unique name of the game over layout required to allow other AppLayouts to tell
     * the {@link it.polimi.ingsw.view.tui.terminal.drawable.app.App} to switch to this layout.
     *
     * @see AppLayout
     */
    public static final String NAME = "GAME_OVER";

    // Layout:

    /**
     * It is a drawable which allows to display a player in the final score board.
     * Every player has a position text-box, a name text-box and a points text-box.
     */
    private static class GameOverDisplayablePlayerDrawable extends FixedLayoutDrawable<Drawable> {
        /**
         * It is the {@link TextBox} which displays the position of the player in the scoreboard.
         */
        private final TextBox positionTextBox = new TextBox().unfocusable();

        /**
         * It is the {@link TextBox} which displays the name of the player.
         */
        private final TextBox playerNameTextBox = new TextBox().hideCursor();

        /**
         * It is the {@link TextBox} which displays the points that the player has scored.
         */
        private final TextBox playerPointsTextBox = new TextBox().hideCursor();

        /**
         * Constructor of the class.
         * It initializes the layout in which all the text-boxes are arranged.
         */
        private GameOverDisplayablePlayerDrawable() {
            setLayout(new OrientedLayout(Orientation.HORIZONTAL,
                positionTextBox.center().weight(1),
                playerNameTextBox.center().weight(1),
                playerPointsTextBox.center().weight(1)
            ).center().crop().fixSize(new DrawableSize(5, 70))
                .addBorderBox());
        }
    }

    /**
     * {@link RecyclerDrawable} used to display the score board. It is populated only once.
     * The advantage of using a RecyclerDrawable is that it is capable of handling a different number
     * of players in the scoreboard from game to game.
     */
    private final RecyclerDrawable<GameOverDisplayablePlayerDrawable,
        DisplayablePlayer> scoreBoardRecyclerDrawable =
        new RecyclerDrawable<>(Orientation.VERTICAL,
            GameOverDisplayablePlayerDrawable::new,
            ((gameOverDisplayablePlayerDrawable, displayablePlayer) -> {
                gameOverDisplayablePlayerDrawable.positionTextBox.text("# " + displayablePlayer.getPosition());
                gameOverDisplayablePlayerDrawable.playerNameTextBox.text(displayablePlayer.getName())
                    .color(displayablePlayer.isWinner() ? Color.GREEN : (displayablePlayer.isClientPlayer()
                    ? Color.FOCUS : (displayablePlayer.isConnected() ? Color.WHITE : Color.GREY)));
                gameOverDisplayablePlayerDrawable.playerPointsTextBox.text("Points: " + displayablePlayer.getPoints());
            }));

    /**
     * {@link Button} which allows the user to get back to {@link AvailableGamesMenuLayout}.
     */
    private final Button backToAvailableGamesButton = new Button("Back to available games");

    /**
     * {@link Button} which allows the user to quit the game.
     */
    private final Button exitButton = new Button("Exit");

    // Data:

    /**
     * {@link NetworkEventTransceiver} which allows to detect if the connection to the server has been
     * interrupted.
     */
    private NetworkEventTransceiver transceiver = null;

    /**
     * Constructor of the class.
     * It initializes the layout in which all elements are arranged and sets all the  {@link Button}s'
     * callbacks.
     */
    public GameOverLayout() {
        setLayout(new OrientedLayout(Orientation.VERTICAL,
            new TextBox().text("Scoreboard").unfocusable().center().weight(1),
            scoreBoardRecyclerDrawable.center()
                .scrollable().alignUpLeft().weight(8),
            new OrientedLayout(Orientation.HORIZONTAL,
                new Fill(PrimitiveSymbol.EMPTY).weight(2),
                backToAvailableGamesButton.center().weight(1),
                exitButton.center().weight(1),
                new Fill(PrimitiveSymbol.EMPTY).weight(2)
            ).weight(3)
        ).center().crop());

        setData(new AppLayoutData(
            Map.of()
        ));

        backToAvailableGamesButton.onpress(() -> {
           switchAppLayout(AvailableGamesMenuLayout.NAME);
        });

        exitButton.onpress(() -> {
            transceiver.disconnect();
            mustExit();
        });
    }

    @Override
    public void setup(String previousLayoutName) {
        if (!previousLayoutName.equals(GameLayout.NAME)) {
            throw new IllegalStateException("You can reach GameOverLayout only form GameLayout");
        }

        if (transceiver == null) {
            transceiver = (NetworkEventTransceiver) appDataProvider.get(ConnectionMenuLayout.NAME,
            "transceiver");

            PlayerDisconnectedInternalEventData.castEventReceiver(transceiver).registerListener(data -> {
                transceiver = null;

                if (isCurrentLayout()) {
                    switchAppLayout(ConnectionMenuLayout.NAME);
                }
            });
        }

        scoreBoardRecyclerDrawable.populate(((DisplayableScoreBoard) appDataProvider
                .get(GameLayout.NAME, "scoreboard")).getDisplayablePlayers());
    }

    @Override
    public void beforeSwitch() {

    }

    @Override
    public String getName() {
        return NAME;
    }
}
