package potatobot.model.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import potatobot.exception.PotatoBotException;

public class EventTest {
    @Test
    public void constructor_invalidEndDate_reportsDateFormatError() {
        assertEquals("Use the date format YYYY-MM-DD, for example 2019-12-02.",
                assertThrows(PotatoBotException.class,
                        () -> new Event("meeting", "2026-08-31", "2026-02-30")).getMessage());
    }

    @Test
    public void constructor_mixedDateFormatsAndYearBoundary_formatsBothDates() throws PotatoBotException {
        assertEquals("meeting (Event, from: Dec 31 2026 to: Jan 01 2027)",
                new Event("meeting", "Dec 31 2026", "2027-01-01").toString());
    }

    @Test
    public void constructor_isoDates_formattedEventCreated() throws PotatoBotException {
        Event event = new Event("conference", "2026-08-31", "2026-09-02");

        assertEquals(
                "conference (Event, from: Aug 31 2026 to: Sep 02 2026)",
                event.toString());
    }

    @Test
    public void constructor_storedDisplayDates_formattedEventCreated()
            throws PotatoBotException {
        Event event = new Event("conference", "Aug 31 2026", "Sep 02 2026");

        assertEquals(
                "conference (Event, from: Aug 31 2026 to: Sep 02 2026)",
                event.toString());
    }

    @Test
    public void constructor_sameStartAndEndDate_eventCreated() throws PotatoBotException {
        Event event = new Event("conference", "2026-08-31", "2026-08-31");

        assertEquals(
                "conference (Event, from: Aug 31 2026 to: Aug 31 2026)",
                event.toString());
    }

    @Test
    public void constructor_endBeforeStart_exceptionThrown() {
        PotatoBotException exception = assertThrows(PotatoBotException.class, () -> new Event(
                "conference", "2026-09-02", "2026-08-31"));

        assertEquals(
                "The event end date cannot be before its start date.",
                exception.getMessage());
    }

    @Test
    public void constructor_invalidDate_exceptionThrown() {
        PotatoBotException exception = assertThrows(PotatoBotException.class,
                () -> new Event("conference", "invalid", "2026-09-02"));

        assertEquals(
                "Use the date format YYYY-MM-DD, for example 2019-12-02.",
                exception.getMessage());
    }
}
