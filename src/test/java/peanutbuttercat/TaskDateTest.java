package peanutbuttercat;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests whether task schedules include a specified date. */
class TaskDateTest {
    @Test
    void occursOn_deadlineDueDate_returnsTrue() {
        Deadline deadline = new Deadline("submit report", LocalDateTime.of(2026, 9, 9, 18, 0));

        assertTrue(deadline.occursOn(LocalDate.of(2026, 9, 9)));
        assertFalse(deadline.occursOn(LocalDate.of(2026, 9, 10)));
    }

    @Test
    void occursOn_eventDateRange_includesBothBoundaries() {
        Event event = new Event("conference",
                LocalDateTime.of(2026, 9, 9, 18, 0),
                LocalDateTime.of(2026, 9, 11, 9, 0));

        assertTrue(event.occursOn(LocalDate.of(2026, 9, 9)));
        assertTrue(event.occursOn(LocalDate.of(2026, 9, 10)));
        assertTrue(event.occursOn(LocalDate.of(2026, 9, 11)));
        assertFalse(event.occursOn(LocalDate.of(2026, 9, 12)));
    }

    @Test
    void occursOn_todo_returnsFalse() {
        Todo todo = new Todo("buy milk");

        assertFalse(todo.occursOn(LocalDate.of(2026, 9, 9)));
    }

    @Test
    void constructor_nonPositiveEventDuration_exceptionThrown() {
        LocalDateTime startTime = LocalDateTime.of(2026, 9, 9, 18, 0);

        assertThrows(IllegalArgumentException.class, () ->
                new Event("zero duration", startTime, startTime));
        assertThrows(IllegalArgumentException.class, () ->
                new Event("negative duration", startTime, startTime.minusMinutes(1)));
    }
}
