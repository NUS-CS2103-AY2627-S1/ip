package bro.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Verifies task descriptions, completion state, and display formatting. */
class TaskTest {
    /** A new task starts incomplete and uses the todo display marker. */
    @Test
    void constructor_newTask_hasTodoFormatAndIncompleteState() {
        Task task = new Task("read book");

        assertEquals("read book", task.getDescription());
        assertFalse(task.isDone());
        assertEquals("[T] [ ] read book", task.toString());
    }

    /** Changing completion state updates both the predicate and display marker. */
    @Test
    void setDone_completedTask_updatesStateAndDisplay() {
        Task task = new Task("read book");

        task.setDone(true);
        assertTrue(task.isDone());
        assertEquals("X", task.showDone());
        assertEquals("[T] [X] read book", task.toString());

        task.setDone(false);
        assertFalse(task.isDone());
        assertEquals(" ", task.showDone());
    }
}
