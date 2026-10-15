package jelly.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests task state, tags, and display formatting. */
class TaskTest {
    @Test
    void task_initialStateAndDisplay_areCorrect() {
        Task task = new Todo("buy milk");

        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
        assertEquals("[T][ ] buy milk", task.toString());
        assertFalse(task.hasTag());
        assertNull(task.getTag());
    }

    @Test
    void task_markingAndTags_updateStateAndDisplay() {
        Task task = new Todo("buy milk");

        task.markAsDone();
        task.setTag("urgent_1");
        assertTrue(task.isDone());
        assertEquals("[T][X] buy milk   {#urgent_1}", task.toString());

        task.markAsNotDone();
        task.removeTag();
        assertFalse(task.isDone());
        assertEquals("[T][ ] buy milk", task.toString());
    }

    @Test
    void task_invalidDescriptionsAndTags_areRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Todo(null));
        assertThrows(IllegalArgumentException.class, () -> new Todo("bad | data"));
        Task task = new Todo("valid");

        assertThrows(IllegalArgumentException.class, () -> task.setTag("bad tag"));
        assertThrows(IllegalArgumentException.class, () -> task.setTag("#tag"));
        assertThrows(IllegalArgumentException.class, () -> task.setTag(null));
    }

    @Test
    void deadlineAndEvent_displayDetails() {
        LocalDateTime from = LocalDateTime.of(2026, 1, 2, 9, 30);
        LocalDateTime to = from.plusHours(2);

        assertEquals("[D][ ] report (by: Jan 02 2026 09:30)",
                new Deadline("report", from).toString());
        assertEquals("[E][ ] meeting (from: Jan 02 2026 09:30 to: Jan 02 2026 11:30)",
                new Event("meeting", from, to).toString());
        assertThrows(IllegalArgumentException.class, () -> new Event("meeting", null, to));
        assertThrows(IllegalArgumentException.class, () -> new Event("meeting", from, from));
    }
}
