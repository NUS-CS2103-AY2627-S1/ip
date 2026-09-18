package rene.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Verifies the task entities' display format and status transitions.
 */
class TaskEntityTest {
    @Test
    void todo_displayShowsTypeAndStatusIcons() {
        Todo todo = new Todo("read book");

        assertEquals("[T][ ] read book", todo.toString());
        assertEquals(TaskType.TODO, todo.getTaskType());
        assertFalse(todo.isDone());
    }

    @Test
    void statusTransitions_areReversible() {
        Todo todo = new Todo("read book");

        todo.markAsDone();
        assertTrue(todo.isDone());
        assertEquals("[T][X] read book", todo.toString());

        todo.unmarkAsDone();
        assertFalse(todo.isDone());
        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    void deadline_displayIncludesFormattedDueDate() {
        Deadline deadline = new Deadline("return book", LocalDate.of(2026, 6, 6));

        assertEquals("[D][ ] return book (by: Jun 6 2026)", deadline.toString());
        assertEquals(LocalDate.of(2026, 6, 6), deadline.getDueDate());
        assertEquals(TaskType.DEADLINE, deadline.getTaskType());
    }

    @Test
    void deadline_displayPreservesDateAfterStatusChange() {
        Deadline deadline = new Deadline("submit report", LocalDate.of(2024, 2, 29));
        deadline.markAsDone();

        assertEquals("[D][X] submit report (by: Feb 29 2024)", deadline.toString());
    }

    @Test
    void event_displayIncludesFromAndToValues() {
        Event event = new Event("lecture", "Mon 2pm", "4pm");

        assertEquals("[E][ ] lecture (from: Mon 2pm to: 4pm)", event.toString());
        assertEquals("Mon 2pm", event.getFrom());
        assertEquals("4pm", event.getTo());
        assertEquals(TaskType.EVENT, event.getTaskType());
    }

    @Test
    void description_isPreservedExactly() {
        Task task = new Todo("  spaced  and  mixed Case  ");

        assertEquals("  spaced  and  mixed Case  ", task.getDescription());
        assertTrue(task.toString().contains("  spaced  and  mixed Case  "));
    }
}
