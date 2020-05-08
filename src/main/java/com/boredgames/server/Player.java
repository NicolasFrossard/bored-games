package com.boredgames.server;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class Player {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Player.class);

    @JsonProperty
    private final String name;

    @JsonProperty
    private String socketId;

    @JsonProperty
    private boolean isConnected;

    @JsonProperty
    private final boolean isAdmin;

    @JsonProperty
    private final ArrayList<Integer> cardsInHand;

    public Player(String name, boolean isAdmin, boolean isConnected, String socketId) {
        this.name = name;
        this.isAdmin = isAdmin;
        this.socketId = socketId;
        this.isConnected = isConnected;
        this.cardsInHand = new ArrayList<Integer>();
        LOGGER.debug("New player {}-{}-{}-{}", name, isAdmin, socketId, isConnected);
    }

    public String getName() {
        return name;
    }

    public String getSocketId() {
        return socketId;
    }

    public void giveCard(Integer card) {
        this.cardsInHand.add(card);
    }

    public void setConnected(String socketId) {
        this.socketId = socketId;
        this.isConnected = true;
    }

    public void setDisconnected() {
        this.socketId = null;
        this.isConnected = false;
    }

    public boolean isConnected() {
        return this.isConnected;
    }

    public boolean isAdmin() {
        return this.isAdmin;
    }
}
