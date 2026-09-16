package crow.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class TaskTest {
    @Test
    void task_statusChanges_displayReflectsStatus() {
        Task task = new Task("read book");

        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
        assertEquals("[ ] read book", task.toString());

        task.markAsDone();
        assertTrue(task.isDone());
        assertEquals("[X] read book", task.toString());

        task.markAsNotDone();
        assertFalse(task.isDone());
    }

    @Test
    void todo_toString_includesTypeAndStatus() {
        Todo todo = new Todo("borrow book");

        assertEquals("borrow book", todo.getDescription());
        assertEquals("[T][ ] borrow book", todo.toString());
    }

    @Test
    void deadline_toString_formatsDateTime() {
        LocalDateTime deadlineDateTime = LocalDateTime.of(2019, 12, 2, 18, 0);
        Deadline deadline = new Deadline("return book", deadlineDateTime);

        assertEquals(deadlineDateTime, deadline.getDeadlineDateTime());
        assertEquals("[D][ ] return book (by: Dec 02 2019 6:00PM)", deadline.toString());
    }

    @Test
    void event_toString_formatsStartAndEnd() {
        LocalDateTime startDateTime = LocalDateTime.of(2019, 12, 3, 14, 0);
        LocalDateTime endDateTime = LocalDateTime.of(2019, 12, 3, 16, 0);
        Event event = new Event("meeting", startDateTime, endDateTime);

        assertEquals(startDateTime, event.getStartDateTime());
        assertEquals(endDateTime, event.getEndDateTime());
        assertEquals("[E][ ] meeting (from: Dec 03 2019 2:00PM to: Dec 03 2019 4:00PM)",
                event.toString());
    }
}
