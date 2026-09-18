package toothless.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/**
 * Tests deadline date parsing and display formatting.
 */
public class DeadlineDateTest {
    /**
     * Verifies ordinary ISO input becomes the matching date.
     */
    @Test
    public void parse_isoDate_returnsMatchingDate() {
        assertEquals(LocalDate.of(2019, 12, 2), DeadlineDate.parse("2019-12-02"));
    }

    /**
     * Verifies valid leap days are accepted.
     */
    @Test
    public void parse_leapDayInLeapYear_returnsMatchingDate() {
        assertEquals(LocalDate.of(2020, 2, 29), DeadlineDate.parse("2020-02-29"));
    }

    /**
     * Verifies dates use the required friendly display format.
     */
    @Test
    public void format_date_returnsFriendlyEnglishDate() {
        assertEquals("Dec 2 2019", DeadlineDate.format(LocalDate.of(2019, 12, 2)));
    }

    /**
     * Verifies February 29 is rejected outside a leap year.
     */
    @Test
    public void parse_leapDayInNonLeapYear_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> DeadlineDate.parse("2019-02-29"));
    }

    /**
     * Verifies calendar dates that cannot occur are rejected.
     */
    @Test
    public void parse_impossibleDate_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> DeadlineDate.parse("2019-02-30"));
    }

    /**
     * Verifies non-ISO date ordering is rejected.
     */
    @Test
    public void parse_nonIsoDate_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> DeadlineDate.parse("02-12-2019"));
    }

    /**
     * Verifies extra text cannot be mistaken for part of a valid date.
     */
    @Test
    public void parse_dateWithAdditionalContent_throwsDateTimeParseException() {
        assertThrows(
                DateTimeParseException.class, () ->
                DeadlineDate.parse("2019-12-02 evening"));
    }
}
