package oz.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import oz.exception.OzException;

/**
 * Tests weekly recurrence, occurrence completion, validation, and serialization.
 */
public class RecurringEventTest {

    @Test
    public void occursOn_weeklyIntervalAndEndDate_returnsExpectedResults() throws OzException {
        RecurringEvent event = createRecurringEvent(2, LocalDate.of(2026, 10, 30));

        assertTrue(event.occursOn(LocalDate.of(2026, 10, 2)));
        assertFalse(event.occursOn(LocalDate.of(2026, 10, 9)));
        assertTrue(event.occursOn(LocalDate.of(2026, 10, 16)));
        assertTrue(event.occursOn(LocalDate.of(2026, 10, 30)));
        assertFalse(event.occursOn(LocalDate.of(2026, 11, 13)));
    }

    @Test
    public void constructor_invalidRule_exceptionThrown() throws OzException {
        TaskDateTime start = TaskDateTime.parse("2026-10-02 1400");
        TaskDateTime sameDayEnd = TaskDateTime.parse("2026-10-02 1500");
        TaskDateTime nextDayEnd = TaskDateTime.parse("2026-10-03 1500");

        assertThrows(OzException.class, () ->
                new RecurringEvent("meeting", start, nextDayEnd, 1, null));
        assertThrows(OzException.class, () ->
                new RecurringEvent("meeting", start, sameDayEnd, 0, null));
        assertThrows(OzException.class, () ->
                new RecurringEvent("meeting", start, sameDayEnd, 1,
                        LocalDate.of(2026, 10, 1)));
    }

    @Test
    public void markAndUnmarkOccurrence_validAndRepeatedOperations_correctState() throws OzException {
        RecurringEvent event = createRecurringEvent(1, null);
        LocalDate occurrenceDate = LocalDate.of(2026, 10, 9);

        event.markOccurrence(occurrenceDate);
        assertEquals("[R][X] project meeting (from: Oct 09 2026, 2pm to: Oct 09 2026, 3pm)",
                event.toOccurrenceString(occurrenceDate));
        assertThrows(OzException.class, () -> event.markOccurrence(occurrenceDate));

        event.unmarkOccurrence(occurrenceDate);
        assertEquals("[R][ ] project meeting (from: Oct 09 2026, 2pm to: Oct 09 2026, 3pm)",
                event.toOccurrenceString(occurrenceDate));
        assertThrows(OzException.class, () -> event.unmarkOccurrence(occurrenceDate));
    }

    @Test
    public void occurrenceOperations_nonOccurrenceDate_exceptionThrown() throws OzException {
        RecurringEvent event = createRecurringEvent(1, null);
        LocalDate nonOccurrenceDate = LocalDate.of(2026, 10, 3);

        assertThrows(OzException.class, () -> event.markOccurrence(nonOccurrenceDate));
        assertThrows(OzException.class, () -> event.toOccurrenceString(nonOccurrenceDate));
    }

    @Test
    public void displayAndStorage_finiteSeries_correctFormats() throws OzException {
        RecurringEvent event = createRecurringEvent(2, LocalDate.of(2026, 12, 31));
        event.markOccurrence(LocalDate.of(2026, 10, 2));
        event.markOccurrence(LocalDate.of(2026, 10, 16));

        assertEquals("[R] project meeting (from: Oct 02 2026, 2pm to: Oct 02 2026, 3pm; "
                + "repeats: every 2 weeks until Dec 31 2026)", event.toString());
        assertEquals("R | 0 | project meeting | 2026-10-02 1400 | 2026-10-02 1500 | 2 | "
                + "2026-12-31 | 2026-10-02,2026-10-16", event.toFileFormat());
    }

    /**
     * Creates the standard recurring event fixture used by the tests.
     *
     * @param weekInterval Weeks between occurrences.
     * @param untilDate Inclusive recurrence end date, or null.
     * @return Recurring event fixture.
     * @throws OzException If fixture construction fails.
     */
    private RecurringEvent createRecurringEvent(int weekInterval, LocalDate untilDate)
            throws OzException {
        return new RecurringEvent("project meeting",
                TaskDateTime.parse("2026-10-02 1400"),
                TaskDateTime.parse("2026-10-02 1500"), weekInterval, untilDate);
    }
}
