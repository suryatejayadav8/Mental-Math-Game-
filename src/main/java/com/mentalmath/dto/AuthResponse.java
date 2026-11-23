package com.mentalmath.dto;

import com.mentalmath.model.Player;

public class AuthResponse {
    private boolean success;
    private String message;
    private Player player;

    public AuthResponse() {
    }

    public AuthResponse(boolean success, String message, Player player) {
        this.success = success;
        this.message = message;
        this.player = player;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
