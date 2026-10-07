package drax;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests supported schedule formats, strict validation, and user-facing date formatting. */
public class ScheduleDateTimeTest {
    private static final String INVALID_DATE_TIME_MESSAGE = "Please use a valid date and time: "
            + "yyyy-MM-dd, yyyy-MM-ddTHH:mm, or d/M/yyyy HHmm!";

    @Test
    public void parse_isoDate_returnsDateAtMidnight() {
        LocalDateTime expected = LocalDateTime.of(2026, 12, 2, 0, 0);
        LocalDateTime actual = ScheduleDateTime.parse("2026-12-02");
        assertEquals(expected, actual);
    }

    @Test
    public void parse_isoLeapDay_returnsDateAtMidnight() {
        LocalDateTime expected = LocalDateTime.of(2024, 2, 29, 0, 0);

        assertEquals(expected, ScheduleDateTime.parse("2024-02-29"));
    }

    @Test
    public void parse_isoDateTime_returnsDateAndTime() {
        LocalDateTime expected = LocalDateTime.of(2026, 12, 2, 18, 30);
        LocalDateTime actual = ScheduleDateTime.parse("2026-12-02T18:30");
        assertEquals(expected, actual);
    }

    @Test
    public void parse_isoDateTimeWithSeconds_preservesSeconds() {
        LocalDateTime expected = LocalDateTime.of(2026, 12, 2, 18, 30, 45);

        assertEquals(expected, ScheduleDateTime.parse("2026-12-02T18:30:45"));
    }

    @Test
    public void parse_commandDateAndTime_returnsDateAndTime() {
        LocalDateTime expected = LocalDateTime.of(2026, 12, 2, 18, 30);
        LocalDateTime actual = ScheduleDateTime.parse("02/12/2026 1830");
        assertEquals(expected, actual);
    }

    @Test
    public void parse_commandDateWithSingleDigitDayAndMonth_returnsDateAndTime() {
        LocalDateTime expected = LocalDateTime.of(2026, 3, 2, 7, 5);

        assertEquals(expected, ScheduleDateTime.parse("2/3/2026 0705"));
    }

    @Test
    public void parse_commandLeapDay_returnsDateAndTime() {
        LocalDateTime expected = LocalDateTime.of(2024, 2, 29, 23, 59);

        assertEquals(expected, ScheduleDateTime.parse("29/2/2024 2359"));
    }

    @Test
    public void parse_nullInput_throwsExceptionWithGuidance() {
        assertInvalidDateTime(null);
    }

    @Test
    public void parse_emptyInput_throwsExceptionWithGuidance() {
        assertInvalidDateTime("");
    }

    @Test
    public void parse_whitespaceInput_throwsExceptionWithGuidance() {
        assertInvalidDateTime("   ");
    }

    @Test
    public void parse_invalidDateFormat_exceptionThrown() {
        assertInvalidDateTime("2026-12-02 1830");
    }

    @Test
    public void parse_invalidDate_exceptionThrown() {
        assertInvalidDateTime("31/02/2026 1830");
    }

    @Test
    public void parse_nonLeapYearFebruaryTwentyNine_throwsExceptionWithGuidance() {
        assertInvalidDateTime("29/2/2026 1830");
    }

    @Test
    public void parse_invalidIsoDate_throwsExceptionWithGuidance() {
        assertInvalidDateTime("2026-02-29");
    }

    @Test
    public void parse_invalidIsoDateTime_throwsExceptionWithGuidance() {
        assertInvalidDateTime("2026-12-02T24:00");
    }

    @Test
    public void parse_commandDateWithInvalidHour_throwsExceptionWithGuidance() {
        assertInvalidDateTime("2/12/2026 2400");
    }

    @Test
    public void parse_commandDateWithInvalidMinute_throwsExceptionWithGuidance() {
        assertInvalidDateTime("2/12/2026 1860");
    }

    @Test
    public void parse_isoDateWithoutRequiredPadding_throwsExceptionWithGuidance() {
        assertInvalidDateTime("2026-2-2");
    }

    @Test
    public void parse_isoDateTimeWithOffset_throwsExceptionWithGuidance() {
        assertInvalidDateTime("2026-12-02T18:30Z");
    }

    @Test
    public void format_midnight_returnsDateOnly() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 12, 2, 0, 0);
        String actual = ScheduleDateTime.formatForDisplay(dateTime);
        assertEquals("Dec 02 2026", actual);
    }

    @Test
    public void format_nonMidnight_returnsDateAndTime() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 12, 2, 18, 30);
        String actual = ScheduleDateTime.formatForDisplay(dateTime);
        assertEquals("Dec 02 2026 6:30 PM", actual);
    }

    @Test
    public void format_justAfterMidnight_returnsTwelveHourAmTime() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 12, 2, 0, 5);

        assertEquals("Dec 02 2026 12:05 AM", ScheduleDateTime.formatForDisplay(dateTime));
    }

    @Test
    public void format_noon_returnsTwelveHourPmTime() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 12, 2, 12, 0);

        assertEquals("Dec 02 2026 12:00 PM", ScheduleDateTime.formatForDisplay(dateTime));
    }

    @Test
    public void format_leapDayAtMidnight_returnsDateOnly() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 2, 29, 0, 0);

        assertEquals("Feb 29 2024", ScheduleDateTime.formatForDisplay(dateTime));
    }

    private static void assertInvalidDateTime(String input) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> ScheduleDateTime.parse(input));

        assertEquals(INVALID_DATE_TIME_MESSAGE, exception.getMessage());
    }

}
