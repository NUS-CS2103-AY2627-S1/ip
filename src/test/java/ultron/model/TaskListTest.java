package ultron.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests task-list ordering, removal, and persistence snapshots.
 */
class TaskListTest {

    @Test
    void addAndGet_tasksAdded_returnsTasksInOrder() {
        Task firstTask = new Todo("read book");
        Task secondTask = new Todo("return book");
        TaskList tasks = new TaskList();

        tasks.add(firstTask);
        tasks.add(secondTask);

        assertEquals(2, tasks.size());
        assertSame(firstTask, tasks.get(0));
        assertSame(secondTask, tasks.get(1));
    }

    @Test
    void remove_taskAtIndex_returnsTaskAndClosesGap() {
        Task firstTask = new Todo("read book");
        Task secondTask = new Todo("return book");
        TaskList tasks = new TaskList();
        tasks.add(firstTask);
        tasks.add(secondTask);

        Task removedTask = tasks.remove(0);

        assertSame(firstTask, removedTask);
        assertEquals(1, tasks.size());
        assertSame(secondTask, tasks.get(0));
    }

    @Test
    void getTasks_taskListChanges_snapshotRemainsUnchangedAndImmutable() {
        Task firstTask = new Todo("read book");
        TaskList tasks = new TaskList();
        tasks.add(firstTask);
        List<Task> snapshot = tasks.getTasks();

        tasks.add(new Todo("return book"));

        assertEquals(List.of(firstTask), snapshot);
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(new Todo("buy bread")));
    }

    @Test
    void getMatchingTasks_keywordMatchesTasks_returnsMatchingTasksInOrder() {
        Task firstTask = new Todo("read book");
        Task secondTask = new Todo("return book");
        TaskList tasks = new TaskList();
        tasks.add(firstTask);
        tasks.add(new Todo("buy bread"));
        tasks.add(secondTask);

        List<Task> matchingTasks = tasks.getMatchingTasks("book");

        assertEquals(List.of(firstTask, secondTask), matchingTasks);
    }

    @Test
    void getMatchingTasks_keywordMatchesNoTasks_returnsEmptyList() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        List<Task> matchingTasks = tasks.getMatchingTasks("bread");

        assertEquals(List.of(), matchingTasks);
    }
}
