package oz.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import oz.exception.OzException;

/**
 * Tests weekly recurrence validation, occurrence calculation, and display.
 */
public class WeeklyRecurrenceTest {
    /** Verifies interval and inclusive end-date boundaries. */
    @Test
    public void occursOn_boundedTwoWeekRule_returnsExpectedResults() throws OzException {
        WeeklyRecurrence recurrence = new WeeklyRecurrence(
                LocalDate.of(2026, 10, 2), 2, LocalDate.of(2026, 10, 30));

        assertFalse(recurrence.occursOn(LocalDate.of(2026, 10, 1)));
        assertTrue(recurrence.occursOn(LocalDate.of(2026, 10, 2)));
        assertFalse(recurrence.occursOn(LocalDate.of(2026, 10, 9)));
        assertTrue(recurrence.occursOn(LocalDate.of(2026, 10, 30)));
        assertFalse(recurrence.occursOn(LocalDate.of(2026, 11, 13)));
    }

    /** Verifies singular, plural, finite, and indefinite display wording. */
    @Test
    public void toDisplayString_differentRules_returnsNaturalWording() throws OzException {
        LocalDate firstDate = LocalDate.of(2026, 10, 2);

        assertEquals("every 1 week",
                new WeeklyRecurrence(firstDate, 1, null).toDisplayString());
        assertEquals("every 2 weeks until Dec 31 2026",
                new WeeklyRecurrence(firstDate, 2, LocalDate.of(2026, 12, 31))
                        .toDisplayString());
    }

    /** Verifies invalid intervals and end dates are rejected. */
    @Test
    public void constructor_invalidRule_throwsException() {
        LocalDate firstDate = LocalDate.of(2026, 10, 2);

        assertThrows(OzException.class, () ->
                new WeeklyRecurrence(firstDate, 0, null));
        assertThrows(OzException.class, () ->
                new WeeklyRecurrence(firstDate, 1, LocalDate.of(2026, 10, 1)));
    }
}
