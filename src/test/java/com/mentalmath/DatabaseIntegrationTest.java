package com.mentalmath;

import com.mentalmath.db.DatabaseConnection;
import com.mentalmath.manager.PlayerManager;
import com.mentalmath.manager.ScoreManager;
import com.mentalmath.model.Player;
import com.mentalmath.model.Score;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.util.List;

import static org.junit.Assert.*;

public class DatabaseIntegrationTest {

    @BeforeClass
    public static void checkDbAvailable() {
        // Only run integration tests if database is accessible
        boolean connected = DatabaseConnection.testConnection();
        Assume.assumeTrue("MySQL database mental_math_game is reachable", connected);
    }

    @Test
    public void testDatabaseConnection() throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            assertNotNull("Connection should not be null", conn);
            assertFalse("Connection should be open", conn.isClosed());
        }
    }

    @Test
    public void testPlayerAndScoreCycle() {
        PlayerManager playerManager = new PlayerManager();
        ScoreManager scoreManager = new ScoreManager();

        String testUsername = "TestPlayer_" + System.currentTimeMillis() % 100000;

        // 1. Register test player
        Player registered = playerManager.registerPlayer(testUsername);
        assertNotNull("Player registration should succeed", registered);
        assertTrue("Player ID should be generated", registered.getPlayerId() > 0);

        // 2. Login test player
        Player loggedIn = playerManager.loginPlayer(testUsername);
        assertNotNull("Player login should succeed", loggedIn);
        assertEquals(registered.getPlayerId(), loggedIn.getPlayerId());

        // 3. Save a score record
        Score score = new Score(loggedIn.getPlayerId(), "EASY", 10, 9, 95, 38);
        boolean saved = scoreManager.saveScore(score);
        assertTrue("Score saving should succeed", saved);

        // 4. Retrieve player history
        List<Score> history = scoreManager.getPlayerScores(loggedIn.getPlayerId());
        assertNotNull("History should not be null", history);
        assertFalse("History should contain the saved score", history.isEmpty());
        assertEquals("EASY", history.get(0).getDifficulty());
        assertEquals(95, history.get(0).getScore());

        // 5. Retrieve global leaderboard
        List<Score> leaderboard = scoreManager.getTopScores(10);
        assertNotNull("Leaderboard should not be null", leaderboard);
        assertFalse("Leaderboard should contain records", leaderboard.isEmpty());
    }

    @Test
    public void testReadExistingDataSafely() {
        ScoreManager scoreManager = new ScoreManager();
        List<Score> topScores = scoreManager.getTopScores(10);
        assertNotNull("Top scores query should not be null", topScores);
        // Verify JOIN query integrity
        for (Score s : topScores) {
            assertTrue("Valid score ID", s.getScoreId() > 0);
            assertTrue("Valid player ID", s.getPlayerId() > 0);
            assertNotNull("Player name resolved via SQL JOIN", s.getPlayerName());
        }
    }
}
