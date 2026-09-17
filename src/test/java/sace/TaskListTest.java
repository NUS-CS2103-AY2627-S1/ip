package sace;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests task-list operations that select tasks without changing the stored list.
 */
class TaskListTest {
    @Test
    void addInsertGetAndSize_maintainRequestedOrder() {
        Todo first = new Todo("first");
        Todo second = new Todo("second");
        TaskList tasks = new TaskList();

        tasks.add(second);
        tasks.insert(0, first);

        assertEquals(2, tasks.size());
        assertEquals(first, tasks.get(0));
        assertEquals(second, tasks.get(1));
    }

    @Test
    void markUnmarkAndDelete_updateSelectedTask() {
        Todo first = new Todo("first");
        Todo second = new Todo("second");
        TaskList tasks = new TaskList(List.of(first, second));

        assertEquals(second, tasks.mark(1));
        assertTrue(second.isDone());
        assertEquals(second, tasks.unmark(1));
        assertFalse(second.isDone());
        assertEquals(first, tasks.delete(0));
        assertEquals(List.of(second), tasks.asList());
    }

    @Test
    void asList_attemptedModification_throwsException() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        List<Task> readOnlyTasks = tasks.asList();

        assertThrows(UnsupportedOperationException.class, () ->
                readOnlyTasks.add(new Todo("write notes")));
    }

    @Test
    void mark_invalidIndex_violatesDocumentedAssumption() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertThrows(AssertionError.class, () -> tasks.mark(1));
    }

    @Test
    void otherOperations_invalidIndexes_violateDocumentedAssumptions() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertThrows(AssertionError.class, () -> tasks.get(-1));
        assertThrows(AssertionError.class, () -> tasks.unmark(1));
        assertThrows(AssertionError.class, () -> tasks.delete(1));
        assertThrows(AssertionError.class, () -> tasks.insert(2, new Todo("write notes")));
    }

    @Test
    void find_keywordWithDifferentCase_returnsMatchingTasksInOriginalOrder() {
        Task firstMatch = new Todo("Read Book");
        Task secondMatch = new Deadline("return book", LocalDate.of(2026, 9, 30));
        Task nonMatch = new Event("project meeting", "Monday", "Tuesday");
        TaskList tasks = new TaskList(List.of(firstMatch, secondMatch, nonMatch));

        List<Task> matchingTasks = tasks.find("BOOK");

        assertEquals(List.of(firstMatch, secondMatch), matchingTasks);
    }

    @Test
    void find_keywordOnlyOutsideDescription_returnsEmptyList() {
        Task event = new Event("project meeting", "bookstore", "library");
        TaskList tasks = new TaskList(List.of(event));

        List<Task> matchingTasks = tasks.find("book");

        assertTrue(matchingTasks.isEmpty());
    }
}
