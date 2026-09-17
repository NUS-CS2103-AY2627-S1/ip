package panda.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import panda.exception.task.InvalidEventParametersException;

/**
 * Contains unit tests for task date checking logic across {@link Todo}, {@link Deadline}, and {@link Event}.
 */
public class TaskDateCheckTest {

    @Test
    public void todoCheckDate_alwaysReturnsFalse() {
        Todo todo = new Todo("read book");
        assertFalse(todo.occursOn(LocalDate.of(2026, 10, 15)));
        assertFalse(todo.occursOn(LocalDateTime.of(2026, 10, 15, 12, 0)));
        assertFalse(todo.occursOn(null));
    }

    @Test
    public void deadlineCheckDate_sameDate_returnsTrue() {
        Deadline deadline = new Deadline("submit assignment", LocalDateTime.of(2026, 10, 15, 18, 0));
        assertTrue(deadline.occursOn(LocalDate.of(2026, 10, 15)));
        assertTrue(deadline.occursOn(LocalDateTime.of(2026, 10, 15, 9, 0)));
    }

    @Test
    public void deadlineCheckDate_differentDate_returnsFalse() {
        Deadline deadline = new Deadline("submit assignment", LocalDate.of(2026, 10, 15));
        assertFalse(deadline.occursOn(LocalDate.of(2026, 10, 16)));
        assertFalse(deadline.occursOn(LocalDate.of(2026, 10, 14)));
        assertFalse(deadline.occursOn(null));
    }

    @Test
    public void eventCheckDate_encompassingDates_returnsTrue() throws Exception {
        Event event = new Event("camp",
                LocalDate.of(2026, 10, 15),
                LocalDate.of(2026, 10, 17));

        // Start date
        assertTrue(event.occursOn(LocalDate.of(2026, 10, 15)));
        // Middle date
        assertTrue(event.occursOn(LocalDate.of(2026, 10, 16)));
        // End date
        assertTrue(event.occursOn(LocalDate.of(2026, 10, 17)));
    }

    @Test
    public void eventCheckDate_outOfRangeDates_returnsFalse() throws Exception {
        Event event = new Event("camp",
                LocalDate.of(2026, 10, 15),
                LocalDate.of(2026, 10, 17));

        assertFalse(event.occursOn(LocalDate.of(2026, 10, 14)));
        assertFalse(event.occursOn(LocalDate.of(2026, 10, 18)));
        assertFalse(event.occursOn(null));
    }

    @Test
    public void eventCheckDate_dateOnlyEnd_includesEntireEndDate() throws Exception {
        Event event = new Event("camp",
                LocalDate.of(2026, 10, 15),
                LocalDate.of(2026, 10, 17));

        assertTrue(event.occursOn(LocalDateTime.of(2026, 10, 17, 23, 59)));
        assertFalse(event.occursOn(LocalDate.of(2026, 10, 18)));
    }

    @Test
    public void eventCheckDate_dateTimeEndAtMidnight_excludesEndDate() throws Exception {
        Event event = new Event("overnight event",
                LocalDateTime.of(2026, 10, 15, 20, 0),
                LocalDateTime.of(2026, 10, 16, 0, 0));

        assertTrue(event.occursOn(LocalDate.of(2026, 10, 15)));
        assertFalse(event.occursOn(LocalDate.of(2026, 10, 16)));
    }

    @Test
    public void eventConstructor_endNotAfterStart_throwsInvalidEventParametersException() {
        InvalidEventParametersException sameTimeException = assertThrows(
                InvalidEventParametersException.class, () ->
                new Event("meeting",
                        LocalDateTime.of(2026, 10, 15, 14, 0),
                        LocalDateTime.of(2026, 10, 15, 14, 0)));
        assertEquals("Congratulations, you scheduled time backwards. The event must end after it starts.",
                sameTimeException.getMessage());

        assertThrows(InvalidEventParametersException.class, () ->
                new Event("meeting",
                        LocalDateTime.of(2026, 10, 15, 15, 0),
                        LocalDateTime.of(2026, 10, 15, 14, 0)));
    }
}
