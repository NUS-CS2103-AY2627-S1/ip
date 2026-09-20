package harold.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

class TaskDateTest {
    @Test
    void parse_validIsoDate_returnsLocalDate() {
        assertEquals(LocalDate.of(2026, 8, 30), TaskDate.parse("2026-08-30"));
    }

    @Test
    void parse_validLeapDay_returnsLocalDate() {
        assertEquals(LocalDate.of(2024, 2, 29), TaskDate.parse("2024-02-29"));
    }

    @Test
    void parse_invalidDates_throwException() {
        assertThrows(DateTimeParseException.class, () -> TaskDate.parse("2023-02-29"));
        assertThrows(DateTimeParseException.class, () -> TaskDate.parse("30-08-2026"));
        assertThrows(DateTimeParseException.class, () -> TaskDate.parse(""));
    }

    @Test
    void format_date_returnsReadableEnglishDate() {
        assertEquals("Aug 30 2026", TaskDate.format(LocalDate.of(2026, 8, 30)));
    }
}
