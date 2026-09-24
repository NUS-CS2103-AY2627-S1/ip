package aravii;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

import org.junit.jupiter.api.Test;

/**
 * Tests polymorphic task behavior, date display, and locale-independent search.
 */
class TaskTest {
    @Test
    void markAndUnmark_updatesCompletionStatus() {
        Task task = new Todo("read notes");
        assertEquals("[T] [ ] read notes", task.toString());
        task.mark();
        assertTrue(task.isCompleted());
        assertEquals("[T] [X] read notes", task.toString());
        task.unmark();
        assertFalse(task.isCompleted());
    }

    @Test
    void deadline_storesDateAndDisplaysReadableFormat() {
        Deadline task = new Deadline("Submit report", LocalDate.of(2026, 9, 5));
        assertEquals(LocalDate.of(2026, 9, 5), task.getDueDate());
        assertEquals("[D] [ ] Submit report (by: Sep 5 2026)", task.toString());
        assertTrue(task.matches("SUBMIT"));
        assertTrue(task.matches("2026-09-05"));
        assertTrue(task.matches("sep 5"));
        assertFalse(task.matches("meeting"));
    }

    @Test
    void event_storesTimesAndRejectsReversedRange() {
        LocalDateTime start = LocalDateTime.of(2026, 9, 21, 14, 0);
        Event task = new Event("meeting", start, start.plusHours(1));
        assertEquals(start, task.getStart());
        assertEquals(start.plusHours(1), task.getEnd());
        assertEquals("[E] [ ] meeting (from: Sep 21 2026, 14:00 to: Sep 21 2026, 15:00)", task.toString());
        assertThrows(IllegalArgumentException.class, () -> new Event("meeting", start, start.minusMinutes(1)));
    }

    @Test
    void matches_turkishLocale_isIndependentOfSystemLocale() {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            assertTrue(new Todo("IMPORTANT").matches("important"));
        } finally {
            Locale.setDefault(original);
        }
    }

    @Test
    void constructors_rejectInvalidArguments() {
        assertThrows(IllegalArgumentException.class, () -> new Todo(null));
        assertThrows(IllegalArgumentException.class, () -> new Deadline("description", null));
        assertThrows(IllegalArgumentException.class, () -> new Event("description", null, LocalDateTime.now()));
        assertThrows(IllegalArgumentException.class, () -> new TaskList().add((Task) null));
    }

    @Test
    void constructor_invalidDescription_rejectsInput() {
        assertThrows(IllegalArgumentException.class, () -> new Todo(" "));
        assertThrows(IllegalArgumentException.class, () -> new Todo("one\ttwo"));
        assertThrows(IllegalArgumentException.class, () -> new Todo("one\ntwo"));
    }
}
