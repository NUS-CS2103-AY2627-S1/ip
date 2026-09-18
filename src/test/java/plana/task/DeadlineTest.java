package plana.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class DeadlineTest {

    private static final LocalDate DUE_DATE = LocalDate.of(2026, 8, 31);

    @Test
    void isOn_dateMatchesDeadline_returnsTrue() {
        Deadline deadline = new Deadline("submit report", DUE_DATE);

        assertTrue(deadline.isOn(DUE_DATE));
        assertFalse(deadline.isOn(DUE_DATE.plusDays(1)));
    }

    @Test
    void toString_incompleteDeadline_includesDateAndPendingStatus() {
        Deadline deadline = new Deadline("submit report", DUE_DATE);

        assertEquals("[D][ ] submit report (by: Aug 31 2026)", deadline.toString());
        assertEquals("D | 0 | submit report | 2026-08-31", deadline.toStorageString());
    }

    @Test
    void toString_completedDeadline_includesDoneStatus() {
        Deadline deadline = new Deadline("submit report", DUE_DATE);
        deadline.markAsDone();

        assertEquals("[D][X] submit report (by: Aug 31 2026)", deadline.toString());
        assertEquals("D | 1 | submit report | 2026-08-31", deadline.toStorageString());
    }

    @Test
    void constructor_missingDueDate_assertionFails() {
        assertThrows(AssertionError.class, () -> new Deadline("submit report", null));
    }
}
