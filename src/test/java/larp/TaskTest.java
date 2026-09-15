package larp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests task state and display formatting for every supported task type.
 */
public class TaskTest {
    @Test
    public void todo_markAndUnmark_updatesStatusIcon() {
        Todo todo = new Todo("read book");

        assertFalse(todo.isDone());
        assertEquals("[T][ ] read book", todo.toString());

        todo.markAsDone();
        assertTrue(todo.isDone());
        assertEquals("[T][X] read book", todo.toString());

        todo.markAsNotDone();
        assertFalse(todo.isDone());
    }

    @Test
    public void deadline_toString_formatsDateForDisplay() {
        Deadline deadline = new Deadline(
                "submit report", LocalDate.of(2026, 9, 18), true);

        assertEquals("[D][X] submit report (by: Sep 18 2026)", deadline.toString());
    }

    @Test
    public void event_toString_includesStartAndEnd() {
        Event event = new Event("project meeting", "2pm", "3pm");

        assertEquals("[E][ ] project meeting (from: 2pm to: 3pm)", event.toString());
    }
}
