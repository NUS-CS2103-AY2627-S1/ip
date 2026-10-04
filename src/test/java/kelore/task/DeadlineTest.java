package kelore.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests display, storage, and date behavior of {@link Deadline}. */
public class DeadlineTest {
    private static final LocalDateTime DUE_DATE = LocalDateTime.of(2026, 9, 2, 18, 5);

    @Test
    public void toString_incompleteDeadline_formatsDescriptionAndDate() {
        Deadline deadline = new Deadline("submit report", DUE_DATE);

        assertEquals("[D][ ] submit report (by: Sep 2 2026, 6:05 PM)", deadline.toString());
    }

    @Test
    public void toStorageString_markedDeadline_formatsAllFields() {
        Deadline deadline = new Deadline("submit report", DUE_DATE);
        deadline.markAsDone();

        assertEquals("D | 1 | submit report | 2026-09-02T18:05", deadline.toStorageString());
    }

    @Test
    public void occursOn_beforeOnAndAfterDueDate_matchesOnlyDueDate() {
        Deadline deadline = new Deadline("submit report", DUE_DATE);

        assertFalse(deadline.occursOn(LocalDate.of(2026, 9, 1)));
        assertTrue(deadline.occursOn(LocalDate.of(2026, 9, 2)));
        assertFalse(deadline.occursOn(LocalDate.of(2026, 9, 3)));
    }

    @Test
    public void constructor_nullDueDate_assertionError() {
        assertThrows(AssertionError.class, () -> new Deadline("submit report", null));
    }
}
