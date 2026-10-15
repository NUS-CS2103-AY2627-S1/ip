package xiaozhi.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import xiaozhi.exception.XiaoZhiException;

public class DatesTest {

    @Test
    public void parse_validIsoDate_returnsLocalDate() throws XiaoZhiException {
        assertEquals(LocalDate.of(2019, 10, 15), Dates.parse("2019-10-15"));
    }

    @Test
    public void parse_dateWithSurroundingWhitespace_isTrimmedAndParsed() throws XiaoZhiException {
        assertEquals(LocalDate.of(2019, 10, 15), Dates.parse("  2019-10-15  "));
    }

    @Test
    public void parse_nonDateText_throwsXiaoZhiExceptionWithGuidance() {
        XiaoZhiException thrown = assertThrows(XiaoZhiException.class, () -> Dates.parse("tomorrow"));
        assertTrue(thrown.getMessage().contains("yyyy-mm-dd"));
        assertTrue(thrown.getMessage().contains("tomorrow"));
    }

    @Test
    public void parse_calendarDateThatDoesNotExist_throwsXiaoZhiException() {
        // 2019 is not a leap year, so February only has 28 days.
        assertThrows(XiaoZhiException.class, () -> Dates.parse("2019-02-30"));
    }

    @Test
    public void parse_feb29InANonLeapYear_throwsXiaoZhiException() {
        assertThrows(XiaoZhiException.class, () -> Dates.parse("2019-02-29"));
    }

    @Test
    public void parse_feb29InALeapYear_isAccepted() throws XiaoZhiException {
        assertEquals(LocalDate.of(2020, 2, 29), Dates.parse("2020-02-29"));
    }

    @Test
    public void parse_monthThirteen_throwsXiaoZhiException() {
        assertThrows(XiaoZhiException.class, () -> Dates.parse("2019-13-01"));
    }

    @Test
    public void parse_dayZero_throwsXiaoZhiException() {
        assertThrows(XiaoZhiException.class, () -> Dates.parse("2019-10-00"));
    }

    @Test
    public void parse_aprilThirtyFirst_throwsXiaoZhiException() {
        // April only has 30 days.
        assertThrows(XiaoZhiException.class, () -> Dates.parse("2019-04-31"));
    }

    @Test
    public void parse_wrongSeparators_throwsXiaoZhiException() {
        assertThrows(XiaoZhiException.class, () -> Dates.parse("15/10/2019"));
    }

    @Test
    public void format_validDate_returnsFriendlyDisplayFormat() {
        assertEquals("Oct 15 2019", Dates.format(LocalDate.of(2019, 10, 15)));
    }

    @Test
    public void format_singleDigitDay_padsWithLeadingZero() {
        assertEquals("Jan 05 2020", Dates.format(LocalDate.of(2020, 1, 5)));
    }
}
