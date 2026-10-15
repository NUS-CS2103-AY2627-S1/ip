package rocky;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import rocky.task.Deadline;
import rocky.task.Event;
import rocky.task.Task;
import rocky.task.Todo;

/** Tests the core collection operations provided by {@link TaskList}. */
class TaskListTest {
    /** Verifies that a newly created list starts empty. */
    @Test
    void constructor_noTasks_createsEmptyList() {
        TaskList tasks = new TaskList();

        assertTrue(tasks.isEmpty());
        assertEquals(0, tasks.size());
    }

    /** Verifies that adding, retrieving, and deleting preserve task order. */
    @Test
    void addGetAndDelete_multipleTasks_maintainsTaskOrder() {
        Task first = new Todo("first");
        Task second = new Todo("second");
        TaskList tasks = new TaskList();

        tasks.add(first);
        tasks.add(second);

        assertEquals(2, tasks.size());
        assertEquals(first, tasks.get(0));
        assertEquals(second, tasks.get(1));

        tasks.delete(0);

        assertEquals(1, tasks.size());
        assertEquals(second, tasks.get(0));
    }

    /** Verifies that constructing a task list copies the supplied collection. */
    @Test
    void constructor_suppliedTasks_copiesSourceList() {
        List<Task> original = List.of(new Todo("saved task"));
        TaskList tasks = new TaskList(original);

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] saved task", tasks.get(0).toString());
    }

    /** Verifies that a null source collection violates the task-list invariant. */
    @Test
    void constructor_nullSource_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new TaskList(null));
    }

    /** Verifies that a source collection cannot contain null tasks. */
    @Test
    void constructor_nullTaskInSource_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new TaskList(Arrays.asList(new Todo("task"), null)));
    }

    /** Verifies that null tasks cannot be inserted into the task list. */
    @Test
    void add_nullTask_throwsAssertionError() {
        TaskList tasks = new TaskList();

        assertThrows(AssertionError.class, () -> tasks.add(null));
    }

    /** Verifies that callers cannot modify the list through its persistence view. */
    @Test
    void asList_externalModification_throwsUnsupportedOperationException() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("protected task"));

        assertThrows(UnsupportedOperationException.class,
                () -> tasks.asList().clear());
        assertEquals(1, tasks.size());
    }

    /** Verifies that matching is case-insensitive and preserves task-list order. */
    @Test
    void find_matchingKeyword_returnsTasksInOriginalOrderCaseInsensitive() {
        Task first = new Todo("Read a book");
        Task second = new Todo("Exercise");
        Task third = new Todo("Return book");
        TaskList tasks = new TaskList(List.of(first, second, third));

        List<Task> matchingTasks = tasks.find("BOOK");

        assertEquals(List.of(first, third), matchingTasks);
    }

    /** Verifies that null and blank search keywords have no matches. */
    @Test
    void find_nullOrBlankKeyword_returnsNoTasks() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertTrue(tasks.find(null).isEmpty());
        assertTrue(tasks.find("   ").isEmpty());
    }

    /** Verifies that an unmatched search keyword returns no tasks. */
    @Test
    void find_unmatchedKeyword_returnsEmptyList() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertTrue(tasks.find("meeting").isEmpty());
    }

    /** Verifies that dated tasks are sorted before undated tasks chronologically. */
    @Test
    void sortByDate_mixedTasks_ordersDatedTasksFirst() {
        Task undatedTask = new Todo("undated");
        Task laterDeadline = new Deadline("later", LocalDate.of(2025, 1, 2));
        Task earliestEvent = new Event("earliest",
                LocalDateTime.of(2025, 1, 1, 8, 0),
                LocalDateTime.of(2025, 1, 1, 9, 0));
        Task earlierDeadline = new Deadline("earlier", LocalDate.of(2024, 12, 31));
        TaskList tasks = new TaskList(List.of(undatedTask, laterDeadline,
                earliestEvent, earlierDeadline));

        tasks.sortByDate();

        assertEquals(List.of(earlierDeadline, earliestEvent, laterDeadline, undatedTask),
                tasks.asList());
    }

    /** Verifies that chronological sorting keeps tasks with equal dates in insertion order. */
    @Test
    void sortByDate_equalDates_preservesInsertionOrder() {
        Task first = new Deadline("first", LocalDate.of(2025, 1, 1));
        Task second = new Deadline("second", LocalDate.of(2025, 1, 1));
        TaskList tasks = new TaskList(List.of(second, first));

        tasks.sortByDate();

        assertEquals(List.of(second, first), tasks.asList());
    }

    /** Verifies that invalid indices are reported by the underlying task collection. */
    @Test
    void getAndDelete_invalidIndex_throwsIndexOutOfBoundsException() {
        TaskList tasks = new TaskList();

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.delete(0));
    }
}
