package mary.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/**
 * Tests completion transitions and the date parsing used throughout MARY.
 */
class TaskTest {
    /**
     * Tests completion: repeated mark and unmark; is idempotent.
     */
    @Test
    void completion_repeatedMarkAndUnmark_isIdempotent() {
        Task task = new Task("read book");
        assertEquals("[ ] read book", task.toString());
        task.markAsDone();
        task.markAsDone();
        assertEquals("X", task.getStatusIcon());
        assertEquals("T | 1 | read book", task.toStorageRecord());
        task.markAsNotDone();
        task.markAsNotDone();
        assertEquals(" ", task.getStatusIcon());
        assertEquals("T | 0 | read book", task.toStorageRecord());
    }

    /**
     * Tests set done: loaded state; controls display and storage.
     */
    @Test
    void setDone_loadedState_controlsDisplayAndStorage() {
        Task task = new Task("read");
        task.setDone(true);
        assertEquals("[X] read", task.toString());
        task.setDone(false);
        assertEquals("[ ] read", task.toString());
    }

    /**
     * Tests parse date time: valid leap day and time boundaries; parses correctly.
     */
    @Test
    void parseDateTime_validLeapDayAndTimeBoundaries_parsesCorrectly() {
        assertEquals(LocalDateTime.of(2024, 2, 29, 0, 0), Task.parseDateTime("29/2/2024 0000"));
        assertEquals(LocalDateTime.of(2019, 12, 2, 23, 59), Task.parseDateTime("02/12/2019 2359"));
    }

    /**
     * Tests parse date: valid date; returns local date.
     */
    @Test
    void parseDate_validDate_returnsLocalDate() {
        assertEquals(LocalDate.of(2019, 12, 2), Task.parseDate("2/12/2019"));
        assertEquals(LocalDate.of(2024, 2, 29), Task.parseDate("29/2/2024"));
    }

    /**
     * Tests parse date: malformed values; throws parse exception.
     */
    @Test
    void parseDate_malformedValues_throwsParseException() {
        for (String input : new String[] {"", "tomorrow", "2019-12-02", "1/13/2019", "0/12/2019"}) {
            assertThrows(DateTimeParseException.class, () -> Task.parseDate(input), input);
        }
    }

    /**
     * Tests parse date time: malformed values; throws parse exception.
     */
    @Test
    void parseDateTime_malformedValues_throwsParseException() {
        String[] inputs = {"", "2/12/2019", "2/12/2019 18:00",
            "2/12/2019 2500", "2/12/2019 1860", "2/13/2019 1800"};
        for (String input : inputs) {
            assertThrows(DateTimeParseException.class, () -> Task.parseDateTime(input), input);
        }
    }

    /**
     * Tests parse date: impossible calendar date; rejects rather than changing date.
     */
    @Test
    void parseDate_impossibleCalendarDate_rejectsRatherThanChangingDate() {
        for (String input : new String[] {"29/2/2023", "29/2/1900", "31/4/2024"}) {
            assertThrows(DateTimeParseException.class, () -> Task.parseDate(input), input);
        }
    }

    /**
     * Tests parse date time: impossible calendar date; rejects rather than changing date.
     */
    @Test
    void parseDateTime_impossibleCalendarDate_rejectsRatherThanChangingDate() {
        for (String input : new String[] {"31/4/2024 1800", "29/2/2023 1800"}) {
            assertThrows(DateTimeParseException.class, () -> Task.parseDateTime(input), input);
        }
    }

    /**
     * Tests parse date time: hour24; rejects rather than rolling into next day.
     */
    @Test
    void parseDateTime_hour24_rejectsRatherThanRollingIntoNextDay() {
        assertThrows(DateTimeParseException.class, () -> Task.parseDateTime("2/12/2019 2400"));
    }
}
