package marcus.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests the behavior of {@link Deadline}.
 */
class DeadlineTest {
    /** Verifies that a new deadline displays its date and incomplete status. */
    @Test
    void toString_newDeadline_showsDeadlineAndIncompleteStatus() {
        LocalDate date = LocalDate.parse("2026-08-27");
        Deadline deadline = new Deadline("Meet Drake", date);

        assertEquals("[D][ ] Meet Drake (by: Aug 27 2026)", deadline.toString());
    }

    /** Verifies that a completed deadline displays its completed status. */
    @Test
    void toString_completedDeadline_showsCompletedStatus() {
        Deadline deadline = new Deadline("Meet Drake", LocalDate.parse("2026-08-27"));
        deadline.markAsDone();

        assertEquals("[D][X] Meet Drake (by: Aug 27 2026)", deadline.toString());
    }

    /** Verifies that a deadline occurs on its due date. */
    @Test
    void occursOn_matchingDate_returnsTrue() {
        Deadline deadline = new Deadline("Meet Drake", LocalDate.parse("2026-08-27"));

        assertTrue(deadline.occursOn(LocalDate.parse("2026-08-27")));
    }

    /** Verifies that a deadline does not occur before its due date. */
    @Test
    void occursOn_earlierDate_returnsFalse() {
        Deadline deadline = new Deadline("Meet Drake", LocalDate.parse("2026-08-27"));

        assertFalse(deadline.occursOn(LocalDate.parse("2026-08-26")));
    }

    /** Verifies that a deadline does not occur after its due date. */
    @Test
    void occursOn_laterDate_returnsFalse() {
        Deadline deadline = new Deadline("Meet Drake", LocalDate.parse("2026-08-27"));

        assertFalse(deadline.occursOn(LocalDate.parse("2026-08-28")));
    }

    /** Verifies that a new deadline is persisted with an ISO date and incomplete status. */
    @Test
    void toFileString_newDeadline_savesIsoDateAndIncompleteStatus() {
        Deadline deadline = new Deadline("Meet Drake", LocalDate.parse("2026-08-27"));

        assertEquals("D | 0 | Meet Drake | 2026-08-27", deadline.toFileString());
    }

    /** Verifies that a completed deadline is persisted with a completed status. */
    @Test
    void toFileString_completedDeadline_savesCompletedStatus() {
        Deadline deadline = new Deadline("Meet Drake", LocalDate.parse("2026-08-27"));
        deadline.markAsDone();

        assertEquals("D | 1 | Meet Drake | 2026-08-27", deadline.toFileString());
    }
}
