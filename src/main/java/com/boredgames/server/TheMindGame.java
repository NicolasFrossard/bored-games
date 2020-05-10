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
    private final ArrayList<Integer> cardsPlayed;

    public TheMindGame() {
        super();
        this.round = 0;
        this.lives = 0;
        this.cardsPlayed = new ArrayList<Integer>();
    }

    public void start() {
        super.start();
        this.lives = this.players.size();
    }

    public void stop() {
        super.stop();
        this.round = 0;
        this.lives = 0;
        this.cardsPlayed.clear();
    }

    public void moveToNextRound() {
        ArrayList<Integer> cards = new ArrayList<Integer>();
        IntStream.range(1, 100).forEach(cards::add);
        Collections.shuffle(cards);

        this.round++;
        this.cardsPlayed.clear();

        for (int i = 0; i < this.round && cards.size() >= this.players.size(); i++) {
            this.players.forEach(player -> player.giveCard(cards.remove(0)));
        }
    }

    public boolean isCurrentRoundFinished() {
        return (this.cardsPlayed.size() == this.round * this.players.size());
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

    public ArrayList<Integer> getCardsPlayed() {
        return cardsPlayed;
    }

    public ArrayList<Integer> playCard(Player player, int card) {
        ArrayList<Integer> belowCards = new ArrayList<>();

        player.playCard(card);
        this.cardsPlayed.add(card);

        for (Player p : this.players) {
            for (Integer belowCard : p.playCardsInHandBelow(card)) {
                belowCards.add(belowCard);
                this.cardsPlayed.add(belowCard);
            }
        }
        return (belowCards);
    }
}
