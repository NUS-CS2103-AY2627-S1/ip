package crystal.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests deadline normalization, date matching, and representations.
 */
public class DeadlineTest {
    /** Verifies that supported deadline dates and times are normalized consistently. */
    @Test
    public void representations_supportedDateTime_normalizedForDisplayAndStorage() {
        Deadline deadline = new Deadline("return book", "2/12/26 630pm");

        assertEquals("D | 0 | return book | 02 Dec 2026 1830", deadline.toDataString());
        assertEquals("[D][ ] return book (by: 02 Dec 2026 1830)", deadline.toString());
    }

    /** Verifies that a dated deadline matches only its own calendar date. */
    @Test
    public void occursOn_datedDeadline_matchesOnlyDeadlineDate() {
        Deadline deadline = new Deadline("return book", "2Dec26 0900");

        assertTrue(deadline.occursOn(LocalDate.of(2026, 12, 2)));
        assertFalse(deadline.occursOn(LocalDate.of(2026, 12, 1)));
    }

    /** Verifies that unrestricted deadline text does not match a calendar date. */
    @Test
    public void occursOn_textDeadline_returnsFalse() {
        Deadline deadline = new Deadline("return book", "Monday");

        assertFalse(deadline.occursOn(LocalDate.of(2026, 12, 2)));
        assertEquals("D | 0 | return book | Monday", deadline.toDataString());
    }

    /** Verifies that completed deadlines persist their completed status. */
    @Test
    public void toDataString_completedDeadline_includesCompletedStatus() {
        Deadline deadline = new Deadline("return book", "2026-12-02");
        deadline.markAsDone();

        assertEquals("D | 1 | return book | 02 Dec 2026", deadline.toDataString());
    }
}
