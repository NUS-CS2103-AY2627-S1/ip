package shan.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class TaskRepresentationTest {
    @Test
    void todo_toStringAndDataString_useExpectedFormats() {
        ToDo task = new ToDo("read book");

        assertEquals("[T][ ] read book", task.toString());
        assertEquals("T | 0 | read book", task.toFileString());

        task.markDone();
        assertEquals("[T][X] read book", task.toString());
        assertEquals("T | 1 | read book", task.toFileString());
    }

    @Test
    void deadline_toStringAndDataString_useHumanAndStorageDateFormats() {
        Deadline task = new Deadline("submit report",
                LocalDateTime.of(2026, 9, 15, 18, 5));

        assertEquals("[D][ ] submit report (by: Sep 15 2026, 6:05 PM)", task.toString());
        assertEquals("D | 0 | submit report | 2026-09-15 18:05", task.toFileString());
        assertFalse(task.occursOn(LocalDate.of(2026, 9, 16)));
    }

    @Test
    void event_toStringAndDataString_useHumanAndStorageDateFormats() {
        Event task = new Event("project meeting",
                LocalDateTime.of(2026, 9, 15, 18, 5),
                LocalDateTime.of(2026, 9, 15, 19, 30));

        assertEquals("[E][ ] project meeting (from: Sep 15 2026, 6:05 PM"
                + " to: Sep 15 2026, 7:30 PM)", task.toString());
        assertEquals("E | 0 | project meeting | 2026-09-15 18:05 | 2026-09-15 19:30",
                task.toFileString());
        assertFalse(task.occursOn(LocalDate.of(2026, 9, 16)));
    }

    @Test
    void constructors_nullRequiredValues_assertionErrorThrown() {
        LocalDateTime dateTime = LocalDateTime.of(2026, 9, 15, 18, 0);

        assertThrows(AssertionError.class, () -> new ToDo(null));
        assertThrows(AssertionError.class, () -> new Deadline("task", null));
        assertThrows(AssertionError.class, () -> new Event("task", null, dateTime));
        assertThrows(AssertionError.class, () -> new Event("task", dateTime, null));
    }
}
