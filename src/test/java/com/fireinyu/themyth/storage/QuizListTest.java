package com.fireinyu.themyth.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.fireinyu.themyth.Quiz;

/**
 * Unit tests for {@link QuizList}.
 */
public class QuizListTest {

    /**
     * Tests parsing of CSV tokens into a {@link Quiz} object.
     */
    @Test
    public void parse_validCsvRow_createsQuiz() {
        QuizList list = new QuizList();
        Quiz quiz = list.parse("What is 2+2?", "1", "3", "4", "5");

        assertEquals("What is 2+2?", quiz.getQuestion());
        assertEquals(1, quiz.getAnswerIndex());
        assertEquals(List.of("3", "4", "5"), quiz.getChoices());
    }

    /**
     * Tests {@link QuizList#getRandom()} returns an element from the list.
     */
    @Test
    public void getRandom_returnsItemFromList() {
        QuizList list = new QuizList();
        Quiz q1 = new Quiz("Q1", List.of("A", "B"), 0);
        Quiz q2 = new Quiz("Q2", List.of("C", "D"), 1);
        list.add(q1);
        list.add(q2);

        Quiz randomQuiz = list.getRandom();
        assertNotNull(randomQuiz);
    }
}
