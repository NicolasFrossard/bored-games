package com.boredgames.server;

import com.boredgames.server.types.GameStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.*;


public class Game {
    @JsonProperty
    protected GameStatus status;
    @JsonProperty
    protected List<Player> players = Collections.synchronizedList(new ArrayList<>());

    public Game() {
        this.status = GameStatus.TO_BE_STARTED;
    }

    public void addPlayer(Player p) {
        this.players.add(p);
    }

    public boolean deletePlayerByName(String name) {
        for (Player player : this.players) {
            if (player.getName().equals(name)) {
                this.players.remove(player);
                return true;
            }
        }
        return false;
    }

    public Optional<Player> getPlayerBySessionId(String sessionId) {
        return this.players.stream().filter(p -> p.getSocketId().equals(sessionId)).findFirst();
    }

    public Optional<Player> getPlayerByName(String name) {
        return this.players.stream().filter(p -> p.getName().equals(name)).findFirst();
    }

    public boolean isStarted() {
        return this.status == GameStatus.STARTED;
    }

    public void start() {
        this.status = GameStatus.STARTED;;
    }

    public void stop() {
        this.status = GameStatus.TO_BE_STARTED;
        this.players.forEach(Player::dropAllCardsInHand);
    }

    public GameStatus getStatus() {
        return status;
    }

    public List<Player> getPlayers() {
        return players;
    }
}
