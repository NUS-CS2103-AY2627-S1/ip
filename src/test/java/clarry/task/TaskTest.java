package clarry.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;

import clarry.ClarryException;

/** Tests task status, calendar boundaries, serialization and identity. */
class TaskTest {
    @Test
    void todo_completionTransitions_updatesDisplayAndStorage() {
        Todo task = new Todo("read book");
        assertFalse(task.isDone());
        assertEquals("[T][ ] read book", task.toString());
        assertEquals("T | 0 | read book", task.toFileFormat());
        task.markAsDone();
        task.markAsDone();
        assertTrue(task.isDone());
        assertEquals("[T][X] read book", task.toString());
        assertEquals("T | 1 | read book", task.toFileFormat());
        task.markAsNotDone();
        task.markAsNotDone();
        assertEquals(" ", task.getStatusIcon());
        assertFalse(task.occursOn(LocalDate.of(2026, 9, 15)));
    }

    @Test
    @ResourceLock("defaultLocale")
    void datedTasks_englishLocale_formatsDisplayAndStorage() throws ClarryException {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.ENGLISH);
            Deadline deadline = new Deadline("book", "2024-02-29");
            deadline.markAsDone();
            assertEquals("[D][X] book (by: Feb 29 2024)", deadline.toString());
            assertEquals("D | 1 | book | 2024-02-29", deadline.toFileFormat());
            Event event = new Event("swim", "2024-02-29 23:00", "2024-03-01 01:00");
            assertEquals("[E][ ] swim (from: Feb 29 2024 23:00 to: Mar 01 2024 01:00)", event.toString());
            event.markAsDone();
            assertEquals("E | 1 | swim | 2024-02-29 23:00 | 2024-03-01 01:00", event.toFileFormat());
        } finally {
            Locale.setDefault(original);
        }
    }

    @Test
    void occursOn_calendarBoundaries_includesOnlyScheduledDates() throws ClarryException {
        LocalDate start = LocalDate.of(2024, 2, 28);
        Event event = new Event("trip", "2024-02-28 23:00", "2024-03-01 01:00");
        assertFalse(event.occursOn(start.minusDays(1)));
        assertTrue(event.occursOn(start));
        assertTrue(event.occursOn(start.plusDays(1)));
        assertTrue(event.occursOn(start.plusDays(2)));
        assertFalse(event.occursOn(start.plusDays(3)));
        Deadline deadline = new Deadline("book", "2024-02-29");
        assertTrue(deadline.occursOn(start.plusDays(1)));
        assertFalse(deadline.occursOn(start));
        assertFalse(deadline.occursOn(start.plusDays(2)));
    }

    @Test
    void hasSameDetails_typeCaseScheduleAndStatus_comparesTaskIdentity() throws ClarryException {
        Todo todo = new Todo("book");
        Todo completed = new Todo("book");
        completed.markAsDone();
        assertTrue(todo.hasSameDetails(completed));
        assertFalse(todo.hasSameDetails(new Todo("Book")));
        Deadline deadline = new Deadline("book", "2024-02-29");
        assertFalse(todo.hasSameDetails(deadline));
        assertFalse(deadline.hasSameDetails(new Deadline("book", "2024-03-01")));
        Event event = new Event("trip", "2024-02-29 10:00", "2024-02-29 11:00");
        assertTrue(event.hasSameDetails(new Event("trip", "2024-02-29 10:00", "2024-02-29 11:00")));
        assertFalse(event.hasSameDetails(new Event("trip", "2024-02-29 10:00", "2024-02-29 12:00")));
    }

    @Test
    void constructors_impossibleDatesAndTimes_rejectInsteadOfAdjusting() {
        for (String date : new String[] {"2023-02-29", "2024-04-31", "2024-13-01", "2024-01-00"}) {
            assertThrows(ClarryException.class, () -> new Deadline("book", date), date);
        }
        for (String time : new String[] {"2024-02-29 24:00", "2024-02-29 10:60", "2023-02-29 10:00"}) {
            assertThrows(ClarryException.class, () -> new Event("trip", time, "2025-01-01 00:00"), time);
        }
    }
}
