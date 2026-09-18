package duck.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests deadline display, persistence, status, and identity behavior.
 */
public class DeadlineTest {
    private static final LocalDate DEADLINE_DATE = LocalDate.of(2026, 10, 15);

    @Test
    public void toString_statusChanges_displaysFormattedDateAndCurrentStatus() {
        Deadline deadline = new Deadline("submit report", DEADLINE_DATE);

        assertEquals("[D][ ] submit report (by: Oct 15 2026)", deadline.toString());

        deadline.markAsDone();
        assertEquals("[D][X] submit report (by: Oct 15 2026)", deadline.toString());
    }

    @Test
    public void formatForStorage_specialCharacters_escapesDescriptionAndUsesIsoDate() {
        Deadline deadline = new Deadline("submit A | B \\ C", DEADLINE_DATE);
        deadline.markAsDone();

        assertEquals("D | 1 | submit A \\| B \\\\ C | 2026-10-15",
                deadline.formatForStorage());
    }

    @Test
    public void hasSameDetails_sameDescriptionAndDateIgnoringStatus_returnsTrue() {
        Deadline completedDeadline = new Deadline("submit report", DEADLINE_DATE);
        completedDeadline.markAsDone();

        assertTrue(completedDeadline.hasSameDetails(
                new Deadline("submit report", DEADLINE_DATE)));
    }

    @Test
    public void hasSameDetails_differentDetailsOrType_returnsFalse() {
        Deadline deadline = new Deadline("submit report", DEADLINE_DATE);

        assertFalse(deadline.hasSameDetails(
                new Deadline("submit draft", DEADLINE_DATE)));
        assertFalse(deadline.hasSameDetails(
                new Deadline("submit report", DEADLINE_DATE.plusDays(1))));
        assertFalse(deadline.hasSameDetails(new Todo("submit report")));
        assertFalse(deadline.hasSameDetails(null));
    }
}
