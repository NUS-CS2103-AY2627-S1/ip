package happy.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import happy.exception.HappyException;

/**
 * Unit tests for the TaskList class.
 */
public class TaskListTest {

    @Test
    public void delete_validIndex_removesAndReturnsTask() throws HappyException {
        TaskList taskList = new TaskList();
        Task task1 = new Todo("read principles of software engineering");
        Task task2 = new Todo("return book");
        taskList.add(task1);
        taskList.add(task2);

        Task removedTask = taskList.delete(0);

        assertEquals(task1, removedTask);
        assertEquals(1, taskList.size());
        assertEquals(task2, taskList.get(0));
    }

    @Test
    public void delete_invalidIndex_throwsHappyException() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));
        assertThrows(HappyException.class, () -> taskList.delete(5));
    }

    @Test
    public void markAndUnmark_validIndex_updatesStatus() throws HappyException {
        TaskList taskList = new TaskList();
        Task task = new Todo("do homework");
        taskList.add(task);

        taskList.mark(0);
        assertEquals("X", task.getStatusIcon());

        taskList.unmark(0);
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void setPriority_validIndex_updatesPriority() throws HappyException {
        TaskList taskList = new TaskList();
        Task task = new Todo("prepare presentation");
        taskList.add(task);

        taskList.setPriority(0, Priority.HIGH);
        assertEquals(Priority.HIGH, task.getPriority());
    }

    @Test
    public void isDuplicate_matchingTaskExists_returnsTrue() {
        TaskList taskList = new TaskList();
        Task task = new Todo("read book");
        taskList.add(task);

        assertTrue(taskList.isDuplicate(new Todo("read book")));
        assertFalse(taskList.isDuplicate(new Todo("write code")));
    }

    @Test
    public void find_matchingKeyword_returnsMatchingTasks() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("buy milk"));
        taskList.add(new Todo("read book"));
        taskList.add(new Todo("buy bread"));

        ArrayList<Task> results = taskList.find("buy");
        assertEquals(2, results.size());
    }
}
