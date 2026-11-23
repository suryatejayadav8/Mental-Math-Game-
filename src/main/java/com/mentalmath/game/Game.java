package com.mentalmath.game;

import com.mentalmath.manager.ScoreManager;
import com.mentalmath.model.Player;
import com.mentalmath.model.Question;
import com.mentalmath.model.Score;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Manages an active 10-question Mental Math Game session for a player.
 * Handles timing, input validation, scoring, feedback, and database persistence.
 */
public class Game {

    private static final int TOTAL_QUESTIONS = 10;

    private final Player player;
    private final QuestionGenerator.Difficulty difficulty;
    private final QuestionGenerator questionGenerator;
    private final ScoreManager scoreManager;
    private final Scanner scanner;

    // Session log helper to review answers at the end
    private static class QuestionResult {
        Question question;
        int userAnswer;
        boolean isCorrect;

        QuestionResult(Question question, int userAnswer, boolean isCorrect) {
            this.question = question;
            this.userAnswer = userAnswer;
            this.isCorrect = isCorrect;
        }
    }

    public Game(Player player, QuestionGenerator.Difficulty difficulty, ScoreManager scoreManager, Scanner scanner) {
        this.player = player;
        this.difficulty = difficulty;
        this.questionGenerator = new QuestionGenerator();
        this.scoreManager = scoreManager;
        this.scanner = scanner;
    }

    /**
     * Executes the 10-question game session.
     */
    public void start() {
        System.out.println("\n=======================================================");
        System.out.println("  STARTING MENTAL MATH CHALLENGE: " + difficulty + " MODE");
        System.out.println("  Player: " + player.getPlayerName());
        System.out.println("  Total Questions: " + TOTAL_QUESTIONS);
        System.out.println("  Timer starts NOW! Do your best!");
        System.out.println("=======================================================");

        List<Question> questions = questionGenerator.generateQuestions(TOTAL_QUESTIONS, difficulty);
        List<QuestionResult> results = new ArrayList<>();
        int correctCount = 0;

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            System.out.printf("%n[Question %d/%d]  %s%nYour Answer: ", (i + 1), TOTAL_QUESTIONS, q.getQuestionText());

            int userAnswer = readIntegerInput();
            boolean isCorrect = q.checkAnswer(userAnswer);

            if (isCorrect) {
                System.out.println("  ✅ Correct!");
                correctCount++;
            } else {
                System.out.println("  ❌ Incorrect! Correct answer was: " + q.getCorrectAnswer());
            }

            results.add(new QuestionResult(q, userAnswer, isCorrect));
        }

        long endTime = System.currentTimeMillis();
        int timeTakenSeconds = (int) Math.max(1, (endTime - startTime) / 1000);

        // Calculate Score & Accuracy
        int finalScore = calculateScore(correctCount, timeTakenSeconds, difficulty);
        double accuracy = ((double) correctCount / TOTAL_QUESTIONS) * 100.0;

        // Display summary and detailed review
        displaySessionSummary(correctCount, timeTakenSeconds, finalScore, accuracy, results);

        // Save result to MySQL database
        Score scoreRecord = new Score(
                player.getPlayerId(),
                difficulty.name(),
                TOTAL_QUESTIONS,
                correctCount,
                finalScore,
                timeTakenSeconds
        );

        scoreManager.saveScore(scoreRecord);
    }

    /**
     * Calculates score based on correct answers, difficulty multiplier, and speed bonus.
     * Made public static so both console and Swing GUI share the exact same scoring logic.
     */
    public static int calculateScore(int correctAnswers, int timeTakenSeconds, QuestionGenerator.Difficulty diff) {
        int basePerQuestion;
        int targetSeconds;

        switch (diff) {
            case HARD:
                basePerQuestion = 30;
                targetSeconds = 120;
                break;
            case MEDIUM:
                basePerQuestion = 20;
                targetSeconds = 90;
                break;
            case EASY:
            default:
                basePerQuestion = 10;
                targetSeconds = 60;
                break;
        }

        int score = correctAnswers * basePerQuestion;

        // Speed bonus: If player answers correctly and finishes faster than target time
        if (correctAnswers >= 5 && timeTakenSeconds < targetSeconds) {
            int bonus = (targetSeconds - timeTakenSeconds) * (diff.ordinal() + 1);
            score += bonus;
        }

        return Math.max(0, score);
    }

    /**
     * Displays session performance review table and final metrics.
     */
    private void displaySessionSummary(int correct, int timeTaken, int score, double accuracy, List<QuestionResult> results) {
        System.out.println("\n=======================================================");
        System.out.println("                 SESSION COMPLETED!                    ");
        System.out.println("=======================================================");
        System.out.printf("  Correct Answers : %d / %d%n", correct, TOTAL_QUESTIONS);
        System.out.printf("  Accuracy        : %.1f%%%n", accuracy);
        System.out.printf("  Time Taken      : %d seconds%n", timeTaken);
        System.out.printf("  Final Score     : %d points%n", score);
        System.out.println("-------------------------------------------------------");
        System.out.println("  DETAILED QUESTION BREAKDOWN:");
        System.out.println("-------------------------------------------------------");
        System.out.printf("  %-4s | %-15s | %-10s | %-10s | %-8s%n",
                "#", "Question", "Your Ans", "Correct Ans", "Result");
        System.out.println("  -----------------------------------------------------");

        int qNum = 1;
        for (QuestionResult res : results) {
            System.out.printf("  %-4d | %-15s | %-10d | %-10d | %-8s%n",
                    qNum++,
                    res.question.getQuestionText().replace(" = ?", ""),
                    res.userAnswer,
                    res.question.getCorrectAnswer(),
                    res.isCorrect ? "✓ Correct" : "✗ Wrong");
        }
        System.out.println("=======================================================");
    }

    /**
     * Safely reads an integer from console input, preventing crashes on invalid input.
     */
    private int readIntegerInput() {
        while (true) {
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("  ⚠️ Please enter a valid integer: ");
            }
        }
    }
}
