package brucli.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests the strict user-input parsing performed by {@link DateTimes}.
 */
public class DateTimesTest {

    @Test
    public void parse_validDateTime_returnsLocalDateTime() {
        LocalDateTime result = DateTimes.parse("2026-08-27 1830");

        assertEquals(LocalDateTime.of(2026, 8, 27, 18, 30), result);
    }

    @Test
    public void parse_midnight_returnsStartOfDay() {
        LocalDateTime result = DateTimes.parse("2026-08-27 0000");

        assertEquals(LocalDateTime.of(2026, 8, 27, 0, 0), result);
    }

    @Test
    public void parse_validLeapDay_returnsLocalDateTime() {
        LocalDateTime result = DateTimes.parse("2024-02-29 2359");

        assertEquals(LocalDateTime.of(2024, 2, 29, 23, 59), result);
    }

    @Test
    public void parse_impossibleCalendarDate_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> DateTimes.parse("2026-02-30 1200"));
    }

    @Test
    public void parse_leapDayInNonLeapYear_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> DateTimes.parse("2025-02-29 1200"));
    }

    @Test
    public void parse_hourOutsideRange_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> DateTimes.parse("2026-08-27 2400"));
    }

    @Test
    public void parse_minuteOutsideRange_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> DateTimes.parse("2026-08-27 1260"));
    }

    @Test
    public void parse_wrongDateTimeFormat_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> DateTimes.parse("27-08-2026 1830"));
    }

    @Test
    public void parse_missingLeadingZeros_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> DateTimes.parse("2026-8-27 830"));
    }
}
