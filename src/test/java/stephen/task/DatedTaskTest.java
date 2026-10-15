package stephen.task;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/** Tests the date-matching business rules for scheduled task types. */
class DatedTaskTest {
    /** Verifies that a deadline occurs only on its due date. */
    @Test
    void deadlineOccursOnOnlyExactDueDateMatches() {
        Deadline deadline = new Deadline("submit", LocalDate.of(2024, 6, 15));

        assertFalse(deadline.occursOn(LocalDate.of(2024, 6, 14)));
        assertTrue(deadline.occursOn(LocalDate.of(2024, 6, 15)));
        assertFalse(deadline.occursOn(LocalDate.of(2024, 6, 16)));
    }

    /** Verifies that a multi-day event includes both range boundaries. */
    @Test
    void eventOccursOnMultiDayRangeIncludesBothBoundaries() {
        Event event = new Event("conference",
                LocalDate.of(2024, 6, 10), LocalDate.of(2024, 6, 12));

        assertFalse(event.occursOn(LocalDate.of(2024, 6, 9)));
        assertTrue(event.occursOn(LocalDate.of(2024, 6, 10)));
        assertTrue(event.occursOn(LocalDate.of(2024, 6, 11)));
        assertTrue(event.occursOn(LocalDate.of(2024, 6, 12)));
        assertFalse(event.occursOn(LocalDate.of(2024, 6, 13)));
    }

    /** Verifies that a single-day event matches only its event date. */
    @Test
    void eventOccursOnSingleDayRangeMatchesOnlyThatDay() {
        LocalDate date = LocalDate.of(2024, 6, 10);
        Event event = new Event("one-day event", date, date);

        assertTrue(event.occursOn(date));
        assertFalse(event.occursOn(date.minusDays(1)));
        assertFalse(event.occursOn(date.plusDays(1)));
    }
}
