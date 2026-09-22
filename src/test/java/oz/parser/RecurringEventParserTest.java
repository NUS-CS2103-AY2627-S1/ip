package oz.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import oz.exception.OzException;
import oz.task.RecurringEvent;

/**
 * Tests weekly recurring-event command parsing.
 */
public class RecurringEventParserTest {
    /** Verifies an omitted end date creates an indefinite recurring series. */
    @Test
    public void parse_withoutUntilDate_returnsIndefiniteSeries() throws OzException {
        RecurringEvent event = RecurringEventParser.parse(
                "tutorial /on 2026-10-02 /start 1000 /end 1200 /every 2 weeks");

        assertEquals("[R] tutorial (from: Oct 02 2026, 10am to: Oct 02 2026, 12pm; "
                + "repeats: every 2 weeks)", event.toString());
    }

    /** Verifies unsupported recurrence units produce a focused error. */
    @Test
    public void parse_unsupportedUnit_throwsException() {
        OzException exception = assertThrows(OzException.class, () ->
                RecurringEventParser.parse(
                        "tutorial /on 2026-10-02 /start 1000 /end 1200 /every 2 days"));

        assertEquals("The recurrence unit must be week or weeks.", exception.getMessage());
    }

    /** Verifies out-of-order recurring flags produce the ordering error. */
    @Test
    public void parse_outOfOrderFlags_throwsException() {
        OzException exception = assertThrows(OzException.class, () ->
                RecurringEventParser.parse(
                        "tutorial /start 1000 /on 2026-10-02 /end 1200 /every 2 weeks"));

        assertEquals("Recurring parameters are out of order. "
                + "Use: recurring <description> /on <date> /start <time> "
                + "/end <time> /every <interval> week|weeks [/until <date>].",
                exception.getMessage());
    }

    /** Verifies recurrence intervals that overflow an integer are rejected. */
    @Test
    public void parse_intervalOverflow_throwsException() {
        OzException exception = assertThrows(OzException.class, () ->
                RecurringEventParser.parse(
                        "tutorial /on 2026-10-02 /start 1000 /end 1200 "
                        + "/every 999999999999999 weeks"));

        assertEquals("The recurrence interval must be a positive whole number.",
                exception.getMessage());
    }
}
