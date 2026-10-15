package tangent.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class TaskTest {
    @Test
    public void task_newTaskIsIncompleteAndHasDescription() {
        Task task = new Task("read book");

        assertEquals("read book", task.getDescription());
        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
        assertEquals("[ ] read book", task.toString());
    }

    @Test
    public void task_markingDoneAndUndone_updatesStatusAndIcon() {
        Task task = new Task("read book");

        task.markAsDone();
        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());
        assertEquals("[X] read book", task.toString());

        task.markAsUndone();
        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void todo_toString_includesTodoType() {
        assertEquals("[T][ ] read book", new ToDo("read book").toString());
    }

    @Test
    public void deadline_getterAndToString_returnDeadlineDetails() {
        LocalDateTime by = LocalDateTime.of(2019, 12, 2, 18, 0);
        Deadline deadline = new Deadline("return book", by);

        assertEquals(by, deadline.getBy());
        assertEquals("[D][ ] return book (by: Dec 02 2019, 6:00PM)", deadline.toString());
    }

    @Test
    public void event_gettersAndToString_returnEventDetails() {
        LocalDateTime from = LocalDateTime.of(2019, 12, 3, 9, 0);
        LocalDateTime to = LocalDateTime.of(2019, 12, 3, 11, 0);
        Event event = new Event("project meeting", from, to);

        assertEquals(from, event.getFrom());
        assertEquals(to, event.getTo());
        assertEquals("[E][ ] project meeting (from: Dec 03 2019, 9:00AM to: Dec 03 2019, 11:00AM)",
                event.toString());
    }
}
