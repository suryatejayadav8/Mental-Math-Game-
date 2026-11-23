package com.mentalmath.controller;

import com.mentalmath.dto.ApiResponse;
import com.mentalmath.dto.GameSubmitRequest;
import com.mentalmath.game.Game;
import com.mentalmath.game.QuestionGenerator;
import com.mentalmath.manager.ScoreManager;
import com.mentalmath.model.Question;
import com.mentalmath.model.Score;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Math Question Generation and Session Score Submission.
 */
@RestController
@RequestMapping("/api/game")
public class GameController {

    private final QuestionGenerator questionGenerator = new QuestionGenerator();
    private final ScoreManager scoreManager = new ScoreManager();

    /**
     * Generates a single arithmetic question based on chosen difficulty.
     */
    @GetMapping("/question")
    public ResponseEntity<Question> getQuestion(
            @RequestParam(value = "difficulty", defaultValue = "EASY") String difficulty) {
        QuestionGenerator.Difficulty diff = parseDifficulty(difficulty);
        Question q = questionGenerator.generateSingleQuestion(diff);
        return ResponseEntity.ok(q);
    }

    /**
     * Generates a batch of arithmetic questions (typically 10) for a game round.
     */
    @GetMapping("/questions")
    public ResponseEntity<List<Question>> getQuestions(
            @RequestParam(value = "difficulty", defaultValue = "EASY") String difficulty,
            @RequestParam(value = "count", defaultValue = "10") int count) {
        QuestionGenerator.Difficulty diff = parseDifficulty(difficulty);
        int safeCount = Math.max(1, Math.min(count, 50));
        List<Question> questions = questionGenerator.generateQuestions(safeCount, diff);
        return ResponseEntity.ok(questions);
    }

    /**
     * Submits and validates a completed game session, saving the score into MySQL.
     */
    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<Score>> submitGame(@RequestBody GameSubmitRequest req) {
        if (req == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Request body cannot be empty."));
        }

        if (req.getPlayerId() <= 0) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Valid player ID is required."));
        }

        QuestionGenerator.Difficulty diff = parseDifficulty(req.getDifficulty());
        int totalQuestions = req.getTotalQuestions() > 0 ? req.getTotalQuestions() : 10;
        int correctAnswers = Math.max(0, Math.min(req.getCorrectAnswers(), totalQuestions));
        int timeTaken = Math.max(1, req.getTimeTaken());

        // Reusing existing game score calculation with speed bonus
        int calculatedScore = Game.calculateScore(correctAnswers, timeTaken, diff);

        Score score = new Score(
                req.getPlayerId(),
                diff.name(),
                totalQuestions,
                correctAnswers,
                calculatedScore,
                timeTaken
        );

        boolean saved = scoreManager.saveScore(score);
        if (saved) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Game score successfully saved!", score));
        }

        String err = scoreManager.getLastErrorMessage();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(err != null ? err : "Failed to record score in database."));
    }

    private QuestionGenerator.Difficulty parseDifficulty(String raw) {
        if (raw == null) {
            return QuestionGenerator.Difficulty.EASY;
        }
        try {
            return QuestionGenerator.Difficulty.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return QuestionGenerator.Difficulty.EASY;
        }
    }
}
