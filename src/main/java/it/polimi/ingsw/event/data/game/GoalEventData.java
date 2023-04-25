package it.polimi.ingsw.event.data.game;

import it.polimi.ingsw.event.Requester;
import it.polimi.ingsw.event.Responder;
import it.polimi.ingsw.event.data.EventData;
import it.polimi.ingsw.event.receiver.CastEventReceiver;
import it.polimi.ingsw.event.receiver.EventReceiver;
import it.polimi.ingsw.event.transmitter.EventTransmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class GoalEventData implements EventData {
    private final int personalGoal;
    private final List<Integer> commonGoal;

    public GoalEventData(int personalGoal, List<Integer> commonGoal) {
        this.personalGoal = personalGoal;
        this.commonGoal = new ArrayList<>(commonGoal);
    }

    public int getPersonalGoal () {
        return this.personalGoal;
    }

    public List<Integer> getCommonGoal () {
        return new ArrayList<>(commonGoal);
    }

    public static final String ID = "GOAL";

    public static CastEventReceiver<GoalEventData> castEventReceiver(EventReceiver<EventData> receiver) {
        return new CastEventReceiver<>(ID, receiver);
    }

    public static <T extends EventData> Requester<GoalEventData, T> requester(EventTransmitter transmitter,
                                                                                  EventReceiver<EventData> receiver,
                                                                                  Object responsesLock) {
        return new Requester<>(ID, transmitter, receiver, responsesLock);
    }

    public static <T extends EventData> Responder<GoalEventData, T> responder(EventTransmitter transmitter,
                                                                                  EventReceiver<EventData> receiver,
                                                                                  Function<GoalEventData, T> response) {
        return new Responder<>(ID, transmitter, receiver, response);
    }

    @Override
    public String getId() {
        return ID;
    }
}
