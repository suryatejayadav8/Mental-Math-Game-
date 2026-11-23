package com.mentalmath;

import com.mentalmath.model.Score;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ScoreModelTest {

    @Test
    public void testAccuracyPercentage() {
        Score score1 = new Score(1, "EASY", 10, 8, 80, 45);
        assertEquals(80.0, score1.getAccuracyPercentage(), 0.001);

        Score score2 = new Score(1, "HARD", 10, 7, 210, 85);
        assertEquals(70.0, score2.getAccuracyPercentage(), 0.001);

        Score scoreEmpty = new Score(1, "MEDIUM", 0, 0, 0, 0);
        assertEquals(0.0, scoreEmpty.getAccuracyPercentage(), 0.001);
    }
}
