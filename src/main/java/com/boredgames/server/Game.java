package com.boredgames.server;

import java.util.List;


public class Game {

    private enum GameState {
        TO_BE_STARTED,
        STARTED;
    }

    private GameState gameState;
    private List<Player> players;

    public Game() {
        this.gameState = GameState.TO_BE_STARTED;
    }

    public void addPlayer(Player p) {
        this.players.add(p);
    }

    public boolean isStarted() {
        return this.gameState == GameState.STARTED;
    }

    public void start() {
        this.gameState = GameState.STARTED;;
    }

    public void stop() {
        this.gameState = GameState.TO_BE_STARTED;
    }
}
