package bany.parsers;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests validation of Bany's supported date-time format. */
class DateTimeParserTest {

    @Test
    void isValidDateTimeString_validDateTime_returnsTrue() {
        assertTrue(DateTimeParser.isValidDateTimeString("21-01-2026 16:00"));
    }

    @Test
    void isValidDateTimeString_leapDay_returnsTrueOnlyForLeapYear() {
        assertAll(() -> assertTrue(DateTimeParser.isValidDateTimeString(
                        "29-02-2024 12:30")), () -> assertFalse(DateTimeParser.isValidDateTimeString(
                        "29-02-2023 12:30")), () -> assertFalse(DateTimeParser.isValidDateTimeString(
                        "29-02-1900 12:30")), () -> assertTrue(DateTimeParser.isValidDateTimeString(
                        "29-02-2000 12:30"))
        );
    }

    @Test
    void isValidDateTimeString_supportedYearBoundaries_returnsTrue() {
        assertAll(() -> assertTrue(DateTimeParser.isValidDateTimeString(
                        "01-01-0001 00:00")), () -> assertTrue(DateTimeParser.isValidDateTimeString(
                        "31-12-9999 23:59"))
        );
    }

    @Test
    void isValidDateTimeString_nullOrBlankInput_returnsFalse() {
        assertAll(() -> assertFalse(DateTimeParser.isValidDateTimeString(null)), () -> assertFalse(
                        DateTimeParser.isValidDateTimeString("")), () -> assertFalse(
                        DateTimeParser.isValidDateTimeString("   ")), () -> assertFalse(
                        DateTimeParser.isValidDateTimeString("\t"))
        );
    }

    @Test
    void isValidDateTimeString_invalidCalendarDate_returnsFalse() {
        assertAll(() -> assertFalse(DateTimeParser.isValidDateTimeString(
                        "31-04-2026 16:00")), () -> assertFalse(DateTimeParser.isValidDateTimeString(
                        "00-01-2026 16:00")), () -> assertFalse(DateTimeParser.isValidDateTimeString(
                        "01-00-2026 16:00")), () -> assertFalse(DateTimeParser.isValidDateTimeString(
                        "01-01-0000 16:00"))
        );
    }

    @Test
    void isValidDateTimeString_invalidFormatOrTime_returnsFalse() {
        assertAll(() -> assertFalse(DateTimeParser.isValidDateTimeString(
                        "21/01/2026 16:00")), () -> assertFalse(DateTimeParser.isValidDateTimeString(
                        "21-1-2026 16:00")), () -> assertFalse(DateTimeParser.isValidDateTimeString(
                        "21-01-2026T16:00")), () -> assertFalse(DateTimeParser.isValidDateTimeString(
                        "21-01-2026 16:00:00")), () -> assertFalse(DateTimeParser.isValidDateTimeString(
                        "21-01-2026 24:00")), () -> assertFalse(DateTimeParser.isValidDateTimeString(
                        "21-01-2026 16:60")), () -> assertFalse(DateTimeParser.isValidDateTimeString(
                        " 21-01-2026 16:00")), () -> assertFalse(DateTimeParser.isValidDateTimeString(
                        "21-01-2026 16:00 "))
        );
    }
}
