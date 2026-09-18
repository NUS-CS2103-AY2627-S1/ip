package student.project.bob.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests the one-level undo history used by Bob.
 */
public class UndoHistoryTest {
    /**
     * Verifies that undo applies and consumes the most recently recorded action.
     */
    @Test
    public void undo_recordedAction_appliesOnce() {
        UndoHistory history = new UndoHistory();
        TaskList taskList = new TaskList();
        Task task = new Todo("read book");
        taskList.add(task);
        history.record(() -> taskList.remove(0), "Removed read book");

        assertEquals("Removed read book", history.undo().orElseThrow());
        assertTrue(taskList.asList().isEmpty());
        assertTrue(history.undo().isEmpty());
    }

    /**
     * Verifies that recording a new action replaces the previous action.
     */
    @Test
    public void record_newAction_replacesPreviousAction() {
        UndoHistory history = new UndoHistory();
        StringBuilder result = new StringBuilder();
        history.record(() -> result.append("old"), "old action");
        history.record(() -> result.append("new"), "new action");

        assertEquals("new action", history.undo().orElseThrow());
        assertEquals("new", result.toString());
    }
}
