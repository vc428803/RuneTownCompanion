package com.runetown.player;

public class Player {

    private String playerId;
    private String name;
    private int level;
    private int exp;

    public Player(String playerId, String name) {
        this.playerId = playerId;
        this.name = name;
        this.level = 1;
        this.exp = 0;
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public int getExp() {
        return exp;
    }

    public void gainExp(int amount) {
    this.exp += amount;

    if (this.exp >= 100) {
        this.exp -= 100;
        this.level++;
    }
    }
}