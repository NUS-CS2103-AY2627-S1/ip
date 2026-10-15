package yourhand.tasks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests for task state changes and common task representations. */
class TaskTest {

    @Test
    public void newTodo_isIncompleteAndUsesTodoRepresentations() {
        Task task = new Todo("read book");

        assertEquals(" ", task.getStatusIcon());
        assertEquals("[T][ ] read book", task.toString());
        assertEquals("T | 0 | read book", task.toFileString());
    }

    @Test
    public void markAsDone_incompleteTask_marksTaskAndReportsChange() {
        Task task = new Todo("read book");

        assertTrue(task.markAsDone());
        assertEquals("X", task.getStatusIcon());
        assertEquals("T | 1 | read book", task.toFileString());
        assertFalse(task.markAsDone());
    }

    @Test
    public void markAsUndone_doneTask_marksTaskUndoneAndReportsChange() {
        Task task = new Todo("read book");
        task.markAsDone();

        assertTrue(task.markAsUndone());
        assertEquals(" ", task.getStatusIcon());
        assertFalse(task.markAsUndone());
    }

    @Test
    public void task_invalidDescription_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Todo(null));
        assertThrows(IllegalArgumentException.class, () -> new Todo("   "));
        assertThrows(IllegalArgumentException.class, () -> new Todo("contains | delimiter"));
        assertThrows(IllegalArgumentException.class, () -> new Todo("contains\nnewline"));
    }

    @Test
    public void deadline_missingDate_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Deadline("report", null));
    }

    @Test
    public void event_invalidRangeOrDate_throwsIllegalArgumentException() {
        TaskDateTime start = new TaskDateTime(java.time.LocalDate.of(2026, 9, 10));
        TaskDateTime end = new TaskDateTime(java.time.LocalDate.of(2026, 9, 10));

        assertThrows(IllegalArgumentException.class, () -> new Event("meeting", start, end));
        assertThrows(IllegalArgumentException.class, () -> new Event("meeting", null, end));
    }
}
