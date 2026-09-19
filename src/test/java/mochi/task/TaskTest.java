package mochi.task;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests the behavior and representations of each task type.
 */
public class TaskTest {
    private static final LocalDateTime START = LocalDateTime.of(2026, 8, 6, 14, 0);
    private static final LocalDateTime END = LocalDateTime.of(2026, 8, 6, 16, 0);

    @Test
    public void task_markAndUnmark_updatesStatusAndDataString() {
        Task task = new Task("read book");

        assertAll(
                () -> assertEquals(" ", task.getStatusIcon()),
                () -> assertEquals("read book", task.getDescription()),
                () -> assertEquals("[ ] read book", task.toString()),
                () -> assertEquals("T | 0 | read book", task.toDataString())
        );

        task.mark();
        assertAll(
                () -> assertEquals("X", task.getStatusIcon()),
                () -> assertEquals("[X] read book", task.toString()),
                () -> assertEquals("T | 1 | read book", task.toDataString())
        );

        task.unmark();
        assertEquals("T | 0 | read book", task.toDataString());
    }

    @Test
    public void todo_newTodo_returnsTodoDisplayAndDataStrings() {
        Todo todo = new Todo("read book");

        assertAll(
                () -> assertEquals("[T][ ] read book", todo.toString()),
                () -> assertEquals("T | 0 | read book", todo.toDataString())
        );
    }

    @Test
    public void deadline_newDeadline_returnsDeadlineDisplayAndDataStrings() {
        Deadline deadline = new Deadline("submit report", END);

        assertAll(
                () -> assertEquals("[D][ ] submit report (by: Aug 06 2026, 4:00 PM)",
                        deadline.toString()),
                () -> assertEquals("D | 0 | submit report | 2026-08-06 1600",
                        deadline.toDataString())
        );
    }

    @Test
    public void event_newEvent_returnsEventDisplayAndDataStrings() {
        Event event = new Event("project meeting", START, END);

        assertAll(
                () -> assertEquals(
                        "[E][ ] project meeting (from: Aug 06 2026, 2:00 PM "
                                + "to: Aug 06 2026, 4:00 PM)",
                        event.toString()),
                () -> assertEquals(
                        "E | 0 | project meeting | 2026-08-06 1400 | 2026-08-06 1600",
                        event.toDataString())
        );
    }

    @Test
    public void event_endNotAfterStart_exceptionThrown() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new Event("meeting", START, START)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new Event("meeting", START, START.minusMinutes(1)))
        );
    }
}
