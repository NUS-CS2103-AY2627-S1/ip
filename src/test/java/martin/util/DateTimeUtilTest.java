package martin.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import martin.exception.IllegalCommandException;

/**
 * Unit tests for {@link DateTimeUtil}.
 */
public class DateTimeUtilTest {

    @Test
    public void parse_validDateTimeFormat_success() {
        LocalDateTime expected = LocalDateTime.of(2026, 8, 26, 18, 30);
        LocalDateTime actual = DateTimeUtil.parse("2026/08/26 1830");
        assertEquals(expected, actual);
    }

    @Test
    public void parse_invalidDateTimeFormat_throwsIllegalCommandException() {
        assertThrows(IllegalCommandException.class, () -> DateTimeUtil.parse("2026-08-26 18:30"));
        assertThrows(IllegalCommandException.class, () -> DateTimeUtil.parse("invalid date"));
    }

    @Test
    public void formatDisplay_validDateTime_success() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 8, 26, 18, 30);
        assertEquals("Aug 26 2026, 18:30", DateTimeUtil.formatDisplay(dateTime));
    }
}
