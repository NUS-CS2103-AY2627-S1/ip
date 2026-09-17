package nova.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import nova.exception.NovaException;

/**
 * Tests the parsing, formatting, and value validation behavior of {@link TaskDateTime}.
 */
public class TaskDateTimeTest {
    private static final String EXPECTED_ERROR_MESSAGE = "The /by date/time must be a real date in "
            + "yyyy-MM-dd or d/M/yyyy format, optionally followed by HHmm, for example: "
            + "2019-12-02 1800.";
    private static final String EXPECTED_DATE_ERROR_MESSAGE = "The date after on must be a real "
            + "date in yyyy-MM-dd or d/M/yyyy format, for example: on 2019-12-02.";

    @Test
    public void parse_isoDate_returnsStartOfDayWithoutTime() throws NovaException {
        TaskDateTime.ParsedValue result = TaskDateTime.parse("2019-12-02", "/by");

        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0), result.dateTime());
        assertFalse(result.hasTime());
    }

    @Test
    public void parse_slashDate_returnsStartOfDayWithoutTime() throws NovaException {
        TaskDateTime.ParsedValue result = TaskDateTime.parse("2/12/2019", "/by");

        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0), result.dateTime());
        assertFalse(result.hasTime());
    }

    @Test
    public void parse_isoDateTime_returnsDateTimeWithTime() throws NovaException {
        TaskDateTime.ParsedValue result = TaskDateTime.parse("2019-12-02 1800", "/by");

        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), result.dateTime());
        assertTrue(result.hasTime());
    }

    @Test
    public void parse_slashDateTime_returnsDateTimeWithTime() throws NovaException {
        TaskDateTime.ParsedValue result = TaskDateTime.parse("2/12/2019 1800", "/by");

        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), result.dateTime());
        assertTrue(result.hasTime());
    }

    @Test
    public void parse_validLeapDay_returnsParsedDate() throws NovaException {
        TaskDateTime.ParsedValue result = TaskDateTime.parse("2020-02-29", "/by");

        assertEquals(LocalDateTime.of(2020, 2, 29, 0, 0), result.dateTime());
    }

    @Test
    public void parse_invalidCalendarDate_throwsNovaException() {
        NovaException exception = assertThrows(NovaException.class, () ->
                TaskDateTime.parse("2019-02-29", "/by"));

        assertEquals(EXPECTED_ERROR_MESSAGE, exception.getMessage());
    }

    @Test
    public void parse_invalidTime_throwsNovaException() {
        NovaException exception = assertThrows(NovaException.class, () ->
                TaskDateTime.parse("2019-12-02 2400", "/by"));

        assertEquals(EXPECTED_ERROR_MESSAGE, exception.getMessage());
    }

    @Test
    public void parse_unsupportedFormat_throwsNovaException() {
        NovaException exception = assertThrows(NovaException.class, () ->
                TaskDateTime.parse("December 2, 2019", "/by"));

        assertEquals(EXPECTED_ERROR_MESSAGE, exception.getMessage());
    }

    @Test
    public void parseForEdit_timeOnly_retainsExistingDate() throws NovaException {
        LocalDateTime existingDateTime = LocalDateTime.of(2019, 12, 3, 16, 0);

        TaskDateTime.ParsedValue result = TaskDateTime.parseForEdit(
                "1700", "/to", existingDateTime);

        assertEquals(LocalDateTime.of(2019, 12, 3, 17, 0), result.dateTime());
        assertTrue(result.hasTime());
    }

    @Test
    public void parseForEdit_invalidTime_throwsActionableException() {
        LocalDateTime existingDateTime = LocalDateTime.of(2019, 12, 3, 16, 0);

        NovaException exception = assertThrows(NovaException.class, () ->
                TaskDateTime.parseForEdit("2500", "/to", existingDateTime));

        assertEquals("The /to time must be a real time in HHmm format, for example: 1700.",
                exception.getMessage());
    }

    @Test
    public void parse_differentFieldName_usesFieldNameInErrorMessage() {
        NovaException exception = assertThrows(NovaException.class, () ->
                TaskDateTime.parse("not-a-date", "/from"));

        assertEquals(EXPECTED_ERROR_MESSAGE.replace("/by", "/from"), exception.getMessage());
    }

    @Test
    public void parseDate_isoDate_returnsParsedDate() throws NovaException {
        LocalDate result = TaskDateTime.parseDate("2019-12-02");

        assertEquals(LocalDate.of(2019, 12, 2), result);
    }

    @Test
    public void parseDate_slashDate_returnsParsedDate() throws NovaException {
        LocalDate result = TaskDateTime.parseDate("2/12/2019");

        assertEquals(LocalDate.of(2019, 12, 2), result);
    }

    @Test
    public void parseDate_invalidCalendarDate_throwsNovaException() {
        NovaException exception = assertThrows(NovaException.class, () ->
                TaskDateTime.parseDate("2019-02-29"));

        assertEquals(EXPECTED_DATE_ERROR_MESSAGE, exception.getMessage());
    }

    @Test
    public void parseDate_dateWithTime_throwsNovaException() {
        NovaException exception = assertThrows(NovaException.class, () ->
                TaskDateTime.parseDate("2019-12-02 1800"));

        assertEquals(EXPECTED_DATE_ERROR_MESSAGE, exception.getMessage());
    }

    @Test
    public void formatForDisplay_withoutTime_returnsReadableDate() {
        LocalDateTime dateTime = LocalDateTime.of(2019, 12, 2, 18, 0);

        assertEquals("Dec 02 2019", TaskDateTime.formatForDisplay(dateTime, false));
    }

    @Test
    public void formatForDisplay_withTime_returnsReadableDateTime() {
        LocalDateTime dateTime = LocalDateTime.of(2019, 12, 2, 18, 5);

        assertEquals("Dec 02 2019, 6:05 PM", TaskDateTime.formatForDisplay(dateTime, true));
    }

    @Test
    public void formatForStorage_withoutTime_returnsCanonicalDate() {
        LocalDateTime dateTime = LocalDateTime.of(2019, 12, 2, 18, 5);

        assertEquals("2019-12-02", TaskDateTime.formatForStorage(dateTime, false));
    }

    @Test
    public void formatForStorage_withTime_returnsCanonicalDateTime() {
        LocalDateTime dateTime = LocalDateTime.of(2019, 12, 2, 18, 5);

        assertEquals("2019-12-02 1805", TaskDateTime.formatForStorage(dateTime, true));
    }

    @Test
    public void parsedValue_nullDateTime_throwsDateTimeException() {
        DateTimeException exception = assertThrows(DateTimeException.class, () ->
                new TaskDateTime.ParsedValue(null, false));

        assertEquals("dateTime cannot be null", exception.getMessage());
    }
}
