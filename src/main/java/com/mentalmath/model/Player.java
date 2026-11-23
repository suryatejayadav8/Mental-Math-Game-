package com.mentalmath.model;

import java.sql.Timestamp;

/**
 * Represents a registered player in the Mental Math Game.
 * Maps to the 'players' database table.
 */
public class Player {
    private int playerId;
    private String playerName;
    private Timestamp createdAt;

    public Player() {
    }

    public Player(int playerId, String playerName, Timestamp createdAt) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.createdAt = createdAt;
    }

    public Player(String playerName) {
        this.playerName = playerName;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Player [ID: " + playerId + ", Name: '" + playerName + "', Member Since: " + createdAt + "]";
    }
}
