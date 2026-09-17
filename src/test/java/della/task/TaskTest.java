package della.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

public class TaskTest {
    @Test
    public void task_markAndUnmarkTodo_statusDisplayedCorrectly() {
        Task task = new Todo("read book");
        assertEquals("[T][ ] read book", task.toString());
        task.mark();
        assertEquals("[T][X] read book", task.toString());
        task.unmark();
        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    public void task_markAndUnmarkDeadline_statusDisplayedCorrectly() {
        Task task = new Deadline("read book", LocalDateTime.of(2030, 9, 9, 21, 30));
        assertEquals("[D][ ] read book (by Sept 09 2030 9:30pm)", task.toString());
        task.mark();
        assertEquals("[D][X] read book (by Sept 09 2030 9:30pm)", task.toString());
        task.unmark();
        assertEquals("[D][ ] read book (by Sept 09 2030 9:30pm)", task.toString());
    }

    @Test
    public void task_markAndUnmarkEvent_statusDisplayedCorrectly() {
        Task task = new Event(
                "read book",
                LocalDateTime.of(2030, 9, 9, 21, 30),
                LocalDateTime.of(2050, 9, 9, 21, 30));
        assertEquals("[E][ ] read book (from: Sept 09 2030 9:30pm to: Sept 09 2050 9:30pm)", task.toString());
        task.mark();
        assertEquals("[E][X] read book (from: Sept 09 2030 9:30pm to: Sept 09 2050 9:30pm)", task.toString());
        task.unmark();
        assertEquals("[E][ ] read book (from: Sept 09 2030 9:30pm to: Sept 09 2050 9:30pm)", task.toString());
    }

    @Test
    public void task_equalTasks_equalAndSameHashCode() {
        Task first = new Todo("read book");
        Task second = new Todo("read book");
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertNotEquals(first, new Deadline("read book", java.time.LocalDateTime.of(2099, 1, 1, 1, 0)));
    }
}
