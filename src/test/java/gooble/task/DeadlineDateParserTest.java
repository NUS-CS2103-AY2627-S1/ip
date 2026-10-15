package gooble.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import gooble.GoobleException;

/** Tests the supported deadline date and time input formats. */
class DeadlineDateParserTest {
    @Test
    void parse_isoDateWithoutTime_returnsDateAndNoTime() throws GoobleException {
        DeadlineDateParser.DeadlineDate deadline = DeadlineDateParser.parse("2026-02-01");

        assertEquals(LocalDate.of(2026, 2, 1), deadline.date());
        assertNull(deadline.time());
    }

    @Test
    void parse_slashDateWithTime_returnsDateAndTime() throws GoobleException {
        DeadlineDateParser.DeadlineDate deadline = DeadlineDateParser.parse("2/12/2019 1800");

        assertEquals(LocalDate.of(2019, 12, 2), deadline.date());
        assertEquals(LocalTime.of(18, 0), deadline.time());
    }

    @Test
    void parse_inputWithSurroundingWhitespace_trimsInput() throws GoobleException {
        DeadlineDateParser.DeadlineDate deadline = DeadlineDateParser.parse("  2026-02-01  0905  ");

        assertEquals(LocalDate.of(2026, 2, 1), deadline.date());
        assertEquals(LocalTime.of(9, 5), deadline.time());
    }

    @Test
    void parse_invalidCalendarDate_exceptionThrown() {
        assertThrows(GoobleException.class, () -> DeadlineDateParser.parse("2025-02-29"));
    }

    @Test
    void parse_invalidTime_exceptionThrown() {
        assertThrows(GoobleException.class, () -> DeadlineDateParser.parse("2026-02-01 2460"));
    }

    @Test
    void parse_nullBlankOrTooManyParts_exceptionThrown() {
        assertThrows(GoobleException.class, () -> DeadlineDateParser.parse(null));
        assertThrows(GoobleException.class, () -> DeadlineDateParser.parse("   "));
        assertThrows(GoobleException.class, () -> DeadlineDateParser.parse("2026-02-01 0900 extra"));
    }

    @Test
    void format_dateWithoutTime_returnsDisplayDate() {
        DeadlineDateParser.DeadlineDate deadline = new DeadlineDateParser.DeadlineDate(
                LocalDate.of(2026, 2, 1), null);

        assertEquals("Feb 01 2026", DeadlineDateParser.format(deadline));
    }

    @Test
    void format_dateWithTime_returnsDisplayDateAndTime() {
        DeadlineDateParser.DeadlineDate deadline = new DeadlineDateParser.DeadlineDate(
                LocalDate.of(2019, 12, 2), LocalTime.of(18, 0));

        assertEquals("Dec 02 2019, 6:00 PM", DeadlineDateParser.format(deadline));
    }

    @Test
    void isValentinesDay_valentinesDate_returnsTrue() {
        DeadlineDateParser.DeadlineDate deadline = new DeadlineDateParser.DeadlineDate(
                LocalDate.of(2026, 2, 14), null);

        assertTrue(DeadlineDateParser.isValentinesDay(deadline));
    }

    @Test
    void isValentinesDay_nonValentinesDate_returnsFalse() {
        DeadlineDateParser.DeadlineDate deadline = new DeadlineDateParser.DeadlineDate(
                LocalDate.of(2026, 2, 13), null);

        assertFalse(DeadlineDateParser.isValentinesDay(deadline));
    }

    @Test
    void isChineseNewYear_knownDate_returnsTrue() {
        DeadlineDateParser.DeadlineDate deadline = new DeadlineDateParser.DeadlineDate(
                LocalDate.of(2026, 2, 17), null);

        assertTrue(DeadlineDateParser.isChineseNewYear(deadline));
    }

    @Test
    void isChineseNewYear_wrongDateOrUnsupportedYear_returnsFalse() {
        DeadlineDateParser.DeadlineDate wrongDate = new DeadlineDateParser.DeadlineDate(
                LocalDate.of(2026, 2, 16), null);
        DeadlineDateParser.DeadlineDate unsupportedYear = new DeadlineDateParser.DeadlineDate(
                LocalDate.of(2036, 2, 19), null);

        assertFalse(DeadlineDateParser.isChineseNewYear(wrongDate));
        assertFalse(DeadlineDateParser.isChineseNewYear(unsupportedYear));
    }
}
