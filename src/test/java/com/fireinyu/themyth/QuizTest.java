package com.fireinyu.themyth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Quiz}.
 */
public class QuizTest {

    /**
     * Tests initialization and getters of {@link Quiz}.
     */
    @Test
    public void constructor_initializesFieldsCorrectly() {
        Quiz quiz = new Quiz("What is 2+2?", List.of("3", "4", "5"), 1);
        assertEquals("What is 2+2?", quiz.getQuestion());
        assertEquals(List.of("3", "4", "5"), quiz.getChoices());
        assertEquals(1, quiz.getAnswerIndex());
    }

    /**
     * Tests serialization of {@link Quiz#extract()}.
     */
    @Test
    public void extract_serializesCorrectly() {
        Quiz quiz = new Quiz("What is 2+2?", List.of("3", "4", "5"), 1);
        List<String> extracted = quiz.extract();

        assertEquals(5, extracted.size());
        assertEquals("What is 2+2?", extracted.get(0));
        assertEquals("1", extracted.get(1));
        assertEquals("3", extracted.get(2));
        assertEquals("4", extracted.get(3));
        assertEquals("5", extracted.get(4));
    }

    /**
     * Tests string representation of {@link Quiz#toString()}.
     */
    @Test
    public void toString_formatsOptions() {
        Quiz quiz = new Quiz("Capital of France?", List.of("London", "Paris", "Berlin"), 1);
        String text = quiz.toString();

        assertTrue(text.contains("Capital of France?"));
        assertTrue(text.contains("0: London"));
        assertTrue(text.contains("1: Paris"));
        assertTrue(text.contains("2: Berlin"));
    }
}
