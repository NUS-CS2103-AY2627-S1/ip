package bob.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests one-based indexing and mutation behavior in {@link TaskList}.
 */
public class TaskListTest {
    @Test
    public void constructor_noTasks_createsEmptyList() {
        TaskList tasks = new TaskList();

        assertTrue(tasks.isEmpty());
        assertEquals(0, tasks.size());
    }

    @Test
    public void constructor_taskCollection_copiesCollection() {
        ArrayList<Task> originalTasks = new ArrayList<>();
        originalTasks.add(new Todo("first"));
        TaskList tasks = new TaskList(originalTasks);

        originalTasks.add(new Todo("second"));

        assertEquals(1, tasks.size());
    }

    @Test
    public void add_task_appendsTaskAndUpdatesSize() {
        TaskList tasks = new TaskList();
        Task task = new Todo("read book");

        tasks.add(task);

        assertEquals(1, tasks.size());
        assertSame(task, tasks.get(1));
    }

    @Test
    public void get_validOneBasedTaskNumber_returnsSelectedTask() {
        Task firstTask = new Todo("first");
        Task secondTask = new Todo("second");
        TaskList tasks = new TaskList(List.of(firstTask, secondTask));

        assertSame(secondTask, tasks.get(2));
    }

    @Test
    public void remove_validTaskNumber_removesTaskAndReindexesList() {
        Task firstTask = new Todo("first");
        Task secondTask = new Todo("second");
        TaskList tasks = new TaskList(List.of(firstTask, secondTask));

        Task removedTask = tasks.remove(1);

        assertSame(firstTask, removedTask);
        assertEquals(1, tasks.size());
        assertSame(secondTask, tasks.get(1));
    }

    @Test
    public void hasTaskNumber_boundaryValues_returnsExpectedResults() {
        TaskList tasks = new TaskList(List.of(new Todo("first"), new Todo("second")));

        assertFalse(tasks.hasTaskNumber(0));
        assertTrue(tasks.hasTaskNumber(1));
        assertTrue(tasks.hasTaskNumber(2));
        assertFalse(tasks.hasTaskNumber(3));
    }

    @Test
    public void find_keywordInSomeDescriptions_returnsMatchesInOriginalOrder() {
        Task firstMatch = new Todo("read book");
        Task nonMatch = new Todo("buy groceries");
        Task secondMatch = new Todo("return book");
        TaskList tasks = new TaskList(List.of(firstMatch, nonMatch, secondMatch));

        TaskList matches = tasks.find("book");

        assertEquals(2, matches.size());
        assertSame(firstMatch, matches.get(1));
        assertSame(secondMatch, matches.get(2));
    }

    @Test
    public void find_keywordWithDifferentCase_returnsMatchingTasks() {
        Task matchingTask = new Todo("Read Book");
        TaskList tasks = new TaskList(List.of(matchingTask));

        TaskList matches = tasks.find("BOOK");

        assertEquals(1, matches.size());
        assertSame(matchingTask, matches.get(1));
    }

    @Test
    public void find_keywordAbsent_returnsEmptyList() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        TaskList matches = tasks.find("movie");

        assertTrue(matches.isEmpty());
    }

    @Test
    public void asList_addAttempt_exceptionThrown() {
        TaskList tasks = new TaskList(List.of(new Todo("first")));

        assertThrows(UnsupportedOperationException.class,
                () -> tasks.asList().add(new Todo("second")));
    }
}
