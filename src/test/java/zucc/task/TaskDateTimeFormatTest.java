package zucc.task;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import zucc.ZuccException;

/**
 * Tests strict parsing and stable formatting of task dates and times.
 */
public class TaskDateTimeFormatTest {
    /**
     * Verifies that a valid date and time with single-digit fields is parsed correctly.
     *
     * @throws ZuccException if the valid date and time cannot be parsed.
     */
    @Test
    public void parse_validSingleDigitDateTime_parsedValueReturned() throws ZuccException {
        assertEquals(
                LocalDateTime.of(2026, 9, 2, 8, 5),
                TaskDateTimeFormat.parse("2/9/2026 0805"));
    }

    /**
     * Verifies that a valid leap-day date and time is parsed correctly.
     *
     * @throws ZuccException if the valid leap-day value cannot be parsed.
     */
    @Test
    public void parse_validLeapDay_parsedValueReturned() throws ZuccException {
        assertEquals(
                LocalDateTime.of(2028, 2, 29, 23, 59),
                TaskDateTimeFormat.parse("29/2/2028 2359"));
    }

    /**
     * Verifies that malformed, impossible, invalid-time, and blank values are rejected.
     */
    @Test
    public void parse_malformedImpossibleOrInvalidTime_exceptionThrown() {
        assertAll(
                () -> assertThrows(ZuccException.class,
                        () -> TaskDateTimeFormat.parse("2026-09-02 0805")),
                () -> assertThrows(ZuccException.class,
                        () -> TaskDateTimeFormat.parse("29/2/2027 0805")),
                () -> assertThrows(ZuccException.class,
                        () -> TaskDateTimeFormat.parse("2/9/2026 2400")),
                () -> assertThrows(ZuccException.class,
                        () -> TaskDateTimeFormat.parse("   ")));
    }

    /**
     * Verifies that a valid date without a time is parsed correctly.
     *
     * @throws ZuccException if the valid date cannot be parsed.
     */
    @Test
    public void parseDate_validDate_parsedValueReturned() throws ZuccException {
        assertEquals(LocalDate.of(2026, 9, 2), TaskDateTimeFormat.parseDate("2/9/2026"));
    }

    /**
     * Verifies that malformed, impossible, and blank date-only values are rejected.
     */
    @Test
    public void parseDate_malformedOrImpossibleDate_exceptionThrown() {
        assertAll(
                () -> assertThrows(ZuccException.class,
                        () -> TaskDateTimeFormat.parseDate("2026-09-02")),
                () -> assertThrows(ZuccException.class,
                        () -> TaskDateTimeFormat.parseDate("31/4/2026")),
                () -> assertThrows(ZuccException.class,
                        () -> TaskDateTimeFormat.parseDate("")));
    }

    /**
     * Verifies the user-facing and persistent formats for a known date and time.
     */
    @Test
    public void formatMethods_dateTime_returnExpectedDisplayAndStorageForms() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 9, 2, 8, 5);

        assertAll(
                () -> assertEquals(
                        "Sep 02 2026, 8:05AM",
                        TaskDateTimeFormat.formatForDisplay(dateTime)),
                () -> assertEquals(
                        "2/9/2026 0805",
                        TaskDateTimeFormat.formatForStorage(dateTime)),
                () -> assertEquals(
                        "Sep 02 2026",
                        TaskDateTimeFormat.formatDateForDisplay(dateTime.toLocalDate())));
    }
}
