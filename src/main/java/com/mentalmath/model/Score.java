package com.mentalmath.model;

import java.sql.Timestamp;

/**
 * Represents a game session score record.
 * Maps to the 'scores' database table.
 */
public class Score {
    private int scoreId;
    private int playerId;
    private String playerName; // Populated during joins for leaderboard
    private String difficulty;
    private int totalQuestions;
    private int correctAnswers;
    private int score;
    private int timeTaken; // in seconds
    private Timestamp playedAt;

    public Score() {
    }

    public Score(int playerId, String difficulty, int totalQuestions, int correctAnswers, int score, int timeTaken) {
        this.playerId = playerId;
        this.difficulty = difficulty;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.score = score;
        this.timeTaken = timeTaken;
    }

    public Score(int scoreId, int playerId, String playerName, String difficulty, int totalQuestions,
                 int correctAnswers, int score, int timeTaken, Timestamp playedAt) {
        this.scoreId = scoreId;
        this.playerId = playerId;
        this.playerName = playerName;
        this.difficulty = difficulty;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.score = score;
        this.timeTaken = timeTaken;
        this.playedAt = playedAt;
    }

    public int getScoreId() {
        return scoreId;
    }

    public void setScoreId(int scoreId) {
        this.scoreId = scoreId;
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

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(int timeTaken) {
        this.timeTaken = timeTaken;
    }

    public Timestamp getPlayedAt() {
        return playedAt;
    }

    public void setPlayedAt(Timestamp playedAt) {
        this.playedAt = playedAt;
    }

    /**
     * Calculates the player's accuracy percentage for this session.
     */
    public double getAccuracyPercentage() {
        if (totalQuestions <= 0) {
            return 0.0;
        }
        return ((double) correctAnswers / totalQuestions) * 100.0;
    }

    @Override
    public String toString() {
        return String.format("Score[ID=%d, Player=%s, Difficulty=%s, Score=%d, Correct=%d/%d (%.1f%%), Time=%ds, Date=%s]",
                scoreId, (playerName != null ? playerName : String.valueOf(playerId)), difficulty,
                score, correctAnswers, totalQuestions, getAccuracyPercentage(), timeTaken, playedAt);
    }
}
