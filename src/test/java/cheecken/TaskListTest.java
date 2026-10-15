package cheecken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Checks task ordering, index boundaries, immutable snapshots, and description search.
 */
class TaskListTest {
    /**
     * Verifies new list with empty: has no tasks or matches.
     */
    @Test
    void newList_empty_hasNoTasksOrMatches() {
        TaskList tasks = new TaskList();
        assertEquals(0, tasks.size());
        assertTrue(tasks.asList().isEmpty());
        assertTrue(tasks.find("book").isEmpty());
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.remove(0));
    }

    /**
     * Verifies remove with middle task: returns same task and preserves order.
     */
    @Test
    void remove_middleTask_returnsSameTaskAndPreservesOrder() {
        TaskList tasks = new TaskList();
        Task first = new Todo("first");
        Task middle = new Todo("middle");
        Task last = new Todo("last");
        tasks.add(first);
        tasks.add(middle);
        tasks.add(last);
        assertSame(middle, tasks.get(1));
        assertSame(middle, tasks.remove(1));
        assertEquals(List.of(first, last), tasks.asList());
        assertEquals(2, tasks.size());
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.get(2));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.remove(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.remove(2));
        assertEquals(List.of(first, last), tasks.asList());
    }

    /**
     * Verifies as list with snapshot: is immutable and unaffected by later additions.
     */
    @Test
    void asList_snapshot_isImmutableAndUnaffectedByLaterAdditions() {
        TaskList tasks = new TaskList();
        Task first = new Todo("first");
        tasks.add(first);
        List<Task> snapshot = tasks.asList();
        assertThrows(UnsupportedOperationException.class, () -> snapshot.add(new Todo("blocked")));
        tasks.add(new Todo("second"));
        tasks.remove(0);
        assertEquals(List.of(first), snapshot);
    }

    /**
     * Verifies find with mixed task types: matches descriptions only in insertion order.
     */
    @Test
    void find_mixedTaskTypes_matchesDescriptionsOnlyInInsertionOrder() {
        TaskList tasks = new TaskList();
        Task book = new Todo("Read BOOK");
        Task deadline = new Deadline("return book", "2026-09-18");
        tasks.add(book);
        tasks.add(new Event("meeting", "2026-09-18", "2026-09-19"));
        tasks.add(deadline);
        assertEquals(List.of(book, deadline), tasks.find("bOoK"));
        assertEquals(List.of(book), tasks.find("ead BO"));
        assertTrue(tasks.find("Sep").isEmpty());
        assertTrue(tasks.find("missing").isEmpty());
        assertEquals(tasks.asList(), tasks.find(""));
        assertThrows(UnsupportedOperationException.class, () -> tasks.find("book").clear());
    }
}
