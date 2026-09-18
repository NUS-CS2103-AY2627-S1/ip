package friday.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests serialization, display, and equality behavior for concrete task types.
 */
class TaskSubtypeTest {
    /**
     * Verifies the todo display and storage representations.
     */
    @Test
    void todo_formatsDisplayAndStorageRepresentations() {
        ToDo todo = new ToDo("read book");
        todo.markAsDone();

        assertEquals("[T][X] read book", todo.toString());
        assertEquals("T | 1 | read book", todo.toFileString());
    }

    /**
     * Verifies deadline formatting and equality include the due date.
     */
    @Test
    void deadline_formatsDateAndComparesDateDetails() {
        LocalDate dueDate = LocalDate.of(2026, 9, 30);
        Deadline deadline = new Deadline("submit report", dueDate);

        assertEquals("[D][ ] submit report (by: Sep 30 2026)", deadline.toString());
        assertEquals("D | 0 | submit report | 2026-09-30", deadline.toFileString());
        assertTrue(deadline.hasSameDetails(new Deadline("submit report", dueDate)));
        assertFalse(deadline.hasSameDetails(new Deadline("submit report", dueDate.plusDays(1))));
    }

    /**
     * Verifies event formatting and equality include both date-times.
     */
    @Test
    void event_formatsDateTimesAndComparesTimeDetails() {
        LocalDateTime from = LocalDateTime.of(2026, 10, 1, 14, 0);
        LocalDateTime to = LocalDateTime.of(2026, 10, 1, 15, 30);
        Event event = new Event("design review", from, to);

        assertEquals("[E][ ] design review (from: Oct 01 2026 14:00 to: Oct 01 2026 15:30)",
                event.toString());
        assertEquals("E | 0 | design review | 2026-10-01T14:00 | 2026-10-01T15:30",
                event.toFileString());
        assertTrue(event.hasSameDetails(new Event("design review", from, to)));
        assertFalse(event.hasSameDetails(new Event("design review", from, to.plusMinutes(30))));
    }
}
