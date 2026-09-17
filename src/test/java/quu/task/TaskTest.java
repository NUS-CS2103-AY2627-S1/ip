package quu.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests the status handling and rendering shared by every kind of {@link Task}.
 */
public class TaskTest {

    @Test
    public void toString_newTask_showsEmptyStatusBox() {
        assertEquals("[ ] read book", new Task("read book").toString());
    }

    @Test
    public void mark_thenToString_showsTickedStatusBox() {
        Task task = new Task("read book");
        task.mark();
        assertEquals("[X] read book", task.toString());
    }

    @Test
    public void unmark_afterMark_clearsStatusBox() {
        Task task = new Task("read book");
        task.mark();
        task.unmark();
        assertEquals("[ ] read book", task.toString());
    }

    @Test
    public void toFileString_encodesStatusAsZeroOrOne() {
        Task task = new Task("read book");
        assertEquals("| 0 | read book", task.toFileString());
        task.mark();
        assertEquals("| 1 | read book", task.toFileString());
    }
}
