package wiz;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

public class TaskListTest {

    @Test
    public void constructor_varargsTasks_tasksAddedCorrectly() {
        Task task1 = new ToDo("read book");
        Task task2 = new ToDo("write code");
        TaskList taskList = new TaskList(task1, task2);

        assertEquals(2, taskList.size());
        assertEquals(task1, taskList.get(0));
        assertEquals(task2, taskList.get(1));
    }

    @Test
    public void add_varargsTasks_tasksAddedCorrectly() {
        TaskList taskList = new TaskList();
        Task task1 = new ToDo("task 1");
        Task task2 = new ToDo("task 2");
        taskList.add(task1, task2);

        assertEquals(2, taskList.size());
        assertEquals(task1, taskList.get(0));
        assertEquals(task2, taskList.get(1));
    }

    @Test
    public void delete_validIndex_taskRemoved() {
        Task task1 = new ToDo("task 1");
        Task task2 = new ToDo("task 2");
        TaskList taskList = new TaskList(task1, task2);

        Task deleted = taskList.delete(0);
        assertEquals(task1, deleted);
        assertEquals(1, taskList.size());
        assertEquals(task2, taskList.get(0));
    }

    @Test
    public void find_matchingKeyword_returnsMatchingTasks() {
        Task task1 = new ToDo("read book");
        Task task2 = new ToDo("write book chapter");
        Task task3 = new ToDo("buy groceries");
        TaskList taskList = new TaskList(task1, task2, task3);

        ArrayList<Task> results = taskList.find("book");
        assertEquals(2, results.size());
        assertEquals(task1, results.get(0));
        assertEquals(task2, results.get(1));

        ArrayList<Task> caseInsensitiveResults = taskList.find("BOOK");
        assertEquals(2, caseInsensitiveResults.size());

        ArrayList<Task> emptyResults = taskList.find("nonexistent");
        assertEquals(0, emptyResults.size());
    }

    @Test
    public void hasDuplicate_matchingTask_returnsTrue() {
        Task task1 = new ToDo("read book");
        TaskList taskList = new TaskList(task1);

        assertEquals(true, taskList.hasDuplicate(new ToDo("read book")));
        assertEquals(true, taskList.hasDuplicate(new ToDo("READ BOOK")));
        assertEquals(false, taskList.hasDuplicate(new ToDo("other task")));
    }
}
