package geek.time;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

import geek.time.DateTimeParser.ParsedDateTime;

/**
 * Tests the supported user-facing and stored date-time formats.
 */
class DateTimeParserTest {
    private static final LocalDate EXPECTED_DATE =
            LocalDate.of(2019, 12, 2);

    private static final LocalDateTime EXPECTED_DATE_TIME =
            LocalDateTime.of(2019, 12, 2, 18, 0);

    @Test
    void parseDate_supportedFormats_returnSameDate() {
        assertAll(() -> assertEquals(
                        EXPECTED_DATE,
                        DateTimeParser.parseDate("2019-12-02")
                ), () -> assertEquals(
                        EXPECTED_DATE,
                        DateTimeParser.parseDate("2/12/2019")
                ), () -> assertEquals(
                        EXPECTED_DATE,
                        DateTimeParser.parseDate("2-12-2019")
                ), () -> assertEquals(
                        EXPECTED_DATE,
                        DateTimeParser.parseDate("2 Dec 2019")
                ), () -> assertEquals(
                        EXPECTED_DATE,
                        DateTimeParser.parseDate("Dec 2, 2019")
                )
        );
    }

    @Test
    void parseDate_mixedCaseAndExtraSpaces_returnsDate() {
        LocalDate result = DateTimeParser.parseDate(
                "  dEc   2,   2019  "
        );

        assertEquals(EXPECTED_DATE, result);
    }

    @Test
    void parseDate_invalidCalendarDate_throwsException() {
        assertThrows(
                DateTimeParseException.class, () -> DateTimeParser.parseDate("2019-02-29")
        );
    }

    @Test
    void parseDateTime_supported24HourFormat_returnsDateTime() {
        LocalDateTime result = DateTimeParser.parseDateTime(
                "2/12/2019 1800"
        );

        assertEquals(EXPECTED_DATE_TIME, result);
    }

    @Test
    void parseDateTime_supported12HourFormat_returnsDateTime() {
        LocalDateTime result = DateTimeParser.parseDateTime(
                "Dec 2 2019 6:00 PM"
        );

        assertEquals(EXPECTED_DATE_TIME, result);
    }

    @Test
    void parseDateTime_dateWithoutTime_throwsException() {
        assertThrows(
                DateTimeParseException.class, () -> DateTimeParser.parseDateTime("2019-12-02")
        );
    }

    @Test
    void parseDeadline_dateOnly_marksTimeAsAbsent() {
        ParsedDateTime result = DateTimeParser.parseDeadline(
                "2019-12-02"
        );

        assertAll(() -> assertEquals(
                        EXPECTED_DATE.atStartOfDay(),
                        result.value()
                ), () -> assertFalse(result.hasTime())
        );
    }

    @Test
    void parseDeadline_dateTime_marksTimeAsPresent() {
        ParsedDateTime result = DateTimeParser.parseDeadline(
                "2019-12-02 18:00"
        );

        assertAll(() -> assertEquals(
                        EXPECTED_DATE_TIME,
                        result.value()
                ), () -> assertTrue(result.hasTime())
        );
    }

    @Test
    void parseStoredDeadline_storedForms_preserveTimeFlag() {
        ParsedDateTime dateOnly =
                DateTimeParser.parseStoredDeadline("2019-12-02");
        ParsedDateTime dateTime =
                DateTimeParser.parseStoredDeadline(
                        "2019-12-02T18:00"
                );

        assertAll(() -> assertEquals(
                        EXPECTED_DATE.atStartOfDay(),
                        dateOnly.value()
                ), () -> assertFalse(dateOnly.hasTime()), () -> assertEquals(
                        EXPECTED_DATE_TIME,
                        dateTime.value()
                ), () -> assertTrue(dateTime.hasTime())
        );
    }

    @Test
    void parseStoredDeadline_invalidValue_throwsException() {
        assertThrows(
                DateTimeParseException.class, () -> DateTimeParser.parseStoredDeadline("not-a-date")
        );
    }
}
