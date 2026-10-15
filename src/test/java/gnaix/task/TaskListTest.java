package gnaix.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

class TaskListTest {

    @Test
    void add_emptyTaskList_taskAdded() {
        TaskList taskList = new TaskList();
        Task task = new Todo("buy milk");

        taskList.add(task);

        assertEquals(1, taskList.size());
        assertEquals(task, taskList.get(0));
    }

    @Test
    void add_existingTaskList_taskAddedAtEnd() {
        TaskList taskList = new TaskList();
        Task first = new Todo("buy milk");
        Task second = new Todo("buy eggs");

        taskList.add(first);
        taskList.add(second);

        assertEquals(2, taskList.size());
        assertEquals(first, taskList.get(0));
        assertEquals(second, taskList.get(1));
    }

    @Test
    void delete_middleIndex_taskRemovedAndOrderPreserved() {
        TaskList taskList = new TaskList();
        Task first = new Todo("buy milk");
        Task second = new Todo("buy eggs");
        Task third = new Todo("buy bread");

        taskList.add(first);
        taskList.add(second);
        taskList.add(third);

        Task deletedTask = taskList.delete(1);

        assertEquals(second, deletedTask);
        assertEquals(2, taskList.size());
        assertEquals(first, taskList.get(0));
        assertEquals(third, taskList.get(1));
    }

    @Test
    void delete_firstIndex_taskRemovedAndOrderPreserved() {
        TaskList taskList = new TaskList();
        Task first = new Todo("buy milk");
        Task second = new Todo("buy eggs");

        taskList.add(first);
        taskList.add(second);

        Task deletedTask = taskList.delete(0);

        assertEquals(first, deletedTask);
        assertEquals(1, taskList.size());
        assertEquals(second, taskList.get(0));
    }

    @Test
    void delete_lastIndex_taskRemovedAndOrderPreserved() {
        TaskList taskList = new TaskList();
        Task first = new Todo("buy milk");
        Task second = new Todo("buy eggs");

        taskList.add(first);
        taskList.add(second);

        Task deletedTask = taskList.delete(1);

        assertEquals(second, deletedTask);
        assertEquals(1, taskList.size());
        assertEquals(first, taskList.get(0));
    }

    @Test
    void delete_invalidIndex_exceptionThrown() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("buy milk"));

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.delete(1));
    }

    @Test
    void mark_validIndex_taskMarkedComplete() {
        TaskList taskList = new TaskList();
        Task task = new Todo("buy milk");

        taskList.add(task);
        taskList.mark(0);

        assertTrue(taskList.get(0).isCompleted());
    }

    @Test
    void mark_invalidIndex_exceptionThrown() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("buy milk"));

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.mark(1));
    }

    @Test
    void unmark_completedTask_taskMarkedIncomplete() {
        TaskList taskList = new TaskList();
        Task task = new Todo("buy milk");

        task.markAsComplete();
        taskList.add(task);

        taskList.unmark(0);

        assertFalse(taskList.get(0).isCompleted());
    }

    @Test
    void unmark_invalidIndex_exceptionThrown() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("buy milk"));

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.unmark(1));
    }

    @Test
    void getLast_multipleTasks_lastTaskReturned() {
        TaskList taskList = new TaskList();
        Task first = new Todo("buy milk");
        Task second = new Todo("buy eggs");

        taskList.add(first);
        taskList.add(second);

        assertEquals(second, taskList.getLast());
    }

    @Test
    void getLast_emptyTaskList_exceptionThrown() {
        TaskList taskList = new TaskList();

        assertThrows(NoSuchElementException.class,
                taskList::getLast);
    }

    @Test
    void getTasks_modifyReturnedList_exceptionThrown() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("buy milk"));

        assertThrows(UnsupportedOperationException.class, () -> taskList.getTasks().add(new Todo("buy eggs")));
    }

    @Test
    void constructor_existingArrayList_tasksRetained() {
        Task first = new Todo("buy milk");
        Task second = new Todo("buy eggs");
        ArrayList<Task> tasks = new ArrayList<>(List.of(first, second));

        TaskList taskList = new TaskList(tasks);

        assertEquals(2, taskList.size());
        assertEquals(first, taskList.get(0));
        assertEquals(second, taskList.get(1));
    }

    @Test
    void iterator_multipleTasks_tasksReturnedInOrder() {
        Task first = new Todo("buy milk");
        Task second = new Todo("buy eggs");
        TaskList taskList = new TaskList();
        taskList.add(first);
        taskList.add(second);

        List<Task> iteratedTasks = new ArrayList<>();
        for (Task task : taskList) {
            iteratedTasks.add(task);
        }

        assertEquals(List.of(first, second), iteratedTasks);
    }
}
