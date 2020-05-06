package com.boredgames.server;

import com.boredgames.server.events.BoredEventDto;
import com.boredgames.server.types.GameStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.websocket.Session;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class Game {
    @JsonProperty
    protected GameStatus gameStatus;
    @JsonProperty
    protected Map<String, Player> players = new HashMap<>();

    public Game() {
        this.gameStatus = GameStatus.TO_BE_STARTED;
    }

    public void addPlayer(Player p) {
        if (this.players.isEmpty())

        this.players.put(p.getSessionId(), p);
    }

    public Player getPlayerBySessionId(String sessionId) {
        return this.players.get(sessionId);
    }

    public Player getPlayerByName(String name) {
        Iterator playersIterator = this.players.entrySet().iterator();
        while (playersIterator.hasNext()) {
            Map.Entry mapElt = (Map.Entry)playersIterator.next();
            Player p = (Player)mapElt.getValue();
            if (p.getName().equals(name))
                return p;
        }
        return null;
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

    public Map<String, Player> getPlayers() {
        return players;
    }

    public void broadcastEvent(String message) throws IOException {
        Iterator playersIterator = this.players.entrySet().iterator();
        while (playersIterator.hasNext()) {
            Map.Entry mapElt = (Map.Entry)playersIterator.next();
            Session session = (Session)mapElt.getKey();
            session.getBasicRemote().sendText(message);
        }
    }
}
