package com.mentalmath.manager;

import com.mentalmath.db.DatabaseConnection;
import com.mentalmath.model.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

/**
 * Handles database operations for players (Registration, Login, Verification).
 * All queries utilize PreparedStatements to prevent SQL injection.
 */
public class PlayerManager {

    private String lastErrorMessage = null;

    public String getLastErrorMessage() {
        return lastErrorMessage;
    }

    /**
     * Registers a new player in the database.
     *
     * @param rawName Desired player name
     * @return Newly created Player object, or null if registration failed
     */
    public Player registerPlayer(String rawName) {
        lastErrorMessage = null;
        String playerName = sanitizeName(rawName);
        if (playerName == null) {
            lastErrorMessage = "Invalid player name. Name must be 2-50 characters (letters, numbers, spaces, underscores).";
            System.out.println("❌ " + lastErrorMessage);
            return null;
        }

        String sql = "INSERT INTO players (player_name) VALUES (?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, playerName);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int playerId = generatedKeys.getInt(1);
                        Timestamp now = new Timestamp(System.currentTimeMillis());
                        System.out.println("✅ Registration successful! Welcome, " + playerName + " (ID: " + playerId + ").");
                        return new Player(playerId, playerName, now);
                    }
                }
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // Duplicate entry error code in MySQL
                lastErrorMessage = "Player name '" + playerName + "' is already taken! Please choose another name or login.";
                System.out.println("⚠️ " + lastErrorMessage);
            } else {
                lastErrorMessage = "Database error during registration: " + e.getMessage();
                System.err.println("❌ " + lastErrorMessage);
            }
        }
        return null;
    }

    /**
     * Logs in an existing player by name.
     *
     * @param rawName Player name
     * @return Player object if found, or null if player does not exist
     */
    public Player loginPlayer(String rawName) {
        lastErrorMessage = null;
        String playerName = sanitizeName(rawName);
        if (playerName == null) {
            lastErrorMessage = "Invalid player name. Please enter a valid name (2-50 characters).";
            System.out.println("❌ " + lastErrorMessage);
            return null;
        }

        String sql = "SELECT player_id, player_name, created_at FROM players WHERE player_name = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, playerName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int playerId = rs.getInt("player_id");
                    String name = rs.getString("player_name");
                    Timestamp createdAt = rs.getTimestamp("created_at");

                    System.out.println("✅ Login successful! Welcome back, " + name + "!");
                    return new Player(playerId, name, createdAt);
                } else {
                    lastErrorMessage = "Player '" + playerName + "' not found. Please register first!";
                    System.out.println("⚠️ " + lastErrorMessage);
                }
            }
        } catch (SQLException e) {
            lastErrorMessage = "Database error during login: " + e.getMessage();
            System.err.println("❌ " + lastErrorMessage);
        }
        return null;
    }

    /**
     * Retrieves a player by their ID.
     *
     * @param playerId ID of the player
     * @return Player object if found, or null
     */
    public Player getPlayerById(int playerId) {
        String sql = "SELECT player_id, player_name, created_at FROM players WHERE player_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, playerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("player_id");
                    String name = rs.getString("player_name");
                    Timestamp createdAt = rs.getTimestamp("created_at");
                    return new Player(id, name, createdAt);
                }
            }
        } catch (SQLException e) {
            lastErrorMessage = "Database error fetching player by ID: " + e.getMessage();
            System.err.println("❌ " + lastErrorMessage);
        }
        return null;
    }

    /**
     * Checks if a player exists by username.
     *
     * @param rawName Player name
     * @return true if exists, false otherwise
     */
    public boolean playerExists(String rawName) {
        String playerName = sanitizeName(rawName);
        if (playerName == null) {
            return false;
        }

        String sql = "SELECT 1 FROM players WHERE player_name = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, playerName);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("❌ Database error checking player: " + e.getMessage());
            return false;
        }
    }

    /**
     * Sanitizes and validates the player's name.
     */
    public static String sanitizeName(String name) {
        if (name == null) {
            return null;
        }
        String trimmed = name.trim();
        if (trimmed.length() < 2 || trimmed.length() > 50) {
            return null;
        }
        // Allows alphanumeric characters, spaces, and underscores
        if (!trimmed.matches("^[a-zA-Z0-9_ ]+$")) {
            return null;
        }
        return trimmed;
    }
}
