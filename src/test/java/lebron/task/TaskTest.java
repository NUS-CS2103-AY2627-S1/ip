package lebron.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TaskTest {
    @Test
    public void getDescription_returnsConstructorValue() {
        Task task = new Task("read book");
        assertEquals("read book", task.getDescription());
    }

    @Test
    public void equals_sameInstance_returnsTrue() {
        Task task = new Task("read book");
        assertEquals(task, task);
    }

    @Test
    public void equals_sameDescription_returnsTrue() {
        assertEquals(new Task("read book"), new Task("read book"));
    }

    @Test
    public void equals_differentDescription_returnsFalse() {
        assertNotEquals(new Task("read book"), new Task("write essay"));
    }

    @Test
    public void equals_differentClass_returnsFalse() {
        assertNotEquals(new Task("read book"), new Todo("read book"));
    }

    @Test
    public void equals_null_returnsFalse() {
        assertFalse(new Task("read book").equals(null));
    }

    @Test
    public void hashCode_equalTasks_haveSameHashCode() {
        assertTrue(new Task("read book").hashCode() == new Task("read book").hashCode());
    }

    @Test
    public void toString_newTask_notDoneFormat() {
        Task task = new Task("read book");
        assertEquals("[ ] read book", task.toString());
    }

    @Test
    public void toString_afterSetStatusTrue_doneFormat() {
        Task task = new Task("read book");
        task.setStatus(true);
        assertEquals("[X] read book", task.toString());
    }

    @Test
    public void toString_afterSetStatusTrueThenFalse_notDoneFormat() {
        Task task = new Task("read book");
        task.setStatus(true);
        task.setStatus(false);
        assertEquals("[ ] read book", task.toString());
    }

    @Test
    public void reformat_notDone_correctSaveFormat() {
        Task task = new Task("read book");
        assertEquals("0 | read book", task.reformat());
    }

    @Test
    public void reformat_done_correctSaveFormat() {
        Task task = new Task("read book");
        task.setStatus(true);
        assertEquals("1 | read book", task.reformat());
    }
}
