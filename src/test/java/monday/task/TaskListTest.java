package monday.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;

import org.junit.jupiter.api.Test;

/**
 * Tests for TaskList.
 */
class TaskListTest {
    @Test
    void constructor_withoutTasks_createsEmptyList() {
        TaskList taskList = new TaskList();

        assertEquals(0, taskList.size());
    }

    @Test
    void constructor_withTasks_createsListContainingTasksInGivenOrder() {
        Task firstTask = new Todo("read chapter");
        Task secondTask = new Todo("submit report");
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(firstTask);
        tasks.add(secondTask);

        TaskList taskList = new TaskList(tasks);

        assertEquals(2, taskList.size());
        assertSame(firstTask, taskList.get(0));
        assertSame(secondTask, taskList.get(1));
    }

    @Test
    void constructor_withTasks_copiesSuppliedList() {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("read chapter"));
        TaskList taskList = new TaskList(tasks);

        tasks.add(new Todo("submit report"));

        assertEquals(1, taskList.size());
        assertEquals("read chapter", taskList.get(0).getDescription());
    }

    @Test
    void add_addsTaskToEndOfList() {
        TaskList taskList = new TaskList();
        Task firstTask = new Todo("read chapter");
        Task secondTask = new Todo("submit report");

        taskList.add(firstTask);
        taskList.add(secondTask);

        assertEquals(2, taskList.size());
        assertSame(firstTask, taskList.get(0));
        assertSame(secondTask, taskList.get(1));
    }

    @Test
    void get_withInvalidIndex_throwsIndexOutOfBoundsException() {
        TaskList taskList = new TaskList();

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.get(-1));
    }

    @Test
    void remove_removesAndReturnsTaskAtGivenIndex() {
        Task firstTask = new Todo("read chapter");
        Task secondTask = new Todo("submit report");
        TaskList taskList = new TaskList();
        taskList.add(firstTask);
        taskList.add(secondTask);

        Task removedTask = taskList.remove(0);

        assertSame(firstTask, removedTask);
        assertEquals(1, taskList.size());
        assertSame(secondTask, taskList.get(0));
    }

    @Test
    void remove_withInvalidIndex_throwsIndexOutOfBoundsException() {
        TaskList taskList = new TaskList();

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.remove(0));
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.remove(-1));
    }

    @Test
    void iterator_returnsTasksInListOrder() {
        Task firstTask = new Todo("read chapter");
        Task secondTask = new Todo("submit report");
        TaskList taskList = new TaskList();
        taskList.add(firstTask);
        taskList.add(secondTask);

        Iterator<Task> iterator = taskList.iterator();

        assertTrue(iterator.hasNext());
        assertSame(firstTask, iterator.next());
        assertTrue(iterator.hasNext());
        assertSame(secondTask, iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    void find_withMatchingKeyword_returnsMatchingTasks() {
        Task firstMatchingTask = new Todo("read Java chapter");
        Task nonMatchingTask = new Todo("submit report");
        Task secondMatchingTask = new Todo("review java notes");
        TaskList taskList = new TaskList();
        taskList.add(firstMatchingTask);
        taskList.add(nonMatchingTask);
        taskList.add(secondMatchingTask);

        TaskList matchingTasks = taskList.find("java");

        assertEquals(2, matchingTasks.size());
        assertSame(firstMatchingTask, matchingTasks.get(0));
        assertSame(secondMatchingTask, matchingTasks.get(1));
    }

    @Test
    void find_withDifferentCapitalisation_returnsMatchingTasks() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("Read Java chapter"));

        TaskList matchingTasks = taskList.find("jAvA");

        assertEquals(1, matchingTasks.size());
        assertEquals("Read Java chapter", matchingTasks.get(0).getDescription());
    }

    @Test
    void find_withoutMatchingKeyword_returnsEmptyList() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read chapter"));

        TaskList matchingTasks = taskList.find("exercise");

        assertEquals(0, matchingTasks.size());
    }
}
