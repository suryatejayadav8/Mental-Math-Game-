package com.mentalmath.gui.screens;

import com.mentalmath.game.Game;
import com.mentalmath.game.QuestionGenerator;
import com.mentalmath.gui.MainFrame;
import com.mentalmath.gui.UITheme;
import com.mentalmath.gui.components.ModernTextField;
import com.mentalmath.gui.components.RoundedButton;
import com.mentalmath.gui.components.RoundedPanel;
import com.mentalmath.model.Player;
import com.mentalmath.model.Question;
import com.mentalmath.model.Score;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * Interactive Game Screen for the 10-question mental calculation challenge.
 * Manages question flow, live timer, real-time score display, and answer verification.
 */
public class GamePanel extends JPanel {

    public static class QuestionResult {
        public final Question question;
        public final int userAnswer;
        public final boolean isCorrect;

        public QuestionResult(Question question, int userAnswer, boolean isCorrect) {
            this.question = question;
            this.userAnswer = userAnswer;
            this.isCorrect = isCorrect;
        }
    }

    private static final int TOTAL_QUESTIONS = 10;

    private final MainFrame mainFrame;
    private final QuestionGenerator questionGenerator;

    // Session State
    private QuestionGenerator.Difficulty currentDifficulty;
    private List<Question> questionList;
    private List<QuestionResult> sessionResults;
    private int currentQuestionIndex = 0;
    private int runningScore = 0;
    private int correctCount = 0;

    // Timer State
    private Timer gameTimer;
    private int elapsedSeconds = 0;

    // UI Elements
    private JLabel questionProgressLabel;
    private JLabel difficultyBadge;
    private JLabel scoreBadge;
    private JLabel timerLabel;
    private JProgressBar progressBar;

    private JLabel questionDisplayLabel;
    private ModernTextField answerField;
    private RoundedButton actionButton; // Toggles between Submit & Next
    private JLabel feedbackBanner;

    private boolean isAwaitingNext = false;

