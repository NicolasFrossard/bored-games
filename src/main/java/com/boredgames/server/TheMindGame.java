package com.boredgames.server;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;
import java.util.stream.IntStream;

public class TheMindGame extends Game {
    @JsonProperty
    private int round;
    @JsonProperty
    private int lives;
    @JsonProperty
    private final ArrayList<Integer> playedCards;

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
        IntStream.range(1, 100).forEach(cards::add);
        Collections.shuffle(cards);

        this.round++;
        this.playedCards.clear();

        for (int i = 0; i <= this.round && cards.size() >= this.players.size(); i++) {
            this.players.forEach(player -> player.giveCard(cards.remove(0)));
        }
    }

    public boolean isCurrentRoundFinished() {
        return (this.playedCards.size() == this.round * this.players.size());
    }

    public boolean regainOneLife() {
        if (this.lives < this.players.size()) {
            this.lives++;
            return true;
        }
        return false;
    }

    public int lostOneLife() {
        if (this.lives > 0)
            this.lives--;
        return (this.lives);
    }

    public int getLives() {
        return lives;
    }

    public int getRound() {
        return round;
    }

    public ArrayList<Integer> getPlayedCards() {
        return playedCards;
    }

    public ArrayList<Integer> playCard(int card) {
        ArrayList<Integer> belowCards = new ArrayList<>();

        this.playedCards.add(card);

        for (Player player : this.players) {
            for (Integer belowCard : player.playCardsInHandBelow(card)) {
                belowCards.add(belowCard);
                this.playedCards.add(belowCard);
            }
        }
        return (belowCards);
    }
}
