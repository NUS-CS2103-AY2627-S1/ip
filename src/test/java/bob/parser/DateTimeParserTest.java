package bob.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/**
 * Tests date and date-time parsing and formatting performed by {@link DateTimeParser}.
 */
public class DateTimeParserTest {
    @Test
    public void parse_singleDigitDayAndMonth_returnsLocalDate() {
        assertEquals(LocalDate.of(2019, 2, 1), DateTimeParser.parse("1/2/2019"));
    }

    @Test
    public void parse_twoDigitDayAndMonth_returnsLocalDate() {
        assertEquals(LocalDate.of(2019, 12, 2), DateTimeParser.parse("02/12/2019"));
    }

    @Test
    public void parse_leapDay_returnsLocalDate() {
        assertEquals(LocalDate.of(2020, 2, 29), DateTimeParser.parse("29/02/2020"));
    }

    @Test
    public void parse_midnight_returnsLocalDateTime() {
        LocalDateTime expected = LocalDateTime.of(2019, 12, 2, 0, 0);
        assertEquals(expected, DateTimeParser.parse("2/12/2019 0000"));
    }

    @Test
    public void parse_lastMinuteOfDay_returnsLocalDateTime() {
        LocalDateTime expected = LocalDateTime.of(2019, 12, 2, 23, 59);
        assertEquals(expected, DateTimeParser.parse("2/12/2019 2359"));
    }

    @Test
    public void parse_nonLeapYearFebruary29_exceptionThrown() {
        assertThrows(DateTimeParseException.class,
                () -> DateTimeParser.parse("29/02/2019"));
    }

    @Test
    public void parse_impossibleCalendarDate_exceptionThrown() {
        assertThrows(DateTimeParseException.class,
                () -> DateTimeParser.parse("31/04/2019"));
    }

    @Test
    public void parse_hourOutside24HourRange_exceptionThrown() {
        assertThrows(DateTimeParseException.class,
                () -> DateTimeParser.parse("2/12/2019 2400"));
    }

    @Test
    public void parse_minuteOutsideValidRange_exceptionThrown() {
        assertThrows(DateTimeParseException.class,
                () -> DateTimeParser.parse("2/12/2019 1260"));
    }

    @Test
    public void parse_unsupportedDateSeparator_exceptionThrown() {
        assertThrows(DateTimeParseException.class,
                () -> DateTimeParser.parse("02-12-2019"));
    }

    @Test
    public void parse_incompleteTime_exceptionThrown() {
        assertThrows(DateTimeParseException.class,
                () -> DateTimeParser.parse("2/12/2019 900"));
    }

    @Test
    public void formatForDisplay_localDate_returnsDisplayDate() {
        LocalDate date = LocalDate.of(2019, 10, 15);
        assertEquals("Oct 15 2019", DateTimeParser.formatForDisplay(date));
    }

    @Test
    public void formatForDisplay_localDateTime_returnsDisplayDateTime() {
        LocalDateTime dateTime = LocalDateTime.of(2019, 10, 15, 18, 0);
        assertEquals("Oct 15 2019 1800hrs", DateTimeParser.formatForDisplay(dateTime));
    }

    @Test
    public void formatForStorage_localDate_returnsZeroPaddedDate() {
        LocalDate date = LocalDate.of(2019, 2, 1);
        assertEquals("01/02/2019", DateTimeParser.formatForStorage(date));
    }

    @Test
    public void formatForStorage_localDateTime_returnsZeroPaddedDateTime() {
        LocalDateTime dateTime = LocalDateTime.of(2019, 2, 1, 9, 5);
        assertEquals("01/02/2019 0905", DateTimeParser.formatForStorage(dateTime));
    }
}
