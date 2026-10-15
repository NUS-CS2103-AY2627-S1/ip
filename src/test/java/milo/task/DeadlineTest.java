package milo.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

public class DeadlineTest {

    @Test
    public void storageString_newDeadline_expectedCanonicalDateTimeFormat() {
        assertEquals("D | 0 | ABC | 2026-08-30 1800", new Deadline("ABC",
                LocalDateTime.of(2026, 8, 30, 18, 0)).storageString());
    }

    @Test
    public void toString_deadlineAtNoon_expectedReadableDateTimeFormat() {
        assertEquals("[D] [ ] DEF (by: Jan 01 2026, 12:00 PM)", new Deadline("DEF",
                LocalDateTime.of(2026, 1, 1, 12, 0)).toString());
    }

    @Test
    public void constructorWithDateString_slashDateTime_expectedParsedLocalDateTime() {
        Deadline deadline = new Deadline("return book", "2/12/2019 1800");

        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), deadline.getDeadlineDate());
    }

    @Test
    public void markAsDone_deadlineTask_expectedDoneMarker() {
        Deadline deadline = new Deadline("ABC", LocalDateTime.of(2026, 8, 30, 18, 0));
        deadline.markAsDone();

        assertEquals("D | 1 | ABC | 2026-08-30 1800", deadline.storageString());
        assertEquals("[D] [X] ABC (by: Aug 30 2026, 6:00 PM)", deadline.toString());
    }

    @Test
    public void constructorWithDateString_invalidDate_expectedParseException() {
        assertThrows(DateTimeParseException.class, () -> new Deadline("ABC", "31/02/2019 1800"));
    }
}
