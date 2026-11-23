package com.mentalmath.controller;

import com.mentalmath.manager.ScoreManager;
import com.mentalmath.model.Score;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Leaderboard and Player Score History.
 */
@RestController
@RequestMapping("/api")
public class ScoreController {

    private final ScoreManager scoreManager = new ScoreManager();

    /**
     * Retrieves the global top scoring players (defaults to top 10).
     */
    @GetMapping("/leaderboard")
    public ResponseEntity<List<Score>> getLeaderboard(
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 100));
        List<Score> leaderboard = scoreManager.getTopScores(safeLimit);
        return ResponseEntity.ok(leaderboard);
    }

    /**
     * Retrieves all recorded scores for a specific player, ordered by played_at descending.
     */
    @GetMapping("/scores/{playerId}")
    public ResponseEntity<List<Score>> getPlayerScores(@PathVariable("playerId") int playerId) {
        List<Score> history = scoreManager.getPlayerScores(playerId);
        return ResponseEntity.ok(history);
    }
}
