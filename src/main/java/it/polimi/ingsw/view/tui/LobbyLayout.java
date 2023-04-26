package it.polimi.ingsw.view.tui;

import it.polimi.ingsw.controller.Response;
import it.polimi.ingsw.event.NetworkEventTransceiver;
import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.data.client.JoinGameEventData;
import it.polimi.ingsw.event.data.client.StartGameEventData;
import it.polimi.ingsw.event.data.game.GameHasStartedEventData;
import it.polimi.ingsw.event.data.game.PlayerHasJoinEventData;
import it.polimi.ingsw.view.tui.terminal.drawable.*;
import it.polimi.ingsw.view.tui.terminal.drawable.app.AppLayout;
import it.polimi.ingsw.view.tui.terminal.drawable.app.AppLayoutData;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.Button;
import it.polimi.ingsw.view.tui.terminal.drawable.menu.value.TextBox;
import it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.OrientedLayout;
import it.polimi.ingsw.view.tui.terminal.drawable.orientedlayout.OrientedLayoutElement;
import it.polimi.ingsw.view.tui.terminal.drawable.symbol.PrimitiveSymbol;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LobbyLayout extends AppLayout {
    public static final String NAME = "LOBBY";

    private final TextBox gameTextBox = new TextBox().unfocusable();
    private static class PlayerDrawable extends FixedLayoutDrawable<Drawable> {
        private final TextBox textBox = new TextBox().hideCursor();

        public PlayerDrawable() {
            setLayout(new OrientedLayout(Orientation.HORIZONTAL,
                    new TextBox().text("Player: ").unfocusable().center().weight(1),
                    textBox.center().weight(1)
                ).center().crop().fixSize(new DrawableSize(6, 50))
                .addBorderBox()
            );
        }
    }
    private final RecyclerDrawable<PlayerDrawable, String> recyclerPlayersList = new RecyclerDrawable<>(Orientation.VERTICAL,
        PlayerDrawable::new, (playerDrawable, player) -> playerDrawable.textBox.text(player));
    private final Button startButton = new Button("Start game");
    private final OrientedLayoutElement startButtonLayoutElement = startButton.center().weight(1);
    private final Button backButton = new Button("Back");

    public LobbyLayout() {
        setLayout(new OrientedLayout(Orientation.VERTICAL,
            new OrientedLayout(Orientation.HORIZONTAL,
                new TextBox().text("Game: ").unfocusable().weight(1),
                gameTextBox.weight(1)
            ).center().crop().fixSize(new DrawableSize(3, 40)).center().weight(1),
            recyclerPlayersList.center().scrollable().weight(4),
            new OrientedLayout(Orientation.HORIZONTAL,
                new Fill(PrimitiveSymbol.EMPTY).weight(1),
                startButtonLayoutElement,
                backButton.center().weight(1),
                new Fill(PrimitiveSymbol.EMPTY).weight(1)
            ).weight(1)
        ).center().crop());

        setData(new AppLayoutData(
            Map.of(
                "playernames", () -> playerNames
            )
        ));

        startButton.onpress(() -> {
            displayServerResponse(startGameRequester.request(new StartGameEventData()));
        });

        backButton.onpress(() -> switchAppLayout(AvailableGamesMenuLayout.NAME));
    }

    private List<String> playerNames;
    private String gameName;
    private NetworkEventTransceiver transceiver;
    private Requester<Response, JoinGameEventData> joinGameRequester;
    private Requester<Response, StartGameEventData> startGameRequester;

    @Override
    public void setup(String previousLayoutName) {
        if (previousLayoutName.equals(AvailableGamesMenuLayout.NAME)) {
            playerNames = new ArrayList<>();

            gameName = appDataProvider.getString(AvailableGamesMenuLayout.NAME, "selectedgame");

            gameTextBox.text(gameName);

            transceiver = (NetworkEventTransceiver) appDataProvider.get(ConnectionMenuLayout.NAME,
                "transceiver");

            joinGameRequester = Response.requester(transceiver, transceiver, getLock());
            startGameRequester = Response.requester(transceiver, transceiver, getLock());

            PlayerHasJoinEventData.castEventReceiver(transceiver).registerListener(data -> {
                synchronized (getLock()) {
                    playerNames.add(data.getUsername());

                    recyclerPlayersList.populate(playerNames);

                    boolean isOwner = true;

                    if (isOwner) {
                        startButtonLayoutElement.setWeight(1);

                        if (playerNames.size() >= 2) {
                            startButton.focusable(true);
                        } else {
                            startButton.focusable(false);
                        }
                    } else {
                        startButtonLayoutElement.setWeight(0);
                    }
                }
            });

            GameHasStartedEventData.castEventReceiver(transceiver).registerListener(data -> {
                synchronized (getLock()) {
                    switchAppLayout(GameLayout.NAME);
                }
            });

            displayServerResponse(joinGameRequester.request(new JoinGameEventData(gameName)));
        }
    }

    @Override
    public String getName() {
        return NAME;
    }
}
