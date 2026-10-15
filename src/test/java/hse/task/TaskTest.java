package hse.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class TaskTest {
    @Test
    void todo_markAndUnmark_changesCompletionDisplay() {
        ToDos todo = new ToDos("read book");

        assertEquals("[T][ ] read book", todo.toString());
        todo.markAsDone();
        assertEquals("[T][X] read book", todo.toString());
        todo.markAsNotDone();
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    void deadline_isOn_matchesOnlyItsDueDate() {
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 9, 3).atTime(14, 0));

        assertTrue(deadline.isOn(LocalDate.of(2026, 9, 3)));
        assertFalse(deadline.isOn(LocalDate.of(2026, 9, 4)));
    }

    @Test
    void event_occursOn_includesStartEndAndDatesBetween() {
        Event event = new Event("camp", LocalDateTime.of(2026, 9, 3, 18, 0),
                LocalDateTime.of(2026, 9, 5, 10, 0));

        assertTrue(event.occursOn(LocalDate.of(2026, 9, 3)));
        assertTrue(event.occursOn(LocalDate.of(2026, 9, 4)));
        assertTrue(event.occursOn(LocalDate.of(2026, 9, 5)));
        assertFalse(event.occursOn(LocalDate.of(2026, 9, 2)));
        assertFalse(event.occursOn(LocalDate.of(2026, 9, 6)));
    }

    @Test
    void event_endBeforeStart_throwsException() {
        LocalDateTime start = LocalDateTime.of(2026, 9, 5, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 9, 4, 10, 0);

        assertThrows(IllegalArgumentException.class, () -> new Event("camp", start, end));
    }

    @Test
    void event_endTimeBeforeStartOnSameDate_throwsException() {
        LocalDateTime start = LocalDateTime.of(2026, 9, 5, 10, 0);
        LocalDateTime end = LocalDateTime.of(2026, 9, 5, 9, 59);

        assertThrows(IllegalArgumentException.class, () -> new Event("camp", start, end));
    }

    @Test
    void event_endAtSameTimeAsStart_throwsException() {
        LocalDateTime start = LocalDateTime.of(2026, 9, 5, 10, 0);

        assertThrows(IllegalArgumentException.class, () -> new Event("camp", start, start));
    }
}
