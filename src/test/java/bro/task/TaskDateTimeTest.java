package bro.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import bro.exception.BroException;

/**
 * Unit tests for {@link TaskDateTime}.
 */
public class TaskDateTimeTest {

    // -------------------------------------------------------------------------
    // Constructor tests
    // -------------------------------------------------------------------------

    @Test
    public void constructor_variousInputs_fieldsInitializedCorrectly() {
        LocalDateTime dt = LocalDateTime.of(2026, 8, 28, 18, 0);

        TaskDateTime dateTimeWithTime = new TaskDateTime(dt, true);
        assertEquals(dt, dateTimeWithTime.getDateTime());
        assertTrue(dateTimeWithTime.hasTime());
        assertTrue(dateTimeWithTime.isParsedDateTime());
        assertNull(dateTimeWithTime.getRawString());

        TaskDateTime dateOnly = new TaskDateTime(dt, false);
        assertEquals(dt, dateOnly.getDateTime());
        assertFalse(dateOnly.hasTime());
        assertTrue(dateOnly.isParsedDateTime());

        TaskDateTime rawOnly = new TaskDateTime("2pm");
        assertNull(rawOnly.getDateTime());
        assertFalse(rawOnly.hasTime());
        assertFalse(rawOnly.isParsedDateTime());
        assertEquals("2pm", rawOnly.getRawString());
    }

    // -------------------------------------------------------------------------
    // parse tests: Date-Time formats
    // -------------------------------------------------------------------------

    @Test
    public void parse_dateTimeFormats_success() throws BroException {
        LocalDateTime expected = LocalDateTime.of(2026, 8, 28, 18, 0);

        // "d/M/yyyy HHmm"
        TaskDateTime dt1 = TaskDateTime.parse("28/8/2026 1800");
        assertTrue(dt1.isParsedDateTime());
        assertTrue(dt1.hasTime());
        assertEquals(expected, dt1.getDateTime());

        // "yyyy-MM-dd HHmm"
        TaskDateTime dt2 = TaskDateTime.parse("2026-08-28 1800");
        assertEquals(expected, dt2.getDateTime());

        // "d-M-yyyy HHmm"
        TaskDateTime dt3 = TaskDateTime.parse("28-8-2026 1800");
        assertEquals(expected, dt3.getDateTime());

        // "d/M/yyyy HH:mm"
        TaskDateTime dt4 = TaskDateTime.parse("28/8/2026 18:00");
        assertEquals(expected, dt4.getDateTime());

        // "yyyy-MM-dd HH:mm"
        TaskDateTime dt5 = TaskDateTime.parse("2026-08-28 18:00");
        assertEquals(expected, dt5.getDateTime());

        // "d-M-yyyy HH:mm"
        TaskDateTime dt6 = TaskDateTime.parse("28-8-2026 18:00");
        assertEquals(expected, dt6.getDateTime());

        // "yyyy-MM-dd'T'HH:mm"
        TaskDateTime dt7 = TaskDateTime.parse("2026-08-28T18:00");
        assertEquals(expected, dt7.getDateTime());
    }

    // -------------------------------------------------------------------------
    // parse tests: Date-Only formats
    // -------------------------------------------------------------------------

    @Test
    public void parse_dateOnlyFormats_success() throws BroException {
        LocalDate expectedDate = LocalDate.of(2026, 8, 28);
        LocalDateTime expectedDt = expectedDate.atStartOfDay();

        // "yyyy-MM-dd"
        TaskDateTime dt1 = TaskDateTime.parse("2026-08-28");
        assertTrue(dt1.isParsedDateTime());
        assertFalse(dt1.hasTime());
        assertEquals(expectedDt, dt1.getDateTime());
        assertEquals(expectedDate, dt1.toLocalDate());

        // "d/M/yyyy"
        TaskDateTime dt2 = TaskDateTime.parse("28/8/2026");
        assertEquals(expectedDt, dt2.getDateTime());

        // "d-M-yyyy"
        TaskDateTime dt3 = TaskDateTime.parse("28-8-2026");
        assertEquals(expectedDt, dt3.getDateTime());

        // "yyyy/M/d"
        TaskDateTime dt4 = TaskDateTime.parse("2026/8/28");
        assertEquals(expectedDt, dt4.getDateTime());
    }

