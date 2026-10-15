package edith.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/** Tests date parsing and display formatting used by Edith tasks. */
public class DateFormatterTest {
    @Test
    public void parseInput_validDate_returnsStartOfDayWithoutTime() {
        DateFormatter.ParsedDateTime result = DateFormatter.parseInput("2026-09-06");

        assertEquals(LocalDateTime.of(2026, 9, 6, 0, 0), result.value());
        assertFalse(result.hasTime());
    }

    @Test
    public void parseInput_validDateTime_returnsDateTimeWithTime() {
        DateFormatter.ParsedDateTime result = DateFormatter.parseInput("2026-09-06 1430");

        assertEquals(LocalDateTime.of(2026, 9, 6, 14, 30), result.value());
        assertTrue(result.hasTime());
    }

    @Test
    public void parseInput_invalidCalendarDate_exceptionThrown() {
        assertThrows(DateTimeParseException.class, () -> DateFormatter.parseInput("2026-02-29"));
    }

    @Test
    public void parseInput_invalidTime_exceptionThrown() {
        assertThrows(DateTimeParseException.class, () -> DateFormatter.parseInput("2026-09-06 2500"));
    }

    @Test
    public void parseInput_incorrectDateFormat_exceptionThrown() {
        assertThrows(DateTimeParseException.class, () -> DateFormatter.parseInput("06-09-2026"));
    }

    @Test
    public void parseInput_incorrectTimeFormat_exceptionThrown() {
        assertThrows(DateTimeParseException.class, () -> DateFormatter.parseInput("2026-09-06 14:30"));
    }

    @Test
    public void parseDisplayedDateTime_validDate_returnsStartOfDayWithoutTime() {
        DateFormatter.ParsedDateTime result = DateFormatter.parseDisplayedDateTime("Sep 06 2026");

        assertEquals(LocalDateTime.of(2026, 9, 6, 0, 0), result.value());
        assertFalse(result.hasTime());
    }

    @Test
    public void parseDisplayedDateTime_validDateTime_returnsDateTimeWithTime() {
        DateFormatter.ParsedDateTime result = DateFormatter.parseDisplayedDateTime("Sep 06 2026 14:30");

        assertEquals(LocalDateTime.of(2026, 9, 6, 14, 30), result.value());
        assertTrue(result.hasTime());
    }

    @Test
    public void parseDisplayedDateTime_invalidCalendarDate_exceptionThrown() {
        assertThrows(DateTimeParseException.class, () -> DateFormatter.parseDisplayedDateTime("Apr 32 2026"));
    }

    @Test
    public void parseDisplayedDateTime_invalidTime_exceptionThrown() {
        assertThrows(DateTimeParseException.class, () -> DateFormatter.parseDisplayedDateTime("Sep 06 2026 25:00"));
    }

    @Test
    public void parseDisplayedDateTime_incorrectDateFormat_exceptionThrown() {
        assertThrows(DateTimeParseException.class, () -> DateFormatter.parseDisplayedDateTime("September 06 2026"));
    }

    @Test
    public void parseDisplayedDateTime_incorrectTimeFormat_exceptionThrown() {
        assertThrows(DateTimeParseException.class, () -> DateFormatter.parseDisplayedDateTime("Sep 06 2026 1430"));
    }

    @Test
    public void formatForDisplay_dateWithoutTime_returnsReadableDate() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 9, 6, 14, 30);

        assertEquals("Sep 06 2026", DateFormatter.formatForDisplay(dateTime, false));
    }

    @Test
    public void formatForDisplay_dateWithTime_returnsReadableDateTime() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 9, 6, 14, 30);

        assertEquals("Sep 06 2026 14:30", DateFormatter.formatForDisplay(dateTime, true));
    }
}
