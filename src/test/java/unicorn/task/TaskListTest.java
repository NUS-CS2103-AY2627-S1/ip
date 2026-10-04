package unicorn.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Checks the basic operations provided by {@link TaskList}.
 */
public class TaskListTest {
    /**
     * Verifies that tasks can be added, read, deleted, and reinserted.
     */
    @Test
    void taskListOperationsWork() {
        Task firstTask = new TodoTask("read book");
        Task secondTask = new DeadlineTask("return book", LocalDateTime.of(2019, 12, 2, 18, 0));
        TaskList tasks = new TaskList(List.of(firstTask));

        tasks.add(secondTask);
        assertEquals(2, tasks.size(), "Adding a task should increase the task count.");
        assertSame(secondTask, tasks.get(1), "The added task should be available by its index.");

        Task deletedTask = tasks.delete(0);
        assertSame(firstTask, deletedTask, "Deleting a task should return that task.");
        assertEquals(1, tasks.size(), "Deleting a task should decrease the task count.");

        tasks.add(0, deletedTask);
        assertEquals(2, tasks.size(), "Adding at an index should restore a deleted task.");
        assertEquals(2, tasks.asList().size(), "The task snapshot should include all tasks.");
    }

    /**
     * Verifies that snapshots cannot modify the task list and remain independent of its source list.
     */
    @Test
    void asList_taskListChanges_snapshotIsImmutableAndIndependent() {
        Task firstTask = new TodoTask("read book");
        List<Task> sourceTasks = new ArrayList<>(List.of(firstTask));
        TaskList tasks = new TaskList(sourceTasks);

        sourceTasks.add(new TodoTask("write notes"));
        List<Task> snapshot = tasks.asList();
        tasks.add(new TodoTask("submit work"));

        assertEquals(List.of(firstTask), snapshot,
                "A snapshot should not change when the task list changes.");
        assertThrows(UnsupportedOperationException.class, () ->
                snapshot.add(new TodoTask("change snapshot")),
                "A snapshot should not allow callers to change the task list.");
    }

    /**
     * Verifies that searches ignore case and return only matching descriptions in list order.
     */
    @Test
    void find_keywordMatchesDescriptionsIgnoringCase() {
        Task firstMatch = new TodoTask("Read Book");
        Task nonMatch = new TodoTask("write notes");
        Task secondMatch = new DeadlineTask("return book", LocalDateTime.of(2019, 12, 2, 18, 0));
        TaskList tasks = new TaskList(List.of(firstMatch, nonMatch, secondMatch));

        assertEquals(List.of(firstMatch, secondMatch), tasks.find("BOOK"),
                "Find should return every description containing the keyword in list order.");
        assertEquals(List.of(nonMatch), tasks.find("NOTES"),
                "Find should match a task regardless of the keyword's letter case.");
        assertEquals(List.of(), tasks.find("meeting"),
                "Find should return an empty list when no descriptions match.");
        assertThrows(UnsupportedOperationException.class, () ->
                tasks.find("book").add(new TodoTask("another book")),
                "Find results should not allow callers to modify the task list.");
    }

    /**
     * Verifies that programmers cannot place null entries in a task list.
     */
    @Test
    void constructorOrAdd_nullTask_assertionErrorThrown() {
        List<Task> sourceTasks = new ArrayList<>();
        sourceTasks.add(null);

        assertThrows(AssertionError.class, () -> new TaskList(sourceTasks),
                "A task list should reject a null entry from its source list.");

        TaskList tasks = new TaskList();
        assertThrows(AssertionError.class, () -> tasks.add(null),
                "A task list should reject a null task appended to it.");
        assertThrows(AssertionError.class, () -> tasks.add(0, null),
                "A task list should reject a null task inserted into it.");
    }
}
