package it.polimi.ingsw.event.data.game;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;
import it.polimi.ingsw.model.bookshelf.BookshelfMaskSet;
import it.polimi.ingsw.model.game.PlayerView;
import it.polimi.ingsw.utils.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class GameOverEventData implements EventData {
    public static final String ID = "GAME_OVER";

    private final List<PlayerView> winners;
    private final List<Pair<Integer, BookshelfMaskSet>> personalGoal;

    public GameOverEventData(List<PlayerView> winners, List<Pair<Integer, BookshelfMaskSet>> personalGoal) {
        this.winners = new ArrayList<>(winners);
        this.personalGoal = new ArrayList<>(personalGoal);
    }

    public List<Pair<Integer, BookshelfMaskSet>> getPersonalGoal() {
        return Collections.unmodifiableList(personalGoal);
    }

    public List<PlayerView> getWinners () {
        return Collections.unmodifiableList(this.winners);
    }


    public static CastEventReceiver<GameOverEventData> castEventReceiver(EventReceiver<EventData> receiver) {
        return new CastEventReceiver<>(ID, receiver);
    }

    public static <T extends EventData> Requester<GameOverEventData, T> requester(EventTransmitter transmitter,
                                                                                      EventReceiver<EventData> receiver,
                                                                                      Object responsesLock) {
        return new Requester<>(ID, transmitter, receiver, responsesLock);
    }

    public static <T extends EventData> Responder<GameOverEventData, T> responder(EventTransmitter transmitter,
                                                                                      EventReceiver<EventData> receiver,
                                                                                      Function<GameOverEventData, T> response) {
        return new Responder<>(ID, transmitter, receiver, response);
    }

    public List<String> getWinnersUsername() {
        return this.winners
                .stream()
                .map(PlayerView::getUsername)
                .toList();
    }

    @Override
    public String getId() {
        return ID;
    }
}
