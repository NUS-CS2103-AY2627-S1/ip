package nexus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/** Tests task validation, state changes, snoozing, and display formatting. */
class TaskTest {
    @Test
    void constructor_invalidDescription_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new Todo(null));
        assertThrows(IllegalArgumentException.class, () -> new Todo("  "));
        assertThrows(IllegalArgumentException.class, () -> new Todo("line\nbreak"));
    }

    @Test
    void taskStateAndSnooze_updateDisplayAndVisibility() {
        Task task = new Todo("prepare launch");

        assertEquals("prepare launch", task.getDescription());
        assertEquals(TaskType.TODO, task.getTaskType());
        assertEquals("[T][ ] prepare launch", task.toString());
        assertFalse(task.isSnoozed(LocalDate.of(2026, 9, 15)));

        task.markAsDone();
        assertEquals("X", task.getStatusIcon());
        task.snoozeUntil(LocalDate.of(2026, 9, 20));
        assertTrue(task.isSnoozed(LocalDate.of(2026, 9, 19)));
        assertFalse(task.isSnoozed(LocalDate.of(2026, 9, 20)));

        task.markAsNotDone();
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    void taskTypes_haveExpectedIconsAndFormatting() {
        assertEquals("T", TaskType.TODO.getIcon());
        assertEquals("D", TaskType.DEADLINE.getIcon());
        assertEquals("E", TaskType.EVENT.getIcon());
        assertEquals("[E][ ] meeting (from: 10:00 to: 11:00)",
                new Event("meeting", "10:00", "11:00").toString());
    }
}
