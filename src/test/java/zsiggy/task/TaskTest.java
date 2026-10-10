package zsiggy.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TaskTest {

    @Test
    public void toString_unmarkedTask_returnsCorrectString() {
        Task task = new Task("read book");

        assertEquals("[ ] read book", task.toString());
    }

    @Test
    public void mark_taskMarked_returnsCorrectString() {
        Task task = new Task("read book");

        task.mark();

        assertEquals("[X] read book", task.toString());
    }

    @Test
    public void unmark_markedTask_returnsCorrectString() {
        Task task = new Task("read book");

        task.mark();
        task.unmark();

        assertEquals("[ ] read book", task.toString());
    }
}
