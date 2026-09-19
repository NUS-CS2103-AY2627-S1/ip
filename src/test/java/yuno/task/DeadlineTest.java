package yuno.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class DeadlineTest {
    private static final LocalDateTime DEADLINE = LocalDateTime.of(2026, 8, 26, 15, 30);

    @Test
    void constructor_nullDeadline_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Deadline("submit report", false, null));
    }

    @Test
    void isRelevantFor_dateBeforeDeadline_returnsFalse() {
        Deadline deadline = new Deadline("submit report", false, DEADLINE);

        assertFalse(deadline.isRelevantFor(LocalDate.of(2026, 8, 25)));
    }

    @Test
    void isRelevantFor_deadlineDate_returnsTrue() {
        Deadline deadline = new Deadline("submit report", false, DEADLINE);

        assertTrue(deadline.isRelevantFor(LocalDate.of(2026, 8, 26)));
    }

    @Test
    void isRelevantFor_dateAfterDeadline_returnsTrue() {
        Deadline deadline = new Deadline("submit report", false, DEADLINE);

        assertTrue(deadline.isRelevantFor(LocalDate.of(2026, 8, 27)));
    }

    @Test
    void toStorageString_descriptionContainsDelimiter_preservesDescriptionAndDateTime() {
        Deadline deadline = new Deadline("compare A | B", true, DEADLINE);

        assertEquals(
                "D | X | compare A | B | Aug 26 2026, 03:30 PM",
                deadline.toStorageString());
    }

    @Test
    void toString_deadline_returnsDisplayFormat() {
        Deadline deadline = new Deadline("submit report", false, DEADLINE);

        assertEquals(
                "[D][ ] submit report (by: Aug 26 2026, 03:30 PM)",
                deadline.toString());
    }
}
