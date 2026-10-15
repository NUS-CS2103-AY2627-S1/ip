package sevensix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

/** Verifies the task models' public constructors, dates, completion state, and display contracts. */
class TaskModelTest {
    /** A plain task behaves as a to-do and retains its description verbatim. */
    @Test
    void taskPreservesDescriptionAndDefaultsToIncompleteTodo() {
        Task task = new Task("读书 | notes\\chapter");

        assertEquals("读书 | notes\\chapter", task.getDescription());
        assertEquals("T", task.getTaskTypeIcon());
        assertEquals(" ", task.getStatusIcon());
        assertFalse(task.isDone());
        assertEquals("[T][ ] 读书 | notes\\chapter", task.toString());
    }

    /** Repeated status updates are idempotent and do not modify the description. */
    @Test
    void markAndUnmarkUpdateOnlyCompletionState() {
        Todo todo = new Todo("read book");
        todo.markAsDone();
        todo.markAsDone();
        assertTrue(todo.isDone());
        assertEquals("X", todo.getStatusIcon());
        assertEquals("[T][X] read book", todo.toString());

        todo.markAsNotDone();
        todo.markAsNotDone();
        assertFalse(todo.isDone());
        assertEquals("[T][ ] read book", todo.toString());
    }

    /** The date-only deadline constructor must not invent a clock time. */
    @Test
    void deadlineDateConstructorPreservesAbsentTime() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2024, 2, 29));

        assertEquals(LocalDate.of(2024, 2, 29), deadline.getBy());
        assertNull(deadline.getByTime());
        assertEquals("D", deadline.getTaskTypeIcon());
        assertEquals("[D][ ] return book (by: Feb 29 2024)", deadline.toString());
    }

    /** The date-time constructor preserves a deadline's date and time separately. */
    @Test
    void deadlineDateTimeConstructorPreservesTime() {
        Deadline deadline = new Deadline("submit report", LocalDateTime.of(2024, 3, 1, 0, 0));
        deadline.markAsDone();

        assertEquals(LocalDate.of(2024, 3, 1), deadline.getBy());
        assertEquals(LocalTime.MIDNIGHT, deadline.getByTime());
        assertEquals("[D][X] submit report (by: Mar 01 2024 12:00 AM)", deadline.toString());
    }

    /** The date-only event constructor preserves both dates without adding times. */
    @Test
    void eventDateConstructorPreservesAbsentTimes() {
        Event event = new Event("trip", LocalDate.of(2024, 12, 31), LocalDate.of(2025, 1, 2));

        assertEquals(LocalDate.of(2024, 12, 31), event.getFrom());
        assertEquals(LocalDate.of(2025, 1, 2), event.getTo());
        assertNull(event.getFromTime());
        assertNull(event.getToTime());
        assertEquals("E", event.getTaskTypeIcon());
        assertEquals("[E][ ] trip (from: Dec 31 2024 to: Jan 02 2025)", event.toString());
    }

    /** The date-time event constructor must retain both endpoints across a day boundary. */
    @Test
    void eventDateTimeConstructorPreservesBothEndpoints() {
        Event event = new Event("shift", LocalDateTime.of(2024, 12, 31, 23, 0),
                LocalDateTime.of(2025, 1, 1, 1, 30));
        event.markAsDone();

        assertEquals(LocalDate.of(2024, 12, 31), event.getFrom());
        assertEquals(LocalDate.of(2025, 1, 1), event.getTo());
        assertEquals(LocalTime.of(23, 0), event.getFromTime());
        assertEquals(LocalTime.of(1, 30), event.getToTime());
        assertEquals("[E][X] shift (from: Dec 31 2024 11:00 PM to: Jan 01 2025 1:30 AM)", event.toString());
    }
}