    public GamePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.questionGenerator = new QuestionGenerator();
        this.sessionResults = new ArrayList<>();

        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(25, 40, 25, 40));

        initUI();
    }

    private void initUI() {
        JPanel centerContainer = new JPanel();
        centerContainer.setOpaque(false);
        centerContainer.setLayout(new BoxLayout(centerContainer, BoxLayout.Y_AXIS));

        // 1. Top HUD Bar (Score, Timer, Question #, Difficulty)
        RoundedPanel hudPanel = new RoundedPanel(16, UITheme.CARD_BG, UITheme.CARD_BORDER, 1);
        hudPanel.setLayout(new BorderLayout());
        hudPanel.setBorder(new EmptyBorder(14, 24, 14, 24));
        hudPanel.setMaximumSize(new Dimension(840, 68));

        JPanel hudLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        hudLeft.setOpaque(false);

        questionProgressLabel = new JLabel("Question 1 / 10");
        questionProgressLabel.setFont(UITheme.FONT_BOLD_16);
        questionProgressLabel.setForeground(UITheme.TEXT_PRIMARY);

        difficultyBadge = new JLabel("EASY");
        difficultyBadge.setFont(UITheme.FONT_BOLD_14);
        difficultyBadge.setForeground(UITheme.ACCENT_EMERALD);

        hudLeft.add(questionProgressLabel);
        hudLeft.add(difficultyBadge);

        JPanel hudRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        hudRight.setOpaque(false);

        timerLabel = new JLabel("⏱️ 00:00");
        timerLabel.setFont(UITheme.FONT_BOLD_16);
        timerLabel.setForeground(UITheme.ACCENT_CYAN);

        scoreBadge = new JLabel("🏆 Score: 0");
        scoreBadge.setFont(UITheme.FONT_BOLD_16);
        scoreBadge.setForeground(UITheme.WARNING);

        hudRight.add(timerLabel);
        hudRight.add(scoreBadge);

        hudPanel.add(hudLeft, BorderLayout.WEST);
        hudPanel.add(hudRight, BorderLayout.EAST);

        centerContainer.add(hudPanel);
        centerContainer.add(Box.createVerticalStrut(10));

        // Progress Bar
        progressBar = new JProgressBar(0, TOTAL_QUESTIONS);
        progressBar.setValue(0);
        progressBar.setPreferredSize(new Dimension(840, 6));
        progressBar.setMaximumSize(new Dimension(840, 6));
        progressBar.setForeground(UITheme.PRIMARY);
        progressBar.setBackground(UITheme.INPUT_BG);
        progressBar.setBorderPainted(false);
        centerContainer.add(progressBar);
        centerContainer.add(Box.createVerticalStrut(25));

        // 2. Main Question Card
        RoundedPanel questionCard = new RoundedPanel(22, UITheme.CARD_BG, UITheme.CARD_BORDER, 1);
        questionCard.setLayout(new BoxLayout(questionCard, BoxLayout.Y_AXIS));
        questionCard.setBorder(new EmptyBorder(35, 45, 35, 45));
        questionCard.setMaximumSize(new Dimension(740, 380));

        JLabel mathPrompt = new JLabel("Calculate the answer:");
        mathPrompt.setFont(UITheme.FONT_REGULAR_14);
        mathPrompt.setForeground(UITheme.TEXT_SECONDARY);
        mathPrompt.setAlignmentX(Component.CENTER_ALIGNMENT);

        questionDisplayLabel = new JLabel("12 + 15 = ?");
        questionDisplayLabel.setFont(UITheme.FONT_MATH_QUESTION);
        questionDisplayLabel.setForeground(UITheme.TEXT_PRIMARY);
        questionDisplayLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        answerField = new ModernTextField("Type your answer and press Enter", 15);
        answerField.setMaximumSize(new Dimension(320, 48));
        answerField.setFont(UITheme.FONT_TITLE);
        answerField.setHorizontalAlignment(ModernTextField.CENTER);
        answerField.setAlignmentX(Component.CENTER_ALIGNMENT);
        answerField.addActionListener(e -> handleActionButton());

        actionButton = new RoundedButton("Submit Answer", RoundedButton.Style.PRIMARY);
        actionButton.setFont(UITheme.FONT_BOLD_16);
        actionButton.setMaximumSize(new Dimension(320, 48));
        actionButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        actionButton.addActionListener(e -> handleActionButton());

        feedbackBanner = new JLabel(" ");
        feedbackBanner.setFont(UITheme.FONT_BOLD_16);
        feedbackBanner.setAlignmentX(Component.CENTER_ALIGNMENT);

        questionCard.add(mathPrompt);
        questionCard.add(Box.createVerticalStrut(15));
        questionCard.add(questionDisplayLabel);
        questionCard.add(Box.createVerticalStrut(25));
        questionCard.add(answerField);
        questionCard.add(Box.createVerticalStrut(18));
        questionCard.add(actionButton);
        questionCard.add(Box.createVerticalStrut(18));
        questionCard.add(feedbackBanner);

        centerContainer.add(questionCard);
        centerContainer.add(Box.createVerticalGlue());

        add(centerContainer, BorderLayout.CENTER);
    }

    /**
     * Prepares and starts a fresh 10-question game session.
     */
    public void startSession(QuestionGenerator.Difficulty difficulty) {
        this.currentDifficulty = difficulty;
        this.questionList = questionGenerator.generateQuestions(TOTAL_QUESTIONS, difficulty);
        this.sessionResults = new ArrayList<>();
        this.currentQuestionIndex = 0;
        this.runningScore = 0;
        this.correctCount = 0;
        this.elapsedSeconds = 0;
        this.isAwaitingNext = false;

        // Difficulty Badge Style
        difficultyBadge.setText(difficulty.name());
        switch (difficulty) {
            case HARD:
                difficultyBadge.setForeground(UITheme.DANGER);
                break;
            case MEDIUM:
                difficultyBadge.setForeground(UITheme.PRIMARY_HOVER);
                break;
            case EASY:
            default:
                difficultyBadge.setForeground(UITheme.ACCENT_EMERALD);
                break;
        }

        // Start Timer
        if (gameTimer != null && gameTimer.isRunning()) {
            gameTimer.stop();
        }
        updateTimerLabel();
        gameTimer = new Timer(1000, e -> {
            elapsedSeconds++;
            updateTimerLabel();
        });
        gameTimer.start();

        loadCurrentQuestion();
    }

    private void updateTimerLabel() {
        int mins = elapsedSeconds / 60;
        int secs = elapsedSeconds % 60;
        timerLabel.setText(String.format("⏱️ %02d:%02d", mins, secs));
    }

    private void loadCurrentQuestion() {
        isAwaitingNext = false;
        Question q = questionList.get(currentQuestionIndex);

        questionProgressLabel.setText(String.format("Question %d / %d", (currentQuestionIndex + 1), TOTAL_QUESTIONS));
        scoreBadge.setText("🏆 Score: " + runningScore);
        progressBar.setValue(currentQuestionIndex);

        questionDisplayLabel.setText(q.getQuestionText());
        answerField.setText("");
        answerField.setEnabled(true);
        answerField.requestFocusInWindow();

        feedbackBanner.setText(" ");
        actionButton.setText("Submit Answer");
        actionButton.setEnabled(true);
    }

    private void handleActionButton() {
        if (!isAwaitingNext) {
            submitCurrentAnswer();
        } else {
            advanceToNextQuestion();
        }
    }

    private void submitCurrentAnswer() {
        String input = answerField.getText().trim();
        if (input.isEmpty()) {
            feedbackBanner.setForeground(UITheme.WARNING);
            feedbackBanner.setText("⚠️ Please type your answer before submitting!");
            return;
        }

        int userAnswer;
        try {
            userAnswer = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            feedbackBanner.setForeground(UITheme.DANGER);
            feedbackBanner.setText("⚠️ Invalid number! Please enter an integer.");
            return;
        }

        Question currentQuestion = questionList.get(currentQuestionIndex);
        boolean isCorrect = currentQuestion.checkAnswer(userAnswer);

        sessionResults.add(new QuestionResult(currentQuestion, userAnswer, isCorrect));
        answerField.setEnabled(false);

        if (isCorrect) {
            correctCount++;
            int pointsEarned = getBasePointsPerQuestion(currentDifficulty);
            runningScore += pointsEarned;
            scoreBadge.setText("🏆 Score: " + runningScore);

            feedbackBanner.setForeground(UITheme.ACCENT_EMERALD);
            feedbackBanner.setText("✅ Correct! +" + pointsEarned + " points!");
        } else {
            feedbackBanner.setForeground(UITheme.DANGER);
            feedbackBanner.setText("❌ Incorrect! The correct answer was: " + currentQuestion.getCorrectAnswer());
        }

        isAwaitingNext = true;
        if (currentQuestionIndex + 1 < TOTAL_QUESTIONS) {
            actionButton.setText("Next Question  →");
        } else {
            actionButton.setText("View Final Results  🏆");
        }
    }

    private void advanceToNextQuestion() {
        currentQuestionIndex++;
        if (currentQuestionIndex < TOTAL_QUESTIONS) {
            loadCurrentQuestion();
        } else {
            finishGameSession();
        }
    }

    private void finishGameSession() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
        progressBar.setValue(TOTAL_QUESTIONS);

        // Final score calculation with speed bonus
        int finalScore = Game.calculateScore(correctCount, elapsedSeconds, currentDifficulty);
        Player player = mainFrame.getCurrentPlayer();

        Score scoreRecord = new Score(
                player != null ? player.getPlayerId() : 0,
                currentDifficulty.name(),
                TOTAL_QUESTIONS,
                correctCount,
                finalScore,
                elapsedSeconds
        );

        // Save score to database via SwingWorker
        if (player != null) {
            new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() {
                    return mainFrame.getScoreManager().saveScore(scoreRecord);
                }

                @Override
                protected void done() {
                    // Score saved
                }
            }.execute();
        }

        mainFrame.showResult(scoreRecord, sessionResults);
    }

    private int getBasePointsPerQuestion(QuestionGenerator.Difficulty diff) {
        switch (diff) {
            case HARD:
                return 30;
            case MEDIUM:
                return 20;
            case EASY:
            default:
                return 10;
        }
    }
}
