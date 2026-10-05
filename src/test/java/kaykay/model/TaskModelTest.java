package kaykay.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests task state changes, display text, and storage representations. */
class TaskModelTest {

    /** Checks the inherited mark and unmark state transitions. */
    @Test
    void task_markAndUnmark_updatesStatusIcon() {
        Task task = new Task("read notes");

        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
        task.mark();
        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());
        task.unmark();
        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
    }

    /** Checks todo display and storage text in both unfinished and finished states. */
    @Test
    void todo_toStringAndFileFormat_reflectTaskState() {
        Todo todo = new Todo("buy milk");

        assertEquals("[T][ ] buy milk", todo.toString());
        assertEquals("T | 0 | buy milk", todo.toFileFormat());

        todo.mark();
        assertEquals("[T][X] buy milk", todo.toString());
        assertEquals("T | 1 | buy milk", todo.toFileFormat());
    }

    /** Checks deadline and event date/time values in display and storage text. */
    @Test
    void datedTasks_toStringAndFileFormat_includeDateTimes() {
        LocalDateTime from = LocalDateTime.of(2026, 12, 26, 14, 0);
        LocalDateTime to = LocalDateTime.of(2026, 12, 26, 16, 0);
        Deadline deadline = new Deadline("submit report", from);
        Event event = new Event("project meeting", from, to);

        assertEquals("[D][ ] submit report (by: 26 Dec 2026, 14:00)", deadline.toString());
        assertEquals("D | 0 | submit report | 26 12 2026 14:00", deadline.toFileFormat());
        assertEquals("[E][ ] project meeting (from: 26 Dec 2026, 14:00 to: 26 Dec 2026, 16:00)",
                event.toString());
        assertEquals("E | 0 | project meeting | 26 12 2026 14:00 | 26 12 2026 16:00",
                event.toFileFormat());
    }
}
