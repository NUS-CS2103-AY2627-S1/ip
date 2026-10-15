package chocolate.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests task status changes and display forms.
 */
public class TaskTest {
    @Test
    public void taskStatus_markAndUnmark_updatesIcon() {
        Task task = new Task("read book");

        assertFalse(task.isDone());
        assertEquals("[ ] read book", task.toString());
        task.markAsDone();
        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());
        task.markAsUndone();
        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void taskTypes_toString_includeTypeSpecificDetails() {
        Todo todo = new Todo("buy chocolate");
        Deadline deadline = new Deadline("submit report", LocalDate.of(2019, 1, 2));
        Event event = new Event("project meeting", "Mon 2pm", "4pm");

        assertEquals("[T][ ] buy chocolate", todo.toString());
        assertEquals("[D][ ] submit report (by: Jan 02 2019)", deadline.toString());
        assertEquals("[E][ ] project meeting (from: Mon 2pm to: 4pm)", event.toString());
        assertEquals(LocalDate.of(2019, 1, 2), deadline.getBy());
        assertEquals("Mon 2pm", event.getStart());
        assertEquals("4pm", event.getEnd());
    }
}
