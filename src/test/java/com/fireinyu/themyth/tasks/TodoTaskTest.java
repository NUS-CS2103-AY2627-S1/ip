package com.fireinyu.themyth.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link TodoTask}.
 */
public class TodoTaskTest {

    /**
     * Tests that a {@link TodoTask} is correctly initialized with description and type code.
     */
    @Test
    public void constructor_initializesCorrectly() {
        TodoTask task = new TodoTask("Read chapter 1");
        assertEquals("Read chapter 1", task.getDescription());
        assertEquals("T", task.getTypeCode());
        assertFalse(task.isCompleted());
    }

    /**
     * Tests serialization of a {@link TodoTask} via {@link TodoTask#extract()}.
     */
    @Test
    public void extract_serializesAttributes() {
        TodoTask task = new TodoTask("Read chapter 1");
        List<String> extracted = task.extract();

        assertEquals("T", extracted.get(0));
        assertEquals("false", extracted.get(1));
        assertEquals("Read chapter 1", extracted.get(4));
    }

    /**
     * Tests formatting of {@link TodoTask#toString()}.
     */
    @Test
    public void toString_formatsCorrectly() {
        TodoTask task = new TodoTask("Read chapter 1");
        assertEquals("[T][ ] Read chapter 1", task.toString());

        task.mark();
        assertEquals("[T][X] Read chapter 1", task.toString());

        task.unmark();
        assertEquals("[T][ ] Read chapter 1", task.toString());
    }

    /**
     * Tests completion marking and unmarking.
     */
    @Test
    public void markAndUnmark_updatesCompletionStatus() {
        TodoTask task = new TodoTask("Read chapter 1");
        assertFalse(task.isCompleted());

        task.mark();
        assertTrue(task.isCompleted());

        task.unmark();
        assertFalse(task.isCompleted());
    }
}
