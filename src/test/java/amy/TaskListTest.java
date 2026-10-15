package amy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import amy.task.Task;
import amy.task.Todo;

/**
 * Tests the collection and task-state operations provided by {@link TaskList}.
 */
class TaskListTest {
    @Test
    void newTaskList_isEmpty() {
        TaskList taskList = new TaskList();

        assertTrue(taskList.isEmpty());
        assertEquals(0, taskList.size());
    }

    @Test
    void taskListWithInitialTasks_containsThoseTasks() {
        Todo task = new Todo("already stored");
        ArrayList<Task> initialTasks = new ArrayList<>();
        initialTasks.add(task);

        TaskList taskList = new TaskList(initialTasks);

        assertEquals(1, taskList.size());
        assertEquals(task, taskList.get(0));
    }

    @Test
    void add_tasksAreStoredInInsertionOrder() {
        TaskList taskList = new TaskList();
        Todo firstTask = new Todo("first");
        Todo secondTask = new Todo("second");

        taskList.add(firstTask);
        taskList.add(secondTask);

        assertEquals(2, taskList.size());
        assertEquals(firstTask, taskList.get(0));
        assertEquals(secondTask, taskList.get(1));
    }

    @Test
    void remove_existingTask_returnsTaskAndUpdatesList() {
        TaskList taskList = new TaskList();
        Todo task = new Todo("remove me");
        taskList.add(task);

        assertEquals(task, taskList.remove(0));
        assertTrue(taskList.isEmpty());
    }

    @Test
    void remove_middleTask_preservesOrderOfRemainingTasks() {
        TaskList taskList = new TaskList();
        Todo firstTask = new Todo("first");
        Todo middleTask = new Todo("middle");
        Todo lastTask = new Todo("last");
        taskList.add(firstTask);
        taskList.add(middleTask);
        taskList.add(lastTask);

        assertEquals(middleTask, taskList.remove(1));

        assertEquals(2, taskList.size());
        assertEquals(firstTask, taskList.get(0));
        assertEquals(lastTask, taskList.get(1));
    }

    @Test
    void mark_taskBecomesDone() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("finish me"));

        taskList.mark(0);

        assertTrue(taskList.get(0).isDone());
    }

    @Test
    void unmark_doneTaskBecomesNotDone() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("reopen me"));
        taskList.mark(0);

        taskList.unmark(0);

        assertFalse(taskList.get(0).isDone());
    }

    @Test
    void asList_returnsCurrentTasksForPersistence() {
        TaskList taskList = new TaskList();
        Todo task = new Todo("persist me");
        taskList.add(task);

        ArrayList<Task> persistedTasks = taskList.asList();

        assertEquals(1, persistedTasks.size());
        assertEquals(task, persistedTasks.get(0));
    }
}
