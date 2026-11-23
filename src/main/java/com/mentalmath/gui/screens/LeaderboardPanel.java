package com.mentalmath.gui.screens;

import com.mentalmath.gui.MainFrame;
import com.mentalmath.gui.UITheme;
import com.mentalmath.gui.components.ModernTable;
import com.mentalmath.gui.components.RoundedButton;
import com.mentalmath.gui.components.RoundedPanel;
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
import java.util.List;

/**
 * Screen displaying the Top 10 global leaderboard fetched from MySQL.
 * Loads records asynchronously using SwingWorker to prevent UI freezing.
 */
public class LeaderboardPanel extends JPanel {

    private final MainFrame mainFrame;
    private DefaultTableModel tableModel;
    private ModernTable leaderboardTable;
    private JLabel statusLabel;
    private RoundedButton refreshButton;

    public LeaderboardPanel(MainFrame mainFrame) {
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
        headerCard.setMaximumSize(new Dimension(860, 80));

        JPanel headerText = new JPanel();
        headerText.setOpaque(false);
        headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("🏆 Global Top 10 Leaderboard");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);

        statusLabel = new JLabel("Best calculation records across all players");
        statusLabel.setFont(UITheme.FONT_REGULAR_14);
        statusLabel.setForeground(UITheme.ACCENT_CYAN);

        headerText.add(title);
        headerText.add(Box.createVerticalStrut(4));
        headerText.add(statusLabel);

        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnGroup.setOpaque(false);

        refreshButton = new RoundedButton("🔄 Refresh", RoundedButton.Style.SECONDARY);
        refreshButton.setPreferredSize(new Dimension(110, 36));
        refreshButton.addActionListener(e -> loadLeaderboardData());

        RoundedButton backBtn = new RoundedButton("← Dashboard", RoundedButton.Style.PRIMARY);
        backBtn.setPreferredSize(new Dimension(130, 36));
        backBtn.addActionListener(e -> mainFrame.showDashboard());

        btnGroup.add(refreshButton);
        btnGroup.add(backBtn);

        headerCard.add(headerText, BorderLayout.WEST);
        headerCard.add(btnGroup, BorderLayout.EAST);

        centerContainer.add(headerCard);
        centerContainer.add(Box.createVerticalStrut(20));

        // 2. Leaderboard Table Card
        RoundedPanel tableCard = new RoundedPanel(18, UITheme.CARD_BG, UITheme.CARD_BORDER, 1);
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(new EmptyBorder(18, 20, 18, 20));
        tableCard.setMaximumSize(new Dimension(860, 440));

        String[] columns = {"Rank", "Player", "Difficulty", "Score", "Accuracy", "Correct", "Time (s)", "Played At"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        leaderboardTable = new ModernTable(tableModel);
        leaderboardTable.setRowHeight(38);

        // Adjust column widths
        leaderboardTable.getColumnModel().getColumn(0).setPreferredWidth(70);
        leaderboardTable.getColumnModel().getColumn(1).setPreferredWidth(160);
        leaderboardTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        leaderboardTable.getColumnModel().getColumn(3).setPreferredWidth(90);

        JScrollPane scrollPane = leaderboardTable.createScrollPane();
        scrollPane.setPreferredSize(new Dimension(820, 380));

        tableCard.add(scrollPane, BorderLayout.CENTER);
        centerContainer.add(tableCard);
        centerContainer.add(Box.createVerticalGlue());

        add(centerContainer, BorderLayout.CENTER);
    }

    /**
     * Loads the top 10 scores asynchronously via SwingWorker.
     */
    public void loadLeaderboardData() {
        refreshButton.setLoading(true);
        statusLabel.setForeground(UITheme.TEXT_SECONDARY);
        statusLabel.setText("Fetching top scores from MySQL...");

        new SwingWorker<List<Score>, Void>() {
            @Override
            protected List<Score> doInBackground() {
                return mainFrame.getScoreManager().getTopScores(10);
            }

            @Override
            protected void done() {
                refreshButton.setLoading(false);
                try {
                    List<Score> scores = get();
                    tableModel.setRowCount(0);

                    if (scores == null || scores.isEmpty()) {
                        statusLabel.setText("No game records yet. Be the first to rank on the leaderboard!");
                    } else {
                        statusLabel.setText(String.format("Showing Top %d highest scoring records", scores.size()));
                        int rank = 1;
                        for (Score s : scores) {
                            String rankBadge = (rank == 1) ? "🥇 1st" :
                                               (rank == 2) ? "🥈 2nd" :
                                               (rank == 3) ? "🥉 3rd" : (rank + "th");

                            String dateStr = s.getPlayedAt() != null ? s.getPlayedAt().toString().substring(0, 16) : "N/A";
                            tableModel.addRow(new Object[]{
                                    rankBadge,
                                    s.getPlayerName(),
                                    s.getDifficulty(),
                                    s.getScore() + " pts",
                                    String.format("%.1f%%", s.getAccuracyPercentage()),
                                    s.getCorrectAnswers() + " / " + s.getTotalQuestions(),
                                    s.getTimeTaken() + "s",
                                    dateStr
                            });
                            rank++;
                        }
                    }
                } catch (Exception e) {
                    statusLabel.setForeground(UITheme.DANGER);
                    statusLabel.setText("Failed to load leaderboard: " + e.getMessage());
                }
            }
        }.execute();
    }
}
