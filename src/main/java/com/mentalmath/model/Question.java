package com.mentalmath.model;

/**
 * Represents a single arithmetic question generated for a player.
 */
public class Question {
    private final int operand1;
    private final int operand2;
    private final char operator;
    private final int correctAnswer;

    public Question(int operand1, int operand2, char operator, int correctAnswer) {
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.operator = operator;
        this.correctAnswer = correctAnswer;
    }

    public int getOperand1() {
        return operand1;
    }

    public int getOperand2() {
        return operand2;
    }

    public char getOperator() {
        return operator;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    /**
     * Formats the question for display, e.g. "24 + 18 = ?"
     */
    public String getQuestionText() {
        return operand1 + " " + operator + " " + operand2 + " = ?";
    }

    /**
     * Checks if the user's submitted answer matches the correct answer.
     */
    public boolean checkAnswer(int userAnswer) {
        return userAnswer == this.correctAnswer;
    }

    @Override
    public String toString() {
        return getQuestionText() + " [Answer: " + correctAnswer + "]";
    }
}
