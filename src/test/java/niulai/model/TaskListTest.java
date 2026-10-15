package niulai.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests task-list operations that support task searching. */
class TaskListTest {
    /** Verifies that description searches are case-insensitive and preserve list order. */
    @Test
    void findByDescription_keywordMatchesInOriginalOrder() {
        Task firstMatch = new Todo("Read a book");
        Task nonMatch = new Todo("Buy groceries");
        Task secondMatch = new Deadline("Return book", "June 6th");
        TaskList tasks = new TaskList(firstMatch, nonMatch, secondMatch);

        List<Task> matches = tasks.findByDescription("BOOK");

        assertEquals(2, matches.size());
        assertSame(firstMatch, matches.get(0));
        assertSame(secondMatch, matches.get(1));
    }

    /** Verifies that a keyword with no matches produces an empty result. */
    @Test
    void findByDescription_noMatch_returnsEmptyList() {
        TaskList tasks = new TaskList(new Todo("Read a book"));

        assertEquals(List.of(), tasks.findByDescription("holiday"));
    }

    /** Verifies that case and spacing variants of one task are duplicates. */
    @Test
    void add_sameNormalizedIdentity_exceptionThrownWithoutMutation() {
        Task first = new Todo("Read   Book");
        TaskList tasks = new TaskList(first);

        assertThrows(DuplicateTaskException.class,
                () -> tasks.add(new Todo("  read book  ")));
        assertEquals(1, tasks.size());
        assertSame(first, tasks.get(0));
    }

    /** Verifies that task type and temporal details remain part of identity. */
    @Test
    void add_differentTypeOrDetails_tasksRemainDistinct() {
        TaskList tasks = new TaskList(new Todo("read book"));

        tasks.add(new Deadline("read book", "tomorrow"));
        tasks.add(new Deadline("read book", "Friday"));

        assertEquals(3, tasks.size());
    }

    /** Verifies indexed insertion, removal, and iteration preserve display order. */
    @Test
    void indexedOperations_validTasks_preserveOrder() {
        Task first = new Todo("first");
        Task second = new Todo("second");
        Task middle = new Todo("middle");
        TaskList tasks = new TaskList(first, second);

        tasks.add(1, middle);

        assertEquals(List.of(first, middle, second),
                java.util.stream.StreamSupport.stream(tasks.spliterator(), false).toList());
        assertSame(middle, tasks.remove(1));
        assertEquals(2, tasks.size());
        assertSame(second, tasks.get(1));
    }

    /** Verifies null list inputs are rejected before mutating the collection. */
    @Test
    void taskList_nullInputs_exceptionThrownWithoutMutation() {
        assertThrows(NullPointerException.class, () -> new TaskList((Task[]) null));
        TaskList tasks = new TaskList(new Todo("existing"));

        assertThrows(NullPointerException.class, () -> tasks.add(null));
        assertThrows(NullPointerException.class, () -> tasks.add(0, null));
        assertThrows(NullPointerException.class, () -> tasks.findByDescription(null));
        assertEquals(1, tasks.size());
    }

    /** Verifies duplicate detection also applies to indexed insertion and construction. */
    @Test
    void duplicateTask_indexedInsertionOrConstruction_exceptionThrown() {
        Task original = new Event("meeting", "9am", "10am");
        Task duplicate = new Event("MEETING", "9AM", "10AM");
        TaskList tasks = new TaskList(original);

        assertThrows(DuplicateTaskException.class, () -> tasks.add(0, duplicate));
        assertThrows(DuplicateTaskException.class,
                () -> new TaskList(original, duplicate));
    }
}
