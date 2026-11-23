package com.mentalmath.game;

import com.mentalmath.model.Question;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Generates random mathematical questions based on difficulty level.
 * Covers Addition, Subtraction, Multiplication, and Division.
 * Guarantees clean integer division without decimals or division by zero.
 */
public class QuestionGenerator {

    private final Random random = new Random();
    private static final char[] OPERATORS = {'+', '-', '*', '/'};

    public enum Difficulty {
        EASY,
        MEDIUM,
        HARD
    }

    /**
     * Generates a batch of mathematical questions for a game round.
     *
     * @param count      Number of questions to generate (typically 10)
     * @param difficulty EASY, MEDIUM, or HARD
     * @return List of generated Question instances
     */
    public List<Question> generateQuestions(int count, Difficulty difficulty) {
        List<Question> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            list.add(generateSingleQuestion(difficulty));
        }
        return list;
    }

    /**
     * Generates a single question tailored to the chosen difficulty level.
     */
    public Question generateSingleQuestion(Difficulty difficulty) {
        char op = OPERATORS[random.nextInt(OPERATORS.length)];
        int op1 = 0;
        int op2 = 0;
        int answer = 0;

        switch (difficulty) {
            case EASY:
                switch (op) {
                    case '+':
                        op1 = random.nextInt(20) + 1; // 1 to 20
                        op2 = random.nextInt(20) + 1;
                        answer = op1 + op2;
                        break;
                    case '-':
                        op1 = random.nextInt(20) + 1;
                        op2 = random.nextInt(op1) + 1; // Guarantees op1 >= op2 for non-negative result
                        answer = op1 - op2;
                        break;
                    case '*':
                        op1 = random.nextInt(10) + 1; // 1 to 10
                        op2 = random.nextInt(10) + 1;
                        answer = op1 * op2;
                        break;
                    case '/':
                        // To ensure clean division: pick divisor and quotient first
                        op2 = random.nextInt(9) + 2;   // Divisor: 2 to 10
                        answer = random.nextInt(10) + 1; // Quotient: 1 to 10
                        op1 = op2 * answer;            // Dividend
                        break;
                }
                break;

            case MEDIUM:
                switch (op) {
                    case '+':
                        op1 = random.nextInt(90) + 10; // 10 to 99
                        op2 = random.nextInt(90) + 10;
                        answer = op1 + op2;
                        break;
                    case '-':
                        op1 = random.nextInt(90) + 10;
                        op2 = random.nextInt(90) + 10;
                        if (op1 < op2) {
                            int temp = op1;
                            op1 = op2;
                            op2 = temp;
                        }
                        answer = op1 - op2;
                        break;
                    case '*':
                        op1 = random.nextInt(15) + 5;  // 5 to 19
                        op2 = random.nextInt(12) + 2;  // 2 to 13
                        answer = op1 * op2;
                        break;
                    case '/':
                        op2 = random.nextInt(11) + 2;   // Divisor: 2 to 12
                        answer = random.nextInt(20) + 2; // Quotient: 2 to 21
                        op1 = op2 * answer;
                        break;
                }
                break;

            case HARD:
                switch (op) {
                    case '+':
                        op1 = random.nextInt(400) + 100; // 100 to 499
                        op2 = random.nextInt(400) + 100;
                        answer = op1 + op2;
                        break;
                    case '-':
                        op1 = random.nextInt(400) + 100;
                        op2 = random.nextInt(400) + 100;
                        if (op1 < op2) {
                            int temp = op1;
                            op1 = op2;
                            op2 = temp;
                        }
                        answer = op1 - op2;
                        break;
                    case '*':
                        op1 = random.nextInt(35) + 15; // 15 to 49
                        op2 = random.nextInt(20) + 5;  // 5 to 24
                        answer = op1 * op2;
                        break;
                    case '/':
                        op2 = random.nextInt(20) + 5;   // Divisor: 5 to 24
                        answer = random.nextInt(30) + 10; // Quotient: 10 to 39
                        op1 = op2 * answer;
                        break;
                }
                break;
        }

        return new Question(op1, op2, op, answer);
    }
}
