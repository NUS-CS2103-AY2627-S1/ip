package bobby;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests task-list search behavior. */
public class TaskListTest {

    /** Verifies that construction copies the source list instead of retaining it. */
    @Test
    public void constructor_mutatedSource_taskListRemainsIndependent() {
        Task firstTask = new ToDo("first task");
        List<Task> sourceTasks = new java.util.ArrayList<>(List.of(firstTask));
        TaskList tasks = new TaskList(sourceTasks);

        sourceTasks.clear();

        assertEquals(1, tasks.size());
        assertSame(firstTask, tasks.get(0));
    }

    /** Verifies append, indexed insertion, retrieval, and removal preserve order. */
    @Test
    public void listOperations_validTasks_expectedOrderAndRemovedTaskReturned() {
        Task firstTask = new ToDo("first task");
        Task secondTask = new ToDo("second task");
        Task insertedTask = new ToDo("inserted task");
        TaskList tasks = new TaskList();

        tasks.add(firstTask);
        tasks.add(secondTask);
        tasks.add(1, insertedTask);

        assertEquals(3, tasks.size());
        assertSame(firstTask, tasks.get(0));
        assertSame(insertedTask, tasks.get(1));
        assertSame(secondTask, tasks.get(2));
        assertSame(insertedTask, tasks.remove(1));
        assertEquals(2, tasks.size());
    }

    /** Verifies invalid retrieval, insertion, and removal indices are rejected. */
    @Test
    public void listOperations_invalidIndices_indexOutOfBoundsExceptionThrown() {
        TaskList tasks = new TaskList(List.of(new ToDo("read book")));

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.get(1));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.add(2, new ToDo("write code")));
        assertThrows(IndexOutOfBoundsException.class, () -> tasks.remove(1));
    }

    /** Verifies null inputs violate the task-list contracts. */
    @Test
    public void taskListContracts_nullInputs_assertionErrorThrown() {
        TaskList tasks = new TaskList();

        assertThrows(AssertionError.class, () -> new TaskList(null));
        assertThrows(AssertionError.class, () -> new TaskList(java.util.Arrays.asList((Task) null)));
        assertThrows(AssertionError.class, () -> tasks.add(null));
        assertThrows(AssertionError.class, () -> tasks.add(0, null));
        assertThrows(AssertionError.class, () -> tasks.containsTaskWithSameDetails(null));
        assertThrows(AssertionError.class, () -> tasks.findByDescription(null));
    }

    /** Verifies that search is case-insensitive, substring-based, and ordered. */
    @Test
    public void findByDescription_keywordMatchesIgnoringCaseInOriginalOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));
        tasks.add(new Deadline("return BOOK", java.time.LocalDate.of(2026, 6, 6)));
        tasks.add(new ToDo("go jogging"));

        List<Task> matchingTasks = tasks.findByDescription("book");

        assertEquals(2, matchingTasks.size());
        assertEquals("read book", matchingTasks.get(0).getDescription());
        assertEquals("return BOOK", matchingTasks.get(1).getDescription());
    }

    /** Verifies that a keyword with no matches returns an empty result. */
    @Test
    public void findByDescription_noMatch_emptyListReturned() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("read book"));

        assertEquals(0, tasks.findByDescription("movie").size());
    }

    /** Verifies that modifying search results does not modify the task list. */
    @Test
    public void findByDescription_resultModified_taskListUnchanged() {
        TaskList tasks = new TaskList(List.of(new ToDo("read book")));
        List<Task> matchingTasks = tasks.findByDescription("");

        matchingTasks.clear();

        assertEquals(1, tasks.size());
        assertEquals(0, matchingTasks.size());
    }

    /** Verifies that duplicate checks compare type, normalized description, and date details. */
    @Test
    public void containsTaskWithSameDetails_variedCandidates_expectedResultReturned() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("Read Book"));
        tasks.add(new Deadline("return book", java.time.LocalDate.of(2026, 6, 6)));

        assertTrue(tasks.containsTaskWithSameDetails(new ToDo("  read   book ")));
        assertTrue(tasks.containsTaskWithSameDetails(
                new Deadline("RETURN BOOK", java.time.LocalDate.of(2026, 6, 6))));
        assertFalse(tasks.containsTaskWithSameDetails(
                new Deadline("return book", java.time.LocalDate.of(2026, 6, 7))));
        assertFalse(tasks.containsTaskWithSameDetails(new ToDo("return book")));
    }
}
