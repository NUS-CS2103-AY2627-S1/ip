package com.fireinyu.themyth.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link QuizTask}.
 */
public class QuizTaskTest {

    /**
     * Tests that a {@link QuizTask} is initialized with default description and type code.
     */
    @Test
    public void constructor_initializesCorrectly() {
        QuizTask task = new QuizTask();
        assertEquals("Revise software engineering with The Myth", task.getDescription());
        assertEquals("L", task.getTypeCode());
        assertFalse(task.isCompleted());
    }

    /**
     * Tests formatting of {@link QuizTask#toString()}.
     */
    @Test
    public void toString_formatsCorrectly() {
        QuizTask task = new QuizTask();
        assertEquals("[L][ ] Revise software engineering with The Myth", task.toString());

        task.mark();
        assertEquals("[L][X] Revise software engineering with The Myth", task.toString());
    }

    /**
     * Tests marking and unmarking a {@link QuizTask}.
     */
    @Test
    public void markAndUnmark_updatesStatus() {
        QuizTask task = new QuizTask();
        task.mark();
        assertTrue(task.isCompleted());
        task.unmark();
        assertFalse(task.isCompleted());
    }
}
