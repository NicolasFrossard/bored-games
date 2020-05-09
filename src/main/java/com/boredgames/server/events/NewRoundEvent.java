package com.boredgames.server.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NewRoundEvent implements IBoredEvent {

    private final int newRound;

    @JsonCreator
    public NewRoundEvent(@JsonProperty("newRound") int newRound) {
        this.newRound = newRound;
    }

    public int getRound() {
        return this.newRound;
    }
}