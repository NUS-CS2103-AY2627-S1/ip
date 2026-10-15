package cheecken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Checks relative date keywords using a fixed clock.
 */
class DateTimeValueTest {
    private static final Clock FIXED = Clock.fixed(Instant.parse("2026-09-04T10:30:00Z"), ZoneOffset.UTC);

    /**
     * Verifies today resolves to date only.
     */
    @Test
    void todayResolvesToDateOnly() {
        assertEquals("Sep 04 2026", DateTimeValue.parse("today", "deadline", FIXED).display());
    }

    /**
     * Verifies now resolves to date and time.
     */
    @Test
    void nowResolvesToDateAndTime() {
        assertEquals("Sep 04 2026 10:30 AM", DateTimeValue.parse("now", "deadline", FIXED).display());
    }

    /**
     * Verifies parse with natural date: resolves strictly next weekday.
     * @param input input supplied by the test case
     * @param expected expected result for this test case
     */
    @ParameterizedTest
    @CsvSource({
        "today, 2026-09-04", "tomorrow, 2026-09-05",
        "Mon, 2026-09-07", "Monday, 2026-09-07",
        "Tue, 2026-09-08", "Tuesday, 2026-09-08",
        "Wed, 2026-09-09", "Wednesday, 2026-09-09",
        "Thu, 2026-09-10", "Thursday, 2026-09-10",
        "Fri, 2026-09-11", "Friday, 2026-09-11",
        "Sat, 2026-09-05", "Saturday, 2026-09-05",
        "Sun, 2026-09-06", "Sunday, 2026-09-06"
    })
    void parse_naturalDate_resolvesStrictlyNextWeekday(String input, String expected) {
        DateTimeValue value = DateTimeValue.parse("  " + input.toUpperCase(Locale.ROOT) + "  ", "deadline", FIXED);
        assertEquals(expected, value.storage());
        assertFalse(value.hasExplicitTime());
    }

    /**
     * Verifies parse with natural date with time: preserves explicit time.
     * @param input input supplied by the test case
     * @param expected expected result for this test case
     */
    @ParameterizedTest
    @CsvSource({
        "today 0900, 2026-09-04T09:00", "tomorrow 1800, 2026-09-05T18:00",
        "Fri 0000, 2026-09-11T00:00", "Monday 2359, 2026-09-07T23:59"
    })
    void parse_naturalDateWithTime_preservesExplicitTime(String input, String expected) {
        DateTimeValue value = DateTimeValue.parse(input, "event", FIXED);
        assertEquals(expected, value.storage());
        assertTrue(value.hasExplicitTime());
    }

    /**
     * Verifies parse with whitespace between date and time: is accepted.
     */
    @Test
    void parse_whitespaceBetweenDateAndTime_isAccepted() {
        assertEquals("2026-09-05T09:00", DateTimeValue.parse(" tomorrow \t 0900 ", "event", FIXED).storage());
    }

    /**
     * Verifies parse with unsupported expression: throws date time exception.
     * @param input input supplied by the test case
     */
    @ParameterizedTest
    @ValueSource(strings = {"yesterday", "next Monday", "next week", "in 3 days", "Tues", "Thurs", "Mon.",
        "today 2400", "today 2360", "today 900", "today 09000", "today 09:00", "tomorrow 6pm",
        "Mon at 0900", "now 0900", "today 0900 extra", ""})
    void parse_unsupportedExpression_throwsDateTimeException(String input) {
        assertThrows(CheeckenDateTimeException.class, () -> DateTimeValue.parse(input, "deadline", FIXED));
    }

    /**
     * Verifies parse with absolute format: preserves existing support.
     * @param input input supplied by the test case
     * @param expected expected result for this test case
     */
    @ParameterizedTest
    @CsvSource({
        "4/9/2026, 2026-09-04", "4/9/2026 0900, 2026-09-04T09:00",
        "2026-09-04, 2026-09-04", "2026-09-04T09:00:15, 2026-09-04T09:00:15"
    })
    void parse_absoluteFormat_preservesExistingSupport(String input, String expected) {
        assertEquals(expected, DateTimeValue.parse(input, "event", FIXED).storage());
    }

    /**
     * Verifies parse with calendar boundary: uses clock zone.
     * @param instant reference instant in ISO format
     * @param zone timezone for the returned clock
     * @param input input supplied by the test case
     * @param expected expected result for this test case
     */
    @ParameterizedTest
    @CsvSource({
        "2026-12-31T23:59:59Z, UTC, tomorrow, 2027-01-01",
        "2028-02-28T10:00:00Z, UTC, tomorrow, 2028-02-29",
        "2028-02-29T10:00:00Z, UTC, tomorrow, 2028-03-01",
        "2026-09-06T16:00:00Z, Asia/Singapore, Mon, 2026-09-14",
        "2026-09-06T16:00:00Z, UTC, Mon, 2026-09-07",
        "2026-03-08T06:59:59Z, America/New_York, tomorrow, 2026-03-09"
    })
    void parse_calendarBoundary_usesClockZone(String instant, String zone, String input, String expected) {
        Clock clock = Clock.fixed(Instant.parse(instant), ZoneId.of(zone));
        assertEquals(expected, DateTimeValue.parse(input, "deadline", clock).storage());
    }

    /**
     * Verifies parse with now: preserves local instant and precision.
     */
    @Test
    void parse_now_preservesLocalInstantAndPrecision() {
        Clock clock = Clock.fixed(Instant.parse("2026-09-04T23:59:59.123Z"), ZoneId.of("Asia/Singapore"));
        assertEquals("2026-09-05T07:59:59.123", DateTimeValue.parse("NOW", "event", clock).storage());
    }
}
