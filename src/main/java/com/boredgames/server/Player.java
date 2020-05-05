package com.boredgames.server;

import java.util.ArrayList;

public class Player {

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
    }

    public String getName() {
        return name;
    }

    public void giveCard(Integer card) {
        this.cardsInHand.add(card);
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setConnected() {
        this.isConnected = true;
    }

    public void setDisconnected() {
        this.isConnected = false;
    }

    public boolean isConnected() {
        return this.isConnected;
    }
}
