package it.polimi.ingsw.view.tui;

import it.polimi.ingsw.controller.Response;
import it.polimi.ingsw.controller.ResponseStatus;
import it.polimi.ingsw.event.NetworkEventTransceiver;
import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.data.client.JoinGameEventData;
import it.polimi.ingsw.event.data.client.PlayerExitGame;
import it.polimi.ingsw.event.data.client.StartGameEventData;
import it.polimi.ingsw.event.data.game.GameHasStartedEventData;
import it.polimi.ingsw.event.data.game.PlayerHasDisconnectedEventData;
import it.polimi.ingsw.event.data.game.PlayerHasJoinEventData;
import it.polimi.ingsw.event.data.internal.PlayerDisconnectedInternalEventData;
import it.polimi.ingsw.networking.DisconnectedException;
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
            )
        ));

        startButton.onpress(() -> {
            try {
                displayServerResponse(startGameRequester.request(new StartGameEventData()));
            } catch (DisconnectedException e) {
                displayServerResponse(new Response("Disconnected!", ResponseStatus.FAILURE));
            }
        });

        backButton.onpress(() -> {
            try {
                displayServerResponse(playerExitRequester.request(new PlayerExitGame()));
            } catch (DisconnectedException e) {
                displayServerResponse(new Response("Disconnected!", ResponseStatus.FAILURE));
            }

            switchAppLayout(AvailableGamesMenuLayout.NAME);
        });
    }

    private List<String> playerNames;
    private String gameName;
    private NetworkEventTransceiver transceiver = null;
    private Requester<Response, JoinGameEventData> joinGameRequester = null;
    private Requester<Response, StartGameEventData> startGameRequester = null;
    private Requester<Response, PlayerExitGame> playerExitRequester = null;

    private void populatePlayersList() {
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

    @Override
    public void setup(String previousLayoutName) {
        if (previousLayoutName.equals(AvailableGamesMenuLayout.NAME)) {
            playerNames = new ArrayList<>();

            gameName = appDataProvider.getString(AvailableGamesMenuLayout.NAME, "selectedgame");

            gameTextBox.text(gameName);

            if (transceiver == null) {
                transceiver = (NetworkEventTransceiver) appDataProvider.get(ConnectionMenuLayout.NAME,
                    "transceiver");

                joinGameRequester = Response.requester(transceiver, transceiver, getLock());
                startGameRequester = Response.requester(transceiver, transceiver, getLock());
                playerExitRequester = Response.requester(transceiver, transceiver, getLock());

                PlayerHasJoinEventData.castEventReceiver(transceiver).registerListener(data -> {
                    playerNames.add(data.username());

                    populatePlayersList();
                });

                GameHasStartedEventData.castEventReceiver(transceiver).registerListener(data -> {
                    switchAppLayout(GameLayout.NAME);
                });

                PlayerHasDisconnectedEventData.castEventReceiver(transceiver).registerListener(data -> {
                    playerNames.remove(data.username());

                    populatePlayersList();
                });

                PlayerDisconnectedInternalEventData.castEventReceiver(transceiver).registerListener(data -> {
                    transceiver = null;
                    joinGameRequester = null;
                    startGameRequester = null;
                    playerExitRequester = null;

                    if (isCurrentLayout()) {
                        switchAppLayout(ConnectionMenuLayout.NAME);
                    }
                });
            }

            try {
                displayServerResponse(joinGameRequester.request(new JoinGameEventData(gameName)));
            } catch (DisconnectedException e) {
                displayServerResponse(new Response("Disconnected!", ResponseStatus.FAILURE));
            }
        }
    }

    @Override
    public String getName() {
        return NAME;
    }
}
