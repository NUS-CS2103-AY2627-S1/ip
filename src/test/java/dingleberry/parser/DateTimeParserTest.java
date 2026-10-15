package dingleberry.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import org.junit.jupiter.api.Test;

import dingleberry.exception.DingleberryException;

/**
 * Tests date parsing with fixed reference dates and event endpoints.
 */
class DateTimeParserTest {
    /** Defines the fixed Tuesday used as the parser reference date. */
    private static final LocalDate TUESDAY = LocalDate.of(2026, 9, 8);
    /** Defines the Monday used for relative weekday boundary tests. */
    private static final LocalDate MONDAY = LocalDate.of(2026, 9, 14);
    /** Defines midnight on the Monday following the reference Tuesday. */
    private static final LocalDateTime NEXT_MONDAY_MIDNIGHT =
            LocalDateTime.of(2026, 9, 14, 0, 0);
    /** Defines a weekday timestamp with a dotted afternoon time. */
    private static final LocalDateTime NEXT_MONDAY_AFTERNOON =
            LocalDateTime.of(2026, 9, 14, 14, 30);
    /** Defines midnight on the Monday after the current Monday. */
    private static final LocalDateTime FOLLOWING_MONDAY_MIDNIGHT =
            LocalDateTime.of(2026, 9, 21, 0, 0);
    /** Defines a time-only value resolved against an inherited date. */
    private static final LocalDateTime INHERITED_THREE_PM =
            LocalDateTime.of(2026, 9, 14, 15, 0);
    /** Defines an inherited date with a twelve-hour morning time. */
    private static final LocalDateTime INHERITED_TWO_THIRTY_AM =
            LocalDateTime.of(2026, 9, 14, 2, 30);
    /** Lists the next occurrences after the fixed reference Tuesday. */
    private static final Map<String, String> NEXT_WEEKDAYS = Map.of(
            "Mon", "2026-09-14", "Tue", "2026-09-15", "Wed", "2026-09-09",
            "Thu", "2026-09-10", "Fri", "2026-09-11", "Sat", "2026-09-12",
            "Sun", "2026-09-13");

    @Test
    void parse_naturalWeekday_returnsNextOccurrence()
            throws DingleberryException {
        assertEquals(NEXT_MONDAY_MIDNIGHT,
                DateTimeParser.parse("Mon", TUESDAY, null));
        assertEquals(NEXT_MONDAY_AFTERNOON,
                DateTimeParser.parse("monday 2.30pm", TUESDAY, null));
    }

    @Test
    void parse_relativeWeekday_honorsThisAndNext()
            throws DingleberryException {
        assertEquals(NEXT_MONDAY_MIDNIGHT,
                DateTimeParser.parse("this Mon", MONDAY, null));
        assertEquals(FOLLOWING_MONDAY_MIDNIGHT,
                DateTimeParser.parse("next Mon", MONDAY, null));
    }

    @Test
    void parse_timeOnly_usesInheritedDate()
            throws DingleberryException {
        assertEquals(INHERITED_THREE_PM,
                DateTimeParser.parse("3pm", TUESDAY, MONDAY));
        assertEquals(INHERITED_TWO_THIRTY_AM,
                DateTimeParser.parse("2:30am", TUESDAY, MONDAY));
    }

    @Test
    void parse_invalidTime_throwsIncorrectParameters() {
        DingleberryException exception = assertThrows(
                DingleberryException.class,
                () -> DateTimeParser.parse("13pm", TUESDAY, MONDAY));

        assertEquals(
                "I couldn't understand that date/time. Use yyyy-MM-dd HHmm,"
                        + " a weekday such as Mon or next Tue, or a time"
                        + " such as 2:30pm.", exception.getMessage());
    }

    @Test
    void parse_timeOnlyWithoutDate_throwsIncorrectParameters() {
        assertThrows(DingleberryException.class,
                () -> DateTimeParser.parse("3pm", TUESDAY, null));
    }

    @Test
    void parse_absoluteDate_acceptsLeapDay() throws DingleberryException {
        assertEquals(LocalDateTime.parse("2028-02-29T18:00"),
                DateTimeParser.parse("2028-02-29 1800", TUESDAY, null));
    }

    @Test
    void parse_absoluteDate_rejectsInvalidDates() {
        for (String input : new String[] {"2026-02-30 1800",
                "2027-02-29 1800", "2026-13-01 1800", "2026-09-08 2400"}) {
            assertThrows(DingleberryException.class,
                    () -> DateTimeParser.parse(input, TUESDAY, null));
        }
    }

    @Test
    void parse_twelveHourBoundary_resolvesNoonAndMidnight()
            throws DingleberryException {
        assertEquals(LocalDateTime.parse("2026-09-14T00:00"),
                DateTimeParser.parse("12am", TUESDAY, MONDAY));
        assertEquals(LocalDateTime.parse("2026-09-14T12:00"),
                DateTimeParser.parse("12 PM", TUESDAY, MONDAY));
    }

    @Test
    void parse_invalidTime_rejectsOutOfRangeValues() {
        for (String input : new String[] {"0am", "13pm", "2:60pm", "2:3pm",
                "25:01", "noon", "Monday invalid"}) {
            assertThrows(DingleberryException.class,
                    () -> DateTimeParser.parse(input, TUESDAY, MONDAY));
        }
    }

    @Test
    void parse_weekdayBoundary_preservesThisAndBareWeekday()
            throws DingleberryException {
        assertEquals(LocalDateTime.parse("2026-09-08T00:00"),
                DateTimeParser.parse("this Tue", TUESDAY, null));
        assertEquals(LocalDateTime.parse("2026-09-15T00:00"),
                DateTimeParser.parse("Tue", TUESDAY, null));
        assertEquals(LocalDateTime.parse("2026-09-15T00:00"),
                DateTimeParser.parse("next Tue", TUESDAY, null));
    }

    @Test
    void parse_eachWeekday_returnsExpectedDate() throws DingleberryException {
        for (Map.Entry<String, String> weekday : NEXT_WEEKDAYS.entrySet()) {
            assertEquals(LocalDate.parse(weekday.getValue()).atStartOfDay(),
                    DateTimeParser.parse(weekday.getKey(), TUESDAY, null));
        }
    }
}
