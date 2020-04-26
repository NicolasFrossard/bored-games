package com.boredgames.server;

import java.util.ArrayList;

public class Player {

    private final String name;
    private final boolean isAdmin;
    private ArrayList<Integer> cardsInHand;

    public Player(String name, boolean isAdmin) {
        this.name = name;
        this.isAdmin = isAdmin;
        this.cardsInHand = new ArrayList<Integer>();
    }

    public String getName() {
        return name;
    }

    public void giveCard(Integer card) {
        this.cardsInHand.add(card);
    }
}
