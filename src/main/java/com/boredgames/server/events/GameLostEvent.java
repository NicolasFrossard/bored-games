package com.boredgames.server.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GameLostEvent implements IBoredEvent {

    private final int round;

    @JsonCreator
    public GameLostEvent(@JsonProperty("round") int round) {
        this.round = round;
    }

    public int getRound() {
        return this.round;
    }
}