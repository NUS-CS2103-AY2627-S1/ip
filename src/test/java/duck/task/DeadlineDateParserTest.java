package duck.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/**
 * Tests canonical and natural deadline-date parsing.
 */
public class DeadlineDateParserTest {

    @Test
    public void parse_validBoundaryDates_returnsParsedDates() {
        assertEquals(LocalDate.of(1, 1, 1), DeadlineDateParser.parse("0001-01-01"));
        assertEquals(LocalDate.of(2024, 2, 29), DeadlineDateParser.parse("2024-02-29"));
        assertEquals(LocalDate.of(9999, 12, 31), DeadlineDateParser.parse("9999-12-31"));
    }

    @Test
    public void parse_invalidDates_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> DeadlineDateParser.parse("2026-02-30"));
        assertThrows(DateTimeParseException.class, () -> DeadlineDateParser.parse("2026-8-3"));
        assertThrows(DateTimeParseException.class, () -> DeadlineDateParser.parse("0000-01-01"));
    }

    @Test
    public void parseCommandDate_weekdayAbbreviations_returnsStrictlyNextDates() {
        LocalDate currentDate = LocalDate.of(2026, 9, 8);

        assertEquals(LocalDate.of(2026, 9, 14),
                DeadlineDateParser.parseCommandDate("Mon", currentDate));
        assertEquals(LocalDate.of(2026, 9, 15),
                DeadlineDateParser.parseCommandDate("Tue", currentDate));
        assertEquals(LocalDate.of(2026, 9, 9),
                DeadlineDateParser.parseCommandDate("Wed", currentDate));
        assertEquals(LocalDate.of(2026, 9, 10),
                DeadlineDateParser.parseCommandDate("Thu", currentDate));
        assertEquals(LocalDate.of(2026, 9, 11),
                DeadlineDateParser.parseCommandDate("Fri", currentDate));
        assertEquals(LocalDate.of(2026, 9, 12),
                DeadlineDateParser.parseCommandDate("Sat", currentDate));
        assertEquals(LocalDate.of(2026, 9, 13),
                DeadlineDateParser.parseCommandDate("Sun", currentDate));
    }

    @Test
    public void parseCommandDate_mixedCaseWeekday_returnsNextMatchingDate() {
        LocalDate currentDate = LocalDate.of(2026, 9, 8);

        assertEquals(LocalDate.of(2026, 9, 14),
                DeadlineDateParser.parseCommandDate("mOn", currentDate));
    }

    @Test
    public void parseCommandDate_yearRollover_returnsDateInNextYear() {
        LocalDate currentDate = LocalDate.of(2026, 12, 31);

        assertEquals(LocalDate.of(2027, 1, 1),
                DeadlineDateParser.parseCommandDate("Fri", currentDate));
    }

    @Test
    public void parseCommandDate_canonicalPastDate_returnsExplicitDate() {
        LocalDate currentDate = LocalDate.of(2026, 9, 8);

        assertEquals(LocalDate.of(2020, 1, 1),
                DeadlineDateParser.parseCommandDate("2020-01-01", currentDate));
    }

    @Test
    public void parseCommandDate_unsupportedNaturalDates_throwsDateTimeParseException() {
        LocalDate currentDate = LocalDate.of(2026, 9, 8);

        assertThrows(DateTimeParseException.class, () ->
                DeadlineDateParser.parseCommandDate("Monday", currentDate));
        assertThrows(DateTimeParseException.class, () ->
                DeadlineDateParser.parseCommandDate("Tues", currentDate));
        assertThrows(DateTimeParseException.class, () ->
                DeadlineDateParser.parseCommandDate("next Mon", currentDate));
        assertThrows(DateTimeParseException.class, () ->
                DeadlineDateParser.parseCommandDate("Mon 6pm", currentDate));
    }
}
