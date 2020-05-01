package com.boredgames.server;

import com.boredgames.server.types.GameStatus;

import java.util.ArrayList;


public class Game {

    protected GameStatus gameStatus;
    protected ArrayList<Player> players;

    public Game() {
        this.players = new ArrayList<Player>();
        this.gameStatus = GameStatus.TO_BE_STARTED;
    }

    public void addPlayer(Player p) {
        this.players.add(p);
    }

    public boolean isStarted() {
        return this.gameStatus == GameStatus.STARTED;
    }

    public void start() {
        this.gameStatus = GameStatus.STARTED;;
    }

    public void stop() {
        this.gameStatus = GameStatus.TO_BE_STARTED;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }
}
