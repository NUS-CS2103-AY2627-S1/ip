package ralph.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests for DateTimeUtil formatting helpers.
 */
class DateTimeUtilTest {

    @Test
    void formatDateOrDateTime_midnight_returnsDateOnly() {
        LocalDateTime dt = LocalDateTime.of(2023, 1, 2, 0, 0);
        String formatted = DateTimeUtil.formatDateOrDateTime(dt);
        assertEquals("Jan 02 2023", formatted);
    }

    @Test
    void formatDateOrDateTime_nonMidnight_returnsDateAndTime() {
        LocalDateTime dt = LocalDateTime.of(2023, 1, 2, 18, 30);
        String formatted = DateTimeUtil.formatDateOrDateTime(dt);
        assertTrue(formatted.contains("Jan 02 2023"));
        assertTrue(formatted.contains("18:30"));
    }

    @Test
    void formatDate_givenLocalDateTime_returnsFormattedDate() {
        LocalDateTime dt = LocalDateTime.of(2024, 12, 5, 9, 15);
        assertEquals("Dec 05 2024", DateTimeUtil.formatDate(dt));
    }

    @Test
    void formatTime_givenLocalDateTime_returnsFormattedTime() {
        LocalDateTime dt = LocalDateTime.of(2024, 12, 5, 9, 7);
        assertEquals("09:07", DateTimeUtil.formatTime(dt));
    }
}
