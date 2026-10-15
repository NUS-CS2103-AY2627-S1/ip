package min.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class TaskTest {
    @Test
    void todo_newTask_formatsForDisplayAndStorage() {
        Todo todo = new Todo("read book");

        assertEquals("read book", todo.getDescription());
        assertFalse(todo.isDone());
        assertEquals("[T][ ] read book", todo.toString());
        assertEquals("T | 0 | read book", todo.toFileString());
    }

    @Test
    void markAndUnmarkTask_updatesCompletionStatusAndFormatting() {
        Todo todo = new Todo("read book");

        todo.markAsDone();
        assertTrue(todo.isDone());
        assertEquals("[T][X] read book", todo.toString());
        assertEquals("T | 1 | read book", todo.toFileString());

        todo.markAsNotDone();
        assertFalse(todo.isDone());
        assertEquals("[T][ ] read book", todo.toString());
        assertEquals("T | 0 | read book", todo.toFileString());
    }

    @Test
    void deadline_newTask_formatsDateForDisplayAndStorage() {
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 8, 28));

        assertFalse(deadline.isDone());
        assertEquals("[D][ ] submit report (by: Aug 28 2026)", deadline.toString());
        assertEquals("D | 0 | submit report | 2026-08-28", deadline.toFileString());
    }

    @Test
    void deadline_markedTask_formatsCompletedStatus() {
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 8, 28));

        deadline.markAsDone();

        assertTrue(deadline.isDone());
        assertEquals("[D][X] submit report (by: Aug 28 2026)", deadline.toString());
        assertEquals("D | 1 | submit report | 2026-08-28", deadline.toFileString());
    }

    @Test
    void event_newTask_formatsTimesForDisplayAndStorage() {
        Event event = new Event("meeting", "2pm", "4pm");

        assertFalse(event.isDone());
        assertEquals("[E][ ] meeting (from: 2pm to: 4pm)", event.toString());
        assertEquals("E | 0 | meeting | 2pm | 4pm", event.toFileString());
    }

    @Test
    void event_markedTask_formatsCompletedStatus() {
        Event event = new Event("meeting", "2pm", "4pm");

        event.markAsDone();

        assertTrue(event.isDone());
        assertEquals("[E][X] meeting (from: 2pm to: 4pm)", event.toString());
        assertEquals("E | 1 | meeting | 2pm | 4pm", event.toFileString());
    }

    @Test
    void todo_invalidDescriptions_throwAssertionError() {
        assertThrows(AssertionError.class, () -> new Todo(null));
        assertThrows(AssertionError.class, () -> new Todo("   "));
        assertThrows(AssertionError.class, () -> new Todo("read | book"));
    }

    @Test
    void event_invalidTimes_throwAssertionError() {
        assertThrows(AssertionError.class, () -> new Event("meeting", null, "4pm"));
        assertThrows(AssertionError.class, () -> new Event("meeting", "   ", "4pm"));
        assertThrows(AssertionError.class, () -> new Event("meeting", "2 | 3pm", "4pm"));
        assertThrows(AssertionError.class, () -> new Event("meeting", "2pm", null));
        assertThrows(AssertionError.class, () -> new Event("meeting", "2pm", "   "));
        assertThrows(AssertionError.class, () -> new Event("meeting", "2pm", "4 | 5pm"));
    }
}
