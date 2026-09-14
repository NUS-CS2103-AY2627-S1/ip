package baron.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import baron.exception.BaronException;

class DateHandlerTest {
    @Test
    void parse_supportedDateFormats_returnsExpectedDateTime() throws BaronException {
        LocalDateTime expected = LocalDateTime.of(2025, 8, 20, 0, 0);

        assertEquals(expected, DateHandler.parse("2025-08-20"));
        assertEquals(expected, DateHandler.parse("20-08-2025"));
        assertEquals(expected, DateHandler.parse("2025/08/20"));
        assertEquals(expected, DateHandler.parse("20/08/2025"));
        assertEquals(expected, DateHandler.parse("Aug 20 2025"));
        assertEquals(expected, DateHandler.parse("08 20 2025"));
    }

    @Test
    void parse_supportedTimeFormats_returnsExpectedTime() throws BaronException {
        assertEquals(
                LocalDateTime.of(2025, 8, 20, 14, 30),
                DateHandler.parse("2025-08-20 14:30"));
        assertEquals(
                LocalDateTime.of(2025, 8, 20, 14, 30, 45),
                DateHandler.parse("2025-08-20 14:30:45"));
    }

    @Test
    void parse_invalidDateOrTime_throwsBaronException() {
        assertThrows(BaronException.class, () -> DateHandler.parse("2025-13-01"));
        assertThrows(BaronException.class, () -> DateHandler.parse("not a date"));
        assertThrows(BaronException.class, () -> DateHandler.parse(""));
    }

    @Test
    void parse_nullInput_throwsBaronException() {
        assertThrows(BaronException.class, () -> DateHandler.parse(null));
    }
}
