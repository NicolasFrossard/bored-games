package com.boredgames.server;

import java.util.ArrayList;
import java.util.List;


public class Game {

    protected enum GameState {
        TO_BE_STARTED,
        STARTED;
    }

    protected GameState gameState;
    protected ArrayList<Player> players;

    public Game() {
        this.players = new ArrayList<Player>();
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

    public GameState getGameState() {
        return gameState;
    }
}
