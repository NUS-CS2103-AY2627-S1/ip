package arthur.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import arthur.exceptions.InvalidDateTimeException;

public class UtilsTest {
    // AI-assisted: Split date and datetime parsing assertions into scenario-specific tests.
    @Test
    public void parseDateTime_validInput_returnsParsedDateTime() {
        assertEquals(LocalDateTime.of(2026, 9, 15, 14, 30), Utils.parseDateTime("2026-09-15 1430"));
    }

    // AI-assisted: Separate invalid datetime scenarios for clear failure reporting.
    @Test
    public void parseDateTime_invalidHour_throwsInvalidDateTimeException() {
        assertThrows(InvalidDateTimeException.class, () -> Utils.parseDateTime("2026-09-15 2500"));
    }

    @Test
    public void parseDateTime_monthDaySwapped_throwsInvalidDateTimeException() {
        assertThrows(InvalidDateTimeException.class, () -> Utils.parseDateTime("2026-15-09 1430"));
    }

    @Test
    public void parseDateTime_midnightOnJanFirst_returnsStartOfDay() {
        assertEquals(LocalDateTime.of(2026, 1, 1, 0, 0), Utils.parseDateTime("2026-01-01 0000"));
    }

    @Test
    public void parseDateTime_validLeapDay_returnsParsedDateTime() {
        assertEquals(LocalDateTime.of(2024, 2, 29, 12, 0), Utils.parseDateTime("2024-02-29 1200"));
    }

    @Test
    public void parseDateTime_invalidLeapDay_throwsInvalidDateTimeException() {
        assertThrows(InvalidDateTimeException.class, () -> Utils.parseDateTime("2026-02-29 1200"));
    }

    @Test
    public void parseDateTime_invalidMonthDay_throwsInvalidDateTimeException() {
        assertThrows(InvalidDateTimeException.class, () -> Utils.parseDateTime("2026-04-31 1200"));
    }

    @Test
    public void parseDateTime_trailingCharacter_throwsInvalidDateTimeException() {
        assertThrows(InvalidDateTimeException.class, () -> Utils.parseDateTime("2026-09-15 1430a"));
    }

    // AI-assisted: Verify the date-time display formatter used by dated tasks.
    @Test
    public void formatDateTime_validInput_returnsDisplayFormat() {
        assertEquals("Sep 15 2026 @ 14:30", Utils.formatDateTime(LocalDateTime.of(2026, 9, 15, 14, 30)));
    }

    // AI-assisted: Separate date parsing scenarios for clear failure reporting.
    @Test
    public void parseDate_validLeapDay_returnsParsedDate() {
        assertEquals(LocalDate.of(2024, 2, 29), Utils.parseDate("2024-02-29"));
    }

    @Test
    public void parseDate_datetimeInput_throwsInvalidDateTimeException() {
        assertThrows(InvalidDateTimeException.class, () -> Utils.parseDate("2026-09-15 1430"));
    }

    @Test
    public void parseDate_incompleteInput_throwsInvalidDateTimeException() {
        assertThrows(InvalidDateTimeException.class, () -> Utils.parseDate("2026-09-"));
    }

    @Test
    public void parseDate_monthDaySwapped_throwsInvalidDateTimeException() {
        assertThrows(InvalidDateTimeException.class, () -> Utils.parseDate("2026-15-09"));
    }

    @Test
    public void parseDate_janFirst_returnsParsedDate() {
        assertEquals(LocalDate.of(2026, 1, 1), Utils.parseDate("2026-01-01"));
    }

    @Test
    public void parseDate_invalidLeapDay_throwsInvalidDateTimeException() {
        assertThrows(InvalidDateTimeException.class, () -> Utils.parseDate("2026-02-29"));
    }

    @Test
    public void parseDate_invalidMonthDay_throwsInvalidDateTimeException() {
        assertThrows(InvalidDateTimeException.class, () -> Utils.parseDate("2026-04-31"));
    }

    @Test
    public void parseDate_trailingCharacter_throwsInvalidDateTimeException() {
        assertThrows(InvalidDateTimeException.class, () -> Utils.parseDate("2026-09-15a"));
    }
}
