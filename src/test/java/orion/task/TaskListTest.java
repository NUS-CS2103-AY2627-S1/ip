package orion.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class TaskListTest {
    @Test
    public void removeTask_validIndex_success() {
        TaskList taskList = new TaskList();
        taskList.addTask(new Todo("read book"));
        assertEquals("[T][ ] read book", taskList.removeTask(0).toString());
        assertEquals(0, taskList.getSize());
    }

    @Test
    public void removeTask_invalidIndex_exceptionThrown() {
        TaskList taskList = new TaskList();
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.removeTask(0));
    }

    @Test
    public void findTasks_existingKeyword_success() {
        TaskList taskList = new TaskList();
        taskList.addTask(new Todo("read book"));
        taskList.addTask(new Todo("return book"));
        taskList.addTask(new Todo("do homework"));
        assertEquals(2, taskList.findTasks("book").size());
    }
}
