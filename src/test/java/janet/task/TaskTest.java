package janet.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

class TaskTest {
    @Test
    void todo_statusTransitions_preservesDescriptionAndType() {
        Todo todo = new Todo("read book");

        assertEquals("[T]", todo.getTypeIcon());
        assertEquals("read book", todo.getDescription());
        assertEquals("read book", todo.getRawDescription());
        assertEquals(" ", todo.getStatusIcon());
        assertFalse(todo.isDone());

        todo.markAsDone();
        assertEquals("X", todo.getStatusIcon());
        assertTrue(todo.isDone());

        todo.markAsUndone();
        assertEquals(" ", todo.getStatusIcon());
        assertFalse(todo.isDone());
    }

    @Test
    void deadline_getters_returnFormattedAndRawDetails() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 12, 2));

        assertEquals("[D]", deadline.getTypeIcon());
        assertEquals(LocalDate.of(2019, 12, 2), deadline.getDeadline());
        assertEquals("return book", deadline.getRawDescription());
        assertEquals("return book (by: Dec 02 2019)", deadline.getDescription());
    }

    @Test
    void event_getters_returnFormattedAndRawDetails() {
        Event event = new Event("project meeting", "14:00", "16:00");

        assertEquals("[E]", event.getTypeIcon());
        assertEquals("14:00", event.getStart());
        assertEquals("16:00", event.getEnd());
        assertEquals("project meeting", event.getRawDescription());
        assertEquals("project meeting (from: 14:00 to: 16:00)", event.getDescription());
    }

    @Test
    void event_invalidOrReversedDateTime_throwsValidationException() {
        assertThrows(DateTimeParseException.class, () -> new Event("invalid", "10:99", "11:00"));
        assertThrows(IllegalArgumentException.class, () -> new Event("mixed", "2099-01-01", "11:00"));
        assertThrows(IllegalArgumentException.class, () -> new Event("reversed", "12:00", "11:00"));
    }
}
