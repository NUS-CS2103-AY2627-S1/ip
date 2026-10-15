package walle.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

public class TaskListTest {

    @Test
    public void add_singleTask_sizeIncreasesAndTaskRetrievable() {
        TaskList taskList = new TaskList();
        Task task = new ToDo("read book");

        taskList.add(task);

        assertEquals(1, taskList.size());
        assertSame(task, taskList.get(1));
    }

    @Test
    public void get_multipleTasks_returnsTaskAtOneBasedPosition() {
        TaskList taskList = new TaskList();
        Task first = new ToDo("first task");
        Task second = new ToDo("second task");
        taskList.add(first);
        taskList.add(second);

        assertSame(first, taskList.get(1));
        assertSame(second, taskList.get(2));
    }

    @Test
    public void mark_validIndex_taskMarkedDone() {
        TaskList taskList = new TaskList();
        taskList.add(new ToDo("read book"));

        Task marked = taskList.mark(1);

        assertTrue(marked.isChecked());
        assertTrue(taskList.get(1).isChecked());
    }

    @Test
    public void unmark_previouslyMarkedTask_taskMarkedNotDone() {
        TaskList taskList = new TaskList();
        taskList.add(new ToDo("read book"));
        taskList.mark(1);

        Task unmarked = taskList.unmark(1);

        assertFalse(unmarked.isChecked());
        assertFalse(taskList.get(1).isChecked());
    }

    @Test
    public void delete_firstOfTwoTasks_removesTaskAndShiftsRemaining() {
        TaskList taskList = new TaskList();
        Task first = new ToDo("first task");
        Task second = new ToDo("second task");
        taskList.add(first);
        taskList.add(second);

        Task deleted = taskList.delete(1);

        assertSame(first, deleted);
        assertEquals(1, taskList.size());
        assertSame(second, taskList.get(1));
    }

    @Test
    public void find_keywordMatchesTwoOfThreeTasks_returnsMatchesInOriginalOrder() {
        TaskList taskList = new TaskList();
        Task readBook = new ToDo("read book");
        Task buyGroceries = new ToDo("buy groceries");
        Task returnBook = new ToDo("return book");
        taskList.add(readBook);
        taskList.add(buyGroceries);
        taskList.add(returnBook);

        ArrayList<Task> matches = taskList.find("book");

        assertEquals(2, matches.size());
        assertSame(readBook, matches.get(0));
        assertSame(returnBook, matches.get(1));
    }

    @Test
    public void find_noMatchingTasks_returnsEmptyList() {
        TaskList taskList = new TaskList();
        taskList.add(new ToDo("buy groceries"));

        ArrayList<Task> matches = taskList.find("book");

        assertEquals(0, matches.size());
    }

    @Test
    public void constructor_existingTasks_exposesThoseSameTasks() {
        Task loadedTask = new ToDo("loaded task");
        ArrayList<Task> existingTasks = new ArrayList<>();
        existingTasks.add(loadedTask);

        TaskList taskList = new TaskList(existingTasks);

        assertEquals(1, taskList.size());
        assertSame(loadedTask, taskList.get(1));
        assertSame(loadedTask, taskList.getTasks().get(0));
    }

}
