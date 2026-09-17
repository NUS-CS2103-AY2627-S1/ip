package jarvis;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/** Tests deadline-specific task behavior. */
class DeadlineTest {
    @Test
    void getBy_deadline_returnsOriginalDate() {
        LocalDate dueDate = LocalDate.of(2026, 8, 24);
        Deadline deadline = new Deadline("submit report", dueDate);

        assertEquals(dueDate, deadline.getBy());
    }

    @Test
    void getFormattedBy_deadline_returnsDisplayDate() {
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 8, 24));

        assertEquals("Aug 24 2026", deadline.getFormattedBy());
    }

    @Test
    void toString_completedDeadline_includesTypeStatusDescriptionAndDate() {
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 8, 24));
        deadline.markAsDone();

        assertEquals("[D][X] submit report (by: Aug 24 2026)", deadline.toString());
    }

    @Test
    void snoozeUntil_deadline_changesDueDate() {
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 8, 24));
        deadline.snoozeUntil(LocalDate.of(2026, 8, 31));
        assertEquals(LocalDate.of(2026, 8, 31), deadline.getBy());
    }

    @Test
    void snoozeUntil_completedDeadline_preservesCompletedStatus() {
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 8, 24));
        deadline.markAsDone();

        deadline.snoozeUntil(LocalDate.of(2026, 8, 31));

        assertEquals("X", deadline.getStatusIcon());
        assertEquals("[D][X] submit report (by: Aug 31 2026)", deadline.toString());
    }
}
