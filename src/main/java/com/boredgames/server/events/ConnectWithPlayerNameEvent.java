package com.boredgames.server.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ConnectWithPlayerNameEvent implements IBoredEvent {

    private String playerName;

    @JsonCreator
    public ConnectWithPlayerNameEvent(@JsonProperty("playerName") String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }
}