    // -------------------------------------------------------------------------
    // parse tests: Raw String fallback
    // -------------------------------------------------------------------------

    @Test
    public void parse_unparseableRawString_rawStringStored() throws BroException {
        TaskDateTime dt = TaskDateTime.parse("tomorrow 2pm");
        assertFalse(dt.isParsedDateTime());
        assertFalse(dt.hasTime());
        assertNull(dt.getDateTime());
        assertNull(dt.toLocalDate());
        assertEquals("tomorrow 2pm", dt.getRawString());
    }

    @Test
    public void parse_nullOrEmptyInput_exceptionThrown() {
        assertThrows(BroException.class, () -> TaskDateTime.parse(null));
        assertThrows(BroException.class, () -> TaskDateTime.parse(""));
        assertThrows(BroException.class, () -> TaskDateTime.parse("   "));
    }

    // -------------------------------------------------------------------------
    // formatDisplay and formatFile tests
    // -------------------------------------------------------------------------

    @Test
    public void formatDisplayAndFile_withTime_formattedCorrectly() throws BroException {
        TaskDateTime dt = TaskDateTime.parse("28/8/2026 1800");
        assertEquals("Aug 28 2026, 6:00PM", dt.formatDisplay());
        assertEquals("2026-08-28 1800", dt.formatFile());
    }

    @Test
    public void formatDisplayAndFile_dateOnly_formattedCorrectly() throws BroException {
        TaskDateTime dt = TaskDateTime.parse("2026-08-28");
        assertEquals("Aug 28 2026", dt.formatDisplay());
        assertEquals("2026-08-28", dt.formatFile());
    }

    @Test
    public void formatDisplayAndFile_rawFallback_formattedCorrectly() throws BroException {
        TaskDateTime dt = TaskDateTime.parse("Sunday evening");
        assertEquals("Sunday evening", dt.formatDisplay());
        assertEquals("Sunday evening", dt.formatFile());
    }

    // -------------------------------------------------------------------------
    // isOnDate tests
    // -------------------------------------------------------------------------

    @Test
    public void isOnDate_parsedDateTime_matchesCorrectly() throws BroException {
        TaskDateTime dt = TaskDateTime.parse("28/8/2026 1800");
        LocalDate matchingDate = LocalDate.of(2026, 8, 28);
        LocalDate differentDate = LocalDate.of(2026, 8, 29);

        assertTrue(dt.isOnDate(matchingDate));
        assertFalse(dt.isOnDate(differentDate));
        assertFalse(dt.isOnDate(null));
    }

    @Test
    public void isOnDate_rawString_falseReturned() throws BroException {
        TaskDateTime dt = TaskDateTime.parse("tomorrow");
        LocalDate someDate = LocalDate.of(2026, 8, 28);

        assertFalse(dt.isOnDate(someDate));
        assertFalse(dt.isOnDate(null));
    }

    // -------------------------------------------------------------------------
    // parseQueryDate tests
    // -------------------------------------------------------------------------

    @Test
    public void parseQueryDate_dateOnlyFormats_localDateReturned() throws BroException {
        LocalDate expected = LocalDate.of(2026, 8, 28);

        assertEquals(expected, TaskDateTime.parseQueryDate("2026-08-28"));
        assertEquals(expected, TaskDateTime.parseQueryDate("28/8/2026"));
        assertEquals(expected, TaskDateTime.parseQueryDate("28-8-2026"));
        assertEquals(expected, TaskDateTime.parseQueryDate("2026/8/28"));
    }

    @Test
    public void parseQueryDate_withTimeFormats_localDateReturned() throws BroException {
        LocalDate expected = LocalDate.of(2026, 8, 28);

        assertEquals(expected, TaskDateTime.parseQueryDate("28/8/2026 1800"));
        assertEquals(expected, TaskDateTime.parseQueryDate("2026-08-28 1800"));
        assertEquals(expected, TaskDateTime.parseQueryDate("28-8-2026 18:00"));
        assertEquals(expected, TaskDateTime.parseQueryDate("2026-08-28T18:00"));
    }

