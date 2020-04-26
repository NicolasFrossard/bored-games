package com.boredgames.server.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GetGameStateEvent implements IBoredEvent {

    @JsonCreator
    public GetGameStateEvent(@JsonProperty("message") String message) {

    }
}