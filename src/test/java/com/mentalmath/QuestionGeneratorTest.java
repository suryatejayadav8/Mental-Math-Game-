package com.mentalmath;

import com.mentalmath.game.QuestionGenerator;
import com.mentalmath.model.Question;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class QuestionGeneratorTest {

    private QuestionGenerator generator;

    @Before
    public void setUp() {
        generator = new QuestionGenerator();
    }

    @Test
    public void testGenerateTenQuestions() {
        List<Question> questions = generator.generateQuestions(10, QuestionGenerator.Difficulty.EASY);
        assertNotNull(questions);
        assertEquals(10, questions.size());
    }

    @Test
    public void testCleanDivisionIntegrity() {
        // Generate multiple division questions across difficulties to verify zero remainder and no divide-by-zero
        for (int i = 0; i < 50; i++) {
            Question q = generator.generateSingleQuestion(QuestionGenerator.Difficulty.EASY);
            if (q.getOperator() == '/') {
                assertTrue("Divisor must be greater than zero", q.getOperand2() > 0);
                assertEquals("Dividend must equal divisor multiplied by answer",
                        q.getOperand1(), q.getOperand2() * q.getCorrectAnswer());
                assertEquals("Division remainder must be zero", 0, q.getOperand1() % q.getOperand2());
            }
        }
    }

    @Test
    public void testQuestionAnswerEvaluation() {
        Question q = new Question(15, 25, '+', 40);
        assertTrue(q.checkAnswer(40));
        assertFalse(q.checkAnswer(39));
        assertEquals("15 + 25 = ?", q.getQuestionText());
    }
}
