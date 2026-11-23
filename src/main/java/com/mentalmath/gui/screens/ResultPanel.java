package com.mentalmath.gui.screens;

import com.mentalmath.game.QuestionGenerator;
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
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

/**
 * Screen presenting session outcome, accuracy percentage, time taken,
 * and a question-by-question review table.
 */
public class ResultPanel extends JPanel {

    private final MainFrame mainFrame;

    private JLabel titleLabel;
    private JLabel scoreMetricLabel;
    private JLabel accuracyMetricLabel;
    private JLabel correctMetricLabel;
    private JLabel timeMetricLabel;

    private DefaultTableModel tableModel;
    private ModernTable breakdownTable;
    private QuestionGenerator.Difficulty lastDifficulty;

    public ResultPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.lastDifficulty = QuestionGenerator.Difficulty.EASY;

        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(25, 40, 25, 40));

        initUI();
    }

    private void initUI() {
        JPanel centerContainer = new JPanel();
        centerContainer.setOpaque(false);
        centerContainer.setLayout(new BoxLayout(centerContainer, BoxLayout.Y_AXIS));

        // 1. Header & Title
        titleLabel = new JLabel("🎉 Challenge Completed!");
        titleLabel.setFont(UITheme.FONT_HERO_TITLE);
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Your game results have been recorded in the MySQL database.");
        subtitle.setFont(UITheme.FONT_REGULAR_14);
        subtitle.setForeground(UITheme.ACCENT_CYAN);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerContainer.add(titleLabel);
        centerContainer.add(Box.createVerticalStrut(4));
        centerContainer.add(subtitle);
        centerContainer.add(Box.createVerticalStrut(20));

        // 2. Metrics Cards Row
        JPanel metricsGrid = new JPanel(new GridLayout(1, 4, 16, 0));
        metricsGrid.setOpaque(false);
        metricsGrid.setMaximumSize(new Dimension(860, 95));

        scoreMetricLabel = new JLabel("0");
        accuracyMetricLabel = new JLabel("0%");
        correctMetricLabel = new JLabel("0 / 10");
        timeMetricLabel = new JLabel("0s");

        metricsGrid.add(createMetricCard("FINAL SCORE", scoreMetricLabel, UITheme.WARNING));
        metricsGrid.add(createMetricCard("ACCURACY", accuracyMetricLabel, UITheme.ACCENT_EMERALD));
        metricsGrid.add(createMetricCard("CORRECT ANSWERS", correctMetricLabel, UITheme.PRIMARY_HOVER));
        metricsGrid.add(createMetricCard("TIME TAKEN", timeMetricLabel, UITheme.ACCENT_CYAN));

        centerContainer.add(metricsGrid);
        centerContainer.add(Box.createVerticalStrut(20));

        // 3. Question Breakdown Table Card
        RoundedPanel tableCard = new RoundedPanel(18, UITheme.CARD_BG, UITheme.CARD_BORDER, 1);
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(new EmptyBorder(16, 20, 16, 20));
        tableCard.setMaximumSize(new Dimension(860, 260));

        JLabel tableHeader = new JLabel("Detailed Question Breakdown");
        tableHeader.setFont(UITheme.FONT_BOLD_14);
        tableHeader.setForeground(UITheme.PRIMARY_HOVER);

        String[] columns = {"#", "Question", "Your Answer", "Correct Answer", "Result"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        breakdownTable = new ModernTable(tableModel);
        breakdownTable.setRowHeight(32);
        JScrollPane scrollPane = breakdownTable.createScrollPane();
        scrollPane.setPreferredSize(new Dimension(800, 190));

        tableCard.add(tableHeader, BorderLayout.NORTH);
        tableCard.add(Box.createVerticalStrut(8), BorderLayout.CENTER);
        tableCard.add(scrollPane, BorderLayout.SOUTH);

        centerContainer.add(tableCard);
        centerContainer.add(Box.createVerticalStrut(20));

        // 4. Action Buttons Card
        RoundedPanel actionCard = new RoundedPanel(16, UITheme.CARD_BG, UITheme.CARD_BORDER, 1);
        actionCard.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 12));
        actionCard.setMaximumSize(new Dimension(860, 68));

        RoundedButton playAgainBtn = new RoundedButton("🔄  Play Again", RoundedButton.Style.SUCCESS);
        playAgainBtn.setPreferredSize(new Dimension(160, 42));
        playAgainBtn.addActionListener(e -> mainFrame.showGame(lastDifficulty));

        RoundedButton dashboardBtn = new RoundedButton("🏠  Dashboard", RoundedButton.Style.PRIMARY);
        dashboardBtn.setPreferredSize(new Dimension(160, 42));
        dashboardBtn.addActionListener(e -> mainFrame.showDashboard());

        RoundedButton leaderboardBtn = new RoundedButton("🏆  Leaderboard", RoundedButton.Style.SECONDARY);
        leaderboardBtn.setPreferredSize(new Dimension(160, 42));
        leaderboardBtn.addActionListener(e -> mainFrame.showLeaderboard());

        actionCard.add(playAgainBtn);
        actionCard.add(dashboardBtn);
        actionCard.add(leaderboardBtn);

        centerContainer.add(actionCard);
        centerContainer.add(Box.createVerticalGlue());

        add(centerContainer, BorderLayout.CENTER);
    }

    private JPanel createMetricCard(String title, JLabel valueLabel, Color accentColor) {
        RoundedPanel card = new RoundedPanel(14, UITheme.CARD_BG_SOLID, UITheme.CARD_BORDER, 1);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(12, 14, 12, 14));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UITheme.FONT_SMALL);
        titleLbl.setForeground(UITheme.TEXT_MUTED);
        titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        valueLabel.setFont(UITheme.FONT_TITLE);
        valueLabel.setForeground(accentColor);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(titleLbl);
        card.add(Box.createVerticalStrut(4));
        card.add(valueLabel);
        return card;
    }

    /**
     * Updates the result screen with the finished session details.
     */
    public void displayResults(Score score, List<GamePanel.QuestionResult> results) {
        if (score.getDifficulty() != null) {
            try {
                this.lastDifficulty = QuestionGenerator.Difficulty.valueOf(score.getDifficulty());
            } catch (Exception ignored) {
            }
        }

        scoreMetricLabel.setText(String.valueOf(score.getScore()) + " pts");
        accuracyMetricLabel.setText(String.format("%.1f%%", score.getAccuracyPercentage()));
        correctMetricLabel.setText(score.getCorrectAnswers() + " / " + score.getTotalQuestions());
        timeMetricLabel.setText(score.getTimeTaken() + "s");

        // Populate Table
        tableModel.setRowCount(0);
        int qNum = 1;
        for (GamePanel.QuestionResult res : results) {
            String status = res.isCorrect ? "✅ Correct" : "❌ Wrong";
            tableModel.addRow(new Object[]{
                    "Q" + (qNum++),
                    res.question.getQuestionText().replace(" = ?", ""),
                    res.userAnswer,
                    res.question.getCorrectAnswer(),
                    status
            });
        }
    }
}
