package xerxes.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.List;

import org.junit.jupiter.api.Test;


/**
 * Tests keyword search behavior in {@link TaskList}.
 */
class TaskListTest {

    /** Verifies that partial matches are returned in their original order. */
    @Test
    void findMatchingTasks_partialKeyword_returnsMatchingTasksInOrder() {
        TaskList tasks = new TaskList();
        Task firstTask = new ToDo("Read book");
        Task secondTask = new ToDo("Return book");
        tasks.addTask(firstTask);
        tasks.addTask(new ToDo("Buy groceries"));
        tasks.addTask(secondTask);

        List<Task> matchingTasks = tasks.findMatchingTasks("book");

        assertEquals(2, matchingTasks.size());
        assertSame(firstTask, matchingTasks.get(0));
        assertSame(secondTask, matchingTasks.get(1));
    }

    /** Verifies that keyword matching ignores letter case. */
    @Test
    void findMatchingTasks_differentCase_returnsMatchingTasks() {
        TaskList tasks = new TaskList();
        Task task = new ToDo("Read Book");
        tasks.addTask(task);

        List<Task> matchingTasks = tasks.findMatchingTasks("BOOK");

        assertEquals(List.of(task), matchingTasks);
    }

    /** Verifies that a keyword with no matches returns an empty list. */
    @Test
    void findMatchingTasks_noMatch_returnsEmptyList() {
        TaskList tasks = new TaskList();
        tasks.addTask(new ToDo("Read book"));

        List<Task> matchingTasks = tasks.findMatchingTasks("movie");

        assertEquals(List.of(), matchingTasks);
    }

    /** Verifies that a blank keyword returns an empty list. */
    @Test
    void findMatchingTasks_blankKeyword_returnsEmptyList() {
        TaskList tasks = new TaskList();
        tasks.addTask(new ToDo("Read book"));

        List<Task> matchingTasks = tasks.findMatchingTasks("   ");

        assertEquals(List.of(), matchingTasks);
    }
}
