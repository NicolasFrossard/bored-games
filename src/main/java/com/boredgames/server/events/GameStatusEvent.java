package com.boredgames.server.events;

import com.boredgames.server.types.GameStatus;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GameStatusEvent implements IBoredEvent {

    @JsonProperty
    private GameStatus gameStatus;

    @JsonCreator
    public GameStatusEvent(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }
}