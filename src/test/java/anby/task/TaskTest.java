package anby.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TaskTest {
    @Test
    public void constructor_newTask_setsNameAndUndoneStatus() {
        Task task = new Task("eat burger");

        assertEquals("eat burger", task.getName());
        assertFalse(task.isDone());
        assertEquals(0, task.getDoneStatusAsInt());
    }

    @Test
    public void markAsDone_undoneTask_setsDoneStatus() {
        Task task = new Task("eat burger");

        task.markAsDone();

        assertTrue(task.isDone());
        assertEquals(1, task.getDoneStatusAsInt());
    }

    @Test
    public void unmarkAsDone_doneTask_setsUndoneStatus() {
        Task task = new Task("eat burger");

        task.markAsDone();
        task.unmarkAsDone();

        assertFalse(task.isDone());
        assertEquals(0, task.getDoneStatusAsInt());
    }

    @Test
    public void toString_undoneTask_returnsUndoneDisplayString() {
        Task task = new Task("eat burger");

        assertEquals("[ ] eat burger", task.toString());
    }

    @Test
    public void toString_doneTask_returnsDoneDisplayString() {
        Task task = new Task("eat burger");

        task.markAsDone();

        assertEquals("[X] eat burger", task.toString());
    }

    @Test
    public void toFileString_task_returnsStorageString() {
        Task task = new Task("eat burger");

        assertEquals("0 | eat burger", task.toFileString());
    }
}
