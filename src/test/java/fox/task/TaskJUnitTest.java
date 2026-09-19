package fox.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import fox.exception.FoxException;

/** JUnit tests for common Fox task state and display behavior. */
class TaskJUnitTest {
    @Test
    void newTaskIsIncompleteAndDisplaysDescription() {
        Task task = new Task("read book");

        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
        assertEquals("[ ] read book", task.toString());
    }

    @Test
    void taskCanBeMarkedDoneAndNotDoneAgain() {
        Task task = new Todo("read book");

        task.markAsDone();
        assertTrue(task.isDone());
        assertEquals("[T][X] read book", task.toString());

        task.markAsNotDone();
        assertFalse(task.isDone());
        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    void deadlineAndEventRetainTheirDetails() throws FoxException {
        Deadline deadline = new Deadline("submit", FoxDate.parse("2026-12-02"));
        Event event = new Event("meeting", "10am", "11am");

        assertEquals("2026-12-02", deadline.getBy());
        assertEquals("[D][ ] submit (by: Dec 02 2026)", deadline.toString());
        assertEquals("[E][ ] meeting (from: 10am to: 11am)", event.toString());
    }
}
