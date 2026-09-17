package sace;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests the state and display formats shared by the task model classes.
 */
class TaskTest {
    @Test
    void task_markAndUnmark_updatesCompletionStateAndDisplay() {
        Task task = new Task("read notes");

        assertFalse(task.isDone());
        assertEquals("[ ] read notes", task.toString());

        task.markAsDone();
        assertTrue(task.isDone());
        assertEquals("[X] read notes", task.toString());

        task.markAsNotDone();
        assertFalse(task.isDone());
        assertEquals("[ ] read notes", task.toString());
    }

    @Test
    void todo_toString_includesTypeStatusAndDescription() {
        Todo todo = new Todo("read notes");

        todo.markAsDone();

        assertEquals("read notes", todo.getDescription());
        assertEquals("[T][X] read notes", todo.toString());
    }

    @Test
    void deadline_getByAndToString_returnFormattedDate() {
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 9, 7));

        assertEquals(LocalDate.of(2026, 9, 7), deadline.getBy());
        assertEquals("[D][ ] submit report (by: Sep 7 2026)", deadline.toString());
    }

    @Test
    void event_accessorsAndToString_returnTimeRange() {
        Event event = new Event("tutorial", "Monday 2pm", "Monday 4pm");

        assertEquals("Monday 2pm", event.getFrom());
        assertEquals("Monday 4pm", event.getTo());
        assertEquals(
                "[E][ ] tutorial (from: Monday 2pm to: Monday 4pm)",
                event.toString());
    }
}
