package echo.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

import echo.EchoException;
import echo.util.DateTimeParser.DateTimeValue;

/**
 * Tests date-time parsing, formatting, and chronological validation in {@link DateTimeParser}.
 */
public class DateTimeParserTest {

    @Test
    public void parse_validDateOnly_returnsDateTimeValueWithoutTime() {
        DateTimeValue value = DateTimeParser.parse("25-12-2026");

        assertEquals(LocalDate.of(2026, 12, 25), value.date());
        assertNull(value.time());
        assertEquals("Dec 25 2026", value.formatForDisplay());
        assertEquals("25-12-2026", value.formatForStorage());
    }

    @Test
    public void parse_validDateTime_returnsDateTimeValueWithTime() {
        DateTimeValue value = DateTimeParser.parse("25-12-2026 14:30");

        assertEquals(LocalDate.of(2026, 12, 25), value.date());
        assertEquals(LocalTime.of(14, 30), value.time());
        assertEquals("Dec 25 2026, 2:30pm", value.formatForDisplay());
        assertEquals("25-12-2026 14:30", value.formatForStorage());
    }

    @Test
    public void parse_invalidPatterns_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> DateTimeParser.parse("2026-12-25"));
        assertThrows(DateTimeParseException.class, () -> DateTimeParser.parse("25/12/2026"));
        assertThrows(DateTimeParseException.class, () -> DateTimeParser.parse("not a date"));
        assertThrows(DateTimeParseException.class, () -> DateTimeParser.parse("25-12-2026 25:00"));
        assertThrows(DateTimeParseException.class, () -> DateTimeParser.parse("31-02-2026"));
    }

    @Test
    public void validate_validInput_doesNotThrowException() {
        assertDoesNotThrow(() -> DateTimeParser.validate("15-08-2026"));
        assertDoesNotThrow(() -> DateTimeParser.validate("15-08-2026 09:00"));
    }

    @Test
    public void validate_invalidInput_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> DateTimeParser.validate("invalid"));
    }

    @Test
    public void validateChronologicalOrder_startDateAfterEndDate_throwsEchoException() {
        DateTimeValue start = new DateTimeValue(LocalDate.of(2026, 12, 26), null);
        DateTimeValue end = new DateTimeValue(LocalDate.of(2026, 12, 25), null);

        EchoException exception = assertThrows(
                EchoException.class, () -> DateTimeParser.validateChronologicalOrder(start, end));
        assertTrue(exception.getMessage().contains("cannot be after end date"));
    }

    @Test
    public void validateChronologicalOrder_sameDateWithoutTimes_throwsEchoException() {
        DateTimeValue start = new DateTimeValue(LocalDate.of(2026, 12, 25), null);
        DateTimeValue end = new DateTimeValue(LocalDate.of(2026, 12, 25), null);

        EchoException exception = assertThrows(
                EchoException.class, () -> DateTimeParser.validateChronologicalOrder(start, end));
        assertTrue(exception.getMessage().contains("cannot be identical to end date without specifying distinct"));
    }

    @Test
    public void validateChronologicalOrder_sameDateOneTimeMissing_throwsEchoException() {
        DateTimeValue startWithTime = new DateTimeValue(LocalDate.of(2026, 12, 25), LocalTime.of(10, 0));
        DateTimeValue endWithoutTime = new DateTimeValue(LocalDate.of(2026, 12, 25), null);

        EchoException exception1 = assertThrows(EchoException.class, () ->
                DateTimeParser.validateChronologicalOrder(startWithTime, endWithoutTime));
        assertTrue(exception1.getMessage().contains("both start and end must specify times"));

        EchoException exception2 = assertThrows(EchoException.class, () ->
                DateTimeParser.validateChronologicalOrder(endWithoutTime, startWithTime));
        assertTrue(exception2.getMessage().contains("both start and end must specify times"));
    }

    @Test
    public void validateChronologicalOrder_sameDateStartTimeAfterEndTime_throwsEchoException() {
        DateTimeValue start = new DateTimeValue(LocalDate.of(2026, 12, 25), LocalTime.of(15, 0));
        DateTimeValue end = new DateTimeValue(LocalDate.of(2026, 12, 25), LocalTime.of(14, 0));

        EchoException exception = assertThrows(
                EchoException.class, () -> DateTimeParser.validateChronologicalOrder(start, end));
        assertTrue(exception.getMessage().contains("cannot be after end time"));
    }

    @Test
    public void validateChronologicalOrder_sameDateStartTimeEqualsEndTime_throwsEchoException() {
        DateTimeValue start = new DateTimeValue(LocalDate.of(2026, 12, 25), LocalTime.of(14, 0));
        DateTimeValue end = new DateTimeValue(LocalDate.of(2026, 12, 25), LocalTime.of(14, 0));

        EchoException exception = assertThrows(
                EchoException.class, () -> DateTimeParser.validateChronologicalOrder(start, end));
        assertTrue(exception.getMessage().contains("cannot be the same as end time"));
    }

    @Test
    public void validateChronologicalOrder_validChronology_doesNotThrow() {
        DateTimeValue start1 = new DateTimeValue(LocalDate.of(2026, 12, 24), null);
        DateTimeValue end1 = new DateTimeValue(LocalDate.of(2026, 12, 25), null);
        assertDoesNotThrow(() -> DateTimeParser.validateChronologicalOrder(start1, end1));

        DateTimeValue start2 = new DateTimeValue(LocalDate.of(2026, 12, 25), LocalTime.of(10, 0));
        DateTimeValue end2 = new DateTimeValue(LocalDate.of(2026, 12, 25), LocalTime.of(12, 0));
        assertDoesNotThrow(() -> DateTimeParser.validateChronologicalOrder(start2, end2));
    }

    @Test
    public void formatForDisplay_variousTimes_formatsAmPmCorrectly() {
        LocalDate date = LocalDate.of(2026, 1, 5);

        assertEquals("Jan 5 2026, 9:05am",
                DateTimeParser.formatForDisplay(date, LocalTime.of(9, 5)));
        assertEquals("Jan 5 2026, 12:00pm",
                DateTimeParser.formatForDisplay(date, LocalTime.of(12, 0)));
        assertEquals("Jan 5 2026, 12:00am",
                DateTimeParser.formatForDisplay(date, LocalTime.of(0, 0)));
        assertEquals("Jan 5 2026, 11:59pm",
                DateTimeParser.formatForDisplay(date, LocalTime.of(23, 59)));
    }

    @Test
    public void formatForStorage_formatsDateAndOptionalTime() {
        LocalDate date = LocalDate.of(2026, 3, 9);

        assertEquals("09-03-2026", DateTimeParser.formatForStorage(date, null));
        assertEquals("09-03-2026 08:15", DateTimeParser.formatForStorage(date, LocalTime.of(8, 15)));
    }
}
