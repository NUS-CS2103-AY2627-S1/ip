package jeff.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class TaskAdditionalTest {

    @Test
    public void todo_newAndMarkedTask_displaysExpectedStatus() {
        Todo todo = new Todo("read book");

        assertFalse(todo.isDone());
        assertEquals("[ ] ", todo.getStatus());
        assertEquals("[T][ ]  read book", todo.toString());

        todo.markAsDone();

        assertTrue(todo.isDone());
        assertEquals("[X] ", todo.getStatus());
        assertEquals("[T][X]  read book", todo.toString());

        todo.unmarkAsDone();

        assertFalse(todo.isDone());
    }

    @Test
    public void deadline_toString_formatsDateForDisplay() {
        Deadline deadline = new Deadline(
                "submit report", LocalDate.of(2026, 9, 3));

        assertEquals("submit report", deadline.getDescription());
        assertEquals(LocalDate.of(2026, 9, 3), deadline.getDueDate());
        assertTrue(deadline.toString().startsWith(
                "[D][ ]  submit report (by: "));
        assertTrue(deadline.toString().contains("03"));
        assertTrue(deadline.toString().contains("2026"));
    }

    @Test
    public void event_toStringDisplaysTimeRange() {
        Event event = new Event("meeting", "2pm", "4pm");

        assertEquals("meeting", event.getDescription());
        assertEquals("2pm", event.getStartTime());
        assertEquals("4pm", event.getEndTime());
        assertEquals(
                "[E][ ]  meeting (from: 2pm to: 4pm)",
                event.toString());
    }
}
