package com.mentalmath.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages database connectivity using JDBC for the Mental Math Game.
 * Reads credentials securely from environment variables to avoid hardcoding secrets.
 */
public class DatabaseConnection {

    // Default configuration (can be overridden via environment variables)
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/mental_math_game?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DEFAULT_USER = "root";

    // Runtime credentials (can be set programmatically if not provided via env variables)
    private static String runtimePassword = null;
    private static String runtimeUser = null;
    private static String runtimeUrl = null;

    /**
     * Resolves the database URL from environment variable DB_URL or defaults to localhost.
     */
    public static String getUrl() {
        if (runtimeUrl != null && !runtimeUrl.trim().isEmpty()) {
            return runtimeUrl;
        }
        String envUrl = System.getenv("DB_URL");
        if (envUrl != null && !envUrl.trim().isEmpty()) {
            return envUrl;
        }
        String propUrl = System.getProperty("DB_URL");
        if (propUrl != null && !propUrl.trim().isEmpty()) {
            return propUrl;
        }
        String springUrl = System.getProperty("spring.datasource.url");
        if (springUrl != null && !springUrl.trim().isEmpty()) {
            return springUrl;
        }
        return DEFAULT_URL;
    }

    /**
     * Resolves the database user from environment variable DB_USER or defaults to 'root'.
     */
    public static String getUser() {
        if (runtimeUser != null && !runtimeUser.trim().isEmpty()) {
            return runtimeUser;
        }
        String envUser = System.getenv("DB_USER");
        if (envUser != null && !envUser.trim().isEmpty()) {
            return envUser;
        }
        String propUser = System.getProperty("DB_USER");
        if (propUser != null && !propUser.trim().isEmpty()) {
            return propUser;
        }
        String springUser = System.getProperty("spring.datasource.username");
        if (springUser != null && !springUser.trim().isEmpty()) {
            return springUser;
        }
        return DEFAULT_USER;
    }

    /**
     * Resolves the database password from environment variable DB_PASSWORD or MYSQL_PWD.
     */
    public static String getPassword() {
        if (runtimePassword != null) {
            return runtimePassword;
        }
        String envPassword = System.getenv("DB_PASSWORD");
        if (envPassword != null) {
            return envPassword;
        }
        String propPassword = System.getProperty("DB_PASSWORD");
        if (propPassword != null) {
            return propPassword;
        }
        String springPassword = System.getProperty("spring.datasource.password");
        if (springPassword != null) {
            return springPassword;
        }
        String mysqlPwd = System.getenv("MYSQL_PWD");
        return (mysqlPwd != null) ? mysqlPwd : "";
    }

    /**
     * Allows setting credentials at runtime (useful for interactive console prompts).
     */
    public static void setCredentials(String user, String password) {
        runtimeUser = user;
        runtimePassword = password;
    }

    /**
     * Obtains a new database connection using JDBC.
     *
     * @return Connection object
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Load MySQL JDBC driver (modern connector auto-registers, but explicit load is good practice)
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found. Ensure mysql-connector-j is included in pom.xml.", e);
        }

        return DriverManager.getConnection(getUrl(), getUser(), getPassword());
    }

    /**
     * Checks if the database is reachable without writing logs or throwing.
     *
     * @return true if connection is active, false otherwise
     */
    public static boolean checkConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed() && conn.isValid(3);
        } catch (Throwable t) {
            return false;
        }
    }

    /**
     * Tests the database connection and prints diagnostic information.
     *
     * @return true if connection succeeds, false otherwise
     */
    public static boolean testConnection() {
        System.out.println("\n--- Testing Database Connection ---");
        System.out.println("Connecting to: " + getUrl());
        System.out.println("Database User: " + getUser());

        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("✓ Successfully connected to MySQL database: mental_math_game");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ Failed to connect to database!");
            System.err.println("Error Message: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            
            if (e.getErrorCode() == 1045) {
                System.err.println("Tip: Access denied. Check your DB_USER and DB_PASSWORD environment variables.");
            } else if (e.getErrorCode() == 1049) {
                System.err.println("Tip: Unknown database 'mental_math_game'. Verify that the database exists.");
            } else if (e.getMessage() != null && e.getMessage().contains("Communications link failure")) {
                System.err.println("Tip: Cannot reach MySQL server. Verify that the MySQL service is running on port 3306.");
            }
        }
        return false;
    }

    public static void main(String[] args) {
        System.out.println("=== Standalone Database Connection Tester ===");
        boolean connected = testConnection();
        if (connected) {
            System.out.println("Connection test PASSED! Database is ready for the Mental Math Game.");
        } else {
            System.out.println("Connection test FAILED. Please review the tips above.");
        }
    }
}
