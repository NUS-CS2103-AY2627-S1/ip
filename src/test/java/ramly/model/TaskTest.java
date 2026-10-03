package ramly.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Tests the observable completion status of a task. */
public class TaskTest {
    @Test
    public void getStatusIcon_newTask_returnsIncompleteIcon() {
        Task task = new Todo("read a book");

        assertEquals("[ ]", task.getStatusIcon());
    }

    @Test
    public void getStatusIcon_markedTask_returnsCompleteIcon() {
        Task task = new Todo("read a book");
        task.mark();

        assertEquals("[✓]", task.getStatusIcon());
    }

    @Test
    public void getStatusIcon_unmarkedTask_returnsIncompleteIcon() {
        Task task = new Todo("read a book");
        task.mark();
        task.unmark();

        assertEquals("[ ]", task.getStatusIcon());
    }

    @Test
    public void getType_todoTask_returnsTodoType() {
        Task task = new Todo("read a book");

        assertEquals(TaskType.TODO, task.getType());
    }

    @Test
    public void toFileString_newTask_returnsUncompletedStorageFormat() {
        Task task = new Todo("read a book");

        assertEquals("T | 0 | read a book", task.toFileString());
    }

    @Test
    public void toFileString_markedTask_returnsCompletedStorageFormat() {
        Task task = new Todo("read a book");
        task.mark();

        assertEquals("T | 1 | read a book", task.toFileString());
    }

    @Test
    public void toString_newTask_returnsFormattedDisplayText() {
        Task task = new Todo("read a book");

        assertEquals("[•][ ] read a book", task.toString());
    }

    @Test
    public void toString_markedTask_returnsFormattedCompletedDisplayText() {
        Task task = new Todo("read a book");
        task.mark();

        assertEquals("[•][✓] read a book", task.toString());
    }

    @Test
    public void constructor_nullDescription_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Task(null, TaskType.TODO));
    }

    @Test
    public void constructor_nullType_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Task("read a book", null));
    }
}