    @Test
    public void parseQueryDate_nullOrEmptyInput_exceptionThrown() {
        BroException e1 = assertThrows(BroException.class, () -> TaskDateTime.parseQueryDate(null));
        assertTrue(e1.getMessage().contains("please tell me which date you want to check"));

        BroException e2 = assertThrows(BroException.class, () -> TaskDateTime.parseQueryDate(""));
        assertTrue(e2.getMessage().contains("please tell me which date you want to check"));

        BroException e3 = assertThrows(BroException.class, () -> TaskDateTime.parseQueryDate("   "));
        assertTrue(e3.getMessage().contains("please tell me which date you want to check"));
    }

    @Test
    public void parseQueryDate_invalidFormat_exceptionThrown() {
        BroException e = assertThrows(BroException.class, () -> TaskDateTime.parseQueryDate("invalid date"));
        assertTrue(e.getMessage().contains("please use a valid date format"));
    }

    // -------------------------------------------------------------------------
    // Impossible calendar date rejection tests
    // -------------------------------------------------------------------------

    @Test
    public void parse_calendarDateLikeImpossibleDate_exceptionThrown() {
        BroException e1 = assertThrows(BroException.class, () -> TaskDateTime.parse("2026-02-30"));
        assertTrue(e1.getMessage().contains("isn't a real date on the calendar"));

        BroException e2 = assertThrows(BroException.class, () -> TaskDateTime.parse("2026-02-30 1800"));
        assertTrue(e2.getMessage().contains("isn't a real date on the calendar"));

        BroException e3 = assertThrows(BroException.class, () -> TaskDateTime.parse("31/4/2026"));
        assertTrue(e3.getMessage().contains("isn't a real date on the calendar"));

        BroException e4 = assertThrows(BroException.class, () -> TaskDateTime.parse("2026-13-01"));
        assertTrue(e4.getMessage().contains("isn't a real date on the calendar"));
    }

    // -------------------------------------------------------------------------
    // isBefore and isAfter tests
    // -------------------------------------------------------------------------

    @Test
    public void isBeforeAndIsAfter_comparisons_correctBooleanReturned() throws BroException {
        TaskDateTime dt1 = TaskDateTime.parse("2026-10-15 1000");
        TaskDateTime dt2 = TaskDateTime.parse("2026-10-15 1200");
        TaskDateTime raw = TaskDateTime.parse("someday");

        assertTrue(dt1.isBefore(dt2));
        assertTrue(dt2.isAfter(dt1));
        assertFalse(dt1.isAfter(dt2));
        assertFalse(dt2.isBefore(dt1));

        assertFalse(dt1.isBefore(raw));
        assertFalse(raw.isBefore(dt1));
        assertFalse(dt1.isBefore(null));
    }

    // -------------------------------------------------------------------------
    // equals and hashCode tests
    // -------------------------------------------------------------------------

    @Test
    public void equalsAndHashCode_variousInputs_behaveCorrectly() throws BroException {
        TaskDateTime dt1 = TaskDateTime.parse("2026-10-15 1000");
        TaskDateTime dt2 = TaskDateTime.parse("2026-10-15 10:00");
        TaskDateTime dt3 = TaskDateTime.parse("2026-10-15 1200");
        TaskDateTime raw1 = TaskDateTime.parse("later");
        TaskDateTime raw2 = TaskDateTime.parse("LATER");

        assertEquals(dt1, dt2);
        assertEquals(dt1.hashCode(), dt2.hashCode());
        assertFalse(dt1.equals(dt3));
        assertEquals(raw1, raw2);
        assertEquals(raw1.hashCode(), raw2.hashCode());
        assertFalse(dt1.equals(raw1));
        assertFalse(dt1.equals(null));
        assertFalse(dt1.equals("string"));
    }
}
