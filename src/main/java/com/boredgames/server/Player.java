package com.boredgames.server;

import org.slf4j.LoggerFactory;

import javax.websocket.Session;
import java.util.ArrayList;

public class Player {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Player.class);

    private final String name;
    private String sessionId;
    private boolean isConnected;
    private final boolean isAdmin;
    private ArrayList<Integer> cardsInHand;

    public Player(String name, boolean isAdmin, boolean isConnected, String sessionId) {
        this.name = name;
        this.isAdmin = isAdmin;
        this.sessionId = sessionId;
        this.isConnected = isConnected;
        this.cardsInHand = new ArrayList<Integer>();
        LOGGER.debug("New player {}-{}-{}-{}", name, isAdmin, sessionId, isConnected);
    }

    public String getName() {
        return name;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public void giveCard(Integer card) {
        this.cardsInHand.add(card);
    }

    public void setConnected(String sessionId) {
        this.sessionId = sessionId;
        this.isConnected = true;
    }

    public void setDisconnected() {
        this.sessionId = null;
        this.isConnected = false;
    }

    public boolean isConnected() {
        return this.isConnected;
    }
}
