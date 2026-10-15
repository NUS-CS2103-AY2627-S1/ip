package tasklist;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class TaskTest {

    @Test
    public void constructor_validInput_success() {
        Task task = new Task("read book");
        assertNotNull(task);
        assertEquals(Task.TaskType.TODO, task.getCategory());
    }

    @Test
    public void setDone_unmarkedTask_setsDoneToTrue() {
        Task task = new Task("read book");
        task.setDone();
        assertEquals("[T] [X] read book", task.toString());
    }

    @Test
    public void setUndone_markedTask_setsDoneToFalse() {
        Task task = new Task("read book");
        task.setDone();
        task.setUndone();
        assertEquals("[T] [ ] read book", task.toString());
    }

    @Test
    public void toString_unmarkedTask_correctFormat() {
        Task task = new Task("read book");
        assertEquals("[T] [ ] read book", task.toString());
    }

    @Test
    public void toString_markedTask_correctFormat() {
        Task task = new Task("read book");
        task.setDone();
        assertEquals("[T] [X] read book", task.toString());
    }

    @Test
    public void fileFormatText_todoTask_correctFormat() {
        Task task = new Task("read book");
        assertEquals("T | read book", task.fileFormatText());
    }

    @Test
    public void getCategory_todoTask_returnsTodoCategory() {
        Task task = new Task("read book");
        assertEquals(Task.TaskType.TODO, task.getCategory());
    }
}
