package mary.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Verifies list ordering, deletion boundaries, and collection ownership.
 */
class TaskListTest {
    /**
     * Checks mixed-type ordering, equal-time stability, duplicate references and completion state.
     */
    @Test
    void sortChronologically_mixedTasks_preservesTiesAndState() {
        LocalDateTime time = LocalDateTime.of(2026, 12, 2, 14, 0);
        Task todo = new Todo("read");
        Task nextTodo = new Todo("buy");
        Task deadline = new Deadline("return", time);
        deadline.markAsDone();
        Task event = new Event("meeting", time, time.plusHours(2));
        Task earlier = new Deadline("submit", time.minusHours(1));
        TaskList tasks = new TaskList(List.of(todo, deadline, event, nextTodo, earlier, deadline));
        tasks.sortChronologically();
        List<Task> expected = List.of(earlier, deadline, event, deadline, todo, nextTodo);
        assertEquals(expected, tasks.getTasks());
        assertEquals("X", deadline.getStatusIcon());
        tasks.sortChronologically();
        assertEquals(expected, tasks.getTasks());
    }

    /**
     * Checks that empty, single-task and undated lists keep their contents and order.
     */
    @Test
    void sortChronologically_emptySingletonAndTodos_keepsOrder() {
        TaskList tasks = new TaskList();
        tasks.sortChronologically();
        assertTrue(tasks.isEmpty());
        Task first = new Todo("first");
        tasks.add(first);
        tasks.sortChronologically();
        assertSame(first, tasks.get(0));
        Task second = new Todo("second");
        tasks.add(second);
        tasks.sortChronologically();
        assertEquals(List.of(first, second), tasks.getTasks());
    }

    /**
     * Checks actual date/time ordering rather than formatted text, with undated tasks last.
     */
    @Test
    void sortChronologically_dateBoundaries_ordersByDateAndTime() {
        Task january = new Deadline("january", LocalDateTime.of(2027, 1, 1, 0, 0));
        Task december = new Deadline("december", LocalDateTime.of(2026, 12, 31, 23, 59));
        Task maximum = new Deadline("maximum", LocalDateTime.MAX);
        Task todo = new Todo("undated");
        TaskList tasks = new TaskList(List.of(todo, maximum, january, december));
        tasks.sortChronologically();
        assertEquals(List.of(december, january, maximum, todo), tasks.getTasks());
    }

    /**
     * Tests add: empty list; preserves insertion order and object identity.
     */
    @Test
    void add_emptyList_preservesInsertionOrderAndObjectIdentity() {
        TaskList tasks = new TaskList();
        assertTrue(tasks.isEmpty());
        Task first = new Todo("first");
        Task second = new Todo("second");
        tasks.add(first);
        tasks.add(second);
        assertEquals(2, tasks.size());
        assertSame(first, tasks.get(0));
        assertSame(second, tasks.get(1));
    }

    /**
     * Tests remove: middle first and last; updates order and size.
     */
    @Test
    void remove_middleFirstAndLast_updatesOrderAndSize() {
        Task firstTask = new Todo("a");
        Task secondTask = new Todo("b");
        Task thirdTask = new Todo("c");
        TaskList tasks = new TaskList(List.of(firstTask, secondTask, thirdTask));
        assertSame(secondTask, tasks.remove(1));
        assertEquals(List.of(firstTask, thirdTask), tasks.getTasks());
        assertSame(firstTask, tasks.remove(0));
        assertSame(thirdTask, tasks.remove(0));
        assertTrue(tasks.isEmpty());
    }

    /**
     * Tests remove: invalid indices; throws without changing list.
     */
    @Test
    void remove_invalidIndices_throwsWithoutChangingList() {
        Task task = new Todo("keep");
        TaskList tasks = new TaskList(List.of(task));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.remove(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.remove(1));
        assertEquals(List.of(task), tasks.getTasks());
        assertThrows(IndexOutOfBoundsException.class, () -> new TaskList().remove(0));
    }

    /**
     * Tests constructor: source collection changes; do not change owned list.
     */
    @Test
    void constructor_sourceCollectionChanges_doNotChangeOwnedList() {
        ArrayList<Task> sourceTasks = new ArrayList<>(List.of(new Todo("keep")));
        TaskList tasks = new TaskList(sourceTasks);
        sourceTasks.clear();
        assertEquals(1, tasks.size());
    }

    /**
     * Tests task snapshots: modifying the copy does not change the owned list.
     */
    @Test
    void getTasks_modifyingSnapshot_doesNotChangeOwnedList() {
        Task task = new Todo("keep");
        TaskList tasks = new TaskList(List.of(task));
        List<Task> snapshotTasks = tasks.getTasks();
        snapshotTasks.clear();
        assertEquals(1, tasks.size());
        assertSame(task, tasks.get(0));
    }
}
