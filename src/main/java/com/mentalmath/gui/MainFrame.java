package com.mentalmath.gui;

import com.mentalmath.db.DatabaseConnection;
import com.mentalmath.game.QuestionGenerator;
import com.mentalmath.gui.components.GradientBackgroundPanel;
import com.mentalmath.gui.screens.DashboardPanel;
import com.mentalmath.gui.screens.GamePanel;
import com.mentalmath.gui.screens.LeaderboardPanel;
import com.mentalmath.gui.screens.LoginPanel;
import com.mentalmath.gui.screens.RegisterPanel;
import com.mentalmath.gui.screens.ResultPanel;
import com.mentalmath.gui.screens.ScoreHistoryPanel;
import com.mentalmath.gui.screens.WelcomePanel;
import com.mentalmath.manager.PlayerManager;
import com.mentalmath.manager.ScoreManager;
import com.mentalmath.model.Player;
import com.mentalmath.model.Score;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.util.List;

/**
 * Main application window and navigation controller for the Java Swing frontend.
 * Manages screen transitions via CardLayout and maintains shared game/player state.
 */
public class MainFrame extends JFrame {

    public static final String CARD_WELCOME     = "WELCOME";
    public static final String CARD_LOGIN       = "LOGIN";
    public static final String CARD_REGISTER    = "REGISTER";
    public static final String CARD_DASHBOARD   = "DASHBOARD";
    public static final String CARD_GAME        = "GAME";
    public static final String CARD_RESULT      = "RESULT";
    public static final String CARD_LEADERBOARD = "LEADERBOARD";
    public static final String CARD_HISTORY     = "HISTORY";

    private final CardLayout cardLayout;
    private final JPanel cardsContainer;

    // Screens
    private final WelcomePanel welcomePanel;
    private final LoginPanel loginPanel;
    private final RegisterPanel registerPanel;
    private final DashboardPanel dashboardPanel;
    private final GamePanel gamePanel;
    private final ResultPanel resultPanel;
    private final LeaderboardPanel leaderboardPanel;
    private final ScoreHistoryPanel scoreHistoryPanel;

    // Backend Managers
    private final PlayerManager playerManager;
    private final ScoreManager scoreManager;
    private Player currentPlayer;

    public MainFrame() {
        super("Mental Math Game – Java Swing & MySQL");

        // 1. Initialize Backend
        this.playerManager = new PlayerManager();
        this.scoreManager = new ScoreManager();
        this.currentPlayer = null;

        // 2. Window Configuration
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1020, 720);
        setMinimumSize(new Dimension(900, 620));
        setLocationRelativeTo(null);

        // 3. CardLayout Container with Navy-to-Purple Gradient Background
        this.cardLayout = new CardLayout();
        this.cardsContainer = new GradientBackgroundPanel(cardLayout);

        // 4. Initialize Screen Panels
        this.welcomePanel = new WelcomePanel(this);
        this.loginPanel = new LoginPanel(this);
        this.registerPanel = new RegisterPanel(this);
        this.dashboardPanel = new DashboardPanel(this);
        this.gamePanel = new GamePanel(this);
        this.resultPanel = new ResultPanel(this);
        this.leaderboardPanel = new LeaderboardPanel(this);
        this.scoreHistoryPanel = new ScoreHistoryPanel(this);

        // 5. Add Screens to CardLayout
        cardsContainer.add(welcomePanel, CARD_WELCOME);
        cardsContainer.add(loginPanel, CARD_LOGIN);
        cardsContainer.add(registerPanel, CARD_REGISTER);
        cardsContainer.add(dashboardPanel, CARD_DASHBOARD);
        cardsContainer.add(gamePanel, CARD_GAME);
        cardsContainer.add(resultPanel, CARD_RESULT);
        cardsContainer.add(leaderboardPanel, CARD_LEADERBOARD);
        cardsContainer.add(scoreHistoryPanel, CARD_HISTORY);

        setContentPane(cardsContainer);

        // 6. Test Database Connection
        checkDatabaseConnection();

        // 7. Start on Welcome Screen
        showWelcome();
    }

    private void checkDatabaseConnection() {
        SwingUtilities.invokeLater(() -> {
            boolean connected = DatabaseConnection.testConnection();
            if (!connected) {
                JOptionPane.showMessageDialog(this,
                        "Could not connect to MySQL database 'mental_math_game'.\n" +
                        "Please verify MySQL server is running and DB_PASSWORD environment variable is set.\n" +
                        "You can still explore the interface, but database operations will require a valid connection.",
                        "Database Connection Notice",
                        JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    // --- Navigation Methods ---

    public void showWelcome() {
        cardLayout.show(cardsContainer, CARD_WELCOME);
    }

    public void showLogin() {
        loginPanel.resetForm();
        cardLayout.show(cardsContainer, CARD_LOGIN);
    }

    public void showRegister() {
        registerPanel.resetForm();
        cardLayout.show(cardsContainer, CARD_REGISTER);
    }

    public void showDashboard() {
        if (currentPlayer == null) {
            showLogin();
            return;
        }
        dashboardPanel.updatePlayerInfo(currentPlayer);
        cardLayout.show(cardsContainer, CARD_DASHBOARD);
    }

    public void showGame(QuestionGenerator.Difficulty difficulty) {
        if (currentPlayer == null) {
            showLogin();
            return;
        }
        gamePanel.startSession(difficulty);
        cardLayout.show(cardsContainer, CARD_GAME);
    }

    public void showResult(Score score, List<GamePanel.QuestionResult> results) {
        resultPanel.displayResults(score, results);
        cardLayout.show(cardsContainer, CARD_RESULT);
    }

    public void showLeaderboard() {
        leaderboardPanel.loadLeaderboardData();
        cardLayout.show(cardsContainer, CARD_LEADERBOARD);
    }

    public void showScoreHistory() {
        if (currentPlayer == null) {
            showLogin();
            return;
        }
        scoreHistoryPanel.loadPlayerHistory();
        cardLayout.show(cardsContainer, CARD_HISTORY);
    }

    public void logout() {
        this.currentPlayer = null;
        showWelcome();
    }

    // --- Getters & Setters ---

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public ScoreManager getScoreManager() {
        return scoreManager;
    }

    /**
     * Standalone main launcher for the Java Swing GUI.
     */
    public static void main(String[] args) {
        // Set Look & Feel for crisp system rendering
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
