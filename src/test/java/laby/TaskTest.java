package laby;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import laby.task.Deadline;
import laby.task.Event;
import laby.task.Task;
import laby.task.Todo;

/** Tests the display and persistence formats of each task type. */
class TaskTest {

    private static final LocalDateTime DEADLINE = LocalDateTime.of(2026, 8, 23, 10, 0);
    private static final LocalDateTime EVENT_START = LocalDateTime.of(2026, 8, 23, 11, 0);
    private static final LocalDateTime EVENT_END = LocalDateTime.of(2026, 8, 23, 12, 0);

    @Test
    void task_doneAndUndone_formatsCompletionState() {
        Task task = new Task("read book", false);

        assertEquals("[ ] read book", task.toString());
        assertEquals("0|read book", task.toFileString());

        task.setDone(true);

        assertEquals("[X] read book", task.toString());
        assertEquals("1|read book", task.toFileString());
        assertEquals("read book", task.getDescription());
    }

    @Test
    void todo_done_formatsWithTodoMarker() {
        Todo todo = new Todo("read book", true);

        assertEquals("[T][X] read book", todo.toString());
        assertEquals("T|1|read book\n", todo.toFileString());
    }

    @Test
    void deadline_done_formatsDateAndStorageRecord() {
        Deadline deadline = new Deadline("return book", DEADLINE, true);

        assertEquals("[D][X] return book (by: Aug 23 2026 10:00)", deadline.toString());
        assertEquals("D|1|return book|2026-08-23 10:00\n", deadline.toFileString());
    }

    @Test
    void event_done_formatsDatesAndStorageRecord() {
        Event event = new Event("project meeting", EVENT_START, EVENT_END, true);

        assertEquals("[E][X] project meeting (from: Aug 23 2026 11:00 to: Aug 23 2026 12:00)",
                event.toString());
        assertEquals("E|1|project meeting|2026-08-23 11:00|2026-08-23 12:00\n",
                event.toFileString());
    }
}
