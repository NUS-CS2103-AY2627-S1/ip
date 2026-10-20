package yapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;

/**
 * Tests strict calendar parsing and the display and persistence formats.
 */
public class TaskDateTimeTest {
    @Test
    public void parse_supportedTimeFormats_returnsSameDateTime() {
        LocalDateTime expected = LocalDateTime.of(2024, 2, 29, 18, 30);
        for (String value : new String[] {"2024-02-29 1830", "29/2/2024 1830", "2024-02-29T18:30:00"}) {
            assertEquals(expected, TaskDateTime.parse(value), value);
        }
    }

    @Test
    public void parse_dateOnly_defaultsToMidnight() {
        LocalDateTime expected = LocalDate.of(2024, 2, 29).atStartOfDay();
        assertEquals(expected, TaskDateTime.parse("2024-02-29"));
        assertEquals(expected, TaskDateTime.parse("29/2/2024"));
    }

    @Test
    public void parse_invalidDatesAndTimes_rejectsInsteadOfNormalizing() {
        for (String value : new String[] {"", "tomorrow", "2023-02-29", "31/4/2024", "2024-13-01",
            "2024-01-00", "2024-02-29 2400", "29/2/2024 1260", "2024-02-29 930"}) {
            assertThrows(IllegalArgumentException.class, () -> TaskDateTime.parse(value), value);
        }
    }

    @Test
    public void parseDate_supportedFormats_acceptsLeapDay() {
        LocalDate expected = LocalDate.of(2024, 2, 29);
        assertEquals(expected, TaskDateTime.parseDate("2024-02-29"));
        assertEquals(expected, TaskDateTime.parseDate("29/2/2024"));
    }

    @Test
    public void parseDate_invalidOrContainsTime_throwsException() {
        for (String value : new String[] {"", "2023-02-29", "31/4/2024", "2024-02-29T12:00:00"}) {
            assertThrows(IllegalArgumentException.class, () -> TaskDateTime.parseDate(value), value);
        }
    }

    @Test
    public void formatForDisplay_midnightAndAfternoon_omitsTimeOnlyAtMidnight() {
        LocalDateTime midnight = LocalDate.of(2024, 2, 29).atStartOfDay();
        // Production display follows the default locale; keep this test portable across machines.
        assertEquals(midnight.format(DateTimeFormatter.ofPattern("MMM d uuuu")),
                TaskDateTime.formatForDisplay(midnight));
        LocalDateTime afternoon = midnight.withHour(18).withMinute(30);
        assertEquals(afternoon.format(DateTimeFormatter.ofPattern("MMM d uuuu, h:mma")),
                TaskDateTime.formatForDisplay(afternoon));
    }

    @Test
    public void formatForStorage_secondsAndFraction_roundTripsWithoutLoss() {
        LocalDateTime value = LocalDateTime.of(2024, 2, 29, 18, 30, 45, 123000000);
        assertEquals("2024-02-29T18:30:45.123", TaskDateTime.formatForStorage(value));
        assertEquals(value, TaskDateTime.parse(TaskDateTime.formatForStorage(value)));
    }
}
