package todd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests the parsing behavior provided by {@link DateTimeUtil}. */
public class DateTimeUtilTest {
    @Test
    public void parseDateTime_dateOnly_returnsStartOfDay() throws TodException {
        LocalDateTime result = DateTimeUtil.parseDateTime("2026-09-04");

        assertEquals(LocalDateTime.of(2026, 9, 4, 0, 0), result);
    }

    @Test
    public void parseDateTime_dateAndTime_returnsCorrectDateTime() throws TodException {
        LocalDateTime result = DateTimeUtil.parseDateTime("2026-09-04 1430");

        assertEquals(LocalDateTime.of(2026, 9, 4, 14, 30), result);
    }

    @Test
    public void parseDateTime_weekdayAndTime_returnsNextOccurrence() throws TodException {
        LocalDateTime result = DateTimeUtil.parseDateTime(
                "Mon 1430", LocalDate.of(2026, 9, 11));

        assertEquals(LocalDateTime.of(2026, 9, 14, 14, 30), result);
    }

    @Test
    public void parseDateTime_sameWeekday_returnsFollowingWeek() throws TodException {
        LocalDateTime result = DateTimeUtil.parseDateTime(
                "Friday", LocalDate.of(2026, 9, 11));

        assertEquals(LocalDateTime.of(2026, 9, 18, 0, 0), result);
    }

    @Test
    public void parseDateTime_invalidDate_throwsTodException() {
        assertThrows(TodException.class, () ->
                DateTimeUtil.parseDateTime("2026-02-30"));
    }

    @Test
    public void parseDate_validDate_returnsLocalDate() throws TodException {
        LocalDate result = DateTimeUtil.parseDate("2026-09-04");

        assertEquals(LocalDate.of(2026, 9, 4), result);
    }

    @Test
    public void parseDate_todayAndTomorrow_returnRelativeDates() throws TodException {
        LocalDate referenceDate = LocalDate.of(2026, 9, 11);

        assertEquals(referenceDate, DateTimeUtil.parseDate("today", referenceDate));
        assertEquals(referenceDate.plusDays(1), DateTimeUtil.parseDate("tomorrow", referenceDate));
    }

    @Test
    public void parseDate_dateWithTime_throwsTodException() {
        assertThrows(TodException.class, () ->
                DateTimeUtil.parseDate("2026-09-04 1430"));
    }

    @Test
    public void parseDate_invalidDate_throwsTodException() {
        assertThrows(TodException.class, () ->
                DateTimeUtil.parseDate("2026-02-30"));
    }

    @Test
    public void format_dateTimeAtMidnight_omitsTime() {
        String result = DateTimeUtil.format(LocalDateTime.of(2026, 9, 4, 0, 0));

        assertEquals("Sep 04 2026", result);
    }

    @Test
    public void format_dateTimeWithTime_includesTime() {
        String result = DateTimeUtil.format(LocalDateTime.of(2026, 9, 4, 14, 30));

        assertEquals("Sep 04 2026, 2:30PM", result);
    }

    @Test
    public void format_date_returnsFormattedDate() {
        String result = DateTimeUtil.format(LocalDate.of(2026, 9, 4));

        assertEquals("Sep 04 2026", result);
    }
    @Test
    public void parseDateTime_invalidDateAndTime_giveDifferentGuidance() {
        TodException dateError = assertThrows(TodException.class, () ->
                DateTimeUtil.parseDateTime("2026-02-30 1430"));
        assertTrue(dateError.getMessage().contains("That date doesn't look right"));
        assertTrue(dateError.getMessage().contains("yyyy-MM-dd"));
        TodException timeError = assertThrows(TodException.class, () ->
                DateTimeUtil.parseDateTime("2026-09-17 2500"));
        assertTrue(timeError.getMessage().contains("That time doesn't look right"));
        assertTrue(timeError.getMessage().contains("0000 to 2359"));
    }
}
