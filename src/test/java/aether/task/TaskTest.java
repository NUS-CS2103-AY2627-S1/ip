package aether.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class TaskTest {
    @Test
    void taskStatusChangesAndBaseTaskHasNoSortDate() {
        Task task = new Task("read book");

        assertEquals("[ ] read book", task.toString());
        assertEquals(TaskStatus.PENDING, task.getStatus());
        assertFalse(task.getSortDate().isPresent());

        task.markAsDone();
        assertEquals("[X] read book", task.toString());

        task.markAsNotDone();
        assertEquals("[ ] read book", task.toString());

        task.setStatus(TaskStatus.COMPLETED);
        assertEquals("X", task.getStatusIcon());
    }

    @Test
    void taskConstructorsRejectMissingRequiredValues() {
        assertThrows(NullPointerException.class, () -> new Task(null));
        assertThrows(NullPointerException.class, () -> new Task("task", null));
        assertThrows(AssertionError.class, () -> new Task("task").setStatus(null));
    }

    @Test
    void taskSubtypesExposeTheirIconsDatesAndReadableText() {
        LocalDate dueDate = LocalDate.of(2026, 9, 20);
        LocalDate startDate = LocalDate.of(2026, 9, 21);
        LocalDate endDate = LocalDate.of(2026, 9, 22);
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("submit", dueDate);
        Event event = new Event("camp", startDate, endDate);

        assertEquals("[T][ ] read book", todo.toString());
        assertEquals("[D][ ] submit (by: Sep 20 2026)", deadline.toString());
        assertEquals("[E][ ] camp (from: Sep 21 2026 to: Sep 22 2026)", event.toString());
        assertEquals(dueDate, deadline.getBy());
        assertEquals(dueDate, deadline.getSortDate().orElseThrow());
        assertEquals(startDate, event.getFrom());
        assertEquals(endDate, event.getTo());
        assertEquals(startDate, event.getSortDate().orElseThrow());
        assertEquals("T", TaskType.TODO.getIcon());
        assertEquals("D", TaskType.DEADLINE.getIcon());
        assertEquals("E", TaskType.EVENT.getIcon());
        assertTrue(deadline.hasSameDetails(new Deadline("SUBMIT", dueDate)));
        assertFalse(deadline.hasSameDetails(new Todo("submit")));
    }
}
