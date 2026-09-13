package xuan.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class TaskTest {

    @Test
    public void task_newTask_isNotDone() {
        Task task = new Task("read book");

        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void markAsDone_taskBecomesDone() {
        Task task = new Task("read book");

        task.markAsDone();

        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());
    }

    @Test
    public void markAsNotDone_doneTaskBecomesNotDone() {
        Task task = new Task("read book");
        task.markAsDone();

        task.markAsNotDone();

        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void todo_toString_returnsCorrectFormat() {
        Todo todo = new Todo("read book");

        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void deadline_toString_returnsCorrectFormat() {
        Deadline deadline = new Deadline(
                "submit assignment",
                LocalDate.parse("2026-09-20"));

        assertEquals(
                "[D][ ] submit assignment (by: Sep 20 2026)",
                deadline.toString());
    }

    @Test
    public void event_toString_returnsCorrectFormat() {
        Event event = new Event("meeting", "2pm", "4pm");

        assertEquals(
                "[E][ ] meeting (from: 2pm to: 4pm)",
                event.toString());
    }

    @Test
    public void deadline_getBy_returnsDeadlineDate() {
        LocalDate date = LocalDate.parse("2026-09-20");
        Deadline deadline = new Deadline("submit assignment", date);

        assertEquals(date, deadline.getBy());
    }

    @Test
    public void event_getFromAndTo_returnsEventTimes() {
        Event event = new Event("meeting", "2pm", "4pm");

        assertEquals("2pm", event.getFrom());
        assertEquals("4pm", event.getTo());
    }
}
