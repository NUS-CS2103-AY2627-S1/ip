package walnut;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

class TaskListTest {
    @Test
    void newTaskList_isEmpty() {
        assertTrue(new TaskList().isEmpty());
    }

    @Test
    void addAndGet_preservesOrder() {
        TaskList tasks = new TaskList();
        Task first = new ToDo("first");
        Task second = new ToDo("second");

        tasks.add(first);
        tasks.add(second);

        assertEquals(2, tasks.size());
        assertEquals(first, tasks.get(0));
        assertEquals(second, tasks.get(1));
    }

    @Test
    void remove_decreasesSize() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("first"));
        tasks.add(new ToDo("second"));

        tasks.remove(0);

        assertEquals(1, tasks.size());
        assertEquals("second", tasks.get(0).getDescription());
    }

    @Test
    void find_returnsMatchingTasksOnly() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("buy milk"));
        tasks.add(new ToDo("read book"));

        ArrayList<Task> found = tasks.find("milk");

        assertEquals(1, found.size());
        assertEquals("buy milk", found.get(0).getDescription());
    }

    @Test
    void find_isCaseSensitive() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("Buy Milk"));

        assertTrue(tasks.find("Buy").size() == 1);
        assertTrue(tasks.find("buy").isEmpty());
    }

    @Test
    void toString_listsTasksAndCount() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("buy milk"));

        assertEquals(
                "1. [T][ ] buy milk\nYou have 1 tasks in your list.",
                tasks.toString()
        );
    }

    @Test
    void invalidIndexes_throwException() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("buy milk"));

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.remove(1));
    }

    @Test
    void iterator_visitsEveryTask() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("first"));
        tasks.add(new ToDo("second"));

        int count = 0;
        for (Task ignored : tasks) {
            count++;
        }

        assertEquals(2, count);
        assertFalse(tasks.isEmpty());
    }
}
