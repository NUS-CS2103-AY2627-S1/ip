package nudge.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests task state, accessors, and display formatting.
 */
class TaskTest {
    @Test
    void markAndUnmark_task_updatesCompletionState() {
        Task task = new Task("read book");

        task.markAsDone();
        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());

        task.markAsNotDone();
        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    void toString_todo_formatsTypeStatusAndDescription() {
        Todo todo = new Todo("read book");

        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    void toString_deadline_formatsTypeStatusDescriptionAndDate() {
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 9, 20));

        assertEquals(LocalDate.of(2026, 9, 20), deadline.getDueDate());
        assertEquals("[D][ ] submit report (by: Sep 20 2026)", deadline.toString());
    }

    @Test
    void toString_event_formatsTypeStatusDescriptionAndDates() {
        Event event = new Event("project meeting", LocalDate.of(2026, 9, 20),
                LocalDate.of(2026, 9, 21));

        assertEquals(LocalDate.of(2026, 9, 20), event.getStartDate());
        assertEquals(LocalDate.of(2026, 9, 21), event.getEndDate());
        assertEquals("[E][ ] project meeting (from: Sep 20 2026 to: Sep 21 2026)",
                event.toString());
    }
}
