package monday.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

/**
 * Tests for Deadline.
 */
class DeadlineTest {
    @Test
    void constructor_withDateAndTime_createsIncompleteDeadline() {
        LocalDate date = LocalDate.of(2026, 8, 30);
        LocalTime time = LocalTime.of(14, 30);
        Deadline deadline = new Deadline("submit report", date, time);

        assertEquals("submit report", deadline.getDescription());
        assertFalse(deadline.isDone());
        assertEquals(date, deadline.getDeadlineDate());
        assertEquals(time, deadline.getDeadlineTime());
        assertEquals("[D][ ] submit report (by: 30 Aug 2026 1430)",
                deadline.toString());
    }

    @Test
    void constructor_withCompletionStatus_createsDeadlineWithGivenStatus() {
        Deadline deadline = new Deadline(
                "submit report",
                true,
                LocalDate.of(2026, 8, 30),
                LocalTime.of(14, 30));

        assertTrue(deadline.isDone());
        assertEquals("[D][X] submit report (by: 30 Aug 2026 1430)",
                deadline.toString());
    }

    @Test
    void constructor_withoutTime_storesNullTimeAndDisplaysOnlyDate() {
        Deadline deadline = new Deadline(
                "submit report",
                LocalDate.of(2026, 8, 30),
                null);

        assertNull(deadline.getDeadlineTime());
        assertEquals("[D][ ] submit report (by: 30 Aug 2026)",
                deadline.toString());
    }

    @Test
    void constructor_withNotes_storesAndDisplaysNotes() {
        Deadline deadline = new Deadline(
                "submit report",
                LocalDate.of(2026, 8, 30),
                LocalTime.of(14, 30),
                "Attach the appendix");

        assertEquals("Attach the appendix", deadline.getNotes());
        assertEquals("[D][ ] submit report (by: 30 Aug 2026 1430) Attach the appendix",
                deadline.toString());
    }

    @Test
    void markAsDone_marksDeadline() {
        Deadline deadline = new Deadline(
                "submit report",
                LocalDate.of(2026, 8, 30),
                LocalTime.of(14, 30));

        deadline.markAsDone();

        assertTrue(deadline.isDone());
        assertEquals("[D][X] submit report (by: 30 Aug 2026 1430)",
                deadline.toString());
    }

    @Test
    void markAsNotDone_unmarksCompletedDeadline() {
        Deadline deadline = new Deadline(
                "submit report",
                true,
                LocalDate.of(2026, 8, 30),
                LocalTime.of(14, 30));

        deadline.markAsNotDone();

        assertFalse(deadline.isDone());
        assertEquals("[D][ ] submit report (by: 30 Aug 2026 1430)",
                deadline.toString());
    }
}
