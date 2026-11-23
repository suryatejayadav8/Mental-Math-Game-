package com.mentalmath.manager;

import com.mentalmath.db.DatabaseConnection;
import com.mentalmath.model.Score;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles database operations for game scores:
 * - Saving game session results
 * - Retrieving individual player history
 * - Retrieving top-10 global leaderboard
 */
public class ScoreManager {

    private String lastErrorMessage = null;

    public String getLastErrorMessage() {
        return lastErrorMessage;
    }

    /**
     * Saves a completed game session score into the MySQL 'scores' table.
     *
     * @param score Score object containing game results
     * @return true if saved successfully, false otherwise
     */
    public boolean saveScore(Score score) {
        lastErrorMessage = null;
        String sql = "INSERT INTO scores (player_id, difficulty, total_questions, correct_answers, score, time_taken) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, score.getPlayerId());
            stmt.setString(2, score.getDifficulty());
            stmt.setInt(3, score.getTotalQuestions());
            stmt.setInt(4, score.getCorrectAnswers());
            stmt.setInt(5, score.getScore());
            stmt.setInt(6, score.getTimeTaken());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("💾 Game result successfully saved to database!");
                return true;
            }
        } catch (SQLException e) {
            lastErrorMessage = "Database error saving score: " + e.getMessage();
            System.err.println("❌ " + lastErrorMessage);
        }
        return false;
    }

    /**
     * Fetches all scores recorded for a specific player, ordered from newest to oldest.
     *
     * @param playerId ID of the player
     * @return List of Score records
     */
    public List<Score> getPlayerScores(int playerId) {
        List<Score> history = new ArrayList<>();
        String sql = "SELECT score_id, player_id, difficulty, total_questions, correct_answers, score, time_taken, played_at " +
                     "FROM scores WHERE player_id = ? ORDER BY played_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, playerId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int scoreId = rs.getInt("score_id");
                    String difficulty = rs.getString("difficulty");
                    int total = rs.getInt("total_questions");
                    int correct = rs.getInt("correct_answers");
                    int score = rs.getInt("score");
                    int timeTaken = rs.getInt("time_taken");
                    Timestamp playedAt = rs.getTimestamp("played_at");

                    history.add(new Score(scoreId, playerId, null, difficulty, total, correct, score, timeTaken, playedAt));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Database error fetching player history: " + e.getMessage());
        }
        return history;
    }

    /**
     * Fetches the top scores globally across all players.
     * Ordered by highest score first, then lowest time taken.
     *
     * @param limit Maximum number of leaderboard records to fetch (e.g. 10)
     * @return List of top Score records with player names
     */
    public List<Score> getTopScores(int limit) {
        List<Score> leaderboard = new ArrayList<>();
        String sql = "SELECT s.score_id, s.player_id, p.player_name, s.difficulty, s.total_questions, " +
                     "       s.correct_answers, s.score, s.time_taken, s.played_at " +
                     "FROM scores s " +
                     "JOIN players p ON s.player_id = p.player_id " +
                     "ORDER BY s.score DESC, s.time_taken ASC " +
                     "LIMIT ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int scoreId = rs.getInt("score_id");
                    int playerId = rs.getInt("player_id");
                    String playerName = rs.getString("player_name");
                    String difficulty = rs.getString("difficulty");
                    int total = rs.getInt("total_questions");
                    int correct = rs.getInt("correct_answers");
                    int score = rs.getInt("score");
                    int timeTaken = rs.getInt("time_taken");
                    Timestamp playedAt = rs.getTimestamp("played_at");

                    leaderboard.add(new Score(scoreId, playerId, playerName, difficulty, total, correct, score, timeTaken, playedAt));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Database error fetching leaderboard: " + e.getMessage());
        }
        return leaderboard;
    }

    /**
     * Pretty-prints a player's score history to the console in a table format.
     */
    public void printPlayerHistory(String playerName, List<Score> history) {
        System.out.println("\n=========================================================================================");
        System.out.println("                             SCORE HISTORY FOR: " + playerName);
        System.out.println("=========================================================================================");

        if (history.isEmpty()) {
            System.out.println("  No game sessions played yet. Start a game to build your record!");
            System.out.println("=========================================================================================");
            return;
        }

        System.out.printf("%-6s | %-10s | %-7s | %-8s | %-10s | %-10s | %-19s%n",
                "Game #", "Difficulty", "Score", "Correct", "Accuracy", "Time (s)", "Played At");
        System.out.println("-----------------------------------------------------------------------------------------");

        int count = 1;
        for (Score s : history) {
            String dateStr = s.getPlayedAt() != null ? s.getPlayedAt().toString().substring(0, 19) : "N/A";
            System.out.printf("%-6d | %-10s | %-7d | %-2d/%-5d | %8.1f%% | %-10d | %-19s%n",
                    count++,
                    s.getDifficulty(),
                    s.getScore(),
                    s.getCorrectAnswers(),
                    s.getTotalQuestions(),
                    s.getAccuracyPercentage(),
                    s.getTimeTaken(),
                    dateStr);
        }
        System.out.println("=========================================================================================");
    }

    /**
     * Pretty-prints the global leaderboard table to the console.
     */
    public void printLeaderboard(List<Score> leaderboard) {
        System.out.println("\n===================================================================================================");
        System.out.println("                                  GLOBAL TOP 10 LEADERBOARD                                        ");
        System.out.println("===================================================================================================");

        if (leaderboard.isEmpty()) {
            System.out.println("  No records on the leaderboard yet. Be the first to set a high score!");
            System.out.println("===================================================================================================");
            return;
        }

        System.out.printf("%-4s | %-20s | %-10s | %-7s | %-8s | %-10s | %-10s | %-19s%n",
                "Rank", "Player", "Difficulty", "Score", "Correct", "Accuracy", "Time (s)", "Date");
        System.out.println("---------------------------------------------------------------------------------------------------");

        int rank = 1;
        for (Score s : leaderboard) {
            String rankBadge = (rank == 1) ? "🥇 1" : (rank == 2) ? "🥈 2" : (rank == 3) ? "🥉 3" : String.format("   %d", rank);
            String dateStr = s.getPlayedAt() != null ? s.getPlayedAt().toString().substring(0, 19) : "N/A";
            System.out.printf("%-4s | %-20s | %-10s | %-7d | %-2d/%-5d | %8.1f%% | %-10d | %-19s%n",
                    rankBadge,
                    s.getPlayerName(),
                    s.getDifficulty(),
                    s.getScore(),
                    s.getCorrectAnswers(),
                    s.getTotalQuestions(),
                    s.getAccuracyPercentage(),
                    s.getTimeTaken(),
                    dateStr);
            rank++;
        }
        System.out.println("===================================================================================================");
    }
}
