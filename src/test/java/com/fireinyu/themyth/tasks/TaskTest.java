package com.fireinyu.themyth.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.fireinyu.themyth.util.MythDateTime;

/**
 * Unit tests for base {@link Task} functionality.
 */
public class TaskTest {

    private static class DummyTask extends Task {
        DummyTask(String description) {
            super(description, "DUMMY");
        }
    }

    /**
     * Tests basic properties and initial state of a Task.
     */
    @Test
    public void constructor_initialState() {
        DummyTask task = new DummyTask("test task");
        assertEquals("test task", task.getDescription());
        assertFalse(task.isCompleted());
        assertEquals("DUMMY", task.getTypeCode());
        assertEquals("[DUMMY][ ] test task", task.toString());
    }

    /**
     * Tests marking and unmarking tasks.
     */
    @Test
    public void markAndUnmark_updatesCompletionAndTimestamp() {
        DummyTask task = new DummyTask("test task");
        MythDateTime initialMod = task.getLastModified();

        task.mark();
        assertTrue(task.isCompleted());
        assertEquals("[DUMMY][X] test task", task.toString());

        task.unmark();
        assertFalse(task.isCompleted());
        assertEquals("[DUMMY][ ] test task", task.toString());
    }

    /**
     * Tests overriding access timestamps via setAccessTimes.
     */
    @Test
    public void setAccessTimes_overridesCreatedAndModified() {
        DummyTask task = new DummyTask("test task");
        MythDateTime created = MythDateTime.parse("2025-01-01-00-00-00");
        MythDateTime modified = MythDateTime.parse("2025-02-02-12-00-00");

        task.setAccessTimes(created, modified);
        assertEquals(created, task.getCreated());
        assertEquals(modified, task.getLastModified());
    }

    /**
     * Tests serialization extract format.
     */
    @Test
    public void extract_containsStandardCsvColumns() {
        DummyTask task = new DummyTask("test task");
        MythDateTime fixedTime = MythDateTime.parse("2025-01-01-00-00-00");
        task.setAccessTimes(fixedTime, fixedTime);

        List<String> columns = task.extract();
        assertEquals(5, columns.size());
        assertEquals("DUMMY", columns.get(0));
        assertEquals("false", columns.get(1));
        assertEquals(fixedTime.dump(), columns.get(2));
        assertEquals(fixedTime.dump(), columns.get(3));
        assertEquals("test task", columns.get(4));
    }
}
