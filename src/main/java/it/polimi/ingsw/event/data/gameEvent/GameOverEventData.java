package it.polimi.ingsw.event.data.gameEvent;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;
import it.polimi.ingsw.model.game.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class GameOverEventData implements EventData {

    private final List<Player> winners;
    public static final String ID = "GAME_OVER";

    public GameOverEventData(Collection<Player> winners) {
        this.winners = new ArrayList<>(winners);
    }

    public static <T extends GameOverEventData> CastEventReceiver<T> castEventReceiver(EventReceiver<EventData> receiver) {
        return new CastEventReceiver<>(ID, receiver);
    }

    public static <T extends EventData> Requester<GameOverEventData, T> requester(EventTransmitter transmitter,
                                                                                      EventReceiver<EventData> receiver) {
        return new Requester<>(ID, transmitter, receiver);
    }

    public static <T extends EventData> Responder<GameOverEventData, T> responder(EventTransmitter transmitter,
                                                                                      EventReceiver<EventData> receiver,
                                                                                      Function<GameOverEventData, T> response) {
        return new Responder<>(ID, transmitter, receiver, response);
    }

    public List<Player> getWinners () {
        return new ArrayList<>(this.winners);
    }

    public List<String> getWinnersUsername() {
        return this.winners
                .stream()
                .map(Player::getUsername)
                .toList();
    }

    @Override
    public String getId() {
        return ID;
    }
}
