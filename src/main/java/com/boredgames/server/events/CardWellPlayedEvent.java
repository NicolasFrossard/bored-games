package com.boredgames.server.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CardWellPlayedEvent implements IBoredEvent {

    private final int card;

    @JsonCreator
    public CardWellPlayedEvent(@JsonProperty("card") int card) {
        this.card = card;
    }

    public int getCard() {
        return this.card;
    }
}