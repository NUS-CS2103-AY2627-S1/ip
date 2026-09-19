package bob;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/** Tests the task value objects and their storage representations. */
public class TaskTest {
    @Test
    public void task_blankDescription_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new Task("  "));
        assertThrows(IllegalArgumentException.class, () -> new Task(null));
    }

    @Test
    public void task_markAndUnmark_updatesStateAndOutput() {
        Task task = new Task("  Buy milk  ");

        assertEquals("Buy milk", task.getItem());
        assertFalse(task.isDone());
        assertEquals("T | 0 | Buy milk", task.toStorageString());

        task.markDone();
        assertTrue(task.isDone());
        assertEquals("[T][X] Buy milk", task.toString());
        assertEquals("T | 1 | Buy milk", task.toStorageString());

        task.markUndone();
        assertFalse(task.isDone());
    }

    @Test
    public void deadlineAndEvent_renderAndPersistDates() {
        Deadline deadline = new Deadline("Submit report", LocalDate.of(2026, 9, 1));
        Event event = new Event("Conference", LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 2));

        assertEquals("[D][ ] Submit report (by: Sept 01 2026)", deadline.toString());
        assertEquals("D | 0 | Submit report | 2026-09-01", deadline.toStorageString());
        assertEquals("[E][ ] Conference (from: Sept 01 2026 to: Sept 02 2026)", event.toString());
        assertEquals("E | 0 | Conference | 2026-09-01 | 2026-09-02", event.toStorageString());
    }

    @Test
    public void event_invalidDateRange_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Event("Event", LocalDate.of(2026, 9, 2), LocalDate.of(2026, 9, 1)));
        assertThrows(IllegalArgumentException.class,
                () -> new Event("Event", null, LocalDate.of(2026, 9, 1)));
        assertThrows(IllegalArgumentException.class,
                () -> new Deadline("Deadline", null));
    }
}
