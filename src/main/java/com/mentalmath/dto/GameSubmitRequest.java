package com.mentalmath.dto;

public class GameSubmitRequest {
    private int playerId;
    private String difficulty;
    private int totalQuestions = 10;
    private int correctAnswers;
    private int timeTaken;
    private Integer score; // Optional: calculated automatically on backend if null

    public GameSubmitRequest() {
    }

    public GameSubmitRequest(int playerId, String difficulty, int totalQuestions, int correctAnswers, int timeTaken, Integer score) {
        this.playerId = playerId;
        this.difficulty = difficulty;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.timeTaken = timeTaken;
        this.score = score;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
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

    public int getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(int timeTaken) {
        this.timeTaken = timeTaken;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
