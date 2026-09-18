package student.project.bob.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/**
 * Tests supported date-time parsing and display formatting.
 */
public class DateTimeParserTest {
    /**
     * Verifies that ISO and space-separated date-time forms are parsed correctly.
     */
    @Test
    public void parse_isoAndSpaceSeparatedDateTimes_returnsLocalDateTimes() {
        LocalDateTime expected = LocalDateTime.of(2019, 10, 15, 18, 0);

        assertEquals(expected, DateTimeParser.parse("2019-10-15T18:00"));
        assertEquals(expected, DateTimeParser.parse("2019-10-15 18:00"));
        assertEquals(expected.withSecond(5), DateTimeParser.parse("2019-10-15 18:00:05"));
        assertEquals(expected, DateTimeParser.parse("  2019-10-15T18:00  "));
    }

    /**
     * Verifies that ISO date and persisted display forms are parsed correctly.
     */
    @Test
    public void parse_dateAndDisplayForms_returnsExpectedTemporalValues() {
        LocalDate date = LocalDate.of(2019, 10, 15);
        LocalDateTime dateTime = LocalDateTime.of(2019, 10, 15, 18, 0, 5);

        assertEquals(date, DateTimeParser.parse("2019-10-15"));
        assertEquals(date, DateTimeParser.parse("Tue, Oct 15 2019"));
        assertEquals(dateTime.withSecond(0), DateTimeParser.parse("Tue, Oct 15 2019 18:00"));
        assertEquals(dateTime, DateTimeParser.parse("Tue, Oct 15 2019 18:00:05"));
    }

    /**
     * Verifies that unsupported date-time input is rejected.
     */
    @Test
    public void parse_invalidInput_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> DateTimeParser.parse("not-a-date"));
        assertThrows(DateTimeParseException.class, () -> DateTimeParser.parse("2019-02-30"));
    }

    /**
     * Verifies date and minute-only date-time formatting.
     */
    @Test
    public void format_dateAndDateTimeWithoutSeconds_returnsDisplayText() {
        assertEquals("Tue, Oct 15 2019", DateTimeParser.format(LocalDate.of(2019, 10, 15)));
        assertEquals("Tue, Oct 15 2019 18:00", DateTimeParser.format(LocalDateTime.of(2019, 10, 15, 18, 0)));
    }

    /**
     * Verifies second-precision date-time formatting and unsupported types.
     */
    @Test
    public void format_dateTimeWithSecondsOrUnsupportedType_handlesValues() {
        assertEquals("Tue, Oct 15 2019 18:00:05", DateTimeParser.format(LocalDateTime.of(2019, 10, 15, 18, 0, 5)));

        assertThrows(IllegalArgumentException.class, () -> DateTimeParser.format(Year.of(2019)));
    }
}
