package tt.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TaskTest {

    @Test
    void newTask_isUnmarkedAndFormatsDescription() {
        Task task = new Task("read book");

        assertEquals("read book", task.getTask());
        assertEquals(" ", task.getStatusIcon());
        assertFalse(task.isDone());
        assertEquals("[ ] read book", task.toString());
    }

    @Test
    void markAndUnmark_toggleCompletionState() {
        Task task = new Task("submit report");

        task.mark();
        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());
        assertEquals("[X] submit report", task.toString());

        task.unmark();
        assertFalse(task.isDone());
        assertEquals("[ ] submit report", task.toString());
    }
}
