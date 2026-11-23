package com.mentalmath;

import com.mentalmath.db.DatabaseConnection;
import com.mentalmath.game.Game;
import com.mentalmath.game.QuestionGenerator;
import com.mentalmath.manager.PlayerManager;
import com.mentalmath.manager.ScoreManager;
import com.mentalmath.model.Player;

import java.util.Scanner;

/**
 * Main entry point for the Mental Math Game CLI Application.
 * Guides the user through authentication, gameplay, history, and leaderboards.
 */
public class Main {

    private final PlayerManager playerManager;
    private final ScoreManager scoreManager;
    private final Scanner scanner;
    private Player currentPlayer;

    public Main() {
        this.playerManager = new PlayerManager();
        this.scoreManager = new ScoreManager();
        this.scanner = new Scanner(System.in);
        this.currentPlayer = null;
    }

    public static void main(String[] args) {
        // If user explicitly requests console mode via --console or -c
        if (args.length > 0 && ("--console".equalsIgnoreCase(args[0]) || "-c".equalsIgnoreCase(args[0]))) {
            Main app = new Main();
            app.run();
        } else {
            // Default: Launch modern Java Swing GUI
            com.mentalmath.gui.MainFrame.main(args);
        }
    }

    /**
     * Main application loop.
     */
    public void run() {
        printBanner();

        // 1. Initial Database Connection Check
        if (!DatabaseConnection.testConnection()) {
            promptDatabaseSetup();
        }

        // 2. Main Interaction Loop
        boolean running = true;
        while (running) {
            if (currentPlayer == null) {
                running = showGuestMenu();
            } else {
                running = showPlayerMenu();
            }
        }

        System.out.println("\n👋 Thank you for playing Mental Math Game! Keep exercising your brain. Goodbye!");
        scanner.close();
    }

    /**
     * Displays options when no player is logged in.
     */
    private boolean showGuestMenu() {
        System.out.println("\n==========================================");
        System.out.println("              MAIN MENU                   ");
        System.out.println("==========================================");
        System.out.println("  1. Login Player");
        System.out.println("  2. Register New Player");
        System.out.println("  3. View Global Top 10 Leaderboard");
        System.out.println("  4. Test / Configure DB Connection");
        System.out.println("  5. Exit Game");
        System.out.println("==========================================");
        System.out.print("Select an option (1-5): ");

        int choice = readIntChoice(1, 5);

        switch (choice) {
            case 1:
                handleLogin();
                break;
            case 2:
                handleRegistration();
                break;
            case 3:
                scoreManager.printLeaderboard(scoreManager.getTopScores(10));
                break;
            case 4:
                promptDatabaseSetup();
                break;
            case 5:
                return false;
        }
        return true;
    }

    /**
     * Displays options when a player is currently logged in.
     */
    private boolean showPlayerMenu() {
        System.out.println("\n==========================================");
        System.out.println("  PLAYER DASHBOARD: " + currentPlayer.getPlayerName());
        System.out.println("==========================================");
        System.out.println("  1. Play New Game (10 Questions)");
        System.out.println("  2. View My Score History");
        System.out.println("  3. View Global Top 10 Leaderboard");
        System.out.println("  4. Logout / Switch Player");
        System.out.println("  5. Exit Game");
        System.out.println("==========================================");
        System.out.print("Select an option (1-5): ");

        int choice = readIntChoice(1, 5);

        switch (choice) {
            case 1:
                startNewGame();
                break;
            case 2:
                scoreManager.printPlayerHistory(currentPlayer.getPlayerName(),
                        scoreManager.getPlayerScores(currentPlayer.getPlayerId()));
                break;
            case 3:
                scoreManager.printLeaderboard(scoreManager.getTopScores(10));
                break;
            case 4:
                System.out.println("👋 Logged out " + currentPlayer.getPlayerName() + ".");
                currentPlayer = null;
                break;
            case 5:
                return false;
        }
        return true;
    }

    /**
     * Handles player login.
     */
    private void handleLogin() {
        System.out.print("\nEnter your Player Name: ");
        String name = scanner.nextLine().trim();

        Player player = playerManager.loginPlayer(name);
        if (player != null) {
            this.currentPlayer = player;
        }
    }

    /**
     * Handles new player registration.
     */
    private void handleRegistration() {
        System.out.print("\nChoose a unique Player Name (2-50 characters): ");
        String name = scanner.nextLine().trim();

        Player player = playerManager.registerPlayer(name);
        if (player != null) {
            this.currentPlayer = player;
        }
    }

    /**
     * Prompts for difficulty and launches the game session.
     */
    private void startNewGame() {
        System.out.println("\nSelect Difficulty Level:");
        System.out.println("  1. Easy   (Single/small digits: +,-,*,/ with clean integers)");
        System.out.println("  2. Medium (Double digits: faster pace & larger multipliers)");
        System.out.println("  3. Hard   (Triple digits & advanced arithmetic challenge)");
        System.out.print("Select level (1-3): ");

        int diffChoice = readIntChoice(1, 3);
        QuestionGenerator.Difficulty diff;
        switch (diffChoice) {
            case 2:
                diff = QuestionGenerator.Difficulty.MEDIUM;
                break;
            case 3:
                diff = QuestionGenerator.Difficulty.HARD;
                break;
            case 1:
            default:
                diff = QuestionGenerator.Difficulty.EASY;
                break;
        }

        Game game = new Game(currentPlayer, diff, scoreManager, scanner);
        game.start();
    }

    /**
     * Provides an interactive configuration prompt if the database is not accessible via env vars.
     */
    private void promptDatabaseSetup() {
        System.out.println("\n⚠️ Database connection is not yet established.");
        System.out.println("Options:");
        System.out.println("  1. Enter MySQL User & Password manually for this session");
        System.out.println("  2. Retry connection with existing Environment Variables");
        System.out.println("  3. Continue to Main Menu (Database features may fail until configured)");
        System.out.print("Choose option (1-3): ");

        int opt = readIntChoice(1, 3);
        if (opt == 1) {
            System.out.print("Enter MySQL Username (default 'root'): ");
            String user = scanner.nextLine().trim();
            if (user.isEmpty()) {
                user = "root";
            }

            System.out.print("Enter MySQL Password: ");
            String pwd = scanner.nextLine();

            DatabaseConnection.setCredentials(user, pwd);
            DatabaseConnection.testConnection();
        } else if (opt == 2) {
            DatabaseConnection.testConnection();
        }
    }

    /**
     * Utility method to safely read an integer within a specific range.
     */
    private int readIntChoice(int min, int max) {
        while (true) {
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.printf("  ⚠️ Invalid choice. Please enter a number between %d and %d: ", min, max);
            } catch (NumberFormatException e) {
                System.out.printf("  ⚠️ Please enter a number between %d and %d: ", min, max);
            }
        }
    }

    /**
     * Prints an ASCII welcome banner.
     */
    private void printBanner() {
        System.out.println("======================================================================");
        System.out.println("            🧠 MENTAL MATH GAME - JAVA, JDBC & MYSQL 🧠               ");
        System.out.println("======================================================================");
        System.out.println("  Sharpen your arithmetic agility with random real-time math questions!");
        System.out.println("  Track your speed, accuracy, score history, and global rankings.     ");
        System.out.println("======================================================================");
    }
}
