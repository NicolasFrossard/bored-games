package com.boredgames.server.events;

import com.boredgames.server.BoredGamesWsServer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;

public class BoredEventDto {

    @JsonProperty
    private final BoredEventType type;

    @JsonProperty
    private final Optional<IBoredEvent> event;

    @JsonCreator
    public BoredEventDto(@JsonProperty("type") BoredEventType type, @JsonProperty("event") Optional<IBoredEvent> event) {
        this.type = type;
        this.event = event;
    }

    public BoredEventType getType() {
        return type;
    }

    public Optional<IBoredEvent> getEvent() {
        return event;
    }
}