package mono.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/**
 * Tests the shared input and display date formats.
 */
public class DateParserTest {
    @Test
    public void parseDate_isoDate_returnsLocalDate() {
        assertEquals(LocalDate.of(2026, 8, 24), DateParser.parseDate("2026-08-24"));
    }

    @Test
    public void parseDate_leapDay_returnsLocalDate() {
        assertEquals(LocalDate.of(2024, 2, 29), DateParser.parseDate("2024-02-29"));
    }

    @Test
    public void parseDate_invalidCalendarDate_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> DateParser.parseDate("2025-02-29"));
    }

    @Test
    public void parseDate_nonIsoDate_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> DateParser.parseDate("24/08/2026"));
    }

    @Test
    public void formatDate_date_returnsEnglishDisplayDate() {
        assertEquals("Aug 24 2026", DateParser.formatDate(LocalDate.of(2026, 8, 24)));
    }
}
