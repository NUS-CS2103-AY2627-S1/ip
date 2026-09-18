package phin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Checks strict calendar parsing and stable English display formatting.
 */
class TaskDateTest {
    @Test
    void parse_validCalendarBoundaries_returnsDate() {
        assertEquals(LocalDate.of(2024, 2, 29), TaskDate.parse("2024-02-29"));
        assertEquals(LocalDate.of(2000, 2, 29), TaskDate.parse("2000-02-29"));
        assertEquals(LocalDate.of(2024, 12, 31), TaskDate.parse("2024-12-31"));
        assertEquals(LocalDate.of(2025, 1, 1), TaskDate.parse("2025-01-01"));
    }

    @Test
    void parse_impossibleDates_rejected() {
        for (String date : new String[] {"2023-02-29", "1900-02-29", "2024-04-31", "2024-00-01",
                "2024-13-01", "2024-01-00", "2024-01-32"}) {
            assertThrows(IllegalArgumentException.class, () -> TaskDate.parse(date), date);
        }
    }

    @Test
    void parse_wrongFormat_rejectedWithGuidance() {
        for (String date : new String[] {"", "Sunday", "2024-2-03", "2024-02-3", "24-02-03",
                "03/02/2024", " 2024-02-03", "2024-02-03 ", "2024-02-03T12:00"}) {
            IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                    () -> TaskDate.parse(date), date);
            assertEquals("Use a valid date in yyyy-MM-dd format (e.g., 2019-10-15).", error.getMessage());
        }
    }

    @Test
    void format_date_usesEnglishMonthAndPaddedDay() {
        assertEquals("Feb 03 2024", TaskDate.format(LocalDate.of(2024, 2, 3)));
        assertEquals("Dec 31 2024", TaskDate.format(LocalDate.of(2024, 12, 31)));
    }
}
