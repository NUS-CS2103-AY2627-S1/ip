package com.fireinyu.themyth.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.fireinyu.themyth.exceptions.CorruptedTaskFileException;
import com.fireinyu.themyth.tasks.DeadlineTask;
import com.fireinyu.themyth.tasks.EventTask;
import com.fireinyu.themyth.tasks.Task;
import com.fireinyu.themyth.tasks.TodoTask;

/**
 * Unit tests for {@link TaskList}.
 */
public class TaskListTest {

    /**
     * Tests parsing a serialized {@link TodoTask} row.
     */
    @Test
    public void parse_todoRow_returnsTodoTask() {
        TaskList list = new TaskList();
        Task task = list.parse("T", "true", "2025-01-01-10-00-00", "2025-01-01-11-00-00", "Buy milk");

        assertInstanceOf(TodoTask.class, task);
        assertEquals("Buy milk", task.getDescription());
        assertTrue(task.isCompleted());
    }

    /**
     * Tests parsing a serialized {@link DeadlineTask} row.
     */
    @Test
    public void parse_deadlineRow_returnsDeadlineTask() {
        TaskList list = new TaskList();
        Task task = list.parse("D", "false", "2025-01-01-10-00-00", "2025-01-01-11-00-00",
                "Submit report", "2025-10-15-18-00-00");

        assertInstanceOf(DeadlineTask.class, task);
        assertEquals("Submit report", task.getDescription());
        assertFalse(task.isCompleted());
    }

    /**
     * Tests parsing a serialized {@link EventTask} row.
     */
    @Test
    public void parse_eventRow_returnsEventTask() {
        TaskList list = new TaskList();
        Task task = list.parse("E", "false", "2025-01-01-10-00-00", "2025-01-01-11-00-00",
                "Orientation Camp", "2025-06-01-09-00-00", "2025-06-01-17-00-00");

        assertInstanceOf(EventTask.class, task);
        assertEquals("Orientation Camp", task.getDescription());
        assertFalse(task.isCompleted());
    }

    /**
     * Tests that parsing invalid or unknown task rows throws {@link CorruptedTaskFileException}.
     */
    @Test
    public void parse_corruptedRow_throwsCorruptedTaskFileException() {
        TaskList list = new TaskList();

        assertThrows(CorruptedTaskFileException.class, () -> list.parse("UNKNOWN", "false",
                "2025-01-01-10-00-00", "2025-01-01-11-00-00", "Description"));
        assertThrows(CorruptedTaskFileException.class, () -> list.parse("T"));
    }
}
