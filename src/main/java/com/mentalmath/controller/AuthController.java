package com.mentalmath.controller;

import com.mentalmath.dto.AuthRequest;
import com.mentalmath.dto.AuthResponse;
import com.mentalmath.manager.PlayerManager;
import com.mentalmath.model.Player;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller handling player registration, login, and profile retrieval.
 */
@RestController
@RequestMapping("/api")
public class AuthController {

    private final PlayerManager playerManager = new PlayerManager();

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        boolean dbConnected = com.mentalmath.db.DatabaseConnection.checkConnection();
        java.util.Map<String, Object> response = new java.util.LinkedHashMap<>();
        response.put("status", dbConnected ? "UP" : "DEGRADED");
        response.put("database", dbConnected ? "CONNECTED" : "DISCONNECTED");
        response.put("databaseName", "mental_math_game");
        response.put("service", "Mental Math Game REST API");
        response.put("timestamp", System.currentTimeMillis());

        HttpStatus httpStatus = dbConnected ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;
        return ResponseEntity.status(httpStatus).body(response);
    }

    @PostMapping("/auth/register")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthRequest request) {
        if (request == null || request.getPlayerName() == null || request.getPlayerName().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(false, "Player name cannot be empty.", null));
        }

        String sanitized = PlayerManager.sanitizeName(request.getPlayerName());
        if (sanitized == null) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(false, "Invalid player name. Must be 2-50 characters (letters, numbers, underscores).", null));
        }

        Player player = playerManager.registerPlayer(sanitized);
        if (player != null) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new AuthResponse(true, "Registration successful! Welcome, " + player.getPlayerName(), player));
        }

        String err = playerManager.getLastErrorMessage();
        HttpStatus status = (err != null && err.contains("already taken")) ? HttpStatus.CONFLICT : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status)
                .body(new AuthResponse(false, err != null ? err : "Registration failed.", null));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        if (request == null || request.getPlayerName() == null || request.getPlayerName().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(false, "Player name is required.", null));
        }

        String sanitized = PlayerManager.sanitizeName(request.getPlayerName());
        if (sanitized == null) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(false, "Invalid player name format.", null));
        }

        Player player = playerManager.loginPlayer(sanitized);
        if (player != null) {
            return ResponseEntity.ok(new AuthResponse(true, "Login successful! Welcome back, " + player.getPlayerName(), player));
        }

        String err = playerManager.getLastErrorMessage();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new AuthResponse(false, err != null ? err : "Player not found. Please register first.", null));
    }

    @GetMapping("/players/{playerId}")
    public ResponseEntity<?> getPlayer(@PathVariable("playerId") int playerId) {
        Player player = playerManager.getPlayerById(playerId);
        if (player != null) {
            return ResponseEntity.ok(player);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new AuthResponse(false, "Player with ID " + playerId + " not found.", null));
    }
}
