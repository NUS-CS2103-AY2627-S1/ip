package com.benthecat.kachow.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.junit.jupiter.api.Test;

/**
 * Tests the date and time formats exercised by the console UI test plan.
 */
class DateTimeParserTest {
    /** Verifies that every supported date syntax produces the expected calendar date. */
    @Test
    void parse_supportedDateFormats_returnExpectedDates() {
        assertEquals(LocalDate.of(2019, 10, 15), DateTimeParser.parse("2019-10-15").date());
        assertEquals(LocalDate.of(2019, 12, 2), DateTimeParser.parse("2/12/2019").date());
        assertEquals(LocalDate.of(2019, 12, 3), DateTimeParser.parse("2019/12/3").date());
        assertEquals(LocalDate.of(2019, 12, 3), DateTimeParser.parse("12/03/2019").date());
    }

    /** Verifies supported clock formats and normalization of repeated whitespace. */
    @Test
    void parse_supportedTimesAndRepeatedWhitespace_returnExpectedDateTimes() {
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0),
                DateTimeParser.parse("2/12/2019 1800").toLocalDateTime());
        assertEquals(LocalDateTime.of(2020, 2, 29, 9, 30),
                DateTimeParser.parse("2020/02/29 09:30").toLocalDateTime());
        assertEquals(LocalDateTime.of(2024, 1, 1, 0, 0),
                DateTimeParser.parse("2024-01-01 12am").toLocalDateTime());
        assertEquals(LocalDateTime.of(2024, 1, 1, 18, 0),
                DateTimeParser.parse("2024-01-01    6 PM").toLocalDateTime());
    }

    /** Verifies that a time-only value inherits the supplied default date. */
    @Test
    void parse_timeOnlyWithDefaultDate_usesDefaultDate() {
        DateTimeParser.ParsedDateTime parsed =
                DateTimeParser.parse("10:30", LocalDate.of(2019, 12, 3));

        assertEquals(LocalDateTime.of(2019, 12, 3, 10, 30), parsed.toLocalDateTime());
    }

    /** Verifies that invalid calendar and clock values are rejected consistently. */
    @Test
    void parse_invalidDatesAndTimes_throwDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> DateTimeParser.parse("31/02/2019"));
        assertThrows(DateTimeParseException.class, () -> DateTimeParser.parse("2019-10-15 2460"));
        assertThrows(DateTimeParseException.class, () -> DateTimeParser.parse("tomorrow"));
    }

    /** Verifies stable human-readable and persistence date/time formats. */
    @Test
    void format_displayAndStorageFormats_areStable() {
        DateTimeParser.ParsedDateTime dateOnly = DateTimeParser.parse("2019-10-15");
        DateTimeParser.ParsedDateTime dateTime = DateTimeParser.parse("2019-12-02 1800");

        assertEquals("Oct 15 2019", DateTimeParser.format(dateOnly));
        assertEquals("Dec 02 2019, 6:00 PM", DateTimeParser.format(dateTime));
        assertEquals("2019-10-15", DateTimeParser.formatForStorage(dateOnly));
        assertEquals("2019-12-02T18:00", DateTimeParser.formatForStorage(dateTime));
    }
    @Test
    void parse_slashDateAmbiguities_followsDocumentedPrecedence() {
        assertEquals(LocalDate.of(2026, 12, 2), DateTimeParser.parse("2/12/2026").date());
        assertEquals(LocalDate.of(2026, 2, 12), DateTimeParser.parse("02/12/2026").date());
        assertEquals(LocalDate.of(2026, 2, 13), DateTimeParser.parse("13/02/2026").date());
        assertEquals(LocalDate.of(2026, 12, 31), DateTimeParser.parse("12/31/2026").date());
        assertEquals(LocalDate.of(2026, 1, 31), DateTimeParser.parse("1/31/2026").date());
        assertEquals(LocalDate.of(2000, 2, 29), DateTimeParser.parse("2000-02-29").date());
    }

    @Test
    void parse_everyClockSyntax_preservesMidnightNoonAndMinuteBoundaries() {
        LocalDate date = LocalDate.of(2026, 9, 11);
        for (String time : List.of("1830", "18:30", "6:30pm", "6:30 PM", "6:30 pM")) {
            assertEquals(date.atTime(18, 30), DateTimeParser.parse(time, date).toLocalDateTime(), time);
            assertEquals(date.atTime(18, 30), DateTimeParser.parse("2026-09-11 " + time).toLocalDateTime());
        }
        assertEquals(date.atStartOfDay(), DateTimeParser.parse("12 AM", date).toLocalDateTime());
        assertEquals(date.atTime(12, 0), DateTimeParser.parse("12pm", date).toLocalDateTime());
        assertEquals(date.atTime(23, 59), DateTimeParser.parse("2359", date).toLocalDateTime());
        assertEquals(LocalDate.of(2027, 1, 1), DateTimeParser.parse("2027-01-01", date).date());
    }

    @Test
    void parse_invalidCalendarAndClockBoundaries_rejectsInsteadOfRounding() {
        for (String text : List.of("1900-02-29", "2026-02-29", "2026-04-31", "2026-00-10",
                "2026-13-01", "2026-01-00", "02/30/2026", "2026-09-11 24:00", "2026-09-11 1260",
                "2026-09-11 0am", "2026-09-11 13pm", "2026-09-11 12:60 PM", "", " ", "18:30")) {
            assertThrows(DateTimeParseException.class, () -> DateTimeParser.parse(text), text);
        }
        assertThrows(DateTimeParseException.class, () ->
                DateTimeParser.parse("tomorrow", LocalDate.of(2026, 9, 11)));
    }

    @Test
    void formatForStorage_isoPrecision_roundTripsDateTimeWithoutLosingSeconds() {
        for (String text : List.of("2026-09-11", "2026-09-11T00:00", "2026-09-11T18:30:45",
                "2026-09-11T18:30:45.123456789")) {
            DateTimeParser.ParsedDateTime value = DateTimeParser.parse(text);
            assertEquals(value, DateTimeParser.parse(DateTimeParser.formatForStorage(value)), text);
        }
    }

    @Test
    void format_nonEnglishDefaultLocale_keepsEnglishDisplayAndParsing() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.FRANCE);
            assertEquals("Sep 11 2026, 6:30 PM", DateTimeParser.format(DateTimeParser.parse("2026-09-11 6:30 PM")));
            assertEquals("Sep 11 2026", DateTimeParser.format(LocalDate.of(2026, 9, 11)));
        } finally {
            Locale.setDefault(original);
        }
    }

    @Test
    void parsedDateTime_optionalTime_preservesPrecisionAndRequiresNonNullComponents() {
        LocalDate date = LocalDate.of(2026, 9, 11);
        DateTimeParser.ParsedDateTime dateOnly = new DateTimeParser.ParsedDateTime(date);
        assertEquals(Optional.empty(), dateOnly.time());
        assertEquals(date.atStartOfDay(), dateOnly.toLocalDateTime());
        DateTimeParser.ParsedDateTime timed = new DateTimeParser.ParsedDateTime(
                date, Optional.of(LocalTime.of(10, 30)));
        assertEquals(date.atTime(10, 30), timed.toLocalDateTime());
        assertThrows(NullPointerException.class, () -> new DateTimeParser.ParsedDateTime(null, Optional.empty()));
        assertThrows(NullPointerException.class, () -> new DateTimeParser.ParsedDateTime(date, null));
    }

}
