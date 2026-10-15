package zikiai.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests the user-facing and persistent date formats of deadline tasks.
 */
class DeadlineTest {

    @Test
    void constructor_nullDeadline_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> new Deadline("submit report", null));
    }

    @Test
    void constructor_isoDate_friendlyDisplayAndIsoStorageReturned() {
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 8, 23));

        assertEquals(
                "[D][ ] submit report (by: Aug 23 2026)", deadline.getDescription());
        assertEquals(
                "[D][ ] | submit report | 2026-08-23", deadline.toDataString());
    }

    @Test
    void markAsDone_incompleteDeadline_doneStatusShownInBothRepresentations() {
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 8, 23));

        deadline.markAsDone();

        assertEquals(
                "[D][X] submit report (by: Aug 23 2026)", deadline.getDescription());
        assertEquals(
                "[D][X] | submit report | 2026-08-23", deadline.toDataString());
    }
}
