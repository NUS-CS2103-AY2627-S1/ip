package yokohama.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/**
 * Tests conversion and validation of the application's date formats.
 */
public class DateTimeHandlerTest {
    @Test
    public void convertToLocalDateTime_validValue_returnsDateTime() {
        assertEquals(LocalDateTime.of(2026, 9, 3, 23, 59),
                DateTimeHandler.convertToLocalDateTime("9/3/2026 2359"));
    }

    @Test
    public void convertToDeadlineDateTime_dateOnly_usesEndOfDay() {
        assertEquals(LocalDateTime.of(2026, 9, 3, 23, 59),
                DateTimeHandler.convertToDeadlineDateTime("9/3/2026"));
    }

    @Test
    public void convertToDeadlineDateTime_withTime_preservesTime() {
        assertEquals(LocalDateTime.of(2026, 9, 3, 18, 30),
                DateTimeHandler.convertToDeadlineDateTime("9/3/2026 1830"));
    }

    @Test
    public void convertToLocalDate_validValue_returnsDate() {
        assertEquals(LocalDate.of(2026, 9, 3),
                DateTimeHandler.convertToLocalDate("9/3/2026"));
    }

    @Test
    public void formatToReadable_validValue_returnsExpectedFormat() {
        assertEquals("9/3/2026 2359",
                DateTimeHandler.formatToReadable(LocalDateTime.of(2026, 9, 3, 23, 59)));
    }

    @Test
    public void convertToLocalDateTime_invalidDate_throwsException() {
        assertThrows(DateTimeParseException.class, () -> DateTimeHandler.convertToLocalDateTime(
                "2/29/2025 1200"));
    }

    @Test
    public void convertToLocalDate_invalidDate_throwsException() {
        assertThrows(DateTimeParseException.class, () -> DateTimeHandler.convertToLocalDate(
                "2/30/2026"));
    }

    @Test
    public void convertToLocalDateTime_invalidFormat_throwsException() {
        assertThrows(DateTimeParseException.class, () -> DateTimeHandler.convertToLocalDateTime(
                "09/03/2026 12:00"));
    }

    @Test
    public void convertToLocalDateTime_invalidTime_throwsException() {
        assertThrows(DateTimeParseException.class, () -> DateTimeHandler.convertToLocalDateTime(
                "9/3/2026 2460"));
    }
}
