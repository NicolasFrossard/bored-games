package com.boredgames.server.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class BoredEventDto {
    private final BoredEventType type;

    private final JsonNode event;

    @JsonCreator
    public BoredEventDto(@JsonProperty("type") BoredEventType type, @JsonProperty("event") JsonNode event) {
        this.type = type;
        this.event = event;
    }

    public BoredEventType getType() {
        return type;
    }

    public JsonNode getEvent() {
        return event;
    }
}