package computah.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import computah.exception.ComputahException;

/**
 * Tests date/time parsing and formatting behavior in {@link DateTimeUtil}.
 */
public class DateTimeUtilTest {
    @Test
    public void parse_isoDate_returnsStartOfDay() throws ComputahException {
        assertEquals(LocalDateTime.of(2019, 10, 15, 0, 0), DateTimeUtil.parse("2019-10-15"));
    }

    @Test
    public void parse_isoDateTime_returnsDateTime() throws ComputahException {
        assertEquals(LocalDateTime.of(2019, 10, 15, 18, 0), DateTimeUtil.parse("2019-10-15 1800"));
    }

    @Test
    public void parse_slashDate_returnsStartOfDay() throws ComputahException {
        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0), DateTimeUtil.parse("2/12/2019"));
    }

    @Test
    public void parse_slashDateTime_returnsDateTime() throws ComputahException {
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), DateTimeUtil.parse("2/12/2019 1800"));
    }

    @Test
    public void parse_inputWithSurroundingWhitespace_returnsDateTime() throws ComputahException {
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), DateTimeUtil.parse("  2/12/2019 1800  "));
    }

    @Test
    public void parse_unsupportedFormat_exceptionThrown() {
        ComputahException exception = assertThrows(ComputahException.class, () -> DateTimeUtil.parse("Sunday"));

        assertEquals("Date/time must be in yyyy-MM-dd, yyyy-MM-dd HHmm, d/M/yyyy, or d/M/yyyy HHmm format.",
                exception.getMessage());
    }

    @Test
    public void parse_invalidDate_exceptionThrown() {
        assertThrows(ComputahException.class, () -> DateTimeUtil.parse("2019-02-30"));
    }

    @Test
    public void formatForDisplay_midnightDateTime_returnsDateOnlyDisplay() {
        LocalDateTime dateTime = LocalDateTime.of(2019, 10, 15, 0, 0);

        assertEquals("Oct 15 2019", DateTimeUtil.formatForDisplay(dateTime));
    }

    @Test
    public void formatForDisplay_nonMidnightDateTime_returnsDateTimeDisplay() {
        LocalDateTime dateTime = LocalDateTime.of(2019, 12, 2, 18, 0);

        assertEquals("Dec 2 2019 6:00 PM", DateTimeUtil.formatForDisplay(dateTime));
    }

    @Test
    public void formatForFile_midnightDateTime_returnsIsoDate() {
        LocalDateTime dateTime = LocalDateTime.of(2019, 10, 15, 0, 0);

        assertEquals("2019-10-15", DateTimeUtil.formatForFile(dateTime));
    }

    @Test
    public void formatForFile_nonMidnightDateTime_returnsIsoDateWithMilitaryTime() {
        LocalDateTime dateTime = LocalDateTime.of(2019, 12, 2, 18, 0);

        assertEquals("2019-12-02 1800", DateTimeUtil.formatForFile(dateTime));
    }
}
