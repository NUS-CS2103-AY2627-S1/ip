package answerme.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import answerme.exception.AnswerMeException;

public class DateTimeParserTest {
    private static final String INVALID_DATE_TIME_MESSAGE =
            "Ensure that date/time is formatted correctly.\n"
                    + "Use a date such as 11/9/2026 or a date and time such as 11/9/2026 1800.";

    @Test
    public void parseDateTime_supportedDateTimeFormats_returnsExpectedDateTime()
            throws AnswerMeException {
        LocalDateTime expectedDateTime = LocalDateTime.of(2026, 8, 31, 14, 0);

        assertEquals(expectedDateTime, DateTimeParser.parseDateTime("31/8/2026 1400"));
        assertEquals(expectedDateTime, DateTimeParser.parseDateTime("31/8/2026 14:00"));
        assertEquals(expectedDateTime, DateTimeParser.parseDateTime("31-8-2026 1400"));
        assertEquals(expectedDateTime, DateTimeParser.parseDateTime("31-8-2026 14:00"));
        assertEquals(expectedDateTime, DateTimeParser.parseDateTime("2026/8/31 14:00"));
        assertEquals(expectedDateTime, DateTimeParser.parseDateTime("2026-8-31 14:00"));
        assertEquals(expectedDateTime, DateTimeParser.parseDateTime("Aug 31 2026 14:00"));
    }

    @Test
    public void parseDateTime_supportedDateFormats_defaultsToMidnight()
            throws AnswerMeException {
        LocalDateTime expectedDateTime = LocalDateTime.of(2026, 8, 31, 0, 0);

        assertEquals(expectedDateTime, DateTimeParser.parseDateTime("31/8/2026"));
        assertEquals(expectedDateTime, DateTimeParser.parseDateTime("31-8-2026"));
        assertEquals(expectedDateTime, DateTimeParser.parseDateTime("2026/8/31"));
        assertEquals(expectedDateTime, DateTimeParser.parseDateTime("2026-8-31"));
        assertEquals(expectedDateTime, DateTimeParser.parseDateTime("Aug 31 2026"));
    }

    @Test
    public void parseDateTime_surroundingWhitespace_returnsTrimmedDateTime()
            throws AnswerMeException {
        LocalDateTime expectedDateTime = LocalDateTime.of(2026, 8, 31, 14, 0);

        assertEquals(expectedDateTime,
                DateTimeParser.parseDateTime("  31/8/2026 14:00  "));
    }

    @Test
    public void parseDateTime_mixedCaseMonth_returnsDateTime() throws AnswerMeException {
        LocalDateTime expectedDateTime = LocalDateTime.of(2026, 8, 31, 14, 0);

        assertEquals(expectedDateTime,
                DateTimeParser.parseDateTime("aUg 31 2026 14:00"));
    }

    @Test
    public void parseDateTime_validLeapDay_returnsDateTime() throws AnswerMeException {
        LocalDateTime expectedDateTime = LocalDateTime.of(2028, 2, 29, 0, 0);

        assertEquals(expectedDateTime, DateTimeParser.parseDateTime("29/2/2028"));
    }

    @Test
    public void parseDateTime_invalidTime_throwsAnswerMeException() {
        assertParseFails("31/8/2026 14:67");
    }

    @Test
    public void parseDateTime_invalidCalendarDate_throwsAnswerMeException() {
        assertParseFails("29/2/2026");
    }

    @Test
    public void parseDateTime_nullOrBlankInput_throwsAnswerMeException() {
        assertParseFails(null);
        assertParseFails("");
        assertParseFails("   ");
    }

    @Test
    public void formatDateTime_midnight_omitsTime() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 8, 31, 0, 0);

        assertEquals("Aug 31 2026", DateTimeParser.formatDateTime(dateTime));
    }

    @Test
    public void formatDateTime_nonMidnight_includesTime() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 8, 31, 14, 5);

        assertEquals("Aug 31 2026 14:05", DateTimeParser.formatDateTime(dateTime));
    }

    @Test
    public void formatDateTime_nullInput_throwsNullPointerException() {
        NullPointerException exception = assertThrows(
                NullPointerException.class, () -> DateTimeParser.formatDateTime(null));

        assertEquals("Datetime cannot be null.", exception.getMessage());
    }

    private void assertParseFails(String input) {
        AnswerMeException exception = assertThrows(
                AnswerMeException.class, () -> DateTimeParser.parseDateTime(input));

        assertEquals(INVALID_DATE_TIME_MESSAGE, exception.getMessage());
    }
}
