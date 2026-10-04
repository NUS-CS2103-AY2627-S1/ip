package unicorn.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/**
 * Checks date parsing and display formatting for deadline tasks.
 */
public class DeadlineTaskTest {
    /**
     * Verifies supported deadline input formats and display formatting.
     */
    @Test
    void parseBy_supportedDateTimeFormats_expectedDateTimesReturned() {
        LocalDateTime expectedDateTime = LocalDateTime.of(2019, 12, 2, 18, 0);
        assertEquals(expectedDateTime, DeadlineTask.parseBy("2019-12-02 1800"),
                "The ISO date and time should be parsed.");
        assertEquals(expectedDateTime, DeadlineTask.parseBy("2/12/2019 1800"),
                "The example date and time should be parsed.");

        LocalDateTime expectedDate = LocalDateTime.of(2019, 10, 15, 0, 0);
        assertEquals(expectedDate, DeadlineTask.parseBy("2019-10-15"),
                "A date-only deadline should start at midnight.");
    }

    /**
     * Verifies that invalid deadline input is rejected rather than silently changed.
     */
    @Test
    void parseBy_invalidDateTime_exceptionThrown() {
        assertThrows(DateTimeParseException.class, () -> DeadlineTask.parseBy("2019-13-01"),
                "A date with an invalid month should be rejected.");
        assertThrows(DateTimeParseException.class, () -> DeadlineTask.parseBy("2019-02-30"),
                "A nonexistent calendar date should be rejected.");
        assertThrows(DateTimeParseException.class, () -> DeadlineTask.parseBy("tomorrow evening"),
                "An unsupported deadline format should be rejected.");
    }

    /**
     * Verifies deadline display for both date-only and timed deadlines.
     */
    @Test
    void toString_deadlineHasTimeOrIsDateOnly_usesAppropriateDisplayFormat() {
        LocalDateTime parsedDateTime = LocalDateTime.of(2019, 12, 2, 18, 0);

        DeadlineTask task = new DeadlineTask("return book", parsedDateTime);
        assertEquals("[D] [ ] return book (by: Dec 02 2019 6:00 PM)", task.toString(),
                "A deadline should use the display format.");

        DeadlineTask dateOnlyTask = new DeadlineTask("submit form", LocalDateTime.of(2019, 10, 15, 0, 0));
        assertEquals("[D] [ ] submit form (by: Oct 15 2019)", dateOnlyTask.toString(),
                "A date-only deadline should not display a time.");
    }

    /**
     * Verifies that every deadline task has a deadline date and time.
     */
    @Test
    void constructor_nullDeadline_assertionErrorThrown() {
        assertThrows(AssertionError.class, () -> new DeadlineTask("return book", null),
                "A missing deadline violates the deadline task invariant.");
    }
}
