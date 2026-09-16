package puyo.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@code Task}.
 */
public class TaskTest {

    /**
     * Dummy implementation of {@code Task} for testing purposes.
     */
    private static class DummyTask extends Task {
        public DummyTask(String name) {
            super(name, TaskType.TODO);
        }
    }

    @Test
    public void constructor_trimsTaskName() {
        Task task = new DummyTask("   read book   ");
        assertEquals("read book", task.getName());
    }

    @Test
    public void markDone_unmarkedTask_changesDoneToTrue() {
        Task task = new DummyTask("read book");
        assertFalse(task.getDone());

        task.markDone();
        assertTrue(task.getDone());
    }

    @Test
    public void unmark_markedTask_changesDoneToFalse() {
        Task task = new DummyTask("read book");
        task.markDone();
        assertTrue(task.getDone());

        task.unmark();
        assertFalse(task.getDone());
    }

    @Test
    public void toFileString_unmarkedTask_returnsCorrectFormat() {
        Task task = new DummyTask("read book");
        assertEquals("T | 0 | read book", task.toFileString());
    }

    @Test
    public void toFileString_markedTask_returnsCorrectFormat() {
        Task task = new DummyTask("read book");
        task.markDone();
        assertEquals("T | 1 | read book", task.toFileString());
    }

    @Test
    public void toString_unmarkedTask_returnsCorrectFormat() {
        Task task = new DummyTask("read book");
        assertEquals("[T][X] read book", task.toString());
    }

    @Test
    public void toString_markedTask_returnsCorrectFormat() {
        Task task = new DummyTask("read book");
        task.markDone();
        assertEquals("[T][✓] read book", task.toString());
    }
}
