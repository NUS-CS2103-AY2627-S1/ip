package turtley.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import turtley.exception.TurtleyException;

/** Tests ordering, capacity, and mutation invariants of {@link TaskList}. */
class TaskListTest {

    @Test
    void add_getAndRemove_preserveTaskOrder() {
        Task first = new ToDo("first");
        Task second = new ToDo("second");
        TaskList tasks = new TaskList();

        tasks.add(first);
        tasks.add(second);

        assertAllBasicState(tasks, 2, false);
        assertSame(first, tasks.get(0));
        assertSame(second, tasks.get(1));
        assertSame(first, tasks.remove(0));
        assertSame(second, tasks.get(0));
        assertEquals(1, tasks.size());
    }

    @Test
    void add_indexedTask_insertsBeforeExistingTask() {
        Task first = new ToDo("first");
        Task inserted = new ToDo("inserted");
        Task last = new ToDo("last");
        TaskList tasks = new TaskList(List.of(first, last));

        tasks.add(1, inserted);

        assertEquals(List.of(first, inserted, last), tasks.asList());
    }

    @Test
    void add_nullTask_throwsAndDoesNotMutateList() {
        TaskList tasks = new TaskList();

        TurtleyException exception = assertThrows(TurtleyException.class, () -> tasks.add(null));

        assertEquals("Cannot add a null task.", exception.getMessage());
        assertTrue(tasks.isEmpty());
    }

    @Test
    void addAll_nullOrOverCapacity_throwsWithoutPartialMutation() {
        TaskList tasks = new TaskList(List.of(new ToDo("existing")));

        assertThrows(TurtleyException.class, () -> tasks.addAll(null));
        assertThrows(TurtleyException.class,
                () -> tasks.addAll(Arrays.asList(new ToDo("valid"), null)));

        TaskList nearlyFull = new TaskList(createTasks(99));
        assertThrows(TurtleyException.class,
                () -> nearlyFull.addAll(List.of(new ToDo("one"), new ToDo("two"))));

        assertEquals(1, tasks.size());
        assertEquals("existing", tasks.get(0).getDescription());
        assertEquals(99, nearlyFull.size());
    }

    @Test
    void add_duplicateTaskDetails_throwsWithoutMutation() {
        TaskList tasks = new TaskList(List.of(new ToDo("same task")));

        TurtleyException exception = assertThrows(TurtleyException.class,
                () -> tasks.add(new ToDo("same task")));

        assertEquals("Cannot add a duplicate task.", exception.getMessage());
        assertEquals(1, tasks.size());
    }

    @Test
    void addAll_duplicateTaskDetails_throwsAtomically() {
        TaskList tasks = new TaskList();

        assertThrows(TurtleyException.class,
                () -> tasks.addAll(List.of(new ToDo("same task"), new ToDo("same task"))));

        assertTrue(tasks.isEmpty());
    }

    @Test
    void event_startNotBeforeEnd_throwsTurtleyException() {
        TurtleyException exception = assertThrows(TurtleyException.class,
                () -> new Event("meeting", LocalDateTime.of(2026, 8, 27, 10, 0),
                        LocalDateTime.of(2026, 8, 27, 10, 0)));

        assertEquals("Event start time must be before end time.", exception.getMessage());
    }

    @Test
    void capacity_isFullAtOneHundredAndRejectsFurtherAdds() {
        TaskList tasks = new TaskList(createTasks(100));

        assertTrue(tasks.isFull());
        assertThrows(TurtleyException.class, () -> tasks.add(new ToDo("too many")));
        assertThrows(TurtleyException.class, () -> tasks.add(0, new ToDo("too many")));
        assertEquals(100, tasks.size());
    }

    @Test
    void asList_isReadOnlyViewOfCurrentTasks() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("task"));

        assertThrows(UnsupportedOperationException.class,
                () -> tasks.asList().add(new ToDo("not allowed")));
        assertEquals(1, tasks.asList().size());
    }

    private static void assertAllBasicState(TaskList tasks, int size, boolean isEmpty) {
        assertEquals(size, tasks.size());
        assertEquals(isEmpty, tasks.isEmpty());
        assertFalse(tasks.isFull());
    }

    private static List<Task> createTasks(int count) {
        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            tasks.add(new ToDo("task " + i));
        }
        return tasks;
    }
}
