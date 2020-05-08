package com.boredgames.server.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PlayCardEvent implements IBoredEvent {

    private final int card;
    private final int round;

    @JsonCreator
    public PlayCardEvent(@JsonProperty("card") int card, @JsonProperty("round") int round) {
        this.card = card;
        this.round = round;
    }

    public int getCard() {
        return card;
    }

    public int getRound() {
        return round;
    }
}