package sevensix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Verifies task ordering, list ownership, replacement, and searching.
 */
class TaskListTest {
    /** A new list has no tasks or iterator elements. */
    @Test
    void emptyListHasNoTasks() {
        TaskList tasks = new TaskList();

        assertEquals(0, tasks.size());
        assertFalse(tasks.iterator().hasNext());
        assertFalse(tasks.hasDuplicate(new Todo("new")));
    }

    /** Adding and removing retain object identity and close gaps in list numbering. */
    @Test
    void addAndRemovePreserveTaskOrder() {
        TaskList tasks = new TaskList();
        Todo first = new Todo("first");
        Todo middle = new Todo("middle");
        Todo last = new Todo("last");
        tasks.add(first);
        tasks.add(middle);
        tasks.add(last);

        assertSame(middle, tasks.remove(1));
        assertEquals(2, tasks.size());
        assertSame(first, tasks.get(0));
        assertSame(last, tasks.get(1));
        assertSame(first, tasks.iterator().next());
        assertSame(first, tasks.remove(0));
        assertSame(last, tasks.remove(0));
        assertEquals(0, tasks.size());
    }

    /** Later edits to a constructor's source collection must not change the task list. */
    @Test
    void constructorCopiesTheSourceCollection() {
        List<Task> original = new ArrayList<>(List.of(new Todo("first")));
        TaskList tasks = new TaskList(original);
        original.clear();

        assertEquals(1, tasks.size());
        assertEquals("first", tasks.get(0).getDescription());
    }

    /** Replacement copies collection contents, including an empty snapshot. */
    @Test
    void replaceWithCopiesTheReplacementCollection() {
        TaskList tasks = new TaskList(List.of(new Todo("old")));
        List<Task> replacement = new ArrayList<>(List.of(new Todo("new")));
        tasks.replaceWith(replacement);
        replacement.clear();

        assertEquals(1, tasks.size());
        assertEquals("new", tasks.get(0).getDescription());
        tasks.replaceWith(List.of());
        assertEquals(0, tasks.size());
    }

    /** Search results keep their original membership when the task list later grows. */
    @Test
    void findResultsDoNotChangeAfterAddingTasks() {
        TaskList tasks = new TaskList(List.of(new Todo("读书")));
        List<Task> results = tasks.find("书");
        tasks.add(new Todo("借书"));

        assertEquals(1, results.size());
        assertEquals("读书", results.get(0).getDescription());
        assertEquals(2, tasks.find("书").size());
    }

    @Test
    void findKeywordReturnsCaseInsensitivePartialMatchesInOrder() {
        TaskList tasks = new TaskList(List.of(
                new Todo("read book"),
                new Todo("join sports club"),
                new Deadline("return BOOK", LocalDate.of(2019, 6, 6))));

        List<Task> matchingTasks = tasks.find("book");

        assertEquals(2, matchingTasks.size());
        assertEquals("read book", matchingTasks.get(0).getDescription());
        assertEquals("return BOOK", matchingTasks.get(1).getDescription());
        assertEquals(3, tasks.size());
    }

    @Test
    void findKeywordWithNoMatchesReturnsEmptyList() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        List<Task> matchingTasks = tasks.find("holiday");

        assertTrue(matchingTasks.isEmpty());
    }
}
