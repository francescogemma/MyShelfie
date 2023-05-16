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

public class GameOverLayout extends AppLayout {
    public static final String NAME = "GAME_OVER";

    // Layout:
    private static class GameOverDisplayablePlayerDrawable extends FixedLayoutDrawable<Drawable> {
        private final TextBox positionTextBox = new TextBox().unfocusable();
        private final TextBox playerNameTextBox = new TextBox().hideCursor();
        private final TextBox playerPointsTextBox = new TextBox().hideCursor();

        private GameOverDisplayablePlayerDrawable() {
            setLayout(new OrientedLayout(Orientation.HORIZONTAL,
                positionTextBox.center().weight(1),
                playerNameTextBox.center().weight(1),
                playerPointsTextBox.center().weight(1)
            ).center().crop().fixSize(new DrawableSize(5, 70))
                .addBorderBox());
        }
    }

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

    private final Button backToAvailableGamesButton = new Button("Back to available games");
    private final Button exitButton = new Button("Exit");

    // Data:
    private NetworkEventTransceiver transceiver = null;

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
