package stephen.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/** Tests task status, display, persistence, and constructor boundaries. */
class TaskTest {
    /** Verifies a Todo's status transitions and stable text representations. */
    @Test
    void todoStatusTransitionsUpdatesDisplayAndStorageText() {
        Todo todo = new Todo("line one\nline two | path\\notes");

        assertEquals("[T][ ] line one\nline two | path\\notes", todo.toString());
        assertEquals("T | 0 | line one\\nline two \\| path\\\\notes", todo.toDataString());
        assertFalse(todo.occursOn(LocalDate.of(2024, 1, 1)));

        todo.markAsDone();
        assertEquals("X", todo.getStatusIcon());
        assertEquals("T | 1 | line one\\nline two \\| path\\\\notes", todo.toDataString());

        todo.markAsNotDone();
        assertEquals(" ", todo.getStatusIcon());
    }

    /** Verifies dated task display and save-file formats. */
    @Test
    void datedTasksToStringAndDataStringUsesExpectedDates() {
        Deadline deadline = new Deadline("submit", LocalDate.of(2024, 2, 29));
        Event event = new Event("conference",
                LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 3));

        assertEquals("[D][ ] submit (by: Feb 29 2024)", deadline.toString());
        assertEquals("D | 0 | submit | 2024-02-29", deadline.toDataString());
        assertEquals("[E][ ] conference (from: Mar 1 2024 to: Mar 3 2024)",
                event.toString());
        assertEquals("E | 0 | conference | 2024-03-01 | 2024-03-03",
                event.toDataString());
    }

    /** Verifies that invalid event ranges cannot enter the domain model. */
    @Test
    void eventInvalidDatesThrowsIllegalArgumentException() {
        LocalDate date = LocalDate.of(2024, 3, 1);

        assertThrows(IllegalArgumentException.class, () -> new Event("missing start", null, date));
        assertThrows(IllegalArgumentException.class, () -> new Event("missing end", date, null));
        assertThrows(IllegalArgumentException.class, () -> new Event("reversed", date, date.minusDays(1)));
    }
}
