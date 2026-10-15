package kpei.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import kpei.exceptions.BertException;

/**
 * Tests the date and time parsing capabilities of {@link DateTimeParser}.
 */
class DateTimeParserTest {

    @Test
    void parseDate_supportedFormats_success() throws BertException {
        LocalDate expectedParsedDate = LocalDate.of(2026, 8, 29);
        assertEquals(expectedParsedDate, DateTimeParser.parseDate("2026-08-29"));
        assertEquals(expectedParsedDate, DateTimeParser.parseDate("2026/8/29"));
        assertEquals(expectedParsedDate, DateTimeParser.parseDate("2026.8.29"));
        assertEquals(expectedParsedDate, DateTimeParser.parseDate("29/8/2026"));
        assertEquals(expectedParsedDate, DateTimeParser.parseDate("29-8-2026"));
        assertEquals(expectedParsedDate, DateTimeParser.parseDate("29.8.2026"));
        assertEquals(expectedParsedDate, DateTimeParser.parseDate("29 Aug 2026"));
        assertEquals(expectedParsedDate, DateTimeParser.parseDate("29 August 2026"));
        assertEquals(expectedParsedDate, DateTimeParser.parseDate("Aug 29 2026"));
        assertEquals(expectedParsedDate, DateTimeParser.parseDate("  29 Aug 2026  "));
    }

    @Test
    void parseTime_supportedFormats_success() throws BertException {
        LocalTime expectedParsedTime = LocalTime.of(16, 0);
        assertEquals(expectedParsedTime, DateTimeParser.parseTime("16:00"));
        assertEquals(expectedParsedTime, DateTimeParser.parseTime("1600"));
        assertEquals(expectedParsedTime, DateTimeParser.parseTime("16:00:00"));
        assertEquals(expectedParsedTime, DateTimeParser.parseTime("04:00 PM"));
        assertEquals(expectedParsedTime, DateTimeParser.parseTime("4:00pm"));
        assertEquals(expectedParsedTime, DateTimeParser.parseTime("16.00"));
        assertEquals(expectedParsedTime, DateTimeParser.parseTime("  16:00  "));
    }

    @Test
    void parseDate_nullOrBlankInput_exceptionThrown() {
        assertThrows(BertException.class, () -> DateTimeParser.parseDate(null));
        assertThrows(BertException.class, () -> DateTimeParser.parseDate(""));
        assertThrows(BertException.class, () -> DateTimeParser.parseDate("   "));
    }

    @Test
    void parseTime_nullOrBlankInput_exceptionThrown() {
        assertThrows(BertException.class, () -> DateTimeParser.parseTime(null));
        assertThrows(BertException.class, () -> DateTimeParser.parseTime(""));
        assertThrows(BertException.class, () -> DateTimeParser.parseTime("   "));
    }

    @Test
    void parseDate_invalidInput_exceptionThrown() {
        assertThrows(BertException.class, () -> DateTimeParser.parseDate("29/08/26"));
        assertThrows(BertException.class, () -> DateTimeParser.parseDate("2026-99-99"));
        assertThrows(BertException.class, () -> DateTimeParser.parseDate("2026-02-29"));
        assertThrows(BertException.class, () -> DateTimeParser.parseDate("2026-00-10"));
    }

    @Test
    void parseTime_invalidInput_exceptionThrown() {
        assertThrows(BertException.class, () -> DateTimeParser.parseTime("25:00"));
        assertThrows(BertException.class, () -> DateTimeParser.parseTime("16:60"));
        assertThrows(BertException.class, () -> DateTimeParser.parseTime("4 pm"));
    }

    @Test
    void parseDate_leapDay_validDate_success() throws BertException {
        LocalDate expectedParsedDate = LocalDate.of(2024, 2, 29);

        assertEquals(expectedParsedDate, DateTimeParser.parseDate("2024-02-29"));
    }

    @Test
    void parseTime_midnightAndNoon_validTimes_success() throws BertException {
        assertEquals(LocalTime.MIDNIGHT, DateTimeParser.parseTime("00:00"));
        assertEquals(LocalTime.NOON, DateTimeParser.parseTime("12:00 PM"));
    }

    @Test
    void formatDateAndTime_validValues_expectedDisplayFormatReturned() {
        String expectedFormatDate = "29 Aug 2026";
        String expectedFormatTime = "4:00 pm";
        assertEquals(expectedFormatDate, DateTimeParser.formatDate(LocalDate.of(2026, 8, 29)));
        assertEquals(expectedFormatTime, DateTimeParser.formatTime(LocalTime.of(16, 0)));
    }

    @Test
    void formatDateAndTime_singleDigitAndBoundaryValues_expectedDisplayFormatReturned() {
        assertEquals("05 Jan 2026",
                DateTimeParser.formatDate(LocalDate.of(2026, 1, 5)));
        assertEquals("12:00 am", DateTimeParser.formatTime(LocalTime.MIDNIGHT));
        assertEquals("12:00 pm", DateTimeParser.formatTime(LocalTime.NOON));
    }
}
