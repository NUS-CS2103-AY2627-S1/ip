package goat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests parsing, formatting, and validation of task dates.
 */
public class TaskDateTest {
    @Test
    public void parse_validLeapDate_returnsLocalDate() throws GoatException {
        assertEquals(LocalDate.of(2028, 2, 29),
                TaskDate.parse("2028-02-29", "The date"));
    }

    @Test
    public void parse_invalidLeapDate_throwsHelpfulException() {
        GoatException exception = assertThrows(
                GoatException.class, () -> TaskDate.parse("2027-02-29", "The date"));

        assertEquals("The date must be a valid date in yyyy-MM-dd format.",
                exception.getMessage());
    }

    @Test
    public void format_validDate_usesReadableEnglishFormat() {
        assertEquals("Jan 05 2026", TaskDate.format(LocalDate.of(2026, 1, 5)));
    }

    @Test
    public void requireStartBeforeEnd_reverseRange_throwsHelpfulException() {
        GoatException exception = assertThrows(GoatException.class, () ->
                TaskDate.requireStartBeforeEnd(
                        LocalDate.of(2026, 9, 2), LocalDate.of(2026, 9, 1), "An event"));

        assertEquals("An event must start before it ends.", exception.getMessage());
    }
}
