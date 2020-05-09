package com.boredgames.server.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DeletePlayerEvent implements IBoredEvent {

    private final String playerName;

    @JsonCreator
    public DeletePlayerEvent(@JsonProperty("playerName") String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return this.playerName;
    }
}