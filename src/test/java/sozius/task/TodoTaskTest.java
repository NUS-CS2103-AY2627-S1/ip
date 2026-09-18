package sozius.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TodoTaskTest {

    @Test
    public void constructor_newTask_notDone() {
        TodoTask task = new TodoTask("read book");
        assertFalse(task.isDone());
        assertEquals("read book", task.getDescription());
    }

    @Test
    public void toUserString_notDone_formatsCorrectly() {
        TodoTask task = new TodoTask("read book");
        assertEquals("[T][ ] read book", task.toUserString());
    }

    @Test
    public void toUserString_done_formatsCorrectly() {
        TodoTask task = new TodoTask("read book");
        task.setDone(true);
        assertEquals("[T][X] read book", task.toUserString());
    }

    @Test
    public void toFileString_notDone_formatsCorrectly() {
        TodoTask task = new TodoTask("read book");
        assertEquals("T | 0 | read book", task.toFileString());
    }

    @Test
    public void toFileString_done_formatsCorrectly() {
        TodoTask task = new TodoTask("read book");
        task.setDone(true);
        assertEquals("T | 1 | read book", task.toFileString());
    }

    @Test
    public void setDone_toggle_updatesFlag() {
        TodoTask task = new TodoTask("read book");
        task.setDone(true);
        assertTrue(task.isDone());
        task.setDone(false);
        assertFalse(task.isDone());
    }
}
