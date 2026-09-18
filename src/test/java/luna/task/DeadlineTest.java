package luna.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Deadline}.
 */
public class DeadlineTest {
    @Test
    public void getByDate_deadlineCreated_returnsStoredDate() {
        LocalDate deadlineDate = LocalDate.of(2026, 8, 27);
        Deadline deadline = new Deadline("submit report", deadlineDate);

        assertEquals(deadlineDate, deadline.getByDate());
    }

    @Test
    public void constructor_nullDate_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Deadline("submit report", null));
    }

    @Test
    public void toString_deadlineCreated_formatsDateForDisplay() {
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 9, 15));

        assertEquals("[D][ ] submit report (by: 15 Sep 2026)", deadline.toString());
        assertEquals("D | 0 | submit report | 2026-09-15", deadline.toStorageString());
    }

    @Test
    public void hasSameDetails_descriptionAndDateMatch_returnsTrue() {
        Deadline firstDeadline = new Deadline("Submit Report", LocalDate.of(2026, 9, 15));
        Deadline secondDeadline = new Deadline("submit report", LocalDate.of(2026, 9, 15));

        assertTrue(firstDeadline.hasSameDetails(secondDeadline));
    }

    @Test
    public void hasSameDetails_dateDiffers_returnsFalse() {
        Deadline firstDeadline = new Deadline("submit report", LocalDate.of(2026, 9, 15));
        Deadline secondDeadline = new Deadline("submit report", LocalDate.of(2026, 9, 16));

        assertFalse(firstDeadline.hasSameDetails(secondDeadline));
    }
}
