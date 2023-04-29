package it.polimi.ingsw.view.tui;

import it.polimi.ingsw.view.tui.terminal.drawable.*;
import it.polimi.ingsw.view.tui.terminal.drawable.app.AppLayout;
import it.polimi.ingsw.view.tui.terminal.drawable.app.AppLayoutData;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.Button;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.value.TextBox;
import it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.OrientedLayout;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.Color;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.PrimitiveSymbol;

import java.util.List;
import java.util.Map;

public class GameOverLayout extends AppLayout {
    public static final String NAME = "GAME_OVER";

    private static class PlayerDisplayDrawable extends FixedLayoutDrawable<Drawable> {
        private final TextBox positionTextBox = new TextBox().unfocusable();
        private final TextBox playerNameTextBox = new TextBox().hideCursor();
        private final TextBox playerPointsTextBox = new TextBox().hideCursor();

        public PlayerDisplayDrawable() {
            setLayout(new OrientedLayout(Orientation.HORIZONTAL,
                positionTextBox.center().weight(1),
                playerNameTextBox.center().weight(1),
                playerPointsTextBox.center().weight(1)
            ).center().crop().fixSize(new DrawableSize(5, 70))
                .addBorderBox());
        }
    }

    private final RecyclerDrawable<PlayerDisplayDrawable, GameLayout.PlayerDisplay> playerDisplayRecyclerDrawable =
        new RecyclerDrawable<>(Orientation.VERTICAL, PlayerDisplayDrawable::new,
            ((playerDisplayDrawable, playerDisplay) -> {
                playerDisplayDrawable.positionTextBox.text("# " + String.valueOf(playerDisplay.getPosition()));
                playerDisplayDrawable.playerNameTextBox.text(playerDisplay.getName())
                    .color(playerDisplay.getPosition() == 1 ? Color.GREEN : (playerDisplay.isClientPlayer()
                    ? Color.FOCUS : Color.WHITE));
                playerDisplayDrawable.playerPointsTextBox.text("Points: " + playerDisplay.getPoints());
            }));

    private final Button backToAvailableGamesButton = new Button("Back to available games");
    private final Button exitButton = new Button("Exit");

    public GameOverLayout() {
        setLayout(new OrientedLayout(Orientation.VERTICAL,
            new TextBox().text("Scoreboard").unfocusable().center().weight(1),
            playerDisplayRecyclerDrawable.center()
                .scrollable().alignUpLeft().weight(8),
            new OrientedLayout(Orientation.HORIZONTAL,
                new Fill(PrimitiveSymbol.EMPTY).weight(1),
                backToAvailableGamesButton.center().weight(1),
                exitButton.center().weight(1),
                new Fill(PrimitiveSymbol.EMPTY).weight(1)
            ).weight(3)
        ).center().crop());

        setData(new AppLayoutData(
            Map.of()
        ));

        backToAvailableGamesButton.onpress(() -> {
           switchAppLayout(AvailableGamesMenuLayout.NAME);
        });

        exitButton.onpress(() -> {
            mustExit();
        });
    }

    @Override
    public void setup(String previousLayoutName) {
        if (previousLayoutName.equals(GameLayout.NAME)) {
            playerDisplayRecyclerDrawable.populate((List<GameLayout.PlayerDisplay>) appDataProvider
                .get(GameLayout.NAME, "scoreboard"));
        }
    }

    @Override
    public String getName() {
        return NAME;
    }
}
