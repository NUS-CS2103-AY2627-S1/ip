package chim.task;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TaskListDuplicateTest {

    @Test
    public void isDuplicate_identicalTodoExists_returnsTrue() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));
        assertTrue(taskList.isDuplicate(new Todo("read book")));
    }

    @Test
    public void isDuplicate_differentDescription_returnsFalse() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));
        assertFalse(taskList.isDuplicate(new Todo("write essay")));
    }
}
