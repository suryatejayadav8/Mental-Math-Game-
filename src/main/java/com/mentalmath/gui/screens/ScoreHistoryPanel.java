package com.mentalmath.gui.screens;

import com.mentalmath.gui.MainFrame;
import com.mentalmath.gui.UITheme;
import com.mentalmath.gui.components.ModernTable;
import com.mentalmath.gui.components.RoundedButton;
import com.mentalmath.gui.components.RoundedPanel;
import com.mentalmath.model.Player;
import com.mentalmath.model.Score;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

/**
 * Screen presenting personal score history for the currently logged-in player.
 * Displays summary cards (Total Games, High Score, Avg Accuracy) and a detailed table.
 */
public class ScoreHistoryPanel extends JPanel {

    private final MainFrame mainFrame;
    private DefaultTableModel tableModel;
    private ModernTable historyTable;
    private JLabel statusLabel;
    private RoundedButton refreshButton;

    // Stat Summary Labels
    private JLabel totalGamesMetric;
    private JLabel bestScoreMetric;
    private JLabel avgAccuracyMetric;

    public ScoreHistoryPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(25, 40, 25, 40));

        initUI();
    }

    private void initUI() {
        JPanel centerContainer = new JPanel();
        centerContainer.setOpaque(false);
        centerContainer.setLayout(new BoxLayout(centerContainer, BoxLayout.Y_AXIS));

        // 1. Header Card
        RoundedPanel headerCard = new RoundedPanel(18, UITheme.CARD_BG, UITheme.CARD_BORDER, 1);
        headerCard.setLayout(new BorderLayout());
        headerCard.setBorder(new EmptyBorder(16, 24, 16, 24));
        headerCard.setMaximumSize(new Dimension(860, 75));

        JPanel headerText = new JPanel();
        headerText.setOpaque(false);
        headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("📊 My Score History");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);

        statusLabel = new JLabel("Your recorded game performances");
        statusLabel.setFont(UITheme.FONT_REGULAR_14);
        statusLabel.setForeground(UITheme.ACCENT_CYAN);

        headerText.add(title);
        headerText.add(Box.createVerticalStrut(3));
        headerText.add(statusLabel);

        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnGroup.setOpaque(false);

        refreshButton = new RoundedButton("🔄 Refresh", RoundedButton.Style.SECONDARY);
        refreshButton.setPreferredSize(new Dimension(110, 36));
        refreshButton.addActionListener(e -> loadPlayerHistory());

        RoundedButton backBtn = new RoundedButton("← Dashboard", RoundedButton.Style.PRIMARY);
        backBtn.setPreferredSize(new Dimension(130, 36));
        backBtn.addActionListener(e -> mainFrame.showDashboard());

        btnGroup.add(refreshButton);
        btnGroup.add(backBtn);

        headerCard.add(headerText, BorderLayout.WEST);
        headerCard.add(btnGroup, BorderLayout.EAST);

        centerContainer.add(headerCard);
        centerContainer.add(Box.createVerticalStrut(15));

        // 2. Summary Metrics Row
        JPanel summaryGrid = new JPanel(new GridLayout(1, 3, 16, 0));
        summaryGrid.setOpaque(false);
        summaryGrid.setMaximumSize(new Dimension(860, 75));

        totalGamesMetric = new JLabel("0");
        bestScoreMetric = new JLabel("0 pts");
        avgAccuracyMetric = new JLabel("0%");

        summaryGrid.add(createMiniCard("GAMES PLAYED", totalGamesMetric, UITheme.ACCENT_CYAN));
        summaryGrid.add(createMiniCard("PERSONAL BEST", bestScoreMetric, UITheme.WARNING));
        summaryGrid.add(createMiniCard("AVERAGE ACCURACY", avgAccuracyMetric, UITheme.ACCENT_EMERALD));

        centerContainer.add(summaryGrid);
        centerContainer.add(Box.createVerticalStrut(15));

        // 3. History Table Card
        RoundedPanel tableCard = new RoundedPanel(18, UITheme.CARD_BG, UITheme.CARD_BORDER, 1);
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(new EmptyBorder(16, 20, 16, 20));
        tableCard.setMaximumSize(new Dimension(860, 390));

        String[] columns = {"#", "Difficulty", "Score", "Accuracy", "Correct", "Time (s)", "Played At"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        historyTable = new ModernTable(tableModel);
        historyTable.setRowHeight(36);

        JScrollPane scrollPane = historyTable.createScrollPane();
        scrollPane.setPreferredSize(new Dimension(820, 340));

        tableCard.add(scrollPane, BorderLayout.CENTER);
        centerContainer.add(tableCard);
        centerContainer.add(Box.createVerticalGlue());

        add(centerContainer, BorderLayout.CENTER);
    }

    private JPanel createMiniCard(String title, JLabel valueLbl, java.awt.Color color) {
        RoundedPanel p = new RoundedPanel(14, UITheme.CARD_BG_SOLID, UITheme.CARD_BORDER, 1);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(8, 12, 8, 12));

        JLabel t = new JLabel(title);
        t.setFont(UITheme.FONT_SMALL);
        t.setForeground(UITheme.TEXT_MUTED);
        t.setAlignmentX(Component.CENTER_ALIGNMENT);

        valueLbl.setFont(UITheme.FONT_SECTION_HEADER);
        valueLbl.setForeground(color);
        valueLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(t);
        p.add(Box.createVerticalStrut(2));
        p.add(valueLbl);
        return p;
    }

    /**
     * Loads the logged-in player's history asynchronously via SwingWorker.
     */
    public void loadPlayerHistory() {
        Player player = mainFrame.getCurrentPlayer();
        if (player == null) {
            statusLabel.setText("No player logged in.");
            return;
        }

        refreshButton.setLoading(true);
        statusLabel.setForeground(UITheme.TEXT_SECONDARY);
        statusLabel.setText("Loading past games for " + player.getPlayerName() + "...");

        new SwingWorker<List<Score>, Void>() {
            @Override
            protected List<Score> doInBackground() {
                return mainFrame.getScoreManager().getPlayerScores(player.getPlayerId());
            }

            @Override
            protected void done() {
                refreshButton.setLoading(false);
                try {
                    List<Score> scores = get();
                    tableModel.setRowCount(0);

                    if (scores == null || scores.isEmpty()) {
                        statusLabel.setText("No games played yet. Play a game to record your first score!");
                        totalGamesMetric.setText("0");
                        bestScoreMetric.setText("0 pts");
                        avgAccuracyMetric.setText("0%");
                    } else {
                        statusLabel.setText(String.format("Found %d completed game sessions", scores.size()));

                        int bestScore = 0;
                        double totalAccuracy = 0;
                        int count = 1;

                        for (Score s : scores) {
                            if (s.getScore() > bestScore) {
                                bestScore = s.getScore();
                            }
                            totalAccuracy += s.getAccuracyPercentage();

                            String dateStr = s.getPlayedAt() != null ? s.getPlayedAt().toString().substring(0, 16) : "N/A";
                            tableModel.addRow(new Object[]{
                                    "#" + (count++),
                                    s.getDifficulty(),
                                    s.getScore() + " pts",
                                    String.format("%.1f%%", s.getAccuracyPercentage()),
                                    s.getCorrectAnswers() + " / " + s.getTotalQuestions(),
                                    s.getTimeTaken() + "s",
                                    dateStr
                            });
                        }

                        totalGamesMetric.setText(String.valueOf(scores.size()));
                        bestScoreMetric.setText(bestScore + " pts");
                        avgAccuracyMetric.setText(String.format("%.1f%%", totalAccuracy / scores.size()));
                    }
                } catch (Exception e) {
                    statusLabel.setForeground(UITheme.DANGER);
                    statusLabel.setText("Failed to load history: " + e.getMessage());
                }
            }
        }.execute();
    }
}
