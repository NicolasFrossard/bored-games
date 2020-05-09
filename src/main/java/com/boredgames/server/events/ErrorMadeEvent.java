package com.boredgames.server.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class ErrorMadeEvent implements IBoredEvent {

    private final ArrayList<Integer> cardsForcedToPlay;

    @JsonCreator
    public ErrorMadeEvent(@JsonProperty("cardsForcedToPlay") ArrayList<Integer> cardsForcedToPlay) {
        this.cardsForcedToPlay = cardsForcedToPlay;
    }

    public ArrayList<Integer> getCardsForcedToPlay() {
        return this.cardsForcedToPlay;
    }
}