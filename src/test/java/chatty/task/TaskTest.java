package chatty.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests task state, accessors, type icons, and display output.
 */
public class TaskTest {
    /** Verifies base todo state changes and display output. */
    @Test
    public void todo_stateChanges_statusAndDisplayUpdated() {
        Todo todo = new Todo("read book");

        assertEquals("T", todo.getTypeIcon());
        assertEquals("read book", todo.getDescription());
        assertEquals(" ", todo.getStatusIcon());
        assertFalse(todo.isDone());
        assertEquals("[T][ ] read book", todo.toString());

        todo.markAsDone();
        assertTrue(todo.isDone());
        assertEquals("X", todo.getStatusIcon());
        assertEquals("[T][X] read book", todo.toString());

        todo.markAsNotDone();
        assertFalse(todo.isDone());
    }

    /** Verifies deadline accessors, type icon, and formatted date display. */
    @Test
    public void deadline_validDate_detailsReturnedAndDisplayed() {
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 10, 15));

        assertEquals("D", deadline.getTypeIcon());
        assertEquals(LocalDate.of(2026, 10, 15), deadline.getDueDate());
        assertEquals("[D][ ] submit report (by: Oct 15 2026)", deadline.toString());
    }

    /** Verifies event accessors, type icon, and range display. */
    @Test
    public void event_validRange_detailsReturnedAndDisplayed() {
        Event event = new Event("project meeting", "2pm", "4pm");

        assertEquals("E", event.getTypeIcon());
        assertEquals("2pm", event.getFrom());
        assertEquals("4pm", event.getTo());
        assertEquals("[E][ ] project meeting (from: 2pm to: 4pm)", event.toString());
    }
}
