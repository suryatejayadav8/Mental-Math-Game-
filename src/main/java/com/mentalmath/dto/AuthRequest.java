package com.mentalmath.dto;

public class AuthRequest {
    private String playerName;
    private String password; // Optional field for future password support

    public AuthRequest() {
    }

    public AuthRequest(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
