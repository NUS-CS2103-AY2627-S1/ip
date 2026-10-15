package dude.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/**
 * Tests parsing and validation of task dates.
 */
public class TaskDateTest {
    @Test
    public void parseDateOnly_returnsDateWithoutTime() {
        TaskDate taskDate = TaskDate.parse("2019-12-02");

        assertEquals(LocalDate.of(2019, 12, 2), taskDate.date());
        assertNull(taskDate.dateTime());
    }

    @Test
    public void parseDateTime_returnsDateAndTime() {
        TaskDate taskDate = TaskDate.parse("2019-12-02 1800");

        assertEquals(LocalDate.of(2019, 12, 2), taskDate.date());
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), taskDate.dateTime());
    }

    @Test
    public void parseInvalidCalendarDate_throwsException() {
        assertThrows(DateTimeParseException.class, () -> TaskDate.parse("2019-02-29"));
    }
}
