package nabmak.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link Task} and its subclasses.
 */
public class TaskTest {

    @Test
    public void markDone_thenMarkNotDone() {
        Task task = new ToDo("read book");
        assertEquals("[ ]", task.getStatus());

        task.markDone();
        assertEquals("[X]", task.getStatus());
        assertEquals("[To Do][X] read book", task.toString());

        task.markNotDone();
        assertEquals("[ ]", task.getStatus());
    }

    @Test
    public void deadlineToString_formatsDate() {
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2026, 12, 1, 18, 0));
        assertEquals("[Deadline][ ] return book (by: Dec 01 2026 18:00)", deadline.toString());
    }

    @Test
    public void eventToString_formatsStartAndEnd() {
        Event event = new Event("camp",
                LocalDateTime.of(2026, 12, 1, 9, 0), LocalDateTime.of(2026, 12, 2, 17, 0));
        assertEquals("[Event][ ] camp (from: Dec 01 2026 09:00 to: Dec 02 2026 17:00)",
                event.toString());
    }

}
