package com.boredgames.server;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.websocket.Session;
import java.util.*;
import java.util.stream.IntStream;

public class TheMindGame extends Game {
    @JsonProperty
    private int round;
    @JsonProperty
    private int lives;
    @JsonProperty
    private ArrayList<Integer> playedCards;

    public TheMindGame() {
        super();
        this.round = 0;
        this.lives = 0;
        this.playedCards = new ArrayList<Integer>();
    }

    public void start() {
        super.start();
        this.lives = this.players.size();
    }

    public void stop() {
        super.stop();
        this.round = 0;
    }

    public void moveToNextRound() {
        ArrayList<Integer> cards = new ArrayList<Integer>();
        IntStream.range(1, 100).forEach(card -> cards.add(card));
        Collections.shuffle(cards);

        this.round++;
        this.playedCards.clear();

        for (int i = 0; i <= this.round && cards.size() >= this.players.size(); i++) {
            this.players.forEach((k,v) -> v.giveCard(cards.remove(0)));
        }
    }

    public boolean isCurrentRoundFinished() {
        return (this.playedCards.size() == this.round * this.players.size());
    }

    public void regainOneLife() {
        if (this.lives < this.players.size())
            this.lives++;
    }

    public void playCard() {

    }
}
