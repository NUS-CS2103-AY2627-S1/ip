package soar.parser;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Tests the conversion of supported scheduling text into calendar dates.
 */
public class DateTimeParserTest {
    private static final LocalDate EXPECTED_DATE = LocalDate.of(2019, 10, 15);
    private static final LocalDateTime EXPECTED_DATE_TIME = LocalDateTime.of(2019, 10, 15, 18, 0);

    /** Verifies that every documented date format is accepted. */
    @Test
    public void parseDate_supportedFormats_returnsDate() {
        Map<String, LocalDate> supportedInputs = Map.of(
                "2019-10-15", EXPECTED_DATE,
                "15/10/2019", EXPECTED_DATE);

        assertAll(supportedInputs.entrySet().stream()
                .map(entry -> () -> assertEquals(
                        entry.getValue(),
                        DateTimeParser.parseDate(entry.getKey()).orElseThrow(),
                        "Failed to parse supported date: " + entry.getKey())));
    }

    /** Verifies strict date validation while retaining valid leap days. */
    @Test
    public void parseDate_leapDay_returnsDate() {
        assertEquals(
                LocalDate.of(2020, 2, 29),
                DateTimeParser.parseDate("2020-02-29").orElseThrow());
    }

    /** Verifies that dates which do not exist are rejected. */
    @Test
    public void parseDate_impossibleDates_returnsEmpty() {
        assertTrue(DateTimeParser.parseDate("2019-02-29").isEmpty());
        assertTrue(DateTimeParser.parseDate("31/04/2019").isEmpty());
        assertTrue(DateTimeParser.parseDate("2019-13-01").isEmpty());
    }

    /** Verifies that date-time, empty, malformed, and unsupported inputs are rejected. */
    @Test
    public void parseDate_unrecognizedInputs_returnsEmpty() {
        assertTrue(DateTimeParser.parseDate("2019-10-15T18:00").isEmpty());
        assertTrue(DateTimeParser.parseDate("").isEmpty());
        assertTrue(DateTimeParser.parseDate("not a date").isEmpty());
        assertTrue(DateTimeParser.parseDate("15-10-2019").isEmpty());
        assertTrue(DateTimeParser.parseDate(" 2019-10-15 ").isEmpty());
    }

    /** Verifies that every documented date-time format is accepted. */
    @Test
    public void parseDateTime_supportedFormats_returnsDateTime() {
        Map<String, LocalDateTime> supportedInputs = Map.of(
                "15/10/2019 1800", EXPECTED_DATE_TIME,
                "2019-10-15 18:00", EXPECTED_DATE_TIME,
                "15 Oct 2019 6:00 PM", EXPECTED_DATE_TIME,
                "2019-10-15T18:00", EXPECTED_DATE_TIME);

        assertAll(supportedInputs.entrySet().stream()
                .map(entry -> () -> assertEquals(
                        entry.getValue(),
                        DateTimeParser.parseDateTime(entry.getKey()).orElseThrow(),
                        "Failed to parse supported date-time: " + entry.getKey())));
    }

    /** Verifies that the textual date-time format is case-insensitive. */
    @Test
    public void parseDateTime_lowercaseTextualFormat_returnsDateTime() {
        assertEquals(
                EXPECTED_DATE_TIME,
                DateTimeParser.parseDateTime("15 oct 2019 6:00 pm").orElseThrow());
    }

    /** Verifies strict date and time validation. */
    @Test
    public void parseDateTime_impossibleValues_returnsEmpty() {
        assertTrue(DateTimeParser.parseDateTime("29/02/2019 1800").isEmpty());
        assertTrue(DateTimeParser.parseDateTime("31/04/2019 1800").isEmpty());
        assertTrue(DateTimeParser.parseDateTime("2019-10-15 24:00").isEmpty());
        assertTrue(DateTimeParser.parseDateTime("2019-10-15 18:60").isEmpty());
    }

    /** Verifies that date-only, empty, malformed, and padded inputs are rejected. */
    @Test
    public void parseDateTime_unrecognizedInputs_returnsEmpty() {
        assertTrue(DateTimeParser.parseDateTime("2019-10-15").isEmpty());
        assertTrue(DateTimeParser.parseDateTime("").isEmpty());
        assertTrue(DateTimeParser.parseDateTime("not a date-time").isEmpty());
        assertTrue(DateTimeParser.parseDateTime("15-10-2019 18:00").isEmpty());
        assertTrue(DateTimeParser.parseDateTime(" 2019-10-15T18:00 ").isEmpty());
    }

    /** Verifies that every documented date and date-time format is accepted. */
    @Test
    public void parseCalendarDate_supportedFormats_returnsCalendarDate() {
        Map<String, LocalDate> supportedInputs = Map.of(
                "2019-10-15", EXPECTED_DATE,
                "15/10/2019", EXPECTED_DATE,
                "15/10/2019 1800", EXPECTED_DATE,
                "2019-10-15 18:00", EXPECTED_DATE,
                "15 Oct 2019 6:00 PM", EXPECTED_DATE,
                "2019-10-15T18:00", EXPECTED_DATE);

        assertAll(supportedInputs.entrySet().stream()
                .map(entry -> () -> assertEquals(
                        entry.getValue(),
                        DateTimeParser.parseCalendarDate(entry.getKey()).orElseThrow(),
                        "Failed to parse supported input: " + entry.getKey())));
    }

    /** Verifies strict date validation while retaining valid leap days. */
    @Test
    public void parseCalendarDate_leapDay_returnsCalendarDate() {
        assertEquals(
                LocalDate.of(2020, 2, 29),
                DateTimeParser.parseCalendarDate("2020-02-29").orElseThrow());
    }

    /** Verifies that dates which do not exist are rejected. */
    @Test
    public void parseCalendarDate_impossibleDates_returnsEmpty() {
        assertTrue(DateTimeParser.parseCalendarDate("2019-02-29").isEmpty());
        assertTrue(DateTimeParser.parseCalendarDate("31/04/2019").isEmpty());
        assertTrue(DateTimeParser.parseCalendarDate("2019-13-01").isEmpty());
    }

    /** Verifies that empty, malformed, and unsupported inputs are rejected. */
    @Test
    public void parseCalendarDate_unrecognizedInputs_returnsEmpty() {
        assertTrue(DateTimeParser.parseCalendarDate("").isEmpty());
        assertTrue(DateTimeParser.parseCalendarDate("not a date").isEmpty());
        assertTrue(DateTimeParser.parseCalendarDate("15-10-2019").isEmpty());
        assertTrue(DateTimeParser.parseCalendarDate(" 2019-10-15 ").isEmpty());
    }
}
