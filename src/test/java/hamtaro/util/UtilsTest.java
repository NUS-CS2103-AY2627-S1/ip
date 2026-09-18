package hamtaro.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests date-time parsing across the supported input formats. */
class UtilsTest {

    @Test
    void parseDateTime_supportedFormats_returnsExpectedDateTime() {
        LocalDateTime expected = LocalDateTime.of(2026, 9, 1, 14, 30);

        assertEquals(expected, Utils.parseDateTime("01/09/2026 1430"));
        assertEquals(expected, Utils.parseDateTime("01-09-2026 1430"));
        assertEquals(expected, Utils.parseDateTime("2026/09/01 1430"));
        assertEquals(expected, Utils.parseDateTime("2026-09-01 1430"));
        assertEquals(expected, Utils.parseDateTime("2026-09-01T14:30"));
    }

    @Test
    void parseDateTime_impossibleDate_throwsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Utils.parseDateTime("31/02/2026 1430"));

        assertEquals("Invalid date/time format", exception.getMessage());
    }

    @Test
    void parseDateTime_unsupportedFormat_throwsException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> Utils.parseDateTime("2026-09-01 14:30"));

        assertEquals("Invalid date/time format", exception.getMessage());
    }
}
